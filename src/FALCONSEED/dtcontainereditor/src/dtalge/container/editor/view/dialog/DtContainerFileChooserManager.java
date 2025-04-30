/*
 * @(#)DtContainerFileChooserManager.java	2.0.0	2025/02/17
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtContainerFileChooserManager.java	1.1.0	2023/01/23
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtContainerFileChooserManager.java	1.0.0	2022/12/16
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.view.dialog;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import dtalge.container.editor.DtContainerEditorMessages;
import ssac.aadl.module.swing.FileDialogManager;
import ssac.util.io.ExtensionFileFilter;

/**
 * アプリケーションで使用するファイル選択ダイアログのインスタンスを管理するクラス。
 * このクラスのインスタンスはアプリケーションで唯一であり、
 * すべての操作は静的メソッドから行う。
 * 
 * @version 2.0.0
 */
public class DtContainerFileChooserManager
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static private DtContainerFileChooserManager _instance;
	
	public final ExtensionFileFilter	filterCSV;
	public final ExtensionFileFilter	filterXML;
	public final ExtensionFileFilter	filterJSON;
//	public final ExtensionFileFilter	filterDocument;
//	public final ExtensionFileFilter	filterBinder;
//	public final ExtensionFileFilter	filterSlipObjects;
//	public final ExtensionFileFilter	filterSlipList;
//	public final ExtensionFileFilter	filterSlipSingle;
	
	static {
	}

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected DtContainerFileChooserManager() {
		filterCSV = new ExtensionFileFilter(DtContainerEditorMessages.getInstance().descExtCSV,
											DtContainerEditorMessages.getInstance().extCSV);
		filterXML = new ExtensionFileFilter(DtContainerEditorMessages.getInstance().descExtXML,
											DtContainerEditorMessages.getInstance().extXML);
		filterJSON = new ExtensionFileFilter(DtContainerEditorMessages.getInstance().descExtJSON,
											DtContainerEditorMessages.getInstance().extJSON);
//		filterDocument = new ExtensionFileFilter(DtContainerEditorMessages.getInstance().descExtDocument,
//											DtContainerEditorMessages.getInstance().extDocument);
//		filterBinder = new ExtensionFileFilter(DtContainerEditorMessages.getInstance().descExtDtBinder,
//											DtContainerEditorMessages.getInstance().extDtBinder);
//		filterSlipObjects = new ExtensionFileFilter(DtContainerEditorMessages.getInstance().descExtSlipObject,
//											DtContainerEditorMessages.getInstance().extSlipObject);
//		filterSlipList = new ExtensionFileFilter(DtContainerEditorMessages.getInstance().descExtDtSlipList,
//											DtContainerEditorMessages.getInstance().extDtSlipList);
//		filterSlipSingle = new ExtensionFileFilter(DtContainerEditorMessages.getInstance().descExtDtSlipSingle,
//											DtContainerEditorMessages.getInstance().extDtSlipSingle);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public final JFileChooser createCsvFileOnlyChooser(boolean allowAllFileFilter, String title) {
		JFileChooser fc = FileDialogManager.createFileChooser(JFileChooser.FILES_ONLY, false, null, getInstance().filterCSV);
		if (title != null && !title.isEmpty()) {
			fc.setDialogTitle(title);
		}
		fc.setApproveButtonText(null);
		fc.setAcceptAllFileFilterUsed(allowAllFileFilter);
		return fc;
	}
	
	static public final JFileChooser createCsvXmlFileOnlyChooser(boolean allowAllFileFilter, String title) {
		JFileChooser fc = FileDialogManager.createFileChooser(JFileChooser.FILES_ONLY, false, null, getInstance().filterCSV, getInstance().filterXML);
		if (title != null && !title.isEmpty()) {
			fc.setDialogTitle(title);
		}
		fc.setApproveButtonText(null);
		fc.setAcceptAllFileFilterUsed(allowAllFileFilter);
		return fc;
	}
	
	static public final JFileChooser createFileOnlyChooser(boolean allowAllFileFilter, String title, FileFilter...filters) {
		JFileChooser fc = FileDialogManager.createFileChooser(JFileChooser.FILES_ONLY, false, null, filters);
		if (title != null && !title.isEmpty()) {
			fc.setDialogTitle(title);
		}
		fc.setApproveButtonText(null);
		fc.setAcceptAllFileFilterUsed(allowAllFileFilter);
		return fc;
	}
	
	/**
	 * データコンテナエディタでにおける JSON ファイルを対象とした、ファイル選択ダイアログを表示し、選択されたファイルを返す。
	 * @param parentComponent	親コンポーネント、指定しない場合は {@code null}
	 * @param initialFile		初期選択ファイル、指定しない場合は {@code null}
	 * @param title				ファイル選択ダイアログのタイトル、指定しない場合は {@code null}
	 * @return	選択されたファイル、キャンセルされた場合は {@code null}
	 */
	static public final File chooseJsonDocument(Component parentComponent, File initialFile, String title) {
		JFileChooser fc = FileDialogManager.createFileChooser(JFileChooser.FILES_ONLY, false, initialFile, getInstance().filterJSON);
		if (title != null && !title.isEmpty()) {
			fc.setDialogTitle(title);
		}
		fc.setAcceptAllFileFilterUsed(true);
		fc.setApproveButtonText(null);

		int ret = fc.showOpenDialog(parentComponent);
		if (ret != JFileChooser.APPROVE_OPTION) {
			// user canceled
			return null;
		}

		// selected
		return fc.getSelectedFile();
	}
	
