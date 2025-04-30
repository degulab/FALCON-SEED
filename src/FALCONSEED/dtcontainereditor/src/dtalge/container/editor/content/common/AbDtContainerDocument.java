/*
 * @(#)AbDtContainerDocument.java	1.1.0	2023/01/24
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbDtContainerDocument.java	1.0.0	2022/12/21
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.content.common;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.DOMException;

import dtalge.DtAlgeSet;
import dtalge.Dtalge;
import dtalge.container.DtSlip;
import dtalge.container.DtSlipList;
import dtalge.container.editor.DtContainerEditorMessages;
import dtalge.container.editor.content.DtContainerContentTypes;
import dtalge.container.editor.content.DtContainerContentTypesManager;
import dtalge.container.editor.content.IDtContainerDocument;
import dtalge.container.editor.content.common.table.DTCContentDtalgeEditModel;
import dtalge.container.editor.content.common.table.DTCContentExalgeEditModel;
import dtalge.container.editor.content.common.table.DTCContentEditTableModelConversionError;
import dtalge.container.editor.content.common.table.DTCContentInvalidCellValue;
import dtalge.container.editor.content.common.tree.DTCContentTransferTreeNode;
import dtalge.container.editor.content.common.tree.DtContainerContentLeafTreeNode;
import dtalge.container.editor.content.common.tree.DtContainerContentParentTreeNode;
import dtalge.container.editor.content.common.tree.DtContainerContentTreeModel;
import dtalge.container.editor.content.common.tree.IDtContainerContentTreeNode;
import dtalge.json.DtJSON;
import exalge2.ExAlgeSet;
import exalge2.Exalge;

/**
 * データコンテナの編集中の内容を保持するドキュメントの共通実装。
 * 
 * @version 1.1.0
 */
