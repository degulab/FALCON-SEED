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
 * @(#)MExecDefTreeView.java	3.2.1	2015/07/08
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecDefTreeView.java	3.2.0	2015/06/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecDefTreeView.java	3.1.0	2014/05/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecDefTreeView.java	2.0.0	2012/11/03
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecDefTreeView.java	1.22	2012/08/22
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecDefTreeView.java	1.20	2012/03/26
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecDefTreeView.java	1.10	2011/02/15
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.runner.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;

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
import ssac.falconseed.file.DefaultVirtualFilePathFormatter;
import ssac.falconseed.file.VirtualFilePathFormatterList;
import ssac.falconseed.filter.common.gui.FilterEditModel;
import ssac.falconseed.filter.generic.gui.GenericFilterEditDialog;
import ssac.falconseed.module.MExecDefFileManager;
import ssac.falconseed.module.MExecDefFileTransferable;
import ssac.falconseed.module.ZipDecodeOperationException;
import ssac.falconseed.module.ZipFileDecodeHandler;
import ssac.falconseed.module.ZipFileDecoder;
import ssac.falconseed.module.ZipFileEncodeHandler;
import ssac.falconseed.module.ZipFileEncoder;
import ssac.falconseed.module.ZipIllegalChecksumException;
import ssac.falconseed.module.swing.MExecDefEditDialog;
import ssac.falconseed.module.swing.MacroFilterEditDialog;
import ssac.falconseed.module.swing.MacroFilterEditModel;
import ssac.falconseed.module.swing.tree.IMExecDefFile;
import ssac.falconseed.module.swing.tree.MExecDefDataFile;
import ssac.falconseed.module.swing.tree.MExecDefExportDialog;
import ssac.falconseed.module.swing.tree.MExecDefFolderChooser;
import ssac.falconseed.module.swing.tree.MExecDefImportDialog;
import ssac.falconseed.module.swing.tree.MExecDefTree;
import ssac.falconseed.module.swing.tree.MExecDefTreeModel;
import ssac.falconseed.module.swing.tree.MExecDefTreeNode;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.menu.RunnerMenuBar;
import ssac.falconseed.runner.menu.RunnerMenuResources;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.util.Objects;
import ssac.util.Strings;
import ssac.util.io.Files;
import ssac.util.io.VirtualFile;
import ssac.util.logging.AppLogger;
import ssac.util.swing.Application;
import ssac.util.swing.IDialogResult;
import ssac.util.swing.ProgressMonitorTask;
import ssac.util.swing.SwingTools;
import ssac.util.swing.tree.DnDTree;

/**
 * モジュールランナーのツリービュー
 * 
 * @version 3.2.1
 * @since 1.10
 */
public class MExecDefTreeView extends JPanel implements IRunnerTreeView
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	private final TreeHandler _hTree = new TreeHandler();

	/** ツリーコンポーネント **/
	private MExecDefTree _cTree;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 標準のコンストラクタ
	 */
	public MExecDefTreeView() {
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
		MExecDefTreeModel newModel = new MExecDefTreeModel(systemRootDir, userRootDir);
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

	/**
	 * 現在の設定による、ツリーのパス表示名整形オブジェクトを取得する。
	 * @return	新しいパス表示名整形オブジェクト
	 * @since 1.22
	 */
	public VirtualFilePathFormatterList getTreePathFormatter() {
		VirtualFilePathFormatterList vfFormatter = new VirtualFilePathFormatterList();
		
		//--- System root dir
		MExecDefTreeNode ndSystemRoot = _cTree.getModel().getSystemRootNode();
		if (ndSystemRoot != null) {
			DefaultVirtualFilePathFormatter formatter = new DefaultVirtualFilePathFormatter(ndSystemRoot.getFileObject(), ndSystemRoot.getDisplayName());
			vfFormatter.add(formatter);
		}
		//--- User root dir
		MExecDefTreeNode ndUserRoot = _cTree.getModel().getUserRootNode();
		if (ndUserRoot != null) {
			DefaultVirtualFilePathFormatter formatter = new DefaultVirtualFilePathFormatter(ndUserRoot.getFileObject(), ndUserRoot.getDisplayName());
			vfFormatter.add(formatter);
		}
		
		return vfFormatter;
	}

	/**
	 * ツリーコンポーネントに設定されているツリーモデルを取得する。
	 */
	public MExecDefTreeModel getTreeModel() {
		return _cTree.getModel();
	}
	
	public MExecDefTree getTreeComponent() {
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
	
	public VirtualFile getSelectionMExecDefPrefsFile() {
		return _cTree.getSelectionMExecDefPrefsFile();
	}
	
	public VirtualFile[] getSelectionMExecDefPrefsFiles() {
		return _cTree.getSelectionMExecDefPrefsFiles();
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
	 * 選択されているノードについて、モジュール実行定義の表示が可能かを判定する。
	 * @return	表示が可能なモジュール実行定義が選択されている場合は <tt>true</tt>、
	 * 			そうでない場合は <tt>false</tt>
	 */
	public boolean canShowMExecDef() {
		// 選択されているノードが単一でない場合は、表示も編集もできない
		if (_cTree.getSelectionCount() != 1) {
			return false;
		}
		
		// ノードを取得
		TreePath tp = _cTree.getSelectionPath();
		MExecDefTreeNode node = (MExecDefTreeNode)tp.getLastPathComponent();
		if (!node.isExecDefData()) {
			// no module execution definition
			return false;
		}
		
		// 表示可能
		return true;
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
		MExecDefTreeNode ndUserRoot = _cTree.getModel().getUserRootNode();
		if (ndUserRoot == null || !ndUserRoot.exists() || !ndUserRoot.isDirectory()) {
			return false;
		}
		
		// ノードのチェック
		try {
			TreePath path = _cTree.getSelectionPath();
			MExecDefTreeNode selected = (MExecDefTreeNode)path.getLastPathComponent();
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
	 * モジュール実行定義の新規作成を許可する状態であれば <tt>true</tt> を返す。
	 * モジュール実行定義が新規作成可能な状態は次の通り。
	 * <ul>
	 * <li>単一のノードが選択されている</li>
	 * <li>選択されているノードがルートノードではない場合</li>
	 * </ul>
	 */
	public boolean canCreateMExecDef() {
		if (_cTree.getSelectionCount() != 1) {
			// 単一選択ではない
			return false;
		}
		
		// ユーザールートの存在チェック
		MExecDefTreeNode ndUserRoot = _cTree.getModel().getUserRootNode();
		if (ndUserRoot == null || !ndUserRoot.exists() || !ndUserRoot.isDirectory()) {
			return false;
		}
		
		// ノードのチェック
		try {
			TreePath path = _cTree.getSelectionPath();
			MExecDefTreeNode selected = (MExecDefTreeNode)path.getLastPathComponent();
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
	 * 汎用フィルタの新規作成を許可する状態であれば <tt>true</tt> を返す。
	 * 汎用フィルタが新規作成可能な状態は次の通り。
	 * <ul>
	 * <li>単一のノードが選択されている</li>
	 * <li>選択されているノードがルートノードではない場合</li>
	 * </ul>
	 * <p><b>注：</b>
	 * <blockquote>
	 * 現在の実装では、{@link #canCreateMExecDef()} と同じ機能とする。
	 * </blockquote>
	 * @since 3.2.0
	 */
	public boolean canCreateGenericFilter() {
		return canCreateMExecDef();
	}
	
	/**
	 * フィルタマクロ定義の新規作成を許可する状態であれば <tt>true</tt> を返す。
	 * フィルタマクロ定義が新規作成可能な状態は次の通り。
	 * <ul>
	 * <li>単一のノードが選択されている</li>
	 * <li>選択されているノードがルートノードではない場合</li>
	 * </ul>
	 * <p><b>注：</b>
	 * <blockquote>
	 * 現在の実装では、{@link #canCreateMExecDef()} と同じ機能とする。
	 * </blockquote>
	 * @since 2.0.0
	 */
	public boolean canCreateFilterMacro() {
		return canCreateMExecDef();
	}

	/**
	 * 指定されたモジュール実行定義ルートノードが、読み込み専用かを判定する。
	 * 次の場合、読み込み専用と断定する。
	 * <ul>
	 * <lu>モジュール実行定義がシステムルートディレクトリ以下の場合
	 * <lu>モジュール実行定義がユーザールートディレクトリ以下ではない場合
	 * <lu>モジュール実行定義のルートディレクトリが書き込み禁止の場合
	 * <lu>モジュール実行定義の定義ファイルが書き込み禁止の場合
	 * </ul>
	 * @param node	判定するモジュール実行定義のルートとなるノード
	 * @return	読み込み専用なら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws SecurityException	<code>canWrite</code> メソッドの呼び出しが許可されていない場合
	 * @since	1.20
	 */
	protected boolean isMExecDefReadOnly(MExecDefTreeNode node) {
		assert node.isExecDefData();
		
		if (node.getFileObject().isDescendingFrom(_cTree.getModel().getUserRootNode().getFileObject())) {
			if (node.getFileObject().isDescendingFrom(_cTree.getModel().getSystemRootNode().getFileObject())) {
				// システムルート以下のモジュール実行定義は編集不可
				return true;
			}
		} else {
			// ユーザールート以下のモジュール実行定義ではない場合は、編集不可
			return true;
		}
		
		// 書き込み可能かをチェック
		if (!node.getFileObject().canWrite()) {
			return true;
		}
		else if (!((MExecDefDataFile)node.getUserObject()).getDataFile().canWrite()) {
			return true;
		}
		
		// 変更可能
		return false;
	}

	/**
	 * 選択されているモジュール実行定義の内容を表示する。
	 * モジュール実行定義が選択されていない場合は、何もしない。
	 */
	public void doShowMExecDef() {
		// 選択されているノードが単一でない場合は、表示も編集もできない
		if (_cTree.getSelectionCount() != 1) {
			return;
		}
		
		// モジュール実行定義の表示
		TreePath tp = _cTree.getSelectionPath();
		if (tp != null) {
			doShowMExecDefEditDialog(tp);
		}
	}

	/**
	 * 指定されたツリーパスの終端に位置するノードについて、
	 * モジュール実行定義編集ダイアログを表示する。
	 * このメソッドでは、モジュール実行定義が編集可能かどうかを判定し、
	 * 編集可能な場合は編集可能なダイアログを、編集不可能な場合は参照専用のダイアログを
	 * 表示する。
	 * ツリーパスの終端がモジュール実行定義ではない場合、このメソッドは何も行わない。
	 * @param treepath	参照するノードを示すツリーパス
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	protected void doShowMExecDefEditDialog(TreePath treepath) {
		// ノードを取得
		MExecDefTreeNode node = (MExecDefTreeNode)treepath.getLastPathComponent();
		if (!node.isExecDefData()) {
			// no Module Execution Definition
			return;
		}

		// モジュール実行定義をチェック
		boolean readOnly = false;
		try {
			readOnly = isMExecDefReadOnly(node);
		}
		catch (SecurityException ex) {
			String errmsg = CommonMessages.formatErrorMessage(
								CommonMessages.getInstance().msgCouldNotAccessFile,
								ex, node.getFileObject().toString());
			AppLogger.error(errmsg, ex);
			Application.showErrorMessage(getFrame(), errmsg);
			return;
		}
		
		// ダイアログを表示
		VirtualFile vfParent = null;
		MExecDefTreeNode ndRoot = (MExecDefTreeNode)treepath.getPathComponent(1);
		if (node.isExecDefGenericFilter()) {
			// 汎用フィルタの編集(@since 3.2.1)
			FilterEditModel.EditType etype = (readOnly ? FilterEditModel.EditType.VIEW : FilterEditModel.EditType.MODIFY);
			GenericFilterEditDialog dlg;
			dlg = new GenericFilterEditDialog(getFrame());
			dlg.setConfiguration(AppSettings.GENERICFILTER_EDIT_DLG, AppSettings.getInstance().getConfiguration());
			try {
				if (!dlg.initialComponent(etype, getFrame().getMExecDefPathFormatter(), ndRoot.getDisplayName(), ndRoot.getFileObject(), node.getFileObject())) {
					// error
					return;
				}
			}
			catch (SecurityException ex) {
				// ファイルへのアクセス不可
				String errmsg = CommonMessages.formatErrorMessage(
									CommonMessages.getInstance().msgCouldNotAccessFile,
									ex, node.getFileObject().toString());
				AppLogger.error(errmsg, ex);
				Application.showErrorMessage(getFrame(), errmsg);
				return;
				
			}
			catch (Throwable ex) {
				// その他のエラー
				String errmsg = CommonMessages.formatErrorMessage(
									RunnerMessages.getInstance().msgMExecDefCouldNotOpen,
									ex, node.getFileObject().toString());
				AppLogger.error(errmsg, ex);
				Application.showErrorMessage(getFrame(), errmsg);
				return;
			}
			dlg.setVisible(true);
			dlg.dispose();
			if (dlg.getDialogResult() != IDialogResult.DialogResult_OK) {
				// user canceled!
				return;
			}
			vfParent = dlg.getParentDirectory();
		}
		else if (node.isExecDefFilterMacro()) {
			// フィルタマクロの編集(@since 2.0.0)
			MacroFilterEditModel.EditType etype = (readOnly ? MacroFilterEditModel.EditType.VIEW : MacroFilterEditModel.EditType.MODIFY);
			MacroFilterEditDialog dlg;
			dlg = new MacroFilterEditDialog(getFrame());
			dlg.setConfiguration(AppSettings.MACROFILTER_EDIT_DLG, AppSettings.getInstance().getConfiguration());
			try {
				if (!dlg.initialComponent(etype, getFrame().getMExecDefPathFormatter(), ndRoot.getDisplayName(), ndRoot.getFileObject(), node.getFileObject(), null)) {
					// error
					return;
				}
			}
			catch (SecurityException ex) {
				// ファイルへのアクセス不可
				String errmsg = CommonMessages.formatErrorMessage(
									CommonMessages.getInstance().msgCouldNotAccessFile,
									ex, node.getFileObject().toString());
				AppLogger.error(errmsg, ex);
				Application.showErrorMessage(getFrame(), errmsg);
				return;
				
			}
			catch (Throwable ex) {
				// その他のエラー
				String errmsg = CommonMessages.formatErrorMessage(
									RunnerMessages.getInstance().msgMExecDefCouldNotOpen,
									ex, node.getFileObject().toString());
				AppLogger.error(errmsg, ex);
				Application.showErrorMessage(getFrame(), errmsg);
				return;
			}
			dlg.setVisible(true);
			dlg.dispose();
			if (dlg.getDialogResult() != IDialogResult.DialogResult_OK) {
				// user canceled!
				return;
			}
			vfParent = dlg.getParentDirectory();
		}
		else {
			// モジュール実行定義の編集
			MExecDefEditDialog.EditType etype = (readOnly ? MExecDefEditDialog.EditType.VIEW : MExecDefEditDialog.EditType.MODIFY);
			MExecDefEditDialog dlg;
			try {
				// ダイアログの生成
				dlg = new MExecDefEditDialog(getFrame(), etype,
								getFrame().getFramePathFormatter(),
								ndRoot.getDisplayName(),
								ndRoot.getFileObject(),
								node.getFileObject());
			}
			catch (SecurityException ex) {
				// ファイルへのアクセス不可
				String errmsg = CommonMessages.formatErrorMessage(
									CommonMessages.getInstance().msgCouldNotAccessFile,
									ex, node.getFileObject().toString());
				AppLogger.error(errmsg, ex);
				Application.showErrorMessage(getFrame(), errmsg);
				return;
				
			}
			catch (Throwable ex) {
				// その他のエラー
				String errmsg = CommonMessages.formatErrorMessage(
									RunnerMessages.getInstance().msgMExecDefCouldNotOpen,
									ex, node.getFileObject().toString());
				AppLogger.error(errmsg, ex);
				Application.showErrorMessage(getFrame(), errmsg);
				return;
			}
			//--- ダイアログを表示
			dlg.setConfiguration(AppSettings.MEXECDEF_EDIT_DLG, AppSettings.getInstance().getConfiguration());
			//dlg.initialComponent();
			if (dlg.setupComponent(getFrame())) {
				dlg.setVisible(true);
			}
			dlg.dispose();
			if (dlg.getDialogResult() != IDialogResult.DialogResult_OK) {
				// user canceled!
				return;
			}
			vfParent = dlg.getParentDirectory();
		}
		
		// 作成されたファイルの情報を更新
		if (vfParent != null) {
			TreePath tpParentDir = _cTree.getModel().getTreePathForFile(vfParent);
			if (tpParentDir == null)
				throw new AssertionError("TreeNode not found : \"" + vfParent.toString() + "\"");
			_cTree.refreshTree(tpParentDir);
		}
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
		MExecDefFolderChooser chooser = MExecDefFolderChooser.createFolderChooser(getFrame(),
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
			initParent = ((MExecDefTreeNode)selectedPath.getLastPathComponent()).getFileObject();
		}
		FilenameValidator validator = FileDialogManager.createDefaultFilenameValidator();
		MExecDefFolderChooser chooser = MExecDefFolderChooser.createFolderChooser(getFrame(),
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
	 * モジュール実行定義を新規に作成するためのダイアログを表示し、
	 * 入力された情報で新規モジュール実行定義を作成する。
	 * @return	正常に作成できた場合は <tt>true</tt>、そうでない場合は <tt>false</tt> を返す
	 */
	public boolean doCreateMExecDef() {
		// 作成可能な状態でないなら、処理しない
		if (!canCreateMExecDef()) {
			return false;
		}
		
		// ディレクトリ作成位置の確認
		VirtualFile initParent = null;
		TreePath selectedPath = _cTree.getSelectionPath();
		if (selectedPath != null) {
			MExecDefTreeNode node = (MExecDefTreeNode)selectedPath.getLastPathComponent();
			if (node.isExecDefData()) {
				// 親ディレクトリを取得
				initParent = node.getFileObject().getParentFile();
			}
			else if (!node.isDirectory()) {
				// ファイルなら、親ディレクトリを取得
				initParent = node.getFileObject().getParentFile();
			}
			else {
				// 選択ファイルを初期の親とする
				initParent = node.getFileObject();
			}
		}
		
		// ダイアログを表示
		MExecDefTreeNode ndUserRoot = _cTree.getModel().getUserRootNode();
		MExecDefEditDialog dlg = new MExecDefEditDialog(getFrame(),
										MExecDefEditDialog.EditType.NEW,
										getFrame().getFramePathFormatter(),
										ndUserRoot.getDisplayName(),
										ndUserRoot.getFileObject(),
										initParent);
		dlg.setConfiguration(AppSettings.MEXECDEF_EDIT_DLG, AppSettings.getInstance().getConfiguration());
		//dlg.initialComponent();
		if (dlg.setupComponent(getFrame())) {
			dlg.setVisible(true);
		}
		dlg.dispose();
		if (dlg.getDialogResult() != IDialogResult.DialogResult_OK) {
			// user canceled!
			return false;
		}
		
		// 作成されたファイルの情報を更新
		VirtualFile vfParent = dlg.getParentDirectory();
		TreePath tpParentDir = _cTree.getModel().getTreePathForFile(vfParent);
		if (tpParentDir == null)
			throw new AssertionError("TreeNode not found : \"" + vfParent.toString() + "\"");
		_cTree.refreshTree(tpParentDir);
		return true;
	}

	/**
	 * 汎用フィルタを新規に作成するためのダイアログを表示し、
	 * 入力された情報で汎用フィルタを作成する。
	 * @return	正常に作成できた場合は <tt>true</tt>、そうでない場合は <tt>false</tt> を返す
	 * @since 3.2.0
	 */
	public boolean doCreateGenericFilter() {
		// 作成可能な状態でないなら、処理しない
		if (!canCreateGenericFilter()) {
			return false;
		}
		
		// ディレクトリ作成位置の確認
		VirtualFile initParent = null;
		TreePath selectedPath = _cTree.getSelectionPath();
		if (selectedPath != null) {
			MExecDefTreeNode node = (MExecDefTreeNode)selectedPath.getLastPathComponent();
			if (node.isExecDefData()) {
				// 親ディレクトリを取得
				initParent = node.getFileObject().getParentFile();
			}
			else if (!node.isDirectory()) {
				// ファイルなら、親ディレクトリを取得
				initParent = node.getFileObject().getParentFile();
			}
			else {
				// 選択ファイルを初期の親とする
				initParent = node.getFileObject();
			}
		}
		
		// ダイアログを表示
		MExecDefTreeNode ndUserRoot = _cTree.getModel().getUserRootNode();
		GenericFilterEditDialog dlg = new GenericFilterEditDialog(getFrame());
		dlg.setConfiguration(AppSettings.GENERICFILTER_EDIT_DLG, AppSettings.getInstance().getConfiguration());
		if (!dlg.initialComponent(FilterEditModel.EditType.NEW, getFrame().getMExecDefPathFormatter(), ndUserRoot.getDisplayName(), ndUserRoot.getFileObject(), initParent)) {
			// error
			return false;
		}
		dlg.setVisible(true);
		dlg.dispose();
		if (dlg.getDialogResult() != IDialogResult.DialogResult_OK) {
			// user canceled!
			return false;
		}

		// TODO: OK の処理を実装
		// 作成されたファイルの情報を更新
		VirtualFile vfParent = dlg.getParentDirectory();
		TreePath tpParentDir = _cTree.getModel().getTreePathForFile(vfParent);
		if (tpParentDir == null)
			throw new AssertionError("TreeNode not found : \"" + vfParent.toString() + "\"");
		_cTree.refreshTree(tpParentDir);
		return true;
	}

	/**
	 * フィルタマクロ定義を新規に作成するためのダイアログを表示し、
	 * 入力された情報で新規フィルタマクロ定義を作成する。
	 * @return	正常に作成できた場合は <tt>true</tt>、そうでない場合は <tt>false</tt> を返す
	 * @since 2.0.0
	 */
	public boolean doCreateFilterMacro() {
		// 作成可能な状態でないなら、処理しない
		if (!canCreateFilterMacro()) {
			return false;
		}
		
		// ディレクトリ作成位置の確認
		VirtualFile initParent = null;
		TreePath selectedPath = _cTree.getSelectionPath();
		if (selectedPath != null) {
			MExecDefTreeNode node = (MExecDefTreeNode)selectedPath.getLastPathComponent();
			if (node.isExecDefData()) {
				// 親ディレクトリを取得
				initParent = node.getFileObject().getParentFile();
			}
			else if (!node.isDirectory()) {
				// ファイルなら、親ディレクトリを取得
				initParent = node.getFileObject().getParentFile();
			}
			else {
				// 選択ファイルを初期の親とする
				initParent = node.getFileObject();
			}
		}
		
		// ダイアログを表示
		MExecDefTreeNode ndUserRoot = _cTree.getModel().getUserRootNode();
		MacroFilterEditDialog dlg = new MacroFilterEditDialog(getFrame());
		dlg.setConfiguration(AppSettings.MACROFILTER_EDIT_DLG, AppSettings.getInstance().getConfiguration());
		if (!dlg.initialComponent(MacroFilterEditModel.EditType.NEW, getFrame().getMExecDefPathFormatter(), ndUserRoot.getDisplayName(), ndUserRoot.getFileObject(), initParent, null)) {
			// error
			return false;
		}
		dlg.setVisible(true);
		dlg.dispose();
		if (dlg.getDialogResult() != IDialogResult.DialogResult_OK) {
			// user canceled!
			return false;
		}
		
		// 作成されたファイルの情報を更新
		VirtualFile vfParent = dlg.getParentDirectory();
		TreePath tpParentDir = _cTree.getModel().getTreePathForFile(vfParent);
		if (tpParentDir == null)
			throw new AssertionError("TreeNode not found : \"" + vfParent.toString() + "\"");
		_cTree.refreshTree(tpParentDir);
		return true;
	}
	
	/**
	 * モジュール実行定義のインポート設定ダイアログを表示し、
	 * ユーザーの設定に従い、インポートする。
	 * @return	正常にインポートできた場合は <tt>true</tt>、そうでない場合は <tt>false</tt> を返す
	 * @since 1.10
	 */
	public boolean doImportMExecDef() {
		// インポートダイアログ作成
		VirtualFile initParent = getSelectionFile();
		String dlgTreeLabel = CommonMessages.getInstance().TreeLabel_SelectParentFolder;
		FilenameValidator validator = FileDialogManager.createDefaultFilenameValidator();
		MExecDefImportDialog dlg = new MExecDefImportDialog(getFrame(), validator);
		dlg.initialComponent(true,	// フォルダ作成を許可
							null,	// システムルートは表示しない
							getUserRootDirectory(),
							initParent,
							null,	// 説明は表示しない
							dlgTreeLabel,
							null,	// 入力ラベルは表示しない
							MExecDefFolderChooser.DefaultFolderFileFilter);
		//dlg.pack();
		dlg.restoreConfiguration();
		dlg.setVisible(true);
		dlg.dispose();
		//--- 作成したフォルダをリフレッシュ
		{
			VirtualFile[] folders = dlg.getCreatedFolders();
			if (folders != null && folders.length > 0) {
				for (VirtualFile file : folders) {
					refreshFileTree(file);
				}
			}
		}
		if (dlg.getDialogResult() != IDialogResult.DialogResult_OK) {
			// user canceled
			return false;
		}
		
		// インポート
		VirtualFile vfTargetDir = dlg.getSelectedDirectory();
		File importFile = dlg.getImportFile();
		File targetDir = ModuleFileManager.toJavaFile(vfTargetDir);
		//String[] mexecdefEntries = dlg.getImportMExecDefEntries();
		VirtualFile vfUserRoot = null;
		String strUserRootName = null;
		MExecDefTreeNode ndUserRoot = getTreeModel().getUserRootNode();
		if (ndUserRoot != null) {
			vfUserRoot = ndUserRoot.getFileObject();
			strUserRootName = ndUserRoot.getDisplayName();
		}
		getTreeModel().getUserRootNode().getDisplayName();
		MExecDefDataImportProgressMonitor task = new MExecDefDataImportProgressMonitor(importFile, targetDir, vfUserRoot, strUserRootName);
		task.execute(getFrame());
		// ツリービューの表示を更新
		TreePath treepath = getTreeModel().getTreePathForFile(vfTargetDir);
		if (treepath != null) {
			getTreeComponent().refreshTree(treepath);
			getTreeComponent().expandPath(treepath);
			getTreeComponent().setSelectionPath(treepath);
			// ツリービューをアクティブにする
			requestFocusInComponent();
		}
		if (task.isTerminateRequested()) {
			// user canceled
			return false;
		}
		task.showError(getFrame());
		return (task.getErrorCause() == null);
	}
	
	/**
	 * モジュール実行定義のエクスポート設定ダイアログを表示し、
	 * ユーザーの設定に従い、エクスポートする。
	 * @return	正常にエクスポートできた場合は <tt>true</tt>、そうでない場合は <tt>false</tt> を返す
	 * @since 1.10
	 */
	public boolean doExportMExecDef() {
		// エクスポートダイアログの作成
		VirtualFile initParent = getSelectionFile();
		String dlgTreeLabel = RunnerMessages.getInstance().MExecDefExportDlg_Label_ExportTree + ":";
		FilenameValidator validator = FileDialogManager.createDefaultFilenameValidator();
		MExecDefExportDialog dlg = new MExecDefExportDialog(getFrame(), validator);
		dlg.initialComponent(false,		// フォルダ作成を許可しない
							getSystemRootDirectory(),
							getUserRootDirectory(),
							initParent,
							null,		// 説明は表示しない
							dlgTreeLabel,
							null,		// 入力ラベルは表示しない
							null);		// フィルタはデフォルト
		//dlg.pack();
		dlg.restoreConfiguration();
		dlg.setVisible(true);
		dlg.dispose();
		if (dlg.getDialogResult() != IDialogResult.DialogResult_OK) {
			// user canceled
			return false;
		}
		
		// エクスポート
		File exportFile = dlg.getExportFile();
		VirtualFile vfMExecDefData = dlg.getMExecDefData();
		MExecDefDataExportProgressMonitor task = new MExecDefDataExportProgressMonitor(exportFile, ModuleFileManager.toJavaFile(vfMExecDefData));
		task.execute(getFrame());
		if (task.isTerminateRequested()) {
			// user canceled
			return false;
		}
		task.showError(getFrame());
		return (task.getErrorCause() == null);
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
		MExecDefTreeNode node = (MExecDefTreeNode)path.getLastPathComponent();
		MExecDefTreeNode ndUserRoot = _cTree.getModel().getUserRootNode();
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
		
		// 名前入力ダイアログの表示
		MExecDefTreeNode selectedNode = (MExecDefTreeNode)selectedPath.getLastPathComponent();
		String strNewName = FileDialogManager.showRenameDialog(frame, selectedNode.getFileObject());
		if (Strings.isNullOrEmpty(strNewName)) {
			// user canceled!
			return;
		}
		
		// 名前変更
		Map<VirtualFile, VirtualFile> watchMap = _cTree.renameFile(frame, selectedPath,
														strNewName, null/*willMoveMap.keySet()*/);
		
		// 変更されたドキュメントの保存先を更新
		if (watchMap != null && !watchMap.isEmpty()) {
			/*
			for (Map.Entry<VirtualFile, VirtualFile> entry : watchMap.entrySet()) {
				VirtualFile vfSrc = entry.getKey();
				VirtualFile vfDst = entry.getValue();
				if (vfDst != null) {
					frame.renameToEditorFile(willMoveMap.get(vfSrc), ((DefaultFile)vfDst).getJavaFile());
				}
			}
			***/
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
		MExecDefTreeNode ndUserRoot = _cTree.getModel().getUserRootNode();
		TreePath[] paths = _cTree.getSelectionPaths();
		for (TreePath path : paths) {
			MExecDefTreeNode node = (MExecDefTreeNode)path.getLastPathComponent();
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
		MExecDefTreeModel model = _cTree.getModel();
		TreePath[] selectedPaths = _cTree.getSelectionPaths();
		
		// 移動先を選択
		TreePath selectedOnePath = _cTree.getSelectionPath();
		VirtualFile targetDir = chooseDirectory(
								CommonMessages.getInstance().Choose_MoveTarget,
								((MExecDefTreeNode)selectedOnePath.getLastPathComponent()).getFileObject());
		if (targetDir == null) {
			// user canceled!
			return;
		}
		TreePath tpTargetDir = model.getTreePathForFile(targetDir);
		
		// 移動先が同一ディレクトリもしくは選択ディレクトリ以下では許可しない
		for (TreePath path : selectedPaths) {
			String errmsg = null;
			VirtualFile vfSource = ((MExecDefTreeNode)path.getLastPathComponent()).getFileObject();
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
													tpTargetDir, null/*willMoveMap.keySet()*/);

		// 変更されたドキュメントの保存先を更新
		if (watchMap != null && !watchMap.isEmpty()) {
			/*
			for (Map.Entry<VirtualFile, VirtualFile> entry : watchMap.entrySet()) {
				VirtualFile vfSrc = entry.getKey();
				VirtualFile vfDst = entry.getValue();
				if (vfDst != null) {
					frame.renameToEditorFile(willMoveMap.get(vfSrc), ((DefaultFile)vfDst).getJavaFile());
				}
			}
			***/
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
		IMExecDefFile[] datas = _cTree.getSelectionNodeDatas();
		if (datas == null || datas.length <= 0) {
			return;
		}
		
		// 転送データを作成
		MExecDefFileTransferable transfer = new MExecDefFileTransferable(datas);
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
			MExecDefTreeNode selected = (MExecDefTreeNode)path.getLastPathComponent();
			MExecDefTreeNode ndUserRoot = _cTree.getModel().getUserRootNode();
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
		MExecDefTreeNode selectedNode = (MExecDefTreeNode)selectedPath.getLastPathComponent();
		if (selectedNode.isRoot())
			return;		// root selected
		try {
			if (!selectedNode.isDirectory()) {
				// 選択位置がディレクトリではない場合は、親を対象とする
				selectedPath = selectedPath.getParentPath();
				selectedNode = (MExecDefTreeNode)selectedPath.getLastPathComponent();
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
		VirtualFile[] files = MExecDefFileTransferable.getVirtualFilesFromTransferData(trans);
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
		MExecDefTreeNode ndUserRoot = _cTree.getModel().getUserRootNode();
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
						((MExecDefTreeNode)selectedPaths[0].getLastPathComponent()).getFilename());
			}
			int retAsk = JOptionPane.showConfirmDialog(getFrame(), confmsg,
					CommonMessages.getInstance().confirmDeleteTitle, JOptionPane.OK_CANCEL_OPTION);
			if (retAsk != JOptionPane.OK_OPTION) {
				// user canceled
				return;
			}
		}

		/*
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
		***/
		
		// 削除
		_cTree.deleteFiles(frame, selectedPaths);

		/*
		// 削除されたドキュメントのエディタを閉じる
		if (willDeleteList != null && !willDeleteList.isEmpty()) {
			for (IEditorView editor : willDeleteList) {
				frame.closeEditorByEditor(true, false, editor);
			}
		}
		***/
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	private MExecDefTree createTreeComponent() {
		MExecDefTree newTree = new MExecDefTree(null, _hTree){
			@Override
			protected void onTreeSelectionAdjusted() {
				super.onTreeSelectionAdjusted();
				// このビュー専用の処理
				MExecDefTreeView.this.onTreeSelectionAdjusted();
			}
		};
		return newTree;
	}
	
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
		
		// no process
		return false;
	}
	
	// menu : [Edit]-[Copy]
	protected void onSelectedMenuEditCopy(Action action) {
		AppLogger.debug("MExecDefTreeView : menu [Edit]-[Copy] selected.");
		if (!canCopy()) {
			action.setEnabled(false);
			return;
		}
		doCopy();
		requestFocusInComponent();
	}
	
	// menu : [Edit]-[Paste]
	protected void onSelectedMenuEditPaste(Action action) {
		AppLogger.debug("MExecDefTreeView : menu [Edit]-[Paste] selected.");
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
		AppLogger.debug("MExecDefTreeView : menu [Edit]-[Delete] selected.");
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
			frame.updateMenuItem(RunnerMenuResources.ID_FILE_MOVETO);
			frame.updateMenuItem(RunnerMenuResources.ID_FILE_RENAME);
			frame.updateMenuItem(RunnerMenuResources.ID_EDIT_MENU);
//			frame.updateMenuItem(RunnerMenuResources.ID_RUN_MENU);
			frame.updateMenuItem(RunnerMenuResources.ID_FILTER_MENU);
//			frame.updateMenuItem(RunnerMenuResources.ID_HIDE_EDIT_EXECDEF);
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

		// モジュール実行定義の実行
		getFrame().onSelectedMenuFilterRun(getFrame().getMenuAction(RunnerMenuResources.ID_FILTER_RUN));
//		getFrame().onSelectedMenuRunRun(getFrame().getMenuAction(RunnerMenuResources.ID_RUN_RUN));
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
					menuBar.getMExecDefTreeContextMenu().show(me.getComponent(), me.getX(), me.getY());
				}
			}
		}
	}

	/**
	 * ツリーコンポーネントの制御を行うハンドラ
	 */
	protected class TreeHandler extends MExecDefTree.DefaultTreeHandler
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
	
	static protected class MExecDefDataImportProgressMonitor extends ProgressMonitorTask implements ZipFileDecodeHandler
	{
		//------------------------------------------------------------
		// Constants
		//------------------------------------------------------------
		
		static protected final int YES_ONE_OPTION = 0;
		static protected final int YES_ALL_OPTION = 1;
		static protected final int NO_ONE_OPTION  = 2;
		static protected final int NO_ALL_OPTION  = 3;
		static protected final int CANCEL_OPTION  = 4;

		static protected final String[] OVERWRITE_OPTIONS = {
			CommonMessages.getInstance().Overwrite_YesOne,
			CommonMessages.getInstance().Overwrite_YesAll,
			CommonMessages.getInstance().Overwrite_NoOne,
			CommonMessages.getInstance().Overwrite_NoAll,
			CommonMessages.getInstance().Button_Cancel
		};

		//------------------------------------------------------------
		// Fields
		//------------------------------------------------------------
		
		private final File		_importFile;
		private final File		_targetDir;
		private final VirtualFile	_userRootDir;
		private final String		_userRootName;
		
		private long	_totalLength;
		private long	_totalWroteLength;
		
		private Boolean		_allowOverwriteAllFiles = null;
		
		private ZipFileDecoder	_decoder;
		private Map<String,Long>	_mapMExecDefDataTimes = new HashMap<String, Long>();

		//------------------------------------------------------------
		// Constructions
		//------------------------------------------------------------
		
		public MExecDefDataImportProgressMonitor(File importFile, File mexecDefDir, VirtualFile userRootDir, String userRootName)
		{
			super(RunnerMessages.getInstance().MExecDefImport_ProgressTitle, RunnerMessages.getInstance().MExecDefImport_ProgressDesc, null, 0, 0, 0);
			_importFile = importFile;
			_targetDir  = mexecDefDir;
			_userRootDir = userRootDir;
			_userRootName = userRootName;
		}

		//------------------------------------------------------------
		// Public interfaces
		//------------------------------------------------------------
		
		@Override
		public void processTask() throws Throwable
		{
			_mapMExecDefDataTimes.clear();
			_allowOverwriteAllFiles = null;
			
			// 圧縮用オブジェクトの生成
			if (AppLogger.isInfoEnabled()) {
				StringBuilder sb = new StringBuilder();
				sb.append("Start MExecDefDataImportProgressMonitor task : \"" + _importFile.getAbsolutePath() + "\"");
				sb.append("\n[Target Directory] ");
				sb.append(_targetDir.getAbsolutePath());
				AppLogger.info(sb.toString());
			}
			_decoder = new ZipFileDecoder(_importFile, this);
			try {
				// 処理対象のファイルサイズ合計をカウント
				_totalWroteLength = 0L;
				_totalLength = 0L;
				{
					Enumeration<? extends ZipEntry> entries = _decoder.zipEntries();
					for (;entries.hasMoreElements();) {
						checkTerminateRequest();
						ZipEntry entry = entries.nextElement();
						if (!entry.isDirectory()) {
							String entryName = entry.getName();
							long len = entry.getSize();
							_totalLength += (len < 0L ? 1L : len);
							if (Strings.endsWithIgnoreCase(entryName, MExecDefFileManager.MEXECDEF_PREFS_FILENAME)) {
								// モジュール実行定義設定ファイル
								String dirEntryName = ZipFileDecoder.getParentEntryName(entryName);
								_mapMExecDefDataTimes.put(dirEntryName, entry.getTime());
							}
						}
					}
				}
				if (AppLogger.isInfoEnabled()) {
					AppLogger.info("import total size : " + _totalLength + " bytes.");
				}
				checkTerminateRequest();
				
				// 進捗の最大値を設定
				setMaximum(100);
				
				// ファイルのデコード
				_decoder.decode(_targetDir);
				
				// 完了
				setValue(getMaximum());
			}
			finally {
				try {
					_decoder.close();
					_decoder = null;
				} catch (Throwable ignoreEx) {}
			}
			
			if (AppLogger.isInfoEnabled()) {
				AppLogger.info("MExecDefDataExportProgressMonitor task finished : \"" + _importFile.getAbsolutePath() + "\"");
			}
		}

		/**
		 * コピー、移動、削除など、プログレスモニタータスクの処理中に発生した
		 * エラーに関するメッセージを表示する。
		 * エラーが発生していない場合は、処理しない。
		 * 
		 * @param parentComponent	メッセージボックスのオーナー
		 */
		public void showError(Component parentComponent) {
			if (isTerminateRequested()) {
				// user canceled
				return;
			}

			if (getErrorCause() != null) {
				String errmsg;
				Throwable errorcause = getErrorCause();
				if (errorcause instanceof ZipDecodeOperationException) {
					ZipDecodeOperationException ex = (ZipDecodeOperationException)errorcause;
					String strSourceEntryName = ex.getSourceEntryName();
					String strDestinationFile = null;
					if (ex.getDestinationFile() != null) {
						strDestinationFile = formatFilePath(ex.getDestinationFile());
					}
					
					StringBuilder sb = new StringBuilder();
					sb.append(RunnerMessages.getInstance().msgMExecDefImportErrorFile);
					//--- source
					sb.append("\n  Source : \"");
					sb.append(_importFile.getName());
					if (strSourceEntryName != null) {
						sb.append(Files.CommonSeparatorChar);
						sb.append(strSourceEntryName);
					}
					sb.append("\"");
					//--- dest
					if (strDestinationFile != null) {
						sb.append("\n  Destination : \"");
						sb.append(strDestinationFile);
						sb.append("\"");
					}
					//--- cause
					if (ex.getCause() != null) {
						sb.append("\n  cause:(");
						sb.append(ex.getCause().getClass().getName());
						sb.append(")");
						String msg = ex.getCause().getLocalizedMessage();
						if (msg == null) {
							sb.append("\n        " + msg);
						}
					}
					errmsg = sb.toString();
				}
				else if (errorcause instanceof ZipIllegalChecksumException) {
					ZipIllegalChecksumException ex = (ZipIllegalChecksumException)errorcause;
					errmsg = CommonMessages.formatErrorMessage(RunnerMessages.getInstance().msgMExecDefImportChecksumError, ex, ex.zipEntryName());
				}
				else if (errorcause instanceof ZipException) {
					errmsg = CommonMessages.formatErrorMessage(RunnerMessages.getInstance().msgMExecDefImportIllegalSource,
									errorcause, _importFile.getAbsolutePath());
				}
				else if (errorcause instanceof FileNotFoundException) {
					errmsg = CommonMessages.formatErrorMessage(CommonMessages.getInstance().msgFileNotFound,
									null, _importFile.getAbsolutePath());
				}
				else {
					// unknown exception
					StringBuilder sb = new StringBuilder();
					sb.append(RunnerMessages.getInstance().msgMExecDefImportFailed);
					sb.append("\n[Source] " + _importFile.getAbsolutePath());
					sb.append("\n[Dest] " + _targetDir.getAbsolutePath());
					sb.append("\n[Cause] ");
					String exmsg = errorcause.getLocalizedMessage();
					if (exmsg != null) {
						sb.append(exmsg);
					} else {
						sb.append(errorcause.getClass().getName());
					}
					errmsg = sb.toString();
				}
				AppLogger.error(errmsg, errorcause);
				Application.showErrorMessage(parentComponent, errmsg);
			}
		}

		public boolean acceptDecodeDirectory(final File dir, final String entryName) {
			checkTerminateRequest();
			
			if (!dir.exists()) {
				if (AppLogger.isDebugEnabled()) {
					AppLogger.debug("accept [" + entryName + "] \"" + dir.getAbsolutePath() + "\"");
				}
				return true;	// create directory
			}
			if (!dir.isDirectory()) {
				if (AppLogger.isDebugEnabled()) {
					AppLogger.debug("accept [" + entryName + "] \"" + dir.getAbsolutePath() + "\"");
				}
				return true;	// throw ZipDecodeOperationException
			}
			
			// check MExecDef data directory
			if (_mapMExecDefDataTimes.containsKey(entryName)) {
				// Zip entry is MExecDef data directory
				if (!MExecDefFileManager.isModuleExecDefData(dir)) {
					throw new ZipDecodeOperationException(ZipDecodeOperationException.WRITE, entryName, dir);
				}
				
				// confirm replace MExecDef data directory
				Boolean allowOverwrite = null;
				if (_allowOverwriteAllFiles == null) {
					int ret = showReplaceMExecDefConfirmDialog(dir, entryName);
					switch (ret) {
						case YES_ONE_OPTION :
							allowOverwrite = Boolean.TRUE;
							break;
						case YES_ALL_OPTION :
							_allowOverwriteAllFiles = Boolean.TRUE;
							allowOverwrite = Boolean.TRUE;
							break;
						case NO_ONE_OPTION :
							allowOverwrite = Boolean.FALSE;
							break;
						case NO_ALL_OPTION :
							_allowOverwriteAllFiles = Boolean.FALSE;
							allowOverwrite = Boolean.FALSE;
							break;
					}
				} else {
					if (_allowOverwriteAllFiles.booleanValue()) {
						// allow overwrite all files
						allowOverwrite = Boolean.TRUE;
					} else {
						// deny overwrite all files
						allowOverwrite = Boolean.FALSE;
					}
				}
				if (allowOverwrite == null) {
					// user canceled
					requestTerminate();
					checkTerminateRequest();
					return false;
				}
				else if (!allowOverwrite.booleanValue()) {
					// skip MExecDef data
					return false;
				}
				
				// allow replace MExecDef, delete all sub files
				deleteAllSubFiles(dir, entryName);
			} else {
				// Zip entry is normal directory
				if (MExecDefFileManager.isModuleExecDefData(dir)) {
					throw new ZipDecodeOperationException(ZipDecodeOperationException.MKDIR, entryName, dir);
				}
			}

			// allow
			if (AppLogger.isDebugEnabled()) {
				AppLogger.debug("accept [" + entryName + "] \"" + dir.getAbsolutePath() + "\"");
			}
			return true;
		}

		public boolean acceptDecodeFile(final File file, final String entryName, final ZipEntry zipEntry) {
			checkTerminateRequest();
			
			if (!file.exists()) {
				if (AppLogger.isDebugEnabled()) {
					AppLogger.debug("accept [" + entryName + "] \"" + file.getAbsolutePath() + "\"");
				}
				return true;	// create file
			}
			if (file.isDirectory()) {
				if (AppLogger.isDebugEnabled()) {
					AppLogger.debug("accept [" + entryName + "] \"" + file.getAbsolutePath() + "\"");
				}
				return true;	// throw ZipDecodeOperationException
			}
			
			// confirm replace MExecDef data directory
			Boolean allowOverwrite = null;
			if (_allowOverwriteAllFiles == null) {
				int ret = showOverwriteFileConfirmDialog(file, entryName, zipEntry);
				switch (ret) {
					case YES_ONE_OPTION :
						allowOverwrite = Boolean.TRUE;
						break;
					case YES_ALL_OPTION :
						_allowOverwriteAllFiles = Boolean.TRUE;
						allowOverwrite = Boolean.TRUE;
						break;
					case NO_ONE_OPTION :
						allowOverwrite = Boolean.FALSE;
						break;
					case NO_ALL_OPTION :
						_allowOverwriteAllFiles = Boolean.FALSE;
						allowOverwrite = Boolean.FALSE;
						break;
				}
			} else {
				if (_allowOverwriteAllFiles.booleanValue()) {
					// allow overwrite all files
					allowOverwrite = Boolean.TRUE;
				} else {
					// deny overwrite all files
					allowOverwrite = Boolean.FALSE;
				}
			}
			if (allowOverwrite == null) {
				// user canceled
				requestTerminate();
				checkTerminateRequest();
				return false;
			}
			else if (!allowOverwrite.booleanValue()) {
				// skip MExecDef data
				return false;
			}

			// allow
			if (AppLogger.isDebugEnabled()) {
				AppLogger.debug("accept [" + entryName + "] \"" + file.getAbsolutePath() + "\"");
			}
			return true;
		}

		public void completedDecodeDirectory(final File dir, final String entryName) {
			if (AppLogger.isDebugEnabled()) {
				AppLogger.debug("completed [" + entryName + "] \"" + dir.getAbsolutePath() + "\"");
			}
			checkTerminateRequest();
		}

		public void completedDecodeFile(final File file, final String entryName, final ZipEntry zipEntry)
		{
			if (AppLogger.isDebugEnabled()) {
				AppLogger.debug("completed [" + entryName + "] \"" + file.getAbsolutePath() + "\"");
			}
			checkTerminateRequest();
			if (zipEntry.getSize() < 0L) {
				appendTotalLength(1L);
			}
		}
		
		public void onWritingFile(final File file, final String entryName, final ZipEntry zipEntry, final int wroteLen, final long wroteTotalLen)
		{
			checkTerminateRequest();
			if (zipEntry.getSize() >= 0L) {
				appendTotalLength(wroteLen);
			}
		}
		
		public void onSkipFileEntry(final File file, final String entryName, final ZipEntry zipEntry) {
			if (AppLogger.isDebugEnabled()) {
				AppLogger.debug("skipped [" + entryName + "] \"" + file.getAbsolutePath() + "\"");
			}
			checkTerminateRequest();
			long size = zipEntry.getSize();
			appendTotalLength(size < 0L ? 1L : size);
		}

		//------------------------------------------------------------
		// Internal methods
		//------------------------------------------------------------
		
		protected void checkTerminateRequest() {
			if (isTerminateRequested()) {
				throw new IllegalStateException("User canceled!");
			}
		}
		
		protected void appendTotalLength(long length) {
			_totalWroteLength += length;
			int val = (int)((double)_totalWroteLength / (double)_totalLength * 100.0);
			setValue(val);
		}
		
		protected void deleteAllSubFiles(final File destDir, final String entryName) {
			File[] files = destDir.listFiles();
			if (files != null && files.length > 0) {
				for (File child : files) {
					deleteAllSubFilesRecursive(child, destDir, entryName);
				}
			}
		}
		
		protected void deleteAllSubFilesRecursive(final File file, final File destDir, final String entryName) {
			checkTerminateRequest();
			
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				if (files != null && files.length > 0) {
					for (File child : files) {
						deleteAllSubFilesRecursive(child, destDir, entryName);
					}
				}
			}
			
			try {
				file.delete();
			} catch (Throwable ex) {
				// ignore exception
			}
		}
		
		protected String formatFilePath(File destFile) {
			if (!destFile.isAbsolute()) {
				destFile = destFile.getAbsoluteFile();
			}
			if (_userRootDir == null) {
				return destFile.getPath();
			}
			
			// パス文字列を生成
			VirtualFile vfDest = ModuleFileManager.fromJavaFile(destFile);
			if (vfDest.isDescendingFrom(_userRootDir)) {
				StringBuilder sb = new StringBuilder();
				sb.append(Files.CommonSeparatorChar);
				if (_userRootName != null && _userRootName.length() > 0) {
					sb.append(_userRootName);
				} else {
					sb.append(_userRootDir.getName());
				}
				sb.append(Files.CommonSeparatorChar);
				sb.append(vfDest.relativePathFrom(_userRootDir, Files.CommonSeparatorChar));
				return sb.toString();
			}
			else {
				return destFile.getPath();
			}
		}
		
		protected String createMExecDefOverwriteConfirmMessage(File destFile, String entryName)
		{
			DateFormat   frmDate = DateFormat.getDateTimeInstance();
			long srcLastModified = _mapMExecDefDataTimes.get(entryName);
			long dstLastModified = MExecDefFileManager.lastModifiedModuleExecDefData(destFile);
			String strSrcLastModified = (srcLastModified < 0L ? "Unknown" : frmDate.format(new Date(srcLastModified)));
			String strDstLastModified = frmDate.format(new Date(dstLastModified));
			
			return String.format(RunnerMessages.getInstance().confirmDetailReplaceMExecDef,
					formatFilePath(destFile),
					strDstLastModified,
					strSrcLastModified);
		}
		
		protected int showReplaceMExecDefConfirmDialog(File destFile, String entryName)
		{
			String title = CommonMessages.getInstance().confirmOverwiteTitle;
			String message = createMExecDefOverwriteConfirmMessage(destFile, entryName);
			int optionType = JOptionPane.YES_NO_CANCEL_OPTION;
			int messageType = JOptionPane.QUESTION_MESSAGE;
			
			return showOptionDialog(message, title, optionType, messageType, null,
									OVERWRITE_OPTIONS, OVERWRITE_OPTIONS[0]);
		}
		
		protected String createFileOverwriteConfirmMessage(File destFile, String entryName, ZipEntry zipEntry)
		{
			NumberFormat frmNumber = NumberFormat.getNumberInstance();
			DateFormat   frmDate = DateFormat.getDateTimeInstance();
			
			// last modified
			long srcLastModified = zipEntry.getTime();
			long dstLastModified = destFile.lastModified();
			String strSrcLastModified = (srcLastModified < 0L ? "Unknown" : frmDate.format(new Date(srcLastModified)));
			String strDstLastModified = frmDate.format(new Date(dstLastModified));
			
			// size
			long srcLength = zipEntry.getSize();
			long dstLength = destFile.length();
			String strSrcLength = (srcLength < 0L ? "?" : frmNumber.format(srcLength));
			String strDstLength = frmNumber.format(dstLength);
			
			return String.format(CommonMessages.getInstance().confirmDetailOverwriteFile,
					formatFilePath(destFile),
					strDstLength, strDstLastModified,
					strSrcLength, strSrcLastModified);
		}
		
		protected int showOverwriteFileConfirmDialog(File destFile, String entryName, ZipEntry zipEntry)
		{
			String title = CommonMessages.getInstance().confirmOverwiteTitle;
			String message = createFileOverwriteConfirmMessage(destFile, entryName, zipEntry);
			int optionType = JOptionPane.YES_NO_CANCEL_OPTION;
			int messageType = JOptionPane.QUESTION_MESSAGE;
			
			return showOptionDialog(message, title, optionType, messageType, null,
									OVERWRITE_OPTIONS, OVERWRITE_OPTIONS[0]);
		}
	}
	
	static protected class MExecDefDataExportProgressMonitor extends ProgressMonitorTask
	implements ZipFileEncodeHandler
	{
		//------------------------------------------------------------
		// Constants
		//------------------------------------------------------------

		//------------------------------------------------------------
		// Fields
		//------------------------------------------------------------
		
		private final File		_exportFile;
		private final File		_targetDir;
		
		private Set<File>	_excludeFiles;
		
		private long	_totalLength;
		private long	_totalWroteLength;
		
		private File	_lastTargetFile;
		private String	_lastTargetRelativePath;

		//------------------------------------------------------------
		// Constructions
		//------------------------------------------------------------
		
		public MExecDefDataExportProgressMonitor(File exportFile, File mexecDefDir)
		{
			super(RunnerMessages.getInstance().MExecDefExport_ProgressTitle, RunnerMessages.getInstance().MExecDefExport_ProgressDesc, null, 0, 0, 0);
			_exportFile = exportFile;
			_targetDir  = mexecDefDir;
			//--- 除外ファイル
			_excludeFiles = new HashSet<File>(2);
			//--- history ファイル
			_excludeFiles.add(new File(mexecDefDir, MExecDefFileManager.MEXECDEF_HISTORY_FILENAME));
			//--- 作業コピー用ディレクトリ
			_excludeFiles.add(new File(mexecDefDir, MExecDefFileManager.MEXECDEF_FILES_WORK_DIRNAME));
		}

		//------------------------------------------------------------
		// Public interfaces
		//------------------------------------------------------------
		
		@Override
		public void processTask() throws Throwable
		{
			// 圧縮用オブジェクトの生成
			if (AppLogger.isInfoEnabled()) {
				StringBuilder sb = new StringBuilder();
				sb.append("Start MExecDefDataExportProgressMonitor task : \"" + _exportFile.getAbsolutePath() + "\"");
				sb.append("\n[Target Directory] ");
				sb.append(_targetDir.getAbsolutePath());
				AppLogger.info(sb.toString());
			}
			ZipFileEncoder encoder = new ZipFileEncoder(_exportFile, _targetDir.getParentFile(), this);
			try {
				encoder.setFileFilter(new MExecDefDataExporFileFilter());
				
				// 処理対象のファイルサイズ合計をカウント
				_totalWroteLength = 0L;
				_totalLength = encoder.countTotalFileSize(_targetDir);
				if (AppLogger.isInfoEnabled()) {
					AppLogger.info("export total size : " + _totalLength + " bytes.");
				}
				
				// terminate?
				checkTerminateRequest();
				
				// 進捗の最大値を設定
				setMaximum(100);
				
				// ファイルのエンコード
				_lastTargetFile = null;
				_lastTargetRelativePath = null;
				encoder.encode(_targetDir);
				
				// 完了
				setValue(getMaximum());
			}
			finally {
				try {
					encoder.close();
				} catch (Throwable ignoreEx) {}
			}
			
			if (AppLogger.isInfoEnabled()) {
				long ziplen = _exportFile.length();
				int rate = (int)((double)ziplen / (double)_totalLength * 100.0);
				String infomsg = String.format("MExecDefDataExportProgressMonitor task finished : %d bytes (%d%%) : \"%s\"", ziplen, rate, _exportFile.getAbsolutePath());
				AppLogger.info(infomsg);
			}
		}

		/**
		 * コピー、移動、削除など、プログレスモニタータスクの処理中に発生した
		 * エラーに関するメッセージを表示する。
		 * エラーが発生していない場合は、処理しない。
		 * 
		 * @param parentComponent	メッセージボックスのオーナー
		 */
		public void showError(Component parentComponent) {
			if (isTerminateRequested()) {
				// user canceled
				return;
			}
			
			if (getErrorCause() != null) {
				StringBuilder sb = new StringBuilder();
				sb.append(RunnerMessages.getInstance().msgMExecDefExportFailed);
				sb.append("\n[Dest] " + _exportFile.getAbsolutePath());
				sb.append("\n[Source] " + _targetDir.getAbsolutePath());
				sb.append("\n[Cause] ");
				String exmsg = getErrorCause().getLocalizedMessage();
				if (exmsg != null) {
					sb.append(exmsg);
				} else {
					sb.append(getErrorCause().getClass().getName());
				}
				String errmsg = sb.toString();
				AppLogger.error(errmsg, getErrorCause());
				Application.showErrorMessage(parentComponent, errmsg);
			}
		}

		//------------------------------------------------------------
		// Internal methods
		//------------------------------------------------------------
		
		protected void checkTerminateRequest() {
			if (isTerminateRequested()) {
				throw new IllegalStateException("User canceled!");
			}
		}
		
		protected File getLastTargetFile() {
			return _lastTargetFile;
		}
		
		protected String getLastTargetRelativePath() {
			return _lastTargetRelativePath;
		}

		public boolean acceptEncodeDirectory(File file, String relativePath) {
			checkTerminateRequest();
			
			_lastTargetFile = file;
			_lastTargetRelativePath = relativePath;
			if (AppLogger.isDebugEnabled()) {
				AppLogger.debug("accept [" + relativePath + "/] \"" + file.getAbsolutePath() + "\"");
			}
			return true;
		}

		public boolean acceptEncodeFile(File file, String relativePath) {
			checkTerminateRequest();
			
			_lastTargetFile = file;
			_lastTargetRelativePath = relativePath;
			if (AppLogger.isDebugEnabled()) {
				AppLogger.debug("accept [" + relativePath + "] \"" + file.getAbsolutePath() + "\"");
			}
			return true;
		}

		public void completedEncodeDirectory(File file, String relativePath) {
			_lastTargetFile = null;
			_lastTargetRelativePath = null;
			if (AppLogger.isDebugEnabled()) {
				AppLogger.debug("completed [" + relativePath + "/] \"" + file.getAbsolutePath() + "\"");
			}
			
			checkTerminateRequest();
		}

		public void completedEncodeFile(File file, String relativePath) {
			_lastTargetFile = null;
			_lastTargetRelativePath = null;
			if (AppLogger.isDebugEnabled()) {
				AppLogger.debug("completed [" + relativePath + "] \"" + file.getAbsolutePath() + "\"");
			}
			
			checkTerminateRequest();
		}

		public void onWritingFile(File file, String relativePath, int wroteLen, long wroteTotalLen)
		{
			_totalWroteLength += wroteLen;
			int val = (int)((double)_totalWroteLength / (double)_totalLength * 100.0);
			setValue(val);
			
			checkTerminateRequest();
		}

		private class MExecDefDataExporFileFilter extends ZipFileEncoder.DefaultFileFilter
		{
			public boolean accept(File pathname) {
				if (!super.accept(pathname)) {
					return false;		// deny
				}
				
				// 除外ファイルなら許可しない
				if (_excludeFiles.contains(pathname)) {
					return false;		// deny
				}
				
				// allow
				return true;
			}
		}
	}
}
