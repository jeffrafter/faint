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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import de.offis.faint.controller.MainController;
import de.offis.faint.global.Constants;
import de.offis.faint.gui.MainFrame;
import de.offis.faint.gui.events.EventRegionSelectionChanged;
import de.offis.faint.model.ImageModel;
import de.offis.faint.model.Region;

/**
 * @author maltech
 *
 */
public class AnnotationTable extends JTable {
	
	public static final int FACE_ICON_ROW = 0;
	public static final int FACE_ANNOTATION_ROW = 1;
	private MainFrame mainFrame;
	
	private Region currentRegion = null;
	
	
	/**
	 *  Constructor.
	 *  
	 * @param mainFrame
	 */
	public AnnotationTable(MainFrame mainFrame){
		this.mainFrame = mainFrame;
		this.setModel(new AnnotationTableModel());
		this.getColumnModel().setColumnSelectionAllowed(true);
		this.setAutoCreateColumnsFromModel(true);
		this.getColumnModel().getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.setDefaultRenderer(Object.class, new AnnotationTableRenderer());
		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.setRowMargin(0);
		this.setRowSelectionAllowed(false);
		this.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.setIntercellSpacing(new Dimension(0,0));
		
		this.setShowGrid(false);
		this.setTableHeader(null);
		
		this.getColumnModel().getSelectionModel().addListSelectionListener(new AnnotationTableListener(this));	
	}
	
	/**
	 * @return
	 */
	public AnnotationTableModel getAnnotationModel() {
		return (AnnotationTableModel) this.getModel();
	}
		
	public void setSelectedRegion(Region region){
		Region oldRegion = currentRegion;
		currentRegion = region;
		if (oldRegion != currentRegion){
		if((oldRegion == null && region != null) ||
		   (oldRegion != null && !oldRegion.equals(region)))
			
			if (region == null)
				this.clearSelection();
			else{
				int column = this.getColumnIndex(region);
				this.changeSelection(0, column, false, false);
			}
		}
	}

	
	public Region getSelectedRegion(){
		return currentRegion;
	}

	private int getColumnIndex(Region r){
		for (int i = 0; i < this.getModel().getColumnCount(); i++){
			if (getModel().getValueAt(0, i).equals(r)) return i;
		}
		return -1;
	}

	/**
	 * @param currentImage
	 */
	public void setImage(ImageModel currentImage) {
		getAnnotationModel().setImageModel(currentImage);
		this.updateColumns();
	}
	
	public void updateFromModel() {
		int colCount = getColumnCount();		
		int col = getSelectedColumn();
		this.getColumnModel().getSelectionModel().setValueIsAdjusting(true);

		this.updateColumns();
		if (colCount != getColumnCount())
			this.getAnnotationModel().fireTableDataChanged();
		
//		this.getColumnModel().getSelectionModel().setValueIsAdjusting(false);

		if (col != this.getSelectedColumn()){
			while( col >=0){
				if (getAnnotationModel().getValueAt(0, col)!=null){
					this.setColumnSelectionInterval(col, col);
					return;
				}
				col--;
			}
		}
	}
	
	private void updateColumns(){
//		if (this.getAnnotationModel().getColumnCount()!= this.getColumnCount()){
			
			this.getAnnotationModel().initIcons();
			this.createDefaultColumnsFromModel();

			for (int i = 0; i < this.getColumnCount(); i++){
				this.getColumnModel().getColumn(i).setMinWidth(Constants.FACE_THUMBNAIL_SIZE.width + Constants.FACE_THUMBNAIL_MARGIN);
			}
//		}
//		else this.repaint();
	}
	
	class AnnotationTableListener implements ListSelectionListener{

		private AnnotationTable table;
	    
	    public AnnotationTableListener(AnnotationTable table) {
	        this.table = table;
	    }
	    
