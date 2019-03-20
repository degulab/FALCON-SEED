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
 * @(#)SchemaDateTimeFormats.java	3.2.1	2015/07/21
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SchemaDateTimeFormats.java	3.2.0	2015/06/22
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module.schema.type;

import ssac.aadl.runtime.util.internal.FSDateTimeFormats;

/**
 * 日付時刻書式パターンを等値判定可能とするためのインタフェースを追加した、<code>FSDateTimeFormats</code> の派生クラス。
 * このオブジェクトは、不変オブジェクトとする。
 * @version 3.2.1
 * @since 3.2.0
 */
public class SchemaDateTimeFormats extends FSDateTimeFormats
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	private final String	_strPattern;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public SchemaDateTimeFormats() {
		super();
		_strPattern = toPatternsString(true);
	}

	public SchemaDateTimeFormats(String commaSeparatedPatterns) {
		super(commaSeparatedPatterns);
		_strPattern = toPatternsString(true);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 単一の日付時刻書式パターンを正規化する。
	 * <em>withEnquoteAsNeede</em> に <tt>true</tt> を指定した場合、必要に応じて、ダブルクオートによるエスケープを付加する。
	 * <em>singlePattern</em> が <tt>null</tt> もしくは空文字の場合は標準書式が指定されたものとみなす。
	 * @param singlePattern	対象の文字列
	 * @return	正規化されたパターン文字列
	 */
	static public String normalizeSinglePattern(String singlePattern, boolean withEnquoteAsNeeded) {
		if (singlePattern == null) {
			return FSDateTimeFormats.DefaultSpecifier;
		}
		
		singlePattern = singlePattern.trim();
		if (singlePattern.isEmpty()) {
			// default pattern
			return FSDateTimeFormats.DefaultSpecifier;
		}
		else if (FSDateTimeFormats.DefaultSpecifier.equalsIgnoreCase(singlePattern)) {
			// specifiled default keyword
			return FSDateTimeFormats.DefaultSpecifier;
		}
		else {
			// custom pattern
			if (withEnquoteAsNeeded) {
				return _ensureEnquoteAsNeeded(singlePattern);
			} else {
				return singlePattern;
			}
		}
	}
	
	static public String getDefaultSpecifier() {
		return FSDateTimeFormats.DefaultSpecifier;
	}

	@Override
	public String toString() {
		return _strPattern;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static protected String _ensureEnquoteAsNeeded(String strPattern) {
		int len = strPattern.length();
		boolean enquoted = false;
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; ++i) {
			char ch = strPattern.charAt(i);
			if (ch == ',') {
				enquoted = true;
			}
			else if (ch == '\"') {
				enquoted = true;
				sb.append(ch);
			}
			sb.append(ch);
		}
		if (enquoted) {
			sb.insert(0, '\"');
			sb.append('\"');
			return sb.toString();
		} else {
			return strPattern;
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
