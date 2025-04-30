/*
 * @(#)DTCContentEditTableModelConversionError.java	1.1.0	2023/01/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DTCContentEditTableModelConversionError.java	1.0.0	2022/12/21
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.content.common.table;

import dtalge.container.editor.content.common.tree.IDtContainerContentTreeNode;

/**
 * データコンテナ要素編集テーブルのデータモデルをデータコンテナ要素の値に変換する際に、
 * 不正な値が含まれている場合にスローされる例外オブジェクト。
 * 
 * @version 1.1.0
 */
public class DTCContentEditTableModelConversionError extends Exception
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** 不正な値が含まれているデータコンテナ要素の位置を示すツリーノード **/
	private final IDtContainerContentTreeNode			_atErrorNode;
	/** 不正な値が含まれているテーブルモデルの、エラー情報を含むセル値 **/
	private final DTCContentInvalidCellValue	_invalidCellValue;
	/** エラー情報を含むセル値の位置を示す、テーブルモデルの行インデックス **/
	private final int	_atTableModelRowIndex;
	/** エラー情報を含むセル値の位置を示す、テーブルモデルの列インデックス **/
	private final int	_atTableModelColumnIndex;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/**
	 * 指定されたパラメーターを保持する、新しいインスタンスを生成する。
	 * @param message			例外のメッセージ
	 * @param errTreeNode		不正な値が含まれているデータコンテナ要素の位置を示すツリーノード
	 * @param cellInvalidValue	不正な値が含まれているテーブルモデルの、エラー情報を含むセル値
	 * @param modelRowIndex		エラー情報を含むセル値の位置を示す、テーブルモデルの行インデックス
	 * @param modelColumnIndex	エラー情報を含むセル値の位置を示す、テーブルモデルの列インデックス
	 */
	public DTCContentEditTableModelConversionError(String message, IDtContainerContentTreeNode errTreeNode, DTCContentInvalidCellValue cellInvalidValue, int modelRowIndex, int modelColumnIndex)
	{
		super(message);
		_atErrorNode = errTreeNode;
		_invalidCellValue = cellInvalidValue;
		_atTableModelRowIndex = modelRowIndex;
		_atTableModelColumnIndex = modelColumnIndex;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public IDtContainerContentTreeNode getErrorTreeNode() {
		return _atErrorNode;
	}
	
	public DTCContentInvalidCellValue getInvalidCellValue() {
		return _invalidCellValue;
	}
	
	public int getRowIndexInTableModel() {
		return _atTableModelRowIndex;
	}
	
	public int getColumnIndexInTableModel() {
		return _atTableModelColumnIndex;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
