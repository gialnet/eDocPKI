package es.redmoon.pl.ts;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.x500.X500Principal;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import oracle.sql.BLOB;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEREncodable;
import org.bouncycastle.asn1.DERInteger;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.tsp.TimeStampResp;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.tsp.TSPAlgorithms;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampRequest;
import org.bouncycastle.tsp.TimeStampRequestGenerator;
import org.bouncycastle.tsp.TimeStampResponse;
import org.bouncycastle.tsp.TimeStampToken;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import es.redmoon.utils.Base64;
import es.redmoon.utils.DigestAlgorithm;
import es.redmoon.utils.LOBUtils;
import es.redmoon.utils.Stream;


/**
 * Class for create timestamp.
 * @version 0.1
 */
public class TimeStamp {
	
	/**
	 * Create timestamp request.
	 * @param digest Digest for timestamp.
	 * @param hash Digest algorithm.
	 * @return Timestamp request.
	 */
	private static TimeStampRequest createRequest(byte[] digest, String hash){
		
		// Create request generator
		TimeStampRequestGenerator requestGenerator = new TimeStampRequestGenerator();
		requestGenerator.setCertReq(true); // Add TSA public key
		
		String digestAlgorithm = DigestAlgorithm.getAlgorithm(hash);
		TimeStampRequest request = requestGenerator.generate(digestAlgorithm, digest);
		
		return request;
	}
		
	/**
	 * Create http connection for timestamp request.
	 * @param tsa_url TimeStamp Authority URL.
	 * @param length Request length.
	 * @return The connection for timestamp request or null in case of error.
	 */
	private static HttpURLConnection createConnection(String tsa_url, int length){
		
		URL url;
		try {
			// Create http connection
			url = new URL(tsa_url);
			HttpURLConnection connecion = (HttpURLConnection) url.openConnection();
			
			// Config the connection
			connecion.setDoInput(true);
			connecion.setDoOutput(true);
			connecion.setRequestMethod("POST");
			connecion.setRequestProperty("Content-type", "application/timestamp-query"); 
			connecion.setRequestProperty("Content-length", String.valueOf(length));
			
			return connecion;
			
		} catch (MalformedURLException e) {
			String error = "Error creating URL.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (IOException e) {
			String error = "Error creating http connection.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			return null;
		}
	}
	
	/**
	 * Create authenticated http connection for timestamp request.
	 * @param tsa_url TimeStamp Authority URL.
	 * @param user User name.
	 * @param password User password.
	 * @param length Request length.
	 * @return The connection for timestamp request or null in case of error.
	 */
	private static HttpURLConnection createAuthenticatedConnection(String tsa_url, final String user, final String password, int length){
		
		try {
			// Create http connection
			URL url;
			url = new URL(tsa_url);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			
			Authenticator.setDefault (new Authenticator() { 
			    protected PasswordAuthentication getPasswordAuthentication() { 
			        return new PasswordAuthentication (user, password.toCharArray()); 
			    } 
			});
			
			// Config the connection
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-type", "application/timestamp-query"); 
			connection.setRequestProperty("Content-length", String.valueOf(length));
			
			return connection;
			
		} catch (MalformedURLException e) {
			String error = "Error creating URL.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (IOException e) {
			String error = "Error creating authenticated http connection.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			return null;
		}
	}
	
