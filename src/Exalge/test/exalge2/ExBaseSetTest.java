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

import java.util.Arrays;

import junit.framework.TestCase;

public class ExBaseSetTest extends TestCase {
	
	static private final String keyName = "名前";
	static private final String keyUnit = "単位";
	static private final String keyTime = "時間";
	static private final String keySubject = "サブジェクト";
	
	static private final String nohatN___ = keyName + "-NO_HAT-#-#-#";
	static private final String hatN___ = keyName + "-HAT-#-#-#";
	static private final String nohatNU__ = keyName + "-NO_HAT-" + keyUnit + "-#-#";
	static private final String hatNU__ = keyName + "-HAT-" + keyUnit + "-#-#";
	static private final String nohatNUT_ = keyName + "-NO_HAT-" + keyUnit + "-" + keyTime + "-#";
	static private final String hatNUT_ = keyName + "-HAT-" + keyUnit + "-" + keyTime + "-#";
	static private final String nohatNUTS = keyName + "-NO_HAT-" + keyUnit + "-" + keyTime + "-" + keySubject;
	static private final String hatNUTS = keyName + "-HAT-" + keyUnit + "-" + keyTime + "-" + keySubject;
	
	static private final String nohatN_TS = keyName + "-NO_HAT-#-" + keyTime + "-" + keySubject;
	static private final String hatN_TS = keyName + "-HAT-#-" + keyTime + "-" + keySubject;
	static private final String nohatNU_S = keyName + "-NO_HAT-" + keyUnit + "-#-" + keySubject;
	static private final String hatNU_S = keyName + "-HAT-" + keyUnit + "-#-" + keySubject;
	static protected final String nohatAppleYen	= "りんご-NO_HAT-円-#-#";
	static protected final String nohatAppleYenQ1	= "りんご-NO_HAT-円-Y2007Q1-#";
	static protected final String nohatAppleYenQ2	= "りんご-NO_HAT-円-Y2007Q2-#";
	static protected final String nohatAppleYenQ3	= "りんご-NO_HAT-円-Y2007Q3-#";
	static protected final String nohatAppleYenQ4	= "りんご-NO_HAT-円-Y2007Q4-#";
	static protected final String hatAppleYen		= "りんご-HAT-円-#-#";
	static protected final String hatAppleYenQ1	= "りんご-HAT-円-Y2007Q1-#";
	static protected final String hatAppleYenQ2	= "りんご-HAT-円-Y2007Q2-#";
	static protected final String hatAppleYenQ3	= "りんご-HAT-円-Y2007Q3-#";
	static protected final String hatAppleYenQ4	= "りんご-HAT-円-Y2007Q4-#";
	
	static protected final String nohatOrangeYen		= "みかん-NO_HAT-円-#-#";
	static protected final String nohatOrangeYenQ1	= "みかん-NO_HAT-円-Y2007Q1-#";
	static protected final String nohatOrangeYenQ2	= "みかん-NO_HAT-円-Y2007Q2-#";
	static protected final String nohatOrangeYenQ3	= "みかん-NO_HAT-円-Y2007Q3-#";
	static protected final String nohatOrangeYenQ4	= "みかん-NO_HAT-円-Y2007Q4-#";
	static protected final String hatOrangeYen		= "みかん-HAT-円-#-#";
	static protected final String hatOrangeYenQ1		= "みかん-HAT-円-Y2007Q1-#";
	static protected final String hatOrangeYenQ2		= "みかん-HAT-円-Y2007Q2-#";
	static protected final String hatOrangeYenQ3		= "みかん-HAT-円-Y2007Q3-#";
	static protected final String hatOrangeYenQ4		= "みかん-HAT-円-Y2007Q4-#";

	static protected final String nohatBananaYen		= "ばなな-NO_HAT-円-#-#";
	static protected final String nohatBananaYenQ1	= "ばなな-NO_HAT-円-Y2007Q1-#";
	static protected final String nohatBananaYenQ2	= "ばなな-NO_HAT-円-Y2007Q2-#";
	static protected final String nohatBananaYenQ3	= "ばなな-NO_HAT-円-Y2007Q3-#";
	static protected final String nohatBananaYenQ4	= "ばなな-NO_HAT-円-Y2007Q4-#";
	static protected final String hatBananaYen		= "ばなな-HAT-円-#-#";
	static protected final String hatBananaYenQ1		= "ばなな-HAT-円-Y2007Q1-#";
	static protected final String hatBananaYenQ2		= "ばなな-HAT-円-Y2007Q2-#";
	static protected final String hatBananaYenQ3		= "ばなな-HAT-円-Y2007Q3-#";
	static protected final String hatBananaYenQ4		= "ばなな-HAT-円-Y2007Q4-#";
	
	static protected final String nohatMelonYen	= "メロン-NO_HAT-円-#-#";
	static protected final String nohatMelonYenQ1	= "メロン-NO_HAT-円-Y2007Q1-#";
	static protected final String nohatMelonYenQ2	= "メロン-NO_HAT-円-Y2007Q2-#";
	static protected final String nohatMelonYenQ3	= "メロン-NO_HAT-円-Y2007Q3-#";
	static protected final String nohatMelonYenQ4	= "メロン-NO_HAT-円-Y2007Q4-#";
	static protected final String hatMelonYen		= "メロン-HAT-円-#-#";
	static protected final String hatMelonYenQ1	= "メロン-HAT-円-Y2007Q1-#";
	static protected final String hatMelonYenQ2	= "メロン-HAT-円-Y2007Q2-#";
	static protected final String hatMelonYenQ3	= "メロン-HAT-円-Y2007Q3-#";
	static protected final String hatMelonYenQ4	= "メロン-HAT-円-Y2007Q4-#";
	
	static protected final String nohatMangoYen	= "マンゴー-NO_HAT-円-#-#";
	static protected final String nohatMangoYenQ1	= "マンゴー-NO_HAT-円-Y2007Q1-#";
	static protected final String nohatMangoYenQ2	= "マンゴー-NO_HAT-円-Y2007Q2-#";
	static protected final String nohatMangoYenQ3	= "マンゴー-NO_HAT-円-Y2007Q3-#";
	static protected final String nohatMangoYenQ4	= "マンゴー-NO_HAT-円-Y2007Q4-#";
	static protected final String hatMangoYen		= "マンゴー-HAT-円-#-#";
	static protected final String hatMangoYenQ1	= "マンゴー-HAT-円-Y2007Q1-#";
	static protected final String hatMangoYenQ2	= "マンゴー-HAT-円-Y2007Q2-#";
	static protected final String hatMangoYenQ3	= "マンゴー-HAT-円-Y2007Q3-#";
	static protected final String hatMangoYenQ4	= "マンゴー-HAT-円-Y2007Q4-#";
	
