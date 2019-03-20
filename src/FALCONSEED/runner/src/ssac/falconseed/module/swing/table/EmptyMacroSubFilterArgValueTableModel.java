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
 * @(#)EmptyMacroSubFilterArgValueTableModel.java	3.1.0	2014/05/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)EmptyMacroSubFilterArgValueTableModel.java	2.0.0	2012/10/12
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing.table;

import ssac.falconseed.module.IModuleArgConfig;

/**
 * マクロフィルタを構成するサブフィルタの引数値設定用テーブルモデルのデータを持たない実装。
 * 
 * @version 3.1.0	2014/05/16
 * @since 2.0.0
 */
public class EmptyMacroSubFilterArgValueTableModel extends AbMacroSubFilterArgValueTableModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public EmptyMacroSubFilterArgValueTableModel() {
		super();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	@Override
	public int getRowCount() {
		return 0;
	}

	@Override
	public IModuleArgConfig getArgument(int rowIndex) {
		return null;
	}

	@Override
	public void setArgumentValue(int rowIndex, Object newValue) {
		// no entry
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
