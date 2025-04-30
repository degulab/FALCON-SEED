/*
 * @(#)RepeatTypes.java	3.3.0	2016/04/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.excel2csv.command.option;

/**
 * 変換定義における 'repeat' オプションの列挙値。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public enum RepeatTypes
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** none **/
	NONE("none"),
	/** vertical **/
	VERT("vert"),
	/** horizontal **/
	HORZ("horz"),
	/** both **/
	BOTH("both");

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
	private RepeatTypes(String name) {
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
	static public RepeatTypes fromName(String name) {
		RepeatTypes retType = null;
		
		if (name == null || name.isEmpty()) {
			retType = null;
		}
		else if (NONE._typeName.equalsIgnoreCase(name)) {
			retType = NONE;
		}
		else if (VERT._typeName.equalsIgnoreCase(name)) {
			retType = VERT;
		}
		else if (HORZ._typeName.equalsIgnoreCase(name)) {
			retType = HORZ;
		}
		else if (BOTH._typeName.equalsIgnoreCase(name)) {
			retType = BOTH;
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
