package es.redmoon.pl.dsig;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.sql.SQLException;

import oracle.sql.BLOB;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import es.redmoon.utils.LOBUtils;

/**
 * Class for create PKCS7 signatures.
 * @version 0.1
 */
public class PKCS7 {

	/**
	 * Create PKCS7 signature.
	 * @param keystore Keystore with certificate for the signature.
	 * @param password Password of keystore.
	 * @param document The document to sign.
	 * @return BLOB containing PKCS7 signature or null in case of error.
	 */
	public static BLOB createSignature (BLOB keystore, String password, BLOB document){
		
		Security.addProvider(new BouncyCastleProvider());
		BLOB signedBlob = null;
		
		try {
			
			// Get keystore from BLOB.
			KeyStore ks = KeyStore.getInstance("PKCS12");
			ks.load(new ByteArrayInputStream(keystore.getBytes(1, (int)keystore.length())), password.toCharArray());

			// Get certificate from keystore
			Enumeration<String> enumAlias = ks.aliases();
			String alias = enumAlias.nextElement();
			
			Certificate[] certificateChain = ks.getCertificateChain(alias);
			X509Certificate certificate = (X509Certificate) ks.getCertificate(alias);
			
			// Get private key from certificate
			PrivateKey privateKey = (PrivateKey) ks.getKey(alias, password.toCharArray());

			ArrayList<Certificate> certList = new ArrayList<Certificate>();
			for (int i = 0; i < certificateChain.length; i++)
				certList.add(certificateChain[i]);
			
			CertStore certStore = CertStore.getInstance("Collection", new CollectionCertStoreParameters(certList), "BC");
			
			byte[] bytesDocument = document.getBytes(1, (int)document.length());
						
			CMSSignedDataGenerator signGen = new CMSSignedDataGenerator();
			signGen.addSigner(privateKey, certificate, CMSSignedDataGenerator.DIGEST_SHA1);
			signGen.addCertificatesAndCRLs(certStore);
			CMSProcessable content = new CMSProcessableByteArray(bytesDocument);
			
			CMSSignedData signedData = signGen.generate(content, "BC");
			byte[] signedDataBytes = signedData.getEncoded();
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			baos.write(signedDataBytes);
			
			signedBlob = LOBUtils.OutputStreamToBLOB(baos);
			
		} catch (KeyStoreException e) {
			String error = "Error getting keystore.";
			Logger.getLogger(PKCS7.class.getName()).log(Level.SEVERE, error, e);
			signedBlob = null;
		} catch (NoSuchAlgorithmException e) {
			String error = "Error loading keystore.";
			Logger.getLogger(PKCS7.class.getName()).log(Level.SEVERE, error, e);
			signedBlob = null;
		} catch (CertificateException e) {
			String error = "Error loading keystore.";
			Logger.getLogger(PKCS7.class.getName()).log(Level.SEVERE, error, e);
			signedBlob = null;
		} catch (IOException e) {
			String error = "Error creating signature.";
			Logger.getLogger(PKCS7.class.getName()).log(Level.SEVERE, error, e);
			signedBlob = null;
		} catch (SQLException e) {
			String error = "Error getting bytes from document.";
			Logger.getLogger(PKCS7.class.getName()).log(Level.SEVERE, error, e);
			signedBlob = null;
		} catch (UnrecoverableKeyException e) {
			String error = "Error getting private key.";
			Logger.getLogger(PKCS7.class.getName()).log(Level.SEVERE, error, e);
			signedBlob = null;
		} catch (InvalidAlgorithmParameterException e) {
			String error = "Error getting certificate store.";
			Logger.getLogger(PKCS7.class.getName()).log(Level.SEVERE, error, e);
			signedBlob = null;
		} catch (NoSuchProviderException e) {
			String error = "Error with BouncyCastle provider.";
			Logger.getLogger(PKCS7.class.getName()).log(Level.SEVERE, error, e);
			signedBlob = null;
		} catch (CertStoreException e) {
			String error = "Fallo al añadir los certificados.";
			Logger.getLogger(PKCS7.class.getName()).log(Level.SEVERE, error, e);
			signedBlob = null;
		} catch (CMSException e) {
			String error = "Error creating signature.";
			Logger.getLogger(PKCS7.class.getName()).log(Level.SEVERE, error, e);
			signedBlob = null;
		}
		
		return signedBlob;
	}
}
