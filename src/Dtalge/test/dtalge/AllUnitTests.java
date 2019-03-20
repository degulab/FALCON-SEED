/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 *  Copyright 2007-2012  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
/*
 * @(#)AllUnitTests.java	0.40	2012/05/23
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AllUnitTests.java	0.20	2010/03/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AllUnitTests.java	0.10	2008/08/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * <code>dtalge</code> パッケージ直下のクラスの全ユニットテスト。
 * 
 * @author Y.Ishizuka(PieCake,Inc.)
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
		TestSuite suite = new TestSuite("Test for classes in dtalge package.");
		//$JUnit-BEGIN$
		suite.addTestSuite(DtDataTypesTest.class);
		suite.addTestSuite(DtStringThesaurusTest.class);
		suite.addTestSuite(DtBaseTest.class);
		suite.addTestSuite(DtBasePatternTest.class);
		suite.addTestSuite(DtBaseSetTest.class);
		suite.addTestSuite(DtBasePatternSetTest.class);
		suite.addTestSuite(DtalgeTest.class);
		suite.addTestSuite(DtAlgeSetTest.class);
		suite.addTestSuite(DtBaseSetIOTest.class);
		suite.addTestSuite(DtBasePatternSetIOTest.class);
		suite.addTestSuite(DtalgeIOTest.class);
		suite.addTestSuite(DtAlgeSetIOTest.class);
		suite.addTestSuite(DtStringThesaurusIOTest.class);
		suite.addTestSuite(DtalgeTableIOTest.class);
		suite.addTestSuite(DtAlgeSetTableIOTest.class);
		suite.addTestSuite(DtalgeIOTest2.class);
		suite.addTestSuite(DtAlgeSetIOTest2.class);
		suite.addTestSuite(DtalgeTableIOTest2.class);
		suite.addTestSuite(DtAlgeSetTableIOTest2.class);
		//$JUnit-END$
		return suite;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
