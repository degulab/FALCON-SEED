/*
 * @(#)APrimDtBase.java	1.40	2010/02/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type.prim;

import ssac.aadlc.analysis.type.AADLJavaClass;
import ssac.aadlc.analysis.type.AADLPrimitive;

/**
 * AADL : DtBase
 * 
 * @version 1.40	2010/02/19
 * @since 1.40
 */
public class APrimDtBase extends AADLJavaClass implements AADLPrimitive
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final APrimDtBase instance = new APrimDtBase();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected APrimDtBase() {
		super("DtBase", dtalge.DtBase.class);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

}
