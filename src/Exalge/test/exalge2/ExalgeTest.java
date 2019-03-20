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
 *  <author> Li Hou(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
package exalge2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import junit.framework.TestCase;

public class ExalgeTest extends TestCase {

	static protected final String nohatAppleYen	= "りんご-NO_HAT-円-#-#";
	static protected final String hatAppleYen	= "りんご-HAT-円-#-#";
	static protected final String nohatAppleNum	= "りんご-NO_HAT-個-#-#";
	static protected final String hatAppleNum	= "りんご-HAT-個-#-#";
	static protected final String nohatOrangeYen	= "みかん-NO_HAT-円-#-#";
	static protected final String hatOrangeYen	= "みかん-HAT-円-#-#";
	static protected final String nohatOrangeNum	= "みかん-NO_HAT-個-#-#";
	static protected final String hatOrangeNum	= "みかん-HAT-個-#-#";
	static protected final String nohatBananaYen	= "ばなな-NO_HAT-円-#-#";
	static protected final String hatBananaYen	= "ばなな-HAT-円-#-#";
	static protected final String nohatBananaNum	= "ばなな-NO_HAT-個-#-#";
	static protected final String hatBananaNum	= "ばなな-HAT-個-#-#";
	static protected final String nohatFruitYen	= "果物-NO_HAT-円-#-#";
	static protected final String hatFruitYen	= "果物-HAT-円-#-#";
	static protected final String nohatFruitNum	= "果物-NO_HAT-個-#-#";
	static protected final String hatFruitNum	= "果物-HAT-個-#-#";
	
	static protected final String nohatMelonYen	= "メロン-NO_HAT-円-#-#";
	static protected final String hatMelonYen	= "メロン-HAT-円-#-#";
	static protected final String nohatMelonNum	= "メロン-NO_HAT-個-#-#";
	static protected final String hatMelonNum	= "メロン-HAT-個-#-#";
	static protected final String nohatMangoYen	= "マンゴー-NO_HAT-円-#-#";
	static protected final String hatMangoYen	= "マンゴー-HAT-円-#-#";
	static protected final String nohatMangoNum	= "マンゴー-NO_HAT-個-#-#";
	static protected final String hatMangoNum	= "マンゴー-HAT-個-#-#";
	static protected final String nohatExpFruitYen	= "高級果物-NO_HAT-円-#-#";
	static protected final String hatExpFruitYen	= "高級果物-HAT-円-#-#";
	static protected final String nohatExpFruitNum	= "高級果物-NO_HAT-個-#-#";
	static protected final String hatExpFruitNum	= "高級果物-HAT-個-#-#";
	
	static protected final String nohatTunaYen	= "まぐろ-NO_HAT-円-#-#";
	static protected final String hatTunaYen		= "まぐろ-HAT-円-#-#";
	static protected final String nohatTunaNum	= "まぐろ-NO_HAT-匹-#-#";
	static protected final String hatTunaNum		= "まぐろ-HAT-匹-#-#";
	static protected final String nohatBonitoYen	= "かつお-NO_HAT-円-#-#";
	static protected final String hatBonitoYen	= "かつお-HAT-円-#-#";
	static protected final String nohatBonitoNum	= "かつお-NO_HAT-匹-#-#";
	static protected final String hatBonitoNum	= "かつお-HAT-匹-#-#";
	static protected final String nohatFishYen	= "魚-NO_HAT-円-#-#";
	static protected final String hatFishYen		= "魚-HAT-円-#-#";
	static protected final String nohatFishNum	= "魚-NO_HAT-匹-#-#";
	static protected final String hatFishNum		= "魚-HAT-匹-#-#";
	
	static protected final String nohatCashYen	= "現金-NO_HAT-円-#-#";
	static protected final String hatCashYen		= "現金-HAT-円-#-#";

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	static protected Exalge makeAlge(ExBase base, int value) {
		return new Exalge(base, new BigDecimal(value));
	}
	
	static protected Exalge makeAlge(ExBase base, BigDecimal value) {
		return new Exalge(base, value);
	}

	/**
	 * 
	 * @param elems Base, BigDecimal, Base, BigDecimal ...
	 * @return Exalge
	 */
	static protected Exalge makeAlge(Object...elems) {
		Exalge alge = new Exalge();
		
		if (elems.length < 1 )
			return alge;
		
		for (int i = 0; i < elems.length; i+=2) {
			//--- base
			ExBase base = (ExBase)elems[i];
			//--- value
			BigDecimal value;
			if (elems[i+1] instanceof Number)
				value = new BigDecimal(elems[i+1].toString());
			else if (elems[i+1] instanceof String)
				value = new BigDecimal((String)elems[i+1]);
			else
				value = (BigDecimal)elems[i+1];
			//--- plus
			alge.plusValue(base, value);
		}
		return alge;
	}
	
	static protected ExBaseSet makeBaseSet(ExBase...bases) {
		return new ExBaseSet(Arrays.asList(bases));
	}

	/**
	 * {@link exalge2.Exalge#Exalge()} のためのテスト・メソッド。
	 */
	public void testExalge() {
		Exalge test = new Exalge();
		assertTrue(test.data.isEmpty());
		assertEquals(0, test.data.size());
	}

	/**
	 * {@link exalge2.Exalge#Exalge(java.lang.String, java.math.BigDecimal)} のためのテスト・メソッド。
	 */
	public void testExalgeStringBigDecimal() {
		ExBase base1 = new ExBase(nohatCashYen);
		Exalge test1 = new Exalge(nohatCashYen, BigDecimal.ZERO);
		assertEquals(1, test1.data.size());
		assertEquals(BigDecimal.ZERO, test1.data.get(base1));

		ExBase base2 = new ExBase(hatAppleNum);
		Exalge test2 = new Exalge(hatAppleNum, new BigDecimal(100));
		assertEquals(1, test2.data.size());
		assertEquals(new BigDecimal(100), test2.data.get(base2));
		
		ExBase base3 = new ExBase(nohatOrangeYen);
		Exalge test3 = new Exalge(nohatOrangeYen, BigDecimal.ZERO);
		assertEquals(1, test3.data.size());
		assertEquals(0, test3.data.get(base3).compareTo(BigDecimal.ZERO));
		
		ExBase base4 = new ExBase(hatOrangeYen);
		Exalge test4 = new Exalge(hatOrangeYen, BigDecimal.ZERO);
		assertEquals(1, test4.data.size());
		assertEquals(0, test4.data.get(base4).compareTo(BigDecimal.ZERO));
	}

	/**
	 * {@link exalge2.Exalge#Exalge(exalge2.ExBase, java.math.BigDecimal)} のためのテスト・メソッド。
	 */
	public void testExalgeExBaseBigDecimal() {
		ExBase base1 = new ExBase(nohatCashYen);
		Exalge test1 = new Exalge(base1, BigDecimal.ZERO);
		assertEquals(1, test1.data.size());
		assertEquals(BigDecimal.ZERO, test1.data.get(base1));

		ExBase base2 = new ExBase(hatAppleNum);
		Exalge test2 = new Exalge(base2, new BigDecimal(100));
		assertEquals(1, test2.data.size());
		assertEquals(new BigDecimal(100), test2.data.get(base2));
		
		ExBase base3 = new ExBase(nohatOrangeYen);
		Exalge test3 = new Exalge(base3, BigDecimal.ZERO);
		assertEquals(1, test3.data.size());
		assertEquals(0, test3.data.get(base3).compareTo(BigDecimal.ZERO));
		
		ExBase base4 = new ExBase(hatOrangeYen);
		Exalge test4 = new Exalge(base4, BigDecimal.ZERO);
		assertEquals(1, test4.data.size());
		assertEquals(0, test4.data.get(base4).compareTo(BigDecimal.ZERO));
	}

	/**
	 * {@link exalge2.Exalge#Exalge(java.lang.Object[])} のためのテスト・メソッド。
	 */
	public void testExalgeObjectArray() {
		final Object[] objData = new Object[]{
			new ExBase(nohatAppleYen), new BigDecimal(10),
			new ExBase(hatCashYen), new BigDecimal(10),
			new ExBase(nohatOrangeYen), new BigDecimal(20),
			new ExBase(hatCashYen), new BigDecimal(20),
			new ExBase(nohatBananaYen), new BigDecimal(30),
			new ExBase(hatCashYen), new BigDecimal(30),
			new ExBase(nohatMelonYen), new BigDecimal(0)
		};
		final Object[] seqData = new Object[]{
			new ExBase(nohatAppleYen), new BigDecimal(10),
			new ExBase(hatCashYen), new BigDecimal(60),
			new ExBase(nohatOrangeYen), new BigDecimal(20),
			new ExBase(nohatBananaYen), new BigDecimal(30),
			new ExBase(nohatMelonYen), new BigDecimal(0)
		};
		
		Exalge test = new Exalge(objData);
		assertEquals(5, test.data.size());
		
		assertEquals(new BigDecimal(10), test.data.get(new ExBase(nohatAppleYen)));
		assertEquals(new BigDecimal(20), test.data.get(new ExBase(nohatOrangeYen)));
		assertEquals(new BigDecimal(30), test.data.get(new ExBase(nohatBananaYen)));
		assertEquals(new BigDecimal(60), test.data.get(new ExBase(hatCashYen)));
		assertEquals(0, test.data.get(new ExBase(nohatMelonYen)).compareTo(BigDecimal.ZERO));
		
		ExAlgeSetIOTest.checkElemSequence(test, seqData);
	}

	/**
	 * {@link exalge2.Exalge#Exalge(java.util.Map)} のためのテスト・メソッド。
	 */
	public void testExalgeMapOfExBaseBigDecimal() {
		LinkedHashMap<ExBase,BigDecimal> testmap = new LinkedHashMap<ExBase,BigDecimal>();
		testmap.put(new ExBase(hatAppleYen), new BigDecimal(20));
		testmap.put(new ExBase(hatOrangeYen), new BigDecimal(40));
		testmap.put(new ExBase(hatBananaYen), new BigDecimal(60));
		testmap.put(new ExBase(hatMangoYen), new BigDecimal(0));
		testmap.put(new ExBase(nohatCashYen), new BigDecimal(120));
		
		final Object[] seqData = new Object[]{
			new ExBase(hatAppleYen), new BigDecimal(20),
			new ExBase(hatOrangeYen), new BigDecimal(40),
			new ExBase(hatBananaYen), new BigDecimal(60),
			new ExBase(hatMangoYen), new BigDecimal(0),
			new ExBase(nohatCashYen), new BigDecimal(120)
		};
		
		Exalge test = new Exalge(testmap);
		assertEquals(5, test.data.size());
		
		assertEquals(new BigDecimal(20), test.data.get(new ExBase(hatAppleYen)));
		assertEquals(new BigDecimal(40), test.data.get(new ExBase(hatOrangeYen)));
		assertEquals(new BigDecimal(60), test.data.get(new ExBase(hatBananaYen)));
		assertEquals(0, test.data.get(new ExBase(hatMangoYen)).compareTo(BigDecimal.ZERO));
		assertEquals(new BigDecimal(120), test.data.get(new ExBase(nohatCashYen)));
		
		ExAlgeSetIOTest.checkElemSequence(test, seqData);
	}

	/**
	 * {@link exalge2.Exalge#isEmpty()} のためのテスト・メソッド。
	 */
	public void testIsEmpty() {
		Exalge test1 = new Exalge();
		assertTrue(test1.isEmpty());
		
		Exalge test2 = new Exalge(nohatFishNum, new BigDecimal(30));
		assertFalse(test2.isEmpty());
	}

	/**
	 * {@link exalge2.Exalge#getNumElements()} のためのテスト・メソッド。
	 */
	public void testGetNumElements() {
		Exalge test0 = new Exalge();
		assertEquals(0, test0.getNumElements());
		
		HashMap<ExBase,BigDecimal> testmap = new HashMap<ExBase,BigDecimal>();
		testmap.put(new ExBase(hatAppleYen), new BigDecimal(20));
		testmap.put(new ExBase(hatOrangeYen), new BigDecimal(40));
		testmap.put(new ExBase(hatBananaYen), new BigDecimal(60));
		testmap.put(new ExBase(nohatCashYen), new BigDecimal(120));
		
		Exalge test1 = new Exalge(testmap);
		assertEquals(4, test1.getNumElements());
	}

	/**
	 * {@link exalge2.Exalge#containsBase(exalge2.ExBase)} のためのテスト・メソッド。
	 */
	public void testContainsBase() {
		LinkedHashMap<ExBase,BigDecimal> testmap = new LinkedHashMap<ExBase,BigDecimal>();
		testmap.put(new ExBase(hatAppleYen), new BigDecimal(20));
		testmap.put(new ExBase(hatOrangeYen), new BigDecimal(40));
		testmap.put(new ExBase(hatBananaYen), new BigDecimal(60));
		testmap.put(new ExBase(nohatCashYen), new BigDecimal(120));
		testmap.put(new ExBase(nohatMelonYen), BigDecimal.ZERO);
		testmap.put(new ExBase(hatMelonYen), BigDecimal.ZERO);
		
		Exalge test1 = new Exalge(testmap);
		assertEquals(6, test1.getNumElements());
		
		assertFalse(test1.containsBase(new ExBase(nohatAppleYen)));
		assertTrue(test1.containsBase(new ExBase(hatAppleYen)));
		assertFalse(test1.containsBase(new ExBase(nohatAppleNum)));
		assertFalse(test1.containsBase(new ExBase(hatAppleNum)));
		assertFalse(test1.containsBase(new ExBase(nohatMelonNum)));
		assertFalse(test1.containsBase(new ExBase(hatMelonNum)));

		assertFalse(test1.containsBase(new ExBase(nohatOrangeYen)));
		assertTrue(test1.containsBase(new ExBase(hatOrangeYen)));
		assertFalse(test1.containsBase(new ExBase(nohatOrangeNum)));
		assertFalse(test1.containsBase(new ExBase(hatOrangeNum)));
		assertTrue(test1.containsBase(new ExBase(nohatMelonYen)));
		assertTrue(test1.containsBase(new ExBase(hatMelonYen)));
	}

	/**
	 * {@link exalge2.Exalge#containsValue(java.math.BigDecimal)} のためのテスト・メソッド。
	 */
	public void testContainsValue() {
		HashMap<ExBase,BigDecimal> testmap = new HashMap<ExBase,BigDecimal>();
		testmap.put(new ExBase(hatAppleYen), new BigDecimal(20));
		testmap.put(new ExBase(hatOrangeYen), new BigDecimal(40));
		testmap.put(new ExBase(hatBananaYen), new BigDecimal(60));
		testmap.put(new ExBase(nohatCashYen), new BigDecimal(120));
		testmap.put(new ExBase(nohatMelonYen), new BigDecimal(0));
		testmap.put(new ExBase(hatMelonYen), new BigDecimal(0));
		
		Exalge test1 = new Exalge(testmap);
		assertEquals(6, test1.getNumElements());
		
		assertFalse(test1.containsValue(new BigDecimal(10)));
		assertTrue(test1.containsValue(new BigDecimal(20)));
		assertFalse(test1.containsValue(new BigDecimal(30)));
		assertTrue(test1.containsValue(new BigDecimal(40)));
		assertFalse(test1.containsValue(new BigDecimal(50)));
		assertTrue(test1.containsValue(new BigDecimal(60)));
		assertFalse(test1.containsValue(new BigDecimal(70)));
		assertFalse(test1.containsValue(new BigDecimal(80)));
		assertFalse(test1.containsValue(new BigDecimal(90)));
		assertFalse(test1.containsValue(new BigDecimal(100)));
		assertTrue(test1.containsValue(BigDecimal.ZERO));
	}

	/**
	 * Test {@link exalge2.Exalge#iterator()}
	 * @since 0.94
	 */
	public void testIterator() {
		final Object[] seqData = new Object[]{
			new ExBase(nohatAppleYen), new BigDecimal(10),
			new ExBase(hatCashYen), new BigDecimal(60),
			new ExBase(nohatOrangeYen), new BigDecimal(20),
			new ExBase(nohatBananaYen), new BigDecimal(30),
			new ExBase(nohatMelonYen), new BigDecimal(0)
		};
		Exalge test = new Exalge(seqData);

		int i = 0;
		Iterator<Exalge> it = test.iterator();
		while (it.hasNext()) {
			Exalge alge = it.next();
			ExBase orgBase = (ExBase)seqData[i++];
			BigDecimal orgVal = (BigDecimal)seqData[i++];
			assertFalse(alge.isEmpty());
			assertEquals(alge.data.size(), 1);
			assertTrue(i <= seqData.length);
			assertEquals(0, alge.get(orgBase).compareTo(orgVal));
			try {
				it.remove();
				fail("Illegal reached here!");
			}
			catch (UnsupportedOperationException ex) {
				assertTrue(true);
			}
			catch (Throwable ex) {
				fail("Unknown exception[" + ex.getMessage() + "]");
			}
			Exalge ignoreAlge = test.put(new ExBase(nohatMelonYen), new BigDecimal(i));
			assertFalse(ignoreAlge == test || ignoreAlge.isSameValues(test));
			ignoreAlge = test.plus(new ExBase(hatMelonYen), new BigDecimal(i));
			assertFalse(ignoreAlge == test || ignoreAlge.isSameValues(test));
		}
		assertEquals(i, seqData.length);
	}

	/**
	 * {@link exalge2.Exalge#isEqualValues(exalge2.Exalge)} のためのテスト・メソッド。
	 */
	public void testIsEqualValues() {
		HashMap<ExBase,BigDecimal> testmap1 = new HashMap<ExBase,BigDecimal>();
		testmap1.put(new ExBase(hatAppleYen), new BigDecimal(20));
		testmap1.put(new ExBase(hatOrangeYen), new BigDecimal(40));
		testmap1.put(new ExBase(hatBananaYen), new BigDecimal(60));
		testmap1.put(new ExBase(nohatCashYen), new BigDecimal(120));
		
		HashMap<ExBase,BigDecimal> testmap2 = new HashMap<ExBase,BigDecimal>();
		testmap2.put(new ExBase(hatAppleYen), new BigDecimal(20));
		testmap2.put(new ExBase(hatOrangeYen), new BigDecimal(30));
		testmap2.put(new ExBase(hatBananaYen), new BigDecimal(60));
		testmap2.put(new ExBase(nohatCashYen), new BigDecimal(110));
		
		HashMap<ExBase,BigDecimal> testmap3 = new HashMap<ExBase,BigDecimal>();
		testmap3.put(new ExBase(hatAppleYen), new BigDecimal(20));
		testmap3.put(new ExBase(hatOrangeYen), new BigDecimal(40));
		testmap3.put(new ExBase(hatBananaYen), new BigDecimal(60));
		testmap3.put(new ExBase(hatCashYen), new BigDecimal(120));

		Exalge test0a = new Exalge();
		Exalge test0b = new Exalge();
		Exalge test1 = new Exalge(testmap1);
		Exalge test2 = new Exalge(testmap2);
		Exalge test3 = new Exalge(testmap1);
		Exalge test4 = test3.put(new ExBase(nohatFruitYen), BigDecimal.ZERO);
		Exalge test5 = new Exalge(testmap3);

		assertTrue(test0a.isEqualValues(test0b));
		assertFalse(test0a.isEqualValues(test1));
		assertFalse(test1.isEqualValues(test0b));
		assertFalse(test2.isEqualValues(test3));
		assertTrue(test3.isEqualValues(test4));
		assertTrue(test3.isEqualValues(test1));
		assertFalse(test1.isEqualValues(test2));
		assertFalse(test2.isEqualValues(test5));
		assertFalse(test5.isEqualValues(test1));
	}

	/**
	 * {@link exalge2.Exalge#isSameValues(exalge2.Exalge)} のためのテスト・メソッド。
	 */
	public void testIsSameValues() {
		HashMap<ExBase,BigDecimal> testmap1 = new HashMap<ExBase,BigDecimal>();
		testmap1.put(new ExBase(hatAppleYen), new BigDecimal(20));
		testmap1.put(new ExBase(hatOrangeYen), new BigDecimal(40));
		testmap1.put(new ExBase(hatBananaYen), new BigDecimal(60));
		testmap1.put(new ExBase(nohatCashYen), new BigDecimal(120));
		
		HashMap<ExBase,BigDecimal> testmap2 = new HashMap<ExBase,BigDecimal>();
		testmap2.put(new ExBase(hatAppleYen), new BigDecimal(20));
		testmap2.put(new ExBase(hatOrangeYen), new BigDecimal(30));
		testmap2.put(new ExBase(hatBananaYen), new BigDecimal(60));
		testmap2.put(new ExBase(nohatCashYen), new BigDecimal(110));
		
		HashMap<ExBase,BigDecimal> testmap3 = new HashMap<ExBase,BigDecimal>();
		testmap3.put(new ExBase(hatAppleYen), new BigDecimal(20));
		testmap3.put(new ExBase(hatOrangeYen), new BigDecimal(40));
		testmap3.put(new ExBase(hatBananaYen), new BigDecimal(60));
		testmap3.put(new ExBase(hatCashYen), new BigDecimal(120));

		Exalge test0a = new Exalge();
		Exalge test0b = new Exalge();
		Exalge test1 = new Exalge(testmap1);
		Exalge test2 = new Exalge(testmap2);
		Exalge test3 = new Exalge(testmap1);
		Exalge test4 = test3.put(new ExBase(nohatFruitYen), BigDecimal.ZERO);
		Exalge test5 = new Exalge(testmap3);

		assertTrue(test0a.isSameValues(test0b));
		assertFalse(test0a.isSameValues(test1));
		assertFalse(test1.isSameValues(test0b));
		assertFalse(test2.isSameValues(test3));
		assertFalse(test3.isSameValues(test4));
		assertTrue(test3.isSameValues(test1));
		assertFalse(test1.isSameValues(test2));
		assertFalse(test2.isSameValues(test5));
		assertFalse(test5.isSameValues(test1));
	}

	/**
	 * {@link exalge2.Exalge#getBases()} のためのテスト・メソッド。
	 */
	public void testGetBases() {
		LinkedHashMap<ExBase,BigDecimal> testmap1 = new LinkedHashMap<ExBase,BigDecimal>();
		testmap1.put(new ExBase(hatAppleYen), new BigDecimal(20));
		testmap1.put(new ExBase(hatOrangeYen), new BigDecimal(40));
		testmap1.put(new ExBase(hatBananaYen), new BigDecimal(60));
		testmap1.put(new ExBase(hatMangoYen), new BigDecimal(0));
		testmap1.put(new ExBase(nohatCashYen), new BigDecimal(120));
		
		ExBase[] seqBases = new ExBase[]{
			new ExBase(hatAppleYen),
			new ExBase(hatOrangeYen),
			new ExBase(hatBananaYen),
			new ExBase(hatMangoYen),
			new ExBase(nohatCashYen)
		};
		
		Exalge test0 = new Exalge();
		ExBaseSet baseSet0 = test0.getBases();
		assertTrue(baseSet0.isEmpty());
		
		Exalge test1 = new Exalge(testmap1);
		ExBaseSet baseSet1 = test1.getBases();
		assertFalse(baseSet1.isEmpty());
		assertTrue(baseSet1.containsAll(testmap1.keySet()));
		assertTrue(baseSet1.containsAll(Arrays.asList(seqBases)));
		assertFalse(baseSet1.contains(new ExBase(hatCashYen)));
		
		int idx = 0;
		Iterator<ExBase> it = baseSet1.iterator();
		while (it.hasNext()) {
			ExBase fBase = it.next();
			ExBase sBase = seqBases[idx++];
			assertTrue(sBase.equals(fBase));
		}
	}

	/**
	 * {@link exalge2.Exalge#get(exalge2.ExBase)} のためのテスト・メソッド。
	 */
	public void testGet() {
		HashMap<ExBase,BigDecimal> testmap1 = new HashMap<ExBase,BigDecimal>();
		testmap1.put(new ExBase(hatAppleYen), new BigDecimal(20));
		testmap1.put(new ExBase(hatOrangeYen), new BigDecimal(40));
		testmap1.put(new ExBase(hatBananaYen), new BigDecimal(60));
		testmap1.put(new ExBase(nohatCashYen), new BigDecimal(120));
		testmap1.put(new ExBase(nohatMelonYen), BigDecimal.ZERO);
		
		Exalge test1 = new Exalge(testmap1);
		
		assertEquals(new BigDecimal(20), test1.get(new ExBase(hatAppleYen)));
		assertEquals(new BigDecimal(40), test1.get(new ExBase(hatOrangeYen)));
		assertEquals(new BigDecimal(60), test1.get(new ExBase(hatBananaYen)));
		assertEquals(new BigDecimal(120), test1.get(new ExBase(nohatCashYen)));
		assertEquals(BigDecimal.ZERO, test1.get(new ExBase(nohatAppleYen)));
		assertEquals(BigDecimal.ZERO, test1.get(new ExBase(hatCashYen)));
		assertEquals(BigDecimal.ZERO, test1.get(new ExBase(nohatMelonYen)));
		assertEquals(BigDecimal.ZERO, test1.get(new ExBase(hatMelonYen)));
	}

	/**
	 * {@link exalge2.Exalge#fastGet(exalge2.ExBase)} のためのテスト・メソッド。
	 */
	public void testFastGet() {
		HashMap<ExBase,BigDecimal> testmap1 = new HashMap<ExBase,BigDecimal>();
		testmap1.put(new ExBase(hatAppleYen), new BigDecimal(20));
		testmap1.put(new ExBase(hatOrangeYen), new BigDecimal(40));
		testmap1.put(new ExBase(hatBananaYen), new BigDecimal(60));
		testmap1.put(new ExBase(nohatCashYen), new BigDecimal(120));
		testmap1.put(new ExBase(nohatMelonYen), BigDecimal.ZERO);
		
		Exalge test1 = new Exalge(testmap1);
		
		assertEquals(new BigDecimal(20), test1.fastGet(new ExBase(hatAppleYen)));
		assertEquals(new BigDecimal(40), test1.fastGet(new ExBase(hatOrangeYen)));
		assertEquals(new BigDecimal(60), test1.fastGet(new ExBase(hatBananaYen)));
		assertEquals(new BigDecimal(120), test1.fastGet(new ExBase(nohatCashYen)));
		assertEquals(BigDecimal.ZERO, test1.fastGet(new ExBase(nohatAppleYen)));
		assertEquals(BigDecimal.ZERO, test1.fastGet(new ExBase(hatCashYen)));
		assertEquals(BigDecimal.ZERO, test1.fastGet(new ExBase(nohatMelonYen)));
		assertEquals(BigDecimal.ZERO, test1.fastGet(new ExBase(hatMelonYen)));
	}

	/**
	 * {@link exalge2.Exalge#put(exalge2.ExBase, java.math.BigDecimal)} のためのテスト・メソッド。
	 */
	public void testPut() {
		Exalge test1 = new Exalge();
		assertTrue(test1.isEmpty());
		
		Exalge test2 = test1.put(new ExBase(nohatFruitYen), new BigDecimal(300));
		assertEquals(1, test2.getNumElements());
		assertTrue(test2.containsBase(new ExBase(nohatFruitYen)));
		assertEquals(new BigDecimal(300), test2.get(new ExBase(nohatFruitYen)));
		
		Exalge test3 = test2.put(new ExBase(nohatFruitYen), new BigDecimal(500));
		assertEquals(1, test3.getNumElements());
		assertTrue(test3.containsBase(new ExBase(nohatFruitYen)));
		assertEquals(new BigDecimal(500), test3.get(new ExBase(nohatFruitYen)));
		
		Exalge test4 = test3.put(new ExBase(hatCashYen), new BigDecimal(500));
		assertEquals(2, test4.getNumElements());
		assertTrue(test4.containsBase(new ExBase(hatCashYen)));
		assertEquals(new BigDecimal(500), test4.get(new ExBase(hatCashYen)));
		
		Exalge test5 = test4.put(new ExBase(nohatExpFruitYen), new BigDecimal(0));
		assertEquals(3, test5.getNumElements());
		assertTrue(test5.containsBase(new ExBase(nohatExpFruitYen)));
		assertEquals(BigDecimal.ZERO, test5.get(new ExBase(nohatExpFruitYen)));
		
		final Object[] seqData = new Object[]{
			new ExBase(nohatFruitYen), new BigDecimal(500),
			new ExBase(hatCashYen), new BigDecimal(500),
			new ExBase(nohatExpFruitYen), new BigDecimal(0)
		};
		
		ExAlgeSetIOTest.checkElemSequence(test5, seqData);
	}

	/**
	 * {@link exalge2.Exalge#normalization()} のためのテスト・メソッド。
	 * @since 0.94
	 */
	public void testNormalization() {
		Exalge test1 = new Exalge();
		assertTrue(test1.isEmpty());
		
		Exalge test2 = test1.normalization();
		assertTrue(test2.isEmpty());
		
		Exalge test3 = new Exalge(new ExBase(hatFruitYen), new BigDecimal(200));
		assertEquals(1, test3.getNumElements());

		Exalge test4 = test3.normalization();
		assertEquals(1, test4.getNumElements());
		
		Exalge test5 = test3.put(new ExBase(nohatCashYen), BigDecimal.ZERO);
		assertEquals(2, test5.getNumElements());
		
		Exalge test6 = test5.normalization();
		assertEquals(1, test6.getNumElements());
		assertTrue(test5.containsBase(new ExBase(nohatCashYen)));
		assertFalse(test6.containsBase(new ExBase(nohatCashYen)));
	}

	/**
	 * {@link exalge2.Exalge#copy()} のためのテスト・メソッド。
	 */
	public void testCopy() {
		final Object[] seqData = new Object[]{
				new ExBase(hatAppleYen),	new BigDecimal(20),
				new ExBase(hatOrangeYen),	new BigDecimal(40),
				new ExBase(hatBananaYen),	new BigDecimal(60),
				new ExBase(hatMangoYen),	new BigDecimal(0),
				new ExBase(nohatCashYen),	new BigDecimal(120)
		};
		
		Exalge test1 = new Exalge(seqData);
		Exalge test2 = test1.copy();
		
		assertFalse(test1 == test2);
		assertTrue(test1.equals(test2));
		assertTrue(test1.isEqualValues(test2));
		assertTrue(test1.isSameValues(test2));
		
		ExAlgeSetIOTest.checkElemSequence(test1, seqData);
		ExAlgeSetIOTest.checkElemSequence(test2, seqData);
	}

	/**
	 * {@link exalge2.Exalge#replaceUnitKey(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testReplaceUnitKey() {
		LinkedHashMap<ExBase,BigDecimal> testmap1 = new LinkedHashMap<ExBase,BigDecimal>();
		testmap1.put(new ExBase(hatAppleYen), new BigDecimal(20));
		testmap1.put(new ExBase(hatAppleNum), new BigDecimal(5));
		testmap1.put(new ExBase(hatOrangeYen), new BigDecimal(40));
		testmap1.put(new ExBase(hatBananaYen), new BigDecimal(60));
		testmap1.put(new ExBase(nohatCashYen), new BigDecimal(120));
		
		final Object[] seqData = new Object[]{
				new ExBase(hatAppleYen.replaceAll("円", "＄")),	new BigDecimal(25),
				new ExBase(hatOrangeYen.replaceAll("円", "＄")),	new BigDecimal(40),
				new ExBase(hatBananaYen.replaceAll("円", "＄")),	new BigDecimal(60),
				new ExBase(nohatCashYen.replaceAll("円", "＄")),	new BigDecimal(120)
		};
		
		final String doll = "＄";
		
		Exalge test1 = new Exalge(testmap1);
		ExBaseSet orgBases = test1.getBases();
		
		Exalge test2 = test1.replaceUnitKey(doll);
		ExBaseSet retBases2 = test2.getBases();
		//assertEquals(orgBases.size(), retBases2.size());
		assertEquals(test1.norm(), test2.norm());
		Iterator<ExBase> it = retBases2.iterator();
		while (it.hasNext()) {
			ExBase exbase = it.next();
			assertFalse(orgBases.contains(exbase));
			assertEquals(doll, exbase.getUnitKey());
		}
		
		ExAlgeSetIOTest.checkElemSequence(test2, seqData);
		
		Exalge test3 = test1.replaceUnitKey(null);
		ExBaseSet retBases3 = test3.getBases();
		//assertEquals(orgBases.size(), retBases3.size());
		assertEquals(test1.norm(), test3.norm());
		it = retBases3.iterator();
		while (it.hasNext()) {
			ExBase exbase = it.next();
			assertFalse(orgBases.contains(exbase));
			assertEquals(ExBase.OMITTED, exbase.getUnitKey());
		}
	}

	/**
	 * {@link exalge2.Exalge#replaceTimeKey(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testReplaceTimeKey() {
		HashMap<ExBase,BigDecimal> testmap1 = new HashMap<ExBase,BigDecimal>();
		testmap1.put(new ExBase(hatAppleYen), new BigDecimal(20));
		testmap1.put(new ExBase(hatOrangeYen), new BigDecimal(40));
		testmap1.put(new ExBase(hatBananaYen), new BigDecimal(60));
		testmap1.put(new ExBase(nohatCashYen), new BigDecimal(120));
		
		final String newTime = "Y2007Q1";
		
		Exalge test1 = new Exalge(testmap1);
		ExBaseSet ret1 = test1.getBases();
		
		Exalge test2 = test1.replaceTimeKey(newTime);
		ExBaseSet ret2 = test2.getBases();
		assertEquals(ret1.size(), ret2.size());
		Iterator<ExBase> it = ret2.iterator();
		while (it.hasNext()) {
			ExBase exbase = it.next();
			assertFalse(ret1.contains(exbase));
			assertEquals(newTime, exbase.getTimeKey());
		}
		
		Exalge test3 = test2.replaceTimeKey(null);
		ExBaseSet ret3 = test3.getBases();
		assertEquals(ret2.size(), ret3.size());
		it = ret3.iterator();
		while (it.hasNext()) {
			ExBase exbase = it.next();
			assertFalse(ret2.contains(exbase));
			assertTrue(ret1.contains(exbase));
			assertEquals(ExBase.OMITTED, exbase.getTimeKey());
		}
	}

	/**
	 * {@link exalge2.Exalge#replaceExtendedKey(exalge2.ExtendedKeyID, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testReplaceExtendedKey() {
		HashMap<ExBase,BigDecimal> testmap1 = new HashMap<ExBase,BigDecimal>();
		testmap1.put(new ExBase(hatAppleYen), new BigDecimal(20));
		testmap1.put(new ExBase(hatOrangeYen), new BigDecimal(40));
		testmap1.put(new ExBase(hatBananaYen), new BigDecimal(60));
		testmap1.put(new ExBase(nohatCashYen), new BigDecimal(120));
		
		final String newKey = "予定";
		ExtendedKeyID[] ids = new ExtendedKeyID[]{
				ExtendedKeyID.UNIT, ExtendedKeyID.TIME, ExtendedKeyID.SUBJECT
		};
		
		Exalge orgAlge = new Exalge(testmap1);
		ExBaseSet orgBases = orgAlge.getBases();
		
		for (int i = 0; i < ids.length; i++) {
			Iterator<ExBase> it;
			Exalge test1 = orgAlge.replaceExtendedKey(ids[i], newKey);
			ExBaseSet ret1 = test1.getBases();
			assertEquals(orgBases.size(), ret1.size());
			it = ret1.iterator();
			while( it.hasNext()) {
				ExBase exbase = it.next();
				assertFalse(orgBases.contains(exbase));
				assertEquals(newKey, exbase.getExtendedKey(ids[i]));
			}
			
			Exalge test2 = test1.replaceExtendedKey(ids[i], null);
			ExBaseSet ret2 = test2.getBases();
			assertEquals(orgBases.size(), ret2.size());
			it = ret2.iterator();
			while (it.hasNext()) {
				ExBase exbase = it.next();
				assertFalse(ret1.contains(exbase));
				assertEquals(ExBase.OMITTED, exbase.getExtendedKey(ids[i]));
			}
		}
	}

	/**
	 * Test {@link exalge2.Exalge#equals(Object)}
	 * @since 0.94
	 */
	public void testEqualsObject() {
		// isSameValue() と同じ挙動であること
		HashMap<ExBase,BigDecimal> testmap1 = new HashMap<ExBase,BigDecimal>();
		testmap1.put(new ExBase(hatAppleYen), new BigDecimal(20));
		testmap1.put(new ExBase(hatOrangeYen), new BigDecimal(40));
		testmap1.put(new ExBase(hatBananaYen), new BigDecimal(60));
		testmap1.put(new ExBase(nohatCashYen), new BigDecimal(120));
		
		HashMap<ExBase,BigDecimal> testmap2 = new HashMap<ExBase,BigDecimal>();
		testmap2.put(new ExBase(hatAppleYen), new BigDecimal(20));
		testmap2.put(new ExBase(hatOrangeYen), new BigDecimal(30));
		testmap2.put(new ExBase(hatBananaYen), new BigDecimal(60));
		testmap2.put(new ExBase(nohatCashYen), new BigDecimal(110));
		
		HashMap<ExBase,BigDecimal> testmap3 = new HashMap<ExBase,BigDecimal>();
		testmap3.put(new ExBase(hatAppleYen), new BigDecimal(20));
		testmap3.put(new ExBase(hatOrangeYen), new BigDecimal(40));
		testmap3.put(new ExBase(hatBananaYen), new BigDecimal(60));
		testmap3.put(new ExBase(hatCashYen), new BigDecimal(120));

		Exalge test0a = new Exalge();
		Exalge test0b = new Exalge();
		Exalge test1 = new Exalge(testmap1);
		Exalge test2 = new Exalge(testmap2);
		Exalge test3 = new Exalge(testmap1);
		Exalge test4 = test3.put(new ExBase(nohatFruitYen), BigDecimal.ZERO);
		Exalge test5 = new Exalge(testmap3);
		Exalge test6 = test4;

		assertTrue(test0a.equals(test0b));
		assertFalse(test0a.equals(test1));
		assertFalse(test1.equals(test0b));
		assertFalse(test2.equals(test3));
		assertFalse(test3.equals(test4));
		assertTrue(test3.equals(test1));
		assertFalse(test1.equals(test2));
		assertFalse(test2.equals(test5));
		assertFalse(test5.equals(test1));
		assertFalse(test6.equals(test3));
		assertTrue(test6.equals(test4));
		assertFalse(test6.equals(null));
		assertFalse(test6.equals(new ExBase(hatAppleYen)));
	}

	/**
	 * Test {@link exalge2.Exalge#hashCode()}
	 * @since 0.94
	 */
	public void testHashCode() {
		HashMap<ExBase,BigDecimal> testmap1 = new HashMap<ExBase,BigDecimal>();
		testmap1.put(new ExBase(hatAppleYen), new BigDecimal("20"));
		testmap1.put(new ExBase(hatOrangeYen), new BigDecimal("40"));
		testmap1.put(new ExBase(hatBananaYen), new BigDecimal("60"));
		testmap1.put(new ExBase(nohatCashYen), new BigDecimal("120"));
		testmap1.put(new ExBase(hatMelonYen), new BigDecimal("0"));
		
		HashMap<ExBase,BigDecimal> testmap2 = new HashMap<ExBase,BigDecimal>();
		testmap2.put(new ExBase(hatAppleYen), new BigDecimal("20.0"));
		testmap2.put(new ExBase(hatOrangeYen), new BigDecimal("40.0"));
		testmap2.put(new ExBase(hatBananaYen), new BigDecimal("60.00"));
		testmap2.put(new ExBase(nohatCashYen), new BigDecimal("120.000"));
		testmap2.put(new ExBase(hatMelonYen), new BigDecimal("0.0"));

		// check ZERO
		BigDecimal zero1 = BigDecimal.ZERO;
		BigDecimal zero2 = new BigDecimal("0");
		BigDecimal zero3 = new BigDecimal(".0");
		BigDecimal zero4 = new BigDecimal("0.0");
		BigDecimal zero5 = new BigDecimal("0.00");
		assertEquals(0, BigDecimal.ZERO.compareTo(zero1));
		assertEquals(0, BigDecimal.ZERO.compareTo(zero2));
		assertEquals(0, BigDecimal.ZERO.compareTo(zero3));
		assertEquals(0, BigDecimal.ZERO.compareTo(zero4));
		assertEquals(0, BigDecimal.ZERO.compareTo(zero5));

		// check ONE
		BigDecimal one1 = BigDecimal.ONE;
		BigDecimal one2 = new BigDecimal("1");
		BigDecimal one3 = new BigDecimal("1.");
		BigDecimal one4 = new BigDecimal("1.0");
		BigDecimal one5 = new BigDecimal("1.00");
		assertEquals(0, BigDecimal.ONE.compareTo(one1));
		assertEquals(0, BigDecimal.ONE.compareTo(one2));
		assertEquals(0, BigDecimal.ONE.compareTo(one3));
		assertEquals(0, BigDecimal.ONE.compareTo(one4));
		assertEquals(0, BigDecimal.ONE.compareTo(one5));
		BigDecimal os1 = one1.stripTrailingZeros();
		BigDecimal os2 = one2.stripTrailingZeros();
		BigDecimal os3 = one3.stripTrailingZeros();
		BigDecimal os4 = one4.stripTrailingZeros();
		BigDecimal os5 = one5.stripTrailingZeros();
		int hos1 = os1.hashCode();
		int hos2 = os2.hashCode();
		int hos3 = os3.hashCode();
		int hos4 = os4.hashCode();
		int hos5 = os5.hashCode();
		assertTrue(hos1 == hos2);
		assertTrue(hos1 == hos3);
		assertTrue(hos1 == hos4);
		assertTrue(hos1 == hos5);

		// check Exalge hashCodes
		Exalge test1a = new Exalge(testmap1);
		Exalge test2a = new Exalge(testmap2);
		Exalge test1b = test1a;
		Exalge test2b = test2a;
		Exalge test1c = test1a.copy();
		Exalge test2c = test2a.copy();
		Exalge test1d = test1a.hat();
		Exalge test2d = test2a.hat();
		
		int hash1a = test1a.hashCode();
		int hash2a = test2a.hashCode();
		int hash1b = test1b.hashCode();
		int hash2b = test2b.hashCode();
		int hash1c = test1c.hashCode();
		int hash2c = test2c.hashCode();
		int hash1d = test1d.hashCode();
		int hash2d = test2d.hashCode();

		assertTrue(hash1a == hash2a);
		assertTrue(hash1b == hash2b);
		assertTrue(hash1c == hash2c);
		assertTrue(hash1d == hash2d);
		
		assertTrue(hash1a == hash1b);
		assertTrue(hash1a == hash1c);
		assertTrue(hash1a != hash1d);
	}

	/**
	 * {@link exalge2.Exalge#toString()} のためのテスト・メソッド。
	 * @since 0.94(Modified)
	 */
	public void testToString() {
		LinkedHashMap<ExBase,BigDecimal> testmap1 = new LinkedHashMap<ExBase,BigDecimal>();
		testmap1.put(new ExBase(hatAppleYen), new BigDecimal(20));
		testmap1.put(new ExBase(hatOrangeYen), new BigDecimal(40));
		testmap1.put(new ExBase(hatBananaYen), new BigDecimal(60));
		testmap1.put(new ExBase(hatMangoYen), new BigDecimal(0));
		testmap1.put(new ExBase(nohatCashYen), new BigDecimal(120));
		
		final String ans1 = "20^<りんご,円,#,#>";
		final String ans2 = "40^<みかん,円,#,#>";
		final String ans3 = "60^<ばなな,円,#,#>";
		final String ans5 = "0^<マンゴー,円,#,#>";
		final String ans4 = "120<現金,円,#,#>";
		final String ans = ans1 + "+" + ans2 + "+" + ans3 + "+" + ans5 + "+" + ans4;
		
		Exalge test1 = new Exalge(testmap1);
		String ret1 = test1.toString();
		
		assertTrue(ret1.indexOf(ans1) >= 0);
		assertTrue(ret1.indexOf(ans2) >= 0);
		assertTrue(ret1.indexOf(ans3) >= 0);
		assertTrue(ret1.indexOf(ans5) >= 0);
		assertTrue(ret1.indexOf(ans4) >= 0);
		assertEquals(ret1, ans);
	}

	/**
	 * {@link exalge2.Exalge#projection(exalge2.ExBase)} のためのテスト・メソッド。
	 */
	public void testProjectionExBase() {
		ExBase base1 = new ExBase(hatAppleYen);
		ExBase base2 = new ExBase(hatOrangeYen);
		ExBase base3 = new ExBase(hatBananaYen);
		ExBase base5 = new ExBase(hatMangoYen);
		ExBase base4 = new ExBase(nohatCashYen);
		LinkedHashMap<ExBase,BigDecimal> testmap1 = new LinkedHashMap<ExBase,BigDecimal>();
		testmap1.put(base1, new BigDecimal(20));
		testmap1.put(base2, new BigDecimal(40));
		testmap1.put(base3, new BigDecimal(60));
		testmap1.put(base5, new BigDecimal(0));
		testmap1.put(base4, new BigDecimal(120));
		
		ExBase data1 = new ExBase(hatAppleYen);
		ExBase data2 = new ExBase(hatOrangeYen);
		ExBase data3 = new ExBase(hatBananaYen);
		ExBase data4 = new ExBase(hatCashYen);

		Exalge ret;
		Exalge test1 = new Exalge(testmap1);

		ret = test1.projection(data1);
		assertFalse(ret.isEmpty());
		assertEquals(1, ret.getNumElements());
		assertTrue(ret.containsBase(base1));
		assertFalse(ret.containsBase(base2));
		assertFalse(ret.containsBase(base3));
		assertFalse(ret.containsBase(base4));
		assertFalse(ret.containsBase(base5));

		ret = test1.projection(data2);
		assertFalse(ret.isEmpty());
		assertEquals(1, ret.getNumElements());
		assertFalse(ret.containsBase(base1));
		assertTrue(ret.containsBase(base2));
		assertFalse(ret.containsBase(base3));
		assertFalse(ret.containsBase(base4));
		assertFalse(ret.containsBase(base5));

		ret = test1.projection(data3);
		assertFalse(ret.isEmpty());
		assertEquals(1, ret.getNumElements());
		assertFalse(ret.containsBase(base1));
		assertFalse(ret.containsBase(base2));
		assertTrue(ret.containsBase(base3));
		assertFalse(ret.containsBase(base4));
		assertFalse(ret.containsBase(base5));

		ret = test1.projection(data4);
		assertTrue(ret.isEmpty());
		assertEquals(0, ret.getNumElements());
		assertFalse(ret.containsBase(base1));
		assertFalse(ret.containsBase(base2));
		assertFalse(ret.containsBase(base3));
		assertFalse(ret.containsBase(base4));
		assertFalse(ret.containsBase(base5));
	}

	/**
	 * {@link exalge2.Exalge#projection(exalge2.ExBaseSet)} のためのテスト・メソッド。
	 */
	public void testProjectionExBaseSet() {
		ExBase base1 = new ExBase(hatAppleYen);
		ExBase base2 = new ExBase(hatOrangeYen);
		ExBase base3 = new ExBase(hatBananaYen);
		ExBase base4 = new ExBase(nohatCashYen);
		ExBase base5 = new ExBase(hatMangoYen);
		
		LinkedHashMap<ExBase,BigDecimal> testmap1 = new LinkedHashMap<ExBase,BigDecimal>();
		testmap1.put(base1, new BigDecimal(20));
		testmap1.put(base2, new BigDecimal(40));
		testmap1.put(base3, new BigDecimal(60));
		testmap1.put(base5, new BigDecimal(0));
		testmap1.put(base4, new BigDecimal(120));

		ExBaseSet data1 = new ExBaseSet();
		data1.add(new ExBase(hatMangoYen));
		data1.add(new ExBase(hatAppleYen));
		data1.add(new ExBase(nohatCashYen));
		
		ExBaseSet data2 = new ExBaseSet();
		data2.add(new ExBase(nohatAppleYen));
		data2.add(new ExBase(hatCashYen));
		
		ExBaseSet data3 = new ExBaseSet();

		Object[] seqData1 = new Object[]{
			base1, new BigDecimal(20),
			base5, new BigDecimal(0),
			base4, new BigDecimal(120)
		};

		Exalge ret;
		Exalge test1 = new Exalge(testmap1);

		ret = test1.projection(data1);
		assertFalse(ret.isEmpty());
		assertEquals(3, ret.getNumElements());
		assertTrue(ret.containsBase(base1));
		assertFalse(ret.containsBase(base2));
		assertFalse(ret.containsBase(base3));
		assertTrue(ret.containsBase(base4));
		assertTrue(ret.containsBase(base5));
		ExAlgeSetIOTest.checkElemSequence(ret, seqData1);
		
		ret = test1.projection(data2);
		assertTrue(ret.isEmpty());
		assertFalse(ret.containsBase(base1));
		assertFalse(ret.containsBase(base2));
		assertFalse(ret.containsBase(base3));
		assertFalse(ret.containsBase(base4));
		assertFalse(ret.containsBase(base5));
		
		ret = test1.projection(data3);
		assertTrue(ret.isEmpty());
		assertFalse(ret.containsBase(base1));
		assertFalse(ret.containsBase(base2));
		assertFalse(ret.containsBase(base3));
		assertFalse(ret.containsBase(base4));
		assertFalse(ret.containsBase(base5));
	}

	/**
	 * Test {@link exalge2.Exalge#patternProjection(ExBase)}
	 * @since 0.94
	 */
	public void testPatternProjectionExBase() {
		ExBase baseHatApple   = new ExBase("りんご", ExBase.HAT   , "円", "Y2007Q1", "貸方1");
		ExBase baseNohatCash1 = new ExBase("現金"  , ExBase.NO_HAT, "円", "Y2007Q1", "借方2");
		ExBase baseHatOrange  = new ExBase("みかん", ExBase.HAT   , "円", "Y2007Q2", "貸方3");
		ExBase baseNohatCash2 = new ExBase("現金"  , ExBase.NO_HAT, "円", "Y2007Q2", "借方4");
		ExBase baseHatBanana  = new ExBase("バナナ", ExBase.HAT   , "円", "Y2007Q3", "貸方5");
		ExBase baseNohatCash3 = new ExBase("現金"  , ExBase.NO_HAT, "円", "Y2007Q3", "借方6");
		ExBase baseHatMelon   = new ExBase("メロン", ExBase.HAT   , "円", "Y2007Q4", "貸方7");
		ExBase baseNohatCash4 = new ExBase("現金"  , ExBase.NO_HAT, "円", "Y2007Q4", "借方8");
		Object[] algeData = new Object[]{
			baseHatApple  , new BigDecimal(20),
			baseNohatCash1, new BigDecimal(20),
			baseHatOrange , new BigDecimal(40),
			baseNohatCash2, new BigDecimal(40),
			baseHatBanana , new BigDecimal(60),
			baseNohatCash3, new BigDecimal(60),
			baseHatMelon  , new BigDecimal(0),
			baseNohatCash4, new BigDecimal(0),
		};
		Exalge alge = new Exalge(algeData);
		
		ExBase pat1 = new ExBase("りんご", ExBase.HAT   , "円", "Y2007Q1", "貸方1");
		ExBase pat2 = new ExBase("りんご", ExBase.NO_HAT, "円", "Y2007Q1", "貸方1");
		ExBase pat3 = new ExBase("現金"  , ExBase.HAT   , "*", "*", "*");
		ExBase pat4 = new ExBase("現金"  , ExBase.NO_HAT, "*", "*", "*");
		ExBase pat5 = new ExBase("*"     , ExBase.HAT   , "*", "Y*Q3", "*");
		ExBase pat6 = new ExBase("*"     , ExBase.NO_HAT, "*", "Y*Q3", "*");
		ExBase pat7 = new ExBase("*"     , ExBase.HAT   , "*", "*", "*");
		ExBase pat8 = new ExBase("*"     , ExBase.NO_HAT, "*", "*", "*");
		ExBase pat9 = new ExBase("マンゴー", ExBase.HAT, "*", "*", "*");
		
		Object[] seqData1 = new Object[]{
			baseHatApple   , new BigDecimal(20),
		};
		Object[] seqData3 = new Object[]{
			baseNohatCash1, new BigDecimal(20),
			baseNohatCash2, new BigDecimal(40),
			baseNohatCash3, new BigDecimal(60),
			baseNohatCash4, new BigDecimal(0),
		};
		Object[] seqData5 = new Object[]{
			baseHatBanana , new BigDecimal(60),
			baseNohatCash3, new BigDecimal(60),
		};
		Object[] seqData7 = new Object[]{
			baseHatApple  , new BigDecimal(20),
			baseNohatCash1, new BigDecimal(20),
			baseHatOrange , new BigDecimal(40),
			baseNohatCash2, new BigDecimal(40),
			baseHatBanana , new BigDecimal(60),
			baseNohatCash3, new BigDecimal(60),
			baseHatMelon  , new BigDecimal(0),
			baseNohatCash4, new BigDecimal(0),
		};
		
		Exalge ret;
		
		// pat1
		ret = alge.patternProjection(pat1);
		assertEquals(seqData1.length/2, ret.getNumElements());
		ExAlgeSetIOTest.checkElemSequence(ret, seqData1);
		// pat2
		ret = alge.patternProjection(pat2);
		assertEquals(seqData1.length/2, ret.getNumElements());
		ExAlgeSetIOTest.checkElemSequence(ret, seqData1);
		// pat3
		ret = alge.patternProjection(pat3);
		assertEquals(seqData3.length/2, ret.getNumElements());
		ExAlgeSetIOTest.checkElemSequence(ret, seqData3);
		// pat4
		ret = alge.patternProjection(pat4);
		assertEquals(seqData3.length/2, ret.getNumElements());
		ExAlgeSetIOTest.checkElemSequence(ret, seqData3);
		// pat5
		ret = alge.patternProjection(pat5);
		assertEquals(seqData5.length/2, ret.getNumElements());
		ExAlgeSetIOTest.checkElemSequence(ret, seqData5);
		// pat6
		ret = alge.patternProjection(pat6);
		assertEquals(seqData5.length/2, ret.getNumElements());
		ExAlgeSetIOTest.checkElemSequence(ret, seqData5);
		// pat7
		ret = alge.patternProjection(pat7);
		assertEquals(seqData7.length/2, ret.getNumElements());
		ExAlgeSetIOTest.checkElemSequence(ret, seqData7);
		// pat8
		ret = alge.patternProjection(pat8);
		assertEquals(seqData7.length/2, ret.getNumElements());
		ExAlgeSetIOTest.checkElemSequence(ret, seqData7);
		// pat9
		ret = alge.patternProjection(pat9);
		assertTrue(ret.isEmpty());
		assertEquals(0, ret.getNumElements());
		
		// pat10
		Exalge alge2 = new Exalge(new ExBase("資産(A)c4",ExBase.NO_HAT, "#","#","（参考）外貨準備"), new BigDecimal(10179));
		ret = alge2.patternProjection(new ExBase("資産(A)c4",ExBase.NO_HAT, "#","#","（参考）外貨準備"));
		System.out.println(ret);
		assertFalse(ret.isEmpty());
		assertEquals(alge2, ret);
		ret = alge2.patternProjection(new ExBase("*",ExBase.NO_HAT, "#","#","（参考）外貨準備"));
		System.out.println(ret);
		assertFalse(ret.isEmpty());
		assertEquals(alge2, ret);
		ret = alge2.patternProjection(new ExBase("資産(A)c4",ExBase.NO_HAT, "#","#","*"));
		System.out.println(ret);
		assertFalse(ret.isEmpty());
		assertEquals(alge2, ret);
	}

	/**
	 * Test {@link exalge2.Exalge#patternProjection(ExBasePattern)}
	 * @since 0.94
	 */
	public void testPatternProjectionExBasePattern() {
		ExBase baseHatApple   = new ExBase("りんご", ExBase.HAT   , "円", "Y2007Q1", "貸方1");
		ExBase baseNohatCash1 = new ExBase("現金"  , ExBase.NO_HAT, "円", "Y2007Q1", "借方2");
		ExBase baseHatOrange  = new ExBase("みかん", ExBase.HAT   , "円", "Y2007Q2", "貸方3");
		ExBase baseNohatCash2 = new ExBase("現金"  , ExBase.NO_HAT, "円", "Y2007Q2", "借方4");
		ExBase baseHatBanana  = new ExBase("バナナ", ExBase.HAT   , "円", "Y2007Q3", "貸方5");
		ExBase baseNohatCash3 = new ExBase("現金"  , ExBase.NO_HAT, "円", "Y2007Q3", "借方6");
		ExBase baseHatMelon   = new ExBase("メロン", ExBase.HAT   , "円", "Y2007Q4", "貸方7");
		ExBase baseNohatCash4 = new ExBase("現金"  , ExBase.NO_HAT, "円", "Y2007Q4", "借方8");
		Object[] algeData = new Object[]{
			baseHatApple  , new BigDecimal(20),
			baseNohatCash1, new BigDecimal(20),
			baseHatOrange , new BigDecimal(40),
			baseNohatCash2, new BigDecimal(40),
			baseHatBanana , new BigDecimal(60),
			baseNohatCash3, new BigDecimal(60),
			baseHatMelon  , new BigDecimal(0),
			baseNohatCash4, new BigDecimal(0),
		};
		Exalge alge = new Exalge(algeData);
		
		ExBasePattern pat1h = new ExBasePattern("りんご", ExBase.HAT   , "円", "Y2007Q1", "貸方1");
		ExBasePattern pat1n = new ExBasePattern("りんご", ExBase.NO_HAT, "円", "Y2007Q1", "貸方1");
		ExBasePattern pat1w = new ExBasePattern("りんご", ExBasePattern.WILDCARD, "円", "Y2007Q1", "貸方1");
		ExBasePattern pat2h = new ExBasePattern("現金"  , ExBase.HAT  , "*", "*", "*");
		ExBasePattern pat2n = new ExBasePattern("現金"  , ExBase.NO_HAT, "*", "*", "*");
		ExBasePattern pat2w = new ExBasePattern("現金"  , ExBasePattern.WILDCARD, "*", "*", "*");
		ExBasePattern pat3h = new ExBasePattern("*"     , ExBase.HAT   , "*", "Y*Q3", "*");
		ExBasePattern pat3n = new ExBasePattern("*"     , ExBase.NO_HAT, "*", "Y*Q3", "*");
		ExBasePattern pat3w = new ExBasePattern("*"     , ExBasePattern.WILDCARD, "*", "Y*Q3", "*");
		ExBasePattern pat4h = new ExBasePattern("*"     , ExBase.HAT   , "*", "*", "*");
		ExBasePattern pat4n = new ExBasePattern("*"     , ExBase.NO_HAT, "*", "*", "*");
		ExBasePattern pat4w = new ExBasePattern("*"     , ExBasePattern.WILDCARD, "*", "*", "*");
		ExBasePattern pat5h = new ExBasePattern("マンゴー", ExBase.HAT   , "*", "*", "*");
		ExBasePattern pat5n = new ExBasePattern("マンゴー", ExBase.NO_HAT, "*", "*", "*");
		ExBasePattern pat5w = new ExBasePattern("マンゴー", ExBasePattern.WILDCARD, "*", "*", "*");
		
		Object[] seqData1 = new Object[]{
			baseHatApple   , new BigDecimal(20),
		};
		Object[] seqData2 = new Object[]{
			baseNohatCash1, new BigDecimal(20),
			baseNohatCash2, new BigDecimal(40),
			baseNohatCash3, new BigDecimal(60),
			baseNohatCash4, new BigDecimal(0),
		};
		Object[] seqData3h = new Object[]{
			baseHatBanana , new BigDecimal(60),
		};
		Object[] seqData3n = new Object[]{
			baseNohatCash3, new BigDecimal(60),
		};
		Object[] seqData3w = new Object[]{
			baseHatBanana , new BigDecimal(60),
			baseNohatCash3, new BigDecimal(60),
		};
		Object[] seqData4h = new Object[]{
			baseHatApple  , new BigDecimal(20),
			baseHatOrange , new BigDecimal(40),
			baseHatBanana , new BigDecimal(60),
			baseHatMelon  , new BigDecimal(0),
		};
		Object[] seqData4n = new Object[]{
			baseNohatCash1, new BigDecimal(20),
			baseNohatCash2, new BigDecimal(40),
			baseNohatCash3, new BigDecimal(60),
			baseNohatCash4, new BigDecimal(0),
		};
		Object[] seqData4w = new Object[]{
			baseHatApple  , new BigDecimal(20),
			baseNohatCash1, new BigDecimal(20),
			baseHatOrange , new BigDecimal(40),
			baseNohatCash2, new BigDecimal(40),
			baseHatBanana , new BigDecimal(60),
			baseNohatCash3, new BigDecimal(60),
			baseHatMelon  , new BigDecimal(0),
			baseNohatCash4, new BigDecimal(0),
		};
		
		Exalge ret;
		
		// pat1
		ret = alge.patternProjection(pat1h);
		assertEquals(seqData1.length/2, ret.getNumElements());
		ExAlgeSetIOTest.checkElemSequence(ret, seqData1);
		ret = alge.patternProjection(pat1n);
		assertTrue(ret.isEmpty());
		assertEquals(0, ret.getNumElements());
		ret = alge.patternProjection(pat1w);
		assertEquals(seqData1.length/2, ret.getNumElements());
		ExAlgeSetIOTest.checkElemSequence(ret, seqData1);
		// pat2
		ret = alge.patternProjection(pat2h);
		assertTrue(ret.isEmpty());
		assertEquals(0, ret.getNumElements());
		ret = alge.patternProjection(pat2n);
		assertEquals(seqData2.length/2, ret.getNumElements());
		ExAlgeSetIOTest.checkElemSequence(ret, seqData2);
		ret = alge.patternProjection(pat2w);
		assertEquals(seqData2.length/2, ret.getNumElements());
		ExAlgeSetIOTest.checkElemSequence(ret, seqData2);
		// pat3
		ret = alge.patternProjection(pat3h);
		assertEquals(seqData3h.length/2, ret.getNumElements());
		ExAlgeSetIOTest.checkElemSequence(ret, seqData3h);
		ret = alge.patternProjection(pat3n);
		assertEquals(seqData3n.length/2, ret.getNumElements());
		ExAlgeSetIOTest.checkElemSequence(ret, seqData3n);
		ret = alge.patternProjection(pat3w);
		assertEquals(seqData3w.length/2, ret.getNumElements());
		ExAlgeSetIOTest.checkElemSequence(ret, seqData3w);
		// pat4
		ret = alge.patternProjection(pat4h);
		assertEquals(seqData4h.length/2, ret.getNumElements());
		ExAlgeSetIOTest.checkElemSequence(ret, seqData4h);
		ret = alge.patternProjection(pat4n);
		assertEquals(seqData4n.length/2, ret.getNumElements());
		ExAlgeSetIOTest.checkElemSequence(ret, seqData4n);
		ret = alge.patternProjection(pat4w);
		assertEquals(seqData4w.length/2, ret.getNumElements());
		ExAlgeSetIOTest.checkElemSequence(ret, seqData4w);
		// pat5
		ret = alge.patternProjection(pat5h);
		assertTrue(ret.isEmpty());
		assertEquals(0, ret.getNumElements());
		ret = alge.patternProjection(pat5n);
		assertTrue(ret.isEmpty());
		assertEquals(0, ret.getNumElements());
		ret = alge.patternProjection(pat5w);
		assertTrue(ret.isEmpty());
		assertEquals(0, ret.getNumElements());
	}

	/**
	 * Test {@link exalge2.Exalge#patternProjection(ExBaseSet)}
	 * @since 0.94
	 */
	public void testPatternProjectionExBaseSet() {
		ExBase baseHatApple   = new ExBase("りんご", ExBase.HAT   , "円", "Y2007Q1", "貸方1");
		ExBase baseNohatCash1 = new ExBase("現金"  , ExBase.NO_HAT, "円", "Y2007Q1", "借方2");
		ExBase baseHatOrange  = new ExBase("みかん", ExBase.HAT   , "円", "Y2007Q2", "貸方3");
		ExBase baseNohatCash2 = new ExBase("現金"  , ExBase.NO_HAT, "円", "Y2007Q2", "借方4");
		ExBase baseHatBanana  = new ExBase("バナナ", ExBase.HAT   , "円", "Y2007Q3", "貸方5");
		ExBase baseNohatCash3 = new ExBase("現金"  , ExBase.NO_HAT, "円", "Y2007Q3", "借方6");
		ExBase baseHatMelon   = new ExBase("メロン", ExBase.HAT   , "円", "Y2007Q4", "貸方7");
		ExBase baseNohatCash4 = new ExBase("現金"  , ExBase.NO_HAT, "円", "Y2007Q4", "借方8");
		Object[] algeData = new Object[]{
			baseHatApple  , new BigDecimal(20),
			baseNohatCash1, new BigDecimal(20),
			baseHatOrange , new BigDecimal(40),
			baseNohatCash2, new BigDecimal(40),
			baseHatBanana , new BigDecimal(60),
			baseNohatCash3, new BigDecimal(60),
			baseHatMelon  , new BigDecimal(0),
			baseNohatCash4, new BigDecimal(0),
		};
		Exalge alge = new Exalge(algeData);
		
		ExBase pat1h = new ExBase("りんご", ExBase.HAT   , "円", "Y2007Q1", "貸方1");
		ExBase pat1n = new ExBase("りんご", ExBase.NO_HAT, "円", "Y2007Q1", "貸方1");
		ExBase pat2h = new ExBase("現金"  , ExBase.HAT  , "*", "*", "*");
		ExBase pat2n = new ExBase("現金"  , ExBase.NO_HAT, "*", "*", "*");
		ExBase pat3h = new ExBase("*"     , ExBase.HAT   , "*", "Y*Q3", "*");
		ExBase pat3n = new ExBase("*"     , ExBase.NO_HAT, "*", "Y*Q3", "*");
		ExBase pat4h = new ExBase("*"     , ExBase.HAT   , "*", "*", "*");
		ExBase pat4n = new ExBase("*"     , ExBase.NO_HAT, "*", "*", "*");
		ExBase pat5h = new ExBase("マンゴー", ExBase.HAT   , "*", "*", "*");
		ExBase pat5n = new ExBase("マンゴー", ExBase.NO_HAT, "*", "*", "*");
		
		ExBaseSet patset1h = new ExBaseSet(Arrays.asList(new ExBase[]{
			pat1h, pat2h, pat3h, pat5h
		}));
		ExBaseSet patset1n = new ExBaseSet(Arrays.asList(new ExBase[]{
			pat1n, pat2n, pat3n, pat5n
		}));
		ExBaseSet patset2h = new ExBaseSet(Arrays.asList(new ExBase[]{
			pat1h, pat2h, pat3h, pat4h, pat5h
		}));
		ExBaseSet patset2n = new ExBaseSet(Arrays.asList(new ExBase[]{
			pat1n, pat2n, pat3n, pat4n, pat5n
		}));
		ExBaseSet patset3h = new ExBaseSet(Arrays.asList(new ExBase[]{
			pat5h
		}));
		ExBaseSet patset3n = new ExBaseSet(Arrays.asList(new ExBase[]{
			pat5n
		}));
		ExBaseSet patset4 = new ExBaseSet();
		
		Object[] seqData1w = new Object[]{
				baseHatApple  , new BigDecimal(20),
				baseNohatCash1, new BigDecimal(20),
				baseNohatCash2, new BigDecimal(40),
				baseHatBanana , new BigDecimal(60),
				baseNohatCash3, new BigDecimal(60),
				baseNohatCash4, new BigDecimal(0),
		};
		Object[] seqData2w = new Object[]{
				baseHatApple  , new BigDecimal(20),
				baseNohatCash1, new BigDecimal(20),
				baseHatOrange , new BigDecimal(40),
				baseNohatCash2, new BigDecimal(40),
				baseHatBanana , new BigDecimal(60),
				baseNohatCash3, new BigDecimal(60),
				baseHatMelon  , new BigDecimal(0),
				baseNohatCash4, new BigDecimal(0),
		};
		
		Exalge ret;
		
		// pat1
		ret = alge.patternProjection(patset1h);
		assertEquals(seqData1w.length/2, ret.getNumElements());
		ExAlgeSetIOTest.checkElemSequence(ret, seqData1w);
		ret = alge.patternProjection(patset1n);
		assertEquals(seqData1w.length/2, ret.getNumElements());
		ExAlgeSetIOTest.checkElemSequence(ret, seqData1w);
		// pat2
		ret = alge.patternProjection(patset2h);
		assertEquals(seqData2w.length/2, ret.getNumElements());
		ExAlgeSetIOTest.checkElemSequence(ret, seqData2w);
		ret = alge.patternProjection(patset2n);
		assertEquals(seqData2w.length/2, ret.getNumElements());
		ExAlgeSetIOTest.checkElemSequence(ret, seqData2w);
		// pat3
		ret = alge.patternProjection(patset3h);
		assertTrue(ret.isEmpty());
		assertEquals(0, ret.getNumElements());
		ret = alge.patternProjection(patset3n);
		assertTrue(ret.isEmpty());
		assertEquals(0, ret.getNumElements());
		// pat4
		ret = alge.patternProjection(patset4);
		assertTrue(ret.isEmpty());
		assertEquals(0, ret.getNumElements());
	}

	/**
	 * Test {@link exalge2.Exalge#patternProjection(ExBasePatternSet)}
	 * @since 0.94
	 */
	public void testPatternProjectionExBasePatternSet() {
		ExBase baseHatApple   = new ExBase("りんご", ExBase.HAT   , "円", "Y2007Q1", "貸方1");
		ExBase baseNohatCash1 = new ExBase("現金"  , ExBase.NO_HAT, "円", "Y2007Q1", "借方2");
		ExBase baseHatOrange  = new ExBase("みかん", ExBase.HAT   , "円", "Y2007Q2", "貸方3");
		ExBase baseNohatCash2 = new ExBase("現金"  , ExBase.NO_HAT, "円", "Y2007Q2", "借方4");
		ExBase baseHatBanana  = new ExBase("バナナ", ExBase.HAT   , "円", "Y2007Q3", "貸方5");
		ExBase baseNohatCash3 = new ExBase("現金"  , ExBase.NO_HAT, "円", "Y2007Q3", "借方6");
		ExBase baseHatMelon   = new ExBase("メロン", ExBase.HAT   , "円", "Y2007Q4", "貸方7");
		ExBase baseNohatCash4 = new ExBase("現金"  , ExBase.NO_HAT, "円", "Y2007Q4", "借方8");
		Object[] algeData = new Object[]{
			baseHatApple  , new BigDecimal(20),
			baseNohatCash1, new BigDecimal(20),
			baseHatOrange , new BigDecimal(40),
			baseNohatCash2, new BigDecimal(40),
			baseHatBanana , new BigDecimal(60),
			baseNohatCash3, new BigDecimal(60),
			baseHatMelon  , new BigDecimal(0),
			baseNohatCash4, new BigDecimal(0),
		};
		Exalge alge = new Exalge(algeData);
		
		ExBasePattern pat1h = new ExBasePattern("りんご", ExBase.HAT   , "円", "Y2007Q1", "貸方1");
		ExBasePattern pat1n = new ExBasePattern("りんご", ExBase.NO_HAT, "円", "Y2007Q1", "貸方1");
		ExBasePattern pat1w = new ExBasePattern("りんご", ExBasePattern.WILDCARD, "円", "Y2007Q1", "貸方1");
		ExBasePattern pat2h = new ExBasePattern("現金"  , ExBase.HAT  , "*", "*", "*");
		ExBasePattern pat2n = new ExBasePattern("現金"  , ExBase.NO_HAT, "*", "*", "*");
		ExBasePattern pat2w = new ExBasePattern("現金"  , ExBasePattern.WILDCARD, "*", "*", "*");
		ExBasePattern pat3h = new ExBasePattern("*"     , ExBase.HAT   , "*", "Y*Q3", "*");
		ExBasePattern pat3n = new ExBasePattern("*"     , ExBase.NO_HAT, "*", "Y*Q3", "*");
		ExBasePattern pat3w = new ExBasePattern("*"     , ExBasePattern.WILDCARD, "*", "Y*Q3", "*");
		ExBasePattern pat4h = new ExBasePattern("*"     , ExBase.HAT   , "*", "*", "*");
		ExBasePattern pat4n = new ExBasePattern("*"     , ExBase.NO_HAT, "*", "*", "*");
		ExBasePattern pat4w = new ExBasePattern("*"     , ExBasePattern.WILDCARD, "*", "*", "*");
		ExBasePattern pat5h = new ExBasePattern("マンゴー", ExBase.HAT   , "*", "*", "*");
		ExBasePattern pat5n = new ExBasePattern("マンゴー", ExBase.NO_HAT, "*", "*", "*");
		ExBasePattern pat5w = new ExBasePattern("マンゴー", ExBasePattern.WILDCARD, "*", "*", "*");
		
		ExBasePatternSet patset1h = new ExBasePatternSet(Arrays.asList(new ExBasePattern[]{
			pat1h, pat2h, pat3h, pat5h
		}));
		ExBasePatternSet patset1n = new ExBasePatternSet(Arrays.asList(new ExBasePattern[]{
			pat1n, pat2n, pat3n, pat5n
		}));
		ExBasePatternSet patset1w = new ExBasePatternSet(Arrays.asList(new ExBasePattern[]{
			pat1w, pat2w, pat3w, pat5w
		}));
		ExBasePatternSet patset2h = new ExBasePatternSet(Arrays.asList(new ExBasePattern[]{
			pat1h, pat2h, pat3h, pat4h, pat5h
		}));
		ExBasePatternSet patset2n = new ExBasePatternSet(Arrays.asList(new ExBasePattern[]{
			pat1n, pat2n, pat3n, pat4n, pat5n
		}));
		ExBasePatternSet patset2w = new ExBasePatternSet(Arrays.asList(new ExBasePattern[]{
			pat1w, pat2w, pat3w, pat4w, pat5w
		}));
		ExBasePatternSet patset3h = new ExBasePatternSet(Arrays.asList(new ExBasePattern[]{
			pat5h
		}));
		ExBasePatternSet patset3n = new ExBasePatternSet(Arrays.asList(new ExBasePattern[]{
			pat5n
		}));
		ExBasePatternSet patset3w = new ExBasePatternSet(Arrays.asList(new ExBasePattern[]{
			pat5w
		}));
		ExBasePatternSet patset4 = new ExBasePatternSet();
		
		Object[] seqData1h = new Object[]{
				baseHatApple  , new BigDecimal(20),
				baseHatBanana , new BigDecimal(60),
		};
		Object[] seqData1n = new Object[]{
				baseNohatCash1, new BigDecimal(20),
				baseNohatCash2, new BigDecimal(40),
				baseNohatCash3, new BigDecimal(60),
				baseNohatCash4, new BigDecimal(0),
		};
		Object[] seqData1w = new Object[]{
				baseHatApple  , new BigDecimal(20),
				baseNohatCash1, new BigDecimal(20),
				baseNohatCash2, new BigDecimal(40),
				baseHatBanana , new BigDecimal(60),
				baseNohatCash3, new BigDecimal(60),
				baseNohatCash4, new BigDecimal(0),
		};
		Object[] seqData2h = new Object[]{
				baseHatApple  , new BigDecimal(20),
				baseHatOrange , new BigDecimal(40),
				baseHatBanana , new BigDecimal(60),
				baseHatMelon  , new BigDecimal(0),
		};
		Object[] seqData2n = new Object[]{
				baseNohatCash1, new BigDecimal(20),
				baseNohatCash2, new BigDecimal(40),
				baseNohatCash3, new BigDecimal(60),
				baseNohatCash4, new BigDecimal(0),
		};
		Object[] seqData2w = new Object[]{
				baseHatApple  , new BigDecimal(20),
				baseNohatCash1, new BigDecimal(20),
				baseHatOrange , new BigDecimal(40),
				baseNohatCash2, new BigDecimal(40),
				baseHatBanana , new BigDecimal(60),
				baseNohatCash3, new BigDecimal(60),
				baseHatMelon  , new BigDecimal(0),
				baseNohatCash4, new BigDecimal(0),
		};
		
		Exalge ret;
		
		// pat1
		ret = alge.patternProjection(patset1h);
		assertEquals(seqData1h.length/2, ret.getNumElements());
		ExAlgeSetIOTest.checkElemSequence(ret, seqData1h);
		ret = alge.patternProjection(patset1n);
		assertEquals(seqData1n.length/2, ret.getNumElements());
		ExAlgeSetIOTest.checkElemSequence(ret, seqData1n);
		ret = alge.patternProjection(patset1w);
		assertEquals(seqData1w.length/2, ret.getNumElements());
		ExAlgeSetIOTest.checkElemSequence(ret, seqData1w);
		// pat2
		ret = alge.patternProjection(patset2h);
		assertEquals(seqData2h.length/2, ret.getNumElements());
		ExAlgeSetIOTest.checkElemSequence(ret, seqData2h);
		ret = alge.patternProjection(patset2n);
		assertEquals(seqData2n.length/2, ret.getNumElements());
		ExAlgeSetIOTest.checkElemSequence(ret, seqData2n);
		ret = alge.patternProjection(patset2w);
		assertEquals(seqData2w.length/2, ret.getNumElements());
		ExAlgeSetIOTest.checkElemSequence(ret, seqData2w);
		// pat3
		ret = alge.patternProjection(patset3h);
		assertTrue(ret.isEmpty());
		assertEquals(0, ret.getNumElements());
		ret = alge.patternProjection(patset3n);
		assertTrue(ret.isEmpty());
		assertEquals(0, ret.getNumElements());
		ret = alge.patternProjection(patset3w);
		assertTrue(ret.isEmpty());
		assertEquals(0, ret.getNumElements());
		// pat4
		ret = alge.patternProjection(patset4);
		assertTrue(ret.isEmpty());
		assertEquals(0, ret.getNumElements());
	}

	/**
	 * {@link exalge2.Exalge#plus(exalge2.ExBase, java.math.BigDecimal)} のためのテスト・メソッド。
	 */
	public void testPlusExBaseBigDecimal() {
		Exalge test1 = new Exalge();
		assertTrue(test1.isEmpty());
		
		Exalge test2 = test1.put(new ExBase(nohatAppleYen), new BigDecimal(20));
		assertEquals(new BigDecimal(20), test2.get(new ExBase(nohatAppleYen)));
		
		Exalge test3 = test2.plus(new ExBase(nohatAppleYen), new BigDecimal(20));
		assertEquals(new BigDecimal(40), test3.get(new ExBase(nohatAppleYen)));
		
		Exalge test4 = test3.plus(new ExBase(nohatAppleYen), BigDecimal.ZERO);
		assertEquals(new BigDecimal(40), test4.get(new ExBase(nohatAppleYen)));
		
		Exalge test5 = test4.plus(new ExBase(hatCashYen), new BigDecimal(30));
		assertEquals(new BigDecimal(40), test5.get(new ExBase(nohatAppleYen)));
		assertEquals(new BigDecimal(30), test5.get(new ExBase(hatCashYen)));
	}

	/**
	 * {@link exalge2.Exalge#add(ExBase, BigDecimal)} のためのテスト・メソッド。
	 * @since 0.984
	 */
	public void testAddExBaseBigDecimal() {
		Exalge test1 = new Exalge();
		assertTrue(test1.isEmpty());
		
		Exalge test2 = test1.put(new ExBase(nohatAppleYen), new BigDecimal(20));
		assertEquals(new BigDecimal(20), test2.get(new ExBase(nohatAppleYen)));
		
		Exalge test3 = test2.plus(new ExBase(nohatAppleYen), new BigDecimal(20));
		assertEquals(new BigDecimal(40), test3.get(new ExBase(nohatAppleYen)));
		
		Exalge test4 = test3.plus(new ExBase(nohatAppleYen), BigDecimal.ZERO);
		assertEquals(new BigDecimal(40), test4.get(new ExBase(nohatAppleYen)));
		
		Exalge test5 = test4.plus(new ExBase(hatCashYen), new BigDecimal(30));
		assertEquals(new BigDecimal(40), test5.get(new ExBase(nohatAppleYen)));
		assertEquals(new BigDecimal(30), test5.get(new ExBase(hatCashYen)));
		
		//--- add
		
		Exalge ret1 = new Exalge();
		assertTrue(ret1.isEmpty());
		Exalge ret2 = ret1.add(new ExBase(nohatAppleYen), new BigDecimal(20));
		assertSame(ret2, ret1);
		assertEquals(ret2, test2);
		
		Exalge ret3 = ret2.add(new ExBase(nohatAppleYen), new BigDecimal(20));
		assertSame(ret3, ret2);
		assertEquals(ret3, test3);
		
		Exalge ret4 = ret3.add(new ExBase(nohatAppleYen), BigDecimal.ZERO);
		assertSame(ret4, ret3);
		assertEquals(ret4, test4);
		
		Exalge ret5 = ret4.add(new ExBase(hatCashYen), new BigDecimal(30));
		assertSame(ret5, ret4);
		assertEquals(ret5, test5);
	}

	/**
	 * {@link exalge2.Exalge#plus(exalge2.Exalge)} のためのテスト・メソッド。
	 */
	public void testPlusExalge() {
		ExBase base1 = new ExBase(hatAppleYen);
		ExBase base2 = new ExBase(hatOrangeYen);
		ExBase base3 = new ExBase(hatBananaYen);
		ExBase base4 = new ExBase(nohatCashYen);
		ExBase base5 = new ExBase(hatMangoYen);
		
		LinkedHashMap<ExBase,BigDecimal> testmap1 = new LinkedHashMap<ExBase,BigDecimal>();
		testmap1.put(base1, new BigDecimal(20));
		testmap1.put(base2, new BigDecimal(40));
		testmap1.put(base3, new BigDecimal(60));
		testmap1.put(base4, new BigDecimal(120));
		
		LinkedHashMap<ExBase,BigDecimal> testmap2 = new LinkedHashMap<ExBase,BigDecimal>();
		testmap2.put(base1, new BigDecimal(5));
		testmap2.put(base2, BigDecimal.ZERO);
		testmap2.put(base3, BigDecimal.ZERO);
		testmap2.put(base5, new BigDecimal(0));
		testmap2.put(base4, new BigDecimal(15));
		
		Exalge test1 = new Exalge(testmap1);
		Exalge test2 = new Exalge(testmap2);
		Exalge test3 = test1.plus(test2);
		
		assertEquals(new BigDecimal(25), test3.get(base1));
		assertEquals(new BigDecimal(40), test3.get(base2));
		assertEquals(new BigDecimal(60), test3.get(base3));
		assertEquals(new BigDecimal(135), test3.get(base4));
		assertTrue(test3.containsBase(base5));
		assertEquals(0, test3.get(base5).compareTo(BigDecimal.ZERO));
		
		Object[] seqData = new Object[]{
			base1,	new BigDecimal(25),
			base2,	new BigDecimal(40),
			base3,	new BigDecimal(60),
			base4,	new BigDecimal(135),
			base5,	BigDecimal.ZERO,
		};
		ExAlgeSetIOTest.checkElemSequence(test3, seqData);
	}

	/**
	 * {@link exalge2.Exalge#add(Exalge)} のためのテスト・メソッド。
	 */
	public void testAddExalge() {
		ExBase base1 = new ExBase(hatAppleYen);
		ExBase base2 = new ExBase(hatOrangeYen);
		ExBase base3 = new ExBase(hatBananaYen);
		ExBase base4 = new ExBase(nohatCashYen);
		ExBase base5 = new ExBase(hatMangoYen);
		
		LinkedHashMap<ExBase,BigDecimal> testmap1 = new LinkedHashMap<ExBase,BigDecimal>();
		testmap1.put(base1, new BigDecimal(20));
		testmap1.put(base2, new BigDecimal(40));
		testmap1.put(base3, new BigDecimal(60));
		testmap1.put(base4, new BigDecimal(120));
		
		LinkedHashMap<ExBase,BigDecimal> testmap2 = new LinkedHashMap<ExBase,BigDecimal>();
		testmap2.put(base1, new BigDecimal(5));
		testmap2.put(base2, BigDecimal.ZERO);
		testmap2.put(base3, BigDecimal.ZERO);
		testmap2.put(base5, new BigDecimal(0));
		testmap2.put(base4, new BigDecimal(15));
		
		Exalge test1 = new Exalge(testmap1);
		Exalge test2 = new Exalge(testmap2);
		Exalge test3 = test1.plus(test2);
		
		assertEquals(new BigDecimal(25), test3.get(base1));
		assertEquals(new BigDecimal(40), test3.get(base2));
		assertEquals(new BigDecimal(60), test3.get(base3));
		assertEquals(new BigDecimal(135), test3.get(base4));
		assertTrue(test3.containsBase(base5));
		assertEquals(0, test3.get(base5).compareTo(BigDecimal.ZERO));
		
		//--- add
		
		Exalge ret1 = new Exalge();
		assertTrue(ret1.isEmpty());
		
		Exalge ret2 = ret1.add(test1);
		assertSame(ret2, ret1);
		assertEquals(ret2, test1);
		
		Exalge ret3 = ret2.add(test2);
		assertSame(ret3, ret2);
		assertEquals(ret3, test3);
	}

	/**
	 * {@link exalge2.Exalge#multiple(java.math.BigDecimal)} のためのテスト・メソッド。
	 */
	public void testMultipleBigDecimal() {
		ExBase base1 = new ExBase(hatAppleYen);
		ExBase base2 = new ExBase(hatOrangeYen);
		ExBase base3 = new ExBase(hatBananaYen);
		ExBase base4 = new ExBase(nohatCashYen);
		
		LinkedHashMap<ExBase,BigDecimal> testmap1 = new LinkedHashMap<ExBase,BigDecimal>();
		testmap1.put(base1, new BigDecimal(20));
		testmap1.put(base2, new BigDecimal(40));
		testmap1.put(base3, new BigDecimal(60));
		testmap1.put(base4, new BigDecimal(120));
		
		Object[] seqData1 = new Object[]{
			base1,	new BigDecimal(100),
			base2,	new BigDecimal(200),
			base3,	new BigDecimal(300),
			base4,	new BigDecimal(600),
		};
		Object[] seqData2 = new Object[]{
			base1,	new BigDecimal(0),
			base2,	new BigDecimal(0),
			base3,	new BigDecimal(0),
			base4,	new BigDecimal(0),
		};
		
		Exalge test1 = new Exalge(testmap1);

		Exalge ret1 = test1.multiple(new BigDecimal(5));
		assertEquals(new BigDecimal(100), ret1.get(base1));
		assertEquals(new BigDecimal(200), ret1.get(base2));
		assertEquals(new BigDecimal(300), ret1.get(base3));
		assertEquals(new BigDecimal(600), ret1.get(base4));
		ExAlgeSetIOTest.checkElemSequence(ret1, seqData1);
		
		Exalge ret2 = test1.multiple(BigDecimal.ZERO);
		assertEquals(BigDecimal.ZERO, ret2.get(base1));
		assertEquals(BigDecimal.ZERO, ret2.get(base2));
		assertEquals(BigDecimal.ZERO, ret2.get(base3));
		assertEquals(BigDecimal.ZERO, ret2.get(base4));
		assertTrue(ret2.containsBase(base1));
		assertTrue(ret2.containsBase(base2));
		assertTrue(ret2.containsBase(base3));
		assertTrue(ret2.containsBase(base4));
		ExAlgeSetIOTest.checkElemSequence(ret2, seqData2);
		
		try {
			Exalge ret3 = test1.multiple(new BigDecimal(-1));
			super.fail("Exception not occurred");
			ret3.isEmpty();
		}
		catch (ArithmeticException ex) {
			// OK
		}
	}

	/**
	 * {@link exalge2.Exalge#divide(java.math.BigDecimal)} のためのテスト・メソッド。
	 */
	public void testDivide() {
		ExBase base1 = new ExBase(hatAppleYen);
		ExBase base2 = new ExBase(hatOrangeYen);
		ExBase base3 = new ExBase(hatBananaYen);
		ExBase base4 = new ExBase(nohatCashYen);
		ExBase base5 = new ExBase(hatMangoYen);
		
		//BigDecimal zero = BigDecimal.ZERO;
		//BigDecimal bmin = new BigDecimal("0.00000000001");
		//BigDecimal val = zero.divide(bmin, java.math.MathContext.DECIMAL128);
		//BigDecimal sval = val.stripTrailingZeros();
		//BigDecimal mval = bmin.subtract(bmin);
		//String strVal = val.toPlainString();
		//String strSVal = sval.toPlainString();
		//String strMVal = mval.toPlainString();
		
		LinkedHashMap<ExBase,BigDecimal> testmap1 = new LinkedHashMap<ExBase,BigDecimal>();
		testmap1.put(base1, new BigDecimal(20));
		testmap1.put(base2, new BigDecimal(40));
		testmap1.put(base3, new BigDecimal(60));
		testmap1.put(base5, new BigDecimal(0));
		testmap1.put(base4, new BigDecimal(120));
		Object[] seqData1 = new Object[]{
			base1,	new BigDecimal(4),
			base2,	new BigDecimal(8),
			base3,	new BigDecimal(12),
			base5,	BigDecimal.ZERO,
			base4,	new BigDecimal(24),
		};
		
		Exalge test1 = new Exalge(testmap1);

		Exalge ret1 = test1.divide(new BigDecimal(5));
		assertEquals(new BigDecimal(4), ret1.get(base1));
		assertEquals(new BigDecimal(8), ret1.get(base2));
		assertEquals(new BigDecimal(12), ret1.get(base3));
		assertEquals(new BigDecimal(24), ret1.get(base4));
		assertEquals(0, ret1.get(base5).compareTo(BigDecimal.ZERO));
		ExAlgeSetIOTest.checkElemSequence(ret1, seqData1);

		try {
			Exalge ret2 = test1.divide(BigDecimal.ZERO);
			super.fail("Exception not occurred at Divide by ZERO");
			ret2.isEmpty();
		}
		catch (ArithmeticException ex) {
			// OK!
		}
		
		try {
			Exalge ret3 = test1.divide(new BigDecimal(-1));
			super.fail("Exception not occurred at Divide by minus");
			ret3.isEmpty();
		}
		catch (ArithmeticException ex) {
			// OK!
		}
	}

	/**
	 * {@link exalge2.Exalge#elementMultiple(exalge2.Exalge)} のためのテスト・メソッド。
	 */
	public void testElementMultipleExalge() {
		final String nohatAppleNum0A = "りんご-NO_HAT-個-Y2000-A";
		final String nohatOrangeNum0A = "みかん-NO_HAT-個-Y2000-A";
		final String nohatBananaNum0A = "ばなな-NO_HAT-個-Y2000-A";
		final String nohatMelonNum0A = "メロン-NO_HAT-個-Y2000-A";
		final String nohatAppleYen0A = "りんご-NO_HAT-円-Y2000-A";
		//final String nohatAppleYen1A = "りんご-NO_HAT-円-Y2001-A";
		//final String nohatAppleYen0B = "りんご-NO_HAT-円-Y2000-B";
		final String nohatAppleYen1B = "りんご-NO_HAT-円-Y2001-B";
		final String nohatOrangeYen0A = "みかん-NO_HAT-円-Y2000-A";
		final String nohatOrangeYen1A = "みかん-NO_HAT-円-Y2001-A";
		//final String nohatOrangeYen0B = "みかん-NO_HAT-円-Y2000-B";
		final String nohatOrangeYen1B = "みかん-NO_HAT-円-Y2001-B";
		final String nohatBananaYen0A = "ばなな-NO_HAT-円-Y2000-A";
		//final String nohatBananaYen1A = "ばなな-NO_HAT-円-Y2001-A";
		final String nohatBananaYen0B = "ばなな-NO_HAT-円-Y2000-B";
		final String nohatBananaYen1B = "ばなな-NO_HAT-円-Y2001-B";
		//final String nohatMelonYen0A = "メロン-NO_HAT-円-Y2000-A";
		//final String nohatMelonYen1A = "メロン-NO_HAT-円-Y2001-A";
		//final String nohatMelonYen0B = "メロン-NO_HAT-円-Y2000-B";
		final String nohatMelonYen1B = "メロン-NO_HAT-円-Y2001-B";
		
		Exalge data1 = new Exalge(new Object[]{
				new ExBase(nohatAppleNum0A),	new BigDecimal(10),
				new ExBase(nohatOrangeNum0A),	new BigDecimal(20),
				new ExBase(nohatBananaNum0A),	new BigDecimal(30),
				new ExBase(nohatMelonNum0A),	new BigDecimal(0),
		});
		
		Exalge data2 = new Exalge(new Object[]{
				new ExBase(nohatAppleYen0A),	new BigDecimal(50),
				new ExBase(nohatOrangeYen0A),	new BigDecimal(60),
				new ExBase(nohatBananaYen0A),	new BigDecimal(70)
		});
		
		Exalge data3 = new Exalge(new Object[]{
				new ExBase(nohatAppleYen1B),	new BigDecimal(4),
				new ExBase(nohatOrangeYen1B),	new BigDecimal(5),
				new ExBase(nohatBananaYen1B),	new BigDecimal(6),
				new ExBase(nohatMelonYen1B),	new BigDecimal(400)
		});
		
		Exalge data4 = new Exalge(new Object[]{
				new ExBase(nohatAppleYen0A),	new BigDecimal(100),
				new ExBase(nohatOrangeYen1A),	new BigDecimal(200),
				new ExBase(nohatBananaYen0B),	new BigDecimal(300)
		});
		
		Exalge ret1 = data1.elementMultiple(data2);
		assertEquals(new BigDecimal(500), ret1.get(new ExBase("りんご-NO_HAT-#-Y2000-A")));
		assertEquals(new BigDecimal(1200), ret1.get(new ExBase("みかん-NO_HAT-#-Y2000-A")));
		assertEquals(new BigDecimal(2100), ret1.get(new ExBase("ばなな-NO_HAT-#-Y2000-A")));
		assertFalse(ret1.containsBase(new ExBase("メロン-NO_HAT-#-Y2000-A")));
		assertEquals(BigDecimal.ZERO, ret1.get(new ExBase("メロン-NO_HAT-#-Y2000-A")));
		
		Exalge ret2 = data1.elementMultiple(data3);
		assertEquals(new BigDecimal(40), ret2.get(new ExBase("りんご-NO_HAT-#-#-#")));
		assertEquals(new BigDecimal(100), ret2.get(new ExBase("みかん-NO_HAT-#-#-#")));
		assertEquals(new BigDecimal(180), ret2.get(new ExBase("ばなな-NO_HAT-#-#-#")));
		assertTrue(ret2.containsBase(new ExBase("メロン-NO_HAT-#-#-#")));
		assertEquals(BigDecimal.ZERO, ret2.get(new ExBase("メロン-NO_HAT-#-#-#")));
		
		try {
			Exalge ret3 = data1.elementMultiple(data4);
			super.fail("Exception not occurred at elements dimension not same");
			ret3.isEmpty();
		}
		catch (ArithmeticException ex) {
			// OK!
		}
	}

	/**
	 * {@link exalge2.Exalge#elementMultiple(exalge2.Exalge, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testElementMultipleExalgeString() {
		final String nohatAppleNum0A = "りんご-NO_HAT-個-Y2000-A";
		final String nohatOrangeNum0A = "みかん-NO_HAT-個-Y2000-A";
		final String nohatBananaNum0A = "ばなな-NO_HAT-個-Y2000-A";
		final String nohatMelonNum0A = "めろん-NO_HAT-個-Y2000-A";
		final String nohatAppleYen1B = "りんご-NO_HAT-円-Y2001-B";
		final String nohatOrangeYen1B = "みかん-NO_HAT-円-Y2001-B";
		final String nohatBananaYen1B = "ばなな-NO_HAT-円-Y2001-B";
		
		Exalge data1 = new Exalge(new Object[]{
				new ExBase(nohatAppleNum0A),	new BigDecimal(10),
				new ExBase(nohatOrangeNum0A),	new BigDecimal(20),
				new ExBase(nohatBananaNum0A),	new BigDecimal(30),
				new ExBase(nohatMelonNum0A),	new BigDecimal(40),
		});
		
		Exalge data2 = new Exalge(new Object[]{
				new ExBase(nohatAppleYen1B),	new BigDecimal(4),
				new ExBase(nohatOrangeYen1B),	new BigDecimal(5),
				new ExBase(nohatBananaYen1B),	new BigDecimal(6)
		});
		
		Exalge ret2 = data1.elementMultiple(data2, "価額");
		assertEquals(new BigDecimal(40), ret2.get(new ExBase("りんご-NO_HAT-価額-#-#")));
		assertEquals(new BigDecimal(100), ret2.get(new ExBase("みかん-NO_HAT-価額-#-#")));
		assertEquals(new BigDecimal(180), ret2.get(new ExBase("ばなな-NO_HAT-価額-#-#")));
		assertEquals(BigDecimal.ZERO, ret2.get(new ExBase("めろん-NO_HAT-価額-#-#")));
	}

	/**
	 * {@link exalge2.Exalge#elementMultiple(exalge2.Exalge, exalge2.ExtendedKeyID, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testElementMultipleExalgeExtendedKeyIDString() {
		final String nohatAppleNum0A = "りんご-NO_HAT-個-Y2000-A";
		final String nohatOrangeNum0A = "みかん-NO_HAT-個-Y2000-A";
		final String nohatBananaNum0A = "ばなな-NO_HAT-個-Y2000-A";
		final String nohatMelonNum0A = "めろん-NO_HAT-個-Y2000-A";
		final String nohatAppleYen1B = "りんご-NO_HAT-円-Y2001-B";
		final String nohatOrangeYen1B = "みかん-NO_HAT-円-Y2001-B";
		final String nohatBananaYen1B = "ばなな-NO_HAT-円-Y2001-B";
		
		Exalge data1 = new Exalge(new Object[]{
				new ExBase(nohatAppleNum0A),	new BigDecimal(10),
				new ExBase(nohatOrangeNum0A),	new BigDecimal(20),
				new ExBase(nohatBananaNum0A),	new BigDecimal(30),
				new ExBase(nohatMelonNum0A),	new BigDecimal(40),
		});
		
		Exalge data2 = new Exalge(new Object[]{
				new ExBase(nohatAppleYen1B),	new BigDecimal(4),
				new ExBase(nohatOrangeYen1B),	new BigDecimal(5),
				new ExBase(nohatBananaYen1B),	new BigDecimal(6)
		});
		
		Exalge ret1 = data1.elementMultiple(data2, ExtendedKeyID.UNIT, "価額");
		assertEquals(new BigDecimal(40), ret1.get(new ExBase("りんご-NO_HAT-価額-#-#")));
		assertEquals(new BigDecimal(100), ret1.get(new ExBase("みかん-NO_HAT-価額-#-#")));
		assertEquals(new BigDecimal(180), ret1.get(new ExBase("ばなな-NO_HAT-価額-#-#")));
		assertEquals(BigDecimal.ZERO, ret1.get(new ExBase("めろん-NO_HAT-価額-#-#")));
		
		Exalge ret2 = data1.elementMultiple(data2, ExtendedKeyID.TIME, "Y2005Q3");
		assertEquals(new BigDecimal(40), ret2.get(new ExBase("りんご-NO_HAT-#-Y2005Q3-#")));
		assertEquals(new BigDecimal(100), ret2.get(new ExBase("みかん-NO_HAT-#-Y2005Q3-#")));
		assertEquals(new BigDecimal(180), ret2.get(new ExBase("ばなな-NO_HAT-#-Y2005Q3-#")));
		assertEquals(BigDecimal.ZERO, ret2.get(new ExBase("めろん-NO_HAT-#-Y2005Q3-#")));
		
		Exalge ret3 = data1.elementMultiple(data2, ExtendedKeyID.SUBJECT, "CX");
		assertEquals(new BigDecimal(40), ret3.get(new ExBase("りんご-NO_HAT-#-#-CX")));
		assertEquals(new BigDecimal(100), ret3.get(new ExBase("みかん-NO_HAT-#-#-CX")));
		assertEquals(new BigDecimal(180), ret3.get(new ExBase("ばなな-NO_HAT-#-#-CX")));
		assertEquals(BigDecimal.ZERO, ret3.get(new ExBase("めろん-NO_HAT-#-#-CX")));
	}

	/**
	 * {@link exalge2.Exalge#norm()} のためのテスト・メソッド。
	 */
	public void testNorm() {
		Exalge data1 = new Exalge(new Object[]{
				new ExBase(nohatAppleYen),	new BigDecimal(10),
				new ExBase(nohatOrangeYen),	new BigDecimal(20),
				new ExBase(nohatBananaYen),	new BigDecimal(30),
				new ExBase(nohatMelonYen),	new BigDecimal(0)
		});
		
		Exalge data2 = new Exalge(new Object[]{
				new ExBase(nohatAppleYen),	new BigDecimal(10),
				new ExBase(nohatOrangeYen),	new BigDecimal(20),
				new ExBase(nohatBananaYen),	new BigDecimal(30),
				new ExBase(hatCashYen),		new BigDecimal(60),
				new ExBase(hatAppleYen),	new BigDecimal(10),
				new ExBase(hatOrangeYen),	new BigDecimal(20),
				new ExBase(hatBananaYen),	new BigDecimal(30)
		});
		
		BigDecimal ret1 = data1.norm();
		assertEquals(new BigDecimal(60), ret1);
		
		BigDecimal ret2 = data2.norm();
		assertEquals(new BigDecimal(180), ret2);
	}

	/**
	 * {@link exalge2.Exalge#hat()} のためのテスト・メソッド。
	 */
	public void testHat() {
		Exalge data1 = new Exalge(new Object[]{
				new ExBase(nohatAppleYen),	new BigDecimal(10),
				new ExBase(nohatOrangeYen),	new BigDecimal(20),
				new ExBase(nohatBananaYen),	new BigDecimal(30),
				new ExBase(nohatMelonYen),	new BigDecimal(0),
				new ExBase(hatCashYen),		new BigDecimal(60)
		});
		
		Exalge ret1 = data1.hat();
		assertEquals(new BigDecimal(10), ret1.get(new ExBase(hatAppleYen)));
		assertEquals(new BigDecimal(20), ret1.get(new ExBase(hatOrangeYen)));
		assertEquals(new BigDecimal(30), ret1.get(new ExBase(hatBananaYen)));
		assertFalse(ret1.containsBase(new ExBase(nohatMelonYen)));
		assertTrue(ret1.containsBase(new ExBase(hatMelonYen)));
		assertEquals(0, ret1.get(new ExBase(hatMelonYen)).compareTo(BigDecimal.ZERO));
		assertEquals(new BigDecimal(60), ret1.get(new ExBase(nohatCashYen)));
		
		Exalge ret2 = ret1.hat();
		assertEquals(new BigDecimal(10), ret2.get(new ExBase(nohatAppleYen)));
		assertEquals(new BigDecimal(20), ret2.get(new ExBase(nohatOrangeYen)));
		assertEquals(new BigDecimal(30), ret2.get(new ExBase(nohatBananaYen)));
		assertEquals(new BigDecimal(60), ret2.get(new ExBase(hatCashYen)));
		
		assertFalse(data1.isEqualValues(ret1));
		assertTrue(data1.isEqualValues(ret2));
	}

	/**
	 * {@link exalge2.Exalge#bar()} のためのテスト・メソッド。
	 */
	public void testBar() {
		Exalge data1 = new Exalge(new Object[]{
				new ExBase(nohatAppleYen),	new BigDecimal(10),
				new ExBase(nohatOrangeYen),	new BigDecimal(20),
				new ExBase(nohatBananaYen),	new BigDecimal(30),
				new ExBase(nohatMelonYen),	new BigDecimal(0)
		});
		
		Exalge data2 = new Exalge(new Object[]{
				new ExBase(nohatAppleYen),	new BigDecimal(10),
				new ExBase(nohatOrangeYen),	new BigDecimal(20),
				new ExBase(nohatBananaYen),	new BigDecimal(30),
				new ExBase(hatCashYen),		new BigDecimal(60),
				new ExBase(hatAppleYen),	new BigDecimal(10),
				new ExBase(hatOrangeYen),	new BigDecimal(20),
				new ExBase(hatBananaYen),	new BigDecimal(30)
		});
		
		Exalge ret1 = data1.bar();
		assertTrue(data1.isEqualValues(ret1));
		assertFalse(data1.isSameValues(ret1));
		assertFalse(ret1.containsBase(new ExBase(nohatMelonYen)));
		
		Exalge ret2 = data2.bar();
		assertFalse(data2.isEqualValues(ret2));
		assertFalse(data2.isSameValues(ret2));
		
		assertEquals(BigDecimal.ZERO, ret2.get(new ExBase(nohatAppleYen)));
		assertEquals(BigDecimal.ZERO, ret2.get(new ExBase(nohatOrangeYen)));
		assertEquals(BigDecimal.ZERO, ret2.get(new ExBase(nohatBananaYen)));
		assertEquals(BigDecimal.ZERO, ret2.get(new ExBase(hatAppleYen)));
		assertEquals(BigDecimal.ZERO, ret2.get(new ExBase(hatOrangeYen)));
		assertEquals(BigDecimal.ZERO, ret2.get(new ExBase(hatBananaYen)));
		assertEquals(new BigDecimal(60), ret2.get(new ExBase(hatCashYen)));
	}
	
	/**
	 * {@link exalge2.Exalge#strictBar()} のためのテスト・メソッド。
	 * @since 0.984
	 */
	public void testStrictBar() {
		Exalge data1 = new Exalge(new Object[]{
				new ExBase(nohatAppleYen),	new BigDecimal(10),
				new ExBase(nohatOrangeYen),	new BigDecimal(20),
				new ExBase(nohatBananaYen),	new BigDecimal(30),
				new ExBase(nohatMelonYen),	new BigDecimal(0)
		});
		
		Exalge data2 = new Exalge(new Object[]{
				new ExBase(nohatAppleYen),	new BigDecimal(10),
				new ExBase(nohatOrangeYen),	new BigDecimal(20),
				new ExBase(nohatBananaYen),	new BigDecimal(30),
				new ExBase(nohatMangoYen) , new BigDecimal(0),
				new ExBase(nohatTunaYen)  , new BigDecimal(0),
				new ExBase(hatCashYen),		new BigDecimal(60),
				new ExBase(hatAppleYen),	new BigDecimal(10),
				new ExBase(hatOrangeYen),	new BigDecimal(20),
				new ExBase(hatBananaYen),	new BigDecimal(30),
				new ExBase(hatMangoYen),    new BigDecimal(0),
				new ExBase(hatBonitoYen),   new BigDecimal(0)
		});
		
		Exalge ret1 = data1.strictBar();
		assertTrue(data1.isEqualValues(ret1));
		assertTrue(data1.isSameValues(ret1));
		
		Exalge ret2 = data2.strictBar();
		assertFalse(data2.isEqualValues(ret2));
		assertFalse(data2.isSameValues(ret2));
		//--- 相殺
		assertFalse(ret2.containsBase(new ExBase(nohatAppleYen)));
		assertFalse(ret2.containsBase(new ExBase(nohatOrangeYen)));
		assertFalse(ret2.containsBase(new ExBase(nohatBananaYen)));
		assertFalse(ret2.containsBase(new ExBase(nohatMangoYen)));
		assertFalse(ret2.containsBase(new ExBase(hatAppleYen)));
		assertFalse(ret2.containsBase(new ExBase(hatOrangeYen)));
		assertFalse(ret2.containsBase(new ExBase(hatBananaYen)));
		assertFalse(ret2.containsBase(new ExBase(hatMangoYen)));
		assertEquals(new BigDecimal(0), ret2.get(new ExBase(nohatAppleYen)));
		assertEquals(new BigDecimal(0), ret2.get(new ExBase(nohatOrangeYen)));
		assertEquals(new BigDecimal(0), ret2.get(new ExBase(nohatBananaYen)));
		assertEquals(new BigDecimal(0), ret2.get(new ExBase(nohatMangoYen)));
		assertEquals(new BigDecimal(0), ret2.get(new ExBase(hatAppleYen)));
		assertEquals(new BigDecimal(0), ret2.get(new ExBase(hatOrangeYen)));
		assertEquals(new BigDecimal(0), ret2.get(new ExBase(hatBananaYen)));
		assertEquals(new BigDecimal(0), ret2.get(new ExBase(hatMangoYen)));
		//--- 要素あり
		assertFalse(ret2.containsBase(new ExBase(nohatCashYen)));
		assertTrue(ret2.containsBase(new ExBase(hatCashYen)));
		assertTrue(ret2.containsBase(new ExBase(nohatTunaYen)));
		assertFalse(ret2.containsBase(new ExBase(hatTunaYen)));
		assertFalse(ret2.containsBase(new ExBase(nohatBonitoYen)));
		assertTrue(ret2.containsBase(new ExBase(hatBonitoYen)));
		assertEquals(new BigDecimal(0) , ret2.get(new ExBase(nohatCashYen)));
		assertEquals(new BigDecimal(60), ret2.get(new ExBase(hatCashYen)));
		assertEquals(new BigDecimal(0) , ret2.get(new ExBase(nohatTunaYen)));
		assertEquals(new BigDecimal(0) , ret2.get(new ExBase(hatTunaYen)));
		assertEquals(new BigDecimal(0) , ret2.get(new ExBase(nohatBonitoYen)));
		assertEquals(new BigDecimal(0) , ret2.get(new ExBase(hatBonitoYen)));
	}
	
	/**
	 * {@link exalge2.Exalge#strictBarLeaveZero(boolean)} のためのテスト・メソッド。
	 * @since 0.984
	 */
	public void testStrictBarLeaveZeroBoolean() {
		Exalge data1 = new Exalge(new Object[]{
				new ExBase(nohatAppleYen),	new BigDecimal(10),
				new ExBase(nohatOrangeYen),	new BigDecimal(20),
				new ExBase(nohatBananaYen),	new BigDecimal(30),
				new ExBase(nohatMelonYen),	new BigDecimal(0)
		});
		
		Exalge data2 = new Exalge(new Object[]{
				new ExBase(nohatAppleYen),	new BigDecimal(10),
				new ExBase(nohatOrangeYen),	new BigDecimal(20),
				new ExBase(nohatBananaYen),	new BigDecimal(30),
				new ExBase(nohatMangoYen) , new BigDecimal(0),
				new ExBase(nohatTunaYen)  , new BigDecimal(0),
				new ExBase(hatCashYen),		new BigDecimal(60),
				new ExBase(hatAppleYen),	new BigDecimal(10),
				new ExBase(hatOrangeYen),	new BigDecimal(20),
				new ExBase(hatBananaYen),	new BigDecimal(30),
				new ExBase(hatMangoYen),    new BigDecimal(0),
				new ExBase(hatBonitoYen),   new BigDecimal(0)
		});
		
		Exalge ret1t = data1.strictBarLeaveZero(true);
		assertTrue(data1.isEqualValues(ret1t));
		assertTrue(data1.isSameValues(ret1t));
		
		Exalge ret1f = data1.strictBarLeaveZero(false);
		assertTrue(data1.isEqualValues(ret1f));
		assertTrue(data1.isSameValues(ret1f));
		
		Exalge ret2t = data2.strictBarLeaveZero(true);
		assertFalse(data2.isEqualValues(ret2t));
		assertFalse(data2.isSameValues(ret2t));
		//--- 相殺
		assertTrue(ret2t.containsBase(new ExBase(nohatAppleYen)));
		assertTrue(ret2t.containsBase(new ExBase(nohatOrangeYen)));
		assertTrue(ret2t.containsBase(new ExBase(nohatBananaYen)));
		assertTrue(ret2t.containsBase(new ExBase(nohatMangoYen)));
		assertFalse(ret2t.containsBase(new ExBase(hatAppleYen)));
		assertFalse(ret2t.containsBase(new ExBase(hatOrangeYen)));
		assertFalse(ret2t.containsBase(new ExBase(hatBananaYen)));
		assertFalse(ret2t.containsBase(new ExBase(hatMangoYen)));
		assertEquals(new BigDecimal(0), ret2t.get(new ExBase(nohatAppleYen)));
		assertEquals(new BigDecimal(0), ret2t.get(new ExBase(nohatOrangeYen)));
		assertEquals(new BigDecimal(0), ret2t.get(new ExBase(nohatBananaYen)));
		assertEquals(new BigDecimal(0), ret2t.get(new ExBase(nohatMangoYen)));
		assertEquals(new BigDecimal(0), ret2t.get(new ExBase(hatAppleYen)));
		assertEquals(new BigDecimal(0), ret2t.get(new ExBase(hatOrangeYen)));
		assertEquals(new BigDecimal(0), ret2t.get(new ExBase(hatBananaYen)));
		assertEquals(new BigDecimal(0), ret2t.get(new ExBase(hatMangoYen)));
		//--- 要素あり
		assertFalse(ret2t.containsBase(new ExBase(nohatCashYen)));
		assertTrue(ret2t.containsBase(new ExBase(hatCashYen)));
		assertTrue(ret2t.containsBase(new ExBase(nohatTunaYen)));
		assertFalse(ret2t.containsBase(new ExBase(hatTunaYen)));
		assertFalse(ret2t.containsBase(new ExBase(nohatBonitoYen)));
		assertTrue(ret2t.containsBase(new ExBase(hatBonitoYen)));
		assertEquals(new BigDecimal(0) , ret2t.get(new ExBase(nohatCashYen)));
		assertEquals(new BigDecimal(60), ret2t.get(new ExBase(hatCashYen)));
		assertEquals(new BigDecimal(0) , ret2t.get(new ExBase(nohatTunaYen)));
		assertEquals(new BigDecimal(0) , ret2t.get(new ExBase(hatTunaYen)));
		assertEquals(new BigDecimal(0) , ret2t.get(new ExBase(nohatBonitoYen)));
		assertEquals(new BigDecimal(0) , ret2t.get(new ExBase(hatBonitoYen)));
		
		Exalge ret2f = data2.strictBarLeaveZero(false);
		assertFalse(data2.isEqualValues(ret2f));
		assertFalse(data2.isSameValues(ret2f));
		//--- 相殺
		assertFalse(ret2f.containsBase(new ExBase(nohatAppleYen)));
		assertFalse(ret2f.containsBase(new ExBase(nohatOrangeYen)));
		assertFalse(ret2f.containsBase(new ExBase(nohatBananaYen)));
		assertFalse(ret2f.containsBase(new ExBase(nohatMangoYen)));
		assertTrue(ret2f.containsBase(new ExBase(hatAppleYen)));
		assertTrue(ret2f.containsBase(new ExBase(hatOrangeYen)));
		assertTrue(ret2f.containsBase(new ExBase(hatBananaYen)));
		assertTrue(ret2f.containsBase(new ExBase(hatMangoYen)));
		assertEquals(new BigDecimal(0), ret2f.get(new ExBase(nohatAppleYen)));
		assertEquals(new BigDecimal(0), ret2f.get(new ExBase(nohatOrangeYen)));
		assertEquals(new BigDecimal(0), ret2f.get(new ExBase(nohatBananaYen)));
		assertEquals(new BigDecimal(0), ret2f.get(new ExBase(nohatMangoYen)));
		assertEquals(new BigDecimal(0), ret2f.get(new ExBase(hatAppleYen)));
		assertEquals(new BigDecimal(0), ret2f.get(new ExBase(hatOrangeYen)));
		assertEquals(new BigDecimal(0), ret2f.get(new ExBase(hatBananaYen)));
		assertEquals(new BigDecimal(0), ret2f.get(new ExBase(hatMangoYen)));
		//--- 要素あり
		assertFalse(ret2f.containsBase(new ExBase(nohatCashYen)));
		assertTrue(ret2f.containsBase(new ExBase(hatCashYen)));
		assertTrue(ret2f.containsBase(new ExBase(nohatTunaYen)));
		assertFalse(ret2f.containsBase(new ExBase(hatTunaYen)));
		assertFalse(ret2f.containsBase(new ExBase(nohatBonitoYen)));
		assertTrue(ret2f.containsBase(new ExBase(hatBonitoYen)));
		assertEquals(new BigDecimal(0) , ret2f.get(new ExBase(nohatCashYen)));
		assertEquals(new BigDecimal(60), ret2f.get(new ExBase(hatCashYen)));
		assertEquals(new BigDecimal(0) , ret2f.get(new ExBase(nohatTunaYen)));
		assertEquals(new BigDecimal(0) , ret2f.get(new ExBase(hatTunaYen)));
		assertEquals(new BigDecimal(0) , ret2f.get(new ExBase(nohatBonitoYen)));
		assertEquals(new BigDecimal(0) , ret2f.get(new ExBase(hatBonitoYen)));
	}

	/**
	 * {@link exalge2.Exalge#inverse()} のためのテスト・メソッド。
	 */
	public void testInverse() {
		Exalge data1 = new Exalge(new Object[]{
				new ExBase(nohatAppleYen),	new BigDecimal(10),
				new ExBase(nohatOrangeYen),	new BigDecimal(20),
				new ExBase(hatBananaYen),	new BigDecimal(40),
				new ExBase(hatMangoYen),	new BigDecimal(0)
		});
		
		BigDecimal val1 = new BigDecimal("0.1");		// = 1 / 10
		BigDecimal val2 = new BigDecimal("0.05");		// = 1 / 20
		BigDecimal val3 = new BigDecimal("0.025");		// = 1 / 40
		BigDecimal vala = val1.add(val2).add(val3);
		
		Exalge ret1 = data1.inverse();
		//System.out.println("data1 = " + data1.toFormattedString());
		//System.out.println("ret1 = " + ret1.toFormattedString());
		assertFalse(data1.isEqualValues(ret1));
		assertTrue(ret1.containsBase(new ExBase(nohatAppleYen)));
		assertTrue(ret1.containsBase(new ExBase(nohatOrangeYen)));
		assertTrue(ret1.containsBase(new ExBase(hatBananaYen)));
		assertFalse(ret1.containsBase(new ExBase(hatMangoYen)));
		assertTrue(0 == ret1.get(new ExBase(nohatAppleYen)).compareTo(val1));
		assertTrue(0 == ret1.get(new ExBase(nohatOrangeYen)).compareTo(val2));
		assertTrue(0 == ret1.get(new ExBase(hatBananaYen)).compareTo(val3));
		assertTrue(0 == ret1.norm().compareTo(vala));
	}

	/**
	 * {@link exalge2.Exalge#sum(java.util.Collection)} のためのテスト・メソッド。
	 */
	public void testSum() {
		Exalge data1 = new Exalge(new Object[]{
				new ExBase(nohatMelonYen),	new BigDecimal(0),
				new ExBase(nohatAppleYen),	new BigDecimal(10),
				new ExBase(nohatOrangeYen),	new BigDecimal(20),
				new ExBase(nohatBananaYen),	new BigDecimal(30),
				new ExBase(hatCashYen),		new BigDecimal(60)
		});
		
		Exalge data2 = new Exalge(new Object[]{
				new ExBase(hatAppleYen),	new BigDecimal(1),
				new ExBase(hatOrangeYen),	new BigDecimal(2),
				new ExBase(hatBananaYen),	new BigDecimal(3)
		});
		
		Exalge data3 = new Exalge(new Object[]{
				new ExBase(nohatMangoYen),	new BigDecimal(0),
				new ExBase(nohatOrangeYen),	new BigDecimal(100),
				new ExBase(nohatBananaYen),	new BigDecimal(200),
				new ExBase(nohatMelonYen),	new BigDecimal(1),
				new ExBase(hatCashYen),		new BigDecimal(300)
		});
		
		Object[] seqData = new Object[]{
				new ExBase(nohatMelonYen),	new BigDecimal(1),
				new ExBase(nohatAppleYen),	new BigDecimal(10),
				new ExBase(nohatOrangeYen),	new BigDecimal(120),
				new ExBase(nohatBananaYen),	new BigDecimal(230),
				new ExBase(hatCashYen),		new BigDecimal(360),
				
				new ExBase(hatAppleYen),	new BigDecimal(1),
				new ExBase(hatOrangeYen),	new BigDecimal(2),
				new ExBase(hatBananaYen),	new BigDecimal(3),
				
				new ExBase(nohatMangoYen),	new BigDecimal(0),
		};
		
		ArrayList<Exalge> alges = new ArrayList<Exalge>();
		alges.add(data1);
		alges.add(data2);
		alges.add(data3);
		
		Exalge ret1 = Exalge.sum(alges);
		assertEquals(new BigDecimal(10), ret1.get(new ExBase(nohatAppleYen)));
		assertEquals(new BigDecimal(120), ret1.get(new ExBase(nohatOrangeYen)));
		assertEquals(new BigDecimal(230), ret1.get(new ExBase(nohatBananaYen)));
		assertEquals(new BigDecimal(360), ret1.get(new ExBase(hatCashYen)));
		assertEquals(new BigDecimal(1), ret1.get(new ExBase(hatAppleYen)));
		assertEquals(new BigDecimal(2), ret1.get(new ExBase(hatOrangeYen)));
		assertEquals(new BigDecimal(3), ret1.get(new ExBase(hatBananaYen)));
		
		ExAlgeSetIOTest.checkElemSequence(ret1, seqData);
	}

	/**
	 * {@link exalge2.Exalge#transfer(exalge2.TransTable)} のためのテスト・メソッド。
	 */
	public void testTransferTransTable() {
		TransTable table = new TransTable();
		table.put(new ExBasePattern("りんご"), new ExBasePattern("果物-*-個"));
		table.put(new ExBasePattern("みかん"), new ExBasePattern("果物-*-個"));
		table.put(new ExBasePattern("ばなな"), new ExBasePattern("果物-*-個"));
		table.put(new ExBasePattern("メロン"), new ExBasePattern("高級果物-*-個"));
		table.put(new ExBasePattern("マンゴー"), new ExBasePattern("高級果物-*-個"));
		table.put(new ExBasePattern("まぐろ"), new ExBasePattern("魚-*"));
		table.put(new ExBasePattern("かつお"), new ExBasePattern("魚-*"));
		
		Exalge data1 = new Exalge(new Object[]{
				new ExBase(nohatAppleYen),	new BigDecimal(10),
				new ExBase(nohatOrangeYen),	new BigDecimal(20),
				new ExBase(nohatBananaYen),	new BigDecimal(30),
				new ExBase(nohatMelonYen),	new BigDecimal(0),
				new ExBase(hatCashYen),		new BigDecimal(60)
		});
		Exalge ret1 = data1.transfer(table);
		assertFalse(data1.isEqualValues(ret1));
		assertEquals(0, ret1.get(new ExBase(nohatAppleYen)).compareTo(new BigDecimal(10)));
		assertEquals(0, ret1.get(new ExBase(hatAppleYen)).compareTo(new BigDecimal(10)));
		assertEquals(0, ret1.get(new ExBase(nohatOrangeYen)).compareTo(new BigDecimal(20)));
		assertEquals(0, ret1.get(new ExBase(hatOrangeYen)).compareTo(new BigDecimal(20)));
		assertEquals(0, ret1.get(new ExBase(nohatBananaYen)).compareTo(new BigDecimal(30)));
		assertEquals(0, ret1.get(new ExBase(hatBananaYen)).compareTo(new BigDecimal(30)));
		assertEquals(0, ret1.get(new ExBase(nohatFruitNum)).compareTo(new BigDecimal(60)));
		assertTrue(ret1.containsBase(new ExBase(nohatMelonYen)));
		assertTrue(ret1.containsBase(new ExBase(hatMelonYen)));
		assertTrue(ret1.containsBase(new ExBase(nohatExpFruitNum)));
		assertEquals(0, ret1.get(new ExBase(nohatExpFruitNum)).compareTo(BigDecimal.ZERO));
		assertEquals(0, ret1.get(new ExBase(hatCashYen)).compareTo(new BigDecimal(60)));
		assertEquals(new BigDecimal(240), ret1.norm());
		
		Exalge data2 = new Exalge(new Object[]{
				new ExBase(hatAppleYen),	new BigDecimal(10),
				new ExBase(hatOrangeYen),	new BigDecimal(20),
				new ExBase(hatBananaYen),	new BigDecimal(30),
				new ExBase(hatMangoYen),	new BigDecimal(0),
				new ExBase(nohatCashYen),	new BigDecimal(60)
		});
		Exalge ret2 = data2.transfer(table);
		assertFalse(data2.isEqualValues(ret2));
		assertEquals(0, ret2.get(new ExBase(nohatAppleYen)).compareTo(new BigDecimal(10)));
		assertEquals(0, ret2.get(new ExBase(hatAppleYen)).compareTo(new BigDecimal(10)));
		assertEquals(0, ret2.get(new ExBase(nohatOrangeYen)).compareTo(new BigDecimal(20)));
		assertEquals(0, ret2.get(new ExBase(hatOrangeYen)).compareTo(new BigDecimal(20)));
		assertEquals(0, ret2.get(new ExBase(nohatBananaYen)).compareTo(new BigDecimal(30)));
		assertEquals(0, ret2.get(new ExBase(hatBananaYen)).compareTo(new BigDecimal(30)));
		assertEquals(0, ret2.get(new ExBase(hatFruitNum)).compareTo(new BigDecimal(60)));
		assertTrue(ret2.containsBase(new ExBase(hatMangoYen)));
		assertTrue(ret2.containsBase(new ExBase(nohatMangoYen)));
		assertTrue(ret2.containsBase(new ExBase(hatExpFruitNum)));
		assertEquals(0, ret2.get(new ExBase(hatExpFruitNum)).compareTo(BigDecimal.ZERO));
		assertEquals(0, ret2.get(new ExBase(nohatCashYen)).compareTo(new BigDecimal(60)));
		assertEquals(new BigDecimal(240), ret2.norm());
		
		Exalge data3 = new Exalge(new Object[]{
				new ExBase(nohatTunaYen),	new BigDecimal(100),
				new ExBase(hatCashYen),		new BigDecimal(100),
				new ExBase(hatBonitoYen),	new BigDecimal(200),
				new ExBase(nohatCashYen),	new BigDecimal(200)
		});
		Exalge ret3 = data3.transfer(table);
		assertFalse(data3.isEqualValues(ret3));
		assertEquals(0, ret3.get(new ExBase(nohatTunaYen)).compareTo(new BigDecimal(100)));
		assertEquals(0, ret3.get(new ExBase(hatTunaYen)).compareTo(new BigDecimal(100)));
		assertEquals(0, ret3.get(new ExBase(nohatBonitoYen)).compareTo(new BigDecimal(200)));
		assertEquals(0, ret3.get(new ExBase(hatBonitoYen)).compareTo(new BigDecimal(200)));
		assertEquals(0, ret3.get(new ExBase(nohatFishYen)).compareTo(new BigDecimal(100)));
		assertEquals(0, ret3.get(new ExBase(hatFishYen)).compareTo(new BigDecimal(200)));
		assertEquals(0, ret3.get(new ExBase(hatCashYen)).compareTo(new BigDecimal(100)));
		assertEquals(0, ret3.get(new ExBase(nohatCashYen)).compareTo(new BigDecimal(200)));
		assertEquals(new BigDecimal(1200), ret3.norm());
	}

	/**
	 * {@link exalge2.Exalge#aggreTransfer(exalge2.TransTable)} のためのテスト・メソッド。
	 */
	public void testAggreTransfer() {
		TransTable table = new TransTable();
		table.put(new ExBasePattern("りんご"), new ExBasePattern("果物-*-個"));
		table.put(new ExBasePattern("みかん"), new ExBasePattern("果物-*-個"));
		table.put(new ExBasePattern("ばなな"), new ExBasePattern("果物-*-個"));
		table.put(new ExBasePattern("メロン"), new ExBasePattern("高級果物-*-個"));
		table.put(new ExBasePattern("マンゴー"), new ExBasePattern("高級果物-*-個"));
		table.put(new ExBasePattern("まぐろ"), new ExBasePattern("魚-*"));
		table.put(new ExBasePattern("かつお"), new ExBasePattern("魚-*"));
		
		Exalge data1 = new Exalge(new Object[]{
				new ExBase(nohatAppleYen),	new BigDecimal(10),
				new ExBase(nohatOrangeYen),	new BigDecimal(20),
				new ExBase(nohatBananaYen),	new BigDecimal(30),
				new ExBase(nohatMelonYen),	new BigDecimal(0),
				new ExBase(hatCashYen),		new BigDecimal(60)
		});
		Exalge ret1 = data1.aggreTransfer(table);
		assertFalse(data1.isEqualValues(ret1));
		assertEquals(0, ret1.get(new ExBase(nohatAppleYen)).compareTo(new BigDecimal(10)));
		assertEquals(0, ret1.get(new ExBase(hatAppleYen)).compareTo(new BigDecimal(10)));
		assertEquals(0, ret1.get(new ExBase(nohatOrangeYen)).compareTo(new BigDecimal(20)));
		assertEquals(0, ret1.get(new ExBase(hatOrangeYen)).compareTo(new BigDecimal(20)));
		assertEquals(0, ret1.get(new ExBase(nohatBananaYen)).compareTo(new BigDecimal(30)));
		assertEquals(0, ret1.get(new ExBase(hatBananaYen)).compareTo(new BigDecimal(30)));
		assertEquals(0, ret1.get(new ExBase(nohatFruitNum)).compareTo(new BigDecimal(60)));
		assertTrue(ret1.containsBase(new ExBase(nohatMelonYen)));
		assertTrue(ret1.containsBase(new ExBase(hatMelonYen)));
		assertTrue(ret1.containsBase(new ExBase(nohatExpFruitNum)));
		assertEquals(0, ret1.get(new ExBase(nohatExpFruitNum)).compareTo(BigDecimal.ZERO));
		assertEquals(0, ret1.get(new ExBase(hatCashYen)).compareTo(new BigDecimal(60)));
		assertEquals(new BigDecimal(240), ret1.norm());
		
		Exalge data2 = new Exalge(new Object[]{
				new ExBase(hatAppleYen),	new BigDecimal(10),
				new ExBase(hatOrangeYen),	new BigDecimal(20),
				new ExBase(hatBananaYen),	new BigDecimal(30),
				new ExBase(hatMangoYen),	new BigDecimal(0),
				new ExBase(nohatCashYen),	new BigDecimal(60)
		});
		Exalge ret2 = data2.aggreTransfer(table);
		assertFalse(data2.isEqualValues(ret2));
		assertEquals(0, ret2.get(new ExBase(nohatAppleYen)).compareTo(new BigDecimal(10)));
		assertEquals(0, ret2.get(new ExBase(hatAppleYen)).compareTo(new BigDecimal(10)));
		assertEquals(0, ret2.get(new ExBase(nohatOrangeYen)).compareTo(new BigDecimal(20)));
		assertEquals(0, ret2.get(new ExBase(hatOrangeYen)).compareTo(new BigDecimal(20)));
		assertEquals(0, ret2.get(new ExBase(nohatBananaYen)).compareTo(new BigDecimal(30)));
		assertEquals(0, ret2.get(new ExBase(hatBananaYen)).compareTo(new BigDecimal(30)));
		assertEquals(0, ret2.get(new ExBase(hatFruitNum)).compareTo(new BigDecimal(60)));
		assertTrue(ret2.containsBase(new ExBase(hatMangoYen)));
		assertTrue(ret2.containsBase(new ExBase(nohatMangoYen)));
		assertTrue(ret2.containsBase(new ExBase(hatExpFruitNum)));
		assertEquals(0, ret2.get(new ExBase(hatExpFruitNum)).compareTo(BigDecimal.ZERO));
		assertEquals(0, ret2.get(new ExBase(nohatCashYen)).compareTo(new BigDecimal(60)));
		assertEquals(new BigDecimal(240), ret2.norm());
		
		Exalge data3 = new Exalge(new Object[]{
				new ExBase(nohatTunaYen),	new BigDecimal(100),
				new ExBase(hatCashYen),		new BigDecimal(100),
				new ExBase(hatBonitoYen),	new BigDecimal(200),
				new ExBase(nohatCashYen),	new BigDecimal(200)
		});
		Exalge ret3 = data3.aggreTransfer(table);
		assertFalse(data3.isEqualValues(ret3));
		assertEquals(0, ret3.get(new ExBase(nohatTunaYen)).compareTo(new BigDecimal(100)));
		assertEquals(0, ret3.get(new ExBase(hatTunaYen)).compareTo(new BigDecimal(100)));
		assertEquals(0, ret3.get(new ExBase(nohatBonitoYen)).compareTo(new BigDecimal(200)));
		assertEquals(0, ret3.get(new ExBase(hatBonitoYen)).compareTo(new BigDecimal(200)));
		assertEquals(0, ret3.get(new ExBase(nohatFishYen)).compareTo(new BigDecimal(100)));
		assertEquals(0, ret3.get(new ExBase(hatFishYen)).compareTo(new BigDecimal(200)));
		assertEquals(0, ret3.get(new ExBase(hatCashYen)).compareTo(new BigDecimal(100)));
		assertEquals(0, ret3.get(new ExBase(nohatCashYen)).compareTo(new BigDecimal(200)));
		assertEquals(new BigDecimal(1200), ret3.norm());
	}

	/**
	 * {@link exalge2.Exalge#divideTransfer(exalge2.TransMatrix)} のためのテスト・メソッド。
	 */
	public void testDivideTransfer() {
		ExBasePattern patTo1 = new ExBasePattern("りんご");
		ExBasePattern patTo2 = new ExBasePattern("みかん");
		ExBasePattern patTo3 = new ExBasePattern("ばなな");
		ExBasePattern patTo4 = new ExBasePattern("まぐろ");
		ExBasePattern patTo5 = new ExBasePattern("かつお");
		ExBasePattern patTo6 = new ExBasePattern("メロン");
		ExBasePattern patTo7 = new ExBasePattern("マンゴー");
		ExBasePattern patFrom1 = new ExBasePattern("果物");
		ExBasePattern patFrom2 = new ExBasePattern("魚");
		ExBasePattern patFrom3 = new ExBasePattern("高級果物");

		TransMatrix matrix1 = new TransMatrix();
		TransDivideRatios tomap1a = new TransDivideRatios();
		tomap1a.put(patTo1, new BigDecimal(1));
		tomap1a.put(patTo2, new BigDecimal(1));
		tomap1a.put(patTo3, new BigDecimal(1));
		tomap1a.updateTotalRatio();
		matrix1.put(patFrom1, tomap1a);
		TransDivideRatios tomap2a = new TransDivideRatios();
		tomap2a.put(patTo4, new BigDecimal(2));
		tomap2a.put(patTo5, new BigDecimal(2));
		tomap2a.updateTotalRatio();
		matrix1.put(patFrom2, tomap2a);
		TransDivideRatios tomap3a = new TransDivideRatios();
		tomap3a.put(patTo6, new BigDecimal("0.3"));
		tomap3a.put(patTo7, new BigDecimal("0.7"));
		tomap3a.updateTotalRatio();
		matrix1.put(patFrom3, tomap3a);
		matrix1.setUseTotalRatio(true);
		
		TransMatrix matrix2 = new TransMatrix();
		TransDivideRatios tomap1b = new TransDivideRatios();
		tomap1b.put(patTo1, new BigDecimal(0.5));
		tomap1b.put(patTo2, new BigDecimal(0.25));
		tomap1b.put(patTo3, new BigDecimal(0.25));
		matrix2.put(patFrom1, tomap1b);
		TransDivideRatios tomap2b = new TransDivideRatios();
		tomap2b.put(patTo4, new BigDecimal(0.5));
		tomap2b.put(patTo5, new BigDecimal(0.5));
		matrix2.put(patFrom2, tomap2b);
		TransDivideRatios tomap3b = new TransDivideRatios();
		tomap3b.put(patTo6, new BigDecimal("0.3"));
		tomap3b.put(patTo7, new BigDecimal("0.7"));
		matrix2.put(patFrom3, tomap3b);
		matrix2.setUseTotalRatio(false);
		
		Exalge data1 = new Exalge(new Object[]{
				new ExBase(nohatFruitYen),	new BigDecimal(300),
				new ExBase(nohatExpFruitYen),	new BigDecimal(0),
				new ExBase(hatCashYen),		new BigDecimal(300),
				new ExBase(hatFishYen),		new BigDecimal(500),
				new ExBase(nohatCashYen),	new BigDecimal(500)
		});
		
		Exalge ret1 = data1.divideTransfer(matrix1);
		System.out.println("testDivideTransfer() - Exalge ret1 = \n    " + ret1.toString());
		assertFalse(data1.isEqualValues(ret1));
		assertEquals(0, ret1.get(new ExBase(nohatFruitYen)).compareTo(new BigDecimal(300)));
		assertEquals(0, ret1.get(new ExBase(hatFruitYen)).compareTo(new BigDecimal(300)));
		assertEquals(0, ret1.get(new ExBase(nohatFishYen)).compareTo(new BigDecimal(500)));
		assertEquals(0, ret1.get(new ExBase(hatFishYen)).compareTo(new BigDecimal(500)));
		assertEquals(0, ret1.get(new ExBase(nohatAppleYen)).compareTo(new BigDecimal(100)));
		assertEquals(0, ret1.get(new ExBase(nohatOrangeYen)).compareTo(new BigDecimal(100)));
		assertEquals(0, ret1.get(new ExBase(nohatBananaYen)).compareTo(new BigDecimal(100)));
		assertEquals(0, ret1.get(new ExBase(hatTunaYen)).compareTo(new BigDecimal(250)));
		assertEquals(0, ret1.get(new ExBase(hatBonitoYen)).compareTo(new BigDecimal(250)));
		assertEquals(0, ret1.get(new ExBase(hatCashYen)).compareTo(new BigDecimal(300)));
		assertEquals(0, ret1.get(new ExBase(nohatCashYen)).compareTo(new BigDecimal(500)));
		assertTrue(ret1.containsBase(new ExBase(nohatExpFruitYen)));
		assertTrue(ret1.containsBase(new ExBase(hatExpFruitYen)));
		assertTrue(ret1.containsBase(new ExBase(nohatMelonYen)));
		assertTrue(ret1.containsBase(new ExBase(nohatMangoYen)));
		assertEquals(0, ret1.get(new ExBase(nohatExpFruitYen)).compareTo(BigDecimal.ZERO));
		assertEquals(0, ret1.get(new ExBase(hatExpFruitYen)).compareTo(BigDecimal.ZERO));
		assertEquals(0, ret1.get(new ExBase(nohatMelonYen)).compareTo(BigDecimal.ZERO));
		assertEquals(0, ret1.get(new ExBase(nohatMangoYen)).compareTo(BigDecimal.ZERO));
		assertEquals(0, ret1.norm().compareTo(new BigDecimal(3200)));
		
		Exalge ret2 = data1.divideTransfer(matrix2);
		assertFalse(data1.isEqualValues(ret2));
		assertEquals(0, ret2.get(new ExBase(nohatFruitYen)).compareTo(new BigDecimal(300)));
		assertEquals(0, ret2.get(new ExBase(hatFruitYen)).compareTo(new BigDecimal(300)));
		assertEquals(0, ret2.get(new ExBase(nohatFishYen)).compareTo(new BigDecimal(500)));
		assertEquals(0, ret2.get(new ExBase(hatFishYen)).compareTo(new BigDecimal(500)));
		assertEquals(0, ret2.get(new ExBase(nohatAppleYen)).compareTo(new BigDecimal(150)));
		assertEquals(0, ret2.get(new ExBase(nohatOrangeYen)).compareTo(new BigDecimal(75)));
		assertEquals(0, ret2.get(new ExBase(nohatBananaYen)).compareTo(new BigDecimal(75)));
		assertEquals(0, ret2.get(new ExBase(hatTunaYen)).compareTo(new BigDecimal(250)));
		assertEquals(0, ret2.get(new ExBase(hatBonitoYen)).compareTo(new BigDecimal(250)));
		assertEquals(0, ret2.get(new ExBase(hatCashYen)).compareTo(new BigDecimal(300)));
		assertEquals(0, ret2.get(new ExBase(nohatCashYen)).compareTo(new BigDecimal(500)));
		assertTrue(ret2.containsBase(new ExBase(nohatExpFruitYen)));
		assertTrue(ret2.containsBase(new ExBase(hatExpFruitYen)));
		assertTrue(ret2.containsBase(new ExBase(nohatMelonYen)));
		assertTrue(ret2.containsBase(new ExBase(nohatMangoYen)));
		assertEquals(0, ret2.get(new ExBase(nohatExpFruitYen)).compareTo(BigDecimal.ZERO));
		assertEquals(0, ret2.get(new ExBase(hatExpFruitYen)).compareTo(BigDecimal.ZERO));
		assertEquals(0, ret2.get(new ExBase(nohatMelonYen)).compareTo(BigDecimal.ZERO));
		assertEquals(0, ret2.get(new ExBase(nohatMangoYen)).compareTo(BigDecimal.ZERO));
		assertEquals(0, ret2.norm().compareTo(new BigDecimal(3200)));
	}

	/**
	 * {@link exalge2.Exalge#generalProjection(ExBase)} のためのテスト・メソッド。
	 * @since 0.960
	 */
	public void testGeneralProjectionExBase() {
		ExBase nBaseAppleYen = new ExBase(nohatAppleYen);
		ExBase hBaseAppleYen = new ExBase(hatAppleYen);
		ExBase nBaseOrangeYen = new ExBase(nohatOrangeYen);
		ExBase hBaseOrangeYen = new ExBase(hatOrangeYen);
		ExBase nBaseBananaYen = new ExBase(nohatBananaYen);
		ExBase hBaseBananaYen = new ExBase(hatBananaYen);
		ExBase nBaseMelonYen  = new ExBase(nohatMelonYen);
		ExBase hBaseMelonYen  = new ExBase(hatMelonYen);
		ExBase nBaseCashYen   = new ExBase(nohatCashYen);
		ExBase hBaseCashYen   = new ExBase(hatCashYen);
		
		ExBase[] allBaseData = new ExBase[]{
				nBaseAppleYen,
				hBaseAppleYen,
				nBaseOrangeYen,
				hBaseOrangeYen,
				nBaseBananaYen,
				hBaseBananaYen,
				nBaseMelonYen,
				hBaseMelonYen,
				nBaseCashYen,
				hBaseCashYen,
		};
		
		Object[] allAlgeData = new Object[]{
			nBaseAppleYen , new BigDecimal(20),
			hBaseAppleYen , new BigDecimal(20),
			nBaseOrangeYen, new BigDecimal(30),
			hBaseOrangeYen, new BigDecimal(30),
			nBaseBananaYen, new BigDecimal(40),
			hBaseMelonYen , new BigDecimal(50),
		};
		Exalge allAlge = new Exalge(allAlgeData);
		ExAlgeSetIOTest.checkElemSequence(allAlge, allAlgeData);
		
		ExBaseSet allBases = new ExBaseSet(Arrays.asList(allBaseData));
		
		// test case
		for (ExBase base : allBases) {
			ExBase nBase = base.removeHat();
			ExBase hBase = base.setHat();
			Exalge ret = allAlge.generalProjection(base);
			ExAlgeSetIOTest.checkElemSequence(allAlge, allAlgeData);
			ExBaseSet existTargetBases = new ExBaseSet(2);
			if (allAlge.containsBase(nBase)) {
				existTargetBases.add(nBase);
				assertTrue(ret.containsBase(nBase));
				assertTrue(allAlge.get(nBase).compareTo(ret.get(nBase))==0);
			} else {
				assertFalse(ret.containsBase(nBase));
			}
			if (allAlge.containsBase(hBase)) {
				existTargetBases.add(hBase);
				assertTrue(ret.containsBase(hBase));
				assertTrue(allAlge.get(hBase).compareTo(ret.get(hBase))==0);
			} else {
				assertFalse(ret.containsBase(hBase));
			}
			
			ExBaseSet notExistBases = (ExBaseSet)allBases.clone();
			notExistBases.removeAll(existTargetBases);
			for (ExBase noBase : notExistBases) {
				assertFalse(ret.containsBase(noBase));
			}
		}
		
		// null check
		boolean isCought = false;
		try {
			allAlge.generalProjection((ExBase)null);
			isCought = false;
		} catch (NullPointerException ex) {
			isCought = true;
		}
		assertTrue(isCought);
	}

	/**
	 * {@link exalge2.Exalge#generalProjection(ExBaseSet)} のためのテスト・メソッド。
	 * @since 0.960
	 */
	public void testGeneralProjectionExBaseSet() {
		ExBase nBaseAppleYen = new ExBase(nohatAppleYen);
		ExBase hBaseAppleYen = new ExBase(hatAppleYen);
		ExBase nBaseOrangeYen = new ExBase(nohatOrangeYen);
		ExBase hBaseOrangeYen = new ExBase(hatOrangeYen);
		ExBase nBaseBananaYen = new ExBase(nohatBananaYen);
		ExBase hBaseBananaYen = new ExBase(hatBananaYen);
		ExBase nBaseMelonYen  = new ExBase(nohatMelonYen);
		ExBase hBaseMelonYen  = new ExBase(hatMelonYen);
		ExBase nBaseCashYen   = new ExBase(nohatCashYen);
		ExBase hBaseCashYen   = new ExBase(hatCashYen);
		
		ExBase[] testBaseData1 = new ExBase[]{
				nBaseOrangeYen,
				nBaseBananaYen,
				nBaseMelonYen,
				nBaseCashYen,
		};
		
		ExBase[] testBaseData2 = new ExBase[]{
				hBaseOrangeYen,
				hBaseBananaYen,
				hBaseMelonYen,
				hBaseCashYen,
		};
		
		Object[] allAlgeData = new Object[]{
			nBaseAppleYen , new BigDecimal(20),
			hBaseAppleYen , new BigDecimal(20),
			nBaseOrangeYen, new BigDecimal(30),
			hBaseOrangeYen, new BigDecimal(30),
			nBaseBananaYen, new BigDecimal(40),
			hBaseMelonYen , new BigDecimal(50),
		};
		Exalge allAlge = new Exalge(allAlgeData);
		ExAlgeSetIOTest.checkElemSequence(allAlge, allAlgeData);
		
		ExBaseSet testBases1 = new ExBaseSet(Arrays.asList(testBaseData1));
		for (ExBase base : testBases1) {
			assertTrue(base.isNoHat());
		}
		ExBaseSet testBases2 = new ExBaseSet(Arrays.asList(testBaseData2));
		for (ExBase base : testBases2) {
			assertTrue(base.isHat());
		}
		ExBaseSet testAllBases = new ExBaseSet(Arrays.asList(testBaseData1));
		testAllBases.addAll(Arrays.asList(testBaseData2));
		assertTrue(testAllBases.containsAll(Arrays.asList(testBaseData1)));
		assertTrue(testAllBases.containsAll(Arrays.asList(testBaseData2)));
		ExBaseSet retBases = allAlge.getBases();
		retBases.retainAll(testAllBases);
		
		// test case 1
		Exalge ret1 = allAlge.generalProjection(new ExBaseSet());
		ExAlgeSetIOTest.checkElemSequence(allAlge, allAlgeData);
		assertTrue(ret1.isEmpty());
		
		// test case 2
		Exalge ret2 = allAlge.generalProjection(testBases1);
		assertFalse(ret2.isEmpty());
		assertEquals(ret2.getBases(), retBases);
		for (ExBase base : retBases) {
			assertTrue(allAlge.get(base).compareTo(ret2.get(base))==0);
		}
		
		// test case 3
		Exalge ret3 = allAlge.generalProjection(testBases2);
		assertFalse(ret3.isEmpty());
		assertEquals(ret3.getBases(), retBases);
		for (ExBase base : retBases) {
			assertTrue(allAlge.get(base).compareTo(ret3.get(base))==0);
		}
		
		// null check
		boolean isCought = false;
		try {
			allAlge.generalProjection((ExBaseSet)null);
			isCought = false;
		} catch (NullPointerException ex) {
			isCought = true;
		}
		assertTrue(isCought);
	}

	/**
	 * {@link exalge2.Exalge#multiple(Exalge)} のためのテスト・メソッド。
	 * @since 0.970
	 */
	public void testMultipleExalge() {
		ExBase[] testBases = new ExBase[]{
				new ExBase("e0", ExBase.NO_HAT),
				new ExBase("e1", ExBase.NO_HAT),
				new ExBase("e2", ExBase.NO_HAT),
				new ExBase("e3", ExBase.NO_HAT),
				new ExBase("e4", ExBase.NO_HAT),
				new ExBase("e5", ExBase.NO_HAT),
				new ExBase("e6", ExBase.NO_HAT),
				new ExBase("e7", ExBase.NO_HAT),
				new ExBase("e8", ExBase.NO_HAT),
				new ExBase("e9", ExBase.NO_HAT),
		};

		boolean coughtException = false;
		ExBase NE0 = testBases[0].removeHat();
		ExBase HE0 = testBases[0].setHat();
		Exalge algeEmpty = new Exalge();
		Exalge algeNE0   = makeAlge(NE0, 2);
		Exalge algeHE0   = makeAlge(HE0, 2);
		Exalge algePair0 = makeAlge(NE0, 3, HE0, 4);

		// null
		//---
		try {
			algeEmpty.multiple((Exalge)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//---
		try {
			algeNE0.multiple((Exalge)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		
		// Empty
		Exalge ret;
		//--- () * ae = ()
		ret = algeEmpty.multiple(algeNE0);
		assertTrue(ret.isEmpty());
		//--- () * a^e = ()
		ret = algeEmpty.multiple(algeHE0);
		assertTrue(ret.isEmpty());
		//--- () * ae + b^e = ()
		ret = algeEmpty.multiple(algePair0);
		assertTrue(ret.isEmpty());
		//--- ae * () = ()
		ret = algeNE0.multiple(algeEmpty);
		assertTrue(ret.isEmpty());
		//--- a^e * () = ()
		ret = algeHE0.multiple(algeEmpty);
		assertTrue(ret.isEmpty());
		//--- ae + b^e * () = ()
		ret = algePair0.multiple(algeEmpty);
		assertTrue(ret.isEmpty());
		
		// multiply one element
		Exalge ansNE0 = makeAlge(NE0, 4);
		Exalge ansHE0 = makeAlge(HE0, 4);
		//--- ae * ae = 2ae
		ret = algeNE0.multiple(algeNE0);
		assertEquals(ret, ansNE0);
		//--- ae * a^e = 2a^e
		ret = algeNE0.multiple(algeHE0);
		assertEquals(ret, ansHE0);
		//--- a^e * ae = 2a^e
		ret = algeHE0.multiple(algeNE0);
		assertEquals(ret, ansHE0);
		//--- a^e * a^e = 2ae
		ret = algeHE0.multiple(algeHE0);
		assertEquals(ret, ansNE0);
		
		// multiply two element
		Exalge ansPairNE0 = makeAlge(NE0, 6, HE0, 8);
		Exalge ansPairHE0 = makeAlge(NE0, 8, HE0, 6);
		Exalge ansPairPair = makeAlge(NE0, 25, HE0, 24);
		//--- ae + b^e * ce  = ace + bc^e
		ret = algePair0.multiple(algeNE0);
		assertEquals(ret, ansPairNE0);
		//--- ae + b^e * c^e = bce + ac^e
		ret = algePair0.multiple(algeHE0);
		assertEquals(ret, ansPairHE0);
		//--- ce * ae + b^e  = ace + bc^e
		ret = algeNE0.multiple(algePair0);
		assertEquals(ret, ansPairNE0);
		//--- c^e * ae + b^e = bce + ac^e
		ret = algeHE0.multiple(algePair0);
		assertEquals(ret, ansPairHE0);
		//--- ae + b^e * ae + b^e = (aa+bb)e + 2ab^e
		ret = algePair0.multiple(algePair0);
		assertEquals(ret, ansPairPair);
		
		// multiply alges
		Exalge alge1 = makeAlge(
							testBases[0].removeHat(), 1,
							testBases[0].setHat()   , 2,
							testBases[1].removeHat(), 11,
							testBases[1].setHat()   , 12,
							testBases[2].removeHat(), 21,
							testBases[2].setHat()   , 22,
							testBases[3].removeHat(), 31,
							testBases[3].setHat()   , 32,
							testBases[4].removeHat(), 41,
							testBases[4].setHat()   , 42,
							testBases[5].removeHat(), 51,
							testBases[5].setHat()   , 52,
							testBases[6].removeHat(), 61,
							testBases[6].setHat()   , 62,
							testBases[7].removeHat(), 71,
							testBases[7].setHat()   , 72,
							testBases[8].removeHat(), 81,
							testBases[8].setHat()   , 82,
							testBases[9].removeHat(), 91,
							testBases[9].setHat()   , 92
						);
		Exalge alge2 = makeAlge(
							testBases[0].removeHat(), 2,
							testBases[1].setHat()   , 3,
							testBases[2].removeHat(), 4,
							testBases[2].setHat()   , 0,
							testBases[3].removeHat(), 0,
							testBases[3].setHat()   , 5,
							testBases[4].removeHat(), 6,
							testBases[4].setHat()   , 7,
							testBases[6].removeHat(), 1,
							testBases[6].setHat()   , 1,
							testBases[7].removeHat(), 1,
							testBases[7].setHat()   , 0,
							testBases[8].removeHat(), 0,
							testBases[8].setHat()   , 1
						);
		Exalge algeAns = makeAlge(
							//---  1e0 +  2^e0 * 2e0        = 2e0 + 4^e0
							testBases[0].removeHat(), 2,
							testBases[0].setHat()   , 4,
							//--- 11e1 + 12^e1 * 3^e1       = 36e1 + 33^e1
							testBases[1].removeHat(), 36,
							testBases[1].setHat()   , 33,
							//--- 21e2 + 22^e2 * 4e2 + 0^e2 = 84e2 + 88^e2
							testBases[2].removeHat(), 84,
							testBases[2].setHat()   , 88,
							//--- 31e3 + 32^e3 * 0e3 + 5^e3 = 160e3 + 155^e3
							testBases[3].removeHat(), 160,
							testBases[3].setHat()   , 155,
							//--- 41e4 + 42^e4 * 6e4 + 7^e4 = (41*6+42*7)e4 + (41*7+42*6)^e4 = 540e4 + 539^e4
							testBases[4].removeHat(), 540,
							testBases[4].setHat()   , 539,
							//--- 51e5 + 52^e5 * ()         = ()
							//--- 61e6 + 62^e6 * 1e6 + 1^e6 = (61*1+62*1)e6 + (61*1+62*1)^e6 = 123e6 + 123^e6
							testBases[6].removeHat(), 123,
							testBases[6].setHat()   , 123,
							//--- 71e7 + 72^e7 * 1e7 + 0^e7 = 71e7 + 72^e7
							testBases[7].removeHat(), 71,
							testBases[7].setHat()   , 72,
							//--- 81e8 + 82^e8 * 0e8 + 1^e8 = 82e8 + 81^e8
							testBases[8].removeHat(), 82,
							testBases[8].setHat()   , 81
							//--- 91e9 + 92^e9 * ()         = ()
						);
		System.out.println("Multiple...");
		System.out.println("    alge1 = " + alge1);
		System.out.println("    alge2 = " + alge2);
		System.out.println("    algeAns = " + algeAns);
		System.out.println("    alge1*alge2 = " + alge1.multiple(alge2));
		System.out.println("    alge2*alge1 = " + alge2.multiple(alge1));
		//---
		ret = alge1.multiple(alge2);
		assertEquals(ret, algeAns);
		//---
		ret = alge2.multiple(alge1);
		assertEquals(ret, algeAns);
	}

	/**
	 * {@link exalge2.Exalge#containsAllBases(ExBaseSet)} のためのテスト・メソッド。
	 * @since 0.970
	 */
	public void testContainsAllBaseExBaseSet() {
		Exalge testAlge = makeAlge(
								new ExBase(hatAppleYen) , 20,
								new ExBase(hatOrangeYen), 40,
								new ExBase(hatBananaYen), 60,
								new ExBase(nohatCashYen), 120,
								new ExBase(nohatMelonYen), 0,
								new ExBase(hatMelonYen)  , 0
							);
		
		// null
		boolean coughtException;
		try {
			testAlge.containsAllBases(null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		
		// Empty
		boolean ret = testAlge.containsAllBases(new ExBaseSet());
		assertTrue(ret);
		
		// test
		//---
		ExBaseSet test1 = new ExBaseSet(Arrays.asList(new ExBase(nohatAppleYen), new ExBase(hatAppleYen)));
		ret = testAlge.containsAllBases(test1);
		assertFalse(ret);
		//---
		ExBaseSet test2 = new ExBaseSet(Arrays.asList(new ExBase(nohatCashYen), new ExBase(hatCashYen)));
		ret = testAlge.containsAllBases(test2);
		assertFalse(ret);
		//---
		ExBaseSet test3 = new ExBaseSet(Arrays.asList(new ExBase(nohatMelonYen), new ExBase(hatMelonYen)));
		ret = testAlge.containsAllBases(test3);
		assertTrue(ret);
		//---
		ExBaseSet test4 = testAlge.getBases();
		ret = testAlge.containsAllBases(test4);
		assertTrue(ret);
	}

	/**
	 * {@link exalge2.Exalge#getNoHatBases()} のためのテスト・メソッド。
	 * @since 0.970
	 */
	public void testGetNoHatBases() {
		Exalge testAlge = makeAlge(
				new ExBase(hatAppleYen) , 20,
				new ExBase(hatOrangeYen), 40,
				new ExBase(hatBananaYen), 60,
				new ExBase(nohatCashYen), 120,
				new ExBase(nohatMelonYen), 0,
				new ExBase(hatMelonYen)  , 0
			);
		ExBaseSet retBases;
		
		// Empty
		Exalge algeEmpty = new Exalge();
		retBases = algeEmpty.getNoHatBases();
		assertTrue(retBases.isEmpty());
		
		// alge
		ExBaseSet ansBases = makeBaseSet(new ExBase(nohatCashYen), new ExBase(nohatMelonYen));
		retBases = testAlge.getNoHatBases();
		assertEquals(retBases, ansBases);
	}

	/**
	 * {@link exalge2.Exalge#getHatBases()} のためのテスト・メソッド。
	 * @since 0.970
	 */
	public void testGetHatBases() {
		Exalge testAlge = makeAlge(
				new ExBase(hatAppleYen) , 20,
				new ExBase(hatOrangeYen), 40,
				new ExBase(hatBananaYen), 60,
				new ExBase(nohatCashYen), 120,
				new ExBase(nohatMelonYen), 0,
				new ExBase(hatMelonYen)  , 0
			);
		ExBaseSet retBases;
		
		// Empty
		Exalge algeEmpty = new Exalge();
		retBases = algeEmpty.getHatBases();
		assertTrue(retBases.isEmpty());
		
		// alge
		ExBaseSet ansBases = makeBaseSet(new ExBase(hatAppleYen), new ExBase(hatOrangeYen), new ExBase(hatBananaYen), new ExBase(hatMelonYen));
		retBases = testAlge.getHatBases();
		assertEquals(retBases, ansBases);
	}

	/**
	 * {@link exalge2.Exalge#getOneBase()} のためのテスト・メソッド。
	 * @since 0.970
	 */
	public void testGetBase() {
		ExBase nAppleYen = new ExBase(nohatAppleYen);
		ExBase hAppleYen = new ExBase(hatAppleYen);
		ExBase base;
		
		// Empty
		boolean coughtException;
		Exalge algeEmpty = new Exalge();
		try {
			algeEmpty.getOneBase();
			coughtException = false;
		} catch (NoSuchElementException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		
		// Only one
		Exalge alge1 = makeAlge(nAppleYen, 10);
		base = alge1.getOneBase();
		assertEquals(base, nAppleYen);
		
		Exalge alge2 = makeAlge(hAppleYen, 20);
		base = alge2.getOneBase();
		assertEquals(base, hAppleYen);
		
		// get top base
		Exalge alge3 = makeAlge(nAppleYen, 10, hAppleYen, 20);
		base = alge3.getOneBase();
		assertEquals(base, nAppleYen);
		
		Exalge alge4 = makeAlge(hAppleYen, 20, nAppleYen, 10);
		base = alge4.getOneBase();
		assertEquals(base, hAppleYen);
	}

	/**
	 * {@link exalge2.Exalge#getBasesWithRemoveHat()} のためのテスト・メソッド。
	 * @since 0.970
	 */
	public void testGetBasesWithRemoveHat() {
		Exalge testAlge = makeAlge(
				new ExBase(hatAppleYen) , 20,
				new ExBase(hatOrangeYen), 40,
				new ExBase(hatBananaYen), 60,
				new ExBase(nohatCashYen), 120,
				new ExBase(nohatMelonYen), 0,
				new ExBase(hatMelonYen)  , 0
			);
		ExBase[] bases = new ExBase[]{
			new ExBase(nohatAppleYen),
			new ExBase(nohatOrangeYen),
			new ExBase(nohatBananaYen),
			new ExBase(nohatCashYen),
			new ExBase(nohatMelonYen),
		};
		ExBaseSet retBases;
		
		// Empty
		Exalge algeEmpty = new Exalge();
		retBases = algeEmpty.getBasesWithRemoveHat();
		assertTrue(retBases.isEmpty());
		
		// alge
		ExBaseSet ansBases = makeBaseSet(bases);
		retBases = testAlge.getBasesWithRemoveHat();
		assertEquals(retBases, ansBases);
	}

	/**
	 * {@link exalge2.Exalge#getBasesWithSetHat()} のためのテスト・メソッド。
	 * @since 0.970
	 */
	public void testGetBasesWithSetHat() {
		Exalge testAlge = makeAlge(
				new ExBase(hatAppleYen) , 20,
				new ExBase(hatOrangeYen), 40,
				new ExBase(hatBananaYen), 60,
				new ExBase(nohatCashYen), 120,
				new ExBase(nohatMelonYen), 0,
				new ExBase(hatMelonYen)  , 0
			);
		ExBase[] bases = new ExBase[]{
			new ExBase(hatAppleYen),
			new ExBase(hatOrangeYen),
			new ExBase(hatBananaYen),
			new ExBase(hatCashYen),
			new ExBase(hatMelonYen),
		};
		ExBaseSet retBases;
		
		// Empty
		Exalge algeEmpty = new Exalge();
		retBases = algeEmpty.getBasesWithSetHat();
		assertTrue(retBases.isEmpty());
		
		// alge
		ExBaseSet ansBases = makeBaseSet(bases);
		retBases = testAlge.getBasesWithSetHat();
		assertEquals(retBases, ansBases);
	}

	/**
	 * {@link exalge2.Exalge#makeBaseKeyTargetSet(String[])} のためのテスト・メソッド。
	 * @since 0.970
	 */
	public void testMakeBaseKeyTargetSetString() {
		Exalge testAlge = makeAlge(
				new ExBase(hatAppleYen) , 20,
				new ExBase(hatOrangeYen), 40,
				new ExBase(hatBananaYen), 60,
				new ExBase(nohatCashYen), 120,
				new ExBase(nohatMelonYen), 0,
				new ExBase(hatMelonYen)  , 0
			);
		
		// null
		boolean coughtException;
		try {
			testAlge.makeBaseKeyTargetSet((String[])null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		
		// empty
		Set<String> retSet = testAlge.makeBaseKeyTargetSet();
		assertTrue(retSet.isEmpty());
		
		// one
		String strOne = "hoge";
		retSet = testAlge.makeBaseKeyTargetSet(strOne);
		assertTrue(retSet.size() == 1);
		assertTrue(retSet.contains(strOne));
		
		// any
		String[] strData = {"hoge", "age", "sage", "ugo"};
		retSet = testAlge.makeBaseKeyTargetSet(strData);
		assertTrue(retSet.size() == strData.length);
		assertTrue(retSet.containsAll(Arrays.asList(strData)));
	}
	
	/**
	 * {@link exalge2.Exalge#getNextByBaseKey(Iterator, int, Set)} のためのテスト・メソッド。
	 * @since 0.970
	 */
	public void testGetNextByBaseKeyIteratorIntSet() {
		Exalge testAlge = makeAlge(
				new ExBase(hatAppleYen) , 20,
				new ExBase(hatOrangeYen), 40,
				new ExBase(hatBananaYen), 60,
				new ExBase(nohatCashYen), 120,
				new ExBase(nohatMelonYen), 0,
				new ExBase(hatMelonYen)  , 0,
				new ExBase(nohatAppleYen), 20,
				new ExBase(hatCashYen)   , 20
			);
		String[] strNoNames = {"まぐろ", "かつお"};
		String[] strNames = {"りんご", "マンゴー", "メロン"};
		Set<String> illegalSet = testAlge.makeBaseKeyTargetSet(strNoNames);
		Set<String> nameSet = testAlge.makeBaseKeyTargetSet(strNames);
		
		// null
		boolean coughtException;
		try {
			testAlge.getNextByBaseKey(null, 1, nameSet);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//---
		try {
			testAlge.getNextByBaseKey(testAlge.data.entrySet().iterator(), 1, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//---
		try {
			testAlge.getNextByBaseKey(null, 0, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		
		// out of bounds
		try {
			testAlge.getNextByBaseKey(testAlge.data.entrySet().iterator(), -1, illegalSet);
			coughtException = false;
		} catch (ArrayIndexOutOfBoundsException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//---
		try {
			testAlge.getNextByBaseKey(testAlge.data.entrySet().iterator(), 5, illegalSet);
			coughtException = false;
		} catch (ArrayIndexOutOfBoundsException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);

		// no such element
		try {
			Exalge algeEmpty = new Exalge();
			testAlge.getNextByBaseKey(algeEmpty.data.entrySet().iterator(), 0, illegalSet);
			coughtException = false;
		} catch (NoSuchElementException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		
		// nothing
		Iterator<Map.Entry<ExBase, BigDecimal>> it = testAlge.data.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<ExBase, BigDecimal> entry = testAlge.getNextByBaseKey(it, 0, illegalSet);
			assertTrue(entry == null);
		}
		
		// existing
		Exalge ansAlge = makeAlge(
				new ExBase(hatAppleYen) , 20,
				new ExBase(nohatMelonYen), 0,
				new ExBase(hatMelonYen)  , 0,
				new ExBase(nohatAppleYen), 20
			);
		Exalge retAlge = new Exalge();
		it = testAlge.data.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<ExBase, BigDecimal> entry = testAlge.getNextByBaseKey(it, 0, nameSet);
			if (entry != null) {
				retAlge.putValue(entry.getKey(), entry.getValue());
			}
		}
		assertEquals(retAlge, ansAlge);
	}

	/**
	 * {@link exalge2.Exalge#getBasesByBaseKey(int, java.util.Set)} のためのテスト・メソッド。
	 * @since 0.970
	 */
	public void testGetBasesByBaseKeyIntSet() {
		Exalge testAlge = makeAlge(
				new ExBase("りんご-NO_HAT-YEN-Y2000-果物"), 10,
				new ExBase("みかん-NO_HAT-YEN-Y2000-果物"), 20,
				new ExBase("ばなな-NO_HAT-YEN-Y2000-果物"), 30,
				new ExBase("まぐろ-HAT-YEN-Y2001-魚類")   , 40,
				new ExBase("かつお-HAT-YEN-Y2001-魚類")   , 50,
				new ExBase("現金-HAT-YEN-Y2000-果物")     , 60,
				new ExBase("現金-NO_HAT-YEN-Y2001-魚類")  , 90
			);
		
		// null
		boolean coughtException;
		try {
			testAlge.getBasesByBaseKey(0, (Set<String>)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		
		// out of bounds
		Set<String> testSet = testAlge.makeBaseKeyTargetSet("hoge");
		try {
			testAlge.getBasesByBaseKey(-1, testSet);
			coughtException = false;
		} catch (ArrayIndexOutOfBoundsException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//---
		try {
			testAlge.getBasesByBaseKey(5, testSet);
			coughtException = false;
		} catch (ArrayIndexOutOfBoundsException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		
		// empty
		Exalge algeEmpty = new Exalge();
		Set<String> testExistSet = testAlge.makeBaseKeyTargetSet("現金", ExBase.HAT, "YEN", "Y2000", "果物");
		for (int i = 0; i < ExBase.NUM_ALL_KEYS; i++) {
			ExBaseSet retSet = algeEmpty.getBasesByBaseKey(i, testExistSet);
			assertTrue(retSet.isEmpty());
		}
		
		// exist
		ExBaseSet retBases;
		//--- name
		Set<String> fishNameSet = testAlge.makeBaseKeyTargetSet("まぐろ", "かつお");
		Exalge ansNameAlge = makeAlge(
				new ExBase("まぐろ-HAT-YEN-Y2001-魚類")   , 40,
				new ExBase("かつお-HAT-YEN-Y2001-魚類")   , 50
			);
		retBases = testAlge.getBasesByBaseKey(ExBase.KEY_NAME, fishNameSet);
		assertEquals(retBases, ansNameAlge.getBases());
		//--- hat
		Set<String> nohatSet = testAlge.makeBaseKeyTargetSet(ExBase.NO_HAT);
		Exalge ansHatAlge = makeAlge(
				new ExBase("りんご-NO_HAT-YEN-Y2000-果物"), 10,
				new ExBase("みかん-NO_HAT-YEN-Y2000-果物"), 20,
				new ExBase("ばなな-NO_HAT-YEN-Y2000-果物"), 30,
				new ExBase("現金-NO_HAT-YEN-Y2001-魚類")  , 90
			);
		retBases = testAlge.getBasesByBaseKey(ExBase.KEY_HAT, nohatSet);
		assertEquals(retBases, ansHatAlge.getBases());
		//--- unit
		Set<String> yenSet = testAlge.makeBaseKeyTargetSet("YEN", "円");
		Exalge ansUnitAlge = makeAlge(
				new ExBase("りんご-NO_HAT-YEN-Y2000-果物"), 10,
				new ExBase("みかん-NO_HAT-YEN-Y2000-果物"), 20,
				new ExBase("ばなな-NO_HAT-YEN-Y2000-果物"), 30,
				new ExBase("まぐろ-HAT-YEN-Y2001-魚類")   , 40,
				new ExBase("かつお-HAT-YEN-Y2001-魚類")   , 50,
				new ExBase("現金-HAT-YEN-Y2000-果物")     , 60,
				new ExBase("現金-NO_HAT-YEN-Y2001-魚類")  , 90
			);
		retBases = testAlge.getBasesByBaseKey(ExBase.KEY_EXT_UNIT, yenSet);
		assertEquals(retBases, ansUnitAlge.getBases());
		//--- time
		Set<String> timeSet = testAlge.makeBaseKeyTargetSet("Y1999", "Y2001", "Y2002");
		Exalge ansTimeAlge = makeAlge(
				new ExBase("まぐろ-HAT-YEN-Y2001-魚類")   , 40,
				new ExBase("かつお-HAT-YEN-Y2001-魚類")   , 50,
				new ExBase("現金-NO_HAT-YEN-Y2001-魚類")  , 90
			);
		retBases = testAlge.getBasesByBaseKey(ExBase.KEY_EXT_TIME, timeSet);
		assertEquals(retBases, ansTimeAlge.getBases());
		//--- subject
		Set<String> fruitSet = testAlge.makeBaseKeyTargetSet("フルーツ", "果物");
		Exalge ansSubjectAlge = makeAlge(
				new ExBase("りんご-NO_HAT-YEN-Y2000-果物"), 10,
				new ExBase("みかん-NO_HAT-YEN-Y2000-果物"), 20,
				new ExBase("ばなな-NO_HAT-YEN-Y2000-果物"), 30,
				new ExBase("現金-HAT-YEN-Y2000-果物")     , 60
			);
		retBases = testAlge.getBasesByBaseKey(ExBase.KEY_EXT_SUBJECT, fruitSet);
		assertEquals(retBases, ansSubjectAlge.getBases());
	}

	/**
	 * {@link exalge2.Exalge#projectionByBaseKey(int, java.util.Set)} のためのテスト・メソッド。
	 * @since 0.970
	 */
	public void testProjectionByBaseKeyIntSet() {
		Exalge testAlge = makeAlge(
				new ExBase("りんご-NO_HAT-YEN-Y2000-果物"), 10,
				new ExBase("みかん-NO_HAT-YEN-Y2000-果物"), 20,
				new ExBase("ばなな-NO_HAT-YEN-Y2000-果物"), 30,
				new ExBase("まぐろ-HAT-YEN-Y2001-魚類")   , 40,
				new ExBase("かつお-HAT-YEN-Y2001-魚類")   , 50,
				new ExBase("現金-HAT-YEN-Y2000-果物")     , 60,
				new ExBase("現金-NO_HAT-YEN-Y2001-魚類")  , 90
			);
		
		// null
		boolean coughtException;
		try {
			testAlge.projectionByBaseKey(0, (Set<String>)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		
		// out of bounds
		Set<String> testSet = testAlge.makeBaseKeyTargetSet("hoge");
		try {
			testAlge.projectionByBaseKey(-1, testSet);
			coughtException = false;
		} catch (ArrayIndexOutOfBoundsException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//---
		try {
			testAlge.projectionByBaseKey(5, testSet);
			coughtException = false;
		} catch (ArrayIndexOutOfBoundsException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		
		// empty
		Exalge algeEmpty = new Exalge();
		Set<String> testExistSet = testAlge.makeBaseKeyTargetSet("現金", ExBase.HAT, "YEN", "Y2000", "果物");
		for (int i = 0; i < ExBase.NUM_ALL_KEYS; i++) {
			Exalge retAlge = algeEmpty.projectionByBaseKey(i, testExistSet);
			assertTrue(retAlge.isEmpty());
		}
		
		// exist
		Exalge retAlge;
		//--- name
		Set<String> fishNameSet = testAlge.makeBaseKeyTargetSet("まぐろ", "かつお");
		Exalge ansNameAlge = makeAlge(
				new ExBase("まぐろ-HAT-YEN-Y2001-魚類")   , 40,
				new ExBase("かつお-HAT-YEN-Y2001-魚類")   , 50
			);
		retAlge = testAlge.projectionByBaseKey(ExBase.KEY_NAME, fishNameSet);
		assertEquals(retAlge, ansNameAlge);
		//--- hat
		Set<String> nohatSet = testAlge.makeBaseKeyTargetSet(ExBase.NO_HAT);
		Exalge ansHatAlge = makeAlge(
				new ExBase("りんご-NO_HAT-YEN-Y2000-果物"), 10,
				new ExBase("みかん-NO_HAT-YEN-Y2000-果物"), 20,
				new ExBase("ばなな-NO_HAT-YEN-Y2000-果物"), 30,
				new ExBase("現金-NO_HAT-YEN-Y2001-魚類")  , 90
			);
		retAlge = testAlge.projectionByBaseKey(ExBase.KEY_HAT, nohatSet);
		assertEquals(retAlge, ansHatAlge);
		//--- unit
		Set<String> yenSet = testAlge.makeBaseKeyTargetSet("YEN", "円");
		Exalge ansUnitAlge = makeAlge(
				new ExBase("りんご-NO_HAT-YEN-Y2000-果物"), 10,
				new ExBase("みかん-NO_HAT-YEN-Y2000-果物"), 20,
				new ExBase("ばなな-NO_HAT-YEN-Y2000-果物"), 30,
				new ExBase("まぐろ-HAT-YEN-Y2001-魚類")   , 40,
				new ExBase("かつお-HAT-YEN-Y2001-魚類")   , 50,
				new ExBase("現金-HAT-YEN-Y2000-果物")     , 60,
				new ExBase("現金-NO_HAT-YEN-Y2001-魚類")  , 90
			);
		retAlge = testAlge.projectionByBaseKey(ExBase.KEY_EXT_UNIT, yenSet);
		assertEquals(retAlge, ansUnitAlge);
		//--- time
		Set<String> timeSet = testAlge.makeBaseKeyTargetSet("Y1999", "Y2001", "Y2002");
		Exalge ansTimeAlge = makeAlge(
				new ExBase("まぐろ-HAT-YEN-Y2001-魚類")   , 40,
				new ExBase("かつお-HAT-YEN-Y2001-魚類")   , 50,
				new ExBase("現金-NO_HAT-YEN-Y2001-魚類")  , 90
			);
		retAlge = testAlge.projectionByBaseKey(ExBase.KEY_EXT_TIME, timeSet);
		assertEquals(retAlge, ansTimeAlge);
		//--- subject
		Set<String> fruitSet = testAlge.makeBaseKeyTargetSet("フルーツ", "果物");
		Exalge ansSubjectAlge = makeAlge(
				new ExBase("りんご-NO_HAT-YEN-Y2000-果物"), 10,
				new ExBase("みかん-NO_HAT-YEN-Y2000-果物"), 20,
				new ExBase("ばなな-NO_HAT-YEN-Y2000-果物"), 30,
				new ExBase("現金-HAT-YEN-Y2000-果物")     , 60
			);
		retAlge = testAlge.projectionByBaseKey(ExBase.KEY_EXT_SUBJECT, fruitSet);
		assertEquals(retAlge, ansSubjectAlge);
	}

	/**
	 * {@link exalge2.Exalge#invElement()} のためのテスト・メソッド。
	 * @since 0.970
	 */
	public void testInvElement() {
		ExBase[] testBases = new ExBase[]{
				new ExBase("e0", ExBase.NO_HAT),
				new ExBase("e1", ExBase.NO_HAT),
				new ExBase("e2", ExBase.NO_HAT),
				new ExBase("e3", ExBase.NO_HAT),
				new ExBase("e4", ExBase.NO_HAT),
				new ExBase("e5", ExBase.NO_HAT),
				new ExBase("e6", ExBase.NO_HAT),
				new ExBase("e7", ExBase.NO_HAT),
				new ExBase("e8", ExBase.NO_HAT),
				new ExBase("e9", ExBase.NO_HAT),
		};
		ExBase NE0 = testBases[0].removeHat();
		ExBase HE0 = testBases[0].setHat();
		Exalge ret;
		
		// Empty
		Exalge algeEmpty = new Exalge();
		ret = algeEmpty.invElement();
		assertTrue(algeEmpty.isEmpty());
		assertTrue(ret.isEmpty());
		
		// only NO_HAT
		Exalge test1 = makeAlge(NE0, 4);
		ret = test1.invElement();
		assertEquals(test1, makeAlge(NE0, 4));
		assertEquals(ret, makeAlge(NE0, "0.25"));
		
		// only HAT
		Exalge test2 = makeAlge(HE0, 4);
		ret = test2.invElement();
		assertEquals(test2, makeAlge(HE0, 4));
		assertEquals(ret, makeAlge(HE0, "0.25"));
		
		// NO_HAT & HAT
		Object[][] testPairs = new Object[][]{
				{ NE0, 9, HE0, 4 },
				{ NE0, 4, HE0, 9 },
				{ NE0, 9, HE0, 9 },
				{ NE0, 5, HE0, 0 },
				{ NE0, 0, HE0, 5 },
				{ NE0, 0, HE0, 0 },
		};
		Object[][] ansPairs = new Object[][]{
				{ NE0, "0.2" },
				{ HE0, "0.2" },
				{},
				{ NE0, "0.2" },
				{ HE0, "0.2" },
				{},
		};
		assertEquals(testPairs.length, ansPairs.length);
		for (int i = 0; i < testPairs.length; i++) {
			Exalge org = makeAlge(testPairs[i]);
			Exalge test = makeAlge(testPairs[i]);
			Exalge ans = makeAlge(ansPairs[i]);
			ret = test.invElement();
			assertEquals(test, org);
			assertEquals(ret, ans);
		}
		
		// normal Alge
		Object[] testAlgeData = new Object[]{
				testBases[0].removeHat(), 9, testBases[0].setHat(), 4,
				testBases[1].removeHat(), 4, testBases[1].setHat(), 9,
				testBases[2].removeHat(), 9, testBases[2].setHat(), 9,
				testBases[3].removeHat(), 4,
				testBases[4].removeHat(), 5, testBases[5].setHat(), 0,
				testBases[5].setHat(), 4,
				testBases[6].removeHat(), 0, testBases[6].setHat(), 5,
				testBases[7].removeHat(), 0, testBases[7].setHat(), 0,
		};
		Object[] ansAlgeData = new Object[]{
				testBases[0].removeHat(), "0.2",
				testBases[1].setHat()   , "0.2",
				testBases[3].removeHat(), "0.25",
				testBases[4].removeHat(), "0.2",
				testBases[5].setHat()   , "0.25",
				testBases[6].setHat()   , "0.2",
		};
		Exalge orgAlge = makeAlge(testAlgeData);
		Exalge testAlge = makeAlge(testAlgeData);
		Exalge ansAlge = makeAlge(ansAlgeData);
		ret = testAlge.invElement();
		assertEquals(testAlge, orgAlge);
		assertEquals(ret, ansAlge);
	}

	/**
	 * {@link exalge2.Exalge#divide(Exalge)} のためのテスト・メソッド。
	 * @since 0.970
	 */
	public void testDivideExalge() {
		ExBase[] testBases = new ExBase[]{
				new ExBase("e0", ExBase.NO_HAT),
				new ExBase("e1", ExBase.NO_HAT),
				new ExBase("e2", ExBase.NO_HAT),
				new ExBase("e3", ExBase.NO_HAT),
				new ExBase("e4", ExBase.NO_HAT),
				new ExBase("e5", ExBase.NO_HAT),
				new ExBase("e6", ExBase.NO_HAT),
				new ExBase("e7", ExBase.NO_HAT),
				new ExBase("e8", ExBase.NO_HAT),
				new ExBase("e9", ExBase.NO_HAT),
		};

		boolean coughtException = false;
		ExBase NE0 = testBases[0].removeHat();
		ExBase HE0 = testBases[0].setHat();
		Exalge algeEmpty = new Exalge();
		Exalge algeNE0   = makeAlge(NE0, 4);
		Exalge algeHE0   = makeAlge(HE0, 4);
		Exalge algePair0 = makeAlge(NE0, 10, HE0, 2);
		Exalge algePair1 = makeAlge(NE0, 2, HE0, 10);

		// null
		//---
		try {
			algeEmpty.divide((Exalge)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		//---
		try {
			algeNE0.divide((Exalge)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		
		// Empty
		Exalge ret;
		//--- () / ae = ()
		ret = algeEmpty.divide(algeNE0);
		assertTrue(ret.isEmpty());
		//--- () / a^e = ()
		ret = algeEmpty.divide(algeHE0);
		assertTrue(ret.isEmpty());
		//--- () / ae + b^e = ()
		ret = algeEmpty.divide(algePair0);
		assertTrue(ret.isEmpty());
		//--- ae / () = ()
		ret = algeNE0.divide(algeEmpty);
		assertTrue(ret.isEmpty());
		//--- a^e / () = ()
		ret = algeHE0.divide(algeEmpty);
		assertTrue(ret.isEmpty());
		//--- ae + b^e / () = ()
		ret = algePair0.divide(algeEmpty);
		assertTrue(ret.isEmpty());
		
		// divide one element
		Exalge ans1NE0 = makeAlge(NE0, 1);
		Exalge ans1HE0 = makeAlge(HE0, 1);
		//--- ae / ae = 1ae
		ret = algeNE0.divide(algeNE0);
		assertEquals(ret, ans1NE0);
		//--- ae * a^e = 1a^e
		ret = algeNE0.divide(algeHE0);
		assertEquals(ret, ans1HE0);
		//--- a^e * ae = 1a^e
		ret = algeHE0.divide(algeNE0);
		assertEquals(ret, ans1HE0);
		//--- a^e * a^e = 1ae
		ret = algeHE0.divide(algeHE0);
		assertEquals(ret, ans1NE0);
		
		// divide two element
		Exalge ansAlge1 = makeAlge(NE0, "2.5", HE0, "0.5");
		Exalge ansAlge2 = makeAlge(NE0, "0.5", HE0, "2.5");
		Exalge ansAlge3 = makeAlge(NE0, "0.5");
		Exalge ansAlge4 = makeAlge(HE0, "0.5");
		Exalge ansAlge5 = makeAlge(NE0, "1.25", HE0, "0.25");
		Exalge ansAlge6 = makeAlge(NE0, "0.25", HE0, "1.25");
		//--- 10e + 2^e / 4e  = 2.5e + 0.5^e
		ret = algePair0.divide(algeNE0);
		assertEquals(ret, ansAlge1);
		//--- 2e + 10^e / 4e  = 0.5e + 2.5^e
		ret = algePair1.divide(algeNE0);
		assertEquals(ret, ansAlge2);
		//--- 10e + 2^e / 4^e = 0.5e + 2.5^e
		ret = algePair0.divide(algeHE0);
		assertEquals(ret, ansAlge2);
		//--- 2e + 10^e / 4^e = 2.5e + 0.5^e
		ret = algePair1.divide(algeHE0);
		assertEquals(ret, ansAlge1);
		//--- 4e  / 10e + 2^e = 0.5e
		ret = algeNE0.divide(algePair0);
		assertEquals(ret, ansAlge3);
		//--- 4e  / 2e + 10^e = 0.5^e
		ret = algeNE0.divide(algePair1);
		assertEquals(ret, ansAlge4);
		//--- 4^e / 10e + 2^e = 0.5^e
		ret = algeHE0.divide(algePair0);
		assertEquals(ret, ansAlge4);
		//--- 4^e / 2e + 10^e = 0.5e
		ret = algeHE0.divide(algePair1);
		assertEquals(ret, ansAlge3);
		//--- 10e + 2^e / 10e + 2^e = 1.25e + 0.25^e
		ret = algePair0.divide(algePair0);
		assertEquals(ret, ansAlge5);
		//--- 10e + 2^e / 2e + 10^e = 0.25e + 1.25^e
		ret = algePair0.divide(algePair1);
		assertEquals(ret, ansAlge6);
		//--- 2e + 10^e / 10e + 2^e = 0.25e + 1.25^e
		ret = algePair1.divide(algePair0);
		assertEquals(ret, ansAlge6);
		//--- 2e + 10^e / 2e + 10^e = 1.25e + 0.25^e
		ret = algePair1.divide(algePair1);
		assertEquals(ret, ansAlge5);
		
		// divide alges
		Exalge alge1 = makeAlge(
							testBases[0].removeHat(), 10,
							testBases[0].setHat()   , 20,
							testBases[1].removeHat(), 110,
							testBases[1].setHat()   , 120,
							testBases[2].removeHat(), 210,
							testBases[2].setHat()   , 220,
							testBases[3].removeHat(), 310,
							testBases[3].setHat()   , 320,
							testBases[4].removeHat(), 410,
							testBases[4].setHat()   , 420,
							testBases[5].removeHat(), 510,
							testBases[5].setHat()   , 520,
							testBases[6].removeHat(), 610,
							testBases[6].setHat()   , 620,
							testBases[7].removeHat(), 710,
							testBases[7].setHat()   , 720,
							testBases[8].removeHat(), 810,
							testBases[8].setHat()   , 820
						);
		Exalge alge2 = makeAlge(
							testBases[0].removeHat(), 10,
							testBases[1].setHat()   , 20,
							testBases[2].removeHat(), 40,
							testBases[2].setHat()   , 0,
							testBases[3].removeHat(), 0,
							testBases[3].setHat()   , 50,
							testBases[4].removeHat(), 60,
							testBases[4].setHat()   , 60,
							testBases[6].removeHat(), 1,
							testBases[6].setHat()   , 0,
							testBases[7].removeHat(), 0,
							testBases[7].setHat()   , 1,
							testBases[8].removeHat(), 0,
							testBases[8].setHat()   , 0,
							testBases[9].removeHat(), 20,
							testBases[9].setHat()   , 10
						);
		Exalge algeAns = makeAlge(
							//---  10e0 +  20^e0 / 10e0         = 1e0 + 2^e0
							testBases[0].removeHat(), 1,
							testBases[0].setHat()   , 2,
							//--- 110e1 + 120^e1 /        20^e1 = 6e1 + 5.5^e1
							testBases[1].removeHat(), 6,
							testBases[1].setHat()   , "5.5",
							//--- 210e2 + 220^e2 / 40e2 +  0^e2 = 5.25e2 + 5.5^e2
							testBases[2].removeHat(), "5.25",
							testBases[2].setHat()   , "5.5",
							//--- 310e3 + 320^e3 /  0e3 + 50^e3 = 6.4e3 + 6.2^e3
							testBases[3].removeHat(), "6.4",
							testBases[3].setHat()   , "6.2",
							//--- 410e4 + 420^e4 / 60e4 + 60^e4 = 0e4 + 0^e4 = ()
							//--- 510e5 + 520^e5 / ()           = ()
							//--- 610e6 + 620^e6 /  1e6 +  0^e6 = 610e6 + 620^e6
							testBases[6].removeHat(), 610,
							testBases[6].setHat()   , 620,
							//--- 710e7 + 720^e7 /  0e7 +  1^e7 = 720e7 + 710^e7
							testBases[7].removeHat(), 720,
							testBases[7].setHat()   , 710
							//--- 810e8 + 820^e8 /  0e8 +  0^e8 = 0e8 + 0^e8 = ()
							//---             () / 20e9 + 10^e9 = ()
						);
		Exalge algeRevAns = makeAlge(
				//--- 10e0         /  10e0 +  20^e0 = 1^e0
				testBases[0].setHat()   , 1,
				//---        20^e1 / 110e1 + 120^e1 = 2e1
				testBases[1].removeHat(), 2,
				//--- 40e2 +  0^e2 / 210e2 + 220^e2 = 4^e2
				testBases[2].setHat()   , 4,
				//---  0e3 + 50^e3 / 310e3 + 320^e3 = 5e3
				testBases[3].removeHat(), 5,
				//--- 60e4 + 60^e4 / 410e4 + 420^e4 = 6e4 + 6^e4
				testBases[4].removeHat(), 6,
				testBases[4].setHat()   , 6,
				//--- ()           / 510e5 + 520^e5 = ()
				//---  1e6 +  0^e6 / 610e6 + 620^e6 = 0.1^e6
				testBases[6].setHat()   , "0.1",
				//---  0e7 +  1^e7 / 710e7 + 720^e7 = 0.1e7
				testBases[7].removeHat(), "0.1"
				//---  0e8 +  0^e8 / 810e8 + 820^e8 = 0e8 + 0^e8 = ()
				//--- 20e9 + 10^e9 / ()             = ()
			);
		System.out.println("divide...");
		System.out.println("    alge1 = " + alge1);
		System.out.println("    alge2 = " + alge2);
		System.out.println("  < pattern : alge1/alge2 >");
		System.out.println("    answer = " + algeAns);
		System.out.println("    alge1/alge2 = " + alge1.divide(alge2));
		System.out.println("  < pattern : alge2/alge1 >");
		System.out.println("    answer = " + algeRevAns);
		System.out.println("    alge2/alge1 = " + alge2.divide(alge1));
		//---
		ret = alge1.divide(alge2);
		assertEquals(ret, algeAns);
		//---
		ret = alge2.divide(alge1);
		assertEquals(ret, algeRevAns);
		//---
		ret = alge1.multiple(alge2.invElement());
		assertEquals(ret, algeAns);
		//---
		ret = alge2.multiple(alge1.invElement());
		assertEquals(ret, algeRevAns);
	}

	/**
	 * {@link exalge2.Exalge#transform(ExBase, ExBase)} のためのテスト・メソッド。
	 * @since 0.970
	 */
	public void testTransformExBaseExBase() {
		Object[] data1 = new Object[]{
				new ExBase(nohatAppleYen),	new BigDecimal(10),
				new ExBase(nohatOrangeYen),	new BigDecimal(20),
				new ExBase(nohatBananaYen),	new BigDecimal(30),
				new ExBase(nohatMelonYen),	new BigDecimal(0),
				new ExBase(hatCashYen),		new BigDecimal(60)
		};
		Object[] data2 = new Object[]{
				new ExBase(hatAppleYen),	new BigDecimal(10),
				new ExBase(hatOrangeYen),	new BigDecimal(20),
				new ExBase(hatBananaYen),	new BigDecimal(30),
				new ExBase(hatMangoYen),	new BigDecimal(0),
				new ExBase(nohatCashYen),	new BigDecimal(60)
		};
		Exalge orgAlge1 = new Exalge(data1);
		Exalge orgAlge2 = new Exalge(data2);
		ExBase[] fromPatternData1 = new ExBase[]{
				new ExBase("りんご-NO_HAT-*-*-*"),
				new ExBase("みかん-NO_HAT-*-*-*"),
				new ExBase("メロン-NO_HAT-*-*-*"),
				new ExBase("マンゴー-NO_HAT-*-*-*"),
		};
		ExBase[] fromPatternData2 = new ExBase[]{
				new ExBase("まぐろ-NO_HAT-*-*-*"),
				new ExBase("かつお-NO_HAT-*-*-*"),
		};
		ExBase toPattern = new ExBase("果物-NO_HAT-個-*-*hoge*");
		Exalge algeEmpty = new Exalge();
		Exalge alge1 = new Exalge(data1);
		Exalge alge2 = new Exalge(data2);
		Exalge ret;
		
		// check null
		boolean coughtException;
		try {
			alge1.transform((ExBase)null, toPattern);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			alge1.transform(toPattern, (ExBase)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			alge1.transform((ExBase)null, (ExBase)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			algeEmpty.transform((ExBase)null, toPattern);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			algeEmpty.transform(toPattern, (ExBase)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			algeEmpty.transform((ExBase)null, (ExBase)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		
		// check Empty
		ret = algeEmpty.transform(new ExBase(nohatAppleYen), toPattern);
		assertTrue(algeEmpty.isEmpty());
		assertTrue(ret.isEmpty());
		ret = algeEmpty.transform(fromPatternData1[0], toPattern);
		assertTrue(algeEmpty.isEmpty());
		assertTrue(ret.isEmpty());
		ret = algeEmpty.transform(fromPatternData2[0], toPattern);
		assertTrue(algeEmpty.isEmpty());
		assertTrue(ret.isEmpty());
		
		// for data1
		Exalge[] ansAlge1 = new Exalge[]{
			makeAlge(
					new ExBase(nohatOrangeYen), 20,
					new ExBase(nohatBananaYen), 30,
					new ExBase(nohatMelonYen) ,  0,
					new ExBase(hatCashYen)    , 60,
					new ExBase(nohatFruitNum) , 10
			),
			makeAlge(
					new ExBase(nohatAppleYen) , 10,
					new ExBase(nohatBananaYen), 30,
					new ExBase(nohatMelonYen) ,  0,
					new ExBase(hatCashYen)    , 60,
					new ExBase(nohatFruitNum) , 20
			),
			makeAlge(
					new ExBase(nohatAppleYen) , 10,
					new ExBase(nohatOrangeYen), 20,
					new ExBase(nohatBananaYen), 30,
					new ExBase(hatCashYen)    , 60,
					new ExBase(nohatFruitNum) ,  0
			),
			makeAlge(
					new ExBase(nohatAppleYen) , 10,
					new ExBase(nohatOrangeYen), 20,
					new ExBase(nohatBananaYen), 30,
					new ExBase(nohatMelonYen) ,  0,
					new ExBase(hatCashYen)    , 60
			),
		};
		assertEquals(fromPatternData1.length, ansAlge1.length);
		System.out.println("transform(ExBase, ExBase)...");
		System.out.println("    alge = " + orgAlge1);
		for (int i = 0; i < fromPatternData1.length; i++) {
			ret = alge1.transform(fromPatternData1[i], toPattern);
			assertEquals(alge1, orgAlge1);
			assertEquals(ret, ansAlge1[i]);
			System.out.println("  ----------");
			System.out.println("    fromPattern = " + fromPatternData1[i]);
			System.out.println("    toPattern = " + toPattern);
			System.out.println("    result = " + ret);
		}
		for (ExBase base : fromPatternData2) {
			ret = alge1.transform(base, toPattern);
			assertEquals(alge1, orgAlge1);
			assertEquals(ret, orgAlge1);
		}
		
		// for data2
		Exalge[] ansAlge2 = new Exalge[]{
				makeAlge(
						new ExBase(hatOrangeYen)  , 20,
						new ExBase(hatBananaYen)  , 30,
						new ExBase(hatMangoYen)   ,  0,
						new ExBase(nohatCashYen)  , 60,
						new ExBase(hatFruitNum)   , 10
				),
				makeAlge(
						new ExBase(hatAppleYen)   , 10,
						new ExBase(hatBananaYen)  , 30,
						new ExBase(hatMangoYen)   ,  0,
						new ExBase(nohatCashYen)  , 60,
						new ExBase(hatFruitNum)   , 20
				),
				makeAlge(
						new ExBase(hatAppleYen)   , 10,
						new ExBase(hatOrangeYen)  , 20,
						new ExBase(hatBananaYen)  , 30,
						new ExBase(hatMangoYen)   ,  0,
						new ExBase(nohatCashYen)  , 60
				),
				makeAlge(
						new ExBase(hatAppleYen)   , 10,
						new ExBase(hatOrangeYen)  , 20,
						new ExBase(hatBananaYen)  , 30,
						new ExBase(nohatCashYen)  , 60,
						new ExBase(hatFruitNum)   ,  0
				),
		};
		assertEquals(fromPatternData1.length, ansAlge2.length);
		System.out.println("transform(ExBase, ExBase)...");
		System.out.println("    alge = " + orgAlge2);
		for (int i = 0; i < fromPatternData1.length; i++) {
			ret = alge2.transform(fromPatternData1[i], toPattern);
			assertEquals(alge2, orgAlge2);
			assertEquals(ret, ansAlge2[i]);
			System.out.println("  ----------");
			System.out.println("    fromPattern = " + fromPatternData1[i]);
			System.out.println("    toPattern = " + toPattern);
			System.out.println("    result = " + ret);
		}
		for (ExBase base : fromPatternData2) {
			ret = alge2.transform(base, toPattern);
			assertEquals(alge2, orgAlge2);
			assertEquals(ret, orgAlge2);
		}
	}

	/**
	 * {@link exalge2.Exalge#transform(ExBasePattern, ExBasePattern)} のためのテスト・メソッド。
	 * @since 0.970
	 */
	public void testTransformExBasePatternExBasePattern() {
		Object[] data1 = new Object[]{
				new ExBase(nohatAppleYen),	new BigDecimal(10),
				new ExBase(nohatOrangeYen),	new BigDecimal(20),
				new ExBase(nohatBananaYen),	new BigDecimal(30),
				new ExBase(nohatMelonYen),	new BigDecimal(0),
				new ExBase(hatCashYen),		new BigDecimal(60)
		};
		Object[] data2 = new Object[]{
				new ExBase(hatAppleYen),	new BigDecimal(10),
				new ExBase(hatOrangeYen),	new BigDecimal(20),
				new ExBase(hatBananaYen),	new BigDecimal(30),
				new ExBase(hatMangoYen),	new BigDecimal(0),
				new ExBase(nohatCashYen),	new BigDecimal(60)
		};
		Exalge orgAlge1 = new Exalge(data1);
		Exalge orgAlge2 = new Exalge(data2);
		ExBasePattern[] fromPatternData1 = new ExBasePattern[]{
				new ExBasePattern("りんご-NO_HAT-*-*-*"),
				new ExBasePattern("みかん-NO_HAT-*-*-*"),
				new ExBasePattern("メロン-NO_HAT-*-*-*"),
				new ExBasePattern("マンゴー-NO_HAT-*-*-*"),
		};
		ExBasePattern[] fromPatternData2 = new ExBasePattern[]{
				new ExBasePattern("りんご-*-*-*-*"),
				new ExBasePattern("みかん-*-*-*-*"),
				new ExBasePattern("メロン-*-*-*-*"),
				new ExBasePattern("マンゴー-*-*-*-*"),
		};
		ExBasePattern[] fromPatternData3 = new ExBasePattern[]{
				new ExBasePattern("まぐろ-*-*-*-*"),
				new ExBasePattern("かつお-*-*-*-*"),
		};
		ExBasePattern toPattern = new ExBasePattern("果物-*-個-*-*hoge*");
		Exalge algeEmpty = new Exalge();
		Exalge alge1 = new Exalge(data1);
		Exalge alge2 = new Exalge(data2);
		Exalge ret;
		
		// check null
		boolean coughtException;
		try {
			alge1.transform((ExBasePattern)null, toPattern);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			alge1.transform(toPattern, (ExBasePattern)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			alge1.transform((ExBasePattern)null, (ExBasePattern)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			algeEmpty.transform((ExBasePattern)null, toPattern);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			algeEmpty.transform(toPattern, (ExBasePattern)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			algeEmpty.transform((ExBasePattern)null, (ExBasePattern)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		
		// check Empty
		ret = algeEmpty.transform(fromPatternData1[0], toPattern);
		assertTrue(algeEmpty.isEmpty());
		assertTrue(ret.isEmpty());
		ret = algeEmpty.transform(fromPatternData2[0], toPattern);
		assertTrue(algeEmpty.isEmpty());
		assertTrue(ret.isEmpty());
		ret = algeEmpty.transform(fromPatternData3[0], toPattern);
		assertTrue(algeEmpty.isEmpty());
		assertTrue(ret.isEmpty());
		
		// for data1
		Exalge[] ansAlge11 = new Exalge[]{
				makeAlge(
						new ExBase(nohatOrangeYen), 20,
						new ExBase(nohatBananaYen), 30,
						new ExBase(nohatMelonYen) ,  0,
						new ExBase(hatCashYen)    , 60,
						new ExBase(nohatFruitNum) , 10
				),
				makeAlge(
						new ExBase(nohatAppleYen) , 10,
						new ExBase(nohatBananaYen), 30,
						new ExBase(nohatMelonYen) ,  0,
						new ExBase(hatCashYen)    , 60,
						new ExBase(nohatFruitNum) , 20
				),
				makeAlge(
						new ExBase(nohatAppleYen) , 10,
						new ExBase(nohatOrangeYen), 20,
						new ExBase(nohatBananaYen), 30,
						new ExBase(hatCashYen)    , 60,
						new ExBase(nohatFruitNum) ,  0
				),
				makeAlge(
						new ExBase(nohatAppleYen) , 10,
						new ExBase(nohatOrangeYen), 20,
						new ExBase(nohatBananaYen), 30,
						new ExBase(nohatMelonYen) ,  0,
						new ExBase(hatCashYen)    , 60
				),
			};
		Exalge[] ansAlge12 = new Exalge[]{
				makeAlge(
						new ExBase(nohatOrangeYen), 20,
						new ExBase(nohatBananaYen), 30,
						new ExBase(nohatMelonYen) ,  0,
						new ExBase(hatCashYen)    , 60,
						new ExBase(nohatFruitNum) , 10
				),
				makeAlge(
						new ExBase(nohatAppleYen) , 10,
						new ExBase(nohatBananaYen), 30,
						new ExBase(nohatMelonYen) ,  0,
						new ExBase(hatCashYen)    , 60,
						new ExBase(nohatFruitNum) , 20
				),
				makeAlge(
						new ExBase(nohatAppleYen) , 10,
						new ExBase(nohatOrangeYen), 20,
						new ExBase(nohatBananaYen), 30,
						new ExBase(hatCashYen)    , 60,
						new ExBase(nohatFruitNum) ,  0
				),
				makeAlge(
						new ExBase(nohatAppleYen) , 10,
						new ExBase(nohatOrangeYen), 20,
						new ExBase(nohatBananaYen), 30,
						new ExBase(nohatMelonYen) ,  0,
						new ExBase(hatCashYen)    , 60
				),
			};
		assertEquals(fromPatternData1.length, ansAlge11.length);
		assertEquals(fromPatternData2.length, ansAlge12.length);
		System.out.println("transform(ExBasePatternSet, ExBasePattern)...");
		System.out.println("    alge = " + orgAlge1);
		for (int i = 0; i < fromPatternData1.length; i++) {
			ret = alge1.transform(fromPatternData1[i], toPattern);
			assertEquals(alge1, orgAlge1);
			assertEquals(ret, ansAlge11[i]);
			System.out.println("  ----------");
			System.out.println("    fromPattern = " + fromPatternData1[i]);
			System.out.println("    toPattern = " + toPattern);
			System.out.println("    result = " + ret);
		}
		for (int i = 0; i < fromPatternData2.length; i++) {
			ret = alge1.transform(fromPatternData2[i], toPattern);
			assertEquals(alge1, orgAlge1);
			assertEquals(ret, ansAlge12[i]);
			System.out.println("  ----------");
			System.out.println("    fromPattern = " + fromPatternData2[i]);
			System.out.println("    toPattern = " + toPattern);
			System.out.println("    result = " + ret);
		}
		for (ExBasePattern pattern : fromPatternData3) {
			ret = alge1.transform(pattern, toPattern);
			assertEquals(alge1, orgAlge1);
			assertEquals(ret, orgAlge1);
		}
		
		// for data2
		Exalge[] ansAlge21 = new Exalge[]{
				makeAlge(
						new ExBase(hatAppleYen)   , 10,
						new ExBase(hatOrangeYen)  , 20,
						new ExBase(hatBananaYen)  , 30,
						new ExBase(hatMangoYen)   ,  0,
						new ExBase(nohatCashYen)  , 60
				),
				makeAlge(
						new ExBase(hatAppleYen)   , 10,
						new ExBase(hatOrangeYen)  , 20,
						new ExBase(hatBananaYen)  , 30,
						new ExBase(hatMangoYen)   ,  0,
						new ExBase(nohatCashYen)  , 60
				),
				makeAlge(
						new ExBase(hatAppleYen)   , 10,
						new ExBase(hatOrangeYen)  , 20,
						new ExBase(hatBananaYen)  , 30,
						new ExBase(hatMangoYen)   ,  0,
						new ExBase(nohatCashYen)  , 60
				),
				makeAlge(
						new ExBase(hatAppleYen)   , 10,
						new ExBase(hatOrangeYen)  , 20,
						new ExBase(hatBananaYen)  , 30,
						new ExBase(hatMangoYen)   ,  0,
						new ExBase(nohatCashYen)  , 60
				),
		};
		Exalge[] ansAlge22 = new Exalge[]{
				makeAlge(
						new ExBase(hatOrangeYen)  , 20,
						new ExBase(hatBananaYen)  , 30,
						new ExBase(hatMangoYen)   ,  0,
						new ExBase(nohatCashYen)  , 60,
						new ExBase(hatFruitNum)   , 10
				),
				makeAlge(
						new ExBase(hatAppleYen)   , 10,
						new ExBase(hatBananaYen)  , 30,
						new ExBase(hatMangoYen)   ,  0,
						new ExBase(nohatCashYen)  , 60,
						new ExBase(hatFruitNum)   , 20
				),
				makeAlge(
						new ExBase(hatAppleYen)   , 10,
						new ExBase(hatOrangeYen)  , 20,
						new ExBase(hatBananaYen)  , 30,
						new ExBase(hatMangoYen)   ,  0,
						new ExBase(nohatCashYen)  , 60
				),
				makeAlge(
						new ExBase(hatAppleYen)   , 10,
						new ExBase(hatOrangeYen)  , 20,
						new ExBase(hatBananaYen)  , 30,
						new ExBase(nohatCashYen)  , 60,
						new ExBase(hatFruitNum)   ,  0
				),
		};
		assertEquals(fromPatternData1.length, ansAlge21.length);
		assertEquals(fromPatternData2.length, ansAlge22.length);
		System.out.println("transform(ExBasePatternSet, ExBasePattern)...");
		System.out.println("    alge = " + orgAlge2);
		for (int i = 0; i < fromPatternData1.length; i++) {
			ret = alge2.transform(fromPatternData1[i], toPattern);
			assertEquals(alge2, orgAlge2);
			assertEquals(ret, ansAlge21[i]);
			System.out.println("  ----------");
			System.out.println("    fromPattern = " + fromPatternData1[i]);
			System.out.println("    toPattern = " + toPattern);
			System.out.println("    result = " + ret);
		}
		for (int i = 0; i < fromPatternData2.length; i++) {
			ret = alge2.transform(fromPatternData2[i], toPattern);
			assertEquals(alge2, orgAlge2);
			assertEquals(ret, ansAlge22[i]);
			System.out.println("  ----------");
			System.out.println("    fromPattern = " + fromPatternData2[i]);
			System.out.println("    toPattern = " + toPattern);
			System.out.println("    result = " + ret);
		}
		for (ExBasePattern pattern : fromPatternData3) {
			ret = alge2.transform(pattern, toPattern);
			assertEquals(alge2, orgAlge2);
			assertEquals(ret, orgAlge2);
		}
	}

	/**
	 * {@link exalge2.Exalge#transform(ExBaseSet, ExBase)} のためのテスト・メソッド。
	 * @since 0.970
	 */
	public void testTransformExBaseSetExBase() {
		Object[] data1 = new Object[]{
				new ExBase(nohatAppleYen),	new BigDecimal(10),
				new ExBase(nohatOrangeYen),	new BigDecimal(20),
				new ExBase(nohatBananaYen),	new BigDecimal(30),
				new ExBase(nohatMelonYen),	new BigDecimal(0),
				new ExBase(hatCashYen),		new BigDecimal(60)
		};
		Object[] data2 = new Object[]{
				new ExBase(hatAppleYen),	new BigDecimal(10),
				new ExBase(hatOrangeYen),	new BigDecimal(20),
				new ExBase(hatBananaYen),	new BigDecimal(30),
				new ExBase(hatMangoYen),	new BigDecimal(0),
				new ExBase(nohatCashYen),	new BigDecimal(60)
		};
		Exalge orgAlge1 = new Exalge(data1);
		Exalge orgAlge2 = new Exalge(data2);
		ExBase[] fromPatternData1 = new ExBase[]{
				new ExBase("りんご-NO_HAT-*-*-*"),
				new ExBase("みかん-NO_HAT-*-*-*"),
				new ExBase("メロン-NO_HAT-*-*-*"),
				new ExBase("マンゴー-NO_HAT-*-*-*"),
				new ExBase("まぐろ-NO_HAT-*-*-*"),
				new ExBase("かつお-NO_HAT-*-*-*"),
		};
		ExBase[] fromPatternData2 = new ExBase[]{
				new ExBase("まぐろ-NO_HAT-*-*-*"),
				new ExBase("かつお-NO_HAT-*-*-*"),
		};
		ExBase toPattern = new ExBase("果物-NO_HAT-個-*-*hoge*");
		Exalge algeEmpty = new Exalge();
		Exalge alge1 = new Exalge(data1);
		Exalge alge2 = new Exalge(data2);
		ExBaseSet fromPatterns0 = new ExBaseSet();
		ExBaseSet fromPatterns1 = makeBaseSet(fromPatternData1);
		ExBaseSet fromPatterns2 = makeBaseSet(fromPatternData2);
		Exalge ret;
		
		// check null
		boolean coughtException;
		try {
			alge1.transform((ExBaseSet)null, toPattern);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			alge1.transform(fromPatterns1, (ExBase)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			alge1.transform((ExBaseSet)null, (ExBase)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			algeEmpty.transform((ExBaseSet)null, toPattern);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			algeEmpty.transform(fromPatterns1, (ExBase)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			algeEmpty.transform((ExBaseSet)null, (ExBase)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		
		// check Empty
		ret = algeEmpty.transform(fromPatterns0, toPattern);
		assertTrue(algeEmpty.isEmpty());
		assertTrue(ret.isEmpty());
		ret = algeEmpty.transform(fromPatterns1, toPattern);
		assertTrue(algeEmpty.isEmpty());
		assertTrue(ret.isEmpty());
		ret = algeEmpty.transform(fromPatterns2, toPattern);
		assertTrue(algeEmpty.isEmpty());
		assertTrue(ret.isEmpty());
		
		// for data1
		Exalge ansAlge1 = makeAlge(
							new ExBase(nohatBananaYen), 30,
							new ExBase(hatCashYen)    , 60,
							new ExBase(nohatFruitNum) , 10,
							new ExBase(nohatFruitNum) , 20,
							new ExBase(nohatFruitNum) ,  0
						  );
		ret = alge1.transform(fromPatterns0, toPattern);
		assertEquals(alge1, orgAlge1);
		assertEquals(ret, orgAlge1);
		ret = alge1.transform(fromPatterns1, toPattern);
		assertEquals(alge1, orgAlge1);
		assertEquals(ret, ansAlge1);
		System.out.println("transform(ExBaseSet, ExBase)...");
		System.out.println("    alge = " + orgAlge1);
		System.out.println("    fromPatterns = " + fromPatterns1);
		System.out.println("    toPattern = " + toPattern);
		System.out.println("    result = " + ret);
		ret = alge1.transform(fromPatterns2, toPattern);
		assertEquals(alge1, orgAlge1);
		assertEquals(ret, orgAlge1);
		assertTrue(fromPatterns0.isEmpty());
		assertEquals(fromPatterns1, makeBaseSet(fromPatternData1));
		assertEquals(fromPatterns2, makeBaseSet(fromPatternData2));
		
		// for data2
		Exalge ansAlge2 = makeAlge(
							new ExBase(hatBananaYen)  , 30,
							new ExBase(nohatCashYen)  , 60,
							new ExBase(hatFruitNum)   , 10,
							new ExBase(hatFruitNum)   , 20,
							new ExBase(hatFruitNum)   ,  0
						  );
		ret = alge2.transform(fromPatterns0, toPattern);
		assertEquals(alge2, orgAlge2);
		assertEquals(ret, orgAlge2);
		ret = alge2.transform(fromPatterns1, toPattern);
		assertEquals(alge2, orgAlge2);
		assertEquals(ret, ansAlge2);
		System.out.println("transform(ExBaseSet, ExBase)...");
		System.out.println("    alge = " + orgAlge2);
		System.out.println("    fromPatterns = " + fromPatterns1);
		System.out.println("    toPattern = " + toPattern);
		System.out.println("    result = " + ret);
		ret = alge2.transform(fromPatterns2, toPattern);
		assertEquals(alge2, orgAlge2);
		assertEquals(ret, orgAlge2);
		assertTrue(fromPatterns0.isEmpty());
		assertEquals(fromPatterns1, makeBaseSet(fromPatternData1));
		assertEquals(fromPatterns2, makeBaseSet(fromPatternData2));
	}

	/**
	 * {@link exalge2.Exalge#transform(ExBasePatternSet, ExBasePattern)} のためのテスト・メソッド。
	 * @since 0.970
	 */
	public void testTransformExBasePatternSetExBasePattern() {
		Object[] data1 = new Object[]{
				new ExBase(nohatAppleYen),	new BigDecimal(10),
				new ExBase(nohatOrangeYen),	new BigDecimal(20),
				new ExBase(nohatBananaYen),	new BigDecimal(30),
				new ExBase(nohatMelonYen),	new BigDecimal(0),
				new ExBase(hatCashYen),		new BigDecimal(60)
		};
		Object[] data2 = new Object[]{
				new ExBase(hatAppleYen),	new BigDecimal(10),
				new ExBase(hatOrangeYen),	new BigDecimal(20),
				new ExBase(hatBananaYen),	new BigDecimal(30),
				new ExBase(hatMangoYen),	new BigDecimal(0),
				new ExBase(nohatCashYen),	new BigDecimal(60)
		};
		Exalge orgAlge1 = new Exalge(data1);
		Exalge orgAlge2 = new Exalge(data2);
		ExBasePattern[] fromPatternData1 = new ExBasePattern[]{
				new ExBasePattern("りんご-NO_HAT-*-*-*"),
				new ExBasePattern("みかん-NO_HAT-*-*-*"),
				new ExBasePattern("メロン-NO_HAT-*-*-*"),
				new ExBasePattern("マンゴー-NO_HAT-*-*-*"),
				new ExBasePattern("まぐろ-NO_HAT-*-*-*"),
				new ExBasePattern("かつお-NO_HAT-*-*-*"),
		};
		ExBasePattern[] fromPatternData2 = new ExBasePattern[]{
				new ExBasePattern("りんご-*-*-*-*"),
				new ExBasePattern("みかん-*-*-*-*"),
				new ExBasePattern("メロン-*-*-*-*"),
				new ExBasePattern("マンゴー-*-*-*-*"),
				new ExBasePattern("まぐろ-*-*-*-*"),
				new ExBasePattern("かつお-*-*-*-*"),
		};
		ExBasePattern[] fromPatternData3 = new ExBasePattern[]{
				new ExBasePattern("まぐろ-*-*-*-*"),
				new ExBasePattern("かつお-*-*-*-*"),
		};
		ExBasePattern toPattern = new ExBasePattern("果物-*-個-*-*hoge*");
		Exalge algeEmpty = new Exalge();
		Exalge alge1 = new Exalge(data1);
		Exalge alge2 = new Exalge(data2);
		ExBasePatternSet fromPatterns0 = new ExBasePatternSet();
		ExBasePatternSet fromPatterns1 = new ExBasePatternSet(Arrays.asList(fromPatternData1));
		ExBasePatternSet fromPatterns2 = new ExBasePatternSet(Arrays.asList(fromPatternData2));
		ExBasePatternSet fromPatterns3 = new ExBasePatternSet(Arrays.asList(fromPatternData3));
		Exalge ret;
		
		// check null
		boolean coughtException;
		try {
			alge1.transform((ExBasePatternSet)null, toPattern);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			alge1.transform(fromPatterns1, (ExBasePattern)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			alge1.transform((ExBasePatternSet)null, (ExBasePattern)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			algeEmpty.transform((ExBasePatternSet)null, toPattern);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			algeEmpty.transform(fromPatterns1, (ExBasePattern)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			algeEmpty.transform((ExBasePatternSet)null, (ExBasePattern)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		
		// check Empty
		ret = algeEmpty.transform(fromPatterns0, toPattern);
		assertTrue(algeEmpty.isEmpty());
		assertTrue(ret.isEmpty());
		ret = algeEmpty.transform(fromPatterns1, toPattern);
		assertTrue(algeEmpty.isEmpty());
		assertTrue(ret.isEmpty());
		ret = algeEmpty.transform(fromPatterns2, toPattern);
		assertTrue(algeEmpty.isEmpty());
		assertTrue(ret.isEmpty());
		
		// for data1
		Exalge ansAlge11 = makeAlge(
							new ExBase(nohatBananaYen), 30,
							new ExBase(hatCashYen)    , 60,
							new ExBase(nohatFruitNum) , 10,
							new ExBase(nohatFruitNum) , 20,
							new ExBase(nohatFruitNum) ,  0
			  			  );
		Exalge ansAlge12 = makeAlge(
							new ExBase(nohatBananaYen), 30,
							new ExBase(hatCashYen)    , 60,
							new ExBase(nohatFruitNum) , 10,
							new ExBase(nohatFruitNum) , 20,
							new ExBase(nohatFruitNum) ,  0
						  );
		System.out.println("transform(ExBasePatternSet, ExBasePattern)...");
		System.out.println("    alge = " + orgAlge1);
		ret = alge1.transform(fromPatterns0, toPattern);
		assertEquals(alge1, orgAlge1);
		assertEquals(ret, orgAlge1);
		ret = alge1.transform(fromPatterns1, toPattern);
		assertEquals(alge1, orgAlge1);
		assertEquals(ret, ansAlge11);
		System.out.println("  ----------");
		System.out.println("    fromPatterns = " + fromPatterns1);
		System.out.println("    toPattern = " + toPattern);
		System.out.println("    result = " + ret);
		ret = alge1.transform(fromPatterns2, toPattern);
		assertEquals(alge1, orgAlge1);
		assertEquals(ret, ansAlge12);
		System.out.println("  ----------");
		System.out.println("    fromPatterns = " + fromPatterns2);
		System.out.println("    toPattern = " + toPattern);
		System.out.println("    result = " + ret);
		ret = alge1.transform(fromPatterns3, toPattern);
		assertEquals(alge1, orgAlge1);
		assertEquals(ret, orgAlge1);
		assertTrue(fromPatterns0.isEmpty());
		assertEquals(fromPatterns1, new ExBasePatternSet(Arrays.asList(fromPatternData1)));
		assertEquals(fromPatterns2, new ExBasePatternSet(Arrays.asList(fromPatternData2)));
		assertEquals(fromPatterns3, new ExBasePatternSet(Arrays.asList(fromPatternData3)));
		
		// for data2
		Exalge ansAlge21 = makeAlge(
							new ExBase(hatAppleYen)   , 10,
							new ExBase(hatOrangeYen)  , 20,
							new ExBase(hatBananaYen)  , 30,
							new ExBase(hatMangoYen)   ,  0,
							new ExBase(nohatCashYen)  , 60
						  );
		Exalge ansAlge22 = makeAlge(
							new ExBase(hatBananaYen)  , 30,
							new ExBase(nohatCashYen)  , 60,
							new ExBase(hatFruitNum)   , 10,
							new ExBase(hatFruitNum)   , 20,
							new ExBase(hatFruitNum)   ,  0
			  			  );
		System.out.println("transform(ExBasePatternSet, ExBasePattern)...");
		System.out.println("    alge = " + orgAlge2);
		ret = alge2.transform(fromPatterns0, toPattern);
		assertEquals(alge2, orgAlge2);
		assertEquals(ret, orgAlge2);
		ret = alge2.transform(fromPatterns1, toPattern);
		assertEquals(alge2, orgAlge2);
		assertEquals(ret, ansAlge21);
		System.out.println("  ----------");
		System.out.println("    fromPatterns = " + fromPatterns1);
		System.out.println("    toPattern = " + toPattern);
		System.out.println("    result = " + ret);
		ret = alge2.transform(fromPatterns2, toPattern);
		assertEquals(alge2, orgAlge2);
		assertEquals(ret, ansAlge22);
		System.out.println("  ----------");
		System.out.println("    fromPatterns = " + fromPatterns2);
		System.out.println("    toPattern = " + toPattern);
		System.out.println("    result = " + ret);
		ret = alge2.transform(fromPatterns3, toPattern);
		assertEquals(alge2, orgAlge2);
		assertEquals(ret, orgAlge2);
		assertTrue(fromPatterns0.isEmpty());
		assertEquals(fromPatterns1, new ExBasePatternSet(Arrays.asList(fromPatternData1)));
		assertEquals(fromPatterns2, new ExBasePatternSet(Arrays.asList(fromPatternData2)));
		assertEquals(fromPatterns3, new ExBasePatternSet(Arrays.asList(fromPatternData3)));
	}

	/**
	 * {@link exalge2.Exalge#aggreTransfer(ExBase, ExBase)} のためのテスト・メソッド。
	 * @since 0.970
	 */
	public void testAggreTransferExBaseExBase() {
		Object[] data1 = new Object[]{
				new ExBase(nohatAppleYen),	new BigDecimal(10),
				new ExBase(nohatOrangeYen),	new BigDecimal(20),
				new ExBase(nohatBananaYen),	new BigDecimal(30),
				new ExBase(nohatMelonYen),	new BigDecimal(0),
				new ExBase(hatCashYen),		new BigDecimal(60)
		};
		Object[] data2 = new Object[]{
				new ExBase(hatAppleYen),	new BigDecimal(10),
				new ExBase(hatOrangeYen),	new BigDecimal(20),
				new ExBase(hatBananaYen),	new BigDecimal(30),
				new ExBase(hatMangoYen),	new BigDecimal(0),
				new ExBase(nohatCashYen),	new BigDecimal(60)
		};
		Exalge orgAlge1 = new Exalge(data1);
		Exalge orgAlge2 = new Exalge(data2);
		ExBase[] fromPatternData1 = new ExBase[]{
				new ExBase("りんご-NO_HAT-*-*-*"),
				new ExBase("みかん-NO_HAT-*-*-*"),
				new ExBase("メロン-NO_HAT-*-*-*"),
				new ExBase("マンゴー-NO_HAT-*-*-*"),
		};
		ExBase[] fromPatternData2 = new ExBase[]{
				new ExBase("まぐろ-NO_HAT-*-*-*"),
				new ExBase("かつお-NO_HAT-*-*-*"),
		};
		ExBase toPattern = new ExBase("果物-NO_HAT-個-*-*hoge*");
		Exalge algeEmpty = new Exalge();
		Exalge alge1 = new Exalge(data1);
		Exalge alge2 = new Exalge(data2);
		Exalge ret;
		
		// check null
		boolean coughtException;
		try {
			alge1.aggreTransfer((ExBase)null, toPattern);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			alge1.aggreTransfer(toPattern, (ExBase)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			alge1.aggreTransfer((ExBase)null, (ExBase)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			algeEmpty.aggreTransfer((ExBase)null, toPattern);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			algeEmpty.aggreTransfer(toPattern, (ExBase)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			algeEmpty.aggreTransfer((ExBase)null, (ExBase)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		
		// check Empty
		ret = algeEmpty.aggreTransfer(new ExBase(nohatAppleYen), toPattern);
		assertTrue(algeEmpty.isEmpty());
		assertTrue(ret.isEmpty());
		ret = algeEmpty.aggreTransfer(fromPatternData1[0], toPattern);
		assertTrue(algeEmpty.isEmpty());
		assertTrue(ret.isEmpty());
		ret = algeEmpty.aggreTransfer(fromPatternData2[0], toPattern);
		assertTrue(algeEmpty.isEmpty());
		assertTrue(ret.isEmpty());
		
		// for data1
		Exalge[] ansAlge1 = new Exalge[]{
			makeAlge(
					new ExBase(nohatAppleYen) , 10,
					new ExBase(nohatOrangeYen), 20,
					new ExBase(nohatBananaYen), 30,
					new ExBase(nohatMelonYen) ,  0,
					new ExBase(hatCashYen)    , 60,
					new ExBase(hatAppleYen)   , 10,
					new ExBase(nohatFruitNum) , 10
			),
			makeAlge(
					new ExBase(nohatAppleYen) , 10,
					new ExBase(nohatOrangeYen), 20,
					new ExBase(nohatBananaYen), 30,
					new ExBase(nohatMelonYen) ,  0,
					new ExBase(hatCashYen)    , 60,
					new ExBase(hatOrangeYen)  , 20,
					new ExBase(nohatFruitNum) , 20
			),
			makeAlge(
					new ExBase(nohatAppleYen) , 10,
					new ExBase(nohatOrangeYen), 20,
					new ExBase(nohatBananaYen), 30,
					new ExBase(nohatMelonYen) ,  0,
					new ExBase(hatCashYen)    , 60,
					new ExBase(hatMelonYen)   ,  0,
					new ExBase(nohatFruitNum) ,  0
			),
			makeAlge(
					new ExBase(nohatAppleYen) , 10,
					new ExBase(nohatOrangeYen), 20,
					new ExBase(nohatBananaYen), 30,
					new ExBase(nohatMelonYen) ,  0,
					new ExBase(hatCashYen)    , 60
			),
		};
		assertEquals(fromPatternData1.length, ansAlge1.length);
		System.out.println("aggreTransfer(ExBase, ExBase)...");
		System.out.println("    alge = " + orgAlge1);
		for (int i = 0; i < fromPatternData1.length; i++) {
			ret = alge1.aggreTransfer(fromPatternData1[i], toPattern);
			assertEquals(alge1, orgAlge1);
			assertEquals(ret, ansAlge1[i]);
			System.out.println("  ----------");
			System.out.println("    fromPattern = " + fromPatternData1[i]);
			System.out.println("    toPattern = " + toPattern);
			System.out.println("    result = " + ret);
		}
		for (ExBase base : fromPatternData2) {
			ret = alge1.aggreTransfer(base, toPattern);
			assertEquals(alge1, orgAlge1);
			assertEquals(ret, orgAlge1);
		}
		
		// for data2
		Exalge[] ansAlge2 = new Exalge[]{
				makeAlge(
						new ExBase(hatAppleYen)   , 10,
						new ExBase(hatOrangeYen)  , 20,
						new ExBase(hatBananaYen)  , 30,
						new ExBase(hatMangoYen)   ,  0,
						new ExBase(nohatCashYen)  , 60,
						new ExBase(nohatAppleYen) , 10,
						new ExBase(hatFruitNum)   , 10
				),
				makeAlge(
						new ExBase(hatAppleYen)   , 10,
						new ExBase(hatOrangeYen)  , 20,
						new ExBase(hatBananaYen)  , 30,
						new ExBase(hatMangoYen)   ,  0,
						new ExBase(nohatCashYen)  , 60,
						new ExBase(nohatOrangeYen), 20,
						new ExBase(hatFruitNum)   , 20
				),
				makeAlge(
						new ExBase(hatAppleYen)   , 10,
						new ExBase(hatOrangeYen)  , 20,
						new ExBase(hatBananaYen)  , 30,
						new ExBase(hatMangoYen)   ,  0,
						new ExBase(nohatCashYen)  , 60
				),
				makeAlge(
						new ExBase(hatAppleYen)   , 10,
						new ExBase(hatOrangeYen)  , 20,
						new ExBase(hatBananaYen)  , 30,
						new ExBase(hatMangoYen)   ,  0,
						new ExBase(nohatCashYen)  , 60,
						new ExBase(nohatMangoYen) ,  0,
						new ExBase(hatFruitNum)   ,  0
				),
		};
		assertEquals(fromPatternData1.length, ansAlge2.length);
		System.out.println("aggreTransfer(ExBase, ExBase)...");
		System.out.println("    alge = " + orgAlge2);
		for (int i = 0; i < fromPatternData1.length; i++) {
			ret = alge2.aggreTransfer(fromPatternData1[i], toPattern);
			assertEquals(alge2, orgAlge2);
			assertEquals(ret, ansAlge2[i]);
			System.out.println("  ----------");
			System.out.println("    fromPattern = " + fromPatternData1[i]);
			System.out.println("    toPattern = " + toPattern);
			System.out.println("    result = " + ret);
		}
		for (ExBase base : fromPatternData2) {
			ret = alge2.aggreTransfer(base, toPattern);
			assertEquals(alge2, orgAlge2);
			assertEquals(ret, orgAlge2);
		}
	}
	
	/**
	 * {@link exalge2.Exalge#aggreTransfer(ExBasePattern, ExBasePattern)} のためのテスト・メソッド。
	 * @since 0.970
	 */
	public void testAggreTransferExBasePatternExBasePattern() {
		Object[] data1 = new Object[]{
				new ExBase(nohatAppleYen),	new BigDecimal(10),
				new ExBase(nohatOrangeYen),	new BigDecimal(20),
				new ExBase(nohatBananaYen),	new BigDecimal(30),
				new ExBase(nohatMelonYen),	new BigDecimal(0),
				new ExBase(hatCashYen),		new BigDecimal(60)
		};
		Object[] data2 = new Object[]{
				new ExBase(hatAppleYen),	new BigDecimal(10),
				new ExBase(hatOrangeYen),	new BigDecimal(20),
				new ExBase(hatBananaYen),	new BigDecimal(30),
				new ExBase(hatMangoYen),	new BigDecimal(0),
				new ExBase(nohatCashYen),	new BigDecimal(60)
		};
		Exalge orgAlge1 = new Exalge(data1);
		Exalge orgAlge2 = new Exalge(data2);
		ExBasePattern[] fromPatternData1 = new ExBasePattern[]{
				new ExBasePattern("りんご-NO_HAT-*-*-*"),
				new ExBasePattern("みかん-NO_HAT-*-*-*"),
				new ExBasePattern("メロン-NO_HAT-*-*-*"),
				new ExBasePattern("マンゴー-NO_HAT-*-*-*"),
		};
		ExBasePattern[] fromPatternData2 = new ExBasePattern[]{
				new ExBasePattern("りんご-*-*-*-*"),
				new ExBasePattern("みかん-*-*-*-*"),
				new ExBasePattern("メロン-*-*-*-*"),
				new ExBasePattern("マンゴー-*-*-*-*"),
		};
		ExBasePattern[] fromPatternData3 = new ExBasePattern[]{
				new ExBasePattern("まぐろ-*-*-*-*"),
				new ExBasePattern("かつお-*-*-*-*"),
		};
		ExBasePattern toPattern = new ExBasePattern("果物-*-個-*-*hoge*");
		Exalge algeEmpty = new Exalge();
		Exalge alge1 = new Exalge(data1);
		Exalge alge2 = new Exalge(data2);
		Exalge ret;
		
		// check null
		boolean coughtException;
		try {
			alge1.aggreTransfer((ExBasePattern)null, toPattern);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			alge1.aggreTransfer(toPattern, (ExBasePattern)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			alge1.aggreTransfer((ExBasePattern)null, (ExBasePattern)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			algeEmpty.aggreTransfer((ExBasePattern)null, toPattern);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			algeEmpty.aggreTransfer(toPattern, (ExBasePattern)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			algeEmpty.aggreTransfer((ExBasePattern)null, (ExBasePattern)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		
		// check Empty
		ret = algeEmpty.aggreTransfer(fromPatternData1[0], toPattern);
		assertTrue(algeEmpty.isEmpty());
		assertTrue(ret.isEmpty());
		ret = algeEmpty.aggreTransfer(fromPatternData2[0], toPattern);
		assertTrue(algeEmpty.isEmpty());
		assertTrue(ret.isEmpty());
		ret = algeEmpty.aggreTransfer(fromPatternData3[0], toPattern);
		assertTrue(algeEmpty.isEmpty());
		assertTrue(ret.isEmpty());
		
		// for data1
		Exalge[] ansAlge11 = new Exalge[]{
				makeAlge(
						new ExBase(nohatAppleYen) , 10,
						new ExBase(nohatOrangeYen), 20,
						new ExBase(nohatBananaYen), 30,
						new ExBase(nohatMelonYen) ,  0,
						new ExBase(hatCashYen)    , 60,
						new ExBase(hatAppleYen)   , 10,
						new ExBase(nohatFruitNum) , 10
				),
				makeAlge(
						new ExBase(nohatAppleYen) , 10,
						new ExBase(nohatOrangeYen), 20,
						new ExBase(nohatBananaYen), 30,
						new ExBase(nohatMelonYen) ,  0,
						new ExBase(hatCashYen)    , 60,
						new ExBase(hatOrangeYen)  , 20,
						new ExBase(nohatFruitNum) , 20
				),
				makeAlge(
						new ExBase(nohatAppleYen) , 10,
						new ExBase(nohatOrangeYen), 20,
						new ExBase(nohatBananaYen), 30,
						new ExBase(nohatMelonYen) ,  0,
						new ExBase(hatCashYen)    , 60,
						new ExBase(hatMelonYen)   ,  0,
						new ExBase(nohatFruitNum) ,  0
				),
				makeAlge(
						new ExBase(nohatAppleYen) , 10,
						new ExBase(nohatOrangeYen), 20,
						new ExBase(nohatBananaYen), 30,
						new ExBase(nohatMelonYen) ,  0,
						new ExBase(hatCashYen)    , 60
				),
			};
		Exalge[] ansAlge12 = new Exalge[]{
				makeAlge(
						new ExBase(nohatAppleYen) , 10,
						new ExBase(nohatOrangeYen), 20,
						new ExBase(nohatBananaYen), 30,
						new ExBase(nohatMelonYen) ,  0,
						new ExBase(hatCashYen)    , 60,
						new ExBase(hatAppleYen)   , 10,
						new ExBase(nohatFruitNum) , 10
				),
				makeAlge(
						new ExBase(nohatAppleYen) , 10,
						new ExBase(nohatOrangeYen), 20,
						new ExBase(nohatBananaYen), 30,
						new ExBase(nohatMelonYen) ,  0,
						new ExBase(hatCashYen)    , 60,
						new ExBase(hatOrangeYen)  , 20,
						new ExBase(nohatFruitNum) , 20
				),
				makeAlge(
						new ExBase(nohatAppleYen) , 10,
						new ExBase(nohatOrangeYen), 20,
						new ExBase(nohatBananaYen), 30,
						new ExBase(nohatMelonYen) ,  0,
						new ExBase(hatCashYen)    , 60,
						new ExBase(hatMelonYen)   ,  0,
						new ExBase(nohatFruitNum) ,  0
				),
				makeAlge(
						new ExBase(nohatAppleYen) , 10,
						new ExBase(nohatOrangeYen), 20,
						new ExBase(nohatBananaYen), 30,
						new ExBase(nohatMelonYen) ,  0,
						new ExBase(hatCashYen)    , 60
				),
			};
		assertEquals(fromPatternData1.length, ansAlge11.length);
		assertEquals(fromPatternData2.length, ansAlge12.length);
		System.out.println("aggreTransfer(ExBasePatternSet, ExBasePattern)...");
		System.out.println("    alge = " + orgAlge1);
		for (int i = 0; i < fromPatternData1.length; i++) {
			ret = alge1.aggreTransfer(fromPatternData1[i], toPattern);
			assertEquals(alge1, orgAlge1);
			assertEquals(ret, ansAlge11[i]);
			System.out.println("  ----------");
			System.out.println("    fromPattern = " + fromPatternData1[i]);
			System.out.println("    toPattern = " + toPattern);
			System.out.println("    result = " + ret);
		}
		for (int i = 0; i < fromPatternData2.length; i++) {
			ret = alge1.aggreTransfer(fromPatternData2[i], toPattern);
			assertEquals(alge1, orgAlge1);
			assertEquals(ret, ansAlge12[i]);
			System.out.println("  ----------");
			System.out.println("    fromPattern = " + fromPatternData2[i]);
			System.out.println("    toPattern = " + toPattern);
			System.out.println("    result = " + ret);
		}
		for (ExBasePattern pattern : fromPatternData3) {
			ret = alge1.aggreTransfer(pattern, toPattern);
			assertEquals(alge1, orgAlge1);
			assertEquals(ret, orgAlge1);
		}
		
		// for data2
		Exalge[] ansAlge21 = new Exalge[]{
				makeAlge(
						new ExBase(hatAppleYen)   , 10,
						new ExBase(hatOrangeYen)  , 20,
						new ExBase(hatBananaYen)  , 30,
						new ExBase(hatMangoYen)   ,  0,
						new ExBase(nohatCashYen)  , 60
				),
				makeAlge(
						new ExBase(hatAppleYen)   , 10,
						new ExBase(hatOrangeYen)  , 20,
						new ExBase(hatBananaYen)  , 30,
						new ExBase(hatMangoYen)   ,  0,
						new ExBase(nohatCashYen)  , 60
				),
				makeAlge(
						new ExBase(hatAppleYen)   , 10,
						new ExBase(hatOrangeYen)  , 20,
						new ExBase(hatBananaYen)  , 30,
						new ExBase(hatMangoYen)   ,  0,
						new ExBase(nohatCashYen)  , 60
				),
				makeAlge(
						new ExBase(hatAppleYen)   , 10,
						new ExBase(hatOrangeYen)  , 20,
						new ExBase(hatBananaYen)  , 30,
						new ExBase(hatMangoYen)   ,  0,
						new ExBase(nohatCashYen)  , 60
				),
		};
		Exalge[] ansAlge22 = new Exalge[]{
				makeAlge(
						new ExBase(hatAppleYen)   , 10,
						new ExBase(hatOrangeYen)  , 20,
						new ExBase(hatBananaYen)  , 30,
						new ExBase(hatMangoYen)   ,  0,
						new ExBase(nohatCashYen)  , 60,
						new ExBase(nohatAppleYen) , 10,
						new ExBase(hatFruitNum)   , 10
				),
				makeAlge(
						new ExBase(hatAppleYen)   , 10,
						new ExBase(hatOrangeYen)  , 20,
						new ExBase(hatBananaYen)  , 30,
						new ExBase(hatMangoYen)   ,  0,
						new ExBase(nohatCashYen)  , 60,
						new ExBase(nohatOrangeYen), 20,
						new ExBase(hatFruitNum)   , 20
				),
				makeAlge(
						new ExBase(hatAppleYen)   , 10,
						new ExBase(hatOrangeYen)  , 20,
						new ExBase(hatBananaYen)  , 30,
						new ExBase(hatMangoYen)   ,  0,
						new ExBase(nohatCashYen)  , 60
				),
				makeAlge(
						new ExBase(hatAppleYen)   , 10,
						new ExBase(hatOrangeYen)  , 20,
						new ExBase(hatBananaYen)  , 30,
						new ExBase(hatMangoYen)   ,  0,
						new ExBase(nohatCashYen)  , 60,
						new ExBase(nohatMangoYen) ,  0,
						new ExBase(hatFruitNum)   ,  0
				),
		};
		assertEquals(fromPatternData1.length, ansAlge21.length);
		assertEquals(fromPatternData2.length, ansAlge22.length);
		System.out.println("aggreTransfer(ExBasePatternSet, ExBasePattern)...");
		System.out.println("    alge = " + orgAlge2);
		for (int i = 0; i < fromPatternData1.length; i++) {
			ret = alge2.aggreTransfer(fromPatternData1[i], toPattern);
			assertEquals(alge2, orgAlge2);
			assertEquals(ret, ansAlge21[i]);
			System.out.println("  ----------");
			System.out.println("    fromPattern = " + fromPatternData1[i]);
			System.out.println("    toPattern = " + toPattern);
			System.out.println("    result = " + ret);
		}
		for (int i = 0; i < fromPatternData2.length; i++) {
			ret = alge2.aggreTransfer(fromPatternData2[i], toPattern);
			assertEquals(alge2, orgAlge2);
			assertEquals(ret, ansAlge22[i]);
			System.out.println("  ----------");
			System.out.println("    fromPattern = " + fromPatternData2[i]);
			System.out.println("    toPattern = " + toPattern);
			System.out.println("    result = " + ret);
		}
		for (ExBasePattern pattern : fromPatternData3) {
			ret = alge2.aggreTransfer(pattern, toPattern);
			assertEquals(alge2, orgAlge2);
			assertEquals(ret, orgAlge2);
		}
	}

	/**
	 * {@link exalge2.Exalge#aggreTransfer(ExBaseSet, ExBase)} のためのテスト・メソッド。
	 * @since 0.970
	 */
	public void testAggreTransferExBaseSetExBase() {
		Object[] data1 = new Object[]{
				new ExBase(nohatAppleYen),	new BigDecimal(10),
				new ExBase(nohatOrangeYen),	new BigDecimal(20),
				new ExBase(nohatBananaYen),	new BigDecimal(30),
				new ExBase(nohatMelonYen),	new BigDecimal(0),
				new ExBase(hatCashYen),		new BigDecimal(60)
		};
		Object[] data2 = new Object[]{
				new ExBase(hatAppleYen),	new BigDecimal(10),
				new ExBase(hatOrangeYen),	new BigDecimal(20),
				new ExBase(hatBananaYen),	new BigDecimal(30),
				new ExBase(hatMangoYen),	new BigDecimal(0),
				new ExBase(nohatCashYen),	new BigDecimal(60)
		};
		Exalge orgAlge1 = new Exalge(data1);
		Exalge orgAlge2 = new Exalge(data2);
		ExBase[] fromPatternData1 = new ExBase[]{
				new ExBase("りんご-NO_HAT-*-*-*"),
				new ExBase("みかん-NO_HAT-*-*-*"),
				new ExBase("メロン-NO_HAT-*-*-*"),
				new ExBase("マンゴー-NO_HAT-*-*-*"),
				new ExBase("まぐろ-NO_HAT-*-*-*"),
				new ExBase("かつお-NO_HAT-*-*-*"),
		};
		ExBase[] fromPatternData2 = new ExBase[]{
				new ExBase("まぐろ-NO_HAT-*-*-*"),
				new ExBase("かつお-NO_HAT-*-*-*"),
		};
		ExBase toPattern = new ExBase("果物-NO_HAT-個-*-*hoge*");
		Exalge algeEmpty = new Exalge();
		Exalge alge1 = new Exalge(data1);
		Exalge alge2 = new Exalge(data2);
		ExBaseSet fromPatterns0 = new ExBaseSet();
		ExBaseSet fromPatterns1 = makeBaseSet(fromPatternData1);
		ExBaseSet fromPatterns2 = makeBaseSet(fromPatternData2);
		Exalge ret;
		
		// check null
		boolean coughtException;
		try {
			alge1.aggreTransfer((ExBaseSet)null, toPattern);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			alge1.aggreTransfer(fromPatterns1, (ExBase)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			alge1.aggreTransfer((ExBaseSet)null, (ExBase)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			algeEmpty.aggreTransfer((ExBaseSet)null, toPattern);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			algeEmpty.aggreTransfer(fromPatterns1, (ExBase)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			algeEmpty.aggreTransfer((ExBaseSet)null, (ExBase)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		
		// check Empty
		ret = algeEmpty.aggreTransfer(fromPatterns0, toPattern);
		assertTrue(algeEmpty.isEmpty());
		assertTrue(ret.isEmpty());
		ret = algeEmpty.aggreTransfer(fromPatterns1, toPattern);
		assertTrue(algeEmpty.isEmpty());
		assertTrue(ret.isEmpty());
		ret = algeEmpty.aggreTransfer(fromPatterns2, toPattern);
		assertTrue(algeEmpty.isEmpty());
		assertTrue(ret.isEmpty());
		
		// for data1
		Exalge ansAlge1 = makeAlge(
							new ExBase(nohatAppleYen) , 10,
							new ExBase(nohatOrangeYen), 20,
							new ExBase(nohatBananaYen), 30,
							new ExBase(nohatMelonYen) ,  0,
							new ExBase(hatCashYen)    , 60,
							new ExBase(hatAppleYen)   , 10,
							new ExBase(nohatFruitNum) , 10,
							new ExBase(hatOrangeYen)  , 20,
							new ExBase(nohatFruitNum) , 20,
							new ExBase(hatMelonYen)   ,  0,
							new ExBase(nohatFruitNum) ,  0
						  );
		ret = alge1.aggreTransfer(fromPatterns0, toPattern);
		assertEquals(alge1, orgAlge1);
		assertEquals(ret, orgAlge1);
		ret = alge1.aggreTransfer(fromPatterns1, toPattern);
		assertEquals(alge1, orgAlge1);
		assertEquals(ret, ansAlge1);
		System.out.println("aggreTransfer(ExBaseSet, ExBase)...");
		System.out.println("    alge = " + orgAlge1);
		System.out.println("    fromPatterns = " + fromPatterns1);
		System.out.println("    toPattern = " + toPattern);
		System.out.println("    result = " + ret);
		ret = alge1.aggreTransfer(fromPatterns2, toPattern);
		assertEquals(alge1, orgAlge1);
		assertEquals(ret, orgAlge1);
		assertTrue(fromPatterns0.isEmpty());
		assertEquals(fromPatterns1, makeBaseSet(fromPatternData1));
		assertEquals(fromPatterns2, makeBaseSet(fromPatternData2));
		
		// for data2
		Exalge ansAlge2 = makeAlge(
							new ExBase(hatAppleYen)   , 10,
							new ExBase(hatOrangeYen)  , 20,
							new ExBase(hatBananaYen)  , 30,
							new ExBase(hatMangoYen)   ,  0,
							new ExBase(nohatCashYen)  , 60,
							new ExBase(nohatAppleYen) , 10,
							new ExBase(hatFruitNum)   , 10,
							new ExBase(nohatOrangeYen), 20,
							new ExBase(hatFruitNum)   , 20,
							new ExBase(nohatMangoYen) ,  0,
							new ExBase(hatFruitNum)   ,  0
						  );
		ret = alge2.aggreTransfer(fromPatterns0, toPattern);
		assertEquals(alge2, orgAlge2);
		assertEquals(ret, orgAlge2);
		ret = alge2.aggreTransfer(fromPatterns1, toPattern);
		assertEquals(alge2, orgAlge2);
		assertEquals(ret, ansAlge2);
		System.out.println("aggreTransfer(ExBaseSet, ExBase)...");
		System.out.println("    alge = " + orgAlge2);
		System.out.println("    fromPatterns = " + fromPatterns1);
		System.out.println("    toPattern = " + toPattern);
		System.out.println("    result = " + ret);
		ret = alge2.aggreTransfer(fromPatterns2, toPattern);
		assertEquals(alge2, orgAlge2);
		assertEquals(ret, orgAlge2);
		assertTrue(fromPatterns0.isEmpty());
		assertEquals(fromPatterns1, makeBaseSet(fromPatternData1));
		assertEquals(fromPatterns2, makeBaseSet(fromPatternData2));
	}
	
	/**
	 * {@link exalge2.Exalge#aggreTransfer(ExBasePatternSet, ExBasePattern)} のためのテスト・メソッド。
	 * @since 0.970
	 */
	public void testAggreTransferExBasePatternSetExBasePattern() {
		Object[] data1 = new Object[]{
				new ExBase(nohatAppleYen),	new BigDecimal(10),
				new ExBase(nohatOrangeYen),	new BigDecimal(20),
				new ExBase(nohatBananaYen),	new BigDecimal(30),
				new ExBase(nohatMelonYen),	new BigDecimal(0),
				new ExBase(hatCashYen),		new BigDecimal(60)
		};
		Object[] data2 = new Object[]{
				new ExBase(hatAppleYen),	new BigDecimal(10),
				new ExBase(hatOrangeYen),	new BigDecimal(20),
				new ExBase(hatBananaYen),	new BigDecimal(30),
				new ExBase(hatMangoYen),	new BigDecimal(0),
				new ExBase(nohatCashYen),	new BigDecimal(60)
		};
		Exalge orgAlge1 = new Exalge(data1);
		Exalge orgAlge2 = new Exalge(data2);
		ExBasePattern[] fromPatternData1 = new ExBasePattern[]{
				new ExBasePattern("りんご-NO_HAT-*-*-*"),
				new ExBasePattern("みかん-NO_HAT-*-*-*"),
				new ExBasePattern("メロン-NO_HAT-*-*-*"),
				new ExBasePattern("マンゴー-NO_HAT-*-*-*"),
				new ExBasePattern("まぐろ-NO_HAT-*-*-*"),
				new ExBasePattern("かつお-NO_HAT-*-*-*"),
		};
		ExBasePattern[] fromPatternData2 = new ExBasePattern[]{
				new ExBasePattern("りんご-*-*-*-*"),
				new ExBasePattern("みかん-*-*-*-*"),
				new ExBasePattern("メロン-*-*-*-*"),
				new ExBasePattern("マンゴー-*-*-*-*"),
				new ExBasePattern("まぐろ-*-*-*-*"),
				new ExBasePattern("かつお-*-*-*-*"),
		};
		ExBasePattern[] fromPatternData3 = new ExBasePattern[]{
				new ExBasePattern("まぐろ-*-*-*-*"),
				new ExBasePattern("かつお-*-*-*-*"),
		};
		ExBasePattern toPattern = new ExBasePattern("果物-*-個-*-*hoge*");
		Exalge algeEmpty = new Exalge();
		Exalge alge1 = new Exalge(data1);
		Exalge alge2 = new Exalge(data2);
		ExBasePatternSet fromPatterns0 = new ExBasePatternSet();
		ExBasePatternSet fromPatterns1 = new ExBasePatternSet(Arrays.asList(fromPatternData1));
		ExBasePatternSet fromPatterns2 = new ExBasePatternSet(Arrays.asList(fromPatternData2));
		ExBasePatternSet fromPatterns3 = new ExBasePatternSet(Arrays.asList(fromPatternData3));
		Exalge ret;
		
		// check null
		boolean coughtException;
		try {
			alge1.aggreTransfer((ExBasePatternSet)null, toPattern);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			alge1.aggreTransfer(fromPatterns1, (ExBasePattern)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			alge1.aggreTransfer((ExBasePatternSet)null, (ExBasePattern)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			algeEmpty.aggreTransfer((ExBasePatternSet)null, toPattern);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			algeEmpty.aggreTransfer(fromPatterns1, (ExBasePattern)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			algeEmpty.aggreTransfer((ExBasePatternSet)null, (ExBasePattern)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		
		// check Empty
		ret = algeEmpty.aggreTransfer(fromPatterns0, toPattern);
		assertTrue(algeEmpty.isEmpty());
		assertTrue(ret.isEmpty());
		ret = algeEmpty.aggreTransfer(fromPatterns1, toPattern);
		assertTrue(algeEmpty.isEmpty());
		assertTrue(ret.isEmpty());
		ret = algeEmpty.aggreTransfer(fromPatterns2, toPattern);
		assertTrue(algeEmpty.isEmpty());
		assertTrue(ret.isEmpty());
		
		// for data1
		Exalge ansAlge11 = makeAlge(
							new ExBase(nohatAppleYen) , 10,
							new ExBase(nohatOrangeYen), 20,
							new ExBase(nohatBananaYen), 30,
							new ExBase(nohatMelonYen) ,  0,
							new ExBase(hatCashYen)    , 60,
							new ExBase(hatAppleYen)   , 10,
							new ExBase(nohatFruitNum) , 10,
							new ExBase(hatOrangeYen)  , 20,
							new ExBase(nohatFruitNum) , 20,
							new ExBase(hatMelonYen)   ,  0,
							new ExBase(nohatFruitNum) ,  0
			  			  );
		Exalge ansAlge12 = makeAlge(
							new ExBase(nohatAppleYen) , 10,
							new ExBase(nohatOrangeYen), 20,
							new ExBase(nohatBananaYen), 30,
							new ExBase(nohatMelonYen) ,  0,
							new ExBase(hatCashYen)    , 60,
							new ExBase(hatAppleYen)   , 10,
							new ExBase(nohatFruitNum) , 10,
							new ExBase(hatOrangeYen)  , 20,
							new ExBase(nohatFruitNum) , 20,
							new ExBase(hatMelonYen)   ,  0,
							new ExBase(nohatFruitNum) ,  0
						  );
		ret = alge1.aggreTransfer(fromPatterns0, toPattern);
		assertEquals(alge1, orgAlge1);
		assertEquals(ret, orgAlge1);
		ret = alge1.aggreTransfer(fromPatterns1, toPattern);
		assertEquals(alge1, orgAlge1);
		assertEquals(ret, ansAlge11);
		System.out.println("aggreTransfer(ExBasePatternSet, ExBasePattern)...");
		System.out.println("    alge = " + orgAlge1);
		System.out.println("  ----------");
		System.out.println("    fromPatterns = " + fromPatterns1);
		System.out.println("    toPattern = " + toPattern);
		System.out.println("    result = " + ret);
		ret = alge1.aggreTransfer(fromPatterns2, toPattern);
		assertEquals(alge1, orgAlge1);
		assertEquals(ret, ansAlge12);
		System.out.println("aggreTransfer(ExBasePatternSet, ExBasePattern)...");
		System.out.println("    alge = " + orgAlge1);
		System.out.println("  ----------");
		System.out.println("    fromPatterns = " + fromPatterns2);
		System.out.println("    toPattern = " + toPattern);
		System.out.println("    result = " + ret);
		ret = alge1.aggreTransfer(fromPatterns3, toPattern);
		assertEquals(alge1, orgAlge1);
		assertEquals(ret, orgAlge1);
		assertTrue(fromPatterns0.isEmpty());
		assertEquals(fromPatterns1, new ExBasePatternSet(Arrays.asList(fromPatternData1)));
		assertEquals(fromPatterns2, new ExBasePatternSet(Arrays.asList(fromPatternData2)));
		assertEquals(fromPatterns3, new ExBasePatternSet(Arrays.asList(fromPatternData3)));
		
		// for data2
		Exalge ansAlge21 = makeAlge(
							new ExBase(hatAppleYen)   , 10,
							new ExBase(hatOrangeYen)  , 20,
							new ExBase(hatBananaYen)  , 30,
							new ExBase(hatMangoYen)   ,  0,
							new ExBase(nohatCashYen)  , 60
						  );
		Exalge ansAlge22 = makeAlge(
							new ExBase(hatAppleYen)   , 10,
							new ExBase(hatOrangeYen)  , 20,
							new ExBase(hatBananaYen)  , 30,
							new ExBase(hatMangoYen)   ,  0,
							new ExBase(nohatCashYen)  , 60,
							new ExBase(nohatAppleYen) , 10,
							new ExBase(hatFruitNum)   , 10,
							new ExBase(nohatOrangeYen), 20,
							new ExBase(hatFruitNum)   , 20,
							new ExBase(nohatMangoYen) ,  0,
							new ExBase(hatFruitNum)   ,  0
			  			  );
		System.out.println("aggreTransfer(ExBasePatternSet, ExBasePattern)...");
		System.out.println("    alge = " + orgAlge2);
		ret = alge2.aggreTransfer(fromPatterns0, toPattern);
		assertEquals(alge2, orgAlge2);
		assertEquals(ret, orgAlge2);
		ret = alge2.aggreTransfer(fromPatterns1, toPattern);
		assertEquals(alge2, orgAlge2);
		assertEquals(ret, ansAlge21);
		System.out.println("  ----------");
		System.out.println("    fromPatterns = " + fromPatterns1);
		System.out.println("    toPattern = " + toPattern);
		System.out.println("    result = " + ret);
		ret = alge2.aggreTransfer(fromPatterns2, toPattern);
		assertEquals(alge2, orgAlge2);
		assertEquals(ret, ansAlge22);
		System.out.println("  ----------");
		System.out.println("    fromPatterns = " + fromPatterns2);
		System.out.println("    toPattern = " + toPattern);
		System.out.println("    result = " + ret);
		ret = alge2.aggreTransfer(fromPatterns3, toPattern);
		assertEquals(alge2, orgAlge2);
		assertEquals(ret, orgAlge2);
		assertTrue(fromPatterns0.isEmpty());
		assertEquals(fromPatterns1, new ExBasePatternSet(Arrays.asList(fromPatternData1)));
		assertEquals(fromPatterns2, new ExBasePatternSet(Arrays.asList(fromPatternData2)));
		assertEquals(fromPatterns3, new ExBasePatternSet(Arrays.asList(fromPatternData3)));
	}
	
	/**
	 * {@link exalge2.Exalge#getBasesByNameKey(String[])} のためのテスト・メソッド。
	 * @since 0.982
	 */
	public void testGetBasesByNameKeyStringArray() {
		Exalge testAlge = makeAlge(
				new ExBase("りんご-NO_HAT-YEN-Y2000-果物"), 10,
				new ExBase("みかん-NO_HAT-YEN-Y2000-果物"), 20,
				new ExBase("ばなな-NO_HAT-YEN-Y2000-果物"), 30,
				new ExBase("まぐろ-HAT-YEN-Y2001-魚類")   , 40,
				new ExBase("かつお-HAT-YEN-Y2001-魚類")   , 50,
				new ExBase("現金-HAT-YEN-Y2000-果物")     , 60,
				new ExBase("現金-NO_HAT-YEN-Y2001-魚類")  , 90
			);
		ExBaseSet retBases;
		
		// null
		boolean coughtException;
		try {
			testAlge.getBasesByNameKey(null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		
		// test keys
		String[] keys1 = {};
		String[] keys2 = {"メロン", "マンゴー", "ひらめ"};
		String[] keys3 = {"みかん", "メロン", "現金", "まぐろ", "ひらめ"};
		
		// empty
		Exalge algeEmpty = new Exalge();
		retBases = algeEmpty.getBasesByNameKey(keys1);
		assertTrue(retBases.isEmpty());
		//---
		retBases = algeEmpty.getBasesByNameKey(keys2);
		assertTrue(retBases.isEmpty());
		//---
		retBases = algeEmpty.getBasesByNameKey(keys3);
		assertTrue(retBases.isEmpty());
		
		// exist
		retBases = testAlge.getBasesByNameKey(keys1);
		assertTrue(retBases.isEmpty());
		//---
		retBases = testAlge.getBasesByNameKey(keys2);
		assertTrue(retBases.isEmpty());
		//---
		Exalge ansAlge = makeAlge(
				new ExBase("みかん-NO_HAT-YEN-Y2000-果物"), 20,
				new ExBase("まぐろ-HAT-YEN-Y2001-魚類")   , 40,
				new ExBase("現金-HAT-YEN-Y2000-果物")     , 60,
				new ExBase("現金-NO_HAT-YEN-Y2001-魚類")  , 90
			);
		retBases = testAlge.getBasesByNameKey(keys3);
		assertEquals(retBases, ansAlge.getBases());
	}
	
	/**
	 * {@link exalge2.Exalge#getBasesByUnitKey(String[])} のためのテスト・メソッド。
	 * @since 0.982
	 */
	public void testGetBasesByUnitKeyStringArray() {
		Exalge testAlge = makeAlge(
				new ExBase("りんご-NO_HAT-YEN-Y2000-果物"), 10,
				new ExBase("みかん-NO_HAT-DOLLER-Y2000-果物"), 20,
				new ExBase("ばなな-NO_HAT-YEN-Y2000-果物"), 30,
				new ExBase("まぐろ-HAT-YEN-Y2001-魚類")   , 40,
				new ExBase("かつお-HAT-YEN-Y2001-魚類")   , 50,
				new ExBase("現金-HAT-DOLLER-Y2000-果物")     , 60,
				new ExBase("現金-NO_HAT-YEN-Y2001-魚類")  , 90
			);
		ExBaseSet retBases;
		
		// null
		boolean coughtException;
		try {
			testAlge.getBasesByUnitKey(null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		
		// test keys
		String[] keys1 = {};
		String[] keys2 = {"\\", "$", "SPD"};
		String[] keys3 = {"YEN", "\\", "$"};
		
		// empty
		Exalge algeEmpty = new Exalge();
		retBases = algeEmpty.getBasesByUnitKey(keys1);
		assertTrue(retBases.isEmpty());
		//---
		retBases = algeEmpty.getBasesByUnitKey(keys2);
		assertTrue(retBases.isEmpty());
		//---
		retBases = algeEmpty.getBasesByUnitKey(keys3);
		assertTrue(retBases.isEmpty());
		
		// exist
		retBases = testAlge.getBasesByUnitKey(keys1);
		assertTrue(retBases.isEmpty());
		//---
		retBases = testAlge.getBasesByUnitKey(keys2);
		assertTrue(retBases.isEmpty());
		//---
		Exalge ansAlge = makeAlge(
				new ExBase("りんご-NO_HAT-YEN-Y2000-果物"), 10,
				new ExBase("ばなな-NO_HAT-YEN-Y2000-果物"), 30,
				new ExBase("まぐろ-HAT-YEN-Y2001-魚類")   , 40,
				new ExBase("かつお-HAT-YEN-Y2001-魚類")   , 50,
				new ExBase("現金-NO_HAT-YEN-Y2001-魚類")  , 90
			);
		retBases = testAlge.getBasesByUnitKey(keys3);
		assertEquals(retBases, ansAlge.getBases());
	}
	
	/**
	 * {@link exalge2.Exalge#getBasesByTimeKey(String[])} のためのテスト・メソッド。
	 * @since 0.982
	 */
	public void testGetBasesByTimeKeyStringArray() {
		Exalge testAlge = makeAlge(
				new ExBase("りんご-NO_HAT-YEN-Y2000-果物"), 10,
				new ExBase("みかん-NO_HAT-YEN-Y2000-果物"), 20,
				new ExBase("ばなな-NO_HAT-YEN-Y2000-果物"), 30,
				new ExBase("まぐろ-HAT-YEN-Y2001-魚類")   , 40,
				new ExBase("かつお-HAT-YEN-Y2001-魚類")   , 50,
				new ExBase("現金-HAT-YEN-Y2000-果物")     , 60,
				new ExBase("現金-NO_HAT-YEN-Y2001-魚類")  , 90
			);
		ExBaseSet retBases;
		
		// null
		boolean coughtException;
		try {
			testAlge.getBasesByTimeKey(null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		
		// test keys
		String[] keys1 = {};
		String[] keys2 = {"Y1999", "Y2006", "Y2009"};
		String[] keys3 = {"Y1999", "Y2001", "Y2003", "Y2005"};
		
		// empty
		Exalge algeEmpty = new Exalge();
		retBases = algeEmpty.getBasesByTimeKey(keys1);
		assertTrue(retBases.isEmpty());
		//---
		retBases = algeEmpty.getBasesByTimeKey(keys2);
		assertTrue(retBases.isEmpty());
		//---
		retBases = algeEmpty.getBasesByTimeKey(keys3);
		assertTrue(retBases.isEmpty());
		
		// exist
		retBases = testAlge.getBasesByTimeKey(keys1);
		assertTrue(retBases.isEmpty());
		//---
		retBases = testAlge.getBasesByTimeKey(keys2);
		assertTrue(retBases.isEmpty());
		//---
		Exalge ansAlge = makeAlge(
				new ExBase("まぐろ-HAT-YEN-Y2001-魚類")   , 40,
				new ExBase("かつお-HAT-YEN-Y2001-魚類")   , 50,
				new ExBase("現金-NO_HAT-YEN-Y2001-魚類")  , 90
			);
		retBases = testAlge.getBasesByTimeKey(keys3);
		assertEquals(retBases, ansAlge.getBases());
	}
	
	/**
	 * {@link exalge2.Exalge#getBasesBySubjectKey(String[])} のためのテスト・メソッド。
	 * @since 0.982
	 */
	public void testGetBasesBySubjectKeyStringArray() {
		Exalge testAlge = makeAlge(
				new ExBase("りんご-NO_HAT-YEN-Y2000-果物"), 10,
				new ExBase("みかん-NO_HAT-YEN-Y2000-果物"), 20,
				new ExBase("ばなな-NO_HAT-YEN-Y2000-果物"), 30,
				new ExBase("まぐろ-HAT-YEN-Y2001-魚類")   , 40,
				new ExBase("かつお-HAT-YEN-Y2001-魚類")   , 50,
				new ExBase("現金-HAT-YEN-Y2000-果物")     , 60,
				new ExBase("現金-NO_HAT-YEN-Y2001-魚類")  , 90
			);
		ExBaseSet retBases;
		
		// null
		boolean coughtException;
		try {
			testAlge.getBasesBySubjectKey(null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		
		// test keys
		String[] keys1 = {};
		String[] keys2 = {"甲殻類", "鳥類", "哺乳類"};
		String[] keys3 = {"甲殻類", "哺乳類", "果物"};
		
		// empty
		Exalge algeEmpty = new Exalge();
		retBases = algeEmpty.getBasesBySubjectKey(keys1);
		assertTrue(retBases.isEmpty());
		//---
		retBases = algeEmpty.getBasesBySubjectKey(keys2);
		assertTrue(retBases.isEmpty());
		//---
		retBases = algeEmpty.getBasesBySubjectKey(keys3);
		assertTrue(retBases.isEmpty());
		
		// exist
		retBases = testAlge.getBasesBySubjectKey(keys1);
		assertTrue(retBases.isEmpty());
		//---
		retBases = testAlge.getBasesBySubjectKey(keys2);
		assertTrue(retBases.isEmpty());
		//---
		Exalge ansAlge = makeAlge(
				new ExBase("りんご-NO_HAT-YEN-Y2000-果物"), 10,
				new ExBase("みかん-NO_HAT-YEN-Y2000-果物"), 20,
				new ExBase("ばなな-NO_HAT-YEN-Y2000-果物"), 30,
				new ExBase("現金-HAT-YEN-Y2000-果物")     , 60
			);
		retBases = testAlge.getBasesBySubjectKey(keys3);
		assertEquals(retBases, ansAlge.getBases());
	}
	
	/**
	 * {@link exalge2.Exalge#projectionByNameKey(String[])} のためのテスト・メソッド。
	 * @since 0.982
	 */
	public void testProjectionByNameKeyStringArray() {
		Exalge testAlge = makeAlge(
				new ExBase("りんご-NO_HAT-YEN-Y2000-果物")   , 10,
				new ExBase("みかん-NO_HAT-DOLLER-Y2000-果物"), 20,
				new ExBase("ばなな-NO_HAT-YEN-Y2000-果物")   , 30,
				new ExBase("まぐろ-HAT-YEN-Y2001-魚類")      , 40,
				new ExBase("かつお-HAT-YEN-Y2001-魚類")      , 50,
				new ExBase("現金-HAT-DOLLER-Y2000-果物")     , 60,
				new ExBase("現金-NO_HAT-YEN-Y2001-魚類")     , 90
			);
		Exalge retAlge;
		
		// null
		boolean coughtException;
		try {
			testAlge.projectionByNameKey(null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		
		// test keys
		String[] keys1 = {};
		String[] keys2 = {"メロン", "マンゴー", "ひらめ"};
		String[] keys3 = {"みかん", "メロン", "現金", "まぐろ", "ひらめ"};
		
		// empty
		Exalge algeEmpty = new Exalge();
		retAlge = algeEmpty.projectionByNameKey(keys1);
		assertTrue(retAlge.isEmpty());
		//---
		retAlge = algeEmpty.projectionByNameKey(keys2);
		assertTrue(retAlge.isEmpty());
		//---
		retAlge = algeEmpty.projectionByNameKey(keys3);
		assertTrue(retAlge.isEmpty());
		
		// exist
		retAlge = testAlge.projectionByNameKey(keys1);
		assertTrue(retAlge.isEmpty());
		//---
		retAlge = testAlge.projectionByNameKey(keys2);
		assertTrue(retAlge.isEmpty());
		//---
		Exalge ansAlge = makeAlge(
				new ExBase("みかん-NO_HAT-DOLLER-Y2000-果物"), 20,
				new ExBase("まぐろ-HAT-YEN-Y2001-魚類")      , 40,
				new ExBase("現金-HAT-DOLLER-Y2000-果物")     , 60,
				new ExBase("現金-NO_HAT-YEN-Y2001-魚類")     , 90
			);
		retAlge = testAlge.projectionByNameKey(keys3);
		assertEquals(retAlge, ansAlge);
	}
	
	/**
	 * {@link exalge2.Exalge#projectionByUnitKey(String[])} のためのテスト・メソッド。
	 * @since 0.982
	 */
	public void testProjectionByUnitKeyStringArray() {
		Exalge testAlge = makeAlge(
				new ExBase("りんご-NO_HAT-YEN-Y2000-果物")   , 10,
				new ExBase("みかん-NO_HAT-DOLLER-Y2000-果物"), 20,
				new ExBase("ばなな-NO_HAT-YEN-Y2000-果物")   , 30,
				new ExBase("まぐろ-HAT-YEN-Y2001-魚類")      , 40,
				new ExBase("かつお-HAT-YEN-Y2001-魚類")      , 50,
				new ExBase("現金-HAT-DOLLER-Y2000-果物")     , 60,
				new ExBase("現金-NO_HAT-YEN-Y2001-魚類")     , 90
			);
		Exalge retAlge;
		
		// null
		boolean coughtException;
		try {
			testAlge.projectionByUnitKey(null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		
		// test keys
		String[] keys1 = {};
		String[] keys2 = {"\\", "$", "SPD"};
		String[] keys3 = {"YEN", "\\", "$"};
		
		// empty
		Exalge algeEmpty = new Exalge();
		retAlge = algeEmpty.projectionByUnitKey(keys1);
		assertTrue(retAlge.isEmpty());
		//---
		retAlge = algeEmpty.projectionByUnitKey(keys2);
		assertTrue(retAlge.isEmpty());
		//---
		retAlge = algeEmpty.projectionByUnitKey(keys3);
		assertTrue(retAlge.isEmpty());
		
		// exist
		retAlge = testAlge.projectionByUnitKey(keys1);
		assertTrue(retAlge.isEmpty());
		//---
		retAlge = testAlge.projectionByUnitKey(keys2);
		assertTrue(retAlge.isEmpty());
		//---
		Exalge ansAlge = makeAlge(
				new ExBase("りんご-NO_HAT-YEN-Y2000-果物")   , 10,
				new ExBase("ばなな-NO_HAT-YEN-Y2000-果物")   , 30,
				new ExBase("まぐろ-HAT-YEN-Y2001-魚類")      , 40,
				new ExBase("かつお-HAT-YEN-Y2001-魚類")      , 50,
				new ExBase("現金-NO_HAT-YEN-Y2001-魚類")     , 90
			);
		retAlge = testAlge.projectionByUnitKey(keys3);
		assertEquals(retAlge, ansAlge);
	}
	
	/**
	 * {@link exalge2.Exalge#projectionByTimeKey(String[])} のためのテスト・メソッド。
	 * @since 0.982
	 */
	public void testProjectionByTimeKeyStringArray() {
		Exalge testAlge = makeAlge(
				new ExBase("りんご-NO_HAT-YEN-Y2000-果物")   , 10,
				new ExBase("みかん-NO_HAT-DOLLER-Y2000-果物"), 20,
				new ExBase("ばなな-NO_HAT-YEN-Y2000-果物")   , 30,
				new ExBase("まぐろ-HAT-YEN-Y2001-魚類")      , 40,
				new ExBase("かつお-HAT-YEN-Y2001-魚類")      , 50,
				new ExBase("現金-HAT-DOLLER-Y2000-果物")     , 60,
				new ExBase("現金-NO_HAT-YEN-Y2001-魚類")     , 90
			);
		Exalge retAlge;
		
		// null
		boolean coughtException;
		try {
			testAlge.projectionByTimeKey(null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		
		// test keys
		String[] keys1 = {};
		String[] keys2 = {"Y1999", "Y2006", "Y2009"};
		String[] keys3 = {"Y1999", "Y2001", "Y2003", "Y2005"};
		
		// empty
		Exalge algeEmpty = new Exalge();
		retAlge = algeEmpty.projectionByTimeKey(keys1);
		assertTrue(retAlge.isEmpty());
		//---
		retAlge = algeEmpty.projectionByTimeKey(keys2);
		assertTrue(retAlge.isEmpty());
		//---
		retAlge = algeEmpty.projectionByTimeKey(keys3);
		assertTrue(retAlge.isEmpty());
		
		// exist
		retAlge = testAlge.projectionByTimeKey(keys1);
		assertTrue(retAlge.isEmpty());
		//---
		retAlge = testAlge.projectionByTimeKey(keys2);
		assertTrue(retAlge.isEmpty());
		//---
		Exalge ansAlge = makeAlge(
				new ExBase("まぐろ-HAT-YEN-Y2001-魚類")      , 40,
				new ExBase("かつお-HAT-YEN-Y2001-魚類")      , 50,
				new ExBase("現金-NO_HAT-YEN-Y2001-魚類")     , 90
			);
		retAlge = testAlge.projectionByTimeKey(keys3);
		assertEquals(retAlge, ansAlge);
	}
	
	/**
	 * {@link exalge2.Exalge#projectionBySubjectKey(String[])} のためのテスト・メソッド。
	 * @since 0.982
	 */
	public void testProjectionBySubjectKeyStringArray() {
		Exalge testAlge = makeAlge(
				new ExBase("りんご-NO_HAT-YEN-Y2000-果物")   , 10,
				new ExBase("みかん-NO_HAT-DOLLER-Y2000-果物"), 20,
				new ExBase("ばなな-NO_HAT-YEN-Y2000-果物")   , 30,
				new ExBase("まぐろ-HAT-YEN-Y2001-魚類")      , 40,
				new ExBase("かつお-HAT-YEN-Y2001-魚類")      , 50,
				new ExBase("現金-HAT-DOLLER-Y2000-果物")     , 60,
				new ExBase("現金-NO_HAT-YEN-Y2001-魚類")     , 90
			);
		Exalge retAlge;
		
		// null
		boolean coughtException;
		try {
			testAlge.projectionBySubjectKey(null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		
		// test keys
		String[] keys1 = {};
		String[] keys2 = {"甲殻類", "鳥類", "哺乳類"};
		String[] keys3 = {"甲殻類", "哺乳類", "果物"};
		
		// empty
		Exalge algeEmpty = new Exalge();
		retAlge = algeEmpty.projectionBySubjectKey(keys1);
		assertTrue(retAlge.isEmpty());
		//---
		retAlge = algeEmpty.projectionBySubjectKey(keys2);
		assertTrue(retAlge.isEmpty());
		//---
		retAlge = algeEmpty.projectionBySubjectKey(keys3);
		assertTrue(retAlge.isEmpty());
		
		// exist
		retAlge = testAlge.projectionBySubjectKey(keys1);
		assertTrue(retAlge.isEmpty());
		//---
		retAlge = testAlge.projectionBySubjectKey(keys2);
		assertTrue(retAlge.isEmpty());
		//---
		Exalge ansAlge = makeAlge(
				new ExBase("りんご-NO_HAT-YEN-Y2000-果物")   , 10,
				new ExBase("みかん-NO_HAT-DOLLER-Y2000-果物"), 20,
				new ExBase("ばなな-NO_HAT-YEN-Y2000-果物")   , 30,
				new ExBase("現金-HAT-DOLLER-Y2000-果物")     , 60
			);
		retAlge = testAlge.projectionBySubjectKey(keys3);
		assertEquals(retAlge, ansAlge);
	}

	/**
	 * {@link exalge2.Exalge#transfer(ExTransfer)} のためのテスト・メソッド。
	 * @since 0.982
	 */
	public void testTransferExTransfer() {
		Object[] objData0 = new Object[]{
				ExTransferTest.nFruitNum, new BigDecimal(10+20+30),
				ExTransferTest.nExpFruitNum, new BigDecimal(40+50),
				ExTransferTest.hFisheryYen, new BigDecimal(50+30+10+10),
				ExTransferTest.nMuttonYen, new BigDecimal(20),
				ExTransferTest.nBallPenNum, new BigDecimal(200*5),
				ExTransferTest.hMechaPenNum, new BigDecimal(250*4),
				ExTransferTest.nEraserNum, new BigDecimal(50*2),
				ExTransferTest.hPrefTokyoNum, new BigDecimal(100),
				ExTransferTest.nHoryuYen, new BigDecimal(123),
		};
		Object[] objData1 = new Object[]{
				ExTransferTest.nAppleYen,		new BigDecimal(10),
				ExTransferTest.nOrangeYen,		new BigDecimal(20),
				ExTransferTest.nBananaYen,		new BigDecimal(30),
				ExTransferTest.hMelonYen,		new BigDecimal(40),
				ExTransferTest.hMangoYen,		new BigDecimal(50),
				ExTransferTest.hGrapefruitYen,	new BigDecimal(60),
				
				ExTransferTest.nFishYen, new BigDecimal(50+30+10+10),
				
				ExTransferTest.hMeatYen, new BigDecimal(20),
				
				ExTransferTest.nBallPenYen,	new BigDecimal(200*5),
				ExTransferTest.hMechaPenYen,	new BigDecimal(250*4),
				ExTransferTest.nPencilYen,		new BigDecimal(80*3),
				ExTransferTest.hEraserYen,		new BigDecimal(50*2),

				ExTransferTest.nPrefChibaNum,		new BigDecimal(11),
				ExTransferTest.nPrefKanagawaNum,	new BigDecimal(22),
				ExTransferTest.nPrefSaitamaNum,	new BigDecimal(33),
				ExTransferTest.nPrefTokyoNum,		new BigDecimal(44),
				
				ExTransferTest.hRochin1Yen,	new BigDecimal(123),
				ExTransferTest.nRochin2Yen, new BigDecimal(456),
				ExTransferTest.hRochin3Yen, new BigDecimal(789),
		};
		Object[] objData2 = new Object[]{
				ExTransferTest.nVegetablesYen, ExTransferTest.int5
		};
		
		Exalge dataEmpty = new Exalge();
		Exalge orgData0 = new Exalge(objData0);
		Exalge orgData1 = new Exalge(objData1);
		Exalge orgData2 = new Exalge(objData2);
		Exalge data0 = new Exalge(objData0);
		Exalge data1 = new Exalge(objData1);
		Exalge data2 = new Exalge(objData2);
		Exalge ans1 = new Exalge(new Object[]{
				ExTransferTest.nAppleYen,		new BigDecimal(10),
				ExTransferTest.nOrangeYen,		new BigDecimal(20),
				ExTransferTest.nBananaYen,		new BigDecimal(30),
				ExTransferTest.hMelonYen,		new BigDecimal(40),
				ExTransferTest.hMangoYen,		new BigDecimal(50),
				ExTransferTest.hGrapefruitYen,	new BigDecimal(60),
				
				ExTransferTest.nFishYen, new BigDecimal(50+30+10+10),
				
				ExTransferTest.hMeatYen, new BigDecimal(20),
				
				ExTransferTest.nBallPenYen,	new BigDecimal(200*5),
				ExTransferTest.hMechaPenYen,	new BigDecimal(250*4),
				ExTransferTest.nPencilYen,		new BigDecimal(80*3),
				ExTransferTest.hEraserYen,		new BigDecimal(50*2),

				ExTransferTest.nPrefChibaNum,		new BigDecimal(11),
				ExTransferTest.nPrefKanagawaNum,	new BigDecimal(22),
				ExTransferTest.nPrefSaitamaNum,	new BigDecimal(33),
				ExTransferTest.nPrefTokyoNum,		new BigDecimal(44),
				
				ExTransferTest.hRochin1Yen,	new BigDecimal(123),
				ExTransferTest.nRochin2Yen, new BigDecimal(456),
				ExTransferTest.hRochin3Yen, new BigDecimal(789),

				ExTransferTest.hAppleYen,		new BigDecimal(10),
				ExTransferTest.nFruitNum,		new BigDecimal(10),
				ExTransferTest.hOrangeYen,		new BigDecimal(20),
				ExTransferTest.nFruitNum,		new BigDecimal(20),
				ExTransferTest.hBananaYen,		new BigDecimal(30),
				ExTransferTest.nFruitNum,		new BigDecimal(30),
				ExTransferTest.nMelonYen,		new BigDecimal(40),
				ExTransferTest.hExpFruitNum,	new BigDecimal(40),
				ExTransferTest.nMangoYen,		new BigDecimal(50),
				ExTransferTest.hExpFruitNum,	new BigDecimal(50),
				
				ExTransferTest.hFishYen,		new BigDecimal(50+30+10+10),
				ExTransferTest.nTunaYen,		new BigDecimal(50),
				ExTransferTest.nBonitoYen,		new BigDecimal(30),
				ExTransferTest.nSauryYen,		new BigDecimal(10),
				ExTransferTest.nSardineYen,	new BigDecimal(10),
				
				ExTransferTest.nMeatYen,		new BigDecimal(20),
				ExTransferTest.hBeefYen,		new BigDecimal(40),
				ExTransferTest.hPorkYen,		new BigDecimal(20),
				ExTransferTest.hChickenYen,	new BigDecimal(20),
				
				ExTransferTest.hBallPenYen,	new BigDecimal(200*5),
				ExTransferTest.nBallPenNum,	new BigDecimal(5),
				ExTransferTest.nMechaPenYen,	new BigDecimal(250*4),
				ExTransferTest.hMechaPenNum,	new BigDecimal(4),
				ExTransferTest.hPencilYen,		new BigDecimal(80*3),
				ExTransferTest.nPencilNum,		new BigDecimal(3),

				ExTransferTest.hPrefChibaNum,		new BigDecimal(11),
				ExTransferTest.nJapanNum,			new BigDecimal(11),
				ExTransferTest.hPrefKanagawaNum,	new BigDecimal(22),
				ExTransferTest.nJapanNum,			new BigDecimal(22),
				ExTransferTest.hPrefSaitamaNum,	new BigDecimal(33),
				ExTransferTest.nJapanNum,			new BigDecimal(33),
				
				ExTransferTest.nRochin1Yen,		new BigDecimal(123),
				ExTransferTest.nNaibuHoryuYen,	new BigDecimal(123),
				ExTransferTest.hRochin2Yen,		new BigDecimal(456),
				ExTransferTest.hNaibuHoryuYen,	new BigDecimal(456),
		});
		
		boolean coughtException;
		Exalge ret;
		
		ExTransfer trans1 = new ExTransfer(false);
		ExTransfer trans2 = new ExTransfer(true);
		ExTransfer trans3 = new ExTransfer(false);
		ExTransfer trans4 = new ExTransfer(true);
		ExTransferTest.putAllTransfer(trans3, ExTransferTest.data5);
		ExTransferTest.putAllTransfer(trans4, ExTransferTest.data5);
		assertTrue(trans1.isEmpty());
		assertTrue(trans2.isEmpty());
		ExTransferTest.assertEqualTransfer(trans3, ExTransferTest.data5);
		ExTransferTest.assertEqualTransfer(trans4, ExTransferTest.data5);
		
		// dataEmpty
		try {
			dataEmpty.transfer((ExTransfer)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		ret = dataEmpty.transfer(trans1);
		assertTrue(dataEmpty.isEmpty());
		assertTrue(ret.isEmpty());
		ret = dataEmpty.transfer(trans2);
		assertTrue(dataEmpty.isEmpty());
		assertTrue(ret.isEmpty());
		ret = dataEmpty.transfer(trans3);
		assertTrue(dataEmpty.isEmpty());
		assertTrue(ret.isEmpty());
		ret = dataEmpty.transfer(trans4);
		assertTrue(dataEmpty.isEmpty());
		assertTrue(ret.isEmpty());
		
		// data0
		try {
			data0.transfer((ExTransfer)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		assertEquals(orgData0, data0);
		ret = data0.transfer(trans1);
		assertEquals(data0, ret);
		assertEquals(orgData0, data0);
		ret = data0.transfer(trans2);
		assertEquals(data0, ret);
		assertEquals(orgData0, data0);
		ret = data0.transfer(trans3);
		assertEquals(data0, ret);
		assertEquals(orgData0, data0);
		ret = data0.transfer(trans4);
		assertEquals(data0, ret);
		assertEquals(orgData0, data0);
		
		// data1
		try {
			data1.transfer((ExTransfer)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		assertEquals(orgData1, data1);
		ret = data1.transfer(trans1);
		assertEquals(data1, ret);
		assertEquals(orgData1, data1);
		ret = data1.transfer(trans2);
		assertEquals(data1, ret);
		assertEquals(orgData1, data1);
		ret = data1.transfer(trans3);
		assertEquals(ans1, ret);
		assertEquals(orgData1, data1);
		ret = data1.transfer(trans4);
		assertEquals(ans1, ret);
		assertEquals(orgData1, data1);
		
		// data2
		try {
			data2.transfer((ExTransfer)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		assertEquals(orgData2, data2);
		ret = data2.transfer(trans1);
		assertEquals(data2, ret);
		assertEquals(orgData2, data2);
		ret = data2.transfer(trans2);
		assertEquals(data2, ret);
		assertEquals(orgData2, data2);
		try {
			data2.transfer(trans3);
			coughtException = false;
		} catch (ArithmeticException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		assertEquals(orgData2, data2);
		try {
			data2.transfer(trans4);
			coughtException = false;
		} catch (ArithmeticException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		assertEquals(orgData2, data2);

		//--- trans check
		assertTrue(trans1.isEmpty());
		assertTrue(trans2.isEmpty());
		ExTransferTest.assertEqualTransfer(trans3, ExTransferTest.data5);
		ExTransferTest.assertEqualTransfer(trans4, ExTransferTest.data5);
	}
	
	/**
	 * {@link exalge2.Exalge#containsAnyBases(ExBaseSet)} のためのテスト・メソッド。
	 * @since 0.983
	 */
	public void testContainsAnyBases() {
		Exalge testAlge = makeAlge(
				new ExBase(hatAppleYen) , 20,
				new ExBase(hatOrangeYen), 40,
				new ExBase(hatBananaYen), 60,
				new ExBase(nohatCashYen), 120,
				new ExBase(nohatMelonYen), 0,
				new ExBase(hatMelonYen)  , 0
		);

		// null
		boolean coughtException;
		try {
			testAlge.containsAnyBases(null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);

		// Empty
		boolean ret = testAlge.containsAnyBases(new ExBaseSet());
		assertFalse(ret);

		// test
		//---
		ExBaseSet test1 = new ExBaseSet(Arrays.asList(new ExBase(nohatAppleYen), new ExBase(hatAppleYen)));
		ret = testAlge.containsAnyBases(test1);
		assertTrue(ret);
		//---
		ExBaseSet test2 = new ExBaseSet(Arrays.asList(new ExBase(nohatCashYen), new ExBase(hatCashYen)));
		ret = testAlge.containsAnyBases(test2);
		assertTrue(ret);
		//---
		ExBaseSet test3 = new ExBaseSet(Arrays.asList(new ExBase(nohatMelonYen), new ExBase(hatMelonYen)));
		ret = testAlge.containsAnyBases(test3);
		assertTrue(ret);
		//---
		ExBaseSet test4 = testAlge.getBases();
		ret = testAlge.containsAnyBases(test4);
		assertTrue(ret);
		//---
		ExBaseSet test5 = new ExBaseSet(Arrays.asList(new ExBase(nohatAppleYen), new ExBase(nohatBananaYen), new ExBase(hatCashYen)));
		ret = testAlge.containsAnyBases(test5);
		assertFalse(ret);
	}
	
	/**
	 * {@link exalge2.Exalge#containsNull()} のためのテスト・メソッド。
	 * @since 0.983
	 */
	public void testContainsNull() {
		Exalge testAlge1 = makeAlge(
				new ExBase(hatAppleYen) , 20,
				new ExBase(hatOrangeYen), 40,
				new ExBase(hatBananaYen), 60,
				new ExBase(nohatCashYen), 120,
				new ExBase(nohatMelonYen), 0,
				new ExBase(hatMelonYen)  , 0
		);
		Exalge testAlge2 = makeAlge(
				new ExBase(hatAppleYen) , 20,
				new ExBase(hatOrangeYen), null,
				new ExBase(hatBananaYen), 60,
				new ExBase(nohatCashYen), 120,
				new ExBase(nohatMelonYen), null,
				new ExBase(hatMelonYen)  , null
		);
		Exalge testAlge3 = makeAlge(
				new ExBase(hatAppleYen) , null,
				new ExBase(hatOrangeYen), null,
				new ExBase(hatBananaYen), null,
				new ExBase(nohatCashYen), null,
				new ExBase(nohatMelonYen), null,
				new ExBase(hatMelonYen)  , null
		);
		
		boolean ret = (new Exalge()).containsNull();
		assertFalse(ret);
		//---
		ret = testAlge1.containsNull();
		assertFalse(ret);
		//---
		ret = testAlge2.containsNull();
		assertTrue(ret);
		//---
		ret = testAlge3.containsNull();
		assertTrue(ret);
	}
	
	/**
	 * {@link exalge2.Exalge#oneValueProjection(BigDecimal)} のためのテスト・メソッド。
	 * @since 0.983
	 */
	public void testOneValueProjectionBigDecimal() {
		final BigDecimal valN = null;
		final BigDecimal val0 = BigDecimal.ZERO;
		final BigDecimal val1 = new BigDecimal("20");
		final BigDecimal val2 = new BigDecimal("60");
		final BigDecimal val3 = new BigDecimal("120.12");
		Exalge testAlge0 = new Exalge();
		Exalge testAlge1 = makeAlge(
				new ExBase(hatAppleYen) , 20,
				new ExBase(hatOrangeYen), 40,
				new ExBase(hatBananaYen), 60,
				new ExBase(nohatCashYen), 120,
				new ExBase(nohatMelonYen), 0,
				new ExBase(hatMelonYen)  , 0
		);
		Exalge testAlge2 = makeAlge(
				new ExBase(hatAppleYen) , new BigDecimal("20"),
				new ExBase(hatOrangeYen), null,
				new ExBase(hatBananaYen), 60,
				new ExBase(nohatCashYen), new BigDecimal("120.1200"),
				new ExBase(nohatMelonYen), new BigDecimal("0.00"),
				new ExBase(hatMelonYen)  , null
		);
		Exalge orgAlge1 = testAlge1.copy();
		Exalge orgAlge2 = testAlge2.copy();
		Exalge ansAlgeN = makeAlge(
				new ExBase(hatOrangeYen), null,
				new ExBase(hatMelonYen)  , null
		);
		Exalge ansAlge0_1 = makeAlge(
				new ExBase(nohatMelonYen), 0,
				new ExBase(hatMelonYen)  , 0
		);
		Exalge ansAlge0_2 = makeAlge(
				new ExBase(nohatMelonYen), new BigDecimal("0.00")
		);
		Exalge ansAlge1 = makeAlge(
				new ExBase(hatAppleYen) , 20
		);
		Exalge ansAlge2 = makeAlge(
				new ExBase(hatBananaYen), 60
		);
		Exalge ansAlge3 = makeAlge(
				new ExBase(nohatCashYen), new BigDecimal("120.1200")
		);
		
		Exalge ret;
		//---
		ret = testAlge0.oneValueProjection(valN);
		assertTrue(ret.isEmpty());
		ret = testAlge1.oneValueProjection(valN);
		assertTrue(ret.isEmpty());
		ret = testAlge2.oneValueProjection(valN);
		assertTrue(ret.equals(ansAlgeN));
		assertTrue(testAlge0.isEmpty());
		assertTrue(testAlge1.equals(orgAlge1));
		assertTrue(testAlge2.equals(orgAlge2));
		//---
		ret = testAlge0.oneValueProjection(val0);
		assertTrue(ret.isEmpty());
		ret = testAlge1.oneValueProjection(val0);
		assertTrue(ret.equals(ansAlge0_1));
		ret = testAlge2.oneValueProjection(val0);
		assertTrue(ret.equals(ansAlge0_2));
		assertTrue(testAlge0.isEmpty());
		assertTrue(testAlge1.equals(orgAlge1));
		assertTrue(testAlge2.equals(orgAlge2));
		//---
		ret = testAlge0.oneValueProjection(val1);
		assertTrue(ret.isEmpty());
		ret = testAlge1.oneValueProjection(val1);
		assertTrue(ret.equals(ansAlge1));
		ret = testAlge2.oneValueProjection(val1);
		assertTrue(ret.equals(ansAlge1));
		assertTrue(testAlge0.isEmpty());
		assertTrue(testAlge1.equals(orgAlge1));
		assertTrue(testAlge2.equals(orgAlge2));
		//---
		ret = testAlge0.oneValueProjection(val2);
		assertTrue(ret.isEmpty());
		ret = testAlge1.oneValueProjection(val2);
		assertTrue(ret.equals(ansAlge2));
		ret = testAlge2.oneValueProjection(val2);
		assertTrue(ret.equals(ansAlge2));
		assertTrue(testAlge0.isEmpty());
		assertTrue(testAlge1.equals(orgAlge1));
		assertTrue(testAlge2.equals(orgAlge2));
		//---
		ret = testAlge0.oneValueProjection(val3);
		assertTrue(ret.isEmpty());
		ret = testAlge1.oneValueProjection(val3);
		assertTrue(ret.isEmpty());
		ret = testAlge2.oneValueProjection(val3);
		assertTrue(ret.equals(ansAlge3));
		assertTrue(testAlge0.isEmpty());
		assertTrue(testAlge1.equals(orgAlge1));
		assertTrue(testAlge2.equals(orgAlge2));
	}
	
	/**
	 * {@link exalge2.Exalge#valuesProjection(java.util.Collection)} のためのテスト・メソッド。
	 * @since 0.983
	 */
	public void testValuesProjectionCollectionOfBigDecimal() {
		final BigDecimal valN = null;
		final BigDecimal val0 = BigDecimal.ZERO;
		final BigDecimal val1 = new BigDecimal("20");
		final BigDecimal val3 = new BigDecimal("120.12");
		Exalge testAlge0 = new Exalge();
		Exalge testAlge1 = makeAlge(
				new ExBase(hatAppleYen) , 20,
				new ExBase(hatOrangeYen), 40,
				new ExBase(hatBananaYen), 60,
				new ExBase(nohatCashYen), 120,
				new ExBase(nohatMelonYen), 0,
				new ExBase(hatMelonYen)  , 0
		);
		Exalge testAlge2 = makeAlge(
				new ExBase(hatAppleYen) , new BigDecimal("20"),
				new ExBase(hatOrangeYen), null,
				new ExBase(hatBananaYen), 60,
				new ExBase(nohatCashYen), new BigDecimal("120.1200"),
				new ExBase(nohatMelonYen), new BigDecimal("0.00"),
				new ExBase(hatMelonYen)  , null
		);
		Exalge orgAlge1 = testAlge1.copy();
		Exalge orgAlge2 = testAlge2.copy();

		// null
		boolean coughtException;
		try {
			testAlge0.valuesProjection((Collection<BigDecimal>)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			testAlge2.valuesProjection((Collection<BigDecimal>)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);

		Exalge ret;
		//---
		Collection<? extends BigDecimal> c0 = Collections.<BigDecimal>emptyList();
		ret = testAlge0.valuesProjection(c0);
		assertTrue(ret.isEmpty());
		ret = testAlge1.valuesProjection(c0);
		assertTrue(ret.isEmpty());
		ret = testAlge2.valuesProjection(c0);
		assertTrue(ret.isEmpty());
		assertTrue(testAlge0.isEmpty());
		assertTrue(testAlge1.equals(orgAlge1));
		assertTrue(testAlge2.equals(orgAlge2));
		
		//---
		Collection<? extends BigDecimal> c1 = Arrays.asList(valN, val0, val1, val3);
		Exalge ansAlge1_1 = makeAlge(
				new ExBase(hatAppleYen) , 20,
				new ExBase(nohatMelonYen), 0,
				new ExBase(hatMelonYen)  , 0
		);
		Exalge ansAlge1_2 = makeAlge(
				new ExBase(hatAppleYen) , new BigDecimal("20"),
				new ExBase(hatOrangeYen), null,
				new ExBase(nohatCashYen), new BigDecimal("120.1200"),
				new ExBase(nohatMelonYen), new BigDecimal("0.00"),
				new ExBase(hatMelonYen)  , null
		);
		ret = testAlge0.valuesProjection(c1);
		assertTrue(ret.isEmpty());
		ret = testAlge1.valuesProjection(c1);
		assertTrue(ret.equals(ansAlge1_1));
		ret = testAlge2.valuesProjection(c1);
		assertTrue(ret.equals(ansAlge1_2));
		assertTrue(testAlge0.isEmpty());
		assertTrue(testAlge1.equals(orgAlge1));
		assertTrue(testAlge2.equals(orgAlge2));
		
		//---
		Collection<? extends BigDecimal> c2 = Arrays.asList(new BigDecimal("140.1"), new BigDecimal("40.001"));
		ret = testAlge0.valuesProjection(c2);
		assertTrue(ret.isEmpty());
		ret = testAlge1.valuesProjection(c2);
		assertTrue(ret.isEmpty());
		ret = testAlge2.valuesProjection(c2);
		assertTrue(ret.isEmpty());
		assertTrue(testAlge0.isEmpty());
		assertTrue(testAlge1.equals(orgAlge1));
		assertTrue(testAlge2.equals(orgAlge2));
	}
	
	/**
	 * {@link exalge2.Exalge#nullProjection()} のためのテスト・メソッド。
	 * @since 0.983
	 */
	public void testNullProjection() {
		Exalge testAlge0 = new Exalge();
		Exalge testAlge1 = makeAlge(
				new ExBase(hatAppleYen) , 20,
				new ExBase(hatOrangeYen), 40,
				new ExBase(hatBananaYen), 60,
				new ExBase(nohatCashYen), 120,
				new ExBase(nohatMelonYen), 0,
				new ExBase(hatMelonYen)  , 0
		);
		Exalge testAlge2 = makeAlge(
				new ExBase(hatAppleYen) , new BigDecimal("20"),
				new ExBase(hatOrangeYen), null,
				new ExBase(hatBananaYen), 60,
				new ExBase(nohatCashYen), new BigDecimal("120.1200"),
				new ExBase(nohatMelonYen), new BigDecimal("0.00"),
				new ExBase(hatMelonYen)  , null
		);
		Exalge orgAlge1 = testAlge1.copy();
		Exalge orgAlge2 = testAlge2.copy();
		
		Exalge ansAlge = makeAlge(
				new ExBase(hatOrangeYen), null,
				new ExBase(hatMelonYen)  , null
		);

		Exalge ret;
		ret = testAlge0.nullProjection();
		assertTrue(ret.isEmpty());
		ret = testAlge1.nullProjection();
		assertTrue(ret.isEmpty());
		ret = testAlge2.nullProjection();
		assertTrue(ret.equals(ansAlge));
		
		assertTrue(testAlge0.isEmpty());
		assertTrue(testAlge1.equals(orgAlge1));
		assertTrue(testAlge2.equals(orgAlge2));
	}
	
	/**
	 * {@link exalge2.Exalge#nonullProjection()} のためのテスト・メソッド。
	 * @since 0.983
	 */
	public void testNoNullProjection() {
		Exalge testAlge0 = new Exalge();
		Exalge testAlge1 = makeAlge(
				new ExBase(hatAppleYen) , 20,
				new ExBase(hatOrangeYen), 40,
				new ExBase(hatBananaYen), 60,
				new ExBase(nohatCashYen), 120,
				new ExBase(nohatMelonYen), 0,
				new ExBase(hatMelonYen)  , 0
		);
		Exalge testAlge2 = makeAlge(
				new ExBase(hatAppleYen) , new BigDecimal("20"),
				new ExBase(hatOrangeYen), null,
				new ExBase(hatBananaYen), 60,
				new ExBase(nohatCashYen), new BigDecimal("120.1200"),
				new ExBase(nohatMelonYen), new BigDecimal("0.00"),
				new ExBase(hatMelonYen)  , null
		);
		Exalge orgAlge1 = testAlge1.copy();
		Exalge orgAlge2 = testAlge2.copy();
		
		Exalge ansAlge = makeAlge(
				new ExBase(hatAppleYen) , new BigDecimal("20"),
				new ExBase(hatBananaYen), 60,
				new ExBase(nohatCashYen), new BigDecimal("120.1200"),
				new ExBase(nohatMelonYen), new BigDecimal("0.00")
		);
		
		Exalge ret;
		ret = testAlge0.nonullProjection();
		assertTrue(ret.isEmpty());
		ret = testAlge1.nonullProjection();
		assertTrue(ret.equals(orgAlge1));
		ret = testAlge2.nonullProjection();
		assertTrue(ret.equals(ansAlge));
		
		assertTrue(testAlge0.isEmpty());
		assertTrue(testAlge1.equals(orgAlge1));
		assertTrue(testAlge2.equals(orgAlge2));
	}
	
	/**
	 * {@link exalge2.Exalge#zeroProjection()} のためのテスト・メソッド。
	 * @since 0.984
	 */
	public void testZeroProjection() {
		Exalge testAlge0 = new Exalge();
		Exalge testAlge1 = makeAlge(
				new ExBase(hatAppleYen) , 20,
				new ExBase(hatOrangeYen), 40,
				new ExBase(hatBananaYen), 60,
				new ExBase(nohatCashYen), 120,
				new ExBase(nohatMelonYen), 0,
				new ExBase(hatMelonYen)  , 0
		);
		Exalge testAlge2 = makeAlge(
				new ExBase(hatAppleYen) , new BigDecimal("20"),
				new ExBase(hatOrangeYen), null,
				new ExBase(hatBananaYen), 60,
				new ExBase(nohatCashYen), new BigDecimal("120.1200"),
				new ExBase(nohatMelonYen), new BigDecimal("0.00"),
				new ExBase(hatMelonYen)  , null
		);
		Exalge testAlge3 = makeAlge(
				new ExBase(hatAppleYen) , 0,
				new ExBase(hatOrangeYen), 0,
				new ExBase(hatBananaYen), 0,
				new ExBase(nohatCashYen), 0,
				new ExBase(nohatMelonYen), 0,
				new ExBase(hatMelonYen)  , 0
		);
		Exalge orgAlge1 = testAlge1.copy();
		Exalge orgAlge2 = testAlge2.copy();
		Exalge orgAlge3 = testAlge3.copy();
		
		Exalge ansAlge1 = makeAlge(
				new ExBase(nohatMelonYen), 0,
				new ExBase(hatMelonYen), 0
		);
		
		Exalge ansAlge2 = makeAlge(
				new ExBase(nohatMelonYen), 0
		);
		
		Exalge ansAlge3 = makeAlge(
				new ExBase(hatAppleYen) , 0,
				new ExBase(hatOrangeYen), 0,
				new ExBase(hatBananaYen), 0,
				new ExBase(nohatCashYen), 0,
				new ExBase(nohatMelonYen), 0,
				new ExBase(hatMelonYen)  , 0
		);

		Exalge ret;
		ret = testAlge0.zeroProjection();
		assertTrue(ret.isEmpty());
		ret = testAlge1.zeroProjection();
		assertTrue(ret.equals(ansAlge1));
		ret = testAlge2.zeroProjection();
		assertTrue(ret.equals(ansAlge2));
		ret = testAlge3.zeroProjection();
		assertTrue(ret.equals(ansAlge3));
		
		assertTrue(testAlge0.isEmpty());
		assertTrue(testAlge1.equals(orgAlge1));
		assertTrue(testAlge2.equals(orgAlge2));
		assertTrue(testAlge3.equals(orgAlge3));
	}
	
	/**
	 * {@link exalge2.Exalge#notzeroProjection()} のためのテスト・メソッド。
	 * @since 0.984
	 */
	public void testNotZeroProjection() {
		Exalge testAlge0 = new Exalge();
		Exalge testAlge1 = makeAlge(
				new ExBase(hatAppleYen) , 20,
				new ExBase(hatOrangeYen), 40,
				new ExBase(hatBananaYen), 60,
				new ExBase(nohatCashYen), 120,
				new ExBase(nohatMelonYen), 0,
				new ExBase(hatMelonYen)  , 0
		);
		Exalge testAlge2 = makeAlge(
				new ExBase(hatAppleYen) , new BigDecimal("20"),
				new ExBase(hatOrangeYen), null,
				new ExBase(hatBananaYen), 60,
				new ExBase(nohatCashYen), new BigDecimal("120.1200"),
				new ExBase(nohatMelonYen), new BigDecimal("0.00"),
				new ExBase(hatMelonYen)  , null
		);
		Exalge testAlge3 = makeAlge(
				new ExBase(hatAppleYen) , 0,
				new ExBase(hatOrangeYen), 0,
				new ExBase(hatBananaYen), 0,
				new ExBase(nohatCashYen), 0,
				new ExBase(nohatMelonYen), 0,
				new ExBase(hatMelonYen)  , 0
		);
		Exalge orgAlge1 = testAlge1.copy();
		Exalge orgAlge2 = testAlge2.copy();
		Exalge orgAlge3 = testAlge3.copy();
		
		Exalge ansAlge1 = makeAlge(
				new ExBase(hatAppleYen) , 20,
				new ExBase(hatOrangeYen), 40,
				new ExBase(hatBananaYen), 60,
				new ExBase(nohatCashYen), 120
		);
		
		Exalge ansAlge2 = makeAlge(
				new ExBase(hatAppleYen) , new BigDecimal("20"),
				new ExBase(hatOrangeYen), null,
				new ExBase(hatBananaYen), 60,
				new ExBase(nohatCashYen), new BigDecimal("120.1200"),
				new ExBase(hatMelonYen)  , null
		);

		Exalge ret;
		ret = testAlge0.notzeroProjection();
		assertTrue(ret.isEmpty());
		ret = testAlge1.notzeroProjection();
		assertTrue(ret.equals(ansAlge1));
		ret = testAlge2.notzeroProjection();
		assertTrue(ret.equals(ansAlge2));
		ret = testAlge3.notzeroProjection();
		assertTrue(ret.isEmpty());
		
		assertTrue(testAlge0.isEmpty());
		assertTrue(testAlge1.equals(orgAlge1));
		assertTrue(testAlge2.equals(orgAlge2));
		assertTrue(testAlge3.equals(orgAlge3));
	}
}
