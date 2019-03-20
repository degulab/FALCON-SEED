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
 *  Copyright 2007-2011  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)AbSpreadSheetView.java	1.10	2011/02/14
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbSpreadSheetView.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.editor.view.table;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.CellEditor;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.table.JTableHeader;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.falconseed.editor.document.IEditorDocument;
import ssac.falconseed.editor.document.IEditorTableDocument;
import ssac.falconseed.editor.plugin.IComponentManager;
import ssac.falconseed.editor.view.IEditorView;
import ssac.falconseed.editor.view.dialog.AbFindReplaceHandler;
import ssac.falconseed.editor.view.dialog.JumpDialog;
import ssac.falconseed.editor.view.text.ITextComponent;
import ssac.falconseed.runner.menu.RunnerMenuBar;
import ssac.falconseed.runner.menu.RunnerMenuResources;
import ssac.falconseed.runner.view.RunnerFrame;
import ssac.util.Objects;
import ssac.util.Strings;
import ssac.util.Validations;
import ssac.util.logging.AppLogger;
import ssac.util.swing.IDialogResult;
import ssac.util.swing.SwingTools;
import ssac.util.swing.menu.AbMenuItemAction;
import ssac.util.swing.table.SpreadSheetColumnHeader;
import ssac.util.swing.table.SpreadSheetRowHeader;
import ssac.util.swing.table.SpreadSheetTable;
import ssac.util.swing.table.SpreadSheetTable.CellIndex;

/**
 * スプレッドシートのようなテーブルビューの基本機能。
 * 
 * @version 1.10	2011/02/14
 */
