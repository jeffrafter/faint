package de.offis.faint.detection.plugins.haar;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Generated JavaDoc Comment.
 *
 * @author <a href="mailto:matt.nathan@paphotos.com">Matt Nathan</a>
 */
public interface ObjectDetector {

    List<Rectangle> detectObjects(BufferedImage image, int minSize);
}
