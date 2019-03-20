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
 * @(#)GenericOutputCsvSchemaTableModel.java	3.2.1	2015/07/22
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui.table;

import java.util.ArrayList;
import java.util.Arrays;

import ssac.aadl.fs.module.schema.SchemaElementValue;
import ssac.falconseed.filter.generic.gui.GenericFilterEditModel;
import ssac.falconseed.filter.generic.gui.util.GenericSchemaElementTreeNode;
import ssac.falconseed.runner.RunnerMessages;
import ssac.util.swing.table.AbSpreadSheetTableModel;

/**
 * 汎用フィルタの出力スキーマ編集ダイアログで使用する、CSV 出力スキーマ編集テーブルのデータモデル。
 * 
 * @version 3.2.1
 * @since 3.2.1
 */
public class GenericOutputCsvSchemaTableModel extends AbSpreadSheetTableModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	static protected final int ROWIDX_TARGETNAME	= 0;
	static protected final int ROWIDX_TARGETVALUE	= 1;
	static protected final int FIXED_UPPERINDEX		= ROWIDX_TARGETVALUE;
	static protected final int FIXED_ROWS			= FIXED_UPPERINDEX + 1;
	
	static public final int	CAN_SHIFT_LEFT	= 0x01;
	static public final int	CAN_SHIFT_RIGHT	= 0x02;
	static public final int	CAN_SHIFT_BOTH	= CAN_SHIFT_LEFT | CAN_SHIFT_RIGHT;
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 削除されたオリジナルのフィールドデータのリスト **/
	private ArrayList<OutputCsvFieldSchemaEditModel>	_removedOrgFields;
	/** 編集対象のデータモデル **/
	private OutputCsvTableSchemaEditModel	_dataModel;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定された編集対象データモデルから、編集用テーブルモデルを生成する。
	 * @param editModel	汎用フィルタの編集用データモデル
	 * @param tableNode	編集対象の出力スキーマのテーブルを示すツリーノード、新規の場合は <tt>null</tt>
	 * @throws NullPointerException	<em>editModel</em> が <tt>null</tt> の場合
	 */
	public GenericOutputCsvSchemaTableModel(GenericFilterEditModel editModel, GenericTableSchemaTreeNode tableNode) {
		super();
		if (editModel == null)
			throw new NullPointerException();
		
		// 編集用データモデルの生成
		OutputCsvTableSchemaEditModel newDataModel;
		if (tableNode == null) {
			// 新規作成
			String strDefDesc = editModel.getDefaultNewOutputTableSchemaDescription();
			newDataModel = new OutputCsvTableSchemaEditModel();
			newDataModel.setDescription(strDefDesc);
			newDataModel.setAutoHeaderRecordEnabled(true);	// ヘッダー行自動生成をデフォルト
		}
		else {
			// 既存のデータを持つ編集用データ生成
			OutputCsvTableSchemaEditModel orgModel = (OutputCsvTableSchemaEditModel)tableNode.getUserObject().getData();
			newDataModel = new OutputCsvTableSchemaEditModel(false, orgModel);	// 要素はコピーしない
			newDataModel.setDescription(newDataModel.getFilterArgModel().getDescription());	// 編集用
			int numFields = tableNode.getChildCount();
			if (numFields > 0) {
				// ツリーの子ノードからコピーを生成
				for (int i = 0; i < numFields; ++i) {
					GenericSchemaElementTreeNode child = (GenericSchemaElementTreeNode)tableNode.getChildAt(i);
					OutputCsvFieldSchemaEditModel orgField = (OutputCsvFieldSchemaEditModel)child.getUserObject();
					OutputCsvFieldSchemaEditModel editField = new OutputCsvFieldSchemaEditModel(orgField);
					newDataModel.add(editField);
				}
			}
		}
		_dataModel = newDataModel;
		
		// 削除したオリジナルデータを格納するリストの初期化
		_removedOrgFields = new ArrayList<OutputCsvFieldSchemaEditModel>();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public OutputCsvTableSchemaEditModel getDataModel() {
		return _dataModel;
	}
	
	public ArrayList<OutputCsvFieldSchemaEditModel> getRemovedOriginalFieldDataList() {
		return _removedOrgFields;
	}
	
	public boolean isAutoHeaderRecordEnabled() {
		return _dataModel.isAutoHeaderRecordEnabled();
	}
	
	public boolean setAutoHeaderRecordEnabled(boolean toEnable) {
		int oldRowCount = getRowCount();
		if (!_dataModel.updateAutoHeaderRecordEnabled(toEnable))
			return false;	// no changes
		//--- modified
		int newRowCount = getRowCount();
		if (newRowCount < oldRowCount) {
			// remove header rows
			fireTableRowsDeleted(newRowCount, oldRowCount-1);
		}
		else if (newRowCount > oldRowCount) {
			// insert header rows
			fireTableRowsInserted(oldRowCount, newRowCount-1);
		}
		return true;
	}
	
	public int getHeaderRecordCount() {
		return _dataModel.getHeaderRecordCount();
	}
	
	public boolean setHeaderRecordCount(int numRecords) {
		int oldRowCount = getRowCount();
		if (!_dataModel.updateHeaderRecordCount(numRecords))
			return false;	// no changes
		//--- modified
		int newRowCount = getRowCount();
		if (newRowCount < oldRowCount) {
			// remove header rows
			fireTableRowsDeleted(newRowCount, oldRowCount-1);
		}
		else if (newRowCount > oldRowCount) {
			// insert header rows
			fireTableRowsInserted(oldRowCount, newRowCount-1);
		}
		return true;
	}
	
	public boolean isEmpty() {
		return _dataModel.isEmpty();
	}
	
	public boolean clear() {
		if (_dataModel.isEmpty())
			return false;
		//--- modified
		_dataModel.clear();
		fireTableStructureChanged();
		return true;
	}

	@Override
	public int getRowCount() {
		if (_dataModel.isEmpty()) {
			return 0;	// no cells
		}
		else if (_dataModel.isAutoHeaderRecordEnabled()) {
			return FIXED_ROWS;
		} else {
			return (_dataModel.getHeaderRecordCount() + FIXED_ROWS);
		}
	}

	@Override
	public int getColumnCount() {
		return _dataModel.size();
	}

	@Override
	public String getColumnName(int columnIndex) {
		return Integer.toString(columnIndex+1);
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (rowIndex >= FIXED_ROWS) {
			// header fields are editable
			return true;
		} else {
			// other cells are uneditable
			return false;
		}
	}

	@Override
	public String getRowName(int rowIndex) {
		if (rowIndex == ROWIDX_TARGETNAME) {
			return RunnerMessages.getInstance().GenericFilterEditDlg_name_Name;
		}
		else if (rowIndex == ROWIDX_TARGETVALUE) {
			return RunnerMessages.getInstance().GenericFilterEditDlg_name_Operand;
		}
		else if (rowIndex > 0) {
			return String.format("%s[%d]", RunnerMessages.getInstance().GenericFilterEditDlg_name_Header,
					(rowIndex - FIXED_UPPERINDEX));
		}
		else {
			return super.getRowName(rowIndex);
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex < 0 || rowIndex > getRowCount())
			throw new IndexOutOfBoundsException("Row index out of range : " + rowIndex);

		OutputCsvFieldSchemaEditModel field = (OutputCsvFieldSchemaEditModel)_dataModel.get(columnIndex);
		if (rowIndex == ROWIDX_TARGETNAME) {
			return field.toNameString();
		}
		else if (rowIndex == ROWIDX_TARGETVALUE) {
			return field.getTargetValue();
		}
		else {
			int headerIndex = rowIndex - FIXED_ROWS;
			if (headerIndex < field.getHeaderCount()) {
				return field.getHeaderValue(headerIndex);
			} else {
				return null;
			}
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (rowIndex < 0 || rowIndex > getRowCount())
			throw new IndexOutOfBoundsException("Row index out of range : " + rowIndex);
		
		OutputCsvFieldSchemaEditModel field = (OutputCsvFieldSchemaEditModel)_dataModel.get(columnIndex);
		if (rowIndex == ROWIDX_TARGETNAME) {
			// field name
			String strName = (aValue==null ? null : aValue.toString());
			if (field.updateName(strName)) {
				fireTableCellUpdated(rowIndex, columnIndex);
			}
		}
		else if (rowIndex == ROWIDX_TARGETVALUE) {
			// target value
			if (field.updateTargetValue((SchemaElementValue)aValue)) {
				fireTableCellUpdated(rowIndex, columnIndex);
			}
		}
		else {
			// header records
			int headerIndex = rowIndex - FIXED_ROWS;
			if (headerIndex < field.getHeaderCount()) {
				// update
				if (field.updateHeaderValue(headerIndex, (aValue==null ? null : aValue.toString()))) {
					fireTableCellUpdated(rowIndex, columnIndex);
				}
			}
			else {
				// growup header fields
				field.growAndSetHeaderValue(headerIndex, (aValue==null ? null : aValue.toString()));
				fireTableCellUpdated(rowIndex, columnIndex);
			}
		}
	}
	
	public void addNewField(SchemaElementValue targetValue) {
		OutputCsvFieldSchemaEditModel field = new OutputCsvFieldSchemaEditModel();
		field.setTargetValue(targetValue);
		_dataModel.add(field);
		fireTableStructureChanged();
	}
	
	public boolean updateFieldTargetValue(int columnIndex, SchemaElementValue targetValue) {
		OutputCsvFieldSchemaEditModel field = (OutputCsvFieldSchemaEditModel)_dataModel.get(columnIndex);
		if (field.updateTargetValue(targetValue)) {
			// modofied
			fireTableCellUpdated(ROWIDX_TARGETNAME, columnIndex);
			fireTableCellUpdated(ROWIDX_TARGETVALUE, columnIndex);
			return true;
		}
		else {
			// no changes
			return false;
		}
	}

	/**
	 * 指定された列インデックスの要素を削除する。
	 * @param columnIndices	削除対象の列インデックスの配列
	 * @return	一つ以上削除された場合は <tt>true</tt>
	 */
	public boolean removeFields(int...columnIndices) {
		if (columnIndices.length == 0)
			return false;
		
		Arrays.sort(columnIndices);
		boolean modified = false;
		for (int i = columnIndices.length - 1; i >= 0; --i) {
			OutputCsvFieldSchemaEditModel removed = (OutputCsvFieldSchemaEditModel)_dataModel.remove(columnIndices[i]);
			if (removed != null) {
				modified = true;
				removed.setTargetValue(null);
				OutputCsvFieldSchemaEditModel orgModel = removed.getOriginalModel();
				if (orgModel != null) {
					_removedOrgFields.add(orgModel);
				}
			}
		}
		if (modified) {
			fireTableStructureChanged();
		}
		return modified;
	}

	/**
	 * 配列で指定された列インデックスに対応する列が移動可能かを判定する。
	 * @param fromColumnIndices	移動対象の列インデックスの配列
	 * @return	列が一つでも移動可能な場合は移動可能な方向を示す値、移動不可能な場合は 0
	 */
	public int canMoveFields(int...fromColumnIndices) {
		if (fromColumnIndices.length == 0)
			return 0;
		
		Arrays.sort(fromColumnIndices);
		int result = 0;
		if (fromColumnIndices[0] > 0) {
			result |= CAN_SHIFT_LEFT;
		}
		if (fromColumnIndices[fromColumnIndices.length-1] < (_dataModel.size() - 1)) {
			result |= CAN_SHIFT_RIGHT;
		}
		if (result == CAN_SHIFT_BOTH) {
			return result;
		}
		
		// check gap
		int previdx = fromColumnIndices[0];
		for (int i = 1; i < fromColumnIndices.length; ++i) {
			if (previdx != (fromColumnIndices[i] - 1)) {
				// has gap
				return CAN_SHIFT_BOTH;
			}
			previdx = fromColumnIndices[i];
		}
		
		// no gaps
		return result;
	}
	
	/**
	 * 配列で指定された列インデックスに対応する列を移動する。
	 * @param direction	移動方向、負の値の場合は列先頭へ移動、正の値の場合は列終端へ移動
	 * @param fromColumnIndices	移動対象の列インデックスの配列
	 * @return	移動した場合は新しい位置を示すインデックスの配列、移動しなかった場合は <tt>null</tt>
	 */
	public int[] moveFields(int direction, int...fromColumnIndices) {
		if (fromColumnIndices.length == 0 || direction == 0)
			return null;
		
		Arrays.sort(fromColumnIndices);
		int[] newColumnIndices = new int[fromColumnIndices.length];
		boolean moved = false;
		if (direction < 0) {
			// move to prev
			int lastSelIndex = -1;
			for (int posIndex = 0; posIndex < fromColumnIndices.length; ++posIndex) {
				int curSelIndex = fromColumnIndices[posIndex];
				int newSelIndex = curSelIndex - 1;
				if (lastSelIndex < newSelIndex) {
					// swap
					moved = true;
					OutputCsvFieldSchemaEditModel tmp = (OutputCsvFieldSchemaEditModel)_dataModel.get(newSelIndex);
					_dataModel.set(newSelIndex, _dataModel.get(curSelIndex));
					_dataModel.set(curSelIndex, tmp);
					lastSelIndex = newSelIndex;
				}
				else {
					// cannot move
					lastSelIndex = curSelIndex;
				}
				newColumnIndices[posIndex] = lastSelIndex;
			}
		}
		else {
			// move to next
			int lastSelIndex = _dataModel.size();
			for (int posIndex = fromColumnIndices.length - 1; posIndex >= 0; --posIndex) {
				int curSelIndex = fromColumnIndices[posIndex];
				int newSelIndex = curSelIndex + 1;
				if (lastSelIndex > newSelIndex) {
					// swap
					moved = true;
					OutputCsvFieldSchemaEditModel tmp = (OutputCsvFieldSchemaEditModel)_dataModel.get(newSelIndex);
					_dataModel.set(newSelIndex, _dataModel.get(curSelIndex));
					_dataModel.set(curSelIndex, tmp);
					lastSelIndex = newSelIndex;
				}
				else {
					// cannot move
					lastSelIndex = curSelIndex;
				}
				newColumnIndices[posIndex] = lastSelIndex;
			}
		}
		
		if (moved) {
			fireTableDataChanged();
			return newColumnIndices;
		} else {
			return null;
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner class
	//------------------------------------------------------------
}
