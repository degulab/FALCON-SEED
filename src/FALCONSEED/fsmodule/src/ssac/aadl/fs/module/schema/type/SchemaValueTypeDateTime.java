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
 *  Copyright 2007-2015  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)SchemaValueTypeDateTime.java	3.2.1	2015/07/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SchemaValueTypeDateTime.java	3.2.0	2015/06/22
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module.schema.type;

import java.util.Calendar;

/**
 * 汎用フィルタ定義における、日付時刻データ型を示すクラス。
 * 日付時刻のフォーマットも保持する。
 * このオブジェクトは、不変オブジェクトである。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public class SchemaValueTypeDateTime extends SchemaValueType
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final String TYPENAME_DATETIME	= "DateTime";

	/** このオブジェクト標準的なインスタンス **/
	static public final SchemaValueTypeDateTime instance = new SchemaValueTypeDateTime();

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 日付時刻値のフォーマット **/
	protected SchemaDateTimeFormats	_format;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 標準的なフォーマットの日付時刻データ型を示すオブジェクトを生成する。
	 */
	public SchemaValueTypeDateTime() {
		this(new SchemaDateTimeFormats(null));
	}

	/**
	 * 指定されたパターンの日付時刻データ型を示すオブジェクトを生成する。
	 * パターンは、日付時刻書式の文字列をカンマを区切り文字として解析する。
	 * ダブルクオートで囲まれた範囲内のカンマは通常文字として認識し、 区切り文字として認識しない。
	 * 日付時刻書式は、{@link java.text.SimpleDateFormat} の日付/時刻パターンによって記述する。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * ダブルクオートは日付時刻書式文字列のパターンに使用することは
	 * できないので、ダブルクオートをエスケープすることはできない。
	 * </blockquote>
	 * <p>指定された文字列が <tt>null</tt> もしくは空文字列の場合、
	 * 標準のパターンを持つ、新規の日付時刻書式定義を生成する。
	 * また、日付/時刻パターンに &quot;default&quot; と記述した場合、標準の
	 * 日付/時刻パターンを使用する。
	 * @param datetimeFormat	日付時刻書式パターンを示す文字列
	 * @throws IllegalArgumentException	指定された文字列に含まれる日付時刻書式が 正しくない場合
	 */
	public SchemaValueTypeDateTime(String datetimeFormat) {
		this(new SchemaDateTimeFormats(datetimeFormat));
	}

	/**
	 * 指定された日付時刻書式オブジェクトを持つ、日付時刻データ型を示すオブジェクトを生成する。
	 * @param dtformats	日付時刻書式オブジェクト
	 * @throws NullPointerException	<em>dtformats</em> が <tt>null</tt> の場合
	 * @since 3.2.1
	 */
	public SchemaValueTypeDateTime(SchemaDateTimeFormats dtformats) {
		super(TYPENAME_DATETIME);
		if (dtformats == null)
			throw new NullPointerException();
		_format = dtformats;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このデータ型に相当する JAVA クラスを取得する。
	 * @return	<code>Calendar</code> クラスオブジェクト
	 */
	@Override
	public Class<Calendar> getJavaClass() {
		return Calendar.class;
	}

	/**
	 * 指定された文字列から、このデータ型に応じた値に変換する。
	 * 文字列が <tt>null</tt> もしくは空文字列の場合、このメソッドは <tt>null</tt> を返す。
	 * @param value	値を示す文字列
	 * @return	文字列から変換された値のオブジェクト
	 * @throws SchemaValueFormatException	変換に失敗した場合
	 */
	@Override
	public Calendar convertFromString(String value) throws SchemaValueFormatException
	{
		if (value != null && !value.isEmpty()) {
			Calendar cal = _format.parse(value);
			if (cal != null) {
				return cal;
			} else {
				throw new SchemaValueFormatException(this, value);
			}
		} else {
			return null;
		}
	}

	/**
	 * 指定されたオブジェクトを、このデータ型に応じた文字列形式に変換する。
	 * このデータ型に応じたオブジェクト型ではない場合、{@link java.lang.Object#toString()} メソッドによって文字列形式に変換する。
	 * <em>value</em> が <tt>null</tt> の場合、このメソッドは <tt>null</tt> を返す。
	 * @param value	文字列へ変換する値	
	 * @return	変換された文字列、<em>value</em> が <tt>null</tt> の場合は <tt>null</tt>
	 * @since 3.2.1
	 */
	@Override
	public String convertToString(Object value) {
		if (value instanceof Calendar) {
			return _format.format((Calendar)value);
		} else {
			return super.convertToString(value);
		}
	}

	/**
	 * 補助パラメータの保持が許可されているデータ型かを返す。
	 * @return	このオブジェクトは常に <tt>true</tt> を返す
	 */
	@Override
	public boolean getAllowSubParameter() {
		return true;
	}

	/**
	 * 補助パラメータが保持されているかを判定する。
	 * @return	このオブジェクトは常に <tt>true</tt> を返す
	 */
	@Override
	public boolean hasSubParameter() {
		return true;
	}

	/**
	 * 保持されている補助パラメータを取得する。
	 * @return	<code>FSDateTimeFormats</code> オブジェクト
	 */
	@Override
	public SchemaDateTimeFormats getSubParameter() {
		return _format;
	}

	/**
	 * このオブジェクトの正確なハッシュ値を返す。
	 * @return	正確な状態を示すハッシュ値
	 */
	@Override
	public int exactlyHashCode() {
		int h = super.hashCode();
		h = 31 * h + _format.hashCode();
		return h;
	}

	/**
	 * 指定されたオブジェクトとこのオブジェクトが正確に等しいかを判定する。
	 * @param obj	判定対象のオブジェクト
	 * @return	正確に等しい場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	@Override
	public boolean exactlyEquals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (obj != null && obj.getClass().equals(this.getClass())) {
			SchemaValueTypeDateTime aType = (SchemaValueTypeDateTime)obj;
			if (getTypeName().equals(aType.getTypeName()) && _format.equals(aType._format)) {
				return true;
			}
		}
		
		// not equals
		return false;
	}

	/**
	 * このオブジェクトの正確な状態を示す文字列表現を返す。
	 * 基本的に、オブジェクトを復元可能な文字列表現とする。
	 * @return	このオブジェクトの正確な文字列表現
	 */
	@Override
	public String toExactlyString() {
		return String.format("%s(%s)", getTypeName(), _format.toString());
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
