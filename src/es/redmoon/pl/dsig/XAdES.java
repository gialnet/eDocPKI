package es.redmoon.pl.dsig;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;

import es.redmoon.utils.XML;
import oracle.sql.BLOB;

/**
 * Class for create XAdES signatures. For work loadded/embedded in database.
 * @version 0.1
 */
public class XAdES {

	/**
	 * Create a XAdES BES Enveloped Signature.
	 * @param keystore Keystore with certificate for the signature.
	 * @param keystoreType The keystore's type. (Only support JKS and PKCS12 types).
	 * @param keystorePassword Password of keystore.
	 * @param privateKeyPassword The private key password.
	 * @param document The document to sign.
	 * @param name The name of the document.
	 * @param description A short description for the document.
	 * @param mimeType The mime type of the document.
	 * @return A BLOB containing XAdES BES Enveloped Signature or null in case of error.
	 */
	public static BLOB createEnvelopedBES(BLOB keystore, String keystoreType, String keystorePassword, String privateKeyPassword, BLOB document, String name, String description, String mimeType) {

		InputStream isKeyStore = null;
		InputStream isDocument = null;
		
		BLOB blobSignature = null;
		Document docSigned = null;
		
		try {
			
			isKeyStore = keystore.getBinaryStream();
			isDocument = document.getBinaryStream();
			
			// Sign the document
			docSigned = es.redmoon.dsig.XAdES.generateEnvelopedBES(isKeyStore, keystoreType, keystorePassword, privateKeyPassword, isDocument, name, description, mimeType);
			
			blobSignature = XML.storeDocumentToBLOB(docSigned);
			
			return blobSignature;
			
		} catch (Exception e) {
			Logger.getLogger(XAdES.class.getName()).log(Level.SEVERE, "Error signing document.", e);
			return null;
		}
	}

	/**
	 * Create a XAdES BES Enveloping Signature.
	 * @param keystore Keystore with certificate for the signature.
	 * @param keystoreType The keystore's type. (Only support JKS and PKCS12 types).
	 * @param keystorePassword Password of keystore.
	 * @param privateKeyPassword The private key password.
	 * @param document The document to sign.
	 * @param name The name of the document.
	 * @param description A short description for the document.
	 * @param mimeType The mime type of the document.
	 * @return A BLOB containing XAdES BES Enveloping Signature or null in case of error.
	 */
	public static BLOB createEnvelopingBES(BLOB keystore, String keystoreType, String keystorePassword, String privateKeyPassword, BLOB document, String name, String description, String mimeType) {

		InputStream isKeyStore = null;
		InputStream isDocument = null;
		
		BLOB blobSignature = null;
		Document docSigned = null;
		
		try {
			
			isKeyStore = keystore.getBinaryStream();
			isDocument = document.getBinaryStream();
			
			// Sign the document
			docSigned = es.redmoon.dsig.XAdES.generateEnvelopingBES(isKeyStore, keystoreType, keystorePassword, privateKeyPassword, isDocument, name, description, mimeType);
			
			blobSignature = XML.storeDocumentToBLOB(docSigned);
			
			return blobSignature;
			
		} catch (Exception e) {
			Logger.getLogger(XAdES.class.getName()).log(Level.SEVERE, "Error signing document.", e);
			return null;
		}
	}
	
	/**
	 * Create a XAdES BES Detached Signature.
	 * @param keystore Keystore with certificate for the signature.
	 * @param keystoreType The keystore's type. (Only support JKS and PKCS12 types).
	 * @param keystorePassword Password of keystore.
	 * @param privateKeyPassword The private key password.
	 * @param document The document to sign.
	 * @param name The name of the document.
	 * @param description A short description for the document.
	 * @param mimeType The mime type of the document.
	 * @return A BLOB containing XAdES BES Detached Signature or null in case of error.
	 */
	public static BLOB createDetachedBES(BLOB keystore, String keystoreType, String keystorePassword, String privateKeyPassword, BLOB document, String name, String description, String mimeType) {

		InputStream isKeyStore = null;
		InputStream isDocument = null;
		
		BLOB blobSignature = null;
		Document docSigned = null;
		
		try {
			
			isKeyStore = keystore.getBinaryStream();
			isDocument = document.getBinaryStream();
			
			// Sign the document
			docSigned = es.redmoon.dsig.XAdES.generateDetachedBES(isKeyStore, keystoreType, keystorePassword, privateKeyPassword, isDocument, name, description, mimeType);
			
			blobSignature = XML.storeDocumentToBLOB(docSigned);
			
			return blobSignature;
			
		} catch (Exception e) {
			Logger.getLogger(XAdES.class.getName()).log(Level.SEVERE, "Error signing document.", e);
			return null;
		}
	}

