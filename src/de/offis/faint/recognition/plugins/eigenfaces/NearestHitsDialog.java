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
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

/**
 * @author maltech
 *
 */
public class NearestHitsDialog extends JDialog {
	
	private JLabel originalThumb = new JLabel();
	private JLabel reconstructionThumb = new JLabel();	
	private JLabel[] nearestHitThumbs = new JLabel[9];
	private JButton closeButton = new JButton("Close Window");
	
	public NearestHitsDialog(){
		this.setTitle("Unfiltered Eigenface Results");
		JPanel reconstructionPanel = new JPanel(new GridLayout(1,1,5,5));
		reconstructionPanel.setBorder(new TitledBorder("Reconstruction"));
//		reconstructionPanel.add(originalThumb);
		reconstructionThumb.setVerticalAlignment(SwingConstants.TOP);
		reconstructionPanel.add(reconstructionThumb);
		
		JPanel hitsPanel = new JPanel(new GridLayout(3,3,5,5));
		hitsPanel.setBorder(new TitledBorder("Best Hits"));
		for (int i = 0; i < nearestHitThumbs.length; i++){
			nearestHitThumbs[i] = new JLabel();
			hitsPanel.add(nearestHitThumbs[i]);
		}
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(closeButton);
		
		JPanel rootPanel = new JPanel(new BorderLayout());
		rootPanel.add(reconstructionPanel, BorderLayout.WEST);
		rootPanel.add(hitsPanel, BorderLayout.CENTER);
		rootPanel.add(buttonPanel, BorderLayout.SOUTH);
		this.setContentPane(rootPanel);
		this.setResizable(false);
		
		closeButton.addActionListener(new Listener());
	}
	
	public void show(BufferedImage original, BufferedImage reconstruction, BufferedImage[] hits){
		
		this.setVisible(false);
		
		originalThumb.setIcon(new ImageIcon(original));
		reconstructionThumb.setIcon(new ImageIcon(reconstruction));
		
		if (hits != null)
		for (int i = 0; i < nearestHitThumbs.length; i++){
			if (i < hits.length)
				nearestHitThumbs[i].setIcon(new ImageIcon(hits[i]));
			else
				nearestHitThumbs[i].setIcon(null);
		}

		if (hits.length == 0)
			nearestHitThumbs[0].setText("(No Hits)");
		else
			nearestHitThumbs[0].setText(null);
		
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	
	class Listener implements ActionListener {

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			setVisible(false);
		}		
	}
}
