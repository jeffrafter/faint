package de.offis.faint.detection.plugins.haar;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import de.offis.faint.controller.MainController;
import de.offis.faint.detection.plugins.haar.CvHidHaarFeature.HidRect;
import de.offis.faint.interfaces.IDetectionPlugin;
import de.offis.faint.interfaces.ISwingCustomizable;
import de.offis.faint.model.Region;

public class HaarClassifierDetection implements IDetectionPlugin , ISwingCustomizable {
	
	private static final long serialVersionUID = -6413328362450632598L;
	
	private float scaleFactor = 1.1f;
	private CvHaarClassifierCascade cascade;
	private String cascadeFile = BUILT_IN_CASCADES[0];
	
	static final String[] BUILT_IN_CASCADES = {
		"haarcascade_frontalface_default.bin",
//		"haarcascade_frontalface_alt.bin",
//		"haarcascade_frontalface_alt_tree.bin",
//		"haarcascade_frontalface_alt2.bin",
//		"haarcascade_profileface.bin"
	};
	
	private transient HaarClassifierSettingsPanel settingsPanel = null;

	public String getName() {
		return "HaarClassfier Detection (pure Java)";
	}
	
	public String getCopyrightNotes() {
		return "bla bla";
	}

	public String getDescription() {
		return "bla bla bla";
	}
	
	/* (non-Javadoc)
	 * @see de.offis.faint.interfaces.ISwingCustomizable#getSettingsPanel()
	 */
	public JPanel getSettingsPanel() {
		if (settingsPanel == null)
			settingsPanel = new HaarClassifierSettingsPanel(this);
		return settingsPanel;
	}
	
	public HaarClassifierDetection(){
		cascade = CvHaarClassifierCascade.deserializeFromStream(getClass().getResourceAsStream(cascadeFile));
		cascade.initHiddenCascade();
	}

	public Region[] detectFaces(String file, int minScanWindowSize) {
		
		// This is necessary for deserialized cascades
		if (!cascade.hasHiddenCascade()) {
			cascade.initHiddenCascade();			
		}
				
		BufferedImage image = MainController.getInstance().getBufferedImageCache().getImage(file);
		
		ArrayList<CvRect> result = haarDetectObjects(image, minScanWindowSize);

		Region[] regions = new Region[result.size()];
		
		for (int i = 0; i< regions.length; i++) {
			CvRect r = result.get(i);
			regions[i] = new Region(r.x + Math.round(r.width*0.5f), r.y + Math.round(r.height*0.5f), r.width, r.height, 0, file);
		}
		
		return regions;
	}
	
	public String toString(){
		return getName();
	}
	
