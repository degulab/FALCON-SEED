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
 *  Copyright 2007-2011  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)MExecDefImportDialog.java	2.0.0	2012/11/05
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecDefImportDialog.java	1.10	2011/02/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing.tree;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.aadl.module.swing.FilenameValidator;
import ssac.falconseed.module.MExecDefFileManager;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.falconseed.runner.view.dialog.FileChooserManager;
import ssac.util.io.Files;
import ssac.util.io.VirtualFile;
import ssac.util.logging.AppLogger;
import ssac.util.swing.Application;
import ssac.util.swing.JStaticMultilineTextPane;


/**
 * モジュール実行定義インポートダイアログ
 * 
 * @version 2.0.0	2012/11/05
 * @since 1.10
 */
public class MExecDefImportDialog extends MExecDefFolderChooser
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final Dimension DM_MIN_SIZE = new Dimension(320, 480);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** インポートするファイル選択ボタン **/
	private JButton	_btnChooseFile;
	/** インポートするファイルのフルパス **/
	private JTextComponent	_stcImportFile;
	/** インポートするモジュール実行定義 **/
	private JTextComponent	_stcImportMExecDefs;
	
	/** インポートファイル **/
	private File			_importFile;
	/** インポート先のディレクトリ **/
	private VirtualFile	_targetDir;
	/** インポートファイルに含まれるモジュール実行定義エントリ **/
	private String[]		_mexecdefEntries;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MExecDefImportDialog(Frame owner, FilenameValidator validator)
	{
		super(owner, RunnerMessages.getInstance().MExecDefImportDlg_Title, validator);
		setConfiguration(AppSettings.IMPORTMEXECDEF_DLG, AppSettings.getInstance().getConfiguration());
	}
	
	public MExecDefImportDialog(Dialog owner, FilenameValidator validator)
	{
		super(owner, RunnerMessages.getInstance().MExecDefImportDlg_Title, validator);
		setConfiguration(AppSettings.IMPORTMEXECDEF_DLG, AppSettings.getInstance().getConfiguration());
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public File getImportFile() {
		return _importFile;
	}
	
	public VirtualFile getSelectedDirectory() {
		return _targetDir;
	}
	
	public String[] getImportMExecDefEntries() {
		return _mexecdefEntries;
	}
	
	static public String[] getMExecDefEntriesFromMExecDefFile(File file)
	throws ZipException, IOException
	{
		// open zip file
		ZipFile zipfile = new ZipFile(file);
		
		// get entries
		List<String> list = new ArrayList<String>();
		try {
			Enumeration<? extends ZipEntry> entries = zipfile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				if (entry.isDirectory() && !isDescendingFromEntries(list, entry.getName())) {
					// directory external exist filter
					String name = entry.getName();
					if (zipfile.getEntry(name + MExecDefFileManager.MEXECDEF_PREFS_FILENAME) != null) {
						// MExecDef dir found!
						list.add(name);
					}
				}
			}
		}
		finally {
			try {
				zipfile.close();
			} catch (Throwable ex) {}
		}
		
		if (!list.isEmpty()) {
			return list.toArray(new String[list.size()]);
		} else {
			return null;
		}
	}
	
	static protected boolean isDescendingFromEntries(final List<String> entries, final String target) {
		if (!entries.isEmpty()) {
			for (String entry : entries) {
				if (target.startsWith(entry)) {
					return true;
				}
			}
		}
		return false;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	protected JButton createChooseFileButton() {
		return CommonResources.createBrowseButton(RunnerMessages.getInstance().MExecDefImportDlg_Tooltip_ChooseImportFile);
	}
	
	protected JStaticMultilineTextPane createTextComponent() {
		JStaticMultilineTextPane tc = new JStaticMultilineTextPane();
		tc.setWrapStyleWord(false);
		return tc;
	}
	
	@Override
	protected void createContentComponents() {
		super.createContentComponents();
		
		this._btnChooseFile = createChooseFileButton();
		this._stcImportFile = createTextComponent();
		this._stcImportMExecDefs = createTextComponent();
	}
	
	protected JPanel createNorthPanel() {
		// create Import panel
		JPanel pnlImport = new JPanel(new GridBagLayout());
		
		JLabel lblFile = new JLabel(RunnerMessages.getInstance().MExecDefImportDlg_Label_ImportTargetFile + ":");
		JLabel lblMExecDefs = new JLabel(RunnerMessages.getInstance().MExecDefImportDlg_Label_ImportTargetMExecDef + ":");
		
		Border border = BorderFactory.createCompoundBorder(
							BorderFactory.createEmptyBorder(0, 0, 5, 0),
							BorderFactory.createTitledBorder(RunnerMessages.getInstance().MExecDefImportDlg_Label_ImportFileArea));
		pnlImport.setBorder(border);
		
		GridBagConstraints gbc = new GridBagConstraints();
		Insets defInsets = gbc.insets;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		//--- import file
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		pnlImport.add(lblFile, gbc);
		gbc.gridy++;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		pnlImport.add(_stcImportFile, gbc);
		gbc.gridx++;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		pnlImport.add(_btnChooseFile, gbc);
		gbc.gridy++;
		//--- import MExecDef
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(5, 0, 0, 0);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		pnlImport.add(lblMExecDefs, gbc);
		gbc.gridy++;
		gbc.insets = defInsets;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		pnlImport.add(_stcImportMExecDefs, gbc);
		gbc.gridy++;
		
		// create north panel
		JPanel pnlNorth = new JPanel(new BorderLayout());
		pnlNorth.add(pnlImport, BorderLayout.CENTER);
		pnlNorth.add(getDescriptionLabel(), BorderLayout.NORTH);
		
		return pnlNorth;
	}

	@Override
	protected Dimension getDefaultSize() {
		return DM_MIN_SIZE;
	}

	@Override
	protected void setupMainContents() {
		// create content components
		createContentComponents();
		
		// create main panel
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(CommonResources.DIALOG_CONTENT_BORDER);
		
		// setup layout
		mainPanel.add(getTreePane(), BorderLayout.CENTER);
		mainPanel.add(createNorthPanel(), BorderLayout.NORTH);
		
		// add to root pane
		//this.add(mainPanel, BorderLayout.CENTER);
		this.getContentPane().add(mainPanel, BorderLayout.CENTER);
	}

	@Override
	protected void setupActions() {
		super.setupActions();
		
		_btnChooseFile.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				onChooseFileButton();
			}
		});
	}
	
	@Override
	protected void setupDialogConditions() {
		//super.setupDialogConditions();
		
		setResizable(true);
		setStoreLocation(false);
		setStoreSize(true);
		
		Dimension dmMin = getDefaultSize();
		if (dmMin == null) {
			dmMin = DM_MIN_SIZE;
		}
		this.setMinimumSize(dmMin);
		setKeepMinimumSize(true);
		
		//this.setLocationRelativeTo(null);
	}
	
	@Override
	protected void updateButtons() {
		super.updateButtons();
		getOkButton().setEnabled(true);
	}

	protected void onChooseFileButton() {
		File initialFile = FileChooserManager.getInitialChooseFile(AppSettings.MEXECDEF_IMPORT, _importFile);
		
		File selectedFile = FileChooserManager.chooseOpenFile(this,
								RunnerMessages.getInstance().MExecDefImportDlg_Tooltip_ChooseImportFile,
								false, initialFile, FileChooserManager.getMExecDefFileFilter());
		if (selectedFile == null) {
			// user canceled
			return;
		}
		FileChooserManager.setLastChooseFile(AppSettings.MEXECDEF_IMPORT, selectedFile);
		
		// zip ファイル解析
		String[] mexecdefEntries = null;
		try {
			mexecdefEntries = getMExecDefEntriesFromMExecDefFile(selectedFile);
		}
		catch (Throwable ex) {
			String errmsg = CommonMessages.formatErrorMessage(CommonMessages.getInstance().msgCouldNotReadFile,
												ex, selectedFile.getAbsolutePath());
			AppLogger.error("<MExecDefImportDialog#onChooseFileButton> " + errmsg);
			Application.showErrorMessage(this, errmsg);
			return;
		}
		if (mexecdefEntries == null || mexecdefEntries.length <= 0) {
			String errmsg = CommonMessages.formatErrorMessage(RunnerMessages.getInstance().msgMExecDefImportIllegalSource,
												null, selectedFile.getAbsolutePath());
			Application.showErrorMessage(this, errmsg);
			return;
		}
		
		// 表示更新
		this._importFile = selectedFile;
		this._mexecdefEntries = mexecdefEntries;
		_stcImportFile.setText(selectedFile.getAbsolutePath());
		StringBuilder sb = new StringBuilder();
		sb.append(removeTerminateEntrySeparator(mexecdefEntries[0]));
		for (int i = 1; i < mexecdefEntries.length; i++) {
			sb.append("\n");
			sb.append(removeTerminateEntrySeparator(mexecdefEntries[i]));
		}
		_stcImportMExecDefs.setText(sb.toString());
	}
	
	protected String removeTerminateEntrySeparator(String entryName) {
		if (entryName == null)
			return entryName;
		
		if (!entryName.endsWith(Files.CommonSeparator)) {
			return entryName;
		}
		
		return entryName.substring(0, entryName.length()-1);
	}

	@Override
	protected boolean doOkAction() {
		// 読み込み元ファイルが指定されているかのチェック
		if (_importFile == null || _importFile.isDirectory()) {
			String errmsg = RunnerMessages.getInstance().msgMExecDefImportNotFile;
			Application.showErrorMessage(this, errmsg);
			return false;
		}
		if (_mexecdefEntries == null || _mexecdefEntries.length <= 0) {
			String errmsg = CommonMessages.formatErrorMessage(RunnerMessages.getInstance().msgMExecDefImportIllegalSource,
												null, _importFile.getAbsolutePath());
			Application.showErrorMessage(this, errmsg);
			return false;
		}
		
		// モジュール実行定義が選択されているかのチェック
		VirtualFile targetDir = getTreePane().getSelectionFile();
		if (targetDir != null && !targetDir.isDirectory()) {
			// 配置先の選択は、ディレクトリではないファイル
			targetDir = targetDir.getParentFile();
		}
		if (targetDir != null && MExecDefFileManager.isModuleExecDefData(targetDir)) {
			// 選択された配置先は、モジュール実行定義のディレクトリ
			targetDir = targetDir.getParentFile();
		}
		if (targetDir == null) {
			String errmsg = RunnerMessages.getInstance().msgMExecDefImportNoTarget;
			Application.showErrorMessage(this, errmsg);
			return false;
		}
		
		// OK
		_targetDir = targetDir;
		return true;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
