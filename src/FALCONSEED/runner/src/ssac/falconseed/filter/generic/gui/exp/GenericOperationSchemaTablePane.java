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
 *  Copyright 2007-2015  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)GenericOperationSchemaTablePane.java	3.2.1	2015/07/21
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)GenericOperationSchemaTablePane.java	3.2.0	2015/06/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui.exp;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ssac.aadl.fs.module.schema.SchemaValueObject;
import ssac.falconseed.filter.generic.gui.GenericFilterEditModel;
import ssac.util.swing.table.SpreadSheetRowHeader;
import ssac.util.swing.table.SpreadSheetTable;

/**
 * 条件や式のテーブルパネルの基本実装。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public abstract class GenericOperationSchemaTablePane extends JPanel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	
	static protected final int	EMPTY_ROWHEADER_WIDTH	= 30;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 初期化フラグ **/
	private boolean	_initialized;

	/** 汎用フィルタ編集用データモデル **/
	protected GenericFilterEditModel		_editModel;

	/** テーブルコンポーネント **/
	protected GenericOperationSchemaTable	_table;
	
	protected JScrollPane					_scroll;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public GenericOperationSchemaTablePane() {
		super(new BorderLayout());
	}
	
	public void initialComponent(GenericFilterEditModel editModel, GenericOperationSchemaTableModel<? extends SchemaValueObject> model) {
		if (editModel == null)
			throw new NullPointerException();
		_editModel = editModel;
		
		// create components
		_table = createTable(model);
		
		// layout components
		_scroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		_scroll.setViewportView(_table);
		JPanel cornerPanel = SpreadSheetTable.createUpperLeftCornerComponent();
		_scroll.setCorner(JScrollPane.UPPER_LEFT_CORNER, cornerPanel);
		SpreadSheetRowHeader rowHeader = _table.getTableRowHeader();
		if (_table.getRowCount() == 0) {
			rowHeader.setFixedCellWidth(EMPTY_ROWHEADER_WIDTH);
		}
		_scroll.setRowHeaderView(rowHeader);
		add(_scroll, BorderLayout.CENTER);
		
		// accomplished
		_initialized = true;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public String getTitle() {
		ensureInitialized();
		return _table.getTitle();
	}
	
	public GenericFilterEditModel getEditModel() {
		return _editModel;
	}
	
	public abstract GenericOperationSchemaPropertyPane getLocalPropertyPane();
	
	public GenericOperationSchemaTable getTableComponent() {
		return _table;
	}

	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------

	/**
	 * ダイアログが表示される直前に呼び出されるイベントハンドラ。
	 */
	public void onInitDialog() {
		//--- テーブル列のサイズ調整
		if (_table.getRowCount() > 0) {
			_table.adjustAllColumnsPreferredWidth();
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void ensureInitialized() {
		if (!_initialized) {
			throw new IllegalStateException("Components are not initialized.");
		}
	}
	
	protected GenericOperationSchemaTable createTable(GenericOperationSchemaTableModel<? extends SchemaValueObject> model) {
		return new GenericOperationSchemaTable(model);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
