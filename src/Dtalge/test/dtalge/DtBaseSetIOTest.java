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
 * @(#)DtBaseSetIOTest.java	0.20	2010/02/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtBaseSetIOTest.java	0.10	2008/09/02
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge;

import static dtalge.AllUnitTests.*;

import java.io.File;
import java.util.Arrays;

import dtalge.util.DtDataTypes;

import junit.framework.TestCase;

/**
 * <code>DtBaseSet</code> のファイル入出力機能のユニットテスト。
 * 
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class DtBaseSetIOTest extends TestCase
{
	static protected final File tocsv_def = new File(csvpath, "OutputDtBaseSet.csv");
	static protected final File tocsv_sjis = new File(csvpath, "OutputDtBaseSet_SJIS.csv");
	static protected final File tocsv_utf8 = new File(csvpath, "OutputDtBaseSet_UTF8.csv");
	static protected final File toxml = new File(xmlpath, "OutputDtBaseSet.xml");
	static protected final File fromcsv_def = new File(csvpath, "NormalDtBaseSet.csv");
	static protected final File fromcsv_sjis = new File(csvpath, "NormalDtBaseSet_SJIS.csv");
	static protected final File fromcsv_utf8 = new File(csvpath, "NormalDtBaseSet_UTF8.csv");
	static protected final File fromxml = new File(xmlpath, "NormalDtBaseSet.xml");
	
	static private final String NORMAL_BASEKEY_1 = "基底キー1";
	static private final String NORMAL_BASEKEY_2 = "基底キー2";
	static private final String NORMAL_BASEKEY_3 = "基底キー3";
	static private final String NORMAL_BASEKEY_4 = "基底キー4";
	static private final String NORMAL_BASEKEY_5 = "基底キー5";
	//static private final String NORMAL_BASEKEY_MARK = "基底 !\"#$%&'()=-=^~\\|@`[{;+:*]},<.>/?_";
	static private final String NORMAL_BASEKEY_MARK = "基底#$()=~\\`[{;+:*]}./_";
	//static private final String NORMAL_BASEKEY_ESC  = "エスケープ\"\b\f\'\0\'\\\"";
	static private final String NORMAL_BASEKEY_ESC  = "エスケープ";
	static private final String NORMAL_BASEKEY_UNI  = "ユニコード：\u3053\u3053\u307e\u3067"; // ユニコード：ここまで
	//static private final String REGEXP_BASEKEY_1 = "\\A\\s*\\Q基底キー\\E[0-9]?\\s*\\z";
	//static private final String REGEXP_BASEKEY_2 = "^基底キー(\\d{1})$";
	//static private final String REGEXP_BASEKEY_3 = "^([0-9]{4})/([0-9]{2})/([0-9]{2})";
	static private final String REGEXP_BASEKEY_1 = "\\A\\s*\\Q基底キー\\E\\d\\s*\\z";
	static private final String REGEXP_BASEKEY_2 = "基底キー(\\d{1})$";
	static private final String REGEXP_BASEKEY_3 = "(\\d{4})/(\\d{2})/(\\d{2})";
	
	static private final DtBase[] normalAllBases = {
		new DtBase(DtBase.OMITTED, DtDataTypes.BOOLEAN),
		//new DtBase(DtBase.OMITTED, DtDataTypes.INTEGER),
		new DtBase(DtBase.OMITTED, DtDataTypes.DECIMAL),
		new DtBase(DtBase.OMITTED, DtDataTypes.STRING),
		new DtBase(NORMAL_BASEKEY_1, DtDataTypes.BOOLEAN),
		//new DtBase(NORMAL_BASEKEY_1, DtDataTypes.INTEGER),
		new DtBase(NORMAL_BASEKEY_1, DtDataTypes.DECIMAL),
		new DtBase(NORMAL_BASEKEY_1, DtDataTypes.STRING),
		new DtBase(NORMAL_BASEKEY_1, DtDataTypes.BOOLEAN, NORMAL_BASEKEY_2),
		//new DtBase(NORMAL_BASEKEY_1, DtDataTypes.INTEGER, NORMAL_BASEKEY_2),
		new DtBase(NORMAL_BASEKEY_1, DtDataTypes.DECIMAL, NORMAL_BASEKEY_2),
		new DtBase(NORMAL_BASEKEY_1, DtDataTypes.STRING, NORMAL_BASEKEY_2),
		new DtBase(NORMAL_BASEKEY_1, DtDataTypes.BOOLEAN, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		//new DtBase(NORMAL_BASEKEY_1, DtDataTypes.INTEGER, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		new DtBase(NORMAL_BASEKEY_1, DtDataTypes.DECIMAL, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		new DtBase(NORMAL_BASEKEY_1, DtDataTypes.STRING, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		new DtBase(NORMAL_BASEKEY_5, DtDataTypes.BOOLEAN, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2),
		//new DtBase(NORMAL_BASEKEY_5, DtDataTypes.INTEGER, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2),
		new DtBase(NORMAL_BASEKEY_5, DtDataTypes.DECIMAL, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2),
		new DtBase(NORMAL_BASEKEY_5, DtDataTypes.STRING, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2),
		new DtBase(NORMAL_BASEKEY_MARK, DtDataTypes.BOOLEAN, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		//new DtBase(NORMAL_BASEKEY_MARK, DtDataTypes.INTEGER, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBase(NORMAL_BASEKEY_MARK, DtDataTypes.DECIMAL, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBase(NORMAL_BASEKEY_MARK, DtDataTypes.STRING, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBase(REGEXP_BASEKEY_1, DtDataTypes.BOOLEAN, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		//new DtBase(REGEXP_BASEKEY_1, DtDataTypes.INTEGER, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		new DtBase(REGEXP_BASEKEY_1, DtDataTypes.DECIMAL, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		new DtBase(REGEXP_BASEKEY_1, DtDataTypes.STRING, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
	};
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * Test method for {@link dtalge.DtBaseSet#fromCSV(java.io.File)}.
	 */
	public void testFromCSVFile() {
		File target;
		DtBaseSet ret = new DtBaseSet();
		
		// Normal data
		target = fromcsv_def;
		//--- read
		try {
			ret = DtBaseSet.fromCSV(target);
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from CSV file \"" + target.getPath() + "\"");
		}
		//--- check
		assertTrue(DtBaseSetTest.equalElementSequence(ret, normalAllBases));
	}

	/**
	 * Test method for {@link dtalge.DtBaseSet#fromCSV(java.io.File, java.lang.String)}.
	 */
	public void testFromCSVFileString() {
		File target;
		DtBaseSet ret;
		
		//--------------------------------------
		// SJIS
		//--------------------------------------
		
		// Normal data
		target = fromcsv_sjis;
		ret = new DtBaseSet();
		//--- read
		try {
			ret = DtBaseSet.fromCSV(target, SJIS);
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from CSV file \"" + target.getPath() + "\"");
		}
		//--- check
		assertTrue(DtBaseSetTest.equalElementSequence(ret, normalAllBases));
		
		//--------------------------------------
		// UTF-8
		//--------------------------------------
		
		// Normal data
		target = fromcsv_utf8;
		ret = new DtBaseSet();
		//--- read
		try {
			ret = DtBaseSet.fromCSV(target, UTF8);
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from CSV file \"" + target.getPath() + "\"");
		}
		//--- check
		assertTrue(DtBaseSetTest.equalElementSequence(ret, normalAllBases));
	}

	/**
	 * Test method for {@link dtalge.DtBaseSet#fromXML(java.io.File)}.
	 */
	public void testFromXML() {
		File target;
		DtBaseSet ret = new DtBaseSet();
		
		// Normal data
		target = fromxml;
		//--- read
		try {
			ret = DtBaseSet.fromXML(target);
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from XML file \"" + target.getPath() + "\"");
		}
		//--- check
		assertTrue(DtBaseSetTest.equalElementSequence(ret, normalAllBases));
	}

	/**
	 * Test method for {@link dtalge.DtBaseSet#toCSV(java.io.File)}.
	 */
	public void testToCSVFile() {
		File target = tocsv_def;
		DtBaseSet data = new DtBaseSet(Arrays.asList(normalAllBases));

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
			DtBaseSet ret = DtBaseSet.fromCSV(target);
			assertTrue(DtBaseSetTest.equalElementSequence(ret, normalAllBases));
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from CSV file \"" + target.getPath() + "\"");
		}
	}

	/**
	 * Test method for {@link dtalge.DtBaseSet#toCSV(java.io.File, java.lang.String)}.
	 */
	public void testToCSVFileString() {
		File target;
		DtBaseSet data = new DtBaseSet(Arrays.asList(normalAllBases));

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
			DtBaseSet ret = DtBaseSet.fromCSV(target, SJIS);
			assertTrue(DtBaseSetTest.equalElementSequence(ret, normalAllBases));
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
			DtBaseSet ret = DtBaseSet.fromCSV(target, UTF8);
			assertTrue(DtBaseSetTest.equalElementSequence(ret, normalAllBases));
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from CSV file \"" + target.getPath() + "\"");
		}
	}

	/**
	 * Test method for {@link dtalge.DtBaseSet#toXML(java.io.File)}.
	 */
	public void testToXMLFile() {
		File target = toxml;
		DtBaseSet data = new DtBaseSet(Arrays.asList(normalAllBases));

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
			DtBaseSet ret = DtBaseSet.fromXML(target);
			assertTrue(DtBaseSetTest.equalElementSequence(ret, normalAllBases));
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from XML file \"" + target.getPath() + "\"");
		}
	}

}
