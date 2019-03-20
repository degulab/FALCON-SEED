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
 * @(#)DtBaseTest.java	0.20	2010/02/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtBaseTest.java	0.10	2008/08/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge;

import java.util.Arrays;

import junit.framework.TestCase;

/**
 * <code>DtBase</code> のユニットテスト。
 * このテストケースでは、ファイル入出力のテストは含まない。
 * 
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class DtBaseTest extends TestCase
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final String NORMAL_BASEKEY_1 = "基底キー1";
	static public final String NORMAL_BASEKEY_2 = "基底キー2";
	static public final String NORMAL_BASEKEY_3 = "基底キー3";
	static public final String NORMAL_BASEKEY_4 = "基底キー4";
	static public final String NORMAL_BASEKEY_5 = "基底キー5";
	//static public final String NORMAL_BASEKEY_MARK = "基底 !\"#$%&'()=-=^~\\|@`[{;+:*]},<.>/?_";
	static public final String NORMAL_BASEKEY_MARK = "基底!#$()=~\\`[{;+:*]}./_";
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
	static public final String ILLEGAL_BASEKEY_ALL  = "全部 \t\n\u000B\f\r<>-,^%&?|@\'\"含む";
	
	static public final String DT_BOOLEAN_L = "boolean";
	static public final String DT_BOOLEAN_U = "BOOLEAN";
	static public final String DT_BOOLEAN_M = "bOoLeAn";
	//static public final String DT_INTEGER_L = "integer";
	//static public final String DT_INTEGER_U = "INTEGER";
	//static public final String DT_INTEGER_M = "InTeGeR";
	static public final String DT_DECIMAL_L = "decimal";
	static public final String DT_DECIMAL_U = "DECIMAL";
	static public final String DT_DECIMAL_M = "dEcImAl";
	static public final String DT_STRING_L = "string";
	static public final String DT_STRING_U = "STRING";
	static public final String DT_STRING_M = "StRiNg";
	
	static protected final String[] illegalOneStrings = new String[]{
			createOneKeyString((String[])null),
			//createOneKeyString(null, DT_INTEGER_M),
			//createOneKeyString(null, DT_INTEGER_M, NORMAL_BASEKEY_3),
			//createOneKeyString(null, DT_INTEGER_M, NORMAL_BASEKEY_3, NORMAL_BASEKEY_4),
			createOneKeyString(null, DT_DECIMAL_M),
			createOneKeyString(null, DT_DECIMAL_M, NORMAL_BASEKEY_3),
			createOneKeyString(null, DT_DECIMAL_M, NORMAL_BASEKEY_3, NORMAL_BASEKEY_4),
			createOneKeyString(DtBase.OMITTED),
			createOneKeyString(DtBase.OMITTED, DtBase.OMITTED),
			createOneKeyString(DtBase.OMITTED, DtBase.OMITTED, DtBase.OMITTED),
			createOneKeyString(DtBase.OMITTED, DtBase.OMITTED, DtBase.OMITTED, DtBase.OMITTED),
			//createOneKeyString(ILLEGAL_BASEKEY_ALL, DT_INTEGER_M),
			createOneKeyString(ILLEGAL_BASEKEY_ALL, DT_DECIMAL_M),
			createOneKeyString(NORMAL_BASEKEY_1, DT_STRING_M, ILLEGAL_BASEKEY_ALL),
			createOneKeyString(NORMAL_BASEKEY_1, DT_STRING_M, NORMAL_BASEKEY_3, ILLEGAL_BASEKEY_ALL),
	};
	
	static protected final String[][] normalOneStrings = new String[][]{
			createOneKeyStringPair(DtBase.OMITTED, DT_BOOLEAN_M),
			//createOneKeyStringPair(DtBase.OMITTED, DT_INTEGER_M, DtBase.OMITTED),
			createOneKeyStringPair(DtBase.OMITTED, DT_DECIMAL_M, DtBase.OMITTED),
			createOneKeyStringPair(DtBase.OMITTED, DT_DECIMAL_M, DtBase.OMITTED, DtBase.OMITTED),
			createOneKeyStringPair(DtBase.OMITTED, DT_STRING_M, DtBase.OMITTED, DtBase.OMITTED, DtBase.OMITTED),
			createOneKeyStringPair(NORMAL_BASEKEY_1, DT_BOOLEAN_M),
			//createOneKeyStringPair(NORMAL_BASEKEY_1, DT_INTEGER_M, NORMAL_BASEKEY_2),
			createOneKeyStringPair(NORMAL_BASEKEY_1, DT_DECIMAL_M, NORMAL_BASEKEY_2),
			createOneKeyStringPair(NORMAL_BASEKEY_1, DT_DECIMAL_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
			createOneKeyStringPair(NORMAL_BASEKEY_5, DT_STRING_M, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2, NORMAL_BASEKEY_1),
			createOneKeyStringPair(NORMAL_BASEKEY_MARK, DT_STRING_U, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
			//createOneKeyStringPair(REGEXP_BASEKEY_1, DT_INTEGER_U, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
			createOneKeyStringPair(REGEXP_BASEKEY_1, DT_DECIMAL_U, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
	};
	
	static public final String[][] illegalKeys = new String[][]{
		((String[])null),
		//new String[]{null, DT_INTEGER_M},
		//new String[]{null, DT_INTEGER_M, NORMAL_BASEKEY_3},
		//new String[]{null, DT_INTEGER_M, NORMAL_BASEKEY_3, NORMAL_BASEKEY_4},
		new String[]{null, DT_DECIMAL_M},
		new String[]{null, DT_DECIMAL_M, NORMAL_BASEKEY_3},
		new String[]{null, DT_DECIMAL_M, NORMAL_BASEKEY_3, NORMAL_BASEKEY_4},
		new String[]{DtBase.OMITTED},
		new String[]{DtBase.OMITTED, DtBase.OMITTED},
		new String[]{DtBase.OMITTED, DtBase.OMITTED, DtBase.OMITTED},
		new String[]{DtBase.OMITTED, DtBase.OMITTED, DtBase.OMITTED, DtBase.OMITTED},
		//new String[]{ILLEGAL_BASEKEY_ALL, DT_INTEGER_M},
		new String[]{ILLEGAL_BASEKEY_ALL, DT_DECIMAL_M},
		new String[]{NORMAL_BASEKEY_1, DT_STRING_M, ILLEGAL_BASEKEY_ALL},
		new String[]{NORMAL_BASEKEY_1, DT_STRING_M, NORMAL_BASEKEY_3, ILLEGAL_BASEKEY_ALL},
	};
	
	static public final String[][][] normalKeys = new String[][][]{
		createBaseKeysPair(DtBase.OMITTED, DT_BOOLEAN_M),
		//createBaseKeysPair(DtBase.OMITTED, DT_INTEGER_M),
		createBaseKeysPair(DtBase.OMITTED, DT_DECIMAL_M),
		createBaseKeysPair(DtBase.OMITTED, DT_STRING_M),
		createBaseKeysPair(DtBase.OMITTED, DT_BOOLEAN_M, DtBase.OMITTED),
		//createBaseKeysPair(DtBase.OMITTED, DT_INTEGER_M, DtBase.OMITTED),
		createBaseKeysPair(DtBase.OMITTED, DT_DECIMAL_M, DtBase.OMITTED),
		createBaseKeysPair(DtBase.OMITTED, DT_STRING_M, DtBase.OMITTED),
		createBaseKeysPair(DtBase.OMITTED, DT_BOOLEAN_M, DtBase.OMITTED, DtBase.OMITTED),
		//createBaseKeysPair(DtBase.OMITTED, DT_INTEGER_M, DtBase.OMITTED, DtBase.OMITTED),
		createBaseKeysPair(DtBase.OMITTED, DT_DECIMAL_M, DtBase.OMITTED, DtBase.OMITTED),
		createBaseKeysPair(DtBase.OMITTED, DT_STRING_M, DtBase.OMITTED, DtBase.OMITTED),
		createBaseKeysPair(DtBase.OMITTED, DT_BOOLEAN_M, DtBase.OMITTED, DtBase.OMITTED, DtBase.OMITTED),
		//createBaseKeysPair(DtBase.OMITTED, DT_INTEGER_M, DtBase.OMITTED, DtBase.OMITTED, DtBase.OMITTED),
		createBaseKeysPair(DtBase.OMITTED, DT_DECIMAL_M, DtBase.OMITTED, DtBase.OMITTED, DtBase.OMITTED),
		createBaseKeysPair(DtBase.OMITTED, DT_STRING_M, DtBase.OMITTED, DtBase.OMITTED, DtBase.OMITTED),
		createBaseKeysPair(NORMAL_BASEKEY_1, DT_BOOLEAN_M),
		//createBaseKeysPair(NORMAL_BASEKEY_1, DT_INTEGER_M),
		createBaseKeysPair(NORMAL_BASEKEY_1, DT_DECIMAL_M),
		createBaseKeysPair(NORMAL_BASEKEY_1, DT_STRING_M),
		createBaseKeysPair(NORMAL_BASEKEY_1, DT_BOOLEAN_M, NORMAL_BASEKEY_2),
		//createBaseKeysPair(NORMAL_BASEKEY_1, DT_INTEGER_M, NORMAL_BASEKEY_2),
		createBaseKeysPair(NORMAL_BASEKEY_1, DT_DECIMAL_M, NORMAL_BASEKEY_2),
		createBaseKeysPair(NORMAL_BASEKEY_1, DT_STRING_M, NORMAL_BASEKEY_2),
		createBaseKeysPair(NORMAL_BASEKEY_1, DT_BOOLEAN_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		//createBaseKeysPair(NORMAL_BASEKEY_1, DT_INTEGER_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		createBaseKeysPair(NORMAL_BASEKEY_1, DT_DECIMAL_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		createBaseKeysPair(NORMAL_BASEKEY_1, DT_STRING_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		createBaseKeysPair(NORMAL_BASEKEY_5, DT_BOOLEAN_M, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2, NORMAL_BASEKEY_1),
		//createBaseKeysPair(NORMAL_BASEKEY_5, DT_INTEGER_M, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2, NORMAL_BASEKEY_1),
		createBaseKeysPair(NORMAL_BASEKEY_5, DT_DECIMAL_M, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2, NORMAL_BASEKEY_1),
		createBaseKeysPair(NORMAL_BASEKEY_5, DT_STRING_M, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2, NORMAL_BASEKEY_1),
		createBaseKeysPair(NORMAL_BASEKEY_MARK, DT_BOOLEAN_U, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		//createBaseKeysPair(NORMAL_BASEKEY_MARK, DT_INTEGER_U, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		createBaseKeysPair(NORMAL_BASEKEY_MARK, DT_DECIMAL_U, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		createBaseKeysPair(NORMAL_BASEKEY_MARK, DT_STRING_U, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		createBaseKeysPair(REGEXP_BASEKEY_1, DT_BOOLEAN_U, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		//createBaseKeysPair(REGEXP_BASEKEY_1, DT_INTEGER_U, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		createBaseKeysPair(REGEXP_BASEKEY_1, DT_DECIMAL_U, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		createBaseKeysPair(REGEXP_BASEKEY_1, DT_STRING_U, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
	};

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

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
		Arrays.fill(nkeys, DtBase.OMITTED);
		int lim = Math.min(ckeys.length, nkeys.length);
		for (int i = 0; i < lim; i++) {
			if (ckeys[i] != null && ckeys[i].length() > 0) {
				if (i == 1)
					nkeys[i] = ckeys[i].toLowerCase();
				else
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
		String nameKey = DtBase.OMITTED;
		String typeKey = DtBase.OMITTED;
		String attrKey = DtBase.OMITTED;
		String subjectKey = DtBase.OMITTED;
		
		if (keys != null) {
			if (keys.length > 0 && keys[0] != null && keys[0].length() > 0)
				nameKey = keys[0];
			if (keys.length > 1 && keys[1] != null && keys[1].length() > 0)
				typeKey = keys[1].toLowerCase();
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
	 * {@link dtalge.DtBase#newBase(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewBaseString() {
		// キャッシュなし
		dtalge.util.DtConditions.setEnableCachedString(false);
		dtalge.util.DtConditions.setEnableCachedBase(false);
		//--- errors
		for (String keys : illegalOneStrings) {
			try {
				DtBase base = DtBase.newBase(keys);
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
				DtBase base = DtBase.newBase(ckey);
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
				DtBase base = DtBase.newBase(keys);
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
				DtBase base = DtBase.newBase(ckey);
				assertEquals("nkey(" + nkey + ") != basekey(" + base.key() + ")", nkey, base.key());
			}
			catch (Exception ex) {
				fail("[new DtBase(\"" + String.valueOf(ckey) + "\")] : " + ex.getMessage());
			}
		}
	}

	/**
	 * {@link dtalge.DtBase#newBase(java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewBaseStringString() {
		// キャッシュなし
		dtalge.util.DtConditions.setEnableCachedString(false);
		dtalge.util.DtConditions.setEnableCachedBase(false);
		//--- errors
		for (String[] keys : illegalKeys) {
			if (keys != null && keys.length == 2) {
				try {
					DtBase base = DtBase.newBase(keys[0], keys[1]);
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
					DtBase base = DtBase.newBase(ckeys[0], ckeys[1]);
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
					DtBase base = DtBase.newBase(keys[0], keys[1]);
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
					DtBase base = DtBase.newBase(ckeys[0], ckeys[1]);
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
	 * {@link dtalge.DtBase#newBase(java.lang.String, java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewBaseStringStringString() {
		// キャッシュなし
		dtalge.util.DtConditions.setEnableCachedString(false);
		dtalge.util.DtConditions.setEnableCachedBase(false);
		//--- errors
		for (String[] keys : illegalKeys) {
			if (keys != null && keys.length == 3) {
				try {
					DtBase base = DtBase.newBase(keys[0], keys[1], keys[2]);
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
					DtBase base = DtBase.newBase(ckeys[0], ckeys[1], ckeys[2]);
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
					DtBase base = DtBase.newBase(keys[0], keys[1], keys[2]);
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
					DtBase base = DtBase.newBase(ckeys[0], ckeys[1], ckeys[2]);
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
	 * {@link dtalge.DtBase#newBase(java.lang.String, java.lang.String, java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testNewBaseStringStringStringString() {
		// キャッシュなし
		dtalge.util.DtConditions.setEnableCachedString(false);
		dtalge.util.DtConditions.setEnableCachedBase(false);
		//--- errors
		for (String[] keys : illegalKeys) {
			if (keys != null && keys.length == 4) {
				try {
					DtBase base = DtBase.newBase(keys[0], keys[1], keys[2], keys[3]);
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
					DtBase base = DtBase.newBase(ckeys[0], ckeys[1], ckeys[2], ckeys[3]);
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
					DtBase base = DtBase.newBase(keys[0], keys[1], keys[2], keys[3]);
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
					DtBase base = DtBase.newBase(ckeys[0], ckeys[1], ckeys[2], ckeys[3]);
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
	 * {@link dtalge.DtBase#newBase(java.lang.String[])} のためのテスト・メソッド。
	 */
	public void testNewBaseStringArray() {
		// キャッシュなし
		dtalge.util.DtConditions.setEnableCachedString(false);
		dtalge.util.DtConditions.setEnableCachedBase(false);
		//--- errors
		for (String[] keys : illegalKeys) {
			try {
				DtBase base = DtBase.newBase(keys);
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
				DtBase base = DtBase.newBase(ckeys);
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
				DtBase base = DtBase.newBase(keys);
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
				DtBase base = DtBase.newBase(ckeys);
				assertTrue("nkeys(" + Arrays.toString(nkeys) + " != basekey(" + Arrays.toString(base._baseKeys) + ")",
						Arrays.equals(nkeys, base._baseKeys));
			}
			catch (Exception ex) {
				fail("Unknown exception [" + Arrays.toString(ckeys) + "] : " + ex.getMessage());
			}
		}
	}

	/**
	 * {@link dtalge.DtBase#DtBase(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testDtBaseString() {
		//--- errors
		for (String keys : illegalOneStrings) {
			try {
				DtBase base = new DtBase(keys);
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
				DtBase base = new DtBase(ckey);
				assertEquals("nkey(" + nkey + ") != basekey(" + base.key() + ")", nkey, base.key());
			}
			catch (Exception ex) {
				fail("[new DtBase(\"" + String.valueOf(ckey) + "\")] : " + ex.getMessage());
			}
		}
	}

	/**
	 * {@link dtalge.DtBase#DtBase(java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testDtBaseStringString() {
		//--- errors
		for (String[] keys : illegalKeys) {
			if (keys != null && keys.length == 2) {
				try {
					DtBase base = new DtBase(keys[0], keys[1]);
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
					DtBase base = new DtBase(ckeys[0], ckeys[1]);
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
	 * {@link dtalge.DtBase#DtBase(java.lang.String, java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testDtBaseStringStringString() {
		//--- errors
		for (String[] keys : illegalKeys) {
			if (keys != null && keys.length == 3) {
				try {
					DtBase base = new DtBase(keys[0], keys[1], keys[2]);
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
					DtBase base = new DtBase(ckeys[0], ckeys[1], ckeys[2]);
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
	 * {@link dtalge.DtBase#DtBase(java.lang.String, java.lang.String, java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testDtBaseStringStringStringString() {
		//--- errors
		for (String[] keys : illegalKeys) {
			if (keys != null && keys.length == 4) {
				try {
					DtBase base = new DtBase(keys[0], keys[1], keys[2], keys[3]);
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
					DtBase base = new DtBase(ckeys[0], ckeys[1], ckeys[2], ckeys[3]);
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
	 * {@link dtalge.DtBase#DtBase(java.lang.String[])} のためのテスト・メソッド。
	 */
	public void testDtBaseStringArray() {
		//--- errors
		for (String[] keys : illegalKeys) {
			try {
				DtBase base = new DtBase(keys);
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
				DtBase base = new DtBase(ckeys);
				assertTrue("nkeys(" + Arrays.toString(nkeys) + " != basekey(" + Arrays.toString(base._baseKeys) + ")",
						Arrays.equals(nkeys, base._baseKeys));
			}
			catch (Exception ex) {
				fail("Unknown exception [" + Arrays.toString(ckeys) + "] : " + ex.getMessage());
			}
		}
	}

	/**
	 * {@link dtalge.DtBase#compareTo(dtalge.DtBase)} のためのテスト・メソッド。
	 */
	public void testCompareTo() {
		//DtBase base1 = new DtBase(DT_BOOLEAN_M, DT_INTEGER_M, DT_DECIMAL_M, DT_STRING_M);
		//DtBase base2 = new DtBase(DT_BOOLEAN_L, DT_INTEGER_L, DT_DECIMAL_L, DT_STRING_L);
		//DtBase base3 = new DtBase(DT_BOOLEAN_U, DT_INTEGER_U, DT_DECIMAL_U, DT_STRING_U);
		//DtBase base4 = new DtBase(DT_BOOLEAN_M, DT_INTEGER_M, DT_DECIMAL_M, DT_STRING_M);
		//DtBase base5 = new DtBase(DT_BOOLEAN_L, DT_INTEGER_L, DT_DECIMAL_L, DT_STRING_L);
		//DtBase base6 = new DtBase(DT_BOOLEAN_U, DT_INTEGER_U, DT_DECIMAL_U, DT_STRING_U);
		//DtBase baseN = new DtBase(DT_BOOLEAN_M, DT_INTEGER_M, DT_DECIMAL_M, NORMAL_BASEKEY_1);
		DtBase base1 = new DtBase(DT_BOOLEAN_M, DT_DECIMAL_M, DT_DECIMAL_M, DT_STRING_M);
		DtBase base2 = new DtBase(DT_BOOLEAN_L, DT_DECIMAL_L, DT_DECIMAL_L, DT_STRING_L);
		DtBase base3 = new DtBase(DT_BOOLEAN_U, DT_DECIMAL_U, DT_DECIMAL_U, DT_STRING_U);
		DtBase base4 = new DtBase(DT_BOOLEAN_M, DT_DECIMAL_M, DT_DECIMAL_M, DT_STRING_M);
		DtBase base5 = new DtBase(DT_BOOLEAN_L, DT_DECIMAL_L, DT_DECIMAL_L, DT_STRING_L);
		DtBase base6 = new DtBase(DT_BOOLEAN_U, DT_DECIMAL_U, DT_DECIMAL_U, DT_STRING_U);
		DtBase baseN = new DtBase(DT_BOOLEAN_M, DT_DECIMAL_M, DT_DECIMAL_M, NORMAL_BASEKEY_1);
		
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
	 * {@link dtalge.DtBase#compareToIgnoreCase(dtalge.DtBase)} のためのテスト・メソッド。
	 */
	public void testCompareToIgnoreCase() {
		//DtBase base1 = new DtBase(DT_BOOLEAN_M, DT_INTEGER_M, DT_DECIMAL_M, DT_STRING_M);
		//DtBase base2 = new DtBase(DT_BOOLEAN_L, DT_INTEGER_L, DT_DECIMAL_L, DT_STRING_L);
		//DtBase base3 = new DtBase(DT_BOOLEAN_U, DT_INTEGER_U, DT_DECIMAL_U, DT_STRING_U);
		//DtBase baseN = new DtBase(DT_BOOLEAN_M, DT_INTEGER_M, DT_DECIMAL_M, NORMAL_BASEKEY_1);
		DtBase base1 = new DtBase(DT_BOOLEAN_M, DT_DECIMAL_M, DT_DECIMAL_M, DT_STRING_M);
		DtBase base2 = new DtBase(DT_BOOLEAN_L, DT_DECIMAL_L, DT_DECIMAL_L, DT_STRING_L);
		DtBase base3 = new DtBase(DT_BOOLEAN_U, DT_DECIMAL_U, DT_DECIMAL_U, DT_STRING_U);
		DtBase baseN = new DtBase(DT_BOOLEAN_M, DT_DECIMAL_M, DT_DECIMAL_M, NORMAL_BASEKEY_1);
		
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
		String[][] keys1 = createBaseKeysPair(NORMAL_BASEKEY_1, DT_STRING_M, NORMAL_BASEKEY_3, NORMAL_BASEKEY_4);
		DtBase base1 = new DtBase(keys1[0]);
		assertTrue(Arrays.equals(base1._baseKeys, keys1[1]));
		assertEquals(Arrays.hashCode(keys1[1]), base1.hashCode());
		//---
		//String[][] keys2 = createBaseKeysPair(NORMAL_BASEKEY_1, DT_INTEGER_M);
		String[][] keys2 = createBaseKeysPair(NORMAL_BASEKEY_1, DT_DECIMAL_M);
		DtBase base2 = new DtBase(keys2[0]);
		assertTrue(Arrays.equals(base2._baseKeys, keys2[1]));
		assertEquals(Arrays.hashCode(keys2[1]), base2.hashCode());
		//---
		String[][] keys3 = createBaseKeysPair(DtBase.OMITTED, DT_DECIMAL_M, DtBase.OMITTED, NORMAL_BASEKEY_4);
		DtBase base3 = new DtBase(keys3[0]);
		assertTrue(Arrays.equals(base3._baseKeys, keys3[1]));
		assertEquals(Arrays.hashCode(keys3[1]), base3.hashCode());
		//---
		DtBase b1 = new DtBase(NORMAL_BASEKEY_MARK, DT_STRING_M, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI);
		DtBase b2 = new DtBase(NORMAL_BASEKEY_MARK, DT_STRING_M, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI);
		DtBase b3 = new DtBase(NORMAL_BASEKEY_MARK, DT_STRING_M, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_5);
		assertTrue(b1.hashCode() == b2.hashCode());
		assertTrue(b1.hashCode() != b3.hashCode());
	}

	/**
	 * {@link dtalge.AbDtBase#getNumAllKeys()} のためのテスト・メソッド。
	 */
	public void testGetNumAllKeys() {
		assertEquals(4, DtBase.getNumAllKeys());
	}

	/**
	 * {@link dtalge.AbDtBase#getNameKey()} のためのテスト・メソッド。
	 */
	public void testGetNameKey() {
		String[] key = createBaseKeys(NORMAL_BASEKEY_1, DT_BOOLEAN_L, NORMAL_BASEKEY_3, NORMAL_BASEKEY_4);
		DtBase base = new DtBase(NORMAL_BASEKEY_1, DT_BOOLEAN_M, NORMAL_BASEKEY_3, NORMAL_BASEKEY_4);
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
		String[] key = createBaseKeys(NORMAL_BASEKEY_1, DT_BOOLEAN_L, NORMAL_BASEKEY_3, NORMAL_BASEKEY_4);
		DtBase base = new DtBase(NORMAL_BASEKEY_1, DT_BOOLEAN_M, NORMAL_BASEKEY_3, NORMAL_BASEKEY_4);
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
		String[] key = createBaseKeys(NORMAL_BASEKEY_1, DT_BOOLEAN_L, NORMAL_BASEKEY_3, NORMAL_BASEKEY_4);
		DtBase base = new DtBase(NORMAL_BASEKEY_1, DT_BOOLEAN_M, NORMAL_BASEKEY_3, NORMAL_BASEKEY_4);
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
		String[] key = createBaseKeys(NORMAL_BASEKEY_1, DT_BOOLEAN_L, NORMAL_BASEKEY_3, NORMAL_BASEKEY_4);
		DtBase base = new DtBase(NORMAL_BASEKEY_1, DT_BOOLEAN_M, NORMAL_BASEKEY_3, NORMAL_BASEKEY_4);
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
			DtBase base = new DtBase(ckey);
			assertEquals("nkey[" + nkey + "] != base[" + base.key() + "]", nkey, base.key());
		}
	}

	/**
	 * {@link dtalge.AbDtBase#equals(java.lang.Object)} のためのテスト・メソッド。
	 */
	public void testEqualsObject() {
		//--- make data
		DtBase[] bases1 = new DtBase[normalKeys.length];
		DtBase[] bases2 = new DtBase[normalKeys.length];
		for (int i = 0; i < normalKeys.length; i++) {
			String[] ckeys = normalKeys[i][0];
			String[] nkeys = normalKeys[i][1];
			bases1[i] = new DtBase(ckeys);
			bases2[i] = new DtBase(ckeys);
			assertTrue(Arrays.equals(nkeys, bases1[i]._baseKeys));
			assertTrue(Arrays.equals(nkeys, bases2[i]._baseKeys));
			assertNotSame(bases1[i], bases2[i]);
		}
		
		//--- check
		for (int i = 0; i < bases1.length; i++) {
			for (int j = 0; j < bases2.length; j++) {
				DtBase base1 = bases1[i];
				DtBase base2 = bases2[j];
				DtBase base3 = bases1[i];
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
		DtBase base1 = new DtBase(DtBase.OMITTED, DT_BOOLEAN_M);
		//DtBase base2 = new DtBase(NORMAL_BASEKEY_1, DT_INTEGER_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3);
		DtBase base2 = new DtBase(NORMAL_BASEKEY_1, DT_BOOLEAN_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3);
		DtBase base3 = new DtBase(NORMAL_BASEKEY_MARK, DT_STRING_M, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI);
		DtBase base4 = new DtBase(NORMAL_BASEKEY_1, DT_DECIMAL_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3);
		String strFormat = "<%s,%s,%s,%s>";
		String tostr1 = String.format(strFormat, DtBase.OMITTED, DT_BOOLEAN_L, DtBase.OMITTED, DtBase.OMITTED);
		//String tostr2 = String.format(strFormat, NORMAL_BASEKEY_1, DT_INTEGER_L, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3);
		String tostr2 = String.format(strFormat, NORMAL_BASEKEY_1, DT_BOOLEAN_L, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3);
		String tostr3 = String.format(strFormat, NORMAL_BASEKEY_MARK, DT_STRING_L, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI);
		String tostr4 = String.format(strFormat, NORMAL_BASEKEY_1, DT_DECIMAL_L, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3);
		
		assertEquals(tostr1, base1.toString());
		assertEquals(tostr2, base2.toString());
		assertEquals(tostr3, base3.toString());
		assertEquals(tostr4, base4.toString());
	}

	/**
	 * {@link dtalge.AbDtBase#getKeyName(int)} のためのテスト・メソッド。
	 */
	public void testGetKeyName() {
		assertEquals("name key", DtBase.getKeyName(0));
		assertEquals("type key", DtBase.getKeyName(1));
		assertEquals("attribute key", DtBase.getKeyName(2));
		assertEquals("subject key", DtBase.getKeyName(3));
		
		for (int i = -10 ; i < 0; i++) {
			String data = "#" + (i+1) + " key";
			assertEquals(data, DtBase.getKeyName(i));
		}
		for (int i = 4; i <= 10; i++) {
			String data = "#" + (i+1) + " key";
			assertEquals(data, DtBase.getKeyName(i));
		}
	}
}
