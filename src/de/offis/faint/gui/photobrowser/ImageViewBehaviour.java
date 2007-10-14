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

import java.awt.Point;

import de.offis.faint.global.Constants;

/**
 * This class helps to center and fit in images displayed on the ImagePanel.
 * 
 * @author maltech
 *
 */
public class ImageViewBehaviour {
	
	private ImagePanel panel;

	private int imageWidth, imageHeight, panelWidth, panelHeight;

	private int imageViewWidth, imageViewHeight, imagePositionX, imagePositionY;

	public ImageViewBehaviour(ImagePanel panel){
		this.panel = panel;
	}
	
	private void checkValues(){
		
		int newPanelWidth = panel.getVisibleRect().width - Constants.PREVIEW_MARGIN;
		int newPanelHeight = panel.getVisibleRect().height - Constants.PREVIEW_MARGIN;
		
		
		int newImageWidth = 0;
		int newImageHeight = 0;
		if (panel.currentImageModel != null){
			newImageHeight = panel.currentImageModel.getHeight();
			newImageWidth = panel.currentImageModel.getWidth();
		}
		
		// Check if size of panel or size of image has changed
		if (newPanelHeight != panelHeight || newPanelWidth != panelWidth ||
			newImageHeight != imageHeight || newImageWidth != imageWidth)
		{
			// Fit image width and height into visible rectangle and center image
			double imageaspect = (double)newImageWidth/(double)newImageHeight;			
			double previewAspect = (double)newPanelWidth/(double)newPanelHeight;
			if ((imageaspect) > (previewAspect))		{
				imagePositionX = Constants.PREVIEW_MARGIN / 2;
				imagePositionY = (int) (panel.getVisibleRect().height-(newPanelWidth/imageaspect)) / 2;
				imageViewWidth = newPanelWidth;
				imageViewHeight = (int) (newPanelWidth/imageaspect);
			}
			else
			{
				imagePositionX = (int) (panel.getVisibleRect().width-(newPanelHeight*imageaspect)) / 2;
				imagePositionY = Constants.PREVIEW_MARGIN / 2;
				imageViewWidth = (int) (newPanelHeight*imageaspect);
				imageViewHeight = newPanelHeight;
			}
			
			// Store values
			panelHeight = newPanelHeight;
			panelWidth = newPanelWidth;
			imageHeight = newImageHeight;
			imageWidth = newImageWidth;
		}
	}
	
	public Point viewToModel(Point p){
		Point point = new Point(p);
		
		point.x -= this.getImagePositionX();
		point.y -= this.getImagePositionY();
		
		double zoom = this.getZoomFactor();		
		point.x = (int) Math.round(((double)point.x)/zoom);
		point.y = (int) Math.round(((double)point.y)/zoom);

		return point;
	}

	public int getImageViewHeight() {
		checkValues();
		return imageViewHeight;
	}

	public int getImageViewWidth() {
		checkValues();
		return imageViewWidth;
	}

	public int getImagePositionX() {
		checkValues();
		return imagePositionX;
	}

	public int getImagePositionY() {
		checkValues();
		return imagePositionY;
	}

	public double getZoomFactor() {
		checkValues();
		return (double) imageViewWidth / (double) imageWidth;
	}

	public int getImageHeight() {
		checkValues();
		return imageHeight;
	}

	public int getImageWidth() {
		checkValues();
		return imageWidth;
	}

	public int getPanelHeight() {
		checkValues();
		return panelHeight;
	}

	public int getPanelWidth() {
		checkValues();
		return panelWidth;
	}
}
