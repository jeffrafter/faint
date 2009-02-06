package de.offis.faint.detection.plugins.haar;

import java.io.Serializable;

/** A single stage in the matching of features against an image region */
public class DefaultClassifierStage implements Serializable {

    private float threshold;
    private Classifier[] classifiers;

    /** The next stage to compute on success, null if succeeded */
    DefaultClassifierStage success;
    /** the next stage to compute on failure, null if should fail alltogether */
    DefaultClassifierStage failure;





    public DefaultClassifierStage(float threshold, DefaultClassifierStage success, DefaultClassifierStage failure,
                                  Classifier... classifiers) {
        this.threshold = threshold;
        this.success = success;
        this.failure = failure;
        this.classifiers = classifiers;
    }





    DefaultClassifierStage(float threshold, Classifier... classifiers) {
        this.threshold = threshold;
        this.classifiers = classifiers;
    }





    public boolean matches(ImageModel model) {
        double total = 0;
        for (Classifier classifier : classifiers) {
            total += classifier.match(model);
        }
        return total >= threshold;
    }





    public DefaultClassifierStage onSuccess() {
        return success;
    }





    public DefaultClassifierStage onFailure() {
        return failure;
    }
}
