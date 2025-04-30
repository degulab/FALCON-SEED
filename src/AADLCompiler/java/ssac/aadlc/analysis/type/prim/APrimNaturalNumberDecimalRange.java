/*
 * @(#)APrimNaturalNumberDecimalRange.java	1.70	2011/06/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type.prim;

import ssac.aadlc.analysis.type.AADLIterableType;
import ssac.aadlc.analysis.type.AADLPrimitive;

/**
 * AADL : NaturalNumberDecimalRange
 * 
 * @author Y.Ishizuka(PieCake,Inc.)
 * @version 1.70	2011/06/29
 */
public class APrimNaturalNumberDecimalRange extends AADLIterableType implements AADLPrimitive
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final APrimNaturalNumberDecimalRange instance = new APrimNaturalNumberDecimalRange();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected APrimNaturalNumberDecimalRange() {
		super("NaturalNumberDecimalRange", ssac.aadl.runtime.util.range.NaturalNumberDecimalRange.class, APrimDecimal.instance);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

}
