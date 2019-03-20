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
 *  Copyright 2007-2015  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)ModuleFileType.java	2.2.0	2015/06/01
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.module;

/**
 * 実行モジュールファイルの種類。
 * 
 * @version 2.2.0
 * @since 2.2.0
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
	/** 汎用フィルタ定義を含む jar ファイルであることを示す **/
	AADL_GENERIC("aadl.generic"),
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

		if (name==null || name.isEmpty()) {
			// null や空文字の場合は UNKNOWN とする
			retType = UNKNOWN;
		}
		else if (AADL_JAR._typeName.equalsIgnoreCase(name)) {
			retType = AADL_JAR;
		}
		else if (AADL_MACRO._typeName.equalsIgnoreCase(name)) {
			retType = AADL_MACRO;
		}
		else if (AADL_GENERIC._typeName.equalsIgnoreCase(name)) {
			retType = AADL_GENERIC;
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
