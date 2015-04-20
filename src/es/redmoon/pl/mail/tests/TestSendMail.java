package es.redmoon.pl.mail.tests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import es.redmoon.pl.mail.Mail;

import oracle.jdbc.OraclePreparedStatement;
import oracle.sql.BLOB;
import oracle.sql.CLOB;

public class TestSendMail {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Connection conn = null;
		OraclePreparedStatement objStatement = null;
		ResultSet rs = null;
		
		BLOB document = null;
		BLOB image = null;
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			System.out.println("Connecting with data base.");

			conn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.2.150:1521:orcl", "edocpki", "redmoon");
			
			/*
			 * ######################################################
			 * Create body
			 * ######################################################
			 */
			objStatement = (OraclePreparedStatement) conn.prepareStatement("SELECT cuerpo_mail FROM prueba WHERE id = 5");

			System.out.println("Getting body from data base.");
			rs = objStatement.executeQuery();

			if (!rs.next()) {
				System.out.println("There aren't values in data base.");
				System.exit(1);
			}
			
			CLOB html = (CLOB) rs.getClob(1);
			
			BLOB content = Mail.createContent("related", html);
			
			/*
			 * ######################################################
			 * Attacht first document
			 * ######################################################
			 */
			objStatement = (OraclePreparedStatement) conn.prepareStatement("SELECT documento FROM prueba WHERE id = 1");

			System.out.println("Getting first document from data base.");
			rs = objStatement.executeQuery();

			if (!rs.next()) {
				System.out.println("There aren't values in data base.");
				System.exit(1);
			}
			
			document = (BLOB) rs.getBlob(1);
			
			content = Mail.addAttachment(content, "related", document, "ejemplo.pdf", "application/pdf");
			
			/*
			 * ######################################################
			 * Add first html image
			 * ######################################################
			 */
			objStatement = (OraclePreparedStatement) conn.prepareStatement("SELECT documento FROM prueba WHERE id = 4");

			System.out.println("Getting first image from data base.");
			rs = objStatement.executeQuery();

			if (!rs.next()) {
				System.out.println("There aren't values in data base.");
				System.exit(1);
			}
			
			image = (BLOB) rs.getBlob(1);
			
			content = Mail.addHtmlImage (content, image, "figura1", "gato.gif", "image/gif");
			
			/*
			 * ######################################################
			 * Attacht second document
			 * ######################################################
			 */
			objStatement = (OraclePreparedStatement) conn.prepareStatement("SELECT documento FROM prueba WHERE id = 2");

			System.out.println("Getting parameters from data base.");
			rs = objStatement.executeQuery();

			if (!rs.next()) {
				System.out.println("There aren't values in data base.");
				System.exit(1);
			}
			
			document = (BLOB) rs.getBlob(1);
			
			content = Mail.addAttachment(content, "related", document, "presentacion.pdf", "application/pdf");
			
			/*
			 * ######################################################
			 * Add second html image
			 * ######################################################
			 */
			objStatement = (OraclePreparedStatement) conn.prepareStatement("SELECT documento FROM prueba WHERE id = 5");

			System.out.println("Getting second document from data base.");
			rs = objStatement.executeQuery();

			if (!rs.next()) {
				System.out.println("There aren't values in data base.");
				System.exit(1);
			}
			
			image = (BLOB) rs.getBlob(1);
			
			content = Mail.addHtmlImage (content, image, "bidon", "bidon.jpg", "image/jpeg");
			
			/*
			 * ######################################################
			 * Send mail
			 * ######################################################
			 */
			objStatement = (OraclePreparedStatement) conn.prepareStatement("SELECT * FROM outgoing_mail_configuration WHERE id = 1");

			System.out.println("Getting second image from data base.");
			rs = objStatement.executeQuery();

			if (!rs.next()) {
				System.out.println("There aren't values in data base.");
				System.exit(1);
			}
			
			String from = rs.getString("email");
			String host = rs.getString("host");
			String port = rs.getString("port");
			String user = rs.getString("user_name");
			String passwd = rs.getString("passwd");
			String security = rs.getString("server_security");
			
			String to = from;
			String cc = null;
			String bcc = null;
			
			String subject = "Javamail test";
			
			System.out.println("Sending mail.");
			if (Mail.sendMessage(from, to, cc, bcc, subject, host, port, security, user, passwd, content, "related")) {
				System.out.println("Sent ok.");
			} else {
				System.out.println("Cann't be sent.");
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
