package main;

import java.io.IOException;
import java.util.logging.FileHandler;

public class LogFileHandler {


	private final String LOG_FILE_NAME = "DoItLog.txt";

	private FileHandler fh;

	private static LogFileHandler theOne = null;
	
	private LogFileHandler(){
		
	}

	public static LogFileHandler getInstance(){
		if(theOne == null){
			theOne = new LogFileHandler();
		}
		return theOne;
	}


	public FileHandler getFileHandler(){
		try {
			
			if(fh == null){
				fh = new FileHandler(LOG_FILE_NAME);
			}
		} catch (SecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fh;
	}





}
