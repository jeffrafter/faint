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

import java.awt.Cursor;
import java.awt.Point;

/**
 * @author maltech
 *
 */
public class ImageCursorBehaviour {
	
	private static final Cursor MOVE_CURSOR = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
	private static final Cursor SELECT_CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
	private static final Cursor CREATE_CURSOR = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
	
	ImagePanel panel;
	
	public ImageCursorBehaviour(ImagePanel panel){
		this.panel = panel;
	}
	
	public void updateCursor(Point point){
		Cursor cursor = Cursor.getDefaultCursor();
		
		switch (panel.mode){
		
		case NORMAL:
			// if cursor is above image...
//			if (panel.currentImageModel != null &&
//					point.x > panel.imageViewBehaviour.getImagePositionX() &&
//					point.x < panel.imageViewBehaviour.getImagePositionX() + panel.imageViewBehaviour.getImageViewWidth() &&
//					point.y > panel.imageViewBehaviour.getImagePositionY() &&
//					point.y < panel.imageViewBehaviour.getImagePositionY() + panel.imageViewBehaviour.getImageViewHeight())
			if (panel.currentImageModel != null)
			{
				// transform cursor position to image coords
				point = panel.imageViewBehaviour.viewToModel(point);
				
				// check if cursor is above selected region
				if (panel.currentRegion != null && panel.currentRegion.containsPoint(point))
					cursor = MOVE_CURSOR;
				
				// check if cursor is inside of the small arc above the selected region
				else if (panel.currentRegion != null && panel.pointInsideUpperRegionCircle(point)){
					cursor = getReshapeCursor();						
				}
				
				// check if cursor is above any region
				if (cursor.getType() == Cursor.DEFAULT_CURSOR && panel.currentImageModel.getRegionAtPoint(point) != null){
					cursor = SELECT_CURSOR;					
				}
			}
			break;
			
		case CREATE: cursor = CREATE_CURSOR;
		break;
		
		case MOVE: cursor = MOVE_CURSOR;
		break;
		
		case RESHAPE: cursor = getReshapeCursor();
		break;
		}
		
		panel.setCursor(cursor);
	}


	private Cursor getReshapeCursor() {
		Cursor cursor = null;
		double degrees = panel.currentRegion.getAngle();
		if (degrees < 22.5 || degrees > 360 - 22.5) cursor = Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
		else if (degrees < 22.5 + 45) cursor = Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
		else if (degrees < 22.5 + 90) cursor = Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
		else if (degrees < 22.5 + 135) cursor = Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR);
		else if (degrees < 22.5 + 180) cursor = Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
		else if (degrees < 22.5 + 225) cursor = Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
		else if (degrees < 22.5 + 270) cursor = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
		else cursor = Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
		return cursor;	
	}

}
