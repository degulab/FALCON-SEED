/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  Copyright 2007-2009  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)AbstractTreeModel.java	1.40	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.tree;

import java.util.EventListener;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * ファイルツリーの抽象モデルクラス。
 * 
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public abstract class AbstractTreeModel implements TreeModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** イベントリスナーリスト **/
	protected EventListenerList _listenerList = new EventListenerList();
	/** ルートノード **/
	protected TreeNode _rootNode;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AbstractTreeModel(TreeNode root) {
		super();
		this._rootNode = root;
	}

	//------------------------------------------------------------
	// implement javax.swing.tree.TreeModel interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public Object getRoot() {
		return _rootNode;
	}
	
	public void setRoot(TreeNode root) {
		Object oldRoot = this._rootNode;
		this._rootNode = root;
		if (root == null && oldRoot != null) {
			fireTreeStructureChanged(this, null);
		} else {
			nodeStructureChanged(root);
		}
	}

    /**
     * 親における子のインデックスを返す。親か子のどちらかが <tt>null</tt> の場合は、-1 を返す。
     * @param parent	このデータソースから取得された、ツリー内のノード
     * @param child		対象となるノード
     * @return	親における子のインデックス。親か子のどちらかが <tt>null</tt> の場合は -1
     */
    public int getIndexOfChild(Object parent, Object child) {
    	if (parent == null || child == null)
    		return (-1);
    	return ((TreeNode)parent).getIndex((TreeNode)child);
    }

    /**
     * 親の持つ子配列の、インデックスにある子を返す。
     * <I>parent</I> は、このデータソースからそれまでに取得されたノードでなければならない。
     * <I>index</I> が <I>parent</I> の有効なインデックス
     * (つまり、<I>index</I> &gt;= 0 && <I>index</I> &lt; getChildCount(<I>parent</I>)) である場合は、<tt>null</tt> を返さない。
     * @param parent	このデータソースから取得された、ツリー内のノード
     * @param index		インデックス
     * @return	指定されたインデックスにある <I>parent</I> の子ノード
     */
    public Object getChild(Object parent, int index) {
    	return ((TreeNode)parent).getChildAt(index);
    }

    /**
     * <I>parent</I> の子の数を返す。ノードが葉の場合や、ノードが子を持たない場合は 0 を返す。
     * <I>parent</I> は、このデータソースからそれまでに取得されたノードでなければならない。
     * @param parent	このデータソースから取得された、ツリー内のノード
     * @return	ノード <I>parent</I> の子の数
     */
    public int getChildCount(Object parent) {
    	return ((TreeNode)parent).getChildCount();
    }

    /**
     * 指定のノードが葉ノードかどうかを返す。
     * @param node	チェック対象のノード
     * @return	ノードが葉ノードの場合は <tt>true</tt>
     */
    public boolean isLeaf(Object node) {
    	return ((TreeNode)node).isLeaf();
    }

    /**
     * ユーザがこのモデルの依存するツリーノードを変更した場合に、このメソッドを呼び出す。
     * モデルは、それ自身が変更済みであることを、そのリスナーのすべてに通知する。
     */
    public void reload() {
    	reload(_rootNode);
    }

    /**
     * ユーザがこのモデルの依存するツリーノードを変更した場合に、このメソッドを呼び出す。
     * モデルは、ノード <I>node</I> より下の部分で変更されたことを、そのリスナーのすべてに通知する。
     */
    public void reload(TreeNode node) {
    	if (node != null) {
    		fireTreeStructureChanged(this, getPathToRoot(node), null, null);
    	}
    }

	public void insertNodeInto(MutableTreeNode newChild, MutableTreeNode parent, int index)
	{
		parent.insert(newChild, index);

		int[] newIndexs = new int[1];

		newIndexs[0] = index;
		nodesWereInserted(parent, newIndexs);
	}

	public void removeNodeFromParent(MutableTreeNode node) {
		MutableTreeNode parent = (MutableTreeNode)node.getParent();

		if(parent == null)
			throw new IllegalArgumentException("node does not have a parent.");

		int[]   childIndex = new int[1];
		Object[] removedArray = new Object[1];

		childIndex[0] = parent.getIndex(node);
		parent.remove(childIndex[0]);
		removedArray[0] = node;
		nodesWereRemoved(parent, childIndex, removedArray);
	}
	
	//
	// Events
	//

	/**
	 * ツリーが変更されたあとに送信された <code>TreeModelEvent</code> のリスナーを追加する。
	 * @param l	追加するリスナー
	 * @see	#removeTreeModelListener(TreeModelListener)
	 */
	public void addTreeModelListener(TreeModelListener l) {
		_listenerList.add(TreeModelListener.class, l);
	}

	/**
	 * これまでに <B>addTreeModelListener()</B> で追加されていたリスナーを削除する。
	 * @param l	削除するリスナー
	 * @see #addTreeModelListener(TreeModelListener)
	 */
	public void removeTreeModelListener(TreeModelListener l) {
		_listenerList.remove(TreeModelListener.class, l);
	}

	/**
	 * このモデルに登録された、すべてのツリーモデルリスナーからなる配列を返す。
	 * @return	このモデルの全 <code>TreeModelListener</code>。ツリーモデルリスナーが登録されていない場合は空の配列
	 * @see #addTreeModelListener(TreeModelListener)
	 * @see #removeTreeModelListener(TreeModelListener)
	 */
	public TreeModelListener[] getTreeModelListeners() {
		return (TreeModelListener[])_listenerList.getListeners(TreeModelListener.class);
	}

	/**
	 * このモデルに <code><em>Foo</em>Listener</code> として登録されているすべてのオブジェクトの配列を返す。
	 * この <code><em>Foo</em>Listener</code> は <code>add<em>Foo</em>Listener</code> として登録されたものである。
	 * <code><em>Foo</em>Listener</code> などのクラスリテラルを使用して、<code>listenerType</code> 引数を指定できる。
	 * @param listenerType	要求されるリスナーの型。<code>java.util.EventListener</code> の下位インタフェースを指定
	 * @return	このコンポーネントに <code><em>Foo</em>Listener</code> として登録されているすべてのオブジェクトの配列。リスナーが登録されていない場合は空の配列
	 * @throws ClassCastException	<code>listenerType</code> が <code>java.util.EventListener</code> を実装するクラスまたはインタフェースを指定しない場合
	 * @see #getTreeModelListeners()
	 */
	public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
		return _listenerList.getListeners(listenerType);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	public void nodeChanged(TreeNode node) {
		if (_listenerList != null && node != null) {
			TreeNode parent = node.getParent();
			if (parent != null) {
				int anIndex = parent.getIndex(node);
				if (anIndex >= 0) {
					int[] cIndices = new int[]{anIndex};
					nodesChanged(parent, cIndices);
				}
			}
			else if (node == getRoot()) {
				nodesChanged(node, null);
			}
		}
	}
    
    public void nodesChanged(TreeNode node, int[] childIndices) {
    	if (node != null) {
    		if (childIndices != null) {
    			int num = childIndices.length;
    			if (num > 0) {
    				Object[] cChildren = new Object[num];
    				for (int i = 0; i < num; i++) {
    					cChildren[i] = node.getChildAt(childIndices[i]);
    				}
    				fireTreeNodesChanged(this, getPathToRoot(node), childIndices, cChildren);
    			}
    		}
    		else if (node == getRoot()) {
    			fireTreeNodesChanged(this, getPathToRoot(node), null, null);
    		}
    	}
    }
    
    public void nodesWereInserted(TreeNode node, int[] childIndices) {
    	if (_listenerList != null && node != null && childIndices != null && childIndices.length > 0) {
    		int num = childIndices.length;
    		Object[] newChildren = new Object[num];
    		for (int i = 0; i < num; i++) {
    			newChildren[i] = node.getChildAt(childIndices[i]);
    		}
    		fireTreeNodesInserted(this, getPathToRoot(node), childIndices, newChildren);
    	}
    }
    
    public void nodesWereRemoved(TreeNode node, int[] childIndices, Object[] removedChildren) {
    	if (node != null && childIndices != null) {
    		fireTreeNodesRemoved(this, getPathToRoot(node), childIndices, removedChildren);
    	}
    }
    
    public void nodeStructureChanged(TreeNode node) {
    	if (node != null) {
    		fireTreeStructureChanged(this, getPathToRoot(node), null, null);
    	}
    }
    
    public TreeNode[] getPathToRoot(TreeNode node) {
    	return getPathToRoot(node, 0);
    }
    
    protected TreeNode[] getPathToRoot(TreeNode node, int depth) {
    	TreeNode[] retNodes;
    	if (node == null) {
    		if (depth == 0)
    			return null;
    		else
    			retNodes = new TreeNode[depth];
    	}
    	else {
    		depth++;
    		if (node == _rootNode)
    			retNodes = new TreeNode[depth];
    		else
    			retNodes = getPathToRoot(node.getParent(), depth);
    		retNodes[retNodes.length - depth] = node;
    	}
    	return retNodes;
	}

	//
	// Events
	//
    
	protected void fireTreeNodesChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
		Object[] listeners = _listenerList.getListenerList();
		TreeModelEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==TreeModelListener.class) {
				if (e == null) {
					e = new TreeModelEvent(source, path, childIndices, children);
				}
				((TreeModelListener)listeners[i+1]).treeNodesChanged(e);
			}          
		}
	}

	protected void fireTreeNodesInserted(Object source, Object[] path, int[] childIndices, Object[] children) {
		Object[] listeners = _listenerList.getListenerList();
		TreeModelEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==TreeModelListener.class) {
				if (e == null) {
					e = new TreeModelEvent(source, path, childIndices, children);
				}
				((TreeModelListener)listeners[i+1]).treeNodesInserted(e);
			}          
		}
	}
	
	protected void fireTreeNodesRemoved(Object source, Object[] path, int[] childIndices, Object[] children) {
		Object[] listeners = _listenerList.getListenerList();
		TreeModelEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==TreeModelListener.class) {
				if (e == null) {
					e = new TreeModelEvent(source, path, childIndices, children);
				}
				((TreeModelListener)listeners[i+1]).treeNodesRemoved(e);
			}          
		}
	}
	
	protected void fireTreeStructureChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
		Object[] listeners = _listenerList.getListenerList();
		TreeModelEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==TreeModelListener.class) {
				if (e == null) {
					e = new TreeModelEvent(source, path, childIndices, children);
				}
				((TreeModelListener)listeners[i+1]).treeStructureChanged(e);
			}          
		}
	}
	
	private void fireTreeStructureChanged(Object source, TreePath path) {
		Object[] listeners = _listenerList.getListenerList();
		TreeModelEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==TreeModelListener.class) {
				if (e == null) {
					e = new TreeModelEvent(source, path);
				}
				((TreeModelListener)listeners[i+1]).treeStructureChanged(e);
			}
		}
	}
}
