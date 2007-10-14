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
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import de.offis.faint.controller.MainController;
import de.offis.faint.global.Constants;
import de.offis.faint.gui.MainFrame;
import de.offis.faint.gui.events.EventAddKnownPerson;
import de.offis.faint.gui.events.EventClassifyRegion;
import de.offis.faint.gui.photobrowser.ClassificationPanel.ClassificationTableModel.ClassificationTableRow;
import de.offis.faint.gui.tools.JTableHeaderEventGenerator;
import de.offis.faint.gui.tools.JTableHeaderEventGenerator.JTableWithHeaderListener;
import de.offis.faint.model.ImageModel;
import de.offis.faint.model.Region;

/**
 * @author maltech
 *
 */
public class ClassificationPanel extends JPanel{
	
	private static final long serialVersionUID = 443891291865709400L;
	
	private JLabel iconLabel = new JLabel();
	private ClassificationTable classificationTable = new ClassificationTable();
	private ClassificationTableModel classificationTableModel = new ClassificationTableModel(classificationTable);
	private MainFrame mainFrame;
	private Region currentRegion = null;

	
	private static final Color TRANSLUCENT_COLOR = new Color(255,255,255,200);
	
	
	public ClassificationPanel(MainFrame mainFrame){
		super(new BorderLayout());
	
		this.mainFrame = mainFrame;

//		iconLabel.setBackground(TRANSLUCENT_COLOR);
		
		classificationTable.setDefaultRenderer(Object.class, new ClassificationTableRenderer());
		classificationTable.setModel(classificationTableModel);
		classificationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		classificationTable.setOpaque(false);
		classificationTable.setBackground(new Color(0,0,0,0));

		this.add(iconLabel, BorderLayout.WEST);
		
		JScrollPane scrollpane = new JScrollPane();
		scrollpane.setViewport(new JViewport(){
			
			public void paintComponent(Graphics g){
				this.setOpaque(true);
				super.paintComponent(g);
			}
			
			protected void paintBorder(Graphics g){
				this.setOpaque(true);
				this.setBackground(Color.WHITE);
				super.paintBorder(g);
				this.setBackground(TRANSLUCENT_COLOR);
			}

			@Override
			protected void paintChildren(Graphics g) {
				this.setOpaque(false);
				super.paintChildren(g);
			}			
		});
		scrollpane.setViewportView(classificationTable);
		this.add(scrollpane, BorderLayout.CENTER);

		iconLabel.setVerticalAlignment(JLabel.TOP);
		iconLabel.setBorder(new TitledBorder("Face"));
		iconLabel.setOpaque(true);

//		scrollpane.setBackground(TRANSLUCENT_COLOR);
		scrollpane.getViewport().setBackground(TRANSLUCENT_COLOR);
		
//		JPanel corner = new JPanel();
//		corner.setBackground(TRANSLUCENT_COLOR);
		JPanel corner = new JPanel();
		scrollpane.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, corner);
		scrollpane.setBorder(new LineBorder(Color.LIGHT_GRAY,1));
		scrollpane.setOpaque(false);

		this.setOpaque(false);
		
//		classificationTable.getTableHeader().setOpaque(false);
//		classificationTable.getTableHeader().setBackground(TRANSLUCENT_COLOR);
		
