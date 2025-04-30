/*
 * @(#)AbConversionStructureTableModel.java	3.4.0	2020/03/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.convert.json.swing;

import java.util.ArrayList;

import ssac.util.swing.table.AbSpreadSheetTableModel;

/**
 * JSON-CSV 変換のための、変換構造テーブル・モデル。
 * 
 * @version 3.4.0
 * @since 3.4.0
 */
public abstract class AbConversionStructureTableModel<E extends IConversionStructureItem> extends AbSpreadSheetTableModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	protected ArrayList<E>	_listItems;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AbConversionStructureTableModel() {
		super();
		_listItems = new ArrayList<E>();
	}
	
	public AbConversionStructureTableModel(int capacity) {
		super();
		_listItems = new ArrayList<E>(capacity);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isEmpty() {
		return _listItems.isEmpty();
	}
	
	public void clear() {
		if (!_listItems.isEmpty()) {
			int lastIndex = _listItems.size() - 1;
			//--- リンクを解除
			for (E item : _listItems) {
				item.setAttachedItem(null);
			}
			_listItems.clear();
			fireTableRowsDeleted(0, lastIndex);
		}
	}
	
	public int getItemCount() {
		return _listItems.size();
	}
	
	public E getItem(int index) {
		return _listItems.get(index);
	}
	
	public void addItem(E newItem) {
		//newItem.refreshDisplayValues();
		int rowIndex = _listItems.size();
		_listItems.add(newItem);
		fireTableRowsInserted(rowIndex, rowIndex);
	}
	
	public void insertItem(int index, E newItem) {
		//newItem.refreshDisplayValues();
		_listItems.add(index, newItem);
		fireTableRowsInserted(index, index);
	}
	
	public E setItem(int index, E newItem) {
		//newItem.refreshDisplayValues();
		E oldItem = _listItems.set(index, newItem);
		fireTableRowsUpdated(index, index);
		return oldItem;
	}
	
	public E removeItem(int index) {
		E removed = _listItems.remove(index);
		if (removed != null) {
			removed.setAttachedItem(null);	// リンクを解除
		}
		fireTableRowsDeleted(index, index);
		return removed;
	}

	//------------------------------------------------------------
	// Implement TableModel interfaces
	//------------------------------------------------------------

	@Override
	public String getRowName(int rowIndex) {
		if (rowIndex >= 0 && rowIndex < _listItems.size()) {
			String str = _listItems.get(rowIndex).getRowName();
			if (str != null) {
				return str;
			}
		}
		
		return super.getRowName(rowIndex);
	}

	@Override
	public int getRowCount() {
		return _listItems.size();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
