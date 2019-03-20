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
 * @(#)IFilterDialogHandler.java	2.0.0	2012/10/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

import ssac.falconseed.module.swing.tree.MEDLocalFileTreePanel;
import ssac.util.io.VirtualFile;

/**
 * モードレスのフィルタダイアログ内で発生するイベントに対するイベントハンドラ。
 * 
 * @version 2.0.0	2012/10/29
 * @since 2.0.0
 */
public interface IFilterDialogHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このハンドラが設定されたコンポーネント内で、ツリーの選択状態が変更された
	 * ときに呼び出される。
	 */
	public void onFileTreeSelectionChanged(MEDLocalFileTreePanel treePanel);

	/**
	 * ステータスバーのメッセージ領域に設定されたメッセージを返す。
	 * このメソッドは <tt>null</tt> を返さない。
	 * ステータスバーが存在しない場合も、空文字列を返す。
	 * @return	メッセージを示す文字列
	 */
	public String getStatusBarMessage();

	/**
	 * ステータスバーのメッセージ領域に、新しいメッセージを設定する。
	 * <em>message</em> が <tt>null</tt> の場合は、空文字列が設定される。
	 * ステータスバーが存在しない場合、このメソッド呼び出しは無視される。
	 * @param message	メッセージを示す文字列
	 */
	public void setStatusBarMessage(String message);

	/**
	 * ダイアログからエディタでのファイル表示リクエストが発生したときに呼び出されるハンドラ。
	 * @param dlg	ダイアログオブジェクト
	 * @param file	表示対象のファイルを示す抽象パス
	 * @return	ファイルが表示された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean doOpenFileOnEditor(IFilterDialog dlg, VirtualFile file);
	
	/**
	 * ダイアログからエディタでのCSVファイル表示リクエストが発生したときに呼び出されるハンドラ。
	 * @param dlg	ダイアログオブジェクト
	 * @param file	表示対象のファイルを示す抽象パス
	 * @return	ファイルが表示された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean doOpenFileByCsvOnEditor(IFilterDialog dlg, VirtualFile file);

	/**
	 * ダイアログからエディタのファイルを閉じるリクエストが発生したときに呼び出されるハンドラ。
	 * @param dlg	ダイアログオブジェクト
	 * @param file	表示対象のファイルを示す抽象パス
	 * @return	ファイルが閉じられた場合は <tt>true</tt>、開かれていて閉じることができない場合は <tt>false</tt>
	 */
	public boolean doCloseFileOnEditor(IFilterDialog dlg, VirtualFile file);

	/**
	 * ダイアログが表示されたときに呼び出されるハンドラ。
	 * @param dlg	ダイアログオブジェクト
	 */
	public void onShownDialog(IFilterDialog dlg);

	/**
	 * ダイアログが非表示にされたときに呼び出されるハンドラ。
	 * @param dlg	ダイアログオブジェクト
	 */
	public void onHiddenDialog(IFilterDialog dlg);

	/**
	 * ダイアログを閉じる前に呼び出されるハンドラ。
	 * ダイアログを閉じて良いかを問い合わせる。
	 * @param dlg	ダイアログオブジェクト
	 * @return	ダイアログを閉じてよい場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean canCloseDialog(IFilterDialog dlg);

	/**
	 * ダイアログが閉じられた後に呼び出されるハンドラ。
	 * @param dlg	ダイアログオブジェクト
	 */
	public void onClosedDialog(IFilterDialog dlg);
}
