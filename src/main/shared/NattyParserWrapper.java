package main.shared;

import java.util.List;
import java.util.TimeZone;

import org.joda.time.DateTime;

import com.joestelmach.natty.CalendarSource;
import com.joestelmach.natty.Parser;
import com.joestelmach.natty.DateGroup;

public class NattyParserWrapper {
	static NattyParserWrapper theOne = null;
	Parser nattyParser = null;

	private NattyParserWrapper() {
		nattyParser = new Parser(TimeZone.getDefault());
	}

	public List<DateGroup> parseWithDefaultBaseDate(String str) {
		CalendarSource.setBaseDate(new DateTime().withTime(23, 59, 00, 00)
				.toDate());
		return nattyParser.parse(str);
	}

	public List<DateGroup> parseWithCustomisedBaseDate(int hour, int minute,
			int second, String str) {
		CalendarSource.setBaseDate(new DateTime().withTime(hour, minute,
				second, 00).toDate());
		return nattyParser.parse(str);

	}

	public List<DateGroup> parseWithCustomisedBaseDate(DateTime dateTime,
			String str) {
		CalendarSource.setBaseDate(dateTime.toDate());
		return nattyParser.parse(str);
	}

	public static NattyParserWrapper getInstance() {
		if (theOne == null) {
			theOne = new NattyParserWrapper();
		}
		return theOne;
	}
}
