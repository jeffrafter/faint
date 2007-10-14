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

package de.offis.faint.gui.facedb;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import de.offis.faint.export.XMPExporter;

/**
 * @author maltech
 *
 */
public class XMPDialog extends JDialog {
	
	private JProgressBar progressBar = new JProgressBar();
	
	XMPExporter exporter = null;
	
	JTextPane textArea = new JTextPane();
	JPanel buttons = new JPanel(new GridLayout());
	JPanel progressPanel = new JPanel(new BorderLayout());
	JButton butOk = new JButton("Proceed");
	JButton butCancel = new JButton("Cancel");
	JLabel label = new JLabel("<html>" +
			"                        <b>Warning:</b><br>" +
			"                        All images found in the database will be physically<br>" +
			"                        modified! Please back up your files before" +
			"                        proceeding.<br><br>" +
			"						 Currently XMP headers can only be written to images<br>" +
			"                        in JPEG format.</html>"); 
	
	
	public XMPDialog(JFrame frame){
		this.setModal(true);
		this.setTitle("Write XMP Headers...");
		this.getContentPane().setLayout(new BorderLayout());
		label.setBorder(new EmptyBorder(10,20,20,60));
		this.add(label, BorderLayout.CENTER);
		buttons.add(butOk);
		buttons.add(butCancel);
		progressPanel.setBorder(new TitledBorder("Status"));
		progressPanel.add(new JScrollPane(textArea));
		
		textArea.setEditable(false);
				
		this.add(buttons, BorderLayout.SOUTH);
		this.pack();
		this.setLocationRelativeTo(null);
		new Listener();
		
		this.setVisible(true);
	}
	
	class Listener implements ActionListener{
		
		public Listener(){
			butOk.addActionListener(this);
			butCancel.addActionListener(this);
		}

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {

			// proceed
			if (e.getSource() == butOk){
				setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
				remove(label);
				add(progressPanel, BorderLayout.CENTER);
				
				buttons.removeAll();
				buttons.setLayout(new BorderLayout());
				buttons.add(progressBar, BorderLayout.CENTER);
				buttons.add(butCancel, BorderLayout.EAST);
				validate();
				
				exporter = new XMPWorker();
				exporter.execute();
			}
			
			// close Dialog
			else {
				if (exporter != null) exporter.cancel(true);
				dispose();
			}
		}
		
	}
	
	class XMPWorker extends XMPExporter {
		
		String lastPath = null;
		
		
		@Override
		protected void process(List<String> chunks) {			
//			super.process(chunks);
			
			String append = "";

			for (String s : chunks) {
				int i = Math.max(s.lastIndexOf("\\"), s.lastIndexOf("/"));
				String path = s.substring(0, i);

				
				if (!path.equals(lastPath)) {
					lastPath = path;
					append += "Entering " + path + "\n";
				}
				append += "Processing " + s.substring(i + 1) + "\n";
			}
			textArea.setText(textArea.getText() + append);
			
			progressBar.setValue(Math.round(100 * ((float) filesProcessed / (float) this.files.length)));
			
		}

		@Override
		protected void done() {
			super.done();
			butCancel.setText("Close");
			setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		}
		
	}

}
