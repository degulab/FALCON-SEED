package exalge2;

import junit.framework.Test;
import junit.framework.TestSuite;
import exalge2.io.FileUtilTest;
import exalge2.util.ExFiscalYearTimeKeyTest;
import exalge2.util.ExMonthTimeKeyTest;
import exalge2.util.ExQuarterTimeKeyTest;
import exalge2.util.ExTimeKeyFactoryTest;
import exalge2.util.ExYearTimeKeyTest;
import exalge2.util.FileConverterTest;

public class AllUnitTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for exalge2");
		//$JUnit-BEGIN$
		suite.addTestSuite(ExBaseTest.class);
		suite.addTestSuite(ExBasePatternTest.class);
		suite.addTestSuite(ExBaseSetTest.class);
		suite.addTestSuite(ExBasePatternSetTest.class);
		suite.addTestSuite(ExalgeTest.class);
		suite.addTestSuite(ExAlgeSetTest.class);
		suite.addTestSuite(TransTableTest.class);
		suite.addTestSuite(TransDivideRatiosTest.class);
		suite.addTestSuite(TransMatrixTest.class);
		suite.addTestSuite(ExBasePatternIndexSetTest.class);
		suite.addTestSuite(ExTransferTest.class);
		suite.addTestSuite(ExBaseSetIOTest.class);
		suite.addTestSuite(ExalgeIOTest.class);
		suite.addTestSuite(ExAlgeSetIOTest.class);
		suite.addTestSuite(ExBasePatternSetIOTest.class);
		suite.addTestSuite(TransTableIOTest.class);
		suite.addTestSuite(TransMatrixIOTest.class);
		suite.addTestSuite(ExTransferIOTest.class);
		suite.addTestSuite(ExYearTimeKeyTest.class);
		suite.addTestSuite(ExMonthTimeKeyTest.class);
		suite.addTestSuite(ExQuarterTimeKeyTest.class);
		suite.addTestSuite(ExFiscalYearTimeKeyTest.class);
		suite.addTestSuite(ExTimeKeyFactoryTest.class);
		suite.addTestSuite(FileConverterTest.class);
		suite.addTestSuite(FileUtilTest.class);
		//$JUnit-END$
		return suite;
	}

}
