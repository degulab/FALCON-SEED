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
 * @(#)GenericConditionSchemaTable.java	3.2.1	2015/07/15
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)GenericConditionSchemaTable.java	3.2.0	2015/07/01
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui.exp;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import ssac.aadl.common.CommonResources;
import ssac.util.swing.table.SpreadSheetTable;

/**
 * 条件や式のテーブルの基本実装。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public class GenericOperationSchemaTable extends SpreadSheetTable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public GenericOperationSchemaTable() {
		super();
		localConstructor();
	}

	public GenericOperationSchemaTable(TableModel model) {
		super(model);
		localConstructor();
	}
	
	private void localConstructor() {
		setDefaultRenderer(Object.class, new GenericOperationSchemaCellRenderer());

		// 初期設定
		setCellSelectionEnabled(false);		// セル選択不可
		setColumnSelectionAllowed(false);	// 列選択不可
		setRowSelectionAllowed(true);		// 行選択のみ
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);	// 自動サイズ調整は行わない
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);	// 単一選択
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public String getTitle() {
		TableModel tableModel = getModel();
		if (tableModel instanceof GenericOperationSchemaTableModel) {
			// 条件等のテーブルモデルから取得
			return ((GenericOperationSchemaTableModel<?>)tableModel).getTitle();
		}
		else {
			// デフォルトの文字列
			return "Table";
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	/**
	 * 引数の値列のセルレンダラー
	 */
	protected class GenericOperationSchemaCellRenderer extends DefaultTableCellRenderer
	{
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
														boolean isSelected, boolean hasFocus,
														int row, int column)
		{
			//--- (!isSelected && hasFocus)の状態では、super#getTableCellRendererComponent() 内で、
			//--- "Table.focusCellForeground" と "Table.focusCellBackground" にカラーが変更されているため、
			//--- 元に戻す。
			Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			
			// エラーの有無をチェック
			String strToolTip = null;
			boolean hasError = false;
			TableModel tmodel = table.getModel();
			if (tmodel instanceof GenericOperationSchemaTableModel) {
				GenericOperationSchemaTableModel<?> opmodel = (GenericOperationSchemaTableModel<?>)tmodel;
				hasError = opmodel.isError(row, column);
				strToolTip = opmodel.getCellToolTipText(row, column);
			}
			
			if (hasError) {
				if (!isSelected) {
					//--- 背景色のみ、エラーカラー
					comp.setBackground(CommonResources.DEF_BACKCOLOR_ERROR);
					comp.setForeground(table.getForeground());
				} else {
					//--- 文字列を赤にしてみる
					comp.setBackground(table.getSelectionBackground());
					comp.setForeground(Color.RED);
				}
			}
			else {
				if (!isSelected) {
					comp.setBackground(table.getBackground());
					comp.setForeground(table.getForeground());
				} else {
					comp.setBackground(table.getSelectionBackground());
					comp.setForeground(table.getSelectionForeground());
				}
			}
			
			setToolTipText(strToolTip);
			
			return comp;
		}
	}
}
