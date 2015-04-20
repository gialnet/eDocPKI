package es.redmoon.pl.forms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

import es.redmoon.pl.dsig.PDF;
import es.redmoon.utils.LOBUtils;

import oracle.sql.BLOB;

/**
 * Class for pharse PDF forms.
 * @author María del Mar Pérez <maria@redmoon.es>
 * @author Antonio Pérez <antonio@redmoon.es>
 * @version 0.1
 *
 */
public class PDFForm {
	
	/**
	 * Get form fields.
	 * @param form The form.
	 * @return A BLOB containing XML with form fields or null in case or error.
	 */
	public static BLOB getFormFields(BLOB form) {
		
		BLOB xmlBlob = null;
		Document xmlWithFields = null;
		Element root = null;
		Element node = null;
		Element valuesNode = null;
		Element value = null;
		OutputStream baos = null;
		
		try {

			// Get document bytes
			byte [] bytesPDF = form.getBytes(1, (int)form.length());
			
			PdfReader reader = new PdfReader(bytesPDF);
			AcroFields formFields = reader.getAcroFields();
			HashMap<?, ?> fields = formFields.getFields();
			String[] options = null;
			String[] values = null;
			String[] states = null;
			String key;
			
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			
			// Create XML document  
			xmlWithFields = docBuilder.newDocument();		
	        
			// Create root
			root = xmlWithFields.createElement("formulario");
			xmlWithFields.appendChild(root);
			
			for (Iterator<?> i = fields.keySet().iterator(); i.hasNext();) {
				
				node = xmlWithFields.createElement("campo");
				
				key = (String) i.next();
				
				switch (formFields.getFieldType(key)) {
				case AcroFields.FIELD_TYPE_CHECKBOX:
					
					states = formFields.getAppearanceStates(key);
					
					node.setAttribute("nombre", key);
					node.setAttribute("tipo", "checkbox");
					
					valuesNode = xmlWithFields.createElement("valores");
					
					for (int j = 0; j < states.length; j++){
						value = xmlWithFields.createElement("valor");
						value.appendChild(xmlWithFields.createTextNode(states[j]));
						valuesNode.appendChild(value);
					}
					node.appendChild(valuesNode);
					
					break;
				case AcroFields.FIELD_TYPE_COMBO:
					
					options = formFields.getListOptionExport(key);
					values = formFields.getListOptionDisplay(key);
					
					node.setAttribute("nombre", key);
					node.setAttribute("tipo", "combobox");
					
					valuesNode = xmlWithFields.createElement("valores");
					
					for (int j = 0; j < options.length; j++){
						value = xmlWithFields.createElement("valor");
						value.appendChild(xmlWithFields.createTextNode(values[j]));
						valuesNode.appendChild(value);
					}
					node.appendChild(valuesNode);
					
					break;
				case AcroFields.FIELD_TYPE_LIST:
					
					node.setAttribute("nombre", key);
					node.setAttribute("tipo", "list");
					
					options = formFields.getListOptionExport(key);
					values = formFields.getListOptionDisplay(key);
					
					valuesNode = xmlWithFields.createElement("valores");
					
					for (int j = 0; j < options.length; j++) {

						value = xmlWithFields.createElement("valor");
						value.appendChild(xmlWithFields.createTextNode(values[j]));
						valuesNode.appendChild(value);
					}
					node.appendChild(valuesNode);
					
					break;
				case AcroFields.FIELD_TYPE_NONE:
					node.setAttribute("nombre", key);
					node.setAttribute("tipo", "none");
					
					break;
				case AcroFields.FIELD_TYPE_PUSHBUTTON:
					node.setAttribute("nombre", key);
					node.setAttribute("tipo", "pushbutton");

					break;
				case AcroFields.FIELD_TYPE_RADIOBUTTON:
										
					states = formFields.getAppearanceStates(key);
					
					node.setAttribute("nombre", key);
					node.setAttribute("tipo", "radiobutton");
					
					valuesNode = xmlWithFields.createElement("valores");
					
					for (int j = 0; j < states.length; j++){
						value = xmlWithFields.createElement("valor");
						value.appendChild(xmlWithFields.createTextNode(states[j]));
						valuesNode.appendChild(value);
					}
					node.appendChild(valuesNode);
					
				case AcroFields.FIELD_TYPE_SIGNATURE:
					
					node.setAttribute("nombre", key);
					node.setAttribute("tipo", "signature");
					
					break;
				case AcroFields.FIELD_TYPE_TEXT:
					node.setAttribute("nombre", key);
					node.setAttribute("tipo", "text");
					node.setAttribute("valor", formFields.getField(key));
					
					break;
				default:
					System.out.println("?");
				}
				root.appendChild(node);
			}
			
			baos = new ByteArrayOutputStream();
			
			Source source = new DOMSource(xmlWithFields);
			Result result = new StreamResult(baos);

			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(source, result);
			
			xmlBlob = LOBUtils.OutputStreamToBLOB(baos);
			
		} catch (Exception e) {
			Logger.getLogger(PDFForm.class.getName()).log(Level.SEVERE, "Error getting PDF values.", e);
			xmlBlob = null;
		} finally {
			freeOutputStream(baos);
		}
		
		return xmlBlob;
	}
	
