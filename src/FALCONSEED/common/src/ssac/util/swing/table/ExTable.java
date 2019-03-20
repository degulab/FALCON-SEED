/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  Copyright 2007-2012  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)ExTable.java	2.00	2012/09/25
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.table;

import java.util.Vector;

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
 * @version 2.00	2012/09/25
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

	public ExTable(Vector<?> rowData, Vector<?> columnNames) {
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
