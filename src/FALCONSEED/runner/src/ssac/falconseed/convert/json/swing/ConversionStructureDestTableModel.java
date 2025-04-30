/*
 * @(#)ConversionStructureDestTableModel.java	3.4.0	2020/03/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.convert.json.swing;

import java.util.Collection;
import java.util.Objects;

import ssac.falconseed.runner.RunnerMessages;

/**
 * JSON-CSV 変換における、変換結果となる変換構造テーブル・モデル。
 * 
 * @version 3.4.0
 * @since 3.4.0
 */
public class ConversionStructureDestTableModel<E extends IConversionStructureItem> extends AbConversionStructureTableModel<E>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;
	
	static public final int	CI_NUMBER	= -1;
	static public final int	CI_NAME		= 0;
	static public final int	CI_SOURCE	= 1;
	static public final int	CI_DATATYPE	= 2;
	
	static public final String[] COLUMN_NAMES = {
		RunnerMessages.getInstance().ConversionStructureTableColumn_name,
		RunnerMessages.getInstance().ConversionStructureTableColumn_source,
		RunnerMessages.getInstance().ConversionStructureTableColumn_datatype,
	};

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public ConversionStructureDestTableModel() {
		super();
	}
	
	public ConversionStructureDestTableModel(int capacity) {
		super(capacity);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	public void addAllItems(Collection<? extends IConversionStructureItem> items) {
		int startItemIndex = _listItems.size();
		for (IConversionStructureItem item : items) {
			_listItems.add((E)item);
		}
		int endItemIndex = _listItems.size() - 1;
		fireTableRowsInserted(startItemIndex, endItemIndex);
	}

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
		// 名前のみ編集可
		return (columnIndex == CI_NAME);
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
			else if (columnIndex == CI_SOURCE) {
				if (item.hasAttachedItem()) {
					String strRowName = item.getAttachedItem().getRowName();
					String strDspName = item.getAttachedItem().getDisplayName();
					return String.format("[%s] %s", (strRowName==null ? "" : strRowName), (strDspName==null ? "" : strDspName));
				}
			}
			else if (columnIndex == CI_DATATYPE) {
				if (item.hasAttachedItem()) {
					return item.getAttachedItem().getDisplayDataTypeString();
				} else {
					return item.getDisplayDataTypeString();
				}
			}
		}
		return null;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// 名前のみ編集可能
		if (rowIndex >= 0 && rowIndex < _listItems.size() && columnIndex == CI_NAME) {
			E item = _listItems.get(rowIndex);
			String strValue = (aValue==null ? null : aValue.toString());
			if (strValue != null && strValue.isEmpty())
				strValue = null;
			String oldValue = item.getDisplayName();
			if (!Objects.equals(strValue, oldValue)) {
				item.setCustomName(strValue);
				fireTableCellUpdated(rowIndex, columnIndex);
			}
		}
		else {
			super.setValueAt(aValue, rowIndex, columnIndex);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
