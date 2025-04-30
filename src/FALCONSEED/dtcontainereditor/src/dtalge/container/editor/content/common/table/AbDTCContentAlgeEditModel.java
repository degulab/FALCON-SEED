/*
 * @(#)AbDTCContentAlgeEditModel.java	1.1.0	2023/01/23
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.content.common.table;

import java.util.ArrayList;
import java.util.Objects;

import javax.swing.event.UndoableEditListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.undo.UndoableEdit;

import dtalge.container.editor.content.common.tree.DTCContentTransferTableData;
import ssac.aadl.module.swing.table.UndoableTableModel;
import ssac.util.Strings;

/**
 * 交換代数元やデータ代数元の編集用データモデルの基本実装。
 * データ行数の自動拡張機能を持ち、格納領域を持つデータ行数と、テーブルで表現可能な仮想行数を管理する。
 * データの性質上、列数の拡張はない。
 * 
 * @version 1.1.0
 * @since 1.1.0
 */
public abstract class AbDTCContentAlgeEditModel extends AbstractTableModel implements UndoableTableModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	
	/** 標準の仮想行数上限 **/
	static public final int	DEFAULT_LIMIT_ROW_COUNT	= Integer.MAX_VALUE;
	/** 標準の初期仮想行数 **/
	static public final int	DEFAULT_MIN_ROW_COUNT	= 100;
	/** 仮想行数の拡張単位行数 **/
	static public final int EXPAND_VIRTUAL_ROWS_STEPS	= 100;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** データ変更前にアクションを実行するためのイベントハンドラー **/
	protected IDTCContentAlgePreEditHandler	_preEditHandler;
	
	/** このモデルのデータが変更されている場合は {@code true} */
	private boolean		_modified;
	
	/** 最大許容行数 **/
	private final int			_limitRowCount;
	/** 最小行数(仮想行含む) **/
	private final int			_minRowCount;
	/** 固定列数 **/
	private final int			_columnCount;
	/** 仮想行数 **/
	protected int					_virtualRowCount;
	/** 行データの格納領域 **/
	protected ArrayList<Object[]>	_actualRows;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/**
	 * 指定されたパラメーターで、新しいインスタンスを生成する。
	 * @param limitRowCount		最大許容行数、負の値は 0 を指定されたものとみなす
	 * @param minRowCount		最小行数
	 * @param columnCount		固定列数
	 * @throws IllegalArgumentException	固定列数が 1 よりも小さい場合
	 */
	public AbDTCContentAlgeEditModel(int limitRowCount, int minRowCount, int columnCount) {
		if (columnCount < 1) throw new IllegalArgumentException("Columnt count is less than 1 : " + columnCount);
		_columnCount = columnCount;
		_limitRowCount = (limitRowCount < 0 ? 0 : limitRowCount);
		_minRowCount = Math.min(_limitRowCount, (minRowCount < 0 ? 0 : minRowCount));
		_virtualRowCount = _minRowCount;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public IDTCContentAlgePreEditHandler getPreEditHandler() {
		return _preEditHandler;
	}
	
	public void setPreEditHandler(IDTCContentAlgePreEditHandler handler) {
		_preEditHandler = handler;
	}
	
	/**
	 * 転送データの内容で、このデータモデルを初期化する。
	 * この操作は、内部的な複製作成であり、編集フラグはクリアされ、Undo も作成しない。
	 * なお、この操作では、データ変更に際したイベントハンドラーは呼び出されない。
	 * @param transferData	転送データ
	 */
	public void initByCopiedRows(DTCContentTransferTableData transferData) {
		int copiedRowCount = (transferData==null ? 0 : transferData.getRowCount());
		int actualRowCount = Math.min(getLimitRowCount(), copiedRowCount);
		if (actualRowCount <= 0) {
			// 内容をクリア
			_virtualRowCount = _minRowCount;
			_actualRows = null;
			_modified = false;
			return;
		}
		
		// データをコピー
		if (_actualRows == null) {
			_actualRows = new ArrayList<Object[]>(actualRowCount);
		} else {
			_actualRows.clear();
			_actualRows.ensureCapacity(actualRowCount);
		}
		Object[][] copiedRows = transferData.getCopiedRows();
		if (copiedRows != null) {
			// コピー済み行データから複製
			for (int rowIndex = 0; rowIndex < actualRowCount; rowIndex++) {
				Object[] srcRow = copiedRows[rowIndex];
				Object[] dstRow = createEmptyRowData();
				System.arraycopy(srcRow, 0, dstRow, 0, Math.min(srcRow.length, dstRow.length));
				_actualRows.add(dstRow);
			}
		}
		else if (transferData.getSourceDataModel() != null) {
			// テーブルモデルから行を複製
			AbDTCContentAlgeEditModel srcModel = transferData.getSourceDataModel();
			for (int rowIndex = 0; rowIndex < actualRowCount; rowIndex++) {
				Object[] srcRow = srcModel.getRowAt(rowIndex);
				Object[] dstRow = createEmptyRowData();
				System.arraycopy(srcRow, 0, dstRow, 0, Math.min(srcRow.length, dstRow.length));
				_actualRows.add(dstRow);
			}
		}
		_virtualRowCount = Math.max(actualRowCount, _minRowCount);
		_modified = false;
	}
	
	/**
	 * テーブルの行数が指定された行数に満たない場合のみ、
	 * テーブル行数を指定された行数に設定する。
	 * <p>このメソッドでは、特殊な編集操作オブジェクト(<code>UndoableEdit</code>) を
	 * 生成し、ここで追加された行は Undo/Redo 操作時のフォーカス対象とはならない。
	 * @return	追加された行数
	 */
	public int ensureMinimumRowCount() {
		int appendedRowCount = 0;
		
		if (_virtualRowCount < _minRowCount) {
			appendedRowCount = _minRowCount - _virtualRowCount;
			//--- update table view
			int firstRowIndex = _virtualRowCount;
			int lastRowIndex = _minRowCount - 1;
			if (_preEditHandler != null) {
				_preEditHandler.preInsertingRows(firstRowIndex, lastRowIndex);
			}
			_virtualRowCount = _minRowCount;
			fireTableRowsInserted(firstRowIndex, lastRowIndex);
			//--- fire undoable edit event
			//EditAdjustedInsertRowUndo undoable = new EditAdjustedInsertRowUndo(firstRowIndex, lastRowIndex);
			//fireUndoableEditUpdate(undoable);
		}
		
		return appendedRowCount;
	}
	
	/**
	 * 値列のインデックスを取得する。
	 * @return	値列のインデックス
	 */
	abstract public int getValueColumnIndex();
	
	/**
	 * 指定された列インデックスが値列のインデックスかを判定する。
	 * @param columnIndex	判定する列インデックス
	 * @return	値列のインデックスなら {@code true}
	 */
	abstract public boolean isValueColumn(int columnIndex);
	
	/**
	 * 基底の名前キーの列インデックスを取得する。
	 * @return	名前キーの列インデックス
	 */
	abstract public int getNameKeyColumnIndex();
	
	/**
	 * 指定された列インデックスが、基底キー文字列を入力可能な列のインデックスかを判定する。
	 * @param columnIndex	判定する列インデックス
	 * @return	基底キー文字列を入力可能な列のインデックスなら {@code true}
	 */
	abstract public boolean isBaseKeyStringColumnIndex(int columnIndex);

	/**
	 * このデータモデルの内容が変更されているかどうかを取得する。
	 * @return	このデータモデルの内容が変更されている場合は {@code true}
	 */
	public boolean isModified() {
		return _modified;
	}
	
	/**
	 * このデータモデルの内容が変更されたことを示すフラグを設定する。
	 * @param modified	変更されたことを示すなら {@code true}、変更されていないことを示すなら {@code false}
	 */
	public void setModifiedFlag(boolean modified) {
		_modified = modified;
	}
	
	/**
	 * このデータモデルの最大行数を取得する。
	 * この行数より多く行を生成することはできない。
	 * @return	最大許容行数
	 */
	public int getLimitRowCount() {
		return _limitRowCount;
	}
	
	/**
	 * このデータモデルの最小行数を取得する。
	 * @return	最小行数
	 */
	public int getMinimumRowCount() {
		return _minRowCount;
	}
	
	/**
	 * データ格納領域を持つ、実際の行数。
	 * @return	格納領域の存在する、実際の行数
	 */
	public int getActualRowCount() {
		return (_actualRows==null ? 0 : _actualRows.size());
	}
	
	/**
	 * このデータモデルの仮想行数。
	 * @return	仮想行数
	 */
	public int getVirtualRowCount() {
		return _virtualRowCount;
	}
	
	/**
	 * 文字列の配列から、行データを生成する。
	 * 不正な値のチェックも行う。
	 * @param srcData	行データのソースとなる文字列の配列
	 * @return	行データ
	 */
	abstract public Object[] createRowDataByStrings(String[] srcData);
	
	/**
	 * 指定された位置の行データを取得する。
	 * @param rowIndex	行インデックス
	 * @return	行データ
	 * @throws IndexOutOfBoundsException	行インデックスが範囲外の場合
	 */
	public Object[] getRowAt(int rowIndex) {
		validRowIndexRange(rowIndex);
		
		if (rowIndex < getActualRowCount()) {
			// 格納領域の行データ
			return _actualRows.get(rowIndex);
		}
		else {
			// 仮想行なので、新規作成
			return createEmptyRowData();
		}
	}

	//------------------------------------------------------------
	// Implement javax.swing.table.TableModel interfaces
	//------------------------------------------------------------
	
	/**
	 * 指定された位置のセルが編集可能かどうかを取得する。
	 * @param rowIndex		行インデックス
	 * @param columnIndex	列インデックス
	 * @return	セルが編集可能なら {@code true}
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}
	
	/**
	 * このデータモデルの仮想行数を取得する。
	 * このモデルが返却する行数は、データ格納領域を持つ行数以上となる。
	 * @return	データモデルの仮想行数
	 */
	@Override
	public int getRowCount() {
		return getVirtualRowCount();
	}
	
	/**
	 * データモデルの列数を取得する。
	 * @return	データモデルの列数
	 */
	@Override
	public int getColumnCount() {
		return _columnCount;
	}
	
	/**
	 * 指定された位置のセルの値を取得する。
	 * 格納領域を持たない行位置が指定された場合、このメソッドは {@code null} を返す。
	 * @param rowIndex		行インデックス
	 * @param columnIndex	列インデックス
	 * @return	指定されたセルの値、格納領域を持たないセルなら {@code null}
	 * @throws IndexOutOfBoundsException	行インデックスもしくは列インデックスが範囲外の場合
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex < 0 || rowIndex >= getRowCount())
			throw new IndexOutOfBoundsException("Row index is out of range : " + rowIndex);
		if (columnIndex < 0 || columnIndex >= getColumnCount())
			throw new IndexOutOfBoundsException("Column index is out of range : " + columnIndex);
		
		if (rowIndex < getActualRowCount()) {
			// actual row
			Object[] row = _actualRows.get(rowIndex);
			return row[columnIndex];
		}
		else {
			// virtual row
			return getDefaultCellValueAt(columnIndex);
		}
	}

	/**
	 * 指定された位置に、新しい行データを挿入する。
	 * 
	 * @param rowIndex	挿入位置を示す行インデックス、getRowCount() より大きい場合は getRowCount() と同じ
	 * @param rowData	新しい行データ、{@code null} を指定した場合は内容が空の行データを挿入
	 * @throws IndexOutOfBoundsException	指定された位置が負の値の場合
	 */
	public void insertRow(int rowIndex, Object[] rowData) {
		if (rowIndex < getRowCount()) {
			validRowIndexRange(rowIndex);
		} else {
			rowIndex = getRowCount();
		}
		
		// row data
		if (rowData == null) {
			// 内容が空の行データ
			rowData = createEmptyRowData();
		}
		
		// 行挿入
		if (_preEditHandler != null) {
			_preEditHandler.preInsertingRows(rowIndex, rowIndex);
		}
		++_virtualRowCount;
		if (rowIndex <= getActualRowCount()) {
			// 格納領域への挿入
			ArrayList<Object[]> rows = getActualRows();
			rows.add(rowIndex, rowData);
			updateNameKeyCellValueIfEmptyNameKey(rowData);	// 行全体の挿入イベントが発行されるので、判定不要
		}
		else {
			// 格納領域の拡張と追加
			ArrayList<Object[]> rows = filledActualRows(rowIndex);
			rows.add(rowData);
		}
		//--- update table view
		fireTableRowsInserted(rowIndex, rowIndex);
		//--- fire undoable edit event
		//EditInsertRowUndo undoable = new EditInsertRowUndo(rowIndex, cloneRowData(rowData));
		//fireUndoableEditUpdate(undoable);
	}

	/**
	 * 指定された行インデックスのデータを、テーブルデータから削除する。
	 * 
	 * @param rowIndex	削除対象の行インデックス
	 * @throws IndexOutOfBoundsException	指定された位置が無効だった場合
	 */
	public void removeRow(int rowIndex) {
		validRowIndexRange(rowIndex);
		
		if (rowIndex < getActualRowCount()) {
			// 格納領域から行を削除
			if (_preEditHandler != null) {
				_preEditHandler.preDeletingRows(rowIndex, rowIndex);
			}
			Object[] removed = _actualRows.remove(rowIndex);
			--_virtualRowCount;
			// update table view
			fireTableRowsDeleted(rowIndex, rowIndex);
			// fire undoable edit event
			// TODO: undo
		}
		else {
			// 仮想行を削除
			if (_preEditHandler != null) {
				_preEditHandler.preDeletingRows(rowIndex, rowIndex);
			}
			--_virtualRowCount;
			// update table view
			fireTableRowsDeleted(rowIndex, rowIndex);
			// fire undoable edit event
			// TODO: undo
		}
	}

	//------------------------------------------------------------
	// Implements ssac.aadl.module.swing.table.UndoableTableModel interfaces
	//------------------------------------------------------------

	/**
	 * 任意の変更を通知するアンドゥリスナーを追加する。
	 * {@link UndoableEdit} で実行される「元に戻す/再実行」操作は、
	 * 適切な <code>DocumentEvent</code> を発生させて、ビューをモデルと
	 * 同期させる。
	 * 
	 * @param listener	追加する {@link UndoableEditListener}
	 */
	public void addUndoableEditListener(UndoableEditListener listener) {
		listenerList.add(UndoableEditListener.class, listener);
	}

	/**
	 * アンドゥリスナーを削除する。
	 * 
	 * @param listener	削除する {@link UndoableEditListener}
	 */
	public void removeUndoableEditListener(UndoableEditListener listener) {
		listenerList.remove(UndoableEditListener.class, listener);
	}

	/**
	 * このモデルに登録された、すべての取り消し可能編集リスナーからなる配列を返す。
	 * 
	 * @return	このモデルの {@link UndoableEditListener} すべて。
	 * 			取り消し可能編集リスナーが登録されていない場合は空の配列。
	 * 
	 * @see #addUndoableEditListener(UndoableEditListener)
	 * @see #removeUndoableEditListener(UndoableEditListener)
	 */
	public UndoableEditListener[] getUndoableEditListeners() {
		return (UndoableEditListener[])listenerList.getListeners(UndoableEditListener.class);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/*---**
	protected void ensureVirtualRowCount(long minRowCount) {
		if (minRowCount >= _virtualRowCount) {
			long about = minRowCount % (long)EXPAND_VIRTUAL_ROWS_STEPS;
			long expectedRowCount = (long)minRowCount + (about==0 ? EXPAND_VIRTUAL_ROWS_STEPS : about);
			int oldVirtualRowCount = _virtualRowCount;
			_virtualRowCount = (int)Math.min(expectedRowCount, (long)_limitRowCount);
			fireTableRowsInserted(oldVirtualRowCount, _virtualRowCount-1);
		}
	}
	/*---*/
	
	/*
	 * 指定された行インデックスに対応する格納領域を準備する。
	 * すでに格納領域が存在する場合は、何もしない。
	 * @param rowIndex	行インデックス
	 * @return	格納領域が存在する場合は {@code true}、行インデックスが最大許容行数を超える場合は {@code false}
	 *
	protected boolean ensureRowIndex(int rowIndex) {
		if (rowIndex >= _limitRowCount) {
			return false;	// 最大許容行数オーバー
		}

		ensureVirtualRowCount(rowIndex+1);
		
		if (_actualRows == null) {
			// 格納領域の新規作成
			_actualRows = new ArrayList<Object[]>(rowIndex+1);
			for (int i = 0; i <= rowIndex; i++) {
				_actualRows.add(createEmptyRowData());
			}
		}
		else if (rowIndex >= _actualRows.size()) {
			// 格納領域の拡張
			_actualRows.ensureCapacity(rowIndex+1);
			for (int i = _actualRows.size(); i <= rowIndex; i++) {
				_actualRows.add(createEmptyRowData());
			}
		}
		// else: 格納領域作成済み
		return true;
	}
	/*---*/
	
	/**
	 * 指定された列インデックスに対応する、デフォルト値を取得する。
	 * 仮想行のセルの表示に利用される。
	 * @param columnIndex	列インデックス
	 * @return	列インデックスに対応するデフォルト値
	 */
	protected Object getDefaultCellValueAt(int columnIndex) {
		return null;
	}
	
	/**
	 * 指定された行データにおいて、名前キー以外に値が指定されており、名前キーが空の場合に、名前キーセルにエラーを設定する。
	 * テーブルモデルの変更イベントは発行しない。
	 * @param rowData	判定対象の行データ
	 * @return	名前キーのセル値が変更された場合は {@code true}
	 */
	protected boolean updateNameKeyCellValueIfEmptyNameKey(Object[] rowData) {
		if (rowData == null)
			return false;
		
		// 名前キーの値の判定
		int colNameKey = getNameKeyColumnIndex();
		if (colNameKey >= rowData.length) {
			return false;	// 名前キー列が行データに存在しないなら、何もしない
		}
		Object objNameKey = rowData[colNameKey];
		if (objNameKey instanceof DTCContentInvalidCellValue) {
			;	// 名前キーは空
		}
		else if (objNameKey==null || objNameKey.toString().isEmpty()) {
			;	// 名前キーは空
		}
		else {
			// 名前キーは空ではないので、変更不要
			return false;
		}
		
		// 名前キー以外の値の判定
		boolean otherColumnHasValue = false;
		for (int colIndex = 0; colIndex < rowData.length; colIndex++) {
			if (colNameKey == colIndex) {
				continue;	// 名前キーは判定から除外
			}
			else if (!isBaseKeyStringColumnIndex(colIndex) && !isValueColumn(colIndex)) {
				continue;	// 値を入力しない列は除外
			}
			//--- 判定
			if (rowData[colIndex] != null && !rowData[colIndex].toString().isEmpty()) {
				otherColumnHasValue = true;
			}
		}
		
		// 必要があれば、名前キーの値を更新
		if (objNameKey instanceof DTCContentInvalidCellValue) {
			if (otherColumnHasValue) {
				return false;	// すでにエラー情報が設定されているので、変更不要
			}
			else {
				// エラー情報を除去
				rowData[colNameKey] = "";
				return true;
			}
		}
		else if (otherColumnHasValue) {
			// エラー情報を新たに設定
			rowData[colNameKey] = DTCContentInvalidCellValue.EMPTY_NAME_CELL_VALUE;
			return true;
		}
		else {
			return false;	// 変更不要
		}
	}
	
	/**
	 * 指定された位置の、行データ格納領域の値を更新する。
	 * このメソッドは、当該領域のセルの値と <em>newValue</em> が等しくない場合のみ、値を置き換える。
	 * なお、このメソッドでは行データ格納領域の拡張は行わない。
	 * @param newValue		新しい値
	 * @param rowIndex		行インデックス
	 * @param columnIndex	列インデックス
	 * @return	値が更新された場合は {@code true}
	 * @throws IndexOutOfBoundsException	指定された位置が行データ格納領域の範囲外の場合
	 */
	protected boolean updateActualCellValueAt(Object newValue, int rowIndex, int columnIndex) {
		if (_actualRows == null) throw new IndexOutOfBoundsException("Actual row index is out of range: " + rowIndex);
		Object[] rowData = _actualRows.get(rowIndex);
		if (!Objects.equals(rowData[columnIndex], newValue)) {
			// modified
			if (_preEditHandler != null) {
				_preEditHandler.preModifyingCellValue(rowData[columnIndex], newValue, rowIndex, columnIndex);
			}
			rowData[columnIndex] = newValue;
			setModifiedFlag(true);
			fireTableCellUpdated(rowIndex, columnIndex);
			//--- 名前キーの判定
			if (updateNameKeyCellValueIfEmptyNameKey(rowData)) {
				//--- 他の列の値更新イベント発行
				fireTableCellUpdated(rowIndex, getNameKeyColumnIndex());
			}
			return true;
		}
		else {
			// no changes
			return false;
		}
	}
	
	/**
	 * 指定された基底キー文字列が省略を表す文字列や {@code null} の場合、空文字列に変換する。
	 * @param keystr	変換する文字列
	 * @return	変換後の文字列
	 */
	abstract protected String ensureRawBaseKeyString(String keystr);
	
	/**
	 * 代数基底キーとして不正な文字の配列を返す。
	 * @return	代数基底キーとして不正な文字の配列
	 */
	abstract protected char[] getIllegalBaseKeyChars();
	
	/**
	 * 指定された文字が代数基底キーとして正当な文字かどうかを判定する。
	 * @param ch	判定する文字
	 * @return	正当なら {@code true}
	 */
	protected boolean isValidBaseKeyCharacter(char ch) {
		char[] illegalChars = getIllegalBaseKeyChars();
		for (int i = 0; i < illegalChars.length; i++) {
			if (illegalChars[i] == ch) {
				// invalid character for base key
				return false;
			}
		}
		// valid
		return true;
	}
	
	/**
	 * 指定された文字列から、代数基底キーとして不正な文字を除去する。
	 * <em>value</em> に {@code null} を指定した場合、このメソッドは {@code null} を返す。
	 * @param value	対象の文字列
	 * @return	代数基底キーの不正文字を除外した文字列、不正な文字が含まれていない場合は <em>value</em> に指定されたインスタンス
	 */
	protected String removeInvalidBaseKeyCharacters(String value) {
		if (value != null && !value.isEmpty()) {
			// 不正文字の検索
			int idxInvalid = (-1);
			int strlen = value.length();
			for (int i = 0; i < strlen; i++) {
				char ch = value.charAt(i);
				if (!isValidBaseKeyCharacter(ch)) {
					idxInvalid = i;
					break;
				}
			}
			// 不正文字の除去
			if (idxInvalid >= 0) {
				StringBuilder strbuf = Strings.getThreadLocalStringBuilder();
				strbuf.setLength(0);
				strbuf.append(value, 0, idxInvalid);
				for (int i = idxInvalid + 1; i < strlen; i++) {
					char ch = value.charAt(i);
					if (isValidBaseKeyCharacter(ch)) {
						strbuf.append(ch);
					}
				}
				value = strbuf.toString();
			}
		}
		return value;
	}
	
	/**
	 * 内容が空の行データを生成する。
	 * @return	行データ
	 */
	protected Object[] createEmptyRowData() {
		return new Object[_columnCount];
	}
	
	/**
	 * 指定された文字列が、{@code Boolean} を表す文字列として適切かどうかを判定する。
	 * このメソッドでは、空文字、{@code null}、{@code "true"}、{@code "false"} のいずれかであれば、適切とみなす。
	 * なお、大文字小文字は区別しない。
	 * @param strValue	判定する文字列
	 * @return	適切なら {@code true}
	 */
	protected boolean isValidBooleanString(String strValue) {
		if (strValue == null || strValue.isEmpty())
			return true;
		
		if ("true".equalsIgnoreCase(strValue)) {
			return true;
		}
		else if ("false".equalsIgnoreCase(strValue)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/*
	protected Boolean convertToBooleanValue(Object value) {
		if (value == null)
			return null;
		
		if (value instanceof Boolean) {
			return (Boolean)value;
		}
		else if (value instanceof BigDecimal) {
			BigDecimal d = (BigDecimal)value;
			if (BigDecimal.ZERO.compareTo(d) == 0) {
				return Boolean.FALSE;
			} else {
				return Boolean.TRUE;
			}
		}
		else {
			// String
			String strValue = value.toString();
			if (strValue.isEmpty())
				return null;
			
			if ("true".equalsIgnoreCase(strValue)) {
				return Boolean.TRUE;
			}
			else if ("false".equalsIgnoreCase(strValue)) {
				return Boolean.FALSE;
			}
			else {
				return Boolean.FALSE;
			}
		}
	}
	*/

	/**
	 * 仮想行も含めた、行インデックスの有効範囲をチェックする。指定された有効範囲外の場合は、例外をスローする。
	 * チェックでは、<code>0 &lt;= index &lt; getRowCount()</code> を正当とする。
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
	 * 格納領域に限定した、行インデックスの有効範囲をチェックする。指定された有効範囲外の場合は、例外をスローする。
	 * チェックでは、<code>0 &lt;= index &lt; getActualRowCount()</code> を正当とする。
	 * @param index	行インデックス
	 * @throws IndexOutOfBoundsException	指定されたインデックスが範囲外の場合
	 */
	protected final void validActualRowIndexRange(final int index) {
		if (index < 0)
			throw new IndexOutOfBoundsException("Actual row index out of range : index(" + index + ")<0");
		if (index >= getActualRowCount())
			throw new IndexOutOfBoundsException("Actual row index out of range : index(" + index + ")>=" + getActualRowCount());
	}
	
	/**
	 * 列インデックスの有効範囲をチェックする。指定された有効範囲外の場合は、例外をスローする。
	 * チェックでは、<code>0 &lt;= index &lt; getColumnCount()</code> を正当とする。
	 * @param index	列インデックス
	 * @throws IndexOutOfBoundsException	指定されたインデックスが範囲外の場合
	 */
	protected final void validColumnIndexRange(final int index) {
		if (index < 0)
			throw new IndexOutOfBoundsException("Column index out of range : index(" + index + ")<0");
		if (index >= getColumnCount())
			throw new IndexOutOfBoundsException("Column index out of range : index(" + index + ")>=" + getColumnCount());
	}
	
	/**
	 * 行データ格納領域を取得する。
	 * 格納領域が作成されていない場合、標準の要領で生成する。
	 * @return	行データ格納領域のインスタンス
	 */
	protected ArrayList<Object[]> getActualRows() {
		if (_actualRows == null) {
			_actualRows = new ArrayList<>();
		}
		return _actualRows;
	}
	
	/**
	 * <em>minCapacity</em> の容量を持つ、行データ格納領域を生成もしくは拡張する。
	 * @param minCapacity	要求する容量
	 * @return	行データ格納領域のインスタンス
	 * @throws IllegalArgumentException	<em>minCapacity</em> が負の値の場合
	 */
	protected ArrayList<Object[]> getActualRows(int minCapacity) {
		if (_actualRows == null) {
			_actualRows = new ArrayList<>(minCapacity);
		} else {
			_actualRows.ensureCapacity(minCapacity);
		}
		return _actualRows;
	}
	
	/**
	 * <em>rowCount</em> の行データ格納領域行数となるよう、内容が空の行データを終端に追加する。
	 * 格納領域の行数が <em>rowCount</em> 以上の場合、このメソッドは何もしない。
	 * @param rowCount	目標の格納領域の行数
	 * @return	行データ格納領域のインスタンス
	 */
	protected ArrayList<Object[]> filledActualRows(int rowCount) {
		ArrayList<Object[]> rows = getActualRows(rowCount);
		if (rowCount > rows.size()) {
			rowCount -= rows.size();
			for (int cnt = 0; cnt < rowCount; cnt++) {
				rows.add(createEmptyRowData());
			}
		}
		return rows;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

//	/**
//	 * セルデータの変更を保持するクラス。
//	 * このクラスは、<code>UndoableEdit</code> インタフェースを実装する。 
//	 * @version 1.1.0
//	 * @since 1.1.0
//	 */
//	class DTCContentTableEditCellUndo extends AbstractUndoableEdit {
//		private static final long serialVersionUID = 1L;
//		
//		protected final int rowIndex;
//		protected final int columnIndex;
//		protected final Object oldCellData;
//		protected final Object newCellData;
//		
//		protected DTCContentTableEditCellUndo(int rowIndex, int columnIndex, Object oldCellData, Object newCellData) {
//			super();
//			this.rowIndex = rowIndex;
//			this.columnIndex = columnIndex;
//			this.oldCellData = oldCellData;
//			this.newCellData = newCellData;
//		}
//		
//		public void undo() throws CannotUndoException {
//			super.undo();
//			try {
//				// check
//				validActualRowIndexRange(rowIndex);
//				validColumnIndexRange(columnIndex);
//				// update
//				if (setElementAt(oldCellData, rowIndex, columnIndex)) {
//					fireTableCellUpdated(rowIndex, columnIndex);
//				}
//			}
//			catch (Throwable ex) {
//				throw new CannotUndoException();
//			}
//		}
//		
//		public void redo() throws CannotRedoException {
//			super.redo();
//			try {
//				// check
//				validActualRowIndexRange(rowIndex);
//				validColumnIndexRange(columnIndex);
//				// update
//				if (setElementAt(newCellData, rowIndex, columnIndex)) {
//					fireTableCellUpdated(rowIndex, columnIndex);
//				}
//			}
//			catch (Throwable ex) {
//				throw new CannotRedoException();
//			}
//		}
//	}
//
//	/**
//	 * 行数調整用の行データ追加を保持するクラス。
//	 * このインスタンスは、<code>{@link SpreadSheetModel}</code> が呼び出された場合にのみ
//	 * 生成される。
//	 * 
//	 * このクラスは、<code>UndoableEdit</code> インタフェースを実装する。
//	 * @version 1.1.0
//	 * @since 1.1.0
//	 */
//	class DTCContentTableEditAdjustedInsertRowUndo extends AbstractUndoableEdit {
//		private static final long serialVersionUID = 1L;
//		
//		protected final int firstRowIndex;
//		protected final int lastRowIndex;
//		
//		protected DTCContentTableEditAdjustedInsertRowUndo(int firstRowIndex, int lastRowIndex) {
//			super();
//			if (firstRowIndex > lastRowIndex)
//				throw new IllegalArgumentException("Illegal row range : firstRowIndex(" + firstRowIndex + ")>lastRowIndex(" + lastRowIndex + ")");
//			this.firstRowIndex = firstRowIndex;
//			this.lastRowIndex = lastRowIndex;
//		}
//		
//		public void undo() throws CannotUndoException {
//			//--- 行インデックスの行データ削除
//			super.undo();
//			try {
//				// check
//				validRowIndexRange(firstRowIndex);
//				validRowIndexRange(lastRowIndex);
//				// remove row
//				for (int row = lastRowIndex; row >= firstRowIndex; row--) {
//					removeRowAt(row);
//				}
//				// update
//				fireTableRowsDeleted(firstRowIndex, lastRowIndex);
//			}
//			catch (Throwable ex) {
//				throw new CannotUndoException();
//			}
//		}
//		
//		public void redo() throws CannotUndoException {
//			//--- 行インデックスの行データ追加
//			super.redo();
//			try {
//				int numRows = lastRowIndex - firstRowIndex + 1;
//				if (data.size() > firstRowIndex) {
//					data.addAll(firstRowIndex, Arrays.asList(new RowDataModel[numRows]));
//				}
//				rowSize += numRows;
//				// update
//				fireTableRowsInserted(firstRowIndex, lastRowIndex);
//			}
//			catch (Throwable ex) {
//				throw new CannotRedoException();
//			}
//		}
//	}
//
//	/**
//	 * 行データの追加を保持するクラス。
//	 * このクラスは、<code>UndoableEdit</code> インタフェースを実装する。
//	 * @version 1.1.0
//	 * @since 1.1.0
//	 */
//	class DTCContentTableEditInsertRowUndo extends AbstractUndoableEdit {
//		private static final long serialVersionUID = 1L;
//		
//		protected final int rowIndex;
//		protected final RowDataModel rowData;
//		
//		protected DTCContentTableEditInsertRowUndo(int rowIndex, RowDataModel rowData) {
//			super();
//			this.rowIndex = rowIndex;
//			this.rowData = rowData;
//		}
//		
//		public void undo() throws CannotUndoException {
//			//--- 行インデックスの行データ削除
//			super.undo();
//			try {
//				// check
//				validRowIndexRange(rowIndex);
//				// remove row
//				removeRowAt(rowIndex);
//				// update
//				fireTableRowsDeleted(rowIndex, rowIndex);
//			}
//			catch (Throwable ex) {
//				throw new CannotUndoException();
//			}
//		}
//		
//		public void redo() throws CannotRedoException {
//			//--- 行インデックスの行データ追加
//			super.redo();
//			try {
//				// check(現在の行数よりも大きい行インデックスはエラー)
//				if (rowIndex < 0)
//					throw new IndexOutOfBoundsException("Row index out of range : index(" + rowIndex + ")<0");
//				else if (rowIndex > rowSize)
//					throw new IndexOutOfBoundsException("Row index out of range : index(" + rowIndex + ")>" + rowSize);
//				// insert row
//				if (rowData != null && !rowData.isEmpty())
//					insertRowAt(rowIndex, createRowDataModel(rowData));
//				else
//					insertRowAt(rowIndex, null);
//				// update
//				fireTableRowsInserted(rowIndex, rowIndex);
//			}
//			catch (Throwable ex) {
//				throw new CannotRedoException();
//			}
//		}
//	}
//
//	/**
//	 * 行データの削除を保持するクラス。
//	 * このクラスは、<code>UndoableEdit</code> インタフェースを実装する。
//	 * @version 1.1.0
//	 * @since 1.1.0
//	 */
//	class DTCContentTableEditDeleteRowUndo extends AbstractUndoableEdit {
//		private static final long serialVersionUID = 1L;
//		
//		protected final int rowIndex;
//		protected final RowDataModel rowData;
//		
//		protected DTCContentTableEditDeleteRowUndo(int rowIndex, RowDataModel rowData) {
//			super();
//			this.rowIndex = rowIndex;
//			this.rowData = rowData;
//		}
//		
//		public void undo() throws CannotUndoException {
//			//--- 行インデックスの行データ追加
//			super.undo();
//			try {
//				// check(現在の行数よりも大きい行インデックスはエラー)
//				if (rowIndex < 0)
//					throw new IndexOutOfBoundsException("Row index out of range : index(" + rowIndex + ")<0");
//				else if (rowIndex > rowSize)
//					throw new IndexOutOfBoundsException("Row index out of range : index(" + rowIndex + ")>" + rowSize);
//				// insert row
//				if (rowData != null && !rowData.isEmpty())
//					insertRowAt(rowIndex, createRowDataModel(rowData));
//				else
//					insertRowAt(rowIndex, null);
//				// update
//				fireTableRowsInserted(rowIndex, rowIndex);
//			}
//			catch (Throwable ex) {
//				throw new CannotUndoException();
//			}
//		}
//		
//		public void redo() throws CannotRedoException {
//			//--- 行インデックスの行データ削除
//			super.redo();
//			try {
//				// check
//				validRowIndexRange(rowIndex);
//				// remove row
//				removeRowAt(rowIndex);
//				// update
//				fireTableRowsDeleted(rowIndex, rowIndex);
//			}
//			catch (Throwable ex) {
//				throw new CannotRedoException();
//			}
//		}
//	}
}
