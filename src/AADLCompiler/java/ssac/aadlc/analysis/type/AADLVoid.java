/*
 * @(#)AADLVoid.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type;



/**
 * void 型 を表す、特殊なデータ型
 * 
 * 
 * @version 1.00	2007/11/29
 */
public final class AADLVoid extends AADLType
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final AADLVoid instance = new AADLVoid();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected AADLVoid() {
		super("void", void.class);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	// AADL operator
	// void型は、常にサポートなし
	public String getOperatorMethodName(int opType) {
		return NO_METHOD;
	}
}
