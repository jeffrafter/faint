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

package de.offis.faint.controller;

import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * @author maltech
 *
 */
public class BufferedImageCache {

	private BufferedImage cachedImage = null;
	private String key = null;
	
	public synchronized BufferedImage getImage(File file) {
		if (key != null && key.equals(file.toString()))
			return cachedImage;
		else
			try {
                // convert the image into a more efficient type
                BufferedImage tmp = ImageIO.read(file);
                BufferedImage image = GraphicsEnvironment.getLocalGraphicsEnvironment().
                        getDefaultScreenDevice().getDefaultConfiguration().
                        createCompatibleImage(tmp.getWidth(), tmp.getHeight(), tmp.getTransparency());
                Graphics g = image.createGraphics();
                g.drawImage(tmp, 0, 0, null);
                g.dispose();
                tmp.flush();
                return image;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
	}
	
	public synchronized void cacheImage(String file){
		
		if (key == null || !key.equals(file)){
			cachedImage = getImage(new File(file));
			key  = file;
		}
	}
	
	public boolean isCached(String file){
		return(file.equals(key));
	}
}
