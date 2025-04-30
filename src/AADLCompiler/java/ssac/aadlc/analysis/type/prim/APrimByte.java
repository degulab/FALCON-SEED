/*
 * @(#)APrimByte.java	1.50	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type.prim;

import ssac.aadlc.analysis.type.AADLJavaClass;
import ssac.aadlc.analysis.type.AADLPrimitive;

/**
 * AADL : Byte
 * 
 * @version 1.50	2010/09/27
 * @since 1.50
 */
public class APrimByte extends AADLJavaClass implements AADLPrimitive
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final APrimByte instance = new APrimByte();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected APrimByte() {
		super("Byte", java.lang.Byte.class);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
}
