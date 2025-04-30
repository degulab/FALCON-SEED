/*
 * @(#)AllUnitTests.java	0.2.0	2025/02/16
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.util;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * <code>dtalge.container.util</code> パッケージ直下のクラスの全ユニットテスト。
 * 
 * @author Y.Ishizuka(PieCake,Inc.)
 * 
 * @since 0.2.0
 */
public class AllUnitTests
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final File csvpath = new File("testdata/dtalge/CSV");
	static protected final File xmlpath = new File("testdata/dtalge/XML");
	static protected final File tablepath = new File("testdata/dtalge/TableCSV");
	static protected final String SJIS = "MS932";
	static protected final String UTF8 = "UTF-8";

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
		TestSuite suite = new TestSuite("Test for classes in dtalge.container.util package.");
		//$JUnit-BEGIN$
		suite.addTestSuite(DebitCreditItemTest.class);
		suite.addTestSuite(DebitCreditItemDefinitionTableTest.class);
		suite.addTestSuite(DtDebitCreditValuePairTest.class);
		suite.addTestSuite(DtDoubleEntrySimpleSlipsCsvUtilTest.class);
		//$JUnit-END$
		return suite;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
