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

package de.offis.faint.detection.filters.skincolor;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;

import javax.imageio.ImageIO;

/**
 * 
 * This tool can be used to generate a hue-saturation-table
 * out of sample skin and non-skin images and masks.
 * 
 *
 * console output:
 
processing skin files
124806.jpg caused an error
1296780.gif caused an error
1803749.gif caused an error
2253453.jpg caused an error
Skin Pixel Count: 80468616
Non Skin Pixel Count: 419214030
processing nonskin files
1002100.gif caused an error
1034036.gif caused an error
1151839.gif caused an error
12492.gif caused an error
1362590.gif caused an error
167671.gif caused an error
1679735.gif caused an error
1762639.gif caused an error
1989526.gif caused an error
2003739.gif caused an error
555731.gif caused an error
617105.gif caused an error
683153.gif caused an error
688911.gif caused an error
82273.gif caused an error
953116.gif caused an error
Non Skin Pixel Count: 1273154592
Processing hue saturation table
Writing table to disk

 */
public class TableBuilder {
	
	public static final BigInteger ONE = new BigInteger("1");
	
	public final static String skinFolder = "C:\\\\Dokumente und Einstellungen\\\\maltech\\\\Desktop\\\\ip\\\\skin detect\\\\skin-images";
	public final static String maskFolder = "C:\\\\Dokumente und Einstellungen\\\\maltech\\\\Desktop\\\\ip\\\\skin detect\\\\masks";
	public final static String nonSkinFolder = "C:\\\\Dokumente und Einstellungen\\\\maltech\\\\Desktop\\\\ip\\\\skin detect\\\\non-skin-images";
	

	public static void main(String[] args){
		
		BigInteger[][] skinHueSaturation = new BigInteger[256][256];		
		BigInteger[][] nonSkinHueSaturation = new BigInteger[256][256];
		
		for (int i = 0; i<skinHueSaturation.length; i++){
			for (int j = 0; j<skinHueSaturation.length; j++){
				skinHueSaturation[i][j]= new BigInteger("0");
				nonSkinHueSaturation[i][j]= new BigInteger("0");
			}
		}
		
		BigInteger skinPixelCount = new BigInteger("0");
		BigInteger nonSkinPixelCount = new BigInteger("0");
		
		BufferedImage tempImage, tempMask;
		float[] tempColor = new float[3];
		
		System.out.println("processing skin files");
		File f = new File(skinFolder);
		File[] images = f.listFiles();
		for (File image : images){
			
			try{
				tempImage = ImageIO.read(image);
				String imageName = image.getName();
				
				File mask = new File(maskFolder + File.separator + imageName.substring(0, imageName.length()-4) + ".gif");
				tempMask = ImageIO.read(mask);

				for (int y = 0; y < tempImage.getHeight(); y++){
					for(int x = 0; x < tempImage.getWidth(); x++){
						
		
						Color c = new Color(tempImage.getRGB(x,y));
						Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), tempColor);
						
						int hIndex = Math.round(tempColor[0] * 255);
						int sIndex = Math.round(tempColor[1] * 255);
						Color col = new Color(tempMask.getRGB(x,y));
						if (col.getBlue() == Color.BLACK.getBlue()){
							nonSkinPixelCount = nonSkinPixelCount.add(ONE);
							nonSkinHueSaturation[hIndex][sIndex] = nonSkinHueSaturation[hIndex][sIndex].add(ONE);
						}
						else{
							skinPixelCount = skinPixelCount.add(ONE);
							skinHueSaturation[hIndex][sIndex] = skinHueSaturation[hIndex][sIndex].add(ONE);
						}
					}
				}
//				System.out.print(".");
			} catch (Exception e){
				System.err.println(image.getName() +" caused an error");
//				e.printStackTrace();
			};
		}	
		System.out.println("Skin Pixel Count: " + skinPixelCount.toString());
		System.out.println("Non Skin Pixel Count: "+nonSkinPixelCount);

		
		System.out.println("processing nonskin files");
		f = new File(nonSkinFolder);
		images = f.listFiles();
		for (File image : images){
			
			try{
				tempImage = ImageIO.read(image);
				for (int y = 0; y < tempImage.getHeight(); y++){
					for(int x = 0; x < tempImage.getWidth(); x++){


						Color c = new Color(tempImage.getRGB(x,y));
						Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), tempColor);
						
						int hIndex = Math.round(tempColor[0] * 255);
						int sIndex = Math.round(tempColor[1] * 255);
						nonSkinPixelCount = nonSkinPixelCount.add(ONE);
						nonSkinHueSaturation[hIndex][sIndex] = nonSkinHueSaturation[hIndex][sIndex].add(ONE);
					}
				}
//				System.out.print(".");
			} catch (Exception e){
				System.err.println(image.getName() +" caused an error");
//				e.printStackTrace();
				};
		}
		System.out.println("Non Skin Pixel Count: "+nonSkinPixelCount);
		
		try {
			writeToDisk(skinHueSaturation,"skin.dat");
			writeToDisk(nonSkinHueSaturation,"nonskin.dat");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		
//		try {
//			InputStream file = new FileInputStream(new File("./skin.dat"));
//			ObjectInputStream in = new ObjectInputStream(file);
//			skinHueSaturation = (BigInteger[][]) in.readObject();
//			in.close();			
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}		
//		try {
//			InputStream file = new FileInputStream(new File("./nonskin.dat"));
//			ObjectInputStream in = new ObjectInputStream(file);
//			nonSkinHueSaturation = (BigInteger[][]) in.readObject();
//			in.close();
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

//		BigInteger nonSkinPixelCount = new BigInteger("1273154592");
//		BigInteger skinPixelCount =    new BigInteger(  "80468616");
		
		BigInteger ratio = (nonSkinPixelCount.divide(skinPixelCount));
		
		HS_Table table = new HS_Table();

		System.out.println(ratio +"Processing hue saturation table");
		for (int i = 0; i< 256; i++){
			for (int j = 0; j< 256; j++){
				
				nonSkinHueSaturation[i][j] = nonSkinHueSaturation[i][j].divide(ratio);
				
				boolean b = nonSkinHueSaturation[i][j].compareTo(skinHueSaturation[i][j]) > 0;
//				System.out.println(b);
				table.values[i].set(j,b);
			}
		}

		System.out.println("Writing table to disk");
		try{
			writeToDisk(table, "./hstable.dat");
		} catch(Exception e){}
	}
	
	
	public static void writeToDisk(Serializable object, String filename) throws IOException{
		FileOutputStream fos = new FileOutputStream(filename);
		ObjectOutputStream out = new ObjectOutputStream(fos);
		out.writeObject(object);
		out.close();
	}
}
