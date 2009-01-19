package de.offis.faint.detection.plugins.haar;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.nio.IntBuffer;
import java.nio.LongBuffer;

/**
 * Class to check the performance of the getIntegralImages function
 *
 * @author <a href="mailto:matt.nathan@paphotos.com">Matt Nathan</a>
 */
public class GetIntegralImagesPerformance {

    /** Test that functions the same as the original image integral function */
    private static class Original extends NestedArrayTest {

        @Override
        public void test(BufferedImage img, int[][] sumIntegralImage, long[][] sumSQIntegralImage) {
            for (int y = 1; y <= img.getHeight(); y++) {

                int rowSum = 0;
                long rowSumSQ = 0;

                for (int x = 1; x <= img.getWidth(); x++) {
                    Color c = new Color(img.getRGB(x - 1, y - 1));
                    int grey = (int) Math.round((c.getBlue() + c.getGreen() + c.getRed()) / 3.0);

                    rowSum += grey;
                    rowSumSQ += grey * grey;

                    sumIntegralImage[y][x] = sumIntegralImage[y - 1][x] + rowSum;
                    sumSQIntegralImage[y][x] = sumSQIntegralImage[y - 1][x] + rowSumSQ;
                }
            }
        }
    }

    /** Same as the original but uses a flat (1D) array instead of a 2D array for the result */
    private static class FlatOriginal extends FlatArrayTest {

        @Override
        public void test(BufferedImage img, int[] sumIntegralImage, long[] sumSQIntegralImage) {
            for (int y = 1; y <= img.getHeight(); y++) {

                int rowSum = 0;
                long rowSumSQ = 0;

                for (int x = 1; x <= img.getWidth(); x++) {
                    Color c = new Color(img.getRGB(x - 1, y - 1));
                    int grey = (int) Math.round((c.getBlue() + c.getGreen() + c.getRed()) / 3.0);

                    rowSum += grey;
                    rowSumSQ += grey * grey;

                    sumIntegralImage[x + (y * img.getWidth())] = sumIntegralImage[x + ((y - 1) * img.getWidth())] + rowSum;
                    sumSQIntegralImage[x + (y * img.getWidth())] = sumSQIntegralImage[x + ((y - 1) * img.getWidth())] + rowSumSQ;
                }
            }
        }
    }

    /** Gets the image pixels all at once and places them into an uncached int array */
    private static class ImageAtATimeNestedArrayTest extends AbstractImageAtATimeNestedArrayTest {

        @Override
        protected int[] getImageBuffer(int width, int height) {
            return new int[width * height];
        }


    }

    /** Gets the image pixels all at once and places them into a caches int array */
    private static class CachedImageAtATimeNestedArrayTest extends AbstractImageAtATimeNestedArrayTest {

        private int[] imgBufferCache;





        @Override
        protected int[] getImageBuffer(int width, int height) {
            if (imgBufferCache == null || imgBufferCache.length != width * height) {
                imgBufferCache = new int[width * height];
            }
            return imgBufferCache;
        }


    }

    /** Same as ImageAtATimeNestedArrayTest but uses a 1D array for the results */
    private static class ImageAtATimeFlatArrayTest extends AbstractImageAtATimeFlatArrayTest {


        @Override
        protected int[] getImageBuffer(int width, int height) {
            return new int[width * height];
        }


    }

    /** Same as CachedImageAtATimeNestedArrayTest but uses a 1D array instead of a 2D array for the results */
    private static class CachedImageAtATimeFlatArrayTest extends AbstractImageAtATimeFlatArrayTest {

        private int[] imgBufferCache;





        @Override
        protected int[] getImageBuffer(int width, int height) {
            if (imgBufferCache == null || imgBufferCache.length != width * height) {
                imgBufferCache = new int[width * height];
            }
            return imgBufferCache;
        }


    }

    /** Grabs image pixels one row at a time and places them into a non-cached int array */
    private static class RowAtATimeNestedArrayTest extends AbstractRowAtATimeNestedArrayTest {

