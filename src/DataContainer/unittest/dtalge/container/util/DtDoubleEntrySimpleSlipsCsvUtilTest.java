/*
 * @(#)DtDoubleEntrySimpleSlipsCsvUtilTest.java	0.2.0	2025/02/16
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.util;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import dtalge.DtBase;
import dtalge.Dtalge;
import dtalge.container.DtBinder;
import dtalge.container.DtSlip;
import dtalge.container.DtSlipList;
import dtalge.json.DtJSON;
import dtalge.util.DtDataTypes;
import dtalge.util.Strings;
import exalge2.ExAlgeSet;
import exalge2.ExBase;
import exalge2.Exalge;
import junit.framework.TestCase;

/**
 * {@link DtDoubleEntrySimpleSlipsCsvUtil} クラスのユニットテスト。
 * 
 * @author Y.Ishizuka(PieCake,Inc.)
 * 
 * @since 0.2.0
 */
public class DtDoubleEntrySimpleSlipsCsvUtilTest extends TestCase
{
	//------------------------------------------------------------
	// Test cases
	//------------------------------------------------------------

	/**
	 * Test method for {@link dtalge.container.util.DtDoubleEntrySimpleSlipsCsvUtil#importBinderFromDoubleEntrySimpleSlipsCSV(java.io.File, dtalge.container.util.DebitCreditItemDefinitionTable)}.
	 */
	public void test01_ImportBinderFromDoubleEntrySimpleSlipsCSVFileDebitCreditItemDefinitionTable()
	{
		DtBinder resultBinder = null;
		File outFile = null;
		
		// read from CSV
		try {
			resultBinder = DtDoubleEntrySimpleSlipsCsvUtil.importBinderFromDoubleEntrySimpleSlipsCSV(fileInputDoubleEntrySimpleSlipsCsv_SJIS, EXP_DEBITCREDIT_DEF_TABLE);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail("Unexpected exception: " + ex.toString());
		}
		assertEquals(EXP_DTBINDER_FROM_CSV_NO_CONDITION, resultBinder);
		outFile = outFile_DTBINDER_FROM_CSV_NO_CONDITION;
		try {
			DtJSON.serialize(outFile, resultBinder);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail("Failed to write DtBinder to JSON file: " + outFile  + "\n  exception: " + ex.toString());
		}
	}

	/**
	 * Test method for {@link dtalge.container.util.DtDoubleEntrySimpleSlipsCsvUtil#importBinderFromDoubleEntrySimpleSlipsCSV(java.io.File, java.lang.String, dtalge.container.util.DebitCreditItemDefinitionTable)}.
	 */
	public void test02_ImportBinderFromDoubleEntrySimpleSlipsCSVFileStringDebitCreditItemDefinitionTable() {
		DtBinder resultBinder = null;
		//File outFile = null;
		
		// read from CSV
		try {
			resultBinder = DtDoubleEntrySimpleSlipsCsvUtil.importBinderFromDoubleEntrySimpleSlipsCSV(fileInputDoubleEntrySimpleSlipsCsv_UTF8, UTF8, EXP_DEBITCREDIT_DEF_TABLE);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail("Unexpected exception: " + ex.toString());
		}
		assertEquals(EXP_DTBINDER_FROM_CSV_NO_CONDITION, resultBinder);
	}

	/**
	 * Test method for {@link dtalge.container.util.DtDoubleEntrySimpleSlipsCsvUtil#importBinderFromDoubleEntrySimpleSlipsCSV(java.io.File, java.io.File)}.
	 */
	public void test03_ImportBinderFromDoubleEntrySimpleSlipsCSVFileFile() {
		DtBinder resultBinder = null;
		
		// read from CSV
		try {
			resultBinder = DtDoubleEntrySimpleSlipsCsvUtil.importBinderFromDoubleEntrySimpleSlipsCSV(fileInputDoubleEntrySimpleSlipsCsv_SJIS, fileInputDebitCreditDefTableCsv_SJIS);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail("Unexpected exception: " + ex.toString());
		}
		assertEquals(EXP_DTBINDER_FROM_CSV_NO_CONDITION, resultBinder);
	}

	/**
	 * Test method for {@link dtalge.container.util.DtDoubleEntrySimpleSlipsCsvUtil#importBinderFromDoubleEntrySimpleSlipsCSV(java.io.File, java.io.File, java.lang.String)}.
	 */
	public void test04_ImportBinderFromDoubleEntrySimpleSlipsCSVFileFileString() {
		DtBinder resultBinder = null;
		
		// read from CSV
		try {
			resultBinder = DtDoubleEntrySimpleSlipsCsvUtil.importBinderFromDoubleEntrySimpleSlipsCSV(fileInputDoubleEntrySimpleSlipsCsv_UTF8, fileInputDebitCreditDefTableCsv_UTF8, UTF8);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail("Unexpected exception: " + ex.toString());
		}
		assertEquals(EXP_DTBINDER_FROM_CSV_NO_CONDITION, resultBinder);
	}

