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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.UnknownServiceException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * This class contains methods to extract and modify header segments of a JPEG
 * file.
 * 
 * @author maltech
 *
 */
public class JPEG_Adapter {

	// ------ JPEG segment markers -------
	private final static byte SOI  = (byte) 0xD8; // Start Of Image
	private final static byte SOS  = (byte) 0xDA; // Start Of Scan
	private final static byte EOI  = (byte) 0xD9; // End Of Image
	private final static byte APP1 = (byte) 0xE1; // Segment used for EXIF and XMP data
	
	// ------ XMP marker in APP1 segment (ASCII Charset, null-terminated)-------
	private final static String XMP_MARKER = "http://ns.adobe.com/xap/1.0/\0";
	private final static String XMP_MARKER_ENCODING = "US-ASCII";
	
	// ------ start of XMP packet
	private static String XMP_PACKET_START = "<?xpacket";

	// ------ possible Charsets for XMP data
	private final static String[] UTF_CHARSETS = {"UTF-8","UTF-16LE", "UTF-16BE", "UTF-32LE", "UTF-32BE"};

	
	private URL jpegFile;
	private long headerSize;
	private ArrayList<byte[]> headerSegments = new ArrayList<byte[]>();
	
	
	/**
	 * Constructor. Validates the SOI tag of a given JPEG and stores the header
	 * segments in an internal ArrayList.
	 * 
	 * @param jpegFile
	 * @throws IOException
	 */
	public JPEG_Adapter(URL jpegFile) throws IOException{
		
		// prepare InputStream for file
		this.jpegFile = jpegFile;
		InputStream in = jpegFile.openStream();		
		
		// validate SOI
		if (in.read() != 0xFF || in.read() != (SOI & 0xFF))
			throw new IOException("Not a valid JPEG");
		
		// loop through segments
		headerSize = 2;
		byte[] buffer = new byte[4];
		loop: while (in.read(buffer) != -1 && (buffer[0] & 0xFF) == 0xFF) {

			// SOS marker indicates end of header
			if (buffer[1] == SOS || buffer[1] == EOI) {
				break loop;
			}

			// read size of segment
			int segmentSize = 256 * (buffer[2] & 0xFF) + (buffer[3] & 0xFF);
			
			// allocate new segment array in list
			byte[] segment = new byte[segmentSize + 2];
			headerSegments.add(segment);
			
			// store segment data in segment array
			System.arraycopy(buffer, 0, segment, 0, buffer.length);
			in.read(segment, buffer.length, segmentSize - 2);
			
			headerSize += segment.length;
		}
	}
	
	
	/**
	 * Returns the list of header segments found in the JPEG file. Changes to
	 * this list or its elements will affect the header if writeChanges() is
	 * called.
	 * 
	 * @return
	 */
	public ArrayList<byte[]> getHeaderSegments() {
		return headerSegments;
	}

	
	/**
	 * Scans the header segments for XMP markers and returns a HashMap
	 * containing all XMP packets found and the associated segment indexes.
	 * 
	 * @return
	 * @throws IOException
	 */
	public HashMap<Integer, XMPPacket> extractXMPpackets(){
		
		HashMap<Integer, XMPPacket> xmpPackets = new HashMap<Integer, XMPPacket>();
		
		for (int i=0; i<headerSegments.size(); i++) {
			byte[] segment = headerSegments.get(i);
			
			try {
				if (segment[1] == APP1 && (new String(segment, 4, XMP_MARKER.length(), XMP_MARKER_ENCODING).equals(XMP_MARKER))) {
					
					int offset = XMP_MARKER.length() + 4;

					// find fitting UTF Charset for XMP packet
					for (String enc : UTF_CHARSETS){
						try {
							String data = new String(segment, offset, segment.length - offset, enc);
							if (data.startsWith(XMP_PACKET_START)) {
								xmpPackets.put(i, new XMPPacket(data, enc));
								break;
							}
						}
						catch (UnsupportedEncodingException e){} // The VM might not support UTF-32
					}
				}
			}
			catch (UnsupportedEncodingException e) {} // US-ASCII is always supported (see java.nio.charset.Charset)
			catch (ArrayIndexOutOfBoundsException e) {} // APP1 segment to small for XMP_MARKER
		}
		return xmpPackets;
	}
	
