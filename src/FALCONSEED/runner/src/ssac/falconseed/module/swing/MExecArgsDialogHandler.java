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
 * @(#)MExecArgsDialogHandler.java	1.22	2012/08/21
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecArgsDialogHandler.java	1.20	2012/03/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

import ssac.util.io.VirtualFile;

/**
 * モジュール実行時引数ダイアログ内で発生するイベントに対するイベントハンドラ。
 * 
 * @version 1.22	2012/08/21
 * @since 1.20
 */
public interface MExecArgsDialogHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * ダイアログからエディタでのファイル表示リクエストが発生したときに呼び出されるハンドラ。
	 * @param dlg	ダイアログオブジェクト
	 * @param file	表示対象のファイルを示す抽象パス
	 * @return	ファイルが表示された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean doOpenFileOnEditor(IMExecArgsDialog dlg, VirtualFile file);
	
	/**
	 * ダイアログからエディタでのCSVファイル表示リクエストが発生したときに呼び出されるハンドラ。
	 * @param dlg	ダイアログオブジェクト
	 * @param file	表示対象のファイルを示す抽象パス
	 * @return	ファイルが表示された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean doOpenFileByCsvOnEditor(IMExecArgsDialog dlg, VirtualFile file);

	/**
	 * ダイアログからエディタのファイルを閉じるリクエストが発生したときに呼び出されるハンドラ。
	 * @param dlg	ダイアログオブジェクト
	 * @param file	表示対象のファイルを示す抽象パス
	 * @return	ファイルが閉じられた場合は <tt>true</tt>、開かれていて閉じることができない場合は <tt>false</tt>
	 */
	public boolean doCloseFileOnEditor(IMExecArgsDialog dlg, VirtualFile file);

	/**
	 * ダイアログが表示されたときに呼び出されるハンドラ。
	 * @param dlg	ダイアログオブジェクト
	 */
	public void onShownDialog(IMExecArgsDialog dlg);

	/**
	 * ダイアログが非表示にされたときに呼び出されるハンドラ。
	 * @param dlg	ダイアログオブジェクト
	 */
	public void onHiddenDialog(IMExecArgsDialog dlg);

	/**
	 * ダイアログを閉じる前に呼び出されるハンドラ。
	 * ダイアログを閉じて良いかを問い合わせる。
	 * @param dlg	ダイアログオブジェクト
	 * @return	ダイアログを閉じてよい場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean canCloseDialog(IMExecArgsDialog dlg);

	/**
	 * ダイアログが閉じられた後に呼び出されるハンドラ。
	 * @param dlg	ダイアログオブジェクト
	 */
	public void onClosedDialog(IMExecArgsDialog dlg);
}
