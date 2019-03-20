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
 * @(#)ModuleArgDetailTableModel.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.view.table;

import javax.swing.table.DefaultTableModel;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.module.ModuleArgDetail;
import ssac.aadl.module.ModuleArgType;
import ssac.aadl.module.setting.ModuleSettings;

/**
 * モジュール引数定義のテーブルモデル
 * 
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public class ModuleArgDetailTableModel extends DefaultTableModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** 最大カラム数 **/
	static private final int maxColumnCount = 2;
	
	static public final int COL_ATTR = 0;
	static public final int COL_DESC = 1;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ModuleArgDetailTableModel() {
		this(null);
	}
	
	public ModuleArgDetailTableModel(ModuleSettings setting) {
		super(createColumnNames(), 0);
		if (setting != null && !setting.isArgumentEmpty()) {
			int len = setting.getNumArguments();
			for (int i = 0; i < len; i++) {
				ModuleArgDetail desc = setting.getArgument(i);
				addRow(desc);
			}
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == COL_ATTR) {
			return ModuleArgType.class;
		}
		else if (columnIndex == COL_DESC) {
			return String.class;
		}
		else {
			return null;
		}
	}
	
	public void addNewRow() {
		addRow(new Object[]{null, null});
	}
	
	public void addRow(ModuleArgDetail newRowData) {
		addRow(new Object[]{
				newRowData.type(),
				newRowData.description(),
		});
	}
	
	public void storeToSettin(ModuleSettings setting) {
		setting.clearArguments();
		int numRows = getRowCount();
		if (numRows > 0) {
			for (int i = 0; i < numRows; i++) {
				ModuleArgDetail desc = new ModuleArgDetail(getArgumentAttr(i), getArgumentDescription(i));
				setting.addArgument(desc);
			}
		}
	}
	
	public ModuleArgType getArgumentAttr(int rowIndex) {
		return (ModuleArgType)getValueAt(rowIndex, COL_ATTR);
	}
	
	public String getArgumentDescription(int rowIndex) {
		return (String)getValueAt(rowIndex, COL_DESC);
	}
	
	public void setArgumentAttr(int rowIndex, ModuleArgType newAttr) {
		setValueAt(newAttr, rowIndex, COL_ATTR);
	}
	
	public void setArgumentDescription(int rowIndex, String newDesc) {
		setValueAt(newDesc, rowIndex, COL_DESC);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static protected final String[] createColumnNames() {
		String[] names = new String[maxColumnCount];
		names[COL_ATTR] = CommonMessages.getInstance().ModuleInfoLabel_args_attr;
		names[COL_DESC] = CommonMessages.getInstance().ModuleInfoLabel_args_desc;
		return names;
	}
}
