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

import exalge2.ExBase;
import exalge2.ExtendedKeyID;
import junit.framework.TestCase;

public class ExBaseTest extends TestCase {

	static private final String strData1 = "hoge";
	static private final String strData2 = "予定";
	static private final String strData3 = "Y2000M09";
	
	static private final String keyName = "名前";
	static private final String keyUnit = "単位";
	static private final String keyTime = "時間";
	static private final String keySubject = "サブジェクト";
	
	static private final String nohatN___ = keyName + "-NO_HAT-#-#-#";
	static private final String hatN___ = keyName + "-HAT-#-#-#";
	static private final String nohatNU__ = keyName + "-NO_HAT-" + keyUnit + "-#-#";
	static private final String hatNU__ = keyName + "-HAT-" + keyUnit + "-#-#";
	static private final String nohatNUT_ = keyName + "-NO_HAT-" + keyUnit + "-" + keyTime + "-#";
	static private final String hatNUT_ = keyName + "-HAT-" + keyUnit + "-" + keyTime + "-#";
	static private final String nohatNUTS = keyName + "-NO_HAT-" + keyUnit + "-" + keyTime + "-" + keySubject;
	static private final String hatNUTS = keyName + "-HAT-" + keyUnit + "-" + keyTime + "-" + keySubject;
	
	static private final String nohatN_TS = keyName + "-NO_HAT-#-" + keyTime + "-" + keySubject;
	static private final String nohatNU_S = keyName + "-NO_HAT-" + keyUnit + "-#-" + keySubject;
	
	static private final String nohatN1TS = keyName + "-NO_HAT-" + strData1 + "-" + keyTime + "-" + keySubject;
	static private final String nohatNU1S = keyName + "-NO_HAT-" + keyUnit + "-" + strData1 + "-" + keySubject;
	static private final String nohatNUT1 = keyName + "-NO_HAT-" + keyUnit + "-" + keyTime + "-" + strData1;
	
	static private final String nohatN2TS = keyName + "-NO_HAT-" + strData2 + "-" + keyTime + "-" + keySubject;
	static private final String nohatNU2S = keyName + "-NO_HAT-" + keyUnit + "-" + strData2 + "-" + keySubject;
	static private final String nohatNUT2 = keyName + "-NO_HAT-" + keyUnit + "-" + keyTime + "-" + strData2;
	
	static private final String nohatN3TS = keyName + "-NO_HAT-" + strData3 + "-" + keyTime + "-" + keySubject;
	static private final String nohatNU3S = keyName + "-NO_HAT-" + keyUnit + "-" + strData3 + "-" + keySubject;
	static private final String nohatNUT3 = keyName + "-NO_HAT-" + keyUnit + "-" + keyTime + "-" + strData3;
	
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	static private final String testIllegalChar = " \t\n\u000B\f\r<>-,^%&?|@'\"";
	static private final String testNormalChar = "!#$()=~\\`[{;+:*]}./\\_　予約";
	
