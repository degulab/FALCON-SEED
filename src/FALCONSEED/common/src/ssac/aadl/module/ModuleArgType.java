/*
 * @(#)PackageSettings.java	3.1.0	2014/05/12
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)PackageSettings.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module;

import ssac.util.Strings;


/**
 * モジュール引数の型
 * 
 * @version 3.1.0	2014/05/12
 * @since 1.14
 */
public enum ModuleArgType
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** 入力ファイルのパスとなる引数であることを示す **/
	IN("[IN]"),
	/** 出力ファイルのパスとなる引数であることを示す **/
	OUT("[OUT]"),
	/** 入力文字列となる引数であることを示す **/
	STR("[STR]"),
	/** Publish 属性の引数であることを示す **/
	PUB("[PUB]"),
	/** Subscribe 属性の引数であることを示す **/
	SUB("[SUB]"),
	/** 型が指定されていないことを示す **/
	NONE("");

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
	private ModuleArgType(String name) {
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
	static public ModuleArgType fromName(String name) {
		ModuleArgType retType = null;

		if (Strings.isNullOrEmpty(name)) {
			// null や空文字の場合は NONE を返す
			retType = NONE;
		}
		else if (IN._typeName.equalsIgnoreCase(name)) {
			retType = IN;
		}
		else if (OUT._typeName.equalsIgnoreCase(name)) {
			retType = OUT;
		}
		else if (STR._typeName.equalsIgnoreCase(name)) {
			retType = STR;
		}
		else if (PUB._typeName.equalsIgnoreCase(name)) {
			retType = PUB;
		}
		else if (SUB._typeName.equalsIgnoreCase(name)) {
			retType = SUB;
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
