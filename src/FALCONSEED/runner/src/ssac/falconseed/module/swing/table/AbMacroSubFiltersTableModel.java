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
 * @(#)AbMacroSubFiltersTableModel.java	3.1.0	2014/05/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbMacroSubFiltersTableModel.java	2.0.0	2012/10/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing.table;

import java.util.TreeSet;

import javax.swing.table.AbstractTableModel;

import ssac.falconseed.module.MacroSubModuleRuntimeData;
import ssac.falconseed.module.ModuleRuntimeData;
import ssac.falconseed.module.swing.MacroFilterEditModel;
import ssac.falconseed.runner.RunnerMessages;

/**
 * サブフィルタ一覧を表示するテーブルコンポーネント用データモデル。
 * 
 * @version 3.1.0	2014/05/29
 * @since 2.0.0
 */
public abstract class AbMacroSubFiltersTableModel extends AbstractTableModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	
	static public final int	CI_WAITFILTERS	= 0;
	static public final int	CI_FILTERNAME	= 1;
	static public final int CI_COMMENT		= 2;
	static public final int CI_FIRSTARG		= 3;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** テーブルのカラム数 **/
	private int	_numColumns;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定された行インデックスに対応するモジュール実行データを返す。
	 * @param rowIndex	行インデックス
	 * @return モジュール実行データ
	 * @throws IndexOutOfBoundsException	インデックスが適切ではない場合
	 */
	public MacroSubModuleRuntimeData getRowData(int rowIndex) {
		ensureEditModel();
		return getEditModel().getSubFilter(rowIndex);
	}
	
	/**
	 * 指定されたインデックスの行に新しい行データをセットする。
	 * このメソッドでは、行が更新された場合のみ、<code>fireTableRowsUpdated</code> を呼び出す。
	 * <p><b>注：</b>
	 * <blockquote>
	 * もとの位置にあったモジュールの実行順序リンクはすべて除去し、関連する表示も更新する。
	 * 実行番号や列数の更新は行わないため、呼び出し側で適切に処理すること。
	 * </blockquote>
	 * @param rowIndex	行インデックス
	 * @param newData	新しい行データ
	 * @throws IndexOutOfBoundsException	インデックスが適切ではない場合
	 */
	public void setRowData(int rowIndex, final MacroSubModuleRuntimeData newData) {
		ensureEditModel();
		MacroSubModuleRuntimeData oldData = getEditModel().getSubFilterList().set(rowIndex, newData);
		if (oldData != newData) {
			fireTableRowsUpdated(rowIndex, rowIndex);
			clearRunOrderLink(oldData);
		}
	}
	
	/**
	 * 終端に新しい行を追加する。
	 * このメソッドでは、<code>fireTableRowsInserted</code> を呼び出す。
	 * <p><b>注：</b>
	 * <blockquote>
	 * 実行番号や列数の更新は行わないため、呼び出し側で適切に処理すること。
	 * </blockquote>
	 * @param newData	新しい行データ
	 */
	public void addRowData(final MacroSubModuleRuntimeData newData) {
		ensureEditModel();
		int newRowIndex = getRowCount();
		getEditModel().getSubFilterList().add(newData);
		fireTableRowsInserted(newRowIndex, newRowIndex);
	}

	/**
	 * 指定されたインデックスの行を削除する。
	 * このメソッドでは、行が削除された場合のみ、<code>fireTableRowsDeleted</code> を呼び出す。
	 * <p><b>注：</b>
	 * <blockquote>
	 * 実行順序リンクはすべて除去し、関連する表示も更新する。
	 * 実行番号や列数の更新は行わないため、呼び出し側で適切に処理すること。
	 * </blockquote>
	 * @param rowIndex	行インデックス
	 */
	public void removeRow(int rowIndex) {
		ensureEditModel();
		MacroSubModuleRuntimeData removedModule = getEditModel().getSubFilterList().remove(rowIndex);
		if (removedModule != null) {
			// 列数の更新は、このメソッドを呼び出す側で処理する
			fireTableRowsDeleted(rowIndex, rowIndex);
			//--- 実行順序リンクを除去し、表示を更新
			clearRunOrderLink(removedModule);
		}
	}

	/**
	 * 列数を更新する。
	 * 列数が更新された場合のみ、このメソッドは <code>fireTableStructureChanged()</code> を呼び出す。
	 * @return	更新された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean updateColumnCount() {
		int newColumns;
		if (hasEditModel()) {
			newColumns = getEditModel().getSubFilterList().getMaxArgumentCount() + CI_FIRSTARG;
		} else {
			newColumns = CI_FIRSTARG;
		}

		boolean modified = false;
		if (newColumns != _numColumns) {
			modified = true;
			_numColumns = newColumns;
			fireTableStructureChanged();
		}
		return modified;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return Object.class;
	}

	@Override
	public String getColumnName(int columnIndex) {
		if (columnIndex == CI_WAITFILTERS) {
			// 待機フィルタ
			return RunnerMessages.getInstance().MacroSubFiltersTableColumnWait;
		}
		else if (columnIndex == CI_FILTERNAME) {
			// フィルタ名
			return RunnerMessages.getInstance().MacroSubFiltersTableColumnName;
		}
		else if (columnIndex == CI_COMMENT) {
			// コメント
			return RunnerMessages.getInstance().MacroSubFiltersTableColumnComment;
		}
		else if (columnIndex >= CI_FIRSTARG) {
			// 引数番号
			return "$".concat(String.valueOf(columnIndex - CI_FIRSTARG + 1));
		}
		else {
			return super.getColumnName(columnIndex);
		}
	}
	
	public String getRowName(int rowIndex) {
		if (hasEditModel() && rowIndex >= 0 && rowIndex < getEditModel().getSubFilterCount()) {
			return String.valueOf(getEditModel().getSubFilterRunNo(rowIndex));
		} else {
			return String.valueOf(rowIndex);
		}
	}

	@Override
	public int getColumnCount() {
		return _numColumns;
	}

	@Override
	public int getRowCount() {
		return (hasEditModel() ? getEditModel().getSubFilterCount() : 0);
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (hasEditModel() && getEditModel().isEditing() && rowIndex >= 0 && rowIndex < getEditModel().getSubFilterCount()) {
			return (columnIndex == CI_COMMENT);	// 編集中なら、コメントのみ編集可能
		} else {
			return false;
		}
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		if (hasEditModel() && getEditModel().isEditing() && rowIndex >= 0 && rowIndex < getEditModel().getSubFilterCount()) {
			if (columnIndex == CI_COMMENT) {
				// 編集中なら、コメントのみ変更を受け付ける
				Object oldValue = getEditModel().getSubFilter(rowIndex).getComment();
				if (!oldValue.equals(value)) {
					getEditModel().getSubFilter(rowIndex).setComment(value==null ? "" : value.toString());
					fireTableCellUpdated(rowIndex, columnIndex);
				}
				return;
			}
		}
		// 書き換え可能ではない場合は例外をスロー
		throw new UnsupportedOperationException("loc=(" + rowIndex + "," + columnIndex + ") / value=" + String.valueOf(value));
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex >= 0 && rowIndex >= 0 && rowIndex < getRowCount()) {
			MacroSubModuleRuntimeData data = getEditModel().getSubFilter(rowIndex);
			if (columnIndex == CI_WAITFILTERS) {
				return getDisplayFilterRunOrder(data);
			}
			else if (columnIndex == CI_FILTERNAME) {
				return getDisplayFilterName(data);
			}
			else if (columnIndex == CI_COMMENT) {
				return getDisplayFilterComment(data);
			}
			else if (columnIndex < (data.getArgumentCount()+CI_FIRSTARG)) {
				return data.getArgument(columnIndex - CI_FIRSTARG);
			}
		}
		
		return null;
	}
	
	public void fireWaitFiltersUpdate(final ModuleRuntimeData moduledata) {
		int rowIndex = rowIndexOf(moduledata);
		if (rowIndex >= 0) {
			fireWaitFiltersUpdate(rowIndex);
		}
	}
	
	public void fireWaitFiltersUpdate(int rowIndex) {
		fireTableCellUpdated(rowIndex, CI_WAITFILTERS);
	}
	
	public void fireArgumentDefinitionUpdated(final ModuleRuntimeData moduledata) {
		int rowIndex = rowIndexOf(moduledata);
		if (rowIndex >= 0) {
			fireTableRowsUpdated(rowIndex, rowIndex);
		}
	}
	
	public void fireArgumentDefinitionUpdated(int rowIndex) {
		fireTableRowsUpdated(rowIndex, rowIndex);
	}
	
	public void fireArgumentValueUpdated(final ModuleRuntimeData moduledata, int argIndex) {
		int rowIndex = rowIndexOf(moduledata);
		if (rowIndex >= 0) {
			fireArgumentValueUpdated(rowIndex, argIndex);
		}
	}
	
	public void fireArgumentValueUpdated(int rowIndex, int argIndex) {
		fireTableCellUpdated(rowIndex, argIndex+CI_FIRSTARG);
	}
	
	public void fireArgumentValuesUpdated(final ModuleRuntimeData moduledata, int firstArgIndex, int lastArgIndex) {
		int rowIndex = rowIndexOf(moduledata);
		if (rowIndex >= 0) {
			fireArgumentValuesUpdated(rowIndex, firstArgIndex, lastArgIndex);
		}
	}
	
	public void fireArgumentValuesUpdated(int rowIndex, int firstArgIndex, int lastArgIndex) {
		int colIndex, toColIndex;
		if (firstArgIndex > lastArgIndex) {
			colIndex   = lastArgIndex;
			toColIndex = firstArgIndex;
		} else {
			colIndex   = firstArgIndex;
			toColIndex = lastArgIndex;
		}
		if (toColIndex < 0) {
			fireTableRowsUpdated(rowIndex, rowIndex);
		} else {
			colIndex = (colIndex < 0 ? 0 : colIndex) + CI_FIRSTARG;
			toColIndex += CI_FIRSTARG;
			for (; colIndex <= toColIndex; colIndex++) {
				fireTableCellUpdated(rowIndex, colIndex);
			}
		}
	}
	
	public void fireAllArgumentsUpdated() {
		if (getRowCount() > 0) {
			fireTableRowsUpdated(0, getRowCount()-1);
		} else {
			fireTableDataChanged();
		}
	}
	
	public void fireRunOrderLinkUpdated(final ModuleRuntimeData moduledata) {
		int rowIndex = rowIndexOf(moduledata);
		if (rowIndex >= 0) {
			fireRunOrderLinkUpdated(rowIndex);
		}
	}
	
	public void fireRunOrderLinkUpdated(int rowIndex) {
		fireTableCellUpdated(rowIndex, CI_WAITFILTERS);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected String getDisplayFilterRunOrder(MacroSubModuleRuntimeData moduledata) {
		if (moduledata.getRunOrderLinkEnabled()) {
			// 任意のフィルタを待機
			if (moduledata.getWaitModuleCount() > 0) {
				// 待機番号
				return moduledata.getWaitModuleRunNumberString();
			} else {
				// 待機しない
				return RunnerMessages.getInstance().MacroSubFiltersTable_Value_NoWait;
			}
		} else {
			// 直前のフィルタのみ待機
			return RunnerMessages.getInstance().MacroSubFiltersTable_Value_WaitPrevious;
		}
	}
	
	protected String getDisplayFilterName(MacroSubModuleRuntimeData moduledata) {
		return moduledata.getName();
	}
	
	protected String getDisplayFilterComment(MacroSubModuleRuntimeData moduledata) {
		return moduledata.getComment();
	}

	/**
	 * 編集用モデルデータが存在しない場合に例外をスローする。
	 * @throws IndexOutOfBoundsException	編集用モデルデータが <tt>null</tt> の場合
	 */
	protected void ensureEditModel() {
		if (!hasEditModel()) {
			throw new IndexOutOfBoundsException("No such data.");
		}
	}

	/**
	 * 指定されたモジュールの実行順序リンク情報をクリアし、関連するサブモジュールの表示を更新する。
	 * なお、指定されたモジュールの表示内容は変更しない。
	 * @param subModule	実行順序リンクをクリアするサブモジュール
	 * @throws NullPointerException	指定されたサブモジュールが <tt>null</tt> の場合
	 * @since 3.1.0
	 */
	protected void clearRunOrderLink(MacroSubModuleRuntimeData subModule) {
		// 指定モジュールが待機するモジュールを除去
		subModule.disconnectAllWaitModules();
		
		// 次に実行するモジュールを除去
		if (!subModule.isEmptyNextModules()) {
			TreeSet<Integer> rowIndices = new TreeSet<Integer>();
			MacroSubModuleRuntimeData nextModule;
			//--- 次に実行するモジュールがなくなるまで、一つずつ削除
			while ((nextModule = subModule.getFirstNextModule()) != null) {
				int rowIndex = rowIndexOf(nextModule);
				if (rowIndex >= 0) {
					rowIndices.add(rowIndex);
				}
				subModule.disconnectNextModule(nextModule);
			}
			//--- 次に実行するモジュールの待機モジュール表示を更新
			for (Integer rowIndex : rowIndices) {
				fireTableCellUpdated(rowIndex, CI_WAITFILTERS);
			}
		}
	}
	
	abstract protected boolean hasEditModel();
	
	abstract protected MacroFilterEditModel getEditModel();
	
	protected int rowIndexOf(final ModuleRuntimeData moduledata) {
		return (hasEditModel() ? getEditModel().indexOfSubFilter(moduledata) : -1);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
