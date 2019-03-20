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
 * @(#)DtalgeIOTest.java	0.20	2010/02/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtalgeIOTest.java	0.10	2008/09/02
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge;

import static dtalge.AllUnitTests.*;

import java.io.File;

import junit.framework.TestCase;

/**
 * <code>Dtalge</code> のファイル入出力機能のユニットテスト。
 * 
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class DtalgeIOTest extends TestCase
{
	static protected final File tocsv_def = new File(csvpath, "OutputDtalge.csv");
	static protected final File tocsv_sjis = new File(csvpath, "OutputDtalge_SJIS.csv");
	static protected final File tocsv_utf8 = new File(csvpath, "OutputDtalge_UTF8.csv");
	static protected final File toxml = new File(xmlpath, "OutputDtalge.xml");
	static protected final File fromcsv_def = new File(csvpath, "NormalDtAlgeSet.csv");
	static protected final File fromcsv_sjis = new File(csvpath, "NormalDtAlgeSet_SJIS.csv");
	static protected final File fromcsv_utf8 = new File(csvpath, "NormalDtAlgeSet_UTF8.csv");
	static protected final File fromxml = new File(xmlpath, "NormalDtAlgeSet.xml");
	
	static private final Object[] testAlge = DtAlgeSetIOTest.normalAlges[DtAlgeSetIOTest.normalAlges.length-1];

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
		assertTrue(DtalgeTest.equalElementSequence(ret, testAlge));
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
		assertTrue(DtalgeTest.equalElementSequence(ret, testAlge));
		
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
		assertTrue(DtalgeTest.equalElementSequence(ret, testAlge));
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
		assertTrue(DtalgeTest.equalElementSequence(ret, testAlge));
	}

	/**
	 * Test method for {@link dtalge.Dtalge#toCSV(java.io.File)}.
	 */
	public void testToCSVFile() {
		File target = tocsv_def;
		Dtalge data = new Dtalge(testAlge);

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
			assertTrue(DtalgeTest.equalElementSequence(ret, testAlge));
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
		Dtalge data = new Dtalge(testAlge);

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
			assertTrue(DtalgeTest.equalElementSequence(ret, testAlge));
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
			assertTrue(DtalgeTest.equalElementSequence(ret, testAlge));
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
		Dtalge data = new Dtalge(testAlge);

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
			assertTrue(DtalgeTest.equalElementSequence(ret, testAlge));
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from XML file \"" + target.getPath() + "\"");
		}
	}

}
