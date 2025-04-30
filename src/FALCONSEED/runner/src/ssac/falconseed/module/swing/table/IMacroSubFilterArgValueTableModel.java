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
