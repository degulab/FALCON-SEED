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
 * @(#)RuntimeSchemaValueTypeManager.java	3.2.1	2015/07/22
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module.schema.type;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 汎用フィルタ定義の実行時における、値のデータ型を管理するクラス。
 * アプリケーション固有のデータ型変換のため、個別のインスタンスによりデータ型を保持する。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public class RuntimeSchemaValueTypeManager
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 固有のデータ型等を保持するための、タイプ名とオブジェクトのマップ **/
	private final Map<String, SchemaValueType>	_localTypeNameMap;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public RuntimeSchemaValueTypeManager() {
		// 基本のデータ型を持つローカルマップを生成
		List<SchemaValueType> basics = SchemaValueTypeManager.getBasicTypeList();
		_localTypeNameMap = new LinkedHashMap<String, SchemaValueType>();
		for (SchemaValueType basicType : basics) {
			_localTypeNameMap.put(basicType.getTypeName(), basicType);
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このオブジェクトに、指定されたデータ型を登録する。
	 * 同一のタイプ名を持つデータ型が登録済みの場合、新しいデータ型で置き換える。
	 * @param valuetype	登録する新しいデータ型
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @since 3.2.1
	 */
	public void registValueType(SchemaValueType valuetype) {
		_localTypeNameMap.put(valuetype.getTypeName(), valuetype);
	}

	/**
	 * 指定された文字列を、指定のデータ型に応じた値に変換する。
	 * このメソッドでは、このオブジェクトインスタンスに登録されたデータ型オブジェクトを使用して変換される。
	 * 登録されていないデータ型の場合は、指定されたデータ型を用いて変換される。
	 * @param valuetype		変換に用いるデータ型
	 * @param targetvalue	変換対象の文字列
	 * @return	文字列から変換された値のオブジェクト
	 * @throws NullPointerException	<em>valuetype</em> が <tt>null</tt> の場合
	 * @throws SchemaValueFormatException	変換に失敗した場合
	 * @since 3.2.1
	 */
	public Object convertFromString(SchemaValueType valuetype, String targetvalue) throws SchemaValueFormatException
	{
		SchemaValueType registType = _localTypeNameMap.get(valuetype.getTypeName());
		if (registType == null) {
			return valuetype.convertFromString(targetvalue);
		} else {
			return registType.convertFromString(targetvalue);
		}
	}

	/**
	 * 指定されたオブジェクトを、このデータ型に応じた文字列形式に変換する。
	 * このデータ型に応じたオブジェクト型ではない場合、{@link java.lang.Object#toString()} メソッドによって文字列形式に変換する。
	 * <em>targetvalue</em> が <tt>null</tt> の場合、このメソッドは <tt>null</tt> を返す。
	 * @param valuetype		変換に用いるデータ型
	 * @param targetvalue	文字列へ変換する値	
	 * @return	変換された文字列、<em>targetvalue</em> が <tt>null</tt> の場合は <tt>null</tt>
	 * @throws NullPointerException	<em>valuetype</em> が <tt>null</tt> の場合
	 * @since 3.2.1
	 */
	public String convertToString(SchemaValueType valuetype, Object targetvalue) {
		SchemaValueType registType = _localTypeNameMap.get(valuetype.getTypeName());
		if (registType == null) {
			return valuetype.convertToString(targetvalue);
		} else {
			return registType.convertToString(targetvalue);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
