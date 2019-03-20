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
 * @(#)FileTree.java	1.20	2012/03/05
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.file.swing.tree;

import java.awt.Component;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.aadl.module.ModuleFileManager;
import ssac.falconseed.file.VirtualFileProgressMonitorTask;
import ssac.util.Strings;
import ssac.util.io.VirtualFile;
import ssac.util.io.VirtualFileOperationException;
import ssac.util.logging.AppLogger;
import ssac.util.swing.Application;
import ssac.util.swing.tree.DnDTree;
import ssac.util.swing.tree.IDnDTreeHandler;

/**
 * 汎用ファイルツリーコンポーネント。
 * 
 * @version 1.20	2012/03/05
 * @since 1.20
 */
public class FileTree extends DnDTree
implements TreeWillExpandListener, TreeSelectionListener
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final FocusRequester _hFocusRequester = new FocusRequester();

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public FileTree() {
		this(null, null);
	}
	
	public FileTree(DefaultFileTreeModel newModel) {
		this(newModel, null);
	}
	
	public FileTree(DefaultFileTreeModel newModel, IDnDTreeHandler newHandler) {
		super(newModel==null ? new DefaultFileTreeModel() : newModel);
		setTreeHandler(newHandler);
		
		setRootVisible(true);		// ルートノードを表示
		setShowsRootHandles(true);	// 最上位にハンドルを表示する
		setTooltipEnabled(false);	// ツールチップは表示しない

		addTreeSelectionListener(this);
		addTreeWillExpandListener(this);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public void setDefaultCutAction(Action action) {
		getActionMap().put(TreeEditCutActionName, action);
	}
	
	public void setDefaultCopyAction(Action action) {
		getActionMap().put(TreeEditCopyActionName, action);
	}
	
	public void setDefaultPasteAction(Action action) {
		getActionMap().put(TreeEditPasteActionName, action);
	}
	
	public void setDefaultDeleteAction(Action action) {
		getActionMap().put(TreeEditDeleteActionName, action);
	}

	/**
	 * このコンポーネントに設定されているモデルを取得する。
	 * このツリーコンポーネントのモデルは、{@link DefaultFileTreeModel} クラスの
	 * インスタンスに限定される。
	 * @return	このモデルに設定されている <code>DefaultFileTreeModel</code> オブジェクト
	 */
	@Override
	public DefaultFileTreeModel getModel() {
		return (DefaultFileTreeModel)super.getModel();
	}
	
	public String getTreeNodeFileProperty(TreePath treepath) {
		if (treepath != null) {
			return getTreeNodeFileProperty((DefaultFileTreeNode)treepath.getLastPathComponent());
		} else {
			return null;
		}
	}
	
	public String getTreeNodeFileProperty(DefaultFileTreeNode node) {
		String strProperty = getTreeNodeFileInfo(node);
		if (strProperty != null) {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			sb.append(node.getDisplayName());
			sb.append("] ");
			sb.append(strProperty);
			strProperty = sb.toString();
		}
		return strProperty;
	}
	
	public String getTreeNodeFileInfo(DefaultFileTreeNode node) {
		if (node == null || node.getFileObject() == null) {
			return null;
		}
		
		StringBuilder sb = new StringBuilder();

		// 最終更新日を表示する
		long lastmodified = node.lastModified();
		sb.append(DateFormat.getDateTimeInstance().format(new Date(lastmodified)));
		
		// 単一ファイルなら、サイズを表示する
		if (node.isFile()) {
			long len = node.getFileObject().length();
			double dlen;
			if ((len / 1048576L) > 0L) {
				dlen = (double)len / 1048576.0;
				DecimalFormat df = new DecimalFormat("#,##0.##");
				df.setMaximumFractionDigits(2);
				sb.append(" (");
				sb.append(df.format(dlen));
				sb.append(" MB)");
			}
			else if ((len / 1024L) > 0L) {
				dlen = (double)len / 1024.0;
				DecimalFormat df = new DecimalFormat("#,##0.##");
				df.setMaximumFractionDigits(2);
				sb.append(" (");
				sb.append(df.format(dlen));
				sb.append(" KB)");
			}
			else {
				sb.append(" (");
				sb.append(len);
				sb.append(" bytes)");
			}
		}

		return sb.toString();
	}

	/**
	 * このコンポーネントに指定されたツリーモデルを設定する。
	 * 設定可能なツリーモデルは、{@link DefaultFileTreeModel} クラスのインスタンスに
	 * 限定される。
	 * @param newModel	設定する <code>DefaultFileTreeModel</code> オブジェクト
	 * @throws NullPointerException	<em>newModel</em> が <tt>null</tt> の場合
	 * @throws ClassCastException	<em>newModel</em> が <code>DefaultFileTreeModel</code> の
	 * 								インスタンスではない場合
	 */
	@Override
	public void setModel(TreeModel newModel) {
		if (newModel == null)
			throw new NullPointerException("newModel argument is null.");
		super.setModel((DefaultFileTreeModel)newModel);
	}

	/**
	 * このコンポーネントにフォーカスを設定する。
	 */
	public void setFocus() {
		if (!hasFocus()) {
			SwingUtilities.invokeLater(_hFocusRequester);
		}
	}
//	
//	/**
//	 * このビューを格納するフレームウィンドウを取得する。
//	 * @return	このビューを格納するフレームウィンドウのインスタンス。
//	 * 			このビューがフレームに格納されていない場合は <tt>null</tt> を返す。
//	 */
//	public FrameWindow getFrame() {
//		Window parentFrame = SwingUtilities.windowForComponent(this);
//		if (parentFrame instanceof FrameWindow)
//			return (FrameWindow)parentFrame;
//		else
//			return null;
//	}
	
	/**
	 * 選択されているノードがすべて同じ階層にある場合に <tt>true</tt> を返す。
	 * 選択されていない場合は <tt>false</tt> を返す。
	 */
	public boolean isSelectedAllSameLevels() {
		if (getSelectionCount() > 0) {
			// 複数選択なら、同じレベルか判定する
			TreePath[] paths = getSelectionPaths();
			return isTreePathAllSameLevels(paths);
		}
		else {
			// no selection
			return false;
		}
	}
	
	/**
	 * 指定されたツリーパスがすべて同じ階層のものであるかを検証する。
	 * このメソッドでは、指定されたツリーパスの要素数がすべて同じ場合に同じ階層とみなす。
	 * @param paths	検証するパスの配列
	 * @return	すべて同じ階層のパスであれば <tt>true</tt> を返す。
	 * 			指定された配列の要素が空である場合は <tt>false</tt> を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public boolean isTreePathAllSameLevels(TreePath[] paths) {
		int len = paths.length;
		if (len == 1) {
			// 要素が一つなら、同一階層
			return true;
		}
		else if (len > 0) {
			int level = paths[0].getPathCount();
			for (int i = 1; i < len; i++) {
				if (paths[i].getPathCount() != level) {
					// not same level
					return false;
				}
			}
			// all same levels
			return true;
		}
		else {
			// 要素が空なら判定不可
			return false;
		}
	}

	/**
	 * 現在選択されている一つのノードに関連付けられている抽象パスを取得する。
	 * このメソッドが返す抽象パスは、{@link javax.swing.JTree#getSelectionPath()} メソッドが
	 * 返すノードのものとなる。
	 * @return	選択されているノードの一つからそのノードに関連付けられている抽象パスを返す。
	 * 			選択されていない場合、もしくは抽象パスが関連付けられていない場合は <tt>null</tt> を返す。
	 */
	public VirtualFile getSelectionFile() {
		TreePath path = getSelectionPath();
		if (path == null) {
			return null;
		} else {
			return ((DefaultFileTreeNode)path.getLastPathComponent()).getFileObject();
		}
	}

	/**
	 * 現在選択されているすべてのノードに関連付けられている抽象パスを取得する。
	 * このメソッドが返す抽象パスは、{@link javax.swing.JTree#getSelectionPaths()} メソッドが
	 * 返すノードのものとなる。
	 * なお、抽象パスが関連付けられていないものは <tt>null</tt> とする。
	 * @return	選択されているすべてのノードに関連付けられている抽象パスの配列を返す。
	 * 			選択されていない場合は <tt>null</tt> を返す。
	 */
	public VirtualFile[] getSelectionFiles() {
		TreePath[] paths = getSelectionPaths();
		if (paths == null) {
			return null;
		} else {
			VirtualFile[] files = new VirtualFile[paths.length];
			for (int i = 0; i < paths.length; i++) {
				files[i] = ((DefaultFileTreeNode)paths[i].getLastPathComponent()).getFileObject();
			}
			return files;
		}
	}

	/**
	 * 現在選択されているノードのツリー構造を最新の情報に更新する。
	 * 選択されていない場合は、ルートノードから最新情報に更新する。
	 */
	public void refreshTree() {
		if (getSelectionCount() > 0) {
			TreePath[] paths = getSelectionPaths();
			for (TreePath path : paths) {
				refreshTree(path);
			}
		} else {
			// for Root node
			Object rootNode = getModel().getRoot();
			if (rootNode != null) {
				refreshTree(new TreePath(rootNode));
			}
		}
	}

	/**
	 * 指定されたツリーパスが示す最終ノードを起点に、ツリー構造を最新の情報に更新する。
	 * この更新では、ツリーコンポーネントに表示されているノードの情報のみを更新する。
	 * @param path	更新するノードの位置を示すツリーパス
	 * @throws NullPointerException	<em>path</em> が <tt>null</tt> の場合
	 * @throws ClassCastException		<em>path</em> の終端ノードが {@link DefaultFileTreeNode} の
	 * 									インスタンスではない場合
	 */
	public void refreshTree(TreePath path) {
		DefaultFileTreeNode node = (DefaultFileTreeNode)path.getLastPathComponent();
		if (!node.exists()) {
			// 指定のノードが示すファイルが存在しない場合は、親のレベルで更新する。
			if (!node.isRoot()) {
				refreshTree(path.getParentPath());
			}
			return;
		}
		
		// refresh
		//node.refreshProperties();
		if (isExpanded(path)) {
			getModel().refreshChildren(node);
			int numChildren = node.getChildCount();
			for (int i = 0; i < numChildren; i++) {
				TreePath childPath = path.pathByAddingChild(node.getChildAt(i));
				refreshTree(childPath);
			}
		} else {
			getModel().reload(node);
		}
	}

	@Override
	public Icon convertValueToIcon(Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		if (value instanceof DefaultFileTreeNode) {
			DefaultFileTreeNode node = (DefaultFileTreeNode)value;
			if (node.exists()) {
				// 存在するファイル
				if (node.isDirectory()) {
					if (expanded) {
						return CommonResources.ICON_DIR_OPEN;
					} else {
						return CommonResources.ICON_DIR_CLOSE;
					}
				} else {
					return ModuleFileManager.getDisplayIcon(node.getFilename(), false, false, node.isDirectory(), node.isFile(), expanded, false);
				}
			} else {
				// 存在しないファイルなら、×アイコン
				return CommonResources.ICON_DELETE;
			}
		}
		
		return null;
	}

	@Override
	public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		if (value == null) {
			return "";
		}
		else if (value instanceof DefaultFileTreeNode) {
			return ((DefaultFileTreeNode)value).getDisplayName();
		}
		else {
			return value.toString();
		}
	}

	//------------------------------------------------------------
	// Event handlers
	//------------------------------------------------------------
	
	public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException
	{
		// no implementation
	}
	
	public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException
	{
		// ツリーが展開されるときに、ノードの情報を最新に更新する
		DefaultFileTreeNode node = (DefaultFileTreeNode)event.getPath().getLastPathComponent();
		((FileTree)event.getSource()).getModel().refreshNode(node);
	}

	/**
	 * ツリーコンポーネントのノード選択状態が変更されたときに呼び出されるハンドラ
	 */
	public void valueChanged(TreeSelectionEvent tse) {
		/*--- for test codes ---*
		// check Event
		ModuleTree _eventTree = (ModuleTree)tse.getSource();
		TreeSelectionModel _selModel = _eventTree.getSelectionModel();
		_selModel.getLeadSelectionPath();
		TreePath[] _eventPaths = tse.getPaths();
		TreePath _anchorPath  = _eventTree.getAnchorSelectionPath();
		TreePath _newLeadPath = tse.getNewLeadSelectionPath();
		TreePath _oldLeadPath = tse.getOldLeadSelectionPath();
		StringBuilder sb = new StringBuilder();
		sb.append("[Debug] ModuleTree#valueChanged(TreeSelectionEvent)");
		sb.append("\n  <TreeSelectionEvent> path count : " + _eventPaths.length);
		if (_anchorPath != null) {
			sb.append("\n  Anchor   <row:" + _eventTree.getRowForPath(_anchorPath) + "> ");
			sb.append(_anchorPath);
		} else {
			sb.append("\n  Anchor nothing.");
		}
		if (_oldLeadPath != null) {
			sb.append("\n  Old Lead <row:" + _eventTree.getRowForPath(_oldLeadPath) + "> ");
			sb.append(_oldLeadPath);
		} else {
			sb.append("\n  Old Lead nothing.");
		}
		if (_newLeadPath != null) {
			sb.append("\n  New Lead <row:" + _eventTree.getRowForPath(_newLeadPath) + "> ");
			sb.append(_newLeadPath);
		} else {
			sb.append("\n  New Lead nothing.");
		}
		for (int i = 0; i < _eventPaths.length; i++) {
			int row = _eventTree.getRowForPath(_eventPaths[i]);
			sb.append("\n  [" + i + "] <row:" + row + "> ");
			if (tse.isAddedPath(i)) {
				sb.append("(add) ");
			} else {
				sb.append("(remove) ");
			}
			if (_eventPaths[i].equals(_anchorPath)) {
				sb.append("(Anchor) ");
			}
			if (_eventPaths[i].equals(_oldLeadPath)) {
				sb.append("(Old Lead) ");
			}
			if (_eventPaths[i].equals(_newLeadPath)) {
				sb.append("(New Lead) ");
			}
			sb.append(_eventPaths[i]);
		}
		int eventTreeLeadRow = _eventTree.getSelectionModel().getLeadSelectionRow();
		TreePath eventTreeLeadPath = _eventTree.getSelectionModel().getLeadSelectionPath();
		sb.append("\n  <Event source Tree info> selection count : " + _eventTree.getSelectionCount());
		sb.append("\n      Lead selection <row:" + eventTreeLeadRow + "> ");
		sb.append(eventTreeLeadPath);
		System.err.println(sb.toString());
		/**--- end of test codes ---*/
		
		//SwingUtilities.invokeLater(_hInvoker);

		//DefaultTreeSelectionModel model;
		//((BasicTreeUI)getUI()).

		/**/
		// 選択数の判定
		if (getSelectionModel().getSelectionMode() == TreeSelectionModel.SINGLE_TREE_SELECTION) {
			// 単一選択の場合は調整の必要なし
			onTreeSelectionAdjusted();
			return;
		}
		FileTree srcTree = (FileTree)tse.getSource();
		int numSelection = srcTree.getSelectionCount();
		if (numSelection <= 1) {
			// 選択がない場合や単一選択の場合は、調整の必要なし
			onTreeSelectionAdjusted();
			return;
		}
		
		// 選択パスの取得
		TreePath[] eventPaths = tse.getPaths();
		TreePath anchorPath  = getAnchorSelectionPath();
		TreePath newLeadPath = tse.getNewLeadSelectionPath();
		int numEventPaths = eventPaths.length;
		int numAddedPaths = 0;
		int addedMinIndex = -1;		// 追加された選択の最小インデックス
		int addedMaxIndex = -1;		// 追加された選択の最大インデックス
		int addedAnchorIndex = -1;	// 追加された選択の中で現在のアンカーとなるインデックス
		int addedLeadIndex = -1;	// 追加された選択の中で現在のリードとなるインデックス
		for (int i = 0; i < numEventPaths; i++) {
			if (tse.isAddedPath(i)) {
				++numAddedPaths;
				if ((addedMinIndex < 0) || (i < addedMinIndex)) {
					addedMinIndex = i;
				}
				if (i > addedMaxIndex) {
					addedMaxIndex = i;
				}
				if (eventPaths[i].equals(anchorPath)) {
					addedAnchorIndex = i;
				}
				if (eventPaths[i].equals(newLeadPath)) {
					addedLeadIndex = i;
				}
			}
		}
		if (numAddedPaths == 0) {
			// 追加された選択が一つもない場合は、選択の調整は不要
			onTreeSelectionAdjusted();
			return;
		}

		// 選択状態の調整
		int originIndex;
		if (addedLeadIndex >= 0) {
			// 追加された選択にリードが含まれている場合、そのノードが
			// ユーザーに選択されたものとみなす。ただし、リードとアンカーが
			// 同一の場合は、最小インデックスがユーザーにより選択された
			// ものとみなす。
			originIndex = (addedLeadIndex==addedAnchorIndex ? addedMinIndex : addedLeadIndex);
		} else {
			// 追加された選択にリードが含まれていない場合は、上方向に
			// 選択範囲が拡張されたことを示すため、追加された選択の
			// 最小インデックスが示すパスを基準とする。
			originIndex = addedMinIndex;
		}
		TreeNode originParentNode = ((TreeNode)eventPaths[originIndex].getLastPathComponent()).getParent();
		TreePath[] selectionPaths = srcTree.getSelectionPaths();
		TreeNode parentNode;
		numSelection = selectionPaths.length;
		int numRemoveSelection = numSelection;
		for (int i = 0; i < numSelection; i++) {
			parentNode = ((TreeNode)selectionPaths[i].getLastPathComponent()).getParent();
			if (parentNode == originParentNode) {
				//--- 同じ親ノードを持つものは同一階層なので選択を維持
				--numRemoveSelection;
				selectionPaths[i] = null;
			}
		}
		if (numRemoveSelection > 0) {
			// 削除する選択が存在する場合は、選択を解除
			removeSelectionPaths(selectionPaths);
			//--- この場合、再度選択変更イベントが呼び出されるため、
			//--- 選択確定メソッドの呼び出しは行わない
		} else {
			// 削除する選択が存在しない場合は、選択を確定
			onTreeSelectionAdjusted();
		}
		/**/
	}

	/**
	 * ツリーコンポーネントの選択状態が変更され、その変更が確定された
	 * ときに呼び出される。
	 * <p>
	 * このツリーコンポーネントでは、ツリーノードの複数選択は同一階層のみに
	 * 限定するため、選択状態を調整する。選択変更確定とは、この調整が
	 * 完了した状態を指す。単一選択や、選択が解除される場合は、調整は
	 * 行われずに、このメソッドが即座に呼び出される。
	 */
	protected void onTreeSelectionAdjusted() {
		
	}

	//------------------------------------------------------------
	// File operations
	//------------------------------------------------------------
	
	public TreePath createDirectory(Component parentComponent, TreePath parentDir, String newDirName) {
		DefaultFileTreeModel model = getModel();
		DefaultFileTreeNode ndParent = (DefaultFileTreeNode)parentDir.getLastPathComponent();
		if (!ndParent.isNodeAncestor(getModel().getRoot()))
			throw new IllegalArgumentException("last node of 'sourcePath' is not descendant of current root.");
		DefaultFileTreeNode ndNewDir = null;
		//--- create
		try {
			ndNewDir = model.createDirectory(ndParent, newDirName);
		} catch (VirtualFileOperationException ex) {
			AppLogger.error(ex);
			String errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_CREATE, ex, "'" + String.valueOf(newDirName) + "'");
			Application.showErrorMessage(parentComponent, errmsg);
			return null;
		}
		
		TreePath retPath = new TreePath(model.getPathToRoot(ndNewDir));
		setSelectionPath(retPath);
		return retPath;
	}

	/**
	 * <em>sourcePath</em> が示すファイルもしくはディレクトリの名称を <em>newName</em> に
	 * 変更する。このメソッドでは名称の正当性については検証しない。
	 * <em>watchFiles</em> にエントリが存在する場合、そのファイルと移動先との対応を
	 * 保持するマップを返す。
	 * 通知するメッセージがある場合は、<em>ownerFrame</em> を基準として
	 * メッセージボックスを表示する。
	 * @param parentComponent	メッセージボックスのオーナー
	 * @param sourcePath	名前を変更する対象の位置を示すツリーパス
	 * @param newName		新しい名前を表す文字列
	 * @return	<em>watchFiles</em> にエントリが存在する場合は、そのファイルと変更後の抽象パスとの
	 * 			対応を保持するマップを返す。<em>watchFiles</em> が <tt>null</tt> もしくは
	 * 			空の場合は、空のマップを返す。
	 * @throws NullPointerException		<em>sourcePath</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>sourcePath</em> が示すノードがルートのノードの場合、
	 * 										<em>newName</em> が <tt>null</tt> もしくは空文字列の場合
	 */
	public Map<VirtualFile,VirtualFile> renameFile(Component parentComponent, TreePath sourcePath, String newName,
													Collection<? extends VirtualFile> watchFiles)
	{
		if (Strings.isNullOrEmpty(newName))
			throw new IllegalArgumentException("'newName' argument is null or empty.");
		DefaultFileTreeNode node = (DefaultFileTreeNode)sourcePath.getLastPathComponent();
		if (!node.isNodeAncestor(getModel().getRoot()))
			throw new IllegalArgumentException("last node of 'sourcePath' is not descendant of current root.");
		VirtualFile sourceFile = node.getFileObject();
		if (sourceFile == null)
			throw new IllegalArgumentException("last node of 'sourcePath' has no file object.");
		VirtualFile parentFile = sourceFile.getParentFile();
		if (parentFile == null)
			throw new IllegalArgumentException("Source file is root directory.");
		
		HashMap<VirtualFile,VirtualFile> watchMap = new HashMap<VirtualFile, VirtualFile>();
		if (watchFiles != null && !watchFiles.isEmpty()) {
			for (VirtualFile f : watchFiles) {
				watchMap.put(f, null);
			}
		}
		
		// rename
		VirtualFile targetFile = parentFile.getChildFile(newName);
		String errmsg = null;
		try {
			if (!sourceFile.renameTo(targetFile)) {
				errmsg = String.format(CommonMessages.getInstance().msgCouldNotRenameTo,
										sourceFile.getName(), newName);
				AppLogger.error(errmsg);
			}
		}
		catch (SecurityException ex) {
			errmsg = String.format(CommonMessages.getInstance().msgCouldNotRenameTo,
									sourceFile.getName(), newName);
			AppLogger.error(errmsg, ex);
		}
		if (errmsg != null) {
			Application.showErrorMessage(parentComponent, errmsg);
			return watchMap;
		}
		
		// update watch map
		if (!watchMap.isEmpty()) {
			for (VirtualFile from : watchMap.keySet()) {
				if (from.equals(sourceFile)) {
					watchMap.put(from, targetFile);
				}
				else if (from.isDescendingFrom(sourceFile)) {
					String relpath = from.relativePathFrom(sourceFile);
					VirtualFile toFile = targetFile.getChildFile(relpath);
					watchMap.put(from, toFile);
				}
			}
		}
		
		// update tree nodes
		getModel().changeFileObject(node, targetFile);
		getModel().sortChildren((DefaultFileTreeNode)node.getParent());
		//refreshTree(sourcePath);
		setSelectionPath(sourcePath);
		
		// completed
		return watchMap;
	}

	/**
	 * <em>sourcePaths</em> のファイルを <em>targetPath</em> の示す位置に移動する。
	 * <em>watchFiles</em> にエントリが存在する場合、そのファイルと移動先との対応を
	 * 保持するマップを返す。
	 * 通知するメッセージがある場合は、<em>ownerFrame</em> を基準として
	 * メッセージボックスを表示する。
	 * @param parentComponent	メッセージボックスのオーナー
	 * @param sourcePaths	移動するファイルの位置を示すツリーパス
	 * @param targetPath	移動先ディレクトリの位置を示すツリーパス
	 * @param watchFiles	移動先を監視するファイルのコレクション
	 * @return	<em>watchFiles</em> にエントリが存在する場合は、そのファイルと移動先との
	 * 			対応を保持するマップを返す。<em>watchFiles</em> が <tt>null</tt> もしくは
	 * 			空の場合は、空のマップを返す。
	 * @throws NullPointerException	<em>sourcePath</em> もしくは <em>targetPath</em> が <tt>null</tt> の場合
	 */
	public Map<VirtualFile,VirtualFile> moveFiles(Component parentComponent, TreePath[] sourcePaths, TreePath targetPath,
													Collection<? extends VirtualFile> watchFiles)
	{
		if (sourcePaths == null)
			throw new NullPointerException("'sourcePaths' argument is null.");
		if (targetPath == null)
			throw new NullPointerException("'targetPath' argument is null.");
		
		DefaultMoveFileTask task = new DefaultMoveFileTask(watchFiles, sourcePaths, targetPath);
		execFileProgressTask(parentComponent, task);
		
		return task.watchFiles;
	}

	/**
	 * <em>sourceFiles</em> に含まれるすべてのファイルを <em>targetPath</em> の示す位置にコピーする。
	 * コピー先の直下に <em>sourceFiles</em> に含まれるファイルが存在する場合は、名前を変更して
	 * コピーする。
	 * 通知するメッセージがある場合は、<em>ownerFrame</em> を基準として
	 * メッセージボックスを表示する。
	 * @param parentComponent	メッセージボックスのオーナー
	 * @param sourceFiles	コピーするファイルを示す抽象パスの配列
	 * @param targetPath	コピー先となるディレクトリを示すツリーパス
	 * @throws NullPointerException	<em>sourceFiles</em> もしくは <em>targetPath</em> が <tt>null</tt> の場合
	 */
	public void copyFiles(Component parentComponent, VirtualFile[] sourceFiles, TreePath targetPath) {
		if (sourceFiles == null)
			throw new NullPointerException("'sourceFiles' argument is null.");
		if (targetPath == null)
			throw new NullPointerException("'targetPath' argument is null.");

		DefaultCopyFileTask task = new DefaultCopyFileTask(sourceFiles, targetPath);
		execFileProgressTask(parentComponent, task);
	}

	/**
	 * 指定されたツリーパスのファイルもしくはディレクトリを削除する。
	 * 通知するメッセージがある場合は、<em>ownerFrame</em> を基準として
	 * メッセージボックスを表示する。
	 * @param parentComponent	メッセージボックスのオーナー
	 * @param deletePaths	削除するファイルの位置を示すツリーパス
	 * @throws NullPointerException	<em>deletePaths</em> が <tt>null</tt> の場合
	 */
	public void deleteFiles(Component parentComponent, TreePath[] deletePaths) {
		if (deletePaths == null)
			throw new NullPointerException("'deletePaths' argument is null.");
		
		DefaultDeleteFileTask task = new DefaultDeleteFileTask(deletePaths);
		execFileProgressTask(parentComponent, task);
	}

	/**
	 * 指定されたタスクを実行し、タスクの結果に従いツリーを更新する。
	 * このメソッドでは、エラーメッセージの表示まで行う。
	 * @param parentComponent	メッセージボックスのオーナー
	 * @param task				実行するタスク
	 * @throws NullPointerException	<em>task</em> が <tt>null</tt> の場合
	 */
	public void execFileProgressTask(Component parentComponent, FileTreeProgressMonitorTask task)
	{
		task.execute(parentComponent);
		refreshAfterProgressTask(task);
		task.showError(parentComponent);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * コピー、移動、削除など、プログレスモニタータスクの処理後に
	 * ツリー表示を更新する。
	 * @param task	実行したプログレスモニタータスク
	 */
	protected void refreshAfterProgressTask(FileTreeProgressMonitorTask task) {
		DefaultFileTreeModel model = getModel();
		
		// ソースのツリー表示を更新
		for (TreePath sPath : task._sources.values()) {
			if (sPath != null) {
				DefaultFileTreeNode node = (DefaultFileTreeNode)sPath.getLastPathComponent();
				if (node.exists()) {
					refreshTree(sPath);
				} else {
					model.removeNodeFromParent(node);
				}
			}
		}
		
		// ターゲットのツリー表示を更新
		if (task._targetPath != null) {
			refreshTree(task._targetPath);
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	/**
	 * このビューのメインコンポーネントとなるツリーコンポーネントに
	 * フォーカスを設定するアクションクラス。
	 */
	protected class FocusRequester implements Runnable {
		public void run() {
			if (!requestFocusInWindow()) {
				requestFocus();
			}
		}
	}
	
	/**
	 * ファイルコピー状況を表示するプログレスダイアログとファイルコピータスクのセット
	 */
	static public class DefaultCopyFileTask extends FileTreeProgressMonitorTask
	{
		public DefaultCopyFileTask(VirtualFile[] sourceFiles, TreePath targetPath) {
			super(CommonMessages.getInstance().Progress_CopyTo, sourceFiles, targetPath);
		}

		@Override
		public void processTask() {
			AppLogger.info("Start " + this.getClass().getName() + " task...");
			
			super.processTask();
			
			VirtualFile targetDir = ((DefaultFileTreeNode)_targetPath.getLastPathComponent()).getFileObject();
			
			// ファイルコピー
			resetAllowOverwriteAllFilesFlag();
			for (Map.Entry<VirtualFile, TreePath> entry : _sources.entrySet()) {
				// setup current target
				VirtualFile sourceFile = entry.getKey();
				_doingPath = entry.getValue();
				if (_doingPath == null) {
					_doingNode = null;
				} else {
					_doingNode = (DefaultFileTreeNode)_doingPath.getLastPathComponent();
				}
				// do copy
				copyRecursive(sourceFile, targetDir);
				// save completed target
				_completed.put(sourceFile, _doingPath);
			}
			_doingPath = null;	// finished
			_doingNode = null;	// finished
			
			AppLogger.info(this.getClass().getName() + " task Completed!");
		}
	}
	
	/**
	 * ファイル移動状況を表示するプログレスダイアログとファイル移動するタスクのセット
	 */
	static public class DefaultMoveFileTask extends FileTreeProgressMonitorTask
	{
		private final HashMap<VirtualFile,VirtualFile> watchFiles = new HashMap<VirtualFile,VirtualFile>();
		
		public DefaultMoveFileTask(Collection<? extends VirtualFile> watches, TreePath[] sourcePaths, TreePath targetPath) {
			super(CommonMessages.getInstance().Progress_MoveTo, sourcePaths, targetPath);
			if (watches != null && !watches.isEmpty()) {
				for (VirtualFile f : watches) {
					watchFiles.put(f, null);
				}
			}
		}
		
		protected void updateWatchFiles(VirtualFile sourceFile, VirtualFile targetDir) {
			VirtualFile destFile = targetDir.getChildFile(sourceFile.getName());
			for (VirtualFile wvf : watchFiles.keySet()) {
				if (wvf.equals(sourceFile)) {
					watchFiles.put(wvf, destFile);
				}
				else if (wvf.isDescendingFrom(sourceFile)) {
					VirtualFile rf = wvf.relativeFileFrom(sourceFile);
					if (!rf.isAbsolute()) {
						watchFiles.put(wvf, destFile.getChildFile(rf.getPath()));
					}
				}
			}
		}

		@Override
		public void processTask() {
			AppLogger.info("Start " + this.getClass().getName() + " task...");
			
			super.processTask();
			
			VirtualFile targetDir = ((DefaultFileTreeNode)_targetPath.getLastPathComponent()).getFileObject();
			
			// ファイル移動
			resetAllowOverwriteAllFilesFlag();
			for (Map.Entry<VirtualFile, TreePath> entry : _sources.entrySet()) {
				// setup current target
				VirtualFile sourceFile = entry.getKey();
				_doingPath = entry.getValue();
				if (_doingPath == null) {
					_doingNode = null;
				} else {
					_doingNode = (DefaultFileTreeNode)_doingPath.getLastPathComponent();
				}
				// do move
				try {
					moveRecursive(sourceFile, targetDir);
				}
				finally {
					if (!watchFiles.isEmpty()) {
						try {
							updateWatchFiles(sourceFile, targetDir);
						} catch (Throwable ignoreEx) {ignoreEx = null;}
					}
				}
				// save completed target
				_completed.put(sourceFile, _doingPath);
			}
			_doingPath = null;	// finished
			_doingNode = null;	// finished
			
			AppLogger.info(this.getClass().getName() + " task Completed!");
		}
	}
	
	/**
	 * ファイル削除状況を表示するプログレスダイアログとファイル削除するタスクのセット
	 */
	static public class DefaultDeleteFileTask extends FileTreeProgressMonitorTask
	{
		public DefaultDeleteFileTask(TreePath[] sourcePaths) {
			super(CommonMessages.getInstance().Progress_Delete, sourcePaths, null);
		}

		@Override
		public void processTask() {
			AppLogger.info("Start " + this.getClass().getName() + " task...");
			
			super.processTask();
			
			// ファイル削除
			for (Map.Entry<VirtualFile, TreePath> entry : _sources.entrySet()) {
				// setup current target
				VirtualFile sourceFile = entry.getKey();
				_doingPath = entry.getValue();
				if (_doingPath == null) {
					_doingNode = null;
				} else {
					_doingNode = (DefaultFileTreeNode)_doingPath.getLastPathComponent();
				}
				// do delete
				deleteRecursive(sourceFile);
				// save completed target
				_completed.put(sourceFile, _doingPath);
			}
			_doingPath = null;	// finished
			_doingNode = null;	// finished
			
			AppLogger.info(this.getClass().getName() + " task Completed!");
		}
	}

	/**
	 * ファイル処理状況を表示するプログレスダイアログと実行するタスクのセット
	 */
	static public abstract class FileTreeProgressMonitorTask extends VirtualFileProgressMonitorTask
	{
		/** このタスクで処理する予定のファイルとツリーパスのマップ **/
		protected final Map<VirtualFile, TreePath> _sources;
		/** このタスクで処理が完了したファイルとツリーパスのマップ **/
		protected final Map<VirtualFile, TreePath> _completed;
		/** 処理の宛先となる位置を示すツリーパス **/
		protected final TreePath		_targetPath;
		/** 処理中のノードを示すツリーパス **/
		protected TreePath				_doingPath;
		/** 処理中のノード **/
		protected DefaultFileTreeNode	_doingNode;
		
		public FileTreeProgressMonitorTask(String title, VirtualFile[] sourceFiles, TreePath targetPath) {
			super(title, null, null, 0, 0, 0);
			this._targetPath = targetPath;
			this._completed = new TreeMap<VirtualFile,TreePath>();
			this._sources = new TreeMap<VirtualFile,TreePath>();
			//--- setup source files
			for (VirtualFile sf : sourceFiles) {
				_sources.put(sf, null);
			}
			this._doingPath = null;
			this._doingNode = null;
		}
		
		public FileTreeProgressMonitorTask(String title, TreePath[] sourcePaths, TreePath targetPath) {
			super(title, null, null, 0, 0, 0);
			this._targetPath = targetPath;
			this._completed = new TreeMap<VirtualFile,TreePath>();
			this._sources = new TreeMap<VirtualFile,TreePath>();
			//--- setup source paths
			for (TreePath tp : sourcePaths) {
				DefaultFileTreeNode node = (DefaultFileTreeNode)tp.getLastPathComponent();
				VirtualFile targetFile = node.getFileObject();
				_sources.put(targetFile, tp);
			}
			this._doingPath = null;
			this._doingNode = null;
		}
		
		@Override
		public void processTask()
		{
			// 処理対象のファイル総数をカウント
			int numFiles = 0;
			for (VirtualFile f : _sources.keySet()) {
				numFiles += f.countFiles();
			}
			if (AppLogger.isInfoEnabled()) {
				AppLogger.info("Start " + getClass().getName() + " task for " + numFiles + " files.");
			}
			
			// 総数をプログレスバーに設定
			setMaximum(numFiles);
		}
	}
}
