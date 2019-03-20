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
 *  Copyright 2007-2016  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)RunnerMenuBar.java	3.3.0	2016/05/31
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerMenuBar.java	3.2.2	2015/10/13 (Bug fixed)
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerMenuBar.java	3.2.0	2015/06/17
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerMenuBar.java	2.1.0	2013/08/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerMenuBar.java	2.0.0	2012/11/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerMenuBar.java	1.22	2012/08/22
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerMenuBar.java	1.20	2012/03/22
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerMenuBar.java	1.10	2011/02/15
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerMenuBar.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.runner.menu;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import ssac.util.Strings;
import ssac.util.Validations;
import ssac.util.swing.JCharsetComboBox;
import ssac.util.swing.menu.AbExMenuBar;
import ssac.util.swing.menu.AbMenuItemAction;
import ssac.util.swing.menu.IMenuHandler;
import ssac.util.swing.menu.MenuItemResource;
import ssac.util.swing.menu.ToolBarButton;
import ssac.util.swing.tree.JTreePopupMenu;

/**
 * モジュールランナー用標準メニューバー
 * 
 * @version 3.3.0
 */
public class RunnerMenuBar extends AbExMenuBar
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private JPopupMenu		_popupEditorTabContextMenu;
	private JPopupMenu		_popupEditorContextMenu;
	private JPopupMenu		_popupHistoryContextMenu;
	private JTreePopupMenu	_popupMExecDefTreeContext;
	private JTreePopupMenu	_popupDataFileTreeContext;
	
	private JToolBar		_mainToolBar;
	
	private ToolBarButton	_btnShowExecArgsDlg;
	private ToolBarButton	_btnShowChartWindow;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public RunnerMenuBar() {
		this(null);
	}
	
	public RunnerMenuBar(final IMenuHandler handler) {
		super(handler);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * モジュール引数設定ダイアログ表示ボタンを取得する。
	 * このボタンはツールバーのみに設定されるメニューアイテムで、
	 * モードレスのモジュール実行引数設定ダイアログが表示されている場合のみ表示される。
	 * @return	ボタン
	 * @since 1.20
	 */
	public ToolBarButton getShowExecArgsDlgButton() {
		return _btnShowExecArgsDlg;
	}
	
	/**
	 * チャートウィンドウ表示ボタンを取得する。
	 * このボタンはツールバーのみに設定されるメニューアイテムで、
	 * モードレスのチャートウィンドウが表示されている場合のみ表示される。
	 * @return	ボタン
	 * @since 2.1.0
	 */
	public ToolBarButton getShowChartWindowButton() {
		return _btnShowChartWindow;
	}

	/**
	 * モジュール実行定義ツリー専用コンテキストメニューを取得する。
	 * @return	ツリーコンポーネント用コンテキストメニュー
	 * @since 1.10
	 */
	public JTreePopupMenu getMExecDefTreeContextMenu() {
		if (_popupMExecDefTreeContext == null) {
			_popupMExecDefTreeContext = createMExecDefTreeContextMenu();
		}
		return _popupMExecDefTreeContext;
	}

	/**
	 * データファイルツリー専用コンテキストメニューを取得する。
	 * @return	ツリーコンポーネント用コンテキストメニュー
	 * @since 1.10
	 */
	public JTreePopupMenu getDataFileTreeContextMenu() {
		if (_popupDataFileTreeContext == null) {
			_popupDataFileTreeContext = createDataFileTreeContextMenu();
		}
		return _popupDataFileTreeContext;
	}
	
	public JPopupMenu getEditorTabContextMenu() {
		if (_popupEditorTabContextMenu == null) {
			_popupEditorTabContextMenu = createEditorTabContextMenu();
		}
		return _popupEditorTabContextMenu;
	}
	
	public JPopupMenu getEditorContextMenu() {
		if (_popupEditorContextMenu == null) {
			_popupEditorContextMenu = createEditorContextMenu();
		}
		return _popupEditorContextMenu;
	}
	public JPopupMenu getHistoryContextMenu() {
		if (_popupHistoryContextMenu == null) {
			_popupHistoryContextMenu = createHistoryContextMenu();
		}
		return _popupHistoryContextMenu;
	}

	/**
	 * メインフレーム用標準ツールバーを取得する。
	 * @return
	 */
	public JToolBar getMainToolBar() {
		if (_mainToolBar == null) {
			_mainToolBar = createMainToolBar();
		}
		return _mainToolBar;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * このメニューバーを初期化する。
	 */
	protected void initMenu() {
		// setup menu bar
		this.add(createFileMenu());
		this.add(createEditMenu());
		this.add(createFindMenu());
		this.add(createFilterMenu());
		this.add(createToolMenu());
		this.add(createHelpMenu());
		this.add(createTreeFileMenu());
		this.add(createInvisibleMenu());
	}

	/**
	 * 指定されたコマンド名に対応する標準メニューリソースにより、
	 * メニューを生成する。
	 * @param command	標準メニューリソースを取得するキーとなるコマンド名
	 * @return	生成されたメニュー
	 * @throws IllegalArgumentException コマンド名が標準メニューリソースに対応していない場合
	 */
	protected JMenu createDefaultMenu(String command) {
		MenuItemResource mr = RunnerMenuResources.getMenuResource(command);
		Validations.validArgument(mr != null);
		return createMenu(mr);
	}

	/**
	 * 指定されたコマンド名に対応する標準メニューリソースにより、
	 * アクションを持つメニュー項目を生成する。
	 * @param command	標準メニューリソースを取得するキーとなるコマンド名
	 * @return	生成されたメニュー項目
	 * @throws IllegalArgumentException コマンド名が標準メニューリソースに対応していない場合
	 */
	protected JMenuItem createDefaultMenuItem(String command) {
		MenuItemResource mr = RunnerMenuResources.getMenuResource(command);
		Validations.validArgument(mr != null);
		return createActionMenuItem(mr);
	}
	
	protected ExCheckMenuItem createDefaultCheckMenuItem(String command) {
		MenuItemResource mr = RunnerMenuResources.getMenuResource(command);
		Validations.validArgument(mr != null);
		AbCheckMenuItemAction action = createCheckMenuAction(mr);
		String cmd = AbMenuItemAction.getCommandKey(action);
		ExCheckMenuItem item = new ExCheckMenuItem(true, false, action);
		if (!Strings.isNullOrEmpty(cmd)) {
			menuActionMap.put(cmd, action);
		}
		return item;
	}

	/**
	 * メニューには表示しないがメニューバーに含めるためのメニューを生成する。
	 * このメニューはメニューバーには表示されない。
	 * @return	メニュー項目を格納するメニュー
	 * @since 1.20
	 */
	protected JMenu createInvisibleMenu() {
		JMenu menu = createDefaultMenu(RunnerMenuResources.ID_HIDE_MENU);

//		//--- [Edit]-[Execution Definition](E)
//		menu.add(createDefaultMenuItem(RunnerMenuResources.ID_HIDE_EDIT_EXECDEF));
		//--- [Show]-[ExecArgsDlg]
		menu.add(createDefaultMenuItem(RunnerMenuResources.ID_HIDE_SHOW_MEDARGSDLG));
		//--- [Show]-[ChartWindow]
		menu.add(createDefaultMenuItem(RunnerMenuResources.ID_HIDE_SHOW_CHARTWINDOW));
		//--- [Filter]-[History Delete]
		menu.add(createDefaultMenuItem(RunnerMenuResources.ID_FILTER_HISTORY_DELETE));
		
		menu.setVisible(false);
		return menu;
	}

	/**
	 * ツリー専用のファイルメニューを生成する。
	 * このメニューはメニューバーには表示されない。
	 * @return	メニュー項目を格納するメニュー
	 */
	protected JMenu createTreeFileMenu() {
		JMenu menu = createDefaultMenu(RunnerMenuResources.ID_TREE_FILE_MENU);
		
		//--- Tree [open]
		menu.add(createDefaultMenuItem(RunnerMenuResources.ID_TREE_FILE_OPEN));
		//--- Tree [open by type]
		JMenu typedOpenMenu = createDefaultMenu(RunnerMenuResources.ID_TREE_FILE_OPEN_TYPED);
		//--- Tree [open by type]-[CSV]
		typedOpenMenu.add(createDefaultMenuItem(RunnerMenuResources.ID_TREE_FILE_OPEN_TYPED_CSV));
		menu.add(typedOpenMenu);

		menu.setVisible(false);
		return menu;
	}

	/**
	 * 標準構成の保存メニュー項目を、ファイルメニューの終端に追加する。
	 * @param menu	追加対象のメニュー
	 * @since 3.2.2
	 */
	protected void appendFileSaveMenu(JMenu menu) {
		//--- [save]
		//menu.add(createDefaultMenuItem(RunnerMenuResources.ID_FILE_SAVE));
		//--- [save as]
		menu.add(createDefaultMenuItem(RunnerMenuResources.ID_FILE_SAVEAS));
		//---
		menu.addSeparator();
	}

	/**
	 * 標準の構成でファイルメニューを生成する。
	 * @return	ファイルメニュー項目を格納するメニュー
	 */
	protected JMenu createFileMenu() {
		JMenu menu = createDefaultMenu(RunnerMenuResources.ID_FILE_MENU);
		
		//--- [new]
		JMenu newMenu = createDefaultMenu(RunnerMenuResources.ID_FILE_NEW);
		//--- [new] - [Folder]
		newMenu.add(createDefaultMenuItem(RunnerMenuResources.ID_FILE_NEW_FOLDER));
		//--- [new] - [Module Execution Definition]
		newMenu.add(createDefaultMenuItem(RunnerMenuResources.ID_FILE_NEW_MODULEEXECDEF));
		//--- [new] - [Generic Filter] (3.2.0)
		newMenu.add(createDefaultMenuItem(RunnerMenuResources.ID_FILE_NEW_GENERICFILTER));
		//--- [new] - [Macro Filter]
		newMenu.add(createDefaultMenuItem(RunnerMenuResources.ID_FILE_NEW_MACROFILTER));
		menu.add(newMenu);
		//--- [open]
		menu.add(createDefaultMenuItem(RunnerMenuResources.ID_FILE_OPEN));
		//--- [open config]
		menu.add(createDefaultMenuItem(RunnerMenuResources.ID_FILE_OPEN_CONFIG_CSV));
		//--- [Reopen]
		JMenu reopenMenu = createDefaultMenu(RunnerMenuResources.ID_FILE_REOPEN);
		//------ [Reopen]-[Default]
		reopenMenu.add(createDefaultMenuItem(RunnerMenuResources.ID_FILE_REOPEN_DEFAULT));
		//------ [Reopen]-[Encodings]
		{
			String[] encodings = JCharsetComboBox.getAvailableCharsetNames();
			if (encodings != null && encodings.length > 0) {
				reopenMenu.addSeparator();
				for (String name : encodings) {
					MenuItemResource mr = new MenuItemResource(name);
					mr.setCommandKey(RunnerMenuResources.ID_FILE_REOPEN_PREFIX + name);
					reopenMenu.add(createActionMenuItem(mr));
				}
			}
		}
		menu.add(reopenMenu);
		//---
		menu.addSeparator();
		//--- [close]
		menu.add(createDefaultMenuItem(RunnerMenuResources.ID_FILE_CLOSE));
		//--- [close all]
		menu.add(createDefaultMenuItem(RunnerMenuResources.ID_FILE_ALL_CLOSE));
		//---
		menu.addSeparator();
		//--- [save] group
		appendFileSaveMenu(menu);
		//--- [copy to]
		//menu.add(createDefaultMenuItem(RunnerMenuResources.ID_FILE_COPYTO));
		//--- [move to]
		menu.add(createDefaultMenuItem(RunnerMenuResources.ID_FILE_MOVETO));
		//--- [rename]
		menu.add(createDefaultMenuItem(RunnerMenuResources.ID_FILE_RENAME));
		//--- [refresh]
		menu.add(createDefaultMenuItem(RunnerMenuResources.ID_FILE_REFRESH));
		//---
		menu.addSeparator();
		//--- [switch user folder]
		JMenu userFolderMenu = createDefaultMenu(RunnerMenuResources.ID_FILE_SELECT_USERROOT);
		//--- [switch user folder] - [Module Execution Definition]
		userFolderMenu.add(createDefaultMenuItem(RunnerMenuResources.ID_FILE_SELECT_USERROOT_MEXECDEF));
		//--- [switch user folder] - [Data File]
		userFolderMenu.add(createDefaultMenuItem(RunnerMenuResources.ID_FILE_SELECT_USERROOT_DATAFILE));
		menu.add(userFolderMenu);
		//---
		menu.addSeparator();
		//--- [import]
		menu.add(createDefaultMenuItem(RunnerMenuResources.ID_FILE_IMPORT));
		//--- [export]
		menu.add(createDefaultMenuItem(RunnerMenuResources.ID_FILE_EXPORT));
		//---
		menu.addSeparator();
		//--- [preference]
		menu.add(createDefaultMenuItem(RunnerMenuResources.ID_FILE_PREFERENCE));
		//---
		menu.addSeparator();
		//--- [quit]
		menu.add(createDefaultMenuItem(RunnerMenuResources.ID_FILE_QUIT));
		
		return menu;
	}

	/**
	 * 標準の構成で編集メニューを生成する。
	 * @return	編集メニュー項目を格納するメニュー
	 */
	protected JMenu createEditMenu() {
		JMenu menu = createDefaultMenu(RunnerMenuResources.ID_EDIT_MENU);

//		//--- [cut]
//		menu.add(createDefaultMenuItem(RunnerMenuResources.ID_EDIT_CUT));
		//--- [copy]
		menu.add(createDefaultMenuItem(RunnerMenuResources.ID_EDIT_COPY));
		//--- [paste]
		menu.add(createDefaultMenuItem(RunnerMenuResources.ID_EDIT_PASTE));
		//--- [delete]
		menu.add(createDefaultMenuItem(RunnerMenuResources.ID_EDIT_DELETE));
		//---
		menu.addSeparator();
		//--- [select all]
		menu.add(createDefaultMenuItem(RunnerMenuResources.ID_EDIT_SELECTALL));
		//--- [jump]
		menu.add(createDefaultMenuItem(RunnerMenuResources.ID_EDIT_JUMP));
//		//---
//		menu.addSeparator();
//		//--- [Execution Definition]
//		menu.add(createDefaultMenuItem(RunnerMenuResources.ID_EDIT_EXECDEF));
		
		return menu;
	}
	
	/**
	 * 標準の構成で検索メニューを生成する。
	 * @return	検索メニュー項目を格納するメニュー
	 */
	protected JMenu createFindMenu() {
		JMenu menu = createDefaultMenu(RunnerMenuResources.ID_FIND_MENU);

		//--- [find]
		menu.add(createDefaultMenuItem(RunnerMenuResources.ID_FIND_FIND));
		//--- [prev]
		menu.add(createDefaultMenuItem(RunnerMenuResources.ID_FIND_PREV));
		//--- [next]
		menu.add(createDefaultMenuItem(RunnerMenuResources.ID_FIND_NEXT));
		
		return menu;
	}
	
	/**
	 * 標準の構成でフィルターメニューを生成する。
	 * @return フィルターメニュー項目を格納するメニュー
	 * @since 1.22
	 */
	protected JMenu createFilterMenu() {
		JMenu menu = createDefaultMenu(RunnerMenuResources.ID_FILTER_MENU);
		
		//--- [run]
		menu.add(createDefaultMenuItem(RunnerMenuResources.ID_FILTER_RUN));
		//---
		menu.addSeparator();
		//--- [run history]
		JMenu histMenu = createDefaultMenu(RunnerMenuResources.ID_FILTER_HISTORY_RUN_MENU);
		{
			//--- [selected]
			histMenu.add(createDefaultMenuItem(RunnerMenuResources.ID_FILTER_HISTORY_RUN_SELECTED));
			//--- [from]
			histMenu.add(createDefaultMenuItem(RunnerMenuResources.ID_FILTER_HISTORY_RUN_FROM));
			//--- [before]
			histMenu.add(createDefaultMenuItem(RunnerMenuResources.ID_FILTER_HISTORY_RUN_BEFORE));
//			//--- [latest]
//			histMenu.add(createDefaultMenuItem(RunnerMenuResources.ID_FILTER_HISTORY_RUN_LATEST));
		}
		menu.add(histMenu);
		//--- [run history as]
		JMenu histAsMenu = createDefaultMenu(RunnerMenuResources.ID_FILTER_HISTORY_RUNAS_MENU);
		{
			//--- [selected]
			histAsMenu.add(createDefaultMenuItem(RunnerMenuResources.ID_FILTER_HISTORY_RUNAS_SELECTED));
			//--- [from]
			histAsMenu.add(createDefaultMenuItem(RunnerMenuResources.ID_FILTER_HISTORY_RUNAS_FROM));
			//--- [before]
			histAsMenu.add(createDefaultMenuItem(RunnerMenuResources.ID_FILTER_HISTORY_RUNAS_BEFORE));
//			//--- [latest]
//			histAsMenu.add(createDefaultMenuItem(RunnerMenuResources.ID_FILTER_HISTORY_RUNAS_LATEST));
		}
		menu.add(histAsMenu);
		//---
		menu.addSeparator();
		//--- [New by history]
		menu.add(createDefaultMenuItem(RunnerMenuResources.ID_FILTER_NEW_BY_HISTORY));
		//---
		menu.addSeparator();
		//--- [record history]
		ExCheckMenuItem mitem = createDefaultCheckMenuItem(RunnerMenuResources.ID_FILTER_RECORD_HISTORY);
		menu.add(mitem);
		//---
		menu.addSeparator();
		//--- [edit filter]
		menu.add(createDefaultMenuItem(RunnerMenuResources.ID_FILTER_EDIT));
		
		return menu;
	}
	
	/**
	 * 標準の構成でツールメニューを生成する。
	 * @return	ツールメニュー項目を格納するメニュー
	 * @since 2.1.0
	 */
	protected JMenu createToolMenu() {
		JMenu menu = createDefaultMenu(RunnerMenuResources.ID_TOOL_MENU);
		
		//--- [Chart]
		JMenu chartMenu = createDefaultMenu(RunnerMenuResources.ID_TOOL_CHART_MENU);
		{
			//--- [Scatter]
			chartMenu.add(createDefaultMenuItem(RunnerMenuResources.ID_TOOL_CHART_SCATTER));
			//--- [Line]
			chartMenu.add(createDefaultMenuItem(RunnerMenuResources.ID_TOOL_CHART_LINE));
		}
		menu.add(chartMenu);
		//---
		menu.addSeparator();
		//--- [Excel 2 CSV]
		menu.add(createDefaultMenuItem(RunnerMenuResources.ID_TOOL_EXCEL2CSV));
		
		return menu;
	}
	
	/**
	 * 標準の構成でヘルプメニューを生成する。
	 * @return	ヘルプメニュー項目を格納するメニュー
	 */
	protected JMenu createHelpMenu() {
		JMenu menu = createDefaultMenu(RunnerMenuResources.ID_HELP_MENU);

		//--- [about]
		menu.add(createDefaultMenuItem(RunnerMenuResources.ID_HELP_ABOUT));
		
		return menu;
	}

	/**
	 * 保存メニューを、エディタタブのコンテキストメニュー終端に追加する。
	 * @param menu	追加対象のポップアップメニュー
	 * @since 3.2.2
	 */
	protected void appendEditorTabContextSaveMenu(JPopupMenu menu) {
		JMenuItem item;
		
		//--- save
		//item = createMenuItem(getMenuAction(RunnerMenuResources.ID_FILE_SAVE));
		//menu.add(item);
		//--- save as
		item = createMenuItem(getMenuAction(RunnerMenuResources.ID_FILE_SAVEAS));
		menu.add(item);
		//---
		menu.addSeparator();
	}

	/**
	 * エディタタブのコンテキストメニューを生成する。
	 * @return	エディタタブ用コンテキストメニュー
	 */
	protected JPopupMenu createEditorTabContextMenu() {
		JMenuItem item;
		JPopupMenu menu = new JPopupMenu();
		
		//--- close
		item = createMenuItem(getMenuAction(RunnerMenuResources.ID_FILE_CLOSE));
		menu.add(item);
		//---
		menu.addSeparator();
		//--- save
		appendEditorTabContextSaveMenu(menu);
		//--- [File]-[Reopen]
		JMenu reopenMenu = createDefaultMenu(RunnerMenuResources.ID_FILE_REOPEN);
		{
			//------ [Reopen]-[Default]
			item = createMenuItem(getMenuAction(RunnerMenuResources.ID_FILE_REOPEN_DEFAULT));
			reopenMenu.add(item);
			//------ [Reopen]- encodings
			String[] encodings = JCharsetComboBox.getAvailableCharsetNames();
			if (encodings != null && encodings.length > 0) {
				reopenMenu.addSeparator();
				for (String name : encodings) {
					String cmd = RunnerMenuResources.ID_FILE_REOPEN_PREFIX + name;
					reopenMenu.add(getMenuAction(cmd));
				}
			}
		}
		menu.add(reopenMenu);
		//---
		menu.addSeparator();
		//--- close all
		item = createMenuItem(getMenuAction(RunnerMenuResources.ID_FILE_ALL_CLOSE));
		menu.add(item);
		
		return menu;
	}

	/**
	 * エディタ用コンテキストメニューを生成する。
	 * @return	エディタ用コンテキストメニュー
	 */
	protected JPopupMenu createEditorContextMenu() {
		JMenuItem item;
		JPopupMenu menu = new JPopupMenu();
		
//		//--- cut
//		item = createMenuItem(getMenuAction(RunnerMenuResources.ID_EDIT_CUT));
//		menu.add(item);
		//--- copy
		item = createMenuItem(getMenuAction(RunnerMenuResources.ID_EDIT_COPY));
		menu.add(item);
		//--- paste
		item = createMenuItem(getMenuAction(RunnerMenuResources.ID_EDIT_PASTE));
		menu.add(item);
		//--- delete
		item = createMenuItem(getMenuAction(RunnerMenuResources.ID_EDIT_DELETE));
		menu.add(item);
		//---
		menu.addSeparator();
		//--- select all
		item = createMenuItem(getMenuAction(RunnerMenuResources.ID_EDIT_SELECTALL));
		menu.add(item);
		//--- jump
		item = createMenuItem(getMenuAction(RunnerMenuResources.ID_EDIT_JUMP));
		menu.add(item);
		
		return menu;
	}

	/**
	 * モジュール実行定義ツリーコンポーネント専用のコンテキストメニューを生成。
	 * @return	ツリーコンポーネント用コンテキストメニュー
	 * @since 1.10
	 */
	protected JTreePopupMenu createMExecDefTreeContextMenu() {
		JMenuItem item;
		JTreePopupMenu menu = new JTreePopupMenu();
		
		//--- [File]-[new](N)
		JMenu newMenu = createDefaultMenu(RunnerMenuResources.ID_FILE_NEW);
		//--- [File]-[new]-[Folder](F)
		item = createMenuItem(getMenuAction(RunnerMenuResources.ID_FILE_NEW_FOLDER));
		newMenu.add(item);
		//--- [File]-[new]-[Module Execution Definition](E)
		item = createMenuItem(getMenuAction(RunnerMenuResources.ID_FILE_NEW_MODULEEXECDEF));
		newMenu.add(item);
		//--- [File]-[new]-[Generic Filter](G) (3.2.0)
		item = createMenuItem(getMenuAction(RunnerMenuResources.ID_FILE_NEW_GENERICFILTER));
		newMenu.add(item);
		//--- [File]-[new]-[Macro Filter](M)
		item = createMenuItem(getMenuAction(RunnerMenuResources.ID_FILE_NEW_MACROFILTER));
		newMenu.add(item);
//		//--- [File]-[new]- Plugins
//		for (int i = 0; i < PluginManager.getPluginCount(); i++) {
//			IComponentManager plugin = PluginManager.getPlugin(i);
//			if (plugin.isAllowCreateNewDocument()) {
//				String cmd = RunnerMenuResources.ID_FILE_NEW_PREFIX + plugin.getID();
//				newMenu.add(getMenuAction(cmd));
//			}
//		}
		menu.add(newMenu);
//		//---
//		menu.addSeparator();
//		//--- [Edit]-[Execution Definition](O)
//		item = createMenuItem(getMenuAction(RunnerMenuResources.ID_HIDE_EDIT_EXECDEF));
//		menu.add(item);
		//---
		menu.addSeparator();
		//--- [Filter]-[Edit Filter](O)
		item = createMenuItem(getMenuAction(RunnerMenuResources.ID_FILTER_EDIT));
		menu.add(item);
		//---
		menu.addSeparator();
		//--- [Edit]-[Copy](C)
		item = createMenuItem(getMenuAction(RunnerMenuResources.ID_EDIT_COPY));
		menu.add(item);
		//--- [Edit]-[Paste](P)
		item = createMenuItem(getMenuAction(RunnerMenuResources.ID_EDIT_PASTE));
		menu.add(item);
		//--- [Edit]-[Delete](L)
		item = createMenuItem(getMenuAction(RunnerMenuResources.ID_EDIT_DELETE));
		menu.add(item);
		//---
		menu.addSeparator();
//		//--- [File]-[CopyTo](Y)
//		item = createMenuItem(getMenuAction(RunnerMenuResources.ID_FILE_COPYTO));
		//--- [File]-[MoveTo](V)
		item = createMenuItem(getMenuAction(RunnerMenuResources.ID_FILE_MOVETO));
		menu.add(item);
		//--- [File]-[Rename](M)
		item = createMenuItem(getMenuAction(RunnerMenuResources.ID_FILE_RENAME));
		menu.add(item);
		//---
		menu.addSeparator();
		//--- [File]-[Refresh](F)
		item = createMenuItem(getMenuAction(RunnerMenuResources.ID_FILE_REFRESH));
		menu.add(item);
//		//---
//		menu.addSeparator();
//		//------ [Run]-[Run](R)
//		item = createMenuItem(getMenuAction(RunnerMenuResources.ID_RUN_RUN));
//		menu.add(item);
		//---
		menu.addSeparator();
		//------ [Filter]-[Run](R)
		item = createMenuItem(getMenuAction(RunnerMenuResources.ID_FILTER_RUN));
		menu.add(item);
		
		return menu;
	}

	/**
	 * データファイルツリーコンポーネント専用のコンテキストメニュー生成。
	 * @return	ツリーコンポーネント用コンテキストメニュー
	 * @since 1.10
	 */
	protected JTreePopupMenu createDataFileTreeContextMenu() {
		JMenuItem item;
		JTreePopupMenu menu = new JTreePopupMenu();
		
		//--- [File]-[new](N)
		JMenu newMenu = createDefaultMenu(RunnerMenuResources.ID_FILE_NEW);
//		//--- [File]-[new]-[Module Execution Definition](E)
//		item = createMenuItem(getMenuAction(RunnerMenuResources.ID_FILE_NEW_MODULEEXECDEF));
//		newMenu.add(item);
		//--- [File]-[new]-[Folder](F)
		item = createMenuItem(getMenuAction(RunnerMenuResources.ID_FILE_NEW_FOLDER));
		newMenu.add(item);
//		//--- [File]-[new]- Plugins
//		for (int i = 0; i < PluginManager.getPluginCount(); i++) {
//			IComponentManager plugin = PluginManager.getPlugin(i);
//			if (plugin.isAllowCreateNewDocument()) {
//				String cmd = RunnerMenuResources.ID_FILE_NEW_PREFIX + plugin.getID();
//				newMenu.add(getMenuAction(cmd));
//			}
//		}
		menu.add(newMenu);
		//---
		menu.addSeparator();
		//--- Tree [open]
		item = createMenuItem(getMenuAction(RunnerMenuResources.ID_TREE_FILE_OPEN));
		menu.add(item);
		//--- Tree [open by type]
		JMenu typedOpenMenu = createDefaultMenu(RunnerMenuResources.ID_TREE_FILE_OPEN_TYPED);
		//--- Tree [open by type]-[CSV]
		item = createMenuItem(getMenuAction(RunnerMenuResources.ID_TREE_FILE_OPEN_TYPED_CSV));
		typedOpenMenu.add(item);
		menu.add(typedOpenMenu);
		//---
		menu.addSeparator();
		//--- [Edit]-[Copy](C)
		item = createMenuItem(getMenuAction(RunnerMenuResources.ID_EDIT_COPY));
		menu.add(item);
		//--- [Edit]-[Paste](P)
		item = createMenuItem(getMenuAction(RunnerMenuResources.ID_EDIT_PASTE));
		menu.add(item);
		//--- [Edit]-[Delete](L)
		item = createMenuItem(getMenuAction(RunnerMenuResources.ID_EDIT_DELETE));
		menu.add(item);
		//---
		menu.addSeparator();
//		//--- [File]-[CopyTo](Y)
//		item = createMenuItem(getMenuAction(RunnerMenuResources.ID_FILE_COPYTO));
		//--- [File]-[MoveTo](V)
		item = createMenuItem(getMenuAction(RunnerMenuResources.ID_FILE_MOVETO));
		menu.add(item);
		//--- [File]-[Rename](M)
		item = createMenuItem(getMenuAction(RunnerMenuResources.ID_FILE_RENAME));
		menu.add(item);
		//---
		menu.addSeparator();
		//--- [File]-[Refresh](F)
		item = createMenuItem(getMenuAction(RunnerMenuResources.ID_FILE_REFRESH));
		menu.add(item);
//		//---
//		menu.addSeparator();
//		//------ [Run]-[Run](R)
//		item = createMenuItem(getMenuAction(RunnerMenuResources.ID_RUN_RUN));
//		menu.add(item);
//		//---
//		menu.addSeparator();
//		//--- [Edit]-[Execution Definition](O)
//		item = createMenuItem(getMenuAction(RunnerMenuResources.ID_EDIT_EXECDEF));
//		menu.add(item);
		
		return menu;
	}

	/**
	 * フィルター実行履歴コンポーネント専用のコンテキストメニュー生成。
	 * @return	コンポーネント用コンテキストメニュー
	 * @since 1.22
	 */
	protected JTreePopupMenu createHistoryContextMenu() {
		JMenuItem item;
		JTreePopupMenu menu = new JTreePopupMenu();
		
		//--- [Run History]
		JMenu histMenu = createDefaultMenu(RunnerMenuResources.ID_FILTER_HISTORY_RUN_MENU);
		{
			//--- [Selected]
			item = createMenuItem(getMenuAction(RunnerMenuResources.ID_FILTER_HISTORY_RUN_SELECTED));
			histMenu.add(item);
			//--- [From]
			item = createMenuItem(getMenuAction(RunnerMenuResources.ID_FILTER_HISTORY_RUN_FROM));
			histMenu.add(item);
			//--- [Before]
			item = createMenuItem(getMenuAction(RunnerMenuResources.ID_FILTER_HISTORY_RUN_BEFORE));
			histMenu.add(item);
			//--- [Latest]
			item = createMenuItem(getMenuAction(RunnerMenuResources.ID_FILTER_HISTORY_RUN_LATEST));
			histMenu.add(item);
		}
		menu.add(histMenu);
		//--- [Run History As]
		JMenu histAsMenu = createDefaultMenu(RunnerMenuResources.ID_FILTER_HISTORY_RUNAS_MENU);
		{
			//--- [Selected]
			item = createMenuItem(getMenuAction(RunnerMenuResources.ID_FILTER_HISTORY_RUNAS_SELECTED));
			histAsMenu.add(item);
			//--- [From]
			item = createMenuItem(getMenuAction(RunnerMenuResources.ID_FILTER_HISTORY_RUNAS_FROM));
			histAsMenu.add(item);
			//--- [Before]
			item = createMenuItem(getMenuAction(RunnerMenuResources.ID_FILTER_HISTORY_RUNAS_BEFORE));
			histAsMenu.add(item);
			//--- [Latest]
			item = createMenuItem(getMenuAction(RunnerMenuResources.ID_FILTER_HISTORY_RUNAS_LATEST));
			histAsMenu.add(item);
		}
		menu.add(histAsMenu);
		//---
		menu.addSeparator();
		//--- [New By History]
		item = createMenuItem(getMenuAction(RunnerMenuResources.ID_FILTER_NEW_BY_HISTORY));
		menu.add(item);
		//---
		menu.addSeparator();
		//--- [Edit]-[Delete](L)
		item = createMenuItem(getMenuAction(RunnerMenuResources.ID_EDIT_DELETE));
		menu.add(item);
		//---
		menu.addSeparator();
		//--- select all
		item = createMenuItem(getMenuAction(RunnerMenuResources.ID_EDIT_SELECTALL));
		menu.add(item);
		
		return menu;
	}

	/**
	 * 標準のツールバーを生成する。
	 * @return	ツールバー
	 */
	protected JToolBar createMainToolBar() {
		JToolBar bar = new JToolBar(JToolBar.HORIZONTAL);
		
		//--- [File]-[switch user folder]-[Module Execution Definition]
		bar.add(new ToolBarButton(getMenuAction(RunnerMenuResources.ID_FILE_SELECT_USERROOT_MEXECDEF)));
		//--- [File]-[switch user folder]-[Data File]
		bar.add(new ToolBarButton(getMenuAction(RunnerMenuResources.ID_FILE_SELECT_USERROOT_DATAFILE)));
		//---
		bar.addSeparator();
		
//		//--- [File]-[new]
//		JButton btnNew = createFileNewToolbarButton();
//		if (btnNew != null) {
//			bar.add(btnNew);
//		}
		//--- [File]-[open]
		bar.add(new ToolBarButton(getMenuAction(RunnerMenuResources.ID_FILE_OPEN)));
		//--- [File]-[open for csv]
		bar.add(new ToolBarButton(getMenuAction(RunnerMenuResources.ID_FILE_OPEN_CONFIG_CSV)));
		//--- [File]-[save]
		//bar.add(new ToolBarButton(getMenuAction(RunnerMenuResources.ID_FILE_SAVE)));
		//---
		bar.addSeparator();
//		//--- [Edit]-[cut]
//		bar.add(new ToolBarButton(getMenuAction(RunnerMenuResources.ID_EDIT_CUT)));
		//--- [Edit]-[copy]
		bar.add(new ToolBarButton(getMenuAction(RunnerMenuResources.ID_EDIT_COPY)));
		//--- [Edit]-[paste]
		bar.add(new ToolBarButton(getMenuAction(RunnerMenuResources.ID_EDIT_PASTE)));
		//---
		bar.addSeparator();
		//--- [Find]-[find]
		bar.add(new ToolBarButton(getMenuAction(RunnerMenuResources.ID_FIND_FIND)));
		//--- [Find]-[prev]
		bar.add(new ToolBarButton(getMenuAction(RunnerMenuResources.ID_FIND_PREV)));
		//--- [Find]-[next]
		bar.add(new ToolBarButton(getMenuAction(RunnerMenuResources.ID_FIND_NEXT)));
//		//---
//		bar.addSeparator();
//		//--- [Run]-[Run]
//		bar.add(new ToolBarButton(getMenuAction(RunnerMenuResources.ID_RUN_RUN)));
		//---
		bar.addSeparator();
		//--- [Filter]-[Run]
		bar.add(new ToolBarButton(getMenuAction(RunnerMenuResources.ID_FILTER_EDIT)));
		//---
		bar.addSeparator();
		//--- [Filter]-[Run]
		bar.add(new ToolBarButton(getMenuAction(RunnerMenuResources.ID_FILTER_RUN)));
		//---
		bar.addSeparator();
		//--- [File]-[Refresh]
		bar.add(new ToolBarButton(getMenuAction(RunnerMenuResources.ID_FILE_REFRESH)));
		
		//--- glue
		bar.add(Box.createGlue());
		
		//--- toolbar view-ChartWindow(特殊なツールバーボタン)
		_btnShowChartWindow = new ToolBarButton(getMenuAction(RunnerMenuResources.ID_HIDE_SHOW_CHARTWINDOW));
		bar.add(_btnShowChartWindow);
		_btnShowChartWindow.setVisible(false);
		
		//--- toolbar view-ExecArgsDlg(特殊なツールバーボタン)
		_btnShowExecArgsDlg = new ToolBarButton(getMenuAction(RunnerMenuResources.ID_HIDE_SHOW_MEDARGSDLG));
		bar.add(_btnShowExecArgsDlg);
		_btnShowExecArgsDlg.setVisible(false);
		
		return bar;
	}

	/**
	 * 指定されたメニューリソースから、このメニューバー専用アクションを
	 * 生成する。
	 * <p><b>注：</b>
	 * <blockquote>
	 * このメソッドで生成されたアクションは、このメニューバーの
	 * メニューアクションマップには登録されない。
	 * </blockquote>
	 * @param mir	アクションに反映するメニューリソース
	 * @return	新しいアクション
	 */
	protected AbCheckMenuItemAction createCheckMenuAction(MenuItemResource mir) {
		return new AbCheckMenuItemAction(mir){
			public void actionPerformed(ActionEvent e) {
				fireMenuActionPerformed(e);
			}
		};
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	static public class ExCheckMenuItem extends JCheckBoxMenuItem implements PropertyChangeListener
	{
		private static final long serialVersionUID = 5261934560208529990L;
		
		private boolean ignoreToolTip = false;
		private boolean ignoreIcon = false;

		//------------------------------------------------------------
		// Constructions
		//------------------------------------------------------------

		public ExCheckMenuItem() {
			super();
		}
		
		public ExCheckMenuItem(Action a) {
			super(a);
		}

		public ExCheckMenuItem(Icon icon) {
			super(icon);
		}

		public ExCheckMenuItem(String text, Icon icon) {
			super(text, icon);
		}

		public ExCheckMenuItem(String text) {
			super(text);
		}
		
		public ExCheckMenuItem(boolean ignoreToolTip, boolean ignoreIcon) {
			super();
			this.ignoreToolTip = ignoreToolTip;
			this.ignoreIcon = ignoreIcon;
		}
		
		public ExCheckMenuItem(boolean ignoreToolTip, boolean ignoreIcon, Action a) {
			super();
			this.ignoreToolTip = ignoreToolTip;
			this.ignoreIcon = ignoreIcon;
			setAction(a);
		}
		
		public ExCheckMenuItem(MenuItemResource res) {
			super();
			setMenuItemResource(res);
		}
		
		public ExCheckMenuItem(boolean ignoreToolTip, boolean ignoreIcon, MenuItemResource res) {
			super();
			this.ignoreToolTip = ignoreToolTip;
			this.ignoreIcon = ignoreIcon;
			setMenuItemResource(res);
		}

		//------------------------------------------------------------
		// Public interfaces
		//------------------------------------------------------------
		
		public boolean isToolTipIgnored() {
			return this.ignoreToolTip;
		}
		
		public boolean isIconIgnored() {
			return this.ignoreIcon;
		}
		
		public void setIgnoreToolTip(boolean ignore) {
			this.ignoreToolTip = ignore;
		}
		
		public void setIgnoreIcon(boolean ignore) {
			this.ignoreIcon = ignore;
		}
		
		public void setMenuItemResource(MenuItemResource res) {
			if (res != null) {
				setMnemonic(res.getMnemonic());
				setText(res.getName());
				setToolTipText(res.getToolTip());
				setIcon(res.getIcon());
				setActionCommand(res.getCommandKey());
				setAccelerator(res.getAccelerator());
			}
			else {
				// clear all
				setMnemonic('\0');
				setText(null);
				setToolTipText(null);
				setIcon(null);
				setActionCommand(null);
				setAccelerator(null);
			}
		}

		public void propertyChange(PropertyChangeEvent evt) {
			Action action = (Action)evt.getSource();
			if (action instanceof AbCheckMenuItemAction && evt.getPropertyName().equals(AbCheckMenuItemAction.SELECTED)) {
				Boolean val = (Boolean)evt.getNewValue();
				setSelected(val==null ? false : val);
			}
		}

		//------------------------------------------------------------
		// Internal methods
		//------------------------------------------------------------

		@Override
		public void setIcon(Icon defaultIcon) {
			super.setIcon(ignoreIcon ? null : defaultIcon);
		}

		@Override
		public void setToolTipText(String text) {
			super.setToolTipText(ignoreToolTip ? null : text);
		}
		
		@Override
		public void setAction(Action a) {
			Action oldValue = getAction();
			if (oldValue == null || !oldValue.equals(a)) {
				if (oldValue != null) {
					oldValue.removePropertyChangeListener(this);
				}
				
				if (a instanceof AbCheckMenuItemAction) {
					AbCheckMenuItemAction ca = (AbCheckMenuItemAction)a;
					setSelected(ca.isSelected());
					ca.addPropertyChangeListener(this);
				}
				
				super.setAction(a);
			}
	    }
	}
	
	static public class CheckToolBarButton extends JToggleButton implements PropertyChangeListener
	{
		private static final long serialVersionUID = 7362196310593291609L;
		
		private boolean ignoreMnemonic = true;
		
		public CheckToolBarButton() {
			super();
			this.setHorizontalTextPosition(JButton.CENTER);
			this.setVerticalTextPosition(JButton.BOTTOM);
		}

		public CheckToolBarButton(Action a) {
			super();
			this.setHorizontalTextPosition(JButton.CENTER);
			this.setVerticalTextPosition(JButton.BOTTOM);
			this.setAction(a);
		}

		public CheckToolBarButton(Icon icon) {
			super(icon);
			this.setHorizontalTextPosition(JButton.CENTER);
			this.setVerticalTextPosition(JButton.BOTTOM);
		}

		public CheckToolBarButton(String text, Icon icon) {
			super(text, icon);
			this.setHorizontalTextPosition(JButton.CENTER);
			this.setVerticalTextPosition(JButton.BOTTOM);
		}

		public CheckToolBarButton(String text) {
			super(text);
			this.setHorizontalTextPosition(JButton.CENTER);
			this.setVerticalTextPosition(JButton.BOTTOM);
		}
		
		public boolean isIgnoreMnemonic() {
			return ignoreMnemonic;
		}
		
		public void setIgnoreMnemonic(boolean ignore) {
			ignoreMnemonic = ignore;
		}
		
		@Override
		public void setAction(Action a) {
			Action oldValue = getAction();
			if (oldValue == null || !oldValue.equals(a)) {
				if (oldValue != null) {
					oldValue.removePropertyChangeListener(this);
				}
				
				if (a instanceof AbCheckMenuItemAction) {
					AbCheckMenuItemAction ca = (AbCheckMenuItemAction)a;
					setSelected(ca.isSelected());
					ca.addPropertyChangeListener(this);
				}
				
				// check icon
				Icon icon = (a != null ? (Icon)a.getValue(Action.SMALL_ICON) : null);
				if (icon != null) {
				    this.putClientProperty("hideActionText", Boolean.TRUE);
				}
				
				super.setAction(a);
			}
	    }

		@Override
		public void setMnemonic(int mnemonic) {
			if (ignoreMnemonic) {
				super.setMnemonic((int)'\0');
			} else {
				super.setMnemonic(mnemonic);
			}
		}

		public void propertyChange(PropertyChangeEvent evt) {
			Action action = (Action)evt.getSource();
			if (action instanceof AbCheckMenuItemAction && evt.getPropertyName().equals(AbCheckMenuItemAction.SELECTED)) {
				Boolean val = (Boolean)evt.getNewValue();
				setSelected(val==null ? false : val);
			}
		}
	}
	
	static public abstract class AbCheckMenuItemAction extends AbMenuItemAction
	{
		private static final long serialVersionUID = 8918233721709856060L;
		
		static public final String SELECTED		= "AbCheckMenuItemAction.Selected";
		
		public AbCheckMenuItemAction() {
			super();
		}

		public AbCheckMenuItemAction(MenuItemResource mir) {
			super(mir);
		}

		public AbCheckMenuItemAction(String name, Icon icon) {
			super(name, icon);
		}

		public AbCheckMenuItemAction(String actionCommand, String name,
				Icon icon, String tooltip, String description, int mnemonic,
				KeyStroke keyStroke) {
			super(actionCommand, name, icon, tooltip, description, mnemonic, keyStroke);
		}

		public AbCheckMenuItemAction(String name) {
			super(name);
		}

		/**
		 * このアクションの選択状態を取得する。
		 * @return	選択されていれば <tt>true</tt>、そうでない場合は <tt>false</tt>
		 */
		public boolean isSelected() {
			Boolean val = (Boolean)getValue(SELECTED);
			return (val==null ? false : val);
		}

		/**
		 * 指定された選択状態を、このアクションに設定する。
		 * @param strValue	ツールチップテキスト
		 */
		public void setSelected(boolean selected) {
			if (selected != isSelected()) {
				if (selected) {
					putValue(SELECTED, true);
				} else {
					putValue(SELECTED, null);
				}
			}
		}
	}
}
