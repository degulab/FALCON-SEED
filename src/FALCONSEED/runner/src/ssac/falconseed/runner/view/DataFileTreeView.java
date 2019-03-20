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
 *  Copyright 2007-2014  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)DataFileTreeView.java	3.1.0	2014/05/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DataFileTreeView.java	2.0.0	2012/09/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DataFileTreeView.java	1.22	2012/08/22
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DataFileTreeView.java	1.20	2012/03/24
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DataFileTreeView.java	1.10	2011/02/15
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.runner.view;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

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
import ssac.aadl.module.ModuleFileManager;
import ssac.aadl.module.swing.FileDialogManager;
import ssac.aadl.module.swing.FilenameValidator;
import ssac.falconseed.data.swing.tree.DataFileTree;
import ssac.falconseed.data.swing.tree.DataFileTreeModel;
import ssac.falconseed.data.swing.tree.DataFolderChooser;
import ssac.falconseed.data.swing.tree.FileTreeNode;
import ssac.falconseed.editor.view.IEditorView;
import ssac.falconseed.file.DefaultVirtualFilePathFormatter;
import ssac.falconseed.file.VirtualFilePathFormatterList;
import ssac.falconseed.file.VirtualFileTransferable;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.menu.RunnerMenuBar;
import ssac.falconseed.runner.menu.RunnerMenuResources;
import ssac.util.Objects;
import ssac.util.Strings;
import ssac.util.io.DefaultFile;
import ssac.util.io.VirtualFile;
import ssac.util.logging.AppLogger;
import ssac.util.swing.Application;
import ssac.util.swing.IDialogResult;
import ssac.util.swing.SwingTools;
import ssac.util.swing.tree.DnDTree;

/**
 * データファイル専用のツリービュー
 * 
 * @version 3.1.0	2014/05/19
 * @since 1.10
 */
