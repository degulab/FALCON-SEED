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
 * @(#)DtalgeTableIOTest2.java	0.40	2012/05/30
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge;

import static dtalge.AllUnitTests.SJIS;
import static dtalge.AllUnitTests.UTF8;
import static dtalge.AllUnitTests.tablepath;

import java.io.File;
import java.util.Arrays;

import junit.framework.TestCase;
import dtalge.exception.CsvFormatException;

/**
 * <code>Dtalge</code> でのバージョン 2 テーブル形式のCSVファイル入出力機能のユニットテスト。
 * 
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class DtalgeTableIOTest2 extends TestCase
{
	static protected final Dtalge[] outputAlges = {
		new Dtalge(),
		new Dtalge(DtalgeTableIOTest.normalAlge1),
		new Dtalge(DtalgeTableIOTest.normalAlge2),
		new Dtalge(DtalgeTableIOTest.normalAlge3),
		new Dtalge(DtalgeTableIOTest.normalAlge4),
		new Dtalge(DtalgeTableIOTest.normalAlge5),
		new Dtalge(DtalgeIOTest2.writeRandomAlgeData),
	};
	
	static protected final Object[][] checkOutputAlges = {
		{},
		DtalgeTableIOTest.normalAlge1,
		DtalgeTableIOTest.normalAlge2,
		DtalgeTableIOTest.normalAlge4,
		DtalgeTableIOTest.normalAlge4,
		DtalgeTableIOTest.normalAlge5,
		DtalgeIOTest2.readRandomAlgeData,
	};
	
	static protected final Object[][] checkInputAlges = {
		{},
		{},
		{},
		{},
		DtalgeTableIOTest.normalAlge5,
		DtalgeTableIOTest.normalAlge5,
		DtalgeTableIOTest.normalAlge5,
		DtalgeIOTest2.readRandomAlgeData,
	};
	
	static protected final File[] tocsv_utf8 = {
		new File(tablepath, "v2_OutputTableAlge0_UTF8.csv"),
		new File(tablepath, "v2_OutputTableAlge1_UTF8.csv"),
		new File(tablepath, "v2_OutputTableAlge2_UTF8.csv"),
		new File(tablepath, "v2_OutputTableAlge3_UTF8.csv"),
		new File(tablepath, "v2_OutputTableAlge4_UTF8.csv"),
		new File(tablepath, "v2_OutputTableAlge5_UTF8.csv"),
		new File(tablepath, "v2_OutputTableAlge6_UTF8.csv"),
	};
	static protected final File[] tocsv_sjis = {
		new File(tablepath, "v2_OutputTableAlge0_SJIS.csv"),
		new File(tablepath, "v2_OutputTableAlge1_SJIS.csv"),
		new File(tablepath, "v2_OutputTableAlge2_SJIS.csv"),
		new File(tablepath, "v2_OutputTableAlge3_SJIS.csv"),
		new File(tablepath, "v2_OutputTableAlge4_SJIS.csv"),
		new File(tablepath, "v2_OutputTableAlge5_SJIS.csv"),
		new File(tablepath, "v2_OutputTableAlge6_SJIS.csv"),
	};
	static protected final File[] tocsv_def  = {
		new File(tablepath, "v2_OutputTableAlge0.csv"),
		new File(tablepath, "v2_OutputTableAlge1.csv"),
		new File(tablepath, "v2_OutputTableAlge2.csv"),
		new File(tablepath, "v2_OutputTableAlge3.csv"),
		new File(tablepath, "v2_OutputTableAlge4.csv"),
		new File(tablepath, "v2_OutputTableAlge5.csv"),
		new File(tablepath, "v2_OutputTableAlge6.csv"),
	};

	static private class TableCsvParam {
		public final Dtalge		srcAlge;
		public final DtBaseSet		baseOrder;
		public final boolean		withoutNull;
		public final Object[]		algeSequence;
		
		public TableCsvParam(Dtalge pSourceAlge, DtBaseSet pBaseOrder, boolean pWithoutNull, Object[] pSequence) {
			srcAlge      = pSourceAlge;
			baseOrder    = pBaseOrder;
			withoutNull  = pWithoutNull;
			algeSequence = pSequence;
		}
	}
	
	static private final Object[]		EmptyAlgeSequence = {};
	static private final Dtalge		EmptyAlge = new Dtalge();
	static private final DtBaseSet	EmptyBaseOrder = new DtBaseSet();
	static private final DtBaseSet	HalfBaseOrder = new DtBaseSet(Arrays.asList(DtAlgeSetIOTest2.halfSortedAllBaseSetData));
	static private final DtBaseSet	AllBaseOrder = new DtBaseSet(Arrays.asList(DtAlgeSetIOTest2.sortedAllBaseSetData));
	
	static private final TableCsvParam[] checkTableCsvParamData = {
		// [ 0] Empty, No order, With null
		new TableCsvParam(EmptyAlge, null          , false, EmptyAlgeSequence),
		// [ 1] Empty, No order, No null
		new TableCsvParam(EmptyAlge, null          , true , EmptyAlgeSequence),
		// [ 2] Empty, No order, With null
		new TableCsvParam(EmptyAlge, EmptyBaseOrder, false, EmptyAlgeSequence),
		// [ 3] Empty, No order, No null
		new TableCsvParam(EmptyAlge, EmptyBaseOrder, true, EmptyAlgeSequence),
		// [ 4] Empty, With order, With null
		new TableCsvParam(EmptyAlge, AllBaseOrder  , false, EmptyAlgeSequence),
		// [ 5] Empty, With order, No null
		new TableCsvParam(EmptyAlge, AllBaseOrder  , true , EmptyAlgeSequence),
		// [ 6] Random, No order, With null
		new TableCsvParam(new Dtalge(DtalgeIOTest2.writeRandomAlgeData), null          , false, DtalgeIOTest2.readRandomAlgeData),
		// [ 7] Random, No order, No null
		new TableCsvParam(new Dtalge(DtalgeIOTest2.writeRandomAlgeData), null          , true , DtalgeIOTest2.readRandomAlgeData),
		// [ 8] Ramdon, No order, With null
		new TableCsvParam(new Dtalge(DtalgeIOTest2.writeRandomAlgeData), EmptyBaseOrder, false, DtalgeIOTest2.readRandomAlgeData),
		// [ 9] Random, No order, No null
		new TableCsvParam(new Dtalge(DtalgeIOTest2.writeRandomAlgeData), EmptyBaseOrder, true, DtalgeIOTest2.readRandomAlgeData),
		// [10] Random, With order, With null
		new TableCsvParam(new Dtalge(DtalgeIOTest2.writeRandomAlgeData), AllBaseOrder  , false, DtalgeIOTest2.readSortedAlgeData),
		// [11] Random, With order, No null
		new TableCsvParam(new Dtalge(DtalgeIOTest2.writeRandomAlgeData), AllBaseOrder  , true , DtalgeIOTest2.readSortedAlgeData),
		// [12] Random, With order, With null
		new TableCsvParam(new Dtalge(DtalgeIOTest2.writeRandomAlgeData), HalfBaseOrder  , false, DtalgeIOTest2.readHalfSortedAlgeData),
		// [13] Random, With order, No null
		new TableCsvParam(new Dtalge(DtalgeIOTest2.writeRandomAlgeData), HalfBaseOrder  , true , DtalgeIOTest2.readHalfSortedAlgeData),
	};
	
	static private final File[] toTableCsv_utf8 = {
		new File(tablepath, "v2_OutputTableAlge00_UTF8_NoOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlge01_UTF8_NoOrderNoNull.csv"),
		new File(tablepath, "v2_OutputTableAlge02_UTF8_NoOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlge03_UTF8_NoOrderNoNull.csv"),
		new File(tablepath, "v2_OutputTableAlge04_UTF8_WithOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlge05_UTF8_WithOrderNoNull.csv"),
		new File(tablepath, "v2_OutputTableAlge06_UTF8_NoOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlge07_UTF8_NoOrderNoNull.csv"),
		new File(tablepath, "v2_OutputTableAlge08_UTF8_NoOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlge09_UTF8_NoOrderNoNull.csv"),
		new File(tablepath, "v2_OutputTableAlge10_UTF8_WithOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlge11_UTF8_WithOrderNoNull.csv"),
		new File(tablepath, "v2_OutputTableAlge12_UTF8_WithOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlge13_UTF8_WithOrderNoNull.csv"),
	};
	
	static private final File[] toTableCsv_sjis = {
		new File(tablepath, "v2_OutputTableAlge00_SJIS_NoOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlge01_SJIS_NoOrderNoNull.csv"),
		new File(tablepath, "v2_OutputTableAlge02_SJIS_NoOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlge03_SJIS_NoOrderNoNull.csv"),
		new File(tablepath, "v2_OutputTableAlge04_SJIS_WithOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlge05_SJIS_WithOrderNoNull.csv"),
		new File(tablepath, "v2_OutputTableAlge06_SJIS_NoOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlge07_SJIS_NoOrderNoNull.csv"),
		new File(tablepath, "v2_OutputTableAlge08_SJIS_NoOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlge09_SJIS_NoOrderNoNull.csv"),
		new File(tablepath, "v2_OutputTableAlge10_SJIS_WithOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlge11_SJIS_WithOrderNoNull.csv"),
		new File(tablepath, "v2_OutputTableAlge12_SJIS_WithOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlge13_SJIS_WithOrderNoNull.csv"),
	};
	
	static private final File[] toTableCsv_def = {
		new File(tablepath, "v2_OutputTableAlge00_NoOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlge01_NoOrderNoNull.csv"),
		new File(tablepath, "v2_OutputTableAlge02_NoOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlge03_NoOrderNoNull.csv"),
		new File(tablepath, "v2_OutputTableAlge04_WithOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlge05_WithOrderNoNull.csv"),
		new File(tablepath, "v2_OutputTableAlge06_NoOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlge07_NoOrderNoNull.csv"),
		new File(tablepath, "v2_OutputTableAlge08_NoOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlge09_NoOrderNoNull.csv"),
		new File(tablepath, "v2_OutputTableAlge10_WithOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlge11_WithOrderNoNull.csv"),
		new File(tablepath, "v2_OutputTableAlge12_WithOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlge13_WithOrderNoNull.csv"),
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
		for (File target : DtAlgeSetTableIOTest2.err_fromcsv_sjis) {
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
		for (int i = 0; i < DtAlgeSetTableIOTest2.fromcsv_def.length; i++) {
			File target = DtAlgeSetTableIOTest2.fromcsv_def[i];
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
		for (File target : DtAlgeSetTableIOTest2.err_fromcsv_sjis) {
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
		for (int i = 0; i < DtAlgeSetTableIOTest2.fromcsv_sjis.length; i++) {
			File target = DtAlgeSetTableIOTest2.fromcsv_sjis[i];
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
		for (int i = 0; i < DtAlgeSetTableIOTest2.fromcsv_utf8.length; i++) {
			File target = DtAlgeSetTableIOTest2.fromcsv_utf8[i];
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

	/**
	 * Test method for {@link dtalge.Dtalge#toTableCsvWithBaseOrder(DtBaseSet, File)}.
	 */
	public void testToTableCsvWithBaseOrderDtBaseSetFile() {
		System.out.println("\n<<< Test for 'Dtalge#toTableCsvWithBaseOrder(DtBaseSet, File)' by normal table csv >>>");
		assertEquals(checkTableCsvParamData.length, toTableCsv_def.length);
		for (int i = 0; i < checkTableCsvParamData.length; i++) {
			File target = toTableCsv_def[i];
			Dtalge    alge        = checkTableCsvParamData[i].srcAlge;
			DtBaseSet baseOrder   = checkTableCsvParamData[i].baseOrder;
			boolean  withoutNull = checkTableCsvParamData[i].withoutNull;
			if (withoutNull) {
				System.out.println("--- skip writing checkTableCsvParamData[" + i + "] to \"" + target.toString() + "\"");
				continue;
			}
			//--- write
			System.out.println("--- write checkTableCsvParamData[" + i + "] to \"" + target.toString() + "\"");
			try {
				alge.toTableCsvWithBaseOrder(baseOrder, target);
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
			assertTrue(DtalgeTest.equalElementSequence(ret, checkTableCsvParamData[i].algeSequence));
			System.out.println("   ...O.K!");
		}
	}

	/**
	 * Test method for {@link dtalge.Dtalge#toTableCsvWithBaseOrder(DtBaseSet, File, String)}.
	 */
	public void testToTableCsvWithBaseOrderDtBaseSetFileString() {
		// Write (SJIS)
		System.out.println("\n<<< Test for 'Dtalge#toTableCsvWithBaseOrder(DtBaseSet, File, \"MS932\")' by normal table csv >>>");
		assertEquals(checkTableCsvParamData.length, toTableCsv_sjis.length);
		for (int i = 0; i < checkTableCsvParamData.length; i++) {
			File target = toTableCsv_sjis[i];
			Dtalge    alge        = checkTableCsvParamData[i].srcAlge;
			DtBaseSet baseOrder   = checkTableCsvParamData[i].baseOrder;
			boolean  withoutNull = checkTableCsvParamData[i].withoutNull;
			if (withoutNull) {
				System.out.println("--- skip writing checkTableCsvParamData[" + i + "] to \"" + target.toString() + "\"");
				continue;
			}
			//--- write
			System.out.println("--- write checkTableCsvParamData[" + i + "] to \"" + target.toString() + "\"");
			try {
				alge.toTableCsvWithBaseOrder(baseOrder, target, SJIS);
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
			assertTrue(DtalgeTest.equalElementSequence(ret, checkTableCsvParamData[i].algeSequence));
			System.out.println("   ...O.K!");
		}
		
		// Write (UTF-8)
		System.out.println("\n<<< Test for 'Dtalge#toTableCsvWithBaseOrder(DtBaseSet, File, \"UTF-8\")' by normal table csv >>>");
		assertEquals(checkTableCsvParamData.length, toTableCsv_utf8.length);
		for (int i = 0; i < checkTableCsvParamData.length; i++) {
			File target = toTableCsv_utf8[i];
			Dtalge    alge        = checkTableCsvParamData[i].srcAlge;
			DtBaseSet baseOrder   = checkTableCsvParamData[i].baseOrder;
			boolean  withoutNull = checkTableCsvParamData[i].withoutNull;
			if (withoutNull) {
				System.out.println("--- skip writing checkTableCsvParamData[" + i + "] to \"" + target.toString() + "\"");
				continue;
			}
			//--- write
			System.out.println("--- write checkTableCsvParamData[" + i + "] to \"" + target.toString() + "\"");
			try {
				alge.toTableCsvWithBaseOrder(baseOrder, target, UTF8);
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
			assertTrue(DtalgeTest.equalElementSequence(ret, checkTableCsvParamData[i].algeSequence));
			System.out.println("   ...O.K!");
		}
	}

	/**
	 * Test method for {@link dtalge.Dtalge#toTableCsvWithoutNull(File)}.
	 */
	public void testToTableCsvWithoutNullFile() {
		System.out.println("\n<<< Test for 'Dtalge#toTableCsvWithoutNull(File)' by normal table csv >>>");
		assertEquals(checkTableCsvParamData.length, toTableCsv_def.length);
		for (int i = 0; i < checkTableCsvParamData.length; i++) {
			File target = toTableCsv_def[i];
			Dtalge    alge        = checkTableCsvParamData[i].srcAlge;
			DtBaseSet baseOrder   = checkTableCsvParamData[i].baseOrder;
			boolean  withoutNull = checkTableCsvParamData[i].withoutNull;
			if (!withoutNull || (baseOrder != null && !baseOrder.isEmpty())) {
				System.out.println("--- skip writing checkTableCsvParamData[" + i + "] to \"" + target.toString() + "\"");
				continue;
			}
			//--- write
			System.out.println("--- write checkTableCsvParamData[" + i + "] to \"" + target.toString() + "\"");
			try {
				alge.toTableCsvWithoutNull(target);
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
			assertTrue(DtalgeTest.equalNoNullElementSequence(ret, checkTableCsvParamData[i].algeSequence));
			System.out.println("   ...O.K!");
		}
	}

	/**
	 * Test method for {@link dtalge.Dtalge#toTableCsvWithoutNull(File, String)}.
	 */
	public void testToTableCsvWithoutNullFileString() {
		// Write (SJIS)
		System.out.println("\n<<< Test for 'Dtalge#toTableCsvWithoutNull(File, \"MS932\")' by normal table csv >>>");
		assertEquals(checkTableCsvParamData.length, toTableCsv_sjis.length);
		for (int i = 0; i < checkTableCsvParamData.length; i++) {
			File target = toTableCsv_sjis[i];
			Dtalge    alge        = checkTableCsvParamData[i].srcAlge;
			DtBaseSet baseOrder   = checkTableCsvParamData[i].baseOrder;
			boolean  withoutNull = checkTableCsvParamData[i].withoutNull;
			if (!withoutNull || (baseOrder != null && !baseOrder.isEmpty())) {
				System.out.println("--- skip writing checkTableCsvParamData[" + i + "] to \"" + target.toString() + "\"");
				continue;
			}
			//--- write
			System.out.println("--- write checkTableCsvParamData[" + i + "] to \"" + target.toString() + "\"");
			try {
				alge.toTableCsvWithoutNull(target, SJIS);
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
			assertTrue(DtalgeTest.equalNoNullElementSequence(ret, checkTableCsvParamData[i].algeSequence));
			System.out.println("   ...O.K!");
		}
		
		// Write (UTF-8)
		System.out.println("\n<<< Test for 'Dtalge#toTableCsvWithoutNull(File, \"UTF-8\")' by normal table csv >>>");
		assertEquals(checkTableCsvParamData.length, toTableCsv_utf8.length);
		for (int i = 0; i < checkTableCsvParamData.length; i++) {
			File target = toTableCsv_utf8[i];
			Dtalge    alge        = checkTableCsvParamData[i].srcAlge;
			DtBaseSet baseOrder   = checkTableCsvParamData[i].baseOrder;
			boolean  withoutNull = checkTableCsvParamData[i].withoutNull;
			if (!withoutNull || (baseOrder != null && !baseOrder.isEmpty())) {
				System.out.println("--- skip writing checkTableCsvParamData[" + i + "] to \"" + target.toString() + "\"");
				continue;
			}
			//--- write
			System.out.println("--- write checkTableCsvParamData[" + i + "] to \"" + target.toString() + "\"");
			try {
				alge.toTableCsvWithoutNull(target, UTF8);
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
			assertTrue(DtalgeTest.equalNoNullElementSequence(ret, checkTableCsvParamData[i].algeSequence));
			System.out.println("   ...O.K!");
		}
	}

	/**
	 * Test method for {@link dtalge.Dtalge#toTableCSV(DtBaseSet, boolean, File)}.
	 */
	public void testToTableCsvFileDtBaseSetBoolean() {
		System.out.println("\n<<< Test for 'Dtalge#toTableCSV(DtBaseSet, boolean, File)' by normal table csv >>>");
		assertEquals(checkTableCsvParamData.length, toTableCsv_def.length);
		for (int i = 0; i < checkTableCsvParamData.length; i++) {
			File target = toTableCsv_def[i];
			Dtalge    alge        = checkTableCsvParamData[i].srcAlge;
			DtBaseSet baseOrder   = checkTableCsvParamData[i].baseOrder;
			boolean  withoutNull = checkTableCsvParamData[i].withoutNull;
			//--- write
			System.out.println("--- write checkTableCsvParamData[" + i + "] to \"" + target.toString() + "\"");
			try {
				alge.toTableCSV(baseOrder, withoutNull, target);
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
			if (withoutNull) {
				assertTrue(DtalgeTest.equalNoNullElementSequence(ret, checkTableCsvParamData[i].algeSequence));
			} else {
				assertTrue(DtalgeTest.equalElementSequence(ret, checkTableCsvParamData[i].algeSequence));
			}
			System.out.println("   ...O.K!");
		}
	}

	/**
	 * Test method for {@link dtalge.Dtalge#toTableCSV(DtBaseSet, boolean, File, String)}.
	 */
	public void testToTableCsvFileStringDtBaseSetBoolean() {
		// Write (SJIS)
		System.out.println("\n<<< Test for 'Dtalge#toTableCSV(DtBaseSet, boolean, File, \"MS932\")' by normal table csv >>>");
		assertEquals(checkTableCsvParamData.length, toTableCsv_sjis.length);
		for (int i = 0; i < checkTableCsvParamData.length; i++) {
			File target = toTableCsv_sjis[i];
			Dtalge    alge        = checkTableCsvParamData[i].srcAlge;
			DtBaseSet baseOrder   = checkTableCsvParamData[i].baseOrder;
			boolean  withoutNull = checkTableCsvParamData[i].withoutNull;
			//--- write
			System.out.println("--- write checkTableCsvParamData[" + i + "] to \"" + target.toString() + "\"");
			try {
				alge.toTableCSV(baseOrder, withoutNull, target, SJIS);
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
			if (withoutNull) {
				assertTrue(DtalgeTest.equalNoNullElementSequence(ret, checkTableCsvParamData[i].algeSequence));
			} else {
				assertTrue(DtalgeTest.equalElementSequence(ret, checkTableCsvParamData[i].algeSequence));
			}
			System.out.println("   ...O.K!");
		}
		
		// Write (UTF-8)
		System.out.println("\n<<< Test for 'Dtalge#toTableCSV(DtBaseSet, boolean, File, \"UTF-8\")' by normal table csv >>>");
		assertEquals(checkTableCsvParamData.length, toTableCsv_utf8.length);
		for (int i = 0; i < checkTableCsvParamData.length; i++) {
			File target = toTableCsv_utf8[i];
			Dtalge    alge        = checkTableCsvParamData[i].srcAlge;
			DtBaseSet baseOrder   = checkTableCsvParamData[i].baseOrder;
			boolean  withoutNull = checkTableCsvParamData[i].withoutNull;
			//--- write
			System.out.println("--- write checkTableCsvParamData[" + i + "] to \"" + target.toString() + "\"");
			try {
				alge.toTableCSV(baseOrder, withoutNull, target, UTF8);
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
			if (withoutNull) {
				assertTrue(DtalgeTest.equalNoNullElementSequence(ret, checkTableCsvParamData[i].algeSequence));
			} else {
				assertTrue(DtalgeTest.equalElementSequence(ret, checkTableCsvParamData[i].algeSequence));
			}
			System.out.println("   ...O.K!");
		}
	}
}