	/**
	 * Test method for {@link dtalge.container.util.DtDoubleEntrySimpleSlipsCsvUtil#importBinderFromDoubleEntrySimpleSlipsCSV(java.lang.String, java.io.File, dtalge.container.util.DebitCreditItemDefinitionTable)}.
	 */
	public void test05_ImportBinderFromDoubleEntrySimpleSlipsCSVStringFileDebitCreditItemDefinitionTable() {
		DtBinder resultBinder = null;
		
		// read from CSV
		try {
			resultBinder = DtDoubleEntrySimpleSlipsCsvUtil.importBinderFromDoubleEntrySimpleSlipsCSV(EXP_CSV_FIELD_CONDITION1, fileInputDoubleEntrySimpleSlipsCsv_SJIS, EXP_DEBITCREDIT_DEF_TABLE);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail("Unexpected exception: " + ex.toString());
		}
		assertEquals(EXP_DTBINDER_FROM_CSV_BY_CONDITION1, resultBinder);
		assertEquals(false, EXP_DTBINDER_FROM_CSV_NO_CONDITION.equals(resultBinder));
	}

	/**
	 * Test method for {@link dtalge.container.util.DtDoubleEntrySimpleSlipsCsvUtil#importBinderFromDoubleEntrySimpleSlipsCSV(java.lang.String, java.io.File, java.lang.String, dtalge.container.util.DebitCreditItemDefinitionTable)}.
	 */
	public void test06_ImportBinderFromDoubleEntrySimpleSlipsCSVStringFileStringDebitCreditItemDefinitionTable() {
		DtBinder resultBinder = null;
		
		// read from CSV
		try {
			resultBinder = DtDoubleEntrySimpleSlipsCsvUtil.importBinderFromDoubleEntrySimpleSlipsCSV(EXP_CSV_FIELD_CONDITION1, fileInputDoubleEntrySimpleSlipsCsv_UTF8, UTF8, EXP_DEBITCREDIT_DEF_TABLE);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail("Unexpected exception: " + ex.toString());
		}
		assertEquals(EXP_DTBINDER_FROM_CSV_BY_CONDITION1, resultBinder);
		assertEquals(false, EXP_DTBINDER_FROM_CSV_NO_CONDITION.equals(resultBinder));
	}

	/**
	 * Test method for {@link dtalge.container.util.DtDoubleEntrySimpleSlipsCsvUtil#importBinderFromDoubleEntrySimpleSlipsCSV(java.lang.String, java.io.File, java.io.File)}.
	 */
	public void test07_ImportBinderFromDoubleEntrySimpleSlipsCSVStringFileFile() {
		DtBinder resultBinder = null;
		
		// read from CSV
		try {
			resultBinder = DtDoubleEntrySimpleSlipsCsvUtil.importBinderFromDoubleEntrySimpleSlipsCSV(EXP_CSV_FIELD_CONDITION1, fileInputDoubleEntrySimpleSlipsCsv_SJIS, fileInputDebitCreditDefTableCsv_SJIS);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail("Unexpected exception: " + ex.toString());
		}
		assertEquals(EXP_DTBINDER_FROM_CSV_BY_CONDITION1, resultBinder);
		assertEquals(false, EXP_DTBINDER_FROM_CSV_NO_CONDITION.equals(resultBinder));
	}

	/**
	 * Test method for {@link dtalge.container.util.DtDoubleEntrySimpleSlipsCsvUtil#importBinderFromDoubleEntrySimpleSlipsCSV(java.lang.String, java.io.File, java.io.File, java.lang.String)}.
	 */
	public void test08_ImportBinderFromDoubleEntrySimpleSlipsCSVStringFileFileString() {
		DtBinder resultBinder = null;
		
		// read from CSV
		try {
			resultBinder = DtDoubleEntrySimpleSlipsCsvUtil.importBinderFromDoubleEntrySimpleSlipsCSV(EXP_CSV_FIELD_CONDITION1, fileInputDoubleEntrySimpleSlipsCsv_UTF8, fileInputDebitCreditDefTableCsv_UTF8, UTF8);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail("Unexpected exception: " + ex.toString());
		}
		assertEquals(EXP_DTBINDER_FROM_CSV_BY_CONDITION1, resultBinder);
		assertEquals(false, EXP_DTBINDER_FROM_CSV_NO_CONDITION.equals(resultBinder));
	}

	/**
	 * Test method for {@link dtalge.container.util.DtDoubleEntrySimpleSlipsCsvUtil#exportAllSlipsToDoubleEntrySimpleSlipsCSV(dtalge.container.DtBinder, java.io.File, dtalge.container.util.DebitCreditItemDefinitionTable)}.
	 */
	public void test11_ExportAllSlipsToDoubleEntrySimpleSlipsCSVDtBinderFileDebitCreditItemDefinitionTable() {
		DtBinder expBinder = null;
		DtBinder resultBinder = null;
		File outFile = null;
		
		// write to CSV
		expBinder = EXP_DTBINDER_FROM_CSV_NO_CONDITION;
		outFile = fileOutDoubleEntrySimpleSlipsCsv_SJIS_1;
		try {
			DtDoubleEntrySimpleSlipsCsvUtil.exportAllSlipsToDoubleEntrySimpleSlipsCSV(expBinder, outFile, EXP_DEBITCREDIT_DEF_TABLE);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail("Unexpected exception: " + ex.toString());
		}
		//--- check result
		try {
			resultBinder = DtDoubleEntrySimpleSlipsCsvUtil.importBinderFromDoubleEntrySimpleSlipsCSV(outFile, EXP_DEBITCREDIT_DEF_TABLE);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail("Unexpected exception: " + ex.toString());
		}
		assertEquals(expBinder, resultBinder);
		
		// unbalance
		expBinder = EXP_SRC_UNBALANCE_DTBINDER_FROM_CSV_NO_CONDITION;
		outFile = fileOutUnbalanceDoubleEntrySimpleSlipsCsv_SJIS_1;
		try {
			DtDoubleEntrySimpleSlipsCsvUtil.exportAllSlipsToDoubleEntrySimpleSlipsCSV(expBinder, outFile, EXP_DEBITCREDIT_DEF_TABLE);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail("Unexpected exception: " + ex.toString());
		}
		//--- check result
		try {
			resultBinder = DtDoubleEntrySimpleSlipsCsvUtil.importBinderFromDoubleEntrySimpleSlipsCSV(outFile, EXP_DEBITCREDIT_DEF_TABLE);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail("Unexpected exception: " + ex.toString());
		}
		assertEquals(EXP_DST_UNBALANCE_DTBINDER_FROM_CSV_NO_CONDITION, resultBinder);
	}

