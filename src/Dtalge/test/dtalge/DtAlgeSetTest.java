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
 * @(#)DtAlgeSetTest.java	0.40	2012/04/20
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtAlgeSetTest.java	0.30	2011/03/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtAlgeSetTest.java	0.20	2010/02/22
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtAlgeSetTest.java	0.10	2008/09/02
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;
import dtalge.util.DtDataTypes;

/**
 * <code>DtAlgeSet</code> のユニットテスト。
 * このテストケースでは、ファイル入出力のテストは含まない。
 * 
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class DtAlgeSetTest extends TestCase
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final DtBase[] testBases = {
		new DtBase("名前","string","#","#"),
		new DtBase("性別","string","#","#"),
		//new DtBase("年齢","integer","#","#"),
		new DtBase("年齢","decimal","#","#"),
		new DtBase("都道府県","string","#","#"),
		new DtBase("注目食品","string","#","#"),
		new DtBase("ポイント","decimal","#","#"),
		new DtBase("フラグ","boolean","#","#"),
		new DtBase("備考","string","#","#"),
	};
	
	static private final DtBase[] testBases2 = {
		new DtBase("名前","string","#","#"),
		new DtBase("性別","string","#","#"),
		//new DtBase("年齢","integer","#","#"),
		new DtBase("年齢","decimal","#","#"),
		new DtBase("都道府県","string","#","#"),
		new DtBase("注目食品","string","#","#"),
		new DtBase("ポイント","decimal","#","#"),
		new DtBase("フラグ","boolean","#","#"),
		new DtBase("備考","string","#","#"),
		new DtBase("hoge1","string","#","#"),
		new DtBase("hoge2","decimal","#","#"),
		new DtBase("hoge3","boolean","#","#"),
	};
	
	static private final Object[] emptyAlge = {};
	static private final Object[][] testEmptyAlges = {
		{},
	};

	static private final Object[] selectTestData = {
		new DtBase("名前","string","#","#"), "萩原",
		new DtBase("性別","string","#","#"), "女性",
		new DtBase("年齢","decimal","#","#"), new BigDecimal("25"),
		new DtBase("都道府県","string","#","#"), "熊本県",
		new DtBase("注目食品","string","#","#"), "若鶏のグリルガーリックソース",
		new DtBase("ポイント","decimal","#","#"), new BigDecimal("987654321987654321.01234567890123456"),
		new DtBase("フラグ","boolean","#","#"), null,
		new DtBase("備考","string","#","#"), null,
		new DtBase("n名前","string","#","#"), "萩原",
		new DtBase("n性別","string","#","#"), "女性",
		new DtBase("n年齢","decimal","#","#"), new BigDecimal("25"),
		new DtBase("n都道府県","string","#","#"), "熊本県",
		new DtBase("n注目食品","string","#","#"), "若鶏のグリルガーリックソース",
		new DtBase("nポイント","decimal","#","#"), new BigDecimal("987654321987654321.01234567890123456"),
		new DtBase("nフラグ","boolean","#","#"), null,
		new DtBase("n備考","string","#","#"), null,
	};
	
	static private final Object[][] normalAlges = {
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
	
	static private final Object[][] testAlges1 = {
		normalAlges[0],
		normalAlges[1],
		normalAlges[2],
		normalAlges[3],
		normalAlges[4],
		normalAlges[5],
	};
	static private final Object[][] testAlges2 = {
		normalAlges[6],
		normalAlges[7],
		normalAlges[8],
	};
	static private final Object[][] testAlges3 = {
		normalAlges[9],
		normalAlges[10],
		normalAlges[11],
		normalAlges[12],
		normalAlges[13],
		normalAlges[14],
		normalAlges[15],
	};
	
	static private final Object[][] testWithEmptyAlges1 = {
		normalAlges[0],
		{},
		normalAlges[1],
		normalAlges[2],
		{},
		normalAlges[3],
		normalAlges[4],
		{},{},
		normalAlges[5],
	};
	static private final Object[][] testWithEmptyAlges2 = {
		normalAlges[6],
		normalAlges[7],
		{},{},
		normalAlges[8],
	};
	static private final Object[][] testWithEmptyAlges3 = {
		{},
		normalAlges[9],
		normalAlges[10],
		normalAlges[11],
		{},{},
		normalAlges[12],
		normalAlges[13],
		normalAlges[14],
		{},
		normalAlges[15],
	};
	
	static private final Object[][] testEmptyAlges1 = {
		{},
		{},
		{},{},
	};
	static private final Object[][] testEmptyAlges2 = {
		{},{},
	};
	static private final Object[][] testEmptyAlges3 = {
		{},
		{},{},
		{},
	};
	
	static private final Object[][] withEmptyNormalAlges = {
		normalAlges[0],
		{},
		normalAlges[1],
		normalAlges[2],
		{},
		normalAlges[3],
		normalAlges[4],
		{},{},
		normalAlges[5],
		normalAlges[6],
		normalAlges[7],
		{},{},
		normalAlges[8],
		{},
		normalAlges[9],
		normalAlges[10],
		normalAlges[11],
		{},{},
		normalAlges[12],
		normalAlges[13],
		normalAlges[14],
		{},
		normalAlges[15],
	};
	
	static private final Object[][] withNullNormalAlges = {
		normalAlges[0],
		null,
		normalAlges[1],
		normalAlges[2],
		normalAlges[3],
		null,
		null,
		normalAlges[4],
		normalAlges[5],
		normalAlges[6],
		normalAlges[7],
		normalAlges[8],
		null,
		normalAlges[9],
		null,
		normalAlges[10],
		normalAlges[11],
		normalAlges[12],
		null,
		normalAlges[13],
		normalAlges[14],
		normalAlges[15],
	};
	
	static private final String[] thesMPPairs1 = new String[]{
		"P1", "P2",
		"P1", "P3",
		"P2", "P4",
		"P2", "P5",
		"P4", "P6",
		"P5", "P6",
		"P3", "P7",
		"P6", "P7",
	};
	
	static private final String[] thesMPPairs2 = new String[]{
		"N1", "N2",
		"N1", "N3",
		"N2", "N4",
		"N2", "N5",
		"N4", "N6",
		"N5", "N6",
		"N3", "N7",
		"N6", "N7",
	};
	
	static private final Object[][] thesAlges1 = new Object[][]{
		{
			new DtBase("Type", DtDataTypes.STRING), "A",
			new DtBase("Task", DtDataTypes.STRING), "P1",
			new DtBase("Flag", DtDataTypes.BOOLEAN), Boolean.FALSE,
			new DtBase("Value", DtDataTypes.DECIMAL), new BigDecimal("1"),
		},
		{
			new DtBase("Type", DtDataTypes.STRING), "B",
			new DtBase("Task", DtDataTypes.STRING), "P2",
			new DtBase("Flag", DtDataTypes.BOOLEAN), Boolean.FALSE,
			new DtBase("Value", DtDataTypes.DECIMAL), new BigDecimal("2"),
		},
		{
			new DtBase("Type", DtDataTypes.STRING), "C",
			new DtBase("Task", DtDataTypes.STRING), "P3",
			new DtBase("Flag", DtDataTypes.BOOLEAN), Boolean.FALSE,
			new DtBase("Value", DtDataTypes.DECIMAL), new BigDecimal("3"),
		},
		{
			new DtBase("Type", DtDataTypes.STRING), "D",
			new DtBase("Task", DtDataTypes.STRING), "P4",
			new DtBase("Flag", DtDataTypes.BOOLEAN), Boolean.FALSE,
			new DtBase("Value", DtDataTypes.DECIMAL), new BigDecimal("4"),
		},
		{
			new DtBase("Type", DtDataTypes.STRING), "E",
			new DtBase("Task", DtDataTypes.STRING), "P5",
			new DtBase("Flag", DtDataTypes.BOOLEAN), Boolean.FALSE,
			new DtBase("Value", DtDataTypes.DECIMAL), new BigDecimal("5"),
		},
		{
			new DtBase("Type", DtDataTypes.STRING), "F",
			new DtBase("Task", DtDataTypes.STRING), "P6",
			new DtBase("Flag", DtDataTypes.BOOLEAN), Boolean.FALSE,
			new DtBase("Value", DtDataTypes.DECIMAL), new BigDecimal("6"),
		},
		{
			new DtBase("Type", DtDataTypes.STRING), "G",
			new DtBase("Task", DtDataTypes.STRING), "P7",
			new DtBase("Flag", DtDataTypes.BOOLEAN), Boolean.FALSE,
			new DtBase("Value", DtDataTypes.DECIMAL), new BigDecimal("7"),
		},
	};
	
	static private final Object[][] thesSelectMax1 = new Object[][]{
		{
			new DtBase("Type", DtDataTypes.STRING), "A",
			new DtBase("Task", DtDataTypes.STRING), "P1",
			new DtBase("Flag", DtDataTypes.BOOLEAN), Boolean.FALSE,
			new DtBase("Value", DtDataTypes.DECIMAL), new BigDecimal("1"),
		},
	};
	
	static private final Object[][] thesSelectMin1 = new Object[][]{
		{
			new DtBase("Type", DtDataTypes.STRING), "G",
			new DtBase("Task", DtDataTypes.STRING), "P7",
			new DtBase("Flag", DtDataTypes.BOOLEAN), Boolean.FALSE,
			new DtBase("Value", DtDataTypes.DECIMAL), new BigDecimal("7"),
		},
	};
	
	static private final Object[][] thesAlges2 = new Object[][]{
		{
			new DtBase("Type", DtDataTypes.STRING), "B",
			new DtBase("Task", DtDataTypes.STRING), "P2",
			new DtBase("Flag", DtDataTypes.BOOLEAN), Boolean.FALSE,
			new DtBase("Value", DtDataTypes.DECIMAL), new BigDecimal("2"),
		},
		{
			new DtBase("Type", DtDataTypes.STRING), "C",
			new DtBase("Task", DtDataTypes.STRING), "P3",
			new DtBase("Flag", DtDataTypes.BOOLEAN), Boolean.FALSE,
			new DtBase("Value", DtDataTypes.DECIMAL), new BigDecimal("3"),
		},
		{
			new DtBase("Type", DtDataTypes.STRING), "D",
			new DtBase("Task", DtDataTypes.STRING), "P4",
			new DtBase("Flag", DtDataTypes.BOOLEAN), Boolean.FALSE,
			new DtBase("Value", DtDataTypes.DECIMAL), new BigDecimal("4"),
		},
		{
			new DtBase("Type", DtDataTypes.STRING), "E",
			new DtBase("Task", DtDataTypes.STRING), "P5",
			new DtBase("Flag", DtDataTypes.BOOLEAN), Boolean.FALSE,
			new DtBase("Value", DtDataTypes.DECIMAL), new BigDecimal("5"),
		},
		{
			new DtBase("Type", DtDataTypes.STRING), "F",
			new DtBase("Task", DtDataTypes.STRING), "P6",
			new DtBase("Flag", DtDataTypes.BOOLEAN), Boolean.FALSE,
			new DtBase("Value", DtDataTypes.DECIMAL), new BigDecimal("6"),
		},
		{
			new DtBase("Type", DtDataTypes.STRING), "AB",
			new DtBase("Task", DtDataTypes.STRING), "P2",
			new DtBase("Flag", DtDataTypes.BOOLEAN), Boolean.TRUE,
			new DtBase("Value", DtDataTypes.DECIMAL), new BigDecimal("12"),
		},
		{
			new DtBase("Type", DtDataTypes.STRING), "AD",
			new DtBase("Task", DtDataTypes.STRING), "P4",
			new DtBase("Flag", DtDataTypes.BOOLEAN), Boolean.TRUE,
			new DtBase("Value", DtDataTypes.DECIMAL), new BigDecimal("14"),
		},
		{
			new DtBase("Type", DtDataTypes.STRING), "AF",
			new DtBase("Task", DtDataTypes.STRING), "P6",
			new DtBase("Flag", DtDataTypes.BOOLEAN), Boolean.TRUE,
			new DtBase("Value", DtDataTypes.DECIMAL), new BigDecimal("16"),
		},
	};
	
	static private final Object[][] thesSelectMax2 = new Object[][]{
		{
			new DtBase("Type", DtDataTypes.STRING), "B",
			new DtBase("Task", DtDataTypes.STRING), "P2",
			new DtBase("Flag", DtDataTypes.BOOLEAN), Boolean.FALSE,
			new DtBase("Value", DtDataTypes.DECIMAL), new BigDecimal("2"),
		},
		{
			new DtBase("Type", DtDataTypes.STRING), "C",
			new DtBase("Task", DtDataTypes.STRING), "P3",
			new DtBase("Flag", DtDataTypes.BOOLEAN), Boolean.FALSE,
			new DtBase("Value", DtDataTypes.DECIMAL), new BigDecimal("3"),
		},
		{
			new DtBase("Type", DtDataTypes.STRING), "AB",
			new DtBase("Task", DtDataTypes.STRING), "P2",
			new DtBase("Flag", DtDataTypes.BOOLEAN), Boolean.TRUE,
			new DtBase("Value", DtDataTypes.DECIMAL), new BigDecimal("12"),
		},
	};
	
	static private final Object[][] thesSelectMin2 = new Object[][]{
		{
			new DtBase("Type", DtDataTypes.STRING), "C",
			new DtBase("Task", DtDataTypes.STRING), "P3",
			new DtBase("Flag", DtDataTypes.BOOLEAN), Boolean.FALSE,
			new DtBase("Value", DtDataTypes.DECIMAL), new BigDecimal("3"),
		},
		{
			new DtBase("Type", DtDataTypes.STRING), "F",
			new DtBase("Task", DtDataTypes.STRING), "P6",
			new DtBase("Flag", DtDataTypes.BOOLEAN), Boolean.FALSE,
			new DtBase("Value", DtDataTypes.DECIMAL), new BigDecimal("6"),
		},
		{
			new DtBase("Type", DtDataTypes.STRING), "AF",
			new DtBase("Task", DtDataTypes.STRING), "P6",
			new DtBase("Flag", DtDataTypes.BOOLEAN), Boolean.TRUE,
			new DtBase("Value", DtDataTypes.DECIMAL), new BigDecimal("16"),
		},
	};
	
	static private final Object[][] thesAlges3 = new Object[][]{
		{
			new DtBase("Type", DtDataTypes.STRING), "D",
			new DtBase("Task", DtDataTypes.STRING), "P4",
			new DtBase("Flag", DtDataTypes.BOOLEAN), Boolean.FALSE,
			new DtBase("Value", DtDataTypes.DECIMAL), new BigDecimal("4"),
		},
		{
			new DtBase("Type", DtDataTypes.STRING), "AD",
			new DtBase("Task", DtDataTypes.STRING), "P4",
			new DtBase("Flag", DtDataTypes.BOOLEAN), Boolean.TRUE,
			new DtBase("Value", DtDataTypes.DECIMAL), new BigDecimal("14"),
		},
		{
			new DtBase("Type", DtDataTypes.STRING), "E",
			new DtBase("Task", DtDataTypes.STRING), "P5",
			new DtBase("Flag", DtDataTypes.BOOLEAN), Boolean.FALSE,
			new DtBase("Value", DtDataTypes.DECIMAL), new BigDecimal("5"),
		},
		{
			new DtBase("Type", DtDataTypes.STRING), "F",
			new DtBase("Task", DtDataTypes.STRING), "P6",
			new DtBase("Flag", DtDataTypes.BOOLEAN), Boolean.FALSE,
			new DtBase("Value", DtDataTypes.DECIMAL), new BigDecimal("6"),
		},
		{
			new DtBase("Type", DtDataTypes.STRING), "AF",
			new DtBase("Task", DtDataTypes.STRING), "P6",
			new DtBase("Flag", DtDataTypes.BOOLEAN), Boolean.TRUE,
			new DtBase("Value", DtDataTypes.DECIMAL), new BigDecimal("16"),
		},
		{
			new DtBase("Type", DtDataTypes.STRING), "AC",
			new DtBase("Task", DtDataTypes.STRING), "P3",
			new DtBase("Flag", DtDataTypes.BOOLEAN), Boolean.TRUE,
			new DtBase("Value", DtDataTypes.DECIMAL), new BigDecimal("13"),
		},
	};
	
	static private final Object[][] thesSelectMax3 = new Object[][]{
		{
			new DtBase("Type", DtDataTypes.STRING), "D",
			new DtBase("Task", DtDataTypes.STRING), "P4",
			new DtBase("Flag", DtDataTypes.BOOLEAN), Boolean.FALSE,
			new DtBase("Value", DtDataTypes.DECIMAL), new BigDecimal("4"),
		},
		{
			new DtBase("Type", DtDataTypes.STRING), "AD",
			new DtBase("Task", DtDataTypes.STRING), "P4",
			new DtBase("Flag", DtDataTypes.BOOLEAN), Boolean.TRUE,
			new DtBase("Value", DtDataTypes.DECIMAL), new BigDecimal("14"),
		},
		{
			new DtBase("Type", DtDataTypes.STRING), "E",
			new DtBase("Task", DtDataTypes.STRING), "P5",
			new DtBase("Flag", DtDataTypes.BOOLEAN), Boolean.FALSE,
			new DtBase("Value", DtDataTypes.DECIMAL), new BigDecimal("5"),
		},
		{
			new DtBase("Type", DtDataTypes.STRING), "AC",
			new DtBase("Task", DtDataTypes.STRING), "P3",
			new DtBase("Flag", DtDataTypes.BOOLEAN), Boolean.TRUE,
			new DtBase("Value", DtDataTypes.DECIMAL), new BigDecimal("13"),
		},
	};
	
	static private final Object[][] thesSelectMin3 = new Object[][]{
		{
			new DtBase("Type", DtDataTypes.STRING), "F",
			new DtBase("Task", DtDataTypes.STRING), "P6",
			new DtBase("Flag", DtDataTypes.BOOLEAN), Boolean.FALSE,
			new DtBase("Value", DtDataTypes.DECIMAL), new BigDecimal("6"),
		},
		{
			new DtBase("Type", DtDataTypes.STRING), "AF",
			new DtBase("Task", DtDataTypes.STRING), "P6",
			new DtBase("Flag", DtDataTypes.BOOLEAN), Boolean.TRUE,
			new DtBase("Value", DtDataTypes.DECIMAL), new BigDecimal("16"),
		},
		{
			new DtBase("Type", DtDataTypes.STRING), "AC",
			new DtBase("Task", DtDataTypes.STRING), "P3",
			new DtBase("Flag", DtDataTypes.BOOLEAN), Boolean.TRUE,
			new DtBase("Value", DtDataTypes.DECIMAL), new BigDecimal("13"),
		},
	};
	
	static private final Object[][] sortTestAlges = new Object[][]{
		{
			new DtBase("string", DtDataTypes.STRING), "B",
			new DtBase("decimal", DtDataTypes.DECIMAL), null,
			new DtBase("boolean", DtDataTypes.BOOLEAN), null,
		},
		{
			new DtBase("string", DtDataTypes.STRING), "C",
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("0.110"),
		},
		{
			new DtBase("string", DtDataTypes.STRING), "B",
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("0.10"),
			new DtBase("boolean", DtDataTypes.BOOLEAN), null,
		},
		{
			new DtBase("string", DtDataTypes.STRING), "C",
		},
		{
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.TRUE,
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("0.1"),
		},
		{
			new DtBase("string", DtDataTypes.STRING), "A",
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.FALSE,
		},
		{
			new DtBase("string", DtDataTypes.STRING), null,
			new DtBase("decimal", DtDataTypes.DECIMAL), null,
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.TRUE,
		},
		{
			new DtBase("string", DtDataTypes.STRING), "A",
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("-0.1"),
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.FALSE,
		},
		{
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.TRUE,
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("-0.10"),
		},
		{
			new DtBase("string", DtDataTypes.STRING), null,
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("0.11"),
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.TRUE,
		},
	};
	
	static private final Object[][] sortedAnsAlgesStringAsc = new Object[][]{
		{
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.TRUE,
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("0.1"),
		},
		{
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.TRUE,
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("-0.10"),
		},
		{
			new DtBase("string", DtDataTypes.STRING), null,
			new DtBase("decimal", DtDataTypes.DECIMAL), null,
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.TRUE,
		},
		{
			new DtBase("string", DtDataTypes.STRING), null,
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("0.11"),
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.TRUE,
		},
		{
			new DtBase("string", DtDataTypes.STRING), "A",
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.FALSE,
		},
		{
			new DtBase("string", DtDataTypes.STRING), "A",
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("-0.1"),
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.FALSE,
		},
		{
			new DtBase("string", DtDataTypes.STRING), "B",
			new DtBase("decimal", DtDataTypes.DECIMAL), null,
			new DtBase("boolean", DtDataTypes.BOOLEAN), null,
		},
		{
			new DtBase("string", DtDataTypes.STRING), "B",
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("0.10"),
			new DtBase("boolean", DtDataTypes.BOOLEAN), null,
		},
		{
			new DtBase("string", DtDataTypes.STRING), "C",
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("0.110"),
		},
		{
			new DtBase("string", DtDataTypes.STRING), "C",
		},
	};
	
	static private final Object[][] sortedAnsAlgesStringDsc = new Object[][]{
		{
			new DtBase("string", DtDataTypes.STRING), "C",
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("0.110"),
		},
		{
			new DtBase("string", DtDataTypes.STRING), "C",
		},
		{
			new DtBase("string", DtDataTypes.STRING), "B",
			new DtBase("decimal", DtDataTypes.DECIMAL), null,
			new DtBase("boolean", DtDataTypes.BOOLEAN), null,
		},
		{
			new DtBase("string", DtDataTypes.STRING), "B",
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("0.10"),
			new DtBase("boolean", DtDataTypes.BOOLEAN), null,
		},
		{
			new DtBase("string", DtDataTypes.STRING), "A",
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.FALSE,
		},
		{
			new DtBase("string", DtDataTypes.STRING), "A",
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("-0.1"),
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.FALSE,
		},
		{
			new DtBase("string", DtDataTypes.STRING), null,
			new DtBase("decimal", DtDataTypes.DECIMAL), null,
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.TRUE,
		},
		{
			new DtBase("string", DtDataTypes.STRING), null,
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("0.11"),
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.TRUE,
		},
		{
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.TRUE,
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("0.1"),
		},
		{
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.TRUE,
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("-0.10"),
		},
	};
	
	static private final Object[][] sortedAnsAlgesDecimalAsc = new Object[][]{
		{
			new DtBase("string", DtDataTypes.STRING), "C",
		},
		{
			new DtBase("string", DtDataTypes.STRING), "A",
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.FALSE,
		},
		{
			new DtBase("string", DtDataTypes.STRING), "B",
			new DtBase("decimal", DtDataTypes.DECIMAL), null,
			new DtBase("boolean", DtDataTypes.BOOLEAN), null,
		},
		{
			new DtBase("string", DtDataTypes.STRING), null,
			new DtBase("decimal", DtDataTypes.DECIMAL), null,
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.TRUE,
		},
		{
			new DtBase("string", DtDataTypes.STRING), "A",
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("-0.1"),
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.FALSE,
		},
		{
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.TRUE,
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("-0.10"),
		},
		{
			new DtBase("string", DtDataTypes.STRING), "B",
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("0.10"),
			new DtBase("boolean", DtDataTypes.BOOLEAN), null,
		},
		{
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.TRUE,
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("0.1"),
		},
		{
			new DtBase("string", DtDataTypes.STRING), "C",
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("0.110"),
		},
		{
			new DtBase("string", DtDataTypes.STRING), null,
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("0.11"),
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.TRUE,
		},
	};
	
	static private final Object[][] sortedAnsAlgesDecimalDsc = new Object[][]{
		{
			new DtBase("string", DtDataTypes.STRING), "C",
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("0.110"),
		},
		{
			new DtBase("string", DtDataTypes.STRING), null,
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("0.11"),
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.TRUE,
		},
		{
			new DtBase("string", DtDataTypes.STRING), "B",
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("0.10"),
			new DtBase("boolean", DtDataTypes.BOOLEAN), null,
		},
		{
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.TRUE,
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("0.1"),
		},
		{
			new DtBase("string", DtDataTypes.STRING), "A",
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("-0.1"),
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.FALSE,
		},
		{
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.TRUE,
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("-0.10"),
		},
		{
			new DtBase("string", DtDataTypes.STRING), "B",
			new DtBase("decimal", DtDataTypes.DECIMAL), null,
			new DtBase("boolean", DtDataTypes.BOOLEAN), null,
		},
		{
			new DtBase("string", DtDataTypes.STRING), null,
			new DtBase("decimal", DtDataTypes.DECIMAL), null,
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.TRUE,
		},
		{
			new DtBase("string", DtDataTypes.STRING), "C",
		},
		{
			new DtBase("string", DtDataTypes.STRING), "A",
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.FALSE,
		},
	};
	
	static private final Object[][] sortedAnsAlgesBooleanAsc = new Object[][]{
		{
			new DtBase("string", DtDataTypes.STRING), "C",
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("0.110"),
		},
		{
			new DtBase("string", DtDataTypes.STRING), "C",
		},
		{
			new DtBase("string", DtDataTypes.STRING), "B",
			new DtBase("decimal", DtDataTypes.DECIMAL), null,
			new DtBase("boolean", DtDataTypes.BOOLEAN), null,
		},
		{
			new DtBase("string", DtDataTypes.STRING), "B",
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("0.10"),
			new DtBase("boolean", DtDataTypes.BOOLEAN), null,
		},
		{
			new DtBase("string", DtDataTypes.STRING), "A",
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.FALSE,
		},
		{
			new DtBase("string", DtDataTypes.STRING), "A",
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("-0.1"),
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.FALSE,
		},
		{
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.TRUE,
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("0.1"),
		},
		{
			new DtBase("string", DtDataTypes.STRING), null,
			new DtBase("decimal", DtDataTypes.DECIMAL), null,
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.TRUE,
		},
		{
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.TRUE,
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("-0.10"),
		},
		{
			new DtBase("string", DtDataTypes.STRING), null,
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("0.11"),
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.TRUE,
		},
	};
	
	static private final Object[][] sortedAnsAlgesBooleanDsc = new Object[][]{
		{
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.TRUE,
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("0.1"),
		},
		{
			new DtBase("string", DtDataTypes.STRING), null,
			new DtBase("decimal", DtDataTypes.DECIMAL), null,
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.TRUE,
		},
		{
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.TRUE,
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("-0.10"),
		},
		{
			new DtBase("string", DtDataTypes.STRING), null,
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("0.11"),
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.TRUE,
		},
		{
			new DtBase("string", DtDataTypes.STRING), "A",
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.FALSE,
		},
		{
			new DtBase("string", DtDataTypes.STRING), "A",
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("-0.1"),
			new DtBase("boolean", DtDataTypes.BOOLEAN), Boolean.FALSE,
		},
		{
			new DtBase("string", DtDataTypes.STRING), "B",
			new DtBase("decimal", DtDataTypes.DECIMAL), null,
			new DtBase("boolean", DtDataTypes.BOOLEAN), null,
		},
		{
			new DtBase("string", DtDataTypes.STRING), "B",
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("0.10"),
			new DtBase("boolean", DtDataTypes.BOOLEAN), null,
		},
		{
			new DtBase("string", DtDataTypes.STRING), "C",
			new DtBase("decimal", DtDataTypes.DECIMAL), new BigDecimal("0.110"),
		},
		{
			new DtBase("string", DtDataTypes.STRING), "C",
		},
	};

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static protected List<Object> getElementValues(DtAlgeSet algeset, DtBase base) {
		List<Object> list = new ArrayList<Object>();
		for (Dtalge alge : algeset) {
			if (alge.containsBase(base)) {
				list.add(alge.get(base));
			}
		}
		return list;
	}
	
	static protected List<String> getStringValues(Object[][] algeElements, DtBase base) {
		List<String> list = new ArrayList<String>();
		if (DtDataTypes.STRING.equals(base.getTypeKey())) {
			for (int i = 0; i < algeElements.length; i++) {
				for (int j = 0; j < algeElements[i].length; j+=2) {
					DtBase elemBase = (DtBase)algeElements[i][j];
					if (base.equals(elemBase)) {
						String elemVal  = (String)algeElements[i][j+1];
						list.add(elemVal);
					}
				}
			}
		}
		return list;
	}
	
	static protected List<BigDecimal> getDecimalValues(Object[][] algeElements, DtBase base) {
		List<BigDecimal> list = new ArrayList<BigDecimal>();
		if (DtDataTypes.DECIMAL.equals(base.getTypeKey())) {
			for (int i = 0; i < algeElements.length; i++) {
				for (int j = 0; j < algeElements[i].length; j+=2) {
					DtBase elemBase = (DtBase)algeElements[i][j];
					if (base.equals(elemBase)) {
						BigDecimal elemVal  = (BigDecimal)algeElements[i][j+1];
						list.add(elemVal);
					}
				}
			}
		}
		return list;
	}
	
	static protected List<Boolean> getBooleanValues(Object[][] algeElements, DtBase base) {
		List<Boolean> list = new ArrayList<Boolean>();
		if (DtDataTypes.BOOLEAN.equals(base.getTypeKey())) {
			for (int i = 0; i < algeElements.length; i++) {
				for (int j = 0; j < algeElements[i].length; j+=2) {
					DtBase elemBase = (DtBase)algeElements[i][j];
					if (base.equals(elemBase)) {
						Boolean elemVal  = (Boolean)algeElements[i][j+1];
						list.add(elemVal);
					}
				}
			}
		}
		return list;
	}
	
	static protected Object[][] filterElements(Object[][] algeElements, Object...values) {
		ArrayList<Object> list = new ArrayList<Object>();
		for (int i = 0; i < algeElements.length; i++) {
			Object[] alge = DtalgeTest.filterAlgeValue(algeElements[i], values);
			if (alge.length > 0) {
				list.add(alge);
			}
		}
		
		Object[][] ret;
		if (list.isEmpty()) {
			ret = new Object[0][];
		} else {
			ret = new Object[list.size()][];
			for (int i = 0; i < list.size(); i++) {
				ret[i] = (Object[])list.get(i);
			}
		}
		return ret;
	}
	
	static protected Object[][] removeElements(Object[][] algeElements, Object...values) {
		ArrayList<Object> list = new ArrayList<Object>();
		for (int i = 0; i < algeElements.length; i++) {
			Object[] alge = DtalgeTest.removeAlgeValue(algeElements[i], values);
			if (alge.length > 0) {
				list.add(alge);
			}
		}
		
		Object[][] ret;
		if (list.isEmpty()) {
			ret = new Object[0][];
		} else {
			ret = new Object[list.size()][];
			for (int i = 0; i < list.size(); i++) {
				ret[i] = (Object[])list.get(i);
			}
		}
		return ret;
	}
	
	static protected Object[][] catElements(Object[][] algeElements, Object[]...elements) {
		int newSize = 0;
		if (algeElements != null && algeElements.length > 0) {
			newSize = algeElements.length;
		}
		if (elements != null && elements.length > 0) {
			newSize += elements.length;
		}
		
		Object[][] newElements = new Object[newSize][];
		int index = 0;
		if (algeElements != null && algeElements.length > 0) {
			for (; index < algeElements.length; index++) {
				newElements[index] = algeElements[index];
			}
		}
		if (elements != null && elements.length > 0) {
			for (int i = 0; i < elements.length; i++) {
				newElements[index+i] = elements[i];
			}
		}
		
		return newElements;
	}
	
	static protected Object[][] catAlges(Object[][] algeElements, Object[][]...elements) {
		int newSize = 0;
		if (algeElements != null && algeElements.length > 0) {
			newSize = algeElements.length;
		}
		if (elements != null && elements.length > 0) {
			for (Object[][] elem : elements) {
				if (elem != null && elem.length > 0) {
					newSize += elem.length;
				}
			}
		}
		
		Object[][] newElements = new Object[newSize][];
		int index = 0;
		if (algeElements != null && algeElements.length > 0) {
			for (; index < algeElements.length; index++) {
				newElements[index] = algeElements[index];
			}
		}
		if (elements != null && elements.length > 0) {
			for (Object[][] elem : elements) {
				if (elem != null && elements.length > 0) {
					for (Object[] ee : elem) {
						newElements[index++] = ee;
					}
				}
			}
		}
		
		return newElements;
	}
	
	static protected Collection<Dtalge> makeDtalges(Object[]...algeElements) {
		ArrayList<Dtalge> list = new ArrayList<Dtalge>();
		for (Object[] elem : algeElements) {
			Dtalge alge = null;
			if (elem != null) {
				alge = new Dtalge(elem);
			}
			list.add(alge);
		}
		return list;
	}
	
	static protected boolean equalElementSequence(DtAlgeSet aset, Object[][] elements) {
		if (elements == null || elements.length < 1) {
			return (aset == null ? true : aset.isEmpty());
		}
		
		if (aset.size() != elements.length)
			return false;
		
		int index = 0;
		for (Dtalge alge : aset) {
			if (!DtalgeTest.equalElementSequence(alge, elements[index])) {
				System.err.println("DtAlgeSetTest : equalElementSequence : [" + index + "]");
				return false;
			}
			index++;
		}
		return true;
	}
	
	static protected boolean equalNoNullElementSequence(DtAlgeSet aset, Object[][] elements) {
		if (elements == null || elements.length < 1) {
			return (aset == null ? true : aset.isEmpty());
		}
		
		if (aset.size() != elements.length)
			return false;
		
		int index = 0;
		for (Dtalge alge : aset) {
			if (!DtalgeTest.equalNoNullElementSequence(alge, elements[index])) {
				System.err.println("DtAlgeSetTest : equalElementSequence : [" + index + "]");
				return false;
			}
			index++;
		}
		return true;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	//------------------------------------------------------------
	// Test cases
	//------------------------------------------------------------

	/**
	 * {@link dtalge.DtAlgeSet#DtAlgeSet(java.util.Collection)} のためのテスト・メソッド。
	 */
	public void testDtAlgeSetCollectionOfQextendsDtalge() {
		DtAlgeSet aset;
		
		//--- null
		try {
			aset = new DtAlgeSet(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) { assertTrue(true); }
		
		//--- new
		aset = new DtAlgeSet(makeDtalges(normalAlges));
		assertTrue(equalElementSequence(aset, normalAlges));
		
		//--- with null
		aset = new DtAlgeSet(makeDtalges(withNullNormalAlges));
		assertTrue(equalElementSequence(aset, normalAlges));
		
		//--- with empty alge
		Object[][] cat1 = catElements(testAlges1, emptyAlge);
		Object[][] withEmptyNormalAlges = catElements(cat1, testAlges2);
		aset = new DtAlgeSet(makeDtalges(withEmptyNormalAlges));
		assertTrue(equalElementSequence(aset, withEmptyNormalAlges));
	}

	/**
	 * {@link dtalge.DtAlgeSet#addition(dtalge.DtAlgeSet)} のためのテスト・メソッド。
	 */
	public void testAddition() {
		DtAlgeSet ret;
		DtAlgeSet asempty = new DtAlgeSet();
		DtAlgeSet as1 = new DtAlgeSet(makeDtalges(testAlges1));
		DtAlgeSet as2 = new DtAlgeSet(makeDtalges(testAlges2));
		
		//--- null
		try {
			ret = asempty.addition(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			ret = as1.addition(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		//--- empty
		ret = asempty.addition(asempty);
		assertTrue(ret.isEmpty());
		ret = as1.addition(asempty);
		assertNotSame(ret, as1);
		assertTrue(asempty.isEmpty());
		assertTrue(equalElementSequence(as1, testAlges1));
		assertTrue(equalElementSequence(ret, testAlges1));
		ret = asempty.addition(as2);
		assertNotSame(ret, as2);
		assertTrue(asempty.isEmpty());
		assertTrue(equalElementSequence(as2, testAlges2));
		assertTrue(equalElementSequence(ret, testAlges2));
		
		//--- same elements
		Object[][] catAlges11 = catElements(testAlges1, testAlges1);
		Object[][] catAlges22 = catElements(testAlges2, testAlges2);
		ret = as1.addition(as1);
		assertTrue(equalElementSequence(as1, testAlges1));
		assertTrue(equalElementSequence(ret, catAlges11));
		ret = as2.addition(as2);
		assertTrue(equalElementSequence(as2, testAlges2));
		assertTrue(equalElementSequence(ret, catAlges22));
		
		//--- different elements
		Object[][] catAlges1e = catElements(testAlges1, testEmptyAlges);
		Object[][] catAlges1e2 = catElements(catAlges1e, testAlges2);
		DtAlgeSet ase = new DtAlgeSet(makeDtalges(testEmptyAlges));
		assertTrue(equalElementSequence(ase, testEmptyAlges));
		ret = as1.addition(ase);
		assertNotSame(ret, ase);
		assertNotSame(ret, as1);
		assertTrue(equalElementSequence(ase, testEmptyAlges));
		assertTrue(equalElementSequence(as1, testAlges1));
		assertTrue(equalElementSequence(ret, catAlges1e));
		ret = ret.addition(as2);
		assertNotSame(ret, ase);
		assertNotSame(ret, as1);
		assertNotSame(ret, as2);
		assertTrue(equalElementSequence(ase, testEmptyAlges));
		assertTrue(equalElementSequence(as1, testAlges1));
		assertTrue(equalElementSequence(as2, testAlges2));
		assertTrue(equalElementSequence(ret, catAlges1e2));
	}

	/**
	 * {@link dtalge.DtAlgeSet#subtraction(dtalge.DtAlgeSet)} のためのテスト・メソッド。
	 */
	public void testSubtraction() {
		DtAlgeSet ret;
		DtAlgeSet asempty = new DtAlgeSet();
		DtAlgeSet as2 = new DtAlgeSet(makeDtalges(testAlges2));
		DtAlgeSet asa = new DtAlgeSet(makeDtalges(normalAlges));
		Object[][] catAlges13 = catElements(testAlges1, testAlges3);
		
		//--- null
		try {
			ret = asempty.subtraction(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			ret = asa.addition(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		//--- empty
		ret = asempty.subtraction(asempty);
		assertTrue(asempty.isEmpty());
		assertNotSame(ret, asempty);
		assertTrue(ret.isEmpty());
		ret = asempty.subtraction(asa);
		assertTrue(asempty.isEmpty());
		assertTrue(equalElementSequence(asa, normalAlges));
		assertNotSame(ret, asempty);
		assertNotSame(ret, asa);
		assertTrue(ret.isEmpty());
		ret = asa.subtraction(asempty);
		assertTrue(asempty.isEmpty());
		assertTrue(equalElementSequence(asa, normalAlges));
		assertNotSame(ret, asempty);
		assertNotSame(ret, asa);
		assertTrue(equalElementSequence(ret, normalAlges));
		
		//--- same elements
		ret = as2.subtraction(as2);
		assertTrue(equalElementSequence(as2, testAlges2));
		assertNotSame(ret, as2);
		assertTrue(ret.isEmpty());
		ret = asa.subtraction(asa);
		assertTrue(equalElementSequence(asa, normalAlges));
		assertNotSame(ret, asa);
		assertTrue(ret.isEmpty());
		Object[][] catAlges22 = catElements(testAlges2, testAlges2);
		DtAlgeSet as22 = as2.addition(as2);
		assertTrue(equalElementSequence(as22, catAlges22));
		ret = as22.subtraction(as2);
		assertTrue(equalElementSequence(as2, testAlges2));
		assertTrue(equalElementSequence(as22, catAlges22));
		assertNotSame(ret, as2);
		assertNotSame(ret, as22);
		assertTrue(ret.isEmpty());
		
		//--- different elements
		Object[][] catAlgesAA = catAlges(withEmptyNormalAlges, withEmptyNormalAlges);
		Object[][] catAlgesEE = catAlges(testEmptyAlges1, testEmptyAlges2, testEmptyAlges3,
										testEmptyAlges1, testEmptyAlges2, testEmptyAlges3);
		Object[][] catAlgesE2EE2E = catAlges(testEmptyAlges1, testWithEmptyAlges2, testEmptyAlges3,
											testEmptyAlges1, testWithEmptyAlges2, testEmptyAlges3);
		DtAlgeSet asAA = new DtAlgeSet(makeDtalges(catAlgesAA));
		DtAlgeSet asEE = new DtAlgeSet(makeDtalges(catAlgesEE));
		DtAlgeSet as13 = new DtAlgeSet(makeDtalges(catAlges13));
		ret = asAA.subtraction(as13);
		assertTrue(equalElementSequence(asAA, catAlgesAA));
		assertTrue(equalElementSequence(as13, catAlges13));
		assertNotSame(ret, asAA);
		assertNotSame(ret, as13);
		assertTrue(equalElementSequence(ret, catAlgesE2EE2E));
		ret = asEE.subtraction(asAA);
		assertTrue(equalElementSequence(asAA, catAlgesAA));
		assertTrue(equalElementSequence(asEE, catAlgesEE));
		assertNotSame(ret, asAA);
		assertNotSame(ret, asEE);
		assertTrue(ret.isEmpty());
		ret = as2.subtraction(asa);
		assertTrue(equalElementSequence(as2, testAlges2));
		assertTrue(equalElementSequence(asa, normalAlges));
		assertNotSame(ret, as2);
		assertNotSame(ret, asa);
		assertTrue(ret.isEmpty());
		ret = asa.subtraction(as2);
		assertTrue(equalElementSequence(as2, testAlges2));
		assertTrue(equalElementSequence(asa, normalAlges));
		assertNotSame(ret, as2);
		assertNotSame(ret, asa);
		assertTrue(equalElementSequence(ret, catAlges13));
	}

	/**
	 * {@link dtalge.DtAlgeSet#retention(dtalge.DtAlgeSet)} のためのテスト・メソッド。
	 */
	public void testRetention() {
		DtAlgeSet ret;
		DtAlgeSet asempty = new DtAlgeSet();
		DtAlgeSet as2 = new DtAlgeSet(makeDtalges(testAlges2));
		DtAlgeSet asa = new DtAlgeSet(makeDtalges(normalAlges));
		
		//--- null
		try {
			ret = asempty.retention(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			ret = asa.retention(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		//--- empty
		ret = asempty.retention(asempty);
		assertTrue(asempty.isEmpty());
		assertNotSame(ret, asempty);
		assertTrue(ret.isEmpty());
		ret = asempty.retention(asa);
		assertTrue(asempty.isEmpty());
		assertTrue(equalElementSequence(asa, normalAlges));
		assertNotSame(ret, asempty);
		assertNotSame(ret, asa);
		assertTrue(ret.isEmpty());
		ret = asa.retention(asempty);
		assertTrue(asempty.isEmpty());
		assertTrue(equalElementSequence(asa, normalAlges));
		assertNotSame(ret, asempty);
		assertNotSame(ret, asa);
		assertTrue(ret.isEmpty());
		
		//--- same elements
		ret = as2.retention(as2);
		assertTrue(equalElementSequence(as2, testAlges2));
		assertNotSame(ret, as2);
		assertTrue(equalElementSequence(ret, testAlges2));
		ret = asa.retention(asa);
		assertTrue(equalElementSequence(asa, normalAlges));
		assertNotSame(ret, asa);
		assertTrue(equalElementSequence(ret, normalAlges));
		
		//--- different elements
		Object[][] catAlgesAA = catAlges(withEmptyNormalAlges, withEmptyNormalAlges);
		Object[][] catAlgesEE = catAlges(testEmptyAlges1, testEmptyAlges2, testEmptyAlges3,
										testEmptyAlges1, testEmptyAlges2, testEmptyAlges3);
		Object[][] catAlges13 = catAlges(testAlges1, testAlges3);
		Object[][] catAlges1313 = catAlges(testAlges1, testAlges3, testAlges1, testAlges3);
		DtAlgeSet asAA = new DtAlgeSet(makeDtalges(catAlgesAA));
		DtAlgeSet asEE = new DtAlgeSet(makeDtalges(catAlgesEE));
		DtAlgeSet as13 = new DtAlgeSet(makeDtalges(catAlges13));
		assertTrue(equalElementSequence(asAA, catAlgesAA));
		assertTrue(equalElementSequence(asEE, catAlgesEE));
		assertTrue(equalElementSequence(as13, catAlges13));
		ret = asAA.retention(asEE);
		assertTrue(equalElementSequence(asAA, catAlgesAA));
		assertTrue(equalElementSequence(asEE, catAlgesEE));
		assertNotSame(ret, asAA);
		assertNotSame(ret, asEE);
		assertTrue(equalElementSequence(ret, catAlgesEE));
		ret = asEE.retention(asAA);
		assertTrue(equalElementSequence(asAA, catAlgesAA));
		assertTrue(equalElementSequence(asEE, catAlgesEE));
		assertNotSame(ret, asAA);
		assertNotSame(ret, asEE);
		assertTrue(equalElementSequence(ret, catAlgesEE));
		ret = asAA.retention(as13);
		assertTrue(equalElementSequence(asAA, catAlgesAA));
		assertTrue(equalElementSequence(as13, catAlges13));
		assertNotSame(ret, asAA);
		assertNotSame(ret, as13);
		assertTrue(equalElementSequence(ret, catAlges1313));
		ret = as13.retention(asAA);
		assertTrue(equalElementSequence(asAA, catAlgesAA));
		assertTrue(equalElementSequence(as13, catAlges13));
		assertNotSame(ret, asAA);
		assertNotSame(ret, as13);
		assertTrue(equalElementSequence(ret, catAlges13));
	}

	/**
	 * {@link dtalge.DtAlgeSet#union(dtalge.DtAlgeSet)} のためのテスト・メソッド。
	 */
	public void testUnion() {
		Object[][] catAlges13 = catAlges(testWithEmptyAlges1, testWithEmptyAlges3);
		Object[][] catAlges132 = catAlges(testAlges1, testAlges3, testAlges2);
		
		DtAlgeSet ret;
		DtAlgeSet asempty = new DtAlgeSet();
		DtAlgeSet as13 = new DtAlgeSet(makeDtalges(catAlges13));
		DtAlgeSet as132 = new DtAlgeSet(makeDtalges(catAlges132));
		DtAlgeSet asAA = new DtAlgeSet(makeDtalges(withEmptyNormalAlges));
		
		//--- null
		try {
			ret = asempty.union(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			ret = asAA.union(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		//--- empty
		ret = asempty.union(asempty);
		assertTrue(asempty.isEmpty());
		assertNotSame(ret, asempty);
		assertTrue(ret.isEmpty());
		ret = as132.union(asempty);
		assertNotSame(ret, as132);
		assertTrue(asempty.isEmpty());
		assertTrue(equalElementSequence(as132, catAlges132));
		assertNotSame(ret, as132);
		assertNotSame(ret, asempty);
		assertTrue(equalElementSequence(ret, catAlges132));
		ret = asempty.union(asAA);
		assertTrue(asempty.isEmpty());
		assertTrue(equalElementSequence(asAA, withEmptyNormalAlges));
		assertNotSame(ret, asempty);
		assertNotSame(ret, asAA);
		assertTrue(equalElementSequence(ret, normalAlges));
		
		//--- same elements
		ret = as132.union(as132);
		assertTrue(equalElementSequence(as132, catAlges132));
		assertNotSame(ret, as132);
		assertTrue(equalElementSequence(ret, catAlges132));
		ret = asAA.union(asAA);
		assertTrue(equalElementSequence(asAA, withEmptyNormalAlges));
		assertNotSame(ret, asAA);
		assertTrue(equalElementSequence(ret, normalAlges));
		
		//--- different elements
		ret = as13.union(asAA);
		assertTrue(equalElementSequence(as13, catAlges13));
		assertTrue(equalElementSequence(asAA, withEmptyNormalAlges));
		assertNotSame(ret, as13);
		assertNotSame(ret, asAA);
		assertTrue(equalElementSequence(ret, catAlges132));
		ret = asAA.union(as13);
		assertTrue(equalElementSequence(as13, catAlges13));
		assertTrue(equalElementSequence(asAA, withEmptyNormalAlges));
		assertNotSame(ret, as13);
		assertNotSame(ret, asAA);
		assertTrue(equalElementSequence(ret, normalAlges));
	}

	/**
	 * {@link dtalge.DtAlgeSet#intersection(dtalge.DtAlgeSet)} のためのテスト・メソッド。
	 */
	public void testIntersection() {
		Object[][] catAlges13 = catAlges(testWithEmptyAlges1, testWithEmptyAlges3);
		Object[][] catAlges132 = catAlges(testAlges1, testAlges3, testAlges2);
		Object[][] catRet13 = catAlges(testAlges1, testAlges3);
		
		DtAlgeSet ret;
		DtAlgeSet asempty = new DtAlgeSet();
		DtAlgeSet as13 = new DtAlgeSet(makeDtalges(catAlges13));
		DtAlgeSet as132 = new DtAlgeSet(makeDtalges(catAlges132));
		DtAlgeSet asAA = new DtAlgeSet(makeDtalges(withEmptyNormalAlges));
		
		//--- null
		try {
			ret = asempty.intersection(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			ret = asAA.intersection(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		//--- empty
		ret = asempty.intersection(asempty);
		assertTrue(asempty.isEmpty());
		assertNotSame(ret, asempty);
		assertTrue(ret.isEmpty());
		ret = as132.intersection(asempty);
		assertNotSame(ret, as132);
		assertTrue(asempty.isEmpty());
		assertTrue(equalElementSequence(as132, catAlges132));
		assertNotSame(ret, as132);
		assertNotSame(ret, asempty);
		assertTrue(ret.isEmpty());
		ret = asempty.intersection(asAA);
		assertTrue(asempty.isEmpty());
		assertTrue(equalElementSequence(asAA, withEmptyNormalAlges));
		assertNotSame(ret, asempty);
		assertNotSame(ret, asAA);
		assertTrue(ret.isEmpty());
		
		//--- same elements
		ret = as132.intersection(as132);
		assertTrue(equalElementSequence(as132, catAlges132));
		assertNotSame(ret, as132);
		assertTrue(equalElementSequence(ret, catAlges132));
		ret = asAA.intersection(asAA);
		assertTrue(equalElementSequence(asAA, withEmptyNormalAlges));
		assertNotSame(ret, asAA);
		assertTrue(equalElementSequence(ret, normalAlges));
		
		//--- different elements
		ret = as13.intersection(asAA);
		assertTrue(equalElementSequence(as13, catAlges13));
		assertTrue(equalElementSequence(asAA, withEmptyNormalAlges));
		assertNotSame(ret, as13);
		assertNotSame(ret, asAA);
		assertTrue(equalElementSequence(ret, catRet13));
		ret = asAA.intersection(as13);
		assertTrue(equalElementSequence(as13, catAlges13));
		assertTrue(equalElementSequence(asAA, withEmptyNormalAlges));
		assertNotSame(ret, as13);
		assertNotSame(ret, asAA);
		assertTrue(equalElementSequence(ret, catRet13));
	}

	/**
	 * {@link dtalge.DtAlgeSet#difference(dtalge.DtAlgeSet)} のためのテスト・メソッド。
	 */
	public void testDifference() {
		Object[][] catAlges13 = catAlges(testWithEmptyAlges1, testWithEmptyAlges3);
		Object[][] catAlges21 = catAlges(testWithEmptyAlges2, testWithEmptyAlges1);
		Object[][] catAlges132 = catAlges(testAlges1, testAlges3, testAlges2);
		
		DtAlgeSet ret;
		DtAlgeSet asempty = new DtAlgeSet();
		DtAlgeSet as13 = new DtAlgeSet(makeDtalges(catAlges13));
		DtAlgeSet as21 = new DtAlgeSet(makeDtalges(catAlges21));
		DtAlgeSet as132 = new DtAlgeSet(makeDtalges(catAlges132));
		DtAlgeSet asAA = new DtAlgeSet(makeDtalges(withEmptyNormalAlges));
		
		//--- null
		try {
			ret = asempty.difference(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			ret = asAA.difference(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		//--- empty
		ret = asempty.difference(asempty);
		assertTrue(asempty.isEmpty());
		assertNotSame(ret, asempty);
		assertTrue(ret.isEmpty());
		ret = as132.difference(asempty);
		assertNotSame(ret, as132);
		assertTrue(asempty.isEmpty());
		assertTrue(equalElementSequence(as132, catAlges132));
		assertNotSame(ret, as132);
		assertNotSame(ret, asempty);
		assertTrue(equalElementSequence(ret, catAlges132));
		ret = asempty.difference(asAA);
		assertTrue(asempty.isEmpty());
		assertTrue(equalElementSequence(asAA, withEmptyNormalAlges));
		assertNotSame(ret, asempty);
		assertNotSame(ret, asAA);
		assertTrue(ret.isEmpty());
		ret = asAA.difference(asempty);
		assertTrue(asempty.isEmpty());
		assertTrue(equalElementSequence(asAA, withEmptyNormalAlges));
		assertNotSame(ret, asempty);
		assertNotSame(ret, asAA);
		assertTrue(equalElementSequence(ret, normalAlges));
		
		//--- same elements
		ret = as132.difference(as132);
		assertTrue(equalElementSequence(as132, catAlges132));
		assertNotSame(ret, as132);
		assertTrue(ret.isEmpty());
		ret = asAA.difference(asAA);
		assertTrue(equalElementSequence(asAA, withEmptyNormalAlges));
		assertNotSame(ret, asAA);
		assertTrue(ret.isEmpty());
		
		//--- different elements
		ret = as13.difference(asAA);
		assertTrue(equalElementSequence(as13, catAlges13));
		assertTrue(equalElementSequence(asAA, withEmptyNormalAlges));
		assertNotSame(ret, as13);
		assertNotSame(ret, asAA);
		assertTrue(ret.isEmpty());
		ret = asAA.difference(as13);
		assertTrue(equalElementSequence(as13, catAlges13));
		assertTrue(equalElementSequence(asAA, withEmptyNormalAlges));
		assertNotSame(ret, as13);
		assertNotSame(ret, asAA);
		assertTrue(equalElementSequence(ret, testAlges2));
		ret = as13.difference(as21);
		assertTrue(equalElementSequence(as13, catAlges13));
		assertTrue(equalElementSequence(as21, catAlges21));
		assertNotSame(ret, as13);
		assertNotSame(ret, as21);
		assertTrue(equalElementSequence(ret, testAlges3));
	}

	/**
	 * {@link dtalge.DtAlgeSet#getBases()} のためのテスト・メソッド。
	 */
	public void testGetBases() {
		DtBaseSet ret;
		
		//--- empty
		DtAlgeSet asempty = new DtAlgeSet();
		assertTrue(asempty.isEmpty());
		ret = asempty.getBases();
		assertTrue(ret.isEmpty());
		
		//--- exists
		DtAlgeSet asall = new DtAlgeSet(makeDtalges(withEmptyNormalAlges));
		assertTrue(equalElementSequence(asall, withEmptyNormalAlges));
		ret = asall.getBases();
		assertTrue(DtBaseSetTest.equalElementSequence(ret, testBases));
	}

	/**
	 * {@link dtalge.DtAlgeSet#getMatchedBases(dtalge.DtBasePattern)} のためのテスト・メソッド。
	 */
	public void testGetMatchedBasesDtBasePattern() {
		DtBasePattern patNull = null;
		DtBasePattern pat1 = new DtBasePattern("*", "*tri*", "*", "*");
		DtBasePattern pat2 = new DtBasePattern("*", "*", "#", "#");
		//DtBasePattern pat3 = new DtBasePattern("年齢", "integer", "#", "#");
		DtBasePattern pat3 = new DtBasePattern("年齢", "decimal", "#", "#");
		DtBasePattern pat4 = new DtBasePattern("職業", "string", "*", "*");
		
		DtBase[] ans1 = {
				new DtBase("名前","string","#","#"),
				new DtBase("性別","string","#","#"),
				new DtBase("都道府県","string","#","#"),
				new DtBase("注目食品","string","#","#"),
				new DtBase("備考","string","#","#"),
		};
		DtBase[] ans2 = {
				new DtBase("名前","string","#","#"),
				new DtBase("性別","string","#","#"),
				//new DtBase("年齢","integer","#","#"),
				new DtBase("年齢","decimal","#","#"),
				new DtBase("都道府県","string","#","#"),
				new DtBase("注目食品","string","#","#"),
				new DtBase("ポイント","decimal","#","#"),
				new DtBase("フラグ","boolean","#","#"),
				new DtBase("備考","string","#","#"),
		};
		DtBase[] ans3 = {
				new DtBase("年齢","decimal","#","#"),
		};
		
		DtAlgeSet asempty = new DtAlgeSet();
		DtAlgeSet asall = new DtAlgeSet(makeDtalges(withEmptyNormalAlges));
		assertTrue(asempty.isEmpty());
		assertTrue(equalElementSequence(asall, withEmptyNormalAlges));
		
		DtBaseSet ret;
		
		//--- null
		try {
			ret = asempty.getMatchedBases(patNull);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			ret = asall.getMatchedBases(patNull);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		//--- empty
		ret = asempty.getMatchedBases(pat1);
		assertTrue(ret.isEmpty());
		ret = asempty.getMatchedBases(pat2);
		assertTrue(ret.isEmpty());
		ret = asempty.getMatchedBases(pat3);
		assertTrue(ret.isEmpty());
		ret = asempty.getMatchedBases(pat4);
		assertTrue(ret.isEmpty());
		
		//--- exists
		ret = asall.getMatchedBases(pat1);
		assertTrue(DtBaseSetTest.equalElementSequence(ret, ans1));
		ret = asall.getMatchedBases(pat2);
		assertTrue(DtBaseSetTest.equalElementSequence(ret, ans2));
		ret = asall.getMatchedBases(pat3);
		assertTrue(DtBaseSetTest.equalElementSequence(ret, ans3));
		ret = asall.getMatchedBases(pat4);
		assertTrue(ret.isEmpty());
	}

	/**
	 * {@link dtalge.DtAlgeSet#getMatchedBases(dtalge.DtBasePatternSet)} のためのテスト・メソッド。
	 */
	public void testGetMatchedBasesDtBasePatternSet() {
		DtBasePatternSet patNull = null;
		DtBasePatternSet pat0 = new DtBasePatternSet();
		DtBasePatternSet pat1 = new DtBasePatternSet();
		pat1.add(new DtBasePattern("性別", "*", "#", "#"));
		pat1.add(new DtBasePattern("注目*", "*", "*", "*"));
		pat1.add(new DtBasePattern("*", "boolean", "*", "*"));
		pat1.add(new DtBasePattern("職業", "string", "*", "*"));
		DtBasePatternSet pat2 = new DtBasePatternSet();
		pat2.add(new DtBasePattern("名前", "boolean", "*", "*"));
		pat2.add(new DtBasePattern("*", "decimal", "*", "*"));
		
		DtBase[] ans1 = {
				new DtBase("性別","string","#","#"),
				new DtBase("注目食品","string","#","#"),
				new DtBase("フラグ","boolean","#","#"),
		};
		DtBase[] ans2 = {
				new DtBase("年齢","decimal","#","#"),
				new DtBase("ポイント","decimal","#","#"),
		};
		
		DtAlgeSet asempty = new DtAlgeSet();
		DtAlgeSet asall = new DtAlgeSet(makeDtalges(withEmptyNormalAlges));
		assertTrue(asempty.isEmpty());
		assertTrue(equalElementSequence(asall, withEmptyNormalAlges));
		
		DtBaseSet ret;
		
		//--- null
		try {
			ret = asempty.getMatchedBases(patNull);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			ret = asall.getMatchedBases(patNull);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		//--- empty
		ret = asempty.getMatchedBases(pat0);
		assertTrue(ret.isEmpty());
		ret = asempty.getMatchedBases(pat1);
		assertTrue(ret.isEmpty());
		ret = asempty.getMatchedBases(pat2);
		assertTrue(ret.isEmpty());
		
		//--- exists
		ret = asempty.getMatchedBases(pat0);
		assertTrue(ret.isEmpty());
		ret = asall.getMatchedBases(pat1);
		assertTrue(DtBaseSetTest.equalElementSequence(ret, ans1));
		ret = asall.getMatchedBases(pat2);
		assertTrue(DtBaseSetTest.equalElementSequence(ret, ans2));
	}
	
	/**
	 * {@link dtalge.DtAlgeSet#oneValueProjection(Object)} のためのテスト・メソッド。
	 * @since 0.20
	 */
	public void testOneValueProjection() {
		DtAlgeSet ret;
		DtAlgeSet ans;
		DtAlgeSet asempty = new DtAlgeSet();
		DtAlgeSet as1 = new DtAlgeSet(makeDtalges(normalAlges));
		
		Object[] values = new Object[]{
				null,
				new BigDecimal("60.00"),
				"男性",
				"健康食品",
		};

		for (Object value : values) {
			ret = asempty.oneValueProjection(value);
			assertTrue(ret.isEmpty());
			assertTrue(asempty.isEmpty());
			
			ans = new DtAlgeSet(makeDtalges(filterElements(normalAlges, value)));
			ret = as1.oneValueProjection(value);
			assertEquals(ret, ans);
			assertTrue(equalElementSequence(as1, normalAlges));
		}
	}
	
	/**
	 * {@link dtalge.DtAlgeSet#valuesProjection(Collection)} のためのテスト・メソッド。
	 * @since 0.20
	 */
	public void testValuesProjection() {
		Object objNull = null;
		DtAlgeSet ret;
		DtAlgeSet ans;
		DtAlgeSet asempty = new DtAlgeSet();
		DtAlgeSet as1 = new DtAlgeSet(makeDtalges(normalAlges));

		Object[][] values = new Object[][]{
				{objNull, new BigDecimal("60.00"), "男性", "健康食品"},
				{new BigDecimal("1024"), "中性", "たべもの"},
		};
		
		// null
		try {
			asempty.valuesProjection(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			as1.valuesProjection(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		assertTrue(asempty.isEmpty());
		assertTrue(equalElementSequence(as1, normalAlges));
		
		// empty
		ret = asempty.valuesProjection(Collections.<Object>emptyList());
		assertTrue(ret.isEmpty());
		ret = as1.valuesProjection(Collections.<Object>emptyList());
		assertTrue(ret.isEmpty());
		assertTrue(asempty.isEmpty());
		assertTrue(equalElementSequence(as1, normalAlges));
		
		// values
		for (int i = 0; i < values.length; i++) {
			ret = asempty.valuesProjection(Arrays.<Object>asList(values[i]));
			assertTrue(ret.isEmpty());
			assertTrue(asempty.isEmpty());
			
			ans = new DtAlgeSet(makeDtalges(filterElements(normalAlges, values[i])));
			ret = as1.valuesProjection(Arrays.<Object>asList(values[i]));
			assertEquals(ret, ans);
			assertTrue(equalElementSequence(as1, normalAlges));
		}
	}
	
	/**
	 * {@link dtalge.DtAlgeSet#nullProjection()} のためのテスト・メソッド。
	 * @since 0.20
	 */
	public void testNullProjection() {
		Object objNull = null;
		DtAlgeSet ret;
		DtAlgeSet ans;
		DtAlgeSet asempty = new DtAlgeSet();
		DtAlgeSet as1 = new DtAlgeSet(makeDtalges(normalAlges));
		
		ret = asempty.nullProjection();
		assertTrue(ret.isEmpty());
		assertTrue(asempty.isEmpty());
		
		ret = as1.nullProjection();
		ans = new DtAlgeSet(makeDtalges(filterElements(normalAlges, objNull)));
		assertEquals(ret, ans);
		assertTrue(equalElementSequence(as1, normalAlges));
	}
	
	/**
	 * {@link dtalge.DtAlgeSet#nonullProjection()} のためのテスト・メソッド。
	 * @since 0.20
	 */
	public void testNoNullProjection() {
		Object objNull = null;
		DtAlgeSet ret;
		DtAlgeSet ans;
		DtAlgeSet asempty = new DtAlgeSet();
		DtAlgeSet as1 = new DtAlgeSet(makeDtalges(normalAlges));
		
		ret = asempty.nonullProjection();
		assertTrue(ret.isEmpty());
		assertTrue(asempty.isEmpty());
		
		ret = as1.nonullProjection();
		ans = new DtAlgeSet(makeDtalges(removeElements(normalAlges, objNull)));
		assertEquals(ret, ans);
		assertTrue(equalElementSequence(as1, normalAlges));
	}

	/**
	 * {@link dtalge.DtAlgeSet#projection(dtalge.DtBase)} のためのテスト・メソッド。
	 */
	public void testProjectionDtBase() {
		DtBase baseNull = null;
		DtBase base1 = new DtBase("性別","string","#","#");
		DtBase base2 = new DtBase("性別","boolean","#","#");
		
		//--- make result data
		final Object[][] ansAlges = {
			{	// [0]
				new DtBase("性別","string","#","#"), "女性",
			},
			{	// [1]
				new DtBase("性別","string","#","#"), "男性",
			},
			{	// [2]
				new DtBase("性別","string","#","#"), "男性",
			},
			{	// [3]
				new DtBase("性別","string","#","#"), "男性",
			},
			{	// [4]
				new DtBase("性別","string","#","#"), "男性",
			},
			{	// [5]
				new DtBase("性別","string","#","#"), "女性",
			},
		};
		
		DtAlgeSet ret;
		DtAlgeSet asempty = new DtAlgeSet();
		DtAlgeSet as1 = new DtAlgeSet(makeDtalges(testWithEmptyAlges1));
		assertTrue(asempty.isEmpty());
		assertTrue(equalElementSequence(as1, testWithEmptyAlges1));
		
		//--- null
		try {
			ret = asempty.projection(baseNull);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			ret = as1.projection(baseNull);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		//--- empty
		ret = asempty.projection(base1);
		assertNotNull(asempty);
		assertTrue(ret.isEmpty());
		ret = asempty.projection(base2);
		assertNotNull(asempty);
		assertTrue(ret.isEmpty());
		
		//--- exists
		ret = as1.projection(base1);
		assertTrue(equalElementSequence(as1, testWithEmptyAlges1));
		assertNotSame(ret, as1);
		assertTrue(equalElementSequence(ret, ansAlges));
		ret = as1.projection(base2);
		assertTrue(equalElementSequence(as1, testWithEmptyAlges1));
		assertNotSame(ret, as1);
		assertTrue(ret.isEmpty());
	}

	/**
	 * {@link dtalge.DtAlgeSet#projection(dtalge.DtBaseSet)} のためのテスト・メソッド。
	 */
	public void testProjectionDtBaseSet() {
		DtBaseSet bsnull = null;
		DtBaseSet bsempty = new DtBaseSet();
		DtBaseSet bssome = new DtBaseSet(Arrays.asList(testBases[1], testBases[3]));
		DtBaseSet bsall = new DtBaseSet(Arrays.asList(testBases));
		DtAlgeSet asempty = new DtAlgeSet();
		DtAlgeSet as1 = new DtAlgeSet(makeDtalges(withEmptyNormalAlges));
		assertTrue(asempty.isEmpty());
		assertFalse(as1.isEmpty());
		DtAlgeSet ret;
		
		//--- null
		try {
			asempty.projection(bsnull);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			as1.projection(bsnull);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		//--- empty
		ret = asempty.projection(bsempty);
		assertTrue(ret.isEmpty());
		ret = asempty.projection(bssome);
		assertTrue(ret.isEmpty());
		ret = asempty.projection(bsall);
		assertTrue(ret.isEmpty());
		
		//--- data
		ret = as1.projection(bsempty);
		assertTrue(ret.isEmpty());
		ret = as1.projection(bssome);
		DtAlgeSet ans_some = new DtAlgeSet();
		for (Dtalge alge : makeDtalges(normalAlges)) {
			Dtalge pa = alge.projection(bssome);
			if (!pa.isEmpty()) {
				ans_some.add(pa);
			}
		}
		assertTrue(ret.equals(ans_some));
		ret = as1.projection(bsall);
		assertTrue(ret.equals(new DtAlgeSet(makeDtalges(normalAlges))));
	}

	/**
	 * {@link dtalge.DtAlgeSet#patternProjection(dtalge.DtBasePattern)} のためのテスト・メソッド。
	 */
	public void testPatternProjectionDtBasePattern() {
		DtBasePattern bpnull = null;
		DtBasePattern bpnone = new DtBasePattern("*名*前*", "boolean", "*", "*");
		DtBasePattern bpone = new DtBasePattern("*名*前*", "string", "*", "*");
		DtBasePattern bpall = new DtBasePattern("*", "*", "*", "*");
		DtAlgeSet ans = new DtAlgeSet();
		for (Dtalge alge : makeDtalges(normalAlges)) {
			Dtalge pa = alge.patternProjection(bpone);
			if (!pa.isEmpty()) {
				ans.add(pa);
			}
		}
		DtAlgeSet asempty = new DtAlgeSet();
		DtAlgeSet as1 = new DtAlgeSet(makeDtalges(withEmptyNormalAlges));
		assertTrue(asempty.isEmpty());
		assertFalse(as1.isEmpty());
		DtAlgeSet ret;
		
		//--- null
		try {
			asempty.patternProjection(bpnull);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			as1.patternProjection(bpnull);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		//--- empty
		ret = asempty.patternProjection(bpnone);
		assertTrue(ret.isEmpty());
		ret = asempty.patternProjection(bpone);
		assertTrue(ret.isEmpty());
		ret = asempty.patternProjection(bpall);
		assertTrue(ret.isEmpty());
		
		//--- data
		ret = as1.patternProjection(bpnone);
		assertTrue(ret.isEmpty());
		ret = as1.patternProjection(bpone);
		assertTrue(ret.equals(ans));
		ret = as1.patternProjection(bpall);
		assertTrue(ret.equals(new DtAlgeSet(makeDtalges(normalAlges))));
		assertTrue(equalElementSequence(as1, withEmptyNormalAlges));
	}

	/**
	 * {@link dtalge.DtAlgeSet#patternProjection(dtalge.DtBasePatternSet)} のためのテスト・メソッド。
	 */
	public void testPatternProjectionDtBasePatternSet() {
		DtBasePattern patNone1 = new DtBasePattern("*名*前*", "boolean", "*", "*");
		DtBasePattern patNone2 = new DtBasePattern("年齢", "string", "*", "*");
		DtBasePattern patNone3 = new DtBasePattern("*", "*", "Primary", "*");
		DtBasePattern patName = new DtBasePattern("*名*前*", "string", "*", "*");
		DtBasePattern patInt  = new DtBasePattern("*", "integer", "*", "*");
		DtBasePatternSet bsnull = null;
		DtBasePatternSet bsnone = new DtBasePatternSet(Arrays.asList(patNone1, patNone2, patNone3));
		DtBasePatternSet bssome = new DtBasePatternSet(Arrays.asList(patName, patInt, patNone3));
		DtBasePatternSet bsall  = new DtBasePatternSet(new DtBaseSet(Arrays.asList(testBases)), true);
		DtAlgeSet ans = new DtAlgeSet();
		for (Dtalge alge : makeDtalges(normalAlges)) {
			Dtalge pa = alge.patternProjection(bssome);
			if (!pa.isEmpty()) {
				ans.add(pa);
			}
		}
		DtAlgeSet asempty = new DtAlgeSet();
		DtAlgeSet as1 = new DtAlgeSet(makeDtalges(withEmptyNormalAlges));
		assertTrue(asempty.isEmpty());
		assertFalse(as1.isEmpty());
		DtAlgeSet ret;
		
		//--- null
		try {
			asempty.patternProjection(bsnull);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			as1.patternProjection(bsnull);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		//--- empty
		ret = asempty.patternProjection(bsnone);
		assertTrue(ret.isEmpty());
		ret = asempty.patternProjection(bssome);
		assertTrue(ret.isEmpty());
		ret = asempty.patternProjection(bsall);
		assertTrue(ret.isEmpty());
		
		//--- data
		ret = as1.patternProjection(bsnone);
		assertTrue(ret.isEmpty());
		ret = as1.patternProjection(bssome);
		assertTrue(ret.equals(ans));
		ret = as1.patternProjection(bsall);
		assertTrue(ret.equals(new DtAlgeSet(makeDtalges(normalAlges))));
		assertTrue(equalElementSequence(as1, withEmptyNormalAlges));
	}

	/**
	 * {@link dtalge.DtAlgeSet#set(int, dtalge.Dtalge)} のためのテスト・メソッド。
	 */
	public void testSetIntDtalge() {
		DtAlgeSet asempty = new DtAlgeSet();
		DtAlgeSet as1 = new DtAlgeSet(makeDtalges(normalAlges));
		assertTrue(asempty.isEmpty());
		assertFalse(as1.isEmpty());
		
		//--- null
		try {
			as1.set(5, null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		//--- out of range
		try {
			asempty.set(0, new Dtalge());
			fail("must be throw IndexOutOfBoundsException.");
		} catch (IndexOutOfBoundsException ex) {
			assertTrue(true);
		}
		try {
			as1.set(as1.size(), new Dtalge());
			fail("must be throw IndexOutOfBoundsException.");
		} catch (IndexOutOfBoundsException ex) {
			assertTrue(true);
		}
		
		//--- test
		final Object[][] emptyAlges = {{},{},{}};
		DtAlgeSet se = new DtAlgeSet(makeDtalges(testAlges2));
		as1 = new DtAlgeSet(makeDtalges(catAlges(testAlges1, emptyAlges, testAlges3)));
		assertTrue(equalElementSequence(as1, catAlges(testAlges1, emptyAlges, testAlges3)));
		Dtalge r = as1.set(testAlges1.length+0, se.get(0));
		assertTrue(r.isEmpty());
		r = as1.set(testAlges1.length+1, se.get(1));
		assertTrue(r.isEmpty());
		r = as1.set(testAlges1.length+2, se.get(2));
		assertTrue(r.isEmpty());
		assertTrue(equalElementSequence(as1, normalAlges));
	}

	/**
	 * {@link dtalge.DtAlgeSet#add(dtalge.Dtalge)} のためのテスト・メソッド。
	 */
	public void testAddDtalge() {
		boolean ret = false;
		assertFalse(ret);
		DtAlgeSet asempty = new DtAlgeSet();
		assertTrue(asempty.isEmpty());
		DtAlgeSet as1 = new DtAlgeSet(makeDtalges(testWithEmptyAlges1));
		assertTrue(equalElementSequence(as1, testWithEmptyAlges1));
		
		//--- null
		try {
			ret = asempty.add(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			ret = as1.add(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		for (Object[] objalge : testWithEmptyAlges1) {
			Dtalge aalge = new Dtalge(objalge);
			assertTrue(DtalgeTest.equalElementSequence(aalge, objalge));
			assertTrue(asempty.add(aalge));
		}
		assertTrue(equalElementSequence(asempty, testWithEmptyAlges1));
		
		for (Object[] objalge : testWithEmptyAlges2) {
			Dtalge aalge = new Dtalge(objalge);
			assertTrue(DtalgeTest.equalElementSequence(aalge, objalge));
			assertTrue(as1.add(aalge));
		}
		Object[][] catAlges12 = catAlges(testWithEmptyAlges1, testWithEmptyAlges2);
		assertTrue(equalElementSequence(as1, catAlges12));
	}

	/**
	 * {@link dtalge.DtAlgeSet#add(int, dtalge.Dtalge)} のためのテスト・メソッド。
	 */
	public void testAddIntDtalge() {
		DtAlgeSet asempty = new DtAlgeSet();
		assertTrue(asempty.isEmpty());
		DtAlgeSet as1 = new DtAlgeSet(makeDtalges(testWithEmptyAlges1));
		assertTrue(equalElementSequence(as1, testWithEmptyAlges1));
		
		//--- null
		try {
			asempty.add(0, null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			as1.add(0, null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		//--- Out of bounds
		try {
			asempty.add(3, new Dtalge());
			fail("must be throw IndexOutOfBoundsException.");
		} catch (IndexOutOfBoundsException ex) {
			assertTrue(true);
		}
		try {
			as1.add(as1.size()+1, new Dtalge());
			fail("must be throw IndexOutOfBoundsException.");
		} catch (IndexOutOfBoundsException ex) {
			assertTrue(true);
		}
	}

	/**
	 * {@link dtalge.DtAlgeSet#addAll(java.util.Collection)} のためのテスト・メソッド。
	 */
	public void testAddAllCollectionOfQextendsDtalge() {
		DtAlgeSet asempty = new DtAlgeSet();
		DtAlgeSet as1 = new DtAlgeSet(makeDtalges(normalAlges));
		assertTrue(asempty.isEmpty());
		assertFalse(as1.isEmpty());
		
		//--- null
		try {
			asempty.addAll(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			as1.addAll(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		//--- add empty
		assertFalse(asempty.addAll(Collections.<Dtalge>emptyList()));
		assertFalse(as1.addAll(Collections.<Dtalge>emptyList()));
		assertTrue(asempty.isEmpty());
		assertTrue(equalElementSequence(as1, normalAlges));
		
		//--- with empty
		asempty = new DtAlgeSet();
		as1 = new DtAlgeSet(makeDtalges(normalAlges));
		assertTrue(asempty.isEmpty());
		assertFalse(as1.isEmpty());
		assertTrue(asempty.addAll(makeDtalges(withEmptyNormalAlges)));
		assertTrue(as1.addAll(makeDtalges(withEmptyNormalAlges)));
		assertTrue(equalElementSequence(asempty, withEmptyNormalAlges));
		assertTrue(equalElementSequence(as1, catElements(normalAlges, withEmptyNormalAlges)));
		
		//--- with null
		asempty = new DtAlgeSet();
		as1 = new DtAlgeSet(makeDtalges(normalAlges));
		assertTrue(asempty.isEmpty());
		assertFalse(as1.isEmpty());
		assertTrue(asempty.addAll(makeDtalges(withNullNormalAlges)));
		assertTrue(as1.addAll(makeDtalges(withNullNormalAlges)));
		assertTrue(equalElementSequence(asempty, normalAlges));
		assertTrue(equalElementSequence(as1, catElements(normalAlges, normalAlges)));
	}

	/**
	 * {@link dtalge.DtAlgeSet#addAll(int, java.util.Collection)} のためのテスト・メソッド。
	 */
	public void testAddAllIntCollectionOfQextendsDtalge() {
		DtAlgeSet asempty = new DtAlgeSet();
		DtAlgeSet as1 = new DtAlgeSet(makeDtalges(testAlges3));
		assertTrue(asempty.isEmpty());
		assertFalse(as1.isEmpty());
		
		//--- null
		try {
			asempty.addAll(0, null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			as1.addAll(0, null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		//--- Out of bounds
		try {
			asempty.add(2, new Dtalge());
			fail("must be throw IndexOutOfBoundsException.");
		} catch (IndexOutOfBoundsException ex) {
			assertTrue(true);
		}
		try {
			as1.add(as1.size()+1, new Dtalge());
			fail("must be throw IndexOutOfBoundsException.");
		} catch (IndexOutOfBoundsException ex) {
			assertTrue(true);
		}
		
		//--- add empty
		assertFalse(asempty.addAll(0, Collections.<Dtalge>emptyList()));
		assertFalse(as1.addAll(0, Collections.<Dtalge>emptyList()));
		assertTrue(asempty.isEmpty());
		assertTrue(equalElementSequence(as1, testAlges3));
		
		//--- add normal
		as1 = new DtAlgeSet();
		assertTrue(as1.isEmpty());
		assertTrue(as1.addAll(0, makeDtalges(testAlges3)));
		assertTrue(equalElementSequence(as1, testAlges3));
		//----------
		assertTrue(as1.addAll(0, makeDtalges(testAlges1)));
		assertTrue(equalElementSequence(as1, catElements(testAlges1, testAlges3)));
		//----------
		assertTrue(as1.addAll(testAlges1.length, makeDtalges(testAlges2)));
		assertTrue(equalElementSequence(as1, normalAlges));
		
		//--- with null
		asempty = new DtAlgeSet();
		as1 = new DtAlgeSet(makeDtalges(catElements(testAlges1, testAlges3)));
		assertTrue(asempty.isEmpty());
		assertFalse(as1.isEmpty());
		assertTrue(asempty.addAll(0, makeDtalges(withNullNormalAlges)));
		assertTrue(as1.addAll(testAlges1.length, makeDtalges(withNullNormalAlges)));
		assertTrue(equalElementSequence(asempty, normalAlges));
		assertTrue(equalElementSequence(as1, catAlges(testAlges1, normalAlges, testAlges3)));
	}

	/**
	 * {@link dtalge.DtAlgeSet#clone()} のためのテスト・メソッド。
	 */
	public void testClone() {
		DtAlgeSet as1 = new DtAlgeSet();
		DtAlgeSet clone1 = (DtAlgeSet)as1.clone();
		assertNotSame(as1, clone1);
		assertEquals(as1, clone1);
		assertTrue(as1.isEmpty());
		assertTrue(clone1.isEmpty());
		
		DtAlgeSet as2 = new DtAlgeSet(makeDtalges(testWithEmptyAlges1));
		assertTrue(equalElementSequence(as2, testWithEmptyAlges1));
		DtAlgeSet clone2 = (DtAlgeSet)as2.clone();
		assertNotSame(as2, clone2);
		assertEquals(as2, clone2);
		assertTrue(equalElementSequence(as2, testWithEmptyAlges1));
		assertTrue(equalElementSequence(clone2, testWithEmptyAlges1));
		
		Object[][] catAlges12 = catAlges(testWithEmptyAlges1, testWithEmptyAlges2);
		DtAlgeSet as3 = new DtAlgeSet(makeDtalges(testWithEmptyAlges2));
		assertTrue(equalElementSequence(as3, testWithEmptyAlges2));
		assertTrue(clone2.addAll(as3));
		assertNotSame(clone2, as3);
		assertFalse(as2.equals(clone2));
		assertFalse(as3.equals(clone2));
		assertTrue(equalElementSequence(as2, testWithEmptyAlges1));
		assertTrue(equalElementSequence(as3, testWithEmptyAlges2));
		assertTrue(equalElementSequence(clone2, catAlges12));
	}
	
	/**
	 * {@link dtalge.DtAlgeSet#sum()} のためのテスト・メソッド。
	 * @since 0.30
	 */
	public void testSum() {
		Dtalge empty1 = new Dtalge();
		Dtalge empty2 = new Dtalge();
		Dtalge empty3 = new Dtalge();
		Dtalge sumalge1 = new Dtalge(DtalgeTest.sumAlgeData1);
		Dtalge sumalge2 = new Dtalge(DtalgeTest.sumAlgeData2);
		Dtalge sumalge3 = new Dtalge(DtalgeTest.sumAlgeData3);
		Dtalge sumret123 = new Dtalge(DtalgeTest.sumRetData123);
		Dtalge sumret321 = new Dtalge(DtalgeTest.sumRetData321);
		Dtalge sumret213 = new Dtalge(DtalgeTest.sumRetData213);
		Dtalge sumret231 = new Dtalge(DtalgeTest.sumRetData231);
		DtAlgeSet algeset;
		Dtalge ret;
		
		//--- null
		try {
			Dtalge.sum(null);
			fail("Dtalge#sum(null) must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		// empty
		ret = Dtalge.sum(new DtAlgeSet());
		assertTrue(ret.isEmpty());
		algeset = new DtAlgeSet(Arrays.asList(empty1));
		ret = algeset.sum();
		assertTrue(ret.isEmpty());
		algeset = new DtAlgeSet(Arrays.asList(empty1, empty2, empty3));
		ret = algeset.sum();
		assertTrue(ret.isEmpty());
		
		// 123
		algeset = new DtAlgeSet(Arrays.asList(sumalge1, sumalge2, sumalge3));
		ret = algeset.sum();
		assertEquals(ret, sumret123);
		assertTrue(DtalgeTest.equalElementSequence(ret, DtalgeTest.sumRetData123));
		algeset = new DtAlgeSet(Arrays.asList(sumalge1, empty1, sumalge2, empty2, sumalge3, empty3));
		ret = algeset.sum();
		assertEquals(ret, sumret123);
		assertTrue(DtalgeTest.equalElementSequence(ret, DtalgeTest.sumRetData123));
		
		// 321
		algeset = new DtAlgeSet(Arrays.asList(sumalge3, sumalge2, sumalge1));
		ret = algeset.sum();
		assertEquals(ret, sumret321);
		assertTrue(DtalgeTest.equalElementSequence(ret, DtalgeTest.sumRetData321));
		algeset = new DtAlgeSet(Arrays.asList(empty1, sumalge3, empty2, sumalge2, empty3, sumalge1));
		ret = algeset.sum();
		assertEquals(ret, sumret321);
		assertTrue(DtalgeTest.equalElementSequence(ret, DtalgeTest.sumRetData321));
		
		// 213
		algeset = new DtAlgeSet(Arrays.asList(sumalge2, sumalge1, sumalge3));
		ret = algeset.sum();
		assertEquals(ret, sumret213);
		assertTrue(DtalgeTest.equalElementSequence(ret, DtalgeTest.sumRetData213));
		algeset = new DtAlgeSet(Arrays.asList(sumalge2, empty1, sumalge1, empty2, sumalge3));
		ret = algeset.sum();
		assertEquals(ret, sumret213);
		assertTrue(DtalgeTest.equalElementSequence(ret, DtalgeTest.sumRetData213));
		
		// 231
		algeset = new DtAlgeSet(Arrays.asList(sumalge2, sumalge3, sumalge1));
		ret = algeset.sum();
		assertEquals(ret, sumret231);
		assertTrue(DtalgeTest.equalElementSequence(ret, DtalgeTest.sumRetData231));
		algeset = new DtAlgeSet(Arrays.asList(empty3, sumalge2, empty1, sumalge3, empty2, sumalge1, empty3));
		ret = algeset.sum();
		assertEquals(ret, sumret231);
		assertTrue(DtalgeTest.equalElementSequence(ret, DtalgeTest.sumRetData231));
	}
	
	/**
	 * {@link dtalge.DtAlgeSet#toStringList(DtBase)} のためのテスト・メソッド。
	 * @since 0.30
	 */
	public void testToStringListDtBase() {
		DtAlgeSet emptyset = new DtAlgeSet();
		assertTrue(emptyset.isEmpty());
		DtAlgeSet algeset = new DtAlgeSet(makeDtalges(normalAlges));
		assertTrue(equalElementSequence(algeset, normalAlges));
		
		// check null
		try {
			emptyset.toStringList(null);
			fail("DtAlgeSet#toStringList(null) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			algeset.toStringList(null);
			fail("DtAlgeSet#toStringList(null) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		// check illegal data type
		for (DtBase base : testBases2) {
			if (!DtDataTypes.STRING.equals(base.getTypeKey())) {
				try {
					emptyset.toStringList(base);
					fail("DtAlgeSet#toStringList(" + base.toString() + ") must be throw exception.");
				} catch (IllegalArgumentException ex) {
					assertTrue(true);
				}
				try {
					algeset.toStringList(base);
					fail("DtAlgeSet#toStringList(" + base.toString() + ") must be throw exception.");
				} catch (IllegalArgumentException ex) {
					assertTrue(true);
				}
			}
		}
		
		// check empty
		for (DtBase base : testBases2) {
			if (DtDataTypes.STRING.equals(base.getTypeKey())) {
				List<String> ret = emptyset.toStringList(base);
				assertTrue(ret.isEmpty());
			}
		}
		
		// check
		for (DtBase base : testBases2) {
			if (DtDataTypes.STRING.equals(base.getTypeKey())) {
				List<String> ret = algeset.toStringList(base);
				List<String> org = getStringValues(normalAlges, base);
				assertEquals(ret, org);
			}
		}
	}
	
	/**
	 * {@link dtalge.DtAlgeSet#toDistinctStringList(DtBase)} のためのテスト・メソッド。
	 * @since 0.30
	 */
	public void testToDistinctStringListDtBase() {
		DtAlgeSet emptyset = new DtAlgeSet();
		assertTrue(emptyset.isEmpty());
		DtAlgeSet algeset = new DtAlgeSet(makeDtalges(normalAlges));
		assertTrue(equalElementSequence(algeset, normalAlges));
		
		// check null
		try {
			emptyset.toDistinctStringList(null);
			fail("DtAlgeSet#toDistinctStringList(null) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			algeset.toDistinctStringList(null);
			fail("DtAlgeSet#toDistinctStringList(null) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		// check illegal data type
		for (DtBase base : testBases2) {
			if (!DtDataTypes.STRING.equals(base.getTypeKey())) {
				try {
					emptyset.toDistinctStringList(base);
					fail("DtAlgeSet#toDistinctStringList(" + base.toString() + ") must be throw exception.");
				} catch (IllegalArgumentException ex) {
					assertTrue(true);
				}
				try {
					algeset.toDistinctStringList(base);
					fail("DtAlgeSet#toDistinctStringList(" + base.toString() + ") must be throw exception.");
				} catch (IllegalArgumentException ex) {
					assertTrue(true);
				}
			}
		}
		
		// check empty
		for (DtBase base : testBases2) {
			if (DtDataTypes.STRING.equals(base.getTypeKey())) {
				List<String> ret = emptyset.toDistinctStringList(base);
				assertTrue(ret.isEmpty());
			}
		}
		
		// check
		for (DtBase base : testBases2) {
			if (DtDataTypes.STRING.equals(base.getTypeKey())) {
				List<String> ret = algeset.toDistinctStringList(base);
				List<String> vals = getStringValues(normalAlges, base);
				List<String> org = new ArrayList<String>(new LinkedHashSet<String>(vals));
				System.out.println("DtAlgeSet#toDistinctStringList() - distinct size : " + org.size() + "/" + vals.size());
				assertEquals(ret, org);
			}
		}
	}
	
	/**
	 * {@link dtalge.DtAlgeSet#toDecimalList(DtBase)} のためのテスト・メソッド。
	 * @since 0.30
	 */
	public void testToDecimalListDtBase() {
		DtAlgeSet emptyset = new DtAlgeSet();
		assertTrue(emptyset.isEmpty());
		DtAlgeSet algeset = new DtAlgeSet(makeDtalges(normalAlges));
		assertTrue(equalElementSequence(algeset, normalAlges));
		
		// check null
		try {
			emptyset.toDecimalList(null);
			fail("DtAlgeSet#toDecimalList(null) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			algeset.toDecimalList(null);
			fail("DtAlgeSet#toDecimalList(null) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		// check illegal data type
		for (DtBase base : testBases2) {
			if (!DtDataTypes.DECIMAL.equals(base.getTypeKey())) {
				try {
					emptyset.toDecimalList(base);
					fail("DtAlgeSet#toDecimalList(" + base.toString() + ") must be throw exception.");
				} catch (IllegalArgumentException ex) {
					assertTrue(true);
				}
				try {
					algeset.toDecimalList(base);
					fail("DtAlgeSet#toDecimalList(" + base.toString() + ") must be throw exception.");
				} catch (IllegalArgumentException ex) {
					assertTrue(true);
				}
			}
		}
		
		// check empty
		for (DtBase base : testBases2) {
			if (DtDataTypes.DECIMAL.equals(base.getTypeKey())) {
				List<BigDecimal> ret = emptyset.toDecimalList(base);
				assertTrue(ret.isEmpty());
			}
		}
		
		// check
		for (DtBase base : testBases2) {
			if (DtDataTypes.DECIMAL.equals(base.getTypeKey())) {
				List<BigDecimal> ret = algeset.toDecimalList(base);
				List<BigDecimal> org = getDecimalValues(normalAlges, base);
				assertEquals(ret, org);
			}
		}
	}
	
	/**
	 * {@link dtalge.DtAlgeSet#toDistinctDecimalList(DtBase)} のためのテスト・メソッド。
	 * @since 0.30
	 */
	public void testToDistinctDecimalListDtBase() {
		DtAlgeSet emptyset = new DtAlgeSet();
		assertTrue(emptyset.isEmpty());
		DtAlgeSet algeset = new DtAlgeSet(makeDtalges(normalAlges));
		assertTrue(equalElementSequence(algeset, normalAlges));
		
		// check null
		try {
			emptyset.toDistinctDecimalList(null);
			fail("DtAlgeSet#toDistinctDecimalList(null) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			algeset.toDistinctDecimalList(null);
			fail("DtAlgeSet#toDistinctDecimalList(null) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		// check illegal data type
		for (DtBase base : testBases2) {
			if (!DtDataTypes.DECIMAL.equals(base.getTypeKey())) {
				try {
					emptyset.toDistinctDecimalList(base);
					fail("DtAlgeSet#toDistinctDecimalList(" + base.toString() + ") must be throw exception.");
				} catch (IllegalArgumentException ex) {
					assertTrue(true);
				}
				try {
					algeset.toDistinctDecimalList(base);
					fail("DtAlgeSet#toDistinctDecimalList(" + base.toString() + ") must be throw exception.");
				} catch (IllegalArgumentException ex) {
					assertTrue(true);
				}
			}
		}
		
		// check empty
		for (DtBase base : testBases2) {
			if (DtDataTypes.DECIMAL.equals(base.getTypeKey())) {
				List<BigDecimal> ret = emptyset.toDistinctDecimalList(base);
				assertTrue(ret.isEmpty());
			}
		}
		
		// check
		for (DtBase base : testBases2) {
			if (DtDataTypes.DECIMAL.equals(base.getTypeKey())) {
				List<BigDecimal> ret = algeset.toDistinctDecimalList(base);
				List<BigDecimal> org = new ArrayList<BigDecimal>();
				List<BigDecimal> vals = getDecimalValues(normalAlges, base);
				Set<BigDecimal> distvals = new LinkedHashSet<BigDecimal>();
				for (BigDecimal dval : vals) {
					BigDecimal stripped = (dval==null ? dval : dval.stripTrailingZeros());
					if (!distvals.contains(stripped)) {
						org.add(dval);
						distvals.add(stripped);
					}
				}
				System.out.println("DtAlgeSet#toDistinctDecimalList() - distinct size : " + org.size() + "/" + vals.size());
				assertEquals(ret, org);
			}
		}
	}
	
	/**
	 * {@link dtalge.DtAlgeSet#toBooleanList(DtBase)} のためのテスト・メソッド。
	 * @since 0.30
	 */
	public void testToBooleanListDtBase() {
		DtAlgeSet emptyset = new DtAlgeSet();
		assertTrue(emptyset.isEmpty());
		DtAlgeSet algeset = new DtAlgeSet(makeDtalges(normalAlges));
		assertTrue(equalElementSequence(algeset, normalAlges));
		
		// check null
		try {
			emptyset.toBooleanList(null);
			fail("DtAlgeSet#toBooleanList(null) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			algeset.toBooleanList(null);
			fail("DtAlgeSet#toBooleanList(null) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		// check illegal data type
		for (DtBase base : testBases2) {
			if (!DtDataTypes.BOOLEAN.equals(base.getTypeKey())) {
				try {
					emptyset.toBooleanList(base);
					fail("DtAlgeSet#toBooleanList(" + base.toString() + ") must be throw exception.");
				} catch (IllegalArgumentException ex) {
					assertTrue(true);
				}
				try {
					algeset.toBooleanList(base);
					fail("DtAlgeSet#toBooleanList(" + base.toString() + ") must be throw exception.");
				} catch (IllegalArgumentException ex) {
					assertTrue(true);
				}
			}
		}
		
		// check empty
		for (DtBase base : testBases2) {
			if (DtDataTypes.BOOLEAN.equals(base.getTypeKey())) {
				List<Boolean> ret = emptyset.toBooleanList(base);
				assertTrue(ret.isEmpty());
			}
		}
		
		// check
		for (DtBase base : testBases2) {
			if (DtDataTypes.BOOLEAN.equals(base.getTypeKey())) {
				List<Boolean> ret = algeset.toBooleanList(base);
				List<Boolean> org = getBooleanValues(normalAlges, base);
				assertEquals(ret, org);
			}
		}
	}
	
	/**
	 * {@link dtalge.DtAlgeSet#toDistinctBooleanList(DtBase)} のためのテスト・メソッド。
	 * @since 0.30
	 */
	public void testToDistinctBooleanListDtBase() {
		DtAlgeSet emptyset = new DtAlgeSet();
		assertTrue(emptyset.isEmpty());
		DtAlgeSet algeset = new DtAlgeSet(makeDtalges(normalAlges));
		assertTrue(equalElementSequence(algeset, normalAlges));
		
		// check null
		try {
			emptyset.toDistinctBooleanList(null);
			fail("DtAlgeSet#toDistinctBooleanList(null) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			algeset.toDistinctBooleanList(null);
			fail("DtAlgeSet#toDistinctBooleanList(null) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		// check illegal data type
		for (DtBase base : testBases2) {
			if (!DtDataTypes.BOOLEAN.equals(base.getTypeKey())) {
				try {
					emptyset.toDistinctBooleanList(base);
					fail("DtAlgeSet#toDistinctBooleanList(" + base.toString() + ") must be throw exception.");
				} catch (IllegalArgumentException ex) {
					assertTrue(true);
				}
				try {
					algeset.toDistinctBooleanList(base);
					fail("DtAlgeSet#toDistinctBooleanList(" + base.toString() + ") must be throw exception.");
				} catch (IllegalArgumentException ex) {
					assertTrue(true);
				}
			}
		}
		
		// check empty
		for (DtBase base : testBases2) {
			if (DtDataTypes.BOOLEAN.equals(base.getTypeKey())) {
				List<Boolean> ret = emptyset.toDistinctBooleanList(base);
				assertTrue(ret.isEmpty());
			}
		}
		
		// check
		for (DtBase base : testBases2) {
			if (DtDataTypes.BOOLEAN.equals(base.getTypeKey())) {
				List<Boolean> ret = algeset.toDistinctBooleanList(base);
				List<Boolean> vals = getBooleanValues(normalAlges, base);
				List<Boolean> org = new ArrayList<Boolean>(new LinkedHashSet<Boolean>(vals));
				System.out.println("DtAlgeSet#toDistinctBooleanList() - distinct size : " + org.size() + "/" + vals.size());
				assertEquals(ret, org);
			}
		}
	}
	
	/**
	 * {@link dtalge.DtAlgeSet#minValue(DtBase)} のためのテスト・メソッド。
	 * @since 0.30
	 */
	@SuppressWarnings("unchecked")
	public void testMinValueDtBase() {
		DtAlgeSet emptyset = new DtAlgeSet();
		assertTrue(emptyset.isEmpty());
		DtAlgeSet algeset = new DtAlgeSet(makeDtalges(normalAlges));
		assertTrue(equalElementSequence(algeset, normalAlges));
		
		// check null
		try {
			emptyset.minValue(null);
			fail("DtAlgeSet#minValue(null) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			algeset.minValue(null);
			fail("DtAlgeSet#minValue(null) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		// test empty
		for (DtBase base : testBases2) {
			Object val = emptyset.minValue(base);
			assertNull(val);
		}
		
		// test
		for (DtBase base : testBases2) {
			Object val = algeset.minValue(base);
			Object ans = null;
			for (Dtalge alge : algeset) {
				if (alge.containsBase(base)) {
					Object algevalue = alge.get(base);
					if (ans == null) {
						if (algevalue instanceof Comparable) {
							ans = algevalue;
						}
					}
					else if ((ans instanceof Comparable) && (algevalue instanceof Comparable)) {
						if (((Comparable)algevalue).compareTo(ans) < 0) {
							ans = algevalue;
						}
					}
				}
			}
			assertEquals(val, ans);
			System.out.println("DtAlgeSet#minValue = " + String.valueOf(val) + base.toString());
		}
	}
	
	/**
	 * {@link dtalge.DtAlgeSet#maxValue(DtBase)} のためのテスト・メソッド。
	 * @since 0.30
	 */
	@SuppressWarnings("unchecked")
	public void testMaxValueDtBase() {
		DtAlgeSet emptyset = new DtAlgeSet();
		assertTrue(emptyset.isEmpty());
		DtAlgeSet algeset = new DtAlgeSet(makeDtalges(normalAlges));
		assertTrue(equalElementSequence(algeset, normalAlges));
		
		// check null
		try {
			emptyset.maxValue(null);
			fail("DtAlgeSet#maxValue(null) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			algeset.maxValue(null);
			fail("DtAlgeSet#maxValue(null) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		// test empty
		for (DtBase base : testBases2) {
			Object val = emptyset.maxValue(base);
			assertNull(val);
		}
		
		// test
		for (DtBase base : testBases2) {
			Object val = algeset.maxValue(base);
			Object ans = null;
			for (Dtalge alge : algeset) {
				if (alge.containsBase(base)) {
					Object algevalue = alge.get(base);
					if (ans == null) {
						if (algevalue instanceof Comparable) {
							ans = algevalue;
						}
					}
					else if ((ans instanceof Comparable) && (algevalue instanceof Comparable)) {
						if (((Comparable)algevalue).compareTo(ans) > 0) {
							ans = algevalue;
						}
					}
				}
			}
			assertEquals(val, ans);
			System.out.println("DtAlgeSet#maxValue = " + String.valueOf(val) + base.toString());
		}
	}
	
	/**
	 * {@link dtalge.DtAlgeSet#minDecimal(DtBase)} のためのテスト・メソッド。
	 * @since 0.30
	 */
	public void testMinDecimalDtBase() {
		DtAlgeSet emptyset = new DtAlgeSet();
		assertTrue(emptyset.isEmpty());
		DtAlgeSet algeset = new DtAlgeSet(makeDtalges(normalAlges));
		assertTrue(equalElementSequence(algeset, normalAlges));
		
		// check null
		try {
			emptyset.minDecimal(null);
			fail("DtAlgeSet#minDecimal(null) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			algeset.minDecimal(null);
			fail("DtAlgeSet#minDecimal(null) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		// check illegal data type
		for (DtBase base : testBases2) {
			if (!DtDataTypes.DECIMAL.equals(base.getTypeKey())) {
				try {
					emptyset.minDecimal(base);
					fail("DtAlgeSet#minDecimal(" + base.toString() + ") must be throw exception.");
				} catch (IllegalArgumentException ex) {
					assertTrue(true);
				}
				try {
					algeset.minDecimal(base);
					fail("DtAlgeSet#minDecimal(" + base.toString() + ") must be throw exception.");
				} catch (IllegalArgumentException ex) {
					assertTrue(true);
				}
			}
		}
		
		// test empty
		for (DtBase base : testBases2) {
			if (DtDataTypes.DECIMAL.equals(base.getTypeKey())) {
				BigDecimal val = emptyset.minDecimal(base);
				assertNull(val);
			}
		}
		
		// test
		for (DtBase base : testBases2) {
			if (DtDataTypes.DECIMAL.equals(base.getTypeKey())) {
				BigDecimal val = algeset.minDecimal(base);
				BigDecimal ans = null;
				for (Dtalge alge : algeset) {
					if (alge.containsBase(base)) {
						BigDecimal algevalue = alge.getDecimal(base);
						if (ans == null) {
							if (algevalue != null) {
								ans = algevalue;
							}
						}
						else if (algevalue != null) {
							if (algevalue.compareTo(ans) < 0) {
								ans = algevalue;
							}
						}
					}
				}
				assertEquals(val, ans);
				System.out.println("DtAlgeSet#minDecimal = " + String.valueOf(val) + base.toString());
			}
		}
	}
	
	/**
	 * {@link dtalge.DtAlgeSet#maxDecimal(DtBase)} のためのテスト・メソッド。
	 * @since 0.30
	 */
	public void testMaxDecimalDtBase() {
		DtAlgeSet emptyset = new DtAlgeSet();
		assertTrue(emptyset.isEmpty());
		DtAlgeSet algeset = new DtAlgeSet(makeDtalges(normalAlges));
		assertTrue(equalElementSequence(algeset, normalAlges));
		
		// check null
		try {
			emptyset.maxDecimal(null);
			fail("DtAlgeSet#maxDecimal(null) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			algeset.maxDecimal(null);
			fail("DtAlgeSet#maxDecimal(null) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		// check illegal data type
		for (DtBase base : testBases2) {
			if (!DtDataTypes.DECIMAL.equals(base.getTypeKey())) {
				try {
					emptyset.maxDecimal(base);
					fail("DtAlgeSet#maxDecimal(" + base.toString() + ") must be throw exception.");
				} catch (IllegalArgumentException ex) {
					assertTrue(true);
				}
				try {
					algeset.maxDecimal(base);
					fail("DtAlgeSet#maxDecimal(" + base.toString() + ") must be throw exception.");
				} catch (IllegalArgumentException ex) {
					assertTrue(true);
				}
			}
		}
		
		// test empty
		for (DtBase base : testBases2) {
			if (DtDataTypes.DECIMAL.equals(base.getTypeKey())) {
				BigDecimal val = emptyset.maxDecimal(base);
				assertNull(val);
			}
		}
		
		// test
		for (DtBase base : testBases2) {
			if (DtDataTypes.DECIMAL.equals(base.getTypeKey())) {
				BigDecimal val = algeset.maxDecimal(base);
				BigDecimal ans = null;
				for (Dtalge alge : algeset) {
					if (alge.containsBase(base)) {
						BigDecimal algevalue = alge.getDecimal(base);
						if (ans == null) {
							if (algevalue != null) {
								ans = algevalue;
							}
						}
						else if (algevalue != null) {
							if (algevalue.compareTo(ans) > 0) {
								ans = algevalue;
							}
						}
					}
				}
				assertEquals(val, ans);
				System.out.println("DtAlgeSet#maxDecimal = " + String.valueOf(val) + base.toString());
			}
		}
	}
	
	/**
	 * {@link dtalge.DtAlgeSet#containsValue(DtBase, Object)} のためのテスト・メソッド。
	 * @since 0.30
	 */
	public void testContainsValueDtBaseObject() {
		DtAlgeSet emptyset = new DtAlgeSet();
		assertTrue(emptyset.isEmpty());
		DtAlgeSet algeset = new DtAlgeSet(makeDtalges(normalAlges));
		assertTrue(equalElementSequence(algeset, normalAlges));
		
		Object[][] ansdata = {
				{ true, new DtBase("名前","string","#","#"), "馬場", },
				{ true, new DtBase("性別","string","#","#"), "男性", },
				{ true, new DtBase("年齢","decimal","#","#"), new BigDecimal("41"), },
				{ true, new DtBase("都道府県","string","#","#"), "宮城県", },
				{ true, new DtBase("注目食品","string","#","#"), "まぐろのたたきご飯", },
				{ true, new DtBase("ポイント","decimal","#","#"), new BigDecimal("123.456"), },
				{ true, new DtBase("フラグ","boolean","#","#"), null, },
				{ true, new DtBase("備考","string","#","#"), null, },
				{ true, new DtBase("名前","string","#","#"), "藤原", },
				{ true, new DtBase("性別","string","#","#"), "男性", },
				{ true, new DtBase("年齢","decimal","#","#"), new BigDecimal("48"), },
				{ true, new DtBase("都道府県","string","#","#"), "長崎県", },
				{ true, new DtBase("注目食品","string","#","#"), "ハンバーグ＆白身魚フライ弁当", },
				{ true, new DtBase("ポイント","decimal","#","#"), new BigDecimal("-987654321.0123456789"), },
				{ true, new DtBase("フラグ","boolean","#","#"), Boolean.FALSE, },
				{ true, new DtBase("備考","string","#","#"), null, },
				{ true, new DtBase("名前","string","#","#"), "萩原", },
				{ true, new DtBase("性別","string","#","#"), "女性", },
				{ true, new DtBase("年齢","decimal","#","#"), new BigDecimal("25"), },
				{ true, new DtBase("都道府県","string","#","#"), "熊本県", },
				{ true, new DtBase("注目食品","string","#","#"), "若鶏のグリルガーリックソース", },
				{ true, new DtBase("ポイント","decimal","#","#"), new BigDecimal("987654321987654321.01234567890123456"), },
				{ true, new DtBase("フラグ","boolean","#","#"), null, },
				{ true, new DtBase("備考","string","#","#"), null, },
				{ false, new DtBase("Type", DtDataTypes.STRING), "C", },
				{ false, new DtBase("Task", DtDataTypes.STRING), "P3", },
				{ false, new DtBase("Flag", DtDataTypes.BOOLEAN), Boolean.FALSE, },
				{ false, new DtBase("Value", DtDataTypes.DECIMAL), new BigDecimal("3"), },
				{ false, new DtBase("Type", DtDataTypes.STRING), "D", },
				{ false, new DtBase("Task", DtDataTypes.STRING), "P4", },
				{ false, new DtBase("Flag", DtDataTypes.BOOLEAN), Boolean.FALSE, },
				{ false, new DtBase("Value", DtDataTypes.DECIMAL), new BigDecimal("4"), },
				{ false, new DtBase("Type", DtDataTypes.STRING), "E", },
				{ false, new DtBase("Task", DtDataTypes.STRING), "P5", },
				{ false, new DtBase("Flag", DtDataTypes.BOOLEAN), Boolean.FALSE, },
				{ false, new DtBase("Value", DtDataTypes.DECIMAL), new BigDecimal("5"), },
				{ false, new DtBase("名前","string","#","#"), "じゅげむじゅげむ", },
				{ false, new DtBase("性別","string","#","#"), "性別不明", },
				{ false, new DtBase("年齢","decimal","#","#"), new BigDecimal("130"), },
				{ false, new DtBase("都道府県","string","#","#"), "くーすべい", },
				{ false, new DtBase("注目食品","string","#","#"), "般若弁当", },
				{ false, new DtBase("ポイント","decimal","#","#"), new BigDecimal("-213481234.0123456789"), },
				{ false, new DtBase("備考","string","#","#"), "ぺけぽん", },
		};
		
		// check null
		assertFalse(emptyset.containsValue(null, null));
		assertFalse(algeset.containsValue(null, null));
		
		// test
		for (Object[] anspack : ansdata) {
			boolean ansret  = (Boolean)anspack[0];
			DtBase   ansbase = (DtBase)anspack[1];
			Object   ansval  = anspack[2];
			
			// emptyset
			assertFalse(emptyset.containsValue(ansbase, ansval));
			
			// algeset
			if (ansret) {
				assertTrue(algeset.containsValue(ansbase, ansval));
			} else {
				assertFalse(algeset.containsValue(ansbase, ansval));
			}
		}
	}
	
	/**
	 * {@link dtalge.DtAlgeSet#containsAnyValues(DtBase, Collection)} のためのテスト・メソッド。
	 * @since 0.30
	 */
	public void testContainsAnyValuesDtBaseCollectionOfObject() {
		DtAlgeSet emptyset = new DtAlgeSet();
		assertTrue(emptyset.isEmpty());
		DtAlgeSet algeset = new DtAlgeSet(makeDtalges(normalAlges));
		assertTrue(equalElementSequence(algeset, normalAlges));
		
		DtBase[] existBases = {
				new DtBase("名前","string","#","#"),
				new DtBase("性別","string","#","#"),
				new DtBase("年齢","decimal","#","#"),
				new DtBase("都道府県","string","#","#"),
				new DtBase("注目食品","string","#","#"),
				new DtBase("ポイント","decimal","#","#"),
				new DtBase("フラグ","boolean","#","#"),
				new DtBase("備考","string","#","#"),
		};
		
		DtBase[] notExistBases = {
				new DtBase("Type", DtDataTypes.STRING),
				new DtBase("Task", DtDataTypes.STRING),
				new DtBase("Flag", DtDataTypes.BOOLEAN),
				new DtBase("Value", DtDataTypes.DECIMAL),
				new DtBase("Type", DtDataTypes.STRING),
				new DtBase("Task", DtDataTypes.STRING),
				new DtBase("Flag", DtDataTypes.BOOLEAN),
				new DtBase("Value", DtDataTypes.DECIMAL),
				new DtBase("Type", DtDataTypes.STRING),
				new DtBase("Task", DtDataTypes.STRING),
				new DtBase("Flag", DtDataTypes.BOOLEAN),
				new DtBase("Value", DtDataTypes.DECIMAL),
		};
		
		Object[] existValues = {
				"馬場",
				"男性",
				new BigDecimal("41"),
				"宮城県",
				"まぐろのたたきご飯",
				new BigDecimal("123.456"),
				null,
				null,
				"藤原",
				"男性",
				new BigDecimal("48"),
				"長崎県",
				"ハンバーグ＆白身魚フライ弁当",
				new BigDecimal("-987654321.0123456789"),
				Boolean.FALSE,
				null,
				"萩原",
				"女性",
				new BigDecimal("25"),
				"熊本県",
				"若鶏のグリルガーリックソース",
				new BigDecimal("987654321987654321.01234567890123456"),
				null,
				null,
				"C",
				"P3",
				Boolean.FALSE,
				new BigDecimal("3"),
				"D",
				"P4",
				Boolean.FALSE,
				new BigDecimal("4"),
				"E",
				"P5",
				Boolean.FALSE,
				new BigDecimal("5"),
				"じゅげむじゅげむ",
				"性別不明",
				new BigDecimal("130"),
				"くーすべい",
				"般若弁当",
				new BigDecimal("-213481234.0123456789"),
				"ぺけぽん",
		};
		
		Object[] notExistValues = {
				"C",
				"P3",
				new BigDecimal("3"),
				"D",
				"P4",
				new BigDecimal("4"),
				"E",
				"P5",
				"じゅげむじゅげむ",
				"性別不明",
				new BigDecimal("130"),
				"くーすべい",
				"般若弁当",
				new BigDecimal("-213481234.0123456789"),
				"ぺけぽん",
		};
		
		// check null
		assertFalse(emptyset.containsAnyValues(null, null));
		assertFalse(emptyset.containsAnyValues(existBases[0], null));
		assertFalse(emptyset.containsAnyValues(null, Arrays.asList(existValues)));
		assertFalse(emptyset.containsAnyValues(existBases[0], Collections.EMPTY_LIST));
		assertFalse(algeset.containsAnyValues(null, null));
		assertFalse(algeset.containsAnyValues(existBases[0], null));
		assertFalse(algeset.containsAnyValues(null, Arrays.asList(existValues)));
		assertFalse(algeset.containsAnyValues(existBases[0], Collections.EMPTY_LIST));
		
		// test
		for (DtBase base : existBases) {
			assertFalse(emptyset.containsAnyValues(base, Arrays.asList(existValues)));
			assertFalse(emptyset.containsAnyValues(base, Arrays.asList(notExistValues)));
			assertTrue(algeset.containsAnyValues(base, Arrays.asList(existValues)));
			assertFalse(algeset.containsAnyValues(base, Arrays.asList(notExistValues)));
		}
		
		for (DtBase base : notExistBases) {
			assertFalse(emptyset.containsAnyValues(base, Arrays.asList(existValues)));
			assertFalse(emptyset.containsAnyValues(base, Arrays.asList(notExistValues)));
			assertFalse(algeset.containsAnyValues(base, Arrays.asList(existValues)));
			assertFalse(algeset.containsAnyValues(base, Arrays.asList(notExistValues)));
		}
	}
	
	/**
	 * {@link dtalge.DtAlgeSet#selectEqualValue(DtBase, Object)} のためのテスト・メソッド。
	 * @since 0.30
	 */
	public void testSelectEqualValueDtBaseObject() {
		DtAlgeSet emptyset = new DtAlgeSet();
		assertTrue(emptyset.isEmpty());
		DtAlgeSet algeset = new DtAlgeSet(makeDtalges(normalAlges));
		assertTrue(equalElementSequence(algeset, normalAlges));
		
		// check null
		try {
			emptyset.selectEqualValue(null, "test");
			fail("DtAlgeSet#selectEqualValue(null, obj) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			algeset.selectEqualValue(null, "test");
			fail("DtAlgeSet#selectEqualValue(null, obj) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		for (int i = 0; i < selectTestData.length; i+=2) {
			DtBase base = (DtBase)selectTestData[i];
			Object val  = selectTestData[i+1];
			
			DtAlgeSet ret = emptyset.selectEqualValue(base, val);
			assertTrue(ret.isEmpty());
			
			DtAlgeSet ans = new DtAlgeSet();
			for (Dtalge alge : algeset) {
				if (alge.containsBase(base)) {
					Object algevalue = alge.get(base);
					if (val == null) {
						if (algevalue == null) {
							ans.add(alge);
						}
					}
					else if (val instanceof BigDecimal) {
						if ((algevalue instanceof BigDecimal) && 0==((BigDecimal)val).compareTo((BigDecimal)algevalue)) {
							ans.add(alge);
						}
					}
					else {
						if (val.equals(algevalue)) {
							ans.add(alge);
						}
					}
				}
			}
			
			ret = algeset.selectEqualValue(base, val);
			assertEquals(ret, ans);
		}
	}
	
	/**
	 * {@link dtalge.DtAlgeSet#selectNotEqualValue(DtBase, Object)} のためのテスト・メソッド。
	 * @since 0.30
	 */
	public void testSelectNotEqualValueDtBaseObject() {
		DtAlgeSet emptyset = new DtAlgeSet();
		assertTrue(emptyset.isEmpty());
		DtAlgeSet algeset = new DtAlgeSet(makeDtalges(normalAlges));
		assertTrue(equalElementSequence(algeset, normalAlges));
		
		// check null
		try {
			emptyset.selectNotEqualValue(null, "test");
			fail("DtAlgeSet#selectNotEqualValue(null, obj) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			algeset.selectNotEqualValue(null, "test");
			fail("DtAlgeSet#selectNotEqualValue(null, obj) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		for (int i = 0; i < selectTestData.length; i+=2) {
			DtBase base = (DtBase)selectTestData[i];
			Object val  = selectTestData[i+1];
			
			DtAlgeSet ret = emptyset.selectNotEqualValue(base, val);
			assertTrue(ret.isEmpty());
			
			DtAlgeSet ans = new DtAlgeSet(algeset);
			for (int j = ans.size()-1; j >= 0; j--) {
				Dtalge alge = ans.get(j);
				if (alge.containsBase(base)) {
					Object algevalue = alge.get(base);
					if (val == null) {
						if (algevalue == null) {
							ans.remove(j);
						}
					}
					else if (val instanceof BigDecimal) {
						if ((algevalue instanceof BigDecimal) && 0==((BigDecimal)val).compareTo((BigDecimal)algevalue)) {
							ans.remove(j);
						}
					}
					else {
						if (val.equals(algevalue)) {
							ans.remove(j);
						}
					}
				}
			}
			
			ret = algeset.selectNotEqualValue(base, val);
			assertEquals(ret, ans);
		}
	}
	
	/**
	 * {@link dtalge.DtAlgeSet#selectLessThanValue(DtBase, Object)} のためのテスト・メソッド。
	 * @since 0.30
	 */
	@SuppressWarnings("unchecked")
	public void testSelectLessThanValueDtBaseObject() {
		DtAlgeSet emptyset = new DtAlgeSet();
		assertTrue(emptyset.isEmpty());
		DtAlgeSet algeset = new DtAlgeSet(makeDtalges(normalAlges));
		assertTrue(equalElementSequence(algeset, normalAlges));
		
		// check null
		try {
			emptyset.selectLessThanValue(null, "test");
			fail("DtAlgeSet#selectLessThanValue(null, obj) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			algeset.selectLessThanValue(null, "test");
			fail("DtAlgeSet#selectLessThanValue(null, obj) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		for (int i = 0; i < selectTestData.length; i+=2) {
			DtBase base = (DtBase)selectTestData[i];
			Object val  = selectTestData[i+1];
			
			DtAlgeSet ret = emptyset.selectLessThanValue(base, val);
			assertTrue(ret.isEmpty());
			
			DtAlgeSet ans = new DtAlgeSet();
			for (Dtalge alge : algeset) {
				if (alge.containsBase(base)) {
					Object algevalue = alge.get(base);
					if ((val instanceof Comparable) && (algevalue instanceof Comparable)) {
						int cmp = ((Comparable)algevalue).compareTo(val);
						if (cmp < 0) {
							ans.add(alge);
						}
					}
				}
			}
			
			ret = algeset.selectLessThanValue(base, val);
			assertEquals(ret, ans);
		}
	}
	
	/**
	 * {@link dtalge.DtAlgeSet#selectLessEqualValue(DtBase, Object)} のためのテスト・メソッド。
	 * @since 0.30
	 */
	@SuppressWarnings("unchecked")
	public void testSelectLessEqualValueDtBaseObject() {
		DtAlgeSet emptyset = new DtAlgeSet();
		assertTrue(emptyset.isEmpty());
		DtAlgeSet algeset = new DtAlgeSet(makeDtalges(normalAlges));
		assertTrue(equalElementSequence(algeset, normalAlges));
		
		// check null
		try {
			emptyset.selectLessEqualValue(null, "test");
			fail("DtAlgeSet#selectLessEqualValue(null, obj) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			algeset.selectLessEqualValue(null, "test");
			fail("DtAlgeSet#selectLessEqualValue(null, obj) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		for (int i = 0; i < selectTestData.length; i+=2) {
			DtBase base = (DtBase)selectTestData[i];
			Object val  = selectTestData[i+1];
			
			DtAlgeSet ret = emptyset.selectLessEqualValue(base, val);
			assertTrue(ret.isEmpty());
			
			DtAlgeSet ans = new DtAlgeSet();
			for (Dtalge alge : algeset) {
				if (alge.containsBase(base)) {
					Object algevalue = alge.get(base);
					if ((val instanceof Comparable) && (algevalue instanceof Comparable)) {
						int cmp = ((Comparable)algevalue).compareTo(val);
						if (cmp <= 0) {
							ans.add(alge);
						}
					}
				}
			}
			
			ret = algeset.selectLessEqualValue(base, val);
			assertEquals(ret, ans);
		}
	}
	
	/**
	 * {@link dtalge.DtAlgeSet#selectGreaterThanValue(DtBase, Object)} のためのテスト・メソッド。
	 * @since 0.30
	 */
	@SuppressWarnings("unchecked")
	public void testSelectGreaterThanValueDtBaseObject() {
		DtAlgeSet emptyset = new DtAlgeSet();
		assertTrue(emptyset.isEmpty());
		DtAlgeSet algeset = new DtAlgeSet(makeDtalges(normalAlges));
		assertTrue(equalElementSequence(algeset, normalAlges));
		
		// check null
		try {
			emptyset.selectGreaterThanValue(null, "test");
			fail("DtAlgeSet#selectGreaterThanValue(null, obj) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			algeset.selectGreaterThanValue(null, "test");
			fail("DtAlgeSet#selectGreaterThanValue(null, obj) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		for (int i = 0; i < selectTestData.length; i+=2) {
			DtBase base = (DtBase)selectTestData[i];
			Object val  = selectTestData[i+1];
			
			DtAlgeSet ret = emptyset.selectGreaterThanValue(base, val);
			assertTrue(ret.isEmpty());
			
			DtAlgeSet ans = new DtAlgeSet();
			for (Dtalge alge : algeset) {
				if (alge.containsBase(base)) {
					Object algevalue = alge.get(base);
					if ((val instanceof Comparable) && (algevalue instanceof Comparable)) {
						int cmp = ((Comparable)algevalue).compareTo(val);
						if (cmp > 0) {
							ans.add(alge);
						}
					}
				}
			}
			
			ret = algeset.selectGreaterThanValue(base, val);
			assertEquals(ret, ans);
		}
	}
	
	/**
	 * {@link dtalge.DtAlgeSet#selectGreaterEqualValue(DtBase, Object)} のためのテスト・メソッド。
	 * @since 0.30
	 */
	@SuppressWarnings("unchecked")
	public void testSelectGreaterEqualValueDtBaseObject() {
		DtAlgeSet emptyset = new DtAlgeSet();
		assertTrue(emptyset.isEmpty());
		DtAlgeSet algeset = new DtAlgeSet(makeDtalges(normalAlges));
		assertTrue(equalElementSequence(algeset, normalAlges));
		
		// check null
		try {
			emptyset.selectGreaterEqualValue(null, "test");
			fail("DtAlgeSet#selectGreaterEqualValue(null, obj) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			algeset.selectGreaterEqualValue(null, "test");
			fail("DtAlgeSet#selectGreaterEqualValue(null, obj) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		for (int i = 0; i < selectTestData.length; i+=2) {
			DtBase base = (DtBase)selectTestData[i];
			Object val  = selectTestData[i+1];
			
			DtAlgeSet ret = emptyset.selectGreaterEqualValue(base, val);
			assertTrue(ret.isEmpty());
			
			DtAlgeSet ans = new DtAlgeSet();
			for (Dtalge alge : algeset) {
				if (alge.containsBase(base)) {
					Object algevalue = alge.get(base);
					if ((val instanceof Comparable) && (algevalue instanceof Comparable)) {
						int cmp = ((Comparable)algevalue).compareTo(val);
						if (cmp >= 0) {
							ans.add(alge);
						}
					}
				}
			}
			
			ret = algeset.selectGreaterEqualValue(base, val);
			assertEquals(ret, ans);
		}
	}
	
	/**
	 * {@link dtalge.DtAlgeSet#replaceEqualValue(DtBase, Object, Dtalge)} のためのテスト・メソッド。
	 * @since 0.30
	 */
	public void testReplaceEqualValueDtBaseObjectDtalge() {
		DtAlgeSet emptyset = new DtAlgeSet();
		assertTrue(emptyset.isEmpty());
		DtAlgeSet algeset = new DtAlgeSet(makeDtalges(normalAlges));
		assertTrue(equalElementSequence(algeset, normalAlges));
		DtBase newBase = new DtBase("hoge", DtDataTypes.STRING, "#","#");
		Dtalge newAlge = new Dtalge(newBase, "ほげ～");
		
		// check null
		try {
			emptyset.replaceEqualValue(null, "test", newAlge);
			fail("DtAlgeSet#replaceEqualValue(null, obj, obj) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			emptyset.replaceEqualValue(newBase, "test", null);
			fail("DtAlgeSet#replaceEqualValue(obj, obj, null) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			algeset.replaceEqualValue(null, "test", newAlge);
			fail("DtAlgeSet#replaceEqualValue(null, obj, obj) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			algeset.replaceEqualValue(newBase, "test", null);
			fail("DtAlgeSet#replaceEqualValue(obj, obj, null) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		for (int i = 0; i < selectTestData.length; i+=2) {
			DtBase base = (DtBase)selectTestData[i];
			Object val  = selectTestData[i+1];
			
			DtAlgeSet ret = emptyset.replaceEqualValue(base, val, newAlge);
			assertTrue(ret.isEmpty());
			
			DtAlgeSet ans = new DtAlgeSet();
			for (Dtalge alge : algeset) {
				if (alge.containsBase(base)) {
					Object algevalue = alge.get(base);
					if (val == null) {
						if (algevalue == null) {
							alge = newAlge;
						}
					}
					else if (val instanceof BigDecimal) {
						if ((algevalue instanceof BigDecimal) && 0==((BigDecimal)val).compareTo((BigDecimal)algevalue)) {
							alge = newAlge;
						}
					}
					else {
						if (val.equals(algevalue)) {
							alge = newAlge;
						}
					}
				}
				ans.add(alge);
			}
			
			ret = algeset.replaceEqualValue(base, val, newAlge);
			assertEquals(ret, ans);
		}
	}
	
	/**
	 * {@link dtalge.DtAlgeSet#updateEqualValue(DtBase, Object, Dtalge)} のためのテスト・メソッド。
	 * @since 0.30
	 */
	public void testUpdateEqualValueDtBaseObjectDtalge() {
		DtAlgeSet emptyset = new DtAlgeSet();
		assertTrue(emptyset.isEmpty());
		DtAlgeSet algeset = new DtAlgeSet(makeDtalges(normalAlges));
		assertTrue(equalElementSequence(algeset, normalAlges));
		DtBase newBase = new DtBase("hoge", DtDataTypes.STRING, "#","#");
		Dtalge newAlge = new Dtalge(newBase, "ほげ～");
		
		// check null
		try {
			emptyset.updateEqualValue(null, "test", newAlge);
			fail("DtAlgeSet#updateEqualValue(null, obj, obj) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			emptyset.updateEqualValue(newBase, "test", null);
			fail("DtAlgeSet#updateEqualValue(obj, obj, null) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			algeset.updateEqualValue(null, "test", newAlge);
			fail("DtAlgeSet#updateEqualValue(null, obj, obj) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			algeset.updateEqualValue(newBase, "test", null);
			fail("DtAlgeSet#updateEqualValue(obj, obj, null) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		for (int i = 0; i < selectTestData.length; i+=2) {
			DtBase base = (DtBase)selectTestData[i];
			Object val  = selectTestData[i+1];

			DtAlgeSet org = new DtAlgeSet(emptyset);
			boolean ret = org.updateEqualValue(base, val, newAlge);
			if (emptyset.equals(org)) {
				assertFalse(ret);
			} else {
				assertTrue(ret);
			}
			
			DtAlgeSet ans = new DtAlgeSet();
			for (Dtalge alge : algeset) {
				if (alge.containsBase(base)) {
					Object algevalue = alge.get(base);
					if (val == null) {
						if (algevalue == null) {
							alge = newAlge;
						}
					}
					else if (val instanceof BigDecimal) {
						if ((algevalue instanceof BigDecimal) && 0==((BigDecimal)val).compareTo((BigDecimal)algevalue)) {
							alge = newAlge;
						}
					}
					else {
						if (val.equals(algevalue)) {
							alge = newAlge;
						}
					}
				}
				ans.add(alge);
			}

			org = new DtAlgeSet(algeset);
			ret = org.updateEqualValue(base, val, newAlge);
			if (algeset.equals(org)) {
				assertFalse(ret);
			} else {
				assertTrue(ret);
			}
		}
	}
	
	/**
	 * {@link dtalge.DtAlgeSet#removeEqualValue(DtBase, Object)} のためのテスト・メソッド。
	 * @since 0.30
	 */
	public void testRemoveEqualValueDtBaseObject() {
		DtAlgeSet emptyset = new DtAlgeSet();
		assertTrue(emptyset.isEmpty());
		DtAlgeSet algeset = new DtAlgeSet(makeDtalges(normalAlges));
		assertTrue(equalElementSequence(algeset, normalAlges));
		
		// check null
		try {
			emptyset.removeEqualValue(null, "test");
			fail("DtAlgeSet#removeEqualValue(null, obj) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			algeset.removeEqualValue(null, "test");
			fail("DtAlgeSet#removeEqualValue(null, obj) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		for (int i = 0; i < selectTestData.length; i+=2) {
			DtBase base = (DtBase)selectTestData[i];
			Object val  = selectTestData[i+1];
			
			DtAlgeSet ret = emptyset.removeEqualValue(base, val);
			assertTrue(ret.isEmpty());
			
			DtAlgeSet ans = new DtAlgeSet(algeset);
			for (int j = ans.size()-1; j >= 0; j--) {
				Dtalge alge = ans.get(j);
				if (alge.containsBase(base)) {
					Object algevalue = alge.get(base);
					if (val == null) {
						if (algevalue == null) {
							ans.remove(j);
						}
					}
					else if (val instanceof BigDecimal) {
						if ((algevalue instanceof BigDecimal) && 0==((BigDecimal)val).compareTo((BigDecimal)algevalue)) {
							ans.remove(j);
						}
					}
					else {
						if (val.equals(algevalue)) {
							ans.remove(j);
						}
					}
				}
			}
			
			ret = algeset.removeEqualValue(base, val);
			assertEquals(ret, ans);
		}
	}
	
	/**
	 * {@link dtalge.DtAlgeSet#deleteEqualValue(DtBase, Object)} のためのテスト・メソッド。
	 * @since 0.30
	 */
	public void testDeleteEqualValueDtBaseObject() {
		DtAlgeSet emptyset = new DtAlgeSet();
		assertTrue(emptyset.isEmpty());
		DtAlgeSet algeset = new DtAlgeSet(makeDtalges(normalAlges));
		assertTrue(equalElementSequence(algeset, normalAlges));
		
		// check null
		try {
			emptyset.deleteEqualValue(null, "test");
			fail("DtAlgeSet#deleteEqualValue(null, obj) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			algeset.deleteEqualValue(null, "test");
			fail("DtAlgeSet#deleteEqualValue(null, obj) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		for (int i = 0; i < selectTestData.length; i+=2) {
			DtBase base = (DtBase)selectTestData[i];
			Object val  = selectTestData[i+1];

			DtAlgeSet org = new DtAlgeSet(emptyset);
			boolean ret = org.deleteEqualValue(base, val);
			if (emptyset.equals(org)) {
				assertFalse(ret);
			} else {
				assertTrue(ret);
			}
			
			DtAlgeSet ans = new DtAlgeSet(algeset);
			for (int j = ans.size()-1; j >= 0; j--) {
				Dtalge alge = ans.get(j);
				if (alge.containsBase(base)) {
					Object algevalue = alge.get(base);
					if (val == null) {
						if (algevalue == null) {
							ans.remove(j);
						}
					}
					else if (val instanceof BigDecimal) {
						if ((algevalue instanceof BigDecimal) && 0==((BigDecimal)val).compareTo((BigDecimal)algevalue)) {
							ans.remove(j);
						}
					}
					else {
						if (val.equals(algevalue)) {
							ans.remove(j);
						}
					}
				}
			}

			org = new DtAlgeSet(algeset);
			ret = org.deleteEqualValue(base, val);
			if (algeset.equals(org)) {
				assertFalse(ret);
			} else {
				assertTrue(ret);
			}
		}
	}
	
	/**
	 * {@link dtalge.DtAlgeSet#selectThesaurusMax(DtBase, DtStringThesaurus)} のためのテスト・メソッド。
	 * @since 0.30
	 */
	public void testSelectThesaurusMaxDtBaseDtStringThesaurus() {
		// シソーラス定義の生成
		DtStringThesaurus thes0 = new DtStringThesaurus();
		assertTrue(thes0.isEmpty());
		DtStringThesaurus thes1 = DtStringThesaurusTest.newThesaurus(thesMPPairs1);
		assertEquals((thesMPPairs1.length/2), thes1.size());
		for (int i = 0; i < thesMPPairs1.length; i+=2) {
			String parent = thesMPPairs1[i];
			String child  = thesMPPairs1[i+1];
			assertTrue(thes1.isComparable(parent, child));
			assertTrue(thes1.isComparable(child, parent));
			assertFalse(thes1.isComparable(parent, parent));
			assertFalse(thes1.isComparable(child, child));
		}
		DtStringThesaurus thes2 = DtStringThesaurusTest.newThesaurus(thesMPPairs2);
		assertEquals((thesMPPairs2.length/2), thes2.size());
		for (int i = 0; i < thesMPPairs2.length; i+=2) {
			String parent = thesMPPairs2[i];
			String child  = thesMPPairs2[i+1];
			assertTrue(thes2.isComparable(parent, child));
			assertTrue(thes2.isComparable(child, parent));
			assertFalse(thes2.isComparable(parent, parent));
			assertFalse(thes2.isComparable(child, child));
		}
		
		// make data
		DtAlgeSet taset1 = new DtAlgeSet(makeDtalges(thesAlges1));
		assertTrue(equalElementSequence(taset1, thesAlges1));
		DtAlgeSet taset2 = new DtAlgeSet(makeDtalges(thesAlges2));
		assertTrue(equalElementSequence(taset2, thesAlges2));
		DtAlgeSet taset3 = new DtAlgeSet(makeDtalges(thesAlges3));
		assertTrue(equalElementSequence(taset3, thesAlges3));
		DtAlgeSet tamax1 = new DtAlgeSet(makeDtalges(thesSelectMax1));
		assertTrue(equalElementSequence(tamax1, thesSelectMax1));
		DtAlgeSet tamin1 = new DtAlgeSet(makeDtalges(thesSelectMin1));
		assertTrue(equalElementSequence(tamin1, thesSelectMin1));
		DtAlgeSet tamax2 = new DtAlgeSet(makeDtalges(thesSelectMax2));
		assertTrue(equalElementSequence(tamax2, thesSelectMax2));
		DtAlgeSet tamin2 = new DtAlgeSet(makeDtalges(thesSelectMin2));
		assertTrue(equalElementSequence(tamin2, thesSelectMin2));
		DtAlgeSet tamax3 = new DtAlgeSet(makeDtalges(thesSelectMax3));
		assertTrue(equalElementSequence(tamax3, thesSelectMax3));
		DtAlgeSet tamin3 = new DtAlgeSet(makeDtalges(thesSelectMin3));
		assertTrue(equalElementSequence(tamin3, thesSelectMin3));
		DtAlgeSet naset = new DtAlgeSet(makeDtalges(normalAlges));
		assertTrue(equalElementSequence(naset, normalAlges));
		DtAlgeSet emptyset = new DtAlgeSet();

		DtBase basena = new DtBase("名前","string","#","#");
		DtBase basetask = new DtBase("Task", DtDataTypes.STRING);
		DtBase baseflag = new DtBase("Flag", DtDataTypes.BOOLEAN);
		DtBase basevalue = new DtBase("Value", DtDataTypes.DECIMAL);
		
		// make patterns
		Object[][] testpatterns = new Object[][]{
				{ emptyset, basetask, thes1, emptyset },
				{ naset, basena, thes1, emptyset },
				{ naset, basetask, thes1, emptyset },
				{ taset1, basena, thes1, emptyset },
				{ taset1, basena, thes2, emptyset },
				{ taset1, basetask, thes1, tamax1 },
				{ taset1, basetask, thes2, emptyset },
				{ taset2, basena, thes1, emptyset },
				{ taset2, basena, thes2, emptyset },
				{ taset2, basetask, thes1, tamax2 },
				{ taset2, basetask, thes2, emptyset },
				{ taset3, basena, thes1, emptyset },
				{ taset3, basena, thes2, emptyset },
				{ taset3, basetask, thes1, tamax3 },
				{ taset3, basetask, thes2, emptyset },
		};
		
		// check null
		try {
			naset.selectThesaurusMax(null, thes1);
			fail("DtAlgeSet#selectThesaurusMax(null, obj) must be throw exception!");
		} catch (NullPointerException ex) { assertTrue(true); }
		try {
			naset.selectThesaurusMax(basena, null);
			fail("DtAlgeSet#selectThesaurusMax(obj, null) must be throw exception!");
		} catch (NullPointerException ex) { assertTrue(true); }
		try {
			naset.selectThesaurusMax(null, null);
			fail("DtAlgeSet#selectThesaurusMax(null, null) must be throw exception!");
		} catch (NullPointerException ex) { assertTrue(true); }
		
		// check illegal data type
		try {
			naset.selectThesaurusMax(baseflag, thes1);
			fail("DtAlgeSet#selectThesaurusMax(base(boolean), obj) must be throw exception!");
		} catch (IllegalArgumentException ex) { assertTrue(true); }
		try {
			naset.selectThesaurusMax(basevalue, thes1);
			fail("DtAlgeSet#selectThesaurusMax(base(decimal), obj) must be throw exception!");
		} catch (IllegalArgumentException ex) { assertTrue(true); }

		// test
		for (int i = 0; i < testpatterns.length; i++) {
			System.out.println("DtAlgeSetTest#testSelectThesaurusMaxDtBaseDtStringThesaurus() - test pattern index : " + i);
			DtAlgeSet algeset = (DtAlgeSet)testpatterns[i][0];
			DtBase base = (DtBase)testpatterns[i][1];
			DtStringThesaurus thes = (DtStringThesaurus)testpatterns[i][2];
			DtAlgeSet ansset = (DtAlgeSet)testpatterns[i][3];

			DtAlgeSet ret = algeset.selectThesaurusMax(base, thes);
			assertEquals(ret, ansset);
		}
	}
	
	/**
	 * {@link dtalge.DtAlgeSet#selectThesaurusMin(DtBase, DtStringThesaurus)} のためのテスト・メソッド。
	 * @since 0.30
	 */
	public void testSelectThesaurusMinDtBaseDtStringThesaurus() {
		// シソーラス定義の生成
		DtStringThesaurus thes0 = new DtStringThesaurus();
		assertTrue(thes0.isEmpty());
		DtStringThesaurus thes1 = DtStringThesaurusTest.newThesaurus(thesMPPairs1);
		assertEquals((thesMPPairs1.length/2), thes1.size());
		for (int i = 0; i < thesMPPairs1.length; i+=2) {
			String parent = thesMPPairs1[i];
			String child  = thesMPPairs1[i+1];
			assertTrue(thes1.isComparable(parent, child));
			assertTrue(thes1.isComparable(child, parent));
			assertFalse(thes1.isComparable(parent, parent));
			assertFalse(thes1.isComparable(child, child));
		}
		DtStringThesaurus thes2 = DtStringThesaurusTest.newThesaurus(thesMPPairs2);
		assertEquals((thesMPPairs2.length/2), thes2.size());
		for (int i = 0; i < thesMPPairs2.length; i+=2) {
			String parent = thesMPPairs2[i];
			String child  = thesMPPairs2[i+1];
			assertTrue(thes2.isComparable(parent, child));
			assertTrue(thes2.isComparable(child, parent));
			assertFalse(thes2.isComparable(parent, parent));
			assertFalse(thes2.isComparable(child, child));
		}
		
		// make data
		DtAlgeSet taset1 = new DtAlgeSet(makeDtalges(thesAlges1));
		assertTrue(equalElementSequence(taset1, thesAlges1));
		DtAlgeSet taset2 = new DtAlgeSet(makeDtalges(thesAlges2));
		assertTrue(equalElementSequence(taset2, thesAlges2));
		DtAlgeSet taset3 = new DtAlgeSet(makeDtalges(thesAlges3));
		assertTrue(equalElementSequence(taset3, thesAlges3));
		DtAlgeSet tamax1 = new DtAlgeSet(makeDtalges(thesSelectMax1));
		assertTrue(equalElementSequence(tamax1, thesSelectMax1));
		DtAlgeSet tamin1 = new DtAlgeSet(makeDtalges(thesSelectMin1));
		assertTrue(equalElementSequence(tamin1, thesSelectMin1));
		DtAlgeSet tamax2 = new DtAlgeSet(makeDtalges(thesSelectMax2));
		assertTrue(equalElementSequence(tamax2, thesSelectMax2));
		DtAlgeSet tamin2 = new DtAlgeSet(makeDtalges(thesSelectMin2));
		assertTrue(equalElementSequence(tamin2, thesSelectMin2));
		DtAlgeSet tamax3 = new DtAlgeSet(makeDtalges(thesSelectMax3));
		assertTrue(equalElementSequence(tamax3, thesSelectMax3));
		DtAlgeSet tamin3 = new DtAlgeSet(makeDtalges(thesSelectMin3));
		assertTrue(equalElementSequence(tamin3, thesSelectMin3));
		DtAlgeSet naset = new DtAlgeSet(makeDtalges(normalAlges));
		assertTrue(equalElementSequence(naset, normalAlges));
		DtAlgeSet emptyset = new DtAlgeSet();

		DtBase basena = new DtBase("名前","string","#","#");
		DtBase basetask = new DtBase("Task", DtDataTypes.STRING);
		DtBase baseflag = new DtBase("Flag", DtDataTypes.BOOLEAN);
		DtBase basevalue = new DtBase("Value", DtDataTypes.DECIMAL);
		
		// make patterns
		Object[][] testpatterns = new Object[][]{
				{ emptyset, basetask, thes1, emptyset },
				{ naset, basena, thes1, emptyset },
				{ naset, basetask, thes1, emptyset },
				{ taset1, basena, thes1, emptyset },
				{ taset1, basena, thes2, emptyset },
				{ taset1, basetask, thes1, tamin1 },
				{ taset1, basetask, thes2, emptyset },
				{ taset2, basena, thes1, emptyset },
				{ taset2, basena, thes2, emptyset },
				{ taset2, basetask, thes1, tamin2 },
				{ taset2, basetask, thes2, emptyset },
				{ taset3, basena, thes1, emptyset },
				{ taset3, basena, thes2, emptyset },
				{ taset3, basetask, thes1, tamin3 },
				{ taset3, basetask, thes2, emptyset },
		};
		
		// check null
		try {
			naset.selectThesaurusMin(null, thes1);
			fail("DtAlgeSet#selectThesaurusMax(null, obj) must be throw exception!");
		} catch (NullPointerException ex) { assertTrue(true); }
		try {
			naset.selectThesaurusMin(basena, null);
			fail("DtAlgeSet#selectThesaurusMax(obj, null) must be throw exception!");
		} catch (NullPointerException ex) { assertTrue(true); }
		try {
			naset.selectThesaurusMin(null, null);
			fail("DtAlgeSet#selectThesaurusMax(null, null) must be throw exception!");
		} catch (NullPointerException ex) { assertTrue(true); }
		
		// check illegal data type
		try {
			naset.selectThesaurusMin(baseflag, thes1);
			fail("DtAlgeSet#selectThesaurusMax(base(boolean), obj) must be throw exception!");
		} catch (IllegalArgumentException ex) { assertTrue(true); }
		try {
			naset.selectThesaurusMin(basevalue, thes1);
			fail("DtAlgeSet#selectThesaurusMax(base(decimal), obj) must be throw exception!");
		} catch (IllegalArgumentException ex) { assertTrue(true); }

		// test
		for (int i = 0; i < testpatterns.length; i++) {
			System.out.println("DtAlgeSetTest#testSelectThesaurusMaxDtBaseDtStringThesaurus() - test pattern index : " + i);
			DtAlgeSet algeset = (DtAlgeSet)testpatterns[i][0];
			DtBase base = (DtBase)testpatterns[i][1];
			DtStringThesaurus thes = (DtStringThesaurus)testpatterns[i][2];
			DtAlgeSet ansset = (DtAlgeSet)testpatterns[i][3];

			DtAlgeSet ret = algeset.selectThesaurusMin(base, thes);
			assertEquals(ret, ansset);
		}
	}
	
	/**
	 * {@link dtalge.DtAlgeSet#sortAlgesByValue(DtBase, boolean)} のためのテスト・メソッド。
	 * @since 0.30
	 */
	public void testSortAlgesByValueDtBaseBoolean() {
		DtAlgeSet testalge = new DtAlgeSet(makeDtalges(sortTestAlges));
		assertTrue(equalElementSequence(testalge, sortTestAlges));
		DtAlgeSet test1 = new DtAlgeSet(makeDtalges(sortTestAlges));
		assertTrue(equalElementSequence(test1, sortTestAlges));
		DtAlgeSet test2 = new DtAlgeSet(makeDtalges(sortTestAlges));
		assertTrue(equalElementSequence(test2, sortTestAlges));
		DtAlgeSet test3 = new DtAlgeSet(makeDtalges(sortTestAlges));
		assertTrue(equalElementSequence(test3, sortTestAlges));
		DtAlgeSet test4 = new DtAlgeSet(makeDtalges(sortTestAlges));
		assertTrue(equalElementSequence(test4, sortTestAlges));
		DtAlgeSet test5 = new DtAlgeSet(makeDtalges(sortTestAlges));
		assertTrue(equalElementSequence(test5, sortTestAlges));
		DtAlgeSet test6 = new DtAlgeSet(makeDtalges(sortTestAlges));
		assertTrue(equalElementSequence(test6, sortTestAlges));
		
		DtAlgeSet ans1 = new DtAlgeSet(makeDtalges(sortedAnsAlgesStringAsc));
		assertTrue(equalElementSequence(ans1, sortedAnsAlgesStringAsc));
		DtAlgeSet ans2 = new DtAlgeSet(makeDtalges(sortedAnsAlgesStringDsc));
		assertTrue(equalElementSequence(ans2, sortedAnsAlgesStringDsc));
		DtAlgeSet ans3 = new DtAlgeSet(makeDtalges(sortedAnsAlgesDecimalAsc));
		assertTrue(equalElementSequence(ans3, sortedAnsAlgesDecimalAsc));
		DtAlgeSet ans4 = new DtAlgeSet(makeDtalges(sortedAnsAlgesDecimalDsc));
		assertTrue(equalElementSequence(ans4, sortedAnsAlgesDecimalDsc));
		DtAlgeSet ans5 = new DtAlgeSet(makeDtalges(sortedAnsAlgesBooleanAsc));
		assertTrue(equalElementSequence(ans5, sortedAnsAlgesBooleanAsc));
		DtAlgeSet ans6 = new DtAlgeSet(makeDtalges(sortedAnsAlgesBooleanDsc));
		assertTrue(equalElementSequence(ans6, sortedAnsAlgesBooleanDsc));
		
		DtBase base1 = new DtBase("string", DtDataTypes.STRING);
		DtBase base2 = new DtBase("string", DtDataTypes.STRING);
		DtBase base3 = new DtBase("decimal", DtDataTypes.DECIMAL);
		DtBase base4 = new DtBase("decimal", DtDataTypes.DECIMAL);
		DtBase base5 = new DtBase("boolean", DtDataTypes.BOOLEAN);
		DtBase base6 = new DtBase("boolean", DtDataTypes.BOOLEAN);
		
		DtAlgeSet emptyset = new DtAlgeSet();
		
		// check null
		try {
			emptyset.sortAlgesByValue(null, true);
			fail("DtAlgeSet#sortAlgesByValue(null, obj) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			test1.sortAlgesByValue(null, true);
			fail("DtAlgeSet#sortAlgesByValue(null, obj) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		// test1
		emptyset = new DtAlgeSet();
		emptyset.sortAlgesByValue(base1, true);
		assertTrue(emptyset.isEmpty());
		test1.sortAlgesByValue(base1, true);
		assertEquals(test1, ans1);
		assertFalse(test1.equals(testalge));
		
		// test2
		emptyset = new DtAlgeSet();
		emptyset.sortAlgesByValue(base2, false);
		assertTrue(emptyset.isEmpty());
		test2.sortAlgesByValue(base2, false);
		assertEquals(test2, ans2);
		assertFalse(test2.equals(testalge));
		
		// test3
		emptyset = new DtAlgeSet();
		emptyset.sortAlgesByValue(base3, true);
		assertTrue(emptyset.isEmpty());
		test3.sortAlgesByValue(base3, true);
		assertEquals(test3, ans3);
		assertFalse(test3.equals(testalge));
		
		// test4
		emptyset = new DtAlgeSet();
		emptyset.sortAlgesByValue(base4, false);
		assertTrue(emptyset.isEmpty());
		test4.sortAlgesByValue(base4, false);
		assertEquals(test4, ans4);
		assertFalse(test4.equals(testalge));
		
		// test5
		emptyset = new DtAlgeSet();
		emptyset.sortAlgesByValue(base5, true);
		assertTrue(emptyset.isEmpty());
		test5.sortAlgesByValue(base5, true);
		assertEquals(test5, ans5);
		assertFalse(test5.equals(testalge));
		
		// test6
		emptyset = new DtAlgeSet();
		emptyset.sortAlgesByValue(base6, false);
		assertTrue(emptyset.isEmpty());
		test6.sortAlgesByValue(base6, false);
		assertEquals(test6, ans6);
		assertFalse(test6.equals(testalge));
	}
	
	/**
	 * {@link dtalge.DtAlgeSet#sortedAlgesByValue(DtBase, boolean)} のためのテスト・メソッド。
	 * @since 0.30
	 */
	public void testSortedAlgesByValueDtBaseBoolean() {
		DtAlgeSet testalge = new DtAlgeSet(makeDtalges(sortTestAlges));
		assertTrue(equalElementSequence(testalge, sortTestAlges));
		DtAlgeSet test1 = new DtAlgeSet(makeDtalges(sortTestAlges));
		assertTrue(equalElementSequence(test1, sortTestAlges));
		DtAlgeSet test2 = new DtAlgeSet(makeDtalges(sortTestAlges));
		assertTrue(equalElementSequence(test2, sortTestAlges));
		DtAlgeSet test3 = new DtAlgeSet(makeDtalges(sortTestAlges));
		assertTrue(equalElementSequence(test3, sortTestAlges));
		DtAlgeSet test4 = new DtAlgeSet(makeDtalges(sortTestAlges));
		assertTrue(equalElementSequence(test4, sortTestAlges));
		DtAlgeSet test5 = new DtAlgeSet(makeDtalges(sortTestAlges));
		assertTrue(equalElementSequence(test5, sortTestAlges));
		DtAlgeSet test6 = new DtAlgeSet(makeDtalges(sortTestAlges));
		assertTrue(equalElementSequence(test6, sortTestAlges));
		
		DtAlgeSet ans1 = new DtAlgeSet(makeDtalges(sortedAnsAlgesStringAsc));
		assertTrue(equalElementSequence(ans1, sortedAnsAlgesStringAsc));
		DtAlgeSet ans2 = new DtAlgeSet(makeDtalges(sortedAnsAlgesStringDsc));
		assertTrue(equalElementSequence(ans2, sortedAnsAlgesStringDsc));
		DtAlgeSet ans3 = new DtAlgeSet(makeDtalges(sortedAnsAlgesDecimalAsc));
		assertTrue(equalElementSequence(ans3, sortedAnsAlgesDecimalAsc));
		DtAlgeSet ans4 = new DtAlgeSet(makeDtalges(sortedAnsAlgesDecimalDsc));
		assertTrue(equalElementSequence(ans4, sortedAnsAlgesDecimalDsc));
		DtAlgeSet ans5 = new DtAlgeSet(makeDtalges(sortedAnsAlgesBooleanAsc));
		assertTrue(equalElementSequence(ans5, sortedAnsAlgesBooleanAsc));
		DtAlgeSet ans6 = new DtAlgeSet(makeDtalges(sortedAnsAlgesBooleanDsc));
		assertTrue(equalElementSequence(ans6, sortedAnsAlgesBooleanDsc));
		
		DtBase base1 = new DtBase("string", DtDataTypes.STRING);
		DtBase base2 = new DtBase("string", DtDataTypes.STRING);
		DtBase base3 = new DtBase("decimal", DtDataTypes.DECIMAL);
		DtBase base4 = new DtBase("decimal", DtDataTypes.DECIMAL);
		DtBase base5 = new DtBase("boolean", DtDataTypes.BOOLEAN);
		DtBase base6 = new DtBase("boolean", DtDataTypes.BOOLEAN);
		
		DtAlgeSet emptyset = new DtAlgeSet();
		DtAlgeSet ret;
		
		// check null
		try {
			emptyset.sortedAlgesByValue(null, true);
			fail("DtAlgeSet#sortedAlgesByValue(null, obj) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			test1.sortedAlgesByValue(null, true);
			fail("DtAlgeSet#sortedAlgesByValue(null, obj) must be throw exception.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		// test1
		emptyset = new DtAlgeSet();
		ret = emptyset.sortedAlgesByValue(base1, true);
		assertTrue(emptyset.isEmpty());
		assertTrue(ret.isEmpty());
		ret = test1.sortedAlgesByValue(base1, true);
		assertEquals(ret, ans1);
		assertEquals(test1, testalge);
		assertFalse(ret.equals(testalge));
		
		// test2
		emptyset = new DtAlgeSet();
		ret = emptyset.sortedAlgesByValue(base2, false);
		assertTrue(emptyset.isEmpty());
		assertTrue(ret.isEmpty());
		ret = test2.sortedAlgesByValue(base2, false);
		assertEquals(ret, ans2);
		assertEquals(test2, testalge);
		assertFalse(ret.equals(testalge));
		
		// test3
		emptyset = new DtAlgeSet();
		ret = emptyset.sortedAlgesByValue(base3, true);
		assertTrue(emptyset.isEmpty());
		assertTrue(ret.isEmpty());
		ret = test3.sortedAlgesByValue(base3, true);
		assertEquals(ret, ans3);
		assertEquals(test3, testalge);
		assertFalse(ret.equals(testalge));
		
		// test4
		emptyset = new DtAlgeSet();
		ret = emptyset.sortedAlgesByValue(base4, false);
		assertTrue(emptyset.isEmpty());
		assertTrue(ret.isEmpty());
		ret = test4.sortedAlgesByValue(base4, false);
		assertEquals(ret, ans4);
		assertEquals(test4, testalge);
		assertFalse(ret.equals(testalge));
		
		// test5
		emptyset = new DtAlgeSet();
		ret = emptyset.sortedAlgesByValue(base5, true);
		assertTrue(emptyset.isEmpty());
		assertTrue(ret.isEmpty());
		ret = test5.sortedAlgesByValue(base5, true);
		assertEquals(ret, ans5);
		assertEquals(test5, testalge);
		assertFalse(ret.equals(testalge));
		
		// test6
		emptyset = new DtAlgeSet();
		ret = emptyset.sortedAlgesByValue(base6, false);
		assertTrue(emptyset.isEmpty());
		assertTrue(ret.isEmpty());
		ret = test6.sortedAlgesByValue(base6, false);
		assertEquals(ret, ans6);
		assertEquals(test6, testalge);
		assertFalse(ret.equals(testalge));
	}
}
