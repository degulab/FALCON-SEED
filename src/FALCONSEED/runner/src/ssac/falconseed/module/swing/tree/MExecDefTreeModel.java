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
 *  Copyright 2007-2010  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)MExecDefTreeModel.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing.tree;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Stack;

import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import ssac.aadl.module.ModuleFileManager;
import ssac.falconseed.module.MExecDefFileManager;
import ssac.falconseed.runner.RunnerMessages;
import ssac.util.Objects;
import ssac.util.Strings;
import ssac.util.io.Files;
import ssac.util.io.VirtualFile;
import ssac.util.io.VirtualFileFilter;
import ssac.util.io.VirtualFileOperationException;
import ssac.util.swing.tree.AbstractTreeModel;

/**
 * モジュール実行定義専用のツリーモデル。
 * 
 * @version 1.00	2010/12/20
 */
public class MExecDefTreeModel extends AbstractTreeModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** ツリーノードに登録するファイルを選択するためのフィルタ **/
	private VirtualFileFilter	_fileFilter;
	/** ツリーノードの表示順序を制御するためのコンパレータ **/
	private Comparator<MExecDefTreeNode>	_comparator;

	/** システムルートとなるツリーノード **/
	private MExecDefTreeNode	_rdSystem;
	/** ユーザールートとなるツリーノード **/
	private MExecDefTreeNode	_rdUser;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MExecDefTreeModel() {
		this(null, null);
	}
	
	public MExecDefTreeModel(VirtualFile systemRootDir, VirtualFile userRootDir) {
		super(new MExecDefTreeRootNode());
		this._fileFilter = new DefaultExecDefFileFilter();
		this._comparator = new MExecDefTreeNodeComparator();
		if (systemRootDir != null) {
			createSystemRootNode(systemRootDir);
		}
		if (userRootDir != null) {
			createUserRootNode(userRootDir);
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public VirtualFile getSystemRootDirectory() {
		return (_rdSystem != null ? _rdSystem.getFileObject() : null);
	}
	
	public VirtualFile getUserRootDirectory() {
		return (_rdUser != null ? _rdUser.getFileObject() : null);
	}
	
	public IMExecDefFile getSystemRootObject() {
		return (_rdSystem != null ? _rdSystem.getUserObject() : null);
	}
	
	public IMExecDefFile getUserRootObject() {
		return (_rdUser != null ? _rdUser.getUserObject() : null);
	}
	
	public MExecDefTreeNode getSystemRootNode() {
		return _rdSystem;
	}
	
	public MExecDefTreeNode getUserRootNode() {
		return _rdUser;
	}
	
	public void setSystemRootDirectory(VirtualFile newDir) {
		VirtualFile vfOldDir = getSystemRootDirectory();
		if (Objects.isEqual(vfOldDir, newDir)) {
			// no changed
			return;
		}
		
		if (vfOldDir == null && newDir != null) {
			// create new system root node
			createSystemRootNode(newDir);
		}
		else if (vfOldDir != null && newDir == null) {
			// remove system root node
			removeSystemRootNode();
		}
		else {
			// replace system root directory
			replaceSystemRootFile(newDir);
		}
	}
	
	public void setUserRootDirectory(VirtualFile newDir) {
		VirtualFile vfOldDir = getUserRootDirectory();
		if (Objects.isEqual(vfOldDir, newDir)) {
			// no changed
			return;
		}
		
		if (vfOldDir == null && newDir != null) {
			// create new user root node
			createUserRootNode(newDir);
		}
		else if (vfOldDir != null && newDir == null) {
			// remove user root node
			removeUserRootNode();
		}
		else {
			// replace user root directory
			replaceUserRootFile(newDir);
		}
	}

	/**
	 * 指定されたツリーパスから、パスを示す文字列を取得する。
	 * このパス文字列は、システムルートディレクトリ、もしくは
	 * ユーザールートディレクトリをルートディレクトリとし、
	 * スラッシュ(/) をパス区切り文字とする抽象パスの表示文字列となる。
	 * システムルートディレクトリもしくはユーザールートディレクトリの
	 * 名前は、それぞれに設定された表示名となる。
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
		StringBuilder sb = new StringBuilder();
		int len = treepath.getPathCount();
		if (len > 1) {
			for (int i = 1; i < len; i++) {
				MExecDefTreeNode node = (MExecDefTreeNode)treepath.getPathComponent(i);
				sb.append(Files.CommonSeparatorChar);
				sb.append(node.toString());
			}
		} else {
			sb.append(Files.CommonSeparatorChar);
		}
		
		return sb.toString();
	}

	/**
	 * 指定されたノードから、パスを示す文字列を取得する。
	 * このパス文字列は、システムルートディレクトリ、もしくは
	 * ユーザールートディレクトリをルートディレクトリとし、
	 * スラッシュ(/) をパス区切り文字とする抽象パスの表示文字列となる。
	 * システムルートディレクトリもしくはユーザールートディレクトリの
	 * 名前は、それぞれに設定された表示名となる。
	 * @param node	表示するノード
	 * @return	抽象パスの表示文字列
	 * @throws NullPointerException		引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	指定されたパスが、このモデルのものではない場合
	 */
	public String formatFilePath(MExecDefTreeNode node) {
		if (!node.isNodeAncestor(getRoot())) {
			// このモデルのノードではない
			throw new IllegalArgumentException("The specified node is not a node of this model.");
		}

		// パス文字列を生成
		StringBuilder sb = new StringBuilder();
		TreeNode[] nodepath = getPathToRoot(node);
		int len = nodepath.length;
		if (len > 1) {
			for (int i = 1; i < len; i++) {
				sb.append(Files.CommonSeparatorChar);
				sb.append(((MExecDefTreeNode)nodepath[i]).toString());
			}
		} else {
			sb.append(Files.CommonSeparatorChar);
		}
		
		return sb.toString();
	}

	/**
	 * 指定されたファイルから、パスを示す文字列を生成する。
	 * このパス文字列は、システムルートディレクトリ、もしくは
	 * ユーザールートディレクトリをルートディレクトリとし、
	 * スラッシュ(/) をパス区切り文字とする抽象パスの表示文字列となる。
	 * システムルートディレクトリもしくはユーザールートディレクトリの
	 * 名前は、それぞれに設定された表示名となる。
	 * <p>指定されたファイルが、システムルートディレクトリもしくは
	 * ユーザールートディレクトリ以下に存在しない場合は、絶対パスを示す文字列を返す。
	 * @param file	パスを表示するファイル
	 * @return	抽象パスの表示文字列
	 * @throws NullPointerException		引数が <tt>null</tt> の場合
	 */
	public String formatFilePath(VirtualFile file) {
		if (!file.isAbsolute()) {
			file = file.getAbsoluteFile();
		}
		
		// パス文字列を生成
		StringBuilder sb = new StringBuilder();
		VirtualFile vfSystem = getSystemRootDirectory();
		VirtualFile vfUser   = getUserRootDirectory();
		if (vfSystem != null && file.isDescendingFrom(vfSystem)) {
			sb.append(Files.CommonSeparatorChar);
			sb.append(getSystemRootNode().toString());
			sb.append(Files.CommonSeparatorChar);
			sb.append(file.relativePathFrom(vfSystem, Files.CommonSeparatorChar));
		}
		else if (vfUser != null && file.isDescendingFrom(vfUser)) {
			sb.append(Files.CommonSeparatorChar);
			sb.append(getUserRootNode().toString());
			sb.append(Files.CommonSeparatorChar);
			sb.append(file.relativePathFrom(vfUser, Files.CommonSeparatorChar));
		}
		else {
			sb.append(file.toString());
		}

		return sb.toString();
	}

	public VirtualFileFilter getFileFilter() {
		return _fileFilter;
	}
	
	public void setFileFilter(VirtualFileFilter filter) {
		if (_fileFilter != filter) {
			_fileFilter = filter;
			// ルートノード直下のノードを削除し、子の構成を更新する
			// システムルート、ユーザールートの子ノードを全て削除し、再構築する。
			MExecDefTreeNode ndSystem = getSystemRootNode();
			if (ndSystem != null) {
				ndSystem.removeAllChildren();
			}
			MExecDefTreeNode ndUser   = getUserRootNode();
			if (ndUser != null) {
				ndUser.removeAllChildren();
			}
			reload();
		}
	}
	
	public Comparator<MExecDefTreeNode> getNodeComparator() {
		return _comparator;
	}
	
	public void setNodeComparaotr(Comparator<MExecDefTreeNode> c) {
		if (_comparator != c) {
			_comparator = c;
			// システムルート、ユーザールートの子ノードを全て削除し、再構築する。
			MExecDefTreeNode ndSystem = getSystemRootNode();
			if (ndSystem != null) {
				ndSystem.removeAllChildren();
			}
			MExecDefTreeNode ndUser   = getUserRootNode();
			if (ndUser != null) {
				ndUser.removeAllChildren();
			}
			reload();
		}
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
		VirtualFile vfSystemDir = getSystemRootDirectory();
		VirtualFile vfUserDir   = getUserRootDirectory();
		if (vfSystemDir != null && targetFile.isDescendingFrom(vfSystemDir)) {
			return getTreePathForFile(getSystemRootNode(), targetFile);
		}
		else if (vfUserDir != null && targetFile.isDescendingFrom(vfUserDir)) {
			return getTreePathForFile(getUserRootNode(), targetFile);
		}
		else {
			return null;
		}
	}
	
	protected TreePath getTreePathForFile(MExecDefTreeNode ndSearchRoot, VirtualFile targetFile)
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

		MExecDefTreeNode targetNode = ndSearchRoot;
		TreePath treePath = new TreePath(targetNode.getPath());
		while (treePath != null && !fileStack.isEmpty()) {
			MExecDefTreeNode targetChild = null;
			VirtualFile fChild = fileStack.pop();
			refreshNode(targetNode);
			int numChildren = targetNode.getChildCount();
			for (int i = 0; i < numChildren; i++) {
				MExecDefTreeNode child = (MExecDefTreeNode)targetNode.getChildAt(i);
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
	public void changeFileObject(MExecDefTreeNode targetNode, VirtualFile newFile) {
		targetNode.setUserObject(targetNode.getUserObject().replaceFile(newFile));
		int numChildren = targetNode.getChildCount();
		for (int i = 0; i < numChildren; i++) {
			MExecDefTreeNode child = (MExecDefTreeNode)targetNode.getChildAt(i);
			VirtualFile newChildFile = newFile.getChildFile(child.getFilename());
			changeFileObject(child, newChildFile);
		}
	}
	
	public void sortChildren(MExecDefTreeNode parentNode) {
		parentNode.sortChildren(getNodeComparator());
		reload(parentNode);
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
	public MExecDefTreeNode createDirectory(MExecDefTreeNode parent, String name)
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
		MExecDefTreeNode newNode = new MExecDefTreeNode(new MExecDefFile(fDir));
		int index = getInsertionIndex(parent, newNode);
		if (index < 0)
			throw new AssertionError("Illegal insertion index : " + index);
		insertNodeInto(newNode, parent, index);
		
		return newNode;
	}

	/**
	 * <em>parent</em> の直下に、指定された名前のモジュール実行定義を作成する。
	 * 新規に作成されるモジュール実行定義はディレクトリであり、作成されたディレクトリの
	 * 直下に空のモジュール実行定義ファイルが作成される。
	 * @param parent	親ディレクトリとなる抽象パスを格納するツリーノード
	 * @param name		作成するモジュール実行定義名
	 * @return	生成されたディレクトリを示すノード
	 * @throws IllegalArgumentException	<em>parent</em> がディレクトリではない場合、
	 * 										もしくは <em>name</em> が有効な文字列ではない場合
	 * @throws IllegalStateException		<em>parent</em> の子に <em>name</em> と同名の
	 * 										ファイルもしくはディレクトリが存在する場合
	 * @throws VirtualFileOperationException	ディレクトリもしくはファイルの作成に失敗した場合
	 */
	public MExecDefTreeNode createMExecDef(MExecDefTreeNode parent, String name)
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
			throw new IllegalStateException("New Module Execution Definition already exists! : \"" + fDir.toString() + "\"");
		try {
			if (!fDir.mkdir()) {
				throw new VirtualFileOperationException(VirtualFileOperationException.MKDIR, null, fDir);
			}
		} catch (SecurityException ex) {
			throw new VirtualFileOperationException(VirtualFileOperationException.MKDIR, null, fDir, ex);
		}
		
		// モジュール実行定義ファイルの作成
		VirtualFile fFile = MExecDefFileManager.getModuleExecDefDataFile(fDir);
		try {
			if (!fFile.createNewFile()) {
				throw new VirtualFileOperationException(VirtualFileOperationException.CREATE, null, fFile);
			}
		} catch (IOException ex) {
			throw new VirtualFileOperationException(VirtualFileOperationException.CREATE, null, fFile, ex);
		} catch (SecurityException ex) {
			throw new VirtualFileOperationException(VirtualFileOperationException.CREATE, null, fFile, ex);
		}
		
		// モジュール実行定義ノードの作成
		MExecDefTreeNode newNode = new MExecDefTreeNode(new MExecDefDataFile(fDir));
		int index = getInsertionIndex(parent, newNode);
		if (index < 0)
			throw new AssertionError("Illegal insertion index : " + index);
		insertNodeInto(newNode, parent, index);
		
		return newNode;
	}

	//------------------------------------------------------------
	// Implement AbstractTreeModel interfaces
	//------------------------------------------------------------

	public void valueForPathChanged(TreePath path, Object newValue) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRoot(TreeNode root) {
		if (root instanceof MExecDefTreeRootNode)
			throw new IllegalArgumentException("The specified TreeNode is not instance of MExecDefTreeRootNode.");
		super.setRoot(root);
	}

	@Override
	public MExecDefTreeRootNode getRoot() {
		return (MExecDefTreeRootNode)super.getRoot();
	}

	/**
	 * このモデルのルートノードのプロパティと子ノード構成を最新の情報に更新する。
	 */
	public void refresh() {
		if (_rootNode != null) {
			refreshNode((MExecDefTreeNode)_rootNode);
		}
	}

	/**
	 * ルートノードの子ノード構成を最新の情報に更新する。
	 */
	public void refreshChildren() {
		if (_rootNode != null) {
			refreshChildren((MExecDefTreeNode)_rootNode);
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
	public void refreshNode(MExecDefTreeNode targetNode) {
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
	public void refreshChildren(MExecDefTreeNode targetNode) {
		// ルートノードの場合は特殊な処理
		if (targetNode instanceof MExecDefTreeRootNode) {
			return;
		}
		
		// ルートノード以外は、ファイル構造により更新
		IMExecDefFile file = targetNode.getUserObject();
		if (file.isDirectory()) {
			// ファイルリストを取得
			IMExecDefFile[] childFileArray = file.listFiles(getFileFilter());
			if (childFileArray != null && childFileArray.length > 0) {
				// ディレクトリにファイルあり
				Set<IMExecDefFile> files = new LinkedHashSet<IMExecDefFile>(Arrays.asList(childFileArray));
				
				// ファイルの存在しない子ノード、ならびにファイルリストに存在しない子ノードを除去
				for (int i = targetNode.getChildCount() - 1; i >= 0; i--) {
					MExecDefTreeNode child = (MExecDefTreeNode)targetNode.getChildAt(i);
					IMExecDefFile childFile = child.getUserObject();
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
				MExecDefTreeNode dummyNode = new MExecDefTreeNode(file);
				for (IMExecDefFile f : files) {
					dummyNode.setUserObject(f);
					int index = targetNode.binarySearch(dummyNode, getNodeComparator());
					if (index < 0) {
						// create new node
						MExecDefTreeNode newNode = new MExecDefTreeNode(f);
						insertNodeInto(newNode, targetNode, (Math.abs(index) - 1));
					} else {
						// already exists
						MExecDefTreeNode existChild = (MExecDefTreeNode)targetNode.getChildAt(index);
						if (!existChild.getUserObject().equals(f)) {
							existChild.setUserObject(f);
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
	
	protected void setRootNode(MExecDefTreeRootNode newRootNode) {
		super.setRoot(newRootNode);
	}
	
	protected void createSystemRootNode(VirtualFile newFile) {
		MExecDefTreeRootNode rootNode = getRoot();
		MExecDefTreeNode ndChild = new MExecDefTreeNode(new MExecDefFile(newFile));
		ndChild.setDisplayName(RunnerMessages.getInstance().MExecDef_system);
		_rdSystem = ndChild;
		insertNodeInto(ndChild, rootNode, 0);
	}
	
	protected void createUserRootNode(VirtualFile newFile) {
		MExecDefTreeRootNode rootNode = getRoot();
		MExecDefTreeNode ndChild = new MExecDefTreeNode(new MExecDefFile(newFile));
		ndChild.setDisplayName(RunnerMessages.getInstance().MExecDef_user);
		_rdUser = ndChild;
		insertNodeInto(ndChild, rootNode, rootNode.getChildCount());
	}
	
	protected void removeSystemRootNode() {
		removeNodeFromParent(_rdSystem);
	}
	
	protected void removeUserRootNode() {
		removeNodeFromParent(_rdUser);
	}
	
	protected void replaceSystemRootFile(VirtualFile newFile) {
		MExecDefTreeNode ndChild = _rdSystem;
		ndChild.setUserObject(new MExecDefFile(newFile));
		nodeChanged(ndChild);
		refreshChildren(ndChild);
	}
	
	protected void replaceUserRootFile(VirtualFile newFile) {
		MExecDefTreeNode ndChild = _rdUser;
		ndChild.setUserObject(new MExecDefFile(newFile));
		nodeChanged(ndChild);
		refreshChildren(ndChild);
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
	protected int getInsertionIndex(MExecDefTreeNode parent, MExecDefTreeNode child) {
		if (parent == null)
			throw new IllegalArgumentException("parent argument is null.");
		if (child == null)
			throw new IllegalArgumentException("child argument is null.");
		
		Comparator<? super MExecDefTreeNode> c = getNodeComparator();
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
	 * 標準的なファイルリストのフィルタリングを行うファイルフィルター。
	 * このクラスの実装では、隠しファイル、ドット(.)で始まるファイル名、&quot;.prefs&quot; で終了するファイル名の
	 * ものはリストから除外する。
	 */
	static public class DefaultExecDefFileFilter implements VirtualFileFilter
	{
		public boolean accept(VirtualFile pathname) {
			// 隠しファイルは表示しない
			if (pathname.isHidden()) {
				return false;
			}
			
			// '.' で始まるファイルやディレクトリは非表示とする
			if (Strings.startsWithIgnoreCase(pathname.getName(), ".")) {
				return false;
			}
			
			// .prefs ファイルは非表示とする
			if (pathname.isFile()) {
				// 設定ファイルは表示しない
				if (Strings.endsWithIgnoreCase(pathname.getName(), ModuleFileManager.EXT_FILE_PREFS)) {
					return false;
				}
			}
			
			// 表示許可する
			return true;
		}
	}

	/**
	 * 標準的なツリーノードの比較を行うコンパレータ。
	 * このクラスの実装では、ディレクトリとファイルの関係は
	 * (ディレクトリ &le; ファイル) とし、ディレクトリ同士もしくはファイル同士の
	 * 比較は <code>File</code> の比較結果となる。
	 */
	static public class MExecDefTreeNodeComparator implements Comparator<MExecDefTreeNode>
	{
		public int compare(MExecDefTreeNode node1, MExecDefTreeNode node2) {
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
			if (name1.equalsIgnoreCase(name2)) {
				return node1.getFileObject().compareTo(node2.getFileObject());
			} else {
				return name1.compareToIgnoreCase(name2);
			}
		}
	}
	
	/**
	 * モジュール実行定義ファイルツリーのルート専用ノード。
	 * このノードは、システムルートとユーザー定義ルートの２つの子ノードのみを管理する。
	 * 本アプリケーションでは、プロジェクト名やフォルダ名・ファイル名は Windows 環境を
	 * 基準とし、大文字小文字を区別しないでチェックする。
	 * 
	 * @version 1.00	2010/12/20
	 */
	static protected class MExecDefTreeRootNode extends MExecDefTreeNode
	{
		static private final String DefaultDisplayName = "root";
		
		public MExecDefTreeRootNode() {
			super(null, true);
		}

		@Override
		public boolean isLeaf() {
			return false;
		}

		@Override
		public void setUserObject(Object userObject) {
			throw new UnsupportedOperationException("Unsupported to use user data.");
		}
		
		@Override
		public boolean exists() {
			return true;
		}
		
		@Override
		public boolean isFile() {
			return false;
		}
		
		@Override
		public boolean isDirectory() {
			return true;
		}
		
		@Override
		public boolean isExecDefData() {
			return false;
		}
		
		@Override
		public long lastModified() {
			return 0L;
		}
		
		@Override
		public String getFilename() {
			return null;
		}
		
		@Override
		public String getDisplayName() {
			String strName = getDisplayString();
			return (strName==null ? DefaultDisplayName : strName);
		}
		
		@Override
		public VirtualFile getFileObject() {
			return null;
		}

		@Override
		public String getFilePath() {
			return null;
		}
		
		public void sortChildren(Comparator<? super MExecDefTreeNode> nodeComparator) {
			// no operation
		}
		
		@Override
		public int binarySearch(MExecDefTreeNode child, Comparator<? super MExecDefTreeNode> nodeComparator) {
			throw new UnsupportedOperationException("Unsupported binary search.");
		}
	}
}
