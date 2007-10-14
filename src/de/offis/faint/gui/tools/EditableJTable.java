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

import java.awt.Component;
import java.util.EventObject;

import javax.swing.JTable;
import javax.swing.JTextField;

/**
 * JTable with overwritten editCellAt() method that 
 * requests focus for the editor component. This
 * is done because the caret of the editor component
 * would not be visible otherwise.
 * 
 * @author maltech
 *
 */
public class EditableJTable extends JTable{
	
	public boolean editCellAt(int row, int col , EventObject e){
		boolean result = super.editCellAt(row, col, e);
		Component c = this.getEditorComponent();
		if (c instanceof JTextField){
			JTextField t = (JTextField) c;
			t.selectAll();
		}
		
		if (c != null)
			c.requestFocus();
		
		return result;
	}
	
}
