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
 * @(#)IMExecArgConfigHandler.java	1.22	2012/08/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

import java.awt.Component;

import ssac.falconseed.module.ModuleArgID;
import ssac.util.io.VirtualFile;

/**
 * モジュール実行時引数の１引数設定用ハンドラ・インタフェース。
 * 
 * @version 1.22	2012/08/19
 * @since 1.22
 */
public interface IMExecArgConfigHandler
{
	/**
	 * コンポーネントが編集可能か判定する。
	 * @return	編集可能なら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean isEditable();

	/**
	 * 引数番号を表示・非表示の設定を取得する。
	 * @return	引数番号を表示する場合は <tt>true</tt>、非表示の場合は <tt>false</tt>
	 */
	public boolean isVisibleArgNo();

	/**
	 * 指定された引数IDから、表示名を取得する。
	 * @param argid	引数ID
	 * @return	表示名を示す文字列
	 */
	public String getDisplayModuleArgID(ModuleArgID argid);

	/**
	 * このコンポーネントに設定された、エディタビューで表示されているファイルの抽象パスを返す。
	 * 設定されていない場合は <tt>null</tt> を返す。
	 */
	public VirtualFile getViewingFileOnEditor();

	/**
	 * パス表示時の基準パスを取得する。
	 * @return	基準パスを返す。設定されていない場合は <tt>null</tt> を返す。
	 */
	public VirtualFile getBasePath();

	/**
	 * 指定された子コンポーネントが表示されるように、スクロールする。
	 * @param component	対象の子コンポーネント
	 */
	public void scrollClientComponentToVisible(Component component);
}
