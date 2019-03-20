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
 * @(#)ACodeFuncCall.java	1.70	2011/06/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeFuncCall.java	1.40	2010/02/23
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeFuncCall.java	1.30	2009/12/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeFuncCall.java	1.22	2009/03/10
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeFuncCall.java	1.10	2008/05/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeFuncCall.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.codegen.expression;

import java.util.List;

import org.antlr.runtime.Token;

import ssac.aadlc.AADLMessage;
import ssac.aadlc.CompileException;
import ssac.aadlc.analysis.AADLAnalyzer;
import ssac.aadlc.analysis.type.AADLObject;
import ssac.aadlc.analysis.type.AADLType;
import ssac.aadlc.analysis.type.AADLTypeManager;
import ssac.aadlc.analysis.type.AADLVoid;
import ssac.aadlc.analysis.type.func.AADLFuncType;
import ssac.aadlc.analysis.type.prim.APrimDecimal;
import ssac.aadlc.codegen.ACodeObject;
import ssac.aadlc.codegen.define.ACodeDataType;

/**
 * AADL 関数呼び出し
 *
 * 
 * @version 1.70	2011/06/29
 */
public class ACodeFuncCall extends ACodeObject
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static private final String CAST_JAVAPRIM_PREFIX = "to_";
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected ACodeFuncCall() {
		super();
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	// cast[type](exp)
	static public ACodeFuncCall buildDirectCast(AADLAnalyzer analyzer, Token astToken, ACodeDataType type, ACodeObject exp) {
		ACodeFuncCall retCode = new ACodeFuncCall();
		if (type != null && exp != null) {
			retCode.genDirectCast(analyzer, astToken, type, exp);
		}
		return retCode;
	}

	// typeof[type](exp)
	static public ACodeFuncCall buildInstanceOf(AADLAnalyzer analyzer, Token astToken, ACodeDataType type, ACodeObject exp) {
		ACodeFuncCall retCode = new ACodeFuncCall();
		if (type != null && exp != null) {
			retCode.genInstanceOf(analyzer, astToken, type, exp);
		}
		return retCode;
	}
	
	// func[...](...)
	static public ACodeFuncCall buildSpecialCall(AADLAnalyzer analyzer, Token astToken,
														ACodeObject target, List<ACodeObject> params)
	{
		ACodeFuncCall retCode = new ACodeFuncCall();
		if (astToken != null && target != null) {
			ACodeObject[] objs;
			if (params != null)
				objs = params.toArray(new ACodeObject[params.size()]);
			else
				objs = new ACodeObject[0];
			retCode.genSpecialFuncCall(analyzer, astToken, target, objs);
		}
		return retCode;
	}
	
	// func(...)
	static public ACodeFuncCall buildNormalCall(AADLAnalyzer analyzer, Token astToken,
														List<ACodeObject> params)
	{
		ACodeFuncCall retCode = new ACodeFuncCall();
		if (astToken != null) {
			ACodeObject[] objs;
			if (params != null)
				objs = params.toArray(new ACodeObject[params.size()]);
			else
				objs = new ACodeObject[0];
			retCode.genNormalFuncCall(analyzer, astToken, objs);
		}
		return retCode;
	}
	
	// package.package...package::func(...)
	static public ACodeFuncCall buildModuleMethodCall(AADLAnalyzer analyzer, Token astToken, ACodeDataType target, List<ACodeObject> params) {
		ACodeFuncCall retCode = new ACodeFuncCall();
		if (astToken != null && target != null) {
			ACodeObject[] objs;
			if (params != null)
				objs = params.toArray(new ACodeObject[params.size()]);
			else
				objs = new ACodeObject[0];
			retCode.genModuleMethodCall(analyzer, astToken, target, objs);
		}
		return retCode;
	}
	
	// expression.func(...)
	static public ACodeFuncCall buildInstanceMethodCall(AADLAnalyzer analyzer, Token astToken, ACodeObject target, List<ACodeObject> params) {
		ACodeFuncCall retCode = new ACodeFuncCall();
		if (astToken != null && target != null) {
			ACodeObject[] objs;
			if (params != null)
				objs = params.toArray(new ACodeObject[params.size()]);
			else
				objs = new ACodeObject[0];
			retCode.genInstanceMethodCall(analyzer, astToken, target, objs);
		}
		return retCode;
	}
	
	//------------------------------------------------------------
	// Code generators
	//------------------------------------------------------------
	
	// cast[type](exp) 形式の関数呼び出し
	private void genDirectCast(AADLAnalyzer analyzer, Token astToken, ACodeDataType type, ACodeObject exp)
	{
		jlb.add("(", type.getJavaLineBuffer(), ")");
		jlb.add("(", exp.getJavaLineBuffer(), ")");
		this.setType(type.getType());
	}
	
	// typeof[type](exp) 形式の関数呼び出し
	private void genInstanceOf(AADLAnalyzer analyzer, Token astToken, ACodeDataType type, ACodeObject exp)
	{
		// parameters
		AADLType[] paramTypes = new AADLType[1];
		if (exp != null)
			paramTypes[0] = exp.getType();
		else
			paramTypes[0] = AADLObject.instance;
		
		// 現時点では、まだサポートしない
		String msg = AADLMessage.undefinedSpecialFunction(type.getType().getName(),
				  										astToken.getText(),
				  										AADLMessage.toStringParams(exp));
		throw new CompileException(astToken.getLine(), astToken.getCharPositionInLine(), msg);
	}

	// func[...](...) 形式の関数呼び出し
	private void genSpecialFuncCall(AADLAnalyzer analyzer, Token astToken,
									  ACodeObject target, ACodeObject...paramCodes)
	{
		String funcName = astToken.getText();
		if (funcName.equals("proj")) {
			// proj -> projection
			funcName = "projection";
		}
		else if (funcName.equals("patternProj")) {
			// patternProj -> patternProjection
			funcName = "patternProjection";
		}
		else if (funcName.equals("generalProj")) {
			// generalProj -> generalProjection
			funcName = "generalProjection";
		}
		else if (funcName.equals("inverseProj")) {
			// inverseProj -> inverseProjection
			funcName = "inverseProjection";
		}
		else if (funcName.equals("nullProj")) {
			// nullProj -> nullProjection
			funcName = "nullProjection";
		}
		else if (funcName.equals("nonullProj")) {
			// nonullProj -> nonullProjection
			funcName = "nonullProjection";
		}
		else if (funcName.equals("oneValueProj")) {
			// oneValueProj -> oneValueProjection
			funcName = "oneValueProjection";
		}
		else if (funcName.equals("valuesProj")) {
			// valuesProj -> valuesProjection
			funcName = "valuesProjection";
		}
		this.jlb.add(target.getJavaLineBuffer());
		this.setType(target.getType());

		try {
			ACodeObject retCode = this.buildMemberMethodCall(analyzer, astToken, funcName, paramCodes);
			this.jlb.clear();
			this.jlb.add(retCode.getJavaLineBuffer());
			this.setType(retCode.getType());
		}
		catch (Exception ex) {
			String msg = AADLMessage.undefinedSpecialFunction(target.getType().getName(),
															  astToken.getText(),
															  AADLMessage.toStringParams(paramCodes));
			throw new CompileException(astToken.getLine(), astToken.getCharPositionInLine(), msg, ex);
		}
	}

	// func(...) 形式の関数呼び出し
	private void genNormalFuncCall(AADLAnalyzer analyzer, Token astToken, ACodeObject...paramCodes) {
		// parameters
		AADLType[] paramTypes = new AADLType[paramCodes.length];
		for (int i = 0; i < paramTypes.length; i++) {
			if (paramCodes[i] != null)
				paramTypes[i] = paramCodes[i].getType();
			else
				paramTypes[i] = AADLObject.instance;
		}
		
		// method name
		String methodName = astToken.getText();
		
		// 関数取得
		AADLFuncType funcType = analyzer.getFuncManager().getCallable(methodName, paramTypes);
		if (funcType != null) {
			// ユーザー定義関数/組み込み関数
			if (paramCodes.length > 0) {
				// 引数あり
				this.jlb.appendLine(null, astToken.getText(), "(", astToken.getLine());
				//--- first arg
				if (paramCodes[0] != null) {
					this.jlb.add(paramCodes[0].getJavaLineBuffer());
				}
				//--- next arg
				for (int i = 1; i < paramCodes.length; i++) {
					if (paramCodes[i] != null) {
						this.jlb.add(",", paramCodes[i].getJavaLineBuffer(), null);
					}
				}
				//--- termination
				this.jlb.catFooterAtLastLine(")");
			}
			else {
				// 引数なし
				this.jlb.appendLine(null, astToken.getText(), "()", astToken.getLine());
			}
			if (funcType.hasReturn()) {
				// 戻り値あり
				setType(funcType.getReturnType());
			}
			else {
				// 戻り値なし
				setType(AADLVoid.instance);
			}
		}
		else if (methodName.startsWith(CAST_JAVAPRIM_PREFIX) && paramTypes.length==1) {
			// cast for Java primitive
			String primname = methodName.substring(CAST_JAVAPRIM_PREFIX.length());
			Class<?> javaprim = null;
			try {
				javaprim = AADLTypeManager.getJavaPrimitiveByName(primname);
			}
			catch (Throwable ex) {
				// なし
				String msg = AADLMessage.undefinedFunction(astToken.getText(),
						  								   AADLMessage.toStringParams(paramCodes));
				throw new CompileException(astToken.getLine(), astToken.getCharPositionInLine(), msg);
			}
			
			// cast
			if (paramTypes[0].isJavaAction()) {
				// そのままキャスト
				AADLType castType = null;
				try {
					castType = AADLTypeManager.getTypeByJavaClass(javaprim);
				}
				catch (Throwable ex) {
					// なし
					String msg = AADLMessage.undefinedFunction(astToken.getText(),
							  								   AADLMessage.toStringParams(paramCodes));
					throw new CompileException(astToken.getLine(), astToken.getCharPositionInLine(), msg);
				}
				//--- gen code
				this.jlb.appendLine(astToken.getLine(), "((", primname, ")");
				this.jlb.add("(", paramCodes[0].getJavaLineBuffer(), "))");
				this.setType(castType);
			}
			else if (APrimDecimal.instance.equals(paramTypes[0])) {
				// Decimal からキャスト
				String castMethodName = primname + "Value";
				this.jlb.add(paramCodes[0].getJavaLineBuffer());
				this.setType(paramCodes[0].getType());
				try {
					ACodeObject retCode = this.buildMemberMethodCall(analyzer, astToken, castMethodName);
					this.jlb.clear();
					this.jlb.add(retCode.getJavaLineBuffer());
					this.setType(retCode.getType());
				}
				catch (Exception ex) {
					// なし
					String msg = AADLMessage.undefinedFunction(astToken.getText(),
							  								   AADLMessage.toStringParams(paramCodes));
					throw new CompileException(astToken.getLine(), astToken.getCharPositionInLine(), msg);
				}
			}
			else {
				// なし
				String msg = AADLMessage.undefinedFunction(astToken.getText(),
						  								   AADLMessage.toStringParams(paramCodes));
				throw new CompileException(astToken.getLine(), astToken.getCharPositionInLine(), msg);
			}
		}
		/*
		else if (paramCodes.length > 0) {
			// その他関数：現時点では、呼び出し不可
			ACodeObject target = paramCodes[0];
			ACodeObject[] params = new ACodeObject[paramCodes.length-1];
			for (int i = 1; i < paramCodes.length; i++) {
				params[i-1] = paramCodes[i];
			}
			this.jlb.add(target.getJavaLineBuffer());
			this.setType(target.getType());
			try {
				ACodeObject retCode = this.buildMemberMethodCall(analyzer, astToken,
																astToken.getText(), params);
				this.jlb.clear();
				this.jlb.add(retCode.getJavaLineBuffer());
				this.setType(retCode.getType());
			}
			catch (Exception ex) {
				String msg = AADLMessage.undefinedFunction(astToken.getText(),
														   AADLMessage.toStringParams(paramCodes));
				throw new CompileException(astToken.getLine(), astToken.getCharPositionInLine(), msg, ex);
			}
		}
		*/
		else {
			// なし
			String msg = AADLMessage.undefinedFunction(astToken.getText(),
					  								   AADLMessage.toStringParams(paramCodes));
			throw new CompileException(astToken.getLine(), astToken.getCharPositionInLine(), msg);
		}
	}

	// package.package...package::func(...) 形式の関数呼び出し
	private void genModuleMethodCall(AADLAnalyzer analyzer, Token astToken,
									  ACodeDataType target, ACodeObject...paramCodes)
	{
		/*
		 * 現在は、サポートしない
		 */
		String msg = AADLMessage.undefinedToken(astToken.getText());
		throw new CompileException(astToken.getLine(), astToken.getCharPositionInLine(), msg);
	}

	// expression.func(...) 形式の関数呼び出し
	private void genInstanceMethodCall(AADLAnalyzer analyzer, Token astToken,
									  ACodeObject target, ACodeObject...paramCodes)
	{
		/*
		 * このメソッドは、public メンバーメソッドの呼び出しのみを行う
		 */
		String funcName = astToken.getText();
		this.jlb.add(target.getJavaLineBuffer());
		this.setType(target.getType());

		try {
			ACodeObject retCode = this.buildMemberMethodCall(analyzer, astToken, funcName, paramCodes);
			this.jlb.clear();
			this.jlb.add(retCode.getJavaLineBuffer());
			this.setType(retCode.getType());
		}
		catch (Exception ex) {
			String msg = AADLMessage.undefinedMemberMethod(target.getType().getName(),
															  astToken.getText(),
															  AADLMessage.toStringParams(paramCodes));
			throw new CompileException(astToken.getLine(), astToken.getCharPositionInLine(), msg, ex);
		}
	}
	
	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
