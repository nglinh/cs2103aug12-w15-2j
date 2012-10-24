package ui;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;

public class CalendarRenderer implements HyperlinkListener{

	public String render(){
		
		DateTime dt = new DateTime();
		dt = dt.withMonthOfYear(12);
		
		int month = dt.getMonthOfYear();
		int year = dt.getYear();
		
		// Get the first Sunday before the first of the month as the starting day
		DateTime startingDay = dt.withDayOfMonth(1).withTimeAtStartOfDay();
		while(startingDay.getDayOfWeek() != DateTimeConstants.SUNDAY){
			startingDay = startingDay.minusDays(1);
		}
		System.out.println(startingDay);
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("<table width=100%><tr><td width=1>&lt;</td><td align=center>"+DateTimeFormat.forPattern("MMMM yyyy").print(dt)+"</td><td width=1>&gt;</td></tr></table>");
		sb.append("<table width=100% class=calendar cellpadding=1 cellspacing=1>");
		sb.append("<tr><td>S</td><td>M</td><td>T</td><td>W</td><td>T</td><td>F</td><td>S</td></tr>");
		sb.append("<tr>");
		
		DateTime currentDay = startingDay;
		//while(currentDay.isBefore(dt.withDayOfMonth(1).plusMonths(1).withTimeAtStartOfDay())){
		for (int i=0; i<42; i++){
			if(currentDay != startingDay && currentDay.getDayOfWeek() == DateTimeConstants.SUNDAY){
				sb.append("</tr><tr>");
			}
			System.out.println(currentDay + " " + startingDay.plusMonths(1));
			sb.append("<td align=right>"+currentDay.getDayOfMonth()+"</td>");
			currentDay = currentDay.plusDays(1);
		}		
		sb.append("</tr>");
		sb.append("</table>");
		return sb.toString();		
		
	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
