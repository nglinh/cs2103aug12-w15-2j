package main.logic;

import main.shared.LogicToUi;

public class ExitHandler extends CommandHandler {

	public ExitHandler(String arguments) {
		super(arguments);
	}

	@Override
	public LogicToUi execute() {
		dataBase.unlockFileToExit();
		System.exit(0);
		return new LogicToUi("Exiting DoIt");
	}

}
