package es.redmoon.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import oracle.sql.BLOB;
import oracle.sql.CLOB;

/**
 * Class with utils for BLOB.
 * @author Ángel L. Garcia <angel@redmoon.es>
 *
 */
public class LOBUtils {
	
	/**
	 * Store a OutputStream in a BLOB.
	 * @param outputStream The OutputStream.
	 * @return A BLOB containing the OutputStream or null in case of error.
	 */
	public static BLOB OutputStreamToBLOB (OutputStream outputStream) {
		
		Connection conn = null;
		BLOB blob = null;
		
		OutputStream oStream = null;
		ByteArrayOutputStream baStream = null;
		
		try {
			//Class.forName("oracle.jdbc.driver.OracleDriver");
			//conn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.2.150:1521:orcl", "edocpki", "redmoon");
			
			conn = DriverManager.getConnection("jdbc:default:connection");
			
			// Create temporal BLOB
			blob = BLOB.createTemporary(conn, true, BLOB.DURATION_SESSION);
			
			// Write outputStream to temporal BLOB
			oStream = blob.setBinaryStream(1L);
			baStream = (ByteArrayOutputStream) outputStream;
			baStream.writeTo(oStream);
						
		} catch (SQLException e) {
			Logger.getLogger(LOBUtils.class.getName()).log(Level.SEVERE, "Error with database.", e);
			blob = null;
		} catch (UnsupportedEncodingException e) {
			Logger.getLogger(LOBUtils.class.getName()).log(Level.SEVERE, "Error getting document bytes.", e);
			blob = null;
		} catch (IOException e) {
			Logger.getLogger(LOBUtils.class.getName()).log(Level.SEVERE, "Error storing document.", e);
			blob = null;
		} /*catch (ClassNotFoundException e) {
			Logger.getLogger(LOBUtils.class.getName()).log(Level.SEVERE, "Error getting JDBC driver.", e);
			blob = null;
		} */finally {
			if (oStream != null) {
				try {
					oStream.close();
				} catch (IOException e) {
					Logger.getLogger(LOBUtils.class.getName()).log(Level.SEVERE, "Error freeing resources.", e);
				}
			}
			if (baStream != null) {
				try {
					baStream.close();
				} catch (IOException e) {
					Logger.getLogger(LOBUtils.class.getName()).log(Level.SEVERE, "Error freeing resources.", e);
				}
			}
		}
		
		return blob;
	}
	
	/**
	 * Store a InputStream in a BLOB.
	 * @param inputStream The InputStream.
	 * @return A BLOB containing the InputStream or null in case of error.
	 */
	public static BLOB InputStreamToBLOB (InputStream inputStream) {
		
		Connection conn = null;
		BLOB blob = null;
		
		OutputStream oStream = null;
		ByteArrayOutputStream buffer = null;
		
		try {
			//Class.forName("oracle.jdbc.driver.OracleDriver");
			//conn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.2.150:1521:orcl", "edocpki", "redmoon");
			
			conn = DriverManager.getConnection("jdbc:default:connection");
			
			// Create temporal BLOB
			blob = BLOB.createTemporary(conn, true, BLOB.DURATION_SESSION);
			
			// Write inputStream to ByteArrayOutputStream
			buffer = new ByteArrayOutputStream();

			int nRead;
			byte[] data = new byte[1000];

			while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
			  buffer.write(data, 0, nRead);
			}
			buffer.flush();
			
			// Write ByteArrayOutputStream to temporal BLOB
			oStream = blob.setBinaryStream(1L);
			buffer.writeTo(oStream);
			
		} catch (SQLException e) {
			Logger.getLogger(LOBUtils.class.getName()).log(Level.SEVERE, "Error with database.", e);
			blob = null;
		} catch (UnsupportedEncodingException e) {
			Logger.getLogger(LOBUtils.class.getName()).log(Level.SEVERE, "Error getting document bytes.", e);
			blob = null;
		} catch (IOException e) {
			Logger.getLogger(LOBUtils.class.getName()).log(Level.SEVERE, "Error storing document.", e);
			blob = null;
		} /*catch (ClassNotFoundException e) {
			Logger.getLogger(LOBUtils.class.getName()).log(Level.SEVERE, "Error getting JDBC driver.", e);
			blob = null;
		} */finally {
			if (buffer != null) {
				try {
					buffer.close();
				} catch (IOException e) {
					Logger.getLogger(LOBUtils.class.getName()).log(Level.SEVERE, "Error freeing resources.", e);
				}
			}
			if (oStream != null) {
				try {
					oStream.close();
				} catch (IOException e) {
					Logger.getLogger(LOBUtils.class.getName()).log(Level.SEVERE, "Error freeing resources.", e);
				}
			}
		}
		
		return blob;
	}
	
	/**
	 * Gets the CLOB's content as String. 
	 * @param clob The CLOB.
	 * @return String with CLOB's content or null in case of error.
	 */
	public static String CLOBToString(CLOB clob) {
		
		String strClob = null;
		
		if (clob == null) {
			strClob = "";
		} else {			
			try {
				strClob = clob.getSubString(1, (int)clob.length());
			} catch (SQLException e) {
				Logger.getLogger(LOBUtils.class.getName()).log(Level.SEVERE, "Error getting CLOB text.", e);
				strClob = null;
			}
		}
		return strClob;
	}

}
