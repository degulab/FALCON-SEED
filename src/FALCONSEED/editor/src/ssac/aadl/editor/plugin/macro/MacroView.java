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
 * @(#)MacroView.java	1.17	2011/02/04
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MacroView.java	1.16	2010/09/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MacroView.java	1.14	2009/12/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MacroView.java	1.10	2009/02/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MacroView.java	1.10	2009/01/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.plugin.macro;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;

import ssac.aadl.editor.view.EditorFrame;
import ssac.aadl.editor.view.EditorMenuBar;
import ssac.aadl.editor.view.dialog.FileChooserManager;
import ssac.aadl.editor.view.menu.EditorMenuResources;
import ssac.aadl.editor.view.table.AbSpreadSheetView;
import ssac.aadl.macro.command.MacroAction;
import ssac.aadl.macro.file.CsvMacroFiles;
import ssac.aadl.module.ModuleFileManager;
import ssac.util.Strings;
import ssac.util.io.CsvReader;
import ssac.util.logging.AppLogger;
import ssac.util.swing.Application;
import ssac.util.swing.SwingTools;
import ssac.util.swing.menu.MenuItemResource;
import ssac.util.swing.table.SpreadSheetTable;
import ssac.util.swing.table.SpreadSheetModel.RowDataModel;

/**
 * AADLマクロ専用ビュー。
 * 
 * @version 1.17	2011/02/04
 *
 * @since 1.10
 */
public class MacroView extends AbSpreadSheetView<MacroModel>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static private MacroMenuBar menuBar = null;
	static private KeyStroke[] menuAccelerators = null;
	
	private final FilenameDropTargetListener hFileDropped = new FilenameDropTargetListener();

	/**
	 * 最後に選択されたファイル(相対パス入力用)
	 */
	private File lastSelectedFile = null;

	/**
	 * 現在選択されているセルの総数
	 */
	private int _numSelectedCells;
	/*
	 * 選択されているセルが行全体を含む場合は <tt>true</tt>
	 */
	//private boolean _isSelectedAllRows;
	/**
	 * 選択されているセルが列全体を含む場合は <tt>true</tt>
	 */
	private boolean _isSelectedAllColumns;
	/**
	 * 選択されているセルが単一であり、かつ編集可能なら <tt>true</tt>
	 */
	private boolean _isSelectedEditableOneCell;
	/**
	 * 選択されているセルを含む行に対し、一部でもコメント記号編集が可能な場合は <tt>true</tt>。
	 */
	private boolean _canModifyCommentMark;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * このビューの新しいインスタンスを生成する。
	 * @since 1.16
	 */
	public MacroView() {
		super();
	}

	/*
	public MacroView(MacroModel document) {
		super(document);
		
		// カラム幅を自動調整(初回のみ)
		paneTable.adjustAllColumnsPreferredWidth();
		
		// Mouse action
		paneTable.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e) {
				evaluateTablePopupMenu(e);
			}
			public void mouseReleased(MouseEvent e) {
				evaluateTablePopupMenu(e);
			}
		});
		paneTable.getTableRowHeader().addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e) {
				paneTable.setFocus();
				evaluateRowHeaderPopupMenu(e);
			}
			public void mouseReleased(MouseEvent e) {
				evaluateRowHeaderPopupMenu(e);
			}
		});
		
		// Drop target
		//--- テーブルの列・行ヘッダやスクロールバーでは、ドロップを受け付けないようにするための処置
		new DropTarget(this, DnDConstants.ACTION_COPY, new EmptyDropTargetListener(), true);
		//--- テーブル標準のトランスファーを無効にする(こうしないと、デフォルトで何かを受け付けてしまう)
		paneTable.setTransferHandler(null);
		//--- テーブルに新たに設定するドロップターゲット
		new DropTarget(paneTable, DnDConstants.ACTION_COPY, hFileDropped, true);
		
		/*@@@ added 2009.02.02 - メニューアクセラレータキーのテーブルへの登録 @@@*
		KeyStroke[] strokes = getMenuAccelerators();
		paneTable.registMenuAcceleratorKeyStroke(strokes);
		/*@@@ added 2009.02.02 @@@*
		
		// ステータス更新
		updateSelectionStatus(false);
	}
	*/

	//------------------------------------------------------------
	// Initialization
	//------------------------------------------------------------

	/**
	 * 初期ドキュメントがセットされる前のコンポーネントの初期化
	 * @since 1.16
	 */
	@Override
	protected void setupComponentsBeforeSetDocument() {
		super.setupComponentsBeforeSetDocument();
	}

	/**
	 * 初期ドキュメントがセットされた後のコンポーネントの初期化
	 * @since 1.16
	 */
	@Override
	protected void setupComponentsAfterSetDocument() {
		super.setupComponentsAfterSetDocument();
		final SpreadSheetTable table = getTableComponent();
		
		// カラム幅を自動調整(初回のみ)
		table.adjustAllColumnsPreferredWidth();
		
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
		new DropTarget(this, DnDConstants.ACTION_COPY, new EmptyDropTargetListener(), true);
		//--- テーブル標準のトランスファーを無効にする(こうしないと、デフォルトで何かを受け付けてしまう)
		table.setTransferHandler(null);
		//--- テーブルに新たに設定するドロップターゲット
		new DropTarget(table, DnDConstants.ACTION_COPY, hFileDropped, true);
		
		// メニューアクセラレータキーのテーブルへの登録
		KeyStroke[] strokes = getMenuAccelerators();
		table.registMenuAcceleratorKeyStroke(strokes);
		
		// ステータス更新
		updateSelectionStatus(false);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	@Override
	public boolean onProcessMenuSelection(String command, Object source, Action action) {
		if (MacroMenuBar.ID_TABLE_ROW_INSERT_ABOVE.equals(command)) {
			onSelectedMenuTableInsertRowsAbove(action);
			return true;
		}
		else if (MacroMenuBar.ID_TABLE_ROW_INSERT_BELOW.equals(command)) {
			onSelectedMenuTableInsertRowsBelow(action);
			return true;
		}
		else if (MacroMenuBar.ID_TABLE_ROW_INSERT_COPIED.equals(command)) {
			onSelectedMenuTableInsertCopiedCells(action);
			return true;
		}
		else if (MacroMenuBar.ID_TABLE_ROW_CUT.equals(command)) {
			onSelectedMenuTableCutRows(action);
			return true;
		}
		else if (MacroMenuBar.ID_TABLE_ROW_DELETE.equals(command)) {
			onSelectedMenuTableDeleteRows(action);
			return true;
		}
		else if (MacroMenuBar.ID_TABLE_ROW_SELECT.equals(command)) {
			onSelectedMenuTableSelectRows(action);
			return true;
		}
		else if (MacroMenuBar.ID_DATA_COMMENT_ADD.equals(command)) {
			onSelectedMenuDataCommentAdd(action);
			return true;
		}
		else if (MacroMenuBar.ID_DATA_COMMENT_REMOVE.equals(command)) {
			onSelectedMenuDataCommentRemove(action);
			return true;
		}
		else if (MacroMenuBar.ID_DATA_INPUT_REL_PATH.equals(command)) {
			onSelectedMenuDataInputRelativePath(action);
			return true;
		}
		
		// 標準のアクション
		return super.onProcessMenuSelection(command, source, action);
	}

	@Override
	public boolean onProcessMenuUpdate(String command, Object source, Action action) {
		if (command.startsWith(MacroMenuBar.ID_TABLE_MENU)) {
			if (MacroMenuBar.ID_TABLE_ROW_INSERT_ABOVE.equals(command)) {
				action.setEnabled(_numSelectedCells>0 && _isSelectedAllColumns);
			}
			else if (MacroMenuBar.ID_TABLE_ROW_INSERT_BELOW.equals(command)) {
				action.setEnabled(_numSelectedCells>0 && _isSelectedAllColumns);
			}
			else if (MacroMenuBar.ID_TABLE_ROW_INSERT_COPIED.equals(command)) {
				action.setEnabled(_numSelectedCells>0 && _isSelectedAllColumns && canPaste());
			}
			else if (MacroMenuBar.ID_TABLE_ROW_CUT.equals(command)) {
				action.setEnabled(_numSelectedCells>0 && _isSelectedAllColumns);
			}
			else if (MacroMenuBar.ID_TABLE_ROW_DELETE.equals(command)) {
				action.setEnabled(_numSelectedCells>0 && _isSelectedAllColumns);
			}
			else if (MacroMenuBar.ID_TABLE_ROW_SELECT.equals(command)) {
				action.setEnabled(_numSelectedCells>0);
			}
			return true;
		}
		else if (command.startsWith(MacroMenuBar.ID_DATA_MENU)) {
			if (MacroMenuBar.ID_DATA_COMMENT_ADD.equals(command)) {
				action.setEnabled(_canModifyCommentMark);
			}
			else if (MacroMenuBar.ID_DATA_COMMENT_REMOVE.equals(command)) {
				action.setEnabled(_canModifyCommentMark);
			}
			else if (MacroMenuBar.ID_DATA_INPUT_REL_PATH.equals(command)) {
				action.setEnabled(_isSelectedEditableOneCell);
			}
			return true;
		}
		else if (EditorMenuResources.ID_BUILD_RUN.equals(command)) {
			action.setEnabled(true);
			return true;
		}
		else if (EditorMenuResources.ID_BUILD_OPTION.equals(command)) {
			action.setEnabled(true);
			return true;
		}
		
		// 標準の更新処理
		return super.onProcessMenuUpdate(command, source, action);
	}

	@Override
	public EditorMenuBar getDocumentMenuBar() {
		if (menuBar == null) {
			menuBar = new MacroMenuBar();
		}
		return menuBar;
	}
	
	private KeyStroke[] getMenuAccelerators() {
		if (menuAccelerators == null) {
			EditorMenuBar bar = getDocumentMenuBar();
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
	 * @since 1.17
	 */
	public boolean cachedDocumentFromSourceFile() {
		return true;
	}
	
	/**
	 * ドキュメントの内容をソースファイルから再読込する。
	 * 再読込時の設定は、ドキュメント読込時点の設定と同じ内容とする。
	 * @throws IOException	入出力エラーが発生した場合
	 * @since 1.17
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
		MacroModel oldModel = getDocument();
		if (oldModel == null || oldModel.isNewDocument() || oldModel.getTargetFile() == null) {
			return;		// cannot reopen;
		}
		
		MacroComponentManager manager = (MacroComponentManager)getManager();
		File docFile = oldModel.getTargetFile();
		
		// 新しいドキュメントを生成
		if (Strings.isNullOrEmpty(newEncoding)) {
			newEncoding = manager.getFileEncoding();
		}
		MacroModel newModel = new MacroModel(manager, docFile, newEncoding);
		
		// 新しいドキュメントを適用
		manager.removeDocument(oldModel);
		manager.putDocumentView(newModel, this);
		setDocument(newModel);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 選択された行のコメント記号編集が可能かを判定する。
	 * @return	コメント記号編集が可能であれば <tt>true</tt>
	 */
	protected boolean canModifyCommentMark() {
		return _canModifyCommentMark;
	}

	@Override
	protected MacroTable createSpreadSheetTable() {
		//return super.createDefaultTablePane();
		// マクロ専用テーブルを標準とする。
		return new MacroTable();
	}

	@Override
	protected void onTableSelectionChanged(ListSelectionEvent e) {
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
		boolean selected = (_numSelectedCells > 0);
		
		EditorFrame frame = getFrame();
		if (frame != null) {
			//--- [Edit] menu
			frame.getMenuAction(EditorMenuResources.ID_EDIT_CUT).setEnabled(selected);
			frame.getMenuAction(EditorMenuResources.ID_EDIT_COPY).setEnabled(selected);
			frame.getMenuAction(EditorMenuResources.ID_EDIT_DELETE).setEnabled(selected);
			//--- [Table] menu
			frame.getMenuAction(MacroMenuBar.ID_TABLE_ROW_INSERT_ABOVE).setEnabled(selected && _isSelectedAllColumns);
			frame.getMenuAction(MacroMenuBar.ID_TABLE_ROW_INSERT_BELOW).setEnabled(selected && _isSelectedAllColumns);
			frame.getMenuAction(MacroMenuBar.ID_TABLE_ROW_INSERT_COPIED).setEnabled(selected && _isSelectedAllColumns && canPaste());
			frame.getMenuAction(MacroMenuBar.ID_TABLE_ROW_CUT).setEnabled(selected && _isSelectedAllColumns);
			frame.getMenuAction(MacroMenuBar.ID_TABLE_ROW_DELETE).setEnabled(selected && _isSelectedAllColumns);
			frame.getMenuAction(MacroMenuBar.ID_TABLE_ROW_SELECT).setEnabled(selected);
			//--- [Data] menu
			frame.getMenuAction(MacroMenuBar.ID_DATA_COMMENT_ADD).setEnabled(_canModifyCommentMark);
			frame.getMenuAction(MacroMenuBar.ID_DATA_COMMENT_REMOVE).setEnabled(_canModifyCommentMark);
			frame.getMenuAction(MacroMenuBar.ID_DATA_INPUT_REL_PATH).setEnabled(_isSelectedEditableOneCell);
			//--- update location indicator in status bar
			if (!e.getValueIsAdjusting()) {
				getTableComponent().updateLocationForStatusBar(frame.getStatusBar());
			}
		}
		
		setEditorSelectionChangedProperty(selected);

		// セル情報パネルの内容を更新する
		updateCellInfoPane();
	}

	/**
	 * セルの選択状態に関わるステータスを初期化する。
	 */
	protected void initSelectionStatus() {
		_numSelectedCells = 0;
		//_isSelectedAllRows = false;
		_isSelectedAllColumns = false;
		_isSelectedEditableOneCell = false;
		_canModifyCommentMark = false;
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
				_canModifyCommentMark = false;
				if (table.isEditable()) {
					//--- Selected one cell is editable
					if (_numSelectedCells == 1 && table.isCellEditable(selRows[0], selCols[0])) {
						_isSelectedEditableOneCell = true;
					}
					//--- can modify comment mark
					if (selRows.length > 1) {
						// 複数行が選択されているときは、コメント記号編集を許可する
						_canModifyCommentMark = true;
					}
					else if (selRows.length == 1 && !table.isRowSelected(0)) {
						// 単一行選択のときは、先頭行以外が選択されている場合に、コメント記号編集を許可する
						//--- 将来的に、編集を許可しないセルを任意に設定する、もしくはテンプレートのような
						//--- 機能を実装する場合、その状態をどのように判定するかは検討すること！
						_canModifyCommentMark = true;
					}
				}
			}
		} else {
			//_isSelectedAllRows = false;
			_isSelectedAllColumns = false;
			_isSelectedEditableOneCell = false;
			_canModifyCommentMark = false;
		}
	}

	/**
	 * マクロエディタのコンテキストメニューを表示する。
	 * このメソッドは、ポップアップメニュー表示のマウスイベントから呼び出される。
	 * このメソッド内では、マウスイベントがコンテキストメニュー表示のトリガであるかを
	 * <code>{@link java.awt.event.MouseEvent#isPopupTrigger()}</code> により検証すること。
	 * 
	 * @param me	マウスイベント
	 */
	protected void evaluateTablePopupMenu(MouseEvent me) {
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
		EditorFrame frame = getFrame();
		JPopupMenu pmenu = (frame==null ? null : frame.getActiveEditorConextMenu());
		if (pmenu != null) {
			pmenu.show(me.getComponent(), me.getX(), me.getY());
			me.getComponent().requestFocusInWindow();
		}
	}
	
	/**
	 * マクロエディタ行ヘッダーのコンテキストメニューを表示する。
	 * このメソッドは、ポップアップメニュー表示のマウスイベントから呼び出される。
	 * このメソッド内では、マウスイベントがコンテキストメニュー表示のトリガであるかを
	 * <code>{@link java.awt.event.MouseEvent#isPopupTrigger()}</code> により検証すること。
	 * 
	 * @param me	マウスイベント
	 */
	protected void evaluateRowHeaderPopupMenu(MouseEvent me) {
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

	@Override
	protected void fireUpdateMenusByCutCopy(EditorFrame frame) {
		// 基本メニューの更新
		super.fireUpdateMenusByCutCopy(frame);
		
		// マクロ専用メニューの更新
		frame.updateMenuItem(MacroMenuBar.ID_TABLE_ROW_INSERT_COPIED);
	}

	@Override
	protected void fireUpdateMenusByEditState(EditorFrame frame) {
		// 基本メニューの更新
		super.fireUpdateMenusByEditState(frame);
		
		// マクロ専用メニューの更新
		frame.updateMenuItem(MacroMenuBar.ID_TABLE_ROW_INSERT_ABOVE);
		frame.updateMenuItem(MacroMenuBar.ID_TABLE_ROW_INSERT_BELOW);
		frame.updateMenuItem(MacroMenuBar.ID_TABLE_ROW_INSERT_COPIED);
		frame.updateMenuItem(MacroMenuBar.ID_TABLE_ROW_CUT);
		frame.updateMenuItem(MacroMenuBar.ID_TABLE_ROW_DELETE);
		frame.updateMenuItem(MacroMenuBar.ID_TABLE_ROW_SELECT);
		frame.updateMenuItem(MacroMenuBar.ID_DATA_COMMENT_ADD);
		frame.updateMenuItem(MacroMenuBar.ID_DATA_COMMENT_REMOVE);
		frame.updateMenuItem(MacroMenuBar.ID_DATA_INPUT_REL_PATH);
	}

	// menu : [Table]-[Insert rows above]
	protected void onSelectedMenuTableInsertRowsAbove(Action action) {
		AppLogger.debug("menu [Table]-[Insert rows above] selected.");
		final SpreadSheetTable table = getTableComponent();
		if (!table.isEditable()) {
			requestFocusInComponent();
			return;
		}
		if (table.isEditing()) {
			table.removeEditor();
		}
		int [] selRows = table.getSelectedRows();
		int numSelCols = table.getSelectedColumnCount();
		if (selRows.length > 0 && numSelCols==table.getColumnCount()) {
			Arrays.sort(selRows);
			//--- 第２行目以降への挿入のみを許可
			if (selRows[0] != 0) {
				//--- 行数を調整(全体行数が Integer.MAX_VALUE を超えないように)
				int numNewRows = Math.min(selRows.length, (Integer.MAX_VALUE - table.getRowCount()));
				if (numNewRows > 0) {
					//--- 選択行数分の空行を挿入
					int startRowIndex = selRows[0];
					int endRowIndex = startRowIndex;
					getDocument().startCompoundUndoableEdit();
					for (int i = 0; i < numNewRows; i++) {
						endRowIndex = startRowIndex + i;
						getDocument().insertRow(endRowIndex, null);
					}
					getDocument().endCompoundUndoableEdit();
					
					// 挿入した行のみに選択を設定
					table.changeRowHeaderSelection(endRowIndex, false, false);
					table.changeRowHeaderSelection(startRowIndex, false, true);
				}
				else {
					//--- 最大行数をオーバー
					Application.showErrorMessage(this, MacroMessages.getInstance().msgCannotInsertFurtherRows);
				}
			}
			else {
				//--- 第１行目への挿入は禁止
				Application.showErrorMessage(this, MacroMessages.getInstance().msgCannotInsertToFirstRow);
			}
		}
		requestFocusInComponent();
	}

	// menu : [Table]-[Insert rows below]
	protected void onSelectedMenuTableInsertRowsBelow(Action action) {
		AppLogger.debug("menu [Table]-[Insert rows below] selected.");
		final SpreadSheetTable table = getTableComponent();
		if (!table.isEditable()) {
			requestFocusInComponent();
			return;
		}
		if (table.isEditing()) {
			table.removeEditor();
		}
		int [] selRows = table.getSelectedRows();
		int numSelCols = table.getSelectedColumnCount();
		if (selRows.length > 0 && numSelCols==table.getColumnCount()) {
			Arrays.sort(selRows);
			//--- 行数を調整(全体行数が Integer.MAX_VALUE を超えないように)
			int numNewRows = Math.min(selRows.length, (Integer.MAX_VALUE - table.getRowCount()));
			if (numNewRows > 0) {
				int startRowIndex = selRows[selRows.length-1] + 1;
				int endRowIndex = startRowIndex;
				if (startRowIndex < table.getRowCount()) {
					// 最終選択行の次の行に挿入
					getDocument().startCompoundUndoableEdit();
					for (int i = 0; i < numNewRows; i++) {
						endRowIndex = startRowIndex + i;
						getDocument().insertRow(endRowIndex, null);
					}
					getDocument().endCompoundUndoableEdit();
				}
				else {
					// 行終端に追加
					startRowIndex = table.getRowCount();
					getDocument().startCompoundUndoableEdit();
					for (int i = 0; i < numNewRows; i++) {
						getDocument().addRow(null);
					}
					getDocument().endCompoundUndoableEdit();
					endRowIndex = table.getRowCount()-1;
				}
				
				// 挿入した行のみに選択を設定
				table.changeRowHeaderSelection(endRowIndex, false, false);
				table.changeRowHeaderSelection(startRowIndex, false, true);
			}
			else {
				//--- 最大行数をオーバー
				Application.showErrorMessage(this, MacroMessages.getInstance().msgCannotInsertFurtherRows);
			}
		}
		requestFocusInComponent();
	}
	
	// menu : [Table]-[Insert copied cells]
	protected void onSelectedMenuTableInsertCopiedCells(Action action) {
		AppLogger.debug("menu [Table]-[Insert copied cells] selected.");
		final SpreadSheetTable table = getTableComponent();
		if (!table.isEditable()) {
			requestFocusInComponent();
			return;
		}
		if (table.isEditing()) {
			table.removeEditor();
		}
		int [] selRows = table.getSelectedRows();
		int numSelCols = table.getSelectedColumnCount();
		if (selRows.length > 0 && numSelCols==table.getColumnCount()) {
			Arrays.sort(selRows);
			//--- 第２行目以降への挿入のみを許可
			if (selRows[0] != 0) {
				//--- 選択領域の先頭行へ挿入
				pasteNewRowFromClipboard(selRows[0]);
			}
			else {
				//--- 第１行目への挿入は禁止
				Application.showErrorMessage(this, MacroMessages.getInstance().msgCannotInsertToFirstRow);
			}
		}
		requestFocusInComponent();
	}
	
	protected void pasteNewRowFromClipboard(int rowIndex) {
		// 転送可能なオブジェクトを取得
		Clipboard clip = getToolkit().getSystemClipboard();
		Transferable trans = clip.getContents(this);
		//--- 文字列以外は貼り付け禁止
		if (trans == null || !trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			return;
		}
		
		// 転送データを取得
		final SpreadSheetTable table = getTableComponent();
		ArrayList<RowDataModel> copiedRows = new ArrayList<RowDataModel>();
		try {
			String data = (String)trans.getTransferData(DataFlavor.stringFlavor);
			if (!Strings.isNullOrEmpty(data)) {
				//--- 許容行数を取得
				int remainRowCount = Integer.MAX_VALUE - table.getRowCount();
				if (remainRowCount > 0) {
					//--- クリップボードの内容を取得
					CsvReader reader = new CsvReader(new BufferedReader(new StringReader(data)));
					try {
						reader.setDelimiterChar('\t');
						CsvReader.CsvRecord record;
						while ((record = reader.readRecord()) != null) {
							RowDataModel rowModel = null;
							int numFields = record.getNumFields();
							if (numFields > 0) {
								int limit = Math.min(numFields, table.getColumnCount());
								String[] newRow = new String[limit];
								for (int i = 0; i < limit; i++) {
									CsvReader.CsvField field = record.getField(i);
									if (field != null) {
										newRow[i] = field.getValue();
									}
								}
								//--- 行モデル生成
								rowModel = getDocument().createRowDataModel(newRow);
								if (rowModel.isEmpty()) {
									rowModel = null;
								}
							}
							//--- 行を追加
							copiedRows.add(rowModel);
							if (copiedRows.size() >= remainRowCount) {
								//--- 許容行数到達
								break;
							}
						}
					}
					finally {
						reader.close();
					}
				}
				else {
					//--- 最大行数をオーバー
					Application.showErrorMessage(this, MacroMessages.getInstance().msgCannotInsertFurtherRows);
				}
			}
		}
		catch (IOException ex) {
			AppLogger.error("Cannot to get Transferable data from System clipboard.", ex);
		}
		catch (UnsupportedFlavorException ex) {
			AppLogger.error("Unsupported String flavor in System clipboard data.", ex);
		}
		//--- 転送データが存在しなければ、処理しない
		if (copiedRows.isEmpty()) {
			return;
		}
		
		// 転送データを新しい行として挿入
		getDocument().startCompoundUndoableEdit();
		for (int i = 0; i < copiedRows.size(); i++) {
			getDocument().insertRow((rowIndex + i), copiedRows.get(i));
		}
		getDocument().endCompoundUndoableEdit();
		
		// 挿入した行のみに選択を設定
		table.changeRowHeaderSelection((rowIndex + copiedRows.size() - 1), false, false);
		table.changeRowHeaderSelection(rowIndex, false, true);
	}

	// menu : [Table]-[Cut rows]
	protected void onSelectedMenuTableCutRows(Action action) {
		AppLogger.debug("menu [Table]-[Cut rows] selected.");
		final SpreadSheetTable table = getTableComponent();
		if (!table.isEditable()) {
			requestFocusInComponent();
			return;
		}
		if (table.isEditing()) {
			table.removeEditor();
		}
		int [] selRows = table.getSelectedRows();
		int numSelCols = table.getSelectedColumnCount();
		if (selRows.length > 0 && numSelCols==table.getColumnCount()) {
			Arrays.sort(selRows);
			//--- 第２行目以降の削除のみを許可
			if (selRows[0] != 0) {
				//--- 選択セルのコピー
				table.copy();
				//--- 編集操作の集約開始
				getDocument().startCompoundUndoableEdit();
				//--- 選択行を削除(終端に近い行から削除)
				for (int i = selRows.length-1; i >= 0; i--) {
					getDocument().removeRow(selRows[i]);
				}
				//--- 標準行数を下回る場合は、行数を調整
				getDocument().adjustRowCount();
				//--- 編集操作の集約完了
				getDocument().endCompoundUndoableEdit();
				//--- 選択解除
				table.clearSelection();
			}
			else {
				//--- 第１行目の削除は禁止
				Application.showErrorMessage(this, MacroMessages.getInstance().msgCannotDeleteFirstRow);
			}
		}
		requestFocusInComponent();
	}

	// menu : [Table]-[Delete rows]
	protected void onSelectedMenuTableDeleteRows(Action action) {
		AppLogger.debug("menu [Table]-[Delete rows] selected.");
		final SpreadSheetTable table = getTableComponent();
		if (!table.isEditable()) {
			requestFocusInComponent();
			return;
		}
		if (table.isEditing()) {
			table.removeEditor();
		}
		int [] selRows = table.getSelectedRows();
		int numSelCols = table.getSelectedColumnCount();
		if (selRows.length > 0 && numSelCols==table.getColumnCount()) {
			Arrays.sort(selRows);
			//--- 第２行目以降の削除のみを許可
			if (selRows[0] != 0) {
				getDocument().startCompoundUndoableEdit();
				//--- 選択行を削除(終端に近い行から削除)
				for (int i = selRows.length-1; i >= 0; i--) {
					getDocument().removeRow(selRows[i]);
				}
				//--- 標準行数を下回る場合は、行数を調整
				getDocument().adjustRowCount();
				getDocument().endCompoundUndoableEdit();
				//--- 選択解除
				table.clearSelection();
			}
			else {
				//--- 第１行目の削除は禁止
				Application.showErrorMessage(this, MacroMessages.getInstance().msgCannotDeleteFirstRow);
			}
			
		}
		requestFocusInComponent();
	}
	
	// menu : [Table]-[Select rows]
	protected void onSelectedMenuTableSelectRows(Action action) {
		AppLogger.debug("menu [Table]-[Select rows] selected.");
		final SpreadSheetTable table = getTableComponent();
		if (!table.isEditable()) {
			requestFocusInComponent();
			return;
		}
		if (table.isEditing()) {
			table.removeEditor();
		}
		int numSelRows = table.getSelectedRowCount();
		int numSelCols = table.getSelectedColumnCount();
		if (numSelRows > 0 && numSelCols > 0 && numSelCols!=table.getColumnCount()) {
			table.selectAllColumns();
		}
		requestFocusInComponent();
	}

	// menu : [Data]-[Add comment mark]
	protected void onSelectedMenuDataCommentAdd(Action action) {
		AppLogger.debug("menu [Data]-[Add comment mark] selected.");
		final SpreadSheetTable table = getTableComponent();
		if (!table.isEditable()) {
			requestFocusInComponent();
			return;
		}
		if (table.isEditing()) {
			table.removeEditor();
		}
		int[] selRows = table.getSelectedRows();
		if (selRows.length > 0) {
			MacroModel model = (MacroModel)table.getModel();
			String cm = MacroAction.COMMENT.commandString();
			startCompoundUndoableEdit();
			for (int row : selRows) {
				if (model.isCellEditable(row, CsvMacroFiles.FIELD_COMMAND)) {
					Object obj = model.getValueAt(row, CsvMacroFiles.FIELD_COMMAND);
					String value = (obj==null ? "" : obj.toString());
					if (!value.startsWith(cm)) {
						model.setValueAt(cm+value, row, CsvMacroFiles.FIELD_COMMAND);
					}
				}
			}
			endCompoundUndoableEdit();
		}
	}

	// menu : [Data]-[Remove comment mark]
	protected void onSelectedMenuDataCommentRemove(Action action) {
		AppLogger.debug("menu [Data]-[Remove comment mark] selected.");
		final SpreadSheetTable table = getTableComponent();
		if (!table.isEditable()) {
			requestFocusInComponent();
			return;
		}
		if (table.isEditing()) {
			table.removeEditor();
		}
		int[] selRows = table.getSelectedRows();
		if (selRows.length > 0) {
			MacroModel model = (MacroModel)table.getModel();
			String cm = MacroAction.COMMENT.commandString();
			startCompoundUndoableEdit();
			for (int row : selRows) {
				if (model.isCellEditable(row, CsvMacroFiles.FIELD_COMMAND)) {
					Object obj = model.getValueAt(row, CsvMacroFiles.FIELD_COMMAND);
					String value = (obj==null ? "" : obj.toString());
					if (value.startsWith(cm)) {
						int start = cm.length();
						for (; value.startsWith(cm, start); start+=cm.length());
						if (start < value.length())
							model.setValueAt(value.substring(start), row, CsvMacroFiles.FIELD_COMMAND);
						else
							model.setValueAt(null, row, CsvMacroFiles.FIELD_COMMAND);
					}
				}
			}
			endCompoundUndoableEdit();
		}
	}

	// menu : [Data]-[Input relative path]
	protected void onSelectedMenuDataInputRelativePath(Action action) {
		AppLogger.debug("menu [Data]-[Input relative path] selected.");
		final SpreadSheetTable table = getTableComponent();
		if (!table.isEditable()) {
			requestFocusInComponent();
			return;
		}
		if (table.isEditing()) {
			table.removeEditor();
		}
		int numSelRows = table.getSelectedRowCount();
		int numSelCols = table.getSelectedColumnCount();
		if (numSelRows != 1 || numSelCols != 1) {
			requestFocusInComponent();
			return;		// cannot process.
		}
		int selRow = table.getSelectedRow();
		int selCol = table.getSelectedColumn();
		if (!table.isCellEditable(selRow, selCol)) {
			requestFocusInComponent();
			return;		// cannot edit.
		}
		
		// データ取得
		String value = null;
		Object obj = table.getValueAt(selRow, selCol);
		if (obj != null) {
			value = obj.toString();
			if (Strings.isNullOrEmpty(value))
				value = null;
		}
		
		// 基準となるファイルパスを生成
		File initFile = lastSelectedFile;
		if (value != null) {
			initFile = getDocument().convertAbsolutePath(value);
			if (!initFile.getParentFile().exists()) {
				//--- 親ディレクトリが存在しないなら、最後に選択されたファイルとする。
				initFile = lastSelectedFile;
			}
		}
		//--- 基準パスが null の場合、初期位置をドキュメントとする
		if (initFile == null) {
			initFile = getDocumentFile();
		}
		
		// ファイルを開く
		File target = FileChooserManager.chooseAllFile(this, initFile, MacroMessages.getInstance().chooserTitleRelPath);
		if (target != null) {
			target = target.getAbsoluteFile();
			lastSelectedFile = target;
			
			// 相対パスをセルに入力
			String strPath = getDocument().convertRelativePath(target);
			table.setValueAt(strPath, selRow, selCol);
		}
		requestFocusInComponent();
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	class EmptyDropTargetListener extends DropTargetAdapter {
		@Override
		public void dragOver(DropTargetDragEvent dtde) {
			//--- 全て禁止
			dtde.rejectDrag();
		}

		public void drop(DropTargetDropEvent dtde) {
			//--- 全て禁止
			dtde.rejectDrop();
		}
	}

	/**
	 * ファイル名のドロップリスナー
	 */
	class FilenameDropTargetListener extends DropTargetAdapter {
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
			// サポートする DataFlavor のチェック
			if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				// Drop 位置のカラム判定
				final SpreadSheetTable table = getTableComponent();
				int row = table.rowAtPoint(dtde.getLocation());
				int col = table.columnAtPoint(dtde.getLocation());
				if ((row >= 0 && row < table.getRowCount())
					&& (col >= 0 && col < table.getColumnCount())
					&& table.isCellEditable(row, col))
				{
					dtde.acceptDrag(dtde.getDropAction());
					return;
				}
			}
			
			dtde.rejectDrag();
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

			// カラム位置の取得
			dtde.acceptDrop(DnDConstants.ACTION_COPY);
			final SpreadSheetTable table = getTableComponent();
			int row = table.rowAtPoint(dtde.getLocation());
			int col = table.columnAtPoint(dtde.getLocation());
			if ((row >= 0 && row < table.getRowCount())
				&& (col >= 0 && col < table.getColumnCount())
				&& table.isCellEditable(row, col))
			{
				try {
					// TransferData の取得
					File targetFile;
					Transferable t = dtde.getTransferable();
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
					
					// ドキュメントのパスを取得(相対パスの挿入)
					String strPath;
					getDocument().startCompoundUndoableEdit();	// 編集操作の集約開始
					loopFileList : for (Object elem : flist) {
						if (elem instanceof File) {
							targetFile = (File)elem;
							//--- ターゲットとセットの設定ファイルは無視する
							if (Strings.endsWithIgnoreCase(targetFile.getName(), ModuleFileManager.EXT_FILE_PREFS)) {
								strPath = targetFile.getPath();
								File relFile = new File(strPath.substring(0, strPath.length() - ModuleFileManager.EXT_FILE_PREFS.length()));
								if (fileset.contains(relFile)) {
									continue loopFileList;
								}
							}
							//--- 相対パスの挿入
							targetFile = targetFile.getAbsoluteFile();
							strPath = getDocument().convertRelativePath(targetFile);
							table.setValueAt(strPath, row, col);
							row++;
							//--- 行末かをチェック
							if (row >= table.getRowCount()) {
								//--- 行の終端まで配置したため、終了
								break loopFileList;
							}
						}
					}
					getDocument().endCompoundUndoableEdit();	// 編集操作の集約完了
					dtde.dropComplete(true);
					return;
				}
				catch (UnsupportedFlavorException ex) {
					AppLogger.error("Failed to drop to Macro editor cell.", ex);
				}
				catch (IOException ex) {
					AppLogger.error("Failed to drop to Macro editor cell.", ex);
				}
			}
			
			// drop を受け付けない
			dtde.rejectDrop();
		}
	}
}