//	/**
//	 * データコンテナエディタのすべてのドキュメントファイルを対象とした、ファイル選択ダイアログを表示し、選択されたファイルを返す。
//	 * @param parentComponent	親コンポーネント、指定しない場合は {@code null}
//	 * @param initialFile		初期選択ファイル、指定しない場合は {@code null}
//	 * @param title				ファイル選択ダイアログのタイトル、指定しない場合は {@code null}
//	 * @return	選択されたファイル、キャンセルされた場合は {@code null}
//	 */
//	static public final File chooseAnyDocument(Component parentComponent, File initialFile, String title) {
//		JFileChooser fc = FileDialogManager.createFileChooser(JFileChooser.FILES_ONLY, false, initialFile,
//								getInstance().filterDocument, getInstance().filterBinder, getInstance().filterSlipSingle, getInstance().filterJSON);
//		if (title != null && !title.isEmpty()) {
//			fc.setDialogTitle(title);
//		}
//		fc.setAcceptAllFileFilterUsed(true);
//		fc.setApproveButtonText(null);
//		
//		int ret = fc.showOpenDialog(parentComponent);
//		if (ret != JFileChooser.APPROVE_OPTION) {
//			// user canceled
//			return null;
//		}
//		
//		// selected
//		return fc.getSelectedFile();
//	}
//	
//	/**
//	 * データコンテナエディタの DtBinder ドキュメントファイルを対象とした、ファイル選択ダイアログを表示し、選択されたファイルを返す。
//	 * @param parentComponent	親コンポーネント、指定しない場合は {@code null}
//	 * @param initialFile		初期選択ファイル、指定しない場合は {@code null}
//	 * @param title				ファイル選択ダイアログのタイトル、指定しない場合は {@code null}
//	 * @return	選択されたファイル、キャンセルされた場合は {@code null}
//	 */
//	static public final File chooseDtBinderDocument(Component parentComponent, File initialFile, String title) {
//		JFileChooser fc = FileDialogManager.createFileChooser(JFileChooser.FILES_ONLY, false, initialFile,
//								getInstance().filterBinder, getInstance().filterJSON);
//		if (title != null && !title.isEmpty()) {
//			fc.setDialogTitle(title);
//		}
//		fc.setAcceptAllFileFilterUsed(true);
//		fc.setApproveButtonText(null);
//
//		int ret = fc.showOpenDialog(parentComponent);
//		if (ret != JFileChooser.APPROVE_OPTION) {
//			// user canceled
//			return null;
//		}
//
//		// selected
//		return fc.getSelectedFile();
//	}
//	
//	/**
//	 * データコンテナエディタの DtSlip ドキュメントファイルを対象とした、ファイル選択ダイアログを表示し、選択されたファイルを返す。
//	 * @param parentComponent	親コンポーネント、指定しない場合は {@code null}
//	 * @param initialFile		初期選択ファイル、指定しない場合は {@code null}
//	 * @param title				ファイル選択ダイアログのタイトル、指定しない場合は {@code null}
//	 * @return	選択されたファイル、キャンセルされた場合は {@code null}
//	 */
//	static public final File chooseDtSlipSingleDocument(Component parentComponent, File initialFile, String title) {
//		JFileChooser fc = FileDialogManager.createFileChooser(JFileChooser.FILES_ONLY, false, initialFile,
//								getInstance().filterSlipSingle, getInstance().filterJSON);
//		if (title != null && !title.isEmpty()) {
//			fc.setDialogTitle(title);
//		}
//		fc.setAcceptAllFileFilterUsed(true);
//		fc.setApproveButtonText(null);
//
//		int ret = fc.showOpenDialog(parentComponent);
//		if (ret != JFileChooser.APPROVE_OPTION) {
//			// user canceled
//			return null;
//		}
//
//		// selected
//		return fc.getSelectedFile();
//	}

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

	/* **
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
	/* */
	
	/* **
	static public final File chooseJarFile(Component parentComponent, File initialFile, String title) {
		DtContainerFileChooserManager fcm = getInstance();
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
	/* */
	
	/* **
	static public final File chooseDestFile(Component parentComponent, File initialFile, String title) {
		DtContainerFileChooserManager fcm = getInstance();
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
	/* */
	
	/* **
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
	/* */
	
	/* **
	static public final File chooseArgumentFile(Component parentComponent, File initialFile) {
		DtContainerFileChooserManager fcm = getInstance();
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
	/* */

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static synchronized public DtContainerFileChooserManager getInstance() {
		if (_instance == null) {
			_instance = new DtContainerFileChooserManager();
		}
		return _instance;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