	private ArrayList<CvRect> haarDetectObjects(BufferedImage img, int minScanWindowSize) {
				
		// calculate integral image and square integral image from grey values
		int sumIntegralImage[][] = new int[img.getHeight()+1][img.getWidth()+1];
		long sumSQIntegralImage[][] = new long[img.getHeight()+1][img.getWidth()+1];

		for (int y = 1; y <= img.getHeight(); y++) {
			
			int rowSum = 0;
			long rowSumSQ = 0;
			
			for (int x = 1; x <= img.getWidth(); x++) {

				//FIXME (but not really important right now): this seems not to work if original image file was 8 bit greyscale
				Color c = new Color(img.getRGB(x-1, y-1)); 
				int grey = (int) Math.round((c.getBlue() + c.getGreen() + c.getRed())/3.0);
				
				rowSum += grey;
				rowSumSQ += grey*grey;
				
				sumIntegralImage[y][x] = sumIntegralImage[y-1][x] + rowSum;
				sumSQIntegralImage[y][x] = sumSQIntegralImage[y-1][x] + rowSumSQ;
			}
		}
		
		// variables for detection process
//	    CvRect scanROIRect = new CvRect(0,0,0,0);
	    boolean isFound = false;

	    int splitStage = 2;
	    int nPass = 2;
	    
        if (splitStage >= cascade.getCount() || cascade.hidCascade.isTree) {
           splitStage = cascade.getCount();
           nPass = 1;
        }
        
        // calculate number of scale factors
        int nFactors = 0;
        for (float factor = 1; factor * cascade.origWindowSize.x < img.getWidth() - 10 && 
                           factor * cascade.origWindowSize.y < img.getHeight() - 10; factor *= scaleFactor) {
        	nFactors++;
        }
        
        // main detection loop
        ArrayList<CvRect> resultRects = new ArrayList<CvRect>();
		for (float factor = 1; nFactors-- > 0 && !isFound; factor *= scaleFactor) {
			double ystep = Math.max(2, factor);
			
			Point winSize = new Point(Math.round(cascade.origWindowSize.x * factor),
									  Math.round(cascade.origWindowSize.y * factor));
			
			int stageOffset = 0;
			
			int start_x = 0, start_y = 0;
			int end_x = (int) Math.round(((double)(img.getWidth() - winSize.x)) / ystep);			
			int end_y = (int) Math.round(((double)(img.getHeight() - winSize.y)) / ystep);

			setImagesForHaarClassifierCascade(cascade, sumIntegralImage, sumSQIntegralImage, null, factor);

			cascade.hidCascade.setCount(splitStage);
			
			boolean[][] mask = new boolean[img.getHeight()][img.getWidth()];

			for (int pass = 0; pass < nPass; pass++) {
				
				for (int _iy = start_y; _iy < end_y; _iy++) {
					int iy = (int) Math.round(_iy * ystep);
					int _ix, _xstep = 1;
					boolean[] maskRow = mask[iy];

					for (_ix = start_x; _ix < end_x; _ix += _xstep) {
						int ix = (int) Math.round(_ix * ystep);

						// first pass
						if (pass == 0) {
							int result=0;
							_xstep = 2;

							result = runHaarClassifierCascade(cascade, new Point(ix, iy), 0);
							
							
							if (result > 0) {
								if (pass < nPass - 1) {
									maskRow[ix] = true;
								}
								else {
									CvRect rect = new CvRect(ix, iy, winSize.x, winSize.y);
									resultRects.add(rect);
								}
							}
							if (result < 0)
								_xstep = 1;
						}
						
						// second pass
						else if (maskRow[ix]) {
							
							int result = runHaarClassifierCascade(cascade, new Point(ix, iy), stageOffset);
							
							if (result > 0) {
								if (pass == nPass - 1) {
									CvRect rect = new CvRect(ix, iy, winSize.x, winSize.y);
									resultRects.add(rect);
								}
							} else
								maskRow[ix] = false;
						}
					}
				}
				stageOffset = cascade.hidCascade.count;
				cascade.hidCascade.setCount(cascade.getCount());
			}
		}
		
		// TODO: group results
			
		return resultRects;
	}

