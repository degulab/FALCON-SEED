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
 *  Copyright 2007-2010  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)AbComponentManager.java	1.17	2010/11/21
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbComponentManager.java	1.14	2009/12/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbComponentManager.java	1.10	2008/12/03
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.plugin;

import java.awt.Component;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.editor.AADLEditor;
import ssac.aadl.editor.EditorMessages;
import ssac.aadl.editor.document.IEditorDocument;
import ssac.aadl.editor.view.EditorFrame;
import ssac.aadl.editor.view.IEditorView;
import ssac.util.Strings;
import ssac.util.Validations;
import ssac.util.io.ExtensionFileFilter;
import ssac.util.io.Files;
import ssac.util.logging.AppLogger;
import ssac.util.swing.Application;


/**
 * コンポーネント・マネージャの共通機能の実装。
 * 
 * @version 1.17	2010/11/21
 * 
 * @since 1.10
 */
public abstract class AbComponentManager implements IComponentManager
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final ExtensionFileFilter[] EMPTY_FILTERS = new ExtensionFileFilter[0];

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/**
	 * ドキュメントとビューのマップ
	 */
	protected final Map<IEditorDocument,IEditorView> mapDocumentView;
	/**
	 * ファイル選択用のファイルフィルタ
	 */
	protected final ExtensionFileFilter[] forOpenFilters;
	/**
	 * ファイル保存用のファイルフィルタ
	 */
	protected final ExtensionFileFilter[] forSaveFilters;
	/**
	 * 最後に選択されたファイル
	 */
	protected File lastSelectedFile = null;
	/**
	 *
	 */
	protected Font	editorFont = null;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AbComponentManager() {
		this.mapDocumentView = createDocumentViewMap();
		this.forOpenFilters = createForOpenFileFilters();
		this.forSaveFilters = createForSaveFileFilters();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * ファイル・オープン・ダイアログ用ファイルフィルタを返す。
	 * @return 定義されているファイルフィルタの配列を返す。未定義の場合は <tt>null</tt> を返す。
	 */
	public ExtensionFileFilter[] getOpenFileFilters() {
		return forOpenFilters;
	}

	/**
	 * ファイル・セーブ・ダイアログ用ファイルフィルタを返す。
	 * @return	定義されているファイルフィルタの配列を返す。未定gの場合は <tt>null</tt> を返す。
	 */
	public ExtensionFileFilter[] getSaveFileFilters() {
		return forSaveFilters;
	}

	/**
	 * このマネージャのインタフェースで、最後に選択されたファイルを返す。
	 * @return	ファイルオブジェクトを返す。未選択の場合は <tt>null</tt> を返す。
	 */
	public File getLastSelectedFile() {
		return lastSelectedFile;
	}

	/**
	 * このマネージャにおける、直前の選択ファイルを設定する。
	 * ここで指定したファイルは、ファイルダイアログを表示する際の初期位置となる。
	 * @param file	初期位置とするファイルオブジェクト
	 */
	public void setLastSelectedFile(File file) {
		this.lastSelectedFile = file;
	}
	
	/**
	 * このプラグインの、現在のエディタフォントを取得する。
	 * @return	現在のエディタフォントを返す。設定されていない場合は <tt>null</tt> を返す。
	 */
	public Font getEditorFont() {
		return (editorFont == null ? getDefaultEditorFont() : editorFont);
	}
	
	/**
	 * このプラグインのエディタフォントを設定する。
	 * 現在のフォントと異なるフォントが設定された場合、このマネージャが管理する
	 * 全てのビューに新しいフォントが適用される。
	 * @param font	新しいエディタフォント
	 * @return	新しいフォントが適用された場合に <tt>true</tt> を返す。
	 * 			変更されなかった場合は <tt>false</tt> を返す。
	 * @throws NullPointerException	<code>font</code> が <tt>null</tt> の場合
	 */
	public boolean setEditorFont(Font font) {
		Validations.validNotNull(font);
		if (font.equals(getEditorFont())) {
			// same font
			return false;
		}
		
		// update font
		this.editorFont = font;
		
		// trigger font changed event
		for (IEditorView view : mapDocumentView.values()) {
			view.onChangedEditorFont(this, font);
		}
		
		return true;
	}

	/**
	 * 指定されたファイルが、このマネージャでサポートされているかを判定する。
	 * このメソッドの実装では、ファイルオープン用ファイルフィルタに格納されている
	 * 拡張子に、指定されたファイルの拡張子が一致するかで判定する。
	 * このとき、拡張子の大文字／小文字は区別しない。
	 * 
	 * @param targetFile	判定するファイル
	 * @return	サポートしている場合は <tt>true</tt>
	 */
	public boolean isSupportedFileType(File targetFile) {
		if (targetFile != null) {
			String fname = targetFile.getName();
			for (ExtensionFileFilter eff : forOpenFilters) {
				String[] exts = eff.getExtensions();
				for (String ext : exts) {
					if (Strings.endsWithIgnoreCase(fname, ext)) {
						// matched open file extension
						return true;
					}
				}
			}
		}
		
		// not supported targetFile
		return false;
	}
	
	/**
	 * 指定のドキュメントに対応するビューを取得する。
	 * @param document	ドキュメント
	 * @return	ドキュメントに関連付けられたビュー
	 */
	public IEditorView getView(IEditorDocument document) {
		return mapDocumentView.get(document);
	}

	/**
	 * ドキュメントとビューの対応をマップに登録する。すでに同一のドキュメントが登録済みの
	 * 場合、指定したビューに置き換える。
	 * @param document	登録するドキュメント
	 * @param view		登録するビュー
	 * @return	すでに登録されていたビューを返す。ドキュメントが新規登録の場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	ビューに格納されているドキュメントが指定されたドキュメントと異なる場合
	 */
	public IEditorView putDocumentView(IEditorDocument document, IEditorView view) {
		Validations.validNotNull(document, "'document' is null.");
		Validations.validNotNull(view, "'view' is null.");
		//Validations.validArgument(view.getDocument()==document, "View is not include another document.");
		return mapDocumentView.put(document, view);
	}
	
	/**
	 * 指定されたドキュメントをマネージャから削除する。
	 * このメソッドは、マネージャが管理するドキュメントとビューの関連付けを解除する。
	 * @param document	削除するドキュメント
	 * @return	削除したドキュメントに関連付けられていたビューを返す。指定したドキュメントが
	 * 			マネージャに存在しない場合は <tt>null</tt> を返す。
	 */
	public IEditorView removeDocument(IEditorDocument document) {
		return mapDocumentView.remove(document);
	}
	
	/**
	 * 指定されたビューに関連付けられているドキュメントをマネージャから削除する。
	 * このメソッドは、マネージャが管理するドキュメントとビューの関連付けを解除する。
	 * @param view	削除するドキュメントを保持するビュー
	 * @return	ドキュメントを削除した場合は <tt>true</tt> を返す。
	 */
	public boolean removeDocument(IEditorView view) {
		if (view != null)
			return (mapDocumentView.remove(view.getDocument()) != null);
		else
			return false;
	}

	public boolean onSaveComponent(Component parentComponent, IEditorView targetView)
	{
		// validation
		Validations.validNotNull(targetView, "'targetView' is null.");
		IEditorDocument document = targetView.getDocument();
		Validations.validArgument(document != null, "document is null.");
		Validations.validArgument(getSupportedDocumentClass().isInstance(document),
				"document is not %s class", getSupportedDocumentClass().getName());
		Validations.validArgument(document.hasTargetFile(), "document has no target file.");
		
		// save
		File targetFile = document.getTargetFile();
		try {
			document.save(targetFile);
			targetView.refreshEditingStatus();
			return true;
		}
		catch (UnsupportedEncodingException ex) {
			String errmsg = EditorMessages.getErrorMessage(
					EditorMessages.MessageID.ERR_FILE_UNSUPPORTED_ENCODING, ex);
			AppLogger.error(errmsg, ex);
			Application.showErrorMessage(parentComponent, errmsg);
			return false;
		}
		catch (IOException ex) {
			String errmsg = EditorMessages.getErrorMessage(
					EditorMessages.MessageID.ERR_FILE_READ, ex,
					targetFile.getAbsolutePath());
			AppLogger.error(errmsg, ex);
			Application.showErrorMessage(parentComponent, errmsg);
			return false;
		}
	}

	public boolean onSaveAsComponent(Component parentComponent, IEditorView targetView)
	{
		// validation
		Validations.validNotNull(targetView, "'targetView' is null.");
		IEditorDocument document = targetView.getDocument();
		Validations.validArgument(document != null, "document is null.");
		Validations.validArgument(getSupportedDocumentClass().isInstance(document),
				"document is not %s class", getSupportedDocumentClass().getName());
		
		// choose target file
		File targetFile = chooseSaveFile(parentComponent, getFileChooserTitleForSave());
		if (targetFile == null) {
			// not selected
			return false;
		}
		
		// check read only
		if (((EditorFrame)targetView.getFrame()).isReadOnlyFile(targetFile)) {
			String errmsg = String.format(EditorMessages.getInstance().msgCannotWriteCauseReadOnly, targetFile.getName());
			AppLogger.warn("AbComponentManager#onSaveAsComponent(\"" + targetFile + "\") : " + errmsg);
			AADLEditor.showWarningMessage(targetView.getFrame(), errmsg);
			return false;
		}
		
		// save
		try {
			document.save(targetFile);
			targetView.refreshEditingStatus();
			return true;
		}
		catch (FileNotFoundException ex) {
			String errmsg = EditorMessages.getErrorMessage(
					EditorMessages.MessageID.ERR_FILE_NOTFILE, ex,
					targetFile.getAbsolutePath());
			AppLogger.error(errmsg, ex);
			Application.showErrorMessage(parentComponent, errmsg);
			return false;
		}
		catch (UnsupportedEncodingException ex) {
			String errmsg = EditorMessages.getErrorMessage(
					EditorMessages.MessageID.ERR_FILE_UNSUPPORTED_ENCODING, ex);
			AppLogger.error(errmsg, ex);
			Application.showErrorMessage(parentComponent, errmsg);
			return false;
		}
		catch (IOException ex) {
			String errmsg = EditorMessages.getErrorMessage(
					EditorMessages.MessageID.ERR_FILE_READ, ex,
					targetFile.getAbsolutePath());
			AppLogger.error(errmsg, ex);
			Application.showErrorMessage(parentComponent, errmsg);
			return false;
		}
	}

	//------------------------------------------------------------
	// Implement IEditorMenuActionHandler interfaces
	//------------------------------------------------------------

	/**
	 * メニュー項目の選択時に呼び出されるハンドラ・メソッド。
	 * このメソッドはメニューアクションから呼び出される。
	 * このメソッドが呼び出されると、アクティブドキュメント、アクティブドキュメントの
	 * コンポーネント、このフレームのデフォルト処理の順に、ハンドラが呼び出される。
	 * 
	 * @param command	このイベント要因のコマンド文字列
	 * @param source	このイベント要因のソースオブジェクト。
	 * 					ソースオブジェクトが未定義の場合は <tt>null</tt>。
	 * @param action	このイベント要因のソースオブジェクトに割り当てられたアクション。
	 * 					アクションが未定義の場合は <tt>null</tt>。
	 * @return	このハンドラ内で処理が完結した場合は <tt>true</tt> を返す。イベントシーケンスの
	 * 			別のハンドラに処理を委譲する場合は <tt>false</tt> を返す。
	 */
	public boolean onProcessMenuSelection(String command, Object source, Action action) {
		return false;
	}

	/**
	 * メニュー項目の更新要求時に呼び出されるハンドラ・メソッド。
	 * このメソッドはメニュー項目更新メソッドから呼び出される。
	 * このメソッドが呼び出されると、アクティブドキュメント、アクティブドキュメントの
	 * コンポーネント、このフレームのデフォルト処理の順に、ハンドラが呼び出される。
	 * 
	 * @param command	このイベント要因のコマンド文字列
	 * @param source	このイベント要因のソースオブジェクト。
	 * 					ソースオブジェクトが未定義の場合は <tt>null</tt>。
	 * @param action	このイベント要因のソースオブジェクトに割り当てられたアクション。
	 * 					アクションが未定義の場合は <tt>null</tt>。
	 * @return	このハンドラ内で処理が完結した場合は <tt>true</tt> を返す。イベントシーケンスの
	 * 			別のハンドラに処理を委譲する場合は <tt>false</tt> を返す。
	 */
	public boolean onProcessMenuUpdate(String command, Object source, Action action) {
		return false;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * ファイルオープン用ファイル選択ダイアログのタイトルとなる文字列を返す。
	 * @return	ファイルダイアログタイトル文字列。デフォルトとする場合は <tt>null</tt> を返す。
	 */
	abstract protected String getFileChooserTitleForOpen();
	/**
	 * ファイル保存用ファイルダイアログのタイトルとなる文字列を返す。
	 * @return	ファイルダイアログタイトル文字列。デフォルトとする場合は <tt>null</tt> を返す。
	 */
	abstract protected String getFileChooserTitleForSave();

	/**
	 * ドキュメントとビューのマップインスタンスを生成する。
	 * このメソッドは、このインスタンスの初期化時に呼び出される。
	 * @return ドキュメント・ビューのマップを格納するオブジェクト
	 */
	protected Map<IEditorDocument,IEditorView> createDocumentViewMap() {
		return new HashMap<IEditorDocument,IEditorView>();
	}

	/**
	 * ファイル・オープン・ダイアログ用のファイルフィルタを返す。
	 * このメソッドは、このインスタンスの初期化に呼び出される。
	 * @return	ファイルフィルタの配列
	 */
	protected ExtensionFileFilter[] createForOpenFileFilters() {
		return EMPTY_FILTERS;
	}

	/**
	 * ファイル・セーブ・ダイアログ用のファイルフィルタを返す。
	 * このメソッドは、このインスタンス初期化時に呼び出される。
	 * @return	ファイルフィルタの配列
	 */
	protected ExtensionFileFilter[] createForSaveFileFilters() {
		return EMPTY_FILTERS;
	}

	/**
	 * オープンするファイルを、ファイルダイアログで選択した結果を取得する。
	 * 
	 * @return 選択されたファイル。選択されていない場合は <tt>null</tt>
	 */
	protected File chooseOpenFile(Component parentComponent, String dialogTitle) {
		JFileChooser fc = Files.getFileChooser(JFileChooser.FILES_ONLY, false,
												lastSelectedFile, (FileFilter[])forOpenFilters);
		fc.setDialogTitle(dialogTitle);
		fc.setApproveButtonText(null);
		
		int ret = fc.showOpenDialog(parentComponent);
		if (ret != JFileChooser.APPROVE_OPTION) {
			// user canceled
			return null;
		}
		
		// selected
		lastSelectedFile = fc.getSelectedFile();
		return lastSelectedFile;
	}

	/**
	 * 保存先となるAADLマクロファイルを、ファイルダイアログで選択した結果を取得する。
	 * 保存先のファイルがすでに存在する場合、このメソッドは上書き確認メッセージを、
	 * [Yes]、[No]、[Cancel]の 3 つのオプションを指定して表示する。このとき、
	 * [Yes]ボタンが押下された場合は選択したファイルの抽象パスを、[Cancel]ボタンが
	 * 押下された場合は <tt>null</tt> を返す。[No]ボタンが押下された場合、再度
	 * ファイルダイアログを表示する。
	 * 
	 * @return 選択されたファイル。選択されていない場合は <tt>null</tt>
	 */
	protected File chooseSaveFile(Component parentComponent, String dialogTitle) {
		boolean doRepeat;
		File saveFile = null;
		do {
			doRepeat = false;
			JFileChooser fc = Files.getFileChooser(JFileChooser.FILES_ONLY, false,
													lastSelectedFile, (FileFilter[])forSaveFilters);
			fc.setDialogTitle(dialogTitle);
			fc.setApproveButtonText(null);

			int ret = fc.showSaveDialog(parentComponent);
			if (ret != JFileChooser.APPROVE_OPTION) {
				// user canceled
				return null;
			}

			saveFile = fc.getSelectedFile();
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
			lastSelectedFile = saveFile;

			// 上書き確認
			if (saveFile.exists()) {
				String msg = String.format(CommonMessages.getInstance().confirmOverwriteFile, saveFile.getAbsolutePath());
				int retAsk = JOptionPane.showConfirmDialog(parentComponent, msg);
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
		} while (doRepeat);
		
		return saveFile;
	}
}
