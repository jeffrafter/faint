/**
 * 
 */
package de.offis.faint.detection.plugins.haar;

import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author maltech
 *
 */
public class XMLParser {
	
	static final String ICV_HAAR_STAGES_NAME = "stages";
	static final String ICV_HAAR_SIZE_NAME = "size";
	static final String ICV_HAAR_TREES_NAME = "trees";
	static final String ICV_HAAR_FEATURE_NAME = "feature";
	static final String ICV_HAAR_RECTS_NAME = "rects";
	static final String ICV_HAAR_TILTED_NAME = "tilted";
	static final String ICV_HAAR_THRESHOLD_NAME = "threshold";
	static final String ICV_HAAR_LEFT_NODE_NAME = "left_node";
	static final String ICV_HAAR_LEFT_VAL_NAME = "left_val";
	static final String ICV_HAAR_RIGHT_NODE_NAME = "right_node";
	static final String ICV_HAAR_RIGHT_VAL_NAME = "right_val";
	static final String ICV_HAAR_STAGE_THRESHOLD_NAME = "stage_threshold";
	static final String ICV_HAAR_PARENT_NAME = "parent";
	static final String ICV_HAAR_NEXT_NAME = "next";
	
	static final String EMPTY_TAG = "_";
	
	/**
	 * Equivalent to function icvReadHaarClassifier() in cvhaar.cpp
	 * 
	 * 
	 * @param stream
	 * @return
	 */
	public static CvHaarClassifierCascade parseFromStream(InputStream stream) {
		
		Document doc = null;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		CvHaarClassifierCascade cascade = new CvHaarClassifierCascade();
		
		// read size
		String[] size = doc.getElementsByTagName(ICV_HAAR_SIZE_NAME).item(0).getTextContent().split(" ");
		cascade.origWindowSize = new Point(Integer.valueOf(size[0]), Integer.valueOf(size[1]));
		cascade.realWindowSize = new Point();
		
		// read stages
		ArrayList<Node> stageNodes = getSpecificChildNodes(doc.getElementsByTagName(ICV_HAAR_STAGES_NAME).item(0), EMPTY_TAG);
		
		cascade.stageClassifiers = new CvHaarStageClassifier[stageNodes.size()];		
		for (int i = 0; i< stageNodes.size(); i++) {
			
			CvHaarStageClassifier stageClassifier = new CvHaarStageClassifier();
			cascade.stageClassifiers[i] = stageClassifier;

			NodeList stageTags = stageNodes.get(i).getChildNodes();
			for (int t = 0; t < stageTags.getLength(); t++) {
				Node tag = stageTags.item(t);
				
				if (tag.getNodeName().equals(ICV_HAAR_TREES_NAME)) {
					
					// read trees for stage
					ArrayList<Node> trees = getSpecificChildNodes(tag, EMPTY_TAG);
					
					stageClassifier.classifiers = new CvHaarClassifier[trees.size()];
					for (int j = 0; j < trees.size(); j++) {
												
						ArrayList<Node> treeNodes = getSpecificChildNodes(trees.get(j), EMPTY_TAG);
						CvHaarClassifier classifier = new CvHaarClassifier(treeNodes.size());
						stageClassifier.classifiers[j] = classifier;
						
						// read nodes of each tree
						for (int k = 0,  lastIdx = 0; k < classifier.getCount(); k++) {

							CvHaarFeature feature = new CvHaarFeature();
							classifier.haarFeatures[k] = feature;

							NodeList nodeData = treeNodes.get(k).getChildNodes();
							
							for (int n = 0; n < nodeData.getLength(); n++) {
								
								Node nodeTag = nodeData.item(n);
								
								// read feature for node
								if (nodeTag.getNodeName().equals(ICV_HAAR_FEATURE_NAME)) {
									NodeList featureData = nodeData.item(n).getChildNodes();
																		
									for (int f = 0; f < featureData.getLength(); f++) {
										Node featureTag = featureData.item(f);
										
										if (featureTag.getNodeName().equals(ICV_HAAR_RECTS_NAME)) {
											
											ArrayList<Node> rects = getSpecificChildNodes(featureTag,EMPTY_TAG);

											feature.rects = new Rect[rects.size()];
											
											// read rectangles for feature
											for (int r = 0; r < rects.size(); r++) {
												
												String[] rectData = rects.get(r).getTextContent().split(" ");
												feature.rects[r] = new Rect( Integer.valueOf(rectData[0]),
														                     Integer.valueOf(rectData[1]),
														                     Integer.valueOf(rectData[2]),
														                     Integer.valueOf(rectData[3]),
														                     Float.valueOf(rectData[4]) );
											}											
										}
										else if (featureTag.getNodeName().equals(ICV_HAAR_TILTED_NAME)) {
											feature.tilted = !featureTag.getTextContent().equals("0");
										}
									}
								}
								else if (nodeTag.getNodeName().equals(ICV_HAAR_THRESHOLD_NAME)) {
									classifier.thresholds[k] = Float.valueOf(nodeTag.getTextContent());
								}
								else if (nodeTag.getNodeName().equals(ICV_HAAR_LEFT_NODE_NAME)) {
									classifier.left[k] = Integer.valueOf(nodeTag.getTextContent());
								}
								else if (nodeTag.getNodeName().equals(ICV_HAAR_LEFT_VAL_NAME)) {
									classifier.left[k] = - lastIdx;
									classifier.alpha[lastIdx++] = Float.valueOf(nodeTag.getTextContent());
								}
								else if (nodeTag.getNodeName().equals(ICV_HAAR_RIGHT_NODE_NAME)) {
									classifier.right[k] = Integer.valueOf(nodeTag.getTextContent());
								}
								else if (nodeTag.getNodeName().equals(ICV_HAAR_RIGHT_VAL_NAME)) {
									classifier.right[k] = - lastIdx;
									classifier.alpha[lastIdx++] = Float.valueOf(nodeTag.getTextContent());
								}
							}
						}							
					}
				}
				else if (tag.getNodeName().equals(ICV_HAAR_STAGE_THRESHOLD_NAME)) {
					stageClassifier.threshold = Float.valueOf(tag.getTextContent());
				}
				else if (tag.getNodeName().equals(ICV_HAAR_PARENT_NAME)) {
					stageClassifier.parent = Integer.valueOf(tag.getTextContent());
				}
				else if (tag.getNodeName().equals(ICV_HAAR_NEXT_NAME)) {
					stageClassifier.next = Integer.valueOf(tag.getTextContent());
				}
			}
			stageClassifier.child = -1;
			if (stageClassifier.parent != -1 && cascade.stageClassifiers[stageClassifier.parent].child == -1) {
				cascade.stageClassifiers[stageClassifier.parent].child = i;				
			}
		}
		
		return cascade;
	}
	
	private static ArrayList<Node> getSpecificChildNodes(Node node, String nodeName) {
	
		NodeList tempNodes = node.getChildNodes();
		ArrayList<Node> result = new ArrayList<Node>();
		for (int i = 0; i< tempNodes.getLength(); i++) {
			if (tempNodes.item(i).getNodeName().equals(nodeName))
				result.add(tempNodes.item(i));
		}
		return result;
	}

}
