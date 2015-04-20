package es.redmoon.dsig;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.jcp.xml.dsig.internal.dom.XMLDSigRI;
import org.apache.xml.security.c14n.Canonicalizer;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import es.redmoon.pl.ts.TimeStamp;
import es.redmoon.utils.Base64;
import es.redmoon.utils.Stream;

/**
 * Class for generate XAdES signatures.
 * @version 0.1
 */
public class XAdES {

	private static final String XMLDSIG_NS= "http://www.w3.org/2000/09/xmldsig#";
	private static final String XMLDSIG_PREFIX= "ds";
	
	private static final String XAdES_NS= "http://uri.etsi.org/01903/v1.3.2#";
	private static final String XAdES_PREFIX= "etsi";
	
	private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
	
	private static final String ENCODING = "http://www.w3.org/2000/09/xmldsig#base64";
	
	
	/**
	 * Generate Enveloped XAdES BES signature.
	 * @param keystore The keystore with keys to sign.
	 * @param keystoreType The keystore's type. (Only support JKS and PKCS12 types)
	 * @param keystorePassword The keystore's password.
	 * @param privateKeyPassword The private key password.
	 * @param document The document to be signed. The document will be Base64 encoded and XML wappred enveloped. 
	 * @param documentName The document's name.
	 * @param description The document' description.
	 * @param mimeType The document's mime type.
	 * @return Document with Enveloped XAdES BES signature or null in case of error.
	 */
	public static Document generateEnvelopedBES (InputStream keystore, String keystoreType, String keystorePassword, String privateKeyPassword, InputStream document, String documentName, String description, String mimeType) {
		
		Document doc = null;
		
		String KEYSTORE_TYPE = "";
				
		try {
			
			if (keystoreType.equalsIgnoreCase("PKCS12")) {
				KEYSTORE_TYPE = "PKCS12";
			} else if (keystoreType.equalsIgnoreCase("JKS")) {
				KEYSTORE_TYPE = "JKS";
			} else {
				throw new Exception("Bad keystore type: " + keystoreType);
			}
			
			KeyStore ks = KeyStore.getInstance(KEYSTORE_TYPE);
			ks.load(keystore, keystorePassword.toCharArray());

			// Get private key for encrypt
			Enumeration<String> enumAlias = ks.aliases();
			String alias = enumAlias.nextElement();
			PrivateKey privateKey = (PrivateKey) ks.getKey(alias, privateKeyPassword.toCharArray());
			X509Certificate certificate = (X509Certificate) ks.getCertificate(alias);
			
			// Setting Signature Method
			String signatureMethod = null;
			
			if (privateKey.getAlgorithm().equals("DSA")) {
				signatureMethod = SignatureMethod.DSA_SHA1;
			} else if (privateKey.getAlgorithm().equals("RSA")) {
				signatureMethod = SignatureMethod.RSA_SHA1;
			}
			
			// Create a DOM XMLSignatureFactory that will be used to generate the enveloped signature
			XMLSignatureFactory factory = XMLSignatureFactory.getInstance("DOM", new XMLDSigRI());
			
			// Instantiate the document to be signed
			doc = createDocument(document, documentName);
			
			if (doc == null) {
				throw new Exception("Error creating XML document.");
			}
			
			// Creamos el object que contendra QualifyingProperties
			Element elementQualifyingProperties = createElement(doc, "QualifyingProperties", XAdES_PREFIX, XAdES_NS);
			elementQualifyingProperties.setAttributeNS(null, "Target", "#SignatureId");

			Element elementSignedProperties = createElement(doc, "SignedProperties", XAdES_PREFIX, XAdES_NS);
			Attr attrSignedPropertiesId = doc.createAttributeNS(null, "Id");
			attrSignedPropertiesId.setValue("SignedPropertiesId");
			
			elementSignedProperties.setAttributeNodeNS(attrSignedPropertiesId);
			elementSignedProperties.setIdAttributeNS(null, "Id", true);
			
			elementQualifyingProperties.appendChild(elementSignedProperties);
			
			Element elementSignedSignatureProperties = createElement(doc, "SignedSignatureProperties", XAdES_PREFIX, XAdES_NS);
			elementSignedProperties.appendChild(elementSignedSignatureProperties);
			
			// Create SigningTime
			Element elementSigningTime = createElement(doc, "SigningTime", XAdES_PREFIX, XAdES_NS);
			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
			String strDate = simpleDateFormat.format(new Date());
			
			// Pasamos de +0254 a +02:54 de +hhmm a +hh:mm
			strDate = strDate.substring(0, strDate.length() - 2).concat(":").concat(strDate.substring(strDate.length()-2));
			
			elementSigningTime.setTextContent(strDate);
			elementSignedSignatureProperties.appendChild(elementSigningTime);
			
			
			// Create SigningCertificate
			Element elementSigningCertificate = createElement(doc, "SigningCertificate", XAdES_PREFIX, XAdES_NS);
			elementSignedSignatureProperties.appendChild(elementSigningCertificate);
			
			Element elementCert = createElement(doc, "Cert", XAdES_PREFIX, XAdES_NS);
			elementSigningCertificate.appendChild(elementCert);
			
			Element elementCertDigest = createElement(doc, "CertDigest", XAdES_PREFIX, XAdES_NS);
			elementCert.appendChild(elementCertDigest);
			
			Element elementDigestMethod = createElement(doc, "DigestMethod", XMLDSIG_PREFIX, XMLDSIG_NS);
			elementCertDigest.appendChild(elementDigestMethod);
			elementDigestMethod.setAttributeNS(null, "Algorithm", DigestMethod.SHA1);
			
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			byte [] data = certificate.getEncoded();
			String strDigestValue = Base64.encodeBytes(md.digest(data));
			
			Element elementDigestValue = createElement(doc, "DigestValue", XMLDSIG_PREFIX, XMLDSIG_NS);
			elementDigestValue.setTextContent(strDigestValue);
			elementCertDigest.appendChild(elementDigestValue);		
			
			Element elementIssuerSerial = createElement(doc, "IssuerSerial", XAdES_PREFIX, XAdES_NS);
			elementCert.appendChild(elementIssuerSerial);
			
			Element X509IssuerName = createElement(doc, "X509IssuerName", XMLDSIG_PREFIX, XMLDSIG_NS);
			X509IssuerName.setTextContent(certificate.getIssuerX500Principal().getName());
			elementIssuerSerial.appendChild(X509IssuerName);
			
			Element X509SerialNumber = createElement(doc, "X509SerialNumber", XMLDSIG_PREFIX, XMLDSIG_NS);
			X509SerialNumber.setTextContent(certificate.getSerialNumber().toString());
			elementIssuerSerial.appendChild(X509SerialNumber);
					
			//SignedDataProperties
			Element elementSignedDataObjectProperties = createElement(doc, "SignedDataObjectProperties", XAdES_PREFIX, XAdES_NS);
			elementSignedProperties.appendChild(elementSignedDataObjectProperties);
			
			// DataObjectFormat		
			Element elementDataObjectFormat = createElement(doc, "DataObjectFormat", XAdES_PREFIX, XAdES_NS);	
			Attr objectReference = doc.createAttributeNS(null, "ObjectReference");
			objectReference.setValue("#Reference-Document");
			elementDataObjectFormat.setAttributeNodeNS(objectReference);
			
			elementSignedDataObjectProperties.appendChild(elementDataObjectFormat);
			
			Element elementDescription = createElement(doc, "Description", XAdES_PREFIX, XAdES_NS);
			elementDescription.setTextContent(description);
			elementDataObjectFormat.appendChild(elementDescription);
			
			Element mimeTypeElement = createElement(doc, "MimeType", XAdES_PREFIX, XAdES_NS);
			mimeTypeElement.setTextContent(mimeType);
			elementDataObjectFormat.appendChild(mimeTypeElement);
			
			Element elementEncoding = createElement(doc, "Encoding", XAdES_PREFIX, XAdES_NS);
			elementEncoding.setTextContent(ENCODING);
			elementDataObjectFormat.appendChild(elementEncoding);
			
			// Create UnsignedProperties
			Element elementUnsignedProperties = createElement(doc, "UnsignedProperties", XAdES_PREFIX, XAdES_NS);
			elementQualifyingProperties.appendChild(elementUnsignedProperties);

			Element elementUnsignedSignatureProperties = createElement(doc, "UnsignedSignatureProperties", XAdES_PREFIX, XAdES_NS);
			elementUnsignedProperties.appendChild(elementUnsignedSignatureProperties);
			
			DOMStructure qualifPropStruct = new DOMStructure(elementQualifyingProperties);

			//List<XMLStructure> xmlObj = new ArrayList<XMLStructure>();
			//xmlObj.add(qualifPropStruct);
			
			XMLObject objectQualifyingProperties = factory.newXMLObject(Collections.singletonList(qualifPropStruct), "QualifyingPropertiesId", null, null);

			List<XMLStructure> objectsList = new ArrayList<XMLStructure>();
			objectsList.add(objectQualifyingProperties);		
			
			
			// Create a Reference to the enveloped document (in this case we are signing the whole document, so a URI of "" signifies that) and
			// also specify the SHA1 digest algorithm and the ENVELOPED Transform.
			//Reference refDocument = factory.newReference("", factory.newDigestMethod(DigestMethod.SHA1, null), Collections.singletonList(factory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)), null, "Reference-Document");
			
			// Reference to documentName because we create a new XML document with document content
			Reference refDocument = factory.newReference("#"+documentName, factory.newDigestMethod(DigestMethod.SHA1, null), Collections.singletonList(factory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)), null, "Reference-Document");
			Reference refKeyInfo = factory.newReference("#KeyInfo", factory.newDigestMethod(DigestMethod.SHA1, null));
			Reference refSignedProperties = factory.newReference("#SignedPropertiesId", factory.newDigestMethod(DigestMethod.SHA1, null), null, "http://uri.etsi.org/01903#SignedProperties", "Reference-SignedProperties");
			
			List <XMLStructure> referencesList = new ArrayList<XMLStructure>();
			referencesList.add(refDocument);
			referencesList.add(refKeyInfo);
			referencesList.add(refSignedProperties);
			
			// Create the SignedInfo
			SignedInfo signedInfo = factory
					.newSignedInfo(factory.newCanonicalizationMethod(
							CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS,
							(C14NMethodParameterSpec) null), factory
							.newSignatureMethod(signatureMethod, null),
							referencesList);

			
			// Create a KeyValue containing the PublicKey that was generated
			KeyInfoFactory keyInfoFactory = factory.getKeyInfoFactory();
			KeyValue keyValue = keyInfoFactory.newKeyValue(certificate.getPublicKey());
							
			X509Data x509Data = keyInfoFactory.newX509Data(Collections.singletonList(certificate));
					
			List <XMLStructure> keyInfoList = new ArrayList<XMLStructure>();
			keyInfoList.add(keyValue);
			keyInfoList.add(x509Data);
					
			// Create a KeyInfo and add the KeyValue to it
			KeyInfo keyInfo = keyInfoFactory.newKeyInfo(keyInfoList, "KeyInfo");
			
			// Create a DOMSignContext and specify the PrivateKey and location of the resulting XMLSignature's parent element
			DOMSignContext dsc = new DOMSignContext(privateKey, doc.getDocumentElement());

			// Set the default name space for xmldsig elements
			dsc.setDefaultNamespacePrefix(XMLDSIG_PREFIX);
					
			// Create the XMLSignature (but don't sign it yet)
			XMLSignature signature = factory.newXMLSignature(signedInfo, keyInfo, objectsList, "SignatureId", "SignatureValueId");
			
			// Marshal, generate (and sign) the enveloped signature
			signature.sign(dsc);
			
		} catch (Exception e) {
			Logger.getLogger(XAdES.class.getName()).log(Level.SEVERE, "Error generating Enveloped XAdES BES Signature.", e);
			doc = null;
		}
		
