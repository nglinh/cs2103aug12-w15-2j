package main.ui;

//@author A0086826R

public class GuiUpdate {
	
	public static void update(Object source) {
		GuiMain2.getInstance().updateWindow(source);
		GuiQuickAdd.getInstance().updateWindow(source);
	}

}
