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
 * @(#)SchemaWhereConditionOperators.java	3.2.0	2015/06/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module.schema.exp;

import java.util.HashMap;
import java.util.Map;

import ssac.aadl.fs.module.schema.type.SchemaValueType;
import ssac.aadl.fs.module.schema.type.SchemaValueTypeBoolean;
import ssac.aadl.fs.module.schema.type.SchemaValueTypeDateTime;
import ssac.aadl.fs.module.schema.type.SchemaValueTypeDecimal;
import ssac.aadl.fs.module.schema.type.SchemaValueTypeString;

/**
 * 汎用フィルタ定義における、比較演算子の型情報を保持するクラス。
 * 
 * @version 3.2.0
 * @since 3.2.0
 */
public class SchemaWhereConditionOperators
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	/** 関係演算子：等しい **/
	static public String OP_RELATION_EQUAL			= "==";
	/** 関係演算子：等しくない **/
	static public String OP_RELATION_NOTEQUAL		= "!=";
	/** 関係演算子：より小さい **/
	static public String OP_RELATION_LESSTHAN		= "<";
	/** 関係演算子：以下 **/
	static public String OP_RELATION_LESSEQUAL		= "<=";
	/** 関係演算子：より大きい **/
	static public String OP_RELATION_GREATERTHAN	= ">";
	/** 関係演算子：以上 **/
	static public String OP_RELATION_GREATEREQUAL	= ">=";
	/** 関係演算子：正規表現パターンによる一致 **/
	static public String OP_RELATION_REGEX_MATCH	= "=~";
	/** 関係演算子：正規表現パターンによる不一致 **/
	static public String OP_RELATION_REGEX_NOTMATCH	= "!~";

	/** 演算子と許可されるデータ型の組 **/
	static public final SchemaBinaryOperatorType[] OPERATORS = {
		new SchemaBinaryOperatorType(OP_RELATION_EQUAL, new SchemaValueType[][]{
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeString.instance, SchemaValueTypeString.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeString.instance, SchemaValueTypeDecimal.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeString.instance, SchemaValueTypeDateTime.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeString.instance, SchemaValueTypeBoolean.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeDecimal.instance, SchemaValueTypeString.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeDecimal.instance, SchemaValueTypeDecimal.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeDecimal.instance, SchemaValueTypeDateTime.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeDecimal.instance, SchemaValueTypeBoolean.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeDateTime.instance, SchemaValueTypeString.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeDateTime.instance, SchemaValueTypeDecimal.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeDateTime.instance, SchemaValueTypeDateTime.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeDateTime.instance, SchemaValueTypeBoolean.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeBoolean.instance, SchemaValueTypeString.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeBoolean.instance, SchemaValueTypeDecimal.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeBoolean.instance, SchemaValueTypeDateTime.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeBoolean.instance, SchemaValueTypeBoolean.instance },
		}),
		new SchemaBinaryOperatorType(OP_RELATION_NOTEQUAL, new SchemaValueType[][]{
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeString.instance, SchemaValueTypeString.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeString.instance, SchemaValueTypeDecimal.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeString.instance, SchemaValueTypeDateTime.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeString.instance, SchemaValueTypeBoolean.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeDecimal.instance, SchemaValueTypeString.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeDecimal.instance, SchemaValueTypeDecimal.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeDecimal.instance, SchemaValueTypeDateTime.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeDecimal.instance, SchemaValueTypeBoolean.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeDateTime.instance, SchemaValueTypeString.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeDateTime.instance, SchemaValueTypeDecimal.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeDateTime.instance, SchemaValueTypeDateTime.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeDateTime.instance, SchemaValueTypeBoolean.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeBoolean.instance, SchemaValueTypeString.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeBoolean.instance, SchemaValueTypeDecimal.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeBoolean.instance, SchemaValueTypeDateTime.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeBoolean.instance, SchemaValueTypeBoolean.instance },
		}),
		new SchemaBinaryOperatorType(OP_RELATION_LESSTHAN, new SchemaValueType[][]{
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeString.instance, SchemaValueTypeString.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeString.instance, SchemaValueTypeDecimal.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeString.instance, SchemaValueTypeDateTime.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeString.instance, SchemaValueTypeBoolean.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeDecimal.instance, SchemaValueTypeString.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeDecimal.instance, SchemaValueTypeDecimal.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeDateTime.instance, SchemaValueTypeString.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeDateTime.instance, SchemaValueTypeDateTime.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeBoolean.instance, SchemaValueTypeString.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeBoolean.instance, SchemaValueTypeBoolean.instance },
		}),
		new SchemaBinaryOperatorType(OP_RELATION_LESSEQUAL, new SchemaValueType[][]{
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeString.instance, SchemaValueTypeString.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeString.instance, SchemaValueTypeDecimal.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeString.instance, SchemaValueTypeDateTime.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeString.instance, SchemaValueTypeBoolean.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeDecimal.instance, SchemaValueTypeString.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeDecimal.instance, SchemaValueTypeDecimal.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeDateTime.instance, SchemaValueTypeString.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeDateTime.instance, SchemaValueTypeDateTime.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeBoolean.instance, SchemaValueTypeString.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeBoolean.instance, SchemaValueTypeBoolean.instance },
		}),
		new SchemaBinaryOperatorType(OP_RELATION_GREATERTHAN, new SchemaValueType[][]{
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeString.instance, SchemaValueTypeString.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeString.instance, SchemaValueTypeDecimal.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeString.instance, SchemaValueTypeDateTime.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeString.instance, SchemaValueTypeBoolean.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeDecimal.instance, SchemaValueTypeString.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeDecimal.instance, SchemaValueTypeDecimal.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeDateTime.instance, SchemaValueTypeString.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeDateTime.instance, SchemaValueTypeDateTime.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeBoolean.instance, SchemaValueTypeString.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeBoolean.instance, SchemaValueTypeBoolean.instance },
		}),
		new SchemaBinaryOperatorType(OP_RELATION_GREATEREQUAL, new SchemaValueType[][]{
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeString.instance, SchemaValueTypeString.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeString.instance, SchemaValueTypeDecimal.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeString.instance, SchemaValueTypeDateTime.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeString.instance, SchemaValueTypeBoolean.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeDecimal.instance, SchemaValueTypeString.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeDecimal.instance, SchemaValueTypeDecimal.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeDateTime.instance, SchemaValueTypeString.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeDateTime.instance, SchemaValueTypeDateTime.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeBoolean.instance, SchemaValueTypeString.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeBoolean.instance, SchemaValueTypeBoolean.instance },
		}),
		new SchemaBinaryOperatorType(OP_RELATION_REGEX_MATCH, new SchemaValueType[][]{
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeString.instance, SchemaValueTypeString.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeString.instance, SchemaValueTypeDecimal.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeString.instance, SchemaValueTypeDateTime.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeString.instance, SchemaValueTypeBoolean.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeDecimal.instance, SchemaValueTypeString.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeDateTime.instance, SchemaValueTypeString.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeBoolean.instance, SchemaValueTypeString.instance },
		}),
		new SchemaBinaryOperatorType(OP_RELATION_REGEX_NOTMATCH, new SchemaValueType[][]{
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeString.instance, SchemaValueTypeString.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeString.instance, SchemaValueTypeDecimal.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeString.instance, SchemaValueTypeDateTime.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeString.instance, SchemaValueTypeBoolean.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeDecimal.instance, SchemaValueTypeString.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeDateTime.instance, SchemaValueTypeString.instance },
				{ SchemaValueTypeBoolean.instance, SchemaValueTypeBoolean.instance, SchemaValueTypeString.instance },
		}),
	};
	
	static protected final Map<String, SchemaBinaryOperatorType>	OPERATOR_MAP;
	static {
		OPERATOR_MAP = new HashMap<String, SchemaBinaryOperatorType>();
		for (SchemaBinaryOperatorType optype : OPERATORS) {
			OPERATOR_MAP.put(optype.operator(), optype);
		}
	}

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private SchemaWhereConditionOperators() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public boolean containsOperator(String operator) {
		return OPERATOR_MAP.containsKey(operator);
	}
	
	static public SchemaBinaryOperatorType getOperatorType(String operator) {
		return OPERATOR_MAP.get(operator);
	}
	
	static public boolean containsPair(String operator, SchemaValueType left, SchemaValueType right) {
		SchemaBinaryOperatorType optype = OPERATOR_MAP.get(operator);
		return (optype==null ? false : optype.containsPair(left, right));
	}
	
	static public SchemaValueType getOperationResult(String operator, SchemaValueType left, SchemaValueType right) {
		SchemaBinaryOperatorType optype = OPERATOR_MAP.get(operator);
		return (optype==null ? null : optype.getResultType(left, right));
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
