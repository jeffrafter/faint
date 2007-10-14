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

import java.util.ArrayList;

import javax.swing.SwingWorker;

import de.offis.faint.controller.MainController;
import de.offis.faint.global.Constants;
import de.offis.faint.global.Utilities;
import de.offis.faint.model.Region;

/**
 * @author maltech
 *
 */
public class EigenfaceBuilder {
	
	private EigenfaceRecognition eigenfaceRecognition;

	/**
	 * Constructor.
	 * 
	 * @param eigenfaceRecognition
	 */
	public EigenfaceBuilder(EigenfaceRecognition eigenfaceRecognition){
		this.eigenfaceRecognition = eigenfaceRecognition;
	}

	/**
	 * Calculates a new set of Eigenfaces and then replaces the old set.
	 *
	 */
	public void updateEigenfaces(){
		
		Region[] knownFaces = MainController.getInstance().getFaceDB().getTrainingImages();
		int numTrainingImages = knownFaces.length;
		
		int lastNumTrainingImages = eigenfaceRecognition.lastNumberOfTrainingImages;
		Integer step = eigenfaceRecognition.rebuildFaceSpace;
		if ((step == null && Math.abs(numTrainingImages - lastNumTrainingImages) > 0) ||
			(step != null && Math.abs(numTrainingImages - lastNumTrainingImages) > step))
		{
			if (knownFaces.length > 0) {
				
				eigenfaceRecognition.updateIsRunning = true;
				eigenfaceRecognition.updateView();
				
				double factor = 1.0/knownFaces.length;
				byte[] averageFace = new byte[EigenfaceRecognition.VECTORLENGTH];
				double[] tempAverageFace = new double[EigenfaceRecognition.VECTORLENGTH];
				byte[][] faceVectors = new byte[knownFaces.length][];
				
				// convert all thumbnails to intensity images and calculate average face
				for(int r = 0; r < knownFaces.length; r++){
					byte[] intensityImage = Utilities.bufferedImageToIntensityArray(knownFaces[r].toThumbnail(Constants.FACE_THUMBNAIL_SIZE));
					for (int i = 0; i< tempAverageFace.length; i++){
						tempAverageFace[i] += ((double) (intensityImage[i] & 0xFF)) * factor;
					}
					faceVectors[r] = intensityImage;
				}
				
				// convert average face to byte array
				for (int i = 0; i < tempAverageFace.length; i++){
					averageFace[i] = (byte) (Math.round(tempAverageFace[i]));
				}

				// calculate distances of all intensity images to average face
				short[][] distances = new short[faceVectors.length][EigenfaceRecognition.VECTORLENGTH];
				for (int i = 0; i<faceVectors.length; i++){
					for(int j = 0; j < EigenfaceRecognition.VECTORLENGTH; j++){
						distances[i][j] = (short) ((short)(faceVectors[i][j]  & 0xFF) - (short)(averageFace[j]  & 0xFF));
					}
				}
				
				// build up covariance matrix for Eigenvector calculation
				CovarianceMatrix matrix = new CovarianceMatrix(distances);
				
				// calculate and store Eigenfaces
				ArrayList<double[]> eigenFaces = new ArrayList<double[]>();
				int numEigenfaces = distances.length;
				if (eigenfaceRecognition.maxEigenfaces != null && eigenfaceRecognition.maxEigenfaces < numEigenfaces)
					numEigenfaces = eigenfaceRecognition.maxEigenfaces;
				
				for (int i = 0; i< numEigenfaces; i++){

					eigenFaces.add(matrix.getEigenVector(i));
					
					/*
					 * Umcomment this to view all eigenfaces in separate frames
					 *
					 * byte [] ef = Utilities.spreadGreyValues(eigenFaces.get(i));
					 * BufferedImage recoFaceImage = Utilities.intensityArrayToBufferedImage(ef, Constants.FACE_THUMBNAIL_SIZE);
					 * Utilities.showImageInFrame(recoFaceImage,i+"");
					 *
					 */
					 					
				}
				
				eigenfaceRecognition.updateIsRunning = false;
				eigenfaceRecognition.updateData(averageFace, eigenFaces, numTrainingImages);
				eigenfaceRecognition.updateView();
			}
		}
	}

	/**
	 * Initiates an update of the Eigenfaces in a second thread.
	 */
	public void updateEigenfacesInBackground() {
		(new BackgroundWorker()).execute();
	}
	
	class BackgroundWorker extends SwingWorker{

		/* (non-Javadoc)
		 * @see javax.swing.SwingWorker#doInBackground()
		 */
		@Override
		protected Object doInBackground() throws Exception {
			updateEigenfaces();
			return null;
		}
	}
}
