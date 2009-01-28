package de.offis.faint.detection.plugins.haar;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import de.offis.faint.detection.plugins.opencv.OpenCVDetection;
import javax.imageio.ImageIO;
import javax.xml.stream.XMLStreamException;

/**
 * A test to determin the desired scale factor of images vs the recognition factor that we need to find faces.
 *
 * @author <a href="mailto:matt.nathan@paphotos.com">Matt Nathan</a>
 */
public class TestImageScaleVsPerformance {

    public static void main(String[] args) {
        final String imagePath = "/home/matt/projects/face-recognition/sample-images/groups/";
        final ImageInfo[] images = {
                new ImageInfo(imagePath + "EMP-6799219.jpg", 7),
                new ImageInfo(imagePath + "EMP-6797939.jpg", 3),
                new ImageInfo(imagePath + "EMP-6382066.jpg", 2),
                new ImageInfo(imagePath + "EMP-6804147.jpg", 4),
        };


        MultiscaleDetection detector = new MultiscaleDetection(getCascade());

        for (ImageInfo image : images) {
            testImage(detector, image);
        }
    }





    private static ClassifierCascade getCascade() {
        ClassifierCascade result = null;
        InputStream in = null;
        try {
            in = OpenCVDetection.class.getResourceAsStream("haarcascade_frontalface_alt.xml");
            result = Cascades.readFromXML(in);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }





    private static void testImage(MultiscaleDetection detector, ImageInfo imageInfo) {
        System.out.println("Testing on image: " + imageInfo.path + " (" + imageInfo.expectedFaces + " faces)");
        BufferedImage fullSizeImage;
        try {
            fullSizeImage = ImageIO.read(new File(imageInfo.path));
        } catch (IOException e) {
            System.err.println("Could not load image: " + imageInfo.path);
            e.printStackTrace();
            // this is a test environment error - exit
            return;
        }

        GroupingPolicy grouping = new GroupingPolicy();


        final float scaleFactor = 0.5f;
        BufferedImage image = fullSizeImage;
        int minWindowSize;

        // original image test
        minWindowSize = getMinWindowsSize(image);
        TestInfo info = testScaledImage(image, detector, minWindowSize);
        System.out.println("Original  for [" + image.getWidth() + 'x' + image.getHeight() + "] " +
                           info.runs + " runs (in " + info.totalTimeMillis + "ms)");

        int facesFound;
        do {
            image = scaleImage(image, scaleFactor);
            minWindowSize = getMinWindowsSize(image);
            List<Rectangle> result = detector.detectObjects(image, minWindowSize);
            result = grouping.reduceAreas(result);
            facesFound = result.size();

            if (facesFound == imageInfo.expectedFaces) {
                info = testScaledImage(image, detector, minWindowSize);

                System.out.println("Ran tests for [" + image.getWidth() + 'x' + image.getHeight() + "] " +
                                   info.runs + " runs (in " + info.totalTimeMillis + "ms)");
            } else {
                System.out.println(" [" + image.getWidth() + 'x' + image.getHeight() + "] facesFound = " + facesFound);
            }

        } while (facesFound == imageInfo.expectedFaces);
    }





    private static TestInfo testScaledImage(BufferedImage image, MultiscaleDetection detector, int minWindowSize) {
        final long totalTimeSeconds = 5;
        final long totalTime = totalTimeSeconds * 1000 * 1000 * 1000; // converted to nanos

        TestInfo info = new TestInfo();

        final long t0 = System.nanoTime();
        while (System.nanoTime() - t0 < totalTime) {
            // run test
            info.runs++;
            info.facesFound = detector.detectObjects(image, minWindowSize).size();
        }
        final long t1 = System.nanoTime();

        info.totalTimeMillis = (t1 - t0) / (double) 1000000;
        info.averageTimeMillis = info.runs / info.totalTimeMillis; // convert to millis

        return info;
    }





    private static int getMinWindowsSize(BufferedImage image) {
        final float minWinFactor = 0.08f;
        return (int) Math.min(image.getWidth() * minWinFactor, image.getHeight() * minWinFactor);
    }





    private static BufferedImage scaleImage(BufferedImage image, float scaleFactor) {
        final int width = (int) (image.getWidth() * scaleFactor);
        final int height = (int) (image.getHeight() * scaleFactor);
        BufferedImage result = new BufferedImage(width, height, image.getType());
        Graphics2D g = result.createGraphics();
        // get much better scalability with this flag
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        return result;
    }





    /** Info for loading and checking a simgel image */
    private static class ImageInfo {

        private final String path;
        private final int expectedFaces;





        private ImageInfo(String path, int expectedFaces) {
            this.expectedFaces = expectedFaces;
            this.path = path;
        }
    }

    private static class TestInfo {

        private int runs = 0;
        private double totalTimeMillis = 0;
        private double averageTimeMillis = 0;
        private int facesFound = 0;
    }
}