		new JTableHeaderEventGenerator(classificationTable);
	}
	
	public synchronized void showRegion(ImageModel image, Region region){
		
		if (!region.equals(this.classificationTableModel.getRegion())){
			this.currentRegion = region;

			iconLabel.setIcon(new ImageIcon(region.toThumbnail(Constants.FACE_THUMBNAIL_SIZE.width, Constants.FACE_THUMBNAIL_SIZE.height)));
			classificationTableModel.setRegion(region);
			int selectedRow = classificationTableModel.getRowForValue(MainController.getInstance().getFaceDB().getAnnotation(region));
			classificationTable.changeSelection(selectedRow, 0, false, false);
			classificationTable.repaint();
		}
	}
	
	public void updateRecognitionData(HashMap<String, Integer> recognitionPoints){

		for (ClassificationTableRow row : this.classificationTableModel.rows){
			
			if (recognitionPoints.containsKey(row.annotation)){
				row.recognitionPoints = recognitionPoints.get(row.annotation);
			}
		}
		this.classificationTableModel.sortByColumn(ClassificationTableModel.POINT_COLUMN);
		this.classificationTable.repaint();
	}
	
	public void updateFromModel(){
		((ClassificationTableModel) classificationTable.getModel()).updateAnnotations();
	}
	
	public void updateImage(){
		this.iconLabel.setIcon(new ImageIcon(currentRegion.toThumbnail(Constants.FACE_THUMBNAIL_SIZE.width, Constants.FACE_THUMBNAIL_SIZE.height)));
	}


	public class ClassificationTableModel implements TableModel{
		
		private JTable table;
		private ArrayList<ClassificationTableRow> rows = new ArrayList<ClassificationTableRow>();
		
		private Region currentRegion;
		
		public final static int NAME_COLUMN = 0;
		public final static int POINT_COLUMN = 1;
		private int sortColumn = NAME_COLUMN;

		public class ClassificationTableRow {
			String annotation;
			Integer recognitionPoints = 0;
			int rowIndex;
		}

		public ClassificationTableModel(JTable table){
			this.table = table;
			updateAnnotations();
		}
		
		/**
		 * @return
		 */
		public Region getRegion() {
			return this.currentRegion;
		}

		public void setRegion(Region r){
			if (this.currentRegion == null || !this.currentRegion.equals(r)){
				this.currentRegion = r;
				updateAnnotations();
			}
		}
		
		private synchronized void updateAnnotations(){
			this.rows.clear();
			String[] knownAnnotations = MainController.getInstance().getFaceDB().getExistingAnnotations();
			this.rows.ensureCapacity(knownAnnotations.length);
			
			ClassificationTableRow unknownRow = new ClassificationTableRow();
			unknownRow.annotation = Constants.UNKNOWN_FACE;
			unknownRow.recognitionPoints = null;
			unknownRow.rowIndex = 0;
			this.rows.add(unknownRow);
			
			for(int i = 0; i<knownAnnotations.length; i++){
				ClassificationTableRow newRow = new ClassificationTableRow();
				newRow.annotation = knownAnnotations[i];
				newRow.rowIndex = i+1;
				this.rows.add(newRow);
			}
			
			this.sortByColumn(this.sortColumn);
		}
		
		public synchronized void sortByColumn(int column){
			
			String selectedName = (String) this.getValueAt(this.table.getSelectedRow(), NAME_COLUMN);
			
			sortColumn = column;
			
			if (rows.size()>1){
				switch (column){
				case NAME_COLUMN:
					String[] names = new String[rows.size()-1];
					int counter = 0;
					for (ClassificationTableRow row : rows){
						if (!row.annotation.equals(Constants.UNKNOWN_FACE))
							names[counter++] = row.annotation;
						else
							row.rowIndex = 0; // ensure "unknown" to be the first row
					}
					Arrays.sort(names);
					for (int i = 0; i<names.length; i++){
						loop : for(ClassificationTableRow row : rows){
							if(row.annotation.equals(names[i])){
								row.rowIndex = i+1;
								break loop;
							}
						}
					}
					break;
					
				case POINT_COLUMN:
					int[] points = new int[rows.size()-1];
					int count = 0;
					for (ClassificationTableRow row : rows){
						
						if (!row.annotation.equals(Constants.UNKNOWN_FACE)){
							points[count++] = row.recognitionPoints;
						    row.rowIndex = -1;
						}
						else
							row.rowIndex = 0; // ensure "unknown" to be the first row
					}
					Arrays.sort(points);
					for (int i = 0; i < points.length; i++){
						loop : for(ClassificationTableRow row : rows){
							if(row.rowIndex < 0 && row.recognitionPoints == (points[i])){
								row.rowIndex = points.length-i;
								break loop;
							}
						}						
					}
					break;
															
				}
				if (selectedName != null)
					table.getSelectionModel().setSelectionInterval(this.getRowForValue(selectedName),this.getRowForValue(selectedName));

			}
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		public int getRowCount() {
			return rows.size();
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
			switch (columnIndex){
			case NAME_COLUMN: return "Person";
			case POINT_COLUMN: return "Recognition Score";
			}
			return null;
		}
		
		public int getRowForValue(String value){
			for(int i = 0; i< this.rows.size(); i++){
				if (this.rows.get(i).annotation.equals(value)) return this.rows.get(i).rowIndex;
			}
			return -1;			
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnClass(int)
		 */
		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#isCellEditable(int, int)
		 */
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if (rowIndex == 0 && columnIndex == 0)
				return true;
			else
				return false;
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		public Object getValueAt(int rowIndex, int columnIndex) {
			for (int i = 0; i<rows.size(); i++){
				
				if(rows.get(i).rowIndex == rowIndex) switch(columnIndex){
				case NAME_COLUMN:
					return rows.get(i).annotation;
				case POINT_COLUMN:
					if (rows.get(i).recognitionPoints==null) return "-";
					return rows.get(i).recognitionPoints;
				}
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
		 */
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			if (!aValue.equals(Constants.UNKNOWN_FACE) && !aValue.equals("")){
				  mainFrame.eventDispatcher.dispatchEvent(new EventAddKnownPerson(aValue.toString()));
			  updateAnnotations();
			  int row = this.getRowForValue(aValue.toString());
			  table.changeSelection(row,row,false,false);
			  
          	Region region = ((ClassificationTableModel)table.getModel()).getCurrentRegion();

			  mainFrame.eventDispatcher.dispatchEvent(new EventClassifyRegion(region, aValue.toString()));
			}
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#addTableModelListener(javax.swing.event.TableModelListener)
		 */
		public void addTableModelListener(TableModelListener l) {}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#removeTableModelListener(javax.swing.event.TableModelListener)
		 */
		public void removeTableModelListener(TableModelListener l) {}

		/**
		 * @return
		 */
		public Region getCurrentRegion() {
			return this.currentRegion;
		}		
	}

	public class ClassificationTable extends JTableWithHeaderListener{

		public ClassificationTable(){
			this.addMouseListener(new Listener(this));
		}


		@Override
		public void columnClicked(int colIndex) {
			((ClassificationTableModel)this.getModel()).sortByColumn(colIndex);
		}
		
	}
	
	public class Listener extends MouseAdapter{
		private JTable table;
		
		public Listener(ClassificationTable table){
			this.table = table;
		}
		
		public synchronized void mouseClicked(MouseEvent e){
			if (e.getClickCount() == 2)
	        {
	            int row = table.rowAtPoint(e.getPoint());
	            if (row > 0){
	            	Region region = ((ClassificationTableModel)table.getModel()).getCurrentRegion();
	            	String annotation = table.getValueAt(row, ClassificationTableModel.NAME_COLUMN).toString();
	            	mainFrame.eventDispatcher.dispatchEvent(new EventClassifyRegion(region, annotation));
	            }
	        }
		}
	}
	
	public class ClassificationTableRenderer extends DefaultTableCellRenderer implements TableCellRenderer{

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			if (column == ClassificationTableModel.POINT_COLUMN)
				this.setHorizontalAlignment(SwingConstants.CENTER);
			else
				this.setHorizontalAlignment(SwingConstants.LEFT);

			
			String name = value.toString();
			
			// Check if this is the current classification
			ClassificationTableModel model = (ClassificationTableModel) table.getModel();
			if (value.equals(MainController.getInstance().getFaceDB().getAnnotation(model.currentRegion))){
				name = "<b>"+name+"</b>";
			}

			// Check if this is the unknown face cell
			if (Constants.UNKNOWN_FACE.equals(value)){
				name = "<i>"+name+"</i>";
			}

			setText("<html>"+name+"<html>");
			
			return this;
		}
	}

	public ClassificationTable getClassificationTable() {
		return classificationTable;
	}
}
