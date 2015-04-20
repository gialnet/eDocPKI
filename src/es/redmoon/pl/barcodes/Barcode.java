package es.redmoon.pl.barcodes;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.krysalis.barcode4j.impl.AbstractBarcodeBean;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.impl.datamatrix.DataMatrixBean;
import org.krysalis.barcode4j.impl.pdf417.PDF417Bean;
import org.krysalis.barcode4j.impl.qr.QRCodeBean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

import es.redmoon.utils.LOBUtils;

import oracle.sql.BLOB;

/**
 * Class for generate barcodes.
 * @version 0.1
 */
public class Barcode {
	
	private static String PDF417 = "PDF417";
	private static String QR = "QR";
	private static String CODE39 = "CODE39";
	private static String CODE128 = "CODE128";
	private static String DATAMATRIX = "DATAMATRIX";
	
	/**
	 * Generate a png image with barcode from a text.
	 * @param format The barcode format. Supported barcode formats: PDF417, QR, Code39, Code128, Datamatrix.
	 * @param resolution The barcode image resolution. dpi: dots per inch. Is recommendable set, at least, 300 dpi resolution. If is set 0 or lower value default 300 dpi will be used.
	 * @param width Image width (millimeters). If is set 0 or lower value the image width will be barcode generated width.
	 * Be carrefull with value set because the barcode original width will be scaled to desired image width value and this may distort the code.  
	 * @param height Image heigth (millimeters). If is set 0 or lower value the image height will be barcode generated height.
	 * Be carrefull with value set because the barcode original height will be scaled to desired image heigth value and this may distort the code.
	 * @param text The original text to encode.
	 * @return A BLOB containing png image or null in case of error.
	 * Be carefull with width and height because the generated barcode will be scaled to desired values and may be distorted.
	 */
	public static BLOB createBarcode (String format, int resolution, int width, int height, String text) {
		
		AbstractBarcodeBean bean = null;
		BLOB blobBarcode = null;
		OutputStream outStream = null;
		
		int dpi;
		
		if (resolution > 0){
			dpi = resolution;
		} else{
			dpi = 300;
		}
		
		if (format.toUpperCase().equals(PDF417)) {
			bean = new PDF417Bean();
			
			if (dpi <= 145) {
				bean.setModuleWidth(25.4/dpi);
			}
			
		} else if (format.toUpperCase().equals(QR)) {
			bean = new QRCodeBean();
			
			if (dpi <= 145) {
				bean.setModuleWidth(25.4/dpi);
			}
			
		} else if (format.toUpperCase().equals(CODE39)) {
			bean = new Code39Bean();
			
			if (dpi <= 270) {
				bean.setModuleWidth(25.4/dpi);
			}
			
		} else if (format.toUpperCase().equals(CODE128)) {
			bean = new Code128Bean();
			
			if (dpi <= 245) {
				bean.setModuleWidth(25.4/dpi);
			}
			
		} else if (format.toUpperCase().equals(DATAMATRIX)) {
			bean = new DataMatrixBean();
			
			if (dpi <= 145) {
				bean.setModuleWidth(25.4/dpi);
			}

		}
		
		if (bean != null) {
			
			try {
				
				if (width <= 0 && height <= 0) {
					// Barcode original dimensions.
					outStream = new ByteArrayOutputStream();
					
					//Set up the canvas provider for monochrome PNG output 
					BitmapCanvasProvider canvas = new BitmapCanvasProvider(outStream, "image/x-png", dpi, BufferedImage.TYPE_BYTE_GRAY, false, 0);

					//Generate the barcode
					bean.generateBarcode(canvas, text);

					//Signal end of generation
					canvas.finish();
					
					blobBarcode = LOBUtils.OutputStreamToBLOB(outStream);
					
				} else if (width > 0 && height > 0){
					// Width and height
					BitmapCanvasProvider provider = new BitmapCanvasProvider(dpi, BufferedImage.TYPE_BYTE_GRAY, false, 0);
					bean.generateBarcode(provider, text);
					provider.finish();
					BufferedImage before = provider.getBufferedImage();
					
					int w = before.getWidth();
					int h = before.getHeight();
					
					BufferedImage after = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
					AffineTransform at = new AffineTransform();
					
					at.scale(  ((double)width)/w, ((double)height)/h);
					
					AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
					after = scaleOp.filter(before, after);
					
					outStream = new ByteArrayOutputStream();
					
					ImageIO.write(after, "PNG", outStream);
					
					blobBarcode = LOBUtils.OutputStreamToBLOB(outStream);
					
				} else if (width <= 0 && height > 0) {
					// Only height scaled
					BitmapCanvasProvider provider = new BitmapCanvasProvider(dpi, BufferedImage.TYPE_BYTE_GRAY, false, 0);
					bean.generateBarcode(provider, text);
					provider.finish();
					BufferedImage before = provider.getBufferedImage();
					
					int w = before.getWidth();
					int h = before.getHeight();
					
					BufferedImage after = new BufferedImage(w, height, BufferedImage.TYPE_BYTE_GRAY);
					AffineTransform at = new AffineTransform();
					
					at.scale(1d, ((double)height)/h);
					
					AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
					after = scaleOp.filter(before, after);
					
					outStream = new ByteArrayOutputStream();
					
					ImageIO.write(after, "PNG", outStream);
					
					blobBarcode = LOBUtils.OutputStreamToBLOB(outStream);
					
				} else if (width > 0 && height <= 0) {
					// Only width scaled
					BitmapCanvasProvider provider = new BitmapCanvasProvider(dpi, BufferedImage.TYPE_BYTE_GRAY, false, 0);
					bean.generateBarcode(provider, text);
					provider.finish();
					BufferedImage before = provider.getBufferedImage();
					
					int w = before.getWidth();
					int h = before.getHeight();
					
					BufferedImage after = new BufferedImage(width, h, BufferedImage.TYPE_BYTE_GRAY);
					AffineTransform at = new AffineTransform();
					
					at.scale( ((double)width)/w, 1d);
					
					AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
					after = scaleOp.filter(before, after);
					
					outStream = new ByteArrayOutputStream();
					
					ImageIO.write(after, "PNG", outStream);
					
					blobBarcode = LOBUtils.OutputStreamToBLOB(outStream);
					
				}

			} catch (IOException e) {
				Logger.getLogger(Barcode.class.getName()).log(Level.SEVERE, "Error generatting the barcode..", e);
				blobBarcode = null;
			} finally {
				if (outStream != null) {
					try {
						outStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
		} else {
			blobBarcode = null;
		}
		
		return blobBarcode;
	}
	
}
