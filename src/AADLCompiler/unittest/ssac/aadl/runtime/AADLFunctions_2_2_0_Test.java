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
 *  Copyright 2007-2015  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)AADLFunctions_2_2_0_Test.java	2.2.0	2015/05/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import junit.framework.TestCase;

/**
 * {@link ssac.aadl.runtime.AADLFunctions} クラスのテスト。
 * 
 * @version 2.2.0	2012/05/29
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * 
 * @since 2.2.0
 */
public class AADLFunctions_2_2_0_Test extends TestCase
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final class CsvTestPattern {
		public final String	csvString;
		public final String[] csvFields;
		
		public CsvTestPattern(String _csvString, String..._csvFields) {
			csvString = _csvString;
			csvFields = _csvFields;
		}
	}
	
	static protected final CsvTestPattern decimalToCsvTestData = new CsvTestPattern(
			// CSV string
			"0,-431234.124323141,0.000000000000000000000000000000000000001,-0.00000000001000000000001,23144521345136434252345246456234532452,7654345276542534264745758645532.0000000000000123413456450345234523405234523"
			// decimal list
			,"0"
			,"-431234.1243231410000"
			,"0.000000000000000000000000000000000000001"
			,"-0.000000000010000000000010000000000"
			,"23144521345136434252345246456234532452"
			,"7654345276542534264745758645532.0000000000000123413456450345234523405234523"
	);
	
	static protected final CsvTestPattern[] toCsvTestData = {
		// [0]
		new CsvTestPattern(",,\"\"\"\",\",\",\"\r\",\"\n\",\"\r\n\",hoge,hoge\thoge,\"hoge\"\"hoge\",\"hoge,hoge\",\"hoge\rhoge\",\"hoge\nhoge\",\"hoge\r\nhoge\""
			,null
			,""
			,"\""
			,","
			,"\r"
			,"\n"
			,"\r\n"
			,"hoge"
			,"hoge\thoge"
			,"hoge\"hoge"
			,"hoge,hoge"
			,"hoge\rhoge"
			,"hoge\nhoge"
			,"hoge\r\nhoge"
		),
		// [1]
		new CsvTestPattern("\"\"\"\",\",\",\"\r\",\"\n\",\"\r\n\",hoge,\thoge\thoge\t,\"\"\"hoge\"\"hoge\"\"\",\",hoge,hoge,\",\"\rhoge\rhoge\r\",\"\nhoge\nhoge\n\",\"\r\nhoge\r\nhoge\r\n\",,"
			,"\""
			,","
			,"\r"
			,"\n"
			,"\r\n"
			,"hoge"
			,"\thoge\thoge\t"
			,"\"hoge\"hoge\""
			,",hoge,hoge,"
			,"\rhoge\rhoge\r"
			,"\nhoge\nhoge\n"
			,"\r\nhoge\r\nhoge\r\n"
			,""
			,null
		),
	};
	
	static protected final CsvTestPattern[] fromCsvTestData = {
		// [0]
		new CsvTestPattern("\"\"", ""),
		// [1]
		new CsvTestPattern("\r", ""),
		// [2]
		new CsvTestPattern("\n", ""),
		// [3]
		new CsvTestPattern("\r\n", ""),
		// [4]
		new CsvTestPattern(",", "", ""),
		// [5]
		new CsvTestPattern(",\r", "", ""),
		// [6]
		new CsvTestPattern(",\n", "", ""),
		// [7]
		new CsvTestPattern(",\r\n", "", ""),
		// [8]
		new CsvTestPattern("hoge", "hoge"),
		// [9]
		new CsvTestPattern("hoge\r", "hoge"),
		// [10]
		new CsvTestPattern("hoge\n", "hoge"),
		// [11]
		new CsvTestPattern("hoge\r\n", "hoge"),
		// [12]
		new CsvTestPattern("\rhoge,", "", "hoge", ""),
		// [13]
		new CsvTestPattern("\nhoge,", "", "hoge", ""),
		// [14]
		new CsvTestPattern("\r\nhoge,", "", "hoge", ""),
		// [15]
		new CsvTestPattern("\r\r\nhoge,\r\n\r", "", "", "hoge", "", ""),
		// [16]
		new CsvTestPattern("\r\n\rhoge,\r\n\r", "", "", "hoge", "", ""),
		// [17]
		new CsvTestPattern(",,,,,", "", "", "", "", "", ""),
		// [18]
		new CsvTestPattern("\"\",\"hoge\",\thoge\thoge\t,\"\r\",\"\n\",\"\r\n\",\",\",\r\nh\"oge,\"hoge\"\"hoge\",\"hoge\"hoge\","
				,""
				,"hoge"
				,"\thoge\thoge\t"
				,"\r"
				,"\n"
				,"\r\n"
				,","
				,""
				,"h\"oge"
				,"hoge\"hoge"
				,"hogehoge\""
				,""
		),
		// [19]
		new CsvTestPattern("\"\",\"hoge\",\thoge\thoge\t,\"\r\",\"\n\",\"\r\n\",\",\",\r\nh\"oge,\"hoge\"\"hoge\",\"hoge\"hoge\",\r"
				,""
				,"hoge"
				,"\thoge\thoge\t"
				,"\r"
				,"\n"
				,"\r\n"
				,","
				,""
				,"h\"oge"
				,"hoge\"hoge"
				,"hogehoge\""
				,""
		),
		// [20]
		new CsvTestPattern("\"\",\"hoge\",\thoge\thoge\t,\"\r\",\"\n\",\"\r\n\",\",\",\r\nh\"oge,\"hoge\"\"hoge\",\"hoge\"hoge\",\n"
				,""
				,"hoge"
				,"\thoge\thoge\t"
				,"\r"
				,"\n"
				,"\r\n"
				,","
				,""
				,"h\"oge"
				,"hoge\"hoge"
				,"hogehoge\""
				,""
		),
		// [21]
		new CsvTestPattern("\"\",\"hoge\",\thoge\thoge\t,\"\r\",\"\n\",\"\r\n\",\",\",\r\nh\"oge,\"hoge\"\"hoge\",\"hoge\"hoge\",\r\n"
				,""
				,"hoge"
				,"\thoge\thoge\t"
				,"\r"
				,"\n"
				,"\r\n"
				,","
				,""
				,"h\"oge"
				,"hoge\"hoge"
				,"hogehoge\""
				,""
		),
		// [22]
		new CsvTestPattern("\"\",\"hoge\",\thoge\thoge\t,\"\r\",\"\n\",\"\r\n\",\",\",\r\nh\"oge,\"hoge\"\"hoge\",\"hoge\"hoge\",\r\nhoge"
				,""
				,"hoge"
				,"\thoge\thoge\t"
				,"\r"
				,"\n"
				,"\r\n"
				,","
				,""
				,"h\"oge"
				,"hoge\"hoge"
				,"hogehoge\""
				,""
				,"hoge"
		),
		// [23]
		new CsvTestPattern("\"\",\"hoge\",\thoge\thoge\t,\"\r\",\"\n\",\"\r\n\",\",\",\r\nh\"oge,\"hoge\"\"hoge\",\"hoge\"hoge\",\r\n\",\"\",\""
				,""
				,"hoge"
				,"\thoge\thoge\t"
				,"\r"
				,"\n"
				,"\r\n"
				,","
				,""
				,"h\"oge"
				,"hoge\"hoge"
				,"hogehoge\""
				,""
				,",\","
		),
	};
	
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
	// Test Cases for 2.2.0
	//------------------------------------------------------------

	/**
	 * Test method for {@link ssac.aadl.runtime.AADLFunctions#toCsvString(java.util.Collection)}.
	 */
	public void testToCsvStringCollectionOfQ() {
		// check null
		try {
			AADLFunctions.toCsvString(null, false);
			fail("Must throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			AADLFunctions.toCsvString(null, true);
			fail("Must throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		// check empty
		String result = AADLFunctions.toCsvString(Collections.emptyList(), false);
		assertEquals("", result);
		result = AADLFunctions.toCsvString(Collections.emptyList(), true);
		assertEquals("", result);
		
		// check has empty field only one
		result = AADLFunctions.toCsvString(Arrays.asList(""), false);
		assertEquals("\"\"", result);
		result = AADLFunctions.toCsvString(Arrays.asList(""), true);
		assertEquals("\r\n", result);
		
		// check by string list
		for (int i = 0; i < toCsvTestData.length; ++i) {
			String header = "Index[" + i + "]:";
			CsvTestPattern tpat = toCsvTestData[i];
			result = AADLFunctions.toCsvString(Arrays.asList(tpat.csvFields), false);
//			System.out.println(header+result);
//			System.out.println(header+tpat.csvString);
			assertEquals(header, tpat.csvString, result);
			result = AADLFunctions.toCsvString(Arrays.asList(tpat.csvFields), true);
			assertEquals(header, tpat.csvString + "\r\n", result);
		}
		
		// check by decimal list
		ArrayList<BigDecimal> dlist = new ArrayList<BigDecimal>();
		for (int i = 0; i < decimalToCsvTestData.csvFields.length; ++i) {
			dlist.add(new BigDecimal(decimalToCsvTestData.csvFields[i]));
		}
		result = AADLFunctions.toCsvString(dlist, false);
		assertEquals(decimalToCsvTestData.csvString, result);
		result = AADLFunctions.toCsvString(dlist, true);
		assertEquals(decimalToCsvTestData.csvString + "\r\n", result);
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.AADLFunctions#newStringListFromCsvString(java.lang.String)}.
	 */
	public void testNewStringListFromCsvString() {
		ArrayList<String> ansEmpty = new ArrayList<String>(0);
		ArrayList<String> result;
		
		// check null
		result = AADLFunctions.newStringListFromCsvString(null);
		assertEquals(ansEmpty, result);
		
		// check empty
		result = AADLFunctions.newStringListFromCsvString("");
		assertEquals(ansEmpty, result);
		
		// check by decimals CSV string that made by decimal list
		ArrayList<String> ansDecimals = new ArrayList<String>();
		for (String str : decimalToCsvTestData.csvFields) {
			ansDecimals.add(AADLFunctions.toString(new BigDecimal(str)));
		}
		result = AADLFunctions.newStringListFromCsvString(decimalToCsvTestData.csvString);
		assertEquals(ansDecimals, result);
		result = AADLFunctions.newStringListFromCsvString(decimalToCsvTestData.csvString+"\r\n");
		assertEquals(ansDecimals, result);
		
		// check by test data
		for (int i = 0; i < fromCsvTestData.length; ++i) {
			String header = "Index[" + i + "]:";
			CsvTestPattern tpat = fromCsvTestData[i];
			result = AADLFunctions.newStringListFromCsvString(tpat.csvString);
//			System.out.println(header+"answer : " + Arrays.asList(tpat.csvFields).toString());
//			System.out.println(header+"result : " + result.toString());
			assertEquals(header, Arrays.asList(tpat.csvFields), result);
		}
	}
}
