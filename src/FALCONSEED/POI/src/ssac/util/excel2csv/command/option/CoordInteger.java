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
 *  Copyright 2007-2016  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)CoordInteger.java	3.3.0	2016/04/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.excel2csv.command.option;


/**
 * 変換定義における、行または列の位置を示す整数値。
 * 第1列または第1行を 0 とする値として保持する。
 * このオブジェクトは、不変オブジェクトとする。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public final class CoordInteger extends Number implements CoordValue
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = -8382793435850251028L;
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** このオブジェクトの値が絶対位置を示す場合は <tt>true</tt> **/
	private final boolean	_abs;
	/** 値 **/
	private final int		_value;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定された値を相対座標値として、新しいインスタンスを生成する。
	 * @param value	値
	 */
	public CoordInteger(int value) {
		this(false, value);
	}

	/**
	 * 指定されたパラメータで、新しいインスタンスを生成する。
	 * @param abs	絶対座標値とする場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @param value	値
	 */
	public CoordInteger(boolean abs, int value) {
		_abs   = abs;
		_value = value;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 行位置を示す文字列を、数値に変換する。
	 * 文字列の先頭が 'R' で続く文字列が数値であれば絶対座標値に変換される。
	 * それ以外の場合は相対座標値に変換される。
	 * なお、絶対位置を示す文字列のとき、数値が 0 以下の値の場合は例外がスローされる。
	 * @param input	変換する文字列
	 * @return	変換後の値
	 * @throws NumberFormatException	変換できない場合
	 */
	static public CoordInteger parseRowNumber(String input) throws NumberFormatException
	{
		if (input == null)
			throw new NumberFormatException("null");
		if (input.isEmpty())
			throw new NumberFormatException("empty");
		
		if (input.charAt(0) == 'R') {
			int val;
			try {
				val = Integer.parseInt(input.substring(1));
			}
			catch (NumberFormatException ex) {
				throw new NumberFormatException(formatErrorString(input));
			}
			if (val < 1)
				throw new NumberFormatException(formatErrorString(input));
			return new CoordInteger(true, val-1);
		}
		else {
			return new CoordInteger(false, Integer.parseInt(input));
		}
	}

	/**
	 * 列位置を示す文字列を、数値に変換する。
	 * 文字列の先頭が 'C' で続く文字列が数値であれば絶対座標値に変換される。
	 * それ以外の場合は相対座標値に変換される。
	 * なお、絶対位置を示す文字列のとき、数値が 0 以下の値の場合は例外がスローされる。
	 * @param input	変換する文字列
	 * @return	変換後の値
	 * @throws NumberFormatException	変換できない場合
	 */
	static public CoordInteger parseColumnNumber(String input) throws NumberFormatException
	{
		if (input == null)
			throw new NumberFormatException("null");
		if (input.isEmpty())
			throw new NumberFormatException("empty");
		
		if (input.charAt(0) == 'C') {
			int val;
			try {
				val = Integer.parseInt(input.substring(1));
			}
			catch (NumberFormatException ex) {
				throw new NumberFormatException(formatErrorString(input));
			}
			if (val < 1)
				throw new NumberFormatException(formatErrorString(input));
			return new CoordInteger(true, val-1);
		}
		else {
			return new CoordInteger(false, Integer.parseInt(input));
		}
	}

	/**
	 * この値が絶対座標を示す場合に <tt>true</tt> を返す。
	 */
	public boolean isAbsolute() {
		return _abs;
	}

	/**
	 * この値が相対座標を示す場合に <tt>true</tt> を返す。
	 */
	public boolean isRelative() {
		return !_abs;
	}

	@Override
	public int intValue() {
		return _value;
	}

	@Override
	public long longValue() {
		return (long)_value;
	}

	@Override
	public float floatValue() {
		return (float)_value;
	}

	@Override
	public double doubleValue() {
		return (double)_value;
	}

	@Override
	public int hashCode() {
		int h = _abs ? 1231 : 1237;
		h = 31 * h + _value;
		return h;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CoordInteger) {
			CoordInteger avalue = (CoordInteger)obj;
			if (avalue._abs==this._abs && avalue._value==this._value) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * 列位置の座標値を示す文字列表現を返す。
	 * 相対座標の場合は単なる数値の文字列表現とする。
	 * 絶対座標の場合は、先頭に 'C' 文字を付加した (数値 + 1) の文字列表現とする。
	 * @return	列位置を示す文字列表現
	 */
	public String toColumnString() {
		return (_abs ? "C".concat(String.valueOf(_value+1)) : String.valueOf(_value));
	}

	/**
	 * 行位置の座標値を示す文字列表現を返す。
	 * 相対座標の場合は単なる数値の文字列表現とする。
	 * 絶対座標の場合は、先頭に 'R' 文字を付加した (数値 + 1) の文字列表現とする。
	 * @return	行位置を示す文字列表現
	 */
	public String toRowString() {
		return (_abs ? "R".concat(String.valueOf(_value+1)) : String.valueOf(_value));
	}

	/**
	 * このオブジェクトの文字列表現を返す。
	 * 相対座標の場合は単なる数値の文字列表現とする。
	 * 絶対座標の場合は、先頭に '@’ 文字を付加した数値の文字列表現とする。
	 * @return	このオブジェクトの文字列表現。
	 */
	public String toString() {
		return (_abs ? "@".concat(String.valueOf(_value)) : String.valueOf(_value));
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static String formatErrorString(String input) {
		return "For input string: \"" + input + "\"";
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
