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
 * @(#)SchemaDateTimeFormatEditDialog.java	3.2.1	2015/07/21
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SchemaDateTimeFormatEditDialog.java	3.2.0	2015/06/22
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ssac.aadl.common.CommonResources;
import ssac.aadl.fs.module.schema.type.SchemaDateTimeFormats;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.util.swing.AbBasicDialog;
import ssac.util.swing.Application;

/**
 * 日付時刻書式パターンを編集するためのダイアログクラス。
 * @version 3.2.1
 * @since 3.2.0
 */
public class SchemaDateTimeFormatEditDialog extends AbBasicDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	static private final Dimension DM_MIN_SIZE = new Dimension(320, 240);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 無効時のテキストエリアの背景色 **/
	private Color				_crTextAreaDisabledBack;
	/** 有効時のテキストエリアの背景色 **/
	private Color				_crTextAreaEnabledBack;

	/** フォーマット定義 **/
	private SchemaDateTimeFormats	_dtfCustom;
	/** 標準のパターンを使用することを示すラジオボタン **/
	private JRadioButton		_rdoDefault;
	/** フィルタ定義引数で指定することを示すラジオボタン **/
	private JRadioButton		_rdoUseFilterArg;
	/** 任意のパターンを指定することを示すラジオボタン **/
	private JRadioButton		_rdoCustom;
	/** ラジオボタングループ **/
	private ButtonGroup			_rdoGroup;
	/** 任意のパターンを入力するテキストエディタ **/
	private JTextArea			_txtCustomPatterns;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public SchemaDateTimeFormatEditDialog(Dialog owner) {
		super(owner, RunnerMessages.getInstance().SchemaDateTimeFormatEditDlg_title, true);
		setConfiguration(AppSettings.SCHEMADATETIMEFORMAT_EDIT_DLG, AppSettings.getInstance().getConfiguration());
	}
	
	public SchemaDateTimeFormatEditDialog(Frame owner) {
		super(owner, RunnerMessages.getInstance().SchemaDateTimeFormatEditDlg_title, true);
		setConfiguration(AppSettings.SCHEMADATETIMEFORMAT_EDIT_DLG, AppSettings.getInstance().getConfiguration());
	}
	
	public void initialComponent(final GenericFilterEditModel editModel) {
		if (editModel == null)
			throw new NullPointerException("GenericFilterEditModel object is null.");
		
		// create components
		_rdoUseFilterArg = new JRadioButton(RunnerMessages.getInstance().SchemaDateTimeFormatEditDlg_rdo_useFilterArg);
		_rdoDefault      = new JRadioButton(RunnerMessages.getInstance().SchemaDateTimeFormatEditDlg_rdo_default);
		_rdoCustom       = new JRadioButton(RunnerMessages.getInstance().SchemaDateTimeFormatEditDlg_rdo_custom);
		_rdoGroup = new ButtonGroup();
		_rdoGroup.add(_rdoUseFilterArg);
		_rdoGroup.add(_rdoDefault);
		_rdoGroup.add(_rdoCustom);
		_txtCustomPatterns = new JTextArea();
		_txtCustomPatterns.setWrapStyleWord(true);
		_txtCustomPatterns.setLineWrap(true);
		_txtCustomPatterns.setDisabledTextColor(UIManager.getColor("Label.disabledForeground"));
		_crTextAreaDisabledBack = UIManager.getColor("TextArea.disabledBackgroundnull");
		_crTextAreaEnabledBack  = UIManager.getColor("TextArea.background");
		if (_crTextAreaDisabledBack == null || _crTextAreaDisabledBack.equals(_crTextAreaEnabledBack)) {
			// 無効時背景色が設定されていない、もしくは有効時背景色と同じ場合は、無効時背景色をラベル背景色と同色にする
			_crTextAreaDisabledBack = UIManager.getColor("Label.background");
		} else {
			// 無効時背景色と有効時背景色が異なるなら、そのまま利用
			_crTextAreaEnabledBack = null;
			_crTextAreaDisabledBack = null;
		}
		
		// initialize values
		SchemaDateTimeFormatEditModel dataModel = editModel.getDateTimeFormatEditModel();
		if (dataModel.hasFilterArgumentModel()) {
			_rdoUseFilterArg.setSelected(true);
			setEnabledTextArea(false);
		}
		else if (dataModel.getPatternString() == null) {
			_rdoDefault.setSelected(true);
			setEnabledTextArea(false);
		}
		else {
			_rdoCustom.setSelected(true);
			setEnabledTextArea(true);
		}
		
		// コンポーネントの初期化
		super.initialComponent();
		
		// 設定情報の反映
		restoreConfiguration();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isUseFilterArg() {
		return (_rdoUseFilterArg.isSelected());
	}
	
	public SchemaDateTimeFormats getCustomDateTimeFormats() {
		return _dtfCustom;
	}
	
	@Override
	public void restoreConfiguration() {
		super.restoreConfiguration();
	}

	@Override
	public void storeConfiguration() {
		super.storeConfiguration();
	}

	/**
	 * ダイアログが表示されている場合はダイアログを閉じ、リソースを開放する。
	 * このメソッドでは、{@link java.awt.Window#dispose()} を呼び出す。
	 */
	public void destroy() {
		if (this.isDisplayable()) {
			dialogClose(DialogResult_Cancel);
		}
	}
	
	@Override
	protected boolean doCancelAction() {
		return super.doCancelAction();
	}

	@Override
	protected boolean doOkAction() {
		// check custom formats
		if (_rdoCustom.isSelected()) {
			String strPatterns = _txtCustomPatterns.getText();
			if (strPatterns == null) {
				// error
				Application.showErrorMessage(this, RunnerMessages.getInstance().msgSchemaDateTimeFormatEdit_EmptyFormat);
				return false;
			}
			strPatterns = strPatterns.trim();
			if (strPatterns.isEmpty()) {
				// error
				Application.showErrorMessage(this, RunnerMessages.getInstance().msgSchemaDateTimeFormatEdit_EmptyFormat);
				return false;
			}
			
			try {
				_dtfCustom = new SchemaDateTimeFormats(strPatterns);
			}
			catch (Throwable ex) {
				// error
				Application.showErrorMessage(this, RunnerMessages.getInstance().msgSchemaDateTimeFormatEdit_IllegalFormat);
				return false;
			}
		}
		else if (_rdoUseFilterArg.isSelected()) {
			_dtfCustom = null;
		}
		else {
			_dtfCustom = new SchemaDateTimeFormats();
		}

		return true;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected boolean setEnabledTextArea(boolean toEnable) {
		if (toEnable == _txtCustomPatterns.isEnabled())
			return false;
		//--- modified
		if (toEnable) {
			// 有効化
			_txtCustomPatterns.setEditable(true);
			_txtCustomPatterns.setEnabled(true);
			if (_crTextAreaDisabledBack != null) {
				_txtCustomPatterns.setBackground(_crTextAreaEnabledBack);
			}
		}
		else {
			// 無効化
			_txtCustomPatterns.setEditable(false);
			_txtCustomPatterns.setEnabled(false);
			if (_crTextAreaDisabledBack != null) {
				_txtCustomPatterns.setBackground(_crTextAreaDisabledBack);
			}
		}
		return true;
	}
	
	protected JPanel createMainPanel() {
		JLabel lbl = new JLabel(RunnerMessages.getInstance().GenericFilterEditDlg_name_DateTimeFormat + " : ");
		
		JScrollPane scroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setViewportView(_txtCustomPatterns);

		JPanel pnl = new JPanel(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth  = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy = 0;
		//--- radio buttons
		pnl.add(_rdoUseFilterArg, gbc);
		gbc.gridy++;
		pnl.add(_rdoDefault, gbc);
		gbc.gridy++;
		pnl.add(_rdoCustom, gbc);
		gbc.gridy++;
		//--- label
		gbc.insets = new Insets(5,0,0,0);
		pnl.add(lbl, gbc);
		gbc.gridy++;
		//--- text area
		gbc.insets = new Insets(0,0,0,0);
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		pnl.add(scroll, gbc);
		
		return pnl;
	}

	@Override
	protected void setupMainContents() {
		// layout main panel
		JPanel pnlMain = createMainPanel();
		pnlMain.setBorder(CommonResources.DIALOG_CONTENT_BORDER);
		
		// set to main content panel
		getContentPane().add(pnlMain, BorderLayout.CENTER);
	}

	@Override
	protected Dimension getDefaultSize() {
		return DM_MIN_SIZE;
	}

	@Override
	protected JButton createApplyButton() {
		// no apply button
		return null;
	}

	@Override
	protected void setupDialogConditions() {
		super.setupDialogConditions();
		
		this.setResizable(true);
		//this.setStoreLocation(false);
		//--- setup minimum size
		Dimension dmMin = getDefaultSize();
		if (dmMin == null) {
			dmMin = new Dimension(200, 300);
		}
		setMinimumSize(dmMin);
		setKeepMinimumSize(true);
	}

	@Override
	protected void setupActions() {
		super.setupActions();
		
		// radio button
		ChangeListener rdoListener = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {
				onRadioButtonSelectionChanged(ce);
			}
		};
		_rdoUseFilterArg.addChangeListener(rdoListener);
		_rdoDefault.addChangeListener(rdoListener);
		_rdoCustom.addChangeListener(rdoListener);
	}
	
	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------

	@Override
	protected void initDialog() {
		super.initDialog();
	}
	
	protected void onRadioButtonSelectionChanged(ChangeEvent ce) {
		setEnabledTextArea(_rdoCustom.isSelected());
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
