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
import java.awt.Dialog.ModalExclusionType;
import java.awt.Window.Type;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.awt.Dialog.ModalityType;

public class GuiPreferences extends UI {

	static final String SHORTCUT_KEY_EXTRA = "DoIt! ShortcutKey Extra";
	static final String SHORTCUT_KEY_WIN = "DoIt! ShortcutKey Win";
	static final String SHORTCUT_KEY_SHIFT = "DoIt! ShortcutKey Shift";
	static final String SHORTCUT_KEY_ALT = "DoIt! ShortcutKey Alt";
	static final String SHORTCUT_KEY_CTRL = "DoIt! ShortcutKey Ctrl";
	static final String HOME_GOES_TO_DEFAULT_VIEW = "DoIt! Home goes to Default View";
	static final String SHOW_HINTS = "DoIt! Show hints";
	static final String DEFAULT_VIEW = "DoIt! Default View";
	
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
		frmDoitPreferences.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		frmDoitPreferences.setTitle("DoIt! Preferences");
		frmDoitPreferences.setBounds(100, 100, 359, 195);
		frmDoitPreferences.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JLabel lblDefaultViewOn = new JLabel("Default View:");
		
		rdbtnAgenda = new JRadioButton("Agenda");
		buttonGroup.add(rdbtnAgenda);
		
		rdbtnList = new JRadioButton("List");
		buttonGroup.add(rdbtnList);
		
		JLabel lblShow = new JLabel("Show:");
		
		JLabel lblQuickAddShortcut = new JLabel("Quick Add Shortcut:");
		
		chckbxCommandHints = new JCheckBox("Command Hints");
		
		chckbxCtrl = new JCheckBox("Ctrl");
		
		chckbxAlt = new JCheckBox("Alt");
		
		chckbxShift = new JCheckBox("Shift");
		
		chckbxWin = new JCheckBox("Win");
		
		txtShortcut = new JTextField();
		txtShortcut.setText("A");
		txtShortcut.setColumns(2);
		
		btnSave = new JButton("Save Preferences");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Preferences prefs = Preferences.userNodeForPackage(this.getClass());
				if(rdbtnAgenda.isSelected()){
					prefs.put(DEFAULT_VIEW, GuiMain2.CARD_AGENDA);
				}else{
					prefs.put(DEFAULT_VIEW, GuiMain2.CARD_LIST);
				}
				
				prefs.putBoolean(HOME_GOES_TO_DEFAULT_VIEW, chckbxHomeGoesTo.isSelected());
				
				prefs.putBoolean(SHOW_HINTS, chckbxCommandHints.isSelected());
					
				prefs.putBoolean(SHORTCUT_KEY_CTRL, chckbxCtrl.isSelected());
				prefs.putBoolean(SHORTCUT_KEY_ALT, chckbxAlt.isSelected());
				prefs.putBoolean(SHORTCUT_KEY_SHIFT, chckbxShift.isSelected());
				prefs.putBoolean(SHORTCUT_KEY_WIN, chckbxWin.isSelected());
				
				String shortcutExtra = txtShortcut.getText().trim().toUpperCase();
				if(shortcutExtra.length() == 1){
					prefs.put(SHORTCUT_KEY_EXTRA, shortcutExtra);
				}else{
					JOptionPane.showMessageDialog(frmDoitPreferences, "The character you have selected for the shortcut is invalid. Please re-enter.");
					return;
				}
				
				frmDoitPreferences.dispose();

			}
		});
		
		chckbxHomeGoesTo = new JCheckBox("Home goes to Default View");
		
		JButton btnReset = new JButton("Reset");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Preferences prefs = Preferences.userNodeForPackage(this.getClass());
				try {
					prefs.clear();
				} catch (BackingStoreException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				frmDoitPreferences.dispose();
			}
		});
		
		GroupLayout groupLayout = new GroupLayout(frmDoitPreferences.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(lblDefaultViewOn)
						.addComponent(lblShow)
						.addComponent(lblQuickAddShortcut))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(chckbxCommandHints)
						.addComponent(chckbxHomeGoesTo)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(rdbtnAgenda)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(rdbtnList))
						.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
							.addGroup(groupLayout.createSequentialGroup()
								.addComponent(btnReset)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(btnSave))
							.addGroup(groupLayout.createSequentialGroup()
								.addComponent(chckbxCtrl)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(chckbxAlt)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(chckbxShift)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(chckbxWin)
								.addGap(6)
								.addComponent(txtShortcut, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
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
					.addComponent(chckbxHomeGoesTo)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(chckbxCommandHints)
						.addComponent(lblShow))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(chckbxCtrl)
						.addComponent(lblQuickAddShortcut)
						.addComponent(chckbxAlt)
						.addComponent(chckbxShift)
						.addComponent(chckbxWin)
						.addComponent(txtShortcut, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnSave)
						.addComponent(btnReset))
					.addContainerGap(15, Short.MAX_VALUE))
		);
		frmDoitPreferences.getContentPane().setLayout(groupLayout);
		
		// Show preferences in window
		Preferences prefs = Preferences.userNodeForPackage(this.getClass());
		if(prefs.get(DEFAULT_VIEW, GuiMain2.CARD_AGENDA).equals(GuiMain2.CARD_AGENDA)){
			rdbtnAgenda.setSelected(true);
		}else{
			rdbtnList.setSelected(true);
		}
		chckbxHomeGoesTo.setSelected(prefs.getBoolean(HOME_GOES_TO_DEFAULT_VIEW, true));
		
		chckbxCommandHints.setSelected(prefs.getBoolean(SHOW_HINTS, true));
			
		chckbxCtrl.setSelected(prefs.getBoolean(SHORTCUT_KEY_CTRL, false));
		chckbxAlt.setSelected(prefs.getBoolean(SHORTCUT_KEY_ALT, false));
		chckbxShift.setSelected(prefs.getBoolean(SHORTCUT_KEY_SHIFT, false));
		chckbxWin.setSelected(prefs.getBoolean(SHORTCUT_KEY_WIN, true));
		
		txtShortcut.setText(prefs.get(SHORTCUT_KEY_EXTRA, "A"));
	}

	@Override
	public void runUI() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GuiPreferences window = new GuiPreferences();
					window.frmDoitPreferences.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});		
	}

	private static GuiPreferences theOne;
	private JCheckBox chckbxHomeGoesTo;
	public static GuiPreferences getInstance() {
		if(theOne == null){
			theOne = new GuiPreferences();
		}
		return theOne;
	}
}
