/*******************************************************************************
 * + -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- +
 * |                                                                         |
 *    faint - The Face Annotation Interface
 * |  Copyright (C) 2007  Malte Mathiszig                                    |
 * |  Copyright (C) 2010  Jeff Rafter (minification)                         |
 * 
 * |  This program is free software: you can redistribute it and/or modify   |
 *    it under the terms of the GNU General Public License as published by
 * |  the Free Software Foundation, either version 3 of the License, or      |
 *    (at your option) any later version.                                     
 * |                                                                         |
 *    This program is distributed in the hope that it will be useful,
 * |  but WITHOUT ANY WARRANTY; without even the implied warranty of         |
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * |  GNU General Public License for more details.                           |
 * 
 * |  You should have received a copy of the GNU General Public License      |
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * |                                                                         |
 * + -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- +
 *******************************************************************************/
package faint;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;

public class Utilities {
	
	public static BufferedImage getScaledCopy(BufferedImage image, int width, int height, int scaleMode){
		BufferedImage copy = new BufferedImage(width, height, Constants.THUMBNAIL_IMAGETYPE);
		copy.getGraphics().drawImage(image.getScaledInstance(width, height, scaleMode),0,0,null);			
		return copy;
	}

  /**
   * Converts the given image into an array of length width * height containing the average of the RGB values for each
   * pixel in the range 0 - 255 (the intensity).
   *
   * @param input The image to convert
   * @return The array of intensity values
   */
  public static byte[] bufferedImageToIntensityArray(BufferedImage input){
		byte[] result = new byte[input.getHeight() * input.getWidth()];
		int counter = 0;
		for (int h = 0; h < input.getHeight(); h++ ){	
			for (int w = 0; w < input.getWidth(); w++ ){
				
				int argb = input.getRGB(w,h);
				int red = (argb >> 16) & 0xff;
				int green = (argb >> 8) & 0xff;
				int blue = (argb) & 0xff;
				
				result[counter++] = (byte) (Math.round((double)(red+green+blue)/3) & 0xFF);
			}
		}
		return result;
	}

  /**
   * Converts the given intensity array into a grayscale image of w x h dimensions.
   *
   * @param array The array to convert
   * @param w the width of the resulting image
   * @param h The height of the resulting image
   * @return The grayscale image that the intensity values represent
   */
  public static BufferedImage intensityArrayToBufferedImage(byte[] array, int w, int h){
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        int[] nBits = {8};
        ColorModel cm = new ComponentColorModel(cs, nBits, false, true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        SampleModel sm = cm.createCompatibleSampleModel(w, h);
        DataBufferByte db = new DataBufferByte(array, w*h);
        WritableRaster raster = Raster.createWritableRaster(sm, db, null);
        BufferedImage bm = new BufferedImage(cm, raster, false, null);
		return bm;
	}

  /**
   * Normalise the given values over the set of all given values. This will apply the equation
   * {@code newVal = (oldVal - min) * 255 / (max - min)}.
   *
   * @param values The values to normalise
   * @return The normalised values
   */
  public static byte[] spreadGreyValues(double[] values){
		Double temp[] = new Double[values.length];
		for (int i = 0; i < values.length; i++){
			temp[i] = values[i];
		}
		
		byte[] result = new byte[values.length];
		
		Double minValue = Double.POSITIVE_INFINITY;
		Double maxValue = Double.NEGATIVE_INFINITY;
		
		for(double d : values)
		{
			if (minValue > d) minValue = d;
			if (maxValue < d) maxValue = d;
		}
		
		Double interval = Math.abs(maxValue - minValue);
//		System.err.println(interval);
		
		for(int i = 0; i < result.length; i++){
			temp[i] -= minValue;
			temp[i] *= 255;
			temp[i] /= interval;
			
			result[i] = (byte) temp[i].byteValue();
		}
		
		return result;		
	}

  /**
   * Converts the given Integer to a string, if null "?" is returned.
   *
   * @param number The number to convert
   * @return The String representation of the number or "?"
   */
  public static String integerToString (Integer number){
		if (number == null) return "?";
		else return number.toString();		
	}

  /**
   * Reads the whole InputStream and converts it into a string. This does not do any character conversion. This will
   * not close the stream
   *
   * @param is The input stream to read
   * @return The string contents of the stream.
   * @throws IOException if there was an error reading from the stream.
   */
  public static String inputStreamToString(InputStream is) throws IOException{
		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];	
		for (int n; (n = is.read(b)) != -1;) {
			out.append(new String(b, 0, n));
		}
		return out.toString();
	}

  /**
   * Writes the contents of the entry at the given url to the given file.
   *
   * @param url The url to get
   * @param destinationFile The file to write to
   * @throws IOException if something goes wrong
   */
  public static void saveFileFromURL(URL url, File destinationFile) throws IOException{
		FileOutputStream fo = new FileOutputStream(destinationFile);
		InputStream is = url.openStream();
		
		byte[] data = new byte[1024];
		int bytecount = 0;
		do {
			fo.write(data, 0, bytecount);
			bytecount = is.read(data);
		} while (bytecount > 0);
		
		fo.flush();
		fo.close();
	}

	/**
     * Convert the given intensity array to a buffered image of {@code size} dimensions.
     *
	 * @param averageFace The intensity of the pixels
	 * @param face_thumbnail_size The size of the image.
	 * @return The image.
	 */
	public static BufferedImage intensityArrayToBufferedImage(byte[] averageFace, Dimension size) {
		return intensityArrayToBufferedImage(averageFace, size.width, size.height);
	}


  /**
   * Acts the same as theClass.getSimpleName()
   *
   * @param theClass The class to get the simple name for
   * @return The class name.
   */
  public static String getClassName(Class theClass){
		return theClass.toString().substring(theClass.toString().lastIndexOf('.')+1);
	}

  /**
   * Wraps a generic Object instance in a Comparable that takes a numeric order variable.
   */
  public static class SortableContainer<K> implements Comparable {
		
		private K object; 
		private Comparable number;
		

		/**
		 * @param object
		 * @param number
		 */
		public SortableContainer(K object, Comparable number) {
			super();
			this.object = object;
			this.number = number;
		}
		
		public K getObject(){
			return object;
		}
		
		public Comparable getNumber(){
			return number;
		}


		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(T)
		 */
    @SuppressWarnings("unchecked")     
		public int compareTo(Object o) {
			SortableContainer<K> that = (SortableContainer) o;
			return that.number.compareTo(this.number);
		}
		
	}
}
