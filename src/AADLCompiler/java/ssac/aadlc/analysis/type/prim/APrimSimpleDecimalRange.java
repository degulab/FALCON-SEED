/*
 * @(#)APrimSimpleDecimalRange.java	1.70	2011/06/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type.prim;

import ssac.aadlc.analysis.type.AADLIterableType;
import ssac.aadlc.analysis.type.AADLPrimitive;

/**
 * AADL : SimpleDecimalRange
 * 
 * @author Y.Ishizuka(PieCake,Inc.)
 * @version 1.70	2011/06/29
 */
public class APrimSimpleDecimalRange extends AADLIterableType implements AADLPrimitive
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final APrimSimpleDecimalRange instance = new APrimSimpleDecimalRange();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected APrimSimpleDecimalRange() {
		super("SimpleDecimalRange", ssac.aadl.runtime.util.range.SimpleDecimalRange.class, APrimDecimal.instance);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

}
