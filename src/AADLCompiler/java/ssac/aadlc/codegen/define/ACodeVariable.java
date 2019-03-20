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
 *  Copyright 2007-2009  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)ACodeVariable.java	1.30	2009/12/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeVariable.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.codegen.define;

import org.antlr.runtime.Token;

import ssac.aadlc.AADLMessage;
import ssac.aadlc.CompileException;
import ssac.aadlc.analysis.AADLAnalyzer;
import ssac.aadlc.analysis.AADLKeyword;
import ssac.aadlc.analysis.AADLSymbol;
import ssac.aadlc.analysis.type.AADLObject;
import ssac.aadlc.analysis.type.AADLType;
import ssac.aadlc.codegen.AADLCode;
import ssac.aadlc.codegen.ACodeObject;

/**
 * AADL 変数宣言
 * 
 * @version 1.30	2009/12/02
 */
public class ACodeVariable extends ACodeObject
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private Token varNameToken = null;
	private AADLSymbol varSymbol = null;
	private ACodeVariable varRef = null;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected ACodeVariable() {
		super();
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	// 変数宣言
	static public ACodeVariable buildValiableDefinition(AADLAnalyzer analyzer, boolean isConst, Token astName, ACodeObject dataType) {
		ACodeVariable retCode = new ACodeVariable();
		retCode.genVariableDefinition(analyzer, isConst, astName, dataType);
		return retCode;
	}

	// 変数宣言
	static public ACodeVariable buildVariableDefinition(AADLAnalyzer analyzer, boolean isConst, Token astName, AADLType dataType) {
		ACodeVariable retCode = new ACodeVariable();
		retCode.genVariableDefinition(analyzer, isConst, astName, dataType);
		return retCode;
	}

	// 変数参照
	static public ACodeVariable buildVariableRef(AADLAnalyzer analyzer, Token astName) {
		ACodeVariable retCode = new ACodeVariable();
		retCode.genVariableRef(analyzer, astName);
		return retCode;
	}
	
	// 変数参照
	static public ACodeVariable buildVariableRef(AADLAnalyzer analyzer, int lineNo, AADLSymbol symbol) {
		ACodeVariable retCode = new ACodeVariable();
		if (symbol != null) {
			retCode.varNameToken = null;
			retCode.varSymbol = symbol;
			retCode.jlb.appendLine(symbol.getJavaSymbolName(), lineNo);
			retCode.setType(symbol.getType());
		}
		return retCode;
	}
	
	// 変数参照
	static public ACodeVariable buildVariableRef(AADLAnalyzer analyzer, Token astName, AADLSymbol symbol) {
		ACodeVariable retCode = new ACodeVariable();
		if (symbol != null) {
			retCode.varNameToken = astName;
			retCode.varSymbol = symbol;
			retCode.jlb.appendLine(symbol.getJavaSymbolName(), astName.getLine());
			retCode.setType(symbol.getType());
		}
		return retCode;
	}
	
	public boolean hasSymbol() {
		return (this.varSymbol != null);
	}
	
	public AADLSymbol getSymbol() {
		return this.varSymbol;
	}
	
	public boolean hasReferenceCode() {
		return (this.varRef != null);
	}
	
	public ACodeVariable getReferenceCode() {
		return this.varRef;
	}
	
	public Token getVariableNameToken() {
		return this.varNameToken;
	}
	
	//------------------------------------------------------------
	// Code generators
	//------------------------------------------------------------
	
	private void genVariableDefinition(AADLAnalyzer analyzer, boolean isConst, Token astName, AADLCode dataType) {
		// check keyword
		if (AADLKeyword.instance.contains(astName.getText())) {
			String msg = AADLMessage.undefinedToken(astName.getText());
			throw new CompileException(astName.getLine(), astName.getCharPositionInLine(), msg);
		}
		
		// setup variable
		String strVarName = astName.getText();
		AADLType varType;
		if (dataType != null)
			varType = dataType.getType();
		else
			varType = AADLObject.instance;
		
		// regist variable
		try {
			analyzer.setSymbol(isConst, strVarName, varType);
		}
		catch (Exception ex) {
			String msg = AADLMessage.alreadyExistVariable(strVarName);
			throw new CompileException(astName.getLine(), astName.getCharPositionInLine(), msg, ex);
		}
		this.varSymbol = analyzer.getSymbolValue(strVarName);
		this.varNameToken = astName;
		
		// create codes
		if (dataType != null) {
			StringBuilder prefix = new StringBuilder();
			if (analyzer.isParsingDeclarationBlock()) {
				prefix.append("protected ");
			}
			if (isConst) {
				prefix.append("final ");
			}
			
			if (prefix.length() > 0) {
				jlb.add(prefix.toString(), dataType.getJavaLineBuffer(), " ");
			} else {
				jlb.add(null, dataType.getJavaLineBuffer(), " ");
			}
		}
		jlb.appendLine(this.varSymbol.getJavaSymbolName(), astName.getLine());
		
		// setup type
		setType(varType);
		
		// generate reference code
		if (analyzer.isParsingDeclarationBlock()) {
			this.varRef = buildVariableRef(analyzer, astName, this.varSymbol);
		}
	}
	
	private void genVariableDefinition(AADLAnalyzer analyzer, boolean isConst, Token astName, AADLType dataType) {
		// check keyword
		if (AADLKeyword.instance.contains(astName.getText())) {
			String msg = AADLMessage.undefinedToken(astName.getText());
			throw new CompileException(astName.getLine(), astName.getCharPositionInLine(), msg);
		}
		
		// setup variable
		String strVarName = astName.getText();
		AADLType varType;
		if (dataType != null)
			varType = dataType;
		else
			varType = AADLObject.instance;
		
		// regist variable
		try {
			analyzer.setSymbol(isConst, strVarName, varType);
		}
		catch (Exception ex) {
			String msg = AADLMessage.alreadyExistVariable(strVarName);
			throw new CompileException(astName.getLine(), astName.getCharPositionInLine(), msg, ex);
		}
		this.varSymbol = analyzer.getSymbolValue(strVarName);
		this.varNameToken = astName;
		
		// declaration prefix/suffix
		StringBuilder prefix = new StringBuilder();
		if (analyzer.isParsingDeclarationBlock()) {
			prefix.append("protected ");
		}
		if (isConst) {
			prefix.append("final ");
		}
		
		// create codes
		//--- jlb.appendLine(varType.getJavaClassName(), " ", this.varSymbol.getJavaSymbolName(), astName.getLine());
		if (prefix.length() > 0) {
			jlb.appendLine(astName.getLine(), prefix.toString(), varType.getJavaClassName(), " ", this.varSymbol.getJavaSymbolName());
		} else {
			jlb.appendLine(astName.getLine(), varType.getJavaClassName(), " ", this.varSymbol.getJavaSymbolName());
		}
		
		// setup type
		setType(varType);
		
		// generate reference code
		if (analyzer.isParsingDeclarationBlock()) {
			this.varRef = buildVariableRef(analyzer, astName, this.varSymbol);
		}
	}
	
	private void genVariableRef(AADLAnalyzer analyzer, Token astName) {
		// Reference variable
		String strVarName = astName.getText();
		this.varSymbol = analyzer.getSymbolValue(strVarName);
		if (this.varSymbol == null) {
			String msg = AADLMessage.undefinedSymbol(strVarName);
			throw new CompileException(astName.getLine(), astName.getCharPositionInLine(), msg);
		}
		this.varNameToken = astName;
		
		// create codes
		jlb.appendLine(this.varSymbol.getJavaSymbolName(), astName.getLine());
		
		// setup type
		setType(this.varSymbol.getType());
	}
	
	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

}
