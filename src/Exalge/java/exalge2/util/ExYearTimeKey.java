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
 *  Copyright 2007-2008  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Li Hou(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
/*
 * @(#)ExYearTimeKey.java	0.94	2008/05/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExYearTimeKey.java	0.91	2007/08/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */

package exalge2.util;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 年のみで表される時間キーユーティリティ。
 * <br>
 * &quot;Y2000&quot; のように、固定文字'Y'と四桁の西暦により構成される
 * 時間キーを扱う。
 * <br>
 * 年のみのため、ユニットも年を表すものとなる。
 * 
 * @version 0.94	2008/05/16
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 *
 * @since 0.91
 */
public class ExYearTimeKey extends AbExTimeKey
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static private final Pattern pat = Pattern.compile("\\A\\s*Y(\\d{4})\\s*\\z", Pattern.CASE_INSENSITIVE);

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 新しい <code>ExYearTimeKey</code> インスタンスを生成する。
	 * <br>
	 * 初期値は、デフォルトのロケール、タイムゾーンによる現在日時の年となる。
	 */
	public ExYearTimeKey() {
		Calendar cal = Calendar.getInstance();
		//setYear(cal.get(Calendar.YEAR));
		set(cal);
	}

	/**
	 * 指定されたカレンダーの値で、新しい <code>ExYearTimeKey</code> インスタンスを生成する。
	 * <br>
	 * このメソッドでは、カレンダーのロケールやタイムゾーンは考慮せず、年の値を
	 * そのまま利用する。
	 * 
	 * @param cal	Calendar の値
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException サポートされていない形式か、値が有効範囲にない場合
	 * 
	 * @since 0.94
	 */
	public ExYearTimeKey(Calendar cal) {
		set(cal);
	}
	
	/**
	 * 指定された時間キー文字列で表される値を保持する、
	 * 新しい <code>ExYearTimeKey</code> インスタンスを生成する。
	 * 
	 * @param timeKey 時間キー文字列
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException サポートされない形式か、値が有効範囲にない場合
	 */
	public ExYearTimeKey(String timeKey) {
		set(timeKey);
	}

	/**
	 * 指定された年の値を保持する、
	 * 新しい <code>ExYearTimeKey</code> インスタンスを生成する。
	 * 
	 * @param year 設定する年の値
	 * 
	 * @throws IllegalArgumentException 指定された値が許容範囲にない場合
	 */
	public ExYearTimeKey(int year) {
		setYear(year);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * このインスタンスが、指定された時間キーのフォーマットを
	 * サポートしているかどうかを検証する。
	 * 
	 * @param timeKey 検証する時間キー文字列
	 * 
	 * @return サポートしてれば <tt>true</tt>
	 */
	public boolean isSupported(String timeKey) {
		if (timeKey != null) {
			Matcher mat = pat.matcher(timeKey);
			if (mat.matches()) {
				String strValue = mat.group(1);
				try {
					int newyear = Integer.valueOf(strValue).intValue();
					return isValidYear(newyear);
				} catch (NumberFormatException ex) {
					// No implement
				}
			}
		}
		
		// Not supported
		return false;
	}
	
	/**
	 * 指定された時間キー文字列で、このインスタンスの値を更新する。
	 * 
	 * @param timeKey 時間キー文字列
	 * @return 値が更新された場合に <tt>true</tt> を返す
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException サポートされない形式か、値が有効範囲にない場合
	 */
	public boolean set(String timeKey) {
		if (timeKey == null)
			throw new NullPointerException();
		
		Matcher mat = pat.matcher(timeKey);
		if (!mat.matches()) {
			throw new IllegalArgumentException("This format not supportted");
		}
		
		String strYear = mat.group(1);
		int oldYear = getYear();
		try {
			int newyear = Integer.valueOf(strYear).intValue();
			setYear(newyear);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("Illegal number", ex);
		}
		
		return (oldYear != getYear());
	}

	/**
	 * 指定されたカレンダーの値で、このインスタンスの値を更新する。
	 * <br>
	 * このメソッドでは、カレンダーのロケールやタイムゾーンは考慮せず、年の値を
	 * そのまま利用する。
	 * 
	 * @param cal	Calendar の値
	 * @return	値が更新された場合に <tt>true</tt> を返す
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException サポートされていない形式か、値が有効範囲にない場合
	 * 
	 * @since 0.94
	 */
	public boolean set(Calendar cal) {
		if (cal == null)
			throw new NullPointerException();

		int oldYear = getYear();
		int newYear = setYear(cal.get(Calendar.YEAR));
		
		return (oldYear != newYear);
	}
	
	/**
	 * 指定されたユニットの値が、許容範囲内かどうかを検証する。
	 * <br>
	 * このメソッドは、{@link #isValidYear(int)} とまったく同じ振る舞いとなる。
	 * 
	 * @param unit 検証するユニットの値
	 * @return 許容範囲内であれば <tt>true</tt>
	 */
	public boolean isValidUnit(int unit) {
		return super.isValidYear(unit);
	}
	
	/**
	 * 現在のユニットの値を返す。
	 * <br>
	 * このメソッドは、{@link #getYear()} とまったく同じ振る舞いとなる。
	 * 
	 * @return ユニット
	 */
	public int getUnit() {
		return super.getYear();
	}
	
	/**
	 * ユニットの値として有効な範囲の最小値を返す。
	 * <br>
	 * このメソッドは、{@link #getMinYear()} とまったく同じ振る舞いとなる。
	 * 
	 * @return ユニットの最小値
	 */
	public int getMinUnit() {
		return super.getMinYear();
	}
	
	/**
	 * ユニットの値として有効な範囲の最大値を返す。
	 * <br>
	 * このメソッドは、{@link #getMaxYear()} とまったく同じ振る舞いとなる。
	 * 
	 * @return ユニットの最大値
	 */
	public int getMaxUnit() {
		return super.getMaxYear();
	}
	
	/**
	 * ユニットの値を更新する。
	 * <br>
	 * このメソッドは、{@link #setYear(int)} とまったく同じ振る舞いとなる。
	 * 
	 * @param unit 設定するユニットの値
	 * 
	 * @return 設定後のユニットの値を返す
	 * 
	 * @throws IllegalArgumentException 指定された値が許容範囲にない場合
	 */
	public int setUnit(int unit) {
		return super.setYear(unit);
	}
	
	/**
	 * 指定された値を、現在のユニットの値に加算する。
	 * <br>指定された値が負の場合、結果的に減算となる。
	 * <br>
	 * <b>注：</b>
	 * <blockquote>
	 * 加算(減算)結果が有効範囲を超える場合、例外がスローされる。
	 * </blockquote>
	 * <br>
	 * このメソッドは、{@link #addYear(int)} とまったく同じ振る舞いとなる。
	 * 
	 * @param amount 増減値
	 * @return 演算後のユニットの値を返す
	 * 
	 * @throws IllegalArgumentException 指定された値による演算結果が年の有効範囲外となる場合
	 */
	public int addUnit(int amount) {
		return super.addYear(amount);
	}
	
	/**
	 * 現在の値を、時間キー文字列として出力する。
	 * 
	 * @return 時間キー文字列
	 */
	public String toString() {
		return toFormattedYearString();
	}

}
