/**
 * 
 */
package dtalge.container.util.internal.csv.filter.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import dtalge.container.util.CsvFieldConditionStringParseError;
import dtalge.container.util.internal.csv.CsvColumnInfo;
import dtalge.container.util.internal.csv.DtDoubleEntrySimpleSlipsCsvHeader;
import dtalge.container.util.internal.csv.filter.CsvFIeldConditionAnd;
import dtalge.container.util.internal.csv.filter.CsvFieldConditionNot;
import dtalge.container.util.internal.csv.filter.CsvFieldConditionOr;
import dtalge.container.util.internal.csv.filter.CsvFieldConditionPattern;
import dtalge.container.util.internal.csv.filter.ICsvFieldCondition;
import dtalge.io.internal.CsvReader.CsvField;
import dtalge.io.internal.CsvReader.CsvRecord;
import junit.framework.TestCase;

/**
 * {@link CsvFieldConditionStringParser} クラスのユニットテスト。
 * 
 * @author Y.Ishizuka(PieCake,Inc.)
 * 
 * @since 0.2.0
 */
public class CsvFieldConditionStringParserTest extends TestCase
{
	//------------------------------------------------------------
	// Test cases
	//------------------------------------------------------------

	/**
	 * Test method for {@link dtalge.container.util.internal.csv.filter.parser.CsvFieldConditionStringParser#CsvFieldConditionStringParser(java.lang.String)}.
	 */
	public void testCsvFieldConditionStringParser() {
		CsvFieldConditionStringParser parser;
		
		// null
		parser = new CsvFieldConditionStringParser(null);
		assertEquals("", parser.getSourceString());
		
		// empty
		parser = new CsvFieldConditionStringParser("");
		assertEquals("", parser.getSourceString());
		
		// string
		parser = new CsvFieldConditionStringParser(EXP_STR_CONDITION01);
		assertEquals(EXP_STR_CONDITION01, parser.getSourceString());
		parser = new CsvFieldConditionStringParser(EXP_STR_CONDITION02);
		assertEquals(EXP_STR_CONDITION02, parser.getSourceString());
	}
	
	/**
	 * Test method for {@link dtalge.container.util.internal.csv.filter.parser.CsvFieldConditionStringParser#parse()}.
	 * Test method for {@link dtalge.container.util.internal.csv.filter.parser.CsvFieldConditionStringParser#getCachedFieldPositions()}.
	 */
	public void testParseEmptyString() {
		CsvFieldConditionStringParser parser;
		ICsvFieldCondition retCondition = null;
		CsvColumnInfo[] retPositions = null;
		
		// empty
		parser = new CsvFieldConditionStringParser("");
		try {
			retCondition = parser.parse();
		} catch (CsvFieldConditionStringParseError ex) {
			fail();
		}
		assertNull(retCondition);
		retPositions = parser.getCachedFieldPositions();
		assertEquals(0, retPositions.length);
		
		// all blanks
		parser = new CsvFieldConditionStringParser("\t\r\n\f\b ");
		try {
			retCondition = parser.parse();
		} catch (CsvFieldConditionStringParseError ex) {
			fail();
		}
		assertNull(retCondition);
		retPositions = parser.getCachedFieldPositions();
		assertEquals(0, retPositions.length);
		
		// blanks with commas
		parser = new CsvFieldConditionStringParser(",,\t , \r  , \n\f\b ,,,,");
		try {
			retCondition = parser.parse();
		} catch (CsvFieldConditionStringParseError ex) {
			fail();
		}
		assertNull(retCondition);
		retPositions = parser.getCachedFieldPositions();
		assertEquals(0, retPositions.length);
		
		// comma only
		parser = new CsvFieldConditionStringParser(",");
		try {
			retCondition = parser.parse();
		} catch (CsvFieldConditionStringParseError ex) {
			fail();
		}
		assertNull(retCondition);
		retPositions = parser.getCachedFieldPositions();
		assertEquals(0, retPositions.length);
	}
	
