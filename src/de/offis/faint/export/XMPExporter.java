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

package de.offis.faint.export;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.SwingWorker;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.offis.faint.controller.MainController;
import de.offis.faint.model.FaceDatabase;
import de.offis.faint.model.Region;

/**
 * @author maltech
 *
 */
public class XMPExporter extends SwingWorker<String, String> {
	
	private static final String ATT_NAMESPACE = "xmlns:faint";
	private static final String VAL_NAMESPACE = "http://faint.sourceforge.net/ns";
		
	private static final String TAG_RDF = "rdf:RDF";
	private static final String TAG_RDF_DESCRIPTION = "rdf:Description";
	private static final String TAG_RDF_BAG = "rdf:Bag";
	private static final String TAG_RDF_LI = "rdf:li";

	private static final String ATT_RDF_PARSETYPE = "rdf:parseType";
	private static final String VAL_RDF_RESOURCE = "Resource";
	
	private static final String TAG_FACES = "faint:Faces";
	private static final String TAG_PERSON = "faint:Name";
	private static final String TAG_LOCATION = "faint:Loc";
	private static final String TAG_DIMENSION = "faint:Dim";
	private static final String TAG_ANGLE = "faint:Ang";

	protected String[] files;
	protected int filesProcessed;
	FaceDatabase db;
	
	public XMPExporter(){
		db = MainController.getInstance().getFaceDB();
		files = db.getKnownFiles();
		java.util.Arrays.sort(files);
	}

	/* (non-Javadoc)
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected String doInBackground() throws Exception {
		for (filesProcessed = 0; filesProcessed < files.length; filesProcessed++){
			String file = files[filesProcessed];
			String comment = "";
			
			process : if (file.toUpperCase().endsWith("JPG") || file.toUpperCase().endsWith("JPEG")) {
				
				// Initiate JPEG adapter
				JPEG_Adapter jpeg = null;
				try {
					jpeg = new JPEG_Adapter((new File(file)).toURI().toURL());
				} catch(IOException e){
					comment = "skipped! (Unable to read JPEG)";
					break process;
				}
				
				// Search for existing XMP packet
				XMPPacket packet = null;
				HashMap<Integer, XMPPacket> packets = jpeg.extractXMPpackets();
				
				// Remove old packet
				if (packets.size() > 0) {
					Integer key = Integer.MAX_VALUE;
					for (Integer i : packets.keySet()) {
						key = Math.min(key, i);
					}
					packet = packets.get(key);
					jpeg.getHeaderSegments().remove((int) key);
				} 
				else {
					packet = new XMPPacket();
				}
				
				Document doc = packet.parseXML();
				
				// Find <rdf:RDF> tag
				Node rdfTag = doc.getElementsByTagName(TAG_RDF).item(0);				
				if (rdfTag == null) {
					comment = "skipped! (existing header not valid)";
					break process;
				}
				
				// Remove any existing <rdf:Description xmlns:faint='http://faint.sourceforge.net/ns'> tags from document
				NodeList nodes = doc.getElementsByTagName(TAG_RDF_DESCRIPTION);
				for (int i = 0; i< nodes.getLength(); i++) {
					Element elem =  (Element) nodes.item(i);
					elem.getParentNode().removeChild(elem);
				}
				
				// Append new rdf:Description tag
				Element description = doc.createElement(TAG_RDF_DESCRIPTION);
				description.setAttribute("xmlns:faint", VAL_NAMESPACE);
				rdfTag.appendChild(description);
				
				// Append faint:Faces tag and rdf:Bag tag
				Element faces = doc.createElement(TAG_FACES);
				description.appendChild(faces);
				Element bag = doc.createElement(TAG_RDF_BAG);
				faces.appendChild(bag);
				
				// Append rdf:li tag for every region
				for (Region r : db.getRegionsForImage(file)) {
					Element li = doc.createElement(TAG_RDF_LI);
					li.setAttribute(ATT_RDF_PARSETYPE, VAL_RDF_RESOURCE);
					bag.appendChild(li);
					
					Element name = doc.createElement(TAG_PERSON);
					name.setTextContent(db.getAnnotation(r));
					li.appendChild(name);

					Element location = doc.createElement(TAG_LOCATION);
					location.setTextContent(r.getX() + " " + r.getY());
					li.appendChild(location);

					Element dimension= doc.createElement(TAG_DIMENSION);
					dimension.setTextContent(r.getWidth() +" " + r.getHeight());
					li.appendChild(dimension);

					Element angle = doc.createElement(TAG_ANGLE);
					angle.setTextContent(Double.toString(r.getAngle()));
					li.appendChild(angle);
				}
				
				// Check if update is necessary
				rdfTag.normalize();
				if (!packet.storeXML(doc)) {
					comment ="validated.";
					break process;
				}
				
				// Insert new packet and write changed file to disk
				try {
					jpeg.addXMPpacket(packet);
					jpeg.writeChanges();
					comment = "updated.";
				} catch (IOException e) {
					e.printStackTrace();
					comment = "skipped! (Write error)";
				}
			}
			else comment = "skipped! (not a JPEG)";
			
			publish(file + " -> " + comment);
		}

		return "Finished";
	}
	
}
