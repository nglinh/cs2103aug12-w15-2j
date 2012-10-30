package main;

import java.io.IOException;
import java.util.logging.FileHandler;

public class DoItLogFileHandler {


	private final String LOG_FILE_NAME = "DoItLog.txt";

	private FileHandler fh;

	private static DoItLogFileHandler theOne = null;
	
	private DoItLogFileHandler(){
		
	}

	public static DoItLogFileHandler getInstance(){
		if(theOne == null){
			theOne = new DoItLogFileHandler();
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
