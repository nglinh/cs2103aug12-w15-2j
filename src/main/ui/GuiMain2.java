package main.ui;

import java.awt.Color;
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
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import main.LogHandler;
import main.shared.LogicToUi;
import main.shared.Task;
import main.shared.Task.TaskType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

import javax.swing.JPanel;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import edu.emory.mathcs.backport.java.util.Collections;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import java.awt.CardLayout;
import java.awt.Component;
import javax.swing.Box;
import java.awt.Toolkit;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowEvent;

public class GuiMain2 extends GuiCommandBox{
	
	private static final int TABLE_COLUMN_WIDTH_CHECKBOX = 20;
	private static final String TABLE_COLUMN_WIDTH_DATE_MAX_TEXT = "WMW 00 MWM 0000 23:59pm";
	private static final String TABLE_COLUMN_WIDTH_INDEX_MAX_TEXT = "9999";
	private static final int TABLE_COLUMN_WIDTH_EXTRA = 10;
	private static final String TABLE_EMPTY_DATE_FIELD = "";
	public static final String CARD_AGENDA = "agendaCard";
	public static final String CARD_LIST = "listCard";

	Logger log = LogHandler.getLogInstance();

	private JFrame frmDoit;
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
	//private JEditorPane txtStatus;
	//private JTextField txtCmd;
	
	private DateTime lastShownCalendarDate;
	private Set<DateTime> datesOfTasks;
	
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
	private JToolBar toolBar;
	private JButton btnHome;
	private JButton btnUndo;
	private JToggleButton tglbtnAgendaView;
	private JToggleButton tglbtnListView;
	private JButton btnPreferences;
	private JPanel panelCards;

	private JTable table;

	private JScrollPane scrollPaneTable;
	private JButton btnHelp;
	private JPanel panel_1;
	private JTextField txtTaskEdit;
	private Component horizontalGlue;
	private Component horizontalGlue_1;
	
	private Preferences prefs;
	
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
		setUiLookAndFeel();
		
		prefs = Preferences.userNodeForPackage(this.getClass());
		prefs.addPreferenceChangeListener(new PreferenceChangeListener(){

			@Override
			public void preferenceChange(PreferenceChangeEvent arg0) {
				preferenceShowHint = prefs.getBoolean(GuiPreferences.SHOW_HINTS, true);				
			}
			
		});
		
