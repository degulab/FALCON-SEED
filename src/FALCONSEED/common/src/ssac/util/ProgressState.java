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
 * @(#)ProgressState.java	2.1.0	2013/07/08
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ProgressState.java	1.16	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util;

/**
 * 処理の進捗状況を保持する機能のインタフェース。
 * 
 * @version 2.1.0	2013/07/08
 *
 * @since 1.16
 */
public interface ProgressState
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * キャンセル操作を許可する場合は <tt>true</tt> を返す。
	 * @since 2.1.0
	 */
	public boolean getCancelAllowed();
	/**
	 * キャンセル操作の許可／禁止を設定する。
	 * @param allow		キャンセルを許可する場合は <tt>true</tt>、そうでない場合は <tt>false</tt> を指定する。
	 * @since 2.1.0
	 */
	public void setCancelAllowed(boolean allow);
	/**
	 * キャンセルが要求されている状態かを検証する。
	 * @return	キャンセルが要求されていれば <tt>true</tt>、そうでない場合は <tt>false</tt> を返す。
	 */
	public boolean isCanceled();
	/**
	 * 処理のキャンセルを要求する。
	 * このメソッドを呼び出した後は、{@link #isCanceled()} メソッドは <tt>true</tt> を返す。
	 * @throws IllegalStateException	キャンセルが許可されていない場合
	 */
	public void requestCancel();

	/**
	 * 処理中の説明を取得する。
	 * @return	処理中の説明を表す文字列
	 */
	public String getDescription();
	/**
	 * 処理中の説明を設定する。
	 * @param message	処理中の説明を表す文字列
	 */
	public void setDescription(String desc);

	/**
	 * 進捗状況を表す区間の最小値を返す。
	 * @return	区間の最小値
	 */
	public long getMinimum();
	/**
	 * 進捗状況を表す区間の最大値を返す。
	 * @return	区間の最大値
	 */
	public long getMaximum();
	/**
	 * 進捗状況を表す区間の最小値を設定する。
	 * @param min	区間の最小値
	 */
	public void setMinimum(long min);
	/**
	 * 進捗状況を表す区間の最大値を設定する。
	 * @param max	区間の最大値
	 */
	public void setMaximum(long max);
	/**
	 * 進捗状況を表す区間を設定する。
	 * @param min	区間の最小値
	 * @param max	区間の最大値
	 */
	public void setRange(long min, long max);

	/**
	 * 現在の進捗状況となる区間内の位置を返す。
	 * @return	区間内の位置
	 */
	public long getPosition();
	/**
	 * 進捗状況となる区間内の位置を設定する。
	 * @param pos	区間内の位置
	 */
	public void setPosition(long pos);
	/**
	 * 進捗状況となる区間内の位置に 1 を加算する。
	 * この操作では、区間の位置は区間最大値にクリップされる。
	 * @return	変更後の区間内の位置
	 * @since 2.1.0
	 */
	public long incrementPosition();
	/**
	 * 進捗状況となる区間内の位置から 1 を減算する。
	 * この操作では、区間の位置は区間最小値にクリップされる。
	 * @return	変更後の区間内の位置
	 * @since 2.1.0
	 */
	public long decrementPosition();
	/**
	 * 進捗状況となる区間内の位置に、指定された値を加算する。
	 * 負の値を指定した場合は減算となる。
	 * 加算の場合、区間の位置は区間最大値にクリップされる。
	 * 減算の場合、区間の位置は区間最小値にクリップされる。
	 * @param addition	加算する正もしくは負の値
	 * @return	変更後の区間内の位置
	 * @since 2.1.0
	 */
	public long addPosition(long addition);

	/**
	 * 現在の進捗率を返す。
	 * @return	現在の進捗率を 0.0～1.0 の範囲の値で返す。
	 */
	public double percent();
}
