/*
 * @(#)APrimExalge.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type.prim;

import ssac.aadlc.analysis.type.AADLJavaClass;
import ssac.aadlc.analysis.type.AADLPrimitive;

/**
 * AADL : Exalge
 * 
 * @version 1.00	2007/11/29
 */
public class APrimExalge extends AADLJavaClass implements AADLPrimitive
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final APrimExalge instance = new APrimExalge();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected APrimExalge() {
		super("Exalge", exalge2.Exalge.class);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

}
