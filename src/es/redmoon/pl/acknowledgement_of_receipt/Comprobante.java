package es.redmoon.pl.acknowledgement_of_receipt;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfSignatureAppearance;
import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

import oracle.jdbc.OraclePreparedStatement;
import oracle.sql.BLOB;

Obtener los datos del sello de tiempo, asi no es necesario rellenarlos en la tabla de la TSA y es más facil para el usuario.
¿Es necesario el logo? Si no es necesario se necesitan menos parametros y creo que es mas comodo para el usuario del panel,
no tiene que estar buscando los logos. 
		
/**
 * Clase para gestionar los comprobantes de las notificaciones.
 * @version 0.1
 *
 */
public class Comprobante {

	private static final Font FUENTE_ENCABEZADO = FontFactory.getFont(FontFactory.HELVETICA, 13, Font.UNDERLINE, Color.BLACK);
	private static final Font FUENTE_TITULO = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD, Color.BLACK);
	private static final Font FUENTE_TEXTO = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.NORMAL, Color.BLACK);
	private static final Font FUENTE_PIE = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.NORMAL, Color.BLACK);
	
	private static final String ENCABEZADO = "ACUSE DE RECIBO";
	private static final String PIE = "Este documento es la prueba fehaciente de que la comunicación ha existido y de que los datos no han sido alterados desde la fecha incluida en el texto.";
	

	/**
	 * Método que devuelve un BLOB con un PDF con el comprobante de lectura de una notificación ordinaria.
	 * @param connJava Conexión a la base de datos del usuario Java_jars.
	 * @param connRegistro Conexión a la base de datos del usuario Registro.
	 * @param idCert Identificador del certificado a usar para firmar el acuse.
	 * @param idPlantilla Plantilla de comprobante a utilizar. 
	 * @param idLogo Logo de la institucion ha utilizar en el comprobante. 
	 * @param idAcuse Acuse de lectura a mostrar.
	 * @return PDF con el comprobante.
	 */
	public static BLOB obtenerAcuseLecturaOrdinario(Connection connJava, Connection connRegistro, int idCert, int idPlantilla, int idLogo, int idAcuse) {

		try {
			// Seleccionamos los datos de la Base de Datos
			OraclePreparedStatement stmt = (OraclePreparedStatement) connJava.prepareStatement("SELECT p.plantilla, c.certificado, c.passwd, l.imagen FROM plantillas p, certificados c, logos l WHERE p.id=? AND c.id=? AND l.id=?");
			
			stmt.setInt(1, idPlantilla);
			stmt.setInt(2, idCert);
			stmt.setInt(3, idLogo);

			ResultSet rset = stmt.executeQuery();

			if (!rset.next()){
				throw new SQLException("No se han encontrado datos para la consulta.");
			}

			oracle.sql.BLOB blob = (BLOB) rset.getBlob(1);
			oracle.sql.BLOB blobAlmacen = (BLOB) rset.getBlob(2);
			String clave = rset.getString(3);
			oracle.sql.BLOB blobLogo = (BLOB) rset.getBlob(4);
			
			// Obtenemos la longitud del BLOB con el PDF
			int length = (int) blob.length();
			byte bytesPDF[] = blob.getBytes(1, length);
			
			// Obtenemos la longitud del BLOB con el logo
			length = (int) blobLogo.length();
			byte bytesLogo[] = blobLogo.getBytes(1, length);
				
			stmt = (OraclePreparedStatement) connRegistro.prepareStatement("SELECT n.asunto, s.fecha_lectura, s.ip FROM notificaciones n, sin_acuses s WHERE n.id=s.id_notificacion AND s.id=?");
			stmt.setInt(1, idAcuse);

			rset = stmt.executeQuery();

			if (!rset.next()){
				throw new SQLException("No se han encontrado datos para la consulta.");
			}

			String asunto = rset.getString(1);
			String fechaLectura = rset.getString(2);
			String ip = rset.getString(3);

			// Leemos el pdf original
			PdfReader reader = new PdfReader(bytesPDF);

			// Creamos el blob que contendra el comprobante
			BLOB blobComprobante = BLOB.createTemporary(connRegistro, true,	BLOB.DURATION_SESSION);
			OutputStream outStream = blobComprobante.setBinaryStream(1L);

			// Preparamos el pdf de salida para escribir en el
			PdfStamper stamper = PdfStamper.createSignature(reader, outStream, '\0', null, true);
			PdfContentByte canvas = stamper.getOverContent(1);

			ColumnText columna = new ColumnText(canvas);

			// Ponemos el logo de la institución
			Image logo = Image.getInstance(bytesLogo);
			logo.setAbsolutePosition(90, 620);
			canvas.addImage(logo);
			
			// Empezamos con el encabezado
			Chunk trozo = new Chunk();
			trozo.append(ENCABEZADO);
			trozo.setFont(FUENTE_ENCABEZADO);
			Phrase frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 245, 600, 400, 600, 0, Element.ALIGN_LEFT);
			columna.go();  
			
			// Seguimos con la referencia
			trozo = new Chunk();
			trozo.append("Referencia: ");
			trozo.setFont(FUENTE_TITULO);
			frase = new Phrase(trozo);

			trozo = new Chunk("" + idAcuse);
			trozo.setFont(FUENTE_TEXTO);
			frase.add(trozo);

			columna.setSimpleColumn(frase, 100, 550, 300, 550, 0, Element.ALIGN_LEFT);
			columna.go();
				
			// Seguimos con el asunto
			trozo = new Chunk();
			trozo.append("Asunto");
			trozo.setFont(FUENTE_TITULO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 0, 520, 612, 520, 0, Element.ALIGN_CENTER);
			columna.go();
				
			trozo = new Chunk();
			trozo.append(asunto);			
			trozo.setFont(FUENTE_TEXTO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 100, 450, 508, 510, 13, Element.ALIGN_CENTER);
			columna.go();
				
			// Seguimos los datos del lector
			trozo = new Chunk();
			trozo.append("Dirección IP desde la que se ha leído:");
			trozo.setFont(FUENTE_TITULO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 100, 425, 512, 425, 0, Element.ALIGN_LEFT);
			columna.go();
				
			trozo = new Chunk();
			trozo.append(ip);
			trozo.setFont(FUENTE_TEXTO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 100, 400, 512, 400, 0, Element.ALIGN_LEFT);
			columna.go();

			// Seguimos con la fecha y hora de la lectura
			trozo = new Chunk();
			trozo.append("Fecha y hora de lectura:");
			trozo.setFont(FUENTE_TITULO);
			frase = new Phrase(trozo);
				
			columna.setSimpleColumn(frase, 100, 360, 512, 360, 0, Element.ALIGN_LEFT);
			columna.go();
							
			trozo = new Chunk();
			trozo.append(fechaLectura);
			trozo.setFont(FUENTE_TEXTO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 100, 335, 512, 335, 0, Element.ALIGN_LEFT);
			columna.go();			
			
			// Seguimos con el pie
			trozo = new Chunk();
			trozo.append(PIE);
			trozo.setFont(FUENTE_PIE);
			frase = new Phrase(trozo);
							
			columna.setSimpleColumn(frase, 80, 100, 515, 80, 10, Element.ALIGN_LEFT);
			columna.go();
			
			// Firmamos el acuse
			
			// Obtenemos el almacen
			byte[] almacen = blobAlmacen.getBytes(1, (int) blobAlmacen.length());
			KeyStore ks = KeyStore.getInstance("PKCS12");			
			ks.load(new ByteArrayInputStream(almacen), clave.toCharArray());
			
			// Obtenemos la clave privada
			String alias = (String)ks.aliases().nextElement();
			PrivateKey key = (PrivateKey)ks.getKey(alias, clave.toCharArray());
			Certificate[] chain = ks.getCertificateChain(alias);
			
			// Firmamos
			PdfSignatureAppearance sap = stamper.getSignatureAppearance();
			sap.setCrypto(key, chain, null, PdfSignatureAppearance.WINCER_SIGNED);
			// comment next line to have an invisible signature
			sap.setVisibleSignature(new Rectangle(395, 535, 505, 630), 1, null);
				
			stamper.close();
			outStream.close();

			return blobComprobante;
		} catch (SQLException e) {
			String error = "Fallo al operar con la base de datos.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (IOException e) {
			String error = "Fallo al operar con los PDF's.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (DocumentException e) {
			String error = "Fallo al operar con el PDF del comprobante.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (KeyStoreException e) {
			String error = "Fallo al obtener el almacén.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (NoSuchAlgorithmException e) {
			String error = "Fallo cargar al almacén.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (CertificateException e) {
			String error = "Fallo cargar al almacén.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (UnrecoverableKeyException e) {
			String error = "Fallo al obtener la clave privada.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		}
	}

	/**
	 * Obtiene el acuse de lectura de una notificación ordinaria.
	 * @param idCert Identificador del certificado a usar para firmar el acuse.
	 * @param idPlantilla Identificador de la plantilla a usar como acuse.
	 * @param idLogo Logo ha utilizar en el comprobante.
	 * @param referencia Texto a mostrar en la referencia.
	 * @param asunto Asunto de la notificación.
	 * @param fechaLectura Fecha de lectura de la notificación.
	 * @param ip Dirección Ip desde la que se ha leido la notificación
	 * @return Un BLOB con el PDF del acuse o null en caso de error.
	 */
	public static BLOB obtenerAcuseLecturaOrdinario(int idCert, int idPlantilla, int idLogo, String referencia, String asunto, String fechaLectura, String ip) {

		try {
			// Obtenemos la conexion interna de Oracle
			Connection conn = DriverManager.getConnection("jdbc:default:connection");
			
			// Seleccionamos los datos de la Base de Datos
			OraclePreparedStatement stmt = (OraclePreparedStatement) conn.prepareStatement("SELECT p.plantilla, c.certificado, c.passwd, l.imagen FROM plantillas p, certificados c, logos l WHERE p.id=? AND c.id= ? AND l.id = ?");
																							
			stmt.setInt(1, idPlantilla);
			stmt.setInt(2, idCert);
			stmt.setInt(3, idLogo);

			ResultSet rset = stmt.executeQuery();

			if (!rset.next()){
				throw new SQLException("No se han encontrado datos para la consulta.");
			}

			oracle.sql.BLOB blob = (BLOB) rset.getBlob(1);
			oracle.sql.BLOB blobAlmacen = (BLOB) rset.getBlob(2);
			String clave = rset.getString(3);
			oracle.sql.BLOB blobLogo = (BLOB) rset.getBlob(4);
			
			// Obtenemos la longitud del BLOB con el PDF
			int length = (int) blob.length();
			byte bytesPDF[] = blob.getBytes(1, length);
			
			// Obtenemos la longitud del BLOB con el logo
			length = (int) blobLogo.length();
			byte bytesLogo[] = blobLogo.getBytes(1, length);
				
			// Leemos el pdf original
			PdfReader reader = new PdfReader(bytesPDF);

			// Creamos el blob que contendra el comprobante
			BLOB blobComprobante = BLOB.createTemporary(conn, true,	BLOB.DURATION_SESSION);
			OutputStream outStream = blobComprobante.setBinaryStream(1L);

			// Preparamos el pdf de salida para escribir en el
			PdfStamper stamper = PdfStamper.createSignature(reader, outStream, '\0', null, true);
			PdfContentByte canvas = stamper.getOverContent(1);

			ColumnText columna = new ColumnText(canvas);
			
			// Ponemos el logo de la institución
			Image logo = Image.getInstance(bytesLogo);
			logo.setAbsolutePosition(90, 580);
			canvas.addImage(logo);
			
			// Empezamos con el encabezado
			Chunk trozo = new Chunk();
			trozo.append(ENCABEZADO);
			trozo.setFont(FUENTE_ENCABEZADO);
			Phrase frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 245, 600, 400, 600, 0, Element.ALIGN_LEFT);
			columna.go();  
			
			// Seguimos con la referencia
			trozo = new Chunk();
			trozo.append("Referencia: ");
			trozo.setFont(FUENTE_TITULO);
			frase = new Phrase(trozo);

			trozo = new Chunk(referencia);
			trozo.setFont(FUENTE_TEXTO);
			frase.add(trozo);

			columna.setSimpleColumn(frase, 100, 550, 300, 550, 0, Element.ALIGN_LEFT);
			columna.go();
				
			// Seguimos con el asunto
			trozo = new Chunk();
			trozo.append("Asunto");
			trozo.setFont(FUENTE_TITULO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 0, 520, 612, 520, 0, Element.ALIGN_CENTER);
			columna.go();
				
			trozo = new Chunk();
			trozo.append(asunto);
			trozo.setFont(FUENTE_TEXTO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 100, 450, 508, 510, 13, Element.ALIGN_CENTER);
			columna.go();
				
			// Seguimos los datos del lector
			trozo = new Chunk();
			trozo.append("Dirección IP desde la que se ha leído:");
			trozo.setFont(FUENTE_TITULO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 100, 425, 512, 425, 0, Element.ALIGN_LEFT);
			columna.go();
				
			trozo = new Chunk();
			trozo.append(ip);
			trozo.setFont(FUENTE_TEXTO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 100, 400, 512, 400, 0, Element.ALIGN_LEFT);
			columna.go();

			// Seguimos con la fecha y hora de la lectura
			trozo = new Chunk();
			trozo.append("Fecha y hora de lectura:");
			trozo.setFont(FUENTE_TITULO);
			frase = new Phrase(trozo);
				
			columna.setSimpleColumn(frase, 100, 360, 512, 360, 0, Element.ALIGN_LEFT);
			columna.go();
							
			trozo = new Chunk();
			trozo.append(fechaLectura);
			trozo.setFont(FUENTE_TEXTO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 100, 335, 512, 335, 0, Element.ALIGN_LEFT);
			columna.go();
			
			// Seguimos con el pie
			trozo = new Chunk();
			trozo.append(PIE);
			trozo.setFont(FUENTE_PIE);
			frase = new Phrase(trozo);
							
			columna.setSimpleColumn(frase, 80, 100, 515, 80, 10, Element.ALIGN_LEFT);
			columna.go();			
			
			// Firmamos el acuse
			
			// Obtenemos el almacen
			byte[] almacen = blobAlmacen.getBytes(1, (int) blobAlmacen.length());
			KeyStore ks = KeyStore.getInstance("PKCS12");			
			ks.load(new ByteArrayInputStream(almacen), clave.toCharArray());
			
			// Obtenemos la clave privada
			String alias = (String)ks.aliases().nextElement();
			PrivateKey key = (PrivateKey)ks.getKey(alias, clave.toCharArray());
			Certificate[] chain = ks.getCertificateChain(alias);
			
			// Firmamos
			PdfSignatureAppearance sap = stamper.getSignatureAppearance();
			sap.setCrypto(key, chain, null, PdfSignatureAppearance.WINCER_SIGNED);
			// comment next line to have an invisible signature
			sap.setVisibleSignature(new Rectangle(395, 535, 505, 630), 1, null);
				
			stamper.close();
			outStream.close();

			return blobComprobante;
		} catch (SQLException e) {
			String error = "Fallo al operar con la base de datos.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (IOException e) {
			String error = "Fallo al operar con los PDF's.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (DocumentException e) {
			String error = "Fallo al operar con el PDF del comprobante.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (KeyStoreException e) {
			String error = "Fallo al obtener el almacén.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (NoSuchAlgorithmException e) {
			String error = "Fallo cargar al almacén.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (CertificateException e) {
			String error = "Fallo cargar al almacén.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (UnrecoverableKeyException e) {
			String error = "Fallo al obtener la clave privada.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		}
	}


	/**
	 * Método que devuelve un BLOB con un PDF con el comprobante de lectura de una notificación certificada.
	 * @param connJava Conexión a la base de datos del usuario Java_jars.
	 * @param connRegistro Conexión a la base de datos del usuario Registro.
	 * @param idCert Identificador del certificado a usar para firmar el acuse.
	 * @param idPlantilla Plantilla de comprobante a utilizar.
	 * @param idLogo Logo ha utilizar en el comprobante.
	 * @param idAcuse Acuse de lectura a mostrar.
	 * @param idTSA Identificador de la TSA que se ha utilizado para el sellado de tiempo.
	 * @return PDF con el comprobante.
	 */
	public static BLOB obtenerAcuseLecturaCertificado(Connection connJava, Connection connRegistro, int idCert, int idPlantilla, int idLogo, int idTSA, int idAcuse) {

		try {
			// Seleccionamos los datos de la Base de Datos
			OraclePreparedStatement stmt = (OraclePreparedStatement) connJava.prepareStatement("SELECT p.plantilla, c.certificado, c.passwd, l.imagen FROM plantillas p, certificados c, logos l WHERE p.id=? AND c.id=? AND l.id = ?");
			stmt.setInt(1, idPlantilla);
			stmt.setInt(2, idCert);
			stmt.setInt(3, idLogo);

			ResultSet rset = stmt.executeQuery();

			if (!rset.next()){
				throw new SQLException("No se han encontrado datos para la consulta.");
			}

			oracle.sql.BLOB blob = (BLOB) rset.getBlob(1);
			oracle.sql.BLOB blobAlmacen = (BLOB) rset.getBlob(2);
			String clave = rset.getString(3);
			oracle.sql.BLOB blobLogo = (BLOB) rset.getBlob(4);

			// Obtenemos la longitud del BLOB con el PDF
			int length = (int) blob.length();
			byte bytesPDF[] = blob.getBytes(1, length);
			
			// Obtenemos la longitud del BLOB con el logo
			length = (int) blobLogo.length();
			byte bytesLogo[] = blobLogo.getBytes(1, length);
			
			stmt = (OraclePreparedStatement) connRegistro.prepareStatement("SELECT n.asunto, s.fecha_lectura, s.ip, s.numero_serie_tsa FROM notificaciones n, sin_acuses s WHERE n.id=s.id_notificacion AND s.id=?");
			stmt.setInt(1, idAcuse);

			rset = stmt.executeQuery();

			if (!rset.next()){
				throw new SQLException("No se han encontrado datos para la consulta.");
			}

			String asunto = rset.getString(1);
			String fechaLectura = rset.getString(2);
			String ip = rset.getString(3);
			String nSerieSelloTiempo = rset.getString(4);

			
			stmt = (OraclePreparedStatement) connJava.prepareStatement("SELECT nombre_canonico, TO_CHAR(desde,'DD/MM/YYYY HH24:MI:SS'), TO_CHAR(hasta,'DD/MM/YYYY HH24:MI:SS'), logo, n_serie FROM tsa WHERE id=?");
			stmt.setInt(1, idTSA);

			rset = stmt.executeQuery();

			if (!rset.next()){
				throw new SQLException("No se han encontrado datos para la consulta.");
			}

			String nombreTSA = rset.getString(1);
			String fechaInicioTSA = rset.getString(2);
			String fechaFinTSA = rset.getString(3);
			oracle.sql.BLOB blobLogoTSA = (BLOB) rset.getBlob(4);
			String nSerieTSA = rset.getString(5);
			
			// Obtenemos la longitud del BLOB con el logo de la TSA
			length = (int) blobLogoTSA.length();
			byte bytesLogoTSA[] = blobLogoTSA.getBytes(1, length);
			
			// Leemos el pdf original
			PdfReader reader = new PdfReader(bytesPDF);

			// Creamos el blob que contendra el comprobante
			BLOB blobComprobante = BLOB.createTemporary(connRegistro, true,	BLOB.DURATION_SESSION);
			OutputStream outStream = blobComprobante.setBinaryStream(1L);

			// Preparamos el pdf de salida para escribir en el
			PdfStamper stamper = PdfStamper.createSignature(reader, outStream, '\0', null, true);
			PdfContentByte canvas = stamper.getOverContent(1);

			ColumnText columna = new ColumnText(canvas);

			// Ponemos el logo de la institución y de la TSA
			Image logo = Image.getInstance(bytesLogo);
			logo.setAbsolutePosition(90, 620);
			
			Image logoTSA = Image.getInstance(bytesLogoTSA);
			logoTSA.setAbsolutePosition(350, 620);
			
			canvas.addImage(logo);
			canvas.addImage(logoTSA);
			
			// Empezamos con el encabezado
			Chunk trozo = new Chunk();
			trozo.append(ENCABEZADO);
			trozo.setFont(FUENTE_ENCABEZADO);
			Phrase frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 245, 600, 400, 600, 0, Element.ALIGN_LEFT);
			columna.go();  
			
			// Seguimos con la referencia
			trozo = new Chunk();
			trozo.append("Referencia: ");
			trozo.setFont(FUENTE_TITULO);
			frase = new Phrase(trozo);

			trozo = new Chunk("" + idAcuse);
			trozo.setFont(FUENTE_TEXTO);
			frase.add(trozo);

			columna.setSimpleColumn(frase, 100, 550, 300, 550, 0, Element.ALIGN_LEFT);
			columna.go();
				
			// Seguimos con el asunto
			trozo = new Chunk();
			trozo.append("Asunto");
			trozo.setFont(FUENTE_TITULO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 0, 520, 612, 520, 0, Element.ALIGN_CENTER);
			columna.go();
				
			trozo = new Chunk();
			trozo.append(asunto);
			trozo.setFont(FUENTE_TEXTO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 100, 450, 508, 510, 13, Element.ALIGN_CENTER);
			columna.go();
				
			// Seguimos los datos del lector
			trozo = new Chunk();
			trozo.append("Dirección IP desde la que se ha leído:");
			trozo.setFont(FUENTE_TITULO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 100, 415, 512, 415, 0, Element.ALIGN_LEFT);
			columna.go();
				
			trozo = new Chunk();
			trozo.append(ip);
			trozo.setFont(FUENTE_TEXTO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 100, 400, 512, 400, 0, Element.ALIGN_LEFT);
			columna.go();

			// Seguimos con la fecha y hora de la lectura
			trozo = new Chunk();
			trozo.append("Fecha y hora de lectura:");
			trozo.setFont(FUENTE_TITULO);
			frase = new Phrase(trozo);
				
			columna.setSimpleColumn(frase, 100, 370, 512, 370, 0, Element.ALIGN_LEFT);
			columna.go();
							
			trozo = new Chunk();
			trozo.append(fechaLectura);
			trozo.setFont(FUENTE_TEXTO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 100, 355, 512, 355, 0, Element.ALIGN_LEFT);
			columna.go();
			
			// Seguimos con el numero de serie del sello de tiempo
			trozo = new Chunk();
			trozo.append("Número de serie del sello de tiempo:");
			trozo.setFont(FUENTE_TITULO);
			frase = new Phrase(trozo);
				
			columna.setSimpleColumn(frase, 100, 325, 512, 325, 0, Element.ALIGN_LEFT);
			columna.go();
							
			trozo = new Chunk();
			trozo.append(nSerieSelloTiempo);
			trozo.setFont(FUENTE_TEXTO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 100, 310, 512, 310, 0, Element.ALIGN_LEFT);
			columna.go();			
			
			// Seguimos con la TSA
			trozo = new Chunk();
			trozo.append("Datos de la TSA:");
			trozo.setFont(FUENTE_TITULO);
			frase = new Phrase(trozo);
							
			columna.setSimpleColumn(frase, 100, 280, 512, 280, 0, Element.ALIGN_LEFT);
			columna.go();
										
			trozo = new Chunk();
			trozo.append(nombreTSA);
			trozo.setFont(FUENTE_TEXTO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 100, 265, 512, 265, 0, Element.ALIGN_LEFT);
			columna.go();
			
			trozo = new Chunk();
			trozo.append("Número de serie: ");
			trozo.setFont(FUENTE_TEXTO);
			frase = new Phrase(trozo);

			trozo = new Chunk();
			trozo.append(nSerieTSA);
			trozo.setFont(FUENTE_TEXTO);
			frase.add(trozo);
			
			columna.setSimpleColumn(frase, 100, 250, 512, 250, 0, Element.ALIGN_LEFT);
			columna.go();

			trozo = new Chunk();
			trozo.append("Válido desde: ");
			trozo.setFont(FUENTE_TEXTO);
			frase = new Phrase(trozo);
			
			trozo = new Chunk();
			trozo.append(fechaInicioTSA);
			trozo.setFont(FUENTE_TEXTO);
			frase.add(trozo);
			
			trozo = new Chunk();
			trozo.append(" hasta: ");
			trozo.setFont(FUENTE_TEXTO);
			frase.add(trozo);
			
			trozo = new Chunk();
			trozo.append(fechaFinTSA);
			trozo.setFont(FUENTE_TEXTO);
			frase.add(trozo);
			
			columna.setSimpleColumn(frase, 100, 235, 512, 235, 0, Element.ALIGN_LEFT);
			columna.go();
			
			// Seguimos con el pie
			trozo = new Chunk();
			trozo.append(PIE);
			trozo.setFont(FUENTE_PIE);
			frase = new Phrase(trozo);
							
			columna.setSimpleColumn(frase, 80, 100, 515, 80, 10, Element.ALIGN_LEFT);
			columna.go();
			
			// Firmamos el acuse
			
			// Obtenemos el almacen
			byte[] almacen = blobAlmacen.getBytes(1, (int) blobAlmacen.length());
			KeyStore ks = KeyStore.getInstance("PKCS12");			
			ks.load(new ByteArrayInputStream(almacen), clave.toCharArray());
			
			// Obtenemos la clave privada
			String alias = (String)ks.aliases().nextElement();
			PrivateKey key = (PrivateKey)ks.getKey(alias, clave.toCharArray());
			Certificate[] chain = ks.getCertificateChain(alias);
			
			// Firmamos
			PdfSignatureAppearance sap = stamper.getSignatureAppearance();
			sap.setCrypto(key, chain, null, PdfSignatureAppearance.WINCER_SIGNED);
			// comment next line to have an invisible signature
			sap.setVisibleSignature(new Rectangle(395, 535, 505, 630), 1, null);
				
			stamper.close();
			outStream.close();

			return blobComprobante;
		} catch (SQLException e) {
			String error = "Fallo al operar con la base de datos.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (IOException e) {
			String error = "Fallo al operar con los PDF's.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (DocumentException e) {
			String error = "Fallo al operar con el PDF del comprobante.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (KeyStoreException e) {
			String error = "Fallo al obtener el almacén.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (NoSuchAlgorithmException e) {
			String error = "Fallo cargar al almacén.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (CertificateException e) {
			String error = "Fallo cargar al almacén.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (UnrecoverableKeyException e) {
			String error = "Fallo al obtener la clave privada.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		}
	}


	/**
	 * Obtiene el acuse de lectura de una notificación certificada.
	 * @param idCert Identificador del certificado a usar para firmar el acuse.
	 * @param idPlantilla Identificador de la plantilla a usar como acuse.
	 * @param idLogo Logo ha utilizar en el comprobante.
	 * @param idTSA Identificador de la TSA del sello de tiempo.
	 * @param referencia Texto a mostrar en la referencia.
	 * @param asunto Asunto de la notificación.
	 * @param fechaLectura Fecha de lectura de la notificación.
	 * @param ip Dirección Ip desde la que se ha leido la notificación
	 * @param nSerieSelloTiempo Número de serie del sello de tiempo. 
	 * @return Un BLOB con el PDF del acuse o null en caso de error.
	 */
	public static BLOB obtenerAcuseLecturaCertificado(int idCert, int idPlantilla, int idLogo, int idTSA, String referencia, String asunto, String fechaLectura, String ip, String nSerieSelloTiempo) {

		try {
			// Obtenemos la conexion interna de Oracle
			Connection conn = DriverManager.getConnection("jdbc:default:connection");
			
			// Seleccionamos los datos de la Base de Datos
			OraclePreparedStatement stmt = (OraclePreparedStatement) conn.prepareStatement("SELECT p.plantilla, c.certificado, c.passwd, t.nombre_canonico, t.n_serie, TO_CHAR(t.desde,'DD/MM/YYYY HH24:MI:SS'), TO_CHAR(t.hasta,'DD/MM/YYYY HH24:MI:SS'), t.logo, l.imagen  FROM plantillas p, certificados c, tsa t, logos l WHERE p.id=? AND c.id= ? AND t.id=? AND l.id = ?");
			stmt.setInt(1, idPlantilla);
			stmt.setInt(2, idCert);
			stmt.setInt(3, idTSA);
			stmt.setInt(4, idLogo);

			ResultSet rset = stmt.executeQuery();

			if (!rset.next()){
				throw new SQLException("No se han encontrado datos para la consulta.");
			}

			oracle.sql.BLOB blob = (BLOB) rset.getBlob(1);
			oracle.sql.BLOB blobAlmacen = (BLOB) rset.getBlob(2);
			String clave = rset.getString(3);
			String nombreTSA = rset.getString(4);
			String nSerieTSA = rset.getString(5);
			String fechaInicioTSA = rset.getString(6);
			String fechaFinTSA = rset.getString(7);
			oracle.sql.BLOB blobLogoTSA = (BLOB) rset.getBlob(8);
			oracle.sql.BLOB blobLogo = (BLOB) rset.getBlob(9);
			
			// Obtenemos la longitud del BLOB con el PDF
			int length = (int) blob.length();
			byte bytes[] = blob.getBytes(1, length);
			
			// Obtenemos la longitud del BLOB con el logo de la TSA
			length = (int) blobLogoTSA.length();
			byte bytesLogoTSA[] = blobLogoTSA.getBytes(1, length);

			// Obtenemos la longitud del BLOB con el logo de la TSA
			length = (int) blobLogo.length();
			byte bytesLogo[] = blobLogo.getBytes(1, length);

			// Leemos el pdf original
			PdfReader reader = new PdfReader(bytes);

			// Creamos el blob que contendra el comprobante
			BLOB blobComprobante = BLOB.createTemporary(conn, true,	BLOB.DURATION_SESSION);
			OutputStream outStream = blobComprobante.setBinaryStream(1L);

			// Preparamos el pdf de salida para escribir en el
			PdfStamper stamper = PdfStamper.createSignature(reader, outStream, '\0', null, true);
			PdfContentByte canvas = stamper.getOverContent(1);

			ColumnText columna = new ColumnText(canvas);

			// Ponemos el logo de la institución y de la TSA
			Image logo = Image.getInstance(bytesLogo);
			logo.setAbsolutePosition(90, 620);
			
			Image logoTSA = Image.getInstance(bytesLogoTSA);
			logoTSA.setAbsolutePosition(350, 620);
			
			canvas.addImage(logo);
			canvas.addImage(logoTSA);
			
			// Empezamos con el encabezado
			Chunk trozo = new Chunk();
			trozo.append(ENCABEZADO);
			trozo.setFont(FUENTE_ENCABEZADO);
			Phrase frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 245, 600, 400, 600, 0, Element.ALIGN_LEFT);
			columna.go();  
			
			// Seguimos con la referencia
			trozo = new Chunk();
			trozo.append("Referencia: ");
			trozo.setFont(FUENTE_TITULO);
			frase = new Phrase(trozo);

			trozo = new Chunk(referencia);
			trozo.setFont(FUENTE_TEXTO);
			frase.add(trozo);

			columna.setSimpleColumn(frase, 100, 550, 300, 550, 0, Element.ALIGN_LEFT);
			columna.go();
				
			// Seguimos con el asunto
			trozo = new Chunk();
			trozo.append("Asunto");
			trozo.setFont(FUENTE_TITULO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 0, 520, 612, 520, 0, Element.ALIGN_CENTER);
			columna.go();
				
			trozo = new Chunk();
			trozo.append(asunto);
			trozo.setFont(FUENTE_TEXTO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 100, 450, 508, 510, 13, Element.ALIGN_CENTER);
			columna.go();
				
			// Seguimos los datos del lector
			trozo = new Chunk();
			trozo.append("Dirección IP desde la que se ha leído:");
			trozo.setFont(FUENTE_TITULO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 100, 415, 512, 415, 0, Element.ALIGN_LEFT);
			columna.go();
				
			trozo = new Chunk();
			trozo.append(ip);
			trozo.setFont(FUENTE_TEXTO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 100, 400, 512, 400, 0, Element.ALIGN_LEFT);
			columna.go();

			// Seguimos con la fecha y hora de la lectura
			trozo = new Chunk();
			trozo.append("Fecha y hora de lectura:");
			trozo.setFont(FUENTE_TITULO);
			frase = new Phrase(trozo);
				
			columna.setSimpleColumn(frase, 100, 370, 512, 370, 0, Element.ALIGN_LEFT);
			columna.go();
							
			trozo = new Chunk();
			trozo.append(fechaLectura);
			trozo.setFont(FUENTE_TEXTO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 100, 355, 512, 355, 0, Element.ALIGN_LEFT);
			columna.go();
			
			// Seguimos con el numero de serie del sello de tiempo
			trozo = new Chunk();
			trozo.append("Número de serie del sello de tiempo:");
			trozo.setFont(FUENTE_TITULO);
			frase = new Phrase(trozo);
				
			columna.setSimpleColumn(frase, 100, 325, 512, 325, 0, Element.ALIGN_LEFT);
			columna.go();
							
			trozo = new Chunk();
			trozo.append(nSerieSelloTiempo);
			trozo.setFont(FUENTE_TEXTO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 100, 310, 512, 310, 0, Element.ALIGN_LEFT);
			columna.go();			
			
			// Seguimos con la TSA
			trozo = new Chunk();
			trozo.append("Datos de la TSA:");
			trozo.setFont(FUENTE_TITULO);
			frase = new Phrase(trozo);
							
			columna.setSimpleColumn(frase, 100, 280, 512, 280, 0, Element.ALIGN_LEFT);
			columna.go();
										
			trozo = new Chunk();
			trozo.append(nombreTSA);
			trozo.setFont(FUENTE_TEXTO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 100, 265, 512, 265, 0, Element.ALIGN_LEFT);
			columna.go();
			
			trozo = new Chunk();
			trozo.append("Número de serie: ");
			trozo.setFont(FUENTE_TEXTO);
			frase = new Phrase(trozo);

			trozo = new Chunk();
			trozo.append(nSerieTSA);
			trozo.setFont(FUENTE_TEXTO);
			frase.add(trozo);
			
			columna.setSimpleColumn(frase, 100, 250, 512, 250, 0, Element.ALIGN_LEFT);
			columna.go();

			trozo = new Chunk();
			trozo.append("Válido desde: ");
			trozo.setFont(FUENTE_TEXTO);
			frase = new Phrase(trozo);
			
			trozo = new Chunk();
			trozo.append(fechaInicioTSA);
			trozo.setFont(FUENTE_TEXTO);
			frase.add(trozo);
			
			trozo = new Chunk();
			trozo.append(" hasta: ");
			trozo.setFont(FUENTE_TEXTO);
			frase.add(trozo);
			
			trozo = new Chunk();
			trozo.append(fechaFinTSA);
			trozo.setFont(FUENTE_TEXTO);
			frase.add(trozo);
			
			columna.setSimpleColumn(frase, 100, 235, 512, 235, 0, Element.ALIGN_LEFT);
			columna.go();
			
			// Seguimos con el pie
			trozo = new Chunk();
			trozo.append(PIE);
			trozo.setFont(FUENTE_PIE);
			frase = new Phrase(trozo);
							
			columna.setSimpleColumn(frase, 80, 100, 515, 80, 10, Element.ALIGN_LEFT);
			columna.go();
			
			// Firmamos el acuse
			
			// Obtenemos el almacen
			byte[] almacen = blobAlmacen.getBytes(1, (int) blobAlmacen.length());
			KeyStore ks = KeyStore.getInstance("PKCS12");			
			ks.load(new ByteArrayInputStream(almacen), clave.toCharArray());
			
			// Obtenemos la clave privada
			String alias = (String)ks.aliases().nextElement();
			PrivateKey key = (PrivateKey)ks.getKey(alias, clave.toCharArray());
			Certificate[] chain = ks.getCertificateChain(alias);
			
			// Firmamos
			PdfSignatureAppearance sap = stamper.getSignatureAppearance();
			sap.setCrypto(key, chain, null, PdfSignatureAppearance.WINCER_SIGNED);
			// comment next line to have an invisible signature
			sap.setVisibleSignature(new Rectangle(395, 535, 505, 630), 1, null);
				
			stamper.close();
			outStream.close();

			return blobComprobante;
		} catch (SQLException e) {
			String error = "Fallo al operar con la base de datos.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (IOException e) {
			String error = "Fallo al operar con los PDF's.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (DocumentException e) {
			String error = "Fallo al operar con el PDF del comprobante.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (KeyStoreException e) {
			String error = "Fallo al obtener el almacén.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (NoSuchAlgorithmException e) {
			String error = "Fallo cargar al almacén.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (CertificateException e) {
			String error = "Fallo cargar al almacén.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (UnrecoverableKeyException e) {
			String error = "Fallo al obtener la clave privada.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		}
	}

	
	
	/**
	 * Método que devuelve un BLOB con un PDF con el comprobante de lectura de una notificación fehaciente.
	 * @param connJava Conexión a la base de datos del usuario Java_jars.
	 * @param connRegistro Conexión a la base de datos del usuario Registro.
	 * @param idCert Identificador del certificado a usar para firmar el acuse.
	 * @param idPlantilla Plantilla de comprobante a utilizar.
	 * @param idLogo Logo ha utilizar en el comprobante.
	 * @param idAcuse Acuse de lectura a mostrar.
	 * @param idTSA Identificador de la TSA que se ha utilizado para el sellado de tiempo.
	 * @return PDF con el comprobante.
	 */
	public static BLOB obtenerAcuseLecturaFehaciente(Connection connJava, Connection connRegistro, int idCert, int idPlantilla, int idLogo, int idTSA, int idAcuse) {

		try {
			// Seleccionamos los datos de la Base de Datos
			OraclePreparedStatement stmt = (OraclePreparedStatement) connJava.prepareStatement("SELECT p.plantilla, c.certificado, c.passwd, l.imagen FROM plantillas p, certificados c, logos l WHERE p.id=? AND c.id=? AND l.id = ?");
			stmt.setInt(1, idPlantilla);
			stmt.setInt(2, idCert);
			stmt.setInt(3, idLogo);

			ResultSet rset = stmt.executeQuery();

			if (!rset.next()){
				throw new SQLException("No se han encontrado datos para la consulta.");
			}

			oracle.sql.BLOB blob = (BLOB) rset.getBlob(1);
			oracle.sql.BLOB blobAlmacen = (BLOB) rset.getBlob(2);
			String clave = rset.getString(3);
			oracle.sql.BLOB blobLogo = (BLOB) rset.getBlob(4);

			// Obtenemos la longitud del BLOB con el PDF
			int length = (int) blob.length();
			byte bytesPDF[] = blob.getBytes(1, length);
			
			// Obtenemos la longitud del BLOB con el logo
			length = (int) blobLogo.length();
			byte bytesLogo[] = blobLogo.getBytes(1, length);
			
			stmt = (OraclePreparedStatement) connRegistro.prepareStatement("SELECT n.asunto, a.fecha_lectura, a.subject, a.serial, a.desde_hasta, a.numero_serie_tsa FROM notificaciones n, acuses a WHERE n.id=a.id_notificacion AND a.id=?");
			stmt.setInt(1, idAcuse);

			rset = stmt.executeQuery();

			if (!rset.next()){
				throw new SQLException("No se han encontrado datos para la consulta.");
			}

			String asunto = rset.getString(1);
			String fechaLectura = rset.getString(2);
			String firmante = rset.getString(3);
			String nSerieFirma = rset.getString(4);
			String validez = rset.getString(5);
			String nSerieSelloTiempo = rset.getString(6);
			
			stmt = (OraclePreparedStatement) connJava.prepareStatement("SELECT nombre_canonico, n_serie, TO_CHAR(desde,'DD/MM/YYYY HH24:MI:SS'), TO_CHAR(hasta,'DD/MM/YYYY HH24:MI:SS'), logo FROM tsa WHERE id=?");
			stmt.setInt(1, idTSA);

			rset = stmt.executeQuery();

			if (!rset.next()){
				throw new SQLException("No se han encontrado datos para la consulta.");
			}

			String nombreTSA = rset.getString(1);
			String nSerieTSA = rset.getString(2);
			String fechaInicioTSA = rset.getString(3);
			String fechaFinTSA = rset.getString(4);
			oracle.sql.BLOB blobLogoTSA = (BLOB) rset.getBlob(5);
			
			// Obtenemos la longitud del BLOB con el logo de la TSA
			length = (int) blobLogoTSA.length();
			byte bytesLogoTSA[] = blobLogoTSA.getBytes(1, length);
			
			// Leemos el pdf original
			PdfReader reader = new PdfReader(bytesPDF);

			// Creamos el blob que contendra el comprobante
			BLOB blobComprobante = BLOB.createTemporary(connRegistro, true,	BLOB.DURATION_SESSION);
			OutputStream outStream = blobComprobante.setBinaryStream(1L);

			// Preparamos el pdf de salida para escribir en el
			PdfStamper stamper = PdfStamper.createSignature(reader, outStream, '\0', null, true);
			PdfContentByte canvas = stamper.getOverContent(1);

			ColumnText columna = new ColumnText(canvas);

			// Ponemos el logo de la institución y de la TSA
			Image logo = Image.getInstance(bytesLogo);
			logo.setAbsolutePosition(90, 620);
			
			Image logoTSA = Image.getInstance(bytesLogoTSA);
			logoTSA.setAbsolutePosition(350, 620);
			
			canvas.addImage(logo);
			canvas.addImage(logoTSA);
			
			// Empezamos con el encabezado
			Chunk trozo = new Chunk();
			trozo.append(ENCABEZADO);
			trozo.setFont(FUENTE_ENCABEZADO);
			Phrase frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 245, 600, 400, 600, 0, Element.ALIGN_LEFT);
			columna.go();  
			
			// Seguimos con la referencia
			trozo = new Chunk();
			trozo.append("Referencia: ");
			trozo.setFont(FUENTE_TITULO);
			frase = new Phrase(trozo);
			
			trozo = new Chunk("" + idAcuse);
			trozo.setFont(FUENTE_TEXTO);
			frase.add(trozo);

			columna.setSimpleColumn(frase, 100, 550, 300, 550, 0, Element.ALIGN_LEFT);
			columna.go();
				
			// Seguimos con el asunto
			trozo = new Chunk();
			trozo.append("Asunto");
			trozo.setFont(FUENTE_TITULO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 0, 520, 612, 520, 0, Element.ALIGN_CENTER);
			columna.go();
				
			trozo = new Chunk();
			trozo.append(asunto);
			trozo.setFont(FUENTE_TEXTO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 100, 450, 508, 515, 13, Element.ALIGN_CENTER);
			columna.go();
				
			// Seguimos los datos del firmante y del certificado
			trozo = new Chunk();
			trozo.append("Datos del firmante");
			trozo.setFont(FUENTE_TITULO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 100, 425, 512, 425, 0, Element.ALIGN_LEFT);
			columna.go();
				
			trozo = new Chunk();
			trozo.append(firmante);
			trozo.setFont(FUENTE_TEXTO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 100, 422, 512, 355, 13, Element.ALIGN_LEFT);
			columna.go();

			trozo = new Chunk();
			trozo.append("Número de serie del certificado");
			trozo.setFont(FUENTE_TITULO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 100, 340, 512, 340, 0, Element.ALIGN_LEFT);
			columna.go();
				
			trozo = new Chunk();
			trozo.append(nSerieFirma);
			trozo.setFont(FUENTE_TEXTO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 100, 325, 512, 325, 0, Element.ALIGN_LEFT);
			columna.go();

			trozo = new Chunk();
			trozo.append("Periodo de validez del certificado");
			trozo.setFont(FUENTE_TITULO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 100, 305, 512, 305, 0, Element.ALIGN_LEFT);
			columna.go();
				
			trozo = new Chunk();
			trozo.append(validez);
			trozo.setFont(FUENTE_TEXTO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 100, 302, 512, 275, 13, Element.ALIGN_LEFT);
			columna.go();
						
			// Seguimos con la fecha y hora de la lectura
			trozo = new Chunk();
			trozo.append("Fecha y hora de lectura:");
			trozo.setFont(FUENTE_TITULO);
			frase = new Phrase(trozo);
				
			columna.setSimpleColumn(frase, 100, 255, 512, 255, 0, Element.ALIGN_LEFT);
			columna.go();
							
			trozo = new Chunk();
			trozo.append(fechaLectura);
			trozo.setFont(FUENTE_TEXTO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 100, 240, 512, 240, 0, Element.ALIGN_LEFT);
			columna.go();

			// Seguimos con el numero de serie del sello de tiempo
			trozo = new Chunk();
			trozo.append("Número de serie del sello de tiempo:");
			trozo.setFont(FUENTE_TITULO);
			frase = new Phrase(trozo);
				
			columna.setSimpleColumn(frase, 100, 220, 512, 220, 0, Element.ALIGN_LEFT);
			columna.go();
							
			trozo = new Chunk();
			trozo.append(nSerieSelloTiempo);
			trozo.setFont(FUENTE_TEXTO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 100, 205, 512, 205, 0, Element.ALIGN_LEFT);
			columna.go();
					
			// Seguimos con la TSA
			trozo = new Chunk();
			trozo.append("Datos de la TSA:");
			trozo.setFont(FUENTE_TITULO);
			frase = new Phrase(trozo);
							
			columna.setSimpleColumn(frase, 100, 185, 512, 185, 0, Element.ALIGN_LEFT);
			columna.go();
										
			trozo = new Chunk();
			trozo.append(nombreTSA);
			trozo.setFont(FUENTE_TEXTO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 100, 170, 512, 170, 0, Element.ALIGN_LEFT);
			columna.go();
			
			trozo = new Chunk();
			trozo.append("Número de serie: ");
			trozo.setFont(FUENTE_TEXTO);
			frase = new Phrase(trozo);

			trozo = new Chunk();
			trozo.append(nSerieTSA);
			trozo.setFont(FUENTE_TEXTO);
			frase.add(trozo);
			
			columna.setSimpleColumn(frase, 100, 155, 512, 155, 0, Element.ALIGN_LEFT);
			columna.go();

			trozo = new Chunk();
			trozo.append("Válido desde: ");
			trozo.setFont(FUENTE_TEXTO);
			frase = new Phrase(trozo);
			
			trozo = new Chunk();
			trozo.append(fechaInicioTSA);
			trozo.setFont(FUENTE_TEXTO);
			frase.add(trozo);
			
			trozo = new Chunk();
			trozo.append(" hasta: ");
			trozo.setFont(FUENTE_TEXTO);
			frase.add(trozo);
			
			trozo = new Chunk();
			trozo.append(fechaFinTSA);
			trozo.setFont(FUENTE_TEXTO);
			frase.add(trozo);
			
			columna.setSimpleColumn(frase, 100, 152, 512, 125, 13, Element.ALIGN_LEFT);
			columna.go();
									
			// Seguimos con el pie
			trozo = new Chunk();
			trozo.append(PIE);
			trozo.setFont(FUENTE_PIE);
			frase = new Phrase(trozo);
							
			columna.setSimpleColumn(frase, 80, 100, 515, 80, 10, Element.ALIGN_LEFT);
			columna.go();			
			
			
			// Firmamos el acuse
			
			// Obtenemos el almacen
			byte[] almacen = blobAlmacen.getBytes(1, (int) blobAlmacen.length());
			KeyStore ks = KeyStore.getInstance("PKCS12");			
			ks.load(new ByteArrayInputStream(almacen), clave.toCharArray());
			
			// Obtenemos la clave privada
			String alias = (String)ks.aliases().nextElement();
			PrivateKey key = (PrivateKey)ks.getKey(alias, clave.toCharArray());
			Certificate[] chain = ks.getCertificateChain(alias);
			
			// Firmamos
			PdfSignatureAppearance sap = stamper.getSignatureAppearance();
			sap.setCrypto(key, chain, null, PdfSignatureAppearance.WINCER_SIGNED);
			// comment next line to have an invisible signature
			sap.setVisibleSignature(new Rectangle(395, 535, 505, 630), 1, null);
				
			stamper.close();
			outStream.close();

			return blobComprobante;
		} catch (SQLException e) {
			String error = "Fallo al operar con la base de datos.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (IOException e) {
			String error = "Fallo al operar con los PDF's.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (DocumentException e) {
			String error = "Fallo al operar con el PDF del comprobante.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (KeyStoreException e) {
			String error = "Fallo al obtener el almacén.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (NoSuchAlgorithmException e) {
			String error = "Fallo cargar al almacén.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (CertificateException e) {
			String error = "Fallo cargar al almacén.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (UnrecoverableKeyException e) {
			String error = "Fallo al obtener la clave privada.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		}
	}
	
	/**
	 * Obtiene el acuse de lectura de una notificación fehaciente.
	 * @param idCert Identificador del certificado a usar para firmar el acuse.
	 * @param idPlantilla Identificador de la plantilla a usar como acuse.
	 * @param idLogo Logo ha utilizar en el comprobante.
	 * @param idTSA Identificador de la TSA del sello de tiempo.
	 * @param referencia Texto a mostrar en la referencia.
	 * @param asunto Asunto de la notificación.
	 * @param fechaLectura Fecha de lectura de la notificación.
	 * @param subject Identificador del propietario del certificado.
	 * @param serial Número de serie del certificado de firma.
	 * @param desdeHasta Periodo de validez del certificado de firma.
	 * @param nSerieSelloTiempo Número de serie del sello de tiempo.
	 * @return Un BLOB con el PDF del acuse o null en caso de error.
	 */
	public static BLOB obtenerAcuseLecturaFehaciente(int idCert, int idPlantilla, int idLogo, int idTSA, String referencia, String asunto, String fechaLectura, String subject, String serial, String desdeHasta, String nSerieSelloTiempo) {

		try {
			// Obtenemos la conexion interna de Oracle
			Connection conn = DriverManager.getConnection("jdbc:default:connection");
			
			// Seleccionamos los datos de la Base de Datos
			OraclePreparedStatement stmt = (OraclePreparedStatement) conn.prepareStatement("SELECT p.plantilla, c.certificado, c.passwd, t.nombre_canonico, t.n_serie, TO_CHAR(t.desde,'DD/MM/YYYY HH24:MI:SS'), TO_CHAR(t.hasta,'DD/MM/YYYY HH24:MI:SS'), t.logo, l.imagen  FROM plantillas p, certificados c, tsa t, logos l WHERE p.id=? AND c.id= ? AND t.id=? AND l.id = ?");
			stmt.setInt(1, idPlantilla);
			stmt.setInt(2, idCert);
			stmt.setInt(3, idTSA);
			stmt.setInt(4, idLogo);
			
			
			ResultSet rset = stmt.executeQuery();

			if (!rset.next()){
				throw new SQLException("No se han encontrado datos para la consulta.");
			}

			oracle.sql.BLOB blob = (BLOB) rset.getBlob(1);
			oracle.sql.BLOB blobAlmacen = (BLOB) rset.getBlob(2);
			String clave = rset.getString(3);
			String nombreTSA = rset.getString(4);
			String nSerieTSA = rset.getString(5);
			String fechaInicioTSA = rset.getString(6);
			String fechaFinTSA = rset.getString(7);
			oracle.sql.BLOB blobLogoTSA = (BLOB) rset.getBlob(8);
			oracle.sql.BLOB blobLogo = (BLOB) rset.getBlob(9);
			
			// Obtenemos la longitud del BLOB con el PDF
			int length = (int) blob.length();
			byte bytes[] = blob.getBytes(1, length);
			
			// Obtenemos la longitud del BLOB con el logo de la TSA
			length = (int) blobLogoTSA.length();
			byte bytesLogoTSA[] = blobLogoTSA.getBytes(1, length);

			// Obtenemos la longitud del BLOB con el logo de la TSA
			length = (int) blobLogo.length();
			byte bytesLogo[] = blobLogo.getBytes(1, length);
			
			// Leemos el pdf original
			PdfReader reader = new PdfReader(bytes);

			// Creamos el blob que contendra el comprobante
			BLOB blobComprobante = BLOB.createTemporary(conn, true,	BLOB.DURATION_SESSION);
			OutputStream outStream = blobComprobante.setBinaryStream(1L);

			// Preparamos el pdf de salida para escribir en el
			PdfStamper stamper = PdfStamper.createSignature(reader, outStream, '\0', null, true);
			PdfContentByte canvas = stamper.getOverContent(1);

			ColumnText columna = new ColumnText(canvas);

			// Ponemos el logo de la institución y de la TSA
			Image logo = Image.getInstance(bytesLogo);
			logo.setAbsolutePosition(90, 620);
			
			Image logoTSA = Image.getInstance(bytesLogoTSA);
			logoTSA.setAbsolutePosition(350, 620);
			
			canvas.addImage(logo);
			canvas.addImage(logoTSA);
			
			// Empezamos con el encabezado
			Chunk trozo = new Chunk();
			trozo.append(ENCABEZADO);
			trozo.setFont(FUENTE_ENCABEZADO);
			Phrase frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 245, 600, 400, 600, 0, Element.ALIGN_LEFT);
			columna.go();  
			
			// Seguimos con la referencia
			trozo = new Chunk();
			trozo.append("Referencia: ");
			trozo.setFont(FUENTE_TITULO);
			frase = new Phrase(trozo);
			
			trozo = new Chunk(referencia);
			trozo.setFont(FUENTE_TEXTO);
			frase.add(trozo);

			columna.setSimpleColumn(frase, 100, 550, 300, 550, 0, Element.ALIGN_LEFT);
			columna.go();
				
			// Seguimos con el asunto
			trozo = new Chunk();
			trozo.append("Asunto");
			trozo.setFont(FUENTE_TITULO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 0, 520, 612, 520, 0, Element.ALIGN_CENTER);
			columna.go();
				
			trozo = new Chunk();
			trozo.append(asunto);
			trozo.setFont(FUENTE_TEXTO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 100, 450, 508, 515, 13, Element.ALIGN_CENTER);
			columna.go();
				
			// Seguimos los datos del firmante y del certificado
			trozo = new Chunk();
			trozo.append("Datos del firmante");
			trozo.setFont(FUENTE_TITULO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 100, 425, 512, 425, 0, Element.ALIGN_LEFT);
			columna.go();
				
			trozo = new Chunk();
			trozo.append(subject);
			trozo.setFont(FUENTE_TEXTO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 100, 422, 512, 355, 13, Element.ALIGN_LEFT);
			columna.go();

			trozo = new Chunk();
			trozo.append("Número de serie del certificado");
			trozo.setFont(FUENTE_TITULO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 100, 340, 512, 340, 0, Element.ALIGN_LEFT);
			columna.go();
				
			trozo = new Chunk();
			trozo.append(serial);
			trozo.setFont(FUENTE_TEXTO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 100, 325, 512, 325, 0, Element.ALIGN_LEFT);
			columna.go();

			trozo = new Chunk();
			trozo.append("Periodo de validez del certificado");
			trozo.setFont(FUENTE_TITULO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 100, 305, 512, 305, 0, Element.ALIGN_LEFT);
			columna.go();
				
			trozo = new Chunk();
			trozo.append(desdeHasta);
			trozo.setFont(FUENTE_TEXTO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 100, 302, 512, 275, 13, Element.ALIGN_LEFT);
			columna.go();
						
			// Seguimos con la fecha y hora de la lectura
			trozo = new Chunk();
			trozo.append("Fecha y hora de lectura:");
			trozo.setFont(FUENTE_TITULO);
			frase = new Phrase(trozo);
				
			columna.setSimpleColumn(frase, 100, 255, 512, 255, 0, Element.ALIGN_LEFT);
			columna.go();
							
			trozo = new Chunk();
			trozo.append(fechaLectura);
			trozo.setFont(FUENTE_TEXTO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 100, 240, 512, 240, 0, Element.ALIGN_LEFT);
			columna.go();

			// Seguimos con el numero de serie del sello de tiempo
			trozo = new Chunk();
			trozo.append("Número de serie del sello de tiempo:");
			trozo.setFont(FUENTE_TITULO);
			frase = new Phrase(trozo);
				
			columna.setSimpleColumn(frase, 100, 220, 512, 220, 0, Element.ALIGN_LEFT);
			columna.go();
							
			trozo = new Chunk();
			trozo.append(nSerieSelloTiempo);
			trozo.setFont(FUENTE_TEXTO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 100, 205, 512, 205, 0, Element.ALIGN_LEFT);
			columna.go();
					
			// Seguimos con la TSA
			trozo = new Chunk();
			trozo.append("Datos de la TSA:");
			trozo.setFont(FUENTE_TITULO);
			frase = new Phrase(trozo);
							
			columna.setSimpleColumn(frase, 100, 185, 512, 185, 0, Element.ALIGN_LEFT);
			columna.go();
										
			trozo = new Chunk();
			trozo.append(nombreTSA);
			trozo.setFont(FUENTE_TEXTO);
			frase = new Phrase(trozo);

			columna.setSimpleColumn(frase, 100, 170, 512, 170, 0, Element.ALIGN_LEFT);
			columna.go();
			
			trozo = new Chunk();
			trozo.append("Número de serie: ");
			trozo.setFont(FUENTE_TEXTO);
			frase = new Phrase(trozo);

			trozo = new Chunk();
			trozo.append(nSerieTSA);
			trozo.setFont(FUENTE_TEXTO);
			frase.add(trozo);
			
			columna.setSimpleColumn(frase, 100, 155, 512, 155, 0, Element.ALIGN_LEFT);
			columna.go();

			trozo = new Chunk();
			trozo.append("Válido desde: ");
			trozo.setFont(FUENTE_TEXTO);
			frase = new Phrase(trozo);
			
			trozo = new Chunk();
			trozo.append(fechaInicioTSA);
			trozo.setFont(FUENTE_TEXTO);
			frase.add(trozo);
			
			trozo = new Chunk();
			trozo.append(" hasta: ");
			trozo.setFont(FUENTE_TEXTO);
			frase.add(trozo);
			
			trozo = new Chunk();
			trozo.append(fechaFinTSA);
			trozo.setFont(FUENTE_TEXTO);
			frase.add(trozo);
			
			columna.setSimpleColumn(frase, 100, 152, 512, 125, 13, Element.ALIGN_LEFT);
			columna.go();
						
			
			// Seguimos con el pie
			trozo = new Chunk();
			trozo.append(PIE);
			trozo.setFont(FUENTE_PIE);
			frase = new Phrase(trozo);
							
			columna.setSimpleColumn(frase, 80, 100, 515, 80, 10, Element.ALIGN_LEFT);
			columna.go();			

			// Firmamos el acuse
			
			// Obtenemos el almacen
			byte[] almacen = blobAlmacen.getBytes(1, (int) blobAlmacen.length());
			KeyStore ks = KeyStore.getInstance("PKCS12");			
			ks.load(new ByteArrayInputStream(almacen), clave.toCharArray());
			
			// Obtenemos la clave privada
			String alias = (String)ks.aliases().nextElement();
			PrivateKey key = (PrivateKey)ks.getKey(alias, clave.toCharArray());
			Certificate[] chain = ks.getCertificateChain(alias);
			
			// Firmamos
			PdfSignatureAppearance sap = stamper.getSignatureAppearance();
			sap.setCrypto(key, chain, null, PdfSignatureAppearance.WINCER_SIGNED);
			// comment next line to have an invisible signature
			sap.setVisibleSignature(new Rectangle(395, 535, 505, 630), 1, null);
			
			stamper.close();
			outStream.close();

			return blobComprobante;
		} catch (SQLException e) {
			String error = "Fallo al operar con la base de datos.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (IOException e) {
			String error = "Fallo al operar con los PDF's.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (DocumentException e) {
			String error = "Fallo al operar con el PDF del comprobante.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (KeyStoreException e) {
			String error = "Fallo al obtener el almacén.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (NoSuchAlgorithmException e) {
			String error = "Fallo cargar al almacén.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (CertificateException e) {
			String error = "Fallo cargar al almacén.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		} catch (UnrecoverableKeyException e) {
			String error = "Fallo al obtener la clave privada.";
			Logger.getLogger(Comprobante.class.getName()).log(Level.SEVERE, error, e);
			return null;
		}
	}
	
}
