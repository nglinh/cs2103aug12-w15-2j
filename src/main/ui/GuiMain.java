package main.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import org.joda.time.DateTime;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.ParseLocation;
import com.joestelmach.natty.Parser;

import main.shared.LogicToUi;
import main.shared.Task;
import main.shared.Task.TaskType;

public class GuiMain extends UI{

	private static final String TABLE_EMPTY_DATE_FIELD = "";
	private JFrame frmDoit;
	private JTextField textCmd;
	private JScrollPane scrollPane;
	private JTable table;
	private JPopupMenu popupCmdHint;
	private JPopupMenu popupStatus;
	private JMenu mnSomeMenu;
	private JEditorPane txtCmdHint;
	private JEditorPane txtStatus;
	private JPanel panel;
	
	private List<String> commandHistory;
	private ListIterator<String> commandHistoryIterator;
	
	private static GuiMain theOne = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		new GuiMain().runUI();
	}
	
	public static GuiMain getInstance() {
		if (theOne == null) {
			theOne = new GuiMain();
		}
		return theOne;
	}
	
	/**
	 * Create the application.
	 */
	private GuiMain() {
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
	@SuppressWarnings("serial")
	private void initialize() {
		frmDoit = new JFrame();
		frmDoit.setTitle("DoIt!");
		frmDoit.setBounds(100, 100, 700, 400);

		JMenuBar menuBar = new JMenuBar();
		frmDoit.setJMenuBar(menuBar);

		mnSomeMenu = new JMenu("Some Menu");
		mnSomeMenu.setMnemonic('s');
		menuBar.add(mnSomeMenu);
		
		panel = new JPanel();
		frmDoit.getContentPane().add(panel, BorderLayout.SOUTH);
				panel.setLayout(new BorderLayout(0, 0));
		
		
		
				textCmd = new JTextField();
				panel.add(textCmd);
				
				commandHistory = new LinkedList<String>();
				commandHistoryIterator = commandHistory.listIterator();
				
				textCmd.addKeyListener(new KeyAdapter() {
					@Override
					public void keyReleased(KeyEvent arg0) {
						if (arg0.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
							commandHistory.add(textCmd.getText());
							commandHistoryIterator = commandHistory.listIterator(commandHistory.size());
							
							executeCommand(textCmd.getText());
							popupCmdHint.setVisible(false);
						} else if (arg0.getKeyCode() == java.awt.event.KeyEvent.VK_UP) {
							if (commandHistoryIterator.hasPrevious()) {
								textCmd.setText(commandHistoryIterator.previous());
								showHint();
							}
						} else if (arg0.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN) {
							if (commandHistoryIterator.hasNext()) {
								textCmd.setText(commandHistoryIterator.next());
								showHint();
							}
						} else {
							showHint();
						}
					}

					private void showHint() {
						List<String> commands = Hint.getInstance().getCommands();
						boolean isCommand = false;
						for (String command : commands){
							if (textCmd.getText().startsWith(command)) {
								System.out.println(Hint.getInstance().helpForThisCommandHTML(command));
								txtCmdHint.setText("<html>"+Hint.getInstance().helpForThisCommandHTML(command)+"</html>");
								//popupCmdHint.setPopupSize(500, 300);
								popupCmdHint.setPopupSize(500, (int) txtCmdHint.getPreferredSize().getHeight() + 20);
								popupCmdHint.show(textCmd, 5, textCmd.getHeight());
								if(popupCmdHint.getLocationOnScreen().getY() < textCmd.getLocationOnScreen().getY()){
									popupCmdHint.show(textCmd, 5, -1 * popupCmdHint.getHeight());
								}
								textCmd.requestFocus();
								isCommand = true;
							} 
						}
						if(isCommand == false){
							popupCmdHint.setVisible(false);
						}
					}
				});
		textCmd.setColumns(10);

		popupCmdHint = new JPopupMenu();
		addPopup(textCmd, popupCmdHint);

		txtCmdHint = new JEditorPane();
		txtCmdHint.setEditable(false);
		txtCmdHint.setContentType("text/html");
		txtCmdHint.setBackground(new Color(0, 0, 0, 0));
		txtCmdHint.setOpaque(false);
		
		HTMLEditorKit kit = new HTMLEditorKit();
		txtCmdHint.setEditorKit(kit);

        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule("body, p {font-family:Segoe UI;}");
        styleSheet.addRule("h1 {font-family:Segoe UI; margin:0px 0px 0px 0px; padding:0px 0px 0px 0px;}");
        styleSheet.addRule("h2 {font-family:Segoe UI; margin:10px 0px 0px 0px; padding:0px 0px 0px 0px;}");
        styleSheet.addRule("p {margin-top:5px;}");
        
        Document doc = kit.createDefaultDocument();
        txtCmdHint.setDocument(doc);
		
		popupCmdHint.add(txtCmdHint);

		txtStatus = new JEditorPane();
		txtStatus.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent event) {
				if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					if (event.getURL().toString().endsWith("/undo")) {
						executeCommand("undo");
					}
				}
			}
		});
		txtStatus.setBackground(new Color(0, 0, 0, 0));
		txtStatus.setOpaque(false);
		panel.add(txtStatus, BorderLayout.SOUTH);
		txtStatus.setEditable(false);
		txtStatus.setContentType("text/html");

		scrollPane = new JScrollPane();
		frmDoit.getContentPane().add(scrollPane, BorderLayout.CENTER);

		table = new JTable();
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

		popupStatus = new JPopupMenu();
		addPopup(scrollPane, popupStatus);
		scrollPane.setViewportView(table);
		
		String fileStatus = checkFilePermissions();		
		executeCommand("list");
		showStatus(fileStatus);
	}

	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

	@Override
	public void runUI() {
		/*EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GuiMain window = new GuiMain();
					window.frmDoit.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});*/
		frmDoit.setVisible(true);
	}

	class MyTableModel extends AbstractTableModel {
		public static final long serialVersionUID = 8328597110205703514L;
		public static final int COL_INDEX = 0;
		public static final int COL_DONE = 1;
		public static final int COL_START = 2;
		public static final int COL_END = 3;
		public static final int COL_TASKNAME = 4;

		private String[] columnNames;
		private List<Task> data;

		public MyTableModel(List<Task> taskList){
			columnNames = new String[]{"Idx", "", "Start/Deadline", "End", "Task"};
			data = taskList;
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			//return data.length;
			return data.size();
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			Task task = data.get(row);

			String start, end;
			if(task.getType().equals(TaskType.TIMED)) {
				start = dateTimeToLongerString(task.getStartDate());
			} else if(task.getType().equals(TaskType.DEADLINE)) {
				start = dateTimeToLongerString(task.getDeadline());
			} else {
				start = TABLE_EMPTY_DATE_FIELD;
			}

			if(task.getType().equals(TaskType.TIMED)) {
				end = dateTimeToLongerString(task.getEndDate());
			} else {
				end = TABLE_EMPTY_DATE_FIELD;
			}

			switch(col){
			case COL_INDEX:
	    		return row+1;
			case COL_DONE:
				return task.isDone();
			case COL_START:
				return start;
			case COL_END:
				return end;
			case COL_TASKNAME:
				return task.getTaskName();
			default:
				assert false;
			}
			return null;
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		public boolean isCellEditable(int row, int col) {
			//Note that the data/cell address is constant,
			//no matter where the cell appears on-screen.
			if (col == COL_INDEX) {
				return false;
			} else {
				return true;
			}
		}

		/*
		 * Don't need to implement this method unless your table's
		 * data can change.
		 */
		public void setValueAt(Object value, int row, int col) {

			Task task = data.get(row);
			
			Parser parser = null;
			List<DateGroup> groups = null;
			DateTime date;
			
			try{

			switch (col) {
			case COL_INDEX:
				assert false;
				break;
			case COL_DONE:
				task.done((boolean) value);
				break;
			case COL_START:
				parser = new Parser();
				groups = parser.parse(((String) value).replace("-", " "));
				date = null;

				if (groups != null && !groups.isEmpty()) {
					date = new DateTime(groups.get(0).getDates().get(0));
				}

				if (date != null) {
					// Managed to parse out a date
					if (task.getType() == TaskType.TIMED) {
						task.changeStartAndEndDate(date, task.getEndDate());
					} else if (task.getType() == TaskType.DEADLINE) {
						task.changeDeadline(date);
					} else if (task.getType() == TaskType.FLOATING) {
						task.changetoDeadline(date);
					}

				} else {
					// Did not manage to parse out a date
					if (task.getType() == TaskType.TIMED) {
						task.changetoDeadline(task.getEndDate());
					} else if (task.getType() == TaskType.DEADLINE) {
						task.changetoFloating();
					}
				}
				break;
			case COL_END:
				parser = new Parser();
				groups = parser.parse(((String) value).replace("-", " "));
				date = null;

				if (groups != null && !groups.isEmpty()) {
					date = new DateTime(groups.get(0).getDates().get(0));
				}

				if (date != null) {
					// Managed to parse out a date
					if (task.getType() == TaskType.TIMED) {
						task.changeStartAndEndDate(task.getStartDate(), date);
					} else if (task.getType() == TaskType.DEADLINE) {
						task.changetoTimed(task.getDeadline(), date);
					} else if (task.getType() == TaskType.FLOATING) {
						task.changetoDeadline(date);
					}
				} else {
					// Did not manage to parse out a date
					if (task.getType() == TaskType.TIMED) {
						task.changetoDeadline(task.getStartDate());
					}
				}

				break;
			case COL_TASKNAME:
				task.changeName((String) value);
				break;
			default:
				assert false;
			}
			
			}catch(Exception e){
				e.printStackTrace();
			}

			fireTableCellUpdated(row, col);
		}
	}
	
	public int getContentWidth(String content) {
	    JLabel dummylabel = new JLabel();
	    //dummyEditorPane.setSize(1, Short.MAX_VALUE);
	    dummylabel.setText(content);
	    return dummylabel.getPreferredSize().width;
	}

	public void showTasksList(List<Task> taskList){
		
		int indexNumberColumnWidth = getContentWidth("9999") + 10;
		int checkboxColumnWidth = 20;
		int dateColumnWidth = getContentWidth("WMW 00 MWM 0000 23:59pm") + 10;

		table.setModel(new MyTableModel(taskList));
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
		
		table.getModel().addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {
				int row = e.getFirstRow();
				int column = e.getColumn();
				TableModel model = (TableModel) e.getSource();
				if (column == MyTableModel.COL_DONE) {
					boolean done = (boolean) model.getValueAt(row, column);
					int index = row + 1;
					if (done) {
						executeCommand("done " + index);
					} else {
						executeCommand("undone " + index);
					}
				}
				if (column == MyTableModel.COL_START) {
					String startTime = (String) model.getValueAt(row, column);
					startTime = startTime.trim();
					String endTime = (String) model.getValueAt(row, MyTableModel.COL_END);
					endTime = endTime.trim();
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
						}else{
							// Timed task
							executeCommand("update " + index +" -begintime " + startTime.replace("-", " ") + " -endtime " + endTime.replace("-", " "));
						}
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
				if (column == MyTableModel.COL_END) {
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
							executeCommand("update " + index + " -begintime " + startTime.replace("-", " ") + " -endtime " + endTime.replace("-", " "));
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

		});
	}

	public void executeCommand(String text) {
		
		System.out.println(text);

		// Call command parser
		LogicToUi returnValue = sendCommandToLogic(text);

		// Set command text box to empty
		textCmd.setText("");



		//int popupWidth = 300;
		//int popupHeight = 60;

		showStatus(returnValue.getString());

		//popupStatus.setPopupSize(popupWidth, popupHeight);
		//popupStatus.show(frmDoit, (frmDoit.getWidth() - popupWidth) / 2,
		//		frmDoit.getHeight() - popupHeight + 5);

		// Call command to refresh the table
			showTasksList(sendCommandToLogic("refresh").getList());


		if (returnValue.containsList()) {
			showTasksList(returnValue.getList());
		}

	}

	private void showStatus(String status) {
		txtStatus
				.setText("<html><table align=\"center\"><tr><td valign=\"middle\" align=\"center\"><font size=\"4\">"
						+ status
						+ " &nbsp;&nbsp;&nbsp;<a href=\"http://doit/undo\">undo</a></font></td></tr></table></html>");
	}

	public void update() {
		executeCommand("refresh");
		
	}

}
