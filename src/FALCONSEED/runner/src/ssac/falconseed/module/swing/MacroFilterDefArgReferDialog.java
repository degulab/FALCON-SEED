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
 *  Copyright 2007-2014  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)MacroFilterDefArgReferDialog.java	3.1.0	2014/05/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MacroFilterDefArgReferDialog.java	2.0.0	2012/10/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

import ssac.aadl.common.CommonResources;
import ssac.falconseed.module.IModuleArgConfig;
import ssac.falconseed.module.ModuleArgConfig;
import ssac.falconseed.runner.ModuleRunner;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.util.Strings;
import ssac.util.swing.AbBasicDialog;

/**
 * マクロフィルタの定義引数から参照する引数を選択するダイアログ。
 * 
 * @version 3.1.0	2014/05/16
 * @since 2.0.0
 */
public class MacroFilterDefArgReferDialog extends AbBasicDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	private final MacroFilterEditModel		_datamodel;
	private final ModuleArgConfig			_targetArg;
	
	private JList	_cList;
	
	private IModuleArgConfig	_selected = null;

	/**
	 * 引数参照の前に付加する文字列のテキストボックス
	 * @since 3.1.0
	 */
	private JTextField			_tfPrefix;
	/**
	 * 引数参照の後に付加する文字列のテキストボックス
	 * @since 3.1.0
	 */
	private JTextField			_tfSuffix;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MacroFilterDefArgReferDialog(Frame owner, MacroFilterEditModel datamodel, ModuleArgConfig targetArg) {
		super(owner, RunnerMessages.getInstance().MacroFilterDefArgReferDlg_Title, true);
		if (datamodel == null)
			throw new NullPointerException("'datamodel' is null.");
		if (targetArg == null)
			throw new NullPointerException("'targetArg' is null.");
		if (targetArg.isFixedValue() || targetArg.getParameterType() == null)
			throw new IllegalArgumentException("'targetArg' is fixed value!");
		this._datamodel = datamodel;
		this._targetArg = targetArg;
		setConfiguration(AppSettings.MACROFILTERDEFARGREF_DLG, AppSettings.getInstance().getConfiguration());
	}
	
	public MacroFilterDefArgReferDialog(Dialog owner, MacroFilterEditModel datamodel, ModuleArgConfig targetArg) {
		super(owner, RunnerMessages.getInstance().MacroFilterDefArgReferDlg_Title, true);
		if (datamodel == null)
			throw new NullPointerException("'datamodel' is null.");
		if (targetArg == null)
			throw new NullPointerException("'targetArg' is null.");
		if (targetArg.isFixedValue() || targetArg.getParameterType() == null)
			throw new IllegalArgumentException("'targetArg' is fixed value!");
		this._datamodel = datamodel;
		this._targetArg = targetArg;
		setConfiguration(AppSettings.MACROFILTERDEFARGREF_DLG, AppSettings.getInstance().getConfiguration());
	}

	@Override
	public void initialComponent() {
		_cList = new JList(createListModel());
		_cList.setCellRenderer(new ArgReferListRenderer());
		_cList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Prefix/Suffix(@since 3.1.0)
		_tfPrefix = new JTextField();
		_tfSuffix = new JTextField();
		Object targetArgValue = _targetArg.getValue();
		if (targetArgValue instanceof FilterArgReferValueEditModel) {
			FilterArgReferValueEditModel refValue = (FilterArgReferValueEditModel)targetArgValue;
			_tfPrefix.setText(refValue.getPrefix());
			_tfSuffix.setText(refValue.getSuffix());
			targetArgValue = refValue.getReferencedArgument();
		}
		
		super.initialComponent();
		
		restoreConfiguration();
		
		// 参照を選択(@since 3.1.0)
		if (targetArgValue != null) {
			_cList.setSelectedValue(targetArgValue, true);
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public IModuleArgConfig getSelectedArgument() {
		return _selected;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	@Override
	protected JButton createApplyButton() {
		return null;	// no [apply] button
	}

	@Override
	protected boolean doOkAction() {
		// 選択された定義引数を保存
		if (_cList.isSelectionEmpty()) {
			// no selection
			ModuleRunner.showErrorMessage(this, RunnerMessages.getInstance().msgChooseReferArgument);
			return false;
		}
		FilterArgEditModel selModel = (FilterArgEditModel)_cList.getSelectedValue();
		
		// Check Prefix
		String strPrefix = _tfPrefix.getText();
		
		// Check Suffix
		String strSuffix = _tfSuffix.getText();
		
		// Prefix/Suffix があれば、固有のインスタンスを生成(@since 3.1.0)
		if (!Strings.isNullOrEmpty(strPrefix) || !Strings.isNullOrEmpty(strSuffix)) {
			// Prefix/Suffix の指定あり
			_selected = new FilterArgReferValueEditModel(selModel, strPrefix, strSuffix);
		} else {
			// 単純な参照
			_selected = selModel;
		}
		
		return super.doOkAction();
	}

	@Override
	protected void setupActions() {
		super.setupActions();
	}

	@Override
	protected void setupDialogConditions() {
		super.setupDialogConditions();
		
		setResizable(true);
		setKeepMinimumSize(true);
	}

	@Override
	protected void setupMainContents() {
		super.setupMainContents();
		
		JScrollPane scList = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scList.setViewportView(this._cList);

		JLabel lblTitle = new JLabel(RunnerMessages.getInstance().ArgReferDlg_Label_args);
		
		JPanel pnlMain = new JPanel(new GridBagLayout());
		pnlMain.setBorder(CommonResources.DIALOG_CONTENT_BORDER);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.NONE;
		pnlMain.add(lblTitle, gbc);
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		pnlMain.add(scList, gbc);
		
		// 付加する文字の入力コンポーネント
		gbc.gridwidth = 1;
		gbc.weighty = 0;
		gbc.insets = new Insets(5, 0, 0, 0);
		
		// 前に付加する文字
		//--- ラベル
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.NONE;
		pnlMain.add(new JLabel(RunnerMessages.getInstance().ArgReferDlg_Label_prefix + " "), gbc);
		//--- 入力フィールド
		gbc.gridx = 1;
		gbc.weightx = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		pnlMain.add(_tfPrefix, gbc);
		
		// 後に付加する文字
		//--- ラベル
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.NONE;
		pnlMain.add(new JLabel(RunnerMessages.getInstance().ArgReferDlg_Label_suffix + " "), gbc);
		//--- 入力フィールド
		gbc.gridx = 1;
		gbc.weightx = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		pnlMain.add(_tfSuffix, gbc);
		
		this.getContentPane().add(pnlMain, BorderLayout.CENTER);
	}
	
	protected ListModel createListModel() {
		return _datamodel.getReferenceableMExecDefArguments(_targetArg);
	}

	@Override
	protected Dimension getDefaultSize() {
		return new Dimension(320,300);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
