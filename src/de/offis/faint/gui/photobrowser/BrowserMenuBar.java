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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JToolBar;

import de.offis.faint.data.RessourceLoader;
import de.offis.faint.gui.MainFrame;
import de.offis.faint.gui.events.EventDetectFacesOnCurrentImage;
import de.offis.faint.gui.events.EventOpenFolder;

/**
 * @author maltech
 *
 */
public class BrowserMenuBar extends JToolBar{
	
	// Buttons
	private JButton detectFacesButton;
	private JButton openFolderButton;

	private JFileChooser folderChooser;
	private MainFrame mainFrame;
	
	/**
	 * Constructor
	 * @param mainFrame 
	 *
	 */
	public BrowserMenuBar(MainFrame mainFrame)
	{
		super("Controls");
		this.mainFrame = mainFrame;
		this.folderChooser = new JFileChooser();
		this.setFloatable(false);
		
		// Prepare listener and FileChooser
		Listener listener = new Listener();
		folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		mainFrame.addIsolatedComponent(folderChooser);
		
		// Prepare buttons
		try {
			detectFacesButton = new JButton(new ImageIcon(ImageIO.read(RessourceLoader.getFile("detect.png"))));
			detectFacesButton.setEnabled(false);
			openFolderButton = new JButton(new ImageIcon(ImageIO.read(RessourceLoader.getFile("open.png"))));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.add(openFolderButton);
		this.add(detectFacesButton);
		openFolderButton.addActionListener(listener);
		detectFacesButton.addActionListener(listener);
		this.addSeparator();
		
		
		// Set tool tips
		openFolderButton.setToolTipText("Open Folder...");
		detectFacesButton.setToolTipText("Run Face Detection");
		openFolderButton.setText("Open Folder...");
		detectFacesButton.setText("Run Face Detection");
	}
	
	class Listener implements ActionListener{
		
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			
			if (e.getSource().equals(openFolderButton)){
				switch (folderChooser.showOpenDialog(mainFrame)){
				case JFileChooser.APPROVE_OPTION:
					mainFrame.eventDispatcher.dispatchEvent(new EventOpenFolder(folderChooser.getSelectedFile()));
					break;
				}
				
			}
			else if (e.getSource().equals(detectFacesButton)){
				mainFrame.eventDispatcher.dispatchEvent(new EventDetectFacesOnCurrentImage());
			}
		}
	}

	/**
	 * @param b
	 */
	public void update(boolean detectionState) {
		this.detectFacesButton.setEnabled(detectionState);		
	}	
}
