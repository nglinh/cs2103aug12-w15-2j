package main.shared;

import java.util.List;
import java.util.TimeZone;

import org.joda.time.DateTime;

import com.joestelmach.natty.CalendarSource;
import com.joestelmach.natty.Parser;
import com.joestelmach.natty.DateGroup;

//@author A0088427U
/**
 * This class serves as a wrapper for Natty Date parser. Since the overhead for
 * Natty to initialize is significant, applying the singleton pattern here would
 * increase performance.
 * 
 */
public class NattyParserWrapper {
	private static NattyParserWrapper theOne = null;
	private static Parser nattyParser = null;

	private NattyParserWrapper() {
		nattyParser = new Parser(TimeZone.getDefault());
	}

	/**
	 * Parse String with date base = current date, time = 23:59
	 * 
	 * @param str
	 *            string to be parsed
	 * @return parse result
	 */
	public List<DateGroup> parseWDefBaseDate(String str) {
		CalendarSource.setBaseDate(new DateTime().withTime(23, 59, 00, 00)
				.toDate());
		return nattyParser.parse(str);
	}

	/**
	 * Parse with custom date time base.
	 * 
	 * @param hour
	 *            hour of the base time
	 * @param minute
	 *            minute of the base time
	 * @param second
	 *            second of the base time
	 * @param str
	 *            string to be parsed
	 * @return parse result
	 */
	public List<DateGroup> parseWCustomBaseDate(int hour, int minute,
			int second, String str) {
		CalendarSource.setBaseDate(new DateTime().withTime(hour, minute,
				second, 00).toDate());
		return nattyParser.parse(str);

	}

	/**
	 * Parse with custom base date.
	 * 
	 * @param dateTime
	 *            base date to be parsed with
	 * @param str
	 *            to be parsed
	 * @return parse result
	 */
	public List<DateGroup> parseWCustomBaseDate(DateTime dateTime, String str) {
		CalendarSource.setBaseDate(dateTime.toDate());
		return nattyParser.parse(str);
	}
	/**
	 * Parse with now as current date base.
	 * @param str to be parsed
	 * @return parse result
	 */
	public List<DateGroup> parseWCurBaseDate(String str) {
		CalendarSource.setBaseDate((new DateTime()).toDate());
		return nattyParser.parse(str);
	}
	/**
	 * Return singleton of NattyParserWrapper
	 * @return an instance of Natty Parser
	 */
	public static NattyParserWrapper getInstance() {
		if (theOne == null) {
			theOne = new NattyParserWrapper();
			nattyParser.parse("Dummy Command for Natty now");
		}
		return theOne;
	}
}