	/**
	 * Equivalent to function as found in cvhaar.cpp
	 * 
	 * void cvSetImagesForHaarClassifierCascade(CvHaarClassifierCascade* _cascade,
	 * const CvArr* _sum, const CvArr* _sqsum, const CvArr* _tilted_sum, double
	 * scale)
	 * 
	 * @param cascade
	 * @param sumIntegralImage
	 * @param sumSQIntegralImage
	 * @param tiltedIntegralImage <-- can be expected to be null for now
	 * @param factor
	 */
	private void setImagesForHaarClassifierCascade(CvHaarClassifierCascade _cascade,
			int[][] sum, long[][] sqsum,
			int[][] tiltedIntegralImage, double scale) {

//		int coi0 = 0, coi1 = 0;
		CvRect equ_rect = new CvRect();
		double weight_scale;

		CvHidHaarClassifierCascade cascade = _cascade.hidCascade;

		_cascade.scale = scale;
		_cascade.realWindowSize.x = (int) Math.round(_cascade.origWindowSize.x * scale );
		_cascade.realWindowSize.y = (int) Math.round(_cascade.origWindowSize.y * scale );

		cascade.sum = sum;
		cascade.sqsum = sqsum;

		equ_rect.x = equ_rect.y = (int)Math.round(scale);
		equ_rect.width = (int)Math.round((_cascade.origWindowSize.x-2)*scale);
		equ_rect.height = (int)Math.round((_cascade.origWindowSize.y-2)*scale);
		weight_scale = 1./(equ_rect.width*equ_rect.height);
		cascade.invWindowArea = weight_scale;

		cascade.p0.setCoords(equ_rect.x, equ_rect.y);
		cascade.p1.setCoords(equ_rect.x + equ_rect.width, equ_rect.y);
		cascade.p2.setCoords(equ_rect.x, equ_rect.y + equ_rect.height);
		cascade.p3.setCoords(equ_rect.x + equ_rect.width, equ_rect.y + equ_rect.height);

		{
			for (int i = 0; i < _cascade.getCount(); i++ ) {

				for (int j = 0; j < cascade.stageClassifiers[i].getCount(); j++ ) {

					CvHidHaarClassifier hidClassifier = cascade.stageClassifiers[i].classifiers[j];

					for (int l = 0; l < cascade.stageClassifiers[i].classifiers[j].getCount(); l++ ) {

						CvHaarFeature feature = _cascade.stageClassifiers[i].classifiers[j].haarFeatures[l];

						CvHidHaarFeature hidFeature = hidClassifier.node[l].feature;
						double sum0 = 0, area0 = 0;

						int base_w = Integer.MAX_VALUE, base_h = Integer.MAX_VALUE;
						int new_base_w = 0, new_base_h = 0;
						int kx, ky;
						boolean flagx = false, flagy = false;
						int x0 = 0, y0 = 0;

						CvRect r[] = new CvRect[feature.rects.length];

						for (int k = 0; k < r.length; k++ ) {
							r[k] = feature.rects[k].r;
							base_w = (int)Math.min( base_w, (r[k].width-1) < 0 ? base_w : (r[k].width-1));
							base_w = (int)Math.min( base_w, (r[k].x - r[0].x-1) < 0 ? base_w : (r[k].x - r[0].x-1) );
							base_h = (int)Math.min( base_h, (r[k].height-1) < 0 ? base_h : (r[k].height-1) );
							base_h = (int)Math.min( base_h, (r[k].y - r[0].y-1) < 0 ? base_h : (r[k].y - r[0].y-1));
						}

						base_w += 1;
						base_h += 1;
						kx = r[0].width / base_w;
						ky = r[0].height / base_h;

						if( kx <= 0 ) {
							flagx = true;
							new_base_w = (int)Math.round( r[0].width * scale ) / kx;
							x0 = (int)Math.round( r[0].x * scale );
						}

						if( ky <= 0 ) {
							flagy = true;
							new_base_h = (int)Math.round( r[0].height * scale ) / ky;
							y0 = (int)Math.round( r[0].y * scale );
						}

						for (int k = 0; k < r.length; k++ ) {
							CvRect tr = new CvRect();
							double correction_ratio;

							if( flagx ) {
								tr.x = (r[k].x - r[0].x) * new_base_w / base_w + x0;
								tr.width = r[k].width * new_base_w / base_w;
							}
							else {
								tr.x = (int)Math.round( r[k].x * scale );
								tr.width = (int)Math.round( r[k].width * scale );
							}

							if( flagy ) {
								tr.y = (r[k].y - r[0].y) * new_base_h / base_h + y0;
								tr.height = r[k].height * new_base_h / base_h;
							}
							else {
								tr.y = (int)Math.round( r[k].y * scale );
								tr.height = (int)Math.round( r[k].height * scale );
							}

							correction_ratio = weight_scale * (!feature.tilted ? 1 : 0.5);

							if( !feature.tilted ) {
								hidFeature.rect[k].p0 = new Point(tr.x, tr.y);
								hidFeature.rect[k].p1 = new Point(tr.x + tr.width, tr.y);
								hidFeature.rect[k].p2 = new Point(tr.x, tr.y + tr.height);
								hidFeature.rect[k].p3 = new Point(tr.x + tr.width, tr.y + tr.height);
							}
							else {
//								hidFeature.rect[k].p2 = sum_elem_ptr(*tilted, tr.y + tr.width, tr.x +
//								tr.width);
//								hidFeature.rect[k].p3 = sum_elem_ptr(*tilted, tr.y + tr.width + tr.height,
//								tr.x + tr.width - tr.height);
//								hidFeature.rect[k].p0 = sum_elem_ptr(*tilted, tr.y, tr.x);
//								hidFeature.rect[k].p1 = sum_elem_ptr(*tilted, tr.y + tr.height, tr.x -
//								tr.height);
							}

							hidFeature.rect[k].weight = (float)(feature.rects[k].weight * correction_ratio);

							if( k == 0 )
								area0 = tr.width * tr.height;
							else
								sum0 += hidFeature.rect[k].weight * tr.width * tr.height;
						}

						hidFeature.rect[0].weight = (float)(-sum0/area0);
					}
				} 
			}
		}
	}

