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

package de.offis.faint.gui.events;

import java.io.File;

/**
 * @author maltech
 *
 */
public class EventOpenFolder implements IEvent{

	private File folder;

	/**
	 * @param folder
	 */
	public EventOpenFolder(File folder) {
		this.folder = folder;
	}

	/**
	 * @return
	 */
	public File getFolder() {
		return folder;
	}
	
//	public void changeFolder(String path){
//		clusterTable.getSelectionModel().clearSelection();
//		imagePreview.setCurrentImage(null);
//		annotationTable.setImage(null);
//		ArrayList<ImageModel> images = new ArrayList<ImageModel>();
//		File folder = new File(path);
//		String[] imageFiles = folder.list(new FileTypeFilter(Constants.IMAGE_SUFFIXES));
//		this.backgroundActionController.clearRequestedThumbNails();
//		for (int i = 0; i< imageFiles.length;i++){
//			ImageModel image = new ImageModel(folder + File.separator + imageFiles[i]);
//			images.add(image);
//			requestThumbnail(image);
//		}
//		Cluster cluster = new Cluster(images);
//		clusterTable.setCluster(cluster);
//		clusterTable.revalidate();
//	}

}
