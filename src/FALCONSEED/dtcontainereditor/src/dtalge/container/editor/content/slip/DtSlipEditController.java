/*
 * @(#)DtSlipEditController.java	1.0.0	2022/12/21
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.content.slip;

import java.awt.Component;
import java.io.File;
import java.io.IOException;

import dtalge.container.editor.DtContainerEditorMessages;
import dtalge.container.editor.content.common.AbDtContainerEditController;
import dtalge.container.editor.content.common.table.DTCContentEditTableModelConversionError;
import ssac.util.io.ExtensionFileFilter;

/**
 * データコンテナの編集用ドキュメントタイプを保持するコントローラーの、{@code DtSlip} 固有の実装。
 * 
 * @version 1.0.0
 */
public class DtSlipEditController extends AbDtContainerEditController
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final String ControllerID = "DtSlip";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Implement IDtContainerEditController interfaces
	//------------------------------------------------------------
	/**
	 *  このプラグインの識別子を返す。
	 *  この識別子は、メニューの新規作成や形式指定でファイルを開く場合に、
	 *  コンポーネントを判別するための識別子となる。
	 */
	public String getID() {
		return ControllerID;
	}
	
	/**
	 * このプラグインの名称を返す。
	 * @return	プラグイン名
	 */
	public String getName() {
		return DtContainerEditorMessages.getInstance().DtSlip_docTypeName;
	}
	
	/**
	 * このプラグインの説明を返す。
	 * @return	プラグインの説明
	 */
	public String getDescription() {
		return DtContainerEditorMessages.getInstance().DtSlip_docTypeDesc;
	}
	
	/**
	 * 指定されたファイルが、このマネージャでサポートされているかを判定する。
	 * 基本的には、ファイル拡張子で判定する。このインタフェースを実装により、
	 * ファイルの内容によって判定する場合もある。
	 * 
	 * @param targetFile	判定するファイル
	 * @return	サポートしている場合は <tt>true</tt>
	 */
	public boolean isSupportedFileType(File targetFile) {
		// ファイルの拡張子のみで判定する
		return super.isSupportedFileType(targetFile);
	}
	
	/**
	 * このマネージャがサポートするドキュメントのクラスを取得する。
	 * @return	マネージャがサポートするドキュメントの <code>Class</code> インスタンス
	 */
	public Class<DtSlipDocument> getSupportedDocumentClass() {
		return DtSlipDocument.class;
	}
	
	/**
	 * このマネージャで読み込み可能なファイルの種別を表すフィルタを取得する。
	 * @return	読み込み用 <code>{@link ssac.aadl.editor.plugin.core.util.io.ExtensionFileFilter}</code> オブジェクト
	 */
	public ExtensionFileFilter getSupportedFileFilter() {
		return this._forOpenFilters[0];
	}
	
	/**
	 * このプラグインのドキュメントが、新規作成可能なドキュメントかを取得する。
	 * @return	常に <tt>true</tt> を返す。
	 * @since 1.16
	 */
	public boolean isAllowCreateNewDocument() {
		return true;
	}
	
	/**
	 * ファイルと関連付けられていない、新規ドキュメントを生成する。
	 * <em>templateText</em> が有効な文字列の場合、その文字列を内容とする
	 * 新しいドキュメントが作成される。ただし、内容についてはこのメソッド内で検証しない。
	 * <em>templateText</em> が <tt>null</tt> もしくは空文字列の場合は、この
	 * コンポーネント標準の内容を持つドキュメントが作成される。
	 * @param targetFile	新規ドキュメントの保存先とするファイル
	 * @return	新規ドキュメントに関連付けられたビューを返す。
	 */
	public DtSlipEditView newDocument(String templateText) {
		DtSlipDocument doc = new DtSlipDocument(this);
		DtSlipEditView view = new DtSlipEditView(doc);
		putDocumentView(doc, view);
///		view.setEditorFont(getEditorFont());
		return view;
	}
	
	/**
	 * 指定されたファイルを保存先とする新規ドキュメントを生成する。
	 * <em>templateText</em> が有効な文字列の場合、その文字列を内容とする
	 * 新しいファイルが作成される。ただし、内容についてはこのメソッド内で検証しない。
	 * <em>templateText</em> が <tt>null</tt> もしくは空文字列の場合は、この
	 * コンポーネント標準の内容を持つファイルが作成される。
	 * なお、指定されたファイルがすでに存在する場合は、ここで生成された内容で
	 * 強制的に上書きされる。
	 * 
	 * @param targetFile	新規ドキュメントの保存先とするファイル
	 * @return	新規ドキュメントに関連付けられたビューを返す。
	 * @throws IOException	ファイルの作成に失敗した場合
	 */
	public DtSlipEditView newDocument(File targetFile, String templateText) throws IOException
	{
		DtSlipDocument doc = new DtSlipDocument(this);
		try {
			doc.save(targetFile);
		} catch (DTCContentEditTableModelConversionError ignoreEx) {
			// この例外は、編集中のデータによるものなので、無視
		}
		DtSlipEditView view = new DtSlipEditView(doc);
		putDocumentView(doc, view);
///		view.setEditorFont(getEditorFont());
		return view;
	}
	
	public DtSlipEditView openDocument(Component parentComponent, File targetFile) throws IOException {
		DtSlipDocument doc = new DtSlipDocument(this, targetFile);
		DtSlipEditView view = new DtSlipEditView(doc);
		putDocumentView(doc, view);
///		view.setEditorFont(getEditorFont());
		return view;
	}

	public DtSlipEditView onNewComponent(Component parentComponent) {
		DtSlipDocument doc = new DtSlipDocument(this);
		DtSlipEditView view = new DtSlipEditView(doc);
		putDocumentView(doc, view);
///		view.setEditorFont(getEditorFont());
		return view;
	}

	@Override
	protected String getFileChooserTitleForOpen() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getFileChooserTitleForSave() {
		// TODO Auto-generated method stub
		return null;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
