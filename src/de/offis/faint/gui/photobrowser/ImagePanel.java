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

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import de.offis.faint.controller.MainController;
import de.offis.faint.data.RessourceLoader;
import de.offis.faint.global.Constants;
import de.offis.faint.gui.MainFrame;
import de.offis.faint.gui.events.EventDeleteRegion;
import de.offis.faint.gui.events.EventRecognizeFace;
import de.offis.faint.gui.events.EventRegionSelectionChanged;
import de.offis.faint.gui.events.EventUpdateAnnotationPanel;
import de.offis.faint.model.ImageModel;
import de.offis.faint.model.Region;


public class ImagePanel extends JLayeredPane{
	
	private static final long serialVersionUID = 4626999742000707324L;
	static final int ARC_SIZE = 16; // size of arc above selected region
	
	private JInternalFrame iFrame = new JInternalFrame( "Edit Region Properties", true, true, true, false);
	private ClassificationPanel classificationPanel;
	private JButton addPersonButton = new JButton("New Person");
	private JButton recognizeButton = new JButton("Recognize");
	private JButton deleteRegionButton = new JButton("Delete Region");
	
	protected ImageModel currentImageModel;
	private Image currentImage;
	protected Region currentRegion = null;
	
	// Points used to describe the rectangle that will become a manually created region
	private Point newRegionA = null;
	private Point newRegionB = null;
	
	private MainFrame mainFrame;
	protected ImageViewBehaviour imageViewBehaviour;
	protected ImageCursorBehaviour imageCursorBehaviour;
	
	static enum Mode {NORMAL, RESHAPE, MOVE, CREATE};
	protected Mode mode = Mode.NORMAL;
	
	
	/**
	 * Constructor.
	 * 
	 */
	public ImagePanel(MainFrame mainFrame){
		this.mainFrame = mainFrame;
				
		// Layout
		this.setBorder(new BevelBorder(BevelBorder.LOWERED));
		
		// Init image viewing and cursor behaviour
		this.imageViewBehaviour = new ImageViewBehaviour(this);
		this.imageCursorBehaviour = new ImageCursorBehaviour(this);

		// Init internal classification frame
		JPanel container = new JPanel(new BorderLayout());
		classificationPanel = new ClassificationPanel(mainFrame);
		container.add(classificationPanel, BorderLayout.CENTER);
		JPanel buttons = new JPanel(new GridLayout());
		buttons.add(addPersonButton);
		buttons.add(recognizeButton);
		buttons.add(deleteRegionButton);
		container.add(buttons, BorderLayout.SOUTH);
		container.setOpaque(false);
		iFrame.setContentPane(container);
		iFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);		
		iFrame.setSize(360, 240);
		iFrame.setLocation(10,10);
		iFrame.setOpaque(false);
		this.add(iFrame);
		
