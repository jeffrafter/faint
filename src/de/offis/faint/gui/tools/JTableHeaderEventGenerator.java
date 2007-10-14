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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.table.JTableHeader;

/**
 * @author maltech
 *
 */
public class JTableHeaderEventGenerator extends MouseAdapter{
	
	private JTableHeader header;
	private JTableWithHeaderListener table;
	
	public JTableHeaderEventGenerator(JTableWithHeaderListener listener){
		this.header = listener.getTableHeader();
		header.addMouseListener(this);
		this.table = listener;
	}
	
	public void mouseClicked(MouseEvent e) {
		int col = header.columnAtPoint(e.getPoint());
	    int sortCol = header.getTable().convertColumnIndexToModel(col);
	    table.columnClicked(sortCol);		
	}			
	
	public static abstract class JTableWithHeaderListener extends EditableJTable{
		public abstract void columnClicked(int colIndex);		
	}
}
