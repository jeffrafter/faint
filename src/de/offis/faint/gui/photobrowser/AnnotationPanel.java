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

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import de.offis.faint.gui.MainFrame;
import de.offis.faint.model.ImageModel;
import de.offis.faint.model.Region;

/**
 * @author maltech
 *
 */
public class AnnotationPanel extends JScrollPane {
	
	private AnnotationTable table;
	
	public AnnotationPanel(MainFrame mainFrame){
		super();
		table =  new AnnotationTable(mainFrame);
		this.setBorder(new TitledBorder("Face Annotations"));

		this.setPreferredSize(new Dimension(150,150));
		this.setViewportView(table);
		this.getViewport().setBackground(Color.WHITE);
		this.setViewportBorder(new BevelBorder(BevelBorder.LOWERED));
	}

	public void setImage(ImageModel currentImage) {
		table.setImage(currentImage);
	}

	public void setSelectedRegion(Region newRegion) {
		table.setSelectedRegion(newRegion);
		
	}

	public void updateFromModel() {
		table.updateFromModel();
	}

}
