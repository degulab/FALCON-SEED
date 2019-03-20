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
 * @(#)MacroSubFiltersTable.java	3.1.0	2014/05/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MacroSubFiltersTable.java	2.0.0	2012/10/22
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing.table;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import ssac.falconseed.module.FilterDataError;
import ssac.falconseed.module.FilterError;
import ssac.falconseed.module.FilterErrorType;
import ssac.falconseed.module.MacroSubModuleRuntimeData;
import ssac.falconseed.module.ModuleArgConfig;
import ssac.falconseed.module.swing.MacroFilterEditModel;
import ssac.util.swing.table.SpreadSheetColumnHeader;
import ssac.util.swing.table.SpreadSheetTable;

/**
 * マクロフィルタを構成するサブフィルタの一覧を表示するテーブルコンポーネント。
 * 
 * @version 3.1.0	2014/05/19
 * @since 2.0.0
 */
public class MacroSubFiltersTable extends SpreadSheetTable
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

	public MacroSubFiltersTable() {
		super();
		setAdjustOnlyVisibleRows(false);			// 列幅の自動調整では全行を対象
		setAdjustOnlySelectedCells(false);			// 列幅の自動調整では選択セルのみに限定しない
		getTableRowHeader().setFixedCellWidth(50);	// 列幅を 50pixel に固定
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		setRowSelectionAllowed(true);
		setColumnSelectionAllowed(false);
		setDefaultRenderer(Object.class, new MacroSubFiltersTableCellRenderer());
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	@Override
	public AbMacroSubFiltersTableModel getModel() {
		return (AbMacroSubFiltersTableModel)super.getModel();
	}

	@Override
	public void setModel(TableModel dataModel) {
		if (!(dataModel instanceof AbMacroSubFiltersTableModel))
			throw new IllegalArgumentException("Table model is not AbMacroSubFiltersTableModel instance.");
		
		super.setModel(dataModel);
	}

	/**
	 * 現在のデータモデルに従い、表示内容を更新する。
	 */
	public void refreshDisplay() {
		getModel().fireTableDataChanged();
	}

	@Override
	public String getToolTipText(MouseEvent event) {
        Point p = event.getPoint();
        int hitModelColumnIndex = convertColumnIndexToModel(columnAtPoint(p));
        int hitModelRowIndex = convertRowIndexToModel(rowAtPoint(p));
        if (hitModelColumnIndex != -1 && hitModelRowIndex != -1) {
        	if (hitModelColumnIndex >= AbMacroSubFiltersTableModel.CI_FIRSTARG) {
        		// 引数列のエラー情報を表示
        		Object value = getModel().getValueAt(hitModelRowIndex, hitModelColumnIndex);
        		if (value instanceof ModuleArgConfig) {
        			//--- 引数定義データ
        			Object errValue = ((ModuleArgConfig)value).getUserData();
        			if (errValue instanceof FilterError) {
        				String errmsg = ((FilterError)errValue).getErrorMessage();
        				if (errmsg != null && errmsg.length() > 0) {
        					return errmsg;
        				}
        			}
        		}
        	}
        	else if (hitModelColumnIndex == AbMacroSubFiltersTableModel.CI_WAITFILTERS) {
        		// 待機フィルタ列なら、モジュールデータから待機フィルタのエラー情報を取得
        		MacroSubModuleRuntimeData moduledata = getModel().getRowData(hitModelRowIndex);
        		FilterDataError errValue = moduledata.getModuleError();
        		if (errValue != null && errValue.getErrorType() == FilterErrorType.FILTER_WAITFILTERS) {
        			//--- 待機フィルタのエラー
        			String errmsg = errValue.getErrorMessage();
        			if (errmsg != null && errmsg.length() > 0) {
        				return errmsg;
        			}
        		}
        	}
        	//--- その他の列では、ツールチップは設定しない
        }
        
        return super.getToolTipText(event);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	@Override
	protected TableModel createDefaultDataModel() {
		return new AbMacroSubFiltersTableModel(){
			@Override
			protected MacroFilterEditModel getEditModel() {
				return null;
			}
			@Override
			protected boolean hasEditModel() {
				return false;
			}
		};
	}

	@Override
	protected JTableHeader createDefaultTableHeader() {
		SpreadSheetColumnHeader th = (SpreadSheetColumnHeader)super.createDefaultTableHeader();
		// このテーブルでは、行単位の選択とするため、
		// 列選択や強調表示は無効とする。
		th.setEnableChangeSelection(false);
		th.setVisibleSelection(false);
		return th;
	}

	@Override
	protected RowHeaderModel createRowHeaderModel() {
		// モジュール引数テーブル専用の行ヘッダーモデル。
		// このモデルが返す値は、'$'と数字を連結させた文字列となる。
		return new SpreadSheetTable.RowHeaderModel(){
			@Override
			public Object getElementAt(int index) {
				return ((AbMacroSubFiltersTableModel)getModel()).getRowName(index);
			}
		};
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
