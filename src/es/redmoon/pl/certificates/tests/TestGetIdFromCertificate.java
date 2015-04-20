package es.redmoon.pl.certificates.tests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import oracle.jdbc.OraclePreparedStatement;
import oracle.sql.BLOB;
import es.redmoon.pl.certificates.Certificate;

public class TestGetIdFromCertificate {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {

			Class.forName("oracle.jdbc.driver.OracleDriver");

			System.out.println("Connecting with data base.");
			
			Connection conCertificado = DriverManager.getConnection("jdbc:oracle:thin:@192.168.2.150:1521:orcl", "edocpki", "redmoon");
			
			//OraclePreparedStatement objStatement = (OraclePreparedStatement) conCertificado.prepareStatement("SELECT certificado FROM firmas WHERE id = 1");
			//OraclePreparedStatement objStatement = (OraclePreparedStatement) conCertificado.prepareStatement("SELECT certificado FROM firmas WHERE id = 2");
			OraclePreparedStatement objStatement = (OraclePreparedStatement) conCertificado.prepareStatement("SELECT certificado FROM firmas WHERE id = 4");
			
			System.out.println("Getting certificate from data base.");
			ResultSet rs = objStatement.executeQuery();
			
			if (!rs.next()) {
				System.out.println("There aren't values in data base.");
				
			} else {
				
				BLOB original = (BLOB) rs.getBlob(1); 
				
				System.out.println("El Id de usuario es: " + Certificate.getIDFromCertificate(original));
			}		
									
			conCertificado.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
