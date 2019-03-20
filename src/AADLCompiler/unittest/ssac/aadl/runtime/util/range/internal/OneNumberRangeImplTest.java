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
 * @(#)OneNumberRangeImplTest.java	1.70	2011/05/11
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.util.range.internal;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import junit.framework.TestCase;

/**
 * {@link ssac.aadl.runtime.util.range.internal.OneNumberRangeImpl} クラスのテスト。
 * 
 * @version 1.70	2011/05/11
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * 
 * @since 1.70
 */
public class OneNumberRangeImplTest extends TestCase
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
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

	static protected final BigDecimal floatMinValue = BigDecimal.valueOf(Float.MIN_VALUE);
	static protected final BigDecimal floatMaxValue = BigDecimal.valueOf(Float.MAX_VALUE);
	static protected final BigDecimal doubleMinValue = BigDecimal.valueOf(Double.MIN_VALUE);
	static protected final BigDecimal doubleMaxValue = BigDecimal.valueOf(Double.MAX_VALUE);
	
	static private final Map<BigDecimal, Class<?>> valueClassMap;
	
	static {
		valueClassMap = new HashMap<BigDecimal, Class<?>>();
		valueClassMap.put(shortMinValue, Short.class);
		valueClassMap.put(shortMaxValue, Short.class);
		valueClassMap.put(intMinValue, Integer.class);
		valueClassMap.put(intMaxValue, Integer.class);
		valueClassMap.put(longMinValue, Long.class);
		valueClassMap.put(longMaxValue, Long.class);
		valueClassMap.put(shortMinValue_M1, Integer.class);
		valueClassMap.put(shortMinValue_P1, Short.class);
		valueClassMap.put(shortMaxValue_M1, Short.class);
		valueClassMap.put(shortMaxValue_P1, Integer.class);
		valueClassMap.put(intMinValue_M1, Long.class);
		valueClassMap.put(intMinValue_P1, Integer.class);
		valueClassMap.put(intMaxValue_M1, Integer.class);
		valueClassMap.put(intMaxValue_P1, Long.class);
		valueClassMap.put(longMinValue_M1, BigDecimal.class);
		valueClassMap.put(longMinValue_P1, Long.class);
		valueClassMap.put(longMaxValue_M1, Long.class);
		valueClassMap.put(longMaxValue_P1, BigDecimal.class);
		valueClassMap.put(shortMinValue_MF5, BigDecimal.class);
		valueClassMap.put(shortMinValue_PF5, BigDecimal.class);
		valueClassMap.put(shortMaxValue_MF5, BigDecimal.class);
		valueClassMap.put(shortMaxValue_PF5, BigDecimal.class);
		valueClassMap.put(intMinValue_MF5, BigDecimal.class);
		valueClassMap.put(intMinValue_PF5, BigDecimal.class);
		valueClassMap.put(intMaxValue_MF5, BigDecimal.class);
		valueClassMap.put(intMaxValue_PF5, BigDecimal.class);
		valueClassMap.put(longMinValue_MF5, BigDecimal.class);
		valueClassMap.put(longMinValue_PF5, BigDecimal.class);
		valueClassMap.put(longMaxValue_MF5, BigDecimal.class);
		valueClassMap.put(longMaxValue_PF5, BigDecimal.class);
		valueClassMap.put(floatMinValue, BigDecimal.class);
		valueClassMap.put(floatMaxValue, BigDecimal.class);
		valueClassMap.put(doubleMinValue, BigDecimal.class);
		valueClassMap.put(doubleMaxValue, BigDecimal.class);
	}

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

	//------------------------------------------------------------
	// Test Cases for 1.70
	//------------------------------------------------------------

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.OneNumberRangeImpl#OneNumberRangeImpl(long)}.
	 */
	public void testOneNumberRangeImplLong() {
		OneNumberRangeImpl range;
		
		// 0
		range = new OneNumberRangeImpl(0L);
		assertEquals((short)0, range.getShortFromValue());
		assertEquals(range.getShortFromValue(), range.getShortToValue());
		assertEquals(0, range.getIntegerFromValue());
		assertEquals(range.getIntegerFromValue(), range.getIntegerToValue());
		assertEquals(0L, range.getLongFromValue());
		assertEquals(range.getLongFromValue(), range.getLongToValue());
		assertEquals(0, BigDecimal.ZERO.compareTo(range.getDecimalFromValue()));
		assertEquals(range.getDecimalFromValue(), range.getDecimalToValue());
		
		// 1
		range = new OneNumberRangeImpl(1L);
		assertEquals((short)1, range.getShortFromValue());
		assertEquals(range.getShortFromValue(), range.getShortToValue());
		assertEquals(1, range.getIntegerFromValue());
		assertEquals(range.getIntegerFromValue(), range.getIntegerToValue());
		assertEquals(1L, range.getLongFromValue());
		assertEquals(range.getLongFromValue(), range.getLongToValue());
		assertEquals(0, BigDecimal.ONE.compareTo(range.getDecimalFromValue()));
		assertEquals(range.getDecimalFromValue(), range.getDecimalToValue());
		
		// -1
		range = new OneNumberRangeImpl(-1L);
		assertEquals((short)-1, range.getShortFromValue());
		assertEquals(range.getShortFromValue(), range.getShortToValue());
		assertEquals(-1, range.getIntegerFromValue());
		assertEquals(range.getIntegerFromValue(), range.getIntegerToValue());
		assertEquals(-1L, range.getLongFromValue());
		assertEquals(range.getLongFromValue(), range.getLongToValue());
		assertEquals(0, BigDecimal.ONE.negate().compareTo(range.getDecimalFromValue()));
		assertEquals(range.getDecimalFromValue(), range.getDecimalToValue());
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.OneNumberRangeImpl#OneNumberRangeImpl(double)}.
	 */
	public void testOneNumberRangeImplDouble() {
		OneNumberRangeImpl range;
		
		// Double.MIN_VALUE
		range = new OneNumberRangeImpl(Double.MIN_VALUE);
		assertEquals(0, BigDecimal.valueOf(Double.MIN_VALUE).compareTo(range.getDecimalFromValue()));
		assertEquals(range.getShortFromValue(), range.getShortToValue());
		assertEquals(range.getIntegerFromValue(), range.getIntegerToValue());
		assertEquals(range.getLongFromValue(), range.getLongToValue());
		assertEquals(range.getDecimalFromValue(), range.getDecimalToValue());
		
		// Double.MAX_VALUE
		range = new OneNumberRangeImpl(Double.MAX_VALUE);
		assertEquals(0, BigDecimal.valueOf(Double.MAX_VALUE).compareTo(range.getDecimalFromValue()));
		assertEquals(range.getShortFromValue(), range.getShortToValue());
		assertEquals(range.getIntegerFromValue(), range.getIntegerToValue());
		assertEquals(range.getLongFromValue(), range.getLongToValue());
		assertEquals(range.getDecimalFromValue(), range.getDecimalToValue());
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.OneNumberRangeImpl#OneNumberRangeImpl(java.math.BigDecimal)}.
	 */
	public void testOneNumberRangeImplBigDecimal() {
		OneNumberRangeImpl range;
		
		// ZERO
		range = new OneNumberRangeImpl(BigDecimal.ZERO);
		assertEquals(0, BigDecimal.ZERO.compareTo(range.getDecimalFromValue()));
		assertEquals(range.getShortFromValue(), range.getShortToValue());
		assertEquals(range.getIntegerFromValue(), range.getIntegerToValue());
		assertEquals(range.getLongFromValue(), range.getLongToValue());
		assertEquals(range.getDecimalFromValue(), range.getDecimalToValue());
		
		// ONE
		range = new OneNumberRangeImpl(BigDecimal.ONE);
		assertEquals(0, BigDecimal.ONE.compareTo(range.getDecimalFromValue()));
		assertEquals(range.getShortFromValue(), range.getShortToValue());
		assertEquals(range.getIntegerFromValue(), range.getIntegerToValue());
		assertEquals(range.getLongFromValue(), range.getLongToValue());
		assertEquals(range.getDecimalFromValue(), range.getDecimalToValue());
		
		// BigDecimal number
		BigDecimal val = new BigDecimal("-9876543210987654321.0123456789");
		range = new OneNumberRangeImpl(val);
		assertEquals(0, val.compareTo(range.getDecimalFromValue()));
		assertEquals(range.getShortFromValue(), range.getShortToValue());
		assertEquals(range.getIntegerFromValue(), range.getIntegerToValue());
		assertEquals(range.getLongFromValue(), range.getLongToValue());
		assertEquals(range.getDecimalFromValue(), range.getDecimalToValue());
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.OneNumberRangeImpl#getValueClass()}.
	 */
	public void testGetValueClass() {
		for (Map.Entry<BigDecimal, Class<?>> entry : valueClassMap.entrySet()) {
			OneNumberRangeImpl range = new OneNumberRangeImpl(entry.getKey());
			assertEquals(entry.getValue(), range.getValueClass());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.OneNumberRangeImpl#isEmpty()}.
	 */
	public void testIsEmpty() {
		for (BigDecimal val : valueClassMap.keySet()) {
			OneNumberRangeImpl range = new OneNumberRangeImpl(val);
			assertFalse(range.isEmpty());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.OneNumberRangeImpl#isIncremental()}.
	 */
	public void testIsIncremental() {
		for (BigDecimal val : valueClassMap.keySet()) {
			OneNumberRangeImpl range = new OneNumberRangeImpl(val);
			assertTrue(range.isIncremental());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.OneNumberRangeImpl#stepSignum()}.
	 */
	public void testStepSignum() {
		for (BigDecimal val : valueClassMap.keySet()) {
			OneNumberRangeImpl range = new OneNumberRangeImpl(val);
			assertEquals(1, range.stepSignum());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.OneNumberRangeImpl#isIncludeValue(java.math.BigDecimal)}.
	 */
	public void testIsIncludeValue() {
		BigDecimal minval = new BigDecimal("0.0000000000000000000000000000000000001");
		
		for (BigDecimal val : valueClassMap.keySet()) {
			OneNumberRangeImpl range = new OneNumberRangeImpl(val);
			assertTrue(range.isIncludeValue(val));
			assertFalse(range.isIncludeValue(val.add(minval)));
			assertFalse(range.isIncludeValue(val.subtract(minval)));
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.OneNumberRangeImpl#containsValue(java.math.BigDecimal)}.
	 */
	public void testContainsValue() {
		BigDecimal minval = new BigDecimal("0.0000000000000000000000000000000000001");
		
		for (BigDecimal val : valueClassMap.keySet()) {
			OneNumberRangeImpl range = new OneNumberRangeImpl(val);
			assertTrue(range.containsValue(val));
			assertFalse(range.containsValue(val.add(minval)));
			assertFalse(range.containsValue(val.subtract(minval)));
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.OneNumberRangeImpl#getShortFromValue()}.
	 */
	public void testGetShortFromValue() {
		for (BigDecimal val : valueClassMap.keySet()) {
			OneNumberRangeImpl range = new OneNumberRangeImpl(val);
			Class<?> clazz = range.getValueClass();
			BigDecimal getval = BigDecimal.valueOf((long)range.getShortFromValue());
			if (Short.class.equals(clazz)) {
				assertTrue(0 == val.compareTo(getval));
			} else {
				assertFalse(0 == val.compareTo(getval));
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.OneNumberRangeImpl#getIntegerFromValue()}.
	 */
	public void testGetIntegerFromValue() {
		for (BigDecimal val : valueClassMap.keySet()) {
			OneNumberRangeImpl range = new OneNumberRangeImpl(val);
			Class<?> clazz = range.getValueClass();
			BigDecimal getval = BigDecimal.valueOf((long)range.getIntegerFromValue());
			if (Short.class.equals(clazz) || Integer.class.equals(clazz)) {
				assertTrue(0 == val.compareTo(getval));
			} else {
				assertFalse(0 == val.compareTo(getval));
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.OneNumberRangeImpl#getLongFromValue()}.
	 */
	public void testGetLongFromValue() {
		for (BigDecimal val : valueClassMap.keySet()) {
			OneNumberRangeImpl range = new OneNumberRangeImpl(val);
			Class<?> clazz = range.getValueClass();
			BigDecimal getval = BigDecimal.valueOf(range.getLongFromValue());
			if (Short.class.equals(clazz) || Integer.class.equals(clazz) || Long.class.equals(clazz)) {
				assertTrue(0 == val.compareTo(getval));
			} else {
				assertFalse(0 == val.compareTo(getval));
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.OneNumberRangeImpl#getDecimalFromValue()}.
	 */
	public void testGetDecimalFromValue() {
		for (BigDecimal val : valueClassMap.keySet()) {
			OneNumberRangeImpl range = new OneNumberRangeImpl(val);
			assertTrue(0 == val.compareTo(range.getDecimalFromValue()));
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.OneNumberRangeImpl#getShortToValue()}.
	 */
	public void testGetShortToValue() {
		for (BigDecimal val : valueClassMap.keySet()) {
			OneNumberRangeImpl range = new OneNumberRangeImpl(val);
			Class<?> clazz = range.getValueClass();
			BigDecimal getval = BigDecimal.valueOf((long)range.getShortToValue());
			if (Short.class.equals(clazz)) {
				assertTrue(0 == val.compareTo(getval));
			} else {
				assertFalse(0 == val.compareTo(getval));
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.OneNumberRangeImpl#getIntegerToValue()}.
	 */
	public void testGetIntegerToValue() {
		for (BigDecimal val : valueClassMap.keySet()) {
			OneNumberRangeImpl range = new OneNumberRangeImpl(val);
			Class<?> clazz = range.getValueClass();
			BigDecimal getval = BigDecimal.valueOf((long)range.getIntegerToValue());
			if (Short.class.equals(clazz) || Integer.class.equals(clazz)) {
				assertTrue(0 == val.compareTo(getval));
			} else {
				assertFalse(0 == val.compareTo(getval));
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.OneNumberRangeImpl#getLongToValue()}.
	 */
	public void testGetLongToValue() {
		for (BigDecimal val : valueClassMap.keySet()) {
			OneNumberRangeImpl range = new OneNumberRangeImpl(val);
			Class<?> clazz = range.getValueClass();
			BigDecimal getval = BigDecimal.valueOf(range.getLongToValue());
			if (Short.class.equals(clazz) || Integer.class.equals(clazz) || Long.class.equals(clazz)) {
				assertTrue(0 == val.compareTo(getval));
			} else {
				assertFalse(0 == val.compareTo(getval));
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.OneNumberRangeImpl#getDecimalToValue()}.
	 */
	public void testGetDecimalToValue() {
		for (BigDecimal val : valueClassMap.keySet()) {
			OneNumberRangeImpl range = new OneNumberRangeImpl(val);
			assertTrue(0 == val.compareTo(range.getDecimalToValue()));
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.OneNumberRangeImpl#getShortStepValue()}.
	 */
	public void testGetShortStepValue() {
		short ss = 1;
		for (BigDecimal val : valueClassMap.keySet()) {
			OneNumberRangeImpl range = new OneNumberRangeImpl(val);
			assertEquals(ss, range.getShortStepValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.OneNumberRangeImpl#getIntegerStepValue()}.
	 */
	public void testGetIntegerStepValue() {
		for (BigDecimal val : valueClassMap.keySet()) {
			OneNumberRangeImpl range = new OneNumberRangeImpl(val);
			assertEquals(1, range.getIntegerStepValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.OneNumberRangeImpl#getLongStepValue()}.
	 */
	public void testGetLongStepValue() {
		for (BigDecimal val : valueClassMap.keySet()) {
			OneNumberRangeImpl range = new OneNumberRangeImpl(val);
			assertEquals(1L, range.getLongStepValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.OneNumberRangeImpl#getDecimalStepValue()}.
	 */
	public void testGetDecimalStepValue() {
		for (BigDecimal val : valueClassMap.keySet()) {
			OneNumberRangeImpl range = new OneNumberRangeImpl(val);
			assertEquals(BigDecimal.ONE, range.getDecimalStepValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.OneNumberRangeImpl#getShortRangeIterator()}.
	 */
	public void testGetShortRangeIterator() {
		for (BigDecimal val : valueClassMap.keySet()) {
			OneNumberRangeImpl range = new OneNumberRangeImpl(val);
			Iterator<Short> it = range.getShortRangeIterator();
			assertTrue(it.hasNext());
			BigDecimal getval = BigDecimal.valueOf(it.next().longValue());
			assertFalse(it.hasNext());
			try {
				it.next();
				fail("Must be throw NoSuchElementException.");
			} catch (NoSuchElementException ex) {
				assertTrue(true);
			}
			Class<?> clazz = range.getValueClass();
			if (Short.class.equals(clazz)) {
				assertTrue(0 == val.compareTo(getval));
			} else {
				assertFalse(0 == val.compareTo(getval));
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.OneNumberRangeImpl#getIntegerRangeIterator()}.
	 */
	public void testGetIntegerRangeIterator() {
		for (BigDecimal val : valueClassMap.keySet()) {
			OneNumberRangeImpl range = new OneNumberRangeImpl(val);
			Iterator<Integer> it = range.getIntegerRangeIterator();
			assertTrue(it.hasNext());
			BigDecimal getval = BigDecimal.valueOf(it.next().longValue());
			assertFalse(it.hasNext());
			try {
				it.next();
				fail("Must be throw NoSuchElementException.");
			} catch (NoSuchElementException ex) {
				assertTrue(true);
			}
			Class<?> clazz = range.getValueClass();
			if (Short.class.equals(clazz) || Integer.class.equals(clazz)) {
				assertTrue(0 == val.compareTo(getval));
			} else {
				assertFalse(0 == val.compareTo(getval));
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.OneNumberRangeImpl#getLongRangeIterator()}.
	 */
	public void testGetLongRangeIterator() {
		for (BigDecimal val : valueClassMap.keySet()) {
			OneNumberRangeImpl range = new OneNumberRangeImpl(val);
			Iterator<Long> it = range.getLongRangeIterator();
			assertTrue(it.hasNext());
			BigDecimal getval = BigDecimal.valueOf(it.next().longValue());
			assertFalse(it.hasNext());
			try {
				it.next();
				fail("Must be throw NoSuchElementException.");
			} catch (NoSuchElementException ex) {
				assertTrue(true);
			}
			Class<?> clazz = range.getValueClass();
			if (Short.class.equals(clazz) || Integer.class.equals(clazz) || Long.class.equals(clazz)) {
				assertTrue(0 == val.compareTo(getval));
			} else {
				assertFalse(0 == val.compareTo(getval));
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.OneNumberRangeImpl#getDecimalRangeIterator()}.
	 */
	public void testGetDecimalRangeIterator() {
		for (BigDecimal val : valueClassMap.keySet()) {
			OneNumberRangeImpl range = new OneNumberRangeImpl(val);
			Iterator<BigDecimal> it = range.getDecimalRangeIterator();
			assertTrue(it.hasNext());
			BigDecimal getval = it.next();
			assertFalse(it.hasNext());
			try {
				it.next();
				fail("Must be throw NoSuchElementException.");
			} catch (NoSuchElementException ex) {
				assertTrue(true);
			}
			assertTrue(0 == val.compareTo(getval));
		}
	}
}
