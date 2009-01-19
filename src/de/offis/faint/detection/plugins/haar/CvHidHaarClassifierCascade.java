package de.offis.faint.detection.plugins.haar;

import java.io.Serializable;
import java.awt.Point;

/**
 * Port of C struct as found in cvhaar.h
 *  
 * struct CvHidHaarClassifierCascade
 * {
 *    int  count;
 *    int  is_stump_based;
 *    int  has_tilted_features;
 *    int  is_tree;
 *    double inv_window_area;
 *    CvMat sum, sqsum, tilted;
 *    CvHidHaarStageClassifier* stage_classifier;
 *    sqsumtype *pq0, *pq1, *pq2, *pq3;
 *    sumtype *p0, *p1, *p2, *p3;
 *
 *    void** ipp_stages;
 * };
 *
 * @author maltech
 *
 */
public	class CvHidHaarClassifierCascade implements Serializable {

	private static final long serialVersionUID = 6879630426484113632L;
	
	int count;
	boolean  isStumpBased;
    boolean  hasTiltedFeatures;
    boolean  isTree;
    
    double invWindowArea;
    CvHidHaarStageClassifier stageClassifiers[];
    
    int[][] sum;
    long[][] sqsum;
    int[][] tilted; //cvMAt
    Point pq0= new Point();
    Point pq1= new Point();
    Point pq2= new Point();
    Point pq3= new Point(); //sqsumtype
    Point p0 = new Point();
    Point p1= new Point();
    Point p2= new Point();
    Point p3= new Point(); //sumtype





    public CvHidHaarClassifierCascade(int count) {
		stageClassifiers = new CvHidHaarStageClassifier[count];
	}

	public void setCount(int count) {
		this.count = count;
	}

//    void** ipp_stages;
}
