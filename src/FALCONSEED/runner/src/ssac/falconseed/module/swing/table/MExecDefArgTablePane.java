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
 * @(#)MExecDefArgTablePane.java	3.1.0	2014/05/12
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecDefArgTablePane.java	2.0.0	2012/10/18
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecDefArgTablePane.java	1.20	2012/03/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecDefArgTablePane.java	1.13	2012/02/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecDefArgTablePane.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing.table;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPopupMenu;
import javax.swing.text.JTextComponent;

import ssac.aadl.module.ModuleFileManager;
import ssac.falconseed.module.args.MExecArgCsvFile;
import ssac.falconseed.module.args.MExecArgDirectory;
import ssac.falconseed.module.args.MExecArgPublish;
import ssac.falconseed.module.args.MExecArgString;
import ssac.falconseed.module.args.MExecArgSubscribe;
import ssac.falconseed.module.args.MExecArgTempFile;
import ssac.falconseed.module.args.MExecArgTextFile;
import ssac.falconseed.module.args.MExecArgXmlFile;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.view.dialog.FileChooserManager;
import ssac.util.io.VirtualFile;
import ssac.util.swing.Application;

/**
 * モジュール実行定義用引数を表示もしくは設定するテーブルペイン。
 * <p>
 * このクラスの利用においては、コンストラクタによるインスタンス生成後、
 * 必ず {@link #initialComponent()} を呼び出すこと。
 * 
 * @version 3.1.0	2014/05/12
 */
public class MExecDefArgTablePane extends AbMExecDefArgTablePane
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final String MIEDIT_FILE_MENU		= "arg.edit.file";
	static protected final String MIEDIT_FILE_CHOOSE	= MIEDIT_FILE_MENU + ".choose";
	static protected final String MIEDIT_FILE_VAR_DIR	= MIEDIT_FILE_MENU + ".dir";
	static protected final String MIEDIT_FILE_VAR_CSV	= MIEDIT_FILE_MENU + ".csv";
	static protected final String MIEDIT_FILE_VAR_XML	= MIEDIT_FILE_MENU + ".xml";
	static protected final String MIEDIT_FILE_VAR_TEXT	= MIEDIT_FILE_MENU + ".text";
	static protected final String MIEDIT_FILE_VAR_TEMP	= MIEDIT_FILE_MENU + ".temp";

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

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MExecDefArgTablePane() {
		super();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	@Override
	public void onButtonDelete(ActionEvent ae) {
		// 現在選択されている行の値列の内容をクリアする
		stopTableCellEditing();
		int selected = getSelectedRow();
		if (selected >= 0) {
			getTableModel().setArgumentValue(selected, null);
			updateButtons();
		}
	}

	@Override
	public void onButtonAdd(ActionEvent ae) {
		// no entry
	}
	
	@Override
	public void onButtonClear(ActionEvent ae) {
		// no entry
	}

	@Override
	public void onButtonDown(ActionEvent ae) {
		// no entry
	}

	@Override
	public void onButtonUp(ActionEvent ae) {
		// no entry
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
		else if (MIEDIT_FILE_VAR_DIR.equals(command)) {
			// 実行時指定(Directory)
			getTableModel().setArgumentValue(rowIndex, MExecArgDirectory.instance);
		}
		else if (MIEDIT_FILE_VAR_CSV.equals(command)) {
			// 実行時指定(CSV)
			getTableModel().setArgumentValue(rowIndex, MExecArgCsvFile.instance);
		}
		else if (MIEDIT_FILE_VAR_TEXT.equals(command)) {
			// 実行時指定(TXT)
			getTableModel().setArgumentValue(rowIndex, MExecArgTextFile.instance);
		}
		else if (MIEDIT_FILE_VAR_XML.equals(command)) {
			// 実行時指定(XML)
			getTableModel().setArgumentValue(rowIndex, MExecArgXmlFile.instance);
		}
		else if (MIEDIT_FILE_VAR_TEMP.equals(command)) {
			// テンポラリファイル指定
			getTableModel().setArgumentValue(rowIndex, MExecArgTempFile.instance);
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
			// [PUB]実行時指定
			getTableModel().setArgumentValue(rowIndex, MExecArgPublish.instance);
		}
		else if (MIEDIT_SUB_VAR.equals(command)) {
			// [SUB]実行時指定
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
		//--- Directory
		map.put(MIEDIT_FILE_VAR_DIR,
				new EditMenuItemAction(MIEDIT_FILE_VAR_DIR,
						RunnerMessages.getInstance().MExecDefArgTable_EditMenuFileVarDirectory));
		//--- CSV
		map.put(MIEDIT_FILE_VAR_CSV,
				new EditMenuItemAction(MIEDIT_FILE_VAR_CSV,
						RunnerMessages.getInstance().MExecDefArgTable_EditMenuFileVarCsv));
		//--- TEXT
		map.put(MIEDIT_FILE_VAR_TEXT,
				new EditMenuItemAction(MIEDIT_FILE_VAR_TEXT,
						RunnerMessages.getInstance().MExecDefArgTable_EditMenuFileVarText));
		//--- XML
		map.put(MIEDIT_FILE_VAR_XML,
				new EditMenuItemAction(MIEDIT_FILE_VAR_XML,
						RunnerMessages.getInstance().MExecDefArgTable_EditMenuFileVarXml));
		//--- Temporary
		map.put(MIEDIT_FILE_VAR_TEMP,
				new EditMenuItemAction(MIEDIT_FILE_VAR_TEMP,
						RunnerMessages.getInstance().MExecDefArgTable_EditMenuFileVarTemporary));
		//--- String direct edit
		map.put(MIEDIT_STR_DIRECT,
				new EditMenuItemAction(MIEDIT_STR_DIRECT,
						RunnerMessages.getInstance().MExecDefArgTable_EditMenuStringDirect));
		//--- String variable
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
		//--- CSV
		menu.add(createEditMenuItem(MIEDIT_FILE_VAR_CSV));
		//--- TEXT
		menu.add(createEditMenuItem(MIEDIT_FILE_VAR_TEXT));
		//--- XML
		menu.add(createEditMenuItem(MIEDIT_FILE_VAR_XML));
		//---
		menu.addSeparator();
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
		//--- CSV
		menu.add(createEditMenuItem(MIEDIT_FILE_VAR_CSV));
		//--- TEXT
		menu.add(createEditMenuItem(MIEDIT_FILE_VAR_TEXT));
		//--- XML
		menu.add(createEditMenuItem(MIEDIT_FILE_VAR_XML));
		//---
		menu.addSeparator();
		//--- Directory
		menu.add(createEditMenuItem(MIEDIT_FILE_VAR_DIR));
//		// モジュール実行定義の[OUT]引数には、テンポラリファイルは指定できないものとする。
//		//---
//		menu.addSeparator();
//		//--- Temporary
//		menu.add(createEditMenuItem(MIEDIT_FILE_VAR_TEMP));
		
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
