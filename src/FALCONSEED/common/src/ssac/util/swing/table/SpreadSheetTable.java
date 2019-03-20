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
 * @(#)SpreadSheetTable.java	3.3.1	2016/06/02 (Java's bug fixed)
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SpreadSheetTable.java	3.2.0	2015/06/22
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SpreadSheetTable.java	1.17	2010/11/24
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SpreadSheetTable.java	1.16	2010/09/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SpreadSheetTable.java	1.14	2009/12/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SpreadSheetTable.java	1.10	2009/02/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SpreadSheetTable.java	1.10	2009/01/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.NumberFormat;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import ssac.util.Strings;
import ssac.util.Validations;
import ssac.util.io.CsvReader;
import ssac.util.logging.AppLogger;
import ssac.util.swing.Application;
import ssac.util.swing.StatusBar;
import ssac.util.swing.SwingTools;
import ssac.util.swing.menu.MenuItemResource;

/**
 * スプレッドシートのテーブル・コンポーネント。
 * 
 * @version 3.3.1
 * @since 1.10
 */
public class SpreadSheetTable extends JTable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = -5670332081831078838L;

	static protected final CellIndex LOWER_CELL_INDEX = new CellIndex(0,0);
	
	static private final String TableEditCutActionName    = "table.edit.cut";
	static private final String TableEditCopyActionName   = "table.edit.copy";
	static private final String TableEditPasteActionName  = "table.edit.paste";
	static private final String TableEditDeleteActionName = "table.edit.delete";
	
	static private final String MenuAccelerationActionName = "Dummy.menu.accelerator";
	
	static public final int ERROR_CANNOT_PASTE_MULTIPLE_AREA = -1;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static private final Action editCutAction    = new EditActionHandler(TableEditCutActionName, "cut");
	static private final Action editCopyAction   = new EditActionHandler(TableEditCopyActionName, "copy");
	static private final Action editPasteAction  = new EditActionHandler(TableEditPasteActionName, "paste");
	static private final Action editDeleteAction = new EditActionHandler(TableEditDeleteActionName, "delete");
	static private final Action dummyMenuAction  = new MenuAcceleratorActionHandler();
	
	static private final Map<Integer,String> errorMessageMap = new HashMap<Integer,String>();
	
	static {
		//--- 複数の選択範囲には貼り付けできません。
		errorMessageMap.put(ERROR_CANNOT_PASTE_MULTIPLE_AREA, "Cannot paste to multiple selections.");
	}

	/** このテーブルのフォーカスリスナー **/
	private final FocusRequester hFocusRequester = new FocusRequester();

	/** セルフォーカス時の前景色 **/
	protected Color focusForeground;
	/** セルフォーカス時の背景色 **/
	protected Color focusBackground;
	/** 行ヘッダーモデル **/
	private RowHeaderModel tableRowHeaderModel;
	/**テーブルの行ヘッダー **/
	protected SpreadSheetRowHeader	tableRowHeader;
	/** テーブルの編集許可フラグ **/
	private boolean flgEditable = true;
	/** プラットフォーム依存の行区切り文字列 **/
	protected String lineSeparator;
	/** テキストエディタによる編集開始時に、テキストを全選択することを示すフラグ **/
	private boolean flgAllTextSelectionAtTextEditorWhenEditingStart = true;

	/** 標準の動作として、表示されている行のみに限定して列幅の自動調整を行うことを示すフラグ **/
	private boolean _adjustOnlyVisibleRows = false;
	/** 標準の動作として、選択されているセルのみに限定して列幅の自動調整を行うことを示すフラグ **/
	private boolean _adjustOnlySelectedCells = true;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public SpreadSheetTable() {
		super();
		
		// 行区切り文字の取得
		lineSeparator = System.getProperty("line.separator");
		if (Strings.isNullOrEmpty(lineSeparator)) {
			lineSeparator = "\r\n";
		}
		
		// このクラスのデフォルト設定
		setupDefaults();

		// コンポーネントの初期化
		initialComponents();
	}

	/*---
	public SpreadSheetTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
		super(dm, cm, sm);
		setupDefaults();
		
		initialComponents();
	}

	public SpreadSheetTable(TableModel dm, TableColumnModel cm) {
		super(dm, cm);
		setupDefaults();
		
		initialComponents();
	}
	---*/

	public SpreadSheetTable(TableModel model) {
		super(model);
		setupDefaults();
		
		initialComponents();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * スクロールペインの左上に表示するコンポーネント。
	 * @return	生成されたコンポーネント
	 * @since 3.2.0
	 */
	static public JPanel createUpperLeftCornerComponent() {
		JPanel borderPanel = new JPanel();
		borderPanel.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		return borderPanel;
	}

	/**
	 * グリッドの表示を設定する。
	 * @since 3.2.0
	 */
	public void setupVisibleGrid() {
		setShowGrid(true);
		setGridColor(new Color(128,128,128));
	}

	/**
	 * エラーコードに対応する、このクラスに登録済みのエラーメッセージを
	 * 取得する。
	 * @param errorCode	エラーコード
	 * @return	対応するエラーメッセージを返す。コードに対応するメッセージが未登録の
	 * 			場合は <tt>null</tt> を返す。
	 */
	static public String getErrorMessage(int errorCode) {
		return errorMessageMap.get(errorCode);
	}

	/**
	 * エラーコードに対応するメッセージを、このクラスに登録する。指定したエラーコードに
	 * 対応するメッセージが登録済みの場合、指定されたメッセージで上書きする。
	 * @param errorCode	エラーコード
	 * @param message	メッセージ
	 * @throws NullPointerException	<code>message</code> が <tt>null</tt> の場合
	 */
	static public void setErrorMessage(int errorCode, String message) {
		errorMessageMap.put(errorCode, Validations.validNotNull(message));
	}

	/**
	 * このテーブルのカット・アクションを返す。
	 * @return カット・アクション
	 */
	static public final Action getCutAction() {
		return editCutAction;
	}

	/**
	 * このテーブルのコピー・アクションを返す。
	 * @return コピー・アクション
	 */
	static public final Action getCopyAction() {
		return editCopyAction;
	}

	/**
	 * このテーブルのペースト・アクションを返す。
	 * @return ペースト・アクション
	 */
	static public final Action getPasteAction() {
		return editPasteAction;
	}

	/**
	 * このテーブルの削除アクションを返す。
	 * @return 削除アクション
	 */
	static public final Action getDeleteAction() {
		return editDeleteAction;
	}

	/**
	 * このテーブルが編集可能であれば <tt>true</tt> を返す。
	 * @return このテーブルがモデルの実装に関わらず編集可能であれば <tt>true</tt> を返す。
	 */
	public boolean isEditable() {
		return flgEditable;
	}

	/**
	 * このテーブルの編集可能状態を設定する。
	 * 編集可能(<tt>true</tt>)を指定した場合、テーブルは編集可能状態となる。セルごとの編集可否は
	 * {@link TableModel#isCellEditable(int, int)} が返す値に順ずる。
	 * 編集不可(<tt>false</tt>)を指定した場合、モデルの状態に関わらず、このテーブルの全てのセルが
	 * 編集不可となる。
	 * 
	 * @param editable	編集可能とする場合は <tt>true</tt> を指定する。
	 */
	public void setEditable(boolean editable) {
		if (editable != flgEditable) {
			boolean oldFlag = flgEditable;
			flgEditable = editable;
		    //enableInputMethods(editable);
			firePropertyChange("editable", Boolean.valueOf(oldFlag), Boolean.valueOf(editable));
			//repaint();
		}
	}

	/**
	 * テーブルの標準フォントを返す。
	 * @return このテーブルの標準フォント
	 */
	static public Font getDefaultTableFont() {
		return UIManager.getFont("Table.font");
	}

	/**
	 * セルフォーカス時の前景色を取得する。
	 * @return	セルフォーカス時の前景色
	 */
	public Color getFocusForeground() {
		return this.focusForeground;
	}

	/**
	 * セルフォーカス時の背景色を取得する。
	 * @return	セルフォーカス時の背景色
	 */
	public Color getFocusBackground() {
		return this.focusBackground;
	}

	/**
	 * セルフォーカス時の前景色を設定する。
	 * @param focusColor	新たに設定するセルフォーカス時の前景色
	 */
	public void setFocusForeground(Color focusColor) {
		Color old = this.focusForeground;
		this.focusForeground = focusColor;
		firePropertyChange("focusForeground", old, focusColor);
		if (!focusColor.equals(old)) {
			repaint();
		}
	}

	/**
	 * セルフォーカス時の背景色を設定する。
	 * @param focusColor	新たに設定するセルフォーカス時の背景色
	 */
	public void setFocusBackground(Color focusColor) {
		Color old = this.focusBackground;
		this.focusBackground = focusColor;
		firePropertyChange("focusBackground", old, focusColor);
		if (!focusColor.equals(old)) {
			repaint();
		}
	}
	
	/**
	 * このスプレッドシート用行ヘッダモデルを返す。
	 * @return 行ヘッダモデル
	 */
	public RowHeaderModel getTableRowHeaderModel() {
		//private final RowHeaderModel tableRowHeaderModel = new RowHeaderModel();
		if (tableRowHeaderModel == null) {
			tableRowHeaderModel = createRowHeaderModel();
		}
		return tableRowHeaderModel;
	}

	/**
	 * このスプレッドシート用行ヘッダとするコンポーネントを返す。
	 * @return 行ヘッダとするコンポーネント
	 */
	public SpreadSheetRowHeader getTableRowHeader() {
		if (tableRowHeader == null) {
			tableRowHeader = createDefaultRowHeader();
		}
		return tableRowHeader;
	}

	/**
	 * スプレッドシート行ヘッダで表示する行の名前を返す。
	 * このメソッドが <tt>null</tt> を返した場合、表示される名前は行ヘッダのデータモデルに移譲される。
	 * @param rowIndex	行インデックス
	 * @return	行ヘッダに表示する文字列、行ヘッダデータモデルの内容とする場合は <tt>null</tt>
	 * @since 3.2.0
	 */
	public String getRowHeaderName(int rowIndex) {
		TableModel tmodel = getModel();
		if (tmodel instanceof AbSpreadSheetTableModel) {
			return ((AbSpreadSheetTableModel)tmodel).getRowName(convertRowIndexToModel(rowIndex));
		}
		else {
			return null;
		}
	}

	/**
	 * スプレッドシート行ヘッダで表示するツールチップテキストを返す。
	 * このメソッドは、{@link SpreadSheetRowHeader#getToolTipText(MouseEvent)} メソッドから
	 * 呼び出される。
	 * @param header	行ヘッダのコンポーネント
	 * @param event		行ヘッダ上でのマウスイベント
	 * @return	ツールチップとして表示する文字列を返す。
	 * 			<tt>null</tt> を返した場合は、行ヘッダコンポーネントの
	 * 			標準の動作となる。
	 * @see	SpreadSheetRowHeader#getToolTipText(MouseEvent)
	 * @since 1.14
	 */
	public String getRowHeaderToolTipText(SpreadSheetRowHeader header, MouseEvent event) {
		TableModel tmodel = getModel();
		if (tmodel instanceof AbSpreadSheetTableModel) {
			int rowIndex = rowAtPoint(event.getPoint());
			if (rowIndex >= 0 && rowIndex < tmodel.getRowCount()) {
				return ((AbSpreadSheetTableModel)tmodel).getRowHeaderToolTipText(convertRowIndexToModel(rowIndex));
			}
		}
		return null;
	}

	/**
	 * キーストロークによる自動編集開始の設定状態を取得する。
	 * 
	 * @return	自動編集開始の場合は <tt>true</tt>、それ以外の場合は <tt>false</tt> を返す。
	 */
	public boolean isEnabledAutoStartEditOnKeyStroke() {
		Object value = getClientProperty("JTable.autoStartsEdit");
		if (value instanceof Boolean)
			return ((Boolean)value).booleanValue();
		else
			return false;
	}

	/**
	 * キーストロークによる自動編集開始を設定する。
	 * 
	 * @param toEnable	自動編集開始とする場合は <tt>true</tt>
	 */
	public void setAutoStartEditOnKeyStroke(boolean toEnable) {
		putClientProperty("JTable.autoStartsEdit", Boolean.valueOf(toEnable));
	}

	/**
	 * テキストコンポーネントのセルエディタにおいて、エディタ起動時に
	 * テキストを全て選択された状態とする設定を取得する。
	 * 
	 * @return	テキスト全選択の場合は <tt>true</tt>、それ以外の場合は <tt>false</tt> を返す。
	 */
	public boolean isEnabledAllTextSelectionAtTextEditorWhenEditingStart() {
		return this.flgAllTextSelectionAtTextEditorWhenEditingStart;
	}

	/**
	 * テキストコンポーネントのセルエディタにおいて、エディタ起動時にテキストを全て
	 * 選択された状態の設定を行う。デフォルトでは <tt>true</tt> となっている。
	 * 
	 * @param toEnable	テキスト全選択を有効とする場合は <tt>true</tt>
	 */
	public void setAllTextSelectionAtTextEditorWhenEditingStart(boolean toEnable) {
		this.flgAllTextSelectionAtTextEditorWhenEditingStart = toEnable;
	}

	/**
	 * このテーブルにフォーカスを設定する。
	 * このメソッドは、Swingの描画スレッドで適切に処理される。
	 */
	public void setFocus() {
		if (!this.hasFocus()) {
			SwingUtilities.invokeLater(hFocusRequester);
		}
	}

	/**
	 * このテーブルのリードセルの位置をステータスバーに通知する。
	 * @param infoBar	通知対象のステータスバー
	 */
	public void updateLocationForStatusBar(StatusBar infoBar) {
		if (infoBar != null) {
			CellIndex ci = getLeadSelectionCellIndex();
			if (ci != null) {
				infoBar.setCaretPosition(ci.row+1, ci.column+1);
			} else {
				infoBar.clearPosition();
			}
		}
	}
	
	public boolean hasValidCell() {
		return (getRowCount() > 0 && getColumnCount() > 0);
	}
	
	public boolean hasSelectedCells() {
		return (getSelectedRowCount()>0 && getSelectedColumnCount()>0);
	}
	
	public boolean isMultipleSelectionCells() {
		int numRows = getSelectedRowCount();
		int numCols = getSelectedColumnCount();
		if (numRows > 1)
			return (numCols > 0);
		else if (numCols > 1)
			return (numRows > 0);
		else
			return false;
	}
	
	/**
	 * 単一のセルのみが選択されているかを判定する。
	 * @return	単一のセルのみが選択されていれば <tt>true</tt>
	 * @since 1.16
	 */
	public boolean isSelectionOneCell() {
		return (getSelectedRowCount()==1 && getSelectedColumnCount()==1);
	}

	/**
	 * 選択されている最初のセルを返す。
	 * このメソッドが返すセルの位置は、{@link #getSelectedRow()} と {@link #getSelectedColumn()} が
	 * 返す位置となる。選択されているセルが存在しない場合、このメソッドは <tt>null</tt> を返す。
	 * @return	選択されているセルの位置を返す。選択されていない場合は <tt>null</tt> を返す。
	 * @since 1.16
	 */
	public CellIndex getSelectedCell() {
		int row = getSelectedRow();
		int col = getSelectedColumn();
		if (row < 0 || col < 0) {
			// no selection
			return null;
		} else {
			// selected
			return new CellIndex(row, col);
		}
	}

	/**
	 * 全ての行が選択されている状態かを判定する。
	 * このメソッドは、行の選択状態のみを判定するものであり、
	 * 列の状態については考慮しない。
	 * 
	 * @return	列の状態に関係なく、全ての行が選択済みであれば <tt>true</tt>
	 */
	public boolean isSelectionAllRows() {
		int numRows = getRowCount();
		return (numRows>0 && numRows==getSelectedRowCount());
	}

	/**
	 * 全ての列が選択されている状態かを判定する。
	 * このメソッドは、列の選択状態のみを判定するものであり、
	 * 行の状態については考慮しない。
	 * 
	 * @return	行の状態に関係なく、全ての列が選択済みであれば <tt>true</tt>
	 */
	public boolean isSelectionAllColumns() {
		int numCols = getColumnCount();
		return (numCols>0 && numCols==getSelectedColumnCount());
	}

	/**
	 * 指定された行が選択状態であり、その行に含まれる全ての列が選択されている
	 * 場合に <tt>true</tt> を返す。
	 * 
	 * @param rowIndex	判定対象の行インデックス
	 * @return	全ての列が選択された行である場合は <tt>true</tt>
	 */
	public boolean isRowSelectedAllColumns(int rowIndex) {
		return (isRowSelected(rowIndex) && isSelectionAllColumns());
	}

	/**
	 * 指定された列が選択状態であり、その列に含まれる全ての行が選択されている
	 * 場合に <tt>true</tt> を返す。
	 * 
	 * @param columnIndex	判定対象の列インデックス
	 * @return	全ての行が選択された列である場合は <tt>true</tt>
	 */
	public boolean isColumnSelectedAllRows(int columnIndex) {
		return (isColumnSelected(columnIndex) && isSelectionAllRows());
	}
	
	public CellIndex getLowerBoundCellIndex() {
		if (hasValidCell())
			return LOWER_CELL_INDEX;
		else
			return null;
	}
	
	public CellIndex getUpperBoundCellIndex() {
		if (hasValidCell())
			return new CellIndex(getRowCount()-1, getColumnCount()-1);
		else
			return null;
	}
	
	public int getMinSelectionRowIndex() {
		return getSelectionModel().getMinSelectionIndex();
	}
	
	public int getMaxSelectionRowIndex() {
		return getSelectionModel().getMaxSelectionIndex();
	}
	
	public int getAnchorSelectionRowIndex() {
		return getSelectionModel().getAnchorSelectionIndex();
	}
	
	public int getLeadSelectionRowIndex() {
		return getSelectionModel().getLeadSelectionIndex();
	}
	
	public int getMinSelectionColumnIndex() {
		return getColumnModel().getSelectionModel().getMinSelectionIndex();
	}
	
	public int getMaxSelectionColumnIndex() {
		return getColumnModel().getSelectionModel().getMaxSelectionIndex();
	}
	
	public int getAnchorSelectionColumnIndex() {
		return getColumnModel().getSelectionModel().getAnchorSelectionIndex();
	}
	
	public int getLeadSelectionColumnIndex() {
		return getColumnModel().getSelectionModel().getLeadSelectionIndex();
	}
	
	public CellIndex getMinSelectionCellIndex() {
		return new CellIndex(getMinSelectionRowIndex(), getMinSelectionColumnIndex());
	}
	
	public CellIndex getMaxSelectionCellIndex() {
		return new CellIndex(getMaxSelectionRowIndex(), getMaxSelectionColumnIndex());
	}
	
	public CellIndex getAnchorSelectionCellIndex() {
		return new CellIndex(getAnchorSelectionRowIndex(), getAnchorSelectionColumnIndex());
	}
	
	public CellIndex getLeadSelectionCellIndex() {
		return new CellIndex(getLeadSelectionRowIndex(), getLeadSelectionColumnIndex());
	}
	
	public boolean isCellSelected(final CellIndex index) {
		return isCellSelected(index.row, index.column);
	}
	
	public void setCellSelected(int rowIndex, int columnIndex) {
		clearSelection();
		setColumnSelectionInterval(columnIndex, columnIndex);
		setRowSelectionInterval(rowIndex, rowIndex);
	}
	
	public void setCellSelected(final CellIndex index) {
		setCellSelected(index.row, index.column);
	}
	
	public void scrollToVisibleCell(int rowIndex, int columnIndex) {
		Rectangle rcCell = getCellRect(rowIndex, columnIndex, false);
		scrollRectToVisible(rcCell);
	}
	
	public void scrollToVisibleCell(final CellIndex index) {
		scrollToVisibleCell(index.row, index.column);
	}
	
	public void setFocusToCell(int rowIndex, int columnIndex) {
		getColumnModel().getSelectionModel().setLeadSelectionIndex(columnIndex);
		getColumnModel().getSelectionModel().setAnchorSelectionIndex(columnIndex);
		getSelectionModel().setLeadSelectionIndex(rowIndex);
		getSelectionModel().setAnchorSelectionIndex(rowIndex);
		scrollToVisibleCell(rowIndex, columnIndex);
	}
	
	public void setFocusToCell(final CellIndex index) {
		setFocusToCell(index.row, index.column);
	}
	
	public CellIndex getNextCellIndex(final CellIndex index) {
		if (index.row < 0 || index.column < 0)
			return null;
		
		int row = index.row;
		int col = index.column + 1;
		
		if (col >= getColumnCount()) {
			row++;
			col = 0;
		}
		
		if (row >= getRowCount()) {
			return null;
		}
		
		return new CellIndex(row, col);
	}
	
	public CellIndex getPreviousCellIndex(final CellIndex index) {
		int numRows = getRowCount();
		int numCols = getColumnCount();
		
		if (index.row >= numRows || index.column >= numCols)
			return null;
		
		int row = index.row;
		int col = index.column - 1;
		
		if (col < 0) {
			row--;
			col = numCols - 1;
		}
		
		if (row < 0) {
			return null;
		}
		
		return new CellIndex(row, col);
	}

	/**
	 * 指定された位置のセルの値を、指定された値で更新する。
	 * このメソッドは、セルが編集可能な場合のみセルの値を設定するメソッドとなる。
	 * 
	 * @param value			セルに設定する値
	 * @param rowIndex		行インデックス
	 * @param columnIndex	列インデックス
	 */
	public void updateValueAt(Object value, int rowIndex, int columnIndex) {
		if (isCellEditable(rowIndex, columnIndex)) {
			setValueAt(value, rowIndex, columnIndex);
		}
	}

	/**
	 * 選択されているセルの内容をクリップボードにコピーし、
	 * 選択されている編集可能セルの内容を消去する。
	 */
	public void cut() {
		onEditActionPerformed(TableEditCutActionName);
	}

	/**
	 * 選択されているセルの内容をクリップボードにコピーする。
	 */
	public void copy() {
		onEditActionPerformed(TableEditCopyActionName);
	}

	/**
	 * クリップボードのコピー可能な内容を、現在のカーソル
	 * 位置に代入する。セルが選択されていない、もしくは
	 * 単一選択の場合、コピー元のデータを現在のカーソル位置から
	 * 複数セルに代入する。
	 * 複数セルが選択されている場合、選択範囲内に限定して、
	 * コピー元の選択範囲の左上から代入する。
	 * 複数セルが選択されている場合、選択セルが連続していない
	 * 場合はエラーとする。
	 */
	public void paste() {
		onEditActionPerformed(TableEditPasteActionName);
	}

	/**
	 * 現在選択されているセルの内容を消去する。
	 */
	public void delete() {
		onEditActionPerformed(TableEditDeleteActionName);
	}

	/**
	 * 表示されている行のデータのみを使用して列幅の自動調整を行うかどうかの
	 * 設定を取得する。
	 * @return	表示行のデータのみを使用して列幅の自動調整を行う場合は <tt>true</tt>、
	 * 			全データを使用して列幅の自動調整を行う場合は <tt>false</tt>
	 * <p>デフォルトでは <tt>false</tt> が設定されている。
	 * @since 1.17
	 */
	public boolean getAdjustOnlyVisibleRows() {
		return _adjustOnlyVisibleRows;
	}

	/**
	 * 表示されている行のデータのみを使用して列幅の自動調整を行うかどうかを設定する。
	 * <p>デフォルトでは <tt>false</tt> が設定されている。
	 * @param toEnable	表示行のデータのみを使用して列幅の自動調整を行う場合は <tt>true</tt>、
	 * 					全データを使用して列幅の自動調整を行う場合は <tt>false</tt>
	 * @since 1.17
	 */
	public void setAdjustOnlyVisibleRows(boolean toEnable) {
		_adjustOnlyVisibleRows = toEnable;
	}

	/**
	 * 選択されているセルのデータのみを使用して列幅の自動調整を行うかどうかの
	 * 設定を取得する。
	 * <p>デフォルトでは <tt>true</tt> が設定されている。
	 * @return	選択セルのみのデータを使用して列幅の自動調整を行う場合は <tt>true</tt>、
	 * 			そうでない場合は <tt>false</tt>
	 * @since 1.17
	 */
	public boolean getAdjustOnlySelectedCells() {
		return _adjustOnlySelectedCells;
	}

	/**
	 * 選択されているセルのデータのみを使用して列幅の自動調整を行うかどうかの
	 * 設定を取得する。
	 * <p>デフォルトでは <tt>true</tt> が設定されている。
	 * @param toEnable	選択セルのみのデータを使用して列幅の自動調整を行う場合は <tt>true</tt>、
	 * 					そうでない場合は <tt>false</tt>
	 * @since 1.17
	 */
	public void setAdjustOnlySelectedCells(boolean toEnable) {
		_adjustOnlySelectedCells = toEnable;
	}

	/**
	 * 指定されたカラムの幅を、セルデータの幅に合わせる。
	 * セルデータの最大幅がカラムヘッダのデータよりも小さい場合、カラムヘッダの
	 * データ幅に合わせる。
	 * <p>このメソッドは、カラム属性のサイズ変更の可否に関わらず列幅を調整する。
	 * 
	 * @param columnIndex	幅を調整する列の列インデックス
	 * @see #getAdjustOnlyVisibleRows()
	 * @see #getAdjustOnlySelectedCells()
	 */
	public void adjustColumnPreferredWidth(int columnIndex) {
		// Check
		validColumnIndexRange(columnIndex);
		
		// adjusting
		TableColumn columnHeader = getColumnModel().getColumn(columnIndex);
		if (adjustLocalColumnWidth(columnIndex, columnHeader, getAdjustOnlySelectedCells())) {
			revalidate();
		}
	}

	/**
	 * 全てのカラム幅を、セルデータの幅に合わせる。
	 * セルデータの最大幅がカラムヘッダのデータよりも小さい場合、カラムヘッダの
	 * データ幅に合わせる。
	 * <p>
	 * このメソッドは、サイズ変更不可のカラムも含め、サイズを調整する。
	 * @see #getAdjustOnlyVisibleRows()
	 * @see #getAdjustOnlySelectedCells()
	 */
	public void adjustAllColumnsPreferredWidth() {
		int numCols = getColumnCount();
		boolean modified = false;
		for (int col = 0; col < numCols; col++) {
			if (adjustLocalColumnWidth(col, getColumnModel().getColumn(col), getAdjustOnlySelectedCells())) {
				modified = true;
			}
		}
		if (modified) {
			revalidate();
		}
	}
	
	/**
	 * 全てのカラム幅を、セルデータの幅に合わせる。
	 * セルデータの最大幅がカラムヘッダのデータよりも小さい場合、カラムヘッダの
	 * データ幅に合わせる。このメソッドは、このコンポーネントの現在の幅を基準とする。
	 * <p>
	 * <em>onlyResizableColumn</em> と <em>lastColumnFitToView</em> がどちらも <tt>true</tt> の
	 * 場合、終端にもっとも近いサイズ変更可能なカラム幅が調整される。
	 * @param onlyResizableColumn	サイズ変更可能なカラムのみ幅を調整する場合は <tt>true</tt>
	 * @param lastColumnFitToView	最終カラムを表示領域の大きさに合わせる場合は <tt>true</tt>
	 * @since 1.14
	 * @see #getAdjustOnlyVisibleRows()
	 * @see #getAdjustOnlySelectedCells()
	 */
	public void adjustAllColumnsPreferredWidth(boolean onlyResizableColumn, boolean lastColumnFitToView) {
		adjustAllColumnsPreferredWidth(onlyResizableColumn, lastColumnFitToView, getWidth());
	}
	
	/**
	 * 全てのカラム幅を、セルデータの幅に合わせる。
	 * セルデータの最大幅がカラムヘッダのデータよりも小さい場合、カラムヘッダの
	 * データ幅に合わせる。
	 * <p>
	 * <em>onlyResizableColumn</em> と <em>lastColumnFitToView</em> がどちらも <tt>true</tt> の
	 * 場合、終端にもっとも近いサイズ変更可能なカラム幅が調整される。
	 * @param onlyResizableColumn	サイズ変更可能なカラムのみ幅を調整する場合は <tt>true</tt>
	 * @param lastColumnFitToView	最終カラムを表示領域の大きさに合わせる場合は <tt>true</tt>
	 * @param fitWidth				表示領域の幅を指定する。<em>lastColumnFitToView</em> が <tt>true</tt> の
	 * 								のとき、このサイズをカラム全体の目標幅とし、カラム幅を調整する。
	 * 								この値が 0 以下のときは、このコンポーネントの現在の幅が使用される。
	 * @since 1.14
	 * @see #getAdjustOnlyVisibleRows()
	 * @see #getAdjustOnlySelectedCells()
	 */
	public void adjustAllColumnsPreferredWidth(boolean onlyResizableColumn, boolean lastColumnFitToView, int fitWidth)
	{
		TableColumnModel colModel = getColumnModel();
		int numCols = getColumnCount();
		int lastResizableColumn = -1;
		boolean modified = false;
		for (int col = 0; col < numCols; col++) {
			TableColumn columnHeader = colModel.getColumn(col);
			if (!onlyResizableColumn || columnHeader.getResizable()) {
				lastResizableColumn = col;
				if (adjustLocalColumnWidth(col, columnHeader, getAdjustOnlySelectedCells())) {
					modified = true;
				}
			}
		}
		
		if (lastResizableColumn >= 0 && lastColumnFitToView) {
			if (modified) {
				doLayout();
			}
			if (adjustColumnToFit((fitWidth>0 ? fitWidth : getWidth()), colModel.getColumn(lastResizableColumn))) {
				modified = true;
			}
		}
		
		if (modified) {
			revalidate();
		}
	}
	
	/**
	 * 指定されたカラムの幅を、セルデータの最大幅に合わせる。
	 * セルデータの最大幅の算出では、{@link #getAdjustOnlyVisibleRows()} が <tt>true</tt> を返す場合、
	 * 表示されている行のセルデータのみを使用する。{@link #getAdjustOnlyVisibleRows()} が <tt>false</tt> を
	 * 返す場合は、このテーブルの全行のセルデータを使用する。
	 * <br><em>adjustOnlySelectedCells</em> に <tt>true</tt> を指定した場合、指定された行範囲に
	 * 含まれる同じ列の選択セルのデータのみを使用してサイズを設定する。この場合、列ヘッダの
	 * 幅は使用しない。
	 * <p>このメソッドは、サイズ変更可能なカラムかどうかに関わらず実行される。
	 * @param beginRowIndex		データを取得する範囲の先頭行インデックス
	 * @param endRowIndex		データを取得する範囲の終端行の次のインデックス
	 * @param columnIndex		幅を取得する列のインデックス
	 * @param columnHeader		カラムヘッダ
	 * @param adjustOnlySelectedCells	選択されているセルのデータのみを使用する場合に <tt>true</tt> を指定する。
	 * @return 列幅が変更された場合は <tt>true</tt>、変更されなかった場合は <tt>false</tt>
	 * @since 1.17
	 * @see #getAdjustOnlyVisibleRows()
	 */
	protected boolean adjustLocalColumnWidth(int columnIndex, TableColumn columnHeader, boolean adjustOnlySelectedCells)
	{
		if (getAdjustOnlyVisibleRows()) {
			Rectangle rc = getVisibleCellIndices();
			if (rc.isEmpty()) {
				return adjustLocalColumnWidth(rc.y, (rc.y + rc.width), columnIndex, columnHeader, adjustOnlySelectedCells);
			} else {
				// 表示領域が存在しないなら、テーブル全体
				return adjustLocalColumnWidth(0, getRowCount(), columnIndex, columnHeader, adjustOnlySelectedCells);
			}
		} else {
			return adjustLocalColumnWidth(0, getRowCount(), columnIndex, columnHeader, adjustOnlySelectedCells);
		}
	}

	/**
	 * 指定されたカラムの <em>beginRowIndex</em> から (<em>endRowIndex</em> - 1) までの
	 * セルデータの最大幅に合わせる。セルデータの最大幅がカラムヘッダのデータよりも小さい場合、
	 * カラムヘッダのデータ幅に合わせる。
	 * (<em>beginRowIndex</em> &gt;= <em>endRowIndex</em>) の場合は、カラム幅を変更しない。
	 * <br><em>adjustOnlySelectedCells</em> に <tt>true</tt> を指定した場合、指定された行範囲に
	 * 含まれる同じ列の選択セルのデータのみを使用してサイズを設定する。この場合、列ヘッダの
	 * 幅は使用しない。
	 * <p>このメソッドは、サイズ変更可能なカラムかどうかに関わらず実行される。
	 * @param beginRowIndex		データを取得する範囲の先頭行インデックス
	 * @param endRowIndex		データを取得する範囲の終端行の次のインデックス
	 * @param columnIndex		幅を取得する列のインデックス
	 * @param columnHeader		カラムヘッダ
	 * @param adjustOnlySelectedCells	選択されているセルのデータのみを使用する場合に <tt>true</tt> を指定する。
	 * @return 列幅が変更された場合は <tt>true</tt>、変更されなかった場合は <tt>false</tt>
	 * @since 1.17
	 */
	protected boolean adjustLocalColumnWidth(int beginRowIndex, int endRowIndex, int columnIndex,
												TableColumn columnHeader, boolean adjustOnlySelectedCells)
	{
		int maxWidth = getAdjustedColumnWidth(beginRowIndex, endRowIndex, columnIndex, columnHeader, adjustOnlySelectedCells);
		if (maxWidth < columnHeader.getMinWidth()) {
			maxWidth = columnHeader.getMinWidth();
		}
		if (maxWidth > columnHeader.getMaxWidth()) {
			maxWidth = columnHeader.getMaxWidth();
		}
		
		// カラム幅の設定
		int curWidth = columnHeader.getWidth();
		if (maxWidth != curWidth) {
			columnHeader.setPreferredWidth(maxWidth);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 指定されたカラムの <em>beginRowIndex</em> から (<em>endRowIndex</em> - 1) までのデータが
	 * すべて表示可能なサイズを取得する。
	 * (<em>beginRowIndex</em> &gt;= <em>endRowIndex</em>) の場合は、現在のカラム推奨幅を返す。
	 * <br><em>adjustOnlySelectedCells</em> に <tt>true</tt> を指定した場合、指定された行範囲に
	 * 含まれる同じ列の選択セルのデータのみを使用してサイズを設定する。この場合、列ヘッダの
	 * 幅は使用しない。
	 * @param beginRowIndex		データを取得する範囲の先頭行インデックス
	 * @param endRowIndex		データを取得する範囲の終端行の次のインデックス
	 * @param columnIndex		幅を取得する列のインデックス
	 * @param columnHeader		カラムヘッダ
	 * @param adjustOnlySelectedCells	選択されているセルのデータのみを使用する場合に <tt>true</tt> を指定する。
	 * @return	計算された列の幅を返す
	 * @since 1.16
	 */
	protected int getAdjustedColumnWidth(int beginRowIndex, int endRowIndex, int columnIndex,
											TableColumn columnHeader, boolean adjustOnlySelectedCells)
	{
		if (beginRowIndex >= endRowIndex) {
			return (columnHeader!=null ? columnHeader.getPreferredWidth() : 0);
		}
		
		// 指定カラムのセルに含まれるデータの表示最大幅を取得
		boolean hasSelectedCellWidth = false;
		int selectedMaxWidth = 0;
		if (isColumnSelectedAllRows(columnIndex)) {
			// 全行が選択されている場合は、列全体を調整対象とする
			adjustOnlySelectedCells = false;
		}
		int maxWidth = 0;
		for (int row = beginRowIndex; row < endRowIndex; row++) {
			Object value = getValueAt(row, columnIndex);
			int width = 0;
			if (value != null) {
				//--- 表示が大きく変化しないよう、選択、フォーカスも設定されているものとする
				TableCellRenderer renderer = getCellRenderer(row, columnIndex);
				Component comp = renderer.getTableCellRendererComponent(this, value, true, true, row, columnIndex);
				width = comp.getPreferredSize().width;
				maxWidth = Math.max(width, maxWidth);
			}
			if (adjustOnlySelectedCells && isCellSelected(row, columnIndex)) {
				hasSelectedCellWidth = true;
				selectedMaxWidth = Math.max(width, selectedMaxWidth);
			}
		}
		
		// カラムの推奨幅を算出
		if (hasSelectedCellWidth) {
			if (selectedMaxWidth <= 0) {
				maxWidth = 2;	// 最小幅
			} else {
				maxWidth = selectedMaxWidth;
			}
			// 選択したセルの幅がある場合は、列ヘッダの幅は使用しない
			columnHeader = null;
		}
		if (columnHeader != null) {
			TableCellRenderer headerRenderer = columnHeader.getHeaderRenderer();
			if (headerRenderer == null)
				headerRenderer = getTableHeader().getDefaultRenderer();
			Object headerValue = columnHeader.getHeaderValue();
			Component headerComp = headerRenderer.getTableCellRendererComponent(this, headerValue, true, true, -1, columnIndex);
			maxWidth = Math.max(maxWidth, headerComp.getPreferredSize().width);
		}
		//--- 推奨幅の調整
		if (maxWidth > 0) {
			maxWidth += 2;
		}
		
		return maxWidth;
	}

	/**
	 * <em>columnHeader</em> のカラム幅が指定された値より小さい場合に、
	 * <em>columnHeader</em> のカラム幅を指定の幅に設定する。
	 * @param fitWidth		設定する推奨幅
	 * @param columnHeader	設定対象のカラム
	 * @return	幅が変更された場合は <tt>true</tt>、変更されなかった場合は <tt>false</tt>
	 */
	protected boolean adjustColumnToFit(int fitWidth, TableColumn columnHeader) {
		int pWidth = columnHeader.getPreferredWidth();
		int delta = fitWidth - getTotalPreferredColumnWidth();
		if (delta > 0) {
			columnHeader.setPreferredWidth(pWidth + delta);
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * 全カラムの推奨幅の合計を取得する。
	 * @return	全カラムの推奨幅の合計値
	 */
	protected int getTotalPreferredColumnWidth() {
		TableColumnModel model = getColumnModel();
		int totalWidth = 0;
		int numCols = model.getColumnCount();
		for (int col = 0; col < numCols; col++) {
			totalWidth += model.getColumn(col).getPreferredWidth();
		}
		return totalWidth;
	}

	/**
	 * 表示されているテーブルの範囲を示す行インデックスと列インデックスの
	 * 矩形を返す。<br>
	 * {@link java.awt.Rectangle#x} は表示領域左上の列インデックス、
	 * {@link java.awt.Rectangle#y} は表示領域左上の行インデックス、
	 * {@link java.awt.Rectangle#width} は表示領域内の列数、
	 * {@link java.awt.Rectangle#height} は表示領域内の行数を格納する。<br>
	 * このメソッドは、表示領域となる座標を {@link #getVisibleRect()} によって座標を表す矩形を取得する。
	 * 表示領域左上の座標から行インデックスもしくは列インデックスが取得できない場合、取得できない
	 * インデックス値を 0 とする。また、表示領域右下の座標から列インデックスが取得できない場合は最大列数、
	 * 行インデックスが取得できない場合は最大行数を上限として領域の大きさを格納する。
	 * 表示領域が空の場合、このメソッドは(すべての要素が 0 の)空の矩形を返す。
	 * @return	<code>Rectangle</code> オブジェクト
	 * @since 1.17
	 */
	public Rectangle getVisibleCellIndices() {
		Rectangle box = getVisibleRect();
		Point pt = box.getLocation();
		int rBegin = rowAtPoint(pt);
		int cBegin = columnAtPoint(pt);
		if (rBegin < 0)	rBegin=0;
		if (cBegin < 0) cBegin=0;
		if (box.isEmpty()) {
			return new Rectangle();
		}
		pt.translate(box.width, box.height);
		
		int rEnd = rowAtPoint(pt);
		int cEnd = columnAtPoint(pt);
		if (rEnd < rBegin) {
			rEnd = getRowCount();
		} else {
			rEnd = Math.min(rEnd + 1, getRowCount());
		}
		if (cEnd < cBegin) {
			cEnd = getColumnCount();
		} else {
			cEnd = Math.min(cEnd + 1, getColumnCount());
		}
		
		return new Rectangle(cBegin, rBegin, (cEnd-cBegin), (rEnd-rBegin));
	}

	/**
	 * 現在選択されているセルを含む全ての列を選択する。
	 * このメソッドは、列選択モデルの全ての要素を選択状態に変更する。
	 * 行選択モデルは変更しない。
	 */
	public void selectAllColumns() {
		if (isEditing()) {
			removeEditor();
		}
		
		if (getRowCount() > 0 && getColumnCount() > 0 && hasSelectedCells()) {
			int oldLead;
			int oldAnchor;
			ListSelectionModel selModel = getColumnModel().getSelectionModel();
			selModel.setValueIsAdjusting(true);
			oldLead = selModel.getLeadSelectionIndex();
			if (oldLead >= getColumnCount())
				oldLead = -1;
			oldAnchor = selModel.getAnchorSelectionIndex();
			if (oldAnchor >= getColumnCount())
				oldAnchor = -1;
			
			//--- 全てのカラムを選択
            setColumnSelectionInterval(0, getColumnCount()-1);
            
            //--- anchor & lead を戻す
            if (oldAnchor < 0) {
            	oldAnchor = oldLead;
            }
            if (oldLead < 0) {
            	selModel.setAnchorSelectionIndex(-1);
            	selModel.setLeadSelectionIndex(-1);
            } else {
            	selModel.addSelectionInterval(oldAnchor, oldLead);
            }
            
            //--- 完了
            selModel.setValueIsAdjusting(false);
		}
	}

	/**
	 * 現在選択されているセルを含む全ての行を選択する。
	 * このメソッドは、行選択モデルの全ての要素を選択状態に変更する。
	 * 列選択モデルは変更しない。
	 */
	public void selectAllRows() {
		if (isEditing()) {
			removeEditor();
		}
		
		if (getRowCount() > 0 && getColumnCount() > 0 && hasSelectedCells()) {
			int oldLead;
			int oldAnchor;
			ListSelectionModel selModel = getSelectionModel();
			selModel.setValueIsAdjusting(true);
			oldLead = selModel.getLeadSelectionIndex();
			if (oldLead >= getRowCount())
				oldLead = -1;
			oldAnchor = selModel.getAnchorSelectionIndex();
			if (oldAnchor >= getRowCount())
				oldAnchor = -1;
			
			//--- 全ての行を選択
			setRowSelectionInterval(0, getRowCount()-1);
			
			//--- anchor & lead を戻す
			if (oldAnchor < 0) {
				oldAnchor = oldLead;
			}
			if (oldLead < 0) {
				selModel.setAnchorSelectionIndex(-1);
				selModel.setLeadSelectionIndex(-1);
			} else {
				selModel.addSelectionInterval(oldAnchor, oldLead);
			}
			
			//--- 完了
			selModel.setValueIsAdjusting(false);
		}
	}

	/**
	 * このテーブルに、メニューアクセラレータキーとなるキーストロークを
	 * 登録する。ここで登録されたキーストロークは、テーブル内で処理をせず、
	 * メニューコンポーネントのキーストロークとして処理される。
	 * <b>注：</b>
	 * <blockquote>
	 * 次のキーストロークは、<code>SpreadSheetTable</code> 独自の処理を
	 * 実行する。
	 * <ul>
	 * <li>[Ctrl]+[X] - カット(セルの内容をクリップボードへコピーし、セル内容を消去)
	 * <li>[Ctrl]+[C] - コピー(セルの内容をクリップボードへコピー)
	 * <li>[Ctrl]+[V] - ペースト(クリップボードでのデータをセルへ上書き)
	 * <li>[Delete]   - 削除(セル内容の消去)
	 * </ul>
	 * 上記以外のキーストロークも <code>JTable</code> の標準キーストローク
	 * マッピングとして登録されているものもある。
	 * </blockquote>
	 * 
	 * @param strokes	登録するキーストローク
	 * @throws NullPointerException	<code>strokes</code> が <tt>null</tt> の場合、
	 * 									もしくは、<code>strokes</code> 内の要素に <tt>null</tt> が含まれている場合
	 * 
	 * @since 1.10
	 */
	public void registMenuAcceleratorKeyStroke(KeyStroke...strokes) {
		// check argument
		if (strokes == null)
			throw new NullPointerException("'strokes' is null.");
		
		// get Maps
		//InputMap imap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		//ActionMap amap = getActionMap();
		InputMap imap = SwingUtilities.getUIInputMap(this, WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		ActionMap amap = SwingUtilities.getUIActionMap(this);

		// regist key strokes
		for (KeyStroke ks : strokes) {
			if (ks == null)
				throw new NullPointerException("'strokes' in null element.");
			SwingTools.registerActionToMaps(imap, amap, ks, MenuAccelerationActionName, dummyMenuAction);
		}
	}

	//------------------------------------------------------------
	// Override JTable interfaces
	//------------------------------------------------------------

	@Override
	public boolean isCellEditable(int row, int column) {
		if (flgEditable)
			return super.isCellEditable(row, column);
		else
			return false;
	}

	@Override
	public Component prepareEditor(TableCellEditor editor, int row, int column) {
		Component ec = super.prepareEditor(editor, row, column);
		//--- Component が JTextComponent の場合、初期状態でテキストを全選択する
		if (ec instanceof JTextComponent && isEnabledAllTextSelectionAtTextEditorWhenEditingStart()) {
			((JTextComponent)ec).selectAll();
		}
		
		return ec;
	}
	
	public void changeColumnHeaderSelection(int columnIndex, boolean toggle, boolean extend) {
		ListSelectionModel rsm = getSelectionModel();
		ListSelectionModel csm = getColumnModel().getSelectionModel();
		boolean selected = (getColumnSelectionAllowed() && csm.isSelectedIndex(columnIndex));
		
		// 列選択状態の変更
		changeTableSelectionModel(csm, columnIndex, toggle, extend, selected, false);
		
		// 行選択状態の変更
		if (csm.isSelectionEmpty()) {
			//--- 行の選択も解除
			rsm.clearSelection();
		}
		else {
			int numRows = getRowCount();
			int numSelRows = getSelectedRowCount();
			if (numRows > 0 && numRows != numSelRows) {
				//--- 行を全て選択(フォーカスは先頭行)
				rsm.setSelectionInterval(numRows-1, 0);
			}
		}
		
		// Scroll after changing the selection as blit scrolling is immediate,
		// so that if we cause the repaint after the scroll we end up painting
		// everything!
		if (getAutoscrolls()) {
			Rectangle cellRect = getCellRect(0, columnIndex, false);
			if (cellRect != null) {
				scrollRectToVisible(cellRect);
			}
		}
	}
	
	public void changeRowHeaderSelection(int rowIndex, boolean toggle, boolean extend) {
		ListSelectionModel rsm = getSelectionModel();
		ListSelectionModel csm = getColumnModel().getSelectionModel();
		boolean selected = (getRowSelectionAllowed() && rsm.isSelectedIndex(rowIndex));

		// 行選択状態の変更
		changeTableSelectionModel(rsm, rowIndex, toggle, extend, selected, true);
		
		// 列選択状態の変更
		if (rsm.isSelectionEmpty()) {
			//--- 列の選択も解除
			csm.clearSelection();
		}
		else {
			int numCols = getColumnCount();
			int numSelCols = getSelectedColumnCount();
			if (numCols > 0 && numCols != numSelCols) {
				//--- 列を全て選択(フォーカスは先頭列)
				csm.setSelectionInterval(numCols-1, 0);
			}
		}
		
		// Scroll after changing the selection as blit scrolling is immediate,
		// so that if we cause the repaint after the scroll we end up painting
		// everything!
		if (getAutoscrolls()) {
			Rectangle cellRect = getCellRect(rowIndex, 0, false);
			if (cellRect != null) {
				scrollRectToVisible(cellRect);
			}
		}
	}
    
    @Override
	public void tableChanged(TableModelEvent e) {
		super.tableChanged(e);
		
		// 行ヘッダーの更新
		if (tableRowHeaderModel != null) {
			if (e.getFirstRow() == TableModelEvent.HEADER_ROW) {
				tableRowHeaderModel.setSize(getRowCount());
			}
			else if (e.getType() == TableModelEvent.INSERT) {
				tableRowHeaderModel.setSize(getRowCount());
			}
			else if (e.getType() == TableModelEvent.DELETE) {
				tableRowHeaderModel.setSize(getRowCount());
			}
		}
		
		/*
		if (tableRowHeader != null) {
			if (e.getType() == TableModelEvent.INSERT
				|| e.getType() == TableModelEvent.DELETE
				|| e.getFirstRow() == TableModelEvent.HEADER_ROW)
			{
				//--- 行の追加/削除
				//System.err.println("SpreadSheetTable#tableChanged : updateNumberList(" + getRowCount() + ")");
				tableRowHeader.updateNumberList(getRowCount());
			}
		}
		*/
	}

    /**
     * セル編集が開始されるときに呼び出されるメソッド。
     * @since 3.3.1
     */
	@Override
	public boolean editCellAt(int row, int column, EventObject e) {
		/**
		 * Java8 の場合、以下のキーが押されると、セル編集が開始してしまうので、回避。
		 * ちなみに、Java6 ではこの現象は発生しない。
		 * 余計なことすんな、Oracle!
		 */
		if (e instanceof KeyEvent) {
//			System.out.println("[" + System.currentTimeMillis() + "] SpreadSheetTable#editCellAt(" + row + ", " + column + ", " + String.valueOf(e) + ")");
			KeyEvent ke = (KeyEvent)e;
			switch (ke.getKeyCode()) {
				case 0 :						// (Mac)[Fn]
				case KeyEvent.VK_CAPS_LOCK :	// (Mac)[caps]=20(0x14)
				case KeyEvent.VK_ESCAPE :		// [ESC]=27(0x1B)
				case KeyEvent.VK_NONCONVERT :	// (Win)[無変換]=29(0x1D)
				case KeyEvent.VK_META :			// (Mac)[command]=157(0x9D)
				case KeyEvent.VK_WINDOWS :		// (Win)[Windows]=524(0x20C)
				case KeyEvent.VK_CONTEXT_MENU :	// (Win)[Menu]=525(0x20D)
				case KeyEvent.VK_NUM_LOCK :		// (Win)[NumLock]=144(0x90)
				case KeyEvent.VK_SCROLL_LOCK :	// (Win)[ScrollLock]=145(0x91)
				case KeyEvent.VK_INSERT :		// (Win)[Insert]=155(0x9B)
					return false;	// キー入力を無視
			}
//			System.out.println("  ke.getKeyChar()    =[" + ke.getKeyChar() + "]");
//			System.out.println("  ke.getKeyCode()    =[" + ke.getKeyCode() + "]");
//			System.out.println("  ke.getKeyLocation()=[" + ke.getKeyLocation() + "]");
//			System.out.println("  ke.getModifiers()  =[" + ke.getModifiers() + "]");
//			System.out.println("  ke.getModifiersEx()=[" + ke.getModifiersEx() + "]");
//			System.out.println("  ke.getWhen()=[" + ke.getWhen() + "]");
//			System.out.println("  ke.isActionKey()   =[" + ke.isActionKey() + "]");
//			System.out.println("  ke.isAltDown()     =[" + ke.isAltDown() + "]");
//			System.out.println("  ke.isAltGraphDown()=[" + ke.isAltGraphDown() + "]");
//			System.out.println("  ke.isConsumed()    =[" + ke.isConsumed() + "]");
//			System.out.println("  ke.isControlDown() =[" + ke.isControlDown() + "]");
//			System.out.println("  ke.isMetaDown()    =[" + ke.isMetaDown() + "]");
//			System.out.println("  ke.isShiftDown()   =[" + ke.isShiftDown() + "]");
		}
		return super.editCellAt(row, column, e);
	}

	@Override
	protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed)
	{
		/*--- for Debug
		if (ks.equals(MacroMenuBar.getMenuResource(MacroMenuBar.ID_TABLE_ROW_DELETE).getAccelerator()) || ks.equals(MenuItemResource.getEditDeleteShortcutKeyStroke())) {
			System.out.println("SpreadSheetTable#processKeyBinding() start");
			System.out.println("  - [KeyStroke] " + ks);
			System.out.println("  - [KeyEvent]  " + e.toString());
			System.out.println("  - [condition] " + condition);
			System.out.println("  - [pressed]   " + pressed);
		}
		---*/
		
		//if (condition == WHEN_ANCESTOR_OF_FOCUSED_COMPONENT && !Boolean.FALSE.equals((Boolean)getClientProperty("JTable.autoStartsEdit"))) {
		if (condition == WHEN_ANCESTOR_OF_FOCUSED_COMPONENT) {
			//--- 基本的なJComponent.processKeyBinding を処理する。
			//--- これは、JTable#processKeyBinding() の実装により、"JTable.autoStartsEdit" が true の場合、
			//--- JMenuBar にあるショートカットよりも先に、セルの編集が開始されてしまう問題を回避するためであり、
			//--- この処理を回避するための仕組みがないため、独自の処理により、コンポーネントの
			//--- キーバインディングを処理してしまう。
			//--- getInputMap(int,boolean) や getActionMap(boolean) は、同一パッケージのみのアクセスとなっており、
			//--- 同様の操作を行うためのインタフェースは用意されていないので、create されても仕方がない
			//InputMap imap = getInputMap(condition);
			//ActionMap amap = getActionMap();
			InputMap imap = SwingUtilities.getUIInputMap(this, condition);
			ActionMap amap = SwingUtilities.getUIActionMap(this);
			if (imap != null && amap != null && isEnabled()) {
				Object binding = imap.get(ks);
				Action action = (binding == null) ? null : amap.get(binding);
				if (action instanceof MenuAcceleratorActionHandler) {
					//--- この場合、キーストロークはメニューアクセラレータとして登録されているため、
					//--- このクラスインスタンスでは処理しない
					return false;
				}
				else if (action != null) {
					boolean isProcessed = SwingUtilities.notifyAction(action, ks, e, this, e.getModifiers());
					if (isProcessed) {
						//--- ここでキーストロークが処理された場合は、以降の処理は必要ない
						//--- JTable#processKeyBinding の実装の通り
						//AppLogger.debug("SpreadSheetTable#processKeyBinding - KeyStroke[" + ks.toString() + "] action performed!");
						return isProcessed;
					}
				}
			}

			/*@@@ removed 2009.02.02 - この処理は、MenuAcceleratorActionHandler を使用する処理に置き換えられた @@@
			// 未処理の場合、メニューバーのショートカットキーを処理する
			//--- 親フレームにあるメニューバーから、キーストロークと同じアクセラレータが登録されているかを
			//--- 確認し、アクセラレータが存在していれば、JTable#processKeyBinding の処理をスキップする。
			//--- っつ～か、キー入力による自動編集が有効になっていても、メニューにアクセラレータが登録されて
			//--- いるのに、セルエディタを起動してどうすんの！
			Window parentFrame = SwingUtilities.windowForComponent(this);
			if (parentFrame instanceof JFrame) {
				ActionListener al = SwingTools.getMenuActionForKeyStroke(((JFrame)parentFrame).getJMenuBar(), ks);
				if (al != null) {
					//--- ショートカットキーが登録されているとみなし、
					//--- このメソッドでの処理をスキップする。
					//AppLogger.debug("SpreadSheetTable#processKeyBinding - KeyStroke[" + ks.toString() + "] registered menu for accelerator!");
					return false;
				}
			}
			**@@@ removed 2009.02.02 - この処理は、MenuAcceleratorActionHandler を使用する処理に置き換えられた @@@*/
		}
		
		// このコンポーネントのキーストロークを処理する
		//AppLogger.debug("SpreadSheetTable#processKeyBinding - call super-class method.");
		return super.processKeyBinding(ks, e, condition, pressed);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	protected int getTableAdjustedIndex(int index, boolean isRow) {
    	int compare = isRow ? getRowCount() : getColumnCount();
    	return (index < compare ? index : -1);
    }
    
    protected void changeTableSelectionModel(ListSelectionModel sm, int index,
    		boolean toggle, boolean extend, boolean selected, boolean isRow)
    {
    	if (extend) {
    		if (toggle) {
    			sm.setAnchorSelectionIndex(index);
    		}
    		else {
    			int anchorIndex = getTableAdjustedIndex(sm.getAnchorSelectionIndex(), isRow);
    			if (anchorIndex < 0) {
    				anchorIndex = 0;
    			}

    			sm.setSelectionInterval(anchorIndex, index);
    		}
    	}
    	else {
    		if (toggle) {
    			if (selected) {
    				sm.removeSelectionInterval(index, index);
    			}
    			else {
    				sm.addSelectionInterval(index, index);
    			}
    		}
    		else {
    			sm.setSelectionInterval(index, index);
    		}
    	}
    }

	private final void setupDefaults() {
		this.focusForeground = getForeground();
		this.focusBackground = new Color(255,199,60);
	}
	
	protected void showMessageBox(int errorCode) {
		// 親フレームを取得
		Window parentFrame = SwingUtilities.windowForComponent(this);
		
		// メッセージを取得
		String message = getErrorMessage(errorCode);
		if (message == null) {
			message = String.format("Error (%d)", errorCode);
		}
		
		// メッセージを表示
		Application.showErrorMessage(parentFrame, message);
	}

	/**
	 * 行インデックスの有効範囲をチェックする。指定された有効範囲外の場合は、例外をスローする。
	 * チェックでは、<code>0 &lt;= index &lt; rowSize</code> を正当とする。
	 * @param index	行インデックス
	 * @throws IndexOutOfBoundsException	指定されたインデックスが範囲外の場合
	 */
	protected final void validRowIndexRange(final int index) {
		if (index < 0)
			throw new IndexOutOfBoundsException("Row index out of range : index(" + index + ")<0");
		if (index >= getRowCount())
			throw new IndexOutOfBoundsException("Row index out of range : index(" + index + ")>=" + getRowCount());
	}
	
	/**
	 * 列インデックスの有効範囲をチェックする。指定された有効範囲外の場合は、例外をスローする。
	 * チェックでは、<code>0 &lt;= index &lt; colSize</code> を正当とする。
	 * @param index	列インデックス
	 * @throws IndexOutOfBoundsException	指定されたインデックスが範囲外の場合
	 */
	protected final void validColumnIndexRange(final int index) {
		if (index < 0)
			throw new IndexOutOfBoundsException("Column index out of range : index(" + index + ")<0");
		if (index >= getColumnCount())
			throw new IndexOutOfBoundsException("Column index out of range : index(" + index + ")>=" + getColumnCount());
	}
	
	protected void initialComponents() {
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);		// 横スクロール可能
		setCellSelectionEnabled(true);					// セル選択可能
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);	// 複数選択可能
		getTableHeader().setReorderingAllowed(false);	// 列の移動を禁止
		getTableHeader().setResizingAllowed(true);		// 列幅の変更を許可
		
		setupEditActions();
		
		// グリッド線の色を固定する
		setShowGrid(true);
		setGridColor(new Color(128,128,128));
//		if (AppLogger.isDebugEnabled()) {
//			AppLogger.debug(String.format("SpreadSheetTable#initialComponents : setShowGrid(true) & setGridColor(%s)", String.valueOf(getGridColor())));
//		}
	}
	
	protected RowHeaderModel createRowHeaderModel() {
		return new RowHeaderModel();
	}

	protected SpreadSheetRowHeader createDefaultRowHeader() {
		SpreadSheetRowHeader rowHeader = new SpreadSheetRowHeader(this);
		//--- modified by Y.Ishizuka : 2010.11.10 ---
		/*--- old codes : 2010.11.10 ---**
		rowHeader.setFixedCellWidth(50);
		/*--- end of old codes : 2010.11.10 ---*/
		rowHeader.setFixedCellWidth(-1);
		//--- end of modified : 2010.11.10 ---
		rowHeader.setBackground(this.getTableHeader().getBackground());
		return rowHeader;
	}

	@Override
	protected JTableHeader createDefaultTableHeader() {
		return new SpreadSheetColumnHeader(this, columnModel);
	}

	@Override
	protected TableModel createDefaultDataModel() {
		return SpreadSheetModel.newDefaultSpreadSheetModel();
	}

	/**
	 * このテーブルの編集アクションを設定する。
	 * 基本的に、InputMap と ActionMap に対し、キーストロークに応じた
	 * アクションの定義となる。
	 */
	protected void setupEditActions() {
		// get Maps
		//InputMap imap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		//ActionMap amap = getActionMap();
		InputMap imap = SwingUtilities.getUIInputMap(this, WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		ActionMap amap = SwingUtilities.getUIActionMap(this);
		
		//--- Test for default Input map
		/*
		System.err.println("@@@ SpreadSheetTable#setupEditActions - show all default key stroke mapping in InputMap");
		{
			TreeMap<String, String> km = new TreeMap<String,String>();
			KeyStroke[] keys = imap.allKeys();
			for (KeyStroke ks : keys) {
				Object name = imap.get(ks);
				Action action = amap.get(name);
				String str = "  - KS[" + ks + "] / Key[" + String.valueOf(name) + "] / Action[" + String.valueOf(action) + "]";
				km.put(ks.toString(), str);
			}
			for (Map.Entry<String, String> entry : km.entrySet()) {
				System.err.println(entry.getValue());
			}
		}
		System.err.println("@@@ SpreadSheetTable#setupEditActions - End of show all default key stroke mapping.");
		*/
		//--- End of test for default input map
		
		// 編集アクション設定
		//--- cut
		SwingTools.registerActionToMaps(imap, amap,
				MenuItemResource.getEditCutShortcutKeyStroke(),
				getCutAction().getValue(Action.ACTION_COMMAND_KEY),
				getCutAction());
		//--- copy
		SwingTools.registerActionToMaps(imap, amap,
				MenuItemResource.getEditCopyShortcutKeyStroke(),
				getCopyAction().getValue(Action.ACTION_COMMAND_KEY),
				getCopyAction());
		//--- paste
		SwingTools.registerActionToMaps(imap, amap,
				MenuItemResource.getEditPasteShortcutKeyStroke(),
				getPasteAction().getValue(Action.ACTION_COMMAND_KEY),
				getPasteAction());
		//--- delete
		SwingTools.registerActionToMaps(imap, amap,
				MenuItemResource.getEditDeleteShortcutKeyStroke(),
				getDeleteAction().getValue(Action.ACTION_COMMAND_KEY),
				getDeleteAction());
		
		// 既存のカット、コピー、ペーストアクションを変更
		Object key;
		//--- cut
		key = TransferHandler.getCutAction().getValue(Action.NAME);
		if (amap.get(key) != null) {
			amap.put(key, getCutAction());
		}
		//--- copy
		key = TransferHandler.getCopyAction().getValue(Action.NAME);
		if (amap.get(key) != null) {
			amap.put(key, getCopyAction());
		}
		//--- paste
		key = TransferHandler.getPasteAction().getValue(Action.NAME);
		if (amap.get(key) != null) {
			amap.put(key, getPasteAction());
		}
	}

	/**
	 * 編集機能のキーストロークによるアクションハンドラ。
	 * カット、コピー、ペースト、削除の処理を行う。
	 * @param commandKey	操作の種別を表すコマンド名
	 */
	protected void onEditActionPerformed(String commandKey) {
		// 編集中の場合は、編集をキャンセル
		if (isEditing()) {
			removeEditor();
		}

		SpreadSheetModel ssModel = null;
		{
			TableModel model = getModel();
			if (model instanceof SpreadSheetModel) {
				ssModel = (SpreadSheetModel)model;
				ssModel.startCompoundUndoableEdit();
			}
		}
		
		if (TableEditPasteActionName.equals(commandKey)) {
			// ペースト処理
			pasteFromClipboard();
		}
		else if (TableEditCutActionName.equals(commandKey)
				|| TableEditCopyActionName.equals(commandKey)
				|| TableEditDeleteActionName.equals(commandKey))
		{
			// カット、コピー、削除処理
			//--- 選択領域を取得
			int numSelectedRows = getSelectedRowCount();
			int numSelectedCols = getSelectedColumnCount();
			if (numSelectedRows <= 0 || numSelectedCols <= 0) {
				//--- 選択なし
				return;
			}
			int[] rows = getSelectedRows();
			int[] cols = getSelectedColumns();
			
			// クリップボードへコピー(カット、コピー)
			if (TableEditCutActionName.equals(commandKey) || TableEditCopyActionName.equals(commandKey)) {
				//--- コピー文字列生成
				StringBuilder sb = new StringBuilder();
				for (int r = 0; r < rows.length; r++) {
					//--- 先頭列
					Object field = getValueAt(rows[r], cols[0]);
					String value = enquoteCellString(field==null ? "" : field.toString());
					sb.append(value);
					//--- 先頭以外の列
					for (int c = 1; c < cols.length; c++) {
						field = getValueAt(rows[r], cols[c]);
						value = enquoteCellString(field==null ? "" : field.toString());
						sb.append('\t');
						sb.append(value);
					}
					//--- 行終端
					sb.append(lineSeparator);
				}
				//--- プレーンテキストとしてクリップボードへ転送
				StringSelection ss = new StringSelection(sb.toString());
				Clipboard clip = getToolkit().getSystemClipboard();
				clip.setContents(ss, ss);
			}
			
			// 選択位置の内容消去(カット、削除)
			if (TableEditCutActionName.equals(commandKey) || TableEditDeleteActionName.equals(commandKey)) {
				for (int r = 0; r < rows.length; r++) {
					for (int c = 0; c < cols.length; c++) {
						updateValueAt(null, rows[r], cols[c]);
					}
				}
				//--- 選択を解除
				clearSelection();
			}
		}
		
		if (ssModel != null) {
			ssModel.endCompoundUndoableEdit();
		}
	}
	
	protected void pasteFromClipboard() {
		// 貼り付ける領域が存在しない場合は、処理しない
		if (getRowCount() <= 0 || getColumnCount() <= 0) {
			return;
		}
		
		// 転送可能なオブジェクトを取得
		Clipboard clip = getToolkit().getSystemClipboard();
		Transferable trans = clip.getContents(this);
		//--- 文字列以外は貼り付け禁止
		if (trans == null || !trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			return;
		}
		
		// 貼り付け範囲を取得
		CellIndex spos;
		CellIndex epos;
		int numSelectedRows = getSelectedRowCount();
		int numSelectedCols = getSelectedColumnCount();
		if (numSelectedRows > 0 && numSelectedCols > 0) {
			// 選択あり
			spos = getMinSelectionCellIndex();
			epos = getMaxSelectionCellIndex();
			//--- 連続性を検証
			int rows = epos.row - spos.row + 1;
			int cols = epos.column - spos.column + 1;
			if (rows != numSelectedRows || cols != numSelectedCols) {
				// 複数の選択領域には貼り付けできない
				showMessageBox(ERROR_CANNOT_PASTE_MULTIPLE_AREA);
				return;
			}
			//--- 選択が単一であれば、領域拡張
			if (spos.equals(epos)) {
				epos = getUpperBoundCellIndex();
			}
		}
		else {
			// 選択なし
			spos = getLeadSelectionCellIndex();
			if (spos == null || !spos.isValid()) {
				//--- カーソル位置不明の場合は、テーブル左上を貼り付け位置とする
				spos = getLowerBoundCellIndex();
			}
			epos = getUpperBoundCellIndex();
		}
		
		// 転送
		try {
			String data = (String)trans.getTransferData(DataFlavor.stringFlavor);
			if (!Strings.isNullOrEmpty(data)) {
				//--- クリップボードの内容を貼り付け
				CsvReader reader = new CsvReader(new BufferedReader(new StringReader(data)));
				try {
					reader.setDelimiterChar('\t');
					CsvReader.CsvRecord record;
					int irow = spos.row;
					int icol = spos.column;
					while ((record = reader.readRecord()) != null) {
						int numFields = record.getNumFields();
						if (numFields > 0) {
							int limit = Math.min(numFields, (epos.column-icol+1));
							for (int i = 0; i < limit; i++) {
								CsvReader.CsvField field = record.getField(i);
								updateValueAt((field==null ? null : field.getValue()), irow, icol+i);
							}
						} else {
							updateValueAt(null, irow, icol);
						}
						irow++;
						if (irow > epos.row) {
							break;
						}
					}
				}
				finally {
					reader.close();
				}
			}
		}
		catch (IOException ex) {
			AppLogger.error("Cannot to get Transferable data from System clipboard.", ex);
		}
		catch (UnsupportedFlavorException ex) {
			AppLogger.error("Unsupported String flavor in System clipboard data.", ex);
		}
	}

	/**
	 * 指定された文字列に、改行文字、タブが含まれている場合、ダブルクオートで囲む。
	 * ダブルクオートで囲む場合は、すでに存在するダブルクオートを二重にする。
	 * @param value	変換する文字列
	 * @return	変換後の文字列
	 */
	protected String enquoteCellString(String value) {
		if (Strings.isNullOrEmpty(value))
			return value;
		
		//--- check for enquote
		boolean hasDelimiterChar = false;
		int len = value.length();
		for (int i = 0; i < len; i++) {
			char c = value.charAt(i);
			if (c=='\t' || c=='\r' || c=='\n') {
				hasDelimiterChar = true;
				break;
			}
		}
		
		//--- enquote
		if (hasDelimiterChar) {
			value = value.replaceAll("\"", "\"\"");
			value = "\"" + value + "\"";
		}
		
		return value;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	/**
	 * セルの位置を示すインデックス。
	 * このクラスは、セルの位置となる行と列のインデックスを保持する。
	 * 無効な位置の場合、負の値がインデックスに格納される。
	 * <p><b>注：</b>
	 * <blockquote>
	 * このクラスは不変オブジェクトである。
	 * </blockquote>
	 */
	static public final class CellIndex implements Comparable<CellIndex>
	{
		public final int row;
		public final int column;
		public CellIndex(int rowIndex, int columnIndex) {
			if (rowIndex < 0 || columnIndex < 0) {
				this.row = -1;
				this.column = -1;
			} else {
				this.row = rowIndex;
				this.column = columnIndex;
			}
		}
		public boolean isValid() {
			return (row>=0 && column>=0);
		}
		public int compareTo(CellIndex aIndex) {
			int trow = this.row;
			int arow = aIndex.row;
			if (trow==arow) {
				int tcol = this.column;
				int acol = aIndex.column;
				return (tcol<acol ? -1 : (tcol==acol ? 0 : 1));
			}
			else {
				return (trow<arow ? -1 : 1);
			}
		}
		@Override
		public int hashCode() {
			return (row ^ column);
		}
		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			if (obj instanceof CellIndex) {
				CellIndex aLoc = (CellIndex)obj;
				return (aLoc.row==this.row && aLoc.column==this.column);
			}
			// not equal
			return false;
		}
		@Override
		public String toString() {
			return String.format("[%d:%d]", row, column);
		}
	}

	/**
	 * このテーブルへのフォーカス設定要求を処理する Swingスレッド用 Runnable クラス
	 */
	class FocusRequester implements Runnable {
		public void run() {
			if (!SpreadSheetTable.this.requestFocusInWindow()) {
				SpreadSheetTable.this.requestFocus();
			}
		}
	}

	/**
	 * このテーブルの編集アクションハンドラ。
	 * カット、コピー、ペースト、削除のキーストロークのトリガによって
	 * 実行される。
	 */
	static class EditActionHandler extends AbstractAction {
		public EditActionHandler(String commandKey, String name) {
			super(name);
			putValue(ACTION_COMMAND_KEY, commandKey);
		}

		public void actionPerformed(ActionEvent e) {
//			AppLogger.debug("SpreadSheetTable.EditActionHandler#actionPerformed(" + e.toString() + ")");
			Object srcComp = e.getSource();
			if (srcComp instanceof SpreadSheetTable) {
				SpreadSheetTable srcTable = (SpreadSheetTable)srcComp;
				srcTable.onEditActionPerformed(e.getActionCommand());
			}
		}
	}

	/**
	 * メニューアクセラレータキー用のアクションハンドラ。
	 * このクラスは、JTableのキーストロークマッピング(InputMap)を変更するために
	 * インスタンス化され、アクションマップでこのインスタンスを取得した場合は、
	 * <code>JTable#processKeyBinding()</code> を呼び出さないように処理する。
	 * これは、JTable標準のキーストロークマッピングに処理されないことと、
	 * セルの自動編集開始が有効な場合にアクセラレータが処理されてもセルエディタが
	 * 起動してしまう問題を回避するためのもの。
	 * <p>このクラスはダミーインスタンスとなり、このクラスインスタンスが
	 * 何らかのアクションを実行することはない。
	 */
	static class MenuAcceleratorActionHandler extends AbstractAction {
		public MenuAcceleratorActionHandler() {
			super(MenuAccelerationActionName);
		}
		
		public void actionPerformed(ActionEvent e) {}	// 処理しない
	}

	/**
	 * 行ヘッダーのデータモデル
	 * @version 3.2.0
	 * @since 1.10
	 */
	static public class RowHeaderModel extends AbstractListModel {
		private int rowSize = 0;

		public int getSize() {
			return rowSize;
		}

		public Object getElementAt(int index) {
			if (index < 0)
				throw new IndexOutOfBoundsException("index(" + index + ")<0");
			else if (index >= rowSize)
				throw new IndexOutOfBoundsException("index(" + index + ")>=" + rowSize);
			return (NumberFormat.getNumberInstance().format(index+1));
		}
		
		public void setSize(int newSize) {
			if (newSize < 0)
				throw new IllegalArgumentException("newSize(" + newSize + ")<0");
			int oldSize = rowSize;
			rowSize = newSize;

			if (oldSize > newSize) {
				fireIntervalRemoved(this, newSize, oldSize-1);
			}
			else if (oldSize < newSize) {
				fireIntervalAdded(this, oldSize, newSize-1);
			}
		}
		
		public void removeElementAt(int index) {
			if (index < 0)
				throw new IndexOutOfBoundsException("index(" + index + ")<0");
			else if (index >= rowSize)
				throw new IndexOutOfBoundsException("index(" + index + ")>=" + rowSize);
			rowSize--;
			fireIntervalRemoved(this, index, index);
		}
		
		public void insertElementAt(int index) {
			if (index < 0)
				throw new IndexOutOfBoundsException("index(" + index + ")<0");
			else if (index > rowSize)
				throw new IndexOutOfBoundsException("index(" + index + ")>" + rowSize);
			rowSize++;
			fireIntervalAdded(this, index, index);
		}
		
		public void addElement() {
			int index = rowSize;
			rowSize++;
			fireIntervalAdded(this, index, index);
		}
		
		public void removeAllElements() {
			int index1 = rowSize - 1;
			rowSize = 0;
			if (index1 >= 0) {
				fireIntervalRemoved(this, 0, index1);
			}
		}
		
		public void addElements(int size) {
			int appendSize = Math.min(size, (Integer.MAX_VALUE - rowSize));
			if (appendSize > 0) {
				int oldSize = rowSize;
				rowSize += appendSize;
				fireIntervalAdded(this, oldSize, rowSize-1);
			}
		}
		
		public void insertElements(int index, int size) {
			if (index < 0)
				throw new IndexOutOfBoundsException("index(" + index + ")<0");
			else if (index > rowSize)
				throw new IndexOutOfBoundsException("index(" + index + ")>" + rowSize);
			int appendSize = Math.min(size, (Integer.MAX_VALUE - rowSize));
			if (appendSize > 0) {
				rowSize += appendSize;
				fireIntervalAdded(this, index, (index+appendSize-1));
			}
		}
		
		public void removeRange(int fromIndex, int toIndex) {
			if (fromIndex > toIndex)
				throw new IllegalArgumentException("fromIndex must be <= toIndex");
			if (fromIndex < 0)
				throw new IndexOutOfBoundsException("fromIndex(" + fromIndex + ")<0");
			if (toIndex >= rowSize)
				throw new IndexOutOfBoundsException("toIndex(" + toIndex + ")>=" + rowSize);
			
			int removeSize = toIndex - fromIndex + 1;
			rowSize -= removeSize;
			fireIntervalRemoved(this, fromIndex, toIndex);
		}
	}

	/**
	 * 選択状態を復元するための情報を保持する基本クラス。
	 */
	abstract class AbstractSelectionUndo extends AbstractUndoableEdit {
	}
	
	/**
	 * Undo実行時のみ設定される選択状態を保持するクラス。
	 */
	class SelectionForUndo extends AbstractSelectionUndo {
		public void undo() throws CannotUndoException {
			super.undo();
			try {
			}
			catch (Throwable ex) {
				throw new CannotUndoException();
			}
		}
		
		public void redo() throws CannotRedoException {
			super.redo();
		}
	}

	/**
	 * Redo実行時のみ設定される選択状態を保持するクラス。
	 */
	class SelectionForRedo extends AbstractSelectionUndo {
		public void undo() throws CannotUndoException {
			super.undo();
		}
		
		public void redo() throws CannotRedoException {
			super.redo();
			try {
			}
			catch (Throwable ex) {
				throw new CannotRedoException();
			}
		}
	}
}
