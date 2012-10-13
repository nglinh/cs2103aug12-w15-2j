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

import ui.Cli;
import ui.CliWithJline;
import ui.GuiMain;
import ui.UI;


public class DoITstart {
	
	
	public static void main(String[] args){
		
		UI doITUi;
		
		if (args.length == 0) {
			doITUi = new GuiMain();
			doITUi.runUI();	
		} else if (args[0].equals("-cli") && isConsoleAttached()) {
			doITUi = new CliWithJline();
			doITUi.runUI();	
			
			//Only can reach here if CliWithJline has crashed.
			System.out.println("DoIt will now revert to fail-safe mode");
			doITUi = new Cli();
			doITUi.runUI();	
		
		} else if (args[0].equals("-clisafe")) {
			doITUi = new Cli();
			doITUi.runUI();
		} else {
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
		return System.console() != null;
	}

}
