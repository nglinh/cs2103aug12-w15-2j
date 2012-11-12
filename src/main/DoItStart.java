//@author A0081007U
package main;

/**  
 * DoItstart.java 
 * The starting point of the program.
 * 
 * Default is GUI
 * CliWithJline if command line argument is -cli
 * Pure compliant CLI if argument is -clisafe or consoleless system
 * 
 * @author  Yeo Kheng Meng
 */ 

import java.util.logging.Level;
import java.util.logging.Logger;

import main.ui.Cli;
import main.ui.CliWithJline;
import main.ui.GuiMain;
import main.ui.GuiTrayIcon;
import main.ui.UI;


public class DoItStart {
	
	private static Logger log = LogHandler.getLogInstance();
	public static void main(String[] args){
		
		log.info("Program start");
		
		UI doITUi;
		
		if (args.length == 0) {
			log.info("Start tray icon");
			UI trayIcon = GuiTrayIcon.getInstance();
			trayIcon.runUI();	
			UI mainWindow = GuiMain.getInstance();
			mainWindow.runUI();	
			
		} else if (args[0].equals("-tray")) {
			log.info("Start tray icon only");
			doITUi = GuiTrayIcon.getInstance();
			doITUi.runUI();	
		
		} else if (args[0].equals("-cli") && isConsoleAttached()) {
			log.info("Start CliWithJline");
			doITUi = new CliWithJline();
			doITUi.runUI();	
		
		} else if (args[0].equals("-clisafe")) {
			log.info("Start Cli safe mode");
			doITUi = new Cli();
			doITUi.runUI();	

		} else {
			log.log(Level.WARNING, "Unknown arguments, start CLi safe mode");
			doITUi = new Cli();
			doITUi.runUI();	
		}

	}

	/**
	 * To check if Terminal Window is attached to DoIt.
	 * <p>
	 * The Jline library employs a native hook to the terminal, a console must be attached for it to work.                     
	 *
	 * @return true if terminal is attached, false if inside Eclipse or in a console-less system
	 */
	private static boolean isConsoleAttached() {
		boolean consoleAttached = ( System.console() != null);
		if(consoleAttached) {
			log.info("Console attached");
		} else {
			log.warning("Console not attached");
		}
		
		return consoleAttached;
	}

}
