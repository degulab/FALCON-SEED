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
 * @(#)IModuleArgTableModel.java	2.0.0	2012/10/18
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)IModuleArgTableModel.java	1.17	2010/11/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module.swing.table;

import javax.swing.table.TableModel;

import ssac.aadl.module.ModuleArgType;

/**
 * 実行時引数を表示もしくは設定するテーブル用モデルの共通インタフェース。
 * 
 * @version 2.0.0	2012/10/18
 * @since 1.17
 */
public interface IModuleArgTableModel extends TableModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------

	/**
	 * 指定された行インデックスに対応する、引数属性を取得する。
	 * @param rowIndex	行インデックス
	 * @return	引数属性
	 * @throws IndexOutOfBoundsException	行インデックスが適切ではない場合
	 * @since 2.0.0
	 */
	public ModuleArgType getArgumentAttr(int rowIndex);

	/**
	 * 標準の空行を終端に追加する。
	 */
	public void addNewRow();
	/**
	 * 指定されたインデックスの行を削除する。
	 * @param row	削除する行のインデックス
	 * @throws ArrayIndexOutOfBoundsException	行が無効だった場合
	 */
	public void removeRow(int row);
	/**
	 * テーブルモデルで、<em>start</em> から <em>end</em> までの 1 行または複数行を、<em>to</em> の位置に移動する。
	 * 移動後は、インデックス <em>start</em> にあった行が、インデックス <em>to</em> に移動する。
	 * このメソッドでは、<code>tableChanged</code> 通知メッセージがすべてのリスナーに送られる。<p>
	 * 
     *  <pre>
     *  移動例:
     *  <p>
     *  1. moveRow(1,3,5);
     *          a|B|C|D|e|f|g|h|i|j|k   - 移動前
     *          a|e|f|g|h|B|C|D|i|j|k   - 移動後
     *  <p>
     *  2. moveRow(6,7,1);
     *          a|b|c|d|e|f|G|H|i|j|k   - 移動前
     *          a|G|H|b|c|d|e|f|i|j|k   - 移動後
     *  <p> 
     *  </pre>
     *  
	 * @param start	移動する行の開始インデックス
	 * @param end	移動する行の終了インデックス
	 * @param to	行の移動先
	 * @throws ArrayIndexOutOfBoundsException	要素のどれかをテーブルの範囲外に移動する場合
	 */
	public void moveRow(int start, int end, int to);
}