        @Override
        protected int[] getRowCache(int width) {
            return new int[width];
        }

    }

    /** Cached version of RowAtATimeNestedArrayTest */
    private static class CachedRowAtATimeNestedArrayTest extends AbstractRowAtATimeNestedArrayTest {

        private int[] rowCache;





        @Override
        protected int[] getRowCache(int width) {
            if (rowCache == null || rowCache.length != width) {
                rowCache = new int[width];
            }
            return rowCache;
        }

    }

    /** Same as RowAtATimeNestedArrayTest but using a 1D array for the result */
    private static class RowAtATimeFlatArrayTest extends AbstractRowAtATimeFlatArrayTest {

        @Override
        protected int[] getRowCache(int width) {
            return new int[width];
        }

    }

    /** Same as CachedRowAtATimeNestedArrayTest but uses a 1D array for the result */
    private static class CachedRowAtATimeFlatArrayTest extends AbstractRowAtATimeFlatArrayTest {

        private int[] rowCache;





        @Override
        protected int[] getRowCache(int width) {
            if (rowCache == null || rowCache.length != width) {
                rowCache = new int[width];
            }
            return rowCache;
        }

    }

    /** Same as RowAtATimeNestedArrayTest but uses n java.nio.IntBuffer and LongBuffer as the result */
    private static class RowAtATimeBufferTest extends AbstractRowAtATimeBufferTest {

        @Override
        protected int[] getRowCache(int width) {
            return new int[width];
        }

    }

    /** Same as CachedRowAtATimeNestedArrayTest but n java.nio.IntBuffer and LongBuffer as the result */
    private static class CachedRowAtATimeBufferTest extends AbstractRowAtATimeBufferTest {

        private int[] rowCache;





        @Override
        protected int[] getRowCache(int width) {
            if (rowCache == null || rowCache.length != width) {
                rowCache = new int[width];
            }
            return rowCache;
        }

    }





    /**
     * Test different methods of getting the integral image and square integral image from a source.
     *
     * @param args Ignored
     */
    public static void main(String[] args) {
        // todo: add different types of image to the test
        // todo: compare different sizes of image

        // The tests that we are going to run
        Test[] tests = {
                new Original(),
                new ImageAtATimeNestedArrayTest(),
                new CachedImageAtATimeNestedArrayTest(),
                new RowAtATimeNestedArrayTest(),
                new CachedRowAtATimeNestedArrayTest(),
                new FlatOriginal(),
                new ImageAtATimeFlatArrayTest(),
                new CachedImageAtATimeFlatArrayTest(),
                new RowAtATimeFlatArrayTest(),
                new CachedRowAtATimeFlatArrayTest(),
                new RowAtATimeBufferTest(),
                new CachedRowAtATimeBufferTest()
        };

        // Size of the image
        final int width = 200;
        final int height = 200;

        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.dispose();

        System.out.println("Testing against image [" + width + 'x' + height + ']');

        // run for 10 times each to get them warmed up
        System.out.println("Warming up...");
        for (Test test : tests) {
            test.test(image, 10);
        }

        final int runIterations = 1000;
        Test quickestTest = null;
        double quickestTestTime = Double.MAX_VALUE;
        for (Test test : tests) {
            System.out.println("Testing: " + test.getClass().getSimpleName());
            double time = test.test(image, runIterations) / (1000d + 1000d);
            time /= (double) runIterations; // for iterations
            if (time < quickestTestTime) {
                quickestTestTime = time;
                quickestTest = test;
            }
            System.out.println(" ran " + runIterations + " iterations at " + time + "ms average");
        }

        System.out.println("--------------------------------------------------");
        System.out.println("Quickest test: " + quickestTest.getClass().getSimpleName());
        System.out.println("Ran in " + quickestTestTime + "ms (average)");


    }





    private abstract static class Test {

        private long t0 = 0;





        public abstract long test(BufferedImage image, int iterations);





        protected void startTimer() {
            t0 = System.nanoTime();
        }





        protected long getTime() {
            return System.nanoTime() - t0;
        }
    }


