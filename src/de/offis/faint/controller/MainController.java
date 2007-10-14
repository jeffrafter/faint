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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import de.offis.faint.data.RessourceLoader;
import de.offis.faint.global.Constants;
import de.offis.faint.global.Utilities;
import de.offis.faint.interfaces.IDetectionFilter;
import de.offis.faint.interfaces.IDetectionPlugin;
import de.offis.faint.interfaces.IModule;
import de.offis.faint.interfaces.IRecognitionFilter;
import de.offis.faint.interfaces.IRecognitionPlugin;
import de.offis.faint.model.FaceDatabase;
import de.offis.faint.model.ImageModel;
import de.offis.faint.model.Region;

/**
 * @author maltech
 *
 */
public class MainController {
	
	private static MainController uniqueInstance = null;
	
	private static final String PLUGIN_CONFIG_FILE = "Modules.conf";
	static final String PLUGIN_SUFFIX = ".data";
	private static final String APPLICATION_DIRECTORY = ".faint";

	// Controllers for Detection and Recognition
	private HotSpotController<IDetectionPlugin,IDetectionFilter> detectionHotSpot;
	private HotSpotController<IRecognitionPlugin, IRecognitionFilter> recognitionHotSpot;
	
	// Minimum scan window size in percent of original min(width, height)
	private int scanWindowSize = 15;
	
	// Dataholding
	private FaceDatabase faceDB;
	private File dataDir;
	
    // Image cache
	private BufferedImageCache bufferedImageCache = new BufferedImageCache();
	
	/**
	 * Static method providing an instance of this class. There can only be
	 * one instance at runtime, see Singleton Pattern for more info.
	 * 
	 * @return The only instance of MainController.
	 */
	public static MainController getInstance(){
		if (uniqueInstance == null)
			uniqueInstance = new MainController();
		return uniqueInstance;
	}
	