	/**
	 * Test method for {@link dtalge.container.util.DtDoubleEntrySimpleSlipsCsvUtil#exportAllSlipsToDoubleEntrySimpleSlipsCSV(dtalge.container.DtBinder, java.io.File, java.lang.String, dtalge.container.util.DebitCreditItemDefinitionTable)}.
	 */
	public void test12_ExportAllSlipsToDoubleEntrySimpleSlipsCSVDtBinderFileStringDebitCreditItemDefinitionTable() {
		DtBinder expBinder = null;
		DtBinder resultBinder = null;
		File outFile = null;
		
		// write to CSV
		expBinder = EXP_DTBINDER_FROM_CSV_NO_CONDITION;
		outFile = fileOutDoubleEntrySimpleSlipsCsv_UTF8_1;
		try {
			DtDoubleEntrySimpleSlipsCsvUtil.exportAllSlipsToDoubleEntrySimpleSlipsCSV(expBinder, outFile, UTF8, EXP_DEBITCREDIT_DEF_TABLE);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail("Unexpected exception: " + ex.toString());
		}
		//--- check result
		try {
			resultBinder = DtDoubleEntrySimpleSlipsCsvUtil.importBinderFromDoubleEntrySimpleSlipsCSV(outFile, UTF8, EXP_DEBITCREDIT_DEF_TABLE);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail("Unexpected exception: " + ex.toString());
		}
		assertEquals(expBinder, resultBinder);
		
		// unbalance
		expBinder = EXP_SRC_UNBALANCE_DTBINDER_FROM_CSV_NO_CONDITION;
		outFile = fileOutUnbalanceDoubleEntrySimpleSlipsCsv_UTF8_1;
		try {
			DtDoubleEntrySimpleSlipsCsvUtil.exportAllSlipsToDoubleEntrySimpleSlipsCSV(expBinder, outFile, UTF8, EXP_DEBITCREDIT_DEF_TABLE);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail("Unexpected exception: " + ex.toString());
		}
		//--- check result
		try {
			resultBinder = DtDoubleEntrySimpleSlipsCsvUtil.importBinderFromDoubleEntrySimpleSlipsCSV(outFile, UTF8, EXP_DEBITCREDIT_DEF_TABLE);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail("Unexpected exception: " + ex.toString());
		}
		assertEquals(EXP_DST_UNBALANCE_DTBINDER_FROM_CSV_NO_CONDITION, resultBinder);
	}

	/**
	 * Test method for {@link dtalge.container.util.DtDoubleEntrySimpleSlipsCsvUtil#exportAllSlipsToDoubleEntrySimpleSlipsCSV(dtalge.container.DtBinder, java.io.File, java.io.File)}.
	 */
	public void test13_ExportAllSlipsToDoubleEntrySimpleSlipsCSVDtBinderFileFile() {
		DtBinder expBinder = null;
		DtBinder resultBinder = null;
		File outFile = null;
		
		// write to CSV
		expBinder = EXP_DTBINDER_FROM_CSV_NO_CONDITION;
		outFile = fileOutDoubleEntrySimpleSlipsCsv_SJIS_2;
		try {
			DtDoubleEntrySimpleSlipsCsvUtil.exportAllSlipsToDoubleEntrySimpleSlipsCSV(expBinder, outFile, fileInputDebitCreditDefTableCsv_SJIS);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail("Unexpected exception: " + ex.toString());
		}
		//--- check result
		try {
			resultBinder = DtDoubleEntrySimpleSlipsCsvUtil.importBinderFromDoubleEntrySimpleSlipsCSV(outFile, fileInputDebitCreditDefTableCsv_SJIS);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail("Unexpected exception: " + ex.toString());
		}
		assertEquals(expBinder, resultBinder);
		
		// unbalance
		expBinder = EXP_SRC_UNBALANCE_DTBINDER_FROM_CSV_NO_CONDITION;
		outFile = fileOutUnbalanceDoubleEntrySimpleSlipsCsv_SJIS_2;
		try {
			DtDoubleEntrySimpleSlipsCsvUtil.exportAllSlipsToDoubleEntrySimpleSlipsCSV(expBinder, outFile, fileInputDebitCreditDefTableCsv_SJIS);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail("Unexpected exception: " + ex.toString());
		}
		//--- check result
		try {
			resultBinder = DtDoubleEntrySimpleSlipsCsvUtil.importBinderFromDoubleEntrySimpleSlipsCSV(outFile, fileInputDebitCreditDefTableCsv_SJIS);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail("Unexpected exception: " + ex.toString());
		}
		assertEquals(EXP_DST_UNBALANCE_DTBINDER_FROM_CSV_NO_CONDITION, resultBinder);
	}

