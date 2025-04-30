/*
 * @(#)DCEditControllerManager.java	1.0.0	2022/12/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.content;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import dtalge.container.editor.content.binder.DtBinderEditController;
import dtalge.container.editor.content.slip.DtSlipEditController;
import net.arnx.jsonic.JSONException;
import ssac.util.Strings;

/**
 * データコンテナエディタでサポートされているドキュメント形式を管理するクラス。
 * 
 * @version 1.0.0
 */
public class DCEditControllerManager
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/**
	 * コントローラーのリスト
	 */
	private IDtContainerEditController[]	_controllers;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public DCEditControllerManager() {
		// コントローラーリストの初期化
		_controllers = new IDtContainerEditController[] {
				new DtSlipEditController(),
				new DtBinderEditController(),
		};
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * コントローラーが一つも登録されていないかを判定する。
	 * @return	コントローラーが一つも登録されていない場合は <code>true</code>
	 */
	public boolean isEmpty() {
		return (size() == 0);
	}
	
	/**
	 * 登録済みコントローラー数を返す。
	 * @return	登録済みコントローラー数
	 */
	public int size() {
		return _controllers.length;
	}
	
	/**
	 * このマネージャーの先頭に登録されているコントローラーを返す。
	 * @return	先頭に登録されているコントローラーを返す。
	 * 			コントローラーが一つも登録されていない場合は {@code null} を返す。
	 */
	public IDtContainerEditController getDefaultController() {
		return (_controllers.length > 0 ? _controllers[0] : null);
	}
	
	/**
	 * 指定されたインデックスに対応するコントローラーを取得する。
	 * @param index	取得する位置を示すインデックス
	 * @return	指定されたインデックスに対応するコントローラー
	 * @throws ArrayIndexOutOfBoundsException	インデックスが範囲外の場合
	 */
	public IDtContainerEditController getControllerByIndex(int index) {
		return _controllers[index];
	}
	
	/**
	 * 指定された識別子を持つコントローラーを取得する。
	 * @param id	識別子
	 * @return	指定された識別子を持つコントローラーを返す。
	 * 			対応するコントローラーが存在しない場合は {@code null} を返す。
	 */
	public IDtContainerEditController getControllerById(String id) {
		if (!Strings.isNullOrEmpty(id)) {
			for (IDtContainerEditController controller : _controllers) {
				if (id.equals(controller.getID())) {
					// found
					return controller;
				}
			}
		}
		// not found
		return null;
	}
	
	/**
	 * 指定されたファイルをサポートしているコントローラーを取得する。
	 * 
	 * @param targetFile	判定するファイル
	 * @return	指定されたファイルをサポートしているコントローラーを返す。
	 * 			サポートするコントローラーが存在しない場合は {@code null} を返す。
	 */
	public IDtContainerEditController findSupportedControllerByFile(File targetFile) {
		if (targetFile == null)
			return null;	// targetFile is null
		
		for (IDtContainerEditController controller : _controllers) {
			if (controller.isSupportedFileType(targetFile)) {
				// supported
				return controller;
			}
		}
		
		// not supported
		return null;
	}
	
	/**
	 * 指定されたファイルをサポートしているドキュメントで読み込み、対応するドキュメントのビューを返す。
	 * @param targetFile	読み込むファイル
	 * @return	指定されたファイルに対応するドキュメントのビューオブジェクトを返す。
	 * 			ドキュメントに対応するファイルではない場合は、{@code null} を返す。
	 * @throws JSONException			ドキュメントの JSON フォーマットとして適切ではない場合
	 * @throws FileNotFoundException 	ファイルが存在しない場合
	 * @throws IOException 				入出力エラーが発生した場合
	 */
	public IDtContainerEditView openSupportedDocument(File targetFile) throws JSONException, FileNotFoundException, IOException
	{
		if (targetFile == null)
			return null;	// targetFile is null
		
		JSONException lastJsonException = null;
		for (IDtContainerEditController controller : _controllers) {
			lastJsonException = null;
			try {
				IDtContainerEditView view = controller.openDocument(null, targetFile);
				return view;	// read succeeded
			}
			catch (JSONException ex) {
				// unsupported format
				lastJsonException = ex;
			}
		}
		if (lastJsonException != null) {
			throw lastJsonException;
		}
		
		// unsupported document
		return null;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
