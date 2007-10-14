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

import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.offis.faint.global.Constants;
import de.offis.faint.gui.events.EventShowTab;
import de.offis.faint.gui.events.EventShutdownMainFrame;
import de.offis.faint.gui.facedb.FaceDBTab;
import de.offis.faint.gui.photobrowser.BrowserTab;
import de.offis.faint.gui.preferences.PreferencesTab;
import de.offis.faint.gui.tools.NiceJFrame;


/**
 * @author maltech
 * 
 */
public class MainFrame extends NiceJFrame {
	
	// Instance of the EventDispatcher
	public EventDispatcher eventDispatcher = new EventDispatcher(this);
	
	// Symbolic tabs
	public enum TAB {BROWSER, FACE_DB, PREFERENCES};
	
	// TabbedPane and tabs
	private JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
	public BrowserTab browserTab = new BrowserTab(this);
	public FaceDBTab faceDBTab = new FaceDBTab(this);
	public PreferencesTab preferencesTab = new PreferencesTab(this);
	
	
	/**
	 * Constructor.
	 *
	 */
	public MainFrame() {

		super("faint - The Face Annotation Interface!");
		
		// Init menus
		this.addWindowMenu();
		this.addStyleMenu();
		this.addInfoMenu("<html>&nbsp; <b>faint - Version "+Constants.RELEASE+"</b><br>&nbsp; http://faint.sourceforge.net/<br><br>&nbsp; (C) 2007&nbsp; Malte Mathiszig</html>");
		
		// Init tabs
		tabbedPane.add("Photo Browser", browserTab);
		tabbedPane.add("Face Database", faceDBTab);
		tabbedPane.add("Preferences", preferencesTab);		
		this.setContentPane(tabbedPane);
		
		// Add listener for window and tab events
		Listener listener = new Listener();
		tabbedPane.addChangeListener(listener);
		this.addWindowListener(listener);

		// Make GUI visible
		this.setSizeAndCenter(800, 600);
		this.setVisible(true);
	}

	/**
	 * 
	 * @return Symbolic representation of the active tab.
	 */
	public TAB getActiveTAB() {
		Component activeTab = tabbedPane.getSelectedComponent();
		if (activeTab == browserTab)
			return TAB.BROWSER;
		if (activeTab == faceDBTab)
			return TAB.FACE_DB;
		if (activeTab == preferencesTab)
			return TAB.PREFERENCES;
		return null;
	}

	/**
	 * Sets the active tab.
	 * 
	 * @param activeTAB Symbolic representation of the tab that is going to be active.
	 */
	public void setActiveTAB(TAB activeTAB) {
		switch (activeTAB) {
		case BROWSER:
			tabbedPane.setSelectedComponent(browserTab);			
			break;
			
		case FACE_DB:
			tabbedPane.setSelectedComponent(faceDBTab);
			break;

		case PREFERENCES:
			tabbedPane.setSelectedComponent(preferencesTab);
			break;
		}
	}
	
	class Listener extends WindowAdapter implements ChangeListener{

		/* (non-Javadoc)
		 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
		 */
		public void stateChanged(ChangeEvent arg0) {
			eventDispatcher.dispatchEvent(new EventShowTab(getActiveTAB()));
		}
		
		public void windowClosing(WindowEvent e) {
			eventDispatcher.dispatchEvent(new EventShutdownMainFrame());
			System.exit(0);
		}		
	}
}
