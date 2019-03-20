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
 * @(#)AbModuleArgTablePane.java	3.2.1	2015/07/21
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbModuleArgTablePane.java	3.1.0	2014/05/12
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbModuleArgTablePane.java	2.0.0	2012/10/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbModuleArgTablePane.java	1.17	2010/11/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module.swing.table;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import javax.swing.AbstractButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import ssac.util.swing.list.IListControlHandler;
import ssac.util.swing.list.ListController;

/**
 * 実行時引数を表示もしくは設定するテーブルペインの抽象クラス。
 * このコンポーネントは、スクロールペインとテーブルによって構成される。
 * <p>
 * このクラスの利用においては、コンストラクタによるインスタンス生成後、
 * 必ず {@link #initialComponent()} を呼び出すこと。
 * 
 * @version 3.2.1
 * @since 1.17
 */
public abstract class AbModuleArgTablePane extends JScrollPane implements IListControlHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final ListController controller = new ListController(this);
	private final TableModelListener argsListener;

	private IModuleArgTableModel	_model;
	private ModuleArgTable			_table;
	/**
	 * テーブルモデル変更後に全カラム幅の自動調整を行うフラグ
	 * @since 2.0.0
	 */
	private boolean				_flgAdjustAllColumnWidthOnSetModel = true;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AbModuleArgTablePane() {
		super(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		this.argsListener = new TableModelListener(){
			public void tableChanged(TableModelEvent event) {
				onTableModelChanged(event);
			}
		};
	}
	
	public void initialComponent() {
		// create fields
		createFields();
		
		// setup Table
		setupTableComponent();
		
		// setup Layout
		setupLayout();
		
		// setup Actions
		setupActions();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * テーブルモデル変更後に全カラム幅の自動調整を行う場合に <tt>true</tt> を返す。
	 * デフォルトは <tt>true</tt>。
	 * @return toEnable	自動調整を行う場合は <tt>true</tt>、行わない場合は <tt>false</tt>
	 * @since 2.0.0
	 */
	public boolean isAdjustAllColumnWidthOnSetModel() {
		return _flgAdjustAllColumnWidthOnSetModel;
	}

	/**
	 * テーブルモデル変更後に全カラム幅の自動調整を行うかどうかを設定する。
	 * デフォルトは <tt>true</tt>。
	 * @param toEnable	自動調整を行う場合は <tt>true</tt>、行わない場合は <tt>false</tt>
	 * @since 2.0.0
	 */
	public void setAdjustAllColumnWidthOnSetModel(boolean toEnable) {
		_flgAdjustAllColumnWidthOnSetModel = toEnable;
	}
	
	public boolean isEditable() {
		return _table.isEditable();
	}
	
	public void setEditable(boolean editable) {
		_table.setEditable(editable);
	}
	
	public AbstractButton attachAddButton(AbstractButton btn) {
		return this.controller.attachAddButton(btn);
	}
	
	public AbstractButton attachEditButton(AbstractButton btn) {
		return this.controller.attachEditButton(btn);
	}
	
	public AbstractButton attachDeleteButton(AbstractButton btn) {
		return this.controller.attachDeleteButton(btn);
	}
	
	public AbstractButton attachUpButton(AbstractButton btn) {
		return this.controller.attachUpButton(btn);
	}
	
	public AbstractButton attachDownButton(AbstractButton btn) {
		return this.controller.attachDownButton(btn);
	}
	
	public AbstractButton getAddButton() {
		return this.controller.getAddButton();
	}
	
	public AbstractButton getEditButton() {
		return this.controller.getEditButton();
	}
	
	public AbstractButton getDeleteButton() {
		return this.controller.getDeleteButton();
	}
	
	public AbstractButton getUpButton() {
		return this.controller.getUpButton();
	}
	
	public AbstractButton getDownButton() {
		return this.controller.getDownButton();
	}
	
	public IModuleArgTableModel getTableModel() {
		return _model;
	}
	
	public void setTableModel(IModuleArgTableModel newModel) {
		if (newModel == null) {
			newModel = createDefaultTableModel();
		}
		if (newModel == _model) {
			return;
		}
		
		_table.getModel().removeTableModelListener(argsListener);
		_model = newModel;
		_table.setModel(_model);
		_model.addTableModelListener(argsListener);
		
		//--- 行ヘッダの更新
		//======= 自動化されているはずなので、不要
		//--- カラム幅の自動調整
		if (_flgAdjustAllColumnWidthOnSetModel) {
			adjustTableAllColumnsPreferredWidth();
		}
		//--- 関連ボタンの更新
		updateButtons();
	}
	
	public void adjustTableAllColumnsPreferredWidth() {
		_table.adjustAllColumnsPreferredWidth(true, true, getViewport().getWidth());
	}
	
	public int getColumnCount() {
		return _table.getColumnCount();
	}
	
	public int getRowCount() {
		return _table.getRowCount();
	}
	
	public void clearSelection() {
		_table.clearSelection();
	}
	
	public int getSelectedColumnCount() {
		return _table.getSelectedColumnCount();
	}
	
	public int getSelectedRowCount() {
		return _table.getSelectedRowCount();
	}
	
	public int getSelectedColumn() {
		return _table.getSelectedColumn();
	}
	
	public int getSelectedRow() {
		return _table.getSelectedRow();
	}
	
	public void updateButtons() {
		boolean enableAdd		= false;
		boolean enableEdit		= false;
		boolean enableDelete	= false;
		boolean enableUp		= false;
		boolean enableDown		= false;
		
		if (isEnabled()) {
			enableAdd = true;
			int selected = _table.getSelectedRow();
			if (selected >= 0) {
				enableEdit = true;
				enableDelete = true;
				if ((selected - 1) >= 0)
					enableUp = true;
				if ((selected + 1) < _table.getRowCount())
					enableDown = true;
			}
		}
		
		controller.setAddButtonEnabled(enableAdd);
		controller.setEditButtonEnabled(enableEdit);
		controller.setDeleteButtonEnabled(enableDelete);
		controller.setUpButtonEnabled(enableUp);
		controller.setDownButtonEnabled(enableDown);
	}
	
	public void stopTableCellEditing() {
		if (_table.isEditing()) {
			_table.getCellEditor().stopCellEditing();
		}
	}

	/**
	 * テーブルコンポーネントの先頭行が表示されるようにスクロールする。
	 * @since 2.0.0
	 */
	public void scrollTopRowToVisible() {
		scrollRowToVisible(0);
	}

	/**
	 * テーブルコンポーネントの終端行が表示されるようにスクロールする。
	 * @since 2.0.0
	 */
	public void scrollBottomRowToVisible() {
		scrollRowToVisible(_table.getRowCount()-1);
	}
	
	/**
	 * テーブルコンポーネントの指定された位置の行が表示されるようにスクロールする。
	 * ただし、インデックスが無効な場合、このメソッドはなにもしない。
	 * @param rowIndex	行インデックス
	 * @since 2.0.0
	 */
	public void scrollRowToVisible(int rowIndex) {
		if (rowIndex >= 0 && rowIndex < _table.getRowCount()) {
			Rectangle rc = _table.getVisibleRect();
			Rectangle rcCell = _table.getCellRect(rowIndex, 0, false);
			rc.y = rcCell.y;
			rc.height = rcCell.height;
			_table.scrollRectToVisible(rc);
		}
	}

	/**
	 * 指定された行と列の位置にあるセルが表示されるようにスクロールする。
	 * @param rowIndex		行インデックス
	 * @param columnIndex	列インデックス
	 * @since 2.0.0
	 */
	public void scrollToVisibleCell(int rowIndex, int columnIndex) {
		_table.scrollToVisibleCell(rowIndex, columnIndex);
	}

	//------------------------------------------------------------
	// Implements IListControlHandler interfaces
	//------------------------------------------------------------

	public void onButtonAdd(ActionEvent ae) {
		stopTableCellEditing();
		_model.addNewRow();
		int selected = _table.getRowCount() - 1;
		_table.setRowSelectionInterval(selected, selected);
	}

	public void onButtonEdit(ActionEvent ae) {
		// No Entry
	}

	public void onButtonDelete(ActionEvent ae) {
		stopTableCellEditing();
		int selected = getSelectedRow();
		if (selected >= 0 && canDeleteRow(selected)) {
			_model.removeRow(selected);
			int maxidx = _model.getRowCount() - 1;
			if (selected > maxidx)
				selected = maxidx;
			if (selected >= 0)
				_table.setRowSelectionInterval(selected, selected);
			updateButtons();
		}
	}
	
	public void onButtonClear(ActionEvent ae) {
		// No Entry
	}

	public void onButtonUp(ActionEvent ae) {
		stopTableCellEditing();
		int selected = getSelectedRow();
		if (selected > 0) {
			int prev = selected - 1;
			_model.moveRow(selected, selected, prev);
			_table.setRowSelectionInterval(prev, prev);
			updateButtons();
		}
	}

	public void onButtonDown(ActionEvent ae) {
		stopTableCellEditing();
		int selected = getSelectedRow();
		int next = selected + 1;
		if (selected >= 0 && next < _model.getRowCount()) {
			_model.moveRow(selected, selected, next);
			_table.setRowSelectionInterval(next, next);
			updateButtons();
		}
	}

	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------

	/**
	 * [削除]ボタンによる指定行削除を許可する場合に <tt>true</tt> を返す。
	 * @param rowIndex	これから削除する行のインデックス
	 * @return	削除を許可する場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 2.0.0
	 */
	protected boolean canDeleteRow(int rowIndex) {
		return true;
	}
	
	protected void onTableModelChanged(TableModelEvent event) {
		if (event.getType() == TableModelEvent.UPDATE) {
			updateButtons();
			if (event.getColumn() != 0) {
				// 属性以外の列が更新された場合のみ、列幅を調整する
				adjustTableAllColumnsPreferredWidth();
			}
		}
	}
	
	protected void onTableSelectionChanged(ListSelectionEvent event) {
		//int selected = getSelectedRow();
		//if (selected >= 0) {
		//	scrollToVisibleRow(selected);
		//}
		updateButtons();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected abstract IModuleArgTableModel createDefaultTableModel();
	
	protected ModuleArgTable createTable() {
		return new ModuleArgTable();
	}
	
	protected void createFields() {
		_model = createDefaultTableModel();
		_table = createTable();
	}
	
	protected ModuleArgTable getTableComponent() {
		return _table;
	}
	
	protected void setupTableComponent() {
		_table.setModel(_model);
		//--- setup table parameters
		_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_table.setColumnSelectionAllowed(false);
		_table.setRowSelectionAllowed(true);
		_table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		_table.getTableHeader().setReorderingAllowed(false);
		_table.getTableHeader().setResizingAllowed(true);
		
		// テーブルの罫線を表示(Mac は白で書いております)
		_table.setShowGrid(true);
		_table.setGridColor(new Color(128,128,128));
	}
	
	protected void setupLayout() {
		// setup ScrollPane
		this.setViewportView(_table);
		this.setRowHeaderView(_table.getTableRowHeader());
		//--- setup corner
		JPanel borderPanel = new JPanel();
		borderPanel.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		this.setCorner(JScrollPane.UPPER_LEFT_CORNER, borderPanel);
	}
	
	protected void setupActions() {
		// setup actions
		_table.getModel().addTableModelListener(argsListener);
		ListSelectionListener lsl = new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent event) {
				onTableSelectionChanged(event);
			}
		};
		_table.getSelectionModel().addListSelectionListener(lsl);
	}
	
	//protected void scrollToVisibleRow(int rowIndex) {
	//	Rectangle rcCell = table.getCellRect(rowIndex, 0, false);
	//	table.scrollRectToVisible(rcCell);
	//}
}
