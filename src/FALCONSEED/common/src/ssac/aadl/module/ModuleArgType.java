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
