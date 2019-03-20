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
 *  Copyright 2007-2014  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)String.java	2.0.0	2014/03/18 : move 'ssac.util' to 'ssac.aadl.macro.util' 
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)Strings.java	1.00	2008/10/06
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.util;

import java.util.ArrayList;


/**
 * 文字列操作ユーティリティ。
 * 
 * @version 2.0.0	2014/03/18
 *
 * @since 1.00
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
	 * コマンドラインとなる文字列を空白を区切り文字として分割する。
	 * ダブルクオート文字は次のダブルクオート文字まで区切り文字を無視する。
	 * ダブルクオート文字以降、連続したダブルクオート文字はダブルクオートの
	 * エスケープとし、単一のダブルクオート文字として認識する。
	 * なお、連続した空白文字は１つの区切りとする。
	 * 
	 * @param str	コマンドライン文字列
	 * @return	分割したコマンドライン文字列
	 */
	static public String[] splitCommandLineWithDoubleQuoteEscape(String str) {
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
