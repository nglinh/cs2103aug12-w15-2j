package main.ui;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Hint {

	boolean hintReady = true;

	private DocumentBuilderFactory dbf;
	private DocumentBuilder db;
	private Document doc;
	private HashMap<String, CmdHint> helpStore = new HashMap<String, CmdHint>();

	private static Hint theOne = null;

	public static Hint getInstance(){
		if (theOne == null){
			theOne = new Hint();
		}

		return theOne;
	}


	private Hint(){
		try{
			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			doc = db.parse(new InputSource("src\\main\\resource\\help.xml"));
			doc.getDocumentElement().normalize();
	//		readHelpFile();

		} catch (SAXException | IOException | ParserConfigurationException e) {
			hintReady = false;
			e.printStackTrace();
		}
	}


	private void readHelpFile() {
		NodeList listOfCommands = doc.getElementsByTagName("command");
		for (int i = 0; i < listOfCommands.getLength(); i++) {

			Element eachCommand = (Element) listOfCommands.item(i);

			NodeList dataForEachParam;
			String name;
			String summary;

			dataForEachParam = getNodeList(eachCommand, "name");
			name =  dataForEachParam.item(0).getNodeValue();

			dataForEachParam = getNodeList(eachCommand, "summary");
			summary = dataForEachParam.item(0).getNodeValue();

			CmdHint helpPack = new CmdHint(name, summary);

			dataForEachParam = getNodeList(eachCommand, "usage");


			for(int j = 0; j < dataForEachParam.getLength(); j++){
				String usage = dataForEachParam.item(j).getNodeValue();
				helpPack.addUsage(usage);
			}


			dataForEachParam = getNodeList(eachCommand, "extra");

			for(int j = 0; j < dataForEachParam.getLength(); j++){
				String extra = dataForEachParam.item(j).getNodeValue();
				helpPack.addExtra(extra);
			}

			helpStore.put(name.toLowerCase(), helpPack);
		}



	}

	private NodeList getNodeList(Element eachCommand, String tag){
		NodeList itemNode = eachCommand.getElementsByTagName(tag);

		if(itemNode.getLength() == 0) {
			return null;
		} else {
			return eachCommand.getElementsByTagName(tag).item(0).getChildNodes();
		}
	}

	public String helpForThisCommandHTML(String needHelp){
		needHelp = needHelp.toLowerCase();
		CmdHint hintPack = helpStore.get(needHelp);

		if(hintPack == null){
			return null;
		} else {
			return hintPack.getHTMLHelp();
		}
	}

	public String helpForThisCommandNoHTML(String needHelp){
		needHelp = needHelp.toLowerCase();
		CmdHint hintPack = helpStore.get(needHelp);

		if(hintPack == null){
			return null;
		} else {
			return hintPack.getNoHTMLHelp();
		}
	}

}





class CmdHint {

	private String name;
	private String summary;
	private LinkedList<String> usage = new LinkedList<String>();
	private LinkedList<String> extra =  new LinkedList<String>();

	private LinkedList<String> usageHTML = new LinkedList<String>();
	private LinkedList<String> extraHTML =  new LinkedList<String>();

	public CmdHint(String name, String summary){
		this.name = name;
		this.summary = summary;
	}

	public void addUsage(String usageHTML){
		this.usageHTML.add(usageHTML);
		this.usage.add(stripHTML(usageHTML));
	}

	public void addExtra(String extraHTML){
		this.extraHTML.addLast(extraHTML);
		this.extra.add(stripHTML(extraHTML));
	}


	private String stripHTML(String input){
		return input.replaceAll("\\<.*?>","");
	}

	public String getNoHTMLHelp(){
		String output = name + "\n";
		output += summary + "\n";
		for(String use : usage){
			output += use + "\n";
		}

		for(String ext : extra){
			output += ext + "\n";
		}

		return output;
	}

	public String getHTMLHelp(){
		String output = name + "\n";
		output += summary + "\n";
		for(String use : usageHTML){
			output += use + "\n";
		}

		for(String ext : extraHTML){
			output += ext + "\n";
		}

		return output;
	}
}