	/**
	 * Private Constructor called by the getInstance() method.
	 * 
	 */
	private MainController(){
		uniqueInstance = this;
		
		dataDir = new File(System.getProperty("user.home") + File.separator + APPLICATION_DIRECTORY );
		dataDir.mkdirs();

		// try to load database from disk
		System.out.println("Preparing face database...");
		try {
			faceDB = FaceDatabase.recoverFromDisk(); 
		}catch(IOException ex){
			faceDB = new FaceDatabase();
		}
		
		// init plugins specified in config file
		System.out.println("Loading Modules...");
		File configFile = new File(dataDir.getAbsoluteFile() + File.separator + PLUGIN_CONFIG_FILE);
		try {
		if (!configFile.exists()){
				Utilities.saveFileFromURL(RessourceLoader.getFile(PLUGIN_CONFIG_FILE), configFile);		
		}
		String config = Utilities.inputStreamToString(configFile.toURL().openStream());
		String[] lines = config.split("\r\n");
		ArrayList<IDetectionPlugin> detectionPlugins = new ArrayList<IDetectionPlugin>();
		ArrayList<IRecognitionPlugin> recognitionPlugins = new ArrayList<IRecognitionPlugin>();
		ArrayList<IDetectionFilter> detectionFilters = new ArrayList<IDetectionFilter>();
		ArrayList<IRecognitionFilter> recognitionFilters = new ArrayList<IRecognitionFilter>();
		for (String line : lines){
			if (!(line.length()==0) && !line.startsWith(" ") && !line.startsWith("\r")){
				
				String pluginName = line.substring(line.lastIndexOf('.')+1);
				System.out.println(">> "+ pluginName);
				
				Object module = null;
				
				// try to deserialize plugin from disk
				try {
					FileInputStream fis = new FileInputStream(dataDir.getAbsoluteFile() + File.separator + pluginName + PLUGIN_SUFFIX);
					ObjectInputStream in = new ObjectInputStream(fis);
					module = (IModule)in.readObject();
					in.close();
				} catch (Exception e) {}
				
				
				// create new instance of plugin, if needed
				if (module == null)
					module = Class.forName(line.trim()).newInstance();
				
				if (module instanceof IDetectionPlugin)
					detectionPlugins.add((IDetectionPlugin) module);
				if (module instanceof IRecognitionPlugin)
					recognitionPlugins.add((IRecognitionPlugin) module);
				if (module instanceof IDetectionFilter)
					detectionFilters.add((IDetectionFilter) module);
				if (module instanceof IRecognitionFilter)
					recognitionFilters.add((IRecognitionFilter) module);
			}
		}
		

		// Prepare hot spot for Detection
		IDetectionPlugin[] availableDetectionPlugins = new IDetectionPlugin[detectionPlugins.size()];
		IDetectionFilter[] availableDetectionFilters = new IDetectionFilter[detectionFilters.size()];
		detectionPlugins.toArray(availableDetectionPlugins);		
		detectionFilters.toArray(availableDetectionFilters);		
		this.detectionHotSpot = new HotSpotController<IDetectionPlugin, IDetectionFilter>(availableDetectionPlugins, availableDetectionFilters);
		
		// Prepare hot spot for Recognition
		IRecognitionPlugin[] availableRecognitionPlugins = new IRecognitionPlugin[recognitionPlugins.size()];
		IRecognitionFilter[] availableRecognitionFilters = new IRecognitionFilter[recognitionFilters.size()];
		recognitionPlugins.toArray(availableRecognitionPlugins);
		recognitionFilters.toArray(availableRecognitionFilters);
		this.recognitionHotSpot = new HotSpotController<IRecognitionPlugin, IRecognitionFilter>(availableRecognitionPlugins, availableRecognitionFilters);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method uses the active Plugin of the Detection Hot Spot to
	 * detect faces in an image.
	 * 
	 * @param image  The image that will be analyzed.
	 * @param storeInDB	 Determines if possible faces should be send to the database directly.
	 * @return  Array of Region elements, that are probably faces or null, if no faces were found.
	 */
	public Region[] detectFaces(ImageModel image, boolean storeInDB){
		
		int scanWindowPixelSize = (int) Math.max(10, Math.round(((double)Math.min(image.getWidth(), image.getHeight()) * ((double) this.scanWindowSize / 100.0f))));
		
		Region[] possibleFaces = detectionHotSpot.getActivePlugin().detectFaces(image.getFile().getAbsolutePath(), scanWindowPixelSize);
		
		for (IDetectionFilter filter : detectionHotSpot.getActiveFilters()){
			if (possibleFaces!=null && possibleFaces.length>0)
				possibleFaces = filter.filterDetectionResult(possibleFaces);
		}
				
		// Store Regions in DB
		if (possibleFaces != null && storeInDB)
			for (int i = 0; i <possibleFaces.length; i++){
				faceDB.put(possibleFaces[i], Constants.UNKNOWN_FACE);
			}
		
		return possibleFaces;
	}
	
	/**
	 * This method uses the active Plugin of the Recognition Hot Spot to try
	 * to recognize a given face.
	 * 
	 * @param face Region that will be analyzed.
	 * @return HashTable containing names of known people and weights describing their similarity to the given face.
	 */
	public HashMap<String, Integer> recognizeFace(Region face){
		HashMap<String, Integer> result = recognitionHotSpot.getActivePlugin().getRecognitionPoints(face);
		
		for (IRecognitionFilter filter : recognitionHotSpot.getActiveFilters())
			result = filter.filterRecognitionResult(face, result);
		
		return result;
	}

		
	//--------------  Getters ---------------//
	

	public BufferedImageCache getBufferedImageCache() {
		return bufferedImageCache;
	}

	public File getDataDir() {
		return dataDir;
	}

	public FaceDatabase getFaceDB() {
		return faceDB;
	}

	public HotSpotController<IDetectionPlugin, IDetectionFilter> getDetectionHotSpot() {
		return this.detectionHotSpot;
	}

	public HotSpotController<IRecognitionPlugin, IRecognitionFilter> getRecognitionHotSpot() {
		return this.recognitionHotSpot;
	}

	public int getScanWindowSize() {
		return scanWindowSize;
	}

	public void setScanWindowSize(int scanWindowSize) {
		this.scanWindowSize = scanWindowSize;
	}
}
