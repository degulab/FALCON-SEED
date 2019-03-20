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
 * @(#)SchemaUtil.java	3.2.0	2015/06/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module.schema;

/**
 * 汎用フィルタ定義のユーティリティ。
 * 
 * @version 3.2.0
 * @since 3.2.0
 */
public class SchemaUtil
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** 要素が空の文字列配列 **/
	static public final String[]	EMPTY_STRING_ARRAY	= new String[0];

	/** 文字列表現において、説明の前に付加される区切り文字 **/
	static public final char	DESC_SEPARATOR_CHAR			= '-';
	/** 文字列表現において、データ型の前に付加される区切り文字 **/
	static public final char	VALUETYPE_SEPARATOR_CHAR	= ':';
	/** 文字列表現において、親の文字列表現との区切り文字 **/
	static public final char	CLASS_SEPARATOR_CHAR		= '.';

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private SchemaUtil() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定された文字列が空文字列であれば、<tt>null</tt> に変換する。
	 * @param str	文字列
	 * @return	空文字列なら <tt>null</tt>、それ以外の場合は <em>str</em> をそのまま返す
	 */
	static public String emptyStringToNull(String str) {
		return (str != null && str.isEmpty() ? null : str);
	}

	/**
	 * 要素番号を正規化する。
	 * このメソッドでは、要素番号が負の値の場合に {@link SchemaElementObject#INVALID_ELEMENT_NO} を返す。
	 * @param elemNo	要素番号
	 * @return	正規化された要素番号
	 */
	static public int normalizeElementNo(int elemNo) {
		return (elemNo < 0 ? SchemaElementObject.INVALID_ELEMENT_NO : elemNo);
	}

	/**
	 * 指定された文字列値を、バッファに追加する。
	 * 文字列が <tt>null</tt> ではない場合、ダブルクオートで囲まれた文字列として追加する。
	 * @param buffer	バッファ
	 * @param strValue	追加する文字列
	 */
	static public void appendStringParam(StringBuilder buffer, String strValue) {
		if (strValue == null) {
			buffer.append("null");
		} else {
			buffer.append('\"');
			buffer.append(strValue);
			buffer.append('\"');
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