	/**
	 * Equivalent to function as found in cvhaar.cpp
	 * 
	 * int cvRunHaarClassifierCascade( CvHaarClassifierCascade* _cascade, CvPoint pt, int start_stage )
	 * 
	 * @param cascade
	 * @param pt
	 * @param startStage
	 * @return
	 */
	private int runHaarClassifierCascade(CvHaarClassifierCascade _cascade, Point pt, int startStage) {
		
		    double mean, variance_norm_factor;
		    CvHidHaarClassifierCascade cascade;

		    cascade = _cascade.hidCascade;

		    if( pt.x < 0 || pt.y < 0 ||
		            pt.x + _cascade.realWindowSize.x >= cascade.sum[0].length-2 ||
		            pt.y + _cascade.realWindowSize.y >= cascade.sum.length-2 )
		            return -1;
		    
//		    p_offset = pt.getHeight() * (cascade.sum.step/sizeof(sumtype)) + pt.getWidth();
//		    pq_offset = pt.getHeight() * (cascade.sqsum.step/sizeof(sqsumtype)) + pt.getWidth();
//		    mean = calc_sum(cascade,p_offset)*cascade.inv_window_area;
		    
		    mean = calcsum(cascade.sum, cascade.p0, cascade.p1, cascade.p2, cascade.p3, (int)pt.x, (int)pt.y) * cascade.invWindowArea;
		    
		    double sqp0 = cascade.sqsum[cascade.p0.y][cascade.p0.x];
		    double sqp1 = cascade.sqsum[cascade.p1.y][cascade.p1.x];
		    double sqp2 = cascade.sqsum[cascade.p2.y][cascade.p2.x];
		    double sqp3 = cascade.sqsum[cascade.p3.y][cascade.p3.x];
		    variance_norm_factor = calcsum(cascade.sqsum, cascade.p0, cascade.p1, cascade.p2, cascade.p3, (int)pt.x, (int)pt.y);
		    variance_norm_factor = variance_norm_factor*cascade.invWindowArea - mean*mean;
		    
		    if( variance_norm_factor >= 0. )
		        variance_norm_factor = Math.sqrt(variance_norm_factor);
		    else
		        variance_norm_factor = 1.;

		    if( cascade.isTree )
		    {
		    	
//		        CvHidHaarStageClassifier ptr;
//		        assert( start_stage == 0 );
//
//		        result = 1;
//		        ptr = cascade.stage_classifier;
//
//		        while( ptr )
//		        {
//		            double stage_sum = 0;
//
//		            for( j = 0; j < ptr.count; j++ )
//		            {
//		                stage_sum += icvEvalHidHaarClassifier( ptr.classifier + j,
//		                    variance_norm_factor, p_offset );
//		            }
//
//		            if( stage_sum >= ptr.threshold )
//		            {
//		                ptr = ptr.child;
//		            }
//		            else
//		            {
//		                while( ptr && ptr.next == NULL ) ptr = ptr.parent;
//		                if( ptr == NULL )
//		                {
//		                    return 0;
//		                }
//		                ptr = ptr.next;
//		            }
//		        }
		    }
		    else if( cascade.isStumpBased ) {
		    	
		        for (int i = startStage; i < cascade.count; i++ ) {
		        	
		            double stage_sum = 0;

// TODO: check if speedup by usage of two_rects option is worth the extra code
//		            
//		            if( cascade.stageClassifiers[i].twoRects ) {
//		            	
//		                for (int j = 0; j < cascade.stageClassifiers[i].getCount(); j++ ) {
//		                    CvHidHaarClassifier classifier = cascade.stageClassifiers[i].classifiers[j];
//		                    CvHidHaarTreeNode node = classifier.node[0];
//		                    double sum, t = node.threshold*variance_norm_factor, a, b;
//
//		                    sum = calcsum(cascade.sum, node.feature.rect[0], pt) * node.feature.rect[0].weight;
//		                    sum += calcsum(cascade.sum, node.feature.rect[1],pt) * node.feature.rect[1].weight;
//
//		                    a = classifier.alpha[0];
//		                    b = classifier.alpha[1];
//		                    stage_sum += sum < t ? a : b;
//		                }
//		            }
//		            else {
		            	
		                for (int j = 0; j < cascade.stageClassifiers[i].getCount(); j++ ) {
		                    CvHidHaarClassifier classifier = cascade.stageClassifiers[i].classifiers[j];
		                    CvHidHaarTreeNode node = classifier.node[0];
		                    double sum=0, t = node.threshold*variance_norm_factor, a, b;

		                    for (int k = 0; k < node.feature.rect.length; k++) {
		                    	sum += calcsum(cascade.sum, node.feature.rect[k],pt) * node.feature.rect[k].weight;
		                    }

		                    a = classifier.alpha[0];
		                    b = classifier.alpha[1];
		                    stage_sum += sum < t ? a : b;
		                }
//		            }

		            if( stage_sum < cascade.stageClassifiers[i].threshold ) {
		                return -i;
		            }
		        }
		    }
		    else {
//		        for( i = startStage; i < cascade.count; i++ )
//		        {
//		            double stage_sum = 0;
//
//		            for( j = 0; j < cascade.stageClassifiers[i].getCount(); j++ )
//		            {
//		                stage_sum += icvEvalHidHaarClassifier(
//		                    cascade.stageClassifiers[i].classifiers[j],
//		                    variance_norm_factor, p_offset );
//		            }
//
//		            if( stage_sum < cascade.stageClassifiers[i].threshold )
//		            {
//		                return -i;
//		            }
//		        }
		    }

		return 1;
	}
	
