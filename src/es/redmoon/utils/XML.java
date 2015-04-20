package es.redmoon.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import oracle.sql.BLOB;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

//import es.mityc.firmaJava.libreria.utilidades.UtilidadTratarNodo;

/**
 * Utils for XML documents.
 * @author Ángel L. García <angel@redmoon.es>
 * @version 0.1
 *
 */
public class XML {
	
	/**
	 * Save a document to file.
	 * @param document The document.
	 * @param pathfile The path name of file.
	 */
	public static void saveDocumentToFile(Document document, String pathfile) {
        try {
            FileOutputStream fos = new FileOutputStream(pathfile);
            
            //org.apache.xml.security.utils.XMLUtils.outputDOM(document, fos, true);
            
    		TransformerFactory tf = TransformerFactory.newInstance();
    		Transformer trans = tf.newTransformer();
    		trans.transform(new DOMSource(document), new StreamResult(fos));
    		
    		fos.close();
            
            
        } catch (Exception e) {
        	Logger.getLogger(XML.class.getName()).log(Level.SEVERE, "Error saving the document.", e);
        }
    }

	/**
	 * Store a document in a BLOB.
	 * @param document The document.
	 * @return A BLOB containing the document or null in case of error.
	 */
	public static BLOB storeDocumentToBLOB (Document document) {
		
		Connection conn = null;
		BLOB blobDocument = null;
		
		try {
			//Class.forName("oracle.jdbc.driver.OracleDriver");
			//conn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.2.150:1521:orcl", "edocpki", "redmoon");
			
			conn = DriverManager.getConnection("jdbc:default:connection");
			
			ByteArrayOutputStream outArray = new ByteArrayOutputStream();
			
			//org.apache.xml.security.utils.XMLUtils.outputDOM(document, outArray, false);
			
			TransformerFactory tf = TransformerFactory.newInstance();
    		Transformer trans = tf.newTransformer();
    		trans.transform(new DOMSource(document), new StreamResult(outArray));
    		
    		outArray.close();
			
			// Create temporal BLOB
			blobDocument = BLOB.createTemporary(conn, true, BLOB.DURATION_SESSION);
			
			// Write document to temporal BLOB
			OutputStream outStream = blobDocument.setBinaryStream(1L);
			outStream.write(outArray.toString("UTF-8").getBytes());
			outStream.close();
			
		} catch (Exception e) {
			Logger.getLogger(XML.class.getName()).log(Level.SEVERE, "Error storing document in BLOB.", e);
			return null;
		}
		
		return blobDocument;
	}
	
	/**
	 * Get a document stored in a BLOB.
	 * @param blobDocument The BLOB with the document.
	 * @return The document stored in the BLOB or null in case of error.
	 */
	public static Document getDocumentFromBLOB (BLOB blobDocument) {
		
		try {
			// Get the length of the blob
			int length = (int) blobDocument.length();

			// Get bytes of the blob
			byte bytes[] = blobDocument.getBytes(1, length);

			// Parse the document from bytes
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = documentBuilder.parse(new ByteArrayInputStream(bytes));

			return document;
		} catch (SQLException e) {
			Logger.getLogger(XML.class.getName()).log(Level.SEVERE, "Error getting the BLOB bytes.", e);
			return null;
		} catch (ParserConfigurationException e) {
			Logger.getLogger(XML.class.getName()).log(Level.SEVERE, "Error getting the DocumentBuilder.", e);
			return null;
		} catch (SAXException e) {
			Logger.getLogger(XML.class.getName()).log(Level.SEVERE, "Error parsing the BLOB bytes.", e);
			return null;
		} catch (IOException e) {
			Logger.getLogger(XML.class.getName()).log(Level.SEVERE, "Error parsing the BLOB bytes.", e);
			return null;
		}
	}
	
}
