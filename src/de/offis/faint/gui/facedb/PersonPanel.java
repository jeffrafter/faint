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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import de.offis.faint.controller.MainController;
import de.offis.faint.global.Constants;
import de.offis.faint.gui.MainFrame;
import de.offis.faint.gui.events.EventDeletePerson;
import de.offis.faint.gui.events.EventRenamePerson;
import de.offis.faint.gui.events.EventShowFacesOfPerson;
import de.offis.faint.gui.tools.EditableJTable;

/**
 * @author maltech
 *
 */
public class PersonPanel  extends JScrollPane{
		
		private JTable table = new EditableJTable();
		private MainFrame mainFrame;
		
		private JPopupMenu popupMenu = new JPopupMenu();
		private JMenuItem miRemove = new JMenuItem("Delete");
		private JMenuItem miRename = new JMenuItem("Rename");
		
		
		public PersonPanel(MainFrame mainFrame){
			super();
			
			this.mainFrame = mainFrame;

			
			// Init table
			this.setViewportView(table);
			this.getViewport().setBackground(Color.WHITE);
			this.setBorder(new BevelBorder(BevelBorder.LOWERED));
			table.setModel(new TModel());
			table.setIntercellSpacing(new Dimension(0,0));
			table.setGridColor(Color.WHITE);
			table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
			// Init table sorter
	        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>();
	        table.setRowSorter( sorter );
	        sorter.setModel( table.getModel() );
	        sorter.toggleSortOrder(0);
	        
	        // Prepare context menu
	        popupMenu.add(miRemove);
	        popupMenu.add(miRename);
	        mainFrame.addIsolatedComponent(popupMenu);
	        
	        // Init listener
	        Listener listener = new Listener();
	        table.addMouseListener(listener);
			table.getSelectionModel().addListSelectionListener(listener);
	        miRemove.addActionListener(listener);
	        miRename.addActionListener(listener);
//	        miRemove.setMnemonic('D');
//	        miRename.setMnemonic('R');
//	        miRemove.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0));
//	        miRename.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT,0));
		}
		
		public void updateFromModel() {
			TModel tableModel = (TModel) table.getModel();
			String selectedPerson = null;
			try {
				selectedPerson = (String) table.getModel().getValueAt(table.getSelectedRow(),0);
			}
			catch (NullPointerException e){}
		    catch (ArrayIndexOutOfBoundsException e){}

			tableModel.update();
            
			if (selectedPerson != null) {
				int selectionRow = tableModel.getRowForValue(selectedPerson);
				if (selectionRow >= 0)
					table.setRowSelectionInterval(selectionRow, selectionRow);
			}
		}
				
		private class TModel extends DefaultTableModel{
			
			String[] knownPersons;

			@Override
			public boolean isCellEditable(int row, int column) {
				return true;
			}

			@Override
			public int getColumnCount() {
				return 1;
			}

			public void update() {
				this.knownPersons = MainController.getInstance().getFaceDB().getExistingAnnotations();
				this.fireTableDataChanged();
			}

			@Override
			public int getRowCount() {
				if (knownPersons == null) return 0;
				return knownPersons.length;
			}

			@Override
			public Object getValueAt(int row, int column) {
				return knownPersons[row];
			}
			
			public int getRowForValue(String value){
				for (int i = 0; i<knownPersons.length; i++){
					if (knownPersons[i].equals(value)) return i;
				}
				return -1;
			}

			@Override
			public String getColumnName(int column) {
				return "Name";
			}
			
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				if (!aValue.equals(Constants.UNKNOWN_FACE) && !aValue.equals("")){
				  mainFrame.eventDispatcher.dispatchEvent(new EventRenamePerson(getValueAt(rowIndex, columnIndex).toString(), aValue.toString()));
				  int row = table.getRowSorter().convertRowIndexToView(getRowForValue(aValue.toString()));
				  table.changeSelection(row,columnIndex,false,false);
				  repaint();
				}
			}
		}
		
		class Listener extends MouseAdapter implements ListSelectionListener, ActionListener{

			/* (non-Javadoc)
			 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
			 */
			public void valueChanged(ListSelectionEvent e) {
				
				if (!e.getValueIsAdjusting()){
					String person = null;
					if (table.getSelectedRow() != -1){
						person = (String) table.getValueAt(table.getSelectedRow(),0);
					}
					mainFrame.eventDispatcher.dispatchEvent(new EventShowFacesOfPerson(person));
				}	
			}
			
			public void mouseClicked(MouseEvent e){
				
				int rowIndex = table.rowAtPoint(e.getPoint());
				if (rowIndex >=0 && rowIndex < table.getRowCount())
					table.changeSelection(rowIndex, 0, false, false);
				
				if (e.getButton() == MouseEvent.BUTTON3)
				{
					popupMenu.setLocation(e.getLocationOnScreen());
					popupMenu.setVisible(true);
				}
			}

			/* (non-Javadoc)
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == miRename){
					table.editCellAt(table.getSelectedRow(), 0, e);
				}
				else if (e.getSource() == miRemove){
					mainFrame.eventDispatcher.dispatchEvent(new EventDeletePerson(table.getValueAt(table.getSelectedRow(),0).toString()));
				}
			}
		}
}
