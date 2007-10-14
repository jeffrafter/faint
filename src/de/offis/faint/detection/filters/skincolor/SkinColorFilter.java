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
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import javax.swing.JPanel;

import de.offis.faint.global.Constants;
import de.offis.faint.interfaces.IDetectionFilter;
import de.offis.faint.interfaces.ISwingCustomizable;
import de.offis.faint.model.Region;

/**
 * @author maltech
 *
 */
public class SkinColorFilter implements IDetectionFilter, ISwingCustomizable {
	
	private static final long serialVersionUID = 7413522209901360254L;

	private static final String TABLE_FILE = "hstable.dat";
	
	private transient SkinColorSettingsPanel settingsPanel = null;
	
	protected int threshold = 30;
	
	private HS_Table hsTable;
	

	/**
	 * Constructor.
	 *
	 */
	public SkinColorFilter() {
		try {
			InputStream file = SkinColorFilter.class.getResource(TABLE_FILE).openStream();
			ObjectInputStream in = new ObjectInputStream(file);
			hsTable = (HS_Table)in.readObject();
			in.close();
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see de.offis.faint.interfaces.IDetectionFilter#filterDetectionResult(de.offis.faint.model.Region[])
	 */
	public Region[] filterDetectionResult(Region[] input) {
		
		ArrayList<Region> filteredResult = new ArrayList<Region>();
		
		for (Region region : input){
			if(skinTest(region))
				filteredResult.add(region);
		}
		
		Region[] result = new Region[filteredResult.size()];
		filteredResult.toArray(result);
		return result;
	}
	
	private boolean skinTest(Region region){
		
		BufferedImage thumb = region.toThumbnail(Constants.FACE_THUMBNAIL_SIZE);
		float[] tempColor = new float[3];
		
		int skinPixels = 0;
		for (int h = 0; h < thumb.getHeight(); h++){
			for  (int w = 0; w < thumb.getWidth(); w++){
				
				Color c = new Color(thumb.getRGB(w,h));
				tempColor = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), tempColor);					

				int hIndex = Math.round(tempColor[0] * 255);
				int sIndex = Math.round(tempColor[1] * 255);
				
				if (!hsTable.values[hIndex].get(sIndex)){
					skinPixels++;
				}
			}
		}	
		int percentage = Math.round((100 * skinPixels) / (thumb.getWidth() * thumb.getHeight()));
		return (percentage > threshold);
	}

	/* (non-Javadoc)
	 * @see de.offis.faint.interfaces.IModule#getDescription()
	 */
	public String getDescription() {
		return "<p>The Skin Color Filter uses a 8 KB Hue-Saturation lookup table generated from two billion manually classified skin and non-skin pixels on training images.</p>";
	}

	public String getCopyrightNotes() {
		return "<p>Malte Mathiszig 2007. Training data used to generate the lookup table provided by Mike Jones.</p>";
	}
	
	public String getName(){
		return "Skin Color Filter";
	}
	
	/**
	 * For testing purpose only
	 * @param args
	 */
	public static void main(String[] args){
		SkinColorFilter i = new SkinColorFilter();	
	}

	/* (non-Javadoc)
	 * @see de.offis.faint.interfaces.ISwingCustomizable#getSettingsPanel()
	 */
	public JPanel getSettingsPanel() {
		if (settingsPanel == null)
			settingsPanel = new SkinColorSettingsPanel(this);
		return settingsPanel;
	}
}