		Listener listener = new Listener();
		addMouseListener(listener);
		addMouseMotionListener(listener);
		iFrame.addInternalFrameListener(listener);
		recognizeButton.addActionListener(listener);
		addPersonButton.addActionListener(listener);
		deleteRegionButton.addActionListener(listener);
	}
	
	
	@Override
	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		Graphics2D g = (Graphics2D) graphics;
		
		// Remember stroke
		Stroke defaultStroke = ((Graphics2D) g).getStroke();
		
		int previewWidth = this.imageViewBehaviour.getPanelWidth();
		int previewHeight = this.imageViewBehaviour.getPanelHeight();
		
		if (currentImageModel != null){
			
			int displayWidth = imageViewBehaviour.getImageViewWidth();
			int displayHeight = imageViewBehaviour.getImageViewHeight();
			int displayX = imageViewBehaviour.getImagePositionX();
			int displayY = imageViewBehaviour.getImagePositionY();
			
			// Draw the image
			g.drawImage(currentImage, displayX, displayY, displayWidth, displayHeight, this);
			
			// Draw region windows
			if (MainController.getInstance().getFaceDB().getRegionsForImage(this.currentImageModel.getFile().toString()) != null) {
				double zoomFactor = imageViewBehaviour.getZoomFactor();
				
				// Draw rectangles for all regions
				g.setColor(new Color(255,0,0));
				g.setStroke(new BasicStroke(3));
				for (Region region : MainController.getInstance().getFaceDB().getRegionsForImage(this.currentImageModel.getFile().toString())){
					paintRegionWindow(g, region, displayX, displayY, zoomFactor);
				}
				
				// Draw selected region on top of other regions
				if (currentRegion != null)
					paintRegionWindow(g, currentRegion, displayX, displayY, zoomFactor);
			}
			
			// Draw new region if user is dragging mouse
			paintNewWindow(g);
						
			// Draw scan window if slider is active
			paintScanWindow(g, Math.min(displayHeight, displayWidth), previewWidth, previewHeight);
		}
		else {
			
			// Draw Watermark if there is no current image
			BufferedImage watermark = RessourceLoader.getWaterMark();
			g.drawImage(watermark, (previewWidth - watermark.getWidth())/2,(previewHeight - watermark.getHeight())/2, null);
			
			// Draw scan window if slider is active
			paintScanWindow(g, Math.min(previewHeight, previewWidth), previewWidth, previewHeight);
		}

		// Restore stroke
		g.setStroke(defaultStroke);
	}
	
	private void paintRegionWindow(Graphics2D g, Region r, int displayX, int displayY, double zoomFactor){
		int x = displayX + (int)(Math.round(r.getX()*zoomFactor));
		int y = displayY + (int)(Math.round(r.getY()*zoomFactor));
		
		double width = r.getWidth() * zoomFactor;
		double height = r.getHeight() * zoomFactor;
		
		// Do tranformation
		g.translate(x,y);
		g.rotate(Math.toRadians(- r.getAngle()));
		
		// Draw rectangle
		g.drawRect((int) Math.round(-width / 2), (int) Math.round(-height / 2),
				   (int) Math.round(width), (int) Math.round(height));
		
		// Draw reshape-circle if region is selected
		if (r.equals(currentRegion)){
			g.drawArc(-ARC_SIZE/2, -(int) Math.round(((double)height)/2) -ARC_SIZE/2,ARC_SIZE,ARC_SIZE,0,180);			
		}

		// Undo transformation
		g.rotate(Math.toRadians(r.getAngle()));
		g.translate(-x,-y);
		
		// Draw inner yellow shapes if region is selected
		if (r.equals(currentRegion) && g.getColor()!=Color.YELLOW){
			Color c = g.getColor();
			Stroke s = g.getStroke();
			g.setColor(Color.YELLOW);
			g.setStroke(new BasicStroke(1));
			paintRegionWindow(g,r,displayX, displayY, zoomFactor);
			g.setColor(c);
			g.setStroke(s);
		}
	}
	
	
	
	private void paintScanWindow(Graphics2D g, int maxSize, int previewWidth, int previewHeight){
		ScanWindowSlider slider = mainFrame.browserTab.getScanWindowSlider();
		if (slider.isActive() || slider.hasFocus()){
			int size = (int) Math.round((double) maxSize * ((double) MainController.getInstance().getScanWindowSize())/100);
			int x = (int) Math.round(((double)(previewWidth-size))/2)+1;
			int y = (int) Math.round(((double)(previewHeight-size))/2)+1;

			g.setColor(Color.BLUE);
			g.setStroke(new BasicStroke(3));		
			g.drawRect(x,y, size, size);

			if (slider.hasFocus()){
				g.setColor(Color.LIGHT_GRAY);
				g.setStroke(new BasicStroke(1));		
				g.drawRect(x,y, size, size);
			}
		}
	}	
	
	private void paintNewWindow(Graphics2D g){
		
		if (newRegionA != null){
			int x = Math.min(newRegionA.x, newRegionB.x);
			int y = Math.min(newRegionA.y, newRegionB.y);
			int w = Math.abs(newRegionA.x - newRegionB.x);
			int h = Math.abs(newRegionA.y - newRegionB.y);
			
			g.setColor(Color.WHITE);
			g.setStroke(new BasicStroke(3));
			g.drawRect(x,y,w,h);
			
			g.setColor(Color.BLACK);
			g.setStroke(new BasicStroke(1));		
			g.drawRect(x,y,w,h);
		}
	}	
	
	public void setImage(ImageModel imageModel) {
		if (imageModel != null) {
			currentImage = imageModel.getImage(true);
		}
		
		this.currentImageModel = imageModel;
		
		if (imageModel == null || (currentRegion != null && !currentRegion.getImage().equals(imageModel.getFile().getPath()))) {
			setSelectedRegion(null);
		}
	
		this.repaint();
	}
	
	public void setSelectedRegion(Region newRegion) {
		this.currentRegion = newRegion;
		
		if (newRegion == null){
			iFrame.setVisible(false);
		}
		else{
			classificationPanel.showRegion(currentImageModel, newRegion);
			iFrame.setVisible(true);
		}
		this.repaint();
	}
	
	public ClassificationPanel getClassificationPanel(){
		return classificationPanel;
	}
	
	/**
	 * Used to check if cursor is inside of the arc above the selected region.
	 * Only if point p is outside of the selected region AND inside of the circle
	 * then p is inside of the arc.
	 * 
	 * @param p
	 * @return
	 */
	protected boolean pointInsideUpperRegionCircle(Point p){
		int x = currentRegion.getX() - p.x;
		int y = currentRegion.getY() - p.y;
		double angle = Math.toRadians(currentRegion.getAngle());
		
		double finalX = x * Math.cos(angle) + y * -Math.sin(angle);
		double finalY = x * Math.sin(angle) + y * Math.cos(angle) - (currentRegion.getHeight()/2);
		double arcRadius = ((double)ARC_SIZE * 0.5) / imageViewBehaviour.getZoomFactor();
		
		return (finalX * finalX + finalY * finalY < arcRadius*arcRadius);
	}
	

	class Listener extends MouseAdapter implements InternalFrameListener, ActionListener {
		
		private boolean currentRegionCacheInvalid = false;
		private Point movingDelta = new Point();
		private int radiusDelta = 0;
		
		/*
		 * Used to update the cursor.
		 *  
		 *  (non-Javadoc)
		 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
		 */
		public void mouseMoved(MouseEvent e) {
			imageCursorBehaviour.updateCursor(e.getPoint());
		}
		
		/*
		 * Used to switch from NORMAL to one of the other modes.
		 * 
		 *  (non-Javadoc)
		 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
		 */
		public void mousePressed(MouseEvent e){
			Point p = imageViewBehaviour.viewToModel(e.getPoint());

			if (currentRegion!= null && currentRegion.containsPoint(p)){
				mode = Mode.MOVE;
				movingDelta.setLocation(currentRegion.getX() - (int) Math.round(p.getX()),
                        currentRegion.getY() - (int) Math.round(p.getY()));

			}
			else if (currentRegion!= null && pointInsideUpperRegionCircle(p)){
				mode = Mode.RESHAPE;
				
				int deltaX = currentRegion.getX() - (int) Math.round(p.getX());
				int deltaY = currentRegion.getY() - (int) Math.round(p.getY());
				radiusDelta = (int)Math.abs(Math.sqrt(deltaX*deltaX + deltaY*deltaY)-(currentRegion.getHeight()/2));
			}
			else {
				mode = Mode.CREATE;
				mainFrame.eventDispatcher.dispatchEvent(new EventRegionSelectionChanged(null));				
			}
		}
		
		/*
		 * Used to move or reshape the current region and to specify the rectangle
		 * points for a new region.
		 *  
		 *  (non-Javadoc)
		 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
		 */
		public void mouseDragged(MouseEvent e){ 
			
			Point p = imageViewBehaviour.viewToModel(e.getPoint());

			switch (mode){
			case MOVE:
				currentRegion.setPosition(new Point(p.x + movingDelta.x, p.y + movingDelta.y));
				handleRegionChange();				
				break;
				
			case RESHAPE:
				int deltaX = currentRegion.getX() - (int) Math.round(p.getX());
				int deltaY = currentRegion.getY() - (int) Math.round(p.getY());
				
				double radius = Math.sqrt(deltaX*deltaX + deltaY*deltaY);

				if (radius == 0.0){
					currentRegion.setHeight(1);
					currentRegion.setWidth(1);
				}
				else {
					
					int size = 2 * (int) Math.round(radius-radiusDelta);
					currentRegion.setHeight(size);
					currentRegion.setWidth(size);
					
					double angle = Math.toDegrees(Math.asin(((double)deltaX)/radius));
					if (deltaY < 0) angle = 180 - angle;
					currentRegion.setAngle((360 + angle)%360);
				}
				handleRegionChange();

				break;
				
			case CREATE:
				
				// prepare rectangle for new region
				if (newRegionA == null){
					newRegionA = new Point(e.getPoint());
				}
				
				newRegionB = e.getPoint();
			}
			
			repaint();			
			imageCursorBehaviour.updateCursor(e.getPoint());
		}
		
		private void handleRegionChange(){
			
			// prevent region from beeing cached to disk on every move of cursor
			if (!currentRegionCacheInvalid && currentRegion.getCachedFile() != null){
				currentRegionCacheInvalid = true;
				currentRegion.deleteCachedFile();
			}
			
			// update view
			mainFrame.eventDispatcher.dispatchEvent(new EventUpdateAnnotationPanel());
			classificationPanel.updateImage();
		}

		/*
		 * Used to create a new region from the specified rectangle points
		 * and to rebuild of a moved region that has been previously cached.
		 * 
		 *  (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
		 */
		public void mouseReleased(MouseEvent e){
			requestFocus();

			// rebuild cached image if previously cached region has been moved
			if (currentRegionCacheInvalid){
				currentRegionCacheInvalid = false;
				try {
					currentRegion.cacheToDisk();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			
			// create new region from manually selected rectangle
			if (newRegionA != null){
				
				Point p = new Point((newRegionA.x + newRegionB.x)/2,(newRegionA.y + newRegionB.y)/2);
				p = imageViewBehaviour.viewToModel(p);
				
				double size = Math.max(Math.abs(newRegionA.x-newRegionB.x), Math.abs(newRegionA.y-newRegionB.y));
				size/=imageViewBehaviour.getZoomFactor();
				
				Region r = new Region((int) Math.round(p.x),
						              (int) Math.round(p.y),
						              (int) Math.round(size),
						              (int) Math.round(size),0,
						              currentImageModel.getFile().toString());
				
				r.setUsedForTraining(false);
				
				newRegionA = null;
				
				MainController.getInstance().getFaceDB().put(r,Constants.UNKNOWN_FACE);				
				mainFrame.eventDispatcher.dispatchEvent(new EventUpdateAnnotationPanel());
				mainFrame.eventDispatcher.dispatchEvent(new EventRegionSelectionChanged(r));
			}
			
			mode = Mode.NORMAL;
		}
		
		/*
		 * Used to change the selected region and request focus.
		 * 
		 *  (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 */
		public void mouseClicked(MouseEvent e){
			requestFocus();
			if (currentImageModel != null){
				Region r = currentImageModel.getRegionAtPoint(imageViewBehaviour.viewToModel(e.getPoint()));
				mainFrame.eventDispatcher.dispatchEvent(new EventRegionSelectionChanged(r));
			}
			imageCursorBehaviour.updateCursor(e.getPoint());
		}
				
		/* 
		 * Used for buttons in internal frame.
		 * 
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == deleteRegionButton){
				mainFrame.eventDispatcher.dispatchEvent(new EventDeleteRegion(currentRegion));
			}
			else if (e.getSource() == recognizeButton){
				mainFrame.eventDispatcher.dispatchEvent(new EventRecognizeFace(currentRegion));								
			}
			else if (e.getSource() == addPersonButton){
				classificationPanel.getClassificationTable().changeSelection(0,0, false, false);
				classificationPanel.getClassificationTable().editCellAt(0,0);
			}
		}
		
		/*
		 * Used to deselect region if internal frame is closed.
		 * 
		 *  (non-Javadoc)
		 * @see javax.swing.event.InternalFrameListener#internalFrameClosing(javax.swing.event.InternalFrameEvent)
		 */
		public void internalFrameClosing(InternalFrameEvent e) {
			mainFrame.eventDispatcher.dispatchEvent(new EventRegionSelectionChanged(null));
		}

		
		// unused methods
		public void internalFrameClosed(InternalFrameEvent e) {}
		public void internalFrameOpened(InternalFrameEvent e) {}
		public void internalFrameIconified(InternalFrameEvent e) {}
		public void internalFrameDeiconified(InternalFrameEvent e) {}
		public void internalFrameActivated(InternalFrameEvent e) {}
		public void internalFrameDeactivated(InternalFrameEvent e) {}
	}

}
