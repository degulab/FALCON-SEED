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
 * @(#)RunnerMenuResources.java	3.3.0	2016/05/31
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerMenuResources.java	3.2.1	2015/07/08
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerMenuResources.java	3.2.0	2015/06/05
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerMenuResources.java	2.1.0	2013/08/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerMenuResources.java	2.0.0	2012/10/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerMenuResources.java	1.22	2012/08/28
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerMenuResources.java	1.20	2012/03/22
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerMenuResources.java	1.10	2011/02/14
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerMenuResources.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.runner.menu;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import ssac.aadl.common.CommonResources;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.RunnerResources;
import ssac.util.swing.menu.MenuItemResource;

/**
 * モジュールランナーの標準メニューリソース。
 * <p>
 * このクラスのインスタンスはアプリケーション内で唯一となる。
 * このインスタンスは、インスタンス取得時に自動的に生成される。
 * 
 * @version 3.3.0
 */
public class RunnerMenuResources
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	//--- menu item IDs for Tree only
	static public final String ID_TREE_FILE_MENU			= "tree.file";
	static public final String ID_TREE_FILE_OPEN			= ID_TREE_FILE_MENU + ".open";
	static public final String ID_TREE_FILE_OPEN_TYPED	= ID_TREE_FILE_MENU + ".open.typed";
	static public final String ID_TREE_FILE_OPEN_TYPED_CSV	= ID_TREE_FILE_OPEN_TYPED + ".csv";
	
	//--- Invisible menu item IDs
	static public final String ID_HIDE_MENU = "invisible";
	static public final String ID_HIDE_SHOW_MEDARGSDLG	= "invisible.show.medargsdlg";
	static public final String ID_HIDE_SHOW_CHARTWINDOW	= "invisible.show.chartwindow";
	
	//--- [File] menu item IDs
	static public final String ID_FILE_MENU			= "file";
	static public final String ID_FILE_NEW			= "file.new";
	static public final String ID_FILE_NEW_PREFIX			= ID_FILE_NEW + ".";
	static public final String ID_FILE_NEW_MODULEEXECDEF	= ID_FILE_NEW_PREFIX + "module.execdef";
	static public final String ID_FILE_NEW_MACROFILTER		= ID_FILE_NEW_PREFIX + "macrofilter";
	static public final String ID_FILE_NEW_GENERICFILTER	= ID_FILE_NEW_PREFIX + "genericfilter";
	static public final String ID_FILE_NEW_FOLDER			= ID_FILE_NEW_PREFIX + "folder";
	static public final String ID_FILE_OPEN				= "file.open";
	static public final String ID_FILE_OPEN_CONFIG_CSV	= "file.open.config.csv";
	static public final String ID_FILE_REOPEN			= "file.reopen";
	static public final String ID_FILE_REOPEN_PREFIX	= ID_FILE_REOPEN + ".";
	static public final String ID_FILE_REOPEN_DEFAULT	= ID_FILE_REOPEN_PREFIX + "default";
	static public final String ID_FILE_CLOSE			= "file.close";
	static public final String ID_FILE_ALL_CLOSE		= "file.allclose";
//	static public final String ID_FILE_SAVE				= "file.save";
	static public final String ID_FILE_SAVEAS			= "file.saveas";
	static public final String ID_FILE_MOVETO			= "file.moveto";
	static public final String ID_FILE_RENAME			= "file.rename";
	static public final String ID_FILE_REFRESH			= "file.refresh";
	static public final String ID_FILE_SELECT_USERROOT	= "file.select.userroot";
	static public final String ID_FILE_SELECT_USERROOT_MEXECDEF	= ID_FILE_SELECT_USERROOT + ".mexecdef";
	static public final String ID_FILE_SELECT_USERROOT_DATAFILE	= ID_FILE_SELECT_USERROOT + ".datafile";
	static public final String ID_FILE_PREFERENCE		= "file.preference";
	static public final String ID_FILE_IMPORT			= "file.import";
	static public final String ID_FILE_EXPORT			= "file.export";
	static public final String ID_FILE_QUIT				= "file.quit";
	
	//--- [Edit] menu item IDs
	static public final String ID_EDIT_MENU			= "edit";
//	static public final String ID_EDIT_UNDO			= "edit.undo";
//	static public final String ID_EDIT_REDO			= "edit.redo";
//	static public final String ID_EDIT_CUT			= "edit.cut";
	static public final String ID_EDIT_COPY			= "edit.copy";
	static public final String ID_EDIT_PASTE		= "edit.paste";
	static public final String ID_EDIT_DELETE		= "edit.delete";
	static public final String ID_EDIT_SELECTALL	= "edit.selectall";
	static public final String ID_EDIT_JUMP			= "edit.jump";
	
	//--- [Find] menu item IDs
	static public final String ID_FIND_MENU	= "find";
	static public final String ID_FIND_FIND	= "find.find";
	static public final String ID_FIND_NEXT	= "find.next";
	static public final String ID_FIND_PREV	= "find.prev";
	
	//--- [Filter] menu item IDs
	static public final String ID_FILTER_MENU					= "filter";
	static public final String ID_FILTER_RUN					= "filter.run";
	static public final String ID_FILTER_EDIT					= "filter.edit";
	static public final String ID_FILTER_NEW_BY_HISTORY			= "filter.newByHistory";
	static public final String ID_FILTER_RECORD_HISTORY			= "filter.record.history";
	static public final String ID_FILTER_HISTORY_PREFIX			= "filter.history";
	static public final String ID_FILTER_HISTORY_RUN_MENU		= ID_FILTER_HISTORY_PREFIX + ".run.menu";
	static public final String ID_FILTER_HISTORY_RUN_LATEST		= ID_FILTER_HISTORY_PREFIX + ".run.latest";
	static public final String ID_FILTER_HISTORY_RUN_FROM		= ID_FILTER_HISTORY_PREFIX + ".run.from";
	static public final String ID_FILTER_HISTORY_RUN_BEFORE		= ID_FILTER_HISTORY_PREFIX + ".run.before";
	static public final String ID_FILTER_HISTORY_RUN_SELECTED	= ID_FILTER_HISTORY_PREFIX + ".run.selected";
	static public final String ID_FILTER_HISTORY_RUNAS_MENU		= ID_FILTER_HISTORY_PREFIX + ".runas.menu";
	static public final String ID_FILTER_HISTORY_RUNAS_LATEST	= ID_FILTER_HISTORY_PREFIX + ".runas.latest";
	static public final String ID_FILTER_HISTORY_RUNAS_FROM		= ID_FILTER_HISTORY_PREFIX + ".runas.from";
	static public final String ID_FILTER_HISTORY_RUNAS_BEFORE	= ID_FILTER_HISTORY_PREFIX + ".runas.before";
	static public final String ID_FILTER_HISTORY_RUNAS_SELECTED	= ID_FILTER_HISTORY_PREFIX + ".runas.selected";
	static public final String ID_FILTER_HISTORY_DELETE			= ID_FILTER_HISTORY_PREFIX + ".delete";
	
	//--- [Tool] menu item IDs
	static public final String ID_TOOL_MENU				= "tool";
	static public final String ID_TOOL_CHART_MENU		= "tool.chart";
	static public final String ID_TOOL_CHART_SCATTER	= "tool.chart.scatter";
	static public final String ID_TOOL_CHART_LINE		= "tool.chart.line";
	static public final String ID_TOOL_EXCEL2CSV		= "tool.excel2csv";
	
	//--- [Help] menu item IDs
	static public final String ID_HELP_MENU	= "help";
	static public final String ID_HELP_ABOUT	= "help.about";

	/**
	 * アプリケーション内唯一となる、標準メニューリソースのコマンド名とのマップ
	 */
	static private Map<String,MenuItemResource> mrmap = null;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private RunnerMenuResources() {}

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
			setupFindMenuResources();
//			setupRunMenuResources();
			setupFilterMenuResources();
			setupToolMenuResources();
			setupHelpMenuResources();
			setupTreeMenuResources();
			setupInvisibleMenuResources();
		}
		return mrmap;
	}
	
	// menu resources for Tree only
	static private void setupTreeMenuResources() {
		MenuItemResource mr;
		
		//--- Tree-File
		mr = new MenuItemResource(ID_TREE_FILE_MENU,
                /* name        */ "TreeFile",
                /* icon        */ null,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ 0,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		
		//--- Tree-File-Open
		mr = new MenuItemResource(ID_TREE_FILE_OPEN,
                /* name        */ RunnerMessages.getInstance().menuTreeFileOpen,
                /* icon        */ null,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_O,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		
		//--- Tree-File-Open by Type
		mr = new MenuItemResource(ID_TREE_FILE_OPEN_TYPED,
                /* name        */ RunnerMessages.getInstance().menuTreeFileOpenTyped,
                /* icon        */ null,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_T,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		
		//--- Tree-File-Open by Type-CSV
		mr = new MenuItemResource(ID_TREE_FILE_OPEN_TYPED_CSV,
                /* name        */ RunnerMessages.getInstance().menuTreeFileOpenTypedCsv,
                /* icon        */ null,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_C,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
	}

	// [File] menu resources
	static private void setupFileMenuResources() {
		MenuItemResource mr;
		
		//--- File
		mr = new MenuItemResource(ID_FILE_MENU,
                /* name        */ RunnerMessages.getInstance().menuFile,
                /* icon        */ null,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_F,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-New
		mr = new MenuItemResource(ID_FILE_NEW,
                /* name        */ RunnerMessages.getInstance().menuFileNew,
                /* icon        */ CommonResources.ICON_FILE_NEW,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_N,
                /* accelerator */ null);
		//mr.setAccelerator(MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_N));
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-New-ModuleExecDef
		mr = new MenuItemResource(ID_FILE_NEW_MODULEEXECDEF,
                /* name        */ RunnerMessages.getInstance().menuFileNewModuleExecDef,
                /* icon        */ RunnerResources.ICON_FILTER_NEW,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_I,
                /* accelerator */ null);
		//mr.setAccelerator(MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_N));
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-New-GenericFilter
		mr = new MenuItemResource(ID_FILE_NEW_GENERICFILTER,
                /* name        */ RunnerMessages.getInstance().menuFileNewGenericFilter,
                /* icon        */ RunnerResources.ICON_GENERICFILTER_NEW,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_G,
                /* accelerator */ null);
		//mr.setAccelerator(MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_N));
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-New-MacroFilter
		mr = new MenuItemResource(ID_FILE_NEW_MACROFILTER,
                /* name        */ RunnerMessages.getInstance().menuFileNewMacroFilter,
                /* icon        */ RunnerResources.ICON_MACROFILTER_NEW,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_M,
                /* accelerator */ null);
		//mr.setAccelerator(MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_N));
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-New-Folder
		mr = new MenuItemResource(ID_FILE_NEW_FOLDER,
                /* name        */ RunnerMessages.getInstance().menuFileNewFolder,
                /* icon        */ CommonResources.ICON_NEW_DIR,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_F,
                /* accelerator */ null);
		//mr.setAccelerator(MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_N));
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-Open
		mr = new MenuItemResource(ID_FILE_OPEN,
                /* name        */ RunnerMessages.getInstance().menuFileOpen,
                /* icon        */ CommonResources.ICON_FILE_OPEN,
                /* tooltip     */ RunnerMessages.getInstance().tipFileOpen,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_O,
                /* accelerator */ MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_O));
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-Open with config
		mr = new MenuItemResource(ID_FILE_OPEN_CONFIG_CSV,
                /* name        */ RunnerMessages.getInstance().menuFileOpenConfigCsv,
                /* icon        */ CommonResources.ICON_FILE_CUSTOM_OPEN,
                /* tooltip     */ RunnerMessages.getInstance().tipFileOpenConfigCsv,
                /* description */ null,
                /* mnemonic    */ 0,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-Reopen
		mr = new MenuItemResource(ID_FILE_REOPEN,
                /* name        */ RunnerMessages.getInstance().menuFileReopen,
                /* icon        */ CommonResources.ICON_BLANK,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_E,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-Reopen-Default
		mr = new MenuItemResource(ID_FILE_REOPEN_DEFAULT,
                /* name        */ RunnerMessages.getInstance().menuFileReopenDefault,
                /* icon        */ null,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_D,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-Close
		mr = new MenuItemResource(ID_FILE_CLOSE,
                /* name        */ RunnerMessages.getInstance().menuFileClose,
                /* icon        */ CommonResources.ICON_FILE_CLOSE,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_C,
                /* accelerator */ MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_W));
		mrmap.put(mr.getCommandKey(), mr);
		//--- File CloseAll
		mr = new MenuItemResource(ID_FILE_ALL_CLOSE,
                /* name        */ RunnerMessages.getInstance().menuFileAllClose,
                /* icon        */ CommonResources.ICON_FILE_ALLCLOSE,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_L,
                /* accelerator */ MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_W, InputEvent.SHIFT_DOWN_MASK));
		mrmap.put(mr.getCommandKey(), mr);
//		//--- File-Save
//		mr = new MenuItemResource(ID_FILE_SAVE,
//                /* name        */ RunnerMessages.getInstance().menuFileSave,
//                /* icon        */ CommonResources.ICON_FILE_SAVE,
//                /* tooltip     */ RunnerMessages.getInstance().tipFileSave,
//                /* description */ null,
//                /* mnemonic    */ KeyEvent.VK_S,
//                /* accelerator */ MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_S));
//		mrmap.put(mr.getCommandKey(), mr);
		//--- File-SaveAs
		mr = new MenuItemResource(ID_FILE_SAVEAS,
                /* name        */ RunnerMessages.getInstance().menuFileSaveAs,
                /* icon        */ CommonResources.ICON_BLANK,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_A,
                /* accelerator */ MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_S, InputEvent.SHIFT_DOWN_MASK));
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-MoveTo
		mr = new MenuItemResource(ID_FILE_MOVETO,
                /* name        */ RunnerMessages.getInstance().menuFileMoveTo,
                /* icon        */ CommonResources.ICON_BLANK,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_V,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-Rename
		mr = new MenuItemResource(ID_FILE_RENAME,
                /* name        */ RunnerMessages.getInstance().menuFileRename,
                /* icon        */ CommonResources.ICON_BLANK,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_M,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-Refresh
		mr = new MenuItemResource(ID_FILE_REFRESH,
                /* name        */ RunnerMessages.getInstance().menuFileRefresh,
                /* icon        */ CommonResources.ICON_REFRESH,
                /* tooltip     */ RunnerMessages.getInstance().tipFileRefresh,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_F,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-SelectUserRoot
		mr = new MenuItemResource(ID_FILE_SELECT_USERROOT,
                /* name        */ RunnerMessages.getInstance().menuFileSelectUserRoot,
                /* icon        */ RunnerResources.ICON_USERAREA_DISK,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_D,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-SelectUserRoot-ModuleExecDef
		mr = new MenuItemResource(ID_FILE_SELECT_USERROOT_MEXECDEF,
                /* name        */ RunnerMessages.getInstance().menuFileSelectUserRootMExecDef,
                /* icon        */ RunnerResources.ICON_USERAREA_FILTER,
                /* tooltip     */ RunnerMessages.getInstance().tipFileSelectUserRootMExecDef,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_I,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-SelectUserRoot-DataFile
		mr = new MenuItemResource(ID_FILE_SELECT_USERROOT_DATAFILE,
                /* name        */ RunnerMessages.getInstance().menuFileSelectUserRootDataFile,
                /* icon        */ RunnerResources.ICON_USERAREA_DATA,
                /* tooltip     */ RunnerMessages.getInstance().tipFileSelectUserRootDataFile,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_D,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-Preference
		mr = new MenuItemResource(ID_FILE_PREFERENCE,
                /* name        */ RunnerMessages.getInstance().menuFilePreference,
                /* icon        */ CommonResources.ICON_BLANK,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_R,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-Import
		mr = new MenuItemResource(ID_FILE_IMPORT,
                /* name        */ RunnerMessages.getInstance().menuFileImport,
                /* icon        */ CommonResources.ICON_IMPORT,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_I,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-Export
		mr = new MenuItemResource(ID_FILE_EXPORT,
                /* name        */ RunnerMessages.getInstance().menuFileExport,
                /* icon        */ CommonResources.ICON_EXPORT,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_P,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-Quit
		mr = new MenuItemResource(ID_FILE_QUIT,
                /* name        */ RunnerMessages.getInstance().menuFileQuit,
                /* icon        */ CommonResources.ICON_BLANK,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_X,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
	}
	
	// [Edit] menu resources
	static private void setupEditMenuResources() {
		MenuItemResource mr;
		
		//--- Edit
		mr = new MenuItemResource(ID_EDIT_MENU,
                /* name        */ RunnerMessages.getInstance().menuEdit,
                /* icon        */ null,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_E,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
//		//--- Edit-Cut
//		mr = new MenuItemResource(ID_EDIT_CUT,
//                /* name        */ RunnerMessages.getInstance().menuEditCut,
//                /* icon        */ CommonResources.ICON_CUT,
//                /* tooltip     */ RunnerMessages.getInstance().tipEditCut,
//                /* description */ null,
//                /* mnemonic    */ KeyEvent.VK_T,
//                /* accelerator */ MenuItemResource.getEditCutShortcutKeyStroke());
//		mrmap.put(mr.getCommandKey(), mr);
		//--- Edit-Copy
		mr = new MenuItemResource(ID_EDIT_COPY,
                /* name        */ RunnerMessages.getInstance().menuEditCopy,
                /* icon        */ CommonResources.ICON_COPY,
                /* tooltip     */ RunnerMessages.getInstance().tipEditCopy,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_C,
                /* accelerator */ MenuItemResource.getEditCopyShortcutKeyStroke());
		mrmap.put(mr.getCommandKey(), mr);
		//--- Edit-Paste
		mr = new MenuItemResource(ID_EDIT_PASTE,
                /* name        */ RunnerMessages.getInstance().menuEditPaste,
                /* icon        */ CommonResources.ICON_PASTE,
                /* tooltip     */ RunnerMessages.getInstance().tipEditPaste,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_P,
                /* accelerator */ MenuItemResource.getEditPasteShortcutKeyStroke());
		mrmap.put(mr.getCommandKey(), mr);
		//--- Edit-Delete
		mr = new MenuItemResource(ID_EDIT_DELETE,
                /* name        */ RunnerMessages.getInstance().menuEditDelete,
                /* icon        */ CommonResources.ICON_DELETE,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_L,
                /* accelerator */ MenuItemResource.getEditDeleteShortcutKeyStroke());
		mrmap.put(mr.getCommandKey(), mr);
		//--- Edit-SelectAll
		mr = new MenuItemResource(ID_EDIT_SELECTALL,
                /* name        */ RunnerMessages.getInstance().menuEditSelectAll,
                /* icon        */ CommonResources.ICON_BLANK,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_A,
                /* accelerator */ MenuItemResource.getEditSelectAllShortcutKeyStroke());
		mrmap.put(mr.getCommandKey(), mr);
		//--- Edit-Jump
		mr = new MenuItemResource(ID_EDIT_JUMP,
                /* name        */ RunnerMessages.getInstance().menuEditJump,
                /* icon        */ CommonResources.ICON_BLANK,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_G,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
	}

	// [Find] menu resources
	static private void setupFindMenuResources() {
		MenuItemResource mr;
		
		//--- Find
		mr = new MenuItemResource(ID_FIND_MENU,
                /* name        */ RunnerMessages.getInstance().menuFind,
                /* icon        */ null,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_D,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Find-Find
		mr = new MenuItemResource(ID_FIND_FIND,
                /* name        */ RunnerMessages.getInstance().menuFindFind,
                /* icon        */ CommonResources.ICON_FIND,
                /* tooltip     */ RunnerMessages.getInstance().tipFindFind,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_F,
                /* accelerator */ MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_F));
		mrmap.put(mr.getCommandKey(), mr);
		//--- Find-Prev
		mr = new MenuItemResource(ID_FIND_PREV,
                /* name        */ RunnerMessages.getInstance().menuFindPrevious,
                /* icon        */ CommonResources.ICON_FIND_PREV,
                /* tooltip     */ RunnerMessages.getInstance().tipFindPrev,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_P,
                /* accelerator */ MenuItemResource.getFindPrevShortcutKeyStroke());
		mrmap.put(mr.getCommandKey(), mr);
		//--- Find-Next
		mr = new MenuItemResource(ID_FIND_NEXT,
                /* name        */ RunnerMessages.getInstance().menuFindNext,
                /* icon        */ CommonResources.ICON_FIND_NEXT,
                /* tooltip     */ RunnerMessages.getInstance().tipFindNext,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_N,
                /* accelerator */ MenuItemResource.getFindNextShortcutKeyStroke());
		mrmap.put(mr.getCommandKey(), mr);
	}
	
	// [Filter] menu resources
	static private void setupFilterMenuResources() {
		MenuItemResource mr;
		
		//--- Filter
		mr = new MenuItemResource(ID_FILTER_MENU,
                /* name        */ RunnerMessages.getInstance().menuFilter,
                /* icon        */ null,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_I,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Filter-Run
		mr = new MenuItemResource(ID_FILTER_RUN,
                /* name        */ RunnerMessages.getInstance().menuFilterRun,
                /* icon        */ RunnerResources.ICON_RUN,
                /* tooltip     */ RunnerMessages.getInstance().tipFilterRun,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_R,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Filter-RunHistory
		mr = new MenuItemResource(ID_FILTER_HISTORY_RUN_MENU,
                /* name        */ RunnerMessages.getInstance().menuFilterHistoryRun,
                /* icon        */ CommonResources.ICON_BLANK,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_H,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Filter-RunHistory-Latest
		mr = new MenuItemResource(ID_FILTER_HISTORY_RUN_LATEST,
                /* name        */ RunnerMessages.getInstance().menuFilterHistoryRunLatest,
                /* icon        */ RunnerResources.ICON_RERUN_STEP,
                /* tooltip     */ RunnerMessages.getInstance().tipFilterHistoryRunLatest,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_L,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Filter-RunHistory-From
		mr = new MenuItemResource(ID_FILTER_HISTORY_RUN_FROM,
                /* name        */ RunnerMessages.getInstance().menuFilterHistoryRunFrom,
                /* icon        */ RunnerResources.ICON_RERUN_FROM,
                /* tooltip     */ RunnerMessages.getInstance().tipFilterHistoryRunFrom,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_F,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Filter-RunHistory-Before
		mr = new MenuItemResource(ID_FILTER_HISTORY_RUN_BEFORE,
                /* name        */ RunnerMessages.getInstance().menuFilterHistoryRunBefore,
                /* icon        */ RunnerResources.ICON_RERUN_TO,
                /* tooltip     */ RunnerMessages.getInstance().tipFilterHistoryRunBefore,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_B,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Filter-RunHistory-Selected
		mr = new MenuItemResource(ID_FILTER_HISTORY_RUN_SELECTED,
                /* name        */ RunnerMessages.getInstance().menuFilterHistoryRunSelected,
                /* icon        */ RunnerResources.ICON_RERUN_RANGE,
                /* tooltip     */ RunnerMessages.getInstance().tipFilterHistoryRunSelected,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_S,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Filter-RunHistoryAs
		mr = new MenuItemResource(ID_FILTER_HISTORY_RUNAS_MENU,
                /* name        */ RunnerMessages.getInstance().menuFilterHistoryRunAs,
                /* icon        */ CommonResources.ICON_BLANK,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_A,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Filter-RunHistoryAs-Latest
		mr = new MenuItemResource(ID_FILTER_HISTORY_RUNAS_LATEST,
                /* name        */ RunnerMessages.getInstance().menuFilterHistoryRunAsLatest,
                /* icon        */ RunnerResources.ICON_RERUNAS_STEP,
                /* tooltip     */ RunnerMessages.getInstance().tipFilterHistoryRunAsLatest,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_L,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Filter-RunHistoryAs-From
		mr = new MenuItemResource(ID_FILTER_HISTORY_RUNAS_FROM,
                /* name        */ RunnerMessages.getInstance().menuFilterHistoryRunAsFrom,
                /* icon        */ RunnerResources.ICON_RERUNAS_FROM,
                /* tooltip     */ RunnerMessages.getInstance().tipFilterHistoryRunAsFrom,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_F,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Filter-RunHistoryAs-Before
		mr = new MenuItemResource(ID_FILTER_HISTORY_RUNAS_BEFORE,
                /* name        */ RunnerMessages.getInstance().menuFilterHistoryRunAsBefore,
                /* icon        */ RunnerResources.ICON_RERUNAS_TO,
                /* tooltip     */ RunnerMessages.getInstance().tipFilterHistoryRunAsBefore,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_B,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Filter-RunHistoryAs-Selected
		mr = new MenuItemResource(ID_FILTER_HISTORY_RUNAS_SELECTED,
                /* name        */ RunnerMessages.getInstance().menuFilterHistoryRunAsSelected,
                /* icon        */ RunnerResources.ICON_RERUNAS_RANGE,
                /* tooltip     */ RunnerMessages.getInstance().tipFilterHistoryRunAsSelected,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_S,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Filter-Record History
		mr = new MenuItemResource(ID_FILTER_RECORD_HISTORY,
                /* name        */ RunnerMessages.getInstance().menuFilterRecordHistory,
                /* icon        */ RunnerResources.ICON_RECORD_ON,
                /* tooltip     */ RunnerMessages.getInstance().tipFilterRecordHistory,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_C,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Filter-NewByHistory
		mr = new MenuItemResource(ID_FILTER_NEW_BY_HISTORY,
                /* name        */ RunnerMessages.getInstance().menuFilterCreateByHistory,
                /* icon        */ RunnerResources.ICON_FILTER_BY_HISTORY,
                /* tooltip     */ RunnerMessages.getInstance().tipFilterCreateByHistory,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_N,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Filter-Edit
		mr = new MenuItemResource(ID_FILTER_EDIT,
                /* name        */ RunnerMessages.getInstance().menuFilterEdit,
                /* icon        */ RunnerResources.ICON_FILTER_EDIT,
                /* tooltip     */ RunnerMessages.getInstance().tipFilterEdit,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_E,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
	}
	
	// [Tool] menu resources
	static private void setupToolMenuResources() {
		MenuItemResource mr;
		
		//--- Tool
		mr = new MenuItemResource(ID_TOOL_MENU,
                /* name        */ RunnerMessages.getInstance().menuTool,
                /* icon        */ null,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_T,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Tool - Chart
		mr = new MenuItemResource(ID_TOOL_CHART_MENU,
                /* name        */ RunnerMessages.getInstance().menuToolChartMenu,
                /* icon        */ RunnerResources.ICON_CHART_LINE,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_H,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Tool - Chart - Scatter
		mr = new MenuItemResource(ID_TOOL_CHART_SCATTER,
                /* name        */ RunnerMessages.getInstance().menuToolChartScatter,
                /* icon        */ RunnerResources.ICON_CHART_SCATTER,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_S,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Tool - Chart - Line
		mr = new MenuItemResource(ID_TOOL_CHART_LINE,
                /* name        */ RunnerMessages.getInstance().menuToolChartLine,
                /* icon        */ RunnerResources.ICON_CHART_LINE,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_L,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Tool-Excel2csv
		mr = new MenuItemResource(ID_TOOL_EXCEL2CSV,
                /* name        */ RunnerMessages.getInstance().menuToolExcel2Csv,
                /* icon        */ CommonResources.ICON_BLANK,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_E,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
	}

	// [Help] menu resources
	static private void setupHelpMenuResources() {
		MenuItemResource mr;
		
		//--- Help
		mr = new MenuItemResource(ID_HELP_MENU,
                /* name        */ RunnerMessages.getInstance().menuHelp,
                /* icon        */ null,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_H,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Help-About
		mr = new MenuItemResource(ID_HELP_ABOUT,
                /* name        */ RunnerMessages.getInstance().menuHelpAbout,
                /* icon        */ CommonResources.ICON_BLANK,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_A,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
	}
	
	// Invisible menu resources
	static private void setupInvisibleMenuResources() {
		MenuItemResource mr;
		
		mr = new MenuItemResource(ID_HIDE_MENU,
                /* name        */ ID_HIDE_MENU,
                /* icon        */ null,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ 0,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		
//		//--- MExecDefEditDlg show
//		mr = new MenuItemResource(ID_HIDE_EDIT_EXECDEF,
//                /* name        */ RunnerMessages.getInstance().menuHideEditExecDef,
//                /* icon        */ RunnerResources.ICON_EDIT_PROPS,
//                /* tooltip     */ null,
//                /* description */ null,
//                /* mnemonic    */ KeyEvent.VK_E,
//                /* accelerator */ null);
//		mrmap.put(mr.getCommandKey(), mr);
		
		//--- ExecArgsDlg show
		mr = new MenuItemResource(ID_HIDE_SHOW_MEDARGSDLG,
                /* name        */ ID_HIDE_SHOW_MEDARGSDLG,
                /* icon        */ RunnerResources.ICON_WINDOW,
                /* tooltip     */ RunnerMessages.getInstance().tipShowExecArgsDlg,
                /* description */ null,
                /* mnemonic    */ 0,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		
		//--- ChartWindow show
		mr = new MenuItemResource(ID_HIDE_SHOW_CHARTWINDOW,
                /* name        */ ID_HIDE_SHOW_CHARTWINDOW,
                /* icon        */ RunnerResources.ICON_CHART_WINDOW,
                /* tooltip     */ RunnerMessages.getInstance().tipShowChartWindow,
                /* description */ null,
                /* mnemonic    */ 0,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		
		//--- [Filter]-[History Delete]
		mr = new MenuItemResource(ID_FILTER_HISTORY_DELETE,
                /* name        */ RunnerMessages.getInstance().menuEditDelete,
                /* icon        */ CommonResources.ICON_DELETE,
                /* tooltip     */ RunnerMessages.getInstance().tipFilterHistoryDelete,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_L,
                /* accelerator */ MenuItemResource.getEditDeleteShortcutKeyStroke());
		mrmap.put(mr.getCommandKey(), mr);
	}
}
