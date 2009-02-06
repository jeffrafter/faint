package de.offis.faint.detection.plugins.haar;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.EnumSet;
import java.util.Set;

/**
 * Collection of statistics and functions for images that can be offset and scaled.
 *
 * @author <a href="mailto:matt.nathan@paphotos.com">Matt Nathan</a>
 */
public class ImageModel {

    private final BufferedImage image;
    private final int baseWidth;
    private final int baseHeight;
    private ImageStatistics stats;
    private int offsetX = 0;
    private int offsetY = 0;
    private float scale = 1f;
    private int windowWidth = -1;
    private int windowHeight = -1;





    public ImageModel(BufferedImage image, int baseWidth, int baseHeight, boolean hasTilted) {
        this.image = image;
        this.baseWidth = baseWidth;
        this.baseHeight = baseHeight;
        Set<ImageStatistics.Flags> flags = EnumSet.noneOf(ImageStatistics.Flags.class);
        if (hasTilted) {
            flags.add(ImageStatistics.Flags.TILTED_CASCADES);
        }
        stats = new ImageStatistics(image, flags);
    }





    int getUnscaledSum(int x, int y, int width, int height) {
        return stats.getSum(offsetX + x, offsetY + y, width, height);
    }





    int getUnscaledSum(Rectangle area) {
        return getUnscaledSum(area.x, area.y, area.width, area.height);
    }





    public int getSum(int x, int y, int width, int height) {
        return getUnscaledSum(Math.round(x * scale), Math.round(y * scale),
                              Math.round(width * scale), Math.round(height * scale));
    }





    public int getSum(Rectangle bounds) {
        return getSum(bounds.x, bounds.y, bounds.width, bounds.height);
    }





    public long getSquareSum(int x, int y, int width, int height) {
        return stats.getSquareSum((int) (offsetX + (x * scale)), (int) (offsetY + (y * scale)), (int) (width * scale), (int) (height * scale));
    }





    public int getTiltedSum(int x, int y, int width, int height) {
        return getUnscaledTiltedSum(Math.round(x * scale), Math.round(y * scale),
                                    Math.round(width * scale), Math.round(height * scale));
    }





    public int getTiltedSum(Rectangle bounds) {
        return getTiltedSum(bounds.x, bounds.y, bounds.width, bounds.height);
    }





    int getUnscaledTiltedSum(int x, int y, int width, int height) {
        return stats.getTiltedSum(offsetX + x, offsetY + y, width, height);
    }





    int getUnscaledTiltedSum(Rectangle area) {
        return getUnscaledTiltedSum(area.x, area.y, area.width, area.height);
    }





    public Rectangle getScaledRegion(int x, int y, int width, int height, Rectangle result) {
        if (result == null) {
            result = new Rectangle();
        }

        result.x = Math.round(offsetX + (x * scale));
        result.y = Math.round(offsetY + (y * scale));
        result.width = Math.round(width * scale);
        result.height = Math.round(height * scale);

        return result;
    }





    // cache variables
    private transient boolean cacheVarienceValid = false;
    private transient double cacheVarianceNormal = 0;
    private transient int cacheArea = 0;





    /**
     * Get the sum of the currently active window.
     *
     * @return the sum of the pixels in the current active window.
     */
    public int getWindowSum() {
        return stats.getSum(offsetX, offsetY, windowWidth, windowHeight);
    }





    /**
     * Get the squared sum of the currently active window.
     *
     * @return the sum of the syared pixel values of the current window.
     */
    public long getWindowSquareSum() {
        return stats.getSquareSum(offsetX, offsetY, windowWidth, windowHeight);
    }





    /**
     * Get a normalisation value for the pixels in the current window.
     *
     * @return The normalisation value.
     */
    public double getWindowVarianceNormal() {
        if (!cacheVarienceValid) {
            double area = getWindowArea();
            int sum = stats.getSum(Math.round(scale) + offsetX, Math.round(scale) + offsetY, windowWidth, windowHeight);
            double mean = sum / area;
            long squareSum = stats.getSquareSum(Math.round(scale) + offsetX, Math.round(scale) + offsetY, windowWidth, windowHeight);
            cacheVarianceNormal = squareSum / area - mean * mean;
            if (cacheVarianceNormal < 0) {
                cacheVarianceNormal = 1;
            } else {
                cacheVarianceNormal = Math.sqrt(cacheVarianceNormal);
            }
            cacheVarienceValid = true;
        }
        return cacheVarianceNormal;
    }





    public int getWindowArea() {
        if (cacheArea == -1) {
            cacheArea = windowWidth * windowHeight;
        }
        return cacheArea;
    }





    public void setWindow(int offsetX, int offsetY, float scale) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.scale = scale;
        // the opencv method -2 for some reason?
        this.windowWidth = Math.round((baseWidth - 2) * scale);
        this.windowHeight = Math.round((baseHeight - 2) * scale);

        // invalidate caches
        cacheArea = -1;
        cacheVarienceValid = false;
    }





    public void setWindowLocation(int offsetX, int offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;

        // invalidate cache
        cacheVarienceValid = false;
    }





    public float getScale() {
        return scale;
    }





    public int getOffsetX() {
        return offsetX;
    }





    public int getOffsetY() {
        return offsetY;
    }
}
