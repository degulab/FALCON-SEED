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
 * @(#)EditorFrame.java	3.2.2	2015/10/15 (Bug fixed)
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)EditorFrame.java	1.22	2012/11/06
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)EditorFrame.java	1.21	2012/06/29 - update for Mac OS X
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)EditorFrame.java	1.19	2012/02/22 - bugfix
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)EditorFrame.java	1.17	2011/02/04
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)EditorFrame.java	1.16	2010/09/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)EditorFrame.java	1.14	2009/12/14
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)EditorFrame.java	1.10	2009/01/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
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
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.editor.AADLEditor;
import ssac.aadl.editor.EditorMessages;
import ssac.aadl.editor.build.ExecutorEvent;
import ssac.aadl.editor.build.ExecutorStoppedListener;
import ssac.aadl.editor.document.IEditorDocument;
import ssac.aadl.editor.document.JarModuleDocument;
import ssac.aadl.editor.plugin.IComponentManager;
import ssac.aadl.editor.plugin.PluginManager;
import ssac.aadl.editor.setting.AppSettings;
import ssac.aadl.editor.view.ActiveViewManager.ActiveViewChangeEvent;
import ssac.aadl.editor.view.ActiveViewManager.ActiveViewChangeListener;
import ssac.aadl.editor.view.console.AbstractMonitorPane;
import ssac.aadl.editor.view.console.CompileMonitorPane;
import ssac.aadl.editor.view.console.ProcessMonitorPane;
import ssac.aadl.editor.view.dialog.BuildOptionDialog;
import ssac.aadl.editor.view.dialog.CompileInDirDialog;
import ssac.aadl.editor.view.dialog.FileChooserManager;
import ssac.aadl.editor.view.dialog.FindDialog;
import ssac.aadl.editor.view.dialog.FindReplaceInterface;
import ssac.aadl.editor.view.dialog.PreferenceDialog;
import ssac.aadl.editor.view.dialog.WorkspaceChooser;
import ssac.aadl.editor.view.menu.EditorMenuResources;
import ssac.aadl.module.ModuleFileManager;
import ssac.aadl.module.setting.AbstractSettings;
import ssac.falconseed.common.FSEnvironment;
import ssac.util.Strings;
import ssac.util.Validations;
import ssac.util.io.DefaultFile;
import ssac.util.io.Files;
import ssac.util.io.VirtualFile;
import ssac.util.logging.AppLogger;
import ssac.util.mac.MacScreenMenuHandler;
import ssac.util.swing.FrameWindow;
import ssac.util.swing.IDialogResult;
import ssac.util.swing.StatusBar;
import ssac.util.swing.menu.IMenuActionHandler;
import ssac.util.swing.menu.IMenuHandler;
import ssac.util.swing.menu.JMenus;

/**
 * AADLエディタのメインフレーム用基底クラス。
 * <p>
 * このクラスは、AADLエディタのフレームワークを提供する。
 * 
 * @version 3.2.2
 * 
 * @since 1.10
 */
public class EditorFrame extends FrameWindow implements IMenuHandler, IMenuActionHandler, MacScreenMenuHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = -7820298136689157235L;

	static private final IEditorView[] EMPTY_EDITOR_ARRAY = new IEditorView[0];
	
	static private final Dimension DM_DEF_SIZE = new Dimension(800,600);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/**
	 * このフレームの標準メニューバー
	 */
	protected EditorMenuBar		defMainMenu;
	/**
	 * 現在アクティブなエディタ
	 */
	protected IEditorView			activeEditor;
	/**
	 * 現在アクティブなビューを管理するマネージャ
	 */
	protected final ActiveViewManager	_activeViewManager;
	
	//--- Events
	private final EditorModifiedChangeHandler	hEditorModified = new EditorModifiedChangeHandler();
	private final FileDropTargetListener hEditorFileDroped = new FileDropTargetListener();
	
	// Components
	private JSplitPane		viewSplitOuter;
	private JSplitPane		viewSplitInner;
	private EditorTreeView	viewTree;
	private JTabbedPane	tabEditor;
	private JTabbedPane	tabInfo;
	private StatusBar		statusBar;
	
	private ProcessMonitorPane	paneConsole;
	private CompileMonitorPane	paneBuild;
	
	private FindDialog		dlgFind;

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
	
	// State
	//--- for Build
	private boolean	compileAndRun = false;
//	//--- local state
//	private Point		lastFrameLocation;
//	private Dimension	lastFrameSize;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public EditorFrame() {
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
		
