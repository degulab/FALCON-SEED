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
 * @(#)ExQuarterTimeKey.java	0.94	2008/05/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExQuarterTimeKey.java	0.91	2007/08/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */

package exalge2.util;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 年と四半期で表される時間キーユーティリティ。
 * <br>
 * &quot;Y2000Q1&quot; のように、固定文字'Y'と四桁の西暦、
 * 固定文字'Q'と四半期(1 ～ 4)により構成される時間キーを扱う。
 * <br>
 * このクラスでは、四半期がユニットとなる。
 * <p>
 * <b>(注)</b>実際の月と四半期との関係はない。
 * 
 * @version 0.94	2008/05/16
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 *
 * @since 0.91
 */
public class ExQuarterTimeKey extends AbExTimeKey
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final int minUnit = 1;
	static private final int maxUnit = 4;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static private final Pattern pat = Pattern.compile("\\A\\s*Y(\\d{4})Q(\\d)\\s*\\z", Pattern.CASE_INSENSITIVE);

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 新しい <code>ExQuarterTimeKey</code> インスタンスを生成する。
	 * <br>
	 * 初期値は、デフォルトのロケール、タイムゾーンによる現在日時の年と
	 * 月から判定された四半期となる。このコンストラクタのみ、1月～3月を
	 * &quot;Q1&quot;、4月～6月を&quot;Q2&quot;、7月～9月を&quot;Q3&quot;、
	 * 10月～12月を&quot;Q4&quot; に変換した値を設定する。
	 */
	public ExQuarterTimeKey() {
		Calendar cal = Calendar.getInstance();
		/*---
		setYear(cal.get(Calendar.YEAR));
		
		int month = cal.get(Calendar.MONTH)+1;
		if (1 <= month && month <= 3)
			setUnit(1);
		else if (4 <= month && month <= 6)
			setUnit(2);
		else if (7 <= month && month <= 9)
			setUnit(3);
		else
			setUnit(4);
		---*/
		set(cal);
	}

	/**
	 * 指定されたカレンダーの値で、新しい <code>ExQuarterTimeKey</code> インスタンスを生成する。
	 * <br>
	 * このメソッドでは、カレンダーのロケールやタイムゾーンは考慮せず、年の値と月の値を
	 * そのまま利用する。
	 * <br>
	 * カレンダーの年と月から判定された四半期とし、1月～3月を
	 * &quot;Q1&quot;、4月～6月を&quot;Q2&quot;、7月～9月を&quot;Q3&quot;、
	 * 10月～12月を&quot;Q4&quot; に変換した値を設定する。
	 * 
	 * @param cal	Calendar の値
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException サポートされていない形式か、値が有効範囲にない場合
	 * 
	 * @since 0.94
	 */
	public ExQuarterTimeKey(Calendar cal) {
		set(cal);
	}
	
	/**
	 * 指定された時間キー文字列で表される値を保持する、
	 * 新しい <code>ExQuarterTimeKey</code> インスタンスを生成する。
	 * 
	 * @param timeKey 時間キー文字列
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException サポートされない形式か、値が有効範囲にない場合
	 */
	public ExQuarterTimeKey(String timeKey) {
		set(timeKey);
	}

	/**
	 * 指定された年の値を保持する、
	 * 新しい <code>ExQuarterTimeKey</code> インスタンスを生成する。
	 * 
	 * @param year 設定する年の値
	 * @param quarter 設定する四半期(ユニット)の値
	 * 
	 * @throws IllegalArgumentException 指定された値が許容範囲にない場合
	 */
	public ExQuarterTimeKey(int year, int quarter) {
		setYear(year);
		setUnit(quarter);
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
				String strYear = mat.group(1);
				String strQuarter = mat.group(2);
				try {
					int newyear = Integer.valueOf(strYear).intValue();
					int newquat = Integer.valueOf(strQuarter).intValue();
					return (isValidYear(newyear) && isValidUnit(newquat));
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
		String strQuat = mat.group(2);
		int oldYear = getYear();
		int oldUnit = getUnit();
		try {
			int newyear = Integer.valueOf(strYear).intValue();
			int newunit = Integer.valueOf(strQuat).intValue();
			setYear(newyear);
			setUnit(newunit);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("Illegal number", ex);
		}
		
		return (oldYear != getYear() || oldUnit != getUnit());
	}

	/**
	 * 指定されたカレンダーの値で、このインスタンスの値を更新する。
	 * <br>
	 * このメソッドでは、カレンダーのロケールやタイムゾーンは考慮せず、年の値と月の値を
	 * そのまま利用する。
	 * <br>
	 * カレンダーの年と月から判定された四半期とし、1月～3月を
	 * &quot;Q1&quot;、4月～6月を&quot;Q2&quot;、7月～9月を&quot;Q3&quot;、
	 * 10月～12月を&quot;Q4&quot; に変換した値を設定する。
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

		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH)+1;
		int quat;
		if (1 <= month && month <= 3)
			quat = 1;
		else if (4 <= month && month <= 6)
			quat = 2;
		else if (7 <= month && month <= 9)
			quat = 3;
		else
			quat = 4;
		
		boolean ret = false;
		if (year != getYear() || quat != getUnit()) {
			setYear(year);
			setUnit(quat);
			ret = true;
		}
		
		return ret;
	}
	
	/**
	 * ユニットの値として有効な範囲の最小値を返す。
	 * 
	 * @return ユニットの最小値
	 */
	public int getMinUnit() {
		return minUnit;
	}
	
	/**
	 * ユニットの値として有効な範囲の最大値を返す。
	 * 
	 * @return ユニットの最大値
	 */
	public int getMaxUnit() {
		return maxUnit;
	}
	
	/**
	 * 指定された値を、現在のユニットの値に加算する。
	 * <br>指定された値が負の場合、結果的に減算となる。
	 * 加減算によってユニット値がユニットの有効範囲(1～4)を超える場合、
	 * 4四半期を年の単位として年値を増減する。
	 * <br>
	 * <b>注：</b>
	 * <blockquote>
	 * 加算(減算)結果が年の有効範囲を超える場合、例外がスローされる。
	 * </blockquote>
	 * 
	 * @param amount 増減値
	 * @return 演算後のユニットの値を返す
	 * 
	 * @throws IllegalArgumentException 指定された値による演算結果が年の有効範囲外となる場合
	 * 
	 * @since 0.94
	 */
	public int addUnit(int amount) {
		int quart = this.unit - 1 + amount;
		int year  = this.year;
		int y_amount;
		
		if (quart >= 0) {
			y_amount = quart / 4;
		} else {
			y_amount = (quart+1) / 4 - 1;
		}
		
		quart %= 4;
		if (quart < 0) {
			quart += 4;
		}
		quart += 1;
		
		if (y_amount != 0) {
			setYear(y_amount + year);
		}
		return setUnit(quart);
	}
	
	/**
	 * 現在の値を、時間キー文字列として出力する。
	 * 
	 * @return 時間キー文字列
	 */
	public String toString() {
		return (toFormattedYearString() + toFormattedString(this.unit, "'Q'0"));
	}
}
