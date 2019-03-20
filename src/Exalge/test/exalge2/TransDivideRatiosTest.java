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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

public class TransDivideRatiosTest extends TestCase {

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/**
	 * {@link exalge2.TransDivideRatios#TransDivideRatios()} のためのテスト・メソッド。
	 */
	public void testTransDivideRatios() {
		TransDivideRatios tdr = new TransDivideRatios();
		assertTrue(tdr.isEmpty());
		assertTrue(BigDecimal.ZERO.compareTo(tdr.getTotalRatio())==0);
	}

	/**
	 * {@link exalge2.TransDivideRatios#isEmpty()} のためのテスト・メソッド。
	 */
	public void testIsEmpty() {
		TransDivideRatios tdr = new TransDivideRatios();
		assertEquals(tdr.size(), 0);
		assertTrue(tdr.isEmpty());
		
		tdr.put(new ExBasePattern("*"), BigDecimal.ONE);
		assertEquals(tdr.size(), 1);
		assertFalse(tdr.isEmpty());
	}

	/**
	 * {@link exalge2.TransDivideRatios#size()} のためのテスト・メソッド。
	 */
	public void testSize() {
		TransDivideRatios tdr = new TransDivideRatios();
		assertTrue(tdr.isEmpty());
		assertEquals(tdr.size(), 0);
		
		tdr.put(new ExBasePattern("name1"), new BigDecimal(1));
		assertEquals(tdr.size(), 1);
		
		tdr.put(new ExBasePattern("name2"), new BigDecimal(2));
		assertEquals(tdr.size(), 2);
		
		tdr.put(new ExBasePattern("name3"), new BigDecimal(3));
		assertEquals(tdr.size(), 3);
		
		tdr.put(new ExBasePattern("name1"), new BigDecimal(10));
		assertEquals(tdr.size(), 3);
	}

	/**
	 * {@link exalge2.TransDivideRatios#clear()} のためのテスト・メソッド。
	 */
	public void testClear() {
		TransDivideRatios tdr = new TransDivideRatios();
		assertTrue(tdr.isEmpty());
		tdr.clear();
		assertTrue(tdr.isEmpty());
		
		tdr.put(new ExBasePattern("name1"), new BigDecimal(1));
		tdr.put(new ExBasePattern("name2"), new BigDecimal(2));
		tdr.put(new ExBasePattern("name3"), new BigDecimal(3));
		assertFalse(tdr.isEmpty());
		tdr.clear();
		assertTrue(tdr.isEmpty());
	}

	/**
	 * {@link exalge2.TransDivideRatios#containsPattern(exalge2.ExBasePattern)} のためのテスト・メソッド。
	 */
	public void testContainsPattern() {
		ExBasePattern pat1 = new ExBasePattern("name1");
		ExBasePattern pat2 = new ExBasePattern("name2");
		ExBasePattern pat3 = new ExBasePattern("name3");
		ExBasePattern pat4 = new ExBasePattern("name4");
		ExBasePattern pat5 = new ExBasePattern("name1-NO_HAT-unit-time-subject");
		
		TransDivideRatios tdr = new TransDivideRatios();
		assertTrue(tdr.isEmpty());
		assertFalse(tdr.containsPattern(pat1));
		assertFalse(tdr.containsPattern(pat2));
		assertFalse(tdr.containsPattern(pat3));
		assertFalse(tdr.containsPattern(pat4));
		assertFalse(tdr.containsPattern(pat5));
		
		tdr.put(new ExBasePattern("name1"), new BigDecimal(1));
		tdr.put(new ExBasePattern("name2"), new BigDecimal(2));
		tdr.put(new ExBasePattern("name3"), new BigDecimal(3));
		assertFalse(tdr.isEmpty());
		assertTrue(tdr.containsPattern(pat1));
		assertTrue(tdr.containsPattern(pat2));
		assertTrue(tdr.containsPattern(pat3));
		assertFalse(tdr.containsPattern(pat4));
		assertFalse(tdr.containsPattern(pat5));
	}

