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
 *  Copyright 2007-2011  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)ACodeObject.java	1.60	2011/03/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeObject.java	1.50	2010/09/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeObject.java	1.40	2010/02/23
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeObject.java	1.22	2009/03/10
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeObject.java	1.10	2008/05/22
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeObject.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.codegen;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.runtime.Token;

import ssac.aadlc.AADLMessage;
import ssac.aadlc.CompileException;
import ssac.aadlc.analysis.AADLAnalyzer;
import ssac.aadlc.analysis.type.AADLCollectionType;
import ssac.aadlc.analysis.type.AADLJavaAction;
import ssac.aadlc.analysis.type.AADLObject;
import ssac.aadlc.analysis.type.AADLType;
import ssac.aadlc.analysis.type.AADLVoid;
import ssac.aadlc.analysis.type.func.AADLFuncType;
import ssac.aadlc.analysis.type.prim.APrimBoolean;
import ssac.aadlc.analysis.type.prim.APrimDecimal;
import ssac.aadlc.analysis.type.prim.APrimDtBase;
import ssac.aadlc.analysis.type.prim.APrimDtalge;
import ssac.aadlc.analysis.type.prim.APrimExBase;
import ssac.aadlc.analysis.type.prim.APrimExalge;
import ssac.aadlc.analysis.type.prim.APrimNull;
import ssac.aadlc.analysis.type.prim.APrimString;
import ssac.aadlc.codegen.define.ACodeDataType;

/**
 * 基本オブジェクトコード
 * 
 * @version 1.60	2011/03/16
 */
