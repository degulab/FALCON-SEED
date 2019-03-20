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
 * @(#)GenericJoinCondElementEditModel.java	3.2.1	2015/07/22
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)GenericJoinCondElementEditModel.java	3.2.0	2015/06/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui.exp;

import ssac.aadl.fs.module.schema.SchemaElementValue;
import ssac.aadl.fs.module.schema.exp.SchemaBinaryOperatorType;
import ssac.aadl.fs.module.schema.exp.SchemaJoinConditionData;
import ssac.aadl.fs.module.schema.exp.SchemaJoinConditionOperators;
import ssac.falconseed.filter.generic.gui.GenericSchemaElementValidation;
import ssac.falconseed.filter.generic.gui.GenericSchemaElementValidationImpl;
import ssac.falconseed.runner.RunnerMessages;

/**
 * 汎用フィルタの結合条件設定用テーブルにおける要素となるデータモデル。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public class GenericJoinCondElementEditModel extends SchemaJoinConditionData implements GenericSchemaElementValidation
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final String	NAME_PREFIX	= SchemaJoinConditionData.TYPE_NAME;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 値のエラー情報を保持するオブジェクト **/
	private final GenericSchemaElementValidationImpl	_implValid = new GenericSchemaElementValidationImpl();

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public GenericJoinCondElementEditModel() {
		super();
	}

	public GenericJoinCondElementEditModel(String operator, SchemaElementValue leftValue, SchemaElementValue rightValue) {
		super(operator, leftValue, rightValue);
		refreshResultValueType();
	}
	
	public GenericJoinCondElementEditModel(final SchemaJoinConditionData src) {
		super(src);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Implements GenericSchemaElementValidation interfaces
	//------------------------------------------------------------

	/**
	 * スキーマ定義要素の正当性を判定する。
	 * エラーがあれば、エラー情報を設定する。
	 * @return	正当なら <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	@Override
	public boolean verify() {
		// clear error
		clearError();
		
		// オペランドのチェック
		SchemaElementValue lop = getLeftOperand();
		SchemaElementValue rop = getRightOperand();
		if (lop == null) {
			// No left operand
			setError(RunnerMessages.getInstance().msgGenericFilterEdit_NoLeftOperand);
			return false;
		}
		else if (rop == null) {
			// No right operand
			setError(RunnerMessages.getInstance().msgGenericFilterEdit_NoRightOperand);
			return false;
		}
		
		// オペレータのチェック
		String strOperator = getOperator();
		if (strOperator == null) {
			// No operator
			setError(RunnerMessages.getInstance().msgGenericFilterEdit_NoOperator);
			return false;
		}
		
		// データ型のチェック
		SchemaBinaryOperatorType optype = SchemaJoinConditionOperators.getOperatorType(strOperator);
		if (optype == null) {
			// Unsupported operator
			setError(RunnerMessages.getInstance().msgGenericFilterEdit_UnsupportedOperator);
			return false;
		}
		if (!optype.containsLeftOperandType(lop.getValueType())) {
			// Invalid left operand data type
			setError(GenericOperationSchemaTableModel.formatLeftOperandValueTypeErrorMessage(optype, lop));
			return false;
		}
		if (!optype.containsPair(lop.getValueType(), rop.getValueType())) {
			// Invalid right operand data type
			setError(GenericOperationSchemaTableModel.formatRightOperandValueTypeErrorMessage(optype, lop, rop));
			return false;
		}
		
		// valid
		return true;
	}

	/**
	 * エラーの有無を返す。
	 * @return	エラーがある場合は <tt>true</tt>、エラーのない場合は <tt>false</tt>
	 */
	@Override
	public boolean hasError() {
		return _implValid.hasError();
	}

	/**
	 * 有効なエラーメッセージを返す。
	 * @return	有効なエラーメッセージ、エラーが存在しない場合は <tt>null</tt>
	 */
	@Override
	public String getAvailableErrorMessage() {
		return _implValid.getAvailableErrorMessage();
	}

	/**
	 * 設定されているエラーメッセージを返す。
	 * @return	設定されているエラーメッセージ、設定されていない場合は <tt>null</tt>
	 */
	@Override
	public String getErrorMessage() {
		return _implValid.getErrorMessage();
	}

	/**
	 * 設定されているエラー要因を返す。
	 * @return	エラー要因となる例外オブジェクト、設定されていない場合は <tt>null</tt>
	 */
	@Override
	public Throwable getErrorCause() {
		return _implValid.getErrorCause();
	}

	/**
	 * エラー情報をクリアする。
	 * @return	状態が変更された場合は <tt>true</tt>
	 */
	@Override
	public boolean clearError() {
		return _implValid.clearError();
	}

	/**
	 * エラーメッセージを設定する。
	 * @param errmsg	設定するエラーメッセージ、もしくは <tt>null</tt>
	 * @return	状態が変更された場合は <tt>true</tt>
	 */
	@Override
	public boolean setError(String errmsg) {
		return _implValid.setError(errmsg);
	}

	/**
	 * エラー要因を設定する。
	 * @param cause	設定するエラー要因、もしくは <tt>null</tt>
	 * @return	状態が変更された場合は <tt>true</tt>
	 */
	@Override
	public boolean setError(Throwable cause) {
		return _implValid.setError(cause);
	}

	/**
	 * エラーメッセージとエラー要因を設定する。
	 * @param errmsg	設定するエラーメッセージ、もしくは <tt>null</tt>
	 * @param cause		設定するエラー要因、もしくは <tt>null</tt>
	 * @return	状態が変更された場合は <tt>true</tt>
	 */
	@Override
	public boolean setError(String errmsg, Throwable cause) {
		return _implValid.setError(errmsg, cause);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
