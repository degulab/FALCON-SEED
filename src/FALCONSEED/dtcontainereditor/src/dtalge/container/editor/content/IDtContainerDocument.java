/*
 * @(#)IDtContainerDocument.java	1.1.0	2023/01/25
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)IDtContainerDocument.java	1.0.0	2022/12/21
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.content;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.DOMException;

import dtalge.container.editor.content.common.table.DTCContentEditTableModelConversionError;
import dtalge.container.editor.content.common.tree.DTCContentTransferTreeNode;
import dtalge.container.editor.content.common.tree.DtContainerContentParentTreeNode;
import dtalge.container.editor.content.common.tree.DtContainerContentTreeModel;
import dtalge.container.editor.content.common.tree.IDtContainerContentTreeNode;

/**
 * データコンテナの編集中の内容を保持するドキュメントのインターフェース。
 * 
 * @version 1.1.0
 */
public interface IDtContainerDocument
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final String	DEF_NEWNODE_NAME_PREFIX	= "new_";
	
	static public final String	DEF_COPIED_NAME_PREFIX	= "copyOf_";

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * このドキュメントのタイプを説明する文字列を取得する。
	 * @return	このドキュメントのタイプを説明する文字列
	 */
	public String getRootContentName();
	
	/**
	 * このドキュメントのデータクラスを表す、パッケージ名を含まないクラス名を取得する。
	 * @return	このドキュメントのデータクラスの、パッケージ名を含まないクラス名
	 */
	public String getRootContentClassSimpleName();
	
	/**
	 * 編集中のデータ構造を表すツリーのルートノードを取得する。
	 * @return	{@code null} ではない、ツリーのルートノード
	 */
	public DtContainerContentParentTreeNode getDataTreeRootNode();
	
	/**
	 * 編集中のデータ構造を表すツリーモデルを取得する。
	 * @return	{@code null} ではない、ツリーモデル
	 */
	public DtContainerContentTreeModel getDataTreeModel();
	
	/**
	 * 指定されたデータオブジェクトのツリーノード以下を、CSV 形式でファイルにエクスポートする。
	 * <em>ndTarget</em> のコンテントタイプが、以下のものではない場合、このメソッドは例外をスローする。
	 * <ul>
	 * <li>{@link DtContainerContentTypes#ContentExalge}</li>
	 * <li>{@link DtContainerContentTypes#ContentExAlgeSet}</li>
	 * <li>{@link DtContainerContentTypes#ContentDtalge}</li>
	 * <li>{@link DtContainerContentTypes#ContentDtAlgeSet}</li>
	 * </ul>
	 * @param ndTarget	エクスポート対象のツリーノード
	 * @param destFile	出力先のファイル
	 * @param encoding	ファイル出力時の文字コード名
	 * @throws NullPointerException	<em>ndTarget</em> もしくは <em>destFile</em> が {@code null} の場合
	 * @throws IllegalArgumentException	<em>ndTarget</em> のコンテントタイプが適切ではない場合
	 * @throws IllegalStateException	<em>ndTarget</em> のコンテントタイプと保持するデータが一致しない場合
	 * @throws DTCContentEditTableModelConversionError	不正な値が含まれている場合
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public void exportDataObjectToCsvFile(IDtContainerContentTreeNode ndTarget, File destFile, String encoding) throws DTCContentEditTableModelConversionError, IOException;
	
	/**
	 * 指定されたデータオブジェクトのツリーノード以下を、XML 形式でファイルにエクスポートする。
	 * <em>ndTarget</em> のコンテントタイプが、以下のものではない場合、このメソッドは例外をスローする。
	 * <ul>
	 * <li>{@link DtContainerContentTypes#ContentExalge}</li>
	 * <li>{@link DtContainerContentTypes#ContentExAlgeSet}</li>
	 * <li>{@link DtContainerContentTypes#ContentDtalge}</li>
	 * <li>{@link DtContainerContentTypes#ContentDtAlgeSet}</li>
	 * </ul>
	 * @param ndTarget	エクスポート対象のツリーノード
	 * @param destFile	出力先のファイル
	 * @throws NullPointerException	<em>ndTarget</em> もしくは <em>destFile</em> が {@code null} の場合
	 * @throws IllegalArgumentException	<em>ndTarget</em> のコンテントタイプが適切ではない場合
	 * @throws IllegalStateException	<em>ndTarget</em> のコンテントタイプと保持するデータが一致しない場合
	 * @throws DTCContentEditTableModelConversionError	不正な値が含まれている場合
	 * @throws IOException	入出力エラーが発生した場合
	 * @throws FactoryConfigurationError XML の実装が使用できないかインスタンス化できない場合
	 * @throws ParserConfigurationException DocumentBuilder を構成できない場合
	 * @throws DOMException XML タグの生成に失敗した場合
	 * @throws TransformerConfigurationException <code>Transformer</code> インスタンスを生成できない場合
	 * @throws TransformerException ファイルへの変換中に回復不能なエラーが発生した場合
	 */
	public void exportDataObjectToXmlFile(IDtContainerContentTreeNode ndTarget, File destFile)
			throws DTCContentEditTableModelConversionError, IOException, FactoryConfigurationError, ParserConfigurationException, DOMException, TransformerConfigurationException, TransformerException;
	
	/**
	 * 指定されたデータオブジェクトのツリーノード以下を、JSON 形式でファイルにエクスポートする。
	 * <em>ndTarget</em> のコンテントタイプが、以下のものではない場合、このメソッドは例外をスローする。
	 * <ul>
	 * <li>{@link DtContainerContentTypes#ContentExalge}</li>
	 * <li>{@link DtContainerContentTypes#ContentExAlgeSet}</li>
	 * <li>{@link DtContainerContentTypes#ContentDtalge}</li>
	 * <li>{@link DtContainerContentTypes#ContentDtAlgeSet}</li>
	 * </ul>
	 * @param ndTarget	エクスポート対象のツリーノード
	 * @param destFile	出力先のファイル
	 * @throws NullPointerException	<em>ndTarget</em> もしくは <em>destFile</em> が {@code null} の場合
	 * @throws IllegalArgumentException	<em>ndTarget</em> のコンテントタイプが適切ではない場合
	 * @throws IllegalStateException	<em>ndTarget</em> のコンテントタイプと保持するデータが一致しない場合
	 * @throws DTCContentEditTableModelConversionError	不正な値が含まれている場合
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public void exportDataObjectToJsonFile(IDtContainerContentTreeNode ndTarget, File destFile) throws DTCContentEditTableModelConversionError, IOException;
	
	/**
	 * 指定されたスリップオブジェクトのツリーノード以下を、JSON 形式でファイルにエクスポートする。
	 * <em>ndTarget</em> のコンテントタイプが、以下のものではない場合、このメソッドは例外をスローする。
	 * <ul>
	 * <li>{@link DtContainerContentTypes#ContentDtSlip}</li>
	 * <li>{@link DtContainerContentTypes#ContentDtSlipList}</li>
	 * </ul>
	 * @param ndTarget	エクスポート対象のツリーノード
	 * @param destFile	出力先のファイル
	 * @throws NullPointerException	<em>ndTarget</em> もしくは <em>destFile</em> が {@code null} の場合
	 * @throws IllegalArgumentException	<em>ndTarget</em> のコンテントタイプが適切ではない場合
	 * @throws IllegalStateException	<em>ndTarget</em> のコンテントタイプと保持するデータが一致しない場合
	 * @throws DTCContentEditTableModelConversionError	不正な値が含まれている場合
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public void exportSlipObjectToJsonFile(IDtContainerContentTreeNode ndTarget, File destFile) throws DTCContentEditTableModelConversionError, IOException;
	
	/**
	 * 指定されたノートオブジェクトのツリーノード以下を、CSV 形式でファイルにエクスポートする。
	 * <em>ndTarget</em> のコンテントタイプが、以下のものではない場合、このメソッドは例外をスローする。
	 * <ul>
	 * <li>{@link DtContainerContentTypes#ContentDtSlipNote}</li>
	 * <li>{@link DtContainerContentTypes#ContentDtBinderNote}</li>
	 * </ul>
	 * @param ndTarget	エクスポート対象のツリーノード
	 * @param destFile	出力先のファイル
	 * @param encoding	ファイル出力時の文字コード名
	 * @throws NullPointerException	<em>ndTarget</em> もしくは <em>destFile</em> が {@code null} の場合
	 * @throws IllegalArgumentException	<em>ndTarget</em> のコンテントタイプが適切ではない場合
	 * @throws IllegalStateException	<em>ndTarget</em> のコンテントタイプと保持するデータが一致しない場合
	 * @throws DTCContentEditTableModelConversionError	不正な値が含まれている場合
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public void exportNoteToCsvFile(IDtContainerContentTreeNode ndTarget, File destFile, String encoding) throws DTCContentEditTableModelConversionError, IOException;
	
	/**
	 * 指定されたノートオブジェクトのツリーノード以下を、XML 形式でファイルにエクスポートする。
	 * <em>ndTarget</em> のコンテントタイプが、以下のものではない場合、このメソッドは例外をスローする。
	 * <ul>
	 * <li>{@link DtContainerContentTypes#ContentDtSlipNote}</li>
	 * <li>{@link DtContainerContentTypes#ContentDtBinderNote}</li>
	 * </ul>
	 * @param ndTarget	エクスポート対象のツリーノード
	 * @param destFile	出力先のファイル
	 * @throws NullPointerException	<em>ndTarget</em> もしくは <em>destFile</em> が {@code null} の場合
	 * @throws IllegalArgumentException	<em>ndTarget</em> のコンテントタイプが適切ではない場合
	 * @throws IllegalStateException	<em>ndTarget</em> のコンテントタイプと保持するデータが一致しない場合
	 * @throws DTCContentEditTableModelConversionError	不正な値が含まれている場合
	 * @throws IOException	入出力エラーが発生した場合
	 * @throws FactoryConfigurationError XML の実装が使用できないかインスタンス化できない場合
	 * @throws ParserConfigurationException DocumentBuilder を構成できない場合
	 * @throws DOMException XML タグの生成に失敗した場合
	 * @throws TransformerConfigurationException <code>Transformer</code> インスタンスを生成できない場合
	 * @throws TransformerException ファイルへの変換中に回復不能なエラーが発生した場合
	 */
	public void exportNoteToXmlFile(IDtContainerContentTreeNode ndTarget, File destFile)
			throws DTCContentEditTableModelConversionError, IOException, FactoryConfigurationError, ParserConfigurationException, DOMException, TransformerConfigurationException, TransformerException;
	
	/**
	 * 指定されたノートオブジェクトのツリーノード以下を、JSON 形式でファイルにエクスポートする。
	 * <em>ndTarget</em> のコンテントタイプが、以下のものではない場合、このメソッドは例外をスローする。
	 * <ul>
	 * <li>{@link DtContainerContentTypes#ContentDtSlipNote}</li>
	 * <li>{@link DtContainerContentTypes#ContentDtBinderNote}</li>
	 * </ul>
	 * @param ndTarget	エクスポート対象のツリーノード
	 * @param destFile	出力先のファイル
	 * @throws NullPointerException	<em>ndTarget</em> もしくは <em>destFile</em> が {@code null} の場合
	 * @throws IllegalArgumentException	<em>ndTarget</em> のコンテントタイプが適切ではない場合
	 * @throws IllegalStateException	<em>ndTarget</em> のコンテントタイプと保持するデータが一致しない場合
	 * @throws DTCContentEditTableModelConversionError	不正な値が含まれている場合
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public void exportNoteToJsonFile(IDtContainerContentTreeNode ndTarget, File destFile) throws DTCContentEditTableModelConversionError, IOException;
	
	/**
	 * <em>ndParent</em> 直下に、指定されたオブジェクトを新しいノード名で追加する。
	 * 同名のものがすでに存在している場合は、そのノードを置き換える。
	 * @param ndParent	格納先の親ノード
	 * @param nodeName	新しいノード名
	 * @param newValue	追加するオブジェクトの値
	 * @return	追加もしくは置き換えられた、新しいツリーノード
	 * @throws NullPointerException	引数のいずれかが {@code null} の場合
	 * @throws IllegalArgumentException	<em>newNodeName</em> が空文字列の場合、<em>ndParent</em> が名前付きオブジェクトを追加可能な親ノードではない場合、もしくは <em>newValue</em> が <em>ndParent</em> に追加可能な値ではない場合
	 */
	public IDtContainerContentTreeNode putNamedObject(DtContainerContentParentTreeNode ndParent, String nodeName, Object newValue);
	
	/**
	 * <em>ndParent</em> 直下に、指定されたツリーノードを、<em>newChild</em> が保持する名前で追加する。
	 * <em>newChild</em> が名前を持つ場合、同名のノードがすでに存在している場合は、それを置き換える。
	 * <em>newChild</em> が名前を持たない場合、{@code "new_99999"} のような名称で重複しない名前を自動的に設定する。
	 * @param ndParent	格納先の親ノード
	 * @param newChild	追加するツリーノード
	 * @throws NullPointerException	引数のいずれかが {@code null} の場合
	 * @throws IllegalArgumentException	<em>ndParent</em> に対して、<em>newChild</em> のコンテントタイプが適切ではない場合
	 * @since 1.1.0
	 */
	public void putNamedTreeNode(DtContainerContentParentTreeNode ndParent, IDtContainerContentTreeNode newChild);
	
	/**
	 * <em>ndParent</em> 直下に、指定されたオブジェクトを指定された位置に挿入する。
	 * @param ndParent	格納先の親ノード
	 * @param position	挿入位置を示すインデックス、インデックスが親ノードが持つ子ノード数以上の場合は終端に追加する
	 * @param newValue	追加するオブジェクトの値
	 * @return	追加された新しいツリーノード
	 * @throws NullPointerException	引数のいずれかが {@code null} の場合
	 * @throws IllegalArgumentException	<em>ndParent</em> がインデックスで子要素を管理する親ノードではない場合、もしくは <em>newValue</em> が <em>ndParent</em> に追加可能な値ではない場合
	 * @throws ArrayIndexOutOfBoundsException	<em>position</em> が負の値の場合
	 */
	public IDtContainerContentTreeNode insertIndexedObject(DtContainerContentParentTreeNode ndParent, int position, Object newValue);
	
	/**
	 * <em>ndParent</em> 直下に、指定されたツリーノードを指定された位置にすべて挿入する。
	 * @param ndParent	格納先の親ノード
	 * @param position	挿入位置を示すインデックス、インデックスが親ノードが持つ子ノード数以上の場合は終端に追加する
	 * @param newNodes	追加するツリーノードのコレクション
	 * @throws NullPointerException	引数のいずれかが {@code null} の場合、もしくは <em>newNodes</em> の要素が {@code null} の場合
	 * @throws IllegalArgumentException	<em>ndParent</em> に対して、<em>newNodes</em> の要素のコンテントタイプが適切ではない場合
	 * @since 1.1.0
	 */
	public void insertAllIndexedTreeNode(DtContainerContentParentTreeNode ndParent, int position, Collection<IDtContainerContentTreeNode> newNodes);
	
	/**
	 * <em>ndTarget</em> の値を、指定された値に置き換える。
	 * @param ndTarget	対象のノード
	 * @param newValue	新しいオブジェクトの値
	 * @return	値が置き換えられた、新しいツリーノード
	 * @throws NullPointerException	引数のいずれかが {@code null} の場合
	 * @throws IllegalArgumentException	<em>newValue</em> が <em>ndTarget</em> に対して置き換え可能な値ではない場合
	 */
	public IDtContainerContentTreeNode replaceObject(IDtContainerContentTreeNode ndTarget, Object newValue);
	
