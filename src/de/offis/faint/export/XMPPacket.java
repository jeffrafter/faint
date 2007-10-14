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

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author maltech
 *
 */
public class XMPPacket {
		
	private static final String EMPTY_PACKET = "<?xpacket begin='\uFEFF' id='W5M0MpCehiHzreSzNTczkc9d'?>\n" +
                                                   "<x:xmpmeta xmlns:x='adobe:ns:meta/'>\n"+
                                                      "<rdf:RDF xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'>\n"+
                                                      "</rdf:RDF>\n"+
                                                   "</x:xmpmeta>\n"+
	                                            "<?xpacket end='w'?>";
	
	private String data;
	private String encoding;

	/**
	 * Creates an empty packet using the JVM's default encoding
	 */
	public XMPPacket(){
		this("" + EMPTY_PACKET, "UTF-8");
	}
	
	public XMPPacket(String data, String encoding){
		this.data = data;
		this.encoding = encoding;
		System.err.println(encoding);
	}
	
	public String getDataString(){
		return data;
	}
	
	public String getEncoding(){
		return encoding;
	}
	
	public Document parseXML() throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory factory  = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder  = factory.newDocumentBuilder();
		return builder.parse(new InputSource(new StringReader(data)));
	}
	
	
	/**
	 * Replaces the data String with a data String created from a given XML document.
	 * Returns true if old and new String are not equal.
	 * 
	 * @param doc
	 * @return
	 * @throws TransformerException
	 */
	public boolean storeXML(Document doc) throws TransformerException{
	      TransformerFactory tranFactory = TransformerFactory.newInstance();
	         Transformer aTransformer = tranFactory.newTransformer();
	         aTransformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	         aTransformer.setOutputProperty(OutputKeys.INDENT, "no");
	         DOMSource src = new DOMSource(doc);
	         StringWriter stringWriter=new StringWriter();
	         StreamResult dest = new StreamResult(stringWriter);
	         aTransformer.transform(src,dest);
	         String output = stringWriter.toString();
	         while (output.contains("\n\n"))
	        	 output.replace("\n\n", "\n");
	         
	         if (this.data.equals(output))
	        	 return false;
	         
	         this.data = output;
	         return true;
	}
}
