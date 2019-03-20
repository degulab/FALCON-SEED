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
 * @(#)MacroMenuBar.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 * @(#)MacroMenuBar.java	1.10	2009/01/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.plugin.macro;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import ssac.aadl.common.CommonResources;
import ssac.aadl.editor.EditorResources;
import ssac.aadl.editor.view.EditorMenuBar;
import ssac.aadl.editor.view.menu.EditorMenuResources;
import ssac.util.Validations;
import ssac.util.swing.menu.IMenuHandler;
import ssac.util.swing.menu.MenuItemResource;

/**
 * AADLマクロ専用メニューバー
 * 
 * @version 1.14	2009/12/09
 * 
 * @since 1.10
 */
public class MacroMenuBar extends EditorMenuBar
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	//--- [Table] menu item IDs
	static public final String ID_TABLE_MENU				= "table";
	static public final String ID_TABLE_ROW_INSERT_ABOVE	= "table.row.insert_above";
	static public final String ID_TABLE_ROW_INSERT_BELOW	= "table.row.insert_below";
	static public final String ID_TABLE_ROW_INSERT_COPIED	= "table.row.Insert_copied";
	static public final String ID_TABLE_ROW_CUT			= "table.row.cut";
	static public final String ID_TABLE_ROW_DELETE		= "table.row.delete";
	static public final String ID_TABLE_ROW_SELECT		= "table.row.select";
	static public final String ID_TABLE_INVISIBLE			= "table.invisible";
	
	//--- [Data] menu item IDs
	static public final String ID_DATA_MENU				= "data";
	static public final String ID_DATA_COMMENT_ADD		= "data.comment.add";
	static public final String ID_DATA_COMMENT_REMOVE		= "data.comment.remove";
	static public final String ID_DATA_INPUT_REL_PATH		= "data.input_rel_path";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/**
	 * アプリケーション内唯一となる、専用メニューリソースのコマンド名とのマップ
	 */
	static private Map<String,MenuItemResource> mrmap = null;
	
	private JPopupMenu rowHeaderContextMenu;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MacroMenuBar() {
		this(null);
	}
	
	public MacroMenuBar(final IMenuHandler handler) {
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
	
	public JPopupMenu getRowHeaderContextMenu() {
		if (rowHeaderContextMenu == null) {
			rowHeaderContextMenu = createRowHeaderContextMenu();
		}
		return rowHeaderContextMenu;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * このメニューバーを初期化する。
	 * マクロ専用メニューバーは、[編集]メニューと[検索]メニューの間に
	 * [テーブル]メニューと[データ]メニューが追加される。
	 */
	@Override
	protected void initMenu() {
		// setup menu bar
		this.add(createFileMenu());
		this.add(createEditMenu());
		this.add(createTableMenu());
		this.add(createDataMenu());
		this.add(createFindMenu());
		this.add(createBuildMenu());
		this.add(createHelpMenu());
	}

	@Override
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
		//--- input relative path
		item = createMenuItem(getMenuAction(ID_DATA_INPUT_REL_PATH));
		menu.add(item);
		//---
		menu.addSeparator();
		//--- select rows
		item = createMenuItem(getMenuAction(ID_TABLE_ROW_SELECT));
		menu.add(item);
		//--- select all
		item = createMenuItem(getMenuAction(EditorMenuResources.ID_EDIT_SELECTALL));
		menu.add(item);
		//--- jump
		item = createMenuItem(getMenuAction(EditorMenuResources.ID_EDIT_JUMP));
		menu.add(item);
		
		return menu;
	}
	
	protected JPopupMenu createRowHeaderContextMenu() {
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
		//--- [Insert rows above]
		item = createMenuItem(getMenuAction(ID_TABLE_ROW_INSERT_ABOVE));
		menu.add(item);
		//--- [Insert rows below]
		item = createMenuItem(getMenuAction(ID_TABLE_ROW_INSERT_BELOW));
		menu.add(item);
		//---
		menu.addSeparator();
		//--- [Cut rows]
		item = createMenuItem(getMenuAction(ID_TABLE_ROW_CUT));
		menu.add(item);
		//--- copy
		item = createMenuItem(getMenuAction(EditorMenuResources.ID_EDIT_COPY));
		menu.add(item);
		//--- [Insert copied cells]
		item = createMenuItem(getMenuAction(ID_TABLE_ROW_INSERT_COPIED));
		menu.add(item);
		//--- [Delete rows]
		item = createMenuItem(getMenuAction(ID_TABLE_ROW_DELETE));
		menu.add(item);
		//---
		menu.addSeparator();
		//--- add comment mark
		item = createMenuItem(getMenuAction(ID_DATA_COMMENT_ADD));
		menu.add(item);
		//--- remove comment mark
		item = createMenuItem(getMenuAction(ID_DATA_COMMENT_REMOVE));
		menu.add(item);
		
		return menu;
	}

	/**
	 * テーブルメニューを生成する。
	 * @return	テーブルメニュー項目を格納するメニュー
	 */
	protected JMenu createTableMenu() {
		JMenu menu = createMacroMenu(ID_TABLE_MENU);
		
		//--- [Insert rows above]
		menu.add(createMacroMenuItem(ID_TABLE_ROW_INSERT_ABOVE));
		//--- [Insert rows below]
		menu.add(createMacroMenuItem(ID_TABLE_ROW_INSERT_BELOW));
		//---
		menu.addSeparator();
		//--- [Cut rows] - only context menu
		menu.add(createMacroMenuItem(ID_TABLE_ROW_CUT));
		//--- [Insert copied cells] - only context menu
		menu.add(createMacroMenuItem(ID_TABLE_ROW_INSERT_COPIED));
		//--- [Delete rows]
		menu.add(createMacroMenuItem(ID_TABLE_ROW_DELETE));
		//---
		menu.addSeparator();
		//--- [Select rows]
		menu.add(createMacroMenuItem(ID_TABLE_ROW_SELECT));

		/*
		// Only context menu
		JMenu invMenu = createMacroMenu(ID_TABLE_INVISIBLE);
		invMenu.setVisible(false);
		
		//--- [Insert copied cells] - only context menu
		invMenu.add(createMacroMenuItem(ID_TABLE_ROW_INSERT_COPIED));
		menu.add(invMenu);
		*/
		
		return menu;
	}

	/**
	 * データメニューを生成する。
	 * @return	データメニュー項目を格納するメニュー
	 */
	protected JMenu createDataMenu() {
		JMenu menu = createMacroMenu(ID_DATA_MENU);
		
		//--- [Add comment mark]
		menu.add(createMacroMenuItem(ID_DATA_COMMENT_ADD));
		//--- [Remove comment mark]
		menu.add(createMacroMenuItem(ID_DATA_COMMENT_REMOVE));
		//---
		menu.addSeparator();
		//--- [Input relative path]
		menu.add(createMacroMenuItem(ID_DATA_INPUT_REL_PATH));
		
		return menu;
	}

	/**
	 * 指定されたコマンド名に対応するマクロ専用メニューリソースにより、
	 * メニューを生成する。
	 * @param command	メニューリソースを取得するキーとなるコマンド名
	 * @return	生成されたメニュー
	 * @throws IllegalArgumentException コマンド名がマクロ専用メニューリソースに対応していない場合
	 */
	private JMenu createMacroMenu(String command) {
		MenuItemResource mr = getMenuResource(command);
		Validations.validArgument(mr != null);
		return createMenu(mr);
	}

	/**
	 * 指定されたコマンド名に対応するマクロ専用メニューリソースにより、
	 * アクションを持つメニュー項目を生成する。
	 * @param command	メニューリソースを取得するキーとなるコマンド名
	 * @return	生成されたメニュー項目
	 * @throws IllegalArgumentException コマンド名がマクロ専用メニューリソースに対応していない場合
	 */
	private JMenuItem createMacroMenuItem(String command) {
		MenuItemResource mr = getMenuResource(command);
		Validations.validArgument(mr != null);
		return createActionMenuItem(mr);
	}
	
	static private final Map<String,MenuItemResource> getMenuResourceMap() {
		if (mrmap == null) {
			mrmap = new HashMap<String,MenuItemResource>();
			setupMacroMenuResources();
		}
		return mrmap;
	}
	
	static private final void setupMacroMenuResources() {
		MenuItemResource mr;
		
		//--- Table
		mr = new MenuItemResource(ID_TABLE_MENU,
                /* name        */ MacroMessages.getInstance().menuTable,
                /* icon        */ null,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_T,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Table - Insert rows above
		mr = new MenuItemResource(ID_TABLE_ROW_INSERT_ABOVE,
                /* name        */ MacroMessages.getInstance().menuTableRowsInsertAbove,
                /* icon        */ EditorResources.ICON_TABLE_ROWS_INSERT_ABOVE,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_A,
                /* accelerator */ MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_I));
		mrmap.put(mr.getCommandKey(), mr);
		//--- Table - Insert rows below
		mr = new MenuItemResource(ID_TABLE_ROW_INSERT_BELOW,
                /* name        */ MacroMessages.getInstance().menuTableRowsInsertBelow,
                /* icon        */ EditorResources.ICON_TABLE_ROWS_INSERT_BELOW,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_B,
                /* accelerator */ MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_I, InputEvent.SHIFT_DOWN_MASK));
		mrmap.put(mr.getCommandKey(), mr);
		//--- Table - Cut rows
		mr = new MenuItemResource(ID_TABLE_ROW_CUT,
                /* name        */ MacroMessages.getInstance().menuTableRowsCut,
                /* icon        */ EditorResources.ICON_TABLE_ROWS_CUT,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_T,
                /* accelerator */ MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_X, InputEvent.SHIFT_DOWN_MASK));
		mrmap.put(mr.getCommandKey(), mr);
		//--- Table - Insert copy cells
		mr = new MenuItemResource(ID_TABLE_ROW_INSERT_COPIED,
                /* name        */ MacroMessages.getInstance().menuTableRowsInsertCopied,
                /* icon        */ CommonResources.ICON_BLANK,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_E,
                /* accelerator */ MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_V, InputEvent.SHIFT_DOWN_MASK));
		mrmap.put(mr.getCommandKey(), mr);
		//--- Table - Delete rows
		mr = new MenuItemResource(ID_TABLE_ROW_DELETE,
                /* name        */ MacroMessages.getInstance().menuTableRowsDelete,
                /* icon        */ EditorResources.ICON_TABLE_ROWS_DELETE,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_D,
                /* accelerator */ KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.SHIFT_DOWN_MASK));
		mrmap.put(mr.getCommandKey(), mr);
		//--- Table - Select rows
		mr = new MenuItemResource(ID_TABLE_ROW_SELECT,
                /* name        */ MacroMessages.getInstance().menuTableRowsSelect,
                /* icon        */ CommonResources.ICON_BLANK,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_S,
                /* accelerator */ MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_R));
		mrmap.put(mr.getCommandKey(), mr);
		//--- Table - invisible menu
		mr = new MenuItemResource(ID_TABLE_INVISIBLE,
                /* name        */ "invisible",
                /* icon        */ null,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ 0,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		
		//--- Data
		mr = new MenuItemResource(ID_DATA_MENU,
                /* name        */ MacroMessages.getInstance().menuData,
                /* icon        */ null,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_D,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Data - Add comment mark
		mr = new MenuItemResource(ID_DATA_COMMENT_ADD,
                /* name        */ MacroMessages.getInstance().menuDataCommentAdd,
                /* icon        */ EditorResources.ICON_DATA_COMMENT_ADD,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_M,
                /* accelerator */ MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_M));
		mrmap.put(mr.getCommandKey(), mr);
		//--- Data - Remove comment mark
		mr = new MenuItemResource(ID_DATA_COMMENT_REMOVE,
                /* name        */ MacroMessages.getInstance().menuDataCommentRemove,
                /* icon        */ EditorResources.ICON_DATA_COMMENT_REMOVE,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_K,
                /* accelerator */ MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_N));
		mrmap.put(mr.getCommandKey(), mr);
		//--- Data - Input relative path
		mr = new MenuItemResource(ID_DATA_INPUT_REL_PATH,
                /* name        */ MacroMessages.getInstance().menuDataInputRelPath,
                /* icon        */ CommonResources.ICON_BLANK,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_I,
                /* accelerator */ MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_F));
		mrmap.put(mr.getCommandKey(), mr);
	}
}
