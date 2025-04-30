/*
 * @(#)AllUnitTest.java	1.70	2011/05/17
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.io;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllUnitTests
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Test suites
	//------------------------------------------------------------

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for ssac.aadl.runtime.io");
		//$JUnit-BEGIN$
		suite.addTestSuite(TextFileReaderTest.class);
		suite.addTestSuite(TextFileWriterTest.class);
		suite.addTestSuite(CsvFileReaderTest.class);
		suite.addTestSuite(CsvFileWriterTest.class);
		//$JUnit-END$
		return suite;
	}
}
