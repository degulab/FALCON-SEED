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
 * @(#)EditorMenuResources.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 * @(#)EditorMenuResources.java	1.10	2008/11/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.view.menu;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import ssac.aadl.common.CommonResources;
import ssac.aadl.editor.EditorMessages;
import ssac.aadl.editor.EditorResources;
import ssac.util.swing.menu.MenuItemResource;

/**
 * AADLエディタの標準メニューリソース。
 * <p>
 * このクラスのインスタンスはアプリケーション内で唯一となる。
 * このインスタンスは、インスタンス取得時に自動的に生成される。
 * 
 * @version 1.14	2009/12/09
 * 
 * @since 1.10
 */
public class EditorMenuResources
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	//--- [File] menu item IDs
	static public final String ID_FILE_MENU			= "file";
	static public final String ID_FILE_NEW			= "file.new";
	static public final String ID_FILE_NEW_PREFIX		= ID_FILE_NEW + ".";
	static public final String ID_FILE_NEW_PROJECT	= ID_FILE_NEW_PREFIX + "project";
	static public final String ID_FILE_NEW_FOLDER		= ID_FILE_NEW_PREFIX + "folder";
	static public final String ID_FILE_OPEN			= "file.open";
	static public final String ID_FILE_REOPEN			= "file.reopen";
	static public final String ID_FILE_REOPEN_PREFIX	= ID_FILE_REOPEN + ".";
	static public final String ID_FILE_REOPEN_DEFAULT	= ID_FILE_REOPEN_PREFIX + "default";
	static public final String ID_FILE_CLOSE			= "file.close";
	static public final String ID_FILE_ALL_CLOSE		= "file.allclose";
	static public final String ID_FILE_SAVE			= "file.save";
	static public final String ID_FILE_SAVEAS			= "file.saveas";
	static public final String ID_FILE_MOVETO			= "file.moveto";
	static public final String ID_FILE_RENAME			= "file.rename";
	static public final String ID_FILE_REFRESH		= "file.refresh";
	static public final String ID_FILE_WS_REFRESH		= "file.ws.refresh";
	static public final String ID_FILE_WS_SELECT		= "file.ws.select";
	static public final String ID_FILE_PROJECT		= "file.project";
	static public final String ID_FILE_PROJECT_REGISTER	= ID_FILE_PROJECT + ".register";
	static public final String ID_FILE_PROJECT_UNREGISTER	= ID_FILE_PROJECT + ".unregister";
	static public final String ID_FILE_PACKAGE		= "file.package";
	static public final String ID_FILE_PACKAGE_REGIST	= ID_FILE_PACKAGE + ".regist";
	static public final String ID_FILE_PACKAGE_REFER	= ID_FILE_PACKAGE + ".refer";
	static public final String ID_FILE_PACKAGE_IMPORT	= ID_FILE_PACKAGE + ".import";
	static public final String ID_FILE_PREFERENCE		= "file.preference";
	static public final String ID_FILE_QUIT			= "file.quit";
	
	//--- [Edit] menu item IDs
	static public final String ID_EDIT_MENU		= "edit";
	static public final String ID_EDIT_UNDO		= "edit.undo";
	static public final String ID_EDIT_REDO		= "edit.redo";
	static public final String ID_EDIT_CUT		= "edit.cut";
	static public final String ID_EDIT_COPY		= "edit.copy";
	static public final String ID_EDIT_PASTE		= "edit.paste";
	static public final String ID_EDIT_DELETE		= "edit.delete";
	static public final String ID_EDIT_SELECTALL	= "edit.selectall";
	static public final String ID_EDIT_JUMP		= "edit.jump";
	
	//--- [Find] menu item IDs
	static public final String ID_FIND_MENU	= "find";
	static public final String ID_FIND_FIND	= "find.find";
	static public final String ID_FIND_NEXT	= "find.next";
	static public final String ID_FIND_PREV	= "find.prev";
	
	//--- [Build] menu item IDs
	static public final String ID_BUILD_MENU			= "build";
	static public final String ID_BUILD_COMPILE		= "build.compile";
	static public final String ID_BUILD_COMPILE_RUN	= "build.compile.run";
	static public final String ID_BUILD_RUN			= "build.run";
	static public final String ID_BUILD_RUNASJAR		= "build.runas.jar";
	static public final String ID_BUILD_COMPILEINDIR	= "build.compile.indir";
	static public final String ID_BUILD_OPTION		= "build.option";
	
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
	
	private EditorMenuResources() {}

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
			setupBuildMenuResources();
			setupHelpMenuResources();
		}
		return mrmap;
	}

	// [File] menu resources
	static private void setupFileMenuResources() {
		MenuItemResource mr;
		
		//--- File
		mr = new MenuItemResource(ID_FILE_MENU,
                /* name        */ EditorMessages.getInstance().menuFile,
                /* icon        */ null,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_F,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-New
		mr = new MenuItemResource(ID_FILE_NEW,
                /* name        */ EditorMessages.getInstance().menuFileNew,
                /* icon        */ CommonResources.ICON_FILE_NEW,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_N,
                /* accelerator */ null);
		//mr.setAccelerator(MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_N));
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-New-Project
		mr = new MenuItemResource(ID_FILE_NEW_PROJECT,
                /* name        */ EditorMessages.getInstance().menuFileNewProject,
                /* icon        */ CommonResources.ICON_NEW_PROJECT,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_P,
                /* accelerator */ null);
		//mr.setAccelerator(MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_N));
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-New-Project
		mr = new MenuItemResource(ID_FILE_NEW_FOLDER,
                /* name        */ EditorMessages.getInstance().menuFileNewFolder,
                /* icon        */ CommonResources.ICON_NEW_DIR,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_F,
                /* accelerator */ null);
		//mr.setAccelerator(MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_N));
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-Open
		mr = new MenuItemResource(ID_FILE_OPEN,
                /* name        */ EditorMessages.getInstance().menuFileOpen,
                /* icon        */ CommonResources.ICON_FILE_OPEN,
                /* tooltip     */ EditorMessages.getInstance().tipFileOpen,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_O,
                /* accelerator */ MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_O));
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-Reopen
		mr = new MenuItemResource(ID_FILE_REOPEN,
                /* name        */ EditorMessages.getInstance().menuFileReopen,
                /* icon        */ CommonResources.ICON_BLANK,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_E,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-Reopen-Default
		mr = new MenuItemResource(ID_FILE_REOPEN_DEFAULT,
                /* name        */ EditorMessages.getInstance().menuFileReopenDefault,
                /* icon        */ null,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_D,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-Close
		mr = new MenuItemResource(ID_FILE_CLOSE,
                /* name        */ EditorMessages.getInstance().menuFileClose,
                /* icon        */ CommonResources.ICON_FILE_CLOSE,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_C,
                /* accelerator */ MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_W));
		mrmap.put(mr.getCommandKey(), mr);
		//--- File CloseAll
		mr = new MenuItemResource(ID_FILE_ALL_CLOSE,
                /* name        */ EditorMessages.getInstance().menuFileAllClose,
                /* icon        */ CommonResources.ICON_FILE_ALLCLOSE,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_L,
                /* accelerator */ MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_W, InputEvent.SHIFT_DOWN_MASK));
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-Save
		mr = new MenuItemResource(ID_FILE_SAVE,
                /* name        */ EditorMessages.getInstance().menuFileSave,
                /* icon        */ CommonResources.ICON_FILE_SAVE,
                /* tooltip     */ EditorMessages.getInstance().tipFileSave,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_S,
                /* accelerator */ MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_S));
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-SaveAs
		mr = new MenuItemResource(ID_FILE_SAVEAS,
                /* name        */ EditorMessages.getInstance().menuFileSaveAs,
                /* icon        */ CommonResources.ICON_BLANK,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_A,
                /* accelerator */ MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_S, InputEvent.SHIFT_DOWN_MASK));
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-moveto
		mr = new MenuItemResource(ID_FILE_MOVETO,
                /* name        */ EditorMessages.getInstance().menuFileMoveTo,
                /* icon        */ CommonResources.ICON_BLANK,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_V,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-rename
		mr = new MenuItemResource(ID_FILE_RENAME,
                /* name        */ EditorMessages.getInstance().menuFileRename,
                /* icon        */ CommonResources.ICON_BLANK,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_M,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-refresh
		mr = new MenuItemResource(ID_FILE_REFRESH,
                /* name        */ EditorMessages.getInstance().menuFileRefresh,
                /* icon        */ CommonResources.ICON_REFRESH,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_F,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-Workspace refresh
		mr = new MenuItemResource(ID_FILE_WS_REFRESH,
                /* name        */ EditorMessages.getInstance().menuFileRefresh,
                /* icon        */ CommonResources.ICON_REFRESH,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_F,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-Register to Project
		mr = new MenuItemResource(ID_FILE_PROJECT_REGISTER,
                /* name        */ EditorMessages.getInstance().menuFileProjRegister,
                /* icon        */ CommonResources.ICON_REGISTER_RPOJECT,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_G,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-Unregister from Project
		mr = new MenuItemResource(ID_FILE_PROJECT_UNREGISTER,
                /* name        */ EditorMessages.getInstance().menuFileProjUnregister,
                /* icon        */ CommonResources.ICON_UNREGISTER_PROJECT,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_U,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-Package
		mr = new MenuItemResource(ID_FILE_PACKAGE,
                /* name        */ EditorMessages.getInstance().menuFilePackage,
                /* icon        */ CommonResources.ICON_BLANK,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_P,
                /* accelerator */ null);
		//mr.setAccelerator(MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_N));
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-Post as Package
		mr = new MenuItemResource(ID_FILE_PACKAGE_REGIST,
                /* name        */ EditorMessages.getInstance().menuFilePackRegist,
                /* icon        */ null,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_R,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-Refer to Package
		mr = new MenuItemResource(ID_FILE_PACKAGE_REFER,
                /* name        */ EditorMessages.getInstance().menuFilePackRefer,
                /* icon        */ null,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_E,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-Import Package
		mr = new MenuItemResource(ID_FILE_PACKAGE_IMPORT,
                /* name        */ EditorMessages.getInstance().menuFilePackImport,
                /* icon        */ null,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_I,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-SelectWorkspace
		mr = new MenuItemResource(ID_FILE_WS_SELECT,
                /* name        */ EditorMessages.getInstance().menuFileSelectWS,
                /* icon        */ CommonResources.ICON_BLANK,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_W,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-Preference
		mr = new MenuItemResource(ID_FILE_PREFERENCE,
                /* name        */ EditorMessages.getInstance().menuFilePreference,
                /* icon        */ CommonResources.ICON_BLANK,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_R,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-Quit
		mr = new MenuItemResource(ID_FILE_QUIT,
                /* name        */ EditorMessages.getInstance().menuFileQuit,
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
                /* name        */ EditorMessages.getInstance().menuEdit,
                /* icon        */ null,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_E,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Edit-Undo
		mr = new MenuItemResource(ID_EDIT_UNDO,
                /* name        */ EditorMessages.getInstance().menuEditUndo,
                /* icon        */ CommonResources.ICON_UNDO,
                /* tooltip     */ EditorMessages.getInstance().tipEditUndo,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_U,
                /* accelerator */ MenuItemResource.getEditUndoShortcutKeyStroke());
		mrmap.put(mr.getCommandKey(), mr);
		//--- Edit-Redo
		mr = new MenuItemResource(ID_EDIT_REDO,
                /* name        */ EditorMessages.getInstance().menuEditRedo,
                /* icon        */ CommonResources.ICON_REDO,
                /* tooltip     */ EditorMessages.getInstance().tipEditRedo,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_R,
                /* accelerator */ MenuItemResource.getEditRedoShortcutKeyStroke());
		mrmap.put(mr.getCommandKey(), mr);
		//--- Edit-Cut
		mr = new MenuItemResource(ID_EDIT_CUT,
                /* name        */ EditorMessages.getInstance().menuEditCut,
                /* icon        */ CommonResources.ICON_CUT,
                /* tooltip     */ EditorMessages.getInstance().tipEditCut,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_T,
                /* accelerator */ MenuItemResource.getEditCutShortcutKeyStroke());
		mrmap.put(mr.getCommandKey(), mr);
		//--- Edit-Copy
		mr = new MenuItemResource(ID_EDIT_COPY,
                /* name        */ EditorMessages.getInstance().menuEditCopy,
                /* icon        */ CommonResources.ICON_COPY,
                /* tooltip     */ EditorMessages.getInstance().tipEditCopy,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_C,
                /* accelerator */ MenuItemResource.getEditCopyShortcutKeyStroke());
		mrmap.put(mr.getCommandKey(), mr);
		//--- Edit-Paste
		mr = new MenuItemResource(ID_EDIT_PASTE,
                /* name        */ EditorMessages.getInstance().menuEditPaste,
                /* icon        */ CommonResources.ICON_PASTE,
                /* tooltip     */ EditorMessages.getInstance().tipEditPaste,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_P,
                /* accelerator */ MenuItemResource.getEditPasteShortcutKeyStroke());
		mrmap.put(mr.getCommandKey(), mr);
		//--- Edit-Delete
		mr = new MenuItemResource(ID_EDIT_DELETE,
                /* name        */ EditorMessages.getInstance().menuEditDelete,
                /* icon        */ CommonResources.ICON_DELETE,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_L,
                /* accelerator */ MenuItemResource.getEditDeleteShortcutKeyStroke());
		mrmap.put(mr.getCommandKey(), mr);
		//--- Edit-SelectAll
		mr = new MenuItemResource(ID_EDIT_SELECTALL,
                /* name        */ EditorMessages.getInstance().menuEditSelectAll,
                /* icon        */ CommonResources.ICON_BLANK,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_A,
                /* accelerator */ MenuItemResource.getEditSelectAllShortcutKeyStroke());
		mrmap.put(mr.getCommandKey(), mr);
		//--- Edit-Jump
		mr = new MenuItemResource(ID_EDIT_JUMP,
                /* name        */ EditorMessages.getInstance().menuEditJump,
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
                /* name        */ EditorMessages.getInstance().menuFind,
                /* icon        */ null,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_S,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Find-Find
		mr = new MenuItemResource(ID_FIND_FIND,
                /* name        */ EditorMessages.getInstance().menuFindFind,
                /* icon        */ CommonResources.ICON_FIND,
                /* tooltip     */ EditorMessages.getInstance().tipFindFind,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_F,
                /* accelerator */ MenuItemResource.getMenuShortcutKeyStroke(KeyEvent.VK_F));
		mrmap.put(mr.getCommandKey(), mr);
		//--- Find-Prev
		mr = new MenuItemResource(ID_FIND_PREV,
                /* name        */ EditorMessages.getInstance().menuFindPrevious,
                /* icon        */ CommonResources.ICON_FIND_PREV,
                /* tooltip     */ EditorMessages.getInstance().tipFindPrev,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_P,
                /* accelerator */ MenuItemResource.getFindPrevShortcutKeyStroke());
		mrmap.put(mr.getCommandKey(), mr);
		//--- Find-Next
		mr = new MenuItemResource(ID_FIND_NEXT,
                /* name        */ EditorMessages.getInstance().menuFindNext,
                /* icon        */ CommonResources.ICON_FIND_NEXT,
                /* tooltip     */ EditorMessages.getInstance().tipFindNext,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_N,
                /* accelerator */ MenuItemResource.getFindNextShortcutKeyStroke());
		mrmap.put(mr.getCommandKey(), mr);
	}

	// [Build] menu resources
	static private void setupBuildMenuResources() {
		MenuItemResource mr;
		
		//--- Build
		mr = new MenuItemResource(ID_BUILD_MENU,
                /* name        */ EditorMessages.getInstance().menuBuild,
                /* icon        */ null,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_B,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Build-Compile
		mr = new MenuItemResource(ID_BUILD_COMPILE,
                /* name        */ EditorMessages.getInstance().menuBuildCompile,
                /* icon        */ EditorResources.ICON_BUILD_COMPILE,
                /* tooltip     */ EditorMessages.getInstance().tipBuildCompile,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_C,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Build-Run
		mr = new MenuItemResource(ID_BUILD_RUN,
                /* name        */ EditorMessages.getInstance().menuBuildRun,
                /* icon        */ EditorResources.ICON_BUILD_RUN,
                /* tooltip     */ EditorMessages.getInstance().tipBuildRun,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_R,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Build-CompileRun
		mr = new MenuItemResource(ID_BUILD_COMPILE_RUN,
                /* name        */ EditorMessages.getInstance().menuBuildCompileAndRun,
                /* icon        */ EditorResources.ICON_BUILD_COMPRUN,
                /* tooltip     */ EditorMessages.getInstance().tipBuildCompileAndRun,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_A,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Build-RunAsJar
		mr = new MenuItemResource(ID_BUILD_RUNASJAR,
                /* name        */ EditorMessages.getInstance().menuBuildRunAsJar,
                /* icon        */ CommonResources.ICON_BLANK,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_J,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Build-CompileAllInFolder
		mr = new MenuItemResource(ID_BUILD_COMPILEINDIR,
                /* name        */ EditorMessages.getInstance().menuBuildCompileInDir,
                /* icon        */ CommonResources.ICON_BLANK,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_D,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Build-Option
		mr = new MenuItemResource(ID_BUILD_OPTION,
                /* name        */ EditorMessages.getInstance().menuBuildOption,
                /* icon        */ CommonResources.ICON_SETTING,
                /* tooltip     */ EditorMessages.getInstance().tipBuildOption,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_O,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
	}

	// [Help] menu resources
	static private void setupHelpMenuResources() {
		MenuItemResource mr;
		
		//--- Help
		mr = new MenuItemResource(ID_HELP_MENU,
                /* name        */ EditorMessages.getInstance().menuHelp,
                /* icon        */ null,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_H,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Help-About
		mr = new MenuItemResource(ID_HELP_ABOUT,
                /* name        */ EditorMessages.getInstance().menuHelpAbout,
                /* icon        */ CommonResources.ICON_BLANK,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_A,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
	}
}
