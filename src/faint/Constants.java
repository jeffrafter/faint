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
import java.awt.image.BufferedImage;

public class Constants {
	
	public static final String RELEASE = "unknown";
	
	public static final int INITIAL_HORIZONTAL_DIVIDERLOCATION = 280;

	public static final int PREVIEW_MARGIN = 3;

	public static final Dimension THUMBNAIL_SIZE = new Dimension(100,75);
	public static final int THUMBNAIL_MARGIN = 10;

	public static final Dimension FACE_THUMBNAIL_SIZE = new Dimension(75,75);
	public static final int FACE_THUMBNAIL_MARGIN = 20;
	
	public static final int SCALE_MODE = Image.SCALE_FAST;
	public static final int THUMBNAIL_IMAGETYPE = BufferedImage.TYPE_3BYTE_BGR;

	public static final String FACE_DB_FILE = "faces.bin";
	public static final String UNKNOWN_FACE = "unknown";

	public static final String CACHED_IMAGE_TYPE = "png";

	public static final String[] IMAGE_SUFFIXES = {".jpg",".JPG",".jpeg",".JPEG",".png",".PNG"};
	
}
