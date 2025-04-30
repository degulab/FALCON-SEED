/*
 * @(#)CmdParamValue.java	3.3.0	2016/05/06
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.excel2csv.command;

import ssac.util.excel2csv.poi.CellPosition;

/**
 * <code>[Excel to CSV]</code> 変換定義におけるコマンドパラメータ値を保持するクラス。
 * このオブジェクトは不変オブジェクトとする。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public class CmdParamValue
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 記述されたセル位置 **/
	private final CellPosition		_cellpos;
	/** 値 **/
	private final Object			_value;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定されたパラメータで、新しいインスタンスを生成する。
	 * @param cellpos	記述されたセル位置
	 * @param value		値
	 * @throws NullPointerException	<em>cellpos</em> が <tt>null</tt> の場合
	 */
	public CmdParamValue(CellPosition cellpos, Object value) {
		if (cellpos == null)
			throw new NullPointerException();
		_cellpos = cellpos;
		_value   = value;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public CellPosition getCellPosition() {
		return _cellpos;
	}
	
	public Object getValue() {
		return _value;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
