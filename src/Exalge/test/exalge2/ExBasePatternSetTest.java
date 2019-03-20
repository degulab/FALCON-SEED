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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import junit.framework.TestCase;

public class ExBasePatternSetTest extends TestCase {
	
	static private final String keyName = "名前";
	static private final String keyUnit = "単位";
	static private final String keyTime = "時間";
	static private final String keySubject = "サブジェクト";

	//static private final String nohat____ = "*-NO_HAT-*-*-*";
	//static private final String hat____ = "*-HAT-*-*-*";
	//static private final String wc____ = "*-*-*-*-*";
	static private final String nohatN___ = keyName + "-NO_HAT-*-*-*";
	static private final String hatN___ = keyName + "-HAT-*-*-*";
	static private final String wcN___ = keyName + "-*-*-*-*";
	static private final String nohatNU__ = keyName + "-NO_HAT-" + keyUnit + "-*-*";
	static private final String hatNU__ = keyName + "-HAT-" + keyUnit + "-*-*";
	static private final String wcNU__ = keyName + "-*-" + keyUnit + "-*-*";
	static private final String nohatNUT_ = keyName + "-NO_HAT-" + keyUnit + "-" + keyTime + "-*";
	static private final String hatNUT_ = keyName + "-HAT-" + keyUnit + "-" + keyTime + "-*";
	static private final String wcNUT_ = keyName + "-*-" + keyUnit + "-" + keyTime + "-*";
	static private final String nohatNUTS = keyName + "-NO_HAT-" + keyUnit + "-" + keyTime + "-" + keySubject;
	static private final String hatNUTS = keyName + "-HAT-" + keyUnit + "-" + keyTime + "-" + keySubject;
	static private final String wcNUTS = keyName + "-*-" + keyUnit + "-" + keyTime + "-" + keySubject;
	
