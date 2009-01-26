package de.offis.faint.detection.plugins.haar;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Collections;
import de.offis.faint.detection.plugins.haar.ImageStatistics;

/**
 * Generated JavaDoc Comment.
 *
 * @author <a href="mailto:matt.nathan@paphotos.com">Matt Nathan</a>
 */
public class ImageStatisticsTest {

    public static void main(String[] args) {
        final int width = 6;
        final int height = 6;
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.setColor(new Color(1, 1, 1));
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.dispose();

        ImageStatistics sqStats = new ImageStatistics(image, Collections.<ImageStatistics.Flags>emptySet());

        int totalSum = sqStats.getSum(0, 0, width, height);
        System.out.println("totalSum = " + totalSum);
    }
}
