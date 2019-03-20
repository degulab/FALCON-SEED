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
 * @(#)DefaultDataSeries.java	2.1.0	2013/07/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.plot;

/**
 * データ系列の 2 フィールド情報を設定可能な共通実装。
 * 
 * @version 2.1.0	2013/07/20
 * @since 2.1.0
 */
public class DefaultDataSeries extends AbDataSeries implements IMutableDataSeries
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** データ系列名 **/
	private String	_legend;

	/** データ系列の X 値とするデータ列 **/
	private IDataField	_xField;
	/** データ系列の Y 値とするデータ列 **/
	private IDataField	_yField;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public DefaultDataSeries() {
		_legend = null;
		_xField = null;
		_yField = null;
	}
	
	public DefaultDataSeries(IDataField xField, IDataField yField) {
		_legend = null;
		_xField = xField;
		_yField = yField;
	}
	
	public DefaultDataSeries(String legend, IDataField xField, IDataField yField) {
		_legend = legend;
		_xField = xField;
		_yField = yField;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * この系列の有効な系列名を返す。
	 * 系列名が定義されていない場合、自動生成された系列名を返す。
	 * @return	有効な系列名
	 */
	public String getAvailableLegend() {
		return (_legend==null ? getDefaultLegend() : _legend);
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
			_legend = legend;
			return true;
		}
		return false;
	}

	/**
	 * 系列の X 値とするデータ列が設定されていれば <tt>true</tt> を返す。
	 */
	public boolean hasXField() {
		return (_xField != null);
	}
	
	/**
	 * 系列の Y 値とするデータ列が設定されていれば <tt>true</tt> を返す。
	 */
	public boolean hasYField() {
		return (_yField != null);
	}

	/**
	 * 系列の X 値とするデータ列を返す。
	 * @return	設定されているデータ列情報、設定されていない場合は <tt>null</tt>
	 */
	public IDataField getXField() {
		return _xField;
	}

	/**
	 * 系列の Y 値とするデータ列を返す。
	 * @return	設定されているデータ列情報、設定されていない場合は <tt>null</tt>
	 */
	public IDataField getYField() {
		return _yField;
	}

	/**
	 * 系列の X 値とするデータ列を設定する。
	 * @param field	データ列の情報
	 * @return 設定が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean setXField(IDataField field) {
		if ((field==null && _xField!=null) || (field!=null && !field.equals(_xField))) {
			_xField = field;
			return true;
		}
		return false;
	}

	/**
	 * 系列の Y 値とするデータ列を設定する。
	 * @param field	データ列の情報
	 * @return 設定が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean setYField(IDataField field) {
		if ((field==null && _yField!=null) || (field!=null && !field.equals(_yField))) {
			_yField = field;
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

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
