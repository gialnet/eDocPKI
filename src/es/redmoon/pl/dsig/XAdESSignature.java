package es.redmoon.pl.dsig;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import oracle.sql.BLOB;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import es.redmoon.utils.Base64;

/**
 * Class to parse XAdES signatures.
 * @version 0.1
 */
public class XAdESSignature {
	
	/**
	 * Gets the Subject of the certificate of the signature.
	 * @param signature The signature.
	 * @return The Issuer or null in case of error.
	 */
	public static String getSubject(BLOB signature){
		
		try {
			
			DocumentBuilder documentBuilder;
						
			// Get the length of the blob
			int length = (int) signature.length();

			byte bytes[] = signature.getBytes(1, length);
			String firma= new String(bytes);

			// 1.- Open the file
			documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = documentBuilder.parse(new ByteArrayInputStream(firma.getBytes("UTF-8")));

			// 2.- Extract certificate
			NodeList listaNodos = doc.getElementsByTagName("ds:X509Certificate");
				
			// Only one node with certificate
			Node nodo = listaNodos.item(0);
				
			//System.out.println(nodo.getTextContent());

			// 3.- Certificate Base64 decode
			byte[] bytesCert = Base64.decode(nodo.getTextContent());

			// 4.- Get certificate from bytes
			InputStream is = new ByteArrayInputStream(bytesCert);
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			X509Certificate certX509 = (X509Certificate) cf.generateCertificate(is);
			is.close();

			// Get subject (Issuer) from Certificate 
			String subject = certX509.getSubjectX500Principal().getName();
			
			return subject;
			
		} catch (ParserConfigurationException e) {
			String error = "Error creating DocumentBuilder.";
			Logger.getLogger(XAdESSignature.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (SAXException e) {
			String error = "Error pharsing document.";
			Logger.getLogger(XAdESSignature.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (IOException e) {
			String error = "Error Base64 decoding or closing certificate InputStream.";
			Logger.getLogger(XAdESSignature.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (CertificateException e) {
			String error = "Error getting certificate.";
			Logger.getLogger(XAdESSignature.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (SQLException e) {
			String error = "Error getting signature.";
			Logger.getLogger(XAdESSignature.class.getName()).log(Level.SEVERE, error, e);
			return null;
		}		
	}
	
	
	/**
	 * Gets the serial number of the signature certificate.
	 * @param signature The signature.
	 * @return The serial number or null in case of error.
	 */
	public static String getSerial(BLOB signature){
		
		try {
			
			DocumentBuilder documentBuilder;
						
			// Get the length of the blob
			int length = (int) signature.length();

			byte bytes[] = signature.getBytes(1, length);
			String firma= new String(bytes);

			// 1.- Open the file
			documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = documentBuilder.parse(new ByteArrayInputStream(firma.getBytes("UTF-8")));

			// 2.- Extract certificate
			NodeList listaNodos = doc.getElementsByTagName("ds:X509Certificate");
				
			// Only one node with certificate
			Node nodo = listaNodos.item(0);
				
			//System.out.println(nodo.getTextContent());

			// 3.- Certificate Base64 decode
			byte[] bytesCert = Base64.decode(nodo.getTextContent());

			// 4.- Get certificate from bytes
			InputStream is = new ByteArrayInputStream(bytesCert);
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			X509Certificate certX509 = (X509Certificate) cf.generateCertificate(is);
			is.close();

			return certX509.getSerialNumber().toString();
			
		} catch (ParserConfigurationException e) {
			String error = "Error creating DocumentBuilder.";
			Logger.getLogger(XAdESSignature.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (SAXException e) {
			String error = "Error pharsing document.";
			Logger.getLogger(XAdESSignature.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (IOException e) {
			String error = "Error Base64 decoding or closing certificate InputStream.";
			Logger.getLogger(XAdESSignature.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (CertificateException e) {
			String error = "Error getting certificate.";
			Logger.getLogger(XAdESSignature.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (SQLException e) {
			String error = "Error getting signature.";
			Logger.getLogger(XAdESSignature.class.getName()).log(Level.SEVERE, error, e);
			return null;
		}		
	}

	
	/**
	 * Gets the notBefore date from the validity period of the signature certificate.
	 * @param signature The signature.
	 * @return The initial valid date or null in case of error.
	 */
	public static String getNotBefore(BLOB signature){
		
		try {
			
			DocumentBuilder documentBuilder;
						
			// Get the length of the blob
			int length = (int) signature.length();

			byte bytes[] = signature.getBytes(1, length);
			String firma= new String(bytes);

			// 1.- Open the file
			documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = documentBuilder.parse(new ByteArrayInputStream(firma.getBytes("UTF-8")));

			// 2.- Extract certificate
			NodeList listaNodos = doc.getElementsByTagName("ds:X509Certificate");
				
			// Only one node with certificate
			Node nodo = listaNodos.item(0);
				
			//System.out.println(nodo.getTextContent());

			// 3.- Certificate Base64 decode
			byte[] bytesCert = Base64.decode(nodo.getTextContent());

			// 4.- Get certificate from bytes
			InputStream is = new ByteArrayInputStream(bytesCert);
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			X509Certificate certX509 = (X509Certificate) cf.generateCertificate(is);
			is.close();

			Date notBefore = certX509.getNotBefore();
			SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy-hh:mm:ss");
			
			return formateador.format(notBefore);
			
		} catch (ParserConfigurationException e) {
			String error = "Error creating DocumentBuilder.";
			Logger.getLogger(XAdESSignature.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (SAXException e) {
			String error = "Error pharsing document.";
			Logger.getLogger(XAdESSignature.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (IOException e) {
			String error = "Error Base64 decoding or closing certificate InputStream.";
			Logger.getLogger(XAdESSignature.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (CertificateException e) {
			String error = "Error getting certificate.";
			Logger.getLogger(XAdESSignature.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (SQLException e) {
			String error = "Error getting signature.";
			Logger.getLogger(XAdESSignature.class.getName()).log(Level.SEVERE, error, e);
			return null;
		}		
	}
	

	/**
	 * Gets the notAfter date from the validity period of the signature certificate.
	 * @param signature The signature.
	 * @return The initial valid date or null in case of error.
	 */
	public static String getNotAfter(BLOB signature){
		
		try {
			
			DocumentBuilder documentBuilder;
						
			// Get the length of the blob
			int length = (int) signature.length();

			byte bytes[] = signature.getBytes(1, length);
			String firma= new String(bytes);

			// 1.- Open the file
			documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = documentBuilder.parse(new ByteArrayInputStream(firma.getBytes("UTF-8")));

			// 2.- Extract certificate
			NodeList listaNodos = doc.getElementsByTagName("ds:X509Certificate");
				
			// Only one node with certificate
			Node nodo = listaNodos.item(0);
				
			//System.out.println(nodo.getTextContent());

			// 3.- Certificate Base64 decode
			byte[] bytesCert = Base64.decode(nodo.getTextContent());

			// 4.- Get certificate from bytes
			InputStream is = new ByteArrayInputStream(bytesCert);
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			X509Certificate certX509 = (X509Certificate) cf.generateCertificate(is);
			is.close();

			Date notAfter = certX509.getNotAfter();
			SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy-hh:mm:ss");
			
			return formateador.format(notAfter);
			
		} catch (ParserConfigurationException e) {
			String error = "Error creating DocumentBuilder.";
			Logger.getLogger(XAdESSignature.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (SAXException e) {
			String error = "Error pharsing document.";
			Logger.getLogger(XAdESSignature.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (IOException e) {
			String error = "Error Base64 decoding or closing certificate InputStream.";
			Logger.getLogger(XAdESSignature.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (CertificateException e) {
			String error = "Error getting certificate.";
			Logger.getLogger(XAdESSignature.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (SQLException e) {
			String error = "Error getting signature.";
			Logger.getLogger(XAdESSignature.class.getName()).log(Level.SEVERE, error, e);
			return null;
		}		
	}	

}
