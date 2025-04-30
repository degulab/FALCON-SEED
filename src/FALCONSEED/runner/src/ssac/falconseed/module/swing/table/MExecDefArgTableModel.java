/*
 * @(#)MExecDefArgTableModel.java	2.0.0	2012/10/18
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecDefArgTableModel.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing.table;

import javax.swing.table.DefaultTableModel;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.module.ModuleArgType;

/**
 * モジュール実行定義編集における引数設定専用のテーブルモデル。
 * 
 * @version 2.0.0	2012/10/18
 */
public class MExecDefArgTableModel extends DefaultTableModel implements IMExecDefArgTableModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** 属性の標準列インデックス **/
	static protected final int COL_ATTR  = 0;
	/** 説明の標準列インデックス **/
	static protected final int COL_DESC  = 1;
	/** 値の標準列インデックス **/
	static protected final int COL_VALUE = 2;

	/** 標準のカラム数 **/
	static private final int defaultColumnCount = 3;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MExecDefArgTableModel() {
		this(createDefaultColumnNames());
	}
	
	protected MExecDefArgTableModel(String[] columnNames) {
		super(columnNames, 0);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	@Override
	public boolean isAttributeColumnIndex(int columnIndex) {
		return (COL_ATTR == columnIndex);
	}

	@Override
	public boolean isDescriptionColumnIndex(int columnIndex) {
		return (COL_DESC == columnIndex);
	}

	@Override
	public boolean isValueColumnIndex(int columnIndex) {
		return (COL_VALUE == columnIndex);
	}
	
	public final int attributeColumnIndex() {
		return COL_ATTR;
	}

	public int descriptionColumnIndex() {
		return COL_DESC;
	}
	
	public int valueColumnIndex() {
		return COL_VALUE;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == attributeColumnIndex()) {
			return ModuleArgType.class;
		}
		else if (columnIndex == descriptionColumnIndex()) {
			return String.class;
		}
		else if (columnIndex == valueColumnIndex()) {
			return String.class;
		}
		else {
			return null;
		}
	}

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
			// 値列以外は編集可能
			return true;
		}
	}
	
	public void addNewRow() {
		addRow(new Object[getColumnCount()]);
	}
	
	public void addRow(ModuleArgType attr, String desc, Object value) {
		addRow(new Object[]{attr, desc, value});
	}

	public void insertRow(int rowIndex, ModuleArgType attr, String desc, Object value) {
		insertRow(rowIndex, new Object[]{attr, desc, value});
	}
	
	public ModuleArgType getArgumentAttr(int rowIndex) {
		return (ModuleArgType)getValueAt(rowIndex, attributeColumnIndex());
	}
	
	public String getArgumentDescription(int rowIndex) {
		return (String)getValueAt(rowIndex, descriptionColumnIndex());
	}
	
	public Object getArgumentValue(int rowIndex) {
		return getValueAt(rowIndex, valueColumnIndex());
	}
	
	public void setArgumentAttr(int rowIndex, ModuleArgType newAttr) {
		setValueAt(newAttr, rowIndex, attributeColumnIndex());
	}
	
	public void setArgumentDescription(int rowIndex, String newDesc) {
		setValueAt(newDesc, rowIndex, descriptionColumnIndex());
	}
	
	public void setArgumentValue(int rowIndex, Object newValue) {
		setValueAt(newValue, rowIndex, valueColumnIndex());
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static private final String[] createDefaultColumnNames() {
		String[] names = new String[defaultColumnCount];
		names[COL_ATTR]  = CommonMessages.getInstance().ModuleInfoLabel_args_attr;
		names[COL_DESC]  = CommonMessages.getInstance().ModuleInfoLabel_args_desc;
		names[COL_VALUE] = CommonMessages.getInstance().ModuleInfoLabel_args_value;
		return names;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
