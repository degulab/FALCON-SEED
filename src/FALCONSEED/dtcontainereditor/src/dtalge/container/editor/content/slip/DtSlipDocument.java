/*
 * @(#)DtSlipDocument.java	1.0.0	2022/12/21
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.content.slip;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import dtalge.container.DtSlip;
import dtalge.container.editor.DtContainerEditorMessages;
import dtalge.container.editor.content.common.AbDtContainerDocument;
import dtalge.container.editor.content.common.table.DTCContentEditTableModelConversionError;
import dtalge.json.DtJSON;
import ssac.util.Validations;

/**
 * データコンテナの編集中の内容を保持するドキュメントの、{@code DtSlip} 固有の実装。
 * 
 * @version 1.0.0
 */
public class DtSlipDocument extends AbDtContainerDocument
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** このドキュメントのエディタコントローラー **/
	private DtSlipEditController	_controller;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected DtSlipDocument(final DtSlipEditController controller) {
		super();
		_controller = Validations.validNotNull(controller);
		initNewDocument();
		setNewFlag(true);
		setModifiedFlag(true);
	}
	
	protected DtSlipDocument(final DtSlipEditController controller, File srcFile) throws IOException
	{
		this(controller, srcFile, null);
	}
	
	protected DtSlipDocument(final DtSlipEditController controller, File srcFile, String encoding) throws IOException
	{
		super();
		_controller = Validations.validNotNull(controller, "'Edit-controller' is null.");
		initDocument(Validations.validNotNull(srcFile, "'srcFile' is null."), encoding);
		setNewFlag(false);
		setModifiedFlag(false);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Implement IDtContainerDocument interfaces
	//------------------------------------------------------------
	
	/**
	 * このドキュメントのタイプを説明する文字列を取得する。
	 * @return	このドキュメントのタイプを説明する文字列
	 */
	public String getRootContentName() {
		return DtContainerEditorMessages.getInstance().rootContentName_DtSlip;
	}
	
	/**
	 * このドキュメントのデータクラスを表す、パッケージ名を含まないクラス名を取得する。
	 * @return	このドキュメントのデータクラスの、パッケージ名を含まないクラス名
	 */
	public String getRootContentClassSimpleName() {
		return DtSlip.class.getSimpleName();
	}
	
	/**
	 * 表示に関するリソースを開放する。
	 * このドキュメントがコンパイルもしくは実行可能なドキュメントの場合、
	 * このメソッドの実行によってコンパイルもしくは実行に影響があってはならない。
	 */
	public void releaseViewResources() {
		// no implement.
	}
	
	/**
	 * このドキュメントを管理するコントローラーを返す。
	 * @return	編集用コントローラー
	 */
	public DtSlipEditController getEditCotnroller() {
		return _controller;
	}
	
	/**
	 * このドキュメントを指定されたファイルに保存する。
	 * 指定されたファイルがすでに存在している場合、このドキュメントの内容で
	 * 上書きする。
	 * @param targetFile	保存先ファイル
	 * @throws NullPointerException	<code>targetFile</code> が <tt>null</tt> の場合
	 * @throws DTCContentEditTableModelConversionError	不正な値が含まれている場合
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public void save(File targetFile) throws DTCContentEditTableModelConversionError, IOException
	{
		saveDocument(targetFile);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	/**
	 * 要素が空の新規ドキュメントとして、このドキュメントを初期化する。
	 */
	protected void initNewDocument() {
		_dataTreeNode = createDtSlipNode(null);
		_dataTreeModel = createContentTreeModel(_dataTreeNode);
	}

	/**
	 * 指定されたファイルから、このドキュメントを初期化する。
	 * 
	 * @param srcFile	ドキュメントファイル
	 * @param encoding	ドキュメントファイル読み込み時に適用するファイルエンコーディング名。
	 * 					<tt>null</tt> の場合は、システム標準のエンコーディング名が適用される。
	 * @throws IOException	入出力エラーが発生した場合
	 */
	protected void initDocument(File srcFile, String encoding) throws IOException
	{
		// ファイルの有無を確認
		if (!srcFile.exists()) {
			throw new FileNotFoundException(srcFile.getAbsolutePath());
		}
		
		// 設定情報の更新
		setTargetFile(srcFile);
		updateLastModifiedTimeWhenLoadingTargetFile();
		
		// ソース読み込み
		DtSlip slip = DtJSON.deserialize(srcFile, DtSlip.class);
		
		// ドキュメントの初期化
		_dataTreeNode = createDtSlipNode(slip);
		_dataTreeModel = createContentTreeModel(_dataTreeNode);
	}

	/**
	 * 指定されたファイルにドキュメントを保存する。ファイルがすでに存在している場合は、
	 * このドキュメントの内容で上書きする。
	 * @param targetFile	保存先ファイル
	 * @throws DTCContentEditTableModelConversionError	不正な値が含まれている場合
	 * @throws IOException	入出力エラーが発生した場合
	 */
	protected void saveDocument(File targetFile) throws DTCContentEditTableModelConversionError, IOException
	{
		// データオブジェクトを生成
		DtSlip slip = createDtSlipObjectFromTreeNode(_dataTreeNode);
		
		// 保存
		DtJSON.serialize(targetFile, slip, false);	// データ型名は強制出力
		
		// フラグの更新
		setNewFlag(false);
		setModifiedFlag(false);
		setTargetFile(targetFile);
		updateLastModifiedTimeWhenLoadingTargetFile();
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
