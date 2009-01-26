package de.offis.faint.detection.plugins.haar;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Set;

/**
 * Algorithms for calculating statistics about the image. This may perform caching of the data to speed retrieval of
 * results.
 *
 * @author <a href="mailto:matt.nathan@paphotos.com">Matt Nathan</a>
 */
public class ImageStatistics {

    public static enum Flags {

        /** Denotes that information will probably be requested on tilted cascade sets */
        TILTED_CASCADES
    }

    private final BufferedImage image;
    private final SumCache cache;





    public ImageStatistics(BufferedImage image, Set<Flags> flags) {
        this.image = image;
        if (flags.contains(Flags.TILTED_CASCADES)) {
            cache = new RotSumCache(image);
        } else {
            cache = new SqSumCache(image);
        }
    }





    public BufferedImage getImage() {
        return image;
    }





    public int getSum(int x, int y, int width, int height) {
        int[][] cache = this.cache.getSumCache();
        return cache[y + height][x + width] - cache[y + height][x] - cache[y][x + width] + cache[y][x];
    }





    public int getSum(Rectangle area, Point offset) {
        return getSum(area.x + offset.x, area.y + offset.y, area.width, area.height);
    }





    public long getSquareSum(int x, int y, int width, int height) {
        long[][] cache = this.cache.getSqSumCache();
        return cache[y + height][x + width] - cache[y + height][x] - cache[y][x + width] + cache[y][x];
    }





    public long getSquareSum(Rectangle area, Point offset) {
        return getSquareSum(area.x + offset.x, area.y + offset.y, area.width, area.height);
    }




    public int getTiltedSum(int x, int y, int width, int height) {
        int[][] cache = this.cache.getRotSumCache();
        return cache[y + width][x + width] + cache[y + height][x + height] - cache[y][x] -
               cache[y + width + height][x + width - height];
    }





    private abstract static class SumCache {

        private boolean cacheValid = false;
        private final BufferedImage image;





        private SumCache(BufferedImage image) {
            this.image = image;
        }





        public BufferedImage getImage() {
            return image;
        }





        public int[][] getSumCache() {
            throw new UnsupportedOperationException("This cache does not support sums");
        }





        public long[][] getSqSumCache() {
            throw new UnsupportedOperationException("This cache does not support square sums");
        }





        public int[][] getRotSumCache() {
            throw new UnsupportedOperationException("This cache does not support rotated sums");
        }





        public boolean isCacheValid() {
            return cacheValid;
        }





        /** Needs to be called before each subclasses call to getXXXCache */
        protected void validateCache() {
            if (!isCacheValid()) {
                updateCache();
                cacheValid = true;
            }
        }





        protected int getGrayValue(final int rgb) {
            return (((rgb >> 16) & 0xFF) +
                    ((rgb >> 8) & 0xFF) +
                    (rgb & 0xFF)) / 3;
        }





        protected abstract void updateCache();
    }


    private static class SqSumCache extends SumCache {

        private int[][] sumCache;
        private long[][] sqSumCache;





        private SqSumCache(BufferedImage image) {
            super(image);
        }





        @Override
        public long[][] getSqSumCache() {
            validateCache();
            return sqSumCache;
        }





        @Override
        public int[][] getSumCache() {
            validateCache();
            return sumCache;
        }





        @Override
        protected void updateCache() {
            // the performance of this method was checked using GetIntegralImagesPerformance in the test source.
            final BufferedImage img = getImage();
            final int width = img.getWidth();
            final int height = img.getHeight();

            // +1 negates a lot of boundry checking later
            sumCache = new int[height + 1][width + 1];
            sqSumCache = new long[height + 1][width + 1];

            final int[] row = new int[width];

            for (int y = 1; y <= height; y++) {

                int rowSum = 0;
                long rowSumSQ = 0;
                img.getRGB(0, y - 1, width, 1, row, 0, width);

                for (int x = 1; x <= width; x++) {
                    final int rgb = row[x - 1];

                    final int grey = getGrayValue(rgb);

                    rowSum += grey;
                    rowSumSQ += grey * grey;

                    sumCache[y][x] = sumCache[y - 1][x] + rowSum;
                    sqSumCache[y][x] = sqSumCache[y - 1][x] + rowSumSQ;
                }
            }
        }
    }


