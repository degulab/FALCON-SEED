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
 * @(#)DtAlgeSetTableIOTest.java	0.40	2012/05/23
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtAlgeSetTableIOTest.java	0.20	2010/03/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge;

import static dtalge.AllUnitTests.*;

import java.io.File;
import java.math.BigDecimal;
import java.util.Arrays;

import dtalge.exception.CsvFormatException;

import junit.framework.TestCase;

/**
 * <code>DtAlgeSet</code> でのテーブル形式のCSVファイル入出力機能のユニットテスト。
 * 
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class DtAlgeSetTableIOTest extends TestCase
{
	static protected final DtBase baseName = DtBase.newBase("名前", "string");
	static protected final DtBase baseSex  = DtBase.newBase("性別", "string");
	static protected final DtBase baseAge  = DtBase.newBase("年齢", "decimal");
	static protected final DtBase baseAddr = DtBase.newBase("住所", "string");
	static protected final DtBase baseFlag = DtBase.newBase("フラグ", "boolean");

	static protected final Object[][] normalAlgeData2 = {
		{
			baseName, null,
			baseSex , null,
			baseAge , null,
			baseAddr, null,
			baseFlag, null,
		},
	};
	static protected final Object[][] normalAlgeData3 = {
		{	// [0]
			//北川,女性,29,三重県,true
			baseName, "北川",
			baseSex , "女性",
			baseAge , new BigDecimal(29),
			baseAddr, "三重県",
			baseFlag, Boolean.TRUE,
		},
		{	// [1]
			//上原,男性,35,,false
			baseName, "上原",
			baseSex , "男性",
			baseAge , new BigDecimal(35),
			baseAddr, null,
			baseFlag, Boolean.FALSE,
		},
		{	// [2]
			//馬場,,41,宮城県,
			baseName, "馬場",
			baseSex , null,
			baseAge , new BigDecimal(41),
			baseAddr, "宮城県",
			baseFlag, null,
		},
		{	// [3]
			//富田,男性,14,鹿児島県,false
			baseName, "富田",
			baseSex , "男性",
			baseAge , new BigDecimal(14),
			baseAddr, "鹿児島県",
			baseFlag, Boolean.FALSE,
		},
		{	// [4]
			//藤原,,,,
			baseName, "藤原",
			baseSex , null,
			baseAge , null,
			baseAddr, null,
			baseFlag, null,
		},
		{	// [5]
			//須藤,女性,,,false
			baseName, "須藤",
			baseSex , "女性",
			baseAge , null,
			baseAddr, null,
			baseFlag, Boolean.FALSE,
		},
		{	// [6]
			//萩原,女性,25,熊本県,true
			baseName, "萩原",
			baseSex , "女性",
			baseAge , new BigDecimal(25),
			baseAddr, "熊本県",
			baseFlag, Boolean.TRUE,
		},
		{	// [7]
			//小沢,女性,,茨城県,true
			baseName, "小沢",
			baseSex , "女性",
			baseAge , null,
			baseAddr, "茨城県",
			baseFlag, Boolean.TRUE,
		},
		{	// [8]
			//,,,,
			baseName, null,
			baseSex , null,
			baseAge , null,
			baseAddr, null,
			baseFlag, null,
		},
		{	// [9]
			//栗原,男性,65,埼玉県,false
			baseName, "栗原",
			baseSex , "男性",
			baseAge , new BigDecimal(65),
			baseAddr, "埼玉県",
			baseFlag, Boolean.FALSE,
		},
		{	// [10]
			//岡本,女性,18,新潟県,true
			baseName, "岡本",
			baseSex , "女性",
			baseAge , new BigDecimal(18),
			baseAddr, "新潟県",
			baseFlag, Boolean.TRUE,
		},
	};
	static protected final Object[][] normalAlgeData4 = {
		{	// [0]
			//北川,女性,29,三重県,true
			baseName, "北川",
			baseSex , "女性",
			baseAge , new BigDecimal(29),
			baseAddr, "三重県",
			baseFlag, Boolean.TRUE,
		},
		{	// [1]
			//上原,男性,35,,false
			baseName, "上原",
			baseSex , "男性",
			baseAge , new BigDecimal(35),
			baseAddr, null,
			baseFlag, Boolean.FALSE,
		},
		{	// [2]
			//馬場,,41,宮城県,
			baseName, "馬場",
			baseAge , new BigDecimal(41),
			baseAddr, "宮城県",
			baseFlag, null,
		},
		{	// [3]
			//富田,男性,14,鹿児島県,false
			baseName, "富田",
			baseSex , "男性",
			baseAge , new BigDecimal(14),
			baseAddr, "鹿児島県",
			baseFlag, Boolean.FALSE,
		},
		{	// [4]
			//藤原,,,,
			baseName, "藤原",
			baseSex , null,
			baseAge , null,
			baseAddr, null,
			baseFlag, null,
		},
		{	// [5]
			//須藤,女性,,,false
			baseName, "須藤",
			baseSex , "女性",
			baseFlag, Boolean.FALSE,
		},
		{	// [6]
			//萩原,女性,25,熊本県,true
			baseName, "萩原",
			baseSex , "女性",
			baseAge , new BigDecimal(25),
			baseAddr, "熊本県",
			baseFlag, Boolean.TRUE,
		},
		{	// [7]
			//小沢,女性,,茨城県,true
			baseName, "小沢",
			baseSex , "女性",
			baseAge , null,
			baseAddr, "茨城県",
			baseFlag, Boolean.TRUE,
		},
		{	// [8]
			//,,,,
		},
		{	// [9]
			//栗原,男性,65,埼玉県,false
			baseName, "栗原",
			baseSex , "男性",
			baseAge , new BigDecimal(65),
			baseAddr, "埼玉県",
			baseFlag, Boolean.FALSE,
		},
		{	// [10]
			//岡本,女性,18,新潟県,true
			baseName, "岡本",
			baseSex , "女性",
			baseAge , new BigDecimal(18),
			baseAddr, "新潟県",
			baseFlag, Boolean.TRUE,
		},
	};
	static protected final Object[][] normalAlgeData5 = {
		{	// [0]
			//北川,女性,29,三重県,true
			baseName, "北川",
			baseSex , "女性",
			baseAge , new BigDecimal(29),
			baseAddr, "三重県",
			baseFlag, Boolean.TRUE,
		},
		{	// [1]
			//上原,男性,35,,false
			baseName, "上原",
			baseSex , "男性",
			baseAge , new BigDecimal(35),
			//baseAddr, null,
			baseFlag, Boolean.FALSE,
		},
		{	// [2]
			//馬場,,41,宮城県,
			baseName, "馬場",
			baseAge , new BigDecimal(41),
			baseAddr, "宮城県",
			//baseFlag, null,
		},
		{	// [3]
			//富田,男性,14,鹿児島県,false
			baseName, "富田",
			baseSex , "男性",
			baseAge , new BigDecimal(14),
			baseAddr, "鹿児島県",
			baseFlag, Boolean.FALSE,
		},
		{	// [4]
			//藤原,,,,
			baseName, "藤原",
			//baseSex , null,
			//baseAge , null,
			//baseAddr, null,
			//baseFlag, null,
		},
		{	// [5]
			//須藤,女性,,,false
			baseName, "須藤",
			baseSex , "女性",
			baseFlag, Boolean.FALSE,
		},
		{	// [6]
			//萩原,女性,25,熊本県,true
			baseName, "萩原",
			baseSex , "女性",
			baseAge , new BigDecimal(25),
			baseAddr, "熊本県",
			baseFlag, Boolean.TRUE,
		},
		{	// [7]
			//小沢,女性,,茨城県,true
			baseName, "小沢",
			baseSex , "女性",
			//baseAge , null,
			baseAddr, "茨城県",
			baseFlag, Boolean.TRUE,
		},
		{	// [8]
			//,,,,
		},
		{	// [9]
			//栗原,男性,65,埼玉県,false
			baseName, "栗原",
			baseSex , "男性",
			baseAge , new BigDecimal(65),
			baseAddr, "埼玉県",
			baseFlag, Boolean.FALSE,
		},
		{	// [10]
			//岡本,女性,18,新潟県,true
			baseName, "岡本",
			baseSex , "女性",
			baseAge , new BigDecimal(18),
			baseAddr, "新潟県",
			baseFlag, Boolean.TRUE,
		},
	};
	
	static protected final DtAlgeSet[] outputAlges = {
		new DtAlgeSet(),
		new DtAlgeSet(Arrays.asList(new Dtalge())),
		new DtAlgeSet(DtAlgeSetTest.makeDtalges(normalAlgeData2)),
		new DtAlgeSet(DtAlgeSetTest.makeDtalges(normalAlgeData3)),
		new DtAlgeSet(DtAlgeSetTest.makeDtalges(normalAlgeData4)),
	};
	
	static protected final Object[][][] checkOutputAlges = {
		{},
		{},
		normalAlgeData2,
		normalAlgeData3,
		normalAlgeData4,
	};
	
	static protected final Object[][][] checkInputAlges = {
		{},
		{},
		{},
		normalAlgeData2,
		normalAlgeData3,
		normalAlgeData3,
		normalAlgeData3,
	};
	
	static protected final File[] tocsv_utf8 = {
		new File(tablepath, "OutputTableAlgeSet0_UTF8.csv"),
		new File(tablepath, "OutputTableAlgeSet1_UTF8.csv"),
		new File(tablepath, "OutputTableAlgeSet2_UTF8.csv"),
		new File(tablepath, "OutputTableAlgeSet3_UTF8.csv"),
		new File(tablepath, "OutputTableAlgeSet4_UTF8.csv"),
	};
	static protected final File[] tocsv_sjis = {
		new File(tablepath, "OutputTableAlgeSet0_SJIS.csv"),
		new File(tablepath, "OutputTableAlgeSet1_SJIS.csv"),
		new File(tablepath, "OutputTableAlgeSet2_SJIS.csv"),
		new File(tablepath, "OutputTableAlgeSet3_SJIS.csv"),
		new File(tablepath, "OutputTableAlgeSet4_SJIS.csv"),
	};
	static protected final File[] tocsv_def  = {
		new File(tablepath, "OutputTableAlgeSet0.csv"),
		new File(tablepath, "OutputTableAlgeSet1.csv"),
		new File(tablepath, "OutputTableAlgeSet2.csv"),
		new File(tablepath, "OutputTableAlgeSet3.csv"),
		new File(tablepath, "OutputTableAlgeSet4.csv"),
	};
	
	static protected final File[] fromcsv_utf8 = {
		new File(tablepath, "NormalTableAlge1_UTF8.csv"),
		new File(tablepath, "NormalTableAlge2_UTF8.csv"),
		new File(tablepath, "NormalTableAlge3_UTF8.csv"),
		new File(tablepath, "NormalTableAlge4_UTF8.csv"),
		new File(tablepath, "NormalTableAlge5_UTF8.csv"),
		new File(tablepath, "NormalTableAlge6_UTF8.csv"),
		new File(tablepath, "NormalTableAlge7_UTF8.csv"),
	};
	static protected final File[] fromcsv_sjis = {
		new File(tablepath, "NormalTableAlge1_SJIS.csv"),
		new File(tablepath, "NormalTableAlge2_SJIS.csv"),
		new File(tablepath, "NormalTableAlge3_SJIS.csv"),
		new File(tablepath, "NormalTableAlge4_SJIS.csv"),
		new File(tablepath, "NormalTableAlge5_SJIS.csv"),
		new File(tablepath, "NormalTableAlge6_SJIS.csv"),
		new File(tablepath, "NormalTableAlge7_SJIS.csv"),
	};
	static protected final File[] fromcsv_def  = fromcsv_sjis;
	
	static protected final File[] err_fromcsv_sjis = {
		new File(tablepath, "IllegalTableAlge1_SJIS.csv"),
		new File(tablepath, "IllegalTableAlge2_SJIS.csv"),
		new File(tablepath, "IllegalTableAlge3_SJIS.csv"),
		new File(tablepath, "IllegalTableAlge4_SJIS.csv"),
		new File(tablepath, "IllegalTableAlge5_SJIS.csv"),
		new File(tablepath, "IllegalTableAlge6_SJIS.csv"),
		new File(tablepath, "IllegalTableAlge7_SJIS.csv"),
		new File(tablepath, "IllegalTableAlge8_SJIS.csv"),
		new File(tablepath, "IllegalTableAlge9_SJIS.csv"),
		new File(tablepath, "IllegalTableAlge10_SJIS.csv"),
		new File(tablepath, "IllegalTableAlge11_SJIS.csv"),
		new File(tablepath, "IllegalTableAlge12_SJIS.csv"),
	};
	static protected final File[] err_fromcsv_def  = err_fromcsv_sjis;

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

}
