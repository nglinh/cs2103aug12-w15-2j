package main;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class LogHandler {


	private static final String LOG_FILE_NAME = "DoItLog.txt";
	private static FileHandler fh;
	private static Logger theOneLogger = null;


	public static Logger getLogInstance(){
		if(theOneLogger == null){
			try {
				theOneLogger = Logger.getLogger("DoItLogger");

				
				//To disable logging to Standard Error
				theOneLogger.setUseParentHandlers(false);
				
				
				theOneLogger.addHandler(getFileHandler());
			} catch (SecurityException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return theOneLogger;
	}


	private static FileHandler getFileHandler() throws SecurityException, IOException{	
			if(fh == null){
				fh = new FileHandler(LOG_FILE_NAME);
			}
		return fh;
	}
	





}
