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
 * @(#)SchemaBinaryOperatorType.java	3.2.1	2015/07/15
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SchemaBinaryOperatorType.java	3.2.0	2015/06/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module.schema.exp;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import ssac.aadl.fs.module.schema.type.SchemaValueType;

/**
 * 汎用フィルタ定義における、二項演算子のタイプ情報を保持するクラス。
 * このオブジェクトは不変オブジェクトとする。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public class SchemaBinaryOperatorType
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	static protected final int	INDEX_RESULT	= 0;
	static protected final int	INDEX_LEFT		= 1;
	static protected final int	INDEX_RIGHT		= 2;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 演算子 **/
	private final String				_operator;
	private final SchemaValueType[][]	_typePairs;
	private final Map<SchemaValueType, Map<SchemaValueType, SchemaValueType>>	_supportedTypeMap;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 演算子と、左右のオペランドに許可されるデータ型を登録する。
	 * @param operator		演算子を示す文字列
	 * @param types			戻り値型と左右で対応するデータ型ペアの2次元配列
	 */
	public SchemaBinaryOperatorType(String operator, SchemaValueType[][] types) {
		_operator  = operator;
		_typePairs = types;
		
		// create supported type map
		_supportedTypeMap = new LinkedHashMap<SchemaValueType, Map<SchemaValueType, SchemaValueType>>();
		for (SchemaValueType[] pairs : types) {
			Map<SchemaValueType, SchemaValueType> submap = _supportedTypeMap.get(pairs[INDEX_LEFT]);
			if (submap == null) {
				submap = new LinkedHashMap<SchemaValueType, SchemaValueType>();
				_supportedTypeMap.put(pairs[INDEX_LEFT], submap);
			}
			submap.put(pairs[INDEX_RIGHT], pairs[INDEX_RESULT]);
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public String operator() {
		return _operator;
	}
	
	public boolean containsResultType(SchemaValueType valuetype) {
		for (SchemaValueType[] pairs : _typePairs) {
			if (pairs[INDEX_RESULT].equals(valuetype)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean containsLeftOperandType(SchemaValueType valuetype) {
		return _supportedTypeMap.containsKey(valuetype);
	}
	
	public boolean containsRightOperandType(SchemaValueType valuetype) {
		for (SchemaValueType[] pairs : _typePairs) {
			if (pairs[INDEX_RIGHT].equals(valuetype)) {
				return true;
			}
		}
		return false;
	}

	public boolean containsPair(SchemaValueType leftType, SchemaValueType rightType) {
		Map<SchemaValueType, SchemaValueType> submap = _supportedTypeMap.get(leftType);
		return (submap==null ? false : submap.containsKey(rightType));
	}

	/**
	 * 左と右のデータ型から得られる結果のデータ型を取得する。
	 * データ型の組合せが適切ではない場合、このメソッドは <tt>null</tt> を返す。
	 * @param leftType	左オペランドのデータ型
	 * @param rightType	右オペランドのデータ型
	 * @return	結果のデータ型、サポートされていない組合せの場合は <tt>true</tt>
	 */
	public SchemaValueType getResultType(SchemaValueType leftType, SchemaValueType rightType) {
		Map<SchemaValueType, SchemaValueType> submap = _supportedTypeMap.get(leftType);
		if (submap != null) {
			return submap.get(rightType);
		} else {
			return null;
		}
	}

	/**
	 * このオペレーションがサポートしている左オペランドのデータ型の集合を取得する。
	 * @return	左オペランドのデータ型集合
	 */
	public Set<SchemaValueType> getSupportedLeftValueTypes() {
		return Collections.unmodifiableSet(_supportedTypeMap.keySet());
	}

	/**
	 * 指定された左オペランドのデータ型に対応する、このオペレーションがサポートしている右オペランドのデータ型と戻り値型のマップを取得する。
	 * @param leftType	左オペランドのデータ型
	 * @return	左オペランドのデータ型に対応する右オペランドと戻り値のデータ型マップ、左オペランドのデータ型がサポートされていない場合は要素が空のマップ
	 */
	public Map<SchemaValueType, SchemaValueType> getSupportedRightValueTypes(SchemaValueType leftType) {
		Map<SchemaValueType, SchemaValueType> submap = _supportedTypeMap.get(leftType);
		if (submap != null) {
			return Collections.unmodifiableMap(submap);
		} else {
			return Collections.emptyMap();
		}
	}

	@Override
	public String toString() {
		return _operator;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
