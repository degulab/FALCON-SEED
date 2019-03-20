/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  Copyright 2007-2012  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)PreferenceDialog.java	2.0.0	2012/10/05
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)PreferenceDialog.java	1.14	2009/12/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)PreferenceDialog.java	1.10	2008/12/03
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)PreferenceDialog.java	1.00	2008/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.view.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.JTextComponent;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.aadl.common.StartupSettings;
import ssac.aadl.editor.AADLEditor;
import ssac.aadl.editor.EditorMessages;
import ssac.aadl.editor.setting.AppSettings;
import ssac.aadl.module.swing.FileDialogManager;
import ssac.util.MacUtilities;
import ssac.util.Strings;
import ssac.util.io.JavaInfo;
import ssac.util.logging.AppLogger;
import ssac.util.swing.BasicDialog;
import ssac.util.swing.StaticTextComponent;

/**
 * 設定ダイアログ
 * 
 * @version 2.0.0	2012/10/05
 */
public class PreferenceDialog extends BasicDialog
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static private final Dimension DM_MIN_SIZE = new Dimension(580, 420);
	
	static private final String FONT_SAMPLE_TEXT = "ABCabcあいう亜居宇";
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	static private String[]	fontFamilyNames;
	static private final String[]	fontSizes = {
		"8","9","10","11","12","13","14","15","16","18","20","22","24","28","32"
	};
	static private String[] fontTargetNames;
	static private LinkedHashMap<String,String> fontTargetKeys = new LinkedHashMap<String,String>();
	
	private final HashMap<String,Font>	selectedFont = new HashMap<String,Font>();
	
	private JavaInfo			customJavaInfo;
	
	private boolean			flgIgnoreListEvent = false;

	//--- Java Home
	private ButtonGroup		bgJavaHome;
	private JRadioButton		rbDefaultJavaHome;
	private JRadioButton		rbCustomJavaHome;
	private JLabel				titleDefaultJavaVersion;
	private JLabel				titleDefaultJavaCommand;
	private JLabel				titleDefaultJavaCompiler;
	private JLabel				titleCustomJavaVersion;
	private JLabel				titleCustomJavaCommand;
	private JLabel				titleCustomJavaCompiler;
	private JTextComponent		lblDefaultJavaHome;
	private JTextComponent		lblDefaultJavaVersion;
	private JTextComponent		lblDefaultJavaCommand;
	private JTextComponent		lblDefaultJavaCompiler;
	private JTextComponent		lblCustomJavaHome;
	private JTextComponent		lblCustomJavaVersion;
	private JTextComponent		lblCustomJavaCommand;
	private JTextComponent		lblCustomJavaCompiler;
	private JButton			btnSelectJavaHome;

	//--- Text encoding
	private CharsetNameChooserPanel	pnlEncodingAadlSource;
	private CharsetNameChooserPanel	pnlEncodingAadlMacro;
	private CharsetNameChooserPanel	pnlEncodingAadlCsv;
	private CharsetNameChooserPanel	pnlEncodingAadlTxt;
	
	//--- Fonts
	private JTextComponent		stcFontFamily;
	private JTextComponent		stcFontSize;
	private JTextField			stcFontSample;
	private JComboBox			cmbFontTarget;
	private JList				lstFontFamily;
	private JList				lstFontSize;

	//--- Tab
	private JTabbedPane		tab;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public PreferenceDialog() {
		this(null);
	}
	
	public PreferenceDialog(Frame owner) {
		super(owner, EditorMessages.getInstance().PreferenceDlg_Title_Main, true);
		
		// initialize
		initialLocalComponent();
		
		// restore settings
		loadSettings();
		restorePreferences();
	}

	protected void initialLocalComponent() {
		// パネル
		JPanel paneBasic = createBasicSettingPanel();
		JPanel paneFont = createFontChooser();
		JPanel paneEncoding = createTextEncodingPanel();
		
		// タブ
		tab = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
		tab.add(EditorMessages.getInstance().PreferenceDlg_Title_Tab_General, paneBasic);
		tab.add(EditorMessages.getInstance().PreferenceDlg_Title_Tab_Font, paneFont);
		tab.add(EditorMessages.getInstance().PreferenceDlg_Title_Tab_Encoding, paneEncoding);
		if (StartupSettings.isSpecifiedDebugOption()) {
			tab.add(EditorMessages.getInstance().PreferenceDlg_Title_Tab_Debug, createDebugSettingsPanel());
		}
		
		// setup main panel layout
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBorder(CommonResources.DIALOG_CONTENT_BORDER);
		//mainPanel.add(paneBasic, BorderLayout.CENTER);
		mainPanel.add(tab, BorderLayout.CENTER);
		
		// setup Dialog style
		this.setResizable(true);
		//--- setup minimum size
		Dimension dmMin = getDefaultSize();
		if (dmMin == null) {
			dmMin = new Dimension(200,300);
		}
		this.setMinimumSize(dmMin);
		setKeepMinimumSize(true);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public void restorePreferences() {
		AppSettings settings = AppSettings.getInstance();
		
		// JDK location
		boolean flgJavaHome = settings.isSpecifiedJavaHomePath();
		JavaInfo defJavaInfo = settings.getDefaultJavaInfo();
		this.customJavaInfo = settings.getUserJavaInfo();
		if (flgJavaHome) {
			this.rbCustomJavaHome.setSelected(true);
		} else {
			this.rbDefaultJavaHome.setSelected(true);
		}
		updateJavaInfo(defJavaInfo,
				this.lblDefaultJavaHome, this.lblDefaultJavaVersion,
				this.lblDefaultJavaCommand, this.lblDefaultJavaCompiler);
		updateJavaInfo(this.customJavaInfo,
				this.lblCustomJavaHome, this.lblCustomJavaVersion,
				this.lblCustomJavaCommand, this.lblCustomJavaCompiler);
		
		// Fonts
		String[] fontTargets = getFontTargetNames();
		for (String strTarget : fontTargets) {
			String strPrefix = fontTargetKeys.get(strTarget);
			if (strPrefix != null) {
				Font font = settings.getFont(strPrefix);
				if (font != null) {
					selectedFont.put(strTarget, font);
				}
			}
		}
		
		// Text file encoding
		pnlEncodingAadlSource.setupDisplay(settings.isSpecifiedAadlSourceEncodingName(),
				settings.getDefaultAadlSourceEncodingName(), settings.getAadlSourceEncodingName());
		pnlEncodingAadlMacro.setupDisplay(settings.isSpecifiedAadlMacroEncodingName(),
				settings.getDefaultAadlMacroEncodingName(), settings.getAadlMacroEncodingName());
		pnlEncodingAadlCsv.setupDisplay(settings.isSpecifiedAadlCsvEncodingName(),
				settings.getDefaultAadlCsvEncodingName(), settings.getAadlCsvEncodingName());
		pnlEncodingAadlTxt.setupDisplay(settings.isSpecifiedAadlTxtEncodingName(),
				settings.getDefaultAadlTxtEncodingName(), settings.getAadlTxtEncodingName());
		
		// update display
		updateDisplayForJavaHome();
		updateDisplayForSelectedFont();
	}
	
	public void storePreferences() {
		AppSettings settings = AppSettings.getInstance();
		
		// JDK location
		boolean flgJavaHome = this.rbCustomJavaHome.isSelected();
		JavaInfo newInfo = (flgJavaHome ? this.customJavaInfo : null);
		settings.setUserJavaInfo(newInfo);
		
		// Font
		String[] fontTargets = getFontTargetNames();
		for (String strTarget : fontTargets) {
			String strPrefix = fontTargetKeys.get(strTarget);
			if (strPrefix != null) {
				Font font = selectedFont.get(strTarget);
				settings.setFont(strPrefix, font);
			}
		}
		
		// Text file encoding
		boolean flgEncoding;
		String strEncoding;
		//--- AADL source file
		flgEncoding = pnlEncodingAadlSource.isCustomSelected();
		strEncoding = pnlEncodingAadlSource.getSelectedCustomEncodingName();
		if (flgEncoding && !Strings.isNullOrEmpty(strEncoding)) {
			settings.setAadlSourceEncodingName(strEncoding);
		} else {
			settings.setAadlSourceEncodingName(null);
		}
		//--- AADL macro file
		flgEncoding = pnlEncodingAadlMacro.isCustomSelected();
		strEncoding = pnlEncodingAadlMacro.getSelectedCustomEncodingName();
		if (flgEncoding && !Strings.isNullOrEmpty(strEncoding)) {
			settings.setAadlMacroEncodingName(strEncoding);
		} else {
			settings.setAadlMacroEncodingName(null);
		}
		//--- AADL csvFile
		flgEncoding = pnlEncodingAadlCsv.isCustomSelected();
		strEncoding = pnlEncodingAadlCsv.getSelectedCustomEncodingName();
		if (flgEncoding && !Strings.isNullOrEmpty(strEncoding)) {
			settings.setAadlCsvEncodingName(strEncoding);
		} else {
			settings.setAadlCsvEncodingName(null);
		}
		//--- AADL txtFile
		flgEncoding = pnlEncodingAadlTxt.isCustomSelected();
		strEncoding = pnlEncodingAadlTxt.getSelectedCustomEncodingName();
		if (flgEncoding && !Strings.isNullOrEmpty(strEncoding)) {
			settings.setAadlTxtEncodingName(strEncoding);
		} else {
			settings.setAadlTxtEncodingName(null);
		}
	}
	
	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------

	protected void onButtonApply() {
		applySettings();
	}

	protected boolean onButtonOK() {
		return applySettings();
	}
	
	protected boolean onButtonCancel() {
		return true;	// Close dialog
	}
	
	protected void dialogClose(int result) {
		super.dialogClose(result);
	}
	
	protected void onRadioButtonJavaHome(ActionEvent ae) {
		updateDisplayForJavaHome();
	}
	
	protected void onButtonSelectJavaHome(ActionEvent ae) {
		AppLogger.debug("on [Browse...] for Java home.");
		String strPath = lblCustomJavaHome.getText();
		if (!rbCustomJavaHome.isSelected() || Strings.isNullOrEmpty(strPath)) {
			strPath = AppSettings.getInstance().getDefaultJavaInfo().getHomePath();
		}
		File initPath = new File(!Strings.isNullOrEmpty(strPath) ? strPath : "");

		File homePath;
		if (MacUtilities.isMac()) {
			// Package 対応
			JFileChooser fc = FileDialogManager.createFileChooser(JFileChooser.DIRECTORIES_ONLY, false, initPath);
			fc.setDialogTitle(EditorMessages.getInstance().chooserTitleJavaHome);
			fc.setApproveButtonText(null);
			Object oldProp = fc.getClientProperty("JFileChooser.packageIsTraversable");
			fc.putClientProperty("JFileChooser.packageIsTraversable", "always");
			int ret = fc.showOpenDialog(this);
			if (ret != JFileChooser.APPROVE_OPTION) {
				// user canceled
				homePath = null;
			} else {
				// selected
				homePath = fc.getSelectedFile();
			}
			fc.putClientProperty("JFileChooser.packageIsTraversable", oldProp);
		}
		else {
			homePath = FileDialogManager.chooseDirectory(this, initPath, EditorMessages.getInstance().chooserTitleJavaHome, null);
		}
		if (homePath == null) {
			AppLogger.debug("...canceled!");
			return;
		}
		
		// update user JavaInfo
		if (AppLogger.isDebugEnabled()) {
			AppLogger.debug("...selected [" + homePath.getAbsolutePath() + "]");
		}
		this.customJavaInfo = new JavaInfo(homePath);
		this.customJavaInfo.collect();
		updateJavaInfo(this.customJavaInfo,
				this.lblCustomJavaHome, this.lblCustomJavaVersion,
				this.lblCustomJavaCommand, this.lblCustomJavaCompiler);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	private void updateJavaInfo(JavaInfo info,
								  JTextComponent cHome,
								  JTextComponent cVersion,
								  JTextComponent cCommand,
								  JTextComponent cCompiler)
	{
		final String strNone = EditorMessages.getInstance().labelMessageNotFound;
		String strHome = null;
		String strVersion = null;
		String strCommand = null;
		String strCompiler = null;
		
		if (info != null) {
			strHome = info.getHomePath();
			strVersion = info.getVersion();
			strCommand = info.getCommandPath();
			strCompiler = info.getCompilerPath();
		}
		
		if (strVersion == null)
			strVersion = strNone;
		if (strCommand == null)
			strCommand = strNone;
		if (strCompiler == null)
			strCompiler = strNone;
		
		cHome.setText(strHome);
		cVersion.setText(strVersion);
		cCommand.setText(strCommand);
		cCompiler.setText(strCompiler);
	}
	
	private void updateDisplayForJavaHome() {
		boolean flgEnable = this.rbCustomJavaHome.isSelected();
		this.titleCustomJavaVersion.setEnabled(flgEnable);
		this.titleCustomJavaCommand.setEnabled(flgEnable);
		this.titleCustomJavaCompiler.setEnabled(flgEnable);
		this.lblCustomJavaHome.setEnabled(flgEnable);
		this.lblCustomJavaVersion.setEnabled(flgEnable);
		this.lblCustomJavaCommand.setEnabled(flgEnable);
		this.lblCustomJavaCompiler.setEnabled(flgEnable);
		this.btnSelectJavaHome.setEnabled(flgEnable);
	}
	
	private void updateDisplayForSelectedFont() {
		String strFontTarget = (String)cmbFontTarget.getSelectedItem();
		Font targetFont = selectedFont.get(strFontTarget);
		if (targetFont == null) {
			targetFont = stcFontFamily.getFont();
		}
		String strFontFamily = targetFont.getFamily();
		String strFontSize = Integer.toString(targetFont.getSize());
		stcFontFamily.setText(strFontFamily);
		stcFontSize.setText(strFontSize);
		{
			flgIgnoreListEvent = true;
			lstFontFamily.setSelectedValue(strFontFamily, true);
			lstFontSize.setSelectedValue(strFontSize, true);
			flgIgnoreListEvent = false;
		}
		stcFontSample.setFont(targetFont);
	}
	
	private void updateDisplayForFontSample() {
		String strFontTarget = (String)cmbFontTarget.getSelectedItem();
		Font targetFont = createSelectedFont();
		selectedFont.put(strFontTarget, targetFont);
		stcFontSample.setFont(targetFont);
	}
	
	private Font createSelectedFont() {
		String strFontFamily = stcFontFamily.getText();
		String strFontSize = stcFontSize.getText();
		int fSize = Integer.parseInt(strFontSize);
		return new Font(strFontFamily, Font.PLAIN, fSize);
	}
	
	private boolean applySettings() {
		// store properties from GUI
		storePreferences();
		
		// commit settings
		boolean succeeded;
		try {
			AppSettings.flush();
			succeeded = true;
		}
		catch (Throwable ex) {
			AppLogger.error(ex);
			String strmsg = EditorMessages.getErrorMessage(EditorMessages.MessageID.ERR_FILE_SETTING_WRITE,
														ex, AppSettings.getPropertyFilePath());
			AADLEditor.showErrorMessage(this, strmsg);
			succeeded = false;
		}
		return succeeded;
	}
	
	@Override
	protected Point getDefaultLocation() {
		return super.getDefaultLocation();
	}

	@Override
	protected Dimension getDefaultSize() {
		//return super.getDefaultSize();
		return DM_MIN_SIZE;
	}

	@Override
	protected void loadSettings() {
		super.loadSettings(AppSettings.PREFERENCE_DLG, AppSettings.getInstance().getConfiguration());
	}

	@Override
	protected void saveSettings() {
		super.saveSettings(AppSettings.PREFERENCE_DLG, AppSettings.getInstance().getConfiguration());
//		// テスト
//		Point loc = this.getLocationOnScreen();
//		Dimension dm = this.getSize();
//		AppLogger.debug("Location : " + loc.toString());
//		AppLogger.debug("Size : " + dm.toString());
//		// テスト
	}
	
	private JPanel createBasicSettingPanel() {
		// setup panel
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
		// items
		JPanel p1 = createJavaHomeChooser();
		
		// Layout
		//--- init
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		//--- Java home
		panel.add(p1, gbc);
		gbc.gridy++;
		//--- blank
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		panel.add(new JLabel(), gbc);
		
		return panel;
	}
	
	private JPanel createJavaHomeChooser() {
		// items
		//--- Radio buttons
		this.rbDefaultJavaHome = new JRadioButton(EditorMessages.getInstance().PreferenceDlg_Title_DefaultJavaHome);
		this.rbCustomJavaHome = new JRadioButton(EditorMessages.getInstance().PreferenceDlg_Title_CustomJavaHome);
		this.bgJavaHome = new ButtonGroup();
		this.bgJavaHome.add(this.rbDefaultJavaHome);
		this.bgJavaHome.add(this.rbCustomJavaHome);
		//--- Labels
		titleDefaultJavaVersion = new JLabel(EditorMessages.getInstance().PreferenceDlg_Title_JavaVersion);
		titleDefaultJavaCommand = new JLabel(EditorMessages.getInstance().PreferenceDlg_Title_JavaCommand);
		titleDefaultJavaCompiler = new JLabel(EditorMessages.getInstance().PreferenceDlg_Title_JavaCompiler);
		titleCustomJavaVersion = new JLabel(EditorMessages.getInstance().PreferenceDlg_Title_JavaVersion);
		titleCustomJavaCommand = new JLabel(EditorMessages.getInstance().PreferenceDlg_Title_JavaCommand);
		titleCustomJavaCompiler = new JLabel(EditorMessages.getInstance().PreferenceDlg_Title_JavaCompiler);
		this.lblDefaultJavaHome = createStaticTextLabel(EditorMessages.getInstance().labelMessageNotFound);
		this.lblDefaultJavaVersion = createStaticTextLabel(EditorMessages.getInstance().labelMessageNotFound);
		this.lblDefaultJavaCommand = createStaticTextLabel(EditorMessages.getInstance().labelMessageNotFound);
		this.lblDefaultJavaCompiler = createStaticTextLabel(EditorMessages.getInstance().labelMessageNotFound);
		this.lblCustomJavaHome = createStaticTextLabel(EditorMessages.getInstance().labelMessageNotFound);
		this.lblCustomJavaVersion = createStaticTextLabel(EditorMessages.getInstance().labelMessageNotFound);
		this.lblCustomJavaCommand = createStaticTextLabel(EditorMessages.getInstance().labelMessageNotFound);
		this.lblCustomJavaCompiler = createStaticTextLabel(EditorMessages.getInstance().labelMessageNotFound);
		//--- Select button
		this.btnSelectJavaHome = CommonResources.createBrowseButton(CommonMessages.getInstance().Button_Browse);
		//--- Panel
		JPanel panel = new JPanel(new GridBagLayout());
		//--- Border
		Border bri = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		Border bro = BorderFactory.createTitledBorder(bri, EditorMessages.getInstance().PreferenceDlg_Title_TargetJDK,
														TitledBorder.LEFT, TitledBorder.TOP);
		panel.setBorder(bro);

		/*--- 使用しない
		// Size setting
		Dimension dm1 = titleDefaultJavaVersion.getPreferredSize();
		Dimension dm2 = titleDefaultJavaCommand.getPreferredSize();
		Dimension dm3 = titleDefaultJavaCompiler.getPreferredSize();
		int w = dm1.width;
		int h = dm1.height;
		w = Math.max(w, dm2.width);
		h = Math.max(h, dm2.height);
		w = Math.max(w, dm3.width);
		h = Math.max(h, dm3.height);
		Dimension newSize = new Dimension(w,h);
		titleDefaultJavaVersion.setSize(newSize);
		titleDefaultJavaVersion.setMinimumSize(newSize);
		titleDefaultJavaCommand.setSize(newSize);
		titleDefaultJavaCommand.setMinimumSize(newSize);
		titleDefaultJavaCompiler.setSize(newSize);
		titleDefaultJavaCompiler.setMinimumSize(newSize);
		titleCustomJavaVersion.setSize(newSize);
		titleCustomJavaVersion.setMinimumSize(newSize);
		titleCustomJavaCommand.setSize(newSize);
		titleCustomJavaCommand.setMinimumSize(newSize);
		titleCustomJavaCompiler.setSize(newSize);
		titleCustomJavaCompiler.setMinimumSize(newSize);
		---*/
		
		// Layout
		//--- init
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(1,1,1,1);
		gbc.ipadx = 0;
		gbc.ipady = 0;
		int default_y = 0;
		int custom_y = 4;
		//--- radio button
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy = default_y;
		panel.add(this.rbDefaultJavaHome, gbc);
		gbc.gridy = custom_y;
		panel.add(this.rbCustomJavaHome, gbc);
		//--- label
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridy = default_y + 1;
		panel.add(titleDefaultJavaVersion, gbc);
		gbc.gridy++;
		panel.add(titleDefaultJavaCommand, gbc);
		gbc.gridy++;
		panel.add(titleDefaultJavaCompiler, gbc);
		gbc.gridy = custom_y + 1;
		panel.add(titleCustomJavaVersion, gbc);
		gbc.gridy++;
		panel.add(titleCustomJavaCommand, gbc);
		gbc.gridy++;
		panel.add(titleCustomJavaCompiler, gbc);
		//--- Text component
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.gridx = 1;
		gbc.gridy = default_y;
		panel.add(this.lblDefaultJavaHome, gbc);
		gbc.gridy++;
		panel.add(this.lblDefaultJavaVersion, gbc);
		gbc.gridy++;
		panel.add(this.lblDefaultJavaCommand, gbc);
		gbc.gridy++;
		panel.add(this.lblDefaultJavaCompiler, gbc);
		gbc.gridy = custom_y;
		panel.add(this.lblCustomJavaHome, gbc);
		gbc.gridy++;
		panel.add(this.lblCustomJavaVersion, gbc);
		gbc.gridy++;
		panel.add(this.lblCustomJavaCommand, gbc);
		gbc.gridy++;
		panel.add(this.lblCustomJavaCompiler, gbc);
		//--- button
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.gridx = 2;
		gbc.gridy = custom_y;
		panel.add(this.btnSelectJavaHome, gbc);
		
		// Actions
		//--- radio button
		ActionListener rbal = new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				onRadioButtonJavaHome(ae);
			}
		};
		this.rbDefaultJavaHome.addActionListener(rbal);
		this.rbCustomJavaHome.addActionListener(rbal);
		//--- select button
		this.btnSelectJavaHome.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				onButtonSelectJavaHome(ae);
			}
		});
		
		return panel;
	}
	
	private JPanel createFontChooser() {
		// Color
		Color crBack = (new JTextField()).getBackground();
		
		// items
		//--- Font target
		JLabel lblTarget = new JLabel(EditorMessages.getInstance().PreferenceDlg_Title_Font_Target);
		cmbFontTarget = new JComboBox(getFontTargetNames());
		//--- Font family
		JLabel lblFamily = new JLabel(EditorMessages.getInstance().PreferenceDlg_Title_Font_Family);
		stcFontFamily = createStaticTextLabel("Family");
		stcFontFamily.setBackground(crBack);
		lstFontFamily = new JList(getFontFamilyNames());
		JScrollPane scFontFamily = new JScrollPane(lstFontFamily,
													JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
													JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		//--- Font size
		JLabel lblSize = new JLabel(EditorMessages.getInstance().PreferenceDlg_Title_Font_Size);
		stcFontSize = createStaticTextLabel("Size");
		stcFontSize.setBackground(crBack);
		lstFontSize = new JList(getFontSizes());
		JScrollPane scFontSize = new JScrollPane(lstFontSize,
												JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
												JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		//--- Font sample
		Border obd = BorderFactory.createTitledBorder(
						BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
						EditorMessages.getInstance().PreferenceDlg_Title_Font_Sample,
						TitledBorder.LEFT, TitledBorder.TOP
					);
		Border bd = BorderFactory.createCompoundBorder(obd, BorderFactory.createEmptyBorder(3,3,3,3));
		stcFontSample = new JTextField(FONT_SAMPLE_TEXT);
		stcFontSample.setEditable(false);
		stcFontSample.setHorizontalAlignment(JTextField.CENTER);
		Dimension dm = stcFontSample.getPreferredSize();
		dm.height = 50;
		stcFontSample.setPreferredSize(dm);
		//stcFontSample.setSize(dm);
		stcFontSample.setMinimumSize(dm);
		stcFontSample.setBackground(crBack);
		JPanel pnlSample = new JPanel(new BorderLayout());
		pnlSample.add(stcFontSample, BorderLayout.CENTER);
		pnlSample.setBorder(bd);
		//--- Panel
		JPanel panel = new JPanel(new GridBagLayout());
		
		// Layout
		//--- init
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(1,1,1,1);
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		//--- target
		panel.add(lblTarget, gbc);
		gbc.gridy++;
		panel.add(cmbFontTarget, gbc);
		gbc.gridy++;
		//--- fonts
		int sy = gbc.gridy;
		//--- Font family
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.weighty = 0;
		panel.add(lblFamily, gbc);
		gbc.gridy++;
		panel.add(stcFontFamily, gbc);
		gbc.gridy++;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		panel.add(scFontFamily, gbc);
		//--- Font size
		gbc.gridy = sy;
		gbc.gridx++;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.weighty = 0;
		panel.add(lblSize, gbc);
		gbc.gridy++;
		panel.add(stcFontSize, gbc);
		gbc.gridy++;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		panel.add(scFontSize, gbc);
		//--- Font sample
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridwidth = 2;
		panel.add(pnlSample, gbc);
		
		// initialize
		cmbFontTarget.setSelectedIndex(0);
		lstFontFamily.setSelectedIndex(0);
		lstFontSize.setSelectedIndex(0);
		
		// Actions
		//--- target
		cmbFontTarget.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				updateDisplayForSelectedFont();
			}
		});
		//--- family
		lstFontFamily.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e) {
				if (!flgIgnoreListEvent) {
					String strValue = (String)lstFontFamily.getSelectedValue();
					stcFontFamily.setText(strValue);
					updateDisplayForFontSample();
				}
			}
		});
		//--- size
		lstFontSize.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e) {
				if (!flgIgnoreListEvent) {
					String strValue = (String)lstFontSize.getSelectedValue();
					stcFontSize.setText(strValue);
					updateDisplayForFontSample();
				}
			}
		});
		
		return panel;
	}
	
	private JPanel createTextEncodingPanel() {
		// setup panel
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
		pnlEncodingAadlSource = new CharsetNameChooserPanel(EditorMessages.getInstance().PreferenceDlg_Title_Encoding_AADLSource);
		pnlEncodingAadlMacro  = new CharsetNameChooserPanel(EditorMessages.getInstance().PreferenceDlg_Title_Encoding_AADLMacro);
		pnlEncodingAadlCsv    = new CharsetNameChooserPanel(EditorMessages.getInstance().PreferenceDlg_Title_Encoding_AADLcsvFile);
		pnlEncodingAadlTxt    = new CharsetNameChooserPanel(EditorMessages.getInstance().PreferenceDlg_Title_Encoding_AADLtxtFile);
		
		// Layout
		//--- init
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		//---
		panel.add(pnlEncodingAadlSource, gbc);
		gbc.gridy++;
		//---
		panel.add(pnlEncodingAadlMacro, gbc);
		gbc.gridy++;
		//---
		panel.add(pnlEncodingAadlCsv, gbc);
		gbc.gridy++;
		//---
		panel.add(pnlEncodingAadlTxt, gbc);
		gbc.gridy++;
		//--- blank
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		panel.add(new JLabel(), gbc);
		
		return panel;
	}
	
	private JPanel createDebugSettingsPanel() {
		// setup panel
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
		// Layout
		//--- init
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		//--- blank
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		panel.add(new JLabel(), gbc);
		
		return panel;
	}
	
	private String[] getFontTargetNames() {
		if (fontTargetNames == null) {
			// font target keys
			fontTargetKeys.put(EditorMessages.getInstance().PreferenceDlg_Title_Target_Editor,
								AppSettings.EDITOR);
			fontTargetKeys.put(EditorMessages.getInstance().PreferenceDlg_Title_Target_Console,
								AppSettings.CONSOLE);
			fontTargetKeys.put(EditorMessages.getInstance().PreferenceDlg_Title_Target_Compile,
								AppSettings.COMPILE);
			// font target names
			fontTargetNames = fontTargetKeys.keySet().toArray(new String[fontTargetKeys.size()]);
		}
		return fontTargetNames;
	}
	
	private String[] getFontFamilyNames() {
		if (fontFamilyNames == null) {
			GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
			fontFamilyNames = genv.getAvailableFontFamilyNames();
		}
		return fontFamilyNames;
	}
	
	private String[] getFontSizes() {
		return fontSizes;
	}
	
	private JTextComponent createStaticTextLabel(String text) {
		/*
		JTextField tf = new JTextField(text);
		tf.setEditable(false);
		return tf;
		*/
		return new StaticTextComponent(text);
	}

}
