/*
 * @(#)ConversionStructureTable.java	3.4.0	2020/03/08
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.convert.json.swing;

import javax.swing.ListSelectionModel;

import ssac.falconseed.filter.generic.gui.table.GenericOutputCsvSchemaTable;
import ssac.util.swing.table.SpreadSheetTable;

/**
 * JSON-CSV 変換のための、変換構造テーブル・コンポーネント。
 * 
 * @version 3.4.0
 * @since 3.4.0
 */
public class ConversionStructureTable extends SpreadSheetTable
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
	
	public ConversionStructureTable(AbConversionStructureTableModel<?> model) {
		super(model);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
