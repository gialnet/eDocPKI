package es.redmoon.pl.dsig.tests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.jdbc.OraclePreparedStatement;
import oracle.sql.BLOB;
import es.redmoon.pl.dsig.XAdES;
import es.redmoon.utils.XML;

public class TestXAdESTEnvelopedSignature {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		BLOB keystore = null;
		String keyStoreType = null;
		String keyStorePassword = null;
		String privateKeyPassword = null;
		BLOB document = null;
		String name = null;
		String description = null;
		String mime = null;
		String tsaUrl = null;
		String tsaUser = null;
		String tsaPassword = null;
		
		Connection conDCyC = null;
		OraclePreparedStatement objStatement = null;
		ResultSet rs = null;

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");

			System.out.println("Connecting with data base.");

			conDCyC = DriverManager.getConnection("jdbc:oracle:thin:@192.168.2.150:1521:orcl", "edocpki", "redmoon");

			objStatement = (OraclePreparedStatement) conDCyC.prepareStatement("SELECT almacen, clave, documento, nombre, descripcion, mime, private_key_password, keystore_type, tsa, tsa_user, tsa_password FROM prueba WHERE id = 1");
			//objStatement = (OraclePreparedStatement) conDCyC.prepareStatement("SELECT almacen, clave, documento, nombre, descripcion, mime, private_key_password, keystore_type, tsa, tsa_user, tsa_password FROM prueba WHERE id = 2");
			
			System.out.println("Getting parameters from data base.");
			rs = objStatement.executeQuery();

			if (!rs.next()) {
				System.out.println("There aren't values in data base.");
				System.exit(1);
			}
			
			keystore = (BLOB) rs.getBlob(1);
			keyStorePassword = rs.getString(2);
			document = (BLOB) rs.getBlob(3);
			name = rs.getString(4);
			description = rs.getString(5);
			mime = rs.getString(6);
			privateKeyPassword = rs.getString(7);
			keyStoreType = rs.getString(8);
			
			tsaUrl = rs.getString(9);
			tsaUser = rs.getString(10);
			tsaPassword = rs.getString(11);
			
			System.out.println("Signing document.");

			BLOB signature = XAdES.createEnvelopedBES_T(keystore, keyStoreType, keyStorePassword, privateKeyPassword, document, name, description, mime, tsaUrl, tsaUser, tsaPassword);
			
			XML.saveDocumentToFile(XML.getDocumentFromBLOB(signature), "./xadesEnveloped-T.xsig");
			//XML.saveDocumentToFile(XML.getDocumentFromBLOB(signature), "./xadesEnveloped-T_Authenticated.xsig");
			
			System.out.println("End.");
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(1);
		} finally {
			
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (objStatement != null) {
				try {
					objStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conDCyC != null) {
				try {
					conDCyC.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
