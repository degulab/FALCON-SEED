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
 * @(#)VirtualFileTreeModel.java	1.14	2009/12/11
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.tree;

import java.io.IOException;
import java.util.Comparator;
import java.util.Stack;

import javax.swing.Icon;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import ssac.util.Strings;
import ssac.util.io.VirtualFile;
import ssac.util.io.VirtualFileFilter;
import ssac.util.io.VirtualFileOperationException;

/**
 * <code>VirtualFile</code> オブジェクトをノードの値とし、ファイルの階層構造を
 * 表現するツリーモデル。
 * このモデルではファイルに関する基本的なインタフェースを提供すると
 * ともに、ファイル階層構造を維持するための機能を提供する。
 * <p>
 * このクラスでは、ファイル階層構造の基準となるパスはコンストラクタで
 * 与えるものとし、インスタンス生成後の基準パスの変更はできない。
 * 
 * @version 1.14	2009/12/11
 * @since 1.14
 */
public class VirtualFileTreeModel extends AbstractTreeModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** ツリーノードに登録するファイルを選択するためのフィルタ **/
	protected VirtualFileFilter	_fileFilter;
	/** ツリーノードの表示順序を制御するためのコンパレータ **/
	protected Comparator<VirtualFileTreeNode>	_comparator;
	/** ルートノード専用のアイコン **/
	protected Icon	_rootNodeIcon;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public VirtualFileTreeModel() {
		super(null);
	}
	
	public VirtualFileTreeModel(VirtualFile rootPath) {
		this(rootPath, null, null);
	}
	
	public VirtualFileTreeModel(VirtualFileTreeNode rootNode) {
		this(rootNode, null, null);
	}
	
	public VirtualFileTreeModel(VirtualFile rootPath, VirtualFileFilter filter, Comparator<VirtualFileTreeNode> comparator) {
		super(null);
		this._fileFilter = filter;
		this._comparator = comparator;
		if (rootPath != null) {
			setRoot(createTreeNode(rootPath));
		}
		refreshChildren();
	}
	
	public VirtualFileTreeModel(VirtualFileTreeNode rootNode, VirtualFileFilter filter, Comparator<VirtualFileTreeNode> comparator) {
		super(rootNode);
		this._fileFilter = filter;
		this._comparator = comparator;
		refreshChildren();
	}
	
	//------------------------------------------------------------
	// Override AbstractTreeModel interfaces
	//------------------------------------------------------------

	/**
	 * このツリーモデルのルートノードを設定する。
	 * このモデルに設定可能なルートノードは <code>VirtualFileTreeNode</code> の
	 * インスタンスとする。
	 * @param root	設定するルートノード。この引数が <tt>null</tt> の場合、ルートノードは設定されない。
	 * @throws ClassCastException	引数が <code>VirtualFileTreeNode</code> のインスタンスではない場合
	 */
	@Override
	public void setRoot(TreeNode root) {
		super.setRoot((VirtualFileTreeNode)root);
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
		// 何もしない
		// ただ、将来的にツリー内で名称を直接編集するような場合に、
		// ここで実装を行う。
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public Icon getRootNodeIcon() {
		return _rootNodeIcon;
	}
	
	public void setRootNodeIcon(Icon icon) {
		_rootNodeIcon = icon;
	}

	public VirtualFileFilter getFileFilter() {
		return _fileFilter;
	}
	
	public void setFileFilter(VirtualFileFilter filter) {
		if (_fileFilter != filter) {
			_fileFilter = filter;
			// ルートノード直下のノードを削除し、子の構成を更新する
			VirtualFileTreeNode rootNode = (VirtualFileTreeNode)getRoot();
			if (rootNode != null && rootNode.getChildCount() > 0) {
				rootNode.removeAllChildren();
				reload();
				refreshChildren();
			}
		}
	}
	
	public Comparator<VirtualFileTreeNode> getNodeComparator() {
		return _comparator;
	}
	
	public void setNodeComparaotr(Comparator<VirtualFileTreeNode> c) {
		if (_comparator != c) {
			_comparator = c;
			// ルートノード直下のノードを削除し、子の構成を更新する
			VirtualFileTreeNode rootNode = (VirtualFileTreeNode)getRoot();
			if (rootNode != null && rootNode.getChildCount() > 0) {
				rootNode.removeAllChildren();
				reload();
			}
		}
	}

	/**
	 * このモデルのルートノードに設定されている <code>Virtual</code> オブジェクトを返す。
	 * ファイルオブジェクトがルートノードとして設定されていない場合は <tt>null</tt> を返す。
	 */
	public VirtualFile getRootFile() {
		VirtualFileTreeNode rootNode = (VirtualFileTreeNode)getRoot();
		if (rootNode == null) {
			return null;
		} else {
			return rootNode.getFileObject();
		}
	}

	/**
	 * このモデルのルートノードとなるパスを設定する。
	 * @param rootPath	ルートノードに格納する <code>VirtualFile</code> オブジェクト
	 */
	public void setRootFile(VirtualFile rootPath) {
		if (rootPath != null) {
			super.setRoot(createTreeNode(rootPath));
		} else {
			super.setRoot(null);
		}
	}

	/**
	 * <em>targetNode</em> のファイルオブジェクトを、<em>newFile</em> に置き換える。
	 * このとき、<em>targetNode</em> のすべての子ノードのファイルオブジェクトも、
	 * 親子関係が維持されるように更新される。
	 * <p><b>(注)</b>
	 * <blockquote>
	 * このメソッドは、ファイルオブジェクトの更新のみであり、ファイルの有無や
	 * 正当性などについては検証しない。
	 * </blockquote>
	 * @param targetNode	変更対象のノード
	 * @param newFile		新しいファイルオブジェクト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public void changeFileObject(VirtualFileTreeNode targetNode, VirtualFile newFile) {
		targetNode.setFileObject(newFile);
		int numChildren = targetNode.getChildCount();
		for (int i = 0; i < numChildren; i++) {
			VirtualFileTreeNode child = targetNode.getChildAt(i);
			VirtualFile newChildFile = newFile.getChildFile(child.getFilename());
			changeFileObject(child, newChildFile);
		}
	}
	/**/
	
	public void sortChildren(VirtualFileTreeNode parentNode) {
		parentNode.sortChildren(getNodeComparator());
		reload(parentNode);
	}

	/**
	 * このモデルのルートノードのプロパティと子ノード構成を最新の情報に更新する。
	 */
	public void refresh() {
		if (_rootNode != null) {
			refreshNode((VirtualFileTreeNode)_rootNode);
		}
	}

	/**
	 * ルートノードの子ノード構成を最新の情報に更新する。
	 */
	public void refreshChildren() {
		if (_rootNode != null) {
			refreshChildren((VirtualFileTreeNode)_rootNode);
		}
	}

	/**
	 * 指定されたノードのプロパティと子ノード構成を最新の情報に更新する。
	 * このメソッドは、指定されたノードに格納されているファイルオブジェクトをキーに、
	 * 子ノードを再構成する。
	 * このメソッドでは、指定ノードの直接の子ノード構成のみを更新し、それ以外の子孫
	 * ノードの構成は更新しない。
	 * @param targetNode	更新対象のノード
	 */
	public void refreshNode(VirtualFileTreeNode targetNode) {
		targetNode.refreshProperties();
		refreshChildren(targetNode);
	}

	/**
	 * 指定されたノードの内容を最新の情報に更新する。
	 * このメソッドは、指定されたノードに格納されているファイルオブジェクトをキーに、
	 * 子ノードを再構成する。
	 * このメソッドでは、指定ノードの直接の子ノード構成のみを更新し、それ以外の子孫
	 * ノードの構成は更新しない。
	 * @param targetNode	更新対象のノード
	 */
	public void refreshChildren(VirtualFileTreeNode targetNode) {
		if (targetNode.isDirectory()) {
			// ファイルリストを取得
			VirtualFile targetFile = targetNode.getFileObject();
			VirtualFile[] flist = targetFile.listFiles(getFileFilter());
			
			// ファイルの存在しない子ノード、ならびにファイルリストに存在しない子ノードを除去
			for (int i = targetNode.getChildCount() - 1; i >= 0; i--) {
				VirtualFileTreeNode child = targetNode.getChildAt(i);
				if (!child.exists()) {
					// 実体ファイルのないノード
					targetNode.remove(i);
					nodesWereRemoved(targetNode, new int[]{i}, new Object[]{child});
				}
				else if (!targetNode.getFileObject().equals(child.getFileObject().getParentFile())) {
					// 抽象パスの親が targetNode の抽象パスと一致しないなら、不正な構造
					targetNode.remove(i);
					nodesWereRemoved(targetNode, new int[]{i}, new Object[]{child});
				}
			}
			
			// ファイルリストを取得し、新しいノードを追加する。
			if (flist != null && flist.length > 0) {
				for (VirtualFile f : flist) {
					if (!targetNode.isFileChild(f)) {
						// 存在しないもののみ作成
						VirtualFileTreeNode newNode = createTreeNode(f);
						int index = getInsertionIndex(targetNode, newNode);
						if (index >= 0) {
							insertNodeInto(newNode, targetNode, index);
						}
					}
				}
			}
		}
		else {
			// ディレクトリでないなら、子ノードを破棄
			if (targetNode.getChildCount() > 0) {
				targetNode.removeAllChildren();
				nodeStructureChanged(targetNode);
			}
		}
	}

	/**
	 * 指定されたノードの子ノードから、指定された名前に一致する抽象パスを持つノードを
	 * 取得する。
	 * @param parent	検索対象のノードを指定する。<tt>null</tt> の場合はルートノードを対象とする。
	 * @param filename	検索するキーとなるファイル名
	 * @return	ファイル名に一致する抽象パスを持つ子ノードを返す。
	 * 			一致するノードが存在しない場合は <tt>null</tt> を返す。
	 */
	public VirtualFileTreeNode getChildByName(VirtualFileTreeNode parent, String filename) {
		if (parent == null) {
			if (_rootNode == null) {
				// 検索対象が存在しない
				return null;
			} else {
				parent = (VirtualFileTreeNode)_rootNode;
			}
		}
		
		int numChildren = parent.getChildCount();
		for (int i = 0; i < numChildren; i++) {
			VirtualFileTreeNode child = (VirtualFileTreeNode)parent.getChildAt(i);
			if (child.getFilename().equalsIgnoreCase(filename)) {
				return child;
			}
		}

		// not found
		return null;
	}

	/**
	 * 指定されたノードの子ノードに、指定された名前を持つノードが存在するかを検証する。
	 * @param parent	検索対象のノードを指定する。<tt>null</tt> の場合はルートノードを対象とする。
	 * @param filename	検証する名前
	 * @return	一致する名前を持つ子ノードが存在する場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean containsName(VirtualFileTreeNode parent, String filename) {
		return (getChildByName(parent, filename) != null);
	}

	/**
	 * 指定された抽象パスを保持するノードまでのツリーパスを取得する。
	 * このメソッドでは、ルートノードから子ノードの構成を更新し、
	 * 指定された抽象パスを格納するノードを探す。
	 * 
	 * @param targetFile	検索する抽象パス
	 * @return	指定された抽象パスがこのモデルルートの下位に存在する場合に、
	 * 			この抽象パスを格納するノードまでのツリーパスを返す。
	 * 			そうでない場合は <tt>null</tt> を返す。
	 */
	public TreePath getTreePathForFile(VirtualFile targetFile) {
		VirtualFile rootFile = getRootFile();
		if (rootFile == null || targetFile == null) {
			return null;
		}
		
		Stack<VirtualFile> fileStack = new Stack<VirtualFile>();
		VirtualFile fPath = targetFile;
		do {
			if (fPath.equals(rootFile)) {
				// same root
				break;
			}
			fileStack.push(fPath);
		} while ((fPath = fPath.getParentFile()) != null);
		if (fPath == null) {
			// Root path is not ancestor
			return null;
		}

		VirtualFileTreeNode targetNode = (VirtualFileTreeNode)getRoot();
		TreePath treePath = new TreePath(targetNode);
		while (treePath != null && !fileStack.isEmpty()) {
			VirtualFileTreeNode targetChild = null;
			VirtualFile fChild = fileStack.pop();
			refreshNode(targetNode);
			int numChildren = targetNode.getChildCount();
			for (int i = 0; i < numChildren; i++) {
				VirtualFileTreeNode child = (VirtualFileTreeNode)targetNode.getChildAt(i);
				if (child.getFileObject().equals(fChild)) {
					targetChild = child;
					break;
				}
			}
			if (targetChild == null) {
				treePath = null;
			} else {
				treePath = treePath.pathByAddingChild(targetChild);
				targetNode = targetChild;
			}
		}
		return treePath;
	}

	/**
	 * <em>parent</em> の直下に、指定された名前のディレクトリを作成する。
	 * 
	 * @param parent	親ディレクトリとなる抽象パスを格納するツリーノード
	 * @param name		作成するディレクトリ名
	 * @return	生成されたディレクトリを示すノード
	 * @throws IllegalArgumentException	<em>parent</em> がディレクトリではない場合、
	 * 										もしくは <em>name</em> が有効な文字列ではない場合
	 * @throws IllegalStateException		<em>parent</em> の子に <em>name</em> と同名の
	 * 										ファイルもしくはディレクトリが存在する場合
	 * @throws VirtualFileOperationException	ディレクトリの作成に失敗した場合
	 */
	public VirtualFileTreeNode createDirectory(VirtualFileTreeNode parent, String name)
	throws VirtualFileOperationException
	{
		if (Strings.isNullOrEmpty(name))
			throw new IllegalArgumentException("name argument is empty or null.");
		if (parent == null)
			throw new IllegalArgumentException("parent argument is null.");
		if (!parent.isDirectory() || !parent.exists())
			throw new IllegalArgumentException("parent is not directory : \"" + parent.getFilePath() + "\"");
		
		// ディレクトリの作成
		VirtualFile fDir = parent.getFileObject().getChildFile(name);
		if (fDir.exists())
			throw new IllegalStateException("New directory already exists! : \"" + fDir.toString() + "\"");
		try {
			if (!fDir.mkdir()) {
				throw new VirtualFileOperationException(VirtualFileOperationException.MKDIR, null, fDir);
			}
		} catch (SecurityException ex) {
			throw new VirtualFileOperationException(VirtualFileOperationException.MKDIR, null, fDir, ex);
		}
		
		// ディレクトリノードの作成
		VirtualFileTreeNode newNode = createTreeNode(fDir);
		int index = getInsertionIndex(parent, newNode);
		if (index < 0)
			throw new AssertionError("Illegal insertion index : " + index);
		insertNodeInto(newNode, parent, index);
		
		return newNode;
	}
	
	/**
	 * <em>parent</em> の直下に、指定された名前のファイルを作成する。
	 * 
	 * @param parent	親ディレクトリとなる抽象パスを格納するツリーノード
	 * @param name		作成するファイル名
	 * @return	生成されたファイルを示すノード
	 * @throws IllegalArgumentException	<em>parent</em> がディレクトリではない場合、
	 * 										もしくは <em>name</em> が有効な文字列ではない場合
	 * @throws IllegalStateException		<em>parent</em> の子に <em>name</em> と同名の
	 * 										ファイルもしくはディレクトリが存在する場合
	 * @throws VirtualFileOperationException	ファイルの作成に失敗した場合
	 */
	public VirtualFileTreeNode createFile(VirtualFileTreeNode parent, String name)
	throws VirtualFileOperationException
	{
		if (Strings.isNullOrEmpty(name))
			throw new IllegalArgumentException("name argument is empty or null.");
		if (parent == null)
			throw new IllegalArgumentException("parent argument is null.");
		if (!parent.isDirectory() || !parent.exists())
			throw new IllegalArgumentException("parent is not directory : \"" + parent.getFilePath() + "\"");
		
		// ファイルの作成
		VirtualFile fFile = parent.getFileObject().getChildFile(name);
		if (fFile.exists())
			throw new IllegalStateException("New file already exists! : \"" + fFile.toString() + "\"");
		try {
			if (!fFile.createNewFile()) {
				throw new VirtualFileOperationException(VirtualFileOperationException.CREATE, null, fFile);
			}
		} catch (IOException ex) {
			throw new VirtualFileOperationException(VirtualFileOperationException.CREATE, null, fFile, ex);
		} catch (SecurityException ex) {
			throw new VirtualFileOperationException(VirtualFileOperationException.CREATE, null, fFile, ex);
		}
		
		// ファイルノードの作成
		VirtualFileTreeNode newNode = createTreeNode(fFile);
		int index = getInsertionIndex(parent, newNode);
		if (index < 0)
			throw new AssertionError("Illegal insertion index : " + index);
		insertNodeInto(newNode, parent, index);
		
		return newNode;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 指定されたオブジェクトを格納するツリーノードを生成する。
	 * このメソッドで生成するツリーノードのインスタンスは、
	 * <code>VirtualFileTreeNode</code> の派生でなければならない。
	 * このメソッドの実装において、<tt>null</tt> を返してはならない。
	 * <p>
	 * 独自のツリーノードインスタンスを使用する場合は、このメソッドを適切にオーバーライドすること。
	 * @param fileObject	ノードに格納するオブジェクト
	 * @return	生成されたオブジェクト
	 */
	protected VirtualFileTreeNode createTreeNode(VirtualFile fileObject) {
		return new VirtualFileTreeNode(fileObject);
	}

	/**
	 * 登録されている <code>Comparator</code> を使用して、
	 * ソートが維持される挿入位置を取得する。
	 * <code>Comparator</code> が登録されていない場合は、子ノードリストの
	 * 終端位置を返す。
	 * ノードの比較において同値と判定されたノードが子ノードに存在する場合、
	 * そのノードの位置を表すインデックスとなる負の値 (-(インデックス+1)) を返す。
	 * @param parent	挿入先のノード
	 * @param child		挿入するノード
	 * @return	新規に挿入する位置を表すインデックス(正の値)を返す。
	 * 			比較において同値とみなされるノードが存在する場合は (-(インデックス+1)) を返す。
	 * 			<code>Comparator</code> が定義されていない場合は、(parent.getChildCount() を返す。
	 * @throws IllegalArgumentException	引数のどちらかが <tt>null</tt> の場合
	 */
	protected int getInsertionIndex(VirtualFileTreeNode parent, VirtualFileTreeNode child) {
		if (parent == null)
			throw new IllegalArgumentException("parent argument is null.");
		if (child == null)
			throw new IllegalArgumentException("child argument is null.");
		
		Comparator<? super VirtualFileTreeNode> c = getNodeComparator();
		if (c == null) {
			return parent.getChildCount();
		} else {
			int index = parent.binarySearch(child, c);
			if (index < 0) {
				return (Math.abs(index) - 1);
			} else {
				return (-index - 1);
			}
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	/**
	 * 標準的なツリーノードの比較を行うコンパレータ。
	 * このクラスの実装では、ディレクトリとファイルの関係は
	 * (ディレクトリ &le; ファイル) とし、ディレクトリ同士もしくはファイル同士の
	 * 比較は <code>VirtualFile</code> の比較結果となる。
	 */
	static public class DefaultVirtualFileComparator implements Comparator<VirtualFileTreeNode>
	{
		public int compare(VirtualFileTreeNode o1, VirtualFileTreeNode o2) {
			// directory < file
			if (o1.isDirectory()) {
				if (!o2.isDirectory()) {
					// o1(directory) < o2(file)
					return (-1);
				}
			}
			else if (o2.isDirectory()) {
				// o1(file) > o2(directory)
				return (1);
			}
			
			// compare by VirtualFile
			return o1.getFileObject().compareTo(o2.getFileObject());
		}
	}
}
