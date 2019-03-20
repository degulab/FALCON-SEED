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
 * @(#)VirtualFileTreeNode.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.tree;

import java.net.URI;
import java.util.Collections;
import java.util.Comparator;
import java.util.EmptyStackException;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.Vector;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import ssac.util.io.VirtualFile;

/**
 * 抽象パスを持つ、<code>MutableTreeNode</code> の実装。
 * このクラスは {@link VirtualFile} オブジェクトをフィールドに持ち、
 * フィールドは不変とする。
 * 
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public class VirtualFileTreeNode implements MutableTreeNode, Cloneable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final Enumeration<VirtualFileTreeNode> EMPTY_ENUMERATION
	= new Enumeration<VirtualFileTreeNode>() {
		public boolean hasMoreElements() { return false; }
		public VirtualFileTreeNode nextElement() {
			throw new NoSuchElementException("No more elements.");
		}
	};

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 親ノード **/
	protected VirtualFileTreeNode	_parent;
	/** 子ノード **/
	protected Vector<VirtualFileTreeNode>	_children;
	/** ファイルオブジェクト。このオブジェクトは <tt>null</tt> であってはならない **/
	protected VirtualFile	_file;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public VirtualFileTreeNode(VirtualFile file) {
		if (file == null)
			throw new IllegalArgumentException("'file' argument is null.");
		this._file = file;
		this._parent = null;
		this._children = null;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このオブジェクトが保持しているファイルオブジェクトを返す。
	 */
	public VirtualFile getFileObject() {
		return _file;
	}

	/**
	 * このオブジェクトに新しいファイルオブジェクトを設定する。
	 * <p><b>(注)</b>
	 * <blockquote>
	 * このメソッドでは、ファイルオブジェクトを変更しても、子ノードの構成は変更されない。
	 * </blockquote>
	 * @param newFile	新しいファイルオブジェクト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public void setFileObject(VirtualFile newFile) {
		if (newFile == null)
			throw new NullPointerException("'newFile' argument is null.");
		this._file = newFile;
	}

	/**
	 * このノードが示すファイルのパスを含まないファイル名を返す。
	 */
	public String getFilename() {
		return _file.getName();
	}

	/**
	 * このノードが保持するファイルオブジェクトのパス文字列を返す。
	 */
	public String getFilePath() {
		return _file.getPath();
	}

	/**
	 * このノードが保持するファイルオブジェクトの絶対パス文字列を返す。
	 */
	public String getAbsolutePath() {
		return _file.getAbsolutePath();
	}
	
	/**
	 * このノードが示すファイルをアプリケーションが読み込める場合に <tt>true</tt> を返す。
	 */
	public boolean canRead() {
		return _file.canRead();
	}
	
	/**
	 * このノードが示すファイルにアプリケーションが書き込める場合に <tt>true</tt> を返す。
	 */
	public boolean canWrite() {
		return _file.canWrite();
	}

	/**
	 * このノードが保持するファイルオブジェクトが絶対パスを表す抽象パスの場合に <tt>true</tt> を返す。
	 */
	public boolean isAbsolute() {
		return _file.isAbsolute();
	}
	
	/**
	 * このノードがディレクトリである場合に <tt>true</tt> を返す。
	 */
	public boolean isDirectory() {
		return _file.isDirectory();
	}
	
	/**
	 * このノードが普通のファイルである場合に <tt>true</tt> を返す。
	 */
	public boolean isFile() {
		return _file.isFile();
	}
	
	/**
	 * このノードが隠しファイルである場合に <tt>true</tt> を返す。
	 */
	public boolean isHidden() {
		return _file.isHidden();
	}
	
	/**
	 * このノードが存在する場合に <tt>true</tt> を返す。
	 */
	public boolean exists() {
		return _file.exists();
	}
	
	/**
	 * このノードが示すファイルが最後に変更された時刻を返す。
	 * @return	アイテムが最後に変更された時刻を表す <code>long</code> 値。
	 * 			エポック (1970 年 1 月 1 日 0 時 0 分 0 秒、グリニッジ標準時) からミリ秒単位で測定。
	 * 			ファイルが存在しないか、入出力エラーが発生した場合は 0L
	 */
	public long lastModified() {
		return _file.lastModified();
	}
	
	/**
	 * このノードが示すファイルの長さ(大きさ)を返す。ディレクトリの場合、戻り値は不定。
	 * @return	このファイルの長さ (バイト単位)。アイテムが存在しない場合は 0L
	 */
	public long length() {
		return _file.length();
	}
	
	/**
	 * この抽象パスを、絶対位置を示す URI に変換する。
	 * このメソッドが返す URI から、この <code>ModuleItem</code> インスタンスを復元できる。
	 * @return このオブジェクトの絶対位置を表す URI
	 */
	public URI toURI() {
		return _file.toURI();
	}

	/**
	 * このオブジェクトのパス文字列を返す。
	 */
	public String toString() {
		return _file.toString();
	}

	/**
	 * このオブジェクト固有のフィールドを更新する。
	 * このメソッドでは、親ノードや子ノードの階層を変更するものとはならない。
	 */
	public void refreshProperties() {
		// place holder
	}

	/**
	 * このオブジェクトのコピーを返す。
	 * このコピーではファイルオブジェクトのシャローコピーが保持され、
	 * 親ノードや子ノードの情報はクリアされている。
	 */
	public VirtualFileTreeNode clone() {
		VirtualFileTreeNode newNode = null;

		try {
			newNode = (VirtualFileTreeNode)super.clone();

			// shallow copy -- the new node has no parent or children
			newNode._children = null;
			newNode._parent = null;
		} catch (CloneNotSupportedException e) {
			throw new Error(e.toString());
		}

		return newNode;
	}
	
	public void add(VirtualFileTreeNode newChild) {
		if (newChild != null && newChild.getParent() == this) {
			insert(newChild, getChildCount() - 1);
		} else {
			insert(newChild, getChildCount());
		}
	}
	
	public void removeAllChildren() {
		for (int i = getChildCount()-1; i >= 0; i--) {
			remove(i);
		}
	}
	
	public void sortChildren(Comparator<? super VirtualFileTreeNode> nodeComparator) {
		Collections.sort(_children, nodeComparator);
	}
	
	public int binarySearch(VirtualFileTreeNode child, Comparator<? super VirtualFileTreeNode> nodeComparator) {
		if (child == null)
			throw new IllegalArgumentException("child argument is null.");
		if (_children == null || _children.isEmpty())
			return (-1);
		if (nodeComparator == null) {
			// 終端のインデックス
			return (-getChildCount());
		}
		
		return Collections.binarySearch(_children, child, nodeComparator);
	}

	/**
	 * <code>fileObject</code> を格納するノードを、このノードも含む上位ノードから
	 * 取得する。自身、もしくは上位ノードに <code>fileObject</code> を格納するノードが
	 * 存在しない場合は <tt>null</tt> を返す。
	 * <p>
	 * このメソッドでは、ノードに格納されているオブジェクトと等しいかを判定するため、
	 * <code>equals</code> メソッドを使用する。
	 * @param fileObject	検索するキーとなるオブジェクト
	 * @return	<code>fileObject</code> を格納する上位ノードを返す。
	 */
	public VirtualFileTreeNode getAncestorNodeByFile(Object fileObject) {
		VirtualFileTreeNode ancestor = this;
		do {
			if (ancestor.getFileObject().equals(fileObject)) {
				return ancestor;
			}
		} while ((ancestor = ancestor.getParent()) != null);

		// not found
		return null;
	}

	/**
	 * <code>fileObject</code> を格納するノードを、このノードも含む下位ノードから
	 * 取得する。自身、もしくは下位ノードに <code>fileObject</code> を格納するノードが
	 * 存在しない場合は <tt>null</tt> を返す。
	 * <p>
	 * このメソッドでは、ノードに格納されているオブジェクトと等しいかを判定するため、
	 * <code>equals</code> メソッドを使用する。
	 * @param fileObject	検索するキーとなるオブジェクト
	 * @return	<code>fileObject</code> を格納する下位ノードを返す。
	 */
	public VirtualFileTreeNode getDescendantNodeByFile(Object fileObject) {
		if (getFileObject().equals(fileObject)) {
			return this;
		}
		
		int numChildren = getChildCount();
		for (int i = 0; i < numChildren; i++) {
			VirtualFileTreeNode target = getChildAt(i).getDescendantNodeByFile(fileObject);
			if (target != null) {
				return target;
			}
		}

		// not found
		return null;
	}

	/**
	 * <code>fileObject</code> を格納するノードを、このノードの子ノードから取得する。
	 * このメソッドでは、自身のノードは検証しない。
	 * 子ノードに <code>fileObject</code> を格納するノードが存在しない場合は <tt>null</tt> を返す。
	 * <p>
	 * このメソッドでは、ノードに格納されているオブジェクトと等しいかを判定するため、
	 * <code>equals</code> メソッドを使用する。
	 * @param fileObject	検索するキーとなるオブジェクト
	 * @return	<code>fileObject</code> を格納する子ノードを返す。
	 */
	public VirtualFileTreeNode getChildNodeByFile(Object fileObject) {
		int numChildren = getChildCount();
		for (int i = 0; i < numChildren; i++) {
			VirtualFileTreeNode child = getChildAt(i);
			if (child.getFileObject().equals(fileObject)) {
				return child;
			}
		}
		
		// not found
		return null;
	}

	/**
	 * <code>fileObject</code> を格納するノードが、自身もしくは上位ノードに
	 * 存在する場合に <tt>null</tt> を返す。
	 * <p>
	 * このメソッドでは、ノードに格納されているオブジェクトと等しいかを判定するため、
	 * <code>equals</code> メソッドを使用する。
	 * @param fileObject	検索するキーとなるオブジェクト
	 * @return	このノードが <code>fileObject</code> を格納するノードの下位ノードである場合は <tt>true</tt>
	 */
	public boolean isFileAncestor(Object fileObject) {
		return (getAncestorNodeByFile(fileObject) != null);
	}

	/**
	 * <code>fileObject</code> を格納するノードが、自身もしくは下位ノードに
	 * 存在する場合に <tt>null</tt> を返す。
	 * <p>
	 * このメソッドでは、ノードに格納されているオブジェクトと等しいかを判定するため、
	 * <code>equals</code> メソッドを使用する。
	 * @param fileObject	検索するキーとなるオブジェクト
	 * @return	このノードが <code>fileObject</code> を格納するノードの上位ノードである場合は <tt>true</tt>
	 */
	public boolean isFileDescendant(Object fileObject) {
		return (getDescendantNodeByFile(fileObject) != null);
	}
	
	/**
	 * <code>fileObject</code> を格納するノードが、子ノードに
	 * 存在する場合に <tt>null</tt> を返す。
	 * <p>
	 * このメソッドでは、ノードに格納されているオブジェクトと等しいかを判定するため、
	 * <code>equals</code> メソッドを使用する。
	 * @param fileObject	検索するキーとなるオブジェクト
	 * @return	このノードが <code>fileObject</code> を格納するノードの親ノードである場合は <tt>true</tt>
	 */
	public boolean isFileChild(Object fileObject) {
		return (getChildNodeByFile(fileObject) != null);
	}

	//------------------------------------------------------------
	// Implements MutableTreeNode interfaces
	//------------------------------------------------------------

	public void insert(MutableTreeNode newNode, int childIndex) {
		VirtualFileTreeNode newChild = (VirtualFileTreeNode)newNode;
		if (!getAllowsChildren())
			throw new IllegalStateException("node does not allow children.");
		if (newChild == null)
			throw new IllegalArgumentException("newChild argument is null.");
		if (isNodeAncestor(newChild))
			throw new IllegalArgumentException("newChild argument is an ancestor.");

		VirtualFileTreeNode oldParent = newChild.getParent();
		
		if (oldParent != null) {
			oldParent.remove(newChild);
		}
		
		newChild.setParent(this);
		if (_children == null) {
			_children = new Vector<VirtualFileTreeNode>();
		}
		_children.insertElementAt(newChild, childIndex);
	}

	public void remove(int childIndex) {
		VirtualFileTreeNode child = getChildAt(childIndex);
		_children.removeElementAt(childIndex);
		child.setParent(null);
	}

	public void remove(MutableTreeNode aNode) {
		if (aNode == null)
			throw new IllegalArgumentException("aNode argument is null.");
		
		if (!isNodeChild(aNode))
			throw new IllegalArgumentException("aNode argument is not a child.");
		
		remove(getIndex(aNode));
	}

	public void removeFromParent() {
		VirtualFileTreeNode parent = getParent();
		if (parent != null) {
			parent.remove(this);
		}
	}

	/**
	 * 親ノードを設定する。
	 * @param	親ノードとする <code>VirtualFileTreeNode</code> オブジェクト
	 * @throws ClassCastException	引数が <code>VirtualFileTreeNode</code> の派生ではない場合
	 */
	public void setParent(MutableTreeNode newParent) {
		_parent = (VirtualFileTreeNode)newParent;
	}

	/**
	 * ファイルオブジェクトを設定する。
	 * このメソッドでは、{@link VirtualFile} インタフェースが実装された
	 * オブジェクト以外は受け付けない。
	 * @param object	このオブジェクトに設定するファイルオブジェクト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws ClassCastException		引数が <code>VirtualFile</code> インタフェースを実装していない場合
	 */
	public void setUserObject(Object object) {
		setFileObject((VirtualFile)object);
	}

	/**
	 * このオブジェクトが保持するファイルオブジェクトを返す。
	 */
	public VirtualFile getUserObject() {
		return getFileObject();
	}

	//------------------------------------------------------------
	// Implements TreeNode interfaces
	//------------------------------------------------------------

	public Enumeration<VirtualFileTreeNode> children() {
		if (_children == null)
			return EMPTY_ENUMERATION;
		else
			return _children.elements();
	}

	public boolean getAllowsChildren() {
		return isDirectory();
	}

	public VirtualFileTreeNode getChildAt(int childIndex) {
		if (_children == null)
			throw new ArrayIndexOutOfBoundsException("node has no children.");
		
		return _children.elementAt(childIndex);
	}

	public int getChildCount() {
		if (_children == null) {
			return 0;
		} else {
			return _children.size();
		}
	}

	public int getIndex(TreeNode aNode) {
		if (aNode == null)
			throw new IllegalArgumentException("aNode argument is null.");
		
		if (!isNodeChild(aNode)) {
			return (-1);
		}
		
		return _children.indexOf(aNode);
	}

	public VirtualFileTreeNode getParent() {
		return _parent;
	}

	public boolean isLeaf() {
		return (!isDirectory());
	}
	
	//------------------------------------------------------------
	// Tree Queries
	//------------------------------------------------------------
	
	public boolean isNodeAncestor(TreeNode anotherNode) {
		if (anotherNode == null) {
			return false;
		}

		TreeNode ancestor = this;

		do {
			if (ancestor == anotherNode) {
				return true;
			}
		} while((ancestor = ancestor.getParent()) != null);

		return false;
	}
	
	public boolean isNodeDescendant(VirtualFileTreeNode anotherNode) {
		if (anotherNode == null)
			return false;

		return anotherNode.isNodeAncestor(this);
	}
	
	public VirtualFileTreeNode getSharedAncestor(VirtualFileTreeNode aNode) {
		if (aNode == this) {
			return this;
		} else if (aNode == null) {
			return null;
		}

		int		level1, level2, diff;
		VirtualFileTreeNode	node1, node2;

		level1 = getLevel();
		level2 = aNode.getLevel();

		if (level2 > level1) {
			diff = level2 - level1;
			node1 = aNode;
			node2 = this;
		} else {
			diff = level1 - level2;
			node1 = this;
			node2 = aNode;
		}

		while (diff > 0) {
			node1 = node1.getParent();
			diff--;
		}

		do {
			if (node1 == node2) {
				return node1;
			}
			node1 = node1.getParent();
			node2 = node2.getParent();
		} while (node1 != null);

		if (node1 != null || node2 != null) {
			throw new Error ("nodes should be null");
		}

		return null;
	}
	
	public boolean isNodeRelated(VirtualFileTreeNode aNode) {
		return (aNode != null) && (getRoot() == aNode.getRoot());
	}
	
	public int getLevel() {
		VirtualFileTreeNode ancestor;
		int levels = 0;

		ancestor = this;
		while((ancestor = ancestor.getParent()) != null){
			levels++;
		}

		return levels;
	}
	
	public int getDepth() {
		Object	last = null;
		Enumeration	enum_ = breadthFirstEnumeration();

		while (enum_.hasMoreElements()) {
			last = enum_.nextElement();
		}

		if (last == null) {
			throw new Error ("nodes should be null");
		}

		return ((VirtualFileTreeNode)last).getLevel() - getLevel();
	}
	
	public VirtualFileTreeNode[] getPath() {
		return getPathToRoot(this, 0);
	}
	
	protected VirtualFileTreeNode[] getPathToRoot(VirtualFileTreeNode aNode, int depth) {
		VirtualFileTreeNode[] retNodes;

		if(aNode == null) {
			if(depth == 0)
				return null;
			else
				retNodes = new VirtualFileTreeNode[depth];
		}
		else {
			depth++;
			retNodes = getPathToRoot(aNode.getParent(), depth);
			retNodes[retNodes.length - depth] = aNode;
		}
		return retNodes;
	}
	
	public VirtualFile[] getFileObjectPath() {
		VirtualFileTreeNode[] realPath = getPath();
		VirtualFile[] retPath = new VirtualFile[realPath.length];

		for (int i = 0; i < realPath.length; i++) {
			retPath[i] = realPath[i].getFileObject();
		}
		
		return retPath;
	}
	
	public VirtualFileTreeNode getRoot() {
		VirtualFileTreeNode ancestor = this;
		VirtualFileTreeNode previous;

		do {
			previous = ancestor;
			ancestor = ancestor.getParent();
		} while (ancestor != null);

		return previous;
	}
	
	public boolean isRoot() {
		return getParent() == null;
	}
	
	public VirtualFileTreeNode getNextNode() {
		if (getChildCount() == 0) {
			VirtualFileTreeNode nextSibling = getNextSibling();

			if (nextSibling == null) {
				VirtualFileTreeNode aNode = getParent();

				do {
					if (aNode == null) {
						return null;
					}

					nextSibling = aNode.getNextSibling();
					if (nextSibling != null) {
						return nextSibling;
					}

					aNode = aNode.getParent();
				} while(true);
			} else {
				return nextSibling;
			}
		} else {
			return getChildAt(0);
		}
	}
	
	public VirtualFileTreeNode getPreviousNode() {
		VirtualFileTreeNode previousSibling;
		VirtualFileTreeNode myParent = getParent();

		if (myParent == null) {
			return null;
		}

		previousSibling = getPreviousSibling();

		if (previousSibling != null) {
			if (previousSibling.getChildCount() == 0)
				return previousSibling;
			else
				return previousSibling.getLastLeaf();
		} else {
			return myParent;
		}
	}
	
	public Enumeration<VirtualFileTreeNode> preorderEnumeration() {
		return new PreorderEnumeration(this);
	}
	
	public Enumeration<VirtualFileTreeNode> postorderEnumeration() {
		return new PostorderEnumeration(this);
	}
	
	public Enumeration<VirtualFileTreeNode> breadthFirstEnumeration() {
		return new BreadthFirstEnumeration(this);
	}

	public Enumeration<VirtualFileTreeNode> depthFirstEnumeration() {
		return postorderEnumeration();
	}

	public Enumeration<VirtualFileTreeNode> pathFromAncestorEnumeration(VirtualFileTreeNode ancestor) {
		return new PathBetweenNodesEnumeration(ancestor, this);
	}
	
	//------------------------------------------------------------
	// Child Queries
	//------------------------------------------------------------

	public boolean isNodeChild(TreeNode aNode) {
		boolean retval;

		if (aNode == null) {
			retval = false;
		} else {
			if (getChildCount() == 0) {
				retval = false;
			} else {
				retval = (aNode.getParent() == this);
			}
		}

		return retval;
	}
	
	public VirtualFileTreeNode getFirstChild() {
		if (getChildCount() == 0) {
			throw new NoSuchElementException("node has no children");
		}
		return getChildAt(0);
	}
	
	public VirtualFileTreeNode getLastChild() {
		if (getChildCount() == 0) {
			throw new NoSuchElementException("node has no children");
		}
		return getChildAt(getChildCount()-1);
	}
	
	public VirtualFileTreeNode getChildBefore(VirtualFileTreeNode aChild) {
		if (aChild == null) {
			throw new IllegalArgumentException("argument is null");
		}

		int index = getIndex(aChild);		// linear search

		if (index == -1) {
			throw new IllegalArgumentException("argument is not a child");
		}

		if (index > 0) {
			return getChildAt(index - 1);
		} else {
			return null;
		}
	}
	
	public VirtualFileTreeNode getChildAfter(VirtualFileTreeNode aChild) {
		if (aChild == null) {
			throw new IllegalArgumentException("argument is null");
		}

		int index = getIndex(aChild);

		if (index == -1) {
			throw new IllegalArgumentException("node is not a child");
		}

		if (index < getChildCount() - 1) {
			return getChildAt(index + 1);
		} else {
			return null;
		}
	}
	
	//------------------------------------------------------------
	// Sibling Queries
	//------------------------------------------------------------
	
	public boolean isNodeSibling(VirtualFileTreeNode anotherNode) {
		boolean retval;

		if (anotherNode == null) {
			retval = false;
		} else if (anotherNode == this) {
			retval = true;
		} else {
			VirtualFileTreeNode  myParent = getParent();
			retval = (myParent != null && myParent == anotherNode.getParent());

			if (retval && !getParent().isNodeChild(anotherNode)) {
				throw new Error("sibling has different parent");
			}
		}

		return retval;
	}

	public int getSiblingCount() {
		VirtualFileTreeNode myParent = getParent();

		if (myParent == null) {
			return 1;
		} else {
			return myParent.getChildCount();
		}
	}
	
	public VirtualFileTreeNode getNextSibling() {
		VirtualFileTreeNode retval;
		VirtualFileTreeNode myParent = getParent();

		if (myParent == null) {
			retval = null;
		} else {
			retval = myParent.getChildAfter(this);
		}

		if (retval != null && !isNodeSibling(retval)) {
			throw new Error("child of parent is not a sibling");
		}

		return retval;
	}
	
	public VirtualFileTreeNode getPreviousSibling() {
		VirtualFileTreeNode retval;
		VirtualFileTreeNode myParent = getParent();

		if (myParent == null) {
			retval = null;
		} else {
			retval = myParent.getChildBefore(this);
		}

		if (retval != null && !isNodeSibling(retval)) {
			throw new Error("child of parent is not a sibling");
		}

		return retval;
	}
	
	//------------------------------------------------------------
	// Leaf Queries
	//------------------------------------------------------------

	public VirtualFileTreeNode getFirstLeaf() {
		VirtualFileTreeNode node = this;

		while (!node.isLeaf()) {
			node = node.getFirstChild();
		}

		return node;
	}

	public VirtualFileTreeNode getLastLeaf() {
		VirtualFileTreeNode node = this;

		while (!node.isLeaf()) {
			node = node.getLastChild();
		}

		return node;
	}

	public VirtualFileTreeNode getNextLeaf() {
		VirtualFileTreeNode nextSibling;
		VirtualFileTreeNode myParent = getParent();

		if (myParent == null)
			return null;

		nextSibling = getNextSibling();

		if (nextSibling != null)
			return nextSibling.getFirstLeaf();

		return myParent.getNextLeaf();
	}

	public VirtualFileTreeNode getPreviousLeaf() {
		VirtualFileTreeNode previousSibling;
		VirtualFileTreeNode myParent = getParent();

		if (myParent == null)
			return null;

		previousSibling = getPreviousSibling();

		if (previousSibling != null)
			return previousSibling.getLastLeaf();

		return myParent.getPreviousLeaf();
	}

	public int getLeafCount() {
		int count = 0;

		VirtualFileTreeNode node;
		Enumeration<VirtualFileTreeNode> e = breadthFirstEnumeration();

		while (e.hasMoreElements()) {
			node = e.nextElement();
			if (node.isLeaf()) {
				count++;
			}
		}

		if (count < 1) {
			throw new Error("tree has zero leaves");
		}

		return count;
	}
	
	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	final class PreorderEnumeration implements Enumeration<VirtualFileTreeNode>
	{
		protected Stack<Enumeration<VirtualFileTreeNode>> stack;

		public PreorderEnumeration(VirtualFileTreeNode rootNode) {
			super();
			Vector<VirtualFileTreeNode> v = new Vector<VirtualFileTreeNode>(1);
			v.addElement(rootNode);
			stack = new Stack<Enumeration<VirtualFileTreeNode>>();
			stack.push(v.elements());
		}

		public boolean hasMoreElements() {
			return (!stack.empty() &&
					((Enumeration<VirtualFileTreeNode>)stack.peek()).hasMoreElements());
		}

		public VirtualFileTreeNode nextElement() {
			Enumeration<VirtualFileTreeNode> enumer = stack.peek();
			VirtualFileTreeNode node = enumer.nextElement();
			Enumeration<VirtualFileTreeNode> children = node.children();

			if (!enumer.hasMoreElements()) {
				stack.pop();
			}
			if (children.hasMoreElements()) {
				stack.push(children);
			}
			return node;
		}
	}

	final class PostorderEnumeration implements Enumeration<VirtualFileTreeNode>
	{
		protected VirtualFileTreeNode root;
		protected Enumeration<VirtualFileTreeNode> children;
		protected Enumeration<VirtualFileTreeNode> subtree;

		public PostorderEnumeration(VirtualFileTreeNode rootNode) {
			super();
			root = rootNode;
			children = root.children();
			subtree = EMPTY_ENUMERATION;
		}

		public boolean hasMoreElements() {
			return root != null;
		}

		public VirtualFileTreeNode nextElement() {
			VirtualFileTreeNode retval;

			if (subtree.hasMoreElements()) {
				retval = subtree.nextElement();
			} else if (children.hasMoreElements()) {
				subtree = new PostorderEnumeration(children.nextElement());
				retval = subtree.nextElement();
			} else {
				retval = root;
				root = null;
			}

			return retval;
		}
	}

	final class BreadthFirstEnumeration implements Enumeration<VirtualFileTreeNode> {
		protected Queue	queue;

		public BreadthFirstEnumeration(VirtualFileTreeNode rootNode) {
			super();
			Vector<VirtualFileTreeNode> v = new Vector<VirtualFileTreeNode>(1);
			v.addElement(rootNode);
			queue = new Queue();
			queue.enqueue(v.elements());
		}

		public boolean hasMoreElements() {
			return (!queue.isEmpty() && queue.firstObject().hasMoreElements());
		}

		public VirtualFileTreeNode nextElement() {
			Enumeration<VirtualFileTreeNode> enumer = queue.firstObject();
			VirtualFileTreeNode node = enumer.nextElement();
			Enumeration<VirtualFileTreeNode> children = node.children();

			if (!enumer.hasMoreElements()) {
				queue.dequeue();
			}
			if (children.hasMoreElements()) {
				queue.enqueue(children);
			}
			return node;
		}

		final class Queue
		{
			QNode head;	// null if empty
			QNode tail;

			final class QNode {
				public Enumeration<VirtualFileTreeNode>	object;
				public QNode	next;	// null if end
				public QNode(Enumeration<VirtualFileTreeNode> object, QNode next) {
					this.object = object;
					this.next = next;
				}
			}

			public void enqueue(Enumeration<VirtualFileTreeNode> anObject) {
				if (head == null) {
					head = tail = new QNode(anObject, null);
				} else {
					tail.next = new QNode(anObject, null);
					tail = tail.next;
				}
			}

			public Enumeration<VirtualFileTreeNode> dequeue() {
				if (head == null) {
					throw new NoSuchElementException("No more elements");
				}

				Enumeration<VirtualFileTreeNode> retval = head.object;
				QNode oldHead = head;
				head = head.next;
				if (head == null) {
					tail = null;
				} else {
					oldHead.next = null;
				}
				return retval;
			}

			public Enumeration<VirtualFileTreeNode> firstObject() {
				if (head == null) {
					throw new NoSuchElementException("No more elements");
				}

				return head.object;
			}

			public boolean isEmpty() {
				return head == null;
			}
		}
	}

	final class PathBetweenNodesEnumeration implements Enumeration<VirtualFileTreeNode>
	{
		protected Stack<VirtualFileTreeNode> stack;

		public PathBetweenNodesEnumeration(VirtualFileTreeNode ancestor,
				VirtualFileTreeNode descendant)
		{
			super();

			if (ancestor == null || descendant == null) {
				throw new IllegalArgumentException("argument is null");
			}

			VirtualFileTreeNode current;

			stack = new Stack<VirtualFileTreeNode>();
			stack.push(descendant);

			current = descendant;
			while (current != ancestor) {
				current = current.getParent();
				if (current == null && descendant != ancestor) {
					throw new IllegalArgumentException("node " + ancestor +
							" is not an ancestor of " + descendant);
				}
				stack.push(current);
			}
		}

		public boolean hasMoreElements() {
			return stack.size() > 0;
		}

		public VirtualFileTreeNode nextElement() {
			try {
				return stack.pop();
			} catch (EmptyStackException e) {
				throw new NoSuchElementException("No more elements");
			}
		}
	}
}
