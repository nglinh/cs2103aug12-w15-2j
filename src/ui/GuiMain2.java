package ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JEditorPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import shared.Task;

import java.awt.Component;
import javax.swing.JPanel;

public class GuiMain2 extends UI{

	private JFrame frame;
	private JTextField textField;
	
	private static GuiMain2 theOne = null;
	public static GuiMain2 getInstance(){
		if (theOne == null){
			theOne = new GuiMain2();
		}
		return theOne;
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		/*EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GuiMain2 window = new GuiMain2();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});*/
		GuiMain2.getInstance().runUI();
	}

	/**
	 * Create the application.
	 */
	private GuiMain2() {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 600, 620);
		
		JSplitPane splitPane = new JSplitPane();
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);
		splitPane.setDividerLocation(350);
		
		JScrollPane scrollPane = new JScrollPane();
		splitPane.setLeftComponent(scrollPane);
		
		JEditorPane txtDatedTasks = new JEditorPane();
		txtDatedTasks.setContentType("text/html");
		txtDatedTasks.setText("<html>\r\n<table>\r\n<tr>\r\n<td width=\"50\"><font face=\"Segoe UI\" size=1>TUE<br>SEP<br> <font size=\"4\">20</font><br> 2012</font></td>\r\n<td>\r\nBy 2:00pm<br>\r\nTask ABCD\r\n</td>\r\n</tr>\r\n</table>");
		txtDatedTasks.setEditable(false);
		scrollPane.setViewportView(txtDatedTasks);
		
		textField = new JTextField();
		frame.getContentPane().add(textField, BorderLayout.SOUTH);
		
		// Dated tasks pane
		HTMLEditorKit kit = new HTMLEditorKit();
        txtDatedTasks.setEditorKit(kit);

        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule("body {color:#000; font-family:Segoe UI;}");
        styleSheet.addRule("table {color:#000; font-family:Segoe UI;}");
        styleSheet.addRule(".calendarbox {border:1px solid #2A5696; color:#000000; width:40px;}");
        styleSheet.addRule(".calendarbox .calendardayofweek{background-color:#2A5696; color:#FFFFFF; width:40px;}");
        styleSheet.addRule(".taskbox{margin-bottom:10px;}");
        
        Document doc = kit.createDefaultDocument();
        txtDatedTasks.setDocument(doc);
        
        DatedTaskListRenderer dtr = new DatedTaskListRenderer(sendCommandToLogic("list").getList());
		txtDatedTasks.setText(dtr.render());
		
		// Panel for right side
		JPanel panel = new JPanel();
		splitPane.setRightComponent(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		// Undated tasks pane
		JScrollPane scrollPane_1 = new JScrollPane();
		panel.add(scrollPane_1);		
		
		JEditorPane txtUndatedTasks = new JEditorPane();
		txtUndatedTasks.setContentType("text/html");
		txtUndatedTasks.setEditable(false);
		scrollPane_1.setViewportView(txtUndatedTasks);
		
		Document doc2 = kit.createDefaultDocument();		
		UndatedTaskListRenderer udtr = new UndatedTaskListRenderer(sendCommandToLogic("list").getList());
		
		txtUndatedTasks.setDocument(doc2);
		txtUndatedTasks.setText(udtr.render());
		
		// Calendar
		JEditorPane txtCalendar = new JEditorPane();
		txtCalendar.setEditable(false);
		txtCalendar.setContentType("text/html");
		panel.add(txtCalendar, BorderLayout.SOUTH);
		
		StyleSheet styleSheet2 = kit.getStyleSheet();
        styleSheet2.addRule(".calendar td{text-align:right;}");
        
        Document doc3 = kit.createDefaultDocument();
        txtCalendar.setDocument(doc3);
		
		CalendarRenderer calr= new CalendarRenderer();
		txtCalendar.setText(calr.render());
		txtCalendar.addHyperlinkListener(calr);
		
	}

	@Override
	public void runUI() {
		// TODO Auto-generated method stub
		frame.setVisible(true);
		
	}

}
