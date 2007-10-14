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

package de.offis.faint.recognition.plugins.eigenfaces;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.JPanel;

import de.offis.faint.controller.MainController;
import de.offis.faint.global.Constants;
import de.offis.faint.global.Utilities;
import de.offis.faint.global.Utilities.SortableContainer;
import de.offis.faint.gui.tools.InfoDialog;
import de.offis.faint.interfaces.IRecognitionPlugin;
import de.offis.faint.interfaces.ISwingCustomizable;
import de.offis.faint.model.FaceDatabase;
import de.offis.faint.model.Region;

/**
 * @author maltech
 *
 */
public class EigenfaceRecognition implements IRecognitionPlugin, ISwingCustomizable {

	// Constants
	private static final long serialVersionUID = 4547532707099091006L;
	protected final static int VECTORLENGTH = Constants.FACE_THUMBNAIL_SIZE.height * Constants.FACE_THUMBNAIL_SIZE.width;
	
	// Settings
	protected Integer maxEigenfaces = 40;
	protected Integer rebuildFaceSpace = null;
	protected boolean mirrorFaces = true;
	protected boolean showHitsDialog = true;
	
	// Data
	private byte[] averageFace = null;
	private ArrayList<double[]> eigenFaces = null;
	protected int lastNumberOfTrainingImages = 0;
	
	// Transient elements
	protected transient boolean updateIsRunning = false;
	private transient EigenfaceBuilder eigenfaceBuilder = null;
	private transient EigenfaceSettingsPanel settingsPanel = null;
	private transient NearestHitsDialog hitsDialog = null;
	
	
	public synchronized HashMap<String, Integer> getRecognitionPoints(Region region) {
		
		// Ensure an instance of EigenfaceBuilder is present
		if (eigenfaceBuilder == null)
			eigenfaceBuilder = new EigenfaceBuilder(this);
		
		// Prepare first set of Eigenfaces (in same thread)
		if (averageFace == null || eigenFaces == null){
			eigenfaceBuilder.updateEigenfaces();
			if (averageFace == null){
				new InfoDialog(null, "<html>Not enough training images availble for EigenfaceRecognition.<br>Please classify the first faces manually!</html>");
				return new HashMap<String, Integer>();
			}
		}
		
		FaceDatabase db = MainController.getInstance().getFaceDB();
		String[] names = db.getExistingAnnotations();
		
		BufferedImage unknownFaceImage = region.toThumbnail(Constants.FACE_THUMBNAIL_SIZE);

		byte[] unknownFace = Utilities.bufferedImageToIntensityArray(unknownFaceImage);
		double[] unknownFaceWeight = this.getWeightForImage(unknownFace);

		// Mirrored region may increase recognition performance
		double[] unknownMirroredFaceWeight = null;
		if (this.mirrorFaces){
			byte[] mirroredFace = new byte[unknownFace.length];
			for (int i = 0; i < Constants.FACE_THUMBNAIL_SIZE.height; i++){
				for (int j = 0; j < Constants.FACE_THUMBNAIL_SIZE.width; j++){
					int elem = i * Constants.FACE_THUMBNAIL_SIZE.width + j;
					mirroredFace[elem] = unknownFace[(i+1) * Constants.FACE_THUMBNAIL_SIZE.width - j - 1];
				}			
			}
			unknownMirroredFaceWeight = this.getWeightForImage(mirroredFace);
		}

		HashMap<String, Integer> result = new HashMap<String, Integer>(	names.length);
		ArrayList<SortableContainer<Region>> bestHits = new ArrayList<SortableContainer<Region>>();
		for (String name : names) {
			Region image = null;
			Region[] regionsForName = db.getRegionsForFace(name);

			if (regionsForName != null) {
				double minDist = Double.MAX_VALUE;
				
				for (int i = 0; i < regionsForName.length; i++) {
					
					if (regionsForName[i] != null && regionsForName[i] != region && regionsForName[i].isUsedForTraining()) {

						byte[] knownFace = Utilities
								.bufferedImageToIntensityArray(regionsForName[i]
										.toThumbnail(Constants.FACE_THUMBNAIL_SIZE));

						double[] knownFaceWeight = this.getWeightForImage(knownFace);

						double distance = this.getDistanceBetweenWeights(knownFaceWeight, unknownFaceWeight);
						
						if (unknownMirroredFaceWeight != null) {
							distance = Math.min(distance, getDistanceBetweenWeights(knownFaceWeight, unknownMirroredFaceWeight));
						}
						
						if (distance < minDist) {
							minDist = distance;
							image = regionsForName[i];
						}
					}
				}
				
				// Map distance to interval [0, 100]
				Integer points = (int) Math.max(0, 100 - Math.round(minDist * 0.2f));
				result.put(name, points);
				
				if (image != null && points != 0){
					bestHits.add(new SortableContainer<Region>(image, points));
				}
			}
		}
		
		if (showHitsDialog){
//		if (showHitsDialog && bestHits.size() > 0){
			if (hitsDialog == null){
				hitsDialog = new NearestHitsDialog();
			}
						
			BufferedImage original =
				Utilities.intensityArrayToBufferedImage(unknownFace, Constants.FACE_THUMBNAIL_SIZE);
			
			BufferedImage reconstruction =
				Utilities.intensityArrayToBufferedImage(getFaceReconstruction(unknownFaceWeight), Constants.FACE_THUMBNAIL_SIZE);
			
			Collections.sort(bestHits);
			BufferedImage[] nearestImages = new BufferedImage[Math.min(9, bestHits.size())];
			for (int i = 0; i < nearestImages.length; i++){
				nearestImages[i] = bestHits.get(i).getObject().toThumbnail(Constants.FACE_THUMBNAIL_SIZE);
			}
			
			hitsDialog.show(original, reconstruction, nearestImages);
		}
		
		// Prepare upcoming set of Eigenfaces in second thread.
		eigenfaceBuilder.updateEigenfacesInBackground();

		return result;
	}
	
