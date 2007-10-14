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

package de.offis.faint.recognition.filters.simplecontext;

import java.util.HashMap;

import de.offis.faint.controller.MainController;
import de.offis.faint.interfaces.IRecognitionFilter;
import de.offis.faint.model.FaceDatabase;
import de.offis.faint.model.Region;

/**
 * @author maltech
 *
 */
public class SimpleContextFilter implements IRecognitionFilter {

	/* (non-Javadoc)
	 * @see de.offis.faint.interfaces.IRecognitionFilter#filterRecognitionResult(de.offis.faint.model.Region, java.util.HashMap)
	 */
	public HashMap<String, Integer> filterRecognitionResult(Region region,
			HashMap<String, Integer> result) {
		
		FaceDatabase faceDB = MainController.getInstance().getFaceDB();
		
		Region[] knownRegions = faceDB.getRegionsForImage(region.getImage());
		
		for (String person : result.keySet()){
			for (Region regionOnImage : knownRegions){
				if (!region.equals(regionOnImage) && faceDB.getAnnotation(regionOnImage).equals(person))
					result.put(person, 0);					
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see de.offis.faint.interfaces.IModule#getDescription()
	 */
	public String getDescription() {
		return "<p>This filter searches for already classified persons on the current photo and sets their recognition score to zero, assuming that a person can only appear once on the same photo.</p>";
	}

	public String getCopyrightNotes() {
		return "<p>Malte Mathiszig 2007</p>";
	}
	
	public String getName(){
		return "Simple Context Filter";
	}

}
