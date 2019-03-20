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
 *  Copyright 2007-2015  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)MExecDefTree.java	3.2.1	2015/07/08
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecDefTree.java	2.0.0	2012/11/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecDefTree.java	1.22	2012/07/12
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecDefTree.java	1.20	2012/03/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecDefTree.java	1.10	2011/02/15
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecDefTree.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing.tree;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetDragEvent;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.Icon;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
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
import ssac.falconseed.module.MExecDefFileTransferable;
import ssac.falconseed.module.swing.MExecDefFileProgressMonitorTask;
import ssac.falconseed.runner.RunnerResources;
import ssac.util.Strings;
import ssac.util.io.VirtualFile;
import ssac.util.io.VirtualFileOperationException;
import ssac.util.logging.AppLogger;
import ssac.util.swing.Application;
import ssac.util.swing.FrameWindow;
import ssac.util.swing.tree.DnDTree;
import ssac.util.swing.tree.IDnDTreeHandler;

/**
 * モジュール実行定義を表示するツリーコンポーネント。
 * 
 * @version 3.2.1
 */
public class MExecDefTree extends DnDTree implements TreeWillExpandListener, TreeSelectionListener
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final FocusRequester _hFocusRequester = new FocusRequester();

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MExecDefTree() {
		this(null, null);
	}
	
	public MExecDefTree(MExecDefTreeModel newModel) {
		this(newModel, null);
	}
	
	public MExecDefTree(MExecDefTreeModel newModel, IDnDTreeHandler newHandler) {
		super(newModel==null ? new MExecDefTreeModel() : newModel);
		setTreeHandler(newHandler);
		
		setRootVisible(false);		// ルートノードは表示しない
		setShowsRootHandles(true);	// 最上位にハンドルを表示する
		setTooltipEnabled(true);	// ツールチップを表示する

		addTreeSelectionListener(this);
		addTreeWillExpandListener(this);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このコンポーネントに設定されているモデルを取得する。
	 * このツリーコンポーネントのモデルは、{@link MExecDefTreeModel} クラスの
	 * インスタンスに限定される。
	 * @return	このモデルに設定されている <code>MExecDefTreeModel</code> オブジェクト
	 */
	@Override
	public MExecDefTreeModel getModel() {
		return (MExecDefTreeModel)super.getModel();
	}

	@Override
	public String getToolTipText(Object value, boolean selected, boolean expanded,
									boolean leaf, int row, boolean hasFocus)
	{
		if (value == getModel().getUserRootNode()) {
			// 選択されたノードがユーザールートの場合のみ、パスを表示する
			return ((MExecDefTreeNode)value).getFileObject().getAbsolutePath();
		} else {
			return null;
		}
	}
	
	public String getTreeNodeFileProperty(TreePath treepath) {
		if (treepath != null) {
			return getTreeNodeFileProperty((MExecDefTreeNode)treepath.getLastPathComponent());
		} else {
			return null;
		}
	}
	
	public String getTreeNodeFileProperty(MExecDefTreeNode node) {
		String strProperty = null;
		if (node == getModel().getUserRootNode() || node == getModel().getSystemRootNode()) {
			// システムルートディレクトリもしくはユーザールートディレクトリなら，絶対パスを表示
			String dirprop = getTreeNodeFileInfo(node);
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			sb.append(node.getFilePath());
			sb.append("] ");
			if (dirprop != null)
				sb.append(dirprop);
			strProperty = sb.toString();
		}
		else {
			strProperty = getTreeNodeFileInfo(node);
			if (strProperty != null) {
				StringBuilder sb = new StringBuilder();
				sb.append("[");
				sb.append(node.getDisplayName());
				sb.append("] ");
				sb.append(strProperty);
				strProperty = sb.toString();
			}
		}
		return strProperty;
	}
	
	public String getTreeNodeFileInfo(MExecDefTreeNode node) {
		if (node == null || node.getFileObject() == null) {
			return null;
		}

		// 最終更新日を表示する
		long lastmodified;
		if (node.isExecDefData()) {
			lastmodified = ((MExecDefDataFile)node.getUserObject()).getDataFile().lastModified();
		}
		else {
			lastmodified = node.lastModified();
		}
		
		// フォーマット
		DateFormat frmDate = DateFormat.getDateTimeInstance();
		Date dtModified = new Date(lastmodified);
		return frmDate.format(dtModified);
	}

	/**
	 * このコンポーネントに指定されたツリーモデルを設定する。
	 * 設定可能なツリーモデルは、{@link MExecDefTreeModel} クラスのインスタンスに
	 * 限定される。
	 * @param newModel	設定する <code>MExecDefTreeModel</code> オブジェクト
	 * @throws NullPointerException	<em>newModel</em> が <tt>null</tt> の場合
	 * @throws ClassCastException	<em>newModel</em> が <code>MExecDefTreeModel</code> の
	 * 								インスタンスではない場合
	 */
	@Override
	public void setModel(TreeModel newModel) {
		if (newModel == null)
			throw new NullPointerException("newModel argument is null.");
		super.setModel((MExecDefTreeModel)newModel);
	}

	/**
	 * 現在のシステムルートとなるディレクトリの抽象パスを返す。
	 * @return	システムルートディレクトリの抽象パス。設定されていない場合は <tt>null</tt>
	 */
	public VirtualFile getSystemRootDirectory() {
		return getModel().getSystemRootDirectory();
	}

	/**
	 * 現在のユーザールートとなるディレクトリの抽象パスを返す。
	 * @return	ユーザールートディレクトリの抽象パス。設定されていない場合は <tt>null</tt>
	 */
	public VirtualFile getUserRootDirectory() {
		return getModel().getUserRootDirectory();
	}

	/**
	 * このコンポーネントにフォーカスを設定する。
	 */
	public void setFocus() {
		if (!hasFocus()) {
			SwingUtilities.invokeLater(_hFocusRequester);
		}
	}
	
	/**
	 * このビューを格納するフレームウィンドウを取得する。
	 * @return	このビューを格納するフレームウィンドウのインスタンス。
	 * 			このビューがフレームに格納されていない場合は <tt>null</tt> を返す。
	 */
	public FrameWindow getFrame() {
		Window parentFrame = SwingUtilities.windowForComponent(this);
		if (parentFrame instanceof FrameWindow)
			return (FrameWindow)parentFrame;
		else
			return null;
	}
	
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
	 * @return	選択されているノードの一つからそのノードに関連付けられている
	 * 			抽象パスを返す。選択されていない場合は <tt>null</tt> を返す。
	 */
	public VirtualFile getSelectionFile() {
		TreePath path = getSelectionPath();
		if (path == null) {
			return null;
		} else {
			return ((MExecDefTreeNode)path.getLastPathComponent()).getFileObject();
		}
	}

	/**
	 * 現在選択されているすべてのノードに関連付けられている抽象パスを取得する。
	 * このメソッドが返す抽象パスは、{@link javax.swing.JTree#getSelectionPaths()} メソッドが
	 * 返すノードのものとなる。
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
				files[i] = ((MExecDefTreeNode)paths[i].getLastPathComponent()).getFileObject();
			}
			return files;
		}
	}

	/**
	 * 現在選択されている一つのノードに関連付けられているファイルモデルを取得する。
	 * このメソッドが返すオブジェクトは、{@link javax.swing.JTree#getSelectionPath()} メソッドが
	 * 返すノードのものとなる。
	 * @return	選択されているノードの一つからそのノードに関連付けられている
	 * 			ファイルモデルを返す。選択されていない場合は <tt>null</tt> を返す。
	 * @since 1.10
	 */
	public IMExecDefFile getSelectionNodeData() {
		TreePath path = getSelectionPath();
		if (path == null) {
			return null;
		} else {
			return ((MExecDefTreeNode)path.getLastPathComponent()).getUserObject();
		}
	}
	
	/**
	 * 現在選択されているすべてのノードに関連付けられているファイルモデルを取得する。
	 * このメソッドが返すオブジェクトは、{@link javax.swing.JTree#getSelectionPaths()} メソッドが
	 * 返すノードのものとなる。
	 * @return	選択されているすべてのノードに関連付けられているファイルモデルの配列を返す。
	 * 			選択されていない場合は <tt>null</tt> を返す。
	 * @since 1.10
	 */
	public IMExecDefFile[] getSelectionNodeDatas() {
		TreePath[] paths = getSelectionPaths();
		if (paths == null) {
			return null;
		} else {
			IMExecDefFile[] datas = new IMExecDefFile[paths.length];
			for (int i = 0; i < paths.length; i++) {
				datas[i] = ((MExecDefTreeNode)paths[i].getLastPathComponent()).getUserObject();
			}
			return datas;
		}
	}

	/**
	 * 現在選択されているノードから、最初のモジュール実行定義設定ファイルを取得する。
	 * モジュール実行定義が選択されていない場合、このメソッドは <tt>null</tt> を返す。
	 * @return	選択されている最初のモジュール実行定義設定ファイルの抽象パスを返す。
	 * 			モジュール実行定義が選択されていない場合は <tt>null</tt> を返す。
	 */
	public VirtualFile getSelectionMExecDefPrefsFile() {
		VirtualFile vfPrefs = null;
		TreePath[] paths = getSelectionPaths();
		if (paths != null) {
			for (TreePath tp : paths) {
				MExecDefTreeNode node = (MExecDefTreeNode)tp.getLastPathComponent();
				if (node.isExecDefData()) {
					vfPrefs = ((MExecDefDataFile)node.getUserObject()).getDataFile();
					break;
				}
			}
		}
		return vfPrefs;
	}

	/**
	 * 現在選択されているノードから、すべてのモジュール実行定義設定ファイルを取得する。
	 * モジュール実行定義が一つも選択されていない場合、このメソッドは <tt>null</tt> を返す。
	 * @return	選択されているすべてのモジュール実行定義設定ファイルの抽象パスを格納する配列を返す。
	 * 			モジュール実行定義が一つも選択されていない場合は <tt>null</tt> を返す。
	 */
	public VirtualFile[] getSelectionMExecDefPrefsFiles() {
		VirtualFile[] vfPrefsArray = null;
		TreePath[] paths = getSelectionPaths();
		if (paths != null) {
			ArrayList<VirtualFile> filelist = new ArrayList<VirtualFile>();
			for (TreePath tp : paths) {
				MExecDefTreeNode node = (MExecDefTreeNode)tp.getLastPathComponent();
				if (node.isExecDefData()) {
					filelist.add( ((MExecDefDataFile)node.getUserObject()).getDataFile() );
				}
			}
			if (!filelist.isEmpty()) {
				vfPrefsArray = filelist.toArray(new VirtualFile[filelist.size()]);
			}
		}
		return vfPrefsArray;
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
	 * @throws ClassCastException		<em>path</em> の終端ノードが {@link MExecDefTreeNode} の
	 * 									インスタンスではない場合
	 */
	public void refreshTree(TreePath path) {
		MExecDefTreeNode node = (MExecDefTreeNode)path.getLastPathComponent();
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
		if (value instanceof MExecDefTreeNode) {
			MExecDefTreeNode node = (MExecDefTreeNode)value;
			if (node.exists()) {
				// 存在するファイル
				if (node.isRoot()) {
					// root node icon
					return CommonResources.ICON_DISKUNIT;
				} else if (node.isDirectory()) {
					if (expanded) {
						return CommonResources.ICON_DIR_OPEN;
					} else {
						return CommonResources.ICON_DIR_CLOSE;
					}
				} else if (node.isExecDefGenericFilter()) {
					//--- 汎用フィルタ定義のルートディレクトリ
					return RunnerResources.ICON_GENERICFILTER;
				} else if (node.isExecDefFilterMacro()) {
					//--- フィルタマクロ定義のルートディレクトリ
					return RunnerResources.ICON_MACROFILTER;
				} else if (node.isExecDefData()) {
					//--- モジュール実行定義のルートディレクトリ
					return RunnerResources.ICON_FILTER;
				} else {
					return CommonResources.ICON_FILE;
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
		else if (value instanceof MExecDefTreeNode) {
			return ((MExecDefTreeNode)value).getDisplayName();
		}
		else {
			return value.toString();
		}
	}

	//------------------------------------------------------------
	// File operations
	//------------------------------------------------------------
	
	public TreePath createDirectory(Component parentComponent, TreePath parentDir, String newDirName) {
		MExecDefTreeModel model = getModel();
		MExecDefTreeNode ndParent = (MExecDefTreeNode)parentDir.getLastPathComponent();
		MExecDefTreeNode ndNewDir = null;
		//--- check
		if (!ndParent.isNodeAncestor(getModel().getUserRootNode())) {
			throw new IllegalArgumentException("This parent node is delived from User root directory.");
		}
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
	
	public TreePath createMExecDef(Component parentComponent, TreePath parentDir, String newName) {
		MExecDefTreeModel model = getModel();
		MExecDefTreeNode ndParent = (MExecDefTreeNode)parentDir.getLastPathComponent();
		MExecDefTreeNode ndNewDir = null;
		//--- check
		if (!ndParent.isNodeAncestor(getModel().getUserRootNode())) {
			throw new IllegalArgumentException("This parent node is delived from User root directory.");
		}
		//--- create
		try {
			ndNewDir = model.createMExecDef(ndParent, newName);
		} catch (VirtualFileOperationException ex) {
			AppLogger.error(ex);
			String errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_CREATE, ex, "'" + String.valueOf(newName) + "'");
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
	 * @param ownerFrame	メッセージボックスのオーナーフレーム
	 * @param sourcePath	名前を変更する対象の位置を示すツリーパス
	 * @param newName		新しい名前を表す文字列
	 * @return	<em>watchFiles</em> にエントリが存在する場合は、そのファイルと変更後の抽象パスとの
	 * 			対応を保持するマップを返す。<em>watchFiles</em> が <tt>null</tt> もしくは
	 * 			空の場合は、空のマップを返す。
	 * @throws NullPointerException		<em>sourcePath</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>sourcePath</em> が示すノードがルートのノードの場合、
	 * 										<em>newName</em> が <tt>null</tt> もしくは空文字列の場合
	 */
	public Map<VirtualFile,VirtualFile> renameFile(FrameWindow ownerFrame, TreePath sourcePath, String newName,
													Collection<? extends VirtualFile> watchFiles)
	{
		if (sourcePath == null)
			throw new NullPointerException("'sourcePath' argument is null.");
		if (sourcePath.getPathCount() < 2)
			throw new IllegalArgumentException("'sourcePath' argument is root node.");
		if (Strings.isNullOrEmpty(newName))
			throw new IllegalArgumentException("'newName' argument is null or empty.");
		
		HashMap<VirtualFile,VirtualFile> watchMap = new HashMap<VirtualFile, VirtualFile>();
		if (watchFiles != null && !watchFiles.isEmpty()) {
			for (VirtualFile f : watchFiles) {
				watchMap.put(f, null);
			}
		}
		
		// rename
		MExecDefTreeNode node = (MExecDefTreeNode)sourcePath.getLastPathComponent();
		VirtualFile sourceFile = node.getFileObject();
		VirtualFile targetFile = sourceFile.getParentFile().getChildFile(newName);
		VirtualFile sourcePrefsFile = null;
		VirtualFile targetPrefsFile = null;
		if (sourceFile.isFile() && !Strings.endsWithIgnoreCase(sourceFile.getName(), ModuleFileManager.EXT_FILE_PREFS)) {
			// ターゲットが設定ファイルではないファイルの場合、
			// 同名の設定ファイルも自動的に処理対象とする
			// *.aadl.pref, *.jar.prefs, *.amf.prefs
			VirtualFile fPrefs = ModuleFileManager.getPrefsFile(sourceFile);
			if (fPrefs.exists() && fPrefs.isFile()) {
				sourcePrefsFile = fPrefs;
				targetPrefsFile = ModuleFileManager.getPrefsFile(targetFile);
			}
		}
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
			Application.showErrorMessage(ownerFrame, errmsg);
			return watchMap;
		}
		
		// rename with prefs
		if (sourcePrefsFile != null) {
			errmsg = null;
			try {
				if (!sourcePrefsFile.renameTo(targetPrefsFile)) {
					errmsg = String.format(CommonMessages.getInstance().msgCouldNotRenameTo,
											sourcePrefsFile.getName(), targetPrefsFile.getName());
					AppLogger.warn(errmsg);
				}
				else {
					// success
					if (watchMap.containsKey(sourcePrefsFile)) {
						watchMap.put(sourcePrefsFile, targetPrefsFile);
					}
				}
			}
			catch (SecurityException ex) {
				errmsg = String.format(CommonMessages.getInstance().msgCouldNotRenameTo,
										sourcePrefsFile.getName(), targetPrefsFile.getName());
				AppLogger.warn(errmsg, ex);
			}
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
		getModel().sortChildren((MExecDefTreeNode)node.getParent());
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
	 * @param ownerFrame	メッセージボックスのオーナーフレーム
	 * @param sourcePaths	移動するファイルの位置を示すツリーパス
	 * @param targetPath	移動先ディレクトリの位置を示すツリーパス
	 * @param watchFiles	移動先を監視するファイルのコレクション
	 * @return	<em>watchFiles</em> にエントリが存在する場合は、そのファイルと移動先との
	 * 			対応を保持するマップを返す。<em>watchFiles</em> が <tt>null</tt> もしくは
	 * 			空の場合は、空のマップを返す。
	 * @throws NullPointerException	<em>sourcePath</em> もしくは <em>targetPath</em> が <tt>null</tt> の場合
	 */
	public Map<VirtualFile,VirtualFile> moveFiles(FrameWindow ownerFrame, TreePath[] sourcePaths, TreePath targetPath,
													Collection<? extends VirtualFile> watchFiles)
	{
		if (sourcePaths == null)
			throw new NullPointerException("'sourcePaths' argument is null.");
		if (targetPath == null)
			throw new NullPointerException("'targetPath' argument is null.");
		
		MoveProgressMonitor task = new MoveProgressMonitor(watchFiles, sourcePaths, targetPath);
		task.execute(ownerFrame);
		refreshAfterProgressTask(task);
		showErrorForProgressTask(ownerFrame, task);
		
		return task.watchFiles;
	}

	/**
	 * <em>sourceFiles</em> に含まれるすべてのファイルを <em>targetPath</em> の示す位置にコピーする。
	 * コピー先の直下に <em>sourceFiles</em> に含まれるファイルが存在する場合は、名前を変更して
	 * コピーする。
	 * 通知するメッセージがある場合は、<em>ownerFrame</em> を基準として
	 * メッセージボックスを表示する。
	 * @param ownerFrame	メッセージボックスのオーナーフレーム
	 * @param sourceFiles	コピーするファイルを示す抽象パスの配列
	 * @param targetPath	コピー先となるディレクトリを示すツリーパス
	 * @throws NullPointerException	<em>sourceFiles</em> もしくは <em>targetPath</em> が <tt>null</tt> の場合
	 */
	public void copyFiles(FrameWindow ownerFrame, VirtualFile[] sourceFiles, TreePath targetPath) {
		if (sourceFiles == null)
			throw new NullPointerException("'sourceFiles' argument is null.");
		if (targetPath == null)
			throw new NullPointerException("'targetPath' argument is null.");

		CopyProgressMonitor task = new CopyProgressMonitor(sourceFiles, targetPath);
		task.execute(ownerFrame);
		refreshAfterProgressTask(task);
		showErrorForProgressTask(ownerFrame, task);
	}

	/**
	 * 指定されたツリーパスのファイルもしくはディレクトリを削除する。
	 * 通知するメッセージがある場合は、<em>ownerFrame</em> を基準として
	 * メッセージボックスを表示する。
	 * @param ownerFrame	メッセージボックスのオーナーフレーム
	 * @param deletePaths	削除するファイルの位置を示すツリーパス
	 * @throws NullPointerException	<em>deletePaths</em> が <tt>null</tt> の場合
	 */
	public void deleteFiles(FrameWindow ownerFrame, TreePath[] deletePaths) {
		if (deletePaths == null)
			throw new NullPointerException("'deletePaths' argument is null.");
		
		DeleteProgressMonitor task = new DeleteProgressMonitor(deletePaths);
		task.execute(ownerFrame);
		refreshAfterProgressTask(task);
		showErrorForProgressTask(ownerFrame, task);
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
		MExecDefTreeNode node = (MExecDefTreeNode)event.getPath().getLastPathComponent();
		((MExecDefTree)event.getSource()).getModel().refreshNode(node);
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
		MExecDefTree srcTree = (MExecDefTree)tse.getSource();
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
	// Internal methods
	//------------------------------------------------------------

	/**
	 * コピー、移動、削除など、プログレスモニタータスクの処理後に
	 * ツリー表示を更新する。
	 * @param task	実行したプログレスモニタータスク
	 */
	protected void refreshAfterProgressTask(FileProgressMonitor task) {
		MExecDefTreeModel model = getModel();
		
		// ソースのツリー表示を更新
		for (TreePath sPath : task._sources.values()) {
			if (sPath != null) {
				MExecDefTreeNode node = (MExecDefTreeNode)sPath.getLastPathComponent();
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

	/**
	 * コピー、移動、削除など、プログレスモニタータスクの処理中に発生した
	 * エラーに関するメッセージを表示する。
	 * エラーが発生していない場合は、処理しない。
	 * 
	 * @param owner	メッセージボックスのオーナー
	 * @param task	実行したプログレスモニタータスク
	 */
	protected void showErrorForProgressTask(Frame owner, FileProgressMonitor task) {
		task.showError(owner);
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
	protected class CopyProgressMonitor extends FileProgressMonitor
	{
		public CopyProgressMonitor(VirtualFile[] sourceFiles, TreePath targetPath)
		{
			super(getModel().getSystemRootNode(), getModel().getUserRootNode(), CommonMessages.getInstance().Progress_CopyTo, sourceFiles, targetPath);
//			//--- sourceFiles のすべてが現在のワークスペース以下のファイルの場合のみ、
//			//--- sourceBaseDirectory を現在のワークスペーストする
//			if (sourceFiles != null && sourceFiles.length > 0) {
//				VirtualFile srcSystemDir = getModel().getSystemRootDirectory();
//				VirtualFile srcUserDir   = getModel().getUserRootDirectory();
//				VirtualFile srcBaseDir = null;
//				for (VirtualFile srcFile : sourceFiles) {
//					if (srcFile != null) {
//						if (srcBaseDir != null) {
//							if (!srcFile.isDescendingFrom(srcBaseDir)) {
//								srcBaseDir = null;
//								break;
//							}
//						}
//						else if (srcFile.isDescendingFrom(srcUserDir)) {
//							srcBaseDir = srcUserDir;
//						}
//						else if (srcFile.isDescendingFrom(srcSystemDir)) {
//							srcBaseDir = srcSystemDir;
//						}
//					}
//				}
//				setSourceBaseDirectory(srcBaseDir);
//			} else {
//				setSourceBaseDirectory(null);
//			}
//			setTargetBaseDirectory(((MExecDefTreeNode)targetPath.getPathComponent(1)).getFileObject());
		}

		@Override
		public void processTask() {
			AppLogger.info("Start CopyProgressMonitor task...");
			
			super.processTask();
			
			VirtualFile targetDir = ((MExecDefTreeNode)_targetPath.getLastPathComponent()).getFileObject();
			
			// ファイルコピー
			resetAllowOverwriteAllFilesFlag();
			for (Map.Entry<VirtualFile, TreePath> entry : _sources.entrySet()) {
				// setup current target
				VirtualFile sourceFile = entry.getKey();
				_doingPath = entry.getValue();
				if (_doingPath == null) {
					_doingNode = null;
				} else {
					_doingNode = (MExecDefTreeNode)_doingPath.getLastPathComponent();
				}
				// do copy
				copyRecursive(sourceFile, targetDir);
				// save completed target
				_completed.put(sourceFile, _doingPath);
			}
			_doingPath = null;	// finished
			_doingNode = null;	// finished
			
			AppLogger.info("CopyProgressMonitor task Completed!");
		}
	}
	
	/**
	 * ファイル移動状況を表示するプログレスダイアログとファイル移動するタスクのセット
	 */
	protected class MoveProgressMonitor extends FileProgressMonitor
	{
		private final HashMap<VirtualFile,VirtualFile> watchFiles = new HashMap<VirtualFile,VirtualFile>();
		
		public MoveProgressMonitor(Collection<? extends VirtualFile> watches, TreePath[] sourcePaths, TreePath targetPath)
		{
			super(getModel().getSystemRootNode(), getModel().getUserRootNode(), CommonMessages.getInstance().Progress_MoveTo, sourcePaths, targetPath);
//			setSourceBaseDirectory(((MExecDefTreeNode)sourcePaths[0].getPathComponent(1)).getFileObject());
//			setTargetBaseDirectory(((MExecDefTreeNode)targetPath.getPathComponent(1)).getFileObject());
			if (watches != null && !watches.isEmpty()) {
				for (VirtualFile f : watches) {
					watchFiles.put(f, null);
				}
			}
		}

		@Override
		protected void onFileMoved(VirtualFile source, VirtualFile target) {
			// 監視対象のファイルで移動に成功したものは、
			// 移動先の抽象パスを保存する
			if (watchFiles.containsKey(source)) {
				watchFiles.put(source, target);
			}
		}

		@Override
		public void processTask() {
			AppLogger.info("Start MoveProgressMonitor task...");
			
			super.processTask();
			
			VirtualFile targetDir = ((MExecDefTreeNode)_targetPath.getLastPathComponent()).getFileObject();
			
			// ファイル移動
			resetAllowOverwriteAllFilesFlag();
			for (Map.Entry<VirtualFile, TreePath> entry : _sources.entrySet()) {
				// setup current target
				VirtualFile sourceFile = entry.getKey();
				_doingPath = entry.getValue();
				if (_doingPath == null) {
					_doingNode = null;
				} else {
					_doingNode = (MExecDefTreeNode)_doingPath.getLastPathComponent();
				}
				// do move
				moveRecursive(sourceFile, targetDir);
				// save completed target
				_completed.put(sourceFile, _doingPath);
			}
			_doingPath = null;	// finished
			_doingNode = null;	// finished
			
			AppLogger.info("MoveProgressMonitor task Completed!");
		}
	}
	
	/**
	 * ファイル削除状況を表示するプログレスダイアログとファイル削除するタスクのセット
	 */
	protected class DeleteProgressMonitor extends FileProgressMonitor
	{
		public DeleteProgressMonitor(TreePath[] sourcePaths)
		{
			super(getModel().getSystemRootNode(), getModel().getUserRootNode(), CommonMessages.getInstance().Progress_Delete, sourcePaths, null);
//			setSourceBaseDirectory(((MExecDefTreeNode)sourcePaths[0].getPathComponent(1)).getFileObject());
//			setTargetBaseDirectory(null);
		}

		@Override
		public void processTask() {
			AppLogger.info("Start DeleteProgressMonitor task...");
			
			super.processTask();
			
			// ファイル削除
			for (Map.Entry<VirtualFile, TreePath> entry : _sources.entrySet()) {
				// setup current target
				VirtualFile sourceFile = entry.getKey();
				_doingPath = entry.getValue();
				if (_doingPath == null) {
					_doingNode = null;
				} else {
					_doingNode = (MExecDefTreeNode)_doingPath.getLastPathComponent();
				}
				// do delete
				deleteRecursive(sourceFile);
				// save completed target
				_completed.put(sourceFile, _doingPath);
			}
			_doingPath = null;	// finished
			_doingNode = null;	// finished
			
			AppLogger.info("DeleteProgressMonitor task Completed!");
		}
	}

	/**
	 * ファイル処理状況を表示するプログレスダイアログと実行するタスクのセット
	 */
	static protected abstract class FileProgressMonitor extends MExecDefFileProgressMonitorTask
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
		protected MExecDefTreeNode		_doingNode;
		
		static private final VirtualFile getFileObjectFromNode(final MExecDefTreeNode node) {
			return (node==null ? null : node.getFileObject());
		}
		
		static private final String getDisplayNameFromNode(final MExecDefTreeNode node) {
			return (node==null ? null : node.getDisplayName());
		}
		
		public FileProgressMonitor(MExecDefTreeNode ndSystemRoot, MExecDefTreeNode ndUserRoot,
									String title, VirtualFile[] sourceFiles, TreePath targetPath)
		{
			super(getFileObjectFromNode(ndSystemRoot), getDisplayNameFromNode(ndSystemRoot),
					getFileObjectFromNode(ndUserRoot), getDisplayNameFromNode(ndUserRoot),
					title, null, null, 0, 0, 0);
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
		
		public FileProgressMonitor(MExecDefTreeNode ndSystemRoot, MExecDefTreeNode ndUserRoot,
									String title, TreePath[] sourcePaths, TreePath targetPath)
		{
			super(getFileObjectFromNode(ndSystemRoot), getDisplayNameFromNode(ndSystemRoot),
					getFileObjectFromNode(ndUserRoot), getDisplayNameFromNode(ndUserRoot),
					title, null, null, 0, 0, 0);
			this._targetPath = targetPath;
			this._completed = new TreeMap<VirtualFile,TreePath>();
			this._sources = new TreeMap<VirtualFile,TreePath>();
			//--- setup source paths
			for (TreePath tp : sourcePaths) {
				MExecDefTreeNode node = (MExecDefTreeNode)tp.getLastPathComponent();
				VirtualFile targetFile = node.getFileObject();
				_sources.put(targetFile, tp);
				if (targetFile.isFile() && !Strings.endsWithIgnoreCase(targetFile.getName(), ModuleFileManager.EXT_FILE_PREFS)) {
					// ターゲットが設定ファイルではないファイルの場合、
					// 同名の設定ファイルも自動的に処理対象とする
					// *.aadl.pref, *.jar.prefs, *.amf.prefs
					VirtualFile fPrefs = ModuleFileManager.getPrefsFile(targetFile);
					if (fPrefs.exists() && !_sources.containsKey(fPrefs)) {
						_sources.put(fPrefs, null);
					}
				}
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
				AppLogger.info("Start FileProgressMonitor task for " + numFiles + " files.");
			}
			
			// 総数をプログレスバーに設定
			setMaximum(numFiles);
		}

		@Override
		protected boolean acceptCopy(VirtualFile source) {
			// プロジェクト設定ファイルは除外する
			if (source.isFile() && ModuleFileManager.PROJECT_PREFS_FILENAME.equalsIgnoreCase(source.getName())) {
				return false;
			}
			
			return super.acceptCopy(source);
		}
	}

	/**
	 * ツリーコンポーネントの制御を行う標準のハンドラ
	 */
	static public class DefaultTreeHandler implements IDnDTreeHandler
	{
		public int acceptTreeDragOverDropAction(DnDTree tree, DropTargetDragEvent dtde, TreePath dragOverPath) {
			// ドロップ先ノードを取得
			if (dragOverPath == null) {
				// ドロップ先ノードが存在しない場合は、無効
				return TransferHandler.NONE;
			}
			
			// データソースがこの同じコンポーネント上のものでなければ、コピーのみ許可する。
			int retDropAction;
			if (tree.isDnDLocalDragging()) {
				retDropAction = dtde.getDropAction();
			} else {
				retDropAction = TransferHandler.COPY;
			}
			
			return retDropAction;
		}

		public boolean canTransferImport(DnDTree tree, DataFlavor[] transferFlavors) {
			// サポートされているデータ形式なら、インポートを許可
			return MExecDefFileTransferable.containsSupportedDataFlavor(transferFlavors);
		}

		public Transferable createTransferable(DnDTree tree) {
			if (tree instanceof MExecDefTree) {
				IMExecDefFile[] datas = ((MExecDefTree)tree).getSelectionNodeDatas();
				if (datas != null && datas.length > 0) {
					// 転送データ生成
					return new MExecDefFileTransferable(datas);
				} else {
					// 操作対象が存在しない
					return null;
				}
			} else {
				return null;
			}
		}

		public int getTransferSourceAction(DnDTree tree) {
			int action;
			TreePath[] paths = tree.getSelectionPaths();
			if (paths != null && paths.length > 0) {
				action = TransferHandler.NONE;
				MExecDefTreeNode ndSystemRoot = ((MExecDefTree)tree).getModel().getSystemRootNode();
				MExecDefTreeNode ndUserRoot = ((MExecDefTree)tree).getModel().getUserRootNode();
				for (TreePath tp : paths) {
					MExecDefTreeNode node = (MExecDefTreeNode)tp.getLastPathComponent();
					if (!node.isRoot()) {
						if (node.isNodeAncestor(ndUserRoot)) {
							if (node != ndUserRoot) {
								// ユーザールート配下は変更可能
								action = TransferHandler.COPY_OR_MOVE;
								break;
							} else {
								// ユーザールートは変更不可
								action = TransferHandler.NONE;
								break;
							}
						}
						else if (node.isNodeAncestor(ndSystemRoot)) {
							if (node != ndSystemRoot) {
								// システムルート配下はコピー可能
								action = TransferHandler.COPY;
								break;
							} else {
								// システムルートは変更不可
								action = TransferHandler.NONE;
								break;
							}
						}
					}
				}
			} else {
				action = TransferHandler.NONE;
			}
			return action;
		}

		public Icon getTransferVisualRepresentation(DnDTree tree, Transferable t) {
			return null;
		}

		public boolean importTransferData(DnDTree tree, Transferable t) {
			// このメソッドは使用しない
			return false;
		}

		public boolean dropTransferData(DnDTree tree, Transferable t, int action) {
			// MExecDefTree インスタンス以外は処理しない
			if (!(tree instanceof MExecDefTree)) {
				return false;
			}
			
			// 転送データを取得
			VirtualFile[] files = MExecDefFileTransferable.getVirtualFilesFromTransferData(t);
			if (files == null || files.length <= 0) {
				return false;	// No data
			}
			
			// 転送先の位置を取得
			TreePath tp = tree.getDragOverPath();
			if (tp == null) {
				return false;	// No target
			}
			
			// 転送
			boolean result;
			if (action == TransferHandler.MOVE) {
				if (tree.isDnDLocalDragging()) {
					result = moveTransferable((MExecDefTree)tree, files, tp);
				} else {
					// 移動は、ローカルのDrag&Dropのみ許可する
					result = false;
				}
			}
			else if (action == TransferHandler.COPY) {
				result = copyTransferable((MExecDefTree)tree, files, tp);
			}
			else {
				// no action
				result = false;
			}
			
			// 完了
			return result;
		}
		
		protected boolean copyTransferable(MExecDefTree tree, VirtualFile[] sourceFiles, TreePath targetPath) {
			return false;
		}
		
		protected boolean moveTransferable(MExecDefTree tree, VirtualFile[] sourceFiles, TreePath targetPath) {
			return false;
		}
	}
}
