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

package de.offis.faint.gui.tools;


import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/**
 * 
 * @author maltech
 */
public class NiceJPanel extends JPanel {
	
	private static final long serialVersionUID = -772855979372021648L;
	
	private GridBagConstraints c = new GridBagConstraints();
	private int columns;
		
	public NiceJPanel(){
		this(1);
	}
	
	public NiceJPanel(String name){
		this();
		this.setTitle(name);		
	}

	public NiceJPanel(String name, int columns){
		this(columns);
		this.setTitle(name);		
	}

	public NiceJPanel(int columns){
		super(new GridBagLayout());
		this.columns = columns;
		c.gridx=0;
		c.gridy=0;
		c.fill=GridBagConstraints.BOTH;
		c.insets=new Insets(5,5,5,5);
		c.weighty=0;
		c.weightx=1;
	}
	
	public void addRow(Container p){
		c.gridwidth=GridBagConstraints.REMAINDER;
		
		c.gridx=0;
		add(p,c);
		c.gridy++;
	}
	
	public void addElement(JLabel l, JComponent p){
		c.gridwidth=1;
		c.weightx=0;
		add(l,c);

		c.gridx++;
		c.weightx=1;
		add(p,c);		

		c.gridx++;
		if (c.gridx/2>=columns) {
			c.gridx=0;
			c.gridy++;			
		}
	}

	public void addElement(JCheckBox box){
		c.gridwidth=1;
		c.weightx=0;
		add(box,c);

		c.gridx++;
		c.weightx=1;
		add(new JPanel(),c);		

		c.gridx++;
		if (c.gridx/2>=columns) {
			c.gridx=0;
			c.gridy++;			
		}
	}

	
	
	public void addParam(JCheckBox checkBox, JLabel l) {
		c.gridwidth=1;
		c.weightx=0;
		add(checkBox,c);

		c.gridx++;
		c.weightx=1;
		add(l,c);		

		c.gridx++;
		if (c.gridx/2>=columns) {
			c.gridx=0;
			c.gridy++;			
		}
		
	}

	
	public void setTitle(String name) {
		if (name == "") this.setBorder(null);
		else this.setBorder(new TitledBorder(name));
	}

}
