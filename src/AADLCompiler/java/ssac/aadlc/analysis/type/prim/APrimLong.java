/*
 * @(#)APrimLong.java	1.50	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type.prim;

import ssac.aadlc.analysis.type.AADLJavaClass;
import ssac.aadlc.analysis.type.AADLPrimitive;

/**
 * AADL : Long
 * 
 * @version 1.50	2010/09/27
 * @since 1.50
 */
public class APrimLong extends AADLJavaClass implements AADLPrimitive
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final APrimLong instance = new APrimLong();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected APrimLong() {
		super("Long", java.lang.Long.class);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
}
