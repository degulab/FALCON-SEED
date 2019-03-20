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
 * @(#)EmptyMExecArgsDialogHandler.java	1.22	2012/08/21
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

import ssac.util.io.VirtualFile;

/**
 * 何も処理を行わない、モジュール実行時引数ダイアログ用ハンドラ。
 * @version 1.22	2012/08/21
 * @since 1.22
 */
public class EmptyMExecArgsDialogHandler implements MExecArgsDialogHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final EmptyMExecArgsDialogHandler _instance = new EmptyMExecArgsDialogHandler();

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public final EmptyMExecArgsDialogHandler getInstance() {
		return _instance;
	}
	
	public boolean doCloseFileOnEditor(IMExecArgsDialog dlg, VirtualFile file) {
		// 常に <tt>false</tt> を返す。
		return false;
	}

	public boolean doOpenFileOnEditor(IMExecArgsDialog dlg, VirtualFile file) {
		// 常に <tt>false</tt> を返す。
		return false;
	}
	
	public boolean doOpenFileByCsvOnEditor(IMExecArgsDialog dlg, VirtualFile file) {
		// 常に <tt>false</tt> を返す。
		return false;
	}

	public void onClosedDialog(IMExecArgsDialog dlg) {}

	public void onHiddenDialog(IMExecArgsDialog dlg) {}
	
	public boolean canCloseDialog(IMExecArgsDialog dlg) {
		// 常に <tt>true</tt> を返す。
		return true;
	}

	public void onShownDialog(IMExecArgsDialog dlg) {}
}
