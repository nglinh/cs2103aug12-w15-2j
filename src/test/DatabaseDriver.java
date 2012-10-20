package test;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.joda.time.DateTime;

import shared.SearchTerms;
import shared.Task;
import storage.Database;
import storage.NoMoreUndoStepsException;
import storage.WillNotWriteToCorruptFileException;

public class DatabaseDriver
{
	public static void main(String args[])
	{
		BufferedWriter out;
		try {
			out = new BufferedWriter(new FileWriter("database.txt"));
			out.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		Database myDB = new Database();
		System.out.println(myDB.getFileAttributes());
		
		
		Task floatingtask = new Task("Test Floating", true);
		Task floatingtask2 = new Task("Test Floating2", false);
		
		
		Task deadlinetask = new Task("Test deadline", new DateTime());
		
		Task deadlinetask2 = new Task("Test deadline2", new DateTime().minusYears(5));
		
		Task timedtask = new Task("Test Timed", new DateTime().minusDays(100), new DateTime(2013, 12, 31, 00, 00), false);
		Task timedtask2 = new Task("Test Timed2", new DateTime(2011, 9, 5, 23,59), new DateTime(2013, 12, 31, 00, 00), true);
		

		try {
			myDB.add(floatingtask);
			myDB.add(deadlinetask);
			myDB.add(timedtask);
			
			myDB.add(floatingtask2);
			myDB.add(deadlinetask2);
			myDB.undo();
			myDB.add(timedtask2);
			
			for(Task temp : myDB.readAll())
			{
				System.out.println(temp.showInfo());
			}
			
			
			System.out.println("After");

			ArrayList<Task> output = myDB.search(new SearchTerms(true, true, true, true, true, new DateTime().minusYears(1), new DateTime().plusYears(1)));

			System.out.println("After");
			
			for(Task temp : output)
			{
				System.out.println(temp.showInfo());
			}
			
			

		} catch (IOException | NoMoreUndoStepsException | WillNotWriteToCorruptFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
//		BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in));
//		
//		String linefrominput;
//		
//		SearchTerms st = new SearchTerms(false, false, false, false, true, null, null, null);
//		
//		for(Task temp : myDB.search(st))
//		{
//			myDB.delete(temp.getSerial());
//		}
//		
//		myDB.undo();
//		myDB.undo();
//		myDB.undo();
//		myDB.undo();
//		myDB.undo();
//		myDB.undo();
//		myDB.undo();
//		myDB.undo();
//		myDB.undo();
//		myDB.undo();
//		myDB.undo();
//		myDB.undo();
//		myDB.undo();
//		myDB.undo();

		
		

		
	}

}
