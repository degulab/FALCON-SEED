/*
 * @(#)AbDtContainerEditController.java	1.0.0	2022/12/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.content.common;

import java.awt.Component;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import dtalge.container.editor.DtContainerEditor;
import dtalge.container.editor.DtContainerEditorMessages;
import dtalge.container.editor.content.IDtContainerDocument;
import dtalge.container.editor.content.IDtContainerEditController;
import dtalge.container.editor.content.IDtContainerEditView;
import dtalge.container.editor.content.common.table.DTCContentEditTableModelConversionError;
import dtalge.container.editor.view.DtContainerEditorFrame;
import dtalge.container.editor.view.dialog.DtContainerFileChooserManager;
import ssac.aadl.common.CommonMessages;
import ssac.util.Strings;
import ssac.util.Validations;
import ssac.util.io.ExtensionFileFilter;
import ssac.util.io.Files;
import ssac.util.logging.AppLogger;
import ssac.util.swing.Application;
import ssac.util.swing.table.SpreadSheetTable;

/**
 * データコンテナの編集用ドキュメントタイプを保持するコントローラーの共通実装。
 * 
 * @version 1.0.0
 */
public abstract class AbDtContainerEditController implements IDtContainerEditController
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
	protected final Map<IDtContainerDocument,IDtContainerEditView> _mapDocumentView;
	/**
	 * ファイル選択用のファイルフィルタ
	 */
	protected final ExtensionFileFilter[] _forOpenFilters;
	/**
	 * ファイル保存用のファイルフィルタ
	 */
	protected final ExtensionFileFilter[] _forSaveFilters;
	/**
	 * 最後に選択されたファイル
	 */
	protected File _lastSelectedFile = null;
	/**
	 *
	 */
	protected Font	_editorFont = null;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AbDtContainerEditController() {
		this._mapDocumentView = createDocumentViewMap();
		this._forOpenFilters = createForOpenFileFilters();
		this._forSaveFilters = createForSaveFileFilters();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * ファイル・オープン・ダイアログ用ファイルフィルタを返す。
	 * @return 定義されているファイルフィルタの配列を返す。未定義の場合は <tt>null</tt> を返す。
	 */
	public ExtensionFileFilter[] getOpenFileFilters() {
		return _forOpenFilters;
	}

	/**
	 * ファイル・セーブ・ダイアログ用ファイルフィルタを返す。
	 * @return	定義されているファイルフィルタの配列を返す。未定gの場合は <tt>null</tt> を返す。
	 */
	public ExtensionFileFilter[] getSaveFileFilters() {
		return _forSaveFilters;
	}

	/**
	 * このマネージャのインタフェースで、最後に選択されたファイルを返す。
	 * @return	ファイルオブジェクトを返す。未選択の場合は <tt>null</tt> を返す。
	 */
	public File getLastSelectedFile() {
		return _lastSelectedFile;
	}
	/**
	 * このプラグインの表示用アイコンを返す。
	 * アイコンが設定されていない場合は <tt>null</tt> を返す。
	 * @return	表示用アイコン
	 */
	public Icon getDisplayIcon() {
		return null;
	}
	
	/**
	 * このプラグインのデータ新規作成時の表示用アイコンを返す。
	 * アイコンが設定されていない場合は <tt>null</tt> を返す。
	 * @return	新規作成時の表示用アイコン
	 */
	public Icon getDisplayNewIcon() {
		return null;
	}
	
	/**
	 * このプラグインの標準エディタフォントを返す。
	 * このメソッドは、アプリケーション設定などで呼び出される。
	 * @return 標準のエディタフォント
	 */
	public Font getDefaultEditorFont() {
		return SpreadSheetTable.getDefaultTableFont();
	}
	
	/**
	 * このプラグインの、現在のエディタフォントを取得する。
	 * @return	現在のエディタフォント
	 */
	public Font getEditorFont() {
		return (_editorFont == null ? getDefaultEditorFont() : _editorFont);
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
		this._editorFont = font;
		
		// trigger font changed event
		for (IDtContainerEditView view : _mapDocumentView.values()) {
			view.onChangedEditorFont(this, font);
		}
		
		return true;
	}

	/**
	 * このマネージャにおける、直前の選択ファイルを設定する。
	 * ここで指定したファイルは、ファイルダイアログを表示する際の初期位置となる。
	 * @param file	初期位置とするファイルオブジェクト
	 */
	public void setLastSelectedFile(File file) {
		this._lastSelectedFile = file;
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
			for (ExtensionFileFilter eff : _forOpenFilters) {
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
	public IDtContainerEditView getView(IDtContainerDocument document) {
		return _mapDocumentView.get(document);
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
	public IDtContainerEditView putDocumentView(IDtContainerDocument document, IDtContainerEditView view) {
		Validations.validNotNull(document, "'document' is null.");
		Validations.validNotNull(view, "'view' is null.");
		//Validations.validArgument(view.getDocument()==document, "View is not include another document.");
		return _mapDocumentView.put(document, view);
	}
	
	/**
	 * 指定されたドキュメントをマネージャから削除する。
	 * このメソッドは、マネージャが管理するドキュメントとビューの関連付けを解除する。
	 * @param document	削除するドキュメント
	 * @return	削除したドキュメントに関連付けられていたビューを返す。指定したドキュメントが
	 * 			マネージャに存在しない場合は <tt>null</tt> を返す。
	 */
	public IDtContainerEditView removeDocument(IDtContainerDocument document) {
		return _mapDocumentView.remove(document);
	}
	
	/**
	 * 指定されたビューに関連付けられているドキュメントをマネージャから削除する。
	 * このメソッドは、マネージャが管理するドキュメントとビューの関連付けを解除する。
	 * @param view	削除するドキュメントを保持するビュー
	 * @return	ドキュメントを削除した場合は <tt>true</tt> を返す。
	 */
	public boolean removeDocument(IDtContainerEditView view) {
		if (view != null)
			return (_mapDocumentView.remove(view.getDocument()) != null);
		else
			return false;
	}
	
	/**
	 * 指定されたビューに関連付けられたドキュメントを既存のファイルに上書き保存する。
	 * 保存先ファイルが設定されていない場合は例外をスローする。
	 * @param parentComponent	メソッド内部でウィンドウを表示する際の親となるコンポーネント
	 * @param targetView	保存対象のドキュメントが関連付けられたビュー
	 * @return	保存に成功した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt> を返す。
	 * @throws IllegalArgumentException	ドキュメントが関連付けられていない場合、
	 * 										もしくは保存先ファイルが設定されていない場合
	 */
	public boolean onSaveComponent(Component parentComponent, IDtContainerEditView targetView)
	{
		// validation
		Validations.validNotNull(targetView, "'targetView' is null.");
		IDtContainerDocument document = targetView.getDocument();
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
		catch (DTCContentEditTableModelConversionError ex) {
			// 編集中のデータに不正な値が含まれている場合に、この例外がスローされる
			//--- 不正な値の位置を選択
			targetView.visibleAndSelectInvalidValueCell(ex);
			//--- エラーメッセージを表示
			final String errmsg = ex.getMessage();
			AppLogger.error(errmsg, ex);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					Application.showErrorMessage(parentComponent, errmsg);
				}
			});
			return false;
		}
		catch (UnsupportedEncodingException ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(
					DtContainerEditorMessages.MessageID.ERR_FILE_UNSUPPORTED_ENCODING, ex);
			AppLogger.error(errmsg, ex);
			Application.showErrorMessage(parentComponent, errmsg);
			return false;
		}
		catch (IOException ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(
					DtContainerEditorMessages.MessageID.ERR_FILE_READ, ex,
					targetFile.getAbsolutePath());
			AppLogger.error(errmsg, ex);
			Application.showErrorMessage(parentComponent, errmsg);
			return false;
		}
	}
	
	/**
	 * 指定されたビューに関連付けられたドキュメントを任意のファイルに保存する。
	 * ファイル選択ダイアログは、このメソッド内で表示する。
	 * ドキュメントが関連付けられていない場合は例外をスローする。
	 * @param parentComponent	メソッド内部でウィンドウを表示する際の親となるコンポーネント
	 * @param targetView	保存対象のドキュメントが関連付けられたビュー
	 * @return	保存に成功した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt> を返す。
	 * @throws IllegalArgumentException	ドキュメントが関連付けられていない場合
	 */
	public boolean onSaveAsComponent(Component parentComponent, IDtContainerEditView targetView)
	{
		// validation
		Validations.validNotNull(targetView, "'targetView' is null.");
		IDtContainerDocument document = targetView.getDocument();
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
		if (((DtContainerEditorFrame)targetView.getFrame()).isReadOnlyFile(targetFile)) {
			String errmsg = String.format(DtContainerEditorMessages.getInstance().msgCannotWriteCauseReadOnly, targetFile.getName());
			AppLogger.warn("AbComponentManager#onSaveAsComponent(\"" + targetFile + "\") : " + errmsg);
			DtContainerEditor.showWarningMessage(targetView.getFrame(), errmsg);
			return false;
		}
		
		// save
		try {
			document.save(targetFile);
			targetView.refreshEditingStatus();
			return true;
		}
		catch (DTCContentEditTableModelConversionError ex) {
			// 編集中のデータに不正な値が含まれている場合に、この例外がスローされる
			//--- 不正な値の位置を選択
			targetView.visibleAndSelectInvalidValueCell(ex);
			//--- エラーメッセージを表示
			final String errmsg = ex.getMessage();
			AppLogger.error(errmsg, ex);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					Application.showErrorMessage(parentComponent, errmsg);
				}
			});
			return false;
		}
		catch (FileNotFoundException ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(
					DtContainerEditorMessages.MessageID.ERR_FILE_NOTFILE, ex,
					targetFile.getAbsolutePath());
			AppLogger.error(errmsg, ex);
			Application.showErrorMessage(parentComponent, errmsg);
			return false;
		}
		catch (UnsupportedEncodingException ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(
					DtContainerEditorMessages.MessageID.ERR_FILE_UNSUPPORTED_ENCODING, ex);
			AppLogger.error(errmsg, ex);
			Application.showErrorMessage(parentComponent, errmsg);
			return false;
		}
		catch (IOException ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(
					DtContainerEditorMessages.MessageID.ERR_FILE_READ, ex,
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
	protected Map<IDtContainerDocument,IDtContainerEditView> createDocumentViewMap() {
		return new HashMap<IDtContainerDocument,IDtContainerEditView>();
	}

	/**
	 * ファイル・オープン・ダイアログ用のファイルフィルタを返す。
	 * このメソッドは、このインスタンスの初期化に呼び出される。
	 * @return	ファイルフィルタの配列
	 */
	protected ExtensionFileFilter[] createForOpenFileFilters() {
		return new ExtensionFileFilter[] {DtContainerFileChooserManager.getInstance().filterJSON};
	}

	/**
	 * ファイル・セーブ・ダイアログ用のファイルフィルタを返す。
	 * このメソッドは、このインスタンス初期化時に呼び出される。
	 * @return	ファイルフィルタの配列
	 */
	protected ExtensionFileFilter[] createForSaveFileFilters() {
		return new ExtensionFileFilter[] {DtContainerFileChooserManager.getInstance().filterJSON};
	}

	/**
	 * オープンするファイルを、ファイルダイアログで選択した結果を取得する。
	 * 
	 * @return 選択されたファイル。選択されていない場合は <tt>null</tt>
	 */
	protected File chooseOpenFile(Component parentComponent, String dialogTitle) {
		JFileChooser fc = Files.getFileChooser(JFileChooser.FILES_ONLY, false,
												_lastSelectedFile, (FileFilter[])_forOpenFilters);
		fc.setDialogTitle(dialogTitle);
		fc.setApproveButtonText(null);
		fc.setAcceptAllFileFilterUsed(true);
		
		int ret = fc.showOpenDialog(parentComponent);
		if (ret != JFileChooser.APPROVE_OPTION) {
			// user canceled
			return null;
		}
		
		// selected
		_lastSelectedFile = fc.getSelectedFile();
		return _lastSelectedFile;
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
													_lastSelectedFile, (FileFilter[])_forSaveFilters);
			fc.setDialogTitle(dialogTitle);
			fc.setApproveButtonText(null);
			fc.setAcceptAllFileFilterUsed(true);

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
			_lastSelectedFile = saveFile;

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
