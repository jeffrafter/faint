/**
 * 
 */
package de.offis.faint.detection.plugins.haar;

/**
 * Port of C struct as found in cvtypes.h
 * 
 * typedef struct CvHaarFeature
 * {
 *     int  tilted;
 *     struct
 *     {
 *         CvRect r;
 *         float weight;
 *     } rect[CV_HAAR_FEATURE_MAX];
 * }
 * CvHaarFeature;
 * 
 * @author maltech
 *
 */
public class CvHaarFeature {
	
	
	boolean tilted;
	Rect[] rects;
}

class Rect {
	
	public Rect(int x, int y, int w, int h, float weight){
		r = new CvRect(x,y,w,h);
		this.weight = weight;
	}
	
	CvRect r;
	float weight;		
}
