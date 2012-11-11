package main.ui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.joda.time.DateTime;

import com.joestelmach.natty.DateGroup;

import main.LogHandler;
import main.shared.LogicToUi;
import main.shared.Task;
import main.shared.Task.TaskType;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GuiMain extends GuiCommandBox{
	
	private static final int TABLE_COLUMN_WIDTH_CHECKBOX = 20;
	private static final String TABLE_COLUMN_WIDTH_DATE_MAX_TEXT = "WMW 00 MWM 0000 23:59pm";
	private static final String TABLE_COLUMN_WIDTH_INDEX_MAX_TEXT = "9999";
	private static final int TABLE_COLUMN_WIDTH_EXTRA = 10;

	Logger log = LogHandler.getLogInstance();

	private static final String TABLE_EMPTY_DATE_FIELD = "";
	private JFrame frmDoit;
	private JTextField txtCmd;
	public JScrollPane scrollPane;
	private JTable table;
	private JPopupMenu popupCmdHint;
	private JMenu mnSomeMenu;
	private JEditorPane txtCmdHint;
	private JEditorPane txtStatus;
	private JPanel panel;
	
	
	private static GuiMain theOne = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		new GuiMain().runUI();
	}
	
	public static GuiMain getInstance() {
		LogHandler.getLogInstance().info("Getting instance of this class");
		if (theOne == null) {
			LogHandler.getLogInstance().info("Previous instance did not exist, instantiating this class");
			theOne = new GuiMain();
		}
		return theOne;
	}
	
	/**
	 * Create the application.
	 */
	private GuiMain() {
		log.entering(this.getClass().getName(), "constructor");
		
		setUiLookAndFeel();
		
		initialize();
		log.exiting(this.getClass().getName(), "constructor");
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings("serial")
	protected void initialize() {
		
		log.entering(this.getClass().getName(), "initialize");
		
		log.info("Setting up stuff in GuiMain");
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

		txtCmd = new JTextField();
		panel.add(txtCmd);
		
		popupCmdHint = new JPopupMenu();
		addPopup(txtCmd, popupCmdHint);

		txtCmdHint = new JEditorPane();
		popupCmdHint.add(txtCmdHint);

		txtStatus = new JEditorPane();
		panel.add(txtStatus, BorderLayout.SOUTH);
		
		log.info("Calling GuiCommandBox to configure widgets");
		configureWidgets(txtCmd, txtStatus, txtCmdHint, popupCmdHint);

		scrollPane = new JScrollPane();
		frmDoit.getContentPane().add(scrollPane, BorderLayout.CENTER);

		table = new JTable();
		table.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				log.finest("Key pressed: " + arg0.getKeyCode());
				if(arg0.getKeyCode() == java.awt.event.KeyEvent.VK_DELETE){
					log.finer("Delete key pressed");
					// Note: With every deletion, the index number changes!
					// Therefore we must delete from the largest number backwards.
					log.info("Deleting selected tasks");
					int[] rowsToDelete = table.getSelectedRows();
					Arrays.sort(rowsToDelete);
					//System.out.println(Arrays.toString(rowsToDelete));
					log.info("Rows to delete: "+ Arrays.toString(rowsToDelete));
					for(int i = (rowsToDelete.length-1); i>=0; i--){
						executeCommand("delete " + (rowsToDelete[i] + 1));
					}
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

		scrollPane.setViewportView(table);
		
		log.info("Checking file status");
		String fileStatus = checkFilePermissions();		
		//executeCommand("list");
		// Note we do not use executeCommand here, as doing so will cause a further update
		// request to be propagated to all windows.
		showTasksList(sendCommandToLogic("refresh").getList());
		showStatus(fileStatus);
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
		log.entering(this.getClass().getName(), "runUI");
		frmDoit.setVisible(true);
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
						executeCommand("update " + index +" -begintime " + startTime.replace("-", " ") + " -endtime " + endTime.replace("-", " "));
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
			log.entering(this.getClass().getName(), "<init>");
			columnNames = new String[]{"Idx", "", "Start/Deadline", "End", "Task"};
			data = taskList;
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
				int index = row+1;
				log.finest(String.format("value at row %1d col %2d is %3d", row, col, index));
	    		return index;
			case COL_DONE:
				boolean isTaskDone = task.isDone();
				log.finest(String.format("value at row %1d col %2d is %3b", row, col, isTaskDone));
				return isTaskDone;
			case COL_START:
				log.finest(String.format("value at row %1d col %2d is %3s", row, col, start));
				return start;
			case COL_END:
				log.finest(String.format("value at row %1d col %2d is %3s", row, col, end));
				return end;
			case COL_TASKNAME:
				String taskName = task.getTaskName();
				log.finest(String.format("value at row %1d col %2d is %3s", row, col, taskName));
				return taskName;
			default:
				log.warning("Invalid column number");
				assert false;
			}
			return null;
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
			log.info(String.format("Setting row %1d col %2d to %3s", row, col, value));
			
			Task task = data.get(row);
			

			List<DateGroup> groups = null;
			DateTime date;
			
			try{

				switch (col) {
				case COL_INDEX:
					log.warning("Try to set value of index!");
					assert false;
					break;
				case COL_DONE:
					log.info("Setting value of done to " + value);
					task.done((boolean) value);
					break;
				case COL_START:
					log.fine("Setting value of start date with raw value " + value);
					groups = nattyParser.parseWDefBaseDate(((String) value).replace("-", " "));
					date = null;
	
					if (groups != null && !groups.isEmpty()) {
						date = new DateTime(groups.get(0).getDates().get(0));
					}
	
					if (date != null) {
						log.fine("Managed to parse a date");
						// Managed to parse out a date
						if (task.getType() == TaskType.TIMED) {
							log.info(String.format("Task is timed task, changing times to %1s %2s", date, task.getEndDate()));
							task.changeStartAndEndDate(date, task.getEndDate());
						} else if (task.getType() == TaskType.DEADLINE) {
							log.info(String.format("Task is deadline task, changing times %1s", date));
							task.changeDeadline(date);
						} else if (task.getType() == TaskType.FLOATING) {
							log.info(String.format("Task is floating task, changing to deadline tas with time %1s", date));
							task.changetoDeadline(date);
						}
	
					} else {
						log.fine("Did not manage to parse a date");
						// Did not manage to parse out a date
						if (task.getType() == TaskType.TIMED) {
							log.info(String.format("Task is timed task, changing to deadline task with time %1s", task.getEndDate()));
							task.changetoDeadline(task.getEndDate());
						} else if (task.getType() == TaskType.DEADLINE) {
							log.info("Task is deadline task, changing to floating task");
							task.changetoFloating();
						}
					}
					break;
				case COL_END:
					log.fine("Setting value of end date with raw value " + value);
					groups = nattyParser.parseWDefBaseDate(((String) value).replace("-", " "));
					date = null;
	
					if (groups != null && !groups.isEmpty()) {
						date = new DateTime(groups.get(0).getDates().get(0));
					}
	
					if (date != null) {
						log.fine("Managed to parse a date");
						
						// Managed to parse out a date
						if (task.getType() == TaskType.TIMED) {
							log.info(String.format("Task is timed task, changing times to %1s %2s", task.getStartDate(), date));
							task.changeStartAndEndDate(task.getStartDate(), date);
						} else if (task.getType() == TaskType.DEADLINE) {
							log.info(String.format("Task is deadline task, changing to timed task %1s %2s", task.getDeadline(), date));
							task.changetoTimed(task.getDeadline(), date);
						} else if (task.getType() == TaskType.FLOATING) {
							log.info(String.format("Task is floating task, changing to deadline tas with time %1s", date));
							task.changetoDeadline(date);
						}
					} else {					
						log.fine("Did not manage to parse a date");
						
						// Did not manage to parse out a date
						if (task.getType() == TaskType.TIMED) {
							log.info(String.format("Task is timed task, changing to deadline task with time %1s", task.getStartDate()));
							task.changetoDeadline(task.getStartDate());
						}
					}
	
					break;
				case COL_TASKNAME:
					log.info("Changing task name to " + value);
					task.changeName((String) value);
					break;
				default:
					assert false;
				}
			
			}catch(Exception e){
				e.printStackTrace();
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

	public void showTasksList(List<Task> taskListOrig){
		List<Task> taskList = new ArrayList<Task>();
		for(Task t : taskListOrig){
			Task newTask = new Task(t);
			taskList.add(newTask);
		}
		
		log.entering(this.getClass().getName(), "showTasksList");
		
		int indexNumberColumnWidth = getContentWidth(TABLE_COLUMN_WIDTH_INDEX_MAX_TEXT) + TABLE_COLUMN_WIDTH_EXTRA;
		int checkboxColumnWidth = TABLE_COLUMN_WIDTH_CHECKBOX;
		int dateColumnWidth = getContentWidth(TABLE_COLUMN_WIDTH_DATE_MAX_TEXT) + TABLE_COLUMN_WIDTH_EXTRA;

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
		
		table.getModel().addTableModelListener(new TableChangedHandler());
		
		log.exiting(this.getClass().getName(), "showTasksList");
	}

	public void update(LogicToUi returnValue){
		// Call command to refresh the table
		showStatus(returnValue.getString());
		showTasksList(sendCommandToLogic("refresh").getList());		
		
		// Update other windows
		GuiUpdate.update(this);
	}

	public void updateWindow(Object source) {
		if(source != this){
			LogicToUi result = sendCommandToLogic("refresh");
			showTasksList(result.getList());
			showStatus(result.getString());
		}
		//executeCommand("refresh");
		
	}

}