	/**
	 * Test method for {@link dtalge.container.util.internal.csv.filter.parser.CsvFieldConditionStringParser#parse()}.
	 * Test method for {@link dtalge.container.util.internal.csv.filter.parser.CsvFieldConditionStringParser#getCachedFieldPositions()}.
	 */
	public void testParseSimpleConditionString() {
		CsvFieldConditionStringParser parser;
		ICsvFieldCondition retCondition = null;
		CsvColumnInfo[] retPositions = null;
		
		// EXP_STR_CONDITION-11
		parser = new CsvFieldConditionStringParser(EXP_STR_CONDITION11);
		try {
		retCondition = parser.parse();
		retPositions = parser.getCachedFieldPositions();
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail();
		}
		assertNotNull(retCondition);
		assertEquals(EXP_FLD_CONDITION11, retCondition);
		assertTrue(Arrays.deepEquals(EXP_POS_CONDITION11, retPositions));
		
		// EXP_STR_CONDITION-12
		parser = new CsvFieldConditionStringParser(EXP_STR_CONDITION12);
		try {
		retCondition = parser.parse();
		retPositions = parser.getCachedFieldPositions();
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail();
		}
		assertNotNull(retCondition);
		assertEquals(EXP_FLD_CONDITION11, retCondition);
		assertTrue(Arrays.deepEquals(EXP_POS_CONDITION11, retPositions));
		
		// check isSatisfied
		updateConditionByCsvHeader(retPositions, EXP_CSVHEADER);
		for (int i = 0; i < EXP_CSVRECORDS.length; i++) {
			boolean result = retCondition.isSatisfied(EXP_CSVRECORDS[i]);
			assertEquals("[" + i + "]", EXP_ACCEPT_CSVRECORD_BY_CONDITION1[i], result);
		}
		
		// EXP_STR_CONDITION-3
		parser = new CsvFieldConditionStringParser(EXP_STR_CONDITION3);
		try {
		retCondition = parser.parse();
		retPositions = parser.getCachedFieldPositions();
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			fail();
		}
		assertNotNull(retCondition);
		assertEquals(EXP_FLD_CONDITION3, retCondition);
		assertTrue(Arrays.deepEquals(EXP_POS_CONDITION3, retPositions));
		
		// check isSatisfied
		updateConditionByCsvHeader(retPositions, EXP_CSVHEADER);
		for (int i = 0; i < EXP_CSVRECORDS.length; i++) {
			boolean result = retCondition.isSatisfied(EXP_CSVRECORDS[i]);
			assertEquals("[" + i + "]", EXP_ACCEPT_CSVRECORD_BY_CONDITION3[i], result);
		}
	}

//	/**
//	 * Test method for {@link dtalge.container.util.internal.csv.filter.parser.CsvFieldConditionStringParser#getCachedFieldPositions()}.
//	 */
//	public void testGetCachedFieldPositions() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link dtalge.container.util.internal.csv.filter.parser.CsvFieldConditionStringParser#parse()}.
//	 */
//	public void testParse() {
//		fail("Not yet implemented");
//	}

	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final String EXP_STR_FLDNAME_KUBUN   = "区分";
	static protected final String EXP_STR_FLDNAME_SHIWAKE = "仕訳番号";
	static protected final String EXP_STR_FLDNAME_BIKOU   = "備考";
	
	static protected final String EXP_STR_KUBUN_URIAGE_NAK = "売上";
	static protected final String EXP_STR_KUBUN_URIAGE_ENQ = "\"売上\"";
	static protected final String EXP_STR_KUBUN_RIEKI_NAK  = "利益";
	static protected final String EXP_STR_KUBUN_RIEKI_ENQ  = "\"利益\"";
	static protected final String EXP_STR_KUBUN_GENKA_NAK  = "原価";
	static protected final String EXP_STR_KUBUN_GENKA_ENQ  = "\"原価\"";
	