public abstract class AbSpreadSheetView<D extends IEditorTableDocument>
extends JPanel
implements IEditorView
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	private UndoManager				_undoMan;
	private TableDocumentHandler		_hDocument;
	private TableUndoHandler			_hUndo;
	private TableSelectionHandler		_hSelection;
	private AbFindReplaceHandler		_hFind;
	
	private JScrollPane		_paneScroll;
	private SpreadSheetTable	_paneTable;
	private JSplitPane			_paneSplit;
	private JPanel				_paneCellInfo;
	private JLabel				_lblCellLocation;
	private JButton			_btnCellEditCancel;
	private JButton			_btnCellEditCommit;
	private JTextComponent		_tcCellTextEditor;
	private CellIndex			_cellInfoTargetLocation;
	private Object				_cellInfoCachedValue;
	
	private JPopupMenu			_tableHeaderPopupMenu;
	
	private CellInfoEditActionHandler	_ciEditCommitAction;
	private CellInfoEditActionHandler	_ciEditCancelAction;
	
	private D	_tableDocument;

	private volatile boolean		_compoundEdits = false;
	private CompoundEdit			_packEdits = null;
	
	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * このビューの新しいインスタンスを生成する。
	 */
	public AbSpreadSheetView() {
		super(new BorderLayout());
	}

	/*
	 * このビューの新しいインスタンスを生成する。
	 * @param document	このビューに割り当てられるドキュメント
	 * @throws NullPointerException <code>document</code> が <tt>null</tt> の場合
	 *
	public AbSpreadSheetView(D document) {
		super(new BorderLayout());
		
		// create panes
		this.paneTable = createDefaultTablePane();
		this.paneScroll = createDefaultScrollPane();
		
		// setup components
		initialComponents();
		
		// set document
		setDocument(document);
	}
	*/
	
	//------------------------------------------------------------
	// Initializer
	//------------------------------------------------------------

	/**
	 * このオブジェクトのコンポーネントを初期化する。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * コンストラクタ呼び出しの後、必ずこのメソッドを呼び出して初期化を完了させること。
	 * </blockquote>
	 * @param initialDocument	初期ドキュメントとする <code>IEditorTableDocument</code> インタフェースを実装するオブジェクト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public void initialComponent(D initialDocument) {
		Validations.validNotNull(initialDocument, "'initialDocument' argument is null.");
		
		// create field instances
		createFieldInstances();
		
		// setup components before call SetDocument() method
		setupComponentsBeforeSetDocument();
		
		// set initial document
		setDocument(initialDocument);
		
		// setup components after called SetDocument() method
		setupComponentsAfterSetDocument();
	}

	/**
	 * このオブジェクトのフィールドのインスタンスを生成する。
	 */
	protected void createFieldInstances() {
		this._undoMan = createUndoManager();
		this._hDocument = createTableDocumentHandler();
		this._hUndo = createTableUndoHandler();
		this._hSelection = createTableSelectionHandler();
		this._hFind = createTableFindReplaceHandler();
		this._paneScroll = createScrollPane();
		this._paneTable  = createSpreadSheetTable();
		this._paneCellInfo = createCellInfoPane();
		this._paneSplit = createSplitPane();
	}

	/**
	 * 初期ドキュメントがセットされる前のコンポーネントの初期化
	 */
	protected void setupComponentsBeforeSetDocument() {
		// setup table
		final SpreadSheetTable table = getTableComponent();
		
		// setup scroll pane
		SpreadSheetRowHeader rowHeader = table.getTableRowHeader();
		final JScrollPane scroll = getScrollPane();
		scroll.setViewportView(table);
		scroll.setRowHeaderView(rowHeader);
		//--- setup border
		Component cTableUpperLeft = createTableUpperLeftCornerComponent();
		if (cTableUpperLeft != null) {
			scroll.setCorner(JScrollPane.UPPER_LEFT_CORNER, cTableUpperLeft);
			setupTableUpperLeftCornerMouseAction(cTableUpperLeft);
		}

		// setup split pane
		JSplitPane split = getSplitPane();
		split.setTopComponent(getCellInfoPane());
		split.setBottomComponent(scroll);
		// add to panel
		this.add(split, BorderLayout.CENTER);
		
		// setup actions
		final TableSelectionHandler selectionHandler = getTableSelectionHandler();
		table.getSelectionModel().addListSelectionListener(selectionHandler);
		table.getColumnModel().getSelectionModel().addListSelectionListener(selectionHandler);
	}

	/**
	 * 初期ドキュメントがセットされた後のコンポーネントの初期化
	 */
	protected void setupComponentsAfterSetDocument() {
		// Mouse action for Popup menu
		getTableComponent().getTableHeader().addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e) {
				evaluateColumnHeaderPopupMenu(e);
			}
			public void mouseReleased(MouseEvent e) {
				evaluateColumnHeaderPopupMenu(e);
			}
		});
	}
	
	/**
	 * このビューの標準スクロールペインのインスタンスを生成する。
	 * @return	<code>JScrollPane</code> のインスタンス
	 */
	protected JScrollPane createScrollPane() {
		return new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	}

	/**
	 * このビューの標準テーブルペインのインスタンスを生成する。
	 * @return <code>SpreadSheetTable</code> のインスタンス
	 */
	protected SpreadSheetTable createSpreadSheetTable() {
		return new SpreadSheetTable();
	}
	
	/**
	 * このビューの分割ペインのインスタンスを生成する。
	 * @return <code>JSplitPane</code> のインスタンス
	 */
	protected JSplitPane createSplitPane() {
		JSplitPane pane = new JSplitPane(
								JSplitPane.VERTICAL_SPLIT,	// 縦分割
								false);						// 分割バー操作中は再描画しない
		return pane;
	}

	/**
	 * このビューのセル情報ペインのインスタンスを生成する。
	 * @return	<code>JPanel</code> のインスタンス
	 */
	protected JPanel createCellInfoPane() {
		// 配置オブジェクトの生成
		JLabel lblLocation = getCellLocationLabel();
		JButton btnCancel  = getCellEditCancelButton();
		JButton btnCommit  = getCellEditCommitButton();
		JTextComponent tc  = getCellTextEditorComponent();
		JScrollPane scroll = new JScrollPane(
									JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
									JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setViewportView(tc);
		
		// ラベルの幅を調整
		lblLocation.setText(" XXXXXXXX, XXXXXXXX ");
		Dimension dim = lblLocation.getPreferredSize();
		lblLocation.setPreferredSize(dim);
		lblLocation.setMinimumSize(dim);
		
		// インナーパネルレイアウト
		JPanel innerPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 1;
		//--- label
		gbc.fill   = GridBagConstraints.VERTICAL;
		innerPanel.add(lblLocation, gbc);
		gbc.gridx++;
		//--- cancel button
		gbc.fill   = GridBagConstraints.NONE;
		innerPanel.add(btnCancel, gbc);
		gbc.gridx++;
		//--- commit button
		gbc.fill   = GridBagConstraints.NONE;
		innerPanel.add(btnCommit, gbc);
		gbc.gridx++;
		
		// パネルレイアウト
		JPanel panel = new JPanel(new GridBagLayout());
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 1;
		//--- inner panel
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill   = GridBagConstraints.NONE;
		panel.add(innerPanel, gbc);
		gbc.gridx++;
		//--- editor
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.BOTH;
		panel.add(scroll, gbc);
		gbc.gridx++;
		
		// 表示の初期化
		lblLocation.setText("");
		btnCancel.setEnabled(false);
		//btnCancel.setVisible(false);
		btnCommit.setEnabled(false);
		//btnCommit.setVisible(false);
		tc.setText("");
		tc.setEditable(false);

		// setup actions
		//InputMap imap = tc.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		InputMap imap = SwingUtilities.getUIInputMap(tc, WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		if (imap == null) {
			imap = SwingUtilities.getUIInputMap(tc, WHEN_FOCUSED);
		}
		ActionMap amap = SwingUtilities.getUIActionMap(tc);
		//--- 編集アクション設定
		Action action = getCellInfoEditCancelAction();
		SwingTools.registerActionToMaps(imap, amap,
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				action.getValue(Action.ACTION_COMMAND_KEY),
				action);
		action = getCellInfoEditCommitAction();
		SwingTools.registerActionToMaps(imap, amap,
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
				action.getValue(Action.ACTION_COMMAND_KEY),
				action);
		
		// completed
		return panel;
	}

	/**
	 * セル情報ペインのテキストコンポーネントで[Enter]キーが押されたときの
	 * アクションを返す。
	 * @return	<code>Action</code> オブジェクト
	 */
	protected Action getCellInfoEditCommitAction() {
		if (_ciEditCommitAction == null) {
			_ciEditCommitAction = new CellInfoEditActionHandler(CellInfoEditActionHandler.COMMAND_COMMIT, "cellEditCommit");
		}
		return _ciEditCommitAction;
	}

	/**
	 * セル情報ペインのテキストコンポーネントで[Escape]キーが押されたときの
	 * アクションを返す。
	 * @return	<code>Action</code> オブジェクト
	 */
	protected Action getCellInfoEditCancelAction() {
		if (_ciEditCancelAction == null) {
			_ciEditCancelAction = new CellInfoEditActionHandler(CellInfoEditActionHandler.COMMAND_CANCEL, "cellEditCancel");
		}
		return _ciEditCancelAction;
	}

	/**
	 * セル情報パネルに表示するセル位置ラベルのインスタンスを生成する。
	 * @return	<code>JLabel</code> のインスタンス
	 */
	protected JLabel createCellLocationLabel() {
		JLabel lbl = new JLabel();
		lbl.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		lbl.setHorizontalAlignment(JLabel.CENTER);
		return lbl;
	}

	/**
	 * セル情報パネルに表示するセル編集キャンセルボタンのインスタンスを生成する。
	 * @return	<code>JButton</code> のインスタンス
	 */
	protected JButton createCellEditCancelButton() {
		JButton btn = CommonResources.createIconButton(CommonResources.ICON_DELETE, CommonMessages.getInstance().Button_Cancel);
		btn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				onCellInfoEditCanceled();
			}
		});
		return btn;
	}

	/**
	 * セル情報パネルに表示するセル編集コミットボタンのインスタンスを生成する。
	 * @return	<code>JButton</code> のインスタンス
	 */
	protected JButton createCellEditCommitButton() {
		JButton btn = CommonResources.createIconButton(CommonResources.ICON_EDIT, CommonMessages.getInstance().Button_Update);
		btn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				onCellInfoEditCommitted();
			}
		});
		return btn;
	}

	/**
	 * セル情報パネルに表示するセル内容テキストエディタのインスタンスを生成する。
	 * @return <code>JTextComponent</code> のインスタンス
	 */
	protected JTextComponent createCellTextEditorComponent() {
		JTextArea ta = new JTextArea();
		ta.setLineWrap(true);
		ta.setWrapStyleWord(true);
		ta.setTabSize(4);
		return ta;
	}

	/**
	 * このビューのアンドゥマネージャのインスタンスを生成する。
	 * @return	<code>UndoManager</code> のインスタンス
	 */
	protected UndoManager createUndoManager() {
		return new UndoManager();
	}

	/**
	 * このビューのドキュメントハンドラのインスタンスを生成する。
	 * @return	<code>TableDocumentHandler</code> のインスタンス
	 */
	protected TableDocumentHandler createTableDocumentHandler() {
		return new TableDocumentHandler();
	}

	/**
	 * このビューのアンドゥハンドラのインスタンスを生成する。
	 * @return	<code>TableUndoHandler</code> のインスタンス
	 */
	protected TableUndoHandler createTableUndoHandler() {
		return new TableUndoHandler();
	}

	/**
	 * このビューのテーブルセレクションハンドラのインスタンスを生成する。
	 * @return	<code>TableSelectionHandler</code> のインスタンス
	 */
	protected TableSelectionHandler createTableSelectionHandler() {
		return new TableSelectionHandler();
	}

	/**
	 * このビューの検索置換ハンドラのインスタンスを生成する。
	 * @return	<code>TableFindReplaceHandler</code> のインスタンス
	 */
	protected AbFindReplaceHandler createTableFindReplaceHandler() {
		return new TableFindReplaceHandler();
	}
	
	/**
	 * テーブル左上コーナーのコンポーネントを生成する。
	 * このコンポーネントは、テーブルをビューとする <code>JScrollPane</code> に設定される。
	 * @return	<code>JScrollPane</code> の左上コーナーに設定されるコンポーネント
	 */
	protected Component createTableUpperLeftCornerComponent() {
		JPanel panel = new JPanel();
		panel.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		return panel;
	}

	/**
	 * テーブル左上コーナーのコンポーネントにマウスアクションを設定する。
	 * このメソッドで設定されるマウスアクションは、このクラスのメンバーメソッドをイベントハンドラとして呼び出す。
	 * @param tableUpperLeftComponent	テーブル左上コーナーのコンポーネント
	 */
	protected void setupTableUpperLeftCornerMouseAction(Component tableUpperLeftComponent) {
		tableUpperLeftComponent.addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent e) {
				onTableUpperLeftCornerMouseClicked(e);
			}

			public void mouseEntered(MouseEvent e) {
				onTableUpperLeftCornerMouseEntered(e);
			}

			public void mouseExited(MouseEvent e) {
				onTableUpperLeftCornerMouseExited(e);
			}

			public void mousePressed(MouseEvent e) {
				onTableUpperLeftCornerMousePressed(e);
			}

			public void mouseReleased(MouseEvent e) {
				onTableUpperLeftCornerMouseReleased(e);
			}
		});
	}

	/**
	 * テーブル列ヘッダのポップアップメニューを生成する。
	 * @return	<code>JPopupMenu</code> オブジェクト
	 */
	protected JPopupMenu createTableHeaderPopupMenu() {
		JPopupMenu menu = new JPopupMenu();
		//--- 列幅の自動調整
		menu.add(new AbstractAction(CommonMessages.getInstance().TableHeaderPopupMenu_Column_AdjustColumnWidth) {
			public void actionPerformed(ActionEvent e) {
				onTableHeaderPopupMenuSelected_AdjustColumnWidth(e);
			}
		});
		//--- 標準の列幅に設定
		menu.add(new AbstractAction(CommonMessages.getInstance().TableHeaderPopupMenu_Column_SetWidthToDefault){
			public void actionPerformed(ActionEvent e) {
				onTableHeaderPopupMenuSelected_SetColumnWidthToDefault(e);
			}
		});

		// completed
		return menu;
	}
	
	//------------------------------------------------------------
	// Field accessor
	//------------------------------------------------------------

	/**
	 * このビューのアンドゥマネージャを取得する。
	 * @return	<code>UndoManager</code> のインスタンス
	 */
	protected UndoManager getUndoManager() {
		return _undoMan;
	}

	/**
	 * このビューのドキュメントハンドラを取得する。
	 * @return	<code>TableDocumentHandler</code> のインスタンス
	 */
	protected TableDocumentHandler getTableDocumentHandler() {
		return _hDocument;
	}

	/**
	 * このビューのアンドゥハンドラを取得する。
	 * @return	<code>TableUndoHandler</code> のインスタンス
	 */
	protected TableUndoHandler getTableUndoHandler() {
		return _hUndo;
	}

	/**
	 * このビューのテーブルセレクションハンドラを取得する。
	 * @return	<code>TableSelectionHandler</code> のインスタンス
	 */
	protected TableSelectionHandler getTableSelectionHandler() {
		return _hSelection;
	}

	/**
	 * このビューのスクロールコンポーネントを取得する。
	 * @return	<code>JScrollPane</code> のインスタンス
	 */
	protected JScrollPane getScrollPane() {
		return _paneScroll;
	}
	
	/**
	 * このビューの分割ペインのインスタンスを取得する。
	 * @return <code>JSplitPane</code> のインスタンス
	 */
	protected JSplitPane getSplitPane() {
		return _paneSplit;
	}
	
	/**
	 * このビューのセル情報ペインのインスタンスを取得する。
	 * @return	<code>JPanel</code> のインスタンス
	 */
	protected JPanel getCellInfoPane() {
		return _paneCellInfo;
	}
	
	/**
	 * セル情報パネルに表示するセル位置ラベルのインスタンスを取得する。
	 * @return	<code>JLabel</code> のインスタンス
	 */
	protected JLabel getCellLocationLabel() {
		if (_lblCellLocation == null) {
			_lblCellLocation = createCellLocationLabel();
		}
		return _lblCellLocation;
	}

	/**
	 * セル情報パネルに表示するセル編集キャンセルボタンのインスタンスを取得する。
	 * @return	<code>JButton</code> のインスタンス
	 */
	protected JButton getCellEditCancelButton() {
		if (_btnCellEditCancel == null) {
			_btnCellEditCancel = createCellEditCancelButton();
		}
		return _btnCellEditCancel;
	}

	/**
	 * セル情報パネルに表示するセル編集コミットボタンのインスタンスを取得する。
	 * @return	<code>JButton</code> のインスタンス
	 */
	protected JButton getCellEditCommitButton() {
		if (_btnCellEditCommit == null) {
			_btnCellEditCommit = createCellEditCommitButton();
		}
		return _btnCellEditCommit;
	}

	/**
	 * セル情報パネルに表示するセル内容テキストエディタのインスタンスを取得する。
	 * @return <code>JTextComponent</code> のインスタンス
	 */
	protected JTextComponent getCellTextEditorComponent() {
		if (_tcCellTextEditor == null) {
			_tcCellTextEditor = createCellTextEditorComponent();
		}
		return _tcCellTextEditor;
	}

	/**
	 * セル情報パネルの表示対象となるセル位置を返す。
	 * @return	単一のセルが選択されている場合はそのセル位置を返す。
	 * 			それ以外の場合は <tt>null</tt> を返す。
	 */
	protected CellIndex getCellInfoTargetLocation() {
		return _cellInfoTargetLocation;
	}

	/**
	 * セル情報パネルの表示対象となるセル位置を設定する。
	 * @param newLocation	設定するセルの位置
	 */
	protected void setCellInfoTargetLocation(CellIndex newLocation) {
		_cellInfoTargetLocation = newLocation;
	}

	/**
	 * セル情報パネルの表示対象となるセルの値を返す。
	 * このセルの値は変更前の値となる。
	 * @return	単一のセルが選択されている場合はそのセルの値を返す。
	 * 			それ以外の場合は <tt>null</tt> を返す。
	 */
	protected Object getCellInfoTargetCachedValue() {
		return _cellInfoCachedValue;
	}

	/**
	 * セル情報パネルの表示対象となるセルの値として、指定された値をキャッシュする。
	 * @param newValue	キャッシュする値
	 */
	protected void setCellInfoTargetCachedValue(Object newValue) {
		_cellInfoCachedValue = newValue;
	}

	/**
	 * テーブル列ヘッダのポップアップメニューを取得する。
	 * @return	<code>JPopupMenu</code> オブジェクト
	 */
	protected JPopupMenu getTableHeaderPopupMenu() {
		if (_tableHeaderPopupMenu == null) {
			_tableHeaderPopupMenu = createTableHeaderPopupMenu();
		}
		return _tableHeaderPopupMenu;
	}

	/*
	 * このビューのコンポーネントを初期化する。
	 * この初期化は、ビューコンポーネントへのドキュメント設定前に行われる。
	 * そのため、フィールド <code>tableDocument</code> は <tt>null</tt> である。
	 *
	protected void initialComponents() {
		// setup table settings
		
		// setup row header
		SpreadSheetRowHeader rowHeader = paneTable.getTableRowHeader();
		
		// setup scroll pane
		paneScroll.setViewportView(paneTable);
		paneScroll.setRowHeaderView(rowHeader);
		//--- setup border
		JPanel borderPanel = new JPanel();
		borderPanel.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		paneScroll.setCorner(JScrollPane.UPPER_LEFT_CORNER, borderPanel);
		
		// add to Panel
		add(paneScroll, BorderLayout.CENTER);
		
		// setup actions
		paneTable.getSelectionModel().addListSelectionListener(hSelection);
		paneTable.getColumnModel().getSelectionModel().addListSelectionListener(hSelection);
	}
	*/

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このビューに新しいドキュメントを設定する。
	 * @param newDocument	このビューに設定するドキュメント
	 * @throws NullPointerException	<code>newDocument</code> が <tt>null</tt> の場合
	 */
	public void setDocument(D newDocument) {
		Validations.validNotNull(newDocument);
		
		// check document already attached
		D oldDocument = getDocument();
		if (oldDocument == newDocument) {
			// already attached
			return;
		}
		
		// get handlers
		final TableDocumentHandler hDocument = getTableDocumentHandler();
		final TableUndoHandler hUndo = getTableUndoHandler();
		
		// detach old document
		if (oldDocument != null) {
			oldDocument.removeTableModelListener(hDocument);
			oldDocument.removeUndoableEditListener(hUndo);
			
			// release document resources
			oldDocument.releaseViewResources();
		}
		
		// attach new document
		this._tableDocument = newDocument;
		getTableComponent().setModel(newDocument);
		newDocument.addTableModelListener(hDocument);
		newDocument.addUndoableEditListener(hUndo);
		
		// refresh
		refreshEditingStatus();
	}

	/**
	 * フレームメニューの状態を更新する。
	 */
	public void updateEditorMenus() {
		RunnerFrame frame = getFrame();
		if (frame != null) {
			fireUpdateMenusByEditState(frame);
		}
	}
	
	static public Font getDefaultFont() {
		return UIManager.getFont("Table.font");
	}

	//------------------------------------------------------------
	// Implements interfaces for Editing
	//------------------------------------------------------------

	/**
	 * テーブルエディタのUndo可能編集内容の集約を開始する。
	 * Undo可能編集内容の集約は、複数の編集操作を１回のUndoで元に戻すため、
	 * 複数の編集内容をコンテナに集約するための操作となる。
	 */
	public void startCompoundUndoableEdit() {
		this._compoundEdits = true;
	}

	/**
	 * テーブルエディタのUndo可能編集内容の集約を終了する。
	 * この操作は、<code>{@link #startCompoundUndoableEdit()}</code> によって開始された編集操作の
	 * 集約を完了し、コンテナに集約された編集操作を UndoManger へ登録する。
	 * <p>
	 * <b>(注)</b>
	 * <blockquote>
	 * このメソッドは、Swingアイテムを操作するメソッドを内部で呼び出すため、
	 * EventQueue が実行されているスレッドから、呼び出すことが必須となる。
	 * </blockquote>
	 */
	public void endCompoundUndoableEdit() {
		if (this._compoundEdits) {
			if (this._packEdits != null) {
				this._packEdits.end();
				getUndoManager().addEdit(this._packEdits);
				this._packEdits = null;
//				RunnerFrame frame = getFrame();
//				if (frame != null) {
//					frame.updateMenuItem(RunnerMenuResources.ID_EDIT_UNDO);
//					frame.updateMenuItem(RunnerMenuResources.ID_EDIT_REDO);
//				}
			}
			this._compoundEdits = false;
		}
	}

	/**
	 * コンポーネントが編集可能であるかを検証する。
	 * 
	 * @return 編集可能であれば true
	 */
	public boolean canEdit() {
		return (_paneTable.isEditable() && _paneTable.isEnabled());
	}

	/**
	 * コンポーネントで選択されているセルが
	 * 存在するかを検証する。
	 * 
	 * @return 選択セルが存在していれば true
	 */
	public boolean hasSelectedCells() {
		return (_paneTable.getSelectedRowCount()>0 && _paneTable.getSelectedColumnCount()>0);
	}

	/**
	 * コンポーネントが Undo 可能かを検証する。
	 * 
	 * @return Undo 可能なら true
	 */
	public boolean canUndo() {
		if (canEdit()) {
			return getUndoManager().canUndo();
		} else {
			return false;
		}
	}

	/**
	 * コンポーネントが Redo 可能かを検証する。
	 * 
	 * @return Redo 可能なら true
	 */
	public boolean canRedo() {
		if (canEdit()) {
			return getUndoManager().canRedo();
		} else {
			return false;
		}
	}

	/**
	 * コンポーネントが Cut 可能かを検証する。
	 * @return	Cut 可能なら true
	 */
	public boolean canCut() {
		if (canEdit()) {
			return hasSelectedCells();
		} else {
			return false;
		}
	}

	/**
	 * コンポーネントが Copy 可能かを検証する。
	 * @return Copy 可能なら true
	 */
	public boolean canCopy() {
		return hasSelectedCells();
	}

	/**
	 * クリップボードにペースト可能なデータが存在し、
	 * コンポーネントにペースト可能な状態かを検証する。
	 * 
	 * @return ペースト可能なら true
	 */
	public boolean canPaste() {
		if (canEdit()) {
			//--- クリップボードのデータの有無を判定
			final SpreadSheetTable table = getTableComponent();
			Clipboard clip = table.getToolkit().getSystemClipboard();
			Transferable trans = clip.getContents(table);
			if (trans != null && trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * テーブルコンポーネントを取得する。
	 * 
	 * @return <code>SpreadSheetTable</code> インスタンス
	 */
	public SpreadSheetTable getTableComponent() {
		return _paneTable;
	}

	/**
	 * コンポーネントに対して Undo する。
	 */
	public void undo() {
		try {
			getUndoManager().undo();
		} catch (CannotUndoException ex) {
			AppLogger.debug("Unable to undo.", ex);
		}
		updateEditorModifiedProperty();
//		RunnerFrame frame = getFrame();
//		if (frame != null) {
//			frame.updateMenuItem(RunnerMenuResources.ID_EDIT_UNDO);
//			frame.updateMenuItem(RunnerMenuResources.ID_EDIT_REDO);
//		}
	}

	/**
	 * コンポーネントに対して Redo する。
	 */
	public void redo() {
		try {
			getUndoManager().redo();
		} catch (CannotRedoException ex) {
			AppLogger.debug("Unable to redo.", ex);
		}
//		RunnerFrame frame = getFrame();
//		if (frame != null) {
//			frame.updateMenuItem(RunnerMenuResources.ID_EDIT_UNDO);
//			frame.updateMenuItem(RunnerMenuResources.ID_EDIT_REDO);
//		}
	}

	/**
	 * 選択セルの内容のみのカット
	 */
	public void cut() {
		final SpreadSheetTable table = getTableComponent();
		table.requestFocusInWindow();
		table.cut();
		RunnerFrame frame = getFrame();
		if (frame != null) {
			fireUpdateMenusByCutCopy(frame);
		}
	}

	/**
	 * 選択セルの内容のコピー
	 */
	public void copy() {
		final SpreadSheetTable table = getTableComponent();
		table.requestFocusInWindow();
		table.copy();
		RunnerFrame frame = getFrame();
		if (frame != null) {
			fireUpdateMenusByCutCopy(frame);
		}
	}

	/**
	 * 現在の選択セル位置を基準にペースト
	 */
	public void paste() {
		final SpreadSheetTable table = getTableComponent();
		table.requestFocusInWindow();
		table.paste();
	}

	/**
	 * 選択セルの内容のデリート
	 */
	public void delete() {
		final SpreadSheetTable table = getTableComponent();
		table.requestFocusInWindow();
		table.delete();
	}

	/**
	 * コンポーネントの全てのセルを選択する。
	 */
	public void selectAll() {
		final SpreadSheetTable table = getTableComponent();
		table.requestFocusInWindow();
		table.selectAll();
	}

	/**
	 * コンポーネントの行数を取得する。
	 * 
	 * @return コンポーネントの行数
	 */
	public int getLineCount() {
		return getTableComponent().getRowCount();
	}

	/**
	 * 選択セルを先頭行先頭列へ移動する。
	 */
	public void jumpToBegin() {
		final SpreadSheetTable table = getTableComponent();
		table.requestFocusInWindow();
		if (table.getRowCount() > 0 && table.getColumnCount() > 0) {
			if (table.isEditing())
				table.removeEditor();
			table.clearSelection();
			table.setFocusToCell(0, 0);
		}
	}

	/**
	 * 選択セルを終端行先頭列へ移動する。
	 */
	public void jumpToEnd() {
		final SpreadSheetTable table = getTableComponent();
		table.requestFocusInWindow();
		if (table.getRowCount() > 0 && table.getColumnCount() > 0) {
			if (table.isEditing())
				table.removeEditor();
			table.clearSelection();
			table.setFocusToCell(table.getRowCount()-1, 0);
		}
	}

	/**
	 * 選択セルを指定行の先頭列へ移動する。
	 * 行番号は、先頭行を 1 とする。
	 * 
	 * @param lineNo 1 から始まる行番号
	 */
	public void jumpToLine(int lineNo) {
		final SpreadSheetTable table = getTableComponent();
		table.requestFocusInWindow();
		int numRows = table.getRowCount();
		if (numRows > 0 && table.getColumnCount() > 0) {
			if (lineNo < 1)
				lineNo = 1;
			else if (lineNo > numRows)
				lineNo = numRows;
			if (table.isEditing())
				table.removeEditor();
			table.clearSelection();
			table.setFocusToCell(lineNo-1, 0);
		}
	}

	//------------------------------------------------------------
	// Implements IEditorView interfaces
	//------------------------------------------------------------
	
	/**
	 * このビューオブジェクトに関連付けられている全てのリソースを開放する。
	 */
	public void destroy() {
		// detach document
		D oldDocument = getDocument();
		if (oldDocument != null) {
			oldDocument.removeTableModelListener(getTableDocumentHandler());
			oldDocument.removeUndoableEditListener(getTableUndoHandler());
			
			// release document resources
			oldDocument.releaseViewResources();
		}
	}
	
	/**
	 * このドキュメントの保存先ファイルが読み取り専用の場合に <tt>true</tt> を返す。
	 * ファイルそのものが読み取り専用ではない場合でも、モジュールパッケージに
	 * 含まれるファイルの場合にも <tt>true</tt> を返す。
	 */
	public boolean isReadOnly() {
		return getFrame().isReadOnlyFile(getDocumentFile());
	}
	
	/**
	 * このドキュメントが編集されているかを判定する。
	 * @return	編集されていれば <tt>true</tt> を返す。
	 */
	public boolean isModified() {
		if (this._tableDocument != null) {
			Boolean value = (Boolean)getClientProperty(IEditorView.PROP_MODIFIED);
			return (value != null ? value.booleanValue() : false);
		} else {
			return false;
		}
	}
	
	/**
	 * ドキュメントに適用されたエンコーディングとなる文字セット名を返す。
	 * このメソッドの実装では <tt>null</tt> を返してはならない。
	 */
	public String getLastEncodingName() {
		return getDocument().getLastEncodingName();
	}
	
	/**
	 * このビューに関連付けられているドキュメントのタイトルを取得する。
	 * @return	タイトル文字列
	 */
	public String getDocumentTitle() {
		return (_tableDocument==null ? null : _tableDocument.getTitle());
	}
	
	/**
	 * このビューに関連付けられているドキュメントの保存先ファイルを取得する。
	 * 保存先ファイルが定義されていない場合は <tt>null</tt> を返す。
	 * @return	ドキュメントの保存先ファイルを返す。保存先ファイルが設定されて
	 * 			いない場合は <tt>null</tt> を返す。
	 */
	public File getDocumentFile() {
		return (_tableDocument==null ? null : _tableDocument.getTargetFile());
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
		return _tableDocument;
	}
	
	/**
	 * このビューに関連付けられたドキュメントの設定情報を、
	 * 最新の情報に更新する。
	 * @return	更新された場合に <tt>true</tt> を返す。
	 * @since 1.14
	 */
	public boolean refreshDocumentSettings() {
		if (_tableDocument != null) {
			return _tableDocument.refreshSettings();
		} else {
			return false;
		}
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
		return _paneTable.hasFocus();
	}
	
	/**
	 * このビューのコンポーネントにフォーカスを要求する。
	 * ビューが複数のコンポーネントを持つ場合、標準となる
	 * コンポーネントにフォーカスを設定する。
	 */
	public void requestFocusInComponent() {
		_paneTable.setFocus();
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
	 * このドキュメントを管理するコンポーネント・マネージャを返す。
	 * @return	コンポーネント・マネージャ
	 */
	public IComponentManager getManager() {
		return _tableDocument.getManager();
	}
	
	/**
	 * このビューに関連付けられたドキュメント専用のメニューバーを返す。
	 * 専用メニューバーが未定義の場合は <tt>null</tt> を返す。
	 * @return	ドキュメント専用メニューバー
	 */
	public RunnerMenuBar getDocumentMenuBar() {
		return null;	// 標準メニューバーを使用
	}

	/**
	 * このビューに関連付けられたドキュメントの保存先ファイルが、
	 * 移動可能かを判定する。
	 * @return 移動可能なら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 1.17
	 */
	public boolean canMoveDocumentFile() {
		// ドキュメントがない場合は、移動不可
		IEditorDocument document = getDocument();
		if (document == null) {
			return false;
		}
		
		// ドキュメントのファイルが移動可能かを判定する。
		return document.canMoveTargetFile();
	}
	
	/**
	 * このエディタビューのフォント変更要求を処理する。
	 * このメソッドは、{@link IComponentManager} から呼び出される。
	 * @param manager	このメソッドの呼び出し元となるマネージャ
	 * @param font	新しいエディタフォント
	 */
	public void onChangedEditorFont(IComponentManager manager, Font font) {
		// TODO: 将来、実装する
	}
	
	/**
	 * このビューに関連付けられたドキュメントの状態から、
	 * このエディタの編集状態を更新する。
	 */
	public void refreshEditingStatus() {
		endCompoundUndoableEdit();
		getUndoManager().discardAllEdits();
		updateEditorModifiedProperty();
		updateEditorSelectionChangedProperty();
		updateEditorMenus();
	}

	/**
	 * このビューで検索／置換が可能かを判定する。このインタフェースは、
	 * フレームワークの検索／置換処理から呼び出される。
	 * @return このビューで検索／置換が可能な場合は <tt>true</tt> を返す。
	 */
	public boolean canFindReplace() {
		return true;
	}
	
	/**
	 * 検索／置換処理のイベントハンドラを返す。
	 * このインタフェースが返すハンドラは、フレームワークの検索／置換ダイアログに
	 * 適用されるインタフェースであり、検索／置換ダイアログ、もしくは前方／後方
	 * 検索メニューにより実行される。
	 * @return	このビューの検索／置換処理イベントハンドラを返す。
	 * 			このビューが検索／置換に対応していない場合は <tt>null</tt> を返す。
	 */
	public AbFindReplaceHandler getFindReplaceHandler() {
		return _hFind;
	}

	//------------------------------------------------------------
	// implements IEditorMenuActionHandler interfaces
	//------------------------------------------------------------
	
	public boolean onProcessMenuSelection(String command, Object source, Action action) {
		if (Strings.isNullOrEmpty(command)) {
			return false;	// undefined command
		}
//		else if (RunnerMenuResources.ID_EDIT_UNDO.equals(command)) {
//			onMenuSelectedEditUndo();
//			return true;
//		}
//		else if (RunnerMenuResources.ID_EDIT_REDO.equals(command)) {
//			onMenuSelectedEditRedo();
//			return true;
//		}
//		else if (RunnerMenuResources.ID_EDIT_CUT.equals(command)) {
//			onMenuSelectedEditCut();
//			return true;
//		}
		else if (RunnerMenuResources.ID_EDIT_COPY.equals(command)) {
			onMenuSelectedEditCopy();
			return true;
		}
		else if (RunnerMenuResources.ID_EDIT_PASTE.equals(command)) {
			onMenuSelectedEditPaste();
			return true;
		}
		else if (RunnerMenuResources.ID_EDIT_DELETE.equals(command)) {
			onMenuSelectedEditDelete();
			return true;
		}
		else if (RunnerMenuResources.ID_EDIT_SELECTALL.equals(command)) {
			onMenuSelectedEditSelectAll();
			return true;
		}
		else if (RunnerMenuResources.ID_EDIT_JUMP.equals(command)) {
			onMenuSelectedEditJump();
			return true;
		}
		
		// not processed
		return false;
	}

	public boolean onProcessMenuUpdate(String command, Object source, Action action) {
		if (Strings.isNullOrEmpty(command) || action == null) {
			return false;	// undefined command
		}
//		else if (RunnerMenuResources.ID_EDIT_UNDO.equals(command)) {
//			action.setEnabled(canUndo());
//			return true;
//		}
//		else if (RunnerMenuResources.ID_EDIT_REDO.equals(command)) {
//			action.setEnabled(canRedo());
//			return true;
//		}
//		else if (RunnerMenuResources.ID_EDIT_CUT.equals(command)) {
//			action.setEnabled(canCut());
//			return true;
//		}
		else if (RunnerMenuResources.ID_EDIT_COPY.equals(command)) {
			action.setEnabled(canCopy());
			return true;
		}
		else if (RunnerMenuResources.ID_EDIT_PASTE.equals(command)) {
			action.setEnabled(canPaste());
			return true;
		}
		else if (RunnerMenuResources.ID_EDIT_DELETE.equals(command)) {
			action.setEnabled(canCut());
			return true;
		}
		else if (RunnerMenuResources.ID_EDIT_SELECTALL.equals(command)) {
			action.setEnabled(true);
			return true;
		}
		else if (RunnerMenuResources.ID_EDIT_JUMP.equals(command)) {
			action.setEnabled(true);
			return true;
		}
//		else if (RunnerMenuResources.ID_FILE_SAVE.equals(command)) {
//			action.setEnabled(!isReadOnly() && isModified());
//			return true;
//		}
		else if (RunnerMenuResources.ID_FILE_SAVEAS.equals(command)) {
			action.setEnabled(true);
			return true;
		}
		
		// not processed
		return false;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void restoreCellInfoEditValue(final CellIndex index) {
		// 保存されている値を取得
		Object oldValue = getCellInfoTargetCachedValue();
		
		// エディタの値を戻す
		JTextComponent component = getCellTextEditorComponent();
		if (oldValue == null) {
			component.setText("");
		} else {
			component.setText(oldValue.toString());
		}
	}
	
	protected void storeCellInfoEditValue(final CellIndex index) {
		// エディタの値を取得
		JTextComponent component = getCellTextEditorComponent();
		Object newValue = component.getText();
		
		// 新しい値を保存
		setCellInfoTargetCachedValue(newValue);
		getTableComponent().setValueAt(newValue, index.row, index.column);
	}
	
	protected void updateCellLocationLabel(CellIndex index) {
		if (index != null && index.isValid()) {
			getCellLocationLabel().setText(String.format("%d, %d", index.row+1, index.column+1));
		} else {
			getCellLocationLabel().setText("");
		}
	}
	
	protected void updateCellLocationLabel(int row, int column) {
		if (row < 0 || column < 0) {
			getCellLocationLabel().setText("");
		} else {
			getCellLocationLabel().setText(String.format("%d, %d", row+1, column+1));
		}
	}
	
	protected void updateCellInfoPane() {
		SpreadSheetTable table = getTableComponent();
		
		// 選択位置が更新されている場合のみ、表示内容を更新する
		CellIndex oldIndex = getCellInfoTargetLocation();
		CellIndex newIndex = (table.isSelectionOneCell() ? table.getSelectedCell() : null);
		if (!Objects.isEqual(oldIndex, newIndex)) {
			// 新しい選択位置の値をキャッシュ
			boolean isEditable = (!isReadOnly() && table.isEditable());
			JTextComponent component = getCellTextEditorComponent();
			JButton btnCancel = getCellEditCancelButton();
			JButton btnCommit = getCellEditCommitButton();
			if (newIndex == null) {
				// 選択なし
				setCellInfoTargetLocation(newIndex);
				component.setText("");
				component.setEditable(false);
				//btnCancel.setVisible(false);
				//btnCommit.setVisible(false);
				btnCancel.setEnabled(false);
				btnCommit.setEnabled(false);
			}
			else {
				// 単一選択
				if (isEditable) {
					isEditable = table.isCellEditable(newIndex.row, newIndex.column);
				}
				Object newValue = table.getValueAt(newIndex.row, newIndex.column);
				setCellInfoTargetCachedValue(newValue);
				setCellInfoTargetLocation(newIndex);
				restoreCellInfoEditValue(newIndex);
				//--- update view
				if (isEditable) {
					component.setEditable(true);
					btnCancel.setEnabled(true);
					btnCommit.setEnabled(true);
					//btnCancel.setVisible(true);
					//btnCommit.setVisible(true);
				} else {
					component.setEditable(false);
					//btnCancel.setVisible(false);
					//btnCommit.setVisible(false);
					btnCancel.setEnabled(false);
					btnCommit.setEnabled(false);
				}
			}
			updateCellLocationLabel(newIndex);
		}
	}

	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------

	/**
	 * ドキュメント(テーブルモデル)が変更されたときに呼び出される
	 * イベントハンドラ。
	 * 
	 * @param e	テーブルモデルイベント
	 */
	protected void onTableModelChanged(TableModelEvent e) {
		setEditorModifiedProperty(true);
		
		// セル情報パネルの内容を更新する
		SpreadSheetTable table = getTableComponent();
		if (table.isSelectionOneCell() && e.getType()==TableModelEvent.UPDATE) {
			CellIndex index = table.getSelectedCell();
			if ((e.getFirstRow() <= index.row && index.row <= e.getLastRow())
				&& (e.getColumn()==index.column || e.getColumn()==TableModelEvent.ALL_COLUMNS))
			{
				// 単一選択セルの内容が更新された
				Object newValue = table.getValueAt(index.row, index.column);
				setCellInfoTargetCachedValue(newValue);
				restoreCellInfoEditValue(index);
			}
		}
	}

	/**
	 * テーブルの選択状態が変更されたときに呼び出されるイベントハンドラ。
	 * @param e	リスト選択イベント
	 */
	protected void onTableSelectionChanged(ListSelectionEvent e) {
		RunnerFrame frame = getFrame();
		if (frame != null) {
//			frame.getMenuAction(RunnerMenuResources.ID_EDIT_CUT).setEnabled(canCut());
			frame.getMenuAction(RunnerMenuResources.ID_EDIT_COPY).setEnabled(canCopy());
			frame.getMenuAction(RunnerMenuResources.ID_EDIT_DELETE).setEnabled(canCut());
			//--- update location indicator in status bar
			if (!e.getValueIsAdjusting()) {
				getTableComponent().updateLocationForStatusBar(frame.getStatusBar());
			}
		}
		setEditorSelectionChangedProperty(hasSelectedCells());
		
		// セル情報パネルの内容を更新する
		updateCellInfoPane();
	}
	
	protected void onCellInfoEditCanceled() {
		// 読み込み専用なら、何もしない
		if (isReadOnly())
			return;
		
		// 単一のセルが選択されていれば、現在の内容をキャンセルする
		CellIndex index = getCellInfoTargetLocation();
		if (index != null) {
			restoreCellInfoEditValue(index);
		}
		
		// フォーカスを標準のコンポーネントに設定
		requestFocusInComponent();
	}
	
	protected void onCellInfoEditCommitted() {
		// 読み込み専用なら、何もしない
		if (isReadOnly())
			return;
		
		// 単一のセルが選択されていれば、現在の内容をコミットする
		CellIndex index = getCellInfoTargetLocation();
		if (index != null) {
			storeCellInfoEditValue(index);
		}
		
		// フォーカスを標準のコンポーネントに設定
		requestFocusInComponent();
	}
	
	protected void onTableUpperLeftCornerMouseClicked(MouseEvent me) {
		// 左ボタンクリックで、全選択・選択解除を行う
		if (SwingUtilities.isLeftMouseButton(me)) {
			SpreadSheetTable table = getTableComponent();
			
			// 編集状態をキャンセル
			if (table.isEditing()) {
				CellEditor editor = table.getCellEditor();
				if (editor != null) {
					//--- セル編集停止
					editor.stopCellEditing();
				}
			}
			
			// フォーカスをテーブルに設定
			if (!table.hasFocus() && table.isRequestFocusEnabled()) {
				table.requestFocus();
			}
			
			// キー入力状態で選択状態を変更
			boolean ctrl = me.isControlDown();
			boolean shft = me.isShiftDown();
			if (shft) {
				// [Shift]キーが押されている場合は、常に全選択
				table.selectAll();
			}
			else if (ctrl) {
				// [Ctrl]キーのみ押されている場合は、全選択のOn/Off
				if (table.isSelectionAllRows() && table.isSelectionAllColumns()) {
					// 全てセルが選択されている場合は、選択解除
					table.clearSelection();
				} else {
					// 全てのセルが選択されていない場合は、全て選択
					table.selectAll();
				}
			}
			else {
				// [Shift] および [Ctrl]キーが押されていない場合は、常に全選択
				table.selectAll();
			}
		}
	}
	
	protected void onTableUpperLeftCornerMouseEntered(MouseEvent me) {
		// No implement.
	}
	
	protected void onTableUpperLeftCornerMouseExited(MouseEvent me) {
		// No implement.
	}
	
	protected void onTableUpperLeftCornerMousePressed(MouseEvent me) {
		// No implement.
	}
	
	protected void onTableUpperLeftCornerMouseReleased(MouseEvent me) {
		// No implement.
	}
	
	/**
	 * テーブル列ヘッダーのコンテキストメニューを表示する。
	 * このメソッドは、ポップアップメニュー表示のマウスイベントから呼び出される。
	 * このメソッド内では、マウスイベントがコンテキストメニュー表示のトリガであるかを
	 * <code>{@link java.awt.event.MouseEvent#isPopupTrigger()}</code> により検証すること。
	 * 
	 * @param me	マウスイベント
	 */
	protected void evaluateColumnHeaderPopupMenu(MouseEvent me) {
		// コンテキストメニュー表示のトリガ検証
		if (!me.isPopupTrigger())
			return;
		
		// ポップアップメニューが存在しない場合は、何もしない
		JPopupMenu pmenu = getTableHeaderPopupMenu();
		if (pmenu == null) {
			return;
		}

		// 編集状態の解除
		final SpreadSheetTable table = getTableComponent();
		if (table.isEditing()) {
			table.removeEditor();
		}
		
		// 選択状態の検証
		//--- [Shift]キーが押されている状態なら、選択状態は変更しない
		if ((me.getModifiers() & MouseEvent.SHIFT_DOWN_MASK)==0) {
			int col = table.columnAtPoint(me.getPoint());
			if (!table.isColumnSelectedAllRows(col)) {
				//--- コンテキストメニュー表示トリガとなるマウスイベント発生位置が
				//--- 選択済み列ではない場合、すべての選択を解除してイベント発生
				//--- 位置の列を選択する。
				if (col >= 0) {
					table.changeColumnHeaderSelection(col, false, false);
				} else {
					table.clearSelection();
				}
			}
		}

		// コンテキストメニューの表示
		pmenu.show(me.getComponent(), me.getX(), me.getY());
		me.getComponent().requestFocusInWindow();
	}
	
	protected void onTableHeaderPopupMenuSelected_AdjustColumnWidth(ActionEvent ae) {
		int targetColumnIndex = getTableComponent().getSelectedColumn();
		if (targetColumnIndex >= 0) {
			JTableHeader header = getTableComponent().getTableHeader();
			if (header instanceof SpreadSheetColumnHeader) {
				((SpreadSheetColumnHeader)header).adjustSelectedColumnWidth(targetColumnIndex);
			}
		}
	}
	
	protected void onTableHeaderPopupMenuSelected_SetColumnWidthToDefault(ActionEvent ae) {
		int targetColumnIndex = getTableComponent().getSelectedColumn();
		if (targetColumnIndex >= 0) {
			JTableHeader header = getTableComponent().getTableHeader();
			if (header instanceof SpreadSheetColumnHeader) {
				((SpreadSheetColumnHeader)header).setSelectedColumnWidthToDefault(targetColumnIndex);
			}
		}
	}

	// Menu : [Edit]-[Undo]
	protected void onMenuSelectedEditUndo() {
		AppLogger.debug("catch [Edit]-[Undo] menu selection.");
		undo();
		requestFocusInComponent();
	}
	
	// Menu : [Edit]-[Redo]
	protected void onMenuSelectedEditRedo() {
		AppLogger.debug("catch [Edit]-[Redo] menu selection.");
		redo();
		requestFocusInComponent();
	}
	
	// Menu : [Edit]-[Cut]
	protected void onMenuSelectedEditCut() {
		AppLogger.debug("catch [Edit]-[Cut] menu selection.");
		cut();
		requestFocusInComponent();
	}
	
	// Menu : [Edit]-[Copy]
	protected void onMenuSelectedEditCopy() {
		AppLogger.debug("catch [Edit]-[Copy] menu selection.");
		copy();
		requestFocusInComponent();
	}
	
	// Menu : [Edit]-[Paste]
	protected void onMenuSelectedEditPaste() {
		AppLogger.debug("catch [Edit]-[Paste] menu selection.");
		paste();
		requestFocusInComponent();
	}
	
	// Menu : [Edit]-[Delete]
	protected void onMenuSelectedEditDelete() {
		AppLogger.debug("catch [Edit]-[Delete] menu selection.");
		delete();
		requestFocusInComponent();
	}
	
	// Menu : [Edit]-[Select all]
	protected void onMenuSelectedEditSelectAll() {
		AppLogger.debug("catch [Edit]-[SelectAll] menu selection.");
		selectAll();
		requestFocusInComponent();
	}
	
	// Menu : [Edit]-[Jump to Line]
	protected void onMenuSelectedEditJump() {
		AppLogger.debug("catch [Edit]-[Jump] menu selection.");
		// Jump dialog
		int maxLines = getLineCount();
		JumpDialog dlg = new JumpDialog(getFrame(), maxLines);
		dlg.setVisible(true);
		int dlgResult = dlg.getDialogResult();
		if (AppLogger.isDebugEnabled()) {
			AppLogger.debug("Dialog result : " + dlgResult);
		}
		if (dlgResult == IDialogResult.DialogResult_OK) {
			int toLineNo = dlg.getSelectedLineNo();
			jumpToLine(toLineNo);
		}
		requestFocusInComponent();
	}
	
	protected void updateEditorModifiedProperty() {
		if (_undoMan.canUndo()) {
			setEditorModifiedProperty(true);
		} else if (_tableDocument != null) {
			setEditorModifiedProperty(_tableDocument.isNewDocument());
		} else {
			setEditorModifiedProperty(false);
		}
	}
	
	protected void updateEditorSelectionChangedProperty() {
		setEditorSelectionChangedProperty(hasSelectedCells());
	}
	
	protected void setEditorModifiedProperty(boolean modified) {
		Boolean oldValue = (Boolean)getClientProperty(ITextComponent.PROP_MODIFIED);
		boolean oldModified = (oldValue != null ? oldValue.booleanValue() : false);
		if (_tableDocument != null) {
			_tableDocument.setModifiedFlag(modified);
		}
		if (modified != oldModified) {
			putClientProperty(ITextComponent.PROP_MODIFIED, modified);
		}
	}
	
	protected void setEditorSelectionChangedProperty(boolean selected) {
		Boolean oldValue = (Boolean)getClientProperty(ITextComponent.PROP_SELECTED);
		boolean oldSelected = (oldValue != null ? oldValue.booleanValue() : false);
		if (selected != oldSelected) {
			putClientProperty(ITextComponent.PROP_SELECTED, selected);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 編集状態の更新によって関連するメニュー更新が必要な場合に呼び出されるメソッド。
	 * 
	 * @param frame	親フレームのインスタンス(<tt>null</tt> 以外)
	 */
	protected void fireUpdateMenusByEditState(RunnerFrame frame) {
//		frame.updateMenuItem(RunnerMenuResources.ID_FILE_SAVE);
//		frame.updateMenuItem(RunnerMenuResources.ID_EDIT_UNDO);
//		frame.updateMenuItem(RunnerMenuResources.ID_EDIT_REDO);
//		frame.updateMenuItem(RunnerMenuResources.ID_EDIT_CUT);
		frame.updateMenuItem(RunnerMenuResources.ID_EDIT_COPY);
		frame.updateMenuItem(RunnerMenuResources.ID_EDIT_PASTE);
		frame.updateMenuItem(RunnerMenuResources.ID_EDIT_DELETE);
		frame.updateMenuItem(RunnerMenuResources.ID_EDIT_SELECTALL);
		frame.updateMenuItem(RunnerMenuResources.ID_EDIT_JUMP);
	}

	/**
	 * カット／コピーの実行によって関連するメニュー更新が必要な場合に呼び出されるメソッド。
	 * 
	 * @param frame	親フレームのインスタンス(<tt>null</tt> 以外)
	 */
	protected void fireUpdateMenusByCutCopy(RunnerFrame frame) {
		frame.updateMenuItem(RunnerMenuResources.ID_EDIT_PASTE);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	/**
	 * セル情報ペインのテキスト編集操作専用のアクション。
	 * このクラス・インスタンスは、セル情報ペインのキャンセルボタン、
	 * コミットボタンのアクションとして使用される。また、テキスト
	 * エディタの[Enter]キーおよび[Escape]キーのアクションとしても
	 * 利用される。
	 */
	protected class CellInfoEditActionHandler extends AbMenuItemAction
	{
		/** コミット操作用コマンド名 **/
		static public final String COMMAND_COMMIT = "cellinfo.editor.commit";
		/** キャンセル操作用コマンド名 **/
		static public final String COMMAND_CANCEL = "cellinfo.editor.cancel";
		
		public CellInfoEditActionHandler(String command, String name) {
			super(name);
			Validations.validArgument(!Strings.isNullOrEmpty(command), "'command' argument is null or empty.");
			setCommandKey(command);
		}

		public void actionPerformed(ActionEvent e) {
			String key = getCommandKey();
			
			if (COMMAND_COMMIT.equals(key)) {
				onCellInfoEditCommitted();
			}
			else if (COMMAND_CANCEL.equals(key)) {
				onCellInfoEditCanceled();
			}
		}
	}

	/**
	 * ドキュメント(テーブルモデル)の変更イベントハンドラ
	 */
	protected class TableDocumentHandler implements TableModelListener {
		public void tableChanged(TableModelEvent e) {
			onTableModelChanged(e);
		}
	};

	/**
	 * テーブルの選択状態変更イベントハンドラ
	 */
	protected class TableSelectionHandler implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			onTableSelectionChanged(e);
		}
	};

	/**
	 * ドキュメント(テーブルモデル)のUndo可能編集発生時のイベントハンドラ
	 */
	protected class TableUndoHandler implements UndoableEditListener {
		public void undoableEditHappened(UndoableEditEvent e) {
			if (_compoundEdits) {
				// CompoundEdit へ登録
				if (_packEdits == null) {
					_packEdits = new CompoundEdit();
				}
				_packEdits.addEdit(e.getEdit());
			} else {
				// UndoManager へ登録
				_undoMan.addEdit(e.getEdit());
//				RunnerFrame frame = getFrame();
//				if (frame != null) {
//					frame.updateMenuItem(RunnerMenuResources.ID_EDIT_UNDO);
//					frame.updateMenuItem(RunnerMenuResources.ID_EDIT_REDO);
//				}
			}
		}
	}
	
	/**
	 * このビュー専用の検索／置換ハンドラ
	 */
	protected class TableFindReplaceHandler extends AbFindReplaceHandler
	{
		public TableFindReplaceHandler() {
			super();
		}

		/**
		 * 検索操作を許可するかどうかを判定する。
		 * @return	検索操作を許可する場合は <tt>true</tt>、そうでない場合は <tt>false</tt> を返す。
		 */
		public boolean allowFindOperation() {
			return canFindReplace();
		}

		/**
		 * 置換操作を許可するかどうかを判定する。
		 * @return	置換操作を許可する場合は <tt>true</tt>、そうでない場合は <tt>false</tt> を返す。
		 */
		public boolean allowReplaceOperation() {
			return canEdit();
		}

		// 次を検索
		public boolean findNext() {
			return searchText(true);
		}

		// 前を検索
		public boolean findPrev() {
			return searchText(false);
		}

		// 置換して次へ
		public boolean replaceNext() {
			CellIndex minSelectedCell = getTableComponent().getMinSelectionCellIndex();
			CellIndex maxSelectedCell = getTableComponent().getMaxSelectionCellIndex();
			if (!minSelectedCell.isValid() || !maxSelectedCell.isValid()) {
				// 選択された領域がないので、現在のフォーカス位置から検索して次へ
				return searchText(true);
			}
			else if (!minSelectedCell.equals(maxSelectedCell)) {
				// 複数のセルが選択されている場合は、現在のフォーカス位置から検索して次へ
				return searchText(true);
			}
			
			// replace
			String strKeyword = getKeywordString();
			if (isIgnoreCase())
				strKeyword = strKeyword.toLowerCase();
			String srcword = findInCell(strKeyword, isIgnoreCase(), minSelectedCell);
			if (srcword != null) {
				startCompoundUndoableEdit();
				replaceInCell(srcword, getReplaceString(), minSelectedCell);
				endCompoundUndoableEdit();
				_replaceCount++;
			}
			
			// 次を検索
			return searchText(true);
		}

		// すべて置換
		public boolean replaceAll() {
			// 初期化
			_replaceCount = 0;
			final SpreadSheetTable table = getTableComponent();
			
			//--- データがなければ無効
			if (table.getRowCount() < 1 || table.getColumnCount() < 1) {
				// no cells
				return false;
			}
			
			// 検索キーワード取得
			String strKeyword = getKeywordString();
			if (Strings.isNullOrEmpty(strKeyword)) {
				return false;	// search keyword nothing!
			}
			if (isIgnoreCase()) {
				strKeyword = strKeyword.toLowerCase();
			}
			
			// 現在位置から終端まで置換
			CellIndex curIndex = table.getLeadSelectionCellIndex();
			// 下へ検索
			if (!curIndex.isValid()) {
				curIndex = new CellIndex(0,0);
			}
			CellIndex index = curIndex;
			startCompoundUndoableEdit();
			do {
				String srcword = findInCell(strKeyword, isIgnoreCase(), index);
				if (srcword != null) {
					replaceInCell(srcword, getReplaceString(), index);
					_replaceCount++;
					curIndex = index;
				}
				index = table.getNextCellIndex(index);
			} while (index != null);
			endCompoundUndoableEdit();
			
			// 最終置換位置に設定
			if (_replaceCount > 0) {
				table.clearSelection();
				table.setFocusToCell(curIndex);
				return true;
			} else {
				// no replaces
				return false;
			}
		}
		
		private boolean searchText(boolean dirToEnd) {
			// check
			final SpreadSheetTable table = getTableComponent();
			//--- データがなければ無効
			if (table.getRowCount() < 1 || table.getColumnCount() < 1) {
				// no cells
				return false;
			}
			//--- キーワードがなければ無効
			String strKeyword = getKeywordString();
			if (Strings.isNullOrEmpty(strKeyword)) {
				// not found
				return false;
			}
			
			// search
			//--- 大文字小文字を区別しない
			if (isIgnoreCase()) {
				strKeyword = strKeyword.toLowerCase();
			}
			//--- 現在の位置を取得する
			CellIndex curIndex = table.getLeadSelectionCellIndex();
			CellIndex found = null;
			if (dirToEnd) {
				// 下へ検索
				if (!curIndex.isValid()) {
					curIndex = new CellIndex(0,0);
				} else if (table.isCellSelected(curIndex)) {
					curIndex = table.getNextCellIndex(curIndex);
					if (curIndex == null)
						curIndex = table.getLowerBoundCellIndex();
				}
				//--- 現在位置から検索
				CellIndex index = curIndex;
				do {
					if (findInCell(strKeyword, isIgnoreCase(), index)!=null) {
						// found
						found = index;
						break;
					}
					index = table.getNextCellIndex(index);
				} while (index != null);
				//--- 先頭から現在位置まで検索
				if (found == null) {
					index = table.getLowerBoundCellIndex();
					while (!index.equals(curIndex)) {
						if (findInCell(strKeyword, isIgnoreCase(), index)!=null) {
							// found
							found = index;
							break;
						}
						index = table.getNextCellIndex(index);
					}
				}
			}
			else {
				// 上へ検索
				if (!curIndex.isValid()) {
					curIndex = new CellIndex(table.getRowCount()-1, table.getColumnCount()-1);
				} else if (table.isCellSelected(curIndex)) {
					curIndex = table.getPreviousCellIndex(curIndex);
					if (curIndex == null)
						curIndex = table.getUpperBoundCellIndex();
				}
				//--- 現在位置から検索
				CellIndex index = curIndex;
				do {
					if (findInCell(strKeyword, isIgnoreCase(), index)!=null) {
						// found
						found = index;
						break;
					}
					index = table.getPreviousCellIndex(index);
				} while (index != null);
				//--- 終端から現在位置まで検索
				if (found == null) {
					index = table.getUpperBoundCellIndex();
					while (!index.equals(curIndex)) {
						if (findInCell(strKeyword, isIgnoreCase(), index)!=null) {
							// found
							found = index;
							break;
						}
						index = table.getPreviousCellIndex(index);
					}
				}
			}
			
			// result
			if (found != null) {
				// 発見
				table.setCellSelected(found);
				table.scrollToVisibleCell(found);
				return true;
			}
			
			// not found
			return false;
		}
		
		private String findInCell(String keyword, boolean isIgnoreCase, final CellIndex index) {
			Object obj = getTableComponent().getValueAt(index.row, index.column);
			String orgval = (obj==null ? null : obj.toString());
			if (!Strings.isNullOrEmpty(orgval)) {
				String val = orgval;
				if (isIgnoreCase)
					val = val.toLowerCase();
				int found = val.indexOf(keyword);
				if (found >= 0) {
					// found
					return orgval.substring(found, found+keyword.length());
				}
			}
			
			// not found
			return null;
		}
		
		private void replaceInCell(String srcword, String replace, final CellIndex index) {
			String srcValue = getTableComponent().getValueAt(index.row, index.column).toString();
			String strReplace = (replace==null ? "" : replace);
			StringBuilder sb = new StringBuilder();
			do {
				int found = srcValue.indexOf(srcword);
				if (found < 0) {
					sb.append(srcValue);
					break;
				}
				
				sb.append(srcValue.substring(0, found));
				//--- bugfix : 2009.02.12
				//--- replace が null のとき、"null" という文字が代入されるため、修正
				sb.append(strReplace);
				srcValue = srcValue.substring(found+srcword.length());
			} while(srcValue.length() > 0);

			// update
			getTableComponent().setValueAt(sb.toString(), index.row, index.column);
		}
	}
}