//		lastFrameLocation = null;
//		lastFrameSize = new Dimension();

		// setup Main menu
		defMainMenu = new EditorMenuBar(this);
		setEditorMenuBar(defMainMenu);
		
		// setup Views
		viewSplitOuter = createMainPanel();
		getContentPane().add(viewSplitOuter, BorderLayout.CENTER);
		
		// setup Status bar
		statusBar = new StatusBar();
		getContentPane().add(statusBar, BorderLayout.SOUTH);
		
		// setup default Workspace
		viewTree.initialSetup(this);
		//setActiveContextMenuToTeeView(getActiveEditorMenuBar());
		File wsRoot = AppSettings.getInstance().getLastWorkspace();
		try {
			if (!wsRoot.exists()) {
				String msg = "Workspace does not exists : \"" + wsRoot.toString() + "\"";
				AppLogger.fatal(msg);
				throw new AssertionError(msg);
			}
			if (!wsRoot.isDirectory()) {
				String msg = "Workspace is not directory : \"" + wsRoot.toString() + "\"";
				AppLogger.fatal(msg);
				throw new AssertionError(msg);
			}
			if (!wsRoot.canWrite()) {
				String msg = "Workspace directory cannot write : \"" + wsRoot.toString() + "\"";
				AppLogger.fatal(msg);
				throw new AssertionError(msg);
			}
			VirtualFile vfRoot = ModuleFileManager.fromJavaFile(wsRoot);
			if (ModuleFileManager.getTopProjectPrefsFile(vfRoot, null) != null) {
				String msg = "Workspace is Project root directory : \"" + wsRoot.toString() + "\"";
				AppLogger.fatal(msg);
				throw new AssertionError(msg);
			}
			if (ModuleFileManager.getTopModulePackagePrefsFile(vfRoot, null) != null) {
				String msg = "Workspace is Module package root directory : \"" + wsRoot.toString() + "\"";
				AppLogger.fatal(msg);
				throw new AssertionError(msg);
			}
		}
		catch (SecurityException ex) {
			String msg = "Workspace directory cannot access : \"" + wsRoot.toString() + "\"";
			AppLogger.fatal(msg, ex);
			throw new AssertionError(msg);
		}
		viewTree.setWorkspaceRoot(ModuleFileManager.fromJavaFile(wsRoot));
		
		// setup drop targets
		new DropTarget(this, DnDConstants.ACTION_COPY, hEditorFileDroped, true);
		
		// register views
		_activeViewManager.registerComponent(viewTree);
		_activeViewManager.registerComponent(tabEditor);
		_activeViewManager.registerComponent(tabInfo);
		
		// setup Window actions
		enableComponentEvents(true);
		enableWindowEvents(true);
		enableWindowStateEvents(true);
		setupComponentActions();
		
		// update all menu items
		updateAllMenuItems();
		
		// restore View's settings
		restoreSettings();
	}

	//------------------------------------------------------------
	// Components creation
	//------------------------------------------------------------
	
	private JSplitPane createMainPanel() {
		// create component
		JSplitPane paneOuter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		paneOuter.setResizeWeight(0);
		JSplitPane paneInner = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		paneInner.setResizeWeight(1);
		
		// create views
		this.viewTree  = createTreeView();
		this.tabEditor = createEditorTab();
		this.tabInfo   = createInfoTab();
		this.viewSplitInner = paneInner;
		
		// add to split
		paneInner.setTopComponent(this.tabEditor);
		paneInner.setBottomComponent(this.tabInfo);
		paneOuter.setLeftComponent(this.viewTree);
		paneOuter.setRightComponent(this.viewSplitInner);
		
		// completed
		return paneOuter;
	}
	
	private EditorTreeView createTreeView() {
		EditorTreeView tree = new EditorTreeView();
		return tree;
	}
	
	private JTabbedPane createEditorTab() {
		// create component
		JTabbedPane tab = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
		tab.setFocusable(false);

		// completed
		return tab;
	}
	
	private JTabbedPane createInfoTab() {
		// create component
		JTabbedPane tab = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
		tab.setFocusable(false);
		
		// create Info panes
		this.paneConsole = new ProcessMonitorPane();
		this.paneBuild = new CompileMonitorPane();
		
		// add to tabs
		tab.addTab(EditorMessages.getInstance().consoleTabTitleConsole, this.paneConsole);
		tab.addTab(EditorMessages.getInstance().consoleTabTitleCompile, this.paneBuild);

		// completed
		return tab;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 現在のワークスペースのルートを示す抽象パスを返す。
	 * @since 1.14
	 */
	public DefaultFile getCurrentWorkspace() {
		DefaultFile ws = null;
		if (viewTree != null) {
			ws = (DefaultFile)viewTree.getWorkspaceRoot();
		}
		return ws;
	}

	/**
	 * 指定されたファイルが読み込み専用かを判定する。
	 * ファイルそのものが読み書き可能な場合でも、モジュールパッケージに
	 * 含まれるファイルの場合は読み込み専用とする。
	 * @param file	判定するファイル
	 * @return	指定されたファイルが読み込み専用であれば <tt>true</tt>、
	 * 			そうでない場合は <tt>false</tt> を返す。
	 * 			<em>file</em> が <tt>null</tt> の場合は <tt>false</tt> を返す。
	 * @since 1.14
	 */
	public boolean isReadOnlyFile(VirtualFile file) {
		if (file == null)
			return false;
		
		if (ModuleFileManager.getTopModulePackagePrefsFile(file, getCurrentWorkspace()) != null) {
			// モジュールパッケージに含まれる抽象パスなら、読み取り専用
			return true;
		} else if (file.exists() && !file.canWrite()) {
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
	 * @since 1.14
	 */
	public boolean isReadOnlyFile(File javaFile) {
		VirtualFile file = (javaFile==null ? (VirtualFile)null : ModuleFileManager.fromJavaFile(javaFile));
		return isReadOnlyFile(file);
	}

	/**
	 * 現在表示されているすべてのエディタを取得する。
	 * @return	表示されているすべてのエディタオブジェクトを格納する配列を返す。
	 * 			エディタが一つも表示されていない場合は空の配列を返す。
	 * @since 1.14
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
	 * @since 1.14
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
	 * @since 1.14
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
	 * @since 1.14
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
		return this.tabEditor.getTabCount();
	}

	/**
	 * 指定されたインデックスのタブが保持するエディタを取得する。
	 * @param index	エディタタブのインデックス
	 * @return	エディタのインスタンス
	 */
	private IEditorView getEditor(int index) {
		return (IEditorView)tabEditor.getComponentAt(index);
	}

	/**
	 * このフレームで、現在アクティブなビューを取得する。
	 * @return	現在アクティブなビューを返す。アクティブなビューが存在しない場合は
	 * 			<tt>null</tt> を返す。
	 */
	public IEditorView getActiveEditor() {
		return activeEditor;
	}

	/**
	 * このフレームで、現在アクティブな情報ペイン(モニターペイン)を取得する。
	 * 情報ペインは「コンソール」と「コンパイル」のどちらかが常に表示されている。
	 * @return	現在アクティブな情報ペインを返す。
	 */
	public AbstractMonitorPane getActiveMonitor() {
		return (AbstractMonitorPane)tabInfo.getSelectedComponent();
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
	 * @since 1.14
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
		tabEditor.removeTabAt(editorTabIndex);
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
	 * @since 1.14
	 */
	public boolean closeEditorByEditor(boolean onlyNotExists, boolean withSave, IEditorView editor) {
		int editorTabIndex = tabEditor.indexOfComponent((Component)editor);
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
	 * @since 1.14
	 */
	public void renameToEditorFile(int editorTabIndex, File newFile) {
		if (newFile == null)
			throw new IllegalArgumentException("'newFile' argument is null.");
		IEditorView editor = getEditor(editorTabIndex);
		editor.getDocument().setTargetFile(newFile);
		updateDisplayEditorTab(editorTabIndex);
		int selidx = tabEditor.getSelectedIndex();
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
	 * @since 1.14
	 */
	public void renameToEditorFile(IEditorView editor, File newFile) {
		int editorTabIndex = tabEditor.indexOfComponent((Component)editor);
		if (editorTabIndex < 0)
			throw new IllegalArgumentException("editor argument does not exist in tab.");
		renameToEditorFile(editorTabIndex, newFile);
	}

	/**
	 * このフレームに設定されているステータスバーを取得する。
	 * @return	ステータスバー
	 */
	public StatusBar getStatusBar() {
		return statusBar;
	}

	/**
	 * 標準メニューバーを返す。このメソッドが返すメニューバーは、
	 * 現在アクティブなメニューバーではない場合もある。
	 * 
	 * @return	標準メニューバー
	 */
	public EditorMenuBar getDefaultEditorMenuBar() {
		return defMainMenu;
	}
	
	/**
	 * アクティブなエディタメニューバーを返す。
	 * 基本的に、このフレームに現在設定されているメニューバーで
	 * <code>EditorMenuBar</code> インスタンスである場合に、
	 * そのインスタンスを返す。この条件に当てはまらない場合は <tt>null</tt> を返す。
	 * @return	アクティブなエディタメニューバー
	 */
	public EditorMenuBar getActiveEditorMenuBar() {
		JMenuBar bar = getJMenuBar();
		if (bar instanceof EditorMenuBar)
			return ((EditorMenuBar)bar);
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
		EditorMenuBar bar = getActiveEditorMenuBar();
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
		EditorMenuBar bar = getActiveEditorMenuBar();
		if (bar != null) {
			return bar.getMenuAction(command);
		}
		return null;
	}

	/*
	 * ツリービューに、現在アクティブなメニューバーの
	 * ツリービュー用コンテキストメニューを設定する。
	 * @since 1.14
	 *
	public void setActiveContextMenuToTeeView(EditorMenuBar targetMenuBar) {
		if (viewTree != null && targetMenuBar != null) {
			viewTree.setTreeComponentPopupMenu(targetMenuBar.getTreeViewContextMenu());
			viewTree.setWorkspaceLabelPopupMenu(targetMenuBar.getTreeHeaderContextMenu());
		}
	}
	*/

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
		EditorMenuBar bar = getActiveEditorMenuBar();
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
		EditorMenuBar bar = getActiveEditorMenuBar();
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
		EditorMenuBar bar = getActiveEditorMenuBar();
		if (bar != null) {
			bar.updateAllMenuItems();
		}
	}

	/**
	 * 指定されたファイルをエディタで開く。
	 * 基本的にサポートされていないファイルは開かない。
	 * すでに開かれているファイルの場合は、そのエディタにフォーカスを設定して <tt>true</tt> を返す。
	 * @param targetFile	開くファイル
	 * @return	エディタで開いた場合は <tt>true</tt>、そうでない場合は <tt>false</tt> を返す。
	 * 			すでに開かれているファイルの場合は <tt>true</tt> を返す。
	 * @since 1.14
	 */
	public boolean openEditorFromFile(File targetFile) {
		// exist opened?
		for (int i = 0; i < getEditorCount(); i++) {
			IEditorView editor = getEditor(i);
			File editorFile = editor.getDocumentFile();
			if (editorFile != null && editorFile.equals(targetFile)) {
				// すでに存在している場合は、そこにフォーカスを設定して終了
				tabEditor.setSelectedIndex(i);
				setFocusToActiveEditor();
				return true;
			}
		}
		
		// find plugin
		IComponentManager manager = PluginManager.findSupportedPlugin(targetFile);
		if (manager == null) {
			// このファイルはサポートされていない
			String errmsg = String.format(EditorMessages.getInstance().msgUnsupportedDocument, targetFile.getAbsolutePath());
			AppLogger.error(errmsg);
			AADLEditor.showErrorMessage(this, errmsg);
			setFocusToActiveView();
			return false;	// not supported
		}
		
		// open document
		IEditorView newEditor = null;
		try {
			newEditor = manager.openDocument(this, targetFile);
		}
		catch (FileNotFoundException ex) {
			String errmsg = EditorMessages.getErrorMessage(EditorMessages.MessageID.ERR_FILE_NOTFOUND, ex, targetFile.getAbsolutePath());
			AppLogger.error(errmsg, ex);
			AADLEditor.showErrorMessage(this, errmsg);
		}
		catch (UnsupportedEncodingException ex) {
			String errmsg = EditorMessages.getErrorMessage(EditorMessages.MessageID.ERR_FILE_UNSUPPORTED_ENCODING, ex);
			AppLogger.error(errmsg, ex);
			AADLEditor.showErrorMessage(this, errmsg);
		}
		catch (IOException ex) {
			String errmsg = EditorMessages.getErrorMessage(EditorMessages.MessageID.ERR_FILE_READ, ex, targetFile.getAbsolutePath());
			AppLogger.error(errmsg, ex);
			AADLEditor.showErrorMessage(this, errmsg);
		}
		catch (OutOfMemoryError ex) {
			String errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
			AppLogger.error(errmsg, ex);
			AADLEditor.showErrorMessage(this, errmsg);
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
	 * ツリービューのモジュール情報パネルの内容を、
	 * 最新の情報に更新する。
	 */
	public void refreshModuleProperties() {
		viewTree.refreshModuleProperties();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

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
		int ret = JOptionPane.showConfirmDialog(this,
									EditorMessages.getInstance().confirmFileChangedReplace,
									editor.getDocumentTitle(), JOptionPane.YES_NO_OPTION);
		setEnableRefreshEditorDocumentWhenUpdating(true);
		if (ret != JOptionPane.YES_OPTION) {
			// user canceled
			//--- 再読込はキャンセルされたため、最新の最終更新日時を保存
			refreshEditorSourceLastModifiedTime(editor);
			return false;
		}
		
		// ドキュメントの再読込
		setEnableRefreshEditorDocumentWhenUpdating(false);
		boolean result = false;
		try {
			editor.refreshDocumentFromSourceFile();
			result = true;
		}
		catch (FileNotFoundException ex) {
			String errmsg = EditorMessages.getErrorMessage(EditorMessages.MessageID.ERR_FILE_NOTFOUND, ex, editor.getDocumentFile().getAbsolutePath());
			AppLogger.error(errmsg, ex);
			AADLEditor.showErrorMessage(this, errmsg);
		}
		catch (UnsupportedEncodingException ex) {
			String errmsg = EditorMessages.getErrorMessage(EditorMessages.MessageID.ERR_FILE_UNSUPPORTED_ENCODING, ex);
			AppLogger.error(errmsg, ex);
			AADLEditor.showErrorMessage(this, errmsg);
		}
		catch (IOException ex) {
			String errmsg = EditorMessages.getErrorMessage(EditorMessages.MessageID.ERR_FILE_READ, ex, editor.getDocumentFile().getAbsolutePath());
			AppLogger.error(errmsg, ex);
			AADLEditor.showErrorMessage(this, errmsg);
		}
		catch (OutOfMemoryError ex) {
			String errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
			AppLogger.error(errmsg, ex);
			AADLEditor.showErrorMessage(this, errmsg);
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
									EditorMessages.getInstance().confirmUpdateConflictReplace,
									EditorMessages.getInstance().confirmTitle_UpdateConflictReplace, JOptionPane.YES_NO_OPTION);
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
	private void setEditorMenuBar(EditorMenuBar menuBar) {
		Validations.validNotNull(menuBar);
		JMenuBar oldBar = getJMenuBar();
		
		// 新しいメニューバーがすでに設定済みなら、処理しない
		if (oldBar == menuBar)
			return;
		
		// ツリービューに新しいコンテキストメニューを設定する
		//setActiveContextMenuToTeeView(menuBar);

		// 古いツールバーを削除
		if (oldBar instanceof EditorMenuBar) {
			JToolBar oldToolBar = ((EditorMenuBar)oldBar).getMainToolBar();
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
//			lastFrameSize.setSize(mainSize);
//		} else {
//			lastFrameSize.setSize(800, 600);
//		}
//		this.setSize(lastFrameSize);
//		//--- Window location
//		if (mainLoc != null) {
//			lastFrameLocation = new Point(mainLoc);
//			this.setLocation(mainLoc);
//		} else {
//			// default location
//			lastFrameLocation = null;
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
		viewSplitOuter.setDividerLocation(divloc);
		//--- Inner Divider location
		divloc = settings.getDividerLocation(AppSettings.INNER_FRAME);
		if (divloc < 0) {
			// default location
			Dimension dm = getSize();
			if (dm.height < 50) {
				dm = this.getSize();
			}
			divloc = dm.height / 3 * 2;
		}
		viewSplitInner.setDividerLocation(divloc);
		//--- Property Divider location
		divloc = settings.getDividerLocation("hogehoge");
		if (divloc < 0) {
			divloc = Integer.MAX_VALUE;
		}
		viewTree.setDividerLocation(divloc);
		
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
//		settings.setWindowLocation(AppSettings.MAINFRAME, this.lastFrameLocation);
//		settings.setWindowSize(AppSettings.MAINFRAME, this.lastFrameSize);
		// Views states
		settings.setDividerLocation(AppSettings.OUTER_FRAME, viewSplitOuter.getDividerLocation());
		settings.setDividerLocation(AppSettings.INNER_FRAME, viewSplitInner.getDividerLocation());
	}
	
	/**
	 * メインフレームをクローズ可能か検証する。
	 * 
	 * @return クローズ可能なら <tt>true</tt> を返す。
	 */
	@Override
	protected boolean canCloseWindow() {
		AppLogger.debug("Can close window?");
		
		// 全てのドキュメントを閉じる
		if (!closeAllEditors()) {
			// user Canceled
			AppLogger.debug("--- user canceled!");
			return false;
		}
		AppLogger.debug("--- close application");
		
		// 検索ダイアログを破棄
		if (dlgFind != null) {
			dlgFind.close();
		}
		
		// save preferences
		storeSettings();
		
		return true;
	}
	
	/**
	 * コンパイルが停止していることを確認する。
	 * 停止していなければ、エラーメッセージを表示する。
	 * 
	 * @return コンパイルが既に停止していれば true を返す。
	 */
	private boolean checkStoppedCompile() {
		boolean isRunning = paneBuild.isRunning();
		if (isRunning) {
			String msg = EditorMessages.getInstance().msgNowCompiling;
			AppLogger.warn(msg);
			AADLEditor.showErrorMessage(this, msg);
		}
		return (!isRunning);
	}
	
	/**
	 * Jar実行が停止していることを確認する。
	 * 停止していなければ、エラーメッセージを表示する。
	 * 
	 * @return 実行が既に停止していれば true を返す。
	 */
	private boolean checkStoppedExecute() {
		boolean isRunning = paneConsole.isRunning();
		if (isRunning) {
			String msg = EditorMessages.getInstance().msgNowExecuting;
			AppLogger.warn(msg);
			AADLEditor.showErrorMessage(this, msg);
		}
		return (!isRunning);
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
					EditorMessages.getInstance().confirmSaveChanges,
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
					EditorMessages.getInstance().msgCannotWriteCauseReadOnly,
					document.getTargetFile().getName());
			AppLogger.error("EditorFrame#saveEditorDocument(\"" + document.getTargetFile() + "\") : " + errmsg);
			AADLEditor.showWarningMessage(this, errmsg);
			return false;
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
		String strLastPath = AppSettings.getInstance().getLastFilename(AppSettings.DOCUMENT);
		File initFile = (Strings.isNullOrEmpty(strLastPath) ? null : new File(strLastPath));
		manager.setLastSelectedFile(initFile);
		
		boolean result = manager.onSaveAsComponent(this, editor);
		if (result) {
			// 保存成功
			removeEditorSourceLastModifiedTime(editor);
		} else {
			// 保存失敗
			refreshEditorSourceLastModifiedTime(editor);
		}
		
		File lastFile = manager.getLastSelectedFile();
		if (lastFile != null && !lastFile.equals(initFile)) {
			AppSettings.getInstance().setLastFilename(AppSettings.DOCUMENT,
					lastFile.getAbsolutePath());
		}
		
		return result;
	}
	
	/**
	 * 指定のエディタのドキュメントをコンパイルする。
	 * 変更が保存されていないドキュメントを含む場合は、ユーザーに
	 * 確認するダイアログを表示する。
	 * 
	 * @param editor 対象のドキュメントを保持するエディタ
	 * @return コンパイルが開始された場合に <tt>true</tt> を返す。
	 */
	private boolean compileEditorDocument(final IEditorView editor) {
		// 実行状態チェック
		if (!checkStoppedCompile() || !editor.getDocument().isCompilable()) {
			return false;
		}
		
		// 保存状態のチェック
		if (editor.isModified()) {
			int ret;
			if (editor.getDocumentFile() != null) {
				ret = JOptionPane.showConfirmDialog(this,
						EditorMessages.getInstance().confirmSaveChanges,
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
			} else {
				ret = JOptionPane.showConfirmDialog(this,
						EditorMessages.getInstance().confirmSaveDocument,
						editor.getDocumentTitle(),
						JOptionPane.OK_CANCEL_OPTION);
				if (ret != JOptionPane.OK_OPTION) {
					// user canceled
					return false;
				}
				if (!saveAsEditorDocument(editor)) {
					// user canceled
					return false;
				}
			}
		}
		
		// 保存ファイルのコンパイル
		editor.refreshDocumentSettings();
		paneBuild.setTargetDocument(editor.getDocument());
		tabInfo.setSelectedComponent(paneBuild);
		paneBuild.start();
		return true;
	}

	/**
	 * 指定のエディタのドキュメントから、実行モジュールの実行を開始する。
	 * 実行モジュールが存在しない場合はエラーとなる。
	 * 
	 * @param editor 実行対象のドキュメントを保持するエディタ
	 * @return 実行が開始された場合に <tt>true</tt> を返す。
	 */
	private boolean execEditorDocument(final IEditorView editor) {
		editor.refreshDocumentSettings();
		return execByDocument(editor.getDocument());
	}

	/**
	 * ドキュメントモデルに関連付けられている実行モジュールの実行を
	 * 開始する。実行モジュールが存在しない場合はエラーとなる。
	 * 
	 * @param document 実行対象の実行モジュールが関連付けられているドキュメント
	 * @return 実行が開始された場合に <tt>true</tt> を返す。
	 */
	private boolean execByDocument(final IEditorDocument document) {
		// 実行状態のチェック
		if (document == null || !document.isExecutable() || !checkStoppedExecute()) {
			return false;
		}
		
		// 状態チェック
		File docFile = document.getTargetFile();
		File exeFile = document.getExecutableFile();
		//--- 実行可能ファイルの有無
		if (docFile == null || exeFile == null || !exeFile.isFile()) {
			if (document.isCompilable()) {
				// compile not yet!
				AADLEditor.showMessageBox(this, document.getTitle(),
						EditorMessages.getInstance().msgCompileNotYet,
						JOptionPane.ERROR_MESSAGE);
			} else {
				// file not found
				AADLEditor.showMessageBox(this, document.getTitle(),
						EditorMessages.getInstance().msgExecutableFileNotFound,
						JOptionPane.ERROR_MESSAGE);
			}
			return false;
		}
		//--- ドキュメントファイルの反映
		if (document.isModified()) {
			int ret = JOptionPane.showConfirmDialog(this,
					EditorMessages.getInstance().confirmExecutePrevious,
					document.getTitle(), JOptionPane.OK_CANCEL_OPTION);
			if (ret != JOptionPane.OK_OPTION) {
				// user canceled
				return false;
			}
		}
		if (document.isCompilable() && docFile.lastModified() > exeFile.lastModified()) {
			int ret = JOptionPane.showConfirmDialog(this,
					EditorMessages.getInstance().confirmExecutePrevious,
					document.getTitle(), JOptionPane.OK_CANCEL_OPTION);
			if (ret != JOptionPane.OK_OPTION) {
				// user canceled
				return false;
			}
		}
		
		// 現在の状態で実行
		paneConsole.setTargetDocument(document);
		//paneConsole.setExecutionSettings(settings);
		tabInfo.setSelectedComponent(paneConsole);
		paneConsole.start();
		return true;
	}

	/**
	 * 任意の実行可能モジュールの実行を開始する。
	 * 実行モジュールはファイルダイアログにより選択する。
	 * 
	 * @return 実行が開始された場合に <tt>true</tt> を返す。
	 */
	private boolean runAsJarFile() {
		// 実行状態のチェック
		if (!checkStoppedExecute()) {
			return false;
		}
		
		// ファイル選択
		String strLastPath = AppSettings.getInstance().getLastFilename(AppSettings.JARMODULE);
		File lastFile = (Strings.isNullOrEmpty(strLastPath) ? null : new File(strLastPath));
		if (lastFile == null) {
			strLastPath = AppSettings.getInstance().getLastFilename(AppSettings.DOCUMENT);
			lastFile = (Strings.isNullOrEmpty(strLastPath) ? null : new File(strLastPath));
		}
		File jarFile = FileChooserManager.chooseJarFile(this, lastFile,
							EditorMessages.getInstance().chooserTitleRunAsJar);
		if (jarFile == null) {
			// not selected
			return false;
		}
		AppSettings.getInstance().setLastFilename(AppSettings.JARMODULE, jarFile.getAbsolutePath());
		
		// Jarモジュール用ドキュメント生成
		JarModuleDocument jarDocument = new JarModuleDocument(jarFile);
		
		// 設定ダイアログ表示
		AbstractSettings setting = jarDocument.getSettings();
		if (setting != null) {
			setting.refresh();
		}
		BuildOptionDialog dlg = new BuildOptionDialog(setting,
										isReadOnlyFile(jarFile),
										this);
		dlg.setVisible(true);
		int dlgResult = dlg.getDialogResult();
		AppLogger.debug("Dialog result : " + dlgResult);
		if (dlgResult != IDialogResult.DialogResult_OK) {
			// user canceled
			return false;
		}
		
		// 実行開始
		// 現在の状態で実行
		paneConsole.setTargetDocument(jarDocument);
		//paneConsole.setExecutionSettings(jarSettings);
		tabInfo.setSelectedComponent(paneConsole);
		paneConsole.start();
		return true;
	}

	/**
	 * 指定されたフォルダに含まれるAADLファイルを全てコンパイルする。
	 * フォルダはダイアログにより選択する。
	 * 
	 * @return 実行が開始された場合に <tt>true</tt> を返す。
	 */
	private boolean compileAllInFolder() {
		// 実行状態のチェック
		if (!checkStoppedExecute()) {
			return false;
		}
		
		// コンパイル実行ダイアログ表示
		CompileInDirDialog dlg = new CompileInDirDialog(this);
		dlg.setVisible(true);
		
		return true;
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
				title = EditorMessages.getInstance().editingDocumentModifier + editor.getDocumentTitle();
			} else {
				title = editor.getDocumentTitle();
			}
		} else {
			title = null;
		}
		return title;
	}

	/**
	 * アクティブなビューのタイトルを含めた、このフレームの
	 * タイトル文字列を返す。
	 * @return	フレームのタイトル文字列
	 */
	private String getFrameTitle(boolean isReadOnly) {
		String title = EditorMessages.getInstance().appMainTitle;
		if (activeEditor != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(activeEditor.getDocumentTitle());
			sb.append("[");
			sb.append(activeEditor.getLastEncodingName());
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
		//editor.setPopupMenu(pmenuForEditor);
		String tabTitle = getEditorTabTitle(editor);
		String tabTooltip = editor.getDocumentPath();
		if (tabTooltip != null)
			tabEditor.addTab(tabTitle, null, editor.getComponent(), tabTooltip);
		else
			tabEditor.addTab(tabTitle, editor.getComponent());
		tabEditor.setSelectedComponent(editor.getComponent());
		//editor.jumpToBegin();
		//editor.getTextComponent().setStatusBar(statusBar);
		//editor.requestFocusInTextComponent();
		editor.requestFocusInComponent();
	}

	/**
	 * 現在アクティブなエディタの情報を更新する。
	 * 更新する情報は、アプリケーションタイトル、タブのタイトル、タブのツールチップとなる。
	 */
	private void updateDisplayCurrentTab() {
		int selidx = tabEditor.getSelectedIndex();
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
     * @since 1.14
	 */
	private void updateDisplayEditorTab(int tabIndex) {
		final IEditorView editor = getEditor(tabIndex);
		String tabTitle = getEditorTabTitle(editor);
		String tabTooltip = editor.getDocumentPath();
		tabEditor.setTitleAt(tabIndex, tabTitle);
		tabEditor.setToolTipTextAt(tabIndex, tabTooltip);
	}
	
	private void attachEditorModifiedPropertyChangeHandler(IEditorView editor) {
		editor.getComponent().addPropertyChangeListener(IEditorView.PROP_MODIFIED, hEditorModified);
	}
	
	private void detachEditorModifiedPropertyChangeHandler(IEditorView editor) {
		editor.getComponent().removePropertyChangeListener(IEditorView.PROP_MODIFIED, hEditorModified);
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
			detachEditorModifiedPropertyChangeHandler(editor);
			editor.getManager().removeDocument(editor);
			tabEditor.removeTabAt(tabIndex);
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
		for (int i = tabEditor.getTabCount() - 1; i >= 0; i--) {
			final IEditorView editor = getEditor(i);
			if (!editor.isModified()) {
				// 更新されていないので、すぐに閉じる
				detachEditorModifiedPropertyChangeHandler(editor);
				editor.getManager().removeDocument(editor);
				tabEditor.removeTabAt(i);
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
		while (tabEditor.getTabCount() > 0) {
			tabEditor.setSelectedIndex(0);
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

	/*
	 * 情報タブのビューを取得する。
	 * @param tabIndex	取得するビューの情報タブ位置
	 * @return	指定された位置にあるビューを返す。
	 * ※今は使用しない
	 *
	private AbstractMonitorPane getInfoPane(int tabIndex) {
		return (AbstractMonitorPane)tabInfo.getComponentAt(tabIndex);
	}
	*---*/

	/*
	 * 情報タブのアクティブなビューを取得する。
	 * @return	情報タブのアクティブなビュー
	 * ※今は使用しない
	 *
	private AbstractMonitorPane getActiveInfoPane() {
		int selidx = tabInfo.getSelectedIndex();
		if (selidx >= 0) {
			return getInfoPane(selidx);
		} else {
			return null;
		}
	}
	*---*/
	
	/**
	 * 現在アクティブなビューにフォーカスを設定する。
	 * フォーカスが設定されるコンポーネントは、ビューの主要コンポーネントとなる。
	 * @since 1.14
	 */
	private void setFocusToActiveView() {
		if (_activeViewManager.isComponentActivated(viewTree)) {
			// ツリービューがアクティブ
			viewTree.requestFocusInComponent();
		}
		else if (_activeViewManager.isComponentActivated(tabEditor)) {
			// エディタビューがアクティブ
			setFocusToActiveEditor();
		}
		else if (_activeViewManager.isComponentActivated(tabInfo)) {
			// 情報ビューがアクティブ
			tabInfo.requestFocusInWindow();
		}
	}

	/**
	 * 現在アクティブなエディタコンポーネントにフォーカスを設定する。
	 */
	private void setFocusToActiveEditor() {
		IEditorView editor = getActiveEditor();
		if (editor != null) {
			editor.requestFocusInComponent();
		}
	}

	/**
	 * 現在アクティブな情報タブのコンソールコンポーネントにフォーカスを設定する。
	 *
	 */
	private void setFocusToActiveConsole() {
		Component comp = tabInfo.getSelectedComponent();
		if (comp instanceof AbstractMonitorPane) {
			((AbstractMonitorPane)comp).requestFocusInTextComponent();
		}
	}

	/**
	 * アプリケーション設定により、ビューの各フォントを更新する。
	 */
	private void updateFontBySettings() {
		updateEditorFont();
		updateConsoleFont();
		updateCompileFont();
	}

	/**
	 * 各プラグインのエディタフォントをアプリケーション設定に従い更新する。
	 */
	private void updateEditorFont() {
		// 現時点では、AADLソースエディタのフォントのみ更新
		Font font = AppSettings.getInstance().getFont(AppSettings.EDITOR);
		if (font != null) {
			PluginManager.getDefaultPlugin().setEditorFont(font);
		}
	}

	/**
	 * コンソールの表示フォントをアプリケーション設定に従い更新する。
	 */
	private void updateConsoleFont() {
		Font font = AppSettings.getInstance().getFont(AppSettings.CONSOLE);
		Font oldFont = paneConsole.getTextComponent().getFont();
		if (font != null && (oldFont == null || !oldFont.equals(font))) {
			// update font
			paneConsole.getTextComponent().setFont(font);
		}
	}

	/**
	 * コンパイルメッセージ表示フォントをアプリケーション設定に従い更新する。
	 */
	private void updateCompileFont() {
		Font font = AppSettings.getInstance().getFont(AppSettings.COMPILE);
		Font oldFont = paneBuild.getTextComponent().getFont();
		if (font != null && (oldFont == null || !oldFont.equals(font))) {
			// update font
			paneBuild.getTextComponent().setFont(font);
		}
	}
	
	private void showFindReplaceDialog(FindReplaceInterface findHandler) {
		if (dlgFind == null) {
			dlgFind = new FindDialog(this, findHandler);
		} else {
			dlgFind.setHandler(findHandler);
		}
		dlgFind.setVisible(true);
	}
	
	private void updateFindReplaceDialogHandler() {
		if (dlgFind != null && dlgFind.isVisible()) {
			IEditorView editor = getActiveEditor();
			if (editor != null)
				dlgFind.setHandler(editor.getFindReplaceHandler());
			else
				dlgFind.setHandler(null);
		}
	}

	/**
	 * 現在のタブ選択状態により、アクティブなエディタを更新する。
	 */
	private void updateActiveEditor() {
		EditorMenuBar menuBar;
		int selidx = tabEditor.getSelectedIndex();
		if (selidx >= 0) {
			activeEditor = getEditor(selidx);
			menuBar = activeEditor.getDocumentMenuBar();
		} else {
			activeEditor = null;
			menuBar = null;
		}
		EditorFrame.this.setTitle(getFrameTitle(activeEditor==null ? false : activeEditor.isReadOnly()));
		setEditorMenuBar(menuBar==null ? defMainMenu : menuBar);
		updateFindReplaceDialogHandler();
		updateAllMenuItems();
		if (activeEditor != null) {
			activeEditor.requestFocusInComponent();
		} else {
			// ビューが一つも存在しない場合は、ツリーにフォーカスを移す
			viewTree.requestFocusInComponent();
		}
		
		if (AppLogger.isDebugEnabled()) {
			String editorName;
			if (activeEditor != null) {
				editorName = activeEditor.getDocumentPath();
				if (Strings.isNullOrEmpty(editorName))
					editorName = activeEditor.getDocumentTitle();
			} else {
				editorName = "null";
			}
//			AppLogger.debug(String.format("EditorFrame#updateActiveEditor : TabIndex[%d] / activeEditor[%s]", selidx, editorName));
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
		sb.append(EditorMessages.getInstance().confirmSaveDocumentBeforeOperation);
		for (IEditorView ev : editors) {
			String et = ev.getDocumentTitle();
			sb.append("\n    '");
			sb.append(et);
			sb.append("'");
		}
		sb.append("\n" + EditorMessages.getInstance().confirmSaveChanges);
		int ret = JOptionPane.showConfirmDialog(this, sb.toString(),
				EditorMessages.getInstance().confirmTitleSaveModifiedDocuments,
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
		if (command.startsWith(EditorMenuResources.ID_FILE_MENU)) {
			if (command.startsWith(EditorMenuResources.ID_FILE_NEW_PREFIX)) {
				if (EditorMenuResources.ID_FILE_NEW_PROJECT.equals(command)) {
					onSelectedMenuFileNewProject(action);
					return true;
				}
				if (EditorMenuResources.ID_FILE_NEW_FOLDER.equals(command)) {
					onSelectedMenuFileNewFolder(action);
					return true;
				}
				onSelectedMenuFileNew(command, action);
				return true;
			}
			else if (EditorMenuResources.ID_FILE_OPEN.equals(command)) {
				onSelectedMenuFileOpen(action);
				return true;
			}
			else if (command.startsWith(EditorMenuResources.ID_FILE_REOPEN_PREFIX)) {
				onSelectedMenuFileReopen(command, action);
				return true;
			}
			else if (EditorMenuResources.ID_FILE_CLOSE.equals(command)) {
				onSelectedMenuFileClose(action);
				return true;
			}
			else if (EditorMenuResources.ID_FILE_ALL_CLOSE.equals(command)) {
				onSelectedMenuFileAllClose(action);
				return true;
			}
			else if (EditorMenuResources.ID_FILE_SAVE.equals(command)) {
				onSelectedMenuFileSave(action);
				return true;
			}
			else if (EditorMenuResources.ID_FILE_SAVEAS.equals(command)) {
				onSelectedMenuFileSaveAs(action);
				return true;
			}
			/*
			else if (EditorMenuResources.ID_FILE_COPYTO.equals(command)) {
				onSelectedMenuFileCopyTo(action);
				return true;
			}
			*/
			else if (EditorMenuResources.ID_FILE_MOVETO.equals(command)) {
				onSelectedMenuFileMoveTo(action);
				return true;
			}
			else if (EditorMenuResources.ID_FILE_RENAME.equals(command)) {
				onSelectedMenuFileRename(action);
				return true;
			}
			else if (EditorMenuResources.ID_FILE_REFRESH.equals(command)) {
				onSelectedMenuFileRefresh(action);
				return true;
			}
			else if (EditorMenuResources.ID_FILE_WS_REFRESH.equals(command)) {
				onSelectedMenuFileWorkspaceRefresh(action);
				return true;
			}
			else if (EditorMenuResources.ID_FILE_PREFERENCE.equals(command)) {
				onSelectedMenuFilePreference(action);
				return true;
			}
			else if (EditorMenuResources.ID_FILE_WS_SELECT.equals(command)) {
				onSelectedMenuFileWorkspaceSelect(action);
				return true;
			}
			else if (EditorMenuResources.ID_FILE_QUIT.equals(command)) {
				onSelectedMenuFileQuit(action);
				return true;
			}
		}
		else if (command.startsWith(EditorMenuResources.ID_FIND_MENU)) {
			if (EditorMenuResources.ID_FIND_FIND.equals(command)) {
				onSelectedMenuFindFind(action);
				return true;
			}
			else if (EditorMenuResources.ID_FIND_PREV.equals(command)) {
				onSelectedMenuFindPrev(action);
				return true;
			}
			else if (EditorMenuResources.ID_FIND_NEXT.equals(command)) {
				onSelectedMenuFindNext(action);
				return true;
			}
		}
		else if (command.startsWith(EditorMenuResources.ID_BUILD_MENU)) {
			if (EditorMenuResources.ID_BUILD_COMPILE.equals(command)) {
				onSelectedMenuBuildCompile(action);
				return true;
			}
			else if (EditorMenuResources.ID_BUILD_RUN.equals(command)) {
				onSelectedMenuBuildRun(action);
				return true;
			}
			else if (EditorMenuResources.ID_BUILD_COMPILE_RUN.equals(command)) {
				onSelectedMenuBuildCompileRun(action);
				return true;
			}
			else if (EditorMenuResources.ID_BUILD_RUNASJAR.equals(command)) {
				onSelectedMenuBuildRunAsJar(action);
				return true;
			}
			else if (EditorMenuResources.ID_BUILD_COMPILEINDIR.equals(command)) {
				onSelectedMenuBuildCompileAllInFolder(action);
				return true;
			}
			else if (EditorMenuResources.ID_BUILD_OPTION.equals(command)) {
				onSelectedMenuBuildOption(action);
				return true;
			}
		}
		else if (EditorMenuResources.ID_HELP_ABOUT.equals(command)) {
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
		if (command.startsWith(EditorMenuResources.ID_FILE_MENU)) {
			if (EditorMenuResources.ID_FILE_NEW_PROJECT.equals(command)) {
				action.setEnabled(true);
			}
			else if (command.startsWith(EditorMenuResources.ID_FILE_NEW)) {
				action.setEnabled(viewTree.canCreate());
			}
			else if (EditorMenuResources.ID_FILE_OPEN.equals(command)) {
				action.setEnabled(true);
			}
			else if (command.startsWith(EditorMenuResources.ID_FILE_REOPEN)) {
				IEditorView view = getActiveEditor();
				boolean toEnable;
				if (view != null && view.canReopen()) {
					toEnable = true;
				} else {
					toEnable = false;
				}
				EditorMenuBar embar = getActiveEditorMenuBar();
				if (embar != null) {
					JMenuItem item = JMenus.getMenuItemByCommand(embar, EditorMenuResources.ID_FILE_REOPEN);
					if (item != null) {
						if (item.isEnabled() != toEnable) {
							item.setEnabled(toEnable);
						}
						item = JMenus.getMenuItemByCommand(embar.getEditorTabContextMenu(),
															EditorMenuResources.ID_FILE_REOPEN);
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
			else if (EditorMenuResources.ID_FILE_CLOSE.equals(command)) {
				action.setEnabled(getActiveEditor()!=null);
			}
			else if (EditorMenuResources.ID_FILE_ALL_CLOSE.equals(command)) {
				action.setEnabled(!isEditorEmpty());
			}
			else if (EditorMenuResources.ID_FILE_SAVE.equals(command)) {
				action.setEnabled(false);
			}
			else if (EditorMenuResources.ID_FILE_SAVEAS.equals(command)) {
				action.setEnabled(false);
			}
			/*
			else if (EditorMenuResources.ID_FILE_COPYTO.equals(command)) {
				action.setEnabled(viewTree.canCopy());
			}
			*/
			else if (EditorMenuResources.ID_FILE_MOVETO.equals(command)) {
				action.setEnabled(false);
			}
			else if (EditorMenuResources.ID_FILE_RENAME.equals(command)) {
				action.setEnabled(false);
			}
			else if (EditorMenuResources.ID_FILE_REFRESH.equals(command)) {
				action.setEnabled(true);
			}
			else if (EditorMenuResources.ID_FILE_WS_REFRESH.equals(command)) {
				action.setEnabled(true);
			}
			else if (EditorMenuResources.ID_FILE_PREFERENCE.equals(command)) {
				action.setEnabled(true);
			}
			else if (EditorMenuResources.ID_FILE_WS_SELECT.equals(command)) {
				action.setEnabled(true);
			}
			else if (EditorMenuResources.ID_FILE_QUIT.equals(command)) {
				action.setEnabled(true);
			}
			else {
				action.setEnabled(false);
			}
			return true;
		}
		else if (command.startsWith(EditorMenuResources.ID_EDIT_MENU)) {
			action.setEnabled(false);
			return true;
		}
		else if (command.startsWith(EditorMenuResources.ID_FIND_MENU)) {
			if (EditorMenuResources.ID_FIND_FIND.equals(command)) {
				IEditorView editor = getActiveEditor();
				action.setEnabled(_activeViewManager.isComponentActivated(tabEditor) && editor != null && editor.canFindReplace());
				return true;
			}
			else if (EditorMenuResources.ID_FIND_PREV.equals(command)) {
				IEditorView editor = getActiveEditor();
				action.setEnabled(_activeViewManager.isComponentActivated(tabEditor) && editor != null && editor.canFindReplace());
				return true;
			}
			else if (EditorMenuResources.ID_FIND_NEXT.equals(command)) {
				IEditorView editor = getActiveEditor();
				action.setEnabled(_activeViewManager.isComponentActivated(tabEditor) && editor != null && editor.canFindReplace());
				return true;
			}
		}
		else if (command.startsWith(EditorMenuResources.ID_BUILD_MENU)) {
			if (EditorMenuResources.ID_BUILD_RUNASJAR.equals(command)) {
				action.setEnabled(true);
			}
			else if (EditorMenuResources.ID_BUILD_COMPILEINDIR.equals(command)) {
				action.setEnabled(true);
			}
			else {
				action.setEnabled(false);
			}
			return true;
		}
		else if (command.startsWith(EditorMenuResources.ID_HELP_MENU)) {
			action.setEnabled(true);
			return true;
		}
		else if (action != null) {
			action.setEnabled(false);
		}
		
		return false;
	}
	
	// menu : [File]-[New]-[Project]
	protected void onSelectedMenuFileNewProject(Action action) {
		AppLogger.debug("menu [File]-[New]-[Project] selected.");
		viewTree.doCreateProject();
		setFocusToActiveView();
	}
	
	// menu : [File]-[New]-[Folder]
	protected void onSelectedMenuFileNewFolder(Action action) {
		AppLogger.debug("menu [File]-[New]-[Folder] selected.");
		viewTree.doCreateDirectory();
		setFocusToActiveView();
	}

	// menu : [File]-[New]-[...]
	protected void onSelectedMenuFileNew(String command, Action action) {
		AppLogger.debug("menu [File]-[New] selected : " + String.valueOf(command));
		IEditorView newEditor = null;
		String pid = command.substring(EditorMenuResources.ID_FILE_NEW_PREFIX.length());
		IComponentManager manager = PluginManager.findPlugin(pid);
		if (manager != null) {
			//@@@ 2009/10/29 : Y.Ishizuka : modified
			VirtualFile newFile = viewTree.doCreateFile(manager);
			if (newFile != null) {
				File newJavaFile = ((DefaultFile)newFile).getJavaFile();
				manager.setLastSelectedFile(newJavaFile);
				AppSettings.getInstance().setLastFile(AppSettings.DOCUMENT, newJavaFile);
				// open file
				try {
					newEditor = manager.newDocument(newJavaFile, null);
				}
				catch (FileNotFoundException ex) {
					String errmsg = EditorMessages.getErrorMessage(EditorMessages.MessageID.ERR_FILE_NOTFOUND, ex, newFile.getAbsolutePath());
					AppLogger.error(errmsg, ex);
					AADLEditor.showErrorMessage(this, errmsg);
				}
				catch (UnsupportedEncodingException ex) {
					String errmsg = EditorMessages.getErrorMessage(EditorMessages.MessageID.ERR_FILE_UNSUPPORTED_ENCODING, ex);
					AppLogger.error(errmsg, ex);
					AADLEditor.showErrorMessage(this, errmsg);
				}
				catch (IOException ex) {
					String errmsg = EditorMessages.getErrorMessage(EditorMessages.MessageID.ERR_FILE_READ, ex, newFile.getAbsolutePath());
					AppLogger.error(errmsg, ex);
					AADLEditor.showErrorMessage(this, errmsg);
				}
				catch (OutOfMemoryError ex) {
					String errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
					AppLogger.error(errmsg, ex);
					AADLEditor.showErrorMessage(this, errmsg);
				}

				if (newEditor != null) {
					addEditor(newEditor);
					updateAllMenuItems();
				}
			}
			/*--- 2009/10/29 : old codes
			File initFile = AppSettings.getInstance().getLastFile(AppSettings.DOCUMENT);
			manager.setLastSelectedFile(initFile);
			IEditorView newEditor = manager.onNewComponent(this);
			File lastFile = manager.getLastSelectedFile();
			if (lastFile != null && !lastFile.equals(initFile))
				AppSettings.getInstance().setLastFile(AppSettings.DOCUMENT, lastFile);
			if (newEditor != null) {
				addEditor(newEditor);
				updateAllMenuItems();
			}
			**--- 2009/10/29 : end of old codes */
			//@@@ 2009/10/29 : Y.Ishizuka : end of modified
		}
		if (newEditor != null) {
			setFocusToActiveEditor();
		} else {
			setFocusToActiveView();
		}
	}

	// menu : [File]-[Open]
	protected void onSelectedMenuFileOpen(Action action) {
		AppLogger.debug("menu [File]-[Open] selected.");
		// choose file
		File initFile = AppSettings.getInstance().getLastFile(AppSettings.DOCUMENT);
		File targetFile = FileChooserManager.chooseAllFile(this, initFile, null);
		if (targetFile == null) {
			// not selected
			setFocusToActiveEditor();
			return;
		}
		//--- store last file
		AppSettings.getInstance().setLastFile(AppSettings.DOCUMENT, targetFile);
		
		// exist opened?
		for (int i = 0; i < getEditorCount(); i++) {
			IEditorView editor = getEditor(i);
			File editorFile = editor.getDocumentFile();
			if (editorFile != null && editorFile.equals(targetFile)) {
				// すでに存在している場合は、そこにフォーカスを設定して終了
				tabEditor.setSelectedIndex(i);
				return;
			}
		}
		
		// open file
		IComponentManager manager = PluginManager.findSupportedPlugin(targetFile);
		if (manager == null) {
			// このファイルはサポートされていない
			String errmsg = String.format(EditorMessages.getInstance().msgUnsupportedDocument, targetFile.getAbsolutePath());
			AppLogger.error(errmsg);
			AADLEditor.showErrorMessage(this, errmsg);
			setFocusToActiveEditor();
			return;
		}
		IEditorView newEditor = null;
		try {
			newEditor = manager.openDocument(this, targetFile);
		}
		catch (FileNotFoundException ex) {
			String errmsg = EditorMessages.getErrorMessage(EditorMessages.MessageID.ERR_FILE_NOTFOUND, ex, targetFile.getAbsolutePath());
			AppLogger.error(errmsg, ex);
			AADLEditor.showErrorMessage(this, errmsg);
		}
		catch (UnsupportedEncodingException ex) {
			String errmsg = EditorMessages.getErrorMessage(EditorMessages.MessageID.ERR_FILE_UNSUPPORTED_ENCODING, ex);
			AppLogger.error(errmsg, ex);
			AADLEditor.showErrorMessage(this, errmsg);
		}
		catch (IOException ex) {
			String errmsg = EditorMessages.getErrorMessage(EditorMessages.MessageID.ERR_FILE_READ, ex, targetFile.getAbsolutePath());
			AppLogger.error(errmsg, ex);
			AADLEditor.showErrorMessage(this, errmsg);
		}
		catch (OutOfMemoryError ex) {
			String errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
			AppLogger.error(errmsg, ex);
			AADLEditor.showErrorMessage(this, errmsg);
		}

		if (newEditor != null) {
			addEditor(newEditor);
			updateAllMenuItems();
			setFocusToActiveEditor();
		} else {
			setFocusToActiveView();
		}
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
					EditorMessages.getInstance().confirmReopenDocument,
					editor.getDocumentTitle(),
					JOptionPane.OK_CANCEL_OPTION);
			if (ret != JOptionPane.OK_OPTION) {
				// user canceled
				setFocusToActiveView();
				return;
			}
		}

		// re-open
		String encoding;
		if (EditorMenuResources.ID_FILE_REOPEN_DEFAULT.equals(command)) {
			// reopen by Default encoding
			encoding = null;
		} else {
			encoding = command.substring(EditorMenuResources.ID_FILE_REOPEN_PREFIX.length());
		}
		// re-open document
		boolean result = false;
		try {
			editor.reopenDocument(encoding);
			result = true;
		}
		catch (FileNotFoundException ex) {
			String errmsg = EditorMessages.getErrorMessage(EditorMessages.MessageID.ERR_FILE_NOTFOUND, ex, editor.getDocumentFile().getAbsolutePath());
			AppLogger.error(errmsg, ex);
			AADLEditor.showErrorMessage(this, errmsg);
		}
		catch (UnsupportedEncodingException ex) {
			String errmsg = EditorMessages.getErrorMessage(EditorMessages.MessageID.ERR_FILE_UNSUPPORTED_ENCODING, ex);
			AppLogger.error(errmsg, ex);
			AADLEditor.showErrorMessage(this, errmsg);
		}
		catch (IOException ex) {
			String errmsg = EditorMessages.getErrorMessage(EditorMessages.MessageID.ERR_FILE_READ, ex, editor.getDocumentFile().getAbsolutePath());
			AppLogger.error(errmsg, ex);
			AADLEditor.showErrorMessage(this, errmsg);
		}
		catch (OutOfMemoryError ex) {
			String errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
			AppLogger.error(errmsg, ex);
			AADLEditor.showErrorMessage(this, errmsg);
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
		int tabidx = tabEditor.getSelectedIndex();
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
				//--- register & refresh tree
				viewTree.refreshFileTree(ModuleFileManager.fromJavaFile(editor.getDocumentFile()));
				refreshModuleProperties();
			}
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
				viewTree.doMoveTo();
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
			viewTree.doRename();
			refreshModuleProperties();
		}
		setFocusToActiveView();
	}
	
	// menu : [File]-[Refresh]
	protected void onSelectedMenuFileRefresh(Action action) {
		AppLogger.debug("menu [File]-[Refresh] selected.");
		if (viewTree.isSelectionEmpty()) {
			viewTree.refreshAllTree();
		} else {
			viewTree.refreshSelectedTree();
		}
		updateAllMenuItems();
		refreshModuleProperties();
		setFocusToActiveView();
	}
	
	// menu : [File]-[Refresh Workspace]
	protected void onSelectedMenuFileWorkspaceRefresh(Action action) {
		AppLogger.debug("menu [File]-[Workspace refresh] selected.");
		viewTree.refreshAllTree();
		updateAllMenuItems();
		refreshModuleProperties();
		setFocusToActiveView();
	}
	
	// menu : [File]-[select Workspace]
	protected void onSelectedMenuFileWorkspaceSelect(Action action) {
		AppLogger.debug("menu [File]-[select Workspace] selected.");
		// select Workspace
		File oldWorkspace = AppSettings.getInstance().getLastWorkspace();
		WorkspaceChooser chooser = new WorkspaceChooser(this);
		chooser.setVisible(true);
		if (chooser.getDialogResult() == IDialogResult.DialogResult_OK) {
			File selectedPath = AppSettings.getInstance().getLastWorkspace();
			//--- 念のためチェック
			if (!WorkspaceChooser.verifyWorkspaceRoot(this, false, selectedPath)) {
				setFocusToActiveView();
				return;
			}
			
			// 現在のワークスペースと同一なら処理しない
			VirtualFile newWS = ModuleFileManager.fromJavaFile(selectedPath);
			if (viewTree.getWorkspaceRoot().equals(newWS)) {
				setFocusToActiveView();
				return;
			}
			
			// すべてのエディタを閉じる
			if (!closeAllEditors()) {
				// user canceled
				AppSettings.getInstance().setLastWorkspace(oldWorkspace);
				setFocusToActiveView();
				return;
			}
			
			// 新しいワークスペースを表示
			viewTree.setWorkspaceRoot(newWS);
			updateAllMenuItems();
		}
		setFocusToActiveView();
	}

	// menu : [File]-[Preference]
	protected void onSelectedMenuFilePreference(Action action) {
		AppLogger.debug("menu [File]-[Preference] selected.");
		PreferenceDialog dlg = new PreferenceDialog(this);
		dlg.setVisible(true);
		int ret = dlg.getDialogResult();
		AppLogger.debug("Dialog result : " + ret);
		if (ret == IDialogResult.DialogResult_OK) {
			updateFontBySettings();
		}
		setFocusToActiveEditor();
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

	// menu : [Build]-[Compile]
	protected void onSelectedMenuBuildCompile(Action action) {
		AppLogger.debug("menu [Build]-[Compile] selected.");
		
		IEditorView editor = getActiveEditor();
		if (editor != null) {
			if (compileEditorDocument(editor)) {
				compileAndRun = false;
			}
		}
		setFocusToActiveEditor();
	}

	// menu : [Build]-[Run]
	protected void onSelectedMenuBuildRun(Action action) {
		AppLogger.debug("menu [Build]-[Run] selected.");
		IEditorView editor = getActiveEditor();
		if (editor != null) {
			execEditorDocument(editor);
		}
		setFocusToActiveEditor();
	}

	// menu : [Build]-[Compile & Run]
	protected void onSelectedMenuBuildCompileRun(Action action) {
		AppLogger.debug("menu [Build]-[Compile & Run] selected.");
		
		IEditorView editor = getActiveEditor();
		if (editor != null) {
			if (compileEditorDocument(editor)) {
				compileAndRun = true;
			}
		}
		setFocusToActiveEditor();
	}

	// menu : [Build]-[Run as Jar]
	protected void onSelectedMenuBuildRunAsJar(Action action) {
		AppLogger.debug("menu [Build]-[Run as Jar] selected.");
		runAsJarFile();
		setFocusToActiveEditor();
	}
	
	// menu : [Build]-[Compile all in Folder]
	protected void onSelectedMenuBuildCompileAllInFolder(Action action) {
		AppLogger.debug("menu [Build]-[Compile all in Folder] selected.");
		compileAllInFolder();
		refreshModuleProperties();
		setFocusToActiveEditor();
	}

	// menu : [Build]-[Option]
	protected void onSelectedMenuBuildOption(Action action) {
		AppLogger.debug("menu [Build]-[Option] selected.");
		
		IEditorView editor = getActiveEditor();
		if (editor != null) {
			IEditorDocument doc = editor.getDocument();
			/*
			if (doc instanceof SourceModel) {
				SourceModel srcModel = (SourceModel)doc;
				if (AppLogger.isDebugEnabled()) {
					String msg = String.format("EditorFrame#onSelectedMenuBuildOption : show BuildOptionDialog for srcModel[%s]", String.valueOf(srcModel.getTargetFile()));
					AppLogger.debug(msg);
				}
				AbstractSettings setting = srcModel.getSettings();
				BuildOptionDialog dlg = new BuildOptionDialog(setting,
												isReadOnlyFile(setting.getVirtualPropertyFile()),
												this);
				dlg.setVisible(true);
				if (AppLogger.isDebugEnabled()) {
					int dlgResult = dlg.getDialogResult();
					AppLogger.debug("Dialog result : " + dlgResult);
				}
			}
			*/
			AbstractSettings settings = doc.getSettings();
			if (settings != null) {
				if (AppLogger.isDebugEnabled()) {
					String msg = String.format("EditorFrame#onSelectedMenuBuildOption : show BuildOptionDialog for model[%s]", String.valueOf(doc.getTargetFile()));
					AppLogger.debug(msg);
				}
				settings.refresh();	// 最新の情報に更新
				BuildOptionDialog dlg = new BuildOptionDialog(settings,
												isReadOnlyFile(doc.getTargetFile()),
												this);
				dlg.setVisible(true);
				if (AppLogger.isDebugEnabled()) {
					int dlgResult = dlg.getDialogResult();
					AppLogger.debug("Dialog result : " + dlgResult);
				}
			}
		}
		setFocusToActiveEditor();
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
//		JButton btn = new JButton(EditorMessages.getInstance().LibVersionDlg_Button);
//		btn.addActionListener(new ActionListener(){
//			public void actionPerformed(ActionEvent e) {
//				LibVersionDialog dlg = new LibVersionDialog(EditorFrame.this);
//				dlg.setVisible(true);
//			}
//		});
//		Object[] options = new Object[]{btn, CommonMessages.getInstance().Button_Close};
//		JOptionPane.showOptionDialog(this,
//				AADLEditor.VERSION_MESSAGE,
//				EditorMessages.getInstance().appMainTitle,
//				JOptionPane.OK_OPTION,
//				JOptionPane.INFORMATION_MESSAGE,
//				null,
//				options,
//				options[1]);
		
		// 1.22 : Show message with FALCON-SEED version
		String version_message = FSEnvironment.getInstance().title();
		if (!Strings.isNullOrEmpty(version_message)) {
			version_message = version_message + "\n - " + AADLEditor.LOCAL_VERSION;
		} else {
			version_message = AADLEditor.LOCAL_VERSION;
		}

		JOptionPane.showMessageDialog(this,
				version_message,
				EditorMessages.getInstance().appMainTitle,
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
		if (_activeViewManager.isComponentActivated(viewTree)) {
			// ツリービューのハンドラ
			if (viewTree.onProcessMenuSelection(pCommand, pSource, pAction)) {
				// process is terminate.
				return;
			}
		}
		
		// エディタビューがアクティブなら、そのメニューコマンドを処理
		if (_activeViewManager.isComponentActivated(tabEditor)) {
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
		
		// 情報ビューがアクティブなら、そのメニューコマンドを処理
		if (_activeViewManager.isComponentActivated(tabInfo)) {
			AbstractMonitorPane info = getActiveMonitor();
			if (info != null) {
				//--- 情報ペイン固有のハンドラ
				if (info.onProcessMenuSelection(pCommand, pSource, pAction)) {
					// process is terminate.
					return;
				}
			}
		}

		/*
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
		*/
		
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
		if (_activeViewManager.isComponentActivated(viewTree)) {
			// ツリービューのハンドラ
			if (viewTree.onProcessMenuUpdate(command, source, pAction)) {
				// process is terminate.
				return;
			}
		}
		
		// エディタビューがアクティブなら、そのメニューコマンドを処理
		if (_activeViewManager.isComponentActivated(tabEditor)) {
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
		
		// 情報ビューがアクティブなら、そのメニューコマンドを処理
		if (_activeViewManager.isComponentActivated(tabInfo)) {
			AbstractMonitorPane info = getActiveMonitor();
			if (info != null) {
				//--- 情報ペイン固有のハンドラ
				if (info.onProcessMenuUpdate(command, source, pAction)) {
					// process is terminate.
					return;
				}
			}
		}

		/*
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
		*/
		
		// このイベントが処理されていない場合は、このフレームのイベントハンドラを呼び出す
		onProcessMenuUpdate(command, source, pAction);
	}

	//------------------------------------------------------------
	// Event handler actions
	//------------------------------------------------------------
	
	protected void setupComponentActions() {
		// Editor tab
		tabEditor.addMouseListener(new EditorTabMouseHandler());
		tabEditor.addChangeListener(new EditorTabSelectionHandler());
		
		// Info tab
		tabInfo.addMouseListener(new InfoTabMouseHandler());
		tabInfo.addChangeListener(new InfoTabSelectionHandler());
		
		// Build pane
		paneBuild.addExecutorStoppedListener(new CompileStoppedHandler());
		
		// Console pane
		paneConsole.addExecutorStoppedListener(new RunStoppedHandler());
		
		// ActiveView
		_activeViewManager.addActiveViewChangeListener(new EditorActiveViewChangeListener());
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
				int tabIndex = tabEditor.indexAtLocation(me.getX(), me.getY());
				if (tabIndex >= 0) {
					EditorMenuBar menuBar = getActiveEditorMenuBar();
					if (menuBar != null) {
						menuBar.getEditorTabContextMenu().show(me.getComponent(), me.getX(), me.getY());
					}
				}
			}
		}
	}

	/**
	 * 情報タブのマウスイベントハンドラ
	 */
	class InfoTabMouseHandler extends MouseAdapter {
		public void mouseClicked(MouseEvent me) {
			setFocusToActiveConsole();
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
	 * 情報タブの選択変更イベントハンドラ
	 */
	class InfoTabSelectionHandler implements ChangeListener {
		public void stateChanged(ChangeEvent ce) {
			setFocusToActiveConsole();
		}
	}

	/**
	 * コンパイル停止時のイベントハンドラ
	 */
	class CompileStoppedHandler implements ExecutorStoppedListener {
		public void executionStopped(ExecutorEvent ee) {
			if (AppLogger.isInfoEnabled()) {
				if (ee.getExecutor() != null) {
					AppLogger.info("[" + ee.getExecutor().getProcessIdentifierString() + "] Compile stopped! --- Exit code : " + ee.getExitCode());
				} else {
					AppLogger.info("[unknown process] Compile stopped! --- Exit code : Nan");
				}
			}
			// コンパイルが正常終了していなければ、終了
			if (ee.getExecutor() == null || ee.getExitCode() != 0) {
				compileAndRun = false;
				return;
			}
			
			// コンパイル設定の検証
			IEditorDocument document = null;
			Component c = ee.getComponent();
			if (c instanceof CompileMonitorPane) {
				document = ((CompileMonitorPane)c).getTargetDocument();
			}
			if (document == null) {
				compileAndRun = false;
				return;
			}
			
			// コンパイル結果による設定情報の更新
			document.onCompileFinished();
			//srcModel.updateSettingsByCompileResult();
			//--- ツリー表示の更新
			viewTree.refreshFileTree(ModuleFileManager.fromJavaFile(document.getTargetFile().getParentFile()));
			refreshModuleProperties();

			// 必要であれば、ドキュメントの実行を開始する
			if (compileAndRun && document.isExecutable()) {
				// コンパイル＆実行
				compileAndRun = false;
				execByDocument(document);
			}
		}
	}

	/**
	 * 実行停止時のイベントハンドラ
	 */
	class RunStoppedHandler implements ExecutorStoppedListener {
		public void executionStopped(ExecutorEvent ee) {
			if (AppLogger.isInfoEnabled()) {
				if (ee.getExecutor() != null) {
					AppLogger.info("[" + ee.getExecutor().getProcessIdentifierString() + "] Execution stopped! --- Exit code : " + ee.getExitCode());
					// TODO: 実行完了時のツリー更新処理。ただし、場合によっては時間のかかり処理なので、今は封印
					//--- 作業ディレクトリから最上位プロジェクトを取得し、そのツリー表示を更新する。
					//File workdir = ee.getExecutor().getWorkDirectory();
					//if (workdir != null) {
					//	viewTree.refreshFileTreeFromProject(ModuleFileManager.fromJavaFile(workdir));
					//}
				} else {
					AppLogger.info("[unknown process] Execution stopped! --- Exit code : Nan");
				}
			}
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
				updateMenuItem(EditorMenuResources.ID_FILE_SAVE);
			}
		}
		protected void evaluateModifiedChanged(IEditorView editor) {
			int tabIndex = tabEditor.indexOfComponent(editor.getComponent());
			if (tabIndex >= 0) {
				String tabTitle = getEditorTabTitle(editor);
				tabEditor.setTitleAt(tabIndex, tabTitle);
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
		}
	}

	/*
	 * エディタタブのフォーカスが変更されたときのイベントハンドラ
	 * @deprecated 使用しない
	 *
	class EditorTabFocusHandler implements FocusListener {
		public void focusGained(FocusEvent e) {
			AppLogger.debug("Editor tab focus Gained!");
			IEditorView editor = getActiveEditor();
			if (editor != null && !editor.hasFocusInComponent()) {
				editor.requestFocusInComponent();
			}
		}
		public void focusLost(FocusEvent e) {
			AppLogger.debug("Editor tab focus Lost!");
		}
	}
	*---*/

	/*
	 * 情報エリアのタブのフォーカスが変更されたときのイベントハンドラ
	 * @deprecated 使用しない
	 *
	class InfoTabFocusHandler implements FocusListener {
		public void focusGained(FocusEvent e) {
			AppLogger.debug("Info tab focus Gained!");
			AbstractMonitorPane info = getActiveInfoPane();
			if (info != null && !info.getTextComponent().hasFocus()) {
				info.requestFocusInTextComponent();
			}
		}
		public void focusLost(FocusEvent e) {
			AppLogger.debug("Info tab focus Lost!");
		}
	}
	*---*/
	
	class FileDropTargetListener extends DropTargetAdapter
	{
		@Override
		public void dragEnter(DropTargetDragEvent dtde) {
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
				return;
			}
			
			// ドロップアクションをコピー操作に限定
			dtde.acceptDrag(DnDConstants.ACTION_COPY);
		}

		@Override
		public void dragOver(DropTargetDragEvent dtde) {
			// 何もしない
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
								tabEditor.setSelectedIndex(i);
								continue loopFileList;
							}
						}
						//--- open file
						IComponentManager manager = PluginManager.findSupportedPlugin(targetFile);
						if (manager == null) {
							// このファイルはサポートされていない
							String errmsg = String.format(EditorMessages.getInstance().msgUnsupportedDocument, targetFile.getAbsolutePath());
							AppLogger.error(errmsg);
							AADLEditor.showErrorMessage(EditorFrame.this, errmsg);
							dtde.dropComplete(true);
							setFocusToActiveEditor();
							return;	// サポートされていないファイルと判明した時点で、処理終了
						}
						IEditorView newEditor = null;
						try {
							newEditor = manager.openDocument(EditorFrame.this, targetFile);
						}
						catch (FileNotFoundException ex) {
							String errmsg = EditorMessages.getErrorMessage(EditorMessages.MessageID.ERR_FILE_NOTFOUND, ex, targetFile.getAbsolutePath());
							AppLogger.error(errmsg, ex);
							AADLEditor.showErrorMessage(EditorFrame.this, errmsg);
						}
						catch (UnsupportedEncodingException ex) {
							String errmsg = EditorMessages.getErrorMessage(EditorMessages.MessageID.ERR_FILE_UNSUPPORTED_ENCODING, ex);
							AppLogger.error(errmsg, ex);
							AADLEditor.showErrorMessage(EditorFrame.this, errmsg);
						}
						catch (IOException ex) {
							String errmsg = EditorMessages.getErrorMessage(EditorMessages.MessageID.ERR_FILE_READ, ex, targetFile.getAbsolutePath());
							AppLogger.error(errmsg, ex);
							AADLEditor.showErrorMessage(EditorFrame.this, errmsg);
						}
						catch (OutOfMemoryError ex) {
							String errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
							AppLogger.error(errmsg, ex);
							AADLEditor.showErrorMessage(EditorFrame.this, errmsg);
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
//		if (this.getExtendedState() == JFrame.NORMAL) {
//			this.lastFrameSize.setSize(this.getSize());
//			try {
//				if (lastFrameLocation != null) {
//					lastFrameLocation.setLocation(this.getLocationOnScreen());
//				} else {
//					lastFrameLocation = new Point(this.getLocationOnScreen());
//				}
//			} catch(IllegalComponentStateException icse) {
//				AppLogger.debug(icse);
//			}
//		}
		
		// adjust Property divider
		viewTree.adjustPropertyDivider();
		
		// set Focus to Tree View
		viewTree.requestFocusInComponent();
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
		AppLogger.info("\n<<<<< Finished - " + AADLEditor.SIMPLE_VERSION_INFO + " >>>>>\n");
	}
	
//	// --- Window resized event
//	@Override
//	protected void onWindowResized(ComponentEvent ce) {
//		if (this.getExtendedState() == JFrame.NORMAL) {
//			lastFrameSize.setSize(this.getSize());
//		}
//	}
//	
//	// --- Window moved event
//	@Override
//	protected void onWindowMoved(ComponentEvent ce) {
//		if (this.getExtendedState() == JFrame.NORMAL) {
//			try {
//				if (lastFrameLocation != null) {
//					lastFrameLocation.setLocation(this.getLocationOnScreen());
//				} else {
//					lastFrameLocation = new Point(this.getLocationOnScreen());
//				}
//			} catch(IllegalComponentStateException icse) {
//				AppLogger.debug(icse);
//			}
//		}
//	}
}
