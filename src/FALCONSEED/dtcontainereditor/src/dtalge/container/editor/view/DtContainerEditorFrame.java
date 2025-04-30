/*
 * @(#)DtContainerEditorFrame.java	2.0.0	2025/02/17
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtContainerEditorFrame.java	1.1.0	2023/01/20
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtContainerEditorFrame.java	1.0.0	2022/12/21
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
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
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import dtalge.container.DtBinder;
import dtalge.container.DtSlip;
import dtalge.container.DtSlipList;
import dtalge.container.editor.DtContainerEditor;
import dtalge.container.editor.DtContainerEditorMessages;
import dtalge.container.editor.content.DCEditControllerManager;
import dtalge.container.editor.content.IDtContainerDocument;
import dtalge.container.editor.content.IDtContainerEditController;
import dtalge.container.editor.content.IDtContainerEditView;
import dtalge.container.editor.content.binder.DtBinderDocument;
import dtalge.container.editor.content.binder.DtBinderEditController;
import dtalge.container.editor.content.binder.DtBinderEditView;
import dtalge.container.editor.content.common.swing.DtBinderDoubleEntrySimpleSlipsCsvDialog;
import dtalge.container.editor.content.common.table.DTCContentEditTableModelConversionError;
import dtalge.container.editor.content.slip.DtSlipEditController;
import dtalge.container.editor.content.slip.DtSlipEditView;
import dtalge.container.editor.menu.DtContainerEditorMenuBar;
import dtalge.container.editor.menu.DtContainerEditorMenuResources;
import dtalge.container.editor.setting.DtContainerEditorSettings;
import dtalge.container.editor.view.dialog.DtContainerFileChooserManager;
import dtalge.container.util.DebitCreditItemDefinitionTable;
import dtalge.container.util.DebitCreditItemDefinitionTableCsvFormatError;
import dtalge.container.util.DtDoubleEntrySimpleSlipsCsvFormatError;
import dtalge.container.util.DtDoubleEntrySimpleSlipsCsvUtil;
import exalge2.ExAlgeSet;
import exalge2.Exalge;
import net.arnx.jsonic.JSONException;
import ssac.aadl.common.CommonMessages;
import ssac.falconseed.common.FSEnvironment;
import ssac.util.Strings;
import ssac.util.Validations;
import ssac.util.io.Files;
import ssac.util.logging.AppLogger;
import ssac.util.swing.Application;
import ssac.util.swing.FrameWindow;
import ssac.util.swing.IDialogResult;
import ssac.util.swing.StatusBar;
import ssac.util.swing.menu.IMenuActionHandler;
import ssac.util.swing.menu.IMenuHandler;
import ssac.util.swing.menu.JMenus;

/**
 * データコンテナエディタのメインフレーム
 * <p>
 * このクラスは、データコンテナエディタのフレームワークを提供する。
 * 
 * @version 2.0.0
 */
