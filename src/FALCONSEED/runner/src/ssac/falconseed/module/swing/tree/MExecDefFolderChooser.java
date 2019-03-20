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
 *  Copyright 2007-2011  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)MExecDefFolderChooser.java	1.10	2011/02/14
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecDefFolderChooser.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing.tree;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;

import ssac.aadl.module.swing.FilenameValidator;
import ssac.falconseed.module.MExecDefFileManager;
import ssac.util.Strings;
import ssac.util.io.VirtualFile;
import ssac.util.io.VirtualFileFilter;
import ssac.util.swing.SwingTools;

/**
 * モジュール実行定義専用ツリー形式のフォルダ選択ダイアログ
 * 
 * @version 1.10	2011/02/14
 */
public class MExecDefFolderChooser extends MExecDefFileChooser
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final VirtualFileFilter DefaultFolderFileFilter = new FolderFileFilter();

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	static public MExecDefFolderChooser createFolderChooser(Component parentComponent, boolean allowCreateFolder,
															VirtualFile systemRoot, VirtualFile userRoot,
															VirtualFile initialSelection,
															String title, String description,
															String treeLabel, String inputLabel,
															FilenameValidator validator)
	{
		// create instance
		MExecDefFolderChooser chooser;
		Window win = SwingTools.getWindowForComponent(parentComponent);
		if (win instanceof Frame) {
			chooser = new MExecDefFolderChooser((Frame)win, title, validator);
		} else {
			chooser = new MExecDefFolderChooser((Dialog)win, title, validator);
		}

		// initial component
		chooser.initialComponent(allowCreateFolder, systemRoot, userRoot,
								initialSelection, description, treeLabel, inputLabel,
								DefaultFolderFileFilter);
		
		// set selection mode
		chooser.setFileSelectionMode(MExecDefFileChooser.FOLDER_ONLY);
		
		// completed
		return chooser;
	}
	
	protected MExecDefFolderChooser(Frame owner, String title, FilenameValidator validator)
	{
		super(owner, title, validator);
	}
	
	protected MExecDefFolderChooser(Dialog owner, String title, FilenameValidator validator) {
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
	static public class FolderFileFilter implements VirtualFileFilter
	{
		public boolean accept(VirtualFile pathname) {
			// 隠しファイルは許可しない
			if (pathname.isHidden()) {
				return false;
			}
			
			// '.' で始まるファイルやディレクトリは非表示とする
			if (Strings.startsWithIgnoreCase(pathname.getName(), ".")) {
				return false;
			}
			
			// フォルダではない場合は許可しない
			if (!pathname.isDirectory()) {
				return false;
			}
			
			// モジュール実行定義は表示しない
			if (MExecDefFileManager.isModuleExecDefData(pathname)) {
				return false;
			}
			
			// 上記以外のものは許可する
			return true;
		}
	}
}
