/*
 * @(#)APrimDtAlgeSet.java	1.40	2010/02/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type.prim;

import ssac.aadlc.analysis.type.AADLListType;
import ssac.aadlc.analysis.type.AADLPrimitive;


/**
 * AADL : DtAlgeSet
 * 
 * @version 1.40	2010/02/19
 * @since 1.40
 */
public class APrimDtAlgeSet extends AADLListType implements AADLPrimitive
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final APrimDtAlgeSet instance = new APrimDtAlgeSet();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected APrimDtAlgeSet() {
		super("DtAlgeSet", dtalge.DtAlgeSet.class, APrimDtalge.instance);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

}
