package es.redmoon.pl.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Class for whois utils.
 * @version 0.1
 */
public class Whois
{
	private static String IANA = "whois.iana.org"; // Global
	
	private static int PORT = 43;
	

	/**
	 * Get IP's country.
	 * @param ip IP to get country.
	 * @return The country or null in case of error.
	 */
	public static String getCountry(String ip){
		
		String country = null;
		// Obtenemos el servidor al que preguntar
		String server = getServer(ip);
		Socket socket = null;
		PrintStream out = null;
		BufferedReader in = null;
		
		boolean exit = false;
		
		if (server == null) {
			country = null;
		} else {
			try {
				// Establecemos conexión con el servidor.
				socket = new Socket(server, PORT);
				out = new PrintStream(socket.getOutputStream());
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String linea = "";

				// Enviamos la ip al servidor
				out.println(ip);

				// Buscamos el pais en la respuesta.
				// Debe ser algo como: "country:    ES"
				Pattern p = Pattern.compile("country:(\\s+)(.+)", Pattern.CASE_INSENSITIVE);
				Matcher m = null;
							
				// Vamos tratando cada una de las lineas de la respuesta
				while ((linea = in.readLine()) != null && !exit)
				{
					m = p.matcher(linea);
					
					// Si se ha encontrado el patron
					if (m.find()){
						country = m.group(2);
						exit = true;
					} else {
						country = null;
					}
				}

			} catch (UnknownHostException e) {
				String error = "Server not found: " + server + ".";
				Logger.getLogger(Whois.class.getName()).log(Level.SEVERE, error, e);
				country = null;
			} catch (IOException e) {
				String error = "Error parsing server response: " + server + ".";
				Logger.getLogger(Whois.class.getName()).log(Level.SEVERE, error, e);
				country = null;
			} finally {
				try {
					if (socket != null)
						socket.close();
					if (out != null)
						out.close();
					if (in != null)
						in.close();
						
				} catch (Exception e) {
					Logger.getLogger(Whois.class.getName()).log(Level.SEVERE, "Error freeing Whois resources.", e);
				}
			}
		}
		
		return country;
	}
	
	/**
	 * Get IP's ISP.
	 * @param ip IP to get the ISP.
	 * @return The ISP or null in case of error.
	 */
	public static String getNameISP(String ip){
		
		String isp = null;
		
		// Obtenemos el servidor al que preguntar		
		String server = getServer(ip);
		Socket socket = null;
		PrintStream out = null;
		BufferedReader in = null;
		
		boolean exit = false;
		
		if (server == null) {
			isp = null;
		} else {
			try {
				// Establecemos conexión con el servidor.
				socket = new Socket(server, PORT);
				out = new PrintStream(socket.getOutputStream());
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String linea = "";

				// Enviamos la ip al servidor
				out.println(ip);

				// Buscamos el ISP en la respuesta.
				// Debe ser algo como: "netname:    JAZZTEL"
				Pattern p = Pattern.compile("netname:(\\s+)(.+)", Pattern.CASE_INSENSITIVE);
				Matcher m = null;
							
				// Vamos tratando cada una de las lineas de la respuesta
				while ((linea = in.readLine()) != null && !exit)
				{
					m = p.matcher(linea);
					
					// Si se ha encontrado el patron
					if (m.find()){
						isp = m.group(2);
						exit = true;
					} else {
						isp = null;
					}
						
				}
				
			} catch (UnknownHostException e) {
				String error = "Server not found: " + server + ".";
				Logger.getLogger(Whois.class.getName()).log(Level.SEVERE, error, e);
				isp = null;
			} catch (IOException e) {
				String error = "Error parsing server response: " + server + ".";
				Logger.getLogger(Whois.class.getName()).log(Level.SEVERE, error, e);
				isp = null;
			} finally {
				try {
					if (socket != null)
						socket.close();
					if (out != null)
						out.close();
					if (in != null)
						in.close();
						
				} catch (Exception e) {
					Logger.getLogger(Whois.class.getName()).log(Level.SEVERE, "Error freeing Whois resources.", e);
				}
			}
		}
		
		return isp;
	}
	
	/**
	 * Gets the server for whois request. The IP's zone.
	 * @param ip The IP.
	 * @return The server for whois request or null in case of error.
	 */
	private static String getServer(String ip){
		
		String server = null;
		Socket socket = null;
		PrintStream out = null;
		BufferedReader in = null;
		
		boolean exit = false;
		
		try {
			// Establecemos conexión con el servidor global.
			socket = new Socket(IANA, PORT);
			out = new PrintStream(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String line = "";

			// Enviamos la ip al servidor global
			out.println(ip);

			// Buscamos el servidor en la respuesta.
			// Debe ser algo como: "whois:    whois.ripe.net"
			Pattern p = Pattern.compile("whois:(\\s+)(.+)", Pattern.CASE_INSENSITIVE);
			Matcher m = null;
						
			// Vamos tratando cada una de las lineas de la respuesta
			while ((line = in.readLine()) != null && !exit) {
				m = p.matcher(line);
				
				// Si se ha encontrado el patron
				if (m.find()){
					server = m.group(2);
					exit = true;
				} else {
					server = null;
				}
			}
			
		} catch (UnknownHostException e) {
			String error = "Server not found: " + IANA + ".";
			Logger.getLogger(Whois.class.getName()).log(Level.SEVERE, error, e);
			server = null;
		} catch (IOException e) {
			String error = "Error parsing server response: " + IANA + ".";
			Logger.getLogger(Whois.class.getName()).log(Level.SEVERE, error, e);
			server = null;
		} finally {
			try {
				if (socket != null)
					socket.close();
				if (out != null)
					out.close();
				if (in != null)
					in.close();
					
			} catch (Exception e) {
				Logger.getLogger(Whois.class.getName()).log(Level.SEVERE, "Error freeing Whois resources.", e);
			}
		}
		
		return server;
	}
}
