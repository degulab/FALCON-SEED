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
 * @(#)PlotStringField.java	2.1.0	2013/07/22
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.plot;

import java.math.BigDecimal;

/**
 * 文字列型のデータ列に関する情報を保持するクラス。
 * 
 * @version 2.1.0	2013/07/22
 * @since 2.1.0
 */
public class PlotStringField extends PlotDataField
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定されたパラメータで、新しいオブジェクトを生成する。
	 * フィールド名は、データテーブルから取得する。
	 * このメソッドでは、フィールドインデックスはチェックしない。
	 * @param targetTable	対象データテーブル
	 * @param fieldIndex	データテーブル内の対象列インデックス
	 * @throws NullPointerException	<em>targetTable</em> もしくは <em>FieldType</em> が <tt>null</tt> の場合
	 * @throws IndexOutOfBoundsException	<em>fieldIndex</em> が範囲外の場合
	 */
	public PlotStringField(IDataTable targetTable, int fieldIndex) {
		this(targetTable, fieldIndex, null);
	}

	/**
	 * 指定されたパラメータで、新しいオブジェクトを生成する。
	 * <em>fieldName</em> に <tt>null</tt> を指定した場合、データテーブルからフィールド名を取得する。
	 * このメソッドでは、フィールドインデックスはチェックしない。
	 * @param targetTable	対象データテーブル
	 * @param fieldIndex	データテーブル内の対象列インデックス
	 * @param fieldName		任意のフィールド名
	 * @throws NullPointerException	<em>targetTable</em> が <tt>null</tt> の場合
	 * @throws IndexOutOfBoundsException	<em>fieldIndex</em> が範囲外の場合
	 */
	public PlotStringField(IDataTable targetTable, int fieldIndex, String fieldName) {
		super(targetTable, fieldIndex, String.class, fieldName);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	/**
	 * データ型が数値の場合はその値、それ以外の場合はプロット位置を示す数値を返す。
	 * 無効値の場合は <tt>null</tt> を返す。
	 * <p>このクラスでは、データレコードインデックスを返す。
	 * @param index	データレコード先頭からのインデックス
	 * @return	プロットする数値、無効値の場合は <tt>null</tt>
	 * @throws IndexOutOfBoundsException	インデックスが範囲外の場合
	 */
	@Override
	public BigDecimal getDecimalValue(long index) {
		//return new BigDecimal(index);
		//--- 空白をチェックする場合は以下のコード
		Object value = getFieldValue(index);
		if (value != null && value.toString().length() > 0) {
			return new BigDecimal(index);
		} else if (isInvalidValueAsZero()) {
			return BigDecimal.ZERO;
		} else {
			return null;
		}
	}
	
	public String getStringValue(long index) {
		Object value = getFieldValue(index);
		return (value==null ? null : value.toString());
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
