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
 * @(#)DtalgeTableIOTest.java	0.40	2012/05/23
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtalgeTableIOTest.java	0.20	2010/03/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge;

import static dtalge.AllUnitTests.SJIS;
import static dtalge.AllUnitTests.UTF8;
import static dtalge.AllUnitTests.tablepath;

import java.io.File;
import java.math.BigDecimal;

import junit.framework.TestCase;
import dtalge.exception.CsvFormatException;

/**
 * <code>Dtalge</code> でのテーブル形式のCSVファイル入出力機能のユニットテスト。
 * 
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class DtalgeTableIOTest extends TestCase
{
	static protected final Object[] normalAlge1 = {
		DtAlgeSetTableIOTest.baseName, null,
		DtAlgeSetTableIOTest.baseSex , null,
		DtAlgeSetTableIOTest.baseAge , null,
		DtAlgeSetTableIOTest.baseAddr, null,
		DtAlgeSetTableIOTest.baseFlag, null,
	};
	static protected final Object[] normalAlge2 = {
		DtAlgeSetTableIOTest.baseName, "岡本",
		DtAlgeSetTableIOTest.baseAge , new BigDecimal(18),
		DtAlgeSetTableIOTest.baseFlag, Boolean.TRUE,
	};
	static protected final Object[] normalAlge3 = {
		DtAlgeSetTableIOTest.baseName, "岡本",
		DtAlgeSetTableIOTest.baseSex , "",
		DtAlgeSetTableIOTest.baseAge , new BigDecimal(18),
		DtAlgeSetTableIOTest.baseAddr, null,
		DtAlgeSetTableIOTest.baseFlag, Boolean.TRUE,
	};
	static protected final Object[] normalAlge4 = {
		DtAlgeSetTableIOTest.baseName, "岡本",
		DtAlgeSetTableIOTest.baseSex , null,
		DtAlgeSetTableIOTest.baseAge , new BigDecimal(18),
		DtAlgeSetTableIOTest.baseAddr, null,
		DtAlgeSetTableIOTest.baseFlag, Boolean.TRUE,
	};
	static protected final Object[] normalAlge5 = {
		DtAlgeSetTableIOTest.baseName, "岡本",
		DtAlgeSetTableIOTest.baseSex , "女性",
		DtAlgeSetTableIOTest.baseAge , new BigDecimal(18),
		DtAlgeSetTableIOTest.baseAddr, "新潟県",
		DtAlgeSetTableIOTest.baseFlag, Boolean.TRUE,
	};
	
	static protected final Dtalge[] outputAlges = {
		new Dtalge(),
		new Dtalge(normalAlge1),
		new Dtalge(normalAlge2),
		new Dtalge(normalAlge3),
		new Dtalge(normalAlge4),
		new Dtalge(normalAlge5),
	};
	
	static protected final Object[][] checkOutputAlges = {
		{},
		normalAlge1,
		normalAlge2,
		normalAlge4,
		normalAlge4,
		normalAlge5,
	};
	
	static protected final Object[][] checkInputAlges = {
		{},
		{},
		{},
		normalAlge1,
		normalAlge5,
		normalAlge5,
		normalAlge5,
	};
	
	static protected final File[] tocsv_utf8 = {
		new File(tablepath, "OutputTableAlge0_UTF8.csv"),
		new File(tablepath, "OutputTableAlge1_UTF8.csv"),
		new File(tablepath, "OutputTableAlge2_UTF8.csv"),
		new File(tablepath, "OutputTableAlge3_UTF8.csv"),
		new File(tablepath, "OutputTableAlge4_UTF8.csv"),
		new File(tablepath, "OutputTableAlge5_UTF8.csv"),
	};
	static protected final File[] tocsv_sjis = {
		new File(tablepath, "OutputTableAlge0_SJIS.csv"),
		new File(tablepath, "OutputTableAlge1_SJIS.csv"),
		new File(tablepath, "OutputTableAlge2_SJIS.csv"),
		new File(tablepath, "OutputTableAlge3_SJIS.csv"),
		new File(tablepath, "OutputTableAlge4_SJIS.csv"),
		new File(tablepath, "OutputTableAlge5_SJIS.csv"),
	};
	static protected final File[] tocsv_def  = {
		new File(tablepath, "OutputTableAlge0.csv"),
		new File(tablepath, "OutputTableAlge1.csv"),
		new File(tablepath, "OutputTableAlge2.csv"),
		new File(tablepath, "OutputTableAlge3.csv"),
		new File(tablepath, "OutputTableAlge4.csv"),
		new File(tablepath, "OutputTableAlge5.csv"),
	};

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
		// Illegal data
		System.out.println("\n<<< Test for 'Dtalge#fromCSV(File)' by illegal table csv >>>");
		for (File target : DtAlgeSetTableIOTest.err_fromcsv_sjis) {
			System.out.println("--- load \"" + target.toString() + "\"");
			try {
				Dtalge.fromCSV(target);
				fail("must be throw CsvFormatException.");
			}
			catch (CsvFormatException ex) {
				System.out.println("    - cought exception : " + ex.toString());
			}
			catch (Exception ex) {
				fail("Failed to read from CSV file : " + ex.toString());
			}
		}
		
		// Normal data
		System.out.println("\n<<< Test for 'Dtalge#fromCSV(File)' by normal table csv >>>");
		for (int i = 0; i < DtAlgeSetTableIOTest.fromcsv_def.length; i++) {
			File target = DtAlgeSetTableIOTest.fromcsv_def[i];
			Dtalge ret = null;
			System.out.println("--- load \"" + target.toString() + "\"");
			try {
				ret = Dtalge.fromCSV(target);
			}
			catch (Throwable ex) {
				fail("Failed to read from CSV file : " + ex.toString());
			}
			assertTrue(DtalgeTest.equalElementSequence(ret, checkInputAlges[i]));
		}
	}

	/**
	 * Test method for {@link dtalge.Dtalge#fromCSV(java.io.File, java.lang.String)}.
	 */
	public void testFromCSVFileString() {
		// Illegal data
		System.out.println("\n<<< Test for 'Dtalge#fromCSV(File,\"MS932\")' by illegal table csv >>>");
		for (File target : DtAlgeSetTableIOTest.err_fromcsv_sjis) {
			System.out.println("--- load \"" + target.toString() + "\"");
			try {
				Dtalge.fromCSV(target, SJIS);
				fail("must be throw CsvFormatException.");
			}
			catch (CsvFormatException ex) {
				System.out.println("    - cought exception : " + ex.toString());
			}
			catch (Exception ex) {
				fail("Failed to read from CSV file : " + ex.toString());
			}
		}
		
		// Normal data(SJIS)
		System.out.println("\n<<< Test for 'Dtalge#fromCSV(File,\"MS932\")' by normal table csv >>>");
		for (int i = 0; i < DtAlgeSetTableIOTest.fromcsv_sjis.length; i++) {
			File target = DtAlgeSetTableIOTest.fromcsv_sjis[i];
			Dtalge ret = null;
			System.out.println("--- load \"" + target.toString() + "\"");
			try {
				ret = Dtalge.fromCSV(target, SJIS);
			}
			catch (Throwable ex) {
				fail("Failed to read from CSV file : " + ex.toString());
			}
			assertTrue(DtalgeTest.equalElementSequence(ret, checkInputAlges[i]));
		}
		
		// Normal data(UTF-8)
		System.out.println("\n<<< Test for 'Dtalge#fromCSV(File,\"UTF-8\")' by normal table csv >>>");
		for (int i = 0; i < DtAlgeSetTableIOTest.fromcsv_utf8.length; i++) {
			File target = DtAlgeSetTableIOTest.fromcsv_utf8[i];
			Dtalge ret = null;
			System.out.println("--- load \"" + target.toString() + "\"");
			try {
				ret = Dtalge.fromCSV(target, UTF8);
			}
			catch (Throwable ex) {
				fail("Failed to read from CSV file : " + ex.toString());
			}
			assertTrue(DtalgeTest.equalElementSequence(ret, checkInputAlges[i]));
		}
	}

	/**
	 * Test method for {@link dtalge.Dtalge#toTableCSV(java.io.File)}.
	 */
	public void testToTableCsvFile() {
		System.out.println("\n<<< Test for 'Dtalge#toTableCSV(File)' >>>");
		for (int i = 0; i < outputAlges.length; i++) {
			File target = tocsv_def[i];
			Dtalge alge = outputAlges[i];
			//--- write
			System.out.println("--- write outputAlges[" + i + "] to \"" + target.toString() + "\"");
			try {
				alge.toTableCSV(target);
			}
			catch (Exception ex) {
				fail("Failed to write to table CSV file : " + ex.toString());
			}
			//--- check
			System.out.println("--- check for read from \"" + target.toString() + "\"");
			Dtalge ret = null;
			try {
				ret = Dtalge.fromCSV(target);
			}
			catch (Exception ex) {
				fail("Failed to read from table CSV file : " + ex.toString());
			}
			assertTrue(DtalgeTest.equalElementSequence(ret, checkOutputAlges[i]));
			System.out.println("   ...O.K!");
		}
	}

	/**
	 * Test method for {@link dtalge.Dtalge#toTableCSV(java.io.File, java.lang.String)}.
	 */
	public void testToTableCsvFileString() {
		// Write (SJIS)
		System.out.println("\n<<< Test for 'Dtalge#toTableCSV(File,\"MS932\")' >>>");
		for (int i = 0; i < outputAlges.length; i++) {
			File target = tocsv_sjis[i];
			Dtalge alge = outputAlges[i];
			//--- write
			System.out.println("--- write outputAlges[" + i + "] to \"" + target.toString() + "\"");
			try {
				alge.toTableCSV(target, SJIS);
			}
			catch (Exception ex) {
				fail("Failed to write to table CSV file : " + ex.toString());
			}
			//--- check
			System.out.println("--- check for read from \"" + target.toString() + "\"");
			Dtalge ret = null;
			try {
				ret = Dtalge.fromCSV(target, SJIS);
			}
			catch (Exception ex) {
				fail("Failed to read from table CSV file : " + ex.toString());
			}
			assertTrue(DtalgeTest.equalElementSequence(ret, checkOutputAlges[i]));
			System.out.println("   ...O.K!");
		}
		
		// Write (UTF-8)
		System.out.println("\n<<< Test for 'Dtalge#toTableCSV(File,\"UTF-8\")' >>>");
		for (int i = 0; i < outputAlges.length; i++) {
			File target = tocsv_utf8[i];
			Dtalge alge = outputAlges[i];
			//--- write
			System.out.println("--- write outputAlges[" + i + "] to \"" + target.toString() + "\"");
			try {
				alge.toTableCSV(target, UTF8);
			}
			catch (Exception ex) {
				fail("Failed to write to table CSV file : " + ex.toString());
			}
			//--- check
			System.out.println("--- check for read from \"" + target.toString() + "\"");
			Dtalge ret = null;
			try {
				ret = Dtalge.fromCSV(target, UTF8);
			}
			catch (Exception ex) {
				fail("Failed to read from table CSV file : " + ex.toString());
			}
			assertTrue(DtalgeTest.equalElementSequence(ret, checkOutputAlges[i]));
			System.out.println("   ...O.K!");
		}
	}

}
