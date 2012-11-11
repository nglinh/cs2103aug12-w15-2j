package test.ui;


import static org.junit.Assert.assertEquals;
import main.ui.Cli;

import org.junit.Test;

public class CliTest {
	
	public final String LINE_BREAK = System.getProperty("line.separator");
	Cli cli = new Cli();
	@Test
	public void testProcessInput() {
		cli.processInput("delete all");

		
		String output;
		
		output = cli.processInput("list");
		
		assertEquals(
				"+-----------------------------------------------------------------------------+" + LINE_BREAK +
				"|Idx|*|  Start/Deadline   |        End        |      What to Do?              |" + LINE_BREAK +
				"+---+-+-------------------+-------------------+-------------------------------+" + LINE_BREAK + 
				LINE_BREAK +
				"Listing based on these parameters:  \"All Tasks\" " + LINE_BREAK, output);
		

		cli.processInput("add \"A done deadline task by 0600 1st Jan 2012\" by 6am 1st Jan 2012");
		cli.processInput("add \"An undone timed task from 2359 31 Dec 2012 to 2248 28 Feb 2013\" from 11:59pm 31 Dec 2012 to 10:48pm 28 Feb 2013");
		cli.processInput("add \"A done floating task\"");
		cli.processInput("list");
		cli.processInput("done 1");
		cli.processInput("done 3");
		
		output = cli.processInput("list");
		
		assertEquals(
				"+-----------------------------------------------------------------------------+" + LINE_BREAK +
				"|Idx|*|  Start/Deadline   |        End        |      What to Do?              |" + LINE_BREAK +
				"+---+-+-------------------+-------------------+-------------------------------+" + LINE_BREAK +
				"|  1|*| 01-Jan-12 06:00AM |         -         | A done deadline task by 0600  |" + LINE_BREAK +
				"|   | |                   |                   | 1st Jan 2012                  |" + LINE_BREAK +
				"+---+-+-------------------+-------------------+-------------------------------+" + LINE_BREAK +
				"|  2|-| 31-Dec-12 11:59PM | 28-Feb-13 10:48PM | An undone timed task from 235 |" + LINE_BREAK +
				"|   | |                   |                   | 9 31 Dec 2012 to 2248 28 Feb  |" + LINE_BREAK +
				"|   | |                   |                   | 2013                          |" + LINE_BREAK +
				"+---+-+-------------------+-------------------+-------------------------------+" + LINE_BREAK +
				"|  3|*|         -         |         -         | A done floating task          |" + LINE_BREAK +
				"+-----------------------------------------------------------------------------+" + LINE_BREAK +
				LINE_BREAK +
				"Listing based on these parameters:  \"All Tasks\" " + LINE_BREAK, output);
	
	}

}