	/**
	 * Test method for {@link dtalge.container.util.DtDoubleEntrySimpleSlipsCsvUtil#exportAllSlipsToDoubleEntrySimpleSlipsCSV(dtalge.container.DtBinder, java.io.File, java.io.File, java.lang.String)}.
	 */
	public void test14_ExportAllSlipsToDoubleEntrySimpleSlipsCSVDtBinderFileFileString() {
		DtBinder expBinder = null;
		DtBinder resultBinder = null;
		File outFile = null;
		
		// write to CSV
		expBinder = EXP_DTBINDER_FROM_CSV_NO_CONDITION;
		outFile = fileOutDoubleEntrySimpleSlipsCsv_UTF8_2;
		try {
			DtDoubleEntrySimpleSlipsCsvUtil.exportAllSlipsToDoubleEntrySimpleSlipsCSV(expBinder, outFile, fileInputDebitCreditDefTableCsv_UTF8, UTF8);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail("Unexpected exception: " + ex.toString());
		}
		//--- check result
		try {
			resultBinder = DtDoubleEntrySimpleSlipsCsvUtil.importBinderFromDoubleEntrySimpleSlipsCSV(outFile, fileInputDebitCreditDefTableCsv_UTF8, UTF8);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail("Unexpected exception: " + ex.toString());
		}
		assertEquals(expBinder, resultBinder);
		
		// unbalance
		expBinder = EXP_SRC_UNBALANCE_DTBINDER_FROM_CSV_NO_CONDITION;
		outFile = fileOutUnbalanceDoubleEntrySimpleSlipsCsv_UTF8_2;
		try {
			DtDoubleEntrySimpleSlipsCsvUtil.exportAllSlipsToDoubleEntrySimpleSlipsCSV(expBinder, outFile, fileInputDebitCreditDefTableCsv_UTF8, UTF8);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail("Unexpected exception: " + ex.toString());
		}
		//--- check result
		try {
			resultBinder = DtDoubleEntrySimpleSlipsCsvUtil.importBinderFromDoubleEntrySimpleSlipsCSV(outFile, fileInputDebitCreditDefTableCsv_UTF8, UTF8);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail("Unexpected exception: " + ex.toString());
		}
		assertEquals(EXP_DST_UNBALANCE_DTBINDER_FROM_CSV_NO_CONDITION, resultBinder);
	}

	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	static protected final File pathBinderRoot = new File("testdata/DoubleEntrySimpleSlipsCsv/binder");
	static protected final File pathCsvRoot = new File("testdata/DoubleEntrySimpleSlipsCsv/csv");
	static protected final File pathCsvOut  = new File(pathCsvRoot, "out");
	static protected final String SJIS = "MS932";
	static protected final String UTF8 = "UTF-8";
	
	static protected final Map<String, Boolean> EXP_DEBITCREDIT_NAMEMAP;
	static {
		EXP_DEBITCREDIT_NAMEMAP = new HashMap<>();
		EXP_DEBITCREDIT_NAMEMAP.put("商品", true);
		EXP_DEBITCREDIT_NAMEMAP.put("現金", true);
		EXP_DEBITCREDIT_NAMEMAP.put("受取手形", true);
		EXP_DEBITCREDIT_NAMEMAP.put("売掛金", true);
		EXP_DEBITCREDIT_NAMEMAP.put("売上原価", true);
		EXP_DEBITCREDIT_NAMEMAP.put("減価償却費", true);
		EXP_DEBITCREDIT_NAMEMAP.put("リース代", true);
		EXP_DEBITCREDIT_NAMEMAP.put("特別損失", true);
		EXP_DEBITCREDIT_NAMEMAP.put("手形売却損", true);
		EXP_DEBITCREDIT_NAMEMAP.put("買掛金", true);

		EXP_DEBITCREDIT_NAMEMAP.put("買掛金", false);
		EXP_DEBITCREDIT_NAMEMAP.put("売上", false);
		EXP_DEBITCREDIT_NAMEMAP.put("売上総利益", false);
		EXP_DEBITCREDIT_NAMEMAP.put("支払手形", false);
		EXP_DEBITCREDIT_NAMEMAP.put("利息", false);
		EXP_DEBITCREDIT_NAMEMAP.put("受取利息", false);
		EXP_DEBITCREDIT_NAMEMAP.put("営業利益", false);
		EXP_DEBITCREDIT_NAMEMAP.put("経常利益", false);
		EXP_DEBITCREDIT_NAMEMAP.put("当期純利益", false);
	}

