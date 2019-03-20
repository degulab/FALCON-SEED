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
 * @(#)DefaultModuleArgTableCellRenderer.java	3.1.0	2014/05/12
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DefaultModuleArgTableCellRenderer.java	2.0.0	2012/11/02
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module.swing.table;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import ssac.aadl.common.CommonResources;
import ssac.aadl.module.ModuleArgType;

/**
 * モジュール引数テーブルの標準セルレンダラー。
 * 
 * @version 3.1.0	2014/05/12
 */
public class DefaultModuleArgTableCellRenderer extends DefaultTableCellRenderer
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

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
													boolean isSelected, boolean hasFocus,
													int row, int column)
	{
		// 標準のレンダラーで描画
		//--- (!isSelected && hasFocus)の状態では、super#getTableCellRendererComponent() 内で、
		//--- "Table.focusCellForeground" と "Table.focusCellBackground" にカラーが変更されているため、
		//--- 元に戻す。
		Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		// 選択時以外の色を設定
		if (!isSelected) {
			// 引数属性の取得
			Object objAttr = null;
			TableModel tmodel = table.getModel();
			if (tmodel instanceof IModuleArgTableModel) {
				objAttr = ((IModuleArgTableModel)tmodel).getArgumentAttr(row);
			} else {
				objAttr = table.getValueAt(row, table.convertColumnIndexToView(0));
			}
			
			// 引数属性による背景色設定
			if (objAttr instanceof ModuleArgType) {
				ModuleArgType attr = (ModuleArgType)objAttr;
				if (attr == ModuleArgType.IN) {
					comp.setBackground(CommonResources.DEF_BACKCOLOR_ARG_IN);
				}
				else if (attr == ModuleArgType.OUT) {
					comp.setBackground(CommonResources.DEF_BACKCOLOR_ARG_OUT);
				}
				else if (attr == ModuleArgType.STR) {
					comp.setBackground(CommonResources.DEF_BACKCOLOR_ARG_STR);
				}
				else if (attr == ModuleArgType.PUB) {
					comp.setBackground(CommonResources.DEF_BACKCOLOR_ARG_PUB);
				}
				else if (attr == ModuleArgType.SUB) {
					comp.setBackground(CommonResources.DEF_BACKCOLOR_ARG_SUB);
				}
				else {
					comp.setBackground(table.getBackground());
				}
			}
			else {
				comp.setBackground(table.getBackground());
			}
		}
		
		return comp;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
