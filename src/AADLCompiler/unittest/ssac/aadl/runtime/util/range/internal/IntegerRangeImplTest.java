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
 * @(#)IntegerRangeImplTest.java	1.70	2011/05/16
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.util.range.internal;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.NoSuchElementException;

import ssac.aadl.runtime.util.range.RangeUtil;

import junit.framework.TestCase;

/**
 * {@link ssac.aadl.runtime.util.range.internal.IntegerRangeImpl} クラスのテスト。
 * 
 * @version 1.70	2011/05/16
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * 
 * @since 1.70
 */
public class IntegerRangeImplTest extends TestCase
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final int[][] rangeDefs = {
		{ 0, 0, 0, 0 },
		{ 0, 1, 1, 1 },
		{ -10, 10, 2, 2 },
		{ 10, -10, -2, -2 },
		{ -32768, 19660, 13107, 13107 },
		{ 32767, -19661, -13107, -13107 },
		{ -2147483648, 1288490188, 858993459, 858993459 },
		{ 2147483647, -1288490189, -858993459, -858993459 },
		{ 10, -10, 2, 0 },
		{ -10, 10, -2, 0 },
		{ Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE - 10, 0 },
	};
	
	static private final int[][] rangeContains = {
		{},
		{ 0, 1 },
		{ -10, -8, -6, -4, -2, 0, 2, 4, 6, 8, 10 },
		{ 10, 8, 6, 4, 2, 0, -2, -4, -6, -8, -10 },
		{ -32768, -19661, -6554, 6553, 19660 },
		{ 32767, 19660, 6553, -6554, -19661 },
		{ -2147483648, -1288490189, -429496730, 429496729, 1288490188 },
		{ 2147483647, 1288490188, 429496729, -429496730, -1288490189 },
		{},
		{},
		{},
	};
	
	static private final int[][] rangeNotIncludes = {
		{ Integer.MIN_VALUE, 0, Integer.MAX_VALUE },
		{ Integer.MIN_VALUE, -1, 2, Integer.MAX_VALUE },
		{ Integer.MIN_VALUE, -11, 11, Integer.MAX_VALUE },
		{ Integer.MAX_VALUE, 11, -11, Integer.MIN_VALUE },
		{ 19661, Short.MAX_VALUE },
		{ Short.MIN_VALUE, -19662 },
		{ 1288490189, Integer.MAX_VALUE },
		{ Integer.MIN_VALUE, -1288490190 },
		{ Integer.MIN_VALUE, -10, 0, 10, Integer.MAX_VALUE },
		{ Integer.MIN_VALUE, -10, 0, 10, Integer.MAX_VALUE },
		{ Integer.MIN_VALUE, 0, Integer.MAX_VALUE },
	};
	
	static private final int[][] rangeNotContains = {
		{ Integer.MIN_VALUE, 0, Integer.MAX_VALUE },
		{ Integer.MIN_VALUE, -1, 2, Integer.MAX_VALUE },
		{ Integer.MIN_VALUE, -11, -1, 1, 11, Integer.MAX_VALUE },
		{ Integer.MAX_VALUE, 11, -1, 1, -11, Integer.MIN_VALUE },
		{ Short.MAX_VALUE, -32767, -19660, -6553, 6552, 19661 },
		{ Short.MIN_VALUE, 32766, 19659, 6552, -6555, -19662 },
		{ Integer.MAX_VALUE, -2147483647, -1288490188, -429496729, 429496730, 1288490189 },
		{ Integer.MIN_VALUE, 2147483646, 1288490187, 429496728, -429496731, -1288490190 },
		{ Integer.MIN_VALUE, -10, 0, 10, Integer.MAX_VALUE },
		{ Integer.MIN_VALUE, -10, 0, 10, Integer.MAX_VALUE },
		{ Integer.MIN_VALUE, 0, Integer.MAX_VALUE },
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
	
	static protected int signum(int step) {
		if (step > 0) {
			return 1;
		}
		else if (step < 0) {
			return -1;
		}
		else {
			return 0;
		}
	}
	
	static protected BigDecimal toDecimal(int value) {
		return BigDecimal.valueOf((long)value);
	}

	//------------------------------------------------------------
	// Test Cases for 1.70
	//------------------------------------------------------------
	
	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.IntegerRangeImpl#IntegerRangeImpl(int, int, int)}.
	 */
	public void testIntegerRangeImpl() {
		for (int i = 0; i < rangeDefs.length; i++) {
			int[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			IntegerRangeImpl range = new IntegerRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, def[0], range.getIntegerFromValue());
			assertEquals(msg, def[1], range.getIntegerToValue());
			assertEquals(msg, def[3], range.getIntegerStepValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.IntegerRangeImpl#getValueClass()}.
	 */
	public void testGetValueClass() {
		for (int i = 0; i < rangeDefs.length; i++) {
			int[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			IntegerRangeImpl range = new IntegerRangeImpl(def[0], def[1], def[2]);
			if (RangeUtil.exactlyShortValues(toDecimal(def[0]), toDecimal(def[1]), toDecimal(def[3]))) {
				assertEquals(msg, Short.class, range.getValueClass());
			} else {
				assertEquals(msg, Integer.class, range.getValueClass());
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.IntegerRangeImpl#isEmpty()}.
	 */
	public void testIsEmpty() {
		for (int i = 0; i < rangeDefs.length; i++) {
			int[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			IntegerRangeImpl range = new IntegerRangeImpl(def[0], def[1], def[2]);
			if (def[3] != 0) {
				assertFalse(msg, range.isEmpty());
			} else {
				assertTrue(msg, range.isEmpty());
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.IntegerRangeImpl#isIncremental()}.
	 */
	public void testIsIncremental() {
		for (int i = 0; i < rangeDefs.length; i++) {
			int[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			IntegerRangeImpl range = new IntegerRangeImpl(def[0], def[1], def[2]);
			if (def[3] < 0) {
				assertFalse(msg, range.isIncremental());
			} else {
				assertTrue(msg, range.isIncremental());
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.IntegerRangeImpl#stepSignum()}.
	 */
	public void testStepSignum() {
		for (int i = 0; i < rangeDefs.length; i++) {
			int[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			IntegerRangeImpl range = new IntegerRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, signum(def[3]), range.stepSignum());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.IntegerRangeImpl#isIncludeValue(java.math.BigDecimal)}.
	 */
	public void testIsIncludeValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			int[] def = rangeDefs[i];
			int[] con = rangeContains[i];
			int[] ni = rangeNotIncludes[i];
			String msg = String.format("Test params : [%d] ", i);
			IntegerRangeImpl range = new IntegerRangeImpl(def[0], def[1], def[2]);
			//--- includes
			for (int v : con) {
				assertTrue(msg + " value=" + v, range.isIncludeValue(toDecimal(v)));
			}
			//--- not includes
			for (int v : ni) {
				assertFalse(msg + " value=" + v, range.isIncludeValue(toDecimal(v)));
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.IntegerRangeImpl#containsValue(java.math.BigDecimal)}.
	 */
	public void testContainsValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			int[] def = rangeDefs[i];
			int[] con = rangeContains[i];
			int[] ni = rangeNotContains[i];
			String msg = String.format("Test params : [%d] ", i);
			IntegerRangeImpl range = new IntegerRangeImpl(def[0], def[1], def[2]);
			//--- includes
			for (int v : con) {
				assertTrue(msg + " value=" + v, range.containsValue(toDecimal(v)));
			}
			//--- not includes
			for (int v : ni) {
				assertFalse(msg + " value=" + v, range.containsValue(toDecimal(v)));
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.IntegerRangeImpl#getShortFromValue()}.
	 */
	public void testGetShortFromValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			int[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			IntegerRangeImpl range = new IntegerRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, (short)def[0], range.getShortFromValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.IntegerRangeImpl#getIntegerFromValue()}.
	 */
	public void testGetIntegerFromValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			int[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			IntegerRangeImpl range = new IntegerRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, (int)def[0], range.getIntegerFromValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.IntegerRangeImpl#getLongFromValue()}.
	 */
	public void testGetLongFromValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			int[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			IntegerRangeImpl range = new IntegerRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, (long)def[0], range.getLongFromValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.IntegerRangeImpl#getDecimalFromValue()}.
	 */
	public void testGetDecimalFromValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			int[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			IntegerRangeImpl range = new IntegerRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, 0, toDecimal(def[0]).compareTo(range.getDecimalFromValue()));
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.IntegerRangeImpl#getShortToValue()}.
	 */
	public void testGetShortToValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			int[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			IntegerRangeImpl range = new IntegerRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, (short)def[1], range.getShortToValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.IntegerRangeImpl#getIntegerToValue()}.
	 */
	public void testGetIntegerToValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			int[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			IntegerRangeImpl range = new IntegerRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, (int)def[1], range.getIntegerToValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.IntegerRangeImpl#getLongToValue()}.
	 */
	public void testGetLongToValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			int[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			IntegerRangeImpl range = new IntegerRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, (long)def[1], range.getLongToValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.IntegerRangeImpl#getDecimalToValue()}.
	 */
	public void testGetDecimalToValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			int[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			IntegerRangeImpl range = new IntegerRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, 0, toDecimal(def[1]).compareTo(range.getDecimalToValue()));
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.IntegerRangeImpl#getShortStepValue()}.
	 */
	public void testGetShortStepValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			int[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			IntegerRangeImpl range = new IntegerRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, (short)def[3], range.getShortStepValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.IntegerRangeImpl#getIntegerStepValue()}.
	 */
	public void testGetIntegerStepValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			int[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			IntegerRangeImpl range = new IntegerRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, (int)def[3], range.getIntegerStepValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.IntegerRangeImpl#getLongStepValue()}.
	 */
	public void testGetLongStepValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			int[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			IntegerRangeImpl range = new IntegerRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, (long)def[3], range.getLongStepValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.IntegerRangeImpl#getDecimalStepValue()}.
	 */
	public void testGetDecimalStepValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			int[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			IntegerRangeImpl range = new IntegerRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, 0, toDecimal(def[3]).compareTo(range.getDecimalStepValue()));
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.IntegerRangeImpl#getShortRangeIterator()}.
	 */
	public void testGetShortRangeIterator() {
		for (int i = 0; i < rangeDefs.length; i++) {
			int[] def = rangeDefs[i];
			int[] con = rangeContains[i];
			String msg = String.format("Test params : [%d] ", i);
			IntegerRangeImpl range = new IntegerRangeImpl(def[0], def[1], def[2]);
			Iterator<Short> it = range.getShortRangeIterator();
			if (con.length > 0) {
				for (int v : con) {
					String vmsg = msg + " value=" + v;
					assertTrue(vmsg, it.hasNext());
					assertEquals(vmsg, (short)v, it.next().shortValue());
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
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.IntegerRangeImpl#getIntegerRangeIterator()}.
	 */
	public void testGetIntegerRangeIterator() {
		for (int i = 0; i < rangeDefs.length; i++) {
			int[] def = rangeDefs[i];
			int[] con = rangeContains[i];
			String msg = String.format("Test params : [%d] ", i);
			IntegerRangeImpl range = new IntegerRangeImpl(def[0], def[1], def[2]);
			Iterator<Integer> it = range.getIntegerRangeIterator();
			if (con.length > 0) {
				for (int v : con) {
					String vmsg = msg + " value=" + v;
					assertTrue(vmsg, it.hasNext());
					assertEquals(vmsg, (int)v, it.next().intValue());
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
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.IntegerRangeImpl#getLongRangeIterator()}.
	 */
	public void testGetLongRangeIterator() {
		for (int i = 0; i < rangeDefs.length; i++) {
			int[] def = rangeDefs[i];
			int[] con = rangeContains[i];
			String msg = String.format("Test params : [%d] ", i);
			IntegerRangeImpl range = new IntegerRangeImpl(def[0], def[1], def[2]);
			Iterator<Long> it = range.getLongRangeIterator();
			if (con.length > 0) {
				for (int v : con) {
					String vmsg = msg + " value=" + v;
					assertTrue(vmsg, it.hasNext());
					assertEquals(vmsg, (long)v, it.next().longValue());
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
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.IntegerRangeImpl#getDecimalRangeIterator()}.
	 */
	public void testGetDecimalRangeIterator() {
		for (int i = 0; i < rangeDefs.length; i++) {
			int[] def = rangeDefs[i];
			int[] con = rangeContains[i];
			String msg = String.format("Test params : [%d] ", i);
			IntegerRangeImpl range = new IntegerRangeImpl(def[0], def[1], def[2]);
			Iterator<BigDecimal> it = range.getDecimalRangeIterator();
			if (con.length > 0) {
				for (int v : con) {
					String vmsg = msg + " value=" + v;
					assertTrue(vmsg, it.hasNext());
					assertEquals(vmsg, 0, toDecimal(v).compareTo(it.next()));
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