	static protected final String EXP_STR_SHIWAKE_PAT1_NAK  = "^100";
	static protected final String EXP_STR_SHIWAKE_PAT1_ENQ  = "\"^100\"";
	static protected final String EXP_STR_SHIWAKE_PAT2_NAK  = "2\\z";
	static protected final String EXP_STR_SHIWAKE_PAT2_ENQ  = "\"2\\\\z\"";
	
	static protected final String EXP_STR_BIKOU_PAT1_NAK    = "売";
	static protected final String EXP_STR_BIKOU_PAT1_ENQ    = "\"売\"";
	static protected final String EXP_STR_BIKOU_PAT2_NAK    = "商";
	static protected final String EXP_STR_BIKOU_PAT2_ENQ    = "\"商\"";
	
	static protected final Pattern EXP_REGEXP_KUBUN_URIAGE = Pattern.compile(EXP_STR_KUBUN_URIAGE_NAK, Pattern.DOTALL);
	static protected final Pattern EXP_REGEXP_KUBUN_RIEKI  = Pattern.compile(EXP_STR_KUBUN_RIEKI_NAK, Pattern.DOTALL);
	static protected final Pattern EXP_REGEXP_KUBUN_GENKA  = Pattern.compile(EXP_STR_KUBUN_GENKA_NAK, Pattern.DOTALL);
	
	static protected final Pattern EXP_REGEXP_SHIWAKE_PAT1  = Pattern.compile(EXP_STR_SHIWAKE_PAT1_NAK, Pattern.DOTALL);
	static protected final Pattern EXP_REGEXP_SHIWAKE_PAT2  = Pattern.compile(EXP_STR_SHIWAKE_PAT2_NAK, Pattern.DOTALL);
	
	static protected final Pattern EXP_REGEXP_BIKOU_PAT1    = Pattern.compile(EXP_STR_BIKOU_PAT1_NAK, Pattern.DOTALL);
	static protected final Pattern EXP_REGEXP_BIKOU_PAT2    = Pattern.compile(EXP_STR_BIKOU_PAT2_NAK, Pattern.DOTALL);
	
	static protected final String EXP_STR_CONDITION01 = EXP_STR_FLDNAME_KUBUN + "=" + EXP_STR_KUBUN_RIEKI_ENQ;
	static protected final String EXP_STR_CONDITION02 = EXP_STR_FLDNAME_KUBUN + " = " + EXP_STR_KUBUN_RIEKI_NAK;
	
	static protected final String EXP_STR_CONDITION11 = ",,, " + EXP_STR_FLDNAME_KUBUN + "==" + EXP_STR_KUBUN_URIAGE_NAK
													  + ", " + EXP_STR_FLDNAME_SHIWAKE + " = " + EXP_STR_SHIWAKE_PAT1_ENQ
													  + ", , , " + EXP_STR_FLDNAME_BIKOU + "=" + EXP_STR_BIKOU_PAT2_ENQ
													  + ",";
	static protected final String EXP_STR_CONDITION12 = EXP_STR_FLDNAME_KUBUN + "==" + EXP_STR_KUBUN_URIAGE_NAK
													  + " && \"" + EXP_STR_FLDNAME_SHIWAKE + "\" = " + EXP_STR_SHIWAKE_PAT1_ENQ
													  + "&&" + EXP_STR_FLDNAME_BIKOU + "=" + EXP_STR_BIKOU_PAT2_ENQ
													  + ",";
	static protected final String EXP_STR_CONDITION3 = "仕訳番号!=\"^100\" && ( !(区分=\"売上\" || 区分=\"営業\") && 区分=\"振替\" )";

