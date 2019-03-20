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
 *  Copyright 2007-2014  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)StaticModuleArgTypeTableCellRenderer.java	3.1.0	2014/05/12
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)StaticModuleArgTypeTableCellRenderer.java	2.0.0	2012/11/02
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module.swing.table;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;

import ssac.aadl.common.CommonResources;
import ssac.aadl.module.ModuleArgType;

/**
 * モジュール引数テーブルの引数属性専用セルレンダラー。
 * この実装では、編集(選択)可能とするため、<code>JComboBox</code> による表示となる。
 * 
 * @version 3.1.0	2014/05/12
 */
public class EditableModuleArgTypeTableCellRenderer extends JComboBox implements TableCellRenderer
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final JTextField comboEditor;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public EditableModuleArgTypeTableCellRenderer() {
		super();
		setEditable(true);
		setBorder(BorderFactory.createEmptyBorder());
		comboEditor = (JTextField)getEditor().getEditorComponent();
		comboEditor.setBorder(BorderFactory.createEmptyBorder());
		comboEditor.setOpaque(true);
		adjustFixedSize();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
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
		// setEditable(editable);
		//--- これをやると背景色が固定されてしまう。
		removeAllItems();
		if (isSelected) {
			comboEditor.setForeground(table.getSelectionForeground());
			comboEditor.setBackground(table.getSelectionBackground());
		} else {
			comboEditor.setForeground(table.getForeground());
			if (value instanceof ModuleArgType) {
				ModuleArgType attr = (ModuleArgType)value;
				if (attr == ModuleArgType.IN) {
					comboEditor.setBackground(CommonResources.DEF_BACKCOLOR_ARG_IN);
				}
				else if (attr == ModuleArgType.OUT) {
					comboEditor.setBackground(CommonResources.DEF_BACKCOLOR_ARG_OUT);
				}
				else if (attr == ModuleArgType.STR) {
					comboEditor.setBackground(CommonResources.DEF_BACKCOLOR_ARG_STR);
				}
				else if (attr == ModuleArgType.PUB) {
					comboEditor.setBackground(CommonResources.DEF_BACKCOLOR_ARG_PUB);
				}
				else if (attr == ModuleArgType.SUB) {
					comboEditor.setBackground(CommonResources.DEF_BACKCOLOR_ARG_SUB);
				}
				else {
					comboEditor.setBackground(table.getBackground());
				}
			}
			else {
				comboEditor.setBackground(table.getBackground());
			}
		}
		addItem(value==null ? "" : value.toString());
		
		return this;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	static protected void setupComboBoxItems(JComboBox cb) {
		cb.removeAllItems();
		cb.addItem(ModuleArgType.NONE);
		cb.addItem(ModuleArgType.IN);
		cb.addItem(ModuleArgType.OUT);
		cb.addItem(ModuleArgType.STR);
		cb.addItem(ModuleArgType.PUB);
		cb.addItem(ModuleArgType.SUB);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
