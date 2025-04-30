/*
 * @(#)APrimExBasePatternSet.java	1.40	2010/02/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)APrimExBasePatternSet.java	1.30	2009/12/02
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type.prim;

import ssac.aadlc.analysis.type.AADLPrimitive;
import ssac.aadlc.analysis.type.AADLSetType;

/**
 * AADL : ExBasePatternSet
 * 
 * @version 1.40	2010/02/19
 * 
 * @since 1.30
 */
public class APrimExBasePatternSet extends AADLSetType implements AADLPrimitive
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final APrimExBasePatternSet instance = new APrimExBasePatternSet();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected APrimExBasePatternSet() {
		//super("ExBasePatternSet", exalge2.ExBasePatternSet.class, new APrimExBasePattern());
		super("ExBasePatternSet", exalge2.ExBasePatternSet.class, APrimExBasePattern.instance);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
}
