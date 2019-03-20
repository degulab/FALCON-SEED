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
 * @(#)IMacroFilterArgValueTableModel.java	2.0.0	2012/10/18
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing.table;

import ssac.aadl.module.ModuleArgType;
import ssac.aadl.module.swing.table.IModuleArgTableModel;

/**
 * モジュール実行定義の引数定義用テーブルのデータモデル・インタフェース。
 * 
 * @version 2.0.0	2012/10/18
 * @since 2.0.0
 */
public interface IMExecDefArgTableModel extends IModuleArgTableModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------

	/**
	 * 引数属性列のインデックスであれば <tt>true</tt> を返す。
	 */
	public boolean isAttributeColumnIndex(int columnIndex);

	/**
	 * 引数説明列のインデックスであれば <tt>true</tt> を返す。
	 */
	public boolean isDescriptionColumnIndex(int columnIndex);

	/**
	 * 引数値列のインデックスであれば <tt>true</tt> を返す。
	 */
	public boolean isValueColumnIndex(int columnIndex);

	/**
	 * 引数属性の列インデックスを返す。
	 */
	public int attributeColumnIndex();

	/**
	 * 引数説明の列インデックスを返す。
	 */
	public int descriptionColumnIndex();

	/**
	 * 引数値の列インデックスを返す。
	 */
	public int valueColumnIndex();

	/**
	 * 指定された行インデックスに対応する、引数属性を取得する。
	 * @param rowIndex	行インデックス
	 * @return	引数属性
	 * @throws IndexOutOfBoundsException	行インデックスが適切ではない場合
	 */
	public ModuleArgType getArgumentAttr(int rowIndex);

	/**
	 * 指定された行インデックスに対応する、引数説明を取得する。
	 * @param rowIndex	行インデックス
	 * @return	引数説明
	 * @throws IndexOutOfBoundsException	行インデックスが適切ではない場合
	 */
	public String getArgumentDescription(int rowIndex);

	/**
	 * 指定された行インデックスに対応する、引数値を取得する。
	 * @param rowIndex	行インデックス
	 * @return	引数値
	 * @throws IndexOutOfBoundsException	行インデックスが適切ではない場合
	 */
	public Object getArgumentValue(int rowIndex);

	/**
	 * 指定された行インデックスに対応する引数に、属性を設定する。
	 * @param rowIndex	行インデックス
	 * @param newAttr	新しい属性
	 * @throws IndexOutOfBoundsException	行インデックスが適切ではない場合
	 */
	public void setArgumentAttr(int rowIndex, ModuleArgType newAttr);

	/**
	 * 指定された行インデックスに対応する引数に、説明を設定する。
	 * @param rowIndex	行インデックス
	 * @param newDesc	新しい説明
	 * @throws IndexOutOfBoundsException	行インデックスが適切ではない場合
	 */
	public void setArgumentDescription(int rowIndex, String newDesc);

	/**
	 * 指定された行インデックスに対応する引数に、値を設定する。
	 * @param rowIndex	行インデックス
	 * @param newValue	新しい値
	 * @throws IndexOutOfBoundsException	行インデックスが適切ではない場合
	 */
	public void setArgumentValue(int rowIndex, Object newValue);
	
	/**
	 * 指定された値を持つ行を、最終行に追加する。
	 * @param attr		新しい属性
	 * @param desc		新しい説明
	 * @param value		新しい値
	 */
	public void addRow(ModuleArgType attr, String desc, Object value);

	/**
	 * 指定された値を持つ行を、<em>rowIndex</em> の位置に挿入する。
	 * <em>rowIndex</em> が挿入前の行数と同じ場合は、最終行に追加する。
	 * @param rowIndex	挿入位置の行インデックス
	 * @param attr		新しい属性
	 * @param desc		新しい説明
	 * @param value		新しい値
	 * @throws IndexOutOfBoundsException	行インデックスが 0 未満、もしくは挿入前の行数よりも大きい場合
	 */
	public void insertRow(int rowIndex, ModuleArgType attr, String desc, Object value);
}
