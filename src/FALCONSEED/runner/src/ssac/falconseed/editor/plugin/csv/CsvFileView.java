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
 * @(#)CsvFileView.java	1.13	2012/02/23
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvFileView.java	1.10	2011/02/14
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvFileView.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.editor.plugin.csv;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import ssac.falconseed.editor.view.dialog.AbFindReplaceHandler;
import ssac.falconseed.editor.view.table.AbSpreadSheetView;
import ssac.falconseed.runner.menu.RunnerMenuBar;
import ssac.falconseed.runner.menu.RunnerMenuResources;
import ssac.falconseed.runner.view.RunnerFrame;
import ssac.util.Strings;
import ssac.util.logging.AppLogger;
import ssac.util.nio.csv.CsvFileData;
import ssac.util.nio.csv.CsvParameters;
import ssac.util.nio.csv.CsvRecordCursor;
import ssac.util.swing.SwingTools;
import ssac.util.swing.menu.MenuItemResource;
import ssac.util.swing.table.SpreadSheetTable;
import ssac.util.swing.table.SpreadSheetTable.CellIndex;

/**
 * CSVファイルの閲覧専用ビュー
 * 
 * @version 1.13	2012/02/23
 */
public class CsvFileView extends AbSpreadSheetView<CsvFileModel>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	static private CsvFileMenuBar	menuBar = null;
	static private KeyStroke[] menuAccelerators = null;

	/** 現在選択されているセルの総数 */
	private int _numSelectedCells;
	/** 選択されているセルが列全体を含む場合は <tt>true</tt> */
	private boolean _isSelectedAllColumns;
	/** 選択されているセルが単一であり、かつ編集可能なら <tt>true</tt> */
	private boolean _isSelectedEditableOneCell;
	
	private boolean			_mouseDragging;
	private MouseInputListener	_scrollBarMouseListener;
	private AdjustmentListener	_scrollBarAdjustmentListener;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * このビューの新しいインスタンスを生成する。
	 */
	public CsvFileView() {
		super();
	}

	//------------------------------------------------------------
	// Initialization
	//------------------------------------------------------------
	
	protected AdjustmentListener getScrollBarAdjustmentListener() {
		if (_scrollBarAdjustmentListener == null) {
			_scrollBarAdjustmentListener = new AdjustmentListener() {
				public void adjustmentValueChanged(AdjustmentEvent e) {
					handleScrollBarAdjustmentEvent(e);
				}
			};
		}
		return _scrollBarAdjustmentListener;
	}
	
	protected MouseInputListener getScrollBarMouseListener() {
		if (_scrollBarMouseListener == null) {
			_scrollBarMouseListener = new MouseInputAdapter() {
				public void mouseClicked(MouseEvent e) {
					handleScrollBarMouseEvent(e);
				}
				public void mousePressed(MouseEvent e) {
					handleScrollBarMouseEvent(e);
				}
				public void mouseReleased(MouseEvent e) {
					handleScrollBarMouseEvent(e);
				}
				public void mouseEntered(MouseEvent e) {
					handleScrollBarMouseEvent(e);
				}
				public void mouseExited(MouseEvent e) {
					handleScrollBarMouseEvent(e);
				}
				public void mouseDragged(MouseEvent e) {
					handleScrollBarMouseEvent(e);
				}
				public void mouseMoved(MouseEvent e) {
					handleScrollBarMouseEvent(e);
				}
			};
		}
		return _scrollBarMouseListener;
	}
	
	/**
	 * このビューの標準スクロールペインのインスタンスを生成する。
	 * @return	<code>JScrollPane</code> のインスタンス
	 */
	@Override
	protected JScrollPane createScrollPane() {
		JScrollPane scroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		installListeners(scroll.getVerticalScrollBar());
		installListeners(scroll.getHorizontalScrollBar());
		return scroll;
	}
	
	protected void installListeners(JScrollBar scrollBar) {
		scrollBar.addMouseListener(getScrollBarMouseListener());
		scrollBar.addMouseMotionListener(getScrollBarMouseListener());
		scrollBar.addAdjustmentListener(getScrollBarAdjustmentListener());
	}

	@Override
	protected CsvFileFindReplaceHandler createTableFindReplaceHandler() {
		return new CsvFileFindReplaceHandler();
	}

	/**
	 * このビューの標準テーブルペインのインスタンスを生成する。
	 * @return <code>SpreadSheetTable</code> のインスタンス
	 */
	@Override
	protected SpreadSheetTable createSpreadSheetTable() {
		//SpreadSheetTable table = new SpreadSheetTable();
		SpreadSheetTable table = new CsvFileViewTable();
		//--- テーブルは編集不可
		table.setEditable(false);
		//--- 自動列幅調整は表示されている行に限定する
		table.setAdjustOnlyVisibleRows(true);
		/*
		// Table cell mouse listener
		MouseListener listener = new MouseListener() {
			final CsvFileView view = CsvFileView.this;

			public void mousePressed(MouseEvent e) {
				view.handleTableCellMouseEvent(e);
			}
			public void mouseReleased(MouseEvent e) {
				view.handleTableCellMouseEvent(e);
			}
			public void mouseClicked(MouseEvent e) {
				view.handleTableCellMouseEvent(e);
			}
			public void mouseEntered(MouseEvent e) {
				view.handleTableCellMouseEvent(e);
			}
			public void mouseExited(MouseEvent e) {
				view.handleTableCellMouseEvent(e);
			}
		};
		table.addMouseListener(listener);
		// Table column header mouse listener
		listener = new MouseListener() {
			final CsvFileView view = CsvFileView.this;

			public void mousePressed(MouseEvent e) {
				view.handleTableColumnHeaderMouseEvent(e);
			}
			public void mouseReleased(MouseEvent e) {
				view.handleTableColumnHeaderMouseEvent(e);
			}
			public void mouseClicked(MouseEvent e) {
				view.handleTableColumnHeaderMouseEvent(e);
			}
			public void mouseEntered(MouseEvent e) {
				view.handleTableColumnHeaderMouseEvent(e);
			}
			public void mouseExited(MouseEvent e) {
				view.handleTableColumnHeaderMouseEvent(e);
			}
		};
		table.getTableHeader().addMouseListener(listener);
		*/
		// finished
		return table;
	}

	/**
	 * 初期ドキュメントがセットされる前のコンポーネントの初期化
	 */
	@Override
	protected void setupComponentsBeforeSetDocument() {
		super.setupComponentsBeforeSetDocument();
	}

	/**
	 * 初期ドキュメントがセットされた後のコンポーネントの初期化
	 */
	@Override
	protected void setupComponentsAfterSetDocument() {
		super.setupComponentsAfterSetDocument();
		final SpreadSheetTable table = getTableComponent();
		
		// Mouse action
		table.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e) {
				evaluateTablePopupMenu(e);
			}
			public void mouseReleased(MouseEvent e) {
				evaluateTablePopupMenu(e);
			}
		});
		table.getTableRowHeader().addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e) {
				getTableComponent().setFocus();
				evaluateRowHeaderPopupMenu(e);
			}
			public void mouseReleased(MouseEvent e) {
				evaluateRowHeaderPopupMenu(e);
			}
		});
		
		// Drop target
		//--- テーブルの列・行ヘッダやスクロールバーでは、ドロップを受け付けないようにするための処置
		//new DropTarget(this, DnDConstants.ACTION_COPY, new EmptyDropTargetListener(), true);
		//--- テーブル標準のトランスファーを無効にする(こうしないと、デフォルトで何かを受け付けてしまう)
		//table.setTransferHandler(null);
		//--- テーブルに新たに設定するドロップターゲット
		//new DropTarget(table, DnDConstants.ACTION_COPY, hFileDropped, true);
		
		// メニューアクセラレータキーのテーブルへの登録
		KeyStroke[] strokes = getMenuAccelerators();
		table.registMenuAcceleratorKeyStroke(strokes);
		
		// ステータス更新
		updateSelectionStatus(false);
		updateTableBounds();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	@Override
	public boolean onProcessMenuSelection(String command, Object source, Action action) {
		
		// 標準のアクション
		return super.onProcessMenuSelection(command, source, action);
	}

	@Override
	public boolean onProcessMenuUpdate(String command, Object source, Action action) {
		if (Strings.isNullOrEmpty(command) || action == null) {
			return false;	// undefined command
		}
		else if (RunnerMenuResources.ID_EDIT_COPY.equals(command)) {
			action.setEnabled(canCopy());
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
//			// 保存は許可しない
//			action.setEnabled(false);
//			return true;
//		}
		else if (RunnerMenuResources.ID_FILE_SAVEAS.equals(command)) {
			// 保存は許可しない
			action.setEnabled(false);
			return true;
		}
		
		// 標準の更新処理
		return super.onProcessMenuUpdate(command, source, action);
	}

	@Override
	public RunnerMenuBar getDocumentMenuBar() {
		if (menuBar == null) {
			menuBar = new CsvFileMenuBar();
		}
		return menuBar;
	}
	
	private KeyStroke[] getMenuAccelerators() {
		if (menuAccelerators == null) {
			RunnerMenuBar bar = getDocumentMenuBar();
			if (bar != null) {
				//--- カット、コピー、ペースト、削除、全て選択のショートカットキーは、
				//--- SpreadSheetTable クラスで登録されているため、除外
				menuAccelerators = SwingTools.getMenuAccelerators(bar,
						MenuItemResource.getEditPasteShortcutKeyStroke(),
						MenuItemResource.getEditCopyShortcutKeyStroke(),
						MenuItemResource.getEditCutShortcutKeyStroke(),
						MenuItemResource.getEditDeleteShortcutKeyStroke(),
						MenuItemResource.getEditSelectAllShortcutKeyStroke());
			}
			else {
				menuAccelerators = new KeyStroke[0];
			}
		}
		return menuAccelerators;
	}
	
	/**
	 * ドキュメントのソースファイルの内容がすべてキャッシュされているかを判定する。
	 * キャッシュされている場合は、ソースファイルが変更されていても表示内容の
	 * 影響を受けない。
	 * @return	キャッシュされている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 1.10
	 */
	public boolean cachedDocumentFromSourceFile() {
		return false;	// 表示内容はソースファイルに依存
	}
	
	/**
	 * ドキュメントの内容をソースファイルから再読込する。
	 * 再読込時の設定は、ドキュメント読込時点の設定と同じ内容とする。
	 * @throws IOException	入出力エラーが発生した場合
	 * @since 1.10
	 */
	public void refreshDocumentFromSourceFile() throws IOException
	{
		reopenDocument(getLastEncodingName());
	}
	
	/**
	 * エンコーディングを指定してドキュメントを開きなおす操作を許可するかを判定する。
	 * @return 許可する場合は <tt>true</tt>、そうでない場合は <tt>false</tt> を返す。
	 */
	public boolean canReopen() {
		final CsvFileModel curModel = getDocument();
		if (curModel == null)
			return false;
		if (curModel.isNewDocument())
			return false;
		if (!curModel.hasTargetFile())
			return false;
		
		// allow reopen
		return true;
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
		// Check
		final CsvFileModel oldModel = getDocument();
		if (oldModel == null || oldModel.isNewDocument() || !oldModel.hasTargetFile()) {
			return;		// cannot reopen;
		}

		// 新しいドキュメントを生成
		final CsvFileComponentManager manager = (CsvFileComponentManager)getManager();
		final CsvFileData filedata = oldModel.getFileData();
		CsvParameters csvParams = new CsvParameters(filedata);
		if (Strings.isNullOrEmpty(newEncoding)) {
			newEncoding = manager.getFileEncoding();
		}
		CsvFileModel newModel = manager.createDocument(getFrame(), filedata.getFile(), newEncoding, csvParams);
		if (newModel == null) {
			return;		// user canceled
		}
		
		// 新しいドキュメントを適用
		manager.removeDocument(oldModel);
		manager.putDocumentView(newModel, this);
		setDocument(newModel);
		oldModel.releaseViewResources();

		// 開きなおした場合は、左上カラムを表示
		getTableComponent().scrollToVisibleCell(0, 0);
	}

	@Override
	public void setDocument(CsvFileModel newDocument) {
		super.setDocument(newDocument);
		updateTableBounds();
	}

	@Override
	public boolean canFindReplace() {
		// このモデルは巨大なため、現時点では検索や置換は禁止する。
		//return super.canFindReplace();
		//return false;
		//---
		// でも、やってみる
		//---
		return true;
	}

	@Override
	public boolean isReadOnly() {
		// このモデルは表示専用のため、常に true を返す。
		return true;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	@Override
	protected void onTableSelectionChanged(ListSelectionEvent e) {
		super.onTableSelectionChanged(e);
		
		//--- 高速化のため、基本クラスの処理をすべてここで再実装
		//super.onTableSelectionChanged(e);
		//--- Debug
		/*---
		{
			CellIndex ca = paneTable.getAnchorSelectionCellIndex();
			CellIndex cl = paneTable.getLeadSelectionCellIndex();
			CellIndex cmin = paneTable.getMinSelectionCellIndex();
			CellIndex cmax = paneTable.getMaxSelectionCellIndex();
			String str = String.format("AbSpreadSheetView#onTableSelectionChanged\n    ListSelectionEvent[%s]\n    min(%d,%d) max(%d,%d) anchor(%d,%d), lead(%d,%d)",
					e.toString(),
					cmin.row, cmin.column,
					cmax.row, cmax.column,
					ca.row, ca.column,
					cl.row, cl.column);
			System.err.println(str);
		}
		---*/
		//--- Debug

		updateSelectionStatus(e.getValueIsAdjusting());
		
		RunnerFrame frame = getFrame();
		if (frame != null) {
			//--- [Edit] menu
//			frame.updateMenuItem(RunnerMenuResources.ID_EDIT_CUT);
			frame.updateMenuItem(RunnerMenuResources.ID_EDIT_COPY);
			frame.updateMenuItem(RunnerMenuResources.ID_EDIT_DELETE);
			//--- update location indicator in status bar
			if (!e.getValueIsAdjusting()) {
				getTableComponent().updateLocationForStatusBar(frame.getStatusBar());
			}
		}
		
		setEditorSelectionChangedProperty(hasSelectedCells());
	}

	/**
	 * セルの選択状態に関わるステータスを初期化する。
	 */
	protected void initSelectionStatus() {
		_numSelectedCells = 0;
		//_isSelectedAllRows = false;
		_isSelectedAllColumns = false;
		_isSelectedEditableOneCell = false;
	}

	/**
	 * セルの選択状態に関わる内部ステータスを更新する。
	 */
	protected void updateSelectionStatus(boolean isAdjusting) {
		final SpreadSheetTable table = getTableComponent();
		int[] selRows = table.getSelectedRows();
		int[] selCols = table.getSelectedColumns();
		
		_numSelectedCells = selRows.length * selCols.length;
		if (_numSelectedCells > 0) {
			//_isSelectedAllRows = (selRows.length == paneTable.getRowCount());
			_isSelectedAllColumns = (selCols.length == table.getColumnCount());
			if (!isAdjusting) {
				_isSelectedEditableOneCell = false;
				if (table.isEditable()) {
					//--- Selected one cell is editable
					if (_numSelectedCells == 1 && table.isCellEditable(selRows[0], selCols[0])) {
						_isSelectedEditableOneCell = true;
					}
				}
			}
		} else {
			//_isSelectedAllRows = false;
			_isSelectedAllColumns = false;
			_isSelectedEditableOneCell = false;
		}
	}

	/**
	 * エディタのコンテキストメニューを表示する。
	 * このメソッドは、ポップアップメニュー表示のマウスイベントから呼び出される。
	 * このメソッド内では、マウスイベントがコンテキストメニュー表示のトリガであるかを
	 * <code>{@link java.awt.event.MouseEvent#isPopupTrigger()}</code> により検証すること。
	 * 
	 * @param me	マウスイベント
	 */
	protected void evaluateTablePopupMenu(MouseEvent me) {
		// コンテキストメニューは表示しない
		if (false) {
		// コンテキストメニュー表示のトリガ検証
		if (!me.isPopupTrigger())
			return;

		final SpreadSheetTable table = getTableComponent();
		if (table.isEditing()) {
			table.removeEditor();
		}
		
		// 選択状態の検証
		int col = table.columnAtPoint(me.getPoint());
		int row = table.rowAtPoint(me.getPoint());
		if (!table.isCellSelected(row, col)) {
			//--- コンテキストメニュー表示トリガとなるマウスイベント発生位置が
			//--- 選択済みセルではない場合、すべての選択を解除してイベント発生
			//--- 位置のセルを選択する。
			if (col >= 0 && row >= 0) {
				table.changeSelection(row, col, false, false);
			} else {
				table.clearSelection();
			}
		}

		// コンテキストメニューの表示
		RunnerFrame frame = getFrame();
		JPopupMenu pmenu = (frame==null ? null : frame.getActiveEditorConextMenu());
		if (pmenu != null) {
			pmenu.show(me.getComponent(), me.getX(), me.getY());
			me.getComponent().requestFocusInWindow();
		}
		}
	}
	
	/**
	 * エディタ行ヘッダーのコンテキストメニューを表示する。
	 * このメソッドは、ポップアップメニュー表示のマウスイベントから呼び出される。
	 * このメソッド内では、マウスイベントがコンテキストメニュー表示のトリガであるかを
	 * <code>{@link java.awt.event.MouseEvent#isPopupTrigger()}</code> により検証すること。
	 * 
	 * @param me	マウスイベント
	 */
	protected void evaluateRowHeaderPopupMenu(MouseEvent me) {
		// コンテキストメニューは表示しない
		/*
		if (false) {
		// コンテキストメニュー表示のトリガ検証
		if (!me.isPopupTrigger())
			return;

		final SpreadSheetTable table = getTableComponent();
		if (table.isEditing()) {
			table.removeEditor();
		}
		
		// 選択状態の検証
		int row = table.rowAtPoint(me.getPoint());
		if (!table.isRowSelectedAllColumns(row)) {
			//--- コンテキストメニュー表示トリガとなるマウスイベント発生位置が
			//--- 選択済み行ではない場合、すべての選択を解除してイベント発生
			//--- 位置の行を選択する。
			if (row >= 0) {
				table.changeRowHeaderSelection(row, false, false);
			} else {
				table.clearSelection();
			}
		}

		// コンテキストメニューの表示
		JPopupMenu pmenu = ((MacroMenuBar)getDocumentMenuBar()).getRowHeaderContextMenu();
		pmenu.show(me.getComponent(), me.getX(), me.getY());
		me.getComponent().requestFocusInWindow();
		}
		*/
	}

	@Override
	protected void fireUpdateMenusByCutCopy(RunnerFrame frame) {
		// 基本メニューの更新
		super.fireUpdateMenusByCutCopy(frame);
		
		// ビュー専用メニューの更新
	}

	@Override
	protected void fireUpdateMenusByEditState(RunnerFrame frame) {
		// 基本メニューの更新
		super.fireUpdateMenusByEditState(frame);
		
		// ビュー専用メニューの更新
	}
	
	protected void handleScrollBarAdjustmentEvent(AdjustmentEvent event) {
		if (!isScrollBarMouseDragging()) {
			updateTableBounds();
		}
	}
	
	protected void handleScrollBarMouseEvent(MouseEvent event) {
		int type = event.getID();
		switch (type) {
			case MouseEvent.MOUSE_DRAGGED:
				setScrollBarMouseDragging(true);
				break;
			case MouseEvent.MOUSE_RELEASED:
				setScrollBarMouseDragging(false);
				updateTableBounds();
				break;
			default:
				setScrollBarMouseDragging(false);
				break;
		}
	}
	
	protected void handleTableCellMouseEvent(MouseEvent event) {
		//if (evt.isPopupTrigger()) {
		//	SwingHelper1.handleCellClipboard(this.getTable(), evt);
		//}
	}
	
	protected void handleTableColumnHeaderMouseEvent(MouseEvent event) {
		// No implement.
	}

	/**
	 * マウスによるスクロールバーのドラッグ中なら <tt>true</tt> を返す。
	 */
	protected boolean isScrollBarMouseDragging() {
		return _mouseDragging;
	}

	/**
	 * スクロールバーのドラッグ中かどうかを示す状態を設定する。
	 * @param toDragging	ドラッグ中なら <tt>true</tt> を指定する。
	 */
	protected void setScrollBarMouseDragging(boolean toDragging) {
		this._mouseDragging = toDragging;
	}

	/**
	 * テーブルの表示領域情報を更新する。
	 */
	protected void updateTableBounds() {
		CsvFileModel model = getDocument();
		if (model == null) {
			return;
		}
		
		//*****************************************************************
		// このビューで使用するモデルは、行単位で読み込み済みデータを
		// キャッシュするため、ビューボックスに全カラムを含める
		//*****************************************************************
		SpreadSheetTable table = getTableComponent();
		Rectangle box = table.getVisibleRect();
		Point pt = box.getLocation();
		int rBegin = table.rowAtPoint(pt);
		int cBegin = 0; //table.columnAtPoint(pt);
		if (rBegin < 0 || cBegin < 0) {
			return;
		}
		if (box.isEmpty()) {
			pt.translate(this.getWidth(), this.getHeight());
		} else {
			pt.translate(box.width, box.height);
		}
		int rEnd = table.rowAtPoint(pt);
		int cEnd = table.getColumnCount(); //table.columnAtPoint(pt);
		if (rEnd < 0) {
			rEnd = table.getRowCount();
		} else {
			rEnd = Math.min(rEnd + 1, table.getRowCount());
		}
		//if (cEnd < 0) {
		//	cEnd = table.getColumnCount();
		//} else {
		//	cEnd = Math.min(cEnd + 1, table.getColumnCount());
		//}
		box = new Rectangle(cBegin, rBegin, cEnd - cBegin, rEnd - rBegin);
		model.setViewBox(box);
	}

	@Override
	protected void onTableModelChanged(TableModelEvent e) {
		// このモデルは表示専用のため、変更フラグをセットしない。
		//super.onTableModelChanged(e);
		
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

	@Override
	public boolean canCopy() {
		// 巨大なモデルのため、コピー操作は許可しない
		return false;
	}

	@Override
	public void copy() {
		// Copy 操作は実行しない
		//super.copy();
	}

	@Override
	public void cut() {
		// Cut 操作は実行しない
		//super.cut();
	}

	@Override
	public void delete() {
		// Delete 操作は実行しない
		//super.delete();
	}

	@Override
	public void paste() {
		// Paste 操作は実行しない
		//super.paste();
	}

	@Override
	public void redo() {
		// Redo 操作は実行しない
		//super.redo();
	}

	@Override
	public void undo() {
		// Undo 操作は実行しない
		//super.undo();
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	static protected class CsvFileViewTable extends SpreadSheetTable
	{
		public CsvFileViewTable() {
			super();
		}

		/**
		 * テーブルに関連付けられているモデルから、データ検索用カーソルを取得する。
		 * このカーソルは、データ表示用カーソルとは異なるキャッシュを持つ。
		 * @return	検索用カーソルを返す。検索用カーソルが取得できない場合は <tt>null</tt> を返す。
		 */
		public CsvRecordCursor getSearchCursor() {
			TableModel model = getModel();
			if (model instanceof CsvFileModel) {
				CsvRecordCursor cursor = ((CsvFileModel)model).getSearchCursor();
				if (cursor == null) {
					// 現在のカーソルを利用する
					//**********************************************************
					// この処理は、あくまでもテスト用である。
					// テーブルモデルのデータをキャッシュするカーソルは、
					// 検索用キャッシュと異なる方が効率が良い(はず)
					//**********************************************************
					cursor = ((CsvFileModel)model).getCursor();
				}
				return cursor;
			} else {
				return null;
			}
		}

		@Override
		protected boolean adjustLocalColumnWidth(int columnIndex, TableColumn columnHeader, boolean adjustOnlySelectedCells)
		{
			TableModel model = getModel();
			if (model instanceof CsvFileModel) {
				// 表示領域の取得には、キャッシュ済みのビューボックスを利用し、テーブル全体のデータは利用しない
				int beginRowIndex, endRowIndex;
				// 表示領域のデータのみでカラム幅を調整
				final CsvFileModel viewboxModel = (CsvFileModel)model;
				Rectangle viewbox = viewboxModel.getViewBox();
				if (viewbox != null && !viewbox.isEmpty()) {
					beginRowIndex = viewbox.y;
					endRowIndex   = viewbox.y + viewbox.height;
				} else {
					// 表示領域が存在しない場合は、範囲なし
					beginRowIndex = 0;
					endRowIndex = -1;
				}
				return adjustLocalColumnWidth(beginRowIndex, endRowIndex, columnIndex, columnHeader, adjustOnlySelectedCells);
			}
			else {
				// キャッシュ済みのビューボックスを持たないモデルの場合は、標準の処理
				return super.adjustLocalColumnWidth(columnIndex, columnHeader, adjustOnlySelectedCells);
			}
		}
	}
	
	/**
	 * このビュー専用の検索／置換ハンドラ
	 */
	protected class CsvFileFindReplaceHandler extends AbFindReplaceHandler
	{
		public CsvFileFindReplaceHandler() {
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
			// 編集を許可していないため、処理しない
			return false;
		}

		// すべて置換
		public boolean replaceAll() {
			// 編集を許可していないため、処理しない
			return false;
		}
		
		private boolean searchText(boolean dirToEnd) {
			// check
			final CsvFileViewTable table = (CsvFileViewTable)getTableComponent();
			//--- カーソルが取得できなければ無効
			final CsvRecordCursor cursor = table.getSearchCursor();
			if (cursor == null) {
				// no cursor
				return false;
			}
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
					if (findInCell(strKeyword, isIgnoreCase(), cursor, index)!=null) {
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
						if (findInCell(strKeyword, isIgnoreCase(), cursor, index)!=null) {
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
					if (findInCell(strKeyword, isIgnoreCase(), cursor, index)!=null) {
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
						if (findInCell(strKeyword, isIgnoreCase(), cursor, index)!=null) {
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
		
		private String findInCell(String keyword, boolean isIgnoreCase, final CsvRecordCursor cursor, final CellIndex index) {
			// 値の取得
			Object obj = null;
			try {
				obj = cursor.getRecord(index.row)[index.column];
			}
			catch (IOException ex) {
				AppLogger.warn(String.format("Failed to read CSV Table at %s by CsvRecordCursor in CsvFileFindReplaceHandler.", index.toString()), ex);
			}
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
	}
}
