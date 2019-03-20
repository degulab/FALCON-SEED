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
 * @(#)MacroSubFilterArgValueTableModel.java	3.1.0	2014/05/18
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MacroSubFilterArgValueTableModel.java	2.0.0	2012/10/18
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing.table;

import ssac.aadl.module.ModuleArgType;
import ssac.falconseed.module.IModuleArgConfig;

/**
 * マクロフィルタを構成するサブフィルタの引数値設定用テーブルモデルの共通実装。
 * 
 * @version 3.1.0	2014/05/18
 * @since 2.0.0
 */
public abstract class AbMacroSubFilterArgValueTableModel extends AbMExecDefArgTableModel implements IMacroSubFilterArgValueTableModel
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
			// 値列以外は編集不可
			return false;
		}
	}

	@Override
	public ModuleArgType getArgumentAttr(int rowIndex) {
		return getArgument(rowIndex).getType();
	}

	@Override
	public String getArgumentDescription(int rowIndex) {
		return getArgument(rowIndex).getDescription();
	}

	@Override
	public IModuleArgConfig getArgumentValue(int rowIndex) {
		// この呼び出しによる取得される値は、引数の値オブジェクトではなく、値を保持する引数定義そのものである必要がある
		return getArgument(rowIndex);
	}

	@Override
	public void setArgumentAttr(int rowIndex, ModuleArgType newAttr) {
		throw new UnsupportedOperationException(getClass().getName() + "#setArgumentAttr() not supported!");
	}

	@Override
	public void setArgumentDescription(int rowIndex, String newDesc) {
		throw new UnsupportedOperationException(getClass().getName() + "#setArgumentDescription() not supported!");
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		if (columnIndex == valueColumnIndex()) {
			setArgumentValue(rowIndex, value);
		} else {
			throw new UnsupportedOperationException(getClass().getName() + "#setArgumentValue() not supported!");
		}
	}

	@Override
	public void addNewRow() {
		throw new UnsupportedOperationException(getClass().getName() + "#addNewRow() not supported!");
	}

	@Override
	public void moveRow(int start, int end, int to) {
		throw new UnsupportedOperationException(getClass().getName() + "#moveRow() not supported!");
	}

	@Override
	public void removeRow(int row) {
		throw new UnsupportedOperationException(getClass().getName() + "#removeRow() not supported!");
	}

	@Override
	public void addRow(ModuleArgType attr, String desc, Object value) {
		throw new UnsupportedOperationException(getClass().getName() + "#addRow() not supported!");
	}

	@Override
	public void insertRow(int rowIndex, ModuleArgType attr, String desc, Object value) {
		throw new UnsupportedOperationException(getClass().getName() + "#insertRow() not supported!");
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
