/*
 * @(#)APrimDecimal.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type.prim;

import ssac.aadlc.analysis.type.AADLJavaClass;
import ssac.aadlc.analysis.type.AADLPrimitive;

/**
 * AADL : Decimal
 * 
 * @version 1.00	2007/11/29
 */
public class APrimDecimal extends AADLJavaClass implements AADLPrimitive
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final APrimDecimal instance = new APrimDecimal();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected APrimDecimal() {
		super("Decimal", java.math.BigDecimal.class);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

}
