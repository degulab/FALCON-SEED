/*
 * @(#)DtContainerContentParentTreeNode.java	1.0.0	2022/12/05
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.content.common.tree;

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import dtalge.container.editor.content.DtContainerContentTypes;

public class DtContainerContentParentTreeNode extends AbDtContainerContentTreeNode
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static protected final DtContainerContentLeafTreeNode	_dummyNodeForBinarySearchByName = new DtContainerContentLeafTreeNode();
	
	/** 子ノード配列 **/
	protected Vector<IDtContainerContentTreeNode>	_children;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/**
	 * すべてのパラメーターが未設定の、新しいインスタンスを生成する。
	 */
	public DtContainerContentParentTreeNode() {
		super();
	}
	
	/**
	 * ノード位置を示すインデックスを設定なしとし、指定されたパラメーターを保持する、新しいインスタンスを生成する。
	 * @param contentType	コンテントタイプ
	 * @param nodeName		ノード名、設定しない場合は {@code null}
	 * @param userObject	ユーザーオブジェクト
	 */
	public DtContainerContentParentTreeNode(DtContainerContentTypes contentType, String nodeName, Object userObject) {
		super(contentType, nodeName, userObject);
	}
	
	/**
	 * ノード名を設定なしとし、指定されたパラメータを保持する、新しいインスタンスを生成する。
	 * @param contentType	コンテントタイプ
	 * @param nodePosition		親ノード内での位置を示すインデックス、設定しない場合は負の値		
	 * @param userObject	ユーザーオブジェクト
	 */
	public DtContainerContentParentTreeNode(DtContainerContentTypes contentType, int nodePosition, Object userObject) {
		super(contentType, nodePosition, userObject);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * このノードがツリーのルートかどうかを判定する。
	 * ルートはツリー内で {@code null} の親を持つ唯一のノードであり、このノードの親が {@code null} であれば {@code true} を返す。
	 * @return	ルートであれば {@code true}
	 */
	public boolean isRoot() {
		return (getParent() == null);
	}
	
	/**
	 * 指定されたノードの親ノードが、このノードかどうかを判定する。
	 * このノードが子ノードを一つも持たない場合、このノードが指定されたノードの親ノードでも {@code false} を返す。
	 * @param aNode	判定するノード
	 * @return	このノードが子ノードを一つ以上持ち、指定されたノードの親ノードがこのノード自身であれば {@code true}
	 */
	public boolean isNodeChild(TreeNode aNode) {
		if (aNode != null && getChildCount() > 0 && aNode.getParent() == this) {
			return true;
		}
		// not child
		return false;
	}
	
	/**
	 * <em>anotherNode</em> がこのノードと同じ親を持つノードであるかどうかを判定する。
	 * <em>anotherNode</em> が {@code null} の場合、このメソッドは {@code false} を返す。
	 * @param anotherNode	判定するノード
	 * @return	<em>anotherNode</em> がこのノードの兄弟であれば {@code true}
	 */
	public boolean isNodeSibling(TreeNode anotherNode) {
		if (anotherNode != null) {
			if (anotherNode == this) {
				return true;
			}
			else if (getParent() == anotherNode.getParent()) {
				return true;
			}
		}
		// not sibling
		return false;
	}

    /**
     * このノードの子ノードをすべて削除し、それらの親ノードを {@code null} に設定する。
     */
    public void removeAllChildren() {
        for (int i = getChildCount()-1; i >= 0; i--) {
            remove(i);
        }
    }
    
    /**
     * <em>newChild</em> の親ノードから <em>newChild</em> を削除し、
     * このノードの子ノード配列の終端に <em>newChild</em> を追加した後、
     * <em>newChild</em> の親ノードにこのノードを設定する。
     * @param newChild	子ノード配列の終端に追加するノード
	 * @throws NullPointerException	<em>child</em> が {@code null} の場合
	 * @throws ClassCastException	<em>child</em> が {@link IDtContainerContentTreeNode} の実装ではない場合
	 * @throws IllegalArgumentException	<em>child</em> がこのノードの上位ノードもしくはこのノード自身の場合
     */
    public void add(IDtContainerContentTreeNode newChild) {
		IDtContainerContentTreeNode ndNewChild = (IDtContainerContentTreeNode)newChild;	// check class
		IDtContainerContentTreeNode ndOldParent = ndNewChild.getParent();				// check null
		if (isNodeAncestor(ndNewChild)) {
			throw new IllegalArgumentException("new child is an ancestor");
		}
		
		if (ndOldParent != null) {
			ndOldParent.remove(ndNewChild);
		}
		
		ndNewChild.setParent(this);
		ensureChildArray();
		_children.addElement(ndNewChild);
    }

    /**
     * Removes <code>newChild</code> from its parent and makes it a child of
     * this node by adding it to the end of this node's child array.
     *
     * @see             #insert
     * @param   newChild        node to add as a child of this node
     * @exception       IllegalArgumentException    if <code>newChild</code>
     *                                          is null
     * @exception       IllegalStateException   if this node does not allow
     *                                          children
     */
    public void add(MutableTreeNode newChild) {
        if(newChild != null && newChild.getParent() == this)
            insert(newChild, getChildCount() - 1);
        else
            insert(newChild, getChildCount());
    }
    
    /**
     * 子ノードが名前順にソートされていることを前提として、指定された名前の位置を検索する。
     * 指定された名前が子ノードに存在しない場合、その名前で挿入可能な位置を返す。
     * 
     * @param nodeName	判定するノード名
     * @return	指定された名前を持つ子ノードの位置を示すインデックスを返す。
     * 			存在しない場合は、挿入可能な位置として、(-(挿入位置インデックス)-1) を返す。
     */
    public int findSortedPositionByName(String nodeName) {
    	_dummyNodeForBinarySearchByName.setNodeName(nodeName);
    	return findSortedPositionByName(_dummyNodeForBinarySearchByName);
    }
    
    /**
     * 子ノードが名前順にソートされていることを前提として、指定されたノードが持つ名前の位置を検索する。
     * 指定されたノードが持つ名前が子ノードに存在しない場合、その名前で挿入可能な位置を返す。
     * 
     * @param nodeName	判定するノード
     * @return	指定されたノードが持つ名前と同名の子ノードの位置を示すインデックスを返す。
     * 			存在しない場合は、挿入可能な位置として、(-(挿入位置インデックス)-1) を返す。
     * @throws NullPointerException <em>node</em> が {@code null} の場合
     */
    public int findSortedPositionByName(IDtContainerContentTreeNode node) {
    	if (_children == null || _children.isEmpty()) {
    		// 新規追加
    		return (-1);
    	}
    	
    	// 検索
    	return  Collections.binarySearch(_children, node, DtContainerContentTreeNodeNameComparator.instance);
    }
    
    /**
     * <em>newChild</em> の親ノードから <em>newChild</em> を削除し、
     * 名前順にソートされていることを前提として、適切な位置に <em>newChild</em> を追加した後、
     * <em>newChild</em> の親ノードにこのノードを設定する。
     * @param newChild	子ノード配列に追加するノード
     * @return	ノードが挿入された位置を示すインデックス
	 * @throws NullPointerException	<em>child</em> が {@code null} の場合
	 * @throws ClassCastException	<em>child</em> が {@link IDtContainerContentTreeNode} の実装ではない場合
	 * @throws IllegalArgumentException	<em>child</em> がこのノードの上位ノードもしくはこのノード自身の場合
     */
    public int insertSortedByName(IDtContainerContentTreeNode newChild) {
		IDtContainerContentTreeNode ndNewChild = (IDtContainerContentTreeNode)newChild;	// check class
		IDtContainerContentTreeNode ndOldParent = ndNewChild.getParent();				// check null
		if (isNodeAncestor(ndNewChild)) {
			throw new IllegalArgumentException("new child is an ancestor");
		}
		
		if (ndOldParent != null) {
			ndOldParent.remove(ndNewChild);
		}
		
		ndNewChild.setParent(this);
		ensureChildArray();

		int newpos;
		if (_children.isEmpty()) {
			// 追加
			newpos = 0;
			_children.add(newChild);
		}
		else {
			// 名前でソート
			newpos = Collections.binarySearch(_children, newChild, DtContainerContentTreeNodeNameComparator.instance);
			if (newpos < 0) {
				// 挿入位置
				newpos = -(newpos + 1);
			}
			else {
				// 同一順位の存在
				++newpos;
				for (; newpos < _children.size(); newpos++) {
					if (DtContainerContentTreeNodeNameComparator.instance.compare(_children.get(newpos), ndNewChild) != 0) {
						// 同一順位の再下端を挿入位置とする
						break;
					}
				}
			}
			//--- 挿入
			_children.insertElementAt(ndNewChild, newpos);
		}
		return newpos;
    }

	//------------------------------------------------------------
	// Implement IDtContainerContentTreeNode interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Implement javax.swing.tree.MutableTreeNode interfaces
	//------------------------------------------------------------

	/**
	 * 子ノードを指定された位置に挿入する。
	 * @param child	挿入する子ノード
	 * @param childIndex	挿入する位置を示すインデックス、終端に追加する場合は挿入前の子ノード数
	 * @throws NullPointerException	<em>child</em> が {@code null} の場合
	 * @throws ClassCastException	<em>child</em> が {@link IDtContainerContentTreeNode} の実装ではない場合
	 * @throws ArrayIndexOutOfBoundsException	<em>childIndex</em> が範囲外の場合
	 * @throws IllegalArgumentException	<em>child</em> がこのノードの上位ノードもしくはこのノード自身の場合
	 */
	public void insert(MutableTreeNode child, int childIndex) {
		IDtContainerContentTreeNode ndNewChild = (IDtContainerContentTreeNode)child;	// check class
		IDtContainerContentTreeNode ndOldParent = ndNewChild.getParent();				// check null
		if (isNodeAncestor(ndNewChild)) {
			throw new IllegalArgumentException("new child is an ancestor");
		}

		if (ndOldParent != null) {
			ndOldParent.remove(ndNewChild);
		}
		ndNewChild.setParent(this);
		ensureChildArray();
		_children.insertElementAt(ndNewChild, childIndex);
	}

	/**
	 * 指定された位置の子ノードを削除し、その子ノードの親に {@code null} を設定する。
	 * @param childIndex	削除する位置を示すインデックス
	 * @throws ArrayIndexOutOfBoundsException	<em>childIndex</em> が範囲外の場合
	 */
    public void remove(int childIndex) {
    	IDtContainerContentTreeNode ndChild = getChildAt(childIndex);
    	_children.remove(childIndex);
    	ndChild.setParent(null);
    }

    /**
     * 指定されたノードを子ノード配列から削除し、削除された子ノードの親を {@code null} にする。
     * 指定されたノードが子ノードではない場合、このメソッドは何もしない。
     * @param node	子ノード配列から削除するノード
     */
    public void remove(MutableTreeNode node) {
    	if (_children != null) {
    		int childIndex = _children.indexOf(node);
    		if (childIndex >= 0) {
    			_children.remove(childIndex);
    			node.setParent(null);
    		}
    	}
    }

	//------------------------------------------------------------
	// Implement javax.swing.tree.TreeNode interfaces
	//------------------------------------------------------------

	/**
	 * このノードの子ノード配列にある、指定された位置の子ノードを取得する。
	 * @param childIndex	子ノードの位置を示すインデックス
	 * @return	指定された位置の子ノード
	 * @throws ArrayIndexOutOfBoundsException	<em>childIndex</em> が範囲外の場合
	 */
	@Override
	public IDtContainerContentTreeNode getChildAt(int childIndex) {
		if (_children == null) {
			throw new ArrayIndexOutOfBoundsException("node has no children");
		}
		return _children.get(childIndex);
	}

	/**
	 * このノードの子ノード数を取得する。
	 * @return	子ノード数
	 */
	@Override
	public int getChildCount() {
		return (_children==null ? 0 : _children.size());
	}

	/**
	 * このノードの子ノード配列にある、<em>node</em> の位置を取得する。
	 * <em>node</em> が子ノード配列に存在しない場合は、{@code -1} を返す。
	 * <em>node</em> の親ノードがこのノード自身でない場合、子ノードとはみなさないため、このメソッドは  {@code -1} を返す。
	 * @return	<em>node</em> の子ノード配列内のインデックス、子ノードでない場合は {@code -1}
	 */
	@Override
	public int getIndex(TreeNode node) {
		if (node != null && _children != null && node.getParent() == this) {
			return _children.indexOf(node);
		}
		// not found
		return (-1);
	}

	/**
	 * このノードは子を持つので、常に {@code true} を返す。
	 * @return	常に {@code true}
	 */
	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	/**
	 * このノードは子を持つので、常に {@code false} を返す。
	 * @return	常に {@code false}
	 */
	@Override
	public boolean isLeaf() {
		return false;
	}

	/**
	 * このノードの子ノードの順方向列挙を取得する。
	 * このノードの子ノード配列を変更すると、変更前に作成された子ノード列挙はどれも無効となる。
	 * @return	新たに作成された子ノードの順方向列挙を表す {@link Enumeration}
	 */
	@Override
	public Enumeration<IDtContainerContentTreeNode> children() {
		if (_children == null) {
			return IDtContainerContentTreeNode.EMPTY_ENUMERATION;
		} else {
			return _children.elements();
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	/**
	 * 子ノード配列(リスト)を生成する。
	 * @return	子ノード配列
	 */
	protected Vector<IDtContainerContentTreeNode> createChildArray() {
		return new Vector<IDtContainerContentTreeNode>();
	}
	
	/**
	 * 子ノード配列(リスト)が生成されていなければ生成する。
	 */
	protected void ensureChildArray() {
		if (_children == null) {
			_children = createChildArray();
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
    
    static public class DtContainerContentTreeNodeNameComparator implements Comparator<IDtContainerContentTreeNode>
    {
    	static public final DtContainerContentTreeNodeNameComparator instance = new DtContainerContentTreeNodeNameComparator();
    	
		@Override
		public int compare(IDtContainerContentTreeNode node1, IDtContainerContentTreeNode node2) {
			String name1 = node1.getNodeName();
			String name2 = node2.getNodeName();
			
			if (name1 == null) {
				if (name2 == null) {
					return 0;
				} else {
					// name1 < name2
					return (-1);
				}
			}
			else if (name2 == null) {
				// name1 > name2
				return 1;
			}
			else {
				return name1.compareTo(name2);
			}
		}
    }
}
