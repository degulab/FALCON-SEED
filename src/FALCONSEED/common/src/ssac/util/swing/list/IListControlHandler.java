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
package ssac.util.swing.list;

import java.awt.event.ActionEvent;

/**
 * <code>{@link ListController}</code> クラスに登録されたボタンの通知を受け取る
 * インタフェース。
 * 
 * @version 2.0.0 2012/10/19
 */
public interface IListControlHandler {
	/**
	 * [追加]ボタン押下時に呼び出されるメソッド
	 */
	public void onButtonAdd(ActionEvent ae);
	/**
	 * [編集]ボタン押下時に呼び出されるメソッド
	 */
	public void onButtonEdit(ActionEvent ae);
	/**
	 * [削除]ボタン押下時に呼び出されるメソッド
	 */
	public void onButtonDelete(ActionEvent ae);
	/**
	 * [クリア]ボタン押下時に呼び出されるメソッド
	 */
	public void onButtonClear(ActionEvent ae);
	/**
	 * [上へ移動]ボタン押下時に呼び出されるメソッド
	 */
	public void onButtonUp(ActionEvent ae);
	/**
	 * [下へ移動]ボタン押下時に呼び出されるメソッド
	 */
	public void onButtonDown(ActionEvent ae);
}
