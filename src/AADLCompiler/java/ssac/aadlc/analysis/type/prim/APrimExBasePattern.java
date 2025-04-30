/*
 * @(#)APrimExBasePattern.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type.prim;

import ssac.aadlc.analysis.type.AADLJavaClass;
import ssac.aadlc.analysis.type.AADLPrimitive;

/**
 * AADL : ExBasePattern
 * 
 * @version 1.00	2007/11/29
 */
public class APrimExBasePattern extends AADLJavaClass implements AADLPrimitive
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final APrimExBasePattern instance = new APrimExBasePattern();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected APrimExBasePattern() {
		super("ExBasePattern", exalge2.ExBasePattern.class);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

}
