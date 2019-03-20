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
 * @(#)DtAlgeSetTableIOTest2.java	0.40	2012/05/30
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
 * <code>DtAlgeSet</code> でのバージョン 2 テーブル形式のCSVファイル入出力機能のユニットテスト。
 * 
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class DtAlgeSetTableIOTest2 extends TestCase
{
	static protected final DtAlgeSet[] outputAlges = {
		new DtAlgeSet(),
		new DtAlgeSet(Arrays.asList(new Dtalge())),
		new DtAlgeSet(DtAlgeSetTest.makeDtalges(DtAlgeSetTableIOTest.normalAlgeData2)),
		new DtAlgeSet(DtAlgeSetTest.makeDtalges(DtAlgeSetTableIOTest.normalAlgeData3)),
		new DtAlgeSet(DtAlgeSetTest.makeDtalges(DtAlgeSetTableIOTest.normalAlgeData4)),
		new DtAlgeSet(DtAlgeSetTest.makeDtalges(DtAlgeSetIOTest2.writeRandomAlgeSetData)),
	};
	
	static protected final Object[][][] checkOutputAlges = {
		{},
		{},
		DtAlgeSetTableIOTest.normalAlgeData2,
		DtAlgeSetTableIOTest.normalAlgeData3,
		DtAlgeSetTableIOTest.normalAlgeData4,
		DtAlgeSetIOTest2.readRandomTableAlgeSetData,
	};
	
	static protected final Object[][][] checkInputAlges = {
		{},
		{},
		{},
		{{},},
		DtAlgeSetTableIOTest.normalAlgeData5,
		DtAlgeSetTableIOTest.normalAlgeData5,
		DtAlgeSetTableIOTest.normalAlgeData5,
		DtAlgeSetIOTest2.readRandomTableAlgeSetData,
	};
	
	static protected final File[] tocsv_utf8 = {
		new File(tablepath, "v2_OutputTableAlgeSet0_UTF8.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet1_UTF8.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet2_UTF8.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet3_UTF8.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet4_UTF8.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet5_UTF8.csv"),
	};
	static protected final File[] tocsv_sjis = {
		new File(tablepath, "v2_OutputTableAlgeSet0_SJIS.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet1_SJIS.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet2_SJIS.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet3_SJIS.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet4_SJIS.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet5_SJIS.csv"),
	};
	static protected final File[] tocsv_def  = {
		new File(tablepath, "v2_OutputTableAlgeSet0.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet1.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet2.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet3.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet4.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet5.csv"),
	};
	
	static protected final File[] fromcsv_utf8 = {
		new File(tablepath, "v2_NormalTableAlge1_UTF8.csv"),
		new File(tablepath, "v2_NormalTableAlge2_UTF8.csv"),
		new File(tablepath, "v2_NormalTableAlge3_UTF8.csv"),
		new File(tablepath, "v2_NormalTableAlge4_UTF8.csv"),
		new File(tablepath, "v2_NormalTableAlge5_UTF8.csv"),
		new File(tablepath, "v2_NormalTableAlge6_UTF8.csv"),
		new File(tablepath, "v2_NormalTableAlge7_UTF8.csv"),
		new File(tablepath, "v2_NormalTableAlge8_UTF8.csv"),
	};
	static protected final File[] fromcsv_sjis = {
		new File(tablepath, "v2_NormalTableAlge1_SJIS.csv"),
		new File(tablepath, "v2_NormalTableAlge2_SJIS.csv"),
		new File(tablepath, "v2_NormalTableAlge3_SJIS.csv"),
		new File(tablepath, "v2_NormalTableAlge4_SJIS.csv"),
		new File(tablepath, "v2_NormalTableAlge5_SJIS.csv"),
		new File(tablepath, "v2_NormalTableAlge6_SJIS.csv"),
		new File(tablepath, "v2_NormalTableAlge7_SJIS.csv"),
		new File(tablepath, "v2_NormalTableAlge8_SJIS.csv"),
	};
	static protected final File[] fromcsv_def  = fromcsv_sjis;
	
	static protected final File[] err_fromcsv_sjis = {
		new File(tablepath, "v2_IllegalTableAlge1_SJIS.csv"),
		new File(tablepath, "v2_IllegalTableAlge2_SJIS.csv"),
		new File(tablepath, "v2_IllegalTableAlge3_SJIS.csv"),
		new File(tablepath, "v2_IllegalTableAlge4_SJIS.csv"),
		new File(tablepath, "v2_IllegalTableAlge5_SJIS.csv"),
		new File(tablepath, "v2_IllegalTableAlge6_SJIS.csv"),
		new File(tablepath, "v2_IllegalTableAlge7_SJIS.csv"),
		new File(tablepath, "v2_IllegalTableAlge8_SJIS.csv"),
		new File(tablepath, "v2_IllegalTableAlge9_SJIS.csv"),
		new File(tablepath, "v2_IllegalTableAlge10_SJIS.csv"),
		new File(tablepath, "v2_IllegalTableAlge11_SJIS.csv"),
		new File(tablepath, "v2_IllegalTableAlge12_SJIS.csv"),
		new File(tablepath, "v2_IllegalTableAlge13_SJIS.csv"),
	};
	static protected final File[] err_fromcsv_def  = err_fromcsv_sjis;

	static private class TableCsvParam {
		public final DtAlgeSet		srcAlge;
		public final DtBaseSet		baseOrder;
		public final boolean		withoutNull;
		public final Object[][]	algeSequence;
		
		public TableCsvParam(DtAlgeSet pSourceAlge, DtBaseSet pBaseOrder, boolean pWithoutNull, Object[][] pSequence) {
			srcAlge      = pSourceAlge;
			baseOrder    = pBaseOrder;
			withoutNull  = pWithoutNull;
			algeSequence = pSequence;
		}
	}
	
	static private final Object[][]	EmptyAlgeSequence = {};
	static private final DtAlgeSet	EmptyAlge = new DtAlgeSet();
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
		new TableCsvParam(new DtAlgeSet(DtAlgeSetTest.makeDtalges(DtAlgeSetIOTest2.writeRandomAlgeSetData)), null          , false, DtAlgeSetIOTest2.readRandomTableAlgeSetData),
		// [ 7] Random, No order, No null
		new TableCsvParam(new DtAlgeSet(DtAlgeSetTest.makeDtalges(DtAlgeSetIOTest2.writeRandomAlgeSetData)), null          , true , DtAlgeSetIOTest2.readRandomTableAlgeSetData),
		// [ 8] Ramdon, No order, With null
		new TableCsvParam(new DtAlgeSet(DtAlgeSetTest.makeDtalges(DtAlgeSetIOTest2.writeRandomAlgeSetData)), EmptyBaseOrder, false, DtAlgeSetIOTest2.readRandomTableAlgeSetData),
		// [ 9] Random, No order, No null
		new TableCsvParam(new DtAlgeSet(DtAlgeSetTest.makeDtalges(DtAlgeSetIOTest2.writeRandomAlgeSetData)), EmptyBaseOrder, true, DtAlgeSetIOTest2.readRandomTableAlgeSetData),
		// [10] Random, With order, With null
		new TableCsvParam(new DtAlgeSet(DtAlgeSetTest.makeDtalges(DtAlgeSetIOTest2.writeRandomAlgeSetData)), AllBaseOrder  , false, DtAlgeSetIOTest2.readSortedAlgeSetData),
		// [11] Random, With order, No null
		new TableCsvParam(new DtAlgeSet(DtAlgeSetTest.makeDtalges(DtAlgeSetIOTest2.writeRandomAlgeSetData)), AllBaseOrder  , true , DtAlgeSetIOTest2.readSortedAlgeSetData),
		// [12] Random, With order, With null
		new TableCsvParam(new DtAlgeSet(DtAlgeSetTest.makeDtalges(DtAlgeSetIOTest2.writeRandomAlgeSetData)), HalfBaseOrder  , false, DtAlgeSetIOTest2.readHalfSortedAlgeSetData),
		// [13] Random, With order, No null
		new TableCsvParam(new DtAlgeSet(DtAlgeSetTest.makeDtalges(DtAlgeSetIOTest2.writeRandomAlgeSetData)), HalfBaseOrder  , true , DtAlgeSetIOTest2.readHalfSortedAlgeSetData),
	};
	
	static private final File[] toTableCsv_utf8 = {
		new File(tablepath, "v2_OutputTableAlgeSet00_UTF8_NoOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet01_UTF8_NoOrderNoNull.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet02_UTF8_NoOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet03_UTF8_NoOrderNoNull.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet04_UTF8_WithOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet05_UTF8_WithOrderNoNull.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet06_UTF8_NoOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet07_UTF8_NoOrderNoNull.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet08_UTF8_NoOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet09_UTF8_NoOrderNoNull.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet10_UTF8_WithOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet11_UTF8_WithOrderNoNull.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet12_UTF8_WithOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet13_UTF8_WithOrderNoNull.csv"),
	};
	
	static private final File[] toTableCsv_sjis = {
		new File(tablepath, "v2_OutputTableAlgeSet00_SJIS_NoOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet01_SJIS_NoOrderNoNull.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet02_SJIS_NoOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet03_SJIS_NoOrderNoNull.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet04_SJIS_WithOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet05_SJIS_WithOrderNoNull.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet06_SJIS_NoOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet07_SJIS_NoOrderNoNull.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet08_SJIS_NoOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet09_SJIS_NoOrderNoNull.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet10_SJIS_WithOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet11_SJIS_WithOrderNoNull.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet12_SJIS_WithOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet13_SJIS_WithOrderNoNull.csv"),
	};
	
	static private final File[] toTableCsv_def = {
		new File(tablepath, "v2_OutputTableAlgeSet00_NoOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet01_NoOrderNoNull.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet02_NoOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet03_NoOrderNoNull.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet04_WithOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet05_WithOrderNoNull.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet06_NoOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet07_NoOrderNoNull.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet08_NoOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet09_NoOrderNoNull.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet10_WithOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet11_WithOrderNoNull.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet12_WithOrderWithNull.csv"),
		new File(tablepath, "v2_OutputTableAlgeSet13_WithOrderNoNull.csv"),
	};

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * Test method for {@link dtalge.DtAlgeSet#fromCSV(java.io.File)}.
	 */
	public void testFromCSVFile() {
		// Illegal data
		System.out.println("\n<<< Test for 'DtAlgeSet#fromCSV(File)' by illegal table csv >>>");
		for (File target : err_fromcsv_sjis) {
			System.out.println("--- load \"" + target.toString() + "\"");
			try {
				DtAlgeSet.fromCSV(target);
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
		System.out.println("\n<<< Test for 'DtAlgeSet#fromCSV(File)' by normal table csv >>>");
		for (int i = 0; i < fromcsv_def.length; i++) {
			File target = fromcsv_def[i];
			DtAlgeSet ret = null;
			System.out.println("--- load \"" + target.toString() + "\"");
			try {
				ret = DtAlgeSet.fromCSV(target);
			}
			catch (Throwable ex) {
				fail("Failed to read from CSV file : " + ex.toString());
			}
			assertTrue(DtAlgeSetTest.equalElementSequence(ret, checkInputAlges[i]));
		}
	}

	/**
	 * Test method for {@link dtalge.DtAlgeSet#fromCSV(java.io.File, java.lang.String)}.
	 */
	public void testFromCSVFileString() {
		// Illegal data
		System.out.println("\n<<< Test for 'DtAlgeSet#fromCSV(File,\"MS932\")' by illegal table csv >>>");
		for (File target : err_fromcsv_sjis) {
			System.out.println("--- load \"" + target.toString() + "\"");
			try {
				DtAlgeSet.fromCSV(target, SJIS);
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
		System.out.println("\n<<< Test for 'DtAlgeSet#fromCSV(File,\"MS932\")' by normal table csv >>>");
		for (int i = 0; i < fromcsv_sjis.length; i++) {
			File target = fromcsv_sjis[i];
			DtAlgeSet ret = null;
			System.out.println("--- load \"" + target.toString() + "\"");
			try {
				ret = DtAlgeSet.fromCSV(target, SJIS);
			}
			catch (Throwable ex) {
				fail("Failed to read from CSV file : " + ex.toString());
			}
			assertTrue(DtAlgeSetTest.equalElementSequence(ret, checkInputAlges[i]));
		}
		
		// Normal data(UTF-8)
		System.out.println("\n<<< Test for 'DtAlgeSet#fromCSV(File,\"UTF-8\")' by normal table csv >>>");
		for (int i = 0; i < fromcsv_utf8.length; i++) {
			File target = fromcsv_utf8[i];
			DtAlgeSet ret = null;
			System.out.println("--- load \"" + target.toString() + "\"");
			try {
				ret = DtAlgeSet.fromCSV(target, UTF8);
			}
			catch (Throwable ex) {
				fail("Failed to read from CSV file : " + ex.toString());
			}
			assertTrue(DtAlgeSetTest.equalElementSequence(ret, checkInputAlges[i]));
		}
	}

	/**
	 * Test method for {@link dtalge.DtAlgeSet#toTableCSV(java.io.File)}.
	 */
	public void testToTableCSVFile() {
		System.out.println("\n<<< Test for 'DtAlgeSet#toTableCSV(File)' >>>");
		for (int i = 0; i < outputAlges.length; i++) {
			File target = tocsv_def[i];
			DtAlgeSet alge = outputAlges[i];
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
			DtAlgeSet ret = null;
			try {
				ret = DtAlgeSet.fromCSV(target);
			}
			catch (Exception ex) {
				fail("Failed to read from table CSV file : " + ex.toString());
			}
			assertTrue(DtAlgeSetTest.equalElementSequence(ret, checkOutputAlges[i]));
			System.out.println("   ...O.K!");
		}
	}

	/**
	 * Test method for {@link dtalge.DtAlgeSet#toTableCSV(java.io.File, java.lang.String)}.
	 */
	public void testToTableCSVFileString() {
		// Write (SJIS)
		System.out.println("\n<<< Test for 'DtAlgeSet#toTableCSV(File,\"MS932\")' >>>");
		for (int i = 0; i < outputAlges.length; i++) {
			File target = tocsv_sjis[i];
			DtAlgeSet alge = outputAlges[i];
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
			DtAlgeSet ret = null;
			try {
				ret = DtAlgeSet.fromCSV(target, SJIS);
			}
			catch (Exception ex) {
				fail("Failed to read from table CSV file : " + ex.toString());
			}
			assertTrue(DtAlgeSetTest.equalElementSequence(ret, checkOutputAlges[i]));
			System.out.println("   ...O.K!");
		}
		
		// Write (UTF-8)
		System.out.println("\n<<< Test for 'DtAlgeSet#toTableCSV(File,\"UTF-8\")' >>>");
		for (int i = 0; i < outputAlges.length; i++) {
			File target = tocsv_utf8[i];
			DtAlgeSet alge = outputAlges[i];
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
			DtAlgeSet ret = null;
			try {
				ret = DtAlgeSet.fromCSV(target, UTF8);
			}
			catch (Exception ex) {
				fail("Failed to read from table CSV file : " + ex.toString());
			}
			assertTrue(DtAlgeSetTest.equalElementSequence(ret, checkOutputAlges[i]));
			System.out.println("   ...O.K!");
		}
	}

	/**
	 * Test method for {@link dtalge.DtAlgeSet#toTableCsvWithBaseOrder(DtBaseSet, File)}.
	 */
	public void testToTableCsvWithBaseOrderDtBaseSetFile() {
		System.out.println("\n<<< Test for 'DtAlgeSet#toTableCsvWithBaseOrder(DtBaseSet, File)' >>>");
		assertEquals(checkTableCsvParamData.length, toTableCsv_def.length);
		for (int i = 0; i < checkTableCsvParamData.length; i++) {
			File target = toTableCsv_def[i];
			DtAlgeSet alge        = checkTableCsvParamData[i].srcAlge;
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
			DtAlgeSet ret = null;
			try {
				ret = DtAlgeSet.fromCSV(target);
			}
			catch (Exception ex) {
				fail("Failed to read from table CSV file : " + ex.toString());
			}
			assertTrue(DtAlgeSetTest.equalElementSequence(ret, checkTableCsvParamData[i].algeSequence));
			System.out.println("   ...O.K!");
		}
	}

	/**
	 * Test method for {@link dtalge.DtAlgeSet#toTableCsvWithBaseOrder(DtBaseSet, File, String)}.
	 */
	public void testToTableCsvWithBaseOrderDtBaseSetFileString() {
		// Write (SJIS)
		System.out.println("\n<<< Test for 'DtAlgeSet#toTableCsvWithBaseOrder(DtBaseSet, File, \"MS932\")' >>>");
		assertEquals(checkTableCsvParamData.length, toTableCsv_sjis.length);
		for (int i = 0; i < checkTableCsvParamData.length; i++) {
			File target = toTableCsv_sjis[i];
			DtAlgeSet alge        = checkTableCsvParamData[i].srcAlge;
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
			DtAlgeSet ret = null;
			try {
				ret = DtAlgeSet.fromCSV(target, SJIS);
			}
			catch (Exception ex) {
				fail("Failed to read from table CSV file : " + ex.toString());
			}
			assertTrue(DtAlgeSetTest.equalElementSequence(ret, checkTableCsvParamData[i].algeSequence));
			System.out.println("   ...O.K!");
		}
		
		// Write (UTF-8)
		System.out.println("\n<<< Test for 'DtAlgeSet#toTableCsvWithBaseOrder(DtBaseSet, File, \"UTF-8\")' >>>");
		assertEquals(checkTableCsvParamData.length, toTableCsv_utf8.length);
		for (int i = 0; i < checkTableCsvParamData.length; i++) {
			File target = toTableCsv_utf8[i];
			DtAlgeSet alge        = checkTableCsvParamData[i].srcAlge;
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
			DtAlgeSet ret = null;
			try {
				ret = DtAlgeSet.fromCSV(target, UTF8);
			}
			catch (Exception ex) {
				fail("Failed to read from table CSV file : " + ex.toString());
			}
			assertTrue(DtAlgeSetTest.equalElementSequence(ret, checkTableCsvParamData[i].algeSequence));
			System.out.println("   ...O.K!");
		}
	}

	/**
	 * Test method for {@link dtalge.DtAlgeSet#toTableCsvWithoutNull(File)}.
	 */
	public void testToTableCsvWithoutNullFile() {
		System.out.println("\n<<< Test for 'DtAlgeSet#toTableCsvWithoutNull(File)' >>>");
		assertEquals(checkTableCsvParamData.length, toTableCsv_def.length);
		for (int i = 0; i < checkTableCsvParamData.length; i++) {
			File target = toTableCsv_def[i];
			DtAlgeSet alge        = checkTableCsvParamData[i].srcAlge;
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
			DtAlgeSet ret = null;
			try {
				ret = DtAlgeSet.fromCSV(target);
			}
			catch (Exception ex) {
				fail("Failed to read from table CSV file : " + ex.toString());
			}
			assertTrue(DtAlgeSetTest.equalNoNullElementSequence(ret, checkTableCsvParamData[i].algeSequence));
			System.out.println("   ...O.K!");
		}
	}

	/**
	 * Test method for {@link dtalge.DtAlgeSet#toTableCsvWithoutNull(File, String)}.
	 */
	public void testToTableCsvWithoutNullFileString() {
		// Write (SJIS)
		System.out.println("\n<<< Test for 'DtAlgeSet#toTableCsvWithoutNull(File, \"MS932\")' >>>");
		assertEquals(checkTableCsvParamData.length, toTableCsv_sjis.length);
		for (int i = 0; i < checkTableCsvParamData.length; i++) {
			File target = toTableCsv_sjis[i];
			DtAlgeSet alge        = checkTableCsvParamData[i].srcAlge;
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
			DtAlgeSet ret = null;
			try {
				ret = DtAlgeSet.fromCSV(target, SJIS);
			}
			catch (Exception ex) {
				fail("Failed to read from table CSV file : " + ex.toString());
			}
			assertTrue(DtAlgeSetTest.equalNoNullElementSequence(ret, checkTableCsvParamData[i].algeSequence));
			System.out.println("   ...O.K!");
		}
		
		// Write (UTF-8)
		System.out.println("\n<<< Test for 'DtAlgeSet#toTableCsvWithoutNull(File, \"UTF-8\")' >>>");
		assertEquals(checkTableCsvParamData.length, toTableCsv_utf8.length);
		for (int i = 0; i < checkTableCsvParamData.length; i++) {
			File target = toTableCsv_utf8[i];
			DtAlgeSet alge        = checkTableCsvParamData[i].srcAlge;
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
			DtAlgeSet ret = null;
			try {
				ret = DtAlgeSet.fromCSV(target, UTF8);
			}
			catch (Exception ex) {
				fail("Failed to read from table CSV file : " + ex.toString());
			}
			assertTrue(DtAlgeSetTest.equalNoNullElementSequence(ret, checkTableCsvParamData[i].algeSequence));
			System.out.println("   ...O.K!");
		}
	}

	/**
	 * Test method for {@link dtalge.DtAlgeSet#toTableCSV(DtBaseSet, boolean, File)}.
	 */
	public void testToTableCSVFileDtBaseSetBoolean() {
		System.out.println("\n<<< Test for 'DtAlgeSet#toTableCSV(DtBaseSet, boolean, File)' >>>");
		assertEquals(checkTableCsvParamData.length, toTableCsv_def.length);
		for (int i = 0; i < checkTableCsvParamData.length; i++) {
			File target = toTableCsv_def[i];
			DtAlgeSet alge        = checkTableCsvParamData[i].srcAlge;
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
			DtAlgeSet ret = null;
			try {
				ret = DtAlgeSet.fromCSV(target);
			}
			catch (Exception ex) {
				fail("Failed to read from table CSV file : " + ex.toString());
			}
			if (withoutNull) {
				assertTrue(DtAlgeSetTest.equalNoNullElementSequence(ret, checkTableCsvParamData[i].algeSequence));
			} else {
				assertTrue(DtAlgeSetTest.equalElementSequence(ret, checkTableCsvParamData[i].algeSequence));
			}
			System.out.println("   ...O.K!");
		}
	}

	/**
	 * Test method for {@link dtalge.DtAlgeSet#toTableCSV(DtBaseSet, boolean, File, String)}.
	 */
	public void testToTableCSVFileStringDtBaseSetBoolean() {
		// Write (SJIS)
		System.out.println("\n<<< Test for 'DtAlgeSet#toTableCSV(DtBaseSet, boolean, File, \"MS932\")' >>>");
		assertEquals(checkTableCsvParamData.length, toTableCsv_sjis.length);
		for (int i = 0; i < checkTableCsvParamData.length; i++) {
			File target = toTableCsv_sjis[i];
			DtAlgeSet alge        = checkTableCsvParamData[i].srcAlge;
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
			DtAlgeSet ret = null;
			try {
				ret = DtAlgeSet.fromCSV(target, SJIS);
			}
			catch (Exception ex) {
				fail("Failed to read from table CSV file : " + ex.toString());
			}
			if (withoutNull) {
				assertTrue(DtAlgeSetTest.equalNoNullElementSequence(ret, checkTableCsvParamData[i].algeSequence));
			} else {
				assertTrue(DtAlgeSetTest.equalElementSequence(ret, checkTableCsvParamData[i].algeSequence));
			}
			System.out.println("   ...O.K!");
		}
		
		// Write (UTF-8)
		System.out.println("\n<<< Test for 'DtAlgeSet#toTableCSV(DtBaseSet, boolean, File, \"UTF-8\")' >>>");
		assertEquals(checkTableCsvParamData.length, toTableCsv_utf8.length);
		for (int i = 0; i < checkTableCsvParamData.length; i++) {
			File target = toTableCsv_utf8[i];
			DtAlgeSet alge        = checkTableCsvParamData[i].srcAlge;
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
			DtAlgeSet ret = null;
			try {
				ret = DtAlgeSet.fromCSV(target, UTF8);
			}
			catch (Exception ex) {
				fail("Failed to read from table CSV file : " + ex.toString());
			}
			if (withoutNull) {
				assertTrue(DtAlgeSetTest.equalNoNullElementSequence(ret, checkTableCsvParamData[i].algeSequence));
			} else {
				assertTrue(DtAlgeSetTest.equalElementSequence(ret, checkTableCsvParamData[i].algeSequence));
			}
			System.out.println("   ...O.K!");
		}
	}
}
