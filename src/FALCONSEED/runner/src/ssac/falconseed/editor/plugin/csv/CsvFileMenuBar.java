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
 * @(#)CsvFileMenuBar.java	3.2.2	2015/10/13 (Bug fixed)
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvFileMenuBar.java	2.1.0	2013/07/23
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvFileMenuBar.java	2.0.0	2012/11/08
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvFileMenuBar.java	1.10	2011/02/14
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvFileMenuBar.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.editor.plugin.csv;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import ssac.aadl.common.CommonResources;
import ssac.falconseed.runner.menu.RunnerMenuBar;
import ssac.falconseed.runner.menu.RunnerMenuResources;
import ssac.util.Validations;
import ssac.util.swing.menu.IMenuHandler;
import ssac.util.swing.menu.MenuItemResource;

/**
 * CSVファイルビューアー専用メニューバー
 * 
 * @version 3.2.2
 */
public class CsvFileMenuBar extends RunnerMenuBar
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;

	//--- custom [File] menu item IDs
	static public final String ID_FILE_SAVEAS_MENU		= "csvfile.saveas";
	static public final String ID_FILE_SAVEAS_DEFAULT		= "csvfile.saveas.default";
	static public final String ID_FILE_SAVEAS_CONFIG		= "csvfile.saveas.config";
	static public final String ID_FILE_EXPORT_DTALGE		= "csvfile.export.dtalge";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/**
	 * アプリケーション内唯一となる、専用メニューリソースのコマンド名とのマップ
	 */
	static private Map<String,MenuItemResource> mrmap = null;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CsvFileMenuBar() {
		this(null);
	}
	
	public CsvFileMenuBar(final IMenuHandler handler) {
		super(handler);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public boolean hasMenuResource(String command) {
		return getMenuResourceMap().containsKey(command);
	}
	
	static public MenuItemResource getMenuResource(String command) {
		return getMenuResourceMap().get(command);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 標準構成の保存メニュー項目を、ファイルメニューの終端に追加する。
	 * @param menu	追加対象のメニュー
	 * @since 3.2.2
	 */
	@Override
	protected void appendFileSaveMenu(JMenu menu) {
		//--- [save]
		//menu.add(createDefaultMenuItem(RunnerMenuResources.ID_FILE_SAVE));
		//--- [save as] menu
		JMenu saveasMenu = createCsvFileMenu(ID_FILE_SAVEAS_MENU);
		//--- [save as]-[Default]
		saveasMenu.add(createCsvFileMenuItem(ID_FILE_SAVEAS_DEFAULT));
		//--- [save as]-[Config]
		saveasMenu.add(createCsvFileMenuItem(ID_FILE_SAVEAS_CONFIG));
		menu.add(saveasMenu);
		//--- [Export dtalge]
		menu.add(createCsvFileMenuItem(ID_FILE_EXPORT_DTALGE));
		//---
		menu.addSeparator();
	}

	/**
	 * 保存メニューを、エディタタブのコンテキストメニュー終端に追加する。
	 * @param menu	追加対象のポップアップメニュー
	 * @since 3.2.2
	 */
	@Override
	protected void appendEditorTabContextSaveMenu(JPopupMenu menu) {
		//--- save
		//item = createMenuItem(getMenuAction(RunnerMenuResources.ID_FILE_SAVE));
		//menu.add(item);
		//--- save as
		JMenu saveasMenu = createCsvFileMenu(ID_FILE_SAVEAS_MENU);
		{
			saveasMenu.add(createMenuItem(getMenuAction(ID_FILE_SAVEAS_DEFAULT)));
			saveasMenu.add(createMenuItem(getMenuAction(ID_FILE_SAVEAS_CONFIG)));
		}
		menu.add(saveasMenu);
		//---
		menu.addSeparator();
	}

	/**
	 * エディタタブのコンテキストメニューを生成する。
	 * @return	エディタタブ用コンテキストメニュー
	 */
	@Override
	protected JPopupMenu createEditorTabContextMenu() {
		JPopupMenu menu = super.createEditorTabContextMenu();
		JMenuItem item;
		
		//--- chart
		JMenu chartMenu = createDefaultMenu(RunnerMenuResources.ID_TOOL_CHART_MENU);
		{
			//--- [Chart]-[Scatter]
			item = createMenuItem(getMenuAction(RunnerMenuResources.ID_TOOL_CHART_SCATTER));
			chartMenu.add(item);
			//--- [Chart]-[Line]
			item = createMenuItem(getMenuAction(RunnerMenuResources.ID_TOOL_CHART_LINE));
			chartMenu.add(item);
		}
		menu.add(chartMenu);
		
		return menu;
	}

	/**
	 * 指定されたコマンド名に対応するCSVファイルビューアー専用メニューリソースにより、
	 * メニューを生成する。
	 * @param command	メニューリソースを取得するキーとなるコマンド名
	 * @return	生成されたメニュー
	 * @throws IllegalArgumentException コマンド名がCSVファイルビューアー専用メニューリソースに対応していない場合
	 */
	private JMenu createCsvFileMenu(String command) {
		MenuItemResource mr = getMenuResource(command);
		Validations.validArgument(mr != null);
		return createMenu(mr);
	}

	/**
	 * 指定されたコマンド名に対応するCSVファイルビューアー専用メニューリソースにより、
	 * アクションを持つメニュー項目を生成する。
	 * @param command	メニューリソースを取得するキーとなるコマンド名
	 * @return	生成されたメニュー項目
	 * @throws IllegalArgumentException コマンド名がCSVファイルビューアー専用メニューリソースに対応していない場合
	 */
	private JMenuItem createCsvFileMenuItem(String command) {
		MenuItemResource mr = getMenuResource(command);
		Validations.validArgument(mr != null);
		return createActionMenuItem(mr);
	}
	
	static private final Map<String,MenuItemResource> getMenuResourceMap() {
		if (mrmap == null) {
			mrmap = new HashMap<String,MenuItemResource>();
			setupCsvFileMenuResources();
		}
		return mrmap;
	}
	
	static private final void setupCsvFileMenuResources() {
		MenuItemResource mr;
		
		//--- File - save as
		mr = new MenuItemResource(ID_FILE_SAVEAS_MENU,
                /* name        */ CsvFileMessages.getInstance().menuFileSaveAsMenu,
                /* icon        */ CommonResources.ICON_BLANK,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_A,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- File - Save as - default
		mr = new MenuItemResource(ID_FILE_SAVEAS_DEFAULT,
                /* name        */ CsvFileMessages.getInstance().menuFileSaveAsDefault,
                /* icon        */ null,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_D,
                /* accelerator */ MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_S, InputEvent.SHIFT_DOWN_MASK));
		mrmap.put(mr.getCommandKey(), mr);
		//--- File - Save as - config
		mr = new MenuItemResource(ID_FILE_SAVEAS_CONFIG,
                /* name        */ CsvFileMessages.getInstance().menuFileSaveAsConfig,
                /* icon        */ null,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_C,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- File - export dtalge
		mr = new MenuItemResource(ID_FILE_EXPORT_DTALGE,
                /* name        */ CsvFileMessages.getInstance().menuFileExportDtalge,
                /* icon        */ CommonResources.ICON_BLANK,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_U,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
	}
}
