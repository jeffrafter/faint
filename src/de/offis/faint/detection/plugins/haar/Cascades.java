package de.offis.faint.detection.plugins.haar;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.*;
import de.offis.faint.detection.plugins.opencv.OpenCVDetection;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Generated JavaDoc Comment.
 *
 * @author <a href="mailto:matt.nathan@paphotos.com">Matt Nathan</a>
 */
public class Cascades {

    private static final float ICV_STAGE_THRESHOLD_BIAS = 0.0001f;

    private Cascades() {}





    public static ClassifierCascade readFromXML(InputStream in) throws IOException, XMLStreamException {
        // todo: check to see what the fastest way of parsing the xml is: DOM, SAX, StAX

        /*
         * Parses xml in the form:
         *
         * <opencv_storage>
         *  <haarcascade_{type} type_id="opencv-haar-classifier">
         *   <size>w h</size>
         *   <stages>
         *    <_>
         *     <trees>
         *      <_>
         *       <_>
         *        <feature>
         *         <rects>
         *          <_>x y w h weight</_>
         *          ...{2,3}
         *         </rects>
         *         <tilted>{0,1}</tilted>
         *        </feature>
         *        <threshold>double</threshold>
         *        <left_val>double</left_val>
         *        {<right_val>double</right_val> (if 1) | <right_node>int</right_node> (if 2)}
         *       </_>
         *       ...{1,2}
         *      </_>
         *      ...{+}
         *     </trees>
         *     <stage_threshold>double</stage_threshold>
         *     <parent>int</parent>
         *     <next>int</next>
         *    </_>
         *    ...{+}
         *   </stages>
         *  </haarcascade_profileface>
         * </opencv_storage>
         *
         */

        final XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        final XMLStreamReader reader = inputFactory.createXMLStreamReader(in);

        reader.nextTag(); // opencv_storage
        reader.nextTag(); // haarcascade_{type}
        if (!"opencv-haar-classifier".equals(reader.getAttributeValue(null, "type_id"))) {
            throw new IOException("Incorrect storage format: " + reader.getAttributeValue(null, "type_id"));
        }

        reader.nextTag(); // size
        String sizeString = reader.getElementText();
        String[] widthAndHeight = sizeString.split(" ");
        if (widthAndHeight.length != 2) {
            throw new IOException("expecting 'w h' for size element, got: " + sizeString);
        }

        int width = Integer.parseInt(widthAndHeight[0]);
        int height = Integer.parseInt(widthAndHeight[1]);
        boolean hasTiltedFeatures = false;

        DefaultClassifierStage root = null;

        reader.nextTag(); // <stages>

        List<StageInfo> stageInfos = new ArrayList<StageInfo>();
        // parse stage tags
        while (reader.nextTag() == XMLStreamReader.START_ELEMENT) { // <_>
            // parse trees
            reader.nextTag(); // <trees>

            List<Classifier> classifiers = new ArrayList<Classifier>();

            while (reader.nextTag() == XMLStreamReader.START_ELEMENT) { // <_>
                // i've only seen two in here
                final List<ClassifierInfo> classifierInfos = new ArrayList<ClassifierInfo>(2);
                while (reader.nextTag() == XMLStreamReader.START_ELEMENT) { // <_> child
                    FeatureRegion[] regions = new FeatureRegion[3];

                    reader.nextTag(); // <feature>
                    reader.nextTag(); // <rects>

                    int rectIndex = 0;
                    while (reader.nextTag() == XMLStreamReader.START_ELEMENT) { // <_>
                        String rectString = reader.getElementText();
                        String[] rectStringParts = rectString.split(" ");
                        int x = Integer.parseInt(rectStringParts[0]);
                        int y = Integer.parseInt(rectStringParts[1]);
                        int w = Integer.parseInt(rectStringParts[2]);
                        int h = Integer.parseInt(rectStringParts[3]);
                        float weight = Float.parseFloat(rectStringParts[4]);

                        regions[rectIndex++] = new FeatureRegion(x, y, w, h, weight);
                    }

                    reader.nextTag(); // <tilted>
                    String tiltedText = reader.getElementText();
                    reader.nextTag(); // </features>

                    Feature feature;
                    if ("1".equals(tiltedText)) {
                        hasTiltedFeatures = true;
                        // tilted feature
                        if (regions[2] == null) {
                            // 2 rects
                            feature = new Feature.TiltedFeature(regions[0], regions[1]);
                        } else {
                            // 3 rects
                            feature = new Feature.TiltedFeature(regions[0], regions[1], regions[2]);
                        }
                    } else {
                        // standard feature
                        if (regions[2] == null) {
                            // 2 rects
                            feature = new Feature.RegularFeature(regions[0], regions[1]);
                        } else {
                            // 3 rects
                            feature = new Feature.RegularFeature(regions[0], regions[1], regions[2]);
                        }
                    }


                    reader.nextTag(); // <threshold>
                    String thresholdText = reader.getElementText();
                    float threshold = (float) Double.parseDouble(thresholdText);

                    ClassifierInfo info = new ClassifierInfo();
                    info.threshold = threshold;
                    info.feature = feature;

                    reader.nextTag(); // <left_val> || <left_node>
                    String leftText = reader.getElementText();
                    if ("left_val".equals(reader.getLocalName())) {
                        info.leftVal = Double.parseDouble(leftText);
                    } else {
                        // find leftIndexed classifier
                        info.leftIndex = Integer.parseInt(leftText);
                    }
                    reader.nextTag(); // <right_val> || <right_node>
                    String rightText = reader.getElementText();
                    if ("right_val".equals(reader.getLocalName())) {
                        info.rightVal = Double.parseDouble(rightText);
                    } else {
                        // find right indexed classifier (put off the lookup until later)
                        info.rightIndex = Integer.parseInt(rightText);
                    }

                    reader.nextTag(); // </_>
                    classifierInfos.add(info);
                }

                // compress classifiers and add to result tree
                Classifier classifier = null;
                // from the opencv code I should be able to assume that the pointers always go to the next index
                // should be able to iterate backwards through the classifiers array to populate the structure
                for (ListIterator<ClassifierInfo> ittr = classifierInfos.listIterator(classifierInfos.size()); ittr.hasPrevious();) {
                    int index = ittr.previousIndex();
                    ClassifierInfo info = ittr.previous();
                    if (info.rightIndex < 0 && info.leftIndex < 0) { // leaf
                        classifier = new FeatureClassifier(info.feature,
                                                           new ValueClassifier(info.leftVal),
                                                           new ValueClassifier(info.rightVal),
                                                           info.threshold);
                    } else if (info.leftIndex < 0) {
                        assert info.rightIndex >= 0;
                        assert info.leftIndex == index + 1;
                        assert classifier != null;
                        classifier = new FeatureClassifier(info.feature,
                                                           new ValueClassifier(info.leftVal),
                                                           classifier,
                                                           info.threshold);
                    } else {
                        assert info.rightIndex < 0;
                        assert info.leftIndex >= 0;
                        assert info.rightIndex == index + 1;
                        assert classifier != null;
                        classifier = new FeatureClassifier(info.feature,
                                                           classifier,
                                                           new ValueClassifier(info.rightVal),
                                                           info.threshold);
                    }
                }
                if (classifier == null) {
                    throw new IOException("No classifiers found");
                }

                // add to the stages classifiers
                classifiers.add(classifier);
            }

            reader.nextTag(); // <stage_threshold>
            String stageThresholdText = reader.getElementText();
            reader.nextTag(); // <parent>
            String parentText = reader.getElementText();
            reader.nextTag(); // <next>
            String nextText = reader.getElementText();

            reader.nextTag(); // </_>

            StageInfo info = new StageInfo();
            info.stage = new DefaultClassifierStage((float) Double.parseDouble(stageThresholdText) - ICV_STAGE_THRESHOLD_BIAS,
                                                    classifiers.toArray(new Classifier[classifiers.size()]));
            info.parent = Integer.parseInt(parentText);
            info.next = Integer.parseInt(nextText);
            stageInfos.add(info);
        }
        reader.close();


        // process stages
        // success is child
        // failure is first parent next that is not null
        boolean isTree = false; // stumps have no branches, trees do i.e. next values
        for (StageInfo info : stageInfos) {
            if (info.parent != -1) {
                DefaultClassifierStage parent = stageInfos.get(info.parent).stage;
                if (parent.success == null) {
                    parent.success = info.stage;
                }
            }
            if (info.next != -1) {
                isTree = true;
                info.stage.failure = stageInfos.get(info.next).stage;
            }

            if (info.next == -1 && info.parent == -1) {
                assert root == null;
                root = info.stage;
            }
        }

        if (isTree) {
            // set the failure of all children of a branch root to the failure node (so we don't have to loop up later)
            Deque<DefaultClassifierStage> stack = new ArrayDeque<DefaultClassifierStage>();
            stack.push(root);
            DefaultClassifierStage failureStage = null;
            while (!stack.isEmpty()) {
                DefaultClassifierStage stage = stack.pop();
                if (stage.failure == null) {
                    // child of failure branch
                    stage.failure = failureStage;
                    if (stage.success != null) {
                        stack.push(stage.success);
                    }
                } else if (stage.failure != failureStage) {
                    // new failure branch
                    stack.push(stage);
                    failureStage = stage.failure;
                    if (stage.success != null) {
                        stack.push(stage.success);
                    }
                } else {
                    // old failure branch
                    assert stage.failure == failureStage;
                    stack.push(stage.failure);
                    failureStage = null;
                }
            }
        }


        ClassifierCascade result = new DefaultClassifierCascade(root, width, height, hasTiltedFeatures);
        return result;
    }





    public static void main(String[] args) throws IOException, XMLStreamException {
        InputStream in = null;
        try {
            in = OpenCVDetection.class.getResourceAsStream("haarcascade_frontalface_alt_tree.xml");
            ClassifierCascade cascade = readFromXML(in);
            System.out.println("cascade = " + cascade);
        } finally {
            in.close();
        }
    }





    public static ClassifierCascade readFromSerialization(InputStream in) throws IOException{
        ObjectInputStream o = new ObjectInputStream(in);
        try {
            return (ClassifierCascade) o.readObject();
        } catch (ClassNotFoundException e) {
            // todo: handle the class was not found
            e.printStackTrace();
            return null;
        }
    }





    private static final class ClassifierInfo {

        private Classifier result;
        private double leftVal;
        private double rightVal;
        private int leftIndex = -1;
        private int rightIndex = -1;
        private float threshold;
        private Feature feature;
    }


    private static final class StageInfo {

        private int parent = -1;
        private int next = -1;
        private DefaultClassifierStage stage;
    }
}