public abstract class AbDtContainerDocument implements IDtContainerDocument
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** 無名の名前付きオブジェクトに命名する際の通し番号 */
	private long	_nextNewNodeNameNumber = 1;
	
	/**
	 * このドキュメントが新規ドキュメントであることを示すフラグ
	 */
	private boolean _flgNew;
	/**
	 * テキストドキュメント変更状態を示すフラグ
	 */
	private boolean _flgModified;
	/**
	 * ソースドキュメントの対象ファイル、{@code null} も許容
	 */
	private File _targetFile;
	/**
	 * ドキュメント対象ファイルのロード時の最終更新日時
	 */
	private long	_lastModifiedWhenLoadingTargetFile = 0L;
	/**
	 * 最後に適用したファイルエンコーディング、JSON なので常に <code>UTF-8</code>
	 */
	private String _lastEncoding = "UTF-8";
	
	/**
	 * 編集中のデータ構造を表すツリーのルートノード
	 */
	protected DtContainerContentParentTreeNode	_dataTreeNode;
	/**
	 * 編集中のデータ構造を表すツリーモデル
	 */
	protected DtContainerContentTreeModel		_dataTreeModel;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * 編集中のデータ構造を表すツリーのルートノードを取得する。
	 * @return	{@code null} ではない、ツリーのルートノード
	 */
	public DtContainerContentParentTreeNode getDataTreeRootNode() {
		return _dataTreeNode;
	}
	
	/**
	 * 編集中のデータ構造を表すツリーモデルを取得する。
	 * @return	{@code null} ではない、ツリーモデル
	 */
	public DtContainerContentTreeModel getDataTreeModel() {
		return _dataTreeModel;
	}

	//------------------------------------------------------------
	// Implement IDtContainerDocument interfaces
	//------------------------------------------------------------
	
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
	public IDtContainerContentTreeNode putNamedObject(DtContainerContentParentTreeNode ndParent, String nodeName, Object newValue) {
		// validation
		if (ndParent == null)	throw new NullPointerException("Parent tree node is null");
		if (nodeName == null)	throw new NullPointerException("Node name is null");
		if (newValue == null)	throw new NullPointerException("New value object is null");
		if (nodeName.isEmpty())
			throw new IllegalArgumentException("Node name is empty");
		
		// create tree node
		DtContainerContentTypes parentType = ndParent.getContentType();
		IDtContainerContentTreeNode ndNewNode;
		if (parentType == DtContainerContentTypes.ContentDtSlipObjects) {
			ndNewNode = createNamedDataObjectTreeNode(nodeName, newValue);
		}
		else if (parentType == DtContainerContentTypes.ContentDtBinderSlips) {
			ndNewNode = createNamedSlipObjectTreeNode(nodeName, newValue);
		}
		else {
			throw new IllegalArgumentException("Parent tree node can not add named object : " + String.valueOf(parentType));
		}
		
		// add to parent
		int newIndex = ndParent.findSortedPositionByName(ndNewNode);
		if (newIndex < 0) {
			// 新規挿入
			newIndex = -(newIndex + 1);
			getDataTreeModel().insertNodeInto(ndNewNode, ndParent, newIndex);
		}
		else {
			// 同名ノードの置き換え
			int [] indices = new int[]{newIndex};
			Object[] removes = new Object[]{ndParent.getChildAt(indices[0])};
			
			ndParent.remove(newIndex);
			getDataTreeModel().nodesWereRemoved(ndParent, indices, removes);
			
			ndParent.insert(ndNewNode, newIndex);
			getDataTreeModel().nodesWereInserted(ndParent, indices);
		}
		
		// modified document
		setModifiedFlag(true);
		return ndNewNode;
	}
	
	/**
	 * <em>ndParent</em> 直下に、指定されたツリーノードを、<em>newChild</em> が保持する名前で追加する。
	 * <em>newChild</em> が名前を持つ場合、同名のノードがすでに存在している場合は、それを置き換える。
	 * <em>newChild</em> が名前を持たない場合、{@code "new_99999"} のような名称で重複しない名前を自動的に設定する。
	 * @param ndParent	格納先の親ノード
	 * @param newChild	追加するツリーノード
	 * @throws NullPointerException	引数のいずれかが {@code null} の場合
	 * @throws IllegalArgumentException	<em>ndParent</em> に対して <em>newChild</em> のコンテントタイプが適切ではない場合、もしくは <em>ndParent</em> が名前付きオブジェクトの親ではない場合
	 * @since 1.1.0
	 */
	public void putNamedTreeNode(DtContainerContentParentTreeNode ndParent, IDtContainerContentTreeNode newChild) {
		// validation
		if (ndParent == null)	throw new NullPointerException("Parent tree node is null");
		if (newChild == null)	throw new NullPointerException("New tree node is null");
		DtContainerContentTypes parentType = ndParent.getContentType();
		DtContainerContentTypes childType  = newChild.getContentType();
		if (DtContainerContentTypes.ContentDtSlipObjects != parentType && DtContainerContentTypes.ContentDtBinderSlips != parentType)
			throw new IllegalArgumentException("Parent node is not type of DtSlipObject or DtBinderSlips: " + String.valueOf(parentType));
		if (!DtContainerContentTypesManager.getInstance().isAppendableIntoParent(parentType, childType))
			throw new IllegalArgumentException("New child object [" + String.valueOf(childType) + "] could not add into parent [" + String.valueOf(parentType) + "]");
		
		// check name
		if (!newChild.hasNodeName()) {
			int sortedIndex;
			do {
				newChild.setNodeName( getNextNewNodeName(DEF_NEWNODE_NAME_PREFIX) );
				sortedIndex = ndParent.findSortedPositionByName(newChild);
			} while (sortedIndex >= 0);	// 同名が存在する間は、新しい名前を生成
		}
		
		// add to parent
		int newIndex = ndParent.findSortedPositionByName(newChild);
		if (newIndex < 0) {
			// 新規挿入
			newIndex = -(newIndex + 1);
			getDataTreeModel().insertNodeInto(newChild, ndParent, newIndex);
		}
		else {
			// 同名ノードの置き換え
			int [] indices = new int[]{newIndex};
			Object[] removes = new Object[]{ndParent.getChildAt(indices[0])};
			
			ndParent.remove(newIndex);
			getDataTreeModel().nodesWereRemoved(ndParent, indices, removes);
			
			ndParent.insert(newChild, newIndex);
			getDataTreeModel().nodesWereInserted(ndParent, indices);
		}
		
		// modified document
		setModifiedFlag(true);
	}
	
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
	public IDtContainerContentTreeNode insertIndexedObject(DtContainerContentParentTreeNode ndParent, int position, Object newValue) {
		// validation
		if (ndParent == null)	throw new NullPointerException("Parent tree node is null");
		if (newValue == null)	throw new NullPointerException("New value object is null");
		if (!DtContainerContentTypesManager.getInstance().isIndexedChildren(ndParent.getContentType()))
			throw new IllegalArgumentException("Parent tree node can not add indexed object : " + String.valueOf(ndParent.getContentType()));
		if (position < 0)
			throw new ArrayIndexOutOfBoundsException("New child position is negative");
		else if (position > ndParent.getChildCount())
			position = ndParent.getChildCount();
		
		// create tree node
		IDtContainerContentTreeNode ndNewNode;
		DtContainerContentTypes parentType = ndParent.getContentType();
		if (parentType == DtContainerContentTypes.ContentExAlgeSet) {
			if (!(newValue instanceof Exalge)) {
				throw new IllegalArgumentException("Data object is not Exalge : " + newValue.getClass().toString());
			}
			//ndNewNode = new DtContainerContentLeafTreeNode(DtContainerContentTypes.ContentExalge, position, newValue);
			ndNewNode = createExalgeDataNode((Exalge)newValue);
			ndNewNode.setNodePosition(position);
		}
		else if (parentType == DtContainerContentTypes.ContentDtAlgeSet) {
			if (!(newValue instanceof Dtalge)) {
				throw new IllegalArgumentException("Data object is not Dtalge : " + newValue.getClass().toString());
			}
			//ndNewNode = new DtContainerContentLeafTreeNode(DtContainerContentTypes.ContentDtalge, position, newValue);
			ndNewNode = createDtalgeDataNode((Dtalge)newValue);
			ndNewNode.setNodePosition(position);
		}
		else if (parentType == DtContainerContentTypes.ContentDtSlipList) {
			if (!(newValue instanceof DtSlip)) {
				throw new IllegalArgumentException("Data object is not DtSlip : " + newValue.getClass().toString());
			}
			ndNewNode = createDtSlipNode((DtSlip)newValue);
			ndNewNode.setNodePosition(position);
		}
		else {
			// unsupported indexed object
			throw new IllegalArgumentException("Parent tree node can not add indexed object : " + String.valueOf(parentType));
		}
		
		// insert new tree node
		ndParent.insert(ndNewNode, position);
		
		// 後続のインデックスを更新
		for (int index = position + 1; index < ndParent.getChildCount(); ++index) {
			IDtContainerContentTreeNode ndChild = ndParent.getChildAt(index);
			ndChild.setNodePosition(index);
		}
		getDataTreeModel().nodeStructureChanged(ndParent);
		
		// modified document
		setModifiedFlag(true);
		return ndNewNode;
	}
	
	/**
	 * <em>ndParent</em> 直下に、指定されたツリーノードを指定された位置にすべて挿入する。
	 * @param ndParent	格納先の親ノード
	 * @param position	挿入位置を示すインデックス、インデックスが親ノードが持つ子ノード数以上の場合は終端に追加する
	 * @param newNodes	追加するツリーノードのコレクション
	 * @throws NullPointerException	引数のいずれかが {@code null} の場合、もしくは <em>newNodes</em> の要素が {@code null} の場合
	 * @throws IllegalArgumentException	<em>ndParent</em> に対して、<em>newNodes</em> の要素のコンテントタイプが適切ではない場合
	 * @since 1.1.0
	 */
	public void insertAllIndexedTreeNode(DtContainerContentParentTreeNode ndParent, int position, Collection<IDtContainerContentTreeNode> newNodes) {
		// validation
		if (ndParent == null)	throw new NullPointerException("Parent tree node is null");
		if (newNodes == null)	throw new NullPointerException("New node collection is null");
		if (!DtContainerContentTypesManager.getInstance().isIndexedChildren(ndParent.getContentType()))
			throw new IllegalArgumentException("Parent tree node can not add indexed object : " + String.valueOf(ndParent.getContentType()));
		if (position < 0)
			throw new ArrayIndexOutOfBoundsException("New child position is negative");
		else if (position > ndParent.getChildCount())
			position = ndParent.getChildCount();
		
		// exist new node
		if (newNodes.isEmpty())
			return;
		
		// insert tree node
		DtContainerContentTypes parentType = ndParent.getContentType();
		for (IDtContainerContentTreeNode newChild : newNodes) {
			if (!DtContainerContentTypesManager.getInstance().isAppendableIntoParent(parentType, newChild.getContentType())) {
				throw new IllegalArgumentException("New child object [" + newChild.getContentType() + "] could not add into parent [" + parentType + "]");
			}
			newChild.setNodeName(null);
			newChild.setNodePosition(position);
			//--- insert new tree node
			ndParent.insert(newChild, position);
			++position;
		}
		
		// 後続のインデックスを更新
		for (; position < ndParent.getChildCount(); position++) {
			IDtContainerContentTreeNode ndChild = ndParent.getChildAt(position);
			ndChild.setNodePosition(position);
		}
		
		// 更新通知
		getDataTreeModel().nodeStructureChanged(ndParent);
		setModifiedFlag(true);
	}
	
	/**
	 * <em>ndTarget</em> の値を、指定された値に置き換える。
	 * @param ndTarget	対象のノード
	 * @param newValue	新しいオブジェクトの値
	 * @return	値が置き換えられた、新しいツリーノード
	 * @throws NullPointerException	引数のいずれかが {@code null} の場合
	 * @throws IllegalArgumentException	<em>newValue</em> が <em>ndTarget</em> に対して置き換え可能な値ではない場合
	 */
	public IDtContainerContentTreeNode replaceObject(IDtContainerContentTreeNode ndTarget, Object newValue) {
		// validation
		if (ndTarget == null)	throw new NullPointerException("Target tree node is null");
		if (newValue == null)	throw new NullPointerException("New value object is null");
		DtContainerContentParentTreeNode ndParent = (DtContainerContentParentTreeNode)ndTarget.getParent();
		DtContainerContentTypes parentType = ndParent.getContentType();
		IDtContainerContentTreeNode ndNewNode;
		
		// replace data object
		if (DtContainerContentTypesManager.getInstance().isNoteObject(ndTarget.getContentType())) {
			//--- replace Note as Dtalge in DtSlip or DtBinder
			if (newValue instanceof Dtalge) {
				//--- create new note node
				ndNewNode = createNoteDtalgeNode(ndTarget.getContentType(), (Dtalge)newValue);
			}
			else if (newValue instanceof DTCContentDtalgeEditModel) {
				//--- create new note node
				ndNewNode = new DtContainerContentLeafTreeNode();
				ndNewNode.setContentType(ndTarget.getContentType());
				ndNewNode.setNodeName(DtContainerEditorMessages.getInstance().contentNodeName_note);
				ndNewNode.setUserObject(newValue);
			}
			else {
				throw new IllegalArgumentException("New value is not Dtalge or DTCContentDtalgeEditModel : " + newValue.getClass().toString());
			}
			//--- replace
			int[] indices = new int[] {ndParent.getIndex(ndTarget)};
			Object[] removes = new Object[] {ndTarget};
			ndParent.remove(indices[0]);
			getDataTreeModel().nodesWereRemoved(ndParent, indices, removes);
			ndParent.insert(ndNewNode, indices[0]);
			getDataTreeModel().nodesWereInserted(ndParent, indices);
		}
		else if (parentType == DtContainerContentTypes.ContentExAlgeSet) {
			//--- replace as Exalge in ExAlgeSet
			if (newValue instanceof Exalge) {
				ndNewNode = createExalgeDataNode((Exalge)newValue);
			}
			else if (newValue instanceof DTCContentExalgeEditModel) {
				
			}
			else {
				throw new IllegalArgumentException("Data object is not Exalge or DTCContentExalgeEditModel : " + newValue.getClass().toString());
			}
			//--- create new node for Exalge
			ndNewNode = createExalgeDataNode((Exalge)newValue);
			ndNewNode.setNodePosition( ndTarget.getNodePosition() );
			//--- replace
			int[] indices = new int[] {ndTarget.getNodePosition()};
			Object[] removes = new Object[] {ndTarget};
			ndParent.remove(indices[0]);
			getDataTreeModel().nodesWereRemoved(ndParent, indices, removes);
			ndParent.insert(ndNewNode, indices[0]);
			getDataTreeModel().nodesWereInserted(ndParent, indices);
		}
		else if (parentType == DtContainerContentTypes.ContentDtAlgeSet) {
			//--- replace as Dtalge in DtAlgeSet
			if (!(newValue instanceof Dtalge)) {
				throw new IllegalArgumentException("Data object is not Dtalge : " + newValue.getClass().toString());
			}
			//--- create new node for Exalge
			//ndNewNode = new DtContainerContentLeafTreeNode(DtContainerContentTypes.ContentDtalge, ndTarget.getNodePosition(), newValue);
			ndNewNode = createDtalgeDataNode((Dtalge)newValue);
			ndNewNode.setNodePosition( ndTarget.getNodePosition() );
			//--- replace
			int[] indices = new int[] {ndTarget.getNodePosition()};
			Object[] removes = new Object[] {ndTarget};
			ndParent.remove(indices[0]);
			getDataTreeModel().nodesWereRemoved(ndParent, indices, removes);
			ndParent.insert(ndNewNode, indices[0]);
			getDataTreeModel().nodesWereInserted(ndParent, indices);
		}
		else if (parentType == DtContainerContentTypes.ContentDtSlipObjects) {
			//--- replace as named data object
			int position = ndParent.getIndex(ndTarget);
			if (position < 0)
				throw new AssertionError(position);
			ndNewNode = createNamedDataObjectTreeNode(ndTarget.getNodeName(), newValue);
			int[] indices = new int[] {position};
			Object[] removes = new Object[] {ndTarget};
			ndParent.remove(position);
			getDataTreeModel().nodesWereRemoved(ndParent, indices, removes);
			ndParent.insert(ndNewNode, position);
			getDataTreeModel().nodesWereInserted(ndParent, indices);
		}
		else if (parentType == DtContainerContentTypes.ContentDtSlipList) {
			//--- replace as DtSlip in DtSlipList
			if (!(newValue instanceof DtSlip)) {
				throw new IllegalArgumentException("Data object is not DtSlip : " + newValue.getClass().toString());
			}
			int position = ndParent.getIndex(ndTarget);
			ndNewNode = createDtSlipNode((DtSlip)newValue);
			ndNewNode.setNodePosition(position);
			int[] indices = new int[] {position};
			Object[] removes = new Object[] {ndTarget};
			ndParent.remove(position);
			getDataTreeModel().nodesWereRemoved(ndParent, indices, removes);
			ndParent.insert(ndNewNode, position);
			getDataTreeModel().nodesWereInserted(ndParent, indices);
		}
		else if (parentType == DtContainerContentTypes.ContentDtBinderSlips) {
			//--- replace as named slip object
			int position = ndParent.getIndex(ndTarget);
			if (position < 0)
				throw new AssertionError(position);
			ndNewNode = createNamedSlipObjectTreeNode(ndTarget.getNodeName(), newValue);
			int[] indices = new int[] {position};
			Object[] removes = new Object[] {ndTarget};
			ndParent.remove(position);
			getDataTreeModel().nodesWereRemoved(ndParent, indices, removes);
			ndParent.insert(ndNewNode, position);
			getDataTreeModel().nodesWereInserted(ndParent, indices);
		}
		else {
			// unsupported indexed object
			throw new IllegalArgumentException("Target tree node can not replace : " + String.valueOf(ndTarget.getContentPathString()));
		}
		
		// modified document
		setModifiedFlag(true);
		return ndNewNode;
	}
	
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
	public void exportDataObjectToCsvFile(IDtContainerContentTreeNode ndTarget, File destFile, String encoding) throws DTCContentEditTableModelConversionError, IOException
	{
		Object value = createDataObjectFromTreeNode(ndTarget);
		
		// save as CSV
		if (value instanceof exalge2.io.IDataOutput) {
			((exalge2.io.IDataOutput)value).toCSV(destFile, encoding);
		}
		else if (value instanceof dtalge.io.IDataOutput) {
			((dtalge.io.IDataOutput)value).toCSV(destFile, encoding);
		}
		else {
			throw new IllegalStateException("Converted value object from Tree node is not a data object : value.class = " + (value==null ? "null" : value.getClass().toString()));
		}
	}
	
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
			throws DTCContentEditTableModelConversionError, IOException, FactoryConfigurationError, ParserConfigurationException, DOMException, TransformerConfigurationException, TransformerException
	{
		Object value = createDataObjectFromTreeNode(ndTarget);
		
		// save as CSV
		if (value instanceof exalge2.io.IDataOutput) {
			((exalge2.io.IDataOutput)value).toXML(destFile);
		}
		else if (value instanceof dtalge.io.IDataOutput) {
			((dtalge.io.IDataOutput)value).toXML(destFile);
		}
		else {
			throw new IllegalStateException("Converted value object from Tree node is not a data object : value.class = " + (value==null ? "null" : value.getClass().toString()));
		}
	}
	
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
	public void exportDataObjectToJsonFile(IDtContainerContentTreeNode ndTarget, File destFile) throws DTCContentEditTableModelConversionError, IOException
	{
		Object value = createDataObjectFromTreeNode(ndTarget);
		
		// save as JSON
		DtJSON.serialize(destFile, value, false);	// データ型名を強制出力
	}
	
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
	public void exportSlipObjectToJsonFile(IDtContainerContentTreeNode ndTarget, File destFile) throws DTCContentEditTableModelConversionError, IOException
	{
		Object value = createNamedSlipObjectFromTreeNode(ndTarget);
		
		// save as JSON
		DtJSON.serialize(destFile, value, false);	// データ型名を強制出力
	}
	
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
	public void exportNoteToCsvFile(IDtContainerContentTreeNode ndTarget, File destFile, String encoding) throws DTCContentEditTableModelConversionError, IOException
	{
		Dtalge noteAlge = createNoteDtalgeFromTreeNode(ndTarget);
		
		// save as CSV
		noteAlge.toCSV(destFile, encoding);
	}
	
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
			throws DTCContentEditTableModelConversionError, IOException, FactoryConfigurationError, ParserConfigurationException, DOMException, TransformerConfigurationException, TransformerException
	{
		Dtalge noteAlge = createNoteDtalgeFromTreeNode(ndTarget);
		
		// save as XML
		noteAlge.toXML(destFile);
	}
	
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
	public void exportNoteToJsonFile(IDtContainerContentTreeNode ndTarget, File destFile) throws DTCContentEditTableModelConversionError, IOException
	{
		Dtalge noteAlge = createNoteDtalgeFromTreeNode(ndTarget);
		
		// save as JSON
		DtJSON.serialize(destFile, noteAlge, false);	// データ型名を強制出力
	}
	
