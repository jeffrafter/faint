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

import java.util.*;

class OpenCV {
  native Region[] detectFaces(String image);

  static {
    System.loadLibrary("opencv");
  }
  
  // For testing
  public static void main(String[] args) {
    OpenCV detector = new OpenCV();
    Region[] faces = detector.detectFaces(args[0]);
    if (faces != null) {
      for (int i=0; i<faces.length; i++) {
        Region region = faces[i];
        System.out.println("Found region: " + region.toString());      
      }     
    }
  }
}