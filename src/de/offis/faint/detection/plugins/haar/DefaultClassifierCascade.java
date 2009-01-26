package de.offis.faint.detection.plugins.haar;

/**
 * Defines a classifier cascade implementation that can contain any path based cascade. Each stage int he cascade is
 * composed of a node which has both successful and unsuccessful paths leading to alternative stages. This can form both
 * simple flat cascades (like looping until failed) or tree based cascades that fall back onto different branches.
 *
 * @author <a href="mailto:matt.nathan@paphotos.com">Matt Nathan</a>
 */
public class DefaultClassifierCascade extends ClassifierCascade {

    private final DefaultClassifierStage root;
    private boolean tiltedFeatures;





    public DefaultClassifierCascade(DefaultClassifierStage root, int width, int height, boolean hasTiltedFeatures) {
        super(width, height); // throws null pointer exception (we want this to happen)
        if (width <= 0) {
            throw new IllegalArgumentException("width should be > 0 : " + width);
        }
        if (height <= 0) {
            throw new IllegalArgumentException("height should be > 0 : " + height);
        }
        if (root == null) {
            throw new IllegalArgumentException("root should not be null");
        }
        this.root = root;
        this.tiltedFeatures = hasTiltedFeatures;
    }





    @Override
    public int matches(ImageModel model) {
        // all stages need to match for this cascade to match
        int matches = 0; // the number of stages that pass
        DefaultClassifierStage cur = root;
        while (true) { // until success or failure
            if (cur.matches(model)) {
                matches++;
                cur = cur.onSuccess();
                if (cur == null) {
                    return matches;
                }
            } else {
                cur = cur.onFailure();
                if (cur == null) {
                    return -matches;
                }
            }
        }
    }





    @Override
    public boolean hasTiltedFeatures() {
        return tiltedFeatures;
    }


}
