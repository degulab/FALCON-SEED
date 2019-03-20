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
 * @(#)EditorMenuBar.java	1.16	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 * @(#)EditorMenuBar.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 * @(#)EditorMenuBar.java	1.10	2009/01/13
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.view;

import static ssac.util.Validations.validNotNull;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

import ssac.aadl.common.CommonResources;
import ssac.aadl.editor.EditorMessages;
import ssac.aadl.editor.plugin.IComponentManager;
import ssac.aadl.editor.plugin.PluginManager;
import ssac.aadl.editor.view.menu.EditorMenuResources;
import ssac.util.Strings;
import ssac.util.Validations;
import ssac.util.swing.menu.AbMenuItemAction;
import ssac.util.swing.menu.ExMenuItem;
import ssac.util.swing.menu.IMenuHandler;
import ssac.util.swing.menu.JMenus;
import ssac.util.swing.menu.MenuItemResource;
import ssac.util.swing.menu.ToolBarButton;
import ssac.util.swing.tree.JTreePopupMenu;

/**
 * AADLエディタ用標準メニューバー
 * 
 * @version 1.16	2010/09/27
 * 
 * @since 1.10
 */
public class EditorMenuBar extends JMenuBar
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/**
	 * このメニューバーのイベントを処理するハンドラ
	 */
	protected IMenuHandler menuHandler = null;

	/**
	 * コマンド名とメニューアクションとのマップ
	 */
	protected final Map<String,Action> menuActionMap = new HashMap<String,Action>();
	
	private JPopupMenu editorTabContextMenu;
	private JPopupMenu editorContextMenu;
	private JPopupMenu treeHeaderContextMenu;
	private JTreePopupMenu	treeContextMenu;
	private JToolBar   mainToolBar;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public EditorMenuBar() {
		this(null);
	}
	
	public EditorMenuBar(final IMenuHandler handler) {
		super();
		this.menuHandler = handler;
		initMenu();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public JPopupMenu getEditorTabContextMenu() {
		if (editorTabContextMenu == null) {
			editorTabContextMenu = createEditorTabContextMenu();
		}
		return editorTabContextMenu;
	}
	
	public JPopupMenu getEditorContextMenu() {
		if (editorContextMenu == null) {
			editorContextMenu = createEditorContextMenu();
		}
		return editorContextMenu;
	}
	
	public JPopupMenu getTreeHeaderContextMenu() {
		if (treeHeaderContextMenu == null) {
			treeHeaderContextMenu = createTreeHeaderContextMenu();
		}
		return treeHeaderContextMenu;
	}
	
	public JTreePopupMenu getTreeViewContextMenu() {
		if (treeContextMenu == null) {
			treeContextMenu = createTreeContextMenu();
		}
		return treeContextMenu;
	}
	
	public JToolBar getMainToolBar() {
		if (mainToolBar == null) {
			mainToolBar = createMainToolBar();
		}
		return mainToolBar;
	}

	/**
	 * このメニューバーに設定されているメニューハンドラを取得する。
	 * @return	設定されているメニューハンドラのインスタンスを返す。
	 */
	public IMenuHandler getMenuHandler() {
		return menuHandler;
	}

	/**
	 * このメニューバーに新しいメニューハンドラを設定する。
	 * @param handler	新たに設定するメニューハンドラ
	 */
	public void setMenuHandler(IMenuHandler handler) {
		this.menuHandler = handler;
	}

	/**
	 * 定義されているメニューアクションを取得する。
	 * @param command	検索キーとなるコマンド名
	 * @return	コマンド名に対応するメニューアクションを返す。
	 * 			アクションが未定義の場合は <tt>null</tt> を返す。
	 */
	public Action getMenuAction(String command) {
		return menuActionMap.get(command);
	}

	/**
	 * 指定されたアクションを、新たにコマンド名に関連付ける。
	 * 指定したコマンド名にすでに関連付けられたアクションが定義されている場合、
	 * 指定されたアクションで既存のアクションを置き換える。
	 * 
	 * @param command	関連付けるコマンド名
	 * @param action	メニューアクション
	 * @return	コマンド名に対応する既存アクションを返す。
	 * アクションが未定義の場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	<code>action</code> が <tt>null</tt> の場合
	 */
	public Action setMenuAction(String command, Action action) {
		return menuActionMap.put(command, validNotNull(action));
	}

	/**
	 * メニューバーに設定されているメニューから、コマンド文字列に対応するメニュー項目を取得する。
	 * コマンド文字列に対応するメニュー項目がない場合は <tt>null</tt> を返す。
	 * <p>
	 * このメソッドは、メニューバーのメニューを検索し、指定されたコマンド文字列を保持する
	 * メニュー項目のうち、最初に見つかったインスタンスを返す。
	 * 
	 * @param command	検索するキーとなるコマンド文字列
	 * @return	コマンド文字列に対応するメニュー項目を返す。存在しない場合は <tt>null</tt> を返す。
	 */
	public JMenuItem getMenuItemByCommand(String command) {
		if (Strings.isNullOrEmpty(command))
			return null;
		
		return JMenus.getMenuItemByCommand(this, command);
	}

	/**
	 * 指定されたインデックスのメニューの状態を更新する。
	 * <p>
	 * このメソッドは、指定された位置にあるメニュー(<code>JMenu</code>)の全てのメニュー項目に
	 * 対して更新イベントを発生させる。この更新イベントは、メニューバーに登録されているハンドラに
	 * 通知される。
	 * <p>
	 * 基本的に、コマンド文字列が登録されていない項目は除外される。
	 * 
	 * @param menuIndex		メニューバーのメニュー位置を示すインデックス。先頭は 0。
	 * 
	 * @throws ArrayIndexOutOfBoundsException メニューインデックスが範囲外の場合
	 */
	public void updateMenuItem(int menuIndex) {
		JMenu menu = getMenu(menuIndex);
		if (menu != null) {
			fireMenuItemUpdate(menu);
		}
	}

	/**
	 * 指定されたコマンド文字列に対応するメニューの状態を更新する。
	 * <p>
	 * このメソッドは、指定されたコマンド文字列に対応するメニュー項目を検索し、その項目に
	 * 対して更新イベントを発生させる。コマンド文字列に対応するものがメニュー(<code>JMenu</code>)の
	 * 場合は、そのメニューに含まれる全てのメニュー項目に対して更新イベントを発生させる。
	 * この更新イベントは、メニューバーに登録されているハンドラに通知される。
	 * <p>
	 * 基本的に、コマンド文字列が登録されていない項目は除外される。
	 * 
	 * @param command	検索するキーとなるコマンド文字列
	 */
	public void updateMenuItem(String command) {
		if (Strings.isNullOrEmpty(command))
			return;	
		
		JMenuItem item = JMenus.getMenuItemByCommand(this, command);
		if (item != null) {
			fireMenuItemUpdate(item);
		}
	}
	
	/**
	 * 全てのメニュー項目の状態を更新する。
	 * <p>
	 * このメソッドは、このメニューバーに登録されている全てのメニュー項目に対して
	 * 更新イベントを発生させる。この更新イベントは、メニューバーに登録されている
	 * ハンドラに通知される。
	 * <p>
	 * 基本的に、コマンド文字列が登録されていない項目は除外される。
	 */
	public void updateAllMenuItems() {
		int len = getMenuCount();
		for (int i = 0; i < len; i++) {
			JMenu menu = getMenu(i);
			if (menu != null) {
				fireMenuItemUpdate(menu);
			}
		}
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
		this.add(createBuildMenu());
		this.add(createHelpMenu());
	}

	/**
	 * コマンド文字列を格納するメニューアイテムの場合、そのメニューアイテムの
	 * 更新イベント・ハンドラを呼び出す。指定されたメニューアイテムが
	 * メニュー(<code>JMenu</code>)の場合、そのメニューに含まれる全てのメニュー
	 * アイテムについて再帰的に処理する。
	 * 
	 * @param item	更新イベント対象のメニューアイテム
	 */
	protected void fireMenuItemUpdate(JMenuItem item) {
		// call event handler for current item
		String command = item.getActionCommand();
		if (!Strings.isNullOrEmpty(command)) {
			fireMenuUpdatePerformed(command, item);
		}
		
		// call event handler, if item is JMenu
		if (item instanceof JMenu) {
			JMenu menu = (JMenu)item;
			int len = menu.getItemCount();
			for (int i = 0; i < len; i++) {
				JMenuItem childItem = menu.getItem(i);
				if (childItem != null) {
					fireMenuItemUpdate(childItem);
				}
			}
		}
	}

	/**
	 * アクションイベントにより、エディタフレームのメニューアクションを
	 * 実行する。
	 * このメソッドは、基本的に <code>EditorFrame</code> インスタンスの
	 * メニューアクションを呼び出す。
	 * 
	 * @param メニューアクションのイベント
	 */
	protected void fireMenuActionPerformed(ActionEvent e) {
		if (menuHandler != null) {
			menuHandler.menuActionPerformed(e);
		}
	}

	/**
	 * メニュー更新要求により、エディタフレームのメニュー更新アクションを
	 * 実行する。
	 * このメソッドは、基本的に <code>EditorFrame</code> インスタンスの
	 * メニュー更新アクションを呼び出す。
	 * 
	 * @param command	コマンド
	 * @param source	更新対象の <code>JMenuItem</code> インスタンス
	 */
	protected void fireMenuUpdatePerformed(String command, Object source) {
		if (menuHandler != null) {
			menuHandler.menuUpdatePerformed(command, source);
		}
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
	protected Action createMenuAction(MenuItemResource mir) {
		return new AbMenuItemAction(mir){
			public void actionPerformed(ActionEvent e) {
				fireMenuActionPerformed(e);
			}
		};
	}

	/**
	 * 指定されたアクションを持つメニュー項目を生成する。
	 * <p><b>注：</b>
	 * <blockquote>
	 * このメソッドで生成されたアクションは、このメニューバーの
	 * メニューアクションマップには登録されない。
	 * </blockquote>
	 * @param action	メニュー項目に設定するアクション
	 * @return	生成されたメニュー項目
	 */
	protected JMenuItem createMenuItem(Action action) {
		return new ExMenuItem(true, false, action);
	}

	/**
	 * 指定されたメニューリソースから、アクションを持たない
	 * メニュー項目を生成する。
	 * @param mir	メニューリソース
	 * @return	生成されたメニュー項目
	 */
	protected JMenuItem createMenuItem(MenuItemResource mir) {
		return new ExMenuItem(true, false, mir);
	}

	/**
	 * 指定されたアクションでメニュー項目を生成する。
	 * このメソッドは、指定されたアクションがコマンド名を保持している
	 * 場合、このアクションをメニューアクションマップに登録する。
	 * 同名のコマンドがメニューアクションマップに登録されている場合は、
	 * 指定されたアクションに置き換える。
	 * @param action	メニュー項目に設定するアクション
	 * @return	生成されたメニュー項目
	 */
	protected JMenuItem createActionMenuItem(Action action) {
		String cmd = AbMenuItemAction.getCommandKey(action);
		JMenuItem item = new ExMenuItem(true, false, action);
		if (!Strings.isNullOrEmpty(cmd)) {
			menuActionMap.put(cmd, action);
		}
		return item;
	}

	/**
	 * 指定されたメニューリソースから新しいアクションを生成し、
	 * そのアクションでメニュー項目を生成する。
	 * このメソッドは、指定されたメニューリソースがコマンド名を保持している
	 * 場合、生成された新しいアクションをメニューアクションマップに登録する。
	 * 同名のコマンドがメニューアクションマップに登録されている場合は、
	 * このメソッドで生成されたアクションに置き換える。
	 * @param mir	メニューリソース
	 * @return	生成されたメニュー項目
	 */
	protected JMenuItem createActionMenuItem(MenuItemResource mir) {
		Action action = createMenuAction(mir);
		JMenuItem item = createActionMenuItem(action);
		return item;
	}

	/**
	 * 指定されたメニューリソースから、アクションを持たないメニューを
	 * 生成する。
	 * @param mir	メニューリソース
	 * @return	生成されたメニュー
	 */
	protected JMenu createMenu(MenuItemResource mir) {
		JMenu menu = mir.createMenu();
		return menu;
	}

	/**
	 * 指定されたコマンド名に対応する標準メニューリソースにより、
	 * メニューを生成する。
	 * @param command	標準メニューリソースを取得するキーとなるコマンド名
	 * @return	生成されたメニュー
	 * @throws IllegalArgumentException コマンド名が標準メニューリソースに対応していない場合
	 */
	protected JMenu createDefaultEditorMenu(String command) {
		MenuItemResource mr = EditorMenuResources.getMenuResource(command);
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
	protected JMenuItem createDefaultEditorMenuItem(String command) {
		MenuItemResource mr = EditorMenuResources.getMenuResource(command);
		Validations.validArgument(mr != null);
		return createActionMenuItem(mr);
	}

	/**
	 * 標準の構成でファイルメニューを生成する。
	 * @return	ファイルメニュー項目を格納するメニュー
	 */
	protected JMenu createFileMenu() {
		JMenu menu = createDefaultEditorMenu(EditorMenuResources.ID_FILE_MENU);
		
		//--- [new]
		JMenu newMenu = createDefaultEditorMenu(EditorMenuResources.ID_FILE_NEW);
		//--- [new] - [Project]
		newMenu.add(createDefaultEditorMenuItem(EditorMenuResources.ID_FILE_NEW_PROJECT));
		//--- [new] - [Folder]
		newMenu.add(createDefaultEditorMenuItem(EditorMenuResources.ID_FILE_NEW_FOLDER));
		//--- [new] - Plugins
		for (int i = 0; i < PluginManager.getPluginCount(); i++) {
			IComponentManager plugin = PluginManager.getPlugin(i);
			if (plugin.isAllowCreateNewDocument()) {
				String cmd = EditorMenuResources.ID_FILE_NEW_PREFIX + plugin.getID();
				String name = plugin.getName() + "...";
				String tip = String.format(EditorMessages.getInstance().tipFileNew, plugin.getName());
				Icon icon = plugin.getDisplayNewIcon();
				if (icon == null) {
					icon = CommonResources.ICON_NEW_FILE;
				}
				MenuItemResource mr = new MenuItemResource(name, icon);
				mr.setCommandKey(cmd);
				mr.setToolTip(tip);
				newMenu.add(createActionMenuItem(mr));
			}
		}
		menu.add(newMenu);
		//--- [open]
		menu.add(createDefaultEditorMenuItem(EditorMenuResources.ID_FILE_OPEN));
		//--- [Reopen]
		JMenu reopenMenu = createDefaultEditorMenu(EditorMenuResources.ID_FILE_REOPEN);
		//------ [Reopen]-[Default]
		reopenMenu.add(createDefaultEditorMenuItem(EditorMenuResources.ID_FILE_REOPEN_DEFAULT));
		//------ [Reopen]-[Encodings]
		{
			String[] encodings = JEncodingComboBox.getAvailableEncodings();
			if (encodings != null && encodings.length > 0) {
				reopenMenu.addSeparator();
				for (String name : encodings) {
					MenuItemResource mr = new MenuItemResource(name);
					mr.setCommandKey(EditorMenuResources.ID_FILE_REOPEN_PREFIX + name);
					reopenMenu.add(createActionMenuItem(mr));
				}
			}
		}
		menu.add(reopenMenu);
		//---
		menu.addSeparator();
		//--- [close]
		menu.add(createDefaultEditorMenuItem(EditorMenuResources.ID_FILE_CLOSE));
		//--- [close all]
		menu.add(createDefaultEditorMenuItem(EditorMenuResources.ID_FILE_ALL_CLOSE));
		//---
		menu.addSeparator();
		//--- [save]
		menu.add(createDefaultEditorMenuItem(EditorMenuResources.ID_FILE_SAVE));
		//--- [save as]
		menu.add(createDefaultEditorMenuItem(EditorMenuResources.ID_FILE_SAVEAS));
		//---
		menu.addSeparator();
		//--- [copy to]
		//menu.add(createDefaultEditorMenuItem(EditorMenuResources.ID_FILE_COPYTO));
		//--- [move to]
		menu.add(createDefaultEditorMenuItem(EditorMenuResources.ID_FILE_MOVETO));
		//--- [rename]
		menu.add(createDefaultEditorMenuItem(EditorMenuResources.ID_FILE_RENAME));
		//--- [refresh]
		menu.add(createDefaultEditorMenuItem(EditorMenuResources.ID_FILE_REFRESH));
		//--- [Workspace refresh] - invisible
		createDefaultEditorMenuItem(EditorMenuResources.ID_FILE_WS_REFRESH);
		//---
		menu.addSeparator();
		//--- [register to project]
		menu.add(createDefaultEditorMenuItem(EditorMenuResources.ID_FILE_PROJECT_REGISTER));
		//--- [unregister from project]
		menu.add(createDefaultEditorMenuItem(EditorMenuResources.ID_FILE_PROJECT_UNREGISTER));
		//---
		menu.addSeparator();
		//--- [Package]
		JMenu packMenu = createDefaultEditorMenu(EditorMenuResources.ID_FILE_PACKAGE);
		//------ [Package]-[Regist]
		packMenu.add(createDefaultEditorMenuItem(EditorMenuResources.ID_FILE_PACKAGE_REGIST));
		//------ [Package]-[Insert]
		packMenu.add(createDefaultEditorMenuItem(EditorMenuResources.ID_FILE_PACKAGE_REFER));
		//------ [Package]-[Import]
		packMenu.add(createDefaultEditorMenuItem(EditorMenuResources.ID_FILE_PACKAGE_IMPORT));
		menu.add(packMenu);
		//---
		menu.addSeparator();
		//--- [select workspace]
		menu.add(createDefaultEditorMenuItem(EditorMenuResources.ID_FILE_WS_SELECT));
		//---
		menu.addSeparator();
		//--- [preference]
		menu.add(createDefaultEditorMenuItem(EditorMenuResources.ID_FILE_PREFERENCE));
		//---
		menu.addSeparator();
		//--- [quit]
		menu.add(createDefaultEditorMenuItem(EditorMenuResources.ID_FILE_QUIT));
		
		return menu;
	}
	
	/**
	 * 標準の構成で編集メニューを生成する。
	 * @return	編集メニュー項目を格納するメニュー
	 */
	protected JMenu createEditMenu() {
		JMenu menu = createDefaultEditorMenu(EditorMenuResources.ID_EDIT_MENU);

		//--- [undo]
		menu.add(createDefaultEditorMenuItem(EditorMenuResources.ID_EDIT_UNDO));
		//--- [redo]
		menu.add(createDefaultEditorMenuItem(EditorMenuResources.ID_EDIT_REDO));
		//---
		menu.addSeparator();
		//--- [cut]
		menu.add(createDefaultEditorMenuItem(EditorMenuResources.ID_EDIT_CUT));
		//--- [copy]
		menu.add(createDefaultEditorMenuItem(EditorMenuResources.ID_EDIT_COPY));
		//--- [paste]
		menu.add(createDefaultEditorMenuItem(EditorMenuResources.ID_EDIT_PASTE));
		//--- [delete]
		menu.add(createDefaultEditorMenuItem(EditorMenuResources.ID_EDIT_DELETE));
		//---
		menu.addSeparator();
		//--- [select all]
		menu.add(createDefaultEditorMenuItem(EditorMenuResources.ID_EDIT_SELECTALL));
		//--- [jump]
		menu.add(createDefaultEditorMenuItem(EditorMenuResources.ID_EDIT_JUMP));
		
		return menu;
	}
	
	/**
	 * 標準の構成で検索メニューを生成する。
	 * @return	検索メニュー項目を格納するメニュー
	 */
	protected JMenu createFindMenu() {
		JMenu menu = createDefaultEditorMenu(EditorMenuResources.ID_FIND_MENU);

		//--- [find]
		menu.add(createDefaultEditorMenuItem(EditorMenuResources.ID_FIND_FIND));
		//--- [prev]
		menu.add(createDefaultEditorMenuItem(EditorMenuResources.ID_FIND_PREV));
		//--- [next]
		menu.add(createDefaultEditorMenuItem(EditorMenuResources.ID_FIND_NEXT));
		
		return menu;
	}
	
	/**
	 * 標準の構成でビルドメニューを生成する。
	 * @return	ビルドメニュー項目を格納するメニュー
	 */
	protected JMenu createBuildMenu() {
		JMenu menu = createDefaultEditorMenu(EditorMenuResources.ID_BUILD_MENU);

		//--- [compile]
		menu.add(createDefaultEditorMenuItem(EditorMenuResources.ID_BUILD_COMPILE));
		//--- [run]
		menu.add(createDefaultEditorMenuItem(EditorMenuResources.ID_BUILD_RUN));
		//--- [compile & run]
		menu.add(createDefaultEditorMenuItem(EditorMenuResources.ID_BUILD_COMPILE_RUN));
		//---
		menu.addSeparator();
		//--- [run as jar]
		menu.add(createDefaultEditorMenuItem(EditorMenuResources.ID_BUILD_RUNASJAR));
		//---
		menu.addSeparator();
		//--- [compile all in folder]
		menu.add(createDefaultEditorMenuItem(EditorMenuResources.ID_BUILD_COMPILEINDIR));
		//---
		menu.addSeparator();
		//--- [option]
		menu.add(createDefaultEditorMenuItem(EditorMenuResources.ID_BUILD_OPTION));
		
		return menu;
	}
	
	/**
	 * 標準の構成でヘルプメニューを生成する。
	 * @return	ヘルプメニュー項目を格納するメニュー
	 */
	protected JMenu createHelpMenu() {
		JMenu menu = createDefaultEditorMenu(EditorMenuResources.ID_HELP_MENU);

		//--- [about]
		menu.add(createDefaultEditorMenuItem(EditorMenuResources.ID_HELP_ABOUT));
		
		return menu;
	}

	/**
	 * エディタタブのコンテキストメニューを生成する。
	 * @return	エディタタブ用コンテキストメニュー
	 */
	protected JPopupMenu createEditorTabContextMenu() {
		JMenuItem item;
		JPopupMenu menu = new JPopupMenu();
		
		//--- close
		item = createMenuItem(getMenuAction(EditorMenuResources.ID_FILE_CLOSE));
		menu.add(item);
		//---
		menu.addSeparator();
		//--- save
		item = createMenuItem(getMenuAction(EditorMenuResources.ID_FILE_SAVE));
		menu.add(item);
		//--- save as
		item = createMenuItem(getMenuAction(EditorMenuResources.ID_FILE_SAVEAS));
		menu.add(item);
		//---
		menu.addSeparator();
		//--- [File]-[Reopen]
		JMenu reopenMenu = createDefaultEditorMenu(EditorMenuResources.ID_FILE_REOPEN);
		{
			//------ [Reopen]-[Default]
			item = createMenuItem(getMenuAction(EditorMenuResources.ID_FILE_REOPEN_DEFAULT));
			reopenMenu.add(item);
			//------ [Reopen]- encodings
			String[] encodings = JEncodingComboBox.getAvailableEncodings();
			if (encodings != null && encodings.length > 0) {
				reopenMenu.addSeparator();
				for (String name : encodings) {
					String cmd = EditorMenuResources.ID_FILE_REOPEN_PREFIX + name;
					reopenMenu.add(getMenuAction(cmd));
				}
			}
		}
		menu.add(reopenMenu);
		//---
		menu.addSeparator();
		//--- close all
		item = createMenuItem(getMenuAction(EditorMenuResources.ID_FILE_ALL_CLOSE));
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
		
		//--- undo
		item = createMenuItem(getMenuAction(EditorMenuResources.ID_EDIT_UNDO));
		menu.add(item);
		//--- redo
		item = createMenuItem(getMenuAction(EditorMenuResources.ID_EDIT_REDO));
		menu.add(item);
		//---
		menu.addSeparator();
		//--- cut
		item = createMenuItem(getMenuAction(EditorMenuResources.ID_EDIT_CUT));
		menu.add(item);
		//--- copy
		item = createMenuItem(getMenuAction(EditorMenuResources.ID_EDIT_COPY));
		menu.add(item);
		//--- paste
		item = createMenuItem(getMenuAction(EditorMenuResources.ID_EDIT_PASTE));
		menu.add(item);
		//--- delete
		item = createMenuItem(getMenuAction(EditorMenuResources.ID_EDIT_DELETE));
		menu.add(item);
		//---
		menu.addSeparator();
		//--- select all
		item = createMenuItem(getMenuAction(EditorMenuResources.ID_EDIT_SELECTALL));
		menu.add(item);
		//--- jump
		item = createMenuItem(getMenuAction(EditorMenuResources.ID_EDIT_JUMP));
		menu.add(item);
		
		return menu;
	}

	/**
	 * ツリービューのラベル用コンテキストメニューを生成する。
	 * @return	ツリービューラベル用コンテキストメニュー
	 */
	protected JPopupMenu createTreeHeaderContextMenu() {
		JMenuItem item;
		JPopupMenu menu = new JPopupMenu();
		
		//--- [File]-[Workspace refresh]
		item = createMenuItem(getMenuAction(EditorMenuResources.ID_FILE_WS_REFRESH));
		menu.add(item);
		//---
		menu.addSeparator();
		//--- [File]-[select Workspace]
		item = createMenuItem(getMenuAction(EditorMenuResources.ID_FILE_WS_SELECT));
		menu.add(item);
		
		return menu;
	}

	/**
	 * ツリーコンポーネント用コンテキストメニューを生成する。
	 * @return	ツリーコンポーネント用コンテキストメニュー
	 */
	protected JTreePopupMenu createTreeContextMenu() {
		JMenuItem item;
		JTreePopupMenu menu = new JTreePopupMenu();
		
		//--- [File]-[new](N)
		JMenu newMenu = createDefaultEditorMenu(EditorMenuResources.ID_FILE_NEW);
		//--- [File]-[new]-[Project](P)
		item = createMenuItem(getMenuAction(EditorMenuResources.ID_FILE_NEW_PROJECT));
		newMenu.add(item);
		//--- [File]-[new]-[Folder](F)
		item = createMenuItem(getMenuAction(EditorMenuResources.ID_FILE_NEW_FOLDER));
		newMenu.add(item);
		//--- [File]-[new]- Plugins
		for (int i = 0; i < PluginManager.getPluginCount(); i++) {
			IComponentManager plugin = PluginManager.getPlugin(i);
			if (plugin.isAllowCreateNewDocument()) {
				String cmd = EditorMenuResources.ID_FILE_NEW_PREFIX + plugin.getID();
				newMenu.add(getMenuAction(cmd));
			}
		}
		menu.add(newMenu);
		//---
		menu.addSeparator();
		//--- [Edit]-[Copy](C)
		item = createMenuItem(getMenuAction(EditorMenuResources.ID_EDIT_COPY));
		menu.add(item);
		//--- [Edit]-[Paste](P)
		item = createMenuItem(getMenuAction(EditorMenuResources.ID_EDIT_PASTE));
		menu.add(item);
		//--- [Edit]-[Delete](L)
		item = createMenuItem(getMenuAction(EditorMenuResources.ID_EDIT_DELETE));
		menu.add(item);
		//---
		menu.addSeparator();
		//--- [File]-[CopyTo](Y)
		//item = createMenuItem(getMenuAction(EditorMenuResources.ID_FILE_COPYTO));
		//--- [File]-[MoveTo](V)
		item = createMenuItem(getMenuAction(EditorMenuResources.ID_FILE_MOVETO));
		menu.add(item);
		//--- [File]-[Rename](M)
		item = createMenuItem(getMenuAction(EditorMenuResources.ID_FILE_RENAME));
		menu.add(item);
		//---
		menu.addSeparator();
		//--- [File]-[Refresh](F)
		item = createMenuItem(getMenuAction(EditorMenuResources.ID_FILE_REFRESH));
		menu.add(item);
		//---
		menu.addSeparator();
		//--- [File]-[Register to Project](G)
		item = createMenuItem(getMenuAction(EditorMenuResources.ID_FILE_PROJECT_REGISTER));
		menu.add(item);
		//--- [File]-[Unregister from Project](U)
		item = createMenuItem(getMenuAction(EditorMenuResources.ID_FILE_PROJECT_UNREGISTER));
		menu.add(item);
		//---
		menu.addSeparator();
		//--- [Package]
		JMenu packMenu = createDefaultEditorMenu(EditorMenuResources.ID_FILE_PACKAGE);
		//------ [Package]-[Regist]
		item = createMenuItem(getMenuAction(EditorMenuResources.ID_FILE_PACKAGE_REGIST));
		packMenu.add(item);
		//------ [Package]-[Insert]
		item = createMenuItem(getMenuAction(EditorMenuResources.ID_FILE_PACKAGE_REFER));
		packMenu.add(item);
		//------ [Package]-[Import]
		item = createMenuItem(getMenuAction(EditorMenuResources.ID_FILE_PACKAGE_IMPORT));
		packMenu.add(item);
		menu.add(packMenu);
		//---
		menu.addSeparator();
		//--- [Build]-[Option](O)
		item = createMenuItem(getMenuAction(EditorMenuResources.ID_BUILD_OPTION));
		menu.add(item);
		
		return menu;
	}

	/**
	 * エディタ用メインツールバーを生成する。
	 * @return	ツールバー
	 */
	protected JToolBar createMainToolBar() {
		JToolBar bar = new JToolBar(JToolBar.HORIZONTAL);
		
//		//--- [File]-[new]
//		JButton btnNew = createFileNewToolbarButton();
//		if (btnNew != null) {
//			bar.add(btnNew);
//		}
		//--- [File]-[open]
		bar.add(new ToolBarButton(getMenuAction(EditorMenuResources.ID_FILE_OPEN)));
		//--- [File]-[save]
		bar.add(new ToolBarButton(getMenuAction(EditorMenuResources.ID_FILE_SAVE)));
		//---
		bar.addSeparator();
		//--- [Edit]-[undo]
		bar.add(new ToolBarButton(getMenuAction(EditorMenuResources.ID_EDIT_UNDO)));
		//--- [Edit]-[redo]
		bar.add(new ToolBarButton(getMenuAction(EditorMenuResources.ID_EDIT_REDO)));
		//---
		bar.addSeparator();
		//--- [Edit]-[cut]
		bar.add(new ToolBarButton(getMenuAction(EditorMenuResources.ID_EDIT_CUT)));
		//--- [Edit]-[copy]
		bar.add(new ToolBarButton(getMenuAction(EditorMenuResources.ID_EDIT_COPY)));
		//--- [Edit]-[paste]
		bar.add(new ToolBarButton(getMenuAction(EditorMenuResources.ID_EDIT_PASTE)));
		//---
		bar.addSeparator();
		//--- [Find]-[find]
		bar.add(new ToolBarButton(getMenuAction(EditorMenuResources.ID_FIND_FIND)));
		//--- [Find]-[prev]
		bar.add(new ToolBarButton(getMenuAction(EditorMenuResources.ID_FIND_PREV)));
		//--- [Find]-[next]
		bar.add(new ToolBarButton(getMenuAction(EditorMenuResources.ID_FIND_NEXT)));
		//---
		bar.addSeparator();
		//--- [Build]-[compile]
		bar.add(new ToolBarButton(getMenuAction(EditorMenuResources.ID_BUILD_COMPILE)));
		//--- [Build]-[run]
		bar.add(new ToolBarButton(getMenuAction(EditorMenuResources.ID_BUILD_RUN)));
		//--- [Build]-[compile & run]
		bar.add(new ToolBarButton(getMenuAction(EditorMenuResources.ID_BUILD_COMPILE_RUN)));
		//---
		bar.addSeparator();
		//--- [Build]-[option]
		bar.add(new ToolBarButton(getMenuAction(EditorMenuResources.ID_BUILD_OPTION)));
		
		return bar;
	}

/*	
	private JButton createFileNewToolbarButton() {
		JButton btn = null;
		IComponentManager defPlugin = PluginManager.getDefaultPlugin();
		if (defPlugin != null) {
			String cmd = EditorMenuResources.ID_FILE_NEW_PREFIX + defPlugin.getID();
			Action action = getMenuAction(cmd);
			if (action != null) {
				btn = new ToolBarButton(action);
			}
		}
		return btn;
	}
/**/
	
	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
