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
 * @(#)Strings.java	2.2.0	2015/06/01
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.util;

/**
 * 文字列操作ユーティリティ。
 * 
 * @version 2.2.0
 * @since 2.2.0
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
	 * 指定された引数が <tt>null</tt> の場合に、空文字列を返す。
	 * @param value	判定する文字列
	 * @return 引数が <tt>null</tt> の場合は空文字列、それ以外の場合は引数の値を返す。
	 */
	static public final String nullToEmpty(String value) {
		return (value==null ? "" : value);
	}

	/**
	 * 指定された引数が空文字の場合に、<tt>null</tt> を返す。
	 * @param value	判定する文字列
	 * @return	引数の値が <tt>null</tt> もしくは空文字の場合は <tt>null</tt>、それ以外の場合は引数の値を返す。
	 */
	static public final String emptyToNull(String value) {
		return (value==null || value.isEmpty() ? null : value);
	}

	/**
	 * 引数に指定された文字列が <tt>null</tt> もしくは、長さ 0 の
	 * 文字列なら <tt>true</tt> を返す。
	 * 
	 * @param value	検査する文字列
	 * @return		<tt>null</tt> もしくは、長さ 0 の文字列なら <tt>true</tt>
	 */
	static public final boolean isNullOrEmpty(String value) {
		return (value == null || value.length() <= 0);
	}

	/**
	 * <em>str</em> が <em>prefix</em> で始まるかどうかを、大文字小文字を無視して判定する。
	 * @param str		判定する文字列
	 * @param prefix	接頭辞
	 * @return	<em>str</em> が大文字小文字に関係なく <em>prefix</em> で始まる場合は <tt>true</tt>、そうでない場合は <tt>false</tt>。
	 * 			<em>prefix</em> が空文字列の場合や、<em>str</em> が大文字小文字に関係なく <em>prefix</em> と等しい場合も <tt>true</tt>。
	 * @throws NullPointerException	<em>str</em> もしくは <em>prefix</em> が <tt>null</tt> の場合
	 */
	static public boolean startsWithIgnoreCase(String str, String prefix) {
		return startsWithIgnoreCase(str, prefix, 0);
	}

	/**
	 * <em>str</em> の指定されたインデックス以降の部分文字列が、<em>prefix</em> で始まるかどうかを、
	 * 大文字小文字を無視して判定する。
	 * @param str		判定する文字列
	 * @param prefix	接頭辞
	 * @param toffset	文字列の比較を開始する位置
	 * @return	<em>str</em> の <em>toffset</em> 以降の部分文字列が、大文字小文字に関係なく
	 * 			<em>prefix</em> で始まる場合は <tt>true</tt>、そうでない場合は <tt>false</tt>。
	 * 			<em>toffset</em> のインデックスが負もしくは <em>str</em> の長さより大きい場合は <tt>false</tt>。
	 * 			<em>toffset</em> が適切であり、<em>prefix</em> が空文字列の場合は <tt>true</tt>。
	 * @throws NullPointerException	<em>str</em> もしくは <em>prefix</em> が <tt>null</tt> の場合
	 */
	static public boolean startsWithIgnoreCase(String str, String prefix, int toffset) {
		int strlen = str.length();
		int prefixlen = prefix.length();
		if (toffset < 0 || toffset > strlen) {
			return false;
		}
		if (prefixlen == 0) {
			return true;	// matched anywhere
		}
		int endpos = toffset + prefixlen;
		if (endpos > strlen) {
			return false;	// prefix length does not enough in str
		}
		// judge
		String target = str.substring(toffset, endpos);
		return prefix.equalsIgnoreCase(target);
	}

	/**
	 * <em>str</em> が <em>suffix</em> で終わるかどうかを、大文字小文字を無視して判定する。
	 * @param str		判定する文字列
	 * @param suffix	接尾辞
	 * @return	<em>str</em> が大文字小文字に関係なく <em>suffix</em> で終わる場合は <tt>true</tt>、そうでない場合は <tt>false</tt>。
	 * 			<em>suffix</em> が空文字列の場合や、<em>str</em> が大文字小文字に関係なく <em>suffix</em> と等しい場合も <tt>true</tt>。
	 * @throws NullPointerException	<em>str</em> もしくは <em>suffix</em> が <tt>null</tt> の場合
	 */
	static public boolean endsWithIgnoreCase(String str, String suffix) {
		int strlen = str.length();
		int suffixlen = suffix.length();
		if (str == suffix) {
			return true;	// same instance
		}
		
		if (suffixlen == 0) {
			return true;	// suffix is empty
		}
		else if (suffixlen == strlen) {
			return suffix.equalsIgnoreCase(str);	// same length
		}
		else if (suffixlen < strlen) {
			// judge
			String target = str.substring(strlen - suffixlen);
			return suffix.equalsIgnoreCase(target);
		}
		else {
			// suffix longer than str
			return false;
		}
	}

	/**
	 * <em>text</em> 文字列内に、指定された文字のどれか一つが含まれているかを判定する。
	 * @param text	検証する文字列
	 * @param cs	文字の配列
	 * @return	指定された文字のどれか一つが含まれている場合は <tt>true</tt>、
	 * 			<em>text</em> が空文字列の場合は <tt>false</tt>、
	 * 			<em>cs</em> の要素が空の場合も <tt>false</tt>。
	 * @throws NullPointerException	<em>text</em> もしくは <em>cs</em> が <tt>null</tt> の場合
	 */
	static public boolean contains(final String text, char...cs) {
		int strlen = text.length();
		int cslen = cs.length;
		if (strlen > 0 && cslen > 0) {
			for (int i = 0; i < strlen; ++i) {
				char tc = text.charAt(i);
				for (char cc : cs) {
					if (cc == tc) {
						return true;	// exist
					}
				}
			}
		}
		// not found
		return false;
	}

	/**
	 * 文字列の先頭から指定文字数分の文字列を返す。
	 * <em>length</em> が <em>text</em> の長さ以上の場合、<em>text</em> をそのまま返す。
	 * <em>length</em> が 0 の場合、空文字列を返す。
	 * @param text		元の文字列
	 * @param length	取得する文字列長
	 * @return 抽出した文字列
	 * @throws NullPointerException	<em>text</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>length</em> が負の場合
	 */
	static public String left(String text, int length) {
		int strlen = text.length();
		if (length < 0) {
			throw new IllegalArgumentException("'length' is negative : " + length);
		}
		else if (strlen == 0) {
			return text;
		}
		else if (length == 0) {
			return "";
		}
		else if (length >= strlen) {
			return text;
		}
		else {
			return text.substring(0, length);
		}
	}

	/**
	 * 文字列の終端から指定文字数分の文字列を返す。
	 * <em>length</em> が <em>text</em> の長さ以上の場合、<em>text</em> をそのまま返す。
	 * <em>length</em> が 0 の場合、空文字列を返す。
	 * 指定の文字数が文字列の長さ以下の場合、引数の文字列をそのまま返す。
	 * @param text 		元の文字列
	 * @param length	取得する文字列長
	 * @return 抽出した文字列
	 * @throws NullPointerException	<em>text</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>length</em> が負の場合
	 */
	static public String right(String text, int length) {
		int strlen = text.length();
		if (length < 0) {
			throw new IllegalArgumentException("'length' is negative : " + length);
		}
		else if (strlen == 0) {
			return text;
		}
		else if (length == 0) {
			return "";
		}
		else if (length >= strlen) {
			return text;
		}
		else {
			return text.substring(strlen - length);
		}
	}

	/**
	 * 指定された文字列を JSON 形式の文字列値(ダブルクオートで囲まれたもの)に変換し、指定されたバッファに追加する。
	 * なお、コードが「\u0000」～「\u001F」の範囲、または「\u007F」～「\u009F」の範囲の場合は、
	 * 4桁の16進数に変換されたユニコード値として変換される。
	 * また、<em>text</em> が <tt>null</tt> の場合は <code>null</code> という文字列が追加される。
	 * @param buffer	変換した文字列を格納するバッファ
	 * @param text		変換する文字列
	 */
	static public void appendJsonString(StringBuilder buffer, String text) {
		if (text == null) {
			buffer.append("null");
			return;
		}
		
		int strlen = text.length();
		buffer.append('\"');
		for (int i = 0; i < strlen; ++i) {
			char ch = text.charAt(i);
			if (ch == '\"') {
				buffer.append("\\\"");
			}
			else if (ch == '\\') {
				buffer.append("\\\\");
			}
			else if (ch == '/') {
				buffer.append("\\/");
			}
			else if (ch == '\b') {
				buffer.append("\\b");
			}
			else if (ch == '\f') {
				buffer.append("\\f");
			}
			else if (ch == '\n') {
				buffer.append("\\n");
			}
			else if (ch == '\r') {
				buffer.append("\\r");
			}
			else if (ch == '\t') {
				buffer.append("\\t");
			}
			else if (Character.isISOControl(ch)) {
				int value = (int)ch;
				buffer.append("\\u");
				buffer.append(String.format("%04X", value));
			}
			else {
				buffer.append(ch);
			}
		}
		buffer.append('\"');
	}

	/**
	 * 
	 * @param text
	 * @return
	 */
	/**
	 * 指定された文字列を JSON 形式の文字列値(ダブルクオートで囲まれたもの)に変換する。
	 * なお、コードが「\u0000」～「\u001F」の範囲、または「\u007F」～「\u009F」の範囲の場合は、
	 * 4桁の16進数に変換されたユニコード値として変換される。
	 * また、<em>text</em> が <tt>null</tt> の場合は <code>null</code> という文字列が返される。
	 * @param text	変換する文字列
	 * @return	ダブルクオートで囲まれた JSON 形式の文字列値、もしくは <code>null</code> という文字列
	 */
	static public String toJsonString(String text) {
		StringBuilder sb = new StringBuilder();
		appendJsonString(sb, text);
		return sb.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
