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
 *  Copyright 2007-2008  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
/*
 * @(#)GeneralTableModel.java	1.00	2008/11/21
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.plugin.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * 汎用的なテーブルモデル。
 * <p>
 * このモデルは編集可能なテーブルモデルであり、<code>Object</code> を
 * 行と列で保持するテーブルモデルとなる。
 * 基本的に、全てのセルを編集可能とし、行の追加、削除、列の追加、削除の
 * インタフェースも実装する。
 * 
 * @version 1.00	2008/11/21
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 *
 * @since 1.00
 */
public class GeneralTableModel extends AbstractTableModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final int INITIAL_ROW_CAPACITY = 10;
	static private final int INITIAL_COL_CAPACITY = 10;

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

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public GeneralTableModel() {
		this(INITIAL_ROW_CAPACITY);
	}
	
	public GeneralTableModel(int rowCapacity) {
		super();
		this.data = newRowsList(rowCapacity);
		this.rowSize = 0;
		this.colSize = 0;
	}
	
	public GeneralTableModel(int rowSize, int columnSize) {
		super();
		if (rowSize < 0)
			throw new IllegalArgumentException("Illegal row count : " + rowSize);
		if (columnSize < 0)
			throw new IllegalArgumentException("Illegal column count : " + columnSize);
		this.data = newRowsList(rowSize);
		this.rowSize = rowSize;
		this.colSize = columnSize;
	}
	
	public GeneralTableModel(Object[] columnNames, int rowSize) {
		this((columnNames == null ? null : Arrays.asList(columnNames)), rowSize);
	}
	
	public GeneralTableModel(Collection<?> columnNames, int rowSize) {
		this(rowSize, (columnNames != null ? columnNames.size() : 0));
		if (columnNames != null) {
			this.columnIdentifiers = new ArrayList<Object>(columnNames);
		}
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
		
		// update
		boolean isUpdate;
		if (aValue != null)
			isUpdate = storeElementAt(aValue, rowIndex, columnIndex);
		else
			isUpdate = deleteElementAt(rowIndex, columnIndex);
		
		if (isUpdate) {
			fireTableCellUpdated(rowIndex, columnIndex);
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
					cl = new RowDataModel(row);
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
					cl = new RowDataModel(row);
					clearInvalidColumns(cl);
				}
				data.add(cl);
			}
			clearInvalidRows();
		}
		
		setColumnIdentifiers(columnNames);
		
		fireTableStructureChanged();
	}
	
	public void addColumn(Object columnName) {
		setColumnIdentifier(colSize, columnName);
		colSize++;

		fireTableStructureChanged();
	}
	
	public void addColumn(Object columnName, ColumnDataModel columnData) {
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
	
	public RowDataModel getRow(int rowIndex) {
		validRowIndexRange(rowIndex);
		
		if (rowIndex < data.size()) {
			return data.get(rowIndex);
		} else {
			return null;
		}
	}
	
	public void addRow(RowDataModel rowData) {
		int rowIndex = rowSize;
		if (rowData != null && !rowData.isEmpty()) {
			ensureElement(data, rowIndex);
			data.set(rowIndex, rowData);
		}
		rowSize++;
		
		fireTableRowsInserted(rowIndex, rowIndex);
	}
	
	public void insertRow(int rowIndex, RowDataModel rowData) {
		validRowIndexRange(rowIndex);

		if (rowIndex < data.size()) {
			data.add(rowIndex, rowData);
		}
		else if (rowData != null && !rowData.isEmpty()) {
			ensureElement(data, rowIndex);
			data.set(rowIndex, rowData);
		}
		rowSize++;
		
		fireTableRowsInserted(rowIndex, rowIndex);
	}
	
	public void removeRow(int rowIndex) {
		validRowIndexRange(rowIndex);
		
		if (rowIndex < data.size()) {
			data.set(rowIndex, null);
			clearInvalidRows();
		}
		rowSize--;
		fireTableRowsDeleted(rowIndex, rowIndex);
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
	
	protected void setColumnIdentifiers(Object[] columnNames) {
		if (columnNames != null && columnNames.length > 0) {
			this.columnIdentifiers = new ArrayList<Object>(Arrays.asList(columnNames));
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
	
	protected boolean isValidValue(Object value) {
		return (value != null);
	}
	
	protected final void clearInvalidColumns(final ArrayList<Object> colList) {
		for (int ci = colList.size()-1; ci >= 0; ci--) {
			if (!isValidValue(colList.get(ci))) {
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
				// delete column data
				row.set(columnIndex, null);
				// remove null column following columnIndex
				clearInvalidColumns(row);
				if (row.isEmpty()) {
					// 行内に有効な列が存在しなければ、行データのインスタンスを除去
					data.set(rowIndex, null);
				}
				isUpdate = true;
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
			RowDataModel row = new RowDataModel(columnIndex+1);
			ensureElement(row, columnIndex);
			row.set(columnIndex, aValue);
			data.set(rowIndex, row);
			isUpdate = true;
		} else {
			RowDataModel row = data.get(rowIndex);
			if (row == null) {
				row = new RowDataModel(columnIndex+1);
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
	 * 行データモデル。
	 * <p>
	 * このクラスは、{@link java.util.ArrayList} の派生クラスであり、
	 * テーブルモデル内の１行分のデータを格納する。
	 * 
	 * @version 1.00	2008/11/21
	 * 
	 * @author H.Deguchi(SOARS Project.)
	 * @author Y.Ishizuka(PieCake.inc,)
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

		public RowDataModel(Collection<? extends Object> c) {
			super(c);
		}
		
		public RowDataModel(Object[] c) {
			super(c == null ? INITIAL_COL_CAPACITY : c.length);
			if (c != null && c.length > 0) {
				for (Object val : c) {
					add(val);
				}
			}
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
	 * @author H.Deguchi(SOARS Project.)
	 * @author Y.Ishizuka(PieCake.inc,)
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

		public ColumnDataModel(Collection<? extends Object> c) {
			super(c);
		}
		
		public ColumnDataModel(Object[] c) {
			super(c == null ? INITIAL_ROW_CAPACITY : c.length);
			if (c != null && c.length > 0) {
				for (Object val : c) {
					add(val);
				}
			}
		}
	}
}
