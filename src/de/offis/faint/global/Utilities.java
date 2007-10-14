/*******************************************************************************
 * + -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- +
 * |                                                                         |
 *    faint - The Face Annotation Interface
 * |  Copyright (C) 2007  Malte Mathiszig                                    |
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

package de.offis.faint.global;

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

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Utilities {
	
	public static BufferedImage getScaledCopy(BufferedImage image, int width, int height, int scaleMode){
		BufferedImage copy = new BufferedImage(width, height, Constants.THUMBNAIL_IMAGETYPE);
		copy.getGraphics().drawImage(image.getScaledInstance(width, height, scaleMode),0,0,null);			
		return copy;
	}
	
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
	
	public static void showImageInFrame(Image image, String title){
		JFrame f = new JFrame(title);
		f.add(new JLabel(new ImageIcon(image)));
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		f.pack();
		f.setVisible(true);		
	}
	
	public static String integerToString (Integer number){
		if (number == null) return "?";
		else return number.toString();		
	}
	
	public static String inputStreamToString(InputStream is) throws IOException{
		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];	
		for (int n; (n = is.read(b)) != -1;) {
			out.append(new String(b, 0, n));
		}
		return out.toString();
	}
	
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
	 * @param averageFace
	 * @param face_thumbnail_size
	 * @return
	 */
	public static BufferedImage intensityArrayToBufferedImage(byte[] averageFace, Dimension size) {
		return intensityArrayToBufferedImage(averageFace, size.width, size.height);
	}
	
	public static class FileTypeFilter implements FilenameFilter, Serializable{
		
		private static final long serialVersionUID = -4726057606270522957L;
		
		String[] suffixes;
		
		public FileTypeFilter(String[] suffixes){
			this.suffixes = suffixes;						
		}

		/* (non-Javadoc)
		 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
		 */
		public boolean accept(File dir, String name) {
			for (String suffix : suffixes){
					if (name.endsWith(suffix)) return true;
			}
			return false;
		}
	}
	
	public static String getClassName(Class theClass){
		return theClass.toString().substring(theClass.toString().lastIndexOf('.')+1);		
	}
	
	public static class FolderFilter extends javax.swing.filechooser.FileFilter{

		/* (non-Javadoc)
		 * @see java.io.FileFilter#accept(java.io.File)
		 */
		public boolean accept(File pathname) {
			if (pathname.isDirectory()) return true;
			return false;
		}

		/* (non-Javadoc)
		 * @see javax.swing.filechooser.FileFilter#getDescription()
		 */
		@Override
		public String getDescription() {
			return "Folders";
		}
	}
	
	public static class SortableContainer<K> implements Comparable{
		
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
		public int compareTo(Object o) {
			SortableContainer<K> that = (SortableContainer) o;
			return that.number.compareTo(this.number);
		}
		
	}
}