	// 区分=売上, 仕訳番号=^100, 備考=商
	static protected final boolean[] EXP_ACCEPT_CSVRECORD_BY_CONDITION1 = {
		/*  0 */ false,
		/*  1 */ true,
		/*  2 */ false,
		/*  3 */ false,
		/*  4 */ false,
		/*  5 */ false,
		/*  6 */ false,
		/*  7 */ false,
		/*  8 */ false,
		/*  9 */ false,
		/* 10 */ false,
		/* 11 */ false,
		/* 12 */ false,
		/* 13 */ false,
		/* 14 */ false,
		/* 15 */ false,
		/* 16 */ false,
		/* 17 */ false,
		/* 18 */ false,
		/* 19 */ false,
	};

	// 仕訳番号!=\"^100\" && !( (区分=\"売上\" || 区分=\"営業\") && 区分!=\"振替\" )
	static protected final boolean[] EXP_ACCEPT_CSVRECORD_BY_CONDITION3 = {
		/*  0 */ false,
		/*  1 */ false,
		/*  2 */ false,
		/*  3 */ false,
		/*  4 */ false,
		/*  5 */ false,
		/*  6 */ false,
		/*  7 */ false,
		/*  8 */ false,
		/*  9 */ false,
		/* 10 */ true,
		/* 11 */ false,
		/* 12 */ false,
		/* 13 */ false,
		/* 14 */ false,
		/* 15 */ true,
		/* 16 */ true,
		/* 17 */ true,
		/* 18 */ true,
		/* 19 */ true,
	};
	
	static protected final CsvColumnInfo[]    EXP_POS_CONDITION_EMPTYT = new CsvColumnInfo[0];
	static protected final ICsvFieldCondition EXP_FLD_CONDITION11;
	static protected final CsvColumnInfo[]    EXP_POS_CONDITION11;
	static protected final ICsvFieldCondition EXP_FLD_CONDITION3;
	static protected final CsvColumnInfo[]    EXP_POS_CONDITION3;
	static {
		ICsvFieldCondition fldcond;
		ICsvFieldCondition topcond;
		
		// EXP_STR_CONDITION11 => ICsv
		topcond = new CsvFieldConditionPattern(EXP_STR_FLDNAME_KUBUN, EXP_REGEXP_KUBUN_URIAGE);
		fldcond = new CsvFieldConditionPattern(EXP_STR_FLDNAME_SHIWAKE, EXP_REGEXP_SHIWAKE_PAT1);
		topcond = new CsvFIeldConditionAnd(topcond, fldcond);
		fldcond = new CsvFieldConditionPattern(EXP_STR_FLDNAME_BIKOU, EXP_REGEXP_BIKOU_PAT2);
		topcond = new CsvFIeldConditionAnd(topcond, fldcond);
		EXP_FLD_CONDITION11 = topcond;
		EXP_POS_CONDITION11 = new CsvColumnInfo[]{
				new CsvColumnInfo(EXP_STR_FLDNAME_KUBUN), new CsvColumnInfo(EXP_STR_FLDNAME_SHIWAKE), new CsvColumnInfo(EXP_STR_FLDNAME_BIKOU),
		};
		
		// EXP_STR_CONDITION13 => ICsv
		// "仕訳番号!=\"^100\" && ( !(区分=\"売上\" || 区分=\"営業\") && 区分=\"振替\" )"
		topcond = new CsvFieldConditionPattern(EXP_STR_FLDNAME_KUBUN, Pattern.compile("売上", Pattern.DOTALL));
		fldcond = new CsvFieldConditionPattern(EXP_STR_FLDNAME_KUBUN, Pattern.compile("営業", Pattern.DOTALL));
		topcond = new CsvFieldConditionOr(topcond, fldcond); // ||
		topcond = new CsvFieldConditionNot(topcond); // !()
		fldcond = new CsvFieldConditionPattern(EXP_STR_FLDNAME_KUBUN, Pattern.compile("振替", Pattern.DOTALL));
		topcond = new CsvFIeldConditionAnd(topcond, fldcond); // &&
		fldcond = new CsvFieldConditionPattern(EXP_STR_FLDNAME_SHIWAKE, Pattern.compile("^100", Pattern.DOTALL));
		fldcond = new CsvFieldConditionNot(fldcond); // !=
		topcond = new CsvFIeldConditionAnd(fldcond, topcond); // &&
		EXP_FLD_CONDITION3 = topcond;
		EXP_POS_CONDITION3 = new CsvColumnInfo[]{
				new CsvColumnInfo(EXP_STR_FLDNAME_SHIWAKE), new CsvColumnInfo(EXP_STR_FLDNAME_KUBUN), new CsvColumnInfo(EXP_STR_FLDNAME_KUBUN), new CsvColumnInfo(EXP_STR_FLDNAME_KUBUN),
		};
	}
	
