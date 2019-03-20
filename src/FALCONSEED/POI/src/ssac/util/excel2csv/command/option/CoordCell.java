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
 * @(#)CoordCell.java	3.3.0	2016/04/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.excel2csv.command.option;


/**
 * 変換定義における、セル位置を示す整数値。
 * 第1列または第1行を 0 とする値として保持する。
 * このオブジェクトは、不変オブジェクトとする。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public final class CoordCell implements CoordValue
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 行の絶対位置もしくは相対位置 **/
	private final CoordInteger	_row;
	/** 列の絶対位置もしくは相対位置 **/
	private final CoordInteger	_col;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定されたパラメータを相対位置として、新しいインスタンスを生成する。
	 * @param row		行の相対位置
	 * @param column	列の相対位置
	 */
	public CoordCell(int row, int column) {
		this(false, row, false, column);
	}

	/**
	 * 指定されたパラメータで、新しいインスタンスを生成する。
	 * @param absRow	行位置を絶対座標値とする場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @param row		行位置
	 * @param absColumn	列位置を絶対座標値とする場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @param column	列位置
	 */
	public CoordCell(boolean absRow, int row, boolean absColumn, int column) {
		_row = new CoordInteger(absRow, row);
		_col = new CoordInteger(absColumn, column);
	}

	/**
	 * 指定されたパラメータで、新しいインスタンスを生成する。
	 * @param row		行位置
	 * @param column	列位置
	 * @throws NullPointerException	引数のどちらかが <tt>null</tt> の場合
	 */
	public CoordCell(CoordInteger row, CoordInteger column) {
		if (row == null || column == null)
			throw new NullPointerException();
		_row = row;
		_col = column;
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定された文字列を、セル位置オブジェクトに変換する。
	 * 文字列は、'[' 行位置 ',' 列位置 ']' の形式であり、
	 * 行位置ならびに列位置は絶対位置もしくは相対位置を示す値であることを前提とする。
	 * なお、絶対位置を示す値が 0 以下の場合は、例外をスローする。
	 * @param input	変換する文字列
	 * @return	変換後の値
	 * @throws NumberFormatException	変換できない場合
	 */
	static public CoordCell parse(String input) throws NumberFormatException
	{
		if (input == null)
			throw new NumberFormatException("null");
		if (input.isEmpty())
			throw new NumberFormatException("empty");
		int len = input.length();
		
		// check term characters
		if (input.charAt(0) != '[' || input.charAt(len-1) != ']')
			throw new NumberFormatException(CoordInteger.formatErrorString(input));
		int dindex = input.indexOf(',');
		if (dindex < 0)
			throw new NumberFormatException(CoordInteger.formatErrorString(input));
		
		// conversion of row
		String part = input.substring(1, dindex).trim();
		CoordInteger row;
		try {
			row = CoordInteger.parseRowNumber(part);
		} catch (NumberFormatException ex) {
			throw new NumberFormatException(CoordInteger.formatErrorString(input));
		}
		
		// conversion of column
		part = input.substring(dindex+1, len-1).trim();
		CoordInteger col;
		try {
			col = CoordInteger.parseColumnNumber(part);
		} catch (NumberFormatException ex) {
			throw new NumberFormatException(CoordInteger.formatErrorString(input));
		}
		
		// completed
		return new CoordCell(row, col);
	}
	
	public CoordInteger row() {
		return _row;
	}
	
	public CoordInteger column() {
		return _col;
	}
	
	@Override
	public int hashCode() {
		int h = _row.hashCode();
		h = 31 * h + _col.hashCode();
		return h;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CoordCell) {
			CoordCell aval = (CoordCell)obj;
			if (aval._row.equals(this._row) && aval._col.equals(this._col)) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		sb.append(_row.toRowString());
		sb.append(',');
		sb.append(_col.toColumnString());
		sb.append(']');
		return sb.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
