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
 *  Copyright 2007-2009  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Li Hou(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
package exalge2;

import junit.framework.TestCase;

public class ExBasePatternTest extends TestCase {
	
	static private final String keyName = "名前";
	static private final String keyUnit = "単位";
	static private final String keyTime = "時間";
	static private final String keySubject = "サブジェクト";

	static private final String nohat____ = "*-NO_HAT-*-*-*";
	static private final String hat____ = "*-HAT-*-*-*";
	static private final String wc____ = "*-*-*-*-*";
	static private final String nohatN___ = keyName + "-NO_HAT-*-*-*";
	//static private final String hatN___ = keyName + "-HAT-*-*-*";
	static private final String wcN___ = keyName + "-*-*-*-*";
	//static private final String nohatNU__ = keyName + "-NO_HAT-" + keyUnit + "-*-*";
	static private final String hatNU__ = keyName + "-HAT-" + keyUnit + "-*-*";
	//static private final String wcNU__ = keyName + "-*-" + keyUnit + "-*-*";
	static private final String nohatNUT_ = keyName + "-NO_HAT-" + keyUnit + "-" + keyTime + "-*";
	//static private final String hatNUT_ = keyName + "-HAT-" + keyUnit + "-" + keyTime + "-*";
	//static private final String wcNUT_ = keyName + "-*-" + keyUnit + "-" + keyTime + "-*";
	static private final String nohatNUTS = keyName + "-NO_HAT-" + keyUnit + "-" + keyTime + "-" + keySubject;
	static private final String hatNUTS = keyName + "-HAT-" + keyUnit + "-" + keyTime + "-" + keySubject;
	static private final String wcNUTS = keyName + "-*-" + keyUnit + "-" + keyTime + "-" + keySubject;
	
	//static private final String nohatN_TS = keyName + "-NO_HAT-*-" + keyTime + "-" + keySubject;
	static private final String nohatNU_S = keyName + "-NO_HAT-" + keyUnit + "-*-" + keySubject;
	
	static private final String testIllegalChar = " \t\n\u000B\f\r<>-,^%&?|@'\"";
	static private final String testNormalChar = "!#$()=~\\`[{;+:*]}./\\_　予約";
	
	private String createOneKeyFromChar(int index, char c) {
		String[] keys = new String[]{"*", "*", "*", "*", "*"};
		String onekey = (index == 0 ? String.valueOf(c) : keys[0]);
		onekey = onekey + "-" + ExBasePattern.WILDCARD;
		for (int i = 2; i < ExBase.NUM_ALL_KEYS; i++) {
			onekey += "-";
			if (index == i) {
				onekey += String.valueOf(c);
			} else {
				onekey += keys[i];
			}
		}
		return onekey;
	}
	
	private String[] createKeyArrayFromChar(int index, char c) {
		String[] keys = new String[Math.max(index+1,ExBase.NUM_BASIC_KEYS)];
		for (int i = 0; i < keys.length; i++) {
			keys[i] = "*";
		}
		keys[ExBase.KEY_HAT] = ExBasePattern.WILDCARD;
		if (index != ExBase.KEY_HAT) {
			keys[index] = String.valueOf(c);
		}
		return keys;
	}
	
	private String[] createPatternArrayFromChar(int index, char c) {
		return createPatternArrayFromString(index, String.valueOf(c));
	}
	
	private String[] createPatternArrayFromString(int index, String str) {
		String[] keys = new String[Math.max(index+1,ExBase.NUM_BASIC_KEYS)];
		for (int i = 0; i < keys.length; i++) {
			keys[i] = "*";
		}
		keys[ExBase.KEY_HAT] = ExBasePattern.WILDCARD;
		if (index != ExBase.KEY_HAT) {
			keys[index] = "*" + str + "*";
		}
		return keys;
	}
	
	private String[] createExBasePatternArrayFromChar(int index, char c) {
		return createExBasePatternArrayFromString(index, String.valueOf(c));
	}
	
	private String[] createExBasePatternArrayFromString(int index, String str) {
		String[] keys = new String[ExBase.NUM_ALL_KEYS];
		for (int i = 0; i < keys.length; i++) {
			keys[i] = "*";
		}
		keys[ExBase.KEY_HAT] = ExBase.HAT;
		if (index != ExBase.KEY_HAT) {
			keys[index] = "*" + str + "*";
		}
		return keys;
		
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * {@link exalge2.ExBasePattern#ExBasePattern(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testExBasePatternString() {
		ExBasePattern test1 = new ExBasePattern(nohatNUTS);
		assertEquals(nohatNUTS, test1.key());
		
		ExBasePattern test2 = new ExBasePattern(hatNUTS);
		assertEquals(hatNUTS, test2.key());
		
		ExBasePattern test3 = new ExBasePattern("");
		assertEquals(wc____, test3.key());

		// Normal chars
		for (char c : testNormalChar.toCharArray()) {
			for (int i = 0; i < AbExBase.NUM_ALL_KEYS; i++) {
				if (i != ExBase.KEY_HAT) {
					String onekey = createOneKeyFromChar(i, c);
					try {
						ExBasePattern base = new ExBasePattern(onekey);
						assertEquals(base.key(), onekey);
					} catch (IllegalArgumentException ex) {
						fail("[" + onekey + "]" + ex.getMessage());
					}
				}
			}
		}
		// Illegal chars
		for (char c : testIllegalChar.toCharArray()) {
			for (int i = 0; i < AbExBase.NUM_ALL_KEYS; i++) {
				if (i != ExBase.KEY_HAT) {
					if (c == '-')	continue;	// 一意キーとしては無効なため除外
					String onekey = createOneKeyFromChar(i, c);
					try {
						ExBasePattern base = new ExBasePattern(onekey);
						fail("[" + onekey + "]:=" + base.toString());
					} catch (IllegalArgumentException ex) {
						String keyname = AbExBase.getKeyName(i);
						assertTrue(ex.getMessage().indexOf(keyname) >= 0);
					}
				}
			}
		}
		
		// 不正文字チェック
		String[] normalKeys = new String[]{"name", "*", "unit", "time", "subject"};
		//--- name
		{
			String[] illegalNameKeys = new String[]{
					"na me",//(空白)
					"na\tme",//(空白)
					"na\nme",//(空白)
					"na\u000Bme",//(空白)
					"na\fme",//(空白)
					"na\rme",//(空白)
					"na<me",//&lt;
					"na>me",//&gt;
					"na,me",//,
					"na^me",//^
					"na%me",//%
					"na&me",//&amp;
					"na?me",//?
					"na|me",//|
					"na@me",//@
					"na\'me",//'
					"na\"me",//&quot;"
			};
			for (String ik : illegalNameKeys) {
				boolean coughtException;
				try {
					new ExBasePattern(ik+'-'+normalKeys[1]+'-'+normalKeys[2]+'-'+normalKeys[3]+'-'+normalKeys[4]);
					coughtException = false;
				} catch (IllegalArgumentException ex) {
					coughtException = true;
				}
				assertTrue(coughtException);
			}
		}
		//--- hat
		{
			String[] illegalHatKeys = new String[]{
					"na me",//(空白)
					"na\tme",//(空白)
					"na\nme",//(空白)
					"na\u000Bme",//(空白)
					"na\fme",//(空白)
					"na\rme",//(空白)
					"na<me",//&lt;
					"na>me",//&gt;
					"na,me",//,
					"na^me",//^
					"na%me",//%
					"na&me",//&amp;
					"na?me",//?
					"na|me",//|
					"na@me",//@
					"na\'me",//'
					"na\"me",//&quot;"
					"HOT",
					"NO__HAT",
			};
			for (String ik : illegalHatKeys) {
				boolean coughtException;
				try {
					new ExBasePattern(normalKeys[0]+'-'+ik+'-'+normalKeys[2]+'-'+normalKeys[3]+'-'+normalKeys[4]);
					coughtException = false;
				} catch (IllegalArgumentException ex) {
					coughtException = true;
				}
				assertTrue(coughtException);
			}
		}
		//--- unit
		{
			String[] illegalUnitKeys = new String[]{
					"na me",//(空白)
					"na\tme",//(空白)
					"na\nme",//(空白)
					"na\u000Bme",//(空白)
					"na\fme",//(空白)
					"na\rme",//(空白)
					"na<me",//&lt;
					"na>me",//&gt;
					"na,me",//,
					"na^me",//^
					"na%me",//%
					"na&me",//&amp;
					"na?me",//?
					"na|me",//|
					"na@me",//@
					"na\'me",//'
					"na\"me",//&quot;"
			};
			for (String ik : illegalUnitKeys) {
				boolean coughtException;
				try {
					new ExBasePattern(normalKeys[0]+'-'+normalKeys[1]+'-'+ik+'-'+normalKeys[3]+'-'+normalKeys[4]);
					coughtException = false;
				} catch (IllegalArgumentException ex) {
					coughtException = true;
				}
				assertTrue(coughtException);
			}
		}
		//--- time
		{
			String[] illegalTimeKeys = new String[]{
					"na me",//(空白)
					"na\tme",//(空白)
					"na\nme",//(空白)
					"na\u000Bme",//(空白)
					"na\fme",//(空白)
					"na\rme",//(空白)
					"na<me",//&lt;
					"na>me",//&gt;
					"na,me",//,
					"na^me",//^
					"na%me",//%
					"na&me",//&amp;
					"na?me",//?
					"na|me",//|
					"na@me",//@
					"na\'me",//'
					"na\"me",//&quot;"
			};
			for (String ik : illegalTimeKeys) {
				boolean coughtException;
				try {
					new ExBasePattern(normalKeys[0]+'-'+normalKeys[1]+'-'+normalKeys[2]+'-'+ik+'-'+normalKeys[4]);
					coughtException = false;
				} catch (IllegalArgumentException ex) {
					coughtException = true;
				}
				assertTrue(coughtException);
			}
		}
		//--- subject
		{
			String[] illegalSubjectKeys = new String[]{
					"na me",//(空白)
					"na\tme",//(空白)
					"na\nme",//(空白)
					"na\u000Bme",//(空白)
					"na\fme",//(空白)
					"na\rme",//(空白)
					"na<me",//&lt;
					"na>me",//&gt;
					"na,me",//,
					"na^me",//^
					"na%me",//%
					"na&me",//&amp;
					"na?me",//?
					"na|me",//|
					"na@me",//@
					"na\'me",//'
					"na\"me",//&quot;"
			};
			for (String ik : illegalSubjectKeys) {
				boolean coughtException;
				try {
					new ExBasePattern(normalKeys[0]+'-'+normalKeys[1]+'-'+normalKeys[2]+'-'+normalKeys[3]+'-'+ik);
					coughtException = false;
				} catch (IllegalArgumentException ex) {
					coughtException = true;
				}
				assertTrue(coughtException);
			}
		}
	}

	/**
	 * {@link exalge2.ExBasePattern#ExBasePattern(java.lang.String[])} のためのテスト・メソッド。
	 */
	public void testExBasePatternStringArray() {
		final String[] testNohatNUTS = new String[]{keyName, "NO_HAT", keyUnit, keyTime, keySubject};
		final String[] testHatNUTS = new String[]{keyName, "HAT", keyUnit, keyTime, keySubject};
		final String[] testWcN___ = new String[]{keyName};
		
		ExBasePattern test1 = new ExBasePattern(testNohatNUTS);
		assertEquals(nohatNUTS, test1.key());
		
		ExBasePattern test2 = new ExBasePattern(testHatNUTS);
		assertEquals(hatNUTS, test2.key());
		
		ExBasePattern test3 = new ExBasePattern(testWcN___);
		assertEquals(wcN___, test3.key());

		// Normal chars
		for (char c : testNormalChar.toCharArray()) {
			for (int i = 0; i < AbExBase.NUM_ALL_KEYS; i++) {
				if (i != ExBase.KEY_HAT) {
					String onekey = createOneKeyFromChar(i, c);
					String[] keys = createKeyArrayFromChar(i, c);
					try {
						ExBasePattern base = new ExBasePattern(keys);
						assertEquals(base.key(), onekey);
					} catch (IllegalArgumentException ex) {
						fail("[" + onekey + "]" + ex.getMessage());
					}
				}
			}
		}
		// Illegal chars
		for (char c : testIllegalChar.toCharArray()) {
			for (int i = 0; i < AbExBase.NUM_ALL_KEYS; i++) {
				if (i != ExBase.KEY_HAT) {
					String[] keys = createKeyArrayFromChar(i, c);
					try {
						ExBasePattern base = new ExBasePattern(keys);
						fail(keys + ":=" + base.toString());
					} catch (IllegalArgumentException ex) {
						String keyname = AbExBase.getKeyName(i);
						assertTrue(ex.getMessage().indexOf(keyname) >= 0);
					}
				}
			}
		}
		
		// 不正文字チェック
		String[] normalKeys = new String[]{"name", "*", "unit", "time", "subject"};
		//--- name
		{
			String[] illegalNameKeys = new String[]{
					"na me",//(空白)
					"na\tme",//(空白)
					"na\nme",//(空白)
					"na\u000Bme",//(空白)
					"na\fme",//(空白)
					"na\rme",//(空白)
					"na<me",//&lt;
					"na>me",//&gt;
					"na-me",//-
					"na,me",//,
					"na^me",//^
					"na%me",//%
					"na&me",//&amp;
					"na?me",//?
					"na|me",//|
					"na@me",//@
					"na\'me",//'
					"na\"me",//&quot;"
			};
			for (String ik : illegalNameKeys) {
				boolean coughtException;
				try {
					new ExBasePattern(new String[]{ik, normalKeys[1], normalKeys[2], normalKeys[3], normalKeys[4]});
					coughtException = false;
				} catch (IllegalArgumentException ex) {
					coughtException = true;
				}
				assertTrue(coughtException);
			}
		}
		//--- hat
		{
			String[] illegalHatKeys = new String[]{
					"na me",//(空白)
					"na\tme",//(空白)
					"na\nme",//(空白)
					"na\u000Bme",//(空白)
					"na\fme",//(空白)
					"na\rme",//(空白)
					"na<me",//&lt;
					"na>me",//&gt;
					"na-me",//-
					"na,me",//,
					"na^me",//^
					"na%me",//%
					"na&me",//&amp;
					"na?me",//?
					"na|me",//|
					"na@me",//@
					"na\'me",//'
					"na\"me",//&quot;"
					"HOT",
					"NO__HAT",
			};
			for (String ik : illegalHatKeys) {
				boolean coughtException;
				try {
					new ExBasePattern(new String[]{normalKeys[0], ik, normalKeys[2], normalKeys[3], normalKeys[4]});
					coughtException = false;
				} catch (IllegalArgumentException ex) {
					coughtException = true;
				}
				assertTrue(coughtException);
			}
		}
		//--- unit
		{
			String[] illegalUnitKeys = new String[]{
					"na me",//(空白)
					"na\tme",//(空白)
					"na\nme",//(空白)
					"na\u000Bme",//(空白)
					"na\fme",//(空白)
					"na\rme",//(空白)
					"na<me",//&lt;
					"na>me",//&gt;
					"na-me",//-
					"na,me",//,
					"na^me",//^
					"na%me",//%
					"na&me",//&amp;
					"na?me",//?
					"na|me",//|
					"na@me",//@
					"na\'me",//'
					"na\"me",//&quot;"
			};
			for (String ik : illegalUnitKeys) {
				boolean coughtException;
				try {
					new ExBasePattern(new String[]{normalKeys[0], normalKeys[1], ik, normalKeys[3], normalKeys[4]});
					coughtException = false;
				} catch (IllegalArgumentException ex) {
					coughtException = true;
				}
				assertTrue(coughtException);
			}
		}
		//--- time
		{
			String[] illegalTimeKeys = new String[]{
					"na me",//(空白)
					"na\tme",//(空白)
					"na\nme",//(空白)
					"na\u000Bme",//(空白)
					"na\fme",//(空白)
					"na\rme",//(空白)
					"na<me",//&lt;
					"na>me",//&gt;
					"na-me",//-
					"na,me",//,
					"na^me",//^
					"na%me",//%
					"na&me",//&amp;
					"na?me",//?
					"na|me",//|
					"na@me",//@
					"na\'me",//'
					"na\"me",//&quot;"
			};
			for (String ik : illegalTimeKeys) {
				boolean coughtException;
				try {
					new ExBasePattern(new String[]{normalKeys[0], normalKeys[1], normalKeys[2], ik, normalKeys[4]});
					coughtException = false;
				} catch (IllegalArgumentException ex) {
					coughtException = true;
				}
				assertTrue(coughtException);
			}
		}
		//--- subject
		{
			String[] illegalSubjectKeys = new String[]{
					"na me",//(空白)
					"na\tme",//(空白)
					"na\nme",//(空白)
					"na\u000Bme",//(空白)
					"na\fme",//(空白)
					"na\rme",//(空白)
					"na<me",//&lt;
					"na>me",//&gt;
					"na-me",//-
					"na,me",//,
					"na^me",//^
					"na%me",//%
					"na&me",//&amp;
					"na?me",//?
					"na|me",//|
					"na@me",//@
					"na\'me",//'
					"na\"me",//&quot;"
			};
			for (String ik : illegalSubjectKeys) {
				boolean coughtException;
				try {
					new ExBasePattern(new String[]{normalKeys[0], normalKeys[1], normalKeys[2], normalKeys[3], ik});
					coughtException = false;
				} catch (IllegalArgumentException ex) {
					coughtException = true;
				}
				assertTrue(coughtException);
			}
		}
	}

	/**
	 * {@link exalge2.ExBasePattern#ExBasePattern(java.lang.String, java.lang.String, java.lang.String[])} のためのテスト・メソッド。
	 */
	public void testExBasePatternStringStringStringArray() {
		final String[] data0 = new String[0];
		final String[] data1 = new String[]{keyUnit};
		final String[] data2 = new String[]{keyUnit, keyTime, keySubject, keyName};
		
		ExBasePattern test1 = new ExBasePattern(keyName, ExBase.NO_HAT, data0);
		assertEquals(nohatN___, test1.key());
		
		ExBasePattern test2 = new ExBasePattern(keyName, ExBase.HAT, data1);
		assertEquals(hatNU__, test2.key());

		final String wc_UTS = "*-*-" + keyUnit + "-" + keyTime + "-" + keySubject;
		ExBasePattern test3 = new ExBasePattern(null, "", data2);
		assertEquals(wc_UTS, test3.key());
		
		// 不正文字チェック
		String[] normalKeys = new String[]{"name", "*", "unit", "time", "subject"};
		//--- name
		{
			String[] illegalNameKeys = new String[]{
					"na me",//(空白)
					"na\tme",//(空白)
					"na\nme",//(空白)
					"na\u000Bme",//(空白)
					"na\fme",//(空白)
					"na\rme",//(空白)
					"na<me",//&lt;
					"na>me",//&gt;
					"na-me",//-
					"na,me",//,
					"na^me",//^
					"na%me",//%
					"na&me",//&amp;
					"na?me",//?
					"na|me",//|
					"na@me",//@
					"na\'me",//'
					"na\"me",//&quot;"
			};
			for (String ik : illegalNameKeys) {
				boolean coughtException;
				try {
					new ExBasePattern(ik, normalKeys[1], new String[]{normalKeys[2], normalKeys[3], normalKeys[4]});
					coughtException = false;
				} catch (IllegalArgumentException ex) {
					coughtException = true;
				}
				assertTrue(coughtException);
			}
		}
		//--- hat
		{
			String[] illegalHatKeys = new String[]{
					"na me",//(空白)
					"na\tme",//(空白)
					"na\nme",//(空白)
					"na\u000Bme",//(空白)
					"na\fme",//(空白)
					"na\rme",//(空白)
					"na<me",//&lt;
					"na>me",//&gt;
					"na-me",//-
					"na,me",//,
					"na^me",//^
					"na%me",//%
					"na&me",//&amp;
					"na?me",//?
					"na|me",//|
					"na@me",//@
					"na\'me",//'
					"na\"me",//&quot;"
					"HOT",
					"NO__HAT",
			};
			for (String ik : illegalHatKeys) {
				boolean coughtException;
				try {
					new ExBasePattern(normalKeys[0], ik, new String[]{normalKeys[2], normalKeys[3], normalKeys[4]});
					coughtException = false;
				} catch (IllegalArgumentException ex) {
					coughtException = true;
				}
				assertTrue(coughtException);
			}
		}
		//--- unit
		{
			String[] illegalUnitKeys = new String[]{
					"na me",//(空白)
					"na\tme",//(空白)
					"na\nme",//(空白)
					"na\u000Bme",//(空白)
					"na\fme",//(空白)
					"na\rme",//(空白)
					"na<me",//&lt;
					"na>me",//&gt;
					"na-me",//-
					"na,me",//,
					"na^me",//^
					"na%me",//%
					"na&me",//&amp;
					"na?me",//?
					"na|me",//|
					"na@me",//@
					"na\'me",//'
					"na\"me",//&quot;"
			};
			for (String ik : illegalUnitKeys) {
				boolean coughtException;
				try {
					new ExBasePattern(normalKeys[0], normalKeys[1], new String[]{ik, normalKeys[3], normalKeys[4]});
					coughtException = false;
				} catch (IllegalArgumentException ex) {
					coughtException = true;
				}
				assertTrue(coughtException);
			}
		}
		//--- time
		{
			String[] illegalTimeKeys = new String[]{
					"na me",//(空白)
					"na\tme",//(空白)
					"na\nme",//(空白)
					"na\u000Bme",//(空白)
					"na\fme",//(空白)
					"na\rme",//(空白)
					"na<me",//&lt;
					"na>me",//&gt;
					"na-me",//-
					"na,me",//,
					"na^me",//^
					"na%me",//%
					"na&me",//&amp;
					"na?me",//?
					"na|me",//|
					"na@me",//@
					"na\'me",//'
					"na\"me",//&quot;"
			};
			for (String ik : illegalTimeKeys) {
				boolean coughtException;
				try {
					new ExBasePattern(normalKeys[0], normalKeys[1], new String[]{normalKeys[2], ik, normalKeys[4]});
					coughtException = false;
				} catch (IllegalArgumentException ex) {
					coughtException = true;
				}
				assertTrue(coughtException);
			}
		}
		//--- subject
		{
			String[] illegalSubjectKeys = new String[]{
					"na me",//(空白)
					"na\tme",//(空白)
					"na\nme",//(空白)
					"na\u000Bme",//(空白)
					"na\fme",//(空白)
					"na\rme",//(空白)
					"na<me",//&lt;
					"na>me",//&gt;
					"na-me",//-
					"na,me",//,
					"na^me",//^
					"na%me",//%
					"na&me",//&amp;
					"na?me",//?
					"na|me",//|
					"na@me",//@
					"na\'me",//'
					"na\"me",//&quot;"
			};
			for (String ik : illegalSubjectKeys) {
				boolean coughtException;
				try {
					new ExBasePattern(normalKeys[0], normalKeys[1], new String[]{normalKeys[2], normalKeys[3], ik});
					coughtException = false;
				} catch (IllegalArgumentException ex) {
					coughtException = true;
				}
				assertTrue(coughtException);
			}
		}
	}

	/**
	 * {@link exalge2.ExBasePattern#ExBasePattern(java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testExBasePatternStringString() {
		ExBasePattern test1 = new ExBasePattern(null, ExBase.NO_HAT);
		assertEquals(nohat____, test1.key());
		
		ExBasePattern test2 = new ExBasePattern("", ExBase.HAT);
		assertEquals(hat____, test2.key());
		
		ExBasePattern test3 = new ExBasePattern(keyName, null);
		assertEquals(wcN___, test3.key());
		
		// 不正文字チェック
		String[] normalKeys = new String[]{"name", "*", "unit", "time", "subject"};
		//--- name
		{
			String[] illegalNameKeys = new String[]{
					"na me",//(空白)
					"na\tme",//(空白)
					"na\nme",//(空白)
					"na\u000Bme",//(空白)
					"na\fme",//(空白)
					"na\rme",//(空白)
					"na<me",//&lt;
					"na>me",//&gt;
					"na-me",//-
					"na,me",//,
					"na^me",//^
					"na%me",//%
					"na&me",//&amp;
					"na?me",//?
					"na|me",//|
					"na@me",//@
					"na\'me",//'
					"na\"me",//&quot;"
			};
			for (String ik : illegalNameKeys) {
				boolean coughtException;
				try {
					new ExBasePattern(ik, normalKeys[1]);
					coughtException = false;
				} catch (IllegalArgumentException ex) {
					coughtException = true;
				}
				assertTrue(coughtException);
			}
		}
		//--- hat
		{
			String[] illegalHatKeys = new String[]{
					"na me",//(空白)
					"na\tme",//(空白)
					"na\nme",//(空白)
					"na\u000Bme",//(空白)
					"na\fme",//(空白)
					"na\rme",//(空白)
					"na<me",//&lt;
					"na>me",//&gt;
					"na-me",//-
					"na,me",//,
					"na^me",//^
					"na%me",//%
					"na&me",//&amp;
					"na?me",//?
					"na|me",//|
					"na@me",//@
					"na\'me",//'
					"na\"me",//&quot;"
					"HOT",
					"NO__HAT",
			};
			for (String ik : illegalHatKeys) {
				boolean coughtException;
				try {
					new ExBasePattern(normalKeys[0], ik);
					coughtException = false;
				} catch (IllegalArgumentException ex) {
					coughtException = true;
				}
				assertTrue(coughtException);
			}
		}
	}

	/**
	 * {@link exalge2.ExBasePattern#ExBasePattern(java.lang.String, java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testExBasePatternStringStringString() {
		ExBasePattern test1 = new ExBasePattern("","","");
		assertEquals(wc____, test1.key());
		
		ExBasePattern test2 = new ExBasePattern(keyName, ExBase.NO_HAT, "");
		assertEquals(nohatN___, test2.key());
		
		ExBasePattern test3 = new ExBasePattern(keyName, ExBase.HAT, keyUnit);
		assertEquals(hatNU__, test3.key());
		
		// 不正文字チェック
		String[] normalKeys = new String[]{"name", "*", "unit", "time", "subject"};
		//--- name
		{
			String[] illegalNameKeys = new String[]{
					"na me",//(空白)
					"na\tme",//(空白)
					"na\nme",//(空白)
					"na\u000Bme",//(空白)
					"na\fme",//(空白)
					"na\rme",//(空白)
					"na<me",//&lt;
					"na>me",//&gt;
					"na-me",//-
					"na,me",//,
					"na^me",//^
					"na%me",//%
					"na&me",//&amp;
					"na?me",//?
					"na|me",//|
					"na@me",//@
					"na\'me",//'
					"na\"me",//&quot;"
			};
			for (String ik : illegalNameKeys) {
				boolean coughtException;
				try {
					new ExBasePattern(ik, normalKeys[1], normalKeys[2]);
					coughtException = false;
				} catch (IllegalArgumentException ex) {
					coughtException = true;
				}
				assertTrue(coughtException);
			}
		}
		//--- hat
		{
			String[] illegalHatKeys = new String[]{
					"na me",//(空白)
					"na\tme",//(空白)
					"na\nme",//(空白)
					"na\u000Bme",//(空白)
					"na\fme",//(空白)
					"na\rme",//(空白)
					"na<me",//&lt;
					"na>me",//&gt;
					"na-me",//-
					"na,me",//,
					"na^me",//^
					"na%me",//%
					"na&me",//&amp;
					"na?me",//?
					"na|me",//|
					"na@me",//@
					"na\'me",//'
					"na\"me",//&quot;"
					"HOT",
					"NO__HAT",
			};
			for (String ik : illegalHatKeys) {
				boolean coughtException;
				try {
					new ExBasePattern(normalKeys[0], ik, normalKeys[2]);
					coughtException = false;
				} catch (IllegalArgumentException ex) {
					coughtException = true;
				}
				assertTrue(coughtException);
			}
		}
		//--- unit
		{
			String[] illegalUnitKeys = new String[]{
					"na me",//(空白)
					"na\tme",//(空白)
					"na\nme",//(空白)
					"na\u000Bme",//(空白)
					"na\fme",//(空白)
					"na\rme",//(空白)
					"na<me",//&lt;
					"na>me",//&gt;
					"na-me",//-
					"na,me",//,
					"na^me",//^
					"na%me",//%
					"na&me",//&amp;
					"na?me",//?
					"na|me",//|
					"na@me",//@
					"na\'me",//'
					"na\"me",//&quot;"
			};
			for (String ik : illegalUnitKeys) {
				boolean coughtException;
				try {
					new ExBasePattern(normalKeys[0], normalKeys[1], ik);
					coughtException = false;
				} catch (IllegalArgumentException ex) {
					coughtException = true;
				}
				assertTrue(coughtException);
			}
		}
	}

	/**
	 * {@link exalge2.ExBasePattern#ExBasePattern(java.lang.String, java.lang.String, java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testExBasePatternStringStringStringString() {
		ExBasePattern test1 = new ExBasePattern("","","","");
		assertEquals(wc____, test1.key());
		
		ExBasePattern test2 = new ExBasePattern(keyName, ExBase.HAT, keyUnit, null);
		assertEquals(hatNU__, test2.key());
		
		ExBasePattern test3 = new ExBasePattern(keyName, ExBase.NO_HAT, keyUnit, keyTime);
		assertEquals(nohatNUT_, test3.key());
		
		// 不正文字チェック
		String[] normalKeys = new String[]{"name", "*", "unit", "time", "subject"};
		//--- name
		{
			String[] illegalNameKeys = new String[]{
					"na me",//(空白)
					"na\tme",//(空白)
					"na\nme",//(空白)
					"na\u000Bme",//(空白)
					"na\fme",//(空白)
					"na\rme",//(空白)
					"na<me",//&lt;
					"na>me",//&gt;
					"na-me",//-
					"na,me",//,
					"na^me",//^
					"na%me",//%
					"na&me",//&amp;
					"na?me",//?
					"na|me",//|
					"na@me",//@
					"na\'me",//'
					"na\"me",//&quot;"
			};
			for (String ik : illegalNameKeys) {
				boolean coughtException;
				try {
					new ExBasePattern(ik, normalKeys[1], normalKeys[2], normalKeys[3]);
					coughtException = false;
				} catch (IllegalArgumentException ex) {
					coughtException = true;
				}
				assertTrue(coughtException);
			}
		}
		//--- hat
		{
			String[] illegalHatKeys = new String[]{
					"na me",//(空白)
					"na\tme",//(空白)
					"na\nme",//(空白)
					"na\u000Bme",//(空白)
					"na\fme",//(空白)
					"na\rme",//(空白)
					"na<me",//&lt;
					"na>me",//&gt;
					"na-me",//-
					"na,me",//,
					"na^me",//^
					"na%me",//%
					"na&me",//&amp;
					"na?me",//?
					"na|me",//|
					"na@me",//@
					"na\'me",//'
					"na\"me",//&quot;"
					"HOT",
					"NO__HAT",
			};
			for (String ik : illegalHatKeys) {
				boolean coughtException;
				try {
					new ExBasePattern(normalKeys[0], ik, normalKeys[2], normalKeys[3]);
					coughtException = false;
				} catch (IllegalArgumentException ex) {
					coughtException = true;
				}
				assertTrue(coughtException);
			}
		}
		//--- unit
		{
			String[] illegalUnitKeys = new String[]{
					"na me",//(空白)
					"na\tme",//(空白)
					"na\nme",//(空白)
					"na\u000Bme",//(空白)
					"na\fme",//(空白)
					"na\rme",//(空白)
					"na<me",//&lt;
					"na>me",//&gt;
					"na-me",//-
					"na,me",//,
					"na^me",//^
					"na%me",//%
					"na&me",//&amp;
					"na?me",//?
					"na|me",//|
					"na@me",//@
					"na\'me",//'
					"na\"me",//&quot;"
			};
			for (String ik : illegalUnitKeys) {
				boolean coughtException;
				try {
					new ExBasePattern(normalKeys[0], normalKeys[1], ik, normalKeys[3]);
					coughtException = false;
				} catch (IllegalArgumentException ex) {
					coughtException = true;
				}
				assertTrue(coughtException);
			}
		}
		//--- time
		{
			String[] illegalTimeKeys = new String[]{
					"na me",//(空白)
					"na\tme",//(空白)
					"na\nme",//(空白)
					"na\u000Bme",//(空白)
					"na\fme",//(空白)
					"na\rme",//(空白)
					"na<me",//&lt;
					"na>me",//&gt;
					"na-me",//-
					"na,me",//,
					"na^me",//^
					"na%me",//%
					"na&me",//&amp;
					"na?me",//?
					"na|me",//|
					"na@me",//@
					"na\'me",//'
					"na\"me",//&quot;"
			};
			for (String ik : illegalTimeKeys) {
				boolean coughtException;
				try {
					new ExBasePattern(normalKeys[0], normalKeys[1], normalKeys[2], ik);
					coughtException = false;
				} catch (IllegalArgumentException ex) {
					coughtException = true;
				}
				assertTrue(coughtException);
			}
		}
	}

	/**
	 * {@link exalge2.ExBasePattern#ExBasePattern(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testExBasePatternStringStringStringStringString() {
		ExBasePattern test1 = new ExBasePattern("","","","","");
		assertEquals(wc____, test1.key());
		
		ExBasePattern test2 = new ExBasePattern(keyName, ExBase.NO_HAT, keyUnit, keyTime, null);
		assertEquals(nohatNUT_, test2.key());
		
		ExBasePattern test3 = new ExBasePattern(keyName, ExBase.HAT, keyUnit, keyTime, keySubject);
		assertEquals(hatNUTS, test3.key());
		
		// 不正文字チェック
		String[] normalKeys = new String[]{"name", "*", "unit", "time", "subject"};
		//--- name
		{
			String[] illegalNameKeys = new String[]{
					"na me",//(空白)
					"na\tme",//(空白)
					"na\nme",//(空白)
					"na\u000Bme",//(空白)
					"na\fme",//(空白)
					"na\rme",//(空白)
					"na<me",//&lt;
					"na>me",//&gt;
					"na-me",//-
					"na,me",//,
					"na^me",//^
					"na%me",//%
					"na&me",//&amp;
					"na?me",//?
					"na|me",//|
					"na@me",//@
					"na\'me",//'
					"na\"me",//&quot;"
			};
			for (String ik : illegalNameKeys) {
				boolean coughtException;
				try {
					new ExBasePattern(ik, normalKeys[1], normalKeys[2], normalKeys[3], normalKeys[4]);
					coughtException = false;
				} catch (IllegalArgumentException ex) {
					coughtException = true;
				}
				assertTrue(coughtException);
			}
		}
		//--- hat
		{
			String[] illegalHatKeys = new String[]{
					"na me",//(空白)
					"na\tme",//(空白)
					"na\nme",//(空白)
					"na\u000Bme",//(空白)
					"na\fme",//(空白)
					"na\rme",//(空白)
					"na<me",//&lt;
					"na>me",//&gt;
					"na-me",//-
					"na,me",//,
					"na^me",//^
					"na%me",//%
					"na&me",//&amp;
					"na?me",//?
					"na|me",//|
					"na@me",//@
					"na\'me",//'
					"na\"me",//&quot;"
					"HOT",
					"NO__HAT",
			};
			for (String ik : illegalHatKeys) {
				boolean coughtException;
				try {
					new ExBasePattern(normalKeys[0], ik, normalKeys[2], normalKeys[3], normalKeys[4]);
					coughtException = false;
				} catch (IllegalArgumentException ex) {
					coughtException = true;
				}
				assertTrue(coughtException);
			}
		}
		//--- unit
		{
			String[] illegalUnitKeys = new String[]{
					"na me",//(空白)
					"na\tme",//(空白)
					"na\nme",//(空白)
					"na\u000Bme",//(空白)
					"na\fme",//(空白)
					"na\rme",//(空白)
					"na<me",//&lt;
					"na>me",//&gt;
					"na-me",//-
					"na,me",//,
					"na^me",//^
					"na%me",//%
					"na&me",//&amp;
					"na?me",//?
					"na|me",//|
					"na@me",//@
					"na\'me",//'
					"na\"me",//&quot;"
			};
			for (String ik : illegalUnitKeys) {
				boolean coughtException;
				try {
					new ExBasePattern(normalKeys[0], normalKeys[1], ik, normalKeys[3], normalKeys[4]);
					coughtException = false;
				} catch (IllegalArgumentException ex) {
					coughtException = true;
				}
				assertTrue(coughtException);
			}
		}
		//--- time
		{
			String[] illegalTimeKeys = new String[]{
					"na me",//(空白)
					"na\tme",//(空白)
					"na\nme",//(空白)
					"na\u000Bme",//(空白)
					"na\fme",//(空白)
					"na\rme",//(空白)
					"na<me",//&lt;
					"na>me",//&gt;
					"na-me",//-
					"na,me",//,
					"na^me",//^
					"na%me",//%
					"na&me",//&amp;
					"na?me",//?
					"na|me",//|
					"na@me",//@
					"na\'me",//'
					"na\"me",//&quot;"
			};
			for (String ik : illegalTimeKeys) {
				boolean coughtException;
				try {
					new ExBasePattern(normalKeys[0], normalKeys[1], normalKeys[2], ik, normalKeys[4]);
					coughtException = false;
				} catch (IllegalArgumentException ex) {
					coughtException = true;
				}
				assertTrue(coughtException);
			}
		}
		//--- subject
		{
			String[] illegalSubjectKeys = new String[]{
					"na me",//(空白)
					"na\tme",//(空白)
					"na\nme",//(空白)
					"na\u000Bme",//(空白)
					"na\fme",//(空白)
					"na\rme",//(空白)
					"na<me",//&lt;
					"na>me",//&gt;
					"na-me",//-
					"na,me",//,
					"na^me",//^
					"na%me",//%
					"na&me",//&amp;
					"na?me",//?
					"na|me",//|
					"na@me",//@
					"na\'me",//'
					"na\"me",//&quot;"
			};
			for (String ik : illegalSubjectKeys) {
				boolean coughtException;
				try {
					new ExBasePattern(normalKeys[0], normalKeys[1], normalKeys[2], normalKeys[3], ik);
					coughtException = false;
				} catch (IllegalArgumentException ex) {
					coughtException = true;
				}
				assertTrue(coughtException);
			}
		}
	}

	/**
	 * Test {@link exalge2.ExBasePattern#ExBasePattern(ExBase, boolean)}
	 * @since 0.94
	 */
	public void testExBasePatternExBaseBoolean() {
		String keyName = "名前";
		String keyUnit = "単位";
		String keyTime = "時間";
		String keySubject = "主体";
		ExBase base1 = new ExBase(keyName, ExBase.NO_HAT);
		ExBase base2 = new ExBase(keyName, ExBase.HAT, keyUnit);
		ExBase base3 = new ExBase(keyName, ExBase.NO_HAT, keyUnit, keyTime);
		ExBase base4 = new ExBase(keyName, ExBase.HAT, keyUnit, keyTime, keySubject);
		ExBase base5 = new ExBase(ExBasePattern.WILDCARD,
									ExBase.HAT,
									ExBasePattern.WILDCARD,
									ExBasePattern.WILDCARD,
									ExBasePattern.WILDCARD);
		ExBase base6 = new ExBase(ExBasePattern.WILDCARD,
									ExBase.HAT,
									keyUnit + ExBasePattern.WILDCARD,
									ExBasePattern.WILDCARD + keyTime,
									ExBasePattern.WILDCARD + keySubject + ExBasePattern.WILDCARD);
		String strbase1f = makeKeyString(keyName, ExBase.NO_HAT, ExBase.OMITTED, ExBase.OMITTED, ExBase.OMITTED);
		String strbase1t = makeKeyString(keyName, ExBasePattern.WILDCARD, ExBase.OMITTED, ExBase.OMITTED, ExBase.OMITTED);
		String strbase2f = makeKeyString(keyName, ExBase.HAT, keyUnit, ExBase.OMITTED, ExBase.OMITTED);
		String strbase2t = makeKeyString(keyName, ExBasePattern.WILDCARD, keyUnit, ExBase.OMITTED, ExBase.OMITTED);
		String strbase3f = makeKeyString(keyName, ExBase.NO_HAT, keyUnit, keyTime, ExBase.OMITTED);
		String strbase3t = makeKeyString(keyName, ExBasePattern.WILDCARD, keyUnit, keyTime, ExBase.OMITTED);
		String strbase4f = makeKeyString(keyName, ExBase.HAT, keyUnit, keyTime, keySubject);
		String strbase4t = makeKeyString(keyName, ExBasePattern.WILDCARD, keyUnit, keyTime, keySubject);
		String strbase5f = makeKeyString(ExBasePattern.WILDCARD, ExBase.HAT, ExBasePattern.WILDCARD, ExBasePattern.WILDCARD, ExBasePattern.WILDCARD);
		String strbase5t = makeKeyString(ExBasePattern.WILDCARD, ExBasePattern.WILDCARD, ExBasePattern.WILDCARD, ExBasePattern.WILDCARD, ExBasePattern.WILDCARD);
		String strbase6f = makeKeyString(ExBasePattern.WILDCARD,
										ExBase.HAT,
										keyUnit + ExBasePattern.WILDCARD,
										ExBasePattern.WILDCARD + keyTime,
										ExBasePattern.WILDCARD + keySubject + ExBasePattern.WILDCARD);
		String strbase6t = makeKeyString(ExBasePattern.WILDCARD,
										ExBasePattern.WILDCARD,
										keyUnit + ExBasePattern.WILDCARD,
										ExBasePattern.WILDCARD + keyTime,
										ExBasePattern.WILDCARD + keySubject + ExBasePattern.WILDCARD);
		
		ExBasePattern pat1f = new ExBasePattern(base1, false);
		ExBasePattern pat1t = new ExBasePattern(base1, true);
		ExBasePattern pat2f = new ExBasePattern(base2, false);
		ExBasePattern pat2t = new ExBasePattern(base2, true);
		ExBasePattern pat3f = new ExBasePattern(base3, false);
		ExBasePattern pat3t = new ExBasePattern(base3, true);
		ExBasePattern pat4f = new ExBasePattern(base4, false);
		ExBasePattern pat4t = new ExBasePattern(base4, true);
		ExBasePattern pat5f = new ExBasePattern(base5, false);
		ExBasePattern pat5t = new ExBasePattern(base5, true);
		ExBasePattern pat6f = new ExBasePattern(base6, false);
		ExBasePattern pat6t = new ExBasePattern(base6, true);
		
		assertEquals(pat1f.key(), strbase1f);
		assertEquals(pat1t.key(), strbase1t);
		assertEquals(pat2f.key(), strbase2f);
		assertEquals(pat2t.key(), strbase2t);
		assertEquals(pat3f.key(), strbase3f);
		assertEquals(pat3t.key(), strbase3t);
		assertEquals(pat4f.key(), strbase4f);
		assertEquals(pat4t.key(), strbase4t);
		assertEquals(pat5f.key(), strbase5f);
		assertEquals(pat5t.key(), strbase5t);
		assertEquals(pat6f.key(), strbase6f);
		assertEquals(pat6t.key(), strbase6t);
	}

	/**
	 * {@link exalge2.ExBasePattern#matches(exalge2.ExBase)} のためのテスト・メソッド。
	 */
	public void testMatches() {
		ExBase base = new ExBase("大河内", ExBase.HAT, "yen", "Y2007M08", "山田花子");
		
		ExBasePattern test1 = new ExBasePattern("");
		assertTrue(test1.matches(base));
		
		ExBasePattern test2 = new ExBasePattern("", ExBase.NO_HAT);
		assertFalse(test2.matches(base));
		
		ExBasePattern test3 = new ExBasePattern("大河内", "*", "yen", "Y*7M*", "*");
		assertTrue(test3.matches(base));
		
		ExBasePattern test4 = new ExBasePattern("大河内", ExBase.HAT, "yen", "Y*8M*", "山田花子");
		assertFalse(test4.matches(base));
		
		ExBasePattern test5 = new ExBasePattern("大河内", ExBase.HAT, "yen", "Y2007M08", "山田花子");
		assertTrue(test5.matches(base));
		
		// 正規表現の予約文字を利用したパターンマッチング
		ExBase target1 = new ExBase(new String[]{
				"\\Q" + testNormalChar + "\\E",
				ExBase.HAT,
				"\\Q" + testNormalChar + "\\E",
				"\\Q" + testNormalChar + "\\E",
				"\\Q" + testNormalChar + "\\E",
		});
		ExBase target2 = new ExBase(new String[]{
				"NAME",
				ExBase.NO_HAT,
				"UNIT",
				"TIME",
				"SUBJECT",
		});
		//--- ExBasePattern
		for (int i = 0; i < ExBase.NUM_ALL_KEYS; i++) {
			if (i != ExBase.KEY_HAT) {
				String[] keys;
				for (char c : testNormalChar.toCharArray()) {
					keys = createPatternArrayFromChar(i, c);
					ExBasePattern pat = new ExBasePattern(keys);
					//System.out.println("[" + pat.key() + "]:=" + pat.getMatchingPattern().pattern());
					System.out.println("[" + pat.key() + "]:=" + String.valueOf(pat.getPatternItems()));
					assertTrue(pat.matches(target1));
					if (c != '*') {
						if (pat.matches(target2)) {
							fail("hoge");
						}
						assertFalse(pat.matches(target2));
					}
				}
				keys = createPatternArrayFromString(i, "\\Q");
				ExBasePattern pat2 = new ExBasePattern(keys);
				//System.out.println("[" + pat2.key() + "]:=" + pat2.getMatchingPattern().pattern());
				System.out.println("[" + pat2.key() + "]:=" + String.valueOf(pat2.getPatternItems()));
				assertTrue(pat2.matches(target1));
				assertFalse(pat2.matches(target2));
				keys = createPatternArrayFromString(i, "\\E");
				ExBasePattern pat3 = new ExBasePattern(keys);
				//System.out.println("[" + pat3.key() + "]:=" + pat3.getMatchingPattern().pattern());
				System.out.println("[" + pat3.key() + "]:=" + String.valueOf(pat3.getPatternItems()));
				assertTrue(pat3.matches(target1));
				assertFalse(pat3.matches(target2));
				keys = createPatternArrayFromString(i, "\\_");
				ExBasePattern pat4 = new ExBasePattern(keys);
				//System.out.println("[" + pat4.key() + "]:=" + pat4.getMatchingPattern().pattern());
				System.out.println("[" + pat4.key() + "]:=" + String.valueOf(pat4.getPatternItems()));
				assertTrue(pat4.matches(target1));
				assertFalse(pat4.matches(target2));
			}
		}
		//--- ExBase in Wildcard
		for (int i = 0; i < ExBase.NUM_ALL_KEYS; i++) {
			if (i != ExBase.KEY_HAT) {
				String[] keys;
				ExBase baseinw;
				for (char c : testNormalChar.toCharArray()) {
					keys = createExBasePatternArrayFromChar(i, c);
					baseinw = new ExBase(keys);
					ExBasePattern pat = new ExBasePattern(baseinw, true);
					//System.out.println("[" + pat.key() + "]:=" + pat.getMatchingPattern().pattern());
					System.out.println("[" + pat.key() + "]:=" + String.valueOf(pat.getPatternItems()));
					assertTrue(pat.matches(target1));
					if (c != '*') {
						if (pat.matches(target2)) {
							fail("hoge");
						}
						assertFalse(pat.matches(target2));
					}
				}
				keys = createExBasePatternArrayFromString(i, "\\Q");
				baseinw = new ExBase(keys);
				ExBasePattern pat2 = new ExBasePattern(baseinw, true);
				//System.out.println("[" + pat2.key() + "]:=" + pat2.getMatchingPattern().pattern());
				System.out.println("[" + pat2.key() + "]:=" + String.valueOf(pat2.getPatternItems()));
				assertTrue(pat2.matches(target1));
				assertFalse(pat2.matches(target2));
				keys = createExBasePatternArrayFromString(i, "\\E");
				baseinw = new ExBase(keys);
				ExBasePattern pat3 = new ExBasePattern(baseinw, true);
				//System.out.println("[" + pat3.key() + "]:=" + pat3.getMatchingPattern().pattern());
				System.out.println("[" + pat3.key() + "]:=" + String.valueOf(pat3.getPatternItems()));
				assertTrue(pat3.matches(target1));
				assertFalse(pat3.matches(target2));
				keys = createExBasePatternArrayFromString(i, "\\_");
				baseinw = new ExBase(keys);
				ExBasePattern pat4 = new ExBasePattern(baseinw, true);
				//System.out.println("[" + pat4.key() + "]:=" + pat4.getMatchingPattern().pattern());
				System.out.println("[" + pat4.key() + "]:=" + String.valueOf(pat4.getPatternItems()));
				assertTrue(pat4.matches(target1));
				assertFalse(pat4.matches(target2));
			}
		}
	}

	/**
	 * {@link exalge2.ExBasePattern#translate(exalge2.ExBase)} のためのテスト・メソッド。
	 */
	public void testTranslate() {
		ExBase base = new ExBase("大河内", ExBase.HAT, "yen", "Y2007M08", "山田花子");
		
		ExBasePattern test1 = new ExBasePattern("");
		ExBase ret1 = test1.translate(base);
		assertTrue(base.equals(ret1));
		
		ExBasePattern test2 = new ExBasePattern("御厨", ExBase.NO_HAT, "$", "*", "*");
		String ans2 = "御厨-NO_HAT-$-Y2007M08-山田花子";
		ExBase ret2 = test2.translate(base);
		assertEquals(ans2, ret2.key());
		
		ExBasePattern test3 = new ExBasePattern("","","","#","#");
		String ans3 = "大河内-HAT-yen-#-#";
		ExBase ret3 = test3.translate(base);
		assertEquals(ans3, ret3.key());
		
		ExBasePattern test4 = new ExBasePattern(nohatNUTS);
		ExBase ret4 = test4.translate(base);
		assertEquals(nohatNUTS, ret4.key());
	}

	/**
	 * Test {@link ExBasePattern#key()}
	 *
	 */
	public void testKey() {
		ExBasePattern test1 = new ExBasePattern(nohatNUTS);
		assertEquals(nohatNUTS, test1.key());
		
		ExBasePattern test2 = new ExBasePattern(hatNUTS);
		assertEquals(hatNUTS, test2.key());
		
		ExBasePattern test3 = new ExBasePattern(wcNUTS);
		assertEquals(wcNUTS, test3.key());
	}
	
	/**
	 * Test {@link ExBasePattern#hashCode()}
	 */
	public void testHashCode() {
		ExBasePattern nohatNUTS1 = new ExBasePattern(nohatNUTS);
		assertEquals(nohatNUTS1.hashCode(), nohatNUTS.hashCode());
		
		ExBasePattern hatNUTS1 = new ExBasePattern(hatNUTS);
		assertEquals(hatNUTS1.hashCode(), hatNUTS.hashCode());
		
		ExBasePattern wcNUTS1 = new ExBasePattern(wcNUTS);
		assertEquals(wcNUTS1.hashCode(), wcNUTS.hashCode());
	}

	/**
	 * Test {@link ExBasePattern#equals(Object)}
	 *
	 */
	public void testEqualsObject() {
		ExBasePattern nohatNUTS1 = new ExBasePattern(nohatNUTS);
		ExBasePattern nohatNUTS2 = new ExBasePattern(nohatNUTS);
		ExBasePattern hatNUTS1 = new ExBasePattern(hatNUTS);
		ExBasePattern hatNUTS2 = new ExBasePattern(hatNUTS);
		ExBasePattern nohatNU_S1 = new ExBasePattern(nohatNU_S);
		ExBasePattern nullBase = null;
		
		assertTrue(nohatNUTS1.equals(nohatNUTS2));
		assertTrue(hatNUTS1.equals(hatNUTS2));
		assertFalse(nohatNUTS1.equals(hatNUTS1));
		assertFalse(hatNUTS1.equals(nohatNUTS1));
		assertFalse(nohatNU_S1.equals(nohatNUTS1));
		assertFalse(nohatNU_S1.equals(hatNUTS1));
		assertFalse(nohatNU_S1.equals(nullBase));
		assertFalse(nohatNU_S1.equals(null));
		
		assertTrue(nohatNUTS1.equals(nohatNUTS1));
	}

	/**
	 * Test {@link ExBasePattern#toString()}
	 */
	public void testToString() {
		ExBasePattern nohatNUTS1 = new ExBasePattern(nohatNUTS);
		ExBasePattern hatNUTS1   = new ExBasePattern(hatNUTS);
		ExBasePattern wcNUTS1    = new ExBasePattern(wcNUTS);

		assertEquals(nohatNUTS1.toString(), nohatNUTS);
		assertEquals(hatNUTS1.toString(), hatNUTS);
		assertEquals(wcNUTS1.toString(), wcNUTS);
	}
	
	private String makeKeyString(String nameKey, String hatKey, String unitKey, String timeKey, String subjectKey) {
		return (nameKey + "-" + hatKey + "-" + unitKey + "-" + timeKey + "-" + subjectKey);
	}
	
	private void printCompareResult(int cmpRequest, int cmpResult, ExBasePattern aBase, ExBasePattern bBase) {
		StringBuffer sb = new StringBuffer();
		sb.append("<compareTo> a[");
		sb.append(aBase.toString());
		sb.append("] ");
		if (cmpResult < 0)
			sb.append("<");
		else if (cmpResult > 0)
			sb.append(">");
		else
			sb.append("=");
		sb.append("(");
		if (cmpRequest < 0)
			sb.append("<");
		else if (cmpRequest > 0)
			sb.append(">");
		else
			sb.append("=");
		sb.append(") b[");
		sb.append(bBase.toString());
		sb.append("]");
		
		System.out.println(sb.toString());
	}
	
	private int judgeCompareResult(String aBaseKey, String bBaseKey) {
		String[] akeys = new String[]{"*","*","*","*","*"};
		String[] bkeys = new String[]{"*","*","*","*","*"};
		
		String[] arykeyA = aBaseKey.split("-");
		String[] arykeyB = bBaseKey.split("-");
		
		for (int i = 0; i < akeys.length && i < arykeyA.length; i++) {
			if (arykeyA[i].length() > 0) {
				akeys[i] = arykeyA[i];
			}
		}
		
		for (int i = 0; i < bkeys.length && i < arykeyB.length; i++) {
			if (arykeyB[i].length() > 0) {
				bkeys[i] = arykeyB[i];
			}
		}
		
		for (int i = 0; i < akeys.length; i++) {
			int cmp = akeys[i].compareTo(bkeys[i]);
			if (cmp != 0) {
				return cmp;
			}
		}
		
		return 0;
	}
	
	/**
	 * Test {@link ExBasePattern#compareTo(ExBasePattern)}
	 * 
	 */
	public void testCompareToExBase() {
		String[][][] strBase = new String[][][]{
				{
					{"ABB-NO_HAT", "ABB-HAT", "ABB-*"},	// 0-0-0, 0-0-1
					{"ABC-NO_HAT", "ABC-HAT", "ABC-*"},	// 0-1-0, 0-1-1
					{"ABD-NO_HAT", "ABD-HAT", "ABD-*"},	// 0-2-0, 0-2-1
				},
				{
					{"ABC-NO_HAT-ABB", "ABC-HAT-ABB", "ABC-*-ABB"},	// 1-0-0, 1-0-1
					{"ABC-NO_HAT-ABC", "ABC-HAT-ABC", "ABC-*-ABC"},	// 1-1-0, 1-1-1
					{"ABC-NO_HAT-ABD", "ABC-HAT-ABD", "ABC-*-ABD"},	// 1-2-0, 1-2-1
				},
				{
					{"ABC-NO_HAT-ABC-ABB", "ABC-HAT-ABC-ABB", "ABC-*-ABC-ABB"},	// 2-0-0, 2-0-1
					{"ABC-NO_HAT-ABC-ABC", "ABC-HAT-ABC-ABC", "ABC-*-ABC-ABC"},	// 2-1-0, 2-1-1
					{"ABC-NO_HAT-ABC-ABD", "ABC-HAT-ABC-ABD", "ABC-*-ABC-ABD"},	// 2-2-0, 2-2-1
				},
				{
					{"ABC-NO_HAT-ABC-ABC-ABB", "ABC-HAT-ABC-ABC-ABB", "ABC-*-ABC-ABC-ABB"},	// 3-0-0, 3-0-1
					{"ABC-NO_HAT-ABC-ABC-ABC", "ABC-HAT-ABC-ABC-ABC", "ABC-*-ABC-ABC-ABC"},	// 3-1-0, 3-1-1
					{"ABC-NO_HAT-ABC-ABC-ABD", "ABC-HAT-ABC-ABC-ABD", "ABC-*-ABC-ABC-ABD"},	// 3-2-0, 3-2-1
				},
		};

		for (int a0 = 0; a0 < strBase.length; a0++) {
			for (int a1 = 0; a1 < strBase[a0].length; a1++) {
				for (int a2 = 0; a2 < strBase[a0][a1].length; a2++) {
					for (int b0 = 0; b0 < strBase.length; b0++) {
						for (int b1 = 0; b1 < strBase[b0].length; b1++) {
							for (int b2 = 0; b2 < strBase[b0][b1].length; b2++) {
								// 予定比較結果
								int cmpRequest = judgeCompareResult(strBase[a0][a1][a2], strBase[b0][b1][b2]);
								// 比較
								ExBasePattern aBase = new ExBasePattern(strBase[a0][a1][a2]);
								ExBasePattern bBase = new ExBasePattern(strBase[b0][b1][b2]);
								int cmpResult = aBase.compareTo(bBase);
								printCompareResult(cmpRequest, cmpResult, aBase, bBase);
								if (cmpRequest < 0)
									assertTrue(0 > cmpResult);
								else if (cmpRequest > 0)
									assertTrue(0 < cmpResult);
								else
									assertTrue(0 == cmpResult);
								// 大文字小文字を区別しない比較
								if (cmpResult == 0) {
									String strLower = strBase[a0][a1][a2].toLowerCase();
									strLower = strLower.replaceAll("no_hat", "NO_HAT");
									strLower = strLower.replaceAll("hat", "HAT");
									ExBasePattern cBase = new ExBasePattern(strLower);
									assertTrue(0 != aBase.compareTo(cBase));
									assertTrue(0 == aBase.compareToIgnoreCase(cBase));
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * test {@link exalge2.ExBasePattern#setHat()}
	 * @since 0.960
	 */
	public void testSetHat() {
		ExBasePattern nPattern = new ExBasePattern(nohatNUTS);
		ExBasePattern hPattern = new ExBasePattern(hatNUTS);
		ExBasePattern wPattern = new ExBasePattern(wcNUTS);
		
		ExBasePattern test1 = new ExBasePattern(nohatNUTS);
		ExBasePattern ret1 = test1.setHat();
		assertTrue(nPattern.equals(test1));
		assertTrue(hPattern.equals(ret1));
		
		ExBasePattern test2 = new ExBasePattern(hatNUTS);
		ExBasePattern ret2 = test2.setHat();
		assertTrue(hPattern.equals(test2));
		assertTrue(hPattern.equals(ret2));
		assertSame(test2, ret2);
		
		ExBasePattern test3 = new ExBasePattern(wcNUTS);
		ExBasePattern ret3 = test3.setHat();
		assertTrue(wPattern.equals(test3));
		assertTrue(hPattern.equals(ret3));
	}

	/**
	 * test {@link exalge2.ExBasePattern#setNoHat()}
	 * @since 0.960
	 */
	public void testSetNoHat() {
		ExBasePattern nPattern = new ExBasePattern(nohatNUTS);
		ExBasePattern hPattern = new ExBasePattern(hatNUTS);
		ExBasePattern wPattern = new ExBasePattern(wcNUTS);
		
		ExBasePattern test1 = new ExBasePattern(nohatNUTS);
		ExBasePattern ret1 = test1.setNoHat();
		assertTrue(nPattern.equals(test1));
		assertTrue(nPattern.equals(ret1));
		assertSame(test1, ret1);
		
		ExBasePattern test2 = new ExBasePattern(hatNUTS);
		ExBasePattern ret2 = test2.setNoHat();
		assertTrue(hPattern.equals(test2));
		assertTrue(nPattern.equals(ret2));
		
		ExBasePattern test3 = new ExBasePattern(wcNUTS);
		ExBasePattern ret3 = test3.setNoHat();
		assertTrue(wPattern.equals(test3));
		assertTrue(nPattern.equals(ret3));
	}

	/**
	 * test {@link exalge2.ExBasePattern#setWildcardHat()}
	 * @since 0.960
	 */
	public void testSetWildcardHat() {
		ExBasePattern nPattern = new ExBasePattern(nohatNUTS);
		ExBasePattern hPattern = new ExBasePattern(hatNUTS);
		ExBasePattern wPattern = new ExBasePattern(wcNUTS);
		
		ExBasePattern test1 = new ExBasePattern(nohatNUTS);
		ExBasePattern ret1 = test1.setWildcardHat();
		assertTrue(nPattern.equals(test1));
		assertTrue(wPattern.equals(ret1));
		
		ExBasePattern test2 = new ExBasePattern(hatNUTS);
		ExBasePattern ret2 = test2.setWildcardHat();
		assertTrue(hPattern.equals(test2));
		assertTrue(wPattern.equals(ret2));
		
		ExBasePattern test3 = new ExBasePattern(wcNUTS);
		ExBasePattern ret3 = test3.setWildcardHat();
		assertTrue(wPattern.equals(test3));
		assertTrue(wPattern.equals(ret3));
		assertSame(test3, ret3);
	}

	/**
	 * test {@link exalge2.ExBasePattern#ExBasePattern()}
	 * @since 0.982
	 */
	public void testExBasePattern() {
		ExBasePattern pattern = new ExBasePattern();
		for (String key : pattern._baseKeys) {
			assertEquals(ExBasePattern.WILDCARD, key);
		}
		assertEquals(pattern.key().hashCode(), pattern.hashCode());
	}

	/**
	 * test {@link exalge2.ExBasePattern#toPattern(ExBase)}
	 * @since 0.982
	 */
	public void testToPatternExBase() {
		ExBase nBase = new ExBase(nohatNUTS);
		ExBase hBase = new ExBase(hatNUTS);
		ExBasePattern wPattern = new ExBasePattern(wcNUTS);
		
		ExBasePattern pat;
		
		pat = ExBasePattern.toPattern(null);
		assertTrue(pat == null);
		
		pat = ExBasePattern.toPattern(nBase);
		assertEquals(pat, wPattern);
		
		pat = ExBasePattern.toPattern(hBase);
		assertEquals(pat, wPattern);
	}

	/**
	 * test {@link exalge2.ExBasePattern#isWildcardHat()}
	 * @since 0.982
	 */
	public void testIsWildcardHat() {
		ExBasePattern nPattern = new ExBasePattern(nohatNUTS);
		ExBasePattern hPattern = new ExBasePattern(hatNUTS);
		ExBasePattern wPattern = new ExBasePattern(wcNUTS);
		
		assertFalse(nPattern.isWildcardHat());
		assertFalse(hPattern.isWildcardHat());
		assertTrue(wPattern.isWildcardHat());
	}
}
