package de.offis.faint.detection.plugins.haar;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Scales the input image and results to improve performance.
 *
 * @author <a href="mailto:matt.nathan@paphotos.com">Matt Nathan</a>
 */
public class ScaledImageDetection implements ObjectDetector {

    private final ObjectDetector detector;
    private float scaleFactor = 0.25f;





    public ScaledImageDetection(ObjectDetector detector) {
        this.detector = detector;
    }





    public List<Rectangle> detectObjects(BufferedImage image, int minSize) {

        if (scaleFactor != 1) {
            BufferedImage tmp = new BufferedImage((int) (image.getWidth() * scaleFactor),
                                                  (int) (image.getHeight() * scaleFactor), image.getType());
            Graphics2D g = tmp.createGraphics();
            g.drawImage(image, 0, 0, tmp.getWidth(), tmp.getHeight(), null);
            g.dispose();
            image = tmp;

            minSize *= scaleFactor;
        }

        List<Rectangle> results = detector.detectObjects(image, minSize);
        
        if (scaleFactor != 1) {
            for (Rectangle result : results) {
                result.width /= scaleFactor;
                result.height /= scaleFactor;
                result.x /= scaleFactor;
                result.y /= scaleFactor;
            }
        }
        return results;
    }





    public float getScaleFactor() {
        return scaleFactor;
    }





    public void setScaleFactor(float scaleFactor) {
        this.scaleFactor = scaleFactor;
    }
}
