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
 * @(#)MExecRunningTableModel.java	3.0.0	2014/03/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing.table;

import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.table.AbstractTableModel;

import ssac.falconseed.module.ModuleRuntimeData;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.view.dialog.AsyncProcessMonitorWindow;

/**
 * フィルタ実行中リストのテーブルモデル。
 * 
 * @version 3.0.0	2014/03/28
 * @since 3.0.0
 */
public class MExecRunningTableModel extends AbstractTableModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = -4529873635648459050L;
	
	static public final int COL_STATUS		= 0;
	static public final int COL_CONTROL		= 1;
	static public final int COL_NAME			= 2;
	static public final int COL_EXEC_START	= 3;
	static public final int COL_EXEC_TIME		= 4;
	
	static private final int defaultColumnCount = 5;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 実行時フィルタ情報のリスト **/
	private ArrayList<AsyncProcessMonitorWindow>	_itemlist;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MExecRunningTableModel() {
		this._itemlist = new ArrayList<AsyncProcessMonitorWindow>();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isEmpty() {
		return _itemlist.isEmpty();
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		// ボタン以外の列からの編集は許可しない
		return (columnIndex == COL_CONTROL);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	@Override
	public String getColumnName(int columnIndex) {
		String value;

		if (columnIndex == COL_STATUS) {
			value = RunnerMessages.getInstance().MExecRunningTableColumnStatus;
		}
		else if (columnIndex == COL_CONTROL) {
			value = RunnerMessages.getInstance().MExecRunningTableColumnControl;
		}
		else if (columnIndex == COL_NAME) {
			value = RunnerMessages.getInstance().MExecRunningTableColumnName;
		}
		else if (columnIndex == COL_EXEC_START) {
			value = RunnerMessages.getInstance().MExecRunningTableColumnStart;
		}
		else if (columnIndex == COL_EXEC_TIME) {
			value = RunnerMessages.getInstance().MExecRunningTableColumnTime;
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

		AsyncProcessMonitorWindow wnd = _itemlist.get(rowIndex);
		if (columnIndex == COL_STATUS) {
			value = formatStatus(wnd);
		}
		else if (columnIndex == COL_CONTROL) {
			value = wnd;
		}
		else if (columnIndex == COL_NAME) {
			value = wnd.getModuleData().getName();
		}
		else if (columnIndex == COL_EXEC_START) {
			value = formatStartTime(wnd);
		}
		else if (columnIndex == COL_EXEC_TIME) {
			value = formatProcessTime(wnd);
		}
		
		return value;
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
	
	static public Object formatResult(final ModuleRuntimeData data) {
		return formatResult(data.isUserCanceled(), data.getExitCode());
	}
	
	static public String formatStatus(boolean running, boolean userCanceled, int exitCode) {
		if (running) {
			if (userCanceled) {
				// terminating
				return "停止中";
			}
			else {
				// running normally
				return "実行中";
			}
		}
		else if (userCanceled) {
			return "Cancel";
		}
		else if (exitCode == 0) {
			// finished : succeeded
			return String.format("OK");
		}
		else {
			// finished : failed
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
		return AsyncProcessMonitorWindow.formatShortTime(milliseconds);
	}
	
	static public String formatStatus(final AsyncProcessMonitorWindow wnd) {
		boolean flgRunning = (wnd.isProcessRunning() || wnd.isProcessTerminating());
		boolean flgCancel  = (wnd.isTerminatedByUser() || wnd.isProcessTerminating());
		return formatStatus(flgRunning, flgCancel, wnd.getModuleData().getExitCode());
	}
	
	static public String formatStartTime(final AsyncProcessMonitorWindow wnd) {
		return formatStartTime(wnd.getProcessStartTime());
	}
	
	static public String formatProcessTime(final AsyncProcessMonitorWindow wnd) {
		return formatProcessTime(wnd.getProcessingTime());
	}
	
	public AsyncProcessMonitorWindow getItem(int rowIndex) {
		return _itemlist.get(rowIndex);
	}
	
	public void addItem(final AsyncProcessMonitorWindow wnd) {
		if (wnd == null)
			throw new NullPointerException("AsyncProcessMonitorWindow object is null.");
		int rowIndex = getRowCount();
		_itemlist.add(wnd);
		fireTableRowsInserted(rowIndex, rowIndex);
	}

	/**
	 * 指定されたモニタオブジェクトを、このモデルから除去する。
	 * @param wnd	除去するモニタオブジェクト
	 * @return	除去できた場合は除去したモニタオブジェクトが格納されていた行インデックス、
	 * 			それ以外の場合は (-1)
	 */
	public int removeItem(final AsyncProcessMonitorWindow wnd) {
		int rowIndex = _itemlist.indexOf(wnd);
		if (rowIndex >= 0) {
			removeRow(rowIndex);
		}
		return rowIndex;
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
