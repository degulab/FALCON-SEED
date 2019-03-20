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
 * @(#)DtBasePatternSetTest.java	0.20	2010/02/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtBasePatternSetTest.java	0.10	2008/09/02
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;
import dtalge.util.DtDataTypes;

/**
 * <code>DtBasePatternSet</code> のユニットテスト。
 * このテストケースでは、ファイル入出力のテストは含まない。
 * 
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class DtBasePatternSetTest extends TestCase
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final String NORMAL_BASEKEY_1 = "基底キー1";
	static private final String NORMAL_BASEKEY_2 = "基底キー2";
	static private final String NORMAL_BASEKEY_3 = "基底キー3";
	static private final String NORMAL_BASEKEY_4 = "基底キー4";
	static private final String NORMAL_BASEKEY_5 = "基底キー5";
	static private final String NORMAL_BASEKEY_A = "基底*";
	static private final String NORMAL_BASEKEY_B = "基底*2";
	static private final String NORMAL_BASEKEY_C = "基底*3";
	static private final String NORMAL_BASEKEY_D = "基底*4";
	static private final String NORMAL_BASEKEY_E = "基底*5";
	//static private final String NORMAL_BASEKEY_MARK = "記号!\"#$%&'()=-=^~\\|@`[{;+:*]},<.>/?_";
	static private final String NORMAL_BASEKEY_MARK = "記号#$()=~\\`[{;+:*]}./_";
	//static private final String NORMAL_BASEKEY_ESC  = "エスケープ\"\b\f\'\0\'\\\"";
	static private final String NORMAL_BASEKEY_ESC  = "エスケープ";
	static private final String NORMAL_BASEKEY_UNI  = "ユニコード：\u3053\u3053\u307e\u3067"; // ユニコード：ここまで
	//static private final String REGEXP_BASEKEY_1 = "\\A\\s*\\Q基底キー\\E[0-9]?\\s*\\z";
	//static private final String REGEXP_BASEKEY_2 = "^基底キー(\\d{1})$";
	//static private final String REGEXP_BASEKEY_3 = "^([0-9]{4})/([0-9]{2})/([0-9]{2})";
	static private final String REGEXP_BASEKEY_1 = "\\A\\s*\\Q基底キー\\E\\d\\s*\\z";
	static private final String REGEXP_BASEKEY_2 = "基底キー(\\d{1})$";
	static private final String REGEXP_BASEKEY_3 = "(\\d{4})/(\\d{2})/(\\d{2})";
	
	static private final String DT_BOOLEAN_U = "BOOLEAN";
	static private final String DT_BOOLEAN_M = "bOoLeAn";
	static private final String DT_BOOLEAN_W = "bo*an";
	static private final String DT_INTEGER_U = "INTEGER";
	static private final String DT_INTEGER_M = "InTeGeR";
	static private final String DT_INTEGER_W = "in*er";
	static private final String DT_DECIMAL_U = "DECIMAL";
	static private final String DT_DECIMAL_M = "dEcImAl";
	static private final String DT_DECIMAL_W = "de*al";
	static private final String DT_STRING_U = "STRING";
	static private final String DT_STRING_M = "StRiNg";
	static private final String DT_STRING_W = "st*ng";
	
	static private final DtBasePattern[] testBases1 = {
		new DtBasePattern(DtBase.OMITTED, DT_BOOLEAN_M),
		new DtBasePattern(DtBase.OMITTED, DT_INTEGER_M),
		new DtBasePattern(DtBase.OMITTED, DT_DECIMAL_M),
		new DtBasePattern(DtBase.OMITTED, DT_STRING_M),
		new DtBasePattern(DtBase.OMITTED, DT_BOOLEAN_M, DtBase.OMITTED),
		new DtBasePattern(DtBase.OMITTED, DT_INTEGER_M, DtBase.OMITTED),
		new DtBasePattern(DtBase.OMITTED, DT_DECIMAL_M, DtBase.OMITTED),
		new DtBasePattern(DtBase.OMITTED, DT_STRING_M, DtBase.OMITTED),
		new DtBasePattern(DtBase.OMITTED, DT_BOOLEAN_M, DtBase.OMITTED, DtBase.OMITTED),
		new DtBasePattern(DtBase.OMITTED, DT_INTEGER_M, DtBase.OMITTED, DtBase.OMITTED),
		new DtBasePattern(DtBase.OMITTED, DT_DECIMAL_M, DtBase.OMITTED, DtBase.OMITTED),
		new DtBasePattern(DtBase.OMITTED, DT_STRING_M, DtBase.OMITTED, DtBase.OMITTED),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_BOOLEAN_M),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_INTEGER_M),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_DECIMAL_M),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_STRING_M),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_BOOLEAN_M, NORMAL_BASEKEY_2),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_INTEGER_M, NORMAL_BASEKEY_2),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_DECIMAL_M, NORMAL_BASEKEY_2),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_STRING_M, NORMAL_BASEKEY_2),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_BOOLEAN_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_INTEGER_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_DECIMAL_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_STRING_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		new DtBasePattern(NORMAL_BASEKEY_5, DT_BOOLEAN_M, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2),
		new DtBasePattern(NORMAL_BASEKEY_5, DT_INTEGER_M, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2),
		new DtBasePattern(NORMAL_BASEKEY_5, DT_DECIMAL_M, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2),
		new DtBasePattern(NORMAL_BASEKEY_5, DT_STRING_M, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2),
	};
	
	static private final DtBasePattern[] testBases2 = {
		new DtBasePattern(NORMAL_BASEKEY_MARK, DT_BOOLEAN_U, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBasePattern(NORMAL_BASEKEY_MARK, DT_INTEGER_U, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBasePattern(NORMAL_BASEKEY_MARK, DT_DECIMAL_U, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBasePattern(NORMAL_BASEKEY_MARK, DT_STRING_U, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBasePattern(REGEXP_BASEKEY_1, DT_BOOLEAN_U, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		new DtBasePattern(REGEXP_BASEKEY_1, DT_INTEGER_U, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		new DtBasePattern(REGEXP_BASEKEY_1, DT_DECIMAL_U, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		new DtBasePattern(REGEXP_BASEKEY_1, DT_STRING_U, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
	};
	
	static private final DtBasePattern[] testBases3 = {
		new DtBasePattern(NORMAL_BASEKEY_A, DT_BOOLEAN_W),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_INTEGER_W),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_DECIMAL_W),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_STRING_W),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_BOOLEAN_W, NORMAL_BASEKEY_B),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_INTEGER_W, NORMAL_BASEKEY_B),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_DECIMAL_W, NORMAL_BASEKEY_B),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_STRING_W, NORMAL_BASEKEY_B),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_BOOLEAN_W, NORMAL_BASEKEY_B, NORMAL_BASEKEY_C),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_INTEGER_W, NORMAL_BASEKEY_B, NORMAL_BASEKEY_C),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_DECIMAL_W, NORMAL_BASEKEY_B, NORMAL_BASEKEY_C),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_STRING_W, NORMAL_BASEKEY_B, NORMAL_BASEKEY_C),
		new DtBasePattern(NORMAL_BASEKEY_E, DT_BOOLEAN_W, NORMAL_BASEKEY_D, NORMAL_BASEKEY_B),
		new DtBasePattern(NORMAL_BASEKEY_E, DT_INTEGER_W, NORMAL_BASEKEY_D, NORMAL_BASEKEY_B),
		new DtBasePattern(NORMAL_BASEKEY_E, DT_DECIMAL_W, NORMAL_BASEKEY_D, NORMAL_BASEKEY_B),
		new DtBasePattern(NORMAL_BASEKEY_E, DT_STRING_W, NORMAL_BASEKEY_D, NORMAL_BASEKEY_B),
		new DtBasePattern(NORMAL_BASEKEY_MARK, DT_BOOLEAN_W, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBasePattern(NORMAL_BASEKEY_MARK, DT_INTEGER_W, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBasePattern(NORMAL_BASEKEY_MARK, DT_DECIMAL_W, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBasePattern(NORMAL_BASEKEY_MARK, DT_STRING_W, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBasePattern(REGEXP_BASEKEY_1, DT_BOOLEAN_W, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		new DtBasePattern(REGEXP_BASEKEY_1, DT_INTEGER_W, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		new DtBasePattern(REGEXP_BASEKEY_1, DT_DECIMAL_W, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		new DtBasePattern(REGEXP_BASEKEY_1, DT_STRING_W, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
	};
	
	static private final DtBasePattern[] normalAllBases = {
		new DtBasePattern(DtBase.OMITTED, DT_BOOLEAN_M),
		new DtBasePattern(DtBase.OMITTED, DT_INTEGER_M),
		new DtBasePattern(DtBase.OMITTED, DT_DECIMAL_M),
		new DtBasePattern(DtBase.OMITTED, DT_STRING_M),
		new DtBasePattern(DtBase.OMITTED, DT_BOOLEAN_M, DtBase.OMITTED),
		new DtBasePattern(DtBase.OMITTED, DT_INTEGER_M, DtBase.OMITTED),
		new DtBasePattern(DtBase.OMITTED, DT_DECIMAL_M, DtBase.OMITTED),
		new DtBasePattern(DtBase.OMITTED, DT_STRING_M, DtBase.OMITTED),
		new DtBasePattern(DtBase.OMITTED, DT_BOOLEAN_M, DtBase.OMITTED, DtBase.OMITTED),
		new DtBasePattern(DtBase.OMITTED, DT_INTEGER_M, DtBase.OMITTED, DtBase.OMITTED),
		new DtBasePattern(DtBase.OMITTED, DT_DECIMAL_M, DtBase.OMITTED, DtBase.OMITTED),
		new DtBasePattern(DtBase.OMITTED, DT_STRING_M, DtBase.OMITTED, DtBase.OMITTED),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_BOOLEAN_M),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_INTEGER_M),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_DECIMAL_M),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_STRING_M),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_BOOLEAN_M, NORMAL_BASEKEY_2),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_INTEGER_M, NORMAL_BASEKEY_2),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_DECIMAL_M, NORMAL_BASEKEY_2),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_STRING_M, NORMAL_BASEKEY_2),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_BOOLEAN_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_INTEGER_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_DECIMAL_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_STRING_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		new DtBasePattern(NORMAL_BASEKEY_5, DT_BOOLEAN_M, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2),
		new DtBasePattern(NORMAL_BASEKEY_5, DT_INTEGER_M, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2),
		new DtBasePattern(NORMAL_BASEKEY_5, DT_DECIMAL_M, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2),
		new DtBasePattern(NORMAL_BASEKEY_5, DT_STRING_M, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2),
		new DtBasePattern(NORMAL_BASEKEY_MARK, DT_BOOLEAN_U, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBasePattern(NORMAL_BASEKEY_MARK, DT_INTEGER_U, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBasePattern(NORMAL_BASEKEY_MARK, DT_DECIMAL_U, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBasePattern(NORMAL_BASEKEY_MARK, DT_STRING_U, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBasePattern(REGEXP_BASEKEY_1, DT_BOOLEAN_U, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		new DtBasePattern(REGEXP_BASEKEY_1, DT_INTEGER_U, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		new DtBasePattern(REGEXP_BASEKEY_1, DT_DECIMAL_U, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		new DtBasePattern(REGEXP_BASEKEY_1, DT_STRING_U, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_BOOLEAN_W),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_INTEGER_W),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_DECIMAL_W),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_STRING_W),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_BOOLEAN_W, NORMAL_BASEKEY_B),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_INTEGER_W, NORMAL_BASEKEY_B),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_DECIMAL_W, NORMAL_BASEKEY_B),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_STRING_W, NORMAL_BASEKEY_B),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_BOOLEAN_W, NORMAL_BASEKEY_B, NORMAL_BASEKEY_C),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_INTEGER_W, NORMAL_BASEKEY_B, NORMAL_BASEKEY_C),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_DECIMAL_W, NORMAL_BASEKEY_B, NORMAL_BASEKEY_C),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_STRING_W, NORMAL_BASEKEY_B, NORMAL_BASEKEY_C),
		new DtBasePattern(NORMAL_BASEKEY_E, DT_BOOLEAN_W, NORMAL_BASEKEY_D, NORMAL_BASEKEY_B),
		new DtBasePattern(NORMAL_BASEKEY_E, DT_INTEGER_W, NORMAL_BASEKEY_D, NORMAL_BASEKEY_B),
		new DtBasePattern(NORMAL_BASEKEY_E, DT_DECIMAL_W, NORMAL_BASEKEY_D, NORMAL_BASEKEY_B),
		new DtBasePattern(NORMAL_BASEKEY_E, DT_STRING_W, NORMAL_BASEKEY_D, NORMAL_BASEKEY_B),
		new DtBasePattern(NORMAL_BASEKEY_MARK, DT_BOOLEAN_W, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBasePattern(NORMAL_BASEKEY_MARK, DT_INTEGER_W, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBasePattern(NORMAL_BASEKEY_MARK, DT_DECIMAL_W, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBasePattern(NORMAL_BASEKEY_MARK, DT_STRING_W, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBasePattern(REGEXP_BASEKEY_1, DT_BOOLEAN_W, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		new DtBasePattern(REGEXP_BASEKEY_1, DT_INTEGER_W, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		new DtBasePattern(REGEXP_BASEKEY_1, DT_DECIMAL_W, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		new DtBasePattern(REGEXP_BASEKEY_1, DT_STRING_W, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
	};
	
	static private final DtBasePattern[] withNullAllBases = {
		null,
		new DtBasePattern(DtBase.OMITTED, DT_BOOLEAN_M),
		new DtBasePattern(DtBase.OMITTED, DT_INTEGER_M),
		new DtBasePattern(DtBase.OMITTED, DT_DECIMAL_M),
		new DtBasePattern(DtBase.OMITTED, DT_STRING_M),
		new DtBasePattern(DtBase.OMITTED, DT_BOOLEAN_M, DtBase.OMITTED),
		new DtBasePattern(DtBase.OMITTED, DT_INTEGER_M, DtBase.OMITTED),
		new DtBasePattern(DtBase.OMITTED, DT_DECIMAL_M, DtBase.OMITTED),
		new DtBasePattern(DtBase.OMITTED, DT_STRING_M, DtBase.OMITTED),
		new DtBasePattern(DtBase.OMITTED, DT_BOOLEAN_M, DtBase.OMITTED, DtBase.OMITTED),
		new DtBasePattern(DtBase.OMITTED, DT_INTEGER_M, DtBase.OMITTED, DtBase.OMITTED),
		new DtBasePattern(DtBase.OMITTED, DT_DECIMAL_M, DtBase.OMITTED, DtBase.OMITTED),
		new DtBasePattern(DtBase.OMITTED, DT_STRING_M, DtBase.OMITTED, DtBase.OMITTED),
		null,
		null,
		new DtBasePattern(NORMAL_BASEKEY_1, DT_BOOLEAN_M),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_INTEGER_M),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_DECIMAL_M),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_STRING_M),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_BOOLEAN_M, NORMAL_BASEKEY_2),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_INTEGER_M, NORMAL_BASEKEY_2),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_DECIMAL_M, NORMAL_BASEKEY_2),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_STRING_M, NORMAL_BASEKEY_2),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_BOOLEAN_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_INTEGER_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_DECIMAL_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		new DtBasePattern(NORMAL_BASEKEY_1, DT_STRING_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		new DtBasePattern(NORMAL_BASEKEY_5, DT_BOOLEAN_M, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2),
		new DtBasePattern(NORMAL_BASEKEY_5, DT_INTEGER_M, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2),
		new DtBasePattern(NORMAL_BASEKEY_5, DT_DECIMAL_M, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2),
		new DtBasePattern(NORMAL_BASEKEY_5, DT_STRING_M, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2),
		null,
		new DtBasePattern(NORMAL_BASEKEY_MARK, DT_BOOLEAN_U, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBasePattern(NORMAL_BASEKEY_MARK, DT_INTEGER_U, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBasePattern(NORMAL_BASEKEY_MARK, DT_DECIMAL_U, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBasePattern(NORMAL_BASEKEY_MARK, DT_STRING_U, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBasePattern(REGEXP_BASEKEY_1, DT_BOOLEAN_U, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		new DtBasePattern(REGEXP_BASEKEY_1, DT_INTEGER_U, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		new DtBasePattern(REGEXP_BASEKEY_1, DT_DECIMAL_U, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		new DtBasePattern(REGEXP_BASEKEY_1, DT_STRING_U, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		null,
		new DtBasePattern(NORMAL_BASEKEY_A, DT_BOOLEAN_W),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_INTEGER_W),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_DECIMAL_W),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_STRING_W),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_BOOLEAN_W, NORMAL_BASEKEY_B),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_INTEGER_W, NORMAL_BASEKEY_B),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_DECIMAL_W, NORMAL_BASEKEY_B),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_STRING_W, NORMAL_BASEKEY_B),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_BOOLEAN_W, NORMAL_BASEKEY_B, NORMAL_BASEKEY_C),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_INTEGER_W, NORMAL_BASEKEY_B, NORMAL_BASEKEY_C),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_DECIMAL_W, NORMAL_BASEKEY_B, NORMAL_BASEKEY_C),
		new DtBasePattern(NORMAL_BASEKEY_A, DT_STRING_W, NORMAL_BASEKEY_B, NORMAL_BASEKEY_C),
		new DtBasePattern(NORMAL_BASEKEY_E, DT_BOOLEAN_W, NORMAL_BASEKEY_D, NORMAL_BASEKEY_B),
		new DtBasePattern(NORMAL_BASEKEY_E, DT_INTEGER_W, NORMAL_BASEKEY_D, NORMAL_BASEKEY_B),
		new DtBasePattern(NORMAL_BASEKEY_E, DT_DECIMAL_W, NORMAL_BASEKEY_D, NORMAL_BASEKEY_B),
		new DtBasePattern(NORMAL_BASEKEY_E, DT_STRING_W, NORMAL_BASEKEY_D, NORMAL_BASEKEY_B),
		null,
		new DtBasePattern(NORMAL_BASEKEY_MARK, DT_BOOLEAN_W, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBasePattern(NORMAL_BASEKEY_MARK, DT_INTEGER_W, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBasePattern(NORMAL_BASEKEY_MARK, DT_DECIMAL_W, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBasePattern(NORMAL_BASEKEY_MARK, DT_STRING_W, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBasePattern(REGEXP_BASEKEY_1, DT_BOOLEAN_W, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		new DtBasePattern(REGEXP_BASEKEY_1, DT_INTEGER_W, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		new DtBasePattern(REGEXP_BASEKEY_1, DT_DECIMAL_W, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		new DtBasePattern(REGEXP_BASEKEY_1, DT_STRING_W, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
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
	
	static protected DtBasePattern[] catBaseElements(DtBasePattern[] baseElements, DtBasePattern...elements) {
		int newSize = 0;
		if (baseElements != null && baseElements.length > 0) {
			newSize = baseElements.length;
		}
		if (elements != null && elements.length > 0) {
			newSize += elements.length;
		}
		
		DtBasePattern[] newElements = new DtBasePattern[newSize];
		int index = 0;
		if (baseElements != null && baseElements.length > 0) {
			for (; index < baseElements.length; index++) {
				newElements[index] = baseElements[index];
			}
		}
		if (elements != null && elements.length > 0) {
			for (int i = 0; i < elements.length; i++) {
				newElements[index+i] = elements[i];
			}
		}
		
		return newElements;
		
	}
	
	static protected String[] getBaseKeyArray(int index, boolean withoutOmitted, DtBasePattern...patterns) {
		if (patterns == null || patterns.length <= 0) {
			return new String[0];
		}
		
		HashSet<String> s = new HashSet<String>();
		for (DtBasePattern pattern : patterns) {
			s.add(pattern._baseKeys[index]);
		}
		if (withoutOmitted) {
			s.remove(DtBase.OMITTED);
		}
		
		String[] ret = s.toArray(new String[s.size()]);
		Arrays.sort(ret);
		
		return ret;
	}
	
	static protected boolean equalElements(DtBasePatternSet pset, DtBasePattern...elements) {
		if (pset == null || elements == null)
			return false;
		
		if (pset.size() != elements.length)
			return false;
		
		return (pset.containsAll(Arrays.asList(elements)));
	}
	
	static protected boolean equalElementSequence(DtBasePatternSet pset, DtBasePattern...elements) {
		if (pset == null || elements == null)
			return false;
		
		if (pset.size() != elements.length)
			return false;

		int index = 0;
		for (DtBasePattern pattern : pset) {
			if (!pattern.equals(elements[index])) {
				System.err.println("equalElementSequence : not equal DtBasePattern : "
						+ pattern.toString() + " : elements[" + String.valueOf(index)
						+ "]=" + String.valueOf(elements[index]));
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
	 * {@link dtalge.DtBasePatternSet#DtBasePatternSet(java.util.Collection)} のためのテスト・メソッド。
	 */
	public void testDtBasePatternSetCollectionOfQextendsDtBasePattern() {
		DtBasePatternSet bset;
		
		//--- null
		try {
			bset = new DtBasePatternSet(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		//--- new
		bset = new DtBasePatternSet(Arrays.asList(withNullAllBases));
		assertFalse(withNullAllBases.length == bset.size());
		assertTrue(equalElementSequence(bset, normalAllBases));
		
		//--- check data
		bset = new DtBasePatternSet(Arrays.asList(DtBaseSetTest.ignoreTypePatterns1));
		assertTrue(equalElementSequence(bset, DtBaseSetTest.ignoreTypePatterns1));
		bset = new DtBasePatternSet(Arrays.asList(DtBaseSetTest.ignoreTypePatterns2));
		assertTrue(equalElementSequence(bset, DtBaseSetTest.ignoreTypePatterns2));
		bset = new DtBasePatternSet(Arrays.asList(DtBaseSetTest.ignoreTypePatterns3));
		assertTrue(equalElementSequence(bset, DtBaseSetTest.ignoreTypePatterns3));
		bset = new DtBasePatternSet(Arrays.asList(DtBaseSetTest.basePatterns1));
		assertTrue(equalElementSequence(bset, DtBaseSetTest.basePatterns1));
		bset = new DtBasePatternSet(Arrays.asList(DtBaseSetTest.basePatterns2));
		assertTrue(equalElementSequence(bset, DtBaseSetTest.basePatterns2));
		bset = new DtBasePatternSet(Arrays.asList(DtBaseSetTest.basePatterns3));
		assertTrue(equalElementSequence(bset, DtBaseSetTest.basePatterns3));
	}

	/**
	 * {@link dtalge.DtBasePatternSet#DtBasePatternSet(dtalge.DtBaseSet, boolean)} のためのテスト・メソッド。
	 */
	public void testDtBasePatternSetDtBaseSetBoolean() {
		DtBasePatternSet pset;
		
		//--- null
		try {
			pset = new DtBasePatternSet(null, false);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			pset = new DtBasePatternSet(null, true);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		DtBaseSet bases0 = new DtBaseSet();
		assertTrue(bases0.isEmpty());
		DtBaseSet bases1 = new DtBaseSet(Arrays.asList(DtBaseSetTest.testBases1));
		assertTrue(DtBaseSetTest.equalElementSequence(bases1, DtBaseSetTest.testBases1));
		DtBaseSet bases2 = new DtBaseSet(Arrays.asList(DtBaseSetTest.testBases2));
		assertTrue(DtBaseSetTest.equalElementSequence(bases2, DtBaseSetTest.testBases2));
		DtBaseSet bases3 = new DtBaseSet(Arrays.asList(DtBaseSetTest.testBases3));
		assertTrue(DtBaseSetTest.equalElementSequence(bases3, DtBaseSetTest.testBases3));
		
		//--- empty
		pset = new DtBasePatternSet(bases0, false);
		assertTrue(pset.isEmpty());
		pset = new DtBasePatternSet(bases0, true);
		assertTrue(pset.isEmpty());
		
		//--- use type key
		pset = new DtBasePatternSet(bases1, false);
		assertTrue(equalElements(pset, DtBaseSetTest.basePatterns1));
		assertTrue(equalElementSequence(pset, DtBaseSetTest.basePatterns1));
		pset = new DtBasePatternSet(bases2, false);
		assertTrue(equalElements(pset, DtBaseSetTest.basePatterns2));
		assertTrue(equalElementSequence(pset, DtBaseSetTest.basePatterns2));
		pset = new DtBasePatternSet(bases3, false);
		assertTrue(equalElements(pset, DtBaseSetTest.basePatterns3));
		assertTrue(equalElementSequence(pset, DtBaseSetTest.basePatterns3));
		
		//--- ignore type key
		pset = new DtBasePatternSet(bases1, true);
		assertTrue(equalElements(pset, DtBaseSetTest.ignoreTypePatterns1));
		assertTrue(equalElementSequence(pset, DtBaseSetTest.ignoreTypePatterns1));
		pset = new DtBasePatternSet(bases2, true);
		assertTrue(equalElements(pset, DtBaseSetTest.ignoreTypePatterns2));
		assertTrue(equalElementSequence(pset, DtBaseSetTest.ignoreTypePatterns2));
		pset = new DtBasePatternSet(bases3, true);
		assertTrue(equalElements(pset, DtBaseSetTest.ignoreTypePatterns3));
		assertTrue(equalElementSequence(pset, DtBaseSetTest.ignoreTypePatterns3));
	}

	/**
	 * {@link dtalge.DtBasePatternSet#addition(dtalge.DtBasePatternSet)} のためのテスト・メソッド。
	 */
	public void testAddition() {
		DtBasePatternSet bset;
		DtBasePatternSet bempty = new DtBasePatternSet();
		DtBasePatternSet bases1 = new DtBasePatternSet(Arrays.asList(testBases1));
		DtBasePatternSet bases2 = new DtBasePatternSet(Arrays.asList(testBases2));
		DtBasePatternSet bases3 = new DtBasePatternSet(Arrays.asList(testBases3));
		DtBasePatternSet allBases = new DtBasePatternSet(Arrays.asList(normalAllBases));
		
		//--- null
		try {
			bset = bempty.addition(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			bset = bases1.addition(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		//--- empty
		bset = bempty.addition(bempty);
		assertTrue(bset.isEmpty());
		bset = bases2.addition(bempty);
		assertTrue(equalElements(bset, testBases2));
		assertTrue(equalElementSequence(bset, testBases2));
		bset = bempty.addition(bases3);
		assertTrue(equalElements(bset, testBases3));
		assertTrue(equalElementSequence(bset, testBases3));
		
		//--- same elements
		bset = bases1.addition(bases1);
		assertTrue(equalElements(bset, testBases1));
		assertTrue(equalElementSequence(bset, testBases1));
		bset = bases2.addition(bases2);
		assertTrue(equalElements(bset, testBases2));
		assertTrue(equalElementSequence(bset, testBases2));
		bset = bases3.addition(bases3);
		assertTrue(equalElements(bset, testBases3));
		assertTrue(equalElementSequence(bset, testBases3));
		
		//--- different elements
		bset = bases1.addition(bases2);
		bset = bset.addition(bases3);
		assertTrue(equalElements(bset, normalAllBases));
		assertTrue(equalElementSequence(bset, normalAllBases));
		bset = bases2.addition(allBases);
		assertTrue(equalElements(bset, normalAllBases));
		
		//--- immidiate check
		assertTrue(bempty.isEmpty());
		assertTrue(equalElementSequence(bases1, testBases1));
		assertTrue(equalElementSequence(bases2, testBases2));
		assertTrue(equalElementSequence(bases3, testBases3));
		assertTrue(equalElementSequence(allBases, normalAllBases));
	}

	/**
	 * {@link dtalge.DtBasePatternSet#subtraction(dtalge.DtBasePatternSet)} のためのテスト・メソッド。
	 */
	public void testSubtraction() {
		DtBasePattern[] testBases13 = catBaseElements(testBases1, testBases3);
		DtBasePatternSet bset;
		DtBasePatternSet bempty = new DtBasePatternSet();
		DtBasePatternSet bases1 = new DtBasePatternSet(Arrays.asList(testBases1));
		DtBasePatternSet bases2 = new DtBasePatternSet(Arrays.asList(testBases2));
		DtBasePatternSet bases3 = new DtBasePatternSet(Arrays.asList(testBases3));
		DtBasePatternSet allBases = new DtBasePatternSet(Arrays.asList(normalAllBases));
		
		//--- null
		try {
			bset = bempty.subtraction(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			bset = bases1.subtraction(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		//--- empty
		bset = bempty.subtraction(bempty);
		assertTrue(bset.isEmpty());
		bset = bempty.subtraction(bases2);
		assertTrue(bset.isEmpty());
		bset = bases3.subtraction(bempty);
		assertTrue(equalElements(bset, testBases3));
		assertTrue(equalElementSequence(bset, testBases3));
		
		//--- same elements
		bset = bases1.subtraction(bases1);
		assertTrue(bset.isEmpty());
		bset = bases2.subtraction(bases2);
		assertTrue(bset.isEmpty());
		bset = bases3.subtraction(bases3);
		assertTrue(bset.isEmpty());
		
		//--- different elements
		bset = bases2.subtraction(allBases);
		assertTrue(bset.isEmpty());
		bset = allBases.subtraction(bases2);
		assertTrue(equalElements(bset, testBases13));
		
		//--- immidiate check
		assertTrue(bempty.isEmpty());
		assertTrue(equalElementSequence(bases1, testBases1));
		assertTrue(equalElementSequence(bases2, testBases2));
		assertTrue(equalElementSequence(bases3, testBases3));
		assertTrue(equalElementSequence(allBases, normalAllBases));
	}

	/**
	 * {@link dtalge.DtBasePatternSet#retention(dtalge.DtBasePatternSet)} のためのテスト・メソッド。
	 */
	public void testRetention() {
		DtBasePattern[] testBases13 = catBaseElements(testBases1, testBases3);
		DtBasePatternSet bset;
		DtBasePatternSet bempty = new DtBasePatternSet();
		DtBasePatternSet bases1 = new DtBasePatternSet(Arrays.asList(testBases1));
		DtBasePatternSet bases2 = new DtBasePatternSet(Arrays.asList(testBases2));
		DtBasePatternSet bases3 = new DtBasePatternSet(Arrays.asList(testBases3));
		DtBasePatternSet bases13 = new DtBasePatternSet(Arrays.asList(testBases13));
		DtBasePatternSet allBases = new DtBasePatternSet(Arrays.asList(normalAllBases));
		
		//--- null
		try {
			bset = bempty.retention(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			bset = bases1.retention(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		//--- empty
		bset = bempty.retention(bempty);
		assertTrue(bset.isEmpty());
		bset = bempty.retention(bases2);
		assertTrue(bset.isEmpty());
		bset = bases3.retention(bempty);
		assertTrue(bset.isEmpty());
		
		//--- same elements
		bset = bases1.retention(bases1);
		assertTrue(equalElements(bset, testBases1));
		assertTrue(equalElementSequence(bset, testBases1));
		bset = bases2.retention(bases2);
		assertTrue(equalElements(bset, testBases2));
		assertTrue(equalElementSequence(bset, testBases2));
		bset = bases3.retention(bases3);
		assertTrue(equalElements(bset, testBases3));
		assertTrue(equalElementSequence(bset, testBases3));
		
		//--- different elements
		bset = allBases.retention(bases2);
		assertTrue(equalElements(bset, testBases2));
		assertTrue(equalElementSequence(bset, testBases2));
		bset = bases2.retention(allBases);
		assertTrue(equalElements(bset, testBases2));
		assertTrue(equalElementSequence(bset, testBases2));
		bset = allBases.retention(bases13);
		assertTrue(equalElements(bset, testBases13));
		assertTrue(equalElementSequence(bset, testBases13));
		bset = bases13.retention(allBases);
		assertTrue(equalElements(bset, testBases13));
		assertTrue(equalElementSequence(bset, testBases13));
		
		//--- immidiate check
		assertTrue(bempty.isEmpty());
		assertTrue(equalElementSequence(bases1, testBases1));
		assertTrue(equalElementSequence(bases2, testBases2));
		assertTrue(equalElementSequence(bases3, testBases3));
		assertTrue(equalElementSequence(bases13, testBases13));
		assertTrue(equalElementSequence(allBases, normalAllBases));
	}

	/**
	 * {@link dtalge.DtBasePatternSet#union(dtalge.DtBasePatternSet)} のためのテスト・メソッド。
	 */
	public void testUnion() {
		DtBasePatternSet bset;
		DtBasePatternSet bempty = new DtBasePatternSet();
		DtBasePatternSet bases1 = new DtBasePatternSet(Arrays.asList(testBases1));
		DtBasePatternSet bases2 = new DtBasePatternSet(Arrays.asList(testBases2));
		DtBasePatternSet bases3 = new DtBasePatternSet(Arrays.asList(testBases3));
		DtBasePatternSet allBases = new DtBasePatternSet(Arrays.asList(normalAllBases));
		
		//--- null
		try {
			bset = bempty.union(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			bset = bases1.union(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		//--- empty
		bset = bempty.union(bempty);
		assertTrue(bset.isEmpty());
		bset = bases2.union(bempty);
		assertTrue(equalElements(bset, testBases2));
		assertTrue(equalElementSequence(bset, testBases2));
		bset = bempty.union(bases3);
		assertTrue(equalElements(bset, testBases3));
		assertTrue(equalElementSequence(bset, testBases3));
		
		//--- same elements
		bset = bases1.union(bases1);
		assertTrue(equalElements(bset, testBases1));
		assertTrue(equalElementSequence(bset, testBases1));
		bset = bases2.union(bases2);
		assertTrue(equalElements(bset, testBases2));
		assertTrue(equalElementSequence(bset, testBases2));
		bset = bases3.union(bases3);
		assertTrue(equalElements(bset, testBases3));
		assertTrue(equalElementSequence(bset, testBases3));
		
		//--- different elements
		bset = bases1.union(bases2);
		bset = bset.union(bases3);
		assertTrue(equalElements(bset, normalAllBases));
		assertTrue(equalElementSequence(bset, normalAllBases));
		bset = bases2.union(allBases);
		assertTrue(equalElements(bset, normalAllBases));
		
		//--- immidiate check
		assertTrue(bempty.isEmpty());
		assertTrue(equalElementSequence(bases1, testBases1));
		assertTrue(equalElementSequence(bases2, testBases2));
		assertTrue(equalElementSequence(bases3, testBases3));
		assertTrue(equalElementSequence(allBases, normalAllBases));
	}

	/**
	 * {@link dtalge.DtBasePatternSet#intersection(dtalge.DtBasePatternSet)} のためのテスト・メソッド。
	 */
	public void testIntersection() {
		DtBasePattern[] testBases13 = catBaseElements(testBases1, testBases3);
		DtBasePatternSet bset;
		DtBasePatternSet bempty = new DtBasePatternSet();
		DtBasePatternSet bases1 = new DtBasePatternSet(Arrays.asList(testBases1));
		DtBasePatternSet bases2 = new DtBasePatternSet(Arrays.asList(testBases2));
		DtBasePatternSet bases3 = new DtBasePatternSet(Arrays.asList(testBases3));
		DtBasePatternSet bases13 = new DtBasePatternSet(Arrays.asList(testBases13));
		DtBasePatternSet allBases = new DtBasePatternSet(Arrays.asList(normalAllBases));
		
		//--- null
		try {
			bset = bempty.intersection(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			bset = bases1.intersection(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		//--- empty
		bset = bempty.intersection(bempty);
		assertTrue(bset.isEmpty());
		bset = bempty.intersection(bases2);
		assertTrue(bset.isEmpty());
		bset = bases3.intersection(bempty);
		assertTrue(bset.isEmpty());
		
		//--- same elements
		bset = bases1.intersection(bases1);
		assertTrue(equalElements(bset, testBases1));
		assertTrue(equalElementSequence(bset, testBases1));
		bset = bases2.intersection(bases2);
		assertTrue(equalElements(bset, testBases2));
		assertTrue(equalElementSequence(bset, testBases2));
		bset = bases3.intersection(bases3);
		assertTrue(equalElements(bset, testBases3));
		assertTrue(equalElementSequence(bset, testBases3));
		
		//--- different elements
		bset = allBases.intersection(bases2);
		assertTrue(equalElements(bset, testBases2));
		assertTrue(equalElementSequence(bset, testBases2));
		bset = bases2.intersection(allBases);
		assertTrue(equalElements(bset, testBases2));
		assertTrue(equalElementSequence(bset, testBases2));
		bset = allBases.intersection(bases13);
		assertTrue(equalElements(bset, testBases13));
		assertTrue(equalElementSequence(bset, testBases13));
		bset = bases13.intersection(allBases);
		assertTrue(equalElements(bset, testBases13));
		assertTrue(equalElementSequence(bset, testBases13));
		
		//--- immidiate check
		assertTrue(bempty.isEmpty());
		assertTrue(equalElementSequence(bases1, testBases1));
		assertTrue(equalElementSequence(bases2, testBases2));
		assertTrue(equalElementSequence(bases3, testBases3));
		assertTrue(equalElementSequence(bases13, testBases13));
		assertTrue(equalElementSequence(allBases, normalAllBases));
	}

	/**
	 * {@link dtalge.DtBasePatternSet#difference(dtalge.DtBasePatternSet)} のためのテスト・メソッド。
	 */
	public void testDifference() {
		DtBasePattern[] testBases13 = catBaseElements(testBases1, testBases3);
		DtBasePatternSet bset;
		DtBasePatternSet bempty = new DtBasePatternSet();
		DtBasePatternSet bases1 = new DtBasePatternSet(Arrays.asList(testBases1));
		DtBasePatternSet bases2 = new DtBasePatternSet(Arrays.asList(testBases2));
		DtBasePatternSet bases3 = new DtBasePatternSet(Arrays.asList(testBases3));
		DtBasePatternSet allBases = new DtBasePatternSet(Arrays.asList(normalAllBases));
		
		//--- null
		try {
			bset = bempty.difference(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			bset = bases1.difference(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		//--- empty
		bset = bempty.difference(bempty);
		assertTrue(bset.isEmpty());
		bset = bempty.difference(bases2);
		assertTrue(bset.isEmpty());
		bset = bases3.difference(bempty);
		assertTrue(equalElements(bset, testBases3));
		assertTrue(equalElementSequence(bset, testBases3));
		
		//--- same elements
		bset = bases1.difference(bases1);
		assertTrue(bset.isEmpty());
		bset = bases2.difference(bases2);
		assertTrue(bset.isEmpty());
		bset = bases3.difference(bases3);
		assertTrue(bset.isEmpty());
		
		//--- different elements
		bset = bases2.difference(allBases);
		assertTrue(bset.isEmpty());
		bset = allBases.difference(bases2);
		assertTrue(equalElements(bset, testBases13));
		
		//--- immidiate check
		assertTrue(bempty.isEmpty());
		assertTrue(equalElementSequence(bases1, testBases1));
		assertTrue(equalElementSequence(bases2, testBases2));
		assertTrue(equalElementSequence(bases3, testBases3));
		assertTrue(equalElementSequence(allBases, normalAllBases));
	}

	/**
	 * {@link dtalge.DtBasePatternSet#matches(dtalge.DtBase)} のためのテスト・メソッド。
	 */
	public void testMatches() {
		DtBase base0 = null;
		DtBase base1 = new DtBase(NORMAL_BASEKEY_1, DtDataTypes.DECIMAL, NORMAL_BASEKEY_2);
		DtBase base2 = new DtBase(NORMAL_BASEKEY_MARK, DT_STRING_U, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI);
		DtBase base3 = new DtBase(REGEXP_BASEKEY_1, DT_BOOLEAN_U, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3);
		DtBase base4 = new DtBase(REGEXP_BASEKEY_1, DT_BOOLEAN_U, REGEXP_BASEKEY_2, NORMAL_BASEKEY_MARK);
		
		DtBasePatternSet pset0 = new DtBasePatternSet();
		assertTrue(pset0.isEmpty());
		assertFalse(pset0.matches(base0));
		assertFalse(pset0.matches(base1));
		assertFalse(pset0.matches(base2));
		assertFalse(pset0.matches(base3));
		assertFalse(pset0.matches(base4));
		
		DtBasePatternSet pset1 = new DtBasePatternSet(Arrays.asList(normalAllBases));
		assertTrue(equalElementSequence(pset1, normalAllBases));
		assertFalse(pset1.matches(base0));
		assertTrue (pset1.matches(base1));
		assertTrue (pset1.matches(base2));
		assertTrue (pset1.matches(base3));
		assertFalse(pset1.matches(base4));
	}

	/**
	 * {@link dtalge.DtBasePatternSet#add(dtalge.DtBasePattern)} のためのテスト・メソッド。
	 */
	public void testAddDtBasePattern() {
		DtBasePatternSet bset;
		
		//--- null
		bset = new DtBasePatternSet();
		try {
			bset.add(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		bset = new DtBasePatternSet(Arrays.asList(normalAllBases));
		try {
			bset.add(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		bset = new DtBasePatternSet();
		for (DtBasePattern base : testBases3) {
			assertTrue(bset.add(base));
		}
		assertTrue(equalElements(bset, testBases3));
		assertTrue(equalElementSequence(bset, testBases3));
		
		for (DtBasePattern base : testBases3) {
			assertFalse(bset.add(base));
		}
		assertTrue(equalElements(bset, testBases3));
		assertTrue(equalElementSequence(bset, testBases3));
	}

	/**
	 * {@link dtalge.DtBasePatternSet#addAll(java.util.Collection)} のためのテスト・メソッド。
	 */
	public void testAddAllCollectionOfQextendsDtBasePattern() {
		DtBasePatternSet bset;
		
		//--- null
		bset = new DtBasePatternSet();
		try {
			bset.addAll(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		bset = new DtBasePatternSet(Arrays.asList(normalAllBases));
		try {
			bset.addAll(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		//--- parameters
		DtBasePattern[] testBases13 = catBaseElements(catBaseElements(testBases1, testBases3));
		DtBasePattern[] testBases132 = catBaseElements(testBases13, testBases2);
		
		//--- new
		DtBasePatternSet bases1 = new DtBasePatternSet();
		assertTrue(bases1.isEmpty());
		assertTrue(bases1.addAll(Arrays.asList(testBases1)));
		assertTrue(equalElements(bases1, testBases1));
		assertTrue(equalElementSequence(bases1, testBases1));
		assertTrue(bases1.addAll(Arrays.asList(testBases3)));
		assertTrue(bases1.addAll(Arrays.asList(testBases2)));
		assertTrue(equalElements(bases1, testBases132));
		assertTrue(equalElementSequence(bases1, testBases132));
		
		DtBasePatternSet bases2 = new DtBasePatternSet();
		assertTrue(bases2.isEmpty());
		assertTrue(bases2.addAll(Arrays.asList(testBases1)));
		assertTrue(bases2.addAll(Arrays.asList(testBases3)));
		assertTrue(equalElements(bases2, testBases13));
		assertTrue(equalElementSequence(bases2, testBases13));
		assertFalse(bases2.addAll(Arrays.asList(testBases13)));
		assertTrue(equalElements(bases2, testBases13));
		assertTrue(equalElementSequence(bases2, testBases13));
		assertTrue(bases2.addAll(Arrays.asList(withNullAllBases)));
		assertTrue(equalElements(bases2, testBases132));
		assertTrue(equalElementSequence(bases2, testBases132));
	}

	/**
	 * {@link dtalge.DtBasePatternSet#addAll(dtalge.DtBaseSet, boolean)} のためのテスト・メソッド。
	 */
	public void testAddAllDtBaseSetBoolean() {
		DtBasePatternSet bset;
		
		//--- null
		bset = new DtBasePatternSet();
		try {
			bset.addAll(null, false);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			bset.addAll(null, true);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		bset = new DtBasePatternSet(Arrays.asList(normalAllBases));
		try {
			bset.addAll(null, false);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			bset.addAll(null, true);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		DtBaseSet bases0 = new DtBaseSet();
		assertTrue(bases0.isEmpty());
		DtBaseSet bases1 = new DtBaseSet(Arrays.asList(DtBaseSetTest.testBases1));
		assertTrue(DtBaseSetTest.equalElementSequence(bases1, DtBaseSetTest.testBases1));
		DtBaseSet bases2 = new DtBaseSet(Arrays.asList(DtBaseSetTest.testBases2));
		assertTrue(DtBaseSetTest.equalElementSequence(bases2, DtBaseSetTest.testBases2));
		DtBaseSet bases3 = new DtBaseSet(Arrays.asList(DtBaseSetTest.testBases3));
		assertTrue(DtBaseSetTest.equalElementSequence(bases3, DtBaseSetTest.testBases3));
		
		DtBasePattern[] basePatterns12 = catBaseElements(DtBaseSetTest.basePatterns1, DtBaseSetTest.basePatterns2);
		DtBasePattern[] basePatterns123 = catBaseElements(basePatterns12, DtBaseSetTest.basePatterns3);
		DtBasePattern[] ignoreTypePatterns32 = catBaseElements(DtBaseSetTest.ignoreTypePatterns3, DtBaseSetTest.ignoreTypePatterns2);
		DtBasePattern[] ignoreTypePatterns321 = catBaseElements(ignoreTypePatterns32, DtBaseSetTest.ignoreTypePatterns1);
		
		//--- empty
		DtBasePatternSet pset0 = new DtBasePatternSet();
		assertFalse(pset0.addAll(bases0, false));
		assertTrue(pset0.isEmpty());
		assertFalse(pset0.addAll(bases0, true));
		assertTrue(pset0.isEmpty());
		
		//--- use type key
		DtBasePatternSet pset1 = new DtBasePatternSet();
		assertTrue(pset1.addAll(bases1, false));
		assertTrue(equalElements(pset1, DtBaseSetTest.basePatterns1));
		assertTrue(equalElementSequence(pset1, DtBaseSetTest.basePatterns1));
		assertTrue(pset1.addAll(bases2, false));
		assertTrue(equalElements(pset1, basePatterns12));
		assertTrue(equalElementSequence(pset1, basePatterns12));
		assertTrue(pset1.addAll(bases3, false));
		assertTrue(equalElements(pset1, basePatterns123));
		assertTrue(equalElementSequence(pset1, basePatterns123));
		
		//--- ignore type key
		DtBasePatternSet pset2 = new DtBasePatternSet();
		assertTrue(pset2.addAll(bases3, true));
		assertTrue(equalElements(pset2, DtBaseSetTest.ignoreTypePatterns3));
		assertTrue(equalElementSequence(pset2, DtBaseSetTest.ignoreTypePatterns3));
		assertTrue(pset2.addAll(bases2, true));
		assertTrue(equalElements(pset2, ignoreTypePatterns32));
		assertTrue(equalElementSequence(pset2, ignoreTypePatterns32));
		assertTrue(pset2.addAll(bases1, true));
		assertTrue(equalElements(pset2, ignoreTypePatterns321));
		assertTrue(equalElementSequence(pset2, ignoreTypePatterns321));
	}

	/**
	 * {@link dtalge.DtBasePatternSet#clone()} のためのテスト・メソッド。
	 */
	public void testClone() {
		DtBasePatternSet bases1 = new DtBasePatternSet();
		DtBasePatternSet clone1 = (DtBasePatternSet)bases1.clone();
		assertNotSame(bases1, clone1);
		assertEquals(bases1, clone1);
		assertTrue(bases1.isEmpty());
		assertTrue(clone1.isEmpty());
		
		DtBasePatternSet bases2 = new DtBasePatternSet(Arrays.asList(testBases3));
		assertTrue(equalElementSequence(bases2, testBases3));
		DtBasePatternSet clone2 = (DtBasePatternSet)bases2.clone();
		assertNotSame(bases2, clone2);
		assertEquals(bases2, clone2);
		assertTrue(equalElementSequence(bases2, testBases3));
		assertTrue(equalElementSequence(clone2, testBases3));
		
		DtBasePattern[] testBases31 = catBaseElements(testBases3, testBases1);
		assertTrue(clone2.addAll(Arrays.asList(testBases1)));
		assertNotSame(bases2, clone2);
		assertFalse(bases2.equals(clone2));
		assertTrue(equalElementSequence(bases2, testBases3));
		assertTrue(equalElementSequence(clone2, testBases31));
	}

	/**
	 * {@link dtalge.AbDtBaseSet#getBaseNameKeySet(boolean)} のためのテスト・メソッド。
	 */
	public void testGetBaseNameKeySet() {
		DtBasePatternSet eset = new DtBasePatternSet();
		Set<String> es1 = eset.getBaseNameKeySet(false);
		assertTrue(es1.isEmpty());
		Set<String> es2 = eset.getBaseNameKeySet(true);
		assertTrue(es2.isEmpty());
		
		String[] ans1 = getBaseKeyArray(DtBase.KEY_NAME, false, normalAllBases);
		String[] ans2 = getBaseKeyArray(DtBase.KEY_NAME, true , normalAllBases);
		
		DtBasePatternSet bset = new DtBasePatternSet(Arrays.asList(normalAllBases));
		assertTrue(equalElements(bset, normalAllBases));
		Set<String> bs1 = bset.getBaseNameKeySet(false);
		Set<String> bs2 = bset.getBaseNameKeySet(true);
		assertTrue(DtBaseSetTest.equalStringSequence(bs1, ans1));
		assertTrue(DtBaseSetTest.equalStringSequence(bs2, ans2));
	}

	/**
	 * {@link dtalge.AbDtBaseSet#getBaseTypeKeySet(boolean)} のためのテスト・メソッド。
	 */
	public void testGetBaseTypeKeySet() {
		DtBasePatternSet eset = new DtBasePatternSet();
		Set<String> es1 = eset.getBaseTypeKeySet(false);
		assertTrue(es1.isEmpty());
		Set<String> es2 = eset.getBaseTypeKeySet(true);
		assertTrue(es2.isEmpty());
		
		String[] ans1 = getBaseKeyArray(DtBase.KEY_TYPE, false, normalAllBases);
		String[] ans2 = getBaseKeyArray(DtBase.KEY_TYPE, true , normalAllBases);
		
		DtBasePatternSet bset = new DtBasePatternSet(Arrays.asList(normalAllBases));
		assertTrue(equalElements(bset, normalAllBases));
		Set<String> bs1 = bset.getBaseTypeKeySet(false);
		Set<String> bs2 = bset.getBaseTypeKeySet(true);
		assertTrue(DtBaseSetTest.equalStringSequence(bs1, ans1));
		assertTrue(DtBaseSetTest.equalStringSequence(bs2, ans2));
	}

	/**
	 * {@link dtalge.AbDtBaseSet#getBaseAttributeKeySet(boolean)} のためのテスト・メソッド。
	 */
	public void testGetBaseAttributeKeySet() {
		DtBasePatternSet eset = new DtBasePatternSet();
		Set<String> es1 = eset.getBaseAttributeKeySet(false);
		assertTrue(es1.isEmpty());
		Set<String> es2 = eset.getBaseAttributeKeySet(true);
		assertTrue(es2.isEmpty());
		
		String[] ans1 = getBaseKeyArray(DtBase.KEY_ATTR, false, normalAllBases);
		String[] ans2 = getBaseKeyArray(DtBase.KEY_ATTR, true , normalAllBases);
		
		DtBasePatternSet bset = new DtBasePatternSet(Arrays.asList(normalAllBases));
		assertTrue(equalElements(bset, normalAllBases));
		Set<String> bs1 = bset.getBaseAttributeKeySet(false);
		Set<String> bs2 = bset.getBaseAttributeKeySet(true);
		assertTrue(DtBaseSetTest.equalStringSequence(bs1, ans1));
		assertTrue(DtBaseSetTest.equalStringSequence(bs2, ans2));
	}

	/**
	 * {@link dtalge.AbDtBaseSet#getBaseSubjectKeySet(boolean)} のためのテスト・メソッド。
	 */
	public void testGetBaseSubjectKeySet() {
		DtBasePatternSet eset = new DtBasePatternSet();
		Set<String> es1 = eset.getBaseSubjectKeySet(false);
		assertTrue(es1.isEmpty());
		Set<String> es2 = eset.getBaseSubjectKeySet(true);
		assertTrue(es2.isEmpty());
		
		String[] ans1 = getBaseKeyArray(DtBase.KEY_SUBJECT, false, normalAllBases);
		String[] ans2 = getBaseKeyArray(DtBase.KEY_SUBJECT, true , normalAllBases);
		
		DtBasePatternSet bset = new DtBasePatternSet(Arrays.asList(normalAllBases));
		assertTrue(equalElements(bset, normalAllBases));
		Set<String> bs1 = bset.getBaseSubjectKeySet(false);
		Set<String> bs2 = bset.getBaseSubjectKeySet(true);
		assertTrue(DtBaseSetTest.equalStringSequence(bs1, ans1));
		assertTrue(DtBaseSetTest.equalStringSequence(bs2, ans2));
	}
}
