/*
 * @(#)APrimDtStringThesaurus.java	1.40	2010/02/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type.prim;

import ssac.aadlc.analysis.type.AADLJavaClass;
import ssac.aadlc.analysis.type.AADLPrimitive;

/**
 * AADL : DtStringThesaurus
 * 
 * @version 1.40	2010/02/19
 * @since 1.40
 */
public class APrimDtStringThesaurus extends AADLJavaClass implements AADLPrimitive
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final APrimDtStringThesaurus instance = new APrimDtStringThesaurus();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected APrimDtStringThesaurus() {
		super("DtStringThesaurus", dtalge.DtStringThesaurus.class);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
}
