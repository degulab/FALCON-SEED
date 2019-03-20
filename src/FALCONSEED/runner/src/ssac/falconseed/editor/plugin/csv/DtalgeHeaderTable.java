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
 * @(#)DtalgeHeaderTable.java	1.21	2012/06/06
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtalgeHeaderTable.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.editor.plugin.csv;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.util.EventObject;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import ssac.util.swing.table.SpreadSheetTable;

/**
 * データ代数CSV標準形のヘッダー情報を設定するテーブルコンポーネント。
 * 
 * @version 1.21	2012/06/06
 */
public class DtalgeHeaderTable extends SpreadSheetTable
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
	
	public DtalgeHeaderTable() {
		super();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	@Override
	public boolean editCellAt(int rowIndex, int columnIndex, EventObject event) {
		// データ型以外のカラムの編集では、エディタコンポーネントの
		// サイズを表示されている領域に限定する。
		boolean result = super.editCellAt(rowIndex, columnIndex, event);
		if (columnIndex != 1 && result) {
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
		
		if (dataModel instanceof DtalgeHeaderTableModel) {
			DtalgeHeaderTableModel tableModel = (DtalgeHeaderTableModel)dataModel;
			
			// setup data-type column
			TableColumn col = getColumnModel().getColumn(tableModel.typeColumnIndex());
			//--- setup cell editor
			TableCellEditor typeCellEditor = createDtalgeDataTypeCellEditor();
			if (typeCellEditor != null) {
				col.setCellEditor(typeCellEditor);
			}
			//--- setup cell renderer
			DtalgeDataTypeCellRenderer typeCellRenderer = createDtalgeDataTypeCellRenderer();
			if (typeCellRenderer != null) {
				col.setCellRenderer(typeCellRenderer);
				
				// setup column size
				col.setMinWidth(typeCellRenderer.getMinimumSize().width);
				col.setMaxWidth(typeCellRenderer.getMaximumSize().width);
				col.setResizable(false);
			}
		}
	}

	@Override
	public Component prepareEditor(TableCellEditor editor, int row, int column) {
		Component c = super.prepareEditor(editor, row, column);
		if (c instanceof JCheckBox) {
			JCheckBox chk = (JCheckBox)c;
			chk.setBackground(getSelectionBackground());
			chk.setBorderPainted(true);
		}
		return c;
	}

	@Override
	public void updateUI() {
		super.updateUI();

		TableCellRenderer renderer = getDefaultRenderer(Boolean.class);
		if (renderer instanceof JComponent) {
			((JComponent)renderer).updateUI();
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	protected TableCellEditor createDtalgeDataTypeCellEditor() {
		JComboBox cb = new JComboBox();
		cb.setBorder(BorderFactory.createEmptyBorder());
		setupComboBoxItems(cb);
		return new DefaultCellEditor(cb);
	}
	
	protected DtalgeDataTypeCellRenderer createDtalgeDataTypeCellRenderer() {
		DtalgeDataTypeCellRenderer renderer = new DtalgeDataTypeCellRenderer();
		return renderer;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	static protected void setupComboBoxItems(JComboBox cb) {
		cb.removeAllItems();
		cb.addItem(DtalgeDataTypes.STRING);
		cb.addItem(DtalgeDataTypes.DECIMAL);
		cb.addItem(DtalgeDataTypes.BOOLEAN);
	}
	
	static protected class DtalgeDataTypeCellRenderer extends JComboBox implements TableCellRenderer
	{
		private final JTextField comboEditor;
		
		public DtalgeDataTypeCellRenderer() {
			super();
			setEditable(true);
			setBorder(BorderFactory.createEmptyBorder());
			comboEditor = (JTextField)getEditor().getEditorComponent();
			comboEditor.setBorder(BorderFactory.createEmptyBorder());
			comboEditor.setOpaque(true);
			adjustFixedSize();
		}
		
		public void adjustFixedSize() {
			addItem("");
			setSize(this.getPreferredSize());
			Dimension cbdm = getSize();
			Dimension tfdm = comboEditor.getPreferredSize();
			int width = cbdm.width - tfdm.width;
			setupComboBoxItems(this);
			int maxWidth = 0;
			for (int i = 0; i < getItemCount(); i++) {
				Object item = getItemAt(i);
				String str = (item==null ? "" : item.toString());
				FontMetrics fm = comboEditor.getFontMetrics(comboEditor.getFont());
				int sw = fm.stringWidth(str);
				maxWidth = Math.max(sw, maxWidth);
			}
			int cbWidth = maxWidth + width + 5;
			removeAllItems();
			//--- setup minimum size
			cbdm = getMinimumSize();
			cbdm.setSize(cbWidth, cbdm.height);
			setMinimumSize(cbdm);
			//--- setup maximum size
			cbdm = getMaximumSize();
			cbdm.setSize(cbWidth, cbdm.height);
			setMaximumSize(cbdm);
			//--- setup Preferred size
			cbdm = getPreferredSize();
			cbdm.setSize(cbWidth, cbdm.height);
			setPreferredSize(cbdm);
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			removeAllItems();
			if (isSelected) {
				comboEditor.setForeground(table.getSelectionForeground());
				comboEditor.setBackground(table.getSelectionBackground());
			} else {
				comboEditor.setForeground(table.getForeground());
				comboEditor.setBackground(table.getBackground());
			}
			addItem(value==null ? "" : value.toString());
			return this;
		}
	}
}
