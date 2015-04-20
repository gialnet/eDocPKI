package es.redmoon.pl.barcodes.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import oracle.sql.BLOB;
import es.redmoon.pl.barcodes.Barcode;

public class TestBarcode {

	public static void main(String[] args) {
		
		//BLOB barcode = Barcode.createBarcode("PDF417", 300, 300, 60, "Hello Angel");
		//BLOB barcode = Barcode.createBarcode("QR", 300, 75, 75, "Hello Angel");
		//BLOB barcode = Barcode.createBarcode("CODE39", 600, 300, 100, "Hello Angel");
		//BLOB barcode = Barcode.createBarcode("CODE128", 300, 350, 100, "Hello Angel");
		BLOB barcode = Barcode.createBarcode("DATAMATRIX", 500, 0, 0, "Hello Angel");
		
		if (barcode != null) {
			System.out.println("Getting barcode.");
			
			//File blobFile = new File("./barcodePDF417.png");
			//File blobFile = new File("./barcodeQR.png");
			//File blobFile = new File("./barcodeCODE39.png");
			//File blobFile = new File("./barcodeCODE128.png");
			File blobFile = new File("./barcodeDATAMATRIX.png");
			
			FileOutputStream outStream = null;
			
			try {
				outStream = new FileOutputStream(blobFile); 
				InputStream inStream = barcode.getBinaryStream(); 
				
				int length = -1; 
				int size = barcode.getBufferSize(); 
				byte[] buffer = new byte[size]; 
				
				System.out.println("Saving barcode to file.");
				while ((length = inStream.read(buffer)) != -1) { 
					outStream.write(buffer, 0, length); 
					outStream.flush(); 
				}
				
				System.out.println("End.");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (outStream != null) {
						outStream.close();
					}
				} catch (IOException e) {
					Logger.getLogger(TestBarcode.class.getName()).log(Level.SEVERE, "Error freeing resources.", e);
				}
			}
		} else {
			System.out.println("Some error happened getting barcode.");
		}
		
	}
}
