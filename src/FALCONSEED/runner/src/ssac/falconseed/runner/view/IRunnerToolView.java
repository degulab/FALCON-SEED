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
 * @(#)IRunnerToolView.java	1.22	2012/07/17
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.runner.view;

import ssac.util.swing.menu.IMenuActionHandler;

/**
 * ツリービュー共通のインタフェース
 * 
 * @version 1.22	2012/07/17
 * @since 1.22
 */
public interface IRunnerToolView extends IMenuActionHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------
	
	/**
	 * このビューを格納するエディタフレームを取得する。
	 * @return	このビューを格納するエディタフレームのインスタンス。
	 * 			このビューがフレームに格納されていない場合は <tt>null</tt> を返す。
	 */
	public RunnerFrame getFrame();

	/**
	 * このビューのメインコンポーネントにフォーカスを設定する。
	 */
	public void requestFocusInComponent();

	/**
	 * 選択されているすべてのノードがコピー可能か検証する。
	 * @return	コピー可能であれば <tt>true</tt> を返す。
	 * 			未選択の場合は <tt>false</tt> を返す。
	 */
	public boolean canCopy();

	/**
	 * 選択されているオブジェクトをクリップボードへコピーする
	 */
	public void doCopy();

	/**
	 * 現在クリップボードに存在する内容が選択位置に貼り付け可能か検証する。
	 * @return	貼り付け可能であれば <tt>true</tt> を返す。
	 * 			未選択の場合は <tt>false</tt> を返す。
	 */
	public boolean canPaste();

	/**
	 * クリップボードに存在するデータを、コンポーネントにコピーする
	 */
	public void doPaste();

	/**
	 * 選択されているすべてのオブジェクトが削除可能か検証する。
	 * @return	削除可能であれば <tt>true</tt> を返す。
	 * 			未選択の場合は <tt>false</tt> を返す。
	 */
	public boolean canDelete();

	/**
	 * 選択されているノードが示すファイルを削除する
	 */
	public void doDelete();
}
