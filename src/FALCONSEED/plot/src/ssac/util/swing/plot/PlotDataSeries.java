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
 * @(#)PlotDataSeries.java	2.1.0	2013/07/18
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.plot;

import java.math.BigDecimal;

/**
 * データ系列の情報を保持するクラス。
 * 
 * @version 2.1.0	2013/07/18
 * @since 2.1.0
 */
public class PlotDataSeries
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 変更フラグ **/
	protected boolean	_modified;

	/** 自動生成されたデータ系列名 **/
	private String	_defLegend;
	/** データ系列名 **/
	private String	_legend;

	/** データ系列の X 値とするデータ列 **/
	private PlotDataField	_xField;
	/** データ系列の Y 値とするデータ列 **/
	private PlotDataField	_yField;
	/** X 軸の値数と Y 軸の値数のより大きい方 **/
	private long	_numValues;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public PlotDataSeries() {
		updateStatus();
		_modified = true;
	}
	
	public PlotDataSeries(PlotDataField xField, PlotDataField yField) {
		_xField = xField;
		_yField = yField;
		updateStatus();
		_modified = true;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 対象データの総レコード数を返す。
	 * この総レコード数は、ヘッダレコード数とデータレコード数の合計となる。
	 */
	public long getRecordCount() {
		return Math.max((_xField==null ? 0L : _xField.getRecordCount()), (_yField==null ? 0L : _yField.getRecordCount()));
	}

	/**
	 * この系列のデータ数を返す。
	 * このデータ数は、X 値列のデータ数と Y 値列のデータ数のうち、大きい方を返す。
	 * @return	この系列のデータ数
	 */
	public long getValueCount() {
		return _numValues;
	}

	/**
	 * この設定内容をクリアする。
	 * この操作では、変更フラグもリセットされる。
	 */
	public void clear() {
		clearStatus();
		_modified = false;
		_xField = null;
		_yField = null;
	}

	/**
	 * この設定内容が変更されている場合は <tt>true</tt> を返す。
	 */
	public boolean isModified() {
		return _modified;
	}

	/**
	 * この設定内容の変更フラグを指定する。
	 * @param toModified	変更状態とする場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public void setModifiedFlag(boolean toModified) {
		_modified = toModified;
	}

	/**
	 * この系列の有効な系列名を返す。
	 * 系列名が定義されていない場合、自動生成された系列名を返す。
	 * @return	有効な系列名
	 */
	public String getAvailableLegend() {
		return (_legend==null ? _defLegend : _legend);
	}

	/**
	 * 自動生成された系列名を返す。
	 * @return	自動生成された系列名
	 */
	public String getDefaultLegend() {
		return _defLegend;
	}

	/**
	 * 任意に指定された系列名を返す。設定されていない場合は <tt>null</tt> を返す。
	 * @return	設定されている系列名、設定されていない場合は <tt>null</tt>
	 */
	public String getLegend() {
		return _legend;
	}

	/**
	 * 任意の系列名を設定する。設定を解除する場合は <tt>null</tt> を指定する。
	 * @param legend	任意の系列名
	 * @return 設定が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean setLegend(String legend) {
		if ((legend==null && _legend!=null) || (legend!=null && !legend.equals(_legend))) {
			_modified = true;
			_legend = legend;
			return true;
		}
		return false;
	}

	/**
	 * 系列の X 値とするフィールドのデータテーブルを返す。
	 * X 値のフィールドが設定されていない場合は <tt>null</tt> を返す。
	 */
	public IDataTable getXDataTable() {
		return (_xField==null ? null : _xField.getDataTable());
	}

	/**
	 * 系列の Y 値とするフィールドのデータテーブルを返す。
	 * Y 値のフィールドが設定されていない場合は <tt>null</tt> を返す。
	 */
	public IDataTable getYDataTable() {
		return (_yField==null ? null : _yField.getDataTable());
	}

	/**
	 * 系列の X 値とするデータ列を返す。
	 * @return	設定されているデータ列情報、設定されていない場合は <tt>null</tt>
	 */
	public PlotDataField getXField() {
		return _xField;
	}

	/**
	 * 系列の Y 値とするデータ列を返す。
	 * @return	設定されているデータ列情報、設定されていない場合は <tt>null</tt>
	 */
	public PlotDataField getYField() {
		return _yField;
	}

	/**
	 * 系列の X 値とするデータ列を設定する。
	 * @param field	データ列の情報
	 * @return 設定が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean setXField(PlotDataField field) {
		if ((field==null && _xField!=null) || (field!=null && !field.equals(_xField))) {
			_modified = true;
			_xField = field;
			updateStatus();
			return true;
		}
		return false;
	}

	/**
	 * 系列の Y 値とするデータ列を設定する。
	 * @param field	データ列の情報
	 * @return 設定が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean setYField(PlotDataField field) {
		if ((field==null && _yField!=null) || (field!=null && !field.equals(_yField))) {
			_modified = true;
			_yField = field;
			updateStatus();
			return true;
		}
		return false;
	}
	
	/**
	 * X 値のデータ列から、指定されたインデックスの実際の値を取得する。
	 * @param index	データレコード先頭からのインデックス
	 * @return	実際の値
	 * @throws NullPointerException		X 値とするデータ列が設定されていない場合
	 * @throws IndexOutOfBoundsException	インデックスが範囲外の場合
	 */
	public Object getXFieldValue(long index) {
		return _xField.getFieldValue(index);
	}
	
	/**
	 * Y 値のデータ列から、指定されたインデックスの実際の値を取得する。
	 * @param index	データレコード先頭からのインデックス
	 * @return	実際の値
	 * @throws NullPointerException		Y 値とするデータ列が設定されていない場合
	 * @throws IndexOutOfBoundsException	インデックスが範囲外の場合
	 */
	public Object getYFieldValue(long index) {
		return _yField.getFieldValue(index);
	}
	
	/**
	 * X 値のデータ列から、データ型が数値の場合はその値、それ以外の場合はプロット位置を示す数値を返す。
	 * 無効値の場合は <tt>null</tt> を返す。
	 * @param index	データレコード先頭からのインデックス
	 * @return	プロットする数値、無効値の場合は <tt>null</tt>
	 * @throws NullPointerException		X 値とするデータ列が設定されていない場合
	 * @throws IndexOutOfBoundsException	インデックスが範囲外の場合
	 */
	public BigDecimal getXDecimalValue(long index) {
		return _xField.getDecimalValue(index);
	}
	
	/**
	 * Y 値のデータ列から、データ型が数値の場合はその値、それ以外の場合はプロット位置を示す数値を返す。
	 * 無効値の場合は <tt>null</tt> を返す。
	 * @param index	データレコード先頭からのインデックス
	 * @return	プロットする数値、無効値の場合は <tt>null</tt>
	 * @throws NullPointerException		Y 値とするデータ列が設定されていない場合
	 * @throws IndexOutOfBoundsException	インデックスが範囲外の場合
	 */
	public BigDecimal getYDecimalValue(long index) {
		return _yField.getDecimalValue(index);
	}

	/**
	 * X 値の有効数値範囲の最小値を返す。
	 * 最小値が存在しない場合は <tt>null</tt> を返す。
	 */
	public BigDecimal getMinimumXDecimalValue() {
		return (_xField==null ? null : _xField.getMinimumDecimalValue());
	}

	/**
	 * X 値の有効数値範囲の最大値を返す。
	 * 最大値が存在しない場合は <tt>null</tt> を返す。
	 */
	public BigDecimal getMaximumXDecimalValue() {
		return (_xField==null ? null : _xField.getMaximumDecimalValue());
	}
	
	/**
	 * Y 値の有効数値範囲の最小値を返す。
	 * 最小値が存在しない場合は <tt>null</tt> を返す。
	 */
	public BigDecimal getMinimumYDecimalValue() {
		return (_yField==null ? null : _yField.getMinimumDecimalValue());
	}
	
	/**
	 * Y 値の有効数値範囲の最大値を返す。
	 * 最大値が存在しない場合は <tt>null</tt> を返す。
	 */
	public BigDecimal getMaximumYDecimalValue() {
		return (_yField==null ? null : _yField.getMaximumDecimalValue());
	}

	/**
	 * このオブジェクトの有効数値範囲をリセットする。
	 */
	public void resetDecimalRanges() {
		if (_xField != null)
			_xField.resetDecimalRange();
		if (_yField != null)
			_yField.resetDecimalRange();
	}

	/**
	 * このオブジェクトの有効数値範囲が更新済みなら <tt>true</tt> を返す。
	 */
	public boolean isDecimalRangesUpdated() {
		if (_xField != null && !_xField.isDecimalRangeUpdated())
			return false;
		
		if (_yField != null && !_yField.isDecimalRangeUpdated())
			return false;
		
		return true;
	}

	/**
	 * このオブジェクトの有効数値範囲更新済みフラグを、更新完了に設定する。
	 * 外部から {@link #refreshDecimalRange(long)} を使用して
	 * レコード単位で数値範囲を更新する場合に、このメソッドで状態を制御する。
	 */
	public void decimalRangeUpdateFinished() {
		if (_xField != null)
			_xField.decimalRangeUpdateFinished();
		if (_yField != null)
			_yField.decimalRangeUpdateFinished();
	}

	/**
	 * 有効数値範囲が更新されていない場合のみ、
	 * 指定されたレコードの値で有効数値範囲を更新する。
	 * <p>このメソッドでは、有効数値範囲の更新完了フラグは変更されない。
	 * @param index	更新する値を取得するデータレコードのインデックス
	 * @throws IndexOutOfBoundsException	インデックスが範囲外の場合
	 */
	public void updateDecimalRangs(long index) {
		// X-Field
		if (_xField != null) {
			_xField.refreshDecimalRange(index);
		}
		
		// Y-Field
		if (_yField != null) {
			_yField.refreshDecimalRange(index);
		}
	}

	/**
	 * このオブジェクトの有効範囲が更新されていない場合のみ、
	 * すべての数値から範囲を更新する。
	 */
	public void updateDecimalRanges() {
		// X-Field
		if (_xField != null) {
			_xField.updateDecimalRange();
		}
		
		// Y-Field
		if (_yField != null) {
			_yField.updateDecimalRange();
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 内部ステータスをクリアする。
	 */
	protected void clearStatus() {
		_defLegend = "";
		_legend = null;
		_numValues = 0L;
	}

	/**
	 * 現在のデータ列に基づき、内部ステータスを更新する。
	 */
	protected void updateStatus() {
		if (_xField != null && _yField != null) {
			String xLegend = _xField.getFieldName();
			String yLegend = _yField.getFieldName();
			if (xLegend.length() > 0 && yLegend.length() > 0) {
				_defLegend = xLegend + "-" + yLegend;
			}
			else if (xLegend.length() > 0) {
				_defLegend = xLegend;
			}
			else if (yLegend.length() > 0) {
				_defLegend = yLegend;
			}
			else {
				_defLegend = "Unknown";
			}
			_numValues = Math.max(_xField.getDataCount(), _yField.getDataCount());
		}
		else if (_xField != null) {
			_defLegend = _xField.getFieldName() + "-none";
			_numValues = _xField.getDataCount();
		}
		else if (_yField != null) {
			_defLegend = "none-" + _yField.getFieldName();
			_numValues = _yField.getDataCount();
		}
		else {
			clearStatus();
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
