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
 *  Copyright 2007-2010  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)ModuleArgsViewDialog.java	1.22	2012/08/22
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

import java.awt.Dialog;
import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JMenuItem;

import ssac.aadl.common.CommonMessages;
import ssac.falconseed.module.RelatedModuleList;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.util.swing.menu.ExMenuItem;
import ssac.util.swing.tree.JTreePopupMenu;

/**
 * モジュール実行時引数表示ダイアログ
 * 
 * @version 1.22	2012/08/22
 * @since 1.22
 */
public class ModuleArgsViewDialog extends AbModuleArgsDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ModuleArgsViewDialog(Frame owner, RelatedModuleList modules, boolean modal) {
		super(owner, RunnerMessages.getInstance().MExecArgsViewDlg_Title, modal, false, false, modules);
		setConfiguration(AppSettings.MEXECARG_VIEW_DLG, AppSettings.getInstance().getConfiguration());
	}
	
	public ModuleArgsViewDialog(Dialog owner, RelatedModuleList modules, boolean modal) {
		super(owner, RunnerMessages.getInstance().MExecArgsViewDlg_Title, modal, false, false, modules);
		setConfiguration(AppSettings.MEXECARG_VIEW_DLG, AppSettings.getInstance().getConfiguration());
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	@Override
	protected JButton createCancelButton() {
		JButton btn = super.createCancelButton();
		btn.setText(CommonMessages.getInstance().Button_Close);
		return btn;
	}

	@Override
	protected JButton createOkButton() {
		// OKボタンは表示しない
		return null;
	}

	@Override
	protected void createTreeContextMenuActions() {
		super.createTreeContextMenuActions();
		
		// copy と refresh 以外は、使用禁止
		_treeActionOpen         = null;
		_treeActionTypedOpen    = null;
		_treeActionTypedOpenCsv = null;
	}

	@Override
	protected JTreePopupMenu createTreeContextMenu() {
		// copy と refresh 以外は、使用禁止

		// create Menu component
		JMenuItem item;
		JTreePopupMenu menu = new JTreePopupMenu();
		//--- copy
		item = new ExMenuItem(true, false, _treeActionCopy);
		menu.add(item);
		//---
		menu.addSeparator();
		//--- refresh
		item = new ExMenuItem(true, false, _treeActionRefresh);
		menu.add(item);
		
		return menu;
	}
	
	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
