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
 *  Copyright 2007-2016  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)RunnerFrame.java	3.3.0	2016/05/31
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerFrame.java	3.2.2	2015/10/15 (Bug fixed)
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerFrame.java	3.2.0	2015/06/05
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerFrame.java	3.1.0	2014/05/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerFrame.java	3.0.0	2014/03/26
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerFrame.java	2.1.0	2013/08/30
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerFrame.java	2.0.0	2012/11/06
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerFrame.java	1.22	2012/08/21
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerFrame.java	1.20	2012/03/26
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerFrame.java	1.13	2012/02/23
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerFrame.java	1.10	2011/02/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerFrame.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.runner.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.poi.EncryptedDocumentException;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.module.ModuleArgType;
import ssac.aadl.module.ModuleFileManager;
import ssac.aadl.module.swing.CsvFileConfigDialog;
import ssac.aadl.module.swing.FileDialogManager;
import ssac.falconseed.common.FSEnvironment;
import ssac.falconseed.editor.document.IEditorDocument;
import ssac.falconseed.editor.plugin.IComponentManager;
import ssac.falconseed.editor.plugin.PluginManager;
import ssac.falconseed.editor.plugin.csv.CsvFileComponentManager;
import ssac.falconseed.editor.plugin.csv.CsvFileMenuBar;
import ssac.falconseed.editor.plugin.csv.CsvFileModel;
import ssac.falconseed.editor.plugin.csv.CsvFileView;
import ssac.falconseed.editor.view.ActiveViewManager;
import ssac.falconseed.editor.view.ActiveViewManager.ActiveViewChangeEvent;
import ssac.falconseed.editor.view.ActiveViewManager.ActiveViewChangeListener;
import ssac.falconseed.editor.view.IEditorView;
import ssac.falconseed.editor.view.dialog.FindDialog;
import ssac.falconseed.editor.view.dialog.FindReplaceInterface;
import ssac.falconseed.excel2csv.EtcConvertMonitorTask;
import ssac.falconseed.excel2csv.EtcParseMonitorTask;
import ssac.falconseed.excel2csv.swing.EtcDestEditDialog;
import ssac.falconseed.file.DefaultVirtualFilePathFormatter;
import ssac.falconseed.file.VirtualFilePathFormatter;
import ssac.falconseed.file.VirtualFilePathFormatterList;
import ssac.falconseed.module.IModuleArgConfig;
import ssac.falconseed.module.MExecDefFileManager;
import ssac.falconseed.module.ModuleRuntimeData;
import ssac.falconseed.module.RelatedModuleList;
import ssac.falconseed.module.args.IMExecArgParam;
import ssac.falconseed.module.args.MExecArgCsvFile;
import ssac.falconseed.module.setting.MExecDefHistory;
import ssac.falconseed.module.setting.MExecDefSettings;
import ssac.falconseed.module.swing.AbMExecArgsDialog;
import ssac.falconseed.module.swing.AbModuleArgsDialog;
import ssac.falconseed.module.swing.IMExecArgsDialog;
import ssac.falconseed.module.swing.MExecArgsDialogHandler;
import ssac.falconseed.module.swing.MExecArgsEditDialog;
import ssac.falconseed.module.swing.MExecArgsModel;
import ssac.falconseed.module.swing.ModuleArgsEditDialog;
import ssac.falconseed.module.swing.tree.IMExecDefFileChooserHandler;
import ssac.falconseed.module.swing.tree.MExecDefFileChooser;
import ssac.falconseed.module.swing.tree.MExecDefFolderChooser;
import ssac.falconseed.plot.ChartConfigModel;
import ssac.falconseed.plot.ChartConfigModel.ChartStyles;
import ssac.falconseed.plot.ChartDialogManager;
import ssac.falconseed.runner.ModuleRunner;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.menu.RunnerMenuBar;
import ssac.falconseed.runner.menu.RunnerMenuBar.AbCheckMenuItemAction;
import ssac.falconseed.runner.menu.RunnerMenuResources;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.falconseed.runner.setting.PreferenceDialog;
import ssac.falconseed.runner.view.dialog.AsyncProcessMonitorWindow;
import ssac.falconseed.runner.view.dialog.FileChooserManager;
import ssac.falconseed.runner.view.dialog.MessageDetailDialog;
import ssac.falconseed.runner.view.dialog.UserFolderChooser;
import ssac.util.Objects;
import ssac.util.Strings;
import ssac.util.Validations;
import ssac.util.excel2csv.EtcConfigCsvData;
import ssac.util.excel2csv.EtcConfigDataSet;
import ssac.util.excel2csv.EtcWorkbookManager;
import ssac.util.excel2csv.parser.ConfigErrorDetail;
import ssac.util.excel2csv.parser.ConfigTooManyErrorsException;
import ssac.util.io.Files;
import ssac.util.io.VirtualFile;
import ssac.util.logging.AppLogger;
import ssac.util.mac.MacScreenMenuHandler;
import ssac.util.nio.csv.CsvParameters;
import ssac.util.swing.Application;
import ssac.util.swing.FrameWindow;
import ssac.util.swing.IDialogResult;
import ssac.util.swing.StatusBar;
import ssac.util.swing.menu.IMenuActionHandler;
import ssac.util.swing.menu.IMenuHandler;
import ssac.util.swing.menu.JMenus;

/**
 * モジュールランナーのメインフレーム
 * <p>
 * このクラスは、モジュールランナーのフレームワークを提供する。
 * 
 * @version 3.3.0
 */
public class RunnerFrame extends FrameWindow implements IMenuHandler, IMenuActionHandler, MacScreenMenuHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;

	static private final IEditorView[] EMPTY_EDITOR_ARRAY = new IEditorView[0];
	
	static private final Dimension DM_DEF_SIZE = new Dimension(800,600);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/**
	 * このフレームの標準メニューバー
	 */
	protected RunnerMenuBar		_defMainMenu;
	/**
	 * 現在アクティブなエディタ
	 */
	protected IEditorView			_activeEditor;
	/**
	 * 現在アクティブなビューを管理するマネージャ
	 */
	protected final ActiveViewManager	_activeViewManager;
	
	//--- Events
	private final EditorModifiedChangeHandler	_hEditorModified = new EditorModifiedChangeHandler();
	private final FileDropTargetListener _hEditorFileDroped = new FileDropTargetListener();
	private final MExecArgsDialogFrameHandler	_hArgsEditDlg = new MExecArgsDialogFrameHandler();
	
	// Components
	private JSplitPane		_viewSplit;
	private JSplitPane		_toolSplit;
	private RunnerTreeTabView	_viewTree;
	private RunnerToolTabView	_viewTool;
	private JTabbedPane	_tabEditor;
	private StatusBar		_statusBar;
	
	private FindDialog		_dlgFind;
	/**
	 * モジュール実行定義の実行時引数設定ダイアログ(モーダレス)
	 */
	private IMExecArgsDialog	_dlgMExecArgsEdit;
	/**
	 * チャートウィンドウマネージャ(モードレス)
	 * @since 2.1.0
	 */
	private ChartDialogManager	_chartWindow;

	/**
	 * エディタドキュメントのソースファイルが更新されている場合に、
	 * エディタドキュメントを再読込する処理を有効にするフラグ
	 * @since 1.17
	 */
	private boolean	_isEnabledRefreshEditorDocumentWhenUpdating = true;
	/**
	 * エディタドキュメントのソースファイル最終更新日時を保持するマップ。
	 * ドキュメントが持つ情報と変わらない場合は、<tt>null</tt> を保持する。
	 * @since 1.17
	 */
	private Map<IEditorView, Long>	_mapEditorSourceFileLastModifiedTime = new HashMap<IEditorView, Long>();
	
	private DefaultVirtualFilePathFormatter	_vformMExecDefSystemRoot;
	private DefaultVirtualFilePathFormatter	_vformMExecDefUserRoot;
	private DefaultVirtualFilePathFormatter	_vformDataFileSystemRoot;
	private DefaultVirtualFilePathFormatter	_vformDataFileUserRoot;
	private RunnerFramePathFormatter			_vformThis = new RunnerFramePathFormatter();
	
	// State