		return doc;
	}
	
	/**
	 * Generate Enveloping XAdES BES signature.
	 * @param keystore The keystore with keys to sign.
	 * @param keystoreType The keystore's type. (Only support JKS and PKCS12 types)
	 * @param keystorePassword The keystore's password.
	 * @param privateKeyPassword The private key password.
	 * @param document The document to be signed. The document will be Base64 encoded and XML wappred enveloped.
	 * @param documentName The document's name.
	 * @param description The document' description.
	 * @param mimeType The document's mime type.
	 * @return Document with Enveloping XAdES BES signature or null in case of error.
	 */
	public static Document generateEnvelopingBES (InputStream keystore, String keystoreType, String keystorePassword, String privateKeyPassword, InputStream document, String documentName, String description, String mimeType) {
		
		Document doc = null;
		
		String KEYSTORE_TYPE = "";
				
		try {
			
			if (keystoreType.equalsIgnoreCase("PKCS12")) {
				KEYSTORE_TYPE = "PKCS12";
			} else if (keystoreType.equalsIgnoreCase("JKS")) {
				KEYSTORE_TYPE = "JKS";
			} else {
				throw new Exception("Bad keystore type: " + keystoreType);
			}
			
			KeyStore ks = KeyStore.getInstance(KEYSTORE_TYPE);
			ks.load(keystore, keystorePassword.toCharArray());

			// Get private key for encrypt
			Enumeration<String> enumAlias = ks.aliases();
			String alias = enumAlias.nextElement();
			PrivateKey privateKey = (PrivateKey) ks.getKey(alias, privateKeyPassword.toCharArray());
			X509Certificate certificate = (X509Certificate) ks.getCertificate(alias);
			
			// Setting Signature Method
			String signatureMethod = null;
			
			if (privateKey.getAlgorithm().equals("DSA")) {
				signatureMethod = SignatureMethod.DSA_SHA1;
			} else if (privateKey.getAlgorithm().equals("RSA")) {
				signatureMethod = SignatureMethod.RSA_SHA1;
			}
			
			// Create the DOM XMLSignatureFactory that will be used to generate the XMLSignature
			XMLSignatureFactory factory = XMLSignatureFactory.getInstance("DOM", new XMLDSigRI());
			
			doc = createDocument(document, documentName);
			
			if (doc == null) {
				throw new Exception("Error creating XML document.");
			}

			XMLStructure documentContent = new DOMStructure(doc.getDocumentElement());

			XMLObject objectDocument = factory.newXMLObject(Collections.singletonList(documentContent), "Object-Document", null, null);				
			
			// QualifyingProperties
			Element elementQualifyingProperties = createElement(doc, "QualifyingProperties", XAdES_PREFIX, XAdES_NS);
			elementQualifyingProperties.setAttributeNS(null, "Target", "#SignatureId");

			Element elementSignedProperties = createElement(doc, "SignedProperties", XAdES_PREFIX, XAdES_NS);
			Attr attrSignedPropertiesId = doc.createAttributeNS(null, "Id");
			attrSignedPropertiesId.setValue("SignedPropertiesId");
			
			elementSignedProperties.setAttributeNodeNS(attrSignedPropertiesId);
			elementSignedProperties.setIdAttributeNS(null, "Id", true);
			
			elementQualifyingProperties.appendChild(elementSignedProperties);
			
			Element elementSignedSignatureProperties = createElement(doc, "SignedSignatureProperties", XAdES_PREFIX, XAdES_NS);
			elementSignedProperties.appendChild(elementSignedSignatureProperties);
			
			// Create SigningTime
			Element elementSigningTime = createElement(doc, "SigningTime", XAdES_PREFIX, XAdES_NS);
			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
			String strDate = simpleDateFormat.format(new Date());
			
			// Pasamos de +0254 a +02:54 de +hhmm a +hh:mm
			strDate = strDate.substring(0, strDate.length() - 2).concat(":").concat(strDate.substring(strDate.length()-2));
			
			elementSigningTime.setTextContent(strDate);
			elementSignedSignatureProperties.appendChild(elementSigningTime);
			
			
			// Create SigningCertificate
			Element elementSigningCertificate = createElement(doc, "SigningCertificate", XAdES_PREFIX, XAdES_NS);
			elementSignedSignatureProperties.appendChild(elementSigningCertificate);
			
			Element elementCert = createElement(doc, "Cert", XAdES_PREFIX, XAdES_NS);
			elementSigningCertificate.appendChild(elementCert);
			
			Element elementCertDigest = createElement(doc, "CertDigest", XAdES_PREFIX, XAdES_NS);
			elementCert.appendChild(elementCertDigest);
			
			Element elementDigestMethod = createElement(doc, "DigestMethod", XMLDSIG_PREFIX, XMLDSIG_NS);
			elementCertDigest.appendChild(elementDigestMethod);
			elementDigestMethod.setAttributeNS(null, "Algorithm", DigestMethod.SHA1);
			
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			byte [] data = certificate.getEncoded();
	        String strDigestValue = Base64.encodeBytes(md.digest(data));
			
	        Element elementDigestValue = createElement(doc, "DigestValue", XMLDSIG_PREFIX, XMLDSIG_NS);
			elementDigestValue.setTextContent(strDigestValue);
	        elementCertDigest.appendChild(elementDigestValue);		
			
			Element elementIssuerSerial = createElement(doc, "IssuerSerial", XAdES_PREFIX, XAdES_NS);
			elementCert.appendChild(elementIssuerSerial);
			
			Element X509IssuerName = createElement(doc, "X509IssuerName", XMLDSIG_PREFIX, XMLDSIG_NS);
			X509IssuerName.setTextContent(certificate.getIssuerX500Principal().getName());
			elementIssuerSerial.appendChild(X509IssuerName);
			
			Element X509SerialNumber = createElement(doc, "X509SerialNumber", XMLDSIG_PREFIX, XMLDSIG_NS);
			X509SerialNumber.setTextContent(certificate.getSerialNumber().toString());
			elementIssuerSerial.appendChild(X509SerialNumber);
					
			//SignedDataProperties
			Element elementSignedDataObjectProperties = createElement(doc, "SignedDataObjectProperties", XAdES_PREFIX, XAdES_NS);
			elementSignedProperties.appendChild(elementSignedDataObjectProperties);
			
			// DataObjectFormat		
			Element elementDataObjectFormat = createElement(doc, "DataObjectFormat", XAdES_PREFIX, XAdES_NS);	
			Attr objectReference = doc.createAttributeNS(null, "ObjectReference");
			objectReference.setValue("#Reference-Object-Document");
			elementDataObjectFormat.setAttributeNodeNS(objectReference);
			
			elementSignedDataObjectProperties.appendChild(elementDataObjectFormat);
			
			Element elementDescription = createElement(doc, "Description", XAdES_PREFIX, XAdES_NS);
			elementDescription.setTextContent(description);
			elementDataObjectFormat.appendChild(elementDescription);
			
			Element mimeTypeElement = createElement(doc, "MimeType", XAdES_PREFIX, XAdES_NS);
			mimeTypeElement.setTextContent(mimeType);
			elementDataObjectFormat.appendChild(mimeTypeElement);
			
			Element elementEncoding = createElement(doc, "Encoding", XAdES_PREFIX, XAdES_NS);
			elementEncoding.setTextContent(ENCODING);
			elementDataObjectFormat.appendChild(elementEncoding);
			
			Element elementUnsignedProperties = createElement(doc, "UnsignedProperties", XAdES_PREFIX, XAdES_NS);
			elementQualifyingProperties.appendChild(elementUnsignedProperties);

			Element elementUnsignedSignatureProperties = createElement(doc, "UnsignedSignatureProperties", XAdES_PREFIX, XAdES_NS);
			elementUnsignedProperties.appendChild(elementUnsignedSignatureProperties);
			
			DOMStructure qualifPropStruct = new DOMStructure(elementQualifyingProperties);

			//List<XMLStructure> xmlObj = new ArrayList<XMLStructure>();
			//xmlObj.add(qualifPropStruct);
			
			XMLObject objectQualifyingProperties = factory.newXMLObject(Collections.singletonList(qualifPropStruct), "QualifyingPropertiesId", null, null);

			List<XMLStructure> objectsList = new ArrayList<XMLStructure>();
			objectsList.add(objectDocument);
			objectsList.add(objectQualifyingProperties);		
			
			
			// Create a Reference to a same-document URI that is an Object element and specify the SHA1 digest algorithm
			Reference refDocument = factory.newReference("#Object-Document", factory.newDigestMethod(DigestMethod.SHA1, null), null, null, "Reference-Object-Document");
			Reference refKeyInfo = factory.newReference("#KeyInfo", factory.newDigestMethod(DigestMethod.SHA1, null));
			Reference refSignedProperties = factory.newReference("#SignedPropertiesId", factory.newDigestMethod(DigestMethod.SHA1, null), null, "http://uri.etsi.org/01903#SignedProperties", "Reference-SignedProperties");
			
			List <XMLStructure> referencesList = new ArrayList<XMLStructure>();
			referencesList.add(refDocument);
			referencesList.add(refKeyInfo);
			referencesList.add(refSignedProperties);
			
			SignedInfo signedInfo = factory
					.newSignedInfo(factory.newCanonicalizationMethod(
							CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS,
							(C14NMethodParameterSpec) null), factory
							.newSignatureMethod(signatureMethod, null),
							referencesList);
			
			
			// Create a KeyValue containing the PublicKey that was generated
			KeyInfoFactory keyInfoFactory = factory.getKeyInfoFactory();
			KeyValue keyValue = keyInfoFactory.newKeyValue(certificate.getPublicKey());
					
			X509Data x509Data = keyInfoFactory.newX509Data(Collections.singletonList(certificate));
			
			List <XMLStructure> keyInfoList = new ArrayList<XMLStructure>();
			keyInfoList.add(keyValue);
			keyInfoList.add(x509Data);
			
			// Create a KeyInfo and add the KeyValue to it
			KeyInfo keyInfo = keyInfoFactory.newKeyInfo(keyInfoList, "KeyInfo");

			// Create a DOMSignContext and specify the PrivateKey for signing
			// and the document location of the XMLSignature
			DOMSignContext dsc = new DOMSignContext(privateKey, doc);
			
			// Set the default name space for xmldsig elements
			dsc.setDefaultNamespacePrefix(XMLDSIG_PREFIX);
					
			// Create the XMLSignature (but don't sign it yet)
			XMLSignature signature = factory.newXMLSignature(signedInfo, keyInfo, objectsList, "SignatureId", "SignatureValueId");

			// Lastly, generate the enveloping signature using the PrivateKey
			signature.sign(dsc);		
						
		} catch (Exception e) {
			Logger.getLogger(XAdES.class.getName()).log(Level.SEVERE, "Error generating Envelopen XAdES BES Signature.", e);
			doc = null;
		}
		
		return doc;
	}
	
	/**
	 * Generate Detached XAdES BES signature.
	 * @param keystore The keystore with keys to sign.
	 * @param keystoreType The keystore's type. (Only support JKS and PKCS12 types)
	 * @param keystorePassword The keystore's password.
	 * @param privateKeyPassword The private key password.
	 * @param document The document to be signed.
	 * @param documentName The document's name.
	 * @param description The document' description.
	 * @param mimeType The document's mime type.
	 * @return Document with Detached XAdES BES signature or null in case of error.
	 */
	public static Document generateDetachedBES (InputStream keystore, String keystoreType, String keystorePassword, String privateKeyPassword, InputStream document, String documentName, String description, String mimeType) {
		
		
		Document doc = null;
		
		String KEYSTORE_TYPE = "";
				
		try {
			
			if (keystoreType.equalsIgnoreCase("PKCS12")) {
				KEYSTORE_TYPE = "PKCS12";
			} else if (keystoreType.equalsIgnoreCase("JKS")) {
				KEYSTORE_TYPE = "JKS";
			} else {
				throw new Exception("Bad keystore type: " + keystoreType);
			}
			
			KeyStore ks = KeyStore.getInstance(KEYSTORE_TYPE);
			ks.load(keystore, keystorePassword.toCharArray());

			// Get private key for encrypt
			Enumeration<String> enumAlias = ks.aliases();
			String alias = enumAlias.nextElement();
			PrivateKey privateKey = (PrivateKey) ks.getKey(alias, privateKeyPassword.toCharArray());
			X509Certificate certificate = (X509Certificate) ks.getCertificate(alias);
			
			// Setting Signature Method
			String signatureMethod = null;
			
			if (privateKey.getAlgorithm().equals("DSA")) {
				signatureMethod = SignatureMethod.DSA_SHA1;
			} else if (privateKey.getAlgorithm().equals("RSA")) {
				signatureMethod = SignatureMethod.RSA_SHA1;
			}
			
			// Create a DOM XMLSignatureFactory that will be used to generate the enveloped signature
			XMLSignatureFactory factory = XMLSignatureFactory.getInstance("DOM", new XMLDSigRI());
			
			byte [] documentByteArray = Stream.toByteArray(document);
			
			String url = documentName.replace("\\", "/");
			
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			
			// Create the Document that will hold the resulting XMLSignature
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true); // must be set
			doc = dbf.newDocumentBuilder().newDocument();
			
			// QualifyingProperties
			Element elementQualifyingProperties = createElement(doc, "QualifyingProperties", XAdES_PREFIX, XAdES_NS);
			elementQualifyingProperties.setAttributeNS(null, "Target", "#SignatureId");

			Element elementSignedProperties = createElement(doc, "SignedProperties", XAdES_PREFIX, XAdES_NS);
			Attr attrSignedPropertiesId = doc.createAttributeNS(null, "Id");
			attrSignedPropertiesId.setValue("SignedPropertiesId");
			
			elementSignedProperties.setAttributeNodeNS(attrSignedPropertiesId);
			elementSignedProperties.setIdAttributeNS(null, "Id", true);
			
			elementQualifyingProperties.appendChild(elementSignedProperties);
			
			Element elementSignedSignatureProperties = createElement(doc, "SignedSignatureProperties", XAdES_PREFIX, XAdES_NS);
			elementSignedProperties.appendChild(elementSignedSignatureProperties);
			
			// Create SigningTime
			Element elementSigningTime = createElement(doc, "SigningTime", XAdES_PREFIX, XAdES_NS);
			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
			String strDate = simpleDateFormat.format(new Date());
			
			// Pasamos de +0254 a +02:54 de +hhmm a +hh:mm
			strDate = strDate.substring(0, strDate.length() - 2).concat(":").concat(strDate.substring(strDate.length()-2));
			
			elementSigningTime.setTextContent(strDate);
			elementSignedSignatureProperties.appendChild(elementSigningTime);
			
			
			// Create SigningCertificate
			Element elementSigningCertificate = createElement(doc, "SigningCertificate", XAdES_PREFIX, XAdES_NS);
			elementSignedSignatureProperties.appendChild(elementSigningCertificate);
			
			Element elementCert = createElement(doc, "Cert", XAdES_PREFIX, XAdES_NS);
			elementSigningCertificate.appendChild(elementCert);
			
			Element elementCertDigest = createElement(doc, "CertDigest", XAdES_PREFIX, XAdES_NS);
			elementCert.appendChild(elementCertDigest);
			
			Element elementDigestMethod = createElement(doc, "DigestMethod", XMLDSIG_PREFIX, XMLDSIG_NS);
			elementCertDigest.appendChild(elementDigestMethod);
			elementDigestMethod.setAttributeNS(null, "Algorithm", DigestMethod.SHA1);
			
			
			byte [] data = certificate.getEncoded();
	        String strDigestValue = Base64.encodeBytes(md.digest(data));
			
	        Element elementDigestValue = createElement(doc, "DigestValue", XMLDSIG_PREFIX, XMLDSIG_NS);
			elementDigestValue.setTextContent(strDigestValue);
	        elementCertDigest.appendChild(elementDigestValue);		
			
			Element elementIssuerSerial = createElement(doc, "IssuerSerial", XAdES_PREFIX, XAdES_NS);
			elementCert.appendChild(elementIssuerSerial);
			
			Element X509IssuerName = createElement(doc, "X509IssuerName", XMLDSIG_PREFIX, XMLDSIG_NS);
			X509IssuerName.setTextContent(certificate.getIssuerX500Principal().getName());
			elementIssuerSerial.appendChild(X509IssuerName);
			
			Element X509SerialNumber = createElement(doc, "X509SerialNumber", XMLDSIG_PREFIX, XMLDSIG_NS);
			X509SerialNumber.setTextContent(certificate.getSerialNumber().toString());
			elementIssuerSerial.appendChild(X509SerialNumber);
					
			//SignedDataProperties
			Element elementSignedDataObjectProperties = createElement(doc, "SignedDataObjectProperties", XAdES_PREFIX, XAdES_NS);
			elementSignedProperties.appendChild(elementSignedDataObjectProperties);
			
			// Crear y rellenar DataObjectFormat		
			Element elementDataObjectFormat = createElement(doc, "DataObjectFormat", XAdES_PREFIX, XAdES_NS);	
			Attr objectReference = doc.createAttributeNS(null, "ObjectReference");
			objectReference.setValue("#Reference-Document");
			elementDataObjectFormat.setAttributeNodeNS(objectReference);
			
			elementSignedDataObjectProperties.appendChild(elementDataObjectFormat);
			
			Element elementDescription = createElement(doc, "Description", XAdES_PREFIX, XAdES_NS);
			elementDescription.setTextContent(description);
			elementDataObjectFormat.appendChild(elementDescription);
			
			Element mimeTypeElement = createElement(doc, "MimeType", XAdES_PREFIX, XAdES_NS);
			mimeTypeElement.setTextContent(mimeType);
			elementDataObjectFormat.appendChild(mimeTypeElement);
			
			Element elementEncoding = createElement(doc, "Encoding", XAdES_PREFIX, XAdES_NS);
			elementEncoding.setTextContent(ENCODING);
			elementDataObjectFormat.appendChild(elementEncoding);
			
			Element elementUnsignedProperties = createElement(doc, "UnsignedProperties", XAdES_PREFIX, XAdES_NS);
			elementQualifyingProperties.appendChild(elementUnsignedProperties);

			Element elementUnsignedSignatureProperties = createElement(doc, "UnsignedSignatureProperties", XAdES_PREFIX, XAdES_NS);
			elementUnsignedProperties.appendChild(elementUnsignedSignatureProperties);
			
			DOMStructure qualifPropStruct = new DOMStructure(elementQualifyingProperties);

			//List<XMLStructure> xmlObj = new ArrayList<XMLStructure>();
			//xmlObj.add(qualifPropStruct);
			
			XMLObject objectQualifyingProperties = factory.newXMLObject(Collections.singletonList(qualifPropStruct), "QualifyingPropertiesId", null, null);

			List<XMLStructure> objectsList = new ArrayList<XMLStructure>();
			objectsList.add(objectQualifyingProperties);		
			

			// Create a Reference to an external URI that will be digested using the SHA1 digest algorithm
			Reference refDocument = factory.newReference("file://"+url , factory.newDigestMethod(DigestMethod.SHA1, null), null, null, "Reference-Document", md.digest(documentByteArray));
			Reference refKeyInfo = factory.newReference("#KeyInfo", factory.newDigestMethod(DigestMethod.SHA1, null));
			Reference refSignedProperties = factory.newReference("#SignedPropertiesId", factory.newDigestMethod(DigestMethod.SHA1, null), null, "http://uri.etsi.org/01903#SignedProperties", "Reference-SignedProperties");
			
			List <XMLStructure> referencesList = new ArrayList<XMLStructure>();
			referencesList.add(refDocument);
			referencesList.add(refKeyInfo);
			referencesList.add(refSignedProperties);

			// Create the SignedInfo
			SignedInfo signedInfo = factory
					.newSignedInfo(factory.newCanonicalizationMethod(
							CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS,
							(C14NMethodParameterSpec) null), factory
							.newSignatureMethod(signatureMethod, null),
							referencesList);
			
			
			// Create a KeyValue containing the PublicKey that was generated
			KeyInfoFactory keyInfoFactory = factory.getKeyInfoFactory();
			KeyValue keyValue = keyInfoFactory.newKeyValue(certificate.getPublicKey());
							
			X509Data x509Data = keyInfoFactory.newX509Data(Collections.singletonList(certificate));
					
			List <XMLStructure> keyInfoList = new ArrayList<XMLStructure>();
			keyInfoList.add(keyValue);
			keyInfoList.add(x509Data);
					
			// Create a KeyInfo and add the KeyValue to it
			KeyInfo keyInfo = keyInfoFactory.newKeyInfo(keyInfoList, "KeyInfo");
			
			// Create a DOMSignContext and specify the PrivateKey and location of the resulting XMLSignature's parent element
			DOMSignContext dsc = new DOMSignContext(privateKey, doc);

			// Set the default name space for xmldsig elements
			dsc.setDefaultNamespacePrefix(XMLDSIG_PREFIX);
					
			// Create the XMLSignature (but don't sign it yet)
			XMLSignature signature = factory.newXMLSignature(signedInfo, keyInfo, objectsList, "SignatureId", "SignatureValueId");

			// Marshal, generate (and sign) the detached XMLSignature. The DOM
			// Document will contain the XML Signature if this method returns
			// successfully.
			signature.sign(dsc);
			
		} catch (Exception e) {
			Logger.getLogger(XAdES.class.getName()).log(Level.SEVERE, "Error generating Detached XAdES BES signature.", e);
			doc = null;
		}
		
		return doc;
	}

	/**
	 * Generate Enveloped XAdES-T BES signature.
	 * @param keystore The keystore with keys to sign.
	 * @param keystoreType The keystore's type. (Only support JKS and PKCS12 types)
	 * @param keystorePassword The keystore's password.
	 * @param privateKeyPassword The private key password.
	 * @param document The document to be signed. The document will be Base64 encoded and XML wappred enveloped. 
	 * @param documentName The document's name.
	 * @param description The document' description.
	 * @param mimeType The document's mime type.
	 * @param tsaUrl The TSA's URL.
	 * @param tsaUserName The username for TSA, can be null if authentication isn't necessary.
	 * @param tsaPassword The password for TSA, can be null if authentication isn't necessary.
	 * @return Document with Enveloped XAdES-T BES signature or null in case of error.
	 */
	public static Document generateEnvelopedBES_T (InputStream keystore, String keystoreType, String keystorePassword, String privateKeyPassword, InputStream document, String documentName, String description, String mimeType, String tsaUrl, String tsaUserName, String tsaPassword) {
		
		Document doc = null;
		
		String KEYSTORE_TYPE = "";
				
		try {
			
			if (keystoreType.equalsIgnoreCase("PKCS12")) {
				KEYSTORE_TYPE = "PKCS12";
			} else if (keystoreType.equalsIgnoreCase("JKS")) {
				KEYSTORE_TYPE = "JKS";
			} else {
				throw new Exception("Bad keystore type: " + keystoreType);
			}
			
			KeyStore ks = KeyStore.getInstance(KEYSTORE_TYPE);
			ks.load(keystore, keystorePassword.toCharArray());

			// Get private key for encrypt
			Enumeration<String> enumAlias = ks.aliases();
			String alias = enumAlias.nextElement();
			PrivateKey privateKey = (PrivateKey) ks.getKey(alias, privateKeyPassword.toCharArray());
			X509Certificate certificate = (X509Certificate) ks.getCertificate(alias);
			
			// Setting Signature Method
			String signatureMethod = null;
			
			if (privateKey.getAlgorithm().equals("DSA")) {
				signatureMethod = SignatureMethod.DSA_SHA1;
			} else if (privateKey.getAlgorithm().equals("RSA")) {
				signatureMethod = SignatureMethod.RSA_SHA1;
			}
			
			// Create a DOM XMLSignatureFactory that will be used to generate the enveloped signature
			XMLSignatureFactory factory = XMLSignatureFactory.getInstance("DOM", new XMLDSigRI());
			
			// Instantiate the document to be signed
			doc = createDocument(document, documentName);
			
			if (doc == null) {
				throw new Exception("Error creating XML document.");
			}

			// QualifyingProperties
			Element elementQualifyingProperties = createElement(doc, "QualifyingProperties", XAdES_PREFIX, XAdES_NS);
			elementQualifyingProperties.setAttributeNS(null, "Target", "#SignatureId");

			Element elementSignedProperties = createElement(doc, "SignedProperties", XAdES_PREFIX, XAdES_NS);
			Attr attrSignedPropertiesId = doc.createAttributeNS(null, "Id");
			attrSignedPropertiesId.setValue("SignedPropertiesId");
			
			elementSignedProperties.setAttributeNodeNS(attrSignedPropertiesId);
			elementSignedProperties.setIdAttributeNS(null, "Id", true);
			
			elementQualifyingProperties.appendChild(elementSignedProperties);
			
			Element elementSignedSignatureProperties = createElement(doc, "SignedSignatureProperties", XAdES_PREFIX, XAdES_NS);
			elementSignedProperties.appendChild(elementSignedSignatureProperties);
			
			// Create SigningTime
			Element elementSigningTime = createElement(doc, "SigningTime", XAdES_PREFIX, XAdES_NS);
			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
			String strDate = simpleDateFormat.format(new Date());
			
			// Pasamos de +0254 a +02:54 de +hhmm a +hh:mm
			strDate = strDate.substring(0, strDate.length() - 2).concat(":").concat(strDate.substring(strDate.length()-2));
			
			elementSigningTime.setTextContent(strDate);
			elementSignedSignatureProperties.appendChild(elementSigningTime);
			
			// Create SigningCertificate
			Element elementSigningCertificate = createElement(doc, "SigningCertificate", XAdES_PREFIX, XAdES_NS);
			elementSignedSignatureProperties.appendChild(elementSigningCertificate);
			
			Element elementCert = createElement(doc, "Cert", XAdES_PREFIX, XAdES_NS);
			elementSigningCertificate.appendChild(elementCert);
			
			Element elementCertDigest = createElement(doc, "CertDigest", XAdES_PREFIX, XAdES_NS);
			elementCert.appendChild(elementCertDigest);
			
			Element elementDigestMethod = createElement(doc, "DigestMethod", XMLDSIG_PREFIX, XMLDSIG_NS);
			elementCertDigest.appendChild(elementDigestMethod);
			elementDigestMethod.setAttributeNS(null, "Algorithm", DigestMethod.SHA1);
			
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			byte [] data = certificate.getEncoded();
	        String strDigestValue = Base64.encodeBytes(md.digest(data));
			
	        Element elementDigestValue = createElement(doc, "DigestValue", XMLDSIG_PREFIX, XMLDSIG_NS);
			elementDigestValue.setTextContent(strDigestValue);
	        elementCertDigest.appendChild(elementDigestValue);		
			
			Element elementIssuerSerial = createElement(doc, "IssuerSerial", XAdES_PREFIX, XAdES_NS);
			elementCert.appendChild(elementIssuerSerial);
			
			Element X509IssuerName = createElement(doc, "X509IssuerName", XMLDSIG_PREFIX, XMLDSIG_NS);
			X509IssuerName.setTextContent(certificate.getIssuerX500Principal().getName());
			elementIssuerSerial.appendChild(X509IssuerName);
			
			Element X509SerialNumber = createElement(doc, "X509SerialNumber", XMLDSIG_PREFIX, XMLDSIG_NS);
			X509SerialNumber.setTextContent(certificate.getSerialNumber().toString());
			elementIssuerSerial.appendChild(X509SerialNumber);
					
			//SignedDataProperties
			Element elementSignedDataObjectProperties = createElement(doc, "SignedDataObjectProperties", XAdES_PREFIX, XAdES_NS);
			elementSignedProperties.appendChild(elementSignedDataObjectProperties);
			
			// DataObjectFormat		
			Element elementDataObjectFormat = createElement(doc, "DataObjectFormat", XAdES_PREFIX, XAdES_NS);	
			Attr objectReference = doc.createAttributeNS(null, "ObjectReference");
			objectReference.setValue("#Reference-Document");
			elementDataObjectFormat.setAttributeNodeNS(objectReference);
			
			elementSignedDataObjectProperties.appendChild(elementDataObjectFormat);
			
			Element elementDescription = createElement(doc, "Description", XAdES_PREFIX, XAdES_NS);
			elementDescription.setTextContent(description);
			elementDataObjectFormat.appendChild(elementDescription);
			
			Element mimeTypeElement = createElement(doc, "MimeType", XAdES_PREFIX, XAdES_NS);
			mimeTypeElement.setTextContent(mimeType);
			elementDataObjectFormat.appendChild(mimeTypeElement);
			
			Element elementEncoding = createElement(doc, "Encoding", XAdES_PREFIX, XAdES_NS);
			elementEncoding.setTextContent(ENCODING);
			elementDataObjectFormat.appendChild(elementEncoding);
			
			// Create UnsignedProperties
			Element elementUnsignedProperties = createElement(doc, "UnsignedProperties", XAdES_PREFIX, XAdES_NS);
			elementQualifyingProperties.appendChild(elementUnsignedProperties);
			
			Attr attrUnsignedPropertiesId = doc.createAttributeNS(null, "Id");
			attrUnsignedPropertiesId.setValue("UnsignedPropertiesId");
			
			elementUnsignedProperties.setAttributeNodeNS(attrUnsignedPropertiesId);
			elementUnsignedProperties.setIdAttributeNS(null, "Id", true);

			Element elementUnsignedSignatureProperties = createElement(doc, "UnsignedSignatureProperties", XAdES_PREFIX, XAdES_NS);
			elementUnsignedProperties.appendChild(elementUnsignedSignatureProperties);
			
			DOMStructure qualifPropStruct = new DOMStructure(elementQualifyingProperties);

			//List<XMLStructure> xmlObj = new ArrayList<XMLStructure>();
			//xmlObj.add(qualifPropStruct);
			
			XMLObject objectQualifyingProperties = factory.newXMLObject(Collections.singletonList(qualifPropStruct), "QualifyingPropertiesId", null, null);

			List<XMLStructure> objectsList = new ArrayList<XMLStructure>();
			objectsList.add(objectQualifyingProperties);		
			
			

			// Create a Reference to the enveloped document (in this case we are signing the whole document, so a URI of "" signifies that) and
			// also specify the SHA1 digest algorithm and the ENVELOPED Transform.
			//Reference refDocument = factory.newReference("", factory.newDigestMethod(DigestMethod.SHA1, null), Collections.singletonList(factory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)), null, "Reference-Document");
			
			// Reference to documentName because we create a new XML document with document content
			Reference refDocument = factory.newReference("#"+documentName, factory.newDigestMethod(DigestMethod.SHA1, null), Collections.singletonList(factory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)), null, "Reference-Document");			
			Reference refKeyInfo = factory.newReference("#KeyInfo", factory.newDigestMethod(DigestMethod.SHA1, null));
			Reference refSignedProperties = factory.newReference("#SignedPropertiesId", factory.newDigestMethod(DigestMethod.SHA1, null), null, "http://uri.etsi.org/01903#SignedProperties", "Reference-SignedProperties");
			
			List <XMLStructure> referencesList = new ArrayList<XMLStructure>();
			referencesList.add(refDocument);
			referencesList.add(refKeyInfo);
			referencesList.add(refSignedProperties);
			
			// Create the SignedInfo
			SignedInfo signedInfo = factory
					.newSignedInfo(factory.newCanonicalizationMethod(
							CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS,
							(C14NMethodParameterSpec) null), factory
							.newSignatureMethod(signatureMethod, null),
							referencesList);

			
			// Create a KeyValue containing the PublicKey that was generated
			KeyInfoFactory keyInfoFactory = factory.getKeyInfoFactory();
			KeyValue keyValue = keyInfoFactory.newKeyValue(certificate.getPublicKey());
							
			X509Data x509Data = keyInfoFactory.newX509Data(Collections.singletonList(certificate));
					
			List <XMLStructure> keyInfoList = new ArrayList<XMLStructure>();
			keyInfoList.add(keyValue);
			keyInfoList.add(x509Data);
					
			// Create a KeyInfo and add the KeyValue to it
			KeyInfo keyInfo = keyInfoFactory.newKeyInfo(keyInfoList, "KeyInfo");
			
			// Create a DOMSignContext and specify the PrivateKey and location of the resulting XMLSignature's parent element
			DOMSignContext dsc = new DOMSignContext(privateKey, doc.getDocumentElement());

			// Set the default name space for xmldsig elements
			dsc.setDefaultNamespacePrefix(XMLDSIG_PREFIX);
					
			// Create the XMLSignature (but don't sign it yet)
			XMLSignature signature = factory.newXMLSignature(signedInfo, keyInfo, objectsList, "SignatureId", "SignatureValueId");
			
			// Marshal, generate (and sign) the enveloped signature
			signature.sign(dsc);
			
			//
			// Generate timestamp element
			//
			NodeList signatureValueList = doc.getElementsByTagName("ds:SignatureValue");
			
			Canonicalizer canon = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS);
			byte canonXmlBytes[] = canon.canonicalizeSubtree(signatureValueList.item(0));
			
			byte [] timeStamp = null; 
			
			if (tsaUserName != null && tsaPassword != null) {
				// Authenticated TimeStamp
				timeStamp = TimeStamp.generateAuthenticatedTimeStamp(md.digest(canonXmlBytes), "SHA-1", tsaUrl, tsaUserName, tsaPassword);
			} else {
				timeStamp = TimeStamp.generateTimeStamp(md.digest(canonXmlBytes), "SHA-1", tsaUrl);
			}
			
			if (timeStamp == null) {
				throw new Exception("Error generating timestamp.");
			}
			
			String strTimeStamp = Base64.encodeBytes(timeStamp);
			
			NodeList unsignedSignaturePropertiesList = doc.getElementsByTagName("etsi:UnsignedSignatureProperties");
			Node nodeUnsignedSignatureProperties = unsignedSignaturePropertiesList.item(0);
			
			// SignatureTimeStamp
			Element elementSignatureTimeStamp = createElement(doc, "SignatureTimeStamp", XAdES_PREFIX, XAdES_NS);	
			
			Attr attrSignatureTimeStampId = doc.createAttributeNS(null, "Id");
			attrSignatureTimeStampId.setValue("SignatureTimeStampId");
			
			elementSignatureTimeStamp.setAttributeNodeNS(attrSignatureTimeStampId);
			
			nodeUnsignedSignatureProperties.appendChild(elementSignatureTimeStamp);
			
			// CanonicalizationMethod
			Element elementCanonicalizationMethod = createElement(doc, "CanonicalizationMethod", XMLDSIG_PREFIX, XMLDSIG_NS);	
			
			Attr attrAlgorithm = doc.createAttributeNS(null, "Algorithm");
			attrAlgorithm.setValue(Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS);
			
			elementCanonicalizationMethod.setAttributeNodeNS(attrAlgorithm);
			
			elementSignatureTimeStamp.appendChild(elementCanonicalizationMethod);
					
			// EncapsulatedTimeStamp etsi:EncapsulatedTimeStamp
			Element elementEncapsulatedTimeStamp = createElement(doc, "EncapsulatedTimeStamp", XAdES_PREFIX, XAdES_NS);	
			
			Attr attrEncapsulatedTimeStampId = doc.createAttributeNS(null, "Id");
			attrEncapsulatedTimeStampId.setValue("EncapsulatedTimeStampId");
			
			elementEncapsulatedTimeStamp.setAttributeNodeNS(attrEncapsulatedTimeStampId);
			elementEncapsulatedTimeStamp.setTextContent(strTimeStamp);
			
			elementSignatureTimeStamp.appendChild(elementEncapsulatedTimeStamp);
						
		} catch (Exception e) {
			Logger.getLogger(XAdES.class.getName()).log(Level.SEVERE, "Error generating Enveloped XAdES-T BES signature.", e);
			doc = null;
		}
		
		return doc;
	}
	
	/**
	 * Generate Enveloping XAdES-T BES signature.
	 * @param keystore The keystore with keys to sign.
	 * @param keystoreType The keystore's type. (Only support JKS and PKCS12 types)
	 * @param keystorePassword The keystore's password.
	 * @param privateKeyPassword The private key password.
	 * @param document The document to be signed. The document will be Base64 encoded and XML wappred enveloped. 
	 * @param documentName The document's name.
	 * @param description The document' description.
	 * @param mimeType The document's mime type.
	 * @param tsaUrl The TSA's URL.
	 * @param tsaUserName The username for TSA, can be null if authentication isn't necessary.
	 * @param tsaPassword The password for TSA, can be null if authentication isn't necessary.
	 * @return Document with Enveloping XAdES-T BES signature or null in case of error.
	 */
	public static Document generateEnvelopingBES_T (InputStream keystore, String keystoreType, String keystorePassword, String privateKeyPassword, InputStream document, String documentName, String description, String mimeType, String tsaUrl, String tsaUserName, String tsaPassword) {
		
		Document doc = null;
		
		String KEYSTORE_TYPE = "";
				
		try {
			
			if (keystoreType.equalsIgnoreCase("PKCS12")) {
				KEYSTORE_TYPE = "PKCS12";
			} else if (keystoreType.equalsIgnoreCase("JKS")) {
				KEYSTORE_TYPE = "JKS";
			} else {
				throw new Exception("Bad keystore type: " + keystoreType);
			}
			
			KeyStore ks = KeyStore.getInstance(KEYSTORE_TYPE);
			ks.load(keystore, keystorePassword.toCharArray());

			// Get private key for encrypt
			Enumeration<String> enumAlias = ks.aliases();
			String alias = enumAlias.nextElement();
			PrivateKey privateKey = (PrivateKey) ks.getKey(alias, privateKeyPassword.toCharArray());
			X509Certificate certificate = (X509Certificate) ks.getCertificate(alias);
			
			// Setting Signature Method
			String signatureMethod = null;
			
			if (privateKey.getAlgorithm().equals("DSA")) {
				signatureMethod = SignatureMethod.DSA_SHA1;
			} else if (privateKey.getAlgorithm().equals("RSA")) {
				signatureMethod = SignatureMethod.RSA_SHA1;
			}
			
			// Create a DOM XMLSignatureFactory that will be used to generate the enveloped signature
			XMLSignatureFactory factory = XMLSignatureFactory.getInstance("DOM", new XMLDSigRI());
			
			// Instantiate the document to be signed
			doc = createDocument(document, documentName);
			
			if (doc == null) {
				throw new Exception("Error creating XML document.");
			}
			
			XMLStructure documentContent = new DOMStructure(doc.getDocumentElement());

			XMLObject objectDocument = factory.newXMLObject(Collections.singletonList(documentContent), "Object-Document", null, null);				
			
			// QualifyingProperties
			Element elementQualifyingProperties = createElement(doc, "QualifyingProperties", XAdES_PREFIX, XAdES_NS);
			elementQualifyingProperties.setAttributeNS(null, "Target", "#SignatureId");

			Element elementSignedProperties = createElement(doc, "SignedProperties", XAdES_PREFIX, XAdES_NS);
			Attr attrSignedPropertiesId = doc.createAttributeNS(null, "Id");
			attrSignedPropertiesId.setValue("SignedPropertiesId");
			
			elementSignedProperties.setAttributeNodeNS(attrSignedPropertiesId);
			elementSignedProperties.setIdAttributeNS(null, "Id", true);
			
			elementQualifyingProperties.appendChild(elementSignedProperties);
			
			Element elementSignedSignatureProperties = createElement(doc, "SignedSignatureProperties", XAdES_PREFIX, XAdES_NS);
			elementSignedProperties.appendChild(elementSignedSignatureProperties);
			
			// Create SigningTime
			Element elementSigningTime = createElement(doc, "SigningTime", XAdES_PREFIX, XAdES_NS);
			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
			String strDate = simpleDateFormat.format(new Date());
			
			// Pasamos de +0254 a +02:54 de +hhmm a +hh:mm
			strDate = strDate.substring(0, strDate.length() - 2).concat(":").concat(strDate.substring(strDate.length()-2));
			
			elementSigningTime.setTextContent(strDate);
			elementSignedSignatureProperties.appendChild(elementSigningTime);
			
			// Create SigningCertificate
			Element elementSigningCertificate = createElement(doc, "SigningCertificate", XAdES_PREFIX, XAdES_NS);
			elementSignedSignatureProperties.appendChild(elementSigningCertificate);
			
			Element elementCert = createElement(doc, "Cert", XAdES_PREFIX, XAdES_NS);
			elementSigningCertificate.appendChild(elementCert);
			
			Element elementCertDigest = createElement(doc, "CertDigest", XAdES_PREFIX, XAdES_NS);
			elementCert.appendChild(elementCertDigest);
			
			Element elementDigestMethod = createElement(doc, "DigestMethod", XMLDSIG_PREFIX, XMLDSIG_NS);
			elementCertDigest.appendChild(elementDigestMethod);
			elementDigestMethod.setAttributeNS(null, "Algorithm", DigestMethod.SHA1);
			
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			byte [] data = certificate.getEncoded();
	        String strDigestValue = Base64.encodeBytes(md.digest(data));
			
	        Element elementDigestValue = createElement(doc, "DigestValue", XMLDSIG_PREFIX, XMLDSIG_NS);
			elementDigestValue.setTextContent(strDigestValue);
	        elementCertDigest.appendChild(elementDigestValue);		
			
			Element elementIssuerSerial = createElement(doc, "IssuerSerial", XAdES_PREFIX, XAdES_NS);
			elementCert.appendChild(elementIssuerSerial);
			
			Element X509IssuerName = createElement(doc, "X509IssuerName", XMLDSIG_PREFIX, XMLDSIG_NS);
			X509IssuerName.setTextContent(certificate.getIssuerX500Principal().getName());
			elementIssuerSerial.appendChild(X509IssuerName);
			
			Element X509SerialNumber = createElement(doc, "X509SerialNumber", XMLDSIG_PREFIX, XMLDSIG_NS);
			X509SerialNumber.setTextContent(certificate.getSerialNumber().toString());
			elementIssuerSerial.appendChild(X509SerialNumber);
					
			//SignedDataProperties
			Element elementSignedDataObjectProperties = createElement(doc, "SignedDataObjectProperties", XAdES_PREFIX, XAdES_NS);
			elementSignedProperties.appendChild(elementSignedDataObjectProperties);
			
			// Crear y rellenar DataObjectFormat		
			Element elementDataObjectFormat = createElement(doc, "DataObjectFormat", XAdES_PREFIX, XAdES_NS);	
			Attr objectReference = doc.createAttributeNS(null, "ObjectReference");
			objectReference.setValue("#Reference-Object-Document");
			elementDataObjectFormat.setAttributeNodeNS(objectReference);
			
			elementSignedDataObjectProperties.appendChild(elementDataObjectFormat);
			
			Element elementDescription = createElement(doc, "Description", XAdES_PREFIX, XAdES_NS);
			elementDescription.setTextContent(description);
			elementDataObjectFormat.appendChild(elementDescription);
			
			Element mimeTypeElement = createElement(doc, "MimeType", XAdES_PREFIX, XAdES_NS);
			mimeTypeElement.setTextContent(mimeType);
			elementDataObjectFormat.appendChild(mimeTypeElement);
			
			Element elementEncoding = createElement(doc, "Encoding", XAdES_PREFIX, XAdES_NS);
			elementEncoding.setTextContent(ENCODING);
			elementDataObjectFormat.appendChild(elementEncoding);
			
			Element elementUnsignedProperties = createElement(doc, "UnsignedProperties", XAdES_PREFIX, XAdES_NS);
			elementQualifyingProperties.appendChild(elementUnsignedProperties);
			
			Attr attrUnsignedPropertiesId = doc.createAttributeNS(null, "Id");
			attrUnsignedPropertiesId.setValue("UnsignedPropertiesId");
			
			elementUnsignedProperties.setAttributeNodeNS(attrUnsignedPropertiesId);
			elementUnsignedProperties.setIdAttributeNS(null, "Id", true);

			Element elementUnsignedSignatureProperties = createElement(doc, "UnsignedSignatureProperties", XAdES_PREFIX, XAdES_NS);
			elementUnsignedProperties.appendChild(elementUnsignedSignatureProperties);
			
			DOMStructure qualifPropStruct = new DOMStructure(elementQualifyingProperties);

			//List<XMLStructure> xmlObj = new ArrayList<XMLStructure>();
			//xmlObj.add(qualifPropStruct);
			
			XMLObject objectQualifyingProperties = factory.newXMLObject(Collections.singletonList(qualifPropStruct), "QualifyingPropertiesId", null, null);

			List<XMLStructure> objectsList = new ArrayList<XMLStructure>();
			objectsList.add(objectDocument);
			objectsList.add(objectQualifyingProperties);		
			
			
			// Create a Reference to a same-document URI that is an Object element and specify the SHA1 digest algorithm
			Reference refDocument = factory.newReference("#Object-Document", factory.newDigestMethod(DigestMethod.SHA1, null), null, null, "Reference-Object-Document");
			Reference refKeyInfo = factory.newReference("#KeyInfo", factory.newDigestMethod(DigestMethod.SHA1, null));
			Reference refSignedProperties = factory.newReference("#SignedPropertiesId", factory.newDigestMethod(DigestMethod.SHA1, null), null, "http://uri.etsi.org/01903#SignedProperties", "Reference-SignedProperties");
			
			List <XMLStructure> referencesList = new ArrayList<XMLStructure>();
			referencesList.add(refDocument);
			referencesList.add(refKeyInfo);
			referencesList.add(refSignedProperties);
			
			SignedInfo signedInfo = factory
					.newSignedInfo(factory.newCanonicalizationMethod(
							CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS,
							(C14NMethodParameterSpec) null), factory
							.newSignatureMethod(signatureMethod, null),
							referencesList);
			
			// Create a KeyValue containing the PublicKey that was generated
			KeyInfoFactory keyInfoFactory = factory.getKeyInfoFactory();
			KeyValue keyValue = keyInfoFactory.newKeyValue(certificate.getPublicKey());
					
			X509Data x509Data = keyInfoFactory.newX509Data(Collections.singletonList(certificate));
			
			List <XMLStructure> keyInfoList = new ArrayList<XMLStructure>();
			keyInfoList.add(keyValue);
			keyInfoList.add(x509Data);
			
			// Create a KeyInfo and add the KeyValue to it
			KeyInfo keyInfo = keyInfoFactory.newKeyInfo(keyInfoList, "KeyInfo");

			// Create a DOMSignContext and specify the PrivateKey for signing
			// and the document location of the XMLSignature
			DOMSignContext dsc = new DOMSignContext(privateKey, doc);
			
			// Set the default name space for xmldsig elements
			dsc.setDefaultNamespacePrefix(XMLDSIG_PREFIX);
					
			// Create the XMLSignature (but don't sign it yet)
			XMLSignature signature = factory.newXMLSignature(signedInfo, keyInfo, objectsList, "SignatureId", "SignatureValueId");

			// Lastly, generate the enveloping signature using the PrivateKey
			signature.sign(dsc);

			//
			// Generate timestamp element
			//
			NodeList signatureValueList = doc.getElementsByTagName("ds:SignatureValue");
			
			Canonicalizer canon = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS);
			byte canonXmlBytes[] = canon.canonicalizeSubtree(signatureValueList.item(0));
			
			byte [] timeStamp = null; 
			
			if (tsaUserName != null && tsaPassword != null) {
				// Authenticated TimeStamp
				timeStamp = TimeStamp.generateAuthenticatedTimeStamp(md.digest(canonXmlBytes), "SHA-1", tsaUrl, tsaUserName, tsaPassword);
			} else {
				timeStamp = TimeStamp.generateTimeStamp(md.digest(canonXmlBytes), "SHA-1", tsaUrl);
			}
			
			if (timeStamp == null) {
				throw new Exception("Error generating timestamp.");
			}
			
			String strTimeStamp = Base64.encodeBytes(timeStamp);
			
			NodeList unsignedSignaturePropertiesList = doc.getElementsByTagName("etsi:UnsignedSignatureProperties");
			Node nodeUnsignedSignatureProperties = unsignedSignaturePropertiesList.item(0);
			
			// SignatureTimeStamp
			Element elementSignatureTimeStamp = createElement(doc, "SignatureTimeStamp", XAdES_PREFIX, XAdES_NS);	
			
			Attr attrSignatureTimeStampId = doc.createAttributeNS(null, "Id");
			attrSignatureTimeStampId.setValue("SignatureTimeStampId");
			
			elementSignatureTimeStamp.setAttributeNodeNS(attrSignatureTimeStampId);
			
			nodeUnsignedSignatureProperties.appendChild(elementSignatureTimeStamp);
			
			// CanonicalizationMethod
			Element elementCanonicalizationMethod = createElement(doc, "CanonicalizationMethod", XMLDSIG_PREFIX, XMLDSIG_NS);	
			
			Attr attrAlgorithm = doc.createAttributeNS(null, "Algorithm");
			attrAlgorithm.setValue(Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS);
			
			elementCanonicalizationMethod.setAttributeNodeNS(attrAlgorithm);
			
			elementSignatureTimeStamp.appendChild(elementCanonicalizationMethod);
					
			// EncapsulatedTimeStamp etsi:EncapsulatedTimeStamp
			Element elementEncapsulatedTimeStamp = createElement(doc, "EncapsulatedTimeStamp", XAdES_PREFIX, XAdES_NS);	
			
			Attr attrEncapsulatedTimeStampId = doc.createAttributeNS(null, "Id");
			attrEncapsulatedTimeStampId.setValue("EncapsulatedTimeStampId");
			
			elementEncapsulatedTimeStamp.setAttributeNodeNS(attrEncapsulatedTimeStampId);
			elementEncapsulatedTimeStamp.setTextContent(strTimeStamp);
			
			elementSignatureTimeStamp.appendChild(elementEncapsulatedTimeStamp);
			
		} catch (Exception e) {
			Logger.getLogger(XAdES.class.getName()).log(Level.SEVERE, "Error generating Enveloping XAdES-T BES signature.", e);
			doc = null;
		}
		
		return doc;
	}
	
	
	/**
	 * Generate Detached XAdES-T BES signature.
	 * @param keystore The keystore with keys to sign.
	 * @param keystoreType The keystore's type. (Only support JKS and PKCS12 types)
	 * @param keystorePassword The keystore's password.
	 * @param privateKeyPassword The private key password.
	 * @param document The document to be signed. The document will be Base64 encoded and XML wappred enveloped. 
	 * @param documentName The document's name.
	 * @param description The document' description.
	 * @param mimeType The document's mime type.
	 * @param tsaUrl The TSA's URL.
	 * @param tsaUserName The username for TSA, can be null if authentication isn't necessary.
	 * @param tsaPassword The password for TSA, can be null if authentication isn't necessary.
	 * @return Document with Detached XAdES-T BES signature or null in case of error.
	 */
	public static Document generateDetachedBES_T (InputStream keystore, String keystoreType, String keystorePassword, String privateKeyPassword, InputStream document, String documentName, String description, String mimeType, String tsaUrl, String tsaUserName, String tsaPassword) {
		
		Document doc = null;
		
		String KEYSTORE_TYPE = "";
				
		try {
			
			if (keystoreType.equalsIgnoreCase("PKCS12")) {
				KEYSTORE_TYPE = "PKCS12";
			} else if (keystoreType.equalsIgnoreCase("JKS")) {
				KEYSTORE_TYPE = "JKS";
			} else {
				throw new Exception("Bad keystore type: " + keystoreType);
			}
			
			KeyStore ks = KeyStore.getInstance(KEYSTORE_TYPE);
			ks.load(keystore, keystorePassword.toCharArray());

			// Get private key for encrypt
			Enumeration<String> enumAlias = ks.aliases();
			String alias = enumAlias.nextElement();
			PrivateKey privateKey = (PrivateKey) ks.getKey(alias, privateKeyPassword.toCharArray());
			X509Certificate certificate = (X509Certificate) ks.getCertificate(alias);
			
			// Setting Signature Method
			String signatureMethod = null;
			
			if (privateKey.getAlgorithm().equals("DSA")) {
				signatureMethod = SignatureMethod.DSA_SHA1;
			} else if (privateKey.getAlgorithm().equals("RSA")) {
				signatureMethod = SignatureMethod.RSA_SHA1;
			}
			
			// Create a DOM XMLSignatureFactory that will be used to generate the enveloped signature
			XMLSignatureFactory factory = XMLSignatureFactory.getInstance("DOM", new XMLDSigRI());
			
			byte [] documentByteArray = Stream.toByteArray(document);
			
			String url = documentName.replace("\\", "/");
			
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			
			// Create the Document that will hold the resulting XMLSignature
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true); // must be set
			doc = dbf.newDocumentBuilder().newDocument();
						
			// QualifyingProperties
			Element elementQualifyingProperties = createElement(doc, "QualifyingProperties", XAdES_PREFIX, XAdES_NS);
			elementQualifyingProperties.setAttributeNS(null, "Target", "#SignatureId");

			Element elementSignedProperties = createElement(doc, "SignedProperties", XAdES_PREFIX, XAdES_NS);
			Attr attrSignedPropertiesId = doc.createAttributeNS(null, "Id");
			attrSignedPropertiesId.setValue("SignedPropertiesId");
			
			elementSignedProperties.setAttributeNodeNS(attrSignedPropertiesId);
			elementSignedProperties.setIdAttributeNS(null, "Id", true);
			
			elementQualifyingProperties.appendChild(elementSignedProperties);
			
			Element elementSignedSignatureProperties = createElement(doc, "SignedSignatureProperties", XAdES_PREFIX, XAdES_NS);
			elementSignedProperties.appendChild(elementSignedSignatureProperties);
			
			// Create SigningTime
			Element elementSigningTime = createElement(doc, "SigningTime", XAdES_PREFIX, XAdES_NS);
			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
			String strDate = simpleDateFormat.format(new Date());
			
			// Pasamos de +0254 a +02:54 de +hhmm a +hh:mm
			strDate = strDate.substring(0, strDate.length() - 2).concat(":").concat(strDate.substring(strDate.length()-2));
			
			elementSigningTime.setTextContent(strDate);
			elementSignedSignatureProperties.appendChild(elementSigningTime);
			
			
			// Create SigningCertificate
			Element elementSigningCertificate = createElement(doc, "SigningCertificate", XAdES_PREFIX, XAdES_NS);
			elementSignedSignatureProperties.appendChild(elementSigningCertificate);
			
			Element elementCert = createElement(doc, "Cert", XAdES_PREFIX, XAdES_NS);
			elementSigningCertificate.appendChild(elementCert);
			
			Element elementCertDigest = createElement(doc, "CertDigest", XAdES_PREFIX, XAdES_NS);
			elementCert.appendChild(elementCertDigest);
			
			Element elementDigestMethod = createElement(doc, "DigestMethod", XMLDSIG_PREFIX, XMLDSIG_NS);
			elementCertDigest.appendChild(elementDigestMethod);
			elementDigestMethod.setAttributeNS(null, "Algorithm", DigestMethod.SHA1);
			
			byte [] data = certificate.getEncoded();
	        String strDigestValue = Base64.encodeBytes(md.digest(data));
			
	        Element elementDigestValue = createElement(doc, "DigestValue", XMLDSIG_PREFIX, XMLDSIG_NS);
			elementDigestValue.setTextContent(strDigestValue);
	        elementCertDigest.appendChild(elementDigestValue);		
			
			Element elementIssuerSerial = createElement(doc, "IssuerSerial", XAdES_PREFIX, XAdES_NS);
			elementCert.appendChild(elementIssuerSerial);
			
			Element X509IssuerName = createElement(doc, "X509IssuerName", XMLDSIG_PREFIX, XMLDSIG_NS);
			X509IssuerName.setTextContent(certificate.getIssuerX500Principal().getName());
			elementIssuerSerial.appendChild(X509IssuerName);
			
			Element X509SerialNumber = createElement(doc, "X509SerialNumber", XMLDSIG_PREFIX, XMLDSIG_NS);
			X509SerialNumber.setTextContent(certificate.getSerialNumber().toString());
			elementIssuerSerial.appendChild(X509SerialNumber);
					
			//SignedDataProperties
			Element elementSignedDataObjectProperties = createElement(doc, "SignedDataObjectProperties", XAdES_PREFIX, XAdES_NS);
			elementSignedProperties.appendChild(elementSignedDataObjectProperties);
			
			// Crear y rellenar DataObjectFormat		
			Element elementDataObjectFormat = createElement(doc, "DataObjectFormat", XAdES_PREFIX, XAdES_NS);	
			Attr objectReference = doc.createAttributeNS(null, "ObjectReference");
			objectReference.setValue("#Reference-Document");
			elementDataObjectFormat.setAttributeNodeNS(objectReference);
			
			elementSignedDataObjectProperties.appendChild(elementDataObjectFormat);
			
			Element elementDescription = createElement(doc, "Description", XAdES_PREFIX, XAdES_NS);
			elementDescription.setTextContent(description);
			elementDataObjectFormat.appendChild(elementDescription);
			
			Element mimeTypeElement = createElement(doc, "MimeType", XAdES_PREFIX, XAdES_NS);
			mimeTypeElement.setTextContent(mimeType);
			elementDataObjectFormat.appendChild(mimeTypeElement);
			
			Element elementEncoding = createElement(doc, "Encoding", XAdES_PREFIX, XAdES_NS);
			elementEncoding.setTextContent(ENCODING);
			elementDataObjectFormat.appendChild(elementEncoding);
			
			Element elementUnsignedProperties = createElement(doc, "UnsignedProperties", XAdES_PREFIX, XAdES_NS);
			elementQualifyingProperties.appendChild(elementUnsignedProperties);

			Attr attrUnsignedPropertiesId = doc.createAttributeNS(null, "Id");
			attrUnsignedPropertiesId.setValue("UnsignedPropertiesId");
			
			elementUnsignedProperties.setAttributeNodeNS(attrUnsignedPropertiesId);
			
			Element elementUnsignedSignatureProperties = createElement(doc, "UnsignedSignatureProperties", XAdES_PREFIX, XAdES_NS);
			elementUnsignedProperties.appendChild(elementUnsignedSignatureProperties);
			
			DOMStructure qualifPropStruct = new DOMStructure(elementQualifyingProperties);

			//List<XMLStructure> xmlObj = new ArrayList<XMLStructure>();
			//xmlObj.add(qualifPropStruct);
			
			XMLObject objectQualifyingProperties = factory.newXMLObject(Collections.singletonList(qualifPropStruct), "QualifyingPropertiesId", null, null);

			List<XMLStructure> objectsList = new ArrayList<XMLStructure>();
			objectsList.add(objectQualifyingProperties);		
			

			// Create a Reference to an external URI that will be digested using the SHA1 digest algorithm
			Reference refDocument = factory.newReference("file:/"+url , factory.newDigestMethod(DigestMethod.SHA1, null), null, null, "Reference-Document", md.digest(documentByteArray));
			Reference refKeyInfo = factory.newReference("#KeyInfo", factory.newDigestMethod(DigestMethod.SHA1, null));
			Reference refSignedProperties = factory.newReference("#SignedPropertiesId", factory.newDigestMethod(DigestMethod.SHA1, null), null, "http://uri.etsi.org/01903#SignedProperties", "Reference-SignedProperties");
			
			List <XMLStructure> referencesList = new ArrayList<XMLStructure>();
			referencesList.add(refDocument);
			referencesList.add(refKeyInfo);
			referencesList.add(refSignedProperties);

			// Create the SignedInfo
			SignedInfo signedInfo = factory
					.newSignedInfo(factory.newCanonicalizationMethod(
							CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS,
							(C14NMethodParameterSpec) null), factory
							.newSignatureMethod(signatureMethod, null),
							referencesList);
			
			
			// Create a KeyValue containing the PublicKey that was generated
			KeyInfoFactory keyInfoFactory = factory.getKeyInfoFactory();
			KeyValue keyValue = keyInfoFactory.newKeyValue(certificate.getPublicKey());
							
			X509Data x509Data = keyInfoFactory.newX509Data(Collections.singletonList(certificate));
					
			List <XMLStructure> keyInfoList = new ArrayList<XMLStructure>();
			keyInfoList.add(keyValue);
			keyInfoList.add(x509Data);
					
			// Create a KeyInfo and add the KeyValue to it
			KeyInfo keyInfo = keyInfoFactory.newKeyInfo(keyInfoList, "KeyInfo");
			
			// Create a DOMSignContext and specify the PrivateKey and location of the resulting XMLSignature's parent element
			DOMSignContext dsc = new DOMSignContext(privateKey, doc);

			// Set the default name sapace for xmldsig elements
			dsc.setDefaultNamespacePrefix(XMLDSIG_PREFIX);
					
			// Create the XMLSignature (but don't sign it yet)
			XMLSignature signature = factory.newXMLSignature(signedInfo, keyInfo, objectsList, "SignatureId", "SignatureValueId");
			
			/*
			// Create the XMLSignature (but don't sign it yet)
			XMLSignature signature = factory.newXMLSignature(si, ki);
			*/

			// Marshal, generate (and sign) the detached XMLSignature. The DOM
			// Document will contain the XML Signature if this method returns
			// successfully.
			signature.sign(dsc);

			//
			// Generate timestamp element
			//
			NodeList signatureValueList = doc.getElementsByTagName("ds:SignatureValue");
			
			Canonicalizer canon = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS);
			byte canonXmlBytes[] = canon.canonicalizeSubtree(signatureValueList.item(0));			

			byte [] timeStamp = null; 
			
			if (tsaUserName != null && tsaPassword != null) {
				// Authenticated TimeStamp
				timeStamp = TimeStamp.generateAuthenticatedTimeStamp(md.digest(canonXmlBytes), "SHA-1", tsaUrl, tsaUserName, tsaPassword);
			} else {
				timeStamp = TimeStamp.generateTimeStamp(md.digest(canonXmlBytes), "SHA-1", tsaUrl);
			}
			
			if (timeStamp == null) {
				throw new Exception("Error generating timestamp.");
			}
			
			String strTimeStamp = Base64.encodeBytes(timeStamp);
			
			NodeList unsignedSignaturePropertiesList = doc.getElementsByTagName("etsi:UnsignedSignatureProperties");
			Node nodeUnsignedSignatureProperties = unsignedSignaturePropertiesList.item(0);
			
			// SignatureTimeStamp
			Element elementSignatureTimeStamp = createElement(doc, "SignatureTimeStamp", XAdES_PREFIX, XAdES_NS);	
			
			Attr attrSignatureTimeStampId = doc.createAttributeNS(null, "Id");
			attrSignatureTimeStampId.setValue("SignatureTimeStampId");
			
			elementSignatureTimeStamp.setAttributeNodeNS(attrSignatureTimeStampId);
			
			nodeUnsignedSignatureProperties.appendChild(elementSignatureTimeStamp);
			
			// CanonicalizationMethod
			Element elementCanonicalizationMethod = createElement(doc, "CanonicalizationMethod", XMLDSIG_PREFIX, XMLDSIG_NS);	
			
			Attr attrAlgorithm = doc.createAttributeNS(null, "Algorithm");
			attrAlgorithm.setValue(Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS);
			
			elementCanonicalizationMethod.setAttributeNodeNS(attrAlgorithm);
			
			elementSignatureTimeStamp.appendChild(elementCanonicalizationMethod);
					
			// EncapsulatedTimeStamp etsi:EncapsulatedTimeStamp
			Element elementEncapsulatedTimeStamp = createElement(doc, "EncapsulatedTimeStamp", XAdES_PREFIX, XAdES_NS);	
			
			Attr attrEncapsulatedTimeStampId = doc.createAttributeNS(null, "Id");
			attrEncapsulatedTimeStampId.setValue("EncapsulatedTimeStampId");
			
			elementEncapsulatedTimeStamp.setAttributeNodeNS(attrEncapsulatedTimeStampId);
			elementEncapsulatedTimeStamp.setTextContent(strTimeStamp);
			
			elementSignatureTimeStamp.appendChild(elementEncapsulatedTimeStamp);
			
		} catch (Exception e) {
			Logger.getLogger(XAdES.class.getName()).log(Level.SEVERE, "Error generating Enveloping XAdES-T BES signature.", e);
			doc = null;
		}
		
		return doc;
	}
	
	
	private static Element createElement(Document doc, String tag,String prefix, String nsURI) {
		String qName = prefix == null ? tag : prefix + ":" + tag;
		return doc.createElementNS(nsURI, qName);
	}
	
	/**
	 * Create a org.w3c.dom.Document containing the InputStream.
	 * @param document InputStream containing a document to sign.
	 * @param name The name and identification of document.
	 * @return org.w3c.dom.Document or null in case of error.
	 */
	private static Document createDocument(InputStream document, String name) {

		Document doc = null;
		try {
			// Parse document.
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilderFactory.setNamespaceAware(true);
			DocumentBuilder docBuilder;
			
			docBuilder = docBuilderFactory.newDocumentBuilder();

			// Document Base64 encoding
			byte[] bytes = null;
			
			bytes = Stream.toByteArray(document);
			
			String content = Base64.encodeBytes(bytes);
			
			// Create document
			doc = docBuilder.newDocument();
			Element root = doc.createElementNS(null, "document");
			doc.appendChild(root);

			// Add content to document
			Element node = doc.createElementNS(null, "parts");
			node.setAttributeNS(null, "Encoding", "urn:ietf-org:base64");
			node.setAttributeNS(null, "FileName", name);
			node.setAttributeNS(null, "Id", name);
			node.setIdAttributeNS(null, "Id", true);
			
			node.appendChild(doc.createTextNode(content));
			root.appendChild(node);
		} catch (DOMException e) {
			Logger.getLogger(XAdES.class.getName()).log(Level.SEVERE, "Error creating XML document.", e);
			doc = null;
		} catch (ParserConfigurationException e) {
			Logger.getLogger(XAdES.class.getName()).log(Level.SEVERE, "Error creating a DocumentBuilder.", e);
			doc = null;
		} catch (IOException e) {
			Logger.getLogger(XAdES.class.getName()).log(Level.SEVERE, "Error getting document content.", e);
			doc = null;
		}
		
		return doc;
	}
}
