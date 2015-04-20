package es.redmoon.pl.ts.tests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import oracle.jdbc.OraclePreparedStatement;
import oracle.sql.BLOB;

import es.redmoon.pl.dsig.tests.TestPDFSignature;
import es.redmoon.pl.ts.TimeStamp;

public class TimeStampTest {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Connection conn = null;
		OraclePreparedStatement objStatement = null;
		ResultSet rs = null;
		
		BLOB document = null;
		
		FileOutputStream outStream = null;
		
		try {
			
			Class.forName("oracle.jdbc.driver.OracleDriver");

			System.out.println("Connecting with data base.");

			conn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.2.150:1521:orcl", "edocpki", "redmoon");

			objStatement = (OraclePreparedStatement) conn.prepareStatement("SELECT documento FROM prueba WHERE id = 1");

			System.out.println("Getting parameters from data base.");
			rs = objStatement.executeQuery();

			if (!rs.next()) {
				System.out.println("There aren't values in data base.");
				System.exit(1);
			}
			
			document = (BLOB) rs.getBlob(1);
			
			
			byte[] data = document.getBytes(1, (int)document.length());
			
			MessageDigest digester;
			digester = MessageDigest.getInstance("SHA-1");
			digester.update(data);

			byte[] digest = digester.digest();
			
			
			BLOB timeStamp = TimeStamp.createTimeStamp(digest, "SHA-1", "http://tss.accv.es:8318/ts");
			//BLOB timeStamp = TimeStamp.createXMLTimeStamp(digest, "SHA-1", "http://tss.accv.es:8318/ts");
			//BLOB timeStamp = TimeStamp.createAuthenticatedTimeStamp(digest, "SHA-1", "http://tsa.acedicom.edicomgroup.com:9026/", "B18874941", "qvgsdlncx");
			//BLOB timeStamp = TimeStamp.createXMLAuthenticatedTimeStamp(digest, "SHA-1", "http://tsa.acedicom.edicomgroup.com:9026/", "B18874941", "qvgsdlncx");
			
			if (timeStamp != null) {
				
				System.out.println("TimeStamp created.");
				
				System.out.println("Serial number: " + TimeStamp.getSerialNumber(timeStamp));
				System.out.println("Gen Time: " + TimeStamp.getGenTime(timeStamp));
				
				//File blobFile = new File("TimeStamp.tsr");
				//File blobFile = new File("TimeStamp.xml");
				//File blobFile = new File("AuthenticatedTimeStamp.tsr");
				File blobFile = new File("AuthenticatedTimeStamp.xml");
				
				outStream = new FileOutputStream(blobFile); 
				InputStream inStream = timeStamp.getBinaryStream(); 
				
				int length = -1; 
				int size = timeStamp.getBufferSize(); 
				byte[] buffer = new byte[size]; 
				
				System.out.println("Saving timeStamp.");
				while ((length = inStream.read(buffer)) != -1) { 
					outStream.write(buffer, 0, length); 
					outStream.flush(); 
				}
			} else {
				System.out.println("Some error happened doing timestamp.");
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