	static protected final String nohatCashYen	= "現金-NO_HAT-円-#-#";
	static protected final String nohatCashYenQ1	= "現金-NO_HAT-円-Y2007Q1-#";
	static protected final String nohatCashYenQ2	= "現金-NO_HAT-円-Y2007Q2-#";
	static protected final String nohatCashYenQ3	= "現金-NO_HAT-円-Y2007Q3-#";
	static protected final String nohatCashYenQ4	= "現金-NO_HAT-円-Y2007Q4-#";
	static protected final String hatCashYen		= "現金-HAT-円-#-#";
	static protected final String hatCashYenQ1	= "現金-HAT-円-Y2007Q1-#";
	static protected final String hatCashYenQ2	= "現金-HAT-円-Y2007Q2-#";
	static protected final String hatCashYenQ3	= "現金-HAT-円-Y2007Q3-#";
	static protected final String hatCashYenQ4	= "現金-HAT-円-Y2007Q4-#";
	
	static protected final ExBase[] baseAppleData = new ExBase[]{
		new ExBase(hatAppleYenQ1)  ,
		new ExBase(nohatCashYenQ1) ,
		new ExBase(hatAppleYenQ2)  ,
		new ExBase(nohatCashYenQ2) ,
		new ExBase(nohatAppleYenQ3),
		new ExBase(hatCashYenQ3)   ,
		new ExBase(nohatAppleYenQ4),
		new ExBase(hatCashYenQ4)   ,
	};
	
	static protected final ExBase[] baseOrangeData = new ExBase[]{
		new ExBase(hatOrangeYenQ1)  ,
		new ExBase(nohatCashYenQ1)  ,
		new ExBase(hatOrangeYenQ2)  ,
		new ExBase(nohatCashYenQ2)  ,
		new ExBase(nohatOrangeYenQ3),
		new ExBase(hatCashYenQ3)    ,
		new ExBase(nohatOrangeYenQ4),
		new ExBase(hatCashYenQ4)    ,
	};
	
	static protected final ExBase[] baseBananaData = new ExBase[]{
		new ExBase(nohatBananaYenQ1),
		new ExBase(hatCashYenQ1)    ,
		new ExBase(hatBananaYenQ2)  ,
		new ExBase(nohatCashYenQ2)  ,
		new ExBase(nohatBananaYenQ3),
		new ExBase(hatCashYenQ3)    ,
		new ExBase(hatBananaYenQ4)  ,
		new ExBase(nohatCashYenQ4)  ,
	};
	
	static protected final ExBase[] baseMelonData = new ExBase[]{
		new ExBase(nohatMelonYenQ1),
		new ExBase(hatCashYenQ1)   ,
		new ExBase(hatMelonYenQ2)  ,
		new ExBase(nohatCashYenQ2) ,
		new ExBase(nohatMelonYenQ3),
		new ExBase(hatCashYenQ3)   ,
		new ExBase(hatMelonYenQ4)  ,
		new ExBase(nohatCashYenQ4) ,
	};
	
	static protected final ExBase[] baseAllData = new ExBase[]{
		new ExBase(nohatAppleYenQ1),
		new ExBase(nohatAppleYenQ2),
		new ExBase(nohatAppleYenQ3),
		new ExBase(nohatAppleYenQ4),
		new ExBase(hatAppleYenQ1),
		new ExBase(hatAppleYenQ2),
		new ExBase(hatAppleYenQ3),
		new ExBase(hatAppleYenQ4),
		new ExBase(nohatOrangeYenQ1),
		new ExBase(nohatOrangeYenQ2),
		new ExBase(nohatOrangeYenQ3),
		new ExBase(nohatOrangeYenQ4),
		new ExBase(hatOrangeYenQ1),
		new ExBase(hatOrangeYenQ2),
		new ExBase(hatOrangeYenQ3),
		new ExBase(hatOrangeYenQ4),
		new ExBase(nohatBananaYenQ1),
		new ExBase(nohatBananaYenQ2),
		new ExBase(nohatBananaYenQ3),
		new ExBase(nohatBananaYenQ4),
		new ExBase(hatBananaYenQ1),
		new ExBase(hatBananaYenQ2),
		new ExBase(hatBananaYenQ3),
		new ExBase(hatBananaYenQ4),
		new ExBase(nohatMelonYenQ1),
		new ExBase(nohatMelonYenQ2),
		new ExBase(nohatMelonYenQ3),
		new ExBase(nohatMelonYenQ4),
		new ExBase(hatMelonYenQ1),
		new ExBase(hatMelonYenQ2),
		new ExBase(hatMelonYenQ3),
		new ExBase(hatMelonYenQ4),
		new ExBase(nohatCashYenQ1),
		new ExBase(nohatCashYenQ2),
		new ExBase(nohatCashYenQ3),
		new ExBase(nohatCashYenQ4),
		new ExBase(hatCashYenQ1),
		new ExBase(hatCashYenQ2),
		new ExBase(hatCashYenQ3),
		new ExBase(hatCashYenQ4),
	};
	
	static protected final ExBase[] baseNoHatAllData = new ExBase[]{
		new ExBase(nohatAppleYenQ1),
		new ExBase(nohatAppleYenQ2),
		new ExBase(nohatAppleYenQ3),
		new ExBase(nohatAppleYenQ4),
		new ExBase(nohatOrangeYenQ1),
		new ExBase(nohatOrangeYenQ2),
		new ExBase(nohatOrangeYenQ3),
		new ExBase(nohatOrangeYenQ4),
		new ExBase(nohatBananaYenQ1),
		new ExBase(nohatBananaYenQ2),
		new ExBase(nohatBananaYenQ3),
		new ExBase(nohatBananaYenQ4),
		new ExBase(nohatMelonYenQ1),
		new ExBase(nohatMelonYenQ2),
		new ExBase(nohatMelonYenQ3),
		new ExBase(nohatMelonYenQ4),
		new ExBase(nohatCashYenQ1),
		new ExBase(nohatCashYenQ2),
		new ExBase(nohatCashYenQ3),
		new ExBase(nohatCashYenQ4),
	};
	
