package es.redmoon.pl.certificates;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import oracle.jdbc.OraclePreparedStatement;
import oracle.sql.BLOB;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.AccessDescription;
import org.bouncycastle.asn1.x509.AuthorityInformationAccess;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.ocsp.BasicOCSPResp;
import org.bouncycastle.ocsp.CertificateID;
import org.bouncycastle.ocsp.CertificateStatus;
import org.bouncycastle.ocsp.OCSPException;
import org.bouncycastle.ocsp.OCSPReq;
import org.bouncycastle.ocsp.OCSPReqGenerator;
import org.bouncycastle.ocsp.OCSPResp;
import org.bouncycastle.ocsp.SingleResp;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import es.redmoon.utils.Base64;
import es.redmoon.utils.XML;

/**
 * Class with utils to certificates.
 * @version 0.1
 */
public class Certificate {

	private final static String AUTHORITY_INFORMATION_ACCESS = "1.3.6.1.5.5.7.1.1";
	private final static DERObjectIdentifier OSCP = new DERObjectIdentifier("1.3.6.1.5.5.7.48.1"); // Identification for OCSP Basic access	
	
	/**
	 * Check certificate's current status.
	 * @param certificate Certificate to validate.
	 * @return	1: If certificate's serial number is valid.
	 * 			2: If certificate's serial number is unknown.
	 * 			3: If certificate's serial number is revocated.
	 * 			0: If Certification Authority hasn't signed the certificate. The certificate isn't trusted.
	 * 			-1: If Certification Authority isn't trusted by eDocPKI.
	 * 			-2: If some error has happend validating certificate.
	 * 			-3: If certifacate has expired.
	 * 			-4: If certificate isn't yet valid.
	 * 			-5: If OCSP URL isn't valid.
	 * 			-6: If some error has happened parsing certificate or Certification Authority certificate.
	 * 			-7: If some error has happened with Input/Output.
	 * 			-8: If some error has happened with data base.
	 * 			-9: If some error has happened with OCSP request.
	 * 			-99: If checking method isn't supported (CRL).
	 * 			
	 */
	public static int validate(X509Certificate certificate){

		String nombreCanonicoEmisor = null;
		BigInteger nSerie = null;

		long caId = 0;
		BLOB blobCertificadoAC = null;
		String metodoBD = null;
		String urlOcspBD = null;
		String urlOcspCert = null;
		String urlOCSP = null;
		
		ASN1InputStream asn1IS = null;
		ByteArrayInputStream baIS = null;
		
		Connection conn = null;
		OraclePreparedStatement stmt = null;
		ResultSet rset = null;

		try {
			// Se carga el proveedor necesario para la peticion OCSP
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
						
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			
			// Obtenemos los datos del certificado
			nombreCanonicoEmisor = certificate.getIssuerX500Principal().getName("CANONICAL");
			nSerie = certificate.getSerialNumber();

			// Obtenemos la información de acceso a la autoridad: OSCP, CRL
			byte[] value = certificate.getExtensionValue(AUTHORITY_INFORMATION_ACCESS);

			if (value != null){
				AuthorityInformationAccess authorityInformationAccess;

				baIS = new ByteArrayInputStream(value);
				asn1IS = new ASN1InputStream(baIS);
				
				//DEROctetString oct = (DEROctetString) (new ASN1InputStream(new ByteArrayInputStream(value)).readObject());
				DEROctetString oct = (DEROctetString) asn1IS.readObject();
				
				asn1IS.close();
				
				asn1IS = new ASN1InputStream(oct.getOctets());
				
				//authorityInformationAccess = new AuthorityInformationAccess(ASN1Sequence.getInstance((new ASN1InputStream(oct.getOctets())).readObject()) );
				authorityInformationAccess = new AuthorityInformationAccess(ASN1Sequence.getInstance(asn1IS.readObject()) );
				
				AccessDescription[] accessDescriptions = authorityInformationAccess.getAccessDescriptions();
				for (AccessDescription accessDescription : accessDescriptions) {
					DERObjectIdentifier metodo = accessDescription.getAccessMethod();

					if (metodo.equals(OSCP)) {
						GeneralName gn = accessDescription.getAccessLocation();
						DERIA5String str = DERIA5String.getInstance(gn.getDERObject());
						urlOcspCert = str.getString();
					}
					// Se deberian comprobar los demas metodos

				}
			} else {
				// No tiene esta extension en la que se indica la forma de acceso a la autoridad certificadora
				urlOcspCert = null;
			}

			/**
			 * Comprobamos la validez del certificado
			 * 1.- Comprobamos que el certificado es auténtico. Está firmado por la CA por la que dice estar firmado, la que ha emitido el certificado.
			 * 2.- Comprobamos las fechas.
			 * 3.- Comprobamos que el número de série es válido.
			 * 	3.1.- OCSP.
			 * 	3.2.- CRL.
			 */

			// Vemos cual es la autoridad certificadora
			X509Certificate certCA = null;
			
			//Class.forName("oracle.jdbc.driver.OracleDriver");
			//conn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.2.150:1521:orcl", "edocpki", "redmoon");

			// Seleccionamos los datos de la AC de la Base de Datos
			conn = DriverManager.getConnection("jdbc:default:connection");
			
			stmt = (OraclePreparedStatement) conn.prepareStatement("SELECT id, certificate, method, ocsp_url FROM certification_authorities WHERE canonical_name=?");

			stmt.setString(1, nombreCanonicoEmisor);

			rset = stmt.executeQuery();

			if (!rset.next())
				return -1;

			caId = rset.getLong(1);
			blobCertificadoAC = (BLOB) rset.getBlob(2);
			metodoBD = rset.getString(3);
			urlOcspBD = rset.getString(4);

			certCA = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(blobCertificadoAC.getBytes(1, (int)blobCertificadoAC.length())));

			// 1.- autenticidad del certificado
			certificate.verify(certCA.getPublicKey());

			// 2.- Comprobamos las fechas
			certificate.checkValidity();

			// 3.- Comprobamos el número de serie

			if (metodoBD.equals("OCSP")) {
				//3.1 OCSP

				// Habría que comprobar que el certificado de la CA es correcto revisando la cadena de certificación
				//No, por que suponemos que los certificados de nuestra BD son correctos, es tarea del administrador comprobarlo. 

				OCSPReqGenerator ocspReqGen = new OCSPReqGenerator();
				CertificateID certid = new CertificateID(CertificateID.HASH_SHA1, certCA, nSerie);
				ocspReqGen.addRequest(certid);

				// Generamos la peticion OCSP
				OCSPReq ocspReq = ocspReqGen.generate();

				// Vemos que URL coger
				if (urlOcspCert != null)
					urlOCSP = urlOcspCert;
				else if (urlOcspBD != null)
					urlOCSP = urlOcspBD;
				else
					return -5;

				// Se establece la conexion HTTP con el ocsp del DNIe
				URL url = new URL(urlOCSP);
				HttpURLConnection con = (HttpURLConnection) url.openConnection();

				// Se configuran las propiedades de la peticion HTTP
				con.setRequestProperty("Content-Type", "application/ocsp-request");
				con.setRequestProperty("Accept", "application/ocsp-response");
				con.setDoOutput(true);
				OutputStream out = con.getOutputStream();
				DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(out));

				// Enviamos la peticion
				dataOut.write(ocspReq.getEncoded());
				dataOut.flush();
				dataOut.close();

				// Se parsea la respuesta y se obtiene el estado del certificado
				// retornado por el OCSP
				InputStream in = con.getInputStream();
				BasicOCSPResp ocspResponse = (BasicOCSPResp) new OCSPResp(in).getResponseObject();

				// Comprobar si la respuesta es successfull
				for (SingleResp singResp : ocspResponse.getResponses()) {
					Object status = singResp.getCertStatus();

					if (status instanceof org.bouncycastle.ocsp.UnknownStatus) {
						return 2;
					} else if (status instanceof org.bouncycastle.ocsp.RevokedStatus) {
						return 3;
					} else if (status == CertificateStatus.GOOD){
						return 1;
					}
				}

				// Si se ha llegado hasta aqui el estado es desconocido
				return 2;
			} else if (metodoBD.equals("CRL")){
				// 3.2 CRL
				
				// Buscamos en la tabla de CRL si existe el numero de serie del certificado.
				stmt = (OraclePreparedStatement) conn.prepareStatement("SELECT id FROM crl WHERE id_ca=? AND serial_number=?");

				stmt.setLong(1, caId);
				stmt.setString(2, nSerie.toString()); // ¿Hexadecimal en vez de decimal?

				rset = stmt.executeQuery();

				if (!rset.next()) {
					// NO está revocado, no aparece en la lista de CRL.
					return 1;
				} else {
					// SI está revocado.
					return 3;
				}
				
			} else {
				// 3.3 OTHER
				System.out.println("OTHER");
				return -99; // Metodo aún no soportado
			}

		} catch (InvalidKeyException e) {
			Logger.getLogger(Certificate.class.getName()).log(Level.SEVERE, "Problem validating certificate.", e);
			return -2;
		} catch (CertificateExpiredException e) {
			Logger.getLogger(Certificate.class.getName()).log(Level.SEVERE, "Certificate has expired.", e);
			return -3;
		} catch (CertificateNotYetValidException e) {
			Logger.getLogger(Certificate.class.getName()).log(Level.SEVERE, "Certificate not yet valid.", e);
			return -4;
		} catch (CertificateException e) {
			Logger.getLogger(Certificate.class.getName()).log(Level.SEVERE, "Certificate error.", e);
			return -6;
		} catch (NoSuchAlgorithmException e) {
			Logger.getLogger(Certificate.class.getName()).log(Level.SEVERE, "Algorithm not available.", e);
			return -2;
		} catch (NoSuchProviderException e) {
			Logger.getLogger(Certificate.class.getName()).log(Level.SEVERE, "Provider not available.", e);
			return -2;
		} catch (SignatureException e) {
			Logger.getLogger(Certificate.class.getName()).log(Level.SEVERE, "Certification Authority hasn't issued the certificate.", e);
			return 0;
		} catch (MalformedURLException e) {
			Logger.getLogger(Certificate.class.getName()).log(Level.SEVERE, "OSCP URL is invalid.", e);
			return -5;
		} catch (IOException e) {
			Logger.getLogger(Certificate.class.getName()).log(Level.SEVERE, "Input - Output error.", e);
			return -7;
		} catch (SQLException e) {
			Logger.getLogger(Certificate.class.getName()).log(Level.SEVERE, "Data base error.", e);
			return -8;
		} catch (OCSPException e) {
			Logger.getLogger(Certificate.class.getName()).log(Level.SEVERE, "OCSP request error.", e);
			return -9;
		} /*catch (ClassNotFoundException e) {
			Logger.getLogger(Certificate.class.getName()).log(Level.SEVERE, "Error getting JDBC driver.", e);
			return -8;
		}*/ finally {
			if (baIS != null) {
				try {
					baIS.close();
				} catch (IOException e) {
					Logger.getLogger(Certificate.class.getName()).log(Level.SEVERE, "Error freeing resources.", e);
				}
			}
			if (asn1IS != null) {
				try {
					asn1IS.close();
				} catch (IOException e) {
					Logger.getLogger(Certificate.class.getName()).log(Level.SEVERE, "Error freeing resources.", e);
				}
			}
			if (rset != null) {
				try {
					rset.close();
				} catch (SQLException e) {
					Logger.getLogger(Certificate.class.getName()).log(Level.SEVERE, "Error freeing resources.", e);
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					Logger.getLogger(Certificate.class.getName()).log(Level.SEVERE, "Error freeing resources.", e);
				}
			}			
		}
	}
	
	/**
	 * Check certificate's current status.
	 * @param blobCertificate BLOB containing certificate to validate.
	 * @return	1: If certificate's serial number is valid.
	 * 			2: If certificate's serial number is unknown.
	 * 			3: If certificate's serial number is revocated.
	 * 			0: If Certification Authority hasn't signed the certificate. The certificate isn't trusted.
	 * 			-1: If Certification Authority isn't trusted by eDocPKI.
	 * 			-2: If some error has happend validating certificate.
	 * 			-3: If certifacate has expired.
	 * 			-4: If certificate isn't yet valid.
	 * 			-5: If OCSP URL isn't valid.
	 * 			-6: If some error has happened parsing certificate or Certification Authority certificate.
	 * 			-7: If some error has happened with Input/Output.
	 * 			-8: If some error has happened with data base.
	 * 			-9: If some error has happened with OCSP request.
	 * 			-99: If checking method isn't supported (CRL).
	 * 			
	 */
	public static int validate(BLOB blobCertificate){

		try {
			
			// Obtenemos un certificado del BLOB
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			X509Certificate certificado = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(blobCertificate.getBytes(1, (int)blobCertificate.length())));
			
			return validate(certificado);

		} catch (CertificateException e) {
			Logger.getLogger(Certificate.class.getName()).log(Level.SEVERE, "Certificate error.", e);
			return -6;
		} catch (SQLException e) {
			Logger.getLogger(Certificate.class.getName()).log(Level.SEVERE, "Data base error.", e);
			return -8;
		}
	}

	/**
	 * Check XAdES signature certificate's current status.
	 * @param xadesSignature BLOB containing XAdES signature.
	 * @return	1: If certificate's serial number is valid.
	 * 			2: If certificate's serial number is unknown.
	 * 			3: If certificate's serial number is revocated.
	 * 			0: If Certification Authority hasn't signed the certificate. The certificate isn't trusted.
	 * 			-1: If Certification Authority isn't trusted by eDocPKI.
	 * 			-2: If some error has happend validating certificate.
	 * 			-3: If certifacate has expired.
	 * 			-4: If certificate isn't yet valid.
	 * 			-5: If OCSP URL isn't valid.
	 * 			-6: If some error has happened parsing certificate or Certification Authority certificate.
	 * 			-7: If some error has happened with Input/Output.
	 * 			-8: If some error has happened with data base.
	 * 			-9: If some error has happened with OCSP request.
	 * 			-99: If checking method isn't supported (CRL).
	 * 			
	 */
	public static int validateXAdESSignature (BLOB xadesSignature){

		// Convertimos el BLOB en Document para poder procesarlo
		Document firma = XML.getDocumentFromBLOB(xadesSignature); 

		try {
			if (firma != null) {
				// Sacamos el certificado
				NodeList listaNodos = firma.getElementsByTagName("ds:X509Certificate");

				// Si no hay certificados
				if ( listaNodos.getLength() == 0)
					return -2;

				// El primer nodo es el del certificado de firma, los demas son de las AC
				Node nodo = listaNodos.item(0);

				// Decodificamos el certificado de base64
				byte[] bytesCert = Base64.decode(nodo.getTextContent());

				// Obtenemos un certificado de los bytes
				InputStream is = new ByteArrayInputStream(bytesCert);
				CertificateFactory cf = CertificateFactory.getInstance("X.509");
				X509Certificate certificado = (X509Certificate) cf.generateCertificate(is);
				is.close();
				
				return validate(certificado);
			} else {
				return -10;
			}

		} catch (DOMException e) {
			Logger.getLogger(Certificate.class.getName()).log(Level.SEVERE, "XML format incorrect.", e);
			return -11;
		} catch (CertificateException e) {
			Logger.getLogger(Certificate.class.getName()).log(Level.SEVERE, "Certificate error.", e);
			return -6;
		} catch (IOException e) {
			Logger.getLogger(Certificate.class.getName()).log(Level.SEVERE, "Input - Output error.", e);
			return -7;
		}
	}
	
	/**
	 * Gets NIF, CIF or NIE from XAdES Signature.
	 * @param xadesSignature XAdES Signature.
	 * @return NIF, CIF or NIE from XAdES or ERROR: + String in case of error.
	 */
	public static String getIDXAdESSignature(BLOB xadesSignature){
		
		Document docEntrada = null;
		
		if (xadesSignature == null)
			return "ERROR: Parameter can not be null.";
		
		// 1.- Creamos el documento de entrada.
		docEntrada = XML.getDocumentFromBLOB(xadesSignature);
		
		if (docEntrada == null) {
			return "ERROR: Can not parse XAdES Signature.";
		}
		
		try {
			
			// 2.- Sacamos el certificado del documento de entrada			
			byte[] bytesCertificado = null; 
			
			try {
				// Obtenemos los nodos con el certificado del firmante
				NodeList listaNodos = docEntrada.getElementsByTagName("ds:X509Certificate");
				// Obtenemos el primer nodo, si hay más de uno da igual por que todos van a tener el mismo certificado.
				Node nodo = listaNodos.item(0);
				
				// Decodificamos el certificado que está en base64
				bytesCertificado = Base64.decode(nodo.getTextContent());
			} catch (DOMException e) {
				String error = "ERROR: XML format incorrect.";
				Logger.getLogger(Certificate.class.getName()).log(Level.SEVERE, error, e);
				return  error + "\n" + e.getMessage();
			} catch (IOException e) {
				String error = "ERROR: Input - Output error.";
				Logger.getLogger(Certificate.class.getName()).log(Level.SEVERE, error, e);
				return  error + "\n" + e.getMessage();
			}  catch (Exception e) {
				String error = "ERROR: Can not extract certificate.";
				Logger.getLogger(Certificate.class.getName()).log(Level.SEVERE, error, e);
				return  error + "\n" + e.getMessage();
			}

			// Obtenemos un certificado de los bytes
			InputStream is = new ByteArrayInputStream(bytesCertificado);
			CertificateFactory cf;
			X509Certificate certificadoX509 = null;
			try {
				cf = CertificateFactory.getInstance("X.509");
				certificadoX509 = (X509Certificate) cf.generateCertificate(is);
				
				is.close();
				
				certificadoX509.checkValidity();
			} catch (IOException e) {
				String error = "ERROR: Fallo al cerrar el InputStream.";
				Logger.getLogger(Certificate.class.getName()).log(Level.SEVERE, error, e);
				return error + "\n" + e.getMessage();
			} catch (CertificateExpiredException e) {
				String error = "ERROR: Fallo al comprobar la validez del certificado.";
				Logger.getLogger(Certificate.class.getName()).log(Level.SEVERE, error, e);
				return error + "\n" + e.getMessage();
			} catch (CertificateNotYetValidException e) {
				String error = "ERROR: Fallo al comprobar la validez del certificado.";
				Logger.getLogger(Certificate.class.getName()).log(Level.SEVERE, error, e);
				return error + "\n" + e.getMessage();
			} catch (CertificateException e) {
				String error = "ERROR: Fallo al obtener el certificado del array de bytes.";
				Logger.getLogger(Certificate.class.getName()).log(Level.SEVERE, error, e);
				return error + "\n" + e.getMessage();
			}  catch (Exception e) {
				String error = "ERROR: Fallo al tratar el certificado.";
				Logger.getLogger(Certificate.class.getName()).log(Level.SEVERE, error, e);
				return error + "\n" + e.getMessage();
			}
			

			// 3.- Extract user identification from certificate
			String subject = certificadoX509.getSubjectDN().toString();
			
			// Hay varios tipos de usuarios
			// Españoles:
				// 12345678Letra
			// Extranjeros:
				// M1234567Letra
				// L1234567Letra
				// X1234567Letra
				// Y1234567Letra
				// Z1234567Letra
			// CIF:
				// A12345678. Sociedades anónimas.
				// B12345678. Sociedades de responsabilidad limitada.
				// C12345678. Sociedades colectivas.
				// D12345678. Sociedades comanditarias.
				// E12345678. Comunidades de bienes.
				// F12345678. Sociedades cooperativas.
				// G12345678. Asociaciones y otros tipos de sociedades civiles.
				// H12345678. Comunidades de propietarios en régimen de propiedad horizontal.
				// J12345678. Sociedades civiles, con o sin personalidad jurídica.
				// K12345678. Formato antiguo.
				// L12345678. Formato antiguo.
				// M12345678. Formato antiguo.
				// N12345678. Entidades no residentes.
				// P12345678. Corporaciones locales.
				// Q12345678. Organismos autónomos, estatales o no, y asimilados, y congregaciones e instituciones religiosas.
				// R12345678. Congregaciones e instituciones religiosas.
				// S12345678. Órganos de la Administración del Estado y comunidades autónomas
				// U12345678. Uniones Temporales de Empresas.
				// V12345678. Otros tipos no definidos en el resto de claves.
				// W12345678. Reservado a establecimientos permanentes de entidades no residentes en territorio español.				
			
			String identificador = null;
			
			
			// Primero discriminamos por CIF, para evitar que tome el NIF de la persona fisica
			Pattern patron = Pattern.compile("((A|a|B|b|C|c|D|d|E|e|F|f|G|g|H|h|J|j|K|k|L|l|M|m|N|n|P|p|Q|q|R|r|S|s|U|u|V|v|W|w)(\\d){8})");
			boolean cumplePatron = patron.matcher(subject).find();
			
			if (cumplePatron){
				
				Matcher matcher = patron.matcher(subject);

				// Hace que Matcher busque los trozos.
				matcher.find();

				identificador = matcher.group(1);

			}
			else {
				// Vemos si es español
				patron = Pattern.compile("((\\d){8}[A-Za-z])");
				cumplePatron = patron.matcher(subject).find();
				
				if (cumplePatron){
					
					Matcher matcher = patron.matcher(subject);

					// Hace que Matcher busque los trozos.
					matcher.find();

					identificador = matcher.group(1);

				}
				else {
					// Si no es español ni empresa comprobamos si es extrangero
					patron = Pattern.compile("((M|m|L|l|X|x|Y|y|Z|z)(\\d){7}[A-Za-z])");
					cumplePatron = patron.matcher(subject).find();
					
					if (cumplePatron){
						
						Matcher matcher = patron.matcher(subject);

						// Hace que Matcher busque los trozos.
						matcher.find();

						identificador = matcher.group(1);

					}
					else {						
						// Si no es ni español ni extrangero ni empresa el identificador es null
						identificador = null;
					}
				}
			}
			
			if (identificador != null){
				return identificador;	
			}
			else{
				// No se ha podido obtener el identificador del usuario
				return "ERROR: No se ha podido obtener el identificador del usuario.";
			}
			
		} catch (Exception e) {
			String error = "ERROR: Fallo al validar el usuario.";
			Logger.getLogger(Certificate.class.getName()).log(Level.SEVERE, error, e);
			return error + "\n" + e.toString();
		}
	}
	
	
	/**
	 * Gets NIF, CIF or NIE from certificate.
	 * @param certificate Certificate.
	 * @return NIF, CIF or NIE from XAdES or ERROR: + String in case of error.
	 */
	public static String getIDFromCertificate(BLOB certificate){
		
		try {
						
			CertificateFactory cf;
			X509Certificate certificadoX509 = null;
			
			// 1.- Sacamos el certificado del documento de entrada	
			try {
				cf = CertificateFactory.getInstance("X.509");
				//certificadoX509 = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(certificate.getBytes(1, (int)certificate.length())));
				certificadoX509 = (X509Certificate) cf.generateCertificate(certificate.getBinaryStream());
				
				certificadoX509.checkValidity();
			} catch (CertificateExpiredException e) {
				String error = "ERROR: Fallo al comprobar la validez del certificado.";
				Logger.getLogger(Certificate.class.getName()).log(Level.SEVERE, error, e);
				return error + "\n" + e.getMessage();
			} catch (CertificateNotYetValidException e) {
				String error = "ERROR: Fallo al comprobar la validez del certificado.";
				Logger.getLogger(Certificate.class.getName()).log(Level.SEVERE, error, e);
				return error + "\n" + e.getMessage();
			} catch (CertificateException e) {
				String error = "ERROR: Fallo al obtener el certificado del array de bytes.";
				Logger.getLogger(Certificate.class.getName()).log(Level.SEVERE, error, e);
				return error + "\n" + e.getMessage();
			}  catch (Exception e) {
				String error = "ERROR: Fallo al tratar el certificado.";
				Logger.getLogger(Certificate.class.getName()).log(Level.SEVERE, error, e);
				return error + "\n" + e.getMessage();
			}

			// 2.- Extract user identification from certificate
			String subject = certificadoX509.getSubjectDN().toString();
			
			// Hay varios tipos de usuarios
			// Españoles:
				// 12345678Letra
			// Extranjeros:
				// M1234567Letra
				// L1234567Letra
				// X1234567Letra
				// Y1234567Letra
				// Z1234567Letra
			// CIF:
				// A12345678. Sociedades anónimas.
				// B12345678. Sociedades de responsabilidad limitada.
				// C12345678. Sociedades colectivas.
				// D12345678. Sociedades comanditarias.
				// E12345678. Comunidades de bienes.
				// F12345678. Sociedades cooperativas.
				// G12345678. Asociaciones y otros tipos de sociedades civiles.
				// H12345678. Comunidades de propietarios en régimen de propiedad horizontal.
				// J12345678. Sociedades civiles, con o sin personalidad jurídica.
				// K12345678. Formato antiguo.
				// L12345678. Formato antiguo.
				// M12345678. Formato antiguo.
				// N12345678. Entidades no residentes.
				// P12345678. Corporaciones locales.
				// Q12345678. Organismos autónomos, estatales o no, y asimilados, y congregaciones e instituciones religiosas.
				// R12345678. Congregaciones e instituciones religiosas.
				// S12345678. Órganos de la Administración del Estado y comunidades autónomas
				// U12345678. Uniones Temporales de Empresas.
				// V12345678. Otros tipos no definidos en el resto de claves.
				// W12345678. Reservado a establecimientos permanentes de entidades no residentes en territorio español.				
			
			String identificador = null;
			
			
			// Primero discriminamos por CIF, para evitar que tome el NIF de la persona fisica
			Pattern patron = Pattern.compile("((A|a|B|b|C|c|D|d|E|e|F|f|G|g|H|h|J|j|K|k|L|l|M|m|N|n|P|p|Q|q|R|r|S|s|U|u|V|v|W|w)(\\d){8})");
			boolean cumplePatron = patron.matcher(subject).find();
			
			if (cumplePatron){
				
				Matcher matcher = patron.matcher(subject);

				// Hace que Matcher busque los trozos.
				matcher.find();

				identificador = matcher.group(1);

			}
			else {
				// Vemos si es español
				patron = Pattern.compile("((\\d){8}[A-Za-z])");
				cumplePatron = patron.matcher(subject).find();
				
				if (cumplePatron){
					
					Matcher matcher = patron.matcher(subject);

					// Hace que Matcher busque los trozos.
					matcher.find();

					identificador = matcher.group(1);

				}
				else {
					// Si no es español ni empresa comprobamos si es extrangero
					patron = Pattern.compile("((M|m|L|l|X|x|Y|y|Z|z)(\\d){7}[A-Za-z])");
					cumplePatron = patron.matcher(subject).find();
					
					if (cumplePatron){
						
						Matcher matcher = patron.matcher(subject);

						// Hace que Matcher busque los trozos.
						matcher.find();

						identificador = matcher.group(1);

					}
					else {						
						// Si no es ni español ni extrangero ni empresa el identificador es null
						identificador = null;
					}
				}
			}
			
			if (identificador != null){
				return identificador;	
			}
			else{
				// No se ha podido obtener el identificador del usuario
				return "ERROR: No se ha podido obtener el identificador del usuario.";
			}
			
		} catch (Exception e) {
			String error = "ERROR: Fallo al validar el usuario.";
			Logger.getLogger(Certificate.class.getName()).log(Level.SEVERE, error, e);
			return error + "\n" + e.toString();
		}
	}
}
