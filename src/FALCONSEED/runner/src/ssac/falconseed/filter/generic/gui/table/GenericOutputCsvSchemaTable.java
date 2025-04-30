/*
 * @(#)GenericInputCsvSchemaTable.java	3.2.1	2015/07/13
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui.table;

import ssac.util.swing.table.SpreadSheetTable;

/**
 * 汎用フィルタの出力スキーマ編集ダイアログで使用する、CSV 出力スキーマ編集テーブルのコンポーネント。
 * 
 * @version 3.2.1
 * @since 3.2.1
 */
public class GenericOutputCsvSchemaTable extends SpreadSheetTable
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

	public GenericOutputCsvSchemaTable(GenericOutputCsvSchemaTableModel model) {
		super(model);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Implement SpreadSheetTable interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
