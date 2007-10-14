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

package de.offis.faint.gui.photobrowser;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;

import de.offis.faint.global.Constants;
import de.offis.faint.gui.MainFrame;
;

/**
 * @author maltech
 *
 */
public class BrowserTab extends JPanel {
	
	private BrowserMenuBar menuBar;
	private ThumbnailPanel thumnailPanel;
	private AnnotationPanel annotationPanel;
	private ImagePanel imagePanel;
	private ScanWindowSlider scanWindowSlider;
	private DetectionDialog detectionDialog;
	
	public BrowserTab(MainFrame mainFrame){
		super(new BorderLayout());
		
		// Prepare detection dialog
		detectionDialog = new DetectionDialog(mainFrame);

		// Init the main panel
		JPanel mainBrowserPanel = new JPanel(new BorderLayout());
		menuBar = new BrowserMenuBar(mainFrame);
		imagePanel = new ImagePanel(mainFrame);
		annotationPanel = new AnnotationPanel(mainFrame);
		scanWindowSlider = new ScanWindowSlider(mainFrame);
		mainBrowserPanel.add(menuBar, BorderLayout.NORTH);
		mainBrowserPanel.add(imagePanel, BorderLayout.CENTER);
		mainBrowserPanel.add(scanWindowSlider, BorderLayout.EAST);
		mainBrowserPanel.setBackground(Color.DARK_GRAY);
		
		// Prepare vertical SplitPane
		JSplitPane verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mainBrowserPanel, annotationPanel);
		verticalSplitPane.setDividerLocation(-1);
		verticalSplitPane.setOneTouchExpandable(true);
		verticalSplitPane.setResizeWeight(1);
		verticalSplitPane.setBorder(new EmptyBorder(0,0,0,0));

		// Prepare horizontal SplitPane
		thumnailPanel = new ThumbnailPanel(mainFrame);
		JSplitPane horizontalSplitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, thumnailPanel, verticalSplitPane);
		horizontalSplitpane.setDividerLocation(Constants.INITIAL_HORIZONTAL_DIVIDERLOCATION);
		horizontalSplitpane.setOneTouchExpandable(true);
		horizontalSplitpane.setBorder(new EmptyBorder(0,0,0,0));
		this.add(horizontalSplitpane, BorderLayout.CENTER);
	}

	public ThumbnailPanel getThumbnailPanel() {
		return thumnailPanel;
	}

	public ImagePanel getImagePanel() {
		return imagePanel;
	}

	/**
	 * @return
	 */
	public ScanWindowSlider getScanWindowSlider() {
		return this.scanWindowSlider;
	}

	/**
	 * @return
	 */
	public AnnotationPanel getAnnotationPanel() {
		return this.annotationPanel;
	}

	public DetectionDialog getDetectionDialog() {
		return detectionDialog;
	}

	public BrowserMenuBar getMenuBar() {
		return menuBar;
	}
}
