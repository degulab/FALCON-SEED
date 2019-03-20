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
 *  Copyright 2007-2016  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)ValueTypes.java	3.3.0	2016/04/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.excel2csv.command.option;

/**
 * 変換定義における 'valuetype' オプションの列挙値。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public enum ValueTypes
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** 自動判別 **/
	AUTO("auto"),
	/** 数値型 **/
	DECIMAL("decimal"),
	/** 真偽値型 **/
	BOOL("boolean"),
	/** 日付時刻型 **/
	DATE("date"),
	/** Excel書式 **/
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
	private ValueTypes(String name) {
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
	static public ValueTypes fromName(String name) {
		ValueTypes retType = null;
		
		if (name == null || name.isEmpty()) {
			retType = null;
		}
		else if (AUTO._typeName.equalsIgnoreCase(name)) {
			retType = AUTO;
		}
		else if (DECIMAL._typeName.equalsIgnoreCase(name)) {
			retType = DECIMAL;
		}
		else if (BOOL._typeName.equalsIgnoreCase(name)) {
			retType = BOOL;
		}
		else if (DATE._typeName.equalsIgnoreCase(name)) {
			retType = DATE;
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
