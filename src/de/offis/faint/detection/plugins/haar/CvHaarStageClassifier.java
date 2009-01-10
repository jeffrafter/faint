/**
 * 
 */
package de.offis.faint.detection.plugins.haar;

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
public class CvHaarStageClassifier {
	
	float threshold;
	CvHaarClassifier[] classifiers;
	
	int next;
	int child;
	int parent;
	
	int getCount(){
		return classifiers.length;
	}

}
