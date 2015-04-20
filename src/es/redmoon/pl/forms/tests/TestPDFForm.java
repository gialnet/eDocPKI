package es.redmoon.pl.forms.tests;

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
import es.redmoon.pl.forms.PDFForm;

public class TestPDFForm {

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
			
			objStatement = (OraclePreparedStatement) conn.prepareStatement("SELECT formulario, xml_datos FROM formularios WHERE id = 1");
			
			System.out.println("Getting form from data base.");
			rs = objStatement.executeQuery();
			
			if (!rs.next()) {
				System.out.println("There aren't values in data base.");
				System.exit(1);
			} else {
				
				BLOB formulario = (BLOB) rs.getBlob(1);
				BLOB xmlDatos =  (BLOB) rs.getBlob(2);
				
				// Obtenemos la firma del original
				System.out.println("Pharse the form.");
				
				BLOB xmlCampos = PDFForm.getFormFields(formulario);
				
				if (xmlCampos != null) {
					System.out.println("Fields gotten.");
					
					File blobFile = new File("./formFields.xml"); 
					outStream = new FileOutputStream(blobFile); 
					InputStream inStream = xmlCampos.getBinaryStream(); 
					
					int length = -1; 
					int size = xmlCampos.getBufferSize(); 
					byte[] buffer = new byte[size]; 
					
					System.out.println("Saving fields.");
					while ((length = inStream.read(buffer)) != -1) { 
						outStream.write(buffer, 0, length); 
						outStream.flush(); 
					}
				} else {
					System.out.println("Some error happened getting fields.");
				}
				
				System.out.println("Filling form.");
				
				BLOB formularioRelleno = PDFForm.fillForm(formulario, xmlDatos);
				
				if (formularioRelleno != null) {
					File blobFile = new File("filledForm.pdf"); 
					outStream = new FileOutputStream(blobFile); 
					InputStream inStream = formularioRelleno.getBinaryStream(); 
					
					int length = -1; 
					int size = formularioRelleno.getBufferSize(); 
					byte[] buffer = new byte[size]; 
					
					System.out.println("Saving filled form.");
					while ((length = inStream.read(buffer)) != -1) { 
						outStream.write(buffer, 0, length); 
						outStream.flush(); 
					}
				} else {
					System.out.println("Some error happened filling form.");
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
				Logger.getLogger(TestPDFForm.class.getName()).log(Level.SEVERE, "Error freeing resources.", e);
			}
		}

	}

}
