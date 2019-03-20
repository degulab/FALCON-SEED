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
 * @(#)SchemaValueTypeBoolean.java	3.2.1	2015/07/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SchemaValueTypeBoolean.java	3.2.0	2015/06/22
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module.schema.type;


/**
 * 汎用フィルタ定義における、真偽値データ型を示すクラス。
 * このオブジェクトは、不変オブジェクトである。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public class SchemaValueTypeBoolean extends SchemaValueType
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final String TYPENAME_BOOLEAN	= "Boolean";

	/** このオブジェクトの唯一のインスタンス **/
	static public final SchemaValueTypeBoolean instance = new SchemaValueTypeBoolean();

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public SchemaValueTypeBoolean() {
		super(TYPENAME_BOOLEAN);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このデータ型に相当する JAVA クラスを取得する。
	 * @return	<code>Boolean</code> クラスオブジェクト
	 */
	@Override
	public Class<Boolean> getJavaClass() {
		return Boolean.class;
	}

	/**
	 * 指定された文字列から、このデータ型に応じた値に変換する。
	 * 文字列が <tt>null</tt> もしくは空文字列の場合、このメソッドは <tt>null</tt> を返す。
	 * @param value	値を示す文字列
	 * @return	文字列から変換された値のオブジェクト
	 * @throws SchemaValueFormatException	変換に失敗した場合
	 */
	@Override
//	public Boolean valueOf(String value) throws SchemaValueFormatException
	public Boolean convertFromString(String value) throws SchemaValueFormatException
	{
		if (value != null && !value.isEmpty()) {
			return Boolean.valueOf(value);
		} else {
			return null;
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
