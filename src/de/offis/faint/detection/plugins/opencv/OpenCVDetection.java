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

package de.offis.faint.detection.plugins.opencv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URL;

import javax.swing.JPanel;

import de.offis.faint.controller.MainController;
import de.offis.faint.global.Utilities;
import de.offis.faint.interfaces.IDetectionPlugin;
import de.offis.faint.interfaces.ISwingCustomizable;
import de.offis.faint.model.Region;

/**
 * @author maltech
 *
 */
public class OpenCVDetection implements IDetectionPlugin, ISwingCustomizable {

	private static final long serialVersionUID = 9040924330337550071L;
	
	// order of libraries matters because of dependency
	private static final String[] LIBRARIES = { "libguide40","cxcore100", "cv100", "highgui100", "opencv"};
	protected static final String SUBFOLDER = "opencv";

	private static final String[] CASCADES = {
			"haarcascade_frontalface_default.xml",
			"haarcascade_frontalface_alt.xml",
			"haarcascade_frontalface_alt_tree.xml",
			"haarcascade_frontalface_alt2.xml",
			"haarcascade_profileface.xml" };
	
	private String currentCascadeFile = CASCADES[0];
	
	private float scaleFactor = 1.1f;
		
	private transient OpenCVSettingsPanel settingsPanel = null;
	private int scanWindowSize;
		
	/**
	 * Constructor.
	 *
	 */
	public OpenCVDetection(){
		
		File dataFolder = new File(MainController.getInstance().getDataDir().getAbsoluteFile() + File.separator + SUBFOLDER);
		dataFolder.mkdirs();
		
		// copy cascade files to disk, if necessary
		for (String cascade : CASCADES){
			
			String file = dataFolder.getAbsolutePath() + File.separator + cascade;
			File destinationFile = new File(file);
			
			if (!destinationFile.exists())
				try {
					URL cascadeURL = OpenCVDetection.class.getResource(cascade);
					Utilities.saveFileFromURL(cascadeURL, destinationFile);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		
		// Load native libraries
		loadLibraries(dataFolder);
		
	}
	
	private void loadLibraries(File dataFolder){
		try {
			String ending = ".dll"; // currently windows only
			for (String lib : LIBRARIES){
				
				String file = dataFolder.getAbsolutePath() + File.separator + lib + ending;
				File destinationFile = new File(file);
				
				// copy library to disk, if necessary
				if (!destinationFile.exists())
				{
					URL libURL = OpenCVDetection.class.getResource(lib + ending);
					Utilities.saveFileFromURL(libURL, destinationFile);
				}
				
				// load library
				System.load(file);
			}
		} catch (Throwable t){
			t.printStackTrace();
			return;			
		}
	}


	/* (non-Javadoc)
	 * @see de.offis.faint.plugins.detection.IDetectionPlugin#detectFaces(java.lang.String)
	 */
	public Region[] detectFaces(String file, int scanWindowSize) {
				
		this.scanWindowSize = scanWindowSize;
		return detectFacesJNI(file, this);
	}
	
	private static native Region[] detectFacesJNI(String file, OpenCVDetection caller);

	public String getCurrentCascadeFile(){
		return currentCascadeFile;
	}
	
	public String getCascade() {
		return MainController.getInstance().getDataDir().getAbsolutePath() + File.separator + SUBFOLDER + File.separator + this.currentCascadeFile;
	}

	public void setCascade(String cascade) {
		this.currentCascadeFile = cascade;
	}
	
	public int getMinSize(){
		return this.scanWindowSize;		
	}
	
	public void setScale(float scaleFactor){
		this.scaleFactor = scaleFactor;		
	}
	
	public float getScale(){
		return scaleFactor;
	}
	
	public String toString(){
		return getName();
	}
	
	/* (non-Javadoc)
	 * @see de.offis.faint.plugins.IPlugin#getName()
	 */
	public String getName() {
		return "OpenCV Haarclassifier Detection";
	}

	/* (non-Javadoc)
	 * @see de.offis.faint.plugins.IPlugin#getRequirementNotes()
	 */
	public String getDescription() {
		return "<p>The OpenCV Haarclassifier Detection makes use of native libraries which are currently only available for Microsoft Windows. However, the source code of these libraries is platform independent, so if you would like to compile them on your favourite operating system please contact me.</p>";
	}

	public String getCopyrightNotes() {
		InputStream is = OpenCVDetection.class.getResourceAsStream("license.txt");
		String notes = null;
		try {
			notes = Utilities.inputStreamToString(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return notes;
	}

	/* (non-Javadoc)
	 * @see de.offis.faint.plugins.IPlugin#getSettingsPanel()
	 */
	public JPanel getSettingsPanel() {
		if (settingsPanel == null)
			settingsPanel = new OpenCVSettingsPanel(this);
		return settingsPanel;
	}
	
	/**
	 * Method that reloads the native libraries on deserialization.
	 * 
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
		in.defaultReadObject();
		File dataFolder = new File(MainController.getInstance().getDataDir().getAbsoluteFile() + File.separator + SUBFOLDER);
		loadLibraries(dataFolder);
	}
	
//	/*
//	 * For testing purpose
//	 */
//	public static void main(String[] args){
//		OpenCVDetection detection = new OpenCVDetection();
//		Region[] regions = detection.detectFaces("C:\\test.jpg");
//		for (Region region : regions){
//			System.out.println(region);
//		}
//	}
	
}
