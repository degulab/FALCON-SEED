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
 *  Copyright 2007-2012  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)CsvUtil.java	1.20	2012/03/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvUtil.java	1.17	2010/11/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvUtil.java	1.16	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.nio.csv;

import java.math.BigDecimal;

/**
 * CSV 形式のデータを処理するためのユーティリティ。
 * 
 * @version 1.20	2012/03/16
 * @since 1.16
 */
public class CsvUtil
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** CSV(Comma Separated Values) の区切り文字 **/
	static public final char CSV_DELIMITER_CHAR = ',';
	/** TSV(Tab Separated Values) の区切り文字 **/
	static public final char TSV_DELIMITER_CHAR = '\t';
	/** SSV(Space Separated Values) の区切り文字 **/
	static public final char SSV_DELIMITER_CHAR = ' ';
	/** シングルクオート文字 **/
	static public final char CSV_SINGLE_QUOTE_CHAR = '\'';
	/** 標準のクオート文字 **/
	static public final char CSV_QUOTE_CHAR = '\"';
	/** 改行文字(CR) **/
	static public final char CR = '\r';
	/** 改行文字(LF) **/
	static public final char LF = '\n';

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private CsvUtil() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * CSVフィールドの文字列からデータ型を判別する。
	 * このメソッドでは、数値型(<code>BigDecimal</code>)、真偽地(<code>Boolean</code>)、文字列(<code>String</code>)の
	 * どれかに判別する。
	 * 真偽値の場合、文字列が &quot;true&quot; もしくは &quot;false&quot; の場合のみ、真偽値と判定する。
	 * @param fieldValue	判定する文字列
	 * @return	判定結果のデータ型を示すクラスを返す。指定された文字列が <tt>null</tt> もしくは空文字の場合は <tt>null</tt> を返す。
	 * @since 1.17
	 */
	static public Class<?> detectDataType(String fieldValue) {
		if (fieldValue == null)
			return null;
		if (fieldValue.length() <= 0)
			return null;
		
		// 真偽値？
		if ("true".equalsIgnoreCase(fieldValue) || "false".equalsIgnoreCase(fieldValue)) {
			return Boolean.class;
		}
		
		// 数値？
		try {
			new BigDecimal(fieldValue);
			return BigDecimal.class;
		}
		catch (NumberFormatException ignoreEx) {}
		
		// 文字列
		return String.class;
	}
	
	/**
	 * CSVフィールドの文字列からデータ型を判別する。
	 * このメソッドでは、数値型(<code>BigDecimal</code>)、真偽地(<code>Boolean</code>)、文字列(<code>String</code>)の
	 * どれかに判別する。
	 * 真偽値の場合、文字列が &quot;true&quot; もしくは &quot;false&quot; の場合のみ、真偽値と判定する。
	 * <p>このメソッドでは、直前の判別結果を考慮する。
	 * 直前の判別結果が文字列型の場合、判別不要とみなし <tt>null</tt> を返す。
	 * 直前の判別結果と新しい判別結果が異なる場合、文字列(<code>String</code>)を返す。
	 * @param beforeDetectedType	直前の判別結果
	 * @param fieldValue	判定する文字列
	 * @return	判定結果のデータ型を示すクラスを返す。指定された文字列が <tt>null</tt> もしくは空文字の場合は <tt>null</tt> を返す。
	 * @since 1.17
	 */
	static public Class<?> detectDataType(Class<?> beforeDetectedType, String fieldValue) {
		if (beforeDetectedType == null) {
			return detectDataType(fieldValue);
		}
		else if (String.class == beforeDetectedType) {
			// 判別不要
			return null;
		}
		else {
			Class<?> type = detectDataType(fieldValue);
			if (type != null && type != beforeDetectedType) {
				return String.class;
			} else {
				return type;
			}
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