	static protected final ExBase[] baseHatAllData = new ExBase[]{
		new ExBase(hatAppleYenQ1),
		new ExBase(hatAppleYenQ2),
		new ExBase(hatAppleYenQ3),
		new ExBase(hatAppleYenQ4),
		new ExBase(hatOrangeYenQ1),
		new ExBase(hatOrangeYenQ2),
		new ExBase(hatOrangeYenQ3),
		new ExBase(hatOrangeYenQ4),
		new ExBase(hatBananaYenQ1),
		new ExBase(hatBananaYenQ2),
		new ExBase(hatBananaYenQ3),
		new ExBase(hatBananaYenQ4),
		new ExBase(hatMelonYenQ1),
		new ExBase(hatMelonYenQ2),
		new ExBase(hatMelonYenQ3),
		new ExBase(hatMelonYenQ4),
		new ExBase(hatCashYenQ1),
		new ExBase(hatCashYenQ2),
		new ExBase(hatCashYenQ3),
		new ExBase(hatCashYenQ4),
	};
	
	static protected final boolean isEqual(ExBaseSet set, ExBase[] bases) {
		if (set.size() == bases.length) {
			return set.containsAll(Arrays.asList(bases));
		}
		else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * Test {@link exalge2.ExBaseSet#addition(ExBaseSet)}
	 * @since 0.94
	 */
	public void testAdditionExBaseSet() {
		ExBase[] data1 = new ExBase[]{
				new ExBase(nohatN___), new ExBase(hatN___),
				new ExBase(nohatNU__), new ExBase(hatNU__),
				new ExBase(nohatNUT_), new ExBase(hatNUT_),
				new ExBase(nohatNUTS), new ExBase(hatNUTS)
		};
		ExBase[] data2 = new ExBase[]{
				new ExBase(nohatN___), new ExBase(hatN___),
				new ExBase(nohatN_TS), new ExBase(hatN_TS),
				new ExBase(nohatNU_S), new ExBase(hatNU_S)
		};
		ExBase[] data3 = new ExBase[]{
				new ExBase(nohatN___), new ExBase(hatN___),
				new ExBase(nohatNU__), new ExBase(hatNU__),
				new ExBase(nohatNUT_), new ExBase(hatNUT_),
				new ExBase(nohatNUTS), new ExBase(hatNUTS),
				new ExBase(nohatN_TS), new ExBase(hatN_TS),
				new ExBase(nohatNU_S), new ExBase(hatNU_S),
		};
		
		ExBaseSet set1 = new ExBaseSet(Arrays.asList(data1));
		ExBaseSet set2 = new ExBaseSet(Arrays.asList(data2));
		
		ExBaseSet ret1 = set1.addition(set2);
		assertTrue(isEqual(set1, data1));
		assertTrue(isEqual(set2, data2));
		assertTrue(isEqual(ret1, data3));
		
		ExBaseSet ret2 = set2.addition(set1);
		assertTrue(isEqual(set1, data1));
		assertTrue(isEqual(set2, data2));
		assertTrue(isEqual(ret2, data3));
	}

	/**
	 * Test {@link exalge2.ExBaseSet#subtraction(ExBaseSet)}
	 * @since 0.94
	 */
	public void testSubtractionExBaseSet() {
		ExBase[] data1 = new ExBase[]{
				new ExBase(nohatN___), new ExBase(hatN___),
				new ExBase(nohatNU__), new ExBase(hatNU__),
				new ExBase(nohatNUT_), new ExBase(hatNUT_),
				new ExBase(nohatNUTS), new ExBase(hatNUTS)
			};
		ExBase[] data2 = new ExBase[]{
			new ExBase(nohatN___), new ExBase(hatN___),
			new ExBase(nohatN_TS), new ExBase(hatN_TS),
			new ExBase(nohatNU_S), new ExBase(hatNU_S)
		};
		ExBase[] data3 = new ExBase[]{
				new ExBase(nohatN___), new ExBase(hatN___)
		};
		ExBase[] data4 = new ExBase[]{
				new ExBase(nohatNU__), new ExBase(hatNU__),
				new ExBase(nohatNUT_), new ExBase(hatNUT_),
				new ExBase(nohatNUTS), new ExBase(hatNUTS)
			};
		ExBase[] data5 = new ExBase[]{
			new ExBase(nohatN_TS), new ExBase(hatN_TS),
			new ExBase(nohatNU_S), new ExBase(hatNU_S)
		};
		
		ExBaseSet set1 = new ExBaseSet(Arrays.asList(data1));
		ExBaseSet set2 = new ExBaseSet(Arrays.asList(data2));
		ExBaseSet set4 = new ExBaseSet(Arrays.asList(data4));

		ExBaseSet ret1 = set1.subtraction(set2);
		assertTrue(isEqual(set1, data1));
		assertTrue(isEqual(set2, data2));
		assertTrue(isEqual(ret1, data4));
		
		ExBaseSet ret2 = set2.subtraction(set1);
		assertTrue(isEqual(set1, data1));
		assertTrue(isEqual(set2, data2));
		assertTrue(isEqual(ret2, data5));
		
		ExBaseSet ret3 = set1.subtraction(set4);
		assertTrue(isEqual(set1, data1));
		assertTrue(isEqual(set4, data4));
		assertTrue(isEqual(ret3, data3));
		
		ExBaseSet ret4 = set4.subtraction(set1);
		assertTrue(isEqual(set1, data1));
		assertTrue(isEqual(set4, data4));
		assertTrue(ret4.isEmpty());
	}

	/**
	 * {@link exalge2.ExBaseSet#union(exalge2.ExBaseSet)} のためのテスト・メソッド。
	 */
	public void testUnion() {
		ExBase[] data1 = new ExBase[]{
				new ExBase(nohatN___), new ExBase(hatN___),
				new ExBase(nohatNU__), new ExBase(hatNU__),
				new ExBase(nohatNUT_), new ExBase(hatNUT_),
				new ExBase(nohatNUTS), new ExBase(hatNUTS)
			};
		ExBase[] data2 = new ExBase[]{
			new ExBase(nohatN___), new ExBase(hatN___),
			new ExBase(nohatN_TS), new ExBase(hatN_TS),
			new ExBase(nohatNU_S), new ExBase(hatNU_S)
		};
		
		ExBaseSet set1 = new ExBaseSet(Arrays.asList(data1));
		ExBaseSet set2 = new ExBaseSet(Arrays.asList(data2));
		
		assertTrue(set1.containsAll(Arrays.asList(data1)));
		assertEquals(set1.size(), data1.length);
		assertTrue(set2.containsAll(Arrays.asList(data2)));
		assertEquals(set2.size(), data2.length);
		
		ExBaseSet ret1 = set1.union(set2);
		ExBaseSet ret2 = set2.union(set1);
		assertEquals(ret1, ret2);
		assertTrue(ret1.containsAll(Arrays.asList(data1)));
		assertTrue(ret1.containsAll(Arrays.asList(data2)));
		assertEquals(ret1.size(), (data1.length + data2.length - 2));
	}

	/**
	 * {@link exalge2.ExBaseSet#intersection(exalge2.ExBaseSet)} のためのテスト・メソッド。
	 */
	public void testIntersection() {
		ExBase[] data1 = new ExBase[]{
				new ExBase(nohatN___), new ExBase(hatN___),
				new ExBase(nohatNU__), new ExBase(hatNU__),
				new ExBase(nohatNUT_), new ExBase(hatNUT_),
				new ExBase(nohatNUTS), new ExBase(hatNUTS)
			};
		ExBase[] data2 = new ExBase[]{
			new ExBase(nohatN___), new ExBase(hatN___),
			new ExBase(nohatN_TS), new ExBase(hatN_TS),
			new ExBase(nohatNU_S), new ExBase(hatNU_S)
		};
		ExBase[] data3 = new ExBase[]{
				new ExBase(nohatN___), new ExBase(hatN___)
		};
		
		ExBaseSet set1 = new ExBaseSet(Arrays.asList(data1));
		ExBaseSet set2 = new ExBaseSet(Arrays.asList(data2));
		
		assertTrue(set1.containsAll(Arrays.asList(data1)));
		assertEquals(set1.size(), data1.length);
		assertTrue(set2.containsAll(Arrays.asList(data2)));
		assertEquals(set2.size(), data2.length);
		
		ExBaseSet ret1 = set1.intersection(set2);
		ExBaseSet ret2 = set2.intersection(set1);
		assertEquals(ret1, ret2);
		assertFalse(ret1.containsAll(Arrays.asList(data1)));
		assertFalse(ret1.containsAll(Arrays.asList(data2)));
		assertTrue(ret1.containsAll(Arrays.asList(data3)));
		assertEquals(ret1.size(), data3.length);
	}

	/**
	 * {@link exalge2.ExBaseSet#difference(exalge2.ExBaseSet)} のためのテスト・メソッド。
	 */
	public void testDifference() {
		ExBase[] data1 = new ExBase[]{
				new ExBase(nohatN___), new ExBase(hatN___),
				new ExBase(nohatNU__), new ExBase(hatNU__),
				new ExBase(nohatNUT_), new ExBase(hatNUT_),
				new ExBase(nohatNUTS), new ExBase(hatNUTS)
			};
		ExBase[] data2 = new ExBase[]{
			new ExBase(nohatN___), new ExBase(hatN___),
			new ExBase(nohatN_TS), new ExBase(hatN_TS),
			new ExBase(nohatNU_S), new ExBase(hatNU_S)
		};
		ExBase[] data3 = new ExBase[]{
				new ExBase(nohatN___), new ExBase(hatN___)
		};
		ExBase[] data4 = new ExBase[]{
				new ExBase(nohatNU__), new ExBase(hatNU__),
				new ExBase(nohatNUT_), new ExBase(hatNUT_),
				new ExBase(nohatNUTS), new ExBase(hatNUTS)
			};
		ExBase[] data5 = new ExBase[]{
			new ExBase(nohatN_TS), new ExBase(hatN_TS),
			new ExBase(nohatNU_S), new ExBase(hatNU_S)
		};
		
		ExBaseSet set1 = new ExBaseSet(Arrays.asList(data1));
		ExBaseSet set2 = new ExBaseSet(Arrays.asList(data2));
		
		assertTrue(set1.containsAll(Arrays.asList(data1)));
		assertEquals(set1.size(), data1.length);
		assertTrue(set2.containsAll(Arrays.asList(data2)));
		assertEquals(set2.size(), data2.length);
		
		ExBaseSet ret1 = set1.difference(set2);
		ExBaseSet ret2 = set2.difference(set1);
		assertFalse(ret1.equals(ret2));
		
		assertFalse(ret1.containsAll(Arrays.asList(data1)));
		assertFalse(ret1.containsAll(Arrays.asList(data2)));
		assertFalse(ret1.containsAll(Arrays.asList(data3)));
		assertTrue(ret1.containsAll(Arrays.asList(data4)));
		assertFalse(ret1.containsAll(Arrays.asList(data5)));
		assertEquals(ret1.size(), data4.length);
		
		assertFalse(ret2.containsAll(Arrays.asList(data1)));
		assertFalse(ret2.containsAll(Arrays.asList(data2)));
		assertFalse(ret2.containsAll(Arrays.asList(data3)));
		assertFalse(ret2.containsAll(Arrays.asList(data4)));
		assertTrue(ret2.containsAll(Arrays.asList(data5)));
		assertEquals(ret2.size(), data5.length);
	}

	/**
	 * Test {@link exalge2.ExBaseSet#getMatchedBases(ExBase)}
	 * @since 0.94
	 */
	public void testGetMatchedBasesExBase() {
		ExBaseSet set1 = new ExBaseSet(Arrays.asList(baseAllData));
		
		// projection-1
		ExBase pat1n = new ExBase("メロン-NO_HAT-*-Y*Q*-*");
		ExBase pat1h = new ExBase("メロン-HAT-*-Y*Q*-*");
		ExBase[] ans1w = new ExBase[]{
				new ExBase(nohatMelonYenQ1),
				new ExBase(nohatMelonYenQ2),
				new ExBase(nohatMelonYenQ3),
				new ExBase(nohatMelonYenQ4),
				new ExBase(hatMelonYenQ1),
				new ExBase(hatMelonYenQ2),
				new ExBase(hatMelonYenQ3),
				new ExBase(hatMelonYenQ4),
		};
		ExBaseSet ret1n = set1.getMatchedBases(pat1n);
		assertTrue(isEqual(set1, baseAllData));
		assertTrue(isEqual(ret1n, ans1w));
		ExBaseSet ret1h = set1.getMatchedBases(pat1h);
		assertTrue(isEqual(set1, baseAllData));
		assertTrue(isEqual(ret1h, ans1w));
		
		// projection-2
		ExBase pat2n = new ExBase("*-NO_HAT-円-*Q3*-*");
		ExBase pat2h = new ExBase("*-HAT-円-*Q3*-*");
		ExBase[] ans2w = new ExBase[]{
				new ExBase(nohatAppleYenQ3),
				new ExBase(hatAppleYenQ3),
				new ExBase(nohatOrangeYenQ3),
				new ExBase(hatOrangeYenQ3),
				new ExBase(nohatBananaYenQ3),
				new ExBase(hatBananaYenQ3),
				new ExBase(nohatMelonYenQ3),
				new ExBase(hatMelonYenQ3),
				new ExBase(nohatCashYenQ3),
				new ExBase(hatCashYenQ3),
		};
		ExBaseSet ret2n = set1.getMatchedBases(pat2n);
		assertTrue(isEqual(set1, baseAllData));
		assertTrue(isEqual(ret2n, ans2w));
		ExBaseSet ret2h = set1.getMatchedBases(pat2h);
		assertTrue(isEqual(set1, baseAllData));
		assertTrue(isEqual(ret2h, ans2w));
		
		// projection-3
		ExBase pat3n = new ExBase("*-NO_HAT-*-*2008*-*");
		ExBase pat3h = new ExBase("*-HAT-*-*2008*-*");
		ExBaseSet ret3n = set1.getMatchedBases(pat3n);
		assertTrue(isEqual(set1, baseAllData));
		assertTrue(ret3n.isEmpty());
		ExBaseSet ret3h = set1.getMatchedBases(pat3h);
		assertTrue(isEqual(set1, baseAllData));
		assertTrue(ret3h.isEmpty());
	}

	/**
	 * Test {@link exalge2.ExBaseSet#getMatchedBases(ExBasePattern)}
	 * @since 0.94
	 */
	public void testGetMatchedBasesExBasePattern() {
		ExBaseSet set1 = new ExBaseSet(Arrays.asList(baseAllData));
		
		// projection-1
		ExBasePattern pat1n = new ExBasePattern("メロン-NO_HAT-*-Y*Q*-*");
		ExBasePattern pat1h = new ExBasePattern("メロン-HAT-*-Y*Q*-*");
		ExBasePattern pat1w = new ExBasePattern("メロン-*-*-Y*Q*-*");
		ExBase[] ans1n = new ExBase[]{
				new ExBase(nohatMelonYenQ1),
				new ExBase(nohatMelonYenQ2),
				new ExBase(nohatMelonYenQ3),
				new ExBase(nohatMelonYenQ4),
		};
		ExBase[] ans1h = new ExBase[]{
				new ExBase(hatMelonYenQ1),
				new ExBase(hatMelonYenQ2),
				new ExBase(hatMelonYenQ3),
				new ExBase(hatMelonYenQ4),
		};
		ExBase[] ans1w = new ExBase[]{
				new ExBase(nohatMelonYenQ1),
				new ExBase(nohatMelonYenQ2),
				new ExBase(nohatMelonYenQ3),
				new ExBase(nohatMelonYenQ4),
				new ExBase(hatMelonYenQ1),
				new ExBase(hatMelonYenQ2),
				new ExBase(hatMelonYenQ3),
				new ExBase(hatMelonYenQ4),
		};
		ExBaseSet ret1n = set1.getMatchedBases(pat1n);
		assertTrue(isEqual(set1, baseAllData));
		assertTrue(isEqual(ret1n, ans1n));
		ExBaseSet ret1h = set1.getMatchedBases(pat1h);
		assertTrue(isEqual(set1, baseAllData));
		assertTrue(isEqual(ret1h, ans1h));
		ExBaseSet ret1w = set1.getMatchedBases(pat1w);
		assertTrue(isEqual(set1, baseAllData));
		assertTrue(isEqual(ret1w, ans1w));
		
		// projection-2
		ExBasePattern pat2n = new ExBasePattern("*-NO_HAT-円-*Q3*-*");
		ExBasePattern pat2h = new ExBasePattern("*-HAT-円-*Q3*-*");
		ExBasePattern pat2w = new ExBasePattern("*-*-円-*Q3*-*");
		ExBase[] ans2n = new ExBase[]{
				new ExBase(nohatAppleYenQ3),
				new ExBase(nohatOrangeYenQ3),
				new ExBase(nohatBananaYenQ3),
				new ExBase(nohatMelonYenQ3),
				new ExBase(nohatCashYenQ3),
		};
		ExBase[] ans2h = new ExBase[]{
				new ExBase(hatAppleYenQ3),
				new ExBase(hatOrangeYenQ3),
				new ExBase(hatBananaYenQ3),
				new ExBase(hatMelonYenQ3),
				new ExBase(hatCashYenQ3),
		};
		ExBase[] ans2w = new ExBase[]{
				new ExBase(nohatAppleYenQ3),
				new ExBase(hatAppleYenQ3),
				new ExBase(nohatOrangeYenQ3),
				new ExBase(hatOrangeYenQ3),
				new ExBase(nohatBananaYenQ3),
				new ExBase(hatBananaYenQ3),
				new ExBase(nohatMelonYenQ3),
				new ExBase(hatMelonYenQ3),
				new ExBase(nohatCashYenQ3),
				new ExBase(hatCashYenQ3),
		};
		ExBaseSet ret2n = set1.getMatchedBases(pat2n);
		assertTrue(isEqual(set1, baseAllData));
		assertTrue(isEqual(ret2n, ans2n));
		ExBaseSet ret2h = set1.getMatchedBases(pat2h);
		assertTrue(isEqual(set1, baseAllData));
		assertTrue(isEqual(ret2h, ans2h));
		ExBaseSet ret2w = set1.getMatchedBases(pat2w);
		assertTrue(isEqual(set1, baseAllData));
		assertTrue(isEqual(ret2w, ans2w));
		
		// projection-3
		ExBasePattern pat3n = new ExBasePattern("*-NO_HAT-*-*2008*-*");
		ExBasePattern pat3h = new ExBasePattern("*-HAT-*-*2008*-*");
		ExBasePattern pat3w = new ExBasePattern("*-*-*-*2008*-*");
		ExBaseSet ret3n = set1.getMatchedBases(pat3n);
		assertTrue(isEqual(set1, baseAllData));
		assertTrue(ret3n.isEmpty());
		ExBaseSet ret3h = set1.getMatchedBases(pat3h);
		assertTrue(isEqual(set1, baseAllData));
		assertTrue(ret3h.isEmpty());
		ExBaseSet ret3w = set1.getMatchedBases(pat3w);
		assertTrue(isEqual(set1, baseAllData));
		assertTrue(ret3w.isEmpty());
	}

	/**
	 * Test {@link exalge2.ExBaseSet#getMatchedBases(ExBaseSet)}
	 * @since 0.94
	 */
	public void testGetMatchedBasesExBaseSet() {
		ExBaseSet set1 = new ExBaseSet(Arrays.asList(baseAllData));

		// projection-1
		ExBaseSet pat1n = new ExBaseSet(Arrays.asList(new ExBase[]{
				new ExBase("りんご-NO_HAT-*-Y*Q*-*"),
				new ExBase("メロン-NO_HAT-*-Y*Q*-*"),
		}));
		ExBaseSet pat1h = new ExBaseSet(Arrays.asList(new ExBase[]{
				new ExBase("りんご-HAT-*-Y*Q*-*"),
				new ExBase("メロン-HAT-*-Y*Q*-*"),
		}));
		ExBase[] ans1w = new ExBase[]{
				new ExBase(nohatAppleYenQ1),
				new ExBase(nohatAppleYenQ2),
				new ExBase(nohatAppleYenQ3),
				new ExBase(nohatAppleYenQ4),
				new ExBase(hatAppleYenQ1),
				new ExBase(hatAppleYenQ2),
				new ExBase(hatAppleYenQ3),
				new ExBase(hatAppleYenQ4),
				new ExBase(nohatMelonYenQ1),
				new ExBase(nohatMelonYenQ2),
				new ExBase(nohatMelonYenQ3),
				new ExBase(nohatMelonYenQ4),
				new ExBase(hatMelonYenQ1),
				new ExBase(hatMelonYenQ2),
				new ExBase(hatMelonYenQ3),
				new ExBase(hatMelonYenQ4),
		};
		ExBaseSet ret1n = set1.getMatchedBases(pat1n);
		assertTrue(isEqual(set1, baseAllData));
		assertTrue(isEqual(ret1n, ans1w));
		ExBaseSet ret1h = set1.getMatchedBases(pat1h);
		assertTrue(isEqual(set1, baseAllData));
		assertTrue(isEqual(ret1h, ans1w));
		
		// projection-2
		ExBaseSet pat2n = new ExBaseSet(Arrays.asList(new ExBase[]{
				new ExBase("*-NO_HAT-円-Y*Q2*-*"),
				new ExBase("*-NO_HAT-円-Y*Q3*-*"),
		}));
		ExBaseSet pat2h = new ExBaseSet(Arrays.asList(new ExBase[]{
				new ExBase("*-HAT-円-Y*Q2*-*"),
				new ExBase("*-HAT-円-Y*Q3*-*"),
		}));
		ExBase[] ans2w = new ExBase[]{
				new ExBase(nohatAppleYenQ2),
				new ExBase(nohatAppleYenQ3),
				new ExBase(hatAppleYenQ2),
				new ExBase(hatAppleYenQ3),
				new ExBase(nohatOrangeYenQ2),
				new ExBase(nohatOrangeYenQ3),
				new ExBase(hatOrangeYenQ2),
				new ExBase(hatOrangeYenQ3),
				new ExBase(nohatBananaYenQ2),
				new ExBase(nohatBananaYenQ3),
				new ExBase(hatBananaYenQ2),
				new ExBase(hatBananaYenQ3),
				new ExBase(nohatMelonYenQ2),
				new ExBase(nohatMelonYenQ3),
				new ExBase(hatMelonYenQ2),
				new ExBase(hatMelonYenQ3),
				new ExBase(nohatCashYenQ2),
				new ExBase(nohatCashYenQ3),
				new ExBase(hatCashYenQ2),
				new ExBase(hatCashYenQ3),
		};
		ExBaseSet ret2n = set1.getMatchedBases(pat2n);
		assertTrue(isEqual(set1, baseAllData));
		assertTrue(isEqual(ret2n, ans2w));
		ExBaseSet ret2h = set1.getMatchedBases(pat2h);
		assertTrue(isEqual(set1, baseAllData));
		assertTrue(isEqual(ret2h, ans2w));
		
		// projection-3
		ExBaseSet pat3n = new ExBaseSet(Arrays.asList(new ExBase[]{
				new ExBase("*-NO_HAT-円-*2008*-*"),
				new ExBase("*-NO_HAT-＄-*-*"),
		}));
		ExBaseSet pat3h = new ExBaseSet(Arrays.asList(new ExBase[]{
				new ExBase("*-HAT-円-*2008*-*"),
				new ExBase("*-HAT-＄-*-*"),
		}));
		ExBaseSet ret3n = set1.getMatchedBases(pat3n);
		assertTrue(isEqual(set1, baseAllData));
		assertTrue(ret3n.isEmpty());
		ExBaseSet ret3h = set1.getMatchedBases(pat3h);
		assertTrue(isEqual(set1, baseAllData));
		assertTrue(ret3h.isEmpty());
		
		// projection-4
		ExBasePatternSet pat4 = new ExBasePatternSet();
		ExBaseSet ret4 = set1.getMatchedBases(pat4);
		assertTrue(isEqual(set1, baseAllData));
		assertTrue(ret4.isEmpty());
	}

	/**
	 * Test {@link exalge2.ExBaseSet#getMatchedBases(ExBasePatternSet)}
	 * @since 0.94
	 */
	public void testGetMatchedBasesExBasePatternSet() {
		ExBaseSet set1 = new ExBaseSet(Arrays.asList(baseAllData));

		// projection-1
		ExBasePatternSet pat1n = new ExBasePatternSet(Arrays.asList(new ExBasePattern[]{
				new ExBasePattern("りんご-NO_HAT-*-Y*Q*-*"),
				new ExBasePattern("メロン-NO_HAT-*-Y*Q*-*"),
		}));
		ExBasePatternSet pat1h = new ExBasePatternSet(Arrays.asList(new ExBasePattern[]{
				new ExBasePattern("りんご-HAT-*-Y*Q*-*"),
				new ExBasePattern("メロン-HAT-*-Y*Q*-*"),
		}));
		ExBasePatternSet pat1w = new ExBasePatternSet(Arrays.asList(new ExBasePattern[]{
				new ExBasePattern("りんご-*-*-Y*Q*-*"),
				new ExBasePattern("メロン-*-*-Y*Q*-*"),
		}));
		ExBase[] ans1n = new ExBase[]{
				new ExBase(nohatAppleYenQ1),
				new ExBase(nohatAppleYenQ2),
				new ExBase(nohatAppleYenQ3),
				new ExBase(nohatAppleYenQ4),
				new ExBase(nohatMelonYenQ1),
				new ExBase(nohatMelonYenQ2),
				new ExBase(nohatMelonYenQ3),
				new ExBase(nohatMelonYenQ4),
		};
		ExBase[] ans1h = new ExBase[]{
				new ExBase(hatAppleYenQ1),
				new ExBase(hatAppleYenQ2),
				new ExBase(hatAppleYenQ3),
				new ExBase(hatAppleYenQ4),
				new ExBase(hatMelonYenQ1),
				new ExBase(hatMelonYenQ2),
				new ExBase(hatMelonYenQ3),
				new ExBase(hatMelonYenQ4),
		};
		ExBase[] ans1w = new ExBase[]{
				new ExBase(nohatAppleYenQ1),
				new ExBase(nohatAppleYenQ2),
				new ExBase(nohatAppleYenQ3),
				new ExBase(nohatAppleYenQ4),
				new ExBase(hatAppleYenQ1),
				new ExBase(hatAppleYenQ2),
				new ExBase(hatAppleYenQ3),
				new ExBase(hatAppleYenQ4),
				new ExBase(nohatMelonYenQ1),
				new ExBase(nohatMelonYenQ2),
				new ExBase(nohatMelonYenQ3),
				new ExBase(nohatMelonYenQ4),
				new ExBase(hatMelonYenQ1),
				new ExBase(hatMelonYenQ2),
				new ExBase(hatMelonYenQ3),
				new ExBase(hatMelonYenQ4),
		};
		ExBaseSet ret1n = set1.getMatchedBases(pat1n);
		assertTrue(isEqual(set1, baseAllData));
		assertTrue(isEqual(ret1n, ans1n));
		ExBaseSet ret1h = set1.getMatchedBases(pat1h);
		assertTrue(isEqual(set1, baseAllData));
		assertTrue(isEqual(ret1h, ans1h));
		ExBaseSet ret1w = set1.getMatchedBases(pat1w);
		assertTrue(isEqual(set1, baseAllData));
		assertTrue(isEqual(ret1w, ans1w));
		
		// projection-2
		ExBasePatternSet pat2n = new ExBasePatternSet(Arrays.asList(new ExBasePattern[]{
				new ExBasePattern("*-NO_HAT-円-Y*Q2*-*"),
				new ExBasePattern("*-NO_HAT-円-Y*Q3*-*"),
		}));
		ExBasePatternSet pat2h = new ExBasePatternSet(Arrays.asList(new ExBasePattern[]{
				new ExBasePattern("*-HAT-円-Y*Q2*-*"),
				new ExBasePattern("*-HAT-円-Y*Q3*-*"),
		}));
		ExBasePatternSet pat2w = new ExBasePatternSet(Arrays.asList(new ExBasePattern[]{
				new ExBasePattern("*-*-円-Y*Q2*-*"),
				new ExBasePattern("*-*-円-Y*Q3*-*"),
		}));
		ExBase[] ans2n = new ExBase[]{
				new ExBase(nohatAppleYenQ2),
				new ExBase(nohatAppleYenQ3),
				new ExBase(nohatOrangeYenQ2),
				new ExBase(nohatOrangeYenQ3),
				new ExBase(nohatBananaYenQ2),
				new ExBase(nohatBananaYenQ3),
				new ExBase(nohatMelonYenQ2),
				new ExBase(nohatMelonYenQ3),
				new ExBase(nohatCashYenQ2),
				new ExBase(nohatCashYenQ3),
		};
		ExBase[] ans2h = new ExBase[]{
				new ExBase(hatAppleYenQ2),
				new ExBase(hatAppleYenQ3),
				new ExBase(hatOrangeYenQ2),
				new ExBase(hatOrangeYenQ3),
				new ExBase(hatBananaYenQ2),
				new ExBase(hatBananaYenQ3),
				new ExBase(hatMelonYenQ2),
				new ExBase(hatMelonYenQ3),
				new ExBase(hatCashYenQ2),
				new ExBase(hatCashYenQ3),
		};
		ExBase[] ans2w = new ExBase[]{
				new ExBase(nohatAppleYenQ2),
				new ExBase(nohatAppleYenQ3),
				new ExBase(hatAppleYenQ2),
				new ExBase(hatAppleYenQ3),
				new ExBase(nohatOrangeYenQ2),
				new ExBase(nohatOrangeYenQ3),
				new ExBase(hatOrangeYenQ2),
				new ExBase(hatOrangeYenQ3),
				new ExBase(nohatBananaYenQ2),
				new ExBase(nohatBananaYenQ3),
				new ExBase(hatBananaYenQ2),
				new ExBase(hatBananaYenQ3),
				new ExBase(nohatMelonYenQ2),
				new ExBase(nohatMelonYenQ3),
				new ExBase(hatMelonYenQ2),
				new ExBase(hatMelonYenQ3),
				new ExBase(nohatCashYenQ2),
				new ExBase(nohatCashYenQ3),
				new ExBase(hatCashYenQ2),
				new ExBase(hatCashYenQ3),
		};
		ExBaseSet ret2n = set1.getMatchedBases(pat2n);
		assertTrue(isEqual(set1, baseAllData));
		assertTrue(isEqual(ret2n, ans2n));
		ExBaseSet ret2h = set1.getMatchedBases(pat2h);
		assertTrue(isEqual(set1, baseAllData));
		assertTrue(isEqual(ret2h, ans2h));
		ExBaseSet ret2w = set1.getMatchedBases(pat2w);
		assertTrue(isEqual(set1, baseAllData));
		assertTrue(isEqual(ret2w, ans2w));
		
		// projection-3
		ExBasePatternSet pat3n = new ExBasePatternSet(Arrays.asList(new ExBasePattern[]{
				new ExBasePattern("*-NO_HAT-円-*2008*-*"),
				new ExBasePattern("*-NO_HAT-＄-*-*"),
		}));
		ExBasePatternSet pat3h = new ExBasePatternSet(Arrays.asList(new ExBasePattern[]{
				new ExBasePattern("*-HAT-円-*2008*-*"),
				new ExBasePattern("*-HAT-＄-*-*"),
		}));
		ExBasePatternSet pat3w = new ExBasePatternSet(Arrays.asList(new ExBasePattern[]{
				new ExBasePattern("*-*-円-*2008*-*"),
				new ExBasePattern("*-*-＄-*-*"),
		}));
		ExBaseSet ret3n = set1.getMatchedBases(pat3n);
		assertTrue(isEqual(set1, baseAllData));
		assertTrue(ret3n.isEmpty());
		ExBaseSet ret3h = set1.getMatchedBases(pat3h);
		assertTrue(isEqual(set1, baseAllData));
		assertTrue(ret3h.isEmpty());
		ExBaseSet ret3w = set1.getMatchedBases(pat3w);
		assertTrue(isEqual(set1, baseAllData));
		assertTrue(ret3w.isEmpty());
		
		// projection-4
		ExBasePatternSet pat4 = new ExBasePatternSet();
		ExBaseSet ret4 = set1.getMatchedBases(pat4);
		assertTrue(isEqual(set1, baseAllData));
		assertTrue(ret4.isEmpty());
	}

	/**
	 * {@link exalge2.ExBaseSet#add(exalge2.ExBase)} のためのテスト・メソッド。
	 */
	public void testAddExBase() {
		ExBase[] data = new ExBase[]{
			new ExBase(nohatN___), new ExBase(hatN___),
			new ExBase(nohatNU__), new ExBase(hatNU__),
			new ExBase(nohatNUT_), new ExBase(hatNUT_),
			new ExBase(nohatNUTS), new ExBase(hatNUTS)
		};
		
		ExBaseSet baseset = new ExBaseSet();
		assertTrue(baseset.isEmpty());
		
		for (int i = 0; i < data.length; i++) {
			assertTrue(baseset.add(data[i]));
		}
		
		assertTrue(baseset.containsAll(Arrays.asList(data)));
	}

	/**
	 * {@link exalge2.ExBaseSet#addAll(java.util.Collection)} のためのテスト・メソッド。
	 */
	public void testAddAllCollectionOfQextendsExBase() {
		ExBase[] data = new ExBase[]{
				new ExBase(nohatN___), new ExBase(hatN___),
				new ExBase(nohatNU__), new ExBase(hatNU__),
				new ExBase(nohatNUT_), new ExBase(hatNUT_),
				new ExBase(nohatNUTS), new ExBase(hatNUTS)
			};

		ExBaseSet baseset = new ExBaseSet();
		assertTrue(baseset.isEmpty());
		
		assertTrue(baseset.addAll(Arrays.asList(data)));
		assertTrue(baseset.containsAll(Arrays.asList(data)));
	}

	/**
	 * Test {@link exalge2.ExBaseSet#clone()}
	 */
	public void testClone() {
		ExBase[] data = new ExBase[]{
			new ExBase(nohatN___), new ExBase(hatN___),
			new ExBase(nohatNU__), new ExBase(hatNU__),
			new ExBase(nohatNUT_), new ExBase(hatNUT_),
			new ExBase(nohatNUTS), new ExBase(hatNUTS)
		};
		ExBaseSet set1 = new ExBaseSet(Arrays.asList(data));
		ExBaseSet set2 = (ExBaseSet)set1.clone();
		assertEquals(set1, set2);
		
		set2.remove(new ExBasePattern(hatNU__));
		set2.remove(new ExBasePattern(nohatNUTS));
		assertFalse(set1.equals(set2));

		ExBase[] data2 = new ExBase[]{
			new ExBase(nohatN_TS), new ExBase(hatN_TS),
			new ExBase(nohatNU_S), new ExBase(hatNU_S),
		};
		ExBaseSet set3 = (ExBaseSet)set1.clone();
		set3.addAll(Arrays.asList(data2));
		assertFalse(set1.equals(set3));
	}

	/**
	 * test {@link exalge2.ExBaseSet#setHat()}
	 * @since 0.960
	 */
	public void testSetHat() {
		ExBaseSet nBases = new ExBaseSet(Arrays.asList(baseNoHatAllData));
		ExBaseSet hBases = new ExBaseSet(Arrays.asList(baseHatAllData));
		
		ExBaseSet test1 = new ExBaseSet(Arrays.asList(baseNoHatAllData));
		ExBaseSet ret1 = test1.setHat();
		assertEquals(nBases, test1);
		assertEquals(hBases, ret1);
		
		ExBaseSet test2 = new ExBaseSet(Arrays.asList(baseHatAllData));
		ExBaseSet ret2 = test2.setHat();
		assertEquals(hBases, test2);
		assertEquals(hBases, ret2);
		
		ExBaseSet test3 = new ExBaseSet(Arrays.asList(baseAllData));
		ExBaseSet ret3 = test3.setHat();
		assertTrue(isEqual(test3, baseAllData));
		assertEquals(hBases, ret3);
	}

	/**
	 * test {@link exalge2.ExBaseSet#removeHat()}
	 * @since 0.960
	 */
	public void testRemoveHat() {
		ExBaseSet nBases = new ExBaseSet(Arrays.asList(baseNoHatAllData));
		ExBaseSet hBases = new ExBaseSet(Arrays.asList(baseHatAllData));
		
		ExBaseSet test1 = new ExBaseSet(Arrays.asList(baseNoHatAllData));
		ExBaseSet ret1 = test1.removeHat();
		assertEquals(nBases, test1);
		assertEquals(nBases, ret1);
		
		ExBaseSet test2 = new ExBaseSet(Arrays.asList(baseHatAllData));
		ExBaseSet ret2 = test2.removeHat();
		assertEquals(hBases, test2);
		assertEquals(nBases, ret2);
		
		ExBaseSet test3 = new ExBaseSet(Arrays.asList(baseAllData));
		ExBaseSet ret3 = test3.removeHat();
		assertTrue(isEqual(test3, baseAllData));
		assertEquals(nBases, ret3);
	}

	/**
	 * {@link exalge2.ExBaseSet#getNoHatBases()} のためのテスト・メソッド。
	 * @since 0.970
	 */
	public void testGetNoHatBases() {
		ExBaseSet testBases = ExalgeTest.makeBaseSet(
				new ExBase(hatAppleYen),
				new ExBase(hatOrangeYen),
				new ExBase(hatBananaYen),
				new ExBase(nohatCashYen),
				new ExBase(nohatMelonYen),
				new ExBase(hatMelonYen)
				);
		ExBaseSet retBases;
		
		// Empty
		ExBaseSet basesEmpty = new ExBaseSet();
		retBases = basesEmpty.getNoHatBases();
		assertTrue(retBases.isEmpty());
		
		// alge
		ExBaseSet ansBases = ExalgeTest.makeBaseSet(new ExBase(nohatCashYen), new ExBase(nohatMelonYen));
		retBases = testBases.getNoHatBases();
		assertEquals(retBases, ansBases);
	}

	/**
	 * {@link exalge2.ExBaseSet#getHatBases()} のためのテスト・メソッド。
	 * @since 0.970
	 */
	public void testGetHatBases() {
		ExBaseSet testBases = ExalgeTest.makeBaseSet(
				new ExBase(hatAppleYen),
				new ExBase(hatOrangeYen),
				new ExBase(hatBananaYen),
				new ExBase(nohatCashYen),
				new ExBase(nohatMelonYen),
				new ExBase(hatMelonYen)
				);
		ExBaseSet retBases;
		
		// Empty
		ExBaseSet basesEmpty = new ExBaseSet();
		retBases = basesEmpty.getHatBases();
		assertTrue(retBases.isEmpty());
		
		// alge
		ExBaseSet ansBases = ExalgeTest.makeBaseSet(new ExBase(hatAppleYen), new ExBase(hatOrangeYen), new ExBase(hatBananaYen), new ExBase(hatMelonYen));
		retBases = testBases.getHatBases();
		assertEquals(retBases, ansBases);
	}

}
