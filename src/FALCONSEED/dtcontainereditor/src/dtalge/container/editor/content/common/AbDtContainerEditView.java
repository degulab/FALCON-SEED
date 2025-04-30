/*
 * @(#)AbDtContainerEditView.java	2.0.0	2025/02/18
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbDtContainerEditView.java	1.1.0	2023/01/20
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbDtContainerEditView.java	1.0.0	2022/12/21
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.content.common;

import static ssac.util.Validations.validNotNull;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.DOMException;

import dtalge.DtAlgeSet;
import dtalge.Dtalge;
import dtalge.container.editor.DtContainerEditor;
import dtalge.container.editor.DtContainerEditorMessages;
import dtalge.container.editor.content.DtContainerContentTypes;
import dtalge.container.editor.content.DtContainerContentTypesManager;
import dtalge.container.editor.content.IDtContainerDocument;
import dtalge.container.editor.content.IDtContainerEditController;
import dtalge.container.editor.content.IDtContainerEditView;
import dtalge.container.editor.content.common.swing.DtContainerContentActiveViewManager;
import dtalge.container.editor.content.common.swing.DtContainerContentActiveViewManager.DtContainerContentActiveViewChangeEvent;
import dtalge.container.editor.content.common.swing.DtContainerContentActiveViewManager.DtContainerContentActiveViewChangeListener;
import dtalge.container.editor.content.common.swing.DtContainerContentDetailView;
import dtalge.container.editor.content.common.swing.DtContainerContentExportDialog;
import dtalge.container.editor.content.common.swing.DtContainerContentInsertDialog;
import dtalge.container.editor.content.common.swing.DtContainerContentNameValidator;
import dtalge.container.editor.content.common.swing.DtContainerContentReplaceDialog;
import dtalge.container.editor.content.common.table.AbDTCContentAlgeEditModel;
import dtalge.container.editor.content.common.table.AbDTCContentAlgeEditView;
import dtalge.container.editor.content.common.table.DTCContentAlgeEditTablePane;
import dtalge.container.editor.content.common.table.DTCContentDtalgeEditModel;
import dtalge.container.editor.content.common.table.DTCContentEditTableModelConversionError;
import dtalge.container.editor.content.common.tree.DTCContentTransferTreeData;
import dtalge.container.editor.content.common.tree.DTCContentTransferTreeNode;
import dtalge.container.editor.content.common.tree.DTCContentTreeSelectionInfo;
import dtalge.container.editor.content.common.tree.DtContainerContentParentTreeNode;
import dtalge.container.editor.content.common.tree.DtContainerContentTreeModel;
import dtalge.container.editor.content.common.tree.DtContainerContentTreePane;
import dtalge.container.editor.content.common.tree.IDtContainerContentTreeNode;
import dtalge.container.editor.menu.DtContainerEditorMenuBar;
import dtalge.container.editor.menu.DtContainerEditorMenuResources;
import dtalge.container.editor.view.DtContainerEditorFrame;
import dtalge.json.DtJSON;
import exalge2.ExAlgeSet;
import exalge2.Exalge;
import ssac.aadl.common.CommonMessages;
import ssac.util.logging.AppLogger;
import ssac.util.swing.Application;
import ssac.util.swing.IDialogResult;
import ssac.util.swing.InputDialog;
import ssac.util.swing.SwingTools;

/**
 * データコンテナの編集用GUIを提供するビューの共通実装。
 * 
 * @version 2.0.0
 */
public abstract class AbDtContainerEditView<D extends IDtContainerDocument> extends JSplitPane implements IDtContainerEditView
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	
	/** このビュー内のツリービューの最小幅 **/
	static public final int	TREEVIEW_MIN_WIDTH	= 200;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** このビューの編集対象となるドキュメント **/
	protected D		_document;
	
	/** 現在アクティブなビューを管理するマネージャー **/
	protected final DtContainerContentActiveViewManager	_activeViewManager;
	
	protected final Map<String,Action>	_menuActionMap = new HashMap<String,Action>();
	
	/** スクロールバーを含む、ツリーコンポーネントを格納するツリービュー **/
	protected JPanel						_viewTree;
	/** 詳細ビュー **/
	protected DtContainerContentDetailView	_viewDetails;
	
	/** データコンテナの内部構造を示すツリーコンポーネント用のツールバー **/
	protected JToolBar		_treeToolBar;
	/** データコンテナの内部構造を示すツリーコンポーネント **/
	protected DtContainerContentTreePane	_treeComponent;
	/** データコンテナ要素のツリーの選択ノードの情報を保持するオブジェクト **/
	protected DTCContentTreeSelectionInfo	_treeSelectionInfo = new DTCContentTreeSelectionInfo();

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AbDtContainerEditView(D document) {
		super(JSplitPane.HORIZONTAL_SPLIT, false);
		this.setResizeWeight(0);
		
		// active view manager
		_activeViewManager = new DtContainerContentActiveViewManager();
		
		// setup tree view
		_viewTree = new JPanel(new BorderLayout());
		Dimension dmSize = _viewTree.getMinimumSize();
		dmSize.width = Math.max(dmSize.width, TREEVIEW_MIN_WIDTH);
		_viewTree.setMinimumSize(dmSize);
		JScrollPane scTree = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		//_treeToolBar = createContentTreeToolBar();
		_treeToolBar = ((DtContainerEditorFrame)DtContainerEditor.getApplicationMainFrame()).getDefaultEditorMenuBar().createContentTreeToolBar();
		JLabel lblRoot = (JLabel)_treeToolBar.getComponent(0);
		lblRoot.setText(" " + document.getRootContentName());
		_treeComponent = createContentTreeComponent();
		scTree.setViewportView(_treeComponent);
		_viewTree.add(_treeToolBar, BorderLayout.NORTH);
		_viewTree.add(scTree, BorderLayout.CENTER);
		
		// setup detail view
		_viewDetails = new DtContainerContentDetailView(this);
		_viewDetails.initialComponent();
//		_detailOuterPanel = new JPanel(new BorderLayout());
//		_detailOuterPanel.add(_viewDetails, BorderLayout.CENTER);
		
		// setup content editor tab
		//_tabContentEditor = createContentEditorTab();
		//_nodeDetailView.add(_tabContentEditor, BorderLayout.CENTER);
		//_tabContentEditor.setVisible(false);	// タブが空のときは非表示
		
		// register views for Active view manager
		_activeViewManager.registerComponent(_viewTree);
		_activeViewManager.registerComponent(_viewDetails);
		_activeViewManager.addActiveViewChangeListener( new DtContainerContentActiveViewChangeListener() {
			public void activeViewChanged(DtContainerContentActiveViewChangeEvent e) {
				onChangedActiveView(e);
			}
		} );
		
		// setup layouts
		this.setLeftComponent(_viewTree);
//		this.setRightComponent(_detailOuterPanel);
		this.setRightComponent(_viewDetails);
		
		// メニューアクセラレータキーを有効にするため、ツリーコンポーネントのマップを変更
		KeyStroke[] strokes = SwingTools.getMenuAccelerators(getFrame().getActiveEditorMenuBar());
		_treeComponent.registMenuAcceleratorKeyStroke(strokes);
		
		// set document
		setDocument(document);
//		_viewDetails.setVisible(false);
	}

