package es.redmoon.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Clase with utils for Streams
 * @author Ángel L. Garcia <angel@redmoon.es>
 * @version 0.1
 */
public class Stream {
	
	/**
	 * Gets byte array from InputStream.
	 * @param is The InputStream.
	 * @return Byte array containing the InputStream.
	 * @throws IOException If happened some Input/Output error.
	 */
	public static byte[] toByteArray(InputStream is) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[1000];

		while ((nRead = is.read(data, 0, data.length)) != -1) {
		  buffer.write(data, 0, nRead);
		}
		buffer.flush();

		return buffer.toByteArray();
	}

	/**
	 * Gets InputStream from byte array.
	 * @param datos The byte array.
	 * @return The InputStream containig byte array.
	 * @throws IOException If happened some Input/Output error.
	 */
	public static InputStream fromByteArray(byte[] datos) throws IOException {
		InputStream is = new ByteArrayInputStream(datos);
		return is;
	}
}
