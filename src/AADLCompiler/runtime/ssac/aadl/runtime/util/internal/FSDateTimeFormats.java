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
 * @(#)FSDateTimeFormats.java	2.2.1	2015/07/20
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)FSDateTimeFormats.java	1.90	2013/08/02
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.util.internal;

import java.util.Calendar;

/**
 * 日付時刻の文字列書式を複数保持するクラス。
 * ここに設定されたフォーマットによって、文字列を <code>Calendar</code> オブジェクトに変換する。
 * <p>{@link #format(Calendar)} メソッドによって日付時刻オブジェクトを文字列に整形する際、
 * 指定された日付時刻パターンの先頭にあるパターンが使用される。
 * <p>なお、このオブジェクトは不変オブジェクトである。
 * 
 * @version 2.2.1
 * @since 1.90
 */
public class FSDateTimeFormats
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** 標準を示す日付時刻書式パターン **/
	static public final String DefaultSpecifier = "default";

	/** 標準の日付時刻書式パターン **/
	static protected java.text.SimpleDateFormat	_defaultFormat = null;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** ユーザー定義の日付/時刻パターンのリスト：(<tt>null</tt> 要素は標準フォーマットを使用することを示す) **/
	protected java.util.List<java.text.SimpleDateFormat>	_patterns;
	/** 標準の日付/時刻パターンを使用するフラグ **/
	protected boolean			_useDefault;
	/** このオブジェクトのハッシュコード **/
	private int	_hashCode;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 標準の日付/時刻パターンを持つ、新規の日付時刻書式定義を生成する。
	 */
	public FSDateTimeFormats() {
		this._useDefault = true;
		this._patterns = null;
		_hashCode = calcHashCode();
	}

	/**
	 * 指定されたパターンを持つ、新規の日付時刻書式定義を生成する。
	 * パターンは、日付時刻書式の文字列を CSV 形式の文字列として解析する。
	 * 日付時刻書式は、{@link java.text.SimpleDateFormat} の日付/時刻パターンによって記述する。
	 * <p>指定された文字列が <tt>null</tt> もしくは空文字列の場合、
	 * 標準のパターンを持つ、新規の日付時刻書式定義を生成する。
	 * また、日付/時刻パターンに &quot;default&quot; と記述した場合、標準の
	 * 日付/時刻パターンを使用する。
	 * @param patterns	カンマ区切りの日付時刻書式文字列
	 * @throws IllegalArgumentException	指定された文字列に含まれる日付時刻書式が
	 * 										正しくない場合
	 */
	public FSDateTimeFormats(String commaSeparatedPatterns) {
		if (commaSeparatedPatterns != null && commaSeparatedPatterns.length() > 0) {
			java.util.List<String> strPatternList = splitPatternsByComma(commaSeparatedPatterns);
			boolean hasCustomPattern = false;
			this._useDefault = false;
			java.util.ArrayList<java.text.SimpleDateFormat> formats = new java.util.ArrayList<java.text.SimpleDateFormat>(strPatternList.size());
			for (String strpat : strPatternList) {
				if (DefaultSpecifier == strpat) {
					this._useDefault = true;
					formats.add(null);
				} else {
					hasCustomPattern = true;
					java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(strpat);
					sdf.setLenient(false);
					formats.add(sdf);
				}
			}
			if (formats.isEmpty() || !hasCustomPattern) {
				// No custom formats
				this._useDefault = true;
				this._patterns = null;
			} else {
				// Included custom formats
				formats.trimToSize();
				this._patterns = formats;
			}
		}
		else {
			// No patterns
			this._useDefault = true;
			this._patterns = null;
		}
		_hashCode = calcHashCode();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 標準の日付/時刻フォーマットのみである場合に <tt>true</tt> を返す。
	 * @since 2.2.1
	 */
	public boolean isOnlyDefaultFormat() {
		return (_useDefault && _patterns==null);
	}

	/**
	 * 標準の日付/時刻フォーマットを使用するかどうかを判定する。
	 * @return	標準のフォーマットを使用する場合は <tt>true</tt>、
	 * 			そうでない場合は <tt>false</tt>
	 */
	public boolean isUseDefaultFormats() {
		return _useDefault;
	}

//	/**
//	 * 標準の日付/時刻フォーマットを使用するかどうかを設定する。
//	 * @param toUse	標準のフォーマットを使用する場合は <tt>true</tt>、
//	 * 				使用しない場合は <tt>false</tt> を指定する。
//	 */
//	public void setUseDefaultFormats(boolean toUse) {
//		_useDefault = toUse;
//	}

	/**
	 * 指定された <code>Calendar</code> オブジェクトを、
	 * このオブジェクトに設定された日付/時刻フォーマットに従い、
	 * 文字列に変換する。
	 * <p>ユーザー定義の日付/時刻フォーマットが設定されている場合は、
	 * 最初のユーザー定義の日付/時刻フォーマットによって変換する。
	 * <p>ユーザー定義の日付/時刻フォーマットが設定されていない場合は、
	 * 日付をスラッシュ区切り、時刻をコロン区切り、ミリ秒をピリオド区切りの
	 * 文字列として出力する。
	 * @param cal	<code>Calendar</code> オブジェクト
	 * @return	変換した文字列を返す。引数が <tt>null</tt> の場合は <tt>null</tt> を返す。
	 */
	public String format(Calendar cal) {
		if (cal == null)
			return null;
		
		java.text.SimpleDateFormat sdf;
		if (_patterns != null && !_patterns.isEmpty()) {
			sdf = _patterns.get(0);
			if (sdf == null)
				sdf = getDefaultDateFormat();
		} else {
			sdf = getDefaultDateFormat();
		}
		
		return sdf.format(cal.getTime());
	}

	/**
	 * 指定された文字列を、このオブジェクトに設定された日付/時刻フォーマットに
	 * 従い、<code>Calendar</code> オブジェクトに変換する。
	 * 基本的に、デフォルトのロケールに準じた日付時刻を返す。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このメソッドは例外をスローしない。
	 * </blockquote>
	 * @param	input	変換する文字列
	 * @return	変換できた場合は <code>Calendar</code> オブジェクト、
	 * 			変換できなかった場合は <tt>null</tt> を返す。
	 */
	public Calendar parse(String input) {
		if (input == null)
			return null;
		input = input.trim();

		Calendar cal = null;
		if (_patterns != null && !_patterns.isEmpty()) {
			// use custom pattern
			for (java.text.DateFormat df : _patterns) {
				if (df == null) {
					// use default pattern
					cal = parseDefaultDateTime(input);
					if (cal != null) {
						// succeeded
						break;
					}
				}
				else {
					// use custom pattern
					java.util.Date dt = null;
					try {
						dt = df.parse(input);
					} catch (java.text.ParseException ex) {
						dt = null;
					}
					if (dt != null) {
						// 厳密性を持たせるため、空白と0を除去した文字列に変換し、比較
						if (sameFormat(df, dt, input)) {
							// succeeded
							try {
								cal = Calendar.getInstance();
								cal.clear();
								cal.setLenient(false);
								cal.setTime(dt);
								//--- 再計算
								cal.get(Calendar.MILLISECOND);
							}
							catch (Throwable ex) {
								cal = null;
							}
							break;
						}
					}
				}
			}
		}
		else if (_useDefault) {
			// use only default pattern
			cal = parseDefaultDateTime(input);
		}
		
		return cal;
	}

	/**
	 * 指定された文字列が、単一の標準パターンを示す文字列かを検証する。
	 * 判定時、指定されたメソッドの前後にある空白は除去される。
	 * なお、文字列が <tt>null</tt> もしくは空文字の場合、このメソッドは <tt>false</tt> を返す。
	 * @param pattern	判定対象の文字列
	 * @return	単一の標準パターンを示す文字列であれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @since 2.2.1
	 */
	static public boolean isSingleDefaultPatternString(String pattern) {
		pattern = normalizeDefaultString(pattern);
		return (DefaultSpecifier == pattern);
	}

	/**
	 * 指定された文字列が、単一の日付時刻パターンを示す文字列かを検証する。
	 * 判定時、指定されたメソッドの前後にある空白は除去される。
	 * なお、文字列が <tt>null</tt> もしくは空文字の場合、このメソッドは <tt>false</tt> を返す。
	 * @param pattern	判定対象の文字列
	 * @return	単一の日付時刻パターンを示す文字列であれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @since 2.2.1
	 */
	static public boolean isValidSinglePatternString(String pattern) {
		pattern = normalizeDefaultString(pattern);
		if (pattern != null && !pattern.isEmpty()) {
			if (DefaultSpecifier == pattern) {
				// default pattern
				return true;
			}
			else {
				try {
					checkDateTimeFormatString(pattern, pattern);
					// valid pattern
					return true;
				} catch (Throwable ex) {
					// invalid pattern
				}
			}
		}
		// invalid
		return false;
	}

	/**
	 * 現在の日付時刻書式の全パターンを、単一の文字列に変換する。
	 * <em>withEnquote</em> に <tt>true</tt> を指定した場合、
	 * このメソッドが返す文字列はこのオブジェクトを復元可能な文字列となる。
	 * @param withEnquote	必要な場合にダブルクオートによるエスケープを行うのであれば <tt>true</tt>、
	 * 						全パターンを単純に文字列に変換するのみとする場合は <tt>false</tt>
	 * @return	全パターンのカンマ区切りの文字列
	 */
	public String toPatternsString(boolean withEnquote) {
		StringBuilder sb = new StringBuilder();
		if (_patterns != null && !_patterns.isEmpty()) {
			// included custom patterns
			if (withEnquote) {
				// with enquote as needed
				for (java.text.SimpleDateFormat sdf : _patterns) {
					if (sb.length() > 0)
						sb.append(',');
					if (sdf == null) {
						sb.append(DefaultSpecifier);
					} else {
						String strPattern = sdf.toPattern();
						boolean hasComma  = (strPattern.indexOf(',') >= 0);
						boolean hasDQuote = (strPattern.indexOf('\"') >= 0);
						if (hasComma || hasDQuote) {
							// enquote
							sb.append('\"');
							if (hasDQuote) {
								// escape double quote
								for (int i = 0; i < strPattern.length(); ++i) {
									char ch = strPattern.charAt(i);
									sb.append(ch);
									if (ch == '\"')
										sb.append(ch);	// double quote escaped
								}
							} else {
								// enquote only
								sb.append(strPattern);
							}
							sb.append('\"');
						} else {
							// normal
							sb.append(strPattern);
						}
					}
				}
			}
			else {
				// without enquote
				for (java.text.SimpleDateFormat sdf : _patterns) {
					if (sb.length() > 0)
						sb.append(',');
					if (sdf == null) {
						sb.append(DefaultSpecifier);
					} else {
						sb.append(sdf.toPattern());
					}
				}
			}
		}
		else if (_useDefault) {
			// only default pattern
			sb.append(DefaultSpecifier);
		}
		return sb.toString();
	}

	/**
	 * このオブジェクトのハッシュ値を返す。
	 */
	@Override
	public int hashCode() {
		return _hashCode;
	}

	/**
	 * 指定されたオブジェクトと自身が等しいかどうかを判定する。
	 * このオブジェクトでは、日付時刻書式のパターン構成が等しいかどうかを判定する。
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		
		if (obj != null && getClass() == obj.getClass()) {
			if (equalFields(obj)) {
				return true;
			}
		}
		
		// not equals
		return false;
	}

	/**
	 * 現在の日付時刻書式の全パターンを、単一の文字列に変換する。
	 * このメソッドが返す文字列はこのオブジェクトを復元可能な文字列となる。
	 * @return	全パターンのカンマ区切りの文字列
	 * @since 2.2.1
	 */
	@Override
	public String toString() {
		return toPatternsString(true);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * このオブジェクトのハッシュ値を計算する。
	 * @return	ハッシュ値
	 * @since 2.2.1
	 */
	private int calcHashCode() {
		int h = _useDefault ? 1231 : 1237;
		if (_patterns != null && !_patterns.isEmpty()) {
			for (java.text.SimpleDateFormat sdf : _patterns) {
				h = 31 * h + (sdf==null ? 0 : sdf.hashCode());
			}
		} else {
			h = 31 * h + 0;
		}
		return h;
	}

	/**
	 * 指定されたオブジェクトと、すべてのメンバーフィールドの値が等しいかを判定する。
	 * @param obj	比較するオブジェクト
	 * @return	等しい場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @since 2.2.1
	 */
	protected boolean equalFields(Object obj) {
		FSDateTimeFormats aFormats = (FSDateTimeFormats)obj;
		
		if (aFormats._hashCode != this._hashCode)
			return false;
		
		if (aFormats._useDefault != this._useDefault)
			return false;
		
		if (!(aFormats._patterns==null ? this._patterns==null : aFormats._patterns.equals(this._patterns)))
			return false;
		
		// equal all fields
		return true;
	}

	static protected boolean sameFormat(java.text.DateFormat df, java.util.Date date, String input) {
		String normalized = input.replaceAll("\\s|0", "");
		String formatted = df.format(date).replaceAll("\\s|0", "");
		return normalized.equals(formatted);
	}

	/**
	 * 日付時刻書式の文字列をカンマを区切り文字として分割する。
	 * ダブルクオートで囲まれた範囲内のカンマは通常文字として認識し、
	 * 区切り文字として認識しない。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * 通常の CSV と同様、ダブルクオートもエスケープする。
	 * ただし、フィールドの先頭がダブルクオートで始まっていないものは、
	 * エスケープの対象としない。
	 * </blockquote>
	 * @param patterns	カンマ区切りの日付時刻書式文字列
	 * @return	分割された日付時刻書式文字列のリストを返す。空文字列の
	 * 			要素は取り除かれる。日付時刻書式文字列が一つも指定されて
	 * 			いない場合、要素が空のリストを返す。
	 * @throws IllegalArgumentException	指定された文字列に含まれる日付時刻書式が
	 * 										正しくない場合
	 */
	static protected final java.util.ArrayList<String> splitPatternsByComma(String patterns)
	{
		if (patterns == null)
			return new java.util.ArrayList<String>(0);
		int len = patterns.length();
		if (len <= 0)
			return new java.util.ArrayList<String>(0);

		java.util.ArrayList<String> retlist = new java.util.ArrayList<String>();
		StringBuilder sb = new StringBuilder(len);
		int spos = 0;
		int epos = -1;
		int pos = 0;
		for (; pos < len; ++pos) {
			char ch = patterns.charAt(pos);
			
			// check enquote and get enquoted
			if (ch == '\"') {
				// enquote
				for(++pos; pos < len; ++pos) {
					ch = patterns.charAt(pos);
					if (ch == '\"') {
						++pos;
						if (pos >= len || patterns.charAt(pos) != '\"') {
							// end of enquote
							break;
						}
					}
					sb.append(ch);
				}
			}
			
			// get normal
			for (; pos < len; ++pos) {
				ch = patterns.charAt(pos);
				if (ch == ',') {
					break;
				}
				sb.append(ch);
			}
			
			// add field
			epos = pos;
			String strInput = patterns.substring(spos, epos);
			String strPattern = normalizeDefaultString(sb.toString());
			sb.setLength(0);
			if (strPattern.length() > 0) {
				if (DefaultSpecifier != strPattern) {
					checkDateTimeFormatString(strPattern, strInput);
				}
				retlist.add(strPattern);
			}
			epos = -1;
			spos = pos+1;
		}
		
		if (sb.length() > 0) {
			epos = len;
			String strInput = patterns.substring(spos, epos);
			String strPattern = normalizeDefaultString(sb.toString());
			if (strPattern.length() > 0) {
				if (DefaultSpecifier != strPattern) {
					checkDateTimeFormatString(strPattern, strInput);
				}
				retlist.add(strPattern);
			}
		}
		retlist.trimToSize();
		
		return retlist;
	}
	
	static protected final void checkDateTimeFormatString(String pattern, String inputString)
	{
		try {
			new java.text.SimpleDateFormat(pattern);
		}
		catch (Throwable ex) {
			StringBuilder sb = new StringBuilder();
			sb.append("Illegal date time format pattern : ");
			if (inputString == null) {
				sb.append(inputString);
			} else {
				sb.append('\'');
				sb.append(inputString);
				sb.append('\'');
			}
			String exmsg = ex.getLocalizedMessage();
			if (exmsg != null) {
				sb.append("\n(Cause) ");
				sb.append(exmsg);
			}
			throw new IllegalArgumentException(sb.toString(), ex);
		}
	}
	
	static protected final String normalizeDefaultString(String pattern) {
		if (pattern == null)
			return pattern;
		if (pattern.length() <= 0)
			return pattern;
		
		String trimedpat = pattern.trim();
		if (DefaultSpecifier.equalsIgnoreCase(trimedpat)) {
			// default
			return DefaultSpecifier;
		} else {
			// not default
			return trimedpat;
		}
	}
	
	static protected java.text.SimpleDateFormat getDefaultDateFormat() {
		if (_defaultFormat == null) {
			_defaultFormat = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
		}
		return _defaultFormat;
	}
	
	static protected final java.util.regex.Pattern[] patterns = {
		java.util.regex.Pattern.compile("\\A\\s*(\\d+)\\/(\\d+)\\/(\\d+)(?:\\s+(\\d+)\\:(\\d+)(?:\\:(\\d+)(?:\\.(\\d+))?)?)?\\s*\\z", java.util.regex.Pattern.DOTALL),
		java.util.regex.Pattern.compile("\\A\\s*(\\d+)\\-(\\d+)\\-(\\d+)(?:\\s+(\\d+)\\:(\\d+)(?:\\:(\\d+)(?:\\.(\\d+))?)?)?\\s*\\z", java.util.regex.Pattern.DOTALL),
		java.util.regex.Pattern.compile("\\A\\s*(\\d+)\\.(\\d+)\\.(\\d+)(?:\\s+(\\d+)\\:(\\d+)(?:\\:(\\d+)(?:\\.(\\d+))?)?)?\\s*\\z", java.util.regex.Pattern.DOTALL),
	};

	/**
	 * 指定された文字列を標準の日付/時刻フォーマットで解析し、<code>Calendar</code> オブジェクトを生成する。
	 * 日付のフォーマットは、次のどれかを受け付ける。
	 * <ul>
	 * <li>yyyy/MM/dd</li>
	 * <li>yyyy-MM-dd</li>
	 * <li>yyyy.MM.dd</li>
	 * </ul>
	 * また、時刻のフォーマットは、次のどれかを受け付ける。
	 * <ul>
	 * <li>HH:mm</li>
	 * <li>HH:mm:ss</li>
	 * <li>HH:mm:ss.SSS<li>
	 * </ul>
	 * なお、日付や時刻の数値は、日付や時刻として正当な数値の場合のみ変換に成功する。
	 * 例えば、13月や 9月31日などは、正当な日付ではないため、変換できない。
	 * @param input	入力する文字列
	 * @return	変換できた場合は <code>Calendar</code> オブジェクト、
	 * 			変換できなかった場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static protected final Calendar parseDefaultDateTime(String input) {
		java.util.regex.Matcher matched = null;
		for (java.util.regex.Pattern pat : patterns) {
			java.util.regex.Matcher mc = pat.matcher(input);
			if (mc.matches()) {
				// matched!
				matched = mc;
			}
		}
		if (matched == null)
			return null;	// no match
		
		int gcnt = matched.groupCount();
		if (gcnt < 3)
			return null;	// no match
		
		int year   = Integer.parseInt(matched.group(1));
		int month  = Integer.parseInt(matched.group(2));
		int day    = Integer.parseInt(matched.group(3));
		int hour   = 0;
		int minute = 0;
		int second = 0;
		int milli  = 0;
		if (gcnt >= 4) {
			String val = matched.group(4);
			if (val != null) {
				hour = Integer.parseInt(val);
			}
		}
		if (gcnt >= 5) {
			String val = matched.group(5);
			if (val != null) {
				minute = Integer.parseInt(val);
			}
		}
		if (gcnt >= 6) {
			String val = matched.group(6);
			if (val != null) {
				second = Integer.parseInt(val);
			}
		}
		if (gcnt >= 7) {
			String val = matched.group(7);
			if (val != null) {
				milli  = Integer.parseInt(val);
			}
		}
		
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.setLenient(false);
		try {
			cal.set(year, month-1, day);
			cal.set(Calendar.HOUR_OF_DAY, hour);
			cal.set(Calendar.MINUTE, minute);
			cal.set(Calendar.SECOND, second);
			cal.set(Calendar.MILLISECOND, milli);
			//--- 再計算
			cal.get(Calendar.MILLISECOND);
		} catch (Throwable ex) {
			cal = null;
		}
		return cal;
	}
}
