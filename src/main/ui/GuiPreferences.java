package main.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JDialog;
import javax.swing.JRadioButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;

import main.LogHandler;

import java.awt.Dialog.ModalExclusionType;
import java.awt.Window.Type;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.awt.Dialog.ModalityType;
import javax.swing.SwingConstants;

//@author A0086826R

public class GuiPreferences extends UI {
	
	private static final String ERR_INVALID_SHORTCUT_CHAR = "The character you have selected for the shortcut is invalid. Please re-enter.";
	
	// Preferences Configuration Constants
	static final String  DEFAULT_VIEW = "DoIt! Default View";
	static final String  DEFAULT_VIEW_DEAFULT = GuiMain.CARD_AGENDA;
	
	static final String  DEFAULT_SHOW_TODAY = "DoIt! Show today's tasks on start";
	static final boolean DEFAULT_SHOW_TODAY_DEFAULT = true;
	
	static final String  HOME_GOES_TO_DEFAULT_VIEW = "DoIt! Home goes to Default View";
	static final boolean HOME_GOES_TO_DEFAULT_VIEW_DEFAULT = true;
	
	static final String  HOME_GOES_TO_TODAY = "DoIt! Home goes to today's tasks";
	static final boolean HOME_GOES_TO_TODAY_DEFAULT = true;
	
	static final String  NUM_CLICKS_EDIT = "DoIt! Number of clicks on a task in agenda view to edit the task";
	static final int     NUM_CLICKS_EDIT_DEFAULT = 2;
	
	static final String  SHOW_HINTS = "DoIt! Show hints";
	static final boolean SHOW_HINTS_DEFAULT = true;
	
	static final String  SHORTCUT_KEY_CTRL = "DoIt! ShortcutKey Ctrl";
	static final boolean SHORTCUT_KEY_CTRL_DEFAULT = false;
	static final String  SHORTCUT_KEY_ALT = "DoIt! ShortcutKey Alt";
	static final boolean SHORTCUT_KEY_ALT_DEFAULT = false;
	static final String  SHORTCUT_KEY_SHIFT = "DoIt! ShortcutKey Shift";
	static final boolean SHORTCUT_KEY_SHIFT_DEFAULT = false;
	static final String  SHORTCUT_KEY_WIN = "DoIt! ShortcutKey Win";
	static final boolean SHORTCUT_KEY_WIN_DEFAULT = true;
	static final String  SHORTCUT_KEY_EXTRA = "DoIt! ShortcutKey Extra";
	static final String  SHORTCUT_KEY_EXTRA_DEFAULT = "A";

	private JDialog frmDoitPreferences;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JTextField txtShortcut;
	private JButton btnSave;
	private JRadioButton rdbtnAgenda;
	private JRadioButton rdbtnList;
	private JCheckBox chckbxCommandHints;
	private JCheckBox chckbxWin;
	private JCheckBox chckbxShift;
	private JCheckBox chckbxAlt;
	private JCheckBox chckbxCtrl;

