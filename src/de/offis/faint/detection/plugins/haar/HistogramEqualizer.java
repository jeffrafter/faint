/**
 * 
 */
package de.offis.faint.detection.plugins.haar;

import java.awt.image.BufferedImage;

/**
 * Function to equalize the histogram of an image. This can be used
 * as a preprocessing step to the HaarClassifier detection.
 * 
 * @author huangnankun
 *
 */
public class HistogramEqualizer {
	
	 public static BufferedImage histoGramEqualizeGray( BufferedImage
			 input ) {
		 BufferedImage output = new BufferedImage(input.getWidth(),
				 input.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		 int[] frequency = new int[256];
		 int maxIntensity = 255;
		 for(int x = 0; x < input.getWidth(); x++)
		 {
			 for(int y = 0; y < input.getHeight(); y++)
			 {
				 frequency[getGrayValue(input.getRGB(x,y))]++;
			 }
		 }
		 int sum = 0;
		 for(int x = 0; x < frequency.length; x++)
		 {
			 sum += frequency[x];
			 frequency[x] = sum * maxIntensity /(input.getWidth() *
					 input.getHeight());
		 }
		 for(int x = 0; x < input.getWidth(); x++)
		 {
			 for(int y = 0; y < input.getHeight(); y++)
			 {
				 double[] pixel = new double[1];
				 pixel[0] = getGrayValue(input.getRGB(x,y));
				 pixel[0] = frequency[(int)pixel[0]];
				 output.getRaster().setPixel(x, y, pixel);
			 }
		 }
		 return output;
	 } 

     static int getGrayValue(final int rgb) {
         return (((rgb >> 16) & 0xFF) +
                 ((rgb >> 8) & 0xFF) +
                 (rgb & 0xFF)) / 3;
     }
     
     
}
