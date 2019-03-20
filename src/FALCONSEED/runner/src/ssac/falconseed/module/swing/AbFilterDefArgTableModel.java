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
 * @(#)FilterEditModel.java	3.1.0	2015/06/10
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

import ssac.aadl.module.ModuleArgType;
import ssac.falconseed.filter.common.gui.FilterEditModel;
import ssac.falconseed.module.FilterArgEditModelList;
import ssac.falconseed.module.swing.table.AbMExecDefArgTableModel;
import ssac.falconseed.module.swing.table.IModuleArgConfigTableModel;

/**
 * フィルタの引数定義用テーブルモデルの共通実装。
 * 
 * @version 3.1.0	2014/05/18
 * @since 2.0.0
 */
public abstract class AbFilterDefArgTableModel extends AbMExecDefArgTableModel implements IModuleArgConfigTableModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

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
	
	public FilterArgEditModel getArgument(int rowIndex) {
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

	@Override
	public void addRow(ModuleArgType attr, String desc, Object value) {
		ensureEditModel();
		insertRow(getRowCount(), attr, desc, value);
	}

	@Override
	public void insertRow(int rowIndex, ModuleArgType attr, String desc, Object value) {
		ensureEditModel();
		getEditModel().insertMExecDefArgument(rowIndex, attr, desc, value);
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

	@Override
	public void removeRow(int row) {
		ensureEditModel();
		getEditModel().removeMExecDefArgument(row);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 編集用モデルデータが存在しない場合に例外をスローする。
	 * @throws IndexOutOfBoundsException	編集用モデルデータが <tt>null</tt> の場合
	 */
	protected void ensureEditModel() {
		if (!hasEditModel()) {
			throw new IndexOutOfBoundsException("No such data.");
		}
	}
	
	abstract protected boolean hasEditModel();
	
	abstract protected FilterEditModel getEditModel();
	
	abstract protected void onMovedRows(int firstRow, int lastRow);

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
