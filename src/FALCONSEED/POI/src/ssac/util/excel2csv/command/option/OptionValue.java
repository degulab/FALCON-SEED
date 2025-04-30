/*
 * @(#)OptionValue.java	3.3.0	2016/04/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.excel2csv.command.option;

import ssac.util.excel2csv.poi.CellPosition;

/**
 * <code>[Excel to CSV]</code> 変換定義におけるオプション値を保持するクラス。
 * このオブジェクトは不変オブジェクトとする。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public class OptionValue
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** オプションの記述されたセル位置 **/
	private final CellPosition		_cellpos;
	/** オプション名 **/
	private final String			_name;
	/** オプション値の種類 **/
	private final OptionValueTypes	_type;
	/** オプション値 **/
	private final Object			_value;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定されたパラメータで、新しいインスタンスを生成する。
	 * @param cellpos	オプションの記述されたセル位置
	 * @param name		オプション名
	 * @param type		オプション値の種類
	 * @param value		オプション値
	 * @throws NullPointerException	<em>cellpos</em> または <em>name</em> が <tt>null</tt> の場合
	 */
	public OptionValue(CellPosition cellpos, String name, OptionValueTypes type, Object value) {
		if (cellpos == null || name == null || type == null)
			throw new NullPointerException();
		_cellpos = cellpos;
		_name    = name;
		_type    = type;
		_value   = value;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public CellPosition getCellPosition() {
		return _cellpos;
	}
	
	public String getName() {
		return _name;
	}
	
	public OptionValueTypes getValueType() {
		return _type;
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
