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

package de.offis.faint.gui.preferences;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.html.HTMLEditorKit;

import de.offis.faint.controller.HotSpotController;
import de.offis.faint.gui.MainFrame;
import de.offis.faint.interfaces.IModule;
import de.offis.faint.interfaces.ISwingCustomizable;

/**
 * @author maltech
 *
 */
public class FilterPanel extends JPanel {
	
	private static final long serialVersionUID = -185379808184742307L;
	
	private JScrollPane optionScroller;
	private JEditorPane htmlPane;
	private JTable	table;
	private JPanel body;
	private HotSpotController hotSpot;
	private MainFrame mainFrame;
	public FilterPanel(String title, HotSpotController hotspot, MainFrame mainFrame){
		super(new BorderLayout());
		this.setBorder(new TitledBorder(title));
		
		this.hotSpot = hotspot;		
		this.mainFrame = mainFrame;
		
		this.table = new JTable(new FilterTableModel());
				
		table.setTableHeader(null);

		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		table.getColumnModel().getColumn(0).setMaxWidth(5);
		table.getColumnModel().getColumn(0).setMinWidth(5);
		table.setRowMargin(0);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setIntercellSpacing(new Dimension(0,0));
		table.setShowGrid(false);
		table.getSelectionModel().addListSelectionListener(new Listener());

		JPanel tableContainer = new JPanel(new BorderLayout());
		tableContainer.setBorder(new EmptyBorder(5,5,5,5));
		tableContainer.add(table, BorderLayout.CENTER);
		
		JPanel filterPanel = new JPanel(new BorderLayout());
		filterPanel.setBorder(new TitledBorder("Active Filters"));
		filterPanel.add(tableContainer, BorderLayout.CENTER);
		this.add(filterPanel, BorderLayout.NORTH);
		
		// Init htmlPane
		htmlPane = new JEditorPane();
		htmlPane.setEditorKit(new HTMLEditorKit());
		htmlPane.setEditable(false);
		JScrollPane htmlScroller = new JScrollPane(htmlPane);
		htmlScroller.setPreferredSize(new Dimension(0,150));
		htmlScroller.setBorder(new BevelBorder(BevelBorder.LOWERED));
		JPanel htmlPanel = new JPanel(new GridLayout());
		htmlPanel.setBorder(new TitledBorder("Filter Information"));
		htmlPanel.add(htmlScroller);
		
		// Init option ScrollPane
		optionScroller = new JScrollPane();
		optionScroller.setBorder(new TitledBorder("Filter Settings"));
		optionScroller.setViewportBorder(new LineBorder(Color.LIGHT_GRAY));
		
		// Put it all together
		this.body = new JPanel(new GridLayout());
		body.add(htmlPanel, BorderLayout.CENTER);
		body.add(optionScroller, BorderLayout.EAST);
		
		this.add(body, BorderLayout.CENTER);
		
		// Fill layout with content
		updateBody();
		 if (table.getRowCount()>0)
			 table.getSelectionModel().setSelectionInterval(0,0);
	}
	
	private void updateBody(){
		
		
		if (table.getSelectedRow() == -1){
			body.setVisible(false);
			return;
		}
		body.setVisible(true);
		
		IModule mudule = (IModule) hotSpot.getAvailableFilters()[table.getSelectedRow()];

		String html = "";
		html += "<h2>Filter Description</h2>" + mudule.getDescription() +"<br>";
		html += "<h2>Copyright Notes</h2>" + mudule.getCopyrightNotes() + "<br>";
		this.htmlPane.setText(html);
		
		Component view = null;
		htmlPane.setCaretPosition(0); // resets the scrollbars
		if (mudule instanceof ISwingCustomizable){
			view = ((ISwingCustomizable)mudule).getSettingsPanel();
		}
		else {
			view = new JLabel("No Settings available.");
			((JLabel) view).setHorizontalAlignment(SwingConstants.CENTER);
		}
		mainFrame.addIsolatedComponent(view);
		this.optionScroller.setViewportView(view);
	}
	
	private class Listener implements ListSelectionListener{

		/* (non-Javadoc)
		 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
		 */
		public void valueChanged(ListSelectionEvent e) {
			updateBody();			
		}
		
	}
	
	class FilterTableModel extends DefaultTableModel {
		
		private static final long serialVersionUID = 1264713805836063245L;
		
		private static final int CHECKBOX = 0;
		private static final int FILTER_NAME = 1;
		
		public Class getColumnClass(int columnIndex)
		{
			if (columnIndex == CHECKBOX)
				return Boolean.class;
			return String.class;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		public int getRowCount() {
			if (hotSpot==null ||hotSpot.getAvailableFilters() == null) return 0;
			return hotSpot.getAvailableFilters().length;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnCount()
		 */
		public int getColumnCount() {
			return 2;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnName(int)
		 */
		public String getColumnName(int columnIndex) {
			if (columnIndex == CHECKBOX)
				return "Active";
			return "Filter Name";	
		}


		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#isCellEditable(int, int)
		 */
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if (columnIndex == CHECKBOX)
				return true;
			return false;
		}


		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		public Object getValueAt(int rowIndex, int columnIndex) {
			Object o = hotSpot.getAvailableFilters()[rowIndex];
			if (columnIndex == CHECKBOX)
				return hotSpot.getActiveFilters().contains(o);
			return ((IModule) o).getName();
		}


		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
		 */
		@SuppressWarnings("unchecked")
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			Boolean b = (Boolean) aValue;
			hotSpot.setFilterStatus(hotSpot.getAvailableFilters()[rowIndex], b);			
		}
	}
}
