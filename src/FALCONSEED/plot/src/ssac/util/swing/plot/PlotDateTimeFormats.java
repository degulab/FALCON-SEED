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
 *  <author> Hideaki Yagi (MRI)
 */
/*
 * @(#)PlotDateTimeFormats.java	2.1.0	2013/07/11
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.plot;

/**
 * プロット専用の日時フォーマットの集合。
 * 
 * @version 2.1.0	2013/07/11
 * @since 2.1.0
 */
public class PlotDateTimeFormats
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final String DefaultSpecifier = "default";
	
	static protected java.text.SimpleDateFormat	_defaultFormat = null;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** ユーザー定義の日付/時刻パターンのリスト **/
	protected java.util.List<java.text.SimpleDateFormat>	_patterns;
	/** 標準の日付/時刻パターンを使用するフラグ **/
	protected boolean			_useDefault;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 標準の日付/時刻パターンを持つ、新規の日付時刻書式定義を生成する。
	 */
	public PlotDateTimeFormats() {
		this._useDefault = true;
		this._patterns = null;
	}

	/**
	 * 指定されたパターンを持つ、新規の日付時刻書式定義を生成する。
	 * パターンは、日付時刻書式の文字列をカンマを区切り文字として解析する。
	 * ダブルクオートで囲まれた範囲内のカンマは通常文字として認識し、
	 * 区切り文字として認識しない。
	 * 日付時刻書式は、{@link java.text.SimpleDateFormat} の日付/時刻パターンによって記述する。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * ダブルクオートは日付時刻書式文字列のパターンに使用することは
	 * できないので、ダブルクオートをエスケープすることはできない。
	 * </blockquote>
	 * <p>指定された文字列が <tt>null</tt> もしくは空文字列の場合、
	 * 標準のパターンを持つ、新規の日付時刻書式定義を生成する。
	 * また、日付/時刻パターンに &quot;default&quot; と記述した場合、標準の
	 * 日付/時刻パターンを使用する。
	 * @param patterns	カンマ区切りの日付時刻書式文字列
	 * @throws IllegalArgumentException	指定された文字列に含まれる日付時刻書式が
	 * 										正しくない場合
	 */
	public PlotDateTimeFormats(String commaSeparatedPatterns) {
		if (commaSeparatedPatterns != null && commaSeparatedPatterns.length() > 0) {
			java.util.List<String> strPatternList = splitPatternsByComma(commaSeparatedPatterns);
			this._useDefault = false;
			java.util.ArrayList<java.text.SimpleDateFormat> formats = new java.util.ArrayList<java.text.SimpleDateFormat>(strPatternList.size());
			for (String strpat : strPatternList) {
				if (DefaultSpecifier == strpat) {
					this._useDefault = true;
				} else {
					java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(strpat);
					sdf.setLenient(false);
					formats.add(sdf);
				}
			}
			if (formats.isEmpty()) {
				this._useDefault = true;
				this._patterns = null;
			} else {
				formats.trimToSize();
				this._patterns = formats;
			}
		}
		else {
			this._useDefault = true;
			this._patterns = null;
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 標準の日付/時刻フォーマットを使用するかどうかを判定する。
	 * @return	標準のフォーマットを使用する場合は <tt>true</tt>、
	 * 			そうでない場合は <tt>false</tt>
	 */
	public boolean isUseDefaultFormats() {
		return _useDefault;
	}

	/**
	 * 標準の日付/時刻フォーマットを使用するかどうかを設定する。
	 * @param toUse	標準のフォーマットを使用する場合は <tt>true</tt>、
	 * 				使用しない場合は <tt>false</tt> を指定する。
	 */
	public void setUseDefaultFormats(boolean toUse) {
		_useDefault = toUse;
	}

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
	public String format(java.util.Calendar cal) {
		if (cal == null)
			return null;
		
		java.text.SimpleDateFormat sdf;
		if (_patterns != null && !_patterns.isEmpty()) {
			sdf = _patterns.get(0);
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
	public java.util.Calendar parse(String input) {
		if (input == null)
			return null;
		input = input.trim();

		if (_patterns != null) {
			for (java.text.DateFormat df : _patterns) {
				java.util.Date dt = null;
				try {
					dt = df.parse(input);
				} catch (java.text.ParseException ex) {
					dt = null;
				}
				if (dt != null) {
					// 厳密性を持たせるため、空白と0を除去した文字列に変換し、比較
					if (sameFormat(df, dt, input)) {
						java.util.Calendar cal;
						try {
							cal = java.util.Calendar.getInstance();
							cal.clear();
							cal.setLenient(false);
							cal.setTime(dt);
							//--- 再計算
							cal.get(java.util.Calendar.MILLISECOND);
						}
						catch (Throwable ex) {
							cal = null;
						}
						return cal;
					}
				}
			}
		}

		java.util.Calendar cal = null;
		if (_useDefault) {
			cal = parseDefaultDateTime(input);
		}
		return cal;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
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
	 * ダブルクオートは日付時刻書式文字列のパターンに使用することは
	 * できないので、ダブルクオートをエスケープすることはできない。
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
		boolean enquote = false;
		int spos = 0;
		int epos = -1;
		int pos = 0;
		for (; pos < len; pos++) {
			char ch = patterns.charAt(pos);
			if (ch == ',') {
				if (!enquote) {
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
				else {
					//--- enquoted
					sb.append(ch);
				}
			}
			else if (ch == '\"') {
				enquote = !enquote;
			}
			else {
				sb.append(ch);
			}
		}
		
		if (sb.length() > 0) {
			epos = pos;
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
	static protected final java.util.Calendar parseDefaultDateTime(String input) {
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
		
		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.clear();
		cal.setLenient(false);
		try {
			cal.set(year, month-1, day);
			cal.set(java.util.Calendar.HOUR_OF_DAY, hour);
			cal.set(java.util.Calendar.MINUTE, minute);
			cal.set(java.util.Calendar.SECOND, second);
			cal.set(java.util.Calendar.MILLISECOND, milli);
			//--- 再計算
			cal.get(java.util.Calendar.MILLISECOND);
		} catch (Throwable ex) {
			cal = null;
		}
		return cal;
	}
}
