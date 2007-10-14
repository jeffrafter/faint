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

package de.offis.faint.gui;


import java.io.File;
import java.util.HashMap;

import javax.swing.JOptionPane;

import de.offis.faint.controller.MainController;
import de.offis.faint.gui.events.EventAddKnownPerson;
import de.offis.faint.gui.events.EventChangeScanWindowSize;
import de.offis.faint.gui.events.EventClassifyRegion;
import de.offis.faint.gui.events.EventDeletePerson;
import de.offis.faint.gui.events.EventDeleteRegion;
import de.offis.faint.gui.events.EventDetectFacesOnCurrentImage;
import de.offis.faint.gui.events.EventModifyTrainingData;
import de.offis.faint.gui.events.EventOpenFolder;
import de.offis.faint.gui.events.EventOpenImage;
import de.offis.faint.gui.events.EventRecognizeFace;
import de.offis.faint.gui.events.EventRegionSelectionChanged;
import de.offis.faint.gui.events.EventRenamePerson;
import de.offis.faint.gui.events.EventShowFacesOfPerson;
import de.offis.faint.gui.events.EventShowTab;
import de.offis.faint.gui.events.EventShutdownMainFrame;
import de.offis.faint.gui.events.EventUpdateAnnotationPanel;
import de.offis.faint.gui.events.EventUpdateMenuBar;
import de.offis.faint.gui.events.IEvent;
import de.offis.faint.gui.photobrowser.DetectionDialog;
import de.offis.faint.gui.tools.ExceptionDialog;
import de.offis.faint.gui.tools.InfoDialog;
import de.offis.faint.model.ImageModel;
import de.offis.faint.model.Region;

/**
 * @author maltech
 *
 */
public class EventDispatcher {
	
	private MainFrame mainFrame;
	
	private File currentFolder;
	private ImageModel currentImage;
	private Region currentRegion;
	
	public EventDispatcher(MainFrame mainFrame){
		this.mainFrame = mainFrame;
	}
	
