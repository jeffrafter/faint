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
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;


/**
 * @author maltech
 *
 */
public class ExceptionDialog extends JDialog{
	
	JButton butOk = new JButton("Ok");
	JButton butDetails = new JButton("Details");

	
	public ExceptionDialog(final Frame frame, final Throwable e, String text){
		super(frame, "Error", true);
		final JDialog thisDialog = this;
		
		if (text == null) text = "An error has occured.";
		JLabel label = new JLabel(text,javax.swing.UIManager.getIcon("OptionPane.warningIcon"), JLabel.LEFT);
		label.setBorder(new EmptyBorder(10,10,10,30));
		this.getContentPane().add(label, BorderLayout.NORTH);
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttons.add(butOk);
		buttons.add(butDetails);
		this.getContentPane().add(buttons, BorderLayout.SOUTH);
		this.pack();
		this.setLocationRelativeTo(frame);
		
		butDetails.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent event) {
				thisDialog.setVisible(false);
				thisDialog.setSize(800,500);
				thisDialog.setLocationRelativeTo(null);
				StringWriter o = new StringWriter();
				PrintWriter w = new PrintWriter(o);
				e.printStackTrace(w);
				JTextArea message = new JTextArea(o.toString());
				thisDialog.setContentPane(new JScrollPane(message));
				thisDialog.setVisible(true);
			}
		});
		
		butOk.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				thisDialog.dispose();
				}
		});	
		
		this.setVisible(true);
	}
}
