/*
 * @(#)APrimExBaseSet.java	1.40	2010/02/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)APrimExBaseSet.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type.prim;

import ssac.aadlc.analysis.type.AADLPrimitive;
import ssac.aadlc.analysis.type.AADLSetType;

/**
 * AADL : ExBaseSet
 * 
 * @version 1.40	2010/02/19
 */
public class APrimExBaseSet extends AADLSetType implements AADLPrimitive
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final APrimExBaseSet instance = new APrimExBaseSet();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected APrimExBaseSet() {
		//super("ExBaseSet", exalge2.ExBaseSet.class, new APrimExBase());
		super("ExBaseSet", exalge2.ExBaseSet.class, APrimExBase.instance);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

}
