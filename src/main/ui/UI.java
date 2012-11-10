package main.ui;

/**  
 * UI.java 
 * An abstract class for all the UIs.
 * @author  Yeo Kheng Meng
 */ 


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.List;
import java.util.logging.Level;

import javax.swing.JComponent;
import javax.swing.Painter;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import main.LogHandler;
import main.shared.LogicToUi;
import main.shared.NattyParserWrapper;
import main.logic.Logic;

public abstract class UI {
	
	private static final String LINE_DATE_FORMAT = "dd-MMM-yy hh:mma";
	private static final String LINE_DATE_LONGER_FORMAT = "EEE dd-MMM-yyyy hh:mma";
	
	private static final DateTimeFormatter LINE_DATE_FORMATTER = DateTimeFormat.forPattern(LINE_DATE_FORMAT);
	private static final DateTimeFormatter LINE_DATE_LONGER_FORMATTER = DateTimeFormat.forPattern(LINE_DATE_LONGER_FORMAT);
	
	private static final String COMMAND_CHECK_FILE_PERMISSIONS = "fileStatus";
	private static final String COMMAND_EXIT = "exit";


	private static Logic logic = Logic.getInstance();
	private static Hint hint = Hint.getInstance();
	
	protected static List<String> commandList = hint.getCommands();
	protected static NattyParserWrapper nattyParser = NattyParserWrapper.getInstance();
	
	//This is the first method that will run the UI after it is constructed. DoITstart will run this.
	public abstract void runUI();
	
	protected LogicToUi sendCommandToLogic(String command) {
		return logic.uiCommunicator(command);
	}
	
	
	protected String dateTimeToString(DateTime toBeConverted) {
		return LINE_DATE_FORMATTER.print(toBeConverted);
	}
	
	protected String dateTimeToLongerString(DateTime toBeConverted) {
		return LINE_DATE_LONGER_FORMATTER.print(toBeConverted);
	}
	
	protected String currentTimeInLongerForm() {
		return dateTimeToLongerString((new DateTime()));
	}
	
	protected String checkFilePermissions() {
		LogicToUi filePermissions = sendCommandToLogic(COMMAND_CHECK_FILE_PERMISSIONS);
		return filePermissions.getString();
	}
	
	
	protected String getHTMLHelp(String command){
		return hint.helpForThisCommandHTML(command);
	}
	
	protected String getNoHTMLHelp(String command){
		return hint.helpForThisCommandNoHTML(command);
	}
	
	protected void exit(){
		sendCommandToLogic(COMMAND_EXIT);
	}
	
	protected void setUiLookAndFeel(){
		try {
			//UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
			//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			//UIManager.setLookAndFeel("com.jgoodies.looks.windows.WindowsLookAndFeel");
			//UIManager.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");
			//UIManager.setLookAndFeel("com.digitprop.tonic.TonicLookAndFeel");
			//UIManager.setLookAndFeel ( "com.alee.laf.WebLookAndFeel" );
			
			// http://stackoverflow.com/questions/949353/java-altering-ui-fonts-nimbus-doesnt-work
			NimbusLookAndFeel laf = new NimbusLookAndFeel();
			UIManager.setLookAndFeel(laf);
			laf.getDefaults().put("defaultFont", new Font("Segoe UI", Font.PLAIN, 12));
			
			// http://stackoverflow.com/questions/7633354/how-to-hide-the-arrow-buttons-in-a-jscrollbar
			UIManager.getLookAndFeelDefaults().put(
					"ScrollBar:ScrollBarThumb[Enabled].backgroundPainter",
					new FillPainter(new Color(127, 169, 191)));
			UIManager.getLookAndFeelDefaults().put(
					"ScrollBar:ScrollBarThumb[MouseOver].backgroundPainter",
					new FillPainter(new Color(127, 169, 191)));
			UIManager.getLookAndFeelDefaults().put(
					"ScrollBar:ScrollBarTrack[Enabled].backgroundPainter",
					new FillPainter(new Color(190, 212, 223)));

			UIManager.getLookAndFeelDefaults().put(
					"ScrollBar:\"ScrollBar.button\".size", 0);
			UIManager.getLookAndFeelDefaults().put(
					"ScrollBar.decrementButtonGap", 0);
			UIManager.getLookAndFeelDefaults().put(
					"ScrollBar.incrementButtonGap", 0);
			    
		} catch (UnsupportedLookAndFeelException e) {
			//e.printStackTrace();
			LogHandler.getLogInstance().log(Level.WARNING, "Error encountered when setting look and feel", e);
		}
	}
	
	public class FillPainter implements Painter<JComponent> {

	    private final Color color;

	    public FillPainter(Color c) { color = c; }

	    @Override
	    public void paint(Graphics2D g, JComponent object, int width, int height) {
	        g.setColor(color);
	        g.fillRect(0, 0, width-1, height-1);
	    }

	}




}
