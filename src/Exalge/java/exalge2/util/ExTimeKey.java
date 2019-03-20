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
 * @(#)ExTimeKey.java	0.94	2008/05/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExTimeKey.java	0.91	2007/08/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */

package exalge2.util;

import java.util.Calendar;

/**
 * 基底の時間キーを操作する機能を提供するユーティリティクラス。
 * <p>
 * 時間キーは、年、年月、四半期を示す文字列で構成される拡張基底キーであり、
 * そのキー文字列と値との相互変換、加減算を行う機能を提供するインタフェースである。
 * <p>
 * 時間キーは、全てのキーに共通の年(値)と、最小単位を示すユニット(値)を保持するものとし、
 * それぞれの値に対しての演算機能を持つ。
 * <br>
 * 年(値)の有効範囲は、{@link #getMinYear()} ～ {@link #getMaxYear()} まで設定可能とする。
 * <br>
 * ユニット(値)の有効範囲は、時間キーのフォーマットの実装により異なが、
 * {@link #getMinUnit()} ～ {@link #getMaxUnit()} まで設定可能とする。
 * <p>
 * <b>(注)</b>Java 標準の日付・時刻クラスとの関連性はない。
 * 
 * @version 0.94	2008/05/16
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 *
 * @since 0.91
 */
public interface ExTimeKey
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

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
	public boolean isSupported(String timeKey);

	/**
	 * 指定された時間キー文字列で、このインスタンスの値を更新する。
	 * 
	 * @param timeKey 時間キー文字列
	 * @return 値が更新された場合に <tt>true</tt> を返す
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException サポートされない形式か、値が有効範囲にない場合
	 */
	public boolean set(String timeKey);

	/**
	 * 指定されたカレンダーの値で、このインスタンスの値を更新する。
	 * 
	 * @param cal	Calendar の値
	 * @return	値が更新された場合に <tt>true</tt> を返す
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException サポートされていない形式か、値が有効範囲にない場合
	 * 
	 * @since 0.94
	 */
	public boolean set(Calendar cal);

	/**
	 * 指定された年の値が、許容範囲内かどうかを検証する。
	 * 
	 * @param year 検証する年の値
	 * @return 許容範囲内であれば <tt>true</tt>
	 */
	public boolean isValidYear(int year);

	/**
	 * 現在の年の値を返す。
	 * 
	 * @return 年
	 */
	public int getYear();

	/**
	 * 年の値として有効な範囲の最小値を返す。
	 * 
	 * @return 年の最小値
	 */
	public int getMinYear();

	/**
	 * 年の値として有効な範囲の最大値を返す。
	 * 
	 * @return 年の最大値
	 */
	public int getMaxYear();

	/**
	 * 年の値を更新する。
	 * 
	 * @param year 設定する年の値
	 * 
	 * @return 設定後の年の値を返す
	 * 
	 * @throws IllegalArgumentException 指定された値が許容範囲にない場合
	 */
	public int setYear(int year);

	/**
	 * 指定された値を、現在の年の値に加算する。
	 * <br>指定された値が負の場合、結果的に減算となる。
	 * <br>
	 * <b>注：</b>
	 * <blockquote>
	 * 加算(減算)結果が有効範囲を超える場合、例外がスローされる。
	 * </blockquote>
	 * 
	 * @param amount 増減値
	 * @return 演算後の年の値を返す
	 * 
	 * @throws IllegalArgumentException 指定された値による演算結果が年の有効範囲外となる場合
	 */
	public int addYear(int amount);

	/**
	 * 指定されたユニットの値が、許容範囲内かどうかを検証する。
	 * 
	 * @param unit 検証するユニットの値
	 * @return 許容範囲内であれば <tt>true</tt>
	 */
	public boolean isValidUnit(int unit);

	/**
	 * 現在のユニットの値を返す。
	 * 
	 * @return ユニット
	 */
	public int getUnit();

	/**
	 * ユニットの値として有効な範囲の最小値を返す。
	 * 
	 * @return ユニットの最小値
	 */
	public int getMinUnit();

	/**
	 * ユニットの値として有効な範囲の最大値を返す。
	 * 
	 * @return ユニットの最大値
	 */
	public int getMaxUnit();

	/**
	 * ユニットの値を更新する。
	 * 
	 * @param unit 設定するユニットの値
	 * 
	 * @return 設定後のユニットの値を返す
	 * 
	 * @throws IllegalArgumentException 指定された値が許容範囲にない場合
	 */
	public int setUnit(int unit);

	/**
	 * 指定された値を、現在のユニットの値に加算する。
	 * <br>指定された値が負の場合、結果的に減算となる。
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
	 */
	public int addUnit(int amount);

	/**
	 * 現在の値を、時間キー文字列として出力する。
	 * 
	 * @return 時間キー文字列
	 */
	public String toString();
}
