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

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.offis.faint.controller.MainController;
import de.offis.faint.gui.MainFrame;
import de.offis.faint.gui.events.EventChangeScanWindowSize;

/**
 * @author maltech
 *
 */
public class ScanWindowSlider extends JSlider {
	
	private MainFrame mainFrame;
	private boolean active = false;
	
	public ScanWindowSlider(MainFrame mainFrame){
		super(1,100);
		this.mainFrame = mainFrame;
		this.setOrientation(JSlider.VERTICAL);
		setMinorTickSpacing(1);
		setMajorTickSpacing(10);
		setPaintTicks(true);
		setToolTipText("Minimum Scan Window Size");
		this.setValue(MainController.getInstance().getScanWindowSize());
		Listener listener = new Listener();
		this.addChangeListener(listener);
		this.addMouseListener(listener);
		this.addFocusListener(listener);
	}
	
	public boolean isActive(){
		return active;
	}
	
	class Listener extends MouseAdapter implements ChangeListener, FocusListener{

		/* (non-Javadoc)
		 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
		 */
		public void stateChanged(ChangeEvent e) {
			mainFrame.eventDispatcher.dispatchEvent(new EventChangeScanWindowSize(((JSlider)e.getSource()).getValue()));
			
		}

		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
		 */
		public void mouseEntered(MouseEvent e) {
			active = true;
			mainFrame.browserTab.getImagePanel().repaint();			
		}

		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
		 */
		public void mouseExited(MouseEvent e) {
			active = false;
			mainFrame.browserTab.getImagePanel().repaint();						
		}

		/* (non-Javadoc)
		 * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
		 */
		public void focusGained(FocusEvent e) {
			mainFrame.browserTab.getImagePanel().repaint();
		}

		/* (non-Javadoc)
		 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
		 */
		public void focusLost(FocusEvent e) {
			mainFrame.browserTab.getImagePanel().repaint();
		}
	}
}