	static protected final String[] EXP_STR_CSVHEADER = {
		"Dr.name", "Dr.value", "Dr.unit", "Cr.name", "Cr.value", "Cr.unit", "区分", "日付", "仕訳番号", "取引先", "取引先コード", "取引先住所", "備考",
	};
	
	static protected final String EXP_STR_CSVRECORDS[][] = {
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
	
	static protected final DtDoubleEntrySimpleSlipsCsvHeader EXP_CSVHEADER;
	static protected final CsvRecord[] EXP_CSVRECORDS;
	static {
		CsvRecord csvrec = makeCsvRecord(1, EXP_STR_CSVHEADER);
		EXP_CSVHEADER = makeCsvRecordHeader(csvrec);

		EXP_CSVRECORDS = new CsvRecord[EXP_STR_CSVRECORDS.length];
		for (int li = 0; li < EXP_STR_CSVRECORDS.length; li++) {
			csvrec = makeCsvRecord(li+2, EXP_STR_CSVRECORDS[li]);
			EXP_CSVRECORDS[li] = csvrec;
		}
	}
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public DtDoubleEntrySimpleSlipsCsvHeader makeCsvRecordHeader(CsvRecord csvrec)
	{
		DtDoubleEntrySimpleSlipsCsvHeader csvheader = new DtDoubleEntrySimpleSlipsCsvHeader();
		try {
			csvheader.readHeaderFromCsvRecord(csvrec, false);
		}
		catch (Throwable ex) {
			throw new RuntimeException("CSV header read error: " + ex.getMessage(), ex);
		}
		return csvheader;
	}
	
	static public CsvRecord makeCsvRecord(int lineno, String[] csvline)
	{
		StringBuilder strbuf = new StringBuilder();
		ArrayList<CsvField> fields = new ArrayList<>();

		int spos = 0;
		int colidx = 0;
		for (String strfld : csvline) {
			if (colidx != 0) {
				strbuf.append(',');
			}
			spos = strbuf.length();
			strbuf.append(strfld);
			CsvField csvfld = new CsvField(colidx, lineno, spos, strfld);
			fields.add(csvfld);
			++colidx;
		}
		
		CsvRecord csvrec = new CsvRecord(lineno, lineno, strbuf.toString(), fields.toArray(new CsvField[fields.size()]));
		return csvrec;
	}
	
	static public boolean updateConditionByCsvHeader(CsvColumnInfo[] fieldPositions, DtDoubleEntrySimpleSlipsCsvHeader csvheader)
	{
		// カラム情報のフィールドインデックスを更新
		boolean modified = false;
		for (CsvColumnInfo colInfo : fieldPositions) {
			int fieldIndex = csvheader.noteItemIndexToCsvFieldIndex( csvheader.getNoteItemIndexByName(colInfo.getName()) );
			if (fieldIndex < 0) {
				if (colInfo.setIndex(-1)) {
					// 変更あり
					modified = true;
				}
			}
			else {
				if (colInfo.setIndex(fieldIndex)) {
					// 変更あり
					modified = true;
				}
			}
		}
		
		return modified;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
