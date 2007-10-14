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

package de.offis.faint.detection.plugins.betaface;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import de.offis.faint.gui.tools.NiceJPanel;

/**
 * @author maltech
 *
 */
public class BetafaceSettingsPanel extends JPanel {
	
	private static final long serialVersionUID = 1472040205731846968L;

	private JTextField tfURL = new JTextField();
	private JTextField tfKey = new JTextField();
	private JCheckBox cbDownScale = new JCheckBox("Downscale big images to a maximum of 640x480");
	
	private BetafaceDetection plugin;
	
	public BetafaceSettingsPanel(BetafaceDetection plugin){
		super(new BorderLayout());
		this.plugin = plugin;
		
		// Layout and initial values
		NiceJPanel rows = new NiceJPanel();
		this.add(rows, BorderLayout.NORTH);

		JPanel urlPanel = new JPanel(new GridLayout());
		urlPanel.setBorder(new TitledBorder("Web Service URL"));
		urlPanel.add(tfURL);
		tfURL.setText(plugin.serviceURL);
		rows.addRow(urlPanel);
		
		JPanel keyPanel = new JPanel(new GridLayout());
		keyPanel.setBorder(new TitledBorder("License Key"));		
		keyPanel.add(tfKey);
		tfKey.setText(plugin.licenseKey);
		rows.addRow(keyPanel);
		
		JPanel shrinkPanel = new JPanel(new GridLayout());
		shrinkPanel.setBorder(new TitledBorder("Preprocessing"));
		shrinkPanel.add(cbDownScale);
		cbDownScale.setSelected(plugin.downScale);
		rows.addRow(shrinkPanel);
		
		// Listener
		Listener listener = new Listener();
		cbDownScale.addActionListener(listener);
		tfKey.addFocusListener(listener);
		tfURL.addFocusListener(listener);		
	}
	
	class Listener implements ActionListener, FocusListener{
		
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			plugin.downScale = cbDownScale.isSelected();
		}

		/* (non-Javadoc)
		 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
		 */
		public void focusLost(FocusEvent e) {
			plugin.licenseKey = tfKey.getText();
			plugin.serviceURL = tfURL.getText();
		}
		
		/* (non-Javadoc)
		 * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
		 */
		public void focusGained(FocusEvent e) {}		
	}

}
