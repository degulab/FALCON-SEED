/*
 * @(#)ACodeMathContext.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.codegen;

import ssac.aadlc.analysis.type.AADLJavaClass;

/**
 * MathContext を示す、特殊なコードオブジェクト
 * 
 * @version 1.00	2007/11/29
 */
public final class ACodeMathContext extends ACodeObject
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
	
	static protected ACodeMathContext getMathContext(int lineNo) {
		ACodeMathContext retCode = new ACodeMathContext();
		retCode.genCode(lineNo);
		return retCode;
	}
	
	protected ACodeMathContext() {
		super(new AADLJavaClass(java.math.MathContext.class));
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	private void genCode(int lineNo) {
		this.jlb.appendLine(lineNo, "java.math.MathContext.DECIMAL128");
	}
}
