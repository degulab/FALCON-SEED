/*
 * @(#)APrimTextFileWriter.java	1.50	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type.prim;

import ssac.aadlc.analysis.type.AADLJavaClass;
import ssac.aadlc.analysis.type.AADLPrimitive;

/**
 * AADL : TextFileWriter
 * 
 * @version 1.50	2010/09/27
 * @since 1.50
 */
public class APrimTextFileWriter extends AADLJavaClass implements AADLPrimitive
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final APrimTextFileWriter instance = new APrimTextFileWriter();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected APrimTextFileWriter() {
		super("TextFileWriter", ssac.aadl.runtime.io.TextFileWriter.class);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

}
