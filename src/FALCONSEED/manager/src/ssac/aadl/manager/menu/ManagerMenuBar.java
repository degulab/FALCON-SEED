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
 *  Copyright 2007-2009  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)EditorMenuBar.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.manager.menu;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

import ssac.util.Validations;
import ssac.util.swing.menu.AbExMenuBar;
import ssac.util.swing.menu.IMenuHandler;
import ssac.util.swing.menu.MenuItemResource;
import ssac.util.swing.menu.ToolBarButton;
import ssac.util.swing.tree.JTreePopupMenu;

/**
 * モジュールマネージャ用標準メニューバー
 * 
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public class ManagerMenuBar extends AbExMenuBar
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private JTreePopupMenu	_popupTreeContext;
	private JPopupMenu		_popupTreeHeaderContext;
	
	private JToolBar		_mainToolBar;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ManagerMenuBar() {
		this(null);
	}
	
	public ManagerMenuBar(final IMenuHandler handler) {
		super(handler);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * ツリービュー用コンテキストメニューを取得する。
	 */
	public JTreePopupMenu getTreeContextMenu() {
		if (_popupTreeContext == null) {
			_popupTreeContext = createTreeContextMenu();
		}
		return _popupTreeContext;
	}
	
	public JPopupMenu getTreeHeaderContextMenu() {
		if (_popupTreeHeaderContext == null) {
			_popupTreeHeaderContext = createTreeHeaderContextMenu();
		}
		return _popupTreeHeaderContext;
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
		this.add(createHelpMenu());
	}

	/**
	 * 指定されたコマンド名に対応する標準メニューリソースにより、
	 * メニューを生成する。
	 * @param command	標準メニューリソースを取得するキーとなるコマンド名
	 * @return	生成されたメニュー
	 * @throws IllegalArgumentException コマンド名が標準メニューリソースに対応していない場合
	 */
	protected JMenu createDefaultManagerMenu(String command) {
		MenuItemResource mr = ManagerMenuResources.getMenuResource(command);
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
	protected JMenuItem createDefaultManagerMenuItem(String command) {
		MenuItemResource mr = ManagerMenuResources.getMenuResource(command);
		Validations.validArgument(mr != null);
		return createActionMenuItem(mr);
	}

	/**
	 * 標準の構成でファイルメニューを生成する。
	 * @return	ファイルメニュー項目を格納するメニュー
	 */
	protected JMenu createFileMenu() {
		JMenu menu = createDefaultManagerMenu(ManagerMenuResources.ID_FILE_MENU);
		
		//--- [new dir]
		menu.add(createDefaultManagerMenuItem(ManagerMenuResources.ID_FILE_NEW_DIR));
		//---
		menu.addSeparator();
		//--- [Move to]
		menu.add(createDefaultManagerMenuItem(ManagerMenuResources.ID_FILE_MOVETO));
		//--- [Rename]
		menu.add(createDefaultManagerMenuItem(ManagerMenuResources.ID_FILE_RENAME));
		//--- [Refresh]
		menu.add(createDefaultManagerMenuItem(ManagerMenuResources.ID_FILE_REFRESH));
		//--- [PackageBase refresh] - invisible
		createDefaultManagerMenuItem(ManagerMenuResources.ID_FILE_WS_REFRESH);
		//---
		menu.addSeparator();
		//--- [select PackageBase]
		menu.add(createDefaultManagerMenuItem(ManagerMenuResources.ID_FILE_WS_SELECT));
		//---
		menu.addSeparator();
		//--- [quit]
		menu.add(createDefaultManagerMenuItem(ManagerMenuResources.ID_FILE_QUIT));
		
		return menu;
	}

	/**
	 * 標準の構成で編集メニューを生成する。
	 * @return	編集メニュー項目を格納するメニュー
	 */
	protected JMenu createEditMenu() {
		JMenu menu = createDefaultManagerMenu(ManagerMenuResources.ID_EDIT_MENU);
		
		//--- [Copy]
		menu.add(createDefaultManagerMenuItem(ManagerMenuResources.ID_EDIT_COPY));
		//--- [Paste]
		menu.add(createDefaultManagerMenuItem(ManagerMenuResources.ID_EDIT_PASTE));
		//--- [Delete]
		menu.add(createDefaultManagerMenuItem(ManagerMenuResources.ID_EDIT_DELETE));
		
		return menu;
	}
	
	/**
	 * 標準の構成でヘルプメニューを生成する。
	 * @return	ヘルプメニュー項目を格納するメニュー
	 */
	protected JMenu createHelpMenu() {
		JMenu menu = createDefaultManagerMenu(ManagerMenuResources.ID_HELP_MENU);

		//--- [about]
		menu.add(createDefaultManagerMenuItem(ManagerMenuResources.ID_HELP_ABOUT));
		
		return menu;
	}

	/**
	 * ツリービューのラベル用コンテキストメニューを生成する。
	 * @return	ツリービューラベル用コンテキストメニュー
	 */
	protected JPopupMenu createTreeHeaderContextMenu() {
		JMenuItem item;
		JPopupMenu menu = new JPopupMenu();
		
		//--- [File]-[PackageBase refresh]
		item = createMenuItem(getMenuAction(ManagerMenuResources.ID_FILE_WS_REFRESH));
		menu.add(item);
		//---
		menu.addSeparator();
		//--- [File]-[select PackageBase]
		item = createMenuItem(getMenuAction(ManagerMenuResources.ID_FILE_WS_SELECT));
		menu.add(item);
		
		return menu;
	}

	/**
	 * ツリービュー用のコンテキストメニューを生成する。
	 * @return	ツリービュー用コンテキストメニュー
	 */
	protected JTreePopupMenu createTreeContextMenu() {
		JMenuItem item;
		JTreePopupMenu menu = new JTreePopupMenu();
		
		//--- create new folder
		item = createMenuItem(getMenuAction(ManagerMenuResources.ID_FILE_NEW_DIR));
		menu.add(item);
		//---
		menu.addSeparator();
		//--- copy
		item = createMenuItem(getMenuAction(ManagerMenuResources.ID_EDIT_COPY));
		menu.add(item);
		//--- paste
		item = createMenuItem(getMenuAction(ManagerMenuResources.ID_EDIT_PASTE));
		menu.add(item);
		//--- delete
		item = createMenuItem(getMenuAction(ManagerMenuResources.ID_EDIT_DELETE));
		menu.add(item);
		//---
		menu.addSeparator();
		//--- move to
		item = createMenuItem(getMenuAction(ManagerMenuResources.ID_FILE_MOVETO));
		menu.add(item);
		//--- rename
		item = createMenuItem(getMenuAction(ManagerMenuResources.ID_FILE_RENAME));
		menu.add(item);
		//---
		menu.addSeparator();
		//--- refresh
		item = createMenuItem(getMenuAction(ManagerMenuResources.ID_FILE_REFRESH));
		menu.add(item);
		
		return menu;
	}

	/**
	 * 標準のツールバーを生成する。
	 * @return	ツールバー
	 */
	protected JToolBar createMainToolBar() {
		JToolBar bar = new JToolBar(JToolBar.HORIZONTAL);
		
		//--- [File]-[Create new folder]
		bar.add(new ToolBarButton(getMenuAction(ManagerMenuResources.ID_FILE_NEW_DIR)));
		//---
		bar.addSeparator();
		//--- [Edit]-[Copy]
		bar.add(new ToolBarButton(getMenuAction(ManagerMenuResources.ID_EDIT_COPY)));
		//--- [Edit]-[Paste]
		bar.add(new ToolBarButton(getMenuAction(ManagerMenuResources.ID_EDIT_PASTE)));
		//---
		bar.addSeparator();
		//--- [View]-[Refresh]
		bar.add(new ToolBarButton(getMenuAction(ManagerMenuResources.ID_FILE_REFRESH)));
		
		return bar;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
