/**
 * 
 */
package de.offis.faint.detection.plugins.haar;

import java.io.Serializable;

/**
 * Port of C struct as found in cvhaar.h
 *  
 * typedef struct CvHidHaarTreeNode
 * {
 *    CvHidHaarFeature feature;
 *    float threshold;
 *    int left;
 *    int right;
 * }
 * CvHidHaarTreeNode;
 * 
 * @author maltech
 *
 */
class CvHidHaarTreeNode implements Serializable
{
	private static final long serialVersionUID = -1711354557642281842L;
	
	CvHidHaarFeature feature;
    float threshold;
    int left;
    int right;
   
}