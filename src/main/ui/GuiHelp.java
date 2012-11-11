package main.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JEditorPane;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

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
		frmDoithelp.setIconImage(Toolkit.getDefaultToolkit().getImage(GuiHelp.class.getResource("/resource/icon.png")));
		frmDoithelp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmDoithelp.setTitle("DoIt! Help");
		frmDoithelp.setBounds(100, 100, 305, 500);
		
		JScrollPane scrollPane = new JScrollPane();
		frmDoithelp.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		JEditorPane editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editorPane.setContentType("text/html");
		scrollPane.setViewportView(editorPane);
		
		URL helpTextUrl = this.getClass().getResource("/resource/help.html");
		try {
			editorPane.setPage(helpTextUrl);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
