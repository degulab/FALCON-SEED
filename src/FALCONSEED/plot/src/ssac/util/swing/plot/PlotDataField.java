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
 * @(#)PlotDataField.java	2.1.0	2013/07/22
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.plot;

import java.math.BigDecimal;

/**
 * プロット対象のデータ列に関する情報を保持するクラス。
 * 
 * @version 2.1.0	2013/07/22
 * @since 2.1.0
 */
public abstract class PlotDataField
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** このフィールドが属するデータテーブル **/
	protected final IDataTable	_datatable;
	/** フィールドのインデックス **/
	protected final int			_fieldIndex;
	/** フィールドのデータ型 **/
	private final Class<?>		_fieldType;
	/** フィールド名 **/
	protected final String		_fieldName;

	/** 空白または無効値を 0 にするフラグ **/
	protected boolean		_invalidValueAsZero;
	/** 有効数値範囲の更新済フラグ **/
	protected boolean		_decimalRangeUpdated;
	/** 無効値を除く最小値 **/
	protected BigDecimal	_minDecimal;
	/** 無効値を除く最大値 **/
	protected BigDecimal	_maxDecimal;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定されたパラメータで、新しいオブジェクトを生成する。
	 * フィールド名は、データテーブルから取得する。
	 * このメソッドでは、フィールドインデックスはチェックしない。
	 * @param targetTable	対象データテーブル
	 * @param fieldIndex	データテーブル内の対象列インデックス
	 * @param fieldType		フィールドのデータ型
	 * @throws NullPointerException	<em>targetTable</em> もしくは <em>FieldType</em> が <tt>null</tt> の場合
	 * @throws IndexOutOfBoundsException	<em>fieldIndex</em> が範囲外の場合
	 */
	public PlotDataField(IDataTable targetTable, int fieldIndex, Class<?> fieldType) {
		this(targetTable, fieldIndex, fieldType, null);
	}

	/**
	 * 指定されたパラメータで、新しいオブジェクトを生成する。
	 * <em>fieldName</em> に <tt>null</tt> を指定した場合、データテーブルからフィールド名を取得する。
	 * このメソッドでは、フィールドインデックスはチェックしない。
	 * @param targetTable	対象データテーブル
	 * @param fieldIndex	データテーブル内の対象列インデックス
	 * @param fieldType		フィールドのデータ型
	 * @param fieldName		任意のフィールド名
	 * @throws NullPointerException	<em>targetTable</em> もしくは <em>FieldType</em> が <tt>null</tt> の場合
	 * @throws IndexOutOfBoundsException	<em>fieldIndex</em> が範囲外の場合
	 */
	public PlotDataField(IDataTable targetTable, int fieldIndex, Class<?> fieldType, String fieldName) {
		if (targetTable == null)
			throw new NullPointerException("Target data table is null.");
		if (fieldType == null)
			throw new NullPointerException("Target field's data type is null.");
		_datatable = targetTable;
		_fieldIndex = fieldIndex;
		_fieldType = fieldType;
		_fieldName = (fieldName==null ? targetTable.getFieldName(fieldIndex) : fieldName);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * この列が属するデータテーブルを返す。
	 */
	public IDataTable getDataTable() {
		return _datatable;
	}
	
	/**
	 * この列のテーブル上のインデックスを返す。
	 */
	public int getFieldIndex() {
		return _fieldIndex;
	}
	
	/**
	 * 対象データのフィールド名(列名)を返す。
	 */
	public String getFieldName() {
		return _fieldName;
	}
	
	/**
	 * このデータの総レコード数を返す。
	 * 総レコード数は、すべてのデータレコードとヘッダレコードの総数となる。
	 */
	public long getRecordCount() {
		return _datatable.getRecordCount();
	}
	
	/**
	 * データ数(データレコード数)を返す。
	 */
	public long getDataCount() {
		return _datatable.getDataRecordCount();
	}
	
	/**
	 * 指定されたインデックスの実際の値を取得する。
	 * @param index	データレコード先頭からのインデックス
	 * @return	実際の値
	 * @throws IndexOutOfBoundsException	インデックスが範囲外の場合
	 */
	public Object getFieldValue(long index) {
		return _datatable.getValue(index, _fieldIndex);
	}
	
	/**
	 * 対象データのデータ型を返す。
	 */
	public Class<?> getFieldType() {
		return _fieldType;
	}

//	/**
//	 * このオブジェクトに、新しいデータ型を設定する。
//	 * @param fieldType	データ列のデータ型
//	 * @return 設定が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
//	 */
//	public boolean setFieldType(Class<?> fieldType) {
//		if (fieldType == null) {
//			fieldType = String.class;
//		}
//		
//		if (fieldType != _fieldType) {
//			resetDecimalRange();
//			_fieldType = fieldType;
//			return true;
//		}
//		
//		return false;
//	}
	
	/**
	 * データ型が数値の場合はその値、それ以外の場合はプロット位置を示す数値を返す。
	 * 無効値の場合は <tt>null</tt> を返す。
	 * @param index	データレコード先頭からのインデックス
	 * @return	プロットする数値、無効値の場合は <tt>null</tt>
	 * @throws IndexOutOfBoundsException	インデックスが範囲外の場合
	 */
	public abstract BigDecimal getDecimalValue(long index);
//	public BigDecimal getDecimalValue(long index) {
//		if (Number.class.isAssignableFrom(_fieldType)) {
//			// number
//			Object fvalue = _datatable.getValue(index, _fieldIndex);
//			BigDecimal retValue = null;
//			if (fvalue != null) {
//				try {
//					retValue = new BigDecimal(fvalue.toString());
//				} catch (Throwable ex) {
//					// invalid number
//					retValue = null;
//				}
//			}
//			return retValue;
//		}
//		else {
//			// not number
//			validDataIndex(index, _datatable.getDataRecordCount());
//			return BigDecimal.valueOf(index);
//		}
//	}

	/**
	 * このオブジェクトの有効数値範囲の最小値を返す。
	 * 最小値が存在しない場合は <tt>null</tt> を返す。
	 */
	public BigDecimal getMinimumDecimalValue() {
		return _minDecimal;
	}

	/**
	 * このオブジェクトの有効数値範囲の最大値を返す。
	 * 最大値が存在しない場合は <tt>null</tt> を返す。
	 */
	public BigDecimal getMaximumDecimalValue() {
		return _maxDecimal;
	}

	/**
	 * このオブジェクトの有効数値範囲をリセットする。
	 */
	public void resetDecimalRange() {
		_decimalRangeUpdated = false;
		_minDecimal = null;
		_maxDecimal = null;
	}

	/**
	 * 空白または無効値を 0 にする場合は <tt>true</tt> を返す。
	 */
	public boolean isInvalidValueAsZero() {
		return _invalidValueAsZero;
	}

	/**
	 * 空白または無効値を 0 にするかどうかを設定する。
	 * @param asZero	空白または無効値を 0 にする場合は <tt>true</tt>
	 * @return 設定が変更された場合は <tt>true</tt>
	 */
	public boolean setInvalidValueAsZero(boolean asZero) {
		if (asZero != _invalidValueAsZero) {
			_invalidValueAsZero = asZero;
			resetDecimalRange();
		}
		return false;
	}

	/**
	 * このオブジェクトの有効数値範囲が更新済みなら <tt>true</tt> を返す。
	 */
	public boolean isDecimalRangeUpdated() {
		return _decimalRangeUpdated;
	}

	/**
	 * このオブジェクトの有効数値範囲更新済みフラグを、更新完了に設定する。
	 * 外部から {@link #refreshDecimalRange(long)} を使用して
	 * レコード単位で数値範囲を更新する場合に、このメソッドで状態を制御する。
	 */
	public void decimalRangeUpdateFinished() {
		_decimalRangeUpdated = true;
	}

	/**
	 * このオブジェクトの有効数値範囲を、指定されたレコードの値で更新する。
	 * @param index	データレコードのインデックス
	 * @throws IndexOutOfBoundsException	インデックスが範囲外の場合
	 */
	public void refreshDecimalRange(long index) {
		BigDecimal value = getDecimalValue(index);
		if (value != null) {
			// min
			if (_minDecimal == null)
				_minDecimal = value;
			else if (value.compareTo(_minDecimal) < 0)
				_minDecimal = value;
			
			// max
			if (_maxDecimal == null)
				_maxDecimal = value;
			else if (value.compareTo(_maxDecimal) > 0)
				_maxDecimal = value;
		}
	}

	/**
	 * このオブジェクトの有効範囲が更新されていない場合のみ、
	 * すべての数値から範囲を更新する。
	 */
	public void updateDecimalRange() {
		if (_decimalRangeUpdated)
			return;
		
		long numRecords = getDataCount();
		for (long index=0L; index < numRecords; ++index) {
			refreshDecimalRange(index);
		}
		
		_decimalRangeUpdated = true;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static protected void validDataIndex(long index, long numDataRecords) {
		if (index < 0L || index >= numDataRecords) {
			throw new IndexOutOfBoundsException("Data record index out of range (" + numDataRecords + ") : " + index);
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
