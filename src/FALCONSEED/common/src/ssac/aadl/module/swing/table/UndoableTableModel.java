/*
 * @(#)DtContainerContentDtalgeEditTablePane.java	1.0.0	2022/12/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module.swing.table;

import javax.swing.table.TableModel;

import ssac.aadl.module.swing.UndoableEditModel;

/**
 * アンドゥリスナーを登録／削除するインターフェースが組み込まれたテーブルモデル。
 * 
 * @version 5.0.0
 * @since 5.0.0
 */
public interface UndoableTableModel extends TableModel, UndoableEditModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------
}
