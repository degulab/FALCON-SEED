/*
 * @(#)SchemaExpressionData.java	3.2.0	2015/06/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module.schema.exp;

import java.math.BigDecimal;
import java.math.MathContext;

import ssac.aadl.fs.module.schema.SchemaElementValue;
import ssac.aadl.fs.module.schema.type.SchemaValueType;
import ssac.aadl.fs.module.schema.type.SchemaValueTypeDecimal;
import ssac.aadl.runtime.AADLFunctions;

/**
 * 汎用フィルタ定義の計算式定義データ。
 * 
 * @version 3.2.0
 * @since 3.2.0
 */
public class SchemaExpressionData extends SchemaBinaryOperationData
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final String	TYPE_NAME	= "Exp";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public SchemaExpressionData() {
		super();
	}

	public SchemaExpressionData(String operator) {
		super(operator);
	}
	
	public SchemaExpressionData(String operator, SchemaElementValue leftValue, SchemaElementValue rightValue) {
		super(operator, leftValue, rightValue);
		refreshResultValueType();
	}
	
	public SchemaExpressionData(final SchemaExpressionData src) {
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
		return SchemaExpressionOperators.containsPair(getOperator(), getLeftValueType(), getRightValueType());
	}

	@Override
	public boolean refreshResultValueType() {
		return updateResultType(SchemaExpressionOperators.getOperationResult(getOperator(), getLeftValueType(), getRightValueType()));
	}

	/**
	 * 現在の式の内容で演算を実行し、このオブジェクトの値を更新する。
	 * @return	演算結果のオブジェクト
	 * @throws SchemaExpressionEvalException	計算に失敗した場合
	 */
	public Object eval() throws SchemaExpressionEvalException
	{
		SchemaBinaryOperatorType operator = SchemaExpressionOperators.getOperatorType(getOperator());
		if (operator == null) {
			throw new SchemaExpressionEvalException(this, "Unsupported operator");
		}
		if (!hasLeftOperand()) {
			throw new SchemaExpressionEvalException(this, "Left operand is not specified");
		}
		if (!hasRightOperand()) {
			throw new SchemaExpressionEvalException(this, "Right operand is not specified");
		}
		
		// check data type
		if (!operator.containsPair(getLeftOperand().getValueType(), getRightOperand().getValueType())) {
			throw new SchemaExpressionEvalException(this, "Cannot evaluate of expression, illegal data types");
		}
		
		// do evaluate
		Object result;
		SchemaValueType resultType = operator.getResultType(getLeftOperand().getValueType(), getRightOperand().getValueType());
		if (SchemaExpressionOperators.OP_ARITH_ADD.equals(operator.operator())) {
			// 加算
			if (SchemaValueTypeDecimal.instance.equals(resultType)) {
				// 数値演算
				BigDecimal val1 = (BigDecimal)getLeftOperand().getValue();
				BigDecimal val2 = (BigDecimal)getRightOperand().getValue();
				if (val1==null || val2==null) {
					result = null;
				} else {
					result = val1.add(val2);
				}
			}
			else {
				// 文字列として連結
				Object val1 = getLeftOperand().getValue();
				Object val2 = getRightOperand().getValue();
				//--- null は空白文字とみなして連結、両方とも null なら null
				if (val1 == null) {
					result = (val2==null ? null : AADLFunctions.toString(val2));
				} else if (val2 == null) {
					result = AADLFunctions.toString(val1);
				} else {
					result = AADLFunctions.toString(val1) + AADLFunctions.toString(val2);
				}
			}
		}
		else if (SchemaExpressionOperators.OP_ARITH_SUB.equals(operator.operator())) {
			// 減算
			BigDecimal val1 = (BigDecimal)getLeftOperand().getValue();
			BigDecimal val2 = (BigDecimal)getRightOperand().getValue();
			if (val1==null || val2==null) {
				result = null;
			} else {
				result = val1.subtract(val2);
			}
		}
		else if (SchemaExpressionOperators.OP_ARITH_MULTI.equals(operator.operator())) {
			// 乗算
			BigDecimal val1 = (BigDecimal)getLeftOperand().getValue();
			BigDecimal val2 = (BigDecimal)getRightOperand().getValue();
			if (val1==null || val2==null) {
				result = null;
			} else {
				result = val1.multiply(val2);
			}
		}
		else if (SchemaExpressionOperators.OP_ARITH_DIV.equals(operator.operator())) {
			// 除算
			BigDecimal val1 = (BigDecimal)getLeftOperand().getValue();
			BigDecimal val2 = (BigDecimal)getRightOperand().getValue();
			if (val1==null || val2==null) {
				result = null;
			} else {
				try {
					result = val1.divide(val2, MathContext.DECIMAL128);
				} catch (ArithmeticException ex) {
					throw new SchemaExpressionEvalException(this, ex);
				}
			}
		}
		else if (SchemaExpressionOperators.OP_ARITH_MOD.equals(operator.operator())) {
			// 剰余
			BigDecimal val1 = (BigDecimal)getLeftOperand().getValue();
			BigDecimal val2 = (BigDecimal)getRightOperand().getValue();
			if (val1==null || val2==null) {
				result = null;
			} else {
				try {
					result = val1.remainder(val2);
				} catch (ArithmeticException ex) {
					throw new SchemaExpressionEvalException(this, ex);
				}
			}
		}
		else {
			throw new SchemaExpressionEvalException(this, "Unsupported operator");
		}

		setValueType(resultType);
		setValue(result);
		return result;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
