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

package de.offis.faint.gui.preferences;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.html.HTMLEditorKit;

import de.offis.faint.controller.HotSpotController;
import de.offis.faint.gui.MainFrame;
import de.offis.faint.interfaces.IModule;
import de.offis.faint.interfaces.ISwingCustomizable;

/**
 * @author maltech
 *
 */
public class PluginPanel extends JPanel{
	
	private HotSpotController hotSpot;
	private JComboBox pluginSelector;
	private JScrollPane optionScroller;
	private JEditorPane htmlPane;
	private MainFrame mainFrame;

	public PluginPanel(String title, HotSpotController hotSpot, MainFrame mainFrame){
		super(new BorderLayout());
		this.mainFrame = mainFrame;
		this.setBorder(new TitledBorder(title));
		
		this.hotSpot = hotSpot;
		EventListener listener = new EventListener();
		
		// Init Dropdown Menu
		pluginSelector = new JComboBox(hotSpot.getAvailablePlugins());
		pluginSelector.setSelectedItem(hotSpot.getActivePlugin());
		pluginSelector.addActionListener(listener);
		JPanel pluginSelectorPanel = new JPanel(new BorderLayout());
		pluginSelectorPanel.setBorder(new TitledBorder("Active Plugin"));
		pluginSelectorPanel.add(pluginSelector, BorderLayout.CENTER);
		
		// Init htmlPane
		htmlPane = new JEditorPane();
		htmlPane.setEditorKit(new HTMLEditorKit());
		htmlPane.setEditable(false);
		JScrollPane htmlScroller = new JScrollPane(htmlPane);
		htmlScroller.setPreferredSize(new Dimension(0,150));
		htmlScroller.setBorder(new BevelBorder(BevelBorder.LOWERED));
		JPanel htmlPanel = new JPanel(new GridLayout());
		htmlPanel.setBorder(new TitledBorder("Plugin Information"));
		htmlPanel.add(htmlScroller);
		
		
		// Init option ScrollPane
		optionScroller = new JScrollPane();
		optionScroller.setBorder(new TitledBorder("Plugin Settings"));
//		optionScroller.setViewportBorder(new BevelBorder(BevelBorder.LOWERED));
		optionScroller.setViewportBorder(new LineBorder(Color.LIGHT_GRAY));
		
		// Put it all together
//		JSplitPane body = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, optionScroller, htmlPanel);
//		body.setBorder(new EmptyBorder(5,5,5,5));
//		body.setOneTouchExpandable(true);
//		body.setDividerLocation(360);
		JPanel body = new JPanel(new GridLayout());
		body.add(htmlPanel, BorderLayout.CENTER);
		body.add(optionScroller, BorderLayout.EAST);
		
		this.add(pluginSelectorPanel, BorderLayout.NORTH);
		this.add(body, BorderLayout.CENTER);
		
		// Fill layout with content
		updateBody();
	}
	
	private IModule getActivePlugin(){
		return (IModule) this.hotSpot.getActivePlugin();
	}
	
	private void updateBody(){
		IModule plugin = this.getActivePlugin();
		String html = "";
		html += "<h2>Plugin Description</h2>" + plugin.getDescription() +"<br>";
		html += "<h2>Copyright Notes</h2>" + plugin.getCopyrightNotes() + "<br>";
		this.htmlPane.setText(html);
		
		Component view = null;
		htmlPane.setCaretPosition(0); // resets the scrollbars
		if (plugin instanceof ISwingCustomizable){
			view = ((ISwingCustomizable)plugin).getSettingsPanel();
		}
		else {
			view = new JLabel("No Settings available.");
			((JLabel) view).setHorizontalAlignment(SwingConstants.CENTER);
		}
		mainFrame.addIsolatedComponent(view);
		this.optionScroller.setViewportView(view);
	}
	
	private class EventListener implements ActionListener{

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@SuppressWarnings("unchecked")
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == pluginSelector){
				hotSpot.setActivePlugin(pluginSelector.getSelectedItem());
				updateBody();
			}
		}
		
	}
}
