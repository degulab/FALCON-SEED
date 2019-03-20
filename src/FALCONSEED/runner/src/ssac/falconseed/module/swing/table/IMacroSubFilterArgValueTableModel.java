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
 * @(#)IMacroFilterArgValueTableModel.java	3.1.0	2014/05/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)IMacroFilterArgValueTableModel.java	2.0.0	2012/10/18
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing.table;

import ssac.falconseed.module.IModuleArgConfig;

/**
 * マクロフィルタを構成するサブフィルタの引数値設定用テーブルのデータモデル・インタフェース。
 * 
 * @version 3.1.0	2014/05/16
 * @since 2.0.0
 */
public interface IMacroSubFilterArgValueTableModel extends IMExecDefArgTableModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------

	/**
	 * 指定された行インデックスに対応する、引数データオブジェクトを取得する。
	 * @param rowIndex	行インデックス
	 * @return	引数データオブジェクト
	 * @throws IndexOutOfBoundsException	行インデックスが適切ではない場合
	 */
	public IModuleArgConfig getArgument(int rowIndex);
}
