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

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

public class ImageModel {
	
	private File file;
	
	private String fileName = null;
	private Integer width = null;
	private Integer height = null;
	private Double aspect = null;
	
	public ImageModel(String path){
		this(new File (path));
	}
	
	/**
	 * @param imageFile
	 */
	public ImageModel(File file) {
		this.file = file;
	}

  // Currently not used
	public Region getRegionAtPoint(Point point, FaceDatabase db){		
		// test if the point lays inside of one of the regions
		for (Region region : db.getRegionsForImage(file.toString()))
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

	public Integer getWidth() {
		return width;
	}
}
