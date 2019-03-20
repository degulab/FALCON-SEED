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
 *  Copyright 2007-2009  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)ExecuteOptionPane.java	1.14	2009/12/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExecuteOptionPane.java	1.10	2009/01/28
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExecuteOptionPane.java	1.02	2008/05/23
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExecuteOptionPane.java	1.00	2008/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.view.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.aadl.editor.EditorMessages;
import ssac.aadl.editor.build.ClassPaths;
import ssac.aadl.editor.setting.AppSettings;
import ssac.aadl.editor.view.table.ModuleArgValueTableModel;
import ssac.aadl.editor.view.table.ModuleArgValueTablePane;
import ssac.aadl.module.ModuleArgDetail;
import ssac.aadl.module.setting.ExecSettings;
import ssac.aadl.module.swing.FileDialogManager;
import ssac.util.Strings;
import ssac.util.io.Files;
import ssac.util.io.JarFileInfo;
import ssac.util.logging.AppLogger;
import ssac.util.swing.MenuToggleButton;
import ssac.util.swing.StaticTextComponent;

/**
 * ビルドオプションダイアログの実行オプション・パネル
 * 
 * @version 1.14	2009/12/09
 */
public class ExecuteOptionPane extends JPanel implements IBuildOptionPane
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final ExecSettings	settings;
	private final boolean readOnly;

	/*--- 作成しない
	//--- Java command
	private ButtonGroup	bgJavaCmd;
	private JRadioButton	rbDefaultJavaCmd;
	private JRadioButton	rbCustomJavaCmd;
	private JTextComponent	lblDefaultJavaCmd;
	private JTextComponent	tbCustomJavaCmd;
	private JButton		btnJavaCmd;
	---*/
	
	//--- Target info
	private JTextComponent	lblTargetFile;
	private JComboBox		cmbTargetMainClass;
	
	//--- Program arguments
	private ModuleArgValueTablePane	tableArgs;
	private MenuToggleButton	btnArgAdd;
	private MenuToggleButton	btnArgEdit;
	private JButton			btnArgDel;
	private JButton			btnArgUp;
	private JButton			btnArgDown;
	
	//--- Java VM arguments
	private JTextArea		taVMArgs;
	
	//--- Working directory
	private ButtonGroup	bgWorkDir;
	private JRadioButton	rbDefaultWorkDir;
	private JRadioButton	rbCustomWorkDir;
	private JTextComponent	lblDefaultWorkDir;
	private JTextComponent	tbCustomWorkDir;
	private JButton		btnWorkDir;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ExecuteOptionPane(ExecSettings settings, boolean readOnly) {
		super(new GridBagLayout());
		if (settings == null)
			throw new NullPointerException();
		this.settings = settings;
		this.readOnly = readOnly;
		setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		initialComponent();
		this.tableArgs.setDefaultArgumentBaseFile(settings.getWorkDirFile());
		//this.tableArgs.setDefaultArgumentPath(settings.getTargetPath());
		//this.tableArgs.setLastArgumentPath(path);
		restoreOptionSettings();
	}
	
	protected void initialComponent() {
		// items
		//JPanel p1 = createJavaCmdPanel();
		JPanel p2 = createTargetInfoPanel();
		JPanel p3 = createProgramArgumentsPanel();
		JPanel p4 = createVMArgumentsPanel();
		JPanel p5 = createWorkingDirPanel();
		
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
		/*--- 作成しない
		//--- Java command
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weighty = 0;
		this.add(p1, gbc);
		gbc.gridy++;
		---*/
		//--- Target info
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weighty = 0;
		this.add(p2, gbc);
		gbc.gridy++;
		//--- Program arguments
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 1;
		this.add(p3, gbc);
		gbc.gridy++;
		//--- Java VM arguments
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weighty = 0;
		this.add(p4, gbc);
		gbc.gridy++;
		//--- Working directory
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weighty = 0;
		this.add(p5, gbc);
		gbc.gridy++;
		
		// setup readonly
		if (readOnly) {
			cmbTargetMainClass.setEditable(false);
			cmbTargetMainClass.setEnabled(false);
			tableArgs.setEditable(false);
			btnArgAdd .setEnabled(false);
			btnArgEdit.setEnabled(false);
			btnArgDel .setEnabled(false);
			btnArgUp  .setEnabled(false);
			btnArgDown.setEnabled(false);
			taVMArgs  .setEditable(false);
			rbDefaultWorkDir.setEnabled(false);
			rbCustomWorkDir.setEnabled(false);
			btnWorkDir.setEnabled(false);
			tbCustomWorkDir.setEnabled(false);
			JTextField dummyField = new JTextField();
			dummyField.setEditable(false);
			taVMArgs.setBackground(dummyField.getBackground());
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public String getTitle() {
		return EditorMessages.getInstance().BuildOptionDlg_Title_Run;
	}
	
	public void updateProgramArgumentDetails(ModuleArgDetail[] details) {
		tableArgs.updateArgumentDetails(details);
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
	
	public String getLastArgumentPath() {
		return this.tableArgs.getLastArgumentPath();
	}
	
	public void setLastArgumentPath(String path) {
		this.tableArgs.setLastArgumentPath(path);
	}
	
	public ExecSettings getOptionSettings() {
		return this.settings;
	}
	
	public void restoreOptionSettings() {
		/*--- 作成しない
		// java command
		boolean flgJavaCmd = this.settings.isSpecifiedJavaCommand();
		String strDefJavaCmd = AppSettings.getInstance().getCurrentJavaCommandPath();
		String strJavaCmd = this.settings.getJavaCommandPath();
		this.lblDefaultJavaCmd.setText(strDefJavaCmd != null ? "("+strDefJavaCmd+")" : "(java");
		if (flgJavaCmd && StringHelper.hasString(strJavaCmd)) {
			this.tbCustomJavaCmd.setText(strJavaCmd);
			this.rbCustomJavaCmd.setSelected(true);
		} else {
			this.tbCustomJavaCmd.setText("");
			this.rbDefaultJavaCmd.setSelected(true);
		}
		---*/
		
		// target info
		DefaultComboBoxModel cmbMainClasses = null;
		String strMainClassName = null;
		File targetFile = settings.getTargetFile();
		if (targetFile != null) {
			if (targetFile.exists()) {
				String libClassPaths = null;
				try {
					// 実行時ライブラリのクラスパス取得
					ClassPaths pathList = new ClassPaths();
					pathList.appendPaths(settings.getClassPathFiles());
					pathList.appendPaths(AppSettings.getInstance().getExecLibraries());
					libClassPaths = pathList.getClassPathString();
					// Jarファイル情報の収集
					JarFileInfo jfi = new JarFileInfo(targetFile, libClassPaths);
					String[] strMainClasses = jfi.mainClasses();
					if (strMainClasses != null && strMainClasses.length > 0) {
						Arrays.sort(strMainClasses);
						cmbMainClasses = new DefaultComboBoxModel(strMainClasses);
						strMainClassName = settings.getTargetMainClass();
						if (Strings.isNullOrEmpty(strMainClassName)) {
							strMainClassName = jfi.getManifestMainClass();
						}
					}
				}
				catch (Throwable ex) {
					String msg = String.format("Failed to get main-class from target file at restore execute options.\n  - target file : \"%s\"\n  - class paths : [%s]",
							targetFile.getPath(), String.valueOf(libClassPaths));
					AppLogger.warn(msg);
					targetFile = null;
				}
			}
			else {
				AppLogger.warn("Target file is not found at restore execute options : \"" + targetFile.getPath() + "\"");
				targetFile = null;
			}
		}
		if (cmbMainClasses == null) {
			cmbMainClasses = new DefaultComboBoxModel();
		}
		lblTargetFile.setText(targetFile == null ? "" : targetFile.getPath());
		cmbTargetMainClass.setModel(cmbMainClasses);
		if (!Strings.isNullOrEmpty(strMainClassName)) {
			cmbTargetMainClass.setSelectedItem(strMainClassName);
		}
		
		// program args
		ModuleArgValueTableModel argsModel = new ModuleArgValueTableModel(settings.getArgumentDetails(), settings.getProgramArgs());
		tableArgs.setTableModel(argsModel);
		
		// java VM args
		String strVMArgs = settings.getJavaVMArgs();
		taVMArgs.setText(strVMArgs != null ? strVMArgs : "");
		
		// work dir
		//--- 標準の作業ディレクトリ
		String strDefWorkDir = settings.getDefaultWorkDirPath();
		lblDefaultWorkDir.setText(strDefWorkDir==null ? "(Default)" : "(" + strDefWorkDir + ")");
		//--- カスタム作業ディレクトリ
		boolean flgWorkDir = settings.isSpecifiedWorkDir();
		File dir = null;
		if (flgWorkDir) {
			dir = settings.getWorkDirFile();
			/*---
			if (dir != null) {
				if (!dir.exists()) {
					AppLogger.warn("Custom work directory is not found at restore execute options : \"" + dir.getPath() + "\"");
					dir = null;
				}
				else if (!dir.isDirectory()) {
					AppLogger.warn("Custom work directory is not directory at restore execute options : \"" + dir.getPath() + "\"");
					dir = null;
				}
			}
			---*/
			flgWorkDir = (dir != null);
		}
		//--- update display
		if (flgWorkDir && !Strings.isNullOrEmpty(dir.getPath())) {
			tbCustomWorkDir.setText(dir.getPath());
			rbCustomWorkDir.setSelected(true);
		} else {
			tbCustomWorkDir.setText("");
			rbDefaultWorkDir.setSelected(true);
		}
		
		// update display
		if (!readOnly) {
			//updateDisplayForJavaCommand();
			updateDisplayForWorkDir();
		}
		tableArgs.setDefaultArgumentBaseFile(getAvailableWorkDir());
	}
	
	public void storeOptionSettings() {
		// 引数編集確定
		this.tableArgs.stopTableCellEditing();
		
		if (!readOnly) {
			/*--- 作成しない
			// java command
			boolean flgJavaCmd = this.rbCustomJavaCmd.isSelected();
			String strJavaCmd = (flgJavaCmd ? this.tbCustomJavaCmd.getText() : null);
			this.settings.setJavaCommandSpecified(flgJavaCmd);
			this.settings.setJavaCommandPath(strJavaCmd);
			---*/

			// target info
			Object objMainClassName = this.cmbTargetMainClass.getSelectedItem();
			String strMainClassName = (objMainClassName != null ? objMainClassName.toString() : null);
			this.settings.setTargetMainClass(strMainClassName);

			// program args
			ModuleArgValueTableModel argsModel = this.tableArgs.getTableModel();
			this.settings.setProgramArgs(argsModel.getArgumentValues());

			// java VM args
			String strVMArgs = this.taVMArgs.getText();
			this.settings.setJavaVMArgs(strVMArgs);

			// work dir
			boolean flgWorkDir = this.rbCustomWorkDir.isSelected();
			String strWorkDir = (flgWorkDir ? this.tbCustomWorkDir.getText() : null);
			this.settings.setWorkDirSpecified(flgWorkDir);
			this.settings.setWorkDirPath(strWorkDir);
		}
	}
	
	/**
	 * このコンポーネントを保持するウィンドウが最初に表示された
	 * 直後に呼び出されるイベント。
	 * @param source	このイベントを呼び出したウィンドウ
	 * @since 1.14
	 */
	public void onWindowOpened(Window source) {
		tableArgs.adjustTableAllColumnsPreferredWidth();
	}

	//------------------------------------------------------------
	// Actions
	//------------------------------------------------------------
	
	/*--- 作成しない
	protected void onRadioButtonJavaCommand(ActionEvent ae) {
		updateDisplayForJavaCommand();
	}
	---*/
	
	protected void onRadioButtonWorkDir(ActionEvent ae) {
		updateDisplayForWorkDir();
		tableArgs.setDefaultArgumentBaseFile(getAvailableWorkDir());
	}

	/*--- 作成しない
	protected void onButtonSelectJavaCommand(ActionEvent ae) {
		AppLogger.debug("on [Browse...] for Java command.");
		String strPath = tbCustomJavaCmd.getText();
		if (!rbCustomJavaCmd.isSelected() || StringHelper.isNullOrEmpty(strPath)) {
			// デフォルト
			strPath = AppSettings.getInstance().getCurrentJavaCommandPath();
		}
		File initPath = new File(StringHelper.hasString(strPath) ? strPath : "");
		
		File cmdPath = FileChooserManager.chooseAllFile(this, initPath, AppMessages.getInstance().chooserTitleJavaCommand);
		if (cmdPath == null) {
			AppLogger.debug("...canceled!");
			return;
		}
		
		tbCustomJavaCmd.setText(cmdPath.getAbsolutePath());
		if (AppLogger.isDebugEnabled()) {
			AppLogger.debug("...selected [" + cmdPath.getAbsolutePath() + "]");
		}
	}
	---*/
	
	protected void onButtonSelectWorkDir(ActionEvent ae) {
		AppLogger.debug("on [Browse...] for Work dir.");
		String strPath = tbCustomWorkDir.getText();
		if (!rbCustomWorkDir.isSelected() || Strings.isNullOrEmpty(strPath)) {
			// デフォルト
			strPath = this.settings.getDefaultWorkDirPath();
		}
		File initPath = new File(!Strings.isNullOrEmpty(strPath) ? strPath : "");
		
		File dirPath = FileDialogManager.chooseDirectory(this, initPath,
							EditorMessages.getInstance().chooserTitleWorkDir, null);
		if (dirPath == null) {
			AppLogger.debug("...canceled!");
			return;
		}
		
		tbCustomWorkDir.setText(dirPath.getAbsolutePath());
		tableArgs.setDefaultArgumentBaseFile(dirPath.getAbsoluteFile());
		if (AppLogger.isDebugEnabled()) {
			AppLogger.debug("...selected [" + dirPath.getAbsolutePath() + "]");
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	/*--- 作成しない
	private void updateDisplayForJavaCommand() {
		boolean flgEnable = this.rbCustomJavaCmd.isSelected();
		this.tbCustomJavaCmd.setEnabled(flgEnable);
		this.btnJavaCmd.setEnabled(flgEnable);
	}
	---*/
	
	private void updateDisplayForWorkDir() {
		boolean flgEnable = this.rbCustomWorkDir.isSelected();
		this.tbCustomWorkDir.setEnabled(flgEnable);
		this.btnWorkDir.setEnabled(flgEnable);
	}
	
	private File getAvailableWorkDir() {
		boolean flgWorkDir = this.rbCustomWorkDir.isSelected();
		String strWorkDir = (flgWorkDir ? this.tbCustomWorkDir.getText() : null);
		File dir;
		if (Strings.isNullOrEmpty(strWorkDir)) {
			dir = settings.getDefaultWorkDirFile();
		} else {
			dir = Files.normalizeFile(new File(strWorkDir).getAbsoluteFile());
		}
		return dir;
	}
	
	private JPanel createTargetInfoPanel() {
		// items
		//--- Labels
		JLabel lblFile = new JLabel(EditorMessages.getInstance().labelFile);
		JLabel lblMain = new JLabel(EditorMessages.getInstance().labelMainClass);
		//--- Text box
		this.lblTargetFile = createStaticTextLabel("Jar file");
		//--- Combo box
		this.cmbTargetMainClass = new JComboBox();
		//--- Panel
		JPanel panel = new JPanel(new GridBagLayout());
		//--- Border
		Border bri = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		Border bro = BorderFactory.createTitledBorder(bri, EditorMessages.getInstance().BuildOptionDlg_Title_Run_Target,
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
		//--- label
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(lblFile, gbc);
		gbc.gridy = 1;
		panel.add(lblMain, gbc);
		//--- values
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.gridx = 1;
		gbc.gridy = 0;
		panel.add(this.lblTargetFile, gbc);
		gbc.gridy = 1;
		panel.add(this.cmbTargetMainClass, gbc);
		
		// Actions
		
		// completed
		return panel;
	}
	
	private JPanel createProgramArgumentsPanel() {
		// items
		//--- Buttons
		/*
		this.btnArgAdd = new JButton(AppMessages.getInstance().Button_Add);
		this.btnArgEdit = new JButton(AppMessages.getInstance().Button_Edit);
		this.btnArgDel = new JButton(AppMessages.getInstance().Button_Delete);
		this.btnArgUp = new JButton(AppMessages.getInstance().Button_Up);
		this.btnArgDown = new JButton(AppMessages.getInstance().Button_Down);
		*/
		this.btnArgAdd	= CommonResources.createMenuIconButton(CommonResources.ICON_ADD, CommonMessages.getInstance().Button_Add);
		this.btnArgEdit	= CommonResources.createMenuIconButton(CommonResources.ICON_EDIT, CommonMessages.getInstance().Button_Edit);
		this.btnArgDel	= CommonResources.createIconButton(CommonResources.ICON_DELETE, CommonMessages.getInstance().Button_Delete);
		this.btnArgUp	= CommonResources.createIconButton(CommonResources.ICON_ARROW_UP, CommonMessages.getInstance().Button_Up);
		this.btnArgDown	= CommonResources.createIconButton(CommonResources.ICON_ARROW_DOWN, CommonMessages.getInstance().Button_Down);
		this.btnArgDel .setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		this.btnArgUp  .setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		this.btnArgDown.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		//--- table
		this.tableArgs = new ModuleArgValueTablePane();
		this.tableArgs.initialComponent();
		if (!readOnly) {
			this.tableArgs.attachAddButton(this.btnArgAdd);
			this.tableArgs.attachEditButton(this.btnArgEdit);
			this.tableArgs.attachDeleteButton(this.btnArgDel);
			this.tableArgs.attachUpButton(this.btnArgUp);
			this.tableArgs.attachDownButton(this.btnArgDown);
		}
		this.btnArgDown.setSize(30, 20);
		//--- Panel
		JPanel panel = new JPanel(new GridBagLayout());
		//--- Border
		Border bri = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		Border bro = BorderFactory.createTitledBorder(bri, EditorMessages.getInstance().BuildOptionDlg_Title_Run_ProgramArgs,
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
		//--- table
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridheight = 6;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(tableArgs, gbc);
		if (!readOnly) {
			//--- buttons
			gbc.anchor = GridBagConstraints.NORTHEAST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.gridheight = 1;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.gridx = 1;
			gbc.gridy = 0;
			panel.add(this.btnArgAdd, gbc);
			gbc.gridy++;
			panel.add(this.btnArgEdit, gbc);
			gbc.gridy++;
			panel.add(this.btnArgDel, gbc);
			gbc.gridy++;
			panel.add(this.btnArgUp, gbc);
			gbc.gridy++;
			panel.add(this.btnArgDown, gbc);
			//--- dummy
			JLabel lblDummy = new JLabel();
			gbc.fill = GridBagConstraints.BOTH;
			gbc.weighty = 1;
			gbc.gridy++;
			panel.add(lblDummy, gbc);
		}
		
		// Actions
		
		// completed
		return panel;
	}
	
	private JPanel createVMArgumentsPanel() {
		// items
		//--- text box
		this.taVMArgs = new JTextArea(3, 10);
		this.taVMArgs.setEditable(true);
		Dimension dm = this.taVMArgs.getPreferredSize();
		this.taVMArgs.setMinimumSize(dm);
		this.taVMArgs.setPreferredSize(dm);
		JScrollPane sp = new JScrollPane(this.taVMArgs,
										  JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
										  JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setMinimumSize(dm);
		//--- Panel
		JPanel panel = new JPanel(new BorderLayout());
		//--- Border
		Border bri = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		Border bro = BorderFactory.createTitledBorder(bri, EditorMessages.getInstance().BuildOptionDlg_Title_Run_VMArgs,
														TitledBorder.LEFT, TitledBorder.TOP);
		Border br = BorderFactory.createCompoundBorder(bro, BorderFactory.createEmptyBorder(2, 2, 2, 2));
		panel.setBorder(br);

		// Layout
		panel.add(sp, BorderLayout.CENTER);
		
		// Actions
		
		// completed
		return panel;
	}
	
	private JPanel createWorkingDirPanel() {
		// items
		//--- Radio buttons
		this.rbDefaultWorkDir = new JRadioButton(EditorMessages.getInstance().labelDefault);
		this.rbCustomWorkDir = new JRadioButton(EditorMessages.getInstance().labelCustom);
		this.bgWorkDir = new ButtonGroup();
		this.bgWorkDir.add(this.rbDefaultWorkDir);
		this.bgWorkDir.add(this.rbCustomWorkDir);
		//--- Labels
		this.lblDefaultWorkDir = createStaticTextLabel(this.rbDefaultWorkDir.getText());
		this.tbCustomWorkDir = createStaticTextLabel(this.rbCustomWorkDir.getText());
		this.tbCustomWorkDir.setEditable(true);
		//--- Select button
		/*
		this.btnWorkDir = new JButton(AppMessages.getInstance().Button_Browse);
		*/
		this.btnWorkDir	= CommonResources.createBrowseButton(CommonMessages.getInstance().Button_Browse);
		//--- Panel
		JPanel panel = new JPanel(new GridBagLayout());
		//--- Border
		Border bri = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		Border bro = BorderFactory.createTitledBorder(bri, EditorMessages.getInstance().BuildOptionDlg_Title_Run_WorkDir,
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
		panel.add(this.rbDefaultWorkDir, gbc);
		gbc.gridy = 1;
		panel.add(this.rbCustomWorkDir, gbc);
		//--- label
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.gridx = 1;
		gbc.gridy = 0;
		panel.add(this.lblDefaultWorkDir, gbc);
		gbc.gridy = 1;
		panel.add(this.tbCustomWorkDir, gbc);
		//--- button
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.gridx = 2;
		gbc.gridy = 1;
		panel.add(this.btnWorkDir, gbc);
		
		// Actions
		//--- radio button
		ActionListener rbal = new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				onRadioButtonWorkDir(ae);
			}
		};
		this.rbDefaultWorkDir.addActionListener(rbal);
		this.rbCustomWorkDir.addActionListener(rbal);
		//--- select button
		this.btnWorkDir.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				onButtonSelectWorkDir(ae);
			}
		});
		
		// completed
		return panel;
	}
	
	/*--- 作成しない
	private JPanel createJavaCmdPanel() {
		// items
		//--- Radio buttons
		this.rbDefaultJavaCmd = new JRadioButton(AppMessages.getInstance().Label_Default);
		this.rbCustomJavaCmd = new JRadioButton(AppMessages.getInstance().Label_Custom);
		this.bgJavaCmd = new ButtonGroup();
		this.bgJavaCmd.add(this.rbDefaultJavaCmd);
		this.bgJavaCmd.add(this.rbCustomJavaCmd);
		//--- Labels
		this.lblDefaultJavaCmd = createStaticTextLabel(this.rbDefaultJavaCmd.getText());
		this.tbCustomJavaCmd = createStaticTextLabel(this.rbCustomJavaCmd.getText());
		this.tbCustomJavaCmd.setEditable(true);
		//--- Select button
		this.btnJavaCmd	= AppConstants.createBrowseButton(AppMessages.getInstance().Button_Browse);
		//--- Panel
		JPanel panel = new JPanel(new GridBagLayout());
		//--- Border
		Border bri = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		Border bro = BorderFactory.createTitledBorder(bri, AppMessages.getInstance().BuildOptionDlg_Title_Run_JavaCmd,
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
		panel.add(this.rbDefaultJavaCmd, gbc);
		gbc.gridy = 1;
		panel.add(this.rbCustomJavaCmd, gbc);
		//--- label
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.gridx = 1;
		gbc.gridy = 0;
		panel.add(this.lblDefaultJavaCmd, gbc);
		gbc.gridy = 1;
		panel.add(this.tbCustomJavaCmd, gbc);
		//--- button
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.gridx = 2;
		gbc.gridy = 1;
		panel.add(this.btnJavaCmd, gbc);
		
		// Actions
		//--- radio button
		ActionListener rbal = new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				onRadioButtonJavaCommand(ae);
			}
		};
		this.rbDefaultJavaCmd.addActionListener(rbal);
		this.rbCustomJavaCmd.addActionListener(rbal);
		//--- select button
		this.btnJavaCmd.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				onButtonSelectJavaCommand(ae);
			}
		});
		
		// completed
		return panel;
	}
	---*/
	
	private JTextComponent createStaticTextLabel(String text) {
		/*
		JTextField tf = new JTextField(text);
		tf.setEditable(false);
		return tf;
		*/
		return new StaticTextComponent(text);
	}
}