		initialize();
		log.exiting(this.getClass().getName(), "<init>");
	}

	/**
	 * Initialize the contents of the frame.
	 * @wbp.parser.entryPoint
	 */
	@SuppressWarnings("serial")
	protected void initialize() {
		log.entering(this.getClass().getName(), "initialize");
				
		frmDoit = new JFrame();
		frmDoit.addWindowFocusListener(new WindowFocusListener() {
			public void windowGainedFocus(WindowEvent arg0) {
				txtCmd.requestFocusInWindow();
			}
			public void windowLostFocus(WindowEvent arg0) {
			}
		});
		frmDoit.setIconImage(Toolkit.getDefaultToolkit().getImage(GuiMain2.class.getResource("/resource/icon.png")));
		frmDoit.setTitle("DoIt!");
		frmDoit.setBounds(100, 100, 800, 620);
		
		frmDoit.addComponentListener(new ComponentListener(){
			@Override
			public void componentResized(ComponentEvent e) {
		        popupCmdHint.setVisible(false);
		    }

			@Override
			public void componentHidden(ComponentEvent arg0) {
				popupCmdHint.setVisible(false);		
			}

			@Override
			public void componentMoved(ComponentEvent arg0) {
				popupCmdHint.setVisible(false);
			}

			@Override
			public void componentShown(ComponentEvent arg0) {
				popupCmdHint.setVisible(false);
			}
		});
		
		toolBar = new JToolBar();
		toolBar.setFloatable(false);
		frmDoit.getContentPane().add(toolBar, BorderLayout.NORTH);
		
		btnUndo = new JButton("Undo");
		btnUndo.setIcon(new ImageIcon(GuiMain2.class.getResource("/resource/arrow_undo.png")));
		btnUndo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				executeCommand("undo");
			}
		});
		
		btnHome = new JButton("Home");
		btnHome.setIcon(new ImageIcon(GuiMain2.class.getResource("/resource/house.png")));
		btnHome.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				executeCommand("list");
				executeCommand("sort");
				if(prefs.getBoolean(GuiPreferences.HOME_GOES_TO_TODAY, true)){
					jumpToTasksToday();
				}
				if(prefs.getBoolean(GuiPreferences.HOME_GOES_TO_DEFAULT_VIEW, true)){
					switchCard(prefs.get(GuiPreferences.DEFAULT_VIEW, GuiMain2.CARD_AGENDA));
				}
			}
		});
		toolBar.add(btnHome);
		toolBar.add(btnUndo);
		
		tglbtnAgendaView = new JToggleButton("Agenda View");
		tglbtnAgendaView.setIcon(new ImageIcon(GuiMain2.class.getResource("/resource/calendar_view_day.png")));
		tglbtnAgendaView.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switchCard(CARD_AGENDA);
			}
		});
		
		horizontalGlue_1 = Box.createHorizontalGlue();
		toolBar.add(horizontalGlue_1);
		toolBar.add(tglbtnAgendaView);
		
		tglbtnListView = new JToggleButton("List View");
		tglbtnListView.setIcon(new ImageIcon(GuiMain2.class.getResource("/resource/calendar_view_week.png")));
		tglbtnListView.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switchCard(CARD_LIST);
			}
		});
		toolBar.add(tglbtnListView);
		
		btnHelp = new JButton("Help");
		btnHelp.setIcon(new ImageIcon(GuiMain2.class.getResource("/resource/help.png")));
		btnHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GuiHelp.getInstance().runUI();
			}
		});
		
		horizontalGlue = Box.createHorizontalGlue();
		toolBar.add(horizontalGlue);
		
		btnPreferences = new JButton("Preferences");
		btnPreferences.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				GuiPreferences.getInstance().runUI();
			}
		});
		btnPreferences.setIcon(new ImageIcon(GuiMain2.class.getResource("/resource/wrench_orange.png")));
		toolBar.add(btnPreferences);
		toolBar.add(btnHelp);
		
		panelCards = new JPanel();
		frmDoit.getContentPane().add(panelCards, BorderLayout.CENTER);
		panelCards.setLayout(new CardLayout(0, 0));
		
		panel_1 = new JPanel();
		panelCards.add(panel_1, CARD_AGENDA);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		// *************************
		// * Panel for Agenda View *
		// *************************
		
		JSplitPane splitPane = new JSplitPane();
		panel_1.add(splitPane);
		splitPane.setDividerLocation(450);
		
		scrollPaneDated = new JScrollPane();
		scrollPaneDated.getVerticalScrollBar().putClientProperty("JComponent.sizeVariant", "small");
		scrollPaneDated.getHorizontalScrollBar().putClientProperty("JComponent.sizeVariant", "small");
		splitPane.setLeftComponent(scrollPaneDated);
		
		txtDatedTasks = new JEditorPane();
		txtDatedTasks.addHyperlinkListener(new TasksHyperlinkHandler());
		txtDatedTasks.setContentType("text/html");
		txtDatedTasks.setText("<html>\r\n<table>\r\n<tr>\r\n<td width=\"50\"><font size=1>TUE<br>SEP<br> <font size=\"4\">20</font><br> 2012</font></td>\r\n<td>\r\nBy 2:00pm<br>\r\nTask ABCD\r\n</td>\r\n</tr>\r\n</table>");
		txtDatedTasks.setEditable(false);
        txtDatedTasks.setEditorKit(generateDatedTasksDocumentStyle());
		scrollPaneDated.setViewportView(txtDatedTasks);
		
		// Panel for right side
		JPanel panel = new JPanel();
		splitPane.setRightComponent(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		// Undated tasks pane
		scrollPaneUndated = new JScrollPane();
		scrollPaneUndated.getVerticalScrollBar().putClientProperty("JComponent.sizeVariant", "small");
		scrollPaneUndated.getHorizontalScrollBar().putClientProperty("JComponent.sizeVariant", "small");
		panel.add(scrollPaneUndated);
		
		txtUndatedTasks = new JEditorPane();
		txtUndatedTasks.setContentType("text/html");
		txtUndatedTasks.setEditable(false);
		txtUndatedTasks.addHyperlinkListener(new TasksHyperlinkHandler());
		scrollPaneUndated.setViewportView(txtUndatedTasks);
		
		// Calendar
		txtCalendar = new JEditorPane();
		txtCalendar.addHyperlinkListener(new CalendarHyperLinkHandler());
		txtCalendar.setEditable(false);
		txtCalendar.setContentType("text/html");
		txtCalendar.setBackground(new Color(0, 0, 0, 0));
		txtCalendar.setOpaque(false);
		panel.add(txtCalendar, BorderLayout.SOUTH);
		
        txtCalendar.setEditorKit(generateCalendarDocumentStyle());
        
        txtTaskEdit = new JTextField();
        txtTaskEdit.setText("txtTaskEdit");
        panel_1.add(txtTaskEdit, BorderLayout.NORTH);
        txtTaskEdit.setColumns(10);
        
        // ***********************
        // * Panel for List View *
        // ***********************
        
        scrollPaneTable = new JScrollPane();

		table = new JTable();
		table.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				log.finest("Key pressed: " + arg0.getKeyCode());
				if(arg0.getKeyCode() == java.awt.event.KeyEvent.VK_DELETE){
					log.finer("Delete key pressed");
					log.info("Deleting selected tasks");
					int[] rowsToDelete = table.getSelectedRows();
					log.info("Rows to delete: "+ Arrays.toString(rowsToDelete));
					
					String command = "delete ";
					for(int i = 0; i<rowsToDelete.length; i++){
						command += (int) table.getModel().getValueAt(rowsToDelete[i], MyTableModel.COL_INDEX) + " ";
					}
					executeCommand(command);
				}
			}
		});
		// Dummy table for WindowBuilder, the table will be filled up by code below
		table.setModel(new DefaultTableModel(
				new Object[][] {
						{new Integer(1), null, "-", "-", "-"},
				},
				new String[] {
						"Idx", "", "Start/Deadline", "End", "What to do?"
				}
				) {
			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] {
				Integer.class, Boolean.class, Object.class, Object.class, String.class
			};
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		table.getColumnModel().getColumn(0).setPreferredWidth(25);
		table.getColumnModel().getColumn(1).setPreferredWidth(20);
		table.getColumnModel().getColumn(2).setPreferredWidth(110);
		table.getColumnModel().getColumn(3).setPreferredWidth(110);
		table.getColumnModel().getColumn(4).setPreferredWidth(160);
		showTasksListInTable(new ArrayList<Task>());

		scrollPaneTable.setViewportView(table);
		panelCards.add(scrollPaneTable, CARD_LIST);       
        
        // *************************
        // * Panel for command box *
        // *************************
        
        panelCmd = new JPanel();
		frmDoit.getContentPane().add(panelCmd, BorderLayout.SOUTH);
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
		
		/*btnNewButton = new JButton("Undo");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				executeCommand("undo");
			}
		});
		btnNewButton.setIcon(new ImageIcon(GuiMain2.class.getResource("/resource/arrow_undo.png")));
		panelCmd.add(btnNewButton, BorderLayout.EAST);*/
		
        log.info("Checking file permissions");
		String fileStatus = checkFilePermissions();		
		// executeCommand("list");
		// Note we do not use executeCommand here, as doing so will cause a further update
		// request to be propagated to all windows.
		
		// First launch 
		lastShownCalendarDate = new DateTime();
		List<Task> firstLaunchTasks = sendCommandToLogic("refresh").getList();
		if(firstLaunchTasks.isEmpty()){
			//txtDatedTasks.setText(Hint.getInstance().helpForThisCommandHTML("help"));
			txtDatedTasks.setText("Placeholder text for first launch with no tasks");
		}else{
			showTasksList(firstLaunchTasks);
		}
		
		txtTaskEdit.setVisible(false);
		switchCard(prefs.get(GuiPreferences.DEFAULT_VIEW, GuiMain2.CARD_AGENDA));

		showStatus(fileStatus);
		
		SwingUtilities.updateComponentTreeUI(frmDoit);
		
		log.exiting(this.getClass().getName(), "initialize");
	}

	private HTMLEditorKit generateCalendarDocumentStyle() {
		HTMLEditorKit kit = new HTMLEditorKit();
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule(".calendar {color:#000000;text-decoration:none;}");
        styleSheet.addRule(".calendar td, .calendarDate td{text-align:right;}");
        styleSheet.addRule(".calendarTitle a {color:#000000;text-decoration:none;}");
        styleSheet.addRule(".calendarDate a {color:#000000;text-decoration:none;font-weight:bold;}");
        styleSheet.addRule(".calendarDateWrongMonth{color:#4D7E99;text-decoration:none;}");
        styleSheet.addRule(".calendarDateWrongMonth a{color:#4D7E99;text-decoration:none;font-weight:bold;}");
        //styleSheet.addRule(".calendarDateWithTask{background-color:#FFAA00;}");
        //styleSheet3.addRule(".calendarDate{padding-right;5px;}");
        //Document doc = kit.createDefaultDocument();
		//return doc;
        return kit;
	}

	private HTMLEditorKit generateDatedTasksDocumentStyle() {
		HTMLEditorKit kit = new HTMLEditorKit();
        //txtDatedTasks.setEditorKit(kit);

        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule("body {color:#000; font-size:12pt;}");
        styleSheet.addRule("table {color:#000; font-size:12pt;}");
        styleSheet.addRule("td{font-size:12pt;}");
        styleSheet.addRule(".calendarbox {border:1px solid #7FA9BF; color:#000000; width:40px;}");
        styleSheet.addRule(".calendarbox .calendarboxDayOfWeek{background-color:#7FA9BF; color:#FFFFFF; width:40px;font-size:10pt;}");
        styleSheet.addRule(".calendarboxDay{font-size:18pt;}");
        styleSheet.addRule(".calendarboxMonth{font-size:10pt;}");
        styleSheet.addRule(".taskbox{margin-bottom:5px;padding:2px;}");
        styleSheet.addRule(".taskbox a, .taskboxhighlight a{color:#000000;text-decoration:none;}");
        styleSheet.addRule(".taskboxhighlight{margin-bottom:5px;background-color:#FDFDBF;padding:2px;}");
        styleSheet.addRule(".taskDescription {font-size:15pt;}");
        styleSheet.addRule(".separatorfirst{font-size:1px;border-width:0px;}");
        styleSheet.addRule(".separator{font-size:1px;border:1px solid #DDDDDD; border-width:1px 0px 0px 0px;}");
     
        //Document doc = kit.createDefaultDocument();
		//return doc;
        return kit;
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
	
	public void update(LogicToUi returnValue){
		List<Task> refreshedList = sendCommandToLogic("refresh").getList();
		showTasksList(refreshedList, returnValue.getLastChangedSerial());		
		showStatus(returnValue.getString());
		GuiUpdate.update(this);
	}
	
	public void showTasksList(List<Task> taskList){
		showTasksList(taskList, -1);
	}
	
	public void showTasksList(List<Task> taskList, int highlightSerial){
		showTasksListInAgenda(taskList, highlightSerial);
		showTasksListInTable(taskList);
	}
	
	public void showTasksListInAgenda(List<Task> taskList, int highlightSerial){
		log.entering(this.getClass().getName(), "taskList");
		
		// This code moves the current caret position to the middle of the current
		// view, and stores it. After updating, we move the caret back to the same position
		// to keep the same view in place.
		int txtDatedTasksCaretPos = 0, txtUndatedTasksCaretPos = 0;
		try{
			txtDatedTasksCaretPos = moveCaretToMiddleOfScrollPane(txtDatedTasks);
			txtUndatedTasksCaretPos = moveCaretToMiddleOfScrollPane(txtUndatedTasks);
		}catch(IllegalArgumentException e){
			log.log(Level.WARNING, "Error with caret position before updating list", e);
		}
		
		// We check if there are tasks to show
		if (taskList.size() > 0){
		
			// Now we render the HTML code that we will display to the user,
			// and set the JEditorPane to the HTML
			DatedTaskListRenderer dtr = new DatedTaskListRenderer(taskList);
			if(highlightSerial >= 0){
				dtr.setHighlightSerial(highlightSerial);
			}
			String datedTaskListHtml = dtr.render();
			datesOfTasks = dtr.datesWithTasks.keySet();
			log.finest(datedTaskListHtml);
			txtDatedTasks.setText(datedTaskListHtml);
					
			UndatedTaskListRenderer udtr = new UndatedTaskListRenderer(taskList);
			if(highlightSerial >= 0){
				udtr.setHighlightSerial(highlightSerial);
			}
			String undatedTaskListHtml = udtr.render();
			log.finest(undatedTaskListHtml);
			txtUndatedTasks.setText(undatedTaskListHtml);
			
			log.finest("Dated task index list: " + dtr.getIndexList());
			log.finest("Undated task index list: " + udtr.getIndexList());
			
			// Checkbox handler
			
			// Note that we use HashMap here instead of other Map such as TreeMap
			// because ToggleButtonModel is not comparable
			checkboxToIndexMapForDated = new HashMap<ToggleButtonModel, Integer>();
			checkboxToIndexMapForUndated = new HashMap<ToggleButtonModel, Integer>();
			
			// Checkbox handler for dated tasks column
			setCheckBoxHandler(txtDatedTasks, checkboxToIndexMapForDated, dtr.getIndexList());
			setCheckBoxHandler(txtUndatedTasks, checkboxToIndexMapForUndated, udtr.getIndexList());
		
		}else{
			txtDatedTasks.setText("Ooops! There nothing to show here. Click the home button to see all your tasks.");
			txtUndatedTasks.setText("Nothing here");
		}		
		
		// After re-rendering the JEditorPane, we set the caret position so that the
		// JEditorPane does not scroll back to the start
		
		// We need to enclose the following in a try-catch block as an exception is thrown
		// if the caret position requested is beyond the length of the contents
		// This can happen if the new list is shorter than the current list.
		try {
			log.finer("Setting caret position for dated tasks to " + txtDatedTasksCaretPos);
			txtDatedTasks.setCaretPosition(txtDatedTasksCaretPos);
			log.finer("Setting caret position for undated tasks to " + txtUndatedTasksCaretPos);
			txtUndatedTasks.setCaretPosition(txtUndatedTasksCaretPos);
		} catch (IllegalArgumentException e1) {
			log.log(Level.WARNING, "Error with caret position after updating list", e1);
		}
		
		// In the event there is a highlighted item, we scroll to that
		txtDatedTasks.scrollToReference("highlight");
		txtUndatedTasks.scrollToReference("highlight");
		
		// Then we render the calendar
		CalendarRenderer calr = new CalendarRenderer(lastShownCalendarDate, taskList);
		txtCalendar.setText(calr.render());
		
		log.exiting(this.getClass().getName(), "showTasksList");
	}

	private void setCheckBoxHandler(JEditorPane txtTasks, Map<ToggleButtonModel, Integer> checkboxToIndexMap, List<Integer> indexList) {
		int i = 0;
			
		HTMLDocument doc = (HTMLDocument)txtTasks.getDocument();
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
                	checkboxToIndexMap.put(model, indexList.get(i++));
                	model.addActionListener(new CheckboxActionHandler(checkboxToIndexMap));
                	elementEnumeratorDebugString.append(model.isSelected());
                }
            }
		}
		log.finest("Element enumerator data: " + elementEnumeratorDebugString.toString());
	}

	private int moveCaretToMiddleOfScrollPane(JEditorPane editorPane) {
		int newCaretPos;
		JScrollPane scrollPane = (JScrollPane) editorPane.getParent().getParent();
		log.finer("Scroll bar pos:" + scrollPane.getVerticalScrollBar().getValue());
		int scrollBarPos = scrollPane.getVerticalScrollBar().getValue();
		int scrollPaneMiddle = scrollPane.getHeight() /2 ;
		log.fine("Caret position: " + editorPane.getCaretPosition());
		if (scrollBarPos >= 1){
			editorPane.setCaretPosition(editorPane.viewToModel(new Point(0,scrollBarPos + scrollPaneMiddle)));
			log.fine("New caret position: " + editorPane.getCaretPosition());
		}
		newCaretPos = editorPane.getCaretPosition();
		return newCaretPos;
	}

	@Override
	public void runUI() {
		log.entering(this.getClass().getName(), "runUI");
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//GuiMain2 window = new GuiMain2();
					GuiMain2 window = GuiMain2.getInstance();
					window.frmDoit.setVisible(true);
					
					if(prefs.getBoolean(GuiPreferences.DEFAULT_SHOW_TODAY, true)){
						jumpToTasksToday();
					}				
				} catch (Exception e) {
					//e.printStackTrace();
					log.log(Level.WARNING, "Error launching GuiMain2", e);
				}
			}
		});
		log.exiting(this.getClass().getName(), "runUI");
	}
	
	private class TasksHyperlinkHandler implements HyperlinkListener {
		public void hyperlinkUpdate(HyperlinkEvent e) {
			
			if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				if(e.getInputEvent() instanceof MouseEvent){
					if(((MouseEvent)e.getInputEvent()).getClickCount() < 2){
						return;
					}
				}
				log.finer("Hyperlink activated (dated tasks) " + e.getURL().getPath());
				if(e.getURL().getPath().startsWith("/editTask/")){
					String indexToEdit = e.getURL().getPath().split("/")[2];
					//System.out.println(indexToEdit);
					int index = Integer.parseInt(indexToEdit);
					table.setRowSelectionInterval(index-1, index-1);
					table.scrollRectToVisible(new Rectangle(table.getCellRect(index-1, 0, true)));
					switchCard(CARD_LIST);
				}
			}
		}
	}

	private class CheckboxActionHandler implements ActionListener {
		
		Map<ToggleButtonModel, Integer> checkboxToIndexMap;
		
		public CheckboxActionHandler(Map<ToggleButtonModel, Integer> c){
			checkboxToIndexMap = c;
		}
		
		public void actionPerformed(ActionEvent e){
			log.info(checkboxToIndexMap.get(e.getSource()).toString() + "\n" + ((ToggleButtonModel)e.getSource()).isSelected());
			boolean isDone = ((ToggleButtonModel)e.getSource()).isSelected();
			int index = checkboxToIndexMap.get(e.getSource());
			if(isDone){
				executeCommand("done " + index);
			}else{
				executeCommand("undone " + index);
			}
		}
	}

	private class CalendarHyperLinkHandler implements HyperlinkListener {
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
					lastShownCalendarDate = new DateTime(year, month, 1,0,0);
					CalendarRenderer calr = new CalendarRenderer(lastShownCalendarDate, sendCommandToLogic("list").getList());
					log.info("Rendering calendar for " + new DateTime(year, month, 1,0,0));
					txtCalendar.setText(calr.render());
				}else if(e.getURL().getPath().startsWith("/showTasksForDay/")){
					log.finer("Hyperlink go to date with tasks action (calendar)");
					String monthToShow = e.getURL().getPath().split("/")[2];
					int year = Integer.parseInt(monthToShow.split("-")[0]);
					int month = Integer.parseInt(monthToShow.split("-")[1]);
					int day = Integer.parseInt(monthToShow.split("-")[2]);
					
					jumpToTasksOnDate(new DateTime(year, month, day, 0, 0));
				}
			}
		}
	}
	
	private class TableChangedHandler implements TableModelListener {
		@Override
		public void tableChanged(TableModelEvent e) {
			log.entering(this.getClass().getName(), "tableChanged");
			log.info("Table has been modified");
			
			int row = e.getFirstRow();
			int column = e.getColumn();
			log.info(String.format("Cell at row %1d and col %2d has been modified", row, column));
			
			TableModel model = (TableModel) e.getSource();
			if (column == MyTableModel.COL_DONE) {
				log.info("Checkbox clicked");
				boolean done = (boolean) model.getValueAt(row, column);
				int index = row + 1;
				if (done) {
					//System.out.println("done " + index);
					executeCommand("done " + index);
				} else {
					//System.out.println("undone " + index);
					executeCommand("undone " + index);
				}
			}
			if (column == MyTableModel.COL_START) {
				log.info("Start date changed");
				
				String startTime = (String) model.getValueAt(row, column);
				startTime = startTime.trim();
				String endTime = (String) model.getValueAt(row, MyTableModel.COL_END);
				endTime = endTime.trim();
				int index = row + 1;
				
				log.finer(String.format("New start date %1s, new end date %2s", startTime, endTime));
				
				try{
					if(startTime.isEmpty() && endTime.isEmpty()){
						// Floating task
						log.finer("Both start and end time empty");
						executeCommand("update " + index +" -tofloating");
					}else if(startTime.isEmpty() || endTime.isEmpty()){
						log.finer("Either start or end time empty");
						// Deadline task
						String deadline;
						if(startTime.isEmpty()){
							log.finer("Start is empty, so we set deadline to endTime");
							deadline = endTime;
						}else{
							log.finer("Start is not empty implies endTime is empty, so we set deadline to startTime");
							deadline = startTime;
						}
						
						executeCommand("update " + index +" -deadline " + deadline.replace("-", " "));
					}else{
						// Timed task
						executeCommand("update " + index +" -start " + startTime.replace("-", " ") + " -end " + endTime.replace("-", " "));
					}
				}catch(Exception ex){
					ex.printStackTrace();
					log.log(Level.WARNING, "Exception encountered when editing task in table", ex);
				}
			}
			if (column == MyTableModel.COL_END) {
				log.info("End date changed");
				
				String endTime = (String) model.getValueAt(row, column);
				String startTime = (String) model.getValueAt(row, MyTableModel.COL_START);
				int index = row + 1;
				
				try{
					if(startTime.isEmpty() && endTime.isEmpty()){
						// Floating task
						executeCommand("update " + index +" -tofloating");
					}else if(startTime.isEmpty() || endTime.isEmpty()){
						// Deadline task
						String deadline;
						if(startTime.isEmpty()){
							deadline = endTime;
						}else{
							deadline = startTime;
						}
						
						executeCommand("update " + index +" -deadline " + deadline.replace("-", " "));
					} else {
						// Timed task
						executeCommand("update " + index + " -start " + startTime.replace("-", " ") + " -end " + endTime.replace("-", " "));
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			if (column == MyTableModel.COL_TASKNAME) {
				String newTaskName = (String) model.getValueAt(row, column);
				int index = row + 1;
				executeCommand("update " + index +" -name " + newTaskName);
			}
		}
	}

	class MyTableModel extends AbstractTableModel {
		public static final long serialVersionUID = 8328597110205703514L;
		public static final int COL_INDEX = 0;
		public static final int COL_DONE = 1;
		public static final int COL_START = 2;
		public static final int COL_END = 3;
		public static final int COL_TASKNAME = 4;

		private String[] columnNames;
		private List<Object[]> data;

		public MyTableModel(List<Object[]> tableItems){
			log.entering(this.getClass().getName(), "<init>");
			columnNames = new String[]{"Idx", "", "Start/Deadline", "End", "Task"};
			data = tableItems;
			log.exiting(this.getClass().getName(), "<init>");
		}

		public int getColumnCount() {
			log.fine("column count is " + columnNames.length);
			return columnNames.length;
		}

		public int getRowCount() {
			//return data.length;
			log.fine("row count is " + data.size());
			return data.size();
		}

		public String getColumnName(int col) {
			log.fine(String.format("name of column %1d is %2s", col, columnNames[col]));
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			switch(col){
			case COL_INDEX:
				int index = row+1;
				log.finest(String.format("value at row %1d col %2d is %3d", row, col, index));
	    		return index;
			default:
				if(data.get(row)[col] != null){
					//log.finest(String.format("value at row %1d col %2d is %3d", row, col, data.get(row)[col].toString()));
				}else{
					log.finest(String.format("value at row %1d col %2d is %3d", row, col, "null"));
				}
				return data.get(row)[col];
			}
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Class getColumnClass(int c) {
			log.fine(String.format("class of column %1d is %2s", c, getValueAt(0, c).getClass()));
			return getValueAt(0, c).getClass();
		}

		public boolean isCellEditable(int row, int col) {
			//Note that the data/cell address is constant,
			//no matter where the cell appears on-screen.
			if (col == COL_INDEX) {
				log.fine(String.format("row %1d col %2d is editable: %3b", row, col, false));
				return false;
			} else {
				log.fine(String.format("row %1d col %2d is editable: %3b", row, col, true));
				return true;
			}
		}

		/*
		 * Don't need to implement this method unless your table's
		 * data can change.
		 */
		public void setValueAt(Object value, int row, int col) {
			log.entering(this.getClass().getName(), "setValueAt");
			log.finest(String.format("Setting row %1d col %2d to %3s", row, col, value));
			
			Object[] tableRow = data.get(row);

			switch (col) {
			case COL_INDEX:
				log.warning("Try to set value of index!");
				assert false;
				break;
			default:
				tableRow[col] = value;
			}

			fireTableCellUpdated(row, col);
			
			log.exiting(this.getClass().getName(), "setValueAt");
		}
	}
	
	public int getContentWidth(String content) {
		log.entering(this.getClass().getName(), "getContentWidth");
		
	    JLabel dummylabel = new JLabel();
	    //dummyEditorPane.setSize(1, Short.MAX_VALUE);
	    dummylabel.setText(content);
	    int width = dummylabel.getPreferredSize().width;
	    
	    log.finest(String.format("Width of string %1s is %2d", content, width));
	    
	    log.exiting(this.getClass().getName(), "getContentWidth");
		return width;
	}

	public void showTasksListInTable(List<Task> taskListOrig){
		List<Object[]> tableItems = new ArrayList<Object[]>();
		int index = 1;
		for(Task t : taskListOrig){
			Object[] tableRowItems = new Object[5];
			tableRowItems[MyTableModel.COL_INDEX] = new Integer(index++);
								
			String start = "", end = "";
			if(t.getType().equals(TaskType.TIMED)) {
				start = dateTimeToLongerString(t.getStartDate());
			} else if(t.getType().equals(TaskType.DEADLINE)) {
				start = dateTimeToLongerString(t.getDeadline());
			} else {
				start = TABLE_EMPTY_DATE_FIELD;
			}

			if(t.getType().equals(TaskType.TIMED)) {
				end = dateTimeToLongerString(t.getEndDate());
			} else {
				end = TABLE_EMPTY_DATE_FIELD;
			}

			tableRowItems[MyTableModel.COL_DONE] = new Boolean(t.isDone());
			tableRowItems[MyTableModel.COL_START] = start;
			tableRowItems[MyTableModel.COL_END] = end;
			tableRowItems[MyTableModel.COL_TASKNAME] = t.getTaskName();

			tableItems.add(tableRowItems);
			
			//System.out.println(Arrays.asList(tableRowItems));
		}
		
		
		log.entering(this.getClass().getName(), "showTasksList");
		
		int indexNumberColumnWidth = getContentWidth(TABLE_COLUMN_WIDTH_INDEX_MAX_TEXT) + TABLE_COLUMN_WIDTH_EXTRA;
		int checkboxColumnWidth = TABLE_COLUMN_WIDTH_CHECKBOX;
		int dateColumnWidth = getContentWidth(TABLE_COLUMN_WIDTH_DATE_MAX_TEXT) + TABLE_COLUMN_WIDTH_EXTRA;

		table.setModel(new MyTableModel(tableItems));
		table.getColumnModel().getColumn(0).setMinWidth(indexNumberColumnWidth);
		table.getColumnModel().getColumn(0).setMaxWidth(indexNumberColumnWidth);
		table.getColumnModel().getColumn(1).setMinWidth(checkboxColumnWidth);
		table.getColumnModel().getColumn(1).setMaxWidth(checkboxColumnWidth);
		// Give more space to date: Test with 27 May 2009 10am to 27 May 2009 10pm
		table.getColumnModel().getColumn(2).setMinWidth(dateColumnWidth);
		table.getColumnModel().getColumn(2).setMaxWidth(dateColumnWidth);
		table.getColumnModel().getColumn(3).setMinWidth(dateColumnWidth);
		table.getColumnModel().getColumn(3).setMaxWidth(dateColumnWidth);
		//table.getColumnModel().getColumn(4).setPreferredWidth(160);
		
		table.getModel().addTableModelListener(new TableChangedHandler());
		
		log.exiting(this.getClass().getName(), "showTasksList");
	}
	
	public void switchCard(String cardName){
		CardLayout cl = (CardLayout)(panelCards.getLayout());
		switch(cardName){
			case CARD_AGENDA:				
				cl.show(panelCards, CARD_AGENDA);
				tglbtnAgendaView.setSelected(true);
				tglbtnListView.setSelected(false);
				break;
			case CARD_LIST:
				cl.show(panelCards, CARD_LIST);
				tglbtnAgendaView.setSelected(false);
				tglbtnListView.setSelected(true);
				break; 
		}
	}

	public void runUI(String cardName) {
		runUI();
		switchCard(cardName);
	}
	
	private void jumpToTasksToday(){
		// assert datesOfTasks is sorted
		
		DateTime today = new DateTime().withTimeAtStartOfDay();
		for(DateTime date : datesOfTasks){
			if(date.equals(today) || date.isAfter(today)){
				jumpToTasksOnDate(date);
				return;
			}
		}
	}

	private void jumpToTasksOnDate(DateTime date) {
		int year = date.getYear();
		int month = date.getMonthOfYear();
		int day = date.getDayOfMonth();
		
		String dateReferenceStr = "date-"+year+"-"+month+"-"+day;
		log.info("Scroll to reference " + dateReferenceStr);
		txtDatedTasks.scrollToReference(dateReferenceStr);
		
		TableModel tableItems = table.getModel();
		for (int i = 0; i < tableItems.getRowCount(); i++) {
			if (((String) tableItems.getValueAt(i, MyTableModel.COL_START)).startsWith(dateTimeToLongerStringDateOnly(date))){
				table.setRowSelectionInterval(i, i);
				table.scrollRectToVisible(new Rectangle(table.getCellRect(i, 0, true)));
				break;
			}
		}
	}
	
	private String dateTimeToLongerStringDateOnly(DateTime toBeConverted) {
		return DateTimeFormat.forPattern("EEE dd-MMM-yyyy").print(toBeConverted);
	}
}
