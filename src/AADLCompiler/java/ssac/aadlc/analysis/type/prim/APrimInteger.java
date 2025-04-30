/*
 * @(#)APrimInteger.java	1.50	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type.prim;

import ssac.aadlc.analysis.type.AADLJavaClass;
import ssac.aadlc.analysis.type.AADLPrimitive;

/**
 * AADL : Integer
 * 
 * @version 1.50	2010/09/27
 * @since 1.50
 */
public class APrimInteger extends AADLJavaClass implements AADLPrimitive
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final APrimInteger instance = new APrimInteger();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected APrimInteger() {
		super("Integer", java.lang.Integer.class);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
}
