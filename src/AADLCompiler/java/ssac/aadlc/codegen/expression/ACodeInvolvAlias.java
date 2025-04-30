/*
 * @(#)ACodeInvolvAlias.java	1.30	2009/12/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeInvolvAlias.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.codegen.expression;

import org.antlr.runtime.Token;

import ssac.aadlc.AADLMessage;
import ssac.aadlc.CompileException;
import ssac.aadlc.analysis.AADLAnalyzer;
import ssac.aadlc.analysis.type.AADLType;
import ssac.aadlc.codegen.ACodeObject;
import ssac.aadlc.codegen.define.ACodeDataType;
import ssac.aadlc.codegen.define.ACodeVariable;

/**
 * 内包記法のエイリアス
 *
 * 
 * @version 1.30	2009/12/02
 */
public class ACodeInvolvAlias extends ACodeInvolving
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected ACodeInvolvAlias() {
		super();
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 内包記法のエイリアス宣言
	 */
	static public ACodeInvolvAlias buildAlias(AADLAnalyzer analyzer, Token astName, ACodeDataType dataType, ACodeObject exp) {
		ACodeInvolvAlias retCode = new ACodeInvolvAlias();
		retCode.genAlias(analyzer, astName, dataType, exp);
		return retCode;
	}
	
	//------------------------------------------------------------
	// Code generators
	//------------------------------------------------------------
	
	private void genAlias(AADLAnalyzer analyzer, Token astName, ACodeDataType dataType, ACodeObject exp) {
		// check dataType
		if (dataType != null) {
			//--- 現在は、エラーとする
			String msg = AADLMessage.undefinedToken(dataType.getType().getName());
			throw new CompileException(dataType.getMinLine(), dataType.getMinCharPositionInLine(), msg);
		}
		
		// check exp
		if (exp == null || exp.getType().isJavaAction()) {
			// イテレート元のオブジェクト型が特定できない
			String msg = AADLMessage.unknownDataType();
			throw new CompileException(astName.getLine(), astName.getCharPositionInLine(), msg);
		}
		
		// Variable Definition
		AADLType codeType = exp.getType();
		ACodeVariable codeVar = ACodeVariable.buildVariableDefinition(analyzer, false, astName, codeType);
		
		// create Codes
		jlb.add(null, codeVar.getJavaLineBuffer(), " = ");
		jlb.add(null, exp.getJavaLineBuffer(), ";");
		
		// setup type
		setType(codeType);
	}
	
	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

}
