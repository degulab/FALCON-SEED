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
 * @(#)EditorTreeView.java	3.1.0	2014/05/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)EditorTreeView.java	1.17	2011/02/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)EditorTreeView.java	1.14	2009/12/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.aadl.common.PackageBaseSettings;
import ssac.aadl.editor.AADLEditor;
import ssac.aadl.editor.EditorMessages;
import ssac.aadl.editor.plugin.IComponentManager;
import ssac.aadl.editor.setting.AppSettings;
import ssac.aadl.editor.view.dialog.BuildOptionDialog;
import ssac.aadl.editor.view.menu.EditorMenuResources;
import ssac.aadl.editor.view.tree.PackageImportProgressMonitor;
import ssac.aadl.manager.swing.dialog.PackageBaseChooser;
import ssac.aadl.module.ModuleFileManager;
import ssac.aadl.module.ModuleFileTransferable;
import ssac.aadl.module.setting.AadlJarProfile;
import ssac.aadl.module.setting.AbstractSettings;
import ssac.aadl.module.setting.PackageSettings;
import ssac.aadl.module.swing.FileDialogManager;
import ssac.aadl.module.swing.FilenameValidator;
import ssac.aadl.module.swing.tree.ModuleFileTreeModel;
import ssac.aadl.module.swing.tree.ModuleFileTreeNode;
import ssac.aadl.module.swing.tree.ModuleFolderChooser;
import ssac.aadl.module.swing.tree.ModuleTree;
import ssac.aadl.module.swing.tree.PackageFileChooser;
import ssac.util.Objects;
import ssac.util.Strings;
import ssac.util.io.DefaultFile;
import ssac.util.io.Files;
import ssac.util.io.VirtualFile;
import ssac.util.logging.AppLogger;
import ssac.util.swing.IDialogResult;
import ssac.util.swing.PackageRegistProgressMonitor;
import ssac.util.swing.SwingTools;
import ssac.util.swing.menu.IMenuActionHandler;
import ssac.util.swing.tree.DnDTree;

/**
 * エディタのツリービュー
 * 
 * @version 3.1.0	2014/05/19
 * @since 1.14
 */
public class EditorTreeView extends JPanel implements IMenuActionHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	private final EditorTreeHandler _hTree = new EditorTreeHandler();

	/** ツリーコンポーネント **/
	private final ModuleTree _cTree;
	/** ワークスペースラベル **/
	private final JLabel	_cWSLabel;
	/** 分割ペイン **/
	private final JSplitPane	_cSplit;
	/** ファイル情報パネル **/
	private final ModulePropertyPanel	_cPropertyPane;
	
	/** ワークスペースの位置 **/
	private VirtualFile	_workspace;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 標準のコンストラクタ
	 */
	public EditorTreeView() {
		super(new BorderLayout());
		this._cTree = createTreeComponent();
		this._cWSLabel = createWorkspaceLabel();
		this._cPropertyPane = new ModulePropertyPanel();
		this._cSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		initialComponent();
		setupActions();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public void adjustPropertyDivider() {
		int divloc = _cSplit.getDividerLocation();
		int maxdivloc = _cSplit.getSize().height
						 - _cPropertyPane.getMinimumSize().height
						 - _cSplit.getDividerSize();
		if (divloc > maxdivloc) {
			_cSplit.setDividerLocation(maxdivloc);
		}
	}
	
	public int getDividerLocation() {
		return _cSplit.getDividerLocation();
	}
	
	public void setDividerLocation(int newLocation) {
		_cSplit.setDividerLocation(newLocation);
	}

	/**
	 * ツリーコンポーネントに設定されているツリーモデルを取得する。
	 */
	public ModuleFileTreeModel getTreeModel() {
		return _cTree.getModel();
	}

	/**
	 * ツリーコンポーネントに設定されているツリーモデルのルートノードを取得する。
	 * ルートノードが存在しない場合は <tt>null</tt> を返す。
	 */
	public ModuleFileTreeNode getTreeRootNode() {
		return _cTree.getRootNode();
	}
	
	public VirtualFile getWorkspaceRoot() {
		return _workspace;
	}
	
	public void setWorkspaceRoot(VirtualFile file) {
		// check
		if (!file.exists())
			throw new IllegalArgumentException("Workspace root is not exist : \"" + file.toString() + "\"");
		else if (!file.isDirectory())
			throw new IllegalArgumentException("Workspace root is not directory : \"" + file.toString() + "\"");
		else if (!file.canWrite())
			throw new IllegalArgumentException("Workspace root cannot write : \"" + file.toString() + "\"");
		
		// create model
		if (_workspace != null) {
			if (!_workspace.equals(file)) {
				_workspace = file;
				ModuleFileTreeModel newModel = new ModuleFileTreeModel(file);
				_cTree.setModel(newModel);
				_cWSLabel.setText(file.getName());
				_cWSLabel.setToolTipText(file.getPath());
			}
		} else {
			_workspace = file;
			ModuleFileTreeModel newModel = new ModuleFileTreeModel(file);
			_cTree.setModel(newModel);
			_cWSLabel.setText(file.getName());
			_cWSLabel.setToolTipText(file.getPath());
		}
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
	
	/**
	 * このビューを格納するエディタフレームを取得する。
	 * @return	このビューを格納するエディタフレームのインスタンス。
	 * 			このビューがフレームに格納されていない場合は <tt>null</tt> を返す。
	 */
	public EditorFrame getFrame() {
		Window parentFrame = SwingUtilities.windowForComponent(this);
		if (parentFrame instanceof EditorFrame)
			return (EditorFrame)parentFrame;
		else
			return null;
	}

	/**
	 * モジュール情報パネルに表示されている情報を必要に応じて更新する。
	 * このメソッドは、情報を保持する設定ファイルが情報読み込み時より
	 * 更新されている場合にのみ、表示の更新を行う。
	 */
	public void refreshModuleProperties() {
		if (_cTree.getSelectionCount() == 1) {
			TreePath path = _cTree.getSelectionPath();
			_cPropertyPane.setTargetFile((ModuleFileTreeNode)path.getLastPathComponent());
		} else {
			_cPropertyPane.setTargetFile(null);
		}
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
	 * 指定された抽象パスの最上位プロジェクトからツリー表示を更新する。
	 * 最上位プロジェクトが見つからない場合は、ルートノードを起点とする。
	 * @param targetFile	プロジェクト内の抽象パス
	 */
	public void refreshFileTreeFromProject(VirtualFile targetFile) {
		TreePath path = _cTree.getModel().getTreePathForFile(targetFile);
		if (path != null) {
			TreePath lastProjPath = null;
			TreePath parent = path;
			do {
				ModuleFileTreeNode node = (ModuleFileTreeNode)parent.getLastPathComponent();
				if (node.isProjectRoot()) {
					lastProjPath = parent;
				}
			} while ((parent = parent.getParentPath()) != null);
			if (lastProjPath != null) {
				_cTree.refreshTree(lastProjPath);
			} else {
				refreshAllTree();
			}
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
	 * 現在選択されている抽象パスがプロジェクトに登録可能な状態であれば <tt>true</tt> を返す。
	 * プロジェクト登録可能な状態とは、次の通り。
	 * <ul>
	 * <li>ルートノードでもプロジェクトルートでもないこと</li>
	 * <li>プロジェクトに登録されていないこと(除外されていること)</li>
	 * </ul>
	 * @return	選択されているすべての抽象パスがプロジェクト登録可能状態であれば <tt>true</tt> を返す。
	 */
	public boolean canRegisterProject() {
		// 選択されていなければ、許可しない
		if (_cTree.isSelectionEmpty())
			return false;
		
		// 選択ノードをチェック
		try {
			TreePath[] paths = _cTree.getSelectionPaths();
			for (TreePath path : paths) {
				ModuleFileTreeNode node = (ModuleFileTreeNode)path.getLastPathComponent();
				//--- ルートやプロジェクトではないこと
				if (node.isRoot() || node.isProjectRoot())
					return false;
				//--- ルート直下のファイルではないこと
				if (node.getParent().isRoot() && node.isFile())
					return false;
				//--- プロジェクト階層下であること
				ModuleFileTreeNode projNode = ModuleFileTreeNode.getTopProjectRootNode(node);
				if (projNode == null || projNode.isRoot())
					return false;
				//--- プロジェクトに登録されていても使用可能とする
			}
		} catch (SecurityException ex) {
			return false;
		}
		
		// 登録可能
		return true;
	}

	/**
	 * 現在選択されている抽象パスがプロジェクトから除外可能な状態であれば <tt>true</tt> を返す。
	 * プロジェクト除外可能な状態とは、次の通り。
	 * <ul>
	 * <li>ルートノードでもプロジェクトルートでもないこと</li>
	 * <li>プロジェクトに登録されていること(除外されていないこと)</li>
	 * </ul>
	 * @return	選択されているすべての抽象パスがプロジェクト除外可能状態であれば <tt>true</tt> を返す。
	 */
	public boolean canUnregisterProject() {
		// 選択されていなければ、許可しない
		if (_cTree.isSelectionEmpty())
			return false;
		
		// 選択ノードをチェック
		try {
			TreePath[] paths = _cTree.getSelectionPaths();
			for (TreePath path : paths) {
				ModuleFileTreeNode node = (ModuleFileTreeNode)path.getLastPathComponent();
				//--- ルートやプロジェクトではないこと
				if (node.isRoot() || node.isProjectRoot())
					return false;
				//--- ルート直下のファイルではないこと
				if (node.getParent().isRoot() && node.isFile())
					return false;
				//--- プロジェクト階層下であること
				ModuleFileTreeNode projNode = ModuleFileTreeNode.getTopProjectRootNode(node);
				if (projNode == null || projNode.isRoot())
					return false;
				//--- プロジェクトから除外されていても使用可能とする
			}
		} catch (SecurityException ex) {
			return false;
		}
		
		// 登録可能
		return true;
	}

	/**
	 * ファイルやフォルダの新規作成を許可する状態であれば <tt>true</tt> を返す。
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
		try {
			TreePath path = _cTree.getSelectionPath();
			ModuleFileTreeNode selected = (ModuleFileTreeNode)path.getLastPathComponent();
			//--- ルートではないこと
			if (selected.isRoot())
				return false;
			//--- ルート直下のファイルではないこと
			if (selected.getParent().isRoot() && selected.isFile())
				return false;
			//--- パッケージ内のファイルやフォルダではないこと
			if (selected.isAssignedModulePackageFile())
				return false;
			//--- パッケージルートではないこと
			if (!selected.isProjectRoot() && selected.isModulePackageRoot())
				return false;
		} catch (SecurityException ex) {
			return false;
		}
		
		// allow create
		return true;
	}

	/**
	 * 選択されているノードについて、オプションダイアログが表示可能かを
	 * 検証する。現時点で、オプションダイアログが表示可能なものは次の通り。
	 * <ul>
	 * <li>AADLソースファイル</li>
	 * <li>AADLマクロファイル</li>
	 * <li>モジュールパッケージルート</li>
	 * <li>プロジェクトルート</li>
	 * </ul>
	 * @return	オプションダイアログが表示可能であれば <tt>true</tt> を返す。
	 * 			そうでない場合は <tt>false</tt> を返す。
	 */
	public boolean canShowBuildOption() {
		// 選択されているノードが単一でない場合は、取得できない
		if (_cTree.getSelectionCount() != 1) {
			return false;
		}
		
		// 選択されているノードのファイルタイプを判定
		try {
			TreePath path = _cTree.getSelectionPath();
			ModuleFileTreeNode node = (ModuleFileTreeNode)path.getLastPathComponent();
			if (node.isProjectRoot()) {
				return true;
			}
			else if (node.isModulePackageRoot()) {
				return true;
			}
			//--- ファイル拡張子で判定
			VirtualFile file = node.getFileObject();
			if (ModuleFileManager.isAadlSourceFile(file)) {
				return true;
			}
			else if (ModuleFileManager.isAadlMacroFile(file)) {
				return true;
			}
		} catch (SecurityException ex) {
			return false;
		}
		
		// 該当なし
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
		ModuleFolderChooser chooser = new ModuleFolderChooser(getFrame(),
											false,		// root は非表示
											false,		// フォルダ作成を許可しない
											_workspace, initParent,
											dlgTitle, null, dlgTreeLabel,
											null, validator);
		
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
	 * プロジェクトを新規に作成するためのプロジェクト名入力ダイアログを表示し、
	 * 入力されたプロジェクト名で新規プロジェクトを作成する。
	 * @return	正常に作成できた場合は <tt>true</tt>、そうでない場合は <tt>false</tt> を返す
	 */
	public boolean doCreateProject() {
		EditorFrame frame = getFrame();
		
		// プロジェクト名入力ダイアログの表示
		String strNewName = FileDialogManager.showCreateProjectDialog(frame, _workspace);
		if (Strings.isNullOrEmpty(strNewName)) {
			return false;	// user canceled!
		}
		
		// プロジェクトの作成
		TreePath tpProj = _cTree.createProject(frame, strNewName);
		return (tpProj != null);
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
				AADLEditor.showErrorMessage(getFrame(), errmsg);
				return false;
			}
		}
		
		// ディレクトリ名入力ダイアログの表示
		EditorFrame frame = getFrame();
		String dlgTitle = CommonMessages.getInstance().InputTitle_NewFolder;
		String dlgTreeLabel = CommonMessages.getInstance().TreeLabel_SelectParentFolder;
		String dlgInputLabel = CommonMessages.getInstance().InputLabel_FolderName;
		VirtualFile initParent = null;
		if (selectedPath != null) {
			initParent = ((ModuleFileTreeNode)selectedPath.getLastPathComponent()).getFileObject();
		}
		FilenameValidator validator = FileDialogManager.createDefaultFilenameValidator();
		ModuleFolderChooser chooser = new ModuleFolderChooser(frame,
															false,		// root は非表示
															false,		// フォルダ作成を許可しない
															_workspace, initParent,
															dlgTitle, null, dlgTreeLabel,
															dlgInputLabel, validator);
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
	 * ファイルを新規に作成するためのファイル名入力ダイアログを表示し、
	 * 入力されたファイル名で新規ファイルを作成する。
	 * @param manager	生成するファイルタイプを保持するマネージャを指定する。
	 * 					<tt>null</tt> の場合は、単純なファイルを生成する。
	 * @return	作成したファイルの抽象パスを返す。
	 * 			ユーザーにより中断された場合やファイルが作成できなかった
	 * 			場合は <tt>null</tt> を返す。
	 */
	public VirtualFile doCreateFile(IComponentManager manager) {
		// 作成可能な状態でないなら、処理しない
		if (!canCreate()) {
			return null;
		}
		
		// ファイル作成位置の確認
		TreePath selectedPath = _cTree.getSelectionPath();
		if (selectedPath != null) {
			VirtualFile packfile = _cTree.findAssignedModulePackageFile(true, selectedPath);
			if (packfile != null) {
				// モジュールパッケージは変更不可
				String errmsg = CommonMessages.getInstance().msgCouldNotModifyFilesInPackage;
				AppLogger.error(errmsg + " : \"" + packfile.getPath() + "\"");
				AADLEditor.showErrorMessage(getFrame(), errmsg);
				return null;
			}
		}
		
		// ファイル名入力ダイアログの表示
		EditorFrame frame = getFrame();
		String dlgTreeLabel = CommonMessages.getInstance().TreeLabel_SelectParentFolder;
		String dlgInputLabel = CommonMessages.getInstance().InputLabel_FileName;
		VirtualFile initParent = null;
		if (selectedPath != null) {
			initParent = ((ModuleFileTreeNode)selectedPath.getLastPathComponent()).getFileObject();
		}
		ModuleFolderChooser chooser;
		if (manager == null) {
			String dlgTitle = CommonMessages.getInstance().InputTitle_NewFile;
			FilenameValidator validator = FileDialogManager.createDefaultFilenameValidator();
			chooser = new ModuleFolderChooser(getFrame(),
											false,		// root は非表示
											false,		// フォルダ作成を許可しない
											_workspace, initParent,
											dlgTitle, null, dlgTreeLabel,
											dlgInputLabel, validator);
		} else {
			String dlgTitle = CommonMessages.getInstance().InputTitle_NewObject + " " + manager.getName();
			dlgInputLabel = manager.getName() + " " + dlgInputLabel;
			dlgInputLabel = dlgInputLabel + " " + CommonMessages.getInstance().InputLabel_OmitExtension;
			FilenameValidator validator = FileDialogManager.createDefaultFilenameValidator();
			validator.setExtensionFilters(manager.getSaveFileFilters());
			chooser = new ModuleFolderChooser(getFrame(),
											false,		// root は非表示
											false,		// フォルダ作成を許可しない
											_workspace, initParent,
											dlgTitle, null, dlgTreeLabel,
											dlgInputLabel, validator);
		}
		chooser.pack();
		chooser.setVisible(true);
		chooser.dispose();
		if (chooser.getDialogResult() != IDialogResult.DialogResult_OK) {
			// user canceled!
			return null;
		}
		
		// ファイルの作成
		String strNewName = chooser.getFilteredInputFieldText();
		VirtualFile parentDir = chooser.getSelectionFile();
		TreePath tpParentDir = _cTree.getModel().getTreePathForFile(parentDir);
		if (tpParentDir == null)
			throw new AssertionError("TreeNode not found : \"" + parentDir.toString() + "\"");
		TreePath tpNewFile = _cTree.createFile(frame, tpParentDir, strNewName);
		if (tpNewFile != null) {
			return ((ModuleFileTreeNode)tpNewFile.getLastPathComponent()).getFileObject();
		} else {
			return null;
		}
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
		EditorFrame frame = getFrame();
		TreePath selectedPath = _cTree.getSelectionPath();
		ModuleFileTreeNode selectedNode = (ModuleFileTreeNode)selectedPath.getLastPathComponent();
		
		// モジュールパッケージの確認
		if (selectedPath != null) {
			VirtualFile packfile = _cTree.findAssignedModulePackageFile(true, selectedPath);
			if (packfile != null) {
				// モジュールパッケージは変更不可
				String errmsg = CommonMessages.getInstance().msgCouldNotModifyFilesInPackage;
				AppLogger.error(errmsg + " : \"" + packfile.getPath() + "\"");
				AADLEditor.showErrorMessage(getFrame(), errmsg);
				return;
			}
		}
		
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
			VirtualFile vfRoot = _cTree.getModel().getRootFile();
			boolean denyMove = false;
			StringBuilder sb = new StringBuilder();
			sb.append(CommonMessages.getInstance().msgCouldNotRenameOpenedEditingFile);
			for (Map.Entry<VirtualFile, IEditorView> entry : willMoveMap.entrySet()) {
				if (!entry.getValue().canMoveDocumentFile()) {
					denyMove = true;
					String filepath;
					if (entry.getKey().isDescendingFrom(vfRoot)) {
						filepath = Files.CommonSeparator + entry.getKey().relativePathFrom(vfRoot, Files.CommonSeparatorChar);
					} else {
						filepath = entry.getKey().getAbsolutePath();
					}
					sb.append("\n  ");
					sb.append(filepath);
				}
			}
			
			if (denyMove) {
				String errmsg = sb.toString();
				AppLogger.error(errmsg);
				AADLEditor.showErrorMessage(getFrame(), errmsg);
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
		for (Map.Entry<VirtualFile, VirtualFile> entry : watchMap.entrySet()) {
			VirtualFile vfSrc = entry.getKey();
			VirtualFile vfDst = entry.getValue();
			if (vfDst != null) {
				frame.renameToEditorFile(willMoveMap.get(vfSrc), ((DefaultFile)vfDst).getJavaFile());
			}
		}
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
		EditorFrame frame = getFrame();
		ModuleFileTreeModel model = _cTree.getModel();
		TreePath[] selectedPaths = _cTree.getSelectionPaths();
		
		// モジュールパッケージの確認
		if (selectedPaths != null) {
			VirtualFile packfile = _cTree.findAssignedModulePackageFile(false, selectedPaths);
			if (packfile != null) {
				// モジュールパッケージは変更不可
				String errmsg = CommonMessages.getInstance().msgCouldNotMoveFilesInPackage;
				AppLogger.error(errmsg + " : \"" + packfile.getPath() + "\"");
				AADLEditor.showErrorMessage(getFrame(), errmsg);
				return;
			}
		}
		
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
						ModuleFileTreeNode node = (ModuleFileTreeNode)path.getLastPathComponent();
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
			VirtualFile vfRoot = _cTree.getModel().getRootFile();
			boolean denyMove = false;
			StringBuilder sb = new StringBuilder();
			sb.append(CommonMessages.getInstance().msgCouldNotMoveOpenedEditingFile);
			for (Map.Entry<VirtualFile, IEditorView> entry : willMoveMap.entrySet()) {
				if (!entry.getValue().canMoveDocumentFile()) {
					denyMove = true;
					String filepath;
					if (entry.getKey().isDescendingFrom(vfRoot)) {
						filepath = Files.CommonSeparator + entry.getKey().relativePathFrom(vfRoot, Files.CommonSeparatorChar);
					} else {
						filepath = entry.getKey().getAbsolutePath();
					}
					sb.append("\n  ");
					sb.append(filepath);
				}
			}
			
			if (denyMove) {
				String errmsg = sb.toString();
				AppLogger.error(errmsg);
				AADLEditor.showErrorMessage(getFrame(), errmsg);
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
				AADLEditor.showErrorMessage(getFrame(), errmsg);
				return;
			}
		}
		
		// 移動
		Map<VirtualFile,VirtualFile> watchMap = _cTree.moveFiles(frame, selectedPaths,
													tpTargetDir, willMoveMap.keySet());
		
		// 変更されたドキュメントの保存先を更新
		for (Map.Entry<VirtualFile, VirtualFile> entry : watchMap.entrySet()) {
			VirtualFile vfSrc = entry.getKey();
			VirtualFile vfDst = entry.getValue();
			if (vfDst != null) {
				frame.renameToEditorFile(willMoveMap.get(vfSrc), ((DefaultFile)vfDst).getJavaFile());
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
		getFrame().updateMenuItem(EditorMenuResources.ID_EDIT_PASTE);
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
			ModuleFileTreeNode selected = (ModuleFileTreeNode)path.getLastPathComponent();
			//--- ルートではないこと
			if (selected.isRoot())
				return false;
			//--- ルート直下のファイルではないこと
			if (selected.getParent().isRoot() && selected.isFile())
				return false;
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
			AADLEditor.showErrorMessage(getFrame(), errmsg);
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
			sb.append("[Debug] EditorTreeView#doPaste()");
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
				AADLEditor.showErrorMessage(getFrame(), errmsg);
				return;
			}
			else if (targetDir.isDescendingFrom(srcfile)) {
				String errmsg = String.format(CommonMessages.getInstance().msgCouldNotCopyToSubDirectory,
												targetDir.getName());
				AppLogger.error(errmsg
								+ "\n  source : \"" + String.valueOf(srcfile) + "\""
								+ "\n  target : \"" + targetDir.getPath() + "\"");
				AADLEditor.showErrorMessage(getFrame(), errmsg);
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
		EditorFrame frame = getFrame();
		TreePath[] selectedPaths = _cTree.getSelectionPaths();
		
		// モジュールパッケージの確認
		if (selectedPaths != null) {
			VirtualFile packfile = _cTree.findAssignedModulePackageFile(false, selectedPaths);
			if (packfile != null) {
				// モジュールパッケージは変更不可
				String errmsg = CommonMessages.getInstance().msgCouldNotDeleteFilesInPackage;
				AppLogger.error(errmsg + " : \"" + packfile.getPath() + "\"");
				AADLEditor.showErrorMessage(getFrame(), errmsg);
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
						ModuleFileTreeNode node = (ModuleFileTreeNode)path.getLastPathComponent();
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
				sb.append(EditorMessages.getInstance().confirmDeleteEditingResources);
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
	
	protected boolean canPackageRegist() {
		// 単一のプロジェクトが選択されている場合のみ許可する
		if (_cTree.getSelectionCount() != 1) {
			return false;
		}
		
		TreePath path = _cTree.getSelectionPath();
		ModuleFileTreeNode node = (ModuleFileTreeNode)path.getLastPathComponent();
		if (!node.isProjectRoot()) {
			return false;
		}
		ModuleFileTreeNode topProj = ModuleFileTreeNode.getTopProjectRootNode(node);
		if (node != topProj) {
			return false;		// 最上位プロジェクト以外は選択不可
		}
		
		return true;
	}

	/**
	 * プロジェクトをパッケージとして登録する。
	 */
	protected void doPackageRegist() {
		if (_cTree.getSelectionCount() != 1) {
			return;
		}
		
		TreePath path = _cTree.getSelectionPath();
		ModuleFileTreeNode node = (ModuleFileTreeNode)path.getLastPathComponent();
		if (!node.isProjectRoot()) {
			return;
		}
		ModuleFileTreeNode topProj = ModuleFileTreeNode.getTopProjectRootNode(node);
		if (node != topProj) {
			return;		// 最上位プロジェクト以外は選択不可
		}
		
		// 登録位置を取得
		EditorFrame frame = getFrame();
		VirtualFile rootDir = choosePackageBaseLocation(frame, true);
		if (rootDir == null) {
			// user canceled
			return;
		}
		
		// パッケージ登録ダイアログを表示
		PackageFileChooser chooser = showPackageRegistDialog(frame,
													rootDir, null, node.getFilename());
		if (chooser.getDialogResult() != IDialogResult.DialogResult_OK) {
			// user canceled
			//--- このダイアログでフォルダが作成されても、エディタのツリービューには
			//--- 影響しないので、そのまま終了
			return;
		}
		
		// 作成位置情報を取得
		VirtualFile parentDir = chooser.getSelectionFile();
		String newName = chooser.getFilteredInputFieldText();
		
		// プロジェクトをパッケージに変換し登録
		PackageRegistProgressMonitor task = new PackageRegistProgressMonitor(node.getFileObject(),
														node.getProjectProperties(),
														parentDir, newName);
		task.setSourceBaseDirectory(getWorkspaceRoot());
		task.setTargetBaseDirectory(null);
		task.execute(frame);
		task.showError(frame);
	}
	
	protected boolean canPackageImport() {
		// 常に許可する
		return true;
	}

	/**
	 * パッケージをプロジェクトとしてインポートする
	 */
	protected void doPackageImport() {
		// 登録位置を取得
		EditorFrame frame = getFrame();
		VirtualFile rootDir = choosePackageBaseLocation(frame, true);
		if (rootDir == null) {
			// user canceled
			return;
		}
		
		// パッケージインポートダイアログを表示
		PackageFileChooser chooser = showPackageImportDialog(frame,
													rootDir, null, getWorkspaceRoot());
		if (chooser.getDialogResult() != IDialogResult.DialogResult_OK) {
			// user canceled
			return;
		}
		
		// 作成位置情報を取得
		VirtualFile packRoot = chooser.getSelectionFile();
		PackageSettings packSettings = new PackageSettings();
		packSettings.loadForTarget(ModuleFileManager.getModulePackagePrefsFile(packRoot));
		String newName = chooser.getFilteredInputFieldText();
		
		// パッケージをプロジェクトに変換し登録
		PackageImportProgressMonitor task = new PackageImportProgressMonitor(packRoot,
											packSettings, getWorkspaceRoot(), newName);
		task.setSourceBaseDirectory(null);
		task.setTargetBaseDirectory(getWorkspaceRoot());
		task.execute(frame);
		task.showError(frame);
		_cTree.getModel().refreshChildren();
	}
	
	protected boolean canPackageInsert() {
		// 単一選択の場合のみ許可する
		if (_cTree.getSelectionCount() != 1) {
			return false;
		}
		
		// ノードのチェック
		try {
			TreePath path = _cTree.getSelectionPath();
			ModuleFileTreeNode selected = (ModuleFileTreeNode)path.getLastPathComponent();
			//--- ルートではないこと
			if (selected.isRoot())
				return false;
			//--- ルート直下のファイルではないこと
			if (selected.getParent().isRoot() && selected.isFile())
				return false;
		} catch (SecurityException ex) {
			return false;
		}
		
		// 参照モジュールパッケージの名前変更は、変更できないことを通知する
		// メッセージを表示するため、ここではスルー
		
		// allow rename
		return true;
	}

	/**
	 * パッケージの参照コピーを挿入
	 */
	protected void doPackageInsert() {
		// 単一選択の場合のみ許可する
		if (_cTree.getSelectionCount() != 1) {
			return;
		}
		
		// ノードのチェック
		TreePath selectedPath = _cTree.getSelectionPath();
		ModuleFileTreeNode selectedNode = (ModuleFileTreeNode)selectedPath.getLastPathComponent();
		if (selectedNode.isRoot())
			return;
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
			AADLEditor.showErrorMessage(getFrame(), errmsg);
			return;
		}
		
		// 登録位置を取得
		EditorFrame frame = getFrame();
		VirtualFile rootDir = choosePackageBaseLocation(frame, true);
		if (rootDir == null) {
			// user canceled
			return;
		}
		
		// パッケージ選択ダイアログを表示
		VirtualFile packFile = choosePackageRoot(frame, rootDir, null);
		if (packFile == null) {
			// user canceled
			return;
		}
		
		// パッケージのコピー
		_cTree.copyFiles(frame, new VirtualFile[]{packFile}, selectedPath);
	}
	
	private JLabel createWorkspaceLabel() {
		JLabel label = new JLabel();
		label.setIcon(ModuleFileManager.getJavaFileSystemDisplayIcon());
		return label;
	}

	private ModuleTree createTreeComponent() {
		ModuleTree newTree = new ModuleTree(null, _hTree){
			@Override
			protected void onTreeSelectionAdjusted() {
				super.onTreeSelectionAdjusted();
				// このビュー専用の処理
				EditorTreeView.this.onEditorTreeSelectionAdjusted();
			}
		};
		return newTree;
	}
	
	private void initialComponent() {
		// setup Workspace Label
		_cWSLabel.setFocusable(false);
		
		// setup Property pane
		_cSplit.setResizeWeight(1);
		
		// setup Main component
		JScrollPane sc = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sc.setViewportView(_cTree);
		_cSplit.setTopComponent(sc);
		_cSplit.setBottomComponent(_cPropertyPane);
		this.add(_cSplit, BorderLayout.CENTER);
		this.add(_cWSLabel, BorderLayout.NORTH);
	}
	
	private void setupActions() {
		// Tree
		//--- Mouse listener
		_cTree.addMouseListener(new TreeComponentMouseHandler());
		
		// Workspace Label
		_cWSLabel.addMouseListener(new WorkspaceLabelMouseHandler());
		
		// View
	}
	
	protected void initialSetup(EditorFrame frame) {
		// メニューアクセラレータキーを有効にするため、
		// ツリーコンポーネントのマップを変更
		JMenuBar menuBar = frame.getDefaultEditorMenuBar();
		KeyStroke[] strokes = SwingTools.getMenuAccelerators(menuBar);
		_cTree.registMenuAcceleratorKeyStroke(strokes);
	}
	
	/**
	 * 指定されたノードに関連付けられる設定情報をファイルから読み込み、
	 * その設定情報オブジェクトを返す。現時点で、設定情報ファイルを
	 * 取得できるものは次の通り。
	 * <ul>
	 * <li>AADLソースファイル</li>
	 * <li>AADLマクロファイル</li>
	 * <li>モジュールパッケージルート</li>
	 * <li>プロジェクトルート</li>
	 * </ul>
	 * <p>このメソッドは、設定情報ファイルが存在しなくても、
	 * 設定情報を取得可能なファイルやディレクトリの場合は、新規の
	 * 設定情報オブジェクトを返す。
	 * @param node	設定情報を取得する対象となる抽象パス
	 * @return	設定情報オブジェクトを返す。設定情報オブジェクトが
	 * 			取得できない場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	protected AbstractSettings getModuleFileSettings(ModuleFileTreeNode node) {
		if (node.isProjectRoot()) {
			return node.getProjectProperties();
		}
		else if (node.isModulePackageRoot()) {
			return node.getModulePackageProperties();
		}
		//--- ファイル拡張子で判定
		VirtualFile file = node.getFileObject();
		/*---
		if (ModuleFileManager.isAadlSourceFile(file)) {
			VirtualFile fPrefs = ModuleFileManager.addPrefsExtension(file);
			CompileSettings setting = new CompileSettings();
			setting.loadForTarget(fPrefs);
			return setting;
		}
		else if (ModuleFileManager.isAadlMacroFile(file)) {
			VirtualFile fPrefs = ModuleFileManager.addPrefsExtension(file);
			MacroExecSettings setting = new MacroExecSettings();
			setting.loadForTarget(fPrefs);
			return setting;
		}
		else if (ModuleFileManager.isJarFile(file)) {
			VirtualFile fAadlSrc = ModuleFileManager.replaceExtensionToAadlSource(file);
			if (fAadlSrc.exists()) {
				VirtualFile fPrefs = ModuleFileManager.addPrefsExtension(fAadlSrc);
				CompileSettings setting = new CompileSettings();
				setting.loadForTarget(fPrefs);
				return setting;
			}
		}
		---*/
		return ModuleFileManager.getModuleFileSettings(file);
	}

	/**
	 * 選択されているパッケージベース(モジュールマネージャの基準パス)を取得する。
	 * パッケージベースが未選択の場合は、選択ダイアログを表示する。
	 * @param parentComponent	ダイアログの親となるコンポーネント
	 * @param showAlways		選択の有無に関係なく、常に選択ダイアログを表示する
	 * 							場合は <tt>true</tt> を指定する。
	 * @return	選択されたパッケージベースの抽象パスを返す。
	 * 			未選択の場合は <tt>null</tt> を返す。
	 */
	protected VirtualFile choosePackageBaseLocation(Frame parentComponent,
														boolean showAlways)
	{
		// 選択済みであれば表示しない場合は、選択状態を取得する
		if (!showAlways) {
			PackageBaseSettings.getInstance().rollback();
			URI uri = PackageBaseSettings.getInstance().getLastPackageBase();
			if (uri != null) {
				VirtualFile vfBase = ModuleFileManager.fromURI(uri);
				if (vfBase != null && PackageBaseChooser.verifyPackageBase(getFrame(), false, vfBase)) {
					return vfBase;
				}
			}
		}
		
		// パッケージベース選択ダイアログの表示
		PackageBaseChooser chooser = new PackageBaseChooser(null,
											AppSettings.PACKBASE_CHOOSER,
											AppSettings.getInstance().getConfiguration());
		chooser.setVisible(true);
		chooser.dispose();
		if (chooser.getDialogResult() != IDialogResult.DialogResult_OK) {
			return null;	// user canceled
		}
		URI uri = PackageBaseSettings.getInstance().getLastPackageBase();
		return ModuleFileManager.fromURI(uri);
	}

	/**
	 * パッケージを選択するためのファイル選択ダイアログを表示する。
	 * @param parentComponent	親コンポーネント
	 * @param rootDir			選択範囲とするルートディレクトリ
	 * @param initialSelection	初期選択位置
	 * @return	選択したファイルを返す。そうでない場合は <tt>null</tt> を返す。
	 */
	protected VirtualFile choosePackageRoot(Component parentComponent,
						VirtualFile rootDir, VirtualFile initialSelection)
	{
		String dlgTitle = CommonMessages.getInstance().Choose_Package;
		String dlgTreeLabel = null;
		
		// インスタンス生成
		Window owner = SwingTools.getWindowForComponent(parentComponent);
		PackageFileChooser chooser;
		if (owner instanceof Dialog) {
			chooser = new PackageFileChooser((Dialog)owner,
							true, false, rootDir, initialSelection,
							dlgTitle, null, dlgTreeLabel, null,
							PackageFileChooser.getDefaultPackageFileFilter(),
							null);
		} else {
			chooser = new PackageFileChooser((Frame)owner,
							true, false, rootDir, initialSelection,
							dlgTitle, null, dlgTreeLabel, null,
							PackageFileChooser.getDefaultPackageFileFilter(),
							null);
		}
		
		// 表示
		chooser.setFileSelectionMode(PackageFileChooser.FILE_ONLY);
		chooser.setAllowAssignedModulePackageFileSelection(false);
		chooser.pack();
		chooser.setVisible(true);
		chooser.dispose();
		if (chooser.getDialogResult() == IDialogResult.DialogResult_OK) {
			return chooser.getSelectionFile();
		} else {
			return null;
		}
	}

	/**
	 * インポートするパッケージを選択するダイアログを表示する。
	 * このダイアログでは、インポート対象のパッケージ選択と、
	 * インポート時のプロジェクト名入力フィールドが表示される。
	 * このプロジェクト名入力フィールドは、ワークスペースの
	 * プロジェクト名と重複する名前の場合に警告するバリデータが設定される。
	 * @param parentComponent	親コンポーネント
	 * @param rootDir			選択範囲とするルートディレクトリ
	 * @param initialSelection	初期選択位置
	 * @param workspaceRoot		ワークスペースのルートディレクトリ
	 * @return	生成された PckageFileChooser オブジェクトを返す。
	 */
	protected PackageFileChooser showPackageImportDialog(Component parentComponent,
															VirtualFile rootDir,
															VirtualFile initialSelection,
															VirtualFile workspaceRoot)
	{
		String dlgTitle = EditorMessages.getInstance().PackImportDlg_Title;
		String dlgTreeLabel = EditorMessages.getInstance().PackImportDlg_TreeLabel;
		String dlgInputLabel = EditorMessages.getInstance().PackImportDlg_InputLabel;
		FilenameValidator validator = FileDialogManager.createPackageImportValidator(workspaceRoot);
		
		// インスタンス生成
		Window owner = SwingTools.getWindowForComponent(parentComponent);
		PackageFileChooser chooser;
		if (owner instanceof Dialog) {
			chooser = new PackageFileChooser((Dialog)owner,
							true, false, rootDir, initialSelection,
							dlgTitle, null, dlgTreeLabel, dlgInputLabel,
							PackageFileChooser.getDefaultPackageFileFilter(),
							validator);
		} else {
			chooser = new PackageFileChooser((Frame)owner,
							true, false, rootDir, initialSelection,
							dlgTitle, null, dlgTreeLabel, dlgInputLabel,
							PackageFileChooser.getDefaultPackageFileFilter(),
							validator);
		}
		
		// 表示
		chooser.setFileSelectionMode(PackageFileChooser.FILE_ONLY);
		chooser.setAllowAssignedModulePackageFileSelection(false);
		chooser.pack();
		chooser.setVisible(true);
		chooser.dispose();
		
		return chooser;
	}

	/**
	 * パッケージを登録するダイアログを表示する。
	 * このダイアログ内でフォルダ作成も許可するため、選択状態に
	 * 関わらず、ダイアログが閉じられた時点で、ダイアログのインスタンスを返す。
	 * @param parentComponent	親コンポーネント
	 * @param rootDir			選択範囲とするルートディレクトリ
	 * @param initialSelection	初期選択位置
	 * @param initialInputText	初期入力テキスト
	 * @return	生成された PackageFileChooser オブジェクトを返す。
	 */
	protected PackageFileChooser showPackageRegistDialog(Component parentComponent,
															VirtualFile rootDir,
															VirtualFile initialSelection,
															String initialInputText)
	{
		String dlgTitle = EditorMessages.getInstance().PackRegistDlg_Title;
		String dlgTreeLabel = EditorMessages.getInstance().PackRegistDlg_TreeLabel;
		String dlgInputLabel = EditorMessages.getInstance().PackRegistDlg_InputLabel;
		FilenameValidator validator = FileDialogManager.createDefaultFilenameValidator();
		
		// インスタンス生成
		Window owner = SwingTools.getWindowForComponent(parentComponent);
		PackageFileChooser chooser;
		if (owner instanceof Dialog) {
			chooser = new PackageFileChooser((Dialog)owner,
							true, true, rootDir, initialSelection,
							dlgTitle, null, dlgTreeLabel, dlgInputLabel,
							PackageFileChooser.getDefaultPackageFileFilter(),
							validator);
		} else {
			chooser = new PackageFileChooser((Frame)owner,
							true, true, rootDir, initialSelection,
							dlgTitle, null, dlgTreeLabel, dlgInputLabel,
							PackageFileChooser.getDefaultPackageFileFilter(),
							validator);
		}
		
		// 表示
		if (!Strings.isNullOrEmpty(initialInputText)) {
			chooser.setInputFieldText(initialInputText);
		}
		chooser.setFileSelectionMode(PackageFileChooser.FOLDER_ONLY);
		chooser.setAllowAssignedModulePackageFileSelection(false);
		chooser.pack();
		chooser.setVisible(true);
		chooser.dispose();
		
		return chooser;
	}

	//------------------------------------------------------------
	// Menu events
	//------------------------------------------------------------

	public boolean onProcessMenuSelection(String command, Object source, Action action) {
		if (EditorMenuResources.ID_EDIT_COPY.equals(command)) {
			onSelectedMenuEditCopy(action);
			return true;
		}
		else if (EditorMenuResources.ID_EDIT_PASTE.equals(command)) {
			onSelectedMenuEditPaste(action);
			return true;
		}
		else if (EditorMenuResources.ID_EDIT_DELETE.equals(command)) {
			onSelectedMenuEditDelete(action);
			return true;
		}
		else if (EditorMenuResources.ID_FILE_PROJECT_REGISTER.equals(command)) {
			onSelectedMenuFileProjectRegister(action);
			return true;
		}
		else if (EditorMenuResources.ID_FILE_PROJECT_UNREGISTER.equals(command)) {
			onSelectedMenuFileProjectUnregister(action);
			return true;
		}
		else if (EditorMenuResources.ID_FILE_PACKAGE_REGIST.equals(command)) {
			onSelectedMenuFilePackagePost(action);
			return true;
		}
		else if (EditorMenuResources.ID_FILE_PACKAGE_REFER.equals(command)) {
			onSelectedMenuFilePackageRefer(action);
			return true;
		}
		else if (EditorMenuResources.ID_FILE_PACKAGE_IMPORT.equals(command)) {
			onSelectedMenuFilePackageImport(action);
			return true;
		}
		else if (EditorMenuResources.ID_BUILD_OPTION.equals(command)) {
			onSelectedMenuBuildOption(action);
			return true;
		}
		
		// no process
		return false;
	}

	public boolean onProcessMenuUpdate(String command, Object source, Action action) {
		if (EditorMenuResources.ID_EDIT_CUT.equals(command)) {
			// ツリービューでは切り取り操作を許可しない
			action.setEnabled(false);
			return true;
		}
		else if (EditorMenuResources.ID_EDIT_COPY.equals(command)) {
			action.setEnabled(canCopy());
			return true;
		}
		else if (EditorMenuResources.ID_EDIT_PASTE.equals(command)) {
			action.setEnabled(canPaste());
			return true;
		}
		else if (EditorMenuResources.ID_EDIT_DELETE.equals(command)) {
			action.setEnabled(canDelete());
			return true;
		}
		/*
		else if (EditorMenuResources.ID_FILE_COPYTO.equals(command)) {
			action.setEnabled(viewTree.canCopy());
		}
		*/
		else if (EditorMenuResources.ID_FILE_MOVETO.equals(command)) {
			action.setEnabled(canMoveTo());
			return true;
		}
		else if (EditorMenuResources.ID_FILE_RENAME.equals(command)) {
			action.setEnabled(canRename());
			return true;
		}
		else if (EditorMenuResources.ID_FILE_PROJECT_REGISTER.equals(command)) {
			action.setEnabled(canRegisterProject());
			return true;
		}
		else if (EditorMenuResources.ID_FILE_PROJECT_UNREGISTER.equals(command)) {
			action.setEnabled(canUnregisterProject());
			return true;
		}
		else if (EditorMenuResources.ID_FILE_PACKAGE_REGIST.equals(command)) {
			action.setEnabled(canPackageRegist());
			return true;
		}
		else if (EditorMenuResources.ID_FILE_PACKAGE_REFER.equals(command)) {
			action.setEnabled(canPackageInsert());
			return true;
		}
		else if (EditorMenuResources.ID_FILE_PACKAGE_IMPORT.equals(command)) {
			action.setEnabled(canPackageImport());
			return true;
		}
		else if (EditorMenuResources.ID_BUILD_OPTION.equals(command)) {
			action.setEnabled(canShowBuildOption());
			return true;
		}
		
		// no process
		return false;
	}
	
	// menu : [Edit]-[Copy]
	protected void onSelectedMenuEditCopy(Action action) {
		AppLogger.debug("EditorTreeView : menu [Edit]-[Copy] selected.");
		if (!canCopy()) {
			action.setEnabled(false);
			return;
		}
		doCopy();
		requestFocusInComponent();
	}
	
	// menu : [Edit]-[Paste]
	protected void onSelectedMenuEditPaste(Action action) {
		AppLogger.debug("EditorTreeView : menu [Edit]-[Paste] selected.");
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
		AppLogger.debug("EditorTreeView : menu [Edit]-[Delete] selected.");
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
	
	// menu : [File]-[Register to Project]
	protected void onSelectedMenuFileProjectRegister(Action action) {
		AppLogger.debug("EditorTreeView : menu [File]-[Register to Project] selected.");
		if (!canRegisterProject()) {
			action.setEnabled(false);
			return;
		}
		
		TreePath[] paths = _cTree.getSelectionPaths();
		if (_cTree.registerToProject(paths)) {
			// プロジェクト構成が変更されたら、メニューを更新
			EditorFrame frame = getFrame();
			if (frame != null) {
				frame.updateMenuItem(EditorMenuResources.ID_FILE_PROJECT_REGISTER);
				frame.updateMenuItem(EditorMenuResources.ID_FILE_PROJECT_UNREGISTER);
			}
		}
		requestFocusInComponent();
	}
	
	// menu : [File]-[Unregister from Project]
	protected void onSelectedMenuFileProjectUnregister(Action action) {
		AppLogger.debug("EditorTreeView : menu [File]-[Unregister to Project] selected.");
		if (!canUnregisterProject()) {
			action.setEnabled(false);
			return;
		}
		
		TreePath[] paths = _cTree.getSelectionPaths();
		if (_cTree.unregisterFromProject(paths)) {
			// プロジェクト構成が変更されたら、メニューを更新
			EditorFrame frame = getFrame();
			if (frame != null) {
				frame.updateMenuItem(EditorMenuResources.ID_FILE_PROJECT_REGISTER);
				frame.updateMenuItem(EditorMenuResources.ID_FILE_PROJECT_UNREGISTER);
			}
		}
		requestFocusInComponent();
	}
	
	// menu : [File]-[Package]-[Register]
	protected void onSelectedMenuFilePackagePost(Action action) {
		AppLogger.debug("EditorTreeView : menu [File]-[Package]-[Register] selected.");
		doPackageRegist();
		requestFocusInComponent();
	}
	
	// menu : [File]-[Package]-[Refer]
	protected void onSelectedMenuFilePackageRefer(Action action) {
		AppLogger.debug("EditorTreeView : menu [File]-[Package]-[Refer] selected.");
		doPackageInsert();
		requestFocusInComponent();
	}
	
	// menu : [File]-[Package]-[Import]
	protected void onSelectedMenuFilePackageImport(Action action) {
		AppLogger.debug("EditorTreeView : menu [File]-[Package]-[Import] selected.");
		doPackageImport();
		requestFocusInComponent();
	}
	
	// menu : [Build] - [Option]
	protected void onSelectedMenuBuildOption(Action action) {
		AppLogger.debug("EditorTreeView : menu [Build]-[Option] selected.");
		EditorFrame frame = getFrame();
		if (_cTree.getSelectionCount() != 1) {
			// 単一選択ではないので、処理しない
			return;
		}
		TreePath path = _cTree.getSelectionPath();
		ModuleFileTreeNode node = (ModuleFileTreeNode)path.getLastPathComponent();
		AbstractSettings setting = getModuleFileSettings(node);
		if (setting != null) {
			if (AppLogger.isDebugEnabled()) {
				String msg = String.format("EditorTreeView#onSelectedMenuBuildOption : show BuildOptionDialog for file[%s]",
											String.valueOf(node.getFileObject()));
				AppLogger.debug(msg);
			}
			setting.refresh();	// 最新の情報に更新
			BuildOptionDialog dlg = new BuildOptionDialog(setting,
											frame.isReadOnlyFile(node.getFileObject()),
											frame);
			dlg.setVisible(true);
			if (AppLogger.isDebugEnabled()) {
				int dlgResult = dlg.getDialogResult();
				AppLogger.debug("Dialog result : " + dlgResult);
			}
			requestFocusInComponent();
		}
	}

	//------------------------------------------------------------
	// Action events
	//------------------------------------------------------------
	
	protected void onEditorTreeSelectionAdjusted() {
		// メニューの更新
		EditorFrame frame = getFrame();
		if (frame != null) {
			frame.updateMenuItem(EditorMenuResources.ID_FILE_NEW);
			//frame.updateMenuItem(EditorMenuResources.ID_FILE_NEW_FOLDER);
			frame.updateMenuItem(EditorMenuResources.ID_FILE_MOVETO);
			frame.updateMenuItem(EditorMenuResources.ID_FILE_RENAME);
			frame.updateMenuItem(EditorMenuResources.ID_FILE_PROJECT_REGISTER);
			frame.updateMenuItem(EditorMenuResources.ID_FILE_PROJECT_UNREGISTER);
			frame.updateMenuItem(EditorMenuResources.ID_FILE_PACKAGE_REGIST);
			frame.updateMenuItem(EditorMenuResources.ID_FILE_PACKAGE_REFER);
			frame.updateMenuItem(EditorMenuResources.ID_EDIT_MENU);
			frame.updateMenuItem(EditorMenuResources.ID_BUILD_OPTION);
		}
		
		// 単一選択なら、ファイルのプロパティを表示
		refreshModuleProperties();
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
		
		// 開けるファイルなら、エディタで開く
		VirtualFile file = ((ModuleFileTreeNode)pathByPos.getLastPathComponent()).getFileObject();
		if (!file.exists() || !file.isFile())
			return;		// not file
		getFrame().openEditorFromFile(((DefaultFile)file).getJavaFile());
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	protected class WorkspaceLabelMouseHandler extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			// このラベルをクリックしたら、ツリーにフォーカスを設定
			requestFocusInComponent();
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
				EditorMenuBar menuBar = getFrame().getActiveEditorMenuBar();
				if (menuBar != null) {
					menuBar.getTreeHeaderContextMenu().show(me.getComponent(), me.getX(), me.getY());
				}
			}
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
				EditorMenuBar menuBar = getFrame().getActiveEditorMenuBar();
				if (menuBar != null) {
					menuBar.getTreeViewContextMenu().show(me.getComponent(), me.getX(), me.getY());
				}
			}
		}
	}
	
	protected class ModulePropertyPanel extends JPanel
	{
		private final JLabel		_cName;
		private final JScrollPane	_cScroll;
		private final JEditorPane	_cInfo;
		
		public ModulePropertyPanel() {
			super(new BorderLayout());
			this._cName = new JLabel();
			this._cInfo = new JEditorPane();
			this._cScroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
											 JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			setupComponents();
		}
		
		private void setupComponents() {
			// Label
			_cName.setIcon(CommonResources.ICON_BLANK);
			_cName.setText(" ");
			
			// Info
			_cInfo.setContentType("text/html");
			_cInfo.setBackground(this.getBackground());
			_cInfo.setEditable(false);
			_cScroll.setViewportView(_cInfo);
			
			// layout
			this.add(_cName, BorderLayout.NORTH);
			this.add(_cScroll, BorderLayout.CENTER);
			
			// update minimum size
			_cInfo.setMinimumSize(new Dimension(0,0));
			_cScroll.setMinimumSize(_cInfo.getMinimumSize());
			//this.setMinimumSize(_cName.getMinimumSize());
			this.setMinimumSize(_cName.getPreferredSize());
		}
		
		public void setTargetFile(ModuleFileTreeNode node) {
			// ファイルの有無を確認
			if (node == null) {
				_cName.setIcon(CommonResources.ICON_BLANK);
				_cName.setText("");
				_cInfo.setText("");
				return;
			}

			// ファイルアクセス
			boolean canAccess;
			Icon dispIcon;
			try {
				if (node.exists()) {
					dispIcon = ModuleFileManager.getDisplayIcon(node.getFilename(),
							node.isProjectRoot(), node.isModulePackageRoot(),
							node.isDirectory(), node.isFile(), false, false);
					canAccess = true;
				} else {
					canAccess = false;
					dispIcon = null;
				}
			} catch (Throwable ex) {
				// アクセス不可
				canAccess = false;
				dispIcon = null;
			}
			
			// Show properties
			if (!canAccess) {
				_cName.setText(node.getFilename());
				_cName.setIcon(CommonResources.ICON_DELETE);
				
				// Remove properties
				_cInfo.setText("");
			} else {
				_cName.setText(node.getFilename());
				_cName.setIcon(dispIcon == null ? CommonResources.ICON_BLANK : dispIcon);
				
				// Show properties
				if (ModuleFileManager.isJarFile(node.getFileObject())) {
					// Jar ファイル専用の解析
					AadlJarProfile jarprop = null;
					try {
						jarprop = new AadlJarProfile(node.getFileObject());
					}
					catch (Throwable ex) {
						AppLogger.debug("EditroTreeView.ModulePropertyPanel#setTargetFile("
										+ node.getFileObject().getPath() + ")", ex);
					}
					//--- create HTML
					StringBuilder sb = new StringBuilder();
					sb.append("<html><body>");
					ModuleFileManager.appendDisplayJarFilePropertyBlock(sb, node.getFileObject(), jarprop);
					sb.append("</body></html>");
					_cInfo.setText(sb.toString());
					_cInfo.setCaretPosition(0);
				}
				else {
					// another file types
					AbstractSettings setting = getModuleFileSettings(node);
					if (setting != null) {
						setting.refresh();
					}
					StringBuilder sb = new StringBuilder();
					sb.append("<html><body>");
					ModuleFileManager.appendDisplayFilePropertyBlock(sb, node.getFileObject(), setting);
					sb.append("</body></html>");
					_cInfo.setText(sb.toString());
					_cInfo.setCaretPosition(0);
				}
			}
		}
	}

	/**
	 * ツリーコンポーネントの制御を行うハンドラ
	 * 
	 * @version 1.14	2009/12/09
	 * @since 1.14
	 */
	protected class EditorTreeHandler extends ModuleTree.DefaultTreeHandler
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
			// 貼り付け位置の取得
			TreePath selectedPath = targetPath;
			ModuleFileTreeNode selectedNode = (ModuleFileTreeNode)selectedPath.getLastPathComponent();
			if (selectedNode.isRoot())
				return false;		// root selected
			if (!selectedNode.isDirectory()) {
				// 選択位置がディレクトリではない場合は、親を対象とする
				selectedPath = selectedPath.getParentPath();
				selectedNode = (ModuleFileTreeNode)selectedPath.getLastPathComponent();
				if (selectedNode.isRoot())
					return false;		// root selected
			}
			
			// モジュールパッケージの確認
			VirtualFile packfile = tree.findAssignedModulePackageFile(true, new TreePath[]{selectedPath});
			if (packfile != null) {
				// モジュールパッケージは変更不可
				String errmsg = CommonMessages.getInstance().msgCouldNotModifyPackageStructure;
				AppLogger.error(errmsg + " : \"" + packfile.getPath() + "\"");
				AADLEditor.showErrorMessage(tree.getFrame(), errmsg);
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
					AADLEditor.showErrorMessage(tree.getFrame(), errmsg);
					return false;
				}
				else if (targetDir.isDescendingFrom(srcfile)) {
					String errmsg = String.format(CommonMessages.getInstance().msgCouldNotCopyToSubDirectory,
													targetDir.getName());
					AppLogger.error(errmsg
									+ "\n  source : \"" + String.valueOf(srcfile) + "\""
									+ "\n  target : \"" + targetDir.getPath() + "\"");
					AADLEditor.showErrorMessage(tree.getFrame(), errmsg);
					return false;
				}
			}
			
			// paste
			_cTree.copyFiles(tree.getFrame(), sourceFiles, selectedPath);
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
					AADLEditor.showErrorMessage(tree.getFrame(), errmsg);
					return false;
				}
			}
			
			// 移動先を取得
			ModuleFileTreeNode targetNode = (ModuleFileTreeNode)targetPath.getLastPathComponent();
			if (targetNode.isRoot())
				return false;	// root cannot target
			if (!targetNode.isDirectory()) {
				// 選択位置がディレクトリではない場合は、親を対象とする
				targetPath = targetPath.getParentPath();
				targetNode = (ModuleFileTreeNode)targetPath.getLastPathComponent();
				if (targetNode.isRoot())
					return false;		// root selected
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
					AADLEditor.showErrorMessage(tree.getFrame(), errmsg);
					return false;
				}
			}
			
			// 移動するドキュメントを収集
			EditorFrame frame = (EditorFrame)tree.getFrame();
			IEditorView[] editors = frame.getAllEditors();
			HashMap<VirtualFile, IEditorView> willMoveMap = new HashMap<VirtualFile, IEditorView>();
			if (editors.length > 0) {
				// 開かれているエディタのドキュメントを確認
				for (IEditorView ev : editors) {
					File fDocument = ev.getDocumentFile();
					if (fDocument != null) {
						VirtualFile vfDoc = new DefaultFile(fDocument);
						for (TreePath path : selectedPaths) {
							ModuleFileTreeNode node = (ModuleFileTreeNode)path.getLastPathComponent();
							// 移動対象のファイルに開かれているドキュメントが含まれているかを確認
							if (vfDoc.isDescendingFrom(node.getFileObject())) {
								willMoveMap.put(vfDoc, ev);
								break;
							}
						}
					}
				}
			}
			
			// 移動
			Map<VirtualFile,VirtualFile> watchMap = _cTree.moveFiles(frame, selectedPaths,
														targetPath, willMoveMap.keySet());
			
			// 変更されたドキュメントの保存先を更新
			for (Map.Entry<VirtualFile, VirtualFile> entry : watchMap.entrySet()) {
				VirtualFile vfSrc = entry.getKey();
				VirtualFile vfDst = entry.getValue();
				if (vfDst != null) {
					frame.renameToEditorFile(willMoveMap.get(vfSrc), ((DefaultFile)vfDst).getJavaFile());
				}
			}
			
			// 完了
			return true;
		}
		--- end of test ---*/
	}
}
