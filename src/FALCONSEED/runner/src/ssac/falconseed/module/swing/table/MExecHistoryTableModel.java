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
 * @(#)MExecHistoryTableModel.java	3.0.0	2014/03/26
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecHistoryTableModel.java	1.22	2012/08/21
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing.table;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.table.AbstractTableModel;

import ssac.falconseed.module.ModuleRuntimeData;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.view.dialog.AsyncProcessMonitorWindow;

/**
 * フィルタ実行履歴のテーブルモデル。
 * 実行履歴管理にも使用する。
 * 
 * @version 3.0.0	2014/03/26
 * @since 1.22
 */
public class MExecHistoryTableModel extends AbstractTableModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = -8635313103565996726L;
	
	static public final int COL_RESULT		= 0;
	static public final int COL_NAME			= 1;
	static public final int COL_EXEC_START	= 2;
	static public final int COL_EXEC_TIME		= 3;
	
	static private final int defaultColumnCount = 4;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 実行時フィルタ情報のリスト **/
	private ArrayList<ModuleRuntimeData>	_itemlist;
	/** 履歴保持数の上限、0 以下の場合は無制限 **/
	private int	_limitHistories;
	private long	_historyNo;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MExecHistoryTableModel() {
		this._itemlist = new ArrayList<ModuleRuntimeData>();
		this._limitHistories = 0;
		this._historyNo = 0L;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public int getLimitCount() {
		return _limitHistories;
	}
	
	public void setLimitCount(int limit) {
		if (limit <= 0) {
			_limitHistories = 0;
		}
		else if (limit >= _limitHistories) {
			_limitHistories = limit;
		}
		else {
			// remove over rows
			int removeCount = _limitHistories - limit;
			removeRows(0, removeCount);
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		// テーブルからの編集は許可しない
		return false;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	@Override
	public String getColumnName(int columnIndex) {
		String value;

		if (columnIndex == COL_RESULT) {
			value = RunnerMessages.getInstance().MExecHistoryTableColumnResult;
		}
		else if (columnIndex == COL_NAME) {
			value = RunnerMessages.getInstance().MExecHistoryTableColumnName;
		}
		else if (columnIndex == COL_EXEC_START) {
			value = RunnerMessages.getInstance().MExecHistoryTableColumnStart;
		}
		else if (columnIndex == COL_EXEC_TIME) {
			value = RunnerMessages.getInstance().MExecHistoryTableColumnTime;
		}
		else {
			value = super.getColumnName(columnIndex);
		}
		
		return value;
	}

	public int getColumnCount() {
		return defaultColumnCount;
	}

	public int getRowCount() {
		return _itemlist.size();
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		// テーブルからの編集は許可しない
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		Object value = null;

		ModuleRuntimeData data = _itemlist.get(rowIndex);
		if (columnIndex == COL_RESULT) {
			value = formatResult(data);
		}
		else if (columnIndex == COL_NAME) {
			value = data.getName();
		}
		else if (columnIndex == COL_EXEC_START) {
			value = formatStartTime(data);
		}
		else if (columnIndex == COL_EXEC_TIME) {
			value = formatProcessTime(data);
		}
		
		return value;
	}
	
	public long getHistoryNo(int rowIndex) {
		return _itemlist.get(rowIndex).getRunNo();
	}
	
	static public String formatHistoryNo(long no) {
		return NumberFormat.getNumberInstance().format(no);
	}
	
	static public String formatResult(boolean userCanceled, int exitCode) {
		if (userCanceled) {
			// user canceled
			return "Cancel";
		}
		else if (exitCode == 0) {
			// succeeded
			return String.format("OK");
		} else {
			// failed
			return String.format("NG(%d)", exitCode);
		}
	}
	
	static public String formatStartTime(long epochTime) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(epochTime);
		return String.format("%d/%d %d:%02d",
				cal.get(Calendar.MONTH)+1,
				cal.get(Calendar.DAY_OF_MONTH),
				cal.get(Calendar.HOUR_OF_DAY),
				cal.get(Calendar.MINUTE));
	}
	
	static public String formatProcessTime(long milliseconds) {
		return AsyncProcessMonitorWindow.formatTime(milliseconds);
	}
	
	static public String formatHistoryNo(final ModuleRuntimeData data) {
		return formatHistoryNo(data.getRunNo());
	}
	
	static public Object formatResult(final ModuleRuntimeData data) {
		return formatResult(data.isUserCanceled(), data.getExitCode());
	}
	
	static public String formatStartTime(final ModuleRuntimeData data) {
		return formatStartTime(data.getStartTime());
	}
	
	static public String formatProcessTime(final ModuleRuntimeData data) {
		return formatProcessTime(data.getProcessTime());
	}
	
	public ModuleRuntimeData getItem(int rowIndex) {
		return _itemlist.get(rowIndex);
	}
	
	public void addItem(ModuleRuntimeData data) {
		if (data == null)
			throw new NullPointerException("ModuleRuntimeData object is null.");
		++_historyNo;
		int rowIndex = getRowCount();
		data.setRunNo(_historyNo);
		_itemlist.add(data);
		fireTableRowsInserted(rowIndex, rowIndex);
		if (_limitHistories > 0) {
			int removeRowCount = getRowCount() - _limitHistories;
			if (removeRowCount > 0) {
				removeRows(0, removeRowCount);
			}
		}
	}
	
	public void removeRow(int rowIndex) {
		_itemlist.remove(rowIndex);
		fireTableRowsDeleted(rowIndex, rowIndex);
	}
	
	public void removeRows(int rowIndex, int length) {
		int endRowIndex = rowIndex + length - 1;
		if (rowIndex < 0)
			rowIndex = 0;
		if (endRowIndex >= _itemlist.size())
			endRowIndex = _itemlist.size() - 1;
		
		if (endRowIndex >= rowIndex) {
			// remove
			for (int row = endRowIndex; row >= rowIndex; row--) {
				_itemlist.remove(row);
			}
			
			// update
			fireTableRowsDeleted(rowIndex, endRowIndex);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
