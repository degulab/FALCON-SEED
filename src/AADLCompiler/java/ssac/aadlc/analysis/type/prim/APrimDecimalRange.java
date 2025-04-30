/*
 * @(#)APrimDecimalRange.java	1.70	2011/06/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type.prim;

import ssac.aadlc.analysis.type.AADLIterableType;
import ssac.aadlc.analysis.type.AADLPrimitive;

/**
 * AADL : DecimalRange
 * 
 * @author Y.Ishizuka(PieCake,Inc.)
 * @version 1.70	2011/06/29
 */
public class APrimDecimalRange extends AADLIterableType implements AADLPrimitive
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final APrimDecimalRange instance = new APrimDecimalRange();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected APrimDecimalRange() {
		super("DecimalRange", ssac.aadl.runtime.util.range.DecimalRange.class, APrimDecimal.instance);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

}