	/**
	 * {@link exalge2.TransDivideRatios#getRatio(exalge2.ExBasePattern)} のためのテスト・メソッド。
	 */
	public void testGetRatio() {
		ExBasePattern pat1 = new ExBasePattern("name1");
		ExBasePattern pat2 = new ExBasePattern("name2");
		ExBasePattern pat3 = new ExBasePattern("name3");
		ExBasePattern pat4 = new ExBasePattern("name4");
		ExBasePattern pat5 = new ExBasePattern("name1-NO_HAT-unit-time-subject");
		
		TransDivideRatios tdr = new TransDivideRatios();
		assertTrue(tdr.isEmpty());
		assertTrue(tdr.getRatio(pat1) == null);
		assertTrue(tdr.getRatio(pat2) == null);
		assertTrue(tdr.getRatio(pat3) == null);
		assertTrue(tdr.getRatio(pat4) == null);
		assertTrue(tdr.getRatio(pat5) == null);
		
		tdr.put(new ExBasePattern("name1"), new BigDecimal(1));
		tdr.put(new ExBasePattern("name2"), new BigDecimal(2));
		tdr.put(new ExBasePattern("name3"), new BigDecimal(3));
		assertFalse(tdr.isEmpty());
		assertTrue(tdr.getRatio(pat1).compareTo(new BigDecimal(1))==0);
		assertTrue(tdr.getRatio(pat2).compareTo(new BigDecimal(2))==0);
		assertTrue(tdr.getRatio(pat3).compareTo(new BigDecimal(3))==0);
		assertTrue(tdr.getRatio(pat4) == null);
		assertTrue(tdr.getRatio(pat5) == null);
	}

