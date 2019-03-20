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
 * @(#)DtBaseSetTest.java	0.20	2010/02/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtBaseSetTest.java	0.10	2008/09/02
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;
import dtalge.util.DtDataTypes;

/**
 * <code>DtBaseSet</code> のユニットテスト。
 * このテストケースでは、ファイル入出力のテストは含まない。
 * 
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class DtBaseSetTest extends TestCase
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final String NORMAL_BASEKEY_1 = "基底キー1";
	static private final String NORMAL_BASEKEY_2 = "基底キー2";
	static private final String NORMAL_BASEKEY_3 = "基底キー3";
	static private final String NORMAL_BASEKEY_4 = "基底キー4";
	static private final String NORMAL_BASEKEY_5 = "基底キー5";
	//static private final String NORMAL_BASEKEY_MARK = "基底 !\"#$%&'()=-=^~\\|@`[{;+:*]},<.>/?_";
	static private final String NORMAL_BASEKEY_MARK = "基底#$()=~\\`[{;+:*]}./_";
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
	//static private final String DT_INTEGER_U = "INTEGER";
	//static private final String DT_INTEGER_M = "InTeGeR";
	static private final String DT_DECIMAL_U = "DECIMAL";
	static private final String DT_DECIMAL_M = "dEcImAl";
	static private final String DT_STRING_U = "STRING";
	static private final String DT_STRING_M = "StRiNg";
	
	static protected final DtBasePattern[] ignoreTypePatterns1 = {
		new DtBasePattern(DtBase.OMITTED, DtBasePattern.WILDCARD, DtBase.OMITTED, DtBase.OMITTED),
	};
	
	static protected final DtBasePattern[] ignoreTypePatterns2 = {
		new DtBasePattern(NORMAL_BASEKEY_1, DtBasePattern.WILDCARD, DtBase.OMITTED, DtBase.OMITTED),
		new DtBasePattern(NORMAL_BASEKEY_1, DtBasePattern.WILDCARD, NORMAL_BASEKEY_2, DtBase.OMITTED),
		new DtBasePattern(NORMAL_BASEKEY_1, DtBasePattern.WILDCARD, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		new DtBasePattern(NORMAL_BASEKEY_5, DtBasePattern.WILDCARD, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2),
	};
	
	static protected final DtBasePattern[] ignoreTypePatterns3 = {
		new DtBasePattern(NORMAL_BASEKEY_MARK, DtBasePattern.WILDCARD, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBasePattern(REGEXP_BASEKEY_1, DtBasePattern.WILDCARD, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
	};
	
	static protected final DtBasePattern[] basePatterns1 = {
		new DtBasePattern(DtBase.OMITTED, DtDataTypes.BOOLEAN, DtBase.OMITTED, DtBase.OMITTED),
		//new DtBasePattern(DtBase.OMITTED, DtDataTypes.INTEGER, DtBase.OMITTED, DtBase.OMITTED),
		new DtBasePattern(DtBase.OMITTED, DtDataTypes.DECIMAL, DtBase.OMITTED, DtBase.OMITTED),
		new DtBasePattern(DtBase.OMITTED, DtDataTypes.STRING, DtBase.OMITTED, DtBase.OMITTED),
	};
	
	static protected final DtBasePattern[] basePatterns2 = {
		new DtBasePattern(NORMAL_BASEKEY_1, DtDataTypes.BOOLEAN, DtBase.OMITTED, DtBase.OMITTED),
		//new DtBasePattern(NORMAL_BASEKEY_1, DtDataTypes.INTEGER, DtBase.OMITTED, DtBase.OMITTED),
		new DtBasePattern(NORMAL_BASEKEY_1, DtDataTypes.DECIMAL, DtBase.OMITTED, DtBase.OMITTED),
		new DtBasePattern(NORMAL_BASEKEY_1, DtDataTypes.STRING, DtBase.OMITTED, DtBase.OMITTED),
		new DtBasePattern(NORMAL_BASEKEY_1, DtDataTypes.BOOLEAN, NORMAL_BASEKEY_2, DtBase.OMITTED),
		//new DtBasePattern(NORMAL_BASEKEY_1, DtDataTypes.INTEGER, NORMAL_BASEKEY_2, DtBase.OMITTED),
		new DtBasePattern(NORMAL_BASEKEY_1, DtDataTypes.DECIMAL, NORMAL_BASEKEY_2, DtBase.OMITTED),
		new DtBasePattern(NORMAL_BASEKEY_1, DtDataTypes.STRING, NORMAL_BASEKEY_2, DtBase.OMITTED),
		new DtBasePattern(NORMAL_BASEKEY_1, DtDataTypes.BOOLEAN, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		//new DtBasePattern(NORMAL_BASEKEY_1, DtDataTypes.INTEGER, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		new DtBasePattern(NORMAL_BASEKEY_1, DtDataTypes.DECIMAL, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		new DtBasePattern(NORMAL_BASEKEY_1, DtDataTypes.STRING, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		new DtBasePattern(NORMAL_BASEKEY_5, DtDataTypes.BOOLEAN, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2),
		//new DtBasePattern(NORMAL_BASEKEY_5, DtDataTypes.INTEGER, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2),
		new DtBasePattern(NORMAL_BASEKEY_5, DtDataTypes.DECIMAL, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2),
		new DtBasePattern(NORMAL_BASEKEY_5, DtDataTypes.STRING, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2),
	};
	
	static protected final DtBasePattern[] basePatterns3 = {
		new DtBasePattern(NORMAL_BASEKEY_MARK, DtDataTypes.BOOLEAN, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		//new DtBasePattern(NORMAL_BASEKEY_MARK, DtDataTypes.INTEGER, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBasePattern(NORMAL_BASEKEY_MARK, DtDataTypes.DECIMAL, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBasePattern(NORMAL_BASEKEY_MARK, DtDataTypes.STRING, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBasePattern(REGEXP_BASEKEY_1, DtDataTypes.BOOLEAN, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		//new DtBasePattern(REGEXP_BASEKEY_1, DtDataTypes.INTEGER, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		new DtBasePattern(REGEXP_BASEKEY_1, DtDataTypes.DECIMAL, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		new DtBasePattern(REGEXP_BASEKEY_1, DtDataTypes.STRING, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
	};
	
	static protected final DtBase[] testBases1 = {
		new DtBase(DtBase.OMITTED, DT_BOOLEAN_M),
		//new DtBase(DtBase.OMITTED, DT_INTEGER_M),
		new DtBase(DtBase.OMITTED, DT_DECIMAL_M),
		new DtBase(DtBase.OMITTED, DT_STRING_M),
	};
	
	static protected final DtBase[] testBases2 = {
		new DtBase(NORMAL_BASEKEY_1, DT_BOOLEAN_M),
		//new DtBase(NORMAL_BASEKEY_1, DT_INTEGER_M),
		new DtBase(NORMAL_BASEKEY_1, DT_DECIMAL_M),
		new DtBase(NORMAL_BASEKEY_1, DT_STRING_M),
		new DtBase(NORMAL_BASEKEY_1, DT_BOOLEAN_M, NORMAL_BASEKEY_2),
		//new DtBase(NORMAL_BASEKEY_1, DT_INTEGER_M, NORMAL_BASEKEY_2),
		new DtBase(NORMAL_BASEKEY_1, DT_DECIMAL_M, NORMAL_BASEKEY_2),
		new DtBase(NORMAL_BASEKEY_1, DT_STRING_M, NORMAL_BASEKEY_2),
		new DtBase(NORMAL_BASEKEY_1, DT_BOOLEAN_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		//new DtBase(NORMAL_BASEKEY_1, DT_INTEGER_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		new DtBase(NORMAL_BASEKEY_1, DT_DECIMAL_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		new DtBase(NORMAL_BASEKEY_1, DT_STRING_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		new DtBase(NORMAL_BASEKEY_5, DT_BOOLEAN_M, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2),
		//new DtBase(NORMAL_BASEKEY_5, DT_INTEGER_M, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2),
		new DtBase(NORMAL_BASEKEY_5, DT_DECIMAL_M, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2),
		new DtBase(NORMAL_BASEKEY_5, DT_STRING_M, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2),
	};
	
	static protected final DtBase[] testBases3 = {
		new DtBase(NORMAL_BASEKEY_MARK, DT_BOOLEAN_U, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		//new DtBase(NORMAL_BASEKEY_MARK, DT_INTEGER_U, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBase(NORMAL_BASEKEY_MARK, DT_DECIMAL_U, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBase(NORMAL_BASEKEY_MARK, DT_STRING_U, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBase(REGEXP_BASEKEY_1, DT_BOOLEAN_U, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		//new DtBase(REGEXP_BASEKEY_1, DT_INTEGER_U, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		new DtBase(REGEXP_BASEKEY_1, DT_DECIMAL_U, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		new DtBase(REGEXP_BASEKEY_1, DT_STRING_U, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
	};
	
	static protected final DtBase[] normalAllBases = {
		new DtBase(DtBase.OMITTED, DT_BOOLEAN_M),
		//new DtBase(DtBase.OMITTED, DT_INTEGER_M),
		new DtBase(DtBase.OMITTED, DT_DECIMAL_M),
		new DtBase(DtBase.OMITTED, DT_STRING_M),
		new DtBase(NORMAL_BASEKEY_1, DT_BOOLEAN_M),
		//new DtBase(NORMAL_BASEKEY_1, DT_INTEGER_M),
		new DtBase(NORMAL_BASEKEY_1, DT_DECIMAL_M),
		new DtBase(NORMAL_BASEKEY_1, DT_STRING_M),
		new DtBase(NORMAL_BASEKEY_1, DT_BOOLEAN_M, NORMAL_BASEKEY_2),
		//new DtBase(NORMAL_BASEKEY_1, DT_INTEGER_M, NORMAL_BASEKEY_2),
		new DtBase(NORMAL_BASEKEY_1, DT_DECIMAL_M, NORMAL_BASEKEY_2),
		new DtBase(NORMAL_BASEKEY_1, DT_STRING_M, NORMAL_BASEKEY_2),
		new DtBase(NORMAL_BASEKEY_1, DT_BOOLEAN_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		//new DtBase(NORMAL_BASEKEY_1, DT_INTEGER_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		new DtBase(NORMAL_BASEKEY_1, DT_DECIMAL_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		new DtBase(NORMAL_BASEKEY_1, DT_STRING_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		new DtBase(NORMAL_BASEKEY_5, DT_BOOLEAN_M, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2),
		//new DtBase(NORMAL_BASEKEY_5, DT_INTEGER_M, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2),
		new DtBase(NORMAL_BASEKEY_5, DT_DECIMAL_M, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2),
		new DtBase(NORMAL_BASEKEY_5, DT_STRING_M, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2),
		new DtBase(NORMAL_BASEKEY_MARK, DT_BOOLEAN_U, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		//new DtBase(NORMAL_BASEKEY_MARK, DT_INTEGER_U, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBase(NORMAL_BASEKEY_MARK, DT_DECIMAL_U, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBase(NORMAL_BASEKEY_MARK, DT_STRING_U, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBase(REGEXP_BASEKEY_1, DT_BOOLEAN_U, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		//new DtBase(REGEXP_BASEKEY_1, DT_INTEGER_U, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		new DtBase(REGEXP_BASEKEY_1, DT_DECIMAL_U, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		new DtBase(REGEXP_BASEKEY_1, DT_STRING_U, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
	};
	
	static private final DtBase[] withNullAllBases = {
		new DtBase(DtBase.OMITTED, DT_BOOLEAN_M),
		//new DtBase(DtBase.OMITTED, DT_INTEGER_M),
		new DtBase(DtBase.OMITTED, DT_DECIMAL_M),
		new DtBase(DtBase.OMITTED, DT_STRING_M),
		null,
		null,
		new DtBase(NORMAL_BASEKEY_1, DT_BOOLEAN_M),
		//new DtBase(NORMAL_BASEKEY_1, DT_INTEGER_M),
		new DtBase(NORMAL_BASEKEY_1, DT_DECIMAL_M),
		new DtBase(NORMAL_BASEKEY_1, DT_STRING_M),
		new DtBase(NORMAL_BASEKEY_1, DT_BOOLEAN_M, NORMAL_BASEKEY_2),
		//new DtBase(NORMAL_BASEKEY_1, DT_INTEGER_M, NORMAL_BASEKEY_2),
		new DtBase(NORMAL_BASEKEY_1, DT_DECIMAL_M, NORMAL_BASEKEY_2),
		new DtBase(NORMAL_BASEKEY_1, DT_STRING_M, NORMAL_BASEKEY_2),
		new DtBase(NORMAL_BASEKEY_1, DT_BOOLEAN_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		//new DtBase(NORMAL_BASEKEY_1, DT_INTEGER_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		new DtBase(NORMAL_BASEKEY_1, DT_DECIMAL_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		new DtBase(NORMAL_BASEKEY_1, DT_STRING_M, NORMAL_BASEKEY_2, NORMAL_BASEKEY_3),
		new DtBase(NORMAL_BASEKEY_5, DT_BOOLEAN_M, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2),
		//new DtBase(NORMAL_BASEKEY_5, DT_INTEGER_M, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2),
		new DtBase(NORMAL_BASEKEY_5, DT_DECIMAL_M, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2),
		new DtBase(NORMAL_BASEKEY_5, DT_STRING_M, NORMAL_BASEKEY_4, NORMAL_BASEKEY_2),
		null,
		new DtBase(NORMAL_BASEKEY_MARK, DT_BOOLEAN_U, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		//new DtBase(NORMAL_BASEKEY_MARK, DT_INTEGER_U, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBase(NORMAL_BASEKEY_MARK, DT_DECIMAL_U, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBase(NORMAL_BASEKEY_MARK, DT_STRING_U, NORMAL_BASEKEY_ESC, NORMAL_BASEKEY_UNI),
		new DtBase(REGEXP_BASEKEY_1, DT_BOOLEAN_U, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		//new DtBase(REGEXP_BASEKEY_1, DT_INTEGER_U, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		new DtBase(REGEXP_BASEKEY_1, DT_DECIMAL_U, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
		new DtBase(REGEXP_BASEKEY_1, DT_STRING_U, REGEXP_BASEKEY_2, REGEXP_BASEKEY_3),
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
	
	static protected DtBase[] catBaseElements(DtBase[] baseElements, DtBase...elements) {
		int newSize = 0;
		if (baseElements != null && baseElements.length > 0) {
			newSize = baseElements.length;
		}
		if (elements != null && elements.length > 0) {
			newSize += elements.length;
		}
		
		DtBase[] newElements = new DtBase[newSize];
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
	
	static protected String[] getBaseKeyArray(int index, boolean withoutOmitted, DtBase...bases) {
		if (bases == null || bases.length <= 0) {
			return new String[0];
		}
		
		HashSet<String> s = new HashSet<String>();
		for (DtBase base : bases) {
			s.add(base._baseKeys[index]);
		}
		if (withoutOmitted) {
			s.remove(DtBase.OMITTED);
		}
		
		String[] ret = s.toArray(new String[s.size()]);
		Arrays.sort(ret);
		
		return ret;
	}
	
	static protected boolean equalElements(DtBaseSet bset, DtBase...bases) {
		if (bset == null || bases == null)
			return false;
		
		if (bset.size() != bases.length)
			return false;
		
		return (bset.containsAll(Arrays.asList(bases)));
	}
	
	static protected boolean equalElementSequence(DtBaseSet bset, DtBase...bases) {
		if (bset == null || bases == null)
			return false;
		
		if (bset.size() != bases.length)
			return false;

		int index = 0;
		for (DtBase base : bset) {
			if (!base.equals(bases[index])) {
				System.err.println("equalElementSequence : not equal DtBase : "
						+ base.toString() + " : bases[" + String.valueOf(index)
						+ "]=" + String.valueOf(bases[index]));
				return false;
			}
			index++;
		}
		
		return true;
	}
	
	static protected boolean equalStringSequence(Set<String> sset, String...elements) {
		if (sset == null || elements == null)
			return false;
		
		if (sset.size() != elements.length)
			return false;
		
		int index = 0;
		for (String s : sset) {
			if (!eq(s, elements[index])) {
				System.err.println("equalStringSequence : not equal String : ["
						+ String.valueOf(s) + "] != ([" + String.valueOf(index)
						+ "]=[" + String.valueOf(elements[index]) + "])");
				return false;
			}
			index++;
		}
		
		return true;
	}
	
	static protected boolean eq(Object val1, Object val2) {
		return (val1 == val2 || (val1 != null && val1.equals(val2)));
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
	 * {@link dtalge.DtBaseSet#DtBaseSet(java.util.Collection)} のためのテスト・メソッド。
	 */
	public void testDtBaseSetCollectionOfQextendsDtBase() {
		DtBaseSet bset;
		
		//--- null
		try {
			bset = new DtBaseSet(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		//--- new
		bset = new DtBaseSet(Arrays.asList(withNullAllBases));
		assertFalse(withNullAllBases.length == bset.size());
		assertTrue(equalElementSequence(bset, normalAllBases));
	}

	/**
	 * {@link dtalge.DtBaseSet#addition(dtalge.DtBaseSet)} のためのテスト・メソッド。
	 */
	public void testAddition() {
		DtBaseSet bset;
		DtBaseSet bempty = new DtBaseSet();
		DtBaseSet bases1 = new DtBaseSet(Arrays.asList(testBases1));
		DtBaseSet bases2 = new DtBaseSet(Arrays.asList(testBases2));
		DtBaseSet bases3 = new DtBaseSet(Arrays.asList(testBases3));
		DtBaseSet allBases = new DtBaseSet(Arrays.asList(normalAllBases));
		
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
	 * {@link dtalge.DtBaseSet#subtraction(dtalge.DtBaseSet)} のためのテスト・メソッド。
	 */
	public void testSubtraction() {
		DtBase[] testBases13 = catBaseElements(testBases1, testBases3);
		DtBaseSet bset;
		DtBaseSet bempty = new DtBaseSet();
		DtBaseSet bases1 = new DtBaseSet(Arrays.asList(testBases1));
		DtBaseSet bases2 = new DtBaseSet(Arrays.asList(testBases2));
		DtBaseSet bases3 = new DtBaseSet(Arrays.asList(testBases3));
		DtBaseSet allBases = new DtBaseSet(Arrays.asList(normalAllBases));
		
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
	 * {@link dtalge.DtBaseSet#retention(dtalge.DtBaseSet)} のためのテスト・メソッド。
	 */
	public void testRetention() {
		DtBase[] testBases13 = catBaseElements(testBases1, testBases3);
		DtBaseSet bset;
		DtBaseSet bempty = new DtBaseSet();
		DtBaseSet bases1 = new DtBaseSet(Arrays.asList(testBases1));
		DtBaseSet bases2 = new DtBaseSet(Arrays.asList(testBases2));
		DtBaseSet bases3 = new DtBaseSet(Arrays.asList(testBases3));
		DtBaseSet bases13 = new DtBaseSet(Arrays.asList(testBases13));
		DtBaseSet allBases = new DtBaseSet(Arrays.asList(normalAllBases));
		
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
	 * {@link dtalge.DtBaseSet#union(dtalge.DtBaseSet)} のためのテスト・メソッド。
	 */
	public void testUnion() {
		DtBaseSet bset;
		DtBaseSet bempty = new DtBaseSet();
		DtBaseSet bases1 = new DtBaseSet(Arrays.asList(testBases1));
		DtBaseSet bases2 = new DtBaseSet(Arrays.asList(testBases2));
		DtBaseSet bases3 = new DtBaseSet(Arrays.asList(testBases3));
		DtBaseSet allBases = new DtBaseSet(Arrays.asList(normalAllBases));
		
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
	 * {@link dtalge.DtBaseSet#intersection(dtalge.DtBaseSet)} のためのテスト・メソッド。
	 */
	public void testIntersection() {
		DtBase[] testBases13 = catBaseElements(testBases1, testBases3);
		DtBaseSet bset;
		DtBaseSet bempty = new DtBaseSet();
		DtBaseSet bases1 = new DtBaseSet(Arrays.asList(testBases1));
		DtBaseSet bases2 = new DtBaseSet(Arrays.asList(testBases2));
		DtBaseSet bases3 = new DtBaseSet(Arrays.asList(testBases3));
		DtBaseSet bases13 = new DtBaseSet(Arrays.asList(testBases13));
		DtBaseSet allBases = new DtBaseSet(Arrays.asList(normalAllBases));
		
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
	 * {@link dtalge.DtBaseSet#difference(dtalge.DtBaseSet)} のためのテスト・メソッド。
	 */
	public void testDifference() {
		DtBase[] testBases13 = catBaseElements(testBases1, testBases3);
		DtBaseSet bset;
		DtBaseSet bempty = new DtBaseSet();
		DtBaseSet bases1 = new DtBaseSet(Arrays.asList(testBases1));
		DtBaseSet bases2 = new DtBaseSet(Arrays.asList(testBases2));
		DtBaseSet bases3 = new DtBaseSet(Arrays.asList(testBases3));
		DtBaseSet allBases = new DtBaseSet(Arrays.asList(normalAllBases));
		
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
	 * {@link dtalge.DtBaseSet#getMatchedBases(dtalge.DtBasePattern)} のためのテスト・メソッド。
	 */
	public void testGetMatchedBasesDtBasePattern() {
		DtBasePattern pattern = new DtBasePattern("*", DtDataTypes.DECIMAL, "*", "*");
		DtBaseSet matchedBases = new DtBaseSet();
		for (DtBase eBase : normalAllBases) {
			if (DtDataTypes.DECIMAL.equals(eBase.getTypeKey())) {
				matchedBases.add(eBase);
			}
		}
		DtBase[] testMatchedBases = matchedBases.toArray(new DtBase[matchedBases.size()]);
		DtBaseSet bases0 = new DtBaseSet();
		DtBaseSet bases1 = new DtBaseSet(Arrays.asList(normalAllBases));
		assertTrue(bases0.isEmpty());
		assertTrue(equalElementSequence(bases1, normalAllBases));
		
		//--- null
		DtBaseSet ret;
		try {
			ret = bases0.getMatchedBases((DtBasePattern)null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			ret = bases1.getMatchedBases((DtBasePattern)null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		//--- empty
		ret = bases0.getMatchedBases(pattern);
		assertTrue(ret.isEmpty());
		
		//--- exist
		ret = bases1.getMatchedBases(pattern);
		assertFalse(ret.isEmpty());
		assertTrue(equalElementSequence(bases1, normalAllBases));
		assertTrue(equalElements(ret, testMatchedBases));
		assertTrue(equalElementSequence(ret, testMatchedBases));
	}

	/**
	 * {@link dtalge.DtBaseSet#getMatchedBases(dtalge.DtBasePatternSet)} のためのテスト・メソッド。
	 */
	public void testGetMatchedBasesDtBasePatternSet() {
		DtBasePatternSet pset = new DtBasePatternSet();
		pset.add(new DtBasePattern("*", DtDataTypes.DECIMAL, "*", "*"));
		pset.add(new DtBasePattern("*", DtDataTypes.STRING, "*", "*"));
		DtBaseSet matchedBases = new DtBaseSet();
		for (DtBase eBase : normalAllBases) {
			if (DtDataTypes.DECIMAL.equals(eBase.getTypeKey())) {
				matchedBases.add(eBase);
			}
			else if (DtDataTypes.STRING.equals(eBase.getTypeKey())) {
				matchedBases.add(eBase);
			}
		}
		DtBase[] testMatchedBases = matchedBases.toArray(new DtBase[matchedBases.size()]);
		DtBaseSet bases0 = new DtBaseSet();
		DtBaseSet bases1 = new DtBaseSet(Arrays.asList(normalAllBases));
		assertTrue(bases0.isEmpty());
		assertTrue(equalElementSequence(bases1, normalAllBases));
		
		//--- null
		DtBaseSet ret;
		try {
			ret = bases0.getMatchedBases((DtBasePatternSet)null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			ret = bases1.getMatchedBases((DtBasePatternSet)null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		//--- empty
		ret = bases0.getMatchedBases(pset);
		assertTrue(ret.isEmpty());
		
		//--- exist
		ret = bases1.getMatchedBases(pset);
		assertFalse(ret.isEmpty());
		assertTrue(equalElementSequence(bases1, normalAllBases));
		assertTrue(equalElements(ret, testMatchedBases));
		assertTrue(equalElementSequence(ret, testMatchedBases));
	}

	/**
	 * {@link dtalge.DtBaseSet#add(dtalge.DtBase)} のためのテスト・メソッド。
	 */
	public void testAddDtBase() {
		DtBaseSet bset;
		
		//--- null
		bset = new DtBaseSet();
		try {
			bset.add(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		bset = new DtBaseSet(Arrays.asList(normalAllBases));
		try {
			bset.add(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		bset = new DtBaseSet();
		for (DtBase base : testBases3) {
			assertTrue(bset.add(base));
		}
		assertTrue(equalElements(bset, testBases3));
		assertTrue(equalElementSequence(bset, testBases3));
		
		for (DtBase base : testBases3) {
			assertFalse(bset.add(base));
		}
		assertTrue(equalElements(bset, testBases3));
		assertTrue(equalElementSequence(bset, testBases3));
	}

	/**
	 * {@link dtalge.DtBaseSet#addAll(java.util.Collection)} のためのテスト・メソッド。
	 */
	public void testAddAllCollectionOfQextendsDtBase() {
		DtBaseSet bset;
		
		//--- null
		bset = new DtBaseSet();
		try {
			bset.addAll(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		bset = new DtBaseSet(Arrays.asList(normalAllBases));
		try {
			bset.addAll(null);
			fail("must be throw NullPointerException.");
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		//--- parameters
		DtBase[] testBases13 = catBaseElements(catBaseElements(testBases1, testBases3));
		DtBase[] testBases132 = catBaseElements(testBases13, testBases2);
		
		//--- new
		DtBaseSet bases1 = new DtBaseSet();
		assertTrue(bases1.isEmpty());
		assertTrue(bases1.addAll(Arrays.asList(testBases1)));
		assertTrue(equalElements(bases1, testBases1));
		assertTrue(equalElementSequence(bases1, testBases1));
		assertTrue(bases1.addAll(Arrays.asList(testBases3)));
		assertTrue(bases1.addAll(Arrays.asList(testBases2)));
		assertTrue(equalElements(bases1, testBases132));
		assertTrue(equalElementSequence(bases1, testBases132));
		
		DtBaseSet bases2 = new DtBaseSet();
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
	 * {@link dtalge.DtBaseSet#clone()} のためのテスト・メソッド。
	 */
	public void testClone() {
		DtBaseSet bases1 = new DtBaseSet();
		DtBaseSet clone1 = (DtBaseSet)bases1.clone();
		assertNotSame(bases1, clone1);
		assertEquals(bases1, clone1);
		assertTrue(bases1.isEmpty());
		assertTrue(clone1.isEmpty());
		
		DtBaseSet bases2 = new DtBaseSet(Arrays.asList(testBases3));
		assertTrue(equalElementSequence(bases2, testBases3));
		DtBaseSet clone2 = (DtBaseSet)bases2.clone();
		assertNotSame(bases2, clone2);
		assertEquals(bases2, clone2);
		assertTrue(equalElementSequence(bases2, testBases3));
		assertTrue(equalElementSequence(clone2, testBases3));
		
		DtBase[] testBases31 = catBaseElements(testBases3, testBases1);
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
		DtBaseSet eset = new DtBaseSet();
		Set<String> es1 = eset.getBaseNameKeySet(false);
		assertTrue(es1.isEmpty());
		Set<String> es2 = eset.getBaseNameKeySet(true);
		assertTrue(es2.isEmpty());
		
		String[] ans1 = getBaseKeyArray(DtBase.KEY_NAME, false, normalAllBases);
		String[] ans2 = getBaseKeyArray(DtBase.KEY_NAME, true , normalAllBases);
		
		DtBaseSet bset = new DtBaseSet(Arrays.asList(normalAllBases));
		assertTrue(equalElements(bset, normalAllBases));
		Set<String> bs1 = bset.getBaseNameKeySet(false);
		Set<String> bs2 = bset.getBaseNameKeySet(true);
		assertTrue(equalStringSequence(bs1, ans1));
		assertTrue(equalStringSequence(bs2, ans2));
	}

	/**
	 * {@link dtalge.AbDtBaseSet#getBaseTypeKeySet(boolean)} のためのテスト・メソッド。
	 */
	public void testGetBaseTypeKeySet() {
		DtBaseSet eset = new DtBaseSet();
		Set<String> es1 = eset.getBaseTypeKeySet(false);
		assertTrue(es1.isEmpty());
		Set<String> es2 = eset.getBaseTypeKeySet(true);
		assertTrue(es2.isEmpty());
		
		String[] ans1 = getBaseKeyArray(DtBase.KEY_TYPE, false, normalAllBases);
		String[] ans2 = getBaseKeyArray(DtBase.KEY_TYPE, true , normalAllBases);
		
		DtBaseSet bset = new DtBaseSet(Arrays.asList(normalAllBases));
		assertTrue(equalElements(bset, normalAllBases));
		Set<String> bs1 = bset.getBaseTypeKeySet(false);
		Set<String> bs2 = bset.getBaseTypeKeySet(true);
		assertTrue(equalStringSequence(bs1, ans1));
		assertTrue(equalStringSequence(bs2, ans2));
	}

	/**
	 * {@link dtalge.AbDtBaseSet#getBaseAttributeKeySet(boolean)} のためのテスト・メソッド。
	 */
	public void testGetBaseAttributeKeySet() {
		DtBaseSet eset = new DtBaseSet();
		Set<String> es1 = eset.getBaseAttributeKeySet(false);
		assertTrue(es1.isEmpty());
		Set<String> es2 = eset.getBaseAttributeKeySet(true);
		assertTrue(es2.isEmpty());
		
		String[] ans1 = getBaseKeyArray(DtBase.KEY_ATTR, false, normalAllBases);
		String[] ans2 = getBaseKeyArray(DtBase.KEY_ATTR, true , normalAllBases);
		
		DtBaseSet bset = new DtBaseSet(Arrays.asList(normalAllBases));
		assertTrue(equalElements(bset, normalAllBases));
		Set<String> bs1 = bset.getBaseAttributeKeySet(false);
		Set<String> bs2 = bset.getBaseAttributeKeySet(true);
		assertTrue(equalStringSequence(bs1, ans1));
		assertTrue(equalStringSequence(bs2, ans2));
	}

	/**
	 * {@link dtalge.AbDtBaseSet#getBaseSubjectKeySet(boolean)} のためのテスト・メソッド。
	 */
	public void testGetBaseSubjectKeySet() {
		DtBaseSet eset = new DtBaseSet();
		Set<String> es1 = eset.getBaseSubjectKeySet(false);
		assertTrue(es1.isEmpty());
		Set<String> es2 = eset.getBaseSubjectKeySet(true);
		assertTrue(es2.isEmpty());
		
		String[] ans1 = getBaseKeyArray(DtBase.KEY_SUBJECT, false, normalAllBases);
		String[] ans2 = getBaseKeyArray(DtBase.KEY_SUBJECT, true , normalAllBases);
		
		DtBaseSet bset = new DtBaseSet(Arrays.asList(normalAllBases));
		assertTrue(equalElements(bset, normalAllBases));
		Set<String> bs1 = bset.getBaseSubjectKeySet(false);
		Set<String> bs2 = bset.getBaseSubjectKeySet(true);
		assertTrue(equalStringSequence(bs1, ans1));
		assertTrue(equalStringSequence(bs2, ans2));
	}
}
