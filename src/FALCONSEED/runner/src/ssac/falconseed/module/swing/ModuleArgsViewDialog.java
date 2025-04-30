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
