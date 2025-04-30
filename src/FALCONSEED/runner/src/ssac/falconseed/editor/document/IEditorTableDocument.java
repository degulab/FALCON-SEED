/*
 * @(#)IEditorTableDocument.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.editor.document;

import javax.swing.event.UndoableEditListener;
import javax.swing.table.TableModel;

/**
 * エディタで利用可能なテーブルエディタの共通ドキュメントインタフェース。
 * 
 * @version 1.00	2010/12/20
 */
public interface IEditorTableDocument extends TableModel, IEditorDocument
{
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
