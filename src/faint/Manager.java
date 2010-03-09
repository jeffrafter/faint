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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Manager {

  // Dataholding
  private FaceDatabase faceDB;
  private String path;
  
  // Private constructor    
  private Manager(String path){
    // try to load database from disk
    try {
      this.path = path;
      faceDB = FaceDatabase.recoverFromDisk(path); 
    }catch(IOException ex){
      faceDB = new FaceDatabase();
    }    
  }
  
  /**
   * This method uses the active Plugin of the Detection Hot Spot to
   * detect faces in an image.
   * 
   * @param image  The image that will be analyzed.
   * @param storeInDB   Determines if possible faces should be send to the database directly.
   * @return  Array of Region elements, that are probably faces or null, if no faces were found.   
  */
  public Region[] detectFaces(File image, boolean storeInDB){    
    OpenCV opencv = new OpenCV();
    Region[] possibleFaces = opencv.detectFaces(image.getAbsolutePath());
    
    // Store Regions in DB
    if (possibleFaces != null && storeInDB) {
      for (int i = 0; i <possibleFaces.length; i++){
        faceDB.put(possibleFaces[i], Constants.UNKNOWN_FACE, this.path);
      }
      try {
        faceDB.writeToDisk(path);
      } catch (IOException io) {
        System.err.println("Could not write face database.");
        return null;
      }
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
    EigenfaceRecognition recognition = new EigenfaceRecognition(faceDB, path);
    HashMap<String, Integer> result = recognition.getRecognitionPoints(face);
    
    // Run a simple context filter (i.e., if the weighted person appears in 
    // another region in the same image then assume this is not them. This
    // works great except for pictures with mirrors, background photos, and
    // collages)
    Region[] knownRegions = faceDB.getRegionsForImage(face.getImage());    
    for (String person : result.keySet()){
      for (Region regionOnImage : knownRegions){
        if (!face.equals(regionOnImage) && faceDB.getAnnotation(regionOnImage).equals(person))
          result.put(person, 0);          
      }
    }
    return result;
  }

  public void trainFace(Region face, String annotation) {
    faceDB.put(face, annotation, this.path);
    try {
      faceDB.writeToDisk(path);
    } catch (IOException io) {
      System.err.println("Could not write face database.");
      return;
    }
  }
    
  /* 
   * Primary cli entry points. This code is seriously ugly, but I was trying to
   * to keep this section as straight-forward as possible.
   *
   */
  public static void main(String[] args) {
    String usage =  "Usage: {detect|train|recognize} </path/to/image> [options]";
    if ("detect".equals(args[0])) {
      detect(args);
    } else if ("train".equals(args[0])) {
      train(args);
    } else if ("recognize".equals(args[0])) {
      recognize(args);   
    } else {
      System.out.println(usage);
      return;
    }    
  }
    
  public static void detect(String[] args) {
    if (args.length != 3) {
      System.out.println("detect <path/to/data> <path/to/image>");
      return;
    }
    String data = args[1];
    String image = args[2];
    Manager manager = new Manager(data);
    manager.detectFaces(new File(image), false);  
  }

  public static void train(String[] args) {
    if (args.length != 8) {
      System.out.println("train <path/to/data> <path/to/image> x y width height annotation");
      return;
    }
    String data = args[1];
    String image = args[2];
    int x = Integer.parseInt(args[3]);
    int y = Integer.parseInt(args[4]);
    int w = Integer.parseInt(args[5]);
    int h = Integer.parseInt(args[6]);
    String annotation = args[7];
    Region region = new Region(x, y, w, h, 0, image);
    region.setUsedForTraining(true);
    Manager manager = new Manager(data);
    manager.trainFace(region, annotation);        
  }  
  
  public static void recognize(String[] args) {
    if (args.length != 7) {
      System.out.println("recognize <path/to/data> <path/to/image> x y width height");
      return;
    }
    String data = args[1];
    String image = args[2];
    int x = Integer.parseInt(args[3]);
    int y = Integer.parseInt(args[4]);
    int w = Integer.parseInt(args[5]);
    int h = Integer.parseInt(args[6]);
    Region region = new Region(x, y, w, h, 0, image);
    // This might be double caching, need to think about it
    String cached = region.cache(data);
    System.out.println(cached);
    Manager manager = new Manager(data);
    HashMap<String, Integer> result = manager.recognizeFace(region); 
    Iterator iterator = result.keySet().iterator();           
    while (iterator.hasNext()) {  
      String key = iterator.next().toString();  
      String value = result.get(key).toString();           
      System.out.println(key + " " + value);  
    }   
  }
}
