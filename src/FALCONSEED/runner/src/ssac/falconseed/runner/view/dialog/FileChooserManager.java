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
 * @(#)FileChooserManager.java	3.3.0	2016/05/06
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)FileChooserManager.java	2.0.0	2012/10/12
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)FileChooserManager.java	1.22	2012/08/22
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)FileChooserManager.java	1.20	2012/03/22
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)FileChooserManager.java	1.13	2011/11/11
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)FileChooserManager.java	1.10	2011/02/14
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)FileChooserManager.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.runner.view.dialog;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.module.ModuleFileManager;
import ssac.aadl.module.swing.FileDialogManager;
import ssac.falconseed.runner.ModuleRunner;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.util.Strings;
import ssac.util.io.ExtensionFileFilter;
import ssac.util.io.Files;
import ssac.util.io.VirtualFile;
import ssac.util.swing.Application;

/**
 * アプリケーションで使用するファイル選択ダイアログのインスタンスを管理するクラス。
 * このクラスのインスタンスはアプリケーションで唯一であり、
 * すべての操作は静的メソッドから行う。
 * 
 * @version 3.3.0
 */
public class FileChooserManager
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static protected File	_javaTmpDir = null;
	
	static protected File _recommendedDir = null;
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static private FileChooserManager instance;
	
	private final ExtensionFileFilter filterJar;
	//private final ExtensionFileFilter filterZip;
	private final ExtensionFileFilter filterCSV;
	private final ExtensionFileFilter filterXML;
	private final ExtensionFileFilter filterTXT;
	private final ExtensionFileFilter filterArg;
	private final ExtensionFileFilter filterExcel;
	private final ExtensionFileFilter filterMExecDef;
	private final ExtensionFileFilter filterPngImage;
	private final ExtensionFileFilter filterGraphVizDot;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected FileChooserManager() {
		filterJar = new ExtensionFileFilter(RunnerMessages.getInstance().descExtJar,
											RunnerMessages.getInstance().extJar);
		//filterZip = new ExtensionFileFilter(RunnerMessages.getInstance().descExtZip,
		//									RunnerMessages.getInstance().extZip);
		filterCSV = new ExtensionFileFilter(RunnerMessages.getInstance().descExtCSV,
											RunnerMessages.getInstance().extCSV);
		filterXML = new ExtensionFileFilter(RunnerMessages.getInstance().descExtXML,
											RunnerMessages.getInstance().extXML);
		filterTXT = new ExtensionFileFilter(RunnerMessages.getInstance().descExtTXT,
											RunnerMessages.getInstance().extTXT);
		filterExcel = new ExtensionFileFilter(RunnerMessages.getInstance().descExcel,
											  RunnerMessages.getInstance().extExcel);
		filterArg = new ExtensionFileFilter(RunnerMessages.getInstance().descAADLArg,
											RunnerMessages.getInstance().extAADLArg);
		filterMExecDef = new ExtensionFileFilter(RunnerMessages.getInstance().descMExecDefFile,
											RunnerMessages.getInstance().extMExecDefFile);
		filterPngImage = new ExtensionFileFilter(RunnerMessages.getInstance().descExtPNG,
											RunnerMessages.getInstance().extPNG);
		filterGraphVizDot = new ExtensionFileFilter(RunnerMessages.getInstance().descExtDOT,
											RunnerMessages.getInstance().extDOT);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 現在の推奨ディレクトリを示す抽象パスを返す。
	 * 推奨ディレクトリは、ファイル保存ダイアログなどで保存先が指定されていない場合
	 * などに、基準とするディレクトリとして利用される。
	 * @return 推奨ディレクトリの抽象パス
	 * @since 1.22
	 */
	static public File getRecommendedDirectory() {
		if (_recommendedDir != null) {
			return _recommendedDir;
		}
		else {
			return getJavaUserDirectory();
		}
	}

	/**
	 * 現在の推奨ディレクトリを、指定された抽象パスに設定する。
	 * 推奨ディレクトリは、ファイル保存ダイアログなどで保存先が指定されていない場合
	 * などに、基準とするディレクトリとして利用される。
	 * 抽象パスがローカルファイルではない場合、推奨ディレクトリは
	 * JAVAシステムのユーザーディレクトリとなる。
	 * 存在しないパスやディレクトリではないパスの場合、推奨ディレクトリは標準のものに設定される。
	 * @param file	新しい推奨ディレクトリの抽象パス
	 * @since 1.22
	 */
	static public void setRecommendedDirectory(Object file) {
		File rfile = ModuleFileManager.toJavaFile(file);
		if (rfile != null) {
			try {
				if (!rfile.exists() || !rfile.isDirectory()) {
					rfile = null;
				}
			} catch (Throwable ex) {
				rfile = null;
			}
		}
		_recommendedDir = rfile;
	}

	/**
	 * 指定された抽象パスが、JAVAシステムのテンポラリディレクトリ以下にある
	 * ファイルかどうかを判定する。指定された抽象パスがローカルファイルでは
	 * ない場合、このメソッドは <tt>false</tt> を返す。
	 * @param file	判定する抽象パスを保持するオブジェクト
	 * @return	テンポラリディレクトリ以下の抽象パスであれば <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 1.22
	 */
	static public boolean isSystemTemporaryFile(Object file) {
		File retFile = ModuleFileManager.toJavaFile(file);
		if (retFile == null)
			return false;
		
		return Files.isDescendingFrom(retFile, getJavaTemporaryDirectory());
	}

	/**
	 * JAVAシステムのテンポラリディレクトリを示す抽象パスを取得する。
	 * @return	テンポラリディレクトリの抽象パスを示す <code>VirtualFile</code> オブジェクト
	 * @since 1.22
	 */
	static public VirtualFile getSystemTemporaryDirectory() {
		return ModuleFileManager.fromJavaFile(getJavaTemporaryDirectory());
	}

	/**
	 * JAVAシステムのテンポラリディレクトリを示す抽象パスを取得する。
	 * テンポラリディレクトリは、システムプロパティの &quot;java.io.tmpdir&quot; が返すパスとなる。
	 * @return	テンポラリディレクトリの抽象パスを示す <code>File</code> オブジェクト
	 * @since 1.22
	 */
	static public File getJavaTemporaryDirectory() {
		if (_javaTmpDir == null) {
			String strTmpDir = System.getProperty("java.io.tmpdir");
			_javaTmpDir = new File(strTmpDir);
		}
		return _javaTmpDir;
	}
	
	/**
	 * JAVAシステムのユーザーディレクトリを示す抽象パスを取得する。
	 * @return	ユーザーディレクトリの抽象パスを示す <code>File</code> オブジェクト
	 * @since 1.22
	 */
	static public VirtualFile getSystemUserDirectory() {
		return ModuleFileManager.fromJavaFile(getJavaUserDirectory());
	}

	/**
	 * JAVAシステムのユーザーディレクトリを示す抽象パスを取得する。
	 * ユーザーディレクトリは、システムプロパティの &quot;user.dir&quot; が返すパスとなる。
	 * @return	ユーザーディレクトリの抽象パスを示す <code>File</code> オブジェクト
	 * @since 1.22
	 */
	static public File getJavaUserDirectory() {
		String strUserDir = System.getProperty("user.dir");
		return new File(strUserDir);
	}
	
	static public void setLastChooseFile(String prefix, File file) {
		AppSettings.getInstance().setLastFile(prefix, file);
	}
	
	static public File getLastChooseFile(String prefix) {
		return AppSettings.getInstance().getLastFile(prefix);
	}
	
	static public void setLastChooseDocumentFile(File file) {
		setLastChooseFile(AppSettings.DOCUMENT, file);
	}
	
	static public File getLastChooseDocumentFile() {
		return getLastChooseFile(AppSettings.DOCUMENT);
	}
	
	static public File getInitialChooseFile(String prefix, Object initialFile) {
		File retFile = ModuleFileManager.toJavaFile(initialFile);
		if (retFile == null) {
			retFile = getLastChooseFile(prefix);
		}
		return retFile;
	}
	
	static public File getInitialDocumentFile(Object initialFile) {
		File retFile = ModuleFileManager.toJavaFile(initialFile);
		if (retFile == null) {
			retFile = getLastChooseDocumentFile();
			if (isSystemTemporaryFile(retFile)) {
				// 直前のドキュメントファイルがテンポラリの場合、推奨ディレクトリへ変更する。
				retFile = new File(getRecommendedDirectory(), retFile.getName());
			}
		}
		else if (isSystemTemporaryFile(retFile)) {
			// 指定された初期ファイルパスがテンポラリの場合、直前の選択ドキュメントに変更する。
			retFile = getLastChooseDocumentFile();
			if (retFile != null && isSystemTemporaryFile(retFile)) {
				// 直前のドキュメントファイルがテンポラリの場合、推奨ディレクトリへ変更する。
				retFile = new File(getRecommendedDirectory(), retFile.getName());
			}
		}
		
		return retFile;
	}
	
	static public final ExtensionFileFilter getCsvFileFilter() {
		return getInstance().filterCSV;
	}
	
	static public final ExtensionFileFilter getXmlFileFilter() {
		return getInstance().filterXML;
	}
	
	static public final ExtensionFileFilter getTextFileFilter() {
		return getInstance().filterTXT;
	}
	
	static public final ExtensionFileFilter getExcelFileFilter() {
		return getInstance().filterExcel;
	}
	
	static public final ExtensionFileFilter getArgumentFileFilter() {
		return getInstance().filterArg;
	}
	
	static public final ExtensionFileFilter getMExecDefFileFilter() {
		return getInstance().filterMExecDef;
	}
	
	static public final ExtensionFileFilter getPngImageFileFilter() {
		return getInstance().filterPngImage;
	}
	
	static public final ExtensionFileFilter getGraphVizDotFileFilter() {
		return getInstance().filterGraphVizDot;
	}
	
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
				String errmsg = CommonMessages.getInstance().msgNotFile;
				ModuleRunner.showErrorMessage(parentComponent, errmsg);
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
	
	static public final File chooseArgDirectory(Component parentComponent, File initialFile) {
		return chooseSaveDirectory(parentComponent, initialFile,
									RunnerMessages.getInstance().chooserTitleProgArgs,
									null);
	}
	
	static public final File chooseArgCsvFile(Component parentComponent, File initialFile) {
		FileChooserManager fcm = getInstance();
		return chooseArgumentFile(parentComponent, true, initialFile, fcm.filterCSV);
	}
	
	static public final File chooseArgXmlFile(Component parentComponent, File initialFile) {
		FileChooserManager fcm = getInstance();
		return chooseArgumentFile(parentComponent, true, initialFile, fcm.filterXML);
	}
	
	static public final File chooseArgTextFile(Component parentComponent, File initialFile) {
		FileChooserManager fcm = getInstance();
		return chooseArgumentFile(parentComponent, true, initialFile, fcm.filterTXT);
	}
	
	static public final File chooseArgumentFile(Component parentComponent, File initialFile) {
		FileChooserManager fcm = getInstance();
		return chooseArgumentFile(parentComponent, true, initialFile, fcm.filterArg, fcm.filterCSV, fcm.filterTXT, fcm.filterXML);
	}
	
	static public final File chooseArgumentDirOrFile(Component parentComponent, File initialFile) {
		FileChooserManager fcm = getInstance();
		return chooseArgumentFile(parentComponent, false, initialFile, fcm.filterArg, fcm.filterCSV, fcm.filterTXT, fcm.filterXML);
	}
	
	/**
	 * 開くファイルを、ファイルダイアログで選択した結果を取得する。
	 * 
	 * @return 選択されたファイル。選択されていない場合は <tt>null</tt>
	 */
	static public final File chooseOpenFile(Component parentComponent, String dialogTitle, boolean multiple, File initialFile, FileFilter...filters)
	{
		JFileChooser fc = FileDialogManager.createFileChooser(JFileChooser.FILES_ONLY, multiple, initialFile, filters);
		fc.setDialogTitle(dialogTitle);
		fc.setApproveButtonText(null);
		
		int ret = fc.showOpenDialog(parentComponent);
		if (ret != JFileChooser.APPROVE_OPTION) {
			// user canceled
			return null;
		}
		
		// selected
		return fc.getSelectedFile();
	}
	
	/**
	 * 保存先ファイルを、ファイルダイアログで選択した結果を取得する。
	 * このメソッドは、標準的な上書き確認メッセージを表示する。
	 * 
	 * @return 選択されたファイル。選択されていない場合は <tt>null</tt>
	 * @since 1.20
	 */
	static public final File chooseSaveFileAndCheckOverwrite(Component parentComponent, String dialogTitle, File initialFile, FileFilter...filters)
	{
		boolean doRepeat;
		File selectedFile = initialFile;
		
		do {
			doRepeat = false;
			selectedFile = chooseSaveFile(parentComponent, dialogTitle, selectedFile, filters);
			if (selectedFile == null) {
				return null;
			}
			
			if (selectedFile.exists()) {
				int retAsk = confirmFileOverwrite(parentComponent, selectedFile, false);
				if (retAsk == JOptionPane.CANCEL_OPTION) {
					// user canceled
					return null;
				}
				else if (retAsk == JOptionPane.NO_OPTION) {
					// repeat choose file
					doRepeat = true;
				}
				else {
					// allow overwrite
					doRepeat = false;
				}
			}
		} while(doRepeat);

		return selectedFile;
	}

	/**
	 * 保存先ファイルを、ファイルダイアログで選択した結果を取得する。
	 * 
	 * @return 選択されたファイル。選択されていない場合は <tt>null</tt>
	 */
	static public final File chooseSaveFile(Component parentComponent, String dialogTitle, File initialFile, FileFilter...filters)
	{
		JFileChooser fc = FileDialogManager.createFileChooser(JFileChooser.FILES_ONLY, false, initialFile, filters);
		fc.setDialogTitle(dialogTitle);
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
					path = path + eff.getDefaultExtension();
					saveFile = new File(path);
				}
			}
			if (saveFile.isDirectory()) {
				// Error : cannot choose directory
				Application.showErrorMessage(parentComponent, CommonMessages.getInstance().msgNotFile);
				return null;
			}
		}
		
		return saveFile;
	}
	
	static public final File chooseSaveDirectory(Component parentComponent, File initialFile, String title, String aproveText)
	{
		JFileChooser chooser;
		if (0 <= System.getProperty("os.name").indexOf("Mac")) {
			//--- for Mac OS X
			chooser = new JFileChooser(){
				@Override
				public void approveSelection() {
					File f = getSelectedFile();
					
					if (f == null) {
						return;		// no selection
					}
					
					if (!f.isDirectory()) {
						// can select an existing directory
						JOptionPane.showMessageDialog(this,
								String.format(RunnerMessages.getInstance().msgLongSelectExistingFolder, f.getAbsolutePath()),
								CommonMessages.getInstance().msgboxTitleError,
								JOptionPane.ERROR_MESSAGE);
						return;		// cancel to approve
					}

					// approve selection
					super.approveSelection();
				}
			};
		} else {
			//--- Other OS
			chooser = new JFileChooser();
		}
		
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setMultiSelectionEnabled(false);
		
		// setup initial file
		chooser.setSelectedFile(initialFile);

		if (title != null) {
			chooser.setDialogTitle(title);
		}
		if (aproveText != null) {
			chooser.setApproveButtonText(aproveText);
		}
		
		int ret = chooser.showSaveDialog(parentComponent);
		if (ret == JFileChooser.APPROVE_OPTION) {
			// selected
			return chooser.getSelectedFile();
		} else {
			// User canceled
			return null;
		}
	}

	/**
	 * 指定されたファイルについて、上書き確認のメッセージを表示する。
	 * このメソッドは {@link javax.swing.JOptionPane#showConfirmDialog(Component, Object, String, int)} の結果を返す。
	 * メッセージに表示するボタンは、<em>withoutCancel</em> が <tt>true</tt> の場合は {@link javax.swing.JOptionPane#YES_NO_OPTION}、
	 * <em>withoutCancel</em> が <tt>false</tt> の場合は {@link javax.swing.JOptionPane#YES_NO_CANCEL_OPTION} となる。
	 * 指定されたファイルが存在しない場合、このメソッドは <code>JOptionPane.YES_OPTION</code> を返す。
	 * @param parentComponent	親となるコンポーネント
	 * @param targetFile		検査するファイル
	 * @param withoutCancel		キャンセルボタンを表示しない場合は <tt>true<tt>、キャンセルボタンを表示する場合は <tt>false</tt>
	 * @return	<code>JOptionPane</code> の戻り値
	 */
	static public final int confirmFileOverwrite(Component parentComponent, File targetFile, boolean withoutCancel) {
		return confirmFileOverwrite(parentComponent, targetFile, UIManager.getString("OptionPane.titleText"), withoutCancel);
	}
	
	/**
	 * 指定されたファイルについて、上書き確認のメッセージを表示する。
	 * このメソッドは {@link javax.swing.JOptionPane#showConfirmDialog(Component, Object, String, int)} の結果を返す。
	 * メッセージに表示するボタンは、<em>withoutCancel</em> が <tt>true</tt> の場合は {@link javax.swing.JOptionPane#YES_NO_OPTION}、
	 * <em>withoutCancel</em> が <tt>false</tt> の場合は {@link javax.swing.JOptionPane#YES_NO_CANCEL_OPTION} となる。
	 * 指定されたファイルが存在しない場合、このメソッドは <code>JOptionPane.YES_OPTION</code> を返す。
	 * @param parentComponent	親となるコンポーネント
	 * @param targetFile		検査するファイル
	 * @param title				ダイアログのタイトル
	 * @param withoutCancel		キャンセルボタンを表示しない場合は <tt>true<tt>、キャンセルボタンを表示する場合は <tt>false</tt>
	 * @return	<code>JOptionPane</code> の戻り値
	 */
	static public final int confirmFileOverwrite(Component parentComponent, File targetFile, String title, boolean withoutCancel) {
		if (targetFile.exists()) {
			String msg = String.format(CommonMessages.getInstance().confirmOverwriteFile, targetFile.getAbsolutePath());
			int option = (withoutCancel ? JOptionPane.YES_NO_OPTION : JOptionPane.YES_NO_CANCEL_OPTION);
			return JOptionPane.showConfirmDialog(parentComponent, msg, title, option);
		} else {
			return JOptionPane.YES_OPTION;
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static protected final File chooseArgumentFile(Component parentComponent, boolean filesOnly, File initialFile, FileFilter...filters) {
		int selectionMode = filesOnly ? JFileChooser.FILES_ONLY : JFileChooser.FILES_AND_DIRECTORIES;
		JFileChooser fc = FileDialogManager.createFileChooser(selectionMode, false, initialFile, filters);
		fc.setDialogTitle(null);
		fc.setApproveButtonText(null);
		
		fc.setDialogTitle(RunnerMessages.getInstance().chooserTitleProgArgs);
		
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
		if (selectionMode==JFileChooser.FILES_ONLY && saveFile.isDirectory()) {
			// Error : cannot choose directory
			String errmsg = CommonMessages.getInstance().msgNotFile;
			ModuleRunner.showErrorMessage(parentComponent, errmsg);
			return null;
		}
		else if (!saveFile.isFile() && !saveFile.isDirectory()) {
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
			if (selectionMode==JFileChooser.FILES_ONLY && saveFile.isDirectory()) {
				// Error : cannot choose directory
				String errmsg = CommonMessages.getInstance().msgNotFile;
				ModuleRunner.showErrorMessage(parentComponent, errmsg);
				return null;
			}
		}
		return saveFile;
	}
	
	static synchronized public FileChooserManager getInstance() {
		if (instance == null) {
			instance = new FileChooserManager();
		}
		return instance;
	}
}
