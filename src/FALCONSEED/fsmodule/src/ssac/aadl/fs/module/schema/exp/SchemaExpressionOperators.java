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
 * @(#)SchemaExpressionOperators.java	3.2.1	2015/07/15
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SchemaExpressionOperators.java	3.2.0	2015/06/24
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
 * 汎用フィルタ定義における、計算演算子の型情報を保持するクラス。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public class SchemaExpressionOperators
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** 算術演算子：加算 **/
	static public String OP_ARITH_ADD		= "+";
	/** 算術演算子：減算 **/
	static public String OP_ARITH_SUB		= "-";
	/** 算術演算子：乗算 **/
	static public String OP_ARITH_MULTI		= "*";
	/** 算術演算子：除算 **/
	static public String OP_ARITH_DIV		= "/";
	/** 算術演算子：剰余 **/
	static public String OP_ARITH_MOD		= "%";

	/** 演算子と許可されるデータ型の組 **/
	static public final SchemaBinaryOperatorType[] OPERATORS = {
		new SchemaBinaryOperatorType(OP_ARITH_ADD, new SchemaValueType[][]{
				{ SchemaValueTypeString.instance, SchemaValueTypeString.instance, SchemaValueTypeString.instance },
				{ SchemaValueTypeString.instance, SchemaValueTypeString.instance, SchemaValueTypeDecimal.instance },
				{ SchemaValueTypeString.instance, SchemaValueTypeString.instance, SchemaValueTypeDateTime.instance },
				{ SchemaValueTypeString.instance, SchemaValueTypeString.instance, SchemaValueTypeBoolean.instance },
				{ SchemaValueTypeString.instance, SchemaValueTypeDecimal.instance, SchemaValueTypeString.instance },
				{ SchemaValueTypeDecimal.instance, SchemaValueTypeDecimal.instance, SchemaValueTypeDecimal.instance },
				{ SchemaValueTypeString.instance, SchemaValueTypeDateTime.instance, SchemaValueTypeString.instance },
				{ SchemaValueTypeString.instance, SchemaValueTypeBoolean.instance, SchemaValueTypeString.instance },
		}),
		new SchemaBinaryOperatorType(OP_ARITH_SUB, new SchemaValueType[][]{
				{ SchemaValueTypeDecimal.instance, SchemaValueTypeDecimal.instance, SchemaValueTypeDecimal.instance },
		}),
		new SchemaBinaryOperatorType(OP_ARITH_MULTI, new SchemaValueType[][]{
				{ SchemaValueTypeDecimal.instance, SchemaValueTypeDecimal.instance, SchemaValueTypeDecimal.instance },
		}),
		new SchemaBinaryOperatorType(OP_ARITH_DIV, new SchemaValueType[][]{
				{ SchemaValueTypeDecimal.instance, SchemaValueTypeDecimal.instance, SchemaValueTypeDecimal.instance },
		}),
		new SchemaBinaryOperatorType(OP_ARITH_MOD, new SchemaValueType[][]{
				{ SchemaValueTypeDecimal.instance, SchemaValueTypeDecimal.instance, SchemaValueTypeDecimal.instance },
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
	
	protected SchemaExpressionOperators() {}

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
	
	static public boolean isValidLeftOperand(String operator, SchemaValueType left) {
		SchemaBinaryOperatorType optype = OPERATOR_MAP.get(operator);
		return (optype==null ? null : optype.containsLeftOperandType(left));
	}
	
	static public boolean isValidOperandPair(String operator, SchemaValueType left, SchemaValueType right) {
		SchemaBinaryOperatorType optype = OPERATOR_MAP.get(operator);
		return (optype==null ? null : optype.containsPair(left, right));
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
