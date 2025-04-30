/*
 * @(#)SchemaJoinConditionOperators.java	3.2.0	2015/06/26
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
 * 汎用フィルタ定義における、結合演算子の型情報を保持するクラス。
 * 
 * @version 3.2.0
 * @since 3.2.0
 */
public class SchemaJoinConditionOperators
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	/** 結合演算子：INNER_JOIN **/
	static public String OP_INNER_JOIN	= "==";

	/** 演算子と許可されるデータ型の組 **/
	static public final SchemaBinaryOperatorType[] OPERATORS = {
		new SchemaBinaryOperatorType(OP_INNER_JOIN, new SchemaValueType[][]{
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
	
	private SchemaJoinConditionOperators() {}

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
