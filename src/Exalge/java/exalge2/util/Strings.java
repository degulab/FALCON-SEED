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
 *  Copyright 2007-2009  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Li Hou(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
/*
 * @(#)Strings.java	0.982	2009/09/13
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package exalge2.util;

import java.util.ArrayList;

/**
 * 文字列操作ユーティリティ。
 * 
 * @author Hiroshi Deguchi(SOARS Project.)
 * @author Yasunari Ishizuka(PieCake.inc,)
 * 
 * @version 0.982	2009/09/13
 *
 * @since 0.982
 */
public final class Strings
{
	private Strings() {}
	
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 引数に指定された文字列が <tt>null</tt> もしくは、長さ 0 の
	 * 文字列なら <tt>true</tt> を返す。
	 * 
	 * @param value	検査する文字列
	 * @return		<tt>null</tt> もしくは、長さ 0 の文字列なら <tt>true</tt>
	 */
	static public boolean isNullOrEmpty(String value) {
		return (value == null || value.length() <= 0);
	}

	/**
	 * 引数に指定された文字列の前後空白を除去する。
	 * 
	 * @param value	対象の文字列
	 * @return	前後空白を除去した文字列を返す。引数が <tt>null</tt> の場合には <tt>null</tt> を返す。
	 */
	static public String trim(String value) {
		return (value==null ? null : value.trim());
	}

	/**
	 * 指定された文字列(<code>str</code>)が、指定された接頭辞(<code>prefix</code>)で
	 * 始まるかどうかを判定する。この判定では、大文字小文字は区別されない。
	 * 
	 * @param str		判定する文字列
	 * @param prefix	接頭辞
	 * @return	文字列が指定された接頭辞で始まる場合は <tt>true</tt>
	 */
	static public boolean startsWithIgnoreCase(String str, String prefix) {
		// 同じインスタンスなら true
		if (str == prefix)
			return true;
		
		// prefix の長さによる判定
		int plen = prefix.length();
		if (plen <= 0)
			return true;
		else if (plen > str.length())
			return false;	// prefix のほうが長い場合は false
		
		// 判定
		String target = str.substring(0, plen);
		return prefix.equalsIgnoreCase(target);
	}

	/**
	 * 指定された文字列(<code>str</code>)が、指定された接尾辞(<code>suffix</code>)で
	 * 終わるかどうかを判定する。この判定では、大文字小文字は区別されない。
	 * 
	 * @param str		判定する文字列
	 * @param suffix	接尾辞
	 * @return	文字列が指定された接尾辞で終わる場合は <tt>true</tt>
	 */
	static public boolean endsWithIgnoreCase(String str, String suffix) {
		// 同じインスタンスなら true
		if (str == suffix)
			return true;
		
		// suffix の長さによる判定
		int slen = suffix.length();
		if (slen <= 0)
			return true;
		else if (slen > str.length())
			return false;	// suffix のほうが長い場合は false
		
		// 判定
		String target = str.substring(str.length() - slen);
		return suffix.equalsIgnoreCase(target);
	}

	/**
	 * target 文字列内に、指定された文字のどれか一つでも含まれている場合に
	 * <tt>true</tt> を返す。
	 * <p>
	 * なお、target 引数が <tt>null</tt> もしくは、長さ 0 の場合は、常に <tt>false</tt> を返す。
	 * 
	 * @param target	検証する文字列
	 * @param cs		文字(群)
	 * @return	指定された文字のどれか一つが含まれている場合は <tt>true</tt>
	 */
	static public boolean contains(final String target, char...cs) {
		boolean exist = false;
		int len;
		if (target != null && (len = target.length()) > 0) {
			for (int i = 0; i < len; i++) {
				char tc = target.charAt(i);
				for (char cc : cs) {
					if (cc == tc) {
						return true;
					}
				}
			}
		}
		return exist;
	}
	
	/**
	 * 文字列をダブルクオートでエンクオートする。
	 * このメソッドは、フィールド区切り文字、改行文字、ダブルクオートが含まれている場合のみ、
	 * ダブルクオートでエンクオートした文字列を返す。それ以外の場合は、入力文字列をそのまま返す。
	 * 
	 * @param srcString エンクオートする文字列
	 * @param fieldDelimiter	フィールド区切り文字
	 * @return エンクオートした文字列
	 */
	public String csvEnquote(final String srcString, final char fieldDelimiter) {
		String retString = srcString;
		if (!Strings.isNullOrEmpty(srcString) && Strings.contains(srcString, fieldDelimiter, '"', '\r', '\n')) {
			StringBuilder sb = new StringBuilder(srcString.length() * 2);
			sb.append('"');
			sb.append(srcString.replaceAll("\"", "\"\""));
			sb.append('"');
			retString = sb.toString();
		}
		return retString;
	}

	/**
	 * コマンドラインとなる文字列を空白を区切り文字として分割する。
	 * ダブルクオート文字は次のダブルクオート文字まで区切り文字を無視する。
	 * ダブルクオート文字以降、連続したダブルクオート文字はダブルクオートの
	 * エスケープとし、単一のダブルクオート文字として認識する。
	 * なお、連続した空白文字は１つの区切りとする。
	 * 
	 * @param str	コマンドライン文字列
	 * @return	分割したコマンドライン文字列
	 */
	static protected String[] splitCommandLineWithDoubleQuoteEscape(String str) {
		final String delimiters = " \t\n\r\f";
		final int maxPosition = str.length();
		final StringBuilder sb = new StringBuilder(maxPosition);
		final ArrayList<String> args = new ArrayList<String>();

		int curPosition = 0;
		while (curPosition < maxPosition) {
			char c = str.charAt(curPosition);
			if (c == '\"') {
				curPosition++;
				while (curPosition < maxPosition) {
					c = str.charAt(curPosition);
					if (c == '\"') {
						int nextPosition = curPosition + 1;
						if (nextPosition < maxPosition) {
							if (str.charAt(nextPosition) == '\"') {
								sb.append(c);
								curPosition = nextPosition;
							} else {
								break;
							}
						} else {
							break;
						}
					} else {
						sb.append(c);
					}
					curPosition++;
				}
			}
			else if (delimiters.indexOf(c) >= 0) {
				args.add(sb.toString());
				sb.setLength(0);
				//--- skip delimiters
				int newPosition = curPosition+1;
				for (; (newPosition < maxPosition) && (delimiters.indexOf(str.charAt(newPosition)) >= 0); newPosition++);
				curPosition = newPosition - 1;
			}
			else {
				sb.append(c);
			}
			curPosition++;
		}
		if (sb.length() > 0) {
			args.add(sb.toString());
		}
		
		return args.toArray(new String[args.size()]);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
