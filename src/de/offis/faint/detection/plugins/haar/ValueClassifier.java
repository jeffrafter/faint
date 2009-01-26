package de.offis.faint.detection.plugins.haar;

import java.io.Serializable;

/**
 * Simple classifier that always returns the same value.
 *
 * @author <a href="mailto:matt.nathan@paphotos.com">Matt Nathan</a>
 */
public class ValueClassifier implements Classifier, Serializable {

    /** The vlaue to return from this classifier */
    private final double value;





    /**
     * Create a new immutable value classifier.
     *
     * @param value The value for this classifier.
     */
    public ValueClassifier(double value) {
        this.value = value;
    }





    /**
     * Return the vlaue of this classifier. The model is ignored.
     *
     * @param model not used @return the value passed at construction.
     */
    public double match(ImageModel model) {
        return value;
    }
}
