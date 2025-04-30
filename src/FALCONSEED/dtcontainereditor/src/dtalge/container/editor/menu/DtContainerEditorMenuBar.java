/*
 * @(#)DtContainerEditorMenuBar.java	2.0.0	2025/02/17
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtContainerEditorMenuBar.java	1.1.0	2023/01/24
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtContainerEditorMenuBar.java	1.0.0	2022/12/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.menu;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import ssac.util.Strings;
import ssac.util.Validations;
import ssac.util.swing.menu.AbExMenuBar;
import ssac.util.swing.menu.AbMenuItemAction;
import ssac.util.swing.menu.IMenuHandler;
import ssac.util.swing.menu.MenuItemResource;
import ssac.util.swing.menu.ToolBarButton;
import ssac.util.swing.tree.JTreePopupMenu;

/**
 * データコンテナエディタの標準メニュー。
 * 
 * @version 2.0.0
 */
public class DtContainerEditorMenuBar extends AbExMenuBar
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** コンテントツリー用ポップアップメニュー **/
	private JTreePopupMenu	_popupContentTreeContext;
	//private JMenuItem[]		_treeContextMenuItems;
	/** コンテントエディタテーブルの行ヘッダー用ポップアップメニュー **/
	private JPopupMenu		_popupContentEditorTableRowHeaderContext;
	/** コンテントエディタテーブルのセル用ポップアップメニュー **/
	private JPopupMenu		_popupContentEditorTableCellContext;
	/** エディタタブのコンテキストメニュー **/
	private JPopupMenu		_popupEditorTabContextMenu;
	/** メインツールバー **/
	private JToolBar		_mainToolBar;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public DtContainerEditorMenuBar() {
		this(null);
	}
	
	public DtContainerEditorMenuBar(final IMenuHandler handler) {
		super(handler);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * コンテントツリー専用のコンテキストメニューを取得する。
	 * @return	コンテントツリー専用コンテキストメニュー
	 */
	public JTreePopupMenu getContentTreeContextMenu() {
		if (_popupContentTreeContext == null) {
			_popupContentTreeContext = createContentTreeContextMenu();
		}
		return _popupContentTreeContext;
	}
	
	/**
	 * コンテントエディタテーブルの行ヘッダー用ポップアップメニューを取得する。
	 * @return	コンテントエディタテーブルの行ヘッダー用ポップアップメニュー
	 */
	public JPopupMenu getContentEditorTableRowHeaderContextMenu() {
		if (_popupContentEditorTableRowHeaderContext == null) {
			_popupContentEditorTableRowHeaderContext = createContentEditorTableRowHeaderContextMenu();
		}
		return _popupContentEditorTableRowHeaderContext;
	}
	
	/**
	 * コンテントエディタテーブルのセル用ポップアップメニューを取得する。
	 * @return	コンテントエディタテーブルのセル用ポップアップメニュー
	 */
	public JPopupMenu getContentEditorTableCellContextMenu() {
		if (_popupContentEditorTableCellContext == null) {
			_popupContentEditorTableCellContext = createContentEditorTableCellContextMenu();
		}
		return _popupContentEditorTableCellContext;
	}

	/**
	 * エディタタブ専用のコンテキストメニューを取得する。
	 * @return	エディタタブ専用コンテキストメニュー
	 */
	public JPopupMenu getEditorTabContextMenu() {
		if (_popupEditorTabContextMenu == null) {
			_popupEditorTabContextMenu = createEditorTabContextMenu();
		}
		return _popupEditorTabContextMenu;
	}
	
	/**
	 * エディタのコンテントツリーに接続するツールバーを生成する。
	 * @return	コンテントツリー専用ツールバー
	 */
	public JToolBar createContentTreeToolBar() {
		JToolBar toolbar = new JToolBar(JToolBar.HORIZONTAL);
		toolbar.setFloatable(false);	// ツールバーの移動禁止

		//--- Label
		toolbar.add(new JLabel());
		//--- 右詰め
		toolbar.add(Box.createHorizontalGlue());
		//--- add
		toolbar.add(new ToolBarButton(getMenuAction(DtContainerEditorMenuResources.ID_TREE_ADD)));
		//--- replace
		toolbar.add(new ToolBarButton(getMenuAction(DtContainerEditorMenuResources.ID_TREE_REPLACE)));
		//--- export
		toolbar.add(new ToolBarButton(getMenuAction(DtContainerEditorMenuResources.ID_TREE_EXPORT)));
		////---
		//toolbar.addSeparator();
		////--- cut
		//toolbar.add(new ToolBarButton(getMenuAction(DtContainerEditorMenuResources.ID_TREE_CUT)));
		////--- copy
		//toolbar.add(new ToolBarButton(getMenuAction(DtContainerEditorMenuResources.ID_TREE_COPY)));
		////--- paste
		//toolbar.add(new ToolBarButton(getMenuAction(DtContainerEditorMenuResources.ID_TREE_PASTE)));
		//---
		//toolbar.addSeparator();
		//--- delete
		//toolbar.add(new ToolBarButton(getMenuAction(DtContainerEditorMenuResources.ID_TREE_DELETE)));
		//---
		//toolbar.addSeparator();
		////--- Move up
		//toolbar.add(new ToolBarButton(getMenuAction(DtContainerEditorMenuResources.ID_TREE_MOVE_UP)));
		////--- Move down
		//toolbar.add(new ToolBarButton(getMenuAction(DtContainerEditorMenuResources.ID_TREE_MOVE_DOWN)));
		
		return toolbar;
	}
	
	public JToolBar createContentEditorTableToolBar() {
		JToolBar toolbar = new JToolBar(JToolBar.HORIZONTAL);
		toolbar.setFloatable(false);	// ツールバーの移動禁止

		//--- 右詰め
		toolbar.add(Box.createHorizontalGlue());
		toolbar.add(Box.createHorizontalStrut(100));
		//--- cut
		toolbar.add(new ToolBarButton(getMenuAction(DtContainerEditorMenuResources.ID_TABLE_CUT)));
		//--- copy
		toolbar.add(new ToolBarButton(getMenuAction(DtContainerEditorMenuResources.ID_TABLE_COPY)));
		//--- paste
		toolbar.add(new ToolBarButton(getMenuAction(DtContainerEditorMenuResources.ID_TABLE_PASTE)));
		
		return toolbar;
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
	
	/**
	 * 全てのメニュー項目の状態を更新する。
	 * <p>
	 * このメソッドは、このメニューバーに登録されている全てのメニュー項目に対して
	 * 更新イベントを発生させる。この更新イベントは、メニューバーに登録されている
	 * ハンドラに通知される。
	 * <p>
	 * 基本的に、コマンド文字列が登録されていない項目は除外される。
	 */
	@Override
	public void updateAllMenuItems() {
		super.updateAllMenuItems();
		
		// update tree menu
		//getContentTreeContextMenu();
		//for (JMenuItem item : _treeContextMenuItems) {
		//	fireMenuItemUpdate(item);
		//}
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
		
		// invisible menu
		this.add(createTreeMenu());
		this.add(createTableMenu());
		
		// setup content-tree only menu items
//		ensureMenuActionsForContentTree();
//		ensureMenuActionsForContentEditor();
	}
	
//	protected void ensureMenuActionsForContentTree() {
//		ensureMenuItemAction(DtContainerEditorMenuResources.ID_DTCTREE_EDIT_ADD);
//		ensureMenuItemAction(DtContainerEditorMenuResources.ID_DTCTREE_EDIT_REPLACE);
//		ensureMenuItemAction(DtContainerEditorMenuResources.ID_DTCTREE_EDIT_EXPORT);
//		ensureMenuItemAction(DtContainerEditorMenuResources.ID_DTCTREE_EDIT_CUT);
//		ensureMenuItemAction(DtContainerEditorMenuResources.ID_DTCTREE_EDIT_COPY);
//		ensureMenuItemAction(DtContainerEditorMenuResources.ID_DTCTREE_EDIT_PASTE);
//		ensureMenuItemAction(DtContainerEditorMenuResources.ID_DTCTREE_EDIT_DELETE);
//		ensureMenuItemAction(DtContainerEditorMenuResources.ID_DTCTREE_EDIT_RENAME);
//		ensureMenuItemAction(DtContainerEditorMenuResources.ID_DTCTREE_EDIT_MOVE_UP);
//		ensureMenuItemAction(DtContainerEditorMenuResources.ID_DTCTREE_EDIT_MOVE_DOWN);
//	}
//	
//	protected void ensureMenuActionsForContentEditor() {
//		ensureMenuItemAction(DtContainerEditorMenuResources.ID_DTCCONTENT_EDIT_CUT);
//		ensureMenuItemAction(DtContainerEditorMenuResources.ID_DTCCONTENT_EDIT_COPY);
//		ensureMenuItemAction(DtContainerEditorMenuResources.ID_DTCCONTENT_EDIT_PASTE);
//		ensureMenuItemAction(DtContainerEditorMenuResources.ID_DTCCONTENT_EDIT_DELETE);
//		ensureMenuItemAction(DtContainerEditorMenuResources.ID_DTCCONTENT_TABLE_ROW_INSERT_ABOVE);
//		ensureMenuItemAction(DtContainerEditorMenuResources.ID_DTCCONTENT_TABLE_ROW_INSERT_BELOW);
//		ensureMenuItemAction(DtContainerEditorMenuResources.ID_DTCCONTENT_TABLE_ROW_INSERT_COPIED);
//		ensureMenuItemAction(DtContainerEditorMenuResources.ID_DTCCONTENT_TABLE_ROW_CUT);
//		ensureMenuItemAction(DtContainerEditorMenuResources.ID_DTCCONTENT_TABLE_ROW_DELETE);
//		ensureMenuItemAction(DtContainerEditorMenuResources.ID_DTCCONTENT_TABLE_ROW_SELECT);
//		ensureMenuItemAction(DtContainerEditorMenuResources.ID_DTCCONTENT_TABLE_INVISIBLE);
//	}
	
	protected Action ensureMenuItemAction(String command) {
		Action act = menuActionMap.get(command);
		if (act == null) {
			MenuItemResource mir = DtContainerEditorMenuResources.getMenuResource(command);
			if (mir != null) {
				act = createMenuAction(mir);
				menuActionMap.put(command, act);
			}
		}
		return act;
	}

	/**
	 * 指定されたコマンド名に対応する標準メニューリソースにより、
	 * メニューを生成する。
	 * @param command	標準メニューリソースを取得するキーとなるコマンド名
	 * @return	生成されたメニュー
	 * @throws IllegalArgumentException コマンド名が標準メニューリソースに対応していない場合
	 */
	protected JMenu createDefaultMenu(String command) {
		MenuItemResource mr = DtContainerEditorMenuResources.getMenuResource(command);
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
		MenuItemResource mr = DtContainerEditorMenuResources.getMenuResource(command);
		Validations.validArgument(mr != null);
		return createActionMenuItem(mr);
	}
	
	protected ExCheckMenuItem createDefaultCheckMenuItem(String command) {
		MenuItemResource mr = DtContainerEditorMenuResources.getMenuResource(command);
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
	 * 標準の構成でファイルメニューを生成する。
	 * @return	ファイルメニュー項目を格納するメニュー
	 */
	protected JMenu createFileMenu() {
		JMenu menu = createDefaultMenu(DtContainerEditorMenuResources.ID_FILE_MENU);
		
		//--- [new]
		JMenu newMenu = createDefaultMenu(DtContainerEditorMenuResources.ID_FILE_NEW);
		//--- [new] - [Slip]
		newMenu.add(createDefaultMenuItem(DtContainerEditorMenuResources.ID_FILE_NEW_SLIP_SINGLE));
		//--- [new] - [Binder]
		newMenu.add(createDefaultMenuItem(DtContainerEditorMenuResources.ID_FILE_NEW_BINDER));
		menu.add(newMenu);
		//--- [open]
		menu.add(createDefaultMenuItem(DtContainerEditorMenuResources.ID_FILE_OPEN));
		//--- [open as]
		JMenu openasMenu = createDefaultMenu(DtContainerEditorMenuResources.ID_FILE_OPENAS);
		//--- [open as] - [Slip]
		openasMenu.add(createDefaultMenuItem(DtContainerEditorMenuResources.ID_FILE_OPENAS_SLIP_SINGLE));
		//--- [open as] - [Binder]
		openasMenu.add(createDefaultMenuItem(DtContainerEditorMenuResources.ID_FILE_OPENAS_BINDER));
		menu.add(openasMenu);
		//---
		menu.addSeparator();
		//--- [close]
		menu.add(createDefaultMenuItem(DtContainerEditorMenuResources.ID_FILE_CLOSE));
		//--- [close all]
		menu.add(createDefaultMenuItem(DtContainerEditorMenuResources.ID_FILE_ALL_CLOSE));
		//---
		menu.addSeparator();
		//--- [save]
		menu.add(createDefaultMenuItem(DtContainerEditorMenuResources.ID_FILE_SAVE));
		//--- [save as]
		menu.add(createDefaultMenuItem(DtContainerEditorMenuResources.ID_FILE_SAVEAS));
		//---
		menu.addSeparator();
		//--- [Import]
		JMenu importMenu = createDefaultMenu(DtContainerEditorMenuResources.ID_FILE_IMPORT_MENU);
		//--- [Import]-[Double-Entry Simple Slips CSV as DtBinder]
		importMenu.add(createDefaultMenuItem(DtContainerEditorMenuResources.ID_FILE_IMPORT_DTBINDER_DESIMPLESLIPS_CSV));
		menu.add(importMenu);
		//--- [Export]
		JMenu exportMenu = createDefaultMenu(DtContainerEditorMenuResources.ID_FILE_EXPORT_MENU);
		//--- [Export]-[DtBinder as Double-Entry Simple Slips CSV]
		exportMenu.add(createDefaultMenuItem(DtContainerEditorMenuResources.ID_FILE_EXPORT_DTBINDER_DESIMPLESLIPS_CSV));
		menu.add(exportMenu);
		//---
		menu.addSeparator();
		//--- [preference]
		createDefaultMenuItem(DtContainerEditorMenuResources.ID_FILE_PREFERENCE);
		//menu.add(createDefaultMenuItem(DtContainerEditorMenuResources.ID_FILE_PREFERENCE));
		//---
		//menu.addSeparator();
		//--- [quit]
		menu.add(createDefaultMenuItem(DtContainerEditorMenuResources.ID_FILE_QUIT));
		
		return menu;
	}

	/**
	 * 標準の構成で、非表示の編集メニューを生成する。
	 * @return	非表示の編集メニュー項目を格納するメニュー
	 */
	protected JMenu createEditMenu() {
		JMenu menu = createDefaultMenu(DtContainerEditorMenuResources.ID_EDIT_MENU);
		
		//--- [undo]
		//menu.add(createDefaultMenuItem(DtContainerEditorMenuResources.ID_EDIT_UNDO));
		//--- [redo]
		//menu.add(createDefaultMenuItem(DtContainerEditorMenuResources.ID_EDIT_REDO));
		//---
		//menu.addSeparator();
		//--- [cut]
		menu.add(createDefaultMenuItem(DtContainerEditorMenuResources.ID_EDIT_CUT));
		//--- [copy]
		menu.add(createDefaultMenuItem(DtContainerEditorMenuResources.ID_EDIT_COPY));
		//--- [paste]
		menu.add(createDefaultMenuItem(DtContainerEditorMenuResources.ID_EDIT_PASTE));
		//---
		menu.addSeparator();
		//--- [delete]
		menu.add(createDefaultMenuItem(DtContainerEditorMenuResources.ID_EDIT_DELETE));

		//menu.setVisible(false);
		return menu;
	}
	
	/**
	 * 標準の構成で、非表示のツリーメニューを生成する。
	 * @return	非表示のツリーメニュー項目を格納するメニュー
	 */
	protected JMenu createTreeMenu() {
		JMenu menu = createDefaultMenu(DtContainerEditorMenuResources.ID_TREE_MENU);
		
		//--- [Add]
		menu.add(createDefaultMenuItem(DtContainerEditorMenuResources.ID_TREE_ADD));
		//--- [Replace]
		menu.add(createDefaultMenuItem(DtContainerEditorMenuResources.ID_TREE_REPLACE));
		//--- [Export]
		menu.add(createDefaultMenuItem(DtContainerEditorMenuResources.ID_TREE_EXPORT));
		//---
		menu.addSeparator();
		//--- [cut]
		menu.add(createDefaultMenuItem(DtContainerEditorMenuResources.ID_TREE_CUT));
		//--- [copy]
		menu.add(createDefaultMenuItem(DtContainerEditorMenuResources.ID_TREE_COPY));
		//--- [paste]
		menu.add(createDefaultMenuItem(DtContainerEditorMenuResources.ID_TREE_PASTE));
		//---
		menu.addSeparator();
		//--- [delete]
		menu.add(createDefaultMenuItem(DtContainerEditorMenuResources.ID_TREE_DELETE));
		//---
		menu.addSeparator();
		//--- [rename]
		menu.add(createDefaultMenuItem(DtContainerEditorMenuResources.ID_TREE_RENAME));
		//---
		menu.addSeparator();
		//--- [move up]
		menu.add(createDefaultMenuItem(DtContainerEditorMenuResources.ID_TREE_MOVE_UP));
		//--- [move down]
		menu.add(createDefaultMenuItem(DtContainerEditorMenuResources.ID_TREE_MOVE_DOWN));
		
		menu.setVisible(false);
		return menu;
	}
	
	/**
	 * 標準の構成で、非表示のテーブルメニューを生成する。
	 * @return	非表示のテーブルメニュー項目を格納するメニュー
	 */
	protected JMenu createTableMenu() {
		JMenu menu = createDefaultMenu(DtContainerEditorMenuResources.ID_TABLE_MENU);
		
		//--- [cut]
		menu.add(createDefaultMenuItem(DtContainerEditorMenuResources.ID_TABLE_CUT));
		//--- [copy]
		menu.add(createDefaultMenuItem(DtContainerEditorMenuResources.ID_TABLE_COPY));
		//--- [paste]
		menu.add(createDefaultMenuItem(DtContainerEditorMenuResources.ID_TABLE_PASTE));
		//---
		menu.addSeparator();
		//--- [delete]
		menu.add(createDefaultMenuItem(DtContainerEditorMenuResources.ID_TABLE_DELETE));
		//---
		menu.addSeparator();
		//--- [Insert rows above]
		menu.add(createDefaultMenuItem(DtContainerEditorMenuResources.ID_TABLE_ROW_INSERT_ABOVE));
		//--- [Insert rows below]
		menu.add(createDefaultMenuItem(DtContainerEditorMenuResources.ID_TABLE_ROW_INSERT_BELOW));
		//---
		menu.addSeparator();
		//--- [Cut rows]
		menu.add(createDefaultMenuItem(DtContainerEditorMenuResources.ID_TABLE_ROW_CUT));
		//--- [Insert copied cells]
		menu.add(createDefaultMenuItem(DtContainerEditorMenuResources.ID_TABLE_ROW_INSERT_COPIED));
		//---
		menu.addSeparator();
		//--- [Delete rows]
		menu.add(createDefaultMenuItem(DtContainerEditorMenuResources.ID_TABLE_ROW_DELETE));
		//---
		menu.addSeparator();
		//--- [Select row]
		menu.add(createDefaultMenuItem(DtContainerEditorMenuResources.ID_TABLE_ROW_SELECT));
		
		menu.setVisible(false);
		return menu;
	}
	
	/**
	 * 標準の構成でヘルプメニューを生成する。
	 * @return	ヘルプメニュー項目を格納するメニュー
	 */
	protected JMenu createHelpMenu() {
		JMenu menu = createDefaultMenu(DtContainerEditorMenuResources.ID_HELP_MENU);

		//--- [about]
		menu.add(createDefaultMenuItem(DtContainerEditorMenuResources.ID_HELP_ABOUT));
		
		return menu;
	}
	
	/**
	 * コンテントツリーのノードが選択された時のポップアップメニュー
	 * @return	コンテントツリービューのコンテキストメニュー
	 */
	protected JTreePopupMenu createContentTreeContextMenu() {
		JMenuItem item;
		JTreePopupMenu menu = new JTreePopupMenu();
		
		//--- add
		item = createMenuItem(getMenuAction(DtContainerEditorMenuResources.ID_TREE_ADD));
		menu.add(item);
		//--- replace
		item = createMenuItem(getMenuAction(DtContainerEditorMenuResources.ID_TREE_REPLACE));
		menu.add(item);
		//--- export
		item = createMenuItem(getMenuAction(DtContainerEditorMenuResources.ID_TREE_EXPORT));
		menu.add(item);
		//---
		menu.addSeparator();
		//--- cut
		item = createMenuItem(getMenuAction(DtContainerEditorMenuResources.ID_TREE_CUT));
		menu.add(item);
		//--- copy
		item = createMenuItem(getMenuAction(DtContainerEditorMenuResources.ID_TREE_COPY));
		menu.add(item);
		//--- paste
		item = createMenuItem(getMenuAction(DtContainerEditorMenuResources.ID_TREE_PASTE));
		menu.add(item);
		//---
		menu.addSeparator();
		//--- delete
		item = createMenuItem(getMenuAction(DtContainerEditorMenuResources.ID_TREE_DELETE));
		menu.add(item);
		//---
		menu.addSeparator();
		//--- rename
		item = createMenuItem(getMenuAction(DtContainerEditorMenuResources.ID_TREE_RENAME));
		menu.add(item);
		//---
		//menu.addSeparator();
		////--- move up
		//item = createMenuItem(getMenuAction(DtContainerEditorMenuResources.ID_DTCTREE_EDIT_MOVE_UP));
		//menu.add(item);
		//itemlist.add(item);
		////--- move down
		//item = createMenuItem(getMenuAction(DtContainerEditorMenuResources.ID_DTCTREE_EDIT_MOVE_DOWN));
		//menu.add(item);
		//itemlist.add(item);
		
		return menu;
	}
	
	/**
	 * コンテントエディタテーブルの行ヘッダー用ポップアップメニューを生成する。
	 * @return	コンテントエディタテーブルの行ヘッダー用ポップアップメニュー
	 */
	protected JPopupMenu createContentEditorTableRowHeaderContextMenu() {
		JMenuItem item;
		JPopupMenu menu = new JPopupMenu();
		
		//--- [Insert rows above]
		item = createMenuItem(getMenuAction(DtContainerEditorMenuResources.ID_TABLE_ROW_INSERT_ABOVE));
		menu.add(item);
		//--- [Insert rows below]
		item = createMenuItem(getMenuAction(DtContainerEditorMenuResources.ID_TABLE_ROW_INSERT_BELOW));
		menu.add(item);
		//---
		menu.addSeparator();
		//--- [Cut rows]
		item = createMenuItem(getMenuAction(DtContainerEditorMenuResources.ID_TABLE_ROW_CUT));
		menu.add(item);
		//--- [copy]
		item = createMenuItem(getMenuAction(DtContainerEditorMenuResources.ID_TABLE_COPY));
		menu.add(item);
		//--- [Insert copied cells]
		item = createMenuItem(getMenuAction(DtContainerEditorMenuResources.ID_TABLE_ROW_INSERT_COPIED));
		menu.add(item);
		//---
		menu.addSeparator();
		//--- [Delete rows]
		item = createMenuItem(getMenuAction(DtContainerEditorMenuResources.ID_TABLE_ROW_DELETE));
		menu.add(item);
		
		return menu;
	}
	
	/**
	 * コンテントエディタテーブルのセル用ポップアップメニューを生成する。
	 * @return	コンテントエディタテーブルのセル用ポップアップメニュー
	 */
	protected JPopupMenu createContentEditorTableCellContextMenu() {
		JMenuItem item;
		JPopupMenu menu = new JPopupMenu();
		
		//--- [cut]
		item = createMenuItem(getMenuAction(DtContainerEditorMenuResources.ID_TABLE_CUT));
		menu.add(item);
		//--- [copy]
		item = createMenuItem(getMenuAction(DtContainerEditorMenuResources.ID_TABLE_COPY));
		menu.add(item);
		//--- [paste]
		item = createMenuItem(getMenuAction(DtContainerEditorMenuResources.ID_TABLE_PASTE));
		menu.add(item);
		//---
		menu.addSeparator();
		//--- [delete]
		item = createMenuItem(getMenuAction(DtContainerEditorMenuResources.ID_TABLE_DELETE));
		menu.add(item);
		////---
		//menu.addSeparator();
		////--- [Select row]
		//item = createMenuItem(getMenuAction(DtContainerEditorMenuResources.ID_TABLE_ROW_SELECT));
		//menu.add(item);
		
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
		item = createMenuItem(getMenuAction(DtContainerEditorMenuResources.ID_FILE_CLOSE));
		menu.add(item);
		//---
		menu.addSeparator();
		//--- save
		item = createMenuItem(getMenuAction(DtContainerEditorMenuResources.ID_FILE_SAVE));
		menu.add(item);
		//--- save as
		item = createMenuItem(getMenuAction(DtContainerEditorMenuResources.ID_FILE_SAVEAS));
		menu.add(item);
		//---
		menu.addSeparator();
		//--- close all
		item = createMenuItem(getMenuAction(DtContainerEditorMenuResources.ID_FILE_ALL_CLOSE));
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
		item = createMenuItem(getMenuAction(DtContainerEditorMenuResources.ID_EDIT_CUT));
		menu.add(item);
		//--- copy
		item = createMenuItem(getMenuAction(DtContainerEditorMenuResources.ID_EDIT_COPY));
		menu.add(item);
		//--- paste
		item = createMenuItem(getMenuAction(DtContainerEditorMenuResources.ID_EDIT_PASTE));
		menu.add(item);
		//--- delete
		item = createMenuItem(getMenuAction(DtContainerEditorMenuResources.ID_EDIT_DELETE));
		menu.add(item);
		
		return menu;
	}

	/**
	 * 標準のツールバーを生成する。
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
		bar.add(new ToolBarButton(getMenuAction(DtContainerEditorMenuResources.ID_FILE_OPEN)));
		//--- [File]-[save]
		bar.add(new ToolBarButton(getMenuAction(DtContainerEditorMenuResources.ID_FILE_SAVE)));
		//---
		//bar.addSeparator();
		//--- [Edit]-[Undo]
		//bar.add(new ToolBarButton(getMenuAction(DtContainerEditorMenuResources.ID_EDIT_UNDO)));
		//--- [Edit]-[Redo]
		//bar.add(new ToolBarButton(getMenuAction(DtContainerEditorMenuResources.ID_EDIT_REDO)));
		//---
		bar.addSeparator();
		//--- [Edit]-[cut]
		bar.add(new ToolBarButton(getMenuAction(DtContainerEditorMenuResources.ID_EDIT_CUT)));
		//--- [Edit]-[copy]
		bar.add(new ToolBarButton(getMenuAction(DtContainerEditorMenuResources.ID_EDIT_COPY)));
		//--- [Edit]-[paste]
		bar.add(new ToolBarButton(getMenuAction(DtContainerEditorMenuResources.ID_EDIT_PASTE)));
		//---
		bar.addSeparator();
		//--- [Edit]-[Delete]
		bar.add(new ToolBarButton(getMenuAction(DtContainerEditorMenuResources.ID_EDIT_DELETE)));
		
		//--- glue
		bar.add(Box.createGlue());
		
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
			private static final long serialVersionUID = 1L;

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
