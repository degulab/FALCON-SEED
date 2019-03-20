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
 *  Copyright 2007-2010  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)CsvFileReaderTest.java	1.50	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

/**
 * {@link ssac.aadl.runtime.io.CsvFileReader} クラスのテスト。
 * 
 * @version 1.50	2010/09/27
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * 
 * @since 1.50
 */
public class CsvFileReaderTest extends TestCase
{
	static protected File basedir = new File("testdata/unittest/runtime/csv");
	static protected File fileTest1_SJIS = new File(basedir, "testData1_sjis.csv");
	static protected File fileTest1_UTF8 = new File(basedir, "testData1_utf8.csv");
	static protected File fileTest2_SJIS = new File(basedir, "testData2_sjis.csv");
	static protected File fileTest2_UTF8 = new File(basedir, "testData2_utf8.csv");
	static protected File notexistFile = new File(basedir, "testData0.csv");
	
	static private final String nullString = null;
	
	static protected final List<List<String>> testData = new ArrayList<List<String>>();
	
	static {
		testData.add(Arrays.asList("名前","性別","年齢","都道府県","注目食品","備考"));
		testData.add(Arrays.asList("北川","女性","29","三重県","マンゴーミルクプリン",nullString));
		testData.add(Arrays.asList("上原","男性","35","佐賀県","エコナクッキングオイルＳ","特保"));
		testData.add(Arrays.asList("馬場","男性","41","宮城県","まぐろのたたきご飯",nullString));
		testData.add(Arrays.asList("富田","男性","14","鹿児島県","五穀米ご飯",nullString));
		testData.add(Arrays.asList("藤原","男性","48","長崎県","\"ハンバーグ\"＆\"白身魚フライ\"弁当","このコメントは改行\r\nを含みます"));
		testData.add(Arrays.asList("須藤","女性","20","京都府","チキンと豆腐のハンバーグ弁当",nullString));
		testData.add(Arrays.asList("萩原","女性","25","熊本県","若鶏のグリルガーリックソース",nullString));
		testData.add(Arrays.asList("小沢","女性","45","茨城県","ミックスサラダ",nullString));
		testData.add(Arrays.asList("栗原","男性","65","埼玉県","ピオテアドリンク","特保"));
		testData.add(Arrays.asList("岡本","女性","18","新潟県","コロッケ＆唐揚げ弁当",nullString));
		testData.add(Arrays.asList("宮崎","男性","23","三重県","ハンバーグ＆唐揚げ",nullString));
		testData.add(Arrays.asList("川村","女性","35","青森県","日清おいしさ\tプラス\tサイリウムドリンク\tプレーン(微糖)","特保"));
		testData.add(Arrays.asList("西村","女性","68","岡山県","ハンバーガー",nullString));
		testData.add(Arrays.asList("永田","男性","60","島根県","ハンバーグ＆海老フライ弁当",nullString));
		testData.add(Arrays.asList("三宅","女性","58","宮崎県","鶏の唐揚げ",nullString));
		testData.add(Arrays.asList("市川","女性","60","大阪府","和風大皿盛り合わせ",nullString));
		testData.add(Arrays.asList("大谷","女性","47","北海道","オレンジジュース",nullString));
		testData.add(Arrays.asList("川島","女性","16","青森県","ピュアフローラ,\"青りんご味\"","特保"));
		testData.add(Arrays.asList("福島","男性","27","徳島県","キシリトール・ガム \"ニューアップルミント\"","特保"));
		testData.add(Arrays.asList("関根","女性","67","島根県","オムライス",nullString));
		testData.add(new ArrayList<String>());
		testData.add(Arrays.asList("中野","男性","56","和歌山県","ポテトサラダ","このコメントは改行\r\nを含みます"));
		testData.add(Arrays.asList("中島","女性","48","愛媛県","マンゴーミルクプリン",nullString));
		testData.add(Arrays.asList("菅野","女性","20","京都府","日清おいしさ プラス サイリウムラーメン タンメン","特保"));
		testData.add(Arrays.asList("高田","女性","24","埼玉県","エージーエフビタホットハニーレモンＣ","特保"));
		testData.add(Arrays.asList("内藤","男性","38","茨城県","ソフールＬＴ","特保"));
		testData.add(Arrays.asList("上田","女性","55","秋田県","ポテトサラダ",nullString));
		testData.add(Arrays.asList("石田","女性","22","京都府","チキンステーキ＆クリームコロッケ弁当",nullString));
		testData.add(Arrays.asList(nullString,"女性","42","栃木県","鶏の唐揚げ","このコメントは改行\r\nを含みます"));
		testData.add(Arrays.asList("小西",nullString,"48","鹿児島県","ミルクプリン",nullString));
		testData.add(Arrays.asList("片岡","女性",nullString,"長野県","チキンのサラダ",nullString));
		testData.add(Arrays.asList("平井","男性","60",nullString,"ゴーサインスティック",nullString));
		testData.add(Arrays.asList("渡辺","男性","66","宮崎県",nullString,"特保"));
		testData.add(Arrays.asList("小田","男性","43","沖縄県","２種のケーキ盛合せ",nullString));
		testData.add(Arrays.asList("酒井","男性","54","神奈川県","和風チキン＆白身魚フライ弁当",nullString));
		testData.add(Arrays.asList("阿部","女性","15","茨城県","ひれかつ弁当",nullString));
		testData.add(Arrays.asList(nullString,nullString,nullString,nullString,nullString,"このコメントは改行\r\nを含みます"));
		testData.add(Arrays.asList("吉村","女性","35","愛媛県","グリル盛合わせ",nullString));
		testData.add(Arrays.asList("小野","女性","50","愛媛県","和風大皿盛り合わせ",nullString));
		testData.add(Arrays.asList("松井","男性","22","茨城県","ハンバーガー",nullString));
		testData.add(Arrays.asList("小林","女性","62","長野県","ヨーグリーナ","特保"));
		testData.add(Arrays.asList("林","男性","14","山形県","ハンバーグ＆中華弁当",nullString));
		testData.add(Arrays.asList("石橋","男性","21","熊本県","天の葉","特保"));
		testData.add(Arrays.asList("横山","女性","22","東京都","コレステミン,アセロラ味","特保"));
		testData.add(Arrays.asList("松村","女性","24","徳島県","浅漬け",nullString));
		testData.add(Arrays.asList("五十嵐","女性","13","長野県","日清おいしさプラスサイリウムドリンクオレンジ","特保"));
		testData.add(Arrays.asList("服部","女性","30","愛知県","大豆芽茶","特保"));
		testData.add(Arrays.asList("岡","男性","10","和歌山県","ほね元気","特保"));
		testData.add(Arrays.asList("大森","女性","23","新潟県","メロンソーダ",nullString));
		testData.add(Arrays.asList("矢野","男性","40","北海道","\"ハンバーグ\"＆\"若鶏の唐揚げ\"弁当",nullString));
		testData.add(Arrays.asList("山田","女性","51","埼玉県","リカルデント オーシャンフレッシュ","特保"));
	}
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.io.CsvFileReader#CsvFileReader(java.io.File)}.
	 */
	public void testCsvFileReaderFile() {
		// null
		try {
			new CsvFileReader(null);
			fail("must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			assertTrue(true);
			ex = null;
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by CsvFileReader constructer: " + ex.toString());
		}
		
		// normal
		CsvFileReader tfr = null;
		try {
			tfr = new CsvFileReader(fileTest1_SJIS);
		}
		catch (Throwable ex) {
			fail("Failed to open by CsvFileReader constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
		
		// file not found
		tfr = null;
		try {
			tfr = new CsvFileReader(notexistFile);
			fail("must be throw FileNotFoundException.");
		}
		catch (FileNotFoundException ex) {
			System.out.println("Throw FileNotFoundException by CsvFileReader constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.io.CsvFileReader#CsvFileReader(java.io.File, java.lang.String)}.
	 */
	public void testCsvFileReaderFileString() {
		// null
		try {
			new CsvFileReader(null, "MS932");
			fail("must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			assertTrue(true);
			ex = null;
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by CsvFileReader constructer: " + ex.toString());
		}
		catch (UnsupportedEncodingException ex) {
			fail("Failed to open by CsvFileReader constructer: " + ex.toString());
		}
		try {
			new CsvFileReader(fileTest1_UTF8, null);
			fail("must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			assertTrue(true);
			ex = null;
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by CsvFileReader constructer: " + ex.toString());
		}
		catch (UnsupportedEncodingException ex) {
			fail("Failed to open by CsvFileReader constructer: " + ex.toString());
		}
		
		// normal
		CsvFileReader tfr = null;
		try {
			tfr = new CsvFileReader(fileTest1_UTF8, "UTF-8");
		}
		catch (Throwable ex) {
			fail("Failed to open by CsvFileReader constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
		
		// file not found
		tfr = null;
		try {
			tfr = new CsvFileReader(notexistFile, "UTF-8");
			fail("must be throw FileNotFoundException.");
		}
		catch (FileNotFoundException ex) {
			System.out.println("Throw FileNotFoundException by CsvFileReader constructer: " + ex.toString());
		}
		catch (UnsupportedEncodingException ex) {
			fail("Failed to open by CsvFileReader constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
		
		// unsupported encoding
		tfr = null;
		try {
			tfr = new CsvFileReader(fileTest1_UTF8, "hoge");
			fail("must be throw UnsupportedEncodingException.");
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by CsvFileReader constructer: " + ex.toString());
		}
		catch (UnsupportedEncodingException ex) {
			System.out.println("Throw UnsupportedEncodingException by CsvFileReader constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.io.internal.AbTextFileReader#getFile()}.
	 */
	public void testGetFile() {
		CsvFileReader tfr = null;
		try {
			tfr = new CsvFileReader(fileTest1_SJIS);
			assertEquals(fileTest1_SJIS, tfr.getFile());
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by CsvFileReader constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.io.internal.AbTextFileReader#getEncoding()}.
	 */
	public void testGetEncoding() {
		CsvFileReader tfr = null;
		try {
			tfr = new CsvFileReader(fileTest1_SJIS);
			assertNull(tfr.getEncoding());
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by CsvFileReader constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
		
		tfr = null;
		try {
			tfr = new CsvFileReader(fileTest1_UTF8, "UTF-8");
			assertEquals("UTF-8", tfr.getEncoding());
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by CsvFileReader constructer: " + ex.toString());
		}
		catch (UnsupportedEncodingException ex) {
			fail("Failed to open by CsvFileReader constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.io.internal.AbTextFileReader#isOpen()}.
	 */
	public void testIsOpen() {
		CsvFileReader tfr = null;
		try {
			tfr = new CsvFileReader(fileTest1_SJIS);
			assertTrue(tfr.isOpen());
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by CsvFileReader constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
		
		assertFalse(tfr.isOpen());
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.io.internal.AbTextFileReader#close()}.
	 */
	public void testClose() {
		CsvFileReader tfr = null;
		try {
			tfr = new CsvFileReader(fileTest1_SJIS);
			assertTrue(tfr.isOpen());
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by CsvFileReader constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
		assertFalse(tfr.isOpen());
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.io.internal.AbTextFileReader#iterator()},
	 * {@link ssac.aadl.runtime.io.internal.AbTextFileReader#iterator()},
	 * {@link ssac.aadl.runtime.io.internal.AbTextFileReader#hasNext()},
	 * {@link ssac.aadl.runtime.io.internal.AbTextFileReader#next()}.
	 */
	public void testIteration() {
		CsvFileReader tfr;
		
		// testData
		//--- SJIS
		tfr = null;
		try {
			tfr = new CsvFileReader(fileTest1_SJIS);
			ArrayList<List<String>> list = new ArrayList<List<String>>();
			Iterator<List<String>> it = tfr.iterator();
			while (it.hasNext()) {
				list.add(it.next());
			}
			assertEquals(testData, list);
			//assertEquals(testData.size(), list.size());
			//int len = testData.size();
			//for (int i = 0; i < len; i++) {
			//	List<String> org = testData.get(i);
			//	List<String> rec = list.get(i);
			//	if (!org.equals(rec)) {
			//		System.err.println("Not equals \"" + tfr.getFile().getPath() + "\" : index=" + i + "\n"
			//						+ "<org>:" + org.toString() + "\n"
			//						+ "<rec>:" + rec.toString() + "\n");
			//	}
			//}
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by CsvFileReader constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
		tfr = null;
		try {
			tfr = new CsvFileReader(fileTest2_SJIS);
			ArrayList<List<String>> list = new ArrayList<List<String>>();
			for (List<String> rec : tfr) {
				list.add(rec);
			}
			assertEquals(testData, list);
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by CsvFileReader constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
		//--- UTF-8
		tfr = null;
		try {
			tfr = new CsvFileReader(fileTest1_UTF8, "UTF-8");
			ArrayList<List<String>> list = new ArrayList<List<String>>();
			Iterator<List<String>> it = tfr.iterator();
			while (it.hasNext()) {
				list.add(it.next());
			}
			assertEquals(testData, list);
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by CsvFileReader constructer: " + ex.toString());
		}
		catch (UnsupportedEncodingException ex) {
			fail("Failed to open by CsvFileReader constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
		tfr = null;
		try {
			tfr = new CsvFileReader(fileTest2_UTF8, "UTF-8");
			ArrayList<List<String>> list = new ArrayList<List<String>>();
			for (List<String> rec : tfr) {
				list.add(rec);
			}
			assertEquals(testData, list);
		}
		catch (FileNotFoundException ex) {
			fail("Failed to open by CsvFileReader constructer: " + ex.toString());
		}
		catch (UnsupportedEncodingException ex) {
			fail("Failed to open by CsvFileReader constructer: " + ex.toString());
		}
		finally {
			if (tfr != null) {
				tfr.close();
			}
		}
	}
}
