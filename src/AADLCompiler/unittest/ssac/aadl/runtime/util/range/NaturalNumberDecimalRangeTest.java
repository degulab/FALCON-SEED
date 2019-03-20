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
 * @(#)NaturalNumberDecimalRangeTest.java	1.70	2011/05/17
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.util.range;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.NoSuchElementException;

import junit.framework.TestCase;
import ssac.aadl.runtime.util.range.internal.NumberRangeImpl;

/**
 * {@link ssac.aadl.runtime.util.range.NaturalNumberDecimalRange} クラスのテスト。
 * 
 * @version 1.70	2011/05/17
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * 
 * @since 1.70
 */
public class NaturalNumberDecimalRangeTest extends TestCase
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final VerifyNaturalRangePattern bigRangePattern = new VerifyNaturalRangePattern(
			"3-4, 1-2, 0-4, 32767-32765,"
			+ "9223372036854775806-9223372036854775804, 9223372036854775803, 9223372036854775808-9223372036854775810, 9223372036854775807,"
			+ "2147483647-2147483645, 2147483643, 2147483644, 2147483647-2147483650,"
			+ "32763, 32764, 32768-32770,"
			+ "12345678901234567891, 12345678901234567888, 12345678901234567889-,"
			+ "7, 2147483640, 2147483652, 9223372036854775800, 9223372036854775812, 12345678901234567885, 12345678901234567895", toDecimal("12345678901234567890"),
			toDecimal(0), toDecimal("12345678901234567890"), toDecimal(1), toDecimal(0), toDecimal("12345678901234567890"),
			new BigDecimal[]{
				toDecimal(0), toDecimal(1), toDecimal(2), toDecimal(3), toDecimal(4), toDecimal(7),
				toDecimal(32763), toDecimal(32764), toDecimal(32765), toDecimal(32766),
				toDecimal(32767), toDecimal(32768), toDecimal(32769), toDecimal(32770),
				toDecimal(2147483640L), toDecimal(2147483643L), toDecimal(2147483644L), toDecimal(2147483645L), toDecimal(2147483646L),
				toDecimal(2147483647L), toDecimal(2147483648L), toDecimal(2147483649L), toDecimal(2147483650L), toDecimal(2147483652L),
				toDecimal(9223372036854775800L), toDecimal(9223372036854775803L), toDecimal(9223372036854775804L), toDecimal(9223372036854775805L), toDecimal(9223372036854775806L),
				toDecimal("9223372036854775807"), toDecimal("9223372036854775808"), toDecimal("9223372036854775809"), toDecimal("9223372036854775810"), toDecimal("9223372036854775812"),
				toDecimal("12345678901234567885"), toDecimal("12345678901234567888"), toDecimal("12345678901234567889"), toDecimal("12345678901234567890"),
			},
			new Object[]{
				(short)(0), 1, 2L, 3.0f, 4.0, toDecimal(7),
				32763, 32764, 32765, 32766,
				toDecimal(32767), toDecimal(32768), toDecimal(32769), toDecimal(32770),
				2147483640L, 2147483643L, toDecimal(2147483644L), toDecimal(2147483645L), toDecimal(2147483646L),
				toDecimal(2147483647L), toDecimal(2147483648L), toDecimal(2147483649L), toDecimal(2147483650L), toDecimal(2147483652L),
				toDecimal(9223372036854775800L), toDecimal(9223372036854775803L), toDecimal(9223372036854775804L), toDecimal(9223372036854775805L), toDecimal(9223372036854775806L),
				toDecimal("9223372036854775807"), toDecimal("9223372036854775808"), toDecimal("9223372036854775809"), toDecimal("9223372036854775810"), toDecimal("9223372036854775812"),
				toDecimal("12345678901234567885"), toDecimal("12345678901234567888"), toDecimal("12345678901234567889"), toDecimal("12345678901234567890"),
			},
			new Object[]{
				Short.MIN_VALUE, -1, 0.5, 5,
				toDecimal(32762), 32762.5, 32769.9f, 32771L,
				2147483642L, toDecimal("2147483649.999999"), toDecimal(2147483651L),
				toDecimal(9223372036854775802L), toDecimal("9223372036854775806.9999999"), toDecimal("9223372036854775810.0000000001"), toDecimal("9223372036854775811"),
				toDecimal("12345678901234567887"), toDecimal("12345678901234567890.000000001"), toDecimal("12345678901234567891"),
				"0", "hoge",
			}
	);
	
	static private final String rangeStringFromBigRange = 
		"0-4,"
		+ "7,"
		+ "32763-32770,"
		+ "2147483640,"
		+ "2147483643-2147483650,"
		+ "2147483652,"
		+ "9223372036854775800,"
		+ "9223372036854775803-9223372036854775806,"
		+ "9223372036854775807-9223372036854775810,"
		+ "9223372036854775812,"
		+ "12345678901234567885,"
		+ "12345678901234567888-12345678901234567890";
	
	static public final VerifyNaturalRangePattern[] naturalRangePatterns = {
		//--- [0] null string (empty range)
		new VerifyNaturalRangePattern(null, null,
				toDecimal(0), toDecimal(0), toDecimal(0), null, null,
				new BigDecimal[]{},
				new Object[]{},
				new Object[]{0, Short.MAX_VALUE, Integer.MAX_VALUE, Long.MAX_VALUE, toDecimal("3"), "0", "hoge", }
		),
		//--- [1] empty string (empty range)
		new VerifyNaturalRangePattern("", toDecimal(Integer.MAX_VALUE),
				toDecimal(0), toDecimal(0), toDecimal(0), null, null,
				new BigDecimal[]{},
				new Object[]{},
				new Object[]{0, Short.MAX_VALUE, Integer.MAX_VALUE, Long.MAX_VALUE, toDecimal("3"), "0", "hoge", }
		),
		//--- [2] short range 1
		new VerifyNaturalRangePattern("15,25,-3,32764", toDecimal(32766),
				toDecimal(1), toDecimal(32764), toDecimal(1), toDecimal(1), toDecimal(32764),
				new BigDecimal[]{
					toDecimal(1), toDecimal(2), toDecimal(3), toDecimal(15), toDecimal(25), toDecimal(32764),
				},
				new Object[]{
					(short)1, 2, 3L, 15.0f, 25.0, toDecimal(32764),
				},
				new Object[]{
					Short.MIN_VALUE, 0, 0.9, 10L, toDecimal("32765.5"), 32767.0f, toDecimal(Long.MAX_VALUE),
					"0", "hoge",
				}
		),
		//--- [3] short range 2
		new VerifyNaturalRangePattern("3, 4, 5, 2-4, 32765, 32764-", toDecimal(32766),
				toDecimal(2), toDecimal(32766), toDecimal(1), toDecimal(2), toDecimal(32766),
				new BigDecimal[]{
					toDecimal(2), toDecimal(3), toDecimal(4), toDecimal(5), toDecimal(32764), toDecimal(32765), toDecimal(32766),
				},
				new Object[]{
					(short)2, 3, 4L, 5.0f, 32764.0, toDecimal(32765), toDecimal(32766),
				},
				new Object[]{
					Short.MIN_VALUE, 0, 1.9, 6L, toDecimal("32765.5"), toDecimal(32767), toDecimal(Long.MAX_VALUE),
					"0", "hoge",
				}
		),
		//--- [4] int range 1
		new VerifyNaturalRangePattern("-3, 65535, 65537, 65534-", toDecimal(65539),
				toDecimal(1), toDecimal(65539), toDecimal(1), toDecimal(1), toDecimal(65539),
				new BigDecimal[]{
					toDecimal(1), toDecimal(2), toDecimal(3), toDecimal(65534), toDecimal(65535), toDecimal(65536), toDecimal(65537), toDecimal(65538), toDecimal(65539),
				},
				new Object[]{
					1.0, 2.0f, (short)3, 65534L, 65535, toDecimal(65536), toDecimal("65537.000000000000"), toDecimal(65538), toDecimal(65539),
				},
				new Object[]{
					Short.MIN_VALUE, 0, 0.9999, 4.0f, 65533L, toDecimal("65539.0001"), toDecimal(65540), toDecimal(Long.MAX_VALUE),
					"0", "hoge",
				}
		),
		//--- [5] int range 2
		new VerifyNaturalRangePattern("65536, 65546, 123456789, 123456788, 123456787-123456788,", toDecimal(Integer.MAX_VALUE),
				toDecimal(65536), toDecimal(123456789), toDecimal(1), toDecimal(65536), toDecimal(123456789),
				new BigDecimal[]{
					toDecimal(65536), toDecimal(65546), toDecimal(123456787), toDecimal(123456788), toDecimal(123456789),
				},
				new Object[]{
					65536.0, 65546, 123456787L, toDecimal("123456788.00000000000000"), toDecimal(123456789),
				},
				new Object[]{
					Short.MIN_VALUE, 0.0f, 1, 65535, 123456786L, toDecimal("123456790.000000000000000"), toDecimal(Integer.MAX_VALUE),
					"0", "hoge",
				}
		),
		//--- [6] long range 1
		new VerifyNaturalRangePattern("-3, 987654321005, 987654321003, 987654321004-", toDecimal(987654321006L),
				toDecimal(1), toDecimal(987654321006L), toDecimal(1), toDecimal(1), toDecimal(987654321006L),
				new BigDecimal[]{
					toDecimal(1), toDecimal(2), toDecimal(3), toDecimal(987654321003L), toDecimal(987654321004L), toDecimal(987654321005L), toDecimal(987654321006L),
				},
				new Object[]{
					(short)1, 2.0f, 3L, 987654321003L, 987654321004L, toDecimal("987654321005.0000000000"), toDecimal(987654321006L),
				},
				new Object[]{
					Short.MIN_VALUE, 0, 0.9, 2.5f, 987654321002L, toDecimal("987654321005.5"), toDecimal(987654321007L), toDecimal(Long.MAX_VALUE),
					"0", "hoge",
				}
		),
		//--- [7] long range 2
		new VerifyNaturalRangePattern("987654321005, 987654321003, 987654321004-987654321006, 999999999999", toDecimal(Long.MAX_VALUE),
				toDecimal(987654321003L), toDecimal(999999999999L), toDecimal(1), toDecimal(987654321003L), toDecimal(999999999999L),
				new BigDecimal[]{
					toDecimal(987654321003L), toDecimal(987654321004L), toDecimal(987654321005L), toDecimal(987654321006L), toDecimal(999999999999L),
				},
				new Object[]{
					987654321003L, 987654321004.0, toDecimal("987654321005.0000000000000"), toDecimal(987654321006L), toDecimal(999999999999L),
				},
				new Object[]{
					Short.MIN_VALUE, 0, 1.0, 987654321003.5, 987654321007L, toDecimal(999999999998L), toDecimal(1000000000000L), toDecimal(Long.MAX_VALUE),
					"0", "hoge",
				}
		),
		//--- [8] big range 1
		new VerifyNaturalRangePattern("2222222222, 2222222224, 2222222226, 2222222221-", toDecimal(Integer.MAX_VALUE),
				toDecimal(0), toDecimal(0), toDecimal(0), null, null,
				new BigDecimal[]{},
				new Object[]{},
				new Object[]{Short.MIN_VALUE, Integer.MAX_VALUE, Float.MIN_VALUE, 22222222222.0, 2222222221L, toDecimal(2222222222L), toDecimal(2222222226L), toDecimal(Integer.MAX_VALUE), "0", "hoge",}
		),
		//--- [9] big range 2
		bigRangePattern,
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
	
	static private String formatFromValue(NumberRangeImpl range) {
		return String.format("from=%s", range.getDecimalFromValue().toPlainString());
	}
	
	static private String formatToValue(NumberRangeImpl range) {
		return String.format("to=%s", range.getDecimalToValue().toPlainString());
	}
	
	static private String formatStepValue(NumberRangeImpl range) {
		return String.format("step=%s", range.getDecimalStepValue().toPlainString());
	}
	
	static protected BigDecimal toDecimal(long value) {
		return BigDecimal.valueOf(value);
	}
	
	static protected BigDecimal toDecimal(String value) {
		return new BigDecimal(value);
	}
	
	static public class VerifyNaturalRangePattern {
		public VerifyNaturalRangePattern(String src, BigDecimal max, BigDecimal vf, BigDecimal vt, BigDecimal vs,
				BigDecimal rangemin, BigDecimal rangemax,
				BigDecimal[] ite, Object[] con, Object[] noc)
		{
			this.rangeString = src;
			this.maxValue = max;
			this.from = vf;
			this.to = vt;
			this.step = vs;
			this.rangemin = rangemin;
			this.rangemax = rangemax;
			this.iterations = ite;
			this.contains = con;
			this.notcontains = noc;
		}

		public final String rangeString;
		public final BigDecimal maxValue;
		public final BigDecimal from;
		public final BigDecimal to;
		public final BigDecimal step;
		public final BigDecimal rangemin;
		public final BigDecimal rangemax;
		public final BigDecimal[] iterations;
		public final Object[] contains;
		public final Object[] notcontains;
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
	 * Test method for {@link ssac.aadl.runtime.util.range.NaturalNumberDecimalRange#NaturalNumberDecimalRange(java.lang.String)}.
	 */
	public void testNaturalNumberDecimalRangeString() {
		NaturalNumberDecimalRange range;
		
		// null
		range = new NaturalNumberDecimalRange(null);
		assertTrue(range.isEmpty());
		
		// empty
		range = new NaturalNumberDecimalRange("");
		assertTrue(range.isEmpty());
		
		// illegal range
		try {
			range = new NaturalNumberDecimalRange("-3,5,7,-,10-13,20-");
			fail("Must be throw IllegalArgumentException.");
		}
		catch (IllegalArgumentException ex) {
			System.out.println("IllegalArgument position=8(7+1) : " + ex.toString());
			assertTrue(true);
		}
		
		// valid range
		range = new NaturalNumberDecimalRange("-3,5,7,10-13,20-");
		assertFalse(range.isEmpty());
		assertEquals(formatFromValue(range._range), 0, toDecimal(1).compareTo(range._range.getDecimalFromValue()));
		assertEquals(formatToValue(range._range), 0, toDecimal(Long.MAX_VALUE).compareTo(range._range.getDecimalToValue()));
		assertEquals(formatStepValue(range._range), 0, toDecimal(1).compareTo(range._range.getDecimalStepValue()));
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.NaturalNumberDecimalRange#NaturalNumberDecimalRange(java.lang.String, java.math.BigDecimal)}.
	 */
	public void testNaturalNumberDecimalRangeStringBigDecimal() {
		BigDecimal maxValue;
		String validString = "-3,5,7,10-13,20-";
		NaturalNumberDecimalRange range;
		
		// null max value
		range = new NaturalNumberDecimalRange(validString, null);
		assertEquals(formatFromValue(range._range), 0, toDecimal(1).compareTo(range._range.getDecimalFromValue()));
		assertEquals(formatToValue(range._range), 0, toDecimal(Long.MAX_VALUE).compareTo(range._range.getDecimalToValue()));
		assertEquals(formatStepValue(range._range), 0, toDecimal(1).compareTo(range._range.getDecimalStepValue()));
		
		// illegal max value
		maxValue = BigDecimal.valueOf(Integer.MIN_VALUE);
		try {
			range = new NaturalNumberDecimalRange(validString, maxValue);
			fail("Must be throw IllegalArgumentException.");
		}
		catch (IllegalArgumentException ex) {
			System.out.println("IllegalArgument maxValue=" + maxValue.toPlainString() + " : " + ex.toString());
		}
		//---
		maxValue = new BigDecimal("123456.0000000000000000000000000000001");
		try {
			range = new NaturalNumberDecimalRange(validString, maxValue);
			fail("Must be throw IllegalArgumentException.");
		}
		catch (IllegalArgumentException ex) {
			System.out.println("IllegalArgument maxValue=" + maxValue.toPlainString() + " : " + ex.toString());
		}
		
		// null range string
		range = new NaturalNumberDecimalRange(null, BigDecimal.valueOf(Integer.MAX_VALUE));
		assertTrue(range.isEmpty());
		
		// empty range string
		range = new NaturalNumberDecimalRange("", BigDecimal.valueOf(Integer.MAX_VALUE));
		assertTrue(range.isEmpty());
		
		// illegal range string
		try {
			range = new NaturalNumberDecimalRange("-3,5,7,-,10-13,20-", BigDecimal.valueOf(Integer.MAX_VALUE));
			fail("Must be throw IllegalArgumentException.");
		}
		catch (IllegalArgumentException ex) {
			System.out.println("IllegalArgument position=8(7+1) : " + ex.toString());
			assertTrue(true);
		}
		try {
			range = new NaturalNumberDecimalRange("-3,5,8 9,10-13,20-", BigDecimal.valueOf(Integer.MAX_VALUE));
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
			range = new NaturalNumberDecimalRange(pat.rangeString, pat.maxValue);
			assertEquals(msg + formatFromValue(range._range), 0, pat.from.compareTo(range._range.getDecimalFromValue()));
			assertEquals(msg + formatToValue(range._range)  , 0, pat.to  .compareTo(range._range.getDecimalToValue()));
			assertEquals(msg + formatStepValue(range._range), 0, pat.step.compareTo(range._range.getDecimalStepValue()));
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.NaturalNumberDecimalRange#isEmpty()}.
	 */
	public void testIsEmpty() {
		for (int i = 0; i < naturalRangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyNaturalRangePattern pat = naturalRangePatterns[i];
			NaturalNumberDecimalRange range = new NaturalNumberDecimalRange(pat.rangeString, pat.maxValue);
			assertEquals(msg, 0, pat.from.compareTo(range._range.getDecimalFromValue()));
			assertEquals(msg, 0, pat.to  .compareTo(range._range.getDecimalToValue()));
			assertEquals(msg, 0, pat.step.compareTo(range._range.getDecimalStepValue()));
			
			if (pat.rangemin != null) {
				assertFalse(msg, range.isEmpty());
			} else {
				assertTrue(msg, range.isEmpty());
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.NaturalNumberDecimalRange#isIncludeRangeLast()}.
	 */
	public void testIsIncludeRangeLast() {
		for (int i = 0; i < naturalRangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyNaturalRangePattern pat = naturalRangePatterns[i];
			NaturalNumberDecimalRange range = new NaturalNumberDecimalRange(pat.rangeString, pat.maxValue);
			assertEquals(msg, 0, pat.from.compareTo(range._range.getDecimalFromValue()));
			assertEquals(msg, 0, pat.to  .compareTo(range._range.getDecimalToValue()));
			assertEquals(msg, 0, pat.step.compareTo(range._range.getDecimalStepValue()));
			
			assertTrue(msg, range.isIncludeRangeLast());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.NaturalNumberDecimalRange#isIncremental()}.
	 */
	public void testIsIncremental() {
		for (int i = 0; i < naturalRangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyNaturalRangePattern pat = naturalRangePatterns[i];
			NaturalNumberDecimalRange range = new NaturalNumberDecimalRange(pat.rangeString, pat.maxValue);
			assertEquals(msg, 0, pat.from.compareTo(range._range.getDecimalFromValue()));
			assertEquals(msg, 0, pat.to  .compareTo(range._range.getDecimalToValue()));
			assertEquals(msg, 0, pat.step.compareTo(range._range.getDecimalStepValue()));
			
			assertTrue(range.isIncremental());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.NaturalNumberDecimalRange#containsValue(java.lang.Object)}.
	 */
	public void testContainsValue() {
		for (int i = 0; i < naturalRangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyNaturalRangePattern pat = naturalRangePatterns[i];
			NaturalNumberDecimalRange range = new NaturalNumberDecimalRange(pat.rangeString, pat.maxValue);
			assertEquals(msg, 0, pat.from.compareTo(range._range.getDecimalFromValue()));
			assertEquals(msg, 0, pat.to  .compareTo(range._range.getDecimalToValue()));
			assertEquals(msg, 0, pat.step.compareTo(range._range.getDecimalStepValue()));

			Object[] con = pat.contains;
			Object[] ni  = pat.notcontains;
			//--- includes
			for (Object v : con) {
				assertTrue(msg + " value=" + v, range.containsValue(v));
			}
			//--- not includes
			for (Object v : ni) {
				assertFalse(msg + " value=" + v, range.containsValue(v));
			}
			assertFalse(msg + " value=null", range.containsValue(null));
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.NaturalNumberDecimalRange#rangeFirst()}.
	 */
	public void testRangeFirst() {
		for (int i = 0; i < naturalRangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyNaturalRangePattern pat = naturalRangePatterns[i];
			NaturalNumberDecimalRange range = new NaturalNumberDecimalRange(pat.rangeString, pat.maxValue);
			assertEquals(msg, 0, pat.from.compareTo(range._range.getDecimalFromValue()));
			assertEquals(msg, 0, pat.to  .compareTo(range._range.getDecimalToValue()));
			assertEquals(msg, 0, pat.step.compareTo(range._range.getDecimalStepValue()));

			if (range.isEmpty()) {
				assertEquals(msg, 0, BigDecimal.ZERO.compareTo(range.rangeFirst()));
			} else {
				assertEquals(msg, 0, pat.from.compareTo(range.rangeFirst()));
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.NaturalNumberDecimalRange#rangeLast()}.
	 */
	public void testRangeLast() {
		for (int i = 0; i < naturalRangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyNaturalRangePattern pat = naturalRangePatterns[i];
			NaturalNumberDecimalRange range = new NaturalNumberDecimalRange(pat.rangeString, pat.maxValue);
			assertEquals(msg, 0, pat.from.compareTo(range._range.getDecimalFromValue()));
			assertEquals(msg, 0, pat.to  .compareTo(range._range.getDecimalToValue()));
			assertEquals(msg, 0, pat.step.compareTo(range._range.getDecimalStepValue()));

			if (range.isEmpty()) {
				assertEquals(msg, 0, BigDecimal.ZERO.compareTo(range.rangeLast()));
			} else {
				assertEquals(msg, 0, pat.to.compareTo(range.rangeLast()));
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.NaturalNumberDecimalRange#rangeStep()}.
	 */
	public void testRangeStep() {
		for (int i = 0; i < naturalRangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyNaturalRangePattern pat = naturalRangePatterns[i];
			NaturalNumberDecimalRange range = new NaturalNumberDecimalRange(pat.rangeString, pat.maxValue);
			assertEquals(msg, 0, pat.from.compareTo(range._range.getDecimalFromValue()));
			assertEquals(msg, 0, pat.to  .compareTo(range._range.getDecimalToValue()));
			assertEquals(msg, 0, pat.step.compareTo(range._range.getDecimalStepValue()));

			assertEquals(msg, 0, BigDecimal.ONE.compareTo(range.rangeStep()));
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.NaturalNumberDecimalRange#rangeMin()}.
	 */
	public void testRangeMin() {
		for (int i = 0; i < naturalRangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyNaturalRangePattern pat = naturalRangePatterns[i];
			NaturalNumberDecimalRange range = new NaturalNumberDecimalRange(pat.rangeString, pat.maxValue);
			assertEquals(msg, 0, pat.from.compareTo(range._range.getDecimalFromValue()));
			assertEquals(msg, 0, pat.to  .compareTo(range._range.getDecimalToValue()));
			assertEquals(msg, 0, pat.step.compareTo(range._range.getDecimalStepValue()));

			if (pat.rangemin != null) {
				assertEquals(msg, 0, pat.rangemin.compareTo(range.rangeMin()));
			} else {
				assertNull(msg, range.rangeMin());
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.NaturalNumberDecimalRange#rangeMax()}.
	 */
	public void testRangeMax() {
		for (int i = 0; i < naturalRangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyNaturalRangePattern pat = naturalRangePatterns[i];
			NaturalNumberDecimalRange range = new NaturalNumberDecimalRange(pat.rangeString, pat.maxValue);
			assertEquals(msg, 0, pat.from.compareTo(range._range.getDecimalFromValue()));
			assertEquals(msg, 0, pat.to  .compareTo(range._range.getDecimalToValue()));
			assertEquals(msg, 0, pat.step.compareTo(range._range.getDecimalStepValue()));

			if (pat.rangemax != null) {
				assertEquals(msg, 0, pat.rangemax.compareTo(range.rangeMax()));
			} else {
				assertNull(msg, range.rangeMax());
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.NaturalNumberDecimalRange#iterator()}.
	 */
	public void testIterator() {
		for (int i = 0; i < naturalRangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyNaturalRangePattern pat = naturalRangePatterns[i];
			NaturalNumberDecimalRange range = new NaturalNumberDecimalRange(pat.rangeString, pat.maxValue);
			assertEquals(msg, 0, pat.from.compareTo(range._range.getDecimalFromValue()));
			assertEquals(msg, 0, pat.to  .compareTo(range._range.getDecimalToValue()));
			assertEquals(msg, 0, pat.step.compareTo(range._range.getDecimalStepValue()));

			BigDecimal[] con = pat.iterations;
			Iterator<BigDecimal> it = range.iterator();
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

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.NaturalNumberDecimalRange#hashCode()}.
	 */
	public void testHashCode() {
		// empty
		NaturalNumberDecimalRange range_e1 = new NaturalNumberDecimalRange(null);
		assertTrue(range_e1.isEmpty());
		NaturalNumberDecimalRange range_e2 = new NaturalNumberDecimalRange("");
		assertTrue(range_e2.isEmpty());
		
		// bigRangePattern
		NaturalNumberDecimalRange range_a1 = new NaturalNumberDecimalRange(bigRangePattern.rangeString, bigRangePattern.maxValue);
		assertFalse(range_a1.isEmpty());
		NaturalNumberDecimalRange range_a2 = new NaturalNumberDecimalRange(bigRangePattern.rangeString, bigRangePattern.maxValue);
		assertFalse(range_a2.isEmpty());
		
		// modified bigRangePattern
		NaturalNumberDecimalRange range_b1 = new NaturalNumberDecimalRange(bigRangePattern.rangeString + ",2147483658", bigRangePattern.maxValue);
		assertFalse(range_b1.isEmpty());
		NaturalNumberDecimalRange range_b2 = new NaturalNumberDecimalRange("2147483658," + bigRangePattern.rangeString, bigRangePattern.maxValue);
		assertFalse(range_b2.isEmpty());
		
		// check hashcode
		int h;
		String rs;
		//--- e1
		h = range_e1.hashCode();
		rs = range_e1._range.toString();
		assertEquals(rs.hashCode(), h);
		//--- e2
		rs = range_e2._range.toString();
		h = range_e2.hashCode();
		assertEquals(rs.hashCode(), h);
		//--- a1
		h = range_a1.hashCode();
		rs = range_a1._range.toString();
		assertEquals(rs.hashCode(), h);
		//--- a2
		rs = range_a2._range.toString();
		h = range_a2.hashCode();
		assertEquals(rs.hashCode(), h);
		//--- b1
		h = range_b1.hashCode();
		rs = range_b1._range.toString();
		assertEquals(rs.hashCode(), h);
		//--- b2
		rs = range_b2._range.toString();
		h = range_b2.hashCode();
		assertEquals(rs.hashCode(), h);
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.NaturalNumberDecimalRange#equals(java.lang.Object)}.
	 */
	public void testEqualsObject() {
		for (int i = 0; i < naturalRangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyNaturalRangePattern pat = naturalRangePatterns[i];
			NaturalNumberDecimalRange range = new NaturalNumberDecimalRange(pat.rangeString, pat.maxValue);
			assertEquals(msg, 0, pat.from.compareTo(range._range.getDecimalFromValue()));
			assertEquals(msg, 0, pat.to  .compareTo(range._range.getDecimalToValue()));
			assertEquals(msg, 0, pat.step.compareTo(range._range.getDecimalStepValue()));
			
			for (int j = 0; j < naturalRangePatterns.length; j++) {
				String amsg = String.format(".equals[%d] ", j);
				VerifyNaturalRangePattern apat = naturalRangePatterns[j];
				NaturalNumberDecimalRange arange = new NaturalNumberDecimalRange(apat.rangeString, apat.maxValue);

				if (i == j) {
					assertEquals(msg + amsg, range, arange);
					assertEquals(msg + amsg, arange, range);
					assertEquals(msg + amsg, range.hashCode(), arange.hashCode());
				} else if (range.isEmpty() && arange.isEmpty()) {
					assertEquals(msg + amsg, range, arange);
					assertEquals(msg + amsg, arange, range);
					assertEquals(msg + amsg, range.hashCode(), arange.hashCode());
				} else {
					assertFalse(msg + amsg, range.equals(arange));
					assertFalse(msg + amsg, arange.equals(range));
				}
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.NaturalNumberDecimalRange#toString()}.
	 */
	public void testToString() {
		NaturalNumberDecimalRange range;
		
		// empty
		range = new NaturalNumberDecimalRange(null);
		assertTrue(range.isEmpty());
		assertEquals("[]", range.toString());
		
		// bigRangePattern
		range = new NaturalNumberDecimalRange(bigRangePattern.rangeString, bigRangePattern.maxValue);
		assertFalse(range.isEmpty());
		assertEquals(0, bigRangePattern.from.compareTo(range._range.getDecimalFromValue()));
		assertEquals(0, bigRangePattern.to  .compareTo(range._range.getDecimalToValue()));
		assertEquals(0, bigRangePattern.step.compareTo(range._range.getDecimalStepValue()));
		assertEquals("[" + rangeStringFromBigRange + "]", range.toString());
	}
}
