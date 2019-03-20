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
 * @(#)MEDLocalFileTreePanel.java	2.0.0	2012/10/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MEDLocalFileTreePanel.java	1.20	2012/03/08
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing.tree;

import java.awt.BorderLayout;
import java.awt.LayoutManager;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashSet;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.module.swing.FileDialogManager;
import ssac.aadl.module.swing.FilenameValidator;
import ssac.falconseed.file.VirtualFileTransferable;
import ssac.falconseed.file.swing.tree.AliasFileTreeData;
import ssac.falconseed.file.swing.tree.DefaultFileTreeHandler;
import ssac.falconseed.file.swing.tree.DefaultFileTreeModel;
import ssac.falconseed.file.swing.tree.DefaultFileTreeNode;
import ssac.falconseed.file.swing.tree.FileTree;
import ssac.falconseed.module.MExecDefFileManager;
import ssac.falconseed.runner.RunnerMessages;
import ssac.util.Strings;
import ssac.util.io.VirtualFile;
import ssac.util.logging.AppLogger;
import ssac.util.swing.Application;
import ssac.util.swing.IDialogResult;
import ssac.util.swing.SwingTools;
import ssac.util.swing.tree.DnDTree;
import ssac.util.swing.tree.JTreePopupMenu;

/**
 * モジュール実行定義ローカルファイルを管理するツリーコンポーネント。
 * 
 * @version 2.0.0	2012/10/29
 * @since 1.20
 */
public class MEDLocalFileTreePanel extends JPanel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final MEDLocalFileTreeHandler _hTree = new MEDLocalFileTreeHandler();

	/** ローカルファイルツリー **/
	private FileTree	_cTree;
	/** ツリーのコンテキストメニュー **/
	private JTreePopupMenu		_treePopupMenu;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public MEDLocalFileTreePanel() {
		super(new BorderLayout());
	}
	
	public void initialComponent() {
		// create components
		this._cTree = createTreeComponent();
		
		// setup Main component
		JScrollPane sc = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sc.setViewportView(_cTree);
		this.add(sc, BorderLayout.CENTER);

		// setup actions
		setupActions();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public FileTree getTreeComponent() {
		return _cTree;
	}
	
	public JTreePopupMenu getTreeContextMenu() {
		return _treePopupMenu;
	}
	
	public void setTreeContextMenu(JTreePopupMenu menu) {
		this._treePopupMenu = menu; 
	}

	/**
	 * このコンポーネントのモデルのルートノードに
	 * 抽象パスが設定されている場合に <tt>true</tt> を返す。
	 * @return	ルートノードが抽象パスを持つ場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @since 2.0.0
	 */
	public boolean hasRootDirectory() {
		return (getRootDirectory() != null);
	}

	/**
	 * ルートディレクトリを取得する。
	 * @return	ルートディレクトリ，設定されていない場合は <tt>null</tt>
	 */
	public VirtualFile getRootDirectory() {
		if (_cTree != null) {
			return _cTree.getModel().getRoot().getFileObject();
		} else {
			return null;
		}
	}

	/**
	 * ルートディレクトリを設定する。
	 * @param rootDir	新しいルートディレクトリ
	 * @throws IllegalArgumentException	<em>rootDir</em> がディレクトリではない場合
	 * @throws IllegalStateException		ツリーコンポーネントが生成されていない場合
	 */
	public void setRootDirectory(VirtualFile rootDir) {
		if (_cTree == null)
			throw new IllegalStateException("Tree component does not exists.");
		if (rootDir != null && rootDir.exists() && !rootDir.isDirectory())
			throw new IllegalArgumentException("The specified file is not directory : \"" + rootDir.getAbsolutePath() + "\"");
		
		DefaultFileTreeNode rootNode = _cTree.getModel().getRoot();
		VirtualFile oldRootDir = rootNode.getFileObject();
		if (oldRootDir == rootDir || (rootDir != null && rootDir.equals(oldRootDir))) {
			// 変更なし
			return;
		}
		
		// ファイルの更新とツリーの表示更新
		rootNode.getFileData().replaceFile(rootDir);
		refreshAllTree();
	}
	
	public void clearSelection() {
		_cTree.clearSelection();
	}
	
	public boolean isSelectionEmpty() {
		return _cTree.isSelectionEmpty();
	}
	
	public int getSelectionCount() {
		return _cTree.getSelectionCount();
	}
	
	public VirtualFile getSelectionFile() {
		return _cTree.getSelectionFile();
	}
	
	public VirtualFile[] getSelectionFiles() {
		return _cTree.getSelectionFiles();
	}
	
	public String getSelectedFileProperty() {
		if (_cTree.getSelectionCount() == 1) {
			return _cTree.getTreeNodeFileProperty(_cTree.getSelectionPath());
		} else {
			return null;
		}
	}

	/**
	 * ルートノードを起点に、表示されているすべてのツリーノードを
	 * 最新の情報に更新する。
	 */
	public void refreshAllTree() {
		Object rootNode = _cTree.getModel().getRoot();
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

	/**
	 * ファイルを開く操作を許可する状態であれば <tt>true</tt> を返す。
	 * ファイルを開く操作を許可する状態は次の通り。
	 * <ul>
	 * <li>選択されているノードがファイル</li>
	 * </ul>
	 */
	public boolean canOpenFile() {
		TreePath[] paths = _cTree.getSelectionPaths();
		
		if (paths == null || paths.length <= 0) {
			return false;
		}
		
		for (TreePath path : paths) {
			if (!((DefaultFileTreeNode)path.getLastPathComponent()).isFile()) {
				// not a file
				return false;
			}
		}
		
		// allow
		return true;
	}

	/**
	 * CSVとしてファイルを開く操作を許可する状態であれば <tt>true</tt> を返す。
	 * 開く操作を許可する状態は次の通り。
	 * <ul>
	 * <li>単一のノードが選択されている</li>
	 * <li>選択されているノードがファイル</li>
	 * </ul>
	 */
	public boolean canOpenFileByCSV() {
		if (_cTree.getSelectionCount() != 1) {
			return false;
		}
		
		return canOpenFile();
	}

	/**
	 * ディレクトリの新規作成を許可する状態であれば <tt>true</tt> を返す。
	 * ディレクトリが新規作成可能な状態は次の通り。
	 * <ul>
	 * <li>単一のノードが選択されている</li>
	 * <li>選択されているノードがルートノードではない場合</li>
	 * </ul>
	 */
	public boolean canCreateDirectory() {
		if (_cTree.getSelectionCount() != 1) {
			// 単一選択ではない
			return false;
		}
		
		// ノードのチェック
		try {
			TreePath path = _cTree.getSelectionPath();
			DefaultFileTreeNode selected = (DefaultFileTreeNode)path.getLastPathComponent();
			//--- ルート配下ではないこと
			if (!selected.isNodeAncestor(_cTree.getModel().getRoot())) {
				return false;
			}
		} catch (SecurityException ex) {
			return false;
		}
		
		// allow create
		return true;
	}
	
	/**
	 * ファイルの新規作成を許可する状態であれば <tt>true</tt> を返す。
	 */
	public boolean canCreateFile() {
		return false;
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
		// TODO:ディレクトリ選択ダイアログのタイトルなど
		String dlgTitle;
		if (Strings.isNullOrEmpty(title)) {
			dlgTitle = CommonMessages.getInstance().Button_Select;
		} else {
			dlgTitle = title;
		}
		String dlgTreeLabel = CommonMessages.getInstance().Choose_folder + ":";
		FilenameValidator validator = FileDialogManager.createDefaultFilenameValidator();
		MEDLocalFolderChooser chooser = MEDLocalFolderChooser.createFolderChooser(this,
											false,		// フォルダ作成を許可しない
											getRootDirectory(),
											initParent,	// 初期選択位置
											dlgTitle,
											null,		// 説明は表示しない
											dlgTreeLabel,
											null,		// 入力ラベルは表示しない
											validator);
		
		// ダイアログを表示
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
		if (!canCreateDirectory()) {
			return false;
		}
		
		// ディレクトリ作成位置の確認
		TreePath selectedPath = _cTree.getSelectionPath();
		
		// ディレクトリ名入力ダイアログの表示
		String dlgTitle = CommonMessages.getInstance().InputTitle_NewFolder;
		String dlgTreeLabel = CommonMessages.getInstance().TreeLabel_SelectParentFolder;
		String dlgInputLabel = CommonMessages.getInstance().InputLabel_FolderName;
		VirtualFile initParent = null;
		if (selectedPath != null) {
			initParent = ((DefaultFileTreeNode)selectedPath.getLastPathComponent()).getFileObject();
		}
		FilenameValidator validator = FileDialogManager.createDefaultFilenameValidator();
		MEDLocalFolderChooser chooser = MEDLocalFolderChooser.createFolderChooser(this,
											false,		// フォルダ作成を許可しない
											getRootDirectory(),
											initParent,	// 初期選択位置
											dlgTitle,
											null,		// 説明は表示しない
											dlgTreeLabel,
											dlgInputLabel,
											validator);
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
		TreePath tpNewDir = _cTree.createDirectory(getParentWindow(), tpParentDir, strNewName);
		return (tpNewDir != null);
	}

	/**
	 * ツリーノードで選択されているノードの名前変更が可能であれば <tt>true</tt> を返す。
	 * 名前変更可能な条件は次の通り。
	 * <ul>
	 * <li>単一の選択である</li>
	 * <li>ユーザールート配下のディレクトリもしくはファイルが選択されている</li>
	 * </ul>
	 */
	public boolean canRename() {
		if (_cTree.getSelectionCount() != 1) {
			// 単一選択以外では名前変更不可
			return false;
		}
		
		// ノードのチェック
		TreePath path = _cTree.getSelectionPath();
		DefaultFileTreeNode node = (DefaultFileTreeNode)path.getLastPathComponent();
		if (node.isRoot()) {
			// ルートノードは変更不可
			return false;
		}
		else if (!node.isNodeAncestor(_cTree.getModel().getRoot())) {
			// ルートノード配下のノードでなければ変更不可
			return false;
		}
		
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
		TreePath selectedPath = _cTree.getSelectionPath();
		DefaultFileTreeNode selectedNode = (DefaultFileTreeNode)selectedPath.getLastPathComponent();

// TODO: 開かれているドキュメントを確認		
//		// 開かれているドキュメントを収集
//		IEditorView[] editors = frame.getAllEditors();
//		HashMap<VirtualFile, IEditorView> willMoveMap = new HashMap<VirtualFile, IEditorView>();
//		if (editors.length > 0) {
//			// 開かれているエディタのドキュメントを確認
//			for (IEditorView ev : editors) {
//				File fDocument = ev.getDocumentFile();
//				if (fDocument != null) {
//					// 対象のファイルに開かれているドキュメントが含まれているかを確認
//					VirtualFile vfDoc = new DefaultFile(fDocument);
//					if (vfDoc.isDescendingFrom(selectedNode.getFileObject())) {
//						willMoveMap.put(vfDoc, ev);
//					}
//				}
//			}
//		}
//		
//		// 開かれているドキュメントが移動可能かを確認
//		if (!willMoveMap.isEmpty()) {
//			boolean denyMove = false;
//			StringBuilder sb = new StringBuilder();
//			sb.append(CommonMessages.getInstance().msgCouldNotRenameOpenedEditingFile);
//			for (Map.Entry<VirtualFile, IEditorView> entry : willMoveMap.entrySet()) {
//				if (!entry.getValue().canMoveDocumentFile()) {
//					denyMove = true;
//					String filepath = _cTree.getModel().formatFilePath(entry.getKey());
//					sb.append("\n  ");
//					sb.append(filepath);
//				}
//			}
//			
//			if (denyMove) {
//				String errmsg = sb.toString();
//				AppLogger.error(errmsg);
//				Application.showErrorMessage(getFrame(), errmsg);
//				return;
//			}
//		}
		
		// 名前入力ダイアログの表示
		String strNewName = FileDialogManager.showRenameDialog(getParentWindow(), selectedNode.getFileObject());
		if (Strings.isNullOrEmpty(strNewName)) {
			// user canceled!
			return;
		}
		
		// 名前変更
		Map<VirtualFile, VirtualFile> watchMap = _cTree.renameFile(getParentWindow(), selectedPath,
														strNewName, null /*willMoveMap.keySet()*/);
		
		// 変更されたドキュメントの保存先を更新
		if (watchMap != null && !watchMap.isEmpty()) {
// TODO: 変更されたドキュメントの保存先を更新
//			for (Map.Entry<VirtualFile, VirtualFile> entry : watchMap.entrySet()) {
//				VirtualFile vfSrc = entry.getKey();
//				VirtualFile vfDst = entry.getValue();
//				if (vfDst != null) {
//					frame.renameToEditorFile(willMoveMap.get(vfSrc), ((DefaultFile)vfDst).getJavaFile());
//				}
//			}
		}
	}

	/**
	 * ツリーノードで選択されているノードが移動可能であれば <tt>true</tt> を返す。
	 * 移動可能となる条件は次の通り。
	 * <ul>
	 * <li>ユーザールート配下のディレクトリもしくはファイルが選択されている</li>
	 * </ul>
	 */
	public boolean canMoveTo() {
		if (_cTree.getSelectionCount() <= 0) {
			// 選択されたノードが存在しない場合は、移動不可
			return false;
		}

		// ノードのチェック
		DefaultFileTreeNode ndRoot = _cTree.getModel().getRoot();
		TreePath[] paths = _cTree.getSelectionPaths();
		for (TreePath path : paths) {
			DefaultFileTreeNode node = (DefaultFileTreeNode)path.getLastPathComponent();
			if (node.isRoot()) {
				// ルートノードは変更不可
				return false;
			}
			else if (!node.isNodeAncestor(ndRoot)) {
				// ルートノード配下のノードでなければ変更不可
				return false;
			}
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
		TreePath[] selectedPaths = _cTree.getSelectionPaths();

// TODO: 開かれているドキュメントの収集
//		// 移動するドキュメントを収集
//		IEditorView[] editors = frame.getAllEditors();
//		HashMap<VirtualFile, IEditorView> willMoveMap = new HashMap<VirtualFile, IEditorView>();
//		if (editors.length > 0) {
//			// 開かれているエディタのドキュメントを確認
//			for (IEditorView ev : editors) {
//				File fDocument = ev.getDocumentFile();
//				if (fDocument != null) {
//					VirtualFile vfDoc = new DefaultFile(fDocument);
//					for (TreePath path : selectedPaths) {
//						FileTreeNode node = (FileTreeNode)path.getLastPathComponent();
//						// 移動対象のファイルに開かれているドキュメントが含まれているかを確認
//						if (vfDoc.isDescendingFrom(node.getFileObject())) {
//							willMoveMap.put(vfDoc, ev);
//							break;
//						}
//					}
//				}
//			}
//		}
//		
//		// 開かれているドキュメントが移動可能かを確認
//		if (!willMoveMap.isEmpty()) {
//			boolean denyMove = false;
//			StringBuilder sb = new StringBuilder();
//			sb.append(CommonMessages.getInstance().msgCouldNotMoveOpenedEditingFile);
//			for (Map.Entry<VirtualFile, IEditorView> entry : willMoveMap.entrySet()) {
//				if (!entry.getValue().canMoveDocumentFile()) {
//					denyMove = true;
//					String filepath = _cTree.getModel().formatFilePath(entry.getKey());
//					sb.append("\n  ");
//					sb.append(filepath);
//				}
//			}
//			
//			if (denyMove) {
//				String errmsg = sb.toString();
//				AppLogger.error(errmsg);
//				Application.showErrorMessage(getFrame(), errmsg);
//				return;
//			}
//		}
		
		// 移動先を選択
		TreePath selectedOnePath = _cTree.getSelectionPath();
		VirtualFile targetDir = chooseDirectory(
								CommonMessages.getInstance().Choose_MoveTarget,
								((DefaultFileTreeNode)selectedOnePath.getLastPathComponent()).getFileObject());
		if (targetDir == null) {
			// user canceled!
			return;
		}
		TreePath tpTargetDir = _cTree.getModel().getTreePathForFile(targetDir);
		
		// 移動先が同一ディレクトリもしくは選択ディレクトリ以下では許可しない
		for (TreePath path : selectedPaths) {
			String errmsg = null;
			VirtualFile vfSource = ((DefaultFileTreeNode)path.getLastPathComponent()).getFileObject();
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
				Application.showErrorMessage(getParentWindow(), errmsg);
				return;
			}
		}
		
		// 移動
		Map<VirtualFile,VirtualFile> watchMap = _cTree.moveFiles(getParentWindow(), selectedPaths,
													tpTargetDir, null /*willMoveMap.keySet()*/);

		// 変更されたドキュメントの保存先を更新
		if (watchMap != null && !watchMap.isEmpty()) {
// TODO: 変更されたドキュメントの保存先を更新
//			for (Map.Entry<VirtualFile, VirtualFile> entry : watchMap.entrySet()) {
//				VirtualFile vfSrc = entry.getKey();
//				VirtualFile vfDst = entry.getValue();
//				if (vfDst != null) {
//					frame.renameToEditorFile(willMoveMap.get(vfSrc), ((DefaultFile)vfDst).getJavaFile());
//				}
//			}
		}
	}

	/**
	 * 選択されているすべてのノードがコピー可能か検証する。
	 * 選択されたノードがすべて同一階層で、ルートではない場合にコピー可能とする。
	 * @return	コピー可能であれば <tt>true</tt> を返す。
	 * 			未選択の場合は <tt>false</tt> を返す。
	 */
	public boolean canCopy() {
		if (!hasRootDirectory() || _cTree.isSelectionEmpty())
			return false;	// no selection
		
		TreePath[] paths = _cTree.getSelectionPaths();
		if (!_cTree.isTreePathAllSameLevels(paths))
			return false;	// no same level
		
		// ルート階層なら、コピー禁止
		if (paths[0].getPathCount() <= 0) {
			return false;
		}
		
		// allow copy
		return true;
	}

	/**
	 * 選択されているノードが示すファイルをクリップボードへコピーする
	 */
	public void doCopy() {
		if (!hasRootDirectory())
			return;
		TreePath[] selectedPaths = _cTree.getSelectionPaths();
		if (selectedPaths == null) {
			return;
		}
		
		// コピーするファイルのリストを作成
		LinkedHashSet<VirtualFile> copyFileSet = new LinkedHashSet<VirtualFile>();
		for (TreePath path : selectedPaths) {
			DefaultFileTreeNode node = (DefaultFileTreeNode)path.getLastPathComponent();
			VirtualFile file = node.getFileObject();
			copyFileSet.add(file);
			// 全てのファイルを表示しているので、次の処理は行わない
//			if (file.isFile()) {
//				// 設定ファイルがあれば、それもコピー対象とする
//				VirtualFile fPrefs = ModuleFileManager.getPrefsFile(file);
//				if (fPrefs.exists() && fPrefs.isFile()) {
//					copyFileSet.add(fPrefs);
//				}
//			}
		}
		
		// 転送データを作成
		VirtualFileTransferable transfer = new VirtualFileTransferable(copyFileSet);
		Clipboard clip = getToolkit().getSystemClipboard();
		clip.setContents(transfer, transfer);
	}

	/**
	 * 現在クリップボードに存在する内容が選択位置に貼り付け可能か検証する。
	 * ファイルもしくはフォルダであり、ノードが一つだけ選択されている場合のみ許可する。
	 * @return	貼り付け可能であれば <tt>true</tt> を返す。
	 * 			未選択の場合は <tt>false</tt> を返す。
	 */
	public boolean canPaste() {
		if (!hasRootDirectory() || _cTree.getSelectionCount() != 1)
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
		
		// ノードのチェック(は不要、ルート直下にペースト可能)
//		try {
//			TreePath path = _cTree.getSelectionPath();
//			DefaultFileTreeNode selected = (DefaultFileTreeNode)path.getLastPathComponent();
//			if (!selected.isNodeAncestor(_cTree.getModel().getRoot())) {
//				// ルート配下のノードでなければ変更不可
//				return false;
//			}
//		} catch (SecurityException ex) {
//			return false;
//		}
		
		// allow paste
		return true;
	}

	/**
	 * クリップボードに存在するデータを、ワークスペースにコピーする
	 */
	public void doPaste() {
		// 貼り付け位置の取得
		if (!hasRootDirectory() || _cTree.getSelectionCount() != 1)
			return;		// not select one
		TreePath selectedPath = _cTree.getSelectionPath();
		DefaultFileTreeNode selectedNode = (DefaultFileTreeNode)selectedPath.getLastPathComponent();
		try {
			if (!selectedNode.isDirectory()) {
				// 選択位置がディレクトリではない場合は、親を対象とする
				selectedPath = selectedPath.getParentPath();
				selectedNode = (DefaultFileTreeNode)selectedPath.getLastPathComponent();
				if (selectedNode.isRoot())
					return;		// root selected
			}
		}
		catch (SecurityException ex) {
			// アクセス不可
			return;
		}
		
		// 貼り付け位置がユーザールート以下ではない場合、貼り付けを許可しない。
		if (selectedNode!=_cTree.getModel().getRoot() && !selectedNode.isNodeAncestor(_cTree.getModel().getRoot())) {
			// 選択された場所には貼り付けできない。
			String errmsg = RunnerMessages.getInstance().msgCouldNotPasteToThis;
			AppLogger.error(errmsg + " : \"" + selectedNode.getFileObject().toString() + "\"");
			Application.showErrorMessage(getParentWindow(), errmsg);
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
		VirtualFile[] files = VirtualFileTransferable.getFilesFromTransferData(trans);
		if (files == null)
			return;		// no supported data

		// test
		if (AppLogger.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("[Debug] " + getClass().getName() + "#doPaste()");
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
				Application.showErrorMessage(getParentWindow(), errmsg);
				return;
			}
			else if (targetDir.isDescendingFrom(srcfile)) {
				String errmsg = String.format(CommonMessages.getInstance().msgCouldNotCopyToSubDirectory,
												targetDir.getName());
				AppLogger.error(errmsg
								+ "\n  source : \"" + String.valueOf(srcfile) + "\""
								+ "\n  target : \"" + targetDir.getPath() + "\"");
				Application.showErrorMessage(getParentWindow(), errmsg);
				return;
			}
		}
		
		// paste
		_cTree.copyFiles(getParentWindow(), files, selectedPath);
	}

	/**
	 * 選択されているすべてのノードが削除可能か検証する。
	 * ルートノード以外のノードが選択されている場合は削除可能とする。
	 * @return	削除可能であれば <tt>true</tt> を返す。
	 * 			未選択の場合は <tt>false</tt> を返す。
	 */
	public boolean canDelete() {
		if (!hasRootDirectory() || _cTree.isSelectionEmpty())
			return false;	// no selection
		
		//--- ルートノードは非表示なので選択されない
		TreePath[] selectedPaths = _cTree.getSelectionPaths();
		TreePath tpRoot = new TreePath(_cTree.getModel().getRoot().getPath());
		for (TreePath tp : selectedPaths) {
			if (tp.getPathCount() < 2 || !tpRoot.isDescendant(tp)) {
				// ルートの配下のパスではないなら、削除禁止
				return false;
			}
		}
		
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
		TreePath[] selectedPaths = _cTree.getSelectionPaths();
		
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
						((DefaultFileTreeNode)selectedPaths[0].getLastPathComponent()).getFilename());
			}
			int retAsk = JOptionPane.showConfirmDialog(getParentWindow(), confmsg,
					CommonMessages.getInstance().confirmDeleteTitle, JOptionPane.OK_CANCEL_OPTION);
			if (retAsk != JOptionPane.OK_OPTION) {
				// user canceled
				return;
			}
		}

// TODO: 変更されたドキュメントの確認
//		// 変更されたドキュメントの削除を確認
//		IEditorView[] editors = frame.getAllEditors();
//		ArrayList<IEditorView> willDeleteList = null;
//		if (editors.length > 0) {
//			// 開かれているエディタのドキュメントを確認
//			StringBuilder sb = new StringBuilder();
//			int numWillDeleteModifiedEditors = 0;
//			willDeleteList = new ArrayList<IEditorView>(editors.length);
//			for (IEditorView ev : editors) {
//				File fDocument = ev.getDocumentFile();
//				if (fDocument != null) {
//					VirtualFile vfDoc = new DefaultFile(fDocument);
//					for (TreePath path : selectedPaths) {
//						FileTreeNode node = (FileTreeNode)path.getLastPathComponent();
//						// 削除対象のファイルに開かれているドキュメントが含まれているかを確認
//						if (vfDoc.isDescendingFrom(node.getFileObject())) {
//							willDeleteList.add(ev);
//							if (ev.isModified()) {
//								numWillDeleteModifiedEditors++;
//								sb.append("\n    '");
//								sb.append(ev.getDocumentTitle());
//								sb.append("'");
//							}
//							break;
//						}
//					}
//				}
//			}
//			// 変更されたドキュメントを削除するかの確認
//			if (numWillDeleteModifiedEditors > 0) {
//				String strNames = sb.toString();
//				sb.setLength(0);
//				sb.append(RunnerMessages.getInstance().confirmDeleteEditingResources);
//				sb.append("\n");
//				sb.append(strNames);
//				int ret = JOptionPane.showConfirmDialog(frame, sb.toString(),
//						CommonMessages.getInstance().confirmDeleteTitle, JOptionPane.OK_CANCEL_OPTION);
//				if (ret != JOptionPane.OK_OPTION) {
//					// user canceled
//					return;
//				}
//			}
//		}
		
		// 削除
		_cTree.deleteFiles(getParentWindow(), selectedPaths);

// TODO: 削除されたドキュメントのエディタを閉じる
//		if (willDeleteList != null && !willDeleteList.isEmpty()) {
//			for (IEditorView editor : willDeleteList) {
//				frame.closeEditorByEditor(true, false, editor);
//			}
//		}
	}

	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------

	/**
	 * ツリーの選択状態が変更されたときのイベントハンドラ
	 */
	protected void onTreeSelectionAdjusted() {
		// 選択変更時の処理
	}

	/**
	 * ツリーノードが左ボタンでダブルクリックされたときのイベントハンドラ
	 */
	protected void onTreeDoubleClicked(MouseEvent e) {
		// ダブルクリックの処理
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected Window getParentWindow() {
		return SwingTools.getWindowForComponent(this);
	}
	
	protected FileTree createTreeComponent() {
		// モデル生成
		AliasFileTreeData filedata = new MEDLocalRootFileTreeData(null, MExecDefFileManager.MEXECDEF_FILES_ROOT_DIRNAME);
		DefaultFileTreeModel model = new DefaultFileTreeModel(new DefaultFileTreeNode(filedata));
		
		// ツリー生成
		FileTree newTree = new FileTree(model, _hTree){
			@Override
			protected void onTreeSelectionAdjusted() {
				super.onTreeSelectionAdjusted();
				// このダイアログ専用の処理
				MEDLocalFileTreePanel.this.onTreeSelectionAdjusted();
			}
		};
		newTree.setRootVisible(true);		// ルートノードを表示
		newTree.setShowsRootHandles(true);	// 最上位にハンドルを表示する
		newTree.setTooltipEnabled(false);	// ツールチップは表示しない
		
		return newTree;
	}
	
	private void setupActions() {
		// Tree
		//--- Mouse listener
		_cTree.addMouseListener(new TreeComponentMouseHandler());
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	static public class FileTreeStatusBar extends JPanel
	{
		static private final String Blank = " ";
		private JLabel lblMessage;
		
		public FileTreeStatusBar() {
			super();
			initialComponent();
		}

		public FileTreeStatusBar(boolean isDoubleBuffered) {
			super(isDoubleBuffered);
			initialComponent();
		}

		public FileTreeStatusBar(LayoutManager layout, boolean isDoubleBuffered) {
			super(layout, isDoubleBuffered);
			initialComponent();
		}

		public FileTreeStatusBar(LayoutManager layout) {
			super(layout);
			initialComponent();
		}
		
		private final void initialComponent() {
			// create Components
			//--- Messages
			lblMessage = new JLabel(Blank);
			
			this.add(lblMessage, BorderLayout.CENTER);
		}
		
		public void clearMessage() {
			lblMessage.setText(Blank);
		}
		
		public String getMessage() {
			return lblMessage.getText();
		}
		
		public void setMessage(String strmsg) {
			lblMessage.setText(Strings.isNullOrEmpty(strmsg) ? Blank : strmsg);
		}
	}
	
	protected class TreeComponentMouseHandler extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount()==2) {
				// 左ボタンのダブルクリック
				onTreeDoubleClicked(e);
			}
		}
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
				JTreePopupMenu menu = getTreeContextMenu();
				if (menu != null) {
					menu.show(me.getComponent(), me.getX(), me.getY());
				}
			}
		}
	}

	/**
	 * ツリーコンポーネントの制御を行うハンドラ
	 */

	/**
	 * ツリーコンポーネントの制御を行う標準のハンドラ
	 */
	static public class MEDLocalFileTreeHandler extends DefaultFileTreeHandler
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
	}
}
