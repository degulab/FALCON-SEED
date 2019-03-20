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
 * @(#)SchemaValueTypeManager.java	3.2.0	2015/06/26
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module.schema.type;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 汎用フィルタ定義における、値のデータ型を管理するクラス。
 * このオブジェクトは、アプリケーションで唯一のインスタンスを持つ。
 * 
 * @version 3.2.0
 * @since 3.2.0
 */
public class SchemaValueTypeManager
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** 基本データ型の変更不可能なリスト **/
	static private final List<SchemaValueType>			_typelist;
	/** データ型タイプ名の判定に使用する変更不可能なマップ、キーはすべて小文字とする **/
	static private final Map<String, SchemaValueType>	_mapTypeNames;
	
	static {
		// 基本データ型リストの生成
		ArrayList<SchemaValueType> typelist = new ArrayList<SchemaValueType>();
		typelist.add(SchemaValueTypeString.instance);
		typelist.add(SchemaValueTypeDecimal.instance);
		typelist.add(SchemaValueTypeBoolean.instance);
		typelist.add(SchemaValueTypeDateTime.instance);
		typelist.trimToSize();
		_typelist = Collections.unmodifiableList(typelist);

		// タイプ名マップの生成
		Map<String, SchemaValueType> map = new LinkedHashMap<String, SchemaValueType>();
		for (SchemaValueType vt : _typelist) {
			String key = vt.getTypeName().toLowerCase();
			if (!map.containsKey(key)) {
				map.put(key, vt);
			}
		}
		_mapTypeNames = Collections.unmodifiableMap(map);
	}

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 基本データ型の変更不可能なリストを取得する。
	 * @return	基本データ型の変更不可能なリスト
	 */
	static public final List<SchemaValueType> getBasicTypeList() {
		return _typelist;
	}

	/**
	 * 指定されたデータ型が <tt>null</tt> の場合に、標準（文字列型）のデータ型を返す。
	 * @param valueType	データ型
	 * @return	<em>valueType</em> が <tt>null</tt> の場合は文字列データ型、それ以外の場合は <em>valueType</em>
	 */
	static public final SchemaValueType ensureValueTypeWithDefault(SchemaValueType valueType) {
		return (valueType==null ? SchemaValueTypeString.instance : valueType);
	}

	/**
	 * 指定されたデータ型を示す文字列から、データ型オブジェクトを生成する。
	 * @param valuetypeString	データ型を示す文字列
	 * @return	文字列から生成されたデータ型オブジェクト
	 * @throws NullPointerException	<em>valuetypeString</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	データ型を示す文字列がデータ型として不適切な場合
	 */
	static public SchemaValueType valueTypeOf(String valuetypeString) {
		return parseValueTypeString(valuetypeString);
	}

	/**
	 * 指定された文字列から、適切なデータ型を判定する。
	 * @param dtformat	日付時刻判定に使用する日付時刻書式
	 * @param value		判定する文字列
	 * @return	値から判別されたデータ型、値が <tt>null</tt> もしくは空文字列の場合は <tt>null</tt>
	 * @throws NullPointerException	<em>dtformat</em> が <tt>null</tt> の場合
	 */
	static public SchemaValueType detectValueTypeByValue(SchemaDateTimeFormats dtformat, String value) {
		if (value == null || value.isEmpty())
			return null;
		
		// check datetime
		if (dtformat.parse(value) != null) {
			return SchemaValueTypeDateTime.instance;
		}
		
		// check decimal
		try {
			new BigDecimal(value);
			return SchemaValueTypeDecimal.instance;
		} catch (Throwable ex) {
			// ignore exception
		}
		
		// check boolean
		if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
			return SchemaValueTypeBoolean.instance;
		}
		
		// string
		return SchemaValueTypeString.instance;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * データ型を示す文字列を、タイプ名と補助パラメータに分解する。
	 * タイプ名はすべて小文字に変換される。
	 * また、補助パラメータは、文字列の先頭に最も近い '(' から、文字列終端の ')' までとする。
	 * 補助パラメータが存在しない場合は、補助パラメータの要素を <tt>null</tt> とする。
	 * <p>なお、このメソッドでは、タイプ名や補助パラメータの正当性は評価しない。
	 * @param valuetypeString	データ型を示す文字列
	 * @return	先頭要素にタイプ名、次の要素に補助パラメータを格納した配列
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	データ型を示す文字列がデータ型書式ではない場合
	 */
	static protected String[] splitTypeNameAndSubParamater(String valuetypeString) {
		int len = valuetypeString.length();
		if (len == 0) {
			throw new IllegalArgumentException("Value type string is empty.");
		}
		
		// split
		String[] typeAndParam;
		int sparen = valuetypeString.indexOf('(');
		if (sparen < 0) {
			// No sub parameter
			typeAndParam = new String[]{valuetypeString.toLowerCase(), null};
		}
		else if (valuetypeString.charAt(len-1) == ')') {
			// has sub parameter
			typeAndParam = new String[]{
					valuetypeString.substring(0, sparen).toLowerCase(),
					valuetypeString.substring(sparen+1, len-1),
			};
		}
		else {
			// illegal format
			throw new IllegalArgumentException("There is no ')' at the end of value type string : \"" + valuetypeString + "\"");
		}
		
		// completed
		return typeAndParam;
	}

	/**
	 * データ型を示す文字列を解析し、データ型オブジェクトに変換する。
	 * 補助パラメータは、文字列の先頭に最も近い '(' から、文字列終端の ')' までとする。
	 * @param valuetypeString	データ型を示す文字列
	 * @return	変換したデータ型オブジェクト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	データ型を示す文字列がデータ型として不適切な場合
	 */
	static protected SchemaValueType parseValueTypeString(String valuetypeString) {
		String[] typeAndParam = splitTypeNameAndSubParamater(valuetypeString);
		
		// check type name
		SchemaValueType baseType = _mapTypeNames.get(typeAndParam[0]);
		if (baseType == null) {
			// unsupported data type
			throw new IllegalArgumentException("Data type is not supported : \"" + valuetypeString + "\"");
		}
		
		// no sub parameter
		if (typeAndParam[1] == null) {
			return baseType;
		}
		
		// check sub parameter
		if (!baseType.getAllowSubParameter()) {
			// sub parameter not allowed
			throw new IllegalArgumentException("Sub parameter can not be specified to this data type : \"" + valuetypeString + "\"");
		}
		
		// call constructor
		try {
			Constructor<?> typeConst = baseType.getClass().getConstructor(String.class);
			SchemaValueType genType = (SchemaValueType)typeConst.newInstance(typeAndParam[1]);
			return genType;
		}
		catch (InvocationTargetException ex) {
			throw new IllegalArgumentException("Sub parameter is illegal : \"" + valuetypeString + "\"", ex.getTargetException());
		}
		catch (Throwable ex) {
			throw new IllegalArgumentException("Data type is not supported sub parameter : \"" + valuetypeString + "\"", ex);
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
