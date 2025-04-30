/*
 * @(#)APrimTextFileReader.java	1.50	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type.prim;

import ssac.aadlc.analysis.type.AADLIterableType;
import ssac.aadlc.analysis.type.AADLPrimitive;

/**
 * AADL : TextFileReader
 * 
 * @version 1.50	2010/09/27
 * @since 1.50
 */
public class APrimTextFileReader extends AADLIterableType implements AADLPrimitive
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final APrimTextFileReader instance = new APrimTextFileReader();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected APrimTextFileReader() {
		super("TextFileReader", ssac.aadl.runtime.io.TextFileReader.class, APrimString.instance);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

}
