/*
 * @(#)APrimFloat.java	1.50	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type.prim;

import ssac.aadlc.analysis.type.AADLJavaClass;
import ssac.aadlc.analysis.type.AADLPrimitive;

/**
 * AADL : Float
 * 
 * @version 1.50	2010/09/27
 * @since 1.50
 */
public class APrimFloat extends AADLJavaClass implements AADLPrimitive
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final APrimFloat instance = new APrimFloat();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected APrimFloat() {
		super("Float", java.lang.Float.class);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
}
