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
 *  Copyright 2007-2010  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
/*
 * @(#)DtDataTypes.java	0.20	2010/02/25
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtDataTypes.java	0.10	2008/08/25
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.util;

import java.math.BigDecimal;

import dtalge.DtBase;
import dtalge.exception.IllegalValueOfDataTypeException;
import dtalge.exception.ValueFormatOfDataTypeException;

/**
 * <code>{@link DtBase}</code> クラスのデータ型定義。
 * 
 * @version 0.20	2010/02/25
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public final class DtDataTypes
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/**
	 * 真偽値(boolean)型を表すデータ型名
	 */
	static public final String BOOLEAN = "boolean";
	/**
	 * 実数(decimal)型を表すデータ型名
	 */
	static public final String DECIMAL = "decimal";
	/**
	 * 文字列(string)型を表すデータ型名
	 */
	static public final String STRING  = "string";

	/**
	 * 真偽値(boolean)型とする基本クラス
	 */
	static public final Class<Boolean> BooleanClass = Boolean.class;
	/**
	 * 実数(decimal)型とする基本クラス
	 */
	static public final Class<BigDecimal> DecimalClass = BigDecimal.class;
	/**
	 * 文字列(string)型とする基本クラス
	 */
	static public final Class<String> StringClass  = String.class;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private DtDataTypes() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * データ型を表す名前から、正規のデータ型名を取得する。
	 * 
	 * @param typeName データ型名
	 * @return 正規のデータ型名
	 * 
	 * @throws NullPointerException 指定されたデータ型名が <tt>null</tt> の場合
	 * @throws IllegalArgumentException 許可されていないデータ型名の場合
	 */
	static public String fromName(String typeName) {
		String name = typeName.toLowerCase().intern();
		
		if (BOOLEAN.equals(name)) {
			// boolean
			return BOOLEAN;
		}
		else if (DECIMAL.equals(name)) {
			// decimal
			return DECIMAL;
		}
		else if (STRING.equals(name)) {
			// string
			return STRING;
		}
		
		// no match
		throw new IllegalArgumentException("Illegal type name : " + String.valueOf(typeName));
	}

	/**
	 * データ型を表す名前から、対応する基本クラスを取得する。
	 * 
	 * @param typeName	データ型名
	 * @return	対応クラスオブジェクト
	 * 
	 * @throws NullPointerException	指定されたデータ型名が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	許可されていないデータ型名の場合
	 */
	static public Class<?> classFromName(String typeName) {
		String name = typeName.toLowerCase().intern();
		
		if (BOOLEAN.equals(name)) {
			// boolean
			return BooleanClass;
		}
		else if (DECIMAL.equals(name)) {
			// decimal
			return DecimalClass;
		}
		else if (STRING.equals(name)) {
			// string
			return StringClass;
		}
		
		// no match
		throw new IllegalArgumentException("Illegal type name : " + String.valueOf(typeName));
	}

	/**
	 * 指定された値が指定されたデータ型のオブジェクトか検証する。
	 * <p>このメソッドは、データ型名に対応するデータ型と一致する(もしくは
	 * 基本クラスである)かを検証する。キャストや変換が可能な場合でも、データ型
	 * が異なれば例外をスローする。
	 * 
	 * @param typeName	データ型名
	 * @param value		検証するオブジェクト
	 * 
	 * @throws NullPointerException	指定されたデータ型名が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	許可されていないデータ型名の場合
	 * @throws IllegalValueOfDataTypeException	値のクラスがデータ型と異なる場合
	 */
	static public void validDataType(String typeName, Object value) {
		// データ型のクラス取得
		Class<?> targetClass = classFromName(typeName);
		
		// 値が null の場合は、無条件に許可する
		if (value == null) {
			return;
		}
		
		// クラス検証
		if (targetClass.isAssignableFrom(value.getClass())) {
			return;	// valid
		}
		
		// illegal class type
		String msg = String.format("Value(%s) is not %s(%s).",
								value.getClass().getName(), typeName, targetClass.getName());
		throw new IllegalValueOfDataTypeException(typeName, targetClass, value, msg);
	}

	/**
	 * 指定された文字列を、指定されたデータ型に対応するオブジェクトに
	 * 変換する。変換できない場合は例外をスローする。
	 * なお、指定された <code>value</code> が <tt>null</tt> の場合、無条件に <tt>null</tt> を返す。
	 * 
	 * @param typeName	データ型名
	 * @param value		変換する文字列
	 * @return	変換した値となるオブジェクトのインスタンス
	 * 
	 * @throws NullPointerException	指定されたデータ型名が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	許可されていないデータ型名の場合
	 * @throws ValueFormatOfDataTypeException	値がデータ型に対応するオブジェクトに変換できない場合
	 */
	static public Object valueOf(String typeName, String value) {
		String name = typeName.toLowerCase().intern();

		Class<?> targetClass = null;
		try {
			if (BOOLEAN.equals(name)) {
				// boolean
				targetClass = BooleanClass;
				Boolean retValue = null;
				if (value != null) {
					retValue = new Boolean(value);
				}
				return retValue;
			}
			else if (DECIMAL.equals(name)) {
				// decimal
				targetClass = DecimalClass;
				BigDecimal retValue = null;
				if (value != null) {
					retValue = new BigDecimal(value);
				}
				return retValue;
			}
			else if (STRING.equals(name)) {
				// string
				targetClass = StringClass;
				return value;
			}
		}
		catch (Throwable ex) {
			String msg = String.format("Value(%s) cannot converted to %s(%s).",
									String.valueOf(value), typeName, targetClass.getName());
			throw new ValueFormatOfDataTypeException(typeName, targetClass, value, msg, ex);
		}
		
		// no match
		throw new IllegalArgumentException("Illegal type name : " + String.valueOf(typeName));
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
