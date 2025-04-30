/*
 * @(#)BlankCellTypes.java	3.3.0	2016/04/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.excel2csv.command.option;

/**
 * 変換定義における 'blankcell' オプションの列挙値。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public enum BlankCellTypes
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** BLANK **/
	BLANK("blank");

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final String _typeName;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/**
	 * このインスタンスに設定されている名前を返す。
	 */
	private BlankCellTypes(String name) {
		this._typeName = name;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * このインスタンスに設定されている名前を返す。
	 */
	public String typeName() {
		return _typeName;
	}
	
	/**
	 * 名前に対応する種類を返す。
	 * @param name	判定する名前
	 * @return	対応する種類を返す。
	 * 			<em>name</em> に指定された名前がどの種類にも一致しない場合は <tt>null</tt> を返す。
	 */
	static public BlankCellTypes fromName(String name) {
		BlankCellTypes retType = null;
		
		if (name == null || name.isEmpty()) {
			retType = null;
		}
		else if (BLANK._typeName.equalsIgnoreCase(name)) {
			retType = BLANK;
		}
		else {
			retType = null;
		}
		
		return retType;
	}

	@Override
	public String toString() {
		return typeName();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
