/*
 * @(#)APrimDtalge.java	1.40	2010/02/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type.prim;

import ssac.aadlc.analysis.type.AADLJavaClass;
import ssac.aadlc.analysis.type.AADLPrimitive;

/**
 * AADL : Dtalge
 * 
 * @version 1.40	2010/02/19
 * @since 1.40
 */
public class APrimDtalge extends AADLJavaClass implements AADLPrimitive
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final APrimDtalge instance = new APrimDtalge();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected APrimDtalge() {
		super("Dtalge", dtalge.Dtalge.class);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

}
