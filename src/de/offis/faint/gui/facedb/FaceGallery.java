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

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import de.offis.faint.controller.MainController;
import de.offis.faint.data.RessourceLoader;
import de.offis.faint.global.Constants;
import de.offis.faint.gui.MainFrame;
import de.offis.faint.gui.events.EventModifyTrainingData;
import de.offis.faint.gui.events.EventRegionSelectionChanged;
import de.offis.faint.model.Region;

/**
 * @author maltech
 *
 */
public class FaceGallery extends JComponent {
	
	private BufferedImage iconCorner;
	private Dimension cornerDelta;

	private BufferedImage iconTraining;
	private BufferedImage iconNoTraining;
	private Dimension trainingDelta;
	
	private MainFrame mainFrame;

	
	private final static Dimension CELLSIZE = new Dimension(
			Constants.FACE_THUMBNAIL_SIZE.width	+ Constants.FACE_THUMBNAIL_MARGIN,
			Constants.FACE_THUMBNAIL_SIZE.height + Constants.FACE_THUMBNAIL_MARGIN);
	
	private Region[] faces;
	
	public FaceGallery(MainFrame mainFrame){
		this.setSize(super.getPreferredSize());
		
		this.mainFrame = mainFrame;
		
		// Prepare icon overlay images and corresponding values
		try {
			this.iconCorner = ImageIO.read(RessourceLoader.getFile("iconcorner.png"));
			this.iconTraining = ImageIO.read(RessourceLoader.getFile("training.png"));
			this.iconNoTraining = ImageIO.read(RessourceLoader.getFile("notraining.png"));
			
			cornerDelta = new Dimension(Constants.FACE_THUMBNAIL_SIZE.width - iconCorner.getWidth(),
					                    Constants.FACE_THUMBNAIL_SIZE.height - iconCorner.getHeight());
			
			trainingDelta = new Dimension(Constants.FACE_THUMBNAIL_SIZE.width - 8, -5);

			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Init listener
		Listener listener = new Listener();
		this.addMouseMotionListener(listener);
		this.addMouseListener(listener);
	}
	
	public void setSelectedPerson(String person){
		faces = MainController.getInstance().getFaceDB().getRegionsForFace(person);
		this.revalidate();
		this.repaint();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		if (faces != null && faces.length > 0){
			
			int columns = this.getColumnCount();
			int rowCounter = 0;
			int colCounter = 0;

			
			for (Region r : faces){
				
				if (r!= null){
					
					int x = colCounter * CELLSIZE.width + Constants.FACE_THUMBNAIL_MARGIN/2;
					int y = rowCounter * CELLSIZE.height + Constants.FACE_THUMBNAIL_MARGIN/2;
					
					// Draw face thumbnail
					Image i = r.toThumbnail(Constants.FACE_THUMBNAIL_SIZE);
					g.drawImage(i, x, y, this);

					// Draw training symbol
					if (r.isUsedForTraining())
						g.drawImage(iconTraining, x + trainingDelta.width, y + trainingDelta.height, this);
					else
						g.drawImage(iconNoTraining, x + trainingDelta.width, y + trainingDelta.height, this);
					
					// Draw lower right corner
					g.drawImage(iconCorner, x + cornerDelta.width, y + cornerDelta.height, this);
					
					colCounter++;
					if (colCounter == columns){
						colCounter = 0;
						rowCounter++;
					}
				}
			}
		}
	}
	
	@Override
	public Dimension getPreferredSize() {
		Dimension size = new Dimension(0, this.getRowCount() * CELLSIZE.height);
		return size;
	}
	
	public int getRowCount(){
		if (faces == null || faces.length == 0 || this.getColumnCount() == 0)
			return 0;
		return	(int) Math.ceil((double) (faces.length) / (double) this.getColumnCount());
	}
	
	public int getColumnCount(){
		if (faces == null || faces.length == 0)
			return 0;
		return (int) Math.floor((double) (this.getWidth() - Constants.FACE_THUMBNAIL_MARGIN/2) / (double) CELLSIZE.width);
	}
	
	class Listener extends MouseAdapter{

		/* (non-Javadoc)
		 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
		 */
		public void mouseMoved(MouseEvent e) {

			if (isCursorOnIcon(e, iconTraining, trainingDelta)) {
				setToolTipText("Training Flag");				
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
			else if (isCursorOnIcon(e, iconCorner, cornerDelta)){
				setToolTipText("Show in Browser");
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
			else
			{
				setToolTipText("");
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}
		
		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 */
		public void mouseClicked(MouseEvent e) {
			Region selectedRegion = getRegionUnderCursor(e);
			if (isCursorOnIcon(e, iconTraining, trainingDelta)) 
				mainFrame.eventDispatcher.dispatchEvent(new EventModifyTrainingData(selectedRegion, !selectedRegion.isUsedForTraining()));
			else if (isCursorOnIcon(e, iconCorner, cornerDelta))
				mainFrame.eventDispatcher.dispatchEvent(new EventRegionSelectionChanged(selectedRegion));				
			repaint();
		}
		
		private boolean isCursorOnIcon(MouseEvent e, BufferedImage icon, Dimension iconDelta){
			int x = e.getX() % CELLSIZE.width;			
			int y = e.getY() % CELLSIZE.height;
			if (x > (iconDelta.width + Constants.FACE_THUMBNAIL_MARGIN/2) && x < (iconDelta.width + Constants.FACE_THUMBNAIL_MARGIN/2 + icon.getWidth()))
			if (y > (iconDelta.height + Constants.FACE_THUMBNAIL_MARGIN/2) && y < (iconDelta.height + Constants.FACE_THUMBNAIL_MARGIN/2 + icon.getHeight())){
					return getRegionUnderCursor(e) != null;
			}
			return false;
		}
		
		private Region getRegionUnderCursor(MouseEvent e){
			int col = (int) Math.ceil(e.getX() / CELLSIZE.width);
			int row = (int) Math.ceil(e.getY() / CELLSIZE.height);

			int modelIndex = row * (getColumnCount()) + col;
			
			if (col < getColumnCount() && row <= getRowCount() && modelIndex < faces.length){
				return faces[modelIndex];
			}
			else
				return null;
		}		
	}
}
