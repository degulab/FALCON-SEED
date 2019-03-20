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
 *  Copyright 2007-2014  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)ReferenceableSubFilterArgTableModel.java	3.1.0	2014/05/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.module.ModuleArgType;
import ssac.aadl.module.swing.table.IModuleArgTableModel;
import ssac.falconseed.module.ModuleArgID;
import ssac.falconseed.module.swing.MacroFilterEditModel;

/**
 * 参照可能引数の一覧を表示するテーブルコンポーネント専用のデータモデル。
 * このデータモデルの行要素は <code>ModuleArgID</code> となる。
 * 
 * @version 3.1.0	2014/05/19
 * @since 3.1.0
 */
public class ReferenceableSubFilterArgTableModel extends AbstractTableModel implements IModuleArgTableModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	static public final int	CI_ARGATTR		= 0;	// 引数番号と属性
	static public final int	CI_ARGDESC		= 1;	// 引数説明
	static public final int	CI_ARGVALUE		= 2;	// 引数の値
	static private final int NUM_COLUMNS	= 3;
	
	static public final ReferenceableSubFilterArgTableModel	EmptyTableModel
										= new ReferenceableSubFilterArgTableModel(null, Collections.<ModuleArgID>emptyList());

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	private final MacroFilterEditModel	_editModel;
	private List<ModuleArgID> _arglist;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ReferenceableSubFilterArgTableModel(final MacroFilterEditModel editModel) {
		this(editModel, new ArrayList<ModuleArgID>());
	}
	
	public ReferenceableSubFilterArgTableModel(final MacroFilterEditModel editModel, int initialCapacity) {
		this(editModel, new ArrayList<ModuleArgID>(initialCapacity));
	}
	
	public ReferenceableSubFilterArgTableModel(final MacroFilterEditModel editModel, Collection<? extends ModuleArgID> c) {
		this(editModel, new ArrayList<ModuleArgID>(c));
	}
	
	protected ReferenceableSubFilterArgTableModel(final MacroFilterEditModel editModel, List<ModuleArgID> list) {
		_editModel = editModel;
		_arglist = list;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定された行インデックスに対応する行データを返す。
	 * @param rowIndex	行インデックス
	 * @return 行データ
	 * @throws IndexOutOfBoundsException	インデックスが適切ではない場合
	 */
	public ModuleArgID getRowData(int rowIndex) {
		return _arglist.get(rowIndex);
	}
	
	/**
	 * 指定されたインデックスの行に新しい行データをセットする。
	 * このメソッドでは、行が更新された場合のみ、<code>fireTableRowsUpdated</code> を呼び出す。
	 * @param rowIndex	行インデックス
	 * @param newData	新しい行データ
	 * @throws NullPointerException			行データが <tt>null</tt> の場合
	 * @throws IndexOutOfBoundsException	インデックスが適切ではない場合
	 */
	public void setRowData(int rowIndex, final ModuleArgID newData) {
		if (!newData.equals(_arglist.get(rowIndex))) {
			_arglist.set(rowIndex, newData);
			fireTableRowsUpdated(rowIndex, rowIndex);
		}
	}
	
	/**
	 * 終端に新しい行を追加する。
	 * このメソッドでは、<code>fireTableRowsInserted</code> を呼び出す。
	 * @param newData	新しい行データ
	 * @throws NullPointerException	行データが <tt>null</tt> の場合
	 */
	public void addRowData(final ModuleArgID newData) {
		if (newData == null)
			throw new NullPointerException();
		int newRowIndex = getRowCount();
		_arglist.add(newData);
		fireTableRowsInserted(newRowIndex, newRowIndex);
	}

	/**
	 * 指定されたインデックスの行を削除する。
	 * このメソッドでは、行が削除された場合のみ、<code>fireTableRowsDeleted</code> を呼び出す。
	 * @param rowIndex	行インデックス
	 */
	public void removeRow(int rowIndex) {
		ModuleArgID removedData = _arglist.remove(rowIndex);
		if (removedData != null) {
			fireTableRowsDeleted(rowIndex, rowIndex);
		}
	}

	@Override
	public int getRowCount() {
		return _arglist.size();
	}

	@Override
	public int getColumnCount() {
		return NUM_COLUMNS;
	}

	@Override
	public String getColumnName(int columnIndex) {
		switch (columnIndex) {
			case CI_ARGATTR :
				return CommonMessages.getInstance().ModuleInfoLabel_args_attr;
			case CI_ARGDESC :
				return CommonMessages.getInstance().ModuleInfoLabel_args_desc;
			case CI_ARGVALUE :
				return CommonMessages.getInstance().ModuleInfoLabel_args_value;
			default :
				return super.getColumnName(columnIndex);
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex >= 0 && rowIndex < _arglist.size()) {
			ModuleArgID argid = _arglist.get(rowIndex);
			if (columnIndex == CI_ARGATTR) {
				ModuleArgType argtype = argid.getArgument().getType();
				return String.format("($%d)%s", argid.getArgument().getArgNo(), (argtype==null ? "" : argtype.toString()));
			}
			else if (columnIndex == CI_ARGDESC) {
				String argdesc = argid.getArgument().getDescription();
				return (argdesc==null ? "" : argdesc);
			}
			else if (columnIndex == CI_ARGVALUE) {
				return MacroSubFilterArgValueTablePane.getDisplayArgumentValueText(_editModel, argid.getData(), argid.getArgument());
			}
		}
		
		return null;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		throw new UnsupportedOperationException();
	}

	//------------------------------------------------------------
	// Implement IModuleArgTableModel interfaces
	//------------------------------------------------------------

	@Override
	public ModuleArgType getArgumentAttr(int rowIndex) {
		if (rowIndex >= 0 && rowIndex < _arglist.size()) {
			return getRowData(rowIndex).getArgument().getType();
		} else {
			return null;
		}
	}

	@Override
	public void addNewRow() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void moveRow(int start, int end, int to) {
		throw new UnsupportedOperationException();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
