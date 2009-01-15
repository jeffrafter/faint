/**
 * 
 */
package de.offis.faint.detection.plugins.haar;

import java.io.Serializable;

/**
 * Port of C struct as found in cvtypes.h
 * 
 * typedef struct CvHaarStageClassifier
 * {
 *    int  count;
 *    float threshold;
 *    CvHaarClassifier* classifier;
 *
 *    int next;
 *    int child;
 *    int parent;
 * }
 * CvHaarStageClassifier;
 * 
 * @author maltech
 *
 */
public class CvHaarStageClassifier implements Serializable {
	
	private static final long serialVersionUID = -9012180117642208785L;
	
	float threshold;
	CvHaarClassifier[] classifiers;
	
	int next;
	int child;
	int parent;
	
	int getCount(){
		return classifiers.length;
	}

}
