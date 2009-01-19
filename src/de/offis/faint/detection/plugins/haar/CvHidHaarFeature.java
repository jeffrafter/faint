/**
 * 
 */
package de.offis.faint.detection.plugins.haar;

import java.io.Serializable;
import java.awt.Point;


/**
 * Port of C struct as found in cvhaar.h
 * 
 * typedef struct CvHidHaarFeature
 * {
 *     struct
 *     {
 *         sumtype *p0, *p1, *p2, *p3;
 *         float weight;
 *     }
 *     rect[CV_HAAR_FEATURE_MAX];
 * }
 * CvHidHaarFeature;
 * 
 * @author maltech
 */
class CvHidHaarFeature implements Serializable
{
	
	private static final long serialVersionUID = 6241676843153232764L;

	/**
	 * @param length
	 */
	public CvHidHaarFeature(int numRects) {
		rect = new HidRect[numRects];
		for (int i = 0; i< numRects; i++) {
			rect[i] = new HidRect();
		}
	}

	class HidRect implements Serializable {

		private static final long serialVersionUID = 7963937018284581291L;
		
		Point p0 = new Point();
        Point p1 = new Point();
        Point p2 = new Point();
        Point p3 = new Point();
        float weight;		
	}
	
	HidRect[] rect;
}
