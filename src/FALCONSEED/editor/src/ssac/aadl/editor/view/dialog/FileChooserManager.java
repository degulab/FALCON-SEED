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
 * @(#)FileChooserManager.java	1.14	2009/12/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)FileChooserManager.java	1.10	2008/12/03
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)FileChooserManager.java	1.00	2008/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.view.dialog;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import ssac.aadl.editor.AADLEditor;
import ssac.aadl.editor.EditorMessages;
import ssac.aadl.module.swing.FileDialogManager;
import ssac.util.Strings;
import ssac.util.io.ExtensionFileFilter;
import ssac.util.io.Files;

/**
 * アプリケーションで使用するファイル選択ダイアログのインスタンスを管理するクラス。
 * このクラスのインスタンスはアプリケーションで唯一であり、
 * すべての操作は静的メソッドから行う。
 * 
 * @version 1.14	2009/12/09
 */
public class FileChooserManager
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static private FileChooserManager instance;
	
	//private JFileChooser	fileChooser;
	
	//private final FileFilter filterSource;
	private final FileFilter filterJar;
	//private final FileFilter filterZip;
	private final FileFilter filterCSV;
	private final FileFilter filterXML;
	private final FileFilter filterArg;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected FileChooserManager() {
		//filterSource = new ExtensionFileFilter(EditorMessages.getInstance().descExtSource,
		//										EditorMessages.getInstance().extSource);
		filterJar = new ExtensionFileFilter(EditorMessages.getInstance().descExtJar,
											EditorMessages.getInstance().extJar);
		//filterZip = new ExtensionFileFilter(EditorMessages.getInstance().descExtZip,
		//									EditorMessages.getInstance().extZip);
		filterCSV = new ExtensionFileFilter(EditorMessages.getInstance().descExtCSV,
											EditorMessages.getInstance().extCSV);
		filterXML = new ExtensionFileFilter(EditorMessages.getInstance().descExtXML,
											EditorMessages.getInstance().extXML);
		filterArg = new ExtensionFileFilter(EditorMessages.getInstance().descAADLArg,
											EditorMessages.getInstance().extAADLArg);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/*
	static public final File chooseOpenSourceFile(Component parentComponent, File initialFile) {
		FileChooserManager fcm = getInstance();
		JFileChooser fc = fcm.getFileChooser(JFileChooser.FILES_ONLY, false, initialFile, fcm.filterSource);
		fc.setDialogTitle(null);
		fc.setApproveButtonText(null);
		
		int ret = fc.showOpenDialog(parentComponent);
		if (ret != JFileChooser.APPROVE_OPTION) {
			// user canceled
			return null;
		}
		
		// selected
		return fc.getSelectedFile();
	}
	*/

	/*
	static public final File chooseSaveSourceFile(Component parentComponent, File initialFile) {
		FileChooserManager fcm = getInstance();
		JFileChooser fc = fcm.getFileChooser(JFileChooser.FILES_ONLY, false, initialFile, fcm.filterSource);
		fc.setDialogTitle(null);
		fc.setApproveButtonText(null);

		int ret = fc.showSaveDialog(parentComponent);
		if (ret != JFileChooser.APPROVE_OPTION) {
			// user canceled
			return null;
		}
		
		File saveFile = fc.getSelectedFile();
		if (saveFile == null) {
			// not selected
			return null;
		}
		if (!saveFile.isFile()) {
			FileFilter curFilter = fc.getFileFilter();
			if (curFilter != null && !curFilter.equals(fc.getAcceptAllFileFilter())) {
				String path = saveFile.getAbsolutePath();
				ExtensionFileFilter eff = (ExtensionFileFilter)curFilter;
				String[] exts = eff.getExtensions();
				boolean isValidExt = false;
				for (String ext : exts) {
					if (StringHelper.endsWith(path, ext, true)) {
						isValidExt = true;
						break;
					}
				}
				if (!isValidExt) {
					path = FileUtil.addExtension(path, eff.getDefaultExtension());
					saveFile = new File(path);
				}
			}
			if (saveFile.isDirectory()) {
				// Error : cannot choose directory
				String errmsg = AppMessages.getInstance().msgNotFile;
				AADLEditor.showErrorMessage(parentComponent, errmsg);
				return null;
			}
		}
		return saveFile;
	}
	*/
	
	static public final File chooseLibraryClassPath(Component parentComponent, File initialFile, String title) {
		JFileChooser fc = FileDialogManager.createFileChooser(JFileChooser.FILES_AND_DIRECTORIES,
																false, initialFile);
		fc.setDialogTitle(title);
		fc.setApproveButtonText(null);
		
		int ret = fc.showOpenDialog(parentComponent);
		if (ret != JFileChooser.APPROVE_OPTION) {
			// user canceled
			return null;
		}
		
		// selected
		return fc.getSelectedFile();
	}
	
	static public final File chooseJarFile(Component parentComponent, File initialFile, String title) {
		FileChooserManager fcm = getInstance();
		JFileChooser fc = FileDialogManager.createFileChooser(JFileChooser.FILES_ONLY, false,
																initialFile, fcm.filterJar);
		fc.setDialogTitle(title);
		fc.setApproveButtonText(null);
		
		int ret = fc.showOpenDialog(parentComponent);
		if (ret != JFileChooser.APPROVE_OPTION) {
			// user canceled
			return null;
		}
		
		// selected
		return fc.getSelectedFile();
	}
	
	static public final File chooseDestFile(Component parentComponent, File initialFile, String title) {
		FileChooserManager fcm = getInstance();
		JFileChooser fc = FileDialogManager.createFileChooser(JFileChooser.FILES_ONLY, false,
																initialFile, fcm.filterJar);
		fc.setDialogTitle(title);
		fc.setApproveButtonText(null);

		int ret = fc.showSaveDialog(parentComponent);
		if (ret != JFileChooser.APPROVE_OPTION) {
			// user canceled
			return null;
		}
		
		File saveFile = fc.getSelectedFile();
		if (saveFile == null) {
			// not selected
			return null;
		}
		if (!saveFile.isFile()) {
			FileFilter curFilter = fc.getFileFilter();
			if (curFilter != null && !curFilter.equals(fc.getAcceptAllFileFilter())) {
				String path = saveFile.getAbsolutePath();
				ExtensionFileFilter eff = (ExtensionFileFilter)curFilter;
				String[] exts = eff.getExtensions();
				boolean isValidExt = false;
				for (String ext : exts) {
					if (Strings.endsWithIgnoreCase(path, ext)) {
						isValidExt = true;
						break;
					}
				}
				if (!isValidExt) {
					path = Files.addExtension(path, eff.getDefaultExtension());
					saveFile = new File(path);
				}
			}
			if (saveFile.isDirectory()) {
				// Error : cannot choose directory
				String errmsg = EditorMessages.getInstance().msgNotFile;
				AADLEditor.showErrorMessage(parentComponent, errmsg);
				return null;
			}
		}
		return saveFile;
	}
	
	static public final File chooseAllFile(Component parentComponent, File initialFile, String title) {
		JFileChooser fc = FileDialogManager.createFileChooser(JFileChooser.FILES_ONLY, false, initialFile);
		fc.setDialogTitle(title);
		fc.setApproveButtonText(null);
		
		int ret = fc.showOpenDialog(parentComponent);
		if (ret != JFileChooser.APPROVE_OPTION) {
			// user canceled
			return null;
		}
		
		// selected
		return fc.getSelectedFile();
	}
	
	static public final File chooseArgumentFile(Component parentComponent, File initialFile) {
		FileChooserManager fcm = getInstance();
		JFileChooser fc = FileDialogManager.createFileChooser(JFileChooser.FILES_ONLY, false, initialFile,
															fcm.filterArg, fcm.filterCSV, fcm.filterXML);
		fc.setDialogTitle(null);
		fc.setApproveButtonText(null);
		
		fc.setDialogTitle(EditorMessages.getInstance().chooserTitleProgArgs);
		
		int ret = fc.showSaveDialog(parentComponent);
		if (ret != JFileChooser.APPROVE_OPTION) {
			// user canceled
			return null;
		}
		
		File saveFile = fc.getSelectedFile();
		if (saveFile == null) {
			// not selected
			return null;
		}
		if (!saveFile.isFile()) {
			FileFilter curFilter = fc.getFileFilter();
			if (curFilter != null && !curFilter.equals(fc.getAcceptAllFileFilter())) {
				String path = saveFile.getAbsolutePath();
				ExtensionFileFilter eff = (ExtensionFileFilter)curFilter;
				String[] exts = eff.getExtensions();
				boolean isValidExt = false;
				for (String ext : exts) {
					if (Strings.endsWithIgnoreCase(path, ext)) {
						isValidExt = true;
						break;
					}
				}
				if (!isValidExt) {
					path = Files.addExtension(path, eff.getDefaultExtension());
					saveFile = new File(path);
				}
			}
			if (saveFile.isDirectory()) {
				// Error : cannot choose directory
				String errmsg = EditorMessages.getInstance().msgNotFile;
				AADLEditor.showErrorMessage(parentComponent, errmsg);
				return null;
			}
		}
		return saveFile;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static synchronized public FileChooserManager getInstance() {
		if (instance == null) {
			instance = new FileChooserManager();
		}
		return instance;
	}
}