    private abstract static class NestedArrayTest extends Test {

        @Override
        public long test(BufferedImage image, int iterations) {
            final int[][] sumIntegralImage = new int[image.getHeight() + 1][image.getWidth() + 1];
            final long[][] sumSQIntegralImage = new long[image.getHeight() + 1][image.getWidth() + 1];

            startTimer();
            for (int i = 0; i < iterations; i++) {
                test(image, sumIntegralImage, sumSQIntegralImage);
            }

            return getTime();
        }





        protected abstract void test(BufferedImage img, int[][] sumIntegralImage, long[][] sumSQIntegralImage);
    }


    private abstract static class FlatArrayTest extends Test {

        @Override
        public long test(BufferedImage image, int iterations) {
            final int[] sumIntegralImage = new int[(image.getWidth() + 1) * (image.getHeight() + 1)];
            final long[] sumSQIntegralImage = new long[sumIntegralImage.length];

            startTimer();
            for (int i = 0; i < iterations; i++) {
                test(image, sumIntegralImage, sumSQIntegralImage);
            }

            return getTime();
        }





        protected abstract void test(BufferedImage image, int[] sumIntegralImage, long[] sumSQIntegralImage);
    }


    private abstract static class BufferTest extends Test {

        @Override
        public long test(BufferedImage image, int iterations) {
            final IntBuffer sumIntegralImage = IntBuffer.allocate((image.getWidth() + 1) * (image.getHeight() + 1));
            final LongBuffer sumSQIntegralImage = LongBuffer.allocate(sumIntegralImage.capacity());

            sumIntegralImage.mark();
            sumSQIntegralImage.mark();

            startTimer();
            for (int i = 0; i < iterations; i++) {
                sumIntegralImage.reset();
                sumSQIntegralImage.reset();
                test(image, sumIntegralImage, sumSQIntegralImage);
            }

            return getTime();
        }





        protected abstract void test(BufferedImage image, IntBuffer sumIntegralImage, LongBuffer sumSQIntegralImage);
    }


    private abstract static class AbstractImageAtATimeNestedArrayTest extends NestedArrayTest {


        @Override
        public void test(BufferedImage img, int[][] sumIntegralImage, long[][] sumSQIntegralImage) {
            final int width = img.getWidth();
            final int height = img.getHeight();

            int[] imgData = getImageBuffer(width, height);
            img.getRGB(0, 0, width, height, imgData, 0, width);
            for (int y = 1; y <= height; y++) {

                int rowSum = 0;
                long rowSumSQ = 0;

                for (int x = 1; x <= width; x++) {
                    int rgb = imgData[x - 1 + (y - 1) * width];

                    int grey = (((rgb >> 16) & 0xFF) +
                                ((rgb >> 8) & 0xFF) +
                                (rgb & 0xFF)) / 3;

                    rowSum += grey;
                    rowSumSQ += grey * grey;

                    sumIntegralImage[y][x] = sumIntegralImage[y - 1][x] + rowSum;
                    sumSQIntegralImage[y][x] = sumSQIntegralImage[y - 1][x] + rowSumSQ;
                }
            }
        }





        protected abstract int[] getImageBuffer(int width, int height);
    }

    private abstract static class AbstractImageAtATimeFlatArrayTest extends FlatArrayTest {

        @Override
        public void test(BufferedImage img, int[] sumIntegralImage, long[] sumSQIntegralImage) {
            final int width = img.getWidth();
            final int height = img.getHeight();
            int[] imgData = getImageBuffer(width, height);
            img.getRGB(0, 0, width, height, imgData, 0, width);
            for (int y = 1; y <= height; y++) {

                int rowSum = 0;
                long rowSumSQ = 0;

                for (int x = 1; x <= width; x++) {
                    int rgb = imgData[x - 1 + (y - 1) * width];

                    int grey = (((rgb >> 16) & 0xFF) +
                                ((rgb >> 8) & 0xFF) +
                                (rgb & 0xFF)) / 3;

                    rowSum += grey;
                    rowSumSQ += grey * grey;

                    sumIntegralImage[x + (y * img.getWidth())] = sumIntegralImage[x + ((y - 1) * img.getWidth())] + rowSum;
                    sumSQIntegralImage[x + (y * img.getWidth())] = sumSQIntegralImage[x + ((y - 1) * img.getWidth())] + rowSumSQ;
                }
            }
        }





