/*
 * @(#)GenericJoinCondSchemaTableModel.java	3.2.1	2015/07/22
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)GenericJoinCondSchemaTableModel.java	3.2.0	2015/06/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui.exp;

import ssac.aadl.fs.module.schema.SchemaElementValue;
import ssac.aadl.fs.module.schema.exp.SchemaBinaryOperatorType;
import ssac.aadl.fs.module.schema.exp.SchemaJoinConditionOperators;
import ssac.falconseed.runner.RunnerMessages;

/**
 * 汎用フィルタの計算式設定用テーブルモデル。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public class GenericJoinCondSchemaTableModel extends GenericOperationSchemaTableModel<GenericJoinCondElementEditModel>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public GenericJoinCondSchemaTableModel() {
		super(false);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	@Override
	protected String getRowNamePrefix() {
		return GenericJoinCondElementEditModel.NAME_PREFIX;
	}

	@Override
	public SchemaBinaryOperatorType getOperatorType(String operator) {
		return SchemaJoinConditionOperators.getOperatorType(operator);
	}

	@Override
	protected GenericJoinCondElementEditModel createElementInstance() {
		return new GenericJoinCondElementEditModel();
	}

	@Override
	protected GenericJoinCondElementEditModel createElementInstance(String operator, SchemaElementValue leftValue,SchemaElementValue rightValue) {
		return new GenericJoinCondElementEditModel(operator, leftValue, rightValue);
	}
	
	@Override
	public String getTitle() {
		return RunnerMessages.getInstance().GenericJoinCondTableModel_name_Expression;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	@Override
	protected String getExpressionColumnName() {
		return RunnerMessages.getInstance().GenericJoinCondTableModel_name_Expression;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
