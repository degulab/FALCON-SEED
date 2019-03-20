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
 * @(#)OptionValueParser.java	3.3.0	2016/05/06
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.excel2csv.parser;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;

import ssac.util.excel2csv.EtcMessages;
import ssac.util.excel2csv.command.option.BlankCellTypes;
import ssac.util.excel2csv.command.option.CoordCell;
import ssac.util.excel2csv.command.option.CoordInteger;
import ssac.util.excel2csv.command.option.CoordValue;
import ssac.util.excel2csv.command.option.DateFormatTypes;
import ssac.util.excel2csv.command.option.OptionValue;
import ssac.util.excel2csv.command.option.OptionValueTypes;
import ssac.util.excel2csv.command.option.PrecisionTypes;
import ssac.util.excel2csv.command.option.RepeatTypes;
import ssac.util.excel2csv.command.option.ValueTypes;
import ssac.util.excel2csv.poi.CellPosition;
import ssac.util.excel2csv.poi.EtcPoiUtil;

/**
 * 変換定義におけるオプション値のパーサー。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public class OptionValueParser
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final String	OPTION_VALUETYPE		= "valuetype";
	static public final String	OPTION_BLANKCELL		= "blankcell";
	static public final String	OPTION_DATEFORMAT		= "dateformat";
	static public final String	OPTION_PRECISION		= "precision";
	static public final String	OPTION_SHEET			= "sheet";
	static public final String	OPTION_FIRST_ROW		= "first-row";
	static public final String	OPTION_LAST_ROW			= "last-row";
	static public final String	OPTION_FIRST_COLUMN		= "first-column";
	static public final String	OPTION_LAST_COLUMN		= "last-column";
	static public final String	OPTION_FIRST_CELL		= "first-cell";
	static public final String	OPTION_LAST_CELL		= "last-cell";
	static public final String	OPTION_ROW_COUNT		= "row-count";
	static public final String	OPTION_COLUMN_COUNT		= "column-count";
	static public final String	OPTION_COLUMN_HEADER_ROWS	= "column-header-rows";
	static public final String	OPTION_GROUP_COUNT		= "group-count";
	static public final String	OPTION_REPEAT			= "repeat";
	static public final String	OPTION_ROW_SPACING		= "row-spacing";
	static public final String	OPTION_COLUMN_SPACING	= "column-spacing";
	
	static protected final Map<String, OptionValueFormat>	_typemap;
	static {
		_typemap = new TreeMap<String         , OptionValueFormat>();
		_typemap.put(OPTION_VALUETYPE         , new OptionValueFormat(OPTION_VALUETYPE, ValueTypes.class));
		_typemap.put(OPTION_BLANKCELL         , new OptionValueFormat(OPTION_BLANKCELL, BlankCellTypes.class, OptionValueTypes.String));
		_typemap.put(OPTION_DATEFORMAT        , new OptionValueFormat(OPTION_DATEFORMAT, DateFormatTypes.class, OptionValueTypes.DateTimePattern));
		_typemap.put(OPTION_PRECISION         , new OptionValueFormat(OPTION_PRECISION, PrecisionTypes.class, OptionValueTypes.Integer));
		_typemap.put(OPTION_SHEET             , new OptionValueFormat(OPTION_SHEET, null, OptionValueTypes.String, OptionValueTypes.Pattern));
		_typemap.put(OPTION_FIRST_ROW         , new OptionValueFormat(OPTION_FIRST_ROW, null, OptionValueTypes.CellCoord, OptionValueTypes.RowCoord));
		_typemap.put(OPTION_LAST_ROW          , new OptionValueFormat(OPTION_LAST_ROW, null, OptionValueTypes.CellCoord, OptionValueTypes.RowCoord));
		_typemap.put(OPTION_FIRST_COLUMN      , new OptionValueFormat(OPTION_FIRST_COLUMN, null, OptionValueTypes.CellCoord, OptionValueTypes.ColumnCoord));
		_typemap.put(OPTION_LAST_COLUMN       , new OptionValueFormat(OPTION_LAST_COLUMN, null, OptionValueTypes.CellCoord, OptionValueTypes.ColumnCoord));
		_typemap.put(OPTION_FIRST_CELL        , new OptionValueFormat(OPTION_FIRST_CELL, null, OptionValueTypes.CellCoord));
		_typemap.put(OPTION_LAST_CELL         , new OptionValueFormat(OPTION_LAST_CELL, null, OptionValueTypes.CellCoord));
		_typemap.put(OPTION_ROW_COUNT         , new OptionValueFormat(OPTION_ROW_COUNT, null, OptionValueTypes.Integer));
		_typemap.put(OPTION_COLUMN_COUNT      , new OptionValueFormat(OPTION_COLUMN_COUNT, null, OptionValueTypes.Integer));
		_typemap.put(OPTION_COLUMN_HEADER_ROWS, new OptionValueFormat(OPTION_COLUMN_HEADER_ROWS, null, OptionValueTypes.MultiRowCoords));
		_typemap.put(OPTION_GROUP_COUNT       , new OptionValueFormat(OPTION_GROUP_COUNT, null, OptionValueTypes.Integer));
		_typemap.put(OPTION_REPEAT            , new OptionValueFormat(OPTION_REPEAT, RepeatTypes.class));
		_typemap.put(OPTION_ROW_SPACING       , new OptionValueFormat(OPTION_ROW_SPACING, null, OptionValueTypes.Integer));
		_typemap.put(OPTION_COLUMN_SPACING    , new OptionValueFormat(OPTION_COLUMN_SPACING, null, OptionValueTypes.Integer));
	}

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private OptionValueParser() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 文字列リテラルの形式を解析し、文字列に変換する。
	 * 前後の空白は除去されない。
	 * @param input	対象の文字列
	 * @return	変換後の文字列、変換できない場合は <tt>null</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public String parseStringLiteral(String input) {
		int len = input.length();
		StringBuilder sb = new StringBuilder(len);
		
		// 文字列リテラルの先頭クオート文字を取得
		if (len < 2)
			return null;
		char chQuote = input.charAt(0);
		if (chQuote != '\"') {
			return null;
		}
		
		// 文字列リテラル終端文字をチェック
		if ((len < 2) || (input.charAt(len-1) != chQuote)) {
			return null;
		}
		
		// 文字列リテラルのチェック
		for (int index = 1; index < len; ++index) {
			char ch = input.charAt(index);
			
			if (ch == '\\') {
				// エスケープシーケンス開始
				++index;
				if (index >= len) {
					// Invalid escape sequence
					return null;
				}
				ch = input.charAt(index);
				if (ch == 'b') {
					sb.append('\b');
				}
				else if (ch == 't') {
					sb.append('\t');
				}
				else if (ch == 'n') {
					sb.append('\n');
				}
				else if (ch == 'f') {
					sb.append('\f');
				}
				else if (ch == 'r') {
					sb.append('\r');
				}
				else if (ch == '\"') {
					sb.append('\"');
				}
				else if (ch == '\'') {
					sb.append('\'');
				}
				else if (ch == '\\') {
					sb.append('\\');
				}
				else if (ch >= '0' && ch <= '7') {
					// Octal
					int octalCount = 1;
					int ni = index + 1;
					if (ni < len && (input.charAt(ni) >= '0' && input.charAt(ni) <= '7')) {
						++octalCount;
						ni = index + 2;
						if (ni < len && (input.charAt(ni) >= '0' && input.charAt(ni) <= '7')) {
							++octalCount;
						}
					}
					String strOctal = input.substring(index, index+octalCount);
					char chOctal = (char)Integer.parseInt(strOctal, 8);
					sb.append(chOctal);
					index += (octalCount - 1);
				}
				else if (ch == 'u') {
					// Unicode
					//--- skip 'u'
					for (; index < len; ++index) {
						ch = input.charAt(index);
						if (ch != 'u') {
							break;
						}
					}
					//--- get hex digits
					int remain = len - index;
					if (remain < 4) {
						// Invalid unicode
						return null;
					}
					String strUnicode = input.substring(index, index+4);
					int unicode;
					try {
						unicode = Integer.parseInt(strUnicode, 16);
					} catch (Throwable ex) {
						unicode = -1;
					}
					if (unicode < 0) {
						// Invalid unicode
						return null;
					}
					sb.append((char)unicode);
					index += 3;
				}
				else {
					// Invalid escape sequence
					return null;
				}
			}
			else if (ch == chQuote) {
				// 文字列リテラル終端
				if (index != (len-1)) {
					// 終端クオート文字が途中にある場合は、エスケープシーケンスの記述忘れ
					return null;
				}
			}
			else {
				// 通常文字
				sb.append(ch);
			}
		}

		return sb.toString();
	}

	/**
	 * 正規表現リテラルの形式を解析し、<code>Pattern</code> オブジェクトに変換する。
	 * 前後の空白は除去されない。
	 * @param input	対象の文字列
	 * @return	<code>Pattern</code> オブジェクト、変換できない場合は <tt>null</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public Pattern parseRegexpLiteral(String input) {
		int len = input.length();
		StringBuilder sb = new StringBuilder(len);
		
		// 先頭文字をチェック
		if (len < 2)
			return null;
		char chTerm = input.charAt(0);
		if (chTerm != '/') {
			return null;
		}
		
		// 終端文字をチェック
		if (input.charAt(len) != chTerm) {
			return null;
		}
		
		// 正規表現リテラルのチェック
		for (int index = 1; index < len; ++index) {
			char ch = input.charAt(index);
			
			if (ch == '/') {
				// 終端文字のエスケープ開始
				++index;
				if (index >= len) {
					// 終端文字
					break;
				}
				ch = input.charAt(index);
				if (ch == '/') {
					sb.append('/');
				}
				else {
					// Invalid slash escaped
					return null;
				}
			}
			else {
				// 通常文字
				sb.append(ch);
			}
		}
		
		// make Pattern object
		try {
			return Pattern.compile(sb.toString(), Pattern.DOTALL);
		} catch (Throwable ex) {
			return null;
		}
	}
	
	/**
	 * 日付時刻フォーマットの形式を解析し、<code>SimpleDateFormat</code> オブジェクトに変換する。
	 * 前後の空白は除去されない。
	 * @param input	対象の文字列
	 * @return	<code>SimpleDateFormat</code> オブジェクト、変換できない場合は <tt>null</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public SimpleDateFormat parseDateTimeFormat(String input) {
		String strliteral = parseStringLiteral(input);
		if (strliteral != null) {
			try {
				return new SimpleDateFormat(strliteral);
			} catch (Throwable ex) {
				// invalid format
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * 指定された文字列を整数値(int)に変換する。
	 * 前後の空白は除去されない。
	 * @param input	対象の文字列
	 * @return	<code>Integer</code> オブジェクト、変換できない場合は <tt>null</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public Integer parseInteger(String input) {
		if (input == null)
			throw new NullPointerException();
		try {
			return new Integer(input);
		} catch (NumberFormatException ex) {
			return null;
		}
	}
	
	/**
	 * 指定された文字列を実数値(BigDecimal)に変換する。
	 * 前後の空白は除去されない。
	 * @param input	対象の文字列
	 * @return	<code>BigDecimal</code> オブジェクト、変換できない場合は <tt>null</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public BigDecimal parseDecimal(String input) {
		if (input == null)
			throw new NullPointerException();
		try {
			return new BigDecimal(input);
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	/**
	 * 指定された文字列を、セル位置の形式として解析する。
	 * 前後の空白は除去されない。
	 * @param input	対象の文字列
	 * @return	<code>CoordCell</code> オブジェクト、変換できない場合は <tt>null</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public CoordCell parseCellCoordinate(String input) {
		if (input == null)
			throw new NullPointerException();
		try {
			return CoordCell.parse(input);
		} catch (NumberFormatException ex) {
			return null;
		}
	}
	
	/**
	 * 指定された文字列を、行位置の形式として解析する。
	 * 前後の空白は除去されない。
	 * @param input	対象の文字列
	 * @return	<code>CoordCell</code> オブジェクト、変換できない場合は <tt>null</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public CoordInteger parseRowCoordinate(String input) {
		if (input == null)
			throw new NullPointerException();
		try {
			return CoordInteger.parseRowNumber(input);
		} catch (NumberFormatException ex) {
			return null;
		}
	}
	
	/**
	 * 指定された文字列を、列位置の形式として解析する。
	 * 前後の空白は除去されない。
	 * @param input	対象の文字列
	 * @return	<code>CoordCell</code> オブジェクト、変換できない場合は <tt>null</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public CoordInteger parseColumnCoordinate(String input) {
		if (input == null)
			throw new NullPointerException();
		try {
			return CoordInteger.parseColumnNumber(input);
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	/**
	 * 指定された文字列を、カンマ区切りの行位置を表す値として解析する。
	 * 前後の空白は除去されない。
	 * @param input	対象の文字列
	 * @return	値の配列、変換できない場合は <tt>null</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public CoordValue[] parseMultipleRowNumberAndCoordinate(String input) {
		int len = input.length();
		if (len == 0)
			return null;
		ArrayList<CoordValue> valuelist = new ArrayList<CoordValue>();
		int spos = 0;
		for (int i = 0; i < len; ++i) {
			char ch = input.charAt(i);
			if (ch == ',') {
				String strValue = input.substring(spos, i).trim();
				if (!strValue.isEmpty()) {
					try {
						CoordInteger coord = CoordInteger.parseRowNumber(strValue);
						valuelist.add(coord);
					} catch (NumberFormatException ex) {
						return null;
					}
				}
				spos = i + 1;
			}
			else if (ch == '[') {
				for (; i < len && input.charAt(i) != ']'; ++i);
				for (; i < len && input.charAt(i) != ','; ++i);
				String strValue = input.substring(spos, (i < len ? i-1 : i)).trim();
				if (!strValue.isEmpty()) {
					try {
						CoordCell coord = CoordCell.parse(strValue);
						valuelist.add(coord);
					} catch (NumberFormatException ex) {
						return null;
					}
				}
				spos = i + 1;
			}
		}
		if (spos < len) {
			String strValue = input.substring(spos).trim();
			if (!strValue.isEmpty()) {
				try {
					CoordInteger coord = CoordInteger.parseRowNumber(strValue);
					valuelist.add(coord);
				} catch (NumberFormatException ex) {
					return null;
				}
			}
		}
		
		if (valuelist.isEmpty())
			return null;
		else
			return valuelist.toArray(new CoordValue[valuelist.size()]);
	}

	/**
	 * 指定された文字列を、カンマ区切りの列位置を表す値として解析する。
	 * 前後の空白は除去されない。
	 * @param input	対象の文字列
	 * @return	値の配列、変換できない場合は <tt>null</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public CoordValue[] parseMultipleColumnNumberAndCoordinate(String input) {
		int len = input.length();
		if (len == 0)
			return null;
		ArrayList<CoordValue> valuelist = new ArrayList<CoordValue>();
		int spos = 0;
		for (int i = 0; i < len; ++i) {
			char ch = input.charAt(i);
			if (ch == ',') {
				String strValue = input.substring(spos, i).trim();
				if (!strValue.isEmpty()) {
					try {
						CoordInteger coord = CoordInteger.parseColumnNumber(strValue);
						valuelist.add(coord);
					} catch (NumberFormatException ex) {
						return null;
					}
				}
				spos = i + 1;
			}
			else if (ch == '[') {
				for (; i < len && input.charAt(i) != ']'; ++i);
				for (; i < len && input.charAt(i) != ','; ++i);
				String strValue = input.substring(spos, (i < len ? i-1 : i)).trim();
				if (!strValue.isEmpty()) {
					try {
						CoordCell coord = CoordCell.parse(strValue);
						valuelist.add(coord);
					} catch (NumberFormatException ex) {
						return null;
					}
				}
				spos = i + 1;
			}
		}
		if (spos < len) {
			String strValue = input.substring(spos).trim();
			if (!strValue.isEmpty()) {
				try {
					CoordInteger coord = CoordInteger.parseColumnNumber(strValue);
					valuelist.add(coord);
				} catch (NumberFormatException ex) {
					return null;
				}
			}
		}
		
		if (valuelist.isEmpty())
			return null;
		else
			return valuelist.toArray(new CoordValue[valuelist.size()]);
	}

	/**
	 * セルの値をオプションとして解析する。
	 * <em>validnames</em> の要素が空ではない場合、この配列内の名前のみ利用可能なオプションとして判定する。
	 * @param cell			解析対象のセル
	 * @param validnames	許可されたオプション名の配列、もしくは <tt>null</tt>
	 * @return	解析結果のオプション値、BLANK の場合は <tt>null</tt>
	 * @throws NullPointerException	<em>cell</em> が <tt>null</tt>、もしくは <em>validnames</em> の要素が <tt>null</tt> の場合
	 * @throws ConfigException	解析エラーが発生した場合
	 */
	static public OptionValue parseOption(Cell cell, String[] validnames) throws ConfigRecognitionException
	{
		if (cell == null || EtcPoiUtil.isBlankCellValue(cell))
			return null;
		if (!EtcPoiUtil.isStringCellValue(cell))
			throw new ConfigRecognitionException(new ConfigErrorDetail(cell, EtcMessages.getInstance().errConfigOptionUndefined));
		
		// check string
		String strCellValue = cell.getStringCellValue().trim();
		if (strCellValue.isEmpty())
			return null;	// cell is empty
		
		// extract option name and value
		int eindex = strCellValue.indexOf('=');
		if (eindex < 0)
			throw new ConfigRecognitionException(new ConfigErrorDetail(cell, EtcMessages.getInstance().errConfigOptionUndefined));
		String strName  = strCellValue.substring(0, eindex).trim();
		String strValue = strCellValue.substring(eindex + 1).trim();
		if (strName.isEmpty() || strValue.isEmpty())
			throw new ConfigRecognitionException(new ConfigErrorDetail(cell, EtcMessages.getInstance().errConfigOptionUndefined));
		
		// check option
		OptionValueFormat optformat = _typemap.get(strName);
		if (optformat == null)
			throw new ConfigRecognitionException(new ConfigErrorDetail(cell, EtcMessages.getInstance().errConfigOptionUndefined));
		if (validnames != null && validnames.length > 0) {
			boolean invalid = true;
			for (int i = 0; i < validnames.length; ++i) {
				if (validnames[i].equals(strName)) {
					invalid = false;
					break;
				}
			}
			if (invalid)
				throw new ConfigRecognitionException(new ConfigErrorDetail(cell, EtcMessages.getInstance().errConfigOptionUnsupported));
		}
		
		// parse option's keyword
		Class<?> keytype = optformat.keywordtype;
		if (keytype != null) {
			// キーワード
			Object obj = null;
			try {
				Method md = keytype.getMethod("fromName", String.class);
				obj = md.invoke(null, strValue);
			} catch (Throwable ex) {
				throw new UnsupportedOperationException("Failed to execute keyword parse operation : " + keytype.getName(), ex);
			}
			if (obj != null) {
				// keyword found
				return new OptionValue(new CellPosition(cell), strName, OptionValueTypes.Keyword, obj);
			}
		}
		
		// parse option value
		if (optformat.valuetypes != null) {
			for (OptionValueTypes vtype : optformat.valuetypes) {
				Object obj;
				switch (vtype) {
					case Integer :			// 整数値
						obj = parseInteger(strValue);
						break;
					case Decimal :			// 実数値
						obj = parseDecimal(strValue);
						break;
					case RowCoord :			// 行位置
						obj = parseRowCoordinate(strValue);
						break;
					case ColumnCoord :		// 列位置
						obj = parseColumnCoordinate(strValue);
						break;
					case CellCoord :		// セル位置
						obj = parseCellCoordinate(strValue);
						break;
					case MultiRowCoords :	// 複数の行位置
						obj = parseMultipleRowNumberAndCoordinate(strValue);
						break;
					case MultiColumnCoords :	// 複数の列位置
						obj = parseMultipleColumnNumberAndCoordinate(strValue);
						break;
					case String :			// 文字列リテラル
						obj = parseStringLiteral(strValue);
						break;
					case Pattern :			// 正規表現リテラル
						obj = parseRegexpLiteral(strValue);
						break;
					case DateTimePattern :	// 日付時刻フォーマット
						obj = parseDateTimeFormat(strValue);
						break;
					default : // unknown type
						obj = null;
				}
				if (obj != null) {
					// obj value parsing succeeded
					return new OptionValue(new CellPosition(cell), strName, vtype, obj);
				}
			}
		}
		
		// undefined option value
		throw new ConfigRecognitionException(new ConfigErrorDetail(cell, EtcMessages.getInstance().errConfigOptionIllegalValue));
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	static protected class OptionValueFormat
	{
		/** オプション名 **/
		public final String				name;
		/** キーワードの場合はキーワードクラス、そうでない場合は <tt>null</tt> */
		public final Class<?>			keywordtype;
		/** キーワード以外の値の種類 **/
		public final OptionValueTypes[]	valuetypes;
		
		public OptionValueFormat(String aname, Class<?> akeywordtype, OptionValueTypes...atypes) {
			this.name        = aname;
			this.keywordtype = akeywordtype;
			this.valuetypes  = atypes;
		}
	}
}
