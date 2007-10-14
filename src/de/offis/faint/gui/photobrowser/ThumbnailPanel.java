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
import java.io.File;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import de.offis.faint.gui.MainFrame;
import de.offis.faint.gui.photobrowser.ThumbnailTable.ThumbnailTableModel;
import de.offis.faint.model.ImageModel;

/**
 * @author maltech
 *
 */
public class ThumbnailPanel extends JPanel{
	
	private JTextField tfFolder = new JTextField();
	private ThumbnailTable thumbTable;
	
	public ThumbnailPanel(MainFrame mainFrame){
		super(new BorderLayout());
		
		thumbTable = new ThumbnailTable(mainFrame);

		// Prepare folder textfield
		JPanel folderPanel = new JPanel(new BorderLayout());
		folderPanel.setBorder(new TitledBorder("Folder"));
		folderPanel.add(tfFolder, BorderLayout.CENTER);
		tfFolder.setEditable(false);
		this.add(folderPanel, BorderLayout.NORTH);
		
		// Prepare thumbnail table
		JScrollPane thumbnailPane = new JScrollPane(thumbTable);
		thumbnailPane.getViewport().setBackground(Color.WHITE);
		thumbnailPane.setViewportBorder(new BevelBorder(BevelBorder.LOWERED));
		thumbnailPane.setBorder(new TitledBorder("Thumbnails"));
		this.add(thumbnailPane, BorderLayout.CENTER);
	}

	/**
	 * @param folder
	 */
	public void setFolder(File folder) {
		this.tfFolder.setText(folder.toString());
		this.thumbTable.getThumbnailTableModel().setFolder(folder);
	}
	
	public void setSelectedImage(ImageModel image) {
		
		for (int i = 0; i < thumbTable.getRowCount(); i++){
			if (((ThumbnailTableModel)thumbTable.getModel()).getImage(i).equals(image))
			this.thumbTable.changeSelection(i, 0, false, false);
		}
	}
}
