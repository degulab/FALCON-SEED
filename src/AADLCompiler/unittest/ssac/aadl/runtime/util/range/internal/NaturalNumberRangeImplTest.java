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
 * @(#)NaturalNumberRangeImplTest.java	1.70	2011/05/16
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.util.range.internal;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.NoSuchElementException;

import ssac.aadl.runtime.util.range.RangeUtil;

import junit.framework.TestCase;

/**
 * {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeImpl} クラスのテスト。
 * 
 * @version 1.70	2011/05/16
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * 
 * @since 1.70
 */
public class NaturalNumberRangeImplTest extends TestCase
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final VerifyPattern bigRangePattern = toPattern(
			"3-4, 1-2, 0-4, 32767-32765,"
			+ "9223372036854775806-9223372036854775804, 9223372036854775803, 9223372036854775808-9223372036854775810, 9223372036854775807,"
			+ "2147483647-2147483645, 2147483643, 2147483644, 2147483647-2147483650,"
			+ "32763, 32764, 32768-32770,"
			+ "12345678901234567891, 12345678901234567888, 12345678901234567889-,"
			+ "7, 2147483640, 2147483652, 9223372036854775800, 9223372036854775812, 12345678901234567885, 12345678901234567895", toDecimal("12345678901234567890"),
			toDecimal(0), toDecimal("12345678901234567890"), toDecimal(1),
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
			new BigDecimal[]{
				toDecimal(Short.MIN_VALUE), toDecimal(-1), toDecimal("0.5"), toDecimal(5),
				toDecimal(32762), toDecimal("32762.5"), toDecimal("32769.9"), toDecimal(32771),
				toDecimal(2147483642L), toDecimal("2147483649.999999"), toDecimal(2147483651L),
				toDecimal(9223372036854775802L), toDecimal("9223372036854775806.9999999"), toDecimal("9223372036854775810.0000000001"), toDecimal("9223372036854775811"),
				toDecimal("12345678901234567887"), toDecimal("12345678901234567890.000000001"), toDecimal("12345678901234567891"),
			},
			new BigDecimal[]{
				toDecimal(0), toDecimal(1), toDecimal(2), toDecimal(3), toDecimal(4),
				toDecimal(32763), toDecimal(32764), toDecimal(32765), toDecimal(32766),
				toDecimal(32767), toDecimal(32768), toDecimal(32769), toDecimal(32770),
				toDecimal(2147483643L), toDecimal(2147483644L), toDecimal(2147483645L), toDecimal(2147483646L),
				toDecimal(2147483647L), toDecimal(2147483648L), toDecimal(2147483649L), toDecimal(2147483650L),
				toDecimal(9223372036854775803L), toDecimal(9223372036854775804L), toDecimal(9223372036854775805L), toDecimal(9223372036854775806L),
				toDecimal("9223372036854775807"), toDecimal("9223372036854775808"), toDecimal("9223372036854775809"), toDecimal("9223372036854775810"),
				toDecimal("12345678901234567888"), toDecimal("12345678901234567889"), toDecimal("12345678901234567890"),
				toDecimal("0.5"), toDecimal(5),
				toDecimal(32762), toDecimal("32762.5"), toDecimal("32769.9"), toDecimal(32771),
				toDecimal(2147483642L), toDecimal("2147483649.999999"), toDecimal(2147483651L),
				toDecimal(9223372036854775802L), toDecimal("9223372036854775806.9999999"), toDecimal("9223372036854775810.0000000001"), toDecimal("9223372036854775811"),
				toDecimal("12345678901234567887"), toDecimal("12345678901234567890"),
			},
			new BigDecimal[]{
				toDecimal(Short.MIN_VALUE), toDecimal(-1), toDecimal("-0.000000000000000000000000000000000000000000000000000000000000000000000001"),
				toDecimal("12345678901234567890.000000001"), toDecimal("12345678901234567891"),
			}
	);
	
	static private final NumberRangeImpl[] rebuildedBigRanges = {
		/* [ 0] */ new IntegerRangeImpl(0, 4, 1),
		/* [ 1] */ new OneNumberRangeImpl(toDecimal(7)),
		/* [ 2] */ new IntegerRangeImpl(32763, 32770, 1),
		/* [ 3] */ new OneNumberRangeImpl(toDecimal(2147483640L)),
		/* [ 4] */ new LongRangeImpl(2147483643L, 2147483650L, 1L),
		/* [ 5] */ new OneNumberRangeImpl(toDecimal(2147483652L)),
		/* [ 6] */ new OneNumberRangeImpl(toDecimal(9223372036854775800L)),
		/* [ 7] */ new LongRangeImpl(9223372036854775803L, 9223372036854775806L, 1L),
		/* [ 8] */ new BigDecimalRangeImpl(toDecimal("9223372036854775807"), toDecimal("9223372036854775810"), BigDecimal.ONE),
		/* [ 9] */ new OneNumberRangeImpl(toDecimal("9223372036854775812")),
		/* [10] */ new OneNumberRangeImpl(toDecimal("12345678901234567885")),
		/* [11] */ new BigDecimalRangeImpl(toDecimal("12345678901234567888"), toDecimal("12345678901234567890"), BigDecimal.ONE),
	};
	
	static private final String[] rangeStringsFromBigRanges = {
		/* [ 0] */ "0-4",
		/* [ 1] */ "7",
		/* [ 2] */ "32763-32770",
		/* [ 3] */ "2147483640",
		/* [ 4] */ "2147483643-2147483650",
		/* [ 5] */ "2147483652",
		/* [ 6] */ "9223372036854775800",
		/* [ 7] */ "9223372036854775803-9223372036854775806",
		/* [ 8] */ "9223372036854775807-9223372036854775810",
		/* [ 9] */ "9223372036854775812",
		/* [10] */ "12345678901234567885",
		/* [11] */ "12345678901234567888-12345678901234567890",
	};
	
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
	
	static private final VerifyPattern[] patterns = {
		//--- [0] null string (empty range)
		toPattern(null, null,
				toDecimal(0), toDecimal(0), toDecimal(0),
				new BigDecimal[]{},
				new BigDecimal[]{toDecimal(0), toDecimal(Short.MAX_VALUE), toDecimal(Integer.MAX_VALUE), toDecimal(Long.MAX_VALUE),},
				new BigDecimal[]{},
				new BigDecimal[]{toDecimal(0), toDecimal(Short.MAX_VALUE), toDecimal(Integer.MAX_VALUE), toDecimal(Long.MAX_VALUE),}
		),
		//--- [1] empty string (empty range)
		toPattern("", toDecimal(Integer.MAX_VALUE),
				toDecimal(0), toDecimal(0), toDecimal(0),
				new BigDecimal[]{},
				new BigDecimal[]{toDecimal(0), toDecimal(Short.MAX_VALUE), toDecimal(Integer.MAX_VALUE), toDecimal(Long.MAX_VALUE),},
				new BigDecimal[]{},
				new BigDecimal[]{toDecimal(0), toDecimal(Short.MAX_VALUE), toDecimal(Integer.MAX_VALUE), toDecimal(Long.MAX_VALUE),}
		),
		//--- [2] short range 1
		toPattern("15,25,-3,32764", toDecimal(32766),
				toDecimal(1), toDecimal(32764), toDecimal(1),
				new BigDecimal[]{
					toDecimal(1), toDecimal(2), toDecimal(3), toDecimal(15), toDecimal(25), toDecimal(32764),
				},
				new BigDecimal[]{
					toDecimal(Short.MIN_VALUE), toDecimal(0), toDecimal("0.9"), toDecimal(10), toDecimal("32765.5"), toDecimal(32767), toDecimal(Long.MAX_VALUE),
				},
				new BigDecimal[]{
					toDecimal(1), toDecimal(10), toDecimal("32763.5"), toDecimal(32764),
				},
				new BigDecimal[]{
					toDecimal(Short.MIN_VALUE), toDecimal(0), toDecimal("0.9"), toDecimal("32764.01"), toDecimal(32767), toDecimal(Long.MAX_VALUE),
				}
		),
		//--- [3] short range 2
		toPattern("3, 4, 5, 2-4, 32765, 32764-", toDecimal(32766),
				toDecimal(2), toDecimal(32766), toDecimal(1),
				new BigDecimal[]{
					toDecimal(2), toDecimal(3), toDecimal(4), toDecimal(5), toDecimal(32764), toDecimal(32765), toDecimal(32766),
				},
				new BigDecimal[]{
					toDecimal(Short.MIN_VALUE), toDecimal(0), toDecimal("1.9"), toDecimal(6), toDecimal("32765.5"), toDecimal(32767), toDecimal(Long.MAX_VALUE),
				},
				new BigDecimal[]{
					toDecimal(2), toDecimal(10), toDecimal("32763.5"), toDecimal(32766),
				},
				new BigDecimal[]{
					toDecimal(Short.MIN_VALUE), toDecimal(0), toDecimal("1.9"), toDecimal("32766.01"), toDecimal(32767), toDecimal(Long.MAX_VALUE),
				}
		),
		//--- [4] int range 1
		toPattern("-3, 65535, 65537, 65534-", toDecimal(65539),
				toDecimal(1), toDecimal(65539), toDecimal(1),
				new BigDecimal[]{
					toDecimal(1), toDecimal(2), toDecimal(3), toDecimal(65534), toDecimal(65535), toDecimal(65536), toDecimal(65537), toDecimal(65538), toDecimal(65539),
				},
				new BigDecimal[]{
					toDecimal(Short.MIN_VALUE), toDecimal(0), toDecimal("0.9999"), toDecimal(4), toDecimal(65533), toDecimal("65539.0001"), toDecimal(65540), toDecimal(Long.MAX_VALUE),
				},
				new BigDecimal[]{
					toDecimal(1), toDecimal("1.000001"), toDecimal(4), toDecimal(65533), toDecimal("65538.9999"), toDecimal(65539),
				},
				new BigDecimal[]{
					toDecimal(Short.MIN_VALUE), toDecimal(0), toDecimal("0.999999"), toDecimal("65539.0001"), toDecimal(65540), toDecimal(Long.MAX_VALUE),
				}
		),
		//--- [5] int range 2
		toPattern("65536, 65546, 123456789, 123456788, 123456787-123456788,", toDecimal(Integer.MAX_VALUE),
				toDecimal(65536), toDecimal(123456789), toDecimal(1),
				new BigDecimal[]{
					toDecimal(65536), toDecimal(65546), toDecimal(123456787), toDecimal(123456788), toDecimal(123456789),
				},
				new BigDecimal[]{
					toDecimal(Short.MIN_VALUE), toDecimal(0), toDecimal(1), toDecimal(65535), toDecimal(123456786), toDecimal(123456790), toDecimal(Integer.MAX_VALUE),
				},
				new BigDecimal[]{
					toDecimal(65536), toDecimal(65540), toDecimal("123456788.5"), toDecimal(123456789),
				},
				new BigDecimal[]{
					toDecimal(Short.MIN_VALUE), toDecimal(0), toDecimal(1), toDecimal(65535), toDecimal(123456790), toDecimal(Integer.MAX_VALUE),
				}
		),
		//--- [6] long range 1
		toPattern("-3, 987654321005, 987654321003, 987654321004-", toDecimal(987654321006L),
				toDecimal(1), toDecimal(987654321006L), toDecimal(1),
				new BigDecimal[]{
					toDecimal(1), toDecimal(2), toDecimal(3), toDecimal(987654321003L), toDecimal(987654321004L), toDecimal(987654321005L), toDecimal(987654321006L),
				},
				new BigDecimal[]{
					toDecimal(Short.MIN_VALUE), toDecimal(0), toDecimal("0.9"), toDecimal("2.5"), toDecimal(987654321002L), toDecimal("987654321005.5"), toDecimal(987654321007L), toDecimal(Long.MAX_VALUE),
				},
				new BigDecimal[]{
					toDecimal(1), toDecimal("2.5"), toDecimal(987654321002L), toDecimal("987654321005.5"), toDecimal(987654321006L),
				},
				new BigDecimal[]{
					toDecimal(Short.MIN_VALUE), toDecimal(0), toDecimal("0.9"), toDecimal(987654321007L), toDecimal(Long.MAX_VALUE),
				}
		),
		//--- [7] long range 2
		toPattern("987654321005, 987654321003, 987654321004-987654321006, 999999999999", toDecimal(Long.MAX_VALUE),
				toDecimal(987654321003L), toDecimal(999999999999L), toDecimal(1),
				new BigDecimal[]{
					toDecimal(987654321003L), toDecimal(987654321004L), toDecimal(987654321005L), toDecimal(987654321006L), toDecimal(999999999999L),
				},
				new BigDecimal[]{
					toDecimal(Short.MIN_VALUE), toDecimal(0), toDecimal(1), toDecimal("987654321003.5"), toDecimal(987654321007L), toDecimal(999999999998L), toDecimal(1000000000000L), toDecimal(Long.MAX_VALUE),
				},
				new BigDecimal[]{
					toDecimal(987654321003L), toDecimal("987654321003.5"), toDecimal(999999999998L), toDecimal(999999999999L),
				},
				new BigDecimal[]{
					toDecimal(Short.MIN_VALUE), toDecimal(0), toDecimal(1), toDecimal("987654321002.5"), toDecimal(1000000000000L), toDecimal(Long.MAX_VALUE),
				}
		),
		//--- [8] big range 1
		toPattern("2222222222, 2222222224, 2222222226, 2222222221-", toDecimal(Integer.MAX_VALUE),
				toDecimal(0), toDecimal(0), toDecimal(0),
				new BigDecimal[]{},
				new BigDecimal[]{toDecimal(2222222221L), toDecimal(2222222222L), toDecimal(2222222226L), toDecimal(Integer.MAX_VALUE)},
				new BigDecimal[]{},
				new BigDecimal[]{toDecimal(2222222221L), toDecimal(2222222222L), toDecimal(2222222226L), toDecimal(Integer.MAX_VALUE)}
		),
		//--- [9] big range 2
		bigRangePattern,
		//--- [10] "1-3,5,8,15"
		toPattern("1-3,5,8,15", toDecimal(Integer.MAX_VALUE),
				toDecimal(1), toDecimal(15), toDecimal(1),
				new BigDecimal[]{
					toDecimal(1), toDecimal(2), toDecimal(3), toDecimal(5), toDecimal(8), toDecimal(15),
				},
				new BigDecimal[]{toDecimal(2222222221L), toDecimal(2222222222L), toDecimal(2222222226L), toDecimal(Integer.MAX_VALUE)},
				new BigDecimal[]{
					toDecimal(1), toDecimal(2), toDecimal(3), toDecimal(5), toDecimal(8), toDecimal(15),
				},
				new BigDecimal[]{toDecimal(2222222221L), toDecimal(2222222222L), toDecimal(2222222226L), toDecimal(Integer.MAX_VALUE)}
		),
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
	
	static private BigDecimal toDecimal(long value) {
		return BigDecimal.valueOf(value);
	}
	
	static private BigDecimal toDecimal(String value) {
		return new BigDecimal(value);
	}
	
	static private VerifyPattern toPattern(String src, BigDecimal maxValue, BigDecimal from, BigDecimal to, BigDecimal step,
			 							BigDecimal[] contains, BigDecimal[] notcontains, BigDecimal[] includes, BigDecimal[] excludes)
	{
		return new VerifyPattern(src, maxValue, from, to, step, includes, excludes, contains, notcontains);
	}
	
	static private class VerifyPattern {
		public VerifyPattern(String src, BigDecimal max, BigDecimal vf, BigDecimal vt, BigDecimal vs,
				BigDecimal[] inc, BigDecimal[] exc, BigDecimal[] con, BigDecimal[] noc)
		{
			this.rangeString = src;
			this.maxValue = max;
			this.from = vf;
			this.to = vt;
			this.step = vs;
			this.includes = inc;
			this.excludes = exc;
			this.contains = con;
			this.notcontains = noc;
		}

		public final String rangeString;
		public final BigDecimal maxValue;
		public final BigDecimal from;
		public final BigDecimal to;
		public final BigDecimal step;
		public final BigDecimal[] includes;
		public final BigDecimal[] excludes;
		public final BigDecimal[] contains;
		public final BigDecimal[] notcontains;
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
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeImpl#NaturalNumberRangeImpl(java.lang.String)}.
	 */
	public void testNaturalNumberRangeImplString() {
		NaturalNumberRangeImpl range;
		
		// null
		range = new NaturalNumberRangeImpl(null);
		assertTrue(range.isEmpty());
		
		// empty
		range = new NaturalNumberRangeImpl("");
		assertTrue(range.isEmpty());
		
		// illegal range
		try {
			range = new NaturalNumberRangeImpl("-3,5,7,-,10-13,20-");
			fail("Must be throw IllegalArgumentException.");
		}
		catch (IllegalArgumentException ex) {
			System.out.println("IllegalArgument position=8(7+1) : " + ex.toString());
			assertTrue(true);
		}
		
		// valid range
		range = new NaturalNumberRangeImpl("-3,5,7,10-13,20-");
		assertFalse(range.isEmpty());
		assertEquals(formatFromValue(range), 0, toDecimal(1).compareTo(range.getDecimalFromValue()));
		assertEquals(formatToValue(range), 0, toDecimal(Long.MAX_VALUE).compareTo(range.getDecimalToValue()));
		assertEquals(formatStepValue(range), 0, toDecimal(1).compareTo(range.getDecimalStepValue()));
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeImpl#NaturalNumberRangeImpl(java.lang.String, java.math.BigDecimal)}.
	 */
	public void testNaturalNumberRangeImplStringBigDecimal() {
		BigDecimal maxValue;
		String validString = "-3,5,7,10-13,20-";
		NaturalNumberRangeImpl range;
		
		// null max value
		range = new NaturalNumberRangeImpl(validString, null);
		assertEquals(formatFromValue(range), 0, toDecimal(1).compareTo(range.getDecimalFromValue()));
		assertEquals(formatToValue(range), 0, toDecimal(Long.MAX_VALUE).compareTo(range.getDecimalToValue()));
		assertEquals(formatStepValue(range), 0, toDecimal(1).compareTo(range.getDecimalStepValue()));
		
		// illegal max value
		maxValue = BigDecimal.valueOf(Integer.MIN_VALUE);
		try {
			range = new NaturalNumberRangeImpl(validString, maxValue);
			fail("Must be throw IllegalArgumentException.");
		}
		catch (IllegalArgumentException ex) {
			System.out.println("IllegalArgument maxValue=" + maxValue.toPlainString() + " : " + ex.toString());
		}
		//---
		maxValue = new BigDecimal("123456.0000000000000000000000000000001");
		try {
			range = new NaturalNumberRangeImpl(validString, maxValue);
			fail("Must be throw IllegalArgumentException.");
		}
		catch (IllegalArgumentException ex) {
			System.out.println("IllegalArgument maxValue=" + maxValue.toPlainString() + " : " + ex.toString());
		}
		
		// null range string
		range = new NaturalNumberRangeImpl(null, BigDecimal.valueOf(Integer.MAX_VALUE));
		assertTrue(range.isEmpty());
		
		// empty range string
		range = new NaturalNumberRangeImpl("", BigDecimal.valueOf(Integer.MAX_VALUE));
		assertTrue(range.isEmpty());
		
		// illegal range string
		try {
			range = new NaturalNumberRangeImpl("-3,5,7,-,10-13,20-", BigDecimal.valueOf(Integer.MAX_VALUE));
			fail("Must be throw IllegalArgumentException.");
		}
		catch (IllegalArgumentException ex) {
			System.out.println("IllegalArgument position=8(7+1) : " + ex.toString());
			assertTrue(true);
		}
		try {
			range = new NaturalNumberRangeImpl("-3,5,8 9,10-13,20-", BigDecimal.valueOf(Integer.MAX_VALUE));
			fail("Must be throw IllegalArgumentException.");
		}
		catch (IllegalArgumentException ex) {
			System.out.println("IllegalArgument position=6(5+1) : " + ex.toString());
			assertTrue(true);
		}
		
		// valid range
		for (int i = 0; i < patterns.length; i++) {
			VerifyPattern pat = patterns[i];
			String msg = String.format("Test params : [%d] ", i);
			range = new NaturalNumberRangeImpl(pat.rangeString, pat.maxValue);
			assertEquals(msg + formatFromValue(range), 0, pat.from.compareTo(range.getDecimalFromValue()));
			assertEquals(msg + formatToValue(range)  , 0, pat.to  .compareTo(range.getDecimalToValue()));
			assertEquals(msg + formatStepValue(range), 0, pat.step.compareTo(range.getDecimalStepValue()));
		}
		
		// check NumberRangeImpl instances by bigRangePattern
		range = new NaturalNumberRangeImpl(bigRangePattern.rangeString, bigRangePattern.maxValue);
		assertEquals(0, bigRangePattern.from.compareTo(range.getDecimalFromValue()));
		assertEquals(0, bigRangePattern.to  .compareTo(range.getDecimalToValue()));
		assertEquals(0, bigRangePattern.step.compareTo(range.getDecimalStepValue()));
		assertEquals(rebuildedBigRanges.length, range._ranges.size());
		for (int i = 0; i < rebuildedBigRanges.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			NumberRangeImpl ansRange = rebuildedBigRanges[i];
			NumberRangeImpl retRange = range._ranges.get(i);
			assertEquals(msg, ansRange.getClass(), retRange.getClass());
			assertEquals(msg, 0, ansRange.getDecimalFromValue().compareTo(retRange.getDecimalFromValue()));
			assertEquals(msg, 0, ansRange.getDecimalToValue()  .compareTo(retRange.getDecimalToValue()));
			assertEquals(msg, 0, ansRange.getDecimalStepValue().compareTo(retRange.getDecimalStepValue()));
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeImpl#getValueClass()}.
	 */
	public void testGetValueClass() {
		for (int i = 0; i < patterns.length; i++) {
			VerifyPattern pat = patterns[i];
			String msg = String.format("Test params : [%d] ", i);
			NaturalNumberRangeImpl range = new NaturalNumberRangeImpl(pat.rangeString, pat.maxValue);
			if (pat.step.signum() == 0) {
				assertEquals(msg, Short.class, range.getValueClass());
			}
			else if (RangeUtil.exactlyLongValues(pat.from, pat.to, pat.step)) {
				if (RangeUtil.exactlyShortValues(pat.from, pat.to, pat.step)) {
					assertEquals(msg, Short.class, range.getValueClass());
				}
				else if (RangeUtil.exactlyIntegerValues(pat.from, pat.to, pat.step)) {
					assertEquals(msg, Integer.class, range.getValueClass());
				}
				else {
					assertEquals(msg, Long.class, range.getValueClass());
				}
			}
			else {
				assertEquals(msg, BigDecimal.class, range.getValueClass());
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeImpl#isEmpty()}.
	 */
	public void testIsEmpty() {
		for (int i = 0; i < patterns.length; i++) {
			VerifyPattern pat = patterns[i];
			String msg = String.format("Test params : [%d] ", i);
			NaturalNumberRangeImpl range = new NaturalNumberRangeImpl(pat.rangeString, pat.maxValue);
			if (pat.step.signum() != 0) {
				assertFalse(msg, range.isEmpty());
			} else {
				assertTrue(msg, range.isEmpty());
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeImpl#isIncremental()}.
	 */
	public void testIsIncremental() {
		for (int i = 0; i < patterns.length; i++) {
			VerifyPattern pat = patterns[i];
			String msg = String.format("Test params : [%d] ", i);
			NaturalNumberRangeImpl range = new NaturalNumberRangeImpl(pat.rangeString, pat.maxValue);
			if (pat.step.signum() < 0) {
				assertFalse(msg, range.isIncremental());
			} else {
				assertTrue(msg, range.isIncremental());
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeImpl#stepSignum()}.
	 */
	public void testStepSignum() {
		for (int i = 0; i < patterns.length; i++) {
			VerifyPattern pat = patterns[i];
			String msg = String.format("Test params : [%d] ", i);
			NaturalNumberRangeImpl range = new NaturalNumberRangeImpl(pat.rangeString, pat.maxValue);
			assertEquals(msg, pat.step.signum(), range.stepSignum());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeImpl#isIncludeValue(java.math.BigDecimal)}.
	 */
	public void testIsIncludeValue() {
		for (int i = 0; i < patterns.length; i++) {
			VerifyPattern pat = patterns[i];
			BigDecimal[] con = pat.includes;
			BigDecimal[] ni = pat.excludes;
			String msg = String.format("Test params : [%d] ", i);
			NaturalNumberRangeImpl range = new NaturalNumberRangeImpl(pat.rangeString, pat.maxValue);
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
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeImpl#containsValue(java.math.BigDecimal)}.
	 */
	public void testContainsValue() {
		for (int i = 0; i < patterns.length; i++) {
			VerifyPattern pat = patterns[i];
			BigDecimal[] con = pat.contains;
			BigDecimal[] ni = pat.excludes;
			String msg = String.format("Test params : [%d] ", i);
			NaturalNumberRangeImpl range = new NaturalNumberRangeImpl(pat.rangeString, pat.maxValue);
			//--- contains
			for (BigDecimal v : con) {
				assertTrue(msg + " value=" + v, range.containsValue(v));
			}
			//--- not contains
			for (BigDecimal v : ni) {
				assertFalse(msg + " value=" + v, range.containsValue(v));
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeImpl#getShortFromValue()}.
	 */
	public void testGetShortFromValue() {
		for (int i = 0; i < patterns.length; i++) {
			VerifyPattern pat = patterns[i];
			String msg = String.format("Test params : [%d] ", i);
			NaturalNumberRangeImpl range = new NaturalNumberRangeImpl(pat.rangeString, pat.maxValue);
			assertEquals(msg, pat.from.shortValue(), range.getShortFromValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeImpl#getIntegerFromValue()}.
	 */
	public void testGetIntegerFromValue() {
		for (int i = 0; i < patterns.length; i++) {
			VerifyPattern pat = patterns[i];
			String msg = String.format("Test params : [%d] ", i);
			NaturalNumberRangeImpl range = new NaturalNumberRangeImpl(pat.rangeString, pat.maxValue);
			assertEquals(msg, pat.from.intValue(), range.getIntegerFromValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeImpl#getLongFromValue()}.
	 */
	public void testGetLongFromValue() {
		for (int i = 0; i < patterns.length; i++) {
			VerifyPattern pat = patterns[i];
			String msg = String.format("Test params : [%d] ", i);
			NaturalNumberRangeImpl range = new NaturalNumberRangeImpl(pat.rangeString, pat.maxValue);
			assertEquals(msg, pat.from.longValue(), range.getLongFromValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeImpl#getDecimalFromValue()}.
	 */
	public void testGetDecimalFromValue() {
		for (int i = 0; i < patterns.length; i++) {
			VerifyPattern pat = patterns[i];
			String msg = String.format("Test params : [%d] ", i);
			NaturalNumberRangeImpl range = new NaturalNumberRangeImpl(pat.rangeString, pat.maxValue);
			assertEquals(msg, 0, pat.from.compareTo(range.getDecimalFromValue()));
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeImpl#getShortToValue()}.
	 */
	public void testGetShortToValue() {
		for (int i = 0; i < patterns.length; i++) {
			VerifyPattern pat = patterns[i];
			String msg = String.format("Test params : [%d] ", i);
			NaturalNumberRangeImpl range = new NaturalNumberRangeImpl(pat.rangeString, pat.maxValue);
			assertEquals(msg, pat.to.shortValue(), range.getShortToValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeImpl#getIntegerToValue()}.
	 */
	public void testGetIntegerToValue() {
		for (int i = 0; i < patterns.length; i++) {
			VerifyPattern pat = patterns[i];
			String msg = String.format("Test params : [%d] ", i);
			NaturalNumberRangeImpl range = new NaturalNumberRangeImpl(pat.rangeString, pat.maxValue);
			assertEquals(msg, pat.to.intValue(), range.getIntegerToValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeImpl#getLongToValue()}.
	 */
	public void testGetLongToValue() {
		for (int i = 0; i < patterns.length; i++) {
			VerifyPattern pat = patterns[i];
			String msg = String.format("Test params : [%d] ", i);
			NaturalNumberRangeImpl range = new NaturalNumberRangeImpl(pat.rangeString, pat.maxValue);
			assertEquals(msg, pat.to.longValue(), range.getLongToValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeImpl#getDecimalToValue()}.
	 */
	public void testGetDecimalToValue() {
		for (int i = 0; i < patterns.length; i++) {
			VerifyPattern pat = patterns[i];
			String msg = String.format("Test params : [%d] ", i);
			NaturalNumberRangeImpl range = new NaturalNumberRangeImpl(pat.rangeString, pat.maxValue);
			assertEquals(msg, 0, pat.to.compareTo(range.getDecimalToValue()));
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeImpl#getShortStepValue()}.
	 */
	public void testGetShortStepValue() {
		for (int i = 0; i < patterns.length; i++) {
			VerifyPattern pat = patterns[i];
			String msg = String.format("Test params : [%d] ", i);
			NaturalNumberRangeImpl range = new NaturalNumberRangeImpl(pat.rangeString, pat.maxValue);
			assertEquals(msg, pat.step.shortValue(), range.getShortStepValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeImpl#getIntegerStepValue()}.
	 */
	public void testGetIntegerStepValue() {
		for (int i = 0; i < patterns.length; i++) {
			VerifyPattern pat = patterns[i];
			String msg = String.format("Test params : [%d] ", i);
			NaturalNumberRangeImpl range = new NaturalNumberRangeImpl(pat.rangeString, pat.maxValue);
			assertEquals(msg, pat.step.intValue(), range.getIntegerStepValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeImpl#getLongStepValue()}.
	 */
	public void testGetLongStepValue() {
		for (int i = 0; i < patterns.length; i++) {
			VerifyPattern pat = patterns[i];
			String msg = String.format("Test params : [%d] ", i);
			NaturalNumberRangeImpl range = new NaturalNumberRangeImpl(pat.rangeString, pat.maxValue);
			assertEquals(msg, pat.step.longValue(), range.getLongStepValue());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeImpl#getDecimalStepValue()}.
	 */
	public void testGetDecimalStepValue() {
		for (int i = 0; i < patterns.length; i++) {
			VerifyPattern pat = patterns[i];
			String msg = String.format("Test params : [%d] ", i);
			NaturalNumberRangeImpl range = new NaturalNumberRangeImpl(pat.rangeString, pat.maxValue);
			assertEquals(msg, 0, pat.step.compareTo(range.getDecimalStepValue()));
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeImpl#getShortRangeIterator()}.
	 */
	public void testGetShortRangeIterator() {
		for (int i = 0; i < patterns.length; i++) {
			VerifyPattern pat = patterns[i];
			BigDecimal[] con = pat.contains;
			String msg = String.format("Test params : [%d] ", i);
			NaturalNumberRangeImpl range = new NaturalNumberRangeImpl(pat.rangeString, pat.maxValue);
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
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeImpl#getIntegerRangeIterator()}.
	 */
	public void testGetIntegerRangeIterator() {
		for (int i = 0; i < patterns.length; i++) {
			VerifyPattern pat = patterns[i];
			BigDecimal[] con = pat.contains;
			String msg = String.format("Test params : [%d] ", i);
			NaturalNumberRangeImpl range = new NaturalNumberRangeImpl(pat.rangeString, pat.maxValue);
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
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeImpl#getLongRangeIterator()}.
	 */
	public void testGetLongRangeIterator() {
		for (int i = 0; i < patterns.length; i++) {
			VerifyPattern pat = patterns[i];
			BigDecimal[] con = pat.contains;
			String msg = String.format("Test params : [%d] ", i);
			NaturalNumberRangeImpl range = new NaturalNumberRangeImpl(pat.rangeString, pat.maxValue);
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
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeImpl#getDecimalRangeIterator()}.
	 */
	public void testGetDecimalRangeIterator() {
		for (int i = 0; i < patterns.length; i++) {
			VerifyPattern pat = patterns[i];
			BigDecimal[] con = pat.contains;
			String msg = String.format("Test params : [%d] ", i);
			NaturalNumberRangeImpl range = new NaturalNumberRangeImpl(pat.rangeString, pat.maxValue);
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

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeImpl#hashCode()}.
	 */
	public void testHashCode() {
		// empty
		NaturalNumberRangeImpl range_e1 = new NaturalNumberRangeImpl(null);
		assertTrue(range_e1.isEmpty());
		NaturalNumberRangeImpl range_e2 = new NaturalNumberRangeImpl("");
		assertTrue(range_e2.isEmpty());
		
		// bigRangePattern
		NaturalNumberRangeImpl range_a1 = new NaturalNumberRangeImpl(bigRangePattern.rangeString, bigRangePattern.maxValue);
		assertFalse(range_a1.isEmpty());
		NaturalNumberRangeImpl range_a2 = new NaturalNumberRangeImpl(bigRangePattern.rangeString, bigRangePattern.maxValue);
		assertFalse(range_a2.isEmpty());
		
		// modified bigRangePattern
		NaturalNumberRangeImpl range_b1 = new NaturalNumberRangeImpl(bigRangePattern.rangeString + ",2147483658", bigRangePattern.maxValue);
		assertFalse(range_b1.isEmpty());
		NaturalNumberRangeImpl range_b2 = new NaturalNumberRangeImpl("2147483658," + bigRangePattern.rangeString, bigRangePattern.maxValue);
		assertFalse(range_b2.isEmpty());
		
		// check hashcode
		int h;
		String rs;
		//--- e1
		h = range_e1.hashCode();
		rs = range_e1.toRangeString();
		assertEquals(rs.hashCode(), h);
		//--- e2
		rs = range_e2.toRangeString();
		h = range_e2.hashCode();
		assertEquals(rs.hashCode(), h);
		//--- a1
		h = range_a1.hashCode();
		rs = range_a1.toRangeString();
		assertEquals(rs.hashCode(), h);
		//--- a2
		rs = range_a2.toRangeString();
		h = range_a2.hashCode();
		assertEquals(rs.hashCode(), h);
		//--- b1
		h = range_b1.hashCode();
		rs = range_b1.toRangeString();
		assertEquals(rs.hashCode(), h);
		//--- b2
		rs = range_b2.toRangeString();
		h = range_b2.hashCode();
		assertEquals(rs.hashCode(), h);
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeImpl#equals(java.lang.Object)}.
	 */
	public void testEqualsObject() {
		// empty
		NaturalNumberRangeImpl range_e1 = new NaturalNumberRangeImpl(null);
		assertTrue(range_e1.isEmpty());
		NaturalNumberRangeImpl range_e2 = new NaturalNumberRangeImpl("");
		assertTrue(range_e2.isEmpty());
		
		// bigRangePattern
		NaturalNumberRangeImpl range_a1 = new NaturalNumberRangeImpl(bigRangePattern.rangeString, bigRangePattern.maxValue);
		assertFalse(range_a1.isEmpty());
		NaturalNumberRangeImpl range_a2 = new NaturalNumberRangeImpl(bigRangePattern.rangeString, bigRangePattern.maxValue);
		assertFalse(range_a2.isEmpty());
		
		// modified bigRangePattern
		NaturalNumberRangeImpl range_b1 = new NaturalNumberRangeImpl(bigRangePattern.rangeString + ",2147483658", bigRangePattern.maxValue);
		assertFalse(range_b1.isEmpty());
		NaturalNumberRangeImpl range_b2 = new NaturalNumberRangeImpl("2147483658," + bigRangePattern.rangeString, bigRangePattern.maxValue);
		assertFalse(range_b2.isEmpty());
		
		// check equals
		assertEquals(range_e1, range_e2);
		assertEquals(range_e2, range_e1);
		assertEquals(range_a1, range_a2);
		assertEquals(range_a2, range_a1);
		assertEquals(range_b1, range_b2);
		assertEquals(range_b2, range_b1);
		assertEquals(range_e1, range_e1);
		assertEquals(range_a1, range_a1);
		assertEquals(range_b1, range_b1);
		
		assertEquals(range_e1.hashCode(), range_e2.hashCode());
		assertEquals(range_a1.hashCode(), range_a2.hashCode());
		assertEquals(range_b1.hashCode(), range_b2.hashCode());
		
		assertFalse(range_e1.equals(range_a1));
		assertFalse(range_e1.equals(range_b1));
		assertFalse(range_a1.equals(range_e1));
		assertFalse(range_a1.equals(range_b1));
		assertFalse(range_b1.equals(range_e1));
		assertFalse(range_b1.equals(range_a1));
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeImpl#toString()}.
	 */
	public void testToString() {
		NaturalNumberRangeImpl range;
		
		// empty
		range = new NaturalNumberRangeImpl(null);
		assertTrue(range.isEmpty());
		assertEquals("", range.toString());
		
		// bigRangePattern
		range = new NaturalNumberRangeImpl(bigRangePattern.rangeString, bigRangePattern.maxValue);
		assertFalse(range.isEmpty());
		assertEquals(0, bigRangePattern.from.compareTo(range.getDecimalFromValue()));
		assertEquals(0, bigRangePattern.to  .compareTo(range.getDecimalToValue()));
		assertEquals(0, bigRangePattern.step.compareTo(range.getDecimalStepValue()));
		assertEquals(rangeStringFromBigRange, range.toString());
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeImpl#toRangeString()}.
	 */
	public void testToRangeString() {
		NaturalNumberRangeImpl range;
		
		// empty
		range = new NaturalNumberRangeImpl(null);
		assertTrue(range.isEmpty());
		assertEquals("", range.toRangeString());
		
		// bigRangePattern
		range = new NaturalNumberRangeImpl(bigRangePattern.rangeString, bigRangePattern.maxValue);
		assertFalse(range.isEmpty());
		assertEquals(0, bigRangePattern.from.compareTo(range.getDecimalFromValue()));
		assertEquals(0, bigRangePattern.to  .compareTo(range.getDecimalToValue()));
		assertEquals(0, bigRangePattern.step.compareTo(range.getDecimalStepValue()));
		assertEquals(rangeStringFromBigRange, range.toRangeString());
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeImpl#formatRangesString(java.lang.StringBuilder)}.
	 */
	public void testFormatRangesString() {
		NaturalNumberRangeImpl range;
		StringBuilder sb;
		
		// empty
		range = new NaturalNumberRangeImpl(null);
		assertTrue(range.isEmpty());
		sb = new StringBuilder();
		range.formatRangesString(sb);
		assertEquals("", sb.toString());
		
		// bigRangePattern
		range = new NaturalNumberRangeImpl(bigRangePattern.rangeString, bigRangePattern.maxValue);
		assertFalse(range.isEmpty());
		assertEquals(0, bigRangePattern.from.compareTo(range.getDecimalFromValue()));
		assertEquals(0, bigRangePattern.to  .compareTo(range.getDecimalToValue()));
		assertEquals(0, bigRangePattern.step.compareTo(range.getDecimalStepValue()));
		sb = new StringBuilder();
		range.formatRangesString(sb);
		assertEquals(rangeStringFromBigRange, sb.toString());
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeImpl#formatRangeString(java.lang.StringBuilder, ssac.aadl.runtime.util.range.internal.NumberRangeImpl)}.
	 */
	public void testFormatRangeString() {
		NaturalNumberRangeImpl range = new NaturalNumberRangeImpl(null);
		assertTrue(range.isEmpty());
		
		for (int i = 0; i < rebuildedBigRanges.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			StringBuilder sb = new StringBuilder();
			range.formatRangeString(sb, rebuildedBigRanges[i]);
			assertEquals(msg, rangeStringsFromBigRanges[i], sb.toString());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeImpl#getMinRangeImpl()}.
	 */
	public void testGetMinRangeImpl() {
		NaturalNumberRangeImpl range;
		
		// empty
		range = new NaturalNumberRangeImpl(null);
		assertTrue(range.isEmpty());
		try {
			range.getMinRangeImpl();
			fail("Must be throw IndexOutOfBoundsException.");
		} catch (IndexOutOfBoundsException ex) {
			assertTrue(true);
		}

		// bigRangePattern
		range = new NaturalNumberRangeImpl(bigRangePattern.rangeString, bigRangePattern.maxValue);
		assertFalse(range.isEmpty());
		assertEquals(0, bigRangePattern.from.compareTo(range.getDecimalFromValue()));
		assertEquals(0, bigRangePattern.to  .compareTo(range.getDecimalToValue()));
		assertEquals(0, bigRangePattern.step.compareTo(range.getDecimalStepValue()));
		int i = 0;
		String msg = String.format("Test params : [%d] ", i);
		NumberRangeImpl ansRange = rebuildedBigRanges[i];
		NumberRangeImpl retRange = range.getMinRangeImpl();
		assertEquals(msg, ansRange.getClass(), retRange.getClass());
		assertEquals(msg, 0, ansRange.getDecimalFromValue().compareTo(retRange.getDecimalFromValue()));
		assertEquals(msg, 0, ansRange.getDecimalToValue()  .compareTo(retRange.getDecimalToValue()));
		assertEquals(msg, 0, ansRange.getDecimalStepValue().compareTo(retRange.getDecimalStepValue()));
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeImpl#getMaxRangeImpl()}.
	 */
	public void testGetMaxRangeImpl() {
		NaturalNumberRangeImpl range;
		
		// empty
		range = new NaturalNumberRangeImpl(null);
		assertTrue(range.isEmpty());
		try {
			range.getMaxRangeImpl();
			fail("Must be throw IndexOutOfBoundsException.");
		} catch (IndexOutOfBoundsException ex) {
			assertTrue(true);
		}

		// bigRangePattern
		range = new NaturalNumberRangeImpl(bigRangePattern.rangeString, bigRangePattern.maxValue);
		assertFalse(range.isEmpty());
		assertEquals(0, bigRangePattern.from.compareTo(range.getDecimalFromValue()));
		assertEquals(0, bigRangePattern.to  .compareTo(range.getDecimalToValue()));
		assertEquals(0, bigRangePattern.step.compareTo(range.getDecimalStepValue()));
		int i = rebuildedBigRanges.length - 1;
		String msg = String.format("Test params : [%d] ", i);
		NumberRangeImpl ansRange = rebuildedBigRanges[i];
		NumberRangeImpl retRange = range.getMaxRangeImpl();
		assertEquals(msg, ansRange.getClass(), retRange.getClass());
		assertEquals(msg, 0, ansRange.getDecimalFromValue().compareTo(retRange.getDecimalFromValue()));
		assertEquals(msg, 0, ansRange.getDecimalToValue()  .compareTo(retRange.getDecimalToValue()));
		assertEquals(msg, 0, ansRange.getDecimalStepValue().compareTo(retRange.getDecimalStepValue()));
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeImpl#getIllegalNumberError(ssac.aadl.runtime.util.range.internal.NaturalNumberRangeTokenizer)}.
	 */
	public void testGetIllegalNumberError() {
		String msg = "Illegal number [pos:5, str:\"abc\"]";
		String str = "1-3,abc,4";
		NaturalNumberRangeTokenizer tokenizer = new NaturalNumberRangeTokenizer(str);
		for (tokenizer.nextToken(); tokenizer.getLastTokenType()!=NaturalNumberRangeTokenizer.EOT && !"abc".equals(tokenizer.getLastToken()); tokenizer.nextToken()) ;
		NaturalNumberRangeImpl range = new NaturalNumberRangeImpl(null);
		
		assertEquals(msg, range.getIllegalNumberError(tokenizer));
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeImpl#getInvalidCharacterError(ssac.aadl.runtime.util.range.internal.NaturalNumberRangeTokenizer)}.
	 */
	public void testGetInvalidCharacterError() {
		String msg = "Invalid characters [pos:5, str:\"abc\"]";
		String str = "1-3,abc,4";
		NaturalNumberRangeTokenizer tokenizer = new NaturalNumberRangeTokenizer(str);
		for (tokenizer.nextToken(); tokenizer.getLastTokenType()!=NaturalNumberRangeTokenizer.EOT && !"abc".equals(tokenizer.getLastToken()); tokenizer.nextToken()) ;
		NaturalNumberRangeImpl range = new NaturalNumberRangeImpl(null);
		
		assertEquals(msg, range.getInvalidCharacterError(tokenizer));
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeImpl#getIllegalFormatError(ssac.aadl.runtime.util.range.internal.NaturalNumberRangeTokenizer)}.
	 */
	public void testGetIllegalFormatErrorNaturalNumberRangeTokenizer() {
		String msg = "Illegal number range format [pos:5, str:\"abc\"]";
		String str = "1-3,abc,4";
		NaturalNumberRangeTokenizer tokenizer = new NaturalNumberRangeTokenizer(str);
		for (tokenizer.nextToken(); tokenizer.getLastTokenType()!=NaturalNumberRangeTokenizer.EOT && !"abc".equals(tokenizer.getLastToken()); tokenizer.nextToken()) ;
		NaturalNumberRangeImpl range = new NaturalNumberRangeImpl(null);
		
		assertEquals(msg, range.getIllegalFormatError(tokenizer));
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeImpl#getIllegalFormatError(int, java.lang.String)}.
	 */
	public void testGetIllegalFormatErrorIntString() {
		String msg = "Illegal number range format [pos:5, str:\"abc\"]";
		String str = "1-3,abc,4";
		NaturalNumberRangeTokenizer tokenizer = new NaturalNumberRangeTokenizer(str);
		for (tokenizer.nextToken(); tokenizer.getLastTokenType()!=NaturalNumberRangeTokenizer.EOT && !"abc".equals(tokenizer.getLastToken()); tokenizer.nextToken()) ;
		NaturalNumberRangeImpl range = new NaturalNumberRangeImpl(null);
		
		assertEquals(msg, range.getIllegalFormatError(tokenizer.getLastTokenPosition(), tokenizer.getLastToken()));
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeImpl#formatTokenError(java.lang.String, int, java.lang.String)}.
	 */
	public void testFormatTokenError() {
		NaturalNumberRangeImpl range = new NaturalNumberRangeImpl(null);
		
		// null
		assertEquals("Illegal number range format [pos:5, str:\"abc\"]", range.formatTokenError(null, 4, "abc"));
		
		// empty
		assertEquals("Illegal number range format [pos:5, str:\"abc\"]", range.formatTokenError("", 4, "abc"));
		
		// specified message
		assertEquals("The specified message [pos:5, str:\"abc\"]", range.formatTokenError("The specified message", 4, "abc"));
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.internal.NaturalNumberRangeImpl#convertLastTokenToNumber(ssac.aadl.runtime.util.range.internal.NaturalNumberRangeTokenizer)}.
	 */
	public void testConvertLastTokenToNumber() {
		NaturalNumberRangeImpl range = new NaturalNumberRangeImpl(null);
		NaturalNumberRangeTokenizer tokenizer;
		
		tokenizer = new NaturalNumberRangeTokenizer("abc,1-3");
		assertEquals(NaturalNumberRangeTokenizer.INVALID, tokenizer.nextToken());
		try {
			range.convertLastTokenToNumber(tokenizer);
			fail("Must be throw IllegalArgumentException.");
		} catch (IllegalArgumentException ex) {
			assertEquals("Illegal number [pos:1, str:\"abc\"]", ex.getMessage());
		}

		assertEquals(NaturalNumberRangeTokenizer.DELIMITER, tokenizer.nextToken());
		assertEquals(NaturalNumberRangeTokenizer.NUMBER, tokenizer.nextToken());
		assertEquals(0, BigDecimal.ONE.compareTo(range.convertLastTokenToNumber(tokenizer)));
	}
}
