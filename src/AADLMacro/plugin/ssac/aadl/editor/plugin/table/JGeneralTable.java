/*
 * @(#)GeneralTableModel.java	1.00	2008/11/21
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.plugin.table;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * 汎用的なテーブル。
 * 
 * @version 1.00	2008/11/21
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 *
 * @since 1.00
 */
public class JGeneralTable extends JTable
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

	public JGeneralTable() {
		super();
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public JGeneralTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
		super(dm, cm, sm);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public JGeneralTable(TableModel dm, TableColumnModel cm) {
		super(dm, cm);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public JGeneralTable(TableModel dm) {
		super(dm);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