	static private final String nohatN_TS = keyName + "-NO_HAT-*-" + keyTime + "-" + keySubject;
	static private final String hatN_TS = keyName + "-HAT-*-" + keyTime + "-" + keySubject;
	static private final String wcN_TS = keyName + "-*-*-" + keyTime + "-" + keySubject;
	static private final String nohatNU_S = keyName + "-NO_HAT-" + keyUnit + "-*-" + keySubject;
	static private final String hatNU_S = keyName + "-HAT-" + keyUnit + "-*-" + keySubject;
	static private final String wcNU_S = keyName + "-*-" + keyUnit + "-*-" + keySubject;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * {@link exalge2.ExBasePatternSet#ExBasePatternSet(exalge2.ExBaseSet, boolean)} のためのテスト・メソッド。
	 * @since 0.94
	 */
	public void testExBasePatternSetExBaseSetBoolean() {
		String keyName = "名前";
		String keyUnit = "単位";
		String keyTime = "時間";
		String keySubject = "主体";
		ExBase base1 = new ExBase(keyName, ExBase.NO_HAT);
		ExBase base2 = new ExBase(keyName, ExBase.HAT, keyUnit);
		ExBase base3 = new ExBase(keyName, ExBase.NO_HAT, keyUnit, keyTime);
		ExBase base4 = new ExBase(keyName, ExBase.HAT, keyUnit, keyTime, keySubject);
		ExBase base5 = new ExBase(ExBasePattern.WILDCARD,
									ExBase.HAT,
									ExBasePattern.WILDCARD,
									ExBasePattern.WILDCARD,
									ExBasePattern.WILDCARD);
		ExBase base6 = new ExBase(ExBasePattern.WILDCARD,
									ExBase.HAT,
									keyUnit + ExBasePattern.WILDCARD,
									ExBasePattern.WILDCARD + keyTime,
									ExBasePattern.WILDCARD + keySubject + ExBasePattern.WILDCARD);
		String strbase1f = makeKeyString(keyName, ExBase.NO_HAT, ExBase.OMITTED, ExBase.OMITTED, ExBase.OMITTED);
		String strbase1t = makeKeyString(keyName, ExBasePattern.WILDCARD, ExBase.OMITTED, ExBase.OMITTED, ExBase.OMITTED);
		String strbase2f = makeKeyString(keyName, ExBase.HAT, keyUnit, ExBase.OMITTED, ExBase.OMITTED);
		String strbase2t = makeKeyString(keyName, ExBasePattern.WILDCARD, keyUnit, ExBase.OMITTED, ExBase.OMITTED);
		String strbase3f = makeKeyString(keyName, ExBase.NO_HAT, keyUnit, keyTime, ExBase.OMITTED);
		String strbase3t = makeKeyString(keyName, ExBasePattern.WILDCARD, keyUnit, keyTime, ExBase.OMITTED);
		String strbase4f = makeKeyString(keyName, ExBase.HAT, keyUnit, keyTime, keySubject);
		String strbase4t = makeKeyString(keyName, ExBasePattern.WILDCARD, keyUnit, keyTime, keySubject);
		String strbase5f = makeKeyString(ExBasePattern.WILDCARD, ExBase.HAT, ExBasePattern.WILDCARD, ExBasePattern.WILDCARD, ExBasePattern.WILDCARD);
		String strbase5t = makeKeyString(ExBasePattern.WILDCARD, ExBasePattern.WILDCARD, ExBasePattern.WILDCARD, ExBasePattern.WILDCARD, ExBasePattern.WILDCARD);
		String strbase6f = makeKeyString(ExBasePattern.WILDCARD,
										ExBase.HAT,
										keyUnit + ExBasePattern.WILDCARD,
										ExBasePattern.WILDCARD + keyTime,
										ExBasePattern.WILDCARD + keySubject + ExBasePattern.WILDCARD);
		String strbase6t = makeKeyString(ExBasePattern.WILDCARD,
										ExBasePattern.WILDCARD,
										keyUnit + ExBasePattern.WILDCARD,
										ExBasePattern.WILDCARD + keyTime,
										ExBasePattern.WILDCARD + keySubject + ExBasePattern.WILDCARD);
		
		ExBaseSet baseset = new ExBaseSet();
		baseset.add(base1);
		baseset.add(base2);
		baseset.add(base3);
		baseset.add(base4);
		baseset.add(base5);
		baseset.add(base6);
		
		ExBasePatternSet patsetF = new ExBasePatternSet(baseset, false);
		ExBasePatternSet patsetT = new ExBasePatternSet(baseset, true);
		
		ArrayList<String> strF = new ArrayList<String>();
		strF.add(strbase1f);
		strF.add(strbase2f);
		strF.add(strbase3f);
		strF.add(strbase4f);
		strF.add(strbase5f);
		strF.add(strbase6f);
		ArrayList<String> strT = new ArrayList<String>();
		strT.add(strbase1t);
		strT.add(strbase2t);
		strT.add(strbase3t);
		strT.add(strbase4t);
		strT.add(strbase5t);
		strT.add(strbase6t);
		
		int i = 0;
		Iterator<ExBasePattern> it;
		
		i = 0;
		it = patsetF.iterator();
		while (it.hasNext()) {
			assertEquals(it.next().key(), strF.get(i++));
		}
		assertEquals(i, strF.size());
		
		i = 0;
		it = patsetT.iterator();
		while (it.hasNext()) {
			assertEquals(it.next().key(), strT.get(i++));
		}
		assertEquals(i, strT.size());
	}

	/**
	 * {@link exalge2.ExBasePatternSet#add(exalge2.ExBasePattern)} のためのテスト・メソッド。
	 * @since 0.94
	 */
	public void testAddExBasePattern() {
		ExBasePattern[] data = new ExBasePattern[]{
			new ExBasePattern(nohatN___), new ExBasePattern(hatN___), new ExBasePattern(wcN___),
			new ExBasePattern(nohatNU__), new ExBasePattern(hatNU__), new ExBasePattern(wcNU__),
			new ExBasePattern(nohatNUT_), new ExBasePattern(hatNUT_), new ExBasePattern(wcNUT_),
			new ExBasePattern(nohatNUTS), new ExBasePattern(hatNUTS), new ExBasePattern(wcNUTS),
			new ExBasePattern(nohatN_TS), new ExBasePattern(hatN_TS), new ExBasePattern(wcN_TS),
			new ExBasePattern(nohatNU_S), new ExBasePattern(hatNU_S), new ExBasePattern(wcNU_S),
		};
		
		ExBasePatternSet patset = new ExBasePatternSet();
		assertTrue(patset.isEmpty());
		
		for (int i = 0; i < data.length; i++) {
			assertTrue(patset.add(data[i]));
		}
		
		assertTrue(patset.containsAll(Arrays.asList(data)));
	}

	/**
	 * {@link exalge2.ExBasePatternSet#addAll(java.util.Collection)} のためのテスト・メソッド。
	 * @since 0.94
	 */
	public void testAddAllCollectionOfQextendsExBasePattern() {
		ExBasePattern[] data = new ExBasePattern[]{
			new ExBasePattern(nohatN___), new ExBasePattern(hatN___), new ExBasePattern(wcN___),
			new ExBasePattern(nohatNU__), new ExBasePattern(hatNU__), new ExBasePattern(wcNU__),
			new ExBasePattern(nohatNUT_), new ExBasePattern(hatNUT_), new ExBasePattern(wcNUT_),
			new ExBasePattern(nohatNUTS), new ExBasePattern(hatNUTS), new ExBasePattern(wcNUTS),
			new ExBasePattern(nohatN_TS), new ExBasePattern(hatN_TS), new ExBasePattern(wcN_TS),
			new ExBasePattern(nohatNU_S), new ExBasePattern(hatNU_S), new ExBasePattern(wcNU_S),
		};
		
		ExBasePatternSet patset = new ExBasePatternSet();
		assertTrue(patset.isEmpty());
		
		assertTrue(patset.addAll(Arrays.asList(data)));
		assertTrue(patset.containsAll(Arrays.asList(data)));
	}

	/**
	 * {@link exalge2.ExBasePatternSet#matches(exalge2.ExBase)} のためのテスト・メソッド。
	 * @since 0.94
	 */
	public void testMatches() {
		ExBasePattern[] data = new ExBasePattern[]{
			new ExBasePattern(wcNU__),
			new ExBasePattern(wcNUT_),
			new ExBasePattern(wcNUTS),
			new ExBasePattern(wcN_TS),
			new ExBasePattern(wcNU_S),
		};
		ExBasePatternSet patset = new ExBasePatternSet(Arrays.asList(data));
		ExBase baseExist = new ExBase(keyName, ExBase.HAT, "エラー", keyTime, keySubject);
		ExBase baseNone = new ExBase("エラー", ExBase.NO_HAT, keyUnit, keyTime, keySubject);
		
		assertTrue(patset.matches(baseExist));
		assertFalse(patset.matches(baseNone));
		
		ExBasePattern[] data2 = new ExBasePattern[]{
			new ExBasePattern(hatNU__),
			new ExBasePattern(hatNUT_),
			new ExBasePattern(hatNUTS),
			new ExBasePattern(hatN_TS),
			new ExBasePattern(hatNU_S),
		};
		ExBase baseNone2 = new ExBase(keyName, ExBase.NO_HAT, "エラー", keyTime, keySubject);
		ExBasePatternSet patset2 = new ExBasePatternSet(Arrays.asList(data2));
		
		assertTrue(patset2.matches(baseExist));
		assertFalse(patset2.matches(baseNone2));
	}

	/**
	 * {@link exalge2.ExBasePatternSet#clone()} のためのテスト・メソッド。
	 * @since 0.94
	 */
	public void testClone() {
		ExBasePattern[] data = new ExBasePattern[]{
			new ExBasePattern(wcNU__),
			new ExBasePattern(wcNUT_),
			new ExBasePattern(wcNUTS),
			new ExBasePattern(wcN_TS),
			new ExBasePattern(wcNU_S),
		};
		ExBasePatternSet patset1 = new ExBasePatternSet(Arrays.asList(data));
		ExBasePatternSet patset2 = (ExBasePatternSet)patset1.clone();
		assertEquals(patset1, patset2);
		
		patset2.remove(new ExBasePattern(wcNUT_));
		patset2.remove(new ExBasePattern(wcNU_S));
		assertFalse(patset1.equals(patset2));
		
		ExBasePattern[] data2 = new ExBasePattern[]{
			new ExBasePattern(hatNU__),
			new ExBasePattern(hatNUT_),
			new ExBasePattern(hatNUTS),
			new ExBasePattern(hatN_TS),
			new ExBasePattern(hatNU_S),
		};
		ExBasePatternSet patset3 = (ExBasePatternSet)patset1.clone();
		patset3.addAll(Arrays.asList(data2));
		assertFalse(patset1.equals(patset3));
	}

	/**
	 * {@link exalge2.ExBasePatternSet#union(exalge2.ExBasePatternSet)} のためのテスト・メソッド。
	 * @since 0.94
	 */
	public void testUnion() {
		ExBasePattern[] data1 = new ExBasePattern[]{
			new ExBasePattern(nohatN___), new ExBasePattern(hatN___), new ExBasePattern(wcN___),
			new ExBasePattern(nohatNU__), new ExBasePattern(hatNU__), new ExBasePattern(wcNU__),
			new ExBasePattern(nohatNUT_), new ExBasePattern(hatNUT_), new ExBasePattern(wcNUT_),
			new ExBasePattern(nohatNUTS), new ExBasePattern(hatNUTS), new ExBasePattern(wcNUTS),
		};
		ExBasePattern[] data2 = new ExBasePattern[]{
			new ExBasePattern(wcN___),
			new ExBasePattern(nohatNU__), new ExBasePattern(hatNU__),
			new ExBasePattern(nohatN_TS), new ExBasePattern(hatN_TS), new ExBasePattern(wcN_TS),
			new ExBasePattern(nohatNU_S), new ExBasePattern(hatNU_S), new ExBasePattern(wcNU_S),
		};
		ExBasePattern[] data3 = new ExBasePattern[]{
			new ExBasePattern(nohatN___), new ExBasePattern(hatN___), new ExBasePattern(wcN___),
			new ExBasePattern(nohatNU__), new ExBasePattern(hatNU__), new ExBasePattern(wcNU__),
			new ExBasePattern(nohatNUT_), new ExBasePattern(hatNUT_), new ExBasePattern(wcNUT_),
			new ExBasePattern(nohatNUTS), new ExBasePattern(hatNUTS), new ExBasePattern(wcNUTS),
			new ExBasePattern(nohatN_TS), new ExBasePattern(hatN_TS), new ExBasePattern(wcN_TS),
			new ExBasePattern(nohatNU_S), new ExBasePattern(hatNU_S), new ExBasePattern(wcNU_S),
		};
		
		ExBasePatternSet set1 = new ExBasePatternSet(Arrays.asList(data1));
		ExBasePatternSet set2 = new ExBasePatternSet(Arrays.asList(data2));
		ExBasePatternSet set3 = new ExBasePatternSet(Arrays.asList(data3));
		
		assertTrue(set1.containsAll(Arrays.asList(data1)));
		assertEquals(set1.size(), data1.length);
		assertTrue(set2.containsAll(Arrays.asList(data2)));
		assertEquals(set2.size(), data2.length);
		assertTrue(set3.containsAll(Arrays.asList(data3)));
		assertEquals(set3.size(), data3.length);
		
		ExBasePatternSet ret1 = set1.union(set2);
		ExBasePatternSet ret2 = set2.union(set1);
		
		assertEquals(ret1, ret2);
		assertEquals(ret1, set3);
		assertEquals(ret2, set3);
	}

	/**
	 * {@link exalge2.ExBasePatternSet#intersection(exalge2.ExBasePatternSet)} のためのテスト・メソッド。
	 * @since 0.94
	 */
	public void testIntersection() {
		ExBasePattern[] data1 = new ExBasePattern[]{
			new ExBasePattern(nohatN___), new ExBasePattern(hatN___), new ExBasePattern(wcN___),
			new ExBasePattern(nohatNU__), new ExBasePattern(hatNU__), new ExBasePattern(wcNU__),
			new ExBasePattern(nohatNUT_), new ExBasePattern(hatNUT_), new ExBasePattern(wcNUT_),
			new ExBasePattern(nohatNUTS), new ExBasePattern(hatNUTS), new ExBasePattern(wcNUTS),
		};
		ExBasePattern[] data2 = new ExBasePattern[]{
			new ExBasePattern(wcN___),
			new ExBasePattern(nohatNU__), new ExBasePattern(hatNU__),
			new ExBasePattern(nohatN_TS), new ExBasePattern(hatN_TS), new ExBasePattern(wcN_TS),
			new ExBasePattern(nohatNU_S), new ExBasePattern(hatNU_S), new ExBasePattern(wcNU_S),
		};
		ExBasePattern[] data3 = new ExBasePattern[]{
			new ExBasePattern(wcN___),
			new ExBasePattern(nohatNU__), new ExBasePattern(hatNU__),
		};
		
		ExBasePatternSet set1 = new ExBasePatternSet(Arrays.asList(data1));
		ExBasePatternSet set2 = new ExBasePatternSet(Arrays.asList(data2));
		ExBasePatternSet set3 = new ExBasePatternSet(Arrays.asList(data3));
		
		assertTrue(set1.containsAll(Arrays.asList(data1)));
		assertEquals(set1.size(), data1.length);
		assertTrue(set2.containsAll(Arrays.asList(data2)));
		assertEquals(set2.size(), data2.length);
		assertTrue(set3.containsAll(Arrays.asList(data3)));
		assertEquals(set3.size(), data3.length);
		
		ExBasePatternSet ret1 = set1.intersection(set2);
		ExBasePatternSet ret2 = set2.intersection(set1);
		
		assertEquals(ret1, ret2);
		assertEquals(ret1, set3);
		assertEquals(ret2, set3);
	}

	/**
	 * {@link exalge2.ExBasePatternSet#difference(exalge2.ExBasePatternSet)} のためのテスト・メソッド。
	 * @since 0.94
	 */
	public void testDifference() {
		ExBasePattern[] data1 = new ExBasePattern[]{
			new ExBasePattern(nohatN___), new ExBasePattern(hatN___), new ExBasePattern(wcN___),
			new ExBasePattern(nohatNU__), new ExBasePattern(hatNU__), new ExBasePattern(wcNU__),
			new ExBasePattern(nohatNUT_), new ExBasePattern(hatNUT_), new ExBasePattern(wcNUT_),
			new ExBasePattern(nohatNUTS), new ExBasePattern(hatNUTS), new ExBasePattern(wcNUTS),
		};
		ExBasePattern[] data2 = new ExBasePattern[]{
			new ExBasePattern(wcN___),
			new ExBasePattern(nohatNU__), new ExBasePattern(hatNU__),
			new ExBasePattern(nohatN_TS), new ExBasePattern(hatN_TS), new ExBasePattern(wcN_TS),
			new ExBasePattern(nohatNU_S), new ExBasePattern(hatNU_S), new ExBasePattern(wcNU_S),
		};
		ExBasePattern[] data3 = new ExBasePattern[]{
			new ExBasePattern(nohatN___), new ExBasePattern(hatN___),
			new ExBasePattern(wcNU__),
			new ExBasePattern(nohatNUT_), new ExBasePattern(hatNUT_), new ExBasePattern(wcNUT_),
			new ExBasePattern(nohatNUTS), new ExBasePattern(hatNUTS), new ExBasePattern(wcNUTS),
		};
		ExBasePattern[] data4 = new ExBasePattern[]{
			new ExBasePattern(nohatN_TS), new ExBasePattern(hatN_TS), new ExBasePattern(wcN_TS),
			new ExBasePattern(nohatNU_S), new ExBasePattern(hatNU_S), new ExBasePattern(wcNU_S),
		};
			
		ExBasePatternSet set1 = new ExBasePatternSet(Arrays.asList(data1));
		ExBasePatternSet set2 = new ExBasePatternSet(Arrays.asList(data2));
		ExBasePatternSet set3 = new ExBasePatternSet(Arrays.asList(data3));
		ExBasePatternSet set4 = new ExBasePatternSet(Arrays.asList(data4));
			
		assertTrue(set1.containsAll(Arrays.asList(data1)));
		assertEquals(set1.size(), data1.length);
		assertTrue(set2.containsAll(Arrays.asList(data2)));
		assertEquals(set2.size(), data2.length);
		assertTrue(set3.containsAll(Arrays.asList(data3)));
		assertEquals(set3.size(), data3.length);
		assertTrue(set4.containsAll(Arrays.asList(data4)));
		assertEquals(set4.size(), data4.length);
			
		ExBasePatternSet ret1 = set1.difference(set2);
		ExBasePatternSet ret2 = set2.difference(set1);
		
		assertFalse(ret1.equals(ret2));
		assertEquals(ret1, set3);
		assertEquals(ret2, set4);
	}

	/**
	 * Test {@link ExBasePattern#setHat()}
	 * @since 0.960
	 */
	public void testSetHat() {
		ExBasePattern nBase = new ExBasePattern(nohatNUTS);
		ExBasePattern hBase = new ExBasePattern(hatNUTS);
		ExBasePattern wBase = new ExBasePattern(wcNUTS);
		assertEquals(ExBase.NO_HAT, nBase.getHatKey());
		assertEquals(ExBase.HAT, hBase.getHatKey());
		assertEquals(ExBasePattern.WILDCARD, wBase.getHatKey());
		
		ExBasePattern test1 = new ExBasePattern(nohatNUTS);
		ExBasePattern ret1 = test1.setHat();
		assertTrue(nBase.equals(test1));
		assertTrue(hBase.equals(ret1));
		
		ExBasePattern test2 = new ExBasePattern(hatNUTS);
		ExBasePattern ret2 = test2.setHat();
		assertTrue(hBase.equals(test2));
		assertTrue(hBase.equals(ret2));
		assertSame(test2, ret2);
		
		ExBasePattern test3 = new ExBasePattern(wcNUTS);
		ExBasePattern ret3 = test3.setHat();
		assertTrue(wBase.equals(test3));
		assertTrue(hBase.equals(ret3));
	}

	/**
	 * Test {@link ExBasePattern#setNoHat()}
	 * @since 0.960
	 */
	public void testSetNoHat() {
		ExBasePattern nBase = new ExBasePattern(nohatNUTS);
		ExBasePattern hBase = new ExBasePattern(hatNUTS);
		ExBasePattern wBase = new ExBasePattern(wcNUTS);
		assertEquals(ExBase.NO_HAT, nBase.getHatKey());
		assertEquals(ExBase.HAT, hBase.getHatKey());
		assertEquals(ExBasePattern.WILDCARD, wBase.getHatKey());
		
		ExBasePattern test1 = new ExBasePattern(nohatNUTS);
		ExBasePattern ret1 = test1.setNoHat();
		assertTrue(nBase.equals(test1));
		assertTrue(nBase.equals(ret1));
		assertSame(test1, ret1);
		
		ExBasePattern test2 = new ExBasePattern(hatNUTS);
		ExBasePattern ret2 = test2.setNoHat();
		assertTrue(hBase.equals(test2));
		assertTrue(nBase.equals(ret2));
		
		ExBasePattern test3 = new ExBasePattern(wcNUTS);
		ExBasePattern ret3 = test3.setNoHat();
		assertTrue(wBase.equals(test3));
		assertTrue(nBase.equals(ret3));
	}
	
	/**
	 * Test {@link ExBasePattern#setWildcardHat()}
	 * @since 0.960
	 */
	public void testSetWildcardHat() {
		ExBasePattern nBase = new ExBasePattern(nohatNUTS);
		ExBasePattern hBase = new ExBasePattern(hatNUTS);
		ExBasePattern wBase = new ExBasePattern(wcNUTS);
		assertEquals(ExBase.NO_HAT, nBase.getHatKey());
		assertEquals(ExBase.HAT, hBase.getHatKey());
		assertEquals(ExBasePattern.WILDCARD, wBase.getHatKey());
		
		ExBasePattern test1 = new ExBasePattern(nohatNUTS);
		ExBasePattern ret1 = test1.setWildcardHat();
		assertTrue(nBase.equals(test1));
		assertTrue(wBase.equals(ret1));
		
		ExBasePattern test2 = new ExBasePattern(hatNUTS);
		ExBasePattern ret2 = test2.setWildcardHat();
		assertTrue(hBase.equals(test2));
		assertTrue(wBase.equals(ret2));
		
		ExBasePattern test3 = new ExBasePattern(wcNUTS);
		ExBasePattern ret3 = test3.setWildcardHat();
		assertTrue(wBase.equals(test3));
		assertTrue(wBase.equals(ret3));
		assertSame(test3, ret3);
	}

	/**
	 * {@link exalge2.ExBasePatternSet#addition(ExBasePatternSet)} のためのテスト・メソッド。
	 * @since 0.982
	 */
	public void testAdditionExBasePatternSet() {
		ExBasePattern[] data1 = new ExBasePattern[]{
			new ExBasePattern(nohatN___), new ExBasePattern(hatN___), new ExBasePattern(wcN___),
			new ExBasePattern(nohatNU__), new ExBasePattern(hatNU__), new ExBasePattern(wcNU__),
			new ExBasePattern(nohatNUT_), new ExBasePattern(hatNUT_), new ExBasePattern(wcNUT_),
			new ExBasePattern(nohatNUTS), new ExBasePattern(hatNUTS), new ExBasePattern(wcNUTS),
		};
		ExBasePattern[] data2 = new ExBasePattern[]{
			new ExBasePattern(wcN___),
			new ExBasePattern(nohatNU__), new ExBasePattern(hatNU__),
			new ExBasePattern(nohatN_TS), new ExBasePattern(hatN_TS), new ExBasePattern(wcN_TS),
			new ExBasePattern(nohatNU_S), new ExBasePattern(hatNU_S), new ExBasePattern(wcNU_S),
		};
		ExBasePattern[] data3 = new ExBasePattern[]{
			new ExBasePattern(nohatN___), new ExBasePattern(hatN___), new ExBasePattern(wcN___),
			new ExBasePattern(nohatNU__), new ExBasePattern(hatNU__), new ExBasePattern(wcNU__),
			new ExBasePattern(nohatNUT_), new ExBasePattern(hatNUT_), new ExBasePattern(wcNUT_),
			new ExBasePattern(nohatNUTS), new ExBasePattern(hatNUTS), new ExBasePattern(wcNUTS),
			new ExBasePattern(nohatN_TS), new ExBasePattern(hatN_TS), new ExBasePattern(wcN_TS),
			new ExBasePattern(nohatNU_S), new ExBasePattern(hatNU_S), new ExBasePattern(wcNU_S),
		};
		
		ExBasePatternSet set1 = new ExBasePatternSet(Arrays.asList(data1));
		ExBasePatternSet set2 = new ExBasePatternSet(Arrays.asList(data2));
		ExBasePatternSet set3 = new ExBasePatternSet(Arrays.asList(data3));
		
		assertTrue(set1.containsAll(Arrays.asList(data1)));
		assertEquals(set1.size(), data1.length);
		assertTrue(set2.containsAll(Arrays.asList(data2)));
		assertEquals(set2.size(), data2.length);
		assertTrue(set3.containsAll(Arrays.asList(data3)));
		assertEquals(set3.size(), data3.length);
		
		ExBasePatternSet ret1 = set1.addition(set2);
		ExBasePatternSet ret2 = set2.addition(set1);
		
		assertEquals(ret1, ret2);
		assertEquals(ret1, set3);
		assertEquals(ret2, set3);
	}

	/**
	 * {@link exalge2.ExBasePatternSet#subtraction(ExBasePatternSet)} のためのテスト・メソッド。
	 * @since 0.982
	 */
	public void testSubtractionExBasePatternSet() {
		ExBasePattern[] data1 = new ExBasePattern[]{
			new ExBasePattern(nohatN___), new ExBasePattern(hatN___), new ExBasePattern(wcN___),
			new ExBasePattern(nohatNU__), new ExBasePattern(hatNU__), new ExBasePattern(wcNU__),
			new ExBasePattern(nohatNUT_), new ExBasePattern(hatNUT_), new ExBasePattern(wcNUT_),
			new ExBasePattern(nohatNUTS), new ExBasePattern(hatNUTS), new ExBasePattern(wcNUTS),
		};
		ExBasePattern[] data2 = new ExBasePattern[]{
			new ExBasePattern(wcN___),
			new ExBasePattern(nohatNU__), new ExBasePattern(hatNU__),
			new ExBasePattern(nohatN_TS), new ExBasePattern(hatN_TS), new ExBasePattern(wcN_TS),
			new ExBasePattern(nohatNU_S), new ExBasePattern(hatNU_S), new ExBasePattern(wcNU_S),
		};
		ExBasePattern[] data3 = new ExBasePattern[]{
			new ExBasePattern(nohatN___), new ExBasePattern(hatN___),
			new ExBasePattern(wcNU__),
			new ExBasePattern(nohatNUT_), new ExBasePattern(hatNUT_), new ExBasePattern(wcNUT_),
			new ExBasePattern(nohatNUTS), new ExBasePattern(hatNUTS), new ExBasePattern(wcNUTS),
		};
		ExBasePattern[] data4 = new ExBasePattern[]{
			new ExBasePattern(nohatN_TS), new ExBasePattern(hatN_TS), new ExBasePattern(wcN_TS),
			new ExBasePattern(nohatNU_S), new ExBasePattern(hatNU_S), new ExBasePattern(wcNU_S),
		};
			
		ExBasePatternSet set1 = new ExBasePatternSet(Arrays.asList(data1));
		ExBasePatternSet set2 = new ExBasePatternSet(Arrays.asList(data2));
		ExBasePatternSet set3 = new ExBasePatternSet(Arrays.asList(data3));
		ExBasePatternSet set4 = new ExBasePatternSet(Arrays.asList(data4));
			
		assertTrue(set1.containsAll(Arrays.asList(data1)));
		assertEquals(set1.size(), data1.length);
		assertTrue(set2.containsAll(Arrays.asList(data2)));
		assertEquals(set2.size(), data2.length);
		assertTrue(set3.containsAll(Arrays.asList(data3)));
		assertEquals(set3.size(), data3.length);
		assertTrue(set4.containsAll(Arrays.asList(data4)));
		assertEquals(set4.size(), data4.length);
			
		ExBasePatternSet ret1 = set1.subtraction(set2);
		ExBasePatternSet ret2 = set2.subtraction(set1);
		
		assertFalse(ret1.equals(ret2));
		assertEquals(ret1, set3);
		assertEquals(ret2, set4);
	}
	
	//-----------------------------------------------------------
	//
	//-----------------------------------------------------------
	
	private String makeKeyString(String nameKey, String hatKey, String unitKey, String timeKey, String subjectKey) {
		return (nameKey + "-" + hatKey + "-" + unitKey + "-" + timeKey + "-" + subjectKey);
	}

}