	/**
	 * {@link exalge2.TransDivideRatios#getTotalRatio()} のためのテスト・メソッド。
	 */
	public void testGetTotalRatio() {
		TransDivideRatios tdr = new TransDivideRatios();
		assertTrue(tdr.isEmpty());
		assertEquals(tdr.getTotalRatio(), BigDecimal.ZERO);
		
		boolean coughtException;
		try {
			tdr.setTotalRatio(null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		
		BigDecimal[] values = new BigDecimal[]{
				new BigDecimal("1.1"),
				new BigDecimal("2.2"),
				new BigDecimal("3.3"),
				new BigDecimal(4),
				new BigDecimal(5),
				new BigDecimal("12345.6789"),
				new BigDecimal("0.1"),
		};
		for (BigDecimal val : values) {
			tdr.setTotalRatio(val);
			assertEquals(val, tdr.getTotalRatio());
		}
	}

	/**
	 * {@link exalge2.TransDivideRatios#patterns()} のためのテスト・メソッド。
	 */
	public void testPatterns() {
		TransDivideRatios tdr = new TransDivideRatios();
		assertTrue(tdr.isEmpty());
		assertTrue(tdr.patterns().isEmpty());
		
		Set<ExBasePattern> ansPatterns = new LinkedHashSet<ExBasePattern>(Arrays.asList(
				new ExBasePattern("name1"),
				new ExBasePattern("name2"),
				new ExBasePattern("name3"),
				new ExBasePattern("name4"),
				new ExBasePattern("name1-NO_HAT-unit-time-subject")
		));
		
		int i = 0;
		for (ExBasePattern pat : ansPatterns) {
			tdr.put(pat, new BigDecimal(++i));
		}
		assertFalse(tdr.isEmpty());
		assertEquals(tdr.size(), ansPatterns.size());
		assertEquals(tdr.patterns(), ansPatterns);
	}

	/**
	 * {@link exalge2.TransDivideRatios#put(exalge2.ExBasePattern, java.math.BigDecimal)} のためのテスト・メソッド。
	 */
	public void testPut() {
		TransDivideRatios tdr = new TransDivideRatios();
		assertTrue(tdr.isEmpty());
		
		// null
		boolean coughtException;
		try {
			tdr.put(new ExBasePattern("name1"), null);
			coughtException = false;
		} catch(NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			tdr.put(null, new BigDecimal(1));
			coughtException = false;
		} catch(NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			tdr.put(null, null);
			coughtException = false;
		} catch(NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		
		// data
		LinkedHashMap<ExBasePattern, BigDecimal> data = new LinkedHashMap<ExBasePattern,BigDecimal>();
		data.put(new ExBasePattern("name1"), new BigDecimal("0.1"));
		data.put(new ExBasePattern("name2-*-unit"), new BigDecimal("0.2"));
		data.put(new ExBasePattern("name3-*-unit-time"), new BigDecimal("0.3"));
		data.put(new ExBasePattern("name4-*-unit-time-subject"), new BigDecimal("0.4"));
		
		// put
		tdr = new TransDivideRatios();
		assertTrue(tdr.isEmpty());
		for (Map.Entry<ExBasePattern, BigDecimal> entry : data.entrySet()) {
			tdr.put(entry.getKey(), entry.getValue());
		}
		assertEquals(tdr.size(), data.size());
		assertEquals(tdr.patterns(), data.keySet());
		for (Map.Entry<ExBasePattern, BigDecimal> entry : data.entrySet()) {
			assertEquals(entry.getValue(), tdr.getRatio(entry.getKey()));
		}
	}

	/**
	 * {@link exalge2.TransDivideRatios#remove(exalge2.ExBasePattern)} のためのテスト・メソッド。
	 */
	public void testRemove() {
		ExBasePattern pat1 = new ExBasePattern("name1");
		ExBasePattern pat2 = new ExBasePattern("name2-*-unit");
		ExBasePattern pat3 = new ExBasePattern("name3-*-unit-time");
		ExBasePattern pat4 = new ExBasePattern("name4-*-unit-time-subject");
		
		TransDivideRatios tdr = new TransDivideRatios();
		tdr.put(pat1, new BigDecimal("0.1"));
		tdr.put(pat2, new BigDecimal("0.2"));
		tdr.put(pat3, new BigDecimal("0.3"));
		tdr.put(pat4, new BigDecimal("0.4"));
		assertFalse(tdr.isEmpty());
		int defSize = tdr.size();

		tdr.remove(null);
		assertEquals(defSize, tdr.size());
		tdr.remove(new ExBasePattern("name1-NO_HAT"));
		assertEquals(defSize, tdr.size());

		tdr.remove(pat1);
		assertEquals(defSize-1, tdr.size());
		assertFalse(tdr.containsPattern(pat1));
		tdr.remove(pat2);
		assertEquals(defSize-2, tdr.size());
		assertFalse(tdr.containsPattern(pat2));
		tdr.remove(pat3);
		assertEquals(defSize-3, tdr.size());
		assertFalse(tdr.containsPattern(pat3));
		tdr.remove(pat4);
		assertEquals(defSize-4, tdr.size());
		assertFalse(tdr.containsPattern(pat4));
	}

	/**
	 * {@link exalge2.TransDivideRatios#updateTotalRatio()} のためのテスト・メソッド。
	 */
	public void testUpdateTotalRatio() {
		ExBasePattern pat1 = new ExBasePattern("name1");
		ExBasePattern pat2 = new ExBasePattern("name2-*-unit");
		ExBasePattern pat3 = new ExBasePattern("name3-*-unit-time");
		ExBasePattern pat4 = new ExBasePattern("name4-*-unit-time-subject");
		
		TransDivideRatios tdr = new TransDivideRatios();
		assertEquals(BigDecimal.ZERO, tdr.getTotalRatio());
		
		tdr.put(pat1, new BigDecimal("0.1"));
		tdr.put(pat2, new BigDecimal("0.2"));
		tdr.put(pat3, new BigDecimal("0.3"));
		tdr.put(pat4, new BigDecimal("0.4"));
		assertEquals(BigDecimal.ZERO, tdr.getTotalRatio());
		
		tdr.updateTotalRatio();
		assertTrue(BigDecimal.ONE.compareTo(tdr.getTotalRatio())==0);
	}

	/**
	 * {@link exalge2.TransDivideRatios#divideTransfer(exalge2.ExBase, java.math.BigDecimal, boolean)} のためのテスト・メソッド。
	 */
	public void testDivideTransfer() {
		ExBase base = new ExBase("りんご-HAT-円-Y2009M03-果物");
		BigDecimal val = new BigDecimal(120);
		TransDivideRatios tdr = new TransDivideRatios();

		boolean coughtException;
		try {
			tdr.divideTransfer(null, val, true);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			tdr.divideTransfer(base, null, true);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			tdr.divideTransfer(null, null, true);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			tdr.divideTransfer(base, val, true);
			coughtException = false;
		} catch (IllegalStateException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		
		ExBasePattern pat1 = new ExBasePattern("name1");
		ExBasePattern pat2 = new ExBasePattern("name2-*-unit");
		ExBasePattern pat3 = new ExBasePattern("name3-*-unit-time");
		ExBasePattern pat4 = new ExBasePattern("name4-*-unit-time-subject");
		tdr.put(pat1, new BigDecimal("10"));
		tdr.put(pat2, new BigDecimal("20"));
		tdr.put(pat3, new BigDecimal("30"));
		tdr.put(pat4, new BigDecimal("40"));
		
		try {
			tdr.divideTransfer(null, val, true);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			tdr.divideTransfer(base, null, true);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			tdr.divideTransfer(null, null, true);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			tdr.divideTransfer(base, val, true);
			coughtException = false;
		} catch (ArithmeticException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		tdr.updateTotalRatio();
		
		// use total ratio
		Exalge ansAlge1 = ExalgeTest.makeAlge(
				new ExBase("name1-HAT-円-Y2009M03-果物")  , new BigDecimal(12),
				new ExBase("name2-HAT-unit-Y2009M03-果物"), new BigDecimal(24),
				new ExBase("name3-HAT-unit-time-果物")    , new BigDecimal(36),
				new ExBase("name4-HAT-unit-time-subject") , new BigDecimal(48)
		);
		Exalge ret1 = tdr.divideTransfer(base, val, true);
		assertEquals(ansAlge1, ret1);
		
		// not use total ratio
		Exalge ansAlge2 = ExalgeTest.makeAlge(
				new ExBase("name1-HAT-円-Y2009M03-果物")  , new BigDecimal(1200),
				new ExBase("name2-HAT-unit-Y2009M03-果物"), new BigDecimal(2400),
				new ExBase("name3-HAT-unit-time-果物")    , new BigDecimal(3600),
				new ExBase("name4-HAT-unit-time-subject") , new BigDecimal(4800)
		);
		Exalge ret2 = tdr.divideTransfer(base, val, false);
		assertEquals(ansAlge2, ret2);
	}

	/**
	 * {@link exalge2.TransDivideRatios#clone()} のためのテスト・メソッド。
	 */
	public void testClone() {
		ExBasePattern pat1 = new ExBasePattern("name1");
		ExBasePattern pat2 = new ExBasePattern("name2-*-unit");
		ExBasePattern pat3 = new ExBasePattern("name3-*-unit-time");
		ExBasePattern pat4 = new ExBasePattern("name4-*-unit-time-subject");
		
		TransDivideRatios tdr1 = new TransDivideRatios();
		assertTrue(tdr1.isEmpty());
		assertEquals(BigDecimal.ZERO, tdr1.getTotalRatio());
		TransDivideRatios tdr2 = (TransDivideRatios)tdr1.clone();
		assertFalse(tdr1 == tdr2);
		assertTrue(tdr2.isEmpty());
		assertEquals(BigDecimal.ZERO, tdr2.getTotalRatio());
		tdr1.put(pat1, new BigDecimal("0.1"));
		tdr1.updateTotalRatio();
		assertTrue(tdr1.size() != tdr2.size());
		assertTrue(tdr1.getTotalRatio().compareTo(tdr2.getTotalRatio())!=0);
		
		tdr1 = new TransDivideRatios();
		tdr1.put(pat1, new BigDecimal("0.1"));
		tdr1.put(pat2, new BigDecimal("0.2"));
		tdr1.put(pat3, new BigDecimal("0.3"));
		tdr1.updateTotalRatio();
		assertFalse(tdr1.isEmpty());
		tdr2 = (TransDivideRatios)tdr1.clone();
		assertEquals(tdr1.size(), tdr2.size());
		assertEquals(tdr1.patterns(), tdr2.patterns());
		assertEquals(tdr1.getTotalRatio(), tdr2.getTotalRatio());
		
		tdr1.put(pat4, new BigDecimal("0.4"));
		tdr1.updateTotalRatio();
		assertTrue(tdr1.size() != tdr2.size());
		assertFalse(tdr1.patterns().equals(tdr2.patterns()));
		assertTrue(tdr1.getTotalRatio().compareTo(tdr2.getTotalRatio())!=0);
	}

	/**
	 * {@link exalge2.TransDivideRatios#hashCode()} のためのテスト・メソッド。
	 */
	public void testHashCode() {
		TransDivideRatios tdr = new TransDivideRatios();
		assertTrue(tdr.isEmpty());
		assertEquals(0, tdr.hashCode());
		
		LinkedHashMap<ExBasePattern, BigDecimal> data = new LinkedHashMap<ExBasePattern,BigDecimal>();
		data.put(new ExBasePattern("name1"), new BigDecimal("0.1"));
		data.put(new ExBasePattern("name2-*-unit"), new BigDecimal("0.2"));
		data.put(new ExBasePattern("name3-*-unit-time"), new BigDecimal("0.3"));
		data.put(new ExBasePattern("name4-*-unit-time-subject"), new BigDecimal("0.4"));
		BigDecimal totalRatio = BigDecimal.ZERO;
		for (Map.Entry<ExBasePattern, BigDecimal> entry : data.entrySet()) {
			tdr.put(entry.getKey(), entry.getValue());
			totalRatio = totalRatio.add(entry.getValue());
		}
		tdr.updateTotalRatio();
		assertEquals(data.size(), tdr.size());
		assertEquals(data.keySet(), tdr.patterns());
		for (Map.Entry<ExBasePattern, BigDecimal> entry : data.entrySet()) {
			assertEquals(entry.getValue(), tdr.getRatio(entry.getKey()));
		}
		assertEquals(totalRatio, tdr.getTotalRatio());
		
		assertEquals((totalRatio.hashCode() + data.hashCode()), tdr.hashCode());
	}

	/**
	 * {@link exalge2.TransDivideRatios#equals(java.lang.Object)} のためのテスト・メソッド。
	 */
	public void testEqualsObject() {
		TransDivideRatios tdr1 = new TransDivideRatios();
		TransDivideRatios tdr2 = new TransDivideRatios();
		assertFalse(tdr1.equals(null));
		assertTrue(tdr1.equals(tdr2));
		
		LinkedHashMap<ExBasePattern, BigDecimal> data = new LinkedHashMap<ExBasePattern,BigDecimal>();
		data.put(new ExBasePattern("name1"), new BigDecimal("0.1"));
		data.put(new ExBasePattern("name2-*-unit"), new BigDecimal("0.2"));
		data.put(new ExBasePattern("name3-*-unit-time"), new BigDecimal("0.3"));
		data.put(new ExBasePattern("name4-*-unit-time-subject"), new BigDecimal("0.4"));
		tdr1 = new TransDivideRatios();
		tdr2 = new TransDivideRatios();
		for (Map.Entry<ExBasePattern, BigDecimal> entry : data.entrySet()) {
			tdr1.put(entry.getKey(), entry.getValue());
			tdr2.put(entry.getKey(), entry.getValue());
		}
		assertEquals(data.size(), tdr1.size());
		assertEquals(data.size(), tdr2.size());
		assertEquals(data.keySet(), tdr1.patterns());
		assertEquals(data.keySet(), tdr2.patterns());
		for (Map.Entry<ExBasePattern, BigDecimal> entry : data.entrySet()) {
			assertEquals(entry.getValue(), tdr1.getRatio(entry.getKey()));
			assertEquals(entry.getValue(), tdr2.getRatio(entry.getKey()));
		}
		
		assertTrue(tdr1.equals(tdr2));
		assertTrue(tdr2.equals(tdr1));
		assertEquals(tdr1.hashCode(), tdr2.hashCode());
		
		tdr1.updateTotalRatio();
		assertFalse(tdr1.equals(tdr2));
		assertFalse(tdr2.equals(tdr1));
		
		tdr2.updateTotalRatio();
		assertTrue(tdr1.equals(tdr2));
		assertTrue(tdr2.equals(tdr1));
		assertEquals(tdr1.hashCode(), tdr2.hashCode());
	}

	/**
	 * {@link exalge2.TransDivideRatios#toString()} のためのテスト・メソッド。
	 */
	public void testToString() {
		TransDivideRatios tdr = new TransDivideRatios();
		assertEquals("(0)[]", tdr.toString());
		tdr.setTotalRatio(new BigDecimal("12.34"));
		assertEquals("(12.34)[]", tdr.toString());
		
		tdr.put(new ExBasePattern("name1"), new BigDecimal("0.1"));
		tdr.put(new ExBasePattern("name2-*-unit"), new BigDecimal("0.2"));
		tdr.put(new ExBasePattern("name3-*-unit-time"), new BigDecimal("0.3"));
		tdr.put(new ExBasePattern("name4-*-unit-time-subject"), new BigDecimal("0.4"));
		tdr.updateTotalRatio();
		
		StringBuilder sb = new StringBuilder();
		sb.append("(" + tdr.getTotalRatio().stripTrailingZeros().toPlainString() + ")[");
		sb.append("(0.1)name1-*-*-*-*");
		sb.append(",(0.2)name2-*-unit-*-*");
		sb.append(",(0.3)name3-*-unit-time-*");
		sb.append(",(0.4)name4-*-unit-time-subject");
		sb.append("]");

		assertEquals(sb.toString(), tdr.toString());
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
