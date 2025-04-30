/*
 * @(#)PrecisionTypes.java	3.3.0	2016/04/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.excel2csv.command.option;

/**
 * 変換定義における 'precision' オプションの列挙値。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public enum PrecisionTypes
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** auto **/
	AUTO("blank");

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
	private PrecisionTypes(String name) {
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
	static public PrecisionTypes fromName(String name) {
		PrecisionTypes retType = null;
		
		if (name == null || name.isEmpty()) {
			retType = null;
		}
		else if (AUTO._typeName.equalsIgnoreCase(name)) {
			retType = AUTO;
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
