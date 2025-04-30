/*
 * @(#)DtContainerContentDtalgeEditTablePane.java	1.0.0	2022/12/17
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module.swing;

import javax.swing.event.UndoableEditListener;

/**
 * アンドゥリスナーを登録／削除するインターフェース。
 * 
 * @version 5.0.0
 * @since 5.0.0
 */
public interface UndoableEditModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------
	
	/**
	 * 任意の変更を通知するアンドゥリスナーを追加する。
	 * <code>UndoableEdit</code> で実行される「元に戻す/再実行」操作は、
	 * 適切な <code>DocumentEvent</code> を発生させて、ビューをモデルと
	 * 同期させる。
	 * 
	 * @param listener	追加する <code>UndoableEditListener</code>
	 */
	public void addUndoableEditListener(UndoableEditListener listener);

	/**
	 * アンドゥリスナーを削除する。
	 * 
	 * @param listener	削除する <code>UndoableEditListener</code>
	 */
	public void removeUndoableEditListener(UndoableEditListener listener);
}
