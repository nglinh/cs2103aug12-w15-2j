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
import org.xml.sax.SAXException;

public class Hint {

	private static final String PATH_TO_HINT_XML = "/resource/help.xml";
	private static final String TAG_COMMAND = "command";
	private static final String TAG_NAME = "name";
	private static final String TAG_SUMMARY = "summary";
	private static final String TAG_USAGE = "usage";
	private static final String TAG_EXTRA = "extra";

	boolean hintReady = true;

	private DocumentBuilderFactory docBuilderFactInst;
	private DocumentBuilder docBuilder;
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
			docBuilderFactInst = DocumentBuilderFactory.newInstance();
			docBuilder = docBuilderFactInst.newDocumentBuilder();
			doc = docBuilder.parse(getClass().getResourceAsStream(PATH_TO_HINT_XML));
			doc.getDocumentElement().normalize();
			readHelpFile();

		} catch (SAXException | IOException | ParserConfigurationException e) {
			hintReady = false;
			e.printStackTrace();
		}
	}


	private void readHelpFile() {
		NodeList listOfCommands = doc.getElementsByTagName(TAG_COMMAND);
		for (int i = 0; i < listOfCommands.getLength(); i++) {

			Element eachCommand = (Element) listOfCommands.item(i);

			String name =  getNodeValue(eachCommand, TAG_NAME, 0);
			String summary = getNodeValue(eachCommand, TAG_SUMMARY, 0);

			CmdHint helpPack = new CmdHint(name, summary);

			for(int j = 0; j < eachCommand.getElementsByTagName(TAG_USAGE).getLength(); j++){
				String usage = getNodeValue(eachCommand, TAG_USAGE, j);
				helpPack.addUsage(usage);
			}

			for(int j = 0; j < eachCommand.getElementsByTagName(TAG_EXTRA).getLength(); j++){
				String extra = getNodeValue(eachCommand, TAG_EXTRA, j);
				helpPack.addExtra(extra);
			}
			
			//To finalise the string creation in help package
			helpPack.getHTMLHelp();
			helpPack.getNoHTMLHelp();

			helpStore.put(CmdHint.stripHTML(name.toLowerCase()), helpPack);
		}



	}


	private String getNodeValue(Element eachCommand, String tag, int position){
		return eachCommand.getElementsByTagName(tag).item(position).getChildNodes().item(0).getNodeValue();
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
	private LinkedList<String> extra = new LinkedList<String>();

	private String nameHTML;
	private String summaryHTML;
	private LinkedList<String> usageHTML = new LinkedList<String>();
	private LinkedList<String> extraHTML =  new LinkedList<String>();

	private static final String END_OF_LINE = System.getProperty("line.separator");
	private static final String EXAMPLE_HTML_HEADING = "<h2>Usage\\Examples :</h2> ";
	private static final String EXAMPLE_NO_HTML_HEADING = stripHTML(EXAMPLE_HTML_HEADING);

	private String returnHTML = null;
	private String returnNoHTML = null;


	public CmdHint(String name, String summary){
		this.nameHTML = name;
		this.summaryHTML = summary;

		this.name = stripHTML(name);
		this.summary = stripHTML(summary);
	}

	public void addUsage(String usageHTML){
		this.usageHTML.add(usageHTML);
		this.usage.add(stripHTML(usageHTML));
	}

	public void addExtra(String extraHTML){
		this.extraHTML.addLast(extraHTML);
		this.extra.add(stripHTML(extraHTML));
	}


	public static String stripHTML(String input){
		return input.replaceAll("\\<.*?>","");
	}

	public String getNoHTMLHelp(){

		if(returnNoHTML == null)
		{
			String output = name + END_OF_LINE;
			output += summary + END_OF_LINE + END_OF_LINE;

			output += EXAMPLE_NO_HTML_HEADING + END_OF_LINE;
			for(String use : usage){
				output += use + END_OF_LINE;
			}

			output += END_OF_LINE;
			
			for(String ext : extra){
				output += ext + END_OF_LINE;
			}
			returnNoHTML = output;
		}
		return returnNoHTML;
	}

	public String getHTMLHelp(){

		if(returnHTML == null)
		{
			String output = nameHTML +END_OF_LINE;
			output += summaryHTML + END_OF_LINE + END_OF_LINE;
			output += EXAMPLE_HTML_HEADING + END_OF_LINE + END_OF_LINE;

			for(String use : usageHTML){
				output += use + END_OF_LINE;
			}
			
			output += END_OF_LINE;

			for(String ext : extraHTML){
				output += ext + END_OF_LINE;
			}

			returnHTML = output;
		}
		return returnHTML;
	}
}

