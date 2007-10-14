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

package de.offis.faint.detection.filters.skincolor;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author maltech
 *
 */
public class SkinColorSettingsPanel extends JPanel {
	
	SpinnerNumberModel spinModel = new SpinnerNumberModel(50,0,100,1);
	private SkinColorFilter filter;
	
	public SkinColorSettingsPanel(SkinColorFilter filter){
		super(new BorderLayout());
		
		this.filter = filter;
		
		spinModel.setValue(filter.threshold);
		spinModel.addChangeListener(new Listener()); 
		JPanel border = new JPanel(new BorderLayout());
		
		JSpinner spin = new JSpinner(spinModel);
		border.setBorder(new TitledBorder("Minimum skin pixel amount"));
		border.add(spin, BorderLayout.CENTER);
		
		this.add(border, BorderLayout.NORTH);
//		NiceJPanel rowPanel = new NiceJPanel();
//		rowPanel.add(new JLabel("Minimum Skin Pixel"),spin);
		
	}
	
	class Listener implements ChangeListener{

		/* (non-Javadoc)
		 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
		 */
		public void stateChanged(ChangeEvent e) {
			filter.threshold = spinModel.getNumber().intValue();
		}		
	}

}
