package de.offis.faint.detection.plugins.haar;

import java.awt.Rectangle;
import java.io.Serializable;

/**
 * Defines a single feature made up of rectangular weighted regions.
 *
 * @author <a href="mailto:matt.nathan@paphotos.com">Matt Nathan</a>
 */
public abstract class Feature implements Serializable {

    protected final FeatureRegion[] regions;





    public Feature(FeatureRegion... regions) {
        this.regions = regions;
    }





    /**
     * Apply the givern feature to the given image model. This will return the match value that the feature has against
     * the given image.
     *
     * @param model The image to apply to
     * @return How well this feature matches.
     */
    public abstract double applyTo(ImageModel model);





    protected abstract static class WeightedFeature extends Feature {

        private transient float cachedScale = 0;
        private transient CacheRegion[] cachedRegions;





        protected WeightedFeature(FeatureRegion... regions) {
            super(regions);
            cachedRegions = null;
        }





        @Override
        public double applyTo(ImageModel model) {
            if (cachedScale != model.getScale()) {
                if (cachedRegions == null) { // we do it here because of serialization not calling the constructor
                    cachedRegions = new CacheRegion[regions.length];
                    for (int i = 0; i < cachedRegions.length; i++) {
                        cachedRegions[i] = new CacheRegion();
                    }
                }
                // we cache the weight of the regions to improve performance
                // we only need to calculate this once window scale.
                cachedScale = model.getScale();

                double sum0 = 0;
                double area0 = 0;

                int base_w = Integer.MAX_VALUE;
                int base_h = Integer.MAX_VALUE;
                int new_base_w = 0;
                int new_base_h = 0;
                int kx;
                int ky;
                boolean flagx = false;
                boolean flagy = false;
                int x0 = 0;
                int y0 = 0;

                Rectangle firstArea = regions[0].getArea();
                for (FeatureRegion region : regions) {
                    Rectangle r = region.getArea();
                    if ((r.width - 1) >= 0) {
                        base_w = Math.min(base_w, (r.width - 1));
                    }
                    if ((r.x - firstArea.x - 1) >= 0) {
                        base_w = Math.min(base_w, (r.x - firstArea.x - 1));
                    }
                    if ((r.height - 1) >= 0) {
                        base_h = Math.min(base_h, (r.height - 1));
                    }
                    if ((r.y - firstArea.y - 1) >= 0) {
                        base_h = Math.min(base_h, (r.y - firstArea.y - 1));
                    }
                }

                base_w += 1;
                base_h += 1;
                kx = firstArea.width / base_w;
                ky = firstArea.height / base_h;

                if (kx <= 0) {
                    flagx = true;
                    new_base_w = Math.round(firstArea.width * cachedScale) / kx;
                    x0 = Math.round(firstArea.x * cachedScale);
                }

                if (ky <= 0) {
                    flagy = true;
                    new_base_h = Math.round(firstArea.height * cachedScale) / ky;
                    y0 = Math.round(firstArea.y * cachedScale);
                }

                for (int k = 0; k < regions.length; k++) {
                    Rectangle r = regions[k].getArea();
                    int x;
                    int y;
                    int width;
                    int height;
                    double correction_ratio;

                    if (flagx) {
                        x = (r.x - firstArea.x) * new_base_w / base_w + x0;
                        width = r.width * new_base_w / base_w;
                    } else {
                        x = Math.round(r.x * cachedScale);
                        width = Math.round(r.width * cachedScale);
                    }

                    if (flagy) {
                        y = (r.y - firstArea.y) * new_base_h / base_h + y0;
                        height = r.height * new_base_h / base_h;
                    } else {
                        y = Math.round(r.y * cachedScale);
                        height = Math.round(r.height * cachedScale);
                    }

                    correction_ratio = getCorrectionFactor(model);

                    cachedRegions[k].weight = (float) (regions[k].getWeight() / correction_ratio);
                    cachedRegions[k].area.setBounds(x, y, width, height);

                    if (k == 0) {
                        area0 = width * height;
                    } else {
                        sum0 += cachedRegions[k].weight * width * height;
                    }
                }

                cachedRegions[0].weight = (float) (-sum0 / area0);
            }
            double total = 0;
            for (CacheRegion cachedRegion : cachedRegions) {
                total += getRegionSum(model, cachedRegion.area) * cachedRegion.weight;
            }
            return total;
        }





        protected abstract int getRegionSum(ImageModel model, Rectangle region);





        protected abstract float getCorrectionFactor(ImageModel model);
    }

    /** A feature composed of regular rectangles. */
    public static class RegularFeature extends WeightedFeature {


        public RegularFeature(FeatureRegion... regions) {
            super(regions);
        }





        @Override
        protected int getRegionSum(ImageModel model, Rectangle region) {
            return model.getUnscaledSum(region);
        }





        @Override
        protected float getCorrectionFactor(ImageModel model) {
            return model.getWindowArea();
        }
    }

    /** A feature composed of tilted (at 45 degrees) rectangles. */
    public static class TiltedFeature extends WeightedFeature {

        public TiltedFeature(FeatureRegion... regions) {
            super(regions);
        }





        @Override
        protected int getRegionSum(ImageModel model, Rectangle region) {
            return model.getUnscaledTiltedSum(region);
        }





        @Override
        protected float getCorrectionFactor(ImageModel model) {
            return model.getWindowArea() << 1;
        }
    }


    private static class CacheRegion {

        private final Rectangle area = new Rectangle();
        private float weight;
    }
}