	    public void valueChanged(ListSelectionEvent e) {
	    	if (!e.getValueIsAdjusting()){
	    		Region region = null;
	    		if (table.getSelectedColumn()!=-1)
	    			region = (Region) table.getAnnotationModel().getValueAt(0, table.getSelectedColumn());
	    		if ((currentRegion != null && region == null) || (region != null && !region.equals(currentRegion))){
	    			mainFrame.eventDispatcher.dispatchEvent(new EventRegionSelectionChanged(region));
	    		}
	    	}
	    }
	}
	
	
	public class AnnotationTableModel extends AbstractTableModel{
		
		
		ImageModel imageModel = null;
		HashMap<String,ImageIcon> faceIcons = new HashMap<String,ImageIcon>();
		
		public void setImageModel(ImageModel model){
			this.imageModel = model;
			initIcons();
		}
		
		private void initIcons(){
			faceIcons.clear();
			
			if (imageModel != null){
				
				BufferedImage image = imageModel.getImage(false); // FIXME - cache problem?
				
				if (MainController.getInstance().getFaceDB().getRegionsForImage(imageModel.getFile().toString()) != null)
				for(Region r: MainController.getInstance().getFaceDB().getRegionsForImage(imageModel.getFile().toString())){
					BufferedImage thumb  = r.toThumbnail(Constants.FACE_THUMBNAIL_SIZE.width, Constants.FACE_THUMBNAIL_SIZE.height);
					ImageIcon icon = new ImageIcon(thumb);
					faceIcons.put(r.toString(), icon);
				}
			}
//			this.fireTableStructureChanged();
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		public int getColumnCount() {
			if (imageModel == null || MainController.getInstance().getFaceDB().getRegionsForImage(imageModel.getFile().toString()) == null) return 0;
			else return MainController.getInstance().getFaceDB().getRegionsForImage(imageModel.getFile().toString()).length;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnCount()
		 */
		public int getRowCount() {
			return 1;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (imageModel != null){
				Region[] regions = MainController.getInstance().getFaceDB().getRegionsForImage(imageModel.getFile().toString());
				if (regions!= null && columnIndex < regions.length)
					return regions[columnIndex];
			}
			return null;
		}	
	}
	
	public class AnnotationTableRenderer extends JPanel implements  TableCellRenderer {
		
		final int INNERHEIGHT = (Constants.FACE_THUMBNAIL_SIZE.height + Constants.FACE_THUMBNAIL_MARGIN);
		
		private JLabel currentIcon = new JLabel();
		private DefaultTableCellRenderer currentText = new DefaultTableCellRenderer();

		public AnnotationTableRenderer(){
			super(new BorderLayout());
			
			this.add(currentIcon, BorderLayout.CENTER);
			currentIcon.setHorizontalAlignment(SwingConstants.CENTER);
			
			this.add(currentText, BorderLayout.SOUTH);
			currentText.setHorizontalAlignment(SwingConstants.CENTER);			
		}

		
		/* (non-Javadoc)
		 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
		 */
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			
			// process row height
			if (table.getParent() != null){
				int scrollpaneHeight = (int) table.getParent().getHeight();
				if (table.getRowHeight()!=Math.max(INNERHEIGHT,scrollpaneHeight)){
					table.setRowHeight(Math.max(INNERHEIGHT,scrollpaneHeight));
					int border = Math.max(0,(int) Math.round(((double)scrollpaneHeight - INNERHEIGHT)/2));
					this.setBorder(new EmptyBorder(border,0,border,0));
				}
			}
			
			// process colors
			if (isSelected){
				this.setBackground(table.getSelectionBackground());
				currentText.setForeground(table.getSelectionForeground());
			}
			else{
				this.setBackground(table.getBackground());
				currentText.setForeground(table.getForeground());
			}
			
			// process icon and text
			Region region = (Region) value;
			if (region!=null){
				currentIcon.setIcon(((AnnotationTableModel)table.getModel()).faceIcons.get(region.toString()));
				currentText.setText(MainController.getInstance().getFaceDB().getAnnotation(region));
				currentText.setFont(table.getFont());
			}

			return this;
		}
	}
}
