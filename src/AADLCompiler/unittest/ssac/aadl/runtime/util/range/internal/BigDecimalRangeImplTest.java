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
 * @(#)BigDecimalRangeImplTest.java	1.70	2011/05/16
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.util.range.internal;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.NoSuchElementException;

import ssac.aadl.runtime.util.range.RangeUtil;

import junit.framework.TestCase;

/**
 * {@link ssac.aadl.runtime.util.range.internal.BigDecimalRangeImpl} クラスのテスト。
 * 
 * @version 1.70	2011/05/16
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * 
 * @since 1.70
 */
public class BigDecimalRangeImplTest extends TestCase
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final BigDecimal[][] rangeDefs = {
		{ BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO },
		{ BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE },
		{ toDecimal(-10L), toDecimal(10L), toDecimal(2L), toDecimal(2L) },
		{ toDecimal(10L), toDecimal(-10L), toDecimal(-2L), toDecimal(-2L) },
		{ toDecimal(-32768L), toDecimal(19660L), toDecimal(13107L), toDecimal(13107L) },
		{ toDecimal(32767L), toDecimal(-19661L), toDecimal(-13107L), toDecimal(-13107L) },
		{ toDecimal(-2147483648L), toDecimal(1288490188L), toDecimal(858993459L), toDecimal(858993459L) },
		{ toDecimal(2147483647L), toDecimal(-1288490189L), toDecimal(-858993459L), toDecimal(-858993459L) },
		{ toDecimal(-9223372036854775808L), toDecimal(5534023222112865484L), toDecimal(3689348814741910323L), toDecimal(3689348814741910323L) },
		{ toDecimal(9223372036854775807L), toDecimal(-5534023222112865485L), toDecimal(-3689348814741910323L), toDecimal(-3689348814741910323L) },
		{ toDecimal(-9223372036854775808L), toDecimal(9223372036854775807L), toDecimal(3689348814741910323L), toDecimal(3689348814741910323L) },
		{ toDecimal(9223372036854775807L), toDecimal(-9223372036854775808L), toDecimal(-3689348814741910323L), toDecimal(-3689348814741910323L) },
		{ toDecimal(10L), toDecimal(-10L), toDecimal(2L), toDecimal(0L) },
		{ toDecimal(-10L), toDecimal(10L), toDecimal(-2L), toDecimal(0L) },
		{ toDecimal("-2"), toDecimal("2"), toDecimal("0.5"), toDecimal("0.5") },
		{ toDecimal("2"), toDecimal("-2"), toDecimal("-0.5"), toDecimal("-0.5") },
	};
	
	static private final BigDecimal[][] rangeContains = {
		{},
		{ toDecimal(0L), toDecimal(1L) },
		{ toDecimal(-10L), toDecimal(-8L), toDecimal(-6L), toDecimal(-4L), toDecimal(-2L), toDecimal(0L), toDecimal(2L), toDecimal(4L), toDecimal(6L), toDecimal(8L), toDecimal(10L) },
		{ toDecimal(10L), toDecimal(8L), toDecimal(6L), toDecimal(4L), toDecimal(2L), toDecimal(0L), toDecimal(-2L), toDecimal(-4L), toDecimal(-6L), toDecimal(-8L), toDecimal(-10L) },
		{ toDecimal(-32768L), toDecimal(-19661L), toDecimal(-6554L), toDecimal(6553L), toDecimal(19660L) },
		{ toDecimal(32767L), toDecimal(19660L), toDecimal(6553L), toDecimal(-6554L), toDecimal(-19661L) },
		{ toDecimal(-2147483648L), toDecimal(-1288490189L), toDecimal(-429496730L), toDecimal(429496729L), toDecimal(1288490188L) },
		{ toDecimal(2147483647L), toDecimal(1288490188L), toDecimal(429496729L), toDecimal(-429496730L), toDecimal(-1288490189L) },
		{ toDecimal(-9223372036854775808L), toDecimal(-5534023222112865485L), toDecimal(-1844674407370955162L), toDecimal(1844674407370955161L), toDecimal(5534023222112865484L) },
		{ toDecimal(9223372036854775807L), toDecimal(5534023222112865484L), toDecimal(1844674407370955161L), toDecimal(-1844674407370955162L), toDecimal(-5534023222112865485L) },
		{ toDecimal(-9223372036854775808L), toDecimal(-5534023222112865485L), toDecimal(-1844674407370955162L), toDecimal(1844674407370955161L), toDecimal(5534023222112865484L), toDecimal(9223372036854775807L) },
		{ toDecimal(9223372036854775807L), toDecimal(5534023222112865484L), toDecimal(1844674407370955161L), toDecimal(-1844674407370955162L), toDecimal(-5534023222112865485L), toDecimal(-9223372036854775808L) },
		{},
		{},
		{ toDecimal("-2"), toDecimal("-1.5"), toDecimal("-1"), toDecimal("-0.5"), toDecimal("0"), toDecimal("0.5"), toDecimal("1"), toDecimal("1.5"), toDecimal("2") },
		{ toDecimal("2"), toDecimal("1.5"), toDecimal("1"), toDecimal("0.5"), toDecimal("0"), toDecimal("-0.5"), toDecimal("-1"), toDecimal("-1.5"), toDecimal("-2") },
	};
	
	static private final BigDecimal[][] rangeNotIncludes = {
		{ toDecimal(Long.MIN_VALUE), toDecimal(0L), toDecimal(Long.MAX_VALUE) },
		{ toDecimal(Long.MIN_VALUE), toDecimal(-1L), toDecimal(2L), toDecimal(Long.MAX_VALUE) },
		{ toDecimal(Long.MIN_VALUE), toDecimal(-11L), toDecimal(11L), toDecimal(Long.MAX_VALUE) },
		{ toDecimal(Long.MAX_VALUE), toDecimal(11L), toDecimal(-11L), toDecimal(Long.MIN_VALUE) },
		{ toDecimal(19661L), toDecimal(Short.MAX_VALUE) },
		{ toDecimal(Short.MIN_VALUE), toDecimal(-19662L) },
		{ toDecimal(1288490189L), toDecimal(Integer.MAX_VALUE) },
		{ toDecimal(Integer.MIN_VALUE), toDecimal(-1288490190L) },
		{ toDecimal(5534023222112865485L), toDecimal(Long.MAX_VALUE) },
		{ toDecimal(Long.MIN_VALUE), toDecimal(-5534023222112865486L) },
		{ toDecimal("-9223372036854775809"), toDecimal("-9223372036854775808.0000000001"), toDecimal("9223372036854775807.0000000001"), toDecimal("9223372036854775808") },
		{ toDecimal("-9223372036854775809"), toDecimal("-9223372036854775808.0000000001"), toDecimal("9223372036854775807.0000000001"), toDecimal("9223372036854775808") },
		{ toDecimal(Long.MIN_VALUE), toDecimal(-10L), toDecimal(0L), toDecimal(10L), toDecimal(Long.MAX_VALUE) },
		{ toDecimal(Long.MIN_VALUE), toDecimal(-10L), toDecimal(0L), toDecimal(10L), toDecimal(Long.MAX_VALUE) },
		{ toDecimal("-2.000000000000000000001"), toDecimal("2.000000000000000000001"), toDecimal("-2.5"), toDecimal("2.5") },
		{ toDecimal("-2.000000000000000000001"), toDecimal("2.000000000000000000001"), toDecimal("-2.5"), toDecimal("2.5") },
	};
	
	static private final BigDecimal[][] rangeNotContains = {
		{ toDecimal(Long.MIN_VALUE), toDecimal(0L), toDecimal(Long.MAX_VALUE) },
		{ toDecimal(Long.MIN_VALUE), toDecimal(-1L), toDecimal(2L), toDecimal(Long.MAX_VALUE) },
		{ toDecimal(Long.MIN_VALUE), toDecimal(-11L), toDecimal(-1L), toDecimal(1L), toDecimal(11L), toDecimal(Long.MAX_VALUE) },
		{ toDecimal(Long.MAX_VALUE), toDecimal(11L), toDecimal(-1L), toDecimal(1L), toDecimal(-11L), toDecimal(Long.MIN_VALUE) },
		{ toDecimal(Short.MAX_VALUE), toDecimal(-32767L), toDecimal(-19660L), toDecimal(-6553L), toDecimal(6552L), toDecimal(19661L) },
		{ toDecimal(Short.MIN_VALUE), toDecimal(32766L), toDecimal(19659L), toDecimal(6552L), toDecimal(-6555L), toDecimal(-19662L) },
		{ toDecimal(Integer.MAX_VALUE), toDecimal(-2147483647L), toDecimal(-1288490188L), toDecimal(-429496729L), toDecimal(429496730L), toDecimal(1288490189L) },
		{ toDecimal(Integer.MIN_VALUE), toDecimal(2147483646L), toDecimal(1288490187L), toDecimal(429496728L), toDecimal(-429496731L), toDecimal(-1288490190L) },
		{ toDecimal(Long.MAX_VALUE), toDecimal(-9223372036854775807L), toDecimal(-5534023222112865484L), toDecimal(-1844674407370955161L), toDecimal(1844674407370955162L), toDecimal(5534023222112865485L) },
		{ toDecimal(Long.MIN_VALUE), toDecimal(9223372036854775806L), toDecimal(5534023222112865483L), toDecimal(1844674407370955160L), toDecimal(-1844674407370955163L), toDecimal(-5534023222112865486L) },
		{ toDecimal("-9223372036854775809"), toDecimal("-9223372036854775808.0000000001"),
			toDecimal("-9223372036854775807.9999999999"), toDecimal("9223372036854775806.9999999999"),
			toDecimal("9223372036854775807.0000000001"), toDecimal("9223372036854775808") },
		{ toDecimal("-9223372036854775809"), toDecimal("-9223372036854775808.0000000001"),
			toDecimal("-9223372036854775807.9999999999"), toDecimal("9223372036854775806.9999999999"),
			toDecimal("9223372036854775807.0000000001"), toDecimal("9223372036854775808") },
		{ toDecimal(Long.MIN_VALUE), toDecimal(-10L), toDecimal(0L), toDecimal(10L), toDecimal(Long.MAX_VALUE) },
		{ toDecimal(Long.MIN_VALUE), toDecimal(-10L), toDecimal(0L), toDecimal(10L), toDecimal(Long.MAX_VALUE) },
		{ toDecimal("-2.1"), toDecimal("-1.9"), toDecimal("-1.4"), toDecimal("-0.9"), toDecimal("-0.4"), toDecimal("0.1"), toDecimal("0.51"), toDecimal("1.1"), toDecimal("1.51"), toDecimal("2.1") },
		{ toDecimal("2.1"), toDecimal("1.9"), toDecimal("1.49"), toDecimal("0.9"), toDecimal("0.49"), toDecimal("-0.1"), toDecimal("-0.51"), toDecimal("-1.1"), toDecimal("-1.51"), toDecimal("-2.1") },
	};
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	static protected BigDecimal toDecimal(long value) {
		return BigDecimal.valueOf(value);
	}
	
	static protected BigDecimal toDecimal(String value) {
		return new BigDecimal(value);
	}

	//------------------------------------------------------------
	// Test Cases for 1.70
	//------------------------------------------------------------

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.BigDecimalRangeImpl#BigDecimalRangeImpl(java.math.BigDecimal, java.math.BigDecimal, java.math.BigDecimal)}.
	 */
	public void testBigDecimalRangeImpl() {
		for (int i = 0; i < rangeDefs.length; i++) {
			BigDecimal[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			BigDecimalRangeImpl range = new BigDecimalRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, 0, def[0].compareTo(range.getDecimalFromValue()));
			assertEquals(msg, 0, def[1].compareTo(range.getDecimalToValue()));
			assertEquals(msg, 0, def[3].compareTo(range.getDecimalStepValue()));
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.BigDecimalRangeImpl#getValueClass()}.
	 */
	public void testGetValueClass() {
		for (int i = 0; i < rangeDefs.length; i++) {
			BigDecimal[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			BigDecimalRangeImpl range = new BigDecimalRangeImpl(def[0], def[1], def[2]);
			if (RangeUtil.exactlyLongValues(def[0], def[1], def[3])) {
				if (RangeUtil.exactlyShortValues(def[0], def[1], def[3])) {
					assertEquals(msg, Short.class, range.getValueClass());
				}
				else if (RangeUtil.exactlyIntegerValues(def[0], def[1], def[3])) {
					assertEquals(msg, Integer.class, range.getValueClass());
				}
				else {
					assertEquals(msg, Long.class, range.getValueClass());
				}
			} else {
				assertEquals(msg, BigDecimal.class, range.getValueClass());
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.BigDecimalRangeImpl#isEmpty()}.
	 */
	public void testIsEmpty() {
		for (int i = 0; i < rangeDefs.length; i++) {
			BigDecimal[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			BigDecimalRangeImpl range = new BigDecimalRangeImpl(def[0], def[1], def[2]);
			if (def[3].signum() != 0) {
				assertFalse(msg, range.isEmpty());
			} else {
				assertTrue(msg, range.isEmpty());
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.BigDecimalRangeImpl#isIncremental()}.
	 */
	public void testIsIncremental() {
		for (int i = 0; i < rangeDefs.length; i++) {
			BigDecimal[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			BigDecimalRangeImpl range = new BigDecimalRangeImpl(def[0], def[1], def[2]);
			if (def[3].signum() < 0) {
				assertFalse(msg, range.isIncremental());
			} else {
				assertTrue(msg, range.isIncremental());
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.BigDecimalRangeImpl#stepSignum()}.
	 */
	public void testStepSignum() {
		for (int i = 0; i < rangeDefs.length; i++) {
			BigDecimal[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			BigDecimalRangeImpl range = new BigDecimalRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, def[3].signum(), range.stepSignum());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.BigDecimalRangeImpl#isIncludeValue(java.math.BigDecimal)}.
	 */
	public void testIsIncludeValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			BigDecimal[] def = rangeDefs[i];
			BigDecimal[] con = rangeContains[i];
			BigDecimal[] ni = rangeNotIncludes[i];
			String msg = String.format("Test params : [%d] ", i);
			BigDecimalRangeImpl range = new BigDecimalRangeImpl(def[0], def[1], def[2]);
			//--- includes
			for (BigDecimal v : con) {
				assertTrue(msg + " value=" + v, range.isIncludeValue(v));
			}
			//--- not includes
			for (BigDecimal v : ni) {
				assertFalse(msg + " value=" + v, range.isIncludeValue(v));
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.BigDecimalRangeImpl#containsValue(java.math.BigDecimal)}.
	 */
	public void testContainsValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			BigDecimal[] def = rangeDefs[i];
			BigDecimal[] con = rangeContains[i];
			BigDecimal[] ni = rangeNotContains[i];
			String msg = String.format("Test params : [%d] ", i);
			BigDecimalRangeImpl range = new BigDecimalRangeImpl(def[0], def[1], def[2]);
			//--- includes
			for (BigDecimal v : con) {
				assertTrue(msg + " value=" + v, range.containsValue(v));
			}
			//--- not includes
			for (BigDecimal v : ni) {
				assertFalse(msg + " value=" + v, range.containsValue(v));
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.BigDecimalRangeImpl#getShortFromValue()}.
	 */
	public void testGetShortFromValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			BigDecimal[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			BigDecimalRangeImpl range = new BigDecimalRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, def[0].shortValue(), range.getShortFromValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.BigDecimalRangeImpl#getIntegerFromValue()}.
	 */
	public void testGetIntegerFromValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			BigDecimal[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			BigDecimalRangeImpl range = new BigDecimalRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, def[0].intValue(), range.getIntegerFromValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.BigDecimalRangeImpl#getLongFromValue()}.
	 */
	public void testGetLongFromValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			BigDecimal[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			BigDecimalRangeImpl range = new BigDecimalRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, def[0].longValue(), range.getLongFromValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.BigDecimalRangeImpl#getDecimalFromValue()}.
	 */
	public void testGetDecimalFromValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			BigDecimal[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			BigDecimalRangeImpl range = new BigDecimalRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, 0, def[0].compareTo(range.getDecimalFromValue()));
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.BigDecimalRangeImpl#getShortToValue()}.
	 */
	public void testGetShortToValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			BigDecimal[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			BigDecimalRangeImpl range = new BigDecimalRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, def[1].shortValue(), range.getShortToValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.BigDecimalRangeImpl#getIntegerToValue()}.
	 */
	public void testGetIntegerToValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			BigDecimal[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			BigDecimalRangeImpl range = new BigDecimalRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, def[1].intValue(), range.getIntegerToValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.BigDecimalRangeImpl#getLongToValue()}.
	 */
	public void testGetLongToValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			BigDecimal[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			BigDecimalRangeImpl range = new BigDecimalRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, def[1].longValue(), range.getLongToValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.BigDecimalRangeImpl#getDecimalToValue()}.
	 */
	public void testGetDecimalToValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			BigDecimal[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			BigDecimalRangeImpl range = new BigDecimalRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, 0, def[1].compareTo(range.getDecimalToValue()));
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.BigDecimalRangeImpl#getShortStepValue()}.
	 */
	public void testGetShortStepValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			BigDecimal[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			BigDecimalRangeImpl range = new BigDecimalRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, def[3].shortValue(), range.getShortStepValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.BigDecimalRangeImpl#getIntegerStepValue()}.
	 */
	public void testGetIntegerStepValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			BigDecimal[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			BigDecimalRangeImpl range = new BigDecimalRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, def[3].intValue(), range.getIntegerStepValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.BigDecimalRangeImpl#getLongStepValue()}.
	 */
	public void testGetLongStepValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			BigDecimal[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			BigDecimalRangeImpl range = new BigDecimalRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, def[3].longValue(), range.getLongStepValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.BigDecimalRangeImpl#getDecimalStepValue()}.
	 */
	public void testGetDecimalStepValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			BigDecimal[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			BigDecimalRangeImpl range = new BigDecimalRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, 0, def[3].compareTo(range.getDecimalStepValue()));
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.BigDecimalRangeImpl#getShortRangeIterator()}.
	 */
	public void testGetShortRangeIterator() {
		for (int i = 0; i < rangeDefs.length; i++) {
			BigDecimal[] def = rangeDefs[i];
			BigDecimal[] con = rangeContains[i];
			String msg = String.format("Test params : [%d] ", i);
			BigDecimalRangeImpl range = new BigDecimalRangeImpl(def[0], def[1], def[2]);
			Iterator<Short> it = range.getShortRangeIterator();
			if (con.length > 0) {
				for (BigDecimal v : con) {
					String vmsg = msg + " value=" + v;
					assertTrue(vmsg, it.hasNext());
					assertEquals(vmsg, v.shortValue(), it.next().shortValue());
				}
			}
			assertFalse(msg, it.hasNext());
			try {
				it.next();
				fail(msg + " Must be throw NoSuchElementException.");
			} catch (NoSuchElementException ex) {
				assertTrue(true);
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.BigDecimalRangeImpl#getIntegerRangeIterator()}.
	 */
	public void testGetIntegerRangeIterator() {
		for (int i = 0; i < rangeDefs.length; i++) {
			BigDecimal[] def = rangeDefs[i];
			BigDecimal[] con = rangeContains[i];
			String msg = String.format("Test params : [%d] ", i);
			BigDecimalRangeImpl range = new BigDecimalRangeImpl(def[0], def[1], def[2]);
			Iterator<Integer> it = range.getIntegerRangeIterator();
			if (con.length > 0) {
				for (BigDecimal v : con) {
					String vmsg = msg + " value=" + v;
					assertTrue(vmsg, it.hasNext());
					assertEquals(vmsg, v.intValue(), it.next().intValue());
				}
			}
			assertFalse(msg, it.hasNext());
			try {
				it.next();
				fail(msg + " Must be throw NoSuchElementException.");
			} catch (NoSuchElementException ex) {
				assertTrue(true);
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.BigDecimalRangeImpl#getLongRangeIterator()}.
	 */
	public void testGetLongRangeIterator() {
		for (int i = 0; i < rangeDefs.length; i++) {
			BigDecimal[] def = rangeDefs[i];
			BigDecimal[] con = rangeContains[i];
			String msg = String.format("Test params : [%d] ", i);
			BigDecimalRangeImpl range = new BigDecimalRangeImpl(def[0], def[1], def[2]);
			Iterator<Long> it = range.getLongRangeIterator();
			if (con.length > 0) {
				for (BigDecimal v : con) {
					String vmsg = msg + " value=" + v;
					assertTrue(vmsg, it.hasNext());
					assertEquals(vmsg, v.longValue(), it.next().longValue());
				}
			}
			assertFalse(msg, it.hasNext());
			try {
				it.next();
				fail(msg + " Must be throw NoSuchElementException.");
			} catch (NoSuchElementException ex) {
				assertTrue(true);
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.BigDecimalRangeImpl#getDecimalRangeIterator()}.
	 */
	public void testGetDecimalRangeIterator() {
		for (int i = 0; i < rangeDefs.length; i++) {
			BigDecimal[] def = rangeDefs[i];
			BigDecimal[] con = rangeContains[i];
			String msg = String.format("Test params : [%d] ", i);
			BigDecimalRangeImpl range = new BigDecimalRangeImpl(def[0], def[1], def[2]);
			Iterator<BigDecimal> it = range.getDecimalRangeIterator();
			if (con.length > 0) {
				for (BigDecimal v : con) {
					String vmsg = msg + " value=" + v;
					assertTrue(vmsg, it.hasNext());
					assertEquals(vmsg, 0, v.compareTo(it.next()));
				}
			}
			assertFalse(msg, it.hasNext());
			try {
				it.next();
				fail(msg + " Must be throw NoSuchElementException.");
			} catch (NoSuchElementException ex) {
				assertTrue(true);
			}
		}
	}
}