	public void dispatchEvent(IEvent incomingEvent){
		
		try {
		
		// Show tab in MainFrame
		if (incomingEvent instanceof EventShowTab){
			EventShowTab event = (EventShowTab) incomingEvent;
			
			// Refresh view of person database if faceDB tab is selected
			if (event.getTab() == MainFrame.TAB.FACE_DB)
				mainFrame.faceDBTab.getPersonPanel().updateFromModel();
			
			// Only change tab if the new tab is not already active
			if (event.getTab()!= mainFrame.getActiveTAB())
				mainFrame.setActiveTAB(event.getTab());
		}
			
        // Update table in faceDB tab
		else if (incomingEvent instanceof EventShowFacesOfPerson){
			EventShowFacesOfPerson event = (EventShowFacesOfPerson) incomingEvent;
			mainFrame.faceDBTab.getFacePanel().setSelectedPerson(event.getPerson());		
		}
		
        // Open Folder
		else if (incomingEvent instanceof EventOpenFolder){
			
			mainFrame.setEnabled(false);

			EventOpenFolder event = (EventOpenFolder) incomingEvent;
			File newFolder = event.getFolder();
			
			if (currentFolder == null || !newFolder.equals(currentFolder))
			{
				currentFolder = newFolder;
				
				// Show thumbnails
				mainFrame.browserTab.getThumbnailPanel().setFolder(newFolder);
				
				// Clear currentImage if it is from another folder...
				if (!(currentImage == null || currentImage.getFolder().equals(newFolder)))
					dispatchEvent(new EventOpenImage(null));
			}
			
			// Make sure browser tab is active
			dispatchEvent(new EventShowTab(MainFrame.TAB.BROWSER));
			
			mainFrame.setEnabled(true);
		} 
		
		// Open Image
		else if (incomingEvent instanceof EventOpenImage){
			EventOpenImage event = (EventOpenImage) incomingEvent;
			ImageModel image = event.getImage();
			
			
			if (image == null || image.isAvailable()){
			mainFrame.setEnabled(false);
			
			if (currentImage == null || !currentImage.equals(image)) {
				currentImage = image;
				
				// Cache BufferedImage
				if (image != null)
				  MainController.getInstance().getBufferedImageCache().cacheImage(image.getFile().getPath());
				
				// Update Image
				mainFrame.browserTab.getImagePanel().setImage(image);

				// Update AnnotationTable and selected region
				mainFrame.browserTab.getAnnotationPanel().setImage(currentImage);
				
				// Make sure that the right folder is opened.
				if (currentImage!=null)
					dispatchEvent(new EventOpenFolder(currentImage.getFolder()));
				
				// Make sure that the right thumbnail is selected
				mainFrame.browserTab.getThumbnailPanel().setSelectedImage(image);
				
				// Update Buttons
				dispatchEvent(new EventUpdateMenuBar(currentImage != null));
			}
			
			mainFrame.setEnabled(true);
			}
			else {
				new InfoDialog(mainFrame," Image "+image.getFile().getPath()+" not available!");				
			}
		}
		
		// Delete Person
		else if (incomingEvent instanceof EventDeletePerson){
			EventDeletePerson event = (EventDeletePerson) incomingEvent;
			
			String[] options = { "Ok", "Cancel"};
			int n = JOptionPane.showOptionDialog( mainFrame,
					"Remove "+event.getName()+" from database?",
					"Delete Person",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null, options,options[0] );
			if ( n == JOptionPane.YES_OPTION )
			{
				MainController.getInstance().getFaceDB().deleteAnnotation(event.getName());
				mainFrame.browserTab.getAnnotationPanel().updateFromModel();
				mainFrame.browserTab.getImagePanel().getClassificationPanel().updateFromModel();
				mainFrame.faceDBTab.getPersonPanel().updateFromModel();				
			}
		}
		
        // Set the classification of a region
		else if (incomingEvent instanceof EventClassifyRegion){
			EventClassifyRegion event = (EventClassifyRegion) incomingEvent;			
        				
			// Save classification in database
			MainController.getInstance().getFaceDB().put(event.getRegion(),event.getAnnotation());
			
			// Update view
			mainFrame.browserTab.getAnnotationPanel().repaint();
			mainFrame.browserTab.getImagePanel().getClassificationPanel().repaint();
		}
		
		// Rename a person
		else if (incomingEvent instanceof EventRenamePerson){
			EventRenamePerson event = (EventRenamePerson) incomingEvent;
			MainController.getInstance().getFaceDB().renameAnnotation(event.getOldName(), event.getNewName());
			mainFrame.browserTab.getAnnotationPanel().updateFromModel();
			mainFrame.browserTab.getImagePanel().getClassificationPanel().updateFromModel();
			mainFrame.faceDBTab.getPersonPanel().updateFromModel();
		}
		
        // Change training flag of region
		else if (incomingEvent instanceof EventModifyTrainingData){
			EventModifyTrainingData event = (EventModifyTrainingData) incomingEvent;
			event.getRegion().setUsedForTraining(event.getValue());	
		}

		
		// Add a new person to the database
		else if (incomingEvent instanceof EventAddKnownPerson){
			EventAddKnownPerson event = (EventAddKnownPerson) incomingEvent;
			MainController.getInstance().getFaceDB().put(null, event.getName());
		} 
		
		// Selected region changed
		else if (incomingEvent instanceof EventRegionSelectionChanged){
			EventRegionSelectionChanged event = (EventRegionSelectionChanged) incomingEvent;
			Region newRegion = event.getRegion();
			
			if (newRegion != null){
				ImageModel image = new ImageModel(newRegion.getImage());
				if (currentImage == null || !currentImage.equals(image))
				{
					dispatchEvent(new EventOpenImage(image));
					
					if (currentImage == null || !currentImage.equals(image))
						newRegion = null;
				}
			}
			
			if (currentRegion == null || !currentRegion.equals(newRegion)){
				currentRegion = newRegion;
				mainFrame.browserTab.getImagePanel().setSelectedRegion(newRegion);
				mainFrame.browserTab.getAnnotationPanel().setSelectedRegion(newRegion);
			}
			
			// Make sure browser tab is active
			if (currentRegion != null)
				this.dispatchEvent(new EventShowTab(MainFrame.TAB.BROWSER));
		}
		
		// AnnotationPanel has to be updated
		else if (incomingEvent instanceof EventUpdateAnnotationPanel) {
			mainFrame.browserTab.getAnnotationPanel().updateFromModel();			
		}
		
		// Delete selected region
		else if (incomingEvent instanceof EventDeleteRegion) {
			EventDeleteRegion event = (EventDeleteRegion) incomingEvent;
			
			// Delete region from database
			MainController.getInstance().getFaceDB().deleteRegion(event.getRegion());
			
			// Update view
			if (event.getRegion().getImage().equals(currentImage.getFile().getAbsolutePath())){
				
				mainFrame.browserTab.getAnnotationPanel().updateFromModel();
				mainFrame.browserTab.getImagePanel().repaint();
			}			
		}

		// Run Face Detection on current image
		else if (incomingEvent instanceof EventDetectFacesOnCurrentImage) {
			
			DetectionDialog dialog = mainFrame.browserTab.getDetectionDialog();
			
			dialog.performDetection(this.currentImage);
		}
		
		// Run Face Recognition on selected region
		else if (incomingEvent instanceof EventRecognizeFace) {
			EventRecognizeFace event = (EventRecognizeFace) incomingEvent;
			
			mainFrame.setEnabled(false);
			
			HashMap<String, Integer> recognitionResult = (MainController.getInstance().recognizeFace(event.getRegion()));
			
			if (this.currentRegion == event.getRegion())
				mainFrame.browserTab.getImagePanel().getClassificationPanel().updateRecognitionData(recognitionResult);
			
			mainFrame.setEnabled(true);
		}
		
		// Change minimum size of scan window
		else if (incomingEvent instanceof EventChangeScanWindowSize) {
				EventChangeScanWindowSize event = (EventChangeScanWindowSize) incomingEvent;
			MainController.getInstance().setScanWindowSize(event.getNewSize());
			mainFrame.browserTab.getImagePanel().repaint();
		}
		
		// Update menu bar
		else if (incomingEvent instanceof EventUpdateMenuBar) {
			EventUpdateMenuBar event = (EventUpdateMenuBar) incomingEvent;
			mainFrame.browserTab.getMenuBar().update(event.isDetectionAllowed());
		}
		
		// Shut down application and save settings
		else if (incomingEvent instanceof EventShutdownMainFrame) {
				MainController.getInstance().getDetectionHotSpot().serializeContent();
				MainController.getInstance().getRecognitionHotSpot().serializeContent();
				MainController.getInstance().getFaceDB().writeToDisk();
		}
		
		} catch (Throwable t){
			new ExceptionDialog(mainFrame, t, "An error occured!");
			t.printStackTrace();
			mainFrame.setEnabled(true);
		}
	}
}