	static protected final DebitCreditItemDefinitionTable EXP_DEBITCREDIT_DEF_TABLE;
	static {
		// exp
		DebitCreditItemDefinitionTable expTable = new DebitCreditItemDefinitionTable();
		//--- #借方:資産
		expTable.put(DebitCreditItem.SIDE_DEBIT, "商品");
		expTable.put(DebitCreditItem.SIDE_DEBIT, "現金");
		expTable.put(DebitCreditItem.SIDE_DEBIT, "受取手形");
		expTable.put(DebitCreditItem.SIDE_DEBIT, "売掛金");
		//--- #借方:費用
		expTable.put(DebitCreditItem.SIDE_DEBIT, "売上原価");
		expTable.put(DebitCreditItem.SIDE_DEBIT, "減価償却費");
		expTable.put(DebitCreditItem.SIDE_DEBIT, "リース代");
		expTable.put(DebitCreditItem.SIDE_DEBIT, "特別損失");
		expTable.put(DebitCreditItem.SIDE_DEBIT, "手形売却損");
		//--- #貸方:負債
		expTable.put(DebitCreditItem.SIDE_CREDIT, "買掛金");
		//--- #貸方:収益
		expTable.put(DebitCreditItem.SIDE_CREDIT, "売上");
		expTable.put(DebitCreditItem.SIDE_CREDIT, "売上総利益");
		expTable.put(DebitCreditItem.SIDE_CREDIT, "支払手形");
		expTable.put(DebitCreditItem.SIDE_CREDIT, "利息");
		expTable.put(DebitCreditItem.SIDE_CREDIT, "受取利息");
		expTable.put(DebitCreditItem.SIDE_CREDIT, "営業利益");
		expTable.put(DebitCreditItem.SIDE_CREDIT, "経常利益");
		expTable.put(DebitCreditItem.SIDE_CREDIT, "当期純利益");
		
		EXP_DEBITCREDIT_DEF_TABLE = expTable;
	}
	
	static protected final String EXP_BINDER_SLIPOBJ_NAME = "dtsliplist";
	static protected final String EXP_SLIP_DATAOBJ_NAME   = "exalge";
	
	static protected final String[] EXP_STR_CSVHEADER = {
			"Dr.name", "Dr.value", "Dr.unit", "Cr.name", "Cr.value", "Cr.unit", "区分", "日付", "仕訳番号", "取引先", "取引先コード", "取引先住所", "備考",
		};
	static protected final Map<Integer, String>	EXP_CSVCOLIDX_NAMEMAP;
	static {
		EXP_CSVCOLIDX_NAMEMAP = new HashMap<>();
		for (int i = 0; i < EXP_STR_CSVHEADER.length; i++) {
			EXP_CSVCOLIDX_NAMEMAP.put(i, EXP_STR_CSVHEADER[i]);
		}
	}

	static protected final File fileInputDebitCreditDefTableCsv_SJIS = new File(pathCsvRoot, "item_side_def_sample_by_deguchi_sjis.csv");
	static protected final File fileInputDoubleEntrySimpleSlipsCsv_SJIS = new File(pathCsvRoot, "journal_sample_by_deguchi_sjis.csv");
	static protected final File fileOutDoubleEntrySimpleSlipsCsv_SJIS_1 = new File(pathCsvOut, "out_journal_sample_by_deguchi_sjis_1.csv");
	static protected final File fileOutDoubleEntrySimpleSlipsCsv_SJIS_2 = new File(pathCsvOut, "out_journal_sample_by_deguchi_sjis_2.csv");
	static protected final File fileInputDebitCreditDefTableCsv_UTF8 = new File(pathCsvRoot, "item_side_def_sample_by_deguchi_utf8.csv");
	static protected final File fileInputDoubleEntrySimpleSlipsCsv_UTF8 = new File(pathCsvRoot, "journal_sample_by_deguchi_utf8.csv");
	static protected final File fileOutDoubleEntrySimpleSlipsCsv_UTF8_1 = new File(pathCsvOut, "out_journal_sample_by_deguchi_utf8_1.csv");
	static protected final File fileOutDoubleEntrySimpleSlipsCsv_UTF8_2 = new File(pathCsvOut, "out_journal_sample_by_deguchi_utf8_2.csv");
	static protected final String EXP_CSV_FIELD_CONDITION1 = "区分=売上, 仕訳番号=\"^100\", 備考=\"商\"";
	static protected final String EXP_STR_CSVRECORDS_1_NO_CONDITION[][] = {
			/*  0 */ {"商品","500","円","買掛金","500","円","仕入れ","20240910","1001","山田商店","A123","東京都新宿区％％２－１－４","商品購入"},
			/*  1 */ {"現金","500","円","売上","500","円","売上計上","20240918","1002","田中小売店","B111","東京都足立区％％３－４－５","商品販売"},
			/*  2 */ {"売上原価","250","円","商品","250","円","売上原価計上","20240918","1003","振替","","",""},
			/*  3 */ {"売上","500","円","売上総利益","500","円","売上総利益振替","20240930","1004","振替","","",""},
			/*  4 */ {"売上総利益","250","円","売上原価","250","円","売上総利益振替","20240930","1005","振替","","",""},
			/*  5 */ {"リース代","100","円","現金","100","円","販売費及び一般管理費","","1006","東京リース","C002","東京都江東区％％3－4－6","販売費・販売管理費"},
			/*  6 */ {"営業利益","100","円","リース代","100","円","リース代の営業利益振替","","1007","振替","","",""},
			/*  7 */ {"営業利益","250","円","売上総利益","250","円","売上総利益の営業利益振替","","1008","振替","","",""},
			/*  8 */ {"売上総利益","500","円","営業利益","500","円","売上総利益の営業利益振替","","1009","振替","","",""},
			/*  9 */ {"現金","100","円","利息","100","円","営業外利益","","1010","市川銀行","C001","千葉県市川市％％1-2-3","営業外利益・営業外損失"},
			/* 10 */ {"利息","100","円","経常利益","100","円","利息の経常利益への振替","","1011","振替","","",""},
			/* 11 */ {"経常利益","100","円","営業利益","100","円","営業利益の経常利益振替","","1012","振替","","",""},
			/* 12 */ {"経常利益","250","円","営業利益","250","円","営業利益の経常利益振替","","1013","振替","","",""},
			/* 13 */ {"営業利益","500","円","経常利益","500","円","営業利益の経常利益振替","","1014","振替","","",""},
			/* 14 */ {"特別損失","100","円","商品","100","円","特別損失","","1015","自己","","","特別利益・特別損失"},
			/* 15 */ {"当期純利益","100","円","特別損失","100","円","損失の当期純利益への振替","","1016","振替","","",""},
			/* 16 */ {"経常利益","100","円","当期純利益","100","円","経常利益の当期純利益への振替","","1017","振替","","",""},
			/* 17 */ {"当期純利益","100","円","経常利益","100","円","経常利益の当期純利益への振替","","1018","振替","","",""},
			/* 18 */ {"当期純利益","250","円","経常利益","250","円","経常利益の当期純利益への振替","","1019","振替","","",""},
			/* 19 */ {"経常利益","500","円","当期純利益","500","円","経常利益の当期純利益への振替","","1020","振替","","",""},
	};
	static protected final String EXP_STR_CSVRECORDS_1_BY_CONDITION1[][] = {
			/*  1 */ {"現金","500","円","売上","500","円","売上計上","20240918","1002","田中小売店","B111","東京都足立区％％３－４－５","商品販売"},
	};
	static protected final DtBinder EXP_DTBINDER_FROM_CSV_NO_CONDITION = makeBinderByDoubleEntrySimpleSlipsCsvRecords(EXP_STR_CSVRECORDS_1_NO_CONDITION);
	static protected final File outFile_DTBINDER_FROM_CSV_NO_CONDITION = new File(pathBinderRoot, "out_binder_no_condition.json");
	static protected final DtBinder EXP_DTBINDER_FROM_CSV_BY_CONDITION1 = makeBinderByDoubleEntrySimpleSlipsCsvRecords(EXP_STR_CSVRECORDS_1_BY_CONDITION1);
	static protected final File outFile_DTBINDER_FROM_CSV_BY_CONDITION1 = new File(pathBinderRoot, "out_binder_by_condition1.json");

