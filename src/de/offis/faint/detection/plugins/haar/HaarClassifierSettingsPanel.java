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

package de.offis.faint.detection.plugins.haar;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import de.offis.faint.controller.MainController;
import de.offis.faint.global.Utilities.FileTypeFilter;
import de.offis.faint.gui.tools.NiceJPanel;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author maltech
 *
 */
public class HaarClassifierSettingsPanel extends JPanel{
	
	private static final long serialVersionUID = -3843774451914048582L;
	
	private HaarClassifierDetection model;
	private JComboBox cascadeMenu = new JComboBox();
	private JSpinner scaleFactor = new JSpinner();
    private JSpinner minimumGroupMembers = new JSpinner();
    private JCheckBox showAllMatches = new JCheckBox("Show all matches");
    private JCheckBox scaleImage = new JCheckBox("Scale image before search");
    private JCheckBox performHistogramEqualization = new JCheckBox("Perform histogram equalization");
    private final String[] CASCADE_SUFFIXES = {".xml",".XML"};
	protected static final String SUBFOLDER = "opencv";
	
	private FileTypeFilter cascadeFilenameFilter = new FileTypeFilter(CASCADE_SUFFIXES);
	

	public HaarClassifierSettingsPanel(HaarClassifierDetection model){
		super(new BorderLayout());
		EventListener listener = new EventListener();
		NiceJPanel rows = new NiceJPanel();
		
		JPanel cascadeMenuPanel = new JPanel(new BorderLayout());
		cascadeMenuPanel.setBorder(new TitledBorder("Cascade File"));
		cascadeMenuPanel.add(cascadeMenu, BorderLayout.CENTER);
		cascadeMenu.addActionListener(listener);
		rows.addRow(cascadeMenuPanel);
		
		JPanel scaleFactorPanel = new JPanel(new BorderLayout());
		scaleFactorPanel.setBorder(new TitledBorder("Scale Factor for Scan Window"));
		scaleFactorPanel.add(scaleFactor, BorderLayout.CENTER);
		scaleFactor.addChangeListener(listener);
		rows.addRow(scaleFactorPanel);

        JPanel minimumGroupMembersPanel = new JPanel(new BorderLayout());
        minimumGroupMembersPanel.setBorder(new TitledBorder("Miminum Matches per Group"));
        minimumGroupMembersPanel.add(minimumGroupMembers, BorderLayout.CENTER);
        minimumGroupMembers.addChangeListener(listener);
        minimumGroupMembersPanel.add(showAllMatches, BorderLayout.SOUTH);
        showAllMatches.addActionListener(listener);
        rows.addRow(minimumGroupMembersPanel);

        JPanel scaleImagePanel = new JPanel(new BorderLayout());
        scaleImagePanel.setBorder(new TitledBorder("Scale Image"));
        scaleImagePanel.add(scaleImage, BorderLayout.CENTER);
        scaleImage.addActionListener(listener);
        rows.addRow(scaleImagePanel);
        
        JPanel performHistogramEqualizationPanel = new JPanel(new BorderLayout());
        performHistogramEqualizationPanel.setBorder(new TitledBorder("Scale Image"));
        performHistogramEqualizationPanel.add(performHistogramEqualization, BorderLayout.CENTER);
        performHistogramEqualization.addActionListener(listener);
        rows.addRow(performHistogramEqualizationPanel);

        this.add(rows, BorderLayout.NORTH);
		this.model = model;
		
		updateFromModel();
	}
	
	private void updateFromModel(){
		File dataFolder = new File(MainController.getInstance().getDataDir().getAbsoluteFile() + File.separator + SUBFOLDER);

		String[] xmlCascades = dataFolder.list(cascadeFilenameFilter);
		String[] binCascades = HaarClassifierDetection.BUILT_IN_CASCADES;
		
		String[] allCascades = new String[xmlCascades.length + binCascades.length];
		
		System.arraycopy(binCascades, 0, allCascades, 0, binCascades.length);
		System.arraycopy(xmlCascades, 0, allCascades, binCascades.length, xmlCascades.length);
		
		cascadeMenu.setModel(new DefaultComboBoxModel(allCascades));
		
		selectionLoop : for(int i = 0; i < allCascades.length; i++){
			if (model.getCurrentCascadeFile().equals(allCascades[i])){
				cascadeMenu.setSelectedIndex(i);
				break selectionLoop;
			}
		}
		
		scaleFactor.setModel(new SpinnerNumberModel(model.getScale(),1.04,2,0.05));

        if (model.getGroupingPolicy().getMinimumGroupSize() == GroupingPolicy.ALL) {
            showAllMatches.setSelected(true);
            minimumGroupMembers.setValue(3);
            minimumGroupMembers.setEnabled(false);
        } else {
            showAllMatches.setSelected(false);
            minimumGroupMembers.setValue(model.getGroupingPolicy().getMinimumGroupSize());
            minimumGroupMembers.setEnabled(true);
        }

        scaleImage.setSelected(model.isScaleImage());
        performHistogramEqualization.setSelected(model.isPerformHistogramEqualization());
    }
	
	private class EventListener implements ActionListener, ChangeListener{

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {

			if (e.getSource()==cascadeMenu){
				String selectedCascade = (String) cascadeMenu.getSelectedItem();
				if (selectedCascade!= null) model.setCascade(selectedCascade);
			} else if (e.getSource() == showAllMatches) {
                if (!showAllMatches.isSelected()) {
                    minimumGroupMembers.setEnabled(true);
                    model.getGroupingPolicy().setMinimumGroupSize((Integer) minimumGroupMembers.getValue());
                } else {
                    minimumGroupMembers.setEnabled(false);
                    model.getGroupingPolicy().setMinimumGroupSize(GroupingPolicy.ALL);
                }
            } else if (e.getSource() == scaleImage) {
                model.setScaleImage(scaleImage.isSelected());
            } else if (e.getSource() == performHistogramEqualization) {
                model.setPerformHistogramEqualization(performHistogramEqualization.isSelected());
            }
		}

		/* (non-Javadoc)
		 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
		 */
		public void stateChanged(ChangeEvent e) {

			if (e.getSource()==scaleFactor){
				model.setScale(((SpinnerNumberModel) scaleFactor.getModel()).getNumber().floatValue());				
			} else if (e.getSource() == minimumGroupMembers) {
                model.getGroupingPolicy().setMinimumGroupSize((Integer) minimumGroupMembers.getValue());
            }
		}
	}
}
