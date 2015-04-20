package es.redmoon.pl.amazon.tests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.redmoon.pl.amazon.S3;

import oracle.jdbc.OraclePreparedStatement;
import oracle.sql.BLOB;

public class TestS3 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Connection conn = null;
		
		OraclePreparedStatement objStatement = null;
		ResultSet rs = null;
		
		FileOutputStream outStream = null;
		
		String accessKey = "0H8T70J0T1AJFR4WQGR2";
		String secretKey = "UgzE0fxyyWG4Oc1s64ehib8jzDcKoz3FGiCZZyH9";
		String bucketName = "test-angel";
		String objectKey = "originalFile";
		
		try {

			Class.forName("oracle.jdbc.driver.OracleDriver");

			System.out.println("Connecting to data base.");
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.2.150:1521:orcl", "edocpki", "redmoon");
			
			objStatement = (OraclePreparedStatement) conn.prepareStatement("SELECT documento FROM prueba WHERE id = 1");
			
			System.out.println("Getting document from data base.");
			rs = objStatement.executeQuery();
			
			if (!rs.next()) {
				System.out.println("There aren't values in data base.");
				System.exit(1);
			} else {
				
				BLOB original = (BLOB) rs.getBlob(1);
				
				boolean result = S3.createBucket(accessKey, secretKey, bucketName);
				
				if (result) {
					
					result = S3.putObject(accessKey, secretKey, bucketName, original, objectKey);
					
					if (result) {
						
						BLOB s3Object = S3.getObject(accessKey, secretKey, bucketName, objectKey);
						if (s3Object != null) {
							
							File pdfFile = new File("s3Object.pdf"); 
							outStream = new FileOutputStream(pdfFile); 
							InputStream inStream = s3Object.getBinaryStream(); 
							
							int length = -1; 
							int size = s3Object.getBufferSize(); 
							byte[] buffer = new byte[size]; 
							
							System.out.println("Saving pdf.");
							while ((length = inStream.read(buffer)) != -1) { 
								outStream.write(buffer, 0, length); 
								outStream.flush(); 
							}
							
							result = S3.deleteObject(accessKey, secretKey, bucketName, objectKey);
							
							if (result) {
								result = S3.deleteBucket(accessKey, secretKey, bucketName);
								if (!result) {
									System.out.println ("Error deleting bucket.");
								}
							} else {
								System.out.println ("Error deleting object.");
							}
						} else {
							System.out.println ("Error getting object.");
						}
					} else {
						System.out.println ("Error putting object.");
					}
				} else {
					System.out.println ("Error creating bucket.");
				}
				
				System.out.println("End.");
			}		

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
				Logger.getLogger(TestS3.class.getName()).log(Level.SEVERE, "Error freeing resources.", e);
			}
		}

	}

}
