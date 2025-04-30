/*
 * @(#)APrimDtBasePatternSet.java	1.40	2010/02/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type.prim;

import ssac.aadlc.analysis.type.AADLPrimitive;
import ssac.aadlc.analysis.type.AADLSetType;

/**
 * AADL : DtBasePatternSet
 * 
 * @version 1.40	2010/02/19
 * @since 1.40
 */
public class APrimDtBasePatternSet extends AADLSetType implements AADLPrimitive
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final APrimDtBasePatternSet instance = new APrimDtBasePatternSet();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected APrimDtBasePatternSet() {
		super("DtBasePatternSet", dtalge.DtBasePatternSet.class, APrimDtBasePattern.instance);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
}
