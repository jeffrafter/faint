/**
 * 
 */
package de.offis.faint.detection.plugins.haar;

import java.awt.Dimension;


/**
 * Port of C struct as found in cvtypes.h
 * 
 * typedef struct CvHaarClassifierCascade
 * {
 *    int  flags; // magic value
 *    int  count;
 *    CvSize orig_window_size;
 *    CvSize real_window_size;
 *    double scale;
 *    CvHaarStageClassifier* stage_classifier; 
 *    CvHidHaarClassifierCascade* hid_cascade;
 * }
 * CvHaarClassifierCascade;
 * 
 * @author maltech
 *
 */
public class CvHaarClassifierCascade {
	
	Point origWindowSize;
	Point realWindowSize;
	
	double scale;
	CvHaarStageClassifier[] stageClassifiers;
	CvHidHaarClassifierCascade hidCascade;
	
	int getCount(){
		return stageClassifiers.length;
	}

	
	static final float icv_stage_threshold_bias = 0.0001f;
	
	/**
	 * Derived from icvCreateHidHaarClassifierCascad() in cvhaar.cpp
	 */
	public void initHiddenCascade() {
		
		hidCascade = new CvHidHaarClassifierCascade(getCount());
		
		hidCascade.isStumpBased = true;
		hidCascade.isTree = false;
		
		// create hidden stages
		for (int i = 0; i<stageClassifiers.length; i++) {
			CvHaarStageClassifier stageClassifier = stageClassifiers[i];
			hidCascade.stageClassifiers[i] = new CvHidHaarStageClassifier(stageClassifier.getCount());
			hidCascade.stageClassifiers[i].threshold = stageClassifier.threshold - icv_stage_threshold_bias;
			
			if (stageClassifier.next != -1)
				hidCascade.isTree = true;
		}
		
		// link hidden stages and create hidden classifiers for each one
		for (int i = 0; i<stageClassifiers.length; i++) {
			CvHaarStageClassifier stageClassifier = stageClassifiers[i];
			CvHidHaarStageClassifier hidStageClassifier = hidCascade.stageClassifiers[i];
			
			if (stageClassifier.parent >= 0) {
				hidStageClassifier.parent = hidCascade.stageClassifiers[stageClassifier.parent];
				hidCascade.stageClassifiers[stageClassifier.parent].child = hidStageClassifier;
			}
			
			if (stageClassifier.next >= 0) {
				hidStageClassifier.next = hidCascade.stageClassifiers[stageClassifier.next];
			}
									
			for (int j = 0; j< stageClassifier.getCount(); j++) {
				CvHaarClassifier haarClassifier = stageClassifier.classifiers[j];
				
				CvHidHaarClassifier hidClassifier = new CvHidHaarClassifier(haarClassifier.getCount());
				hidStageClassifier.classifiers[j] = hidClassifier;
				hidClassifier.alpha = haarClassifier.alpha;
				
				// create nodes for all features of every hidden classifier
				for (int l = 0; l<hidClassifier.getCount(); l++) {
					CvHaarFeature feature = haarClassifier.haarFeatures[l];
					
					CvHidHaarTreeNode node = new CvHidHaarTreeNode();
					hidClassifier.node[l] = node;
					
					node.left = haarClassifier.left[l];
					node.right = haarClassifier.right[l];
					node.threshold = haarClassifier.thresholds[l];
					node.feature = new CvHidHaarFeature(feature.rects.length);
				}
			}
		}
	}
	
}
