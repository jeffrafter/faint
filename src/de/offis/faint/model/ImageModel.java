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

package de.offis.faint.model;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifDirectory;

import de.offis.faint.controller.MainController;
import de.offis.faint.global.Constants;
import de.offis.faint.global.Utilities;

/**
 * @author maltech
 *
 */
public class ImageModel {
	
	private File file;
	
	private String fileName = null;
	private Integer width = null;
	private Integer height = null;
	private Double aspect = null;
	private Icon tumbnail = null;
	
	public ImageModel(String path){
		this(new File (path));
	}
	
	/**
	 * @param imageFile
	 */
	public ImageModel(File file) {
		this.file = file;
		this.initMetadata();
	}

	private void initMetadata(){
		this.fileName = file.getName();
		try {
			Metadata metadata = JpegMetadataReader.readMetadata(file);			
			Iterator directories = metadata.getDirectoryIterator();
			while (directories.hasNext()) {
				Directory directory = (Directory)directories.next(); //iterate through tags and print to System.out 
				
				// try to extract width and height
				try {width = directory.getInt(ExifDirectory.TAG_EXIF_IMAGE_WIDTH);} catch (MetadataException e){}
				try {height = directory.getInt(ExifDirectory.TAG_EXIF_IMAGE_HEIGHT);} catch (MetadataException e){}
				try {directory.getInt(ExifDirectory.TAG_EXIF_IMAGE_WIDTH);} catch (MetadataException e){}
				
			}
		} catch (JpegProcessingException e) {}
		
		if (width != null && height != null) this.aspect = (double) this.width / (double) this.height;
	}
		
	public void initThumbnail(){
		
		BufferedImage image = null;
		
		// Try to extract thumbnail from Exif header
		try {
			Metadata metadata = JpegMetadataReader.readMetadata(file);
			Iterator directories = metadata.getDirectoryIterator();
			while (directories.hasNext()) {
				Directory directory = (Directory) directories.next(); // iterate through tags 
				
				if (directory instanceof ExifDirectory){
					ExifDirectory exifDir = (ExifDirectory) directory;
					if (exifDir.containsThumbnail()){
							InputStream in = new ByteArrayInputStream(exifDir.getThumbnailData());
							image = ImageIO.read(in);
					}
				}
			}
		} catch (Exception e) {}
		
		if (image == null) image = getImage(true);
		
		double thumbAspect = (double) image.getWidth() / (double) image.getHeight();
		int thumbWidth, thumbHeight;
		
		double maxThumbWidth = (double) Constants.THUMBNAIL_SIZE.width;
		double maxThumbHeight = (double) Constants.THUMBNAIL_SIZE.height;
		double bestAspect = maxThumbWidth / maxThumbHeight;
		if (bestAspect < this.aspect){
			thumbWidth = (int) maxThumbWidth;
			thumbHeight = (int) (maxThumbWidth / thumbAspect);		
		}
		else {
			thumbWidth = (int) (maxThumbHeight * thumbAspect);
			thumbHeight = (int) (maxThumbHeight);		
		}
		this.tumbnail = new ImageIcon(Utilities.getScaledCopy(image, thumbWidth, thumbHeight, Constants.SCALE_MODE));
	}
	
	public BufferedImage getImage(boolean updateMissingMetadata){
		BufferedImage image = MainController.getInstance().getBufferedImageCache().getImage(file);
		
		if (updateMissingMetadata) this.updateMetadataFromImage(image);
		return image;
	}
	
	public Region getRegionAtPoint(Point point){
		
		// test if the point lays inside of one of the regions
		for (Region region : MainController.getInstance().getFaceDB().getRegionsForImage(file.toString()))
			if (region.containsPoint(point))
				return region;

		// return null if no region contains the given point
		return null;		
	}
	
	private void updateMetadataFromImage(BufferedImage image){
		if (this.width == null) this.width = image.getWidth();
		if (this.height == null) this.height = image.getHeight();
		if (width != null && height != null) this.aspect = (double) this.width / (double) this.height;
	}
	
	public boolean isAvailable(){
		return file.exists();
	}
	
	public boolean equals(Object o){
		if (o == null)
			return false;
		ImageModel that = (ImageModel) o;
		return that.file.equals(this.file);
	}

	public Double getAspect() {
		return aspect;
	}

	public File getFile() {
		return file;
	}
	
	public File getFolder() {
		return getFile().getParentFile();
	}

	public String getFileName() {
		return fileName;
	}

	public Integer getHeight() {
		return height;
	}

	public Icon getTumbnail() {
		return tumbnail;
	}

	public Integer getWidth() {
		return width;
	}
}
