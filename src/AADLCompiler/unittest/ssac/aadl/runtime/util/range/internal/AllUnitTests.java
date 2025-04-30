/*
 * @(#)AllUnitTests.java	1.70	2011/05/17
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.util.range.internal;

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
		TestSuite suite = new TestSuite(
				"Test for ssac.aadl.runtime.util.range.internal");
		//$JUnit-BEGIN$
		suite.addTestSuite(ShortRangeImplTest.class);
		suite.addTestSuite(IntegerRangeImplTest.class);
		suite.addTestSuite(LongRangeImplTest.class);
		suite.addTestSuite(BigDecimalRangeImplTest.class);
		suite.addTestSuite(OneNumberRangeImplTest.class);
		suite.addTestSuite(NaturalNumberRangeTokenizerTest.class);
		suite.addTestSuite(NaturalNumberRangeImplTest.class);
		//$JUnit-END$
		return suite;
	}
}
