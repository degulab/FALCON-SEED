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
 * @(#)ModuleArgsEditDialog.java	3.0.0	2014/03/26
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ModuleArgsEditDialog.java	1.22	2012/08/22
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import ssac.falconseed.module.EmptyModuleArgValidationHandler;
import ssac.falconseed.module.IModuleArgConfig;
import ssac.falconseed.module.IModuleArgValidationHandler;
import ssac.falconseed.module.ModuleArgValidator;
import ssac.falconseed.module.ModuleRuntimeData;
import ssac.falconseed.module.RelatedModuleList;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.falconseed.runner.view.RunnerToolTabView;
import ssac.util.swing.Application;

/**
 * モジュール実行時引数の設定ダイアログ
 * 
 * @version 3.0.0	2014/03/26
 * @since 1.22
 */
public class ModuleArgsEditDialog extends AbModuleArgsDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = -6854335365138634779L;


	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * モードレスダイアログとして生成
	 */
	public ModuleArgsEditDialog(Frame owner, boolean argsHistoryEnabled, RelatedModuleList modules) {
		super(owner, RunnerMessages.getInstance().MExecArgsEditDlg_Title, false, true, argsHistoryEnabled, modules);
		setConfiguration(AppSettings.MEXECARG_EDIT_DLG, AppSettings.getInstance().getConfiguration());
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	@Override
	protected void setupMainContents() {
		// create main panel
		JPanel mainPanel = createMainPanel();
			
		// add to main panel
		this.getContentPane().add(mainPanel, BorderLayout.CENTER);
	}
	
	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------

	@Override
	protected boolean doOkAction() {
		// 現在表示している値を優先的にチェック
		if (!getArgsEditPane().verifyItemValues()) {
			String errmsg = getArgsEditPane().getFirstItemError();
			Application.showErrorMessage(this, errmsg);
			return false;
		}
		
		// 全ての値のチェック
		final ModuleArgValidator validator = ModuleArgValidator.getInstance();
		final IModuleArgValidationHandler valhandler = EmptyModuleArgValidationHandler.getInstance();
		int errIndex = -1;
		loopModule : for (int index = 0; index < getModuleList().size(); index++) {
			ModuleRuntimeData data = getModuleList().getData(index);
			for (IModuleArgConfig arg : data) {
				if (!validator.verifyValue(arg, valhandler)) {
					errIndex = index;
					break loopModule;
				}
			}
		}
		
		// エラーがあった場合は、その引数設定を表示
		if (errIndex >= 0) {
			setTargetModule(errIndex);
			getArgsEditPane().verifyItemValues();
			String errmsg = getArgsEditPane().getFirstItemError();
			Application.showErrorMessage(this, errmsg);
			return false;
		}
		
		// 基本クラスの処理(ローカルファイルを閉じる)
		if (!super.doOkAction()) {
			return false;
		}
		
		// 履歴の保存
		storeAllArgsHistories();
		
		// 設定を反映
		return true;
	}

	@Override
	protected JComponent createButtonsPanel() {
		JButton[] buttons = createButtons();
		if (buttons == null || buttons.length < 1) {
			// no buttons
			return null;
		}
		
		int maxHeight = adjustButtonSize(buttons);
		
		JComponent modSelButtons = createModuleSelectButtonPanel();
		
		// Layout
		Box btnBox = Box.createHorizontalBox();
		//--- Console settin
		_chkConsoleShowAtStart = RunnerToolTabView.createConsoleShowAtStartCheckBox();
		_chkConsoleAutoClose   = RunnerToolTabView.createConsoleAutoCloseCheckBox();
		_chkConsoleShowAtStart.setSelected(AppSettings.getInstance().getConsoleShowAtStart());
		_chkConsoleAutoClose  .setSelected(AppSettings.getInstance().getConsoleAutoClose());
		_chkConsoleShowAtStart.setMargin(new Insets(0,0,0,0));
		_chkConsoleAutoClose  .setMargin(new Insets(0,0,0,0));
		Box chkBox = Box.createVerticalBox();
		chkBox.add(_chkConsoleShowAtStart);
		chkBox.add(Box.createVerticalGlue());
		chkBox.add(_chkConsoleAutoClose);
		btnBox.add(chkBox);
		//---
		btnBox.add(Box.createHorizontalGlue());
		if (_modulelist.size() > 1) {
			btnBox.add(modSelButtons);
			btnBox.add(Box.createHorizontalGlue());
		}
		for (JButton btn : buttons) {
			btnBox.add(btn);
			btnBox.add(Box.createHorizontalStrut(5));
		}
		btnBox.add(Box.createRigidArea(new Dimension(0, maxHeight+10)));
		return btnBox;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
