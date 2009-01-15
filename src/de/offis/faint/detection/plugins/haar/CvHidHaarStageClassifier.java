/**
 * 
 */
package de.offis.faint.detection.plugins.haar;

import java.io.Serializable;

/**
 * Port of C struct as found in cvhaar.h
 * 
 * typedef struct CvHidHaarStageClassifier
 * {
 *    int  count;
 *    float threshold;
 *    CvHidHaarClassifier* classifier;
 *    int two_rects;
 *
 *    struct CvHidHaarStageClassifier* next;
 *    struct CvHidHaarStageClassifier* child;
 *    struct CvHidHaarStageClassifier* parent;
 * }
 * CvHidHaarStageClassifier;
 * 
 * @author maltech
 *
 */
class CvHidHaarStageClassifier implements Serializable
{

	private static final long serialVersionUID = 2366260035168645882L;

	public CvHidHaarStageClassifier(int count) {
		classifiers = new CvHidHaarClassifier[count];				
	}
	
    float threshold;
    CvHidHaarClassifier classifiers[];
    boolean twoRects = true;

    CvHidHaarStageClassifier next;
    CvHidHaarStageClassifier child;
    CvHidHaarStageClassifier parent;
    
    int getCount(){
    	return classifiers.length;
    }
}