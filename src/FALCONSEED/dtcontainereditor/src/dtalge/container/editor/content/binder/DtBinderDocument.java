/*
 * @(#)DtBinderDocument.java	2.0.0	2025/02/17
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtBinderDocument.java	1.1.0	2023/01/25
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtBinderDocument.java	1.0.0	2022/12/21
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.content.binder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import dtalge.container.DtBinder;
import dtalge.container.editor.DtContainerEditorMessages;
import dtalge.container.editor.content.DtContainerContentTypes;
import dtalge.container.editor.content.common.AbDtContainerDocument;
import dtalge.container.editor.content.common.table.DTCContentDtalgeEditModel;
import dtalge.container.editor.content.common.table.DTCContentEditTableModelConversionError;
import dtalge.container.editor.content.common.tree.DtContainerContentLeafTreeNode;
import dtalge.container.editor.content.common.tree.DtContainerContentParentTreeNode;
import dtalge.container.editor.content.common.tree.IDtContainerContentTreeNode;
import dtalge.json.DtJSON;
import ssac.util.Validations;

/**
 * データコンテナの編集中の内容を保持するドキュメントの、{@code DtBinder} 固有の実装。
 * 
 * @version 2.0.0
 */
public class DtBinderDocument extends AbDtContainerDocument
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** このドキュメントのエディタコントローラー **/
	private DtBinderEditController	_controller;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected DtBinderDocument(final DtBinderEditController controller, DtBinder srcBinder)
	{
		super();
		_controller = Validations.validNotNull(controller);
		initNewDocument(srcBinder);
		setNewFlag(true);
		setModifiedFlag(true);
	}
	
	protected DtBinderDocument(final DtBinderEditController controller) {
		super();
		_controller = Validations.validNotNull(controller);
		initNewDocument();
		setNewFlag(true);
		setModifiedFlag(true);
	}
	
	protected DtBinderDocument(final DtBinderEditController controller, File srcFile) throws IOException
	{
		this(controller, srcFile, null);
	}
	
	protected DtBinderDocument(final DtBinderEditController controller, File srcFile, String encoding) throws IOException
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
	
	/**
	 * 現在のツリーノードの状態から、{@code DtBinder} オブジェクトを生成する。
	 * @return	生成されたオブジェクト
	 * @throws DTCContentEditTableModelConversionError	不正な値が含まれている場合
	 * @since 2.0.0
	 */
	public DtBinder createDataObject() throws DTCContentEditTableModelConversionError
	{
		// データオブジェクトを生成
		DtBinder binder = createDtBinderObjectFromTreeNode(_dataTreeNode);
		return binder;
	}

	//------------------------------------------------------------
	// Implement IDtContainerDocument interfaces
	//------------------------------------------------------------
	
	/**
	 * このドキュメントのタイプを説明する文字列を取得する。
	 * @return	このドキュメントのタイプを説明する文字列
	 */
	public String getRootContentName() {
		return DtContainerEditorMessages.getInstance().rootContentName_DtBinder;
	}
	
	/**
	 * このドキュメントのデータクラスを表す、パッケージ名を含まないクラス名を取得する。
	 * @return	このドキュメントのデータクラスの、パッケージ名を含まないクラス名
	 */
	public String getRootContentClassSimpleName() {
		return DtBinder.class.getSimpleName();
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
	public DtBinderEditController getEditCotnroller() {
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
	 * 指定されたツリーノードから、{@code DtBinder} オブジェクトを生成する。
	 * <em>targetNode</em> が返すコンテントタイプが、{@link DtContainerContentTypes#ContentDtBinder} のものに限る。
	 * @param targetNode	対象のツリーノード
	 * @return	生成されたオブジェクト
	 * @throws NullPointerException	<em>targetNode</em> が {@code null} の場合
	 * @throws IllegalArgumentException	<em>targetNode</em> のコンテントタイプが、許容されたもの以外の場合
	 * @throws IllegalStateException	<em>targetNode</em> およびその子孫に格納されたデータオブジェクトが適切ではない場合
	 * @throws DTCContentEditTableModelConversionError	不正な値が含まれている場合
	 */
	protected DtBinder createDtBinderObjectFromTreeNode(IDtContainerContentTreeNode targetNode) throws DTCContentEditTableModelConversionError
	{
		DtContainerContentTypes contentType = targetNode.getContentType();
		if (contentType != DtContainerContentTypes.ContentDtBinder) {
			throw new IllegalArgumentException("Target node's content type is not DtBinder : " + String.valueOf(contentType));
		}
		if (targetNode.getChildCount() < 2) {
			throw new IllegalStateException("Target node has no 'Note' or 'Slips'");
		}
		
		DtBinder retBinder = new DtBinder();
		
		// note
		IDtContainerContentTreeNode ndNote = targetNode.getChildAt(0);
		DtContainerContentTypes noteType = ndNote.getContentType();
		if (noteType != DtContainerContentTypes.ContentDtBinderNote) {
			throw new IllegalStateException("Target node's note does not found");
		}
		Object objNote = ndNote.getUserObject();
		if (!(objNote instanceof DTCContentDtalgeEditModel)) {
			throw new IllegalStateException("Target node's note is not DtContainerContentDtalgeEditModel : " + (objNote==null ? "null" : objNote.getClass().toString()));
		}
		retBinder.setNote(((DTCContentDtalgeEditModel)objNote).getValuesAsDtalge(ndNote));
		
		// named objects
		IDtContainerContentTreeNode ndSlips = targetNode.getChildAt(1);
		DtContainerContentTypes slipsType = ndSlips.getContentType();
		if (slipsType != DtContainerContentTypes.ContentDtBinderSlips) {
			throw new IllegalStateException("Target node's slips is not named map : " + String.valueOf(slipsType));
		}
		for (int index = 0; index < ndSlips.getChildCount(); ++index) {
			IDtContainerContentTreeNode ndChild = ndSlips.getChildAt(index);
			Object objChild = createNamedSlipObjectFromTreeNode(ndChild);
			retBinder.putObject(ndChild.getNodeName(), objChild);
		}
		
		return retBinder;
	}
	
	/**
	 * データバインダー用ツリーノードを生成する。
	 * <em>binder</em> に {@code null} を指定した場合、要素が空の {@code DtBinder} を表すツリーを生成する。
	 * @param binder	{@code DtBinder} オブジェクト、または {@code null}
	 * @return	生成されたツリーノード
	 */
	protected DtContainerContentParentTreeNode createDtBinderNode(DtBinder binder) {
		// root
		DtContainerContentParentTreeNode ndRoot = new DtContainerContentParentTreeNode();
		ndRoot.setContentType(DtContainerContentTypes.ContentDtBinder);
		ndRoot.setUserObject(null);
		// note in binder
		DtContainerContentLeafTreeNode ndNote = createNoteDtalgeNode(DtContainerContentTypes.ContentDtBinderNote, (binder == null ? null : binder.getNote()));
		ndRoot.add(ndNote);
		// slips in binder
		DtContainerContentParentTreeNode ndObjects = new DtContainerContentParentTreeNode();
		ndObjects.setContentType(DtContainerContentTypes.ContentDtBinderSlips);
		ndObjects.setNodeName(DtContainerEditorMessages.getInstance().contentNodeName_slips);
		Map<String, DtContainerContentParentTreeNode> mapSlips = new TreeMap<>();
		ndObjects.setUserObject(mapSlips);
		ndRoot.add(ndObjects);
		
		if (binder != null && !binder.isObjectEmpty()) {
			// add elements from slips in binder
			for (Map.Entry<String, Object> entry : binder.getUnmodifiableObjects().entrySet()) {
				// get content type from value
				DtContainerContentParentTreeNode ndChild = createNamedSlipObjectTreeNode(entry.getKey(), entry.getValue());
				//--- 名前管理マップへ追加(名前順)
				mapSlips.put(ndChild.getNodeName(), ndChild);
			}
			//--- 名前順にツリーへ追加
			for (Map.Entry<String, DtContainerContentParentTreeNode> entry : mapSlips.entrySet()) {
				ndObjects.add(entry.getValue());
			}
			
		}
		
		return ndRoot;
	}
	
	/**
	 * 要素が空の新規ドキュメントとして、このドキュメントを初期化する。
	 */
	protected void initNewDocument() {
		_dataTreeNode = createDtBinderNode(null);
		_dataTreeModel = createContentTreeModel(_dataTreeNode);
	}

	/**
	 * 指定された要素の持つ新規のドキュメントとして、このドキュメントを初期化する。
	 * @param srcBinder	ソースの DtBinder オブジェクト
	 */
	protected void initNewDocument(DtBinder srcBinder) {
		_dataTreeNode = createDtBinderNode(srcBinder);
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
		DtBinder binder = DtJSON.deserialize(srcFile, DtBinder.class);
		
		// ドキュメントの初期化
		_dataTreeNode = createDtBinderNode(binder);
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
		DtBinder binder = createDtBinderObjectFromTreeNode(_dataTreeNode);
		
		// 保存
		DtJSON.serialize(targetFile, binder, false);	// データ型名は強制出力
		
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