public class DataFileTreeView extends JPanel implements IRunnerTreeView
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	private final LocalTreeHandler _hTree = new LocalTreeHandler();

	/** ツリーコンポーネント **/
	private DataFileTree _cTree;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 標準のコンストラクタ
	 */
	public DataFileTreeView() {
		super(new BorderLayout());
	}
	
	public void initialComponent(VirtualFile systemRootDir, VirtualFile userRootDir) {
		if (systemRootDir == null)
			throw new NullPointerException("The specified System root directory is null.");
		if (userRootDir == null)
			throw new NullPointerException("The specified User root directory is null.");
		
		// create components
		this._cTree = createTreeComponent();
		
		// setup tree model
		DataFileTreeModel newModel = new DataFileTreeModel(systemRootDir, userRootDir);
		_cTree.setModel(newModel);
		
		// setup Main component
		JScrollPane sc = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sc.setViewportView(_cTree);
		this.add(sc, BorderLayout.CENTER);

		// setup actions
		setupActions();
	}
	
	protected void initialSetup(RunnerFrame frame) {
		// メニューアクセラレータキーを有効にするため、
		// ツリーコンポーネントのマップを変更
		JMenuBar menuBar = frame.getDefaultMainMenuBar();
		KeyStroke[] strokes = SwingTools.getMenuAccelerators(menuBar);
		_cTree.registMenuAcceleratorKeyStroke(strokes);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

// サブツリーは使用しない	
//	/**
//	 * このツリービューのツリーモデルを継承する、
//	 * 新しいツリーコンポーネントを取得する。
//	 * <p><b>注意：</b>
//	 * <blockquote>
//	 * このメソッドが返すオブジェクトの使用後には、
//	 * {@link SubDataFileTree#detachTreeViewModel()} を呼び出して
//	 * モデルリスナーを解放しておくこと。
//	 * </blockquote>
//	 * @return	新しい <code>SubDataFileTree</code> オブジェクト
//	 */
//	public SubDataFileTree getSubTree() {
//		return createSubTreeComponent();
//	}

	/**
	 * 現在の設定による、ツリーのパス表示名整形オブジェクトを取得する。
	 * @return	新しいパス表示名整形オブジェクト
	 * @since 1.22
	 */
	public VirtualFilePathFormatterList getTreePathFormatter() {
		VirtualFilePathFormatterList vfFormatter = new VirtualFilePathFormatterList();
		
		//--- System root dir
		FileTreeNode ndSystemRoot = _cTree.getModel().getSystemRootNode();
		if (ndSystemRoot != null) {
			DefaultVirtualFilePathFormatter formatter = new DefaultVirtualFilePathFormatter(ndSystemRoot.getFileObject(), ndSystemRoot.getDisplayName());
			vfFormatter.add(formatter);
		}
		//--- User root dir
		FileTreeNode ndUserRoot = _cTree.getModel().getUserRootNode();
		if (ndUserRoot != null) {
			DefaultVirtualFilePathFormatter formatter = new DefaultVirtualFilePathFormatter(ndUserRoot.getFileObject(), ndUserRoot.getDisplayName());
			vfFormatter.add(formatter);
		}
		
		return vfFormatter;
	}

	/**
	 * ツリーコンポーネントに設定されているツリーモデルを取得する。
	 */
	public DataFileTreeModel getTreeModel() {
		return _cTree.getModel();
	}
	
	public DataFileTree getTreeComponent() {
		return _cTree;
	}
	
	public VirtualFile getSystemRootDirectory() {
		return _cTree.getModel().getSystemRootDirectory();
	}
	
	public VirtualFile getUserRootDirectory() {
		return _cTree.getModel().getUserRootDirectory();
	}
	
	public String getSystemRootDisplayName() {
		return _cTree.getModel().getSystemRootNode().getDisplayName();
	}
	
	public String getUserRootDisplayName() {
		return _cTree.getModel().getUserRootNode().getDisplayName();
	}
	
	public void setSystemRootDir(VirtualFile newDir) {
		_cTree.getModel().setSystemRootDirectory(newDir);
	}
	
	public void setUserRootDir(VirtualFile newDir) {
		_cTree.getModel().setUserRootDirectory(newDir);
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
	 * このビューを格納するエディタフレームを取得する。
	 * @return	このビューを格納するエディタフレームのインスタンス。
	 * 			このビューがフレームに格納されていない場合は <tt>null</tt> を返す。
	 */
	public RunnerFrame getFrame() {
		Window parentFrame = SwingUtilities.windowForComponent(this);
		if (parentFrame instanceof RunnerFrame)
			return (RunnerFrame)parentFrame;
		else
			return null;
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
	
	//
	// File operations
	//

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
			if (!((FileTreeNode)path.getLastPathComponent()).isFile()) {
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
		
		// ユーザールートの存在チェック
		FileTreeNode ndUserRoot = _cTree.getModel().getUserRootNode();
		if (ndUserRoot == null || !ndUserRoot.exists() || !ndUserRoot.isDirectory()) {
			return false;
		}
		
		// ノードのチェック
		try {
			TreePath path = _cTree.getSelectionPath();
			FileTreeNode selected = (FileTreeNode)path.getLastPathComponent();
			//--- ルートではないこと
			if (selected.isRoot())
				return false;
			//--- ユーザールート配下ではないこと
			if (!selected.isNodeAncestor(ndUserRoot)) {
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
		String dlgTitle;
		if (Strings.isNullOrEmpty(title)) {
			dlgTitle = CommonMessages.getInstance().Button_Select;
		} else {
			dlgTitle = title;
		}
		String dlgTreeLabel = CommonMessages.getInstance().Choose_folder + ":";
		FilenameValidator validator = FileDialogManager.createDefaultFilenameValidator();
		DataFolderChooser chooser = DataFolderChooser.createFolderChooser(getFrame(),
											false,		// フォルダ作成を許可しない
											null,		// システムルートは表示しない
											getUserRootDirectory(),	// ユーザールート
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
		RunnerFrame frame = getFrame();
		String dlgTitle = CommonMessages.getInstance().InputTitle_NewFolder;
		String dlgTreeLabel = CommonMessages.getInstance().TreeLabel_SelectParentFolder;
		String dlgInputLabel = CommonMessages.getInstance().InputLabel_FolderName;
		VirtualFile initParent = null;
		if (selectedPath != null) {
			initParent = ((FileTreeNode)selectedPath.getLastPathComponent()).getFileObject();
		}
		FilenameValidator validator = FileDialogManager.createDefaultFilenameValidator();
		DataFolderChooser chooser = DataFolderChooser.createFolderChooser(getFrame(),
											false,		// フォルダ作成を許可しない
											null,		// システムルートは表示しない
											getUserRootDirectory(),	// ユーザールート
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
		TreePath tpNewDir = _cTree.createDirectory(frame, tpParentDir, strNewName);
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
		FileTreeNode node = (FileTreeNode)path.getLastPathComponent();
		FileTreeNode ndUserRoot = _cTree.getModel().getUserRootNode();
		if (node == ndUserRoot) {
			// ユーザールートと同じノードは変更不可
			return false;
		}
		else if (!node.isNodeAncestor(ndUserRoot)) {
			// ユーザールート配下のノードでなければ変更不可
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
		RunnerFrame frame = getFrame();
		TreePath selectedPath = _cTree.getSelectionPath();
		FileTreeNode selectedNode = (FileTreeNode)selectedPath.getLastPathComponent();
		
		// 開かれているドキュメントを収集
		IEditorView[] editors = frame.getAllEditors();
		HashMap<VirtualFile, IEditorView> willMoveMap = new HashMap<VirtualFile, IEditorView>();
		if (editors.length > 0) {
			// 開かれているエディタのドキュメントを確認
			for (IEditorView ev : editors) {
				File fDocument = ev.getDocumentFile();
				if (fDocument != null) {
					// 対象のファイルに開かれているドキュメントが含まれているかを確認
					VirtualFile vfDoc = new DefaultFile(fDocument);
					if (vfDoc.isDescendingFrom(selectedNode.getFileObject())) {
						willMoveMap.put(vfDoc, ev);
					}
				}
			}
		}
		
		// 開かれているドキュメントが移動可能かを確認
		if (!willMoveMap.isEmpty()) {
			boolean denyMove = false;
			StringBuilder sb = new StringBuilder();
			sb.append(CommonMessages.getInstance().msgCouldNotRenameOpenedEditingFile);
			for (Map.Entry<VirtualFile, IEditorView> entry : willMoveMap.entrySet()) {
				if (!entry.getValue().canMoveDocumentFile()) {
					denyMove = true;
					String filepath = _cTree.getModel().formatFilePath(entry.getKey());
					sb.append("\n  ");
					sb.append(filepath);
				}
			}
			
			if (denyMove) {
				String errmsg = sb.toString();
				AppLogger.error(errmsg);
				Application.showErrorMessage(getFrame(), errmsg);
				return;
			}
		}
		
		// 名前入力ダイアログの表示
		String strNewName = FileDialogManager.showRenameDialog(frame, selectedNode.getFileObject());
		if (Strings.isNullOrEmpty(strNewName)) {
			// user canceled!
			return;
		}
		
		// 名前変更
		Map<VirtualFile, VirtualFile> watchMap = _cTree.renameFile(frame, selectedPath,
														strNewName, willMoveMap.keySet());
		
		// 変更されたドキュメントの保存先を更新
		if (watchMap != null && !watchMap.isEmpty()) {
			for (Map.Entry<VirtualFile, VirtualFile> entry : watchMap.entrySet()) {
				VirtualFile vfSrc = entry.getKey();
				VirtualFile vfDst = entry.getValue();
				if (vfDst != null) {
					frame.renameToEditorFile(willMoveMap.get(vfSrc), ((DefaultFile)vfDst).getJavaFile());
				}
			}
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
		FileTreeNode ndUserRoot = _cTree.getModel().getUserRootNode();
		TreePath[] paths = _cTree.getSelectionPaths();
		for (TreePath path : paths) {
			FileTreeNode node = (FileTreeNode)path.getLastPathComponent();
			if (node == ndUserRoot) {
				// ユーザールートと同じノードは変更不可
				return false;
			}
			else if (!node.isNodeAncestor(ndUserRoot)) {
				// ユーザールート配下のノードでなければ変更不可
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
		RunnerFrame frame = getFrame();
		DataFileTreeModel model = _cTree.getModel();
		TreePath[] selectedPaths = _cTree.getSelectionPaths();
		
		// 移動するドキュメントを収集
		IEditorView[] editors = frame.getAllEditors();
		HashMap<VirtualFile, IEditorView> willMoveMap = new HashMap<VirtualFile, IEditorView>();
		if (editors.length > 0) {
			// 開かれているエディタのドキュメントを確認
			for (IEditorView ev : editors) {
				File fDocument = ev.getDocumentFile();
				if (fDocument != null) {
					VirtualFile vfDoc = new DefaultFile(fDocument);
					for (TreePath path : selectedPaths) {
						FileTreeNode node = (FileTreeNode)path.getLastPathComponent();
						// 移動対象のファイルに開かれているドキュメントが含まれているかを確認
						if (vfDoc.isDescendingFrom(node.getFileObject())) {
							willMoveMap.put(vfDoc, ev);
							break;
						}
					}
				}
			}
		}
		
		// 開かれているドキュメントが移動可能かを確認
		if (!willMoveMap.isEmpty()) {
			boolean denyMove = false;
			StringBuilder sb = new StringBuilder();
			sb.append(CommonMessages.getInstance().msgCouldNotMoveOpenedEditingFile);
			for (Map.Entry<VirtualFile, IEditorView> entry : willMoveMap.entrySet()) {
				if (!entry.getValue().canMoveDocumentFile()) {
					denyMove = true;
					String filepath = _cTree.getModel().formatFilePath(entry.getKey());
					sb.append("\n  ");
					sb.append(filepath);
				}
			}
			
			if (denyMove) {
				String errmsg = sb.toString();
				AppLogger.error(errmsg);
				Application.showErrorMessage(getFrame(), errmsg);
				return;
			}
		}
		
		// 移動先を選択
		TreePath selectedOnePath = _cTree.getSelectionPath();
		VirtualFile targetDir = chooseDirectory(
								CommonMessages.getInstance().Choose_MoveTarget,
								((FileTreeNode)selectedOnePath.getLastPathComponent()).getFileObject());
		if (targetDir == null) {
			// user canceled!
			return;
		}
		TreePath tpTargetDir = model.getTreePathForFile(targetDir);
		
		// 移動先が同一ディレクトリもしくは選択ディレクトリ以下では許可しない
		for (TreePath path : selectedPaths) {
			String errmsg = null;
			VirtualFile vfSource = ((FileTreeNode)path.getLastPathComponent()).getFileObject();
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
				Application.showErrorMessage(getFrame(), errmsg);
				return;
			}
		}
		
		// 移動
		Map<VirtualFile,VirtualFile> watchMap = _cTree.moveFiles(frame, selectedPaths,
													tpTargetDir, willMoveMap.keySet());

		// 変更されたドキュメントの保存先を更新
		if (watchMap != null && !watchMap.isEmpty()) {
			for (Map.Entry<VirtualFile, VirtualFile> entry : watchMap.entrySet()) {
				VirtualFile vfSrc = entry.getKey();
				VirtualFile vfDst = entry.getValue();
				if (vfDst != null) {
					frame.renameToEditorFile(willMoveMap.get(vfSrc), ((DefaultFile)vfDst).getJavaFile());
				}
			}
		}
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
		if (paths[0].getPathCount() <= 2)
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
			FileTreeNode node = (FileTreeNode)path.getLastPathComponent();
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
		VirtualFileTransferable transfer = new VirtualFileTransferable(copyFileSet);
		Clipboard clip = getToolkit().getSystemClipboard();
		clip.setContents(transfer, transfer);
		getFrame().updateMenuItem(RunnerMenuResources.ID_EDIT_PASTE);
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
		
		// ノードのチェック
		try {
			TreePath path = _cTree.getSelectionPath();
			FileTreeNode selected = (FileTreeNode)path.getLastPathComponent();
			FileTreeNode ndUserRoot = _cTree.getModel().getUserRootNode();
			if (!selected.isNodeAncestor(ndUserRoot)) {
				// ユーザールート配下のノードでなければ変更不可
				return false;
			}
		} catch (SecurityException ex) {
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
		FileTreeNode selectedNode = (FileTreeNode)selectedPath.getLastPathComponent();
		if (selectedNode.isRoot())
			return;		// root selected
		try {
			if (!selectedNode.isDirectory()) {
				// 選択位置がディレクトリではない場合は、親を対象とする
				selectedPath = selectedPath.getParentPath();
				selectedNode = (FileTreeNode)selectedPath.getLastPathComponent();
				if (selectedNode.isRoot())
					return;		// root selected
			}
		}
		catch (SecurityException ex) {
			// アクセス不可
			return;
		}
		
		// 貼り付け位置がユーザールート以下ではない場合、貼り付けを許可しない。
		if (!selectedNode.getFileObject().isDescendingFrom(_cTree.getUserRootDirectory())) {
			// 選択された場所には貼り付けできない。
			String errmsg = RunnerMessages.getInstance().msgCouldNotPasteToThis;
			AppLogger.error(errmsg + " : \"" + selectedNode.getFileObject().toString() + "\"");
			Application.showErrorMessage(getFrame(), errmsg);
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
			sb.append("[Debug] DataFileTreeView#doPaste()");
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
				Application.showErrorMessage(getFrame(), errmsg);
				return;
			}
			else if (targetDir.isDescendingFrom(srcfile)) {
				String errmsg = String.format(CommonMessages.getInstance().msgCouldNotCopyToSubDirectory,
												targetDir.getName());
				AppLogger.error(errmsg
								+ "\n  source : \"" + String.valueOf(srcfile) + "\""
								+ "\n  target : \"" + targetDir.getPath() + "\"");
				Application.showErrorMessage(getFrame(), errmsg);
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
		TreePath[] selectedPaths = _cTree.getSelectionPaths();
		FileTreeNode ndUserRoot = _cTree.getModel().getUserRootNode();
		TreePath tpUserRoot = new TreePath(ndUserRoot.getPath());
		for (TreePath tp : selectedPaths) {
			if (tp.getPathCount() <= tpUserRoot.getPathCount() || !tpUserRoot.isDescendant(tp)) {
				// ユーザールートの配下のパスではないなら、削除禁止
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
		RunnerFrame frame = getFrame();
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
						((FileTreeNode)selectedPaths[0].getLastPathComponent()).getFilename());
			}
			int retAsk = JOptionPane.showConfirmDialog(getFrame(), confmsg,
					CommonMessages.getInstance().confirmDeleteTitle, JOptionPane.OK_CANCEL_OPTION);
			if (retAsk != JOptionPane.OK_OPTION) {
				// user canceled
				return;
			}
		}

		// 変更されたドキュメントの削除を確認
		IEditorView[] editors = frame.getAllEditors();
		ArrayList<IEditorView> willDeleteList = null;
		if (editors.length > 0) {
			// 開かれているエディタのドキュメントを確認
			StringBuilder sb = new StringBuilder();
			int numWillDeleteModifiedEditors = 0;
			willDeleteList = new ArrayList<IEditorView>(editors.length);
			for (IEditorView ev : editors) {
				File fDocument = ev.getDocumentFile();
				if (fDocument != null) {
					VirtualFile vfDoc = new DefaultFile(fDocument);
					for (TreePath path : selectedPaths) {
						FileTreeNode node = (FileTreeNode)path.getLastPathComponent();
						// 削除対象のファイルに開かれているドキュメントが含まれているかを確認
						if (vfDoc.isDescendingFrom(node.getFileObject())) {
							willDeleteList.add(ev);
							if (ev.isModified()) {
								numWillDeleteModifiedEditors++;
								sb.append("\n    '");
								sb.append(ev.getDocumentTitle());
								sb.append("'");
							}
							break;
						}
					}
				}
			}
			// 変更されたドキュメントを削除するかの確認
			if (numWillDeleteModifiedEditors > 0) {
				String strNames = sb.toString();
				sb.setLength(0);
				sb.append(RunnerMessages.getInstance().confirmDeleteEditingResources);
				sb.append("\n");
				sb.append(strNames);
				int ret = JOptionPane.showConfirmDialog(frame, sb.toString(),
						CommonMessages.getInstance().confirmDeleteTitle, JOptionPane.OK_CANCEL_OPTION);
				if (ret != JOptionPane.OK_OPTION) {
					// user canceled
					return;
				}
			}
		}
		
		// 削除
		_cTree.deleteFiles(frame, selectedPaths);

		// 削除されたドキュメントのエディタを閉じる
		if (willDeleteList != null && !willDeleteList.isEmpty()) {
			for (IEditorView editor : willDeleteList) {
				frame.closeEditorByEditor(true, false, editor);
			}
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	private DataFileTree createTreeComponent() {
		DataFileTree newTree = new DataFileTree(null, _hTree){
			@Override
			protected void onTreeSelectionAdjusted() {
				super.onTreeSelectionAdjusted();
				// このビュー専用の処理
				DataFileTreeView.this.onTreeSelectionAdjusted();
			}
		};
		return newTree;
	}
	
// サブツリーは使用しない：実行時引数設定ダイアログのモードレス化
//	private SubDataFileTree createSubTreeComponent() {
//		SubDataFileTree subTree = new SubDataFileTree(this);
//		return subTree;
//	}
	
	private void setupActions() {
		// Tree
		//--- Mouse listener
		_cTree.addMouseListener(new TreeComponentMouseHandler());
	}

	//------------------------------------------------------------
	// Menu events
	//------------------------------------------------------------

	public boolean onProcessMenuSelection(String command, Object source, Action action) {
		if (RunnerMenuResources.ID_EDIT_COPY.equals(command)) {
			onSelectedMenuEditCopy(action);
			return true;
		}
		else if (RunnerMenuResources.ID_EDIT_PASTE.equals(command)) {
			onSelectedMenuEditPaste(action);
			return true;
		}
		else if (RunnerMenuResources.ID_EDIT_DELETE.equals(command)) {
			onSelectedMenuEditDelete(action);
			return true;
		}
		else if (RunnerMenuResources.ID_TREE_FILE_OPEN.equals(command)) {
			onSelectedMenuTreeFileOpen(action);
			return true;
		}
		else if (RunnerMenuResources.ID_TREE_FILE_OPEN_TYPED_CSV.equals(command)) {
			onSelectedMenuTreeFileOpenTypedCSV(action);
			return true;
		}
		
		// no process
		return false;
	}

	public boolean onProcessMenuUpdate(String command, Object source, Action action) {
		if (RunnerMenuResources.ID_EDIT_COPY.equals(command)) {
			action.setEnabled(canCopy());
			return true;
		}
//		else if (RunnerMenuResources.ID_EDIT_CUT.equals(command)) {
//			// ツリービューでは切り取り操作を許可しない
//			action.setEnabled(false);
//			return true;
//		}
		else if (RunnerMenuResources.ID_EDIT_PASTE.equals(command)) {
			action.setEnabled(canPaste());
			return true;
		}
		else if (RunnerMenuResources.ID_EDIT_DELETE.equals(command)) {
			action.setEnabled(canDelete());
			return true;
		}
		/*
		else if (RunnerMenuResources.ID_FILE_COPYTO.equals(command)) {
			action.setEnabled(viewTree.canCopy());
		}
		*/
		else if (RunnerMenuResources.ID_FILE_MOVETO.equals(command)) {
			action.setEnabled(canMoveTo());
			return true;
		}
		else if (RunnerMenuResources.ID_FILE_RENAME.equals(command)) {
			action.setEnabled(canRename());
			return true;
		}
		else if (RunnerMenuResources.ID_TREE_FILE_OPEN.equals(command)) {
			action.setEnabled(canOpenFile());
			return true;
		}
		else if (RunnerMenuResources.ID_TREE_FILE_OPEN_TYPED_CSV.equals(command)) {
			action.setEnabled(canOpenFileByCSV());
			return true;
		}
		
		// no process
		return false;
	}
	
	// menu : Tree-File-[Open]
	protected void onSelectedMenuTreeFileOpen(Action action) {
		AppLogger.debug("RunnerTreeView : menu Tree-File-[Open] selected.");
		// 選択ファイルを取得
		TreePath[] paths = _cTree.getSelectionPaths();
		if (paths == null || paths.length <= 0) {
			return;
		}
		
		// 表示可能なファイルを表示
		for (TreePath path : paths) {
			VirtualFile file = ((FileTreeNode)path.getLastPathComponent()).getFileObject();
			if (file.exists() && file.isFile()) {
				getFrame().openEditorFromFile(((DefaultFile)file).getJavaFile());
			}
		}
	}
	
	// menu : Tree-File-[Open by Type]-[CSV]
	protected void onSelectedMenuTreeFileOpenTypedCSV(Action action) {
		AppLogger.debug("RunnerTreeView : menu Tree-File-[Open by type]-[CSV] selected.");
		// 選択ファイルを取得
		TreePath path = _cTree.getSelectionPath();
		if (path == null) {
			return;
		}
		
		// ファイルを取得
		VirtualFile file = ((FileTreeNode)path.getLastPathComponent()).getFileObject();
		if (file.exists() && file.isFile()) {
			getFrame().openEditorFromFileWithConfigCSV(((DefaultFile)file).getJavaFile());
		}
	}
	
	// menu : [Edit]-[Copy]
	protected void onSelectedMenuEditCopy(Action action) {
		AppLogger.debug("RunnerTreeView : menu [Edit]-[Copy] selected.");
		if (!canCopy()) {
			action.setEnabled(false);
			return;
		}
		doCopy();
		requestFocusInComponent();
	}
	
	// menu : [Edit]-[Paste]
	protected void onSelectedMenuEditPaste(Action action) {
		AppLogger.debug("RunnerTreeView : menu [Edit]-[Paste] selected.");
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
		AppLogger.debug("RunnerTreeView : menu [Edit]-[Delete] selected.");
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
	
	protected void onTreeSelectionAdjusted() {
		// メニューの更新
		RunnerFrame frame = getFrame();
		if (frame != null) {
			frame.updateMenuItem(RunnerMenuResources.ID_FILE_NEW);
			//frame.updateMenuItem(RunnerMenuResources.ID_FILE_NEW_FOLDER);
			frame.updateMenuItem(RunnerMenuResources.ID_TREE_FILE_MENU);
			frame.updateMenuItem(RunnerMenuResources.ID_FILE_MOVETO);
			frame.updateMenuItem(RunnerMenuResources.ID_FILE_RENAME);
			frame.updateMenuItem(RunnerMenuResources.ID_EDIT_MENU);
//			frame.updateMenuItem(RunnerMenuResources.ID_RUN_MENU);
			frame.updateMenuItem(RunnerMenuResources.ID_FILTER_MENU);
		}

		// 単一選択なら、ファイルのプロパティを表示
		frame.updateStatusBarMessage();
	}

	/**
	 * ツリーノードが左ボタンでダブルクリックされたときのイベントハンドラ
	 */
	protected void onTreeDoubleClicked(MouseEvent e) {
		// マウス位置のノードを取得
		//--- ダブルクリック後、マウスイベントの位置情報から対象パスを取得すると、
		//--- ツリーがスクロールした場合に想定していない位置が取得され、そのファイルを開くことに
		//--- なってしまうので、ダブルクリック後は単一選択になることを前提に、
		//--- 位置から取得したパスと、選択されているパスが一致した場合のみ、
		//--- そのパスのファイルを開く
		TreePath pathByPos = _cTree.getPathForLocation(e.getX(), e.getY());
		TreePath pathBySel = _cTree.getSelectionPath();	// ダブルクリック後は単一選択を前提
		if (pathByPos == null || !Objects.isEqual(pathByPos, pathBySel))
			return;		// no target node
		
		// 表示可能なファイルを表示
		VirtualFile file = ((FileTreeNode)pathByPos.getLastPathComponent()).getFileObject();
		if (!file.exists() || !file.isFile())
			return;		// not file;
		getFrame().openEditorFromFile(((DefaultFile)file).getJavaFile());
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
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
				RunnerMenuBar menuBar = getFrame().getActiveMainMenuBar();
				if (menuBar != null) {
					menuBar.getDataFileTreeContextMenu().show(me.getComponent(), me.getX(), me.getY());
				}
			}
		}
	}

	/**
	 * ツリーコンポーネントの制御を行うハンドラ
	 */
	protected class LocalTreeHandler extends DataFileTree.DefaultDataFileTreeHandler
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
