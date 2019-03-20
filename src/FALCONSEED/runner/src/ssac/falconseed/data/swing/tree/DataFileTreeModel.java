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
 *  Copyright 2007-2011  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)DataFileTreeModel.java	1.10	2011/02/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.data.swing.tree;

import java.util.Comparator;

import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import ssac.falconseed.runner.RunnerMessages;
import ssac.util.Objects;
import ssac.util.io.Files;
import ssac.util.io.VirtualFile;
import ssac.util.io.VirtualFileFilter;

/**
 * データファイルを表示するためのツリーモデル。
 * <p>このモデルでは、ユーザー定義のルートディレクトリを複数設定することができる。
 * 
 * @version 1.10	2011/02/14
 * @since 1.10
 */
public class DataFileTreeModel extends FileTreeModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** システムルートとなるツリーノード **/
	private FileTreeNode	_rdSystem;
	/** ユーザールートとなるツリーノード **/
	private FileTreeNode	_rdUser;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public DataFileTreeModel() {
		this(null, null);
	}
	
	public DataFileTreeModel(VirtualFile systemRootDir, VirtualFile userRootDir)
	{
		this(systemRootDir, userRootDir, getDefaultFileFilter(), getDefaultFileTreeNodeComparator());
	}
	
	public DataFileTreeModel(VirtualFile systemRootDir, VirtualFile userRootDir,
								VirtualFileFilter filter, Comparator<FileTreeNode> comparator)
	{
		super(null, filter, comparator);
		super.setRoot(createDefaultRootNode());
		if (systemRootDir != null) {
			createSystemRootNode(systemRootDir);
		}
		if (userRootDir != null) {
			createUserRootNode(userRootDir);
		}
	}
	
	protected DataFileTreeModel(FileTreeNode rootNode,
								VirtualFileFilter filter, Comparator<FileTreeNode> comparator)
	{
		super(rootNode, filter, comparator);
	}
	
	protected FileTreeNode createDefaultRootNode() {
		return new DefaultFileTreeModelRootNode();
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
	
	public ITreeFileData getSystemRootObject() {
		return (_rdSystem != null ? _rdSystem.getUserObject() : null);
	}
	
	public ITreeFileData getUserRootObject() {
		return (_rdUser != null ? _rdUser.getUserObject() : null);
	}
	
	public FileTreeNode getSystemRootNode() {
		return _rdSystem;
	}
	
	public FileTreeNode getUserRootNode() {
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
	/**
	 * 指定されたノードが、システムルートノードかを判定する。
	 * @param node	判定するノード
	 * @return	システムルートノードの場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean isSystemRootNode(TreeNode node) {
		FileTreeNode ndSystem = getSystemRootNode();
		return (ndSystem!=null && ndSystem==node);
	}

	/**
	 * 指定されたノードが、ユーザー定義ルートノードかを判定する。
	 * @param node	判定するノード
	 * @return	ユーザー定義ルートノードの場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean isUserRootNode(TreeNode node) {
		FileTreeNode ndUser = getUserRootNode();
		return (ndUser!=null && ndUser==node);
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
	@Override
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

	/**
	 * 指定されたノードの内容を最新の情報に更新する。
	 * このメソッドは、指定されたノードに格納されているファイルオブジェクトをキーに、
	 * 子ノードを再構成する。
	 * このメソッドでは、指定ノードの直接の子ノード構成のみを更新し、それ以外の子孫
	 * ノードの構成は更新しない。
	 * @param targetNode	更新対象のノード
	 */
	@Override
	public void refreshChildren(FileTreeNode targetNode) {
		// ルートノードの場合は特殊な処理
		if (targetNode.isRoot()) {
			return;
		}
		
		// ファイル構造により更新
		super.refreshChildren(targetNode);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 指定されたツリーパスから、このモデルの有効なルートとなる
	 * ノードの位置を示すインデックスを取得する。
	 * <p>有効なルートとなるのは、システムルートもしくはユーザールートとなる。
	 * @param treepath	判定するツリーパス
	 * @return	有効なルートとなるノードを示すインデックスを返す。
	 * 			有効なルートがない場合は -1 を返す。
	 */
	@Override
	protected int getAvailableRootNodeIndex(TreePath treepath) {
		// システムルートノードならびにユーザールートノードは、
		// モデルのルートノードの直下に存在するため、
		// このメソッドは 1 を返す。
		return 1;
	}
	
	/**
	 * 指定されたノードのパスを示す配列から、このモデルの有効なルートとなる
	 * ノードの位置を示すインデックスを取得する。
	 * <p>有効なルートとなるのは、システムルートもしくはユーザールートとなる。
	 * @param nodepath	ツリーパスを示すノードの配列
	 * @return	有効なルートとなるノードを示すインデックスを返す。
	 * 			有効なルートがない場合は -1 を返す。
	 */
	@Override
	protected int getAvailableRootNodeIndex(TreeNode[] nodepath) {
		// システムルートノードならびにユーザールートノードは、
		// モデルのルートノードの直下に存在するため、
		// このメソッドは 1 を返す。
		return 1;
	}

	/**
	 * このクラス標準のファイルフィルタを取得する。
	 * @return	<code>VirtualFileFilter</code> オブジェクト
	 */
	static protected VirtualFileFilter getDefaultFileFilter() {
		if (_defFileFilter == null) {
			_defFileFilter = new DataFileFilter(false, null, null);
		}
		return _defFileFilter;
	}
	
	protected ITreeFileData createSystemRootFileData(VirtualFile newFile) {
		return new TreeRootFileData(RunnerMessages.getInstance().DataFile_system, newFile);
	}
	
	protected ITreeFileData createUserRootFileData(VirtualFile newFile) {
		return new TreeRootFileData(RunnerMessages.getInstance().DataFile_user, newFile);
	}
	
	protected void createSystemRootNode(VirtualFile newFile) {
		FileTreeNode rootNode = getRoot();
		FileTreeNode ndChild = new FileTreeNode(createSystemRootFileData(newFile));
		_rdSystem = ndChild;
		insertNodeInto(ndChild, rootNode, 0);
	}
	
	protected void createUserRootNode(VirtualFile newFile) {
		FileTreeNode rootNode = getRoot();
		FileTreeNode ndChild = new FileTreeNode(createUserRootFileData(newFile));
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
		FileTreeNode ndChild = _rdSystem;
		ndChild.setUserObject(createSystemRootFileData(newFile));
		nodeChanged(ndChild);
		refreshChildren(ndChild);
	}
	
	protected void replaceUserRootFile(VirtualFile newFile) {
		FileTreeNode ndChild = _rdUser;
		ndChild.setUserObject(createUserRootFileData(newFile));
		nodeChanged(ndChild);
		refreshChildren(ndChild);
	}
	

	/**
	 * このモデルにおける有効なルートとなるノードから全ての子ノードを削除する。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このクラスの実装では、モデルの有効なルーとなるノードは、
	 * システムルートノードならびにユーザールートノードとなる。
	 * </blockquote>
	 */
	@Override
	protected void removeAllChildrenFromAvailableRootNode() {
		FileTreeNode ndSystem = getSystemRootNode();
		if (ndSystem != null) {
			ndSystem.removeAllChildren();
		}
		FileTreeNode ndUser = getUserRootNode();
		if (ndUser != null) {
			ndUser.removeAllChildren();
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	/**
	 * データファイル表示用ツリーのルートノード
	 */
	static protected class DefaultFileTreeModelRootNode extends FileTreeNode
	{
		static private final String DefaultDisplayName = "root";

		public DefaultFileTreeModelRootNode() {
			super(null, true);
		}

		@Override
		public boolean isLeaf() {
			return false;
		}

		@Override
		public ITreeFileData getUserObject() {
			return null;
		}

		@Override
		public void setUserObject(Object userObject) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public String getDisplayName() {
			return DefaultDisplayName;
		}

		@Override
		public boolean canRead() {
			return true;
		}

		@Override
		public boolean canWrite() {
			return true;
		}

		@Override
		public boolean exists() {
			return true;
		}

		@Override
		public String getFilename() {
			return getDisplayName();
		}

		@Override
		public VirtualFile getFileObject() {
			return null;
		}

		@Override
		public String getFilePath() {
			return null;
		}

		@Override
		public boolean isDirectory() {
			return true;
		}

		@Override
		public boolean isFile() {
			return false;
		}

		@Override
		public boolean isHidden() {
			return true;
		}

		@Override
		public long lastModified() {
			return 0L;
		}

		//------------------------------------------------------------
		// Internal methods
		//------------------------------------------------------------

		//------------------------------------------------------------
		// Inner classes
		//------------------------------------------------------------
	}
}
