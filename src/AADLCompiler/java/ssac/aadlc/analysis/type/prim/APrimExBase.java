/*
 * @(#)APrimExBase.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type.prim;

import ssac.aadlc.analysis.type.AADLJavaClass;
import ssac.aadlc.analysis.type.AADLPrimitive;

/**
 * AADL : ExBase
 * 
 * @version 1.00	2007/11/29
 */
public class APrimExBase extends AADLJavaClass implements AADLPrimitive
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final APrimExBase instance = new APrimExBase();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected APrimExBase() {
		super("ExBase", exalge2.ExBase.class);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

}