	private double calcsum(int[][] sum, HidRect hidRect, Point pt) {
		return calcsum(sum, hidRect.p0, hidRect.p1, hidRect.p2, hidRect.p3, (int)pt.x, (int)pt.y);
	}

	private long calcsum (int[][] sum, Point p0, Point p1, Point p2, Point p3, int xOffset, int yOffset) {

		// 	    ((rect).p0[offset] - (rect).p1[offset] - (rect).p2[offset] + (rect).p3[offset])
		return (   sum[p0.y + yOffset][p0.x + xOffset] 
		         - sum[p1.y + yOffset][p1.x + xOffset] 
		         - sum[p2.y + yOffset][p2.x + xOffset]
		         + sum[p3.y + yOffset][p3.x + xOffset] );
	}
	
	private double calcsum (long[][] sum, Point p0, Point p1, Point p2, Point p3, int xOffset, int yOffset) {

		return (   sum[p0.y + yOffset][p0.x + xOffset] 
		         - sum[p1.y + yOffset][p1.x + xOffset] 
		         - sum[p2.y + yOffset][p2.x + xOffset]
		         + sum[p3.y + yOffset][p3.x + xOffset] );
	}

	public String getCurrentCascadeFile() {
		return cascadeFile;
	}

	/**
	 * @return
	 */
	public double getScale() {
		return scaleFactor;
	}

	/**
	 * @param selectedCascade
	 */
	public void setCascade(String selection) {
		
		if (selection.equals(cascadeFile))
			return;
		
		CvHaarClassifierCascade newCascade = null;

		// try to load serialized cascade from jar file
		for (String s :  BUILT_IN_CASCADES) {
			if (s.equals(selection)) {
				newCascade = CvHaarClassifierCascade.deserializeFromStream(getClass().getResourceAsStream(selection));
				break;
			}
		}
		
		// try to load serialized cascade from external XML file
		if (newCascade == null) try {
			File xmlFile = new File(MainController.getInstance().getDataDir().getAbsoluteFile() + File.separator + HaarClassifierSettingsPanel.SUBFOLDER + File.separator + selection);
			newCascade = XMLParser.parseFromStream(new FileInputStream(xmlFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}		
		
		if (newCascade!=null) {
			cascadeFile = selection;
			cascade = newCascade;
		}
	}

	/**
	 * @param floatValue
	 */
	public void setScale(float scaleFactor) {
		this.scaleFactor = scaleFactor;
	}

	
	/**
	 * For testing and debugging
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			HaarClassifierDetection pluginInstance = new HaarClassifierDetection();
			
			System.out.println("Loading image.");
			BufferedImage image = ImageIO.read(new File("C:\\testdata\\2.png"));
			
			System.out.print("Running face detection... ");
			System.out.println(pluginInstance.haarDetectObjects(image, 10).size() + " faces found.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