//	/**
//	 * <em>ndTarget</em> のノートの値を、指定された値に置き換える。
//	 * @param ndTarget	対象のノートノード
//	 * @param newValue	新しい {@code Dtalge} オブジェクト
//	 * @return	<em>ndTarget</em> ノード
//	 * @throws NullPointerException	引数のいずれかが {@code null} の場合
//	 * @throws IllegalArgumentException	<em>ndTarget</em> がノートオブジェクトを示すノードではない場合、もしくは <em>newValue</em> が {@code Dtalge} オブジェクトではない場合
//	 */
//	public IDtContainerContentTreeNode replaceNoteObject(IDtContainerContentTreeNode ndTarget, Object newValue);
	
	/**
	 * <em>ndTarget</em> のノード名を、指定された名前に変更する。
	 * @param ndTarget		対象のノード
	 * @param newNodeName	新しい名前
	 * @return	<em>ndTarget</em> を返す。
	 * @throws NullPointerException	引数のいずれかが {@code null} の場合
	 * @throws IllegalArgumentException <em>newNodeName</em> が空文字列の場合、もしくは <em>ndTarget</em> がノード名を許容する形式ではない場合
	 */
	public IDtContainerContentTreeNode renameObject(IDtContainerContentTreeNode ndTarget, String newNodeName);
	
	/**
	 * 指定された転送ツリーノードデータから、ツリーノードを生成する。
	 * ここで生成したツリーノードは、データモデルには登録されない。
	 * @param transferNode			ソースとする転送ツリーノードデータ
	 * @param requestContentType	生成するノードのコンテントタイプ、転送データのコンテントタイプで生成する場合は {@code null}
	 * @return	生成されたツリーノード
	 * @throws NullPointerException	<em>transferNode</em> が {@code null} の場合、もしくは <em>transferNode</em> のコンテントタイプと <em>needContentType</em> がともに {@code null} の場合
	 * @throws IllegalArgumentException	サポートされていないコンテントタイプが要求された場合
	 * @since 1.1.0
	 */
	public IDtContainerContentTreeNode createTreeNodeByTransferTreeNode(DTCContentTransferTreeNode transferNode, DtContainerContentTypes requestContentType);
	
	/**
	 * 番号のノード名を生成する。番号は、5 桁に満たない場合は 0 でパディングし、{@code long} 型の数値で循環する。
	 * この番号は、ドキュメントのインスタンスないでのみ循環する。
	 * @param prefix	番号の前に追加する文字列
	 * @return	生成された文字列
	 * @since 1.1.0
	 */
	public String getNextNewNodeName(String prefix);
	
	/**
	 * 表示に関するリソースを開放する。
	 * このドキュメントがコンパイルもしくは実行可能なドキュメントの場合、
	 * このメソッドの実行によってコンパイルもしくは実行に影響があってはならない。
	 */
	public void releaseViewResources();
	
	/**
	 * このドキュメントのタイトルを取得する。
	 * @return	このドキュメントのタイトル
	 */
	public String getTitle();
	
	/**
	 * このドキュメントの保存先ファイルを取得する。
	 * 保存先ファイルが定義されていない場合は <tt>null</tt> を返す。
	 * @return	保存先ファイル
	 */
	public File getTargetFile();
	
	/**
	 * このドキュメントに保存先ファイルが指定されているかを判定する。
	 * @return	保存先ファイルが指定されていれば <tt>true</tt>
	 */
	public boolean hasTargetFile();
	
	/**
	 * このドキュメントの保存先を、指定されたパスに設定する。
	 * このメソッドでは、保存先の正当性検証は行わず、ターゲットを指定されたパスに
	 * 設定するのみとなる。編集状態なども変更されない。
	 * @param newTarget	新しい保存先となる抽象パス
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public void setTargetFile(File newTarget);
	
	/**
	 * このドキュメントが読み込まれた時点での保存先ファイルの最終更新日時を取得する。
	 * 保存先ファイルが指定されていない場合は 0L を返す。
	 * @return	ドキュメントが読み込まれた時点での、最終更新日時
	 */
	public long lastModifiedTimeWhenLoadingTargetFile();
	
	/**
	 * このドキュメントの保存先ファイルの、現在の最終更新日時を取得する。
	 * 保存先ファイルが指定されていない場合は 0L を返す。
	 * @return	最終更新日時
	 */
	public long lastModifiedTimeWhenCurrentTargetFile();
	
	/**
	 * このドキュメントが読み込まれた時点での保存先ファイルの最終更新日時を、
	 * 保存先ファイルの現在の更新日時で更新する。
	 */
	public void updateLastModifiedTimeWhenLoadingTargetFile();
	
	/**
	 * このドキュメントを管理するコントローラーを返す。
	 * @return	編集用コントローラー
	 */
	public IDtContainerEditController getEditCotnroller();
	
	/**
	 * このドキュメントの保存先ファイルが移動可能かを判定する。
	 * @return	移動可能な場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean canMoveTargetFile();
	
	/**
	 * このドキュメントが新規に作成され、一度も保存されていないかを判定する。
	 * @return	このドキュメントが新規に作成され保存されていない場合は <tt>true</tt> を返す。
	 */
	public boolean isNewDocument();
	
	/**
	 * このドキュメントが編集されているかを判定する。
	 * @return	編集されていれば <tt>true</tt> を返す。
	 */
	public boolean isModified();
	
	/**
	 * このドキュメントの編集状態を設定する。
	 * @param modified	編集状態とする場合は <tt>true</tt> を指定する。
	 */
	public void setModifiedFlag(boolean modified);
	
	/**
	 * 現在のドキュメントに関連付けられたファイルのエンコーディング名を返す。
	 * このメソッドが返すエンコーディング名は、読み込み時もしくは保存時に
	 * 適用されたものとなる。
	 * @return	エンコーディング名(<tt>null</tt> 以外)
	 */
	public String getLastEncodingName();
	
	/**
	 * 現在のドキュメントに、編集によって不正な値が含まれているかを検証する。
	 * @throws DTCContentEditTableModelConversionError	不正な値が含まれている場合
	 */
	public void validateContainerContentData() throws DTCContentEditTableModelConversionError;
	
	/**
	 * 指定されたツリーノードおよびその子孫に、編集によって不正な値が含まれているかを検証する。
	 * @param node	検証対象のツリーノード
	 * @throws IllegalArgumentException	<em>node</em> が、このドキュメントのデータコンテナツリーに含まれていない場合
	 * @throws DTCContentEditTableModelConversionError	不正な値が含まれている場合
	 */
	public void validateContainerContentDataInNodeAndDescendants(IDtContainerContentTreeNode node) throws DTCContentEditTableModelConversionError;
	
	/**
	 * このドキュメントを指定されたファイルに保存する。
	 * 指定されたファイルがすでに存在している場合、このドキュメントの内容で
	 * 上書きする。
	 * @param targetFile	保存先ファイル
	 * @throws NullPointerException	<code>targetFile</code> が <tt>null</tt> の場合
	 * @throws DTCContentEditTableModelConversionError	不正な値が含まれている場合
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public void save(File targetFile) throws DTCContentEditTableModelConversionError, IOException;
}
