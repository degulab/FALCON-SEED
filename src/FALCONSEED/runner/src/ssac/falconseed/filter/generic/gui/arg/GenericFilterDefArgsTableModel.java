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
 * @(#)GenericFilterDefArgsTableModel.java	3.2.1	2015/07/16
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui.arg;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.fs.module.schema.type.SchemaValueType;
import ssac.aadl.module.ModuleArgType;
import ssac.falconseed.filter.generic.gui.GenericFilterEditModel;
import ssac.falconseed.module.FilterArgEditModelList;
import ssac.falconseed.module.swing.FilterArgEditModel;
import ssac.falconseed.module.swing.IFilterArgEditHandler;
import ssac.falconseed.module.swing.table.AbMExecDefArgTableModel;
import ssac.falconseed.module.swing.table.IModuleArgConfigTableModel;
import ssac.falconseed.runner.RunnerMessages;

/**
 * 汎用フィルタ編集ダイアログ専用の、フィルタ定義引数データモデル。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public class GenericFilterDefArgsTableModel extends AbMExecDefArgTableModel implements IModuleArgConfigTableModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	/** 属性の標準列インデックス **/
	static public final int GENERICARG_COL_ATTR  	= 0;
	/** データ型の標準列インデックス **/
	static public final int GENERICARG_COL_DATATYPE	= 1;
	/** 説明の標準列インデックス **/
	static public final int GENERICARG_COL_DESC  	= 2;
	/** 値の標準列インデックス **/
	static public final int GENERICARG_COL_VALUE 	= 3;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static private final String[]	_genericColNames = {
		CommonMessages.getInstance().ModuleInfoLabel_args_attr,
		RunnerMessages.getInstance().GenericFilterEditDlg_name_ValueType,
		CommonMessages.getInstance().ModuleInfoLabel_args_desc,
		CommonMessages.getInstance().ModuleInfoLabel_args_value,
	};

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** このテーブルの基準データとなるデータモデル(必ず <tt>null</tt> 以外) **/
	private final GenericFilterEditModel	_editModel;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public GenericFilterDefArgsTableModel(final GenericFilterEditModel editModel) {
		super();
		if (editModel == null)
			throw new NullPointerException("GenericFilterEditModel object is null.");
		_editModel = editModel;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public int getArgumentCount() {
		return getEditModel().getMExecDefArgumentCount();
	}
	
	public boolean containsArgument(Object elem) {
		return getEditModel().getMExecDefArgsList().contains(elem);
	}

	/**
	 * 引数データ型の列インデックスを返す。
	 */
	public int dataTypeColumnIndex() {
		return GENERICARG_COL_DATATYPE;
	}

	/**
	 * 引数属性の列インデックスを返す。
	 */
	@Override
	public int attributeColumnIndex() {
		return GENERICARG_COL_ATTR;
	}

	/**
	 * 引数説明の列インデックスを返す。
	 */
	@Override
	public int descriptionColumnIndex() {
		return GENERICARG_COL_DESC;
	}

	/**
	 * 引数値の列インデックスを返す。
	 */
	@Override
	public int valueColumnIndex() {
		return GENERICARG_COL_VALUE;
	}

	/**
	 * 列数を返す。
	 */
	@Override
	public int getColumnCount() {
		return _genericColNames.length;
	}

	/**
	 * 指定された列インデックスの列名を返す。
	 * @param columnIndex	列インデックス
	 * @return	列名
	 */
	@Override
	public String getColumnName(int columnIndex) {
		if (0 <= columnIndex && columnIndex < _genericColNames.length) {
			return _genericColNames[columnIndex];
		} else {
			return super.getColumnName(columnIndex);
		}
	}
	
	/**
	 * 指定された列のデータ型を返す。
	 * @param columnIndex	列インデックス
	 * @return	列のデータ型
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == attributeColumnIndex()) {
			return ModuleArgType.class;
		}
		else if (columnIndex == dataTypeColumnIndex()) {
			return String.class;
		}
		else if (columnIndex == descriptionColumnIndex()) {
			return String.class;
		}
		else if (columnIndex == valueColumnIndex()) {
			return Object.class;
		}
		else {
			return null;
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (!hasEditModel() || !getEditModel().isEditing()) {
			// no data, or no editing
			return false;
		}
		
		if (columnIndex == valueColumnIndex()) {
			ModuleArgType type = getArgumentAttr(rowIndex);
			if (ModuleArgType.IN == type || ModuleArgType.OUT == type) {
				// ファイル引数の場合は編集不可
				return false;
			}
			else {
				// 文字列なら直接編集可能
				return true;
			}
		}
		else if (columnIndex == descriptionColumnIndex()) {
			// 説明は編集可能
			return true;
		}
		else {
			// 上記以外は編集不可
			return false;
		}
	}

	@Override
	public int getRowCount() {
		return (hasEditModel() ? getEditModel().getMExecDefArgumentCount() : 0);
	}
	
	public GenericFilterArgEditModel getArgument(int rowIndex) {
		ensureEditModel();
		return getEditModel().getMExecDefArgument(rowIndex);
	}

	@Override
	public ModuleArgType getArgumentAttr(int rowIndex) {
		ensureEditModel();
		return getEditModel().getMExecDefArgument(rowIndex).getType();
	}

	@Override
	public String getArgumentDescription(int rowIndex) {
		ensureEditModel();
		return getEditModel().getMExecDefArgument(rowIndex).getDescription();
	}

	@Override
	public Object getArgumentValue(int rowIndex) {
		ensureEditModel();
		return getEditModel().getMExecDefArgument(rowIndex).getValue();
	}
	
	public SchemaValueType getArgumentDataType(int rowIndex) {
		ensureEditModel();
		return getEditModel().getMExecDefArgumentDataType(rowIndex);
	}

	@Override
	public void setArgumentAttr(int rowIndex, ModuleArgType newAttr) {
		throw new UnsupportedOperationException(getClass().getName() + "#setArgumentAttr() is not supported!");
	}

	@Override
	public void setArgumentDescription(int rowIndex, String newDesc) {
		ensureEditModel();
		getEditModel().setMExecDefArgumentDescription(rowIndex, newDesc);
	}

	@Override
	public void setArgumentValue(int rowIndex, Object newValue) {
		ensureEditModel();
		getEditModel().setMExecDefArgumentValue(rowIndex, newValue);
	}
	
	public void setArgumentDataType(int rowIndex, SchemaValueType newValueType) {
		ensureEditModel();
		getEditModel().setMExecDefArgumentDataType(rowIndex, newValueType);
	}

	@Override
	public void addRow(ModuleArgType attr, String desc, Object value) {
		ensureEditModel();
		insertRow(getRowCount(), attr, desc, value);
	}
	
	public void addRow(ModuleArgType attr, SchemaValueType dataType, String desc, Object value) {
		ensureEditModel();
		insertRow(getRowCount(), attr, dataType, desc, value);
	}

	@Override
	public void insertRow(int rowIndex, ModuleArgType attr, String desc, Object value) {
		ensureEditModel();
		getEditModel().insertMExecDefArgument(rowIndex, attr, desc, value);
	}
	
	public void insertRow(int rowIndex, ModuleArgType attr, SchemaValueType dataType, String desc, Object value) {
		ensureEditModel();
		getEditModel().insertMExecDefArgument(rowIndex, attr, dataType, desc, value);
	}

	@Override
	public void addNewRow() {
		throw new UnsupportedOperationException(getClass().getName() + "#addNewRow() is not supported!");
	}

	@Override
	public void moveRow(int start, int end, int to) {
		ensureEditModel();
		int shift = to - start; 
		int first, last; 
		if (shift < 0) { 
			first = to; 
			last = end; 
		}
		else { 
			first = start; 
			last = to + end - start;  
		}

		rotate(getEditModel().getMExecDefArgsList(), first, last + 1, shift);
		onMovedRows(first, last);
		fireTableRowsUpdated(first, last);
	}

	@Override
	public void removeRow(int row) {
		ensureEditModel();
		getEditModel().removeMExecDefArgument(row);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == valueColumnIndex()) {
			return getArgumentValue(rowIndex);
		}
		else if (columnIndex == dataTypeColumnIndex()) {
			return getArgumentDataType(rowIndex);
		}
		else if (columnIndex == descriptionColumnIndex()) {
			return getArgumentDescription(rowIndex);
		}
		else if (columnIndex == attributeColumnIndex()) {
			return getArgumentAttr(rowIndex);
		}
		else {
			return null;
		}
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		if (columnIndex == valueColumnIndex()) {
			setArgumentValue(rowIndex, value);
		}
		else if (columnIndex == descriptionColumnIndex()) {
			setArgumentDescription(rowIndex, (value==null ? null : value.toString()));
		}
		else if (columnIndex == attributeColumnIndex()) {
			setArgumentAttr(rowIndex, (ModuleArgType)value);
		}
	}
	
	public void fireArgumentDataTypeUpdated(int rowIndex) {
		fireTableCellUpdated(rowIndex, dataTypeColumnIndex());
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static protected int gcd(int i, int j) {
		return (j == 0) ? i : gcd(j, i%j);
	}
	
	static protected void rotate(FilterArgEditModelList list, int a, int b, int shift) {
		int size = b - a; 
		int r = size - shift;
		int g = gcd(size, r); 
		for(int i = 0; i < g; i++) {
			int to = i; 
			FilterArgEditModel tmp = list.get(a + to); 
			for(int from = (to + r) % size; from != i; from = (to + r) % size) {
				list.set(a + to, list.get(a + from));
				to = from; 
			}
			list.set(a + to, tmp);
		}
	}

	/**
	 * 編集用モデルデータが存在しない場合に例外をスローする。
	 * @throws IndexOutOfBoundsException	編集用モデルデータが <tt>null</tt> の場合
	 */
	protected void ensureEditModel() {
		if (!hasEditModel()) {
			throw new IndexOutOfBoundsException("No such data.");
		}
	}
	
	public boolean hasEditModel() {
		return true;
	}
	
	public GenericFilterEditModel getEditModel() {
		return _editModel;
	}
	
	public void onMovedRows(int firstRow, int lastRow) {
		IFilterArgEditHandler handler = _editModel.getMExecDefArgEditHandler();
		if (handler != null) {
			handler.onChangedArgument(firstRow, lastRow);
		}
	}

	//------------------------------------------------------------
	// Data access method
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