	/**
	 * Create a XAdES-T BES Enveloped Signature.
	 * @param keystore Keystore with certificate for the signature.
	 * @param keystoreType The keystore's type. (Only support JKS and PKCS12 types).
	 * @param keystorePassword Password of keystore.
	 * @param privateKeyPassword The private key password.
	 * @param document The document to sign.
	 * @param name The name of the document.
	 * @param description A short description for the document.
	 * @param mimeType The mime type of the document.
	 * @param tsaUrl The TSA's URL.
	 * @param tsaUserName The username for TSA, can be null if authentication isn't necessary.
	 * @param tsaPassword The password for TSA, can be null if authentication isn't necessary.
	 * @return A BLOB containing XAdES-T BES Enveloped Signature or null in case of error.
	 */
	public static BLOB createEnvelopedBES_T(BLOB keystore, String keystoreType, String keystorePassword, String privateKeyPassword, BLOB document, String name, String description, String mimeType, String tsaUrl, String tsaUserName, String tsaPassword) {

		InputStream isKeyStore = null;
		InputStream isDocument = null;
		
		BLOB blobSignature = null;
		Document docSigned = null;
		
		try {
			
			isKeyStore = keystore.getBinaryStream();
			isDocument = document.getBinaryStream();
			
			// Sign the document
			docSigned = es.redmoon.dsig.XAdES.generateEnvelopedBES_T(isKeyStore, keystoreType, keystorePassword, privateKeyPassword, isDocument, name, description, mimeType, tsaUrl, tsaUserName, tsaPassword);

			blobSignature = XML.storeDocumentToBLOB(docSigned);
			
			return blobSignature;
			
		} catch (Exception e) {
			Logger.getLogger(XAdES.class.getName()).log(Level.SEVERE, "Error signing document.", e);
			return null;
		}
	}
	
	/**
	 * Create a XAdES-T BES Enveloping Signature.
	 * @param keystore Keystore with certificate for the signature.
	 * @param keystoreType The keystore's type. (Only support JKS and PKCS12 types).
	 * @param keystorePassword Password of keystore.
	 * @param privateKeyPassword The private key password.
	 * @param document The document to sign.
	 * @param name The name of the document.
	 * @param description A short description for the document.
	 * @param mimeType The mime type of the document.
	 * @param tsaUrl The TSA's URL.
	 * @param tsaUserName The username for TSA, can be null if authentication isn't necessary.
	 * @param tsaPassword The password for TSA, can be null if authentication isn't necessary.
	 * @return A BLOB containing XAdES-T BES Enveloping Signature or null in case of error.
	 */
	public static BLOB createEnvelopingBES_T(BLOB keystore, String keystoreType, String keystorePassword, String privateKeyPassword, BLOB document, String name, String description, String mimeType, String tsaUrl, String tsaUserName, String tsaPassword) {

		InputStream isKeyStore = null;
		InputStream isDocument = null;
		
		BLOB blobSignature = null;
		Document docSigned = null;
		
		try {
			
			isKeyStore = keystore.getBinaryStream();
			isDocument = document.getBinaryStream();
			
			// Sign the document
			docSigned = es.redmoon.dsig.XAdES.generateEnvelopingBES_T(isKeyStore, keystoreType, keystorePassword, privateKeyPassword, isDocument, name, description, mimeType, tsaUrl, tsaUserName, tsaPassword);

			blobSignature = XML.storeDocumentToBLOB(docSigned);
			
			return blobSignature;
			
		} catch (Exception e) {
			Logger.getLogger(XAdES.class.getName()).log(Level.SEVERE, "Error signing document.", e);
			return null;
		}
	}
	
	/**
	 * Create a XAdES-T BES Detached Signature.
	 * @param keystore Keystore with certificate for the signature.
	 * @param keystoreType The keystore's type. (Only support JKS and PKCS12 types).
	 * @param keystorePassword Password of keystore.
	 * @param privateKeyPassword The private key password.
	 * @param document The document to sign.
	 * @param name The name of the document.
	 * @param description A short description for the document.
	 * @param mimeType The mime type of the document.
	 * @param tsaUrl The TSA's URL.
	 * @param tsaUserName The username for TSA, can be null if authentication isn't necessary.
	 * @param tsaPassword The password for TSA, can be null if authentication isn't necessary.
	 * @return A BLOB containing XAdES-T BES Detached Signature or null in case of error.
	 */
	public static BLOB createDetachedBES_T(BLOB keystore, String keystoreType, String keystorePassword, String privateKeyPassword, BLOB document, String name, String description, String mimeType, String tsaUrl, String tsaUserName, String tsaPassword) {

		InputStream isKeyStore = null;
		InputStream isDocument = null;
		
		BLOB blobSignature = null;
		Document docSigned = null;
		
		try {
			
			isKeyStore = keystore.getBinaryStream();
			isDocument = document.getBinaryStream();
			
			// Sign the document
			docSigned = es.redmoon.dsig.XAdES.generateDetachedBES_T(isKeyStore, keystoreType, keystorePassword, privateKeyPassword, isDocument, name, description, mimeType, tsaUrl, tsaUserName, tsaPassword);

			blobSignature = XML.storeDocumentToBLOB(docSigned);
			
			return blobSignature;
			
		} catch (Exception e) {
			Logger.getLogger(XAdES.class.getName()).log(Level.SEVERE, "Error signing document.", e);
			return null;
		}
	}
}
