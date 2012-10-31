package main.ui;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class HTMLEncoder {

	public static String encode(String strToEncode) {
		// Sources:
		// http://www.roseindia.net/xml/dom/CreatXMLFile.shtml
		// http://stackoverflow.com/questions/4142046/create-xml-file-using-java
		
		StringWriter strWriter = new StringWriter();
		try {
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("text");
			doc.appendChild(rootElement);
			Text textNode = doc.createTextNode(strToEncode);
			rootElement.appendChild(textNode);

			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc.getDocumentElement()
					.getFirstChild());
			StreamResult result = new StreamResult(strWriter);
			transformer.transform(source, result);
			
		} catch (Exception e) {

		}

		String encodedStr = strWriter.toString();
		encodedStr = encodedStr.split("\\?>", 2)[1];

		return encodedStr;
	}
	
}
