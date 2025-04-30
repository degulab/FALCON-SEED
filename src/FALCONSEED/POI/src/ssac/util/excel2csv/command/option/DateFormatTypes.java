/*
 * @(#)DateFormatTypes.java	3.3.0	2016/05/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.excel2csv.command.option;

/**
 * 変換定義における 'dateformat' オプションの列挙値。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public enum DateFormatTypes
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** default **/
	DEFAULT("default"),
	/** excel **/
	EXCEL("excel");

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
	private DateFormatTypes(String name) {
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
	static public DateFormatTypes fromName(String name) {
		DateFormatTypes retType = null;
		
		if (name == null || name.isEmpty()) {
			retType = null;
		}
		else if (DEFAULT._typeName.equalsIgnoreCase(name)) {
			retType = DEFAULT;
		}
		else if (EXCEL._typeName.equalsIgnoreCase(name)) {
			retType = EXCEL;
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
