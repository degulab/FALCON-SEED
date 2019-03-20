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
 * @(#)SchemaExpressionEvalException.java	3.2.0	2015/06/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module.schema.exp;

import ssac.aadl.fs.module.schema.SchemaElementValue;

/**
 * 計算実行に失敗したときに通知される例外。
 * 
 * @version 3.2.0
 * @since 3.2.0
 */
public class SchemaExpressionEvalException extends Exception
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 8726353859813784828L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private SchemaExpressionData	_exp;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public SchemaExpressionEvalException(SchemaExpressionData exp) {
		super();
		_exp = exp;
	}
	
	public SchemaExpressionEvalException(SchemaExpressionData exp, String message) {
		super(message);
		_exp = exp;
	}
	
	public SchemaExpressionEvalException(SchemaExpressionData exp, Throwable cause) {
		super(cause);
		_exp = exp;
	}
	
	public SchemaExpressionEvalException(SchemaExpressionData exp, String message, Throwable cause) {
		super(message, cause);
		_exp = exp;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public SchemaExpressionData getExpression() {
		return _exp;
	}
	
	public String formatMessage() {
		StringBuilder sb = new StringBuilder();
		String msg = getLocalizedMessage();
		if (msg != null && !msg.isEmpty()) {
			sb.append(msg);
		} else {
			sb.append("Failed to evaluate of expression");
		}
		if (_exp != null) {
			sb.append(" : ");
			if (_exp.hasLeftOperand()) {
				SchemaElementValue op = _exp.getLeftOperand();
				sb.append('[');
				sb.append(op.toVariableNameString());
				sb.append("](");
				sb.append(op.getValueType());
				sb.append(')');
				sb.append(op.getValue());
			} else {
				sb.append("null");
			}
			String operator = _exp.getOperator();
			if (operator != null) {
				sb.append(" ");
				sb.append(operator);
				sb.append(" ");
			} else {
				sb.append(" (unknown) ");
			}
			if (_exp.hasRightOperand()) {
				SchemaElementValue op = _exp.getRightOperand();
				sb.append('[');
				sb.append(op.toVariableNameString());
				sb.append("](");
				sb.append(op.getValueType());
				sb.append(')');
				sb.append(op.getValue());
			} else {
				sb.append("null");
			}
		}
		return sb.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
