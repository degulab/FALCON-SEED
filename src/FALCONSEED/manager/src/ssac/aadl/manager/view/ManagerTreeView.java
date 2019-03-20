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
 * @(#)ManagerTreeView.java	1.14	2009/12/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.manager.view;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashSet;

import javax.swing.Action;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.manager.PackageManager;
import ssac.aadl.manager.menu.ManagerMenuBar;
import ssac.aadl.manager.menu.ManagerMenuResources;
import ssac.aadl.module.ModuleFileManager;
import ssac.aadl.module.ModuleFileTransferable;
import ssac.aadl.module.PackageBaseLocation;
import ssac.aadl.module.swing.FileDialogManager;
import ssac.aadl.module.swing.FilenameValidator;
import ssac.aadl.module.swing.tree.ModuleFileTreeModel;
import ssac.aadl.module.swing.tree.ModuleFileTreeNode;
import ssac.aadl.module.swing.tree.ModuleTree;
import ssac.aadl.module.swing.tree.PackageFileChooser;
import ssac.aadl.module.swing.tree.PackageFileTreeModel;
import ssac.util.Strings;
import ssac.util.io.VirtualFile;
import ssac.util.logging.AppLogger;
import ssac.util.swing.IDialogResult;
import ssac.util.swing.SwingTools;
import ssac.util.swing.menu.IMenuActionHandler;
import ssac.util.swing.tree.DnDTree;

/**
 * モジュールマネージャのツリービュー
 * 
 * @version 1.14	2009/12/14
 * @since 1.14
 */
public class ManagerTreeView extends JPanel implements IMenuActionHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final ManagerTreeHandler	_hTree = new ManagerTreeHandler();

	/** ツリーコンポーネント **/
	private final ModuleTree	_cTree;

	/** パッケージ参照の基準となる位置 **/
	private PackageBaseLocation _curBase;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 標準のコンストラクタ
	 */
	public ManagerTreeView() {
		super(new BorderLayout());
		this._cTree = createTreeComponent();
		initialComponent();
		setupActions();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * ツリーコンポーネントに設定されているツリーモデルを取得する。
	 */
	public PackageFileTreeModel getTreeModel() {
		return (PackageFileTreeModel)_cTree.getModel();
	}

	/**
	 * ツリーコンポーネントに設定されているツリーモデルのルートノードを取得する。
	 * ルートノードが存在しない場合は <tt>null</tt> を返す。
	 */
	public ModuleFileTreeNode getTreeRootNode() {
		return _cTree.getRootNode();
	}

	/**
	 * 現在選択されているパッケージベースの位置情報を返す。
	 */
	public PackageBaseLocation getBaseLocation() {
		return _curBase;
	}

	/**
	 * 新しいパッケージベースの位置を設定する。
	 * @param newLocation	設定する位置情報
	 * @throws NullPointerException	<em>newLocation</em> が <tt>null</tt> の場合
	 */
	public void setBaseLocation(PackageBaseLocation newLocation) {
		// check
		VirtualFile rootFile = newLocation.getFileObject();
		if (!rootFile.exists())
			throw new IllegalArgumentException("Workspace root is not exist : \"" + newLocation.toString() + "\"");
		else if (!rootFile.isDirectory())
			throw new IllegalArgumentException("Workspace root is not directory : \"" + newLocation.toString() + "\"");
		else if (!rootFile.canWrite())
			throw new IllegalArgumentException("Workspace root cannot write : \"" + newLocation.toString() + "\"");

		// create model
		if (_curBase != null) {
			if (!_curBase.equals(newLocation)) {
				_curBase = newLocation;
				PackageFileTreeModel newModel = new PackageFileTreeModel(newLocation.getFileObject());
				newModel.setRootNodeIcon(ModuleFileManager.getSystemDisplayIcon(newLocation.getFileObject()));
				_cTree.setModel(newModel);
			}
		} else {
			_curBase = newLocation;
			PackageFileTreeModel newModel = new PackageFileTreeModel(newLocation.getFileObject());
			newModel.setRootNodeIcon(ModuleFileManager.getSystemDisplayIcon(newLocation.getFileObject()));
			_cTree.setModel(newModel);
		}
	}
	
	public boolean isSelectionEmpty() {
		return _cTree.isSelectionEmpty();
	}
	
	public int getSelectionCount() {
		return _cTree.getSelectionCount();
	}
	
	public ModuleFileTreeNode getSelectionNode() {
		TreePath path = _cTree.getSelectionPath();
		if (path != null) {
			return (ModuleFileTreeNode)path.getLastPathComponent();
		} else {
			return null;
		}
	}
	
	public VirtualFile getSelectionFile() {
		return _cTree.getSelectionFile();
	}
	
	public VirtualFile[] getSelectionFiles() {
		return _cTree.getSelectionFiles();
	}

	/**
	 * このビューを格納するマネージャフレームを取得する。
	 * @return	このビューを格納するマネージャフレームのインスタンス。
	 * 			このビューがフレームに格納されていない場合は <tt>null</tt> を返す。
	 */
	public ManagerFrame getFrame() {
		Window parentFrame = SwingUtilities.windowForComponent(this);
		if (parentFrame instanceof ManagerFrame)
			return (ManagerFrame)parentFrame;
		else
			return null;
	}

	/**
	 * ルートノードを起点に、表示されているすべてのツリーノードを
	 * 最新の情報に更新する。
	 */
	public void refreshAllTree() {
		ModuleFileTreeNode rootNode = _cTree.getRootNode();
		if (rootNode != null) {
			_cTree.refreshTree(new TreePath(rootNode));
		}
	}

	/**
	 * 選択されているノードを起点に、そのノードの下位で表示されている
	 * すべてのツリーノードを最新の情報に更新する。
	 */
	public void refreshSelectedTree() {
		if (_cTree.getSelectionCount() > 0) {
			TreePath[] paths = _cTree.getSelectionPaths();
			for (TreePath path : paths) {
				_cTree.refreshTree(path);
			}
		}
	}

	/**
	 * 指定された抽象パスを持つノードの表示を更新する。
	 * 表示されていないノードの場合や、ツリー階層内の抽象パスではない
	 * 場合は何もしない。
	 * @param targetFile	対象の抽象パス
	 */
	public void refreshFileTree(VirtualFile targetFile) {
		TreePath path = _cTree.getModel().getTreePathForFile(targetFile);
		if (path != null) {
			_cTree.refreshTree(path);
		}
	}

	/**
	 * このビューのメインコンポーネントにフォーカスを設定する。
	 */
	public void requestFocusInComponent() {
		_cTree.setFocus();
	}

	//------------------------------------------------------------
	// File operations
	//------------------------------------------------------------

	/**
	 * フォルダの新規作成を許可する状態であれば <tt>true</tt> を返す。
	 * ファイルやフォルダが新規作成可能な状態は次の通り。
	 * <ul>
	 * <li>単一のノードが選択されている</li>
	 * <li>選択されているノードが参照パッケージ内のファイルやフォルダではない場合</li>
	 * <li>選択されているノードが参照パッケージルートではない場合</li>
	 * </ul>
	 */
	public boolean canCreate() {
		if (_cTree.getSelectionCount() != 1) {
			// 単一選択ではない
			return false;
		}
		
		// ノードのチェック
		TreePath path = _cTree.getSelectionPath();
		ModuleFileTreeNode selected = (ModuleFileTreeNode)path.getLastPathComponent();
		if (selected.isAssignedModulePackageFile())
			return false;
		if (!selected.isProjectRoot() && selected.isModulePackageRoot())
			return false;
		//if (selected.isRoot())
		//	return false;
		
		// allow create
		return true;
	}

	/**
	 * ディレクトリ選択ダイアログを表示し、選択されたディレクトリを返す。
	 * @param title			ディレクトリ選択ダイアログのタイトルとする文字列
	 * @param initParent	初期選択位置とする抽象パス。このパスがディレクトリでは
	 * 						ない場合は、親ディレクトリを初期選択位置とする。
	 * 						ツリーに存在しない抽象パスであれば選択しない。
	 * @return	選択された抽象パスを返す。選択されなかった場合は <tt>null</tt> を返す。
	 */
	public VirtualFile chooseDirectory(String title, VirtualFile initParent) {
		// ダイアログを作成
		String dlgTitle;
		if (Strings.isNullOrEmpty(title)) {
			dlgTitle = CommonMessages.getInstance().Button_Select;
		} else {
			dlgTitle = title;
		}
		String dlgTreeLabel = CommonMessages.getInstance().Choose_folder + ":";
		FilenameValidator validator = FileDialogManager.createDefaultFilenameValidator();
		PackageFileChooser chooser = new PackageFileChooser(getFrame(),
											true,			// root を表示
											false,			// フォルダ作成を許可しない
											_curBase.getFileObject(), initParent,
											dlgTitle, null, dlgTreeLabel, null,
											PackageFileChooser.getDefaultPackageFolderFilter(),
											validator);
		
		// ダイアログを表示
		chooser.setFileSelectionMode(PackageFileChooser.FOLDER_ONLY);
		chooser.setRootSelectionEnable(true);
		chooser.pack();
		chooser.setVisible(true);
		chooser.dispose();
		
		// 結果を取得
		if (chooser.getDialogResult() == IDialogResult.DialogResult_OK) {
			return chooser.getSelectionFile();
		} else {
			return null;	// user canceled!
		}
	}

	/**
	 * ディレクトリを新規に作成するためのディレクトリ名入力ダイアログを表示し、
	 * 入力されたディレクトリ名で新規フォルダを作成する。
	 * @return	正常に作成できた場合は <tt>true</tt>、そうでない場合は <tt>false</tt> を返す
	 */
	public boolean doCreateDirectory() {
		// 作成可能な状態でないなら、処理しない
		if (!canCreate()) {
			return false;
		}
		
		// ディレクトリ作成位置の確認
		TreePath selectedPath = _cTree.getSelectionPath();
		if (selectedPath != null) {
			VirtualFile packfile = _cTree.findAssignedModulePackageFile(true, selectedPath);
			if (packfile != null) {
				// モジュールパッケージは変更不可
				String errmsg = CommonMessages.getInstance().msgCouldNotModifyFilesInPackage;
				AppLogger.error(errmsg + " : \"" + packfile.getPath() + "\"");
				PackageManager.showErrorMessage(getFrame(), errmsg);
				return false;
			}
		}
		
		// ディレクトリ名入力ダイアログの表示
		ManagerFrame frame = getFrame();
		String dlgTitle = CommonMessages.getInstance().InputTitle_NewFolder;
		String dlgTreeLabel = CommonMessages.getInstance().TreeLabel_SelectParentFolder;
		String dlgInputLabel = CommonMessages.getInstance().InputLabel_FolderName;
		VirtualFile initParent = null;
		if (selectedPath != null) {
			initParent = ((ModuleFileTreeNode)selectedPath.getLastPathComponent()).getFileObject();
		}
		FilenameValidator validator = FileDialogManager.createDefaultFilenameValidator();
		PackageFileChooser chooser = new PackageFileChooser(frame, true, false,
															_curBase.getFileObject(), initParent,
															dlgTitle, null, dlgTreeLabel, dlgInputLabel,
															PackageFileChooser.getDefaultPackageFolderFilter(),
															validator);
		chooser.setFileSelectionMode(PackageFileChooser.FOLDER_ONLY);
		chooser.setRootSelectionEnable(true);
		chooser.pack();
		chooser.setVisible(true);
		chooser.dispose();
		if (chooser.getDialogResult() != IDialogResult.DialogResult_OK) {
			// user canceled!
			return false;
		}
		
		// ディレクトリの作成
		String strNewName = chooser.getInputFieldText();
		VirtualFile parentDir = chooser.getSelectionFile();
		TreePath tpParentDir = _cTree.getModel().getTreePathForFile(parentDir);
		if (tpParentDir == null)
			throw new AssertionError("TreeNode not found : \"" + parentDir.toString() + "\"");
		TreePath tpNewDir = _cTree.createDirectory(frame, tpParentDir, strNewName);
		return (tpNewDir != null);
	}

	/**
	 * ツリーノードで選択されているノードの名前変更が可能であれば <tt>true</tt> を返す。
	 * 名前変更可能な条件は次の通り。
	 * <ul>
	 * <li>単一の選択である</li>
	 * <li>パッケージに含まれないファイルもしくはフォルダが選択されている</li>
	 * </ul>
	 */
	public boolean canRename() {
		if (_cTree.getSelectionCount() != 1) {
			// 単一選択以外では名前変更不可
			return false;
		}
		
		// ノードのチェック
		TreePath path = _cTree.getSelectionPath();
		ModuleFileTreeNode node = (ModuleFileTreeNode)path.getLastPathComponent();
		if (node.isRoot())
			return false;
		
		// 参照モジュールパッケージの名前変更は、変更できないことを通知する
		// メッセージを表示するため、ここではスルー
		
		// allow rename
		return true;
	}
	
	/**
	 * 選択されているノードが示すファイル名を変更する。
	 */
	public void doRename() {
		// 操作可能な状態でないなら、処理しない
		if (!canRename()) {
			return;
		}
		ManagerFrame frame = getFrame();
		TreePath selectedPath = _cTree.getSelectionPath();
		
		// モジュールパッケージの確認
		if (selectedPath != null) {
			VirtualFile packfile = _cTree.findAssignedModulePackageFile(false, selectedPath);
			if (packfile != null) {
				// モジュールパッケージは変更不可
				String errmsg = CommonMessages.getInstance().msgCouldNotModifyFilesInPackage;
				AppLogger.error(errmsg + " : \"" + packfile.getPath() + "\"");
				PackageManager.showErrorMessage(getFrame(), errmsg);
				return;
			}
		}
		
		// 名前入力ダイアログの表示
		ModuleFileTreeNode selectedNode = (ModuleFileTreeNode)selectedPath.getLastPathComponent();
		String strNewName = FileDialogManager.showRenameDialog(frame, selectedNode.getFileObject());
		if (Strings.isNullOrEmpty(strNewName)) {
			// user canceled!
			return;
		}
		
		// 名前変更
		_cTree.renameFile(frame, selectedPath, strNewName, null);
	}

	/**
	 * ツリーノードで選択されているノードが移動可能であれば <tt>true</tt> を返す。
	 * 移動可能となる条件は次の通り。
	 * <ul>
	 * <li>プロジェクト以外のファイル、もしくはフォルダが選択されている</li>
	 * <li>パッケージに含まれないファイルもしくはフォルダが選択されている</li>
	 * <li>トップレベルのパッケージフォルダが選択されている<li>
	 * </ul>
	 */
	public boolean canMoveTo() {
		if (_cTree.getSelectionCount() <= 0) {
			// 選択されたノードが存在しない場合は、移動不可
			return false;
		}

		// ノードのチェック
		TreePath[] paths = _cTree.getSelectionPaths();
		for (TreePath path : paths) {
			ModuleFileTreeNode node = (ModuleFileTreeNode)path.getLastPathComponent();
			if (node.isRoot())
				return false;
			if (node.isProjectRoot())
				return false;
			
			// 参照モジュールパッケージに含まれるファイルやフォルダの移動は、
			// 構成変更できないことを通知するメッセージを表示するため、
			// ここではスルー
		}
		
		// allow move
		return true;
	}

	/**
	 * 選択されているノードを、ユーザー指定の場所に移動する。
	 */
	public void doMoveTo() {
		// 操作可能な状態でないなら、処理しない
		if (!canMoveTo()) {
			return;
		}
		ManagerFrame frame = getFrame();
		ModuleFileTreeModel model = _cTree.getModel();
		TreePath[] selectedPaths = _cTree.getSelectionPaths();
		
		// モジュールパッケージの確認
		if (selectedPaths != null) {
			VirtualFile packfile = _cTree.findAssignedModulePackageFile(false, selectedPaths);
			if (packfile != null) {
				// モジュールパッケージは変更不可
				String errmsg = CommonMessages.getInstance().msgCouldNotMoveFilesInPackage;
				AppLogger.error(errmsg + " : \"" + packfile.getPath() + "\"");
				PackageManager.showErrorMessage(getFrame(), errmsg);
				return;
			}
		}
		
		// 移動先を選択
		TreePath selectedOnePath = _cTree.getSelectionPath();
		VirtualFile targetDir = chooseDirectory(
								CommonMessages.getInstance().Choose_MoveTarget,
								((ModuleFileTreeNode)selectedOnePath.getLastPathComponent()).getFileObject());
		if (targetDir == null) {
			// user canceled!
			return;
		}
		TreePath tpTargetDir = model.getTreePathForFile(targetDir);
		
		// 移動先が同一ディレクトリもしくは選択ディレクトリ以下では許可しない
		for (TreePath path : selectedPaths) {
			String errmsg = null;
			VirtualFile vfSource = ((ModuleFileTreeNode)path.getLastPathComponent()).getFileObject();
			if (vfSource.equals(targetDir) || vfSource.getParentFile().equals(targetDir)) {
				errmsg = String.format(
							CommonMessages.getInstance().msgCouldNotMoveSameDirectory,
							targetDir.getName());
			}
			else if (targetDir.isDescendingFrom(vfSource)) {
				errmsg = String.format(
							CommonMessages.getInstance().msgCouldNotMoveToSubDirectory,
							targetDir.getName());
			}
			if (errmsg != null) {
				AppLogger.error(errmsg
								+ "\n  source : \"" + vfSource.getPath() + "\""
								+ "\n  target : \"" + targetDir.getPath() + "\"");
				PackageManager.showErrorMessage(getFrame(), errmsg);
				return;
			}
		}
		
		// 移動
		_cTree.moveFiles(frame, selectedPaths, tpTargetDir, null);
	}

	/**
	 * 選択されているすべてのノードがコピー可能か検証する。
	 * 選択されたノードがすべて同一階層で、ルートではない場合にコピー可能とする。
	 * @return	コピー可能であれば <tt>true</tt> を返す。
	 * 			未選択の場合は <tt>false</tt> を返す。
	 */
	public boolean canCopy() {
		if (_cTree.isSelectionEmpty())
			return false;	// no selection
		
		TreePath[] paths = _cTree.getSelectionPaths();
		if (!_cTree.isTreePathAllSameLevels(paths))
			return false;	// no same level
		
		// ルート階層なら、コピー禁止
		if (paths[0].getPathCount() <= 1)
			return false;
		
		// allow copy
		return true;
	}

	/**
	 * 選択されているノードが示すファイルをクリップボードへコピーする
	 */
	public void doCopy() {
		TreePath[] selectedPaths = _cTree.getSelectionPaths();
		if (selectedPaths == null) {
			return;
		}
		
		// コピーするファイルのリストを作成
		LinkedHashSet<VirtualFile> copyFileSet = new LinkedHashSet<VirtualFile>();
		for (TreePath path : selectedPaths) {
			ModuleFileTreeNode node = (ModuleFileTreeNode)path.getLastPathComponent();
			VirtualFile file = node.getFileObject();
			copyFileSet.add(file);
			if (file.isFile()) {
				// 設定ファイルがあれば、それもコピー対象とする
				VirtualFile fPrefs = ModuleFileManager.getPrefsFile(file);
				if (fPrefs.exists() && fPrefs.isFile()) {
					copyFileSet.add(fPrefs);
				}
			}
		}
		
		// 転送データを作成
		ModuleFileTransferable transfer = new ModuleFileTransferable(copyFileSet);
		Clipboard clip = getToolkit().getSystemClipboard();
		clip.setContents(transfer, transfer);
		getFrame().updateMenuItem(ManagerMenuResources.ID_EDIT_PASTE);
	}

	/**
	 * 現在クリップボードに存在する内容が選択位置に貼り付け可能か検証する。
	 * ファイルもしくはフォルダであり、ノードが一つだけ選択されている場合のみ許可する。
	 * @return	貼り付け可能であれば <tt>true</tt> を返す。
	 * 			未選択の場合は <tt>false</tt> を返す。
	 */
	public boolean canPaste() {
		if (_cTree.getSelectionCount() != 1)
			return false;	// not select one
		
		// クリップボードの情報を判定
		Transferable trans = SwingTools.getSystemClipboardContents(_cTree);
		if (trans == null)
			return false;	// no clipboard contents
		TransferHandler handler = _cTree.getTransferHandler();
		if (handler == null || !handler.canImport(_cTree, trans.getTransferDataFlavors())) {
			// TransferHandler が未定義か、サポートされていない DataFlavor の場合は、ペースト不可
			return false;
		}
		
		// allow paste
		return true;
	}

	/**
	 * クリップボードに存在するデータを、ワークスペースにコピーする
	 */
	public void doPaste() {
		// 貼り付け位置の取得
		if (_cTree.getSelectionCount() != 1)
			return;		// not select one
		TreePath selectedPath = _cTree.getSelectionPath();
		ModuleFileTreeNode selectedNode = (ModuleFileTreeNode)selectedPath.getLastPathComponent();
		if (selectedNode.isRoot())
			return;		// root selected
		if (!selectedNode.isDirectory()) {
			// 選択位置がディレクトリではない場合は、親を対象とする
			selectedPath = selectedPath.getParentPath();
			selectedNode = (ModuleFileTreeNode)selectedPath.getLastPathComponent();
			if (selectedNode.isRoot())
				return;		// root selected
		}
		
		// モジュールパッケージの確認
		VirtualFile packfile = _cTree.findAssignedModulePackageFile(true, new TreePath[]{selectedPath});
		if (packfile != null) {
			// モジュールパッケージは変更不可
			String errmsg = CommonMessages.getInstance().msgCouldNotModifyPackageStructure;
			AppLogger.error(errmsg + " : \"" + packfile.getPath() + "\"");
			PackageManager.showErrorMessage(getFrame(), errmsg);
			return;
		}
		
		// クリップボードの情報を取得
		Transferable trans = SwingTools.getSystemClipboardContents(_cTree);
		if (trans == null)
			return;		// no clipboard contents
		TransferHandler handler = _cTree.getTransferHandler();
		if (handler == null || !handler.canImport(_cTree, trans.getTransferDataFlavors())) {
			// TransferHandler が未定義か、サポートされていない DataFlavor の場合は、ペースト不可
			return;
		}
		VirtualFile[] files = ModuleFileTransferable.getFilesFromTransferData(trans);
		if (files == null)
			return;		// no supported data

		// test
		if (AppLogger.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("[Debug] ManagerTreeView#doPaste()");
			sb.append("\n  Transferable DataFlavors : ");
			DataFlavor[] flavors = trans.getTransferDataFlavors();
			for (DataFlavor df : flavors) {
				sb.append("\n      " + df.toString());
			}
			sb.append("\n  Transfer file list : (" + files.length + " files)");
			for (VirtualFile vf : files) {
				sb.append("\n      " + vf.getPath());
			}
			AppLogger.debug(sb.toString());
		}
		
		// 貼り付け先が同一ディレクトリもしくはコピー元ディレクトリ以下では許可しない
		VirtualFile targetDir = selectedNode.getFileObject();
		for (VirtualFile srcfile : files) {
			if (targetDir.equals(srcfile)) {
				String errmsg = String.format(CommonMessages.getInstance().msgCouldNotCopySameDirectory,
												targetDir.getName());
				AppLogger.error(errmsg
								+ "\n  source : \"" + String.valueOf(srcfile) + "\""
								+ "\n  target : \"" + targetDir.getPath() + "\"");
				PackageManager.showErrorMessage(getFrame(), errmsg);
				return;
			}
			else if (targetDir.isDescendingFrom(srcfile)) {
				String errmsg = String.format(CommonMessages.getInstance().msgCouldNotCopyToSubDirectory,
												targetDir.getName());
				AppLogger.error(errmsg
								+ "\n  source : \"" + String.valueOf(srcfile) + "\""
								+ "\n  target : \"" + targetDir.getPath() + "\"");
				PackageManager.showErrorMessage(getFrame(), errmsg);
				return;
			}
		}
		
		// paste
		_cTree.copyFiles(getFrame(), files, selectedPath);
	}

	/**
	 * 選択されているすべてのノードが削除可能か検証する。
	 * ルートノード以外のノードが選択されている場合は削除可能とする。
	 * @return	削除可能であれば <tt>true</tt> を返す。
	 * 			未選択の場合は <tt>false</tt> を返す。
	 */
	public boolean canDelete() {
		if (_cTree.isSelectionEmpty())
			return false;	// no selection
		
		//--- ルートノードは非表示なので選択されない
		TreePath rootPath = new TreePath(getTreeRootNode());
		if (_cTree.isPathSelected(rootPath))
			return false;	// cannot root delete
		
		// allow delete
		return true;
	}

	/**
	 * 選択されているノードが示すファイルを削除する
	 */
	public void doDelete() {
		// 操作可能な状態でないなら、処理しない
		if (!canDelete()) {
			return;
		}
		ManagerFrame frame = getFrame();
		TreePath[] selectedPaths = _cTree.getSelectionPaths();
		
		// モジュールパッケージの確認
		if (selectedPaths != null) {
			VirtualFile packfile = _cTree.findAssignedModulePackageFile(false, selectedPaths);
			if (packfile != null) {
				// モジュールパッケージは変更不可
				String errmsg = CommonMessages.getInstance().msgCouldNotDeleteFilesInPackage;
				AppLogger.error(errmsg + " : \"" + packfile.getPath() + "\"");
				PackageManager.showErrorMessage(getFrame(), errmsg);
				return;
			}
		}
		
		// 削除を確認
		{
			String confmsg;
			if (selectedPaths.length > 1) {
				//--- 複数選択
				confmsg = String.format(
						CommonMessages.getInstance().confirmDetailDeleteMultiFiles,
						selectedPaths.length);
			} else {
				//--- 単一選択
				confmsg = String.format(
						CommonMessages.getInstance().confirmDetailDeleteSingleFile,
						((ModuleFileTreeNode)selectedPaths[0].getLastPathComponent()).getFilename());
			}
			int retAsk = JOptionPane.showConfirmDialog(getFrame(), confmsg,
					CommonMessages.getInstance().confirmDeleteTitle, JOptionPane.OK_CANCEL_OPTION);
			if (retAsk != JOptionPane.OK_OPTION) {
				// user canceled
				return;
			}
		}
		
		// 削除
		_cTree.deleteFiles(frame, selectedPaths);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	private ModuleTree createTreeComponent() {
		// create default model
		PackageFileTreeModel emptyModel = new PackageFileTreeModel((VirtualFile)null);
		// create Tree component
		ModuleTree newTree = new ModuleTree(emptyModel, _hTree){
			@Override
			protected void onTreeSelectionAdjusted() {
				super.onTreeSelectionAdjusted();
				// このビュー専用の処理
				ManagerTreeView.this.onManagerTreeSelectionAdjusted();
			}
		};
		newTree.setRootVisible(true);	// ルート直下の操作のため、ルートを表示
		return newTree;
	}
	
	private void initialComponent() {
		// setup Main component
		JScrollPane sc = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
										 JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sc.setViewportView(_cTree);
		this.add(sc, BorderLayout.CENTER);
	}
	
	private void setupActions() {
		// Tree
		//--- Mouse listener
		_cTree.addMouseListener(new TreeComponentMouseHandler());
		
		// View
	}
	
	protected void initialSetup(ManagerFrame frame) {
		// メニューアクセラレーターキーを有効にするため、
		// ツリーコンポーネントのマップを変更
		JMenuBar menuBar = frame.getDefaultMenuBar();
		KeyStroke[] strokes = SwingTools.getMenuAccelerators(menuBar);
		_cTree.registMenuAcceleratorKeyStroke(strokes);
	}

	//------------------------------------------------------------
	// Menu events
	//------------------------------------------------------------

	public boolean onProcessMenuSelection(String command, Object source, Action action) {
		if (ManagerMenuResources.ID_EDIT_COPY.equals(command)) {
			onSelectedMenuEditCopy(action);
			return true;
		}
		else if (ManagerMenuResources.ID_EDIT_PASTE.equals(command)) {
			onSelectedMenuEditPaste(action);
			return true;
		}
		else if (ManagerMenuResources.ID_EDIT_DELETE.equals(command)) {
			onSelectedMenuEditDelete(action);
			return true;
		}
		
		return false;
	}

	public boolean onProcessMenuUpdate(String command, Object source, Action action) {
		if (ManagerMenuResources.ID_EDIT_COPY.equals(command)) {
			action.setEnabled(canCopy());
			return true;
		}
		else if (ManagerMenuResources.ID_EDIT_PASTE.equals(command)) {
			action.setEnabled(canPaste());
			return true;
		}
		else if (ManagerMenuResources.ID_EDIT_DELETE.equals(command)) {
			action.setEnabled(canDelete());
			return true;
		}
		
		return false;
	}
	
	// menu : [Edit]-[Copy]
	protected void onSelectedMenuEditCopy(Action action) {
		AppLogger.debug("ManagerTreeView : menu [Edit]-[Copy] selected.");
		if (!canCopy()) {
			action.setEnabled(false);
			return;
		}
		doCopy();
		requestFocusInComponent();
	}
	
	// menu : [Edit]-[Paste]
	protected void onSelectedMenuEditPaste(Action action) {
		AppLogger.debug("ManagerTreeView : menu [Edit]-[Paste] selected.");
		if (!canPaste()) {
			action.setEnabled(false);
			return;
		}
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				doPaste();
				requestFocusInComponent();
			}
		});
	}
	
	// menu : [Edit]-[Delete]
	protected void onSelectedMenuEditDelete(Action action) {
		AppLogger.debug("ManagerTreeView : menu [Edit]-[Delete] selected.");
		if (!canDelete()) {
			action.setEnabled(false);
			return;
		}
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				doDelete();
				requestFocusInComponent();
			}
		});
	}

	//------------------------------------------------------------
	// Action events
	//------------------------------------------------------------
	
	protected void onManagerTreeSelectionAdjusted() {
		// メニューの更新
		ManagerFrame frame = getFrame();
		if (frame != null) {
			frame.updateMenuItem(ManagerMenuResources.ID_FILE_NEW_DIR);
			frame.updateMenuItem(ManagerMenuResources.ID_FILE_MOVETO);
			frame.updateMenuItem(ManagerMenuResources.ID_FILE_RENAME);
			frame.updateMenuItem(ManagerMenuResources.ID_EDIT_MENU);

			// パッケージのプロパティを更新
			frame.updatePackageInfo();
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	protected class TreeComponentMouseHandler extends MouseAdapter
	{
		@Override
		public void mousePressed(MouseEvent me) {
			evaluatePopupMenu(me);
		}

		@Override
		public void mouseReleased(MouseEvent me) {
			evaluatePopupMenu(me);
		}
		
		protected void evaluatePopupMenu(MouseEvent me) {
			if (me.isPopupTrigger()) {
				requestFocusInComponent();
				ManagerMenuBar menuBar = getFrame().getActiveMenuBar();
				if (menuBar != null) {
					menuBar.getTreeContextMenu().show(me.getComponent(), me.getX(), me.getY());
				}
			}
		}
	}
	
	protected class ManagerTreeHandler extends ModuleTree.DefaultTreeHandler
	{
		@Override
		public int acceptTreeDragOverDropAction(DnDTree tree, DropTargetDragEvent dtde, TreePath dragOverPath) {
			// ドロップ処理は未実装
			return TransferHandler.NONE;
		}

		@Override
		public boolean dropTransferData(DnDTree tree, Transferable t, int action) {
			// ドロップ処理は未実装
			return false;
		}
		
		/*--- for test ---
		@Override
		protected boolean copyTransferable(ModuleTree tree, VirtualFile[] sourceFiles, TreePath targetPath) {
			TreePath selectedPath = targetPath;
			ModuleFileTreeNode selectedNode = (ModuleFileTreeNode)selectedPath.getLastPathComponent();
			if (!selectedNode.isDirectory()) {
				// 選択位置がディレクトリではない場合は、親を対象とする
				selectedPath = selectedPath.getParentPath();
				selectedNode = (ModuleFileTreeNode)selectedPath.getLastPathComponent();
				//--- ルートもターゲットとして許可
			}
			
			// モジュールパッケージの確認
			VirtualFile packfile = tree.findAssignedModulePackageFile(true, new TreePath[]{selectedPath});
			if (packfile != null) {
				// モジュールパッケージは変更不可
				String errmsg = CommonMessages.getInstance().msgCouldNotModifyPackageStructure;
				AppLogger.error(errmsg + " : \"" + packfile.getPath() + "\"");
				PackageManager.showErrorMessage(tree.getFrame(), errmsg);
				return false;
			}
			
			// 貼り付け先が同一ディレクトリもしくはコピー元ディレクトリ以下では許可しない
			VirtualFile targetDir = selectedNode.getFileObject();
			for (VirtualFile srcfile : sourceFiles) {
				if (targetDir.equals(srcfile)) {
					String errmsg = String.format(CommonMessages.getInstance().msgCouldNotCopySameDirectory,
													targetDir.getName());
					AppLogger.error(errmsg
									+ "\n  source : \"" + String.valueOf(srcfile) + "\""
									+ "\n  target : \"" + targetDir.getPath() + "\"");
					PackageManager.showErrorMessage(tree.getFrame(), errmsg);
					return false;
				}
				else if (targetDir.isDescendingFrom(srcfile)) {
					String errmsg = String.format(CommonMessages.getInstance().msgCouldNotCopyToSubDirectory,
													targetDir.getName());
					AppLogger.error(errmsg
									+ "\n  source : \"" + String.valueOf(srcfile) + "\""
									+ "\n  target : \"" + targetDir.getPath() + "\"");
					PackageManager.showErrorMessage(tree.getFrame(), errmsg);
					return false;
				}
			}
			
			// paste
			_cTree.copyFiles(getFrame(), sourceFiles, selectedPath);
			
			// 完了
			return true;
		}

		@Override
		protected boolean moveTransferable(ModuleTree tree, VirtualFile[] sourceFiles, TreePath targetPath) {
			// 移動の場合は、同一のコンポーネントでの Drag&Drop 以外では許可しない
			if (!tree.isDnDLocalDragging()) {
				return false;
			}
			
			// 移動元を取得
			TreePath[] selectedPaths = tree.getSelectionPaths();
			if (selectedPaths == null || selectedPaths.length <= 0) {
				return false;	// no sources
			}
			
			// モジュールパッケージの確認
			if (selectedPaths != null) {
				VirtualFile packfile = tree.findAssignedModulePackageFile(false, selectedPaths);
				if (packfile != null) {
					// モジュールパッケージは変更不可
					String errmsg = CommonMessages.getInstance().msgCouldNotMoveFilesInPackage;
					AppLogger.error(errmsg + " : \"" + packfile.getPath() + "\"");
					PackageManager.showErrorMessage(tree.getFrame(), errmsg);
					return false;
				}
			}
			
			// 移動先を取得
			ModuleFileTreeNode targetNode = (ModuleFileTreeNode)targetPath.getLastPathComponent();
			if (!targetNode.isDirectory()) {
				// 選択位置がディレクトリではない場合は、親を対象とする
				targetPath = targetPath.getParentPath();
				targetNode = (ModuleFileTreeNode)targetPath.getLastPathComponent();
				//--- ルートもターゲットとして許可
			}
			VirtualFile targetDir = ((ModuleFileTreeNode)targetPath.getLastPathComponent()).getFileObject();
			
			// 移動先が同一ディレクトリもしくは選択ディレクトリ以下では許可しない
			for (TreePath path : selectedPaths) {
				String errmsg = null;
				VirtualFile vfSource = ((ModuleFileTreeNode)path.getLastPathComponent()).getFileObject();
				if (vfSource.equals(targetDir) || vfSource.getParentFile().equals(targetDir)) {
					errmsg = String.format(
								CommonMessages.getInstance().msgCouldNotMoveSameDirectory,
								targetDir.getName());
				}
				else if (targetDir.isDescendingFrom(vfSource)) {
					errmsg = String.format(
								CommonMessages.getInstance().msgCouldNotMoveToSubDirectory,
								targetDir.getName());
				}
				if (errmsg != null) {
					AppLogger.error(errmsg
									+ "\n  source : \"" + vfSource.getPath() + "\""
									+ "\n  target : \"" + targetDir.getPath() + "\"");
					PackageManager.showErrorMessage(tree.getFrame(), errmsg);
					return false;
				}
			}
			
			// 移動
			_cTree.moveFiles(tree.getFrame(), selectedPaths, targetPath, null);
			
			// 完了
			return true;
		}
		--- end of test ---*/
	}
}