        protected abstract int[] getImageBuffer(int width, int height);


    }


    private abstract static class AbstractRowAtATimeNestedArrayTest extends NestedArrayTest {

        @Override
        public void test(BufferedImage img, int[][] sumIntegralImage, long[][] sumSQIntegralImage) {
            final int width = img.getWidth();
            final int height = img.getHeight();
            int[] imgData = getRowCache(width);
            for (int y = 1; y <= height; y++) {

                int rowSum = 0;
                long rowSumSQ = 0;
                img.getRGB(0, y - 1, width, 1, imgData, 0, width);

                for (int x = 1; x <= width; x++) {
                    int rgb = imgData[x - 1];

                    int grey = (((rgb >> 16) & 0xFF) +
                                ((rgb >> 8) & 0xFF) +
                                (rgb & 0xFF)) / 3;

                    rowSum += grey;
                    rowSumSQ += grey * grey;

                    sumIntegralImage[y][x] = sumIntegralImage[y - 1][x] + rowSum;
                    sumSQIntegralImage[y][x] = sumSQIntegralImage[y - 1][x] + rowSumSQ;
                }
            }
        }





        protected abstract int[] getRowCache(int width);
    }


    private abstract static class AbstractRowAtATimeFlatArrayTest extends FlatArrayTest {

        @Override
        public void test(BufferedImage img, int[] sumIntegralImage, long[] sumSQIntegralImage) {
            final int width = img.getWidth();
            final int height = img.getHeight();
            int[] imgData = getRowCache(width);
            for (int y = 1; y <= height; y++) {

                int rowSum = 0;
                long rowSumSQ = 0;
                img.getRGB(0, y - 1, width, 1, imgData, 0, width);

                for (int x = 1; x <= width; x++) {
                    int rgb = imgData[x - 1];

                    int grey = (((rgb >> 16) & 0xFF) +
                                ((rgb >> 8) & 0xFF) +
                                (rgb & 0xFF)) / 3;

                    rowSum += grey;
                    rowSumSQ += grey * grey;

                    sumIntegralImage[x + (y * img.getWidth())] = sumIntegralImage[x + ((y - 1) * img.getWidth())] + rowSum;
                    sumSQIntegralImage[x + (y * img.getWidth())] = sumSQIntegralImage[x + ((y - 1) * img.getWidth())] + rowSumSQ;
                }
            }
        }





        protected abstract int[] getRowCache(int width);
    }


    private abstract static class AbstractRowAtATimeBufferTest extends BufferTest {

        @Override
        public void test(BufferedImage img, IntBuffer sumIntegralImage, LongBuffer sumSQIntegralImage) {
            final int width = img.getWidth();
            final int height = img.getHeight();
            int[] imgData = getRowCache(width);
            for (int y = 1; y <= height; y++) {

                int rowSum = 0;
                long rowSumSQ = 0;
                img.getRGB(0, y - 1, width, 1, imgData, 0, width);

                for (int x = 1; x <= width; x++) {
                    int rgb = imgData[x - 1];

                    int grey = (((rgb >> 16) & 0xFF) +
                                ((rgb >> 8) & 0xFF) +
                                (rgb & 0xFF)) / 3;

                    rowSum += grey;
                    rowSumSQ += grey * grey;

                    sumIntegralImage.put(sumIntegralImage.get(x + ((y - 1) * img.getWidth())) + rowSum);
                    sumSQIntegralImage.put(sumSQIntegralImage.get(x + ((y - 1) * img.getWidth())) + rowSumSQ);
                }
            }
        }





        protected abstract int[] getRowCache(int width);
    }
}
