/**
 * 
 */
package de.offis.faint.detection.plugins.haar;

/**
 * Port of C struct as found in cvhaar.h
 *  
 * typedef struct CvHidHaarClassifier
 * {
 *    int count;
 *    //CvHaarFeature* orig_feature;
 *    CvHidHaarTreeNode* node;
 *    float* alpha;
 * }
 * CvHidHaarClassifier;
 * 
 * @author maltech
 *
 */
class CvHidHaarClassifier
{
    /**
	 * @param count2
	 */
	public CvHidHaarClassifier(int count) {
		node = new CvHidHaarTreeNode[count];
	}

    //CvHaarFeature* orig_feature;
    CvHidHaarTreeNode[] node;
    float alpha[];

	public int getCount() {
		return node.length;
	}
}