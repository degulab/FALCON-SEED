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
 * @(#)DtAlgeSetIOTest2.java	0.40	2012/05/23
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge;

import static dtalge.AllUnitTests.*;

import java.io.File;
import java.math.BigDecimal;

import dtalge.exception.CsvFormatException;

import junit.framework.TestCase;

/**
 * <code>DtAlgeSet</code> バージョン 2 のファイル入出力機能のユニットテスト。
 * 
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class DtAlgeSetIOTest2 extends TestCase
{
	// テストデータの全基底集合(整列済み)
	static protected final DtBase[] sortedAllBaseSetData =
	{
		new DtBase("基底01", "string" , "#", "#"),
		new DtBase("基底01", "decimal", "#", "#"),
		new DtBase("基底01", "boolean", "#", "#"),
		new DtBase("基底02", "string" , "#", "#"),
		new DtBase("基底02", "decimal", "#", "#"),
		new DtBase("基底02", "boolean", "#", "#"),
		new DtBase("基底03", "string" , "#", "#"),
		new DtBase("基底03", "decimal", "#", "#"),
		new DtBase("基底03", "boolean", "#", "#"),
		new DtBase("基底04", "string" , "#", "#"),
		new DtBase("基底04", "decimal", "#", "#"),
		new DtBase("基底04", "boolean", "#", "#"),
		new DtBase("基底05", "string" , "#", "#"),
		new DtBase("基底05", "decimal", "#", "#"),
		new DtBase("基底05", "boolean", "#", "#"),
		new DtBase("基底06", "string" , "#", "#"),
		new DtBase("基底06", "decimal", "#", "#"),
		new DtBase("基底06", "boolean", "#", "#"),
		new DtBase("基底07", "string" , "#", "#"),
		new DtBase("基底07", "decimal", "#", "#"),
		new DtBase("基底07", "boolean", "#", "#"),
		new DtBase("基底08", "string" , "#", "#"),
		new DtBase("基底08", "decimal", "#", "#"),
		new DtBase("基底08", "boolean", "#", "#"),
		new DtBase("基底09", "string" , "#", "#"),
		new DtBase("基底09", "decimal", "#", "#"),
		new DtBase("基底09", "boolean", "#", "#"),
		new DtBase("基底0a", "string" , "#", "#"),
		new DtBase("基底0a", "decimal", "#", "#"),
		new DtBase("基底0a", "boolean", "#", "#"),
		new DtBase("基底0b", "string" , "#", "#"),
		new DtBase("基底0b", "decimal", "#", "#"),
		new DtBase("基底0b", "boolean", "#", "#"),
		new DtBase("基底0c", "string" , "#", "#"),
		new DtBase("基底0c", "decimal", "#", "#"),
		new DtBase("基底0c", "boolean", "#", "#"),
		new DtBase("基底0d", "string" , "#", "#"),
		new DtBase("基底0d", "decimal", "#", "#"),
		new DtBase("基底0d", "boolean", "#", "#"),
	};
	
	// テストデータの部分基底集合(整列済み)
	static protected final DtBase[] halfSortedAllBaseSetData =
	{
		new DtBase("基底01", "string" , "#", "#"),
		new DtBase("基底01", "decimal", "#", "#"),
		new DtBase("基底01", "boolean", "#", "#"),
		new DtBase("基底02", "string" , "#", "#"),
		new DtBase("基底02", "decimal", "#", "#"),
		new DtBase("基底02", "boolean", "#", "#"),
		new DtBase("基底03", "string" , "#", "#"),
		new DtBase("基底03", "decimal", "#", "#"),
		new DtBase("基底03", "boolean", "#", "#"),
		new DtBase("基底04", "string" , "#", "#"),
		new DtBase("基底04", "decimal", "#", "#"),
		new DtBase("基底04", "boolean", "#", "#"),
		new DtBase("基底05", "string" , "#", "#"),
		new DtBase("基底05", "decimal", "#", "#"),
		new DtBase("基底05", "boolean", "#", "#"),
		new DtBase("基底06", "string" , "#", "#"),
		new DtBase("基底06", "decimal", "#", "#"),
		new DtBase("基底06", "boolean", "#", "#"),
		new DtBase("基底07", "string" , "#", "#"),
		new DtBase("基底07", "decimal", "#", "#"),
		new DtBase("基底07", "boolean", "#", "#"),
		new DtBase("基底08", "string" , "#", "#"),
		new DtBase("基底08", "decimal", "#", "#"),
		new DtBase("基底08", "boolean", "#", "#"),
		new DtBase("基底09", "string" , "#", "#"),
		new DtBase("基底09", "decimal", "#", "#"),
		new DtBase("基底09", "boolean", "#", "#"),
	};

	// 基準となるテストデータ(空文字列も含む)
	static protected final Object[][] sortedAllAlgeSetData =
	{
		{
			new DtBase("基底01", "string" , "#", "#"), "文字列01",
			new DtBase("基底01", "decimal", "#", "#"), new BigDecimal("-0.01"),
			new DtBase("基底01", "boolean", "#", "#"), true,
			new DtBase("基底02", "string" , "#", "#"), "!%!%!%文字列02",
			new DtBase("基底02", "decimal", "#", "#"), new BigDecimal("123.02"),
			new DtBase("基底02", "boolean", "#", "#"), false,
			new DtBase("基底03", "string" , "#", "#"), "文字列03",
			new DtBase("基底03", "decimal", "#", "#"), new BigDecimal("10.03"),
			new DtBase("基底03", "boolean", "#", "#"), true,
			new DtBase("基底04", "string" , "#", "#"), "文字列!%04",
			new DtBase("基底04", "decimal", "#", "#"), new BigDecimal("-975.04"),
			new DtBase("基底04", "boolean", "#", "#"), false,
			new DtBase("基底05", "string" , "#", "#"), "文字列05",
			new DtBase("基底05", "decimal", "#", "#"), new BigDecimal("2345.05"),
			new DtBase("基底05", "boolean", "#", "#"), true,
			new DtBase("基底06", "string" , "#", "#"), " !%文字列06",
			new DtBase("基底06", "decimal", "#", "#"), new BigDecimal("-1006"),
			new DtBase("基底06", "boolean", "#", "#"), false,
			new DtBase("基底07", "string" , "#", "#"), "文字列07",
			new DtBase("基底07", "decimal", "#", "#"), new BigDecimal("513534321207"),
			new DtBase("基底07", "boolean", "#", "#"), true,
			new DtBase("基底08", "string" , "#", "#"), "文字列08",
			new DtBase("基底08", "decimal", "#", "#"), new BigDecimal("-0.2108"),
			new DtBase("基底08", "boolean", "#", "#"), false,
			new DtBase("基底09", "string" , "#", "#"), "文字列09",
			new DtBase("基底09", "decimal", "#", "#"), new BigDecimal("0.109"),
			new DtBase("基底09", "boolean", "#", "#"), true,
			new DtBase("基底0a", "string" , "#", "#"), null,
			new DtBase("基底0a", "decimal", "#", "#"), null,
			new DtBase("基底0a", "boolean", "#", "#"), null,
			new DtBase("基底0b", "string" , "#", "#"), "",
			new DtBase("基底0b", "decimal", "#", "#"), BigDecimal.ZERO,
			new DtBase("基底0b", "boolean", "#", "#"), false,
			new DtBase("基底0c", "string" , "#", "#"), "!%n",
			new DtBase("基底0c", "decimal", "#", "#"), new BigDecimal("1.12"),
			new DtBase("基底0c", "boolean", "#", "#"), true,
			new DtBase("基底0d", "string" , "#", "#"), "!%",
			new DtBase("基底0d", "decimal", "#", "#"), new BigDecimal("-1.13"),
			new DtBase("基底0d", "boolean", "#", "#"), false,
		},
		{
			new DtBase("基底01", "string" , "#", "#"), "文字列11",
			new DtBase("基底01", "decimal", "#", "#"), new BigDecimal("1.01"),
			new DtBase("基底01", "boolean", "#", "#"), false,
			new DtBase("基底02", "string" , "#", "#"), "文字列12",
			new DtBase("基底02", "decimal", "#", "#"), new BigDecimal("1.02"),
			new DtBase("基底02", "boolean", "#", "#"), true,
			new DtBase("基底03", "string" , "#", "#"), "文字列13",
			new DtBase("基底03", "decimal", "#", "#"), new BigDecimal("1.03"),
			new DtBase("基底03", "boolean", "#", "#"), false,
			new DtBase("基底04", "string" , "#", "#"), "文字列14",
			new DtBase("基底04", "decimal", "#", "#"), new BigDecimal("1.04"),
			new DtBase("基底04", "boolean", "#", "#"), true,
			new DtBase("基底05", "string" , "#", "#"), "文字列15",
			new DtBase("基底05", "decimal", "#", "#"), new BigDecimal("1.05"),
			new DtBase("基底05", "boolean", "#", "#"), false,
			new DtBase("基底06", "string" , "#", "#"), "文字列16",
			new DtBase("基底06", "decimal", "#", "#"), new BigDecimal("1.06"),
			new DtBase("基底06", "boolean", "#", "#"), true,
			new DtBase("基底07", "string" , "#", "#"), "文字列17",
			new DtBase("基底07", "decimal", "#", "#"), new BigDecimal("1.07"),
			new DtBase("基底07", "boolean", "#", "#"), false,
			new DtBase("基底08", "string" , "#", "#"), "文字列18",
			new DtBase("基底08", "decimal", "#", "#"), new BigDecimal("1.08"),
			new DtBase("基底08", "boolean", "#", "#"), true,
			new DtBase("基底09", "string" , "#", "#"), "文字列19",
			new DtBase("基底09", "decimal", "#", "#"), new BigDecimal("1.09"),
			new DtBase("基底09", "boolean", "#", "#"), false,
			new DtBase("基底0a", "string" , "#", "#"), "文字列1a",
			new DtBase("基底0a", "decimal", "#", "#"), new BigDecimal("1.10"),
			new DtBase("基底0a", "boolean", "#", "#"), true,
			new DtBase("基底0b", "string" , "#", "#"), "!%",
			new DtBase("基底0b", "decimal", "#", "#"), new BigDecimal("1.11"),
			new DtBase("基底0b", "boolean", "#", "#"), true,
			new DtBase("基底0c", "string" , "#", "#"), "!%文字列1c",
			new DtBase("基底0c", "decimal", "#", "#"), new BigDecimal("1.12"),
			new DtBase("基底0c", "boolean", "#", "#"), false,
			new DtBase("基底0d", "string" , "#", "#"), "!%!%文字列1d",
			new DtBase("基底0d", "decimal", "#", "#"), new BigDecimal("1.13"),
			new DtBase("基底0d", "boolean", "#", "#"), true,
		},
		{
			new DtBase("基底01", "string" , "#", "#"), "文字列21",
			new DtBase("基底01", "decimal", "#", "#"), new BigDecimal("-2.01"),
			new DtBase("基底01", "boolean", "#", "#"), true,
			new DtBase("基底02", "string" , "#", "#"), "文字列22",
			new DtBase("基底02", "decimal", "#", "#"), new BigDecimal("-2.02"),
			new DtBase("基底02", "boolean", "#", "#"), true,
			new DtBase("基底03", "string" , "#", "#"), "文字列23",
			new DtBase("基底03", "decimal", "#", "#"), new BigDecimal("-2.03"),
			new DtBase("基底03", "boolean", "#", "#"), true,
			new DtBase("基底04", "string" , "#", "#"), " !%文字列24",
			new DtBase("基底04", "decimal", "#", "#"), new BigDecimal("-2.04"),
			new DtBase("基底04", "boolean", "#", "#"), true,
			new DtBase("基底05", "string" , "#", "#"), "文字列25",
			new DtBase("基底05", "decimal", "#", "#"), new BigDecimal("-2.05"),
			new DtBase("基底05", "boolean", "#", "#"), false,
			new DtBase("基底06", "string" , "#", "#"), "文字列26",
			new DtBase("基底06", "decimal", "#", "#"), new BigDecimal("-2.06"),
			new DtBase("基底06", "boolean", "#", "#"), false,
			new DtBase("基底07", "string" , "#", "#"), "文字列!%27",
			new DtBase("基底07", "decimal", "#", "#"), new BigDecimal("-2.07"),
			new DtBase("基底07", "boolean", "#", "#"), false,
			new DtBase("基底08", "string" , "#", "#"), "文字列28",
			new DtBase("基底08", "decimal", "#", "#"), new BigDecimal("-2.08"),
			new DtBase("基底08", "boolean", "#", "#"), false,
			new DtBase("基底09", "string" , "#", "#"), "文字列29",
			new DtBase("基底09", "decimal", "#", "#"), new BigDecimal("-2.09"),
			new DtBase("基底09", "boolean", "#", "#"), true,
			new DtBase("基底0a", "string" , "#", "#"), "文字列2a",
			new DtBase("基底0a", "decimal", "#", "#"), new BigDecimal("-2.10"),
			new DtBase("基底0a", "boolean", "#", "#"), true,
			new DtBase("基底0b", "string" , "#", "#"), "文字列2b",
			new DtBase("基底0b", "decimal", "#", "#"), new BigDecimal("-2.11"),
			new DtBase("基底0b", "boolean", "#", "#"), false,
			new DtBase("基底0c", "string" , "#", "#"), "!%文字列2c",
			new DtBase("基底0c", "decimal", "#", "#"), new BigDecimal("-2.12"),
			new DtBase("基底0c", "boolean", "#", "#"), false,
			new DtBase("基底0d", "string" , "#", "#"), "!%!%文字列2d",
			new DtBase("基底0d", "decimal", "#", "#"), new BigDecimal("-2.13"),
			new DtBase("基底0d", "boolean", "#", "#"), null,
		},
		{
			new DtBase("基底01", "string" , "#", "#"), null,
			new DtBase("基底01", "decimal", "#", "#"), new BigDecimal("3.01"),
			new DtBase("基底01", "boolean", "#", "#"), false,
			new DtBase("基底02", "string" , "#", "#"), null,
			new DtBase("基底02", "decimal", "#", "#"), new BigDecimal("3.02"),
			new DtBase("基底02", "boolean", "#", "#"), false,
			new DtBase("基底03", "string" , "#", "#"), null,
			new DtBase("基底03", "decimal", "#", "#"), new BigDecimal("3.03"),
			new DtBase("基底03", "boolean", "#", "#"), false,
			new DtBase("基底04", "string" , "#", "#"), null,
			new DtBase("基底04", "decimal", "#", "#"), new BigDecimal("3.04"),
			new DtBase("基底04", "boolean", "#", "#"), false,
			new DtBase("基底05", "string" , "#", "#"), null,
			new DtBase("基底05", "decimal", "#", "#"), new BigDecimal("3.05"),
			new DtBase("基底05", "boolean", "#", "#"), true,
			new DtBase("基底06", "string" , "#", "#"), null,
			new DtBase("基底06", "decimal", "#", "#"), new BigDecimal("3.06"),
			new DtBase("基底06", "boolean", "#", "#"), true,
			new DtBase("基底07", "string" , "#", "#"), null,
			new DtBase("基底07", "decimal", "#", "#"), new BigDecimal("3.07"),
			new DtBase("基底07", "boolean", "#", "#"), true,
			new DtBase("基底08", "string" , "#", "#"), null,
			new DtBase("基底08", "decimal", "#", "#"), new BigDecimal("3.08"),
			new DtBase("基底08", "boolean", "#", "#"), true,
			new DtBase("基底09", "string" , "#", "#"), null,
			new DtBase("基底09", "decimal", "#", "#"), new BigDecimal("3.09"),
			new DtBase("基底09", "boolean", "#", "#"), false,
			new DtBase("基底0a", "string" , "#", "#"), null,
			new DtBase("基底0a", "decimal", "#", "#"), new BigDecimal("3.10"),
			new DtBase("基底0a", "boolean", "#", "#"), false,
			new DtBase("基底0b", "string" , "#", "#"), null,
			new DtBase("基底0b", "decimal", "#", "#"), new BigDecimal("3.11"),
			new DtBase("基底0b", "boolean", "#", "#"), true,
			new DtBase("基底0c", "string" , "#", "#"), null,
			new DtBase("基底0c", "decimal", "#", "#"), new BigDecimal("3.12"),
			new DtBase("基底0c", "boolean", "#", "#"), true,
			new DtBase("基底0d", "string" , "#", "#"), null,
			new DtBase("基底0d", "decimal", "#", "#"), new BigDecimal("3.13"),
			new DtBase("基底0d", "boolean", "#", "#"), false,
		},
		{
			new DtBase("基底01", "string" , "#", "#"), "文字列41",
			new DtBase("基底01", "decimal", "#", "#"), null,
			new DtBase("基底01", "boolean", "#", "#"), false,
			new DtBase("基底02", "string" , "#", "#"), "文字列42",
			new DtBase("基底02", "decimal", "#", "#"), null,
			new DtBase("基底02", "boolean", "#", "#"), false,
			new DtBase("基底03", "string" , "#", "#"), "文字列43",
			new DtBase("基底03", "decimal", "#", "#"), null,
			new DtBase("基底03", "boolean", "#", "#"), true,
			new DtBase("基底04", "string" , "#", "#"), "文字列44",
			new DtBase("基底04", "decimal", "#", "#"), null,
			new DtBase("基底04", "boolean", "#", "#"), true,
			new DtBase("基底05", "string" , "#", "#"), "文字列45",
			new DtBase("基底05", "decimal", "#", "#"), null,
			new DtBase("基底05", "boolean", "#", "#"), false,
			new DtBase("基底06", "string" , "#", "#"), "文字列46",
			new DtBase("基底06", "decimal", "#", "#"), null,
			new DtBase("基底06", "boolean", "#", "#"), false,
			new DtBase("基底07", "string" , "#", "#"), "文字列47",
			new DtBase("基底07", "decimal", "#", "#"), null,
			new DtBase("基底07", "boolean", "#", "#"), true,
			new DtBase("基底08", "string" , "#", "#"), "文字列48",
			new DtBase("基底08", "decimal", "#", "#"), null,
			new DtBase("基底08", "boolean", "#", "#"), true,
			new DtBase("基底09", "string" , "#", "#"), "文字列49",
			new DtBase("基底09", "decimal", "#", "#"), null,
			new DtBase("基底09", "boolean", "#", "#"), false,
			new DtBase("基底0a", "string" , "#", "#"), "文字列4a",
			new DtBase("基底0a", "decimal", "#", "#"), null,
			new DtBase("基底0a", "boolean", "#", "#"), false,
			new DtBase("基底0b", "string" , "#", "#"), "文字列4b",
			new DtBase("基底0b", "decimal", "#", "#"), null,
			new DtBase("基底0b", "boolean", "#", "#"), true,
			new DtBase("基底0c", "string" , "#", "#"), "!%文字列4c",
			new DtBase("基底0c", "decimal", "#", "#"), null,
			new DtBase("基底0c", "boolean", "#", "#"), true,
			new DtBase("基底0d", "string" , "#", "#"), "!%!%文字列4d",
			new DtBase("基底0d", "decimal", "#", "#"), null,
			new DtBase("基底0d", "boolean", "#", "#"), true,
		},
		{
			new DtBase("基底01", "string" , "#", "#"), null,
			new DtBase("基底01", "decimal", "#", "#"), null,
			new DtBase("基底01", "boolean", "#", "#"), null,
			new DtBase("基底02", "string" , "#", "#"), null,
			new DtBase("基底02", "decimal", "#", "#"), null,
			new DtBase("基底02", "boolean", "#", "#"), null,
			new DtBase("基底03", "string" , "#", "#"), null,
			new DtBase("基底03", "decimal", "#", "#"), null,
			new DtBase("基底03", "boolean", "#", "#"), null,
			new DtBase("基底04", "string" , "#", "#"), null,
			new DtBase("基底04", "decimal", "#", "#"), null,
			new DtBase("基底04", "boolean", "#", "#"), null,
			new DtBase("基底05", "string" , "#", "#"), null,
			new DtBase("基底05", "decimal", "#", "#"), null,
			new DtBase("基底05", "boolean", "#", "#"), null,
			new DtBase("基底06", "string" , "#", "#"), null,
			new DtBase("基底06", "decimal", "#", "#"), null,
			new DtBase("基底06", "boolean", "#", "#"), null,
			new DtBase("基底07", "string" , "#", "#"), null,
			new DtBase("基底07", "decimal", "#", "#"), null,
			new DtBase("基底07", "boolean", "#", "#"), null,
			new DtBase("基底08", "string" , "#", "#"), null,
			new DtBase("基底08", "decimal", "#", "#"), null,
			new DtBase("基底08", "boolean", "#", "#"), null,
			new DtBase("基底09", "string" , "#", "#"), null,
			new DtBase("基底09", "decimal", "#", "#"), null,
			new DtBase("基底09", "boolean", "#", "#"), null,
			new DtBase("基底0a", "string" , "#", "#"), null,
			new DtBase("基底0a", "decimal", "#", "#"), null,
			new DtBase("基底0a", "boolean", "#", "#"), null,
			new DtBase("基底0b", "string" , "#", "#"), null,
			new DtBase("基底0b", "decimal", "#", "#"), null,
			new DtBase("基底0b", "boolean", "#", "#"), null,
			new DtBase("基底0c", "string" , "#", "#"), null,
			new DtBase("基底0c", "decimal", "#", "#"), null,
			new DtBase("基底0c", "boolean", "#", "#"), null,
			new DtBase("基底0d", "string" , "#", "#"), null,
			new DtBase("基底0d", "decimal", "#", "#"), null,
			new DtBase("基底0d", "boolean", "#", "#"), null,
		},
		{
			new DtBase("基底01", "string" , "#", "#"), "文字列01",
			new DtBase("基底01", "decimal", "#", "#"), new BigDecimal("-0.01"),
			new DtBase("基底01", "boolean", "#", "#"), true,
			new DtBase("基底02", "string" , "#", "#"), "!%!%!%文字列02",
			new DtBase("基底02", "decimal", "#", "#"), new BigDecimal("123.02"),
			new DtBase("基底02", "boolean", "#", "#"), false,
			new DtBase("基底03", "string" , "#", "#"), "文字列03",
			new DtBase("基底03", "decimal", "#", "#"), new BigDecimal("10.03"),
			new DtBase("基底03", "boolean", "#", "#"), true,
			new DtBase("基底04", "string" , "#", "#"), "文字列!%04",
			new DtBase("基底04", "decimal", "#", "#"), new BigDecimal("-975.04"),
			new DtBase("基底04", "boolean", "#", "#"), false,
			new DtBase("基底05", "string" , "#", "#"), "文字列05",
			new DtBase("基底05", "decimal", "#", "#"), new BigDecimal("2345.05"),
			new DtBase("基底05", "boolean", "#", "#"), true,
			new DtBase("基底06", "string" , "#", "#"), " !%文字列06",
			new DtBase("基底06", "decimal", "#", "#"), new BigDecimal("-1006"),
			new DtBase("基底06", "boolean", "#", "#"), false,
			new DtBase("基底07", "string" , "#", "#"), "文字列07",
			new DtBase("基底07", "decimal", "#", "#"), new BigDecimal("513534321207"),
			new DtBase("基底07", "boolean", "#", "#"), true,
			new DtBase("基底08", "string" , "#", "#"), "文字列08",
			new DtBase("基底08", "decimal", "#", "#"), new BigDecimal("-0.2108"),
			new DtBase("基底08", "boolean", "#", "#"), false,
			new DtBase("基底09", "string" , "#", "#"), "文字列09",
			new DtBase("基底09", "decimal", "#", "#"), new BigDecimal("0.109"),
			new DtBase("基底09", "boolean", "#", "#"), true,
			new DtBase("基底0a", "string" , "#", "#"), null,
			new DtBase("基底0a", "decimal", "#", "#"), null,
			new DtBase("基底0a", "boolean", "#", "#"), null,
			new DtBase("基底0b", "string" , "#", "#"), "",
			new DtBase("基底0b", "decimal", "#", "#"), BigDecimal.ZERO,
			new DtBase("基底0b", "boolean", "#", "#"), false,
			new DtBase("基底0c", "string" , "#", "#"), "!%n",
			new DtBase("基底0c", "decimal", "#", "#"), new BigDecimal("1.12"),
			new DtBase("基底0c", "boolean", "#", "#"), true,
			new DtBase("基底0d", "string" , "#", "#"), "!%",
			new DtBase("基底0d", "decimal", "#", "#"), new BigDecimal("-1.13"),
			new DtBase("基底0d", "boolean", "#", "#"), false,
		},
	};

	// 書き込み用ランダム順序のテストデータ(空文字列も含む)
	static protected final Object[][] writeRandomAlgeSetData =
	{
		{
			new DtBase("基底08", "string" , "#", "#"), "文字列08",
			new DtBase("基底08", "decimal", "#", "#"), new BigDecimal("-0.2108"),
			new DtBase("基底08", "boolean", "#", "#"), false,
			new DtBase("基底02", "string" , "#", "#"), "!%!%!%文字列02",
			new DtBase("基底02", "decimal", "#", "#"), new BigDecimal("123.02"),
			new DtBase("基底02", "boolean", "#", "#"), false,
			new DtBase("基底0d", "string" , "#", "#"), "!%",
			new DtBase("基底0d", "decimal", "#", "#"), new BigDecimal("-1.13"),
			new DtBase("基底0d", "boolean", "#", "#"), false,
			new DtBase("基底06", "string" , "#", "#"), " !%文字列06",
			new DtBase("基底06", "decimal", "#", "#"), new BigDecimal("-1006"),
			new DtBase("基底06", "boolean", "#", "#"), false,
			new DtBase("基底04", "string" , "#", "#"), "文字列!%04",
			new DtBase("基底04", "decimal", "#", "#"), new BigDecimal("-975.04"),
			new DtBase("基底04", "boolean", "#", "#"), false,
			new DtBase("基底0c", "string" , "#", "#"), "!%n",
			new DtBase("基底0c", "decimal", "#", "#"), new BigDecimal("1.12"),
			new DtBase("基底0c", "boolean", "#", "#"), true,
			new DtBase("基底0b", "string" , "#", "#"), "",
			new DtBase("基底0b", "decimal", "#", "#"), BigDecimal.ZERO,
			new DtBase("基底0b", "boolean", "#", "#"), false,
			new DtBase("基底0a", "string" , "#", "#"), null,
			new DtBase("基底0a", "decimal", "#", "#"), null,
			new DtBase("基底0a", "boolean", "#", "#"), null,
		},
		{
			new DtBase("基底0d", "string" , "#", "#"), "!%!%文字列1d",
			new DtBase("基底0d", "decimal", "#", "#"), new BigDecimal("1.13"),
			new DtBase("基底0d", "boolean", "#", "#"), true,
			new DtBase("基底03", "string" , "#", "#"), "文字列13",
			new DtBase("基底03", "decimal", "#", "#"), new BigDecimal("1.03"),
			new DtBase("基底03", "boolean", "#", "#"), false,
			new DtBase("基底09", "string" , "#", "#"), "文字列19",
			new DtBase("基底09", "decimal", "#", "#"), new BigDecimal("1.09"),
			new DtBase("基底09", "boolean", "#", "#"), false,
			new DtBase("基底07", "string" , "#", "#"), "文字列17",
			new DtBase("基底07", "decimal", "#", "#"), new BigDecimal("1.07"),
			new DtBase("基底07", "boolean", "#", "#"), false,
			new DtBase("基底05", "string" , "#", "#"), "文字列15",
			new DtBase("基底05", "decimal", "#", "#"), new BigDecimal("1.05"),
			new DtBase("基底05", "boolean", "#", "#"), false,
			new DtBase("基底0b", "string" , "#", "#"), "!%",
			new DtBase("基底0b", "decimal", "#", "#"), new BigDecimal("1.11"),
			new DtBase("基底0b", "boolean", "#", "#"), true,
			new DtBase("基底01", "string" , "#", "#"), "文字列11",
			new DtBase("基底01", "decimal", "#", "#"), new BigDecimal("1.01"),
			new DtBase("基底01", "boolean", "#", "#"), false,
		},
		{
			new DtBase("基底03", "boolean", "#", "#"), true,
			new DtBase("基底0d", "decimal", "#", "#"), new BigDecimal("-2.13"),
			new DtBase("基底03", "decimal", "#", "#"), new BigDecimal("-2.03"),
			new DtBase("基底08", "decimal", "#", "#"), new BigDecimal("-2.08"),
			new DtBase("基底0d", "boolean", "#", "#"), null,
			new DtBase("基底04", "string" , "#", "#"), " !%文字列24",
			new DtBase("基底0b", "decimal", "#", "#"), new BigDecimal("-2.11"),
			new DtBase("基底07", "boolean", "#", "#"), false,
			new DtBase("基底0c", "decimal", "#", "#"), new BigDecimal("-2.12"),
			new DtBase("基底0b", "boolean", "#", "#"), false,
			new DtBase("基底04", "decimal", "#", "#"), new BigDecimal("-2.04"),
			new DtBase("基底04", "boolean", "#", "#"), true,
			new DtBase("基底07", "string" , "#", "#"), "文字列!%27",
			new DtBase("基底08", "string" , "#", "#"), "文字列28",
			new DtBase("基底03", "string" , "#", "#"), "文字列23",
			new DtBase("基底08", "boolean", "#", "#"), false,
			new DtBase("基底0b", "string" , "#", "#"), "文字列2b",
			new DtBase("基底0c", "string" , "#", "#"), "!%文字列2c",
			new DtBase("基底07", "decimal", "#", "#"), new BigDecimal("-2.07"),
			new DtBase("基底0c", "boolean", "#", "#"), false,
			new DtBase("基底0d", "string" , "#", "#"), "!%!%文字列2d",
		},
		{
			new DtBase("基底01", "string" , "#", "#"), null,
			new DtBase("基底0a", "boolean", "#", "#"), false,
			new DtBase("基底02", "boolean", "#", "#"), false,
			new DtBase("基底05", "decimal", "#", "#"), new BigDecimal("3.05"),
			new DtBase("基底05", "boolean", "#", "#"), true,
			new DtBase("基底09", "decimal", "#", "#"), new BigDecimal("3.09"),
			new DtBase("基底09", "boolean", "#", "#"), false,
			new DtBase("基底0a", "string" , "#", "#"), null,
			new DtBase("基底01", "boolean", "#", "#"), false,
			new DtBase("基底02", "string" , "#", "#"), null,
			new DtBase("基底0a", "decimal", "#", "#"), new BigDecimal("3.10"),
			new DtBase("基底02", "decimal", "#", "#"), new BigDecimal("3.02"),
			new DtBase("基底0d", "string" , "#", "#"), null,
			new DtBase("基底06", "string" , "#", "#"), null,
			new DtBase("基底06", "decimal", "#", "#"), new BigDecimal("3.06"),
			new DtBase("基底06", "boolean", "#", "#"), true,
			new DtBase("基底09", "string" , "#", "#"), null,
			new DtBase("基底05", "string" , "#", "#"), null,
			new DtBase("基底01", "decimal", "#", "#"), new BigDecimal("3.01"),
			new DtBase("基底0d", "decimal", "#", "#"), new BigDecimal("3.13"),
			new DtBase("基底0d", "boolean", "#", "#"), false,
		},
		{
			new DtBase("基底09", "boolean", "#", "#"), false,
			new DtBase("基底05", "decimal", "#", "#"), null,
			new DtBase("基底02", "boolean", "#", "#"), false,
			new DtBase("基底05", "string" , "#", "#"), "文字列45",
			new DtBase("基底05", "boolean", "#", "#"), false,
			new DtBase("基底02", "string" , "#", "#"), "文字列42",
			new DtBase("基底09", "string" , "#", "#"), "文字列49",
			new DtBase("基底02", "decimal", "#", "#"), null,
			new DtBase("基底09", "decimal", "#", "#"), null,
		},
		{
			new DtBase("基底01", "decimal", "#", "#"), null,
			new DtBase("基底0b", "boolean", "#", "#"), null,
			new DtBase("基底0d", "string" , "#", "#"), null,
			new DtBase("基底03", "decimal", "#", "#"), null,
			new DtBase("基底08", "boolean", "#", "#"), null,
			new DtBase("基底0a", "string" , "#", "#"), null,
			new DtBase("基底03", "boolean", "#", "#"), null,
			new DtBase("基底07", "string" , "#", "#"), null,
			new DtBase("基底03", "string" , "#", "#"), null,
			new DtBase("基底07", "decimal", "#", "#"), null,
			new DtBase("基底08", "decimal", "#", "#"), null,
			new DtBase("基底0a", "decimal", "#", "#"), null,
			new DtBase("基底0a", "boolean", "#", "#"), null,
			new DtBase("基底0b", "string" , "#", "#"), null,
			new DtBase("基底01", "boolean", "#", "#"), null,
			new DtBase("基底07", "boolean", "#", "#"), null,
			new DtBase("基底08", "string" , "#", "#"), null,
			new DtBase("基底0b", "decimal", "#", "#"), null,
			new DtBase("基底0d", "decimal", "#", "#"), null,
			new DtBase("基底0d", "boolean", "#", "#"), null,
			new DtBase("基底01", "string" , "#", "#"), null,
		},
		{
			new DtBase("基底08", "string" , "#", "#"), "文字列08",
			new DtBase("基底08", "decimal", "#", "#"), new BigDecimal("-0.2108"),
			new DtBase("基底08", "boolean", "#", "#"), false,
			new DtBase("基底02", "string" , "#", "#"), "!%!%!%文字列02",
			new DtBase("基底02", "decimal", "#", "#"), new BigDecimal("123.02"),
			new DtBase("基底02", "boolean", "#", "#"), false,
			new DtBase("基底0d", "string" , "#", "#"), "!%",
			new DtBase("基底0d", "decimal", "#", "#"), new BigDecimal("-1.13"),
			new DtBase("基底0d", "boolean", "#", "#"), false,
			new DtBase("基底06", "string" , "#", "#"), " !%文字列06",
			new DtBase("基底06", "decimal", "#", "#"), new BigDecimal("-1006"),
			new DtBase("基底06", "boolean", "#", "#"), false,
			new DtBase("基底04", "string" , "#", "#"), "文字列!%04",
			new DtBase("基底04", "decimal", "#", "#"), new BigDecimal("-975.04"),
			new DtBase("基底04", "boolean", "#", "#"), false,
			new DtBase("基底0c", "string" , "#", "#"), "!%n",
			new DtBase("基底0c", "decimal", "#", "#"), new BigDecimal("1.12"),
			new DtBase("基底0c", "boolean", "#", "#"), true,
			new DtBase("基底0b", "string" , "#", "#"), "",
			new DtBase("基底0b", "decimal", "#", "#"), BigDecimal.ZERO,
			new DtBase("基底0b", "boolean", "#", "#"), false,
			new DtBase("基底0a", "string" , "#", "#"), null,
			new DtBase("基底0a", "decimal", "#", "#"), null,
			new DtBase("基底0a", "boolean", "#", "#"), null,
		},
	};

	// 読み込み用ランダム順序のテストデータ(空文字列はnull)
	static protected final Object[][] readRandomAlgeSetData =
	{
		{
			new DtBase("基底08", "string" , "#", "#"), "文字列08",
			new DtBase("基底08", "decimal", "#", "#"), new BigDecimal("-0.2108"),
			new DtBase("基底08", "boolean", "#", "#"), false,
			new DtBase("基底02", "string" , "#", "#"), "!%!%!%文字列02",
			new DtBase("基底02", "decimal", "#", "#"), new BigDecimal("123.02"),
			new DtBase("基底02", "boolean", "#", "#"), false,
			new DtBase("基底0d", "string" , "#", "#"), "!%",
			new DtBase("基底0d", "decimal", "#", "#"), new BigDecimal("-1.13"),
			new DtBase("基底0d", "boolean", "#", "#"), false,
			new DtBase("基底06", "string" , "#", "#"), " !%文字列06",
			new DtBase("基底06", "decimal", "#", "#"), new BigDecimal("-1006"),
			new DtBase("基底06", "boolean", "#", "#"), false,
			new DtBase("基底04", "string" , "#", "#"), "文字列!%04",
			new DtBase("基底04", "decimal", "#", "#"), new BigDecimal("-975.04"),
			new DtBase("基底04", "boolean", "#", "#"), false,
			new DtBase("基底0c", "string" , "#", "#"), "!%n",
			new DtBase("基底0c", "decimal", "#", "#"), new BigDecimal("1.12"),
			new DtBase("基底0c", "boolean", "#", "#"), true,
			new DtBase("基底0b", "string" , "#", "#"), null,
			new DtBase("基底0b", "decimal", "#", "#"), BigDecimal.ZERO,
			new DtBase("基底0b", "boolean", "#", "#"), false,
			new DtBase("基底0a", "string" , "#", "#"), null,
			new DtBase("基底0a", "decimal", "#", "#"), null,
			new DtBase("基底0a", "boolean", "#", "#"), null,
		},
		{
			new DtBase("基底0d", "string" , "#", "#"), "!%!%文字列1d",
			new DtBase("基底0d", "decimal", "#", "#"), new BigDecimal("1.13"),
			new DtBase("基底0d", "boolean", "#", "#"), true,
			new DtBase("基底03", "string" , "#", "#"), "文字列13",
			new DtBase("基底03", "decimal", "#", "#"), new BigDecimal("1.03"),
			new DtBase("基底03", "boolean", "#", "#"), false,
			new DtBase("基底09", "string" , "#", "#"), "文字列19",
			new DtBase("基底09", "decimal", "#", "#"), new BigDecimal("1.09"),
			new DtBase("基底09", "boolean", "#", "#"), false,
			new DtBase("基底07", "string" , "#", "#"), "文字列17",
			new DtBase("基底07", "decimal", "#", "#"), new BigDecimal("1.07"),
			new DtBase("基底07", "boolean", "#", "#"), false,
			new DtBase("基底05", "string" , "#", "#"), "文字列15",
			new DtBase("基底05", "decimal", "#", "#"), new BigDecimal("1.05"),
			new DtBase("基底05", "boolean", "#", "#"), false,
			new DtBase("基底0b", "string" , "#", "#"), "!%",
			new DtBase("基底0b", "decimal", "#", "#"), new BigDecimal("1.11"),
			new DtBase("基底0b", "boolean", "#", "#"), true,
			new DtBase("基底01", "string" , "#", "#"), "文字列11",
			new DtBase("基底01", "decimal", "#", "#"), new BigDecimal("1.01"),
			new DtBase("基底01", "boolean", "#", "#"), false,
		},
		{
			new DtBase("基底03", "boolean", "#", "#"), true,
			new DtBase("基底0d", "decimal", "#", "#"), new BigDecimal("-2.13"),
			new DtBase("基底03", "decimal", "#", "#"), new BigDecimal("-2.03"),
			new DtBase("基底08", "decimal", "#", "#"), new BigDecimal("-2.08"),
			new DtBase("基底0d", "boolean", "#", "#"), null,
			new DtBase("基底04", "string" , "#", "#"), " !%文字列24",
			new DtBase("基底0b", "decimal", "#", "#"), new BigDecimal("-2.11"),
			new DtBase("基底07", "boolean", "#", "#"), false,
			new DtBase("基底0c", "decimal", "#", "#"), new BigDecimal("-2.12"),
			new DtBase("基底0b", "boolean", "#", "#"), false,
			new DtBase("基底04", "decimal", "#", "#"), new BigDecimal("-2.04"),
			new DtBase("基底04", "boolean", "#", "#"), true,
			new DtBase("基底07", "string" , "#", "#"), "文字列!%27",
			new DtBase("基底08", "string" , "#", "#"), "文字列28",
			new DtBase("基底03", "string" , "#", "#"), "文字列23",
			new DtBase("基底08", "boolean", "#", "#"), false,
			new DtBase("基底0b", "string" , "#", "#"), "文字列2b",
			new DtBase("基底0c", "string" , "#", "#"), "!%文字列2c",
			new DtBase("基底07", "decimal", "#", "#"), new BigDecimal("-2.07"),
			new DtBase("基底0c", "boolean", "#", "#"), false,
			new DtBase("基底0d", "string" , "#", "#"), "!%!%文字列2d",
		},
		{
			new DtBase("基底01", "string" , "#", "#"), null,
			new DtBase("基底0a", "boolean", "#", "#"), false,
			new DtBase("基底02", "boolean", "#", "#"), false,
			new DtBase("基底05", "decimal", "#", "#"), new BigDecimal("3.05"),
			new DtBase("基底05", "boolean", "#", "#"), true,
			new DtBase("基底09", "decimal", "#", "#"), new BigDecimal("3.09"),
			new DtBase("基底09", "boolean", "#", "#"), false,
			new DtBase("基底0a", "string" , "#", "#"), null,
			new DtBase("基底01", "boolean", "#", "#"), false,
			new DtBase("基底02", "string" , "#", "#"), null,
			new DtBase("基底0a", "decimal", "#", "#"), new BigDecimal("3.10"),
			new DtBase("基底02", "decimal", "#", "#"), new BigDecimal("3.02"),
			new DtBase("基底0d", "string" , "#", "#"), null,
			new DtBase("基底06", "string" , "#", "#"), null,
			new DtBase("基底06", "decimal", "#", "#"), new BigDecimal("3.06"),
			new DtBase("基底06", "boolean", "#", "#"), true,
			new DtBase("基底09", "string" , "#", "#"), null,
			new DtBase("基底05", "string" , "#", "#"), null,
			new DtBase("基底01", "decimal", "#", "#"), new BigDecimal("3.01"),
			new DtBase("基底0d", "decimal", "#", "#"), new BigDecimal("3.13"),
			new DtBase("基底0d", "boolean", "#", "#"), false,
		},
		{
			new DtBase("基底09", "boolean", "#", "#"), false,
			new DtBase("基底05", "decimal", "#", "#"), null,
			new DtBase("基底02", "boolean", "#", "#"), false,
			new DtBase("基底05", "string" , "#", "#"), "文字列45",
			new DtBase("基底05", "boolean", "#", "#"), false,
			new DtBase("基底02", "string" , "#", "#"), "文字列42",
			new DtBase("基底09", "string" , "#", "#"), "文字列49",
			new DtBase("基底02", "decimal", "#", "#"), null,
			new DtBase("基底09", "decimal", "#", "#"), null,
		},
		{
			new DtBase("基底01", "decimal", "#", "#"), null,
			new DtBase("基底0b", "boolean", "#", "#"), null,
			new DtBase("基底0d", "string" , "#", "#"), null,
			new DtBase("基底03", "decimal", "#", "#"), null,
			new DtBase("基底08", "boolean", "#", "#"), null,
			new DtBase("基底0a", "string" , "#", "#"), null,
			new DtBase("基底03", "boolean", "#", "#"), null,
			new DtBase("基底07", "string" , "#", "#"), null,
			new DtBase("基底03", "string" , "#", "#"), null,
			new DtBase("基底07", "decimal", "#", "#"), null,
			new DtBase("基底08", "decimal", "#", "#"), null,
			new DtBase("基底0a", "decimal", "#", "#"), null,
			new DtBase("基底0a", "boolean", "#", "#"), null,
			new DtBase("基底0b", "string" , "#", "#"), null,
			new DtBase("基底01", "boolean", "#", "#"), null,
			new DtBase("基底07", "boolean", "#", "#"), null,
			new DtBase("基底08", "string" , "#", "#"), null,
			new DtBase("基底0b", "decimal", "#", "#"), null,
			new DtBase("基底0d", "decimal", "#", "#"), null,
			new DtBase("基底0d", "boolean", "#", "#"), null,
			new DtBase("基底01", "string" , "#", "#"), null,
		},
		{
			new DtBase("基底08", "string" , "#", "#"), "文字列08",
			new DtBase("基底08", "decimal", "#", "#"), new BigDecimal("-0.2108"),
			new DtBase("基底08", "boolean", "#", "#"), false,
			new DtBase("基底02", "string" , "#", "#"), "!%!%!%文字列02",
			new DtBase("基底02", "decimal", "#", "#"), new BigDecimal("123.02"),
			new DtBase("基底02", "boolean", "#", "#"), false,
			new DtBase("基底0d", "string" , "#", "#"), "!%",
			new DtBase("基底0d", "decimal", "#", "#"), new BigDecimal("-1.13"),
			new DtBase("基底0d", "boolean", "#", "#"), false,
			new DtBase("基底06", "string" , "#", "#"), " !%文字列06",
			new DtBase("基底06", "decimal", "#", "#"), new BigDecimal("-1006"),
			new DtBase("基底06", "boolean", "#", "#"), false,
			new DtBase("基底04", "string" , "#", "#"), "文字列!%04",
			new DtBase("基底04", "decimal", "#", "#"), new BigDecimal("-975.04"),
			new DtBase("基底04", "boolean", "#", "#"), false,
			new DtBase("基底0c", "string" , "#", "#"), "!%n",
			new DtBase("基底0c", "decimal", "#", "#"), new BigDecimal("1.12"),
			new DtBase("基底0c", "boolean", "#", "#"), true,
			new DtBase("基底0b", "string" , "#", "#"), null,
			new DtBase("基底0b", "decimal", "#", "#"), BigDecimal.ZERO,
			new DtBase("基底0b", "boolean", "#", "#"), false,
			new DtBase("基底0a", "string" , "#", "#"), null,
			new DtBase("基底0a", "decimal", "#", "#"), null,
			new DtBase("基底0a", "boolean", "#", "#"), null,
		},
	};

	// テーブル形式CSV読み込み用ランダム順序のテストデータ(空文字列はnull)
	static protected final Object[][] readRandomTableAlgeSetData =
	{
		{
			new DtBase("基底08", "string" , "#", "#"), "文字列08",
			new DtBase("基底08", "decimal", "#", "#"), new BigDecimal("-0.2108"),
			new DtBase("基底08", "boolean", "#", "#"), false,
			new DtBase("基底02", "string" , "#", "#"), "!%!%!%文字列02",
			new DtBase("基底02", "decimal", "#", "#"), new BigDecimal("123.02"),
			new DtBase("基底02", "boolean", "#", "#"), false,
			new DtBase("基底0d", "string" , "#", "#"), "!%",
			new DtBase("基底0d", "decimal", "#", "#"), new BigDecimal("-1.13"),
			new DtBase("基底0d", "boolean", "#", "#"), false,
			new DtBase("基底06", "string" , "#", "#"), " !%文字列06",
			new DtBase("基底06", "decimal", "#", "#"), new BigDecimal("-1006"),
			new DtBase("基底06", "boolean", "#", "#"), false,
			new DtBase("基底04", "string" , "#", "#"), "文字列!%04",
			new DtBase("基底04", "decimal", "#", "#"), new BigDecimal("-975.04"),
			new DtBase("基底04", "boolean", "#", "#"), false,
			new DtBase("基底0c", "string" , "#", "#"), "!%n",
			new DtBase("基底0c", "decimal", "#", "#"), new BigDecimal("1.12"),
			new DtBase("基底0c", "boolean", "#", "#"), true,
			new DtBase("基底0b", "string" , "#", "#"), null,
			new DtBase("基底0b", "decimal", "#", "#"), BigDecimal.ZERO,
			new DtBase("基底0b", "boolean", "#", "#"), false,
			new DtBase("基底0a", "string" , "#", "#"), null,
			new DtBase("基底0a", "decimal", "#", "#"), null,
			new DtBase("基底0a", "boolean", "#", "#"), null,
		},
		{
			new DtBase("基底0d", "string" , "#", "#"), "!%!%文字列1d",
			new DtBase("基底0d", "decimal", "#", "#"), new BigDecimal("1.13"),
			new DtBase("基底0d", "boolean", "#", "#"), true,
			new DtBase("基底0b", "string" , "#", "#"), "!%",
			new DtBase("基底0b", "decimal", "#", "#"), new BigDecimal("1.11"),
			new DtBase("基底0b", "boolean", "#", "#"), true,
			new DtBase("基底03", "string" , "#", "#"), "文字列13",
			new DtBase("基底03", "decimal", "#", "#"), new BigDecimal("1.03"),
			new DtBase("基底03", "boolean", "#", "#"), false,
			new DtBase("基底09", "string" , "#", "#"), "文字列19",
			new DtBase("基底09", "decimal", "#", "#"), new BigDecimal("1.09"),
			new DtBase("基底09", "boolean", "#", "#"), false,
			new DtBase("基底07", "string" , "#", "#"), "文字列17",
			new DtBase("基底07", "decimal", "#", "#"), new BigDecimal("1.07"),
			new DtBase("基底07", "boolean", "#", "#"), false,
			new DtBase("基底05", "string" , "#", "#"), "文字列15",
			new DtBase("基底05", "decimal", "#", "#"), new BigDecimal("1.05"),
			new DtBase("基底05", "boolean", "#", "#"), false,
			new DtBase("基底01", "string" , "#", "#"), "文字列11",
			new DtBase("基底01", "decimal", "#", "#"), new BigDecimal("1.01"),
			new DtBase("基底01", "boolean", "#", "#"), false,
		},
		{
			new DtBase("基底08", "string" , "#", "#"), "文字列28",
			new DtBase("基底08", "decimal", "#", "#"), new BigDecimal("-2.08"),
			new DtBase("基底08", "boolean", "#", "#"), false,
			new DtBase("基底0d", "string" , "#", "#"), "!%!%文字列2d",
			new DtBase("基底0d", "decimal", "#", "#"), new BigDecimal("-2.13"),
			new DtBase("基底0d", "boolean", "#", "#"), null,
			new DtBase("基底04", "string" , "#", "#"), " !%文字列24",
			new DtBase("基底04", "decimal", "#", "#"), new BigDecimal("-2.04"),
			new DtBase("基底04", "boolean", "#", "#"), true,
			new DtBase("基底0c", "string" , "#", "#"), "!%文字列2c",
			new DtBase("基底0c", "decimal", "#", "#"), new BigDecimal("-2.12"),
			new DtBase("基底0c", "boolean", "#", "#"), false,
			new DtBase("基底0b", "string" , "#", "#"), "文字列2b",
			new DtBase("基底0b", "decimal", "#", "#"), new BigDecimal("-2.11"),
			new DtBase("基底0b", "boolean", "#", "#"), false,
			new DtBase("基底03", "string" , "#", "#"), "文字列23",
			new DtBase("基底03", "decimal", "#", "#"), new BigDecimal("-2.03"),
			new DtBase("基底03", "boolean", "#", "#"), true,
			new DtBase("基底07", "string" , "#", "#"), "文字列!%27",
			new DtBase("基底07", "decimal", "#", "#"), new BigDecimal("-2.07"),
			new DtBase("基底07", "boolean", "#", "#"), false,
		},
		{
			new DtBase("基底02", "string" , "#", "#"), null,
			new DtBase("基底02", "decimal", "#", "#"), new BigDecimal("3.02"),
			new DtBase("基底02", "boolean", "#", "#"), false,
			new DtBase("基底0d", "string" , "#", "#"), null,
			new DtBase("基底0d", "decimal", "#", "#"), new BigDecimal("3.13"),
			new DtBase("基底0d", "boolean", "#", "#"), false,
			new DtBase("基底06", "string" , "#", "#"), null,
			new DtBase("基底06", "decimal", "#", "#"), new BigDecimal("3.06"),
			new DtBase("基底06", "boolean", "#", "#"), true,
			new DtBase("基底0a", "string" , "#", "#"), null,
			new DtBase("基底0a", "decimal", "#", "#"), new BigDecimal("3.10"),
			new DtBase("基底0a", "boolean", "#", "#"), false,
			new DtBase("基底09", "string" , "#", "#"), null,
			new DtBase("基底09", "decimal", "#", "#"), new BigDecimal("3.09"),
			new DtBase("基底09", "boolean", "#", "#"), false,
			new DtBase("基底05", "string" , "#", "#"), null,
			new DtBase("基底05", "decimal", "#", "#"), new BigDecimal("3.05"),
			new DtBase("基底05", "boolean", "#", "#"), true,
			new DtBase("基底01", "string" , "#", "#"), null,
			new DtBase("基底01", "decimal", "#", "#"), new BigDecimal("3.01"),
			new DtBase("基底01", "boolean", "#", "#"), false,
		},
		{
			new DtBase("基底02", "string" , "#", "#"), "文字列42",
			new DtBase("基底02", "decimal", "#", "#"), null,
			new DtBase("基底02", "boolean", "#", "#"), false,
			new DtBase("基底09", "string" , "#", "#"), "文字列49",
			new DtBase("基底09", "decimal", "#", "#"), null,
			new DtBase("基底09", "boolean", "#", "#"), false,
			new DtBase("基底05", "string" , "#", "#"), "文字列45",
			new DtBase("基底05", "decimal", "#", "#"), null,
			new DtBase("基底05", "boolean", "#", "#"), false,
		},
		{
			new DtBase("基底08", "string" , "#", "#"), null,
			new DtBase("基底08", "decimal", "#", "#"), null,
			new DtBase("基底08", "boolean", "#", "#"), null,
			new DtBase("基底0d", "string" , "#", "#"), null,
			new DtBase("基底0d", "decimal", "#", "#"), null,
			new DtBase("基底0d", "boolean", "#", "#"), null,
			new DtBase("基底0b", "string" , "#", "#"), null,
			new DtBase("基底0b", "decimal", "#", "#"), null,
			new DtBase("基底0b", "boolean", "#", "#"), null,
			new DtBase("基底0a", "string" , "#", "#"), null,
			new DtBase("基底0a", "decimal", "#", "#"), null,
			new DtBase("基底0a", "boolean", "#", "#"), null,
			new DtBase("基底03", "string" , "#", "#"), null,
			new DtBase("基底03", "decimal", "#", "#"), null,
			new DtBase("基底03", "boolean", "#", "#"), null,
			new DtBase("基底07", "string" , "#", "#"), null,
			new DtBase("基底07", "decimal", "#", "#"), null,
			new DtBase("基底07", "boolean", "#", "#"), null,
			new DtBase("基底01", "string" , "#", "#"), null,
			new DtBase("基底01", "decimal", "#", "#"), null,
			new DtBase("基底01", "boolean", "#", "#"), null,
		},
		{
			new DtBase("基底08", "string" , "#", "#"), "文字列08",
			new DtBase("基底08", "decimal", "#", "#"), new BigDecimal("-0.2108"),
			new DtBase("基底08", "boolean", "#", "#"), false,
			new DtBase("基底02", "string" , "#", "#"), "!%!%!%文字列02",
			new DtBase("基底02", "decimal", "#", "#"), new BigDecimal("123.02"),
			new DtBase("基底02", "boolean", "#", "#"), false,
			new DtBase("基底0d", "string" , "#", "#"), "!%",
			new DtBase("基底0d", "decimal", "#", "#"), new BigDecimal("-1.13"),
			new DtBase("基底0d", "boolean", "#", "#"), false,
			new DtBase("基底06", "string" , "#", "#"), " !%文字列06",
			new DtBase("基底06", "decimal", "#", "#"), new BigDecimal("-1006"),
			new DtBase("基底06", "boolean", "#", "#"), false,
			new DtBase("基底04", "string" , "#", "#"), "文字列!%04",
			new DtBase("基底04", "decimal", "#", "#"), new BigDecimal("-975.04"),
			new DtBase("基底04", "boolean", "#", "#"), false,
			new DtBase("基底0c", "string" , "#", "#"), "!%n",
			new DtBase("基底0c", "decimal", "#", "#"), new BigDecimal("1.12"),
			new DtBase("基底0c", "boolean", "#", "#"), true,
			new DtBase("基底0b", "string" , "#", "#"), null,
			new DtBase("基底0b", "decimal", "#", "#"), BigDecimal.ZERO,
			new DtBase("基底0b", "boolean", "#", "#"), false,
			new DtBase("基底0a", "string" , "#", "#"), null,
			new DtBase("基底0a", "decimal", "#", "#"), null,
			new DtBase("基底0a", "boolean", "#", "#"), null,
		},
	};

	// 基準となる全ソート済みテストデータ(空文字列も含む)
	static protected final Object[][] readSortedAlgeSetData =
	{
		{
			new DtBase("基底02", "string" , "#", "#"), "!%!%!%文字列02",
			new DtBase("基底02", "decimal", "#", "#"), new BigDecimal("123.02"),
			new DtBase("基底02", "boolean", "#", "#"), false,
			new DtBase("基底04", "string" , "#", "#"), "文字列!%04",
			new DtBase("基底04", "decimal", "#", "#"), new BigDecimal("-975.04"),
			new DtBase("基底04", "boolean", "#", "#"), false,
			new DtBase("基底06", "string" , "#", "#"), " !%文字列06",
			new DtBase("基底06", "decimal", "#", "#"), new BigDecimal("-1006"),
			new DtBase("基底06", "boolean", "#", "#"), false,
			new DtBase("基底08", "string" , "#", "#"), "文字列08",
			new DtBase("基底08", "decimal", "#", "#"), new BigDecimal("-0.2108"),
			new DtBase("基底08", "boolean", "#", "#"), false,
			new DtBase("基底0a", "string" , "#", "#"), null,
			new DtBase("基底0a", "decimal", "#", "#"), null,
			new DtBase("基底0a", "boolean", "#", "#"), null,
			new DtBase("基底0b", "string" , "#", "#"), null,
			new DtBase("基底0b", "decimal", "#", "#"), BigDecimal.ZERO,
			new DtBase("基底0b", "boolean", "#", "#"), false,
			new DtBase("基底0c", "string" , "#", "#"), "!%n",
			new DtBase("基底0c", "decimal", "#", "#"), new BigDecimal("1.12"),
			new DtBase("基底0c", "boolean", "#", "#"), true,
			new DtBase("基底0d", "string" , "#", "#"), "!%",
			new DtBase("基底0d", "decimal", "#", "#"), new BigDecimal("-1.13"),
			new DtBase("基底0d", "boolean", "#", "#"), false,
		},
		{
			new DtBase("基底01", "string" , "#", "#"), "文字列11",
			new DtBase("基底01", "decimal", "#", "#"), new BigDecimal("1.01"),
			new DtBase("基底01", "boolean", "#", "#"), false,
			new DtBase("基底03", "string" , "#", "#"), "文字列13",
			new DtBase("基底03", "decimal", "#", "#"), new BigDecimal("1.03"),
			new DtBase("基底03", "boolean", "#", "#"), false,
			new DtBase("基底05", "string" , "#", "#"), "文字列15",
			new DtBase("基底05", "decimal", "#", "#"), new BigDecimal("1.05"),
			new DtBase("基底05", "boolean", "#", "#"), false,
			new DtBase("基底07", "string" , "#", "#"), "文字列17",
			new DtBase("基底07", "decimal", "#", "#"), new BigDecimal("1.07"),
			new DtBase("基底07", "boolean", "#", "#"), false,
			new DtBase("基底09", "string" , "#", "#"), "文字列19",
			new DtBase("基底09", "decimal", "#", "#"), new BigDecimal("1.09"),
			new DtBase("基底09", "boolean", "#", "#"), false,
			new DtBase("基底0b", "string" , "#", "#"), "!%",
			new DtBase("基底0b", "decimal", "#", "#"), new BigDecimal("1.11"),
			new DtBase("基底0b", "boolean", "#", "#"), true,
			new DtBase("基底0d", "string" , "#", "#"), "!%!%文字列1d",
			new DtBase("基底0d", "decimal", "#", "#"), new BigDecimal("1.13"),
			new DtBase("基底0d", "boolean", "#", "#"), true,
		},
		{
			new DtBase("基底03", "string" , "#", "#"), "文字列23",
			new DtBase("基底03", "decimal", "#", "#"), new BigDecimal("-2.03"),
			new DtBase("基底03", "boolean", "#", "#"), true,
			new DtBase("基底04", "string" , "#", "#"), " !%文字列24",
			new DtBase("基底04", "decimal", "#", "#"), new BigDecimal("-2.04"),
			new DtBase("基底04", "boolean", "#", "#"), true,
			new DtBase("基底07", "string" , "#", "#"), "文字列!%27",
			new DtBase("基底07", "decimal", "#", "#"), new BigDecimal("-2.07"),
			new DtBase("基底07", "boolean", "#", "#"), false,
			new DtBase("基底08", "string" , "#", "#"), "文字列28",
			new DtBase("基底08", "decimal", "#", "#"), new BigDecimal("-2.08"),
			new DtBase("基底08", "boolean", "#", "#"), false,
			new DtBase("基底0b", "string" , "#", "#"), "文字列2b",
			new DtBase("基底0b", "decimal", "#", "#"), new BigDecimal("-2.11"),
			new DtBase("基底0b", "boolean", "#", "#"), false,
			new DtBase("基底0c", "string" , "#", "#"), "!%文字列2c",
			new DtBase("基底0c", "decimal", "#", "#"), new BigDecimal("-2.12"),
			new DtBase("基底0c", "boolean", "#", "#"), false,
			new DtBase("基底0d", "string" , "#", "#"), "!%!%文字列2d",
			new DtBase("基底0d", "decimal", "#", "#"), new BigDecimal("-2.13"),
			new DtBase("基底0d", "boolean", "#", "#"), null,
		},
		{
			new DtBase("基底01", "string" , "#", "#"), null,
			new DtBase("基底01", "decimal", "#", "#"), new BigDecimal("3.01"),
			new DtBase("基底01", "boolean", "#", "#"), false,
			new DtBase("基底02", "string" , "#", "#"), null,
			new DtBase("基底02", "decimal", "#", "#"), new BigDecimal("3.02"),
			new DtBase("基底02", "boolean", "#", "#"), false,
			new DtBase("基底05", "string" , "#", "#"), null,
			new DtBase("基底05", "decimal", "#", "#"), new BigDecimal("3.05"),
			new DtBase("基底05", "boolean", "#", "#"), true,
			new DtBase("基底06", "string" , "#", "#"), null,
			new DtBase("基底06", "decimal", "#", "#"), new BigDecimal("3.06"),
			new DtBase("基底06", "boolean", "#", "#"), true,
			new DtBase("基底09", "string" , "#", "#"), null,
			new DtBase("基底09", "decimal", "#", "#"), new BigDecimal("3.09"),
			new DtBase("基底09", "boolean", "#", "#"), false,
			new DtBase("基底0a", "string" , "#", "#"), null,
			new DtBase("基底0a", "decimal", "#", "#"), new BigDecimal("3.10"),
			new DtBase("基底0a", "boolean", "#", "#"), false,
			new DtBase("基底0d", "string" , "#", "#"), null,
			new DtBase("基底0d", "decimal", "#", "#"), new BigDecimal("3.13"),
			new DtBase("基底0d", "boolean", "#", "#"), false,
		},
		{
			new DtBase("基底02", "string" , "#", "#"), "文字列42",
			new DtBase("基底02", "decimal", "#", "#"), null,
			new DtBase("基底02", "boolean", "#", "#"), false,
			new DtBase("基底05", "string" , "#", "#"), "文字列45",
			new DtBase("基底05", "decimal", "#", "#"), null,
			new DtBase("基底05", "boolean", "#", "#"), false,
			new DtBase("基底09", "string" , "#", "#"), "文字列49",
			new DtBase("基底09", "decimal", "#", "#"), null,
			new DtBase("基底09", "boolean", "#", "#"), false,
		},
		{
			new DtBase("基底01", "string" , "#", "#"), null,
			new DtBase("基底01", "decimal", "#", "#"), null,
			new DtBase("基底01", "boolean", "#", "#"), null,
			new DtBase("基底03", "string" , "#", "#"), null,
			new DtBase("基底03", "decimal", "#", "#"), null,
			new DtBase("基底03", "boolean", "#", "#"), null,
			new DtBase("基底07", "string" , "#", "#"), null,
			new DtBase("基底07", "decimal", "#", "#"), null,
			new DtBase("基底07", "boolean", "#", "#"), null,
			new DtBase("基底08", "string" , "#", "#"), null,
			new DtBase("基底08", "decimal", "#", "#"), null,
			new DtBase("基底08", "boolean", "#", "#"), null,
			new DtBase("基底0a", "string" , "#", "#"), null,
			new DtBase("基底0a", "decimal", "#", "#"), null,
			new DtBase("基底0a", "boolean", "#", "#"), null,
			new DtBase("基底0b", "string" , "#", "#"), null,
			new DtBase("基底0b", "decimal", "#", "#"), null,
			new DtBase("基底0b", "boolean", "#", "#"), null,
			new DtBase("基底0d", "string" , "#", "#"), null,
			new DtBase("基底0d", "decimal", "#", "#"), null,
			new DtBase("基底0d", "boolean", "#", "#"), null,
		},
		{
			new DtBase("基底02", "string" , "#", "#"), "!%!%!%文字列02",
			new DtBase("基底02", "decimal", "#", "#"), new BigDecimal("123.02"),
			new DtBase("基底02", "boolean", "#", "#"), false,
			new DtBase("基底04", "string" , "#", "#"), "文字列!%04",
			new DtBase("基底04", "decimal", "#", "#"), new BigDecimal("-975.04"),
			new DtBase("基底04", "boolean", "#", "#"), false,
			new DtBase("基底06", "string" , "#", "#"), " !%文字列06",
			new DtBase("基底06", "decimal", "#", "#"), new BigDecimal("-1006"),
			new DtBase("基底06", "boolean", "#", "#"), false,
			new DtBase("基底08", "string" , "#", "#"), "文字列08",
			new DtBase("基底08", "decimal", "#", "#"), new BigDecimal("-0.2108"),
			new DtBase("基底08", "boolean", "#", "#"), false,
			new DtBase("基底0a", "string" , "#", "#"), null,
			new DtBase("基底0a", "decimal", "#", "#"), null,
			new DtBase("基底0a", "boolean", "#", "#"), null,
			new DtBase("基底0b", "string" , "#", "#"), null,
			new DtBase("基底0b", "decimal", "#", "#"), BigDecimal.ZERO,
			new DtBase("基底0b", "boolean", "#", "#"), false,
			new DtBase("基底0c", "string" , "#", "#"), "!%n",
			new DtBase("基底0c", "decimal", "#", "#"), new BigDecimal("1.12"),
			new DtBase("基底0c", "boolean", "#", "#"), true,
			new DtBase("基底0d", "string" , "#", "#"), "!%",
			new DtBase("基底0d", "decimal", "#", "#"), new BigDecimal("-1.13"),
			new DtBase("基底0d", "boolean", "#", "#"), false,
		},
	};

	// 基準となる部分ソート済みテストデータ(空文字列も含む)
	static protected final Object[][] readHalfSortedAlgeSetData =
	{
		{
			new DtBase("基底02", "string" , "#", "#"), "!%!%!%文字列02",
			new DtBase("基底02", "decimal", "#", "#"), new BigDecimal("123.02"),
			new DtBase("基底02", "boolean", "#", "#"), false,
			new DtBase("基底04", "string" , "#", "#"), "文字列!%04",
			new DtBase("基底04", "decimal", "#", "#"), new BigDecimal("-975.04"),
			new DtBase("基底04", "boolean", "#", "#"), false,
			new DtBase("基底06", "string" , "#", "#"), " !%文字列06",
			new DtBase("基底06", "decimal", "#", "#"), new BigDecimal("-1006"),
			new DtBase("基底06", "boolean", "#", "#"), false,
			new DtBase("基底08", "string" , "#", "#"), "文字列08",
			new DtBase("基底08", "decimal", "#", "#"), new BigDecimal("-0.2108"),
			new DtBase("基底08", "boolean", "#", "#"), false,
			new DtBase("基底0d", "string" , "#", "#"), "!%",
			new DtBase("基底0d", "decimal", "#", "#"), new BigDecimal("-1.13"),
			new DtBase("基底0d", "boolean", "#", "#"), false,
			new DtBase("基底0c", "string" , "#", "#"), "!%n",
			new DtBase("基底0c", "decimal", "#", "#"), new BigDecimal("1.12"),
			new DtBase("基底0c", "boolean", "#", "#"), true,
			new DtBase("基底0b", "string" , "#", "#"), null,
			new DtBase("基底0b", "decimal", "#", "#"), BigDecimal.ZERO,
			new DtBase("基底0b", "boolean", "#", "#"), false,
			new DtBase("基底0a", "string" , "#", "#"), null,
			new DtBase("基底0a", "decimal", "#", "#"), null,
			new DtBase("基底0a", "boolean", "#", "#"), null,
		},
		{
			new DtBase("基底01", "string" , "#", "#"), "文字列11",
			new DtBase("基底01", "decimal", "#", "#"), new BigDecimal("1.01"),
			new DtBase("基底01", "boolean", "#", "#"), false,
			new DtBase("基底03", "string" , "#", "#"), "文字列13",
			new DtBase("基底03", "decimal", "#", "#"), new BigDecimal("1.03"),
			new DtBase("基底03", "boolean", "#", "#"), false,
			new DtBase("基底05", "string" , "#", "#"), "文字列15",
			new DtBase("基底05", "decimal", "#", "#"), new BigDecimal("1.05"),
			new DtBase("基底05", "boolean", "#", "#"), false,
			new DtBase("基底07", "string" , "#", "#"), "文字列17",
			new DtBase("基底07", "decimal", "#", "#"), new BigDecimal("1.07"),
			new DtBase("基底07", "boolean", "#", "#"), false,
			new DtBase("基底09", "string" , "#", "#"), "文字列19",
			new DtBase("基底09", "decimal", "#", "#"), new BigDecimal("1.09"),
			new DtBase("基底09", "boolean", "#", "#"), false,
			new DtBase("基底0d", "string" , "#", "#"), "!%!%文字列1d",
			new DtBase("基底0d", "decimal", "#", "#"), new BigDecimal("1.13"),
			new DtBase("基底0d", "boolean", "#", "#"), true,
			new DtBase("基底0b", "string" , "#", "#"), "!%",
			new DtBase("基底0b", "decimal", "#", "#"), new BigDecimal("1.11"),
			new DtBase("基底0b", "boolean", "#", "#"), true,
		},
		{
			new DtBase("基底03", "string" , "#", "#"), "文字列23",
			new DtBase("基底03", "decimal", "#", "#"), new BigDecimal("-2.03"),
			new DtBase("基底03", "boolean", "#", "#"), true,
			new DtBase("基底04", "string" , "#", "#"), " !%文字列24",
			new DtBase("基底04", "decimal", "#", "#"), new BigDecimal("-2.04"),
			new DtBase("基底04", "boolean", "#", "#"), true,
			new DtBase("基底07", "string" , "#", "#"), "文字列!%27",
			new DtBase("基底07", "decimal", "#", "#"), new BigDecimal("-2.07"),
			new DtBase("基底07", "boolean", "#", "#"), false,
			new DtBase("基底08", "string" , "#", "#"), "文字列28",
			new DtBase("基底08", "decimal", "#", "#"), new BigDecimal("-2.08"),
			new DtBase("基底08", "boolean", "#", "#"), false,
			new DtBase("基底0d", "string" , "#", "#"), "!%!%文字列2d",
			new DtBase("基底0d", "decimal", "#", "#"), new BigDecimal("-2.13"),
			new DtBase("基底0d", "boolean", "#", "#"), null,
			new DtBase("基底0c", "string" , "#", "#"), "!%文字列2c",
			new DtBase("基底0c", "decimal", "#", "#"), new BigDecimal("-2.12"),
			new DtBase("基底0c", "boolean", "#", "#"), false,
			new DtBase("基底0b", "string" , "#", "#"), "文字列2b",
			new DtBase("基底0b", "decimal", "#", "#"), new BigDecimal("-2.11"),
			new DtBase("基底0b", "boolean", "#", "#"), false,
		},
		{
			new DtBase("基底01", "string" , "#", "#"), null,
			new DtBase("基底01", "decimal", "#", "#"), new BigDecimal("3.01"),
			new DtBase("基底01", "boolean", "#", "#"), false,
			new DtBase("基底02", "string" , "#", "#"), null,
			new DtBase("基底02", "decimal", "#", "#"), new BigDecimal("3.02"),
			new DtBase("基底02", "boolean", "#", "#"), false,
			new DtBase("基底05", "string" , "#", "#"), null,
			new DtBase("基底05", "decimal", "#", "#"), new BigDecimal("3.05"),
			new DtBase("基底05", "boolean", "#", "#"), true,
			new DtBase("基底06", "string" , "#", "#"), null,
			new DtBase("基底06", "decimal", "#", "#"), new BigDecimal("3.06"),
			new DtBase("基底06", "boolean", "#", "#"), true,
			new DtBase("基底09", "string" , "#", "#"), null,
			new DtBase("基底09", "decimal", "#", "#"), new BigDecimal("3.09"),
			new DtBase("基底09", "boolean", "#", "#"), false,
			new DtBase("基底0d", "string" , "#", "#"), null,
			new DtBase("基底0d", "decimal", "#", "#"), new BigDecimal("3.13"),
			new DtBase("基底0d", "boolean", "#", "#"), false,
			new DtBase("基底0a", "string" , "#", "#"), null,
			new DtBase("基底0a", "decimal", "#", "#"), new BigDecimal("3.10"),
			new DtBase("基底0a", "boolean", "#", "#"), false,
		},
		{
			new DtBase("基底02", "string" , "#", "#"), "文字列42",
			new DtBase("基底02", "decimal", "#", "#"), null,
			new DtBase("基底02", "boolean", "#", "#"), false,
			new DtBase("基底05", "string" , "#", "#"), "文字列45",
			new DtBase("基底05", "decimal", "#", "#"), null,
			new DtBase("基底05", "boolean", "#", "#"), false,
			new DtBase("基底09", "string" , "#", "#"), "文字列49",
			new DtBase("基底09", "decimal", "#", "#"), null,
			new DtBase("基底09", "boolean", "#", "#"), false,
		},
		{
			new DtBase("基底01", "string" , "#", "#"), null,
			new DtBase("基底01", "decimal", "#", "#"), null,
			new DtBase("基底01", "boolean", "#", "#"), null,
			new DtBase("基底03", "string" , "#", "#"), null,
			new DtBase("基底03", "decimal", "#", "#"), null,
			new DtBase("基底03", "boolean", "#", "#"), null,
			new DtBase("基底07", "string" , "#", "#"), null,
			new DtBase("基底07", "decimal", "#", "#"), null,
			new DtBase("基底07", "boolean", "#", "#"), null,
			new DtBase("基底08", "string" , "#", "#"), null,
			new DtBase("基底08", "decimal", "#", "#"), null,
			new DtBase("基底08", "boolean", "#", "#"), null,
			new DtBase("基底0d", "string" , "#", "#"), null,
			new DtBase("基底0d", "decimal", "#", "#"), null,
			new DtBase("基底0d", "boolean", "#", "#"), null,
			new DtBase("基底0b", "string" , "#", "#"), null,
			new DtBase("基底0b", "decimal", "#", "#"), null,
			new DtBase("基底0b", "boolean", "#", "#"), null,
			new DtBase("基底0a", "string" , "#", "#"), null,
			new DtBase("基底0a", "decimal", "#", "#"), null,
			new DtBase("基底0a", "boolean", "#", "#"), null,
		},
		{
			new DtBase("基底02", "string" , "#", "#"), "!%!%!%文字列02",
			new DtBase("基底02", "decimal", "#", "#"), new BigDecimal("123.02"),
			new DtBase("基底02", "boolean", "#", "#"), false,
			new DtBase("基底04", "string" , "#", "#"), "文字列!%04",
			new DtBase("基底04", "decimal", "#", "#"), new BigDecimal("-975.04"),
			new DtBase("基底04", "boolean", "#", "#"), false,
			new DtBase("基底06", "string" , "#", "#"), " !%文字列06",
			new DtBase("基底06", "decimal", "#", "#"), new BigDecimal("-1006"),
			new DtBase("基底06", "boolean", "#", "#"), false,
			new DtBase("基底08", "string" , "#", "#"), "文字列08",
			new DtBase("基底08", "decimal", "#", "#"), new BigDecimal("-0.2108"),
			new DtBase("基底08", "boolean", "#", "#"), false,
			new DtBase("基底0d", "string" , "#", "#"), "!%",
			new DtBase("基底0d", "decimal", "#", "#"), new BigDecimal("-1.13"),
			new DtBase("基底0d", "boolean", "#", "#"), false,
			new DtBase("基底0c", "string" , "#", "#"), "!%n",
			new DtBase("基底0c", "decimal", "#", "#"), new BigDecimal("1.12"),
			new DtBase("基底0c", "boolean", "#", "#"), true,
			new DtBase("基底0b", "string" , "#", "#"), null,
			new DtBase("基底0b", "decimal", "#", "#"), BigDecimal.ZERO,
			new DtBase("基底0b", "boolean", "#", "#"), false,
			new DtBase("基底0a", "string" , "#", "#"), null,
			new DtBase("基底0a", "decimal", "#", "#"), null,
			new DtBase("基底0a", "boolean", "#", "#"), null,
		},
	};
	
	static protected final File tocsv_def = new File(csvpath, "v2_OutputDtAlgeSet.csv");
	static protected final File tocsv_sjis = new File(csvpath, "v2_OutputDtAlgeSet_SJIS.csv");
	static protected final File tocsv_utf8 = new File(csvpath, "v2_OutputDtAlgeSet_UTF8.csv");
	static protected final File toxml = new File(xmlpath, "v2_OutputDtAlgeSet.xml");
	static protected final File fromcsv_def = new File(csvpath, "v2_NormalDtAlgeSet.csv");
	static protected final File fromcsv_sjis = new File(csvpath, "v2_NormalDtAlgeSet_SJIS.csv");
	static protected final File fromcsv_utf8 = new File(csvpath, "v2_NormalDtAlgeSet_UTF8.csv");
	static protected final File fromxml = new File(xmlpath, "v2_NormalDtAlgeSet.xml");
	static protected final File err_fromcsv_def = new File(csvpath, "v2_IllegalDtAlgeSet.csv");
	static protected final File err_fromcsv_sjis = new File(csvpath, "v2_IllegalDtAlgeSet_SJIS.csv");
	static protected final File err_fromcsv_utf8 = new File(csvpath, "v2_IllegalDtAlgeSet_UTF8.csv");
	

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
		assertTrue(DtAlgeSetTest.equalElementSequence(ret, readRandomAlgeSetData));
		
		// Illegal data
		target = err_fromcsv_def;
		//--- read
		try {
			ret = DtAlgeSet.fromCSV(target);
			fail("Must be throw CsvFormatException by CSV file \"" + target.getPath() + "\"");
		}
		catch (CsvFormatException ex) {
			System.err.println(ex.toString() + " from CSV file \"" + target.getPath() + "\"");
			assert(true);
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from CSV file \"" + target.getPath() + "\"");
		}
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
		assertTrue(DtAlgeSetTest.equalElementSequence(ret, readRandomAlgeSetData));
		
		// Illegal data
		target = err_fromcsv_sjis;
		ret = new DtAlgeSet();
		//--- read
		try {
			ret = DtAlgeSet.fromCSV(target, SJIS);
			fail("Must be throw CsvFormatException by CSV file \"" + target.getPath() + "\"");
		}
		catch (CsvFormatException ex) {
			System.err.println(ex.toString() + " from CSV file \"" + target.getPath() + "\"");
			assert(true);
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from CSV file \"" + target.getPath() + "\"");
		}
		
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
		assertTrue(DtAlgeSetTest.equalElementSequence(ret, readRandomAlgeSetData));
		
		// Illegal data
		target = err_fromcsv_utf8;
		ret = new DtAlgeSet();
		//--- read
		try {
			ret = DtAlgeSet.fromCSV(target, UTF8);
			fail("Must be throw CsvFormatException by CSV file \"" + target.getPath() + "\"");
		}
		catch (CsvFormatException ex) {
			System.err.println(ex.toString() + " from CSV file \"" + target.getPath() + "\"");
			assert(true);
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from CSV file \"" + target.getPath() + "\"");
		}
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
		assertTrue(DtAlgeSetTest.equalElementSequence(ret, readRandomAlgeSetData));
	}

	/**
	 * Test method for {@link dtalge.DtAlgeSet#toCSV(java.io.File)}.
	 */
	public void testToCSVFile() {
		File target = tocsv_def;
		DtAlgeSet data = new DtAlgeSet(DtAlgeSetTest.makeDtalges(writeRandomAlgeSetData));

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
			assertTrue(DtAlgeSetTest.equalElementSequence(ret, readRandomAlgeSetData));
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
		DtAlgeSet data = new DtAlgeSet(DtAlgeSetTest.makeDtalges(writeRandomAlgeSetData));

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
			assertTrue(DtAlgeSetTest.equalElementSequence(ret, readRandomAlgeSetData));
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
			assertTrue(DtAlgeSetTest.equalElementSequence(ret, readRandomAlgeSetData));
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
		DtAlgeSet data = new DtAlgeSet(DtAlgeSetTest.makeDtalges(writeRandomAlgeSetData));

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
			assertTrue(DtAlgeSetTest.equalElementSequence(ret, readRandomAlgeSetData));
		}
		catch (Exception ex) {
			System.err.println(ex.toString());
			ex.printStackTrace();
			fail("Failed to read from XML file \"" + target.getPath() + "\"");
		}
	}

}
