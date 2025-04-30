/*
 * @(#)APrimDouble.java	1.50	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type.prim;

import ssac.aadlc.analysis.type.AADLJavaClass;
import ssac.aadlc.analysis.type.AADLPrimitive;

/**
 * AADL : Double
 * 
 * @version 1.50	2010/09/27
 * @since 1.50
 */
public class APrimDouble extends AADLJavaClass implements AADLPrimitive
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final APrimDouble instance = new APrimDouble();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected APrimDouble() {
		super("Double", java.lang.Double.class);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
}
