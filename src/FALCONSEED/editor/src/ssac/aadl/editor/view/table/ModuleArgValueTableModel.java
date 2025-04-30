/*
 * @(#)ModuleArgValueTableModel.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.view.table;

import javax.swing.table.DefaultTableModel;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.module.ModuleArgDetail;
import ssac.aadl.module.ModuleArgType;

/**
 * モジュール引数値のテーブルモデル
 * 
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public class ModuleArgValueTableModel extends DefaultTableModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final ModuleArgDetail[] EMPTY_DETAILS = new ModuleArgDetail[0];

	/** 最大カラム数 **/
	static private final int maxColumnCount = 2;
	
	static public final int COL_ATTR = 0;
	static public final int COL_VALUE = 1;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private ModuleArgDetail[] argDetails;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ModuleArgValueTableModel() {
		this(null, null);
	}
	
	public ModuleArgValueTableModel(ModuleArgDetail[] details, String[] values) {
		super(createColumnNames(), 0);
		this.argDetails = (details==null ? EMPTY_DETAILS : details);
		int numValues = (values==null ? 0 : values.length);
		int numDetails = argDetails.length;
		if (numValues > numDetails) {
			int row = 0;
			for (; row < numDetails; row++) {
				addRow(details[row].type(), values[row]);
			}
			for (; row < numValues; row++) {
				addRow(null, values[row]);
			}
		} else {
			// numRows <= numDetails
			int row = 0;
			for (; row < numValues; row++) {
				addRow(details[row].type(), values[row]);
			}
			for (; row < numDetails; row++) {
				addRow(details[row].type(), null);
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
		else if (columnIndex == COL_VALUE) {
			return String.class;
		}
		else {
			return null;
		}
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		// 引数属性列は編集不可
		return (column != COL_ATTR);
	}

	public void addRow(ModuleArgType argType, String argValue) {
		addRow(new Object[]{
				argType,
				argValue,
		});
	}
	
	public void refreshArgumentTypes() {
		updateArgumentDetails(argDetails);
	}
	
	public void updateArgumentDetails(ModuleArgDetail[] details) {
		this.argDetails = (details == null ? EMPTY_DETAILS : details);
		int numRows = getRowCount();
		if (numRows > argDetails.length) {
			int row = 0;
			for (; row < argDetails.length; row++) {
				setValueAt(argDetails[row].type(), row, COL_ATTR);
			}
			for (; row < numRows; row++) {
				setValueAt(null, row, COL_ATTR);
			}
		} else {
			// numRows <= argDetails.length
			int row = 0;
			for (; row < numRows; row++) {
				setValueAt(argDetails[row].type(), row, COL_ATTR);
			}
			for (; row < argDetails.length; row++) {
				addRow(argDetails[row].type(), null);
			}
		}
	}
	
	public String[] getArgumentValues() {
		String[] argValues = null;
		int numRows = getRowCount();
		if (numRows > 0) {
			argValues = new String[numRows];
			for (int row = 0; row < numRows; row++) {
				argValues[row] = (String)getValueAt(row, COL_VALUE);
			}
		}
		return argValues;
	}
	
	public ModuleArgType getArgumentTypeAt(int rowIndex) {
		return (ModuleArgType)getValueAt(rowIndex, COL_ATTR);
	}
	
	public String getArgumentValueAt(int rowIndex) {
		return (String)getValueAt(rowIndex, COL_VALUE);
	}
	
	public void setArgumentValueAt(int rowIndex, String newValue) {
		setValueAt(newValue, rowIndex, COL_VALUE);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static protected final String[] createColumnNames() {
		String[] names = new String[maxColumnCount];
		names[COL_ATTR]  = CommonMessages.getInstance().ModuleInfoLabel_args_attr;
		names[COL_VALUE] = CommonMessages.getInstance().ModuleInfoLabel_args_value;
		return names;
	}
}
