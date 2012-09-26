/**  
 * DoItstart.java 
 * The starting point of the program.
 * Default is GUI, CLI if command line argument is -cli
 * @author  Yeo Kheng Meng
 */ 

import ui.CLI;
import ui.UI;


public class DoITstart {
	
	
	public static void main(String[] args){
		
		if (args.length == 1 && args[0].equals("-cli")) {
			UI doITUi = new CLI();
			doITUi.runUI();	
		} 
		//else {
//			UI doITUi = new Gui();
//			doITUi.runUI();
//		}
		
	}

}