public class ACodeObject extends AADLCode
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static private final Pattern patNamedOp = Pattern.compile("\\A%([^%]+)%\\z");
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public ACodeObject() {
		super();
	}
	
	public ACodeObject(AADLType dataType) {
		super(dataType);
	}
	
	public ACodeObject(AADLCode srcCode, AADLType newDataType) {
		super(newDataType);
		this.jlb = new JavaLineBuffer(srcCode.getJavaLineBuffer());
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/*
	 * 単項演算子
	 */

	// !(this)
	public ACodeObject not(AADLAnalyzer analyzer, Token astToken) {
		if (!getType().isJavaAction() && !getType().canBooleanOperation()) {
			// Error
			String msg = AADLMessage.undefinedOperator(astToken.getText(), getType().getName());
			throw new CompileException(astToken.getLine(), astToken.getCharPositionInLine(), msg);
		}
		
		ACodeObject retCode = new ACodeObject(APrimBoolean.instance);
		retCode.jlb.appendLine("!", astToken.getLine());
		retCode.jlb.add("(", this.jlb, ")");
		return retCode;
	}

	// +(this)
	public ACodeObject plus(AADLAnalyzer analyzer, Token astToken) {
		// No supported
		String msg = AADLMessage.undefinedOperator(astToken.getText(), getType().getName());
		throw new CompileException(astToken.getLine(), astToken.getCharPositionInLine(), msg);
	}

	// -(this)
	public ACodeObject negate(AADLAnalyzer analyzer, Token astToken) {
		return buildOperatorMethodCall(analyzer, astToken, "negate");
	}

	// ^(this)
	public ACodeObject hat(AADLAnalyzer analyzer, Token astToken) {
		return buildOperatorMethodCall(analyzer, astToken, "hat");
	}

	// ~(this)
	public ACodeObject bar(AADLAnalyzer analyzer, Token astToken) {
		return buildOperatorMethodCall(analyzer, astToken, "bar");
	}

	/*
	 * 二項演算子
	 */

	/*@@@ deleted by Y.Ishizuka : 2008/05/22
	//@@@ added by Y.Ishizuka : 2008/05/21
	// (this) = (arg)
	public ACodeObject assign(AADLAnalyzer analyzer, Token astToken, ACodeObject arg) {
		//--- 自身が変数参照ではない場合、エラー('='演算子の左側は変数参照でなければならない)
		if (!(this instanceof ACodeVariable)) {
			String msg = AADLMessage.notVariableLeftOfOperator(astToken.getText());
			throw new CompileException(astToken.getLine(), astToken.getCharPositionInLine(), msg);
		}
		
		// 同一型のコード生成
		// Make code
		// 型チェック
		if (this.getType().equals(arg.getType()) || arg.getType().isJavaAction()) {
			// 同じ型、もしくは代入する値がJAVA_ACTIONなら、そのまま代入
			ACodeObject retCode = new ACodeObject(this.getType());
			retCode.jlb.add(this.getJavaLineBuffer());
			retCode.jlb.appendLine(" = ", astToken.getLine());
			retCode.jlb.add("(", arg.getJavaLineBuffer(), ")");
			return retCode;
		}
		//--- コレクション系データ型の要素型チェック
		if (arg.getType() instanceof AADLMapType) {
			//--- Map
			if (this.getType() instanceof AADLMapType) {
				AADLMapType mapVar = (AADLMapType)this.getType();
				AADLMapType mapExp = (AADLMapType)arg.getType();
				if (!mapExp.getKeyType().isInstanceOf(mapVar.getKeyType()) ||
					!mapExp.getValueType().isInstanceOf(mapVar.getValueType()))
				{
					// Not assign
					String msg = AADLMessage.cannotConvert(arg.getType().getName(), this.getType().getName());
					throw new CompileException(arg.getMinLineNo(), msg);
				}
			}
			else {
				// Not assign
				String msg = AADLMessage.cannotConvert(arg.getType().getName(), this.getType().getName());
				throw new CompileException(arg.getMinLineNo(), msg);
			}
		}
		else if (arg.getType() instanceof AADLCollectionType) {
			//--- Collection
			if (this.getType() instanceof AADLCollectionType) {
				AADLCollectionType clVar = (AADLCollectionType)this.getType();
				AADLCollectionType clExp = (AADLCollectionType)arg.getType();
				if (!clExp.getElementType().isInstanceOf(clVar.getElementType())) {
					// Not assign
					String msg = AADLMessage.cannotConvert(arg.getType().getName(), this.getType().getName());
					throw new CompileException(arg.getMinLineNo(), msg);
				}
			}
			else {
				// Not assign
				String msg = AADLMessage.cannotConvert(arg.getType().getName(), this.getType().getName());
				throw new CompileException(arg.getMinLineNo(), msg);
			}
		}
		
		// データ型の代入
		ACodeObject retCode = new ACodeObject(this.getType());
		if (arg.getType().isInstanceOf(this.getType())) {
			// 代入可能
			retCode.jlb.add(this.getJavaLineBuffer());
			retCode.jlb.appendLine(" = ", astToken.getLine());
			retCode.jlb.add("(", arg.getJavaLineBuffer(), ")");
		}
		else if (arg.getType() instanceof AADLCollectionType && this.getType().hasConstructor(arg.getType())) {
			// 変数型が式の戻り値型からの派生なら、コンストラクタ経由で代入可能
			retCode.jlb.add(this.getJavaLineBuffer());
			retCode.jlb.appendLine(" = ", astToken.getLine());
			retCode.jlb.appendLine("new " ,this.getType().getJavaConstructorName(), "(", astToken.getLine());
			retCode.jlb.add(arg.getJavaLineBuffer());
			retCode.jlb.appendLine(")", astToken.getLine());
		}
		else {
			// Not assign
			String msg = AADLMessage.cannotConvert(arg.getType().getName(), this.getType().getName());
			throw new CompileException(arg.getMinLineNo(), msg);
		}
		return retCode;
	}
	//@@@ end of added
	**@@@ end of deleted. */
	
	// (this) + (arg)
	public ACodeObject add(AADLAnalyzer analyzer, Token astToken, ACodeObject arg) {
		if (this.getType().equals(APrimString.instance) || (arg != null && arg.getType().equals(APrimString.instance))) {
			//--- (string + object) or (object + string) --> string
			return buildPureExpression(analyzer, astToken, arg, APrimString.instance);
		}
		else if (this.getType().equals(APrimExalge.instance)) {
			//--- Exalge
			return buildOperatorMethodCall(analyzer, astToken, "plus", arg);
		}
		else if (this.getType().equals(APrimDtalge.instance)) {
			//--- Dtalge
			return buildOperatorMethodCall(analyzer, astToken, "put", arg);
		}
		/*--- 現時点では、コレクション型は許可しない
		else if (this.getType().equals(APrimExAlgeSet.instance) && arg != null && arg.getType().equals(APrimExAlgeSet.instance)) {
			//--- ExAlgeSet + ExAlgeSet --> ExAlgeSet.addition(ExAlgeSet)
			return buildOperatorMethodCall(analyzer, astToken, "addition", arg);
		}
		else if (this.getType().equals(APrimExBaseSet.instance) && arg != null && arg.getType().equals(APrimExBaseSet.instance)) {
			//--- ExBaseSet + ExBaseSet --> ExBaseSet.addition(ExBaseSet)
			return buildOperatorMethodCall(analyzer, astToken, "addition", arg);
		}
		**---*/
		else if (this.getType() instanceof AADLCollectionType) {
			// Error(左オペランドがコレクション型の式は許可しない)
			String msg = AADLMessage.undefinedOperator(astToken.getText(), getType().getName(),
														arg.getType().getName());
			throw new CompileException(astToken.getLine(), astToken.getCharPositionInLine(), msg);
		}
		
		/* --- 旧コード
		if (this.getType().equals(APrimString.instance)) {
			//--- String
			if (arg != null && (arg.getType().equals(APrimString.instance) || arg.getType().isJavaAction())) {
				return buildPureExpression(analyzer, astToken, arg);
			}
		}
		else if (this.getType().equals(APrimExalge.instance)) {
			//--- Exalge
			return buildOperatorMethodCall(analyzer, astToken, "plus", arg);
		}
		*/
		
		// default
		return buildOperatorMethodCall(analyzer, astToken, "add", arg);
	}

	// (this) - (arg)
	public ACodeObject subtract(AADLAnalyzer analyzer, Token astToken, ACodeObject arg) {
		/*--- 現時点では、コレクション型は許可しない
		if (this.getType().equals(APrimExAlgeSet.instance) && arg != null && arg.getType().equals(APrimExAlgeSet.instance)) {
			//--- ExAlgeSet - ExAlgeSet --> ExAlgeSet.subtraction(ExAlgeSet)
			return buildOperatorMethodCall(analyzer, astToken, "subtraction", arg);
		}
		else if (this.getType().equals(APrimExBaseSet.instance) && arg != null && arg.getType().equals(APrimExBaseSet.instance)) {
			//--- ExBaseSet - ExBaseSet --> ExBaseSet.subtraction(ExBaseSet)
			return buildOperatorMethodCall(analyzer, astToken, "subtraction", arg);
		}
		else if (this.getType() instanceof AADLCollectionType) {
			// Error(左オペランドがコレクション型の式は許可しない)
			String msg = AADLMessage.undefinedOperator(astToken.getText(), getType().getName(),
														arg.getType().getName());
			throw new CompileException(astToken.getLine(), astToken.getCharPositionInLine(), msg);
		}
		**---*/
		if (this.getType() instanceof AADLCollectionType) {
			// Error(左オペランドがコレクション型の式は許可しない)
			String msg = AADLMessage.undefinedOperator(astToken.getText(), getType().getName(),
														arg.getType().getName());
			throw new CompileException(astToken.getLine(), astToken.getCharPositionInLine(), msg);
		}
		
		return buildOperatorMethodCall(analyzer, astToken, "subtract", arg);
	}

	// (this) * (arg)
	public ACodeObject multiple(AADLAnalyzer analyzer, Token astToken, ACodeObject arg) {
		if (arg != null && this.getType().equals(APrimDecimal.instance) && arg.getType().equals(APrimExalge.instance)) {
			// Decimal * Exalge -> Exalge * Decimal
			return arg.buildOperatorMethodCall(analyzer, astToken, "multiple", this);
		}
		else if (arg != null && this.getType().equals(APrimExalge.instance)) {
			if (arg.getType().equals(APrimDecimal.instance) || arg.getType().equals(APrimExalge.instance)) {
				// Exalge * Decimal || Exalge * Exalge
				return buildOperatorMethodCall(analyzer, astToken, "multiple", arg);
			} else {
				return buildOperatorMethodCall(analyzer, astToken, "multiply", arg);
			}
		}
		else {
			return buildOperatorMethodCall(analyzer, astToken, "multiply", arg);
		}
	}

	// (this) / (arg)
	public ACodeObject divide(AADLAnalyzer analyzer, Token astToken, ACodeObject arg) {
		if (this.getType().equals(APrimDecimal.instance)) {
			if (arg != null && arg.getType().equals(APrimDecimal.instance)) {
				//--- BigDecimal.devide(arg, MathContext)
				ACodeObject retCode;
				try {
					ACodeMathContext mc = ACodeMathContext.getMathContext(arg.getMaxLineNo());
					retCode = this.buildMemberMethodCall(analyzer, astToken, "divide", arg, mc);
				}
				catch (CompileException ex) {
					// Error : この場合、エラーとしない
					retCode = null;
				}
				if (retCode != null) {
					return retCode;
				}
			}
		}
		// 上記条件に該当しなければ、通常の呼び出し
		return buildOperatorMethodCall(analyzer, astToken, "divide", arg);
	}

	// (this) % (arg)
	public ACodeObject mod(AADLAnalyzer analyzer, Token astToken, ACodeObject arg) {
		return buildOperatorMethodCall(analyzer, astToken, "remainder", arg);
	}

	// (this) @ (arg)
	public ACodeObject cat(AADLAnalyzer analyzer, Token astToken, ACodeObject arg) {
		//@@@ modified by Y.Ishizuka : 2010/02/12
		if (arg == null) {
			// No supported
			String msg = AADLMessage.undefinedOperator(astToken.getText(), getType().getName());
			throw new CompileException(astToken.getLine(), astToken.getCharPositionInLine(), msg);
		}

		if (arg != null) {
			if (this.getType().equals(APrimExBase.instance) || arg.getType().equals(APrimExBase.instance)) {
				// Exalge コンストラクション
				ACodeObject codeExBase = null;
				ACodeObject codeDecimal = null;
				
				if (this.getType().equals(APrimExBase.instance)) {
					if (isAllowedExalgeValueType(arg.getType())) {
						codeExBase = this;
						codeDecimal = arg;
					}
				}
				else if (arg.getType().equals(APrimExBase.instance)) {
					if (isAllowedExalgeValueType(this.getType())) {
						codeExBase = arg;
						codeDecimal = this;
					}
				}
				
				if (codeExBase != null && codeDecimal != null) {
					// Make code
					ACodeObject retCode = new ACodeObject(APrimExalge.instance);
					retCode.jlb.appendLine("new ", APrimExalge.instance.getJavaConstructorName(), "(", astToken.getLine());
					retCode.jlb.add(codeExBase.getJavaLineBuffer());
					retCode.jlb.add(", ", codeDecimal.getJavaLineBuffer(), null);
					retCode.jlb.catFooterAtLastLine(")");
					return retCode;
				}
			}
			else if (this.getType().equals(APrimDtBase.instance) || arg.getType().equals(APrimDtBase.instance)) {
				// Dtalge コンストラクション
				ACodeObject codeDtBase = null;
				ACodeObject codeObject = null;
				
				if (this.getType().equals(APrimDtBase.instance)) {
					if (isAllowedDtalgeValueType(arg.getType())) {
						codeDtBase = this;
						codeObject = arg;
					}
				}
				else if (arg.getType().equals(APrimDtBase.instance)) {
					if (isAllowedDtalgeValueType(this.getType())) {
						codeDtBase = arg;
						codeObject = this;
					}
				}
				
				if (codeDtBase != null && codeObject != null) {
					// Make code
					ACodeObject retCode = new ACodeObject(APrimDtalge.instance);
					retCode.jlb.appendLine("new ", APrimDtalge.instance.getJavaConstructorName(), "(", astToken.getLine());
					retCode.jlb.add(codeDtBase.getJavaLineBuffer());
					retCode.jlb.add(", ", codeObject.getJavaLineBuffer(), null);
					retCode.jlb.catFooterAtLastLine(")");
					return retCode;
				}
			}
		}
		
		// No supported
		String msg = AADLMessage.undefinedOperator(astToken.getText(), getType().getName());
		throw new CompileException(astToken.getLine(), astToken.getCharPositionInLine(), msg);
		/*@@@ old codes : 2010/02/12
		ACodeObject codeExBase = null;
		ACodeObject codeDecimal = null;
		// Exalge, Dtalge のコンストラクトのみ
		if (arg != null) {
			if (this.getType().equals(APrimExBase.instance)) {
				if (arg.getType().equals(APrimDecimal.instance) || arg.getType().isJavaAction()) {
					codeExBase = this;
					codeDecimal = arg;
				}
			}
			else if (this.getType().equals(APrimDecimal.instance)) {
				if (arg.getType().equals(APrimExBase.instance) || arg.getType().isJavaAction()) {
					codeExBase = arg;
					codeDecimal = this;
				}
			}
			else if (this.getType().isJavaAction()) {
				if (arg.getType().equals(APrimExBase.instance)) {
					codeDecimal = this;
					codeExBase = arg;
				}
				else if (arg.getType().equals(APrimDecimal.instance)) {
					codeDecimal = arg;
					codeExBase = this;
				}
				else if (arg.getType().isJavaAction()) {
					codeDecimal = this;
					codeExBase = arg;
				}
			}
		}

		// エラーチェック
		if (codeExBase == null || codeDecimal == null) {
			// No supported
			String msg = AADLMessage.undefinedOperator(astToken.getText(), getType().getName());
			throw new CompileException(astToken.getLine(), astToken.getCharPositionInLine(), msg);
		}
		
		// Make code
		ACodeObject retCode = new ACodeObject(APrimExalge.instance);
		retCode.jlb.appendLine("new ", APrimExalge.instance.getJavaConstructorName(), "(", astToken.getLine());
		retCode.jlb.add(codeExBase.getJavaLineBuffer());
		retCode.jlb.add(", ", codeDecimal.getJavaLineBuffer(), null);
		retCode.jlb.catFooterAtLastLine(")");
		return retCode;
		@@@ end of old codes : 2010/02/12 */
		//@@@ end of modified : 2010/02/12
	}
	
	// (this) %name% (arg)
	public ACodeObject namedOperation(AADLAnalyzer analyzer, Token astToken, ACodeObject arg) {
		String strName = getNamedOperatorName(astToken.getText());
		return buildOperatorMethodCall(analyzer, astToken, strName, arg);
	}

	// (this) instanceof (type)
	public ACodeObject instanceofOperation(AADLAnalyzer analyzer, Token astToken, ACodeDataType type) {
		// 現時点では、まだサポートしない
		String msg = AADLMessage.undefinedOperator(astToken.getText(), getType().getName(), type.getType().getName());
		throw new CompileException(astToken.getLine(), astToken.getCharPositionInLine(), msg);
	}

	// (this) == (arg)
	public ACodeObject equal(AADLAnalyzer analyzer, Token astToken, ACodeObject arg) {
		ACodeObject retCode = new ACodeObject(APrimBoolean.instance);

		//@@@ modified by Y.Ishizuka : 2011.03.16
		retCode.jlb.appendLine(astToken.getLine(), "isEqual(");
		retCode.jlb.add("(", this.jlb, "),");
		if (arg != null) {
			retCode.jlb.add("(", arg.getJavaLineBuffer(), "))");
		}
		else {
			retCode.jlb.catFooterAtLastLine("null)");
		}
		/*--- old codes : 2011.03.16 ---
		if (!getType().isJavaAction() && getType().canCompareToOperation()) {
			// use Comparable.compareTo()
			retCode.jlb.add("(", this.jlb, ")");
			retCode.jlb.appendLine(".compareTo", astToken.getLine());
			if (arg != null) {
				retCode.jlb.add("(", arg.getJavaLineBuffer(), ")");
			}
			else {
				retCode.jlb.catFooterAtLastLine("(null)");
			}
			retCode.jlb.appendLine(" == 0", astToken.getLine());
		}
		else {
			// use Object.equals()
			retCode.jlb.add("(", this.jlb, ")");
			retCode.jlb.appendLine(".equals", astToken.getLine());
			if (arg != null) {
				retCode.jlb.add("(", arg.getJavaLineBuffer(), ")");
			}
			else {
				retCode.jlb.catFooterAtLastLine("(null)");
			}
		}
		/*--- end of old codes : 2011.03.16 ---*/
		//@@@ end of modified : 2011.03.16
		
		return retCode;
	}

	// (this) != (arg)
	public ACodeObject notEqual(AADLAnalyzer analyzer, Token astToken, ACodeObject arg) {
		ACodeObject retCode = new ACodeObject(APrimBoolean.instance);
		
		//@@@ modified by Y.Ishizuka : 2011.03.16
		retCode.jlb.appendLine(astToken.getLine(), "!isEqual(");
		retCode.jlb.add("(", this.jlb, "),");
		if (arg != null) {
			retCode.jlb.add("(", arg.getJavaLineBuffer(), "))");
		}
		else {
			retCode.jlb.catFooterAtLastLine("null)");
		}
		/*--- old codes : 2011.03.16 ---
		if (!getType().isJavaAction() && getType().canCompareToOperation()) {
			// use Comparable.compareTo()
			retCode.jlb.add("(", this.jlb, ")");
			retCode.jlb.appendLine(".compareTo", astToken.getLine());
			if (arg != null) {
				retCode.jlb.add("(", arg.getJavaLineBuffer(), ")");
			}
			else {
				retCode.jlb.catFooterAtLastLine("(null)");
			}
			retCode.jlb.appendLine(" != 0", astToken.getLine());
		}
		else {
			// use Object.equals()
			retCode.jlb.appendLine("!", astToken.getLine());
			retCode.jlb.add("((", this.jlb, ")");
			retCode.jlb.appendLine(".equals", astToken.getLine());
			if (arg != null) {
				retCode.jlb.add("(", arg.getJavaLineBuffer(), "))");
			}
			else {
				retCode.jlb.catFooterAtLastLine("(null)");
			}
		}
		/*--- end of old codes : 2011.03.16 ---*/
		//@@@ end of modified : 2011.03.16
		
		return retCode;
	}

	// (this) <, > , <=, >= (arg)
	public ACodeObject compare(AADLAnalyzer analyzer, Token astToken, ACodeObject arg) {
		if (!getType().isJavaAction() && !getType().canCompareToOperation()) {
			// Error
			String msg;
			if (arg != null) {
				msg = AADLMessage.undefinedOperator(astToken.getText(), getType().getName(),
														arg.getType().getName());
			} else {
				msg = AADLMessage.undefinedOperator(astToken.getText(), getType().getName(),
						"null");
			}
			throw new CompileException(astToken.getLine(), astToken.getCharPositionInLine(), msg);
		}
		
		ACodeObject retCode = new ACodeObject(APrimBoolean.instance);
		retCode.jlb.add("(", this.jlb, ")");
		retCode.jlb.appendLine(".compareTo", astToken.getLine());
		if (arg != null) {
			retCode.jlb.add("(", arg.getJavaLineBuffer(), ")");
		}
		else {
			retCode.jlb.catFooterAtLastLine("(null)");
		}
		retCode.jlb.appendLine(" ", astToken.getText(), " 0", astToken.getLine());
		return retCode;
	}
	
	// (this) &&, || (arg)
	public ACodeObject conditionalAndOr(AADLAnalyzer analyzer, Token astToken, ACodeObject arg) {
		if (arg != null && !getType().isJavaAction() && !arg.getType().isJavaAction()) {
			// Check (this) is boolean?
			if (!getType().canBooleanOperation()) {
				// Error
				String msg = AADLMessage.undefinedOperator(astToken.getText(), getType().getName(),
															arg.getType().getName());
				throw new CompileException(astToken.getLine(), astToken.getCharPositionInLine(), msg);
			}
			// Check (arg) is boolean?
			if (!arg.getType().canBooleanOperation()) {
				// Error
				String msg = AADLMessage.undefinedOperator(astToken.getText(), getType().getName(),
															arg.getType().getName());
				throw new CompileException(astToken.getLine(), astToken.getCharPositionInLine(), msg);
			}
		}
		
		ACodeObject retCode = new ACodeObject(APrimBoolean.instance);
		retCode.jlb.add("(", this.jlb, ")");
		retCode.jlb.appendLine(" ", astToken.getText(), " ", astToken.getLine());
		if (arg != null) {
			retCode.jlb.add("(", arg.getJavaLineBuffer(), ")");
		}
		else {
			retCode.jlb.catFooterAtLastLine("(null)");
		}
		return retCode;
	}

	/*
	 * 条件演算子
	 */

	// (this) ? (arg1) : (arg2)
	public ACodeObject conditionalAssign(AADLAnalyzer analyzer, Token astToken, ACodeObject arg1, ACodeObject arg2) {
		// No supported
		String msg = AADLMessage.undefinedToken(astToken.getText());
		throw new CompileException(astToken.getLine(), astToken.getCharPositionInLine(), msg);
	}
	
	/*
	 * セレクタ
	 */

	// class public field
	public ACodeObject memberField(AADLAnalyzer analyzer, Token astToken)
	{
		return this.buildMemberFieldRef(analyzer, astToken, astToken.getText());
	}
	
	// class public method
	public ACodeObject memberMethod(AADLAnalyzer analyzer, Token astToken, ACodeObject...params)
	{
		return this.buildMemberMethodCall(analyzer, astToken, astToken.getText(), params);
	}
	
	public ACodeObject memberMethod(AADLAnalyzer analyzer, Token astToken, List<ACodeObject> params) {
		return memberMethod(analyzer, astToken, params.toArray(new ACodeObject[params.size()]));
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	private boolean isAllowedExalgeValueType(AADLType type) {
		if (APrimNull.instance.equals(type)) {
			return true;
		}
		else if (type.isJavaAction()) {
			return true;
		}
		else if (type.isInstanceOf(APrimDecimal.instance)) {
			return true;
		}
		
		// not allowed
		return false;
	}
	
	private boolean isAllowedDtalgeValueType(AADLType type) {
		if (APrimNull.instance.equals(type)) {
			return true;
		}
		else if (type.isJavaAction()) {
			return true;
		}
		else if (type.isInstanceOf(APrimString.instance)) {
			return true;
		}
		else if (type.isInstanceOf(APrimDecimal.instance)) {
			return true;
		}
		else if (type.isInstanceOf(APrimBoolean.instance)) {
			return true;
		}
		else if (AADLObject.instance.equals(type)) {
			return true;
		}
		
		// not allowed
		return false;
	}

	// %...% から、%を除いた文字列を取得する
	protected String getNamedOperatorName(String strToken) {
		String retName = "";
		Matcher mc = patNamedOp.matcher(strToken);
		if (mc.matches() && mc.groupCount() > 0) {
			retName = mc.group(1);
		}
		return retName;
	}

	// 演算子をそのまま使用する。データ型は指定する。
	// modified by Y.Ishizuka : 2008/05/18
	protected ACodeObject buildPureExpression(AADLAnalyzer analyzer, Token astToken, ACodeObject arg, AADLType dataType) {
		ACodeObject retCode = (ACodeObject)this.clone();
		retCode.jlb.encloseParen();
		retCode.jlb.appendLine(" ", astToken.getText(), " ", astToken.getLine());
		if (arg != null) {
			retCode.jlb.add("(", arg.getJavaLineBuffer(), ")");
		}
		else {
			retCode.jlb.catFooterAtLastLine("(null)");
		}
		retCode.setType(dataType);
		return retCode;
	}
	/*↓↓↓ old codes.
	// 演算子をそのまま使用する。データ型は変わらない
	protected ACodeObject buildPureExpression(AADLAnalyzer analyzer, Token astToken, ACodeObject arg) {
		ACodeObject retCode = (ACodeObject)this.clone();
		retCode.jlb.encloseParen();
		retCode.jlb.appendLine(" ", astToken.getText(), " ", astToken.getLine());
		if (arg != null) {
			retCode.jlb.add("(", arg.getJavaLineBuffer(), ")");
		}
		else {
			retCode.jlb.catFooterAtLastLine("(null)");
		}
		return retCode;
	}
	**↑↑↑ end of old codes. */
	//@@@ end of modified

	/*
	// コレクション用オペレータ
	protected ACodeObject buildCollectionOperatorMethodCall(AADLAnalyzer analyzer, Token astToken, ACodeObject arg)
		throws CompileException
	{
		ACodeObject retCode;
		if (arg == null) {
			// Error
			String msg = AADLMessage.undefinedOperator(astToken.getText(), getType().getName(),
														"null");
			throw new CompileException(astToken.getLine(), astToken.getCharPositionInLine(), msg);
		}

		try {
			if (this.getType().isList()) {
				AADLListType typeList = (AADLListType)this.getType();
				if (typeList.equals(arg.getType())) {
					// List同士のオペレーション
				}
				else if (typeList.getElementType().equals(arg.getType())) {
					// 要素のオペレーション
				}
				else {
					// no supported
					throw new IllegalArgumentException();
				}
			}
			else if (this.getType().isSet()) {
				AADLSetType typeSet = (AADLSetType)this.getType();
				if (typeSet.equals(arg.getType())) {
					// Set同士のオペレーション
				}
				else if (typeSet.getElementType().equals(arg.getType())) {
					// 要素のオペレーション
				}
				else {
					// no supported
					throw new IllegalArgumentException();
				}
			}
			else if (this.getType().isMap()) {
				// no supported
				throw new IllegalArgumentException();
			}
		}
		catch (Throwable ex) {
			// Error
			String msg = AADLMessage.undefinedOperator(astToken.getText(), getType().getName(),
														arg.getType().getName());
			throw new CompileException(astToken.getLine(), astToken.getCharPositionInLine(), msg);
		}
		

		// 同じコレクション型でなければ、エラーとする
		if (!arg.getType().equals(this.getType())) {
			// Error
			String msg = AADLMessage.undefinedOperator(astToken.getText(), getType().getName(),
														arg.getType().getName());
			throw new CompileException(astToken.getLine(), astToken.getCharPositionInLine(), msg);
		}
	}
	*/
	
	protected ACodeObject buildOperatorMethodCall(AADLAnalyzer analyzer, Token astToken, String name)
		throws CompileException
	{
		ACodeObject retCode;
		try {
			retCode = this.buildMemberMethodCall(analyzer, astToken, name);
		}
		catch (CompileException ex) {
			// Error
			String msg = AADLMessage.undefinedOperator(astToken.getText(), getType().getName());
			throw new CompileException(astToken.getLine(), astToken.getCharPositionInLine(), msg);
		}
		return retCode;
	}
	
	protected ACodeObject buildOperatorMethodCall(AADLAnalyzer analyzer, Token astToken,
													String name, ACodeObject arg)
		throws CompileException
	{
		ACodeObject retCode;
		if (arg == null) {
			// Error
			String msg = AADLMessage.undefinedOperator(astToken.getText(), getType().getName(),
														"null");
			throw new CompileException(astToken.getLine(), astToken.getCharPositionInLine(), msg);
		}
		try {
			retCode = this.buildMemberMethodCall(analyzer, astToken, name, arg);
		}
		catch (CompileException ex) {
			// Error
			String msg = AADLMessage.undefinedOperator(astToken.getText(), getType().getName(),
														arg.getType().getName());
			throw new CompileException(astToken.getLine(), astToken.getCharPositionInLine(), msg);
		}
		return retCode;
	}
	
	// Class public field 参照
	protected ACodeObject buildMemberFieldRef(AADLAnalyzer analyzer, Token astToken, String name)
		throws CompileException
	{
		ACodeObject retCode = (ACodeObject)this.clone();
		if (this.getType().equals(APrimNull.instance)) {
			// Field not found!
			String msg = AADLMessage.undefinedMemberField(this.getType().getName(), name);
			throw new CompileException(astToken.getLine(), astToken.getCharPositionInLine(), msg);
		}
		else if (!getType().isJavaAction()) {
			// get Field
			AADLType typeField;
			try {
				typeField = this.getType().getField(name);
			}
			catch (Exception ex) {
				// Field not found!
				String msg = AADLMessage.undefinedMemberField(this.getType().getName(), name);
				throw new CompileException(astToken.getLine(), astToken.getCharPositionInLine(), msg, ex);
			}
			retCode.setType(typeField);
		}
		else {
			// Java action
			retCode.setType(AADLJavaAction.instance);
		}

		// make code
		retCode.jlb.encloseParen();
		retCode.jlb.appendLine(".", name, null, astToken.getLine());
		return retCode;
	}

	// Class public method 呼び出し
	protected ACodeObject buildMemberMethodCall(AADLAnalyzer analyzer, Token astToken, String name, ACodeObject...params)
		throws CompileException
	{
		ACodeObject retCode = (ACodeObject)this.clone();
		if (this.getType().equals(APrimNull.instance)) {
			// Method not found!
			String[] argTypes = new String[params.length];
			for (int i = 0; i < params.length; i++) {
				if (params[i] != null)
					argTypes[i] = params[i].getType().getName();
				else
					argTypes[i] = "null";
			}
			String msg = AADLMessage.undefinedMemberMethod(this.getType().getName(),
															name, argTypes);
			throw new CompileException(astToken.getLine(), astToken.getCharPositionInLine(), msg);
		}
		else if (!this.getType().isJavaAction()) {
			// make parameter
			AADLType[] paramTypes = new AADLType[params.length];
			for (int i = 0; i < params.length; i++) {
				if (params[i] != null)
					paramTypes[i] = params[i].getType();
				else
					paramTypes[i] = AADLObject.instance;
			}
			
			// get Method
			AADLFuncType typeMethod;
			try {
				typeMethod = this.getType().getMethod(name, paramTypes);
			}
			catch (Exception ex) {
				// Method not found!
				String[] argTypes = new String[params.length];
				for (int i = 0; i < params.length; i++) {
					if (params[i] != null)
						argTypes[i] = params[i].getType().getName();
					else
						argTypes[i] = "null";
				}
				String msg = AADLMessage.undefinedMemberMethod(this.getType().getName(),
																name, argTypes);
				throw new CompileException(astToken.getLine(), astToken.getCharPositionInLine(), msg, ex);
			}
			
			// Methodから戻り値型情報を取得
			if (typeMethod.hasReturn()) {
				retCode.setType(typeMethod.getReturnType());
			}
			else {
				retCode.setType(AADLVoid.instance);
			}
		}
		else {
			// Java action
			retCode.setType(AADLJavaAction.instance);
		}

		// make code
		retCode.genCodeMemberMethodCall(astToken, name, params);
		return retCode;
	}
	
	// メンバーメソッド呼び出しコード生成
	protected void genCodeMemberMethodCall(Token astToken, String name, ACodeObject...params) {
		this.jlb.encloseParen();
		if (params.length > 0) {
			this.jlb.appendLine(".", name, "(", astToken.getLine());
			//--- first param
			if (params[0] != null) {
				this.jlb.add(params[0].getJavaLineBuffer());
			}
			//--- next params
			for (int i = 1; i < params.length; i++) {
				if (params[i] != null) {
					this.jlb.add(", ", params[i].getJavaLineBuffer(), null);
				}
			}
			//--- termination
			this.jlb.catFooterAtLastLine(")");
		}
		else {
			this.jlb.appendLine(".", name, "()", astToken.getLine());
		}
		
	}
}