	/**
	 * Fill a form with XML data.
	 * @param form The form to fill.
	 * @param xml A XML with data for fill the PDF document.
	 * @return A BLOB containing the filled form or null in case of error.
	 */
	public static BLOB fillForm(BLOB form, BLOB xml) {
		
		BLOB formBlob = null;
		
		Document xmlWithFields = null;
		
		NodeList nodeList = null;
		
		Node node = null;
		
		NamedNodeMap attributes = null;
		
		String fieldName = null;
		
		String fieldValue = null;
		
		OutputStream outStream = null;
		
		PdfStamper stamper = null;
				
		try {
			
			// Get document bytes
			byte [] bytesPDF = form.getBytes(1, (int)form.length());
			
			PdfReader reader = new PdfReader(bytesPDF);
			
			outStream = new ByteArrayOutputStream();
			
			stamper = new PdfStamper(reader, outStream);
			AcroFields formFields = stamper.getAcroFields();
			
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			
			byte bytes[] = xml.getBytes(1, (int) xml.length());
			
			// Load XML document  
			xmlWithFields = docBuilder.parse (new ByteArrayInputStream(bytes));
			
			// Extract nodes
			nodeList = xmlWithFields.getElementsByTagName("campo");
			
			for (int i = 0; i < nodeList.getLength(); i++){
				node = nodeList.item(i);
				
				attributes  =  node.getAttributes();
				node  =  attributes.getNamedItem("nombre");
				fieldName = node.getNodeValue();
				
				node  =  attributes.getNamedItem("valor");
				fieldValue = node.getNodeValue();
				
				formFields.setField(fieldName, fieldValue);
			}
			
			stamper.close();

			formBlob = LOBUtils.OutputStreamToBLOB(outStream);
			
		} catch (Exception e) {
			Logger.getLogger(PDFForm.class.getName()).log(Level.SEVERE, "Error filling PDF.", e);
			formBlob = null;
		} finally {
			freeStamper(stamper);
			freeOutputStream(outStream);
		}
		
		return formBlob;
	}
	
	/*
	 * Free PDFStamper resources
	 */
	private static void freeStamper(PdfStamper stamper){
		try {
			if (stamper != null)
				stamper.close();
		} catch (Exception e) {
			Logger.getLogger(PDF.class.getName()).log(Level.SEVERE, "Error freeing PDFStamper resources.", e);
		}
	}
	
	/*
	 * Free OutputStream Resources
	 */
	private static void freeOutputStream(OutputStream outStream){
		try {
			if (outStream != null)
				outStream.close();
		} catch (Exception e) {
			Logger.getLogger(PDF.class.getName()).log(Level.SEVERE, "Error freeing OutputStream resources.", e);
		}
	}
	
}
