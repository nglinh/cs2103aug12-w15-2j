package ui;

/**  
 * UI.java 
 * An abstract class for all the UIs.
 * @author  Yeo Kheng Meng
 */ 

import java.util.NoSuchElementException;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public abstract class UI {
	private final String LINE_DATE_FORMAT = "dd-MMM-yy hh:mma";
	private final DateTimeFormatter LINE_DATE_FORMATTER = DateTimeFormat.forPattern(LINE_DATE_FORMAT);
	
	protected String dateTimeToString(DateTime toBeConverted) {
		return LINE_DATE_FORMATTER.print(toBeConverted);
	}
	
	public abstract int indexToSerial(int index) throws NoSuchElementException;
}
