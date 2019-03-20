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
 *  Copyright 2007-2010  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)DtalgeDataTypes.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.editor.plugin.csv;


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
