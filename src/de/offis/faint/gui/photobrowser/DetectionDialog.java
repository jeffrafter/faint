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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import de.offis.faint.controller.MainController;
import de.offis.faint.global.Constants;
import de.offis.faint.global.Utilities;
import de.offis.faint.gui.MainFrame;
import de.offis.faint.gui.tools.ExceptionDialog;
import de.offis.faint.model.ImageModel;
import de.offis.faint.model.Region;

/**
 * @author maltech
 *
 */
public class DetectionDialog extends JDialog {
	
	final static String BUTTON_ABORT= "Cancel Detection";
	final static String BUTTON_CLOSE= "Ok";
	private Listener listener = new Listener();
	private DetectorTask currentSwingWorker = null;
	
	JTextArea outputArea = new JTextArea();
	JScrollPane scrollPane = new JScrollPane(outputArea);
	JProgressBar bar = new JProgressBar();
	JButton button = new JButton(BUTTON_ABORT);
	private MainFrame mainFrame;
	
	public DetectionDialog(MainFrame mainFrame){
		super(mainFrame, "Face Detection",true);
		mainFrame.addIsolatedComponent(this);
		this.mainFrame = mainFrame;
		bar.setValue(100);
		
		JPanel rootPane = new JPanel(new BorderLayout());	
		JPanel progressPanel = new JPanel(new BorderLayout());
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
		
		outputArea.setEditable(false);
		scrollPane.setPreferredSize(new Dimension(300,75));
		progressPanel.setBorder(new TitledBorder("Status"));
		progressPanel.add(scrollPane, BorderLayout.CENTER);
		progressPanel.add(bar, BorderLayout.SOUTH);
		button.addActionListener(listener);
		buttons.add(button);
		buttons.setBorder(new EmptyBorder(5,5,5,5));
		rootPane.add(progressPanel);
		rootPane.add(buttons, BorderLayout.SOUTH);
		
		bar.setIndeterminate(true);
		this.setContentPane(rootPane);
		this.pack();
	}
		
	
	class Listener implements ActionListener{
	
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {

			// Cancel detection
			if (currentSwingWorker != null)
				currentSwingWorker.cancel(true);

			// Close dialog
			dispose();
		}		
	}

	/**
	 * @param model
	 * @return
	 */
	public boolean performDetection(ImageModel model) {
		
			button.setText(BUTTON_ABORT);
			bar.setIndeterminate(true);
			outputArea.setText("");
			currentSwingWorker = new DetectorTask(model);
			currentSwingWorker.execute();
			this.setLocationRelativeTo(mainFrame);
			this.setVisible(true);

		return currentSwingWorker.isDone();
	}
	
	class DetectorTask extends SwingWorker<Region[], String>{
		
		private ImageModel model;

		public DetectorTask(ImageModel model){
			this.model = model;
		}

		/* (non-Javadoc)
		 * @see javax.swing.SwingWorker#doInBackground()
		 */
		@Override
		protected Region[] doInBackground() throws Exception {
			Region[] result = null;
			try{
				String pluginName = Utilities.getClassName(
						MainController.getInstance().getDetectionHotSpot().getActivePlugin().getClass());

				publish("Running " + pluginName + " ... ");
				
				result = MainController.getInstance().detectFaces(model, false);
				int numFaces = 0;
				if (result != null){
					numFaces = result.length;
				}
				
				if (!isCancelled())
					publish("done.\nNumber of possible faces found: " + numFaces + "\n");
			}
			catch(Throwable t){
				new ExceptionDialog(mainFrame,t, "Detection failed");
				done();
			}
			return result;
		}

		@Override
		protected void done() {
			super.done();
			
			try {
				if (!isCancelled() && get() != null){

					for (int i = 0; i < get().length; i++){
						MainController.getInstance().getFaceDB().put(get()[i], Constants.UNKNOWN_FACE);
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			
			mainFrame.browserTab.getImagePanel().repaint();
			mainFrame.browserTab.getAnnotationPanel().updateFromModel();
			bar.setIndeterminate(false);			
			button.setText(BUTTON_CLOSE);
		}

		@Override
		protected void process(List<String> chunks) {
//			super.process(chunks);
			for (String str : chunks){
				outputArea.append(str);				
			}
		}
		
	}
}
