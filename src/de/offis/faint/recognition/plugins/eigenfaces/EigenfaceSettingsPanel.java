/*******************************************************************************
 * + -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- +
 * |                                                                         |
 *    faint - The Face Annotation Interface
 * |  Copyright (C) 2007  Malte Mathiszig                                    |
 * 
 * |  This program is free software: you can redistribute it and/or modify   |
 *    it under the terms of the GNU General Public License as published by
 * |  the Free Software Foundation, either version 3 of the License, or      |
 *    (at your option) any later version.                                     
 * |                                                                         |
 *    This program is distributed in the hope that it will be useful,
 * |  but WITHOUT ANY WARRANTY; without even the implied warranty of         |
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * |  GNU General Public License for more details.                           |
 * 
 * |  You should have received a copy of the GNU General Public License      |
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * |                                                                         |
 * + -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- +
 *******************************************************************************/

package de.offis.faint.recognition.plugins.eigenfaces;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.TitledBorder;

import de.offis.faint.global.Constants;
import de.offis.faint.global.Utilities;
import de.offis.faint.gui.tools.NiceJPanel;

/**
 * @author maltech
 *
 */
public class EigenfaceSettingsPanel extends JPanel {
	
	private static final long serialVersionUID = 6466877259264876511L;

	private static final String[] RECALC_OPTIONS = {"Every new training image", "10 new training images", "100 new training images"};
	private JComboBox recalculationMenu = new JComboBox(RECALC_OPTIONS);

	private static final String[] NUMFACE_OPTIONS = {"Undefined", "40", "100", "400", "1000"};
	private JComboBox numFaceMenu = new JComboBox(NUMFACE_OPTIONS);
	
	private JCheckBox mirrorTrainingImages = new JCheckBox("Extend recognition to mirrored regions.");
	private JCheckBox showNearestHits = new JCheckBox("Show nearest hits in dialog.");
	
	private JLabel averageFace = new JLabel();
	private JLabel eigenFaces[] = new JLabel[3];
	private JPanel images;
	private JPanel progressPanel = new JPanel(new BorderLayout());
	private JProgressBar progressBar = new JProgressBar();
	
	private EigenfaceRecognition model;
	
	public EigenfaceSettingsPanel(EigenfaceRecognition model){
		super(new BorderLayout());
		
		this.model = model;
				
		NiceJPanel rows = new NiceJPanel();
		this.add(rows, BorderLayout.NORTH);
		
		JPanel numFacePanel = new JPanel(new GridLayout());
		numFacePanel.add(numFaceMenu);
		numFacePanel.setBorder(new TitledBorder("Maximum number of Eigenfaces"));
		rows.addRow(numFacePanel);
		
		JPanel recalculationPanel = new JPanel(new GridLayout());
		recalculationPanel.add(recalculationMenu);
		recalculationPanel.setBorder(new TitledBorder("Auto rebuild Face Space after..."));
		rows.addRow(recalculationPanel);
		
		JPanel checkboxPanel = new JPanel(new GridLayout(2,1));
		checkboxPanel.setBorder(new TitledBorder("Extras"));
		checkboxPanel.add(mirrorTrainingImages);
		checkboxPanel.add(showNearestHits);
		rows.addRow(checkboxPanel);
		
		images = new JPanel(new GridLayout(1, eigenFaces.length + 1));
		images.add(averageFace);
		for (int i = 0; i<eigenFaces.length; i++){
			eigenFaces[i] = new JLabel();
			images.add(eigenFaces[i]);	
		}
		images.setBorder(new TitledBorder("Average face and first Eigenfaces"));
		rows.addRow(images);
		
		progressBar.setIndeterminate(true);
		progressPanel.setBorder(new TitledBorder("Rebuild in Progress..."));
		progressPanel.add(progressBar, BorderLayout.CENTER);
		rows.addRow(progressPanel);
		progressPanel.setVisible(false);
		
		
		// Initial value for recalculation ComboBox
		if (model.rebuildFaceSpace == null){
			recalculationMenu.setSelectedItem(RECALC_OPTIONS[0]);
		} else if (model.rebuildFaceSpace <=40) {
			recalculationMenu.setSelectedItem(RECALC_OPTIONS[1]);
		} else if (model.rebuildFaceSpace <=100) {
			recalculationMenu.setSelectedItem(RECALC_OPTIONS[2]);
		} else if (model.rebuildFaceSpace <=400) {
			recalculationMenu.setSelectedItem(RECALC_OPTIONS[3]);
		} else {
			recalculationMenu.setSelectedItem(RECALC_OPTIONS[4]);
		}
		
		// Initial value for maximum eigenfaces ComboBox
		numFaceDetermination : if (model.maxEigenfaces == null){
			numFaceMenu.setSelectedItem(NUMFACE_OPTIONS[0]);
		} else {
			for (int i = 1; i<NUMFACE_OPTIONS.length; i++){
				if (model.maxEigenfaces <= Integer.valueOf(NUMFACE_OPTIONS[i])){
					numFaceMenu.setSelectedItem(NUMFACE_OPTIONS[i]);
					break numFaceDetermination;
				}
			}
			numFaceMenu.setSelectedItem(NUMFACE_OPTIONS[NUMFACE_OPTIONS.length-1]);
		}
		
		
		//Initial values for checkboxes
		mirrorTrainingImages.setSelected(model.mirrorFaces);
		showNearestHits.setSelected(model.showHitsDialog);
	
		
		// Event listener
		EventListener listener = new EventListener();
		numFaceMenu.addActionListener(listener);
		recalculationMenu.addActionListener(listener);
		mirrorTrainingImages.addActionListener(listener);
		showNearestHits.addActionListener(listener);
		
		updateFromModel();
	}
	
