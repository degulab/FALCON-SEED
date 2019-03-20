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
 * @(#)ModuleArgTable.java	2.0.0	2012/11/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ModuleArgTable.java	1.17	2010/11/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ModuleArgTable.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module.swing.table;

import java.awt.Component;
import java.awt.Rectangle;
import java.util.EventObject;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import ssac.aadl.module.ModuleArgType;
import ssac.aadl.module.swing.ModuleArgTypeComboBoxModel;
import ssac.util.swing.table.SpreadSheetColumnHeader;
import ssac.util.swing.table.SpreadSheetTable;

/**
 * モジュール引数のテーブル
 * 
 * @version 2.0.0	2012/11/02
 * @since 1.14
 */
public class ModuleArgTable extends SpreadSheetTable
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
	
	public ModuleArgTable() {
		super();
		setAdjustOnlyVisibleRows(false);			// 列幅の自動調整では全行を対象
		setAdjustOnlySelectedCells(false);			// 列幅の自動調整では選択セルのみに限定しない
		getTableRowHeader().setFixedCellWidth(50);	// 列幅を 50pixel に固定
		setDefaultRenderer(Object.class, createDefaultObjectCellRenderer());
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	@Override
	public boolean editCellAt(int rowIndex, int columnIndex, EventObject event) {
		// 引数属性以外のカラムの編集では、エディタコンポーネントの
		// サイズを表示されている領域に限定する。
		boolean result = super.editCellAt(rowIndex, columnIndex, event);
		if (columnIndex != 0 && result) {
			Rectangle cellRect = getCellRect(rowIndex, columnIndex, false);
			Rectangle editRect = getVisibleRect().intersection(cellRect);
			editorComp.setBounds(editRect);
			editorComp.validate();
		}
		return result;
	}

	@Override
	public void setModel(TableModel dataModel) {
		super.setModel(dataModel);
		
		// setup column
		TableColumn col = getColumnModel().getColumn(0);
		//--- setup cell editor
		TableCellEditor attrCellEditor = createModuleAttrCellEditor();
		if (attrCellEditor != null) {
			col.setCellEditor(attrCellEditor);
		}
		//--- setup cell renderer
		TableCellRenderer attrCellRenderer = createModuleAttrCellRenderer();
		if (attrCellRenderer != null) {
			col.setCellRenderer(attrCellRenderer);
			
			// setup column size
			if (attrCellRenderer instanceof Component) {
				col.setMinWidth(((Component)attrCellRenderer).getMinimumSize().width);
				col.setMaxWidth(((Component)attrCellRenderer).getMaximumSize().width);
			}
			col.setResizable(false);
		}
	}

	/**
	 * 指定された行インデックスに対応する行の引数属性を取得する。
	 * 行インデックスが適切ではない場合や、引数属性が存在しない場合は <tt>null</tt> を返す。
	 * @param rowIndex	行インデックス
	 * @return	取得した引数属性を返す。引数属性が取得できない場合は <tt>null</tt>
	 * @since 2.0.0
	 */
	public ModuleArgType getArgumentAttr(int rowIndex) {
		if (rowIndex >= 0 && rowIndex < getRowCount() && (getModel() instanceof IModuleArgTableModel)) {
			return ((IModuleArgTableModel)getModel()).getArgumentAttr(rowIndex);
		}
		// no data
		return null;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

/*
	static protected void setupComboBoxItems(JComboBox cb) {
		cb.removeAllItems();
		cb.addItem(ModuleArgType.NONE);
		cb.addItem(ModuleArgType.IN);
		cb.addItem(ModuleArgType.OUT);
		cb.addItem(ModuleArgType.STR);
	}
*/

	@Override
	protected JTableHeader createDefaultTableHeader() {
		SpreadSheetColumnHeader th = (SpreadSheetColumnHeader)super.createDefaultTableHeader();
		// このテーブルでは、行単位の選択とするため、
		// 列選択や強調表示は無効とする。
		th.setEnableChangeSelection(false);
		th.setVisibleSelection(false);
		return th;
	}

	protected TableCellEditor createModuleAttrCellEditor() {
//		JComboBox cb = new JComboBox();
//		cb.setBorder(BorderFactory.createEmptyBorder());
//		setupComboBoxItems(cb);
		JComboBox cb = new JComboBox(new ModuleArgTypeComboBoxModel());
		cb.setBorder(BorderFactory.createEmptyBorder());
		return new DefaultCellEditor(cb);
	}
	
	protected TableCellRenderer createModuleAttrCellRenderer() {
		return new EditableModuleArgTypeTableCellRenderer();
	}
	
	protected TableCellRenderer createDefaultObjectCellRenderer() {
		DefaultModuleArgTableCellRenderer renderer = new DefaultModuleArgTableCellRenderer();
		return renderer;
	}

	@Override
	protected RowHeaderModel createRowHeaderModel() {
		// モジュール引数テーブル専用の行ヘッダーモデル。
		// このモデルが返す値は、'$'と数字を連結させた文字列となる。
		return new SpreadSheetTable.RowHeaderModel(){
			@Override
			public Object getElementAt(int index) {
				return "$" + super.getElementAt(index);
			}
		};
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
//	static protected class ArgumentAttrCellRenderer extends JComboBox implements TableCellRenderer
//	{
//		private final JTextField comboEditor;
//		
//		public ArgumentAttrCellRenderer() {
//			super();
//			setEditable(true);
//			setBorder(BorderFactory.createEmptyBorder());
//			comboEditor = (JTextField)getEditor().getEditorComponent();
//			comboEditor.setBorder(BorderFactory.createEmptyBorder());
//			comboEditor.setOpaque(true);
//			adjustFixedSize();
//		}
//		
//		public void adjustFixedSize() {
//			addItem("");
//			setSize(this.getPreferredSize());
//			Dimension cbdm = getSize();
//			Dimension tfdm = comboEditor.getPreferredSize();
//			int width = cbdm.width - tfdm.width;
//			setupComboBoxItems(this);
//			int maxWidth = 0;
//			for (int i = 0; i < getItemCount(); i++) {
//				Object item = getItemAt(i);
//				String str = (item==null ? "" : item.toString());
//				FontMetrics fm = comboEditor.getFontMetrics(comboEditor.getFont());
//				int sw = fm.stringWidth(str);
//				maxWidth = Math.max(sw, maxWidth);
//			}
//			int cbWidth = maxWidth + width + 5;
//			removeAllItems();
//			//--- setup minimum size
//			cbdm = getMinimumSize();
//			cbdm.setSize(cbWidth, cbdm.height);
//			setMinimumSize(cbdm);
//			//--- setup maximum size
//			cbdm = getMaximumSize();
//			cbdm.setSize(cbWidth, cbdm.height);
//			setMaximumSize(cbdm);
//			//--- setup Preferred size
//			cbdm = getPreferredSize();
//			cbdm.setSize(cbWidth, cbdm.height);
//			setPreferredSize(cbdm);
//		}
//
//		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//			removeAllItems();
//			if (isSelected) {
//				comboEditor.setForeground(table.getSelectionForeground());
//				comboEditor.setBackground(table.getSelectionBackground());
//			} else {
//				comboEditor.setForeground(table.getForeground());
//				if (value instanceof ModuleArgType) {
//					ModuleArgType attr = (ModuleArgType)value;
//					if (attr == ModuleArgType.IN) {
//						comboEditor.setBackground(CommonResources.DEF_BACKCOLOR_ARG_IN);
//					}
//					else if (attr == ModuleArgType.OUT) {
//						comboEditor.setBackground(CommonResources.DEF_BACKCOLOR_ARG_OUT);
//					}
//					else if (attr == ModuleArgType.STR) {
//						comboEditor.setBackground(CommonResources.DEF_BACKCOLOR_ARG_STR);
//					}
//					else {
//						comboEditor.setBackground(table.getBackground());
//					}
//				}
//				else {
//					comboEditor.setBackground(table.getBackground());
//				}
//			}
//			addItem(value==null ? "" : value.toString());
//			return this;
//		}
//	}
//
//	/**
//	 * 標準のセルレンダラー
//	 */
//	static public class DelDefaultArgumentCellRenderer extends DefaultTableCellRenderer
//	{
//		@Override
//		public Component getTableCellRendererComponent(JTable table, Object value,
//														boolean isSelected, boolean hasFocus,
//														int row, int column)
//		{
//			// 標準のレンダラーで描画
//			//--- (!isSelected && hasFocus)の状態では、super#getTableCellRendererComponent() 内で、
//			//--- "Table.focusCellForeground" と "Table.focusCellBackground" にカラーが変更されているため、
//			//--- 元に戻す。
//			Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//
//			// 選択時以外の色を設定
//			if (!isSelected) {
//				// 引数属性の取得
//				Object objAttr = null;
//				TableModel tmodel = table.getModel();
//				if (tmodel instanceof IModuleArgTableModel) {
//					objAttr = ((IModuleArgTableModel)tmodel).getArgumentAttr(row);
//				} else {
//					objAttr = table.getValueAt(row, table.convertColumnIndexToView(0));
//				}
//				
//				// 引数属性による背景色設定
//				if (objAttr instanceof ModuleArgType) {
//					ModuleArgType attr = (ModuleArgType)objAttr;
//					if (attr == ModuleArgType.IN) {
//						comp.setBackground(CommonResources.DEF_BACKCOLOR_ARG_IN);
//					}
//					else if (attr == ModuleArgType.OUT) {
//						comp.setBackground(CommonResources.DEF_BACKCOLOR_ARG_OUT);
//					}
//					else if (attr == ModuleArgType.STR) {
//						comp.setBackground(CommonResources.DEF_BACKCOLOR_ARG_STR);
//					}
//					else {
//						comp.setBackground(table.getBackground());
//					}
//				}
//				else {
//					comp.setBackground(table.getBackground());
//				}
//			}
//			
//			return comp;
//		}
//	}
}
