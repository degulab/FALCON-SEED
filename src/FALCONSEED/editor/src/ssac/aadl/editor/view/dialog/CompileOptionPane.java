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
 * @(#)CompileOptionPane.java	1.19	2012/02/23
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CompileOptionPane.java	1.14	2009/12/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CompileOptionPane.java	1.10	2009/01/28
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CompileOptionPane.java	1.00	2008/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.view.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.aadl.editor.EditorMessages;
import ssac.aadl.editor.setting.AppSettings;
import ssac.aadl.editor.view.JEncodingComboBox;
import ssac.aadl.module.setting.CompileSettings;
import ssac.aadl.module.swing.FileDialogManager;
import ssac.util.Strings;
import ssac.util.logging.AppLogger;
import ssac.util.swing.StaticTextComponent;

/**
 * ビルドオプションダイアログのコンパイル・オプション・パネル
 * 
 * @version 1.19	2012/02/23
 */
public class CompileOptionPane extends JPanel implements IBuildOptionPane
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final CompileSettings	settings;
	private final boolean readOnly;

	/*--- 作成しない
	private ButtonGroup	bgJavaCompiler;
	private JRadioButton	rbDefaultJavaCompiler;
	private JRadioButton	rbCustomJavaCompiler;
	private JTextComponent	lblDefaultJavaCompiler;
	private JTextComponent	lblCustomJavaCompiler;
	private JButton		btnSelectJavaCompiler;
	---*/
	
	private ButtonGroup	bgEncoding;
	private JRadioButton	rbDefaultEncoding;
	private JRadioButton	rbCustomEncoding;
	private JLabel			lblDefaultEncoding;
	private JEncodingComboBox	cmbCustomEncoding;
	
	private JCheckBox		chkOptionDestFile;
	private JCheckBox		chkOptionSourceDir;
	private JCheckBox		chkOptionNoManifest;
	private JCheckBox		chkOptionManifest;
	private JCheckBox		chkOptionCompileOnly;
	private JCheckBox		chkOptionNoWarning;
	private JCheckBox		chkOptionVerbose;

	private JTextComponent	lblCustomDestFile;
	private JTextComponent	lblCustomSourceDir;
	private JTextComponent	lblCustomManifest;
	private JButton		btnSelectDestFile;
	private JButton		btnSelectSourceDir;
	private JButton		btnSelectManifest;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CompileOptionPane(CompileSettings settings, boolean readOnly) {
		super(new GridBagLayout());
		if (settings == null)
			throw new NullPointerException();
		this.settings = settings;
		this.readOnly = readOnly;
		setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		initialComponent();
		restoreOptionSettings();
	}
	
	protected void initialComponent() {
		// items
		//JPanel p1 = createJavaCompilerChooser();
		JPanel p2 = createTextEncodingChooser();
		JPanel p3 = createCompileOptions();
		
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
		/*--- 作成しない
		//--- Java compiler
		this.add(p1, gbc);
		gbc.gridy++;
		---*/
		//--- Text encoding
		this.add(p2, gbc);
		gbc.gridy++;
		//--- Compile options
		this.add(p3, gbc);
		gbc.gridy++;
		//--- Dummy
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 1;
		this.add(new JLabel(), gbc);
		gbc.gridy++;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public String getTitle() {
		return EditorMessages.getInstance().BuildOptionDlg_Title_Compile;
	}

	//------------------------------------------------------------
	// implement IBuildOptionPane interfaces
	//------------------------------------------------------------
	/**
	 * このオプションを保持する設定情報が読み込み専用の場合に <tt>true</tt> を返す。
	 * @since 1.14
	 */
	public boolean isReadOnly() {
		return readOnly;
	}
	
	public CompileSettings getOptionSettings() {
		return this.settings;
	}
	
	public void restoreOptionSettings() {
		/*--- 作成しない
		// Java compiler location
		boolean flgJavaCompiler = this.settings.isSpecifiedJavaCompiler();
		String strDefCompiler = AppSettings.getInstance().getJavaCompilerJar();
		String strJavaCompiler = this.settings.getJavaCompilerJar();
		this.lblDefaultJavaCompiler.setText(strDefCompiler != null ? "("+strDefCompiler+")" : "(unknown)");
		if (flgJavaCompiler && StringHelper.hasString(strJavaCompiler)) {
			this.lblCustomJavaCompiler.setText(strJavaCompiler);
			this.rbCustomJavaCompiler.setSelected(true);
		} else {
			this.lblCustomJavaCompiler.setText("");
			this.rbDefaultJavaCompiler.setSelected(true);
		}
		---*/
		
		// Text encoding
		boolean flgEncoding = this.settings.isSpecifiedEncoding();
		String strDefEncoding = AppSettings.getInstance().getAadlSourceEncodingName();
		String strEncoding = this.settings.getEncodingName();
		this.lblDefaultEncoding.setText(strDefEncoding != null ? "("+strDefEncoding+")" : "(Default)");
		if (flgEncoding && !Strings.isNullOrEmpty(strEncoding)) {
			this.cmbCustomEncoding.setSelectedEncoding(strEncoding);
			this.rbCustomEncoding.setSelected(true);
		} else {
			this.cmbCustomEncoding.setSelectedEncoding(strDefEncoding);
			this.rbDefaultEncoding.setSelected(true);
		}
		
		// Compile options
		File file;
		boolean checked;
		//--- Destination
		/*--- 作成しない
		file = settings.getDestinationFile();
		if (file != null && !Strings.isNullOrEmpty(file.getPath())) {
			lblCustomDestFile.setText(file.getPath());
			chkOptionDestFile.setSelected(true);
		} else {
			lblCustomDestFile.setText("");
			chkOptionDestFile.setSelected(false);
		}
		---*/
		//--- Source output
		file = settings.getSourceOutputDirFile();
		/*---
		if (file != null) {
			if (!file.exists()) {
				AppLogger.warn("Source output directory is not found at restore compile options : \"" + file.getPath() + "\"");
				file = null;
			}
			else if (!file.isDirectory()) {
				AppLogger.warn("Source output directory is not directory at restore compile options : \"" + file.getPath() + "\"");
				file = null;
			}
		}
		---*/
		if (file != null && !Strings.isNullOrEmpty(file.getPath())) {
			lblCustomSourceDir.setText(file.getPath());
			chkOptionSourceDir.setSelected(true);
		} else {
			lblCustomSourceDir.setText("");
			chkOptionSourceDir.setSelected(false);
		}
		//--- No manifest
		/*--- 作成しない
		checked = this.settings.isDisabledManifest();
		this.chkOptionNoManifest.setSelected(checked);
		---*/
		//--- manifest
		file = settings.getUserManifestFile();
		/*---
		if (file != null) {
			if (!file.exists()) {
				AppLogger.warn("Specified manifest file is not found at restore compile options : \"" + file.getPath() + "\"");
				file = null;
			}
			else if (!file.isFile()) {
				AppLogger.warn("Specified manifest file is not file at restore compile options : \"" + file.getPath() + "\"");
				file = null;
			}
		}
		---*/
		/*--- 作成しない
		if (file != null && !Strings.isNullOrEmpty(file.getPath())) {
			lblCustomManifest.setText(file.getPath());
			chkOptionManifest.setSelected(true);
		} else {
			lblCustomManifest.setText("");
			chkOptionManifest.setSelected(false);
		}
		---*/
		//--- Compile only
		checked = this.settings.isSpecifiedCompileOnly();
		this.chkOptionCompileOnly.setSelected(checked);
		//--- Disable warning
		checked = this.settings.isDisabledWarning();
		this.chkOptionNoWarning.setSelected(checked);
		//--- verbose
		checked = this.settings.isVerbose();
		this.chkOptionVerbose.setSelected(checked);
		
		// update display
		/*--- 作成しない
		updateDisplayForCompiler();
		---*/
		updateDisplayForEncoding();
		/*--- 作成しない
		updateDisplayForDestFile();
		---*/
		updateDisplayForSourceDir();
		/*--- 作成しない
		updateDisplayForManifest();
		---*/
	}
	
	public void storeOptionSettings() {
		if (!readOnly) {
			/*--- 作成しない
			// Java compiler location
			boolean flgJavaCompiler = this.rbCustomJavaCompiler.isSelected();
			String strJavaCompiler = (flgJavaCompiler ? this.lblCustomJavaCompiler.getText() : null);
			this.settings.setJavaCompilerSpecified(flgJavaCompiler);
			this.settings.setJavaCompilerJar(strJavaCompiler);
			---*/

			// Text encoding
			boolean flgEncoding = this.rbCustomEncoding.isSelected();
			String strEncoding = this.cmbCustomEncoding.getSelectedEncoding();
			if (flgEncoding && !Strings.isNullOrEmpty(strEncoding)) {
				this.settings.setEncodingSpecified(true);
				this.settings.setEncodingName(strEncoding);
			} else {
				this.settings.setEncodingSpecified(false);
				this.settings.setEncodingName(null);
			}

			// Compile options
			String strPath;
			boolean checked;
			//--- Destination
			/*--- 保存しない
			checked = this.chkOptionDestFile.isSelected();
			strPath = this.lblCustomDestFile.getText();
			if (checked && !Strings.isNullOrEmpty(strPath)) {
				this.settings.setDestinationPath(strPath);
			} else {
				this.settings.setDestinationPath(null);
			}
			---*/
			//--- Source output
			checked = this.chkOptionSourceDir.isSelected();
			strPath = this.lblCustomSourceDir.getText();
			if (checked && !Strings.isNullOrEmpty(strPath)) {
				this.settings.setSourceOutputDirPath(strPath);
			} else {
				this.settings.setSourceOutputDirPath(null);
			}
			//--- No manifest
			/*--- 保存しない
			checked = this.chkOptionNoManifest.isSelected();
			this.settings.setDisableManifest(checked);
			---*/
			//--- manifest
			/*--- 保存しない
			checked = this.chkOptionManifest.isSelected();
			strPath = this.lblCustomManifest.getText();
			if (checked && !Strings.isNullOrEmpty(strPath)) {
				this.settings.setUserManifestPath(strPath);
			} else {
				this.settings.setUserManifestPath(null);
			}
			---*/
			//--- Compile only
			checked = this.chkOptionCompileOnly.isSelected();
			this.settings.setCompileOnlySpecified(checked);
			//--- Disable warning
			checked = this.chkOptionNoWarning.isSelected();
			this.settings.setWarningDisabled(checked);
			//--- verbose
			checked = this.chkOptionVerbose.isSelected();
			this.settings.setVerbose(checked);
		}
	}

	/**
	 * このコンポーネントを保持するウィンドウが最初に表示された
	 * 直後に呼び出されるイベント。
	 * @param source	このイベントを呼び出したウィンドウ
	 * @since 1.14
	 */
	public void onWindowOpened(Window source) {
		if (readOnly) {
			rbDefaultEncoding.setEnabled(false);
			rbCustomEncoding.setEnabled(false);
			cmbCustomEncoding.setEnabled(false);
			chkOptionDestFile.setEnabled(false);
			chkOptionSourceDir.setEnabled(false);
			chkOptionNoManifest.setEnabled(false);
			chkOptionManifest.setEnabled(false);
			chkOptionCompileOnly.setEnabled(false);
			chkOptionNoWarning.setEnabled(false);
			chkOptionVerbose.setEnabled(false);
			btnSelectDestFile.setEnabled(false);
			btnSelectSourceDir.setEnabled(false);
			btnSelectManifest.setEnabled(false);
		}
	}

	//------------------------------------------------------------
	// Actions
	//------------------------------------------------------------
	
	/*--- 作成しない
	protected void onRadioButtonJavaCompiler(ActionEvent ae) {
		updateDisplayForCompiler();
	}
	---*/
	
	protected void onRadioButtonEncoding(ActionEvent ae) {
		updateDisplayForEncoding();
	}
	
	protected void onCheckOptionDestFile(ActionEvent ae) {
		updateDisplayForDestFile();
	}
	
	protected void onCheckOptionSourceDir(ActionEvent ae) {
		updateDisplayForSourceDir();
	}
	
	protected void onCheckOptionManifest(ActionEvent ae) {
		updateDisplayForManifest();
	}
	
	/*--- 作成しない
	protected void onButtonSelectJavaCompiler(ActionEvent ae) {
		AppLogger.debug("on [Browse...] for Java compiler.");
		String strPath = lblCustomJavaCompiler.getText();
		if (!rbCustomJavaCompiler.isSelected() || StringHelper.isNullOrEmpty(strPath)) {
			strPath = AppSettings.getInstance().getJavaCompilerJar();
			if (StringHelper.isNullOrEmpty(strPath)) {
				strPath = AppSettings.getInstance().getJavaHomePath();
			}
		}
		File initPath = new File(StringHelper.hasString(strPath) ? strPath : "");
		
		File jarPath = FileChooserManager.chooseJarFile(this, initPath, AppMessages.getInstance().chooserTitleJavaCompiler);
		if (jarPath == null) {
			AppLogger.debug("...canceled!");
			return;
		}

		lblCustomJavaCompiler.setText(jarPath.getAbsolutePath());
		if (AppLogger.isDebugEnabled()) {
			AppLogger.debug("...selected [" + jarPath.getAbsolutePath() + "]");
		}
	}
	---*/
	
	protected void onButtonSelectDestFile(ActionEvent ae) {
		AppLogger.debug("on [Browse...] for Destination option.");
		File initPath;
		String strPath = lblCustomDestFile.getText();
		if (Strings.isNullOrEmpty(strPath)) {
			initPath = this.settings.getJavaPropertyFile();
		} else {
			initPath = new File(strPath);
		}
		
		File dstPath = FileChooserManager.chooseDestFile(this, initPath, EditorMessages.getInstance().chooserTitleDestFile);
		if (dstPath == null) {
			AppLogger.debug("...canceled!");
			return;
		}
		
		lblCustomDestFile.setText(dstPath.getAbsolutePath());
		if (AppLogger.isDebugEnabled()) {
			AppLogger.debug("...selected [" + dstPath.getAbsolutePath() + "]");
		}
	}
	
	protected void onButtonSelectSourceDir(ActionEvent ae) {
		AppLogger.debug("on [Browse...] for Source dir option.");
		File initPath;
		String strPath = lblCustomSourceDir.getText();
		if (Strings.isNullOrEmpty(strPath)) {
			initPath = this.settings.getJavaPropertyFile();
		} else {
			initPath = new File(strPath);
		}
		
		File dirPath = FileDialogManager.chooseDirectory(this, initPath, EditorMessages.getInstance().chooserTitleSourceDir, null);
		if (dirPath == null) {
			AppLogger.debug("...canceled!");
			return;
		}
		
		lblCustomSourceDir.setText(dirPath.getAbsolutePath());
		if (AppLogger.isDebugEnabled()) {
			AppLogger.debug("...selected [" + dirPath.getAbsolutePath() + "]");
		}
	}
	
	protected void onButtonSelectManifest(ActionEvent ae) {
		AppLogger.debug("on [Browse...] for Manifest option.");
		File initPath;
		String strPath = lblCustomManifest.getText();
		if (Strings.isNullOrEmpty(strPath)) {
			initPath = this.settings.getJavaPropertyFile();
		} else {
			initPath = new File(strPath);
		}
		
		File maniPath = FileChooserManager.chooseAllFile(this, initPath, EditorMessages.getInstance().chooserTitleManifest);
		if (maniPath == null) {
			AppLogger.debug("...canceled");
			return;
		}
		
		lblCustomManifest.setText(maniPath.getAbsolutePath());
		if (AppLogger.isDebugEnabled()) {
			AppLogger.debug("...selected [" + maniPath.getAbsolutePath() + "]");
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	/*--- 作成しない
	private void updateDisplayForCompiler() {
		boolean flgEnable = this.rbCustomJavaCompiler.isSelected();
		this.lblCustomJavaCompiler.setEnabled(flgEnable);
		this.btnSelectJavaCompiler.setEnabled(flgEnable);
	}
	---*/
	
	private void updateDisplayForEncoding() {
		boolean flgEnable = this.rbCustomEncoding.isSelected();
		this.cmbCustomEncoding.setEnabled(flgEnable);
	}
	
	private void updateDisplayForDestFile() {
		boolean flgEnable = this.chkOptionDestFile.isSelected();
		this.lblCustomDestFile.setEnabled(flgEnable);
		this.btnSelectDestFile.setEnabled(flgEnable);
	}
	
	private void updateDisplayForSourceDir() {
		boolean flgEnable = this.chkOptionSourceDir.isSelected();
		this.lblCustomSourceDir.setEnabled(flgEnable);
		this.btnSelectSourceDir.setEnabled(flgEnable);
	}
	
	private void updateDisplayForManifest() {
		boolean flgEnable = this.chkOptionManifest.isSelected();
		this.lblCustomManifest.setEnabled(flgEnable);
		this.btnSelectManifest.setEnabled(flgEnable);
	}
	
	/*--- 作成しない
	private JPanel createJavaCompilerChooser() {
		// items
		//--- Radio buttons
		this.rbDefaultJavaCompiler = new JRadioButton(AppMessages.getInstance().Label_Default);
		this.rbCustomJavaCompiler = new JRadioButton(AppMessages.getInstance().Label_Custom);
		this.bgJavaCompiler = new ButtonGroup();
		this.bgJavaCompiler.add(this.rbDefaultJavaCompiler);
		this.bgJavaCompiler.add(this.rbCustomJavaCompiler);
		//--- Labels
		this.lblDefaultJavaCompiler = createStaticTextLabel("tools.jar");
		this.lblCustomJavaCompiler = createStaticTextLabel("Custom");
		//--- Select button
		this.btnSelectJavaCompiler	= AppConstants.createBrowseButton(AppMessages.getInstance().Button_Browse);
		//--- Panel
		JPanel panel = new JPanel(new GridBagLayout());
		//--- Border
		Border bri = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		Border bro = BorderFactory.createTitledBorder(bri, AppMessages.getInstance().BuildOptionDlg_Title_Compile_Compiler,
														TitledBorder.LEFT, TitledBorder.TOP);
		panel.setBorder(bro);

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
		//--- radio button
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(this.rbDefaultJavaCompiler, gbc);
		gbc.gridy = 1;
		panel.add(this.rbCustomJavaCompiler, gbc);
		//--- label
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.gridx = 1;
		gbc.gridy = 0;
		panel.add(this.lblDefaultJavaCompiler, gbc);
		gbc.gridy = 1;
		panel.add(this.lblCustomJavaCompiler, gbc);
		//--- button
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.gridx = 2;
		gbc.gridy = 1;
		panel.add(this.btnSelectJavaCompiler, gbc);
		
		// Actions
		//--- radio button
		ActionListener rbal = new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				onRadioButtonJavaCompiler(ae);
			}
		};
		this.rbDefaultJavaCompiler.addActionListener(rbal);
		this.rbCustomJavaCompiler.addActionListener(rbal);
		//--- select button
		this.btnSelectJavaCompiler.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				onButtonSelectJavaCompiler(ae);
			}
		});
		
		// completed
		return panel;
	}
	---*/
	
	private JPanel createTextEncodingChooser() {
		// items
		//--- Radio buttons
		this.rbDefaultEncoding = new JRadioButton(EditorMessages.getInstance().labelDefault);
		this.rbCustomEncoding = new JRadioButton(EditorMessages.getInstance().labelCustom);
		this.bgEncoding = new ButtonGroup();
		this.bgEncoding.add(this.rbDefaultEncoding);
		this.bgEncoding.add(this.rbCustomEncoding);
		//--- Labels
		this.lblDefaultEncoding = new JLabel(this.rbDefaultEncoding.getText());
		//--- Combo box
		this.cmbCustomEncoding = new JEncodingComboBox();
		//--- Panel
		JPanel panel = new JPanel(new GridBagLayout());
		//--- Border
		Border bri = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		Border bro = BorderFactory.createTitledBorder(bri, EditorMessages.getInstance().labelTextEncoding,
														TitledBorder.LEFT, TitledBorder.TOP);
		panel.setBorder(bro);

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
		//--- radio button
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(this.rbDefaultEncoding, gbc);
		gbc.gridy = 1;
		panel.add(this.rbCustomEncoding, gbc);
		//--- label
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.gridx = 1;
		gbc.gridy = 0;
		panel.add(this.lblDefaultEncoding, gbc);
		//--- combo
		gbc.gridy = 1;
		panel.add(this.cmbCustomEncoding, gbc);
		
		// Actions
		//--- radio button
		ActionListener rbal = new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				onRadioButtonEncoding(ae);
			}
		};
		this.rbDefaultEncoding.addActionListener(rbal);
		this.rbCustomEncoding.addActionListener(rbal);
		
		// completed
		return panel;
	}
	
	private JPanel createCompileOptions() {
		// items
		//--- Check box
		this.chkOptionDestFile = new JCheckBox(EditorMessages.getInstance().BuildOptionDlg_Title_Options_Dest);
		this.chkOptionSourceDir = new JCheckBox(EditorMessages.getInstance().BuildOptionDlg_Title_Options_SrcDir);
		this.chkOptionNoManifest = new JCheckBox(EditorMessages.getInstance().BuildOptionDlg_Title_Options_NoMani);
		this.chkOptionManifest = new JCheckBox(EditorMessages.getInstance().BuildOptionDlg_Title_Options_Mani);
		this.chkOptionCompileOnly = new JCheckBox(EditorMessages.getInstance().BuildOptionDlg_Title_Options_CompileOnly);
		this.chkOptionNoWarning = new JCheckBox(EditorMessages.getInstance().BuildOptionDlg_Title_Options_NoWarn);
		this.chkOptionVerbose = new JCheckBox(EditorMessages.getInstance().BuildOptionDlg_Title_Options_Verbose);
		//--- Labels
		this.lblCustomDestFile = createStaticTextLabel("Destination file");
		this.lblCustomSourceDir = createStaticTextLabel("Source dir");
		this.lblCustomManifest = createStaticTextLabel("Manifest file");
		//--- Buttons
		this.btnSelectDestFile	= CommonResources.createBrowseButton(CommonMessages.getInstance().Button_Browse);
		this.btnSelectSourceDir	= CommonResources.createBrowseButton(CommonMessages.getInstance().Button_Browse);
		this.btnSelectManifest	= CommonResources.createBrowseButton(CommonMessages.getInstance().Button_Browse);
		//--- Panel
		JPanel panel = new JPanel(new GridBagLayout());
		//--- Border
		Border bri = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		Border bro = BorderFactory.createTitledBorder(bri, EditorMessages.getInstance().BuildOptionDlg_Title_Compile_Options,
														TitledBorder.LEFT, TitledBorder.TOP);
		panel.setBorder(bro);

		// Layout
		//--- init
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		//--- dest file
	/*--- 作成しない
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		panel.add(this.chkOptionDestFile, gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.gridx = 1;
		panel.add(this.lblCustomDestFile, gbc);
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.gridx = 2;
		panel.add(this.btnSelectDestFile, gbc);
		gbc.gridy++;
	---*/
		//--- source dir
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		panel.add(this.chkOptionSourceDir, gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.gridx = 1;
		panel.add(this.lblCustomSourceDir, gbc);
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.gridx = 2;
		panel.add(this.btnSelectSourceDir, gbc);
		gbc.gridy++;
		//--- No Manifest
	/*--- 作成しない
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		panel.add(this.chkOptionNoManifest, gbc);
		gbc.gridy++;
	---*/
		//--- Manifest
	/*--- 作成しない
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		panel.add(this.chkOptionManifest, gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.gridx = 1;
		panel.add(this.lblCustomManifest, gbc);
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.gridx = 2;
		panel.add(this.btnSelectManifest, gbc);
		gbc.gridy++;
	---*/
		//--- Compile only
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		panel.add(this.chkOptionCompileOnly, gbc);
		gbc.gridy++;
		//--- No warning
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		panel.add(this.chkOptionNoWarning, gbc);
		gbc.gridy++;
		//--- Verbose
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		panel.add(this.chkOptionVerbose, gbc);
		gbc.gridy++;
		/*--- old codes
		//--- Check box
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(this.chkOptionDestFile, gbc);
		gbc.gridy++;
		panel.add(this.chkOptionSourceDir, gbc);
		gbc.gridy++;
		panel.add(this.chkOptionNoManifest, gbc);
		gbc.gridy++;
		panel.add(this.chkOptionManifest, gbc);
		gbc.gridy++;
		panel.add(this.chkOptionCompileOnly, gbc);
		gbc.gridy++;
		panel.add(this.chkOptionNoWarning, gbc);
		gbc.gridy++;
		panel.add(this.chkOptionVerbose, gbc);
		//--- label
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.gridx = 1;
		gbc.gridy = 0;
		panel.add(this.lblCustomDestFile, gbc);
		gbc.gridy++;
		panel.add(this.lblCustomSourceDir, gbc);
		gbc.gridy++;
		gbc.gridy++;
		panel.add(this.lblCustomManifest, gbc);
		//--- button
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.gridx = 2;
		gbc.gridy = 0;
		panel.add(this.btnSelectDestFile, gbc);
		gbc.gridy++;
		panel.add(this.btnSelectSourceDir, gbc);
		gbc.gridy++;
		gbc.gridy++;
		panel.add(this.btnSelectManifest, gbc);
		--- end of old codes ---*/
		//--- dummy
		gbc.anchor = GridBagConstraints.NORTHEAST;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 0;
		gbc.weighty = 1;
		gbc.gridwidth = 3;
		gbc.gridx = 0;
		gbc.gridy = 8;
		panel.add(new JLabel(), gbc);
		
		// Actions
		//--- Destination
		this.chkOptionDestFile.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				onCheckOptionDestFile(ae);
			}
		});
		this.btnSelectDestFile.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				onButtonSelectDestFile(ae);
			}
		});
		//--- Source output dir
		this.chkOptionSourceDir.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				onCheckOptionSourceDir(ae);
			}
		});
		this.btnSelectSourceDir.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				onButtonSelectSourceDir(ae);
			}
		});
		//--- manifest
		this.chkOptionManifest.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				onCheckOptionManifest(ae);
			}
		});
		this.btnSelectManifest.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				onButtonSelectManifest(ae);
			}
		});
		
		// completed
		return panel;
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
