/*
 * @(#)APrimShort.java	1.50	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type.prim;

import ssac.aadlc.analysis.type.AADLJavaClass;
import ssac.aadlc.analysis.type.AADLPrimitive;

/**
 * AADL : Short
 * 
 * @version 1.50	2010/09/27
 * @since 1.50
 */
public class APrimShort extends AADLJavaClass implements AADLPrimitive
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final APrimShort instance = new APrimShort();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected APrimShort() {
		super("Short", java.lang.Short.class);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
}
