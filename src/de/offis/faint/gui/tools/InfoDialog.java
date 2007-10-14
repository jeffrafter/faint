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

package de.offis.faint.gui.tools;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


/**
 * @author maltech
 *
 */
public class InfoDialog extends JDialog{
	
	JButton butOk = new JButton("Ok");

	
	public InfoDialog(final Frame frame, String text){
		super(frame, "Info", true);
		final JDialog thisDialog = this;
		
		JLabel label = new JLabel(text,javax.swing.UIManager.getIcon("OptionPane.informationIcon"), JLabel.LEFT);
		label.setBorder(new EmptyBorder(10,10,10,30));
		this.getContentPane().add(label, BorderLayout.NORTH);
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttons.add(butOk);
		this.getContentPane().add(buttons, BorderLayout.SOUTH);
		this.pack();
		this.setLocationRelativeTo(frame);
		
		butOk.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				thisDialog.dispose();
				}
		});	
		
		this.setVisible(true);
	}
}
