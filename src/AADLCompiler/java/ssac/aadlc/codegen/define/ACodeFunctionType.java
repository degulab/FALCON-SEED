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
 * @(#)ACodeFunctionType.java	1.70	2011/06/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeFunctionType.java	1.20	2008/09/24
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeFunctionType.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.codegen.define;

import java.util.List;

import org.antlr.runtime.Token;

import ssac.aadlc.AADLMessage;
import ssac.aadlc.CompileException;
import ssac.aadlc.analysis.AADLAnalyzer;
import ssac.aadlc.analysis.AADLKeyword;
import ssac.aadlc.analysis.type.AADLObject;
import ssac.aadlc.analysis.type.AADLType;
import ssac.aadlc.analysis.type.AADLVoid;
import ssac.aadlc.analysis.type.func.AADLFuncType;
import ssac.aadlc.codegen.ACodeObject;

/**
 * AADL 関数定義
 * 
 * @version 1.70	2011/06/29
 */
public class ACodeFunctionType extends ACodeObject
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private AADLFuncType funcType = null;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected ACodeFunctionType() {
		super();
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean hasFuncType() {
		return (this.funcType != null);
	}
	
	public AADLFuncType getFuncType() {
		return this.funcType;
	}
	
	static public ACodeFunctionType buildFunctionType(AADLAnalyzer analyzer, Token astName,
														ACodeDataType retParam,
														List<ACodeVariable> funcParams)
	{
		ACodeFunctionType retCode = new ACodeFunctionType();
		retCode.genFunctionType(analyzer, astName, retParam,
								funcParams.toArray(new ACodeVariable[funcParams.size()]));
		return retCode;
	}
	
	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	private void genFunctionType(AADLAnalyzer analyzer, Token astName,
								   ACodeDataType retParam, ACodeVariable...params)
	{
		// Check keyword
		if (AADLKeyword.instance.contains(astName.getText())) {
			String msg = AADLMessage.undefinedToken(astName.getText());
			throw new CompileException(astName.getLine(), astName.getCharPositionInLine(), msg);
		}
		
		// create func type
		AADLType retType = (retParam != null ? retParam.getType() : null);
		AADLType[] paramTypes = new AADLType[params.length];
		for (int i = 0; i < params.length; i++) {
			if (params[i] != null)
				paramTypes[i] = params[i].getType();
			else
				paramTypes[i] = AADLObject.instance;
		}
		this.funcType = new AADLFuncType(astName.getText(), retType, paramTypes);
		
		// gen Codes
		if (retParam != null) {
			//this.jlb.add("public ", retParam.getJavaLineBuffer(), null);
			//--- 'public' や 'protected' などの修飾子は、ACodeModule で指定
			this.jlb.add(retParam.getJavaLineBuffer());
			this.jlb.appendLine(" ", astName.getText(), "(", astName.getLine());
		}
		else {
			// 戻り値なし
			//this.jlb.appendLine("public void ", astName.getText(), "(", astName.getLine());
			//--- 'public' や 'protected' などの修飾子は、ACodeModule で指定
			this.jlb.appendLine("void ", astName.getText(), "(", astName.getLine());
		}
		//--- 引数
		if (params.length > 0) {
			//--- first
			if (params[0] != null) {
				this.jlb.add(params[0].getJavaLineBuffer());
			}
			//--- next
			for (int i = 1; i < params.length; i++) {
				if (params[i] != null) {
					this.jlb.add(",", params[i].getJavaLineBuffer(), null);
				}
			}
		}
		//--- termination
		this.jlb.catFooterAtLastLine(")");
		
		// setup type
		if (retType != null) {
			setType(retType);
		}
		else {
			setType(AADLVoid.instance);
		}
	}
}
