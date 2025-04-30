/*
 * @(#)IModuleArgConfigTableModel.java	3.2.0	2015/06/14
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)IModuleArgConfigTableModel.java	3.1.0	2014/05/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)IModuleArgConfigTableModel.java	2.0.0	2012/10/18
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing.table;

import ssac.falconseed.module.IModuleArgConfig;

/**
 * <code>IModuleArgConfig</code> オブジェクトを要素とする、
 * 実行時引数値編集データモデル。
 * 
 * @version 3.2.0
 * @since 2.0.0
 */
public interface IModuleArgConfigTableModel extends IMExecDefArgTableModel
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
	
    public void fireTableRowsInserted(int firstRow, int lastRow);

    public void fireTableRowsUpdated(int firstRow, int lastRow);

    public void fireTableRowsDeleted(int firstRow, int lastRow);
	
	public void fireArgumentAttrUpdated(int rowIndex);
	
	public void fireArgumentDescriptionUpdated(int rowIndex);
	
	public void fireArgumentValueUpdated(int rowIndex);
}
