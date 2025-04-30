/*
 * @(#)DtContainerEditorMenuResources.java	2.0.0	2025/02/17
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtContainerEditorMenuResources.java	1.1.0	2023/01/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtContainerEditorMenuResources.java	1.0.0	2022/12/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.menu;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import dtalge.container.editor.DtContainerEditorMessages;
import dtalge.container.editor.DtContainerEditorResources;
import ssac.aadl.common.CommonResources;
import ssac.util.swing.menu.MenuItemResource;

/**
 * データコンテナエディタの標準メニューリソース。
 * <p>
 * このクラスのインスタンスはアプリケーション内で唯一となる。
 * このインスタンスは、インスタンス取得時に自動的に生成される。
 * 
 * @version 2.0.0
 */
public class DtContainerEditorMenuResources
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	//--- [File] menu item IDs
	static public final String ID_FILE_MENU				= "file";
	static public final String ID_FILE_NEW				= "file.new";
	static public final String ID_FILE_NEW_PREFIX		= ID_FILE_NEW + ".";
	static public final String ID_FILE_NEW_SLIP_SINGLE		= ID_FILE_NEW_PREFIX + "slip";
	static public final String ID_FILE_NEW_BINDER			= ID_FILE_NEW_PREFIX + "binder";
	static public final String ID_FILE_OPEN				= "file.open";
	static public final String ID_FILE_OPENAS			= "file.openas";
	static public final String ID_FILE_OPENAS_PREFIX	= ID_FILE_OPENAS + ".";
	static public final String ID_FILE_OPENAS_SLIP_SINGLE	= ID_FILE_OPENAS_PREFIX + "slip";
	static public final String ID_FILE_OPENAS_BINDER		= ID_FILE_OPENAS_PREFIX + "binder";
	static public final String ID_FILE_CLOSE			= "file.close";
	static public final String ID_FILE_ALL_CLOSE		= "file.allclose";
	static public final String ID_FILE_SAVE				= "file.save";
	static public final String ID_FILE_SAVEAS			= "file.saveas";
	static public final String ID_FILE_IMPORT_MENU		= "file.import";
	static public final String ID_FILE_IMPORT_DTBINDER_DESIMPLESLIPS_CSV = "file.import.dtbinder_desimpleslips_csv";
	static public final String ID_FILE_EXPORT_MENU		= "file.export";
	static public final String ID_FILE_EXPORT_DTBINDER_DESIMPLESLIPS_CSV = "file.export.dtbinder_desimpleslips_csv";
	static public final String ID_FILE_PREFERENCE		= "file.preference";
	static public final String ID_FILE_QUIT				= "file.quit";
	
	//--- [Edit] menu item IDs
	static public final String ID_EDIT_MENU			= "edit";
	static public final String ID_EDIT_UNDO			= "edit.undo";
	static public final String ID_EDIT_REDO			= "edit.redo";
	static public final String ID_EDIT_CUT			= "edit.cut";
	static public final String ID_EDIT_COPY			= "edit.copy";
	static public final String ID_EDIT_PASTE		= "edit.paste";
	static public final String ID_EDIT_DELETE		= "edit.delete";
	
	//--- [Tree] menu items IDs
	static public final String ID_TREE_MENU			= "tree";
	static public final String ID_TREE_CUT			= "tree.cut";
	static public final String ID_TREE_COPY			= "tree.copy";
	static public final String ID_TREE_PASTE		= "tree.paste";
	static public final String ID_TREE_DELETE		= "tree.delete";
	static public final String ID_TREE_ADD			= "tree.add";
	static public final String ID_TREE_REPLACE		= "tree.replace";
	static public final String ID_TREE_EXPORT		= "tree.export";
	static public final String ID_TREE_RENAME		= "tree.rename";
	static public final String ID_TREE_MOVE_UP		= "tree.moveup";
	static public final String ID_TREE_MOVE_DOWN	= "tree.movedown";
	
	//--- [Table] menu item IDs
	static public final String ID_TABLE_MENU				= "table";
	static public final String ID_TABLE_CUT					= "table.cut";
	static public final String ID_TABLE_COPY				= "table.copy";
	static public final String ID_TABLE_PASTE				= "table.paste";
	static public final String ID_TABLE_DELETE				= "table.delete";
	static public final String ID_TABLE_ROW_INSERT_ABOVE	= "table.row.insert_above";
	static public final String ID_TABLE_ROW_INSERT_BELOW	= "table.row.insert_below";
	static public final String ID_TABLE_ROW_INSERT_COPIED	= "table.row.insert_copied";
	static public final String ID_TABLE_ROW_CUT				= "table.row.cut";
	static public final String ID_TABLE_ROW_DELETE			= "table.row.delete";
	static public final String ID_TABLE_ROW_SELECT			= "table.row.select";
	
	//--- [Help] menu item IDs
	static public final String ID_HELP_MENU		= "help";
	static public final String ID_HELP_ABOUT	= "help.about";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/**
	 * アプリケーション内唯一となる、標準メニューリソースのコマンド名とのマップ
	 */
	static private Map<String,MenuItemResource> mrmap = null;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private DtContainerEditorMenuResources() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public boolean hasMenuResource(String command) {
		return getInstance().containsKey(command);
	}
	
	static public MenuItemResource getMenuResource(String command) {
		return getInstance().get(command);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static private Map<String,MenuItemResource> getInstance() {
		if (mrmap == null) {
			mrmap = new HashMap<String,MenuItemResource>();
			setupFileMenuResources();
			setupEditMenuResources();
			setupTreeMenuResources();
			setupTableMenuResources();
			setupHelpMenuResources();
		}
		return mrmap;
	}

	/**
	 * [File] menu resources
	 */
	static private void setupFileMenuResources() {
		MenuItemResource mr;
		
		//--- File
		mr = new MenuItemResource(ID_FILE_MENU,
                /* name        */ DtContainerEditorMessages.getInstance().menuFile,
                /* icon        */ null,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_F,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-New
		mr = new MenuItemResource(ID_FILE_NEW,
                /* name        */ DtContainerEditorMessages.getInstance().menuFileNew,
                /* icon        */ CommonResources.ICON_FILE_NEW,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_N,
                /* accelerator */ null);
		//mr.setAccelerator(MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_N));
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-New-Slip
		mr = new MenuItemResource(ID_FILE_NEW_SLIP_SINGLE,
                /* name        */ DtContainerEditorMessages.getInstance().menuFileNewSlipSingle,
                /* icon        */ null,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_S,
                /* accelerator */ null);
		//mr.setAccelerator(MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_N));
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-New-Binder
		mr = new MenuItemResource(ID_FILE_NEW_BINDER,
                /* name        */ DtContainerEditorMessages.getInstance().menuFileNewBinder,
                /* icon        */ null,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_B,
                /* accelerator */ null);
		//mr.setAccelerator(MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_N));
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-Open
		mr = new MenuItemResource(ID_FILE_OPEN,
                /* name        */ DtContainerEditorMessages.getInstance().menuFileOpen,
                /* icon        */ CommonResources.ICON_FILE_OPEN,
                /* tooltip     */ DtContainerEditorMessages.getInstance().tipFileOpen,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_O,
                /* accelerator */ MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_O));
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-OpenAs
		mr = new MenuItemResource(ID_FILE_OPENAS,
                /* name        */ DtContainerEditorMessages.getInstance().menuFileOpenAs,
                /* icon        */ null,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_P,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-New-Slip
		mr = new MenuItemResource(ID_FILE_OPENAS_SLIP_SINGLE,
                /* name        */ DtContainerEditorMessages.getInstance().menuFileNewSlipSingle + "...",
                /* icon        */ null,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_S,
                /* accelerator */ null);
		//mr.setAccelerator(MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_N));
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-New-Binder
		mr = new MenuItemResource(ID_FILE_OPENAS_BINDER,
                /* name        */ DtContainerEditorMessages.getInstance().menuFileNewBinder + "...",
                /* icon        */ null,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_B,
                /* accelerator */ null);
		//mr.setAccelerator(MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_N));
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-Close
		mr = new MenuItemResource(ID_FILE_CLOSE,
                /* name        */ DtContainerEditorMessages.getInstance().menuFileClose,
                /* icon        */ CommonResources.ICON_FILE_CLOSE,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_C,
                /* accelerator */ MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_W));
		mrmap.put(mr.getCommandKey(), mr);
		//--- File CloseAll
		mr = new MenuItemResource(ID_FILE_ALL_CLOSE,
                /* name        */ DtContainerEditorMessages.getInstance().menuFileAllClose,
                /* icon        */ CommonResources.ICON_FILE_ALLCLOSE,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_L,
                /* accelerator */ MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_W, InputEvent.SHIFT_DOWN_MASK));
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-Save
		mr = new MenuItemResource(ID_FILE_SAVE,
                /* name        */ DtContainerEditorMessages.getInstance().menuFileSave,
                /* icon        */ CommonResources.ICON_FILE_SAVE,
                /* tooltip     */ DtContainerEditorMessages.getInstance().tipFileSave,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_S,
                /* accelerator */ MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_S));
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-SaveAs
		mr = new MenuItemResource(ID_FILE_SAVEAS,
                /* name        */ DtContainerEditorMessages.getInstance().menuFileSaveAs,
                /* icon        */ CommonResources.ICON_BLANK,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_A,
                /* accelerator */ MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_S, InputEvent.SHIFT_DOWN_MASK));
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-Import
		mr = new MenuItemResource(ID_FILE_IMPORT_MENU,
                /* name        */ DtContainerEditorMessages.getInstance().menuFileImport,
                /* icon        */ CommonResources.ICON_BLANK,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_I,
                /* accelerator */ null);	//MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_S, InputEvent.SHIFT_DOWN_MASK));
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-Import-DoubleEntrySimpleSlipsCSV
		mr = new MenuItemResource(ID_FILE_IMPORT_DTBINDER_DESIMPLESLIPS_CSV,
                /* name        */ DtContainerEditorMessages.getInstance().menuFileImportBinderFromDoubleEntrySimpleSlipsCsv,
                /* icon        */ null,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_I,
                /* accelerator */ null);	//MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_S, InputEvent.SHIFT_DOWN_MASK));
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-Export
		mr = new MenuItemResource(ID_FILE_EXPORT_MENU,
                /* name        */ DtContainerEditorMessages.getInstance().menuFileExport,
                /* icon        */ CommonResources.ICON_BLANK,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_E,
                /* accelerator */ null);	//MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_S, InputEvent.SHIFT_DOWN_MASK));
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-Export-DoubleEntrySimpleSlipsCSV
		mr = new MenuItemResource(ID_FILE_EXPORT_DTBINDER_DESIMPLESLIPS_CSV,
                /* name        */ DtContainerEditorMessages.getInstance().menuFileExportBinderToDoubleEntrySimpleSlipsCsv,
                /* icon        */ null,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_E,
                /* accelerator */ null);	//MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_S, InputEvent.SHIFT_DOWN_MASK));
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-Preference
		mr = new MenuItemResource(ID_FILE_PREFERENCE,
                /* name        */ DtContainerEditorMessages.getInstance().menuFilePreference,
                /* icon        */ CommonResources.ICON_BLANK,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_R,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-Quit
		mr = new MenuItemResource(ID_FILE_QUIT,
                /* name        */ DtContainerEditorMessages.getInstance().menuFileQuit,
                /* icon        */ CommonResources.ICON_BLANK,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_X,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
	}
	
	/**
	 * [Edit] menu resources
	 */
	static private void setupEditMenuResources() {
		MenuItemResource mr;
		
		//--- Edit
		mr = new MenuItemResource(ID_EDIT_MENU,
                /* name        */ DtContainerEditorMessages.getInstance().menuEdit,
                /* icon        */ null,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_E,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Edit-Undo
		mr = new MenuItemResource(ID_EDIT_UNDO,
                /* name        */ DtContainerEditorMessages.getInstance().menuEditUndo,
                /* icon        */ CommonResources.ICON_UNDO,
                /* tooltip     */ DtContainerEditorMessages.getInstance().tipEditUndo,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_U,
                /* accelerator */ MenuItemResource.getEditUndoShortcutKeyStroke());
		mrmap.put(mr.getCommandKey(), mr);
		//--- Edit-Redo
		mr = new MenuItemResource(ID_EDIT_REDO,
                /* name        */ DtContainerEditorMessages.getInstance().menuEditRedo,
                /* icon        */ CommonResources.ICON_REDO,
                /* tooltip     */ DtContainerEditorMessages.getInstance().tipEditRedo,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_R,
                /* accelerator */ MenuItemResource.getEditRedoShortcutKeyStroke());
		mrmap.put(mr.getCommandKey(), mr);
		//--- Edit-Cut
		mr = new MenuItemResource(ID_EDIT_CUT,
                /* name        */ DtContainerEditorMessages.getInstance().menuEditCut,
                /* icon        */ CommonResources.ICON_CUT,
                /* tooltip     */ DtContainerEditorMessages.getInstance().tipEditCut,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_T,
                /* accelerator */ MenuItemResource.getEditCutShortcutKeyStroke());
		mrmap.put(mr.getCommandKey(), mr);
		//--- Edit-Copy
		mr = new MenuItemResource(ID_EDIT_COPY,
                /* name        */ DtContainerEditorMessages.getInstance().menuEditCopy,
                /* icon        */ CommonResources.ICON_COPY,
                /* tooltip     */ DtContainerEditorMessages.getInstance().tipEditCopy,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_C,
                /* accelerator */ MenuItemResource.getEditCopyShortcutKeyStroke());
		mrmap.put(mr.getCommandKey(), mr);
		//--- Edit-Paste
		mr = new MenuItemResource(ID_EDIT_PASTE,
                /* name        */ DtContainerEditorMessages.getInstance().menuEditPaste,
                /* icon        */ CommonResources.ICON_PASTE,
                /* tooltip     */ DtContainerEditorMessages.getInstance().tipEditPaste,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_P,
                /* accelerator */ MenuItemResource.getEditPasteShortcutKeyStroke());
		mrmap.put(mr.getCommandKey(), mr);
		//--- Edit-Delete
		mr = new MenuItemResource(ID_EDIT_DELETE,
                /* name        */ DtContainerEditorMessages.getInstance().menuEditDelete,
                /* icon        */ CommonResources.ICON_DELETE,
                /* tooltip     */ DtContainerEditorMessages.getInstance().tipEditDelete,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_L,
                /* accelerator */ MenuItemResource.getEditDeleteShortcutKeyStroke());
		mrmap.put(mr.getCommandKey(), mr);
	}
	
	/**
	 * [Tree] menu resources
	 */
	static private void setupTreeMenuResources() {
		MenuItemResource mr;
		
		//--- Edit
		mr = new MenuItemResource(ID_TREE_MENU,
                /* name        */ DtContainerEditorMessages.getInstance().menuEdit,
                /* icon        */ null,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_E,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);

		//--- Edit-Cut
		mr = new MenuItemResource(ID_TREE_CUT,
                /* name        */ DtContainerEditorMessages.getInstance().menuEditCut,
                /* icon        */ CommonResources.ICON_CUT,
                /* tooltip     */ DtContainerEditorMessages.getInstance().tipEditCut,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_T,
                /* accelerator */ MenuItemResource.getEditCutShortcutKeyStroke());
		mrmap.put(mr.getCommandKey(), mr);
		//--- Edit-Copy
		mr = new MenuItemResource(ID_TREE_COPY,
                /* name        */ DtContainerEditorMessages.getInstance().menuEditCopy,
                /* icon        */ CommonResources.ICON_COPY,
                /* tooltip     */ DtContainerEditorMessages.getInstance().tipEditCopy,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_C,
                /* accelerator */ MenuItemResource.getEditCopyShortcutKeyStroke());
		mrmap.put(mr.getCommandKey(), mr);
		//--- Edit-Paste
		mr = new MenuItemResource(ID_TREE_PASTE,
                /* name        */ DtContainerEditorMessages.getInstance().menuEditPaste,
                /* icon        */ CommonResources.ICON_PASTE,
                /* tooltip     */ DtContainerEditorMessages.getInstance().tipEditPaste,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_P,
                /* accelerator */ MenuItemResource.getEditPasteShortcutKeyStroke());
		mrmap.put(mr.getCommandKey(), mr);
		//--- Edit-Delete
		mr = new MenuItemResource(ID_TREE_DELETE,
                /* name        */ DtContainerEditorMessages.getInstance().menuEditDelete,
                /* icon        */ CommonResources.ICON_DELETE,
                /* tooltip     */ DtContainerEditorMessages.getInstance().tipEditDelete,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_L,
                /* accelerator */ MenuItemResource.getEditDeleteShortcutKeyStroke());
		mrmap.put(mr.getCommandKey(), mr);
		
		//--- Edit-Add
		mr = new MenuItemResource(ID_TREE_ADD,
                /* name        */ DtContainerEditorMessages.getInstance().menuTreeAdd,
                /* icon        */ CommonResources.ICON_ADD,
                /* tooltip     */ DtContainerEditorMessages.getInstance().tipTreeAdd,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_M,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Edit-Replace
		mr = new MenuItemResource(ID_TREE_REPLACE,
                /* name        */ DtContainerEditorMessages.getInstance().menuTreeReplace,
                /* icon        */ DtContainerEditorResources.ICON_IMPORT_INTO_NODE,
                /* tooltip     */ DtContainerEditorMessages.getInstance().tipTreeReplace,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_R,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Edit-Export
		mr = new MenuItemResource(ID_TREE_EXPORT,
                /* name        */ DtContainerEditorMessages.getInstance().menuTreeExport,
                /* icon        */ DtContainerEditorResources.ICON_EXPORT_FROM_NODE,
                /* tooltip     */ DtContainerEditorMessages.getInstance().tipTreeExport,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_E,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Edit-Rename
		mr = new MenuItemResource(ID_TREE_RENAME,
                /* name        */ DtContainerEditorMessages.getInstance().menuTreeRename,
                /* icon        */ null,
                /* tooltip     */ DtContainerEditorMessages.getInstance().tipTreeRename,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_M,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Edit-Move up
		mr = new MenuItemResource(ID_TREE_MOVE_UP,
                /* name        */ DtContainerEditorMessages.getInstance().menuTreeMoveUp,
                /* icon        */ CommonResources.ICON_ARROW_UP,
                /* tooltip     */ DtContainerEditorMessages.getInstance().tipTreeMoveUp,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_U,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Edit-Move down
		mr = new MenuItemResource(ID_TREE_MOVE_DOWN,
                /* name        */ DtContainerEditorMessages.getInstance().menuTreeMoveDown,
                /* icon        */ CommonResources.ICON_ARROW_DOWN,
                /* tooltip     */ DtContainerEditorMessages.getInstance().tipTreeMoveDown,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_D,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
	}
	
	/**
	 * [Table] menu resources
	 */
	static private void setupTableMenuResources() {
		MenuItemResource mr;
		
		//--- Table
		mr = new MenuItemResource(ID_TABLE_MENU,
                /* name        */ DtContainerEditorMessages.getInstance().menuTable,
                /* icon        */ null,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_T,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		
		//--- Table-Cut
		mr = new MenuItemResource(ID_TABLE_CUT,
                /* name        */ DtContainerEditorMessages.getInstance().menuEditCut,
                /* icon        */ CommonResources.ICON_CUT,
                /* tooltip     */ DtContainerEditorMessages.getInstance().tipEditCut,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_T,
                /* accelerator */ MenuItemResource.getEditCutShortcutKeyStroke());
		mrmap.put(mr.getCommandKey(), mr);
		//--- Table-Copy
		mr = new MenuItemResource(ID_TABLE_COPY,
                /* name        */ DtContainerEditorMessages.getInstance().menuEditCopy,
                /* icon        */ CommonResources.ICON_COPY,
                /* tooltip     */ DtContainerEditorMessages.getInstance().tipEditCopy,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_C,
                /* accelerator */ MenuItemResource.getEditCopyShortcutKeyStroke());
		mrmap.put(mr.getCommandKey(), mr);
		//--- Table-Paste
		mr = new MenuItemResource(ID_TABLE_PASTE,
                /* name        */ DtContainerEditorMessages.getInstance().menuEditPaste,
                /* icon        */ CommonResources.ICON_PASTE,
                /* tooltip     */ DtContainerEditorMessages.getInstance().tipEditPaste,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_P,
                /* accelerator */ MenuItemResource.getEditPasteShortcutKeyStroke());
		mrmap.put(mr.getCommandKey(), mr);
		//--- Table-Delete
		mr = new MenuItemResource(ID_TABLE_DELETE,
                /* name        */ DtContainerEditorMessages.getInstance().menuEditDelete,
                /* icon        */ CommonResources.ICON_DELETE,
                /* tooltip     */ DtContainerEditorMessages.getInstance().tipEditDelete,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_L,
                /* accelerator */ MenuItemResource.getEditDeleteShortcutKeyStroke());
		mrmap.put(mr.getCommandKey(), mr);

		//--- Table - Insert rows above
		mr = new MenuItemResource(ID_TABLE_ROW_INSERT_ABOVE,
                /* name        */ DtContainerEditorMessages.getInstance().menuTableRowsInsertAbove,
                /* icon        */ DtContainerEditorResources.ICON_TABLE_ROWS_INSERT_ABOVE,
                /* tooltip     */ DtContainerEditorMessages.getInstance().tipTableRowInsertAbove,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_A,
                // accelerator */ MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_I));
        		/* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Table - Insert rows below
		mr = new MenuItemResource(ID_TABLE_ROW_INSERT_BELOW,
                /* name        */ DtContainerEditorMessages.getInstance().menuTableRowsInsertBelow,
                /* icon        */ DtContainerEditorResources.ICON_TABLE_ROWS_INSERT_BELOW,
                /* tooltip     */ DtContainerEditorMessages.getInstance().tipTableRowInsertBelow,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_B,
                // accelerator */ MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_I, InputEvent.SHIFT_DOWN_MASK));
        		/* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Table - Cut rows
		mr = new MenuItemResource(ID_TABLE_ROW_CUT,
                /* name        */ DtContainerEditorMessages.getInstance().menuTableRowsCut,
                /* icon        */ DtContainerEditorResources.ICON_TABLE_ROWS_CUT,
                /* tooltip     */ DtContainerEditorMessages.getInstance().tipTableRowCut,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_T,
                // accelerator */ MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_X, InputEvent.SHIFT_DOWN_MASK));
        		/* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Table - Insert copy cells
		mr = new MenuItemResource(ID_TABLE_ROW_INSERT_COPIED,
                /* name        */ DtContainerEditorMessages.getInstance().menuTableRowsInsertCopied,
                /* icon        */ CommonResources.ICON_BLANK,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_E,
                // accelerator */ MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_V, InputEvent.SHIFT_DOWN_MASK));
				/* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Table - Delete rows
		mr = new MenuItemResource(ID_TABLE_ROW_DELETE,
                /* name        */ DtContainerEditorMessages.getInstance().menuTableRowsDelete,
                /* icon        */ DtContainerEditorResources.ICON_TABLE_ROWS_DELETE,
                /* tooltip     */ DtContainerEditorMessages.getInstance().tipTableRowDelete,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_D,
                // accelerator */ KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.SHIFT_DOWN_MASK));
				/* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Table - Select rows
		mr = new MenuItemResource(ID_TABLE_ROW_SELECT,
                /* name        */ DtContainerEditorMessages.getInstance().menuTableRowsSelect,
                /* icon        */ CommonResources.ICON_BLANK,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_S,
                // accelerator */ MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_R));
				/* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
	}

	// [Help] menu resources
	static private void setupHelpMenuResources() {
		MenuItemResource mr;
		
		//--- Help
		mr = new MenuItemResource(ID_HELP_MENU,
                /* name        */ DtContainerEditorMessages.getInstance().menuHelp,
                /* icon        */ null,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_H,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Help-About
		mr = new MenuItemResource(ID_HELP_ABOUT,
                /* name        */ DtContainerEditorMessages.getInstance().menuHelpAbout,
                /* icon        */ CommonResources.ICON_BLANK,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_A,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