	private String createOneKeyFromChar(int index, char c) {
		String[] keys = new String[]{"#", "NO_HAT", "#", "#", "#"};
		String onekey = (index == 0 ? String.valueOf(c) : keys[0]);
		onekey = onekey + "-" + ExBase.NO_HAT;
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
			keys[i] = "#";
		}
		keys[ExBase.KEY_HAT] = ExBase.NO_HAT;
		if (index != ExBase.KEY_HAT) {
			keys[index] = String.valueOf(c);
		}
		return keys;
	}

	/**
	 * Test {@link ExBase#ExBase(String)}
	 *
	 */
	public void testExBaseString() {
		ExBase test1 = new ExBase(nohatNUTS);
		assertEquals(nohatNUTS, test1.key());
		
		ExBase test2 = new ExBase(hatNUTS);
		assertEquals(hatNUTS, test2.key());

		// Normal chars
		for (char c : testNormalChar.toCharArray()) {
			for (int i = 0; i < AbExBase.NUM_ALL_KEYS; i++) {
				if (i != ExBase.KEY_HAT) {
					String onekey = createOneKeyFromChar(i, c);
					try {
						ExBase base = new ExBase(onekey);
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
						ExBase base = new ExBase(onekey);
						fail("[" + onekey + "]:=" + base.toString());
					} catch (IllegalArgumentException ex) {
						String keyname = AbExBase.getKeyName(i);
						assertTrue(ex.getMessage().indexOf(keyname) >= 0);
					}
				}
			}
		}
		
		// 不正文字チェック('-' は除外)
		String[] normalKeys = new String[]{"name", "NO_HAT", "unit", "time", "subject"};
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
					new ExBase(ik+'-'+normalKeys[1]+'-'+normalKeys[2]+'-'+normalKeys[3]+'-'+normalKeys[4]);
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
					"*",
			};
			for (String ik : illegalHatKeys) {
				boolean coughtException;
				try {
					new ExBase(normalKeys[0]+'-'+ik+'-'+normalKeys[2]+'-'+normalKeys[3]+'-'+normalKeys[4]);
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
					new ExBase(normalKeys[0]+'-'+normalKeys[1]+'-'+ik+'-'+normalKeys[3]+'-'+normalKeys[4]);
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
					new ExBase(normalKeys[0]+'-'+normalKeys[1]+'-'+normalKeys[2]+'-'+ik+'-'+normalKeys[4]);
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
					new ExBase(normalKeys[0]+'-'+normalKeys[1]+'-'+normalKeys[2]+'-'+normalKeys[3]+'-'+ik);
					coughtException = false;
				} catch (IllegalArgumentException ex) {
					coughtException = true;
				}
				assertTrue(coughtException);
			}
		}
	}

	/**
	 * Test {@link ExBase#ExBase(String[])}
	 *
	 */
	public void testExBaseStringArray() {
		final String[] testNohatNUTS = new String[]{keyName, "NO_HAT", keyUnit, keyTime, keySubject};
		final String[] testHatNUTS = new String[]{keyName, "HAT", keyUnit, keyTime, keySubject};
		
		ExBase test1 = new ExBase(testNohatNUTS);
		assertEquals(nohatNUTS, test1.key());
		
		ExBase test2 = new ExBase(testHatNUTS);
		assertEquals(hatNUTS, test2.key());

		// Normal chars
		for (char c : testNormalChar.toCharArray()) {
			for (int i = 0; i < AbExBase.NUM_ALL_KEYS; i++) {
				if (i != ExBase.KEY_HAT) {
					String onekey = createOneKeyFromChar(i, c);
					String[] keys = createKeyArrayFromChar(i, c);
					try {
						ExBase base = new ExBase(keys);
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
						ExBase base = new ExBase(keys);
						fail(keys + ":=" + base.toString());
					} catch (IllegalArgumentException ex) {
						String keyname = AbExBase.getKeyName(i);
						assertTrue(ex.getMessage().indexOf(keyname) >= 0);
					}
				}
			}
		}
		
		// 不正文字チェック
		String[] normalKeys = new String[]{"name", "NO_HAT", "unit", "time", "subject"};
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
					new ExBase(new String[]{ik, normalKeys[1], normalKeys[2], normalKeys[3], normalKeys[4]});
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
					"*",
			};
			for (String ik : illegalHatKeys) {
				boolean coughtException;
				try {
					new ExBase(new String[]{normalKeys[0], ik, normalKeys[2], normalKeys[3], normalKeys[4]});
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
					new ExBase(new String[]{normalKeys[0], normalKeys[1], ik, normalKeys[3], normalKeys[4]});
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
					new ExBase(new String[]{normalKeys[0], normalKeys[1], normalKeys[2], ik, normalKeys[4]});
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
					new ExBase(new String[]{normalKeys[0], normalKeys[1], normalKeys[2], normalKeys[3], ik});
					coughtException = false;
				} catch (IllegalArgumentException ex) {
					coughtException = true;
				}
				assertTrue(coughtException);
			}
		}
	}

	/**
	 * Test {@link ExBase#ExBase(String, String, String[])}
	 *
	 */
	public void testExBaseStringStringStringArray() {
		final String[] data0 = new String[0];
		final String[] data1 = new String[]{keyUnit};
		final String[] data2 = new String[]{keyUnit, keyTime, keySubject, keyName};
		
		ExBase test1 = new ExBase(keyName, ExBase.NO_HAT, data0);
		assertEquals(nohatN___, test1.key());
		
		ExBase test2 = new ExBase(keyName, ExBase.HAT, data1);
		assertEquals(hatNU__, test2.key());
		
		ExBase test3 = new ExBase(keyName, ExBase.NO_HAT, data2);
		assertEquals(nohatNUTS, test3.key());
		
		// 不正文字チェック
		String[] normalKeys = new String[]{"name", "NO_HAT", "unit", "time", "subject"};
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
					new ExBase(ik, normalKeys[1], new String[]{normalKeys[2], normalKeys[3], normalKeys[4]});
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
					"*",
			};
			for (String ik : illegalHatKeys) {
				boolean coughtException;
				try {
					new ExBase(normalKeys[0], ik, new String[]{normalKeys[2], normalKeys[3], normalKeys[4]});
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
					new ExBase(normalKeys[0], normalKeys[1], new String[]{ik, normalKeys[3], normalKeys[4]});
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
					new ExBase(normalKeys[0], normalKeys[1], new String[]{normalKeys[2], ik, normalKeys[4]});
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
					new ExBase(normalKeys[0], normalKeys[1], new String[]{normalKeys[2], normalKeys[3], ik});
					coughtException = false;
				} catch (IllegalArgumentException ex) {
					coughtException = true;
				}
				assertTrue(coughtException);
			}
		}
	}

	/**
	 * Test {@link ExBase#ExBase(String, String)}
	 *
	 */
	public void testExBaseStringString() {
		ExBase test1 = new ExBase(keyName, ExBase.NO_HAT);
		assertEquals(nohatN___, test1.key());
		
		ExBase test2 = new ExBase(keyName, ExBase.HAT);
		assertEquals(hatN___, test2.key());
		
		// 不正文字チェック
		String[] normalKeys = new String[]{"name", "NO_HAT", "unit", "time", "subject"};
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
					new ExBase(ik, normalKeys[1]);
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
					"*",
			};
			for (String ik : illegalHatKeys) {
				boolean coughtException;
				try {
					new ExBase(normalKeys[0], ik);
					coughtException = false;
				} catch (IllegalArgumentException ex) {
					coughtException = true;
				}
				assertTrue(coughtException);
			}
		}
	}

	/**
	 * Test {@link ExBase#ExBase(String, String, String)}
	 *
	 */
	public void testExBaseStringStringString() {
		ExBase test1 = new ExBase(keyName, ExBase.NO_HAT, keyUnit);
		assertEquals(nohatNU__, test1.key());
		
		ExBase test2 = new ExBase(keyName, ExBase.HAT, keyUnit);
		assertEquals(hatNU__, test2.key());
		
		// 不正文字チェック
		String[] normalKeys = new String[]{"name", "NO_HAT", "unit", "time", "subject"};
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
					new ExBase(ik, normalKeys[1], normalKeys[2]);
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
					"*",
			};
			for (String ik : illegalHatKeys) {
				boolean coughtException;
				try {
					new ExBase(normalKeys[0], ik, normalKeys[2]);
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
					new ExBase(normalKeys[0], normalKeys[1], ik);
					coughtException = false;
				} catch (IllegalArgumentException ex) {
					coughtException = true;
				}
				assertTrue(coughtException);
			}
		}
	}

	/**
	 * Test {@link ExBase#ExBase(String, String, String, String)}
	 *
	 */
	public void testExBaseStringStringStringString() {
		ExBase test1 = new ExBase(keyName, ExBase.NO_HAT, keyUnit, keyTime);
		assertEquals(nohatNUT_, test1.key());
		
		ExBase test2 = new ExBase(keyName, ExBase.HAT, keyUnit, keyTime);
		assertEquals(hatNUT_, test2.key());
		
		// 不正文字チェック
		String[] normalKeys = new String[]{"name", "NO_HAT", "unit", "time", "subject"};
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
					new ExBase(ik, normalKeys[1], normalKeys[2], normalKeys[3]);
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
					"*",
			};
			for (String ik : illegalHatKeys) {
				boolean coughtException;
				try {
					new ExBase(normalKeys[0], ik, normalKeys[2], normalKeys[3]);
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
					new ExBase(normalKeys[0], normalKeys[1], ik, normalKeys[3]);
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
					new ExBase(normalKeys[0], normalKeys[1], normalKeys[2], ik);
					coughtException = false;
				} catch (IllegalArgumentException ex) {
					coughtException = true;
				}
				assertTrue(coughtException);
			}
		}
	}

	/**
	 * Test {@link ExBase#ExBase(String, String, String, String, String)}
	 *
	 */
	public void testExBaseStringStringStringStringString() {
		ExBase test1 = new ExBase(keyName, ExBase.NO_HAT, keyUnit, keyTime, keySubject);
		assertEquals(nohatNUTS, test1.key());
		
		ExBase test2 = new ExBase(keyName, ExBase.HAT, keyUnit, keyTime, keySubject);
		assertEquals(hatNUTS, test2.key());
		
		// 不正文字チェック
		String[] normalKeys = new String[]{"name", "NO_HAT", "unit", "time", "subject"};
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
					new ExBase(ik, normalKeys[1], normalKeys[2], normalKeys[3], normalKeys[4]);
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
					"*",
			};
			for (String ik : illegalHatKeys) {
				boolean coughtException;
				try {
					new ExBase(normalKeys[0], ik, normalKeys[2], normalKeys[3], normalKeys[4]);
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
					new ExBase(normalKeys[0], normalKeys[1], ik, normalKeys[3], normalKeys[4]);
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
					new ExBase(normalKeys[0], normalKeys[1], normalKeys[2], ik, normalKeys[4]);
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
					new ExBase(normalKeys[0], normalKeys[1], normalKeys[2], normalKeys[3], ik);
					coughtException = false;
				} catch (IllegalArgumentException ex) {
					coughtException = true;
				}
				assertTrue(coughtException);
			}
		}
	}

	/**
	 * Test {@link ExBase#isHat()}
	 *
	 */
	public void testIsHat() {
		ExBase test1 = new ExBase(nohatNU__);
		assertFalse(test1.isHat());
		
		ExBase test2 = new ExBase(hatNUT_);
		assertTrue(test2.isHat());
	}

	/**
	 * Test {@link ExBase#isNoHat()}
	 *
	 */
	public void testIsNoHat() {
		ExBase test1 = new ExBase(nohatN___);
		assertTrue(test1.isNoHat());
		
		ExBase test2 = new ExBase(hatNUTS);
		assertFalse(test2.isNoHat());
	}

	/**
	 * Test {@link ExBase#isExtendedKeyOmitted(ExtendedKeyID)}
	 *
	 */
	public void testIsExtendedKeyOmitted() {
		final String[] data1 = new String[]{nohatN___, hatNU__, nohatNUT_, nohatNUTS};
		final boolean[][] ret = new boolean[][]{{true,true,true}, {false,true,true},
													{false,false,true},{false,false,false}};
		
		for (int i = 0; i < data1.length; i++) {
			ExBase test = new ExBase(data1[i]);
			//--- unitKey
			assertEquals(ret[i][0], test.isExtendedKeyOmitted(ExtendedKeyID.UNIT));
			//--- timeKey
			assertEquals(ret[i][1], test.isExtendedKeyOmitted(ExtendedKeyID.TIME));
			//--- subjectKey
			assertEquals(ret[i][2], test.isExtendedKeyOmitted(ExtendedKeyID.SUBJECT));
		}
	}

	/**
	 * Test {@link ExBase#replaceUnitKey(String)}
	 *
	 */
	public void testReplaceUnitKey() {
		ExBase data = new ExBase(hatNUTS);
		assertEquals(keyUnit, data.getUnitKey());
		
		ExBase test1 = data.replaceUnitKey(null);
		assertEquals(ExBase.OMITTED, test1.getUnitKey());
		
		ExBase test2 = data.replaceUnitKey(strData2);
		assertEquals(strData2, test2.getUnitKey());
	}

	/**
	 * Test {@link ExBase#replaceTimeKey(String)}
	 *
	 */
	public void testReplaceTimeKey() {
		ExBase data = new ExBase(nohatNUTS);
		assertEquals(keyTime, data.getTimeKey());
		
		ExBase test1 = data.replaceTimeKey(null);
		assertEquals(ExBase.OMITTED, test1.getTimeKey());
		
		ExBase test2 = data.replaceTimeKey(strData3);
		assertEquals(strData3, test2.getTimeKey());
	}

	/**
	 * Test {@link ExBase#replaceExtendedKey(ExtendedKeyID, String)}
	 *
	 */
	public void testReplaceExtendedKey() {
		ExBase data = new ExBase(nohatNUTS);
		assertEquals(false, data.isExtendedKeyOmitted(ExtendedKeyID.UNIT));
		assertEquals(false, data.isExtendedKeyOmitted(ExtendedKeyID.TIME));
		assertEquals(false, data.isExtendedKeyOmitted(ExtendedKeyID.SUBJECT));
		
		Object objData[][] = new Object[][]{
				{ExtendedKeyID.UNIT, null, nohatN_TS},
				{ExtendedKeyID.UNIT, strData2, nohatN2TS},
				{ExtendedKeyID.TIME, null, nohatNU_S},
				{ExtendedKeyID.TIME, strData2, nohatNU2S},
				{ExtendedKeyID.SUBJECT, null, nohatNUT_},
				{ExtendedKeyID.SUBJECT, strData2, nohatNUT2}
		};
		
		for (int i = 0; i < objData.length; i++) {
			ExtendedKeyID id = (ExtendedKeyID)objData[i][0];
			String newKey = (String)objData[i][1];
			String retKey = (String)objData[i][2];
			
			ExBase test = data.replaceExtendedKey(id, newKey);
			assertEquals(retKey, test.key());
		}
	}

	public void testMargeExtendedKeysExBase() {
		ExBase data = new ExBase(nohatNUTS);
		assertEquals(false, data.isExtendedKeyOmitted(ExtendedKeyID.UNIT));
		assertEquals(false, data.isExtendedKeyOmitted(ExtendedKeyID.TIME));
		assertEquals(false, data.isExtendedKeyOmitted(ExtendedKeyID.SUBJECT));

		ExBase test1 = new ExBase(nohatN1TS);
		ExBase ret1 = data.margeExtendedKeys(test1);
		assertEquals(nohatN_TS, ret1.key());
		
		ExBase test2 = new ExBase(nohatNU1S);
		ExBase ret2 = data.margeExtendedKeys(test2);
		assertEquals(nohatNU_S, ret2.key());
		
		ExBase test3 = new ExBase(nohatNUT1);
		ExBase ret3 = data.margeExtendedKeys(test3);
		assertEquals(nohatNUT_, ret3.key());
	}

	public void testMargeExtendedKeysExBaseExtendedKeyIDString() {
		final String nohatTarget = keyName + "-NO_HAT-" + strData1 + "-" + strData1 + "-" + strData1;
		final String nohatN2__ = keyName + "-NO_HAT-" + strData2 + "-#-#";
		final String nohatN_2_ = keyName + "-NO_HAT-#-" + strData2 + "-#";
		final String nohatN__2 = keyName + "-NO_HAT-#-#-" + strData2;
		
		ExBase data1 = new ExBase(nohatNUTS);
		ExBase data2 = new ExBase(nohatTarget);
		assertFalse(nohatN2__.equals(data1.key()));
		assertFalse(nohatN_2_.equals(data1.key()));
		assertFalse(nohatN__2.equals(data1.key()));
		assertFalse(nohatN2__.equals(data2.key()));
		assertFalse(nohatN_2_.equals(data2.key()));
		assertFalse(nohatN__2.equals(data2.key()));

		ExBase test1 = data1.margeExtendedKeys(data2, ExtendedKeyID.UNIT, strData2);
		assertEquals(nohatN2__, test1.key());
		
		ExBase test2 = data1.margeExtendedKeys(data2, ExtendedKeyID.TIME, strData2);
		assertEquals(nohatN_2_, test2.key());
		
		ExBase test3 = data1.margeExtendedKeys(data2, ExtendedKeyID.SUBJECT, strData2);
		assertEquals(nohatN__2, test3.key());
	}

	/**
	 * Test {@link ExBase#hat()}
	 *
	 */
	public void testHat() {
		ExBase test1 = new ExBase(nohatNUTS);
		assertTrue(test1.isNoHat());
		assertFalse(test1.isHat());
		
		ExBase test2 = test1.hat();
		assertFalse(test2.isNoHat());
		assertTrue(test2.isHat());
		assertEquals(hatNUTS, test2.key());
		
		ExBase test3 = test2.hat();
		assertTrue(test3.isNoHat());
		assertFalse(test3.isHat());
		assertEquals(nohatNUTS, test3.key());
		assertTrue(test1.equals(test3));
	}

	/**
	 * Test {@link ExBase#getNumAllKeys()}
	 *
	 */
	public void testGetNumAllKeys() {
		assertEquals(ExBase.NUM_ALL_KEYS, ExBase.getNumAllKeys());
	}

	/**
	 * Test {@link ExBase#getNameKey()}
	 *
	 */
	public void testGetNameKey() {
		ExBase test1 = new ExBase(nohatNUTS);
		assertEquals(keyName, test1.getNameKey());
	}

	/**
	 * Test {@link ExBase#getHatKey()}
	 *
	 */
	public void testGetHatKey() {
		ExBase test2 = new ExBase(nohatNUTS);
		assertEquals(ExBase.NO_HAT, test2.getHatKey());
	}

	/**
	 * Test {@link ExBase#getUnitKey()}
	 *
	 */
	public void testGetUnitKey() {
		ExBase test1 = new ExBase(nohatNUTS);
		assertEquals(keyUnit, test1.getUnitKey());
	}

	/**
	 * Test {@link ExBase#getTimeKey()}
	 *
	 */
	public void testGetTimeKey() {
		ExBase test1 = new ExBase(nohatNUTS);
		assertEquals(keyTime, test1.getTimeKey());
	}

	/**
	 * Test {@link ExBase#getSubjectKey()}
	 *
	 */
	public void testGetSubjectKey() {
		ExBase test1 = new ExBase(nohatNUTS);
		assertEquals(keySubject, test1.getSubjectKey());
	}

	/**
	 * Test {@link ExBase#getExtendedKey(ExtendedKeyID)}
	 *
	 */
	public void testGetExtendedKey() {
		ExBase test = new ExBase(nohatNUTS);
		
		assertEquals(keyUnit, test.getExtendedKey(ExtendedKeyID.UNIT));
		assertEquals(keyTime, test.getExtendedKey(ExtendedKeyID.TIME));
		assertEquals(keySubject, test.getExtendedKey(ExtendedKeyID.SUBJECT));
	}

	/**
	 * Test {@link ExBase#key()}
	 *
	 */
	public void testKey() {
		ExBase test1 = new ExBase(nohatN3TS);
		assertEquals(nohatN3TS, test1.key());
		
		ExBase test2 = new ExBase(nohatNU3S);
		assertEquals(nohatNU3S, test2.key());
		
		ExBase test3 = new ExBase(nohatNUT3);
		assertEquals(nohatNUT3, test3.key());
	}

	/**
	 * Test {@link ExBase#getBasicKeysString()}
	 *
	 */
	public void testGetBasicKeysString() {
		String strBasic = keyName + "-NO_HAT";
		
		ExBase test1 = new ExBase(nohatNUTS);
		assertEquals(strBasic, test1.getBasicKeysString());
	}

	/**
	 * Test {@link ExBase#getExtendedKeysString()}
	 *
	 */
	public void testGetExtendedKeysString() {
		String strExtend = keyUnit + "-" + keyTime + "-" + keySubject;
		
		ExBase test1 = new ExBase(nohatNUTS);
		assertEquals(strExtend, test1.getExtendedKeysString());
	}
	
	/**
	 * Test {@link ExBase#hashCode()}
	 */
	public void testHashCode() {
		ExBase nohatNUTS1 = new ExBase(nohatNUTS);
		assertEquals(nohatNUTS1.hashCode(), nohatNUTS.hashCode());
		
		ExBase hatNUTS1 = new ExBase(hatNUTS);
		assertEquals(hatNUTS1.hashCode(), hatNUTS.hashCode());
		
		ExBase nohatNU_S1 = new ExBase(nohatNU_S);
		assertEquals(nohatNU_S1.hashCode(), nohatNU_S.hashCode());
	}

	/**
	 * Test {@link ExBase#equals(Object)}
	 *
	 */
	public void testEqualsObject() {
		ExBase nohatNUTS1 = new ExBase(nohatNUTS);
		ExBase nohatNUTS2 = new ExBase(nohatNUTS);
		ExBase hatNUTS1 = new ExBase(hatNUTS);
		ExBase hatNUTS2 = new ExBase(hatNUTS);
		ExBase nohatNU_S1 = new ExBase(nohatNU_S);
		ExBase nullBase = null;
		
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
	 * Test {@link ExBase#toString()}
	 * 
	 * @since 0.94
	 */
	public void testToString() {
		ExBase nohatNUTS1 = new ExBase(nohatNUTS);
		ExBase hatNUTS1   = new ExBase(hatNUTS);
		ExBase nohatNU_S1 = new ExBase(nohatNU_S);
		
		String strNohatNUTS1 = "<" + keyName + "," + keyUnit + "," + keyTime + "," + keySubject + ">";
		String strHatNUTS1   = "^<" + keyName + "," + keyUnit + "," + keyTime + "," + keySubject + ">";
		String strNohatNU_S1 = "<" + keyName + "," + keyUnit + ",#," + keySubject + ">";

		assertEquals(nohatNUTS1.toString(), strNohatNUTS1);
		assertEquals(hatNUTS1.toString(), strHatNUTS1);
		assertEquals(nohatNU_S1.toString(), strNohatNU_S1);
	}
	
	private void printCompareResult(int cmpRequest, int cmpResult, ExBase aBase, ExBase bBase) {
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
		String[] akeys = new String[]{"#","#","#","#","#"};
		String[] bkeys = new String[]{"#","#","#","#","#"};
		
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
	 * Test {@link ExBase#compareTo(ExBase)}
	 * 
	 */
	public void testCompareToExBase() {
		String[][][] strBase = new String[][][]{
				{
					{"ABB-NO_HAT", "ABB-HAT"},	// 0-0-0, 0-0-1
					{"ABC-NO_HAT", "ABC-HAT"},	// 0-1-0, 0-1-1
					{"ABD-NO_HAT", "ABD-HAT"},	// 0-2-0, 0-2-1
				},
				{
					{"ABC-NO_HAT-ABB", "ABC-HAT-ABB"},	// 1-0-0, 1-0-1
					{"ABC-NO_HAT-ABC", "ABC-HAT-ABC"},	// 1-1-0, 1-1-1
					{"ABC-NO_HAT-ABD", "ABC-HAT-ABD"},	// 1-2-0, 1-2-1
				},
				{
					{"ABC-NO_HAT-ABC-ABB", "ABC-HAT-ABC-ABB"},	// 2-0-0, 2-0-1
					{"ABC-NO_HAT-ABC-ABC", "ABC-HAT-ABC-ABC"},	// 2-1-0, 2-1-1
					{"ABC-NO_HAT-ABC-ABD", "ABC-HAT-ABC-ABD"},	// 2-2-0, 2-2-1
				},
				{
					{"ABC-NO_HAT-ABC-ABC-ABB", "ABC-HAT-ABC-ABC-ABB"},	// 3-0-0, 3-0-1
					{"ABC-NO_HAT-ABC-ABC-ABC", "ABC-HAT-ABC-ABC-ABC"},	// 3-1-0, 3-1-1
					{"ABC-NO_HAT-ABC-ABC-ABD", "ABC-HAT-ABC-ABC-ABD"},	// 3-2-0, 3-2-1
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
								ExBase aBase = new ExBase(strBase[a0][a1][a2]);
								ExBase bBase = new ExBase(strBase[b0][b1][b2]);
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
									ExBase cBase = new ExBase(strLower);
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
	 * Test {@link ExBase#setHat()}
	 * @since 0.960
	 */
	public void testSetHat() {
		ExBase nBase = new ExBase(nohatNUTS);
		ExBase hBase = new ExBase(hatNUTS);
		assertTrue(nBase.isNoHat());
		assertTrue(hBase.isHat());
		
		ExBase test1 = new ExBase(nohatNUTS);
		ExBase ret1 = test1.setHat();
		assertTrue(nBase.equals(test1));
		assertTrue(hBase.equals(ret1));
		
		ExBase test2 = new ExBase(hatNUTS);
		ExBase ret2 = test2.setHat();
		assertTrue(hBase.equals(test2));
		assertTrue(hBase.equals(ret2));
		assertSame(test2, ret2);
	}

	/**
	 * Test {@link ExBase#removeHat()}
	 * @since 0.960
	 */
	public void testRemoveHat() {
		ExBase nBase = new ExBase(nohatNUTS);
		ExBase hBase = new ExBase(hatNUTS);
		assertTrue(nBase.isNoHat());
		assertTrue(hBase.isHat());
		
		ExBase test1 = new ExBase(nohatNUTS);
		ExBase ret1 = test1.removeHat();
		assertTrue(nBase.equals(test1));
		assertTrue(nBase.equals(ret1));
		assertSame(test1, ret1);
		
		ExBase test2 = new ExBase(hatNUTS);
		ExBase ret2 = test2.removeHat();
		assertTrue(hBase.equals(test2));
		assertTrue(nBase.equals(ret2));
	}
}
