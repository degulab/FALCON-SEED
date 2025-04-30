/*
 * @(#)APrimCsvFileWriter.java	1.50	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type.prim;

import ssac.aadlc.analysis.type.AADLJavaClass;
import ssac.aadlc.analysis.type.AADLPrimitive;

/**
 * AADL : CsvFileWriter
 * 
 * @version 1.50	2010/09/27
 * @since 1.50
 */
public class APrimCsvFileWriter extends AADLJavaClass implements AADLPrimitive
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final APrimCsvFileWriter instance = new APrimCsvFileWriter();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected APrimCsvFileWriter() {
		super("CsvFileWriter", ssac.aadl.runtime.io.CsvFileWriter.class);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

}
