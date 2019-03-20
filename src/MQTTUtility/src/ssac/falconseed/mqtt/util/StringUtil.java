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
 *  Copyright 2007-2013  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)Strings.java	1.0.0	2013/02/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.mqtt.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

/**
 * 文字列操作ユーティリティ。
 * 
 * @version 1.0.0	2013/02/28
 */
public class StringUtil
{
	private StringUtil() {}
	
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
	 * @param value		判定する <code>String</code>
	 * @return 引数が <tt>null</tt> の場合は空文字列、それ以外の場合は引数の値を返す。
	 */
	static public final String nullToEmpty(String value) {
		return (value==null ? "" : value);
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
	 * 文字列の先頭から指定文字数分の文字列を返す。
	 * 指定の文字数が文字列の長さ以下の場合、引数の文字列をそのまま返す。
	 * 
	 * @param text 元の文字列
	 * @param length 取得する文字列長
	 * @return 抽出した文字列
	 */
	static public String left(String text, int length) {
		if (text != null && text.length() > length) {
			return text.substring(0, length);
		} else {
			return text;
		}
	}

	/**
	 * 文字列の終端から指定文字数分の文字列を返す。
	 * 指定の文字数が文字列の長さ以下の場合、引数の文字列をそのまま返す。
	 * 
	 * @param text 元の文字列
	 * @param length 取得する文字列長
	 * @return 抽出した文字列
	 */
	static public String right(String text, int length) {
		if (text != null && text.length() > length) {
			return text.substring(text.length() - length);
		} else {
			return text;
		}
	}
	
	/**
	 * 指定された文字列をCSV出力用にダブルクオートでエンコードする。
	 * ダブルクオートが付加されるのは、" か , を含んでいる場合のみとなる。
	 * このとき " を "" に置き換える。
	 * 指定された文字列が null もしくは長さ 0 の文字列の場合は、長さ 0 の文字列を返す。
	 * 
	 * @param text 処理したい文字列
	 * @return 処理済文字列
	 */
	static public String enquote(String text) {
		// exist text?
		if (text == null || text.length() <= 0)
			return "";
		
		// exist '"' or ','
		if (0 > text.indexOf('"') && 0 > text.indexOf(','))
			return text;
		
		// enquote
		StringBuffer sb = new StringBuffer(text.length() + 10);
		sb.append('"');
		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			if ('"' == ch) {
				sb.append("\"\"");
			} else {
				sb.append(ch);
			}
		}
		sb.append('"');
		return sb.toString();
	}

	/**
	 * 1行のCSVフォーマット文字列から、各カラムの文字列を取得する。
	 * カラムの区切りは , とし、最後の区切り文字以降の文字列長が 0 で
	 * ある場合は、長さ 0 の文字列を最後のカラム文字列として返す。
	 * ダブルクオーテーション(")で囲まれた文字列は区切り文字を無視する。
	 * また、ダブルクオーテーションで囲まれた文字列内では、連続した
	 * ダブルクオーテーションは、単一ダブルクオーテーション文字とする。
	 * 区切り文字間の空白は文字として扱う。
	 * 
	 * @param text CSVフォーマットの文字列
	 * @return カラム毎の文字列の配列
	 */
	static public String[] parseCsvLine(String text) {
		// check text
		if (text == null || text.length() <= 0) {
			return new String[0];	// no text
		}
		
		// parse csv format
		final Vector<String> vec = new Vector<String>();
	    final StringBuffer sb = new StringBuffer(text.length());
	    
	    boolean inquote = false;
	    for (int iPos = 0; iPos < text.length(); iPos++) {
	    	char ch = text.charAt(iPos);
	    	if (inquote) {
	    		// '"'トークン内
	    		if (ch == '"') {
	    			char nch = ((iPos+1) < text.length() ? text.charAt(iPos+1) : '\0');
	    			if (nch == '"') {
	    				// トークン内のエスケープ
	    				sb.append(ch);
	    				iPos++;	// skip escape char
	    			}
	    			else {
	    				// トークン終了
	    				inquote = false;
	    			}
	    		}
	    		else {
	    			sb.append(ch);
	    		}
	    	}
	    	else if (ch == '"') {
	    		if (sb.length() > 0) {
	    			// カラム先頭の'"'でなければ、通常の文字として扱う
	    			sb.append(ch);
	    		}
	    		else {
	    			// カラム先頭の'"'は、トークン開始
	    			inquote = true;
	    		}
	    	}
	    	else if (ch == ',') {
	    		// カラム終端
	    		vec.add(sb.toString());
	    		sb.delete(0, sb.length());
	    	}
	    	else if (ch == '\r' || ch == '\n' || ch == '\u0085' || ch == '\u2028' || ch == '\u2029') {
	    		// 改行文字(Java)は、CSV行の終端とする
	    		break;
	    	}
	    	else {
	    		// 通常文字
	    		sb.append(ch);
	    	}
	    }
	    // 最終カラムのデータ追加
	    vec.add(sb.toString());
	    
	    // completed
	    return vec.toArray(new String[vec.size()]);
	}

	/**
	 * 文字列配列から1行のCSVフォーマット文字列を生成する。
	 * 
	 * @param texts 文字列配列
	 * @return CSVフォーマット文字列
	 */
	static public String buildCsvLine(String[] texts) {
		if (texts == null)
			return null;
		if (texts.length < 1)
			return null;
		
		StringBuffer sb = new StringBuffer();
		if (texts[0] != null && texts[0].length() > 0) {
			sb.append(enquote(texts[0]));
		}
		for (int i = 1; i < texts.length; i++) {
			sb.append(',');
			if (texts[i] != null && texts[i].length() > 0) {
				sb.append(enquote(texts[i]));
			}
		}
		return sb.toString();
	}
	
	/**
	 * 文字列のコレクションから1行のCSVフォーマット文字列を生成する。
	 * 
	 * @param texts 文字列のコレクション
	 * @return CSVフォーマット文字列
	 */
	static public String buildCsvLine(Collection<? extends String> texts) {
		if (texts == null)
			return null;
		if (texts.isEmpty())
			return null;
		
		StringBuffer sb = new StringBuffer();
		Iterator<? extends String> it = texts.iterator();
		String str = it.next();
		if (str != null && str.length() > 0) {
			sb.append(enquote(str));
		}
		while (it.hasNext()) {
			sb.append(',');
			str = it.next();
			if (str != null && str.length() > 0) {
				sb.append(enquote(str));
			}
		}
		return sb.toString();
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
