package es.redmoon.pl.dsig.tests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import oracle.jdbc.OraclePreparedStatement;
import oracle.sql.BLOB;
import es.redmoon.pl.dsig.PDF;

public class TestPDFSignature {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Connection conn = null;
		OraclePreparedStatement objStatement = null;
		ResultSet rs = null;
		
		BLOB keystore = null;
		String password = null;
		BLOB document = null;
		
		FileOutputStream outStream = null;
		
		try {
			
			Class.forName("oracle.jdbc.driver.OracleDriver");

			System.out.println("Connecting with data base.");

			conn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.2.150:1521:orcl", "edocpki", "redmoon");

			objStatement = (OraclePreparedStatement) conn.prepareStatement("SELECT almacen, clave, documento FROM prueba WHERE id = 1");

			System.out.println("Getting parameters from data base.");
			rs = objStatement.executeQuery();

			if (!rs.next()) {
				System.out.println("There aren't values in data base.");
				System.exit(1);
			}
			
			keystore = (BLOB) rs.getBlob(1);
			password = rs.getString(2);
			document = (BLOB) rs.getBlob(3);
			
			BLOB signature = PDF.createSignature(keystore, password, document);
			
			if (signature != null) {
				
				System.out.println("Document signed.");
				
				File blobFile = new File("signedPDF.pdf"); 
				outStream = new FileOutputStream(blobFile); 
				InputStream inStream = signature.getBinaryStream(); 
				
				int length = -1; 
				int size = signature.getBufferSize(); 
				byte[] buffer = new byte[size]; 
				
				System.out.println("Saving signed PDF.");
				while ((length = inStream.read(buffer)) != -1) { 
					outStream.write(buffer, 0, length); 
					outStream.flush(); 
				}
			} else {
				System.out.println("Some error happened signing PDF document.");
			}
			
			System.out.println("End.");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (objStatement != null)
					objStatement.close();
				if (conn != null)
					conn.close();
				if (outStream != null)
					outStream.close();
				
			} catch (Exception e) {
				Logger.getLogger(TestPDFSignature.class.getName()).log(Level.SEVERE, "Error freeing resources.", e);
			}
		}

	}

}
