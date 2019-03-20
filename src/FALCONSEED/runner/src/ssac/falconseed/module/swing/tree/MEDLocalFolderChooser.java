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
 *  Copyright 2007-2012  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)MEDLocalFolderChooser.java	1.20	2012/03/13
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing.tree;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;

import ssac.aadl.module.swing.FilenameValidator;
import ssac.util.io.VirtualFile;
import ssac.util.io.VirtualFileFilter;
import ssac.util.swing.SwingTools;

/**
 * ツリー形式のモジュール実行定義ローカルフォルダ選択ダイアログ
 * 
 * @version 1.20	2012/03/13
 * @since 1.20
 */
public class MEDLocalFolderChooser extends MEDLocalFileChooser
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
	
	static public MEDLocalFolderChooser createFolderChooser(Component parentComponent, boolean allowCreateFolder,
															VirtualFile rootDir,
															VirtualFile initialSelection,
															String title, String description,
															String treeLabel, String inputLabel,
															FilenameValidator validator)
	{
		// create instance
		MEDLocalFolderChooser chooser;
		Window win = SwingTools.getWindowForComponent(parentComponent);
		if (win instanceof Frame) {
			chooser = new MEDLocalFolderChooser((Frame)win, title, validator);
		} else {
			chooser = new MEDLocalFolderChooser((Dialog)win, title, validator);
		}

		// initial component
		chooser.initialComponent(allowCreateFolder, rootDir,
								initialSelection, description, treeLabel, inputLabel,
								DefaultFolderFileFilter);
		
		// set selection mode
		chooser.setFileSelectionMode(MEDLocalFileChooser.FOLDER_ONLY);
		
		// completed
		return chooser;
	}
	
	protected MEDLocalFolderChooser(Frame owner, String title, FilenameValidator validator)
	{
		super(owner, title, validator);
	}
	
	protected MEDLocalFolderChooser(Dialog owner, String title, FilenameValidator validator) {
		super(owner, title, validator);
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
			
			// 上記以外のものは許可する
			return true;
		}
	}
}
