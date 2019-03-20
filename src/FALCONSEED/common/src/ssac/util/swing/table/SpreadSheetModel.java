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
 *  Copyright 2007-2009  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)SpreadSheetModel.java	1.10	2009/01/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import ssac.util.Validations;

/**
 * スプレッドシートのテーブルモデル。
 * <p>
 * このモデルは編集可能なテーブルモデルであり、<code>Object</code> を
 * 行と列で保持するテーブルモデルとなる。
 * 基本的に、全てのセルを編集可能とし、行の追加、削除、列の追加、削除の
 * インタフェースも実装する。
 * 
 * @version 1.10	2009/01/20
 *
 * @since 1.10
 */
public class SpreadSheetModel extends AbstractTableModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final int INITIAL_ROW_CAPACITY = 10;
	static protected final int INITIAL_COL_CAPACITY = 10;
	
	static protected final int DEFAULT_ROW_COUNT = 65536;	// Like Excel2003
	static protected final int DEFAULT_COLUMN_COUNT = 256;	// Like Excel2003

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/**
	 * テーブルデータ
	 */
	protected final ArrayList<RowDataModel> data;
	/**
	 * カラム名のリスト
	 */
	protected ArrayList<Object> columnIdentifiers;
	/**
	 * 現在の行数
	 */
	private int rowSize;
	/**
	 * 現在のカラム数
	 */
	private int colSize;
	
	private volatile boolean		compoundEdits = false;
	private CompoundEdit			packEdits = null;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public SpreadSheetModel() {
		this(INITIAL_ROW_CAPACITY);
	}
	
	public SpreadSheetModel(int rowCapacity) {
		super();
		this.data = newRowsList(rowCapacity);
		this.rowSize = 0;
		this.colSize = 0;
	}
	
	public SpreadSheetModel(int rowSize, int columnSize) {
		super();
		if (rowSize < 0)
			throw new IllegalArgumentException("Illegal row count : " + rowSize);
		if (columnSize < 0)
			throw new IllegalArgumentException("Illegal column count : " + columnSize);
		this.data = newRowsList(rowSize);
		this.rowSize = rowSize;
		this.colSize = columnSize;
	}
	
	public SpreadSheetModel(Object[] columnNames, int rowSize) {
		this((columnNames == null ? null : Arrays.asList(columnNames)), rowSize);
	}
	
	public SpreadSheetModel(Collection<?> columnNames, int rowSize) {
		this(rowSize, (columnNames != null ? columnNames.size() : 0));
		if (columnNames != null) {
			this.columnIdentifiers = new ArrayList<Object>(columnNames);
		}
	}
	
	static public final SpreadSheetModel newDefaultSpreadSheetModel() {
		return new SpreadSheetModel(DEFAULT_ROW_COUNT, DEFAULT_COLUMN_COUNT);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * データテーブル内の行数を取得する。
	 * 
	 * @return 現在の行数
	 */
	public int getRowCount() {
		return rowSize;
	}

	/**
	 * データテーブル内の列数を取得する。
	 * 
	 * @return 現在の列数
	 */
	public int getColumnCount() {
		return colSize;
	}

	/**
	 * テーブルデータの有効最大行数を取得する。
	 * このメソッドは、有効なデータ(<tt>null</tt>ではないデータ)が格納されている
	 * 行の最大位置(最大インデックス)を検索し、その位置までの行数を返す。
	 * 
	 * @return	有効な行数
	 */
	public int getValidRowCount() {
		for (int r = Math.min(rowSize, data.size())-1; r >= 0; r--) {
			ArrayList<Object> row = data.get(r);
			if (row != null && !row.isEmpty()) {
				return (r+1);
			}
		}
		
		return 0;
	}

	/**
	 * 指定された行の有効最大列数を取得する。
	 * このメソッドは、有効なデータ(<tt>null</tt>ではないデータ)が格納されている
	 * 列の最大位置(最大インデックス)を検索し、その位置までの列数を返す。
	 * 
	 * @param rowIndex	行インデックス
	 * @return	有効な列数
	 */
	public int getValidColumnCount(int rowIndex) {
		if (rowIndex < Math.min(rowSize, data.size())) {
			ArrayList<Object> row = data.get(rowIndex);
			if (row != null) {
				return Math.min(colSize, row.size());
			}
		}
		
		return 0;
	}

	/**
	 * 指定された位置の列名を取得する。
	 * 
	 * @param columnIndex	列インデックス
	 * @return このモデルが保持する列名リストから参照した列名を返す。指定された列位置の列名が
	 * 			定義されていない場合は、自動的に列名を生成する。
	 */
	@Override
	public String getColumnName(int columnIndex) {
		Object cn = null;
		
		if (columnIdentifiers != null && columnIndex < columnIdentifiers.size()) {
			cn = columnIdentifiers.get(columnIndex);
		}
		
		return (cn == null ? super.getColumnName(columnIndex) : cn.toString());
	}

	/**
	 * このモデルに格納されているデータに関係なく、<tt>true</tt> を返す。
	 * @param rowIndex		行インデックス
	 * @param columnIndex	列インデックス
	 * @return <tt>true</tt>
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	/**
	 * 指定された位置のセルの値を取得する。セルの値が格納されていない
	 * 場合は <tt>null</tt> を返す。
	 * 
	 * @param rowIndex		行インデックス
	 * @param columnIndex	列インデックス
	 * @return セルの値
	 * 
	 * @throws IndexOutOfBoundsException	指定された位置が無効だった場合
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		// check
		validRowIndexRange(rowIndex);
		validColumnIndexRange(columnIndex);
		
		// get
		return getElementAt(rowIndex, columnIndex);
	}

	/**
	 * 指定された位置のセルに、新しい値を格納する。
	 * 
	 * @param rowIndex		行インデックス
	 * @param columnIndex	列インデックス
	 * 
	 * @throws IndexOutOfBoundsException	指定された位置が無効だった場合
	 */
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// check
		validRowIndexRange(rowIndex);
		validColumnIndexRange(columnIndex);
		
		// 直前の値を取得
		Object oldCellData = cloneElementAt(rowIndex, columnIndex);
		
		// update
		boolean isUpdate;
		if (isValidValue(aValue))
			isUpdate = storeElementAt(aValue, rowIndex, columnIndex);
		else
			isUpdate = deleteElementAt(rowIndex, columnIndex);
		
		if (isUpdate) {
			//--- update table view
			fireTableCellUpdated(rowIndex, columnIndex);
			//--- fire undoable edit event
			EditCellUndo undoable = new EditCellUndo(rowIndex, columnIndex, oldCellData, aValue);
			fireUndoableEditUpdate(undoable);
		}
	}
	
	public void setData(Object[][] dataList, Collection<?> columnNames) {
		data.clear();
		rowSize = 0;
		colSize = 0;
		
		if (dataList != null && dataList.length > 0) {
			rowSize = dataList.length;
			data.ensureCapacity(rowSize);
			for (Object[] row : dataList) {
				RowDataModel cl = null;
				if (row != null && row.length > 0) {
					cl = createRowDataModel(row);
					clearInvalidColumns(cl);
				}
				data.add(cl);
			}
			clearInvalidRows();
		}
		
		setColumnIdentifiers(columnNames);
		
		fireTableStructureChanged();
	}
	
	public void setData(List<List<?>> dataList, Collection<?> columnNames) {
		data.clear();
		rowSize = 0;
		colSize = 0;
		
		if (dataList != null && !dataList.isEmpty()) {
			rowSize = dataList.size();
			data.ensureCapacity(rowSize);
			for (List<?> row : dataList) {
				RowDataModel cl = null;
				if (row != null && !row.isEmpty()) {
					cl = createRowDataModel(row);
					clearInvalidColumns(cl);
				}
				data.add(cl);
			}
			clearInvalidRows();
		}
		
		setColumnIdentifiers(columnNames);
		
		fireTableStructureChanged();
	}
	
	protected void addColumn(Object columnName) {
		setColumnIdentifier(colSize, columnName);
		colSize++;

		fireTableStructureChanged();
	}
	
	protected void addColumn(Object columnName, ColumnDataModel columnData) {
		if (columnData != null && !columnData.isEmpty()) {
			if (columnData.size() > rowSize) {
				rowSize = columnData.size();
			}
			for (int ri = 0; ri < columnData.size(); ri++) {
				Object obj = columnData.get(ri);
				if (isValidValue(obj)) {
					storeElementAt(obj, ri, colSize);
				}
			}
			setColumnIdentifier(colSize, columnName);
			colSize++;
			
			fireTableStructureChanged();
		}
		else {
			addColumn(columnName);
		}
	}

	/**
	 * 指定された位置の行データを取得する。
	 * @param rowIndex	取得する行の行インデックス
	 * @return	取得した行データ
	 * @throws IndexOutOfBoundsException	指定されたインデックスが範囲外の場合
	 */
	public RowDataModel getRow(int rowIndex) {
		validRowIndexRange(rowIndex);
		
		if (rowIndex < data.size()) {
			return data.get(rowIndex);
		} else {
			return null;
		}
	}

	/**
	 * テーブルの行数を設定する。
	 * 現在の行数が指定された行数よりも多い場合、行の終端から指定の行数になるように
	 * 行を削除する。
	 * 現在の行数が指定された行数よりも小さい場合、指定の行数になるまで空行を
	 * 行の終端に追加する。
	 * 
	 * @param rowCount	設定する行数
	 */
	public void setRows(int rowCount) {
		if (rowCount < 0)
			rowCount = 0;
		int oldSize = getRowCount();
		if (oldSize == rowCount) {
			return;
		}
		
		if (rowCount < oldSize) {
			rowSize = rowCount;
			startCompoundUndoableEdit();
			for (int row = oldSize-1; row >= rowCount; row--) {
				RowDataModel removed = null;
				if (row < data.size())
					removed = data.remove(row);
				EditDeleteRowUndo undoable = new EditDeleteRowUndo(row, cloneRowData(removed));
				fireUndoableEditUpdate(undoable);
			}
			endCompoundUndoableEdit();
			clearInvalidRows();
			//--- update table view
			fireTableRowsDeleted(rowCount, oldSize-1);
		}
		else {
			rowSize = rowCount;
			//--- update table view
			fireTableRowsInserted(oldSize, rowCount-1);
			//--- fire undoable edit event
			startCompoundUndoableEdit();
			for (int row = oldSize; row < rowCount; row++) {
				EditInsertRowUndo undoable = new EditInsertRowUndo(row, null);
				fireUndoableEditUpdate(undoable);
			}
			endCompoundUndoableEdit();
		}
	}

	/**
	 * テーブルの行数が指定された行数に満たない場合のみ、
	 * テーブル行数を指定された行数に設定する。
	 * <p>このメソッドでは、特殊な編集操作オブジェクト(<code>UndoableEdit</code>) を
	 * 生成し、ここで追加された行は Undo/Redo 操作時のフォーカス対象とはならない。
	 * 
	 * @param minimumRowCount	調整する最小行数
	 * @return この操作で追加された行数を返す。
	 */
	public int adjustMinimumRows(int minimumRowCount) {
		int appendedRowCount = 0;
		
		if (rowSize < minimumRowCount) {
			appendedRowCount = minimumRowCount - rowSize;
			int firstRowIndex = rowSize;
			int lastRowIndex  = minimumRowCount - 1;
			rowSize = minimumRowCount;
			//--- update table view
			fireTableRowsInserted(firstRowIndex, lastRowIndex);
			//--- fire undoable edit event
			EditAdjustedInsertRowUndo undoable = new EditAdjustedInsertRowUndo(firstRowIndex, lastRowIndex);
			fireUndoableEditUpdate(undoable);
		}
		
		return appendedRowCount;
	}

	/**
	 * 行の終端に、新しい行データを追加する。
	 * 
	 * @param rowData	新しい行データ
	 */
	public void addRow(RowDataModel rowData) {
		int rowIndex = rowSize;
		insertRowAt(rowIndex, rowData);
		//--- update table view
		fireTableRowsInserted(rowIndex, rowIndex);
		//--- fire undoable edit event
		EditInsertRowUndo undoable = new EditInsertRowUndo(rowIndex, cloneRowData(rowData));
		fireUndoableEditUpdate(undoable);
	}

	/**
	 * 指定された位置に、新しい行データを挿入する。
	 * ここで指定する位置は、有効な行インデックスでなければならない。
	 * 
	 * @param rowIndex	挿入位置を示す行インデックス
	 * @param rowData	新しい行データ
	 * @throws IndexOutOfBoundsException	指定された位置が無効だった場合
	 */
	public void insertRow(int rowIndex, RowDataModel rowData) {
		validRowIndexRange(rowIndex);
		insertRowAt(rowIndex, rowData);
		//--- update table view
		fireTableRowsInserted(rowIndex, rowIndex);
		//--- fire undoable edit event
		EditInsertRowUndo undoable = new EditInsertRowUndo(rowIndex, cloneRowData(rowData));
		fireUndoableEditUpdate(undoable);
	}

	/**
	 * 指定された行インデックスのデータを、テーブルデータから削除する。
	 * 
	 * @param rowIndex	削除対象の行インデックス
	 * @throws IndexOutOfBoundsException	指定された位置が無効だった場合
	 */
	public void removeRow(int rowIndex) {
		validRowIndexRange(rowIndex);
		RowDataModel removed = removeRowAt(rowIndex);
		//--- update table view
		fireTableRowsDeleted(rowIndex, rowIndex);
		//--- fire undoable edit event
		EditDeleteRowUndo undoable = new EditDeleteRowUndo(rowIndex, cloneRowData(removed));
		fireUndoableEditUpdate(undoable);
	}

	/**
	 * 任意の変更を通知するアンドゥリスナーを追加する。
	 * <code>UndoableEdit</code> で実行される「元に戻す/再実行」操作は、
	 * 適切な <code>DocumentEvent</code> を発生させて、ビューをモデルと
	 * 同期させる。
	 * 
	 * @param listener	追加する <code>UndoableEditListener</code>
	 */
	public void addUndoableEditListener(UndoableEditListener listener) {
		listenerList.add(UndoableEditListener.class, listener);
	}

	/**
	 * アンドゥリスナーを削除する。
	 * 
	 * @param listener	削除する <code>UndoableEditListener</code>
	 */
	public void removeUndoableEditListener(UndoableEditListener listener) {
		listenerList.remove(UndoableEditListener.class, listener);
	}

	/**
	 * このモデルに登録された、すべての取り消し可能編集リスナーからなる配列を返す。
	 * 
	 * @return	このモデルの <code>UndoableEditListener</code> すべて。
	 * 			取り消し可能編集リスナーが登録されていない場合は空の配列。
	 * 
	 * @see #addUndoableEditListener(UndoableEditListener)
	 * @see #removeUndoableEditListener(UndoableEditListener)
	 */
	public UndoableEditListener[] getUndoableEditListeners() {
		return (UndoableEditListener[])listenerList.getListeners(UndoableEditListener.class);
	}

	/**
	 * テーブルエディタのUndo可能編集内容の集約を開始する。
	 * Undo可能編集内容の集約は、複数の編集操作を１回のUndoで元に戻すため、
	 * 複数の編集内容をコンテナに集約するための操作となる。
	 */
	public void startCompoundUndoableEdit() {
		compoundEdits = true;
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
		boolean isCompound = compoundEdits;
		CompoundEdit edits = packEdits;
		packEdits = null;
		compoundEdits = false;
		
		if (isCompound) {
			if (edits != null) {
				edits.end();
				fireUndoableEditUpdate(new UndoableEditEvent(this, edits));
			}
		}
	}

	/**
	 * このモデルの編集操作としてUndo可能編集内容を登録する。
	 * このメソッドでは、指定されたオブジェクトで取り消し可能編集リスナーを呼び出す。
	 * 
	 * @param edit	取り消し可能な編集操作
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public void postUndoableEdit(UndoableEdit edit) {
		fireUndoableEditUpdate(Validations.validNotNull(edit));
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 行インデックスの有効範囲をチェックする。指定された有効範囲外の場合は、例外をスローする。
	 * チェックでは、<code>0 &lt;= index &lt; rowSize</code> を正当とする。
	 * @param index	行インデックス
	 * @throws IndexOutOfBoundsException	指定されたインデックスが範囲外の場合
	 */
	protected final void validRowIndexRange(final int index) {
		if (index < 0)
			throw new IndexOutOfBoundsException("Row index out of range : index(" + index + ")<0");
		if (index >= rowSize)
			throw new IndexOutOfBoundsException("Row index out of range : index(" + index + ")>=" + rowSize);
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
		if (index >= colSize)
			throw new IndexOutOfBoundsException("Column index out of range : index(" + index + ")>=" + colSize);
	}

	/**
	 * 指定された容量の行データ格納用リストを生成する。容量が <code>{@link #INITIAL_ROW_CAPACITY}</code> を
	 * 下回る場合は、<code>{@link #INITIAL_ROW_CAPACITY}</code> とする。
	 * @param rowCapacity	行格納用リストの初期容量
	 * @return	生成されたリストのインスタンス
	 */
	static protected final ArrayList<RowDataModel> newRowsList(final int rowCapacity) {
		return new ArrayList<RowDataModel>(Math.max(INITIAL_ROW_CAPACITY, rowCapacity));
	}

	/**
	 * 指定されたリストのサイズが、<code>maxBounds</code> 以下のサイズの場合、
	 * <code>maxBounds+1</code> のサイズとなるまで、<tt>null</tt> でパディングする。
	 * @param list		リスト
	 * @param maxBounds	最大インデックス
	 */
	static protected final void ensureElement(ArrayList<?> list, int maxBounds) {
		list.ensureCapacity(maxBounds+1);
		while (maxBounds >= list.size()) {
			list.add(null);
		}
	}
	
	protected void setColumnIdentifier(int columnIndex, Object columnName) {
		if (columnName == null)
			return;
		
		if (this.columnIdentifiers == null) {
			this.columnIdentifiers = new ArrayList<Object>(columnIndex+1);
		}
		ensureElement(this.columnIdentifiers, columnIndex);
		this.columnIdentifiers.set(columnIndex, columnName);
	}
	
	protected void setColumnIdentifiers(ArrayList<Object> columnNames, int columnSize) {
		if (columnSize < 1) {
			throw new IllegalArgumentException("columnSize : " + columnSize);
		}
		this.colSize = columnSize;
		if (columnNames != null && !columnNames.isEmpty()) {
			this.columnIdentifiers = columnNames;
		} else {
			this.columnIdentifiers = null;
		}
	}

	protected void setColumnIdentifiers(Collection<?> columnNames) {
		if (columnNames != null && !columnNames.isEmpty()) {
			this.columnIdentifiers = new ArrayList<Object>(columnNames);
		} else {
			this.columnIdentifiers = null;
		}
	}
	
	protected void setColumnIdentifiers(Object[] columnNames) {
		if (columnNames != null && columnNames.length > 0) {
			this.columnIdentifiers = new ArrayList<Object>(Arrays.asList(columnNames));
		} else {
			this.columnIdentifiers = null;
		}
	}
	
	protected boolean isValidValue(Object value) {
		return (value != null);
	}
	
	protected final void clearInvalidColumns(final ArrayList<Object> colList) {
		for (int ci = colList.size()-1; ci >= 0; ci--) {
			if (isValidValue(colList.get(ci))) {
				//--- 値が有効なら、そこが行の最大有効列
				break;
			}
			//--- 行内の無効な終端列は除去
			colList.remove(ci);
		}
	}
	
	protected final void clearInvalidRows() {
		for (int ri = data.size()-1; ri >= 0; ri--) {
			ArrayList<Object> row = data.get(ri);
			if (row != null && !row.isEmpty()) {
				//--- 行が有効なら、そこがデータの最大有効行
				break;
			}
			//--- データ内の無効な終端行は除去
			data.remove(ri);
		}
	}
	
	/**
	 * 指定された位置のセルデータのクローンを生成する。
	 * ここで生成されたデータは、Undo/Redo バッファに登録されるデータとなる為、
	 * セルの状態を復元可能なデータのディープコピーでなければならない。
	 * 
	 * @param rowIndex		行インデックス
	 * @param columnIndex	列インデックス
	 * @return	指定された位置のセルデータのディープコピー
	 */
	protected Object cloneElementAt(int rowIndex, int columnIndex) {
		// 復元可能なデータとして、文字列に変換する。
		Object obj = getElementAt(rowIndex, columnIndex);
		if (isValidValue(obj))
			return obj.toString();
		else
			return null;
	}

	/**
	 * 指定された位置の行データのクローンを生成する。
	 * ここで生成されたデータは、Undo/Redo バッファに登録されるデータとなる為、
	 * セルの状態を復元可能なデータのディープコピーでなければならない。
	 * この行データは、すべて文字列データとして格納される。
	 * 
	 * @param rowData	クローンを生成するデータ
	 * @return	指定された位置の行データのディープコピー
	 */
	protected RowDataModel cloneRowData(RowDataModel rowData) {
		// 復元可能なデータとして、文字列に変換する。
		RowDataModel newRow = null;
		if (rowData != null && !rowData.isEmpty()) {
			newRow = new RowDataModel(rowData.size());
			ensureElement(newRow, rowData.size()-1);
			for (int i = 0; i < rowData.size(); i++) {
				Object obj = rowData.get(i);
				String value = (obj==null ? null : obj.toString());
				newRow.set(i, value);
			}
		}
		return newRow;
	}

	/**
	 * 指定された位置のセルから値を取得する。その位置に値が格納されていない場合は、<tt>null</tt> を返す。
	 * 
	 * @param rowIndex		行インデックス
	 * @param columnIndex	列インデックス
	 * @return	値
	 */
	protected Object getElementAt(int rowIndex, int columnIndex) {
		Object value = null;
		if (rowIndex < data.size()) {
			List<Object> row = data.get(rowIndex);
			if (row != null && columnIndex < row.size()) {
				value = row.get(columnIndex);
			}
		}
		return value;
	}

	/**
	 * 指定された位置のセルデータを、指定されたデータで置き換える。
	 * 
	 * @param aValue		新たに設定するセルデータ
	 * @param rowIndex		行インデックス
	 * @param columnIndex	列インデックス
	 * @return	データが更新された場合は <tt>true</tt>
	 */
	protected boolean setElementAt(Object aValue, int rowIndex, int columnIndex) {
		if (isValidValue(aValue))
			return storeElementAt(aValue, rowIndex, columnIndex);
		else
			return deleteElementAt(rowIndex, columnIndex);
	}

	/**
	 * 指定された位置のセルデータを削除する。このメソッドは、指定位置のセルデータを削除するが、
	 * テーブルのサイズは変更しない。
	 * 
	 * @param rowIndex		行インデックス
	 * @param columnIndex	列インデックス
	 * @return データが更新された場合は <tt>true</tt>
	 */
	protected boolean deleteElementAt(int rowIndex, int columnIndex) {
		boolean isUpdate = false;
		if (rowIndex < data.size()) {
			RowDataModel row = data.get(rowIndex);
			if (row != null && columnIndex < row.size()) {
				isUpdate = isValidValue(row.get(columnIndex));
				// delete column data
				row.set(columnIndex, null);
				// remove null column following columnIndex
				clearInvalidColumns(row);
				if (row.isEmpty()) {
					// 行内に有効な列が存在しなければ、行データのインスタンスを除去
					data.set(rowIndex, null);
				}
			}
			clearInvalidRows();
		}
		
		return isUpdate;
	}

	/**
	 * 指定された位置にセルデータを登録する。このメソッドは、指定位置にセルデータが
	 * 格納できるよう、実データのサイズを拡張する。
	 * なお、<code>aValue</code> は <tt>null</tt> ではないことを前提としている。
	 * 
	 * @param aValue		登録するデータ
	 * @param rowIndex		行インデックス
	 * @param columnIndex	列インデックス
	 * @return データが更新された場合は <tt>true</tt>
	 */
	protected boolean storeElementAt(Object aValue, int rowIndex, int columnIndex) {
		boolean isUpdate = false;
		if (rowIndex >= data.size()) {
			// 行リストを拡張し、新しい行データを格納する。
			ensureElement(data, rowIndex);
			RowDataModel row = createRowDataModel(columnIndex+1);
			ensureElement(row, columnIndex);
			row.set(columnIndex, aValue);
			data.set(rowIndex, row);
			isUpdate = true;
		} else {
			RowDataModel row = data.get(rowIndex);
			if (row == null) {
				row = createRowDataModel(columnIndex+1);
				data.set(rowIndex, row);
			}
			if (columnIndex >= row.size()) {
				ensureElement(row, columnIndex);
				row.set(columnIndex, aValue);
				isUpdate = true;
			} else {
				Object oldObj = row.get(columnIndex);
				if (!aValue.equals(oldObj)) {
					row.set(columnIndex, aValue);
					isUpdate = true;
				}
			}
		}
		
		return isUpdate;
	}

	/**
	 * 指定された位置に、新しい行データを挿入する。
	 * 行インデックスが現在の行数を越える位置の場合、
	 * 新しい行データを行終端に追加する。
	 * このメソッドは、行インデックスの正当性は評価しない。
	 * 
	 * @param rowIndex	挿入する位置を示す行インデックス。
	 * @param rowData	新しい行データ
	 * @return
	 */
	protected void insertRowAt(int rowIndex, RowDataModel rowData) {
		if (rowIndex < data.size()) {
			data.add(rowIndex, rowData);
		}
		else if (rowData != null && !rowData.isEmpty()) {
			ensureElement(data, rowIndex);
			data.set(rowIndex, rowData);
		}
		rowSize++;
	}

	/**
	 * 指定された行インデックスのデータを、テーブルデータから削除する。
	 * このメソッドは、行インデックスの正当性は評価しない。
	 * <p>
	 * このメソッドは、最小行数の調整は行わない。
	 * 
	 * @param rowIndex	削除対象の行インデックス
	 * @return	削除した行のデータ
	 */
	protected RowDataModel removeRowAt(int rowIndex) {
		RowDataModel removed = null;
		if (rowIndex < data.size()) {
			removed = data.remove(rowIndex);
			clearInvalidRows();
		}
		rowSize--;
		return removed;
	}

	/**
	 * 指定された初期容量で、<code>RowDataModel</code> の新しいインスタンスを生成する。
	 * 
	 * @param initialCapacity	初期容量
	 * @return	生成された <code>RowDataModel</code>
	 * @throws IllegalArgumentException 初期容量が不正の場合
	 */
	protected RowDataModel createRowDataModel(int initialCapacity) {
		return new RowDataModel(initialCapacity);
	}
	
	protected RowDataModel createRowDataModel(Collection<? extends Object> c) {
		RowDataModel row = new RowDataModel(c.size());
		row.addAll(c);
		clearInvalidColumns(row);
		return row;
	}
	
	protected RowDataModel createRowDataModel(Object[] c) {
		RowDataModel row = new RowDataModel(c==null ? INITIAL_COL_CAPACITY : c.length);
		if (c != null && c.length > 0) {
			for (Object val : c) {
				row.add(val);
			}
		}
		clearInvalidColumns(row);
		return row;
	}
	
	protected ColumnDataModel createColumnDataModel(int initialCapacity) {
		return new ColumnDataModel(initialCapacity);
	}
	
	protected ColumnDataModel createColumnDataModel(Collection<? extends Object> c) {
		ColumnDataModel col = new ColumnDataModel(c.size());
		col.addAll(c);
		return col;
	}
	
	protected ColumnDataModel createColumnDataModel(Object[] c) {
		ColumnDataModel col = new ColumnDataModel(c==null ? INITIAL_ROW_CAPACITY : c.length);
		if (c != null && c.length > 0) {
			for (Object val : c) {
				col.add(val);
			}
		}
		return col;
	}
	
	protected void fireUndoableEditUpdate(UndoableEdit ue) {
		if (compoundEdits) {
			if (packEdits == null) {
				packEdits = new CompoundEdit();
			}
			packEdits.addEdit(ue);
		} else {
			fireUndoableEditUpdate(new UndoableEditEvent(this, ue));
		}
	}

    /**
     * Undo 可能な編集が行われたことを通知する。
     *
     * @param e	<code>UndoableEditEvent</code> オブジェクト
     */
	protected void fireUndoableEditUpdate(UndoableEditEvent e) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==UndoableEditListener.class) {
				// Lazily create the event:
				// if (e == null)
				// e = new ListSelectionEvent(this, firstIndex, lastIndex);
				((UndoableEditListener)listeners[i+1]).undoableEditHappened(e);
			}	       
		}
	}
	
	/**
	 * 行データモデル。
	 * <p>
	 * このクラスは、{@link java.util.ArrayList} の派生クラスであり、
	 * テーブルモデル内の１行分のデータを格納する。
	 * 
	 * @version 1.00	2008/11/21
	 *
	 * @since 1.00
	 */
	static public class RowDataModel extends ArrayList<Object>
	{
		public RowDataModel() {
			super();
		}

		public RowDataModel(int initialCapacity) {
			super(initialCapacity);
		}
	}
	
	/**
	 * 列データモデル。
	 * <p>
	 * このクラスは、{@link java.util.ArrayList} の派生クラスであり、
	 * テーブルモデル内の１列分のデータを格納する。
	 * 
	 * @version 1.00	2008/11/21
	 *
	 * @since 1.00
	 */
	static public class ColumnDataModel extends ArrayList<Object>
	{
		public ColumnDataModel() {
			super();
		}

		public ColumnDataModel(int initialCapacity) {
			super(initialCapacity);
		}
	}

	/**
	 * セルデータの変更を保持するクラス。
	 * このクラスは、<code>UndoableEdit</code> インタフェースを実装する。 
	 */
	class EditCellUndo extends AbstractUndoableEdit {
		protected final int rowIndex;
		protected final int columnIndex;
		protected final Object oldCellData;
		protected final Object newCellData;
		
		protected EditCellUndo(int rowIndex, int columnIndex, Object oldCellData, Object newCellData) {
			super();
			this.rowIndex = rowIndex;
			this.columnIndex = columnIndex;
			this.oldCellData = oldCellData;
			this.newCellData = newCellData;
		}
		
		public void undo() throws CannotUndoException {
			super.undo();
			try {
				// check
				validRowIndexRange(rowIndex);
				validColumnIndexRange(columnIndex);
				// update
				if (setElementAt(oldCellData, rowIndex, columnIndex)) {
					fireTableCellUpdated(rowIndex, columnIndex);
				}
			}
			catch (Throwable ex) {
				throw new CannotUndoException();
			}
		}
		
		public void redo() throws CannotRedoException {
			super.redo();
			try {
				// check
				validRowIndexRange(rowIndex);
				validColumnIndexRange(columnIndex);
				// update
				if (setElementAt(newCellData, rowIndex, columnIndex)) {
					fireTableCellUpdated(rowIndex, columnIndex);
				}
			}
			catch (Throwable ex) {
				throw new CannotRedoException();
			}
		}
	}

	/**
	 * 行数調整用の行データ追加を保持するクラス。
	 * このインスタンスは、<code>{@link SpreadSheetModel}</code> が呼び出された場合にのみ
	 * 生成される。
	 * 
	 * このクラスは、<code>UndoableEdit</code> インタフェースを実装する。
	 */
	class EditAdjustedInsertRowUndo extends AbstractUndoableEdit {
		protected final int firstRowIndex;
		protected final int lastRowIndex;
		
		protected EditAdjustedInsertRowUndo(int firstRowIndex, int lastRowIndex) {
			super();
			if (firstRowIndex > lastRowIndex)
				throw new IllegalArgumentException("Illegal row range : firstRowIndex(" + firstRowIndex + ")>lastRowIndex(" + lastRowIndex + ")");
			this.firstRowIndex = firstRowIndex;
			this.lastRowIndex = lastRowIndex;
		}
		
		public void undo() throws CannotUndoException {
			//--- 行インデックスの行データ削除
			super.undo();
			try {
				// check
				validRowIndexRange(firstRowIndex);
				validRowIndexRange(lastRowIndex);
				// remove row
				for (int row = lastRowIndex; row >= firstRowIndex; row--) {
					removeRowAt(row);
				}
				// update
				fireTableRowsDeleted(firstRowIndex, lastRowIndex);
			}
			catch (Throwable ex) {
				throw new CannotUndoException();
			}
		}
		
		public void redo() throws CannotUndoException {
			//--- 行インデックスの行データ追加
			super.redo();
			try {
				int numRows = lastRowIndex - firstRowIndex + 1;
				if (data.size() > firstRowIndex) {
					data.addAll(firstRowIndex, Arrays.asList(new RowDataModel[numRows]));
				}
				rowSize += numRows;
				// update
				fireTableRowsInserted(firstRowIndex, lastRowIndex);
			}
			catch (Throwable ex) {
				throw new CannotRedoException();
			}
		}
	}

	/**
	 * 行データの追加を保持するクラス。
	 * このクラスは、<code>UndoableEdit</code> インタフェースを実装する。
	 */
	class EditInsertRowUndo extends AbstractUndoableEdit {
		protected final int rowIndex;
		protected final RowDataModel rowData;
		
		protected EditInsertRowUndo(int rowIndex, RowDataModel rowData) {
			super();
			this.rowIndex = rowIndex;
			this.rowData = rowData;
		}
		
		public void undo() throws CannotUndoException {
			//--- 行インデックスの行データ削除
			super.undo();
			try {
				// check
				validRowIndexRange(rowIndex);
				// remove row
				removeRowAt(rowIndex);
				// update
				fireTableRowsDeleted(rowIndex, rowIndex);
			}
			catch (Throwable ex) {
				throw new CannotUndoException();
			}
		}
		
		public void redo() throws CannotRedoException {
			//--- 行インデックスの行データ追加
			super.redo();
			try {
				// check(現在の行数よりも大きい行インデックスはエラー)
				if (rowIndex < 0)
					throw new IndexOutOfBoundsException("Row index out of range : index(" + rowIndex + ")<0");
				else if (rowIndex > rowSize)
					throw new IndexOutOfBoundsException("Row index out of range : index(" + rowIndex + ")>" + rowSize);
				// insert row
				if (rowData != null && !rowData.isEmpty())
					insertRowAt(rowIndex, createRowDataModel(rowData));
				else
					insertRowAt(rowIndex, null);
				// update
				fireTableRowsInserted(rowIndex, rowIndex);
			}
			catch (Throwable ex) {
				throw new CannotRedoException();
			}
		}
	}

	/**
	 * 行データの削除を保持するクラス。
	 * このクラスは、<code>UndoableEdit</code> インタフェースを実装する。
	 */
	class EditDeleteRowUndo extends AbstractUndoableEdit {
		protected final int rowIndex;
		protected final RowDataModel rowData;
		
		protected EditDeleteRowUndo(int rowIndex, RowDataModel rowData) {
			super();
			this.rowIndex = rowIndex;
			this.rowData = rowData;
		}
		
		public void undo() throws CannotUndoException {
			//--- 行インデックスの行データ追加
			super.undo();
			try {
				// check(現在の行数よりも大きい行インデックスはエラー)
				if (rowIndex < 0)
					throw new IndexOutOfBoundsException("Row index out of range : index(" + rowIndex + ")<0");
				else if (rowIndex > rowSize)
					throw new IndexOutOfBoundsException("Row index out of range : index(" + rowIndex + ")>" + rowSize);
				// insert row
				if (rowData != null && !rowData.isEmpty())
					insertRowAt(rowIndex, createRowDataModel(rowData));
				else
					insertRowAt(rowIndex, null);
				// update
				fireTableRowsInserted(rowIndex, rowIndex);
			}
			catch (Throwable ex) {
				throw new CannotUndoException();
			}
		}
		
		public void redo() throws CannotRedoException {
			//--- 行インデックスの行データ削除
			super.redo();
			try {
				// check
				validRowIndexRange(rowIndex);
				// remove row
				removeRowAt(rowIndex);
				// update
				fireTableRowsDeleted(rowIndex, rowIndex);
			}
			catch (Throwable ex) {
				throw new CannotRedoException();
			}
		}
	}
}
