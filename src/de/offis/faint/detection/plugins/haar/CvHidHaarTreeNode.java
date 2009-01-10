/**
 * 
 */
package de.offis.faint.detection.plugins.haar;

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
class CvHidHaarTreeNode
{
    CvHidHaarFeature feature;
    float threshold;
    int left;
    int right;
   
}