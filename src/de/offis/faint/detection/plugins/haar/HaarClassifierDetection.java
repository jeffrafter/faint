package de.offis.faint.detection.plugins.haar;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import de.offis.faint.controller.MainController;
import de.offis.faint.interfaces.IDetectionPlugin;
import de.offis.faint.interfaces.ISwingCustomizable;
import de.offis.faint.model.Region;
import javax.swing.JPanel;
import javax.xml.stream.XMLStreamException;

public class HaarClassifierDetection implements IDetectionPlugin, ISwingCustomizable {

    private static final long serialVersionUID = -6413328362450632598L;

    private float scaleFactor = 1.1f;
    private ClassifierCascade cascade;
    private String cascadeFile;

    static final String[] BUILT_IN_CASCADES = {
            "haarcascade_frontalface_default.bin",
            "haarcascade_frontalface_alt.bin",
            "haarcascade_frontalface_alt_tree.bin",
            "haarcascade_frontalface_alt2.bin",
//		"haarcascade_profileface.bin"
    };

    private GroupingPolicy groupingPolicy;
    private transient HaarClassifierSettingsPanel settingsPanel = null;





    public HaarClassifierDetection() {
        setCascade(BUILT_IN_CASCADES[0]);
        groupingPolicy = new GroupingPolicy();
    }





    public String getName() {
        return "HaarClassfier Detection (pure Java)";
    }





    public String getCopyrightNotes() {
        return "bla bla";
    }





    public String getDescription() {
        return "This plugin is still in development. Watch out for the final version!";
    }





    /* (non-Javadoc)
      * @see de.offis.faint.interfaces.ISwingCustomizable#getSettingsPanel()
      */
    public JPanel getSettingsPanel() {
        if (settingsPanel == null) {
            settingsPanel = new HaarClassifierSettingsPanel(this);
        }
        return settingsPanel;
    }





    public GroupingPolicy getGroupingPolicy() {
        return groupingPolicy;
    }





    public void setGroupingPolicy(GroupingPolicy groupingPolicy) {
        this.groupingPolicy = groupingPolicy;
    }





    public Region[] detectFaces(String file, int minScanWindowSize) {

        BufferedImage image = MainController.getInstance().getBufferedImageCache().getImage(new File(file));

        List<Rectangle> result = detectObjects(image, minScanWindowSize);

        Region[] regions = new Region[result.size()];

        for (int i = 0; i < result.size(); i++) {
            Rectangle r = result.get(i);
            regions[i] = new Region((int) Math.round(r.getCenterX()), (int) Math.round(r.getCenterY()), r.width, r.height, 0, file);
        }

        return regions;
    }





    List<Rectangle> detectObjects(BufferedImage image, int minScanWindowSize) {
        MultiscaleDetection detector = new MultiscaleDetection(cascade, scaleFactor);

        List<Rectangle> result = detector.detectObjects(image, minScanWindowSize);
        return groupingPolicy.reduceAreas(result);
    }





    public String toString() {
        return getName();
    }





    public String getCurrentCascadeFile() {
        return cascadeFile;
    }





    public double getScale() {
        return scaleFactor;
    }





    public void setCascade(String selection) {
        // if this given parameter is not the same as the current cascade file
        if (this.cascadeFile == null ? selection != null : !this.cascadeFile.equals(selection)) {
            ClassifierCascade newCascade = null;

            // try to load serialized cascade from jar file
            for (String s : BUILT_IN_CASCADES) {
                if (s.equals(selection)) {
                    InputStream in = null;
                    try {
                        in = getClass().getResourceAsStream(selection);
                        newCascade = Cascades.readFromSerialization(in);
                    } catch (IOException e) {
                        // todo: handle the stream could not be read
                        e.printStackTrace();
                    } finally {
                        if (in != null) {
                            try {
                                in.close();
                            } catch (IOException e) {
                                // todo: handle stream could not be closed
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
                }
            }

            // try to load serialized cascade from external XML file
            if (newCascade == null) {
                InputStream in = null;
                try {
                    File xmlFile = new File(MainController.getInstance().getDataDir().getAbsoluteFile() + File.separator + HaarClassifierSettingsPanel.SUBFOLDER + File.separator + selection);
                    in = new FileInputStream(xmlFile);
                    newCascade = Cascades.readFromXML(in);
                } catch (FileNotFoundException e) {
                    // todo: handle the file does not exist
                    e.printStackTrace();
                } catch (XMLStreamException e) {
                    // todo: handle the xml file is invalid
                    e.printStackTrace();
                } catch (IOException e) {
                    // todo: handle cannot read the file
                    e.printStackTrace();
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            // todo: handle the stream could not be closed
                            e.printStackTrace();
                        }
                    }
                }
            }

            if (newCascade != null) {
                cascadeFile = selection;
                cascade = newCascade;
            }
        }
    }





    public void setScale(float scaleFactor) {
        this.scaleFactor = scaleFactor;
    }
}
