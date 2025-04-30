/*
 * @(#)ExTable.java	4.0.0	2021/08/23
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExTable.java	2.00	2012/09/25
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.table;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * <code>JTable</code> の拡張機能の実装。
 * このクラスのコンストラクタを呼び出した直後は、必ず {@link #initialComponent()} を呼び出すこと。
 * {@link #initialComponent()} の呼び出しによって、ダイアログ内のコンポーネントが初期化される。
 * </blockquote>
 * 
 * @version 4.0.0
 * @since 2.00
 */
public class ExTable extends JTable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public ExTable() {
		super();
	}

	public ExTable(int numRows, int numColumns) {
		super(numRows, numColumns);
	}

	public ExTable(Object[][] rowData, Object[] columnNames) {
		super(rowData, columnNames);
	}

	public ExTable(TableModel dm) {
		super(dm);
	}

	public ExTable(TableModel dm, TableColumnModel cm) {
		super(dm, cm);
	}

	public ExTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
		super(dm, cm, sm);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public JTableHeader getTableColumnHeader() {
		return getTableHeader();
	}
	
	public Object getTableRowHeader() {
		return null;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	@Override
	protected JTableHeader createDefaultTableHeader() {
		return new ExTableHeader(columnModel);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
