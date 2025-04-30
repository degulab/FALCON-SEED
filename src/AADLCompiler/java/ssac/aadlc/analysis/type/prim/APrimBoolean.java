/*
 * @(#)APrimBoolean.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type.prim;

import ssac.aadlc.analysis.type.AADLJavaClass;
import ssac.aadlc.analysis.type.AADLPrimitive;

/**
 * AADL : Boolean
 * 
 * @version 1.00	2007/11/29
 */
public class APrimBoolean extends AADLJavaClass implements AADLPrimitive
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final APrimBoolean instance = new APrimBoolean();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected APrimBoolean() {
		super("Boolean", java.lang.Boolean.class);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

}
