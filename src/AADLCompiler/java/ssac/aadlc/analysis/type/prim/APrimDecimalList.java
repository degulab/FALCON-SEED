/*
 * @(#)APrimDecimalList.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type.prim;

import ssac.aadlc.analysis.type.AADLListType;
import ssac.aadlc.analysis.type.AADLPrimitive;

/**
 * AADL : DecimalList
 * 
 * @version 1.00	2007/11/29
 */
public class APrimDecimalList extends AADLListType implements AADLPrimitive
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final APrimDecimalList instance = new APrimDecimalList();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected APrimDecimalList() {
		super("DecimalList", java.util.ArrayList.class, APrimDecimal.instance);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

}
