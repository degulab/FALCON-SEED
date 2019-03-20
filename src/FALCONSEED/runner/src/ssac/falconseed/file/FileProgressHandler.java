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
 *  Copyright 2007-2012  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)FileProgressHandler.java	1.20	2012/03/08
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.file;

/**
 * 進捗状況通知ハンドラ。
 * このインタフェースは、進捗状況を通知する側から呼び出され、進捗状況の
 * 表示など処理に介入するためのインタフェースを提供する。
 * 
 * @version 1.20	2012/03/08
 * @since 1.20
 */
public interface FileProgressHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 処理の中断が要求された場合に <tt>true</tt> を返す。
	 * このメソッドが <tt>true</tt> を返したとき、このメソッドを呼び出す側は、
	 * 可能な限り迅速に処理を中断する必要がある。
	 */
	public boolean isTerminateRequested();

	/**
	 * 進捗状況を表す数値範囲の最大値を返す。
	 */
	public long getProgressMaximum();

	/**
	 * 現在の進捗状況を表す値を返す。
	 */
	public long getProgressValue();

	/**
	 * 現在の進捗状況をパーセント(0～100)で返す。
	 */
	public double getProgressPercent();

	/**
	 * 進捗状況を表す数値範囲の最大値に、指定された値を加算する。
	 * このインタフェースは、進捗状況を通知する側が、独自の分解能で設定した上限値を
	 * 更新するために呼び出される。
	 * @param value		加算する値
	 * @return	加算後の値
	 */
	public long addProgressMaximum(long value);

	/**
	 * 現在の進捗状況を表す値に、指定された値を加算する。
	 * このインタフェースは、進捗状況を通知する側が、独自の分解能で分割した現在の
	 * 進捗状況を更新するために呼び出される。
	 * @param value		加算する値
	 * @return	加算後の値
	 */
	public long addProgressValue(long value);

	/**
	 * 現在の進捗状況を表す値に、指定された値を設定する。
	 * このインタフェースは、進捗状況を通知する側が、独自の分解能で分割した現在の
	 * 進捗状況を更新するために呼び出される。
	 * @param value	設定する値
	 * @return	設定後の値
	 */
	public long setProgressValue(long value);
}
