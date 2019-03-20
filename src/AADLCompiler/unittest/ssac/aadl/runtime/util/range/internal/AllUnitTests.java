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
 *  Copyright 2007-2011  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
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