	static protected final File fileOutUnbalanceDoubleEntrySimpleSlipsCsv_SJIS_1 = new File(pathCsvOut, "out_unbalance_journal_sample_sjis_1.csv");
	static protected final File fileOutUnbalanceDoubleEntrySimpleSlipsCsv_SJIS_2 = new File(pathCsvOut, "out_unbalance_journal_sample_sjis_2.csv");
	static protected final File fileOutUnbalanceDoubleEntrySimpleSlipsCsv_UTF8_1 = new File(pathCsvOut, "out_unbalance_journal_sample_utf8_1.csv");
	static protected final File fileOutUnbalanceDoubleEntrySimpleSlipsCsv_UTF8_2 = new File(pathCsvOut, "out_unbalance_journal_sample_utf8_2.csv");
	static protected final String EXP_STR_UNBALANCE_CSVRECORDS_1_NO_CONDITION[][] = {
			/*  0 */ {"商品","500","円","買掛金","500","円","仕入れ","20240910","1001","山田商店","A123","東京都新宿区％％２－１－４","商品購入"},
			/*  1 */ {"受取手形","250","円","","","","売上計上","20240918","1002","田中小売店","B111","東京都足立区％％３－４－５","商品販売"},
			/*  1 */ {"現金","250","円","売上","500","円","売上計上","20240918","1002","田中小売店","B111","東京都足立区％％３－４－５","商品販売"},
			/*  2 */ {"売上原価","250","円","商品","250","円","売上原価計上","20240918","1003","振替","","",""},
			/*  3 */ {"売上","500","円","売上総利益","500","円","売上総利益振替","20240930","1004","振替","","",""},
			/*  4 */ {"売上総利益","250","円","売上原価","250","円","売上総利益振替","20240930","1005","振替","","",""},
			/*  5 */ {"リース代","100","円","現金","50","円","販売費及び一般管理費","","1006","東京リース","C002","東京都江東区％％3－4－6","販売費・販売管理費"},
			/*  5 */ {"","","","支払手形","50","円","販売費及び一般管理費","","1006","東京リース","C002","東京都江東区％％3－4－6","販売費・販売管理費"},
			/*  6 */ {"営業利益","100","円","リース代","100","円","リース代の営業利益振替","","1007","振替","","",""},
			/*  7 */ {"営業利益","250","円","売上総利益","250","円","売上総利益の営業利益振替","","1008","振替","","",""},
			/*  8 */ {"売上総利益","500","円","営業利益","500","円","売上総利益の営業利益振替","","1009","振替","","",""},
			/*  9 */ {"現金","100","円","利息","100","円","営業外利益","","1010","市川銀行","C001","千葉県市川市％％1-2-3","営業外利益・営業外損失"},
			/* 9.5 */ {"現金","245","円","受取手形","250","円","手形決済","","1020","市川銀行","C001","千葉県市川市％％1-2-3","営業外利益・営業外損失"},
			/* 9.5 */ {"手形売却損","5","円","","","","手形決済","","1020","市川銀行","C001","千葉県市川市％％1-2-3","営業外利益・営業外損失"},
			/* 10 */ {"利息","100","円","経常利益","100","円","利息の経常利益への振替","","1011","振替","","",""},
			/* 11 */ {"経常利益","100","円","営業利益","100","円","営業利益の経常利益振替","","1012","振替","","",""},
			/* 12 */ {"経常利益","250","円","営業利益","250","円","営業利益の経常利益振替","","1013","振替","","",""},
			/* 13 */ {"営業利益","500","円","経常利益","500","円","営業利益の経常利益振替","","1014","振替","","",""},
			/* 14 */ {"特別損失","100","円","商品","100","円","特別損失","","1015","自己","","","特別利益・特別損失"},
			/* 15 */ {"当期純利益","100","円","特別損失","100","円","損失の当期純利益への振替","","1016","振替","","",""},
			/* 16 */ {"経常利益","100","円","当期純利益","100","円","経常利益の当期純利益への振替","","1017","振替","","",""},
			/* 17 */ {"当期純利益","100","円","経常利益","100","円","経常利益の当期純利益への振替","","1018","振替","","",""},
			/* 18 */ {"当期純利益","250","円","経常利益","250","円","経常利益の当期純利益への振替","","1019","振替","","",""},
			/* 19 */ {"経常利益","500","円","当期純利益","500","円","経常利益の当期純利益への振替","","1020","振替","","",""},
	};
	static protected final DtBinder EXP_SRC_UNBALANCE_DTBINDER_FROM_CSV_NO_CONDITION;
	static {
		String[][] srcRecords = EXP_STR_UNBALANCE_CSVRECORDS_1_NO_CONDITION;
		DtBinder binder = new DtBinder();
		DtSlip nextSlip = null;
		DtSlip curSlip = null;

		int index = 0;
		// 0
		curSlip = makeSlipByDoubleEntrySimpleSlipsCsvFields(srcRecords[index++]);
		binder.putDtSlipObject("slip" + index, curSlip);
		// 1, 1
		curSlip = makeSlipByDoubleEntrySimpleSlipsCsvFields(srcRecords[index++]);
		nextSlip = makeSlipByDoubleEntrySimpleSlipsCsvFields(srcRecords[index++]);
		curSlip = mergeExalgeInSlip(curSlip, nextSlip);
		binder.putDtSlipObject("slip" + index, curSlip);
		// 2
		curSlip = makeSlipByDoubleEntrySimpleSlipsCsvFields(srcRecords[index++]);
		binder.putDtSlipObject("slip" + index, curSlip);
		// 3
		curSlip = makeSlipByDoubleEntrySimpleSlipsCsvFields(srcRecords[index++]);
		binder.putDtSlipObject("slip" + index, curSlip);
		// 4
		curSlip = makeSlipByDoubleEntrySimpleSlipsCsvFields(srcRecords[index++]);
		binder.putDtSlipObject("slip" + index, curSlip);
		// 5, 5
		curSlip = makeSlipByDoubleEntrySimpleSlipsCsvFields(srcRecords[index++]);
		nextSlip = makeSlipByDoubleEntrySimpleSlipsCsvFields(srcRecords[index++]);
		curSlip = mergeExalgeInSlip(curSlip, nextSlip);
		binder.putDtSlipObject("slip" + index, curSlip);
		// 6
		curSlip = makeSlipByDoubleEntrySimpleSlipsCsvFields(srcRecords[index++]);
		binder.putDtSlipObject("slip" + index, curSlip);
		// 7
		curSlip = makeSlipByDoubleEntrySimpleSlipsCsvFields(srcRecords[index++]);
		binder.putDtSlipObject("slip" + index, curSlip);
		// 8
		curSlip = makeSlipByDoubleEntrySimpleSlipsCsvFields(srcRecords[index++]);
		binder.putDtSlipObject("slip" + index, curSlip);
		// 9
		curSlip = makeSlipByDoubleEntrySimpleSlipsCsvFields(srcRecords[index++]);
		binder.putDtSlipObject("slip" + index, curSlip);
		// 9.5
		curSlip = makeSlipByDoubleEntrySimpleSlipsCsvFields(srcRecords[index++]);
		nextSlip = makeSlipByDoubleEntrySimpleSlipsCsvFields(srcRecords[index++]);
		curSlip = mergeExalgeInSlip(curSlip, nextSlip);
		binder.putDtSlipObject("slip" + index, curSlip);
		// 10
		curSlip = makeSlipByDoubleEntrySimpleSlipsCsvFields(srcRecords[index++]);
		binder.putDtSlipObject("slip" + index, curSlip);
		// 11
		curSlip = makeSlipByDoubleEntrySimpleSlipsCsvFields(srcRecords[index++]);
		binder.putDtSlipObject("slip" + index, curSlip);
		// 12
		curSlip = makeSlipByDoubleEntrySimpleSlipsCsvFields(srcRecords[index++]);
		binder.putDtSlipObject("slip" + index, curSlip);
		// 13
		curSlip = makeSlipByDoubleEntrySimpleSlipsCsvFields(srcRecords[index++]);
		binder.putDtSlipObject("slip" + index, curSlip);
		// 14
		curSlip = makeSlipByDoubleEntrySimpleSlipsCsvFields(srcRecords[index++]);
		binder.putDtSlipObject("slip" + index, curSlip);
		// 15
		curSlip = makeSlipByDoubleEntrySimpleSlipsCsvFields(srcRecords[index++]);
		binder.putDtSlipObject("slip" + index, curSlip);
		// 16
		curSlip = makeSlipByDoubleEntrySimpleSlipsCsvFields(srcRecords[index++]);
		binder.putDtSlipObject("slip" + index, curSlip);
		// 17
		curSlip = makeSlipByDoubleEntrySimpleSlipsCsvFields(srcRecords[index++]);
		binder.putDtSlipObject("slip" + index, curSlip);
		// 18
		curSlip = makeSlipByDoubleEntrySimpleSlipsCsvFields(srcRecords[index++]);
		binder.putDtSlipObject("slip" + index, curSlip);
		// 19
		curSlip = makeSlipByDoubleEntrySimpleSlipsCsvFields(srcRecords[index++]);
		binder.putDtSlipObject("slip" + index, curSlip);
		
		EXP_SRC_UNBALANCE_DTBINDER_FROM_CSV_NO_CONDITION = binder;
	}
	static protected final DtBinder EXP_DST_UNBALANCE_DTBINDER_FROM_CSV_NO_CONDITION = makeBinderByDoubleEntrySimpleSlipsCsvRecords(EXP_STR_UNBALANCE_CSVRECORDS_1_NO_CONDITION);
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static protected DtSlip mergeExalgeInSlip(DtSlip firstSlip, DtSlip secondSlip)
	{
		Exalge firstAlge = firstSlip.getExalgeObject(EXP_SLIP_DATAOBJ_NAME);
		Exalge secondAlge = secondSlip.getExalgeObject(EXP_SLIP_DATAOBJ_NAME);
		
		Exalge mergedAlge = firstAlge.plus(secondAlge);
		ExAlgeSet algeset = new ExAlgeSet();
		algeset.add(mergedAlge);
		
		DtSlip retSlip = new DtSlip();
		retSlip.setNote(firstSlip.getNote());
		retSlip.putExAlgeSetObject("exalgeset", algeset);
		return retSlip;
	}
	
