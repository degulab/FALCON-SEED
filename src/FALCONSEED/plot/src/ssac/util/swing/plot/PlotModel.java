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
 * @(#)PlotModel.java	2.1.0	2013/07/12
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.plot;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * 標準のプロットデータモデル。
 * このオブジェクトでは、プロットのスタイル、プロット対象のデータと定義などを保持する。
 * 
 * @version 2.1.0	2013/07/12
 * @since 2.1.0
 */
public class PlotModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 描画対象のデータテーブル **/
	protected final IDataTable	_datatable;

	/** 設定内容が変更された場合は <tt>true</tt> **/
	protected boolean	_modifiedSettings;
	/** データ系列の制約情報の更新済みフラグ **/
	protected boolean	_dataConstraintsUpdated;

	/** チャートのタイトルラベル **/
	private String		_title;
	/** チャートの X 軸ラベル **/
	private String		_xLabel;
	/** チャートの Y 軸ラベル **/
	private String		_yLabel;
	/** チャートの描画スタイル **/
	private PlotStyles	_styles;
	/** 全データ系列の有効数値範囲の最小値 **/
	private BigDecimal	_entireXMinDecimal;
	private BigDecimal	_entireYMinDecimal;
	/** 全データ系列の有効数値範囲の最大値 **/
	private BigDecimal	_entireXMaxDecimal;
	private BigDecimal	_entireYMaxDecimal;
	/** Double の値範囲に変換するためのスケール **/
	private BigDecimal	_entireXScaleToDouble;
	private BigDecimal	_entireYScaleToDouble;
	/** X 軸のメモリ表示に使用するデータ列 **/
	private PlotDataField	_xTicksField;
	/** Y 軸のメモリ表示に使用するデータ列 **/
	private PlotDataField	_yTicksField;
	/** チャートのデータ系列 **/
	private ArrayList<PlotDataSeries>	_serieslist;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public PlotModel(IDataTable targetTable) {
		this(targetTable, null, null);
	}
	
	public PlotModel(IDataTable targetTable, String caption) {
		this(targetTable, null, caption);
	}
	
	public PlotModel(IDataTable targetTable, PlotStyles styles) {
		this(targetTable, styles, null);
	}
	
	public PlotModel(IDataTable targetTable, PlotStyles styles, String title) {
		if (targetTable == null)
			throw new NullPointerException("The specified target table is null.");
		_datatable = targetTable;
		_styles = (styles==null ? createDefaultStyles() : styles);
		_title = title;
		_serieslist = new ArrayList<PlotDataSeries>();
		_modifiedSettings = true;
		_dataConstraintsUpdated = false;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 描画対象のデータテーブルを返す。
	 */
	public IDataTable getDataTable() {
		return _datatable;
	}

	/**
	 * この設定内容が変更されている場合は <tt>true</tt> を返す。
	 */
	public boolean isModifiedSettings() {
		return (_modifiedSettings || _styles.isModified());
	}

	/**
	 * この設定の変更フラグを設定する。
	 * @param toModified	変更ありとする場合は <tt>true</tt>、変更なしとする場合は <tt>false</tt>
	 */
	public void setSettingsModifiedFlag(boolean toModified) {
		_modifiedSettings = toModified;
	}

	/**
	 * チャートのタイトルラベルを返す。
	 * 設定されていない場合は <tt>null</tt> を返す。
	 */
	public String getTitle() {
		return _title;
	}

	/**
	 * 新しいチャートのタイトルを設定する。
	 * @param newTitle	キャプション
	 */
	public void setTitle(String newTitle) {
		if ((newTitle==null && _title!=null) || (newTitle!=null && !newTitle.equals(_title))) {
			_modifiedSettings = true;
			_title = newTitle;
		}
	}
	
	public String getXLabel() {
		return _xLabel;
	}
	
	public String getYLabel() {
		return _yLabel;
	}
	
	public void setXLabel(String label) {
		if ((label==null && _xLabel!=null) || (label!=null && !label.equals(_xLabel))) {
			_modifiedSettings = true;
			_xLabel = label;
		}
	}
	
	public void setYLabel(String label) {
		if ((label==null && _yLabel!=null) || (label!=null && !label.equals(_yLabel))) {
			_modifiedSettings = true;
			_yLabel = label;
		}
	}
	
	public PlotDataField getXTicksField() {
		return _xTicksField;
	}
	
	public PlotDataField getYTicksField() {
		return _yTicksField;
	}
	
	public void setXTicksField(PlotDataField field) {
		if ((field==null && _xTicksField!=null) || (field!=null && !field.equals(_xTicksField))) {
			_modifiedSettings = true;
			_xTicksField = field;
		}
	}
	
	public void setYTicksField(PlotDataField field) {
		if ((field==null && _yTicksField!=null) || (field!=null && !field.equals(_xTicksField))) {
			_modifiedSettings = true;
			_yTicksField = field;
		}
	}

	/**
	 * チャートのスタイルを返す。
	 * @return	<tt>null</tt> ではない <code>PlotStyles</code> オブジェクト
	 */
	public PlotStyles getStyles() {
		return _styles;
	}

	/**
	 * 新しいチャートのスタイルを設定する。
	 * このメソッドでは、指定されたオブジェクトのクローンを保存する。
	 * @param newStyles	新しいチャートのスタイル
	 */
	public void setStyles(PlotStyles newStyles) {
		boolean toBeClone = true;
		if (newStyles == null) {
			toBeClone = false;
			newStyles = createDefaultStyles();
		}
		
		if (!newStyles.equals(_styles)) {
			_modifiedSettings = true;
			if (toBeClone) {
				_styles = newStyles.clone();
			} else {
				_styles = newStyles;
			}
			// 新しいスタイルの適用のため、変更フラグを設定
			_styles.setModifiedFlag(true);
		}
	}
	
	public void clearAllSeries() {
		if (!_serieslist.isEmpty()) {
			_modifiedSettings = true;
			_serieslist.clear();
			clearDataConstraints();
		}
	}
	
	public int getSeriesCount() {
		return _serieslist.size();
	}
	
	public PlotDataSeries getSeries(int index) {
		return _serieslist.get(index);
	}
	
	public PlotDataSeries setSeries(int index, PlotDataSeries series) {
		if (series == null)
			throw new NullPointerException("The specified 'series' is null.");
		validDataTableEquals(series);

		_modifiedSettings = true;
		clearDataConstraints();
		return _serieslist.set(index, series);
	}
	
	public void addSeries(PlotDataSeries series) {
		if (series == null)
			throw new NullPointerException("The specified 'series' is null.");
		validDataTableEquals(series);

		_modifiedSettings = true;
		_serieslist.add(series);
		clearDataConstraints();
	}
	
	public void insertSeries(int index, PlotDataSeries series) {
		if (series == null)
			throw new NullPointerException("The specified 'series' is null.");
		validDataTableEquals(series);
		
		_modifiedSettings = true;
		_serieslist.add(index, series);
		clearDataConstraints();
	}
	
	public boolean removeSeries(int index) {
		if (index >= 0 && index < _serieslist.size()) {
			_modifiedSettings = true;
			_serieslist.remove(index);
			clearDataConstraints();
			return true;
		}
		return false;
	}

	/**
	 * このオブジェクトの数値制約情報をリセットする。
	 */
	public void resetDataConstraints() {
		clearDataConstraints();
		for (PlotDataSeries series : _serieslist) {
			series.resetDecimalRanges();
		}
	}

	/**
	 * このオブジェクトの数値制約情報が更新済みなら <tt>true</tt> を返す。
	 */
	public boolean isDataConstraintsUpdated() {
		return _dataConstraintsUpdated;
	}

	/**
	 * 更新が必要な場合のみ、数値制約情報を更新する。
	 * このメソッドは、数値制約の更新をデータレコードを主として更新する。
	 */
	public void updateDataConstraintsByRecords() {
		// データ系列から未更新のフィールドを収集
		PlotDataField field;
		TreeMap<Integer, PlotDataField> tmap = new TreeMap<Integer, PlotDataField>();
		for (PlotDataSeries series : _serieslist) {
			// X-Field
			field = series.getXField();
			if (field != null && !field.isDecimalRangeUpdated()) {
				tmap.put(field.getFieldIndex(), field);
			}
			// Y-Field
			field = series.getYField();
			if (field != null && !field.isDecimalRangeUpdated()) {
				tmap.put(field.getFieldIndex(), field);
			}
		}
		
		// データ系列の数値範囲更新
		if (!tmap.isEmpty()) {
			clearDataConstraints();
			PlotDataField[] refreshFields = tmap.values().toArray(new PlotDataField[tmap.size()]);
			long numRecords = _datatable.getDataRecordCount();
			for (long rindex = 0L; rindex < numRecords; ++rindex) {
				for (PlotDataField f : refreshFields) {
					f.refreshDecimalRange(rindex);
				}
			}
			//--- 更新完了フラグ設定
			for (PlotDataField f : refreshFields) {
				f.decimalRangeUpdateFinished();
			}
		}

		updateEntireConstraints();
	}
	
	public void updateEntireConstraints() {
		// 全体の統計情報を更新する
		if (!isDataConstraintsUpdated()) {
			for (PlotDataSeries series : _serieslist) {
				refreshXEntireDecimalRange(series.getMinimumXDecimalValue());
				refreshXEntireDecimalRange(series.getMaximumXDecimalValue());
				refreshYEntireDecimalRange(series.getMinimumYDecimalValue());
				refreshYEntireDecimalRange(series.getMaximumYDecimalValue());
			}
			refreshEntireScaleToDouble();
			_dataConstraintsUpdated = true;
		}
	}
	
	public BigDecimal getXEntireMinimumDecimalValue() {
		return _entireXMinDecimal;
	}
	
	public BigDecimal getYEntireMinimumDecimalValue() {
		return _entireYMinDecimal;
	}
	
	public BigDecimal getXEntireMaximumDecimalValue() {
		return _entireXMaxDecimal;
	}
	
	public BigDecimal getYEntireMaximumDecimalValue() {
		return _entireYMaxDecimal;
	}
	
	public BigDecimal getXEntireScaleToDouble() {
		return _entireXScaleToDouble;
	}
	
	public BigDecimal getYEntireScaleToDouble() {
		return _entireYScaleToDouble;
	}

	/**
	 * 現在のスケールに従い、指定された数値を Double で表現可能な値に変換する。
	 * @param value	変換する値
	 * @return	変換後の値、無効な値の場合は <code>Double.NaN</code>
	 */
	public double convertXDecimalToDouble(BigDecimal value) {
		if (value == null) {
			return Double.NaN;
		}
		else if (_entireXScaleToDouble != null) {
			return value.multiply(_entireXScaleToDouble).doubleValue();
		}
		else {
			return value.doubleValue();
		}
	}
	
	public double convertYDecimalToDouble(BigDecimal value) {
		if (value == null) {
			return Double.NaN;
		}
		else if (_entireYScaleToDouble != null) {
			return value.multiply(_entireYScaleToDouble).doubleValue();
		}
		else {
			return value.doubleValue();
		}
	}

	//------------------------------------------------------------
	// Interfaces for drawing
	//------------------------------------------------------------

	/**
	 * 指定されたインデックスに対応する系列が、データ点を結ぶ直線を描画する場合に <tt>true</tt> を返す。
	 * @param seriesIndex	データ系列のインデックス
	 * @return	直線を描画する場合は <tt>true</tt>、それ以外の場合は <tt>false</tt> を返す。
	 * @throws IndexOutOfBoundsException	インデックスが範囲外の場合
	 */
	public boolean isLineConnected(int seriesIndex) {
		// データ系列ごとに設定を変更しないので、全体の設定から取得する
		return _styles.isLineConnected();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void validDataTableEquals(final PlotDataSeries series) {
		if (!_datatable.equals(series.getXDataTable()))
			throw new IllegalArgumentException("Data table for X axis is not equals data table of this model.");
		if (!_datatable.equals(series.getYDataTable()))
			throw new IllegalArgumentException("Data table for Y axis is not equals data table of this model.");
	}

	/**
	 * 標準のチャートのスタイルを生成する。
	 * @return	<code>PlotStyles</code> オブジェクト
	 */
	protected PlotStyles createDefaultStyles() {
		return new PlotStyles();
	}
	
	protected void clearDataConstraints() {
		_dataConstraintsUpdated = false;
		_entireXMinDecimal = null;
		_entireYMinDecimal = null;
		_entireXMaxDecimal = null;
		_entireYMaxDecimal = null;
		_entireXScaleToDouble = null;
		_entireYScaleToDouble = null;
	}
	
	protected void refreshXEntireDecimalRange(BigDecimal value) {
		if (value != null) {
			if (_entireXMinDecimal == null)
				_entireXMinDecimal = value;
			else if (value.compareTo(_entireXMinDecimal) < 0)
				_entireXMinDecimal = value;
			
			if (_entireXMaxDecimal == null)
				_entireXMaxDecimal = value;
			else if (value.compareTo(_entireXMaxDecimal) > 0)
				_entireXMaxDecimal = value;
		}
	}
	
	protected void refreshYEntireDecimalRange(BigDecimal value) {
		if (value != null) {
			if (_entireYMinDecimal == null)
				_entireYMinDecimal = value;
			else if (value.compareTo(_entireYMinDecimal) < 0)
				_entireYMinDecimal = value;
			
			if (_entireYMaxDecimal == null)
				_entireYMaxDecimal = value;
			else if (value.compareTo(_entireYMaxDecimal) > 0)
				_entireYMaxDecimal = value;
		}
	}
	
	protected void refreshEntireScaleToDouble() {
		_entireXScaleToDouble = calcEntireScaleToDouble(_entireXMinDecimal, _entireXMaxDecimal);
		_entireYScaleToDouble = calcEntireScaleToDouble(_entireYMinDecimal, _entireYMaxDecimal);
	}

	/**
	 * Double の値範囲に収めるスケールを計算する。
	 * @param dMin	範囲の最小値、もしくは <tt>null</tt>
	 * @param dMax	範囲の最大値、もしくは <tt>null</tt>
	 * @return	スケール値、スケールの必要がない場合は <tt>null</tt>
	 */
	protected BigDecimal calcEntireScaleToDouble(BigDecimal dMin, BigDecimal dMax) {
		BigDecimal dAbsMax = null;
		
		if (dMin != null) {
			dMin = dMin.abs();
			if (dMin.compareTo(PlotConstants.DOUBLE_MAX_VALUE) > 0) {
				dAbsMax = dMin;
			}
		}
		
		if (dMax != null) {
			dMax = dMax.abs();
			if (dMax.compareTo(PlotConstants.DOUBLE_MAX_VALUE) > 0) {
				if (dAbsMax != null && dMax.compareTo(dAbsMax) > 0) {
					dAbsMax = dMax;
				}
			}
		}
		
		if (dAbsMax != null) {
			dAbsMax = PlotConstants.DOUBLE_MAX_VALUE.divide(dAbsMax, MathContext.DECIMAL64);
		}
		
		return dAbsMax;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
