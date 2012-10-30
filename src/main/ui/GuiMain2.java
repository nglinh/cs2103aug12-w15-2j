package main.ui;

import java.awt.EventQueue;
import java.awt.Point;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;

import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JEditorPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton.ToggleButtonModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import main.LogHandler;
import main.shared.LogicToUi;
import main.shared.Task;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	
	Logger log = LogHandler.getLogInstance();

	private JFrame frame;
	protected JEditorPane txtDatedTasks;
	protected JEditorPane txtUndatedTasks;
	protected JEditorPane txtCalendar;
	protected JTextField txtCmd;
	protected JEditorPane txtStatus;
	protected JPopupMenu popupCmdHint;
	protected JEditorPane txtCmdHint;
	
	
	private JPanel panelCmd;
	private JScrollPane scrollPaneDated;
	private JScrollPane scrollPaneUndated;
	private JMenuBar menuBar;
	private JMenu mnDebug;
	private JMenuItem mntmGetHtmlOf;
	//private JEditorPane txtStatus;
	//private JTextField txtCmd;
	
	private static GuiMain2 theOne = null;
	public static GuiMain2 getInstance(){
		LogHandler.getLogInstance().info("Getting instance of GuiMain2");
		if (theOne == null){
			LogHandler.getLogInstance().info("Instance does not exist, creating GuiMain2");
			theOne = new GuiMain2();
		}
		return theOne;
	}
	
	Map<ToggleButtonModel, Integer> checkboxToIndexMapForDated;
	Map<ToggleButtonModel, Integer> checkboxToIndexMapForUndated;
	private JMenuItem mntmRefresh;

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
		log.entering(this.getClass().getName(), "<init>");
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			//e.printStackTrace();
			log.log(Level.WARNING, "Error encountered when setting look and feel", e);
		}
		
		initialize();
		log.exiting(this.getClass().getName(), "<init>");
	}

	/**
	 * Initialize the contents of the frame.
	 */
	protected void initialize() {
		log.entering(this.getClass().getName(), "initialize");
				
		frame = new JFrame();
		frame.setBounds(100, 100, 600, 620);
		
		JSplitPane splitPane = new JSplitPane();
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);
		splitPane.setDividerLocation(350);
		
		scrollPaneDated = new JScrollPane();
		splitPane.setLeftComponent(scrollPaneDated);
		
		txtDatedTasks = new JEditorPane();
		txtDatedTasks.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent e) {
			}
		});
		txtDatedTasks.setContentType("text/html");
		txtDatedTasks.setText("<html>\r\n<table>\r\n<tr>\r\n<td width=\"50\"><font face=\"Segoe UI\" size=1>TUE<br>SEP<br> <font size=\"4\">20</font><br> 2012</font></td>\r\n<td>\r\nBy 2:00pm<br>\r\nTask ABCD\r\n</td>\r\n</tr>\r\n</table>");
		txtDatedTasks.setEditable(false);
		scrollPaneDated.setViewportView(txtDatedTasks);
		
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
		
		log.info("Calling GuiCommandBox to configure widgets");
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
        styleSheet.addRule(".separatorfirst{font-size:1px;border-width:0px;}");
        styleSheet.addRule(".separator{font-size:1px;border:1px solid #DDDDDD; border-width:1px 0px 0px 0px;}");
        
        Document doc = kit.createDefaultDocument();
        txtDatedTasks.setDocument(doc);
		
		// Panel for right side
		JPanel panel = new JPanel();
		splitPane.setRightComponent(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		// Undated tasks pane
		scrollPaneUndated = new JScrollPane();
		panel.add(scrollPaneUndated);		
		
		txtUndatedTasks = new JEditorPane();
		txtUndatedTasks.setContentType("text/html");
		txtUndatedTasks.setEditable(false);
		scrollPaneUndated.setViewportView(txtUndatedTasks);
		
		Document doc2 = kit.createDefaultDocument();		
		txtUndatedTasks.setDocument(doc2);
		
		// Calendar
		txtCalendar = new JEditorPane();
		txtCalendar.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent e) {
				log.entering(this.getClass().getName(), "hyperLinkUpdate (calendar)");
				
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					log.finer("Hyperlink activated (calendar) " + e.getURL().getPath());
					//System.out.println(e.getURL().getPath());
					if(e.getURL().getPath().startsWith("/gotoMonth/")){
						log.finer("Hyperlink go to month action (calendar)");
						String monthToShow = e.getURL().getPath().split("/")[2];
						int year = Integer.parseInt(monthToShow.split("-")[0]);
						int month = Integer.parseInt(monthToShow.split("-")[1]);						
						
						log.finer("Hyperlink go to month action (calendar)");
						CalendarRenderer calr = new CalendarRenderer(new DateTime(year, month, 1,0,0), sendCommandToLogic("list").getList());
						log.info("Rendering calendar for " + new DateTime(year, month, 1,0,0));
						txtCalendar.setText(calr.render());
					}else if(e.getURL().getPath().startsWith("/showTasksForDay/")){
						log.finer("Hyperlink go to date with tasks action (calendar)");
						String monthToShow = e.getURL().getPath().split("/")[2];
						int year = Integer.parseInt(monthToShow.split("-")[0]);
						int month = Integer.parseInt(monthToShow.split("-")[1]);
						int day = Integer.parseInt(monthToShow.split("-")[2]);
						
						String dateReferenceStr = "date-"+year+"-"+month+"-"+day;
						log.info("Scroll to reference " + dateReferenceStr);
						txtDatedTasks.scrollToReference(dateReferenceStr);
					}
				}
			}
		});
		txtCalendar.setEditable(false);
		txtCalendar.setContentType("text/html");
		panel.add(txtCalendar, BorderLayout.SOUTH);
		
		StyleSheet styleSheet2 = kit.getStyleSheet();
        styleSheet2.addRule(".calendar td{text-align:right;}");
        styleSheet2.addRule("a {color:#2A5696;text-decoration:none;}");
        
        Document doc3 = kit.createDefaultDocument();
        txtCalendar.setDocument(doc3);
        
        menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        
        mnDebug = new JMenu("Debug");
        menuBar.add(mnDebug);
        
        mntmGetHtmlOf = new JMenuItem("Get HTML of right pane");
        mntmGetHtmlOf.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		/*HTMLDocument doc = (HTMLDocument)txtUndatedTasks.getDocument();
        		ElementIterator it = new ElementIterator(doc);
                Element element;
        		
        		while ( (element = it.next()) != null )
                {
                    System.out.println();

                    AttributeSet as = element.getAttributes();
                    Enumeration<?> enumm = as.getAttributeNames();

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
                }*/
        	}
        });
        mnDebug.add(mntmGetHtmlOf);
        
        mntmRefresh = new JMenuItem("Refresh");
        mntmRefresh.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		update(null);
        	}
        });
        mnDebug.add(mntmRefresh);
		
        log.info("Checking file permissions");
		String fileStatus = checkFilePermissions();		
		// executeCommand("list");
		// Note we do not use executeCommand here, as doing so will cause a further update
		// request to be propagated to all windows.
		showTasksList(sendCommandToLogic("refresh").getList());
		showStatus(fileStatus);
		
		log.exiting(this.getClass().getName(), "initialize");
	}
	
	public void update(LogicToUi returnValue){
		log.entering(this.getClass().getName(), "update (LogicToUi)");
		
		// Call command to refresh the table
		showTasksList(sendCommandToLogic("refresh").getList());
	
		if (returnValue.containsList()) {
			log.finer("Return value has list");
			showTasksList(returnValue.getList());
		}
		
		showStatus(returnValue.getString());
		
		// Update other windows
		//GuiMain.getInstance().updateWindow(this);
		//GuiQuickAdd.getInstance().updateWindow(this);
		GuiUpdate.update(this);
		
		log.exiting(this.getClass().getName(), "update (LogicToUi)");
	}
	
	public void updateWindow(Object source) {
		log.entering(this.getClass().getName(), "updateWindow");
		if(source != this){
			LogicToUi result = sendCommandToLogic("refresh");
			showTasksList(result.getList());
			showStatus(result.getString());
		}
		//executeCommand("refresh");
		log.exiting(this.getClass().getName(), "updateWindow");
	}
	
	public void showTasksList(List<Task> taskList){
		log.entering(this.getClass().getName(), "taskList");
		
		// This code moves the current caret position to the middle of the current
		// view, and stores it. After updating, we move the caret back to the same position
		// to keep the same view in place.
		int txtDatedTasksCaretPos = 0, txtUndatedTasksCaretPos = 0;
		try{
			log.finer("Dated tasks scroll bar pos:" + ((JScrollPane) txtDatedTasks.getParent().getParent()).getVerticalScrollBar().getValue());
			int txtDatedTasksScrollBarPos = ((JScrollPane) txtDatedTasks.getParent().getParent()).getVerticalScrollBar().getValue();
			int txtDatedTasksScrollPaneMiddle = ((JScrollPane) txtDatedTasks.getParent().getParent()).getHeight() /2 ;
			log.fine("Dated tasks caret position: " + txtDatedTasks.getCaretPosition());
			if (txtDatedTasksScrollBarPos >= 1){
				txtDatedTasks.setCaretPosition(txtDatedTasks.viewToModel(new Point(0,txtDatedTasksScrollBarPos + txtDatedTasksScrollPaneMiddle)));
				log.fine("New dated tasks caret position: " + txtDatedTasks.getCaretPosition());
			}
			txtDatedTasksCaretPos = txtDatedTasks.getCaretPosition();
			
			log.finer("Undated tasks scroll bar pos:" + ((JScrollPane) txtUndatedTasks.getParent().getParent()).getVerticalScrollBar().getValue());
			int txtUndatedTasksScrollBarPos = ((JScrollPane) txtUndatedTasks.getParent().getParent()).getVerticalScrollBar().getValue();
			int txtUndatedTasksScrollPaneMiddle = ((JScrollPane) txtUndatedTasks.getParent().getParent()).getHeight() /2 ;
			log.fine("Undated tasks caret position: " + txtUndatedTasks.getCaretPosition());
			if (txtUndatedTasksScrollBarPos >= 1){
				txtUndatedTasks.setCaretPosition(txtUndatedTasks.viewToModel(new Point(0,txtUndatedTasksScrollBarPos + txtUndatedTasksScrollPaneMiddle)));
				log.fine("New undated tasks caret position: " + txtUndatedTasks.getCaretPosition());
			}
			txtUndatedTasksCaretPos = txtUndatedTasks.getCaretPosition();
		}catch(IllegalArgumentException e){
			//e.printStackTrace();
			log.log(Level.WARNING, "Error with caret position before updating list", e);
		}
				
		DatedTaskListRenderer dtr = new DatedTaskListRenderer(taskList);
		String datedTaskListHtml = dtr.render();
		log.finest(datedTaskListHtml);
		txtDatedTasks.setText(datedTaskListHtml);
				
		UndatedTaskListRenderer udtr = new UndatedTaskListRenderer(taskList);
		String undatedTaskListHtml = udtr.render();
		log.finest(undatedTaskListHtml);
		txtUndatedTasks.setText(undatedTaskListHtml);
		
		// We need to enclose the following in a try-catch block as an exception is thrown
		// if the caret position requested is beyond the length of the contents
		// This can happen if the new list is shorter than the current list.
		try {
			log.finer("Setting caret position for dated tasks to " + txtDatedTasksCaretPos);
			txtDatedTasks.setCaretPosition(txtDatedTasksCaretPos);
			log.finer("Setting caret position for undated tasks to " + txtUndatedTasksCaretPos);
			txtUndatedTasks.setCaretPosition(txtUndatedTasksCaretPos);
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
			log.log(Level.WARNING, "Error with caret position after updating list", e1);
		}
		
		CalendarRenderer calr = new CalendarRenderer(new DateTime(), taskList);
		txtCalendar.setText(calr.render());
		
		/*
		try {
			System.out.println(txtDatedTasks.modelToView(txtDatedTasks.getCaretPosition()));
			System.out.println((((JScrollPane) txtDatedTasks.getParent().getParent()).getVerticalScrollBar().getValue()));
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		log.finest("Dated task index list: " + dtr.getIndexList());
		log.finest("Undated task index list: " + udtr.getIndexList());
		
		// Checkbox handler
		
		// Note that we use HashMap here instead of other Map such as TreeMap
		// because ToggleButtonModel is not comparable
		checkboxToIndexMapForDated = new HashMap<ToggleButtonModel, Integer>();
		checkboxToIndexMapForUndated = new HashMap<ToggleButtonModel, Integer>();
		
		// Checkbox handler for dated tasks column
		int i = 0;
			
		HTMLDocument doc = (HTMLDocument)txtDatedTasks.getDocument();
		ElementIterator it = new ElementIterator(doc);
        Element element;
		
        StringBuffer elementEnumeratorDebugString = new StringBuffer();
		while ( (element = it.next()) != null )
        {
			elementEnumeratorDebugString.append("\n");

            AttributeSet as = element.getAttributes();
            Enumeration<?> enumm = as.getAttributeNames();

            while( enumm.hasMoreElements() )
            {
                Object name = enumm.nextElement();
                Object value = as.getAttribute( name );
                elementEnumeratorDebugString.append( "\t" + name + " : " + value + "\n");

                if (value instanceof ToggleButtonModel)
                {
                	ToggleButtonModel model = (ToggleButtonModel)value;
                	checkboxToIndexMapForDated.put(model, dtr.getIndexList().get(i++));
                	model.addActionListener(new ActionListener(){
                		public void actionPerformed(ActionEvent e){
                			log.info(checkboxToIndexMapForDated.get(e.getSource()).toString() + "\n" + ((ToggleButtonModel)e.getSource()).isSelected());
                			boolean isDone = ((ToggleButtonModel)e.getSource()).isSelected();
                			int index = checkboxToIndexMapForDated.get(e.getSource());
                			if(isDone){
                				executeCommand("done " + index);
                			}else{
                				executeCommand("undone " + index);
                			}
                		}
                	});
                	elementEnumeratorDebugString.append(model.isSelected());
                }
            }
		}
		log.finest("Dated element enumerator data: " + elementEnumeratorDebugString.toString());
		
		// Checkbox handler for undated tasks column
		i = 0;
			
		HTMLDocument doc2 = (HTMLDocument)txtUndatedTasks.getDocument();
		it = new ElementIterator(doc2);
		
		elementEnumeratorDebugString = new StringBuffer();
		while ( (element = it.next()) != null )
        {
			elementEnumeratorDebugString.append("\n");

            AttributeSet as = element.getAttributes();
            Enumeration<?> enumm = as.getAttributeNames();

            while( enumm.hasMoreElements() )
            {
                Object name = enumm.nextElement();
                Object value = as.getAttribute( name );
                elementEnumeratorDebugString.append( "\t" + name + " : " + value + "\n");

                if (value instanceof ToggleButtonModel)
                {
                	ToggleButtonModel model = (ToggleButtonModel)value;
                	checkboxToIndexMapForUndated.put(model, udtr.getIndexList().get(i++));
                	model.addActionListener(new ActionListener(){
                		public void actionPerformed(ActionEvent e){
                			//System.out.println(checkboxToIndexMapForUndated.get(e.getSource()));
                			//System.out.println(((ToggleButtonModel)e.getSource()).isSelected());
                			log.info(checkboxToIndexMapForDated.get(e.getSource()).toString() + "\n" + ((ToggleButtonModel)e.getSource()).isSelected());
                			boolean isDone = ((ToggleButtonModel)e.getSource()).isSelected();
                			int index = checkboxToIndexMapForUndated.get(e.getSource());
                			if(isDone){
                				executeCommand("done " + index);
                			}else{
                				executeCommand("undone " + index);
                			}
                		}
                	});
                    //System.out.println(model.isSelected());
                }
            }
		}
		log.finest("Undated element enumerator data: " + elementEnumeratorDebugString.toString());
		
		log.exiting(this.getClass().getName(), "showTasksList");
	}

	@Override
	public void runUI() {
		log.entering(this.getClass().getName(), "runUI");
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//GuiMain2 window = new GuiMain2();
					GuiMain2 window = GuiMain2.getInstance();
					window.frame.setVisible(true);
				} catch (Exception e) {
					//e.printStackTrace();
					log.log(Level.WARNING, "Error launching GuiMain2", e);
				}
			}
		});
		log.exiting(this.getClass().getName(), "runUI");
	}

}
