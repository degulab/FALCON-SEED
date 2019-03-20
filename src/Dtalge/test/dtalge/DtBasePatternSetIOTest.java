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
 *  Copyright 2007-2010  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
/*
 * @(#)DtBasePatternSetIOTest.java	0.20	2010/02/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtBasePatternSetIOTest.java	0.10	2008/09/02
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge;

import static dtalge.AllUnitTests.*;

import java.io.File;
import java.util.Arrays;

import junit.framework.TestCase;

/**
 * <code>DtBasePatternSet</code> のファイル入出力機能のユニットテスト。
 * 
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class DtBasePatternSetIOTest extends TestCase
{
	static protected final File tocsv_def = new File(csvpath, "OutputDtBasePatternSet.csv");
	static protected final File tocsv_sjis = new File(csvpath, "OutputDtBasePatternSet_SJIS.csv");
	static protected final File tocsv_utf8 = new File(csvpath, "OutputDtBasePatternSet_UTF8.csv");
	static protected final File toxml = new File(xmlpath, "OutputDtBasePatternSet.xml");
	static protected final File fromcsv_def = new File(csvpath, "NormalDtBasePatternSet.csv");
	static protected final File fromcsv_sjis = new File(csvpath, "NormalDtBasePatternSet_SJIS.csv");
	static protected final File fromcsv_utf8 = new File(csvpath, "NormalDtBasePatternSet_UTF8.csv");
	static protected final File fromxml = new File(xmlpath, "NormalDtBasePatternSet.xml");

	static private final String NORMAL_BASEKEY_1 = "基底キー1";
	static private final String NORMAL_BASEKEY_2 = "基底キー2";
	static private final String NORMAL_BASEKEY_3 = "基底キー3";
	static private final String NORMAL_BASEKEY_4 = "基底キー4";
	static private final String NORMAL_BASEKEY_5 = "基底キー5";
	static private final String NORMAL_BASEKEY_A = "基底*";
	static private final String NORMAL_BASEKEY_B = "基底*2";
	static private final String NORMAL_BASEKEY_C = "基底*3";
	static private final String NORMAL_BASEKEY_D = "基底*4";
	static private final String NORMAL_BASEKEY_E = "基底*5";
	//static private final String NORMAL_BASEKEY_MARK = "記号!\"#$%&'()=-=^~\\|@`[{;+:*]},<.>/?_";
	static private final String NORMAL_BASEKEY_MARK = "記号#$()=~\\`[{;+:*]}./_";
	//static private final String NORMAL_BASEKEY_ESC  = "エスケープ\"\b\f\'\0\'\\\"";
	static private final String NORMAL_BASEKEY_ESC  = "エスケープ";
	static private final String NORMAL_BASEKEY_UNI  = "ユニコード：\u3053\u3053\u307e\u3067"; // ユニコード：ここまで
	//static private final String REGEXP_BASEKEY_1 = "\\A\\s*\\Q基底キー\\E[0-9]?\\s*\\z";
	//static private final String REGEXP_BASEKEY_2 = "^基底キー(\\d{1})$";
	//static private final String REGEXP_BASEKEY_3 = "^([0-9]{4})/([0-9]{2})/([0-9]{2})";
	static private final String REGEXP_BASEKEY_1 = "\\A\\s*\\Q基底キー\\E\\d\\s*\\z";
	static private final String REGEXP_BASEKEY_2 = "基底キー(\\d{1})$";
	static private final String REGEXP_BASEKEY_3 = "(\\d{4})/(\\d{2})/(\\d{2})";
	
	static private final String DT_BOOLEAN_U = "BOOLEAN";
	static private final String DT_BOOLEAN_M = "bOoLeAn";
	static private final String DT_BOOLEAN_W = "bo*an";
	static private final String DT_INTEGER_U = "INTEGER";
	static private final String DT_INTEGER_M = "InTeGeR";
	static private final String DT_INTEGER_W = "in*er";
	static private final String DT_DECIMAL_U = "DECIMAL";
	static private final String DT_DECIMAL_M = "dEcImAl";
	static private final String DT_DECIMAL_W = "de*al";
	static private final String DT_STRING_U = "STRING";
	static private final String DT_STRING_M = "StRiNg";
	static private final String DT_STRING_W = "st*ng";
	
	static private final DtBasePattern[] normalAllBases = {
		new DtBasePattern(DtBase.OMITTED, DT_BOOLEAN_M),
		new DtBasePattern(DtBase.OMITTED, DT_INTEGER_M),
		new DtBasePattern(DtBase.OMITTED, DT_DECIMAL_M),
		new DtBasePattern(DtBase.OMITTED, DT_STRING_M),
		new DtBasePattern(DtBase.OMITTED, DT_BOOLEAN_M, DtBase.OMITTED),
		new DtBasePattern(DtBase.OMITTED, DT_INTEGER_M, DtBase.OMITTED),
		new DtBasePattern(DtBase.OMITTED, DT_DECIMAL_M, DtBase.OMITTED),
		new DtBasePattern(DtBase.OMITTED, DT_STRING_M, DtBase.OMITTED),
		new DtBasePattern(DtBase.OMITTED, DT_BOOLEAN_M, DtBase.OMITTED, DtBase.OMITTED),
		new DtBasePattern(DtBase.OMITTED, DT_INTEGER_M, DtBase.OMITTED, DtBase.OMITTED),
		new DtBasePattern(DtBase.OMITTED, DT_DECIMAL_M, DtBase.OMITTED, DtBase.OMITTED),
		new DtBasePattern(DtBase.OMITTED, DT_STRING_M, DtBase.OMITTED, DtBase.OMITTED),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_BOOLEAN_M),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_INTEGER_M),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_DECIMAL_M),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_STRING_M),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_BOOLEAN_M, NORMAL_BASEKEY_2),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_INTEGER_M, NORMAL_BASEKEY_2),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_DECIMAL_M, NORMAL_BASEKEY_2),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_STRING_M, NORMAL_BASEKEY_2),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_BOOLEAN_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_INTEGER_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_DECIMAL_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_STRING_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		new DtBasePattern(NORMAL_BASEKEY_5, DT_BOOLEAN_M, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2),
		new DtBasePattern(NORMAL_BASEKEY_5, DT_INTEGER_M, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2),
		new DtBasePattern(NORMAL_BASEKEY_5, DT_DECIMAL_M, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2),
		new DtBasePattern(NORMAL_BASEKEY_5, DT_STRING_M, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2),
		new DtBasePattern(NORMAL_BASEKEY_MARK, DT_BOOLEAN_U, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBasePattern(NORMAL_BASEKEY_MARK, DT_INTEGER_U, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBasePattern(NORMAL_BASEKEY_MARK, DT_DECIMAL_U, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBasePattern(NORMAL_BASEKEY_MARK, DT_STRING_U, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBasePattern(REGEXP_BASEKEY_1, DT_BOOLEAN_U, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		new DtBasePattern(REGEXP_BASEKEY_1, DT_INTEGER_U, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		new DtBasePattern(REGEXP_BASEKEY_1, DT_DECIMAL_U, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		new DtBasePattern(REGEXP_BASEKEY_1, DT_STRING_U, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_BOOLEAN_W),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_INTEGER_W),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_DECIMAL_W),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_STRING_W),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_BOOLEAN_W, NORMAL_BASEKEY_B),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_INTEGER_W, NORMAL_BASEKEY_B),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_DECIMAL_W, NORMAL_BASEKEY_B),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_STRING_W, NORMAL_BASEKEY_B),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_BOOLEAN_W, NORMAL_BASEKEY_B, NORMAL_BASEKEY_C),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_INTEGER_W, NORMAL_BASEKEY_B, NORMAL_BASEKEY_C),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_DECIMAL_W, NORMAL_BASEKEY_B, NORMAL_BASEKEY_C),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_STRING_W, NORMAL_BASEKEY_B, NORMAL_BASEKEY_C),
		new DtBasePattern(NORMAL_BASEKEY_E, DT_BOOLEAN_W, NORMAL_BASEKEY_D, NORMAL_BASEKEY_B),
		new DtBasePattern(NORMAL_BASEKEY_E, DT_INTEGER_W, NORMAL_BASEKEY_D, NORMAL_BASEKEY_B),
		new DtBasePattern(NORMAL_BASEKEY_E, DT_DECIMAL_W, NORMAL_BASEKEY_D, NORMAL_BASEKEY_B),
		new DtBasePattern(NORMAL_BASEKEY_E, DT_STRING_W, NORMAL_BASEKEY_D, NORMAL_BASEKEY_B),
		new DtBasePattern(NORMAL_BASEKEY_MARK, DT_BOOLEAN_W, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBasePattern(NORMAL_BASEKEY_MARK, DT_INTEGER_W, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBasePattern(NORMAL_BASEKEY_MARK, DT_DECIMAL_W, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBasePattern(NORMAL_BASEKEY_MARK, DT_STRING_W, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBasePattern(REGEXP_BASEKEY_1, DT_BOOLEAN_W, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		new DtBasePattern(REGEXP_BASEKEY_1, DT_INTEGER_W, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		new DtBasePattern(REGEXP_BASEKEY_1, DT_DECIMAL_W, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		new DtBasePattern(REGEXP_BASEKEY_1, DT_STRING_W, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
	};
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * Test method for {@link dtalge.DtBasePatternSet#fromCSV(java.io.File)}.
	 */
	public void testFromCSVFile() {
		File target;
		DtBasePatternSet ret = new DtBasePatternSet();
		
		// Normal data
		target = fromcsv_def;
		//--- read
		try {
			ret = DtBasePatternSet.fromCSV(target);
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from CSV file \"" + target.getPath() + "\"");
		}
		//--- check
		assertTrue(DtBasePatternSetTest.equalElementSequence(ret, normalAllBases));
	}

	/**
	 * Test method for {@link dtalge.DtBasePatternSet#fromCSV(java.io.File, java.lang.String)}.
	 */
	public void testFromCSVFileString() {
		File target;
		DtBasePatternSet ret;
		
		//--------------------------------------
		// SJIS
		//--------------------------------------
		
		// Normal data
		target = fromcsv_sjis;
		ret = new DtBasePatternSet();
		//--- read
		try {
			ret = DtBasePatternSet.fromCSV(target, SJIS);
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from CSV file \"" + target.getPath() + "\"");
		}
		//--- check
		assertTrue(DtBasePatternSetTest.equalElementSequence(ret, normalAllBases));
		
		//--------------------------------------
		// UTF-8
		//--------------------------------------
		
		// Normal data
		target = fromcsv_utf8;
		ret = new DtBasePatternSet();
		//--- read
		try {
			ret = DtBasePatternSet.fromCSV(target, UTF8);
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from CSV file \"" + target.getPath() + "\"");
		}
		//--- check
		assertTrue(DtBasePatternSetTest.equalElementSequence(ret, normalAllBases));
	}

	/**
	 * Test method for {@link dtalge.DtBasePatternSet#fromXML(java.io.File)}.
	 */
	public void testFromXML() {
		File target;
		DtBasePatternSet ret = new DtBasePatternSet();
		
		// Normal data
		target = fromxml;
		//--- read
		try {
			ret = DtBasePatternSet.fromXML(target);
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from XML file \"" + target.getPath() + "\"");
		}
		//--- check
		assertTrue(DtBasePatternSetTest.equalElementSequence(ret, normalAllBases));
	}

	/**
	 * Test method for {@link dtalge.DtBasePatternSet#toCSV(java.io.File)}.
	 */
	public void testToCSVFile() {
		File target = tocsv_def;
		DtBasePatternSet data = new DtBasePatternSet(Arrays.asList(normalAllBases));

		//--- write
		try {
			data.toCSV(target);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Failed to write to CSV file \"" + target.getPath() + "\"");
		}
		//--- check
		try {
			DtBasePatternSet ret = DtBasePatternSet.fromCSV(target);
			assertTrue(DtBasePatternSetTest.equalElementSequence(ret, normalAllBases));
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from CSV file \"" + target.getPath() + "\"");
		}
	}

	/**
	 * Test method for {@link dtalge.DtBasePatternSet#toCSV(java.io.File, java.lang.String)}.
	 */
	public void testToCSVFileString() {
		File target;
		DtBasePatternSet data = new DtBasePatternSet(Arrays.asList(normalAllBases));

		// SJIS
		//--- write
		target = tocsv_sjis;
		try {
			data.toCSV(target, SJIS);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Failed to write to CSV file \"" + target.getPath() + "\"");
		}
		//--- check
		try {
			DtBasePatternSet ret = DtBasePatternSet.fromCSV(target, SJIS);
			assertTrue(DtBasePatternSetTest.equalElementSequence(ret, normalAllBases));
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from CSV file \"" + target.getPath() + "\"");
		}

		// UTF-8
		//--- write
		target = tocsv_utf8;
		try {
			data.toCSV(target, UTF8);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Failed to write to CSV file \"" + target.getPath() + "\"");
		}
		//--- check
		try {
			DtBasePatternSet ret = DtBasePatternSet.fromCSV(target, UTF8);
			assertTrue(DtBasePatternSetTest.equalElementSequence(ret, normalAllBases));
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from CSV file \"" + target.getPath() + "\"");
		}
	}

	/**
	 * Test method for {@link dtalge.DtBasePatternSet#toXML(java.io.File)}.
	 */
	public void testToXMLFile() {
		File target = toxml;
		DtBasePatternSet data = new DtBasePatternSet(Arrays.asList(normalAllBases));

		//--- write
		try {
			data.toXML(target);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Failed to write to XML file \"" + target.getPath() + "\"");
		}
		//--- check
		try {
			DtBasePatternSet ret = DtBasePatternSet.fromXML(target);
			assertTrue(DtBasePatternSetTest.equalElementSequence(ret, normalAllBases));
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from XML file \"" + target.getPath() + "\"");
		}
	}

}
