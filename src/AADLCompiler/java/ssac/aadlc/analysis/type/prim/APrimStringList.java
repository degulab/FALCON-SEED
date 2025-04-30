/*
 * @(#)APrimStringList.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type.prim;

import ssac.aadlc.analysis.type.AADLListType;
import ssac.aadlc.analysis.type.AADLPrimitive;

/**
 * AADL : StringList
 * 
 * @version 1.00	2007/11/29
 */
public class APrimStringList extends AADLListType implements AADLPrimitive
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final APrimStringList instance = new APrimStringList();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected APrimStringList() {
		super("StringList", java.util.ArrayList.class, APrimString.instance);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

}
