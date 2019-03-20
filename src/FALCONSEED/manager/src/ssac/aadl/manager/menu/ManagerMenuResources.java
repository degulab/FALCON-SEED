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
 * @(#)ManagerMenuResources.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.manager.menu;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import ssac.aadl.common.CommonResources;
import ssac.aadl.manager.ManagerMessages;
import ssac.util.swing.menu.MenuItemResource;

/**
 * モジュールマネージャの標準メニューリソース。
 * <p>
 * このクラスのインスタンスはアプリケーション内で唯一となる。
 * このインスタンスは、インスタンス取得時に自動的に生成される。
 * 
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public class ManagerMenuResources
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	//--- [File] menu item IDs
	static public final String ID_FILE_MENU			= "file";
	static public final String ID_FILE_NEW_DIR		= "file.new.dir";
	static public final String ID_FILE_MOVETO			= "file.moveto";
	static public final String ID_FILE_RENAME			= "file.rename";
	static public final String ID_FILE_REFRESH		= "file.refresh";
	static public final String ID_FILE_WS_REFRESH		= "file.ws.refresh";
	static public final String ID_FILE_WS_SELECT		= "file.ws.select";
	static public final String ID_FILE_QUIT			= "file.quit";
	
	//--- [Edit] menu item IDs
	static public final String ID_EDIT_MENU			= "edit";
	static public final String ID_EDIT_COPY			= "edit.copy";
	static public final String ID_EDIT_PASTE			= "edit.paste";
	static public final String ID_EDIT_DELETE			= "edit.delete";
	
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
	
	private ManagerMenuResources() {}

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
			setupHelpMenuResources();
		}
		return mrmap;
	}

	// [File] menu resources
	static private void setupFileMenuResources() {
		MenuItemResource mr;
		
		//--- File
		mr = new MenuItemResource(ID_FILE_MENU,
                /* name        */ ManagerMessages.getInstance().menuFile,
                /* icon        */ null,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_F,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-CreateFolder
		mr = new MenuItemResource(ID_FILE_NEW_DIR,
                /* name        */ ManagerMessages.getInstance().menuFileNewDir,
                /* icon        */ CommonResources.ICON_NEW_DIR,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_N,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-Rename
		mr = new MenuItemResource(ID_FILE_MOVETO,
                /* name        */ ManagerMessages.getInstance().menuFileMoveTo,
                /* icon        */ CommonResources.ICON_BLANK,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_V,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-Rename
		mr = new MenuItemResource(ID_FILE_RENAME,
                /* name        */ ManagerMessages.getInstance().menuFileRename,
                /* icon        */ CommonResources.ICON_BLANK,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_M,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-Refresh
		mr = new MenuItemResource(ID_FILE_REFRESH,
                /* name        */ ManagerMessages.getInstance().menuFileRefresh,
                /* icon        */ CommonResources.ICON_REFRESH,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_F,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-PackageBase Refresh
		mr = new MenuItemResource(ID_FILE_WS_REFRESH,
                /* name        */ ManagerMessages.getInstance().menuFileRefresh,
                /* icon        */ CommonResources.ICON_REFRESH,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_K,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-PackageBaseSelect
		mr = new MenuItemResource(ID_FILE_WS_SELECT,
                /* name        */ ManagerMessages.getInstance().menuFileSelectWS,
                /* icon        */ CommonResources.ICON_BLANK,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_B,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- File-Quit
		mr = new MenuItemResource(ID_FILE_QUIT,
                /* name        */ ManagerMessages.getInstance().menuFileQuit,
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
                /* name        */ ManagerMessages.getInstance().menuEdit,
                /* icon        */ null,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_E,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Edit-Copy
		mr = new MenuItemResource(ID_EDIT_COPY,
                /* name        */ ManagerMessages.getInstance().menuEditCopy,
                /* icon        */ CommonResources.ICON_COPY,
                /* tooltip     */ ManagerMessages.getInstance().menuEditCopy,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_C,
                /* accelerator */ MenuItemResource.getEditCopyShortcutKeyStroke());
		mrmap.put(mr.getCommandKey(), mr);
		//--- Edit-Paste
		mr = new MenuItemResource(ID_EDIT_PASTE,
                /* name        */ ManagerMessages.getInstance().menuEditPaste,
                /* icon        */ CommonResources.ICON_PASTE,
                /* tooltip     */ ManagerMessages.getInstance().menuEditPaste,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_P,
                /* accelerator */ MenuItemResource.getEditPasteShortcutKeyStroke());
		mrmap.put(mr.getCommandKey(), mr);
		//--- Edit-Paste
		mr = new MenuItemResource(ID_EDIT_DELETE,
                /* name        */ ManagerMessages.getInstance().menuEditDelete,
                /* icon        */ CommonResources.ICON_DELETE,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_L,
                /* accelerator */ MenuItemResource.getEditDeleteShortcutKeyStroke());
		mrmap.put(mr.getCommandKey(), mr);
	}

	// [Help] menu resources
	static private void setupHelpMenuResources() {
		MenuItemResource mr;
		
		//--- Help
		mr = new MenuItemResource(ID_HELP_MENU,
                /* name        */ ManagerMessages.getInstance().menuHelp,
                /* icon        */ null,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_H,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
		//--- Help-About
		mr = new MenuItemResource(ID_HELP_ABOUT,
                /* name        */ ManagerMessages.getInstance().menuHelpAbout,
                /* icon        */ CommonResources.ICON_BLANK,
                /* tooltip     */ null,
                /* description */ null,
                /* mnemonic    */ KeyEvent.VK_A,
                /* accelerator */ null);
		mrmap.put(mr.getCommandKey(), mr);
	}
}
