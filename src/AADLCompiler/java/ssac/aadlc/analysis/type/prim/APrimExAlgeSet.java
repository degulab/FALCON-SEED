/*
 * @(#)APrimExAlgeSet.java	1.40	2010/02/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)APrimExAlgeSet.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type.prim;

import ssac.aadlc.analysis.type.AADLListType;
import ssac.aadlc.analysis.type.AADLPrimitive;

/**
 * AADL : ExAlgeSet
 * 
 * @version 1.40	2010/02/19
 */
public class APrimExAlgeSet extends AADLListType implements AADLPrimitive
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final APrimExAlgeSet instance = new APrimExAlgeSet();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected APrimExAlgeSet() {
		//super("ExAlgeSet", exalge2.ExAlgeSet.class, new APrimExalge());
		super("ExAlgeSet", exalge2.ExAlgeSet.class, APrimExalge.instance);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

}
