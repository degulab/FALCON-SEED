/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  Copyright 2007-2014  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)ModuleArgument.java	2.1.0	2014/05/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ModuleArgument.java	1.10	2009/12/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ModuleArgument.java	1.00	2008/11/17
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.data;

/**
 * 実行モジュールの引数となる要素。
 * 
 * @version 2.1.0	2014/05/29
 *
 * @since 1.00
 */
public final class ModuleArgument
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** [IN] 属性 **/
	static public final String ARGTYPE_IN	= "[IN]";
	/** [OUT] 属性 **/
	static public final String ARGTYPE_OUT	= "[OUT]";
	/** [STR] 属性 **/
	static public final String ARGTYPE_STR	= "[STR]";
	/**
	 * [PUB] 属性
	 * @since 2.1.0
	 */
	static public final String ARGTYPE_PUB	= "[PUB]";
	/**
	 * [SUB] 属性
	 * @since 2.1.0
	 */
	static public final String ARGTYPE_SUB	= "[SUB]";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/**
	 * モジュール引数種別
	 */
	private final String	_type;
	/**
	 * モジュール引数値
	 */
	private final String	_value;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * MacroArgument の新規インスタンスを生成する。
	 * 
	 * @param type	引数の種別
	 * @param value	引数の値
	 */
	public ModuleArgument(String type, String value) {
		this._type = (type == null ? MacroNode.EMPTY_VALUE : type.intern());
		this._value = (value == null ? MacroNode.EMPTY_VALUE : value.intern());
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public boolean isArgTypeIN(String type) {
		return ARGTYPE_IN.equalsIgnoreCase(type);
	}
	
	static public boolean isArgTypeOUT(String type) {
		return ARGTYPE_OUT.equalsIgnoreCase(type);
	}
	
	static public boolean isArgTypeSTR(String type) {
		return ARGTYPE_STR.equalsIgnoreCase(type);
	}
	
	static public boolean isArgTypePUB(String type) {
		return ARGTYPE_PUB.equalsIgnoreCase(type);
	}
	
	static public boolean isArgTypeSUB(String type) {
		return ARGTYPE_SUB.equalsIgnoreCase(type);
	}
	
	public boolean isTypeIN() {
		return ARGTYPE_IN.equalsIgnoreCase(_type);
	}
	
	public boolean isTypeOUT() {
		return ARGTYPE_OUT.equalsIgnoreCase(_type);
	}
	
	public boolean isTypeSTR() {
		return ARGTYPE_STR.equalsIgnoreCase(_type);
	}
	
	public boolean isTypePUB() {
		return ARGTYPE_PUB.equalsIgnoreCase(_type);
	}
	
	public boolean isTypeSUB() {
		return ARGTYPE_SUB.equalsIgnoreCase(_type);
	}

	/**
	 * 引数種別を取得する。
	 * 
	 * @return 引数種別を返す。
	 */
	public String getType() {
		return _type;
	}

	/**
	 * 引数の値を取得する。
	 * 
	 * @return	引数の値を返す。
	 */
	public String getValue() {
		return _value;
	}

	/**
	 * このインスタンスのハッシュコードを返す。
	 * 
	 * @return ハッシュコード
	 */
	@Override
	public int hashCode() {
		return (_type.hashCode() ^ _value.hashCode());
	}

	/**
	 * このインスタンスと指定されたオブジェクトが同値であるかを検証する。
	 * 
	 * @return 同値であれば <tt>true</tt> を返す。
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		else if (obj instanceof ModuleArgument) {
			ModuleArgument aArg = (ModuleArgument)obj;
			if (aArg._type == this._type || (aArg._type != null && aArg._type.equals(this._type))) {
				if (aArg._value == this._value || (aArg._value != null && aArg._value.equals(this._value))) {
					return true;
				}
			}
		}
		
		return false;
	}

	/**
	 * このインスタンスの文字列表現を返す。
	 */
	@Override
	public String toString() {
		return (String.valueOf(_type) + " " + String.valueOf(_value));
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
