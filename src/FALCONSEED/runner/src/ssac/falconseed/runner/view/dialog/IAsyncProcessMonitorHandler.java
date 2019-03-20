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
 *  Copyright 2007-2014  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)IAsyncProcessMonitorHandler.java	3.0.0	2014/03/26
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.runner.view.dialog;


/**
 * プロセス実行モニタによって発生するイベントのハンドラ。
 * 
 * @version 3.0.0	2014/03/26
 * @since 3.0.0
 */
public interface IAsyncProcessMonitorHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * ステータスバーのメッセージ領域に、新しいメッセージを設定する。
	 * <em>message</em> が <tt>null</tt> の場合は、空文字列が設定される。
	 * ステータスバーが存在しない場合、このメソッド呼び出しは無視される。
	 * @param message	メッセージを示す文字列
	 */
	public void setStatusBarMessage(String message);

	/**
	 * モニタが表示されたときに呼び出されるハンドラ。
	 * @param monitor	モニタオブジェクト
	 */
	public void onShownMonitor(AsyncProcessMonitorWindow monitor);

	/**
	 * モニタが非表示にされたときに呼び出されるハンドラ。
	 * @param monitor	モニタオブジェクト
	 */
	public void onHiddenMonitor(AsyncProcessMonitorWindow monitor);

	/**
	 * モニタが破棄された後に呼び出されるハンドラ。
	 * @param monitor	モニタオブジェクト
	 */
	public void onDisposedMonitor(AsyncProcessMonitorWindow monitor);
}
