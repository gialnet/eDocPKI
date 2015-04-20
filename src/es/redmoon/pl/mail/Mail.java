package es.redmoon.pl.mail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.UIDFolder;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import com.sun.mail.pop3.POP3Folder;

import es.redmoon.utils.LOBUtils;

import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleTypes;
import oracle.sql.BLOB;
import oracle.sql.CLOB;

public class Mail {

	/**
	 * Create the mail's content/body.
	 * @param type Type of mail, only sopported types related (html) and mixed.
	 * @param text The text of mail's body. Can be html (type related) or plain text (type mixed).
	 * @return BLOB containing mail' body or null in case of error. 
	 */
	public static BLOB createContent (String type, String text) {

		BLOB contentBLOB = null;
		ByteArrayOutputStream baos = null; 

		try {
			// Se compone la parte del texto
			BodyPart content = new MimeBodyPart();
			if (type.equalsIgnoreCase("related")) {
				content.setContent(text, "text/html");
				content.setHeader("Content-Type", "text/html");
			} else {
				content.setText(text);
			}

			MimeMultipart multiPart = new MimeMultipart(type);
			multiPart.addBodyPart(content);

			baos = new ByteArrayOutputStream();
			multiPart.writeTo(baos);

			contentBLOB = LOBUtils.OutputStreamToBLOB(baos);

		} catch (MessagingException e) {
			Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, "Problem creating mail content.", e);
			contentBLOB = null;
		} catch (IOException e) {
			Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, "Problem creating mail content.", e);
			contentBLOB = null;
		} catch (Exception e) {
			Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, "Problem creating mail content.", e);
			contentBLOB = null;
		} finally {
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
					Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, "Error freeing resources.", e);
				}
			}
		}

		return contentBLOB;
	}

	/**
	 * Create the mail's content/body.
	 * @param type Type of mail, only sopported types related (html) and mixed.
	 * @param text The text of mail's body. Can be html (type related) or plain text (type mixed).
	 * @return BLOB containing mail' body or null in case of error. 
	 */
	public static BLOB createContent (String type, CLOB text) {

		BLOB contentBLOB = null;
		ByteArrayOutputStream baos = null; 

		try {
			// Se compone la parte del texto
			BodyPart content = new MimeBodyPart();

			String strText = LOBUtils.CLOBToString(text);

			if (strText != null) {
				if (type.equalsIgnoreCase("related")) {
					content.setContent(strText, "text/html");
					content.setHeader("Content-Type", "text/html");
				} else {
					content.setText(strText);
				}

				MimeMultipart multiPart = new MimeMultipart(type);
				multiPart.addBodyPart(content);

				baos = new ByteArrayOutputStream();
				multiPart.writeTo(baos);

				contentBLOB = LOBUtils.OutputStreamToBLOB(baos);
			} else {
				contentBLOB = null;
			}

		} catch (MessagingException e) {
			Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, "Problem creating mail content.", e);
			contentBLOB = null;
		} catch (IOException e) {
			Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, "Problem creating mail content.", e);
			contentBLOB = null;
		} catch (Exception e) {
			Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, "Problem creating mail content.", e);
			contentBLOB = null;
		} finally {
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
					Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, "Error freeing resources.", e);
				}
			}
		}

		return contentBLOB;
	}

	/**
	 * Add attachment to mail's content.
	 * @param content The mail's content.
	 * @param type The type of mail. (related or mixed).
	 * @param attachment The attachment.
	 * @param name The attachment's name.
	 * @param mimeType The attachment's mime type. 
	 * @return BLOB containing mail's body and attachment or null in case of error.
	 */
	public static BLOB addAttachment (BLOB content, String type, BLOB attachment, String name, String mimeType) {

		BLOB contentBLOB = null;
		byte [] attachmentBytes;

		ByteArrayOutputStream baos = null;
		InputStream inStream = null;

		try {
			// Obtenemos los bytes del adjunto
			baos = new ByteArrayOutputStream();
			inStream = attachment.getBinaryStream(); 

			int length = -1; 
			int size = attachment.getBufferSize(); 
			byte[] buffer = new byte[size]; 

			while ((length = inStream.read(buffer)) != -1) { 
				baos.write(buffer, 0, length); 
				baos.flush(); 
			}
			attachmentBytes = baos.toByteArray();

			// Get BLOB'c content
			// Get BLOB's length
			length = (int) content.length();

			// Get bytes of the blob
			byte bytes[] = content.getBytes(1, length);

			DataSource ds = new ByteArrayDataSource(bytes, "multipart/"+type);

			MimeMultipart mp = new MimeMultipart(ds);

			MimeMultipart multiPart = new MimeMultipart(type);

			for (int i = 0; i < mp.getCount(); i ++)
				multiPart.addBodyPart(mp.getBodyPart(i));

			// Attacht new file
			BodyPart adjunto = new MimeBodyPart();
			adjunto.setDataHandler(new DataHandler(new ByteArrayDataSource(attachmentBytes, mimeType)));
			adjunto.setHeader("Content-Type", mimeType);
			adjunto.setHeader("Content-Transfer-Encoding", "base64");
			adjunto.setHeader("Content-Disposition", "attachment");
			adjunto.setFileName(name);

			multiPart.addBodyPart(adjunto);

			baos = new ByteArrayOutputStream();
			multiPart.writeTo(baos);

			contentBLOB = LOBUtils.OutputStreamToBLOB(baos);

		} catch (MessagingException e) {
			Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, "Problem adding attachment.", e);
			contentBLOB = null;
		} catch (IOException e) {
			Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, "Problem adding attachment.", e);
			contentBLOB = null;
		} catch (SQLException e) {
			Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, "Problem adding attachment.", e);
			contentBLOB = null;
		} catch (Exception e) {
			Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, "Problem adding attachment.", e);
			contentBLOB = null;
		} finally {
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
					Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, "Error freeing resources.", e);
				}
			}
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, "Error freeing resources.", e);
				}
			}
		}

		return contentBLOB;
	}

	/**
	 * Add image to mail's html content. The image can be linked in html source. For example: <img src="cid:name"\>. 
	 * @param content Mail's html content.
	 * @param image The image file.
	 * @param name The image's cid to link it. 
	 * @param name The image's name. 
	 * @param mimeType Image's mime type.
	 * @return BLOB containing mail's body and image or null in case of error.
	 */
	public static BLOB addHtmlImage (BLOB content, BLOB image, String cid, String name, String mimeType) {

		BLOB contentBLOB = null;
		byte [] attachmentBytes;

		ByteArrayOutputStream baos = null;
		InputStream inStream = null;

		try {
			// Obtenemos los bytes del adjunto
			baos = new ByteArrayOutputStream();
			inStream = image.getBinaryStream(); 

			int length = -1; 
			int size = image.getBufferSize(); 
			byte[] buffer = new byte[size]; 

			while ((length = inStream.read(buffer)) != -1) { 
				baos.write(buffer, 0, length); 
				baos.flush(); 
			}
			attachmentBytes = baos.toByteArray();

			// Se recupera el contenido del BLOB
			// Get the length of the blob
			length = (int) content.length();

			// Get bytes of the blob
			byte bytes[] = content.getBytes(1, length);

			DataSource ds = new ByteArrayDataSource(bytes, "multipart/related");

			MimeMultipart mp = new MimeMultipart(ds);

			MimeMultipart multiPart = new MimeMultipart("related");

			for (int i = 0; i < mp.getCount(); i ++)
				multiPart.addBodyPart(mp.getBodyPart(i));

			// Se adjunta el nuevo fichero
			BodyPart adjunto = new MimeBodyPart();
			adjunto.setDataHandler(new DataHandler(new ByteArrayDataSource(attachmentBytes, mimeType)));
			adjunto.setHeader("Content-Type", mimeType);
			adjunto.setHeader("Content-Transfer-Encoding", "base64");
			adjunto.setHeader("Content-ID", "<"+cid+">");
			adjunto.setHeader("Content-Disposition", "inline");
			adjunto.setFileName(name);

			multiPart.addBodyPart(adjunto);

			baos = new ByteArrayOutputStream();
			multiPart.writeTo(baos);

			contentBLOB = LOBUtils.OutputStreamToBLOB(baos);

		} catch (MessagingException e) {
			Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, "Problem adding html image.", e);
			contentBLOB = null;
		} catch (IOException e) {
			Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, "Problem adding html image.", e);
			contentBLOB = null;
		} catch (SQLException e) {
			Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, "Problem adding html image.", e);
			contentBLOB = null;
		} catch (Exception e) {
			Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, "Problem adding html image.", e);
			contentBLOB = null;
		} finally {
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
					Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, "Error freeing resources.", e);
				}
			}
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, "Error freeing resources.", e);
				}
			}
		}

		return contentBLOB;
	}

	/**
	 * Send a email.
	 * @param from The From header field.
	 * @param to A String with To recipients. If more than one address then must be comma separated sequence of addresses  
	 * @param cc A String with Cc recipients. If more than one address then must be comma separated sequence of addresses
	 * @param bcc A String with Bcc recipients. If more than one address then must be comma separated sequence of addresses
	 * @param subject The Subject header field.
	 * @param host The mail server.
	 * @param port The mail server port.
	 * @param security The security of server (none, ssl/tls, starttls).
	 * @param user The user name in the server. 
	 * @param passwd The password of the user.
	 * @param content The email's content. 
	 * @param type Type of mail, only sopported types related (html) and mixed.
	 * @return true if the message has been sent or false in case of error.
	 */
	public static boolean sendMessage (String from, String to, String cc, String bcc, String subject, String host, String port, String security, String user, String passwd, BLOB content, String type) {

		Properties props = new Properties();

		Transport t = null;
		
		boolean isSent = false;

		if (security.equalsIgnoreCase("STARTTLS")) {
			props.setProperty("mail.smtp.host", host);
			props.setProperty("mail.smtp.starttls.enable", "true");
			if (user != null && passwd != null && !user.trim().equals("") && !passwd.trim().equals("") ) {
				props.setProperty("mail.smtp.auth", "true");
			}

			props.setProperty("mail.smtp.port", port);

			props.setProperty("mail.smtp.connectiontimeout", "10000");
			props.setProperty("mail.smtp.timeout", "10000");

		} else if (security.equalsIgnoreCase("SSL/TLS")) {
			props.setProperty("mail.smtps.host", host);

			props.setProperty("mail.smtps.socketFactory.port", port);
			props.setProperty("mail.smtps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.setProperty("mail.transport.protocol", "smtps");

			// Insecure
			props.setProperty("mail.smtps.ssl.trust", "*");

			if (user != null && passwd != null && !user.trim().equals("") && !passwd.trim().equals("") ) {
				props.setProperty("mail.smtps.auth", "true");
			}

			props.setProperty("mail.smtps.port", port);

			props.setProperty("mail.smtps.connectiontimeout", "10000");
			props.setProperty("mail.smtps.timeout", "10000");

		} else {
			props.setProperty("mail.smtp.host", host);

			if (user != null && passwd != null && !user.trim().equals("") && !passwd.trim().equals("") ) {
				props.setProperty("mail.smtp.auth", "true");
			}

			props.setProperty("mail.smtp.port", port);

			props.setProperty("mail.smtp.connectiontimeout", "10000");
			props.setProperty("mail.smtp.timeout", "10000");
		}

		Session session = Session.getInstance(props, null);
		//session.setDebug(true);

		try {
			// Se recupera el contenido del BLOB
			// Get the length of the blob
			int length = (int) content.length();

			// Get bytes of the blob
			byte bytes[] = content.getBytes(1, length);

			DataSource ds = new ByteArrayDataSource(bytes, "multipart/"+type);

			MimeMultipart mp = new MimeMultipart(ds);

			MimeMultipart multiPart = new MimeMultipart(type);

			for (int i = 0; i < mp.getCount(); i ++) {
				multiPart.addBodyPart(mp.getBodyPart(i));
			}

			// Se compone el correo, dando to, from, subject y el
			// contenido.
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));

			if (to != null) {
				InternetAddress [] toAddresses = InternetAddress.parse(to);
				message.addRecipients(Message.RecipientType.TO, toAddresses);
			}

			if (cc != null) {
				InternetAddress [] ccAddresses = InternetAddress.parse(cc);
				message.addRecipients(Message.RecipientType.CC, ccAddresses);
			}

			if (bcc != null) {
				InternetAddress [] bccAddresses = InternetAddress.parse(bcc);
				message.addRecipients(Message.RecipientType.BCC, bccAddresses);
			}

			message.setSubject(subject);
			message.setContent(multiPart);

			// Se envia el correo.

			if (security.equalsIgnoreCase("SSL/TLS")) {
				t = session.getTransport("smtps");
			} else {
				t = session.getTransport("smtp");
			}

			if (user != null && passwd != null && !user.trim().equals("") && !passwd.trim().equals("") ) {
				t.connect(user, passwd);
			} else {
				t.connect();
			}

			t.sendMessage(message, message.getAllRecipients());

			isSent = true;

		} catch (AddressException e) {
			Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, "Problem sending mail.", e);
			isSent = false;
		} catch (MessagingException e) {
			Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, "Problem sending mail.", e);
			isSent = false;
		} catch (SQLException e) {
			Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, "Problem sending mail.", e);
			isSent = false;
		} catch (Exception e) {
			Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, "Problem sending mail.", e);
			isSent = false;
		} finally {
			if (t != null) {
				try {
					t.close();
				} catch (MessagingException e) {
					Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, "Error freeing resources.", e);
				}
			}
		}

		return isSent;
	}

	/**
	 * Load new mails from email account. 
	 * @param accountID Account's ID in email_received_header table. 
	 * @param folder Folder's name with mails to load in email account.
	 * @return Email loaded or -1 in case of error.
	 */
	public static int loadNewMails (long accountID, String folder) {
		
		Connection conn = null;
		OraclePreparedStatement preparedStatement = null;
		ResultSet rs = null;
		OracleCallableStatement callableStatement = null;
		
		String host;
		String port;
		String protocol;
		String security;
		String user;
		String passwd;
		
		int mailsLoaded = 0;
		String strProtocol = null;

		Properties props = new Properties();

		Store store = null;
		Folder mailFolder = null;		

		try {
			
			//Class.forName("oracle.jdbc.driver.OracleDriver");
			//conn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.2.150:1521:orcl", "edocpki", "redmoon");
			
			conn = DriverManager.getConnection("jdbc:default:connection");
			
			conn.setAutoCommit(false);
			
			preparedStatement = (OraclePreparedStatement) conn.prepareStatement("SELECT user_name, passwd, host, port, server_security, protocol FROM incoming_mail_configuration WHERE id = ?");
			
			preparedStatement.setLong(1, accountID);
			
			rs = preparedStatement.executeQuery();
			
			if (rs.next()) {
				
				
				user = rs.getString(1);
				passwd = rs.getString(2);
				host = rs.getString(3);
				port = rs.getString(4);
				security = rs.getString(5);
				protocol = rs.getString(6);
				
				
				if (protocol.equalsIgnoreCase("POP3")) {
					strProtocol = "pop3";
				} else if (protocol.equalsIgnoreCase("IMAP")) {
					strProtocol = "imap";
				} else {
					strProtocol = "unknown";
					mailsLoaded = -1;
				}

				if (security.equalsIgnoreCase("STARTTLS")) {
					props.setProperty("mail." + strProtocol + ".host", host);
					props.setProperty("mail." + strProtocol + ".port", port);
					props.setProperty("mail." + strProtocol + ".starttls.enable", "true");

					props.setProperty("mail." + strProtocol + ".connectiontimeout", "10000");
					props.setProperty("mail." + strProtocol + ".timeout", "10000");

				} else if (security.equalsIgnoreCase("SSL/TLS")) {

					strProtocol = strProtocol + "s";

					props.setProperty("mail." + strProtocol + ".host", host);
					props.setProperty("mail." + strProtocol + ".port", port);
					props.setProperty("mail." + strProtocol + ".socketFactory.class", "javax.net.ssl.SSLSocketFactory");
					props.setProperty("mail." + strProtocol + ".socketFactory.fallback", "false");
					props.setProperty("mail." + strProtocol + ".socketFactory.port", port);

					// Insecure
					props.setProperty("mail." + strProtocol + ".ssl.trust", "*");

					props.setProperty("mail." + strProtocol + ".connectiontimeout", "10000");
					props.setProperty("mail." + strProtocol + ".timeout", "10000");

				} else {
					props.setProperty("mail." + strProtocol + ".host", host);
					props.setProperty("mail." + strProtocol + ".port", port);

					props.setProperty("mail." + strProtocol + ".connectiontimeout", "10000");
					props.setProperty("mail." + strProtocol + ".timeout", "10000");
				}

				Session session = Session.getInstance(props);
				//session.setDebug(true);
				
				store = session.getStore(strProtocol);
				store.connect(host, user, passwd);

				mailFolder = store.getFolder(folder);
				mailFolder.open(Folder.READ_ONLY);
				
				Message[] messages = null;
				
				if (protocol.equalsIgnoreCase("IMAP")) {
					
					long maxUID = -1;
					
					preparedStatement = (OraclePreparedStatement) conn.prepareStatement("SELECT MAX(uid_imap) FROM email_recived_header WHERE id_account = ? HAVING COUNT(*) > 0");
					
					preparedStatement.setLong(1, accountID);
					
					rs = preparedStatement.executeQuery();
					
					if (rs.next()) {
						maxUID = rs.getLong(1);
					} else {
						maxUID = 0;
					}
					
					if (maxUID == 0) {
						// Si no hay ninguno guardado se obtienen todos
						messages = mailFolder.getMessages();
					} else {
						// Si ya hay se filtra
						messages = ((UIDFolder) mailFolder).getMessagesByUID(maxUID, UIDFolder.LASTUID);
						ArrayList <Message> messagesNotLoaded = new ArrayList<Message>();
						
						for (int i = 0; i < messages.length; i++){

							if (((UIDFolder) mailFolder).getUID(messages[i]) > maxUID) {
								messagesNotLoaded.add(messages[i]);
							}
						}
						
						messages = messagesNotLoaded.toArray(new Message[messagesNotLoaded.size()]);
					}

				} else if (protocol.equalsIgnoreCase("POP3")) {
					
					messages = mailFolder.getMessages();
					
					preparedStatement = (OraclePreparedStatement) conn.prepareStatement("SELECT uid_pop3 FROM email_recived_header WHERE id_account = ?");
					
					preparedStatement.setLong(1, accountID);
					
					rs = preparedStatement.executeQuery();
					
					ArrayList <String> pop3Messages = new ArrayList<String>();
					ArrayList <Message> messagesNotLoaded = new ArrayList<Message>();
					
					while (rs.next()) {
						pop3Messages.add(rs.getString(1));
					}
					boolean isLoaded = false;
					
					for (int i = 0; i < messages.length; i++){
						isLoaded = false;
						for (String pop3UID: pop3Messages) {
							if ( pop3UID.trim().equals ( ((POP3Folder)mailFolder).getUID(messages[i]).trim()) ) {
								isLoaded = true;
								break;
							}
						}
						
						if (!isLoaded) {
							messagesNotLoaded.add(messages[i]);
						}
					}
					
					messages = messagesNotLoaded.toArray(new Message[messagesNotLoaded.size()]);
				}
				
				String from = null;
	        	String subject = null;
	        	Date sentDate = null;
	        	String uidPop3 = null;
	        	long uidImap = -1;
	        	
	        	SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
	        	
	        	long headerID;
	        	
				for (int i = 0; i < messages.length; i++) {
					
		        	from = messages[i].getFrom()[0].toString();
		        	subject = messages[i].getSubject();
		        	sentDate = messages[i].getSentDate();
		        	
		        	if (protocol.equalsIgnoreCase("IMAP")) {
		        		uidImap = ((UIDFolder) mailFolder).getUID(messages[i]);
		        	} else if (protocol.equalsIgnoreCase("POP3")){
		        		uidPop3 = ((POP3Folder)mailFolder).getUID(messages[i]).trim();
		        	}
		        	
		        	String query = "BEGIN INSERT INTO email_recived_header(id_account, folder, from_address, subject, sent_date, uid_pop3, uid_imap) VALUES (?, ?, ?, ?, to_date(? ,'DD/MM/YYYY HH24:MI:SS'), ?, ?) RETURNING id INTO ?; END;";
		        	
		        	callableStatement = (OracleCallableStatement) conn.prepareCall(query);
		        	
		        	callableStatement.setLong(1, accountID);
		        	callableStatement.setString(2, folder);
		        	callableStatement.setString(3, from);
		        	callableStatement.setString(4, subject);
		        	callableStatement.setString(5, sd.format(sentDate));
		        	callableStatement.setString(6, uidPop3);
		        	callableStatement.setLong(7, uidImap);
		        	
		        	callableStatement.registerOutParameter(8, OracleTypes.NUMBER);
		        	callableStatement.execute();
		        	headerID = callableStatement.getLong(8);
		        	
		        	// Procesamos las partes del mensaje
		        	
		        	if (!parseMessagePart(messages[i], conn, headerID) ) {
		        		throw new Exception("Error pharsing array of messages.");
		        	}
		        	
		        	if (conn != null) {
		        		conn.commit();
		        	}
		        	
		        	mailsLoaded++;
		        }
			}

		} catch (NoSuchProviderException e) {
			Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, "Problem loading mails.", e);
			mailsLoaded = -1;
			
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} catch (MessagingException e) {
			Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, "Problem loading mails.", e);
			mailsLoaded = -1;
			
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} catch (SQLException e) {
			Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, "Problem loading mails.", e);
			mailsLoaded = -1;
			
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} catch (Exception e) {
			Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, "Problem loading mails.", e);
			mailsLoaded = -1;
			
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} finally {
			if (mailFolder != null && mailFolder.isOpen()) {
				try {
					mailFolder.close(true);
				} catch (MessagingException e) {
					Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, "Error freeing resources.", e);
				}
			}

			if (store != null) {
				try {
					store.close();
				} catch (MessagingException e) {
					Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, "Error freeing resources.", e);
				}
			}
			
			if (rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, "Error freeing resources.", e);
				}
			}
			
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, "Error freeing resources.", e);
				}
			}
			if (callableStatement != null) {
				try {
					callableStatement.close();
				} catch (SQLException e) {
					Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, "Error freeing resources.", e);
				}
			}
		}

		return mailsLoaded;
	}
	
	
	private static boolean parseMessagePart(Part message, Connection conn, long headerID) {
		
		OraclePreparedStatement preparedStatement = null;

		InputStream is = null;
    	OutputStream os = null;
    	
		boolean isOK = false;
		
		try {
			// Si es multipart, se analiza cada una de sus partes recursivamente.
			if (message.isMimeType("multipart/*")) {
				
				Multipart mp;
				
				mp = (Multipart) message.getContent();
				
				for (int i = 0; i < mp.getCount(); i++) {
					if (!parseMessagePart(mp.getBodyPart(i), conn, headerID)) {
						throw new Exception("Error pharsing message content.");
					}
				}
			} else {
				
				String contentType = null;
				String contentID = null;
				String fileName = null;
				String contentDisposition = null;
				
				contentDisposition = message.getDisposition();
				// Es ya un "objeto final" hay que ver el tipo, INLINE, ATTACHMENT, cuerpo
				// Si es null comprobar si content id es != null y el mime es image => inline
				if (contentDisposition == null) {
					if (message.isMimeType("image/*") && ((MimeBodyPart)message).getContentID() != null) {
						
						contentType = message.getContentType();
						contentID = ((MimeBodyPart)message).getContentID();
						if (contentID != null) {
							contentID = contentID.replaceAll("<|>", "");
						}
						fileName = message.getFileName();
						contentDisposition = Part.INLINE;
						
						preparedStatement = (OraclePreparedStatement) conn.prepareStatement("INSERT INTO email_recived_content(id_header, content, content_type, content_id, content_disposition, file_name) VALUES (?, ?, ?, ?, ?, ?)");

						BLOB blobContent = BLOB.createTemporary(conn, true, BLOB.DURATION_SESSION);

	                	is = message.getInputStream();
	                	os = blobContent.setBinaryStream(1L);
	                	
	                	int length = -1; 
	    				int size = 10000; 
	    				byte[] buffer = new byte[size]; 
	    				
	    				while ((length = is.read(buffer)) != -1) { 
	    					os.write(buffer, 0, length); 
	    					os.flush(); 
	    				}

	        			preparedStatement.setLong(1, headerID);
	        			preparedStatement.setBLOB(2, blobContent);
	        			preparedStatement.setString(3, contentType);
	        			preparedStatement.setString(4, contentID);
	        			preparedStatement.setString(5, contentDisposition);
	        			preparedStatement.setString(6, fileName);
						
	        			preparedStatement.execute();
						
					} else {
						
						contentType = message.getContentType();
						contentID = null;
						fileName = message.getFileName();
						
						preparedStatement = (OraclePreparedStatement) conn.prepareStatement("INSERT INTO email_recived_content(id_header, content, content_type, content_id, content_disposition, file_name) VALUES (?, ?, ?, ?, ?, ?)");

						BLOB blobContent = BLOB.createTemporary(conn, true, BLOB.DURATION_SESSION);

	                	is = new ByteArrayInputStream(message.getContent().toString().getBytes());
	                	os = blobContent.setBinaryStream(1L);
	                	
	                	int length = -1; 
	    				int size = 10000; 
	    				byte[] buffer = new byte[size]; 
	    				
	    				while ((length = is.read(buffer)) != -1) { 
	    					os.write(buffer, 0, length); 
	    					os.flush(); 
	    				}
	                	
	        			preparedStatement.setLong(1, headerID);
	        			preparedStatement.setBLOB(2, blobContent);
	        			preparedStatement.setString(3, contentType);
	        			preparedStatement.setString(4, contentID);
	        			preparedStatement.setString(5, contentDisposition);
	        			preparedStatement.setString(6, fileName);
						
	        			preparedStatement.execute();
						
					}
					// Guardar las diferentes partes
				} else if (contentDisposition.equalsIgnoreCase(Part.INLINE)) {
					
					contentType = message.getContentType();
					contentID = ((MimeBodyPart)message).getContentID();
					if (contentID != null) {
						contentID = contentID.replaceAll("<|>", "");
					}
					fileName = message.getFileName();
					
					preparedStatement = (OraclePreparedStatement) conn.prepareStatement("INSERT INTO email_recived_content(id_header, content, content_type, content_id, content_disposition, file_name) VALUES (?, ?, ?, ?, ?, ?)");

					BLOB blobContent = BLOB.createTemporary(conn, true, BLOB.DURATION_SESSION);

                	is = message.getInputStream();
                	os = blobContent.setBinaryStream(1L);
                	
                	int length = -1; 
    				int size = 10000; 
    				byte[] buffer = new byte[size]; 
    				
    				while ((length = is.read(buffer)) != -1) { 
    					os.write(buffer, 0, length); 
    					os.flush(); 
    				}
    				
        			preparedStatement.setLong(1, headerID);
        			preparedStatement.setBLOB(2, blobContent);
        			preparedStatement.setString(3, contentType);
        			preparedStatement.setString(4, contentID);
        			preparedStatement.setString(5, contentDisposition);
        			preparedStatement.setString(6, fileName);
					
        			preparedStatement.execute();
					
				} else if (contentDisposition.equalsIgnoreCase(Part.ATTACHMENT)) {
					
					contentType = message.getContentType();
					contentID = ((MimeBodyPart)message).getContentID();
					if (contentID != null) {
						contentID = contentID.replaceAll("<|>", "");
					}
					fileName = message.getFileName();
					
					preparedStatement = (OraclePreparedStatement) conn.prepareStatement("INSERT INTO email_recived_content(id_header, content, content_type, content_id, content_disposition, file_name) VALUES (?, ?, ?, ?, ?, ?)");

					BLOB blobContent = BLOB.createTemporary(conn, true, BLOB.DURATION_SESSION);

                	is = message.getInputStream();
                	os = blobContent.setBinaryStream(1L);
                	
                	int length = -1; 
    				int size = 10000; 
    				byte[] buffer = new byte[size]; 
    				
    				while ((length = is.read(buffer)) != -1) { 
    					os.write(buffer, 0, length); 
    					os.flush(); 
    				}
    				
        			preparedStatement.setLong(1, headerID);
        			preparedStatement.setBLOB(2, blobContent);
        			preparedStatement.setString(3, contentType);
        			preparedStatement.setString(4, contentID);
        			preparedStatement.setString(5, contentDisposition);
        			preparedStatement.setString(6, fileName);
					
        			preparedStatement.execute();
        			
				}
			}
			
			isOK = true;
			
		} catch (MessagingException e) {
			Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, "Problem loading mail's content.", e);
			isOK = false;
			
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} catch (IOException e) {
			Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, "Problem loading mail's content.", e);
			isOK = false;
			
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} catch (SQLException e) {
			Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, "Problem loading mail's content.", e);
			isOK = false;
			
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} catch (Exception e) {
			Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, "Problem loading mail's content.", e);
			isOK = false;
			
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} finally {
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, "Error freeing resources.", e);
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, "Error freeing resources.", e);
				}
			}
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, "Error freeing resources.", e);
				}
			}
		}
		
		return isOK;
	}
}
