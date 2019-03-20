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
 *  Copyright 2007-2012  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)IMacroFilterArgValueTableModel.java	2.0.0	2012/10/18
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing.table;

import javax.swing.table.AbstractTableModel;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.module.ModuleArgType;

/**
 * モジュール実行定義の引数定義用テーブルのデータモデル・インタフェース。
 * 
 * @version 2.0.0	2012/10/18
 * @since 2.0.0
 */
public abstract class AbMExecDefArgTableModel extends AbstractTableModel implements IMExecDefArgTableModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** 属性の標準列インデックス **/
	static public final int COL_ATTR  = 0;
	/** 説明の標準列インデックス **/
	static public final int COL_DESC  = 1;
	/** 値の標準列インデックス **/
	static public final int COL_VALUE = 2;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static private final String[]		_colNames = {
		CommonMessages.getInstance().ModuleInfoLabel_args_attr,
		CommonMessages.getInstance().ModuleInfoLabel_args_desc,
		CommonMessages.getInstance().ModuleInfoLabel_args_value,
	};

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 引数属性列のインデックスであれば <tt>true</tt> を返す。
	 */
	public boolean isAttributeColumnIndex(int columnIndex) {
		return (attributeColumnIndex() == columnIndex);
	}

	/**
	 * 引数説明列のインデックスであれば <tt>true</tt> を返す。
	 */
	public boolean isDescriptionColumnIndex(int columnIndex) {
		return (descriptionColumnIndex() == columnIndex);
	}

	/**
	 * 引数値列のインデックスであれば <tt>true</tt> を返す。
	 */
	public boolean isValueColumnIndex(int columnIndex) {
		return (valueColumnIndex() == columnIndex);
	}

	/**
	 * 引数属性の列インデックスを返す。
	 */
	public int attributeColumnIndex() {
		return COL_ATTR;
	}

	/**
	 * 引数説明の列インデックスを返す。
	 */
	public int descriptionColumnIndex() {
		return COL_DESC;
	}

	/**
	 * 引数値の列インデックスを返す。
	 */
	public int valueColumnIndex() {
		return COL_VALUE;
	}

	/**
	 * 列数を返す。
	 */
	public int getColumnCount() {
		return _colNames.length;
	}

	/**
	 * 指定された列インデックスの列名を返す。
	 * @param columnIndex	列インデックス
	 * @return	列名
	 */
	public String getColumnName(int columnIndex) {
		if (0 <= columnIndex && columnIndex < _colNames.length) {
			return _colNames[columnIndex];
		} else {
			return super.getColumnName(columnIndex);
		}
	}
	
	/**
	 * 指定された列のデータ型を返す。
	 * @param columnIndex	列インデックス
	 * @return	列のデータ型
	 */
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == attributeColumnIndex()) {
			return ModuleArgType.class;
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

	public boolean isCellEditable(int rowIndex, int columnIndex) {
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
		else {
			// 値列以外は編集可能
			return true;
		}
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == valueColumnIndex()) {
			return getArgumentValue(rowIndex);
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
	
	public void fireArgumentAttrUpdated(int rowIndex) {
		fireTableCellUpdated(rowIndex, attributeColumnIndex());
	}
	
	public void fireArgumentDescriptionUpdated(int rowIndex) {
		fireTableCellUpdated(rowIndex, descriptionColumnIndex());
	}
	
	public void fireArgumentValueUpdated(int rowIndex) {
		fireTableCellUpdated(rowIndex, valueColumnIndex());
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
