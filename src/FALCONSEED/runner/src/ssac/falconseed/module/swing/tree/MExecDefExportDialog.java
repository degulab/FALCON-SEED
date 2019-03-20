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
 * @(#)MExecDefExportDialog.java	1.10	2011/02/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing.tree;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
import ssac.util.Strings;
import ssac.util.io.Files;
import ssac.util.io.VirtualFile;
import ssac.util.swing.Application;
import ssac.util.swing.JStaticMultilineTextPane;

/**
 * モジュール実行定義エクスポートダイアログ
 * 
 * @version 1.10	2011/02/14
 * @since 1.10
 */
public class MExecDefExportDialog extends MExecDefFileChooser
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final Dimension DM_MIN_SIZE = new Dimension(320, 480);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** エクスポート先ファイル選択ボタン **/
	private JButton	_btnChooseFile;
	/** エクスポート先ファイルのフルパス **/
	private JTextComponent	_stcExportFile;

	/** エクスポート先のファイル **/
	private File			_exportFile;
	/** エクスポート対象のモジュール実行定義 **/
	private VirtualFile	_targetMExecDef;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MExecDefExportDialog(Frame owner, FilenameValidator validator)
	{
		super(owner, RunnerMessages.getInstance().MExecDefExportDlg_Title, validator);
		setConfiguration(AppSettings.EXPORTMEXECDEF_DLG, AppSettings.getInstance().getConfiguration());
	}
	
	public MExecDefExportDialog(Dialog owner, FilenameValidator validator) {
		super(owner, RunnerMessages.getInstance().MExecDefExportDlg_Title, validator);
		setConfiguration(AppSettings.EXPORTMEXECDEF_DLG, AppSettings.getInstance().getConfiguration());
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public File getExportFile() {
		return _exportFile;
	}
	
	public VirtualFile getMExecDefData() {
		return _targetMExecDef;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected JButton createChooseFileButton() {
		return CommonResources.createBrowseButton(RunnerMessages.getInstance().MExecDefExportDlg_Tooltip_ChooseExportFile);
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
		this._stcExportFile = createTextComponent();
	}
	
	protected JPanel createNorthPanel() {
		// create Import panel
		JPanel pnlExport = new JPanel(new GridBagLayout());
		
		JLabel lblFile = new JLabel(RunnerMessages.getInstance().MExecDefExportDlg_Label_ExportTargetFile + ":");
		
		Border border = BorderFactory.createCompoundBorder(
							BorderFactory.createEmptyBorder(0, 0, 5, 0),
							BorderFactory.createTitledBorder(RunnerMessages.getInstance().MExecDefExportDlg_Label_ExportFileArea));
		pnlExport.setBorder(border);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		//--- export file
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		pnlExport.add(lblFile, gbc);
		gbc.gridy++;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		pnlExport.add(_stcExportFile, gbc);
		gbc.gridx++;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		pnlExport.add(_btnChooseFile, gbc);
		gbc.gridy++;
		
		// create north panel
		JPanel pnlNorth = new JPanel(new BorderLayout());
		pnlNorth.add(pnlExport, BorderLayout.CENTER);
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
		getApplyButton().setEnabled(false);
		getOkButton().setEnabled(true);
	}

	protected void onChooseFileButton() {
		File initialFile = FileChooserManager.getInitialChooseFile(AppSettings.MEXECDEF_EXPORT, _exportFile);
		
		File selectedFile = FileChooserManager.chooseSaveFile(this,
								RunnerMessages.getInstance().MExecDefExportDlg_Tooltip_ChooseExportFile,
								initialFile, FileChooserManager.getMExecDefFileFilter());
		if (selectedFile == null) {
			// user canceled
			return;
		}
		
		if (!Strings.endsWithIgnoreCase(selectedFile.getName(), MExecDefFileManager.EXT_FILE_MEXECDEF)) {
			selectedFile = new File(Files.addExtension(selectedFile.getPath(), MExecDefFileManager.EXT_FILE_MEXECDEF));
		}
		FileChooserManager.setLastChooseFile(AppSettings.MEXECDEF_EXPORT, selectedFile);
		
		// 表示更新
		this._exportFile = selectedFile;
		_stcExportFile.setText(selectedFile.getAbsolutePath());
	}

	@Override
	protected boolean doOkAction() {
		// 出力先ファイルが指定されているかのチェック
		if (_exportFile == null || _exportFile.isDirectory()) {
			String errmsg = RunnerMessages.getInstance().msgMExecDefExportNotFile;
			Application.showErrorMessage(this, errmsg);
			return false;
		}
		
		// モジュール実行定義が選択されているかのチェック
		VirtualFile targetMExecDef = getTreePane().getSelectionFile();
		if (!MExecDefFileManager.isModuleExecDefData(targetMExecDef)) {
			String errmsg = RunnerMessages.getInstance().msgMExecDefExportNoSource;
			Application.showErrorMessage(this, errmsg);
			return false;
		}
		
		// 出力先ファイルの正当性チェック(上書き確認)
		if (_exportFile.exists()) {
			int ret = FileChooserManager.confirmFileOverwrite(this, _exportFile, true);
			if (ret != JOptionPane.YES_OPTION) {
				return false;
			}
		}
		boolean canWritable;
		try {
			if (_exportFile.exists()) {
				canWritable = _exportFile.canWrite();
			} else {
				canWritable = true;
			}
		} catch (Throwable ex) {
			canWritable = false;
		}
		if (!canWritable) {
			String errmsg = CommonMessages.formatErrorMessage(CommonMessages.getInstance().msgNotHaveWritePermission, null, _exportFile.getAbsolutePath());
			Application.showErrorMessage(this, errmsg);
			return false;
		}
		
		// OK
		_targetMExecDef = targetMExecDef;
		return true;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
