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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import de.offis.faint.controller.MainController;
import de.offis.faint.global.Constants;

/**
 * @author maltech
 *
 */
public class FaceDatabase implements Serializable {
		
	private static final long serialVersionUID = -6980354179180812672L;
	
	private HashMap<String, ArrayList<Region>> regionsByFace = new HashMap<String, ArrayList<Region>>();
	private HashMap<String, ArrayList<Region>> regionsByFile = new HashMap<String, ArrayList<Region>>();
	
	public synchronized void put(Region region, String name){
		
		// don't delete annotations that have already been made
		if (name != null && name.equals(Constants.UNKNOWN_FACE) && this.containsRegion(region)){
			return;
		}
		
		// cache region on HD, if it belongs to known person
		if (name!= null && region != null && !name.equals(Constants.UNKNOWN_FACE) && region.getCachedFile()==null){
			try {
				region.cacheToDisk();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// put region into the hash tables
		if (this.containsRegion(region)){
			for(String nameInDB : regionsByFace.keySet()){
				if (regionsByFace.get(nameInDB).contains(region)){
					regionsByFace.get(nameInDB).remove(region);
				}
			}
		}
		else
		{			
			if (region != null){
				if (!regionsByFile.containsKey(region.getImage())){
					regionsByFile.put(region.getImage(), new ArrayList<Region>());
				}
				regionsByFile.get(region.getImage()).add(region);
			}
		}
		if (!regionsByFace.containsKey(name)){
			regionsByFace.put(name, new ArrayList<Region>());
		}
		
		if (region != null)
			regionsByFace.get(name).add(region);
	}
	
	public synchronized void deleteRegion(Region region) {
		if (region != null){
			
			region.deleteCachedFile();
			
			regionsByFile.get(region.getImage()).remove(region);
			
			for (ArrayList<Region> list: regionsByFace.values()){
				if (list.contains(region)){
					list.remove(region);
				}
			}
		}
	}
	
	public void deleteAnnotation(String name) {
		ArrayList<Region> regions = regionsByFace.get(name);
		regionsByFace.remove(name);
		regionsByFace.get(Constants.UNKNOWN_FACE).addAll(regions);
	}
	
	public synchronized void renameAnnotation(String oldName, String newName){
		ArrayList<Region> regions = regionsByFace.get(oldName);
		regionsByFace.remove(oldName);
		if (regionsByFace.containsKey(newName))
			regionsByFace.get(newName).addAll(regions);
		else
			regionsByFace.put(newName, regions);
	}
	
	public boolean containsRegion(Region region){
		
		if (region != null && regionsByFile.get(region.getImage()) != null)
			return regionsByFile.get(region.getImage()).contains(region);
		return false;
	}
	
	public Region[] getRegionsForFace(String name){
		if (!regionsByFace.containsKey(name))
		return null;
		else{
			regionsByFace.get(name).remove(null);
			Region[] result = new Region[regionsByFace.get(name).size()];
			return regionsByFace.get(name).toArray(result);	
		}
	}
	
	public String[] getKnownFiles(){
		return regionsByFile.keySet().toArray(new String[regionsByFile.keySet().size()]);
	}
	
	public Region[] getRegionsForImage(String file){
		if (regionsByFile.get(file) == null){
		return new Region[0];
		}
		else{
			Region[] result = new Region[regionsByFile.get(file).size()];
			return regionsByFile.get(file).toArray(result);
		}
	}
	
	public Region[] getKnownFaces(){
		ArrayList<Region> result = new ArrayList<Region>(regionsByFace.size());
		for (String name : regionsByFace.keySet()){
			if (!name.equals(Constants.UNKNOWN_FACE))
				result.addAll(regionsByFace.get(name));
		}
		while (result.contains(null)) result.remove(null);
		Region[] array = new Region[result.size()];
		return (Region[]) result.toArray(array);
	}
	
	public Region[] getTrainingImages(){
		ArrayList<Region> result = new ArrayList<Region>(regionsByFace.size());
		for (String name : regionsByFace.keySet()){
			if (!name.equals(Constants.UNKNOWN_FACE)){
				ArrayList<Region> faces = regionsByFace.get(name);
				for (Region r : faces){
					if (r.isUsedForTraining())
						result.add(r);
				}
			}
		}
		Region[] array = new Region[result.size()];
		return (Region[]) result.toArray(array);
	}
	
	public String[] getExistingAnnotations(){
		Set<String> keys = this.regionsByFace.keySet();
		
		int nameCount = keys.size();
		if (keys.contains(null))
			nameCount -= 1;
		if (keys.contains(Constants.UNKNOWN_FACE))
			nameCount -= 1;
		String[] faces = new String[nameCount];
		for (String name : keys){
			if (name !=null && !name.equals(Constants.UNKNOWN_FACE)){
				nameCount--;
				faces[nameCount] = name;
			}
		}
		
		return faces;
	}
	
	public String getAnnotation(Region r){
		for (String annotation : regionsByFace.keySet()){
			if (regionsByFace.get(annotation).contains(r)){
				return annotation;
			}
		}
		return null;
	}
	
	public void writeToDisk() throws IOException{
		String filename = MainController.getInstance().getDataDir().getPath()+File.separator+Constants.FACE_DB_FILE;
		FileOutputStream fos = new FileOutputStream(filename);
		ObjectOutputStream out = new ObjectOutputStream(fos);
		out.writeObject(this);
		out.close();
	}
	
	public static FaceDatabase recoverFromDisk() throws IOException{
		MainController.getInstance().getDataDir();
		String filename = MainController.getInstance().getDataDir()+File.separator+Constants.FACE_DB_FILE;
		FileInputStream fis = new FileInputStream(filename);
		ObjectInputStream in = new ObjectInputStream(fis);
		FaceDatabase db = null;
		try {
			db = (FaceDatabase)in.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		in.close();
		return db;		
	}
}
