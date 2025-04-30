/*
 * @(#)APrimBooleanList.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type.prim;

import ssac.aadlc.analysis.type.AADLListType;
import ssac.aadlc.analysis.type.AADLPrimitive;

/**
 * AADL : BooleanList
 * 
 * @version 1.00	2007/11/29
 */
public class APrimBooleanList extends AADLListType implements AADLPrimitive
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final APrimBooleanList instance = new APrimBooleanList();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected APrimBooleanList() {
		super("BooleanList", java.util.ArrayList.class, APrimBoolean.instance);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

}