	Logger log = LogHandler.getLogInstance();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		GuiPreferences window = new GuiPreferences();
		window.runUI();
	}

	/**
	 * Create the application.
	 */
	private GuiPreferences() {
		setUiLookAndFeel();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmDoitPreferences = new JDialog();
		frmDoitPreferences.setModalityType(ModalityType.APPLICATION_MODAL);
		frmDoitPreferences.setResizable(false);
		frmDoitPreferences.setType(Type.POPUP);
		frmDoitPreferences
				.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		frmDoitPreferences.setTitle("DoIt! Preferences");
		frmDoitPreferences.setBounds(100, 100, 380, 300);
		frmDoitPreferences.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JLabel lblDefaultViewOn = new JLabel("Default View:");
		lblDefaultViewOn.setHorizontalAlignment(SwingConstants.RIGHT);

		rdbtnAgenda = new JRadioButton("Agenda");
		buttonGroup.add(rdbtnAgenda);

		rdbtnList = new JRadioButton("List");
		buttonGroup.add(rdbtnList);

		chckbxShowTodaysTasks = new JCheckBox("Show today's tasks on start");

		JLabel lblHome = new JLabel("Home Button:");
		lblHome.setHorizontalAlignment(SwingConstants.RIGHT);
		chckbxHomeDefault = new JCheckBox("Jumps to Default View (Agenda/List)");
		chckbxHomeToday = new JCheckBox("Scrolls to today's tasks");

		JLabel lblShow = new JLabel("Show:");
		lblShow.setHorizontalAlignment(SwingConstants.RIGHT);
		chckbxCommandHints = new JCheckBox("Command hints");

		JLabel lblQuickAddShortcut = new JLabel("Quick Add Shortcut:");
		lblQuickAddShortcut.setHorizontalAlignment(SwingConstants.RIGHT);
		chckbxCtrl = new JCheckBox("Ctrl");
		chckbxAlt = new JCheckBox("Alt");
		chckbxShift = new JCheckBox("Shift");
		chckbxWin = new JCheckBox("Win");
		txtShortcut = new JTextField();
		txtShortcut.setText(SHORTCUT_KEY_EXTRA_DEFAULT);
		txtShortcut.setColumns(2);
		
		btnSave = new JButton("Save Preferences");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				savePreferences();				
				frmDoitPreferences.dispose();
			}
		});
		
		JButton btnReset = new JButton("Reset");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Preferences prefs = Preferences.userNodeForPackage(this.getClass());
				try {
					prefs.clear();
				} catch (BackingStoreException e1) {
					log.log(Level.WARNING,
							"Exception when clearing preferences", e1);
				}
				loadPreferences();
			}
		});
		
		JLabel lblInAgendaView = new JLabel("In Agenda View,");
		lblInAgendaView.setHorizontalAlignment(SwingConstants.RIGHT);

		rdbtnSingleClick = new JRadioButton("Single click");
		buttonGroup_1.add(rdbtnSingleClick);

		rdbtnDoubleClick = new JRadioButton("Double click");
		buttonGroup_1.add(rdbtnDoubleClick);
		
		JLabel lblToGoTo = new JLabel("on a task to go to list view to edit it");
		
		GroupLayout groupLayout = new GroupLayout(frmDoitPreferences.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(15)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(lblDefaultViewOn)
						.addComponent(lblShow)
						.addComponent(lblHome)
						.addComponent(lblInAgendaView, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(rdbtnSingleClick, GroupLayout.PREFERRED_SIZE, 91, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(rdbtnDoubleClick, GroupLayout.PREFERRED_SIZE, 98, GroupLayout.PREFERRED_SIZE))
						.addComponent(chckbxShowTodaysTasks)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(rdbtnAgenda)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(rdbtnList))
						.addComponent(chckbxHomeToday)
						.addComponent(chckbxCommandHints)
						.addComponent(chckbxHomeDefault)
						.addComponent(lblToGoTo))
					.addContainerGap(34, Short.MAX_VALUE))
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap(155, Short.MAX_VALUE)
					.addComponent(btnReset)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnSave)
					.addContainerGap())
				.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblQuickAddShortcut)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(chckbxCtrl)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxAlt)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxShift)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxWin)
					.addGap(6)
					.addComponent(txtShortcut, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblDefaultViewOn)
						.addComponent(rdbtnAgenda)
						.addComponent(rdbtnList))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxShowTodaysTasks)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblHome)
						.addComponent(chckbxHomeDefault))
					.addGap(8)
					.addComponent(chckbxHomeToday)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblShow)
						.addComponent(chckbxCommandHints))
					.addGap(12)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(rdbtnSingleClick)
						.addComponent(lblInAgendaView)
						.addComponent(rdbtnDoubleClick))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblToGoTo)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(chckbxCtrl)
						.addComponent(lblQuickAddShortcut)
						.addComponent(chckbxAlt)
						.addComponent(chckbxShift)
						.addComponent(chckbxWin)
						.addComponent(txtShortcut, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(12)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnSave)
						.addComponent(btnReset))
					.addContainerGap(130, Short.MAX_VALUE))
		);
		frmDoitPreferences.getContentPane().setLayout(groupLayout);
		
		// Show preferences in window
		loadPreferences();
	}

	private void loadPreferences() {
		Preferences prefs = Preferences.userNodeForPackage(this.getClass());
		if (prefs.get(DEFAULT_VIEW, GuiMain.CARD_AGENDA).equals(DEFAULT_VIEW_DEAFULT)) {
			rdbtnAgenda.setSelected(true);
		} else {
			rdbtnList.setSelected(true);
		}
		chckbxShowTodaysTasks.setSelected(prefs.getBoolean(DEFAULT_SHOW_TODAY, DEFAULT_SHOW_TODAY_DEFAULT));
		
		chckbxHomeDefault.setSelected(prefs.getBoolean(HOME_GOES_TO_DEFAULT_VIEW, HOME_GOES_TO_DEFAULT_VIEW_DEFAULT));
		chckbxHomeToday.setSelected(prefs.getBoolean(HOME_GOES_TO_TODAY, HOME_GOES_TO_TODAY_DEFAULT));
		
		chckbxCommandHints.setSelected(prefs.getBoolean(SHOW_HINTS, SHOW_HINTS_DEFAULT));
		
		if (prefs.getInt(NUM_CLICKS_EDIT, NUM_CLICKS_EDIT_DEFAULT) == 2) {
			rdbtnDoubleClick.setSelected(true);
		} else {
			rdbtnSingleClick.setSelected(true);
		}
			
		chckbxCtrl.setSelected(prefs.getBoolean(SHORTCUT_KEY_CTRL, SHORTCUT_KEY_CTRL_DEFAULT));
		chckbxAlt.setSelected(prefs.getBoolean(SHORTCUT_KEY_ALT, SHORTCUT_KEY_ALT_DEFAULT));
		chckbxShift.setSelected(prefs.getBoolean(SHORTCUT_KEY_SHIFT, SHORTCUT_KEY_SHIFT_DEFAULT));
		chckbxWin.setSelected(prefs.getBoolean(SHORTCUT_KEY_WIN, SHORTCUT_KEY_WIN_DEFAULT));
		
		txtShortcut.setText(prefs.get(SHORTCUT_KEY_EXTRA, SHORTCUT_KEY_EXTRA_DEFAULT));
	}

	@Override
	public void runUI() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GuiPreferences window = new GuiPreferences();
					window.frmDoitPreferences.setVisible(true);
				} catch (Exception e) {
					log.log(Level.WARNING, "Exception when showing preferences window", e);
				}
			}
		});		
	}

	private void savePreferences() {
		Preferences prefs = Preferences.userNodeForPackage(this.getClass());
		if (rdbtnAgenda.isSelected()) {
			prefs.put(DEFAULT_VIEW, GuiMain.CARD_AGENDA);
		} else {
			prefs.put(DEFAULT_VIEW, GuiMain.CARD_LIST);
		}
		prefs.putBoolean(DEFAULT_SHOW_TODAY, chckbxShowTodaysTasks.isSelected());
		
		prefs.putBoolean(HOME_GOES_TO_DEFAULT_VIEW, chckbxHomeDefault.isSelected());
		prefs.putBoolean(HOME_GOES_TO_TODAY, chckbxHomeToday.isSelected());
		
		prefs.putBoolean(SHOW_HINTS, chckbxCommandHints.isSelected());
		
		if (rdbtnDoubleClick.isSelected()) {
			prefs.putInt(NUM_CLICKS_EDIT, 2);
		} else {
			prefs.putInt(NUM_CLICKS_EDIT, 1);
		}
			
		prefs.putBoolean(SHORTCUT_KEY_CTRL, chckbxCtrl.isSelected());
		prefs.putBoolean(SHORTCUT_KEY_ALT, chckbxAlt.isSelected());
		prefs.putBoolean(SHORTCUT_KEY_SHIFT, chckbxShift.isSelected());
		prefs.putBoolean(SHORTCUT_KEY_WIN, chckbxWin.isSelected());
		
		String shortcutExtra = txtShortcut.getText().trim().toUpperCase();
		if (shortcutExtra.length() == 1) {
			prefs.put(SHORTCUT_KEY_EXTRA, shortcutExtra);
		} else {
			JOptionPane.showMessageDialog(frmDoitPreferences,
					ERR_INVALID_SHORTCUT_CHAR);
			return;
		}
	}

	private static GuiPreferences theOne;
	private JCheckBox chckbxHomeDefault;
	private JCheckBox chckbxShowTodaysTasks;
	private JCheckBox chckbxHomeToday;
	private final ButtonGroup buttonGroup_1 = new ButtonGroup();
	private JRadioButton rdbtnSingleClick;
	private JRadioButton rdbtnDoubleClick;
	public static GuiPreferences getInstance() {
		if(theOne == null){
			theOne = new GuiPreferences();
		}
		return theOne;
	}
}
