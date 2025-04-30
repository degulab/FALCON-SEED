/*
 * @(#)APrimTransTable.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type.prim;

import ssac.aadlc.analysis.type.AADLJavaClass;
import ssac.aadlc.analysis.type.AADLPrimitive;

/**
 * AADL : TransTable
 * 
 * @version 1.00	2007/11/29
 */
public class APrimTransTable extends AADLJavaClass implements AADLPrimitive
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final APrimTransTable instance = new APrimTransTable();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected APrimTransTable() {
		super("TransTable", exalge2.TransTable.class);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

}
