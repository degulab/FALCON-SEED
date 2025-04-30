/*
 * @(#)APrimDtBasePattern.java	1.40	2010/02/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type.prim;

import ssac.aadlc.analysis.type.AADLJavaClass;
import ssac.aadlc.analysis.type.AADLPrimitive;

/**
 * AADL : DtBasePattern
 * 
 * @version 1.40	2010/02/19
 * @since 1.40
 */
public class APrimDtBasePattern extends AADLJavaClass implements AADLPrimitive
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final APrimDtBasePattern instance = new APrimDtBasePattern();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected APrimDtBasePattern() {
		super("DtBasePattern", dtalge.DtBasePattern.class);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

}
