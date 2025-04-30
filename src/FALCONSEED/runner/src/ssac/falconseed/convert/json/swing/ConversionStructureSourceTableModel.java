/*
 * @(#)ConversionStructureSourceTableModel.java	3.4.0	2020/03/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.convert.json.swing;

import java.util.ArrayList;

import ssac.falconseed.runner.RunnerMessages;

/**
 * JSON-CSV 変換における、ソースとなる変換構造テーブル・モデル。
 * 
 * @version 3.4.0
 * @since 3.4.0
 */
public class ConversionStructureSourceTableModel<E extends IConversionStructureItem> extends AbConversionStructureTableModel<E>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;
	
	static public final int	CI_NUMBER	= -1;
	static public final int	CI_NAME		= 0;
	static public final int	CI_DATATYPE	= 1;
	
	static public final String[] COLUMN_NAMES = {
		RunnerMessages.getInstance().ConversionStructureTableColumn_name,
		RunnerMessages.getInstance().ConversionStructureTableColumn_datatype,
	};

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ConversionStructureSourceTableModel() {
		super();
	}
	
	public ConversionStructureSourceTableModel(int capacity) {
		super(capacity);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Implement AbstractTableModel interfaces
	//------------------------------------------------------------

	@Override
	public String getColumnName(int column) {
		if (column >= 0 && column < COLUMN_NAMES.length) {
			return COLUMN_NAMES[column];
		}
		else {
			return super.getColumnName(column);
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex >= 0 && columnIndex < COLUMN_NAMES.length) {
			return String.class;
		}
		else {
			return super.getColumnClass(columnIndex);
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		// すべて編集不可
		return false;
	}

	//------------------------------------------------------------
	// Implement TableModel interfaces
	//------------------------------------------------------------

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex >= 0 && rowIndex < _listItems.size()) {
			E item = _listItems.get(rowIndex);
			if (columnIndex == CI_NAME) {
				return item.getDisplayName();
			}
			else if (columnIndex == CI_DATATYPE) {
				return item.getDisplayDataTypeString();
			}
		}
		
		// unmatched
		return null;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
