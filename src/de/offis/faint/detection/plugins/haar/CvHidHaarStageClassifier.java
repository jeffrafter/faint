/**
 * 
 */
package de.offis.faint.detection.plugins.haar;

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
class CvHidHaarStageClassifier
{

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