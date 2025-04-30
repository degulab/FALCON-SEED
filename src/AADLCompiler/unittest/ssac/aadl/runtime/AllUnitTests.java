/*
 * @(#)AllUnitTests.java	2.1.0	2014/05/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AllUnitTests.java	1.80	2012/05/30
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime;

import ssac.aadl.runtime.io.CsvFileReaderTest;
import ssac.aadl.runtime.io.CsvFileWriterTest;
import ssac.aadl.runtime.io.TextFileReaderTest;
import ssac.aadl.runtime.io.TextFileWriterTest;
import ssac.aadl.runtime.util.range.EmptyRangeIteratorTest;
import ssac.aadl.runtime.util.range.NaturalNumberDecimalRangeTest;
import ssac.aadl.runtime.util.range.RangeUtilTest;
import ssac.aadl.runtime.util.range.SimpleDecimalRangeTest;
import ssac.aadl.runtime.util.range.internal.BigDecimalRangeImplTest;
import ssac.aadl.runtime.util.range.internal.IntegerRangeImplTest;
import ssac.aadl.runtime.util.range.internal.LongRangeImplTest;
import ssac.aadl.runtime.util.range.internal.NaturalNumberRangeImplTest;
import ssac.aadl.runtime.util.range.internal.NaturalNumberRangeTokenizerTest;
import ssac.aadl.runtime.util.range.internal.OneNumberRangeImplTest;
import ssac.aadl.runtime.util.range.internal.ShortRangeImplTest;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * <code>ssac.aadl.runtime</code> パッケージ直下のクラスの全ユニットテスト。
 * 
 * @author Y.Ishizuka(PieCake,Inc.)
 */
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
	// Public interfaces
	//------------------------------------------------------------
	
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for classes in ssac.aadl.runtime.* package.");
		
		// ssac.aadl.runtime
		suite.addTestSuite(AADLAlgeIOFunctionsTest.class);
		suite.addTestSuite(AADLRangeFunctionsTest.class);
		suite.addTestSuite(AADLFunctions_2_0_0_Test.class);
		suite.addTestSuite(AADLFunctions_2_1_0_Test.class);
		
		// ssac.aadl.runtime.io
		suite.addTestSuite(TextFileReaderTest.class);
		suite.addTestSuite(TextFileWriterTest.class);
		suite.addTestSuite(CsvFileReaderTest.class);
		suite.addTestSuite(CsvFileWriterTest.class);
		
		// ssac.aadl.runtime.util.range
		suite.addTestSuite(RangeUtilTest.class);
		suite.addTestSuite(EmptyRangeIteratorTest.class);
		suite.addTestSuite(SimpleDecimalRangeTest.class);
		suite.addTestSuite(NaturalNumberDecimalRangeTest.class);
		
		// ssac.aadl.runtime.util.range.internal
		suite.addTestSuite(ShortRangeImplTest.class);
		suite.addTestSuite(IntegerRangeImplTest.class);
		suite.addTestSuite(LongRangeImplTest.class);
		suite.addTestSuite(BigDecimalRangeImplTest.class);
		suite.addTestSuite(OneNumberRangeImplTest.class);
		suite.addTestSuite(NaturalNumberRangeTokenizerTest.class);
		suite.addTestSuite(NaturalNumberRangeImplTest.class);
		
		return suite;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
