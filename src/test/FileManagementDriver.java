package test;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.joda.time.DateTime;

import shared.Task;
import storage.FileManagement;
import storage.WillNotWriteToCorruptFileException;

public class FileManagementDriver {
	public static void main(String args[])
	{
		ArrayList<Task> taskStoretest = new ArrayList<Task>();
		
		
		
		FileManagement filemgr = new FileManagement(taskStoretest);
		System.out.println(filemgr.getFileAttributes());
		
		Task floatingtask = new Task("Test Floating", true);
		Task floatingtask2 = new Task("Test Floating2", true);
		
		
		Task deadlinetask = new Task("Test deadline", new DateTime());
		Task deadlinetask2 = new Task("Test deadline2", new DateTime().minusYears(5));
		
		Task timedtask = new Task("Test Timed", new DateTime().minusDays(100), new DateTime(2013, 12, 31, 00, 00), false);
		Task timedtask2 = new Task("Test Timed2", new DateTime(2011, 9, 5, 23,59), new DateTime(2013, 12, 31, 00, 00), false);
		
		taskStoretest.add(floatingtask);
		taskStoretest.add(floatingtask2);
		
		taskStoretest.add(deadlinetask);
		taskStoretest.add(deadlinetask2);
		
		taskStoretest.add(timedtask);
		taskStoretest.add(timedtask2);
		
		Collections.sort(taskStoretest);
		
		
		try {
			filemgr.writeDataBaseToFile(taskStoretest);
		} catch (IOException | WillNotWriteToCorruptFileException e) {
			System.out.println("Fail");
			e.printStackTrace();
		} 

		
		
		
		
	}

}
