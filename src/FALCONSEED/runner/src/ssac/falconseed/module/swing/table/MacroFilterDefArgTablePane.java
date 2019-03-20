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
 * @(#)MacroFilterDefArgTablePane.java	3.1.0	2014/05/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MacroFilterDefArgTablePane.java	2.0.0	2012/10/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing.table;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.text.JTextComponent;

import ssac.aadl.module.ModuleFileManager;
import ssac.falconseed.module.args.IMExecArgParam;
import ssac.falconseed.module.args.MExecArgDirectory;
import ssac.falconseed.module.args.MExecArgPublish;
import ssac.falconseed.module.args.MExecArgString;
import ssac.falconseed.module.args.MExecArgSubscribe;
import ssac.falconseed.module.swing.MacroFilterEditModel;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.view.dialog.FileChooserManager;
import ssac.util.io.VirtualFile;
import ssac.util.swing.Application;

/**
 * マクロフィルタ定義用引数を表示もしくは設定するテーブルペイン。
 * <p>
 * このクラスの利用においては、コンストラクタによるインスタンス生成後、
 * 必ず {@link #initialComponent()} を呼び出すこと。
 * 
 * @version 3.1.0	2014/05/16
 */
public class MacroFilterDefArgTablePane extends AbMExecDefArgTablePane
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;

	static protected final String MIEDIT_FILE_MENU		= "arg.edit.file";
	static protected final String MIEDIT_FILE_CHOOSE	= MIEDIT_FILE_MENU + ".choose";
	static protected final String MIEDIT_FILE_VAR_FILE	= MIEDIT_FILE_MENU + ".file";
	static protected final String MIEDIT_FILE_VAR_DIR	= MIEDIT_FILE_MENU + ".dir";
	//static protected final String MIEDIT_FILE_VAR		= MIEDIT_FILE_MENU + ".var";

	static protected final String MIEDIT_STR_MENU		= "arg.edit.str";
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
	private MacroFilterEditModel		_editModel;
//	/** サブフィルタの編集コンポーネント **/
//	private MacroFilterConstEditPane	_subFiltersEditPane;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MacroFilterDefArgTablePane() {
		super();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
//	public MacroFilterConstEditPane getSubFiltersEditPane() {
//		return _subFiltersEditPane;
//	}
//	
//	public void setEditModel(final MacroFilterConstEditPane newEditPane) {
//		if (_subFiltersEditPane != newEditPane) {
//			_subFiltersEditPane = newEditPane;
//			MacroFilterEditModel m = (newEditPane==null ? null : newEditPane.getEditModel());
//			if (m == null) {
//				setTableModel(null);
//			} else {
//				setTableModel(m.getMExecDefArgTableModel());
//			}
//		}
//	}
	
	public MacroFilterEditModel getEditModel() {
		return _editModel;
	}
	
	public void setEditModel(final MacroFilterEditModel newModel) {
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
	public void onButtonAdd(ActionEvent ae) {
		// 追加は使用しない
	}
	
	@Override
	public void onButtonClear(ActionEvent ae) {
		// クリアは使用しない
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
				if (_editModel.isReferencedValueFromSubFilter(_editModel.getMExecDefArgument(selected))) {
					int ret = JOptionPane.showConfirmDialog(this, RunnerMessages.getInstance().confirmDeleteReferedArgument,
															RunnerMessages.getInstance().confirmDeleteArgumentTitle,
															JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
					if (ret != JOptionPane.YES_OPTION) {
						// user canceled
						return;
					}
				}
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

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

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
					Application.showErrorMessage(RunnerMessages.getInstance().msgMExecDefArgCannotUseDir);
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
		else if (MIEDIT_FILE_VAR_DIR.equals(command)) {
			// 実行時指定(既存の型、もしくはディレクトリに昇格)
			IMExecDefArgTableModel tm = getTableModel();
			if (tm instanceof IModuleArgConfigTableModel) {
				IMExecArgParam paramType = ((IModuleArgConfigTableModel) tm).getArgument(rowIndex).getParameterType();
				if (paramType != MExecArgDirectory.instance) {
					// ディレクトリへ昇格
					tm.setArgumentValue(rowIndex, MExecArgDirectory.instance);
				} else {
					// 既存の型
					tm.setArgumentValue(rowIndex, paramType);
				}
			} else {
				throw new IllegalStateException("getTableModel() returns no IModuleArgConfigTableModel instance : " + (tm==null ? "null" : tm.getClass().getName()));
			}
		}
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
		//--- Directory
		map.put(MIEDIT_FILE_VAR_DIR,
				new EditMenuItemAction(MIEDIT_FILE_VAR_DIR,
						RunnerMessages.getInstance().MExecDefArgTable_EditMenuFileVarDirectory));
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
		//--- Directory
		menu.add(createEditMenuItem(MIEDIT_FILE_VAR_DIR));
		
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
		//--- Directory
		menu.add(createEditMenuItem(MIEDIT_FILE_VAR_DIR));
		
		return menu;
	}

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
