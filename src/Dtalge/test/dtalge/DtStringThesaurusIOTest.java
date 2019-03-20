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
 *  Copyright 2007-2011  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
/*
 * @(#)DtStringThesaurusIOTest.java	0.30	2011/03/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtStringThesaurusIOTest.java	0.20	2010/02/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtStringThesaurusIOTest.java	0.10	2008/09/02
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge;

import static dtalge.AllUnitTests.*;

import java.io.File;

import junit.framework.TestCase;

/**
 * <code>DtStringThesaurus</code> のファイル入出力機能のユニットテスト。
 * 
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class DtStringThesaurusIOTest extends TestCase
{
	static protected final File tocsv_def = new File(csvpath, "OutputDtStringThesaurus.csv");
	static protected final File tocsv_sjis = new File(csvpath, "OutputDtStringThesaurus_SJIS.csv");
	static protected final File tocsv_utf8 = new File(csvpath, "OutputDtStringThesaurus_UTF8.csv");
	static protected final File toxml = new File(xmlpath, "OutputDtStringThesaurus.xml");
	static protected final File fromcsv_def = new File(csvpath, "NormalDtStringThesaurus.csv");
	static protected final File fromcsv_sjis = new File(csvpath, "NormalDtStringThesaurus_SJIS.csv");
	static protected final File fromcsv_utf8 = new File(csvpath, "NormalDtStringThesaurus_UTF8.csv");
	static protected final File fromxml = new File(xmlpath, "NormalDtStringThesaurus.xml");
	static protected final File tocsv_mp_def  = new File(csvpath, "OutputDtStringThesMP.csv");
	static protected final File tocsv_mp_sjis = new File(csvpath, "OutputDtStringThesMP_SJIS.csv");
	static protected final File tocsv_mp_utf8 = new File(csvpath, "OutputDtStringThesMP_UTF8.csv");
	static protected final File toxml_mp = new File(xmlpath, "OutputDtStringThesMP.xml");
	static protected final File fromcsv_mp_def  = new File(csvpath, "NormalDtStringThesMP.csv");
	static protected final File fromcsv_mp_sjis = new File(csvpath, "NormalDtStringThesMP_SJIS.csv");
	static protected final File fromcsv_mp_utf8 = new File(csvpath, "NormalDtStringThesMP_UTF8.csv");
	static protected final File fromxml_mp = new File(xmlpath, "NormalDtStringThesMP.xml");
	
	static private final String[] thesPairs2 = {
		"A1", "A1B1",
		"A1", "A1B2",
		"A2", "A2B1",
		"A2", "A2B2",
		"A2", "A2B3",
		"A3", "A3B1",
		"A1B1", "A1B1C1",
		"A1B1", "A1B1C2",
		"A1B2", "A1B2C1",
		"A2B1", "A2B1C1",
		"A2B1", "A2B1C2",
		"A2B3", "A2B3C1",
		"A2B3", "A2B3C2",
		"A2B3", "A2B3C3",
		"A1B1C1", "A1B1C1D1",
		"A1B1C1", "A1B1C1D2",
		"A1B1C1D1", "A1B1C1D1E1",
		"A2B3C3", "A2B3C3D1",
		"A2B3C3", "A2B3C3D2",
		"A2B3C3", "A2B3C3D3",
		"A2B3C3D3", "A2B3C3D3E1",
		"A2B3C3D3E1", "A2B3C3D3E1F1",
		"A2B3C3D3E1F1", "A2B3C3D3E1F1G1",
	};
	
	static private final String[] thesMPPairs = {
		"P1","P2",
		"P1","P3",
		"P2","P4",
		"P2","P5",
		"P4","P6",
		"P5","P6",
		"P3","P7",
		"P6","P7",
		"N1","N2",
		"N1","N3",
		"N1","N4",
		"N2","N21",
		"N2","N22",
		"N2","N23",
		"N4","N41",
		"N41","N411",
		"N41","N412",
		"N1","N5",
		"N5","N51",
		"N5","N52",
		"N5","N53",
		"N5","N54",
		"N5","N55",
		"N51","N501",
		"N53","N501",
		"N55","N501",
		"N5","N501",
	};
	
	static private final DtStringThesaurus thesAns = DtStringThesaurusTest.newThesaurus(thesPairs2);
	static private final DtStringThesaurus thesMPAns = DtStringThesaurusTest.newThesaurus(thesMPPairs);

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * Test method for {@link dtalge.DtStringThesaurus#fromCSV(java.io.File)}.
	 */
	public void testFromCSVFile() {
		File target;
		DtStringThesaurus ret = new DtStringThesaurus();
		
		// Normal data
		target = fromcsv_def;
		//--- read
		try {
			ret = DtStringThesaurus.fromCSV(target);
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from CSV file \"" + target.getPath() + "\"");
		}
		//--- check
		assertEquals(ret, thesAns);
		
		// Normal Multi-parents data
		target = fromcsv_mp_def;
		//--- read
		try {
			ret = DtStringThesaurus.fromCSV(target);
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from CSV file \"" + target.getPath() + "\"");
		}
		//--- check
		assertEquals(ret, thesMPAns);
	}

	/**
	 * Test method for {@link dtalge.DtStringThesaurus#fromCSV(java.io.File, java.lang.String)}.
	 */
	public void testFromCSVFileString() {
		File target;
		DtStringThesaurus ret;
		
		//--------------------------------------
		// SJIS
		//--------------------------------------
		
		// Normal data
		target = fromcsv_sjis;
		ret = new DtStringThesaurus();
		//--- read
		try {
			ret = DtStringThesaurus.fromCSV(target, SJIS);
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from CSV file \"" + target.getPath() + "\"");
		}
		//--- check
		assertEquals(ret, thesAns);
		
		// Normal Multi-parents data
		target = fromcsv_mp_sjis;
		ret = new DtStringThesaurus();
		//--- read
		try {
			ret = DtStringThesaurus.fromCSV(target, SJIS);
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from CSV file \"" + target.getPath() + "\"");
		}
		//--- check
		assertEquals(ret, thesMPAns);
		
		//--------------------------------------
		// UTF-8
		//--------------------------------------
		
		// Normal data
		target = fromcsv_utf8;
		ret = new DtStringThesaurus();
		//--- read
		try {
			ret = DtStringThesaurus.fromCSV(target, UTF8);
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from CSV file \"" + target.getPath() + "\"");
		}
		//--- check
		assertEquals(ret, thesAns);
		
		// Normal Multi-parents data
		target = fromcsv_mp_utf8;
		ret = new DtStringThesaurus();
		//--- read
		try {
			ret = DtStringThesaurus.fromCSV(target, UTF8);
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from CSV file \"" + target.getPath() + "\"");
		}
		//--- check
		assertEquals(ret, thesMPAns);
	}

	/**
	 * Test method for {@link dtalge.DtStringThesaurus#fromXML(java.io.File)}.
	 */
	public void testFromXML() {
		File target;
		DtStringThesaurus ret = new DtStringThesaurus();
		
		// Normal data
		target = fromxml;
		//--- read
		try {
			ret = DtStringThesaurus.fromXML(target);
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from XML file \"" + target.getPath() + "\"");
		}
		//--- check
		assertEquals(ret, thesAns);
		
		// Normal Multi-parents data
		target = fromxml_mp;
		//--- read
		try {
			ret = DtStringThesaurus.fromXML(target);
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from XML file \"" + target.getPath() + "\"");
		}
		//--- check
		assertEquals(ret, thesMPAns);
	}

	/**
	 * Test method for {@link dtalge.DtStringThesaurus#toCSV(java.io.File)}.
	 */
	public void testToCSVFile() {
		File target;
		DtStringThesaurus data;

		// Write normal data
		target = tocsv_def;
		data = DtStringThesaurusTest.newThesaurus(thesPairs2);
		try {
			data.toCSV(target);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Failed to write to CSV file \"" + target.getPath() + "\"");
		}
		//--- check
		try {
			DtStringThesaurus ret = DtStringThesaurus.fromCSV(target);
			assertEquals(ret, thesAns);
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from CSV file \"" + target.getPath() + "\"");
		}

		// Write normal Multi-parents data
		target = tocsv_mp_def;
		data = DtStringThesaurusTest.newThesaurus(thesMPPairs);
		try {
			data.toCSV(target);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Failed to write to CSV file \"" + target.getPath() + "\"");
		}
		//--- check
		try {
			DtStringThesaurus ret = DtStringThesaurus.fromCSV(target);
			assertEquals(ret, thesMPAns);
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from CSV file \"" + target.getPath() + "\"");
		}
	}

	/**
	 * Test method for {@link dtalge.DtStringThesaurus#toCSV(java.io.File, java.lang.String)}.
	 */
	public void testToCSVFileString() {
		File target;
		DtStringThesaurus data;
		
		// Normal data
		data = DtStringThesaurusTest.newThesaurus(thesPairs2);

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
			DtStringThesaurus ret = DtStringThesaurus.fromCSV(target, SJIS);
			assertEquals(ret, thesAns);
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
			DtStringThesaurus ret = DtStringThesaurus.fromCSV(target, UTF8);
			assertEquals(ret, thesAns);
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from CSV file \"" + target.getPath() + "\"");
		}
		
		// Normal Multi-parents data
		data = DtStringThesaurusTest.newThesaurus(thesMPPairs);

		// SJIS
		//--- write
		target = tocsv_mp_sjis;
		try {
			data.toCSV(target, SJIS);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Failed to write to CSV file \"" + target.getPath() + "\"");
		}
		//--- check
		try {
			DtStringThesaurus ret = DtStringThesaurus.fromCSV(target, SJIS);
			assertEquals(ret, thesMPAns);
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from CSV file \"" + target.getPath() + "\"");
		}

		// UTF-8
		//--- write
		target = tocsv_mp_utf8;
		try {
			data.toCSV(target, UTF8);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Failed to write to CSV file \"" + target.getPath() + "\"");
		}
		//--- check
		try {
			DtStringThesaurus ret = DtStringThesaurus.fromCSV(target, UTF8);
			assertEquals(ret, thesMPAns);
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from CSV file \"" + target.getPath() + "\"");
		}
	}

	/**
	 * Test method for {@link dtalge.DtStringThesaurus#toXML(java.io.File)}.
	 */
	public void testToXMLFile() {
		File target;
		DtStringThesaurus data;

		// write normal data
		target = toxml;
		data = DtStringThesaurusTest.newThesaurus(thesPairs2);
		try {
			data.toXML(target);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Failed to write to XML file \"" + target.getPath() + "\"");
		}
		//--- check
		try {
			DtStringThesaurus ret = DtStringThesaurus.fromXML(target);
			assertEquals(ret, thesAns);
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from XML file \"" + target.getPath() + "\"");
		}

		// write normal Multi-parents data
		target = toxml_mp;
		data = DtStringThesaurusTest.newThesaurus(thesMPPairs);
		try {
			data.toXML(target);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Failed to write to XML file \"" + target.getPath() + "\"");
		}
		//--- check
		try {
			DtStringThesaurus ret = DtStringThesaurus.fromXML(target);
			assertEquals(ret, thesMPAns);
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from XML file \"" + target.getPath() + "\"");
		}
	}

}
