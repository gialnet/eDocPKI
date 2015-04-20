package es.redmoon.pl.pdfs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfWriter;

import es.redmoon.pl.barcodes.Barcode;
import es.redmoon.utils.LOBUtils;
import es.redmoon.utils.Stream;

import oracle.sql.BLOB;

/**
 * Class for manipulate PDF.
 * @version 0.1
 */
public class PDF {

	/**
	 * Add barcode to PDF.
	 * @param original Original PDF.
	 * @param format The barcode format. Supported barcode formats: PDF417, QR, Code39, Code128, Datamatrix.
	 * @param resolution The barcode image resolution. dpi: dots per inch. Is recommendable set, at least, 300 dpi resolution. If is set 0 or lower value default 300 dpi will be used.
	 * @param width Image width (points). If is set 0 or lower value the image width will be barcode generated width.
	 * Be carrefull with value set because the barcode original width will be scaled to desired image width value and this may distort the code.  
	 * @param height Image heigth (points). If is set 0 or lower value the image height will be barcode generated height.
	 * Be carrefull with value set because the barcode original height will be scaled to desired image heigth value and this may distort the code.
	 * @param text The original text to encode.
	 * @param absoluteX X position (points) for barcode, inferior left corner.
	 * @param absoluteY Y position (points) for barcode, inferior left corner.
	 * @return BLOB containig original PDF with barcode image or null in case of error.
	 */
	public static BLOB addBarcode(BLOB original, String format, int resolution, int width, int height, String text, float absoluteX, float absoluteY) {

		Document document = null;
		
		OutputStream osPDF = null;
		InputStream isImage = null;
		
		BLOB imageBLOB = null;
		BLOB pdfBLOB = null;

		try {
			
			document = new Document();
			osPDF = new ByteArrayOutputStream();
			
			PdfWriter.getInstance(document, osPDF);
			
			document.open();

			// Points to millimeters
			int w = (int)(width*25.4/72);
			int h = (int)(height*25.4/72);
			
			imageBLOB = Barcode.createBarcode(format, resolution, w, h, text);
			isImage = new ByteArrayInputStream(imageBLOB.getBytes(1, (int)imageBLOB.length()));
			
			Image image = Image.getInstance(Stream.toByteArray(isImage));
			image.setAbsolutePosition(absoluteX, document.getPageSize().getTop() - absoluteY);
			document.add(image);

			document.close();
			
			pdfBLOB = LOBUtils.OutputStreamToBLOB(osPDF);
			
			
		} catch(DocumentException e){
			Logger.getLogger(PDF.class.getName()).log(Level.SEVERE, "Error adding image to PDF document.", e);
			pdfBLOB = null;
		} catch (SQLException e) {
			Logger.getLogger(PDF.class.getName()).log(Level.SEVERE, "Error with data base.", e);
			pdfBLOB = null;
		} catch (MalformedURLException e) {
			Logger.getLogger(PDF.class.getName()).log(Level.SEVERE, "Error adding image to PDF document.", e);
			pdfBLOB = null;
		} catch (IOException e) {
			Logger.getLogger(PDF.class.getName()).log(Level.SEVERE, "Error adding image to PDF document.", e);
			pdfBLOB = null;
		} finally {
			if (osPDF != null) {
				try {
					osPDF.close();
				} catch (IOException e) {
					Logger.getLogger(PDF.class.getName()).log(Level.SEVERE, "Error freeing resources.", e);
				}
			}
			if (isImage != null) {
				try {
					isImage.close();
				} catch (IOException e) {
					Logger.getLogger(PDF.class.getName()).log(Level.SEVERE, "Error freeing resources.", e);
				}
			}
		}
		
		
		return pdfBLOB;
	}
}
