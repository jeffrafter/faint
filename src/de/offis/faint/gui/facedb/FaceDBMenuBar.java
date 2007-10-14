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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import de.offis.faint.data.RessourceLoader;
import de.offis.faint.gui.MainFrame;

/**
 * @author maltech
 *
 */
public class FaceDBMenuBar extends JToolBar{
	
	private JButton xmpButton;
	private MainFrame mainFrame;
	
	/**
	 * Constructor
	 * @param mainFrame 
	 *
	 */
	public FaceDBMenuBar(MainFrame mainFrame)
	{
		super("Controls");
		this.mainFrame = mainFrame;
		this.setFloatable(false);
		
		// Prepare listener
		Listener listener = new Listener();
		
		// Prepare buttons
		try {
			xmpButton = new JButton(new ImageIcon(ImageIO.read(RessourceLoader.getFile("xmp.png"))));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.add(xmpButton);
		xmpButton.setText("Write XMP Headers...");
		xmpButton.addActionListener(listener);
		this.addSeparator();
		
		
		// Set tool tips
		xmpButton.setToolTipText("Write XMP Headers...");
	}
	
	class Listener implements ActionListener{
		
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			
			if (e.getSource().equals(xmpButton)){
				new XMPDialog(mainFrame);				
			}
		}
	}
}
