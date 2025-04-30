/*
 * @(#)APrimCharacter.java	1.50	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type.prim;

import ssac.aadlc.analysis.type.AADLJavaClass;
import ssac.aadlc.analysis.type.AADLPrimitive;

/**
 * AADL : Character
 * 
 * @version 1.50	2010/09/27
 * @since 1.50
 */
public class APrimCharacter extends AADLJavaClass implements AADLPrimitive
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final APrimCharacter instance = new APrimCharacter();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected APrimCharacter() {
		super("Char", java.lang.Character.class);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
}
