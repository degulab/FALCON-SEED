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
 *  Copyright 2007-2012  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)DefaultFileTreeModel.java	2.0.0	2012/10/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DefaultFileTreeModel.java	1.20	2012/03/05
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.file.swing.tree;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Stack;

import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import ssac.util.Strings;
import ssac.util.io.Files;
import ssac.util.io.VirtualFile;
import ssac.util.io.VirtualFileFilter;
import ssac.util.io.VirtualFileOperationException;
import ssac.util.swing.tree.AbstractTreeModel;

/**
 * 汎用ファイルツリー専用のツリーモデル。
 * 
 * @version 2.0.0	2012/10/29
 * @since 1.20
 */
public class DefaultFileTreeModel extends AbstractTreeModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** クラス標準のファイルフィルタ **/
	static protected VirtualFileFilter	_defFileFilter;
	/** クラス標準のファイルコンパレータ **/
	static protected Comparator<? super DefaultFileTreeNode>	_defComparator;

	/** ツリーノードに登録するファイルを選択するためのフィルタ **/
	private VirtualFileFilter	_fileFilter;
	/** ツリーノードの表示順序を制御するためのコンパレータ **/
	private Comparator<? super DefaultFileTreeNode>	_comparator;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public DefaultFileTreeModel() {
		this(null);
	}
	
	public DefaultFileTreeModel(DefaultFileTreeNode rootnode) {
		this(rootnode, getDefaultFileFilter(), getDefaultFileTreeNodeComparator());
	}
	
	public DefaultFileTreeModel(DefaultFileTreeNode rootnode, VirtualFileFilter filter, Comparator<? super DefaultFileTreeNode> comparator)
	{
		super(rootnode);
		this._fileFilter = filter;
		this._comparator = comparator;
		if (rootnode != null) {
			refreshNode(rootnode);
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定されたツリーパスから、パスを示す文字列を取得する。
	 * このパス文字列は、このモデルの有効なルートからの相対パスとし、
	 * スラッシュ(/) をパス区切り文字とする抽象パスの表示文字列となる。
	 * @param treepath	表示するノードを格納するツリーパス
	 * @return	抽象パスの表示文字列
	 * @throws NullPointerException		引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	指定されたパスが、このモデルのものではない場合
	 */
	public String formatFilePath(TreePath treepath) {
		// ルートをチェック
		if (treepath.getPathComponent(0) != getRoot()) {
			// このモデルのツリーパスではない
			throw new IllegalArgumentException("The specified tree path is not path of this model.");
		}

		// パス文字列を生成
		int beginIndex = getAvailableRootNodeIndex(treepath);
		return formatFilePath(beginIndex, treepath);
	}

	/**
	 * 指定されたノードから、パスを示す文字列を取得する。
	 * このパス文字列は、このモデルの有効なルートからの相対パスとし、
	 * スラッシュ(/) をパス区切り文字とする抽象パスの表示文字列となる。
	 * @param node	表示するノード
	 * @return	抽象パスの表示文字列
	 * @throws NullPointerException		引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	指定されたパスが、このモデルのものではない場合
	 */
	public String formatFilePath(DefaultFileTreeNode node) {
		if (!node.isNodeAncestor(getRoot())) {
			// このモデルのノードではない
			throw new IllegalArgumentException("The specified node is not a node of this model.");
		}

		// パス文字列を生成
		TreeNode[] nodepath = getPathToRoot(node);
		int beginIndex = getAvailableRootNodeIndex(nodepath);
		return formatFilePath(beginIndex, nodepath);
	}

	/**
	 * 指定されたファイルから、パスを示す文字列を生成する。
	 * このパス文字列は、このモデルの有効なルートからの相対パスとし、
	 * スラッシュ(/) をパス区切り文字とする抽象パスの表示文字列となる。
	 * <p>指定されたファイルが、有効なルート以下に存在しない場合は、
	 * 絶対パスを示す文字列を返す。
	 * @param file	パスを表示するファイル
	 * @return	抽象パスの表示文字列
	 * @throws NullPointerException		引数が <tt>null</tt> の場合
	 */
	public String formatFilePath(VirtualFile file) {
		if (!file.isAbsolute()) {
			file = file.getAbsoluteFile();
		}
		
		TreePath tp = getTreePathForFile(file);
		return formatFilePath(tp);
	}

	/**
	 * このモデルに設定されているファイルフィルターを取得する。
	 * @return 設定されている <code>VirtualFileFilter</code> オブジェクトを返す。
	 * 			設定されていない場合は <tt>null</tt> を返す。
	 */
	public VirtualFileFilter getFileFilter() {
		return _fileFilter;
	}

	/**
	 * このモデルに、新しいファイルフィルターを設定する。
	 * @param filter	新たに設定する <code>VirtualFileFilter</code> オブジェクト
	 */
	public void setFileFilter(VirtualFileFilter filter) {
		if (_fileFilter != filter) {
			_fileFilter = filter;
			removeAllChildrenFromAvailableRootNode();
			reload();
		}
	}

	/**
	 * このモデルに設定されているコンパレーターを取得する。
	 * @return	設定されている <code>Comparator</code> オブジェクトを返す。
	 * 			設定されていない場合は <tt>null</tt> を返す。
	 */
	public Comparator<? super DefaultFileTreeNode> getNodeComparator() {
		return _comparator;
	}

	/**
	 * このモデルに、新しいコンパレーターを設定する。
	 * @param c	新たに設定する <code>Comparator</code> オブジェクト
	 */
	public void setNodeComparaotr(Comparator<? super DefaultFileTreeNode> c) {
		if (_comparator != c) {
			_comparator = c;
			removeAllChildrenFromAvailableRootNode();
			reload();
		}
	}

	/**
	 * このモデルのルートノードにファイル抽象パスが割り当てられている場合に <tt>true</tt> を返す。
	 * @return	ルートノードに抽象パスが割り当てられている場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @since 2.0.0
	 */
	public boolean hasRootFile() {
		DefaultFileTreeNode rootnode = getRoot();
		return rootnode.getFileData().hasFile();
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
		DefaultFileTreeNode rootnode = getRoot();
		if (rootnode != null) {
			return getTreePathForFile(rootnode, targetFile);
		} else {
			return null;
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
	public void changeFileObject(DefaultFileTreeNode targetNode, VirtualFile newFile) {
		targetNode.setUserObject(targetNode.getFileData().replaceFile(newFile));
		int numChildren = targetNode.getChildCount();
		for (int i = 0; i < numChildren; i++) {
			DefaultFileTreeNode child = (DefaultFileTreeNode)targetNode.getChildAt(i);
			VirtualFile newChildFile = newFile.getChildFile(child.getFilename());
			changeFileObject(child, newChildFile);
		}
	}

	/**
	 * このモデルに設定されているコンパレータに従い、指定されたノードの子ノードを
	 * ソートする。
	 * @param parentNode	子ノードをソートするノード
	 */
	public void sortChildren(DefaultFileTreeNode parentNode) {
		parentNode.sortChildren(getNodeComparator());
		reload(parentNode);
	}

	/**
	 * <em>newNode</em> を <em>parent</em> の子ノードに挿入する。挿入位置は、このモデルに
	 * 設定されているコンパレータに従い、自動的に決定される。
	 * @param parent	親ノード
	 * @param newNode	親ノードに挿入するノード
	 */
	public void insertFileTreeNode(DefaultFileTreeNode parent, DefaultFileTreeNode newNode) {
		int index = getInsertionIndex(parent, newNode);
		if (index < 0)
			throw new AssertionError("Illegal insertion index : " + index);
		insertNodeInto(newNode, parent, index);
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
	public DefaultFileTreeNode createDirectory(DefaultFileTreeNode parent, String name)
	throws VirtualFileOperationException
	{
		if (Strings.isNullOrEmpty(name))
			throw new IllegalArgumentException("name argument is empty or null.");
		if (parent == null)
			throw new IllegalArgumentException("parent argument is null.");
		if (parent.getFileObject()==null)
			throw new IllegalArgumentException("parent has no file object.");
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
		DefaultFileTreeNode newNode = createFileTreeNodeByFile(parent, fDir);
		insertFileTreeNode(parent, newNode);
		
		return newNode;
	}

	//------------------------------------------------------------
	// Implement AbstractTreeModel interfaces
	//------------------------------------------------------------

	public void valueForPathChanged(TreePath path, Object newValue) {
		// 将来的に，名前変更処理を実装
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRoot(TreeNode root) {
		if (!(root instanceof DefaultFileTreeNode))
			throw new IllegalArgumentException("The specified TreeNode is not instance of DefaultFileTreeNode.");
		super.setRoot(root);
	}

	@Override
	public DefaultFileTreeNode getRoot() {
		return (DefaultFileTreeNode)super.getRoot();
	}

	/**
	 * このモデルのルートノードのプロパティと子ノード構成を最新の情報に更新する。
	 */
	public void refresh() {
		if (_rootNode != null) {
			refreshNode((DefaultFileTreeNode)_rootNode);
		}
	}

	/**
	 * ルートノードの子ノード構成を最新の情報に更新する。
	 */
	public void refreshChildren() {
		if (_rootNode != null) {
			refreshChildren((DefaultFileTreeNode)_rootNode);
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
	public void refreshNode(DefaultFileTreeNode targetNode) {
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
	public void refreshChildren(DefaultFileTreeNode targetNode) {
		// ファイル構造により更新
		IFileTreeData file = targetNode.getFileData();
		if (file.isDirectory()) {
			// ファイルリストを取得
			IFileTreeData[] childFileArray = file.listFiles(getFileFilter());
			if (childFileArray != null && childFileArray.length > 0) {
				// ディレクトリにファイルあり
				Set<IFileTreeData> files = new LinkedHashSet<IFileTreeData>(Arrays.asList(childFileArray));
				
				// ファイルの存在しない子ノード、ならびにファイルリストに存在しない子ノードを除去
				for (int i = targetNode.getChildCount() - 1; i >= 0; i--) {
					DefaultFileTreeNode child = (DefaultFileTreeNode)targetNode.getChildAt(i);
					IFileTreeData childFile = child.getFileData();
					if (!childFile.exists()) {
						// 実体ファイルのないノード
						targetNode.remove(i);
						nodesWereRemoved(targetNode, new int[]{i}, new Object[]{child});
					}
					else if (!files.contains(childFile)) {
						// ディレクトリに存在しないファイル
						targetNode.remove(i);
						nodesWereRemoved(targetNode, new int[]{i}, new Object[]{child});
					}
				}
				
				// 新しいファイルリストで、新しいノードを追加する。
				DefaultFileTreeNode dummyNode = new DefaultFileTreeNode(file);
				for (IFileTreeData f : files) {
					dummyNode.setFileData(f);
					int index = targetNode.binarySearch(dummyNode, getNodeComparator());
					if (index < 0) {
						// create new node
						DefaultFileTreeNode newNode = new DefaultFileTreeNode(f);
						insertNodeInto(newNode, targetNode, (Math.abs(index) - 1));
					} else {
						// already exists
						DefaultFileTreeNode existChild = (DefaultFileTreeNode)targetNode.getChildAt(index);
						if (!existChild.getFileData().equals(f)) {
							existChild.setFileData(f);
							nodesChanged(existChild, null);
						}
					}
				}
			}
			else {
				// ディレクトリにファイルがないので、子ノードをすべて破棄
				if (targetNode.getChildCount() > 0) {
					targetNode.removeAllChildren();
					nodeStructureChanged(targetNode);
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

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 指定されたツリーパスから、このモデルの有効なルートとなる
	 * ノードの位置を示すインデックスを取得する。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このクラスの実装では、有効なルートとなる位置は最上位の
	 * ルートノードとなるため、常に 0 を返す。
	 * </blockquote>
	 * @param treepath	判定するツリーパス
	 * @return	有効なルートとなるノードを示すインデックスを返す。
	 * 			有効なルートがない場合は -1 を返す。
	 */
	protected int getAvailableRootNodeIndex(TreePath treepath) {
		return 0;
	}
	
	/**
	 * 指定されたノードのパスを示す配列から、このモデルの有効なルートとなる
	 * ノードの位置を示すインデックスを取得する。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このクラスの実装では、有効なルートとなる位置は最上位の
	 * ルートノードとなるため、常に 0 を返す。
	 * </blockquote>
	 * @param nodepath	ツリーパスを示すノードの配列
	 * @return	有効なルートとなるノードを示すインデックスを返す。
	 * 			有効なルートがない場合は -1 を返す。
	 */
	protected int getAvailableRootNodeIndex(TreeNode[] nodepath) {
		return 0;
	}
	
	/**
	 * 指定されたツリーパスから、パスを示す文字列を取得する。
	 * パス文字列は、指定されたインデックスからのパス文字列とし、
	 * スラッシュ(/) をパス区切り文字とする抽象パスの表示文字列となる。
	 * 指定されたインデックスが有効ではない場合、このメソッドはパス区切り文字のみを返す。
	 * @param beginIndex	パス文字列のルートとするパスの開始位置
	 * @param treepath	表示するノードを格納するツリーパス
	 * @return	抽象パスの表示文字列
	 * @throws NullPointerException		引数が <tt>null</tt> の場合
	 * @throws ClassCastException			<em>nodepath</em> に格納されているオブジェクトが
	 * 										<code>FileTreeNode</code> のインスタンスではない場合
	 */
	protected String formatFilePath(int beginIndex, TreePath treepath) {
		StringBuilder sb = new StringBuilder();
		int len = treepath.getPathCount();
		if (beginIndex >= 0 && len > beginIndex) {
			for (int i = beginIndex; i < len; i++) {
				DefaultFileTreeNode node = (DefaultFileTreeNode)treepath.getPathComponent(i);
				sb.append(Files.CommonSeparatorChar);
				sb.append(node.getDisplayName());
			}
		} else {
			sb.append(Files.CommonSeparatorChar);
		}
		
		return sb.toString();
	}
	
	/**
	 * 指定されたノードのパスを示す配列から、パスを示す文字列を取得する。
	 * パス文字列は、指定されたインデックスからのパス文字列とし、
	 * スラッシュ(/) をパス区切り文字とする抽象パスの表示文字列となる。
	 * 指定されたインデックスが有効ではない場合、このメソッドはパス区切り文字のみを返す。
	 * @param beginIndex	パス文字列のルートとするパスの開始位置
	 * @param nodepath	ツリーパスを示すノードの配列
	 * @return	抽象パスの表示文字列
	 * @throws NullPointerException		<em>nodepath</em> が <tt>null</tt> の場合
	 * @throws ClassCastException			<em>nodepath</em> に格納されているオブジェクトが
	 * 										<code>FileTreeNode</code> のインスタンスではない場合
	 */
	protected String formatFilePath(int beginIndex, TreeNode[] nodepath) {
		StringBuilder sb = new StringBuilder();
		int len = nodepath.length;
		if (beginIndex >= 0 && len > beginIndex) {
			for (int i = beginIndex; i < len; i++) {
				sb.append(Files.CommonSeparatorChar);
				sb.append(((DefaultFileTreeNode)nodepath[i]).getDisplayName());
			}
		} else {
			sb.append(Files.CommonSeparatorChar);
		}
		
		return sb.toString();
	}

	/**
	 * このモデルのツリー構造から、指定された抽象パスを保持するノードを、<em>ndSearchRoot</em> を
	 * 起点として検索し、このモデルのルートノードからのツリーパスを取得する。
	 * @param ndSearchRoot	検索の起点となるノード
	 * @param targetFile	検索する抽象パス
	 * @return	指定の抽象パスを保持するノードを終端に持つツリーパスを返す。
	 * 			見つからなかった場合は <tt>null</tt> を返す。
	 */
	protected TreePath getTreePathForFile(DefaultFileTreeNode ndSearchRoot, VirtualFile targetFile)
	{
		Stack<VirtualFile> fileStack = new Stack<VirtualFile>();
		VirtualFile fSearchRoot = ndSearchRoot.getFileObject();
		VirtualFile fPath = targetFile;
		do {
			if (fPath.equals(fSearchRoot)) {
				// same root
				break;
			}
			fileStack.push(fPath);
		} while ((fPath = fPath.getParentFile()) != null);
		if (fPath == null) {
			// Root path is not ancestor
			return null;
		}

		DefaultFileTreeNode targetNode = ndSearchRoot;
		TreePath treePath = new TreePath(targetNode.getPath());
		while (treePath != null && !fileStack.isEmpty()) {
			DefaultFileTreeNode targetChild = null;
			VirtualFile fChild = fileStack.pop();
			refreshNode(targetNode);
			int numChildren = targetNode.getChildCount();
			for (int i = 0; i < numChildren; i++) {
				DefaultFileTreeNode child = (DefaultFileTreeNode)targetNode.getChildAt(i);
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
	 * このクラス標準のファイルフィルタを取得する。
	 * @return	<code>VirtualFileFilter</code> オブジェクト
	 */
	static protected VirtualFileFilter getDefaultFileFilter() {
		if (_defFileFilter == null) {
			_defFileFilter = new DefaultFileFilter();
		}
		return _defFileFilter;
	}

	/**
	 * このクラス標準のツリーノードコンパレータを取得する。
	 * @return	<code>Comparator</code> オブジェクト
	 */
	static protected Comparator<? super DefaultFileTreeNode> getDefaultFileTreeNodeComparator() {
		if (_defComparator == null) {
			_defComparator = new DefaultFileTreeNodeComparator();
		}
		return _defComparator;
	}

	/**
	 * 指定された抽象パスを格納する、ツリーノードのインスタンスを生成する。
	 * このメソッドは、抽象パスを格納するツリーノードを生成するのみであり、
	 * <em>parent</em> には子ノードとして追加しない。
	 * @param parent	指定された抽象パスの親とするツリーノード
	 * @param file		抽象パス
	 * @return	<code>DefaultFileTreeNode</code> オブジェクト
	 */
	protected DefaultFileTreeNode createFileTreeNodeByFile(DefaultFileTreeNode parent, VirtualFile file) {
		return new DefaultFileTreeNode(new DefaultFileTreeData(file));
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
	protected int getInsertionIndex(DefaultFileTreeNode parent, DefaultFileTreeNode child) {
		if (parent == null)
			throw new IllegalArgumentException("parent argument is null.");
		if (child == null)
			throw new IllegalArgumentException("child argument is null.");
		
		Comparator<? super DefaultFileTreeNode> c = getNodeComparator();
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

	/**
	 * このモデルにおける有効なルートとなるノードから全ての子ノードを削除する。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このクラスの実装では、有効なルートとなるノードは、このモデルのルートノードとなる。
	 * </blockquote>
	 */
	protected void removeAllChildrenFromAvailableRootNode() {
		DefaultFileTreeNode rootnode = getRoot();
		if (rootnode != null) {
			rootnode.removeAllChildren();
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	/**
	 * 標準的なファイルリストのフィルタリングを行うファイルフィルター。
	 * このクラスの実装では、隠しファイル、ドット(.)で始まるファイル名の
	 * ものはリストから除外する。
	 */
	static public class DefaultFileFilter implements VirtualFileFilter
	{
		public boolean accept(VirtualFile pathname) {
			// 隠しファイルは表示しない
			if (pathname.isHidden()) {
				return false;
			}
			
			// 表示許可する
			return true;
		}
	}

	/**
	 * 標準的なツリーノードの比較を行うコンパレータ。
	 * このクラスの実装では、ディレクトリとファイルの関係は
	 * (ディレクトリ &le; ファイル) とし、ディレクトリ同士もしくはファイル同士の
	 * 比較はファイルオブジェクトの比較結果となる。
	 */
	static public class DefaultFileTreeNodeComparator implements Comparator<DefaultFileTreeNode>
	{
		public int compare(DefaultFileTreeNode node1, DefaultFileTreeNode node2) {
			// directory < file
			if (node1.isDirectory()) {
				if (!node2.isDirectory()) {
					// node1(directory) < node2(file)
					return (-1);
				}
				// compare by directory name
				String name1 = node1.getFilename();
				String name2 = node2.getFilename();
				if (name1.equalsIgnoreCase(name2)) {
					return 0;
				} else {
					return name1.compareToIgnoreCase(name2);
				}
			}
			else if (node2.isDirectory()) {
				// node1(file) > node2(directory)
				return (1);
			}
			
			// compare by file name
			String name1 = node1.getFilename();
			String name2 = node2.getFilename();
			int cmp = name1.compareToIgnoreCase(name2);
			if (cmp == 0 && node1.getFileObject()!=null && node2.getFileObject()!=null) {
				cmp = node1.getFileObject().compareTo(node2.getFileObject());
			}
			return cmp;
		}
	}
}
