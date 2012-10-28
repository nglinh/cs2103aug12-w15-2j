package main.ui;

import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;

import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JEditorPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton.ToggleButtonModel;
import javax.swing.JViewport;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import main.shared.LogicToUi;
import main.shared.Task;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent;

import org.joda.time.DateTime;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GuiMain2 extends GuiCommandBox{

	private JFrame frame;
	protected JEditorPane txtDatedTasks;
	protected JEditorPane txtUndatedTasks;
	protected JEditorPane txtCalendar;
	protected JTextField txtCmd;
	protected JEditorPane txtStatus;
	protected JPopupMenu popupCmdHint;
	protected JEditorPane txtCmdHint;
	
	private static GuiMain2 theOne = null;
	private JPanel panelCmd;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane_1;
	private JMenuBar menuBar;
	private JMenu mnDebug;
	private JMenuItem mntmGetHtmlOf;
	//private JEditorPane txtStatus;
	//private JTextField txtCmd;
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
	protected void initialize() {
		super.initialize();
		
		frame = new JFrame();
		frame.setBounds(100, 100, 600, 620);
		
		JSplitPane splitPane = new JSplitPane();
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);
		splitPane.setDividerLocation(350);
		
		scrollPane = new JScrollPane();
		splitPane.setLeftComponent(scrollPane);
		
		txtDatedTasks = new JEditorPane();
		txtDatedTasks.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent e) {
			}
		});
		txtDatedTasks.setContentType("text/html");
		txtDatedTasks.setText("<html>\r\n<table>\r\n<tr>\r\n<td width=\"50\"><font face=\"Segoe UI\" size=1>TUE<br>SEP<br> <font size=\"4\">20</font><br> 2012</font></td>\r\n<td>\r\nBy 2:00pm<br>\r\nTask ABCD\r\n</td>\r\n</tr>\r\n</table>");
		txtDatedTasks.setEditable(false);
		scrollPane.setViewportView(txtDatedTasks);
		
		panelCmd = new JPanel();
		frame.getContentPane().add(panelCmd, BorderLayout.SOUTH);
		panelCmd.setLayout(new BorderLayout(0, 0));
		
		txtCmd = new JTextField();
		panelCmd.add(txtCmd, BorderLayout.NORTH);
		
		txtStatus = new JEditorPane();
		txtStatus.setText("Status goes here");
		panelCmd.add(txtStatus);
		
		popupCmdHint = new JPopupMenu();
		addPopup(txtCmd, popupCmdHint);
		
		txtCmdHint = new JEditorPane();
		popupCmdHint.add(txtCmdHint);
		
		configureWidgets(txtCmd, txtStatus, txtCmdHint, popupCmdHint);
		
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
		
		// Panel for right side
		JPanel panel = new JPanel();
		splitPane.setRightComponent(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		// Undated tasks pane
		scrollPane_1 = new JScrollPane();
		panel.add(scrollPane_1);		
		
		txtUndatedTasks = new JEditorPane();
		txtUndatedTasks.setContentType("text/html");
		txtUndatedTasks.setEditable(false);
		scrollPane_1.setViewportView(txtUndatedTasks);
		
		Document doc2 = kit.createDefaultDocument();		
		txtUndatedTasks.setDocument(doc2);
		
		// Calendar
		txtCalendar = new JEditorPane();
		txtCalendar.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					System.out.println(e.getURL().getPath());
					if(e.getURL().getPath().startsWith("/gotoMonth/")){
						String monthToShow = e.getURL().getPath().split("/")[2];
						int year = Integer.parseInt(monthToShow.split("-")[0]);
						int month = Integer.parseInt(monthToShow.split("-")[1]);						
						
						CalendarRenderer calr= new CalendarRenderer(new DateTime(year, month, 1,0,0), sendCommandToLogic("list").getList());
						txtCalendar.setText(calr.render());
					}else if(e.getURL().getPath().startsWith("/showTasksForDay/")){
						String monthToShow = e.getURL().getPath().split("/")[2];
						int year = Integer.parseInt(monthToShow.split("-")[0]);
						int month = Integer.parseInt(monthToShow.split("-")[1]);
						int day = Integer.parseInt(monthToShow.split("-")[2]);
						
						txtDatedTasks.scrollToReference("date-"+year+"-"+month+"-"+day);
					}
				}
			}
		});
		txtCalendar.setEditable(false);
		txtCalendar.setContentType("text/html");
		panel.add(txtCalendar, BorderLayout.SOUTH);
		
		StyleSheet styleSheet2 = kit.getStyleSheet();
        styleSheet2.addRule(".calendar td{text-align:right;}");
        
        Document doc3 = kit.createDefaultDocument();
        txtCalendar.setDocument(doc3);
        
        menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        
        mnDebug = new JMenu("Debug");
        menuBar.add(mnDebug);
        
        mntmGetHtmlOf = new JMenuItem("Get HTML of right pane");
        mntmGetHtmlOf.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		HTMLDocument doc = (HTMLDocument)txtUndatedTasks.getDocument();
        		ElementIterator it = new ElementIterator(doc);
                Element element;
        		
        		while ( (element = it.next()) != null )
                {
                    System.out.println();

                    AttributeSet as = element.getAttributes();
                    Enumeration enumm = as.getAttributeNames();

                    while( enumm.hasMoreElements() )
                    {
                        Object name = enumm.nextElement();
                        Object value = as.getAttribute( name );
                        System.out.println( "\t" + name + " : " + value );

                        if (value instanceof ToggleButtonModel)
                        {
                        	ToggleButtonModel model = (ToggleButtonModel)value;
                        	model.addActionListener(new ActionListener(){
                        		public void actionPerformed(ActionEvent e){
                        			System.out.println(((ToggleButtonModel)e.getSource()).isSelected());
                        		}
                        	});
                            System.out.println(model.isSelected());
                        }
                    }
                }
        	}
        });
        mnDebug.add(mntmGetHtmlOf);
		
		String fileStatus = checkFilePermissions();		
		executeCommand("list");
		showStatus(fileStatus);
		
	}
	
	public void update(LogicToUi returnValue){
		
		// Call command to refresh the table
		showTasksList(sendCommandToLogic("refresh").getList());
	
		if (returnValue.containsList()) {
			showTasksList(returnValue.getList());
		}
		
		showStatus(returnValue.getString());
	}
	
	public void showTasksList(List<Task> taskList){
		
		int txtDatedTasksCaretPos = 0, txtUndatedTasksCaretPos = 0;
		try{
			int txtDatedTasksScrollBarPos = ((JScrollPane) txtDatedTasks.getParent().getParent()).getVerticalScrollBar().getValue();
			int txtDatedTasksScrollPaneMiddle = ((JScrollPane) txtDatedTasks.getParent().getParent()).getHeight() /2 ;
			if (txtDatedTasksScrollBarPos >= 1){
				txtDatedTasks.setCaretPosition(txtDatedTasks.viewToModel(new Point(0,txtDatedTasksScrollBarPos + txtDatedTasksScrollPaneMiddle)));
			}
			txtDatedTasksCaretPos = txtDatedTasks.getCaretPosition();
			
			int txtUndatedTasksScrollBarPos = ((JScrollPane) txtUndatedTasks.getParent().getParent()).getVerticalScrollBar().getValue();
			int txtUndatedTasksScrollPaneMiddle = ((JScrollPane) txtUndatedTasks.getParent().getParent()).getHeight() /2 ;
			if (txtUndatedTasksScrollBarPos >= 1){
				txtUndatedTasks.setCaretPosition(txtUndatedTasks.viewToModel(new Point(0,txtUndatedTasksScrollBarPos + txtUndatedTasksScrollPaneMiddle)));
			}
			txtUndatedTasksCaretPos = txtUndatedTasks.getCaretPosition();
		}catch(IllegalArgumentException e){
			e.printStackTrace();
		}
				
		DatedTaskListRenderer dtr = new DatedTaskListRenderer(taskList);
		txtDatedTasks.setText(dtr.render());
				
		UndatedTaskListRenderer udtr = new UndatedTaskListRenderer(taskList);
		txtUndatedTasks.setText(udtr.render());
		
		// We need to enclose the following in a try-catch block as an exception is thrown
		// if the caret position requested is beyond the length of the contents
		// This can happen if the new list is shorter than the current list.
		try {
			txtDatedTasks.setCaretPosition(txtDatedTasksCaretPos);
			txtUndatedTasks.setCaretPosition(txtUndatedTasksCaretPos);
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		CalendarRenderer calr = new CalendarRenderer(new DateTime(), taskList);
		txtCalendar.setText(calr.render());
		
		try {
			System.out.println(txtDatedTasks.modelToView(txtDatedTasks.getCaretPosition()));
			System.out.println((((JScrollPane) txtDatedTasks.getParent().getParent()).getVerticalScrollBar().getValue()));
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(dtr.getIndexList());
		System.out.println(udtr.getIndexList());
	}

	@Override
	public void runUI() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GuiMain2 window = new GuiMain2();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}

}
