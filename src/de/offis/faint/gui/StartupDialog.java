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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JWindow;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import de.offis.faint.controller.MainController;
import de.offis.faint.data.RessourceLoader;

/**
 * @author maltech
 *
 */
public class StartupDialog extends JWindow {
	
	
	JTextArea textArea = new JTextArea();
	JScrollPane scrollpane = new JScrollPane(textArea){
				
		public void paint(Graphics g){
			super.paint(g);			
			BufferedImage watermark = RessourceLoader.getWaterMark();
			g.drawImage(watermark, (this.getWidth() - watermark.getWidth())/2, (this.getHeight() - watermark.getHeight())/2, null);
		}
	};
	
	public StartupDialog(){
		
		// Layout
		textArea.setBorder(new EmptyBorder(0,2,0,0));
		scrollpane.setBorder(null);
		scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollpane.setPreferredSize(new Dimension(RessourceLoader.getWaterMark().getWidth()+40, RessourceLoader.getWaterMark().getHeight()+30));
		JPanel rootPanel = new JPanel(new BorderLayout());
		rootPanel.add(scrollpane, BorderLayout.CENTER);
		this.getGlassPane().setVisible(true);
		
		JProgressBar bar = new JProgressBar();
		bar.setString("Working...");
		bar.setIndeterminate(true);
//		Border border = new BevelBorder(BevelBorder.RAISED);
		Border border = new LineBorder(Color.BLACK,1);
		rootPanel.setBorder(border);
//		rootPanel.setBackground(Color.WHITE);
		rootPanel.add(bar, BorderLayout.SOUTH);
		
		this.setContentPane(rootPanel);
		
		
		
		
		// Redirect standard output to text area
		PrintStream p = new PrintStream(new OutputStream(){
			
			/* Override Ancestor method */
			public void write(byte b[]) throws IOException {
				print(new String(b));
			}
			
			/* Override Ancestor method */
			public void write(byte b[], int off, int len) throws IOException {
				print(new String(b, off, len));
			}
			
			/* Override Ancestor method */
			public void write(int b) throws IOException {
				print(new String(new char[] { (char) b}));
			}
			
			private void print(String str){
				textArea.append(str);
				textArea.setCaretPosition(textArea.getText().length());
				scrollpane.repaint();
			}
			
		});
		System.setOut(p);
		
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		System.out.println("[ faint : Startup Console ]");
		MainController.getInstance();
		System.out.println("Running Swing GUI...");
		new MainFrame();
		this.dispose();
	}
	
	public static void main(String[] args){
		new StartupDialog();
	}
}
