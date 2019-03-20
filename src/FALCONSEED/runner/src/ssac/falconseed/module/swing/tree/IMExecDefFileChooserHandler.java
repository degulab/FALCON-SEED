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
 * @(#)IMExecDefFileChooserHandler.java	2.0.0	2012/11/01
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing.tree;

import ssac.util.io.VirtualFile;

/**
 * ツリー形式のモジュール実行定義ファイル選択ダイアログから呼び出されるイベントハンドラ。
 * 
 * @version 2.0.0	2012/11/01
 */
public interface IMExecDefFileChooserHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * ファイルが選択され[OK]ボタンが押された直後に呼び出される。
	 * @param source	呼び出し元となるオブジェクト。通常は <code>MExecDefFileChooser</code> オブジェクト。
	 * @param selectionFile		選択されたファイルを示す抽象パス
	 * @return 選択を受け入れる場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean acceptChooseFile(Object source, VirtualFile selectionFile);
}
