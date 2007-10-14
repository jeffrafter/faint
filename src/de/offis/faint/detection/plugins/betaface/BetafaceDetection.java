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

package de.offis.faint.detection.plugins.betaface;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.apache.axis.AxisFault;

import de.offis.faint.detection.plugins.betaface.generated.BetafaceDetectedFace;
import de.offis.faint.detection.plugins.betaface.generated.BetafaceWebServiceLocator;
import de.offis.faint.detection.plugins.betaface.generated.BetafaceWebServiceSoapStub;
import de.offis.faint.global.Utilities;
import de.offis.faint.gui.tools.ExceptionDialog;
import de.offis.faint.interfaces.IDetectionPlugin;
import de.offis.faint.interfaces.ISwingCustomizable;
import de.offis.faint.model.ImageModel;
import de.offis.faint.model.Region;



public class BetafaceDetection implements IDetectionPlugin, ISwingCustomizable{
	
	
	private static final long serialVersionUID = 3073684014217159880L;
	public static final String LOGO = "betalogo.png";
	
	public static final Dimension MAXSIZE = new Dimension(640,480);
	protected String licenseKey = "82B9762D-C97B-4654-82E5-ECC36A4B4AC5";
	
	protected String serviceURL = "http://www.betaface.com/webservice/service.asmx";
	protected boolean downScale = true;
	
	private transient BetafaceWebServiceSoapStub service;
	private transient JPanel settingsPanel = null;
	
	
	public Region[] detectFaces(String file, int minScanWindowSize) {
		
		try {
			byte[] bytes = null;
			double scaleFactor = 1;
			ImageModel image = new ImageModel(file);
			image.getImage(true);

			
			// Prepare byte array to send
			if (downScale){
				
				int height = image.getHeight();
				int width = image.getWidth();

				if (height > MAXSIZE.getHeight() || width > MAXSIZE.getWidth()){
					double aspect = (double) width / (double) height;
					scaleFactor = width;
					if (aspect > (MAXSIZE.getWidth() / MAXSIZE.getHeight())) {
						width = (int) Math.round(MAXSIZE.getWidth());
						height = (int) Math.round((1.0 /aspect) * width);
					}
					else
					{
						height = (int) Math.round(MAXSIZE.getHeight());
						width = (int) Math.round(aspect * height);
					}
					scaleFactor = (double) width / scaleFactor; // scale = newWidth / oldWidth
					minScanWindowSize = (int) Math.round(minScanWindowSize * scaleFactor);
				}
				
				BufferedImage i = Utilities.getScaledCopy(image.getImage(false),width, height, Image.SCALE_SMOOTH);
				ByteArrayOutputStream o = new ByteArrayOutputStream();
				ImageIO.write(i ,"jpg", o);
				o.close();
				bytes = o.toByteArray();
			}
			else {
				
				bytes = new byte[(int) image.getFile().length()];
				
				InputStream is = new FileInputStream(file);
				int offset = 0;
				int numRead = 0;
				while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
					offset += numRead;
				}
			}
			
			// Init service if not already done
			try {
				service = new BetafaceWebServiceSoapStub(new URL(serviceURL), new BetafaceWebServiceLocator());
			} catch (AxisFault e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}				

			
			// Submit request
			BetafaceDetectedFace[] betaFaces = service.findFaces(licenseKey, bytes, 0,0,0, minScanWindowSize,0,0,0,0);
			if (betaFaces != null && betaFaces.length > 0){
				
				ArrayList<Region> detectedFaces = new ArrayList<Region>();
				for (int i = 0; i<betaFaces.length; i++){
					
					if (betaFaces[i].getOrigFeatures()[0].getWidth() >= minScanWindowSize && betaFaces[i].getOrigFeatures()[0].getHeight() >= minScanWindowSize)		
						detectedFaces.add(new Region(
								(int) Math.round(betaFaces[i].getOrigFeatures()[0].getX() / scaleFactor),
								(int) Math.round(betaFaces[i].getOrigFeatures()[0].getY() / scaleFactor),
								(int) Math.round(betaFaces[i].getOrigFeatures()[0].getWidth() / scaleFactor),
								(int) Math.round(betaFaces[i].getOrigFeatures()[0].getHeight() / scaleFactor),
								betaFaces[i].getOrigFeatures()[0].getAngleDegree(),
								file));
				}
				return detectedFaces.toArray(new Region[detectedFaces.size()]);				
			}
			
		} catch (IOException e) {
			if (e instanceof AxisFault){
				AxisFault a = (AxisFault) e;
				
				// This fault seems to indicate that no faces were detected on image.
				if (!a.getFaultString().startsWith("System.Web.Services.Protocols.SoapException: Server was unable to process request."))
				{
					new ExceptionDialog(null, e, null);
					e.printStackTrace();
				}	
			}
		}

		return new Region[0];
	}
	
	public String toString(){
		return getName();
	}

	
	/* (non-Javadoc)
	 * @see de.offis.faint.plugins.IPlugin#getName()
	 */
	public String getName() {
		return "Betaface.com Detection";
	}

	/* (non-Javadoc)
	 * @see de.offis.faint.plugins.IPlugin#getRequirementNotes()
	 */
	public String getDescription() {
		return "<p>The Betaface Detection Plugin uses the Web Service provided by <b>Betaface.com</b>. Thus a conntection to the internet is needed to use it.</p>";
	}
	
	public String getCopyrightNotes() {
		return "<p>Powered by <b>Betaface.com</b>. The Plugin is based on Apache Axis Libraries. Implementation by Malte Mathiszig 2007.</p><p align=\"LEFT\"><img src=\""+BetafaceDetection.class.getResource(LOGO) +"\"></p>";
	}

	/* (non-Javadoc)
	 * @see de.offis.faint.plugins.IPlugin#getSettingsPanel()
	 */
	public JPanel getSettingsPanel() {
		if (settingsPanel == null)
			settingsPanel = new BetafaceSettingsPanel(this);
		return settingsPanel;
	}
}
