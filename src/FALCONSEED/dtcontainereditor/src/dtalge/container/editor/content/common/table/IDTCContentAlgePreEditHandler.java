/*
 * @(#)IDTCContentAlgePreEditHandler.java	1.1.0	2023/01/26
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.content.common.table;

/**
 * 交換代数元やデータ代数元の編集用データモデルに対する、値変更前にアクションを実行するイベントハンドラー。
 * 主に、値が変更される直前にアクションを実行するために利用する。
 * 
 * @version 1.1.0
 * @since 1.1.0
 */
public interface IDTCContentAlgePreEditHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * セルの値が変更される直前に呼び出される、イベントハンドラー。
	 * @param curValue	現在の値
	 * @param newValue	新しい値
	 * @param rowIndex	変更するセルの行インデックス
	 * @param columnIndex	変更するセルの列インデックス
	 */
	public void preModifyingCellValue(Object curValue, Object newValue, int rowIndex, int columnIndex);
	
	/**
	 * 行が追加される直前に呼び出される、イベントハンドラー。
	 * @param firstRowIndex	追加後の行範囲の先頭行インデックス
	 * @param lastRowIndex	追加後の行範囲の最終行インデックス、この行を含む
	 */
	public void preInsertingRows(int firstRowIndex, int lastRowIndex);
	
	/**
	 * 行が削除される直前に呼び出される、イベントハンドラー
	 * @param firstRowIndex	削除する行範囲の先頭行インデックス
	 * @param lastRowIndex	削除する行範囲の最終行インデックス、この行も含む
	 */
	public void preDeletingRows(int firstRowIndex, int lastRowIndex);
}
