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
 *  Copyright 2007-2011  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)AADLRangeFunctionsTest.java	1.70	2011/05/17
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime;

import java.math.BigDecimal;

import ssac.aadl.runtime.util.range.NaturalNumberDecimalRange;
import ssac.aadl.runtime.util.range.SimpleDecimalRange;
import ssac.aadl.runtime.util.range.NaturalNumberDecimalRangeTest.VerifyNaturalRangePattern;
import ssac.aadl.runtime.util.range.SimpleDecimalRangeTest.VerifyRangePattern;

import static ssac.aadl.runtime.util.range.SimpleDecimalRangeTest.rangePatterns;
import static ssac.aadl.runtime.util.range.NaturalNumberDecimalRangeTest.naturalRangePatterns;

import junit.framework.TestCase;

/**
 * {@link ssac.aadl.runtime.AADLFunctions} クラスのテスト。
 * 
 * @version 1.70	2011/05/17
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * 
 * @since 1.70
 */
public class AADLRangeFunctionsTest extends TestCase
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static protected BigDecimal toDecimal(long value) {
		return BigDecimal.valueOf(value);
	}
	
	static protected BigDecimal toDecimal(String value) {
		return new BigDecimal(value);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	//------------------------------------------------------------
	// Test Cases for 1.70
	//------------------------------------------------------------

	/**
	 * Test method for {@link ssac.aadl.runtime.AADLFunctions#range(java.math.BigDecimal)}.
	 */
	public void testRangeBigDecimal() {
		// check null
		try {
			AADLFunctions.range(null);
			fail("Must be throw NullPointerException.");
		} catch (NullPointerException ex) {}
		
		// check new instances
		for (int i = 0; i < rangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyRangePattern pat = rangePatterns[i];
			SimpleDecimalRange range = AADLFunctions.range(pat.to);
			assertEquals(msg, false, range.isIncludeRangeLast());
			assertEquals(msg, 0, BigDecimal.ZERO.compareTo(range.rangeFirst()));
			assertEquals(msg, 0, pat.to  .compareTo(range.rangeLast()));
			if (pat.to.signum() >= 0) {
				assertEquals(msg, 0, BigDecimal.ONE.compareTo(range.rangeStep()));
			} else {
				assertEquals(msg, 0, BigDecimal.ONE.negate().compareTo(range.rangeStep()));
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.AADLFunctions#range(java.math.BigDecimal, boolean)}.
	 */
	public void testRangeBigDecimalBoolean() {
		// check null
		try {
			AADLFunctions.range(null, true);
			fail("Must be throw NullPointerException.");
		} catch (NullPointerException ex) {}
		
		// check new instances
		for (int i = 0; i < rangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyRangePattern pat = rangePatterns[i];
			SimpleDecimalRange range = AADLFunctions.range(pat.to, pat.includeLast);
			assertEquals(msg, pat.includeLast, range.isIncludeRangeLast());
			assertEquals(msg, 0, BigDecimal.ZERO.compareTo(range.rangeFirst()));
			assertEquals(msg, 0, pat.to  .compareTo(range.rangeLast()));
			if (pat.to.signum() >= 0) {
				assertEquals(msg, 0, BigDecimal.ONE.compareTo(range.rangeStep()));
			} else {
				assertEquals(msg, 0, BigDecimal.ONE.negate().compareTo(range.rangeStep()));
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.AADLFunctions#range(java.math.BigDecimal, java.math.BigDecimal)}.
	 */
	public void testRangeBigDecimalBigDecimal() {
		// check null
		try {
			AADLFunctions.range(null, null);
			fail("Must be throw NullPointerException.");
		} catch (NullPointerException ex) {}
		try {
			AADLFunctions.range(null, BigDecimal.ONE);
			fail("Must be throw NullPointerException.");
		} catch (NullPointerException ex) {}
		try {
			AADLFunctions.range(BigDecimal.ZERO, null);
			fail("Must be throw NullPointerException.");
		} catch (NullPointerException ex) {}
		
		// check new instances
		for (int i = 0; i < rangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyRangePattern pat = rangePatterns[i];
			SimpleDecimalRange range = AADLFunctions.range(pat.from, pat.to);
			assertEquals(msg, false, range.isIncludeRangeLast());
			assertEquals(msg, 0, pat.from.compareTo(range.rangeFirst()));
			assertEquals(msg, 0, pat.to  .compareTo(range.rangeLast()));
			if (pat.to.subtract(pat.from).signum() >= 0) {
				assertEquals(msg, 0, BigDecimal.ONE.compareTo(range.rangeStep()));
			} else {
				assertEquals(msg, 0, BigDecimal.ONE.negate().compareTo(range.rangeStep()));
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.AADLFunctions#range(java.math.BigDecimal, java.math.BigDecimal, boolean)}.
	 */
	public void testRangeBigDecimalBigDecimalBoolean() {
		// check null
		try {
			AADLFunctions.range(null, null, true);
			fail("Must be throw NullPointerException.");
		} catch (NullPointerException ex) {}
		try {
			AADLFunctions.range(null, BigDecimal.ONE, false);
			fail("Must be throw NullPointerException.");
		} catch (NullPointerException ex) {}
		try {
			AADLFunctions.range(BigDecimal.ZERO, null, true);
			fail("Must be throw NullPointerException.");
		} catch (NullPointerException ex) {}
		
		// check new instances
		for (int i = 0; i < rangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyRangePattern pat = rangePatterns[i];
			SimpleDecimalRange range = AADLFunctions.range(pat.from, pat.to, pat.includeLast);
			assertEquals(msg, pat.includeLast, range.isIncludeRangeLast());
			assertEquals(msg, 0, pat.from.compareTo(range.rangeFirst()));
			assertEquals(msg, 0, pat.to  .compareTo(range.rangeLast()));
			if (pat.to.subtract(pat.from).signum() >= 0) {
				assertEquals(msg, 0, BigDecimal.ONE.compareTo(range.rangeStep()));
			} else {
				assertEquals(msg, 0, BigDecimal.ONE.negate().compareTo(range.rangeStep()));
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.AADLFunctions#range(java.math.BigDecimal, java.math.BigDecimal, java.math.BigDecimal)}.
	 */
	public void testRangeBigDecimalBigDecimalBigDecimal() {
		// check null
		try {
			AADLFunctions.range(null, null, null);
			fail("Must be throw NullPointerException.");
		} catch (NullPointerException ex) {}
		try {
			AADLFunctions.range(BigDecimal.ZERO, BigDecimal.ONE, null);
			fail("Must be throw NullPointerException.");
		} catch (NullPointerException ex) {}
		try {
			AADLFunctions.range(null, BigDecimal.ONE, BigDecimal.ONE);
			fail("Must be throw NullPointerException.");
		} catch (NullPointerException ex) {}
		try {
			AADLFunctions.range(BigDecimal.ZERO, null, BigDecimal.ONE);
			fail("Must be throw NullPointerException.");
		} catch (NullPointerException ex) {}
		
		// check new instances
		for (int i = 0; i < rangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyRangePattern pat = rangePatterns[i];
			SimpleDecimalRange range = AADLFunctions.range(pat.from, pat.to, pat.step);
			assertEquals(msg, false, range.isIncludeRangeLast());
			assertEquals(msg, 0, pat.from.compareTo(range.rangeFirst()));
			assertEquals(msg, 0, pat.to  .compareTo(range.rangeLast()));
			assertEquals(msg, 0, pat.step.compareTo(range.rangeStep()));
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.AADLFunctions#range(java.math.BigDecimal, java.math.BigDecimal, java.math.BigDecimal, boolean)}.
	 */
	public void testRangeBigDecimalBigDecimalBigDecimalBoolean() {
		// check null
		try {
			AADLFunctions.range(null, null, null, true);
			fail("Must be throw NullPointerException.");
		} catch (NullPointerException ex) {}
		try {
			AADLFunctions.range(BigDecimal.ZERO, BigDecimal.ONE, null, false);
			fail("Must be throw NullPointerException.");
		} catch (NullPointerException ex) {}
		try {
			AADLFunctions.range(null, BigDecimal.ONE, BigDecimal.ONE, true);
			fail("Must be throw NullPointerException.");
		} catch (NullPointerException ex) {}
		try {
			AADLFunctions.range(BigDecimal.ZERO, null, BigDecimal.ONE, false);
			fail("Must be throw NullPointerException.");
		} catch (NullPointerException ex) {}
		
		// check new instances
		for (int i = 0; i < rangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyRangePattern pat = rangePatterns[i];
			SimpleDecimalRange range = AADLFunctions.range(pat.from, pat.to, pat.step, pat.includeLast);
			assertEquals(msg, pat.includeLast, range.isIncludeRangeLast());
			assertEquals(msg, 0, pat.from.compareTo(range.rangeFirst()));
			assertEquals(msg, 0, pat.to  .compareTo(range.rangeLast()));
			assertEquals(msg, 0, pat.step.compareTo(range.rangeStep()));
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.AADLFunctions#naturalNumberRange(java.lang.String)}.
	 */
	public void testNaturalNumberRangeString() {
		NaturalNumberDecimalRange range;
		
		// null
		range = AADLFunctions.naturalNumberRange(null);
		assertTrue(range.isEmpty());
		
		// empty
		range = AADLFunctions.naturalNumberRange("");
		assertTrue(range.isEmpty());
		
		// illegal range
		try {
			range = AADLFunctions.naturalNumberRange("-3,5,7,-,10-13,20-");
			fail("Must be throw IllegalArgumentException.");
		}
		catch (IllegalArgumentException ex) {
			System.out.println("IllegalArgument position=8(7+1) : " + ex.toString());
			assertTrue(true);
		}
		
		// valid range
		range = AADLFunctions.naturalNumberRange("-3,5,7,10-13,20-");
		assertFalse(range.isEmpty());
		assertEquals(0, toDecimal(1).compareTo(range.rangeFirst()));
		assertEquals(0, toDecimal(Long.MAX_VALUE).compareTo(range.rangeLast()));
		assertEquals(0, toDecimal(1).compareTo(range.rangeStep()));
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.AADLFunctions#naturalNumberRange(java.lang.String, java.math.BigDecimal)}.
	 */
	public void testNaturalNumberRangeStringBigDecimal() {
		BigDecimal maxValue;
		String validString = "-3,5,7,10-13,20-";
		NaturalNumberDecimalRange range;
		
		// null max value
		range = AADLFunctions.naturalNumberRange(validString, null);
		assertEquals(0, toDecimal(1).compareTo(range.rangeFirst()));
		assertEquals(0, toDecimal(Long.MAX_VALUE).compareTo(range.rangeLast()));
		assertEquals(0, toDecimal(1).compareTo(range.rangeStep()));
		
		// illegal max value
		maxValue = BigDecimal.valueOf(Integer.MIN_VALUE);
		try {
			range = AADLFunctions.naturalNumberRange(validString, maxValue);
			fail("Must be throw IllegalArgumentException.");
		}
		catch (IllegalArgumentException ex) {
			System.out.println("IllegalArgument maxValue=" + maxValue.toPlainString() + " : " + ex.toString());
		}
		//---
		maxValue = new BigDecimal("123456.0000000000000000000000000000001");
		try {
			range = AADLFunctions.naturalNumberRange(validString, maxValue);
			fail("Must be throw IllegalArgumentException.");
		}
		catch (IllegalArgumentException ex) {
			System.out.println("IllegalArgument maxValue=" + maxValue.toPlainString() + " : " + ex.toString());
		}
		
		// null range string
		range = AADLFunctions.naturalNumberRange(null, BigDecimal.valueOf(Integer.MAX_VALUE));
		assertTrue(range.isEmpty());
		
		// empty range string
		range = AADLFunctions.naturalNumberRange("", BigDecimal.valueOf(Integer.MAX_VALUE));
		assertTrue(range.isEmpty());
		
		// illegal range string
		try {
			range = AADLFunctions.naturalNumberRange("-3,5,7,-,10-13,20-", BigDecimal.valueOf(Integer.MAX_VALUE));
			fail("Must be throw IllegalArgumentException.");
		}
		catch (IllegalArgumentException ex) {
			System.out.println("IllegalArgument position=8(7+1) : " + ex.toString());
			assertTrue(true);
		}
		try {
			range = AADLFunctions.naturalNumberRange("-3,5,8 9,10-13,20-", BigDecimal.valueOf(Integer.MAX_VALUE));
			fail("Must be throw IllegalArgumentException.");
		}
		catch (IllegalArgumentException ex) {
			System.out.println("IllegalArgument position=6(5+1) : " + ex.toString());
			assertTrue(true);
		}
		
		// valid range
		for (int i = 0; i < naturalRangePatterns.length; i++) {
			VerifyNaturalRangePattern pat = naturalRangePatterns[i];
			String msg = String.format("Test params : [%d] ", i);
			range = AADLFunctions.naturalNumberRange(pat.rangeString, pat.maxValue);
			assertEquals(msg, 0, pat.from.compareTo(range.rangeFirst()));
			assertEquals(msg, 0, pat.to  .compareTo(range.rangeLast()));
			assertEquals(msg, 0, toDecimal(1).compareTo(range.rangeStep()));
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.AADLFunctions#isNaturalNumberRangeFormat(java.lang.String)}.
	 */
	public void testIsNaturalNumberRangeFormat() {
		// illegal range string
		assertFalse(AADLFunctions.isNaturalNumberRangeFormat("-3,5,7,-,10-13,20-"));
		assertFalse(AADLFunctions.isNaturalNumberRangeFormat("-3,5,8 9,10-13,20-"));
		
		// valid range string
		assertTrue(AADLFunctions.isNaturalNumberRangeFormat(null));
		assertTrue(AADLFunctions.isNaturalNumberRangeFormat(""));
		for (int i = 0; i < naturalRangePatterns.length; i++) {
			VerifyNaturalRangePattern pat = naturalRangePatterns[i];
			assertTrue(AADLFunctions.isNaturalNumberRangeFormat(pat.rangeString));
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.AADLFunctions#isEmpty(ssac.aadl.runtime.util.range.Range)}.
	 */
	public void testIsEmpty() {
		try {
			AADLFunctions.isEmpty((SimpleDecimalRange)null);
			fail("Must be throw NullPointerException.");
		} catch (NullPointerException ex) {}
		
		for (int i = 0; i < rangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyRangePattern pat = rangePatterns[i];
			SimpleDecimalRange range = AADLFunctions.range(pat.from, pat.to, pat.step, pat.includeLast);
			assertEquals(msg, pat.includeLast, range.isIncludeRangeLast());
			assertEquals(msg, 0, pat.from.compareTo(range.rangeFirst()));
			assertEquals(msg, 0, pat.to  .compareTo(range.rangeLast()));
			assertEquals(msg, 0, pat.step.compareTo(range.rangeStep()));
			
			assertEquals(msg, range.isEmpty(), AADLFunctions.isEmpty(range));
		}
		
		for (int i = 0; i < naturalRangePatterns.length; i++) {
			VerifyNaturalRangePattern pat = naturalRangePatterns[i];
			String msg = String.format("Test params : [%d] ", i);
			NaturalNumberDecimalRange range = AADLFunctions.naturalNumberRange(pat.rangeString, pat.maxValue);
			assertEquals(msg, 0, pat.from.compareTo(range.rangeFirst()));
			assertEquals(msg, 0, pat.to  .compareTo(range.rangeLast()));
			assertEquals(msg, 0, toDecimal(1).compareTo(range.rangeStep()));
			
			assertEquals(msg, range.isEmpty(), AADLFunctions.isEmpty(range));
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.AADLFunctions#isIncludeRangeLast(ssac.aadl.runtime.util.range.Range)}.
	 */
	public void testIsIncludeRangeLast() {
		try {
			AADLFunctions.isIncludeRangeLast(null);
			fail("Must be throw NullPointerException.");
		} catch (NullPointerException ex) {}
		
		for (int i = 0; i < rangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyRangePattern pat = rangePatterns[i];
			SimpleDecimalRange range = AADLFunctions.range(pat.from, pat.to, pat.step, pat.includeLast);
			assertEquals(msg, pat.includeLast, range.isIncludeRangeLast());
			assertEquals(msg, 0, pat.from.compareTo(range.rangeFirst()));
			assertEquals(msg, 0, pat.to  .compareTo(range.rangeLast()));
			assertEquals(msg, 0, pat.step.compareTo(range.rangeStep()));
			
			assertEquals(msg, range.isIncludeRangeLast(), AADLFunctions.isIncludeRangeLast(range));
		}
		
		for (int i = 0; i < naturalRangePatterns.length; i++) {
			VerifyNaturalRangePattern pat = naturalRangePatterns[i];
			String msg = String.format("Test params : [%d] ", i);
			NaturalNumberDecimalRange range = AADLFunctions.naturalNumberRange(pat.rangeString, pat.maxValue);
			assertEquals(msg, 0, pat.from.compareTo(range.rangeFirst()));
			assertEquals(msg, 0, pat.to  .compareTo(range.rangeLast()));
			assertEquals(msg, 0, toDecimal(1).compareTo(range.rangeStep()));
			
			assertEquals(msg, range.isIncludeRangeLast(), AADLFunctions.isIncludeRangeLast(range));
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.AADLFunctions#isIncremental(ssac.aadl.runtime.util.range.Range)}.
	 */
	public void testIsIncremental() {
		try {
			AADLFunctions.isIncremental(null);
			fail("Must be throw NullPointerException.");
		} catch (NullPointerException ex) {}
		
		for (int i = 0; i < rangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyRangePattern pat = rangePatterns[i];
			SimpleDecimalRange range = AADLFunctions.range(pat.from, pat.to, pat.step, pat.includeLast);
			assertEquals(msg, pat.includeLast, range.isIncludeRangeLast());
			assertEquals(msg, 0, pat.from.compareTo(range.rangeFirst()));
			assertEquals(msg, 0, pat.to  .compareTo(range.rangeLast()));
			assertEquals(msg, 0, pat.step.compareTo(range.rangeStep()));
			
			assertEquals(msg, range.isIncremental(), AADLFunctions.isIncremental(range));
		}
		
		for (int i = 0; i < naturalRangePatterns.length; i++) {
			VerifyNaturalRangePattern pat = naturalRangePatterns[i];
			String msg = String.format("Test params : [%d] ", i);
			NaturalNumberDecimalRange range = AADLFunctions.naturalNumberRange(pat.rangeString, pat.maxValue);
			assertEquals(msg, 0, pat.from.compareTo(range.rangeFirst()));
			assertEquals(msg, 0, pat.to  .compareTo(range.rangeLast()));
			assertEquals(msg, 0, toDecimal(1).compareTo(range.rangeStep()));
			
			assertEquals(msg, range.isIncremental(), AADLFunctions.isIncremental(range));
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.AADLFunctions#containsValue(ssac.aadl.runtime.util.range.Range, java.lang.Object)}.
	 */
	public void testContainsValue() {
		try {
			AADLFunctions.containsValue(null, "hoge");
			fail("Must be throw NullPointerException.");
		} catch (NullPointerException ex) {}
		
		for (int i = 0; i < rangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyRangePattern pat = rangePatterns[i];
			SimpleDecimalRange range = AADLFunctions.range(pat.from, pat.to, pat.step, pat.includeLast);
			assertEquals(msg, pat.includeLast, range.isIncludeRangeLast());
			assertEquals(msg, 0, pat.from.compareTo(range.rangeFirst()));
			assertEquals(msg, 0, pat.to  .compareTo(range.rangeLast()));
			assertEquals(msg, 0, pat.step.compareTo(range.rangeStep()));

			Object[] con = pat.contains;
			Object[] ni  = pat.notcontains;
			//--- includes
			for (Object v : con) {
				assertTrue(msg + " value=" + v, AADLFunctions.containsValue(range, v));
			}
			//--- not includes
			for (Object v : ni) {
				assertFalse(msg + " value=" + v, AADLFunctions.containsValue(range, v));
			}
			assertFalse(msg + " value=null", AADLFunctions.containsValue(range, null));
		}
		
		for (int i = 0; i < naturalRangePatterns.length; i++) {
			VerifyNaturalRangePattern pat = naturalRangePatterns[i];
			String msg = String.format("Test params : [%d] ", i);
			NaturalNumberDecimalRange range = AADLFunctions.naturalNumberRange(pat.rangeString, pat.maxValue);
			assertEquals(msg, 0, pat.from.compareTo(range.rangeFirst()));
			assertEquals(msg, 0, pat.to  .compareTo(range.rangeLast()));
			assertEquals(msg, 0, toDecimal(1).compareTo(range.rangeStep()));

			Object[] con = pat.contains;
			Object[] ni  = pat.notcontains;
			//--- includes
			for (Object v : con) {
				assertTrue(msg + " value=" + v, AADLFunctions.containsValue(range, v));
			}
			//--- not includes
			for (Object v : ni) {
				assertFalse(msg + " value=" + v, AADLFunctions.containsValue(range, v));
			}
			assertFalse(msg + " value=null", AADLFunctions.containsValue(range, null));
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.AADLFunctions#rangeFirst(ssac.aadl.runtime.util.range.DecimalRange)}.
	 */
	public void testRangeFirst() {
		try {
			AADLFunctions.rangeFirst(null);
			fail("Must be throw NullPointerException.");
		} catch (NullPointerException ex) {}
		
		for (int i = 0; i < rangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyRangePattern pat = rangePatterns[i];
			SimpleDecimalRange range = AADLFunctions.range(pat.from, pat.to, pat.step, pat.includeLast);
			assertEquals(msg, pat.includeLast, range.isIncludeRangeLast());
			assertEquals(msg, 0, pat.from.compareTo(range.rangeFirst()));
			assertEquals(msg, 0, pat.to  .compareTo(range.rangeLast()));
			assertEquals(msg, 0, pat.step.compareTo(range.rangeStep()));
			
			assertEquals(msg, range.rangeFirst(), AADLFunctions.rangeFirst(range));
		}
		
		for (int i = 0; i < naturalRangePatterns.length; i++) {
			VerifyNaturalRangePattern pat = naturalRangePatterns[i];
			String msg = String.format("Test params : [%d] ", i);
			NaturalNumberDecimalRange range = AADLFunctions.naturalNumberRange(pat.rangeString, pat.maxValue);
			assertEquals(msg, 0, pat.from.compareTo(range.rangeFirst()));
			assertEquals(msg, 0, pat.to  .compareTo(range.rangeLast()));
			assertEquals(msg, 0, toDecimal(1).compareTo(range.rangeStep()));
			
			assertEquals(msg, range.rangeFirst(), AADLFunctions.rangeFirst(range));
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.AADLFunctions#rangeLast(ssac.aadl.runtime.util.range.DecimalRange)}.
	 */
	public void testRangeLast() {
		try {
			AADLFunctions.rangeLast(null);
			fail("Must be throw NullPointerException.");
		} catch (NullPointerException ex) {}
		
		for (int i = 0; i < rangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyRangePattern pat = rangePatterns[i];
			SimpleDecimalRange range = AADLFunctions.range(pat.from, pat.to, pat.step, pat.includeLast);
			assertEquals(msg, pat.includeLast, range.isIncludeRangeLast());
			assertEquals(msg, 0, pat.from.compareTo(range.rangeFirst()));
			assertEquals(msg, 0, pat.to  .compareTo(range.rangeLast()));
			assertEquals(msg, 0, pat.step.compareTo(range.rangeStep()));
			
			assertEquals(msg, range.rangeLast(), AADLFunctions.rangeLast(range));
		}
		
		for (int i = 0; i < naturalRangePatterns.length; i++) {
			VerifyNaturalRangePattern pat = naturalRangePatterns[i];
			String msg = String.format("Test params : [%d] ", i);
			NaturalNumberDecimalRange range = AADLFunctions.naturalNumberRange(pat.rangeString, pat.maxValue);
			assertEquals(msg, 0, pat.from.compareTo(range.rangeFirst()));
			assertEquals(msg, 0, pat.to  .compareTo(range.rangeLast()));
			assertEquals(msg, 0, toDecimal(1).compareTo(range.rangeStep()));
			
			assertEquals(msg, range.rangeLast(), AADLFunctions.rangeLast(range));
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.AADLFunctions#rangeStep(ssac.aadl.runtime.util.range.DecimalRange)}.
	 */
	public void testRangeStep() {
		try {
			AADLFunctions.rangeStep(null);
			fail("Must be throw NullPointerException.");
		} catch (NullPointerException ex) {}
		
		for (int i = 0; i < rangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyRangePattern pat = rangePatterns[i];
			SimpleDecimalRange range = AADLFunctions.range(pat.from, pat.to, pat.step, pat.includeLast);
			assertEquals(msg, pat.includeLast, range.isIncludeRangeLast());
			assertEquals(msg, 0, pat.from.compareTo(range.rangeFirst()));
			assertEquals(msg, 0, pat.to  .compareTo(range.rangeLast()));
			assertEquals(msg, 0, pat.step.compareTo(range.rangeStep()));
			
			assertEquals(msg, range.rangeStep(), AADLFunctions.rangeStep(range));
		}
		
		for (int i = 0; i < naturalRangePatterns.length; i++) {
			VerifyNaturalRangePattern pat = naturalRangePatterns[i];
			String msg = String.format("Test params : [%d] ", i);
			NaturalNumberDecimalRange range = AADLFunctions.naturalNumberRange(pat.rangeString, pat.maxValue);
			assertEquals(msg, 0, pat.from.compareTo(range.rangeFirst()));
			assertEquals(msg, 0, pat.to  .compareTo(range.rangeLast()));
			assertEquals(msg, 0, toDecimal(1).compareTo(range.rangeStep()));
			
			assertEquals(msg, range.rangeStep(), AADLFunctions.rangeStep(range));
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.AADLFunctions#rangeMin(ssac.aadl.runtime.util.range.DecimalRange)}.
	 */
	public void testRangeMin() {
		try {
			AADLFunctions.rangeMin(null);
			fail("Must be throw NullPointerException.");
		} catch (NullPointerException ex) {}
		
		for (int i = 0; i < rangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyRangePattern pat = rangePatterns[i];
			SimpleDecimalRange range = AADLFunctions.range(pat.from, pat.to, pat.step, pat.includeLast);
			assertEquals(msg, pat.includeLast, range.isIncludeRangeLast());
			assertEquals(msg, 0, pat.from.compareTo(range.rangeFirst()));
			assertEquals(msg, 0, pat.to  .compareTo(range.rangeLast()));
			assertEquals(msg, 0, pat.step.compareTo(range.rangeStep()));
			
			assertEquals(msg, range.rangeMin(), AADLFunctions.rangeMin(range));
		}
		
		for (int i = 0; i < naturalRangePatterns.length; i++) {
			VerifyNaturalRangePattern pat = naturalRangePatterns[i];
			String msg = String.format("Test params : [%d] ", i);
			NaturalNumberDecimalRange range = AADLFunctions.naturalNumberRange(pat.rangeString, pat.maxValue);
			assertEquals(msg, 0, pat.from.compareTo(range.rangeFirst()));
			assertEquals(msg, 0, pat.to  .compareTo(range.rangeLast()));
			assertEquals(msg, 0, toDecimal(1).compareTo(range.rangeStep()));
			
			assertEquals(msg, range.rangeMin(), AADLFunctions.rangeMin(range));
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.AADLFunctions#rangeMax(ssac.aadl.runtime.util.range.DecimalRange)}.
	 */
	public void testRangeMax() {
		try {
			AADLFunctions.rangeMax(null);
			fail("Must be throw NullPointerException.");
		} catch (NullPointerException ex) {}
		
		for (int i = 0; i < rangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyRangePattern pat = rangePatterns[i];
			SimpleDecimalRange range = AADLFunctions.range(pat.from, pat.to, pat.step, pat.includeLast);
			assertEquals(msg, pat.includeLast, range.isIncludeRangeLast());
			assertEquals(msg, 0, pat.from.compareTo(range.rangeFirst()));
			assertEquals(msg, 0, pat.to  .compareTo(range.rangeLast()));
			assertEquals(msg, 0, pat.step.compareTo(range.rangeStep()));
			
			assertEquals(msg, range.rangeMax(), AADLFunctions.rangeMax(range));
		}
		
		for (int i = 0; i < naturalRangePatterns.length; i++) {
			VerifyNaturalRangePattern pat = naturalRangePatterns[i];
			String msg = String.format("Test params : [%d] ", i);
			NaturalNumberDecimalRange range = AADLFunctions.naturalNumberRange(pat.rangeString, pat.maxValue);
			assertEquals(msg, 0, pat.from.compareTo(range.rangeFirst()));
			assertEquals(msg, 0, pat.to  .compareTo(range.rangeLast()));
			assertEquals(msg, 0, toDecimal(1).compareTo(range.rangeStep()));
			
			assertEquals(msg, range.rangeMax(), AADLFunctions.rangeMax(range));
		}
	}
}
