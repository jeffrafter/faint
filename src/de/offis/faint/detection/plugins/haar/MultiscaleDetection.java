package de.offis.faint.detection.plugins.haar;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import de.offis.faint.detection.plugins.opencv.OpenCVDetection;
import javax.imageio.ImageIO;
import javax.xml.stream.XMLStreamException;

/**
 * Generated JavaDoc Comment.
 *
 * @author <a href="mailto:matt.nathan@paphotos.com">Matt Nathan</a>
 */
public class MultiscaleDetection implements ObjectDetector {

    private ClassifierCascade cascade;
    private float scaleFactor;

//    private PrintWriter log;





    public MultiscaleDetection(ClassifierCascade cascade) {
        this(cascade, 1.1f);
    }





    public MultiscaleDetection(ClassifierCascade cascade, float scaleFactor) {
        this.cascade = cascade;
        this.scaleFactor = scaleFactor;
    }





    public List<Rectangle> detectObjects(BufferedImage image, int minSize) {

        List<Rectangle> results = new ArrayList<Rectangle>();

        final int imageWidth = image.getWidth();
        final int imageHeight = image.getHeight();

        final ImageModel model = new ImageModel(image, cascade.getWidth(), cascade.getHeight(), cascade.hasTiltedFeatures());

        // calculate number of scale factors
        int nFactors = 0;
        int startFactor = 0;
        for (float factor = 1; factor * cascade.getWidth() < imageWidth - 10 &&
                               factor * cascade.getHeight() < imageHeight - 10; factor *= scaleFactor) {
            if (factor * cascade.getWidth() < minSize || factor * cascade.getHeight() < minSize) {
                startFactor++;
            }
            nFactors++;
        }

//        nFactors = (int) Math.min(
//                Math.log((imageWidth - 10) / (double) cascade.getWidth()) / Math.log(scaleFactor),
//                Math.log((imageHeight - 10) / (double) cascade.getHeight()) / Math.log(scaleFactor)
//        ); // seems to be one less than the above method?!
//
//        startFactor = (int) Math.ceil(Math.max(
//                Math.log(minSize / (double) cascade.getWidth()) / Math.log(scaleFactor),
//                Math.log(minSize / (double) cascade.getHeight()) / Math.log(scaleFactor)
//        ));

        float factor = (float) Math.pow(scaleFactor, startFactor);
        for (int scaleStep = startFactor; scaleStep < nFactors; factor *= scaleFactor, scaleStep++) {
//            log.println("factor loop: " + factor + " nfactors=" + (nFactors - scaleStep));
            double ystep = Math.max(2, factor);
            model.setWindow(0, 0, factor);

            int windowWidth = (int) (factor * cascade.getWidth());
            int windowHeight = (int) (factor * cascade.getHeight());
//            log.println("winSize = " + windowWidth + ", " + windowHeight);

            int start_x = 0;
            int start_y = 0;
            int end_x = (int) Math.round(((double) (imageWidth - windowWidth)) / ystep);
            int end_y = (int) Math.round(((double) (imageHeight - windowHeight)) / ystep);

            for (int _iy = start_y; _iy < end_y; _iy++) {
                int iy = (int) Math.round(_iy * ystep);
                int _ix;
                int _xstep;

                for (_ix = start_x; _ix < end_x; _ix += _xstep) {
                    int ix = (int) Math.round(_ix * ystep);
//                    log.println("Checking coors: " + ix + ", " + iy);
                    _xstep = 2;

                    model.setWindowLocation(ix, iy);
                    int result = cascade.matches(model);


                    if (result > 0) {
//                        log.println("Success for coords: " + ix + ", " + iy);
                        Rectangle rect = new Rectangle(ix, iy, windowWidth, windowHeight);
                        results.add(rect);
                    }
                    if (result < 0 && result > -2) { // means that the cascade got past the first and second stage of testing
                        _xstep = 1;
                    }
                }
            }
        }

        return results;
    }





    public static void main(String[] args) throws XMLStreamException, IOException {
        InputStream in = OpenCVDetection.class.getResourceAsStream("haarcascade_frontalface_default.xml");
        ClassifierCascade cascade = Cascades.readFromXML(in);
        in.close();

        MultiscaleDetection detector = new MultiscaleDetection(cascade);
        try {
            System.out.println("Loading image.");
            BufferedImage image = ImageIO.read(new File("/home/matt/projects/face-recognition/sample-images/groups/EMP-6713186.jpg"));
            //scale image to smaller size (inside 1000x1000);
            int width = image.getWidth();
            int height = image.getHeight();

            BufferedImage tmp;
            if (width > 1000 || height > 1000) {
                System.out.print("Scaling image: [" + width + 'x' + height + "] -> ");
                float ratio = width / (float) height;
                if (width > height) {
                    width = 1000;
                    height = (int) (width / ratio);
                } else {
                    height = 1000;
                    width = (int) (height * ratio);
                }
                System.out.println("[" + width + 'x' + height + ']');
                tmp = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                Graphics g = tmp.getGraphics();
                g.drawImage(image, 0, 0, width, height, null);
                g.dispose();

                image = tmp;
            }

            System.out.print("Running face detection... ");

            List<Rectangle> cvRectArrayList = Collections.emptyList();

            final long targetTime = 10L * 1000L * 1000L * 1000L; // 10 seconds
            int count = 0;
            final int minFaceSize = (int) (Math.min(image.getWidth(), image.getHeight()) * 0.25f);

            GroupingPolicy grouping = new GroupingPolicy();

            final long t0 = System.nanoTime();
            while ((System.nanoTime() - t0) < targetTime) {
//                detector.log = new PrintWriter(new FileOutputStream(new File("haarDetectObjects4.log")));
                cvRectArrayList = detector.detectObjects(image, minFaceSize);
                cvRectArrayList = grouping.reduceAreas(cvRectArrayList);
//                detector.log.close();
//                detector.log = null;
                count++;
            }
            final long t1 = System.nanoTime();

            System.out.println("Ran " + count + " times (" + ((t1 - t0) / (1000 * 1000 * 1000D)) + " seconds total)");

            System.out.println(cvRectArrayList.size() + " object(s) found.");
            for (Rectangle rectangle : cvRectArrayList) {
                System.out.println(" " + rectangle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
