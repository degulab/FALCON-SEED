/*
 * @(#)SchemaJoinConditionData.java	3.2.0	2015/06/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module.schema.exp;

import ssac.aadl.fs.module.schema.SchemaElementValue;

/**
 * 汎用フィルタ定義の結合条件定義データ。
 * 
 * @version 3.2.0
 * @since 3.2.0
 */
public class SchemaJoinConditionData extends SchemaBinaryOperationData
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final String	TYPE_NAME	= "Join";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public SchemaJoinConditionData() {
		super();
	}

	public SchemaJoinConditionData(String operator) {
		super(operator);
	}
	
	public SchemaJoinConditionData(String operator, SchemaElementValue leftValue, SchemaElementValue rightValue) {
		super(operator, leftValue, rightValue);
		refreshResultValueType();
	}
	
	public SchemaJoinConditionData(final SchemaJoinConditionData src) {
		super(src);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	@Override
	public String getTypeName() {
		return TYPE_NAME;
	}

	@Override
	public boolean isValidOperation() {
		return SchemaJoinConditionOperators.containsPair(getOperator(), getLeftValueType(), getRightValueType());
	}

	@Override
	public boolean refreshResultValueType() {
		return updateResultType(SchemaJoinConditionOperators.getOperationResult(getOperator(), getLeftValueType(), getRightValueType()));
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
