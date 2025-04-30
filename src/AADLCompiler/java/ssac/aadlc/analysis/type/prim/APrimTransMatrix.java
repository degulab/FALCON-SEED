/*
 * @(#)APrimTransMatrix.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type.prim;

import ssac.aadlc.analysis.type.AADLJavaClass;
import ssac.aadlc.analysis.type.AADLPrimitive;

/**
 * AADL : TransMatrix
 * 
 * @version 1.00	2007/11/29
 */
public class APrimTransMatrix extends AADLJavaClass implements AADLPrimitive
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final APrimTransMatrix instance = new APrimTransMatrix();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected APrimTransMatrix() {
		super("TransMatrix", exalge2.TransMatrix.class);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
}
