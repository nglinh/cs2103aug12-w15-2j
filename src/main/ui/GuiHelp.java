package main.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JEditorPane;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;

public class GuiHelp extends UI{

	private static GuiHelp theOne;
	
	public static GuiHelp getInstance(){
		if(theOne == null){
			theOne = new GuiHelp();
		}
		return theOne;
	}
	
	private JFrame frmDoithelp;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GuiHelp window = GuiHelp.getInstance();
					window.frmDoithelp.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	private GuiHelp() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmDoithelp = new JFrame();
		frmDoithelp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmDoithelp.setTitle("DoIt! Help");
		frmDoithelp.setBounds(100, 100, 305, 500);
		
		JScrollPane scrollPane = new JScrollPane();
		frmDoithelp.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		JEditorPane editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editorPane.setContentType("text/html");
		scrollPane.setViewportView(editorPane);
		
		String helpText = getHTMLHelp("help");
		editorPane.setText(helpText);
	}

	@Override
	public void runUI() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GuiHelp window = GuiHelp.getInstance();
					window.frmDoithelp.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});		
	}

}
