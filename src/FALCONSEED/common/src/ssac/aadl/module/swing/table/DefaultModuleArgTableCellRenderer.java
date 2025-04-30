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
