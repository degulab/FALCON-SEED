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
 * @(#)DtBasePatternTest.java	0.20	2010/02/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtBasePatternTest.java	0.10	2008/08/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge;

import java.util.Arrays;

import junit.framework.TestCase;
import dtalge.util.DtDataTypes;

/**
 * <code>DtBasePattern</code> のユニットテスト。
 * このテストケースでは、ファイル入出力のテストは含まない。
 * 
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class DtBasePatternTest extends TestCase
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final String NORMAL_BASEKEY_1 = "基底キー1";
	static public final String NORMAL_BASEKEY_2 = "基底キー2";
	static public final String NORMAL_BASEKEY_3 = "基底キー3";
	static public final String NORMAL_BASEKEY_4 = "基底キー4";
	static public final String NORMAL_BASEKEY_5 = "基底キー5";
	static public final String NORMAL_BASEKEY_A = "基底*";
	static public final String NORMAL_BASEKEY_B = "基底*2";
	static public final String NORMAL_BASEKEY_C = "基底*3";
	static public final String NORMAL_BASEKEY_D = "基底*4";
	static public final String NORMAL_BASEKEY_E = "基底*5";
	//static public final String NORMAL_BASEKEY_MARK = "記号!\"#$%&'()=-=^~\\|@`[{;+:*]},<.>/?_";
	static public final String NORMAL_BASEKEY_MARK = "記号#$()=~\\`[{;+:*]}./_";
	//static public final String NORMAL_BASEKEY_ESC  = "エスケープ\"\b\f\'\0\'\\\"";
	static public final String NORMAL_BASEKEY_ESC  = "エスケープ";
	static public final String NORMAL_BASEKEY_UNI  = "ユニコード：\u3053\u3053\u307e\u3067"; // ユニコード：ここまで
	//static public final String REGEXP_BASEKEY_1 = "\\A\\s*\\Q基底キー\\E[0-9]?\\s*\\z";
	//static public final String REGEXP_BASEKEY_2 = "^基底キー(\\d{1})$";
	//static public final String REGEXP_BASEKEY_3 = "^([0-9]{4})/([0-9]{2})/([0-9]{2})";
	static public final String REGEXP_BASEKEY_1 = "\\A\\s*\\Q基底キー\\E\\d\\s*\\z";
	static public final String REGEXP_BASEKEY_2 = "基底キー(\\d{1})$";
	static public final String REGEXP_BASEKEY_3 = "(\\d{4})/(\\d{2})/(\\d{2})";
	static public final String ILLEGAL_BASEKEY_TAB  = "タブ\tのみ";
	static public final String ILLEGAL_BASEKEY_CR   = "CR\rのみ";
	static public final String ILLEGAL_BASEKEY_LF   = "LF\nのみ";
	static public final String ILLEGAL_BASEKEY_CRLF = "CRLF\r\n組み合わせ";
	static public final String ILLEGAL_BASEKEY_ALL  = "全部\r\n\t\n\r含む";
	
	static public final String DT_BOOLEAN_L = "boolean";
	static public final String DT_BOOLEAN_U = "BOOLEAN";
	static public final String DT_BOOLEAN_M = "bOoLeAn";
	static public final String DT_BOOLEAN_W = "bo*an";
	static public final String DT_INTEGER_L = "integer";
	static public final String DT_INTEGER_U = "INTEGER";
	static public final String DT_INTEGER_M = "InTeGeR";
	static public final String DT_INTEGER_W = "in*er";
	static public final String DT_DECIMAL_L = "decimal";
	static public final String DT_DECIMAL_U = "DECIMAL";
	static public final String DT_DECIMAL_M = "dEcImAl";
	static public final String DT_DECIMAL_W = "de*al";
	static public final String DT_STRING_L = "string";
	static public final String DT_STRING_U = "STRING";
	static public final String DT_STRING_M = "StRiNg";
	static public final String DT_STRING_W = "st*ng";
	
	static protected final String[] illegalOneStrings = new String[]{
		createOneKeyString(ILLEGAL_BASEKEY_ALL),
		createOneKeyString(NORMAL_BASEKEY_A, ILLEGAL_BASEKEY_ALL),
		createOneKeyString(NORMAL_BASEKEY_A, NORMAL_BASEKEY_B, ILLEGAL_BASEKEY_ALL),
		createOneKeyString(NORMAL_BASEKEY_A, NORMAL_BASEKEY_B, NORMAL_BASEKEY_C, ILLEGAL_BASEKEY_ALL),
		createOneKeyString(ILLEGAL_BASEKEY_TAB, ILLEGAL_BASEKEY_CR, ILLEGAL_BASEKEY_LF, ILLEGAL_BASEKEY_CRLF),
	};
	
	static protected final String[][] normalOneStrings = new String[][]{
		createOneKeyStringPair((String[])null),
		createOneKeyStringPair(""),
		createOneKeyStringPair(NORMAL_BASEKEY_A),
		createOneKeyStringPair(NORMAL_BASEKEY_A, NORMAL_BASEKEY_B),
		createOneKeyStringPair(NORMAL_BASEKEY_A, NORMAL_BASEKEY_B, NORMAL_BASEKEY_C),
		createOneKeyStringPair(NORMAL_BASEKEY_A, NORMAL_BASEKEY_B, NORMAL_BASEKEY_C, NORMAL_BASEKEY_D),
		createOneKeyStringPair(NORMAL_BASEKEY_E, NORMAL_BASEKEY_D, NORMAL_BASEKEY_C, NORMAL_BASEKEY_B, NORMAL_BASEKEY_A),
		createOneKeyStringPair(DtBase.OMITTED, DT_BOOLEAN_W),
		createOneKeyStringPair(DtBase.OMITTED, DT_INTEGER_W, DtBase.OMITTED),
		createOneKeyStringPair(DtBase.OMITTED, DT_DECIMAL_W, DtBase.OMITTED, DtBase.OMITTED),
		createOneKeyStringPair(DtBase.OMITTED, DT_STRING_W, DtBase.OMITTED, DtBase.OMITTED, DtBase.OMITTED),
		createOneKeyStringPair(NORMAL_BASEKEY_1, DT_BOOLEAN_M),
		createOneKeyStringPair(NORMAL_BASEKEY_1, DT_INTEGER_M, NORMAL_BASEKEY_2),
		createOneKeyStringPair(NORMAL_BASEKEY_1, DT_DECIMAL_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		createOneKeyStringPair(NORMAL_BASEKEY_5, DT_STRING_M, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2, NORMAL_BASEKEY_1),
		createOneKeyStringPair(NORMAL_BASEKEY_MARK, DT_STRING_W, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		createOneKeyStringPair(REGEXP_BASEKEY_1, DT_INTEGER_W, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
	};
	
	static public final String[][] illegalKeys = new String[][]{
		((String[])null),
		new String[]{ILLEGAL_BASEKEY_ALL},
		new String[]{NORMAL_BASEKEY_A, ILLEGAL_BASEKEY_ALL},
		new String[]{NORMAL_BASEKEY_A, NORMAL_BASEKEY_B, ILLEGAL_BASEKEY_ALL},
		new String[]{NORMAL_BASEKEY_A, NORMAL_BASEKEY_B, NORMAL_BASEKEY_C, ILLEGAL_BASEKEY_ALL},
		new String[]{ILLEGAL_BASEKEY_TAB, ILLEGAL_BASEKEY_CR, ILLEGAL_BASEKEY_LF, ILLEGAL_BASEKEY_CRLF},
	};
	
	static public final String[][][] normalKeys = new String[][][]{
		createBaseKeysPair(),
		createBaseKeysPair((String)null),
		createBaseKeysPair(null, null),
		createBaseKeysPair(null, null, null),
		createBaseKeysPair(null, null, null, null),
		createBaseKeysPair(DtBase.OMITTED, DT_BOOLEAN_M),
		createBaseKeysPair(DtBase.OMITTED, DT_INTEGER_M),
		createBaseKeysPair(DtBase.OMITTED, DT_DECIMAL_M),
		createBaseKeysPair(DtBase.OMITTED, DT_STRING_M),
		createBaseKeysPair(DtBase.OMITTED, DT_BOOLEAN_M, DtBase.OMITTED),
		createBaseKeysPair(DtBase.OMITTED, DT_INTEGER_M, DtBase.OMITTED),
		createBaseKeysPair(DtBase.OMITTED, DT_DECIMAL_M, DtBase.OMITTED),
		createBaseKeysPair(DtBase.OMITTED, DT_STRING_M, DtBase.OMITTED),
		createBaseKeysPair(DtBase.OMITTED, DT_BOOLEAN_M, DtBase.OMITTED, DtBase.OMITTED),
		createBaseKeysPair(DtBase.OMITTED, DT_INTEGER_M, DtBase.OMITTED, DtBase.OMITTED),
		createBaseKeysPair(DtBase.OMITTED, DT_DECIMAL_M, DtBase.OMITTED, DtBase.OMITTED),
		createBaseKeysPair(DtBase.OMITTED, DT_STRING_M, DtBase.OMITTED, DtBase.OMITTED),
		createBaseKeysPair(DtBase.OMITTED, DT_BOOLEAN_M, DtBase.OMITTED, DtBase.OMITTED, DtBase.OMITTED),
		createBaseKeysPair(DtBase.OMITTED, DT_INTEGER_M, DtBase.OMITTED, DtBase.OMITTED, DtBase.OMITTED),
		createBaseKeysPair(DtBase.OMITTED, DT_DECIMAL_M, DtBase.OMITTED, DtBase.OMITTED, DtBase.OMITTED),
		createBaseKeysPair(DtBase.OMITTED, DT_STRING_M, DtBase.OMITTED, DtBase.OMITTED, DtBase.OMITTED),
		createBaseKeysPair(NORMAL_BASEKEY_1, DT_BOOLEAN_M),
		createBaseKeysPair(NORMAL_BASEKEY_1, DT_INTEGER_M),
		createBaseKeysPair(NORMAL_BASEKEY_1, DT_DECIMAL_M),
		createBaseKeysPair(NORMAL_BASEKEY_1, DT_STRING_M),
		createBaseKeysPair(NORMAL_BASEKEY_1, DT_BOOLEAN_M, NORMAL_BASEKEY_2),
		createBaseKeysPair(NORMAL_BASEKEY_1, DT_INTEGER_M, NORMAL_BASEKEY_2),
		createBaseKeysPair(NORMAL_BASEKEY_1, DT_DECIMAL_M, NORMAL_BASEKEY_2),
		createBaseKeysPair(NORMAL_BASEKEY_1, DT_STRING_M, NORMAL_BASEKEY_2),
		createBaseKeysPair(NORMAL_BASEKEY_1, DT_BOOLEAN_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		createBaseKeysPair(NORMAL_BASEKEY_1, DT_INTEGER_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		createBaseKeysPair(NORMAL_BASEKEY_1, DT_DECIMAL_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		createBaseKeysPair(NORMAL_BASEKEY_1, DT_STRING_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		createBaseKeysPair(NORMAL_BASEKEY_5, DT_BOOLEAN_M, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2, NORMAL_BASEKEY_1),
		createBaseKeysPair(NORMAL_BASEKEY_5, DT_INTEGER_M, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2, NORMAL_BASEKEY_1),
		createBaseKeysPair(NORMAL_BASEKEY_5, DT_DECIMAL_M, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2, NORMAL_BASEKEY_1),
		createBaseKeysPair(NORMAL_BASEKEY_5, DT_STRING_M, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2, NORMAL_BASEKEY_1),
		createBaseKeysPair(NORMAL_BASEKEY_MARK, DT_BOOLEAN_U, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		createBaseKeysPair(NORMAL_BASEKEY_MARK, DT_INTEGER_U, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		createBaseKeysPair(NORMAL_BASEKEY_MARK, DT_DECIMAL_U, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		createBaseKeysPair(NORMAL_BASEKEY_MARK, DT_STRING_U, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		createBaseKeysPair(REGEXP_BASEKEY_1, DT_BOOLEAN_U, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		createBaseKeysPair(REGEXP_BASEKEY_1, DT_INTEGER_U, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		createBaseKeysPair(REGEXP_BASEKEY_1, DT_DECIMAL_U, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		createBaseKeysPair(REGEXP_BASEKEY_1, DT_STRING_U, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		createBaseKeysPair(NORMAL_BASEKEY_A, DT_BOOLEAN_W),
		createBaseKeysPair(NORMAL_BASEKEY_A, DT_INTEGER_W),
		createBaseKeysPair(NORMAL_BASEKEY_A, DT_DECIMAL_W),
		createBaseKeysPair(NORMAL_BASEKEY_A, DT_STRING_W),
		createBaseKeysPair(NORMAL_BASEKEY_A, DT_BOOLEAN_W, NORMAL_BASEKEY_B),
		createBaseKeysPair(NORMAL_BASEKEY_A, DT_INTEGER_W, NORMAL_BASEKEY_B),
		createBaseKeysPair(NORMAL_BASEKEY_A, DT_DECIMAL_W, NORMAL_BASEKEY_B),
		createBaseKeysPair(NORMAL_BASEKEY_A, DT_STRING_W, NORMAL_BASEKEY_B),
		createBaseKeysPair(NORMAL_BASEKEY_A, DT_BOOLEAN_W, NORMAL_BASEKEY_B, NORMAL_BASEKEY_C),
		createBaseKeysPair(NORMAL_BASEKEY_A, DT_INTEGER_W, NORMAL_BASEKEY_B, NORMAL_BASEKEY_C),
		createBaseKeysPair(NORMAL_BASEKEY_A, DT_DECIMAL_W, NORMAL_BASEKEY_B, NORMAL_BASEKEY_C),
		createBaseKeysPair(NORMAL_BASEKEY_A, DT_STRING_W, NORMAL_BASEKEY_B, NORMAL_BASEKEY_C),
		createBaseKeysPair(NORMAL_BASEKEY_E, DT_BOOLEAN_W, NORMAL_BASEKEY_D, NORMAL_BASEKEY_B, NORMAL_BASEKEY_A),
		createBaseKeysPair(NORMAL_BASEKEY_E, DT_INTEGER_W, NORMAL_BASEKEY_D, NORMAL_BASEKEY_B, NORMAL_BASEKEY_A),
		createBaseKeysPair(NORMAL_BASEKEY_E, DT_DECIMAL_W, NORMAL_BASEKEY_D, NORMAL_BASEKEY_B, NORMAL_BASEKEY_A),
		createBaseKeysPair(NORMAL_BASEKEY_E, DT_STRING_W, NORMAL_BASEKEY_D, NORMAL_BASEKEY_B, NORMAL_BASEKEY_A),
		createBaseKeysPair(NORMAL_BASEKEY_MARK, DT_BOOLEAN_W, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		createBaseKeysPair(NORMAL_BASEKEY_MARK, DT_INTEGER_W, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		createBaseKeysPair(NORMAL_BASEKEY_MARK, DT_DECIMAL_W, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		createBaseKeysPair(NORMAL_BASEKEY_MARK, DT_STRING_W, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		createBaseKeysPair(REGEXP_BASEKEY_1, DT_BOOLEAN_W, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		createBaseKeysPair(REGEXP_BASEKEY_1, DT_INTEGER_W, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		createBaseKeysPair(REGEXP_BASEKEY_1, DT_DECIMAL_W, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		createBaseKeysPair(REGEXP_BASEKEY_1, DT_STRING_W, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
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
	
	static protected String normalizeTypeKey(String typeKey) {
		if (DtDataTypes.BOOLEAN.equalsIgnoreCase(typeKey))
			return DtDataTypes.BOOLEAN;
		//else if (DtDataTypes.INTEGER.equalsIgnoreCase(typeKey))
		//	return DtDataTypes.INTEGER;
		else if ("integer".equalsIgnoreCase(typeKey))
			return "integer";
		else if (DtDataTypes.DECIMAL.equalsIgnoreCase(typeKey))
			return DtDataTypes.DECIMAL;
		else if (DtDataTypes.STRING.equalsIgnoreCase(typeKey))
			return DtDataTypes.STRING;
		else
			return typeKey;
	}

	// 指定された文字列をそのまま格納する基底キー配列を作成する。
	// この配列のサイズは、4 である。
	static public String[] createBaseKeys(String nameKey, String typeKey, String attrKey, String subjectKey) {
		String[] ret = new String[]{ nameKey, typeKey, attrKey, subjectKey };
		return ret;
	}
	
	// 任意の長さの基底キー配列を生成し、正規化された基底キー配列とセットで返す。
	//--- [0] 生成された基底キー配列
	//--- [1] 正規化れた基底キー配列
	static public String[][] createBaseKeysPair(String...keys) {
		String[] ckeys;
		if (keys != null && keys.length > 0) {
			ckeys = new String[keys.length];
			for (int i = 0; i < keys.length; i++) {
				ckeys[i] = keys[i];
			}
		} else {
			ckeys = new String[0];
		}

		String[] nkeys = new String[4];
		Arrays.fill(nkeys, DtBasePattern.WILDCARD);
		int lim = Math.min(ckeys.length, nkeys.length);
		for (int i = 0; i < lim; i++) {
			if (ckeys[i] != null && ckeys[i].length() > 0) {
				nkeys[i] = ckeys[i];
			}
		}
		
		return new String[][]{ckeys, nkeys};
	}

	// 任意の長さの基底一意文字列キーを生成する。文字列が指定されない場合は、空文字列を返す。
	static public String createOneKeyString(String...keys) {
		if (keys == null || keys.length <= 0) {
			return "";
		}
		else if (keys.length == 1) {
			return keys[0];
		}
		else {
			StringBuilder sb = new StringBuilder();
			if (keys[0] != null)	sb.append(keys[0]);
			for (int i = 1; i < keys.length; i++) {
				sb.append(DtBase.DELIMITOR);
				if (keys[i] != null) sb.append(keys[i]);
			}
			return sb.toString();
		}
	}

	// 正規化された一意文字列キーを生成する。
	static public String createNormalizedOneKeyString(String...keys) {
		String nameKey = DtBasePattern.WILDCARD;
		String typeKey = DtBasePattern.WILDCARD;
		String attrKey = DtBasePattern.WILDCARD;
		String subjectKey = DtBasePattern.WILDCARD;
		
		if (keys != null) {
			if (keys.length > 0 && keys[0] != null && keys[0].length() > 0)
				nameKey = keys[0];
			if (keys.length > 1 && keys[1] != null && keys[1].length() > 0)
				typeKey = keys[1];
			if (keys.length > 2 && keys[2] != null && keys[2].length() > 0)
				attrKey = keys[2];
			if (keys.length > 3 && keys[3] != null && keys[3].length() > 0)
				subjectKey = keys[3];
		}
		
		return (nameKey + DtBase.DELIMITOR + typeKey + DtBase.DELIMITOR + attrKey + DtBase.DELIMITOR + subjectKey);
	}

	// 任意の長さの基底一意文字列キーを生成し、正規化された一意文字列キーとセットで返す。
	//--- [0] 生成された一意文字列キー
	//--- [1] 正規化れた一意文字列キー
	static public String[] createOneKeyStringPair(String...keys) {
		String[] ret = new String[2];
		ret[0] = createOneKeyString(keys);
		ret[1] = createNormalizedOneKeyString(keys);
		return ret;
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
	 * {@link dtalge.DtBasePattern#newPattern(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewPatternString() {
		// キャッシュなし
		dtalge.util.DtConditions.setEnableCachedString(false);
		dtalge.util.DtConditions.setEnableCachedBase(false);
		//--- errors
		for (String keys : illegalOneStrings) {
			try {
				DtBasePattern base = DtBasePattern.newPattern(keys);
				fail("Illegal to valid [" + String.valueOf(keys) + "] : " + base.toString());
			}
			catch (IllegalArgumentException ex) {
				assertTrue(true);
			}
			catch (Exception ex) {
				fail("Unknown exception [new DtBase(\"" + String.valueOf(keys) + "\")] : " + ex.getMessage());
			}
		}
		
		//--- normals
		for (String[] keypair : normalOneStrings) {
			String ckey = keypair[0];
			String nkey = keypair[1];
			try {
				DtBasePattern base = DtBasePattern.newPattern(ckey);
				assertEquals("nkey(" + nkey + ") != basekey(" + base.key() + ")", nkey, base.key());
			}
			catch (Exception ex) {
				fail("[new DtBase(\"" + String.valueOf(ckey) + "\")] : " + ex.getMessage());
			}
		}
		
		// キャッシュあり
		dtalge.util.DtConditions.setEnableCachedString(true);
		dtalge.util.DtConditions.setEnableCachedBase(true);
		//--- errors
		for (String keys : illegalOneStrings) {
			try {
				DtBasePattern base = DtBasePattern.newPattern(keys);
				fail("Illegal to valid [" + String.valueOf(keys) + "] : " + base.toString());
			}
			catch (IllegalArgumentException ex) {
				assertTrue(true);
			}
			catch (Exception ex) {
				fail("Unknown exception [new DtBase(\"" + String.valueOf(keys) + "\")] : " + ex.getMessage());
			}
		}
		
		//--- normals
		for (String[] keypair : normalOneStrings) {
			String ckey = keypair[0];
			String nkey = keypair[1];
			try {
				DtBasePattern base = DtBasePattern.newPattern(ckey);
				assertEquals("nkey(" + nkey + ") != basekey(" + base.key() + ")", nkey, base.key());
			}
			catch (Exception ex) {
				fail("[new DtBase(\"" + String.valueOf(ckey) + "\")] : " + ex.getMessage());
			}
		}
	}

	/**
	 * {@link dtalge.DtBasePattern#newPattern(java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewPatternStringString() {
		// キャッシュなし
		dtalge.util.DtConditions.setEnableCachedString(false);
		dtalge.util.DtConditions.setEnableCachedBase(false);
		//--- errors
		for (String[] keys : illegalKeys) {
			if (keys != null && keys.length == 2) {
				try {
					DtBasePattern base = DtBasePattern.newPattern(keys[0], keys[1]);
					fail("Illegal to valid [" + Arrays.toString(keys) + "] : " + base.toString());
				}
				catch (IllegalArgumentException ex) {
					assertTrue(true);
				}
				catch (Exception ex) {
					fail("Unknown exception [" + Arrays.toString(keys) + "] : " + ex.getMessage());
				}
			}
		}
		
		//--- normals
		for (String[][] keypair : normalKeys) {
			if (keypair != null && keypair[0].length == 2) {
				String[] ckeys = keypair[0];
				String[] nkeys = keypair[1];
				try {
					DtBasePattern base = DtBasePattern.newPattern(ckeys[0], ckeys[1]);
					assertTrue("nkeys(" + Arrays.toString(nkeys) + " != basekey(" + Arrays.toString(base._baseKeys) + ")",
							Arrays.equals(nkeys, base._baseKeys));
				}
				catch (Exception ex) {
					fail("Unknown exception [" + Arrays.toString(ckeys) + "] : " + ex.getMessage());
				}
			}
		}
		// キャッシュあり
		dtalge.util.DtConditions.setEnableCachedString(true);
		dtalge.util.DtConditions.setEnableCachedBase(true);
		//--- errors
		for (String[] keys : illegalKeys) {
			if (keys != null && keys.length == 2) {
				try {
					DtBasePattern base = DtBasePattern.newPattern(keys[0], keys[1]);
					fail("Illegal to valid [" + Arrays.toString(keys) + "] : " + base.toString());
				}
				catch (IllegalArgumentException ex) {
					assertTrue(true);
				}
				catch (Exception ex) {
					fail("Unknown exception [" + Arrays.toString(keys) + "] : " + ex.getMessage());
				}
			}
		}
		
		//--- normals
		for (String[][] keypair : normalKeys) {
			if (keypair != null && keypair[0].length == 2) {
				String[] ckeys = keypair[0];
				String[] nkeys = keypair[1];
				try {
					DtBasePattern base = DtBasePattern.newPattern(ckeys[0], ckeys[1]);
					assertTrue("nkeys(" + Arrays.toString(nkeys) + " != basekey(" + Arrays.toString(base._baseKeys) + ")",
							Arrays.equals(nkeys, base._baseKeys));
				}
				catch (Exception ex) {
					fail("Unknown exception [" + Arrays.toString(ckeys) + "] : " + ex.getMessage());
				}
			}
		}
	}

	/**
	 * {@link dtalge.DtBasePattern#newPattern(java.lang.String, java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewPatternStringStringString() {
		// キャッシュなし
		dtalge.util.DtConditions.setEnableCachedString(false);
		dtalge.util.DtConditions.setEnableCachedBase(false);
		//--- errors
		for (String[] keys : illegalKeys) {
			if (keys != null && keys.length == 3) {
				try {
					DtBasePattern base = DtBasePattern.newPattern(keys[0], keys[1], keys[2]);
					fail("Illegal to valid [" + Arrays.toString(keys) + "] : " + base.toString());
				}
				catch (IllegalArgumentException ex) {
					assertTrue(true);
				}
				catch (Exception ex) {
					fail("Unknown exception [" + Arrays.toString(keys) + "] : " + ex.getMessage());
				}
			}
		}
		
		//--- normals
		for (String[][] keypair : normalKeys) {
			if (keypair != null && keypair[0].length == 3) {
				String[] ckeys = keypair[0];
				String[] nkeys = keypair[1];
				try {
					DtBasePattern base = DtBasePattern.newPattern(ckeys[0], ckeys[1], ckeys[2]);
					assertTrue("nkeys(" + Arrays.toString(nkeys) + " != basekey(" + Arrays.toString(base._baseKeys) + ")",
							Arrays.equals(nkeys, base._baseKeys));
				}
				catch (Exception ex) {
					fail("Unknown exception [" + Arrays.toString(ckeys) + "] : " + ex.getMessage());
				}
			}
		}
		// キャッシュあり
		dtalge.util.DtConditions.setEnableCachedString(true);
		dtalge.util.DtConditions.setEnableCachedBase(true);
		//--- errors
		for (String[] keys : illegalKeys) {
			if (keys != null && keys.length == 3) {
				try {
					DtBasePattern base = DtBasePattern.newPattern(keys[0], keys[1], keys[2]);
					fail("Illegal to valid [" + Arrays.toString(keys) + "] : " + base.toString());
				}
				catch (IllegalArgumentException ex) {
					assertTrue(true);
				}
				catch (Exception ex) {
					fail("Unknown exception [" + Arrays.toString(keys) + "] : " + ex.getMessage());
				}
			}
		}
		
		//--- normals
		for (String[][] keypair : normalKeys) {
			if (keypair != null && keypair[0].length == 3) {
				String[] ckeys = keypair[0];
				String[] nkeys = keypair[1];
				try {
					DtBasePattern base = DtBasePattern.newPattern(ckeys[0], ckeys[1], ckeys[2]);
					assertTrue("nkeys(" + Arrays.toString(nkeys) + " != basekey(" + Arrays.toString(base._baseKeys) + ")",
							Arrays.equals(nkeys, base._baseKeys));
				}
				catch (Exception ex) {
					fail("Unknown exception [" + Arrays.toString(ckeys) + "] : " + ex.getMessage());
				}
			}
		}
	}

	/**
	 * {@link dtalge.DtBasePattern#newPattern(java.lang.String, java.lang.String, java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewPatternStringStringStringString() {
		// キャッシュなし
		dtalge.util.DtConditions.setEnableCachedString(false);
		dtalge.util.DtConditions.setEnableCachedBase(false);
		//--- errors
		for (String[] keys : illegalKeys) {
			if (keys != null && keys.length == 4) {
				try {
					DtBasePattern base = DtBasePattern.newPattern(keys[0], keys[1], keys[2], keys[3]);
					fail("Illegal to valid [" + Arrays.toString(keys) + "] : " + base.toString());
				}
				catch (IllegalArgumentException ex) {
					assertTrue(true);
				}
				catch (Exception ex) {
					fail("Unknown exception [" + Arrays.toString(keys) + "] : " + ex.getMessage());
				}
			}
		}
		
		//--- normals
		for (String[][] keypair : normalKeys) {
			if (keypair != null && keypair[0].length == 4) {
				String[] ckeys = keypair[0];
				String[] nkeys = keypair[1];
				try {
					DtBasePattern base = DtBasePattern.newPattern(ckeys[0], ckeys[1], ckeys[2], ckeys[3]);
					assertTrue("nkeys(" + Arrays.toString(nkeys) + " != basekey(" + Arrays.toString(base._baseKeys) + ")",
							Arrays.equals(nkeys, base._baseKeys));
				}
				catch (Exception ex) {
					fail("Unknown exception [" + Arrays.toString(ckeys) + "] : " + ex.getMessage());
				}
			}
		}
		// キャッシュあり
		dtalge.util.DtConditions.setEnableCachedString(true);
		dtalge.util.DtConditions.setEnableCachedBase(true);
		//--- errors
		for (String[] keys : illegalKeys) {
			if (keys != null && keys.length == 4) {
				try {
					DtBasePattern base = DtBasePattern.newPattern(keys[0], keys[1], keys[2], keys[3]);
					fail("Illegal to valid [" + Arrays.toString(keys) + "] : " + base.toString());
				}
				catch (IllegalArgumentException ex) {
					assertTrue(true);
				}
				catch (Exception ex) {
					fail("Unknown exception [" + Arrays.toString(keys) + "] : " + ex.getMessage());
				}
			}
		}
		
		//--- normals
		for (String[][] keypair : normalKeys) {
			if (keypair != null && keypair[0].length == 4) {
				String[] ckeys = keypair[0];
				String[] nkeys = keypair[1];
				try {
					DtBasePattern base = DtBasePattern.newPattern(ckeys[0], ckeys[1], ckeys[2], ckeys[3]);
					assertTrue("nkeys(" + Arrays.toString(nkeys) + " != basekey(" + Arrays.toString(base._baseKeys) + ")",
							Arrays.equals(nkeys, base._baseKeys));
				}
				catch (Exception ex) {
					fail("Unknown exception [" + Arrays.toString(ckeys) + "] : " + ex.getMessage());
				}
			}
		}
	}

	/**
	 * {@link dtalge.DtBasePattern#newPattern(java.lang.String[])} のためのテスト・メソッド。
	 */
	public void testNewPatternStringArray() {
		// キャッシュなし
		dtalge.util.DtConditions.setEnableCachedString(false);
		dtalge.util.DtConditions.setEnableCachedBase(false);
		//--- errors
		for (String[] keys : illegalKeys) {
			try {
				DtBasePattern base = DtBasePattern.newPattern(keys);
				fail("Illegal to valid [" + Arrays.toString(keys) + "] : " + base.toString());
			}
			catch (IllegalArgumentException ex) {
				assertTrue(true);
			}
			catch (Exception ex) {
				if (!(keys == null && ex instanceof NullPointerException)) {
					fail("Unknown exception [" + Arrays.toString(keys) + "] : " + ex.getMessage());
				}
			}
		}
		
		//--- normals
		for (String[][] keypair : normalKeys) {
			String[] ckeys = keypair[0];
			String[] nkeys = keypair[1];
			try {
				DtBasePattern base = DtBasePattern.newPattern(ckeys);
				assertTrue("nkeys(" + Arrays.toString(nkeys) + " != basekey(" + Arrays.toString(base._baseKeys) + ")",
						Arrays.equals(nkeys, base._baseKeys));
			}
			catch (Exception ex) {
				fail("Unknown exception [" + Arrays.toString(ckeys) + "] : " + ex.getMessage());
			}
		}
		// キャッシュあり
		dtalge.util.DtConditions.setEnableCachedString(true);
		dtalge.util.DtConditions.setEnableCachedBase(true);
		//--- errors
		for (String[] keys : illegalKeys) {
			try {
				DtBasePattern base = DtBasePattern.newPattern(keys);
				fail("Illegal to valid [" + Arrays.toString(keys) + "] : " + base.toString());
			}
			catch (IllegalArgumentException ex) {
				assertTrue(true);
			}
			catch (Exception ex) {
				if (!(keys == null && ex instanceof NullPointerException)) {
					fail("Unknown exception [" + Arrays.toString(keys) + "] : " + ex.getMessage());
				}
			}
		}
		
		//--- normals
		for (String[][] keypair : normalKeys) {
			String[] ckeys = keypair[0];
			String[] nkeys = keypair[1];
			try {
				DtBasePattern base = DtBasePattern.newPattern(ckeys);
				assertTrue("nkeys(" + Arrays.toString(nkeys) + " != basekey(" + Arrays.toString(base._baseKeys) + ")",
						Arrays.equals(nkeys, base._baseKeys));
			}
			catch (Exception ex) {
				fail("Unknown exception [" + Arrays.toString(ckeys) + "] : " + ex.getMessage());
			}
		}
	}

	/**
	 * {@link dtalge.DtBasePattern#newPattern(dtalge.DtBase, boolean)} のためのテスト・メソッド。
	 */
	public void testNewPatternDtBaseBoolean() {
		// キャッシュなし
		dtalge.util.DtConditions.setEnableCachedString(false);
		dtalge.util.DtConditions.setEnableCachedBase(false);
		//--- null check
		try {
			DtBasePattern base = new DtBasePattern(null, true);
			fail("Illegal to valid [null] : " + base.toString());
		}
		catch (NullPointerException ex) {
			assertTrue(true);
		}
		catch (Exception ex) {
			fail("Unknown exception [null] : " + ex.getMessage());
		}
		try {
			DtBasePattern base = new DtBasePattern(null, false);
			fail("Illegal to valid [null] : " + base.toString());
		}
		catch (NullPointerException ex) {
			assertTrue(true);
		}
		catch (Exception ex) {
			fail("Unknown exception [null] : " + ex.getMessage());
		}
		//--- same DtBase check
		for (String[][] keypair : DtBaseTest.normalKeys) {
			String[] ckeys = keypair[0];
			String[] nkeys = keypair[1];
			try {
				DtBasePattern base = DtBasePattern.newPattern(DtBase.newBase(ckeys), false);
				assertTrue("nkeys(" + Arrays.toString(nkeys) + " != basekey(" + Arrays.toString(base._baseKeys) + ")",
						Arrays.equals(nkeys, base._baseKeys));
			}
			catch (Exception ex) {
				fail("Unknown exception [" + Arrays.toString(ckeys) + "] : " + ex.getMessage());
			}
		}
		//--- type key wildcard check
		for (String[][] keypair : DtBaseTest.normalKeys) {
			String[] ckeys = keypair[0];
			String[] nkeys = new String[keypair[1].length];
			for (int i=0; i<nkeys.length; i++) nkeys[i] = keypair[1][i];
			nkeys[1] = DtBasePattern.WILDCARD;
			try {
				DtBasePattern base = DtBasePattern.newPattern(DtBase.newBase(ckeys), true);
				assertTrue("nkeys(" + Arrays.toString(nkeys) + " != basekey(" + Arrays.toString(base._baseKeys) + ")",
						Arrays.equals(nkeys, base._baseKeys));
			}
			catch (Exception ex) {
				fail("Unknown exception [" + Arrays.toString(ckeys) + "] : " + ex.getMessage());
			}
		}
		// キャッシュあり
		dtalge.util.DtConditions.setEnableCachedString(true);
		dtalge.util.DtConditions.setEnableCachedBase(true);
		//--- null check
		try {
			DtBasePattern base = new DtBasePattern(null, true);
			fail("Illegal to valid [null] : " + base.toString());
		}
		catch (NullPointerException ex) {
			assertTrue(true);
		}
		catch (Exception ex) {
			fail("Unknown exception [null] : " + ex.getMessage());
		}
		try {
			DtBasePattern base = new DtBasePattern(null, false);
			fail("Illegal to valid [null] : " + base.toString());
		}
		catch (NullPointerException ex) {
			assertTrue(true);
		}
		catch (Exception ex) {
			fail("Unknown exception [null] : " + ex.getMessage());
		}
		//--- same DtBase check
		for (String[][] keypair : DtBaseTest.normalKeys) {
			String[] ckeys = keypair[0];
			String[] nkeys = keypair[1];
			try {
				DtBasePattern base = DtBasePattern.newPattern(DtBase.newBase(ckeys), false);
				assertTrue("nkeys(" + Arrays.toString(nkeys) + " != basekey(" + Arrays.toString(base._baseKeys) + ")",
						Arrays.equals(nkeys, base._baseKeys));
			}
			catch (Exception ex) {
				fail("Unknown exception [" + Arrays.toString(ckeys) + "] : " + ex.getMessage());
			}
		}
		//--- type key wildcard check
		for (String[][] keypair : DtBaseTest.normalKeys) {
			String[] ckeys = keypair[0];
			String[] nkeys = new String[keypair[1].length];
			for (int i=0; i<nkeys.length; i++) nkeys[i] = keypair[1][i];
			nkeys[1] = DtBasePattern.WILDCARD;
			try {
				DtBasePattern base = DtBasePattern.newPattern(DtBase.newBase(ckeys), true);
				assertTrue("nkeys(" + Arrays.toString(nkeys) + " != basekey(" + Arrays.toString(base._baseKeys) + ")",
						Arrays.equals(nkeys, base._baseKeys));
			}
			catch (Exception ex) {
				fail("Unknown exception [" + Arrays.toString(ckeys) + "] : " + ex.getMessage());
			}
		}
	}

	/**
	 * {@link dtalge.DtBasePattern#DtBasePattern(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testDtBasePatternString() {
		//--- errors
		for (String keys : illegalOneStrings) {
			try {
				DtBasePattern base = new DtBasePattern(keys);
				fail("Illegal to valid [" + String.valueOf(keys) + "] : " + base.toString());
			}
			catch (IllegalArgumentException ex) {
				assertTrue(true);
			}
			catch (Exception ex) {
				fail("Unknown exception [new DtBase(\"" + String.valueOf(keys) + "\")] : " + ex.getMessage());
			}
		}
		
		//--- normals
		for (String[] keypair : normalOneStrings) {
			String ckey = keypair[0];
			String nkey = keypair[1];
			try {
				DtBasePattern base = new DtBasePattern(ckey);
				assertEquals("nkey(" + nkey + ") != basekey(" + base.key() + ")", nkey, base.key());
			}
			catch (Exception ex) {
				fail("[new DtBase(\"" + String.valueOf(ckey) + "\")] : " + ex.getMessage());
			}
		}
	}

	/**
	 * {@link dtalge.DtBasePattern#DtBasePattern(java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testDtBasePatternStringString() {
		//--- errors
		for (String[] keys : illegalKeys) {
			if (keys != null && keys.length == 2) {
				try {
					DtBasePattern base = new DtBasePattern(keys[0], keys[1]);
					fail("Illegal to valid [" + Arrays.toString(keys) + "] : " + base.toString());
				}
				catch (IllegalArgumentException ex) {
					assertTrue(true);
				}
				catch (Exception ex) {
					fail("Unknown exception [" + Arrays.toString(keys) + "] : " + ex.getMessage());
				}
			}
		}
		
		//--- normals
		for (String[][] keypair : normalKeys) {
			if (keypair != null && keypair[0].length == 2) {
				String[] ckeys = keypair[0];
				String[] nkeys = keypair[1];
				try {
					DtBasePattern base = new DtBasePattern(ckeys[0], ckeys[1]);
					assertTrue("nkeys(" + Arrays.toString(nkeys) + " != basekey(" + Arrays.toString(base._baseKeys) + ")",
							Arrays.equals(nkeys, base._baseKeys));
				}
				catch (Exception ex) {
					fail("Unknown exception [" + Arrays.toString(ckeys) + "] : " + ex.getMessage());
				}
			}
		}
	}

	/**
	 * {@link dtalge.DtBasePattern#DtBasePattern(java.lang.String, java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testDtBasePatternStringStringString() {
		//--- errors
		for (String[] keys : illegalKeys) {
			if (keys != null && keys.length == 3) {
				try {
					DtBasePattern base = new DtBasePattern(keys[0], keys[1], keys[2]);
					fail("Illegal to valid [" + Arrays.toString(keys) + "] : " + base.toString());
				}
				catch (IllegalArgumentException ex) {
					assertTrue(true);
				}
				catch (Exception ex) {
					fail("Unknown exception [" + Arrays.toString(keys) + "] : " + ex.getMessage());
				}
			}
		}
		
		//--- normals
		for (String[][] keypair : normalKeys) {
			if (keypair != null && keypair[0].length == 3) {
				String[] ckeys = keypair[0];
				String[] nkeys = keypair[1];
				try {
					DtBasePattern base = new DtBasePattern(ckeys[0], ckeys[1], ckeys[2]);
					assertTrue("nkeys(" + Arrays.toString(nkeys) + " != basekey(" + Arrays.toString(base._baseKeys) + ")",
							Arrays.equals(nkeys, base._baseKeys));
				}
				catch (Exception ex) {
					fail("Unknown exception [" + Arrays.toString(ckeys) + "] : " + ex.getMessage());
				}
			}
		}
	}

	/**
	 * {@link dtalge.DtBasePattern#DtBasePattern(java.lang.String, java.lang.String, java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testDtBasePatternStringStringStringString() {
		//--- errors
		for (String[] keys : illegalKeys) {
			if (keys != null && keys.length == 4) {
				try {
					DtBasePattern base = new DtBasePattern(keys[0], keys[1], keys[2], keys[3]);
					fail("Illegal to valid [" + Arrays.toString(keys) + "] : " + base.toString());
				}
				catch (IllegalArgumentException ex) {
					assertTrue(true);
				}
				catch (Exception ex) {
					fail("Unknown exception [" + Arrays.toString(keys) + "] : " + ex.getMessage());
				}
			}
		}
		
		//--- normals
		for (String[][] keypair : normalKeys) {
			if (keypair != null && keypair[0].length == 4) {
				String[] ckeys = keypair[0];
				String[] nkeys = keypair[1];
				try {
					DtBasePattern base = new DtBasePattern(ckeys[0], ckeys[1], ckeys[2], ckeys[3]);
					assertTrue("nkeys(" + Arrays.toString(nkeys) + " != basekey(" + Arrays.toString(base._baseKeys) + ")",
							Arrays.equals(nkeys, base._baseKeys));
				}
				catch (Exception ex) {
					fail("Unknown exception [" + Arrays.toString(ckeys) + "] : " + ex.getMessage());
				}
			}
		}
	}

	/**
	 * {@link dtalge.DtBasePattern#DtBasePattern(java.lang.String[])} のためのテスト・メソッド。
	 */
	public void testDtBasePatternStringArray() {
		//--- errors
		for (String[] keys : illegalKeys) {
			try {
				DtBasePattern base = new DtBasePattern(keys);
				fail("Illegal to valid [" + Arrays.toString(keys) + "] : " + base.toString());
			}
			catch (IllegalArgumentException ex) {
				assertTrue(true);
			}
			catch (Exception ex) {
				if (!(keys == null && ex instanceof NullPointerException)) {
					fail("Unknown exception [" + Arrays.toString(keys) + "] : " + ex.getMessage());
				}
			}
		}
		
		//--- normals
		for (String[][] keypair : normalKeys) {
			String[] ckeys = keypair[0];
			String[] nkeys = keypair[1];
			try {
				DtBasePattern base = new DtBasePattern(ckeys);
				assertTrue("nkeys(" + Arrays.toString(nkeys) + " != basekey(" + Arrays.toString(base._baseKeys) + ")",
						Arrays.equals(nkeys, base._baseKeys));
			}
			catch (Exception ex) {
				fail("Unknown exception [" + Arrays.toString(ckeys) + "] : " + ex.getMessage());
			}
		}
	}

	/**
	 * {@link dtalge.DtBasePattern#DtBasePattern(dtalge.DtBase, boolean)} のためのテスト・メソッド。
	 */
	public void testDtBasePatternDtBaseBoolean() {
		//--- null check
		try {
			DtBasePattern base = new DtBasePattern(null, true);
			fail("Illegal to valid [null] : " + base.toString());
		}
		catch (NullPointerException ex) {
			assertTrue(true);
		}
		catch (Exception ex) {
			fail("Unknown exception [null] : " + ex.getMessage());
		}
		try {
			DtBasePattern base = new DtBasePattern(null, false);
			fail("Illegal to valid [null] : " + base.toString());
		}
		catch (NullPointerException ex) {
			assertTrue(true);
		}
		catch (Exception ex) {
			fail("Unknown exception [null] : " + ex.getMessage());
		}
		//--- same DtBase check
		for (String[][] keypair : DtBaseTest.normalKeys) {
			String[] ckeys = keypair[0];
			String[] nkeys = keypair[1];
			try {
				DtBasePattern base = new DtBasePattern(new DtBase(ckeys), false);
				assertTrue("nkeys(" + Arrays.toString(nkeys) + " != basekey(" + Arrays.toString(base._baseKeys) + ")",
						Arrays.equals(nkeys, base._baseKeys));
			}
			catch (Exception ex) {
				fail("Unknown exception [" + Arrays.toString(ckeys) + "] : " + ex.getMessage());
			}
		}
		//--- type key wildcard check
		for (String[][] keypair : DtBaseTest.normalKeys) {
			String[] ckeys = keypair[0];
			String[] nkeys = new String[keypair[1].length];
			for (int i=0; i<nkeys.length; i++) nkeys[i] = keypair[1][i];
			nkeys[1] = DtBasePattern.WILDCARD;
			try {
				DtBasePattern base = new DtBasePattern(new DtBase(ckeys), true);
				assertTrue("nkeys(" + Arrays.toString(nkeys) + " != basekey(" + Arrays.toString(base._baseKeys) + ")",
						Arrays.equals(nkeys, base._baseKeys));
			}
			catch (Exception ex) {
				fail("Unknown exception [" + Arrays.toString(ckeys) + "] : " + ex.getMessage());
			}
		}
	}

	/**
	 * {@link dtalge.DtBasePattern#matches(dtalge.DtBase)} のためのテスト・メソッド。
	 */
	public void testMatches() {
		DtBase base1 = new DtBase(NORMAL_BASEKEY_1, DT_STRING_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3);
		DtBase base2 = new DtBase(NORMAL_BASEKEY_A, DT_DECIMAL_M, NORMAL_BASEKEY_B, NORMAL_BASEKEY_C);
		//DtBase base3 = new DtBase(NORMAL_BASEKEY_MARK, DT_INTEGER_M, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI);
		DtBase base4 = new DtBase(REGEXP_BASEKEY_1, DT_BOOLEAN_M, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3);
		
		//--- only name
		{
			DtBasePattern p1 = new DtBasePattern(NORMAL_BASEKEY_1);
			assertTrue (p1.matches(base1));
			assertFalse(p1.matches(base2));
			//assertFalse(p1.matches(base3));
			assertFalse(p1.matches(base4));
			DtBasePattern p2 = new DtBasePattern(NORMAL_BASEKEY_A);
			assertTrue (p2.matches(base1));
			assertTrue (p2.matches(base2));
			//assertFalse(p2.matches(base3));
			assertFalse(p2.matches(base4));
			DtBasePattern p3 = new DtBasePattern(NORMAL_BASEKEY_MARK);
			assertFalse(p3.matches(base1));
			assertFalse(p3.matches(base2));
			//assertTrue (p3.matches(base3));
			assertFalse(p3.matches(base4));
			DtBasePattern p4 = new DtBasePattern(REGEXP_BASEKEY_1);
			assertFalse(p4.matches(base1));
			assertFalse(p4.matches(base2));
			//assertFalse(p4.matches(base3));
			assertTrue (p4.matches(base4));
		}
		//--- only type
		{
			DtBasePattern p1 = new DtBasePattern(null, DT_STRING_L);
			assertTrue (p1.matches(base1));
			assertFalse(p1.matches(base2));
			//assertFalse(p1.matches(base3));
			assertFalse(p1.matches(base4));
			DtBasePattern p2 = new DtBasePattern(null, DT_DECIMAL_L);
			assertFalse(p2.matches(base1));
			assertTrue (p2.matches(base2));
			//assertFalse(p2.matches(base3));
			assertFalse(p2.matches(base4));
			DtBasePattern p3 = new DtBasePattern(null, DT_INTEGER_L);
			assertFalse(p3.matches(base1));
			assertFalse(p3.matches(base2));
			//assertTrue (p3.matches(base3));
			assertFalse(p3.matches(base4));
			DtBasePattern p4 = new DtBasePattern(null, DT_BOOLEAN_L);
			assertFalse(p4.matches(base1));
			assertFalse(p4.matches(base2));
			//assertFalse(p4.matches(base3));
			assertTrue (p4.matches(base4));
			DtBasePattern p5 = new DtBasePattern(null, DT_DECIMAL_W);
			assertFalse(p5.matches(base1));
			assertTrue (p5.matches(base2));
			//assertFalse(p5.matches(base3));
			assertFalse(p5.matches(base4));
		}
		//--- only attr
		{
			DtBasePattern p1 = new DtBasePattern(null, null, NORMAL_BASEKEY_2);
			assertTrue (p1.matches(base1));
			assertFalse(p1.matches(base2));
			//assertFalse(p1.matches(base3));
			assertFalse(p1.matches(base4));
			DtBasePattern p2 = new DtBasePattern(null, null, NORMAL_BASEKEY_B);
			assertTrue (p2.matches(base1));
			assertTrue (p2.matches(base2));
			//assertFalse(p2.matches(base3));
			assertFalse(p2.matches(base4));
			DtBasePattern p3 = new DtBasePattern(null, null, NORMAL_BASEKEY_ESC);
			assertFalse(p3.matches(base1));
			assertFalse(p3.matches(base2));
			//assertTrue (p3.matches(base3));
			assertFalse(p3.matches(base4));
			DtBasePattern p4 = new DtBasePattern(null, null, REGEXP_BASEKEY_2);
			assertFalse(p4.matches(base1));
			assertFalse(p4.matches(base2));
			//assertFalse(p4.matches(base3));
			assertTrue (p4.matches(base4));
		}
		//--- only subject
		{
			DtBasePattern p1 = new DtBasePattern(null, null, null, NORMAL_BASEKEY_3);
			assertTrue (p1.matches(base1));
			assertFalse(p1.matches(base2));
			//assertFalse(p1.matches(base3));
			assertFalse(p1.matches(base4));
			DtBasePattern p2 = new DtBasePattern(null, null, null, NORMAL_BASEKEY_C);
			assertTrue (p2.matches(base1));
			assertTrue (p2.matches(base2));
			//assertFalse(p2.matches(base3));
			assertFalse(p2.matches(base4));
			DtBasePattern p3 = new DtBasePattern(null, null, null, NORMAL_BASEKEY_UNI);
			assertFalse(p3.matches(base1));
			assertFalse(p3.matches(base2));
			//assertTrue (p3.matches(base3));
			assertFalse(p3.matches(base4));
			DtBasePattern p4 = new DtBasePattern(null, null, null, REGEXP_BASEKEY_3);
			assertFalse(p4.matches(base1));
			assertFalse(p4.matches(base2));
			//assertFalse(p4.matches(base3));
			assertTrue (p4.matches(base4));
		}
		//--- all
		{
			DtBasePattern p1 = new DtBasePattern(NORMAL_BASEKEY_1, DT_STRING_W, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3);
			assertTrue (p1.matches(base1));
			assertFalse(p1.matches(base2));
			//assertFalse(p1.matches(base3));
			assertFalse(p1.matches(base4));
			DtBasePattern p2 = new DtBasePattern(NORMAL_BASEKEY_A, "*", NORMAL_BASEKEY_B, NORMAL_BASEKEY_C);
			assertTrue (p2.matches(base1));
			assertTrue (p2.matches(base2));
			//assertFalse(p2.matches(base3));
			assertFalse(p2.matches(base4));
			DtBasePattern p3 = new DtBasePattern(NORMAL_BASEKEY_MARK, DT_INTEGER_W, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI);
			assertFalse(p3.matches(base1));
			assertFalse(p3.matches(base2));
			//assertTrue (p3.matches(base3));
			assertFalse(p3.matches(base4));
			DtBasePattern p4 = new DtBasePattern(REGEXP_BASEKEY_1, DT_BOOLEAN_W, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3);
			assertFalse(p4.matches(base1));
			assertFalse(p4.matches(base2));
			//assertFalse(p4.matches(base3));
			assertTrue (p4.matches(base4));
		}
	}

	/**
	 * {@link dtalge.DtBasePattern#compareTo(dtalge.DtBasePattern)} のためのテスト・メソッド。
	 */
	public void testCompareTo() {
		DtBasePattern base1 = new DtBasePattern(DT_BOOLEAN_M, DT_INTEGER_M, DT_DECIMAL_M, DT_STRING_M);
		DtBasePattern base2 = new DtBasePattern(DT_BOOLEAN_L, DT_INTEGER_L, DT_DECIMAL_L, DT_STRING_L);
		DtBasePattern base3 = new DtBasePattern(DT_BOOLEAN_U, DT_INTEGER_U, DT_DECIMAL_U, DT_STRING_U);
		DtBasePattern base4 = new DtBasePattern(DT_BOOLEAN_M, DT_INTEGER_M, DT_DECIMAL_M, DT_STRING_M);
		DtBasePattern base5 = new DtBasePattern(DT_BOOLEAN_L, DT_INTEGER_L, DT_DECIMAL_L, DT_STRING_L);
		DtBasePattern base6 = new DtBasePattern(DT_BOOLEAN_U, DT_INTEGER_U, DT_DECIMAL_U, DT_STRING_U);
		DtBasePattern baseN = new DtBasePattern(DT_BOOLEAN_M, DT_INTEGER_M, DT_DECIMAL_M, NORMAL_BASEKEY_1);
		
		assertTrue(base1.compareTo(base2) < 0);
		assertTrue(base2.compareTo(base1) > 0);
		assertTrue(base1.compareTo(base3) > 0);
		assertTrue(base3.compareTo(base1) < 0);
		assertTrue(base2.compareTo(base3) > 0);
		assertTrue(base3.compareTo(base2) < 0);
		
		assertTrue(base1.compareTo(base4) == 0);
		assertTrue(base2.compareTo(base5) == 0);
		assertTrue(base3.compareTo(base6) == 0);
		
		assertTrue(base1.compareTo(baseN) != 0);
		assertTrue(base2.compareTo(baseN) != 0);
		assertTrue(base3.compareTo(baseN) != 0);
		assertTrue(baseN.compareTo(base1) != 0);
		assertTrue(baseN.compareTo(base2) != 0);
		assertTrue(baseN.compareTo(base3) != 0);
	}

	/**
	 * {@link dtalge.DtBasePattern#compareToIgnoreCase(dtalge.DtBasePattern)} のためのテスト・メソッド。
	 */
	public void testCompareToIgnoreCase() {
		DtBasePattern base1 = new DtBasePattern(DT_BOOLEAN_M, DT_INTEGER_M, DT_DECIMAL_M, DT_STRING_M);
		DtBasePattern base2 = new DtBasePattern(DT_BOOLEAN_L, DT_INTEGER_L, DT_DECIMAL_L, DT_STRING_L);
		DtBasePattern base3 = new DtBasePattern(DT_BOOLEAN_U, DT_INTEGER_U, DT_DECIMAL_U, DT_STRING_U);
		DtBasePattern baseN = new DtBasePattern(DT_BOOLEAN_M, DT_INTEGER_M, DT_DECIMAL_M, NORMAL_BASEKEY_1);
		
		assertTrue(base1.compareToIgnoreCase(base2) == 0);
		assertTrue(base2.compareToIgnoreCase(base1) == 0);
		assertTrue(base1.compareToIgnoreCase(base3) == 0);
		assertTrue(base3.compareToIgnoreCase(base1) == 0);
		assertTrue(base2.compareToIgnoreCase(base3) == 0);
		assertTrue(base3.compareToIgnoreCase(base2) == 0);
		
		assertTrue(base1.compareToIgnoreCase(baseN) != 0);
		assertTrue(base2.compareToIgnoreCase(baseN) != 0);
		assertTrue(base3.compareToIgnoreCase(baseN) != 0);
		assertTrue(baseN.compareToIgnoreCase(base1) != 0);
		assertTrue(baseN.compareToIgnoreCase(base2) != 0);
		assertTrue(baseN.compareToIgnoreCase(base3) != 0);
	}

	/**
	 * {@link dtalge.AbDtBase#hashCode()} のためのテスト・メソッド。
	 */
	public void testHashCode() {
		//---
		String[][] keys1 = createBaseKeysPair(NORMAL_BASEKEY_A, NORMAL_BASEKEY_B, NORMAL_BASEKEY_C, NORMAL_BASEKEY_D);
		DtBasePattern base1 = new DtBasePattern(keys1[0]);
		assertTrue(Arrays.equals(base1._baseKeys, keys1[1]));
		assertEquals(Arrays.hashCode(keys1[1]), base1.hashCode());
		//---
		String[][] keys2 = createBaseKeysPair(NORMAL_BASEKEY_1, DT_INTEGER_M);
		DtBasePattern base2 = new DtBasePattern(keys2[0]);
		assertTrue(Arrays.equals(base2._baseKeys, keys2[1]));
		assertEquals(Arrays.hashCode(keys2[1]), base2.hashCode());
		//---
		String[][] keys3 = createBaseKeysPair(DtBase.OMITTED, DT_DECIMAL_W, DtBase.OMITTED, REGEXP_BASEKEY_3);
		DtBasePattern base3 = new DtBasePattern(keys3[0]);
		assertTrue(Arrays.equals(base3._baseKeys, keys3[1]));
		assertEquals(Arrays.hashCode(keys3[1]), base3.hashCode());
		//---
		DtBasePattern b1 = new DtBasePattern(NORMAL_BASEKEY_MARK, DT_STRING_W, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI);
		DtBasePattern b2 = new DtBasePattern(NORMAL_BASEKEY_MARK, DT_STRING_W, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI);
		DtBasePattern b3 = new DtBasePattern(NORMAL_BASEKEY_MARK, DT_STRING_W, NORMAL_BASEKEY_ESC, REGEXP_BASEKEY_1);
		assertTrue(b1.hashCode() == b2.hashCode());
		assertTrue(b1.hashCode() != b3.hashCode());
	}

	/**
	 * {@link dtalge.AbDtBase#getNumAllKeys()} のためのテスト・メソッド。
	 */
	public void testGetNumAllKeys() {
		assertEquals(4, DtBasePattern.getNumAllKeys());
	}

	/**
	 * {@link dtalge.AbDtBase#getNameKey()} のためのテスト・メソッド。
	 */
	public void testGetNameKey() {
		String[] key = createBaseKeys(NORMAL_BASEKEY_A, NORMAL_BASEKEY_B, NORMAL_BASEKEY_C, NORMAL_BASEKEY_D);
		DtBasePattern base = new DtBasePattern(NORMAL_BASEKEY_A, NORMAL_BASEKEY_B, NORMAL_BASEKEY_C, NORMAL_BASEKEY_D);
		assertTrue(Arrays.equals(base._baseKeys, key));
		assertTrue (key[0].equals(base.getNameKey()));
		assertFalse(key[1].equals(base.getNameKey()));
		assertFalse(key[2].equals(base.getNameKey()));
		assertFalse(key[3].equals(base.getNameKey()));
	}

	/**
	 * {@link dtalge.AbDtBase#getTypeKey()} のためのテスト・メソッド。
	 */
	public void testGetTypeKey() {
		String[] key = createBaseKeys(NORMAL_BASEKEY_A, NORMAL_BASEKEY_B, NORMAL_BASEKEY_C, NORMAL_BASEKEY_D);
		DtBasePattern base = new DtBasePattern(NORMAL_BASEKEY_A, NORMAL_BASEKEY_B, NORMAL_BASEKEY_C, NORMAL_BASEKEY_D);
		assertTrue(Arrays.equals(base._baseKeys, key));
		assertFalse(key[0].equals(base.getTypeKey()));
		assertTrue (key[1].equals(base.getTypeKey()));
		assertFalse(key[2].equals(base.getTypeKey()));
		assertFalse(key[3].equals(base.getTypeKey()));
	}

	/**
	 * {@link dtalge.AbDtBase#getAttributeKey()} のためのテスト・メソッド。
	 */
	public void testGetAttributeKey() {
		String[] key = createBaseKeys(NORMAL_BASEKEY_A, NORMAL_BASEKEY_B, NORMAL_BASEKEY_C, NORMAL_BASEKEY_D);
		DtBasePattern base = new DtBasePattern(NORMAL_BASEKEY_A, NORMAL_BASEKEY_B, NORMAL_BASEKEY_C, NORMAL_BASEKEY_D);
		assertTrue(Arrays.equals(base._baseKeys, key));
		assertFalse(key[0].equals(base.getAttributeKey()));
		assertFalse(key[1].equals(base.getAttributeKey()));
		assertTrue (key[2].equals(base.getAttributeKey()));
		assertFalse(key[3].equals(base.getAttributeKey()));
	}

	/**
	 * {@link dtalge.AbDtBase#getSubjectKey()} のためのテスト・メソッド。
	 */
	public void testGetSubjectKey() {
		String[] key = createBaseKeys(NORMAL_BASEKEY_A, NORMAL_BASEKEY_B, NORMAL_BASEKEY_C, NORMAL_BASEKEY_D);
		DtBasePattern base = new DtBasePattern(NORMAL_BASEKEY_A, NORMAL_BASEKEY_B, NORMAL_BASEKEY_C, NORMAL_BASEKEY_D);
		assertTrue(Arrays.equals(base._baseKeys, key));
		assertFalse(key[0].equals(base.getSubjectKey()));
		assertFalse(key[1].equals(base.getSubjectKey()));
		assertFalse(key[2].equals(base.getSubjectKey()));
		assertTrue (key[3].equals(base.getSubjectKey()));
	}

	/**
	 * {@link dtalge.AbDtBase#key()} のためのテスト・メソッド。
	 */
	public void testKey() {
		for (String[] keypair : normalOneStrings) {
			String ckey = keypair[0];
			String nkey = keypair[1];
			DtBasePattern base = new DtBasePattern(ckey);
			assertEquals("nkey[" + nkey + "] != base[" + base.key() + "]", nkey, base.key());
		}
	}

	/**
	 * {@link dtalge.AbDtBase#equals(java.lang.Object)} のためのテスト・メソッド。
	 */
	public void testEqualsObject() {
		//--- make data
		DtBasePattern[] bases1 = new DtBasePattern[normalKeys.length];
		DtBasePattern[] bases2 = new DtBasePattern[normalKeys.length];
		for (int i = 0; i < normalKeys.length; i++) {
			String[] ckeys = normalKeys[i][0];
			String[] nkeys = normalKeys[i][1];
			bases1[i] = new DtBasePattern(ckeys);
			bases2[i] = new DtBasePattern(ckeys);
			assertTrue(Arrays.equals(nkeys, bases1[i]._baseKeys));
			assertTrue(Arrays.equals(nkeys, bases2[i]._baseKeys));
			assertNotSame(bases1[i], bases2[i]);
		}
		
		//--- check
		for (int i = 0; i < bases1.length; i++) {
			for (int j = 0; j < bases2.length; j++) {
				DtBasePattern base1 = bases1[i];
				DtBasePattern base2 = bases2[j];
				DtBasePattern base3 = bases1[i];
				if (Arrays.equals(base1._baseKeys, base2._baseKeys)) {
					assertTrue(String.format("base1%s != base2%s",
							Arrays.toString(base1._baseKeys), Arrays.toString(base2._baseKeys)),
							base1.equals(base2));
					assertTrue(String.format("base1%s != base2%s",
							Arrays.toString(base1._baseKeys), Arrays.toString(base3._baseKeys)),
							base1.equals(base3));
				} else {
					assertFalse(String.format("base1%s == base2%s",
							Arrays.toString(base1._baseKeys), Arrays.toString(base2._baseKeys)),
							base1.equals(base2));
				}
			}
		}
	}

	/**
	 * {@link dtalge.AbDtBase#toString()} のためのテスト・メソッド。
	 */
	public void testToString() {
		DtBasePattern base1 = new DtBasePattern(DtBase.OMITTED, DT_BOOLEAN_W);
		DtBasePattern base2 = new DtBasePattern(NORMAL_BASEKEY_A, DT_INTEGER_M, NORMAL_BASEKEY_B, NORMAL_BASEKEY_C);
		DtBasePattern base3 = new DtBasePattern(NORMAL_BASEKEY_MARK, DT_STRING_W, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI);
		DtBasePattern base4 = new DtBasePattern(REGEXP_BASEKEY_1, DT_DECIMAL_W, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3);
		String strFormat = "<%s,%s,%s,%s>";
		String tostr1 = String.format(strFormat, DtBase.OMITTED, DT_BOOLEAN_W, DtBasePattern.WILDCARD, DtBasePattern.WILDCARD);
		String tostr2 = String.format(strFormat, NORMAL_BASEKEY_A, DT_INTEGER_M, NORMAL_BASEKEY_B, NORMAL_BASEKEY_C);
		String tostr3 = String.format(strFormat, NORMAL_BASEKEY_MARK, DT_STRING_W, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI);
		String tostr4 = String.format(strFormat, REGEXP_BASEKEY_1, DT_DECIMAL_W, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3);
		
		assertEquals(tostr1, base1.toString());
		assertEquals(tostr2, base2.toString());
		assertEquals(tostr3, base3.toString());
		assertEquals(tostr4, base4.toString());
	}
}
