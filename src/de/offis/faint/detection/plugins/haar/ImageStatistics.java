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
        // top + bottom - right - left
        final int top = cache[y][x];
        final int bottom = cache[y + width + height][x + width - height + 1];
        final int right = cache[y + width][x + width + 1];
        final int left = cache[y + height][x - height + 1];
        return top + bottom - right - left;
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

            sumCache = new int[height + 1][width + 1];
            sqSumCache = new long[height + 1][width + 1];
            rotSumCache = new int[height + 2][width + 2];

            // go through the image summing the data
            final int[] row = new int[width];
            final int[] lastRow = new int[width];

            // first two rows are special
            // y == 1
            if (height > 0) {
                image.getRGB(0, 0, width, 1, row, 0, width);
                int rowSum = 0;
                long sqRowSum = 0;

                for (int x = 1; x <= width; x++) {
                    final int gray = getGrayValue(row[x - 1]);

                    rowSum += gray;
                    sqRowSum += gray * gray;

                    sumCache[1][x] = rowSum;
                    lastRow[x - 1] = rotSumCache[1][x] = gray;
                    sqSumCache[1][x] = sqRowSum;
                }
            }

            // y == 2
            if (height > 1) {
                image.getRGB(0, 1, width, 1, row, 0, width);
                int rowSum = 0;
                long sqRowSum = 0;

                for (int x = 1; x < width; x++) {
                    final int gray = getGrayValue(row[x - 1]);

                    rowSum += gray;
                    sqRowSum += gray * gray;

                    sumCache[2][x] = sumCache[1][x] + rowSum;
                    sqSumCache[2][x] = sqSumCache[1][x] + sqRowSum;
                    rotSumCache[2][x] = rotSumCache[1][x - 1] + lastRow[x - 1] + rotSumCache[1][x + 1] + gray;
                    lastRow[x - 1] = gray;
                }

                // last column is special
                if (width > 0) {
                    final int gray = getGrayValue(row[width - 1]);

                    rowSum += gray;
                    sqRowSum += gray * gray;

                    sumCache[2][width] = sumCache[1][width] + rowSum;
                    sqSumCache[2][width] = sqSumCache[1][width] + sqRowSum;
                    rotSumCache[2][width] = rotSumCache[1][width - 1] + lastRow[width - 1] + gray;
                    lastRow[width - 1] = gray;
                }

            }

            for (int y = 3; y <= height; y++) {
                image.getRGB(0, y - 1, width, 1, row, 0, width);
                int rowSum = 0;
                long sqRowSum = 0;

                // first column is special
                if (width > 0) {
                    final int gray = getGrayValue(row[0]);
                    rowSum += gray;
                    sqRowSum += gray * gray;

                    sumCache[y][1] = sumCache[y - 1][1] + rowSum;
                    sqSumCache[y][1] = sqSumCache[y - 1][1] + sqRowSum;
                    rotSumCache[y][1] = rotSumCache[y - 1][2] + lastRow[0] + gray;
                    lastRow[0] = gray;
                }

                // from 2nd column to second to last column
                for (int x = 2; x < width; x++) {
                    final int gray = getGrayValue(row[x - 1]);
                    rowSum += gray;
                    sqRowSum += gray * gray;

                    sumCache[y][x] = sumCache[y - 1][x] + rowSum;
                    sqSumCache[y][x] = sqSumCache[y - 1][x] + sqRowSum;
                    rotSumCache[y][x] = rotSumCache[y - 1][x - 1] + lastRow[x - 1] + rotSumCache[y - 1][x + 1] -
                                        rotSumCache[y - 2][x] + gray;
                    lastRow[x - 1] = gray;
                }

                // last column is special
                if (width > 0) {
                    final int gray = getGrayValue(row[width - 1]);
                    rowSum += gray;
                    sqRowSum += gray * gray;

                    sumCache[y][width] = sumCache[y - 1][width] + rowSum;
                    sqSumCache[y][width] = sqSumCache[y - 1][width] + sqRowSum;
                    rotSumCache[y][width] = rotSumCache[y - 1][width - 1] + lastRow[width - 1] + gray;
                    lastRow[width - 1] = gray;
                }
            }
        }


    }
}
