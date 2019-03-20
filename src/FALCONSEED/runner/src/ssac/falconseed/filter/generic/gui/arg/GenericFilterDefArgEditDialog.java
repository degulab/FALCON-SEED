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
 *  Copyright 2007-2015  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)GenericFilterDefArgEditDialog.java	3.2.1	2015/07/22
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui.arg;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.aadl.fs.module.schema.type.SchemaValueType;
import ssac.aadl.module.ModuleArgType;
import ssac.falconseed.filter.common.gui.util.FilterArgUtil;
import ssac.falconseed.filter.generic.gui.GenericFilterEditModel;
import ssac.falconseed.filter.generic.gui.SchemaValueTypeComboBoxModel;
import ssac.falconseed.module.args.MExecArgString;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.util.properties.ExConfiguration;
import ssac.util.swing.Application;
import ssac.util.swing.ExDialog;

/**
 * 汎用フィルタのフィルタ定義引数編集ダイアログ。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public class GenericFilterDefArgEditDialog extends ExDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 7594334604711937328L;

	static private final Dimension DM_MIN_SIZE = new Dimension(320, 120);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 標準のタイトルメッセージ **/
	private final String				_defaultTitle;
	/** 汎用フィルタの編集用データモデル **/
	private GenericFilterEditModel		_editModel;
	/** 編集対象の引数データモデル **/
	private GenericFilterArgEditModel	_argModel;
	/** 編集前のデータ型、新規の場合は <tt>null</tt> **/
	private SchemaValueType				_orgValueType;
	/** データ型選択コンボボックス **/
	private JComboBox					_cmbValueType;
	/** 引数説明 **/
	private JTextField					_txtDesc;
	
	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public GenericFilterDefArgEditDialog(Frame owner, String title) {
		super(owner, title, true);
		_defaultTitle = title;
		setConfiguration(AppSettings.GENERICFILTERARG_EDIT_DLG, AppSettings.getInstance().getConfiguration());
	}
	
	public GenericFilterDefArgEditDialog(Dialog owner, String title) {
		super(owner, title, true);
		_defaultTitle = title;
		setConfiguration(AppSettings.GENERICFILTERARG_EDIT_DLG, AppSettings.getInstance().getConfiguration());
	}

	/**
	 * 文字列引数定義編集ダイアログの初期化。
	 * @param editModel		汎用フィルタの編集用データモデル
	 * @param srcArgModel	編集対象の引数定義データモデル、対象が存在しない場合は <tt>null</tt>
	 * @return	常に <tt>true</tt>
	 */
	public boolean initialComponent(GenericFilterEditModel editModel, GenericFilterArgEditModel srcArgModel) {
		// initialize
		_editModel = editModel;
		if (srcArgModel != null) {
			// 編集対象データを格納
			_argModel = srcArgModel;
			_orgValueType = srcArgModel.getValueType();
		}
		else {
			// 編集対象データを新規作成
			_argModel = new GenericFilterArgEditModel(editModel.getMExecDefArgumentCount()+1, ModuleArgType.STR, null, MExecArgString.instance);
			_argModel.setValue(MExecArgString.instance);
			_orgValueType = null;
		}
		
		// 基本クラスの処理
		return super.initialComponent();
	}

	@Override
	protected boolean onInitialComponent() {
		if (!super.onInitialComponent()) {
			return false;
		}
		
		// タイトルへの引数情報の表示
		String argName = FilterArgUtil.formatArgType(_argModel);
		setTitle(argName + " " + _defaultTitle);
		
		// コンポーネントの生成
		_cmbValueType = new JComboBox(new SchemaValueTypeComboBoxModel());
		_cmbValueType.setEditable(false);
		_txtDesc = new JTextField();
		
		// レイアウト
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBorder(CommonResources.DIALOG_CONTENT_BORDER);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth  = 1;
		gbc.gridheight = 1;
		gbc.weightx    = 0;
		gbc.weighty    = 0;
		gbc.gridy      = 0;
		//--- value type
		gbc.gridx  = 0;
		gbc.fill   = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		JLabel lblValueType = new JLabel(RunnerMessages.getInstance().GenericFilterEditDlg_name_ValueType + " : ");
		mainPanel.add(lblValueType, gbc);
		gbc.gridx++;
		gbc.anchor = GridBagConstraints.WEST;
		mainPanel.add(_cmbValueType, gbc);
		//--- description
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.insets = new Insets(5, 0, 0, 0);
		gbc.fill   = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		JLabel lblDesc = new JLabel(CommonMessages.getInstance().ModuleInfoLabel_args_desc + " : ");
		mainPanel.add(lblDesc, gbc);
		gbc.gridx++;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(_txtDesc, gbc);
		
		// add to main panel
		this.getContentPane().add(mainPanel, BorderLayout.CENTER);
		
		// 成功
		return true;
	}

	@Override
	protected boolean postInitialComponent() {
		// Setup Actions
		
		// setup values
		_cmbValueType.setSelectedItem(_argModel.getValueType());
		_txtDesc.setText(_argModel.getDescription());
		
		// Setup Dialog conditions
		this.setResizable(true);
		this.setLocationRelativeTo(null);
		//this.setStoreLocation(false);
		//--- setup minimum size
		Dimension dmMin = getDefaultSize();
		if (dmMin == null) {
			dmMin = new Dimension(200, 300);
		}
		setMinimumSize(dmMin);
		setKeepMinimumSize(true);
		
		// 完了
		return super.postInitialComponent();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public GenericFilterArgEditModel getTargetModel() {
		return _argModel;
	}
	
	public int getArgumentNo() {
		return _argModel.getArgNo();
	}
	
	public ModuleArgType getArgumentType() {
		return _argModel.getType();
	}
	
	public SchemaValueType getValueType() {
		return (SchemaValueType)_cmbValueType.getSelectedItem();
	}
	
	public String getArgumentDescription() {
		return _txtDesc.getText();
	}

	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------

	/**
	 * このダイアログの標準サイズを返す。
	 * @return	ダイアログの標準サイズを返す。標準サイズを指定しない場合は <tt>null</tt> を返す。
	 */
	@Override
	public Dimension getDefaultSize() {
		return DM_MIN_SIZE;
	}

	@Override
	protected void onRestoreConfiguration(ExConfiguration config, String prefix) {
		super.onRestoreConfiguration(config, prefix);
	}

	@Override
	protected void onStoreConfiguration(ExConfiguration config, String prefix) {
		super.onStoreConfiguration(config, prefix);
	}

	@Override
	protected void onWindowOpened(WindowEvent e) {
		super.onWindowOpened(e);
	}

	@Override
	protected boolean doCancelAction() {
		// 何もしない
		return true;
	}

	@Override
	protected boolean doOkAction() {
		// データ型選択の有無をチェック
		SchemaValueType selectedType = (SchemaValueType)_cmbValueType.getSelectedItem();
		if (selectedType == null) {
			Application.showErrorMessage(this, RunnerMessages.getInstance().msgGenericFilterEdit_NoSelectionValueType);
			return false;
		}
		
		// 引数参照のチェック
		if (_orgValueType != null && !_orgValueType.equals(selectedType)) {
			if (_editModel.isPrecedentReferenceObject(_argModel)) {
				// 参照されている引数のデータ型変更の問い合わせ
				// 問合せ
				int ret = JOptionPane.showConfirmDialog(this,
								RunnerMessages.getInstance().confirmReferencedGenericFilterArgWillChange,
								CommonMessages.getInstance().msgboxTitleWarn,
								JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
				if (ret != JOptionPane.OK_OPTION) {
					// user canceled
					return false;
				}
			}
		}
		
		// 変更を反映
		_argModel.setValueType(selectedType);
		_argModel.setDescription(_txtDesc.getText());
		
		// ダイアログを閉じる
		return true;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods (for Setup contents)
	//------------------------------------------------------------
	
	@Override
	protected JButton createApplyButton() {
		// [Apply] ボタンは生成しない
		return null;
	}

	@Override
	protected JButton createCancelButton() {
		JButton btn = super.createCancelButton();
		if (!_editModel.isEditing()) {
			//--- 表示専用の場合は [Close] ボタンに変更
			btn.setText(CommonMessages.getInstance().Button_Close);
		}
		return btn;
	}

	@Override
	protected JButton createOkButton() {
		if (!_editModel.isEditing()) {
			//--- 表示専用の場合は [OK] ボタンは表示しない
			return null;
		}
		else {
			return super.createOkButton();
		}
	}

	//------------------------------------------------------------
	// Action events
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods (for Creation)
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
