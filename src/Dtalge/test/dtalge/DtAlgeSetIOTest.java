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
 * @(#)DtAlgeSetIOTest.java	0.20	2010/02/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtAlgeSetIOTest.java	0.10	2008/09/02
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge;

import static dtalge.AllUnitTests.*;

import java.io.File;
import java.math.BigDecimal;

import junit.framework.TestCase;

/**
 * <code>DtAlgeSet</code> のファイル入出力機能のユニットテスト。
 * 
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class DtAlgeSetIOTest extends TestCase
{
	static protected final File tocsv_def = new File(csvpath, "OutputDtAlgeSet.csv");
	static protected final File tocsv_sjis = new File(csvpath, "OutputDtAlgeSet_SJIS.csv");
	static protected final File tocsv_utf8 = new File(csvpath, "OutputDtAlgeSet_UTF8.csv");
	static protected final File toxml = new File(xmlpath, "OutputDtAlgeSet.xml");
	static protected final File fromcsv_def = new File(csvpath, "NormalDtAlgeSet.csv");
	static protected final File fromcsv_sjis = new File(csvpath, "NormalDtAlgeSet_SJIS.csv");
	static protected final File fromcsv_utf8 = new File(csvpath, "NormalDtAlgeSet_UTF8.csv");
	static protected final File fromxml = new File(xmlpath, "NormalDtAlgeSet.xml");
	
	static protected final Object[][] normalAlges = {
		{	// [0]
			new DtBase("名前","string","#","#"), "北川",
			new DtBase("性別","string","#","#"), "女性",
			//new DtBase("年齢","integer","#","#"), new BigInteger("29"),
			new DtBase("年齢","decimal","#","#"), new BigDecimal("29"),
			new DtBase("都道府県","string","#","#"), "三重県",
			new DtBase("注目食品","string","#","#"), "マンゴーミルクプリン",
			new DtBase("ポイント","decimal","#","#"), new BigDecimal("0.1"),
			new DtBase("フラグ","boolean","#","#"), Boolean.TRUE,
			new DtBase("備考","string","#","#"), null,
		},
		{	// [1]
			new DtBase("名前","string","#","#"), "上原",
			new DtBase("性別","string","#","#"), "男性",
			//new DtBase("年齢","integer","#","#"), new BigInteger("35"),
			new DtBase("年齢","decimal","#","#"), new BigDecimal("35"),
			new DtBase("都道府県","string","#","#"), "佐賀県",
			new DtBase("注目食品","string","#","#"), "エコナクッキングオイルＳ",
			new DtBase("ポイント","decimal","#","#"), new BigDecimal("-0.1"),
			new DtBase("フラグ","boolean","#","#"), Boolean.FALSE,
			new DtBase("備考","string","#","#"), "特保",
		},
		{	// [2]
			new DtBase("名前","string","#","#"), "馬場",
			new DtBase("性別","string","#","#"), "男性",
			//new DtBase("年齢","integer","#","#"), new BigInteger("41"),
			new DtBase("年齢","decimal","#","#"), new BigDecimal("41"),
			new DtBase("都道府県","string","#","#"), "宮城県",
			new DtBase("注目食品","string","#","#"), "まぐろのたたきご飯",
			new DtBase("ポイント","decimal","#","#"), new BigDecimal("123.456"),
			new DtBase("フラグ","boolean","#","#"), null,
			new DtBase("備考","string","#","#"), null,
		},
		{	// [3]
			new DtBase("名前","string","#","#"), "富田",
			new DtBase("性別","string","#","#"), "男性",
			//new DtBase("年齢","integer","#","#"), new BigInteger("14"),
			new DtBase("年齢","decimal","#","#"), new BigDecimal("14"),
			new DtBase("都道府県","string","#","#"), "鹿児島県",
			new DtBase("注目食品","string","#","#"), "五穀米ご飯",
			new DtBase("ポイント","decimal","#","#"), null,
			new DtBase("フラグ","boolean","#","#"), Boolean.FALSE,
			new DtBase("備考","string","#","#"), null,
		},
		{	// [4]
			new DtBase("名前","string","#","#"), "藤原",
			new DtBase("性別","string","#","#"), "男性",
			//new DtBase("年齢","integer","#","#"), new BigInteger("48"),
			new DtBase("年齢","decimal","#","#"), new BigDecimal("48"),
			new DtBase("都道府県","string","#","#"), "長崎県",
			new DtBase("注目食品","string","#","#"), "ハンバーグ＆白身魚フライ弁当",
			new DtBase("ポイント","decimal","#","#"), new BigDecimal("-987654321.0123456789"),
			new DtBase("フラグ","boolean","#","#"), Boolean.FALSE,
			new DtBase("備考","string","#","#"), null,
		},
		{	// [5]
			new DtBase("名前","string","#","#"), "須藤",
			new DtBase("性別","string","#","#"), "女性",
			//new DtBase("年齢","integer","#","#"), new BigInteger("20"),
			new DtBase("年齢","decimal","#","#"), new BigDecimal("20"),
			new DtBase("都道府県","string","#","#"), "京都府",
			new DtBase("注目食品","string","#","#"), "チキンと豆腐のハンバーグ弁当",
			new DtBase("ポイント","decimal","#","#"), null,
			new DtBase("フラグ","boolean","#","#"), Boolean.TRUE,
			new DtBase("備考","string","#","#"), null,
		},
		{	// [6]
			new DtBase("名前","string","#","#"), "萩原",
			new DtBase("性別","string","#","#"), "女性",
			//new DtBase("年齢","integer","#","#"), new BigInteger("25"),
			new DtBase("年齢","decimal","#","#"), new BigDecimal("25"),
			new DtBase("都道府県","string","#","#"), "熊本県",
			new DtBase("注目食品","string","#","#"), "若鶏のグリルガーリックソース",
			new DtBase("ポイント","decimal","#","#"), new BigDecimal("987654321987654321.01234567890123456000"),
			new DtBase("フラグ","boolean","#","#"), null,
			new DtBase("備考","string","#","#"), null,
		},
		{	// [7]
			new DtBase("名前","string","#","#"), "小沢",
			new DtBase("性別","string","#","#"), "女性",
			//new DtBase("年齢","integer","#","#"), new BigInteger("45"),
			new DtBase("年齢","decimal","#","#"), new BigDecimal("45"),
			new DtBase("都道府県","string","#","#"), "茨城県",
			new DtBase("注目食品","string","#","#"), "ミックスサラダ",
			new DtBase("ポイント","decimal","#","#"), new BigDecimal("963"),
			new DtBase("フラグ","boolean","#","#"), Boolean.TRUE,
			new DtBase("備考","string","#","#"), null,
		},
		{	// [8]
			new DtBase("名前","string","#","#"), "栗原",
			new DtBase("性別","string","#","#"), "男性",
			//new DtBase("年齢","integer","#","#"), new BigInteger("65"),
			new DtBase("年齢","decimal","#","#"), new BigDecimal("65"),
			new DtBase("都道府県","string","#","#"), "埼玉県",
			new DtBase("注目食品","string","#","#"), "ピオテアドリンク",
			new DtBase("ポイント","decimal","#","#"), new BigDecimal("288"),
			new DtBase("フラグ","boolean","#","#"), Boolean.TRUE,
			new DtBase("備考","string","#","#"), "特保",
		},
		{	// [9]
			new DtBase("名前","string","#","#"), "岡本",
			new DtBase("性別","string","#","#"), "女性",
			//new DtBase("年齢","integer","#","#"), new BigInteger("18"),
			new DtBase("年齢","decimal","#","#"), new BigDecimal("18"),
			new DtBase("都道府県","string","#","#"), "新潟県",
			new DtBase("注目食品","string","#","#"), "コロッケ＆唐揚げ弁当",
			new DtBase("ポイント","decimal","#","#"), null,
			new DtBase("フラグ","boolean","#","#"), null,
			new DtBase("備考","string","#","#"), null,
		},
		{	// [10]
			new DtBase("名前","string","#","#"), "宮崎",
			new DtBase("性別","string","#","#"), "男性",
			//new DtBase("年齢","integer","#","#"), new BigInteger("23"),
			new DtBase("年齢","decimal","#","#"), new BigDecimal("23"),
			new DtBase("都道府県","string","#","#"), "三重県",
			new DtBase("注目食品","string","#","#"), "ハンバーグ＆唐揚げ",
			new DtBase("ポイント","decimal","#","#"), null,
			new DtBase("フラグ","boolean","#","#"), null,
			new DtBase("備考","string","#","#"), null,
		},
		{	// [11]
			new DtBase("名前","string","#","#"), "川村",
			new DtBase("性別","string","#","#"), "女性",
			//new DtBase("年齢","integer","#","#"), new BigInteger("35"),
			new DtBase("年齢","decimal","#","#"), new BigDecimal("35"),
			new DtBase("都道府県","string","#","#"), "青森県",
			new DtBase("注目食品","string","#","#"), "日清おいしさプラスサイリウムドリンクプレーン(微糖)",
			new DtBase("ポイント","decimal","#","#"), null,
			new DtBase("フラグ","boolean","#","#"), null,
			new DtBase("備考","string","#","#"), "特保",
		},
		{	// [12]
			new DtBase("名前","string","#","#"), "西村",
			new DtBase("性別","string","#","#"), "女性",
			//new DtBase("年齢","integer","#","#"), new BigInteger("68"),
			new DtBase("年齢","decimal","#","#"), new BigDecimal("68"),
			new DtBase("都道府県","string","#","#"), "岡山県",
			new DtBase("注目食品","string","#","#"), "ハンバーガー",
			new DtBase("ポイント","decimal","#","#"), new BigDecimal("-951357"),
			new DtBase("フラグ","boolean","#","#"), Boolean.TRUE,
			new DtBase("備考","string","#","#"), null,
		},
		{	// [13]
			new DtBase("名前","string","#","#"), "永田",
			new DtBase("性別","string","#","#"), "男性",
			//new DtBase("年齢","integer","#","#"), new BigInteger("60"),
			new DtBase("年齢","decimal","#","#"), new BigDecimal("60"),
			new DtBase("都道府県","string","#","#"), "島根県",
			new DtBase("注目食品","string","#","#"), "ハンバーグ＆海老フライ弁当",
			new DtBase("ポイント","decimal","#","#"), new BigDecimal("-1.1"),
			new DtBase("フラグ","boolean","#","#"), Boolean.FALSE,
			new DtBase("備考","string","#","#"), null,
		},
		{	// [14]
			new DtBase("名前","string","#","#"), "三宅",
			new DtBase("性別","string","#","#"), "女性",
			//new DtBase("年齢","integer","#","#"), new BigInteger("58"),
			new DtBase("年齢","decimal","#","#"), new BigDecimal("58"),
			new DtBase("都道府県","string","#","#"), "宮崎県",
			new DtBase("注目食品","string","#","#"), "鶏の唐揚げ",
			new DtBase("ポイント","decimal","#","#"), null,
			new DtBase("フラグ","boolean","#","#"), null,
			new DtBase("備考","string","#","#"), null,
		},
		{	// [15]
			new DtBase("名前","string","#","#"), "市川",
			new DtBase("性別","string","#","#"), "女性",
			//new DtBase("年齢","integer","#","#"), new BigInteger("60"),
			new DtBase("年齢","decimal","#","#"), new BigDecimal("60"),
			new DtBase("都道府県","string","#","#"), "大阪府",
			new DtBase("注目食品","string","#","#"), "和風大皿盛り合わせ",
			new DtBase("ポイント","decimal","#","#"), new BigDecimal("987000000"),
			new DtBase("フラグ","boolean","#","#"), Boolean.TRUE,
			new DtBase("備考","string","#","#"), null,
		}
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
		File target;
		DtAlgeSet ret = new DtAlgeSet();
		
		// Normal data
		target = fromcsv_def;
		//--- read
		try {
			ret = DtAlgeSet.fromCSV(target);
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from CSV file \"" + target.getPath() + "\"");
		}
		//--- check
		assertTrue(DtAlgeSetTest.equalElementSequence(ret, normalAlges));
	}

	/**
	 * Test method for {@link dtalge.DtAlgeSet#fromCSV(java.io.File, java.lang.String)}.
	 */
	public void testFromCSVFileString() {
		File target;
		DtAlgeSet ret;
		
		//--------------------------------------
		// SJIS
		//--------------------------------------
		
		// Normal data
		target = fromcsv_sjis;
		ret = new DtAlgeSet();
		//--- read
		try {
			ret = DtAlgeSet.fromCSV(target, SJIS);
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from CSV file \"" + target.getPath() + "\"");
		}
		//--- check
		assertTrue(DtAlgeSetTest.equalElementSequence(ret, normalAlges));
		
		//--------------------------------------
		// UTF-8
		//--------------------------------------
		
		// Normal data
		target = fromcsv_utf8;
		ret = new DtAlgeSet();
		//--- read
		try {
			ret = DtAlgeSet.fromCSV(target, UTF8);
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from CSV file \"" + target.getPath() + "\"");
		}
		//--- check
		assertTrue(DtAlgeSetTest.equalElementSequence(ret, normalAlges));
	}

	/**
	 * Test method for {@link dtalge.DtAlgeSet#fromXML(java.io.File)}.
	 */
	public void testFromXML() {
		File target;
		DtAlgeSet ret = new DtAlgeSet();
		
		// Normal data
		target = fromxml;
		//--- read
		try {
			ret = DtAlgeSet.fromXML(target);
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from XML file \"" + target.getPath() + "\"");
		}
		//--- check
		assertTrue(DtAlgeSetTest.equalElementSequence(ret, normalAlges));
	}

	/**
	 * Test method for {@link dtalge.DtAlgeSet#toCSV(java.io.File)}.
	 */
	public void testToCSVFile() {
		File target = tocsv_def;
		DtAlgeSet data = new DtAlgeSet(DtAlgeSetTest.makeDtalges(normalAlges));

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
			DtAlgeSet ret = DtAlgeSet.fromCSV(target);
			assertTrue(DtAlgeSetTest.equalElementSequence(ret, normalAlges));
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from CSV file \"" + target.getPath() + "\"");
		}
	}

	/**
	 * Test method for {@link dtalge.DtAlgeSet#toCSV(java.io.File, java.lang.String)}.
	 */
	public void testToCSVFileString() {
		File target;
		DtAlgeSet data = new DtAlgeSet(DtAlgeSetTest.makeDtalges(normalAlges));

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
			DtAlgeSet ret = DtAlgeSet.fromCSV(target, SJIS);
			assertTrue(DtAlgeSetTest.equalElementSequence(ret, normalAlges));
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
			DtAlgeSet ret = DtAlgeSet.fromCSV(target, UTF8);
			assertTrue(DtAlgeSetTest.equalElementSequence(ret, normalAlges));
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from CSV file \"" + target.getPath() + "\"");
		}
	}

	/**
	 * Test method for {@link dtalge.DtAlgeSet#toXML(java.io.File)}.
	 */
	public void testToXMLFile() {
		File target = toxml;
		DtAlgeSet data = new DtAlgeSet(DtAlgeSetTest.makeDtalges(normalAlges));

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
			DtAlgeSet ret = DtAlgeSet.fromXML(target);
			assertTrue(DtAlgeSetTest.equalElementSequence(ret, normalAlges));
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from XML file \"" + target.getPath() + "\"");
		}
	}

}
