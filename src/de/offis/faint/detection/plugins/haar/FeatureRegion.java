package de.offis.faint.detection.plugins.haar;

import java.awt.Rectangle;
import java.io.Serializable;

/**
 * A region that makes up a feature, this is generally a rectangle that is weighted.
 *
 * @author <a href="mailto:matt.nathan@paphotos.com">Matt Nathan</a>
 */
public class FeatureRegion implements Serializable {

    private final Rectangle area;
    private final float weight;





    public FeatureRegion(Rectangle area, float weight) {
        this.area = area;
        this.weight = weight;
    }





    public FeatureRegion(int x, int y, int width, int height, float weight) {
        this(new Rectangle(x, y, width, height), weight);
    }





    public Rectangle getArea() {
        return area;
    }





    public float getWeight() {
        return weight;
    }
}
