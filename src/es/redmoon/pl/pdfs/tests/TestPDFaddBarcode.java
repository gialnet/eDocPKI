package es.redmoon.pl.pdfs.tests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.redmoon.pl.pdfs.PDF;

import oracle.jdbc.OraclePreparedStatement;
import oracle.sql.BLOB;

public class TestPDFaddBarcode {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Connection conn = null;
		
		OraclePreparedStatement objStatement = null;
		ResultSet rs = null;
		
		FileOutputStream outStream = null;
		
		try {

			Class.forName("oracle.jdbc.driver.OracleDriver");

			System.out.println("Connecting to data base.");
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.2.150:1521:orcl", "edocpki", "redmoon");
			
			objStatement = (OraclePreparedStatement) conn.prepareStatement("SELECT template FROM pdf_templates WHERE id = 1");
			
			System.out.println("Getting original from data base.");
			rs = objStatement.executeQuery();
			
			if (!rs.next()) {
				System.out.println("There aren't values in data base.");
				System.exit(1);
			} else {
				
				BLOB original = (BLOB) rs.getBlob(1);
				
				//BLOB pdfWithImage = PDF.addBarcode(original, "PDF417", 300, 200, 50,"Hello world", 150, 75);
				//BLOB pdfWithImage = PDF.addBarcode(original, "QR", 300, 75, 75, "Hello world", 400, 275);
				//BLOB pdfWithImage = PDF.addBarcode(original, "CODE39", 200, 0, 0, "Hello world", 0, 275);
				//BLOB pdfWithImage = PDF.addBarcode(original, "CODE128", 270, 0, 0, "Hello world", 0, 350);
				BLOB pdfWithImage = PDF.addBarcode(original, "DATAMATRIX", 300, 0, 0, "Hello world", 425, 175);
				
				if (pdfWithImage != null) {
					
					//File pdfFile = new File("pdfwithBarcodePDF417.pdf");
					//File pdfFile = new File("pdfwithBarcodeQR.pdf");
					//File pdfFile = new File("pdfwithBarcodeCODE39.pdf");
					//File pdfFile = new File("pdfwithBarcodeCODE128.pdf");
					File pdfFile = new File("pdfwithBarcodeDATAMATRIX.pdf");
					
					outStream = new FileOutputStream(pdfFile); 
					InputStream inStream = pdfWithImage.getBinaryStream(); 
					
					int length = -1; 
					int size = pdfWithImage.getBufferSize(); 
					byte[] buffer = new byte[size]; 
					
					System.out.println("Saving pdf.");
					while ((length = inStream.read(buffer)) != -1) { 
						outStream.write(buffer, 0, length); 
						outStream.flush(); 
					}
				} else {
					System.out.println("Some error happened savinf pdf.");
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
				Logger.getLogger(TestPDFaddBarcode.class.getName()).log(Level.SEVERE, "Error freeing resources.", e);
			}
		}

	}

}
