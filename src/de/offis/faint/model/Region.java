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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;

import de.offis.faint.controller.MainController;
import de.offis.faint.global.Constants;

/**
 * A Region is a rectangle of a source image that is of interest to us. This can be something that has been identified
 * as containing a face or other object that we have been trained to look for. A Region has the ability to create
 * thumbnail images for the area it represents employing caching techniquest to reduce processing.
 *
 */
public class Region implements Serializable{
	
	private static final long serialVersionUID = 4670842002974016100L;
	
	// Main attributes
	private String image;
	private int x, y, width, height;
	private double angle;
	private boolean usedForTraining = true;

	private String cachedFile = null;
	private transient BufferedImage thumbnail = null;
	
	
	public Region(int x, int y, int width, int height, double angle, String image){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.angle = angle;
		this.image = image;
	}
	
	
	public String toString(){
		return "position = ["+x+","+y+"] | width = "+width+" | height = "+height+" | angle = "+angle+" | image = "+this.image;
	}
	
	public boolean equals(Object region){
		if (region == null) return false;
		Region that = (Region) region;
		return (this.image.equals(that.image) &&
				this.angle == that.angle &&
				this.height == that.height &&
				this.width == that.width &&
				this.x == that.x &&
				this.y == that.y);
	}
	
	
	//--------------- Cache Methods --------------------//
    /**
     * Returns the image portion that this regeion represents scaled to the given size.
     *
     * @param width The target width
     * @param height The target height
     * @return A thumbnail of the image this region represents.
     */
	public BufferedImage toThumbnail(int width, int height){
		
		if (thumbnail == null) {
			
			if (cachedFile == null || MainController.getInstance().getBufferedImageCache().isCached(image))
			{
				
				thumbnail = new BufferedImage(width, height, Constants.THUMBNAIL_IMAGETYPE);
				Graphics2D graphics = (Graphics2D) thumbnail.getGraphics();
				BufferedImage bimage = MainController.getInstance().getBufferedImageCache().getImage(new File(image));
				
				// scale to fit size
				double scaleX = (double)width / (double) this.width;
				double scaleY = (double)height / (double) this.height;
				graphics.scale(scaleX, scaleY);
				
				// center rotation point on region and rotate
				graphics.translate(Math.round((double)getWidth()/2),Math.round((double)getHeight()/2));
				graphics.rotate(Math.toRadians(angle));
				
				// draw
				graphics.drawImage(bimage,-x,-y,null);
				
			}
			
			// else get the thumbnail from a cache on disk
			else{
				
				try {
					File f = new File(MainController.getInstance().getDataDir().getPath() + File.separator + cachedFile);
					this.thumbnail = ImageIO.read(f);
				} catch (IOException e) {
					this.cachedFile = null; return this.toThumbnail(width, height);
				}
			}
		}
		return thumbnail;
	}





    /**
     * Returns the image portion that this regeion represents scaled to the given size.
     *
     * @param size The target size for the image
     * @return A thumbnail of the image this region represents.
     */
    public BufferedImage toThumbnail(Dimension size) {
		return this.toThumbnail(size.width, size.height);				
	}





    /**
     * Clear the cache of this thumbnaill.
     */
	public void clearThumbnail(){
		this.thumbnail = null;
	}





    /**
     * Cache the thumbnail for this region image to disk in the MainController.getInstance().getDataDir() path.
     *
     * @throws IOException if something goes wrong writing to disk
     */
	public void cacheToDisk() throws IOException {

		// find next free file name
		int i = 0;
		File file = null;
		String dir = MainController.getInstance().getDataDir().getPath();
		String leadingZeros = "00000000";
        // fixme: this bit could use File.createTempFile()
        do{
			String fileName = "" + i++;
			fileName = leadingZeros.substring(Math.min(fileName.length(), leadingZeros.length()-1))
			           + fileName + "." + Constants.CACHED_IMAGE_TYPE;
			
			file = new File(dir + File.separator + fileName);
		}
		while (file.exists());
		

		// save image
		ImageIO.write(this.toThumbnail(Constants.FACE_THUMBNAIL_SIZE.width,
				                       Constants.FACE_THUMBNAIL_SIZE.height),
				                       Constants.CACHED_IMAGE_TYPE, file);
		// remember file
		this.cachedFile = file.getName();
		
//		System.out.println(file.getName());
	}





    /**
     * Clears the cache of this region thumbnail from memory so that the next time it is requested it will need to be
     * loaded/created again. This will attempt to cache to disk any existing thumbnail in memory overriting the
     * thumbnail that may exist there already.
     */
    private void updateCache(){
		if (cachedFile != null){
			deleteCachedFile();
			try {
				this.cacheToDisk();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.thumbnail = null;
	}





    /**
     * Deletes the cached thumbnail file for this region from disk.
     */
    public void deleteCachedFile(){
		if (cachedFile!=null){
			File file = new File(MainController.getInstance().getDataDir().getPath() + File.separator + cachedFile);
			file.delete();
			cachedFile = null;
		}
	}
	
	
	//-------------------- Setter and Getter --------------//

	public String getImage() {
		return this.image;
	}
	
	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
		this.updateCache();
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
		this.updateCache();
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
		this.updateCache();
	}
	
	public void setPosition(Point p){
		this.x = p.x;
		this.y = p.y;
		this.updateCache();
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
		this.updateCache();
	}
	
	public String getCachedFile() {
		return cachedFile;
	}

	public void setCachedFile(String cachedFile) {
		this.cachedFile = cachedFile;
	}


	public boolean isUsedForTraining() {
		return usedForTraining;
	}
	
	/**
	 * Checks if a given point on the image lays inside of the region
	 * @param point
	 * @return
	 */
	public boolean containsPoint(Point point){
		
		// set point relative to center of region
		int x = getX() - point.x;
		int y = getY() - point.y;
		
		// rotate point to region coords
		double angle = Math.toRadians(getAngle());
		double finalX = x * Math.cos(angle) + y * -Math.sin(angle);
		double finalY = x * Math.sin(angle) + y * Math.cos(angle);
		
		if (Math.abs(finalX) < getWidth()/2 && Math.abs(finalY) < getHeight()/2)			
			return true;
		
		return false;
	}


	public void setUsedForTraining(boolean usedForTraining) {
		this.usedForTraining = usedForTraining;
	}


	/**
	 * @return
	 */
	public double getAngle() {
		return this.angle;
	}


	/**
	 * @param newAngle
	 */
	public void setAngle(double newAngle) {
		this.angle = newAngle;		
	}


}
