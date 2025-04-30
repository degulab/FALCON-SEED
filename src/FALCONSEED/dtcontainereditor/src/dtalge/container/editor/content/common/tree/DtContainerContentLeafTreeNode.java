/*
 * @(#)DtContainerContentLeafTreeNode.java	1.0.0	2022/12/08
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.content.common.tree;

import java.util.Enumeration;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import dtalge.container.editor.content.DtContainerContentTypes;

/**
 * 子を持たないデータコンテナ要素を示すツリーノード。
 * 
 * @version 1.0.0
 */
public class DtContainerContentLeafTreeNode extends AbDtContainerContentTreeNode
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/**
	 * すべてのパラメーターが未設定の、新しいインスタンスを生成する。
	 */
	public DtContainerContentLeafTreeNode() {
		super();
	}
	
	/**
	 * ノード位置を示すインデックスを設定なしとし、指定されたパラメーターを保持する、新しいインスタンスを生成する。
	 * @param contentType	コンテントタイプ
	 * @param nodeName		ノード名、設定しない場合は {@code null}
	 * @param userObject	ユーザーオブジェクト
	 */
	public DtContainerContentLeafTreeNode(DtContainerContentTypes contentType, String nodeName, Object userObject) {
		super(contentType, nodeName, userObject);
	}
	
	/**
	 * ノード名を設定なしとし、指定されたパラメータを保持する、新しいインスタンスを生成する。
	 * @param contentType	コンテントタイプ
	 * @param nodePosition		親ノード内での位置を示すインデックス、設定しない場合は負の値		
	 * @param userObject	ユーザーオブジェクト
	 */
	public DtContainerContentLeafTreeNode(DtContainerContentTypes contentType, int nodePosition, Object userObject) {
		super(contentType, nodePosition, userObject);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Implement javax.swing.tree.MutableTreeNode interfaces
	//------------------------------------------------------------

	/**
	 * このノードは子を持たないので、常に {@link UnsupportedOperationException} をスローする。
	 * @param child	挿入する子ノード
	 * @param childIndex	挿入する位置を示すインデックス、終端に追加する場合は挿入前の子ノード数
	 */
	@Override
	public void insert(MutableTreeNode child, int childIndex) {
		throw new UnsupportedOperationException();
	}

	/**
	 * このノードは子を持たないので、常に {@link UnsupportedOperationException} をスローする。
	 * @param childIndex	削除する位置を示すインデックス
	 */
	@Override
	public void remove(int childIndex) {
		throw new UnsupportedOperationException();
	}

	/**
	 * このノードは子を持たないので、常に {@link UnsupportedOperationException} をスローする。
     * @param node	子ノード配列から削除するノード
	 */
	@Override
	public void remove(MutableTreeNode node) {
		throw new UnsupportedOperationException();
	}

	//------------------------------------------------------------
	// Implement javax.swing.tree.TreeNode interfaces
	//------------------------------------------------------------

	/**
	 * このノードは子ノードを持たないので、常に {@link ArrayIndexOutOfBoundsException} をスローする。
	 * @param childIndex	子ノードの位置を示すインデックス
	 * @return	指定された位置の子ノード
	 * @throws ArrayIndexOutOfBoundsException	<em>childIndex</em> が範囲外の場合
	 */
	@Override
	public IDtContainerContentTreeNode getChildAt(int childIndex) {
		throw new ArrayIndexOutOfBoundsException("node has no children");
	}

	/**
	 * このノードは子を持たないので、常に {@code 0} を返す。
	 * @return	常に {@code 0}
	 */
	@Override
	public int getChildCount() {
		return 0;
	}

	/**
	 * このノードは子を持たないので、常に {@code -1} を返す。
	 * @return	常に {@code -1}
	 */
	@Override
	public int getIndex(TreeNode node) {
		return (-1);
	}

	/**
	 * このノードは子を持たないので、常に {@code false} を返す。
	 * @return	常に {@code false}
	 */
	@Override
	public boolean getAllowsChildren() {
		return false;
	}

	/**
	 * このノードは子を持たないので、常に {@code true} を返す。
	 * @return	常に {@code true}
	 */
	@Override
	public boolean isLeaf() {
		return true;
	}

	/**
	 * このノードは子を持たないので、常に空の {@link Enumeration} を返す。
	 * @return	常に空の {@link Enumeration}
	 */
	@Override
	public Enumeration<IDtContainerContentTreeNode> children() {
		return IDtContainerContentTreeNode.EMPTY_ENUMERATION;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
