/*
 * @(#)AllUnitTest.java	1.70	2011/05/17
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.util.range;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllUnitTest
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
		TestSuite suite = new TestSuite("Test for ssac.aadl.runtime.util.range");
		//$JUnit-BEGIN$
		suite.addTestSuite(RangeUtilTest.class);
		suite.addTestSuite(EmptyRangeIteratorTest.class);
		suite.addTestSuite(SimpleDecimalRangeTest.class);
		suite.addTestSuite(NaturalNumberDecimalRangeTest.class);
		//$JUnit-END$
		return suite;
	}
}
