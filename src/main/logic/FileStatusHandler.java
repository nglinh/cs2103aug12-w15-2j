package main.logic;

import main.shared.LogicToUi;
import main.storage.Database.DB_File_Status;

public class FileStatusHandler extends CommandHandler {

	public FileStatusHandler(String arguments) {
		super(arguments);
	}

	@Override
	public LogicToUi execute() {
		DB_File_Status status = dataBase.getFileAttributes();

		if (status.equals(DB_File_Status.FILE_ALL_OK)) {
			return new LogicToUi("Database file is ready!");
		} else if (status.equals(DB_File_Status.FILE_READ_ONLY)) {
			return new LogicToUi(
					"Database file is read-only or in use by another program. You can only view but not make changes");
		} else if (status.equals(DB_File_Status.FILE_IS_CORRUPT)) {
			return new LogicToUi(
					"The database file is corrupt. DoIt has attempted to read in as much as possible. You will not be able to write to the file until the file is cleared");
		} else if (status.equals(DB_File_Status.FILE_IS_LOCKED)) {
			return new LogicToUi(
					"The database file is locked by another instance of DoIt. Please close all instances and restart the program to use.");
		} else {
			return new LogicToUi("Unknown error with database file");

		}
	}
}