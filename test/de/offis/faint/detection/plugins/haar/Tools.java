package de.offis.faint.detection.plugins.haar;

import java.io.*;
import java.net.URL;
import de.offis.faint.detection.plugins.opencv.OpenCVDetection;
import javax.xml.stream.XMLStreamException;

/**
 * Generated JavaDoc Comment.
 *
 * @author <a href="mailto:matt.nathan@paphotos.com">Matt Nathan</a>
 */
public class Tools {

    /**
     * Serialise the given cascade xml file into the given output stream as a serialised form. The output strem will not
     * be closed.
     *
     * @param xmlFile The source file
     * @param out     The output stream
     * @throws IOException if something goes wrong.
     * @throws XMLStreamException if somethign is wrong with the xml
     */
    public static void generateBinCascadeDesc(URL xmlFile, OutputStream out) throws IOException, XMLStreamException {

        ClassifierCascade cascade = null;

        InputStream in = null;
        try {
            in = xmlFile.openStream();
            cascade = Cascades.readFromXML(in);
        } finally {
            if (in != null) {
                in.close();
            }
        }

        if (cascade == null) {
            throw new IOException("no cascade found");
        }

        ObjectOutputStream write = new ObjectOutputStream(out);
        write.writeObject(cascade);
    }





    public static void main(String[] args) throws IOException, XMLStreamException {

        final String[] toConvert = {
                "haarcascade_frontalface_default",
                "haarcascade_frontalface_alt",
                "haarcascade_frontalface_alt2",
                "haarcascade_frontalface_alt_tree",
        };


        for (String fileName : toConvert) {
            URL in = OpenCVDetection.class.getResource(fileName + ".xml");
            assert in != null: "The resource [" + fileName + ".xml" + "] was not found";

            File outPath = new File("src/de/offis/faint/detection/plugins/haar", fileName + ".bin");

            OutputStream out = null;
            try {
                out = new FileOutputStream(outPath);
                Tools.generateBinCascadeDesc(in, out);
            } finally {
                if (out != null) {
                    out.close();
                }
            }
        }
    }
}
