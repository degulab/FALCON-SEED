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
 * @(#)MExecArgsEditDialog.java	3.0.0	2014/03/26
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecArgsEditDialog.java	1.20	2012/03/23
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecArgsEditDialog.java	1.10	2011/02/14
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecArgsEditDialog.java	1.00	2010/12/20
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
import javax.swing.JSplitPane;

import ssac.falconseed.data.swing.tree.DataFileTree;
import ssac.falconseed.module.setting.MExecDefHistory;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.falconseed.runner.view.RunnerToolTabView;
import ssac.util.swing.Application;

/**
 * モジュール実行時引数の設定ダイアログ
 * 
 * @version 3.0.0	2014/03/26
 */
public class MExecArgsEditDialog extends AbMExecArgsDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = -7987847298571574292L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private JSplitPane				_cSplit;
	
	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * モードレスダイアログとして生成
	 */
	public MExecArgsEditDialog(Frame owner, MExecArgsModel argsmodel, MExecDefHistory argshistory) {
		super(owner, String.format(RunnerMessages.getInstance().MExecArgsEditDlg_Title, argsmodel.getSettings().getName()), false, argsmodel, argshistory);
		setConfiguration(AppSettings.MEXECARG_EDIT_DLG, AppSettings.getInstance().getConfiguration());
	}

	/**
	 * モーダルダイアログとして生成（古い実装）
	 */
	public MExecArgsEditDialog(Frame owner, DataFileTree dataFileTree, MExecArgsModel argsmodel, MExecDefHistory argshistory)
	{
		super(owner, String.format(RunnerMessages.getInstance().MExecArgsEditDlg_Title, argsmodel.getSettings().getName()), true, argsmodel, argshistory);
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

		// setup main content
		_cSplit = null;
			
		// add to main panel
		this.getContentPane().add(mainPanel, BorderLayout.CENTER);
	}
	
	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------

	@Override
	public void restoreConfiguration() {
		super.restoreConfiguration();
		
		// restore divider location
		if (_cSplit != null) {
			int dl = getConfiguration().getDividerLocation(getConfigurationPrefix());
			if (dl > 0) {
				_cSplit.setDividerLocation(dl);
			}
		}
	}

	@Override
	public void storeConfiguration() {
		super.storeConfiguration();
		
		// store current divider location
		if (_cSplit != null) {
			int dl = _cSplit.getDividerLocation();
			getConfiguration().setDividerLocation(getConfigurationPrefix(), dl);
		}
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	protected boolean doOkAction() {
		// 値のチェック
		if (!getArgsEditPane().verifyItemValues()) {
			String errmsg = getArgsEditPane().getFirstItemError();
			Application.showErrorMessage(this, errmsg);
			return false;
		}
		
		// 基本クラスの処理(ローカルファイルを閉じる)
		if (!super.doOkAction()) {
			return false;
		}
		
		// 履歴の保存
		storeArgsHistory();
		
		// 設定を反映
		return true;
	}

	/**
	 * ダイアログ下部のボタンパネルを生成する。
	 * これは編集時専用のパネルであり、コンソールの表示設定も含む。
	 * @since 3.0.0
	 */
	@Override
	protected JComponent createButtonsPanel() {
		JButton[] buttons = createButtons();
		if (buttons == null || buttons.length < 1) {
			// no buttons
			return null;
		}
		
		int maxHeight = adjustButtonSize(buttons);
		
		// Layout
		Box btnBox = Box.createHorizontalBox();
		//--- Console setting
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