	protected double[] getWeightForImage(byte[] image){
		
		short[] distanceFromAverageFace = new short[VECTORLENGTH];
		for (int i = 0; i< distanceFromAverageFace.length; i++){
			distanceFromAverageFace[i] = (short)(((short) image[i] & 0xFF) - ((short) this.averageFace[i] & 0xFF));
		}
		
		double[] result =  new double[this.eigenFaces.size()];
		for (int i = 0; i < result.length; i++){
			result[i]=0;
			for (int j = 0; j<this.eigenFaces.get(i).length;j++){
				result[i]+= this.eigenFaces.get(i)[j] * distanceFromAverageFace[j];
			}
		}
		return result;
	}
	
	protected double getDistanceBetweenWeights(double[] weightA, double[] weightB){
		double result = 0;
		for(int i = 0; i<weightA.length; i++){
			result+= Math.abs(weightA[i] - weightB[i]);			
		}
		return result/weightA.length;
	}
	
	protected byte[] getAverageFace() {
		return averageFace;
	}

	protected ArrayList<double[]> getEigenFaces() {
		return eigenFaces;
	}
	
	protected byte[] getFaceReconstruction(double[] weight){
		double [] temp = new double[VECTORLENGTH];
		
		for (int i = 0; i< weight.length; i++){
			for (int j = 0; j < temp.length; j++)
				temp[j] += weight[i] * this.eigenFaces.get(i)[j];

		}
		
		for (int j = 0; j < temp.length; j++)
		{	
			temp[j] += averageFace[j] & 0xff;
		}
		

		byte[] image = new byte[VECTORLENGTH];
		for (int i = 0; i < image.length; i++){
			int value = (int) Math.max(0, Math.min(Math.round(temp[i]), 255));
			image[i] = (byte)(value & 0xff);
		}
		return image;
	}
	
	/**
	 * Called by EigenfaceBuilder.
	 * 
	 * @param averageFace
	 * @param eigenFaces
	 * @param numTrainingImages 
	 */
	protected synchronized void updateData(byte[] averageFace, ArrayList<double[]> eigenFaces, int numTrainingImages){
		this.lastNumberOfTrainingImages = numTrainingImages;
		this.averageFace = averageFace;
		this.eigenFaces = eigenFaces;
	}
	
	protected  void updateView(){
		if (settingsPanel != null)
			settingsPanel.updateFromModel();
	}
	
	public String toString(){
		return getName();
	}

	/* (non-Javadoc)
	 * @see de.offis.faint.plugins.IPlugin#getName()
	 */
	public String getName() {
		return "Eigenface Recognition";
	}
	
	/* (non-Javadoc)
	 * @see de.offis.faint.plugins.IPlugin#getRequirements()
	 */
	public String getDescription() {
		return "<p>This Plugin is an implementation of the Eigenfaces approach implemented entirely in Java. Note: The recognition performance relies heavyly on the training sets of faces.</p>";
	}

	public String getCopyrightNotes() {
		return "<p>Malte Mathiszig 2007. Functions to calculate Eigenvalues of a matrix as found in Java Matrix Package (JAMA) are used. JAMA is a cooperative product of The MathWorks and the National Institute of Standards and Technology (NIST).</p>";
	}

	/* (non-Javadoc)
	 * @see de.offis.faint.plugins.IPlugin#getSettingsPanel()
	 */
	public JPanel getSettingsPanel() {
		if (settingsPanel == null) settingsPanel = new EigenfaceSettingsPanel(this);
		return settingsPanel;
	}
}