    private static class RotSumCache extends SumCache {

        private int[][] sumCache;
        private long[][] sqSumCache;
        private int[][] rotSumCache;





        private RotSumCache(BufferedImage image) {
            super(image);
        }





        @Override
        public int[][] getRotSumCache() {
            validateCache();
            return rotSumCache;
        }





        @Override
        public long[][] getSqSumCache() {
            validateCache();
            return sqSumCache;
        }





        @Override
        public int[][] getSumCache() {
            validateCache();
            return sumCache;
        }





        @Override
        protected void updateCache() {
            // we ignore the cache type and calculate all the sums at the same time
            // the best way to get the pixel data seems to be using a row-at-a-time method
            final BufferedImage image = getImage();
            final int width = image.getWidth();
            final int height = image.getHeight();

            sumCache = new int[height][width];
            sqSumCache = new long[height][width];
            rotSumCache = new int[height][width];

            // go through the image summing the data
            final int[] row = new int[width];

            // first row is special
            if (height > 0) {
                image.getRGB(0, 0, width, 1, row, 0, width);
                int rowSum = 0;
                long sqRowSum = 0;

                for (int x = 1; x < width; x++) {
                    final int gray = getGrayValue(row[x]);

                    rowSum += gray;
                    sqRowSum += gray * gray;

                    sumCache[0][x] = rotSumCache[0][x] = rowSum;
                    sqSumCache[0][x] = sqRowSum;
                }
            }

            for (int y = 1; y < height; y++) {
                image.getRGB(0, y, width, 1, row, 0, width);
                int rowSum = 0;
                long sqRowSum = 0;

                // first two columns are special due to x - 1 and x - 2 in computations
                if (width > 0) {
                    // first column
                    final int gray = getGrayValue(row[0]);
                    rowSum += gray;
                    sqRowSum += gray * gray;

                    sumCache[y][0] = sumCache[y - 1][0] + rowSum;
                    sqSumCache[y][0] = sqSumCache[y - 1][0] + sqRowSum;
                    rotSumCache[y][0] = gray;
                }

                if (width > 1) {
                    // second column
                    final int gray = getGrayValue(row[1]);
                    rowSum += gray;
                    sqRowSum += gray * gray;
                    sumCache[y][1] = sumCache[y - 1][1] + rowSum;
                    sqSumCache[y][1] = sqSumCache[y - 1][1] + sqRowSum;
                    rotSumCache[y][1] = rotSumCache[y - 1][0] + rotSumCache[y][0] + gray;
                }

                // the rest of the image
                for (int x = 2; x < width; x++) {
                    final int gray = getGrayValue(row[x]);

                    rowSum += gray;
                    sqRowSum += gray * gray;

                    sumCache[y][x] = sumCache[y - 1][x] + rowSum;
                    sqSumCache[y][x] = sqSumCache[y - 1][x] + sqRowSum;

                    // in this pass only the top half of the sum is computed
                    rotSumCache[y][x] = rotSumCache[y - 1][x - 1] + rotSumCache[y][x - 1] -
                                        rotSumCache[y - 1][x - 2] + gray;
                }
            }

            // second pass for rotated rect to calculate the bottom half
            // bottom row doesn't need processing (there is no second half)
            for (int y = height - 2; y >= 0; y--) {
                for (int x = width - 1; x >= 2; x--) {
                    rotSumCache[y][x] += rotSumCache[y + 1][x - 1] - rotSumCache[y][x - 2];
                }

                // x == 1 is special (x == 0 doesn't need to be processed)
                if (width > 0) {
                    rotSumCache[y][1] += rotSumCache[y + 1][0];
                }
            }
        }


    }
}
