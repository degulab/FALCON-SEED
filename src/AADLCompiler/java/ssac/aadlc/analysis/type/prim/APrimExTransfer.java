/*
 * @(#)APrimExTransfer.java	1.30	2009/12/02
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type.prim;

import ssac.aadlc.analysis.type.AADLJavaClass;
import ssac.aadlc.analysis.type.AADLPrimitive;

/**
 * AADL : ExTransfer
 * 
 * @version 1.30	2009/12/02
 * 
 * @since 1.30
 */
public class APrimExTransfer extends AADLJavaClass implements AADLPrimitive
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final APrimExTransfer instance = new APrimExTransfer();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected APrimExTransfer() {
		super("ExTransfer", exalge2.ExTransfer.class);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
}
