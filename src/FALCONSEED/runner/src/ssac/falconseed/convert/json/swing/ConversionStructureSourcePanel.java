/*
 * @(#)ConversionStructureSourcePanel.java	3.4.0	2020/03/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.convert.json.swing;

import javax.swing.ListSelectionModel;

/**
 * JSON-CSV 変換のための、ソースとなる変換構造テーブル・パネル。
 * 
 * @version 3.4.0
 * @since 3.4.0
 */
public class ConversionStructureSourcePanel<E extends IConversionStructureItem> extends ConversionStructureTablePane<E>
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
	
	public ConversionStructureSourcePanel(ConversionStructureSourceTableModel<E> model) {
		super(model);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	@Override
	public ConversionStructureSourceTableModel<E> getTableModel() {
		return (ConversionStructureSourceTableModel<E>)_table.getModel();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	@Override
	protected void setupTableStyles() {
		_table.setRowSelectionAllowed(true);
		_table.setColumnSelectionAllowed(false);
		_table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
