/*
 * @(#)ModuleFileType.java	3.2.0	2015/06/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ModuleFileType.java	1.17	2010/11/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module;

import ssac.util.Strings;

/**
 * 実行モジュールファイルの種類。
 * 
 * @version 3.2.0
 * @since 1.17
 */
public enum ModuleFileType
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** AADLコンパイラーにより生成された jar ファイルであることを示す **/
	AADL_JAR("aadl.jar"),
	/** AADLマクロ定義ファイルであることを示す **/
	AADL_MACRO("aadl.macro"),
	/** 汎用フィルタ定義ファイルであることを示す **/
	GENERIC_FILTER("aadl.generic.filter"),
	/** 不明な種類であることを示す **/
	UNKNOWN("");

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final String _typeName;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private ModuleFileType(String name) {
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
	 * 			<em>name</em> が <tt>null</tt> もしくは空文字列の場合は {@link #UNKNOWN} を返す。
	 * 			<em>name</em> に指定された名前がどの種類にも一致しない場合は <tt>null</tt> を返す。
	 */
	static public ModuleFileType fromName(String name) {
		ModuleFileType retType = null;
		
		if (Strings.isNullOrEmpty(name)) {
			// null や空文字の場合は UNKNOWN とする
			retType = UNKNOWN;
		}
		else if (AADL_JAR._typeName.equalsIgnoreCase(name)) {
			retType = AADL_JAR;
		}
		else if (AADL_MACRO._typeName.equalsIgnoreCase(name)) {
			retType = AADL_MACRO;
		}
		else if (GENERIC_FILTER._typeName.equalsIgnoreCase(name)) {
			retType = GENERIC_FILTER;
		}
		// 対応しない名前は null
		
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
