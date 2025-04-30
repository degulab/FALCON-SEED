/*
 * @(#)APrimDtBaseSet.java	1.40	2010/02/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type.prim;

import ssac.aadlc.analysis.type.AADLPrimitive;
import ssac.aadlc.analysis.type.AADLSetType;

/**
 * AADL : DtBaseSet
 * 
 * @version 1.40	2010/02/19
 * @since 1.40
 */
public class APrimDtBaseSet extends AADLSetType implements AADLPrimitive
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final APrimDtBaseSet instance = new APrimDtBaseSet();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected APrimDtBaseSet() {
		super("DtBaseSet", dtalge.DtBaseSet.class, APrimDtBase.instance);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

}