public class DtContainerEditorFrame extends FrameWindow implements IMenuHandler, IMenuActionHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;

	static private final IDtContainerEditView[] EMPTY_EDITOR_ARRAY = new IDtContainerEditView[0];
	
	static private final Dimension DM_DEF_SIZE = new Dimension(800,600);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/**
	 * このフレームの標準メニューバー
	 */
	protected DtContainerEditorMenuBar	_defMainMenu;
	/**
	 * 現在アクティブなエディタ
	 */
	protected IDtContainerEditView			_activeEditor;
	
	/** 編集機能のコントローラーを管理するオブジェクト **/
	protected DCEditControllerManager		_editControllerManager = new DCEditControllerManager();
	
	//--- Events
	private final EditorModifiedChangeHandler	hEditorModified = new EditorModifiedChangeHandler();
	//private final FileDropTargetListener hEditorFileDroped = new FileDropTargetListener();
	
	// Components
	//private JSplitPane		viewSplitOuter;
	//private JSplitPane		viewSplitInner;
	//private EditorTreeView	viewTree;
	private JTabbedPane		_tabEditor;
	private StatusBar		_statusBar;
	
	//private ProcessMonitorPane	paneConsole;
	//private CompileMonitorPane	paneBuild;
	
	//private FindDialog		dlgFind;

	/**
	 * エディタドキュメントのソースファイルが更新されている場合に、
	 * エディタドキュメントを再読込する処理を有効にするフラグ
	 */
	private boolean	_isEnabledRefreshEditorDocumentWhenUpdating = true;
	/**
	 * エディタドキュメントのソースファイル最終更新日時を保持するマップ。
	 * ドキュメントが持つ情報と変わらない場合は、<tt>null</tt> を保持する。
	 */
	private Map<IDtContainerEditView, Long>	_mapEditorSourceFileLastModifiedTime = new HashMap<IDtContainerEditView, Long>();

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public DtContainerEditorFrame() {
		super();
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
		_defMainMenu = new DtContainerEditorMenuBar(this);
		setEditorMenuBar(_defMainMenu);
		
		// setup Views
		JComponent mainPanel = createMainPanel();
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		/*--- テスト ---**
		{
			// ダミータブ
			JSplitPane paneOuter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			paneOuter.setResizeWeight(0);
			JSplitPane paneInner = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
			paneInner.setResizeWeight(1);
			
			String[] dummyListData = {"one", "two", "three" };
			JList<String> dlist = new JList<String>(dummyListData);
			JScrollPane scList = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			scList.setViewportView(dlist);
			
			paneInner.setTopComponent(scList);
			paneInner.setBottomComponent(new JPanel());
			
			JTree dtree = new JTree();
			JScrollPane scTree = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scTree.setViewportView(dtree);
			
			paneOuter.setLeftComponent(scTree);
			paneOuter.setRightComponent(paneInner);
			
			_tabEditor.addTab("test", paneOuter);
		}
		/*--- end of test ---*/
		
		// setup Status bar
		_statusBar = new StatusBar();
		getContentPane().add(_statusBar, BorderLayout.SOUTH);
		
		// setup drop targets
		///new DropTarget(this, DnDConstants.ACTION_COPY, hEditorFileDroped, true);
		
		// register views
		///_activeViewManager.registerComponent(viewTree);
		///_activeViewManager.registerComponent(tabEditor);
		///_activeViewManager.registerComponent(tabInfo);
		
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
	
	private JComponent createMainPanel() {
		// メインタブペイン
		//--- タブが空のときの背景色は、JTabbedPane で描画されている。
		//--- JTabbedPane を上位コンポーネントに追加しない場合、上位コンポーネントの背景色は変更できるが、
		//--- JTabbedPane を上位コンポーネントに追加すると、タブが空の場合は白色になってしまう。
		//--- JTabbedPane の背景色を変更しても、変化なし、なので、無視
		_tabEditor = createEditorTab();
		
		// メインパネルを返す
		return _tabEditor;
		
		// testing for DtAlgeSetEditTable
		//DtContainerContentDtAlgeSetEditView testview = new DtContainerContentDtAlgeSetEditView();
		//testview.initialCompoents();
		//return testview;
	}
	
	private JTabbedPane createEditorTab() {
		// tab colors
		/*--- ---*
		//UIManager.put("TabbedPane.background", Color.RED);
		//UIManager.put("TabbedPane.contentAreaColor", Color.RED);
		System.out.println("-----< Look and Feel Defaults >-----");
		UIDefaults defs = UIManager.getLookAndFeelDefaults();
		for (Map.Entry<Object, Object> entry : defs.entrySet()) {
			String key = String.valueOf(entry.getKey());
			String val = String.valueOf(entry.getValue());
			if (key.startsWith("Tabbed")) {
				System.out.println("[" + key + "] = " + val);
			}
		}
		System.out.println("-----< UI Defaults >-----");
		defs = UIManager.getDefaults();
		for (Map.Entry<Object, Object> entry : defs.entrySet()) {
			String key = String.valueOf(entry.getKey());
			String val = String.valueOf(entry.getValue());
			if (key.startsWith("Tabbed")) {
				System.out.println("[" + key + "] = " + val);
			}
		}
		UIManager.put("TabbedPane.background", Color.RED);
		/*--- ---*/
		
		// create component
		JTabbedPane tab = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
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
	public boolean isReadOnlyFile(File file) {
		if (file == null)
			return false;
		
		if (file.exists() && !file.canWrite()) {
			// ファイルは存在するが書き込み不可である場合は、読み取り専用
			return true;
		} else {
			// それ以外は、書き込み可能
			return false;
		}
	}

	/**
	 * 現在表示されているすべてのエディタを取得する。
	 * @return	表示されているすべてのエディタオブジェクトを格納する配列を返す。
	 * 			エディタが一つも表示されていない場合は空の配列を返す。
	 * @since 1.14
	 */
	public IDtContainerEditView[] getAllEditors() {
		int numEditors = getEditorCount();
		if (numEditors > 0) {
			IDtContainerEditView[] editors = new IDtContainerEditView[numEditors];
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
	public IDtContainerEditView[] getModifiedEditors() {
		ArrayList<IDtContainerEditView> list = null;
		int numDocuments = getEditorCount();
		if (numDocuments > 0) {
			list = new ArrayList<IDtContainerEditView>(numDocuments);
			for (int i = 0; i < numDocuments; i++) {
				IDtContainerEditView editor = getEditor(i);
				IDtContainerDocument doc = editor.getDocument();
				if (doc.isModified()) {
					list.add(editor);
				}
			}
		}
		
		if (list != null && !list.isEmpty()) {
			return list.toArray(new IDtContainerEditView[list.size()]);
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
			IDtContainerDocument doc = getEditor(i).getDocument();
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
			IDtContainerDocument doc = getEditor(i).getDocument();
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
		return _tabEditor.getTabCount();
	}

	/**
	 * 指定されたインデックスのタブが保持するエディタを取得する。
	 * @param index	エディタタブのインデックス
	 * @return	エディタのインスタンス
	 */
	private IDtContainerEditView getEditor(int index) {
		return (IDtContainerEditView)_tabEditor.getComponentAt(index);
	}

	/**
	 * このフレームで、現在アクティブなビューを取得する。
	 * @return	現在アクティブなビューを返す。アクティブなビューが存在しない場合は
	 * 			<tt>null</tt> を返す。
	 */
	public IDtContainerEditView getActiveEditor() {
		return _activeEditor;
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
		IDtContainerEditView editor = getEditor(editorTabIndex);
		
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
		editor.getEditController().removeDocument(editor);
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
	 * @param editor		閉じるエディタの <code>IDtContainerEditView</code> オブジェクト
	 * @return	エディタを閉じた場合に <tt>true</tt>、閉じなかった場合は <tt>false</tt>
	 * @throws IllegalArgumentException	<em>editor</em> がタブに存在しない場合
	 */
	public boolean closeEditorByEditor(boolean onlyNotExists, boolean withSave, IDtContainerEditView editor) {
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
		IDtContainerEditView editor = getEditor(editorTabIndex);
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
	public void renameToEditorFile(IDtContainerEditView editor, File newFile) {
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
	public DtContainerEditorMenuBar getDefaultEditorMenuBar() {
		return _defMainMenu;
	}
	
	/**
	 * アクティブなエディタメニューバーを返す。
	 * 基本的に、このフレームに現在設定されているメニューバーで
	 * <code>EditorMenuBar</code> インスタンスである場合に、
	 * そのインスタンスを返す。この条件に当てはまらない場合は <tt>null</tt> を返す。
	 * @return	アクティブなエディタメニューバー
	 */
	public DtContainerEditorMenuBar getActiveEditorMenuBar() {
		JMenuBar bar = getJMenuBar();
		if (bar instanceof DtContainerEditorMenuBar)
			return ((DtContainerEditorMenuBar)bar);
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
		//JMenuBar bar = getActiveEditorMenuBar();
		//return (bar==null ? null : bar.getEditorContextMenu());
		// TODO: コンテキストメニュー
		return null;
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
		DtContainerEditorMenuBar bar = getActiveEditorMenuBar();
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
		DtContainerEditorMenuBar bar = getActiveEditorMenuBar();
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
		DtContainerEditorMenuBar bar = getActiveEditorMenuBar();
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
		DtContainerEditorMenuBar bar = getActiveEditorMenuBar();
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
	 */
	public boolean openEditorFromFile(File targetFile) {
		// exist opened?
		for (int i = 0; i < getEditorCount(); i++) {
			IDtContainerEditView editor = getEditor(i);
			File editorFile = editor.getDocumentFile();
			if (editorFile != null && editorFile.equals(targetFile)) {
				// すでに存在している場合は、そこにフォーカスを設定して終了
				_tabEditor.setSelectedIndex(i);
				setFocusToActiveEditor();
				return true;
			}
		}
		
		// open file as supported document
		AppLogger.debug("open file DtSlip/DtBinder from " + (targetFile==null ? "\'null\'" : targetFile.getAbsolutePath()));
		IDtContainerEditView newEditor = null;
		try {
			newEditor = _editControllerManager.openSupportedDocument(targetFile);
		}
		catch (FileNotFoundException ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_FILE_NOTFOUND, ex, targetFile.getAbsolutePath());
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
		}
		catch (UnsupportedEncodingException ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_FILE_UNSUPPORTED_ENCODING, ex);
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
		}
		catch (JSONException ex) {
			// Unsupported file format as JSON for Data-container
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_FILE_UNSUPPORTED_FORMAT, ex, targetFile.getAbsolutePath());
			AppLogger.error(errmsg);
			DtContainerEditor.showErrorMessage(this, errmsg);
		}
		catch (IOException ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_FILE_READ, ex, targetFile.getAbsolutePath());
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
		}
		catch (OutOfMemoryError ex) {
			String errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
		}
		catch (Throwable ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_UNEXPECTED, ex);
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
		}

		if (newEditor != null) {
			addEditor(newEditor);
			updateAllMenuItems();
			setFocusToActiveEditor();
			return true;
		} else {
			setFocusToActiveEditor();
			return false;
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * ファイルシステムによってドキュメントのソースファイルが更新されている場合に
	 * 再読込を行う処理を有効にするかどうかを設定する。
	 * デフォルトでは有効となっている。
	 * @param toEnable	有効にする場合は <tt>true</tt>、無効にする場合は <tt>false</tt>
	 */
	private void setEnableRefreshEditorDocumentWhenUpdating(boolean toEnable) {
		_isEnabledRefreshEditorDocumentWhenUpdating = toEnable;
	}

	/**
	 * ファイルシステムによってドキュメントのソースファイルが更新されている場合は再読込を行う。
	 * 再読込では、ユーザーに再読込を行うかどうか問い合わせる。
	 * @param editor	再読込を行うエディタ
	 * @return	再読込が行われた場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	private boolean refreshEditorDocumentFromSourceFileAsNeeded(IDtContainerEditView editor) {
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
									DtContainerEditorMessages.getInstance().confirmFileChangedReplace,
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
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_FILE_NOTFOUND, ex, editor.getDocumentFile().getAbsolutePath());
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
		}
		catch (UnsupportedEncodingException ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_FILE_UNSUPPORTED_ENCODING, ex);
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
		}
		catch (IOException ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_FILE_READ, ex, editor.getDocumentFile().getAbsolutePath());
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
		}
		catch (OutOfMemoryError ex) {
			String errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
		}
		catch (Throwable ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_UNEXPECTED, ex);
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
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
///			setFocusToActiveView();
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
	private boolean confirmOverwriteEditorSourceFileWhenUpdateConflict(IDtContainerEditView editor) {
		if (editor==null || !isUpdatedEditorSourceFileWhenSave(editor)) {
			// no updating
			return true;
		}
		
		// confirm
		setEnableRefreshEditorDocumentWhenUpdating(false);
		int ret = JOptionPane.showConfirmDialog(this,
									DtContainerEditorMessages.getInstance().confirmUpdateConflictReplace,
									DtContainerEditorMessages.getInstance().confirmTitle_UpdateConflictReplace, JOptionPane.YES_NO_OPTION);
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
	private boolean isUpdatedEditorSourceFileWhenActive(IDtContainerEditView editor) {
		IDtContainerDocument doc = editor.getDocument();
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
	private boolean isUpdatedEditorSourceFileWhenSave(IDtContainerEditView editor) {
		IDtContainerDocument doc = editor.getDocument();
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
	private void refreshEditorSourceLastModifiedTime(IDtContainerEditView editor) {
		IDtContainerDocument doc = editor.getDocument();
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
	private void removeEditorSourceLastModifiedTime(IDtContainerEditView editor) {
		_mapEditorSourceFileLastModifiedTime.remove(editor);
	}

	/**
	 * アクティブなメニューバーを設定する。メニューバーが更新されると、
	 * 関連するツールバーも更新される。
	 * 
	 * @param menuBar
	 */
	private void setEditorMenuBar(DtContainerEditorMenuBar menuBar) {
		Validations.validNotNull(menuBar);
		JMenuBar oldBar = getJMenuBar();
		
		// 新しいメニューバーがすでに設定済みなら、処理しない
		if (oldBar == menuBar)
			return;
		
		// ツリービューに新しいコンテキストメニューを設定する
		//setActiveContextMenuToTeeView(menuBar);

		// 古いツールバーを削除
		if (oldBar instanceof DtContainerEditorMenuBar) {
			JToolBar oldToolBar = ((DtContainerEditorMenuBar)oldBar).getMainToolBar();
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
	 */
	@Override
	protected Dimension getDefaultSize() {
		return DM_DEF_SIZE;
	}

	/**
	 * アプリケーションのプロパティから、コンポーネントの状態を復帰する。
	 */
	private void restoreSettings() {
		DtContainerEditorSettings settings = DtContainerEditorSettings.getInstance();

		// restore window states
		restoreWindowStatus(settings.getConfiguration(), DtContainerEditorSettings.MAINFRAME);
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
		int divloc = settings.getDividerLocation(DtContainerEditorSettings.OUTER_FRAME);
		if (divloc < 0) {
			// default location
			Dimension dm = getSize();
			divloc = dm.width / 4;
		}
///		viewSplitOuter.setDividerLocation(divloc);
		//--- Inner Divider location
		divloc = settings.getDividerLocation(DtContainerEditorSettings.INNER_FRAME);
		if (divloc < 0) {
			// default location
			Dimension dm = getSize();
			if (dm.height < 50) {
				dm = this.getSize();
			}
			divloc = dm.height / 3 * 2;
		}
///		viewSplitInner.setDividerLocation(divloc);
		//--- Property Divider location
		divloc = settings.getDividerLocation("hogehoge");
		if (divloc < 0) {
			divloc = Integer.MAX_VALUE;
		}
///		viewTree.setDividerLocation(divloc);
		
		// restore View fonts
		updateFontBySettings();
	}

	/**
	 * 現在のコンポーネントの状態を、アプリケーションのプロパティとして保存する。
	 */
	private void storeSettings() {
		DtContainerEditorSettings settings = DtContainerEditorSettings.getInstance();
		// Window states
		storeWindowStatus(settings.getConfiguration(), DtContainerEditorSettings.MAINFRAME);
//		settings.setWindowState(AppSettings.MAINFRAME, this.getExtendedState());
//		settings.setWindowLocation(AppSettings.MAINFRAME, this.lastFrameLocation);
//		settings.setWindowSize(AppSettings.MAINFRAME, this.lastFrameSize);
		// Views states
///		settings.setDividerLocation(AppSettings.OUTER_FRAME, viewSplitOuter.getDividerLocation());
///		settings.setDividerLocation(AppSettings.INNER_FRAME, viewSplitInner.getDividerLocation());
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
		
		// save preferences
		storeSettings();
		
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
	private boolean canEditorDocumentClose(final IDtContainerEditView editor) {
		if (editor.isModified()) {
			int ret = JOptionPane.showConfirmDialog(this,
					DtContainerEditorMessages.getInstance().confirmSaveChanges,
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
	private boolean saveEditorDocument(final IDtContainerEditView editor) {
		final IDtContainerDocument document = editor.getDocument();
		if (!document.hasTargetFile()) {
			return saveAsEditorDocument(editor);
		}
		
		if (editor.isReadOnly()) {
			String errmsg = String.format(
					DtContainerEditorMessages.getInstance().msgCannotWriteCauseReadOnly,
					document.getTargetFile().getName());
			AppLogger.error("DtContainerEditorFrame#saveEditorDocument(\"" + document.getTargetFile() + "\") : " + errmsg);
			DtContainerEditor.showWarningMessage(this, errmsg);
			return false;
		}
		
		// check conflict
		if (!confirmOverwriteEditorSourceFileWhenUpdateConflict(editor)) {
			// user canceled
			return false;
		}
		
		// save current document to current file
		editor.refreshDocumentSettings();
		boolean result = editor.getEditController().onSaveComponent(this, editor);
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
	private boolean saveAsEditorDocument(final IDtContainerEditView editor) {
		IDtContainerEditController controller = editor.getEditController();
		String strLastPath = DtContainerEditorSettings.getInstance().getLastFilename(DtContainerEditorSettings.DOCUMENT);
		File initFile = (Strings.isNullOrEmpty(strLastPath) ? null : new File(strLastPath));
		controller.setLastSelectedFile(initFile);
		
		boolean result = controller.onSaveAsComponent(this, editor);
		if (result) {
			// 保存成功
			removeEditorSourceLastModifiedTime(editor);
		} else {
			// 保存失敗
			refreshEditorSourceLastModifiedTime(editor);
		}
		
		File lastFile = controller.getLastSelectedFile();
		if (lastFile != null && !lastFile.equals(initFile)) {
			DtContainerEditorSettings.getInstance().setLastFilename(DtContainerEditorSettings.DOCUMENT,
					lastFile.getAbsolutePath());
		}
		
		return result;
	}

	/**
	 * エディタビューから、エディタ用タブのタイトルを取得する。
	 * 
	 * @param editor タイトルを取得する対象のエディタ
	 * @return タイトル文字列
	 */
	private String getEditorTabTitle(final IDtContainerEditView editor) {
		String title;
		if (editor != null) {
			StringBuilder sb = new StringBuilder();
			if (editor.isModified()) {
				sb.append(DtContainerEditorMessages.getInstance().editingDocumentModifier);
			}
			sb.append(editor.getDocumentTitle());
			sb.append(" (");
			sb.append(editor.getDocument().getRootContentClassSimpleName());
			sb.append(") ");
			return sb.toString();
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
		String title = DtContainerEditorMessages.getInstance().appMainTitle;
		if (_activeEditor != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(_activeEditor.getDocumentTitle());
			sb.append(" (");
			sb.append(_activeEditor.getDocument().getRootContentClassSimpleName());
			sb.append(") ");
			//sb.append("[");
			//sb.append(_activeEditor.getLastEncodingName());
			//sb.append("] ");
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
	private void addEditor(IDtContainerEditView editor) {
		attachEditorModifiedPropertyChangeHandler(editor);
		//editor.setPopupMenu(pmenuForEditor);
		String tabTitle = getEditorTabTitle(editor);
		String tabTooltip = editor.getDocumentPath();
		if (tabTooltip != null)
			_tabEditor.addTab(tabTitle, null, editor.getComponent(), tabTooltip);
		else
			_tabEditor.addTab(tabTitle, editor.getComponent());
		_tabEditor.setSelectedComponent(editor.getComponent());
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
     * @since 1.14
	 */
	private void updateDisplayEditorTab(int tabIndex) {
		final IDtContainerEditView editor = getEditor(tabIndex);
		String tabTitle = getEditorTabTitle(editor);
		String tabTooltip = editor.getDocumentPath();
		_tabEditor.setTitleAt(tabIndex, tabTitle);
		_tabEditor.setToolTipTextAt(tabIndex, tabTooltip);
	}
	
	private void attachEditorModifiedPropertyChangeHandler(IDtContainerEditView editor) {
		editor.getComponent().addPropertyChangeListener(IDtContainerEditView.PROP_MODIFIED, hEditorModified);
	}
	
	private void detachEditorModifiedPropertyChangeHandler(IDtContainerEditView editor) {
		editor.getComponent().removePropertyChangeListener(IDtContainerEditView.PROP_MODIFIED, hEditorModified);
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
		IDtContainerEditView editor = getEditor(tabIndex);
		boolean flgClosed = false;
		if (editor != null && canEditorDocumentClose(editor)) {
			detachEditorModifiedPropertyChangeHandler(editor);
			editor.getEditController().removeDocument(editor);
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
			final IDtContainerEditView editor = getEditor(i);
			if (!editor.isModified()) {
				// 更新されていないので、すぐに閉じる
				detachEditorModifiedPropertyChangeHandler(editor);
				editor.getEditController().removeDocument(editor);
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
	 * 現在アクティブなエディタコンポーネントにフォーカスを設定する。
	 */
	private void setFocusToActiveEditor() {
		IDtContainerEditView editor = getActiveEditor();
		if (editor != null) {
			editor.requestFocusInComponent();
		}
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
		// 現時点では、AADLソースエディタのフォントのみ更新
		Font font = DtContainerEditorSettings.getInstance().getFont(DtContainerEditorSettings.EDITOR);
		if (font != null) {
			// TODO: フォント、どうする？
///			PluginManager.getDefaultPlugin().setEditorFont(font);
		}
	}

	/**
	 * 現在のタブ選択状態により、アクティブなエディタを更新する。
	 */
	private void updateActiveEditor() {
		DtContainerEditorMenuBar menuBar;
		int selidx = _tabEditor.getSelectedIndex();
		if (selidx >= 0) {
			_activeEditor = getEditor(selidx);
			menuBar = _activeEditor.getDocumentMenuBar();
		} else {
			_activeEditor = null;
			menuBar = null;
		}
		DtContainerEditorFrame.this.setTitle(getFrameTitle(_activeEditor==null ? false : _activeEditor.isReadOnly()));
		setEditorMenuBar(menuBar==null ? _defMainMenu : menuBar);
		updateAllMenuItems();
		if (_activeEditor != null) {
			_activeEditor.requestFocusInComponent();
		} else {
			// ビューが一つも存在しない場合は、ツリーにフォーカスを移す
///			viewTree.requestFocusInComponent();
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
//			AppLogger.debug(String.format("EditorFrame#updateActiveEditor : TabIndex[%d] / activeEditor[%s]", selidx, editorName));
		}
	}

	/**
	 * 変更されたすべてのリソースを保存するかを問い合わせる確認ダイアログを表示し、
	 * 変更されたすべてのリソースを保存する。
	 * @return	変更されたすべてのリソースが保存された場合のみ <tt>true</tt> を返す。
	 */
	private boolean confirmAndSaveAllModifiedDocuments() {
		IDtContainerEditView[] editors = getModifiedEditors();
		if (editors.length < 1) {
			// 変更されているドキュメントは存在しない
			return true;
		}
		
		// 確認ダイアログの表示
		StringBuilder sb = new StringBuilder();
		sb.append(DtContainerEditorMessages.getInstance().confirmSaveDocumentBeforeOperation);
		for (IDtContainerEditView ev : editors) {
			String et = ev.getDocumentTitle();
			sb.append("\n    '");
			sb.append(et);
			sb.append("'");
		}
		sb.append("\n" + DtContainerEditorMessages.getInstance().confirmSaveChanges);
		int ret = JOptionPane.showConfirmDialog(this, sb.toString(),
				DtContainerEditorMessages.getInstance().confirmTitleSaveModifiedDocuments,
				JOptionPane.OK_CANCEL_OPTION);
		if (ret != JOptionPane.OK_OPTION) {
			// user canceled
			return false;
		}
		
		// 変更されたドキュメントをすべて保存
		for (IDtContainerEditView ev : editors) {
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
	 * 指定されたデータバインダーの中に、交換代数要素を持つスリップオブジェクトが存在するかどうかを判定する。
	 * @param binder	データバインダー
	 * @return	存在する場合は <code>true</code>
	 * @since 0.5.2
	 */
	protected boolean containsAvailableExalgeElementInDataBinder(DtBinder binder) {
		if (binder != null) {
			for (Object slipobj : binder.getUnmodifiableObjects().values()) {
				if (slipobj instanceof DtSlip) {
					DtSlip slip = (DtSlip)slipobj;
					if (containsAvailableExalgeElementInSlip(slip)) {
						return true;
					}
				}
				else if (slipobj instanceof DtSlipList) {
					DtSlipList sliplist = (DtSlipList)slipobj;
					for (DtSlip slip : sliplist) {
						if (containsAvailableExalgeElementInSlip(slip)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * 指定されたデータスリップの中に、交換代数要素を持つデータオブジェクトが存在するかどうかを判定する。
	 * @param slip	データスリップ
	 * @return	存在する場合は <code>true</code>
	 * @since 0.5.2
	 */
	protected boolean containsAvailableExalgeElementInSlip(DtSlip slip) {
		if (slip != null) {
			for (Object dataobj : slip.getUnmodifiableObjects().values()) {
				if (dataobj instanceof Exalge) {
					Exalge alge = (Exalge)dataobj;
					if (!alge.isEmpty()) {
						return true;
					}
				}
				else if (dataobj instanceof ExAlgeSet) {
					ExAlgeSet algeset = (ExAlgeSet)dataobj;
					for (Exalge alge : algeset) {
						if (!alge.isEmpty()) {
							return true;
						}
					}
				}
			}
		}
		return false;
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
		if (command.startsWith(DtContainerEditorMenuResources.ID_FILE_MENU)) {
			if (DtContainerEditorMenuResources.ID_FILE_NEW_SLIP_SINGLE.equals(command)) {
				onSelectedMenuFileNewSlipSingle(action);
				return true;
			}
			else if (DtContainerEditorMenuResources.ID_FILE_NEW_BINDER.equals(command)) {
				onSelectedMenuFileNewBinder(action);
				return true;
			}
			else if (DtContainerEditorMenuResources.ID_FILE_OPEN.equals(command)) {
				onSelectedMenuFileOpen(action);
				return true;
			}
			else if (DtContainerEditorMenuResources.ID_FILE_OPENAS_SLIP_SINGLE.equals(command)) {
				onSelectedMenuFileOpenAsSlipSingle(action);
				return true;
			}
			else if (DtContainerEditorMenuResources.ID_FILE_OPENAS_BINDER.equals(command)) {
				onSelectedMenuFileOpenAsBinder(action);
				return true;
			}
			else if (DtContainerEditorMenuResources.ID_FILE_CLOSE.equals(command)) {
				onSelectedMenuFileClose(action);
				return true;
			}
			else if (DtContainerEditorMenuResources.ID_FILE_ALL_CLOSE.equals(command)) {
				onSelectedMenuFileAllClose(action);
				return true;
			}
			else if (DtContainerEditorMenuResources.ID_FILE_SAVE.equals(command)) {
				onSelectedMenuFileSave(action);
				return true;
			}
			else if (DtContainerEditorMenuResources.ID_FILE_SAVEAS.equals(command)) {
				onSelectedMenuFileSaveAs(action);
				return true;
			}
			else if (DtContainerEditorMenuResources.ID_FILE_IMPORT_DTBINDER_DESIMPLESLIPS_CSV.equals(command)) {
				onSelectedMenuFileImportDoubleEntrySimpleSlipsCsvAsBinder(action);
				return true;
			}
			else if (DtContainerEditorMenuResources.ID_FILE_EXPORT_DTBINDER_DESIMPLESLIPS_CSV.equals(command)) {
				onSelectedMenuFileExportBinderAsDoubleEntrySimpleSlipsCsv(action);
				return true;
			}
			else if (DtContainerEditorMenuResources.ID_FILE_PREFERENCE.equals(command)) {
				onSelectedMenuFilePreference(action);
				return true;
			}
			else if (DtContainerEditorMenuResources.ID_FILE_QUIT.equals(command)) {
				onSelectedMenuFileQuit(action);
				return true;
			}
		}
		else if (DtContainerEditorMenuResources.ID_HELP_ABOUT.equals(command)) {
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
		if (command.startsWith(DtContainerEditorMenuResources.ID_FILE_MENU)) {
			if (DtContainerEditorMenuResources.ID_FILE_NEW_SLIP_SINGLE.equals(command)) {
				action.setEnabled(true);
			}
			else if (DtContainerEditorMenuResources.ID_FILE_NEW_BINDER.equals(command)) {
				action.setEnabled(true);
			}
			else if (command.startsWith(DtContainerEditorMenuResources.ID_FILE_NEW)) {
				action.setEnabled(true);
			}
			else if (DtContainerEditorMenuResources.ID_FILE_OPEN.equals(command)) {
				action.setEnabled(true);
			}
			else if (DtContainerEditorMenuResources.ID_FILE_OPENAS_SLIP_SINGLE.equals(command)) {
				action.setEnabled(true);
			}
			else if (DtContainerEditorMenuResources.ID_FILE_OPENAS_BINDER.equals(command)) {
				action.setEnabled(true);
			}
			else if (command.startsWith(DtContainerEditorMenuResources.ID_FILE_OPENAS)) {
				action.setEnabled(true);
			}
			else if (DtContainerEditorMenuResources.ID_FILE_CLOSE.equals(command)) {
				action.setEnabled(getActiveEditor()!=null);
			}
			else if (DtContainerEditorMenuResources.ID_FILE_ALL_CLOSE.equals(command)) {
				action.setEnabled(!isEditorEmpty());
			}
			else if (DtContainerEditorMenuResources.ID_FILE_SAVE.equals(command)) {
				action.setEnabled(false);
			}
			else if (DtContainerEditorMenuResources.ID_FILE_SAVEAS.equals(command)) {
				action.setEnabled(false);
			}
			else if (DtContainerEditorMenuResources.ID_FILE_IMPORT_DTBINDER_DESIMPLESLIPS_CSV.equals(command)) {
				action.setEnabled(true);
			}
			else if (command.startsWith(DtContainerEditorMenuResources.ID_FILE_IMPORT_MENU)) {
				action.setEnabled(true);
			}
			else if (DtContainerEditorMenuResources.ID_FILE_EXPORT_DTBINDER_DESIMPLESLIPS_CSV.equals(command)) {
				action.setEnabled(false);
			}
			else if (command.startsWith(DtContainerEditorMenuResources.ID_FILE_EXPORT_MENU)) {
				action.setEnabled(true);
			}
			else if (DtContainerEditorMenuResources.ID_FILE_PREFERENCE.equals(command)) {
				action.setEnabled(true);
			}
			else if (DtContainerEditorMenuResources.ID_FILE_QUIT.equals(command)) {
				action.setEnabled(true);
			}
			else {
				action.setEnabled(false);
			}
			return true;
		}
		else if (command.startsWith(DtContainerEditorMenuResources.ID_EDIT_MENU)) {
			action.setEnabled(false);
			return true;
		}
		else if (command.startsWith(DtContainerEditorMenuResources.ID_HELP_MENU)) {
			action.setEnabled(true);
			return true;
		}
		else if (action != null) {
			action.setEnabled(false);
		}
		
		return false;
	}
	
	// menu : [File]-[New]-[Slip]
	protected void onSelectedMenuFileNewSlipSingle(Action action) {
		AppLogger.debug("menu [File]-[New]-[Slip] selected.");
		
		// create Slip editor
		IDtContainerEditController controller = _editControllerManager.getControllerById(DtSlipEditController.ControllerID);
		DtSlipEditView newEditor = (DtSlipEditView)controller.newDocument(null);
		addEditor(newEditor);
		updateAllMenuItems();
		setFocusToActiveEditor();
	}
	
	// menu : [File]-[New]-[Binder]
	protected void onSelectedMenuFileNewBinder(Action action) {
		AppLogger.debug("menu [File]-[New]-[Binder] selected.");
		
		// create Binder editor
		IDtContainerEditController controller = _editControllerManager.getControllerById(DtBinderEditController.ControllerID);
		DtBinderEditView newEditor = (DtBinderEditView)controller.newDocument(null);
		addEditor(newEditor);
		updateAllMenuItems();
		setFocusToActiveEditor();
	}

	// menu : [File]-[Open]
	protected void onSelectedMenuFileOpen(Action action) {
		AppLogger.debug("menu [File]-[Open] selected.");
		// choose file
		File initFile = DtContainerEditorSettings.getInstance().getLastFile(DtContainerEditorSettings.DOCUMENT);
		File targetFile = DtContainerFileChooserManager.chooseJsonDocument(this, initFile, null);
		if (targetFile == null) {
			// not selected
			setFocusToActiveEditor();
			return;
		}
		//--- store last file
		DtContainerEditorSettings.getInstance().setLastFile(DtContainerEditorSettings.DOCUMENT, targetFile);
		
		openEditorFromFile(targetFile);
	}
	
	// menu : [File]-[Open As]-[Slip]
	protected void onSelectedMenuFileOpenAsSlipSingle(Action action) {
		AppLogger.debug("menu [File]-[Open As]-[Slip] selected.");
		// choose file
		File initFile = DtContainerEditorSettings.getInstance().getLastFile(DtContainerEditorSettings.DOCUMENT);
		File targetFile = DtContainerFileChooserManager.chooseJsonDocument(this, initFile, null);
		if (targetFile == null) {
			// not selected
			setFocusToActiveEditor();
			return;
		}
		//--- store last file
		DtContainerEditorSettings.getInstance().setLastFile(DtContainerEditorSettings.DOCUMENT, targetFile);
		
		// exist opened?
		for (int i = 0; i < getEditorCount(); i++) {
			IDtContainerEditView editor = getEditor(i);
			File editorFile = editor.getDocumentFile();
			if (editorFile != null && editorFile.equals(targetFile)) {
				// すでに存在している場合は、そこにフォーカスを設定して終了
				_tabEditor.setSelectedIndex(i);
				setFocusToActiveEditor();
				return;
			}
		}
		
		// open file
		AppLogger.debug("open file as DtSlip from " + (targetFile==null ? "\'null\'" : targetFile.getAbsolutePath()));
		DtSlipEditController controller = (DtSlipEditController)_editControllerManager.getControllerById(DtSlipEditController.ControllerID);
		DtSlipEditView newEditor = null;
		try {
			newEditor = controller.openDocument(this, targetFile);
		}
		catch (FileNotFoundException ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_FILE_NOTFOUND, ex, targetFile.getAbsolutePath());
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
		}
		catch (UnsupportedEncodingException ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_FILE_UNSUPPORTED_ENCODING, ex);
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
		}
		catch (JSONException ex) {
			// Unsupported file format as JSON for Data-container
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_FILE_UNSUPPORTED_FORMAT, ex, targetFile.getAbsolutePath());
			AppLogger.error(errmsg);
			DtContainerEditor.showErrorMessage(this, errmsg);
		}
		catch (IOException ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_FILE_READ, ex, targetFile.getAbsolutePath());
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
		}
		catch (OutOfMemoryError ex) {
			String errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
		}
		catch (Throwable ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_UNEXPECTED, ex);
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
		}

		if (newEditor != null) {
			addEditor(newEditor);
			updateAllMenuItems();
			setFocusToActiveEditor();
		} else {
			setFocusToActiveEditor();
		}
	}
	
	// menu : [File]-[Open As]-[Binder]
	protected void onSelectedMenuFileOpenAsBinder(Action action) {
		AppLogger.debug("menu [File]-[Open As]-[Binder] selected.");
		// choose file
		File initFile = DtContainerEditorSettings.getInstance().getLastFile(DtContainerEditorSettings.DOCUMENT);
		File targetFile = DtContainerFileChooserManager.chooseJsonDocument(this, initFile, null);
		if (targetFile == null) {
			// not selected
			setFocusToActiveEditor();
			return;
		}
		//--- store last file
		DtContainerEditorSettings.getInstance().setLastFile(DtContainerEditorSettings.DOCUMENT, targetFile);
		
		// exist opened?
		for (int i = 0; i < getEditorCount(); i++) {
			IDtContainerEditView editor = getEditor(i);
			File editorFile = editor.getDocumentFile();
			if (editorFile != null && editorFile.equals(targetFile)) {
				// すでに存在している場合は、そこにフォーカスを設定して終了
				_tabEditor.setSelectedIndex(i);
				setFocusToActiveEditor();
				return;
			}
		}
		
		// open file
		AppLogger.debug("open file as DtBinder from " + (targetFile==null ? "\'null\'" : targetFile.getAbsolutePath()));
		DtBinderEditController controller = (DtBinderEditController)_editControllerManager.getControllerById(DtBinderEditController.ControllerID);
		DtBinderEditView newEditor = null;
		try {
			newEditor = controller.openDocument(this, targetFile);
		}
		catch (FileNotFoundException ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_FILE_NOTFOUND, ex, targetFile.getAbsolutePath());
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
		}
		catch (UnsupportedEncodingException ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_FILE_UNSUPPORTED_ENCODING, ex);
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
		}
		catch (JSONException ex) {
			// Unsupported file format as JSON for Data-container
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_FILE_UNSUPPORTED_FORMAT, ex, targetFile.getAbsolutePath());
			AppLogger.error(errmsg);
			DtContainerEditor.showErrorMessage(this, errmsg);
		}
		catch (IOException ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_FILE_READ, ex, targetFile.getAbsolutePath());
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
		}
		catch (OutOfMemoryError ex) {
			String errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
		}
		catch (Throwable ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_UNEXPECTED, ex);
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
		}

		if (newEditor != null) {
			addEditor(newEditor);
			updateAllMenuItems();
			setFocusToActiveEditor();
		} else {
			setFocusToActiveEditor();
		}
	}

	// menu : [File]-[Close]
	protected void onSelectedMenuFileClose(Action action) {
		AppLogger.debug("menu [File]-[Close] selected.");
		int tabidx = _tabEditor.getSelectedIndex();
		if (tabidx >= 0) {
			if (closeEditor(tabidx)) {
				updateAllMenuItems();
				updateActiveEditor();
			}
		}
		setFocusToActiveEditor();
	}

	// menu : [File]-[Close all]
	protected void onSelectedMenuFileAllClose(Action action) {
		AppLogger.debug("menu [File]-[Close All] selected.");
		closeAllEditors();
	}

	// menu : [File]-[Save]
	protected void onSelectedMenuFileSave(Action action) {
		AppLogger.debug("menu [File]-[Save] selected.");
		IDtContainerEditView editor = getActiveEditor();
		if (editor != null) {
			if (validateEditorDocument(editor)) {
				if (saveEditorDocument(editor)) {
					updateDisplayCurrentTab();
					updateAllMenuItems();
				}
			}
		}
		setFocusToActiveEditor();
	}

	// menu : [File]-[Save As]
	protected void onSelectedMenuFileSaveAs(Action action) {
		AppLogger.debug("menu [File]-[Save As] selected.");
		IDtContainerEditView editor = getActiveEditor();
		if (editor != null) {
			if (validateEditorDocument(editor)) {
				if (saveAsEditorDocument(editor)) {
					updateDisplayCurrentTab();
					updateAllMenuItems();
					//--- register & refresh tree
	///				viewTree.refreshFileTree(ModuleFileManager.fromJavaFile(editor.getDocumentFile()));
	///				refreshModuleProperties();
				}
			}
		}
		setFocusToActiveEditor();
	}
	
	// menu : [File]-[Import]-[Double-Entry Simple Slips CSV as DtBinder]
	protected void onSelectedMenuFileImportDoubleEntrySimpleSlipsCsvAsBinder(Action action) {
		AppLogger.debug("menu [File]-[Import]-[Double-Entry Simple Slips CSV as DtBinder] selected.");
		
		// ダイアログ表示 (import)
		DtBinderDoubleEntrySimpleSlipsCsvDialog dlg = new DtBinderDoubleEntrySimpleSlipsCsvDialog(this, false);
		dlg.initialComponent();
		dlg.setVisible(true);;
		dlg.dispose();
		if (dlg.getDialogResult() != IDialogResult.DialogResult_OK) {
			// user canceled
			setFocusToActiveEditor();
			return;
		}
		
		// ファイルの読み込み
		String strEncoding;
		File targetFile;
		//--- 貸借科目定義テーブルの読み込み
		DebitCreditItemDefinitionTable defTable = null;
		strEncoding = dlg.getDefTableCsvEncoding();
		targetFile = dlg.getDefTableCsvFile();
		try {
			defTable = DebitCreditItemDefinitionTable.fromCSV(targetFile, strEncoding);
		}
		catch (DebitCreditItemDefinitionTableCsvFormatError ex) {
			// 貸借科目定義CSVフォーマットエラー
			String errmsg = DtContainerEditorMessages.getInstance().msgFailedToReadDebitCreditDefTableCsvFile
							+ "\n    " + ex.getPositionString() + " " + ex.getMessage();
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
			setFocusToActiveEditor();
			return;
		}
		catch (FileNotFoundException ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_FILE_NOTFOUND, ex, targetFile.getAbsolutePath());
			errmsg = DtContainerEditorMessages.getInstance().msgFailedToReadDebitCreditDefTableCsvFile + "\n" + errmsg;
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
			setFocusToActiveEditor();
			return;
		}
		catch (UnsupportedEncodingException ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_FILE_UNSUPPORTED_ENCODING, ex);
			errmsg = DtContainerEditorMessages.getInstance().msgFailedToReadDebitCreditDefTableCsvFile + "\n" + errmsg;
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
			setFocusToActiveEditor();
			return;
		}
		catch (IOException ex) {
			String errmsg = DtContainerEditorMessages.formatErrorMessage(
					DtContainerEditorMessages.getInstance().msgFailedToReadDebitCreditDefTableCsvFile, ex);
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
			setFocusToActiveEditor();
			return;
		}
		catch (OutOfMemoryError ex) {
			String errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
			setFocusToActiveEditor();
			return;
		}
		catch (Throwable ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_UNEXPECTED, ex);
			errmsg = DtContainerEditorMessages.getInstance().msgFailedToReadDebitCreditDefTableCsvFile + "\n" + errmsg;
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
			setFocusToActiveEditor();
			return;
		}
		
		//--- 複式記述簡易データ伝票 CSV ファイルのインポート
		DtBinder newBinder = null;
		strEncoding = dlg.getTargetCsvEncoding();
		targetFile = dlg.getTargetCsvFile();
		try {
			newBinder = DtDoubleEntrySimpleSlipsCsvUtil.importBinderFromDoubleEntrySimpleSlipsCSV(targetFile, strEncoding, defTable);
		}
		catch (DtDoubleEntrySimpleSlipsCsvFormatError ex) {
			// 複式記述簡易データ伝票CSVフォーマットエラー
			String errmsg = DtContainerEditorMessages.getInstance().msgFailedToImportDoubleEntrySimpleSlipsCsvFile
							+ "\n    " + ex.getPositionString() + " " + ex.getMessage();
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
			setFocusToActiveEditor();
			return;
		}
		catch (FileNotFoundException ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_FILE_NOTFOUND, ex, targetFile.getAbsolutePath());
			errmsg = DtContainerEditorMessages.getInstance().msgFailedToImportDoubleEntrySimpleSlipsCsvFile + "\n" + errmsg;
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
			setFocusToActiveEditor();
			return;
		}
		catch (UnsupportedEncodingException ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_FILE_UNSUPPORTED_ENCODING, ex);
			errmsg = DtContainerEditorMessages.getInstance().msgFailedToImportDoubleEntrySimpleSlipsCsvFile + "\n" + errmsg;
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
			setFocusToActiveEditor();
			return;
		}
		catch (IOException ex) {
			String errmsg = DtContainerEditorMessages.formatErrorMessage(
					DtContainerEditorMessages.getInstance().msgFailedToImportDoubleEntrySimpleSlipsCsvFile, ex);
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
			setFocusToActiveEditor();
			return;
		}
		catch (OutOfMemoryError ex) {
			String errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
			setFocusToActiveEditor();
			return;
		}
		catch (Throwable ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_UNEXPECTED, ex);
			errmsg = DtContainerEditorMessages.getInstance().msgFailedToImportDoubleEntrySimpleSlipsCsvFile + "\n" + errmsg;
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
			setFocusToActiveEditor();
			return;
		}
		
		// エディタへの表示
		//--- create Binder editor
		DtBinderEditController controller = (DtBinderEditController)_editControllerManager.getControllerById(DtBinderEditController.ControllerID);
		DtBinderEditView newEditor = (DtBinderEditView)controller.newDocument(newBinder);
		addEditor(newEditor);
		updateAllMenuItems();
		setFocusToActiveEditor();
	}
	
	// menu : [File]-[Export]-[DtBinder as Double-Entry Simple Slips CSV]
	protected void onSelectedMenuFileExportBinderAsDoubleEntrySimpleSlipsCsv(Action action) {
		AppLogger.debug("menu [File]-[Export]-[DtBinder as Double-Entry Simple Slips CSV] selected.");

		// 対象のドキュメントを取得
		DtBinderDocument binderDocument = null;
		IDtContainerEditView editor = getActiveEditor();
		if (editor != null) {
			if (validateEditorDocument(editor)) {
				IDtContainerDocument document = editor.getDocument();
				if (document instanceof DtBinderDocument) {
					binderDocument = (DtBinderDocument)document;
				}
			}
		}
		if (binderDocument == null) {
			setFocusToActiveEditor();
			return;
		}
		
		// 編集中か判定
		if (binderDocument.isModified()) {
			// 編集中なら、確認
			int ret = JOptionPane.showConfirmDialog(this,
					DtContainerEditorMessages.getInstance().confirmExportEditingTarget,
					editor.getDocumentTitle(),
					JOptionPane.OK_CANCEL_OPTION);
			if (ret != JOptionPane.OK_OPTION) {
				// キャンセル
				setFocusToActiveEditor();
				return;
			}
		}
		
		// ドキュメントから DtBinder オブジェクトを生成
		DtBinder binderObject = null;
		try {
			binderObject = binderDocument.createDataObject();
		}
		catch (Throwable ex) {
			String errmsg = DtContainerEditorMessages.getInstance().msgCouldNotExportPresentBinder;
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
			setFocusToActiveEditor();
			return;
		}
		//--- 交換代数の要素が存在しない場合は、エラー
		if (binderObject == null || binderObject.isEmpty()) {
			// データバインダーに要素が存在しない
			String errmsg = DtContainerEditorMessages.getInstance().msgDataBinderEmpty;
			AppLogger.error(errmsg);
			DtContainerEditor.showErrorMessage(this, errmsg);
			setFocusToActiveEditor();
			return;
		}
		else if (!containsAvailableExalgeElementInDataBinder(binderObject)) {
			// 交換代数要素を持つ交換代数元が存在しない
			String errmsg = DtContainerEditorMessages.getInstance().msgDataBinderNotIncludeExalgeElements;
			AppLogger.error(errmsg);
			DtContainerEditor.showErrorMessage(this, errmsg);
			setFocusToActiveEditor();
			return;
		}
		
		// ダイアログ表示 (export)
		DtBinderDoubleEntrySimpleSlipsCsvDialog dlg = new DtBinderDoubleEntrySimpleSlipsCsvDialog(this, true);
		dlg.initialComponent();
		dlg.setVisible(true);;
		dlg.dispose();
		if (dlg.getDialogResult() != IDialogResult.DialogResult_OK) {
			// user canceled
			setFocusToActiveEditor();
			return;
		}
		
		// ファイルの読み込み
		String strEncoding;
		File targetFile;
		//--- 貸借科目定義テーブルの読み込み
		DebitCreditItemDefinitionTable defTable = null;
		strEncoding = dlg.getDefTableCsvEncoding();
		targetFile = dlg.getDefTableCsvFile();
		try {
			defTable = DebitCreditItemDefinitionTable.fromCSV(targetFile, strEncoding);
		}
		catch (DebitCreditItemDefinitionTableCsvFormatError ex) {
			// 貸借科目定義CSVフォーマットエラー
			String errmsg = DtContainerEditorMessages.getInstance().msgFailedToReadDebitCreditDefTableCsvFile
							+ "\n    " + ex.getPositionString() + " " + ex.getMessage();
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
			setFocusToActiveEditor();
			return;
		}
		catch (FileNotFoundException ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_FILE_NOTFOUND, ex, targetFile.getAbsolutePath());
			errmsg = DtContainerEditorMessages.getInstance().msgFailedToReadDebitCreditDefTableCsvFile + "\n" + errmsg;
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
			setFocusToActiveEditor();
			return;
		}
		catch (UnsupportedEncodingException ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_FILE_UNSUPPORTED_ENCODING, ex);
			errmsg = DtContainerEditorMessages.getInstance().msgFailedToReadDebitCreditDefTableCsvFile + "\n" + errmsg;
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
			setFocusToActiveEditor();
			return;
		}
		catch (IOException ex) {
			String errmsg = DtContainerEditorMessages.formatErrorMessage(
					DtContainerEditorMessages.getInstance().msgFailedToReadDebitCreditDefTableCsvFile, ex);
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
			setFocusToActiveEditor();
			return;
		}
		catch (OutOfMemoryError ex) {
			String errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
			setFocusToActiveEditor();
			return;
		}
		catch (Throwable ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_UNEXPECTED, ex);
			errmsg = DtContainerEditorMessages.getInstance().msgFailedToReadDebitCreditDefTableCsvFile + "\n" + errmsg;
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
			setFocusToActiveEditor();
			return;
		}
		
		// 複式記述簡易データ伝票 CSV ファイルへのエクスポート
		strEncoding = dlg.getTargetCsvEncoding();
		targetFile = dlg.getTargetCsvFile();
		try {
			DtDoubleEntrySimpleSlipsCsvUtil.exportAllSlipsToDoubleEntrySimpleSlipsCSV(binderObject, targetFile, strEncoding, defTable);
		}
		catch (FileNotFoundException ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_FILE_NOTFOUND, ex, targetFile.getAbsolutePath());
			errmsg = DtContainerEditorMessages.getInstance().msgFailedToExportDoubleEntrySimpleSlipsCsvFile + "\n" + errmsg;
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
		}
		catch (UnsupportedEncodingException ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_FILE_UNSUPPORTED_ENCODING, ex);
			errmsg = DtContainerEditorMessages.getInstance().msgFailedToExportDoubleEntrySimpleSlipsCsvFile + "\n" + errmsg;
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
		}
		catch (IOException ex) {
			String errmsg = DtContainerEditorMessages.formatErrorMessage(
					DtContainerEditorMessages.getInstance().msgFailedToExportDoubleEntrySimpleSlipsCsvFile, ex);
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
		}
		catch (OutOfMemoryError ex) {
			String errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
		}
		catch (Throwable ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_UNEXPECTED, ex);
			errmsg = DtContainerEditorMessages.getInstance().msgFailedToExportDoubleEntrySimpleSlipsCsvFile + "\n" + errmsg;
			AppLogger.error(errmsg, ex);
			DtContainerEditor.showErrorMessage(this, errmsg);
		}
		
		// 完了
		setFocusToActiveEditor();
		return;
	}

	// menu : [File]-[Preference]
	protected void onSelectedMenuFilePreference(Action action) {
		AppLogger.debug("menu [File]-[Preference] selected.");
		// TODO: Preference dialog
		/*
		PreferenceDialog dlg = new PreferenceDialog(this);
		dlg.setVisible(true);
		int ret = dlg.getDialogResult();
		AppLogger.debug("Dialog result : " + ret);
		if (ret == IDialogResult.DialogResult_OK) {
			updateFontBySettings();
		}
		setFocusToActiveEditor();
		/*** ***/
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
		// Show message with FALCON-SEED version
		String version_message = FSEnvironment.getInstance().title();
		if (!Strings.isNullOrEmpty(version_message)) {
			version_message = version_message + "\n - " + DtContainerEditor.LOCAL_VERSION;
		} else {
			version_message = DtContainerEditor.LOCAL_VERSION;
		}

		JOptionPane.showMessageDialog(this,
				version_message,
				DtContainerEditorMessages.getInstance().appMainTitle,
				JOptionPane.INFORMATION_MESSAGE);
		//--- set focus to this frame
		setFocusToActiveEditor();
	}
	
	/**
	 * 編集中のドキュメントデータに不正な値が含まれていないかを検証する。
	 * 不正な値が含まれている場合、エラー発生個所にフォーカスし、適切なエラーメッセージを表示する。
	 * @param editor	対象のエディタ
	 * @return	正常なら {@code true}、不正な値が含まれている場合は {@code false}
	 */
	protected boolean validateEditorDocument(IDtContainerEditView editor) {
		// 不正な値がないか検証
		try {
			editor.getDocument().validateContainerContentData();
			//--- 値は正常
			return true;
		}
		catch (DTCContentEditTableModelConversionError ex) {
			// 編集中のデータに不正な値が含まれている場合に、この例外がスローされる
			//--- 不正な値の位置を選択
			editor.visibleAndSelectInvalidValueCell(ex);
			//--- エラーメッセージを表示
			final String errmsg = ex.getMessage();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					Application.showErrorMessage(DtContainerEditorFrame.this, errmsg);
				}
			});
			return false;
		}
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
		
		// エディタビューがアクティブなら、そのメニューコマンドを処理
		IDtContainerEditView view = getActiveEditor();
		if (view != null) {
			//--- ビュー固有のハンドラ
			if (view.onProcessMenuSelection(pCommand, pSource, pAction)) {
				// process is terminate.
				return;
			}
			
			//--- コントローラー固有のハンドラ
			if (view.getEditController().onProcessMenuSelection(pCommand, pSource, pAction)) {
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
		
		// エディタビューがアクティブなら、そのメニューコマンドを処理
		IDtContainerEditView view = getActiveEditor();
		if (view != null) {
			//--- ビュー固有のハンドラ
			if (view.onProcessMenuUpdate(command, source, pAction)) {
				// process is terminate.
				return;
			}
			
			//--- コントローラー固有のハンドラ
			if (view.getEditController().onProcessMenuUpdate(command, source, pAction)) {
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
					DtContainerEditorMenuBar menuBar = getActiveEditorMenuBar();
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
			if (IDtContainerEditView.PROP_MODIFIED.equals(strName)) {
				Object objsrc = evt.getSource();
				if (objsrc instanceof IDtContainerEditView) {
					evaluateModifiedChanged((IDtContainerEditView)objsrc);
				}
				updateMenuItem(DtContainerEditorMenuResources.ID_FILE_SAVE);
			}
		}
		protected void evaluateModifiedChanged(IDtContainerEditView editor) {
			int tabIndex = _tabEditor.indexOfComponent(editor.getComponent());
			if (tabIndex >= 0) {
				String tabTitle = getEditorTabTitle(editor);
				_tabEditor.setTitleAt(tabIndex, tabTitle);
			}
		}
	}

	/*
	 * アクティブなビューが変更されたことを通知するイベント
	 *
	class EditorActiveViewChangeListener implements ActiveViewChangeListener {
		public void activeViewChanged(ActiveViewChangeEvent e) {
//			AppLogger.debug(e.toString());
			//--- メニューを更新
			updateAllMenuItems();
		}
	}
	/*---*/

	/*
	 * エディタタブのフォーカスが変更されたときのイベントハンドラ
	 * @deprecated 使用しない
	 *
	class EditorTabFocusHandler implements FocusListener {
		public void focusGained(FocusEvent e) {
			AppLogger.debug("Editor tab focus Gained!");
			IDtContainerEditView editor = getActiveEditor();
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
///				//--- 設定ファイル(*.prefs)を除くパスのみを収集
///				Set<File> fileset = new HashSet<File>();
///				for (Object elem : flist) {
///					if (elem instanceof File) {
///						targetFile = (File)elem;
///						if (!Strings.endsWithIgnoreCase(targetFile.getName(), ModuleFileManager.EXT_FILE_PREFS)) {
///							fileset.add(targetFile);
///						}
///					}
///				}
				
				// ファイルを開く
				int openCount = 0;
				loopFileList : for (Object elem : flist) {
					if (elem instanceof File) {
						targetFile = (File)elem;
						//--- 既に開かれているファイルか?
						targetFile = targetFile.getAbsoluteFile();
						for (int i = 0; i < getEditorCount(); i++) {
							IDtContainerEditView editor = getEditor(i);
							if (targetFile.equals(editor.getDocumentFile())) {
								// すでに存在している場合は、そこにフォーカスを設定して次のファイルへ
								_tabEditor.setSelectedIndex(i);
								continue loopFileList;
							}
						}
						//--- open file
						AppLogger.debug("open D&D file DtSlip/DtBinder from " + (targetFile==null ? "\'null\'" : targetFile.getAbsolutePath()));
						IDtContainerEditView newEditor = null;
						try {
							newEditor = _editControllerManager.openSupportedDocument(targetFile);
						}
						catch (JSONException ex) {
							// Unsupported file format as JSON for Data-container
							String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_FILE_UNSUPPORTED_FORMAT, ex, targetFile.getAbsolutePath());
							AppLogger.error(errmsg);
							DtContainerEditor.showErrorMessage(DtContainerEditorFrame.this, errmsg);
						}
						catch (FileNotFoundException ex) {
							String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_FILE_NOTFOUND, ex, targetFile.getAbsolutePath());
							AppLogger.error(errmsg, ex);
							DtContainerEditor.showErrorMessage(DtContainerEditorFrame.this, errmsg);
						}
						catch (IOException ex) {
							String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_FILE_READ, ex, targetFile.getAbsolutePath());
							AppLogger.error(errmsg, ex);
							DtContainerEditor.showErrorMessage(DtContainerEditorFrame.this, errmsg);
						}
						catch (OutOfMemoryError ex) {
							String errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
							AppLogger.error(errmsg, ex);
							DtContainerEditor.showErrorMessage(DtContainerEditorFrame.this, errmsg);
						}
						catch (Throwable ex) {
							String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_UNEXPECTED, ex);
							AppLogger.error(errmsg, ex);
							DtContainerEditor.showErrorMessage(DtContainerEditorFrame.this, errmsg);
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
///		viewTree.adjustPropertyDivider();
		
		// set Focus to Tree View
///		viewTree.requestFocusInComponent();
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
			DtContainerEditorSettings.flush();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		AppLogger.info("\n<<<<< Finished - " + DtContainerEditor.LOCAL_VERSION + " >>>>>\n");
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
