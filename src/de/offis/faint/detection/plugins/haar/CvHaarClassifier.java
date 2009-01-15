/**
 * 
 */
package de.offis.faint.detection.plugins.haar;

import java.io.Serializable;

/**
 * Port of C struct as found in cvtypes.h
 * 
 * typedef struct CvHaarClassifier
 * {
 *     int count;
 *     CvHaarFeature* haar_feature;
 *     float* threshold;
 *     int* left;
 *     int* right;
 *     float* alpha;
 * }
 * CvHaarClassifier;
 * 
 * @author maltech
 *
 */
public class CvHaarClassifier implements Serializable {
	
	private static final long serialVersionUID = 4737349422121411820L;
	
	CvHaarFeature[] haarFeatures;
	float thresholds[];
	
	int left[];
	int right[];
	float alpha[];
	
	public CvHaarClassifier(int length) {
		haarFeatures = new CvHaarFeature[length];
		thresholds = new float[length];
		left = new int[length];
		right = new int[length];
		alpha = new float[length + 1];
	}

	int getCount(){
		return haarFeatures.length;
	}
}