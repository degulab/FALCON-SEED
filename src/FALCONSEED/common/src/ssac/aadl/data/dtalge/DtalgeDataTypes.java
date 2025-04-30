/*
 * @(#)DtalgeDataTypes.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.data.dtalge;


/**
 * データ代数のデータ型
 * 
 * @version 1.00	2010/12/20
 */
public enum DtalgeDataTypes
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** 文字列型 **/
	STRING("string"),
	/** 数値型 **/
	DECIMAL("decimal"),
	/** 真偽値型 **/
	BOOLEAN("boolean");

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final String _typeName;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private DtalgeDataTypes(String name) {
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
	 * 			<em>name</em> が <tt>null</tt> もしくは空文字列の場合は {@link #NONE} を返す。
	 * 			<em>name</em> に指定された名前がどの種類にも一致しない場合は <tt>null</tt> を返す。
	 */
	static public DtalgeDataTypes fromName(String name) {
		DtalgeDataTypes retType = null;
		
		if (name != null && name.length() > 0) {
			if (STRING.typeName().equalsIgnoreCase(name)) {
				retType = STRING;
			}
			else if (DECIMAL.typeName().equalsIgnoreCase(name)) {
				retType = DECIMAL;
			}
			else if (BOOLEAN.typeName().equalsIgnoreCase(name)) {
				retType = BOOLEAN;
			}
		}
		// 対応しない名前の場合は null を返す

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