//	/**
//	 * @deprecated
//	 */
//	private JTabbedPane createContentEditorTab() {
//		// create component
//		JTabbedPane tab = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
//		//tab.setBackground(Color.red);
//		tab.setFocusable(false);
//
//		// completed
//		return tab;
//	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このビューに新しいドキュメントを関連付ける。
	 * 
	 * @param newDocument	新しいドキュメント
	 * 
	 * @throws NullPointerException	<code>newDocument</code> が <tt>null</tt> の場合
	 */
	public void setDocument(D newDocument) {
		validNotNull(newDocument);
		
		D oldDocument = _document;
		if (oldDocument == newDocument) {
			// same instance
			return;
		}
		
		// detach old document
		_viewDetails.setTargetTreeNode(null);
		if (oldDocument != null) {
			detachDocument(oldDocument);
		}
		/*
		paneLine.detachOwnerText();
		paneText.removeFocusListener(hTextFocus);
		paneText.removeCaretListener(hCaret);
		if (oldDocument != null) {
			oldDocument.removeDocumentListener(hDocument);
			oldDocument.removeUndoableEditListener(hUndo);
			
			// release document resources
			oldDocument.releaseViewResources();
		}
		*/
		
		// attach new document
		_document = newDocument;
		//DtContainerContentTreeModel newTreeModel = createContentTreeModel(newDocument);
		//_treeComponent.setModel(newTreeModel);
		_treeComponent.setModel(newDocument.getDataTreeModel());
		_treeSelectionInfo.clear();
		attachDocument(newDocument);
		updateEditorModifiedProperty();
		/*
		textDocument = newDocument;
		paneText.setDocument(newDocument);
		paneText.setTabSize(defaultTabSize);
		paneLine.attachOwnerText(paneText);
		paneText.addCaretListener(hCaret);
		paneText.addFocusListener(hTextFocus);
		newDocument.addDocumentListener(hDocument);
		newDocument.addUndoableEditListener(hUndo);
		*/
		
		// refresh
		refreshEditingStatus();
	}
	
	protected void detachDocument(D document) {
		
	}
	
	protected void attachDocument(D document) {
		
	}

	//------------------------------------------------------------
	// Implement ssac.util.swing.menu.IMenuActionHandler interfaces
	//------------------------------------------------------------
	
	protected void onChangedActiveView(DtContainerContentActiveViewChangeEvent avce) {
		// Tree view
		getFrame().getActiveEditorMenuBar().updateAllMenuItems();
	}
	
	/**
	 * メニュー項目の選択時に呼び出されるハンドラ・メソッド。
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
		if (_activeViewManager.isComponentActivated(_viewTree)) {
			// ツリービューがアクティブ
			switch (command) {
				case DtContainerEditorMenuResources.ID_TREE_ADD:
					onSelectedMenuContentTreeEditAdd(action);
					return true;
				case DtContainerEditorMenuResources.ID_TREE_REPLACE:
					onSelectedMenuContentTreeEditReplace(action);
					return true;
				case DtContainerEditorMenuResources.ID_TREE_EXPORT:
					onSelectedMenuContentTreeEditExport(action);
					return true;
				case DtContainerEditorMenuResources.ID_TREE_CUT:
					onSelectedMenuContentTreeEditCut(action);
					return true;
				case DtContainerEditorMenuResources.ID_TREE_COPY:
					onSelectedMenuContentTreeEditCopy(action);
					return true;
				case DtContainerEditorMenuResources.ID_TREE_PASTE:
					onSelectedMenuContentTreeEditPaste(action);
					return true;
				case DtContainerEditorMenuResources.ID_TREE_DELETE:
					onSelectedMenuContentTreeEditDelete(action);
					return true;
				case DtContainerEditorMenuResources.ID_TREE_RENAME:
					onSelectedMenuContentTreeEditRename(action);
					return true;
				case DtContainerEditorMenuResources.ID_TREE_MOVE_UP:
					onSelectedMenuContentTreeEditMoveUp(action);
					return true;
				case DtContainerEditorMenuResources.ID_TREE_MOVE_DOWN:
					onSelectedMenuContentTreeEditMoveDown(action);
					return true;
				case DtContainerEditorMenuResources.ID_EDIT_CUT:
					onSelectedMenuContentTreeEditCut(action);
					return true;
				case DtContainerEditorMenuResources.ID_EDIT_COPY:
					onSelectedMenuContentTreeEditCopy(action);
					return true;
				case DtContainerEditorMenuResources.ID_EDIT_PASTE:
					onSelectedMenuContentTreeEditPaste(action);
					return true;
				case DtContainerEditorMenuResources.ID_EDIT_DELETE:
					onSelectedMenuContentTreeEditDelete(action);
					return true;
				default:
					return false;
			}
		}
		else if (_activeViewManager.isComponentActivated(_viewDetails)) {
			// 詳細ビューがアクティブ
			return _viewDetails.onProcessMenuSelection(command, source, action);
		}
		else {
			// アクティブなビューは存在しない
			return false;
		}
	}
	
	/**
	 * メニュー項目の更新要求時に呼び出されるハンドラ・メソッド。
	 * 現時点では、このメソッドでは処理を行わないため、常に {@code false} を返す。
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
		// 共通の処理
		switch (command) {
			case DtContainerEditorMenuResources.ID_FILE_SAVE:
				action.setEnabled(isModified());	// 保存は変更時のみ
				return true;
			case DtContainerEditorMenuResources.ID_FILE_SAVEAS:
				action.setEnabled(true);	// 名前を付けて保存は、常にアクティブ
				return true;
			case DtContainerEditorMenuResources.ID_TREE_ADD:
				action.setEnabled(canTreeNodeAdd());
				return true;
			case DtContainerEditorMenuResources.ID_TREE_REPLACE:
				action.setEnabled(canTreeNodeReplace());
				return true;
			case DtContainerEditorMenuResources.ID_TREE_EXPORT:
				action.setEnabled(canTreeNodeExport());
				return true;
			//case DtContainerEditorMenuResources.ID_TREE_DELETE:
			//	action.setEnabled(canTreeNodeDelete());
			//	return true;
			case DtContainerEditorMenuResources.ID_TREE_RENAME:
				action.setEnabled(canTreeNodeRename());
				return true;
			case DtContainerEditorMenuResources.ID_TREE_MOVE_UP:
				action.setEnabled(canTreeNodeMoveUp());
				return true;
			case DtContainerEditorMenuResources.ID_TREE_MOVE_DOWN:
				action.setEnabled(canTreeNodeMoveDown());
				return true;
		}
		
		// アクティブなビューごとの処理
		if (_activeViewManager.isComponentActivated(_viewTree)) {
			// ツリービューがアクティブ
			switch (command) {
				case DtContainerEditorMenuResources.ID_TREE_CUT:
					action.setEnabled(canTreeNodeCut());
					return true;
				case DtContainerEditorMenuResources.ID_TREE_COPY:
					action.setEnabled(canTreeNodeCopy());
					return true;
				case DtContainerEditorMenuResources.ID_TREE_PASTE:
					action.setEnabled(canTreeNodePaste());
					return true;
				case DtContainerEditorMenuResources.ID_TREE_DELETE:
					action.setEnabled(canTreeNodeDelete());
					return true;
				case DtContainerEditorMenuResources.ID_EDIT_CUT:
					action.setEnabled(canTreeNodeCut());
					return true;
				case DtContainerEditorMenuResources.ID_EDIT_COPY:
					action.setEnabled(canTreeNodeCopy());
					return true;
				case DtContainerEditorMenuResources.ID_EDIT_PASTE:
					action.setEnabled(canTreeNodePaste());
					return true;
				case DtContainerEditorMenuResources.ID_EDIT_DELETE:
					action.setEnabled(canTreeNodeDelete());
					return true;
				default:
					return false;
			}
		}
		else if (_activeViewManager.isComponentActivated(_viewDetails)) {
			// 詳細ビューがアクティブ
			return _viewDetails.onProcessMenuUpdate(command, source, action);
		}
		else {
			// アクティブなビューは存在しない
			return false;
		}
	}
	
	protected boolean canTreeNodeAdd() {
		if (_treeComponent.getSelectionCount() == 1) {
			IDtContainerContentTreeNode ndSelected = (IDtContainerContentTreeNode)_treeComponent.getSelectionPath().getLastPathComponent();
			if (ndSelected.getParent() != null && DtContainerContentTypesManager.getInstance().isAllowAddChild(ndSelected.getContentType())) {
				return true;
			}
		}
		else if (_treeComponent.getSelectionCount() == 0) {
			// 何も選択されていない状態では、追加不可
			return false;
		}
		// not allow
		return false;
	}
	
	protected boolean canTreeNodeReplace() {
		if (_treeComponent.getSelectionCount() == 1) {
			IDtContainerContentTreeNode ndSelected = (IDtContainerContentTreeNode)_treeComponent.getSelectionPath().getLastPathComponent();
			if (ndSelected.getParent() != null && DtContainerContentTypesManager.getInstance().isAllowReplace(ndSelected.getContentType())) {
				return true;
			}
		}
		// not allow
		return false;
	}
	
	protected boolean canTreeNodeExport() {
		if (_treeComponent.getSelectionCount() == 1) {
			IDtContainerContentTreeNode ndSelected = (IDtContainerContentTreeNode)_treeComponent.getSelectionPath().getLastPathComponent();
			if (ndSelected.getParent() != null && DtContainerContentTypesManager.getInstance().isAllowExport(ndSelected.getContentType())) {
				return true;
			}
		}
		// not allow
		return false;
	}
	
	protected boolean canTreeNodeCut() {
		return _treeSelectionInfo.canCut();
	}
	
	protected boolean canTreeNodeCopy() {
		return _treeSelectionInfo.canCopy();
	}
	
	protected boolean canTreeNodePaste() {
		// 単一選択以外は、不可
		if (_treeComponent.getSelectionCount() != 1)
			return false;
		
		// クリップボードの情報を判定
		Transferable trans = SwingTools.getSystemClipboardContents(_treeComponent);
		if (trans == null)
			return false;	// no clipboard contents
		
		// 貼り付け位置の判定
		TreePath selectedPath = _treeComponent.getSelectionPath();
		IDtContainerContentTreeNode ndSelected = (IDtContainerContentTreeNode)selectedPath.getLastPathComponent();
		if (ndSelected.getParent() == null)
			return false;	// ルートノードは対象外
		DTCContentTransferTreeData clipdata = DTCContentTransferTreeData.getDataFromTransferable(trans);
		if (clipdata == null)
			return false;	// no supported data
		if (clipdata.isEmpty())
			return false;	// 転送データなし
		return clipdata.isAllowPasteTo(ndSelected);
	}
	
	protected boolean canTreeNodeDelete() {
		return _treeSelectionInfo.canDelete();
	}
	
	protected boolean canTreeNodeRename() {
		if (_treeComponent.getSelectionCount() == 1) {
			IDtContainerContentTreeNode ndSelected = (IDtContainerContentTreeNode)_treeComponent.getSelectionPath().getLastPathComponent();
			IDtContainerContentTreeNode ndParent = ndSelected.getParent();
			if (ndParent != null) {
				DtContainerContentTypes parentType = ndParent.getContentType();
				if (parentType == DtContainerContentTypes.ContentDtSlipObjects || parentType == DtContainerContentTypes.ContentDtBinderSlips) {
					return true;
				}
			}
		}
		// not allow
		return false;
	}
	
	protected boolean canTreeNodeMoveUp() {
		// not allow
		return false;
	}
	
	protected boolean canTreeNodeMoveDown() {
		// not allow
		return false;
	}

	//------------------------------------------------------------
	// Implement IDtContainerEditView interfaces
	//------------------------------------------------------------

	/**
	 * このビューオブジェクトに関連付けられている全てのリソースを開放する。
	 */
	public void destroy() {
		// TODO: destroy
	}

	/**
	 * このドキュメントの保存先ファイルが読み取り専用の場合に <code>true</code> を返す。
	 * ファイルそのものが読み取り専用ではない場合でも、モジュールパッケージに
	 * 含まれるファイルの場合にも <code>true</code> を返す。
	 */
	public boolean isReadOnly() {
		return getFrame().isReadOnlyFile(getDocumentFile());
	}
	
	/**
	 * このドキュメントが編集されているかを判定する。
	 * @return	編集されていれば <tt>true</tt> を返す。
	 */
	public boolean isModified() {
		if (_document != null) {
			Boolean value = (Boolean)getClientProperty(IDtContainerEditView.PROP_MODIFIED);
			return (value != null ? value.booleanValue() : false);
		}
		else {
			return false;
		}
	}
	
	/**
	 * このビューのドキュメントの変更状態に合わせて、変更を管理するプロパティを更新する。
	 * プロパティの値が変更されると、{@link IDtContainerEditView#PROP_MODIFIED} プロパティ変更イベントが発生する。
	 */
	public void updateEditorModifiedProperty() {
		/**
		if (_undoMan.canUndo()) {
			setEditorModifiedProperty(true);
		} else if (_tableDocument != null) {
			setEditorModifiedProperty(_tableDocument.isNewDocument());
		} else {
			setEditorModifiedProperty(false);
		}
		/*---*/
		if (_document != null) {
			setEditorModifiedProperty(_document.isModified());
		} else {
			setEditorModifiedProperty(false);
		}
	}
	
	/**
	 * このビューに関連付けられているドキュメントのタイトルを取得する。
	 * ここで取得されたタイトル文字列は、エディタフレームのタイトル、
	 * エディタタブのテキストとして使用される。
	 * @return	タイトル文字列
	 */
	public String getDocumentTitle() {
		return _document.getTitle();
	}
	
	/**
	 * このビューに関連付けられているドキュメントの保存先ファイルを取得する。
	 * 保存先ファイルが定義されていない場合は <tt>null</tt> を返す。
	 * @return	ドキュメントの保存先ファイルを返す。保存先ファイルが設定されて
	 * 			いない場合は <tt>null</tt> を返す。
	 */
	public File getDocumentFile() {
		return _document.getTargetFile();
	}
	
	/**
	 * このビューに関連付けられているドキュメントの保存先ファイルのフルパスを取得する。
	 * ここで取得された文字列は、ドキュメントの正式名称として、
	 * エディタフレームやエディタタブのツールチップとして使用される。
	 * @return	ドキュメント保存先ファイルのフルパスを返す。保存先ファイルが
	 * 			設定されていない場合は <tt>null</tt> を返す。
	 */
	public String getDocumentPath() {
		File targetFile = getDocumentFile();
		return (targetFile==null ? null : targetFile.getAbsolutePath());
	}
	
	/**
	 * このビューに関連付けられているドキュメントを取得する。
	 * @return	このビューに関連付けられているドキュメント	
	 */
	public D getDocument() {
		return _document;
	}
	
	/**
	 * このビューに関連付けられたドキュメントの設定情報を、
	 * 最新の情報に更新する。
	 * @return	更新された場合に <tt>true</tt> を返す。
	 */
	public boolean refreshDocumentSettings() {
		return false;
	}
	
	/**
	 * このビューのコンポーネントを取得する。
	 * このメソッドは基本的に <code>this</code> インスタンスを返す。
	 * @return	このビューのコンポーネントオブジェクト
	 */
	public JComponent getComponent() {
		return this;
	}
	
	/**
	 * このビューのコンポーネントがフォーカスを保持しているかを判定する。
	 * ビューが複数のコンポーネントを持つ場合、フォーカスを保持するべき
	 * コンポーネントにフォーカスがあれば <tt>true</tt> を返す。
	 * @return	コンポーネントがフォーカスを所持していれば <tt>true</tt>
	 */
	public boolean hasFocusInComponent() {
		return false;
	}
	
	/**
	 * このビューのコンポーネントにフォーカスを要求する。
	 * ビューが複数のコンポーネントを持つ場合、標準となる
	 * コンポーネントにフォーカスを設定する。
	 */
	public void requestFocusInComponent() {
		setFocusToActiveView();
	}
	
	/**
	 * このビューを格納するエディタフレームを取得する。
	 * @return	このビューを格納するエディタフレームのインスタンス。
	 * 			このビューがフレームに格納されていない場合は <tt>null</tt> を返す。
	 */
	public DtContainerEditorFrame getFrame() {
		return (DtContainerEditorFrame)DtContainerEditor.getApplicationMainFrame();
		
		//Window parentFrame = SwingUtilities.windowForComponent(this);
		//if (parentFrame instanceof DtContainerEditorFrame)
		//	return (DtContainerEditorFrame)parentFrame;
		//else
		//	return null;
	}
	
	/**
	 * このドキュメントを管理するエディット・コントローラーを返す。
	 * @return	エディット・コントローラー
	 */
	public IDtContainerEditController getEditController() {
		return _document.getEditCotnroller();
	}
	
	/**
	 * このビューに関連付けられたドキュメント専用のメニューバーを返す。
	 * 専用メニューバーが未定義の場合は <tt>null</tt> を返す。
	 * @return	ドキュメント専用メニューバー
	 */
	public DtContainerEditorMenuBar getDocumentMenuBar() {
		return null;	// 標準メニューバーを使用
	}
	
	/**
	 * このエディタビューのフォント変更要求を処理する。
	 * このメソッドは、{@link IDtContainerEditController} から呼び出される。
	 * @param controller	このメソッドの呼び出し元となるマネージャ
	 * @param font	新しいエディタフォント
	 */
	public void onChangedEditorFont(IDtContainerEditController controller, Font font) {
		// TODO: 
	}

	/**
	 * ドキュメントの保存先ファイルが移動可能かを判定する。
	 * @return	移動可能な場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean canMoveDocumentFile() {
		return _document.canMoveTargetFile();
	}
	
	/**
	 * ドキュメントに適用されたエンコーディングとなる文字セット名を返す。
	 * このメソッドの実装では <tt>null</tt> を返してはならない。
	 */
	public String getLastEncodingName() {
		return _document.getLastEncodingName();
	}
	
	/**
	 * ドキュメントのソースファイルの内容がすべてキャッシュされているかを判定する。
	 * キャッシュされている場合は、ソースファイルが変更されていても表示内容の
	 * 影響を受けない。
	 * @return	キャッシュされている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean cachedDocumentFromSourceFile() {
		return false;
	}
	
	/**
	 * ドキュメントの内容をソースファイルから再読込する。
	 * 再読込時の設定は、ドキュメント読込時点の設定と同じ内容とする。
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public void refreshDocumentFromSourceFile() throws IOException
	{
		// no implement
	}
	
	/**
	 * エンコーディングを指定してドキュメントを開きなおす操作を許可するかを判定する。
	 * @return 許可する場合は <tt>true</tt>、そうでない場合は <tt>false</tt> を返す。
	 */
	public boolean canReopen() {
		return false;
	}
	
	/**
	 * 指定されたエンコーディングでドキュメントを開きなおす。
	 * この操作では、編集状態は破棄され、<em>newEncoding</em> を適用して
	 * ファイルから読み込む。
	 * 新規ドキュメントの場合は、何もしない。
	 * @param newEncoding	ファイル読み込み時に適用するエンコーディング名を指定する。
	 * 						標準のエンコーディングを適用する場合は <tt>null</tt> を指定する。
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public void reopenDocument(String newEncoding) throws IOException
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * このビューに関連付けられたドキュメントの状態から、
	 * このエディタの編集状態を更新する。
	 */
	public void refreshEditingStatus() {
		updateEditorModifiedProperty();
	}

	/**
	 * <em>cause</em> が示す値をビューで表示し、選択する。
	 * @param cause	エラー情報を保持する {@link DTCContentEditTableModelConversionError} 例外オブジェクト
	 */
	public void visibleAndSelectInvalidValueCell(DTCContentEditTableModelConversionError cause) {
		if (cause == null)
			return;		// cause が null なら、何もしない
		
		// 対象のツリーノードを選択
		IDtContainerContentTreeNode ndTarget = cause.getErrorTreeNode();
		if (ndTarget == null)
			return;		// 不正な値を保持するデータのツリーノードが null なら、何もしない
		if (!ndTarget.isNodeAncestor(getDocument().getDataTreeRootNode()))
			return;		// 不正な値を保持するデータのツリーノードが、このドキュメントのツリーノード配下ではない場合、何もしない
		TreePath tpath = new TreePath(getDocument().getDataTreeModel().getPathToRoot(ndTarget));
		_treeComponent.expandPath(tpath.getParentPath());		// 親ノードを展開
		_treeComponent.setSelectionPath(tpath);					// ノードを選択
		
		// 対象のオブジェクトを表示
		Object objNodeData = ndTarget.getUserObject();
		if (objNodeData instanceof AbDTCContentAlgeEditModel) {
			// 代数編集用ビューを表示
			_viewDetails.setTargetTreeNode(ndTarget);
			AbDTCContentAlgeEditView editview = _viewDetails.getActiveAlgeEditView();
			if (editview != null) {
				DTCContentAlgeEditTablePane table = editview.getTableComponent();
				int rowIndex = table.convertRowIndexToView(cause.getRowIndexInTableModel());
				int colIndex = table.convertColumnIndexToView(cause.getColumnIndexInTableModel());
				table.setCellSelected(rowIndex, colIndex);
				table.setFocus();
			}
		}
//		if (objNodeData instanceof DTCContentExalgeEditModel) {
//			// ノードの情報表示
//			_viewDetails.setTargetTreeNode(ndTarget);
//			int rowIndex = _viewDetails.getDtalgeEditView().getTableComponent().convertRowIndexToView(cause.getRowIndexInTableModel());
//			int colIndex = _viewDetails.getDtalgeEditView().getTableComponent().convertColumnIndexToView(cause.getColumnIndexInTableModel());
//			_viewDetails.getDtalgeEditView().getTableComponent().setCellSelected(rowIndex, colIndex);
//			_viewDetails.getDtalgeEditView().getTableComponent().setFocus();
//		}
//		else if (objNodeData instanceof DTCContentDtalgeEditModel) {
//			// ノードの情報表示
//			_viewDetails.setTargetTreeNode(ndTarget);
//			int rowIndex = _viewDetails.getDtalgeEditView().getTableComponent().convertRowIndexToView(cause.getRowIndexInTableModel());
//			int colIndex = _viewDetails.getDtalgeEditView().getTableComponent().convertColumnIndexToView(cause.getColumnIndexInTableModel());
//			_viewDetails.getDtalgeEditView().getTableComponent().setCellSelected(rowIndex, colIndex);
//			_viewDetails.getDtalgeEditView().getTableComponent().setFocus();
//		}
		else {
			// 上記以外のデータモデルでは表示しない
			_treeComponent.setFocus();
		}
	}

	//------------------------------------------------------------
	// Menu Event handlers
	//------------------------------------------------------------
	
	protected void setFocusToActiveView() {
		if (_activeViewManager.isComponentActivated(_viewTree)) {
			// ツリービューがアクティブ
			_treeComponent.setFocus();
		}
		else if (_activeViewManager.isComponentActivated(_viewDetails)) {
			// 詳細ビューがアクティブ
			_viewDetails.requestFocusInComponent();
		}
		else {
			// アクティブなビューが設定されていない場合は、ツリービューにする
			_treeComponent.setFocus();
		}
	}

	/**
	 * コンテントツリー専用メニューの [Add] が選択されたときに呼び出されるイベントハンドラー。
	 * @param menuItemAction	選択されたメニューアイテムアクション
	 */
	protected void onSelectedMenuContentTreeEditAdd(Action menuItemAction) {
		AppLogger.debug("menu ID_TREE_ADD selected.");
		
		// 単一選択以外は、処理しない
		int initialInsertPosition = (-1);
		DtContainerContentParentTreeNode ndParent;
		if (_treeComponent.getSelectionCount() == 1) {
			// 単一選択
			IDtContainerContentTreeNode ndSelected = (IDtContainerContentTreeNode)_treeComponent.getSelectionPath().getLastPathComponent();
			DtContainerContentTypes selectedType = ndSelected.getContentType();
			if (ndSelected.getParent() == null) {
				// ルートノードが選択されている場合は、処理しない
				return;
			}
			else if (ndSelected.isLeaf()) {
				// 葉ノードの場合は、Exalge, Dtalge, ノートのいずれか
				if (selectedType == DtContainerContentTypes.ContentDtSlipNote || selectedType == DtContainerContentTypes.ContentDtBinderNote) {
					// ノートの新規追加は不可
					return;
				}
				//--- 親ノードが追加先
				ndParent = (DtContainerContentParentTreeNode)ndSelected.getParent();
				if (DtContainerContentTypesManager.getInstance().isIndexedChildren(ndParent.getContentType())) {
					// インデックス型のノード追加なら、選択位置が挿入位置
					initialInsertPosition = ndSelected.getNodePosition();
				}
			}
			else if (selectedType == DtContainerContentTypes.ContentDtSlip) {
				// スリップが選択されている場合は、親ノードが DtSlipList なので、それを選択
				ndParent = (DtContainerContentParentTreeNode)ndSelected.getParent();
				if (DtContainerContentTypesManager.getInstance().isIndexedChildren(ndParent.getContentType())) {
					// インデックス型のノード追加なら、選択位置が挿入位置
					initialInsertPosition = ndSelected.getNodePosition();
				}
			}
			else {
				// 葉ノードではない場合は、選択されたノードが追加先親ノード
				ndParent = (DtContainerContentParentTreeNode)ndSelected;
			}
		}
		else if (_treeComponent.getSelectionCount() == 0) {
			//// 選択なしの場合は、ルートノード直下の名前付きオブジェクトの親ノード
			//ndParent = (DtContainerContentParentTreeNode)getDocument().getDataTreeRootNode().getChildAt(1);
			// 単一選択以外は、追加不可
			return;
		}
		else {
			// 追加不可
			return;
		}
		
		// 子ノードの型判定
		boolean forSlipObject = DtContainerContentTypesManager.getInstance().isAllowStoreSlipObjectChild(ndParent.getContentType());
		
		// 挿入モードでダイアログを表示
		DtContainerContentInsertDialog dlg;
		if (initialInsertPosition < 0) {
			// 名前付きオブジェクト
			dlg = new DtContainerContentInsertDialog(getFrame(), forSlipObject, ndParent);
		} else {
			// リスト要素オブジェクト
			dlg = new DtContainerContentInsertDialog(getFrame(), forSlipObject, ndParent, initialInsertPosition);
		}
		dlg.initialComponent();
		dlg.setVisible(true);
		dlg.dispose();
		if (dlg.getDialogResult() != IDialogResult.DialogResult_OK) {
			// user canceled
			setFocusToActiveView();
			return;
		}
		
		// 子ノードを生成
		IDtContainerContentTreeNode ndNewObject;
		if (dlg.isIndexedChild()) {
			// リスト要素オブジェクト
			ndNewObject = getDocument().insertIndexedObject(ndParent, dlg.getInsertIndex(), dlg.getDataObject());
		}
		else {
			// 名前付きオブジェクト
			ndNewObject = getDocument().putNamedObject(ndParent, dlg.getObjectName(), dlg.getDataObject());
		}
		
		// 挿入されたノードを選択
		TreePath tpath = new TreePath(getDocument().getDataTreeModel().getPathToRoot(ndNewObject));
		_treeComponent.expandPath(tpath.getParentPath());		// 親ノードを展開
		_treeComponent.setSelectionPath(tpath);					// 挿入したノードを選択
		updateEditorModifiedProperty();							// 変更プロパティを更新
		onTreeNodeInsertedByMenuAction(ndParent, ndNewObject);	// ツリー挿入時の処理
		_treeComponent.setFocus();								// ツリーにフォーカスを設定
		
	}

	/**
	 * コンテントツリー専用メニューの [Replace] が選択されたときに呼び出されるイベントハンドラー。
	 * @param menuItemAction	選択されたメニューアイテムアクション
	 */
	protected void onSelectedMenuContentTreeEditReplace(Action menuItemAction) {
		AppLogger.debug("menu ID_TREE_REPLACE selected.");
		
		// 単一選択以外は、処理しない
		if (_treeComponent.getSelectionCount() != 1)
			return;
		IDtContainerContentTreeNode ndSelected = (IDtContainerContentTreeNode)_treeComponent.getSelectionPath().getLastPathComponent();
		DtContainerContentTypes selectedType = ndSelected.getContentType();
		if (ndSelected.getParent() == null) {
			// ルートノードが選択されている場合は、処理しない
			return;
		}
		if (!DtContainerContentTypesManager.getInstance().isAllowReplace(selectedType))
			return;
		DtContainerContentParentTreeNode ndParent = (DtContainerContentParentTreeNode)ndSelected.getParent();
		boolean forSlipObject = DtContainerContentTypesManager.getInstance().isAllowStoreSlipObjectChild(ndParent.getContentType());
		
		// 変更モードでダイアログを表示
		DtContainerContentReplaceDialog dlg = new DtContainerContentReplaceDialog(getFrame(), forSlipObject, ndParent, ndSelected);
		dlg.initialComponent();
		dlg.setVisible(true);
		dlg.dispose();
		if (dlg.getDialogResult() != IDialogResult.DialogResult_OK) {
			// user canceled
			setFocusToActiveView();
			return;
		}
		
		// 置き換え(常に新しいノードを生成する)
		IDtContainerContentTreeNode ndNewObject = getDocument().replaceObject(ndSelected, dlg.getDataObject());
		
		// 置き換えられたノードを選択
		TreePath tpath = new TreePath(getDocument().getDataTreeModel().getPathToRoot(ndNewObject));
		_treeComponent.expandPath(tpath.getParentPath());		// 親ノードを展開
		_treeComponent.setSelectionPath(tpath);					// 挿入したノードを選択
		updateEditorModifiedProperty();							// 変更プロパティを更新
		onTreeNodeReplacedByMenuAction(ndParent, ndNewObject, ndSelected);	// ツリー置き換え時の処理
		_treeComponent.setFocus();								// ツリーにフォーカスを設定
	}

	/**
	 * コンテントツリー専用メニューの [Export] が選択されたときに呼び出されるイベントハンドラー。
	 * @param menuItemAction	選択されたメニューアイテムアクション
	 */
	protected void onSelectedMenuContentTreeEditExport(Action menuItemAction) {
		AppLogger.debug("menu ID_TREE_EXPORT selected.");
		
		// 単一選択以外は、処理しない
		if (_treeComponent.getSelectionCount() != 1)
			return;
		IDtContainerContentTreeNode ndSelected = (IDtContainerContentTreeNode)_treeComponent.getSelectionPath().getLastPathComponent();
		if (ndSelected.getParent() == null) {
			// ルートノードが選択されている場合は、処理しない
			return;
		}
		DtContainerContentTypes nodeType = ndSelected.getContentType();
		if (!DtContainerContentTypesManager.getInstance().isAllowExport(ndSelected.getContentType()))
			return;
		
		// エクスポート対象に、編集によって不正な値が含まれていないか検証する
		try {
			getDocument().validateContainerContentDataInNodeAndDescendants(ndSelected);
		}
		catch (DTCContentEditTableModelConversionError ex) {
			// 編集中のデータに不正な値が含まれている場合に、この例外がスローされる
			//--- 不正な値の位置を選択
			visibleAndSelectInvalidValueCell(ex);
			//--- エラーメッセージを表示
			final String errmsg = ex.getMessage();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					Application.showErrorMessage(getFrame(), errmsg);
				}
			});
			return;
		}
		
		// エクスポートダイアログの表示
		boolean forSlipObject = DtContainerContentTypesManager.getInstance().isDtSlipObject(nodeType);
		DtContainerContentExportDialog dlg = new DtContainerContentExportDialog(getFrame(), forSlipObject, ndSelected);
		dlg.initialComponent();
		dlg.setVisible(true);
		dlg.dispose();
		if (dlg.getDialogResult() != IDialogResult.DialogResult_OK) {
			// user canceled
			setFocusToActiveView();
			return;
		}
		
		// export to file
		if (forSlipObject) {
			// DtSlip or DtSlipList as JSON
			try {
				getDocument().exportSlipObjectToJsonFile(ndSelected, dlg.getDestinationFile());
			}
			catch (IOException ex) {
				String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_FILE_WRITE, ex);
				AppLogger.error(errmsg, ex);
				DtContainerEditor.showErrorMessage(getFrame(), errmsg);
			}
			catch (OutOfMemoryError ex) {
				String errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
				AppLogger.error(errmsg, ex);
				DtContainerEditor.showErrorMessage(this, errmsg);
			}
			catch (Throwable ex) {
				String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_UNEXPECTED, ex);
				AppLogger.error(errmsg, ex);
				DtContainerEditor.showErrorMessage(getFrame(), errmsg);
			}
		}
		else {
			// Exalge, ExAlgeSet, Dtalge(or note), DtAlgeSet as CSV/XML
			if (dlg.isTextTypeXml()) {
				//--- save as XML file
				try {
					if (DtContainerContentTypesManager.getInstance().isNoteObject(nodeType)) {
						// note
						getDocument().exportNoteToXmlFile(ndSelected, dlg.getDestinationFile());
					}
					else {
						// data object
						getDocument().exportDataObjectToXmlFile(ndSelected, dlg.getDestinationFile());
					}
				} catch (DOMException | FactoryConfigurationError | ParserConfigurationException | TransformerException ex) {
					String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_FILE_WRITE, ex);
					AppLogger.error(errmsg, ex);
					DtContainerEditor.showErrorMessage(getFrame(), errmsg);
				}
				catch (IOException ex) {
					String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_FILE_WRITE, ex);
					AppLogger.error(errmsg, ex);
					DtContainerEditor.showErrorMessage(getFrame(), errmsg);
				}
				catch (OutOfMemoryError ex) {
					String errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
					AppLogger.error(errmsg, ex);
					DtContainerEditor.showErrorMessage(this, errmsg);
				}
				catch (Throwable ex) {
					String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_UNEXPECTED, ex);
					AppLogger.error(errmsg, ex);
					DtContainerEditor.showErrorMessage(getFrame(), errmsg);
				}
			}
			else {
				//--- save as CSV file
				try {
					if (DtContainerContentTypesManager.getInstance().isNoteObject(nodeType)) {
						// note
						getDocument().exportNoteToCsvFile(ndSelected, dlg.getDestinationFile(), dlg.getCsvEncoding());
					}
					else {
						// data object
						getDocument().exportDataObjectToCsvFile(ndSelected, dlg.getDestinationFile(), dlg.getCsvEncoding());
					}
				}
				catch (UnsupportedEncodingException ex) {
					String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_FILE_UNSUPPORTED_ENCODING, ex);
					AppLogger.error(errmsg, ex);
					DtContainerEditor.showErrorMessage(getFrame(), errmsg);
				}
				catch (IOException ex) {
					String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_FILE_WRITE, ex);
					AppLogger.error(errmsg, ex);
					DtContainerEditor.showErrorMessage(getFrame(), errmsg);
				}
				catch (OutOfMemoryError ex) {
					String errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
					AppLogger.error(errmsg, ex);
					DtContainerEditor.showErrorMessage(this, errmsg);
				}
				catch (Throwable ex) {
					String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_UNEXPECTED, ex);
					AppLogger.error(errmsg, ex);
					DtContainerEditor.showErrorMessage(getFrame(), errmsg);
				}
			}
		}
		setFocusToActiveView();
	}

	/**
	 * コンテントツリー専用メニューの [Delete] が選択されたときに呼び出されるイベントハンドラー。
	 * @param menuItemAction	選択されたメニューアイテムアクション
	 */
	protected void onSelectedMenuContentTreeEditDelete(Action menuItemAction) {
		AppLogger.debug("menu ID_TREE_DELETE selected.");
		
		// 判定
		if (!_treeSelectionInfo.canDelete())
			return;
		
		// 削除情報を構成
		IDtContainerContentTreeNode ndRemoveParent = _treeSelectionInfo.getParentNode();
		int[] removedIndices = new int[_treeSelectionInfo.size()];
		int idx = 0;
		for (IDtContainerContentTreeNode node : _treeSelectionInfo.getNodes()) {
			removedIndices[idx] = ndRemoveParent.getIndex(node);
			++idx;
		}
		
		// 削除確認
		int ret = DtContainerEditor.showConfirmMessageBox(getFrame(), null, DtContainerEditorMessages.getInstance().confirmDeleteTreeNode, JOptionPane.OK_CANCEL_OPTION);
		if (ret != JOptionPane.OK_OPTION) {
			// user canceled
			return;
		}
		
		// ノードを削除する
		Arrays.sort(removedIndices);	// 念のため、ソート
		//--- 終端から削除
		Object[] removed = new Object[removedIndices.length];
		for (int i = removedIndices.length - 1; i >= 0; i--) {
			int removeIndex = removedIndices[i];
			removed[i] = ndRemoveParent.getChildAt(removeIndex);
			ndRemoveParent.remove(removeIndex);
			onTreeNodeOneRemovedWithoutRefreshingPosition(ndRemoveParent, removeIndex, (IDtContainerContentTreeNode)removed[i]);
		}
		//--- 削除を通知
		getDocument().getDataTreeModel().nodesWereRemoved(ndRemoveParent, removedIndices, removed);
		setEditorModifiedProperty(true);			// 変更プロパティを更新
		onTreeNodePositionChanged(ndRemoveParent, removedIndices[0]);	// 削除された先頭ノード以降のインデックス更新
		_treeComponent.clearSelection();	// 選択解除
		_treeComponent.setFocus();
	}

	/**
	 * コンテントツリー専用メニューの [Cut] が選択されたときに呼び出されるイベントハンドラー。
	 * @param menuItemAction	選択されたメニューアイテムアクション
	 */
	protected void onSelectedMenuContentTreeEditCut(Action menuItemAction) {
		AppLogger.debug("menu ID_TREE_CUT selected.");
		
		// 判定
		if (!_treeSelectionInfo.canCut())
			return;
		
		// 転送データを生成
		DTCContentTransferTreeData trans = new DTCContentTransferTreeData(_treeSelectionInfo.getNodes());
		
		// クリップボードへコピー
		Clipboard clip = getToolkit().getSystemClipboard();
		clip.setContents(trans, trans);
		if (AppLogger.isTraceEnabled()) {
			AppLogger.trace("called clip.setContents(trans, trans) at AbDtContainerEditView#onSelectedMenuContentTreeEditCut() : trans=" + String.valueOf(trans));
			AppLogger.trace(String.format("===== PID=%s : thread-ID=%s", String.valueOf(ProcessHandle.current().pid()), String.valueOf(Thread.currentThread().getId())));
		}
		
		// メニューを更新
		getFrame().updateMenuItem(DtContainerEditorMenuResources.ID_EDIT_PASTE);
		getFrame().updateMenuItem(DtContainerEditorMenuResources.ID_TREE_PASTE);
		
		// 削除(確認不要)
		
		// 削除情報を構成
		IDtContainerContentTreeNode ndRemoveParent = _treeSelectionInfo.getParentNode();
		int[] removedIndices = new int[_treeSelectionInfo.size()];
		int idx = 0;
		for (IDtContainerContentTreeNode node : _treeSelectionInfo.getNodes()) {
			removedIndices[idx] = ndRemoveParent.getIndex(node);
			++idx;
		}
		
		// ノードを削除する
		Arrays.sort(removedIndices);	// 念のため、ソート
		//--- 終端から削除
		Object[] removed = new Object[removedIndices.length];
		for (int i = removedIndices.length - 1; i >= 0; i--) {
			int removeIndex = removedIndices[i];
			removed[i] = ndRemoveParent.getChildAt(removeIndex);
			ndRemoveParent.remove(removeIndex);
			onTreeNodeOneRemovedWithoutRefreshingPosition(ndRemoveParent, removeIndex, (IDtContainerContentTreeNode)removed[i]);
		}
		//--- 削除を通知
		getDocument().getDataTreeModel().nodesWereRemoved(ndRemoveParent, removedIndices, removed);
		setEditorModifiedProperty(true);			// 変更プロパティを更新
		onTreeNodePositionChanged(ndRemoveParent, removedIndices[0]);	// 削除された先頭ノード以降のインデックス更新
		_treeComponent.clearSelection();	// 選択解除
		_treeComponent.setFocus();
	}

	/**
	 * コンテントツリー専用メニューの [Copy] が選択されたときに呼び出されるイベントハンドラー。
	 * @param menuItemAction	選択されたメニューアイテムアクション
	 */
	protected void onSelectedMenuContentTreeEditCopy(Action menuItemAction) {
		AppLogger.debug("menu ID_TREE_COPY selected.");
		
		// 判定
		if (!_treeSelectionInfo.canCopy())
			return;
		
		// 転送データを生成
		DTCContentTransferTreeData trans = new DTCContentTransferTreeData(_treeSelectionInfo.getNodes());
		
		// クリップボードへコピー
		Clipboard clip = getToolkit().getSystemClipboard();
		clip.setContents(trans, trans);
		if (AppLogger.isTraceEnabled()) {
			AppLogger.trace("called clip.setContents(trans, trans) at AbDtContainerEditView#onSelectedMenuContentTreeEditCopy() : trans=" + String.valueOf(trans));
			AppLogger.trace(String.format("===== PID=%s : thread-ID=%s", String.valueOf(ProcessHandle.current().pid()), String.valueOf(Thread.currentThread().getId())));
		}
		
		// メニューを更新
		getFrame().updateMenuItem(DtContainerEditorMenuResources.ID_EDIT_PASTE);
		getFrame().updateMenuItem(DtContainerEditorMenuResources.ID_TREE_PASTE);
	}
	
	/**
	 * 転送データをノート(データ代数元)として、<em>ndSelectedDest</em> のノートと置き換える。
	 * @param ndSelectedDest	貼り付け位置のノートオブジェクトを示すツリーノード
	 * @param transData	転送データ
	 */
	protected boolean pasteTreeNodeToNote(IDtContainerContentTreeNode ndSelectedDest, DTCContentTransferTreeData transData) {
		AppLogger.trace("AbDtContainerEditView#pasteTreeNodeToNote() : called");
		
		DTCContentTransferTreeNode[] transNodes = transData.getToplevelNodes();
		if (transNodes == null || transNodes.length != 1) {
			_treeComponent.setFocus();								// ツリーにフォーカスを設定
			return false;	// no transfer data
		}
		DtContainerContentTypes ctSrc = transNodes[0].getContentType();
		if (DtContainerContentTypes.ContentDtalge != ctSrc && !DtContainerContentTypesManager.getInstance().isNoteObject(ctSrc)) {
			_treeComponent.setFocus();								// ツリーにフォーカスを設定
			return false;	// not Dtalge object
		}
		
		// 上書き確認
		String title = String.format(DtContainerEditorMessages.getInstance().titleConfirmPasteWithPath, ndSelectedDest.getContentPathString());
		String msg = DtContainerEditorMessages.getInstance().confirmPasteNoteAndReplace;
		int ret = DtContainerEditor.showConfirmMessageBox(getFrame(), title, msg, JOptionPane.OK_CANCEL_OPTION);
		if (ret != JOptionPane.OK_OPTION) {
			// user canceled
			AppLogger.debug("Canceled to replace note by user");
			_treeComponent.setFocus();								// ツリーにフォーカスを設定
			return false;
		}
		
		// ノートの複製
		DTCContentDtalgeEditModel newModel = new DTCContentDtalgeEditModel();
		newModel.initByCopiedRows(transNodes[0].getCopiedData());
		
		// 置き換え(常に新しいノードを生成する)
		IDtContainerContentTreeNode ndNewObject = getDocument().replaceObject(ndSelectedDest, newModel);
		
		// 置き換えられたノードを選択
		TreePath tpath = new TreePath(getDocument().getDataTreeModel().getPathToRoot(ndNewObject));
		_treeComponent.expandPath(tpath.getParentPath());		// 親ノードを展開
		updateEditorModifiedProperty();							// 変更プロパティを更新
		onTreeNodeReplacedByMenuAction(ndSelectedDest.getParent(), ndNewObject, ndSelectedDest);	// ツリー置き換え時の処理
		_treeComponent.setFocus();								// ツリーにフォーカスを設定
		return true;
	}
	
	/**
	 * 転送データをデータ代数として、<em>ndSelectedDest</em> の位置に挿入する。
	 * @param ndSelectedDest	貼り付け位置のオブジェクトを示すツリーノード
	 * @param transData	転送データ
	 * @return	転送データの貼り付けに成功した場合は {@code true}
	 */
	protected boolean pasteTreeNodeAsDtalgeIntoDtAlgeSet(IDtContainerContentTreeNode ndSelectedDest, DTCContentTransferTreeData transData) {
		AppLogger.trace("AbDtContainerEditView#pasteTreeNodeAsDtalgeIntoDtAlgeSet() : called");
		
		DTCContentTransferTreeNode[] transNodes = transData.getToplevelNodes();
		if (transNodes == null || transNodes.length <= 0) {
			_treeComponent.setFocus();								// ツリーにフォーカスを設定
			return false;	// no transfer data
		}
		
		// ノード生成
		ArrayList<IDtContainerContentTreeNode> nodelist = new ArrayList<IDtContainerContentTreeNode>(transNodes.length);
		for (DTCContentTransferTreeNode transTarget : transNodes) {
			IDtContainerContentTreeNode ndNew = getDocument().createTreeNodeByTransferTreeNode(transTarget, DtContainerContentTypes.ContentDtalge);
			if (ndNew != null) {
				nodelist.add(ndNew);
			}
		}
		if (nodelist.isEmpty()) {
			_treeComponent.setFocus();								// ツリーにフォーカスを設定
			return false;
		}
		
		// 貼り付け
		int insertIndex;
		DtContainerContentParentTreeNode ndParent;
		if (DtContainerContentTypes.ContentDtAlgeSet == ndSelectedDest.getContentType()) {
			// 選択位置が DtAlgeSet なので、終端に追加
			ndParent = (DtContainerContentParentTreeNode)ndSelectedDest;
			insertIndex = ndParent.getChildCount();
		}
		else {
			// 選択位置が DtAlgeSet ではないので、親ノードを取得し、挿入位置を取得
			ndParent = (DtContainerContentParentTreeNode)ndSelectedDest.getParent();
			insertIndex = ndParent.getIndex(ndSelectedDest);
		}
		getDocument().insertAllIndexedTreeNode(ndParent, insertIndex, nodelist);
		
		// 置き換えられたノードを選択
		TreePath tpath = new TreePath(getDocument().getDataTreeModel().getPathToRoot(nodelist.get(nodelist.size()-1)));	// 追加ノードリストの終端
		_treeComponent.expandPath(tpath.getParentPath());		// 親ノードを展開
		tpath = new TreePath(getDocument().getDataTreeModel().getPathToRoot(ndSelectedDest));	// 選択されていたノード
		_treeComponent.scrollPathToVisible(tpath);				// 選択されていたノードが見えるように
		_treeComponent.setSelectionPath(tpath);					// 選択状態の復帰
		updateEditorModifiedProperty();							// 変更プロパティを更新
		onTreeNodeInsertedByMenuAction(ndParent, null);			// ツリー挿入時の処理
		_treeComponent.setFocus();								// ツリーにフォーカスを設定
		return true;
	}
	
	/**
	 * 転送データを交換代数として、<em>ndSelectedDest</em> の位置に挿入する。
	 * @param ndSelectedDest	貼り付け位置のオブジェクトを示すツリーノード
	 * @param transData	転送データ
	 * @return	転送データの貼り付けに成功した場合は {@code true}
	 */
	protected boolean pasteTreeNodeAsExalgeIntoExAlgeSet(IDtContainerContentTreeNode ndSelectedDest, DTCContentTransferTreeData transData) {
		AppLogger.trace("AbDtContainerEditView#pasteTreeNodeAsExalgeIntoExAlgeSet() : called");
		
		DTCContentTransferTreeNode[] transNodes = transData.getToplevelNodes();
		if (transNodes == null || transNodes.length <= 0) {
			_treeComponent.setFocus();								// ツリーにフォーカスを設定
			return false;	// no transfer data
		}
		
		// ノード生成
		ArrayList<IDtContainerContentTreeNode> nodelist = new ArrayList<IDtContainerContentTreeNode>(transNodes.length);
		for (DTCContentTransferTreeNode transTarget : transNodes) {
			IDtContainerContentTreeNode ndNew = getDocument().createTreeNodeByTransferTreeNode(transTarget, DtContainerContentTypes.ContentExalge);
			if (ndNew != null) {
				nodelist.add(ndNew);
			}
		}
		if (nodelist.isEmpty()) {
			_treeComponent.setFocus();								// ツリーにフォーカスを設定
			return false;
		}
		
		// 貼り付け
		int insertIndex;
		DtContainerContentParentTreeNode ndParent;
		if (DtContainerContentTypes.ContentExAlgeSet == ndSelectedDest.getContentType()) {
			// 選択位置が ExAlgeSet なので、終端に追加
			ndParent = (DtContainerContentParentTreeNode)ndSelectedDest;
			insertIndex = ndParent.getChildCount();
		}
		else {
			// 選択位置が ExAlgeSet ではないので、親ノードを取得し、挿入位置を取得
			ndParent = (DtContainerContentParentTreeNode)ndSelectedDest.getParent();
			insertIndex = ndParent.getIndex(ndSelectedDest);
		}
		getDocument().insertAllIndexedTreeNode(ndParent, insertIndex, nodelist);
		
		// 置き換えられたノードを選択
		TreePath tpath = new TreePath(getDocument().getDataTreeModel().getPathToRoot(nodelist.get(nodelist.size()-1)));	// 追加ノードリストの終端
		_treeComponent.expandPath(tpath.getParentPath());		// 親ノードを展開
		tpath = new TreePath(getDocument().getDataTreeModel().getPathToRoot(ndSelectedDest));	// 選択されていたノード
		_treeComponent.scrollPathToVisible(tpath);				// 選択されていたノードが見えるように
		_treeComponent.setSelectionPath(tpath);					// 選択状態の復帰
		updateEditorModifiedProperty();							// 変更プロパティを更新
		onTreeNodeInsertedByMenuAction(ndParent, null);			// ツリー挿入時の処理
		_treeComponent.setFocus();								// ツリーにフォーカスを設定
		return true;
	}

	/**
	 * 転送データをデータスリップとして、<em>ndSelectedDest</em> の位置に挿入する。
	 * @param ndSelectedDest	貼り付け位置のオブジェクトを示すツリーノード
	 * @param transData	転送データ
	 * @return	転送データの貼り付けに成功した場合は {@code true}
	 */
	protected boolean pasteTreeNodeAsSlipIntoSlipList(IDtContainerContentTreeNode ndSelectedDest, DTCContentTransferTreeData transData) {
		AppLogger.trace("AbDtContainerEditView#pasteTreeNodeAsSlipIntoSlipList() : called");
		
		DTCContentTransferTreeNode[] transNodes = transData.getToplevelNodes();
		if (transNodes == null || transNodes.length <= 0) {
			_treeComponent.setFocus();								// ツリーにフォーカスを設定
			return false;	// no transfer data
		}
		
		// ノード生成
		ArrayList<IDtContainerContentTreeNode> nodelist = new ArrayList<IDtContainerContentTreeNode>(transNodes.length);
		for (DTCContentTransferTreeNode transTarget : transNodes) {
			IDtContainerContentTreeNode ndNew = getDocument().createTreeNodeByTransferTreeNode(transTarget, DtContainerContentTypes.ContentDtSlip);
			if (ndNew != null) {
				nodelist.add(ndNew);
			}
		}
		if (nodelist.isEmpty()) {
			_treeComponent.setFocus();								// ツリーにフォーカスを設定
			return false;
		}
		
		// 貼り付け
		int insertIndex;
		DtContainerContentParentTreeNode ndParent;
		if (DtContainerContentTypes.ContentDtSlipList == ndSelectedDest.getContentType()) {
			// 選択位置が DtSlipList なので、終端に追加
			ndParent = (DtContainerContentParentTreeNode)ndSelectedDest;
			insertIndex = ndParent.getChildCount();
		}
		else {
			// 選択位置が DtSlipList ではないので、親ノードを取得し、挿入位置を取得
			ndParent = (DtContainerContentParentTreeNode)ndSelectedDest.getParent();
			insertIndex = ndParent.getIndex(ndSelectedDest);
		}
		getDocument().insertAllIndexedTreeNode(ndParent, insertIndex, nodelist);
		
		// 置き換えられたノードを選択
		TreePath tpath = new TreePath(getDocument().getDataTreeModel().getPathToRoot(nodelist.get(nodelist.size()-1)));	// 追加ノードリストの終端
		_treeComponent.expandPath(tpath.getParentPath());		// 親ノードを展開
		tpath = new TreePath(getDocument().getDataTreeModel().getPathToRoot(ndSelectedDest));	// 選択されていたノード
		_treeComponent.scrollPathToVisible(tpath);				// 選択されていたノードが見えるように
		_treeComponent.setSelectionPath(tpath);					// 選択状態の復帰
		updateEditorModifiedProperty();							// 変更プロパティを更新
		onTreeNodeInsertedByMenuAction(ndParent, null);			// ツリー挿入時の処理
		_treeComponent.setFocus();								// ツリーにフォーカスを設定
		return true;
	}
	
	/** 名前付きオブジェクトで同名のオブジェクト置き換えるときの確認メッセージのオプションボタン名 **/
	static protected final String[] overwriteNamedObjectOptions = {
			CommonMessages.getInstance().Overwrite_YesOne,
			CommonMessages.getInstance().Overwrite_YesAll,
			DtContainerEditorMessages.getInstance().Overwrite_option_Rename,
			CommonMessages.getInstance().Overwrite_NoOne,
			CommonMessages.getInstance().Overwrite_NoAll,
			CommonMessages.getInstance().Button_Cancel,
	};
	
	static protected final int CONFIRM_OVERWRITE_OPTION_YES_ONCE	= 0;
	static protected final int CONFIRM_OVERWRITE_OPTION_YES_ALL		= 1;
	static protected final int CONFIRM_OVERWRITE_OPTION_RENAME		= 2;
	static protected final int CONFIRM_OVERWRITE_OPTION_NO_ONCE		= 3;
	static protected final int CONFIRM_OVERWRITE_OPTION_NO_ALL		= 4;

	/**
	 * 転送データを名前付きデータオブジェクト、もしくは、名前付きスリップオブジェクトとして、<em>ndSelectedDest</em> の位置に挿入する。
	 * @param ndSelectedDest	貼り付け位置のオブジェクトを示すツリーノード
	 * @param transData	転送データ
	 * @return	転送データの貼り付けに成功した場合は {@code true}
	 */
	protected boolean pasteTreeNodeAsNamedObject(IDtContainerContentTreeNode ndSelectedDest, DTCContentTransferTreeData transData) {
		AppLogger.trace("AbDtContainerEditView#pasteTreeNodeAsNamedSlipObject() : called");
		
		DTCContentTransferTreeNode[] transNodes = transData.getToplevelNodes();
		if (transNodes == null || transNodes.length <= 0) {
			_treeComponent.setFocus();								// ツリーにフォーカスを設定
			return false;	// no transfer data
		}
		
		// 親ノードの取得
		DtContainerContentParentTreeNode ndParent;
		if (DtContainerContentTypes.ContentDtBinderSlips == ndSelectedDest.getContentType() || DtContainerContentTypes.ContentDtSlipObjects == ndSelectedDest.getContentType()) {
			// 選択位置が DtBinder, DtSlip の名前付きオブジェクトルート
			ndParent = (DtContainerContentParentTreeNode)ndSelectedDest;
		}
		else {
			// 選択位置が名前付きオブジェクトルートではないので、親ノードを取得し、挿入位置を取得
			ndParent = (DtContainerContentParentTreeNode)ndSelectedDest.getParent();
		}
		
		// 貼り付け
		int sortedIndex;
		int selectedOption = (-1);
		IDtContainerContentTreeNode ndChild;
		ArrayList<IDtContainerContentTreeNode> addedNodeList = new ArrayList<IDtContainerContentTreeNode>(transNodes.length);
		for (DTCContentTransferTreeNode transTarget : transNodes) {
			//--- 親ノードに追加可能なコンテントタイプでない場合は、スキップ
			if (!DtContainerContentTypesManager.getInstance().isAppendableIntoParent(ndParent.getContentType(), transTarget.getContentType())) {
				continue;
			}
			//--- 名前の有無を判定
			String nodeName;
			if (transTarget.hasNodeName()) {
				// ノード名がすでに設定されている場合は、同名を置き換えるかどうかを選択する
				nodeName = transTarget.getNodeName();
				sortedIndex = ndParent.findSortedPositionByName(nodeName);
				if (sortedIndex >= 0) {
					// 重複あり
					IDtContainerContentTreeNode ndOld = ndParent.getChildAt(sortedIndex);
					if (AppLogger.isDebugEnabled()) {
						AppLogger.debug(String.format("Paste named data object [%s] already exists in [%s]...", nodeName, ndParent.getContentPathString()));
					}
					if (selectedOption < 0) {
						// 要問い合わせ
						String title = String.format(DtContainerEditorMessages.getInstance().titleConfirmPasteWithPath, ndParent.getContentPathString());
						String msg = String.format(DtContainerEditorMessages.getInstance().confirmPasteSameNameObjectAndReplace, nodeName);
						selectedOption = JOptionPane.showOptionDialog(getFrame(), msg, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, overwriteNamedObjectOptions, overwriteNamedObjectOptions[0]);
					}
					//--- 問い合わせ結果の通り
					if (selectedOption == CONFIRM_OVERWRITE_OPTION_YES_ONCE) {
						// 現在のもののみ置き換え
						AppLogger.debug("...replace this once.");
						//--- ノード生成
						ndChild = getDocument().createTreeNodeByTransferTreeNode(transTarget, null);
						ndChild.setNodeName(nodeName);
						//--- 置き換え
						getDocument().putNamedTreeNode(ndParent, ndChild);
						addedNodeList.add(ndChild);
						//--- 詳細ビューの表示更新
						onTreeNodeReplacedByMenuAction(ndParent, ndChild, ndOld);	// ツリー置き換え時の処理
						//--- フラグリセット
						selectedOption = (-1);
					}
					else if (selectedOption == CONFIRM_OVERWRITE_OPTION_YES_ALL) {
						// すべて置き換え
						AppLogger.debug("...replace all.");
						//--- ノード生成
						ndChild = getDocument().createTreeNodeByTransferTreeNode(transTarget, null);
						ndChild.setNodeName(nodeName);
						//--- 置き換え
						getDocument().putNamedTreeNode(ndParent, ndChild);
						addedNodeList.add(ndChild);
						//--- 詳細ビューの表示更新
						onTreeNodeReplacedByMenuAction(ndParent, ndChild, ndOld);	// ツリー置き換え時の処理
					}
					else if (selectedOption == CONFIRM_OVERWRITE_OPTION_RENAME) {
						// 名前変更
						AppLogger.debug("...rename.");
						//--- 名前入力
						DtContainerContentNameValidator validator = new DtContainerContentNameValidator(ndParent);	// 名前入力時の判定用(自身も含む)
						InputDialog dlg = new InputDialog(getFrame(),
								DtContainerEditorMessages.getInstance().RenameDlg_label + ": ", null, 
								String.format(DtContainerEditorMessages.getInstance().titleConfirmPasteWithPath, ndParent.getContentPathString()),
								null,
								IDtContainerDocument.DEF_COPIED_NAME_PREFIX + nodeName, validator);
						dlg.pack();
						dlg.setVisible(true);
						dlg.dispose();
						if (dlg.getDialogResult() != IDialogResult.DialogResult_OK) {
							// user canceled
							AppLogger.debug("...canceled by user.");
							break;
						}
						nodeName = dlg.getFieldText();
						//--- ノード生成
						ndChild = getDocument().createTreeNodeByTransferTreeNode(transTarget, null);
						ndChild.setNodeName(nodeName);
						//--- 追加
						getDocument().putNamedTreeNode(ndParent, ndChild);
						addedNodeList.add(ndChild);
						//--- フラグリセット
						selectedOption = (-1);
					}
					else if (selectedOption == CONFIRM_OVERWRITE_OPTION_NO_ONCE) {
						// 現在のもののみスキップ
						AppLogger.debug("...skip this once.");
						selectedOption = (-1);
						continue;
					}
					else if (selectedOption == CONFIRM_OVERWRITE_OPTION_NO_ALL) {
						// すべてスキップ
						AppLogger.debug("...skip all.");
						continue;
					}
					else {
						// user canceled
						AppLogger.debug("...canceled by user.");
						break;
					}
				}
				else {
					// 重複なしなので、追加
					//--- ノード生成
					ndChild = getDocument().createTreeNodeByTransferTreeNode(transTarget, null);
					ndChild.setNodeName(nodeName);
					//--- 追加
					getDocument().putNamedTreeNode(ndParent, ndChild);
					addedNodeList.add(ndChild);
				}
			}
			else {
				// ノード名が設定されていない場合は、重複しない名前を設定する
				do {
					nodeName = getDocument().getNextNewNodeName(IDtContainerDocument.DEF_COPIED_NAME_PREFIX);
					sortedIndex = ndParent.findSortedPositionByName(nodeName);
				} while (sortedIndex >= 0);	// 同名が存在する間は、新しい名前を生成
				//--- ノード生成
				ndChild = getDocument().createTreeNodeByTransferTreeNode(transTarget, null);
				ndChild.setNodeName(nodeName);
				//--- 追加
				getDocument().putNamedTreeNode(ndParent, ndChild);
				addedNodeList.add(ndChild);
			}
		}
		
		// 最後の追加されたノードを表示
		if (!addedNodeList.isEmpty()) {
			//TreePath tpath = new TreePath(getDocument().getDataTreeModel().getPathToRoot(addedNodeList.get(addedNodeList.size()-1));
			TreePath tpath = new TreePath(getDocument().getDataTreeModel().getPathToRoot(ndParent));
			_treeComponent.expandPath(tpath);
			if (ndSelectedDest == ndParent || ndParent.getIndex(ndSelectedDest) >= 0) {
				// 初期選択ノードが存在している場合は、再選択
				tpath = new TreePath(getDocument().getDataTreeModel().getPathToRoot(ndSelectedDest));
				_treeComponent.setSelectionPath(tpath);
			}
			else {
				// 親ノードを表示する
				_treeComponent.clearSelection();
			}
			_treeComponent.scrollPathToVisible(tpath);				// ノードが見えるように
			updateEditorModifiedProperty();							// 変更プロパティを更新
			_treeComponent.setFocus();								// ツリーにフォーカスを設定
			return true;
		}
		else {
			_treeComponent.setFocus();								// ツリーにフォーカスを設定
			return false;
		}
	}

	/**
	 * コンテントツリー専用メニューの [Paste] が選択されたときに呼び出されるイベントハンドラー。
	 * @param menuItemAction	選択されたメニューアイテムアクション
	 */
	protected void onSelectedMenuContentTreeEditPaste(Action menuItemAction) {
		AppLogger.debug("menu ID_TREE_PASTE selected.");
		
		// 単一選択以外は、不可
		if (_treeComponent.getSelectionCount() != 1)
			return;
		// クリップボードの情報を判定
		Transferable trans = SwingTools.getSystemClipboardContents(_treeComponent);
		if (trans == null)
			return;	// no clipboard contents
		
		// 貼り付け位置の判定
		TreePath selectedPath = _treeComponent.getSelectionPath();
		IDtContainerContentTreeNode ndSelected = (IDtContainerContentTreeNode)selectedPath.getLastPathComponent();
		if (ndSelected.getParent() == null)
			return;	// ルートノードは対象外
		DTCContentTransferTreeData clipdata = DTCContentTransferTreeData.getDataFromTransferable(trans);
		if (clipdata == null)
			return;	// not supported data
		if (clipdata.isEmpty())
			return;	// 転送データなし
		if (!clipdata.isAllowPasteTo(ndSelected))
			return;	// not allow paste to ndSelected
		
		// 貼り付け位置による貼り付け
		DtContainerContentTypes ctSelected = ndSelected.getContentType();
		DtContainerContentTypes ctParent   = ndSelected.getParent().getContentType();
		Set<DtContainerContentTypes> transTopContentTypes = clipdata.getToplevelContentTypes();
		//--- 以下の判定は、clipdata.isAllowPasteTo() により、貼り付け位置と転送データのコンテントタイプ整合性が、ほぼ維持されている(はず)
		if (AppLogger.isDebugEnabled()) {
			String dmsg = "AbDtContainerEditView#onSelectedMenuContentTreeEditPaste() : paste transfer data (" + transTopContentTypes.toString() + ")"
						+ "	at selected=[" + ctSelected.toString() + "], parent=[" + ctParent.toString() + "]";
			AppLogger.debug(dmsg);
		}
		switch (ctSelected) {
			case ContentExalge:
				// 選択位置が交換代数元の場合
				if (DtContainerContentTypes.ContentExAlgeSet == ctParent && transTopContentTypes.size()==1 && transTopContentTypes.contains(DtContainerContentTypes.ContentExalge)) {
					// 親が交換代数集合で、交換代数元のみの転送データなら、交換代数集合への挿入
					pasteTreeNodeAsExalgeIntoExAlgeSet(ndSelected, clipdata);
				}
				else if (DtContainerContentTypes.ContentDtSlipObjects == ctParent) {
					// 親が名前付きデータオブジェクトのルートなら、名前付きデータオブジェクトとして挿入
					pasteTreeNodeAsNamedObject(ndSelected, clipdata);
				}
				else {
					AppLogger.debug("AbDtContainerEditView#onSelectedMenuContentTreeEditPaste() : Unable to paste transfer data, paste canceled!");
				}
				break;
			case ContentExAlgeSet:
				// 選択位置が交換代数集合の場合
				if (transTopContentTypes.size()==1 && transTopContentTypes.contains(DtContainerContentTypes.ContentExalge)) {
					// 親が交換代数集合で、交換代数元のみの転送データなら、交換代数集合への挿入
					pasteTreeNodeAsExalgeIntoExAlgeSet(ndSelected, clipdata);
				}
				else if (DtContainerContentTypes.ContentDtSlipObjects == ctParent) {
					// 親が名前付きデータオブジェクトのルートなら、名前付きデータオブジェクトとして挿入
					pasteTreeNodeAsNamedObject(ndSelected, clipdata);
				}
				else {
					AppLogger.debug("AbDtContainerEditView#onSelectedMenuContentTreeEditPaste() : Unable to paste transfer data, paste canceled!");
				}
				break;
			case ContentDtalge:
				// 選択位置がデータ代数元の場合
				if (DtContainerContentTypes.ContentDtAlgeSet == ctParent && transTopContentTypes.size()==1 && transTopContentTypes.contains(DtContainerContentTypes.ContentDtalge)) {
					// 親がデータ代数集合で、データ代数元のみの転送データなら、データ代数集合への挿入
					pasteTreeNodeAsDtalgeIntoDtAlgeSet(ndSelected, clipdata);
				}
				else if (transTopContentTypes.size()==1 &&
						(transTopContentTypes.contains(DtContainerContentTypes.ContentDtSlipNote) || transTopContentTypes.contains(DtContainerContentTypes.ContentDtBinderNote)))
				{
					// ノートのみの転送データなら、データ代数集合への挿入
					pasteTreeNodeAsDtalgeIntoDtAlgeSet(ndSelected, clipdata);
				}
				else if (DtContainerContentTypes.ContentDtSlipObjects == ctParent) {
					// 親が名前付きデータオブジェクトのルートなら、名前付きデータオブジェクトとして挿入
					pasteTreeNodeAsNamedObject(ndSelected, clipdata);
				}
				else {
					AppLogger.debug("AbDtContainerEditView#onSelectedMenuContentTreeEditPaste() : Unable to paste transfer data, paste canceled!");
				}
				break;
			case ContentDtAlgeSet:
				// 選択位置がデータ代数集合の場合
				if (transTopContentTypes.size()==1 && transTopContentTypes.contains(DtContainerContentTypes.ContentDtalge)) {
					// 親がデータ代数集合で、データ代数元のみの転送データなら、データ代数集合への挿入
					pasteTreeNodeAsDtalgeIntoDtAlgeSet(ndSelected, clipdata);
				}
				else if (transTopContentTypes.size()==1 &&
						(transTopContentTypes.contains(DtContainerContentTypes.ContentDtSlipNote) || transTopContentTypes.contains(DtContainerContentTypes.ContentDtBinderNote)))
				{
					// ノートのみの転送データなら、データ代数集合への挿入
					pasteTreeNodeAsDtalgeIntoDtAlgeSet(ndSelected, clipdata);
				}
				else if (DtContainerContentTypes.ContentDtSlipObjects == ctParent) {
					// 親が名前付きデータオブジェクトのルートなら、名前付きデータオブジェクトとして挿入
					pasteTreeNodeAsNamedObject(ndSelected, clipdata);
				}
				else {
					AppLogger.debug("AbDtContainerEditView#onSelectedMenuContentTreeEditPaste() : Unable to paste transfer data, paste canceled!");
				}
				break;
			case ContentDtSlipNote:
			case ContentDtBinderNote:
				// 選択位置がノートの場合
				if (transTopContentTypes.size()==1 &&
					(transTopContentTypes.contains(DtContainerContentTypes.ContentDtalge) ||
					 transTopContentTypes.contains(DtContainerContentTypes.ContentDtSlipNote) ||
					 transTopContentTypes.contains(DtContainerContentTypes.ContentDtBinderNote)))
				{
					// データ代数元のみの転送データなら、ノートの置き換え
					pasteTreeNodeToNote(ndSelected, clipdata);
				}
				else {
					AppLogger.debug("AbDtContainerEditView#onSelectedMenuContentTreeEditPaste() : Unable to paste transfer data, paste canceled!");
				}
				break;
			case ContentDtSlipObjects:
				// 選択位置が名前付きデータオブジェクトのルートの場合、名前付きデータオブジェクトとして挿入
				pasteTreeNodeAsNamedObject(ndSelected, clipdata);
				break;
			case ContentDtBinderSlips:
				// 選択位置が名前付きスリップオブジェクトのルートの場合、名前付きスリップオブジェクトとして挿入
				pasteTreeNodeAsNamedObject(ndSelected, clipdata);
				break;
			case ContentDtSlip:
				// 選択位置がデータスリップのルートの場合
				if (DtContainerContentTypes.ContentDtSlipList == ctParent && transTopContentTypes.size()==1 && transTopContentTypes.contains(DtContainerContentTypes.ContentDtSlip)) {
					// 親がデータスリップ集合で、データスリップのみの転送データなら、データスリップ集合への挿入
					pasteTreeNodeAsSlipIntoSlipList(ndSelected, clipdata);
				}
				else if (DtContainerContentTypes.ContentDtBinderSlips == ctParent) {
					// 親が名前付きスリップオブジェクトのルートなら、名前付きスリップオブジェクトとして挿入
					pasteTreeNodeAsNamedObject(ndSelected, clipdata);
				}
				else {
					AppLogger.debug("AbDtContainerEditView#onSelectedMenuContentTreeEditPaste() : Unable to paste transfer data, paste canceled!");
				}
				break;
			case ContentDtSlipList:
				// 選択位置がデータスリップリストの場合
				if (transTopContentTypes.size()==1 && transTopContentTypes.contains(DtContainerContentTypes.ContentDtSlip)) {
					// 親がデータスリップ集合で、データスリップのみの転送データなら、データスリップ集合への挿入
					pasteTreeNodeAsSlipIntoSlipList(ndSelected, clipdata);
				}
				else if (DtContainerContentTypes.ContentDtBinderSlips == ctParent) {
					// 親が名前付きスリップオブジェクトのルートなら、名前付きスリップオブジェクトとして挿入
					pasteTreeNodeAsNamedObject(ndSelected, clipdata);
				}
				else {
					AppLogger.debug("AbDtContainerEditView#onSelectedMenuContentTreeEditPaste() : Unable to paste transfer data, paste canceled!");
				}
				break;
			default:
				AppLogger.debug("AbDtContainerEditView#onSelectedMenuContentTreeEditPaste() : Unable to paste transfer data, paste canceled!");
		}
	}

	/**
	 * コンテントツリー専用メニューの [Rename] が選択されたときに呼び出されるイベントハンドラー。
	 * @param menuItemAction	選択されたメニューアイテムアクション
	 */
	protected void onSelectedMenuContentTreeEditRename(Action menuItemAction) {
		AppLogger.debug("menu ID_TREE_RENAME selected.");
		
		// 単一選択時のみ
		if (_treeComponent.getSelectionCount() != 1)
			return;
		IDtContainerContentTreeNode ndSelected = (IDtContainerContentTreeNode)_treeComponent.getSelectionPath().getLastPathComponent();
		DtContainerContentParentTreeNode ndParent = (DtContainerContentParentTreeNode)ndSelected.getParent();
		if (ndParent == null) {
			// ルートノードが選択されている場合は、処理しない
			return;
		}
		DtContainerContentTypes parentType = ndParent.getContentType();
		if (parentType != DtContainerContentTypes.ContentDtSlipObjects && parentType != DtContainerContentTypes.ContentDtBinderSlips)
			return;
		
		// 兄弟ノードの名前を収集
		Set<String> existNames = new HashSet<>();
		for (int i = 0; i < ndParent.getChildCount(); ++i) {
			existNames.add(ndParent.getChildAt(i).getNodeName());
		}
		//--- 自身の名前を除外
		String nodeName = ndSelected.getNodeName();
		existNames.remove(nodeName);
		DtContainerContentNameValidator validator = new DtContainerContentNameValidator(existNames);
		
		// Input dialog
		String dlgTitle = DtContainerEditorMessages.getInstance().RenameDlg_title;
		String dlgLabel = DtContainerEditorMessages.getInstance().RenameDlg_label + ": ";
		InputDialog dlg;
		Window owner = SwingTools.getWindowForComponent(this);
		if (owner instanceof Frame)
			dlg = new InputDialog((Frame)owner, dlgLabel, null, dlgTitle, null, nodeName, validator);
		else
			dlg = new InputDialog((Dialog)owner, dlgLabel, null, dlgTitle, null, nodeName, validator);
		dlg.pack();
		dlg.setVisible(true);
		dlg.dispose();
		if (dlg.getDialogResult() != IDialogResult.DialogResult_OK) {
			// user canceled
			return;
		}
		nodeName = dlg.getFieldText();
		
		// ノード名の更新(重複無し)
		//--- 古いノードを除去
		getDocument().getDataTreeModel().removeNodeFromParent(ndSelected);
		//--- 名前変更
		ndSelected.setNodeName(nodeName);
		//--- 新しい位置に挿入
		int newPosition = ndParent.findSortedPositionByName(ndSelected);
		if (newPosition < 0) {
			newPosition = -(newPosition + 1);
		}
		getDocument().getDataTreeModel().insertNodeInto(ndSelected, ndParent, newPosition);
		//--- 選択状態の復元
		TreePath tpath = new TreePath(getDocument().getDataTreeModel().getPathToRoot(ndSelected));
		_treeComponent.setSelectionPath(tpath);
		//--- 更新の記録
		setEditorModifiedProperty(true);		// 変更プロパティを更新
		//--- 更新処理
		onTreeNodeRenamed(ndSelected);			// ツリーノードの名前変更時の処理
		_treeComponent.requestFocus();			// ツリーにフォーカスを設定
	}

	/**
	 * コンテントツリー専用メニューの [Move Up] が選択されたときに呼び出されるイベントハンドラー。
	 * @param menuItemAction	選択されたメニューアイテムアクション
	 */
	protected void onSelectedMenuContentTreeEditMoveUp(Action menuItemAction) {
		AppLogger.debug("menu ID_DTCTREE_EDIT_MOVE_UP selected.");
		// メニュー処理
	}

	/**
	 * コンテントツリー専用メニューの [Move Down] が選択されたときに呼び出されるイベントハンドラー。
	 * @param menuItemAction	選択されたメニューアイテムアクション
	 */
	protected void onSelectedMenuContentTreeEditMoveDown(Action menuItemAction) {
		AppLogger.debug("menu ID_DTCTREE_EDIT_MOVE_DOWN selected.");
		// メニュー処理
	}

	//------------------------------------------------------------
	// Event handlers
	//------------------------------------------------------------
	
	protected void onTreeWillExpand(TreeExpansionEvent event) throws ExpandVetoException
	{
		// ツリー展開前イベント
	}
	
	protected void onTreeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException
	{
		// ツリークローズ前イベント
	}
	
	/**
	 * ツリー選択状態の調整が完了した後に呼び出されるイベントハンドラ。
	 */
	protected void onTreeSelectionAdjusted() {
		// 選択ノードの情報を収集
		_treeSelectionInfo.setSelectionPaths(_treeComponent.getSelectionPaths());
		// メニュー項目を更新
		getFrame().updateAllMenuItems();
	}
	
	protected void onTreeSelectionChanged(TreeSelectionEvent tse)
	{
		// 選択数の判定
		if (_treeComponent.getSelectionModel().getSelectionMode() == TreeSelectionModel.SINGLE_TREE_SELECTION) {
			// 単一選択の場合は調整の必要なし
			onTreeSelectionAdjusted();
			return;
		}
		DtContainerContentTreePane srcTree = (DtContainerContentTreePane)tse.getSource();
		int numSelection = srcTree.getSelectionCount();
		if (numSelection <= 1) {
			// 選択がない場合や単一選択の場合は、調整の必要なし
			onTreeSelectionAdjusted();
			return;
		}
		
		// 選択パスの取得
		TreePath[] eventPaths = tse.getPaths();
		TreePath anchorPath  = srcTree.getAnchorSelectionPath();
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
			srcTree.removeSelectionPaths(selectionPaths);
			//--- この場合、再度選択変更イベントが呼び出されるため、
			//--- 選択確定メソッドの呼び出しは行わない
		} else {
			// 削除する選択が存在しない場合は、選択を確定
			onTreeSelectionAdjusted();
		}
	}
	
	protected void onTreeNodeDoubleClicked(MouseEvent me)
	{
		// ツリーノードのマウス左ボタンダブルクリック
		// マウス位置のノードを取得
		//--- ダブルクリック後、マウスイベントの位置情報から対象パスを取得すると、
		//--- ツリーがスクロールした場合に想定していない位置が取得され、そのファイルを開くことに
		//--- なってしまうので、ダブルクリック後は単一選択になることを前提に、
		//--- 位置から取得したパスと、選択されているパスが一致した場合のみ、
		//--- そのパスのファイルを開く
		TreePath pathByPos = _treeComponent.getPathForLocation(me.getX(), me.getY());
		TreePath pathBySel = _treeComponent.getSelectionPath();	// ダブルクリック後は単一選択を前提
		if (pathByPos == null || !Objects.equals(pathByPos, pathBySel))
			return;		// no target node
		
		// ノード取得
		IDtContainerContentTreeNode ndSelected = (IDtContainerContentTreeNode)pathByPos.getLastPathComponent();
		if (!ndSelected.isLeaf()) {
			// 葉ノード以外は、ノードの展開等の操作になるので、処理しない
			return;
		}
		
		// ノードの情報表示
		_viewDetails.setTargetTreeNode(ndSelected);
		_viewDetails.requestFocusInComponent();
	}
	
	protected void onTreePopupMenuShowRequested(MouseEvent me)
	{
		// ツリーのポップアップメニューを表示
		DtContainerEditorMenuBar menuBar = getFrame().getActiveEditorMenuBar();
		if (menuBar != null) {
			menuBar.getContentTreeContextMenu().show(me.getComponent(), me.getX(), me.getY());
		}
	}
	
	/**
	 * メニュー操作により、新しいツリーノードがツリーに挿入されたときに呼び出されるイベントハンドラ。
	 * このメソッドは、ツリーモデルの変更イベントとは別に、内部的に呼び出される。
	 * @param ndParent		新たなツリーノードが挿入された親ツリーノード、<em>ndNewNode</em> が返す親ツリーノードと同一。
	 * @param ndNewNode		新たに挿入されたツリーノード
	 */
	protected void onTreeNodeInsertedByMenuAction(IDtContainerContentTreeNode ndParent, IDtContainerContentTreeNode ndNewNode) {
		// 詳細ビューにノードが挿入された親ノードの子孫が表示されていれば、詳細ビューのパスを更新する
		IDtContainerContentTreeNode ndDetailView = _viewDetails.getTargetTreeNode();
		if (ndDetailView != null && ndDetailView.isNodeAncestor(ndParent)) {
			_viewDetails.refreshContentPathString();
		}
	}
	
	/**
	 * メニュー操作により、ツリーノードが置き換えられたときに呼び出されるイベントハンドラ。
	 * このメソッドは、ツリーモデルの変更イベントとは別に、内部的に呼び出される。
	 * @param ndParent	ツリーノードが置き換えられた親ツリーノード、<em>ndNewNode</em> が返す親ツリーノードと同一。
	 * @param ndNewNode	新たにツリーに加えられたツリーノード
	 * @param ndOldNode	ツリーから除去されたツリーノード
	 */
	protected void onTreeNodeReplacedByMenuAction(IDtContainerContentTreeNode ndParent, IDtContainerContentTreeNode ndNewNode, IDtContainerContentTreeNode ndOldNode) {
		// 詳細ビューに表示されているノードが変更の影響をうける場合は、表示を更新
		IDtContainerContentTreeNode ndDetailView = _viewDetails.getTargetTreeNode();
		if (ndDetailView != null) {
			if (ndDetailView == ndOldNode) {
				// 置き換えられたノードそのものなら、新しいノードの内容を表示する
				_viewDetails.setTargetTreeNode(ndNewNode);
			}
			else if  (ndDetailView.isNodeAncestor(ndOldNode)) {
				// 置き換えられたノードの子孫なら、表示を消す
//				_viewDetails.setVisible(false);
				_viewDetails.setTargetTreeNode(null);
			}
			//--- それ以外なら、そのまま
		}
	}
	
	/**
	 * メニュー操作等により、<em>ndParent</em> の子ノードのうち、<em>firstIndex</em> 以降のノード位置が変更されたときに呼び出されるイベントハンドラ。
	 * このメソッドは、ツリーモデルの変更イベントとは別に、内部的に呼び出される。
	 * <em>ndParent</em> 内のノードを位置を示すインデックスを更新する。
	 * なお、<em>ndParent</em> がインデックス型の子ノードを持つ親ノードではない場合、このメソッドはなにもしない。
	 * @param ndParent		対象の親ノード
	 * @param firstIndex	位置が変更されたノードの範囲の先頭位置を示すインデックス
	 * @throws NullPointerException	<em>ndParent</em> が {@code null} の場合
	 */
	protected void onTreeNodePositionChanged(IDtContainerContentTreeNode ndParent, int firstIndex) {
		// 親ノードがリストなら、指定されたノードの位置以降のインデックスを更新
		if (DtContainerContentTypesManager.getInstance().isIndexedChildren(ndParent.getContentType())) {
			if (firstIndex < ndParent.getChildCount()) {
				int[] modifiedIndices = new int[ndParent.getChildCount() - firstIndex];
				for (int childIndex = firstIndex; childIndex < ndParent.getChildCount(); childIndex++) {
					//--- インデックス更新
					IDtContainerContentTreeNode ndChild = ndParent.getChildAt(childIndex);
					ndChild.setNodePosition(childIndex);
					modifiedIndices[childIndex - firstIndex] = childIndex;
				}
				//--- ノードが更新されたことを、ツリーモデルに通知
				getDocument().getDataTreeModel().nodesChanged(ndParent, modifiedIndices);
				//--- 詳細ビューに ndParent の子孫が表示されていれば、詳細ビューのコンテントパスを更新
				IDtContainerContentTreeNode ndDetailView = _viewDetails.getTargetTreeNode();
				if (ndDetailView != null && ndDetailView.isNodeAncestor(ndParent)) {
					_viewDetails.refreshContentPathString();
				}
			}
		}
	}
	
	/**
	 * メニュー操作等により、ツリーノードが削除されたときに呼び出されるイベントハンドラ。
	 * このメソッドは、ツリーモデルの変更イベントとは別に、内部的に呼び出される。
	 * <em>ndRemoved</em> もしくはその子孫が詳細ビューに表示されている場合に、詳細ビューを閉じる。
	 * @param ndParent		削除されたノードの元の親ノード
	 * @param removedIndex	削除されたノードの、親ノード内の位置
	 * @param ndRemoved		削除されたノード
	 */
	protected void onTreeNodeOneRemovedWithoutRefreshingPosition(IDtContainerContentTreeNode ndParent, int removedIndex, IDtContainerContentTreeNode ndRemoved)
	{
		IDtContainerContentTreeNode ndDetailView = _viewDetails.getTargetTreeNode();
		if (ndDetailView != null && ndDetailView.isNodeAncestor(ndRemoved)) {
			_viewDetails.setTargetTreeNode(null);
		}
	}
	
	/**
	 * メニュー操作により、ツリーノードの名前が変更されたときに呼び出されるイベントハンドラ。
	 * このメソッドは、ツリーモデルの変更イベントとは別に、内部的に呼び出される。
	 * @param ndRenamed	名前が変更されたツリーノード
	 */
	protected void onTreeNodeRenamed(IDtContainerContentTreeNode ndRenamed) {
		// 詳細ビューに名前変更したノードもしくはその子孫が表示されていれば、詳細ビューのパスを更新する
		IDtContainerContentTreeNode ndDetailView = _viewDetails.getTargetTreeNode();
		if (ndDetailView != null && ndDetailView.isNodeAncestor(ndRenamed)) {
			//_viewDetails.refreshContentPath();
			_viewDetails.refreshContentPathString();
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void setEditorModifiedProperty(boolean modified) {
		Boolean oldValue = (Boolean)getClientProperty(IDtContainerEditView.PROP_MODIFIED);
		boolean oldModified = (oldValue != null ? oldValue.booleanValue() : false);
		if (_document != null) {
			_document.setModifiedFlag(modified);
		}
		if (modified != oldModified) {
			putClientProperty(IDtContainerEditView.PROP_MODIFIED, modified);
		}
	}
	
	//protected abstract DtContainerContentTreeModel createContentTreeModel(D document);
	
	/**
	 * 指定されたオブジェクトを JSON 形式のファイルとして保存する。
	 * @param contentType	保存対象のコンテントタイプ
	 * @param target		対象のオブジェクト
	 * @param dstFile		保存先のファイル
	 * @return	保存に成功した場合は {@code true}、そうでない場合は {@code false}
	 */
	protected boolean saveSlipObjectToJsonFile(DtContainerContentTypes contentType, Object target, File dstFile) {
		try {
			switch (contentType) {
				case ContentDtSlip:
				case ContentDtSlipList:
					DtJSON.serialize(dstFile, target, false);	// 型名を強制的に付加する
					break;
				default:
					throw new IllegalStateException("Specified content type could not save as JSON file : " + contentType.contentName());
			}
			return true;
		} catch (IOException ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_FILE_WRITE, ex);
			AppLogger.error(errmsg, ex);
			Application.showErrorMessage(getFrame(), errmsg);
			return false;
		}
		catch (Throwable ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_UNEXPECTED, ex);
			AppLogger.error(errmsg, ex);
			Application.showErrorMessage(getFrame(), errmsg);
			return false;
		}
	}
	
	/**
	 * 指定されたオブジェクトを CSV 形式のファイルとして保存する。
	 * @param contentType	保存対象のコンテントタイプ
	 * @param target		対象のオブジェクト
	 * @param dstFile		保存先のファイル
	 * @param encoding		文字コード名
	 * @return	保存に成功した場合は {@code true}、そうでない場合は {@code false}
	 */
	protected boolean saveSlipElementToCsvFile(DtContainerContentTypes contentType, Object target, File dstFile, String encoding) {
		try {
			switch (contentType) {
				case ContentExalge:
					((Exalge)target).toCSV(dstFile, encoding);
					break;
				case ContentDtalge:
				case ContentDtSlipNote:
				case ContentDtBinderNote:
					((Dtalge)target).toCSV(dstFile, encoding);
					break;
				case ContentExAlgeSet:
					((ExAlgeSet)target).toCSV(dstFile, encoding);
					break;
				case ContentDtAlgeSet:
					((DtAlgeSet)target).toCSV(dstFile, encoding);
					break;
				default:
					throw new IllegalStateException("Specified content type could not save as CSV file : " + contentType.contentName());
			}
			return true;
		}
		catch (UnsupportedEncodingException ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_FILE_UNSUPPORTED_ENCODING, ex);
			AppLogger.error(errmsg, ex);
			Application.showErrorMessage(getFrame(), errmsg);
			return false;
		}
		catch (IOException ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_FILE_WRITE, ex);
			AppLogger.error(errmsg, ex);
			Application.showErrorMessage(getFrame(), errmsg);
			return false;
		}
		catch (Throwable ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_UNEXPECTED, ex);
			AppLogger.error(errmsg, ex);
			Application.showErrorMessage(getFrame(), errmsg);
			return false;
		}
	}
	
	/**
	 * 指定されたオブジェクトを XML 形式のファイルとして保存する。
	 * @param contentType	保存対象のコンテントタイプ
	 * @param target		対象のオブジェクト
	 * @param dstFile		保存先のファイル
	 * @return	保存に成功した場合は {@code true}、そうでない場合は {@code false}
	 */
	protected boolean saveSlipElementToXmlFile(DtContainerContentTypes contentType, Object target, File dstFile) {
		try {
			switch (contentType) {
				case ContentExalge:
					((Exalge)target).toXML(dstFile);
					break;
				case ContentDtalge:
				case ContentDtSlipNote:
				case ContentDtBinderNote:
					((Dtalge)target).toXML(dstFile);
					break;
				case ContentExAlgeSet:
					((ExAlgeSet)target).toXML(dstFile);
					break;
				case ContentDtAlgeSet:
					((DtAlgeSet)target).toXML(dstFile);
					break;
				default:
					throw new IllegalStateException("Specified content type could not save as XML file : " + contentType.contentName());
			}
			return true;
		} catch (DOMException | FactoryConfigurationError | ParserConfigurationException | TransformerException ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_FILE_WRITE, ex);
			AppLogger.error(errmsg, ex);
			Application.showErrorMessage(getFrame(), errmsg);
			return false;
		}
		catch (Throwable ex) {
			String errmsg = DtContainerEditorMessages.getErrorMessage(DtContainerEditorMessages.MessageID.ERR_UNEXPECTED, ex);
			AppLogger.error(errmsg, ex);
			Application.showErrorMessage(getFrame(), errmsg);
			return false;
		}
	}
	
	/*
	 * コンテントツリーコンポーネント用のツールバーを生成する。
	 * @return	ツールバー
	 *
	protected JToolBar createContentTreeToolBar() {
		JToolBar toolbar = new JToolBar(JToolBar.HORIZONTAL);
		toolbar.setFloatable(false);
		
		toolbar.add(Box.createHorizontalGlue());
		//--- add
		toolbar.add(new ToolBarButton(ensureMenuItemAction(DtContainerEditorMenuResources.ID_DTCTREE_EDIT_ADD)));
		//--- replace
		toolbar.add(new ToolBarButton(ensureMenuItemAction(DtContainerEditorMenuResources.ID_DTCTREE_EDIT_REPLACE)));
		//---
		toolbar.addSeparator();
		//--- cut
		//toolbar.add(new ToolBarButton(ensureMenuItemAction(DtContainerEditorMenuResources.ID_DTCTREE_CUT)));
		//--- copy
		//toolbar.add(new ToolBarButton(ensureMenuItemAction(DtContainerEditorMenuResources.ID_DTCTREE_COPY)));
		//--- paste
		//toolbar.add(new ToolBarButton(ensureMenuItemAction(DtContainerEditorMenuResources.ID_DTCTREE_PASTE)));
		//---
		//toolbar.addSeparator();
		//--- delete
		toolbar.add(new ToolBarButton(ensureMenuItemAction(DtContainerEditorMenuResources.ID_DTCTREE_EDIT_DELETE)));
		//---
		toolbar.addSeparator();
		//--- import
		//toolbar.add(new ToolBarButton(ensureMenuItemAction(DtContainerEditorMenuResources.ID_DTCTREE_IMPORT)));
		//--- export
		toolbar.add(new ToolBarButton(ensureMenuItemAction(DtContainerEditorMenuResources.ID_DTCTREE_EDIT_EXPORT)));
		
		return toolbar;
	}
	*/
	
	/**
	 * コンテントツリーコンポーネントを生成する。
	 * @return	コンテントツリーコンポーネント
	 */
	protected DtContainerContentTreePane createContentTreeComponent() {
		// create default tree model
		DtContainerContentTreeModel model = new DtContainerContentTreeModel(new DtContainerContentParentTreeNode());
		
		// create tree component
		DtContainerContentTreePane tree = new DtContainerContentTreePane(model);
		//--- setup tree
		tree.setRootVisible(false);			// ルートノードは表示しない
		tree.setShowsRootHandles(true);	// 最上位にハンドルを表示する
		//--- add listeners to tree
		DtContainerContentTreeListener treeListener = new DtContainerContentTreeListener();
		tree.addTreeSelectionListener(treeListener);
		tree.addTreeWillExpandListener(treeListener);
		//--- add mouse listener to tree
		tree.addMouseListener(new DtContainerContentTreeMouseListener());
		
		return tree;
	}
	
	/*
	protected void addEditor(String tabTitle, JComponent editor) {
		//attachEditorModifiedPropertyChangeHandler(editor);
		//editor.setPopupMenu(pmenuForEditor);
		//String tabTitle = getEditorTabTitle(editor);
		//String tabTooltip = editor.getDocumentPath();
		//if (tabTooltip != null)
		//	_tabEditor.addTab(tabTitle, null, editor.getComponent(), tabTooltip);
		//else
		//	_tabEditor.addTab(tabTitle, editor.getComponent());
		_tabContentEditor.addTab(tabTitle, editor);
		_tabContentEditor.setSelectedComponent(editor);
		//editor.jumpToBegin();
		//editor.getTextComponent().setStatusBar(statusBar);
		//editor.requestFocusInTextComponent();
		editor.requestFocus();
		//editor.requestFocusInComponent();
		
	}
	/*---*/

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	public class DtContainerContentTreeListener implements TreeWillExpandListener, TreeSelectionListener
	{
		@Override
		public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException
		{
			onTreeWillExpand(event);
		}

		@Override
		public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException
		{
			onTreeWillCollapse(event);
		}
		
		@Override
		public void valueChanged(TreeSelectionEvent event) {
			onTreeSelectionChanged(event);
		}
	}
	
	protected class DtContainerContentTreeMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount()==2) {
				// 左ボタンのダブルクリック
				onTreeNodeDoubleClicked(e);
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
				//requestFocusInComponent();
				onTreePopupMenuShowRequested(me);
			}
		}
	}
}