//	//--- local state
//	private Point		_lastFrameLocation;
//	private Dimension	_lastFrameSize;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public RunnerFrame() {
		super();
		this._activeViewManager = new ActiveViewManager();
	}

	/**
	 * メインフレームの初期化。
	 * このメソッドは、アプリケーション唯一のインスタンスから呼び出される。
	 */
	@Override
	public void initialComponent() {
		super.initialComponent();
		setTitle(getFrameTitle(false));
		
//		_lastFrameLocation = null;
//		_lastFrameSize = new Dimension();

		// setup Main menu
		_defMainMenu = new RunnerMenuBar(this);
		setActiveMainMenuBar(_defMainMenu);
		
		// setup Views
		_viewSplit = createMainPanel();
		getContentPane().add(_viewSplit, BorderLayout.CENTER);
		
		// setup Status bar
		_statusBar = new StatusBar();
		getContentPane().add(_statusBar, BorderLayout.SOUTH);
		
		// initial setup for tree view
		_viewTree.initialSetup(this);
		_viewTool.initialSetup(this);
		
		// setup drop targets
		new DropTarget(this, DnDConstants.ACTION_COPY, _hEditorFileDroped, true);
		
		// register views
		_activeViewManager.registerComponent(_viewTree);
		_activeViewManager.registerComponent(_tabEditor);
		_activeViewManager.registerComponent(_viewTool);
		
		// setup Window actions
		enableComponentEvents(true);
		enableWindowEvents(true);
		enableWindowStateEvents(true);
		setupComponentActions();
		
		// update all menu items
		updateAllMenuItems();
		
		// create path formatter
		_vformMExecDefSystemRoot = createMExecDefSystemRootPathFormatter();
		_vformMExecDefUserRoot   = createMExecDefUserRootPathFormatter();
		_vformDataFileSystemRoot = createDataFileSystemRootPathFormatter();
		_vformDataFileUserRoot   = createDataFileUserRootPathFormatter();
		
		// restore View's settings
		restoreSettings();
	}

	//------------------------------------------------------------
	// Components creation
	//------------------------------------------------------------
	
	private JSplitPane createMainPanel() {
		// create component
		JSplitPane paneSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		paneSplit.setResizeWeight(0);
		JSplitPane paneTool  = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		paneTool.setResizeWeight(1);
		
		// create views
		this._viewTree  = createTreeTabView();
		this._tabEditor = createEditorTab();
		this._viewTool  = createToolTabView();
		this._toolSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		this._toolSplit.setResizeWeight(1);
		
		// add to split
		_toolSplit.setTopComponent(_tabEditor);
		_toolSplit.setBottomComponent(_viewTool);
		paneSplit.setLeftComponent(_viewTree);
		paneSplit.setRightComponent(_toolSplit);
		
		// completed
		return paneSplit;
	}
	
	private RunnerTreeTabView createTreeTabView() {
		RunnerTreeTabView view = new RunnerTreeTabView();
		
		// get root directories
		VirtualFile mexecSystemDir = ModuleFileManager.fromJavaFile(AppSettings.getInstance().getDefaultSystemMExecDefRootDirectory());
		VirtualFile mexecUserDir   = ModuleFileManager.fromJavaFile(AppSettings.getInstance().getAvailableUserMExecDefRootDirectory());
		VirtualFile dataSystemDir  = ModuleFileManager.fromJavaFile(AppSettings.getInstance().getDefaultSystemDataRootDirectory());
		VirtualFile dataUserDir    = ModuleFileManager.fromJavaFile(AppSettings.getInstance().getAvailableUserDataRootDirectory());
		
		// setup component
		view.initialComponent(mexecSystemDir, mexecUserDir, dataSystemDir, dataUserDir);
		
		return view;
	}
	
	private RunnerToolTabView createToolTabView() {
		RunnerToolTabView view = new RunnerToolTabView();
		view.initialComponent();
		view.setMinimumSize(new Dimension(10,10));
		return view;
	}
	
	private JTabbedPane createEditorTab() {
		// create component
		JTabbedPane tab = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
		tab.setMinimumSize(new Dimension(10,10));
		tab.setFocusable(false);

		// completed
		return tab;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定されたファイルが読み込み専用かを判定する。
	 * ファイルそのものが読み書き可能な場合でも、モジュールパッケージに
	 * 含まれるファイルの場合は読み込み専用とする。
	 * @param file	判定するファイル
	 * @return	指定されたファイルが読み込み専用であれば <tt>true</tt>、
	 * 			そうでない場合は <tt>false</tt> を返す。
	 * 			<em>file</em> が <tt>null</tt> の場合は <tt>false</tt> を返す。
	 */
	public boolean isReadOnlyFile(VirtualFile file) {
		if (file == null)
			return false;
		
		/*if (ModuleFileManager.getTopModulePackagePrefsFile(file, getCurrentWorkspace()) != null) {
			// モジュールパッケージに含まれる抽象パスなら、読み取り専用
			return true;
		} else*/
		if (file.exists() && !file.canWrite()) {
			// ファイルは存在するが書き込み不可である場合は、読み取り専用
			return true;
		} else {
			// それ以外は、書き込み可能
			return false;
		}
	}
	
	/**
	 * 指定されたファイルが読み込み専用かを判定する。
	 * ファイルそのものが読み書き可能な場合でも、モジュールパッケージに
	 * 含まれるファイルの場合は読み込み専用とする。
	 * @param file	判定するファイル
	 * @return	指定されたファイルが読み込み専用であれば <tt>true</tt>、
	 * 			そうでない場合は <tt>false</tt> を返す。
	 * 			<em>file</em> が <tt>null</tt> の場合は <tt>false</tt> を返す。
	 */
	public boolean isReadOnlyFile(File javaFile) {
		VirtualFile file = (javaFile==null ? (VirtualFile)null : ModuleFileManager.fromJavaFile(javaFile));
		return isReadOnlyFile(file);
	}

	/**
	 * 現在表示されているすべてのエディタを取得する。
	 * @return	表示されているすべてのエディタオブジェクトを格納する配列を返す。
	 * 			エディタが一つも表示されていない場合は空の配列を返す。
	 */
	public IEditorView[] getAllEditors() {
		int numEditors = getEditorCount();
		if (numEditors > 0) {
			IEditorView[] editors = new IEditorView[numEditors];
			for (int i = 0; i < numEditors; i++) {
				editors[i] = getEditor(i);
			}
			return editors;
		} else {
			return EMPTY_EDITOR_ARRAY;
		}
	}

	/**
	 * 現在編集中のエディタをすべて返す。
	 * @return 編集中のエディタオブジェクトを格納する配列を返す。
	 * 			編集中のエディタが存在しない場合は空の配列を返す。
	 */
	public IEditorView[] getModifiedEditors() {
		ArrayList<IEditorView> list = null;
		int numDocuments = getEditorCount();
		if (numDocuments > 0) {
			list = new ArrayList<IEditorView>(numDocuments);
			for (int i = 0; i < numDocuments; i++) {
				IEditorView editor = getEditor(i);
				IEditorDocument doc = editor.getDocument();
				if (doc.isModified()) {
					list.add(editor);
				}
			}
		}
		
		if (list != null && !list.isEmpty()) {
			return list.toArray(new IEditorView[list.size()]);
		} else {
			return EMPTY_EDITOR_ARRAY;
		}
	}

	/**
	 * 指定されたドキュメント(ファイル)が編集中の場合に <tt>true</tt> を返す。
	 */
	public boolean isModifiedDocument(File documentFile) {
		int numDocuments = getEditorCount();
		for (int i = 0; i < numDocuments; i++) {
			IEditorDocument doc = getEditor(i).getDocument();
			File fDocument = doc.getTargetFile();
			if (doc.isModified() && fDocument != null && fDocument.equals(documentFile)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 指定された <em>file</em> が編集中のドキュメント、もしくは編集中の
	 * ドキュメントの上位階層のパスを示す場合に <tt>true</tt> を返す。
	 * @param file	検証する抽象パスを示す <code>File</code> オブジェクト
	 * @return	<em>file</em> が編集中のドキュメント、もしくは編集中の
	 * 			ドキュメントの上位階層である場合は <tt>true</tt>
	 */
	public boolean isModifiedDescendingDocument(File file) {
		int numDocuments = getEditorCount();
		for (int i = 0; i < numDocuments; i++) {
			IEditorDocument doc = getEditor(i).getDocument();
			File fDocument = doc.getTargetFile();
			if (doc.isModified() && fDocument != null && Files.isDescendingFrom(fDocument, file)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * エディタビューがアクティブかを判定する。
	 * このメソッドは、アクティブビューを監視するマネージャの状態のみで判定する。
	 * そのため、コンポーネントのフォーカス所有については感知しない。
	 * @return	エディタビューがアクティブであれば <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 1.10
	 */
	public boolean isEditorViewActivated() {
		return _activeViewManager.isComponentActivated(_tabEditor);
	}
	
	/**
	 * ツリービューがアクティブかを判定する。
	 * このメソッドは、アクティブビューを監視するマネージャの状態のみで判定する。
	 * そのため、コンポーネントのフォーカス所有については感知しない。
	 * @return	ツリービューがアクティブであれば <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 1.10
	 */
	public boolean isTreeViewActivated() {
		return _activeViewManager.isComponentActivated(_viewTree);
	}

	/**
	 * ツールビューがアクティブかを判定する。
	 * このメソッドは、アクティブビューを監視するマネージャの状態のみで判定する。
	 * そのため、コンポーネントのフォーカス所有については感知しない。
	 * @return	ツールビューがアクティブであれば <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 1.22
	 */
	public boolean isToolViewActivated() {
		return _activeViewManager.isComponentActivated(_viewTool);
	}

	/**
	 * エディタの有無を検証する。
	 * 
	 * @return エディタが存在しない場合は <tt>true</tt>
	 */
	private boolean isEditorEmpty() {
		return (getEditorCount() <= 0);
	}
	
	/**
	 * エディタの総数を取得する。
	 * 
	 * @return タブに登録されているエディタの総数
	 */
	private int getEditorCount() {
		return this._tabEditor.getTabCount();
	}

	/**
	 * 指定されたインデックスのタブが保持するエディタを取得する。
	 * @param index	エディタタブのインデックス
	 * @return	エディタのインスタンス
	 */
	private IEditorView getEditor(int index) {
		return (IEditorView)_tabEditor.getComponentAt(index);
	}

	/**
	 * 指定されたエディタの、エディタビューにおけるタブインデックスを取得する。
	 * @param editor	エディタ
	 * @return	タブインデックス、タブに存在しない場合は (-1)
	 * @since 1.20
	 */
	public int indexOfEditor(IEditorView editor) {
		return _tabEditor.indexOfComponent((Component)editor);
	}

	/**
	 * このフレームで、現在アクティブなビューを取得する。
	 * @return	現在アクティブなビューを返す。アクティブなビューが存在しない場合は
	 * 			<tt>null</tt> を返す。
	 */
	public IEditorView getActiveEditor() {
		return _activeEditor;
	}
	
	/**
	 * このフレームで、現在アクティブなドキュメントを取得する。
	 * @return	現在アクティブなドキュメントを返す。アクティブなドキュメントが存在しない場合は
	 * 			<tt>null</tt> を返す。
	 */
	public IEditorDocument getActiveDocument() {
		IEditorView activeView = getActiveEditor();
		return (activeView==null ? null : activeView.getDocument());
	}

	/**
	 * 指定されたエディタビューオブジェクトを閉じる。
	 * 
	 * @param onlyNotExists	ファイルが存在しないもののみを閉じる場合は <tt>true</tt> を指定する。
	 * 						ドキュメントがファイルと関連付けられていないものは除外する。
	 * @param withSave		閉じる前に保存する場合は <tt>true</tt> を指定する。
	 * 						ドキュメントがファイルと関連付けられていないものはファイルダイアログを表示する。
	 * @param editorTabIndex	閉じるエディタの位置を示すタブインデックス
	 * @return	エディタを閉じた場合に <tt>true</tt>、閉じなかった場合は <tt>false</tt>
	 * @throws ArrayIndexOutOfBoundsException	インデックスが正しくない場合
	 */
	public boolean closeEditorByIndex(boolean onlyNotExists, boolean withSave, int editorTabIndex) {
		IEditorView editor = getEditor(editorTabIndex);
		
		// ドキュメントファイルが存在するかを確認
		if (onlyNotExists) {
			File fDoc = editor.getDocumentFile();
			if (fDoc == null || fDoc.exists()) {
				// 関連付けられたファイルがないか、ドキュメントファイルが
				// 存在する場合は、閉じない
				return false;
			}
		}
		
		// ドキュメントの保存
		if (withSave) {
			// 閉じるエディタのドキュメントを保存する
			if (!saveEditorDocument(editor)) {
				// user canceled
				return false;
			}
		}
		
		// エディタを閉じる
		detachEditorModifiedPropertyChangeHandler(editor);
		editor.getManager().removeDocument(editor);
		_tabEditor.removeTabAt(editorTabIndex);
		editor.destroy();
		
		// 成功
		return true;
	}
	
	/**
	 * 指定されたエディタビューオブジェクトを閉じる。
	 * 
	 * @param onlyNotExists	ファイルが存在しないもののみを閉じる場合は <tt>true</tt> を指定する。
	 * 						ドキュメントがファイルと関連付けられていないものは除外する。
	 * @param withSave		閉じる前に保存する場合は <tt>true</tt> を指定する。
	 * 						ドキュメントがファイルと関連付けられていないものはファイルダイアログを表示する。
	 * @param editor		閉じるエディタの <code>IEditorView</code> オブジェクト
	 * @return	エディタを閉じた場合に <tt>true</tt>、閉じなかった場合は <tt>false</tt>
	 * @throws IllegalArgumentException	<em>editor</em> がタブに存在しない場合
	 */
	public boolean closeEditorByEditor(boolean onlyNotExists, boolean withSave, IEditorView editor) {
		int editorTabIndex = _tabEditor.indexOfComponent((Component)editor);
		if (editorTabIndex < 0)
			throw new IllegalArgumentException("editor argument does not exist in tab.");
		return closeEditorByIndex(onlyNotExists, withSave, editorTabIndex);
	}

	/**
	 * 指定されたエディタの保存先を、指定された抽象パスに設定する。
	 * @param editorTabIndex	対象エディタの位置を示すタブインデックス
	 * @param newFile			新しい保存先を示す抽象パス
	 * @throws ArrayIndexOutOfBoundsException	インデックスが正しくない場合
	 * @throws IllegalArgumentException	<em>newFile</em> が <tt>null</tt> の場合
	 */
	public void renameToEditorFile(int editorTabIndex, File newFile) {
		if (newFile == null)
			throw new IllegalArgumentException("'newFile' argument is null.");
		IEditorView editor = getEditor(editorTabIndex);
		editor.getDocument().setTargetFile(newFile);
		updateDisplayEditorTab(editorTabIndex);
		int selidx = _tabEditor.getSelectedIndex();
		if (editorTabIndex == selidx) {
			setTitle(getFrameTitle(editor.isReadOnly()));
		}
	}
	
	/**
	 * 指定されたエディタの保存先を、指定された抽象パスに設定する。
	 * @param editorTabIndex	対象エディタの位置を示すタブインデックス
	 * @param newFile			新しい保存先を示す抽象パス
	 * @throws IllegalArgumentException	<em>editor</em> がタブに存在しない場合、
	 * 										もしくは、<em>newFile</em> が <tt>null</tt> の場合
	 */
	public void renameToEditorFile(IEditorView editor, File newFile) {
		int editorTabIndex = _tabEditor.indexOfComponent((Component)editor);
		if (editorTabIndex < 0)
			throw new IllegalArgumentException("editor argument does not exist in tab.");
		renameToEditorFile(editorTabIndex, newFile);
	}

	/**
	 * このフレームに設定されているステータスバーを取得する。
	 * @return	ステータスバー
	 */
	public StatusBar getStatusBar() {
		return _statusBar;
	}

	/**
	 * 標準メニューバーを返す。このメソッドが返すメニューバーは、
	 * 現在アクティブなメニューバーではない場合もある。
	 * 
	 * @return	標準メニューバー
	 */
	public RunnerMenuBar getDefaultMainMenuBar() {
		return _defMainMenu;
	}
	
	/**
	 * アクティブなエディタメニューバーを返す。
	 * 基本的に、このフレームに現在設定されているメニューバーで
	 * <code>RunnerMenuBar</code> インスタンスである場合に、
	 * そのインスタンスを返す。この条件に当てはまらない場合は <tt>null</tt> を返す。
	 * @return	アクティブなエディタメニューバー
	 */
	public RunnerMenuBar getActiveMainMenuBar() {
		JMenuBar bar = getJMenuBar();
		if (bar instanceof RunnerMenuBar)
			return ((RunnerMenuBar)bar);
		else
			return null;
	}

	/**
	 * アクティブなエディタ用のコンテキストメニューを返す。
	 * このメソッドは、アクティブなメニューバーからエディタ用コンテキスト
	 * メニューを取得する。
	 * @return	取得したコンテキストメニューを返す。取得できなかった場合は <tt>null</tt> を返す。
	 */
	public JPopupMenu getActiveEditorConextMenu() {
		RunnerMenuBar bar = getActiveMainMenuBar();
		return (bar==null ? null : bar.getEditorContextMenu());
	}

	/**
	 * このフレームに定義されているメニューアクションを取得する。
	 * このメニューアクションは、アクティブなエディタメニューバーからのみ
	 * 取得される。
	 * @param command	検索キーとなるコマンド名
	 * @return	コマンド名に対応するメニューアクションを返す。
	 * 			アクションが未定義の場合は <tt>null</tt> を返す。
	 */
	public Action getMenuAction(String command) {
		RunnerMenuBar bar = getActiveMainMenuBar();
		if (bar != null) {
			return bar.getMenuAction(command);
		}
		return null;
	}

	/**
	 * メニューバーに設定されているメニューから、コマンド文字列に対応するメニュー項目を取得する。
	 * コマンド文字列に対応するメニュー項目がない場合は <tt>null</tt> を返す。
	 * <p>
	 * このメソッドは、メニューバーのメニューを検索し、指定されたコマンド文字列を保持する
	 * メニュー項目のうち、最初に見つかったインスタンスを返す。
	 * 
	 * @param command	検索するキーとなるコマンド文字列
	 * @return	コマンド文字列に対応するメニュー項目を返す。存在しない場合は <tt>null</tt> を返す。
	 */
	public JMenuItem getMenuItemByCommand(String command) {
		if (Strings.isNullOrEmpty(command))
			return null;
		
		JMenuBar bar = getJMenuBar();
		if (bar == null)
			return null;
		
		return JMenus.getMenuItemByCommand(bar, command);
	}

	/**
	 * 指定されたインデックスのメニューの状態を更新する。
	 * <p>
	 * このメソッドは、指定された位置にあるメニュー(<code>JMenu</code>)の全てのメニュー項目に
	 * 対して更新イベントを発生させる。この更新イベントは、メニューバーに登録されているハンドラに
	 * 通知される。
	 * <p>
	 * 基本的に、コマンド文字列が登録されていない項目は除外される。
	 * 
	 * @param menuIndex		メニューバーのメニュー位置を示すインデックス。先頭は 0。
	 * 
	 * @throws ArrayIndexOutOfBoundsException メニューインデックスが範囲外の場合
	 */
	public void updateMenuItem(int menuIndex) {
		RunnerMenuBar bar = getActiveMainMenuBar();
		if (bar != null) {
			bar.updateMenuItem(menuIndex);
		}
	}

	/**
	 * 指定されたコマンド文字列に対応するメニューの状態を更新する。
	 * <p>
	 * このメソッドは、指定されたコマンド文字列に対応するメニュー項目を検索し、その項目に
	 * 対して更新イベントを発生させる。コマンド文字列に対応するものがメニュー(<code>JMenu</code>)の
	 * 場合は、そのメニューに含まれる全てのメニュー項目に対して更新イベントを発生させる。
	 * この更新イベントは、メニューバーに登録されているハンドラに通知される。
	 * <p>
	 * 基本的に、コマンド文字列が登録されていない項目は除外される。
	 * 
	 * @param command	検索するキーとなるコマンド文字列
	 */
	public void updateMenuItem(String command) {
		RunnerMenuBar bar = getActiveMainMenuBar();
		if (bar != null) {
			bar.updateMenuItem(command);
		}
	}
	
	/**
	 * 全てのメニュー項目の状態を更新する。
	 * <p>
	 * このメソッドは、このメニューバーに登録されている全てのメニュー項目に対して
	 * 更新イベントを発生させる。この更新イベントは、メニューバーに登録されている
	 * ハンドラに通知される。
	 * <p>
	 * 基本的に、コマンド文字列が登録されていない項目は除外される。
	 */
	public void updateAllMenuItems() {
		RunnerMenuBar bar = getActiveMainMenuBar();
		if (bar != null) {
			bar.updateAllMenuItems();
		}
	}
	
	public void updateStatusBarMessage() {
		String strMessage = null;
		
		if (isTreeViewActivated()) {
			if (_viewTree.isDataFileTreeViewSelected()) {
				strMessage = _viewTree.getDataFileTreeView().getSelectedFileProperty();
			}
			else if (_viewTree.isMExecDefTreeViewSelected()) {
				strMessage = _viewTree.getMExecDefTreeView().getSelectedFileProperty();
			}
		}
		
		getStatusBar().setMessage(strMessage==null ? " " : strMessage);
	}

	/**
	 * 指定されたファイルをエディタで開く。
	 * 基本的にサポートされていないファイルは開かない。
	 * すでに開かれているファイルの場合は、そのエディタにフォーカスを設定して <tt>true</tt> を返す。
	 * @param targetFile	開くファイル
	 * @return	エディタで開いた場合は <tt>true</tt>、そうでない場合は <tt>false</tt> を返す。
	 * 			すでに開かれているファイルの場合は <tt>true</tt> を返す。
	 */
	public boolean openEditorFromFile(File targetFile) {
		// exist opened?
		for (int i = 0; i < getEditorCount(); i++) {
			IEditorView editor = getEditor(i);
			File editorFile = editor.getDocumentFile();
			if (editorFile != null && editorFile.equals(targetFile)) {
				// すでに存在している場合は、そこにフォーカスを設定して終了
				_tabEditor.setSelectedIndex(i);
				setFocusToActiveEditor();
				return true;
			}
		}
		
		// find plugin
		IComponentManager manager = PluginManager.findSupportedPlugin(targetFile);
		if (manager == null) {
			// このファイルはサポートされていない
			String errmsg = String.format(CommonMessages.getInstance().msgUnsupportedDocument, targetFile.getAbsolutePath());
			AppLogger.error(errmsg);
			ModuleRunner.showErrorMessage(this, errmsg);
			setFocusToActiveView();
			return false;	// not supported
		}
		
		// open document
		IEditorView newEditor = null;
		try {
			newEditor = manager.openDocument(this, targetFile);
		}
		catch (FileNotFoundException ex) {
			String errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_NOTFOUND, ex, targetFile.getAbsolutePath());
			AppLogger.error(errmsg, ex);
			ModuleRunner.showErrorMessage(this, errmsg);
		}
		catch (UnsupportedEncodingException ex) {
			String errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_UNSUPPORTED_ENCODING, ex);
			AppLogger.error(errmsg, ex);
			ModuleRunner.showErrorMessage(this, errmsg);
		}
		catch (IOException ex) {
			String errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_READ, ex, targetFile.getAbsolutePath());
			AppLogger.error(errmsg, ex);
			ModuleRunner.showErrorMessage(this, errmsg);
		}
		catch (OutOfMemoryError ex) {
			String errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
			AppLogger.error(errmsg, ex);
			ModuleRunner.showErrorMessage(this, errmsg);
		}
		
		// update view
		if (newEditor != null) {
			addEditor(newEditor);
			updateAllMenuItems();
			setFocusToActiveEditor();
			return true;
		} else {
			setFocusToActiveView();
			return false;
		}
	}
	
	/**
	 * 指定されたファイルをCSVファイルとしてエディタで開く。
	 * このメソッドでは、CSVファイルの読み込み設定のダイアログを表示する。
	 * すでに開かれているファイルの場合は、強制的に開き直す。
	 * チャートウィンドウが表示されている場合、そのチャートを閉じてから操作を継続するかを問い合わせる。
	 * @param targetFile	開くファイル
	 * @return	エディタで開いた場合は <tt>true</tt>、そうでない場合は <tt>false</tt> を返す。
	 * 			すでに開かれているファイルの場合は <tt>true</tt> を返す。
	 */
	public boolean openEditorFromFileWithConfigCSV(File targetFile) {
		// check chart window (@since 2.1.0)
		if (_chartWindow != null && _chartWindow.isChartWindowDocumentFile(targetFile)) {
			// ask close chart
			int ret = JOptionPane.showConfirmDialog(this,
						RunnerMessages.getInstance().confirmCloseChartByReopenFile,
						targetFile.getName(),
						JOptionPane.OK_CANCEL_OPTION);
			if (ret != JOptionPane.OK_OPTION) {
				// user canceled
				setFocusToActiveView();
				return false;
			}
			
			// close chart window
			if (_chartWindow.destroyChartByFile(targetFile)) {
				// no chart window
				deleteChartWindow();
			}
		}
		
		// CSV config
		CsvFileConfigDialog dlg = new CsvFileConfigDialog(this, targetFile);
		dlg.setConfiguration(AppSettings.CSVFILECONFIG_DLG, AppSettings.getInstance().getConfiguration());
		dlg.initialComponent();
		dlg.setVisible(true);
		dlg.dispose();
		if (dlg.getDialogResult() != IDialogResult.DialogResult_OK) {
			// user canceled
			setFocusToActiveView();
			return false;
		}
		String csvEncoding = dlg.getEncodingCharsetName();
		CsvParameters csvParams = dlg.getCsvParameters();
		
		// exist opened?
		int openedIndex = -1;
		IEditorView openedEditor = null;
		for (int i = 0; i < getEditorCount(); i++) {
			IEditorView editor = getEditor(i);
			File editorFile = editor.getDocumentFile();
			if (editorFile != null && editorFile.equals(targetFile)) {
				// すでに存在している
				openedEditor = editor;
				openedIndex = i;
				break;
			}
		}
		if (openedEditor != null) {
			if (openedEditor instanceof CsvFileView) {
				CsvFileView csvView = (CsvFileView)openedEditor;
				String curEncoding = csvView.getLastEncodingName();
				CsvParameters curParams = csvView.getDocument().getCsvParameters();
				if (Objects.isEqual(csvEncoding, curEncoding) && csvParams.equals(curParams)) {
					// 同じ設定のため、すでに開かれているファイルにフォーカスを設定
					_tabEditor.setSelectedIndex(openedIndex);
					return true;
				}
			}
			
			// 開かれているエディタを閉じる
			if (openedEditor.isModified()) {
				int ret = JOptionPane.showConfirmDialog(this,
						RunnerMessages.getInstance().confirmReopenDocument,
						openedEditor.getDocumentTitle(),
						JOptionPane.OK_CANCEL_OPTION);
				if (ret != JOptionPane.OK_OPTION) {
					// user canceled
					setFocusToActiveView();
					return false;
				}
			}
			//--- 閉じる
			closeEditorByIndex(false, false, openedIndex);
			openedEditor = null;
		}
		
		// open file
		CsvFileComponentManager manager = (CsvFileComponentManager)PluginManager.findPlugin(CsvFileComponentManager.PluginID);
		assert(manager != null);
		IEditorView newEditor = null;
		try {
			newEditor = manager.openDocument(this, targetFile, csvEncoding, csvParams);
		}
		catch (FileNotFoundException ex) {
			String errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_NOTFOUND, ex, targetFile.getAbsolutePath());
			AppLogger.error(errmsg, ex);
			ModuleRunner.showErrorMessage(this, errmsg);
		}
		catch (UnsupportedEncodingException ex) {
			String errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_UNSUPPORTED_ENCODING, ex);
			AppLogger.error(errmsg, ex);
			ModuleRunner.showErrorMessage(this, errmsg);
		}
		catch (IOException ex) {
			String errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_READ, ex, targetFile.getAbsolutePath());
			AppLogger.error(errmsg, ex);
			ModuleRunner.showErrorMessage(this, errmsg);
		}
		catch (OutOfMemoryError ex) {
			String errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
			AppLogger.error(errmsg, ex);
			ModuleRunner.showErrorMessage(this, errmsg);
		}

		if (newEditor != null) {
			addEditor(newEditor);
			updateAllMenuItems();
			setFocusToActiveEditor();
			return true;
		} else {
			setFocusToActiveView();
			return false;
		}
	}

	/**
	 * ツリービューのモジュール情報パネルの内容を、
	 * 最新の情報に更新する。
	 */
	public void refreshModuleProperties() {
		//_viewTree.refreshModuleProperties();
	}

	/**
	 * モジュール実行定義ツリーにおいて、指定された抽象パスを持つノードの表示を更新する。
	 * 表示されていないノードの場合や、ツリー階層内の抽象パスではない場合は何もしない。
	 * @param targetFile	対象の抽象パス
	 * @since 2.0.0
	 */
	public void refreshMExecDefTree(VirtualFile targetFile) {
		_viewTree.getMExecDefTreeView().refreshFileTree(targetFile);
	}
	
	/**
	 * データファイルツリーにおいて、指定された抽象パスを持つノードの表示を更新する。
	 * 表示されていないノードの場合や、ツリー階層内の抽象パスではない場合は何もしない。
	 * @param targetFile	対象の抽象パス
	 * @since 2.0.0
	 */
	public void refreshDataFileTree(VirtualFile targetFile) {
		_viewTree.getDataFileTreeView().refreshFileTree(targetFile);
	}

	/**
	 * モジュール引数設定ダイアログが表示されている場合に <tt>true</tt> を返す。
	 * このメソッドは、モジュール実行時、ならびに履歴実行時のモジュール引数設定ダイアログの
	 * どちらかが表示されている場合に <tt>true</tt> を返す。
	 * @since 1.20
	 */
	public boolean isVisibleMExecArgsEditDialog() {
		if (_dlgMExecArgsEdit != null && _dlgMExecArgsEdit.isVisible()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 履歴実行時のモジュール引数設定ダイアログが表示されている場合に <tt>true</tt> を返す。
	 * 通常実行時のモジュール引数設定ダイアログが表示されていても、このメソッドは <tt>false</tt> を返す。
	 * @since 1.22
	 */
	public boolean isVisibleHistoryModuleArgsEditDialog() {
		if (_dlgMExecArgsEdit instanceof ModuleArgsEditDialog) {
			return _dlgMExecArgsEdit.isVisible();
		}
		return false;
	}

	/**
	 * チャート表示ウィンドウもしくはチャート設定ダイアログのどちらかが表示されている場合に <tt>true</tt> を返す。
	 * @since 2.1.0
	 */
	public boolean isVisibleAnyChartWindow() {
		return (_chartWindow != null && _chartWindow.isVisibleAnyWindow());
	}

	/**
	 * チャート表示ウィンドウが表示されている場合に <tt>true</tt> を返す。
	 * @since 2.1.0
	 */
	public boolean isVisibleChartConfigDialog() {
		return (_chartWindow != null && _chartWindow.isVisibleChartConfigDialog());
	}

	/**
	 * チャート設定ダイアログが表示されている場合に <tt>true</tt> を返す。
	 * @since 2.1.0
	 */
	public boolean isVisibleChartViewFrame() {
		return (_chartWindow != null && _chartWindow.isVisibleChartViewFrame());
	}

	/**
	 * モジュール引数設定ダイアログのインスタンスを取得する。
	 * このメソッドが <tt>null</tt> ではない値を返した場合でも、そのダイアログが表示されているとは限らない。
	 * @return	インスタンスが存在する場合はそのインスタンス、それ以外の場合は <tt>null</tt> を返す。
	 * @since 1.20
	 */
	public IMExecArgsDialog getMExecArgsEditDialog() {
		return _dlgMExecArgsEdit;
	}

	/**
	 * チャートウィンドウマネージャのインスタンスを取得する。
	 * このメソッドが <tt>null</tt> ではない値を返した場合でも、各チャートウィンドウが表示されているとは限らない。
	 * @return	インスタンスが存在する場合はそのインスタンス、それ以外の場合は <tt>null</tt> を返す。
	 * @since 2.1.0
	 */
	public ChartDialogManager getChartDialogManager() {
		return _chartWindow;
	}

	/**
	 * 指定されたモデルによって生成された、新しいモジュール引数設定ダイアログを表示する。
	 * 既存のダイアログは破棄され、新しいダイアログが生成される。
	 * @param argsmodel	引数設定モデル
	 * @return	生成されたモジュール引数設定ダイアログオブジェクト
	 * @since 1.20
	 */
	public IMExecArgsDialog showMExecArgsEditDialog(final MExecArgsModel argsmodel)
	{
		if (argsmodel == null)
			throw new NullPointerException("MExecArgsModel object is null.");

		// ダイアログが表示されている場合、他のモジュール実行定義の実行は不可能とする
		// 念のため、ダイアログを削除
		deleteMExecArgsEditDialog();
		
		// 引数履歴の取得
		MExecDefHistory history = new MExecDefHistory();
		{
			VirtualFile vfHistory = argsmodel.getSettings().getExecDefDirectory().getChildFile(MExecDefFileManager.MEXECDEF_HISTORY_FILENAME);
			history.loadForTarget(vfHistory);
			history.ensureArgsTypes(argsmodel.getSettings());	// 引数型定義にあわない履歴を除去
			history.ensureMaxSize(AppSettings.getInstance().getHistoryMaxLength());	// 履歴の最大数を反映
		}
		
		// ダイアログの生成
		MExecArgsEditDialog dlgEdit = new MExecArgsEditDialog(null, argsmodel, history);
		dlgEdit.initialComponent();
		dlgEdit.setDialogHandler(_hArgsEditDlg);
		
		// 閲覧中のアクティブなエディタのドキュメントを取得、設定
		if (_activeEditor != null && _activeEditor.getDocument().hasTargetFile()) {
			VirtualFile vfViewing = ModuleFileManager.fromJavaFile(_activeEditor.getDocumentFile());
			dlgEdit.setViewingFileOnEditor(vfViewing);
		}
		
		// ダイアログの表示
		_dlgMExecArgsEdit = dlgEdit;
		dlgEdit.setVisible(true);
		return _dlgMExecArgsEdit;
	}

	/**
	 * 指定された履歴モジュール実行設定によって生成された、新しいモジュール引数設定ダイアログを表示する。
	 * 既存のダイアログは破棄され、新しいダイアログが生成される。
	 * @param modules	モジュール実行設定情報
	 * @return	生成されたモジュール引数設定ダイアログオブジェクト
	 * @since 1.22
	 */
	public IMExecArgsDialog showHistoryModuleArgsEditDialog(final RelatedModuleList modules) {
		if (modules == null)
			throw new NullPointerException("RelatedModuleList object is null.");
		if (modules.isEmpty())
			throw new IllegalArgumentException("RelatedModuleList object is empty.");

		// ダイアログが表示されている場合、他のモジュール実行定義の実行は不可能とする
		// 念のため、ダイアログを削除
		deleteMExecArgsEditDialog();
		
		// ダイアログの生成
		ModuleArgsEditDialog dlgEdit = new ModuleArgsEditDialog(null, false, modules);
		//--- 閲覧中のアクティブなエディタのドキュメントを取得、設定
		if (_activeEditor != null && _activeEditor.getDocument().hasTargetFile()) {
			VirtualFile vfViewing = ModuleFileManager.fromJavaFile(_activeEditor.getDocumentFile());
			dlgEdit.setViewingFileOnEditor(vfViewing);
		}
		
		// ダイアログの初期化
		dlgEdit.initialComponent();
		dlgEdit.setDialogHandler(_hArgsEditDlg);
		
		
		// ダイアログの表示
		_dlgMExecArgsEdit = dlgEdit;
		dlgEdit.setVisible(true);
		return _dlgMExecArgsEdit;
	}

	/**
	 * モジュール引数設定ダイアログが存在する場合に破棄する。
	 * このダイアログが表示されている場合は、閉じた後に破棄する。
	 * @since 1.20
	 */
	public void deleteMExecArgsEditDialog() {
		if (_dlgMExecArgsEdit != null) {
			_dlgMExecArgsEdit.destroy();
			_dlgMExecArgsEdit = null;
		}
	}

	/**
	 * チャートウィンドウが存在する場合に破棄する。
	 * このウィンドウが表示されている場合は、閉じた後に破棄する。
	 * @since 2.1.0
	 */
	public void deleteChartWindow() {
		if (_chartWindow != null) {
			_chartWindow.destroyChartViewFrame();
			_chartWindow.destroyChartConfigDialog();
			_chartWindow = null;
			updateMenuItem(RunnerMenuResources.ID_HIDE_SHOW_CHARTWINDOW);
		}
	}

	/**
	 * モジュール実行定義ツリーのシステムルートディレクトリを取得する。
	 * @return	ディレクトリの抽象パス
	 * @since 1.22
	 */
	public VirtualFile getMExecDefSystemRootDirectory() {
		return _viewTree.getMExecDefTreeView().getSystemRootDirectory();
	}

	/**
	 * モジュール実行定義ツリーのシステムルート表示名を取得する。
	 * @return	表示名
	 * @since 2.0.0
	 */
	public String getMExecDefSystemRootDisplayName() {
		return _viewTree.getMExecDefTreeView().getSystemRootDisplayName();
	}

	/**
	 * モジュール実行定義ツリーのユーザールートディレクトリを取得する。
	 * @return	ディレクトリの抽象パス
	 * @since 1.22
	 */
	public VirtualFile getMExecDefUserRootDirectory() {
		return _viewTree.getMExecDefTreeView().getUserRootDirectory();
	}

	/**
	 * モジュール実行定義ツリーのユーザールート表示名を取得する。
	 * @return	表示名
	 * @since 2.0.0
	 */
	public String getMExecDefUserRootDisplayName() {
		return _viewTree.getMExecDefTreeView().getUserRootDisplayName();
	}

	/**
	 * データファイルツリーのシステムルートディレクトリを取得する。
	 * @return	ディレクトリの抽象パス
	 * @since 1.22
	 */
	public VirtualFile getDataFileSystemRootDirectory() {
		return _viewTree.getDataFileTreeView().getSystemRootDirectory();
	}

	/**
	 * データファイルツリーのシステムルート表示名を取得する。
	 * @return	表示名
	 * @since 2.0.0
	 */
	public String getDataFileSystemRootDisplayName() {
		return _viewTree.getDataFileTreeView().getSystemRootDisplayName();
	}

	/**
	 * データファイルツリーのユーザールートディレクトリを取得する。
	 * @return	ディレクトリの抽象パス
	 * @since 1.22
	 */
	public VirtualFile getDataFileUserRootDirectory() {
		return _viewTree.getDataFileTreeView().getUserRootDirectory();
	}

	/**
	 * データファイルツリーのユーザールート表示名を取得する。
	 * @return	表示名
	 * @since 2.0.0
	 */
	public String getDataFileUserRootDisplayName() {
		return _viewTree.getDataFileTreeView().getUserRootDisplayName();
	}

	/**
	 * このアプリケーションのパスフォーマッタを返す。
	 * 基本的に、モジュール実行定義ツリーもしくはデータファイルツリーの
	 * ルートディレクトリを基準(ルート)とするパス表示文字列を整形する。
	 * @return	このアプリケーションのパスフォーマッタ
	 * @since 2.0.0
	 */
	static public VirtualFilePathFormatter getApplicationPathFormatter() {
		RunnerFrame frame = (RunnerFrame)ModuleRunner.getInstance().getMainFrame();
		return (frame==null ? null : frame.getFramePathFormatter());
	}

	/**
	 * このフレームのパスフォーマッタを返す。
	 * 基本的に、モジュール実行定義ツリーもしくはデータファイルツリーの
	 * ルートディレクトリを基準(ルート)とするパス表示文字列を整形する。
	 * @return	このフレームのパスフォーマッタ
	 * @since 2.0.0
	 */
	public VirtualFilePathFormatter getFramePathFormatter() {
		return _vformThis;
	}

	/**
	 * 現在の設定による、モジュール実行定義のパス表示名整形オブジェクトを取得する。
	 * @return	新しいパス表示名整形オブジェクト
	 * @since 1.22
	 */
	public VirtualFilePathFormatterList getMExecDefPathFormatter() {
		return _viewTree.getMExecDefTreeView().getTreePathFormatter();
	}

	/**
	 * 現在の設定による、データファイルのパス表示名整形オブジェクトを取得する。
	 * @return	新しいパス表示名整形オブジェクト
	 * @since 1.22
	 */
	public VirtualFilePathFormatterList getDataFilePathFormatter() {
		return _viewTree.getDataFileTreeView().getTreePathFormatter();
	}

	/**
	 * モジュール実行結果の履歴データを追加する。
	 * @param data	モジュール実行結果データ
	 * @return	追加された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @since 1.22
	 */
	public boolean addHistoryData(ModuleRuntimeData data) {
		return _viewTool.getHistoryToolView().addHistory(data);
	}

	/**
	 * モジュール実行結果から、閲覧指定されたファイルを開く。
	 * このメソッドでは、実行結果が正常であったモジュールのファイルのみを開く。
	 * @param data	モジュール実行結果データ
	 * @since 1.22
	 */
	public void openResultFilesByModuleResults(final ModuleRuntimeData data) {
		if (data != null && !data.isUserCanceled() && data.isSucceeded()) {
			openModuleResultFiles(data);
		}
	}

	/**
	 * 履歴のモジュール実行結果から、閲覧指定されたファイルを開く。
	 * このメソッドでは、実行結果が正常であったモジュールのファイルのみを開く。
	 * @param modules	モジュール実行結果データの管理オブジェクト
	 * @since 1.22
	 */
	public void openResultFilesByModuleResults(final RelatedModuleList modules) {
		if (modules != null && !modules.isEmpty()) {
			for (ModuleRuntimeData data : modules) {
				if (data != null && !data.isUserCanceled() && data.isSucceeded()) {
					if (!openModuleResultFiles(data)) {
						//--- 継続不可の場合は、ファイル表示中断
						return;
					}
				}
				else {
					//--- データが存在しない場合や、正常終了ではない場合、以降のファイル表示は中断
					return;
				}
			}
		}
	}
	
	/**
	 * 現在アクティブなビューにフォーカスを設定する。
	 * フォーカスが設定されるコンポーネントは、ビューの主要コンポーネントとなる。
	 */
	public void setFocusToActiveView() {
		if (_activeViewManager.isComponentActivated(_viewTree)) {
			// ツリービューがアクティブ
			_viewTree.requestFocusInComponent();
		}
		else if (_activeViewManager.isComponentActivated(_viewTool)) {
			// ツールビューがアクティブ
			_viewTool.requestFocusInComponent();
		}
		else if (_activeViewManager.isComponentActivated(_tabEditor)) {
			// エディタビューがアクティブ
			setFocusToActiveEditor();
		}
	}

	/**
	 * 現在アクティブなエディタコンポーネントにフォーカスを設定する。
	 */
	public void setFocusToActiveEditor() {
		IEditorView editor = getActiveEditor();
		if (editor != null) {
			editor.requestFocusInComponent();
		}
	}

	/**
	 * モジュール実行定義ツリーのフォルダ構成から、フォルダ選択ダイアログを表示し、フォルダを選択する。
	 * @param parentComponent	ダイアログの親コンポーネント
	 * @param allowCreateFolder	ダイアログ内でのフォルダ作成を許可する場合は <tt>true</tt>
	 * @param visibleSystemRoot	システムルートを表示する場合は <tt>true</tt>
	 * @param title				ダイアログのタイトル
	 * @param description		ダイアログ内の説明
	 * @param initialSelection	初期選択位置となるパス
	 * @return	選択された場合は <code>VirtualFile</code> オブジェクト、それ以外の場合は <tt>null</tt>
	 * @since 2.0.0
	 */
	public VirtualFile chooseMExecDefFolder(Component parentComponent, boolean allowCreateFolder, boolean visibleSystemRoot,
											String title, String description, VirtualFile initialSelection)
	{
		if (!(parentComponent instanceof Window) && parentComponent != null) {
			parentComponent = SwingUtilities.getWindowAncestor(parentComponent);
		}
		
		// フォルダ選択ダイアログを生成
		MExecDefFolderChooser chooser = MExecDefFolderChooser.createFolderChooser(parentComponent,
											allowCreateFolder,	// フォルダ作成の許可／禁止
											(visibleSystemRoot ? getMExecDefSystemRootDirectory() : null),	// システムルートの表示／非表示
											getMExecDefUserRootDirectory(),
											initialSelection,	// 初期選択位置
											title,				// ダイアログのタイトル
											description,		// ダイアログの説明
											CommonMessages.getInstance().TreeLabel_SelectParentFolder,	// ツリーの説明
											null,				// 入力ラベルは表示しない
											FileDialogManager.createDefaultFilenameValidator());	// 標準の Validator
		chooser.setConfiguration(AppSettings.MEXECDEF_FOLDER_CHOOSER_DLG, AppSettings.getInstance().getConfiguration());
		chooser.restoreConfiguration();
		chooser.setVisible(true);
		chooser.dispose();
		//--- 作成したフォルダをリフレッシュ
		{
			VirtualFile[] folders = chooser.getCreatedFolders();
			if (folders != null && folders.length > 0) {
				for (VirtualFile file : folders) {
					_viewTree.getMExecDefTreeView().refreshFileTree(file);
				}
			}
		}
		if (chooser.getDialogResult() != IDialogResult.DialogResult_OK) {
			// user canceled
			return null;
		}
		
		// 選択したフォルダの抽象パスを返す
		return chooser.getSelectionFile();
	}
	
	protected String makeLastSelectedFilterPathPropertyKey(String key) {
		if (Strings.isNullOrEmpty(key)) {
			return "filter";
		} else {
			return "filter." + key;
		}
	}

	/**
	 * 指定されたパスを、指定されたキーでプロパティに保存する。
	 * システムルートディレクトリの下にあるパスであれば相対パス、それ以外は絶対パスとして保存する。
	 * @param lastSelection	保存するパス
	 * @param key	保存先とするプロパティ、<tt>null</tt> もしくは空文字の場合は、共通のプロパティへ保存
	 * @since 3.1.0
	 */
	public void saveLastSelectedFilterPath(VirtualFile lastSelection, String key) {
		AppSettings.getInstance().setLastSelectedPath(getMExecDefSystemRootDirectory(),
														lastSelection,
														makeLastSelectedFilterPathPropertyKey(key));
	}

	/**
	 * 指定されたプロパティキーからパスを読み込み、絶対パスに変換する。
	 * 相対パスとして保存されていたパスは、指定されたシステムルートディレクトリに対する相対パスとして展開する。
	 * @param key	読み込むプロパティ、<tt>null</tt> もしくは空文字の場合は、共通のプロパティから読み込む
	 * @return	読み込まれた絶対パス、読み込めなかった場合は <tt>null</tt>
	 * @since 3.1.0
	 */
	public VirtualFile loadLastSelectedFilterPath(String key) {
		return AppSettings.getInstance().getLastSelectedPath(getMExecDefSystemRootDirectory(), makeLastSelectedFilterPathPropertyKey(key));
	}
	
	/**
	 * モジュール実行定義ツリーのファイル構成から、フィルタ選択ダイアログを表示し、フィルタを選択する。
	 * @param parentComponent	ダイアログの親コンポーネント
	 * @param visibleSystemRoot	システムルートを表示する場合は <tt>true</tt>
	 * @param title				ダイアログのタイトル
	 * @param description		ダイアログ内の説明
	 * @param initialSelection	初期選択位置となるパス
	 * @param handler			ファイル選択時のハンドラ
	 * @return	選択された場合は <code>VirtualFile</code> オブジェクト、それ以外の場合は <tt>null</tt>
	 * @since 2.0.0
	 */
	public VirtualFile chooseMExecDefFile(Component parentComponent, boolean visibleSystemRoot,
											String title, String description, VirtualFile initialSelection,
											IMExecDefFileChooserHandler handler)
	{
		if (!(parentComponent instanceof Window) && parentComponent != null) {
			parentComponent = SwingUtilities.getWindowAncestor(parentComponent);
		}
		
		// フィルタ選択ダイアログを生成
		MExecDefFileChooser chooser = MExecDefFileChooser.createFileChooser(parentComponent,
											false,				// フォルダ作成は禁止
											(visibleSystemRoot ? getMExecDefSystemRootDirectory() : null),	// システムルートの表示／非表示
											getMExecDefUserRootDirectory(),
											initialSelection,	// 初期選択位置
											title,				// ダイアログのタイトル
											description,		// ダイアログの説明
											RunnerMessages.getInstance().MExecDefFileChooser_TreeLabel_SelectFilter,	// ツリーの説明
											null,				// 入力ラベルは表示しない
											null,				// フィルタはデフォルト
											FileDialogManager.createDefaultFilenameValidator(),		// 標準の Validator
											handler);
		chooser.setConfiguration(AppSettings.MEXECDEF_FILE_CHOOSER_DLG, AppSettings.getInstance().getConfiguration());
		chooser.restoreConfiguration();
		chooser.setVisible(true);
		chooser.dispose();
		if (chooser.getDialogResult() != IDialogResult.DialogResult_OK) {
			// user canceled
			return null;
		}
		
		// 念のため、チェック
		VirtualFile selectedFile = chooser.getSelectionFile();
		if (!MExecDefFileManager.isModuleExecDefData(selectedFile)) {
			throw new IllegalStateException("Selected file is not MExecDef data folder : " + String.valueOf(selectedFile));
		}
		// 選択したフィルタを返す
		return chooser.getSelectionFile();
	}

	/**
	 * 実行モニタとなるコンソールウィンドウを、フレームに登録する。
	 * @param monitor	登録するモニタ
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public void addAsyncProcessMonitor(final AsyncProcessMonitorWindow monitor) {
		if (monitor == null)
			throw new NullPointerException("AsyncProcessMonitorWindow object is null.");
		_viewTool.getRunningToolView().addRunningMonitor(monitor);
		if (monitor.isDisplayable() && monitor.isVisible()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					monitor.toFront();
				}
			});
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * モジュール実行結果から、閲覧してされたファイルを開く。
	 * このメソッドでは、モジュール実行結果は考慮しない。
	 * @param data	モジュール実行結果データ
	 * @return	正常に完了した場合は <tt>true</tt>、開く操作がキャンセルもしくは異常終了した場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @since 1.22
	 */
	protected boolean openModuleResultFiles(final ModuleRuntimeData data) {
		loopArgs : for (IModuleArgConfig arg : data) {
			if (ModuleArgType.OUT == arg.getType() && arg.getShowFileAfterRun()) {
				File targetFile = ModuleFileManager.toJavaFile(arg.getValue());
				//--- 存在しないファイルは無視
				if (targetFile==null || !targetFile.exists()) {
					continue loopArgs;
				}
				//--- 既に開かれているファイルか?
				targetFile = targetFile.getAbsoluteFile();
				for (int i = 0; i < getEditorCount(); i++) {
					IEditorView editor = getEditor(i);
					if (targetFile.equals(editor.getDocumentFile())) {
						// すでに存在している場合は、そこにフォーカスを設定して次のファイルへ
						_tabEditor.setSelectedIndex(i);
						continue loopArgs;
					}
				}
				//--- open file
				IComponentManager manager = PluginManager.findSupportedPlugin(targetFile);
				if (manager == null) {
					// このファイルはサポートされていない
					String errmsg = String.format(CommonMessages.getInstance().msgUnsupportedDocument, targetFile.getAbsolutePath());
					AppLogger.error(errmsg);
					Application.showErrorMessage(RunnerFrame.this, errmsg);
					continue loopArgs;
				}
				IEditorView newEditor = null;
				try {
					newEditor = manager.openDocument(RunnerFrame.this, targetFile);
					if (newEditor == null) {
						return false;	// ユーザによるキャンセルのため、処理中断
					}
				}
				catch (FileNotFoundException ex) {
					String errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_NOTFOUND, ex, targetFile.getAbsolutePath());
					AppLogger.error(errmsg, ex);
					ModuleRunner.showErrorMessage(RunnerFrame.this, errmsg);
				}
				catch (UnsupportedEncodingException ex) {
					String errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_UNSUPPORTED_ENCODING, ex);
					AppLogger.error(errmsg, ex);
					ModuleRunner.showErrorMessage(RunnerFrame.this, errmsg);
				}
				catch (IOException ex) {
					String errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_READ, ex, targetFile.getAbsolutePath());
					AppLogger.error(errmsg, ex);
					ModuleRunner.showErrorMessage(RunnerFrame.this, errmsg);
				}
				catch (OutOfMemoryError ex) {
					String errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
					AppLogger.error(errmsg, ex);
					ModuleRunner.showErrorMessage(RunnerFrame.this, errmsg);
					return false;	// メモリ不足のため、処理継続不可
				}
				if (newEditor != null) {
					addEditor(newEditor);
				}
			}
		}

		// 処理継続
		return true;
	}

	/**
	 * ファイルシステムによってドキュメントのソースファイルが更新されている場合に
	 * 再読込を行う処理を有効にするかどうかを設定する。
	 * デフォルトでは有効となっている。
	 * @param toEnable	有効にする場合は <tt>true</tt>、無効にする場合は <tt>false</tt>
	 * @since 1.17
	 */
	private void setEnableRefreshEditorDocumentWhenUpdating(boolean toEnable) {
		_isEnabledRefreshEditorDocumentWhenUpdating = toEnable;
	}

	/**
	 * ファイルシステムによってドキュメントのソースファイルが更新されている場合は再読込を行う。
	 * 再読込では、ユーザーに再読込を行うかどうか問い合わせる。
	 * @param editor	再読込を行うエディタ
	 * @return	再読込が行われた場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 1.17
	 */
	private boolean refreshEditorDocumentFromSourceFileAsNeeded(IEditorView editor) {
		if (!_isEnabledRefreshEditorDocumentWhenUpdating) {
			// refresh skipped
			return false;
		}
		
		if (editor==null || !isUpdatedEditorSourceFileWhenActive(editor)) {
			// no updating
			return false;
		}
		
		// confirm
		setEnableRefreshEditorDocumentWhenUpdating(false);
		// ask close chart window (@since 2.1.0)
		int ret;
		if (_chartWindow != null && _chartWindow.isChartWindowDocumentFile(editor.getDocumentFile())) {
			ret = JOptionPane.showConfirmDialog(this,
					RunnerMessages.getInstance().confirmFileChangedCloseChartAndReplace,
					editor.getDocumentTitle(), JOptionPane.YES_NO_OPTION);
		} else {
			ret = JOptionPane.showConfirmDialog(this,
						RunnerMessages.getInstance().confirmFileChangedReplace,
						editor.getDocumentTitle(), JOptionPane.YES_NO_OPTION);
		}
		setEnableRefreshEditorDocumentWhenUpdating(true);
		if (ret != JOptionPane.YES_OPTION) {
			// user canceled
			//--- 再読込はキャンセルされたため、最新の最終更新日時を保存
			refreshEditorSourceLastModifiedTime(editor);
			return false;
		}
		
		// チャートウィンドウの破棄 (@since 2.1.0)
		if (_chartWindow != null && _chartWindow.destroyChartByFile(editor.getDocumentFile())) {
			// no chart window
			deleteChartWindow();
		}
		
		// ドキュメントの再読込
		setEnableRefreshEditorDocumentWhenUpdating(false);
		boolean result = false;
		try {
			editor.refreshDocumentFromSourceFile();
			result = true;
		}
		catch (FileNotFoundException ex) {
			String errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_NOTFOUND, ex, editor.getDocumentFile().getAbsolutePath());
			AppLogger.error(errmsg, ex);
			ModuleRunner.showErrorMessage(this, errmsg);
		}
		catch (UnsupportedEncodingException ex) {
			String errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_UNSUPPORTED_ENCODING, ex);
			AppLogger.error(errmsg, ex);
			ModuleRunner.showErrorMessage(this, errmsg);
		}
		catch (IOException ex) {
			String errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_READ, ex, editor.getDocumentFile().getAbsolutePath());
			AppLogger.error(errmsg, ex);
			ModuleRunner.showErrorMessage(this, errmsg);
		}
		catch (OutOfMemoryError ex) {
			String errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
			AppLogger.error(errmsg, ex);
			ModuleRunner.showErrorMessage(this, errmsg);
		}
		setEnableRefreshEditorDocumentWhenUpdating(true);
		
		// update display
		if (result) {
			removeEditorSourceLastModifiedTime(editor);
			updateDisplayCurrentTab();
			updateAllMenuItems();
			setFocusToActiveEditor();
		} else {
			refreshEditorSourceLastModifiedTime(editor);
			setFocusToActiveView();
		}
		return result;
	}

	/**
	 * ドキュメントの保存先ファイルがファイル読み込み時点から更新されている場合に、
	 * ファイルを上書きするかどうかを確認する。
	 * @param editor	確認するエディタ
	 * @return	上書き可能なら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 1.17
	 */
	private boolean confirmOverwriteEditorSourceFileWhenUpdateConflict(IEditorView editor) {
		if (editor==null || !isUpdatedEditorSourceFileWhenSave(editor)) {
			// no updating
			return true;
		}
		
		// confirm
		setEnableRefreshEditorDocumentWhenUpdating(false);
		int ret = JOptionPane.showConfirmDialog(this,
									RunnerMessages.getInstance().confirmUpdateConflictReplace,
									RunnerMessages.getInstance().confirmTitle_UpdateConflictReplace, JOptionPane.YES_NO_OPTION);
		setEnableRefreshEditorDocumentWhenUpdating(true);
		if (ret != JOptionPane.YES_OPTION) {
			// user canceled
			return false;
		}
		
		// ignore conflict
		return true;
	}

	/**
	 * ドキュメント保存先ファイルが、ファイル読み込み時点から更新されているかを判定する。
	 * このメソッドはエディタがアクティブになった時点で呼び出すものであり、前回チェックした
	 * ときの最終更新日時と現在の最終更新日時を比較する。
	 * <p>新規作成のドキュメントや、保存先ファイルが指定されていないドキュメントの場合、
	 * このメソッドは <tt>false</tt> を返す。
	 * @param editor	判定するエディタ
	 * @return	前回チェック時点からファイルが更新されていれば <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 1.17
	 */
	private boolean isUpdatedEditorSourceFileWhenActive(IEditorView editor) {
		IEditorDocument doc = editor.getDocument();
		if (doc == null) {
			return false;
		}
		
		if (doc.isNewDocument() || !doc.hasTargetFile()) {
			// 新規作成ドキュメントの場合は、更新チェックしない
			return false;
		}

		long oldTime;
		{
			Long editorTime = _mapEditorSourceFileLastModifiedTime.get(editor);
			if (editorTime != null) {
				oldTime = editorTime;
			} else {
				oldTime = doc.lastModifiedTimeWhenLoadingTargetFile();
			}
		}
		long curTime = doc.lastModifiedTimeWhenCurrentTargetFile();
		if (oldTime != curTime) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * ドキュメント保存先ファイルが、ファイル読み込み時点から更新されているかを判定する。
	 * このメソッドはドキュメント保存時に呼び出すものであり、ファイル読み込み時点の
	 * 最終更新日時と現在の最終更新日時を比較する。
	 * <p>新規作成のドキュメントや、保存先ファイルが指定されていないドキュメントの場合、
	 * このメソッドは <tt>false</tt> を返す。
	 * @param editor	判定するエディタ
	 * @return	読込時点からファイルが更新されていれば <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 1.17
	 */
	private boolean isUpdatedEditorSourceFileWhenSave(IEditorView editor) {
		IEditorDocument doc = editor.getDocument();
		if (doc == null) {
			return false;
		}
		
		if (doc.isNewDocument() || !doc.hasTargetFile()) {
			// 新規作成ドキュメントの場合は、更新チェックしない
			return false;
		}
		
		long oldTime = doc.lastModifiedTimeWhenLoadingTargetFile();
		long curTime = doc.lastModifiedTimeWhenCurrentTargetFile();
		if (oldTime != curTime) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 指定されたエディタのドキュメントについて、ソースファイルの最終更新日時を保存する。
	 * @param editor	ドキュメントを保持するエディタ
	 * @since 1.17
	 */
	private void refreshEditorSourceLastModifiedTime(IEditorView editor) {
		IEditorDocument doc = editor.getDocument();
		if (doc != null && doc.hasTargetFile()) {
			// ドキュメント保存先ファイルの、現在の最終更新日時を保存
			long lmTime = doc.lastModifiedTimeWhenCurrentTargetFile();
			_mapEditorSourceFileLastModifiedTime.put(editor, lmTime);
		}
	}

	/**
	 * 指定されたエディタのドキュメントについて、保存されているソースファイル最終更新日時を消去する。
	 * @param editor	ドキュメントを保持するエディタ
	 * @since 1.17
	 */
	private void removeEditorSourceLastModifiedTime(IEditorView editor) {
		_mapEditorSourceFileLastModifiedTime.remove(editor);
	}

	/**
	 * アクティブなメニューバーを設定する。メニューバーが更新されると、
	 * 関連するツールバーも更新される。
	 * 
	 * @param menuBar
	 */
	private void setActiveMainMenuBar(RunnerMenuBar menuBar) {
		Validations.validNotNull(menuBar);
		JMenuBar oldBar = getJMenuBar();
		
		// 新しいメニューバーがすでに設定済みなら、処理しない
		if (oldBar == menuBar)
			return;
		
		// ツリービューのコンテキストメニューは、ツリービューの
		// イベントハンドラで取得する。

		// 古いツールバーを削除
		if (oldBar instanceof RunnerMenuBar) {
			JToolBar oldToolBar = ((RunnerMenuBar)oldBar).getMainToolBar();
			getContentPane().remove(oldToolBar);
		}
		
		// 新しいメニューとツールバーを設定する
		if (menuBar.getMenuHandler() != this) {
			menuBar.setMenuHandler(this);	// メニューハンドラをこのフレームにする。
		}
		JToolBar toolBar = menuBar.getMainToolBar();
		toolBar.setFloatable(false);
		toolBar.setFocusable(false);
		setJMenuBar(menuBar);
		getContentPane().add(toolBar, BorderLayout.NORTH);
		menuBar.revalidate();
		toolBar.revalidate();
	}

	/**
	 * 規定の標準ウィンドウサイズを返す。
	 * @since 3.2.2
	 */
	@Override
	protected Dimension getDefaultSize() {
		return DM_DEF_SIZE;
	}

	/**
	 * アプリケーションのプロパティから、コンポーネントの状態を復帰する。
	 */
	private void restoreSettings() {
		AppSettings settings = AppSettings.getInstance();

		// restore window states
		restoreWindowStatus(settings.getConfiguration(), AppSettings.MAINFRAME);
//		int      mainState = settings.getWindowState(AppSettings.MAINFRAME);
//		Point     mainLoc   = settings.getWindowLocation(AppSettings.MAINFRAME);
//		Dimension mainSize  = settings.getWindowSize(AppSettings.MAINFRAME);
//		if (mainLoc != null) {
//			if (mainSize != null) {
//				Rectangle rc = SwingTools.convertIntoAllScreenDesktopBounds(new Rectangle(mainLoc, mainSize));
//				if (rc != null) {
//					mainLoc  = rc.getLocation();
//					mainSize = rc.getSize();
//				} else {
//					mainLoc  = null;
//					mainSize = null;
//				}
//			} else {
//				mainLoc = null;
//			}
//		}
//		else if (mainSize != null) {
//			mainSize = SwingTools.convertIntoDesktop(mainSize);
//		}
//		//--- Window size
//		if (mainSize != null) {
//			_lastFrameSize.setSize(mainSize);
//		} else {
//			_lastFrameSize.setSize(800, 600);
//		}
//		this.setSize(_lastFrameSize);
//		//--- Window location
//		if (mainLoc != null) {
//			_lastFrameLocation = new Point(mainLoc);
//			// TODO: Debug
//			System.err.println("RunnerFrame#restoreSettings() : set last frame location : " + _lastFrameLocation);
//			this.setLocation(mainLoc);
//		} else {
//			// default location
//			_lastFrameLocation = null;
//			this.setLocationRelativeTo(null);
//		}
//		//--- Window state
//		if (mainState != JFrame.NORMAL) {
//			this.setExtendedState(mainState);
//		}
		
		// restore View states
		//--- Outer Divider location
		int divloc = settings.getDividerLocation(AppSettings.OUTER_FRAME);
		if (divloc < 0) {
			// default location
			Dimension dm = getSize();
			divloc = dm.width / 4;
		}
		_viewSplit.setDividerLocation(divloc);
		//--- Tool Divider location
		divloc = settings.getDividerLocation(AppSettings.TOOL_FRAME);
		if (divloc < 0) {
			// default location
			Dimension dm = _viewSplit.getRightComponent().getPreferredSize();
			if (dm.height < 100) {
				dm = this.getSize();
			}
			divloc = dm.height / 3 * 2;
		}
		_toolSplit.setDividerLocation(divloc);
		
		// restore View fonts
		updateFontBySettings();
	}

	/**
	 * 現在のコンポーネントの状態を、アプリケーションのプロパティとして保存する。
	 */
	private void storeSettings() {
		AppSettings settings = AppSettings.getInstance();
		// Window states
		storeWindowStatus(settings.getConfiguration(), AppSettings.MAINFRAME);
//		settings.setWindowState(AppSettings.MAINFRAME, this.getExtendedState());
//		settings.setWindowLocation(AppSettings.MAINFRAME, this._lastFrameLocation);
//		settings.setWindowSize(AppSettings.MAINFRAME, this._lastFrameSize);
		// Views states
		settings.setDividerLocation(AppSettings.OUTER_FRAME, _viewSplit.getDividerLocation());
		settings.setDividerLocation(AppSettings.TOOL_FRAME, _toolSplit.getDividerLocation());
	}
	
	/**
	 * メインフレームをクローズ可能か検証する。
	 * 
	 * @return クローズ可能なら <tt>true</tt> を返す。
	 */
	@Override
	protected boolean canCloseWindow() {
		AppLogger.debug("Can close window?");
		
		// すべてのチャートウィンドウを閉じる
		deleteChartWindow();
		
		// 全てのドキュメントを閉じる
		if (!closeAllEditors()) {
			// user Canceled
			AppLogger.debug("--- user canceled!");
			return false;
		}
		
		// モジュール実行引数設定ダイアログを破棄
		deleteMExecArgsEditDialog();
		
		// 検索ダイアログを破棄
		if (_dlgFind != null) {
			_dlgFind.close();
		}
		
		// save preferences
		storeSettings();
		
		// 実行中のモジュールをシャットダウン
		if (!_viewTool.getRunningToolView().shutdown(this)) {
			// user canceled
			AppLogger.debug("--- user canceled!");
			return false;
		}
		AppLogger.debug("--- close application");
		
		return true;
	}
	
	/**
	 * 指定のエディタがクローズ可能かを検証する。
	 * 変更が保存されていないドキュメントを含む場合は、ユーザーに
	 * 確認するダイアログを表示する。
	 * 
	 * @param editor 対象となるエディタ
	 * @return 閉じることが可能な状態なら <tt>true</tt> を返す。
	 */
	private boolean canEditorDocumentClose(final IEditorView editor) {
		if (editor.isModified()) {
			int ret = JOptionPane.showConfirmDialog(this,
					CommonMessages.getInstance().confirmSaveChanges,
					editor.getDocumentTitle(),
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (ret == JOptionPane.YES_OPTION) {
				// 保存
				if (!saveEditorDocument(editor)) {
					// user canceled
					return false;
				}
			}
			else if (ret != JOptionPane.NO_OPTION) {
				// user canceled
				return false;
			}
		}
		
		// クローズ可能
		return true;
	}

	/**
	 * 指定のエディタに関連付けられたドキュメントを既存のファイルに
	 * 上書きする。ファイルが関連付けられていない場合は、ファイル保存
	 * ダイアログを表示する。
	 * 
	 * @param editor	保存対象のドキュメントを保持するエディタ
	 * @return	保存が完了した場合に <tt>true</tt> を返す。
	 */
	private boolean saveEditorDocument(final IEditorView editor) {
		final IEditorDocument document = editor.getDocument();
		if (!document.hasTargetFile()) {
			return saveAsEditorDocument(editor);
		}
		
		if (editor.isReadOnly()) {
			String errmsg = String.format(
					CommonMessages.getInstance().msgCannotWriteCauseReadOnly,
					document.getTargetFile().getName());
			AppLogger.error("RunnerFrame#saveEditorDocument(\"" + document.getTargetFile() + "\") : " + errmsg);
			ModuleRunner.showWarningMessage(this, errmsg);
			return false;
		}
		
		if (FileChooserManager.isSystemTemporaryFile(editor.getDocumentFile())) {
			return saveAsEditorDocument(editor);
		}
		
		// check conflict
		if (!confirmOverwriteEditorSourceFileWhenUpdateConflict(editor)) {
			// user canceled
			return false;
		}
		
		// save current document to current file
		editor.refreshDocumentSettings();
		boolean result = editor.getManager().onSaveComponent(this, editor);
		if (result) {
			// 保存成功
			removeEditorSourceLastModifiedTime(editor);
		} else {
			// 保存失敗
			refreshEditorSourceLastModifiedTime(editor);
		}
		return result;
	}
	
	/**
	 * エディタのドキュメントを任意のファイルに保存する。
	 * このメソッドでは、ファイル保存ダイアログも表示する。
	 * 
	 * @param editor 保存対象のドキュメントを保持するエディタ
	 * @return 保存が完了した場合に <tt>true</tt> を返す。
	 */
	private boolean saveAsEditorDocument(final IEditorView editor) {
		IComponentManager manager = editor.getManager();
		File orgRecFile = FileChooserManager.getRecommendedDirectory();
		FileChooserManager.setRecommendedDirectory(_viewTree.getDataFileTreeView().getUserRootDirectory());
		manager.setLastSelectedFile(FileChooserManager.getInitialDocumentFile(editor.getDocumentFile()));
		FileChooserManager.setRecommendedDirectory(orgRecFile);
		
		boolean result = manager.onSaveAsComponent(this, editor);
		if (result) {
			// 保存成功
			removeEditorSourceLastModifiedTime(editor);
		} else {
			// 保存失敗
			refreshEditorSourceLastModifiedTime(editor);
		}
		
		File lastFile = manager.getLastSelectedFile();
		if (lastFile != null) {
			FileChooserManager.setLastChooseDocumentFile(lastFile);
		}
		
		return result;
	}

	/**
	 * エディタビューから、エディタ用タブのタイトルを取得する。
	 * 
	 * @param editor タイトルを取得する対象のエディタ
	 * @return タイトル文字列
	 */
	private String getEditorTabTitle(final IEditorView editor) {
		String title;
		if (editor != null) {
			if (editor.isModified()) {
				title = CommonMessages.getInstance().editingDocumentModifier + editor.getDocumentTitle();
			} else {
				title = editor.getDocumentTitle();
			}
		} else {
			title = null;
		}
		return title;
	}
	
	private String getEditorTabTooltip(final IEditorView editor) {
		String tooltip = null;
		if (editor != null) {
			File fDocument = editor.getDocumentFile();
			if (fDocument != null) {
				VirtualFile vfdoc = ModuleFileManager.fromJavaFile(editor.getDocumentFile());
				tooltip = _viewTree.getDataFileTreeView().getTreeModel().formatFilePath(vfdoc);
			}
		}
		return tooltip;
	}

	/**
	 * アクティブなビューのタイトルを含めた、このフレームの
	 * タイトル文字列を返す。
	 * @return	フレームのタイトル文字列
	 */
	private String getFrameTitle(boolean isReadOnly) {
		String title = RunnerMessages.getInstance().appMainTitle;
		if (_activeEditor != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(_activeEditor.getDocumentTitle());
			sb.append("[");
			sb.append(_activeEditor.getLastEncodingName());
			sb.append("] ");
			if (isReadOnly) {
				sb.append(CommonMessages.getInstance().Message_ReadOnly);
				sb.append(" ");
			}
			sb.append("- ");
			sb.append(title);
			title = sb.toString();
		}
		return title;
	}
	
	/**
	 * エディタタブにエディタビューを追加する。
	 * @param editor	タブに追加するエディタ
	 */
	private void addEditor(IEditorView editor) {
		attachEditorModifiedPropertyChangeHandler(editor);
		String tabTitle = getEditorTabTitle(editor);
		String tabTooltip = getEditorTabTooltip(editor);
		if (tabTooltip != null)
			_tabEditor.addTab(tabTitle, null, editor.getComponent(), tabTooltip);
		else
			_tabEditor.addTab(tabTitle, editor.getComponent());
		_tabEditor.setSelectedComponent(editor.getComponent());
		editor.requestFocusInComponent();
	}

	/**
	 * 現在アクティブなエディタの情報を更新する。
	 * 更新する情報は、アプリケーションタイトル、タブのタイトル、タブのツールチップとなる。
	 */
	private void updateDisplayCurrentTab() {
		int selidx = _tabEditor.getSelectedIndex();
		if (selidx >= 0) {
			updateDisplayEditorTab(selidx);
			setTitle(getFrameTitle(getEditor(selidx).isReadOnly()));
		} else {
			setTitle(getFrameTitle(false));
		}
	}

	/**
	 * 指定されたインデックスにあるタブの情報を更新する。
	 * 更新する情報は、タブのタイトル、タブのツールチップとなる。
	 * @param tabIndex	タブのインデックス
     * @exception IndexOutOfBoundsException インデックスが有効ではない場合
	 */
	private void updateDisplayEditorTab(int tabIndex) {
		final IEditorView editor = getEditor(tabIndex);
		String tabTitle = getEditorTabTitle(editor);
		String tabTooltip = getEditorTabTooltip(editor);
		_tabEditor.setTitleAt(tabIndex, tabTitle);
		_tabEditor.setToolTipTextAt(tabIndex, tabTooltip);
	}

	/**
	 * エディタのタブの内容をすべて更新する
	 * @since 1.10
	 */
	private void updateDisplayAllEditorTab() {
		int len = _tabEditor.getTabCount();
		for (int i = 0; i < len; i++) {
			updateDisplayEditorTab(i);
		}
	}
	
	private void attachEditorModifiedPropertyChangeHandler(IEditorView editor) {
		editor.getComponent().addPropertyChangeListener(IEditorView.PROP_MODIFIED, _hEditorModified);
	}
	
	private void detachEditorModifiedPropertyChangeHandler(IEditorView editor) {
		editor.getComponent().removePropertyChangeListener(IEditorView.PROP_MODIFIED, _hEditorModified);
		//--- 最終更新日の保存情報を破棄
		removeEditorSourceLastModifiedTime(editor);
	}

	/**
	 * 指定されたタブ位置にあるエディタを閉じる。
	 * 変更が保存されていないドキュメントを含む場合は、ユーザーに
	 * 確認するダイアログを表示する。
	 * 
	 * @param tabIndex 対象のエディタを指すタブ・インデックス
	 * @return 閉じた場合に true を返す。
	 */
	private boolean closeEditor(int tabIndex) {
		IEditorView editor = getEditor(tabIndex);
		boolean flgClosed = false;
		if (editor != null && canEditorDocumentClose(editor)) {
			// modified @since 2.1.0
			if (_chartWindow != null && _chartWindow.isChartWindowDocumentFile(editor.getDocumentFile())) {
				// editor file is shown chart
				//--- close chart
				if (_chartWindow.destroyChartByFile(editor.getDocumentFile())) {
					// no chart window
					deleteChartWindow();
				}
			}
			// close document
			detachEditorModifiedPropertyChangeHandler(editor);
			editor.getManager().removeDocument(editor);
			_tabEditor.removeTabAt(tabIndex);
			editor.destroy();
			flgClosed = true;
		}
		return flgClosed;
	}

	/**
	 * 全てのエディタを閉じる。
	 * 変更が保存されていないドキュメントを含む場合は、ユーザーに
	 * 確認するダイアログを表示する。
	 * 
	 * @return 全てのエディタを閉じた場合にのみ true を返す。
	 */
	private boolean closeAllEditors() {
		setEnableRefreshEditorDocumentWhenUpdating(false);
		
		// 変更のないドキュメントを全て閉じる
		boolean removed = false;
		for (int i = _tabEditor.getTabCount() - 1; i >= 0; i--) {
			final IEditorView editor = getEditor(i);
			if (!editor.isModified()) {
				// 更新されていないので、すぐに閉じる
				// modified @since 2.1.0
				if (_chartWindow != null && _chartWindow.isChartWindowDocumentFile(editor.getDocumentFile())) {
					// editor file is shown chart
					//--- close chart
					if (_chartWindow.destroyChartByFile(editor.getDocumentFile())) {
						// no chart window
						deleteChartWindow();
					}
				}
				// close document
				detachEditorModifiedPropertyChangeHandler(editor);
				editor.getManager().removeDocument(editor);
				_tabEditor.removeTabAt(i);
				editor.destroy();
				removed = true;
			}
		}
		//--- 表示を合わせる為、アクティブエディタを更新
		if (removed) {
			updateActiveEditor();
		}
		
		// 残りのタブを先頭から閉じる
		boolean allClosed = true;
		while (_tabEditor.getTabCount() > 0) {
			_tabEditor.setSelectedIndex(0);
			if (!closeEditor(0)) {
				// user canceled
				allClosed = false;
				break;
			}
			else {
				// 削除に成功したので、アクティブエディタを更新
				updateActiveEditor();
			}
		}
		
		// 全て閉じた
		//updateAllMenuItems(); -- 更新しているので、不要
		setEnableRefreshEditorDocumentWhenUpdating(true);
		//--- 必要に応じてドキュメントの更新
		refreshEditorDocumentFromSourceFileAsNeeded(getActiveEditor());
		return allClosed;
	}

	/**
	 * アプリケーション設定により、ビューの各フォントを更新する。
	 */
	private void updateFontBySettings() {
		updateEditorFont();
	}

	/**
	 * 各プラグインのエディタフォントをアプリケーション設定に従い更新する。
	 */
	private void updateEditorFont() {
		// フォントを取得
		Font fontForCsvViewer = AppSettings.getInstance().getFont(AppSettings.VIEW_CSVVIEWER);
		
		// 各プラグインのフォントを更新
		int num = PluginManager.getPluginCount();
		for (int i = 0; i < num; i++) {
			IComponentManager manager = PluginManager.getPlugin(i);
			if (fontForCsvViewer != null && CsvFileComponentManager.PluginID.equals(manager.getID())) {
				manager.setEditorFont(fontForCsvViewer);
			}
		}
	}

	/*
	 * コンソールの表示フォントをアプリケーション設定に従い更新する。
	 *
	private void updateConsoleFont() {
		// TODO: 将来、実装する
		Font font = AppSettings.getInstance().getFont(AppSettings.CONSOLE);
		Font oldFont = paneConsole.getTextComponent().getFont();
		if (font != null && (oldFont == null || !oldFont.equals(font))) {
			// update font
			paneConsole.getTextComponent().setFont(font);
		}
	}
	*/

	/*
	 * コンパイルメッセージ表示フォントをアプリケーション設定に従い更新する。
	 *
	private void updateCompileFont() {
		// TODO: 将来、実装する
		Font font = AppSettings.getInstance().getFont(AppSettings.COMPILE);
		Font oldFont = paneBuild.getTextComponent().getFont();
		if (font != null && (oldFont == null || !oldFont.equals(font))) {
			// update font
			paneBuild.getTextComponent().setFont(font);
		}
	}
	*/
	
	private void showFindReplaceDialog(FindReplaceInterface findHandler) {
		if (_dlgFind == null) {
			_dlgFind = new FindDialog(this, findHandler);
		} else {
			_dlgFind.setHandler(findHandler);
		}
		_dlgFind.setVisible(true);
		_dlgFind.setFocus();
	}
	
	private void updateFindReplaceDialogHandler() {
		if (_dlgFind != null && _dlgFind.isVisible()) {
			IEditorView editor = getActiveEditor();
			if (editor != null)
				_dlgFind.setHandler(editor.getFindReplaceHandler());
			else
				_dlgFind.setHandler(null);
		}
	}

	/**
	 * フィルタツリーのシステムルートディレクトリを基準とするパスフォーマッタを生成する。
	 * @return	<code>DefaultVirtualFilePathFormatter</code> オブジェクトを返す。
	 * 			システムルートディレクトリが未定義の場合は <tt>null</tt> を返す。
	 * @since 2.0.0
	 */
	private DefaultVirtualFilePathFormatter createMExecDefSystemRootPathFormatter() {
		VirtualFile vfRoot = getMExecDefSystemRootDirectory();
		if (vfRoot != null) {
			return new DefaultVirtualFilePathFormatter(vfRoot, getMExecDefSystemRootDisplayName(), true);
		} else {
			return null;
		}
	}

	/**
	 * フィルタツリーのユーザー定義ルートディレクトリを基準とするパスフォーマッタを生成する。
	 * @return	<code>DefaultVirtualFilePathFormatter</code> オブジェクトを返す。
	 * 			ユーザー定義ルートディレクトリが未定義の場合は <tt>null</tt> を返す。
	 * @since 2.0.0
	 */
	private DefaultVirtualFilePathFormatter createMExecDefUserRootPathFormatter() {
		VirtualFile vfRoot = getMExecDefUserRootDirectory();
		if (vfRoot != null) {
			return new DefaultVirtualFilePathFormatter(vfRoot, getMExecDefUserRootDisplayName(), true);
		} else {
			return null;
		}
	}
	
	/**
	 * データファイルツリーのシステムルートディレクトリを基準とするパスフォーマッタを生成する。
	 * @return	<code>DefaultVirtualFilePathFormatter</code> オブジェクトを返す。
	 * 			システムルートディレクトリが未定義の場合は <tt>null</tt> を返す。
	 * @since 2.0.0
	 */
	private DefaultVirtualFilePathFormatter createDataFileSystemRootPathFormatter() {
		VirtualFile vfRoot = getDataFileSystemRootDirectory();
		if (vfRoot != null) {
			return new DefaultVirtualFilePathFormatter(vfRoot, getDataFileSystemRootDisplayName(), true);
		} else {
			return null;
		}
	}
	
	/**
	 * データファイルツリーのユーザー定義ルートディレクトリを基準とするパスフォーマッタを生成する。
	 * @return	<code>DefaultVirtualFilePathFormatter</code> オブジェクトを返す。
	 * 			ユーザー定義ルートディレクトリが未定義の場合は <tt>null</tt> を返す。
	 * @since 2.0.0
	 */
	private DefaultVirtualFilePathFormatter createDataFileUserRootPathFormatter() {
		VirtualFile vfRoot = getDataFileUserRootDirectory();
		if (vfRoot != null) {
			return new DefaultVirtualFilePathFormatter(vfRoot, getDataFileUserRootDisplayName(), true);
		} else {
			return null;
		}
	}

	/**
	 * 現在のタブ選択状態により、アクティブなエディタを更新する。
	 */
	private void updateActiveEditor() {
		RunnerMenuBar menuBar;
		int selidx = _tabEditor.getSelectedIndex();
		if (selidx >= 0) {
			_activeEditor = getEditor(selidx);
			menuBar = _activeEditor.getDocumentMenuBar();
		} else {
			_activeEditor = null;
			menuBar = null;
		}
		RunnerFrame.this.setTitle(getFrameTitle(_activeEditor==null ? false : _activeEditor.isReadOnly()));
		setActiveMainMenuBar(menuBar==null ? _defMainMenu : menuBar);
		updateFindReplaceDialogHandler();
		updateAllMenuItems();
		VirtualFile vfViewingFile;
		if (_activeEditor != null) {
			_activeEditor.requestFocusInComponent();
			if (_activeEditor.getDocument().hasTargetFile())
				vfViewingFile = ModuleFileManager.fromJavaFile(_activeEditor.getDocumentFile());
			else
				vfViewingFile = null;
		} else {
			// ビューが一つも存在しない場合は、ツリーにフォーカスを移す
			_viewTree.requestFocusInComponent();
			vfViewingFile = null;
		}
		
		// モジュール引数設定ダイアログが表示されている場合は、アクティブなエディタのファイルを通知
		if (_dlgMExecArgsEdit != null) {
			_dlgMExecArgsEdit.setViewingFileOnEditor(vfViewingFile);
		}
		
		if (AppLogger.isDebugEnabled()) {
			String editorName;
			if (_activeEditor != null) {
				editorName = _activeEditor.getDocumentPath();
				if (Strings.isNullOrEmpty(editorName))
					editorName = _activeEditor.getDocumentTitle();
			} else {
				editorName = "null";
			}
			AppLogger.debug(String.format("RunnerFrame#updateActiveEditor : TabIndex[%d] / activeEditor[%s]", selidx, editorName));
		}
	}

	/**
	 * 変更されたすべてのリソースを保存するかを問い合わせる確認ダイアログを表示し、
	 * 変更されたすべてのリソースを保存する。
	 * @return	変更されたすべてのリソースが保存された場合のみ <tt>true</tt> を返す。
	 */
	private boolean confirmAndSaveAllModifiedDocuments() {
		IEditorView[] editors = getModifiedEditors();
		if (editors.length < 1) {
			// 変更されているドキュメントは存在しない
			return true;
		}
		
		// 確認ダイアログの表示
		StringBuilder sb = new StringBuilder();
		sb.append(RunnerMessages.getInstance().confirmSaveDocumentBeforeOperation);
		for (IEditorView ev : editors) {
			String et = ev.getDocumentTitle();
			sb.append("\n    '");
			sb.append(et);
			sb.append("'");
		}
		sb.append("\n" + CommonMessages.getInstance().confirmSaveChanges);
		int ret = JOptionPane.showConfirmDialog(this, sb.toString(),
				RunnerMessages.getInstance().confirmTitleSaveModifiedDocuments,
				JOptionPane.OK_CANCEL_OPTION);
		if (ret != JOptionPane.OK_OPTION) {
			// user canceled
			return false;
		}
		
		// 変更されたドキュメントをすべて保存
		for (IEditorView ev : editors) {
			// 保存
			if (!saveEditorDocument(ev)) {
				// user canceled
				return false;
			}
		}
		
		// 完了
		return true;
	}

	/**
	 * 現時点で、モジュール実行定義が実行可能かを判定する。
	 * @return	実行可能なら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	private boolean canRunMExecDef() {
//		int numSelection = _viewTree.getSelectionCount();
//		if (numSelection != 1) {
//			// 単一選択でない場合は、実行不可
//			return false;
//		}
//		
//		// モジュール実行定義が選択されていれば、実行可能とする。
//		return (_viewTree.getSelectionMExecDefPrefsFile() != null);
		if (!_viewTree.isMExecDefTreeViewSelected()) {
			// モジュール実行定義ツリーが選択されていない場合は実行不可
			return false;
		}

		MExecDefTreeView viewMExecDef = _viewTree.getMExecDefTreeView();
		int numSelection = viewMExecDef.getSelectionCount();
		if (numSelection != 1) {
			// 単一選択でない場合は、実行不可
			return false;
		}
		
		// モジュール実行定義が選択されていれば、実行可能とする。
		return (viewMExecDef.getSelectionMExecDefPrefsFile() != null);
	}

//	/**
//	 * モジュール実行定義による実行で使用する、標準的な
//	 * テンポラリファイルを生成する。ここで生成されたファイルは
//	 * <code>deleteOnExit()</code> メソッドが呼び出された状態となっている。
//	 * @return	生成されたファイルの抽象パス
//	 * @throws IOException	入出力エラーが発生した場合
//	 */
//	private File createDefaultTemporaryArgumentFile() throws IOException
//	{
//		File tempfile = File.createTempFile("out", ".tmp");
//		tempfile.deleteOnExit();
//		return tempfile;
//	}
//	
//	private File createTemporaryFile(String prefix, String suffix) throws IOException
//	{
//		if (prefix == null || prefix.length() < 3)
//			prefix = "out";
//		if (suffix == null || suffix.length() < 1)
//			suffix = ".tmp";
//		File tempfile = File.createTempFile(prefix, suffix);
//		tempfile.deleteOnExit();
//		return tempfile;
//	}
//
//	/**
//	 * [IN]属性の実行時引数の値を整形する。
//	 * 実行可能な引数値が指定されている場合は <em>item</em> の編集可能フラグが
//	 * <tt>false</tt> に設定される。
//	 * @param item	対象の実行時引数モデル
//	 * @return	整形に成功した場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
//	 * @throws IOException	テンポラリファイルの生成に失敗した場合
//	 */
//	private boolean correctInputFileArgument(MExecArgItemModel item) throws IOException
//	{
//		// ファイルオブジェクトの取得
//		Object value = item.getValue();
//		VirtualFile vfFile = null;
//		if (value instanceof DefaultFile) {
//			vfFile = (DefaultFile)value;
//		}
//		else if (value instanceof File) {
//			vfFile = ModuleFileManager.fromJavaFile((File)value);
//		}
//
//		// 値の整形
//		boolean valid = true;
//		IMExecArgParam param = item.getParameter();
//		if (param instanceof MExecArgTempFile) {
//			// テンポラリファイル指定
//			if (vfFile == null || !vfFile.exists()) {
//				// テンポラリファイルを生成
//				vfFile = ModuleFileManager.fromJavaFile(createDefaultTemporaryArgumentFile());
//			}
//			item.setEditable(false);
//		}
//		else if ((param instanceof MExecArgCsvFile) && item.isOptionEnabled()) {
//			// CSVファイル指定に、現在表示中のファイルを使用
//			IEditorDocument doc = getActiveDocument();
//			if ((doc instanceof CsvFileModel) && doc.getTargetFile()!=null) {
//				//--- 閲覧ファイルはCSVファイル
//				vfFile = ModuleFileManager.fromJavaFile(doc.getTargetFile());
//				item.setEditable(false);
//			} else {
//				//--- 利用可能な閲覧ファイルは存在しない
//				valid = false;
//				vfFile = null;
//				item.setOptionEnabled(false);
//				item.setEditable(true);
//			}
//		}
//		else if (vfFile != null && vfFile.exists()) {
//			// 存在するファイル指定
//			item.setEditable(false);
//		}
//		else {
//			// ファイルが不明
//			valid = false;
//			item.setEditable(true);
//		}
//		
//		// 値が更新されていれば、代入
//		if (vfFile != value) {
//			item.setValue(vfFile);
//		}
//		
//		// 完了
//		return valid;
//	}
//	
//	/**
//	 * [OUT]属性の実行時引数の値を整形する。
//	 * 実行可能な引数値が指定されている場合は <em>item</em> の編集可能フラグが
//	 * <tt>false</tt> に設定される。
//	 * @param item	対象の実行時引数モデル
//	 * @return	整形に成功した場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
//	 * @throws IOException	テンポラリファイルの生成に失敗した場合
//	 */
//	private boolean correctOutputFileArgument(MExecArgItemModel item) throws IOException
//	{
//		// ファイルオブジェクトの取得
//		Object value = item.getValue();
//		VirtualFile vfFile = null;
//		if (value instanceof DefaultFile) {
//			vfFile = (DefaultFile)value;
//		}
//		else if (value instanceof File) {
//			vfFile = ModuleFileManager.fromJavaFile((File)value);
//		}
//
//		// 値の整形
//		boolean valid = true;
//		IMExecArgParam param = item.getParameter();
//		if (param instanceof MExecArgTempFile) {
//			// テンポラリファイル指定
//			if (vfFile == null) {
//				// テンポラリファイルを生成
//				vfFile = ModuleFileManager.fromJavaFile(createDefaultTemporaryArgumentFile());
//			}
//			item.setEditable(false);
//		}
//		else if (item.isOutToTempEnabled()) {
//			// テンポラリファイル指定
//			if (item.isEditable()) {
//				String prefix = item.getTempFilePrefix();
//				String suffix = null;
//				if (param instanceof MExecArgCsvFile) {
//					suffix = RunnerMessages.getInstance().extCSV;
//				}
//				else if (param instanceof MExecArgXmlFile) {
//					suffix = RunnerMessages.getInstance().extXML;
//				}
//				else if (param instanceof MExecArgTextFile) {
//					suffix = RunnerMessages.getInstance().extTXT;
//				}
//				vfFile = ModuleFileManager.fromJavaFile(createTemporaryFile(prefix, suffix));
//			}
//			item.setEditable(false);
//		}
//		else if (vfFile != null) {
//			// 出力先パスは設定済み
//			item.setEditable(false);
//		}
//		else {
//			// 出力先のパスが指定されていない
//			valid = false;
//			item.setEditable(true);
//		}
//		
//		// 値が更新されていれば、代入
//		if (vfFile != value) {
//			item.setValue(vfFile);
//		}
//		
//		// 完了
//		return valid;
//	}
//	
//	/**
//	 * [STR]属性の実行時引数の値を整形する。
//	 * 実行可能な引数値が指定されている場合は <em>item</em> の編集可能フラグが
//	 * <tt>false</tt> に設定される。
//	 * @param item	対象の実行時引数モデル
//	 * @return	整形に成功した場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
//	 */
//	private boolean correctStringArguments(MExecArgItemModel item)
//	{
//		Object value = item.getValue();
//		String strValue = (value==null ? null : value.toString());
//		if (Strings.isNullOrEmpty(strValue)) {
//			//--- 文字列が指定されていない
//			item.setEditable(true);
//			return false;
//		} else {
//			//--- 文字列は正当
//			item.setEditable(false);
//			if (strValue != value) {
//				// 文字列表現が値ではない場合は、文字列に変更
//				item.setValue(strValue);
//			}
//			return true;
//		}
//	}
//	
//	/**
//	 * 指定されたモデルに含まれるすべての実行時引数の値を整形する。
//	 * 実行可能な引数値が指定されている場合、その <em>item</em> の編集可能フラグが
//	 * <tt>false</tt> に設定される。
//	 * @param datamodel	対象の実行時引数モデル
//	 * @return	整形に成功した場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
//	 * @throws IOException	テンポラリファイルの生成に失敗した場合
//	 */
//	private boolean correctArguments(MExecArgsModel datamodel) throws IOException
//	{
//		boolean valid = true;
//		int numArgs = datamodel.getNumItems();
//		for (int i = 0; i < numArgs; i++) {
//			MExecArgItemModel item = datamodel.getItem(i);
//			if (ModuleArgType.IN == item.getType()) {
//				// [IN]
//				if (!correctInputFileArgument(item)) {
//					valid = false;
//				}
//			}
//			else if (ModuleArgType.OUT == item.getType()) {
//				// [OUT]
//				if (!correctOutputFileArgument(item)) {
//					valid = false;
//				}
//			}
//			else {
//				// [STR]
//				if (!correctStringArguments(item)) {
//					valid = false;
//				}
//			}
//		}
//		
//		return valid;
//	}
//
//	/**
//	 * 指定された引数定義から、実行するコマンドを生成する。
//	 * @param datamodel	引数設定
//	 * @return	生成されたコマンド
//	 * @throws NullPointerException	引数が <tt>null</tt> の場合
//	 * @throws IllegalArgumentException	引数の設定が正しくない場合
//	 * @throws IOException	引数設定でテンポラリファイルが生成できなかった場合
//	 */
//	private CommandExecutor createCommandExecutor(MExecArgsModel datamodel) throws IOException
//	{
//		if (datamodel == null)
//			throw new NullPointerException("The specified data model is null.");
//		
//		// check exist module
//		VirtualFile vfModule = datamodel.getSettings().getModuleFile();
//		if (vfModule == null)
//			throw new IllegalArgumentException("Target module is nothing.");
//		if (!vfModule.exists())
//			throw new IllegalArgumentException("Target module is not found : \"" + vfModule.toString() + "\"");
//		if (!vfModule.isFile())
//			throw new IllegalArgumentException("Target module is not file : \"" + vfModule.toString() + "\"");
//		File fModule = ModuleFileManager.toJavaFile(vfModule);
//		if (fModule == null)
//			throw new IllegalArgumentException("Target module is not local file : \"" + vfModule.toURI() + "\"");
//		
//		// verify arguments
//		if (!correctArguments(datamodel)) {
//			throw new IllegalArgumentException("Target arguments is illegal.");
//		}
//		
//		// create CommandExecutor by module file type
//		if (ModuleFileType.AADL_JAR == datamodel.getSettings().getModuleFileType()) {
//			// AADL Execution module (jar)
//			return createAADLModuleExecutor(datamodel, fModule);
//		}
//		else if (ModuleFileType.AADL_MACRO == datamodel.getSettings().getModuleFileType()) {
//			// AADL Macro file (amf)
//			return createAADLMacroExecutor(datamodel, fModule);
//		}
//		else {
//			// unknown file type
//			throw new IllegalArgumentException("Target module is illegal file type : " + String.valueOf(datamodel.getSettings().getModuleFileType()));
//		}
//	}
//
//	/**
//	 * 指定された引数定義から、AADL実行モジュールを実行するコマンドを生成する。
//	 * @param datamodel	引数設定
//	 * @param 実行モジュールの位置を示す <code>java.io.File</code> オブジェクト
//	 * @return	生成されたコマンド
//	 * @throws NullPointerException	引数が <tt>null</tt> の場合
//	 * @throws IllegalArgumentException	引数の設定が正しくない場合
//	 * @throws IOException	引数設定でテンポラリファイルが生成できなかった場合
//	 * @since 1.20
//	 */
//	private CommandExecutor createAADLModuleExecutor(MExecArgsModel datamodel, File fModule) throws IOException
//	{
//		assert datamodel != null;
//		assert fModule != null;
//		assert datamodel.getSettings().getModuleFileType()==ModuleFileType.AADL_JAR;
//		
//		// create command
//		List<String> cmdList = new ArrayList<String>();
//		
//		// java command
//		CommandExecutor.appendJavaCommandPath(cmdList, AppSettings.getInstance().getCurrentJavaCommandPath());
//		
//		// Java VM arguments
//		String params;
//		//--- AADL csv encoding property
//		//--- AADL csv encoding property
//		params = AppSettings.getInstance().getAadlCsvEncodingName();
//		if (!Strings.isNullOrEmpty(params)) {
//			cmdList.add("-Daadl.csv.encoding=" + params);
//		}
//		//--- AADL txt encoding property
//		params = AppSettings.getInstance().getAadlTxtEncodingName();
//		if (!Strings.isNullOrEmpty(params)) {
//			cmdList.add("-Daadl.txt.encoding=" + params);
//		}
//		//--- java VM arguments
//		String maxMemorySize = ModuleRunner.getMaxMemorySize();
//		String strVmArgs = AppSettings.getInstance().appendJavaVMoptions(datamodel.getSettings().getJavaVMArgs());
//		if (!Strings.isNullOrEmpty(maxMemorySize)) {
//			CommandExecutor.appendJavaMaxHeapSize(cmdList, maxMemorySize);
//			if (!Strings.isNullOrEmpty(strVmArgs)) {
//				//--- ignore -Xmx???? and -Xms???? option
//				String[] vmArgs = Strings.splitCommandLineWithDoubleQuoteEscape(strVmArgs);
//				for (String vma : vmArgs) {
//					if (!vma.startsWith("-Xmx") && !vma.startsWith("-Xms")) {
//						cmdList.add(vma);
//					}
//				}
//			}
//		} else {
//			CommandExecutor.appendJavaVMArgs(cmdList, strVmArgs);
//		}
//		
//		// Check user class-paths
//		//File[] userClassPaths = datamodel.getSettings().getClassPathFiles();
//		//for (File file : userClassPaths) {
//		//	if (file == null)
//		//		throw new IllegalArgumentException("User class path file is null.");
//		//	if (!file.exists())
//		//		throw new IllegalArgumentException("User class path file is not found : \"" + file.getPath() + "\"");
//		//}
//		
//		// Check Exec libraries
//		String[] execLibraries = AppSettings.getInstance().getExecLibraries();
//		for (String path : execLibraries) {
//			if (Strings.isNullOrEmpty(path))
//				throw new IllegalStateException("Class path for execution in Editor environment is illegal value.");
//			if (!(new File(path)).exists())
//				throw new IllegalStateException("Class path for execution in Editor environment is not found : \"" + path + "\"");
//		}
//		
//		//***********************************************
//		// 現在実行可能なモジュールは、AADLモジュール(JAR)のみ
//		//***********************************************
//		
//		// ClassPath
//		ClassPathSet pathSet = new ClassPathSet();
//		//pathSet.appendPaths(userClassPaths);
//		pathSet.addPath(fModule);
//		pathSet.appendPaths(execLibraries);
//		CommandExecutor.appendClassPath(cmdList, pathSet);
//		
//		// Main class
//		CommandExecutor.appendMainClassName(cmdList, datamodel.getSettings().getModuleMainClass());
//		
//		// Program arguments
//		int numArgs = datamodel.getNumItems();
//		for (int i = 0; i < numArgs; i++) {
//			Object value = datamodel.getItem(i).getValue();
//			if (value instanceof DefaultFile) {
//				cmdList.add(((DefaultFile)value).getAbsolutePath());
//			}
//			else if (value instanceof File) {
//				cmdList.add(((File)value).getAbsolutePath());
//			}
//			else if (value != null) {
//				cmdList.add(value.toString());
//			}
//			else {
//				cmdList.add("");
//			}
//		}
//		
//		// create Executor
//		CommandExecutor executor = new CommandExecutor(cmdList);
//		
//		// set working directory
//		executor.setWorkDirectory(fModule.getParentFile());
//		
//		// completed
//		return executor;
//	}
//
//	/**
//	 * 指定された引数定義から、AADLマクロを実行するコマンドを生成する。
//	 * @param datamodel	引数設定
//	 * @param 実行モジュールの位置を示す <code>java.io.File</code> オブジェクト
//	 * @return	生成されたコマンド
//	 * @throws NullPointerException	引数が <tt>null</tt> の場合
//	 * @throws IllegalArgumentException	引数の設定が正しくない場合
//	 * @throws IOException	引数設定でテンポラリファイルが生成できなかった場合
//	 * @since 1.20
//	 */
//	private CommandExecutor createAADLMacroExecutor(MExecArgsModel datamodel, File fModule) throws IOException
//	{
//		assert datamodel != null;
//		assert fModule != null;
//		assert datamodel.getSettings().getModuleFileType() == ModuleFileType.AADL_MACRO;
//		
//		// create command
//		List<String> cmdList = new ArrayList<String>();
//		
//		// java command
//		CommandExecutor.appendJavaCommandPath(cmdList, AppSettings.getInstance().getCurrentJavaCommandPath());
//		
//		// Java VM arguments
//		String params;
//		//--- java VM arguments
//		String maxMemorySize = ModuleRunner.getMaxMemorySize();
//		String strVmArgs = AppSettings.getInstance().appendJavaVMoptions(datamodel.getSettings().getJavaVMArgs());
//		if (!Strings.isNullOrEmpty(maxMemorySize)) {
//			CommandExecutor.appendJavaMaxHeapSize(cmdList, maxMemorySize);
//			if (!Strings.isNullOrEmpty(strVmArgs)) {
//				//--- ignore -Xmx???? and -Xms???? option
//				String[] vmArgs = Strings.splitCommandLineWithDoubleQuoteEscape(strVmArgs);
//				for (String vma : vmArgs) {
//					if (!vma.startsWith("-Xmx") && !vma.startsWith("-Xms")) {
//						cmdList.add(vma);
//					}
//				}
//			}
//		} else {
//			CommandExecutor.appendJavaVMArgs(cmdList, strVmArgs);
//		}
//		
//		// Macro engine class-path
//		ClassPathSet pathSet = new ClassPathSet();
//		File macroClass = Classes.getClassSource(AADLMacroEngine.class);
//		pathSet.addPath(macroClass.getAbsoluteFile());
//		CommandExecutor.appendClassPath(cmdList, pathSet);
//		
//		// Macro main class
//		CommandExecutor.appendMainClassName(cmdList, AADLMacroEngine.class.getName());
//		
//		// Macro options
//		//--- debug
//		if (ModuleRunner.isDebugEnabled()) {
//			cmdList.add("-debug");
//			cmdList.add("-verbose");
//		}
//		//--- encoding : AADL macro encoding property
//		params = AppSettings.getInstance().getAadlMacroEncodingName();
//		if (!Strings.isNullOrEmpty(params)) {
//			cmdList.add("-macroencoding");
//			cmdList.add(params);
//		}
//		//--- encoding : AADL csv encoding property
//		params = AppSettings.getInstance().getAadlCsvEncodingName();
//		if (!Strings.isNullOrEmpty(params)) {
//			cmdList.add("-csvencoding");
//			cmdList.add(params);
//		}
//		//--- encoding : AADL txt encoding property
//		params = AppSettings.getInstance().getAadlTxtEncodingName();
//		if (!Strings.isNullOrEmpty(params)) {
//			cmdList.add("-txtencoding");
//			cmdList.add(params);
//		}
//		//--- heapmax
//		if (!Strings.isNullOrEmpty(maxMemorySize)) {
//			cmdList.add("-heapmax");
//			cmdList.add(maxMemorySize);
//		}
//		//--- Class path setting for AADL module
//		String[] execLibraries = AppSettings.getInstance().getExecLibraries();
//		if (execLibraries != null && execLibraries.length > 0) {
//			StringBuilder sb = new StringBuilder(execLibraries[0]);
//			for (int i = 1; i < execLibraries.length; i++) {
//				sb.append(AADLMacroEngine.MACRO_CLASSPATH_SEPARATOR);
//				sb.append(execLibraries[i]);
//			}
//			cmdList.add("-libpath");
//			cmdList.add(sb.toString());
//		}
//		//--- VM options
//		if (!Strings.isNullOrEmpty(strVmArgs)) {
//			cmdList.add("-javavm");
//			cmdList.add(strVmArgs);
//		}
//		
//		// Target macro file
//		cmdList.add(fModule.getAbsolutePath());
//		
//		// Module arguments
//		int numArgs = datamodel.getNumItems();
//		for (int i = 0; i < numArgs; i++) {
//			Object value = datamodel.getItem(i).getValue();
//			if (value instanceof DefaultFile) {
//				cmdList.add(((DefaultFile)value).getAbsolutePath());
//			}
//			else if (value instanceof File) {
//				cmdList.add(((File)value).getAbsolutePath());
//			}
//			else if (value != null) {
//				cmdList.add(value.toString());
//			}
//			else {
//				cmdList.add("");
//			}
//		}
//		
//		// create Executor
//		CommandExecutor executor = new CommandExecutor(cmdList);
//		
//		// set working directory
//		executor.setWorkDirectory(fModule.getParentFile());
//		
//		// completed
//		return executor;
//	}
	
	//------------------------------------------------------------
	// Menu handlers
	//------------------------------------------------------------
	
	/**
	 * メニュー項目の選択時に呼び出されるハンドラ・メソッド。
	 * このメソッドはメニューアクションから呼び出される。
	 * このメソッドが呼び出されると、アクティブドキュメント、アクティブドキュメントの
	 * コンポーネント、このフレームのデフォルト処理の順に、ハンドラが呼び出される。
	 * 
	 * @param command	このイベント要因のコマンド文字列
	 * @param source	このイベント要因のソースオブジェクト。
	 * 					ソースオブジェクトが未定義の場合は <tt>null</tt>。
	 * @param action	このイベント要因のソースオブジェクトに割り当てられたアクション。
	 * 					アクションが未定義の場合は <tt>null</tt>。
	 * @return	このハンドラ内で処理が完結した場合は <tt>true</tt> を返す。イベントシーケンスの
	 * 			別のハンドラに処理を委譲する場合は <tt>false</tt> を返す。
	 */
	public boolean onProcessMenuSelection(String command, Object source, Action action) {
		if (command.startsWith(RunnerMenuResources.ID_FILE_MENU)) {
			if (command.startsWith(RunnerMenuResources.ID_FILE_NEW_PREFIX)) {
				if (RunnerMenuResources.ID_FILE_NEW_MODULEEXECDEF.equals(command)) {
					onSelectedMenuFileNewModuleExecDef(action);
					return true;
				}
				else if (RunnerMenuResources.ID_FILE_NEW_GENERICFILTER.equals(command)) {
					onSelectedMenuFileNewGenericFilter(action);
					return true;
				}
				else if (RunnerMenuResources.ID_FILE_NEW_MACROFILTER.equals(command)) {
					onSelectedMenuFileNewFilterMacro(action);
					return true;
				}
				else if (RunnerMenuResources.ID_FILE_NEW_FOLDER.equals(command)) {
					onSelectedMenuFileNewFolder(action);
					return true;
				}
			}
			else if (RunnerMenuResources.ID_FILE_OPEN.equals(command)) {
				onSelectedMenuFileOpen(action);
				return true;
			}
			else if (RunnerMenuResources.ID_FILE_OPEN_CONFIG_CSV.equals(command)) {
				onSelectedMenuFileOpenConfigCsv(action);
				return true;
			}
			else if (command.startsWith(RunnerMenuResources.ID_FILE_REOPEN_PREFIX)) {
				onSelectedMenuFileReopen(command, action);
				return true;
			}
			else if (RunnerMenuResources.ID_FILE_CLOSE.equals(command)) {
				onSelectedMenuFileClose(action);
				return true;
			}
			else if (RunnerMenuResources.ID_FILE_ALL_CLOSE.equals(command)) {
				onSelectedMenuFileAllClose(action);
				return true;
			}
//			else if (RunnerMenuResources.ID_FILE_SAVE.equals(command)) {
//				onSelectedMenuFileSave(action);
//				return true;
//			}
			else if (RunnerMenuResources.ID_FILE_SAVEAS.equals(command)) {
				onSelectedMenuFileSaveAs(action);
				return true;
			}
			/*
			else if (RunnerMenuResources.ID_FILE_COPYTO.equals(command)) {
				onSelectedMenuFileCopyTo(action);
				return true;
			}
			*/
			else if (RunnerMenuResources.ID_FILE_MOVETO.equals(command)) {
				onSelectedMenuFileMoveTo(action);
				return true;
			}
			else if (RunnerMenuResources.ID_FILE_RENAME.equals(command)) {
				onSelectedMenuFileRename(action);
				return true;
			}
			else if (RunnerMenuResources.ID_FILE_REFRESH.equals(command)) {
				onSelectedMenuFileRefresh(action);
				return true;
			}
			else if (RunnerMenuResources.ID_FILE_SELECT_USERROOT_MEXECDEF.equals(command)) {
				onSelectedMenuFileChangeMExecDefUserFolder(action);
				return true;
			}
			else if (RunnerMenuResources.ID_FILE_SELECT_USERROOT_DATAFILE.equals(command)) {
				onSelectedMenuFileChangeDataFileUserFolder(action);
				return true;
			}
			else if (RunnerMenuResources.ID_FILE_PREFERENCE.equals(command)) {
				onSelectedMenuFilePreference(action);
				return true;
			}
			else if (RunnerMenuResources.ID_FILE_IMPORT.equals(command)) {
				onSelectedMenuFileImport(action);
				return true;
			}
			else if (RunnerMenuResources.ID_FILE_EXPORT.equals(command)) {
				onSelectedMenuFileExport(action);
				return true;
			}
			else if (RunnerMenuResources.ID_FILE_QUIT.equals(command)) {
				onSelectedMenuFileQuit(action);
				return true;
			}
		}
		else if (CsvFileMenuBar.ID_FILE_SAVEAS_DEFAULT.equals(command)) {
			onSelectedMenuFileSaveAsDefault(action);
			return true;
		}
		else if (CsvFileMenuBar.ID_FILE_SAVEAS_CONFIG.equals(command)) {
			onSelectedMenuFileSaveAsConfig(action);
			return true;
		}
		else if (CsvFileMenuBar.ID_FILE_EXPORT_DTALGE.equals(command)) {
			onSelectedMenuFileExportDtalge(action);
			return true;
		}
		else if (command.startsWith(RunnerMenuResources.ID_FIND_MENU)) {
			if (RunnerMenuResources.ID_FIND_FIND.equals(command)) {
				onSelectedMenuFindFind(action);
				return true;
			}
			else if (RunnerMenuResources.ID_FIND_PREV.equals(command)) {
				onSelectedMenuFindPrev(action);
				return true;
			}
			else if (RunnerMenuResources.ID_FIND_NEXT.equals(command)) {
				onSelectedMenuFindNext(action);
				return true;
			}
		}
//		else if (command.startsWith(RunnerMenuResources.ID_RUN_MENU)) {
//			if (RunnerMenuResources.ID_RUN_RUN.equals(command)) {
//				onSelectedMenuRunRun(action);
//				return true;
//			}
//		}
		else if (RunnerMenuResources.ID_FILTER_RUN.equals(command)) {
			// モジュール実行定義の実行
			onSelectedMenuFilterRun(action);
			return true;
		}
		else if (RunnerMenuResources.ID_FILTER_EDIT.equals(command)) {
			// モジュール実行定義の編集
			onSelectedMenuFilterEdit(action);
			return true;
		}
		else if (RunnerMenuResources.ID_FILTER_NEW_BY_HISTORY.equals(command)) {
			onSelectedMenuFilterNewByHistorySelected(action);
			return true;
		}
		else if (RunnerMenuResources.ID_FILTER_RECORD_HISTORY.equals(command)) {
			if (action instanceof AbCheckMenuItemAction) {
				boolean selected = ((AbCheckMenuItemAction)action).isSelected();
				_viewTool.getHistoryToolView().setHistoryRecordingEnabled(!selected);
			}
		}
		else if (RunnerMenuResources.ID_FILTER_HISTORY_RUN_SELECTED.equals(command)) {
			onSelectedMenuFilterRunHistorySelected(action);
			return true;
		}
		else if (RunnerMenuResources.ID_FILTER_HISTORY_RUN_FROM.equals(command)) {
			onSelectedMenuFilterRunHistoryFrom(action);
			return true;
		}
		else if (RunnerMenuResources.ID_FILTER_HISTORY_RUN_BEFORE.equals(command)) {
			onSelectedMenuFilterRunHistoryBefore(action);
			return true;
		}
		else if (RunnerMenuResources.ID_FILTER_HISTORY_RUN_LATEST.equals(command)) {
			onSelectedMenuFilterRunHistoryLatest(action);
			return true;
		}
		else if (RunnerMenuResources.ID_FILTER_HISTORY_RUNAS_SELECTED.equals(command)) {
			onSelectedMenuFilterRunAsHistorySelected(action);
			return true;
		}
		else if (RunnerMenuResources.ID_FILTER_HISTORY_RUNAS_FROM.equals(command)) {
			onSelectedMenuFilterRunAsHistoryFrom(action);
			return true;
		}
		else if (RunnerMenuResources.ID_FILTER_HISTORY_RUNAS_BEFORE.equals(command)) {
			onSelectedMenuFilterRunAsHistoryBefore(action);
			return true;
		}
		else if (RunnerMenuResources.ID_FILTER_HISTORY_RUNAS_LATEST.equals(command)) {
			onSelectedMenuFilterRunAsHistoryLatest(action);
			return true;
		}
//		else if (RunnerMenuResources.ID_HIDE_EDIT_EXECDEF.equals(command)) {
//			// モジュール実行定義の編集
//			onSelectedMenuEditExecDef(action);
//			return true;
//		}
		else if (RunnerMenuResources.ID_HIDE_SHOW_MEDARGSDLG.equals(command)) {
			// モジュール実行引数設定ダイアログ表示用ツールバーボタンの更新
			if (_dlgMExecArgsEdit != null && _dlgMExecArgsEdit.isVisible()) {
				// 最全面に表示
				_dlgMExecArgsEdit.toFront();
			} else {
				JButton btn = getActiveMainMenuBar().getShowExecArgsDlgButton();
				btn.setVisible(false);
				btn.setEnabled(false);
			}
			return true;
		}
		else if (RunnerMenuResources.ID_HIDE_SHOW_CHARTWINDOW.equals(command)) {
			// チャートウィンドウ表示用ツールバーボタンの更新 (@since 2.1.0)
			if (_chartWindow != null && _chartWindow.isVisibleAnyWindow()) {
				// 最前面に表示
				_chartWindow.activeWindowToFront();
			} else {
				JButton btn = getActiveMainMenuBar().getShowChartWindowButton();
				btn.setVisible(false);
				btn.setEnabled(false);
			}
			return true;
		}
		else if (RunnerMenuResources.ID_TOOL_CHART_SCATTER.equals(command)) {
			onSelectedMenuToolChartScatter(action);
			return true;
		}
		else if (RunnerMenuResources.ID_TOOL_CHART_LINE.equals(command)) {
			onSelectedMenuToolChartLine(action);
			return true;
		}
		else if (RunnerMenuResources.ID_TOOL_EXCEL2CSV.equals(command)) {
			onSelectedMenuToolExcel2Csv(action);
			return true;
		}
		else if (RunnerMenuResources.ID_HELP_ABOUT.equals(command)) {
			onSelectedMenuHelpAbout(action);
			return true;
		}
		
		return false;
	}
	
	/**
	 * メニュー項目の更新要求時に呼び出されるハンドラ・メソッド。
	 * このメソッドはメニュー項目更新メソッドから呼び出される。
	 * このメソッドが呼び出されると、アクティブドキュメント、アクティブドキュメントの
	 * コンポーネント、このフレームのデフォルト処理の順に、ハンドラが呼び出される。
	 * 
	 * @param command	このイベント要因のコマンド文字列
	 * @param source	このイベント要因のソースオブジェクト。
	 * 					ソースオブジェクトが未定義の場合は <tt>null</tt>。
	 * @param action	このイベント要因のソースオブジェクトに割り当てられたアクション。
	 * 					アクションが未定義の場合は <tt>null</tt>。
	 * @return	このハンドラ内で処理が完結した場合は <tt>true</tt> を返す。イベントシーケンスの
	 * 			別のハンドラに処理を委譲する場合は <tt>false</tt> を返す。
	 */
	public boolean onProcessMenuUpdate(String command, Object source, Action action) {
		if (command.startsWith(RunnerMenuResources.ID_FILE_MENU)) {
			if (command.startsWith(RunnerMenuResources.ID_FILE_NEW)) {
				if (RunnerMenuResources.ID_FILE_NEW_FOLDER.equals(command)) {
					action.setEnabled(_viewTree.canCreateDirectory());
				}
				else if (RunnerMenuResources.ID_FILE_NEW_MODULEEXECDEF.equals(command)) {
					action.setEnabled(_viewTree.canCreateMExecDef());
					return true;
				}
				else if (RunnerMenuResources.ID_FILE_NEW_GENERICFILTER.equals(command)) {
					action.setEnabled(_viewTree.canCreateGenericFilter());
					return true;
				}
				else if (RunnerMenuResources.ID_FILE_NEW_MACROFILTER.equals(command)) {
					action.setEnabled(_viewTree.canCreateFilterMacro());
					return true;
				}
				else {
					action.setEnabled(false);
				}
			}
			else if (RunnerMenuResources.ID_FILE_OPEN.equals(command)) {
				action.setEnabled(true);
			}
			else if (RunnerMenuResources.ID_FILE_OPEN_CONFIG_CSV.equals(command)) {
				action.setEnabled(true);
			}
			else if (command.startsWith(RunnerMenuResources.ID_FILE_REOPEN)) {
				IEditorView view = getActiveEditor();
				boolean toEnable;
				if (view != null && view.canReopen()) {
					toEnable = true;
				} else {
					toEnable = false;
				}
				RunnerMenuBar embar = getActiveMainMenuBar();
				if (embar != null) {
					JMenuItem item = JMenus.getMenuItemByCommand(embar, RunnerMenuResources.ID_FILE_REOPEN);
					if (item != null) {
						if (item.isEnabled() != toEnable) {
							item.setEnabled(toEnable);
						}
						item = JMenus.getMenuItemByCommand(embar.getEditorTabContextMenu(),
								RunnerMenuResources.ID_FILE_REOPEN);
						if (item != null) {
							if (item.isEnabled() != toEnable) {
								item.setEnabled(toEnable);
							}
						}
					} else {
						action.setEnabled(toEnable);
					}
				} else {
					action.setEnabled(toEnable);
				}
			}
			else if (RunnerMenuResources.ID_FILE_CLOSE.equals(command)) {
				action.setEnabled(getActiveEditor()!=null);
			}
			else if (RunnerMenuResources.ID_FILE_ALL_CLOSE.equals(command)) {
				action.setEnabled(!isEditorEmpty());
			}
//			else if (RunnerMenuResources.ID_FILE_SAVE.equals(command)) {
//				action.setEnabled(false);
//			}
			else if (RunnerMenuResources.ID_FILE_SAVEAS.equals(command)) {
				action.setEnabled(false);
			}
			/*
			else if (RunnerMenuResources.ID_FILE_COPYTO.equals(command)) {
				action.setEnabled(viewTree.canCopy());
			}
			*/
			else if (RunnerMenuResources.ID_FILE_MOVETO.equals(command)) {
				action.setEnabled(false);
			}
			else if (RunnerMenuResources.ID_FILE_RENAME.equals(command)) {
				action.setEnabled(false);
			}
			else if (RunnerMenuResources.ID_FILE_REFRESH.equals(command)) {
				action.setEnabled(true);
			}
			else if (RunnerMenuResources.ID_FILE_SELECT_USERROOT_MEXECDEF.equals(command)) {
				action.setEnabled(_viewTree.isEnabledMExecDefTreeView());
			}
			else if (RunnerMenuResources.ID_FILE_SELECT_USERROOT_DATAFILE.equals(command)) {
				action.setEnabled(_viewTree.isEnabledDataFileTreeView());
			}
			else if (RunnerMenuResources.ID_FILE_PREFERENCE.equals(command)) {
				action.setEnabled(!isVisibleMExecArgsEditDialog());
			}
			else if (RunnerMenuResources.ID_FILE_IMPORT.equals(command)) {
				action.setEnabled(_viewTree.isEnabledMExecDefTreeView());
			}
			else if (RunnerMenuResources.ID_FILE_EXPORT.equals(command)) {
				action.setEnabled(_viewTree.isEnabledMExecDefTreeView());
			}
			else if (RunnerMenuResources.ID_FILE_QUIT.equals(command)) {
				action.setEnabled(true);
			}
			else {
				action.setEnabled(false);
			}
			return true;
		}
		else if (CsvFileMenuBar.ID_FILE_SAVEAS_DEFAULT.equals(command)) {
			action.setEnabled(getActiveEditor() instanceof CsvFileView);
			return true;
		}
		else if (CsvFileMenuBar.ID_FILE_SAVEAS_CONFIG.equals(command)) {
			action.setEnabled(getActiveEditor() instanceof CsvFileView);
			return true;
		}
		else if (CsvFileMenuBar.ID_FILE_EXPORT_DTALGE.equals(command)) {
			action.setEnabled(getActiveEditor() instanceof CsvFileView);
			return true;
		}
		else if (command.startsWith(RunnerMenuResources.ID_EDIT_MENU)) {
			action.setEnabled(false);
			return true;
		}
		else if (command.startsWith(RunnerMenuResources.ID_FIND_MENU)) {
			if (RunnerMenuResources.ID_FIND_FIND.equals(command)) {
				IEditorView editor = getActiveEditor();
				action.setEnabled(_activeViewManager.isComponentActivated(_tabEditor) && editor != null && editor.canFindReplace());
				return true;
			}
			else if (RunnerMenuResources.ID_FIND_PREV.equals(command)) {
				IEditorView editor = getActiveEditor();
				action.setEnabled(_activeViewManager.isComponentActivated(_tabEditor) && editor != null && editor.canFindReplace());
				return true;
			}
			else if (RunnerMenuResources.ID_FIND_NEXT.equals(command)) {
				IEditorView editor = getActiveEditor();
				action.setEnabled(_activeViewManager.isComponentActivated(_tabEditor) && editor != null && editor.canFindReplace());
				return true;
			}
		}
//		else if (command.startsWith(RunnerMenuResources.ID_RUN_MENU)) {
//			if (RunnerMenuResources.ID_RUN_RUN.equals(command)) {
//				action.setEnabled(canRunMExecDef());
//			}
//			else {
//				action.setEnabled(false);
//			}
//			return true;
//		}
		else if (command.startsWith(RunnerMenuResources.ID_FILTER_MENU)) {
			if (RunnerMenuResources.ID_FILTER_RECORD_HISTORY.equals(command)) {
				action.setEnabled(true);
				if (action instanceof AbCheckMenuItemAction) {
					((AbCheckMenuItemAction)action).setSelected(_viewTool.getHistoryToolView().isHistoryRecordingEnabled());
				}
			}
			else if (RunnerMenuResources.ID_FILTER_EDIT.equals(command)) {
				action.setEnabled(_viewTree.canShowMExecDef());
			}
			else if (RunnerMenuResources.ID_FILTER_RUN.equals(command)) {
				action.setEnabled(canRunMExecDef());
			}
			else if (RunnerMenuResources.ID_FILTER_NEW_BY_HISTORY.equals(command)) {
				_viewTool.getHistoryToolView().onHistoryMenuUpdate(command, action);
			}
			else if (command.startsWith(RunnerMenuResources.ID_FILTER_HISTORY_PREFIX)) {
				_viewTool.getHistoryToolView().onHistoryMenuUpdate(command, action);
			}
			else {
				action.setEnabled(false);
			}
			return true;
		}
		else if (RunnerMenuResources.ID_TOOL_CHART_SCATTER.equals(command)) {
			action.setEnabled(getActiveEditor() instanceof CsvFileView);
			return true;
		}
		else if (RunnerMenuResources.ID_TOOL_CHART_LINE.equals(command)) {
			action.setEnabled(getActiveEditor() instanceof CsvFileView);
			return true;
		}
		else if (RunnerMenuResources.ID_TOOL_EXCEL2CSV.equals(command)) {
			action.setEnabled(true);
		}
		else if (command.startsWith(RunnerMenuResources.ID_HELP_MENU)) {
			action.setEnabled(true);
			return true;
		}
		else if (RunnerMenuResources.ID_HIDE_SHOW_MEDARGSDLG.equals(command)) {
			// モジュール実行引数設定ダイアログ表示用ツールバーボタンの更新
			JButton btn = getActiveMainMenuBar().getShowExecArgsDlgButton();
			if (_dlgMExecArgsEdit != null && _dlgMExecArgsEdit.isVisible()) {
				btn.setEnabled(true);
				btn.setVisible(true);
			} else {
				btn.setVisible(false);
				btn.setEnabled(false);
			}
		}
		else if (RunnerMenuResources.ID_HIDE_SHOW_CHARTWINDOW.equals(command)) {
			// チャートウィンドウ表示用ツールバーボタンの更新 (@since 2.1.0)
			JButton btn = getActiveMainMenuBar().getShowChartWindowButton();
			if (_chartWindow != null && _chartWindow.isVisibleAnyWindow()) {
				btn.setEnabled(true);
				btn.setVisible(true);
			} else {
				btn.setVisible(false);
				btn.setEnabled(false);
			}
		}
		else if (action != null) {
			action.setEnabled(false);
		}
		
		return false;
	}
	
	// menu : [File]-[New]-[Module Execution Definition]
	protected void onSelectedMenuFileNewModuleExecDef(Action action) {
		AppLogger.debug("menu [File]-[New]-[Module Execution Definition] selected.");
		_viewTree.doCreateMExecDef();
		setFocusToActiveView();
	}
	
	// menu : [File]-[New]-[Generic Filter]
	protected void onSelectedMenuFileNewGenericFilter(Action action) {
		AppLogger.debug("menu [File]-[New]-[Generic Filter] selected.");
		_viewTree.doCreateGenericFilter();
		setFocusToActiveView();
	}
	
	// menu : [File]-[New]-[Filter Macro]
	protected void onSelectedMenuFileNewFilterMacro(Action action) {
		AppLogger.debug("menu [File]-[New]-[Fitler Macro] selected.");
		_viewTree.doCreateFilterMacro();
		setFocusToActiveView();
	}
	
	// menu : [File]-[New]-[Folder]
	protected void onSelectedMenuFileNewFolder(Action action) {
		AppLogger.debug("menu [File]-[New]-[Folder] selected.");
		_viewTree.doCreateDirectory();
		setFocusToActiveView();
	}

	// menu : [File]-[Open]
	protected void onSelectedMenuFileOpen(Action action) {
		AppLogger.debug("menu [File]-[Open] selected.");
		// choose file
		//File initFile = AppSettings.getInstance().getLastFile(AppSettings.DOCUMENT);
		File initFile = FileChooserManager.getLastChooseDocumentFile();
		File targetFile = FileChooserManager.chooseAllFile(this, initFile, null);
		if (targetFile == null) {
			// not selected
			setFocusToActiveEditor();
			return;
		}
		//--- store last file
		//AppSettings.getInstance().setLastFile(AppSettings.DOCUMENT, targetFile);
		FileChooserManager.setLastChooseDocumentFile(targetFile);
		
		// exist opened?
		for (int i = 0; i < getEditorCount(); i++) {
			IEditorView editor = getEditor(i);
			File editorFile = editor.getDocumentFile();
			if (editorFile != null && editorFile.equals(targetFile)) {
				// すでに存在している場合は、そこにフォーカスを設定して終了
				_tabEditor.setSelectedIndex(i);
				return;
			}
		}
		
		// open file
		IComponentManager manager = PluginManager.findSupportedPlugin(targetFile);
		if (manager == null) {
			// このファイルはサポートされていない
			String errmsg = String.format(CommonMessages.getInstance().msgUnsupportedDocument, targetFile.getAbsolutePath());
			AppLogger.error(errmsg);
			ModuleRunner.showErrorMessage(this, errmsg);
			setFocusToActiveEditor();
			return;
		}
		IEditorView newEditor = null;
		try {
			newEditor = manager.openDocument(this, targetFile);
		}
		catch (FileNotFoundException ex) {
			String errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_NOTFOUND, ex, targetFile.getAbsolutePath());
			AppLogger.error(errmsg, ex);
			ModuleRunner.showErrorMessage(this, errmsg);
		}
		catch (UnsupportedEncodingException ex) {
			String errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_UNSUPPORTED_ENCODING, ex);
			AppLogger.error(errmsg, ex);
			ModuleRunner.showErrorMessage(this, errmsg);
		}
		catch (IOException ex) {
			String errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_READ, ex, targetFile.getAbsolutePath());
			AppLogger.error(errmsg, ex);
			ModuleRunner.showErrorMessage(this, errmsg);
		}
		catch (OutOfMemoryError ex) {
			String errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
			AppLogger.error(errmsg, ex);
			ModuleRunner.showErrorMessage(this, errmsg);
		}

		if (newEditor != null) {
			addEditor(newEditor);
			updateAllMenuItems();
			setFocusToActiveEditor();
		} else {
			setFocusToActiveView();
		}
	}
	
	// menu : [File]-[Open for CSV]
	protected void onSelectedMenuFileOpenConfigCsv(Action action) {
		AppLogger.debug("menu [File]-[Open for CSV] selected.");
		// choose file
		File initFile = FileChooserManager.getLastChooseDocumentFile();
		File targetFile = FileChooserManager.chooseAllFile(this, initFile, null);
		if (targetFile == null) {
			// not selected
			setFocusToActiveEditor();
			return;
		}
		//--- store last file
		FileChooserManager.setLastChooseDocumentFile(targetFile);
		
		// Open file with CSV config and (@since 2.1.0) ask close chart dialog
		openEditorFromFileWithConfigCSV(targetFile);
	}

	// menu : [File]-[Reopen]-[...]
	protected void onSelectedMenuFileReopen(String command, Action action) {
		AppLogger.debug("menu [File]-[Repen] selected : " + String.valueOf(command));
		IEditorView editor = getActiveEditor();
		if (editor == null || !editor.canReopen()) {
			setFocusToActiveView();
			return;
		}
		
		// ask cancel editing
		if (editor.isModified()) {
			int ret = JOptionPane.showConfirmDialog(this,
					RunnerMessages.getInstance().confirmReopenDocument,
					editor.getDocumentTitle(),
					JOptionPane.OK_CANCEL_OPTION);
			if (ret != JOptionPane.OK_OPTION) {
				// user canceled
				setFocusToActiveView();
				return;
			}
		}
		
		// ask close chart window (@since 2.1.0)
		if (_chartWindow != null && _chartWindow.isChartWindowDocumentFile(editor.getDocumentFile())) {
			// ask close chart
			int ret = JOptionPane.showConfirmDialog(this,
						RunnerMessages.getInstance().confirmCloseChartByReopenFile,
						editor.getDocumentTitle(),
						JOptionPane.OK_CANCEL_OPTION);
			if (ret != JOptionPane.OK_OPTION) {
				// user canceled
				setFocusToActiveView();
				return;
			}
			
			// close chart window
			if (_chartWindow.destroyChartByFile(editor.getDocumentFile())) {
				// no chart window
				deleteChartWindow();
			}
		}

		// re-open
		String encoding;
		if (RunnerMenuResources.ID_FILE_REOPEN_DEFAULT.equals(command)) {
			// reopen by Default encoding
			encoding = null;
		} else {
			encoding = command.substring(RunnerMenuResources.ID_FILE_REOPEN_PREFIX.length());
		}
		// re-open document
		boolean result = false;
		try {
			editor.reopenDocument(encoding);
			result = true;
		}
		catch (FileNotFoundException ex) {
			String errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_NOTFOUND, ex, editor.getDocumentFile().getAbsolutePath());
			AppLogger.error(errmsg, ex);
			ModuleRunner.showErrorMessage(this, errmsg);
		}
		catch (UnsupportedEncodingException ex) {
			String errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_UNSUPPORTED_ENCODING, ex);
			AppLogger.error(errmsg, ex);
			ModuleRunner.showErrorMessage(this, errmsg);
		}
		catch (IOException ex) {
			String errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_READ, ex, editor.getDocumentFile().getAbsolutePath());
			AppLogger.error(errmsg, ex);
			ModuleRunner.showErrorMessage(this, errmsg);
		}
		catch (OutOfMemoryError ex) {
			String errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
			AppLogger.error(errmsg, ex);
			ModuleRunner.showErrorMessage(this, errmsg);
		}
		
		// update display
		if (result) {
			removeEditorSourceLastModifiedTime(editor);
			updateDisplayCurrentTab();
			updateAllMenuItems();
			setFocusToActiveEditor();
		} else {
			refreshEditorSourceLastModifiedTime(editor);
			setFocusToActiveView();
		}
	}

	// menu : [File]-[Close]
	protected void onSelectedMenuFileClose(Action action) {
		AppLogger.debug("menu [File]-[Close] selected.");
		int tabidx = _tabEditor.getSelectedIndex();
		if (tabidx >= 0) {
			if (closeEditor(tabidx)) {
				//updateAllMenuItems();
				updateActiveEditor();
			}
		}
		setFocusToActiveView();
	}

	// menu : [File]-[Close all]
	protected void onSelectedMenuFileAllClose(Action action) {
		AppLogger.debug("menu [File]-[Close All] selected.");
		closeAllEditors();
		setFocusToActiveView();
	}

	// menu : [File]-[Save]
	protected void onSelectedMenuFileSave(Action action) {
		AppLogger.debug("menu [File]-[Save] selected.");
		IEditorView editor = getActiveEditor();
		if (editor != null) {
			if (saveEditorDocument(editor)) {
				updateDisplayCurrentTab();
				updateAllMenuItems();
				refreshModuleProperties();
			}
		}
		setFocusToActiveView();
	}

	// menu : [File]-[Save As]
	protected void onSelectedMenuFileSaveAs(Action action) {
		AppLogger.debug("menu [File]-[Save As] selected.");
		IEditorView editor = getActiveEditor();
		if (editor != null) {
			if (saveAsEditorDocument(editor)) {
				updateDisplayCurrentTab();
				updateAllMenuItems();
				//--- register & refresh (エディタに表示するファイルはツリーとは無関係)
				//---_viewTree.refreshFileTree(ModuleFileManager.fromJavaFile(editor.getDocumentFile()));
				refreshModuleProperties();
			}
		}
		setFocusToActiveEditor();
	}

	// menu : [File] - [Save as] - [Default settings]
	protected void onSelectedMenuFileSaveAsDefault(Action action) {
		AppLogger.debug("menu [File]-[Save as]-[Default settings] selected.");
		IEditorView editor = getActiveEditor();
		if (!(editor instanceof CsvFileView)) {
			AppLogger.debug("menu [File]-[Save as]-[Default settings] Failed - Active editor is not CsvFileView.");
			setFocusToActiveEditor();
			return;
		}
		
		// ask close chart window (@since 2.1.0)
		if (_chartWindow != null && _chartWindow.isChartWindowDocumentFile(editor.getDocumentFile())) {
			// ask close chart
			int ret = JOptionPane.showConfirmDialog(this,
						RunnerMessages.getInstance().confirmCloseChartByReopenFile,
						editor.getDocumentTitle(),
						JOptionPane.OK_CANCEL_OPTION);
			if (ret != JOptionPane.OK_OPTION) {
				// user canceled
				setFocusToActiveView();
				return;
			}
			
			// close chart window
			if (_chartWindow.destroyChartByFile(editor.getDocumentFile())) {
				// no chart window
				deleteChartWindow();
			}
		}
		
		// 保存
		boolean result = ((CsvFileComponentManager)editor.getManager()).saveAsComponent(this, (CsvFileView)editor, false);
		if (result) {
			removeEditorSourceLastModifiedTime(editor);
			updateDisplayCurrentTab();
			{
				File f = editor.getDocumentFile();
				if (f != null) {
					f = f.getParentFile();
					if (f != null) {
						_viewTree.getDataFileTreeView().refreshFileTree(ModuleFileManager.fromJavaFile(f));
					}
				}
			}
			updateAllMenuItems();
			refreshModuleProperties();
			AppLogger.debug("menu [File]-[Save as]-[Default settings] Succeeded.");
		} else {
			refreshEditorSourceLastModifiedTime(editor);
			AppLogger.debug("menu [File]-[Save as]-[Default settings] Failed or Canceled.");
		}
		
		// 完了
		setFocusToActiveEditor();
	}

	// menu : [File] - [Save as] - [Config settings]
	protected void onSelectedMenuFileSaveAsConfig(Action action) {
		AppLogger.debug("menu [File]-[Save as]-[Config settings] selected.");
		IEditorView editor = getActiveEditor();
		if (!(editor instanceof CsvFileView)) {
			AppLogger.debug("menu [File]-[Save as]-[Default settings] Failed - Active editor is not CsvFileView.");
			setFocusToActiveEditor();
			return;
		}
		
		// ask close chart window (@since 2.1.0)
		if (_chartWindow != null && _chartWindow.isChartWindowDocumentFile(editor.getDocumentFile())) {
			// ask close chart
			int ret = JOptionPane.showConfirmDialog(this,
						RunnerMessages.getInstance().confirmCloseChartByReopenFile,
						editor.getDocumentTitle(),
						JOptionPane.OK_CANCEL_OPTION);
			if (ret != JOptionPane.OK_OPTION) {
				// user canceled
				setFocusToActiveView();
				return;
			}
			
			// close chart window
			if (_chartWindow.destroyChartByFile(editor.getDocumentFile())) {
				// no chart window
				deleteChartWindow();
			}
		}
		
		// 保存
		boolean result = ((CsvFileComponentManager)editor.getManager()).saveAsComponent(this, (CsvFileView)editor, true);
		if (result) {
			removeEditorSourceLastModifiedTime(editor);
			updateDisplayCurrentTab();
			{
				File f = editor.getDocumentFile();
				if (f != null) {
					f = f.getParentFile();
					if (f != null) {
						_viewTree.getDataFileTreeView().refreshFileTree(ModuleFileManager.fromJavaFile(f));
					}
				}
			}
			updateAllMenuItems();
			refreshModuleProperties();
			AppLogger.debug("menu [File]-[Save as]-[Config settings] Succeeded.");
		} else {
			refreshEditorSourceLastModifiedTime(editor);
			AppLogger.debug("menu [File]-[Save as]-[Config settings] Failed or Canceled.");
		}
		
		// 完了
		setFocusToActiveEditor();
	}

	/**
	 * Excel ファイルの変換定義をパースし、コマンドリストを取得する。
	 * @param excelbook	対象の Excel ブック
	 * @return	コマンドリスト、続行不可能な場合は <tt>null</tt>
	 * @since 3.3.0
	 */
	protected EtcConfigDataSet excel2csvParse(EtcWorkbookManager excelbook) {
		// パース
		EtcParseMonitorTask task = new EtcParseMonitorTask(excelbook);
		task.execute(this);
		
		// エラーチェック
		boolean errTooMany = false;
		if (task.getErrorCause() instanceof ConfigTooManyErrorsException) {
			// 解析エラーが多数
			errTooMany = true;
		}
		else if (task.getErrorCause() instanceof OutOfMemoryError) {
			// メモリ不足エラー
			throw new OutOfMemoryError();
		}
		else if (task.getErrorCause() != null) {
			// その他のエラー
			String errmsg = RunnerMessages.getInstance().Excel2csv_msgConfigParseError;
			AppLogger.error(errmsg, task.getErrorCause());
			Application.showErrorMessage(this, errmsg + "\n(cause) " + task.getErrorCause().toString());
			return null;
		}
		
		// 解析エラーチェック
		if (task.getParseErrorList().getErrorCount() > 0) {
			// エラーあり
			AppLogger.debug(RunnerMessages.getInstance().Excel2csv_msgConfigSyntaxError);
			//--- 解析エラーメッセージを生成
			StringBuilder sb = new StringBuilder();
			for (ConfigErrorDetail detail : task.getParseErrorList()) {
				sb.append(detail.toString());
				sb.append("\n");
			}
			if (errTooMany) {
				sb.append(RunnerMessages.getInstance().Excel2csv_msgConfigTooManyErrors);
				sb.append("\n");
			}
			//--- エラーメッセージを表示
			MessageDetailDialog.showErrorDetailMessage(this, excelbook.getFile().getName(),
					RunnerMessages.getInstance().Excel2csv_msgConfigSyntaxError, sb.toString());
			return null;
		}
		else if (task.getParseResult().isEmpty()) {
			// トップレベルコマンドが一つも存在しない
			String errmsg = RunnerMessages.getInstance().Excel2csv_msgConfigCommandsNothing;
			AppLogger.debug(errmsg);
			Application.showErrorMessage(this, errmsg);
			return null;
		}
		else if (task.getParseErrorList().getWarnCount() > 0) {
			// 警告あり
			AppLogger.debug(RunnerMessages.getInstance().Excel2csv_confirmParseWarning);
			StringBuilder sb = new StringBuilder();
			for (ConfigErrorDetail detail : task.getParseErrorList()) {
				sb.append(detail.toString());
				sb.append("\n");
			}
			int ret = MessageDetailDialog.showConfirmDetailMessage(this, excelbook.getFile().getName(),
																RunnerMessages.getInstance().Excel2csv_confirmParseWarning,
																sb.toString(), MessageDetailDialog.YES_NO_OPTION);
			if (ret != MessageDetailDialog.YES_OPTION) {
				// user canceled
				AppLogger.debug("User canceled by [No] selection.");
				return null;
			}
		}
		
		// 完了
		return task.getParseResult();
	}

	/**
	 * 変換定義の出力設定を行う。
	 * @param dataset	変換定義のデータセット
	 * @param excelfile	対象のエクセルファイル
	 * @return	処理続行なら <tt>true</tt>、処理中断なら <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @since 3.3.0
	 */
	protected boolean setupExcel2CsvDestinations(EtcConfigDataSet dataset, File excelfile) {
		EtcDestEditDialog dlg = new EtcDestEditDialog(this, dataset, excelfile);
		dlg.initialComponent();
		dlg.setVisible(true);
		dlg.dispose();
		if (dlg.getDialogResult() != IDialogResult.DialogResult_OK) {
			// user canceled
			return false;
		}
		if (AppLogger.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder();
			AppLogger.debug("[Excel 2 CSV] Setup destinations...");
			for (int index = 0; index < dataset.size(); ++index) {
				EtcConfigCsvData item = dataset.get(index);
				sb.setLength(0);
				sb.append("  - ");
				sb.append('[');
				sb.append(index+1);
				sb.append(':');
				sb.append(item.getConversionTitle());
				sb.append(']');
				sb.append(" show=");
				sb.append(item.getShowDestFileEnabled());
				sb.append(", out2temp=");
				sb.append(item.getOutputTemporaryEnabled());
				if (item.getOutputTemporaryEnabled()) {
					sb.append(", prefix=");
					sb.append(item.getTemporaryPrefix());
				}
				else {
					sb.append(", dest=");
					sb.append(item.getDestFile());
				}
				AppLogger.debug(sb.toString());
			}
		}
		return true;
	}

	/**
	 * 変換の実行
	 * @param excelbook	処理対象の Excel ワークブック
	 * @param dataset	変換定義と出力設定のデータオブジェクト
	 * @return	変換が成功した変換結果のうち、結果閲覧が設定されている抽象パスのリスト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @since 3.3.0
	 */
	protected List<File> convertExcel2Csv(EtcWorkbookManager excelbook, EtcConfigDataSet dataset)
	{
		// 出力 CSV フォーマット：標準形式の CSV とする
		String csvEncoding = AppSettings.getInstance().getAadlCsvEncodingName();
		CsvParameters csvFormat = new CsvParameters();
		
		// タスクの実行
		EtcConvertMonitorTask task = new EtcConvertMonitorTask(excelbook, dataset, csvEncoding, csvFormat);
		task.execute(this);
		
		// エラーチェック
		if (task.getErrorCause() instanceof OutOfMemoryError) {
			// メモリ不足エラー
			throw new OutOfMemoryError();
		}
		else if (task.getErrorCause() != null) {
			// その他のエラー
			String errmsg = RunnerMessages.getInstance().Excel2csv_msgOperationError;
			AppLogger.error(errmsg, task.getErrorCause());
			Application.showErrorMessage(this, errmsg + "\n(cause) " + task.getErrorCause().toString());
			return null;
		}
		
		// ユーザーキャンセル
		if (task.isCancelAccepted()) {
			// ユーザーによりキャンセルされた
			return null;
		}
		
		// ファイル置き換えエラー
		if (!task.getFailedReplaceFileMap().isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<File, File> entry : task.getFailedReplaceFileMap().entrySet()) {
				sb.append(entry.getKey());
				sb.append("\n <- ");
				sb.append(entry.getValue());
				sb.append("\n");
			}
			//--- エラーメッセージを表示
			MessageDetailDialog.showErrorDetailMessage(this, excelbook.getFile().getName(),
					RunnerMessages.getInstance().Excel2csv_msgFailedToRenameResults, sb.toString());
			return null;
		}
		
		// 結果ファイルのリストを生成
		ArrayList<File> results = new ArrayList<File>(dataset.size());
		for (EtcConfigCsvData item : dataset) {
			if (item.getShowDestFileEnabled()) {
				results.add(item.getDestFile());
			}
		}
		return results;
	}

	/**
	 * 指定されたファイルを表示する。
	 * @param dispfiles	表示する変換結果ファイルのリスト
	 * @return	正常に完了した場合は <tt>true</tt>、開く操作がキャンセルもしくは異常終了した場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @since 3.3.0
	 */
	protected boolean showExcel2CsvSucceededDestFiles(List<File> dispfiles) {
		loopFiles : for (File targetFile : dispfiles) {
			//--- 存在しないファイルは無視
			if (targetFile==null || !targetFile.exists()) {
				continue loopFiles;
			}
			//--- 既に開かれているファイルか?
			targetFile = targetFile.getAbsoluteFile();
			for (int i = 0; i < getEditorCount(); i++) {
				IEditorView editor = getEditor(i);
				if (targetFile.equals(editor.getDocumentFile())) {
					// すでに存在している場合は、そこにフォーカスを設定して次のファイルへ
					_tabEditor.setSelectedIndex(i);
					continue loopFiles;
				}
			}
			//--- open file
			IComponentManager manager = PluginManager.findSupportedPlugin(targetFile);
			if (manager == null) {
				// このファイルはサポートされていない
				String errmsg = String.format(CommonMessages.getInstance().msgUnsupportedDocument, targetFile.getAbsolutePath());
				AppLogger.error(errmsg);
				Application.showErrorMessage(RunnerFrame.this, errmsg);
				continue loopFiles;
			}
			IEditorView newEditor = null;
			try {
				newEditor = manager.openDocument(RunnerFrame.this, targetFile);
				if (newEditor == null) {
					return false;	// ユーザによるキャンセルのため、処理中断
				}
			}
			catch (FileNotFoundException ex) {
				String errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_NOTFOUND, ex, targetFile.getAbsolutePath());
				AppLogger.error(errmsg, ex);
				ModuleRunner.showErrorMessage(RunnerFrame.this, errmsg);
			}
			catch (UnsupportedEncodingException ex) {
				String errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_UNSUPPORTED_ENCODING, ex);
				AppLogger.error(errmsg, ex);
				ModuleRunner.showErrorMessage(RunnerFrame.this, errmsg);
			}
			catch (IOException ex) {
				String errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_READ, ex, targetFile.getAbsolutePath());
				AppLogger.error(errmsg, ex);
				ModuleRunner.showErrorMessage(RunnerFrame.this, errmsg);
			}
			catch (OutOfMemoryError ex) {
				String errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
				AppLogger.error(errmsg, ex);
				ModuleRunner.showErrorMessage(RunnerFrame.this, errmsg);
				return false;	// メモリ不足のため、処理継続不可
			}
			if (newEditor != null) {
				addEditor(newEditor);
			}
		}

		// 処理継続
		return true;
	}
	
	// menu : [Tool]-[Excel 2 CSV]
	// @since 3.3.0
	protected void onSelectedMenuToolExcel2Csv(Action action) {
		AppLogger.debug("menu [Tool]-[Excel 2 CSV] selected.");
		
		// Choose file
		File initFile = AppSettings.getInstance().getLastFile(AppSettings.Excel2CSV_EXCEL_INPUTFILE);
		if (initFile == null) {
			initFile = FileChooserManager.getRecommendedDirectory();
		}
		File targetFile = FileChooserManager.chooseOpenFile(this, RunnerMessages.getInstance().Excel2csv_OpenFileChooser_title,
															false, initFile, FileChooserManager.getExcelFileFilter());
		if (targetFile == null) {
			// not selected
			AppLogger.debug("[Excel 2 CSV] user canceled at excel file chooser.");
			setFocusToActiveEditor();
			return;
		}
		//--- store last file
		AppSettings.getInstance().setLastFile(AppSettings.Excel2CSV_EXCEL_INPUTFILE, targetFile);
		String excelname = targetFile.getName();
		
		// Open Excel File
		String strPassword = null;
		EtcWorkbookManager excelbook = null;
		do {
			if (AppLogger.isDebugEnabled()) {
				if (strPassword != null)
					AppLogger.debug("[Excel 2 CSV] Open Excel file with password : " + targetFile.toString());
				else
					AppLogger.debug("[Excel 2 CSV] Open Excel file without password : " + targetFile.toString());
			}
			try {
				excelbook = new EtcWorkbookManager(targetFile, strPassword);
			}
			catch (EncryptedDocumentException ex) {
				// パスワードエラー
				if (strPassword != null) {
					// パスワードが異なる
					String errmsg = RunnerMessages.getInstance().Excel2csv_msgWrongExcelPassword;
					AppLogger.debug(errmsg, ex);
					Application.showWarningMessage(this, errmsg);
				}
				//--- パスワードを問い合わせ
				final JPasswordField passfld = new JPasswordField();
				passfld.addAncestorListener(new AncestorListener() {
					@Override
					public void ancestorRemoved(AncestorEvent event) {}
					
					@Override
					public void ancestorMoved(AncestorEvent event) {}
					
					@Override
					public void ancestorAdded(AncestorEvent event) {
						passfld.requestFocusInWindow();
					}
				});
				Object[] opts = new Object[]{RunnerMessages.getInstance().Excel2csv_InputPassword_desc, passfld};
				int opt = JOptionPane.showConfirmDialog(this, opts, excelname, JOptionPane.OK_CANCEL_OPTION);
				if (opt != JOptionPane.OK_OPTION) {
					// user canceled
					AppLogger.debug("[Excel 2 CSV] user canceled at passwrod input dialog.");
					setFocusToActiveEditor();
					return;
				}
				strPassword = new String(passfld.getPassword());
				continue;
			}
			catch (OutOfMemoryError ex) {
				// メモリ不足
				String errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
				AppLogger.error(errmsg, ex);
				Application.showErrorMessage(this, errmsg);
				setFocusToActiveEditor();
				return;
			}
			catch (Throwable ex) {
				// エラー
				String errmsg = RunnerMessages.getInstance().Excel2csv_msgCouldNotOpenExcel;
				AppLogger.error(errmsg, ex);
				Application.showErrorMessage(this, errmsg + "\n(cause) " + ex.toString());
				setFocusToActiveEditor();
				return;
			}
		} while (excelbook == null);
		AppLogger.debug("Excel file open succeeded.");
		
		// Excel ファイルの CSV 出力
		List<File> dispFiles = null;
		try {
			// 変換定義の解析
			EtcConfigDataSet dataset = excel2csvParse(excelbook);
			if (dataset == null) {
				// 処理中断
				AppLogger.debug("[Excel 2 CSV] Failed to parse conversion definitions.");
				return;
			}
			
			// 変換定義の出力設定
			if (!setupExcel2CsvDestinations(dataset, excelbook.getFile())) {
				// 処理中断
				AppLogger.debug("[Excel 2 CSV] user canceled by Destination editing.");
				return;
			}
			
			// 変換
			dispFiles = convertExcel2Csv(excelbook, dataset);
		}
		catch (OutOfMemoryError ex) {
			// メモリ不足
			String errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
			AppLogger.error(errmsg, ex);
			Application.showErrorMessage(this, errmsg);
			return;
		}
		catch (Throwable ex) {
			// エラー
			String errmsg = RunnerMessages.getInstance().Excel2csv_msgOperationError;
			AppLogger.error(errmsg, ex);
			Application.showErrorMessage(this, errmsg + "\n(cause) " + ex.toString());
			return;
		}
		finally {
			excelbook.closeSilent();
			if (AppLogger.isDebugEnabled()) {
				AppLogger.debug("Excel file closed : " + excelbook.getFile().toString());
			}
			setFocusToActiveView();
		}
		
		// 変換結果の表示
		if (dispFiles != null && !dispFiles.isEmpty()) {
			showExcel2CsvSucceededDestFiles(dispFiles);
			setFocusToActiveEditor();
		}
	}

	// menu : [File] - [Export Dtalge]
	protected void onSelectedMenuFileExportDtalge(Action action) {
		AppLogger.debug("menu [File]-[Export Dtalge] selected.");
		IEditorView editor = getActiveEditor();
		if (!(editor instanceof CsvFileView)) {
			AppLogger.debug("menu [File]-[Export Dtalge] Failed - Active editor is not CsvFileView.");
			setFocusToActiveEditor();
			return;
		}
		
		// エクスポート
		File orgRecFile = FileChooserManager.getRecommendedDirectory();
		FileChooserManager.setRecommendedDirectory(getDataFileUserRootDirectory());
		boolean result = ((CsvFileComponentManager)editor.getManager()).exportDtalgeAsComponent(this, (CsvFileView)editor);
		FileChooserManager.setRecommendedDirectory(orgRecFile);
		if (result) {
			{
				File f = editor.getDocumentFile();
				if (f != null) {
					f = f.getParentFile();
					if (f != null) {
						_viewTree.getDataFileTreeView().refreshFileTree(ModuleFileManager.fromJavaFile(f));
					}
				}
			}
			AppLogger.debug("menu [File]-[Export Dtalge] Succeeded.");
		} else {
			AppLogger.debug("menu [File]-[Export Dtalge] Failed or Canceled.");
		}
		
		setFocusToActiveEditor();
	}

	/*
	// menu : [File]-[Copy to]
	protected void onSelectedMenuFileCopyTo(Action action) {
		AppLogger.debug("menu [File]-[Copy To] selected.");
		// 保存を確認
		if (confirmAndSaveAllModifiedDocuments()) {
			// コピーを実行
			viewTree.doCopyTo();
		}
		setFocusToActiveView();
	}
	*/
	
	// menu : [File]-[Move to]
	protected void onSelectedMenuFileMoveTo(Action action) {
		AppLogger.debug("menu [File]-[Move To] selected.");
		// 保存を確認
		if (!confirmAndSaveAllModifiedDocuments()) {
			setFocusToActiveView();
			return;
		}
		
		// 移動を実行
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				_viewTree.doMoveTo();
				setFocusToActiveView();
			}
		});
	}
	
	// menu : [File]-[Rename]
	protected void onSelectedMenuFileRename(Action action) {
		AppLogger.debug("menu [File]-[Rename] selected.");
		// 保存を確認
		if (confirmAndSaveAllModifiedDocuments()) {
			// 名前変更を実行
			_viewTree.doRename();
			refreshModuleProperties();
		}
		setFocusToActiveView();
	}
	
	// menu : [File]-[Refresh]
	protected void onSelectedMenuFileRefresh(Action action) {
		AppLogger.debug("menu [File]-[Refresh] selected.");
		if (_viewTree.isSelectionEmpty()) {
			_viewTree.refreshAllTree();
		} else {
			_viewTree.refreshSelectedTree();
		}
		updateAllMenuItems();
		refreshModuleProperties();
		setFocusToActiveView();
	}
	
	// menu : [File]-[Change User Folder]-[Module Execution Definition]
	protected void onSelectedMenuFileChangeMExecDefUserFolder(Action action) {
		AppLogger.debug("menu [File]-[Change User Folder]-[Module Execution Definition] selected.");
		
		if (!_viewTree.isMExecDefTreeViewSelected()) {
			_viewTree.selectMExecDefTreeView();
		}
		
		// select User Folder
		MExecDefTreeView view = _viewTree.getMExecDefTreeView();
		File newUserDir = UserFolderChooser.chooseMExecDefUserFolder(this);
		if (newUserDir != null) {
			//--- 新しい User Folder を表示
			VirtualFile vfDir = ModuleFileManager.fromJavaFile(newUserDir);
			if (!vfDir.equals(view.getUserRootDirectory())) {
				view.setUserRootDir(vfDir);
				_vformMExecDefUserRoot = createMExecDefUserRootPathFormatter();
				updateAllMenuItems();
			}
			AppLogger.debug("menu [File]-[Change User Folder]-[Module Execution Definition] Succeeded.");
		} else {
			AppLogger.debug("menu [File]-[Change User Folder]-[Module Execution Definition] Canceled.");
		}
		
		// set focus
		setFocusToActiveView();
	}
	
	// menu : [File]-[Change User Folder]-[Data File]
	protected void onSelectedMenuFileChangeDataFileUserFolder(Action action) {
		AppLogger.debug("menu [File]-[Change User Folder]-[Data File] selected.");
		
		if (!_viewTree.isDataFileTreeViewSelected()) {
			_viewTree.selectDataFileTreeView();
		}
		
		// select User Folder
		DataFileTreeView view = _viewTree.getDataFileTreeView();
		File newUserDir = UserFolderChooser.chooseDataFileUserFolder(this);
		if (newUserDir != null) {
			//--- 新しい User Folder を表示
			VirtualFile vfDir = ModuleFileManager.fromJavaFile(newUserDir);
			if (!vfDir.equals(view.getUserRootDirectory())) {
				view.setUserRootDir(vfDir);
				_vformDataFileUserRoot = createDataFileUserRootPathFormatter();
				updateAllMenuItems();
				updateDisplayAllEditorTab();
			}
			AppLogger.debug("menu [File]-[Change User Folder]-[Data File] Succeeded.");
		} else {
			AppLogger.debug("menu [File]-[Change User Folder]-[Data File] Canceled.");
		}
		
		// set focus
		setFocusToActiveView();
	}

	// menu : [File]-[Preference]
	protected void onSelectedMenuFilePreference(Action action) {
		AppLogger.debug("menu [File]-[Preference] selected.");
		PreferenceDialog dlg = new PreferenceDialog(this);
		dlg.initialComponent();
		dlg.setVisible(true);
		dlg.dispose();
		int ret = dlg.getDialogResult();
		AppLogger.debug("Dialog result : " + ret);
		if (ret == IDialogResult.DialogResult_OK) {
			updateFontBySettings();
			AppLogger.debug("menu [File]-[Preference] Succeeded.");
		} else {
			AppLogger.debug("menu [File]-[Preference] Canceled.");
		}
		setFocusToActiveEditor();
	}
	
	// menu : [File]-[Import]
	protected void onSelectedMenuFileImport(Action action) {
		AppLogger.debug("menu [File]-[Import] selected.");
		
		if (!_viewTree.isMExecDefTreeViewSelected()) {
			_viewTree.selectMExecDefTreeView();
		}
		
		if (_viewTree.getMExecDefTreeView().doImportMExecDef()) {
			AppLogger.debug("menu [File]-[Import] Succeeded.");
		} else {
			AppLogger.debug("menu [File]-[Import] Aborted.");
		}
		setFocusToActiveView();
	}
	
	// menu : [File]-[Export]
	protected void onSelectedMenuFileExport(Action action) {
		AppLogger.debug("menu [File]-[Export] selected.");
		
		if (!_viewTree.isMExecDefTreeViewSelected()) {
			_viewTree.selectMExecDefTreeView();
		}
		
		if (_viewTree.getMExecDefTreeView().doExportMExecDef()) {
			AppLogger.debug("menu [File]-[Export] Succeeded.");
		} else {
			AppLogger.debug("menu [File]-[Export] Aborted.");
		}
		setFocusToActiveView();
	}

	// menu : [File]-[Quit]
	protected void onSelectedMenuFileQuit(Action action) {
		AppLogger.debug("menu [File]-[Quit] selected.");
		closeWindow();
	}

	public void onMacMenuQuit() {
		AppLogger.debug("screen menu Application-[Quit] selected.");
		closeWindow();
	}

	// Menu : [Find]-[Find]
	protected void onSelectedMenuFindFind(Action action) {
		AppLogger.debug("catch [Find]-[Find] menu selection.");
		IEditorView editor = getActiveEditor();
		if (editor != null) {
			editor.requestFocusInComponent();
			FindReplaceInterface iFind = editor.getFindReplaceHandler();
			if (iFind != null) {
				showFindReplaceDialog(iFind);
			}
		}
	}
	
	// Menu : [Find]-[Prev]
	protected void onSelectedMenuFindPrev(Action action) {
		AppLogger.debug("catch [Find]-[Prev] menu selection");
		IEditorView editor = getActiveEditor();
		if (editor != null) {
			editor.requestFocusInComponent();
			FindReplaceInterface iFind = editor.getFindReplaceHandler();
			if (iFind != null && iFind.allowFindOperation()) {
				Cursor oldCursor = getCursor();
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				if (iFind.findPrev()) {
					setCursor(oldCursor);
					// not found keyword
					FindDialog.showNotFoundMessage(this, iFind.getKeywordString());
				} else {
					setCursor(oldCursor);
				}
				editor.requestFocusInComponent();
			}
		}
	}
	
	// Menu : [Find]-[Next]
	protected void onSelectedMenuFindNext(Action action) {
		AppLogger.debug("catch [Find]-[Next] menu selection.");
		IEditorView editor = getActiveEditor();
		if (editor != null) {
			editor.requestFocusInComponent();
			FindReplaceInterface iFind = editor.getFindReplaceHandler();
			if (iFind != null) {
				Cursor oldCursor = getCursor();
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				if (iFind.findNext()) {
					setCursor(oldCursor);
					// not found keyword
					FindDialog.showNotFoundMessage(this, iFind.getKeywordString());
				} else {
					setCursor(oldCursor);
				}
				editor.requestFocusInComponent();
			}
		}
	}
	
	// menu : [Filter]-[Run]
	protected void onSelectedMenuFilterEdit(Action action) {
		AppLogger.debug("menu [Filter]-[Edit] selected.");
		_viewTree.doShowMExecDef();
		setFocusToActiveEditor();
	}

	// menu : [Filter]-[Run]
	protected void onSelectedMenuFilterRun(Action action) {
		AppLogger.debug("menu [Filter]-[Run] selected.");
		
		// モジュール実行定義はアクティブか？
		if (!_viewTree.isMExecDefTreeViewSelected()) {
			AppLogger.debug("Abort menu handler : MExecDefTreeView is not active!");
			setFocusToActiveEditor();
			return;
		}
		MExecDefTreeView viewMExec = _viewTree.getMExecDefTreeView();
		
		// 選択数１か？
		if (viewMExec.getSelectionCount() != 1) {
			AppLogger.debug("Abort menu handler : Tree node selection multiple!");
			setFocusToActiveEditor();
			return;
		}
		
		// モジュール実行定義が選択されているか？
		final VirtualFile vfPrefs = viewMExec.getSelectionMExecDefPrefsFile();
		if (vfPrefs == null) {
			AppLogger.debug("Abort menu handler : Module Execution Definition not selected!");
			setFocusToActiveEditor();
			return;
		}
		
		// モジュール実行定義のロード
		MExecDefSettings settings = new MExecDefSettings();
		settings.loadForTarget(vfPrefs);
		
		// モジュール実行引数の設定(全引数)
		IMExecArgParam activeDocType = null;
		{
			IEditorDocument doc = getActiveDocument();
			if (doc instanceof CsvFileModel) {
				activeDocType = MExecArgCsvFile.instance;
			}
		}
		MExecArgsModel argsmodel = new MExecArgsModel(activeDocType, settings);
		
		// モードレスダイアログの生成
		{
			if (showMExecArgsEditDialog(argsmodel) != null) {
				return;		// モードレス対応
			}
			
		}
		
		//
		// 以下の処理は、MExecArgsEditDialogFrameHandler#onClosedDialog へ以降：モードレスダイアログへの対応
		//
		
//		//--- ダイアログの生成(modal)
//		MExecArgsEditDialog dlgEdit = new MExecArgsEditDialog(RunnerFrame.this,
//							_viewTree.getDataFileTreeView().getSubTree(), argsmodel);
//		dlgEdit.initialComponent();
//		//--- ダイアログの表示
//		dlgEdit.setVisible(true);
//		dlgEdit.dispose();
//		//--- 操作結果の判定
//		if (dlgEdit.getDialogResult() != IDialogResult.DialogResult_OK) {
//			// Cancelled by user
//			setFocusToActiveEditor();
//			return;
//		}
//		
//		// 実行コマンドの生成
//		CommandExecutor executor = null;
//		try {
//			executor = createCommandExecutor(argsmodel);
//		}
//		catch (IOException ex) {
//			String errmsg = RunnerMessages.getInstance().msgFailedToCreateTempFile;
//			AppLogger.error(errmsg, ex);
//			Application.showErrorMessage(this, errmsg);
//			setFocusToActiveEditor();
//			return;
//		}
//		catch (Throwable ex) {
//			String errmsg = CommonMessages.formatErrorMessage(RunnerMessages.getInstance().msgIllegalModuleArguments, ex);
//			AppLogger.error(errmsg, ex);
//			Application.showErrorMessage(this, errmsg);
//			setFocusToActiveEditor();
//			return;
//		}
//
//		// 実行開始
//		ProcessMonitorDialog dlgMon = new ProcessMonitorDialog(this, executor, argsmodel);
//		dlgMon.initialComponent();
//		try {
//			dlgMon.start();
//		}
//		catch (IOException ex) {
//			String errmsg = CommonMessages.getInstance().msgCouldNotExecute;
//			AppLogger.error(errmsg, ex);
//			Application.showErrorMessage(this, errmsg);
//			setFocusToActiveEditor();
//			return;
//		}
//		catch (IllegalStateException ex) {
//			String errmsg = CommonMessages.getInstance().msgNowExecuting;
//			AppLogger.error(errmsg, ex);
//			Application.showErrorMessage(this, errmsg);
//			setFocusToActiveEditor();
//			return;
//		}
//		dlgMon.dispose();
//		if (dlgMon.isTerminatedByUser()) {
//			// terminated by user
//			setFocusToActiveEditor();
//			return;
//		}
//		
//		// ファイルを表示
//		int numArgs = argsmodel.getNumItems();
//		loopItemList : for (int itemIndex = 0; itemIndex < numArgs; itemIndex++) {
//			MExecArgItemModel item = argsmodel.getItem(itemIndex);
//			if (ModuleArgType.OUT == item.getType() && item.isOptionEnabled()) {
//				File targetFile = ModuleFileManager.toJavaFile(item.getValue());
//				//--- 存在しないファイルは無視
//				if (targetFile==null || !targetFile.exists()) {
//					continue loopItemList;
//				}
//				//--- 既に開かれているファイルか?
//				targetFile = targetFile.getAbsoluteFile();
//				for (int i = 0; i < getEditorCount(); i++) {
//					IEditorView editor = getEditor(i);
//					if (targetFile.equals(editor.getDocumentFile())) {
//						// すでに存在している場合は、そこにフォーカスを設定して次のファイルへ
//						_tabEditor.setSelectedIndex(i);
//						continue loopItemList;
//					}
//				}
//				//--- open file
//				IComponentManager manager = PluginManager.findSupportedPlugin(targetFile);
//				if (manager == null) {
//					// このファイルはサポートされていない
//					String errmsg = String.format(CommonMessages.getInstance().msgUnsupportedDocument, targetFile.getAbsolutePath());
//					AppLogger.error(errmsg);
//					Application.showErrorMessage(this, errmsg);
//					continue loopItemList;
//				}
//				IEditorView newEditor = null;
//				try {
//					newEditor = manager.openDocument(RunnerFrame.this, targetFile);
//				}
//				catch (FileNotFoundException ex) {
//					String errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_NOTFOUND, ex, targetFile.getAbsolutePath());
//					AppLogger.error(errmsg, ex);
//					ModuleRunner.showErrorMessage(RunnerFrame.this, errmsg);
//				}
//				catch (UnsupportedEncodingException ex) {
//					String errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_UNSUPPORTED_ENCODING, ex);
//					AppLogger.error(errmsg, ex);
//					ModuleRunner.showErrorMessage(RunnerFrame.this, errmsg);
//				}
//				catch (IOException ex) {
//					String errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_READ, ex, targetFile.getAbsolutePath());
//					AppLogger.error(errmsg, ex);
//					ModuleRunner.showErrorMessage(RunnerFrame.this, errmsg);
//				}
//				catch (OutOfMemoryError ex) {
//					String errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
//					AppLogger.error(errmsg, ex);
//					ModuleRunner.showErrorMessage(RunnerFrame.this, errmsg);
//				}
//				if (newEditor != null) {
//					addEditor(newEditor);
//				}
//			}
//		}
	}

	// menu : [Filter]-[New By History]
	protected void onSelectedMenuFilterNewByHistorySelected(Action action) {
		if (action.isEnabled()) {
			AppLogger.debug("menu [Filter]-[New By History] selected.");
			_viewTool.selectHistoryToolView();
			if (_viewTool.getHistoryToolView().doCreateFilterByHistory()) {
				updateAllMenuItems();
				setFocusToActiveEditor();
				return;
			}
		}
		else {
			AppLogger.debug("menu [Filter]-[New By History] aborted - cause : menu disabled.");
		}
		setFocusToActiveView();
	}
	
	// menu : [Filter]-[Run History]-[Selected]
	protected void onSelectedMenuFilterRunHistorySelected(Action action) {
		if (action.isEnabled()) {
			AppLogger.debug("menu [Filter]-[Run History]-[Selected] selected.");
			_viewTool.selectHistoryToolView();
			if (_viewTool.getHistoryToolView().doRunSelected(false)) {
				updateAllMenuItems();
				setFocusToActiveEditor();
				return;
			}
		}
		else {
			AppLogger.debug("menu [Filter]-[Run History]-[Selected] aborted - cause : menu disabled.");
		}
		setFocusToActiveView();
	}
	
	// menu : [Filter]-[Run History]-[Before]
	protected void onSelectedMenuFilterRunHistoryBefore(Action action) {
		if (action.isEnabled()) {
			AppLogger.debug("menu [Filter]-[Run History]-[Before] selected.");
			_viewTool.selectHistoryToolView();
			if (_viewTool.getHistoryToolView().doRunBefore(false)) {
				updateAllMenuItems();
				setFocusToActiveEditor();
				return;
			}
		}
		else {
			AppLogger.debug("menu [Filter]-[Run History]-[Before] aborted - cause : menu disabled.");
		}
		setFocusToActiveView();
	}
	
	// menu : [Filter]-[Run History]-[From]
	protected void onSelectedMenuFilterRunHistoryFrom(Action action) {
		if (action.isEnabled()) {
			AppLogger.debug("menu [Filter]-[Run History]-[From] selected.");
			_viewTool.selectHistoryToolView();
			if (_viewTool.getHistoryToolView().doRunFrom(false)) {
				updateAllMenuItems();
				setFocusToActiveEditor();
				return;
			}
		}
		else {
			AppLogger.debug("menu [Filter]-[Run History]-[From] aborted - cause : menu disabled.");
		}
		setFocusToActiveView();
	}

	// menu : [Filter]-[Run History]-[Latest]
	protected void onSelectedMenuFilterRunHistoryLatest(Action action) {
		if (action.isEnabled()) {
			AppLogger.debug("menu [Filter]-[Run History]-[Latest] selected.");
			_viewTool.selectHistoryToolView();
			if (_viewTool.getHistoryToolView().doRunLatest(false)) {
				updateAllMenuItems();
				setFocusToActiveEditor();
				return;
			}
		}
		else {
			AppLogger.debug("menu [Filter]-[Run History]-[Latest] aborted - cause : menu disabled.");
		}
		setFocusToActiveView();
	}

	// menu : [Filter]-[Run As History]-[Selected]
	protected void onSelectedMenuFilterRunAsHistorySelected(Action action) {
		if (action.isEnabled()) {
			AppLogger.debug("menu [Filter]-[Run As History]-[Selected] selected.");
			_viewTool.selectHistoryToolView();
			if (_viewTool.getHistoryToolView().doRunSelected(true)) {
				updateAllMenuItems();
				setFocusToActiveEditor();
				return;
			}
			else if (isVisibleMExecArgsEditDialog()) {
				return;
			}
		}
		else {
			AppLogger.debug("menu [Filter]-[Run As History]-[Selected] aborted - cause : menu disabled.");
		}
		setFocusToActiveView();
	}

	// menu : [Filter]-[Run As History]-[Before]
	protected void onSelectedMenuFilterRunAsHistoryBefore(Action action) {
		if (action.isEnabled()) {
			AppLogger.debug("menu [Filter]-[Run As History]-[Before] selected.");
			_viewTool.selectHistoryToolView();
			if (_viewTool.getHistoryToolView().doRunBefore(true)) {
				updateAllMenuItems();
				setFocusToActiveView();
				return;
			}
			else if (isVisibleMExecArgsEditDialog()) {
				return;
			}
		}
		else {
			AppLogger.debug("menu [Filter]-[Run As History]-[Before] aborted - cause : menu disabled.");
		}
		setFocusToActiveView();
	}

	// menu : [Filter]-[Run As History]-[From]
	protected void onSelectedMenuFilterRunAsHistoryFrom(Action action) {
		if (action.isEnabled()) {
			AppLogger.debug("menu [Filter]-[Run As History]-[From] selected.");
			_viewTool.selectHistoryToolView();
			if (_viewTool.getHistoryToolView().doRunFrom(true)) {
				updateAllMenuItems();
				setFocusToActiveView();
				return;
			}
			else if (isVisibleMExecArgsEditDialog()) {
				return;
			}
		}
		else {
			AppLogger.debug("menu [Filter]-[Run As History]-[From] aborted - cause : menu disabled.");
		}
		setFocusToActiveView();
	}
	
	// menu : [Filter]-[Run As History]-[Latest]
	protected void onSelectedMenuFilterRunAsHistoryLatest(Action action) {
		if (action.isEnabled()) {
			AppLogger.debug("menu [Filter]-[Run As History]-[Latest] selected.");
			_viewTool.selectHistoryToolView();
			if (_viewTool.getHistoryToolView().doRunLatest(true)) {
				updateAllMenuItems();
				setFocusToActiveView();
				return;
			}
			else if (isVisibleMExecArgsEditDialog()) {
				return;
			}
		}
		else {
			AppLogger.debug("menu [Filter]-[Run As History]-[Latest] aborted - cause : menu disabled.");
		}
		setFocusToActiveView();
	}

	// menu : [Tool]-[Chart]-[Scatter]
	protected void onSelectedMenuToolChartScatter(Action action) {
		AppLogger.debug("menu [Tool]-[Chart]-[Scatter] selected.");
		
		IEditorView editor = getActiveEditor();
		if (!(editor instanceof CsvFileView)) {
			AppLogger.debug("menu [Tool]-[Chart]-[Scatter] Failed - Active editor is not CsvFileView.");
			setFocusToActiveEditor();
			return;
		}
		
		// データ生成
		CsvFileView view = (CsvFileView)editor;
		if (view.getTableComponent().getRowCount() <= 0 || view.getTableComponent().getColumnCount() <= 0) {
			// no data
			Application.showErrorMessage(this, RunnerMessages.getInstance().msgChartConfigNoData);
			AppLogger.debug("menu [Tool]-[Chart]-[Scatter] Failed - Active editor is not CsvFileView.");
			setFocusToActiveEditor();
			return;
		}
		int minSelRow = -1;
		int maxSelRow = -1;
		if (!view.getTableComponent().isSelectionAllRows()) {
			minSelRow = view.getTableComponent().getSelectionModel().getMinSelectionIndex();
			maxSelRow = view.getTableComponent().getSelectionModel().getMaxSelectionIndex();
		}
		int[] selcols = view.getTableComponent().getSelectedColumns();
		final ChartConfigModel chartModel = new ChartConfigModel(view.getDocument(), ChartStyles.SCATTER, minSelRow, maxSelRow, selcols);

//		// ダイアログ作成
//		ChartConfigDialog dlg = new ChartConfigDialog(this, chartModel);
//		dlg.initialComponent();
//		dlg.setVisible(true);
//		if (IDialogResult.DialogResult_OK == dlg.getDialogResult()) {
//			SwingUtilities.invokeLater(new Runnable() {
//				@Override
//				public void run() {
//					ChartViewDialog dlg = new ChartViewDialog(RunnerFrame.this, chartModel);
//					dlg.initialComponent();
//					dlg.setVisible(true);
//				}
//			});
//			AppLogger.debug("menu [Tool]-[Chart]-[Scatter] Succeeded.");
//		} else {
//			AppLogger.debug("menu [Tool]-[Chart]-[Scatter] Canceled.");
//		}
//		
//		setFocusToActiveEditor();
		
		// ダイアログ表示
		if (_chartWindow == null) {
			_chartWindow = new ChartDialogManager(this);
		}
		_chartWindow.doChartConfig(chartModel);
		updateMenuItem(RunnerMenuResources.ID_HIDE_SHOW_CHARTWINDOW);
		AppLogger.debug("menu [Tool]-[Chart]-[Scatter] Succeeded.");
	}

	// menu : [Tool]-[Chart]-[Line]
	protected void onSelectedMenuToolChartLine(Action action) {
		AppLogger.debug("menu [Tool]-[Chart]-[Line] selected.");
		
		IEditorView editor = getActiveEditor();
		if (!(editor instanceof CsvFileView)) {
			AppLogger.debug("menu [Tool]-[Chart]-[Line] Failed - Active editor is not CsvFileView.");
			setFocusToActiveEditor();
			return;
		}
		
		// データ生成
		CsvFileView view = (CsvFileView)editor;
		if (view.getTableComponent().getRowCount() <= 0 || view.getTableComponent().getColumnCount() <= 0) {
			// no data
			Application.showErrorMessage(this, RunnerMessages.getInstance().msgChartConfigNoData);
			AppLogger.debug("menu [Tool]-[Chart]-[Line] Failed - Active editor is not CsvFileView.");
			setFocusToActiveEditor();
			return;
		}
		int minSelRow = -1;
		int maxSelRow = -1;
		if (!view.getTableComponent().isSelectionAllRows()) {
			minSelRow = view.getTableComponent().getSelectionModel().getMinSelectionIndex();
			maxSelRow = view.getTableComponent().getSelectionModel().getMaxSelectionIndex();
		}
		int[] selcols = view.getTableComponent().getSelectedColumns();
		final ChartConfigModel chartModel = new ChartConfigModel(view.getDocument(), ChartStyles.LINE, minSelRow, maxSelRow, selcols);
		
//		// ダイアログ作成
//		ChartConfigDialog dlg = new ChartConfigDialog(this, chartModel);
//		dlg.initialComponent();
//		dlg.setVisible(true);
//		if (IDialogResult.DialogResult_OK == dlg.getDialogResult()) {
//			SwingUtilities.invokeLater(new Runnable() {
//				@Override
//				public void run() {
//					ChartViewDialog dlg = new ChartViewDialog(RunnerFrame.this, chartModel);
//					dlg.initialComponent();
//					dlg.setVisible(true);
//				}
//			});
//			AppLogger.debug("menu [Tool]-[Chart]-[Line] Succeeded.");
//		} else {
//			AppLogger.debug("menu [Tool]-[Chart]-[Line] Canceled.");
//		}
//		
//		setFocusToActiveEditor();
		
		// ダイアログ表示
		if (_chartWindow == null) {
			_chartWindow = new ChartDialogManager(this);
		}
		_chartWindow.doChartConfig(chartModel);
		updateMenuItem(RunnerMenuResources.ID_HIDE_SHOW_CHARTWINDOW);
		AppLogger.debug("menu [Tool]-[Chart]-[Line] Succeeded.");
	}

	// menu : [Help]-[About]
	protected void onSelectedMenuHelpAbout(Action action) {
		AppLogger.debug("menu [Help]-[About] selected.");
		//--- Show Version-Info message
		showAboutDialog();
	}
	
	public void onMacMenuAbout() {
		AppLogger.debug("screen menu Application-[About] selected.");
		//--- Show Version-Info message
		showAboutDialog();
	}
	
	protected void showAboutDialog() {
//		//--- Show Version-Info message with LibVersion
//		JButton btn = new JButton(RunnerMessages.getInstance().LibVersionDlg_Button);
//		btn.addActionListener(new ActionListener(){
//			public void actionPerformed(ActionEvent e) {
//				LibVersionDialog dlg = new LibVersionDialog(RunnerFrame.this);
//				dlg.setVisible(true);
//			}
//		});
//		
//		String version_message = FSEnvironment.getInstance().title();
//		if (!Strings.isNullOrEmpty(version_message)) {
//			version_message = version_message + "\n - " + ModuleRunner.LOCAL_VERSION;
//		} else {
//			version_message = ModuleRunner.LOCAL_VERSION;
//		}
//		
//		Object[] options = new Object[]{btn, CommonMessages.getInstance().Button_Close};
//		JOptionPane.showOptionDialog(this,
//				version_message,
//				RunnerMessages.getInstance().appMainTitle,
//				JOptionPane.OK_OPTION,
//				JOptionPane.INFORMATION_MESSAGE,
//				null,
//				options,
//				options[1]);
		
		//--- Show Simple-Version-Info message
		String version_message = FSEnvironment.getInstance().title();
		if (!Strings.isNullOrEmpty(version_message)) {
			version_message = version_message + "\n - " + ModuleRunner.LOCAL_VERSION;
		} else {
			version_message = ModuleRunner.LOCAL_VERSION;
		}
		JOptionPane.showMessageDialog(this,
				version_message,
				RunnerMessages.getInstance().appMainTitle,
				JOptionPane.INFORMATION_MESSAGE);
		
		//--- set focus to this frame
		setFocusToActiveEditor();
	}

	//------------------------------------------------------------
	// Menu event handler
	//------------------------------------------------------------
	
	public void menuActionPerformed(ActionEvent e) {
		// コマンド名が未定義の場合は、処理しない
		String pCommand = e.getActionCommand();
		if (Strings.isNullOrEmpty(pCommand))
			return;

		// アクションが未定義のものは処理しない
		Object pSource  = e.getSource();
		Action pAction = null;
		if (pSource instanceof AbstractButton)
			pAction = ((AbstractButton)pSource).getAction();
		if (pAction == null)
			return;
		
		// ツリービューがアクティブなら、そのメニューコマンドを処理
		if (_activeViewManager.isComponentActivated(_viewTree)) {
			// ツリービューのハンドラ
			if (_viewTree.onProcessMenuSelection(pCommand, pSource, pAction)) {
				// process is terminate.
				return;
			}
		}
		
		// エディタビューがアクティブなら、そのメニューコマンドを処理
		if (_activeViewManager.isComponentActivated(_tabEditor)) {
			// アクティブビューが存在する場合は、そのハンドラを呼び出す。
			IEditorView view = getActiveEditor();
			if (view != null) {
				//--- ビュー固有のハンドラ
				if (view.onProcessMenuSelection(pCommand, pSource, pAction)) {
					// process is terminate.
					return;
				}
				
				//--- プラグイン固有のハンドラ
				if (view.getManager().onProcessMenuSelection(pCommand, pSource, pAction)) {
					// process is terminate.
					return;
				}
			}
		}
		
		// ツールビューがアクティブなら、そのメニューコマンドを処理
		if (_activeViewManager.isComponentActivated(_viewTool)) {
			// ツールビューのハンドラ
			if (_viewTool.onProcessMenuSelection(pCommand, pSource, pAction)) {
				// process is terminate.
				return;
			}
		}
		
		// このイベントが処理されていない場合は、このフレームのイベントハンドラを呼び出す
		onProcessMenuSelection(pCommand, pSource, pAction);
	}
	
	public void menuUpdatePerformed(String command, Object source) {
		// コマンド名が未定義の場合は、処理しない
		if (Strings.isNullOrEmpty(command))
			return;

		// アクションが未定義のものは処理しない
		Action pAction = null;
		if (source instanceof AbstractButton)
			pAction = ((AbstractButton)source).getAction();
		if (pAction == null)
			return;
		
		// ツリービューがアクティブなら、そのメニューコマンドを処理
		if (_activeViewManager.isComponentActivated(_viewTree)) {
			// ツリービューのハンドラ
			if (_viewTree.onProcessMenuUpdate(command, source, pAction)) {
				// process is terminate.
				return;
			}
		}
		
		// エディタビューがアクティブなら、そのメニューコマンドを処理
		if (_activeViewManager.isComponentActivated(_tabEditor)) {
			// アクティブビューが存在する場合は、そのハンドラを呼び出す。
			IEditorView view = getActiveEditor();
			if (view != null) {
				//--- ビュー固有のハンドラ
				if (view.onProcessMenuUpdate(command, source, pAction)) {
					// process is terminate.
					return;
				}
				
				//--- プラグイン固有のハンドラ
				if (view.getManager().onProcessMenuUpdate(command, source, pAction)) {
					// process is terminate.
					return;
				}
			}
		}
		
		// ツールビューがアクティブなら、そのメニューコマンドを処理
		if (_activeViewManager.isComponentActivated(_viewTool)) {
			// ツールビューのハンドラ
			if (_viewTool.onProcessMenuUpdate(command, source, pAction)) {
				// process is terminate.
				return;
			}
		}
		
		// このイベントが処理されていない場合は、このフレームのイベントハンドラを呼び出す
		onProcessMenuUpdate(command, source, pAction);
	}

	//------------------------------------------------------------
	// Event handler actions
	//------------------------------------------------------------
	
	protected void setupComponentActions() {
		// Editor tab
		_tabEditor.addMouseListener(new EditorTabMouseHandler());
		_tabEditor.addChangeListener(new EditorTabSelectionHandler());
		
		// ActiveView
		_activeViewManager.addActiveViewChangeListener(new EditorActiveViewChangeListener());
	}
	
	class MExecArgsDialogFrameHandler implements MExecArgsDialogHandler
	{
		public boolean doCloseFileOnEditor(final IMExecArgsDialog dlg, final VirtualFile file) {
			//AppLogger.debug(String.format("Called MExecArgsDialogFrameHandler#doCloseFileOnEditor(%s) : file=\"%s\"", dlg.getMExecDefPath(), String.valueOf(file)));
			
			// 指定されたファイルのドキュメントエディタを閉じる
			File fTargetFile = ModuleFileManager.toJavaFile(file);
			if (file != null) {
				IEditorView[] editors = RunnerFrame.this.getAllEditors();
				if (editors != null && editors.length > 0) {
					for (IEditorView ev : editors) {
						if (ev.getDocument().hasTargetFile() && ev.getDocumentFile().equals(fTargetFile)) {
							int tabindex = RunnerFrame.this.indexOfEditor(ev);
							if (tabindex >= 0) {
								return RunnerFrame.this.closeEditor(tabindex);
							}
						}
					}
				}
			}
			
			// completed
			return true;
		}

		public boolean doOpenFileOnEditor(final IMExecArgsDialog dlg, final VirtualFile file) {
			//AppLogger.debug(String.format("Called MExecArgsDialogFrameHandler#doOpenFileOnEditor(%s) : file=\"%s\"", dlg.getMExecDefPath(), String.valueOf(file)));
			
			// 指定されたファイルをエディタで開く
			if (!file.exists() || !file.isFile())
				return false;
			File javafile = ModuleFileManager.toJavaFile(file);
			if (javafile == null)
				return false;
			
			return RunnerFrame.this.openEditorFromFile(javafile);
		}
		
		public boolean doOpenFileByCsvOnEditor(IMExecArgsDialog dlg, VirtualFile file) {
			//AppLogger.debug(String.format("Called MExecArgsDialogFrameHandler#doOpenFileByCsvOnEditor(%s) : file=\"%s\"", dlg.getMExecDefPath(), String.valueOf(file)));
			
			// 指定されたファイルをCSV形式でエディタで開く
			if (!file.exists() || !file.isFile())
				return false;
			File javafile = ModuleFileManager.toJavaFile(file);
			if (javafile == null)
				return false;
			
			return RunnerFrame.this.openEditorFromFileWithConfigCSV(javafile);
		}

		public boolean canCloseDialog(final IMExecArgsDialog dlg) {
			//AppLogger.debug(String.format("Called MExecArgsDialogFrameHandler#canCloseDialog(%s)", dlg.getMExecDefPath()));
			
			// このダイアログで開いたドキュメントのクローズを確認
			if (dlg instanceof AbMExecArgsDialog) {
				VirtualFile vfExecDefDir = ((AbMExecArgsDialog)dlg).getArgsModel().getSettings().getExecDefDirectory();
				IEditorView[] editors = RunnerFrame.this.getAllEditors();
				if (editors.length > 0) {
					for (IEditorView ev : editors) {
						File fDocument = ev.getDocumentFile();
						if (fDocument != null) {
							VirtualFile vfDoc = ModuleFileManager.fromJavaFile(fDocument);
							if (vfDoc.isDescendingFrom(vfExecDefDir)) {
								// 変更されていなければ、閉じる
								int editorTabIndex = _tabEditor.indexOfComponent((Component)ev);
								if (!closeEditor(editorTabIndex)) {
									return false;
								}
							}
						}
					}
				}
			}
			
			//--- 関連する全てのエディタは、クローズ済
			return true;
		}

		public void onClosedDialog(final IMExecArgsDialog dlg) {
			//AppLogger.debug(String.format("Called MExecArgsDialogFrameHandler#onClosedDialog(%s)", dlg.getMExecDefPath()));
			
			// windowClose() イベントが何度も呼ばれるので、変なコードを書く。
			if (_dlgMExecArgsEdit == null || dlg != _dlgMExecArgsEdit) {
				// 呼び出したダイアログが管理下になければ、処理しない
				return;
			}
			//AppLogger.debug("Called MExecArgsDialogFrameHandler#onClosedDialog, to go!");
			
			// ダイアログ破棄の処理
			if (_dlgMExecArgsEdit != null && dlg == _dlgMExecArgsEdit) {
				if (dlg.isDisplayable()) {
					dlg.destroy();
					return;
				}
				_dlgMExecArgsEdit = null;	// ダイアログの破棄
			}
			
			// ダイアログの情報取得
			final int dlgResult = dlg.getDialogResult();
			if (dlg instanceof AbMExecArgsDialog) {
				// 単一のモジュール実行
				final ModuleRuntimeData data = new ModuleRuntimeData(((AbMExecArgsDialog)dlg).getArgsModel());
				// ダイアログが閉じられたとき、モジュール実行定義ツリーを表示
				_viewTree.setEnabledMExecDefTreeView(true);
				_viewTree.selectMExecDefTreeView();
				//_viewTree.getSelectedTreeView().requestFocusInComponent();	// フォーカスを設定しない
				updateAllMenuItems();
				updateStatusBarMessage();

				// ダイアログ結果の判定
				if (dlgResult != IDialogResult.DialogResult_OK) {
					// キャンセル操作
					setFocusToActiveView();
					return;
				}
				
				// 実行
				AsyncProcessMonitorWindow monitor = AsyncProcessMonitorWindow.runModule(RunnerFrame.this, data,
																			((AbMExecArgsDialog)dlg).isConsoleShowAtStart(),
																			((AbMExecArgsDialog)dlg).isConsoleAutoClose());
				if (monitor != null) {
					addAsyncProcessMonitor(monitor);
					return;
				}
			}
			else {
				// 複数のモジュール連続実行
				final boolean consoleShowAtStart;
				final boolean consoleAutoClose;
				if (dlg instanceof AbModuleArgsDialog) {
					consoleShowAtStart = ((AbModuleArgsDialog)dlg).isConsoleShowAtStart();
					consoleAutoClose   = ((AbModuleArgsDialog)dlg).isConsoleAutoClose();
				} else {
					consoleShowAtStart = AppSettings.getInstance().getConsoleShowAtStart();
					consoleAutoClose   = AppSettings.getInstance().getConsoleAutoClose();
				}
				final RelatedModuleList modules = ((AbModuleArgsDialog)dlg).getModuleList();
				SwingUtilities.invokeLater(new Runnable(){
					public void run() {
						// ダイアログが閉じられたとき、モジュール実行定義ツリーを表示
						_viewTree.setEnabledMExecDefTreeView(true);
						_viewTree.selectMExecDefTreeView();
						_viewTree.getSelectedTreeView().requestFocusInComponent();
						updateAllMenuItems();
						updateStatusBarMessage();

						// ダイアログ結果の判定
						if (dlgResult != IDialogResult.DialogResult_OK) {
							// キャンセル操作
							setFocusToActiveView();
							return;
						}
						
						// 実行
						//boolean ret = ContinuousProcessMonitorDialog.runAllModules(RunnerFrame.this, modules);
						//if (ret) {
						//	openResultFilesByModuleResults(modules);
						//}
						AsyncProcessMonitorWindow monitor = AsyncProcessMonitorWindow.runAllModules(RunnerFrame.this, modules, consoleShowAtStart, consoleAutoClose);
						if (monitor != null) {
							addAsyncProcessMonitor(monitor);
						}
						else {
							setFocusToActiveView();
						}
					}
				});
			}
			setFocusToActiveEditor();
			
//			if (dlg instanceof AbMExecArgsDialog) {
//				final MExecArgsModel argsmodel = ((AbMExecArgsDialog)dlg).getArgsModel();
//				SwingUtilities.invokeLater(new Runnable() {
//					public void run() {
//						// ダイアログが閉じられたとき、モジュール実行定義ツリーを表示
//						_viewTree.setEnabledMExecDefTreeView(true);
//						_viewTree.selectMExecDefTreeView();
//						_viewTree.getSelectedTreeView().requestFocusInComponent();
//						updateAllMenuItems();
//						updateStatusBarMessage();
//						
//						// ダイアログ結果の判定
//						if (dlgResult != IDialogResult.DialogResult_OK) {
//							// キャンセル操作
//							setFocusToActiveView();
//							return;
//						}
//						
//						// 実行コマンドの生成
//						CommandExecutor executor = null;
//						try {
//							executor = createCommandExecutor(argsmodel);
//						}
//						catch (IOException ex) {
//							String errmsg = RunnerMessages.getInstance().msgFailedToCreateTempFile;
//							AppLogger.error(errmsg, ex);
//							Application.showErrorMessage(RunnerFrame.this, errmsg);
//							setFocusToActiveEditor();
//							return;
//						}
//						catch (Throwable ex) {
//							String errmsg = CommonMessages.formatErrorMessage(RunnerMessages.getInstance().msgIllegalModuleArguments, ex);
//							AppLogger.error(errmsg, ex);
//							Application.showErrorMessage(RunnerFrame.this, errmsg);
//							setFocusToActiveEditor();
//							return;
//						}
//
//						// 実行開始
//						ProcessMonitorDialog dlgMon = new ProcessMonitorDialog(RunnerFrame.this, executor, argsmodel);
//						dlgMon.initialComponent();
//						long lStartTime = System.currentTimeMillis();
//						try {
//							dlgMon.start();
//						}
//						catch (IOException ex) {
//							String errmsg = CommonMessages.getInstance().msgCouldNotExecute;
//							AppLogger.error(errmsg, ex);
//							Application.showErrorMessage(RunnerFrame.this, errmsg);
//							setFocusToActiveEditor();
//							return;
//						}
//						catch (IllegalStateException ex) {
//							String errmsg = CommonMessages.getInstance().msgNowExecuting;
//							AppLogger.error(errmsg, ex);
//							Application.showErrorMessage(RunnerFrame.this, errmsg);
//							setFocusToActiveEditor();
//							return;
//						}
//						dlgMon.dispose();
//						
//						// 実行結果から、実行履歴を追加
//						ModuleRuntimeData data = new ModuleRuntimeData(argsmodel);
//						data.setStartTime(lStartTime);
//						data.setResults(executor, dlgMon.isTerminatedByUser());
//						_viewTool.getHistoryToolView().addHistory(data);
//
//						// ユーザーによる中断なら、処理終了
//						if (dlgMon.isTerminatedByUser()) {
//							// terminated by user
//							setFocusToActiveEditor();
//							return;
//						}
//						
//						// ファイルを表示
//						openModuleResultFiles(data);
////						int numArgs = argsmodel.getNumItems();
////						loopItemList : for (int itemIndex = 0; itemIndex < numArgs; itemIndex++) {
////							MExecArgItemModel item = argsmodel.getItem(itemIndex);
////							if (ModuleArgType.OUT == item.getType() && item.isOptionEnabled()) {
////								File targetFile = ModuleFileManager.toJavaFile(item.getValue());
////								//--- 存在しないファイルは無視
////								if (targetFile==null || !targetFile.exists()) {
////									continue loopItemList;
////								}
////								//--- 既に開かれているファイルか?
////								targetFile = targetFile.getAbsoluteFile();
////								for (int i = 0; i < getEditorCount(); i++) {
////									IEditorView editor = getEditor(i);
////									if (targetFile.equals(editor.getDocumentFile())) {
////										// すでに存在している場合は、そこにフォーカスを設定して次のファイルへ
////										_tabEditor.setSelectedIndex(i);
////										continue loopItemList;
////									}
////								}
////								//--- open file
////								IComponentManager manager = PluginManager.findSupportedPlugin(targetFile);
////								if (manager == null) {
////									// このファイルはサポートされていない
////									String errmsg = String.format(CommonMessages.getInstance().msgUnsupportedDocument, targetFile.getAbsolutePath());
////									AppLogger.error(errmsg);
////									Application.showErrorMessage(RunnerFrame.this, errmsg);
////									continue loopItemList;
////								}
////								IEditorView newEditor = null;
////								try {
////									newEditor = manager.openDocument(RunnerFrame.this, targetFile);
////								}
////								catch (FileNotFoundException ex) {
////									String errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_NOTFOUND, ex, targetFile.getAbsolutePath());
////									AppLogger.error(errmsg, ex);
////									ModuleRunner.showErrorMessage(RunnerFrame.this, errmsg);
////								}
////								catch (UnsupportedEncodingException ex) {
////									String errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_UNSUPPORTED_ENCODING, ex);
////									AppLogger.error(errmsg, ex);
////									ModuleRunner.showErrorMessage(RunnerFrame.this, errmsg);
////								}
////								catch (IOException ex) {
////									String errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_READ, ex, targetFile.getAbsolutePath());
////									AppLogger.error(errmsg, ex);
////									ModuleRunner.showErrorMessage(RunnerFrame.this, errmsg);
////								}
////								catch (OutOfMemoryError ex) {
////									String errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
////									AppLogger.error(errmsg, ex);
////									ModuleRunner.showErrorMessage(RunnerFrame.this, errmsg);
////								}
////								if (newEditor != null) {
////									addEditor(newEditor);
////								}
////							}
////						}
//					}
//				});
//			}
		}

		public void onHiddenDialog(final IMExecArgsDialog dlg) {
			//AppLogger.debug(String.format("Called MExecArgsDialogFrameHandler#onHiddenDialog(%s)", dlg.getMExecDefPath()));
		}

		public void onShownDialog(final IMExecArgsDialog dlg) {
			//AppLogger.debug(String.format("Called MExecArgsDialogFrameHandler#onShownDialog(%s)", dlg.getMExecDefPath()));
			//--- モジュール実行定義ツリーを非表示
			_viewTree.selectDataFileTreeView();
			_viewTree.setEnabledMExecDefTreeView(false);
			updateAllMenuItems();
			updateStatusBarMessage();
		}
	}

	/**
	 * エディタタブのコンテキストメニュー用ハンドラ
	 */
	class EditorTabMouseHandler extends MouseAdapter {
		public void mouseClicked(MouseEvent me) {
			//setFocusToActiveEditor();
		}
		public void mousePressed(MouseEvent me) {
			setFocusToActiveEditor();
			evaluatePopupMenu(me);
		}
		public void mouseReleased(MouseEvent me) {
			evaluatePopupMenu(me);
		}
		protected void evaluatePopupMenu(MouseEvent me) {
			if (me.isPopupTrigger()) {
				int tabIndex = _tabEditor.indexAtLocation(me.getX(), me.getY());
				if (tabIndex >= 0) {
					RunnerMenuBar menuBar = getActiveMainMenuBar();
					if (menuBar != null) {
						menuBar.getEditorTabContextMenu().show(me.getComponent(), me.getX(), me.getY());
					}
				}
			}
		}
	}

	/**
	 * エディタタブの選択変更イベントハンドラ
	 */
	class EditorTabSelectionHandler implements ChangeListener {
		public void stateChanged(ChangeEvent ce) {
			//System.err.println("EditorFrame.EditorTabSelectionHandler#stateChanged(" + ce.toString() + ")");
			
			/*--- old implementation
			EditorMenuBar menuBar;
			int selidx = tabEditor.getSelectedIndex();
			if (selidx >= 0) {
				activeEditor = getEditor(selidx);
				menuBar = activeEditor.getDocumentMenuBar();
			} else {
				activeEditor = null;
				menuBar = null;
			}
			EditorFrame.this.setTitle(getFrameTitle());
			setEditorMenuBar(menuBar==null ? defMainMenu : menuBar);
			updateFindReplaceDialogHandler();
			updateAllMenuItems();
			if (activeEditor != null) {
				activeEditor.requestFocusInComponent();
			}
			--- old implementation ---*/
			updateActiveEditor();
			SwingUtilities.invokeLater(new Runnable(){
				public void run() {
					//--- 必要ならドキュメントをリフレッシュ
					refreshEditorDocumentFromSourceFileAsNeeded(getActiveEditor());
				}
			});
		}
	}

	/**
	 * エディタの変更フラグの状態変更イベントハンドラ
	 */
	class EditorModifiedChangeHandler implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			String strName = evt.getPropertyName();
			if (IEditorView.PROP_MODIFIED.equals(strName)) {
				Object objsrc = evt.getSource();
				if (objsrc instanceof IEditorView) {
					evaluateModifiedChanged((IEditorView)objsrc);
				}
//				updateMenuItem(RunnerMenuResources.ID_FILE_SAVE);
			}
		}
		protected void evaluateModifiedChanged(IEditorView editor) {
			int tabIndex = _tabEditor.indexOfComponent(editor.getComponent());
			if (tabIndex >= 0) {
				String tabTitle = getEditorTabTitle(editor);
				_tabEditor.setTitleAt(tabIndex, tabTitle);
			}
		}
	}

	/**
	 * アクティブなビューが変更されたことを通知するイベント
	 */
	class EditorActiveViewChangeListener implements ActiveViewChangeListener {
		public void activeViewChanged(ActiveViewChangeEvent e) {
//			AppLogger.debug(e.toString());
			//--- メニューを更新
			updateAllMenuItems();
			updateStatusBarMessage();
		}
	}
	
	class FileDropTargetListener extends DropTargetAdapter
	{
		private boolean _canImport = false;
		
		@Override
		public void dragEnter(DropTargetDragEvent dtde) {
			_canImport = false;
			
			// サポートする DataFlavor のチェック
			if (!dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				// サポートされないデータ形式
				dtde.rejectDrag();
				return;
			}
			
			// ソースアクションにコピーが含まれていない場合は、許可しない
			if ((dtde.getSourceActions() & DnDConstants.ACTION_COPY) == 0) {
				// サポートされないドロップアクション
				dtde.rejectDrag();
				_canImport = false;
				return;
			}
			
			// ドロップアクションをコピー操作に限定
			dtde.acceptDrag(DnDConstants.ACTION_COPY);
			_canImport = true;
		}

		@Override
		public void dragExit(DropTargetEvent dte) {
			_canImport = false;
		}

		@Override
		public void dragOver(DropTargetDragEvent dtde) {
			if (_canImport) {
				dtde.acceptDrag(DnDConstants.ACTION_COPY);
			} else {
				dtde.rejectDrag();
			}
		}

		@SuppressWarnings("unchecked")
		public void drop(DropTargetDropEvent dtde) {
			// サポートする DataFlavor のチェック
			if (!dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				// サポートされないデータ形式
				dtde.rejectDrop();
				return;
			}
			
			// ソースアクションにコピーが含まれていない場合は、許可しない
			if ((dtde.getSourceActions() & DnDConstants.ACTION_COPY) == 0) {
				// サポートされないドロップアクション
				dtde.rejectDrop();
				return;
			}
			
			// データソースの取得
			dtde.acceptDrop(DnDConstants.ACTION_COPY);
			Transferable t = dtde.getTransferable();
			if (t == null) {
				dtde.rejectDrop();
				return;
			}
			try {
				File targetFile;
				List flist = (List)t.getTransferData(DataFlavor.javaFileListFlavor);
				//--- 設定ファイル(*.prefs)を除くパスのみを収集
				Set<File> fileset = new HashSet<File>();
				for (Object elem : flist) {
					if (elem instanceof File) {
						targetFile = (File)elem;
						if (!Strings.endsWithIgnoreCase(targetFile.getName(), ModuleFileManager.EXT_FILE_PREFS)) {
							fileset.add(targetFile);
						}
					}
				}
				
				// ファイルを開く
				int openCount = 0;
				loopFileList : for (Object elem : flist) {
					if (elem instanceof File) {
						targetFile = (File)elem;
						//--- ターゲットとセットの設定ファイルは無視する
						if (Strings.endsWithIgnoreCase(targetFile.getName(), ModuleFileManager.EXT_FILE_PREFS)) {
							String strPath = targetFile.getPath();
							File relFile = new File(strPath.substring(0, strPath.length() - ModuleFileManager.EXT_FILE_PREFS.length()));
							if (fileset.contains(relFile)) {
								continue loopFileList;
							}
						}
						//--- 既に開かれているファイルか?
						targetFile = targetFile.getAbsoluteFile();
						for (int i = 0; i < getEditorCount(); i++) {
							IEditorView editor = getEditor(i);
							if (targetFile.equals(editor.getDocumentFile())) {
								// すでに存在している場合は、そこにフォーカスを設定して次のファイルへ
								_tabEditor.setSelectedIndex(i);
								continue loopFileList;
							}
						}
						//--- open file
						IComponentManager manager = PluginManager.findSupportedPlugin(targetFile);
						if (manager == null) {
							// このファイルはサポートされていない
							String errmsg = String.format(CommonMessages.getInstance().msgUnsupportedDocument, targetFile.getAbsolutePath());
							AppLogger.error(errmsg);
							ModuleRunner.showErrorMessage(RunnerFrame.this, errmsg);
							dtde.dropComplete(true);
							setFocusToActiveEditor();
							return;	// サポートされていないファイルと判明した時点で、処理終了
						}
						IEditorView newEditor = null;
						try {
							newEditor = manager.openDocument(RunnerFrame.this, targetFile);
						}
						catch (FileNotFoundException ex) {
							String errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_NOTFOUND, ex, targetFile.getAbsolutePath());
							AppLogger.error(errmsg, ex);
							ModuleRunner.showErrorMessage(RunnerFrame.this, errmsg);
						}
						catch (UnsupportedEncodingException ex) {
							String errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_UNSUPPORTED_ENCODING, ex);
							AppLogger.error(errmsg, ex);
							ModuleRunner.showErrorMessage(RunnerFrame.this, errmsg);
						}
						catch (IOException ex) {
							String errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_READ, ex, targetFile.getAbsolutePath());
							AppLogger.error(errmsg, ex);
							ModuleRunner.showErrorMessage(RunnerFrame.this, errmsg);
						}
						catch (OutOfMemoryError ex) {
							String errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
							AppLogger.error(errmsg, ex);
							ModuleRunner.showErrorMessage(RunnerFrame.this, errmsg);
						}
						if (newEditor != null) {
							addEditor(newEditor);
							openCount++;
						} else {
							dtde.dropComplete(true);
							setFocusToActiveEditor();
							return;	// 読み込みエラーが発生した時点で、処理終了
						}
					}
				}

				dtde.dropComplete(true);
				if (openCount > 0)
					updateAllMenuItems();
				setFocusToActiveEditor();
				return;
			}
			catch (UnsupportedFlavorException ex) {
				AppLogger.error("Failed to drop to editor.", ex);
			}
			catch (IOException ex) {
				AppLogger.error("Failed to drop to editor.", ex);
			}
			
			// drop を受け付けない
			dtde.rejectDrop();
		}
	}

	//------------------------------------------------------------
	// Component actions override to FrameWindow
	//------------------------------------------------------------
	
	// --- Window opened event
	@Override
	protected void onWindowOpened(WindowEvent we) {
//		// save window bounds
//		this._lastFrameSize.setSize(this.getSize());
//		try {
//			if (_lastFrameLocation != null) {
//				_lastFrameLocation.setLocation(this.getLocationOnScreen());
//			} else {
//				_lastFrameLocation = new Point(this.getLocationOnScreen());
//			}
//			// TODO: Debug
//			System.err.println("RunnerFrame#onWindowOpened() : set last frame location : " + _lastFrameLocation);
//		} catch(IllegalComponentStateException icse) {
//			AppLogger.debug(icse);
//		}
		
		// set Focus to Tree View
		_viewTree.requestFocusInComponent();
	}

	// --- Window activated event
	@Override
	protected void onWindowActivated(WindowEvent e) {
		// アクティブなエディタの編集メニューを更新
		if (e.getWindow() == this) {
			this.updateAllMenuItems();
			//--- 必要ならドキュメントをリフレッシュ
			refreshEditorDocumentFromSourceFileAsNeeded(getActiveEditor());
		}
	}

	// --- Window closed event
	@Override
	protected void onWindowClosed(WindowEvent we) {
		// save Preferences
		try {
			AppSettings.flush();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		AppLogger.info("\n<<<<< Finished - " + ModuleRunner.LOCAL_VERSION + " >>>>>\n");
	}
	
//	// --- Window resized event
//	@Override
//	protected void onWindowResized(ComponentEvent ce) {
//		if (this.getExtendedState() == JFrame.NORMAL) {
//			_lastFrameSize.setSize(this.getSize());
//		}
//	}
//	
//	// --- Window moved event
//	@Override
//	protected void onWindowMoved(ComponentEvent ce) {
//		if (this.getExtendedState() == JFrame.NORMAL) {
//			try {
//				if (_lastFrameLocation != null) {
//					_lastFrameLocation.setLocation(this.getLocationOnScreen());
//				} else {
//					_lastFrameLocation = new Point(this.getLocationOnScreen());
//				}
//				// TODO: Debug
//				System.err.println("RunnerFrame#onWindowMoved() : save window location : " + _lastFrameLocation);
//			} catch(IllegalComponentStateException icse) {
//				AppLogger.debug(icse);
//			}
//		}
//	}

	/**
	 * フィルタツリー、データファイルツリーのルートディレクトリによるパスフォーマッター。
	 * @since 2.0.0
	 */
	protected class RunnerFramePathFormatter implements VirtualFilePathFormatter
	{
		@Override
		public boolean isDescendingFromBasePath(VirtualFile file) {
			if (file != null) {
				// ユーザーデータファイルルート
				if (_vformDataFileUserRoot != null && _vformDataFileUserRoot.isDescendingFromBasePath(file)) {
					return true;
				}
				
				// システムデータファイルルート
				if (_vformDataFileSystemRoot != null && _vformDataFileSystemRoot.isDescendingFromBasePath(file)) {
					return true;
				}
				
				// ユーザーフィルタルート
				if (_vformMExecDefUserRoot != null && _vformMExecDefUserRoot.isDescendingFromBasePath(file)) {
					return true;
				}
				
				// システムフィルタルート
				if (_vformMExecDefSystemRoot != null && _vformMExecDefSystemRoot.isDescendingFromBasePath(file)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public String getPath(VirtualFile file) {
			return getPath(file, Files.CommonSeparatorChar);
		}

		@Override
		public String getPath(VirtualFile file, char separator) {
			String path = formatPath(file, separator);
			if (path == null) {
				return file.getPath();
			} else {
				return path;
			}
		}

		@Override
		public String formatPath(VirtualFile file) {
			return formatPath(file, Files.CommonSeparatorChar);
		}

		@Override
		public String formatPath(VirtualFile file, char separator) {
			String path = null;
			
			if (file != null) {
				// ユーザーデータファイルルート
				if (_vformDataFileUserRoot != null) {
					path = _vformDataFileUserRoot.formatPath(file, separator);
					if (path != null) {
						return path;
					}
				}
				
				// システムデータファイルルート
				if (_vformDataFileSystemRoot != null) {
					path = _vformDataFileSystemRoot.formatPath(file, separator);
					if (path != null) {
						return path;
					}
				}
				
				// ユーザーフィルタルート
				if (_vformMExecDefUserRoot != null) {
					path = _vformMExecDefUserRoot.formatPath(file, separator);
					if (path != null) {
						return path;
					}
					
				}
				
				// システムフィルタルート
				if (_vformMExecDefSystemRoot != null) {
					path = _vformMExecDefSystemRoot.formatPath(file, separator);
					if (path != null) {
						return path;
					}
				}
			}
			
			return path;
		}
	}
}
