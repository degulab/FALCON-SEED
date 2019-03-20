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
 * @(#)ModuleArgValueTablePane.java	2.0.0	2012/11/02
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.view.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.JTextComponent;

import ssac.aadl.editor.EditorMessages;
import ssac.aadl.editor.view.dialog.FileChooserManager;
import ssac.aadl.module.ModuleArgDetail;
import ssac.aadl.module.swing.table.ModuleArgTable;
import ssac.aadl.module.swing.table.StaticModuleArgTypeTableCellRenderer;
import ssac.util.Strings;
import ssac.util.io.Files;
import ssac.util.swing.MenuToggleButton;
import ssac.util.swing.list.IListControlHandler;
import ssac.util.swing.list.ListController;
import ssac.util.swing.table.SpreadSheetRowHeader;

/**
 * モジュール引数値のテーブルパネル
 * 
 * @version 2.0.0	2012/11/02
 * @since 1.14
 */
public class ModuleArgValueTablePane  extends JScrollPane implements IListControlHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final ListController controller = new ListController(this);
	
	private final TableModelListener argsListener;
	
	private ModuleArgValueTableModel	argsModel;
	private ModuleArgTable				table;

	private File	defArgBaseFile;
	private String lastArgPath;
	
	private final JPopupMenu popAdd  = new JPopupMenu();
	private final JPopupMenu popEdit = new JPopupMenu();

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ModuleArgValueTablePane() {
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
		
		// setup List buttons
		//--- make popup menu for [Add] button
		popAdd.add(new PopupMenuAddFileAction(EditorMessages.getInstance().BuildOptionDlg_Menu_ArgsAddFile));
		popAdd.addSeparator();
		popAdd.add(new PopupMenuAddTextAction(EditorMessages.getInstance().BuildOptionDlg_Menu_ArgsAddText));
		popAdd.addPopupMenuListener(new PopupMenuListener(){
			public void popupMenuCanceled(PopupMenuEvent e) {}
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				controller.getAddButton().setSelected(false);
			}
		});
		//--- make popup menu for [Edit] button
		popEdit.add(new PopupMenuEditFileAction(EditorMessages.getInstance().BuildOptionDlg_Menu_ArgsEditFile));
		popEdit.addSeparator();
		popEdit.add(new PopupMenuEditTextAction(EditorMessages.getInstance().BuildOptionDlg_Menu_ArgsEditText));
		popEdit.addPopupMenuListener(new PopupMenuListener(){
			public void popupMenuCanceled(PopupMenuEvent e) {}
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				controller.getEditButton().setSelected(false);
			}
		});
		
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
	
	public void updateArgumentDetails(ModuleArgDetail[] details) {
		argsModel.updateArgumentDetails(details);
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
	
	public File getDefaultArgumentBaseFile() {
		File retFile = (defArgBaseFile == null ? new File("") : defArgBaseFile);
		return retFile.getAbsoluteFile();
	}
	
	public String getDefaultArgumentBasePath() {
		return getDefaultArgumentBaseFile().getPath();
	}
	
	public void setDefaultArgumentBaseFile(File basepath) {
		this.defArgBaseFile = basepath;
	}
	
	public String getLastArgumentPath() {
		return this.lastArgPath;
	}
	
	public void setLastArgumentPath(String path) {
		this.lastArgPath = path;
	}
	
	public ModuleArgValueTableModel getTableModel() {
		return argsModel;
	}
	
	public void setTableModel(ModuleArgValueTableModel newModel) {
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
		MenuToggleButton btn = (MenuToggleButton)ae.getSource();
		popAdd.show(btn, 0, btn.getHeight());
	}

	public void onButtonEdit(ActionEvent ae) {
		stopTableCellEditing();
		MenuToggleButton btn = (MenuToggleButton)ae.getSource();
		popEdit.show(btn, 0, btn.getHeight());
	}

	public void onButtonDelete(ActionEvent ae) {
		stopTableCellEditing();
		int selected = getSelectedRow();
		if (selected >= 0) {
			argsModel.removeRow(selected);
			argsModel.refreshArgumentTypes();
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
			String vPrev = argsModel.getArgumentValueAt(prev);
			String vSel  = argsModel.getArgumentValueAt(selected);
			argsModel.setArgumentValueAt(prev, vSel);
			argsModel.setArgumentValueAt(selected, vPrev);
			table.setRowSelectionInterval(prev, prev);
			updateButtons();
		}
	}

	public void onButtonDown(ActionEvent ae) {
		stopTableCellEditing();
		int selected = getSelectedRow();
		int next = selected + 1;
		if (selected >= 0 && next < argsModel.getRowCount()) {
			String vNext = argsModel.getArgumentValueAt(next);
			String vSel  = argsModel.getArgumentValueAt(selected);
			argsModel.setArgumentValueAt(next, vSel);
			argsModel.setArgumentValueAt(selected, vNext);
			table.setRowSelectionInterval(next, next);
			updateButtons();
		}
	}
	
	public void onMenuAddFile(ActionEvent ae) {
		stopTableCellEditing();
		// ファイル選択
		File argFile = chooseProgramArgument(getLastArgumentPath());
		if (argFile != null) {
			argsModel.addRow(null, convertAbsoluteToRelativePath(argFile.getAbsolutePath()));
			updateButtons();
		}
	}
	
	public void onMenuAddText(ActionEvent ae) {
		stopTableCellEditing();
		// テキスト行の追加
		argsModel.addRow(null, null);
		int selected = table.getRowCount() - 1;
		table.setRowSelectionInterval(selected, selected);
		//scrollToVisibleRow(selected);
		updateButtons();
		table.revalidate();
		table.repaint();

		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				// テキスト編集開始
				final int targetRow = table.getRowCount() - 1;
				table.requestFocusInWindow();
		        table.editCellAt(targetRow, ModuleArgValueTableModel.COL_VALUE);
		        Component editorComponent = table.getEditorComponent();
		        if (editorComponent != null) {
		        	if (!editorComponent.hasFocus()) {
		        		if (!editorComponent.requestFocusInWindow()) {
		        			editorComponent.requestFocus();
		        		}
		        	}
		        	if (editorComponent instanceof JTextComponent) {
		        		JTextComponent textComponent = (JTextComponent)editorComponent;
		        		textComponent.selectAll();
		        	}
		        }
			}
		});
	}
	
	public void onMenuEditFile(ActionEvent ae) {
		stopTableCellEditing();
		// get selected arg
		int selected = getSelectedRow();
		if (selected < 0)
			return;		// no selection
		String strValue = argsModel.getArgumentValueAt(selected);
		
		// ファイル選択
		File argFile = chooseProgramArgument(strValue==null ? null : convertRelativeToAbsolutePath(strValue));
		if (argFile != null) {
			String argPath = convertAbsoluteToRelativePath(argFile.getAbsolutePath());
			argsModel.setArgumentValueAt(selected, argPath);
		}
	}
	
	public void onMenuEditText(ActionEvent ae) {
		stopTableCellEditing();
		// get selected arg
		int selected = getSelectedRow();
		if (selected < 0)
			return;		// no selection
		
		// テキスト編集開始
		table.requestFocusInWindow();
        table.editCellAt(selected, ModuleArgValueTableModel.COL_VALUE);
        Component editorComponent = table.getEditorComponent();
        if (editorComponent != null) {
        	if (!editorComponent.hasFocus()) {
        		if (!editorComponent.requestFocusInWindow()) {
        			editorComponent.requestFocus();
        		}
        	}
        	if (editorComponent instanceof JTextComponent) {
        		JTextComponent textComponent = (JTextComponent)editorComponent;
        		textComponent.selectAll();
        	}
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
	
	protected ModuleArgValueTableModel createDefaultTableModel() {
		ModuleArgValueTableModel model = new ModuleArgValueTableModel();
		return model;
	}
	
	protected ModuleArgTable createTable() {
		return new ModuleArgValueTable();
	}
	
	//protected void scrollToVisibleRow(int rowIndex) {
	//	Rectangle rcCell = table.getCellRect(rowIndex, 0, false);
	//	table.scrollRectToVisible(rcCell);
	//}
	
	private File chooseProgramArgument(String initPath) {
		if (Strings.isNullOrEmpty(initPath)) {
			// デフォルトのパスは、ターゲットと同一の場所
			initPath = getDefaultArgumentBasePath();
		}
		
		// ファイル選択
		File selected = FileChooserManager.chooseArgumentFile(this, new File(initPath));
		if (selected != null) {
			// 選択パスを保存
			String lastPath = selected.getAbsolutePath();
			setLastArgumentPath(lastPath);
		}
		return selected;
	}
	
	private String convertRelativeToAbsolutePath(String pathname) {
		File retFile = new File(pathname);
		if (!retFile.isAbsolute()) {
			retFile = new File(getDefaultArgumentBaseFile(), pathname);
		}
		return Files.normalizePath(retFile.getPath());
	}
	
	private String convertAbsoluteToRelativePath(String pathname) {
		File abFile = new File(pathname);
		return Files.convertAbsoluteToRelativePath(getDefaultArgumentBaseFile(), abFile, Files.CommonSeparatorChar);
	}
	
	static private class ModuleArgValueTable extends ModuleArgTable
	{
		@Override
		public String getRowHeaderToolTipText(SpreadSheetRowHeader header, MouseEvent event) {
			return super.getRowHeaderToolTipText(header, event);
		}
		
		@Override
		protected TableCellEditor createModuleAttrCellEditor() {
			// 引数属性用の特殊なセルエディタは使用しない
			return null;
		}
		
		@Override
		protected TableCellRenderer createModuleAttrCellRenderer() {
//			// 引数属性用の特殊なセルレンダラは使用しない
//			return null;
			//--- 2.0.0 : 2012/11/02
			return new StaticModuleArgTypeTableCellRenderer();
		}
	}
	
	class PopupMenuAddFileAction extends AbstractAction {
		public PopupMenuAddFileAction(String name) {
			super(name);
		}
		public void actionPerformed(ActionEvent ae) {
			onMenuAddFile(ae);
		}
	}
	
	class PopupMenuAddTextAction extends AbstractAction {
		public PopupMenuAddTextAction(String name) {
			super(name);
		}
		public void actionPerformed(ActionEvent ae) {
			onMenuAddText(ae);
		}
	}
	
	class PopupMenuEditFileAction extends AbstractAction {
		public PopupMenuEditFileAction(String name) {
			super(name);
		}
		public void actionPerformed(ActionEvent ae) {
			onMenuEditFile(ae);
		}
	}
	
	class PopupMenuEditTextAction extends AbstractAction {
		public PopupMenuEditTextAction(String name) {
			super(name);
		}
		public void actionPerformed(ActionEvent ae) {
			onMenuEditText(ae);
		}
	}
}
