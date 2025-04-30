/*
 * @(#)DTCContentTransferTreeNode.java	1.1.0	2023/01/25
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.content.common.tree;

import dtalge.container.editor.content.DtContainerContentTypes;
import dtalge.container.editor.content.common.table.AbDTCContentAlgeEditModel;

/**
 * データコンテナ要素のノードを集約する、クリップボードコピー用ツリーノード。
 * コピーしたツリーノードに対応する、必要なデータのみを保持する最小単位のツリー要素。
 * 
 * @version 1.1.0
 * @since 1.1.0
 */
public class DTCContentTransferTreeNode
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final DTCContentTransferTreeNode[]	EMPTY_CHILDREN	= new DTCContentTransferTreeNode[0];

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** コンテントタイプ **/
	protected final DtContainerContentTypes	_contentType;
	/** ノード名、設定されていない場合は {@code null} */
	protected final String					_nodeName;
	/** このノードに関連付けられている、編集用データのコピー */
	protected DTCContentTransferTableData	_data;
	/** このノードを親とする子ノードの配列、子ノードが存在しない場合は {@code null} */
	protected DTCContentTransferTreeNode[]	_children;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public DTCContentTransferTreeNode(DtContainerContentTypes contentType) {
		this(contentType, null);
	}
	
	public DTCContentTransferTreeNode(DtContainerContentTypes contentType, String nodeName) {
		_contentType = contentType;
		_nodeName = (nodeName != null && !nodeName.isEmpty() ? nodeName : null);
		_children = EMPTY_CHILDREN;
	}
	
	public DTCContentTransferTreeNode(IDtContainerContentTreeNode srcTreeNode) {
		if (srcTreeNode == null)
			throw new NullPointerException("Source tree node is null.");
		_contentType = srcTreeNode.getContentType();
		_nodeName = srcTreeNode.getNodeName();
		Object userData = srcTreeNode.getUserObject();
		if (userData instanceof AbDTCContentAlgeEditModel) {
			// テーブルデータのコピーを要求
			DTCContentTransferTableData transferData = new DTCContentTransferTableData(srcTreeNode, (AbDTCContentAlgeEditModel)userData);
			_data = transferData;
		}
		if (srcTreeNode.getChildCount() > 0) {
			// 子を複製
			DTCContentTransferTreeNode[] newChildren = new DTCContentTransferTreeNode[srcTreeNode.getChildCount()];
			for (int i = 0; i < newChildren.length; i++) {
				newChildren[i] = new DTCContentTransferTreeNode( srcTreeNode.getChildAt(i) );
			}
			_children = newChildren;
		}
		else {
			_children = EMPTY_CHILDREN;
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public void clean() {
		if (_data != null) {
			_data.clean();
			_data = null;
		}
		
		for (DTCContentTransferTreeNode child : _children) {
			child.clean();
		}
	}
	
	public DtContainerContentTypes getContentType() {
		return _contentType;
	}
	
	public boolean hasNodeName() {
		return (_nodeName != null);
	}
	
	public String getNodeName() {
		return _nodeName;
	}
	
	public boolean hasCopiedData() {
		return (_data != null);
	}
	
	public DTCContentTransferTableData getCopiedData() {
		return _data;
	}
	
	public void setCopiedData(DTCContentTransferTableData data) {
		_data = data;
	}
	
	public boolean hasChildren() {
		return (_children.length > 0);
	}
	
	public int getChildCount() {
		return _children.length;
	}
	
	public DTCContentTransferTreeNode[] getChildren() {
		return _children;
	}
	
	public DTCContentTransferTreeNode getChildAt(int index) {
		return _children[index];
	}
	
	public void setChildren(DTCContentTransferTreeNode[] newChildren) {
		if (newChildren != null && newChildren.length > 0) {
			_children = newChildren;
		} else {
			_children = EMPTY_CHILDREN;
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