//	/**
//	 * <em>ndTarget</em> のノートの値を、指定された値に置き換える。
//	 * @param ndTarget	対象のノートノード
//	 * @param newValue	新しい {@code Dtalge} オブジェクト
//	 * @return	<em>ndTarget</em> ノード
//	 * @throws NullPointerException	引数のいずれかが {@code null} の場合
//	 * @throws IllegalArgumentException	<em>ndTarget</em> がノートオブジェクトを示すノードではない場合、もしくは <em>newValue</em> が {@code Dtalge} オブジェクトではない場合
//	 */
//	public IDtContainerContentTreeNode replaceNoteObject(IDtContainerContentTreeNode ndTarget, Object newValue) {
//		// validation
//		if (ndTarget == null)	throw new NullPointerException("Target tree node is null");
//		if (newValue == null)	throw new NullPointerException("New value object is null");
//		if (!(newValue instanceof Dtalge))
//			throw new IllegalArgumentException("New value is not Dtalge : " + newValue.getClass().toString());
//		if (!DtContainerContentTypesManager.getInstance().isNoteObject(ndTarget.getContentType()))
//			throw new IllegalArgumentException("Target tree node is not Note of DtSlip or DtBinder : " + String.valueOf(ndTarget.getContentType()));
//		
//		// replace note
//		DtContainerContentDtalgeEditModel noteModel = new DtContainerContentDtalgeEditModel((Dtalge)newValue);
//		ndTarget.setUserObject(noteModel);
//		getDataTreeModel().nodeChanged(ndTarget);
//		
//		// modified
//		setModifiedFlag(true);
//		return ndTarget;
//	}
	
	/**
	 * <em>ndTarget</em> のノード名を、指定された名前に変更する。
	 * @param ndTarget		対象のノード
	 * @param newNodeName	新しい名前
	 * @return	<em>ndTarget</em> を返す。
	 * @throws NullPointerException	引数のいずれかが {@code null} の場合
	 * @throws IllegalArgumentException <em>newNodeName</em> が空文字列の場合、もしくは <em>ndTarget</em> がノード名を許容する形式ではない場合
	 */
	public IDtContainerContentTreeNode renameObject(IDtContainerContentTreeNode ndTarget, String newNodeName) {
		// validation
		if (ndTarget == null)	throw new NullPointerException("Target tree node is null");
		if (newNodeName == null)	throw new NullPointerException("Node name is null");
		DtContainerContentParentTreeNode ndParent = (DtContainerContentParentTreeNode)ndTarget.getParent();
		if (!DtContainerContentTypesManager.getInstance().isNamedChildren(ndParent.getContentType()))
			throw new IllegalArgumentException("Parent tree node can not include named object : " + String.valueOf(ndParent.getContentType()));
		if (newNodeName.isEmpty())
			throw new IllegalArgumentException("Node name is empty");
		
		// 親ノードから除去
		ndTarget.removeFromParent();
		
		// 名前の変更
		ndTarget.setNodeName(newNodeName);
		
		// 新しい位置に挿入
		ndParent.insertSortedByName(ndTarget);
		getDataTreeModel().nodeStructureChanged(ndParent);
		
		// modified
		setModifiedFlag(true);
		return ndTarget;
	}
	
	/**
	 * このドキュメントのタイトルを取得する。
	 * @return	このドキュメントのタイトル
	 */
	public String getTitle() {
		if (_targetFile != null)
			return _targetFile.getName();
		else
			return DtContainerEditorMessages.getInstance().newDocumentTitle;
	}
	
	/**
	 * このドキュメントの保存先ファイルを取得する。
	 * 保存先ファイルが定義されていない場合は <tt>null</tt> を返す。
	 * @return	保存先ファイル
	 */
	public File getTargetFile() {
		return _targetFile;
	}
	
	/**
	 * このドキュメントに保存先ファイルが指定されているかを判定する。
	 * @return	保存先ファイルが指定されていれば <tt>true</tt>
	 */
	public boolean hasTargetFile() {
		return (_targetFile != null);
	}
	
	/**
	 * このドキュメントの保存先を、指定されたパスに設定する。
	 * このメソッドでは、保存先の正当性検証は行わず、ターゲットを指定されたパスに
	 * 設定するのみとなる。編集状態なども変更されない。
	 * @param newTarget	新しい保存先となる抽象パス
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public void setTargetFile(File newTarget) {
		_targetFile = newTarget;
		updateLastModifiedTimeWhenLoadingTargetFile();
	}
	
	/**
	 * このドキュメントが読み込まれた時点での保存先ファイルの最終更新日時を取得する。
	 * 保存先ファイルが指定されていない場合は 0L を返す。
	 * @return	ドキュメントが読み込まれた時点での、最終更新日時
	 */
	public long lastModifiedTimeWhenLoadingTargetFile() {
		return _lastModifiedWhenLoadingTargetFile;
	}
	
	/**
	 * このドキュメントの保存先ファイルの、現在の最終更新日時を取得する。
	 * 保存先ファイルが指定されていない場合は 0L を返す。
	 * @return	最終更新日時
	 */
	public long lastModifiedTimeWhenCurrentTargetFile() {
		if (hasTargetFile()) {
			return getTargetFile().lastModified();
		} else {
			return 0L;
		}
	}
	
	/**
	 * このドキュメントが読み込まれた時点での保存先ファイルの最終更新日時を、
	 * 保存先ファイルの現在の更新日時で更新する。
	 */
	public void updateLastModifiedTimeWhenLoadingTargetFile() {
		if (hasTargetFile()) {
			_lastModifiedWhenLoadingTargetFile = getTargetFile().lastModified();
		}
	}
	
	/**
	 * このドキュメントの保存先ファイルが移動可能かを判定する。
	 * @return	移動可能な場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean canMoveTargetFile() {
		// ターゲットファイルが指定されていなければ、移動不可
		if (hasTargetFile()) {
			return false;
		}
		
		// 編集中のドキュメントなら、移動不可
		if (isModified()) {
			return false;
		}
		
		// 移動を許可
		return true;
	}
	
	/**
	 * このドキュメントが新規に作成され、一度も保存されていないかを判定する。
	 * @return	このドキュメントが新規に作成され保存されていない場合は <tt>true</tt> を返す。
	 */
	public boolean isNewDocument() {
		return _flgNew;
	}
	
	/**
	 * このドキュメントが編集されているかを判定する。
	 * @return	編集されていれば <tt>true</tt> を返す。
	 */
	public boolean isModified() {
		return _flgModified;
	}
	
	/**
	 * このドキュメントの編集状態を設定する。
	 * @param modified	編集状態とする場合は <tt>true</tt> を指定する。
	 */
	public void setModifiedFlag(boolean modified) {
		_flgModified = modified;
	}
	
	/**
	 * 現在のドキュメントに関連付けられたファイルのエンコーディング名を返す。
	 * このメソッドが返すエンコーディング名は、読み込み時もしくは保存時に
	 * 適用されたものとなる。
	 * @return	エンコーディング名(<tt>null</tt> 以外)
	 */
	public String getLastEncodingName() {
		return _lastEncoding;
	}
	
	/**
	 * 現在のドキュメントに、編集によって不正な値が含まれているかを検証する。
	 * @throws DTCContentEditTableModelConversionError	不正な値が含まれている場合
	 */
	public void validateContainerContentData() throws DTCContentEditTableModelConversionError
	{
		validateContainerContentDataInTreeNodeRecursive(getDataTreeRootNode());
	}
	
	/**
	 * 指定されたツリーノードおよびその子孫に、編集によって不正な値が含まれているかを検証する。
	 * @param node	検証対象のツリーノード
	 * @throws IllegalArgumentException	<em>node</em> が、このドキュメントのデータコンテナツリーに含まれていない場合
	 * @throws DTCContentEditTableModelConversionError	不正な値が含まれている場合
	 */
	public void validateContainerContentDataInNodeAndDescendants(IDtContainerContentTreeNode node) throws DTCContentEditTableModelConversionError
	{
		if (node != null) {
			if (!node.isNodeAncestor(getDataTreeRootNode()))
				throw new IllegalArgumentException("'note' is not descendant of the this document's tree");
			validateContainerContentDataInTreeNodeRecursive(node);
		}
	}
	
	/**
	 * 指定されたツリーノードおよびその配下に、編集によって不正な値が含まれているかを検証する
	 * @param ndTarget	検証対象のツリーノード
	 * @throws DTCContentEditTableModelConversionError	不正な値が含まれている場合
	 */
	protected void validateContainerContentDataInTreeNodeRecursive(IDtContainerContentTreeNode ndTarget) throws DTCContentEditTableModelConversionError
	{
		if (ndTarget == null)	return;
		
		// 対象ノードの検証
		Object objDataModel = ndTarget.getUserObject();
		if (objDataModel instanceof DTCContentExalgeEditModel) {
			// Exalge 編集テーブルモデル
			DTCContentExalgeEditModel model = (DTCContentExalgeEditModel)objDataModel;
			int cntRows = model.getRowCount();
			int cntCols = model.getColumnCount();
			for (int rowIndex = 0; rowIndex < cntRows; rowIndex++) {
				for (int colIndex = 0; colIndex < cntCols; colIndex++) {
					Object objValue = model.getValueAt(rowIndex, colIndex);
					if (objValue instanceof DTCContentInvalidCellValue) {
						// 不正な値
						DTCContentInvalidCellValue invalidCellValue = (DTCContentInvalidCellValue)objValue;
						String errmsg = DtContainerEditorMessages.getInstance().msgCouldNotConvertToDtalgeFromEdited + "\n" + String.valueOf(invalidCellValue.getMessage());
						throw new DTCContentEditTableModelConversionError(errmsg, ndTarget, invalidCellValue, rowIndex, colIndex);
					}
				}
			}
		}
		else if (objDataModel instanceof DTCContentDtalgeEditModel) {
			// Dtalge 編集テーブルモデル
			DTCContentDtalgeEditModel model = (DTCContentDtalgeEditModel)objDataModel;
			int cntRows = model.getRowCount();
			int cntCols = model.getColumnCount();
			for (int rowIndex = 0; rowIndex < cntRows; rowIndex++) {
				for (int colIndex = 0; colIndex < cntCols; colIndex++) {
					Object objValue = model.getValueAt(rowIndex, colIndex);
					if (objValue instanceof DTCContentInvalidCellValue) {
						// 不正な値
						DTCContentInvalidCellValue invalidCellValue = (DTCContentInvalidCellValue)objValue;
						String errmsg = DtContainerEditorMessages.getInstance().msgCouldNotConvertToDtalgeFromEdited + "\n" + String.valueOf(invalidCellValue.getMessage());
						throw new DTCContentEditTableModelConversionError(errmsg, ndTarget, invalidCellValue, rowIndex, colIndex);
					}
				}
			}
		}
		
		// 子ノードの検証
		int numChildren = ndTarget.getChildCount();
		if (numChildren > 0) {
			for (int childIndex = 0; childIndex < numChildren; childIndex++) {
				validateContainerContentDataInTreeNodeRecursive( ndTarget.getChildAt(childIndex) );
			}
		}
	}
	
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
	public IDtContainerContentTreeNode createTreeNodeByTransferTreeNode(DTCContentTransferTreeNode transferNode, DtContainerContentTypes requestContentType) {
		if (requestContentType == null) {
			requestContentType = transferNode.getContentType();
			if (requestContentType == null) {
				throw new NullPointerException();
			}
		}
		
		// コンテントタイプでノードを生成
		IDtContainerContentTreeNode newNode;
		switch (requestContentType) {
			case ContentExalge:
				newNode = createTreeNodeAsExalgeByTransferData(transferNode);
				break;
			case ContentExAlgeSet:
				newNode = createTreeNodeAsExAlgeSetByTransferData(transferNode);
				break;
			case ContentDtalge:
				newNode = createTreeNodeAsDtalgeByTransferData(transferNode);
				break;
			case ContentDtAlgeSet:
				newNode = createTreeNodeAsDtAlgeSetByTransferData(transferNode);
				break;
			case ContentDtSlipNote:
			case ContentDtBinderNote:
				newNode = createTreeNodeAsNoteByTransferData(requestContentType, transferNode);
				break;
			case ContentDtSlip:
				newNode = createTreeNodeAsDtSlipByTransferData(transferNode);
				break;
			case ContentDtSlipList:
				newNode = createTreeNodeAsDtSlipListByTransferData(transferNode);
				break;
			default:
				throw new IllegalArgumentException("Requested content type is not supported : " + String.valueOf(requestContentType));
		}
		
		return newNode;
	}
	
	/**
	 * 番号のノード名を生成する。番号は、5 桁に満たない場合は 0 でパディングし、{@code long} 型の数値で循環する。
	 * この番号は、ドキュメントのインスタンスないでのみ循環する。
	 * @param prefix	番号の前に追加する文字列
	 * @return	生成された文字列
	 * @since 1.1.0
	 */
	public String getNextNewNodeName(String prefix) {
		if (prefix != null && !prefix.isEmpty()) {
			return String.format("%s%05d", prefix, _nextNewNodeNameNumber++);
		}
		else {
			return String.format("%05d", _nextNewNodeNameNumber++);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void setNewFlag(boolean newdoc) {
		_flgNew = newdoc;
	}

	/**
	 * 指定されたツリーノードから、名前付きデータオブジェクトの要素を生成する。
	 * ここで生成されるオブジェクトの種類は、{@link IDtContainerContentTreeNode#getContentType()} が返すコンテントタイプが、以下のものに限られる。
	 * <ul>
	 * <li>{@link DtContainerContentTypes#ContentExalge}</li>
	 * <li>{@link DtContainerContentTypes#ContentExAlgeSet}</li>
	 * <li>{@link DtContainerContentTypes#ContentDtalge}</li>
	 * <li>{@link DtContainerContentTypes#ContentDtAlgeSet}</li>
	 * </ul>
	 * @param targetNode	対象のツリーノード
	 * @return	生成されたオブジェクト
	 * @throws NullPointerException	<em>targetNode</em> が {@code null} の場合
	 * @throws IllegalArgumentException	<em>targetNode</em> のコンテントタイプが、許容されたもの以外の場合
	 * @throws IllegalStateException	<em>targetNode</em> およびその子孫に格納されたデータオブジェクトが適切ではない場合
	 * @throws DTCContentEditTableModelConversionError	不正な値が含まれている場合
	 */
	protected Object createDataObjectFromTreeNode(IDtContainerContentTreeNode targetNode) throws DTCContentEditTableModelConversionError
	{
		DtContainerContentTypes contentType = targetNode.getContentType();
		Object retobj;
		switch (contentType) {
			case ContentExalge:
				{
					Object objData = targetNode.getUserObject();
					if (!(objData instanceof DTCContentExalgeEditModel)) {
						throw new IllegalStateException("Target node's data object is not DtContainerContentExalgeEditModel : " + (objData==null ? "null" : objData.getClass().toString()));
					}
					retobj = ((DTCContentExalgeEditModel)objData).getValuesAsExalge(targetNode);
					//retobj = targetNode.getUserObject();
					//if (!(retobj instanceof Exalge)) {
					//	throw new IllegalStateException("Target node's data object is not Exalge : " + (retobj==null ? "null" : retobj.getClass().toString()));
					//}
				}
				break;
			case ContentExAlgeSet:
				{
					ExAlgeSet algeset = new ExAlgeSet(targetNode.getChildCount());
					for (int index = 0; index < targetNode.getChildCount(); ++index) {
						Object childdata = targetNode.getChildAt(index).getUserObject();
						//if (!(childdata instanceof Exalge)) {
						//	String errmsg = String.format("ExAlgeSet node's child(%d) is not Exalge : %s", index, (childdata==null ? "null" : childdata.getClass().toString()));
						//	throw new IllegalStateException(errmsg);
						//}
						//algeset.add((Exalge)childdata);
						if (!(childdata instanceof DTCContentExalgeEditModel)) {
							String errmsg = String.format("ExAlgeSet node's child(%d) is not DtContainerContentExalgeEditModel : %s", index, (childdata==null ? "null" : childdata.getClass().toString()));
							throw new IllegalStateException(errmsg);
						}
						algeset.add( ((DTCContentExalgeEditModel)childdata).getValuesAsExalge(targetNode) );
					}
					retobj = algeset;
				}
				break;
			case ContentDtalge:
				{
					Object objData = targetNode.getUserObject();
					if (!(objData instanceof DTCContentDtalgeEditModel)) {
						throw new IllegalStateException("Target node's data object is not DtContainerContentDtalgeEditModel : " + (objData==null ? "null" : objData.getClass().toString()));
					}
					retobj = ((DTCContentDtalgeEditModel)objData).getValuesAsDtalge(targetNode);
					//retobj = targetNode.getUserObject();
					//if (!(retobj instanceof Dtalge)) {
					//	throw new IllegalStateException("Target node's data object is not Dtalge : " + (retobj==null ? "null" : retobj.getClass().toString()));
					//}
				}
				break;
			case ContentDtAlgeSet:
				{
					DtAlgeSet algeset = new DtAlgeSet(targetNode.getChildCount());
					for (int index = 0; index < targetNode.getChildCount(); ++index) {
						Object childdata = targetNode.getChildAt(index).getUserObject();
						//if (!(childdata instanceof Dtalge)) {
						//	String errmsg = String.format("DtAlgeSet node's child(%d) is not Dtalge : %s", index, (childdata==null ? "null" : childdata.getClass().toString()));
						//	throw new IllegalStateException(errmsg);
						//}
						//algeset.add((Dtalge)childdata);
						if (!(childdata instanceof DTCContentDtalgeEditModel)) {
							String errmsg = String.format("ExAlgeSet node's child(%d) is not DtContainerContentDtalgeEditModel : %s", index, (childdata==null ? "null" : childdata.getClass().toString()));
							throw new IllegalStateException(errmsg);
						}
						algeset.add( ((DTCContentDtalgeEditModel)childdata).getValuesAsDtalge(targetNode) );
					}
					retobj = algeset;
				}
				break;
			default:
				throw new IllegalArgumentException("Target node's content type is not a named data object : " + String.valueOf(contentType));
		}
		return retobj;
	}
	
	/**
	 * 指定されたノートを示すツリーノードから、{@code Dtalge} オブジェクトを生成する。
	 * ここで生成されるオブジェクトの種類は、{@link IDtContainerContentTreeNode#getContentType()} が返すコンテントタイプが、以下のものに限られる。
	 * <ul>
	 * <li>{@link DtContainerContentTypes#ContentDtSlipNote}</li>
	 * <li>{@link DtContainerContentTypes#ContentDtBinderNote}</li>
	 * </ul>
	 * @param targetNode	対象のツリーノード
	 * @return	生成されたオブジェクト
	 * @throws NullPointerException	<em>targetNode</em> が {@code null} の場合
	 * @throws IllegalArgumentException	<em>targetNode</em> のコンテントタイプが、許容されたもの以外の場合
	 * @throws IllegalStateException	<em>targetNode</em> およびその子孫に格納されたデータオブジェクトが適切ではない場合
	 * @throws DTCContentEditTableModelConversionError	不正な値が含まれている場合
	 */
	protected Dtalge createNoteDtalgeFromTreeNode(IDtContainerContentTreeNode targetNode) throws DTCContentEditTableModelConversionError
	{
		if (!DtContainerContentTypesManager.getInstance().isNoteObject(targetNode.getContentType())) {
			// not 'note' object
			throw new IllegalArgumentException("Target node's content type is not 'note' : " + String.valueOf(targetNode.getContentType()));
		}
		Object objNote = targetNode.getUserObject();
		if (!(objNote instanceof DTCContentDtalgeEditModel)) {
			throw new IllegalStateException("Target node's note is not DtContainerContentDtalgeEditModel : " + (objNote==null ? "null" : objNote.getClass().toString()));
		}
		return ((DTCContentDtalgeEditModel)objNote).getValuesAsDtalge(targetNode);
	}
	
	/**
	 * 指定されたツリーノードから、{@code DtSlip} オブジェクトを生成する。
	 * <em>targetNode</em> が返すコンテントタイプが、{@link DtContainerContentTypes#ContentDtSlip} のものに限る。
	 * @param targetNode	対象のツリーノード
	 * @return	生成されたオブジェクト
	 * @throws NullPointerException	<em>targetNode</em> が {@code null} の場合
	 * @throws IllegalArgumentException	<em>targetNode</em> のコンテントタイプが、許容されたもの以外の場合
	 * @throws IllegalStateException	<em>targetNode</em> およびその子孫に格納されたデータオブジェクトが適切ではない場合
	 * @throws DTCContentEditTableModelConversionError	不正な値が含まれている場合
	 */
	protected DtSlip createDtSlipObjectFromTreeNode(IDtContainerContentTreeNode targetNode) throws DTCContentEditTableModelConversionError
	{
		DtContainerContentTypes contentType = targetNode.getContentType();
		if (contentType != DtContainerContentTypes.ContentDtSlip) {
			throw new IllegalArgumentException("Target node's content type is not DtSlip : " + String.valueOf(contentType));
		}
		if (targetNode.getChildCount() < 2) {
			throw new IllegalStateException("Target node has no 'Note' or 'Objects'");
		}
		
		DtSlip retSlip = new DtSlip();
		
		// note
		IDtContainerContentTreeNode ndNote = targetNode.getChildAt(0);
		DtContainerContentTypes noteType = ndNote.getContentType();
		if (noteType != DtContainerContentTypes.ContentDtSlipNote) {
			throw new IllegalStateException("Target node's note does not found");
		}
		Object objNote = ndNote.getUserObject();
		if (!(objNote instanceof DTCContentDtalgeEditModel)) {
			throw new IllegalStateException("Target node's note is not DtContainerContentDtalgeEditModel : " + (objNote==null ? "null" : objNote.getClass().toString()));
		}
		retSlip.setNote(((DTCContentDtalgeEditModel)objNote).getValuesAsDtalge(ndNote));
		
		// named objects
		IDtContainerContentTreeNode ndObjects = targetNode.getChildAt(1);
		DtContainerContentTypes objectsType = ndObjects.getContentType();
		if (objectsType != DtContainerContentTypes.ContentDtSlipObjects) {
			throw new IllegalStateException("Target node's objects is not named map : " + String.valueOf(objectsType));
		}
		for (int index = 0; index < ndObjects.getChildCount(); ++index) {
			IDtContainerContentTreeNode ndChild = ndObjects.getChildAt(index);
			Object objChild = createDataObjectFromTreeNode(ndChild);
			retSlip.putObject(ndChild.getNodeName(), objChild);
		}
		
		return retSlip;
	}
	
	/**
	 * データスリップの名前付きデータオブジェクト用ツリーノードを生成する。
	 * @param name	オブジェクト名
	 * @param value	オブジェクトの値
	 * @return	生成されたツリーノード
	 * @throws IllegalArgumentException	<em>value</em> がデータスリップの名前付きデータオブジェクトではない、もしくは {@code null} の場合
	 */
	protected IDtContainerContentTreeNode createNamedDataObjectTreeNode(String name, Object value) {
		// get content type from value
		IDtContainerContentTreeNode ndObject;
		if (value instanceof Exalge) {
			//ndObject = new DtContainerContentLeafTreeNode(DtContainerContentTypes.ContentExalge, name, value);
			ndObject = createExalgeDataNode((Exalge)value);
			ndObject.setNodeName(name);
		}
		else if (value instanceof ExAlgeSet) {
			DtContainerContentParentTreeNode ndParent = new DtContainerContentParentTreeNode(DtContainerContentTypes.ContentExAlgeSet, name, null);
			ExAlgeSet algeset = (ExAlgeSet)value;
			int ndpos = 0;
			for (Exalge alge : algeset) {
				//DtContainerContentLeafTreeNode ndChild = new DtContainerContentLeafTreeNode(DtContainerContentTypes.ContentExalge, ndpos++, alge);
				DtContainerContentLeafTreeNode ndChild = createExalgeDataNode(alge);
				ndChild.setNodePosition(ndpos++);
				ndParent.add(ndChild);
			}
			ndObject = ndParent;
		}
		else if (value instanceof Dtalge) {
			//ndObject = new DtContainerContentLeafTreeNode(DtContainerContentTypes.ContentDtalge, name, value);
			ndObject = createDtalgeDataNode((Dtalge)value);
			ndObject.setNodeName(name);
		}
		else if (value instanceof DtAlgeSet) {
			DtContainerContentParentTreeNode ndParent = new DtContainerContentParentTreeNode(DtContainerContentTypes.ContentDtAlgeSet, name, null);
			DtAlgeSet algeset = (DtAlgeSet)value;
			int ndpos = 0;
			for (Dtalge alge : algeset) {
				//DtContainerContentLeafTreeNode ndChild = new DtContainerContentLeafTreeNode(DtContainerContentTypes.ContentDtalge, ndpos++, alge);
				DtContainerContentLeafTreeNode ndChild = createDtalgeDataNode(alge);
				ndChild.setNodePosition(ndpos++);
				ndParent.add(ndChild);
			}
			ndObject = ndParent;
		}
		else {
			throw new IllegalArgumentException("value is not named data object for DtSlip");
		}
		return ndObject;
	}

	/**
	 * 指定されたツリーノードから、名前付きスリップオブジェクトの要素を生成する。
	 * ここで生成されるオブジェクトの種類は、{@link IDtContainerContentTreeNode#getContentType()} が返すコンテントタイプが、以下のものに限られる。
	 * <ul>
	 * <li>{@link DtContainerContentTypes#ContentDtSlip}</li>
	 * <li>{@link DtContainerContentTypes#ContentDtSlipList}</li>
	 * </ul>
	 * @param targetNode	対象のツリーノード
	 * @return	生成されたオブジェクト
	 * @throws NullPointerException	<em>targetNode</em> が {@code null} の場合
	 * @throws IllegalArgumentException	<em>targetNode</em> のコンテントタイプが、許容されたもの以外の場合
	 * @throws IllegalStateException	<em>targetNode</em> およびその子孫に格納されたデータオブジェクトが適切ではない場合
	 * @throws DTCContentEditTableModelConversionError	不正な値が含まれている場合
	 */
	protected Object createNamedSlipObjectFromTreeNode(IDtContainerContentTreeNode targetNode) throws DTCContentEditTableModelConversionError
	{
		DtContainerContentTypes contentType = targetNode.getContentType();
		Object retobj;
		switch (contentType) {
			case ContentDtSlip:
				{
					retobj = createDtSlipObjectFromTreeNode(targetNode);
				}
				break;
			case ContentDtSlipList:
				{
					DtSlipList sliplist = new DtSlipList(targetNode.getChildCount());
					for (int index = 0; index < targetNode.getChildCount(); ++index) {
						DtSlip childSlip = createDtSlipObjectFromTreeNode(targetNode.getChildAt(index));
						sliplist.add(childSlip);
					}
					retobj = sliplist;
				}
				break;
			default:
				throw new IllegalArgumentException("Target node's content type is not a named slip object : " + String.valueOf(contentType));
		}
		return retobj;
	}
	
	/**
	 * データオブジェクトとして格納される交換代数元を表す {@code Exalge} オブジェクトのツリーノードを生成する。
	 * <em>valueObject</em> に {@code null} を指定した場合、要素が空の {@code Exalge} を保持するツリーノードを生成する。
	 * コンテントタイプは、{@link DtContainerContentTypes#ContentExalge} に設定される。
	 * ノード名およびインデックスは、設定されない。
	 * @param valueObject	{@code Exalge} オブジェクト、または {@code null}
	 * @return	生成されたツリーノード
	 * @since 1.1.0
	 */
	protected DtContainerContentLeafTreeNode createExalgeDataNode(Exalge valueObject) {
		DtContainerContentLeafTreeNode ndAlge = new DtContainerContentLeafTreeNode();
		ndAlge.setContentType(DtContainerContentTypes.ContentExalge);
		if (valueObject == null) {
			// create empty model
			ndAlge.setUserObject( new DTCContentExalgeEditModel() );
		}
		else {
			// set specified value
			ndAlge.setUserObject( new DTCContentExalgeEditModel(valueObject) );
		}
		return ndAlge;
	}
	
	/**
	 * 転送データの内容から、{@code Exalge} オブジェクト用ツリーノードを生成する。
	 * @param transferData	転送データ
	 * @return	生成されたツリーノード
	 * @throws NullPointerException	<em>transferData</em> が {@code null} の場合
	 * @since 1.1.0
	 */
	protected DtContainerContentLeafTreeNode createTreeNodeAsExalgeByTransferData(DTCContentTransferTreeNode transferData) {
		DtContainerContentLeafTreeNode ndAlge = new DtContainerContentLeafTreeNode();
		ndAlge.setContentType(DtContainerContentTypes.ContentExalge);
		
		if (transferData.hasCopiedData()) {
			// set specified value
			DTCContentExalgeEditModel editModel = new DTCContentExalgeEditModel();
			editModel.initByCopiedRows(transferData.getCopiedData());
			ndAlge.setUserObject(editModel);
		}
		else {
			// create empty model
			ndAlge.setUserObject( new DTCContentExalgeEditModel() );
		}
		
		return ndAlge;
	}
	
	/**
	 * 転送データの内容から、{@code ExAlgeSet} オブジェクト用ツリーノードを生成する。
	 * @param transferData	転送データ
	 * @return	生成されたツリーノード
	 * @throws NullPointerException	<em>transferData</em> が {@code null} の場合
	 * @since 1.1.0
	 */
	protected DtContainerContentParentTreeNode createTreeNodeAsExAlgeSetByTransferData(DTCContentTransferTreeNode transferData) {
		DtContainerContentParentTreeNode ndAlge = new DtContainerContentParentTreeNode();
		ndAlge.setContentType(DtContainerContentTypes.ContentExAlgeSet);
		
		if (transferData.hasChildren()) {
			DTCContentTransferTreeNode[] children = transferData.getChildren();
			for (int i = 0; i < children.length; i++) {
				IDtContainerContentTreeNode ndChild = createTreeNodeAsExalgeByTransferData(children[i]);
				ndChild.setNodePosition(i);
				ndAlge.add(ndChild);
			}
		}
		
		return ndAlge;
	}
	
	/**
	 * データオブジェクトとして格納されるデータ代数元を表す {@code Dtalge} オブジェクトのツリーノードを生成する。
	 * <em>valueObject</em> に {@code null} を指定した場合、要素が空の {@code Dtalge} を保持するツリーノードを生成する。
	 * コンテントタイプは、{@link DtContainerContentTypes#ContentDtalge} に設定される。
	 * ノード名およびインデックスは、設定されない。
	 * @param valueObject	{@code Dtalge} オブジェクト、または {@code null}
	 * @return	生成されたツリーノード
	 * @since 1.1.0
	 */
	protected DtContainerContentLeafTreeNode createDtalgeDataNode(Dtalge valueObject) {
		DtContainerContentLeafTreeNode ndAlge = new DtContainerContentLeafTreeNode();
		ndAlge.setContentType(DtContainerContentTypes.ContentDtalge);
		if (valueObject == null) {
			// create empty model
			ndAlge.setUserObject( new DTCContentDtalgeEditModel() );
		}
		else {
			// set specified value
			ndAlge.setUserObject( new DTCContentDtalgeEditModel(valueObject) );
		}
		return ndAlge;
	}
	
	/**
	 * 転送データの内容から、{@code Dtalge} オブジェクト用ツリーノードを生成する。
	 * @param transferData	転送データ
	 * @return	生成されたツリーノード
	 * @throws NullPointerException	<em>transferData</em> が {@code null} の場合
	 * @since 1.1.0
	 */
	protected DtContainerContentLeafTreeNode createTreeNodeAsDtalgeByTransferData(DTCContentTransferTreeNode transferData) {
		DtContainerContentLeafTreeNode ndAlge = new DtContainerContentLeafTreeNode();
		ndAlge.setContentType(DtContainerContentTypes.ContentDtalge);
		
		if (transferData.hasCopiedData()) {
			// set specified value
			DTCContentDtalgeEditModel editModel = new DTCContentDtalgeEditModel();
			editModel.initByCopiedRows(transferData.getCopiedData());
			ndAlge.setUserObject(editModel);
		}
		else {
			// create empty model
			ndAlge.setUserObject( new DTCContentDtalgeEditModel() );
		}
		
		return ndAlge;
	}
	
	/**
	 * 転送データの内容から、{@code DtAlgeSet} オブジェクト用ツリーノードを生成する。
	 * @param transferData	転送データ
	 * @return	生成されたツリーノード
	 * @throws NullPointerException	<em>transferData</em> が {@code null} の場合
	 * @since 1.1.0
	 */
	protected DtContainerContentParentTreeNode createTreeNodeAsDtAlgeSetByTransferData(DTCContentTransferTreeNode transferData) {
		DtContainerContentParentTreeNode ndAlge = new DtContainerContentParentTreeNode();
		ndAlge.setContentType(DtContainerContentTypes.ContentDtAlgeSet);
		
		if (transferData.hasChildren()) {
			DTCContentTransferTreeNode[] children = transferData.getChildren();
			for (int i = 0; i < children.length; i++) {
				IDtContainerContentTreeNode ndChild = createTreeNodeAsDtalgeByTransferData(children[i]);
				ndChild.setNodePosition(i);
				ndAlge.add(ndChild);
			}
		}
		
		return ndAlge;
	}
	
	/**
	 * 転送データの内容から、データスリップもしくはデータバインダーに格納される、ノートを表す {@code Dtalge} オブジェクトのツリーノードを生成する。
	 * @param noteType		{@link DtContainerContentTypes#ContentDtSlipNote} または {@link DtContainerContentTypes#ContentDtBinderNote} のいずれか
	 * @param transferData	転送データ
	 * @return	生成されたツリーノード
	 * @throws NullPointerException	<em>transferData</em> が {@code null} の場合
	 * @throws IllegalArgumentException	<em>noteType</em> が許容されている値以外の場合、もしくは <em>noteObject</em> のクラスが適切ではない場合
	 * @since 1.1.0
	 */
	protected DtContainerContentLeafTreeNode createTreeNodeAsNoteByTransferData(DtContainerContentTypes noteType, DTCContentTransferTreeNode transferData) {
		if (noteType != DtContainerContentTypes.ContentDtSlipNote && noteType != DtContainerContentTypes.ContentDtBinderNote)
			throw new IllegalArgumentException("Specified DtContainerContentTypes is not 'note' type : " + String.valueOf(noteType));
		
		DtContainerContentLeafTreeNode ndNote = createTreeNodeAsDtalgeByTransferData(transferData);
		ndNote.setContentType(noteType);
		ndNote.setNodeName(DtContainerEditorMessages.getInstance().contentNodeName_note);
		
		return ndNote;
	}
	
	/**
	 * データスリップもしくはデータバインダーに格納される、ノートを表す {@code Dtalge} オブジェクトのツリーノードを生成する。
	 * <em>noteObject</em> に {@code null} を指定した場合、要素が空の {@code Dtalge} を保持するツリーノードを生成する。
	 * @param noteType		{@link DtContainerContentTypes#ContentDtSlipNote} または {@link DtContainerContentTypes#ContentDtBinderNote} のいずれか
	 * @param noteObject	ノートの {@code Dtalge} オブジェクト、または {@code DTCContentDtalgeEditModel}、または {@code null}
	 * @return	生成されたツリーノード
	 * @throws IllegalArgumentException	<em>noteType</em> が許容されている値以外の場合、もしくは <em>noteObject</em> のクラスが適切ではない場合
	 */
	protected DtContainerContentLeafTreeNode createNoteDtalgeNode(DtContainerContentTypes noteType, Dtalge noteObject) {
		if (noteType != DtContainerContentTypes.ContentDtSlipNote && noteType != DtContainerContentTypes.ContentDtBinderNote)
			throw new IllegalArgumentException("Specified DtContainerContentTypes is not 'note' type : " + String.valueOf(noteType));
		
		DtContainerContentLeafTreeNode ndNote = new DtContainerContentLeafTreeNode();
		ndNote.setContentType(noteType);
		ndNote.setNodeName(DtContainerEditorMessages.getInstance().contentNodeName_note);
		
		if (noteObject == null) {
			// create empty note
			ndNote.setUserObject(new DTCContentDtalgeEditModel());
		}
		else {
			// set specified note
			ndNote.setUserObject(new DTCContentDtalgeEditModel(noteObject));
		}
		
		return ndNote;
	}
	
	/**
	 * データスリップ用ツリーノードを生成する。
	 * <em>slip</em> に {@code null} を指定した場合、要素が空の {@code DtSlip} を表すツリーを生成する。
	 * @param slip	データスリップ、または {@code null}
	 * @return	生成されたツリーノード
	 */
	protected DtContainerContentParentTreeNode createDtSlipNode(DtSlip slip) {
		// root
		DtContainerContentParentTreeNode ndRoot = new DtContainerContentParentTreeNode();
		ndRoot.setContentType(DtContainerContentTypes.ContentDtSlip);
		ndRoot.setUserObject(null);
		// note in slip
		DtContainerContentLeafTreeNode ndNote = createNoteDtalgeNode(DtContainerContentTypes.ContentDtSlipNote, (slip == null ? null : slip.getNote()));
		ndRoot.add(ndNote);
		// objects in slip
		DtContainerContentParentTreeNode ndObjects = new DtContainerContentParentTreeNode();
		ndObjects.setContentType(DtContainerContentTypes.ContentDtSlipObjects);
		ndObjects.setNodeName(DtContainerEditorMessages.getInstance().contentNodeName_objects);
		Map<String, IDtContainerContentTreeNode> mapObjects = new TreeMap<>();
		ndObjects.setUserObject(mapObjects);
		ndRoot.add(ndObjects);

		if (slip != null && !slip.isObjectEmpty()) {
			// add elements from objects in slip
			for (Map.Entry<String, Object> entry : slip.getUnmodifiableObjects().entrySet()) {
				IDtContainerContentTreeNode ndChild = createNamedDataObjectTreeNode(entry.getKey(), entry.getValue());
				//--- 名前管理マップへ追加(名前順)
				mapObjects.put(ndChild.getNodeName(), ndChild);
			}
			//--- 名前順にツリーへ追加
			for (Map.Entry<String, IDtContainerContentTreeNode> entry : mapObjects.entrySet()) {
				ndObjects.add(entry.getValue());
			}
		}
		
		return ndRoot;
	}
	
	/**
	 * 転送データの内容から、{@code DtSlip} の名前付きデータオブジェクト用ツリーノードを生成する。
	 * 対象の転送データが名前付きデータオブジェクトのコンテントタイプではない場合は、{@code null} を返す。
	 * @param transferData	転送データ
	 * @return	生成されたツリーノード、転送データが名前付きデータオブジェクトのコンテントタイプでない場合は {@code null}
	 * @throws NullPointerException	<em>transferData</em> が {@code null} の場合
	 * @since 1.1.0
	 */
	protected IDtContainerContentTreeNode createTreeNodeAsNamedDataObjectByTransferData(DTCContentTransferTreeNode transferData)
	{
		DtContainerContentTypes contentType = transferData.getContentType();
		IDtContainerContentTreeNode ndAlge;
		switch (contentType) {
			case ContentExalge:
				ndAlge = createTreeNodeAsExalgeByTransferData(transferData);
				ndAlge.setNodeName(transferData.getNodeName());
				break;
			case ContentExAlgeSet:
				ndAlge = createTreeNodeAsExAlgeSetByTransferData(transferData);
				ndAlge.setNodeName(transferData.getNodeName());
				break;
			case ContentDtalge:
				ndAlge = createTreeNodeAsDtalgeByTransferData(transferData);
				ndAlge.setNodeName(transferData.getNodeName());
				break;
			case ContentDtAlgeSet:
				ndAlge = createTreeNodeAsDtAlgeSetByTransferData(transferData);
				ndAlge.setNodeName(transferData.getNodeName());
				break;
			default:
				ndAlge = null;
		}
		ndAlge.setNodeName(transferData.getNodeName());
		return ndAlge;
	}
	
	/**
	 * 転送データの内容から、{@code DtSlip} オブジェクト用ツリーノードを生成する。
	 * @param transferData	転送データ
	 * @return	生成されたツリーノード
	 * @throws NullPointerException	<em>transferData</em> が {@code null} の場合
	 * @since 1.1.0
	 */
	protected DtContainerContentParentTreeNode createTreeNodeAsDtSlipByTransferData(DTCContentTransferTreeNode transferData) {
		DtContainerContentParentTreeNode ndRoot = new DtContainerContentParentTreeNode();
		ndRoot.setContentType(DtContainerContentTypes.ContentDtSlip);
		
		//--- note
		DtContainerContentLeafTreeNode ndNote;
		if (transferData.getChildCount() > 0) {
			ndNote = createTreeNodeAsNoteByTransferData(DtContainerContentTypes.ContentDtSlipNote, transferData.getChildAt(0));
		} else {
			// empty note
			ndNote = createNoteDtalgeNode(DtContainerContentTypes.ContentDtSlipNote, null);
		}
		ndRoot.add(ndNote);
		
		//--- objects
		DtContainerContentParentTreeNode ndObjects = new DtContainerContentParentTreeNode();
		ndObjects.setContentType(DtContainerContentTypes.ContentDtSlipObjects);
		ndObjects.setNodeName(DtContainerEditorMessages.getInstance().contentNodeName_objects);
		Map<String, IDtContainerContentTreeNode> mapObjects = new TreeMap<>();
		ndObjects.setUserObject(mapObjects);
		ndRoot.add(ndObjects);
		
		if (transferData.getChildCount() > 1) {
			DTCContentTransferTreeNode transObjects = transferData.getChildAt(1);
			if (transObjects.hasChildren()) {
				DTCContentTransferTreeNode[] children = transObjects.getChildren();
				for (int i = 0; i < children.length; i++) {
					IDtContainerContentTreeNode ndChild = createTreeNodeAsNamedDataObjectByTransferData(children[i]);
					if (ndChild != null) {
						// 重複しない名前を設定
						String nodeName = (ndChild.hasNodeName() ? ndChild.getNodeName() : getNextNewNodeName(DEF_COPIED_NAME_PREFIX));
						while (mapObjects.containsKey(nodeName)) {
							nodeName = getNextNewNodeName(DEF_COPIED_NAME_PREFIX);
						};
						//--- 名前管理マップへ追加(名前順)
						ndChild.setNodeName(nodeName);
						mapObjects.put(nodeName, ndChild);
					}
				}
				//--- 名前順にツリーへ追加
				for (Map.Entry<String, IDtContainerContentTreeNode> entry : mapObjects.entrySet()) {
					ndObjects.add(entry.getValue());
				}
			}
		}
		
		return ndRoot;
	}
	
	/**
	 * データバインダーの {@code DtSlipList} 要素を表すツリーを生成する。
	 * <em>sliplist</em> に {@code null} を指定した場合、要素が空の {@code DtSlipList} を表すツリーノードを生成する。
	 * @param sliplist	{@code DtSlipList} オブジェクト、または {@code null}
	 * @return	生成されたツリーノード
	 */
	protected DtContainerContentParentTreeNode createDtSlipListNode(DtSlipList sliplist) {
		DtContainerContentParentTreeNode ndSlipList = new DtContainerContentParentTreeNode();
		ndSlipList.setContentType(DtContainerContentTypes.ContentDtSlipList);
		ndSlipList.setUserObject(null);
		
		if (sliplist != null && !sliplist.isEmpty()) {
			// add elements from DtSlipList
			int ndpos = 0;
			for (DtSlip slip : sliplist) {
				DtContainerContentParentTreeNode ndChild = createDtSlipNode(slip);
				ndChild.setNodePosition(ndpos++);
				ndSlipList.add(ndChild);
			}
		}
		return ndSlipList;
	}
	
	/**
	 * 転送データの内容から、{@code DtSlipList} オブジェクト用ツリーノードを生成する。
	 * <em>transferData</em> の子ノードのコンテントタイプが {@link DtContainerContentTypes#ContentDtSlip} のもののみを子ノードとして追加する。
	 * @param transferData	転送データ
	 * @return	生成されたツリーノード
	 * @throws NullPointerException	<em>transferData</em> が {@code null} の場合
	 * @since 1.1.0
	 */
	protected DtContainerContentParentTreeNode createTreeNodeAsDtSlipListByTransferData(DTCContentTransferTreeNode transferData) {
		DtContainerContentParentTreeNode ndSlipList = new DtContainerContentParentTreeNode();
		ndSlipList.setContentType(DtContainerContentTypes.ContentDtSlipList);
		ndSlipList.setUserObject(null);
		
		if (transferData.hasChildren()) {
			DTCContentTransferTreeNode[] children = transferData.getChildren();
			int index = 0;
			for (DTCContentTransferTreeNode child : children) {
				DtContainerContentTypes ctChild = child.getContentType();
				if (DtContainerContentTypes.ContentDtSlip == ctChild) {
					// DtSlip ノードの生成
					DtContainerContentParentTreeNode ndSlip = createTreeNodeAsDtSlipByTransferData(child);
					ndSlip.setNodePosition(index++);
					ndSlipList.add(ndSlip);
				}
			}
		}

		return ndSlipList;
	}
	
	/**
	 * データバインダーの名前付きスリップオブジェクト用ツリーノードを生成する。
	 * @param name	オブジェクト名
	 * @param value	オブジェクトの値
	 * @return	生成されたツリーノード
	 * @throws IllegalArgumentException	<em>value</em> がデータバインダーの名前付きスリップオブジェクトではない、もしくは {@code null} の場合
	 */
	protected DtContainerContentParentTreeNode createNamedSlipObjectTreeNode(String name, Object value) {
		// get content type from value
		DtContainerContentParentTreeNode ndObject;
		if (value instanceof DtSlip) {
			ndObject = createDtSlipNode((DtSlip)value);
		}
		else if (value instanceof DtSlipList) {
			ndObject = createDtSlipListNode((DtSlipList)value);
		}
		else {
			throw new IllegalArgumentException("value is not named slip object for DtBinder");
		}
		ndObject.setNodeName(name);
		return ndObject;
	}
	
	/**
	 * 転送データの内容から、データバインダーの名前付きスリップオブジェクト用ツリーノードを生成する。
	 * <em>transferData</em> の子ノードのコンテントタイプが {@link DtContainerContentTypes#ContentDtSlip} もしくは {@link DtContainerContentTypes#ContentDtSlipList} ではない場合、
	 * このメソッドは {@code null} を返す。
	 * @param transferData	転送データ
	 * @return	生成されたツリーノード、<em>transferData</em> のコンテントタイプが適切でない場合は {@code null}
	 * @throws NullPointerException	<em>transferData</em> が {@code null} の場合
	 * @since 1.1.0
	 */
	protected DtContainerContentParentTreeNode createTreeNodeAsNamedSlipObjectByTransferData(DTCContentTransferTreeNode transferData) {
		DtContainerContentParentTreeNode ndObject;
		DtContainerContentTypes ctObject = transferData.getContentType();
		
		if (DtContainerContentTypes.ContentDtSlip == ctObject) {
			ndObject = createTreeNodeAsDtSlipByTransferData(transferData);
			ndObject.setNodeName(transferData.getNodeName());
		}
		else if (DtContainerContentTypes.ContentDtSlipList == ctObject) {
			ndObject = createTreeNodeAsDtSlipListByTransferData(transferData);
			ndObject.setNodeName(transferData.getNodeName());
		}
		else {
			ndObject = null;
		}
		
		return ndObject;
	}

	/**
	 * このドキュメントの編集中の構造を表すツリーモデルを生成する。
	 * @param rootNode	ツリーのルートノード
	 * @return	生成されたツリーモデル
	 */
	protected DtContainerContentTreeModel createContentTreeModel(DtContainerContentParentTreeNode rootNode) {
		DtContainerContentTreeModel model = new DtContainerContentTreeModel(rootNode);
		return model;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
