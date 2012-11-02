package main.logic;

import main.shared.LogicToUi;

public class ExitHandler extends CommandHandler {

	public ExitHandler(String arguments) {
		super(arguments);
		// TODO Auto-generated constructor stub
	}

	@Override
	public LogicToUi execute() {
		dataBase.unlockFileToExit();
		System.exit(0);
		return new LogicToUi("Exiting DoIt");
	}

}
