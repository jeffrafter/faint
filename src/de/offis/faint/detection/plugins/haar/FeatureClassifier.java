package de.offis.faint.detection.plugins.haar;

import java.io.Serializable;

/**
 * Defines a classifier for a feature against the source image. this classifier can form a tree or a stump, the nodes
 * may be composed of other default classifiers or simple value classifiers.
 */
public class FeatureClassifier implements Classifier, Serializable {

    private final Feature feature;
    private final float threshold;
    private final Classifier left;
    private final Classifier right;





    /**
     * Defines a classifier for the given feature. If thefeature matched against the given threshold then left is
     * invoked, elst right.
     *
     * @param feature   The feature to classify
     * @param left      The invoked classifier if the feature evaluates < threshold
     * @param right     The invoked classifier if the feature evaluates >= threshold
     * @param threshold The threshold
     */
    public FeatureClassifier(Feature feature, Classifier left, Classifier right, float threshold) {
        this.feature = feature;
        this.left = left;
        this.right = right;
        this.threshold = threshold;
    }





    public double match(ImageModel model) {
        double variance = model.getWindowVarianceNormal();
        return feature.applyTo(model) < threshold * variance ?
                left.match(model) :
                right.match(model);
    }
}
