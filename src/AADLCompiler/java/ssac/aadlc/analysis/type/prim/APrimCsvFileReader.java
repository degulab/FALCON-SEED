/*
 * @(#)APrimCsvFileReader.java	1.50	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type.prim;

import ssac.aadlc.analysis.type.AADLIterableType;
import ssac.aadlc.analysis.type.AADLPrimitive;

/**
 * AADL : CsvFileReader
 * 
 * @version 1.50	2010/09/27
 * @since 1.50
 */
public class APrimCsvFileReader extends AADLIterableType implements AADLPrimitive
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final APrimCsvFileReader instance = new APrimCsvFileReader();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected APrimCsvFileReader() {
		super("CsvFileReader", ssac.aadl.runtime.io.CsvFileReader.class, APrimStringList.instance);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

}
