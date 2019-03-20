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
 * @(#)RangeUtilTest.java	1.70	2011/05/16
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.util.range;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

/**
 * {@link ssac.aadl.runtime.util.range.RangeUtil} クラスのテスト。
 * 
 * @version 1.70	2011/05/16
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * 
 * @since 1.70
 */
public class RangeUtilTest extends TestCase
{
	static protected final BigDecimal BigDecimalF5 = new BigDecimal("0.5");
	
	static protected final BigDecimal shortMinValue = BigDecimal.valueOf((long)Short.MIN_VALUE);
	static protected final BigDecimal shortMaxValue = BigDecimal.valueOf((long)Short.MAX_VALUE);
	static protected final BigDecimal intMinValue = BigDecimal.valueOf((long)Integer.MIN_VALUE);
	static protected final BigDecimal intMaxValue = BigDecimal.valueOf((long)Integer.MAX_VALUE);
	static protected final BigDecimal longMinValue = BigDecimal.valueOf(Long.MIN_VALUE);
	static protected final BigDecimal longMaxValue = BigDecimal.valueOf(Long.MAX_VALUE);
	
	static protected final BigDecimal shortMinValue_M1 = shortMinValue.subtract(BigDecimal.ONE);
	static protected final BigDecimal shortMinValue_P1 = shortMinValue.add(BigDecimal.ONE);
	static protected final BigDecimal shortMaxValue_M1 = shortMaxValue.subtract(BigDecimal.ONE);
	static protected final BigDecimal shortMaxValue_P1 = shortMaxValue.add(BigDecimal.ONE);
	static protected final BigDecimal intMinValue_M1 = intMinValue.subtract(BigDecimal.ONE);
	static protected final BigDecimal intMinValue_P1 = intMinValue.add(BigDecimal.ONE);
	static protected final BigDecimal intMaxValue_M1 = intMaxValue.subtract(BigDecimal.ONE);
	static protected final BigDecimal intMaxValue_P1 = intMaxValue.add(BigDecimal.ONE);
	static protected final BigDecimal longMinValue_M1 = longMinValue.subtract(BigDecimal.ONE);
	static protected final BigDecimal longMinValue_P1 = longMinValue.add(BigDecimal.ONE);
	static protected final BigDecimal longMaxValue_M1 = longMaxValue.subtract(BigDecimal.ONE);
	static protected final BigDecimal longMaxValue_P1 = longMaxValue.add(BigDecimal.ONE);
	
	static protected final BigDecimal shortMinValue_MF5 = shortMinValue.subtract(BigDecimalF5);
	static protected final BigDecimal shortMinValue_PF5 = shortMinValue.add(BigDecimalF5);
	static protected final BigDecimal shortMaxValue_MF5 = shortMaxValue.subtract(BigDecimalF5);
	static protected final BigDecimal shortMaxValue_PF5 = shortMaxValue.add(BigDecimalF5);
	static protected final BigDecimal intMinValue_MF5 = intMinValue.subtract(BigDecimalF5);
	static protected final BigDecimal intMinValue_PF5 = intMinValue.add(BigDecimalF5);
	static protected final BigDecimal intMaxValue_MF5 = intMaxValue.subtract(BigDecimalF5);
	static protected final BigDecimal intMaxValue_PF5 = intMaxValue.add(BigDecimalF5);
	static protected final BigDecimal longMinValue_MF5 = longMinValue.subtract(BigDecimalF5);
	static protected final BigDecimal longMinValue_PF5 = longMinValue.add(BigDecimalF5);
	static protected final BigDecimal longMaxValue_MF5 = longMaxValue.subtract(BigDecimalF5);
	static protected final BigDecimal longMaxValue_PF5 = longMaxValue.add(BigDecimalF5);
	
	static protected final BigDecimal[] aryShortExactValues = {
		shortMinValue_P1, shortMaxValue_M1,
		shortMinValue, shortMaxValue,
	};
	
	static protected final BigDecimal[] aryIntExactValues = {
		shortMinValue_P1, shortMaxValue_M1,
		shortMinValue, shortMaxValue,
		shortMinValue_M1, shortMaxValue_P1,
		intMinValue_P1, intMaxValue_M1,
		intMinValue, intMaxValue,
	};
	
	static protected final BigDecimal[] aryLongExactValues = {
		shortMinValue_P1, shortMaxValue_M1,
		shortMinValue, shortMaxValue,
		shortMinValue_M1, shortMaxValue_P1,
		intMinValue_P1, intMaxValue_M1,
		intMinValue, intMaxValue,
		intMinValue_M1, intMaxValue_P1,
		longMinValue_P1, longMaxValue_M1,
		longMinValue, longMaxValue,
	};
	
	static protected final BigDecimal[] aryIntegerValues = {
		shortMinValue_P1, shortMaxValue_M1,
		shortMinValue, shortMaxValue,
		shortMinValue_M1, shortMaxValue_P1,
		intMinValue_P1, intMaxValue_M1,
		intMinValue, intMaxValue,
		intMinValue_M1, intMaxValue_P1,
		longMinValue_P1, longMaxValue_M1,
		longMinValue, longMaxValue,
		longMinValue_M1, longMaxValue_P1,
	};
	
	static protected final BigDecimal[] aryAllValues = {
		shortMinValue_P1, shortMaxValue_M1,
		shortMinValue, shortMaxValue,
		shortMinValue_M1, shortMaxValue_P1,
		intMinValue_P1, intMaxValue_M1,
		intMinValue, intMaxValue,
		intMinValue_M1, intMaxValue_P1,
		longMinValue_P1, longMaxValue_M1,
		longMinValue, longMaxValue,
		longMinValue_M1, longMaxValue_P1,

		shortMinValue_PF5, shortMaxValue_MF5,
		shortMinValue_MF5, shortMaxValue_PF5,
		intMinValue_PF5, intMaxValue_MF5,
		intMinValue_MF5, intMaxValue_PF5,
		longMinValue_PF5, longMaxValue_MF5,
		longMinValue_MF5, longMaxValue_PF5,
	};
	
	
	static protected final Set<BigDecimal> shortExactValueSet = new HashSet<BigDecimal>(Arrays.asList(aryShortExactValues));
	
	static protected final Set<BigDecimal> intExactValueSet = new HashSet<BigDecimal>(Arrays.asList(aryIntExactValues));
	
	static protected final Set<BigDecimal> longExactValueSet = new HashSet<BigDecimal>(Arrays.asList(aryLongExactValues));
	
	static protected final Set<BigDecimal> integerValueSet = new HashSet<BigDecimal>(Arrays.asList(aryIntegerValues));
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	//------------------------------------------------------------
	// for 1.70
	//------------------------------------------------------------

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.RangeUtil#toBigDecimal(java.lang.Object)}.
	 */
	public void testToBigDecimal() {
		assertNull(RangeUtil.toBigDecimal(null));
		assertTrue(0 == BigDecimal.valueOf(0.5).compareTo(RangeUtil.toBigDecimal(0.5f)));
		assertTrue(0 == BigDecimal.valueOf(0.5).compareTo(RangeUtil.toBigDecimal(0.5)));
		assertTrue(0 == BigDecimal.valueOf(-1234).compareTo(RangeUtil.toBigDecimal(Short.valueOf((short)-1234))));
		assertTrue(0 == BigDecimal.valueOf(-1234).compareTo(RangeUtil.toBigDecimal(Integer.valueOf(-1234))));
		assertTrue(0 == BigDecimal.valueOf(-1234).compareTo(RangeUtil.toBigDecimal(Long.valueOf(-1234L))));
		assertTrue(0 == BigDecimal.valueOf(-1234).compareTo(RangeUtil.toBigDecimal(BigInteger.valueOf(-1234L))));
		assertTrue(0 == BigDecimal.valueOf(-1234).compareTo(RangeUtil.toBigDecimal(BigDecimal.valueOf(-1234L))));
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.RangeUtil#exactlyShort(java.math.BigDecimal)}.
	 */
	public void testExactlyShort() {
		assertFalse(RangeUtil.exactlyShort(null));
		for (BigDecimal value : aryAllValues) {
			if (shortExactValueSet.contains(value)) {
				assertTrue(RangeUtil.exactlyShort(value));
			} else {
				assertFalse(RangeUtil.exactlyShort(value));
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.RangeUtil#exactlyInteger(java.math.BigDecimal)}.
	 */
	public void testExactlyInteger() {
		assertFalse(RangeUtil.exactlyInteger(null));
		for (BigDecimal value : aryAllValues) {
			if (intExactValueSet.contains(value)) {
				assertTrue(RangeUtil.exactlyInteger(value));
			} else {
				assertFalse(RangeUtil.exactlyInteger(value));
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.RangeUtil#exactlyLong(java.math.BigDecimal)}.
	 */
	public void testExactlyLong() {
		assertFalse(RangeUtil.exactlyLong(null));
		for (BigDecimal value : aryAllValues) {
			if (longExactValueSet.contains(value)) {
				assertTrue(RangeUtil.exactlyLong(value));
			} else {
				assertFalse(RangeUtil.exactlyLong(value));
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.RangeUtil#exactlyBigInteger(java.math.BigDecimal)}.
	 */
	public void testExactlyBigInteger() {
		assertFalse(RangeUtil.exactlyBigInteger(null));
		for (BigDecimal value : aryAllValues) {
			if (integerValueSet.contains(value)) {
				assertTrue(RangeUtil.exactlyBigInteger(value));
			} else {
				assertFalse(RangeUtil.exactlyBigInteger(value));
			}
		}
	}
	
	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.RangeUtil#exactlyShortValues(BigDecimal, BigDecimal, BigDecimal)}.
	 */
	public void testExactlyShortBigDecimalBigDecimalBigDecimal() {
		// check null
		assertFalse(RangeUtil.exactlyShortValues(null, null, null));
		assertFalse(RangeUtil.exactlyShortValues(shortMaxValue, shortMaxValue, null));
		assertFalse(RangeUtil.exactlyShortValues(shortMaxValue, null, shortMaxValue));
		assertFalse(RangeUtil.exactlyShortValues(null, shortMaxValue, shortMaxValue));
		
		// check illegal ranges
		assertFalse(RangeUtil.exactlyShortValues(shortMaxValue, shortMaxValue, intMinValue));
		assertFalse(RangeUtil.exactlyShortValues(shortMaxValue, intMinValue, shortMaxValue));
		assertFalse(RangeUtil.exactlyShortValues(intMinValue, shortMaxValue, shortMaxValue));
		assertFalse(RangeUtil.exactlyShortValues(shortMaxValue, shortMaxValue, shortMaxValue_MF5));
		assertFalse(RangeUtil.exactlyShortValues(shortMaxValue, shortMaxValue_MF5, shortMaxValue));
		assertFalse(RangeUtil.exactlyShortValues(shortMaxValue_MF5, shortMaxValue, shortMaxValue));
		
		// valid values
		assertTrue(RangeUtil.exactlyShortValues(shortMaxValue, shortMaxValue, shortMinValue));
		assertTrue(RangeUtil.exactlyShortValues(shortMaxValue, shortMinValue, shortMaxValue));
		assertTrue(RangeUtil.exactlyShortValues(shortMinValue, shortMaxValue, shortMaxValue));
		assertTrue(RangeUtil.exactlyShortValues(shortMinValue, shortMinValue, shortMaxValue));
		assertTrue(RangeUtil.exactlyShortValues(shortMinValue, shortMaxValue, shortMinValue));
		assertTrue(RangeUtil.exactlyShortValues(shortMaxValue, shortMinValue, shortMinValue));
	}
	
	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.RangeUtil#exactlyIntegerValues(BigDecimal, BigDecimal, BigDecimal)}.
	 */
	public void testExactlyIntegerBigDecimalBigDecimalBigDecimal() {
		// check null
		assertFalse(RangeUtil.exactlyIntegerValues(null, null, null));
		assertFalse(RangeUtil.exactlyIntegerValues(intMaxValue, intMaxValue, null));
		assertFalse(RangeUtil.exactlyIntegerValues(intMaxValue, null, intMaxValue));
		assertFalse(RangeUtil.exactlyIntegerValues(null, intMaxValue, intMaxValue));
		
		// check illegal ranges
		assertFalse(RangeUtil.exactlyIntegerValues(intMaxValue, intMaxValue, longMinValue));
		assertFalse(RangeUtil.exactlyIntegerValues(intMaxValue, longMinValue, intMaxValue));
		assertFalse(RangeUtil.exactlyIntegerValues(longMinValue, intMaxValue, intMaxValue));
		assertFalse(RangeUtil.exactlyIntegerValues(intMaxValue, intMaxValue, intMaxValue_MF5));
		assertFalse(RangeUtil.exactlyIntegerValues(intMaxValue, intMaxValue_MF5, intMaxValue));
		assertFalse(RangeUtil.exactlyIntegerValues(intMaxValue_MF5, intMaxValue, intMaxValue));
		
		// valid values
		assertTrue(RangeUtil.exactlyIntegerValues(intMaxValue, intMaxValue, intMinValue));
		assertTrue(RangeUtil.exactlyIntegerValues(intMaxValue, intMinValue, intMaxValue));
		assertTrue(RangeUtil.exactlyIntegerValues(intMinValue, intMaxValue, intMaxValue));
		assertTrue(RangeUtil.exactlyIntegerValues(intMinValue, intMinValue, intMaxValue));
		assertTrue(RangeUtil.exactlyIntegerValues(intMinValue, intMaxValue, intMinValue));
		assertTrue(RangeUtil.exactlyIntegerValues(intMaxValue, intMinValue, intMinValue));
	}
	
	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.RangeUtil#exactlyLongValues(BigDecimal, BigDecimal, BigDecimal)}.
	 */
	public void testExactlyLongBigDecimalBigDecimalBigDecimal() {
		// check null
		assertFalse(RangeUtil.exactlyLongValues(null, null, null));
		assertFalse(RangeUtil.exactlyLongValues(longMaxValue, longMaxValue, null));
		assertFalse(RangeUtil.exactlyLongValues(longMaxValue, null, longMaxValue));
		assertFalse(RangeUtil.exactlyLongValues(null, longMaxValue, longMaxValue));
		
		// check illegal ranges
		assertFalse(RangeUtil.exactlyLongValues(longMaxValue, longMaxValue, longMinValue_M1));
		assertFalse(RangeUtil.exactlyLongValues(longMaxValue, longMinValue_M1, longMaxValue));
		assertFalse(RangeUtil.exactlyLongValues(longMinValue_M1, longMaxValue, longMaxValue));
		assertFalse(RangeUtil.exactlyLongValues(longMaxValue, longMaxValue, longMaxValue_MF5));
		assertFalse(RangeUtil.exactlyLongValues(longMaxValue, longMaxValue_MF5, longMaxValue));
		assertFalse(RangeUtil.exactlyLongValues(longMaxValue_MF5, longMaxValue, longMaxValue));
		
		// valid values
		assertTrue(RangeUtil.exactlyLongValues(longMaxValue, longMaxValue, longMinValue));
		assertTrue(RangeUtil.exactlyLongValues(longMaxValue, longMinValue, longMaxValue));
		assertTrue(RangeUtil.exactlyLongValues(longMinValue, longMaxValue, longMaxValue));
		assertTrue(RangeUtil.exactlyLongValues(longMinValue, longMinValue, longMaxValue));
		assertTrue(RangeUtil.exactlyLongValues(longMinValue, longMaxValue, longMinValue));
		assertTrue(RangeUtil.exactlyLongValues(longMaxValue, longMinValue, longMinValue));
	}
	
	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.RangeUtil#exactlyBigIntegerValues(BigDecimal, BigDecimal, BigDecimal)}.
	 */
	public void testExactlyBigIntegerBigDecimalBigDecimalBigDecimal() {
		// check null
		assertFalse(RangeUtil.exactlyBigIntegerValues(null, null, null));
		assertFalse(RangeUtil.exactlyBigIntegerValues(longMaxValue, longMaxValue, null));
		assertFalse(RangeUtil.exactlyBigIntegerValues(longMaxValue, null, longMaxValue));
		assertFalse(RangeUtil.exactlyBigIntegerValues(null, longMaxValue, longMaxValue));
		
		// check illegal ranges
		assertFalse(RangeUtil.exactlyBigIntegerValues(longMaxValue, longMaxValue, longMaxValue_MF5));
		assertFalse(RangeUtil.exactlyBigIntegerValues(longMaxValue, longMaxValue_MF5, longMaxValue));
		assertFalse(RangeUtil.exactlyBigIntegerValues(longMaxValue_MF5, longMaxValue, longMaxValue));
		
		// valid values
		assertTrue(RangeUtil.exactlyBigIntegerValues(longMaxValue, longMaxValue, longMinValue_M1));
		assertTrue(RangeUtil.exactlyBigIntegerValues(longMaxValue, longMinValue_M1, longMaxValue));
		assertTrue(RangeUtil.exactlyBigIntegerValues(longMinValue_M1, longMaxValue, longMaxValue));
		assertTrue(RangeUtil.exactlyBigIntegerValues(longMaxValue, longMaxValue, longMinValue));
		assertTrue(RangeUtil.exactlyBigIntegerValues(longMaxValue, longMinValue, longMaxValue));
		assertTrue(RangeUtil.exactlyBigIntegerValues(longMinValue, longMaxValue, longMaxValue));
		assertTrue(RangeUtil.exactlyBigIntegerValues(longMinValue, longMinValue, longMaxValue));
		assertTrue(RangeUtil.exactlyBigIntegerValues(longMinValue, longMaxValue, longMinValue));
		assertTrue(RangeUtil.exactlyBigIntegerValues(longMaxValue, longMinValue, longMinValue));
		
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.RangeUtil#exactlyIterableShortRange(short, short, short)}.
	 */
	public void testExactlyIterableShortRangeShortShortShort() {
		assertTrue(RangeUtil.exactlyIterableShortRange(shortMinValue_P1.shortValue(), shortMaxValue_M1.shortValue(), (short)1));
		assertTrue(RangeUtil.exactlyIterableShortRange(shortMaxValue_M1.shortValue(), shortMinValue_P1.shortValue(), (short)-1));
		assertFalse(RangeUtil.exactlyIterableShortRange(shortMinValue.shortValue(), shortMaxValue.shortValue(), (short)1));
		assertFalse(RangeUtil.exactlyIterableShortRange(shortMaxValue.shortValue(), shortMinValue.shortValue(), (short)-1));
		assertFalse(RangeUtil.exactlyIterableShortRange(shortMinValue_P1.shortValue(), shortMaxValue_M1.shortValue(), shortMaxValue_M1.shortValue()));
		assertFalse(RangeUtil.exactlyIterableShortRange(shortMaxValue_M1.shortValue(), shortMinValue_P1.shortValue(), shortMaxValue_M1.negate().shortValue()));
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.RangeUtil#exactlyIterableIntegerRange(int, int, int)}.
	 */
	public void testExactlyIterableIntegerRangeIntIntInt() {
		assertTrue(RangeUtil.exactlyIterableIntegerRange(intMinValue_P1.intValue(), intMaxValue_M1.intValue(), 1));
		assertTrue(RangeUtil.exactlyIterableIntegerRange(intMaxValue_M1.intValue(), intMinValue_P1.intValue(), -1));
		assertFalse(RangeUtil.exactlyIterableIntegerRange(intMinValue.intValue(), intMaxValue.intValue(), 1));
		assertFalse(RangeUtil.exactlyIterableIntegerRange(intMaxValue.intValue(), intMinValue.intValue(), -1));
		assertFalse(RangeUtil.exactlyIterableIntegerRange(intMinValue_P1.intValue(), intMaxValue_M1.intValue(), intMaxValue_M1.intValue()));
		assertFalse(RangeUtil.exactlyIterableIntegerRange(intMaxValue_M1.intValue(), intMinValue_P1.intValue(), intMaxValue_M1.negate().intValue()));
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.RangeUtil#exactlyIterableLongRange(long, long, long)}.
	 */
	public void testExactlyIterableLongRangeLongLongLong() {
		assertTrue(RangeUtil.exactlyIterableLongRange(longMinValue_P1.longValue(), longMaxValue_M1.longValue(), 1L));
		assertTrue(RangeUtil.exactlyIterableLongRange(longMaxValue_M1.longValue(), longMinValue_P1.longValue(), -1L));
		assertFalse(RangeUtil.exactlyIterableLongRange(longMinValue.longValue(), longMaxValue.longValue(), 1L));
		assertFalse(RangeUtil.exactlyIterableLongRange(longMaxValue.longValue(), longMinValue.longValue(), -1L));
		assertFalse(RangeUtil.exactlyIterableLongRange(longMinValue_P1.longValue(), longMaxValue_M1.longValue(), longMaxValue_M1.longValue()));
		assertFalse(RangeUtil.exactlyIterableLongRange(longMaxValue_M1.longValue(), longMinValue_P1.longValue(), longMaxValue_M1.negate().longValue()));
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.RangeUtil#exactlyIterableShortRange(java.math.BigDecimal, java.math.BigDecimal, java.math.BigDecimal)}.
	 */
	public void testExactlyIterableShortRangeBigDecimalBigDecimalBigDecimal() {
		assertTrue(RangeUtil.exactlyIterableShortRange(shortMinValue_P1, shortMaxValue_M1, BigDecimal.ONE));
		assertTrue(RangeUtil.exactlyIterableShortRange(shortMaxValue_M1, shortMinValue_P1, BigDecimal.ONE.negate()));
		assertFalse(RangeUtil.exactlyIterableShortRange(shortMinValue, shortMaxValue, BigDecimal.ONE));
		assertFalse(RangeUtil.exactlyIterableShortRange(shortMaxValue, shortMinValue, BigDecimal.ONE.negate()));
		assertFalse(RangeUtil.exactlyIterableShortRange(shortMinValue_P1, shortMaxValue_M1, shortMaxValue_M1));
		assertFalse(RangeUtil.exactlyIterableShortRange(shortMaxValue_M1, shortMinValue_P1, shortMaxValue_M1.negate()));
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.RangeUtil#exactlyIterableIntegerRange(java.math.BigDecimal, java.math.BigDecimal, java.math.BigDecimal)}.
	 */
	public void testExactlyIterableIntegerRangeBigDecimalBigDecimalBigDecimal() {
		assertTrue(RangeUtil.exactlyIterableIntegerRange(intMinValue_P1, intMaxValue_M1, BigDecimal.ONE));
		assertTrue(RangeUtil.exactlyIterableIntegerRange(intMaxValue_M1, intMinValue_P1, BigDecimal.ONE.negate()));
		assertFalse(RangeUtil.exactlyIterableIntegerRange(intMinValue, intMaxValue, BigDecimal.ONE));
		assertFalse(RangeUtil.exactlyIterableIntegerRange(intMaxValue, intMinValue, BigDecimal.ONE.negate()));
		assertFalse(RangeUtil.exactlyIterableIntegerRange(intMinValue_P1, intMaxValue_M1, intMaxValue_M1));
		assertFalse(RangeUtil.exactlyIterableIntegerRange(intMaxValue_M1, intMinValue_P1, intMaxValue_M1.negate()));
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.RangeUtil#exactlyIterableLongRange(java.math.BigDecimal, java.math.BigDecimal, java.math.BigDecimal)}.
	 */
	public void testExactlyIterableLongRangeBigDecimalBigDecimalBigDecimal() {
		assertTrue(RangeUtil.exactlyIterableLongRange(longMinValue_P1, longMaxValue_M1, BigDecimal.ONE));
		assertTrue(RangeUtil.exactlyIterableLongRange(longMaxValue_M1, longMinValue_P1, BigDecimal.ONE.negate()));
		assertFalse(RangeUtil.exactlyIterableLongRange(longMinValue, longMaxValue, BigDecimal.ONE));
		assertFalse(RangeUtil.exactlyIterableLongRange(longMaxValue, longMinValue, BigDecimal.ONE.negate()));
		assertFalse(RangeUtil.exactlyIterableLongRange(longMinValue_P1, longMaxValue_M1, longMaxValue_M1));
		assertFalse(RangeUtil.exactlyIterableLongRange(longMaxValue_M1, longMinValue_P1, longMaxValue_M1.negate()));
	}
}
