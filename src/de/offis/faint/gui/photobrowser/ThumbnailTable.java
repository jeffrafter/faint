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

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import de.offis.faint.data.RessourceLoader;
import de.offis.faint.global.Constants;
import de.offis.faint.global.Utilities;
import de.offis.faint.gui.MainFrame;
import de.offis.faint.gui.events.EventOpenImage;
import de.offis.faint.model.ImageModel;

/**
 * @author maltech
 *
 */
public class ThumbnailTable extends JTable{
	
	private MainFrame mainFrame;

	public ThumbnailTable(MainFrame mainFrame){
		
		this.mainFrame = mainFrame;
		this.setModel(new ThumbnailTableModel(this));
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		this.setRowHeight(Constants.THUMBNAIL_SIZE.height + Constants.THUMBNAIL_MARGIN);
		this.getColumnModel().getColumn(0).setMaxWidth(Constants.THUMBNAIL_SIZE.width + Constants.THUMBNAIL_MARGIN);
		this.getColumnModel().getColumn(0).setMinWidth(Constants.THUMBNAIL_SIZE.width + Constants.THUMBNAIL_MARGIN);		
		this.setIntercellSpacing(new Dimension(0,0));
		this.setShowGrid(false);
		this.setTableHeader(null);
		this.setDefaultRenderer(Object.class, new ThumbnailTableRenderer());
		
		ThumbnailTableListener listener = new ThumbnailTableListener(this);
		this.getSelectionModel().addListSelectionListener(listener);
	    this.getColumnModel().getSelectionModel().addListSelectionListener(listener);
	}
	
	public ThumbnailTableModel getThumbnailTableModel(){
		return (ThumbnailTableModel) this.getModel();
	}


static class ThumbnailTableModel extends AbstractTableModel{
	
	private ArrayList<ImageModel> images = null;
	private ThumbnailTable table;
	private ThumbnailLoadingThread thumbnailloadingThread = new ThumbnailLoadingThread(this);

	private ImageIcon thumbPlaceholder;
	
	public ThumbnailTableModel(ThumbnailTable table){
		
		this.table = table;
		
		try {
			thumbPlaceholder = new ImageIcon(ImageIO.read(RessourceLoader.getFile("thumb.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		if (this.images == null) return 0;
		else return this.images.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return 2;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		ImageModel image = this.images.get(rowIndex);
		switch (columnIndex){
		case 0:
			if (image.getTumbnail()==null) return thumbPlaceholder;
			else return image.getTumbnail();
		case 1:
			if (image.getWidth()==null||image.getHeight()==null)
				  return "<html><b>"+image.getFileName()+"</b><br>"
			        +"unknown size"
			        +"</html>";
			else
				  return "<html><b>"+image.getFileName()+"</b><br>"
			        +image.getWidth()+" x "+image.getHeight()
			        +"</html>";
		}
		return null;
	}

	/**
	 * @param number
	 * @return
	 */
	public ImageModel getImage(int number) {
		if (number >= 0 && images.size()>number)
		  return images.get(number);
		else
	      return null;
	}


	public void setFolder(File folder) {
		thumbnailloadingThread.clearRequestedThumbNails();
		File[] imageFiles = folder.listFiles(new Utilities.FileTypeFilter(Constants.IMAGE_SUFFIXES));
		this.images = new ArrayList<ImageModel>(imageFiles.length);
		for (File imageFile : imageFiles){
			ImageModel imageModel = new ImageModel(imageFile);
			this.images.add(imageModel);
			this.thumbnailloadingThread.preloadThumbnail(imageModel);
		}
		fireTableDataChanged();
	}
	
	public void reportThumbnailUpdate(){
		table.repaint();
	}
}
	
	static class ThumbnailTableRenderer extends DefaultTableCellRenderer {
		
		public void setValue(Object value) {
			if (value instanceof Icon) {
				setIcon((Icon) value);
				setText(null);
				setHorizontalAlignment(JLabel.CENTER);
				} else{
				setIcon(null);
				setHorizontalAlignment(JLabel.LEFT);
				super.setValue(value);
			}
			this.setBorder(null);
		}
	}

	class ThumbnailTableListener implements ListSelectionListener{

		private ThumbnailTable table;
	    
	    public ThumbnailTableListener(ThumbnailTable table) {
	        this.table = table;
	    }
	    
	    public void valueChanged(ListSelectionEvent e) {
	    	int selectedRow = table.getSelectedRow();
	    	if (selectedRow !=-1 && !e.getValueIsAdjusting());
	        	mainFrame.eventDispatcher.dispatchEvent(new EventOpenImage(table.getThumbnailTableModel().getImage(selectedRow)));
	    }
	}

}
