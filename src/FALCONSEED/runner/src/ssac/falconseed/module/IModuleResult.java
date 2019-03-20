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
 * @(#)IModuleResult.java	1.22	2012/08/10
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module;

/**
 * モジュール情報とモジュール実行結果のインタフェース。
 * 
 * @version 1.22	2012/08/10
 * @since 1.22
 */
public interface IModuleResult<T extends IModuleArgConfig> extends IModuleConfig<T>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------

	/**
	 * モジュール実行が開始された日時(エポック)を取得する。
	 * @return	モジュール実行開始時のエポック、未実行の場合は 0
	 */
	public long getStartTime();

	/**
	 * モジュール実行開始時の日時をエポックで設定する。
	 * @param time	モジュール実行開始時の日時を示すエポック
	 */
	public void setStartTime(long time);

	/**
	 * モジュール実行時間を取得する。
	 * @return	モジュール実行時間を示すミリ秒の値
	 */
	public long getProcessTime();

	/**
	 * モジュール実行時間を設定する。
	 * @param time	モジュール実行時間をミリ秒で指定する
	 */
	public void setProcessTime(long time);

	/**
	 * モジュールの終了コードを取得する。
	 * @return	終了コード
	 */
	public int getExitCode();

	/**
	 * モジュールの終了コードを設定する。
	 * @param code	終了コード
	 */
	public void setExitCode(int code);

	/**
	 * モジュール実行がユーザーによって停止されたかどうかを判定する。
	 * @return	ユーザーによって停止された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isUserCanceled();

	/**
	 * モジュール実行がユーザーによって停止されたかどうかを示す状態を設定する。
	 * @param flag	ユーザーによって停止されたとする場合は <tt>true</tt>、それ以外の場合は <tt>false</tt> を指定する
	 */
	public void setUserCanceled(boolean flag);

	/**
	 * モジュール実行が成功したかどうかを取得する。
	 * @return	モジュール実行が正常に完了した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isSucceeded();
}
