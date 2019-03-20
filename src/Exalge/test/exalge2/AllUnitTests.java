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
 *  Copyright 2007-2009  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Li Hou(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
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
