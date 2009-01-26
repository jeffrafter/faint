package de.offis.faint.detection.plugins.haar;

import java.io.Serializable;

/**
 * Defines a group of feature sets that cascade depending on the type of cascade this is. Each feature set (a stage) is
 * applied to an image to determin if the features match the source, depending on this further stages may be processed.
 *
 * Generally this cascade will be applied multiple times in different positions and at different scales across the image
 * to find features that match at those scales and positions.
 *
 * @author <a href="mailto:matt.nathan@paphotos.com">Matt Nathan</a>
 */
public abstract class ClassifierCascade implements Serializable {

    /** The width of the area this cascade will match against */
    private int width;
    /** The height of the area this cascade will match against */
    private int height;





    protected ClassifierCascade(int width, int height) {
        this.width = width;
        this.height = height;
    }





    /**
     * Get the height of the cascade comparison region.
     *
     * @return The cascades height
     */
    public int getHeight() {
        return height;
    }





    /**
     * Get the width of the cascade comparison region
     *
     * @return The cascade width
     */
    public int getWidth() {
        return width;
    }





    /**
     * Check whether this cascade matches against the given image model.
     *
     * @param model The model to match against
     * @return > 0 if the cascade matches, <= 0 if it does not match. Optionally the cascade can return the closeness of
     *         the match as a negative integer, for example -2 might match closer than -1 (i.e. it may pass 2 stages
     *         instead of 1)
     */
    public abstract int matches(ImageModel model);





    /**
     * Get whether this cascade contains any tilted features.
     *
     * @return {@code true} if there are any tilted features in this cascade.
     */
    public abstract boolean hasTiltedFeatures();
}
