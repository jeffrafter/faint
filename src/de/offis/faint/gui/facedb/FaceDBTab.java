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
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import de.offis.faint.global.Constants;
import de.offis.faint.gui.MainFrame;

/**
 * @author maltech
 *
 */
public class FaceDBTab extends JPanel{
	
	private PersonPanel personPanel;
	private FaceGallery facePanel;
	
	public FaceDBTab(MainFrame mainFrame){
		super(new BorderLayout());
		facePanel = new FaceGallery(mainFrame);
		this.personPanel = new PersonPanel(mainFrame);
	
		JScrollPane faceScrollPane = new JScrollPane(facePanel);
		faceScrollPane.setBorder(new TitledBorder("Face Gallery"));
		faceScrollPane.getViewport().setBackground(Color.WHITE);
		faceScrollPane.setViewportBorder(new BevelBorder(BevelBorder.LOWERED));
		JPanel container = new JPanel(new BorderLayout());
		container.add(personPanel, BorderLayout.CENTER);
		container.setBorder(new TitledBorder("Known Persons"));
		JPanel container2 = new JPanel(new BorderLayout());
		container2.add(faceScrollPane, BorderLayout.CENTER);
		container2.add(new FaceDBMenuBar(mainFrame), BorderLayout.NORTH);

		
		JSplitPane splitPane =  new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, container, container2);
		splitPane.setBorder(new EmptyBorder(0,0,0,0));
		splitPane.setDividerLocation(Constants.INITIAL_HORIZONTAL_DIVIDERLOCATION);
		splitPane.setOneTouchExpandable(true);

		this.add(splitPane, BorderLayout.CENTER);

	}

	public FaceGallery getFacePanel() {
		return facePanel;
	}

	public PersonPanel getPersonPanel() {
		return personPanel;
	}
}
