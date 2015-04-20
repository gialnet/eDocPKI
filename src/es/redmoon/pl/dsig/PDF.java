package es.redmoon.pl.dsig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfSignatureAppearance;
import com.lowagie.text.pdf.PdfStamper;

import es.redmoon.utils.LOBUtils;

import oracle.sql.BLOB;

/**
 * Class for sign PDF files.
 * @version 0.1
 */
public class PDF {
	
	/**
	 * Sign PDF document.
	 * @param keystore Keystore with certificate for the signature.
	 * @param password Password of keystore.
	 * @param document The document to sign.
	 * @return BLOB containing signed PDF document or null in case of error.
	 */
	public static BLOB createSignature (BLOB keystore, String password, BLOB document){
		
		OutputStream outStream = null;
		PdfStamper stamper = null;
		BLOB signedBlob = null;
		
		try {
			
			// Get keystore from BLOB.
			KeyStore ks = KeyStore.getInstance("PKCS12");
			ks.load(new ByteArrayInputStream(keystore.getBytes(1, (int)keystore.length())), password.toCharArray());

			// Get certificate from keystore
			Enumeration<String> enumAlias = ks.aliases();
			String alias = enumAlias.nextElement();
			
			Certificate[] certificate = ks.getCertificateChain(alias);
			
			// Get private key from certificate
			PrivateKey privateKey = (PrivateKey) ks.getKey(alias, password.toCharArray());
			
			// Get document bytes
			byte [] bytesPDF = document.getBytes(1, (int)document.length());
			
			// Read original PDF document
			PdfReader original = new PdfReader(bytesPDF);
			
			// Create out PDF
			outStream = new ByteArrayOutputStream();
			
			stamper = PdfStamper.createSignature(original, outStream, '\0', null, true);
			
			// Sign document in out PDF
			PdfSignatureAppearance sap = stamper.getSignatureAppearance();
			sap.setCrypto(privateKey, certificate, null, PdfSignatureAppearance.WINCER_SIGNED);
			// comment next line to have an invisible signature
			//sap.setVisibleSignature(new Rectangle(395, 535, 505, 630), 1, null);
			
			stamper.close();
			stamper = null;
			
			signedBlob = LOBUtils.OutputStreamToBLOB(outStream);
		    
		} catch (Exception e) {
			Logger.getLogger(PDF.class.getName()).log(Level.SEVERE, "Error signing PDF document.", e);
			signedBlob = null;
		} finally {
			freeStamper(stamper);
			freeOutputStream(outStream);
		}
		
		return signedBlob;
	}
	
	/*
	 * Free PDFStamper resources
	 */
	private static void freeStamper(PdfStamper stamper){
		try {
			if (stamper != null)
				stamper.close();
		} catch (Exception e) {
			Logger.getLogger(PDF.class.getName()).log(Level.SEVERE, "Error freeing PDFStamper resources.", e);
		}
	}
	
	/*
	 * Free OutputStream Resources
	 */
	private static void freeOutputStream(OutputStream outStream){
		try {
			if (outStream != null)
				outStream.close();
		} catch (Exception e) {
			Logger.getLogger(PDF.class.getName()).log(Level.SEVERE, "Error freeing OutputStream resources.", e);
		}
	}
}
