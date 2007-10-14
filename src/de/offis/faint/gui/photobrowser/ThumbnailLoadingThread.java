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

import java.util.Vector;

import de.offis.faint.gui.photobrowser.ThumbnailTable.ThumbnailTableModel;
import de.offis.faint.model.ImageModel;

/**
 * @author maltech
 *
 */
public class ThumbnailLoadingThread extends Thread{
	
	private Vector<ImageModel> thumbNailsToPreload = new Vector<ImageModel>();
	private ThumbnailTableModel thumbModel;
	private boolean suspended = false;
	
	public ThumbnailLoadingThread(ThumbnailTableModel thumbModel){
		this.thumbModel = thumbModel;
		this.start();
	}
	
	
	@Override
	public void run() {
		while(true){
			while(!thumbNailsToPreload.isEmpty()){
				ImageModel model = thumbNailsToPreload.firstElement();
				model.initThumbnail();
				thumbModel.reportThumbnailUpdate();
				thumbNailsToPreload.remove(model);
			}
			suspended = true;
			synchronized(this) {
				while (suspended)
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			}
		}
	}

	public synchronized void preloadThumbnail(ImageModel image){
		if (image.getTumbnail()==null){
			thumbNailsToPreload.add(image);
			if(suspended){
				suspended=false;
				this.notify();
			}
		}
	}
	
	public void clearRequestedThumbNails(){
		this.thumbNailsToPreload.clear();
	}
	

}