	/**
	 * Stores an XMP packet inside a new APP1 segment and appends it behind the
	 * last existing APP1 segment in the list of header segments.
	 * 
	 * @param packet
	 * @throws IOException 
	 */
	public void addXMPpacket(XMPPacket packet) throws IOException{
		
		byte[] packetData = packet.getDataString().getBytes(packet.getEncoding());
		byte[] segment = new byte[packetData.length + XMP_MARKER.length() + 4];
		
		if ((packetData.length + 2) > 65535)
			throw new IOException("Packet exeeds 64 Kb.");

		// Store APP1 marker
		segment[0] = (byte) 0xFF;
		segment[1] = APP1;
		
		//Store size
		int lowerByte = (segment.length - 2)%256;
		int higherByte = (segment.length -2 - lowerByte) / 256;
		segment[2] = (byte) higherByte;
		segment[3] = (byte) lowerByte;
		
		// Store XMP marker
		try {
			byte[] xmpMarker = XMP_MARKER.getBytes(XMP_MARKER_ENCODING);
			System.arraycopy(xmpMarker, 0, segment, 4, xmpMarker.length);
		} catch (UnsupportedEncodingException e) {} // US-ASCII is always supported (see java.nio.charset.Charset)
		
		// Store packet data
		System.arraycopy(packetData, 0, segment, XMP_MARKER.length() + 4, packetData.length);
		
		// Append new segment after last APP1 marker found
		int segmentIndex = 0;
		for (int i = 0; i<headerSegments.size(); i++) {
			if (headerSegments.get(i)[1] == APP1) {
				segmentIndex = i+1;
			}
		}
		headerSegments.add(segmentIndex, segment);
	}
	
	
	/**
	 * Writes the JPEG file with modified header segments to disk.
	 * 
	 * @throws IOException
	 */
	public void writeChanges() throws IOException {
		
		// Prepare InputStream for image data
		InputStream in = jpegFile.openStream();		
		in.skip(headerSize);
		
		// Store image data in temporal ArrayList
		ArrayList<byte[]> imageData = new ArrayList<byte[]>();
		int byteCount = 512;
		do {
			byte[] data = new byte[byteCount];
			byteCount = in.read(data);
			
			if (byteCount == 512)
				imageData.add(data);
			
			else if (byteCount != -1) {
				byte[] dataTail = new byte[byteCount];
				System.arraycopy(data, 0, dataTail, 0, byteCount);
				imageData.add(dataTail);
			}
		}
		while (byteCount == 512);
		in.close();
		
		// Prepare OutputStream
		OutputStream out = null; 
		URLConnection connection = jpegFile.openConnection();
		connection.setDoOutput(true);
		try {
			out = connection.getOutputStream();
		} catch (UnknownServiceException e) {

			// Workaround for 'file' protocol
			if (jpegFile.getProtocol().equals("file")) {
				File f = new File(URLDecoder.decode(jpegFile.getPath(), "UTF-8"));
				out = new FileOutputStream(f);
			}			
		}

		// Write SOI to file
		out.write((byte) 0xFF);
		out.write(SOI);
		
		// Write header segments to file and update size of header
		headerSize = 2;
		for (byte[] segment : headerSegments) {
			out.write(segment);
			headerSize += segment.length;
		}
		
		// Write image data to file
		for (byte[] data : imageData) {
			out.write(data);
		}
		
		// Close OutputStream
		out.flush();
		out.close();
	}
	
	public static void main(String[]s){
		System.out.println(XMP_MARKER.length());
	}
}

