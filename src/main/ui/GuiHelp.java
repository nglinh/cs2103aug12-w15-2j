package main.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JEditorPane;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import java.awt.Toolkit;
import java.io.IOException;
import java.net.URL;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent;

//@author A0086826R

public class GuiHelp extends UI{
	
	private static GuiHelp theOne;

	public static GuiHelp getInstance() {
		if (theOne == null) {
			theOne = new GuiHelp();
		}
		return theOne;
	}

	private JFrame frmDoithelp;
	private JEditorPane editorPane;

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
		frmDoithelp.setIconImage(Toolkit.getDefaultToolkit().getImage(
				GuiHelp.class.getResource("/resource/icon.png")));
		frmDoithelp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmDoithelp.setTitle("DoIt! Help");
		frmDoithelp.setBounds(100, 100, 305, 500);

		JScrollPane scrollPane = new JScrollPane();
		frmDoithelp.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		// add an html editor kit
        HTMLEditorKit helpHtmlKit = new HTMLEditorKit();
        
        // add some styles to the html
        StyleSheet helpStyleSheet = helpHtmlKit.getStyleSheet();
		
		helpStyleSheet.addRule(".help h1 {margin:5px 0px 0px 0px; padding:0px 0px 0px 0px;}");
		helpStyleSheet.addRule(".help h2 {margin:5px 0px 0px 0px; padding:0px 0px 0px 0px;}");
		helpStyleSheet.addRule(".help h3 {margin:5px 0px 0px 0px; padding:0px 0px 0px 0px;}");
		helpStyleSheet.addRule(".help p {margin:5px 0px 0px 0px; padding:0px 0px 0px 0px;}");
		
		Document helpHtmlDoc = helpHtmlKit.createDefaultDocument();

		editorPane = new JEditorPane();
		editorPane.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					log.finer("Hyperlink activated (calendar) " + e.getURL().getPath());
					editorPane.scrollToReference(e.getURL().getRef());
				}
			}
		});
		editorPane.setEditable(false);
		editorPane.setContentType("text/html");
		editorPane.setDocument(helpHtmlDoc);
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
