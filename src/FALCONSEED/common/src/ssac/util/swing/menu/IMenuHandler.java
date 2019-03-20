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
 *  Copyright 2007-2009  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)IMenuHandler.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.menu;

import java.awt.event.ActionEvent;

/**
 * メニュー・アクション・ハンドラとなるインタフェース。
 * <p>
 * このインタフェースは、エディタのメインフレームに適用され、
 * メニューアクションを処理するためのフレームワークとなる。
 * 
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public interface IMenuHandler
{
	/**
	 * メニュー項目の選択時に呼び出されるハンドラ・メソッド。
	 * 
	 * @param e		メニュー項目選択時のイベントオブジェクト
	 */
	public void menuActionPerformed(ActionEvent e);
	
	/**
	 * メニュー項目の更新要求時に呼び出されるハンドラ・メソッド。
	 * 
	 * @param command	このイベント要因のコマンド文字列
	 * @param source	このイベント要因のソースオブジェクト。
	 * 					ソースオブジェクトが未定義の場合は <tt>null</tt>。
	 */
	public void menuUpdatePerformed(String command, Object source);
}
