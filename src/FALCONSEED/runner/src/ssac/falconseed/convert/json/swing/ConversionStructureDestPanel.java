/*
 * @(#)ConversionStructureDestPanel.java	3.4.0	2020/03/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.convert.json.swing;

import javax.swing.ListSelectionModel;

/**
 * JSON-CSV 変換のための、変換結果構造テーブル・パネル。
 * 
 * @version 3.4.0
 * @since 3.4.0
 */
public class ConversionStructureDestPanel<E extends AbConversionStructureItem> extends ConversionStructureTablePane<E>
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
	
	public ConversionStructureDestPanel(ConversionStructureDestTableModel<E> model) {
		super(model);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	@Override
	public ConversionStructureDestTableModel<E> getTableModel() {
		return (ConversionStructureDestTableModel<E>)_table.getModel();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	@Override
	protected void setupTableStyles() {
		_table.setRowSelectionAllowed(true);
		_table.setColumnSelectionAllowed(true);
		_table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
