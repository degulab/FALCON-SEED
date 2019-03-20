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
 * @(#)ModuleFolderChooser.java	1.14	2009/12/11
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module.swing.tree;

import java.awt.Dialog;
import java.awt.Frame;

import ssac.aadl.module.swing.FilenameValidator;
import ssac.util.io.VirtualFile;
import ssac.util.io.VirtualFileFilter;

/**
 * ツリー形式のフォルダ選択ダイアログ
 * 
 * @version 1.14	2009/12/11
 * @since 1.14
 */
public class ModuleFolderChooser extends ModuleFileChooser
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final VirtualFileFilter DefaultFolderFileFilter = new FolderFileFilter();

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ModuleFolderChooser(Frame owner, boolean rootVisible, boolean allowCreateFolder,
								VirtualFile root, VirtualFile initialSelection,
								String title, String description,
								String treeLabel, String inputLabel,
								FilenameValidator validator)
	{
		super(owner, rootVisible, allowCreateFolder,
				root, initialSelection,
				title, description, treeLabel, inputLabel,
				DefaultFolderFileFilter, validator);
		setFileSelectionMode(ModuleFileChooser.FOLDER_ONLY);
	}
	
	public ModuleFolderChooser(Dialog owner, boolean rootVisible, boolean allowCreateFolder,
								VirtualFile root, VirtualFile initialSelection,
								String title, String description,
								String treeLabel, String inputLabel,
								FilenameValidator validator)
	{
		super(owner, rootVisible, allowCreateFolder,
				root, initialSelection,
				title, description, treeLabel, inputLabel,
				DefaultFolderFileFilter, validator);
		setFileSelectionMode(ModuleFileChooser.FOLDER_ONLY);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	/**
	 * フォルダのみを表示するファイルフィルター。
	 * このフィルターでは、プロジェクトルートではないモジュールパッケージルートは
	 * 単一ファイルとみなして表示しない。
	 */
	static protected class FolderFileFilter implements VirtualFileFilter
	{
		public boolean accept(VirtualFile pathname) {
			// 隠しファイルは許可しない
			if (pathname.isHidden()) {
				return false;
			}
			
			// フォルダではない場合は許可しない
			if (!pathname.isDirectory()) {
				return false;
			}
			
			// プロジェクトルートではないモジュールパッケージルートは、
			// 内部変更不可とするので許可しない
			boolean rootProj = ModuleFileTreeModel.isProjectRootFile(pathname);
			boolean rootPack = ModuleFileTreeModel.isModulePackageRootFile(pathname);
			if (rootPack && !rootProj) {
				return false;
			}
			
			// 上記以外のものは許可する
			return true;
		}
	}
}
