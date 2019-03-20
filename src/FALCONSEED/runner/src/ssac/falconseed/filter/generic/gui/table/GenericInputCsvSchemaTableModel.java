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
 * @(#)GenericInputCsvSchemaTableModel.java	3.2.1	2015/07/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)GenericInputCsvSchemaTableModel.java	3.2.0	2015/06/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui.table;

import java.util.ArrayList;
import java.util.Arrays;

import ssac.aadl.fs.module.schema.type.SchemaValueType;
import ssac.falconseed.filter.generic.gui.GenericFilterEditModel;
import ssac.falconseed.filter.generic.gui.util.GenericSchemaElementTreeNode;
import ssac.falconseed.runner.RunnerMessages;
import ssac.util.swing.table.AbSpreadSheetTableModel;

/**
 * 汎用フィルタの入力スキーマ編集ダイアログで使用する、CSV 入力スキーマ編集テーブルのデータモデル。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public class GenericInputCsvSchemaTableModel extends AbSpreadSheetTableModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	static protected final int MAX_ROWS	= 2;
	static protected final int ROWIDX_FIELDNAME	= 0;
	static protected final int ROWIDX_VALUETYPE	= 1;
	
	static public final int	CAN_SHIFT_LEFT	= 0x01;
	static public final int	CAN_SHIFT_RIGHT	= 0x02;
	static public final int	CAN_SHIFT_BOTH	= CAN_SHIFT_LEFT | CAN_SHIFT_RIGHT;
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 編集対象のデータモデル **/
	private InputCsvTableSchemaEditModel	_dataModel;

	/** 編集によって削除されたオリジナルフィールドデータ **/
	private ArrayList<InputCsvFieldSchemaEditModel>	_removedOrgFields;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/**
	 * 指定された編集対象データモデルから、編集用テーブルモデルを生成する。
	 * @param editModel	汎用フィルタの編集用データモデル
	 * @param tableNode	編集対象の入力スキーマのテーブルを示すツリーノード、新規の場合は <tt>null</tt>
	 * @throws NullPointerException	<em>editModel</em> が <tt>null</tt> の場合
	 */
	public GenericInputCsvSchemaTableModel(GenericFilterEditModel editModel, GenericTableSchemaTreeNode tableNode) {
		super();
		if (editModel == null)
			throw new NullPointerException();
		
		// 編集用データモデルの生成
		InputCsvTableSchemaEditModel newDataModel;
		if (tableNode == null) {
			// 新規作成
			String strDefDesc = editModel.getDefaultNewInputTableSchemaDescription();
			newDataModel = new InputCsvTableSchemaEditModel();
			newDataModel.setDescription(strDefDesc);
		}
		else {
			// 既存のデータを持つ編集用データ生成
			InputCsvTableSchemaEditModel orgModel = (InputCsvTableSchemaEditModel)tableNode.getUserObject().getData();
			newDataModel = new InputCsvTableSchemaEditModel(false, orgModel);	// 要素はコピーしない
			newDataModel.setDescription(newDataModel.getFilterArgModel().getDescription());	// 編集用
			int numFields = tableNode.getChildCount();
			if (numFields > 0) {
				// ツリーの子ノードからコピーを生成
				for (int i = 0; i < numFields; ++i) {
					GenericSchemaElementTreeNode child = (GenericSchemaElementTreeNode)tableNode.getChildAt(i);
					InputCsvFieldSchemaEditModel orgField = (InputCsvFieldSchemaEditModel)child.getUserObject();
					InputCsvFieldSchemaEditModel editField = new InputCsvFieldSchemaEditModel(orgField);
					newDataModel.add(editField);
				}
			}
		}
		_dataModel = newDataModel;
		
		// 削除したオリジナルデータを格納するリストの初期化
		_removedOrgFields = new ArrayList<InputCsvFieldSchemaEditModel>();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public InputCsvTableSchemaEditModel getDataModel() {
		return _dataModel;
	}
	
	public ArrayList<InputCsvFieldSchemaEditModel> getRemovedOriginalFieldDataList() {
		return _removedOrgFields;
	}
	
	public boolean isEmpty() {
		return _dataModel.isEmpty();
	}
	
	public boolean clear() {
		if (_dataModel.isEmpty())
			return false;
		//--- modified
		int numFields = _dataModel.size();
		for (int index = 0; index < numFields; ++index) {
			InputCsvFieldSchemaEditModel fieldModel = (InputCsvFieldSchemaEditModel)_dataModel.get(index);
			if (fieldModel.hasOriginalSchema()) {
				_removedOrgFields.add(fieldModel.getOriginalSchema());
			}
		}
		_dataModel.clear();
		fireTableStructureChanged();
		return true;
	}

	@Override
	public int getRowCount() {
		return (_dataModel.isEmpty() ? 0 : MAX_ROWS);
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
		if (rowIndex >= 0 && rowIndex < MAX_ROWS) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String getRowName(int rowIndex) {
		if (rowIndex == ROWIDX_FIELDNAME) {
			return RunnerMessages.getInstance().GenericFilterEditDlg_name_Name;
		}
		else if (rowIndex == ROWIDX_VALUETYPE) {
			return RunnerMessages.getInstance().GenericFilterEditDlg_name_ValueType;
		}
		else {
			return super.getRowName(rowIndex);
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex < 0 || rowIndex >= MAX_ROWS)
			throw new IndexOutOfBoundsException("Row index out of range : " + rowIndex);

		InputCsvFieldSchemaEditModel field = (InputCsvFieldSchemaEditModel)_dataModel.get(columnIndex);
		if (rowIndex == ROWIDX_FIELDNAME) {
			return field.getName();
		}
		else {
			return field.getValueType();
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (rowIndex < 0 || rowIndex >= MAX_ROWS)
			throw new IndexOutOfBoundsException("Row index out of range : " + rowIndex);
		
		InputCsvFieldSchemaEditModel field = (InputCsvFieldSchemaEditModel)_dataModel.get(columnIndex);
		if (rowIndex == ROWIDX_FIELDNAME) {
			if (field.updateName(aValue==null ? null : aValue.toString())) {
				fireTableCellUpdated(rowIndex, columnIndex);
			}
		}
		else {
			SchemaValueType vtype;
			if (aValue instanceof SchemaValueType) {
				vtype = (SchemaValueType)aValue;
			} else {
				vtype = null;
			}
			if (field.updateValueType(vtype)) {
				fireTableCellUpdated(rowIndex, columnIndex);
			}
		}
	}
	
	public void addNewField() {
		InputCsvFieldSchemaEditModel field = new InputCsvFieldSchemaEditModel();
		_dataModel.add(field);
		fireTableStructureChanged();
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
			InputCsvFieldSchemaEditModel removed = (InputCsvFieldSchemaEditModel)_dataModel.remove(columnIndices[i]);
			if (removed != null) {
				modified = true;
				InputCsvFieldSchemaEditModel orgField = removed.getOriginalSchema();
				if (orgField != null) {
					// オリジナルデータの削除
					_removedOrgFields.add(orgField);
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
					InputCsvFieldSchemaEditModel tmp = (InputCsvFieldSchemaEditModel)_dataModel.get(newSelIndex);
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
					InputCsvFieldSchemaEditModel tmp = (InputCsvFieldSchemaEditModel)_dataModel.get(newSelIndex);
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
}
