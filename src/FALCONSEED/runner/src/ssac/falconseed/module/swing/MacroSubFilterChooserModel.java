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
 * @(#)MacroSubFilterChooserModel.java	3.1.0	2014/05/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import ssac.falconseed.module.ModuleRuntimeData;
import ssac.falconseed.runner.RunnerMessages;

/**
 * サブフィルタを選択するダイアログ専用のデータモデル。
 * 
 * @version 3.1.0	2014/05/14
 * @since 3.1.0
 */
public class MacroSubFilterChooserModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final ModuleRunnoComparator	_runnoComparator = new ModuleRunnoComparator();

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** すべてのサブフィルタのリスト **/
	protected final List<? extends ModuleRuntimeData>	_allSubFilters;
	/** 選択候補のサブフィルタインデックス **/
	protected ArrayList<ModuleRuntimeData>	_candModules;
	/** 選択されているサブフィルタインデックス **/
	protected ArrayList<ModuleRuntimeData>	_selectedModules;
	/** 選択不可能なため選択解除されたフィルタ **/
	protected ArrayList<ModuleRuntimeData>	_canceledModules;
	/** 選択候補のテーブルモデル **/
	protected CandidateTableModel			_candTableModel;
	/** 選択済みのテーブルモデル **/
	protected SelectedTableModel			_selectedTableModel;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MacroSubFilterChooserModel(List<? extends ModuleRuntimeData> allSubFilters) {
		this(allSubFilters, null, null);
	}
	
	public MacroSubFilterChooserModel(List<? extends ModuleRuntimeData> allSubFilters,
									Collection<? extends ModuleRuntimeData> ignoreSubFilters)
	{
		this(allSubFilters, null, ignoreSubFilters);
	}
	
	public MacroSubFilterChooserModel(List<? extends ModuleRuntimeData> allSubFilters,
									Collection<? extends ModuleRuntimeData> selectedSubFilters,
									Collection<? extends ModuleRuntimeData> ignoreSubFilters)
	{
		if (allSubFilters.isEmpty())
			throw new IllegalArgumentException();
		
		_allSubFilters   = allSubFilters;
		_candModules     = new ArrayList<ModuleRuntimeData>(allSubFilters);
		_selectedModules = new ArrayList<ModuleRuntimeData>(allSubFilters);
		
		// 除外モジュール
		if (ignoreSubFilters != null && !ignoreSubFilters.isEmpty()) {
			_candModules    .removeAll(ignoreSubFilters);
			_selectedModules.removeAll(ignoreSubFilters);
		}
		
		// 選択モジュール
		if (selectedSubFilters != null && !selectedSubFilters.isEmpty()) {
			_candModules    .removeAll(selectedSubFilters);
			_selectedModules.retainAll(selectedSubFilters);
			//--- 選択を取り消されたモジュールを取得
			int numCanceled = selectedSubFilters.size() - _selectedModules.size();
			if (numCanceled > 0) {
				_canceledModules = new ArrayList<ModuleRuntimeData>(selectedSubFilters);
				_canceledModules.removeAll(_selectedModules);
				Collections.sort(_canceledModules, _runnoComparator);
			}
		} else {
			_selectedModules.clear();
		}
		
		// 実行番号でのソート
		Collections.sort(_candModules, _runnoComparator);
		Collections.sort(_selectedModules, _runnoComparator);
		
		// テーブルモデルの生成
		_candTableModel     = new CandidateTableModel();
		_selectedTableModel = new SelectedTableModel();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isEmpty() {
		return (_candModules.isEmpty() && _selectedModules.isEmpty());
	}
	
	public List<ModuleRuntimeData> getSelectedModuleList() {
		return _selectedModules;
	}

	/**
	 * 選択可能なモジュールではないため、モデル生成時に選択解除されたモジュールのリスト
	 * @return	モジュールのリスト
	 */
	public List<ModuleRuntimeData> getCanceledModuleList() {
		if (_canceledModules != null && !_canceledModules.isEmpty()) {
			return _canceledModules;
		} else {
			return Collections.emptyList();
		}
	}

	/**
	 * 選択候補フィルタのテーブルモデルを取得する。
	 * @return	選択候補のテーブルモデル
	 */
	public CandidateTableModel getCandidateTableModel() {
		return _candTableModel;
	}

	/**
	 * 選択済みフィルタのテーブルモデルを取得する。
	 * @return	選択済みのテーブルモデル
	 */
	public SelectedTableModel getSelectedTableModel() {
		return _selectedTableModel;
	}

	/**
	 * 指定されたフィルタを選択済みにする。
	 * @param module	新たに選択するフィルタモジュール
	 * @return	選択済みに変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	引数のモジュールが選択候補ではない場合
	 */
	public boolean selectModule(ModuleRuntimeData module) {
		if (module == null)
			throw new NullPointerException();
		
		// 選択済みか
		if (_selectedModules.contains(module)) {
			return false;	// already selected
		}
		
		// 選択候補の位置を取得
		int rowIndex = _candTableModel.rowIndexOf(module);
		if (rowIndex < 0) {
			throw new IllegalArgumentException("module is not candidate!");
		}
		
		// 選択候補から除外し、選択済みに追加
		_candTableModel.removeRow(rowIndex);
		_selectedTableModel.appendRowData(module);
		return true;
	}

	/**
	 * 指定されたフィルタの選択を解除し、選択候補にする。
	 * @param module	選択解除するフィルタモジュール
	 * @return	選択が解除された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	引数のモジュールが選択候補ではない場合
	 */
	public boolean deselectModule(ModuleRuntimeData module) {
		if (module == null)
			throw new NullPointerException();
		
		// 選択候補か
		if (_candModules.contains(module)) {
			return false;	// already deselected
		}
		
		// 選択済みの位置を取得
		int rowIndex = _selectedTableModel.rowIndexOf(module);
		if (rowIndex < 0) {
			throw new IllegalArgumentException("module is not candidate!");
		}
		
		// 選択済みから除外し、選択候補に追加
		_selectedTableModel.removeRow(rowIndex);
		_candTableModel.appendRowData(module);
		return true;
	}

	/**
	 * 指定されたすべてのフィルタを選択済みにする。
	 * @param modules	新たに選択するフィルタモジュールのコレクション
	 * @return	一つ以上のモジュールが選択済みに変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	コレクションに <tt>null</tt> が含まれている場合、もしくは選択候補ではないモジュールが含まれている場合
	 */
	public boolean selectAllModules(Collection<ModuleRuntimeData> modules) {
		boolean modified = false;
		for (ModuleRuntimeData data : modules) {
			if (data == null)
				throw new IllegalArgumentException("modules contains a null!");
			//--- 選択済みか
			if (_selectedModules.contains(data)) {
				continue;	// already selected
			}
			//--- 選択候補の位置を取得
			int rowIndex = _candTableModel.rowIndexOf(data);
			if (rowIndex < 0) {
				throw new IllegalArgumentException("modules contains a no candidate!");
			}
			//--- 選択候補から除外し、選択済みに追加
			_candTableModel.removeRow(rowIndex);
			_selectedTableModel.appendRowData(data);
			modified = true;
		}
		return modified;
	}

	/**
	 * 指定されたすべてのフィルタの選択を解除し、選択候補にする。
	 * @param modules	選択解除するフィルタモジュールのコレクション
	 * @return	一つ以上のモジュールの選択が解除された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	コレクションに <tt>null</tt> が含まれている場合、もしくは選択候補ではないモジュールが含まれている場合
	 */
	public boolean deselectAllModules(Collection<ModuleRuntimeData> modules) {
		boolean modified = false;
		for (ModuleRuntimeData data : modules) {
			if (data == null)
				throw new IllegalArgumentException("modules contains a null!");
			//--- 選択候補か
			if (_candModules.contains(data)) {
				continue;	// already deselected
			}
			//--- 選択済みの位置を取得
			int rowIndex = _selectedTableModel.rowIndexOf(data);
			if (rowIndex < 0) {
				throw new IllegalArgumentException("modules contains a no candidate!");
			}
			//--- 選択済みから除外し、選択候補に追加
			_selectedTableModel.removeRow(rowIndex);
			_candTableModel.appendRowData(data);
			modified = true;
		}
		return modified;
	}

	/**
	 * 指定されたインデックスに対応するすべての選択候補フィルタを選択済みにする。
	 * @param targetIndices	選択済みにするフィルタの選択候補リストのインデックス配列
	 * @return	一つ以上のモジュールが選択済みに変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IndexOutOfBoundsException	インデックスが正しくない場合
	 */
	public boolean selectAllModules(int[] targetIndices) {
		boolean modified = false;
		//--- 選択済みに追加
		for (int index : targetIndices) {
			_selectedTableModel.appendRowData(_candModules.get(index));
			modified = true;
		}
		//--- 選択候補から除外
		for (int i = targetIndices.length - 1; i >= 0; --i) {
			_candTableModel.removeRow(targetIndices[i]);
		}
		return modified;
	}

	/**
	 * 指定されたインデックスに対応するすべての選択済みフィルタの選択を解除し、選択候補にする。
	 * @param targetIndices	選択解除するフィルタの選択済みリストのインデックス配列
	 * @return	一つ以上のモジュールの選択が解除された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IndexOutOfBoundsException	インデックスが正しくない場合
	 */
	public boolean deselectAllModules(int[] targetIndices) {
		boolean modified = false;
		//--- 選択候補に追加
		for (int index : targetIndices) {
			_candTableModel.appendRowData(_selectedModules.get(index));
			modified = true;
		}
		//--- 選択済みから除外
		for (int i = targetIndices.length - 1; i >= 0; --i) {
			_selectedTableModel.removeRow(targetIndices[i]);
		}
		return modified;
	}

	/**
	 * 現在選択候補にあるすべてのモジュールを選択済みにする。
	 * @return	一つ以上のモジュールが選択済みに変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean selectAll() {
		if (_candModules.isEmpty())
			return false;	// alread selected
		
		// 選択済みに追加
		int candRowCount = _candModules.size();
		for (ModuleRuntimeData data : _candModules) {
			_selectedTableModel.appendRowData(data);
		}
		
		// 選択候補をすべて除外
		_candModules.clear();
		_candTableModel.fireTableRowsDeleted(0, candRowCount - 1);
		return true;
	}

	/**
	 * 現在選択済みにあるすべてのモジュールの選択を解除する。
	 * @return	一つ以上のモジュールの選択が解除された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean deselectAll() {
		if (_selectedModules.isEmpty())
			return false;	// alread deselected
		
		// 選択候補に追加
		int selRowCount = _selectedModules.size();
		for (ModuleRuntimeData data : _selectedModules) {
			_candTableModel.appendRowData(data);
		}
		
		// 選択済みをすべて除外
		_selectedModules.clear();
		_selectedTableModel.fireTableRowsDeleted(0, selRowCount - 1);
		return true;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	static protected class ModuleRunnoComparator implements Comparator<ModuleRuntimeData>
	{
		@Override
		public int compare(ModuleRuntimeData data1, ModuleRuntimeData data2) {
			long runno1 = data1.getRunNo();
			long runno2 = data2.getRunNo();
			
			if (runno1 < runno2) {
				return (-1);
			}
			else if (runno1 > runno2) {
				return 1;
			}
			else {
				return 0;
			}
		}
	}

	/**
	 * 選択候補のサブフィルタテーブルモデル
	 */
	static protected abstract class ChooserTableModel extends AbstractTableModel
	{
		//------------------------------------------------------------
		// Constants
		//------------------------------------------------------------

		private static final long serialVersionUID = 1L;

		static public final int CI_RUNNO		= 0;
		static public final int	CI_FILTERNAME	= 1;
		static public final int CI_COMMENT		= 2;
		static public final int NUM_COLUMNS		= 3;

		//------------------------------------------------------------
		// Fields
		//------------------------------------------------------------

		//------------------------------------------------------------
		// Constructions
		//------------------------------------------------------------

		//------------------------------------------------------------
		// Public interfaces
		//------------------------------------------------------------

		/**
		 * 指定されたモジュールの行インデックスを取得する。
		 * @param data	検索対象のモジュール
		 * @return	見つかった場合は 0以上のインデックス、見つからなかった場合は負の値
		 * @throws NullPointerException	引数が <tt>null</tt> の場合
		 */
		public int rowIndexOf(final ModuleRuntimeData data) {
			return Collections.binarySearch(getModuleList(), data, _runnoComparator);
		}

		/**
		 * 指定された行インデックスに対応するモジュール実行データを返す。
		 * @param rowIndex	行インデックス
		 * @return モジュール実行データ
		 * @throws IndexOutOfBoundsException	インデックスが適切ではない場合
		 */
		public ModuleRuntimeData getRowData(int rowIndex) {
			return getModuleList().get(rowIndex);
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
		public void appendRowData(final ModuleRuntimeData newData) {
			// 挿入位置を取得
			int find = Collections.binarySearch(getModuleList(), newData, _runnoComparator);
			if (find >= 0) {
				return;	// already exists
			}
			int rowIndex = -(find + 1);
			getModuleList().add(rowIndex, newData);
			fireTableRowsInserted(rowIndex, rowIndex);
		}

		/**
		 * 指定されたインデックスの行を削除する。
		 * このメソッドでは、行が削除された場合のみ、<code>fireTableRowsDeleted</code> を呼び出す。
		 * <p><b>注：</b>
		 * <blockquote>
		 * 実行番号や列数の更新は行わないため、呼び出し側で適切に処理すること。
		 * </blockquote>
		 * @param rowIndex	行インデックス
		 */
		public void removeRow(int rowIndex) {
			if (getModuleList().remove(rowIndex) != null) {
				fireTableRowsDeleted(rowIndex, rowIndex);
			}
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return Object.class;
		}

		@Override
		public String getColumnName(int columnIndex) {
			if (columnIndex == CI_RUNNO) {
				// 実行番号
				return RunnerMessages.getInstance().MacroSubFiltersTableColumnNumber;
			}
			else if (columnIndex == CI_FILTERNAME) {
				// フィルタ名
				return RunnerMessages.getInstance().MacroSubFiltersTableColumnName;
			}
			else if (columnIndex == CI_COMMENT) {
				// コメント
				return RunnerMessages.getInstance().MacroSubFiltersTableColumnComment;
			}
			else {
				return super.getColumnName(columnIndex);
			}
		}
		
		public String getRowName(int rowIndex) {
			if (rowIndex >= 0 && rowIndex < getRowCount()) {
				return String.valueOf(getRowData(rowIndex).getRunNo());
			} else {
				return String.valueOf(rowIndex);
			}
		}

		@Override
		public int getColumnCount() {
			return NUM_COLUMNS;
		}
		
		@Override
		public int getRowCount() {
			return getModuleList().size();
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

		@Override
		public void setValueAt(Object value, int rowIndex, int columnIndex) {
			throw new UnsupportedOperationException("loc=(" + rowIndex + "," + columnIndex + ") / value=" + String.valueOf(value));
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (columnIndex >= 0 && rowIndex >= 0 && rowIndex < getRowCount()) {
				ModuleRuntimeData data = getRowData(rowIndex);
				if (columnIndex == CI_RUNNO) {
					return data.getRunNo();
				}
				else if (columnIndex == CI_FILTERNAME) {
					return data.getName();
				}
				else if (columnIndex == CI_COMMENT) {
					return data.getComment();
				}
			}
			
			return null;
		}

		//------------------------------------------------------------
		// Internal methods
		//------------------------------------------------------------
		
		abstract List<ModuleRuntimeData> getModuleList();
	}
	
	public class CandidateTableModel extends ChooserTableModel
	{
		private static final long serialVersionUID = 1L;

		@Override
		List<ModuleRuntimeData> getModuleList() {
			return _candModules;
		}
	}
	
	public class SelectedTableModel extends ChooserTableModel
	{
		private static final long serialVersionUID = 1L;

		@Override
		List<ModuleRuntimeData> getModuleList() {
			return _selectedModules;
		}
	}
}
