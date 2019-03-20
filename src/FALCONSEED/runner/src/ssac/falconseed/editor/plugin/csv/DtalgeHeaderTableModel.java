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
 * @(#)DtalgeHeaderTableModel.java	1.21	2012/06/06
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtalgeHeaderTableModel.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.editor.plugin.csv;

import javax.swing.table.DefaultTableModel;

/**
 * データ代数CSV標準形のヘッダー情報を保持するテーブルモデル。
 * 
 * @version 1.21	2012/06/06
 */
public class DtalgeHeaderTableModel extends DefaultTableModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** NULL指定の標準列インデックス **/
	static protected final int COL_NULL		= 0;
	/** 名前キーの標準列インデックス **/
	static protected final int COL_NAME		= 1;
	/** データ型キーの標準列インデックス **/
	static protected final int COL_TYPE		= 2;
	/** 属性キーの標準列インデックス **/
	static protected final int COL_ATTR		= 3;
	/** 主体キーの標準列インデックス **/
	static protected final int COL_SUBJECT	= 4;

	/** 標準のカラム数 **/
	static private final int defaultColumnCount = 5;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public DtalgeHeaderTableModel() {
		this(createDefaultColumnNames());
	}
	
	protected DtalgeHeaderTableModel(Object[] columnNames) {
		super(columnNames, 0);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public int nullStateColumnIndex() {
		return COL_NULL;
	}
	
	public int nameColumnIndex() {
		return COL_NAME;
	}
	
	public int typeColumnIndex() {
		return COL_TYPE;
	}
	
	public int attrColumnIndex() {
		return COL_ATTR;
	}
	
	public int subjectColumnIndex() {
		return COL_SUBJECT;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == typeColumnIndex()) {
			return DtalgeDataTypes.class;
		}
		else if (columnIndex == nullStateColumnIndex()) {
			return Boolean.class;
		}
		else if (0 <= columnIndex && columnIndex < getColumnCount()) {
			return String.class;
		}
		else {
			return null;
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		// 編集可能
		return true;
	}
	
	public void addRow(String name, DtalgeDataTypes type, String attr, String subject) {
		addRow(new Object[]{false, name, type, attr, subject});
	}
	
	public boolean getNullState(int rowIndex) {
		return (Boolean)getValueAt(rowIndex, nullStateColumnIndex());
	}
	
	public String getNameKey(int rowIndex) {
		return (String)getValueAt(rowIndex, nameColumnIndex());
	}
	
	public DtalgeDataTypes getTypeKey(int rowIndex) {
		return (DtalgeDataTypes)getValueAt(rowIndex, typeColumnIndex());
	}
	
	public String getAttributeKey(int rowIndex) {
		return (String)getValueAt(rowIndex, attrColumnIndex());
	}
	
	public String getSubjectKey(int rowIndex) {
		return (String)getValueAt(rowIndex, subjectColumnIndex());
	}
	
	public void setNullState(int rowIndex, boolean useNull) {
		setValueAt(useNull, rowIndex, nullStateColumnIndex());
	}
	
	public void setNameKey(int rowIndex, String newName) {
		setValueAt(newName, rowIndex, nameColumnIndex());
	}
	
	public void setTypeKey(int rowIndex, DtalgeDataTypes newType) {
		setValueAt(newType, rowIndex, typeColumnIndex());
	}
	
	public void setAttributeKey(int rowIndex, String newAttr) {
		setValueAt(newAttr, rowIndex, attrColumnIndex());
	}
	
	public void setSubjectKey(int rowIndex, String newSubject) {
		setValueAt(newSubject, rowIndex, subjectColumnIndex());
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static private final String[] createDefaultColumnNames() {
		String[] names = new String[defaultColumnCount];
		names[COL_NULL]     = CsvFileMessages.getInstance().DtalgeNullState;
		names[COL_NAME]		= CsvFileMessages.getInstance().DtalgeNameKey;
		names[COL_TYPE]		= CsvFileMessages.getInstance().DtalgeTypeKey;
		names[COL_ATTR]		= CsvFileMessages.getInstance().DtalgeAttrKey;
		names[COL_SUBJECT]	= CsvFileMessages.getInstance().DtalgeSubjectKey;
		return names;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
