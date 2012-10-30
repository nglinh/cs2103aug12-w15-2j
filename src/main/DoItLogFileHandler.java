package main;

import java.io.IOException;
import java.util.logging.FileHandler;

public class DoItLogFileHandler {


	private static final String LOG_FILE_NAME = "DoItLog.txt";

	private static FileHandler fh;

	DoItLogFileHandler theOne = null;

	public DoItLogFileHandler getInstance(){
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
