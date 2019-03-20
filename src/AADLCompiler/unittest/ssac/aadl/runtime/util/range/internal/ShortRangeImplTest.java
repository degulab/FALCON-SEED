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
 * @(#)ShortRangeImplTest.java	1.70	2011/05/12
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.util.range.internal;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.NoSuchElementException;

import junit.framework.TestCase;

/**
 * {@link ssac.aadl.runtime.util.range.internal.ShortRangeImpl} クラスのテスト。
 * 
 * @version 1.70	2011/05/12
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * 
 * @since 1.70
 */
public class ShortRangeImplTest extends TestCase
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final short SZERO = 0;
	static private final short SONE  = 1;
	static private final short STWO  = 2;
	static private final short STEN  = 10;
	
	static private final short[][] rangeDefs = {
		{ SZERO, SZERO, SZERO, SZERO },
		{ SZERO, SONE, SONE, SONE },
		{ -STEN, STEN, STWO, STWO },
		{ STEN, -STEN, -STWO, -STWO },
		{ -32768, 19660, 13107, 13107 },
		{ 32767, -19661, -13107, -13107 },
		{ STEN, -STEN, STWO, SZERO },
		{ -STEN, STEN, -STWO, SZERO },
		{ Short.MIN_VALUE, Short.MAX_VALUE, Short.MAX_VALUE - STEN, SZERO },
	};
	
	static private final short[][] rangeContains = {
		{},
		{ 0, 1 },
		{ -10, -8, -6, -4, -2, 0, 2, 4, 6, 8, 10 },
		{ 10, 8, 6, 4, 2, 0, -2, -4, -6, -8, -10 },
		{ -32768, -19661, -6554, 6553, 19660 },
		{ 32767, 19660, 6553, -6554, -19661 },
		{},
		{},
		{},
	};
	
	static private final short[][] rangeNotIncludes = {
		{ Short.MIN_VALUE, 0, Short.MAX_VALUE },
		{ Short.MIN_VALUE, -1, 2, Short.MAX_VALUE },
		{ Short.MIN_VALUE, -11, 11, Short.MAX_VALUE },
		{ Short.MAX_VALUE, 11, -11, Short.MIN_VALUE },
		{ 19661, Short.MAX_VALUE },
		{ Short.MIN_VALUE, -19662 },
		{ Short.MIN_VALUE, -10, 0, 10, Short.MAX_VALUE },
		{ Short.MIN_VALUE, -10, 0, 10, Short.MAX_VALUE },
		{ Short.MIN_VALUE, 0, Short.MAX_VALUE },
	};
	
	static private final short[][] rangeNotContains = {
		{ Short.MIN_VALUE, 0, Short.MAX_VALUE },
		{ Short.MIN_VALUE, -1, 2, Short.MAX_VALUE },
		{ Short.MIN_VALUE, -11, -1, 1, 11, Short.MAX_VALUE },
		{ Short.MAX_VALUE, 11, -1, 1, -11, Short.MIN_VALUE },
		{ Short.MAX_VALUE, -32767, -19660, -6553, 6552, 19661 },
		{ Short.MIN_VALUE, 32766, 19659, 6552, -6555, -19662 },
		{ Short.MIN_VALUE, -10, 0, 10, Short.MAX_VALUE },
		{ Short.MIN_VALUE, -10, 0, 10, Short.MAX_VALUE },
		{ Short.MIN_VALUE, 0, Short.MAX_VALUE },
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
	
	static protected int signum(short step) {
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
	
	static protected BigDecimal toDecimal(short value) {
		return BigDecimal.valueOf((long)value);
	}

	//------------------------------------------------------------
	// Test Cases for 1.70
	//------------------------------------------------------------

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.ShortRangeImpl#ShortRangeImpl(short, short, short)}.
	 */
	public void testShortRangeImpl() {
		for (int i = 0; i < rangeDefs.length; i++) {
			short[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			ShortRangeImpl range = new ShortRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, def[0], range.getShortFromValue());
			assertEquals(msg, def[1], range.getShortToValue());
			assertEquals(msg, def[3], range.getShortStepValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.ShortRangeImpl#getValueClass()}.
	 */
	public void testGetValueClass() {
		for (int i = 0; i < rangeDefs.length; i++) {
			short[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			ShortRangeImpl range = new ShortRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, Short.class, range.getValueClass());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.ShortRangeImpl#isEmpty()}.
	 */
	public void testIsEmpty() {
		for (int i = 0; i < rangeDefs.length; i++) {
			short[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			ShortRangeImpl range = new ShortRangeImpl(def[0], def[1], def[2]);
			if (def[3] != 0) {
				assertFalse(msg, range.isEmpty());
			} else {
				assertTrue(msg, range.isEmpty());
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.ShortRangeImpl#isIncremental()}.
	 */
	public void testIsIncremental() {
		for (int i = 0; i < rangeDefs.length; i++) {
			short[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			ShortRangeImpl range = new ShortRangeImpl(def[0], def[1], def[2]);
			if (def[3] < 0) {
				assertFalse(msg, range.isIncremental());
			} else {
				assertTrue(msg, range.isIncremental());
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.ShortRangeImpl#stepSignum()}.
	 */
	public void testStepSignum() {
		for (int i = 0; i < rangeDefs.length; i++) {
			short[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			ShortRangeImpl range = new ShortRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, signum(def[3]), range.stepSignum());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.ShortRangeImpl#isIncludeValue(java.math.BigDecimal)}.
	 */
	public void testIsIncludeValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			short[] def = rangeDefs[i];
			short[] con = rangeContains[i];
			short[] ni = rangeNotIncludes[i];
			String msg = String.format("Test params : [%d] ", i);
			ShortRangeImpl range = new ShortRangeImpl(def[0], def[1], def[2]);
			//--- includes
			for (short v : con) {
				assertTrue(msg + " value=" + v, range.isIncludeValue(toDecimal(v)));
			}
			//--- not includes
			for (short v : ni) {
				assertFalse(msg + " value=" + v, range.isIncludeValue(toDecimal(v)));
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.ShortRangeImpl#containsValue(java.math.BigDecimal)}.
	 */
	public void testContainsValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			short[] def = rangeDefs[i];
			short[] con = rangeContains[i];
			short[] ni = rangeNotContains[i];
			String msg = String.format("Test params : [%d] ", i);
			ShortRangeImpl range = new ShortRangeImpl(def[0], def[1], def[2]);
			//--- includes
			for (short v : con) {
				assertTrue(msg + " value=" + v, range.containsValue(toDecimal(v)));
			}
			//--- not includes
			for (short v : ni) {
				assertFalse(msg + " value=" + v, range.containsValue(toDecimal(v)));
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.ShortRangeImpl#getShortFromValue()}.
	 */
	public void testGetShortFromValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			short[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			ShortRangeImpl range = new ShortRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, def[0], range.getShortFromValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.ShortRangeImpl#getIntegerFromValue()}.
	 */
	public void testGetIntegerFromValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			short[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			ShortRangeImpl range = new ShortRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, (int)def[0], range.getIntegerFromValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.ShortRangeImpl#getLongFromValue()}.
	 */
	public void testGetLongFromValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			short[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			ShortRangeImpl range = new ShortRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, (long)def[0], range.getLongFromValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.ShortRangeImpl#getDecimalFromValue()}.
	 */
	public void testGetDecimalFromValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			short[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			ShortRangeImpl range = new ShortRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, 0, toDecimal(def[0]).compareTo(range.getDecimalFromValue()));
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.ShortRangeImpl#getShortToValue()}.
	 */
	public void testGetShortToValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			short[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			ShortRangeImpl range = new ShortRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, def[1], range.getShortToValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.ShortRangeImpl#getIntegerToValue()}.
	 */
	public void testGetIntegerToValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			short[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			ShortRangeImpl range = new ShortRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, (int)def[1], range.getIntegerToValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.ShortRangeImpl#getLongToValue()}.
	 */
	public void testGetLongToValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			short[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			ShortRangeImpl range = new ShortRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, (long)def[1], range.getLongToValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.ShortRangeImpl#getDecimalToValue()}.
	 */
	public void testGetDecimalToValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			short[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			ShortRangeImpl range = new ShortRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, 0, toDecimal(def[1]).compareTo(range.getDecimalToValue()));
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.ShortRangeImpl#getShortStepValue()}.
	 */
	public void testGetShortStepValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			short[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			ShortRangeImpl range = new ShortRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, def[3], range.getShortStepValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.ShortRangeImpl#getIntegerStepValue()}.
	 */
	public void testGetIntegerStepValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			short[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			ShortRangeImpl range = new ShortRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, (int)def[3], range.getIntegerStepValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.ShortRangeImpl#getLongStepValue()}.
	 */
	public void testGetLongStepValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			short[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			ShortRangeImpl range = new ShortRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, (long)def[3], range.getLongStepValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.ShortRangeImpl#getDecimalStepValue()}.
	 */
	public void testGetDecimalStepValue() {
		for (int i = 0; i < rangeDefs.length; i++) {
			short[] def = rangeDefs[i];
			String msg = String.format("Test params : [%d] ", i);
			ShortRangeImpl range = new ShortRangeImpl(def[0], def[1], def[2]);
			assertEquals(msg, 0, toDecimal(def[3]).compareTo(range.getDecimalStepValue()));
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.ShortRangeImpl#getShortRangeIterator()}.
	 */
	public void testGetShortRangeIterator() {
		for (int i = 0; i < rangeDefs.length; i++) {
			short[] def = rangeDefs[i];
			short[] con = rangeContains[i];
			String msg = String.format("Test params : [%d] ", i);
			ShortRangeImpl range = new ShortRangeImpl(def[0], def[1], def[2]);
			Iterator<Short> it = range.getShortRangeIterator();
			if (con.length > 0) {
				for (short v : con) {
					String vmsg = msg + " value=" + v;
					assertTrue(vmsg, it.hasNext());
					assertEquals(vmsg, v, it.next().shortValue());
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
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.ShortRangeImpl#getIntegerRangeIterator()}.
	 */
	public void testGetIntegerRangeIterator() {
		for (int i = 0; i < rangeDefs.length; i++) {
			short[] def = rangeDefs[i];
			short[] con = rangeContains[i];
			String msg = String.format("Test params : [%d] ", i);
			ShortRangeImpl range = new ShortRangeImpl(def[0], def[1], def[2]);
			Iterator<Integer> it = range.getIntegerRangeIterator();
			if (con.length > 0) {
				for (short v : con) {
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
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.ShortRangeImpl#getLongRangeIterator()}.
	 */
	public void testGetLongRangeIterator() {
		for (int i = 0; i < rangeDefs.length; i++) {
			short[] def = rangeDefs[i];
			short[] con = rangeContains[i];
			String msg = String.format("Test params : [%d] ", i);
			ShortRangeImpl range = new ShortRangeImpl(def[0], def[1], def[2]);
			Iterator<Long> it = range.getLongRangeIterator();
			if (con.length > 0) {
				for (short v : con) {
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
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.ShortRangeImpl#getDecimalRangeIterator()}.
	 */
	public void testGetDecimalRangeIterator() {
		for (int i = 0; i < rangeDefs.length; i++) {
			short[] def = rangeDefs[i];
			short[] con = rangeContains[i];
			String msg = String.format("Test params : [%d] ", i);
			ShortRangeImpl range = new ShortRangeImpl(def[0], def[1], def[2]);
			Iterator<BigDecimal> it = range.getDecimalRangeIterator();
			if (con.length > 0) {
				for (short v : con) {
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