	/**
	 * Do timestamp request.
	 * @param request Timestamp request.
	 * @param connection Connection for timestamp.
	 * @return ASN1Sequence with TSA response or null in case of error.
	 */
	private static ASN1Sequence doRequest(TimeStampRequest request, HttpURLConnection connection){
		
		ASN1InputStream asn1Is = null;
		ASN1Sequence sequence = null;
		
		try {
			// Get request in ASN.1 format
			byte[] requestASN1;
			
			requestASN1 = request.getEncoded();
			OutputStream out = connection.getOutputStream();
			out.write(requestASN1);
			out.flush();
			
			// Verifing
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) { 
				throw new IOException("Received HTTP error: " + connection.getResponseCode() + " - " + connection.getResponseMessage()); 
			} 
			
			InputStream in = connection.getInputStream();
	        
			// Get response, the timestamp   
			asn1Is = new ASN1InputStream(in);
			sequence = ASN1Sequence.getInstance(asn1Is.readObject());
			
			// Validating response
			TimeStampResp tspResp = new TimeStampResp(sequence);
			TimeStampResponse response = new TimeStampResponse(tspResp);
			response.validate(request);
			
		} catch (IOException e) {
			String error = "Error creating request.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			sequence = null;
		} catch (TSPException e) {
			String error = "Error doing request.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			sequence = null;
		} finally {
			try {
				if (asn1Is != null) {
					asn1Is.close();
				}
			} catch (IOException e) {
				Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, "Error freeing resources.", e);
			}
		}
		
		return sequence;
	}
	
	/**
	 * Create BLOB containing XML with timestamp info.
	 * @param sequence ASN.1 sequence with timestamp.
	 * @return BLOB containing XML with timestamp info or null in case of error.
	 */
	private static BLOB createXmlBLOB(ASN1Sequence sequence){
		
		BLOB blob = null;
		ByteArrayOutputStream baos = null;
		
		try {
			// Get timestamp response for XML tags
			TimeStampResp tspResp = new TimeStampResp(sequence);
			TimeStampResponse response;
			response = new TimeStampResponse(tspResp);
			
			TimeStampToken timeStampToken = response.getTimeStampToken();
	        
			// Get timestamp in DER format to store in XML
			// No se puede obtener del TimeStampResponse, tiene un fallo
			// BouncyCastle, hay que sacarlo de la secuencia
			DEREncodable enc = sequence.getObjectAt(1);
			byte [] timestamp = enc.getDERObject().getEncoded();
			
			// Store data in XML

			DocumentBuilder docBuilder;
			
			docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			
			Document document = docBuilder.newDocument();		
	        
			Element root = document.createElement("tst"); // Time Stamp Authority
			document.appendChild(root);
						
			Element node = document.createElement("tsa");
			// Get TSA name
			String tsa = null;
					
			if (timeStampToken.getTimeStampInfo().getTsa() != null){

				//tsa = timeStampToken.getTimeStampInfo().getTsa().toString();
				tsa = new X500Principal(X509Name.getInstance((timeStampToken.getTimeStampInfo().getTsa()).getName()).getEncoded()).toString();
			}

			// If name == null try extract from certificate
			if (tsa == null){
				CertStore cs = timeStampToken.getCertificatesAndCRLs("Collection", null);
				Collection<? extends Certificate> certs = cs.getCertificates(null);
				if (certs.size() > 0) {
					Certificate cert = certs.iterator().next();
					if (cert instanceof X509Certificate) {
						tsa =  ((X509Certificate) cert).getSubjectX500Principal().getName();
					}
				}
			}
			node.appendChild(document.createTextNode(tsa));
			root.appendChild(node);
			
			node = document.createElement("serialNumber");
			node.appendChild(document.createTextNode(timeStampToken.getTimeStampInfo().getSerialNumber().toString()));
			root.appendChild(node);
			
			node = document.createElement("genTime");
			node.appendChild(document.createTextNode(timeStampToken.getTimeStampInfo().getGenTime().toString()));
			root.appendChild(node);
			
			node = document.createElement("tsr");
			// Get timestamp, Base64 encoded
			node.appendChild(document.createTextNode(Base64.encodeBytes(timestamp)));
			root.appendChild(node);

			baos = new ByteArrayOutputStream();
			
			Source source = new DOMSource(document);
			Result result = new StreamResult(baos);

			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			// Quitamos la declaracion de la salida <?xml version="1.0" encoding="UTF-8" standalone="no"?>
			transformer.setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.transform(source, result);
			
			blob = LOBUtils.OutputStreamToBLOB(baos);
			
		} catch (TSPException e) {
			String error = "Fallo al crear el TimeStampResponse.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			blob = null;
		} catch (IOException e) {
			String error = "Fallo al crear el CLOB con el Sello de Tiempo.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			blob = null;
		} catch (ParserConfigurationException e) {
			String error = "Fallo al crear el DocumentBuilder.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			blob = null;
		} catch (TransformerConfigurationException e) {
			String error = "Fallo al crear el transformador de documentos.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			blob = null;
		} catch (TransformerFactoryConfigurationError e) {
			String error = "Fallo al crear la factoria de transformadores de documentos.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			blob = null;
		} catch (TransformerException e) {
			String error = "Fallo al crear el transformador de documentos.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			blob = null;
		} catch (NoSuchAlgorithmException e) {
			String error = "Fallo al obtener el almacén de certificados.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			blob = null;
		} catch (NoSuchProviderException e) {
			String error = "Fallo al obtener el almacén de certificados.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			blob = null;
		} catch (CMSException e) {
			String error = "Fallo al obtener el almacén de certificados.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			blob = null;
		} catch (CertStoreException e) {
			String error = "Fallo al obtener los certificados del Almacén.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			blob = null;
		} finally {
			try {
				if (baos != null)
					baos.close();
			} catch (Exception e) {
				Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, "Error freeing OutputStream resources.", e);
			}
		}
		
		return blob;
	}

	/**
	 * Generate timestamp ASN1 sequence.
	 * @param digest Digest for timestamp.
	 * @param hash Digest algorithm.
	 * @param tsa_url TimeStamp Authority URL.
	 * @return ASN1Sequence with TSA response or null in case of error.
	 */
	private static ASN1Sequence generateASN1Sequence(byte[] digest, String hash, String tsa_url){
		
		ASN1Sequence sequence = null;
				
		try {
			// 1.- Create request
			TimeStampRequest request = TimeStamp.createRequest(digest, hash);
			
			// If some error happened return null
			if (request == null)
				return null;
			
			// Get request in ASN.1 format for get the length
			byte[] asn1Request;
			asn1Request = request.getEncoded();
			
			// 2.- Create connection
			HttpURLConnection connection = TimeStamp.createConnection(tsa_url, asn1Request.length);
			
			// If some error happened return null
			if (connection == null)
				return null;
			
			// 3.- Do request
			sequence = TimeStamp.doRequest(request, connection);
			
		} catch (IOException e) {
			String error = "Error generating ASN.1 sequence.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			sequence = null;
		}
		
		return sequence;
	}
	
	/**
	 * Generate authenticated timestamp ASN1 sequence.
	 * @param digest Digest for timestamp.
	 * @param hash Digest algorithm.
	 * @param tsa_url TimeStamp Authority URL.
	 * @param user TSA user name.
	 * @param password TSA user password. 
	 * @return ASN1Sequence with TSA response or null in case of error.
	 */
	private static ASN1Sequence generateAuthenticatedASN1Sequence(byte[] digest, String hash, String tsa_url, String user, String password){
		
		ASN1Sequence sequence = null;
		
		try {
			// 1.- Create request
			TimeStampRequest request = TimeStamp.createRequest(digest, hash);
			
			// If some error happened return null
			if (request == null)
				return null;
			
			// Get request in ASN.1 format for get the length
			byte[] asn1Request;
			asn1Request = request.getEncoded();
			
			// 2.- Create connection
			HttpURLConnection connection = TimeStamp.createAuthenticatedConnection(tsa_url, user, password, asn1Request.length);
			
			// If some error happened return null
			if (connection == null)
				return null;
			
			// 3.- Do request
			sequence = TimeStamp.doRequest(request, connection);
					
		} catch (IOException e) {
			String error = "Error generating authenticated ASN.1 sequence.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			sequence = null;
		} 
		
		return sequence;
	}
	
	/**
	 * Generate timestamp.
	 * @param digest Digest for timestamp.
	 * @param hash Digest algorithm.
	 * @param tsa_url TimeStamp Authority URL.
	 * @return Byte array containing timestamp or null in case of error.
	 */
	public static byte[] generateTimeStamp(byte[] digest, String hash, String tsa_url){
		
		byte [] timeStamp = null;
				
		try {
			
			ASN1Sequence sequence = TimeStamp.generateASN1Sequence(digest, hash, tsa_url);
			
			// If some error happened return null
			if (sequence == null) {
				timeStamp = null;
			} else {
				// Create byte[] containing timestamp
				DEREncodable enc = sequence.getObjectAt(1);
				timeStamp = enc.getDERObject().getEncoded();	
			}			
			
		} catch (IOException e) {
			String error = "Error encoding response in ASN.1 format.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			timeStamp = null;
		}
		
		return timeStamp;
	}
	
	/**
	 * Create XML containg timestamp info.
	 * @param digest Digest for timestamp.
	 * @param hash Digest algorithm.
	 * @param tsa_url TimeStamp Authority URL.
	 * @param user TSA user name.
	 * @param password TSA user password. 
	 * @return Byte array containing XML containing timestamp info or null in case of error.
	 */
	public static byte[] generateAuthenticatedTimeStamp(byte[] digest, String hash, String tsa_url, String user, String password){
		
		byte [] timeStamp = null;
		
		try {
			
			ASN1Sequence sequence = TimeStamp.generateAuthenticatedASN1Sequence(digest, hash, tsa_url, user, password);
			
			// If some error happened return null
			if (sequence == null) {
				timeStamp = null;
			} else {
				// Create byte[] containing timestamp
				DEREncodable enc = sequence.getObjectAt(1);
				timeStamp = enc.getDERObject().getEncoded();	
			}
					
		} catch (IOException e) {
			String error = "Error encoding authenticated response in ASN.1 format.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			timeStamp = null;
		}
		
		return timeStamp;
	}
		
	/**
	 * Create XML containg timestamp info.
	 * @param digest Digest for timestamp.
	 * @param hash Digest algorithm.
	 * @param tsa_url TimeStamp Authority URL.
	 * @return BLOB containing XML containing timestamp info or null in case of error.
	 */
	public static BLOB createXMLTimeStamp(byte[] digest, String hash, String tsa_url){
		
		try {
			
			ASN1Sequence sequence = TimeStamp.generateASN1Sequence(digest, hash, tsa_url);
			
			// If some error happened return null
			if (sequence == null)
				return null;
			
			// Create BLOB containing XML
			BLOB blob = TimeStamp.createXmlBLOB(sequence);
			
			// If some error happened return null
			if (blob == null)
				return null;
			
			return blob;
			
		} catch (Exception e) {
			String error = "Error creating XML timestamp.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			return null;
		}
	}	

	/**
	 * Create timestamp.
	 * @param digest Digest for timestamp.
	 * @param hash Digest algorithm.
	 * @param tsa_url TimeStamp Authority URL.
	 * @return BLOB containing timestamp or null in case of error.
	 */
	public static BLOB createTimeStamp(byte[] digest, String hash, String tsa_url){
		
		ByteArrayOutputStream baos = null;
		BLOB blob = null;
				
		try {
			
			byte [] sello = generateTimeStamp(digest, hash, tsa_url);
			
			if (sello != null) {
				// Create BLOB containing timestamp
				baos = new ByteArrayOutputStream();
				
				baos.write(sello);
				
				blob = LOBUtils.OutputStreamToBLOB(baos);
			} else {
				blob = null;
			}
			
		} catch (Exception e) {
			String error = "Error creating timestamp.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			blob = null;
		} finally {
			try {
				if (baos != null)
					baos.close();
			} catch (Exception e) {
				Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, "Error freeing OutputStream resources.", e);
			}
		}
		
		return blob;
	}
		
	/**
	 * Create XML containg authenticated timestamp info.
	 * @param digest Digest for timestamp.
	 * @param hash Digest algorithm.
	 * @param tsa_url TimeStamp Authority URL.
	 * @param user TSA user name.
	 * @param password TSA user password. 
	 * @return BLOB containing XML containing authenticated timestamp info or null in case of error.
	 */
	public static BLOB createXMLAuthenticatedTimeStamp(byte[] digest, String hash, String tsa_url, String user, String password){
		
		try {
			
			ASN1Sequence sequence = TimeStamp.generateAuthenticatedASN1Sequence(digest, hash, tsa_url, user, password);
			
			// If some error happened return null
			if (sequence == null)
				return null;
			
			// Create BLOB containing XML
			BLOB blob = TimeStamp.createXmlBLOB(sequence);
			
			// If some error happened return null
			if (blob == null)
				return null;
			
			return blob;
						
		} catch (Exception e) {
			String error = "Error creating authenticated XML tiemstamp.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			return null;
		}
	}

	/**
	 * Create authenticated timestamp.
	 * @param digest Digest for timestamp.
	 * @param hash Digest algorithm.
	 * @param tsa_url TimeStamp Authority URL.
	 * @param user TSA user name.
	 * @param password TSA user password. 
	 * @return BLOB containing authenticated timestamp or null in case of error.
	 */
	public static BLOB createAuthenticatedTimeStamp(byte[] digest, String hash, String tsa_url, String user, String password){
		
		ByteArrayOutputStream baos = null;
		BLOB blob = null;
		
		try {
			
			ASN1Sequence sequence = TimeStamp.generateAuthenticatedASN1Sequence(digest, hash, tsa_url, user, password);
			
			// If some error happened return null
			if (sequence == null)
				return null;
			
			// Create BLOB containing timestamp
			DEREncodable enc = sequence.getObjectAt(1);
			byte [] timestamp = enc.getDERObject().getEncoded();
			
			baos = new ByteArrayOutputStream();
			baos.write(timestamp);
			
			blob = LOBUtils.OutputStreamToBLOB(baos);
					
		} catch (IOException e) {
			String error = "Error creating authenticated timestamp.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			blob = null;
		} finally {
			try {
				if (baos != null)
					baos.close();
			} catch (Exception e) {
				Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, "Error freeing OutputStream resources.", e);
			}
		}
		
		return blob;
	}
	
	/**
	 * Create timeStamp at network/red SARA.
	 * @param digest Digest for timestamp.
	 * @param policy TimeStamp policy.
	 * @param centro Identificación del centro que pide el sello de tiempo. 
	 * @return BLOB containing XML containing timestamp info or null in case of error.
	 */
	
	public static BLOB createXMLRedSARATimeStamp(byte[] digest, String policy, String centro){
				
		ASN1Sequence sequence = doRedSARARequest(digest, policy, centro);	
		
		BLOB blob = TimeStamp.createXmlBLOB(sequence);
		
		// If some error happened return null
		if (blob == null)
			return null;
		
		return blob;
	}
	
	/**
	 * Create timeStamp at network/red SARA.
	 * @param digest Digest for timestamp.
	 * @param policy TimeStamp policy.
	 * @param centro Identificación del centro que pide el sello de tiempo. 
	 * @return BLOB containing timestamp or null in case of error.
	 */
	public static BLOB createRedSARATimeStamp(byte[] digest, String policy, String centro){
		
		ByteArrayOutputStream baos = null;
		BLOB blob = null;
		
		try {
			
			ASN1Sequence sequence = doRedSARARequest(digest, policy, centro);
			
			// If some error happened return null
			if (sequence == null)
				return null;
			
			// Create BLOB containing timestamp
			DEREncodable enc = sequence.getObjectAt(1);
			byte [] timestamp = enc.getDERObject().getEncoded();
			
			baos = new ByteArrayOutputStream();
			baos.write(timestamp);
			
			blob = LOBUtils.OutputStreamToBLOB(baos);
					
		} catch (IOException e) {
			String error = "Error creating Red SARA timestamp.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			blob = null;
		} finally {
			try {
				if (baos != null)
					baos.close();
			} catch (Exception e) {
				Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, "Error freeing OutputStream resources.", e);
			}
		}
		
		return blob;
	}

	/**
	 * Do timestamp request.
	 * @param digest Digest for timestamp.
	 * @param policy TimeStamp policy.
	 * @param centro Identifiación del centro que pide el sello de tiempo.
	 * @return ASN1Sequence with TSA response or null in case of error.
	 */
	private static ASN1Sequence doRedSARARequest(byte[] digest, String policy, String centro)	{
		
		try
		{
			TimeStampRequestGenerator reqgen = new TimeStampRequestGenerator();

			String oid = policy;
			String app = centro;

			reqgen.addExtension(oid, false, new DEROctetString(app.getBytes()));
			reqgen.setReqPolicy(oid);

			Socket sslsocket = new Socket("10.128.129.132", 318);

			TimeStampRequest req = reqgen.generate(TSPAlgorithms.SHA1, digest);
			byte[] request = req.getEncoded();

			sslsocket.setSoTimeout(500000);

			DataOutputStream dataoutputstream = new DataOutputStream(sslsocket.getOutputStream());

			OutputStream os = sslsocket.getOutputStream();
			dataoutputstream.writeInt(request.length + 1);
			dataoutputstream.writeByte(0);
			dataoutputstream.write(request);
			dataoutputstream.flush();
			os.flush();
			return getRedSARASequence(sslsocket);
		} catch (ConnectException e) {
			String error = "Error connecting socket.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (SocketException e) {
			String error = "Error setting socket timeout.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (IOException e) {
			String error = "Error doing request at network SARA.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} 
	}

	/**
	 * Get sequence from request.
	 * @param socket Socket connected with red SARA TSA.
	 * @return A sequence with TSA response or null in case of error.
	 */
	private static ASN1Sequence getRedSARASequence(Socket socket){
		
		byte[] abyte2 = (byte[])null;
		ASN1InputStream asn1is = null;
		ASN1Sequence sequence = null;
		
		try
		{
			byte byte0 = 0;
			DataInputStream datainputstream = new DataInputStream(socket.getInputStream());
			int i = datainputstream.readInt();
			byte0 = datainputstream.readByte();

			

			switch (byte0)
			{
			case 5:
				byte[] abyte1 = new byte[i - 1];
				datainputstream.readFully(abyte1);
				asn1is = new ASN1InputStream(abyte1);
				break;
			case 6:
				abyte2 = new byte[i - 1];
				datainputstream.readFully(abyte2);
				asn1is = new ASN1InputStream(abyte2);
				break;
			default:
				abyte2 = new byte[i - 1];
				datainputstream.readFully(abyte2);
				//System.out.println("Salida \n" + new String(abyte2));
			}

			socket.close();
		  		  
			// Obtenemos la respuesta a nuestra peticion, el sello
			//System.out.println("Obtenemos la secuencia.");
			
			DERObject derObject = asn1is.readObject();
			
			sequence = ASN1Sequence.getInstance(derObject);
			
			// Inicio comprobaciones
			DERSequence asn = null;
		 
			asn = (DERSequence)derObject;
    
			DERSequence info = (DERSequence)asn.getObjectAt(0);
			DERInteger status = (DERInteger)info.getObjectAt(0);

			if ((status.getValue().intValue() != 0) && (status.getValue().intValue() != 1))
			{
				String error = "Error validating sequence.";
				Logger.getLogger(TimeStamp.class.getName()).log(Level.INFO, error);
				sequence = null;
			}
			// Fin comprobaciones

		} catch (Exception e) {
			String error = "Error getting sequence.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error,e);
			sequence = null;
		} finally {
			try {
				if (asn1is != null) {
					asn1is.close();
				}
			} catch (IOException e) {
				Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, "Error freeing resources.", e);
			}
		}
		
		return sequence;
	}

	/**
	 * Gets the generation time of XML timestamp.
	 * @param XMLTimeStamp The XML timestamp.
	 * @return The time or null in case of error.
	 */
	public static String getXMLGenTime(BLOB XMLTimeStamp){
		try {
			
			DocumentBuilder documentBuilder;
						
			// Get the length of the blob
			int length = (int) XMLTimeStamp.length();

			byte bytes[] = XMLTimeStamp.getBytes(1, length);
			String tstamp= new String(bytes);

			// 1.- Open the file
			documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = documentBuilder.parse(new ByteArrayInputStream(tstamp.getBytes("UTF-8")));

			// 2.- Extract genTime
			NodeList nodeList = doc.getElementsByTagName("genTime");
				
			// Only one node with genTime
			Node node = nodeList.item(0);
				
			//System.out.println(nodo.getTextContent());
			
			return node.getTextContent();
			
		} catch (ParserConfigurationException e) {
			String error = "Error creating DocumentBuilder.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (SAXException e) {
			String error = "Error pharsing document.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (IOException e) {
			String error = "Error closing certificate InputStream.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (SQLException e) {
			String error = "Error getting timeStamp.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} 
	}
	
	/**
	 * Gets the serial number of XML timestamp.
	 * @param XMLTimeStamp The XML timestamp.
	 * @return The serial number or null in case of error.
	 */
	public static String getXMLSerialNumber(BLOB XMLTimeStamp){
		try {
			
			DocumentBuilder documentBuilder;
						
			// Get the length of the blob
			int length = (int) XMLTimeStamp.length();

			byte bytes[] = XMLTimeStamp.getBytes(1, length);
			String tstamp= new String(bytes);

			// 1.- Open the file
			documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = documentBuilder.parse(new ByteArrayInputStream(tstamp.getBytes("UTF-8")));

			// 2.- Extract genTime
			NodeList nodeList = doc.getElementsByTagName("serialNumber");
				
			// Only one node with genTime
			Node node = nodeList.item(0);
				
			//System.out.println(nodo.getTextContent());
			
			return node.getTextContent();
			
		} catch (ParserConfigurationException e) {
			String error = "Error creating DocumentBuilder.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (SAXException e) {
			String error = "Error pharsing document.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (IOException e) {
			String error = "Error closing certificate InputStream.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (SQLException e) {
			String error = "Error getting timeStamp.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} 
	}
	
	/**
	 * Gets the generation time of timestamp.
	 * @param timeStamp The timestamp.
	 * @return The time or null in case of error.
	 */
	public static String getGenTime(BLOB timeStamp){
		try {
			
			InputStream is = timeStamp.getBinaryStream();
			
			TimeStampToken timeStampToken = new TimeStampToken(new CMSSignedData(Stream.toByteArray(is))); 
			
			is.close();
			
			return timeStampToken.getTimeStampInfo().getGenTime().toString();
			
		} catch (IOException e) {
			String error = "Error closing certificate InputStream.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (SQLException e) {
			String error = "Error getting timeStamp.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (TSPException e) {
			String error = "Error creating CMSSignedData.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (CMSException e) {
			String error = "Error creating TimeStampToken.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			return null;
		}
	}
	
	/**
	 * Gets the serial number of timestamp.
	 * @param timeStamp The timestamp.
	 * @return The serial number or null in case of error.
	 */
	public static String getSerialNumber(BLOB timeStamp){
		try {
			
			InputStream is =  timeStamp.getBinaryStream();
			
			TimeStampToken timeStampToken = new TimeStampToken(new CMSSignedData(Stream.toByteArray(is))); 
			
			is.close();
			
			return timeStampToken.getTimeStampInfo().getSerialNumber().toString();
			
		} catch (IOException e) {
			String error = "Error closing certificate InputStream.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (SQLException e) {
			String error = "Error getting timeStamp.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (TSPException e) {
			String error = "Error creating CMSSignedData.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (CMSException e) {
			String error = "Error creating TimeStampToken.";
			Logger.getLogger(TimeStamp.class.getName()).log(Level.SEVERE, error, e);
			return null;
		}
	}
}
