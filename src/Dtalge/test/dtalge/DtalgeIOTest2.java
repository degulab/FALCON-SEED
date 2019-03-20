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
 * @(#)DtalgeIOTest2.java	0.40	2012/05/23
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge;

import static dtalge.AllUnitTests.*;

import java.io.File;
import java.math.BigDecimal;

import dtalge.exception.CsvFormatException;

import junit.framework.TestCase;

/**
 * <code>Dtalge</code> バージョン 2 のファイル入出力機能のユニットテスト。
 * 
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class DtalgeIOTest2 extends TestCase
{
	// 書き込み用ランダム順序のテストデータ(空文字列も含む)
	static protected final Object[] writeRandomAlgeData =
	{
		new DtBase("基底08", "string" , "#", "#"), "文字列08",
		new DtBase("基底08", "decimal", "#", "#"), new BigDecimal("-0.2108"),
		new DtBase("基底08", "boolean", "#", "#"), false,
		new DtBase("基底02", "string" , "#", "#"), "!%!%!%文字列02",
		new DtBase("基底02", "decimal", "#", "#"), new BigDecimal("123.02"),
		new DtBase("基底02", "boolean", "#", "#"), false,
		new DtBase("基底0d", "string" , "#", "#"), "!%",
		new DtBase("基底0d", "decimal", "#", "#"), new BigDecimal("-1.13"),
		new DtBase("基底0d", "boolean", "#", "#"), false,
		new DtBase("基底06", "string" , "#", "#"), " !%文字列06",
		new DtBase("基底06", "decimal", "#", "#"), new BigDecimal("-1006"),
		new DtBase("基底06", "boolean", "#", "#"), false,
		new DtBase("基底04", "string" , "#", "#"), "文字列!%04",
		new DtBase("基底04", "decimal", "#", "#"), new BigDecimal("-975.04"),
		new DtBase("基底04", "boolean", "#", "#"), false,
		new DtBase("基底0c", "string" , "#", "#"), "!%n",
		new DtBase("基底0c", "decimal", "#", "#"), new BigDecimal("1.12"),
		new DtBase("基底0c", "boolean", "#", "#"), true,
		new DtBase("基底0b", "string" , "#", "#"), "",
		new DtBase("基底0b", "decimal", "#", "#"), BigDecimal.ZERO,
		new DtBase("基底0b", "boolean", "#", "#"), false,
		new DtBase("基底0a", "string" , "#", "#"), null,
		new DtBase("基底0a", "decimal", "#", "#"), null,
		new DtBase("基底0a", "boolean", "#", "#"), null,
		new DtBase("基底03", "string" , "#", "#"), null,
		new DtBase("基底03", "decimal", "#", "#"), null,
		new DtBase("基底03", "boolean", "#", "#"), null,
		new DtBase("基底09", "string" , "#", "#"), "文字列49",
		new DtBase("基底09", "decimal", "#", "#"), null,
		new DtBase("基底09", "boolean", "#", "#"), false,
		new DtBase("基底07", "string" , "#", "#"), null,
		new DtBase("基底07", "decimal", "#", "#"), null,
		new DtBase("基底07", "boolean", "#", "#"), null,
		new DtBase("基底05", "string" , "#", "#"), "文字列45",
		new DtBase("基底05", "decimal", "#", "#"), null,
		new DtBase("基底05", "boolean", "#", "#"), false,
		new DtBase("基底01", "string" , "#", "#"), null,
		new DtBase("基底01", "decimal", "#", "#"), null,
		new DtBase("基底01", "boolean", "#", "#"), null,
	};

	// 読み込み用ランダム順序のテストデータ(空文字列はnull)
	static protected final Object[] readRandomAlgeData =
	{
		new DtBase("基底08", "string" , "#", "#"), "文字列08",
		new DtBase("基底08", "decimal", "#", "#"), new BigDecimal("-0.2108"),
		new DtBase("基底08", "boolean", "#", "#"), false,
		new DtBase("基底02", "string" , "#", "#"), "!%!%!%文字列02",
		new DtBase("基底02", "decimal", "#", "#"), new BigDecimal("123.02"),
		new DtBase("基底02", "boolean", "#", "#"), false,
		new DtBase("基底0d", "string" , "#", "#"), "!%",
		new DtBase("基底0d", "decimal", "#", "#"), new BigDecimal("-1.13"),
		new DtBase("基底0d", "boolean", "#", "#"), false,
		new DtBase("基底06", "string" , "#", "#"), " !%文字列06",
		new DtBase("基底06", "decimal", "#", "#"), new BigDecimal("-1006"),
		new DtBase("基底06", "boolean", "#", "#"), false,
		new DtBase("基底04", "string" , "#", "#"), "文字列!%04",
		new DtBase("基底04", "decimal", "#", "#"), new BigDecimal("-975.04"),
		new DtBase("基底04", "boolean", "#", "#"), false,
		new DtBase("基底0c", "string" , "#", "#"), "!%n",
		new DtBase("基底0c", "decimal", "#", "#"), new BigDecimal("1.12"),
		new DtBase("基底0c", "boolean", "#", "#"), true,
		new DtBase("基底0b", "string" , "#", "#"), null,
		new DtBase("基底0b", "decimal", "#", "#"), BigDecimal.ZERO,
		new DtBase("基底0b", "boolean", "#", "#"), false,
		new DtBase("基底0a", "string" , "#", "#"), null,
		new DtBase("基底0a", "decimal", "#", "#"), null,
		new DtBase("基底0a", "boolean", "#", "#"), null,
		new DtBase("基底03", "string" , "#", "#"), null,
		new DtBase("基底03", "decimal", "#", "#"), null,
		new DtBase("基底03", "boolean", "#", "#"), null,
		new DtBase("基底09", "string" , "#", "#"), "文字列49",
		new DtBase("基底09", "decimal", "#", "#"), null,
		new DtBase("基底09", "boolean", "#", "#"), false,
		new DtBase("基底07", "string" , "#", "#"), null,
		new DtBase("基底07", "decimal", "#", "#"), null,
		new DtBase("基底07", "boolean", "#", "#"), null,
		new DtBase("基底05", "string" , "#", "#"), "文字列45",
		new DtBase("基底05", "decimal", "#", "#"), null,
		new DtBase("基底05", "boolean", "#", "#"), false,
		new DtBase("基底01", "string" , "#", "#"), null,
		new DtBase("基底01", "decimal", "#", "#"), null,
		new DtBase("基底01", "boolean", "#", "#"), null,
	};

	// 読み込み用ランダム順序のテストデータ(空文字列はnull)
	static protected final Object[] readSortedAlgeData =
	{
		new DtBase("基底01", "string" , "#", "#"), null,
		new DtBase("基底01", "decimal", "#", "#"), null,
		new DtBase("基底01", "boolean", "#", "#"), null,
		new DtBase("基底02", "string" , "#", "#"), "!%!%!%文字列02",
		new DtBase("基底02", "decimal", "#", "#"), new BigDecimal("123.02"),
		new DtBase("基底02", "boolean", "#", "#"), false,
		new DtBase("基底03", "string" , "#", "#"), null,
		new DtBase("基底03", "decimal", "#", "#"), null,
		new DtBase("基底03", "boolean", "#", "#"), null,
		new DtBase("基底04", "string" , "#", "#"), "文字列!%04",
		new DtBase("基底04", "decimal", "#", "#"), new BigDecimal("-975.04"),
		new DtBase("基底04", "boolean", "#", "#"), false,
		new DtBase("基底05", "string" , "#", "#"), "文字列45",
		new DtBase("基底05", "decimal", "#", "#"), null,
		new DtBase("基底05", "boolean", "#", "#"), false,
		new DtBase("基底06", "string" , "#", "#"), " !%文字列06",
		new DtBase("基底06", "decimal", "#", "#"), new BigDecimal("-1006"),
		new DtBase("基底06", "boolean", "#", "#"), false,
		new DtBase("基底07", "string" , "#", "#"), null,
		new DtBase("基底07", "decimal", "#", "#"), null,
		new DtBase("基底07", "boolean", "#", "#"), null,
		new DtBase("基底08", "string" , "#", "#"), "文字列08",
		new DtBase("基底08", "decimal", "#", "#"), new BigDecimal("-0.2108"),
		new DtBase("基底08", "boolean", "#", "#"), false,
		new DtBase("基底09", "string" , "#", "#"), "文字列49",
		new DtBase("基底09", "decimal", "#", "#"), null,
		new DtBase("基底09", "boolean", "#", "#"), false,
		new DtBase("基底0a", "string" , "#", "#"), null,
		new DtBase("基底0a", "decimal", "#", "#"), null,
		new DtBase("基底0a", "boolean", "#", "#"), null,
		new DtBase("基底0b", "string" , "#", "#"), null,
		new DtBase("基底0b", "decimal", "#", "#"), BigDecimal.ZERO,
		new DtBase("基底0b", "boolean", "#", "#"), false,
		new DtBase("基底0c", "string" , "#", "#"), "!%n",
		new DtBase("基底0c", "decimal", "#", "#"), new BigDecimal("1.12"),
		new DtBase("基底0c", "boolean", "#", "#"), true,
		new DtBase("基底0d", "string" , "#", "#"), "!%",
		new DtBase("基底0d", "decimal", "#", "#"), new BigDecimal("-1.13"),
		new DtBase("基底0d", "boolean", "#", "#"), false,
	};

	// 読み込み用ランダム順序のテストデータ(空文字列はnull)
	static protected final Object[] readHalfSortedAlgeData =
	{
		new DtBase("基底01", "string" , "#", "#"), null,
		new DtBase("基底01", "decimal", "#", "#"), null,
		new DtBase("基底01", "boolean", "#", "#"), null,
		new DtBase("基底02", "string" , "#", "#"), "!%!%!%文字列02",
		new DtBase("基底02", "decimal", "#", "#"), new BigDecimal("123.02"),
		new DtBase("基底02", "boolean", "#", "#"), false,
		new DtBase("基底03", "string" , "#", "#"), null,
		new DtBase("基底03", "decimal", "#", "#"), null,
		new DtBase("基底03", "boolean", "#", "#"), null,
		new DtBase("基底04", "string" , "#", "#"), "文字列!%04",
		new DtBase("基底04", "decimal", "#", "#"), new BigDecimal("-975.04"),
		new DtBase("基底04", "boolean", "#", "#"), false,
		new DtBase("基底05", "string" , "#", "#"), "文字列45",
		new DtBase("基底05", "decimal", "#", "#"), null,
		new DtBase("基底05", "boolean", "#", "#"), false,
		new DtBase("基底06", "string" , "#", "#"), " !%文字列06",
		new DtBase("基底06", "decimal", "#", "#"), new BigDecimal("-1006"),
		new DtBase("基底06", "boolean", "#", "#"), false,
		new DtBase("基底07", "string" , "#", "#"), null,
		new DtBase("基底07", "decimal", "#", "#"), null,
		new DtBase("基底07", "boolean", "#", "#"), null,
		new DtBase("基底08", "string" , "#", "#"), "文字列08",
		new DtBase("基底08", "decimal", "#", "#"), new BigDecimal("-0.2108"),
		new DtBase("基底08", "boolean", "#", "#"), false,
		new DtBase("基底09", "string" , "#", "#"), "文字列49",
		new DtBase("基底09", "decimal", "#", "#"), null,
		new DtBase("基底09", "boolean", "#", "#"), false,
		new DtBase("基底0d", "string" , "#", "#"), "!%",
		new DtBase("基底0d", "decimal", "#", "#"), new BigDecimal("-1.13"),
		new DtBase("基底0d", "boolean", "#", "#"), false,
		new DtBase("基底0c", "string" , "#", "#"), "!%n",
		new DtBase("基底0c", "decimal", "#", "#"), new BigDecimal("1.12"),
		new DtBase("基底0c", "boolean", "#", "#"), true,
		new DtBase("基底0b", "string" , "#", "#"), null,
		new DtBase("基底0b", "decimal", "#", "#"), BigDecimal.ZERO,
		new DtBase("基底0b", "boolean", "#", "#"), false,
		new DtBase("基底0a", "string" , "#", "#"), null,
		new DtBase("基底0a", "decimal", "#", "#"), null,
		new DtBase("基底0a", "boolean", "#", "#"), null,
	};

	static protected final File tocsv_def = new File(csvpath, "v2_OutputDtalge.csv");
	static protected final File tocsv_sjis = new File(csvpath, "v2_OutputDtalge_SJIS.csv");
	static protected final File tocsv_utf8 = new File(csvpath, "v2_OutputDtalge_UTF8.csv");
	static protected final File toxml = new File(xmlpath, "v2_OutputDtalge.xml");
	static protected final File fromcsv_def = new File(csvpath, "v2_NormalDtAlgeSet.csv");
	static protected final File fromcsv_sjis = new File(csvpath, "v2_NormalDtAlgeSet_SJIS.csv");
	static protected final File fromcsv_utf8 = new File(csvpath, "v2_NormalDtAlgeSet_UTF8.csv");
	static protected final File fromxml = new File(xmlpath, "v2_NormalDtAlgeSet.xml");
	static protected final File err_fromcsv_def = new File(csvpath, "v2_IllegalDtAlgeSet.csv");
	static protected final File err_fromcsv_sjis = new File(csvpath, "v2_IllegalDtAlgeSet_SJIS.csv");
	static protected final File err_fromcsv_utf8 = new File(csvpath, "v2_IllegalDtAlgeSet_UTF8.csv");

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * Test method for {@link dtalge.Dtalge#fromCSV(java.io.File)}.
	 */
	public void testFromCSVFile() {
		File target;
		Dtalge ret = new Dtalge();
		
		// Normal data
		target = fromcsv_def;
		//--- read
		try {
			ret = Dtalge.fromCSV(target);
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from CSV file \"" + target.getPath() + "\"");
		}
		//--- check
		assertTrue(DtalgeTest.equalElementSequence(ret, readRandomAlgeData));
		
		// Illegal data
		target = err_fromcsv_def;
		//--- read
		try {
			ret = Dtalge.fromCSV(target);
			fail("Must be throw CsvFormatException by CSV file \"" + target.getPath() + "\"");
		}
		catch (CsvFormatException ex) {
			System.err.println(ex.toString() + " from CSV file \"" + target.getPath() + "\"");
			assert(true);
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from CSV file \"" + target.getPath() + "\"");
		}
	}

	/**
	 * Test method for {@link dtalge.Dtalge#fromCSV(java.io.File, java.lang.String)}.
	 */
	public void testFromCSVFileString() {
		File target;
		Dtalge ret;
		
		//--------------------------------------
		// SJIS
		//--------------------------------------
		
		// Normal data
		target = fromcsv_sjis;
		ret = new Dtalge();
		//--- read
		try {
			ret = Dtalge.fromCSV(target, SJIS);
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from CSV file \"" + target.getPath() + "\"");
		}
		//--- check
		assertTrue(DtalgeTest.equalElementSequence(ret, readRandomAlgeData));
		
		// Illegal data
		target = err_fromcsv_sjis;
		ret = new Dtalge();
		//--- read
		try {
			ret = Dtalge.fromCSV(target, SJIS);
			fail("Must be throw CsvFormatException by CSV file \"" + target.getPath() + "\"");
		}
		catch (CsvFormatException ex) {
			System.err.println(ex.toString() + " from CSV file \"" + target.getPath() + "\"");
			assert(true);
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from CSV file \"" + target.getPath() + "\"");
		}
		
		//--------------------------------------
		// UTF-8
		//--------------------------------------
		
		// Normal data
		target = fromcsv_utf8;
		ret = new Dtalge();
		//--- read
		try {
			ret = Dtalge.fromCSV(target, UTF8);
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from CSV file \"" + target.getPath() + "\"");
		}
		//--- check
		assertTrue(DtalgeTest.equalElementSequence(ret, readRandomAlgeData));
		
		// Illegal data
		target = err_fromcsv_utf8;
		ret = new Dtalge();
		//--- read
		try {
			ret = Dtalge.fromCSV(target, UTF8);
			fail("Must be throw CsvFormatException by CSV file \"" + target.getPath() + "\"");
		}
		catch (CsvFormatException ex) {
			System.err.println(ex.toString() + " from CSV file \"" + target.getPath() + "\"");
			assert(true);
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from CSV file \"" + target.getPath() + "\"");
		}
	}

	/**
	 * Test method for {@link dtalge.Dtalge#fromXML(java.io.File)}.
	 */
	public void testFromXML() {
		File target;
		Dtalge ret = new Dtalge();
		
		// Normal data
		target = fromxml;
		//--- read
		try {
			ret = Dtalge.fromXML(target);
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from XML file \"" + target.getPath() + "\"");
		}
		//--- check
		assertTrue(DtalgeTest.equalElementSequence(ret, readRandomAlgeData));
	}

	/**
	 * Test method for {@link dtalge.Dtalge#toCSV(java.io.File)}.
	 */
	public void testToCSVFile() {
		File target = tocsv_def;
		Dtalge data = new Dtalge(writeRandomAlgeData);

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
			Dtalge ret = Dtalge.fromCSV(target);
			assertTrue(DtalgeTest.equalElementSequence(ret, readRandomAlgeData));
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from CSV file \"" + target.getPath() + "\"");
		}
	}

	/**
	 * Test method for {@link dtalge.Dtalge#toCSV(java.io.File, java.lang.String)}.
	 */
	public void testToCSVFileString() {
		File target;
		Dtalge data = new Dtalge(writeRandomAlgeData);

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
			Dtalge ret = Dtalge.fromCSV(target, SJIS);
			assertTrue(DtalgeTest.equalElementSequence(ret, readRandomAlgeData));
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
			Dtalge ret = Dtalge.fromCSV(target, UTF8);
			assertTrue(DtalgeTest.equalElementSequence(ret, readRandomAlgeData));
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from CSV file \"" + target.getPath() + "\"");
		}
	}

	/**
	 * Test method for {@link dtalge.Dtalge#toXML(java.io.File)}.
	 */
	public void testToXMLFile() {
		File target = toxml;
		Dtalge data = new Dtalge(writeRandomAlgeData);

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
			Dtalge ret = Dtalge.fromXML(target);
			assertTrue(DtalgeTest.equalElementSequence(ret, readRandomAlgeData));
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from XML file \"" + target.getPath() + "\"");
		}
	}

}