	static protected DtBinder makeBinderByDoubleEntrySimpleSlipsCsvRecords(String[][] recordValues)
	{
		DtSlipList sliplist = new DtSlipList();
		for (String[] csvrec : recordValues) {
			DtSlip slip = makeSlipByDoubleEntrySimpleSlipsCsvFields(csvrec);
			sliplist.add(slip);
		}
		
		// make binder
		DtBinder binder = new DtBinder();
		if (!sliplist.isEmpty()) {
			binder.putDtSlipListObject(EXP_BINDER_SLIPOBJ_NAME, sliplist);
		}
		return binder;
	}
	
	static protected DtSlip makeSlipByDoubleEntrySimpleSlipsCsvFields(String[] fieldValues)
	{
		Boolean isDebitItem;
		BigDecimal dValue;
		ExBase exbase;
		String hatkey;
		Exalge exalge = new Exalge();
		
		// Debit side
		if (!Strings.isNullOrEmpty(fieldValues[0]) && !Strings.isNullOrEmpty(fieldValues[1])) {
			isDebitItem = EXP_DEBITCREDIT_NAMEMAP.get(fieldValues[0]);
			hatkey = (isDebitItem != null && !isDebitItem.booleanValue() ? ExBase.HAT : ExBase.NO_HAT);
			exbase = new ExBase(fieldValues[0], hatkey, fieldValues[2]);
			try {
				dValue = new BigDecimal(fieldValues[1]);
			} catch (Throwable ex) {
				dValue = null;
			}
			exalge.add(exbase, dValue);
		}
		
		// Credit side
		if (!Strings.isNullOrEmpty(fieldValues[3]) && !Strings.isNullOrEmpty(fieldValues[4])) {
			isDebitItem = EXP_DEBITCREDIT_NAMEMAP.get(fieldValues[3]);
			hatkey = (isDebitItem != null && !isDebitItem.booleanValue() ? ExBase.NO_HAT : ExBase.HAT);
			exbase = new ExBase(fieldValues[3], hatkey, fieldValues[5]);
			try {
				dValue = new BigDecimal(fieldValues[4]);
			} catch (Throwable ex) {
				dValue = null;
			}
			exalge.add(exbase, dValue);
		}
		
		// note
		DtBase dtbase;
		Dtalge note = new Dtalge();
		for (int fldidx = 6; fldidx < fieldValues.length; fldidx++) {
			String fldval = fieldValues[fldidx];
			if (Strings.isNullOrEmpty(fldval)) {
				continue;
			}

			String nameKey = EXP_CSVCOLIDX_NAMEMAP.get(fldidx);
			dtbase = DtBase.newBase(nameKey, DtDataTypes.STRING);
			note.add(dtbase, fldval);
		}
		
		// slip
		DtSlip slip = new DtSlip();
		if (!note.isEmpty()) {
			slip.setNote(note);
		}
		if (!exalge.isEmpty()) {
			slip.putExalgeObject(EXP_SLIP_DATAOBJ_NAME, exalge);
		}
		return slip;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
