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
 * @(#)ModuleArgDetailTablePane.java	2.0.0	2012/10/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.view.table;

import java.awt.Color;
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

import ssac.aadl.module.swing.table.ModuleArgTable;
import ssac.util.swing.list.IListControlHandler;
import ssac.util.swing.list.ListController;

/**
 * モジュール引数設定テーブルを持つペインコンポーネント。
 * テーブルにはスクロールバーが付加される。
 * 
 * @version 2.0.0	2012/10/19
 * @since 1.14
 */
public class ModuleArgDetailTablePane extends JScrollPane implements IListControlHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final ListController controller = new ListController(this);
	
	private final TableModelListener argsListener;
	
	private ModuleArgDetailTableModel	argsModel;
	private ModuleArgTable				table;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ModuleArgDetailTablePane() {
		super(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		this.argsListener = new TableModelListener(){
			public void tableChanged(TableModelEvent event) {
				onTableModelChanged(event);
			}
		};
	}
	
	public void initialComponent() {
		// setup table
		argsModel = createDefaultTableModel();
		table = createTable();
		table.setModel(argsModel);
		//--- setup table parameters
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setColumnSelectionAllowed(false);
		table.setRowSelectionAllowed(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(true);
		
		// setup ScrollPane
		this.setViewportView(table);
		this.setRowHeaderView(table.getTableRowHeader());
		//--- setup corner
		JPanel borderPanel = new JPanel();
		borderPanel.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		this.setCorner(JScrollPane.UPPER_LEFT_CORNER, borderPanel);
		
		// テーブルの罫線を表示(Mac は白で書いてやがる)
		table.setShowGrid(true);
		table.setGridColor(new Color(128,128,128));
		
		// setup actions
		table.getModel().addTableModelListener(argsListener);
		ListSelectionListener lsl = new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent event) {
				onTableSelectionChanged(event);
			}
		};
		table.getSelectionModel().addListSelectionListener(lsl);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isEditable() {
		return table.isEditable();
	}
	
	public void setEditable(boolean editable) {
		table.setEditable(editable);
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

	/*
	public String getDefaultArgumentPath() {
		return this.defArgPath;
	}
	
	public void setDefaultArgumentPath(String path) {
		if (Strings.isNullOrEmpty(path)) {
			this.defArgPath = (new File("")).getAbsolutePath();
		} else {
			this.defArgPath = path;
		}
	}
	
	public String getLastArgumentPath() {
		return this.lastArgPath;
	}
	
	public void setLastArgumentPath(String path) {
		this.lastArgPath = path;
	}
	*/
	
	public ModuleArgDetailTableModel getTableModel() {
		return argsModel;
	}
	
	public void setTableModel(ModuleArgDetailTableModel newModel) {
		if (newModel == null) {
			newModel = createDefaultTableModel();
		}
		if (newModel == argsModel) {
			return;
		}
		
		table.getModel().removeTableModelListener(argsListener);
		argsModel = newModel;
		table.setModel(argsModel);
		argsModel.addTableModelListener(argsListener);
		
		//--- 行ヘッダの更新
		//======= 自動化されているはずなので、不要
		//--- 関連ボタンの更新
		updateButtons();
	}
	
	public void adjustTableAllColumnsPreferredWidth() {
		table.adjustAllColumnsPreferredWidth(true, true, getViewport().getWidth());
	}
	
	public int getColumnCount() {
		return table.getColumnCount();
	}
	
	public int getRowCount() {
		return table.getRowCount();
	}
	
	public void clearSelection() {
		table.clearSelection();
	}
	
	public int getSelectedColumnCount() {
		return table.getSelectedColumnCount();
	}
	
	public int getSelectedRowCount() {
		return table.getSelectedRowCount();
	}
	
	public int getSelectedColumn() {
		return table.getSelectedColumn();
	}
	
	public int getSelectedRow() {
		return table.getSelectedRow();
	}
	
	public void updateButtons() {
		boolean enableAdd		= false;
		boolean enableEdit		= false;
		boolean enableDelete	= false;
		boolean enableUp		= false;
		boolean enableDown		= false;
		
		if (isEnabled()) {
			enableAdd = true;
			int selected = table.getSelectedRow();
			if (selected >= 0) {
				enableEdit = true;
				enableDelete = true;
				if ((selected - 1) >= 0)
					enableUp = true;
				if ((selected + 1) < table.getRowCount())
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
		if (table.isEditing()) {
			table.getCellEditor().stopCellEditing();
		}
	}

	//------------------------------------------------------------
	// Implements IListControlHandler interfaces
	//------------------------------------------------------------

	public void onButtonAdd(ActionEvent ae) {
		stopTableCellEditing();
		argsModel.addNewRow();
		int selected = table.getRowCount() - 1;
		table.setRowSelectionInterval(selected, selected);
	}

	public void onButtonEdit(ActionEvent ae) {
		// No Entry
	}

	public void onButtonDelete(ActionEvent ae) {
		stopTableCellEditing();
		int selected = getSelectedRow();
		if (selected >= 0) {
			argsModel.removeRow(selected);
			int maxidx = argsModel.getRowCount() - 1;
			if (selected > maxidx)
				selected = maxidx;
			if (selected >= 0)
				table.setRowSelectionInterval(selected, selected);
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
			argsModel.moveRow(selected, selected, prev);
			table.setRowSelectionInterval(prev, prev);
			updateButtons();
		}
	}

	public void onButtonDown(ActionEvent ae) {
		stopTableCellEditing();
		int selected = getSelectedRow();
		int next = selected + 1;
		if (selected >= 0 && next < argsModel.getRowCount()) {
			argsModel.moveRow(selected, selected, next);
			table.setRowSelectionInterval(next, next);
			updateButtons();
		}
	}

	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------
	
	protected void onTableModelChanged(TableModelEvent event) {
		updateButtons();
		if (event.getType() == TableModelEvent.UPDATE) {
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
	
	protected ModuleArgDetailTableModel createDefaultTableModel() {
		ModuleArgDetailTableModel model = new ModuleArgDetailTableModel();
		return model;
	}
	
	protected ModuleArgTable createTable() {
		return new ModuleArgTable();
	}
	
	//protected void scrollToVisibleRow(int rowIndex) {
	//	Rectangle rcCell = table.getCellRect(rowIndex, 0, false);
	//	table.scrollRectToVisible(rcCell);
	//}
}
