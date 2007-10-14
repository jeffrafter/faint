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
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class NiceJFrame extends JFrame {

	private static final long serialVersionUID = 4020288451571134673L;
	JMenu file;
	JMenu lookAndFeel;
	ArrayList<Component> isolatedComponents = new ArrayList<Component>();
	
	static final Cursor WAIT_CURSOR = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
	static final Cursor DEFAULT_CURSOR = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
		
	public NiceJFrame() throws HeadlessException {
		super();
		init();
	}

	public NiceJFrame(GraphicsConfiguration arg0) {
		super(arg0);
		init();
	}

	public NiceJFrame(String arg0) throws HeadlessException {
		super(arg0);
		init();
	}

	public NiceJFrame(String arg0, GraphicsConfiguration arg1) {
		super(arg0, arg1);
		init();
	}
	
	private void init(){
		
		// Change L&F if running on Windows machine
		if (System.getProperty("os.name").contains("Windows"))
			setLookAndFeel("Windows");			
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setJMenuBar(new JMenuBar());		
	}
	
	public void addWindowMenu(){
		
		file = new JMenu("Window");
		
		// Always on top
		final JCheckBoxMenuItem alwaysOnTop = new JCheckBoxMenuItem("Always on top");
		alwaysOnTop.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				setAlwaysOnTop(alwaysOnTop.isSelected());
			}
		});
		this.file.add(alwaysOnTop);
		this.file.addSeparator();
		
		
		// Close Window
		final JMenuItem close = new JMenuItem("Close");
		close.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				close();
			}
		});
		this.file.add(close);
		this.getJMenuBar().add(file);
	}
	
	protected void close(){
		WindowEvent we = new WindowEvent(this,WindowEvent.WINDOW_CLOSING, null, 0, 0);
		getWindowListeners()[0].windowClosing(we);
	}
	
	public void addStyleMenu(){
		
		lookAndFeel = new JMenu("Style");
		ActionListener lookAndFeelListener = new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				JRadioButtonMenuItem item = (JRadioButtonMenuItem) e.getSource();
				setLookAndFeel(item.getText());
			}
		};
		
		ButtonGroup lookAndFeelGroup = new ButtonGroup();		
		LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels();
		for (int i=0; i<info.length; i++) {
			JRadioButtonMenuItem item = new JRadioButtonMenuItem(info[i].getName());
			lookAndFeelGroup.add(item);
			item.addActionListener(lookAndFeelListener);
			lookAndFeel.add(item);
			item.setSelected(UIManager.getLookAndFeel().getName().equals(item.getText()));
		}
		
		this.getJMenuBar().add(lookAndFeel);		
	}
	
	public void addInfoMenu(final String infoText){
		ActionListener infoListener = new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				new InfoDialog(null, infoText);
			}
		};
		JMenu infoMenu = new JMenu("?");
		JMenuItem about = new JMenuItem("Info...");
		infoMenu.add(about);
		about.addActionListener(infoListener);
		this.getJMenuBar().add(infoMenu);
	}
	
	protected void setLookAndFeel(String lafName){
		this.setEnabled(false);
		LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels();
	    for (int i=0; i<info.length; i++) {
	        if (info[i].getName().equals(lafName)){
	        	try {
	        		UIManager.setLookAndFeel(info[i].getClassName());
	        		SwingUtilities.updateComponentTreeUI(this);
	        		for (Component c : isolatedComponents){
		        		SwingUtilities.updateComponentTreeUI(c);	        			
	        		}
	            }catch (Exception e){System.err.println("L&F failed: "+lafName);};
	    		this.setEnabled(true);
	        	return;
	        }    
	    }
	}
	
	public void setSizeAndCenter(int w, int h){
		this.setSize(w,h);
		this.setLocationRelativeTo(null);
	}
	
	public void addIsolatedComponent(Component compo){
		if (!this.isolatedComponents.contains(compo))
			this.isolatedComponents.add(compo);
	}
	
	
	public synchronized void setEnabled(boolean enabled){
		if (!enabled){
			this.getGlassPane().setVisible(true);
			this.getGlassPane().setCursor(WAIT_CURSOR);
		}
		else{
			this.getGlassPane().setCursor(DEFAULT_CURSOR);
			this.getGlassPane().setVisible(false);
		}
	}
}
