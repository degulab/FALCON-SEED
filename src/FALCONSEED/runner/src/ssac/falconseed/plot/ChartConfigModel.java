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
 *  Copyright 2007-2013  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Hideaki Yagi (MRI)
 */
/*
 * @(#)ChartConfigModel.java	2.1.0	2013/07/22
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.plot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.TreeSet;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import ssac.falconseed.editor.plugin.csv.CsvFileMessages;
import ssac.falconseed.editor.plugin.csv.CsvFileModel;
import ssac.falconseed.runner.RunnerMessages;
import ssac.util.logging.AppLogger;
import ssac.util.nio.csv.CsvRecordCursor;
import ssac.util.swing.plot.AbDataField;
import ssac.util.swing.plot.AbDataTable;
import ssac.util.swing.plot.DefaultDataSeries;
import ssac.util.swing.plot.IDataField;
import ssac.util.swing.plot.IDataTable;
import ssac.util.swing.plot.PlotDateTimeFormats;

/**
 * チャート設定モデル。
 * 
 * @version 2.1.0	2013/07/22
 * @since 2.1.0
 */
public class ChartConfigModel extends AbDataTable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/**
	 * チャートの種類
	 */
	public enum ChartStyles {
		/** 散布図 **/
		SCATTER,
		/** 折れ線グラフ **/
		LINE,
	};

	/**
	 * 不正値もしくは空白値の扱い
	 */
	public enum ChartInvalidValuePolicy {
		/** 無視する **/
		SKIP_RECORD,
		/** 0 値として扱う **/
		AS_ZERO,
		/** 線でつなぐ **/
		CONNECTED,
	};
	
	public enum ChartDataTypes {
		/** 数値 **/
		DECIMAL,
		/** 日付時刻 **/
		DATETIME,
		/** テキスト **/
		TEXT,
	};

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 対象ファイルの情報 **/
	protected final CsvFileModel	_targetModel;
	/** チャート描画に使用するファイルのカーソル **/
	protected CsvRecordCursor	_csvCursor;

	/** 対象ファイルのフィールド数 **/
	protected final int	_fieldCount;
	/** フィールド名キャッシュ **/
	protected final String[]	_aryFieldNameCache;
	/** 対象ファイルのフィールド位置情報 **/
	protected final ChartConfigDataFieldModel[] _aryFieldModels;
	/** レコード番号をプロットするためのインスタンス **/
	protected final ChartConfigRecordNumberField	_defRecordNumberField;

	/** ヘッダーレコード数 **/
	private long		_numHeaderRecords;
	/** 選択範囲のデータレコード数 **/
	private long		_numDataReocrds;
	/** データレコードの開始インデックス **/
	private long		_firstDataRecordIndex;
	/** データレコードの終了インデックス(レコードを含まない) **/
	private long		_lastDataRecordIndex;

	/** チャート種類 **/
	private ChartStyles	_chartStyle;
	/** 無効値描画ポリシー **/
	private ChartInvalidValuePolicy	_chartPolicy;
	/** チャートのタイトル **/
	private String		_chartTitle;
	/** チャートの X 軸ラベル **/
	private String		_xChartLabel;
	/** チャートの Y 軸ラベル **/
	private String		_yChartLabel;
	/** 日付時刻フォーマット **/
	private PlotDateTimeFormats	_dtCustomFormat;
	/** X 軸データ型 **/
	private ChartDataTypes	_xDataType;
	/** Y 軸データ型 **/
	private ChartDataTypes	_yDataType;
	/** データ系列 **/
	private ArrayList<DefaultDataSeries>	_listSeries;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ChartConfigModel(ChartConfigModel srcModel) {
		if (srcModel == null)
			throw new NullPointerException("The specified ChartConfigModel object is null.");
		
		// copy fields
		_targetModel = srcModel._targetModel;
		_csvCursor   = srcModel._csvCursor;
		_fieldCount  = _csvCursor.getFileData().getMaxFieldSize();
		_numHeaderRecords = srcModel._numHeaderRecords;
		_numDataReocrds   = srcModel._numDataReocrds;
		_firstDataRecordIndex = srcModel._firstDataRecordIndex;
		_lastDataRecordIndex  = srcModel._lastDataRecordIndex;
		_chartStyle = srcModel._chartStyle;
		_chartPolicy = srcModel._chartPolicy;
		_chartTitle  = srcModel._chartTitle;
		_xChartLabel = srcModel._xChartLabel;
		_yChartLabel = srcModel._yChartLabel;
		_dtCustomFormat = srcModel._dtCustomFormat;
		_xDataType = srcModel._xDataType;
		_yDataType = srcModel._yDataType;
		
		// create instance
		_defRecordNumberField = new ChartConfigRecordNumberField();
		_aryFieldNameCache = Arrays.copyOf(srcModel._aryFieldNameCache, _fieldCount);
		_aryFieldModels = new ChartConfigDataFieldModel[_fieldCount];
		for (int i = 0; i < _fieldCount; ++i) {
			_aryFieldModels[i] = new ChartConfigDataFieldModel(i);
		}
		_listSeries = new ArrayList<DefaultDataSeries>(srcModel._listSeries.size());
		for (int i = 0; i < srcModel._listSeries.size(); ++i) {
			DefaultDataSeries series = srcModel._listSeries.get(i);
			IDataField xField = series.getXField();
			IDataField yField = series.getYField();
			DefaultDataSeries newSeries = new DefaultDataSeries();
			newSeries.setLegend(series.getLegend());
			if (xField instanceof ChartConfigRecordNumberField)
				newSeries.setXField(_defRecordNumberField);
			else
				newSeries.setXField(_aryFieldModels[xField.getFieldIndex()]);
			if (yField instanceof ChartConfigRecordNumberField)
				newSeries.setYField(_defRecordNumberField);
			else
				newSeries.setYField(_aryFieldModels[yField.getFieldIndex()]);
			_listSeries.add(newSeries);
		}
	}
	
	public ChartConfigModel(CsvFileModel csvModel, ChartStyles initStyle) {
		this(csvModel, initStyle, -1, -1, null);
	}
	
	public ChartConfigModel(CsvFileModel csvModel, ChartStyles initStyle, int minSelectedRow, int maxSelectedRow, int[] selectedColumns) {
		if (csvModel == null)
			throw new NullPointerException("The specified CsvFileModel object is null.");
		if (csvModel.getColumnCount() <= 0 || csvModel.getRowCount() <= 0)
			throw new IllegalArgumentException("The specified CsvFileModel object is empty.");
		if (initStyle == null)
			throw new NullPointerException("The specified ChartStyles object is null.");
		_targetModel = csvModel;
		
		// data records
		_csvCursor = csvModel.getSearchCursor();
		_fieldCount = _csvCursor.getFileData().getMaxFieldSize();
		_defRecordNumberField = new ChartConfigRecordNumberField();
		_aryFieldNameCache = new String[_fieldCount];
		_aryFieldModels = new ChartConfigDataFieldModel[_fieldCount];
		for (int i = 0; i < _fieldCount; ++i) {
			_aryFieldModels[i] = new ChartConfigDataFieldModel(i);
		}
		_numHeaderRecords = 0L;
		int[] recRange = null;
		if (minSelectedRow >= 0 && maxSelectedRow >= 0) {
			recRange = new int[2];
			if (minSelectedRow > maxSelectedRow) {
				recRange[0] = maxSelectedRow;
				recRange[1] = minSelectedRow;
			} else {
				recRange[0] = minSelectedRow;
				recRange[1] = maxSelectedRow;
			}
			_firstDataRecordIndex = recRange[0];
			_lastDataRecordIndex  = recRange[1] + 1L;
		} else {
			_firstDataRecordIndex = 0L;
			_lastDataRecordIndex  = _csvCursor.getRecordSize();
		}
		_numDataReocrds = _lastDataRecordIndex - _firstDataRecordIndex;
		
		// initialize
		_chartStyle = initStyle;
		_chartPolicy = ChartInvalidValuePolicy.SKIP_RECORD;
		_chartTitle  = "";
		_xChartLabel = "";
		_yChartLabel = "";
		_dtCustomFormat = null;
		_xDataType = ChartDataTypes.DECIMAL;
		_yDataType = ChartDataTypes.DECIMAL;

		if (selectedColumns != null && selectedColumns.length > 0) {
			// X軸はレコード番号、Y軸は選択列
			_listSeries = new ArrayList<DefaultDataSeries>(selectedColumns.length);
			Arrays.sort(selectedColumns);
			for (int si = 0; si < selectedColumns.length; ++si) {
				DefaultDataSeries series = new DefaultDataSeries();
				series.setXField(_defRecordNumberField);
				series.setYField(_aryFieldModels[selectedColumns[si]]);
				_listSeries.add(series);
			}
		} else {
			_listSeries = new ArrayList<DefaultDataSeries>();
		}
	}

	//------------------------------------------------------------
	// Implements ssac.util.swing.IDataTable interfaces
	//------------------------------------------------------------

	/**
	 * 総レコード数を返す。
	 * レコード数は、ヘッダレコード数とデータレコード数の合計となる。
	 */
	public long getRecordCount() {
		return _csvCursor.getRecordSize();
	}
	
	/**
	 * ヘッダとなるレコード数を返す。
	 */
	public long getHeaderRecordCount() {
		return _numHeaderRecords;
	}
	
	/**
	 * 実データの総レコード数を返す。
	 */
	public long getDataRecordCount() {
		return _numDataReocrds;
	}
	
	/**
	 * 最大フィールド数を返す。
	 */
	public int getFieldCount() {
		return _fieldCount;
	}
	
	/**
	 * 指定されたフィールドの名称を返す。
	 * @param fieldIndex	フィールドの位置を示すインデックス
	 * @return	フィールド名
	 * @throws IndexOutOfBoundsException	フィールドインデックスが範囲外の場合
	 */
	public String getFieldName(int fieldIndex) {
		validFieldIndex(fieldIndex, _fieldCount);
		String fieldName = _aryFieldNameCache[fieldIndex];
		if (fieldName != null) {
			return fieldName;	// キャッシュ済みフィールド名
		}
		
		// フィールド名を生成
		StringBuilder sb = new StringBuilder();
		
		// append field number
		sb.append('[');
		sb.append(fieldIndex + 1);
		sb.append(']');

		// check header field exists
		boolean hasFieldHeader = _csvCursor.getFileData().getUseHeaderLine();
		if (hasFieldHeader) {
			fieldName = _csvCursor.getFileData().getFieldName(fieldIndex);
			if (fieldName != null && fieldName.length() > 0) {
				sb.append(fieldName);
			} else {
				fieldName = null;
			}
		}
		
		// append fieldname
		if (_numHeaderRecords > 0L) {
			long lastRecordIndex = 0L;
			try {
				for (long ri = 0L; ri < _numHeaderRecords; ++ri) {
					lastRecordIndex = ri;
					String value = _csvCursor.getRecord(ri)[fieldIndex];
					if (value != null && value.length() > 0) {
						if (fieldName != null) {
							sb.append('_');
						}
						fieldName = value;
						sb.append(value);
					}
				}
			}
			catch (IOException ex) {
				final String exMessage = ex.getLocalizedMessage();
				if (exMessage != null) {
					AppLogger.error("(" + lastRecordIndex + ", " + fieldIndex + ") Failed to read CSV file : " + exMessage, ex);
					sb.append('_');
					sb.append(exMessage);
					// no cached
					return sb.toString();
				} else {
					AppLogger.error("(" + lastRecordIndex + ", " + fieldIndex + ") Failed to read CSV file.",ex);
					sb.append('_');
					sb.append(CsvFileMessages.getInstance().msgValueReadError);
					// no cached
					return sb.toString();
				}
			}
		}
		
		// cached field name
		fieldName = sb.toString();
		_aryFieldNameCache[fieldIndex] = fieldName;
		return fieldName;
	}
	
	/**
	 * 指定された位置のヘッダ値を返す。
	 * @param headerRecordIndex	レコードの位置を示すインデックス(<code>getHeaderRecordCount()</code> を上限とする)
	 * @param fieldIndex	フィールドの位置を示すインデックス
	 * @return	ヘッダ値
	 * @throws IndexOutOfBoundsException	レコードインデックスもしくはフィールドインデックスが範囲外の場合
	 */
	public Object getHeaderValue(long headerRecordIndex, int fieldIndex)
	{
		validHeaderRecordIndex(headerRecordIndex, _numHeaderRecords);
		validFieldIndex(fieldIndex, getFieldCount());
		try {
			return _csvCursor.getRecord(headerRecordIndex)[fieldIndex];
		}
		catch (Exception ex) {
			final String exMessage = ex.getLocalizedMessage();
			if (exMessage != null) {
				AppLogger.error("(" + headerRecordIndex + ", " + fieldIndex + ") Failed to read CSV file : " + exMessage, ex);
				return ex;
			} else {
				AppLogger.error("(" + headerRecordIndex + ", " + fieldIndex + ") Failed to read CSV file.",ex);
				return CsvFileMessages.getInstance().msgValueReadError;
			}
		}
	}
	
	/**
	 * 指定された位置の実値を返す。
	 * @param dataRecordIndex	レコードの位置を示すインデックス(<code>getDataRecordCount()</code> を上限とする)
	 * @param fieldIndex	フィールドの位置を示すインデックス
	 * @return	実値
	 * @throws IndexOutOfBoundsException	レコードインデックスもしくはフィールドインデックスが範囲外の場合
	 */
	public Object getValue(long dataRecordIndex, int fieldIndex) {
		validDataRecordIndex(dataRecordIndex, _numDataReocrds);
		validFieldIndex(fieldIndex, getFieldCount());
		
		dataRecordIndex += _firstDataRecordIndex;
		try {
			return _csvCursor.getRecord(dataRecordIndex)[fieldIndex];
		}
		catch (Exception ex) {
			final String exMessage = ex.getLocalizedMessage();
			if (exMessage != null) {
				AppLogger.error("(" + dataRecordIndex + ", " + fieldIndex + ") Failed to read CSV file : " + exMessage, ex);
				return ex;
			} else {
				AppLogger.error("(" + dataRecordIndex + ", " + fieldIndex + ") Failed to read CSV file.",ex);
				return CsvFileMessages.getInstance().msgValueReadError;
			}
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public File getTargetFile() {
		return _targetModel.getTargetFile();
	}
	
	public CsvRecordCursor getCursor() {
		return _csvCursor;
	}
	
	public ChartConfigRecordNumberField getDefaultRecordNumberField() {
		return _defRecordNumberField;
	}
	
	public ChartConfigDataFieldModel getFieldModel(int fieldIndex) {
		return _aryFieldModels[fieldIndex];
	}
	
	public ChartDataFieldComboBoxModel createFieldComboBoxModel() {
		return new ChartDataFieldComboBoxModel();
	}
	
	public void clearFieldNameCache() {
		Arrays.fill(_aryFieldNameCache, null);
	}
	
	public void setHeaderRecordCount(long newSize) {
		if (newSize < 0L)
			newSize = 0L;
		else if (newSize > _csvCursor.getRecordSize())
			newSize = _csvCursor.getRecordSize();
		
		if (newSize != _numHeaderRecords) {
			_numHeaderRecords = newSize;
			clearFieldNameCache();
		}
	}
	
	public long getFirstDataRecordIndex() {
		return _firstDataRecordIndex;
	}
	
	public long getLastDataRecordIndex() {
		return _lastDataRecordIndex;
	}
	
	public void setDataRecordRange(long firstIndex, long lastIndex) {
		// check
		long numRecords = _csvCursor.getRecordSize();
		if (firstIndex < 0L || firstIndex >= numRecords) {
			throw new IllegalArgumentException("First index out of range(" + numRecords + ") : " + firstIndex);
		}
		if (lastIndex < 0L || lastIndex > numRecords) {
			throw new IllegalArgumentException("Last index out of range(" + numRecords + ") : " + lastIndex);
		}
		if (firstIndex > lastIndex) {
			throw new IllegalArgumentException("First index (" + firstIndex + ") greater than Last index (" + lastIndex + ")");
		}
		
		_firstDataRecordIndex = firstIndex;
		_lastDataRecordIndex  = lastIndex;
		_numDataReocrds = lastIndex - firstIndex;
		//_defRecordNumberField.setOffset(firstIndex);
	}
	
	public ChartStyles getChartStyle() {
		return _chartStyle;
	}
	
	public void setChartStyle(ChartStyles newStyle) {
		_chartStyle = newStyle;
	}
	
	public ChartInvalidValuePolicy getInvalidValuePolicy() {
		return _chartPolicy;
	}
	
	public void setInvalidValuePolicy(ChartInvalidValuePolicy policy) {
		_chartPolicy = policy;
	}
	
	public String getChartTitle() {
		return _chartTitle;
	}
	
	public String getXaxisLabel() {
		return _xChartLabel;
	}
	
	public String getYaxisLabel() {
		return _yChartLabel;
	}
	
	public void setChartTitle(String title) {
		_chartTitle = title;
	}
	
	public void setXaxisLabel(String label) {
		_xChartLabel = label;
	}
	
	public void setYaxisLabel(String label) {
		_yChartLabel = label;
	}
	
	public PlotDateTimeFormats getCustomDateTimeFormats() {
		return _dtCustomFormat;
	}
	
	public void setCustomDateTimeFormats(PlotDateTimeFormats formats) {
		_dtCustomFormat = formats;
	}
	
	public ChartDataTypes getXDataType() {
		return _xDataType;
	}
	
	public void setXDataType(ChartDataTypes type) {
		_xDataType = type;
	}
	
	public ChartDataTypes getYDataType() {
		return _yDataType;
	}
	
	public void setYDataType(ChartDataTypes type) {
		_yDataType = type;
	}
	
	public ArrayList<DefaultDataSeries> getDataSeriesList() {
		return _listSeries;
	}

	/**
	 * 指定されたコレクションのフィールドインデックスから、重複ありペアを生成する。
	 * なお、指定されたコレクションの値は内部バッファにコピーされ、フィールドインデックスが
	 * 重複しない配列にソートされる。異なるフィールドインデックスの総数が 2 未満の場合、
	 * このメソッドは空の配列を返す。
	 * @param fieldIndices	フィールドインデックスのコレクション
	 * @return	フィールドインデックスの組合せの 2 次元配列
	 */
	static public int[][] createRepeatedPairsDataFields(Collection<Integer> fieldIndices) {
		if (fieldIndices == null || fieldIndices.size() < 2) {
			return new int[][]{};
		}
		
		return createRepeatedPairsDataFieldsBySortedSet(new TreeSet<Integer>(fieldIndices));
	}
	
	static public int[][] createRepeatedPairsDataFields(int...fieldIndices) {
		if (fieldIndices == null || fieldIndices.length < 2) {
			return new int[][]{};
		}
		
		TreeSet<Integer> tset = new TreeSet<Integer>();
		for (int index : fieldIndices) {
			tset.add(index);
		}
		return createRepeatedPairsDataFieldsBySortedSet(tset);
	}
	
	static public int[][] createRepeatedPairsDataFieldsBySortedSet(TreeSet<Integer> fieldIndices) {
		if (fieldIndices == null || fieldIndices.size() < 2) {
			return new int[][]{};
		}
		
		int[] indices = new int[fieldIndices.size()];
		int len = 0;
		for (int index : fieldIndices) {
			indices[len++] = index;
		}
		int numPairs = len * (len-1);
		
		// ペアの作成
		int index = 0;
		int[][] pairs = new int[numPairs][2];
		for (int i = 0; i < len; ++i) {
			for (int j = 0; j < len; ++j) {
				if (i != j) {
					pairs[index][0] = indices[i];
					pairs[index][1] = indices[j];
					++index;
				}
			}
		}
		
		return pairs;
	}

	/**
	 * 指定されたコレクションのフィールドインデックスから、重複なしペアを生成する。
	 * なお、指定されたコレクションの値は内部バッファにコピーされ、フィールドインデックスが
	 * 重複しない配列にソートされる。異なるフィールドインデックスの総数が 2 未満の場合、
	 * このメソッドは空の配列を返す。
	 * @param fieldIndices	フィールドインデックスのコレクション
	 * @return	フィールドインデックスの重複しない組合せの 2 次元配列
	 */
	static public int[][] createNoRepetitionPairsDataFields(Collection<Integer> fieldIndices) {
		if (fieldIndices == null || fieldIndices.size() <= 1) {
			return new int[][]{};
		}
		
		return createNoRepetitionPairsDataFieldsBySortedSet(new TreeSet<Integer>(fieldIndices));
	}
	
	static public int[][] createNoRepetitionPairsDataFields(int...fieldIndices) {
		if (fieldIndices == null || fieldIndices.length < 2) {
			return new int[][]{};
		}
		
		TreeSet<Integer> tset = new TreeSet<Integer>();
		for (int index : fieldIndices) {
			tset.add(index);
		}
		return createNoRepetitionPairsDataFieldsBySortedSet(tset);
	}
	
	static public int[][] createNoRepetitionPairsDataFieldsBySortedSet(TreeSet<Integer> fieldIndices) {
		if (fieldIndices == null || fieldIndices.size() < 2) {
			return new int[][]{};
		}
		
		int[] indices = new int[fieldIndices.size()];
		int len = 0;
		int numPairs = 0;
		for (int index : fieldIndices) {
			numPairs += len;
			indices[len++] = index;
		}
		
		// ペアの作成
		int index = 0;
		int[][] pairs = new int[numPairs][2];
		for (int i = 0; i < (len-1); ++i) {
			for (int j = i+1; j < len; ++j) {
				pairs[index][0] = indices[i];
				pairs[index][1] = indices[j];
				++index;
			}
		}
		
		return pairs;
	}
	
	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	public class ChartDataFieldComboBoxModel extends AbstractListModel implements ComboBoxModel
	{
		private static final long serialVersionUID = 1L;
		
		private int	_selectedIndex = (-1);
		
		@Override
		public Object getSelectedItem() {
			return (_selectedIndex < 0 ? null : _aryFieldModels[_selectedIndex]);
		}

		@Override
		public void setSelectedItem(Object anItem) {
			int index;
			if (anItem instanceof ChartConfigDataFieldModel) {
				index = ((ChartConfigDataFieldModel)anItem).getFieldIndex();
			} else {
				// no selection
				index = (-1);
			}
			
			if (index != _selectedIndex) {
				_selectedIndex = index;
				fireContentsChanged(this, -1, -1);
			}
		}

		@Override
		public Object getElementAt(int index) {
			return _aryFieldModels[index];
		}

		@Override
		public int getSize() {
			return _aryFieldModels.length;
		}
	}
	
	public class ChartConfigDataFieldModel extends AbDataField
	{
		public ChartConfigDataFieldModel(int fieldIndex) {
			super(fieldIndex);
		}

		@Override
		public IDataTable getDataTable() {
			return ChartConfigModel.this;
		}

		@Override
		public Class<?> getFieldType() {
			return _csvCursor.getFileData().getFieldDataType(_fieldIndex);
		}
	}
	
	public class ChartConfigRecordNumberField extends ChartConfigDataFieldModel
	{
		public ChartConfigRecordNumberField() {
			super(-1);
		}

		@Override
		public Class<?> getFieldType() {
			return Long.class;
		}

		@Override
		public String getFieldName() {
			return RunnerMessages.getInstance().ChartFieldName_RecordNumber;
		}

		@Override
		public Object getFieldValue(long index) {
			return (index+1L);
		}
	}
}