	protected void updateFromModel(){
		
		// Update average face
		byte[] averageFace = model.getAverageFace();
		if (averageFace != null && model.getEigenFaces()!=null && model.getEigenFaces().size() >= 3 ){
			
//			double[] test = new double[averageFace.length];
//			for(int i = 0; i < test.length; i++){
//				test[i] = ((double)(averageFace[i] & 0xff)) *0.0001 +999999999;
//			}
//			averageFace = Utilities.spreadGreyValues(test);
			
			BufferedImage aFace = Utilities.intensityArrayToBufferedImage(averageFace, Constants.FACE_THUMBNAIL_SIZE);
			this.averageFace.setIcon(new ImageIcon(aFace));
			
			// Update eigenfaces
			if (model.getEigenFaces() != null && model.getEigenFaces().size() > 0)
				for(int i = 0; i<eigenFaces.length; i++){
					byte [] ef = Utilities.spreadGreyValues(model.getEigenFaces().get(i));
					BufferedImage recoFaceImage = Utilities.intensityArrayToBufferedImage(ef, Constants.FACE_THUMBNAIL_SIZE);
					this.eigenFaces[i].setIcon(new ImageIcon(recoFaceImage));
				}
			images.setVisible(true);
		}
		else {
			images.setVisible(false);			
		}
		
		// Update progress bar
		progressPanel.setVisible(model.updateIsRunning);
		this.revalidate();
	}
	
	private class EventListener implements ActionListener{

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == numFaceMenu){
				String value = (String) numFaceMenu.getSelectedItem();
				if (value.equals("Undefined")){
					model.maxEigenfaces = null;
				}
				else model.maxEigenfaces = Integer.valueOf(value);
			}
			else if (e.getSource() == recalculationMenu){
				switch (recalculationMenu.getSelectedIndex()){
				case 0:
					model.rebuildFaceSpace = null;
					break;
				case 1:
					model.rebuildFaceSpace = 10;
					break;
				case 2:
					model.rebuildFaceSpace = 100;
					break;
				}
			}
			else if (e.getSource() == mirrorTrainingImages){
				model.mirrorFaces = mirrorTrainingImages.isSelected();
			}
			else if (e.getSource() == showNearestHits){
				model.showHitsDialog = showNearestHits.isSelected();
			}
		}
	}
}
