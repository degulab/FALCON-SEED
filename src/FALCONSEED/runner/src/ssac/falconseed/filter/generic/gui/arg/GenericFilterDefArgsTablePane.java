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
 * @(#)GenericFilterDefArgsTablePane.java	3.2.1	2015/07/22
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)GenericFilterDefArgsTablePane.java	3.2.0	2015/06/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui.arg;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.module.ModuleFileManager;
import ssac.falconseed.filter.generic.gui.GenericFilterEditModel;
import ssac.falconseed.module.args.MExecArgPublish;
import ssac.falconseed.module.args.MExecArgString;
import ssac.falconseed.module.args.MExecArgSubscribe;
import ssac.falconseed.module.swing.table.AbMExecDefArgTablePane;
import ssac.falconseed.module.swing.table.IMExecDefArgTableModel;
import ssac.falconseed.module.swing.table.IModuleArgConfigTableModel;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.view.dialog.FileChooserManager;
import ssac.util.io.VirtualFile;
import ssac.util.swing.Application;
import ssac.util.swing.IDialogResult;
import ssac.util.swing.MenuToggleButton;

/**
 * 汎用フィルタ編集において、編集可能なフィルタ定義引数を表示もしくは設定するテーブルペイン。
 * <p>
 * このクラスの利用においては、コンストラクタによるインスタンス生成後、
 * 必ず {@link #initialComponent()} を呼び出すこと。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public class GenericFilterDefArgsTablePane extends AbMExecDefArgTablePane
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;

	static protected final String MIEDIT_FILE_MENU		= "arg.edit.file";
	static protected final String MIEDIT_FILE_CHOOSE	= MIEDIT_FILE_MENU + ".choose";
	static protected final String MIEDIT_FILE_VAR_FILE	= MIEDIT_FILE_MENU + ".file";
	//static protected final String MIEDIT_FILE_VAR_DIR	= MIEDIT_FILE_MENU + ".dir";

	static protected final String MIEDIT_STR_MENU		= "arg.edit.str";
	static protected final String MIEDIT_STR_EDIT		= MIEDIT_STR_MENU + ".edit";
	static protected final String MIEDIT_STR_DIRECT		= MIEDIT_STR_MENU + ".direct";
	static protected final String MIEDIT_STR_VAR		= MIEDIT_STR_MENU + ".var";
	
	static protected final String MIEDIT_PUB_MENU		= "arg.edit.pub";
	static protected final String MIEDIT_PUB_DIRECT		= MIEDIT_PUB_MENU + ".direct";
	static protected final String MIEDIT_PUB_VAR		= MIEDIT_PUB_MENU + ".var";
	
	static protected final String MIEDIT_SUB_MENU		= "arg.edit.sub";
	static protected final String MIEDIT_SUB_DIRECT		= MIEDIT_SUB_MENU + ".direct";
	static protected final String MIEDIT_SUB_VAR		= MIEDIT_SUB_MENU + ".var";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 編集モデルデータ **/
	private GenericFilterEditModel		_editModel;

	/** 計算式で参照可能な文字列引数用編集メニュー **/
	private JPopupMenu	_popupEditReferedString;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public GenericFilterDefArgsTablePane() {
		super();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public GenericFilterEditModel getEditModel() {
		return _editModel;
	}
	
	public void setEditModel(final GenericFilterEditModel newModel) {
		if (_editModel != newModel) {
			_editModel = newModel;
			if (newModel == null) {
				setTableModel(null);
			} else {
				setTableModel(newModel.getMExecDefArgTableModel());
			}
		}
	}
	
	@Override
	public void onButtonClear(ActionEvent ae) {
		// クリアは使用しない
	}

	/**
	 * 文字列引数定義の追加
	 * @since 3.2.1
	 */
	@Override
	public void onButtonAdd(ActionEvent ae) {
		stopTableCellEditing();
		// ダイアログの表示
		GenericFilterDefArgEditDialog dlg;
		Window window = SwingUtilities.getWindowAncestor(this);
		if (window instanceof Dialog) {
			dlg = new GenericFilterDefArgEditDialog((Dialog)window, RunnerMessages.getInstance().GenericFilterDefArgEditDlg_title_new);
		} else {
			dlg = new GenericFilterDefArgEditDialog((Frame)window, RunnerMessages.getInstance().GenericFilterDefArgEditDlg_title_new);
		}
		dlg.initialComponent(_editModel, null);
		dlg.setVisible(true);
		dlg.dispose();
		if (dlg.getDialogResult() != IDialogResult.DialogResult_OK) {
			// user canceled
			return;
		}
		
		// 設定の反映
		_editModel.addMExecDefArgument(dlg.getTargetModel());
		
		// 選択
		int rowIndex = _editModel.getMExecDefArgumentCount() - 1;
		getTableComponent().setRowSelectionInterval(rowIndex, rowIndex);
		getTableComponent().scrollToVisibleCell(rowIndex, 0);
	}

	/**
	 * 文字列引数定義の編集
	 * @param ae	イベントオブジェクト
	 * @since 3.2.1
	 */
	protected void onButtonStrArgEdit(ActionEvent ae) {
		stopTableCellEditing();
		// 編集対象の取得
		int selected = getSelectedRow();
		if (selected < 0) {
			// no selection
			return;
		}
		GenericFilterArgEditModel argModel = _editModel.getMExecDefArgument(selected);
		
		// 編集対象の引数かどうかを判定
		if (!_editModel.isEditableMExecDefArgument(argModel)) {
			// uneditable
			return;
		}
		
		// ダイアログの表示
		GenericFilterDefArgEditDialog dlg;
		Window window = SwingUtilities.getWindowAncestor(this);
		if (window instanceof Dialog) {
			dlg = new GenericFilterDefArgEditDialog((Dialog)window, RunnerMessages.getInstance().GenericFilterDefArgEditDlg_title_edit);
		} else {
			dlg = new GenericFilterDefArgEditDialog((Frame)window, RunnerMessages.getInstance().GenericFilterDefArgEditDlg_title_edit);
		}
		dlg.initialComponent(_editModel, argModel);
		dlg.setVisible(true);
		dlg.dispose();
		if (dlg.getDialogResult() != IDialogResult.DialogResult_OK) {
			// user canceled
			return;
		}
		
		// 設定の反映
		_editModel.getMExecDefArgTableModel().fireTableRowsUpdated(selected, selected);
		
		// 変更の反映、および Verify
		_editModel.notifyPrecedentDataChanged(argModel);
	}

	/**
	 * 引数がサブフィルタから参照されている場合、その参照を解除して引数定義を削除する。
	 */
	@Override
	public void onButtonDelete(ActionEvent ae) {
		stopTableCellEditing();
		int selected = getSelectedRow();
		if (selected >= 0) {
			if (_editModel != null) {
				if (!canDeleteArgument(selected)) {
					//--- cannot delete argument
					return;
				}
				//--- delete argument
				_editModel.removeMExecDefArgument(selected);
			}
			else {
				getTableModel().removeRow(selected);
			}
			int maxidx = getTableModel().getRowCount() - 1;
			if (selected > maxidx)
				selected = maxidx;
			if (selected >= 0)
				getTableComponent().setRowSelectionInterval(selected, selected);
			updateButtons();
		}
	}

	@Override
	public void updateButtons() {
		boolean enableAdd		= false;
		boolean enableEdit		= false;
		boolean enableDelete	= false;
		boolean enableUp		= false;
		boolean enableDown		= false;
		
		if (isEnabled() && _editModel.isEditing()) {
			enableAdd = true;
			int selected = getSelectedRow();
			if (selected >= 0) {
				enableEdit = true;	// 値編集は可能とする
				enableDelete = _editModel.isEditableMExecDefArgument(selected);
				if ((selected - 1) >= 0)
					enableUp = true;
				if ((selected + 1) < getRowCount())
					enableDown = true;
			}
		}

		getAddButton()   .setEnabled(enableAdd);
		getEditButton()  .setEnabled(enableEdit);
		getDeleteButton().setEnabled(enableDelete);
		getUpButton()    .setEnabled(enableUp);
		getDownButton()  .setEnabled(enableDown);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	@Override
	protected JPopupMenu getEditStringPopupMenu() {
		int selected = getSelectedRow();
		if (selected >= 0 && _editModel.isEditableMExecDefArgument(selected)) {
			// 計算式で参照可能な引数が選択されている
			if (_popupEditReferedString == null) {
				_popupEditReferedString = createEditReferedStringPopupMenu();
				_popupEditReferedString.addPopupMenuListener(((MenuToggleButton)getEditButton()).getToggleOffPopupMenuListener());
			}
			return _popupEditReferedString;
		}
		else {
			// 自動生成された文字列引数が選択されている
			return super.getEditStringPopupMenu();
		}
	}

	/**
	 * 引数がサブフィルタから削除される直前に、その引数が削除可能かを判定する。
	 * @param argIndex	引数インデックス
	 * @return	削除可能なら <tt>true</tt>、削除しない場合は <tt>false</tt>
	 */
	protected boolean canDeleteArgument(int argIndex) {
		if (!_editModel.isEditing())
			return false;	// 編集中ではないので、削除不可
		
		if (!_editModel.isEditableMExecDefArgument(argIndex))
			return false;	// 編集可能な引数ではないので、削除不可
		
		// チェック
		if (_editModel.isPrecedentReferenceObject(_editModel.getMExecDefArgument(argIndex))) {
			// 他のデータから参照されている
			int ret = JOptionPane.showConfirmDialog(this, RunnerMessages.getInstance().confirmReferencedGenericFilterArgWillDelete,
					CommonMessages.getInstance().msgboxTitleWarn,
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
			if (ret != JOptionPane.OK_OPTION) {
				// user canceled
				return false;
			}
		}
		
		// 削除を許可
		return true;
	}

	@Override
	protected void onEditMenuItemAction(ActionEvent ae) {
		String command = ae.getActionCommand();
		int rowIndex = getSelectedRow();
		
		if (MIEDIT_FILE_CHOOSE.equals(command)) {
			// ファイルの選択
			File selectedFile = FileChooserManager.chooseArgumentDirOrFile(this,
					FileChooserManager.getInitialDocumentFile(getTableModel().getArgumentValue(rowIndex)));
			if (selectedFile != null) {
				// check, cannot be specified a directory
				if (selectedFile.isDirectory()) {
					Application.showErrorMessage(this, RunnerMessages.getInstance().msgMExecDefArgCannotUseDir);
					return;
				}
				// setup argument for the specified file
				FileChooserManager.setLastChooseDocumentFile(selectedFile);
				VirtualFile file = ModuleFileManager.fromJavaFile(selectedFile).getNormalizedFile().getAbsoluteFile();
				getTableModel().setArgumentValue(rowIndex, file);
			}
		}
		else if (MIEDIT_FILE_VAR_FILE.equals(command)) {
			// 実行時指定(既存の型)
			IMExecDefArgTableModel tm = getTableModel();
			if (tm instanceof IModuleArgConfigTableModel) {
				tm.setArgumentValue(rowIndex, ((IModuleArgConfigTableModel) tm).getArgument(rowIndex).getParameterType());
			} else {
				throw new IllegalStateException("getTableModel() returns no IModuleArgConfigTableModel instance : " + (tm==null ? "null" : tm.getClass().getName()));
			}
		}
//	※入出力はあくまでもファイルなので、ディレクトリ指定はなし
//		else if (MIEDIT_FILE_VAR_DIR.equals(command)) {
//			// 実行時指定(既存の型、もしくはディレクトリに昇格)
//			IMExecDefArgTableModel tm = getTableModel();
//			if (tm instanceof IModuleArgConfigTableModel) {
//				IMExecArgParam paramType = ((IModuleArgConfigTableModel) tm).getArgument(rowIndex).getParameterType();
//				if (paramType != MExecArgDirectory.instance) {
//					// ディレクトリへ昇格
//					tm.setArgumentValue(rowIndex, MExecArgDirectory.instance);
//				} else {
//					// 既存の型
//					tm.setArgumentValue(rowIndex, paramType);
//				}
//			} else {
//				throw new IllegalStateException("getTableModel() returns no IModuleArgConfigTableModel instance : " + (tm==null ? "null" : tm.getClass().getName()));
//			}
//		}
		else if (MIEDIT_STR_DIRECT.equals(command) || MIEDIT_PUB_DIRECT.equals(command) || MIEDIT_SUB_DIRECT.equals(command)) {
			// [STR][PUB][SUB] 文字列の直接編集
			getTableComponent().requestFocusInWindow();
			getTableComponent().editCellAt(rowIndex, getTableModel().valueColumnIndex());
	        Component editorComponent = getTableComponent().getEditorComponent();
	        if (editorComponent != null) {
	        	if (!editorComponent.hasFocus()) {
	        		if (!editorComponent.requestFocusInWindow()) {
	        			editorComponent.requestFocus();
	        		}
	        	}
	        	if (editorComponent instanceof JTextComponent) {
	        		JTextComponent textComponent = (JTextComponent)editorComponent;
	        		textComponent.selectAll();
	        	}
	        }
		}
		else if (MIEDIT_STR_VAR.equals(command)) {
			// 文字列の実行時指定
			getTableModel().setArgumentValue(rowIndex, MExecArgString.instance);
		}
		else if (MIEDIT_PUB_VAR.equals(command)) {
			// [PUB] アドレスの実行時指定
			getTableModel().setArgumentValue(rowIndex, MExecArgPublish.instance);
		}
		else if (MIEDIT_SUB_VAR.equals(command)) {
			// [SUB] アドレスの実行時指定
			getTableModel().setArgumentValue(rowIndex, MExecArgSubscribe.instance);
		}
		else if (MIEDIT_STR_EDIT.equals(command)) {
			// [文字列引数定義の編集]
			onButtonStrArgEdit(ae);
		}
	}

	@Override
	protected Map<String, EditMenuItemAction> createEditMenuActionMap() {
		HashMap<String, EditMenuItemAction> map = new HashMap<String, EditMenuItemAction>();
		
		//--- choose file
		map.put(MIEDIT_FILE_CHOOSE,
				new EditMenuItemAction(MIEDIT_FILE_CHOOSE,
						RunnerMessages.getInstance().MExecDefArgTable_EditMenuFileChoose));
		//--- File
		map.put(MIEDIT_FILE_VAR_FILE,
				new EditMenuItemAction(MIEDIT_FILE_VAR_FILE,
						RunnerMessages.getInstance().MExecDefArgTable_EditMenuFileVarFile));
//		※入出力はあくまでもファイルなので、ディレクトリ指定はなし
//		//--- Directory
//		map.put(MIEDIT_FILE_VAR_DIR,
//				new EditMenuItemAction(MIEDIT_FILE_VAR_DIR,
//						RunnerMessages.getInstance().MExecDefArgTable_EditMenuFileVarDirectory));
		//--- String argument edit
		map.put(MIEDIT_STR_EDIT,
				new EditMenuItemAction(MIEDIT_STR_EDIT,
						RunnerMessages.getInstance().GenericFilterDefArgEditDlg_title_edit + "..."));
		//--- String direct edit
		map.put(MIEDIT_STR_DIRECT,
				new EditMenuItemAction(MIEDIT_STR_DIRECT,
						RunnerMessages.getInstance().MExecDefArgTable_EditMenuStringDirect));
		//--- variable
		map.put(MIEDIT_STR_VAR,
				new EditMenuItemAction(MIEDIT_STR_VAR,
						RunnerMessages.getInstance().MExecDefArgTable_EditMenuStringVar));
		//--- Publish addr direct edit
		map.put(MIEDIT_PUB_DIRECT,
				new EditMenuItemAction(MIEDIT_PUB_DIRECT,
						RunnerMessages.getInstance().MExecDefArgTable_EditMenuMqttAddrDirect));
		//--- Publish addr variable
		map.put(MIEDIT_PUB_VAR,
				new EditMenuItemAction(MIEDIT_PUB_VAR,
						RunnerMessages.getInstance().MExecDefArgTable_EditMenuMqttPubAddrVar));
		//--- Subscribe addr direct edit
		map.put(MIEDIT_SUB_DIRECT,
				new EditMenuItemAction(MIEDIT_SUB_DIRECT,
						RunnerMessages.getInstance().MExecDefArgTable_EditMenuMqttAddrDirect));
		//--- Subscribe addr variable
		map.put(MIEDIT_SUB_VAR,
				new EditMenuItemAction(MIEDIT_SUB_VAR,
						RunnerMessages.getInstance().MExecDefArgTable_EditMenuMqttSubAddrVar));
		
		return map;
	}

	@Override
	protected JPopupMenu createEditFileInPopupMenu() {
		JPopupMenu menu = new JPopupMenu();
		
		//--- choose file
		menu.add(createEditMenuItem(MIEDIT_FILE_CHOOSE));
		//---
		menu.addSeparator();
		//--- File
		menu.add(createEditMenuItem(MIEDIT_FILE_VAR_FILE));
//		※入出力はあくまでもファイルなので、ディレクトリ指定はなし
//		//--- Directory
//		menu.add(createEditMenuItem(MIEDIT_FILE_VAR_DIR));
		
		return menu;
	}

	@Override
	protected JPopupMenu createEditFileOutPopupMenu() {
		JPopupMenu menu = new JPopupMenu();
		
//		// モジュール実行定義の[OUT]引数には、固定の出力先は指定できないものとする。
//		//--- choose file
//		menu.add(createEditMenuItem(MIEDIT_FILE_CHOOSE));
//		//---
//		menu.addSeparator();
		//--- File
		menu.add(createEditMenuItem(MIEDIT_FILE_VAR_FILE));
//		※入出力はあくまでもファイルなので、ディレクトリ指定はなし
//		//--- Directory
//		menu.add(createEditMenuItem(MIEDIT_FILE_VAR_DIR));
		
		return menu;
	}

	/**
	 * 自動追加された文字列引数の編集メニュー。
	 */
	@Override
	protected JPopupMenu createEditStringPopupMenu() {
		JPopupMenu menu = new JPopupMenu();

		//--- direct input
		menu.add(createEditMenuItem(MIEDIT_STR_DIRECT));
		//---
		menu.addSeparator();
		//--- variable
		menu.add(createEditMenuItem(MIEDIT_STR_VAR));
		
		return menu;
	}

	/**
	 * 計算式に使用可能な文字列引数の編集メニュー。
	 */
	protected JPopupMenu createEditReferedStringPopupMenu() {
		JPopupMenu menu = new JPopupMenu();

		//--- edit definition
		menu.add(createEditMenuItem(MIEDIT_STR_EDIT));
		//---
		menu.addSeparator();
		//--- direct input
		menu.add(createEditMenuItem(MIEDIT_STR_DIRECT));
		//---
		menu.addSeparator();
		//--- variable
		menu.add(createEditMenuItem(MIEDIT_STR_VAR));
		
		return menu;
	}

	@Override
	protected JPopupMenu createEditPublishPopupMenu() {
		JPopupMenu menu = new JPopupMenu();
		
		//--- direct input
		menu.add(createEditMenuItem(MIEDIT_PUB_DIRECT));
		//---
		menu.addSeparator();
		//--- variable
		menu.add(createEditMenuItem(MIEDIT_PUB_VAR));
		
		return menu;
	}

	@Override
	protected JPopupMenu createEditSubscribePopupMenu() {
		JPopupMenu menu = new JPopupMenu();
		
		//--- direct input
		menu.add(createEditMenuItem(MIEDIT_SUB_DIRECT));
		//---
		menu.addSeparator();
		//--- variable
		menu.add(createEditMenuItem(MIEDIT_SUB_VAR));
		
		return menu;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
