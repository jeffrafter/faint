/**
 * 
 */
package de.offis.faint.detection.plugins.haar;

import java.io.Serializable;

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
public class CvHaarFeature implements Serializable {
	
	private static final long serialVersionUID = -5770098279536388791L;
	
	boolean tilted;
	Rect[] rects;
}

class Rect implements Serializable {
	
	private static final long serialVersionUID = -7455465814808638015L;
	
	public Rect(int x, int y, int w, int h, float weight){
		r = new CvRect(x,y,w,h);
		this.weight = weight;
	}
	
	CvRect r;
	float weight;		
}
