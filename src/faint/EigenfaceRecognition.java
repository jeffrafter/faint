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

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class EigenfaceRecognition {

  // Constants
  private static final long serialVersionUID = 4547532707099091006L;
  protected final static int VECTORLENGTH = Constants.FACE_THUMBNAIL_SIZE.height * Constants.FACE_THUMBNAIL_SIZE.width;
  
  // Settings
  protected Integer maxEigenfaces = 40;
  protected Integer rebuildFaceSpace = null;
  protected boolean mirrorFaces = true;
  
  // Data
  private byte[] averageFace = null;
  private ArrayList<double[]> eigenFaces = null;
  protected int lastNumberOfTrainingImages = 0;

  private FaceDatabase db;
  private String path;
  
  public EigenfaceRecognition(FaceDatabase db, String path) {
    this.db = db;
    this.path = path;
  }
  
  public HashMap<String, Integer> getRecognitionPoints(Region region) {
    
    // Prepare first set of Eigenfaces
    if (averageFace == null || eigenFaces == null){
      updateEigenfaces();
      if (averageFace == null){
        System.err.println("Not enough training images availble for recognition.");
        return new HashMap<String, Integer>();
      }
    }
    
    String[] names = db.getExistingAnnotations();
    
    BufferedImage unknownFaceImage = region.toThumbnail(this.path, Constants.FACE_THUMBNAIL_SIZE);
    if (unknownFaceImage == null) {
      return new HashMap<String, Integer>();    
    }

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

    HashMap<String, Integer> result = new HashMap<String, Integer>(  names.length);
    ArrayList<Utilities.SortableContainer<Region>> bestHits = new ArrayList<Utilities.SortableContainer<Region>>();
    for (String name : names) {
      Region image = null;
      Region[] regionsForName = db.getRegionsForFace(name);

      if (regionsForName != null) {
        double minDist = Double.MAX_VALUE;
        
        for (int i = 0; i < regionsForName.length; i++) {
          
          if (regionsForName[i] != null && regionsForName[i] != region && regionsForName[i].isUsedForTraining()) {

            byte[] knownFace = Utilities
                .bufferedImageToIntensityArray(regionsForName[i]
                    .toThumbnail(this.path, Constants.FACE_THUMBNAIL_SIZE));

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
          bestHits.add(new Utilities.SortableContainer<Region>(image, points));
        }
      }
    }
    
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

  /**
   * Returns the average of the differences of all the values in the two given arrays.
   *
   * @param weightA The first array
   * @param weightB The second array
   * @return The average of the differences of the arrays.
   */
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
  
  /**
   * Calculates a new set of Eigenfaces and then replaces the old set.
   *
   */
  public void updateEigenfaces(){
    
    Region[] knownFaces = db.getTrainingImages();
    int numTrainingImages = knownFaces.length;

    Integer step = this.rebuildFaceSpace;
    if ((step == null && Math.abs(numTrainingImages - this.lastNumberOfTrainingImages) > 0) ||
      (step != null && Math.abs(numTrainingImages - this.lastNumberOfTrainingImages) > step))
    {
      if (knownFaces.length > 0) {        
        double factor = 1.0/knownFaces.length;
        byte[] averageFace = new byte[this.VECTORLENGTH];
        double[] tempAverageFace = new double[this.VECTORLENGTH];
        byte[][] faceVectors = new byte[knownFaces.length][];
        
        // convert all thumbnails to intensity images and calculate average face
        for(int r = 0; r < knownFaces.length; r++){
          byte[] intensityImage = Utilities.bufferedImageToIntensityArray(knownFaces[r].toThumbnail(this.path, Constants.FACE_THUMBNAIL_SIZE));
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
        short[][] distances = new short[faceVectors.length][this.VECTORLENGTH];
        for (int i = 0; i<faceVectors.length; i++){
          for(int j = 0; j < this.VECTORLENGTH; j++){
            distances[i][j] = (short) ((short)(faceVectors[i][j]  & 0xFF) - (short)(averageFace[j]  & 0xFF));
          }
        }
        
        // build up covariance matrix for Eigenvector calculation
        CovarianceMatrix matrix = new CovarianceMatrix(distances);
        
        // calculate and store Eigenfaces
        ArrayList<double[]> eigenFaces = new ArrayList<double[]>();
        int numEigenfaces = distances.length;
        if (this.maxEigenfaces != null && this.maxEigenfaces < numEigenfaces)
          numEigenfaces = this.maxEigenfaces;
        
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
        
        this.lastNumberOfTrainingImages = numTrainingImages;
        this.averageFace = averageFace;
        this.eigenFaces = eigenFaces;
      }
    }
  }
}
