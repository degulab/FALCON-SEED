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
 *  Copyright 2007-2014  SOARS Project.
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
import java.util.Iterator;

import junit.framework.TestCase;

public class ExAlgeSetTest extends TestCase {
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
	
	static protected final Object[] algeAppleData = new Object[]{
		new ExBase(hatAppleYenQ1)  , new BigDecimal(10),
		new ExBase(nohatCashYenQ1) , new BigDecimal(10),
		new ExBase(hatAppleYenQ2)  , new BigDecimal(20),
		new ExBase(nohatCashYenQ2) , new BigDecimal(20),
		new ExBase(nohatAppleYenQ3), new BigDecimal(30),
		new ExBase(hatCashYenQ3)   , new BigDecimal(30),
		new ExBase(nohatAppleYenQ4), new BigDecimal(40),
		new ExBase(hatCashYenQ4)   , new BigDecimal(40),
	};
	
	static protected final Object[] algeOrangeData = new Object[]{
		new ExBase(hatOrangeYenQ1)  , new BigDecimal(11),
		new ExBase(nohatCashYenQ1)  , new BigDecimal(11),
		new ExBase(hatOrangeYenQ2)  , new BigDecimal(22),
		new ExBase(nohatCashYenQ2)  , new BigDecimal(22),
		new ExBase(nohatOrangeYenQ3), new BigDecimal(33),
		new ExBase(hatCashYenQ3)    , new BigDecimal(33),
		new ExBase(nohatOrangeYenQ4), new BigDecimal(44),
		new ExBase(hatCashYenQ4)    , new BigDecimal(44),
	};
	
	static protected final Object[] algeBananaData = new Object[]{
		new ExBase(nohatBananaYenQ1), new BigDecimal(21),
		new ExBase(hatCashYenQ1)    , new BigDecimal(21),
		new ExBase(hatBananaYenQ2)  , new BigDecimal(42),
		new ExBase(nohatCashYenQ2)  , new BigDecimal(42),
		new ExBase(nohatBananaYenQ3), new BigDecimal(63),
		new ExBase(hatCashYenQ3)    , new BigDecimal(63),
		new ExBase(hatBananaYenQ4)  , new BigDecimal(84),
		new ExBase(nohatCashYenQ4)  , new BigDecimal(84),
	};
	
	static protected final Object[] algeMelonData = new Object[]{
		new ExBase(nohatMelonYenQ1), new BigDecimal(120),
		new ExBase(hatCashYenQ1)   , new BigDecimal(120),
		new ExBase(hatMelonYenQ2)  , new BigDecimal(0),
		new ExBase(nohatCashYenQ2) , new BigDecimal(0),
		new ExBase(nohatMelonYenQ3), new BigDecimal(0),
		new ExBase(hatCashYenQ3)   , new BigDecimal(0),
		new ExBase(hatMelonYenQ4)  , new BigDecimal(240),
		new ExBase(nohatCashYenQ4) , new BigDecimal(240),
	};
	
	static protected final Object[] algeAllData = new Object[]{
		new ExBase(hatAppleYenQ1)   , new BigDecimal(10),
		new ExBase(nohatCashYenQ1)  , new BigDecimal(10+11),
		new ExBase(hatAppleYenQ2)   , new BigDecimal(20),
		new ExBase(nohatCashYenQ2)  , new BigDecimal(20+22+42+0),
		new ExBase(nohatAppleYenQ3) , new BigDecimal(30),
		new ExBase(hatCashYenQ3)    , new BigDecimal(30+33+63+0),
		new ExBase(nohatAppleYenQ4) , new BigDecimal(40),
		new ExBase(hatCashYenQ4)    , new BigDecimal(40+44),
		new ExBase(hatOrangeYenQ1)  , new BigDecimal(11),
		new ExBase(hatOrangeYenQ2)  , new BigDecimal(22),
		new ExBase(nohatOrangeYenQ3), new BigDecimal(33),
		new ExBase(nohatOrangeYenQ4), new BigDecimal(44),
		new ExBase(nohatBananaYenQ1), new BigDecimal(21),
		new ExBase(hatCashYenQ1)    , new BigDecimal(21+120),
		new ExBase(hatBananaYenQ2)  , new BigDecimal(42),
		new ExBase(nohatBananaYenQ3), new BigDecimal(63),
		new ExBase(hatBananaYenQ4)  , new BigDecimal(84),
		new ExBase(nohatCashYenQ4)  , new BigDecimal(84+240),
		new ExBase(nohatMelonYenQ1) , new BigDecimal(120),
		new ExBase(hatMelonYenQ2)   , new BigDecimal(0),
		new ExBase(nohatMelonYenQ3) , new BigDecimal(0),
		new ExBase(hatMelonYenQ4)   , new BigDecimal(240),
	};
	
	static protected final Object[] algeAppleDataWithNull = new Object[]{
		new ExBase(hatAppleYenQ1)  , null,
		new ExBase(nohatCashYenQ1) , null,
		new ExBase(hatAppleYenQ2)  , null,
		new ExBase(nohatCashYenQ2) , null,
		new ExBase(nohatAppleYenQ3), new BigDecimal(30),
		new ExBase(hatCashYenQ3)   , new BigDecimal(30),
		new ExBase(nohatAppleYenQ4), new BigDecimal(40),
		new ExBase(hatCashYenQ4)   , new BigDecimal(40),
	};
	
	static protected final Object[] algeOrangeDataWithNull = new Object[]{
		new ExBase(hatOrangeYenQ1)  , new BigDecimal(11),
		new ExBase(nohatCashYenQ1)  , new BigDecimal(11),
		new ExBase(hatOrangeYenQ2)  , new BigDecimal(22),
		new ExBase(nohatCashYenQ2)  , new BigDecimal(22),
		new ExBase(nohatOrangeYenQ3), null,
		new ExBase(hatCashYenQ3)    , null,
		new ExBase(nohatOrangeYenQ4), null,
		new ExBase(hatCashYenQ4)    , null,
	};
	
	static protected final Object[] algeBananaDataWithNull = new Object[]{
		new ExBase(nohatBananaYenQ1), null,
		new ExBase(hatCashYenQ1)    , new BigDecimal(21),
		new ExBase(hatBananaYenQ2)  , null,
		new ExBase(nohatCashYenQ2)  , new BigDecimal(42),
		new ExBase(nohatBananaYenQ3), null,
		new ExBase(hatCashYenQ3)    , null,
		new ExBase(hatBananaYenQ4)  , null,
		new ExBase(nohatCashYenQ4)  , null,
	};
	
	static protected final Object[] algeMelonDataWithNull = new Object[]{
		new ExBase(nohatMelonYenQ1), new BigDecimal(120),
		new ExBase(hatCashYenQ1)   , new BigDecimal(120),
		new ExBase(hatMelonYenQ2)  , null,
		new ExBase(nohatCashYenQ2) , null,
		new ExBase(nohatMelonYenQ3), new BigDecimal(0),
		new ExBase(hatCashYenQ3)   , new BigDecimal(0),
		new ExBase(hatMelonYenQ4)  , null,
		new ExBase(nohatCashYenQ4) , null,
	};
	
	static protected final Object[] algeMangoDataWithNull = new Object[]{
		new ExBase(hatMangoYenQ1)  , null,
		new ExBase(nohatCashYenQ1) , null,
		new ExBase(hatMangoYenQ2)  , null,
		new ExBase(nohatCashYenQ2) , null,
		new ExBase(nohatMangoYenQ3), null,
		new ExBase(hatCashYenQ3)   , null,
		new ExBase(nohatMangoYenQ4), null,
		new ExBase(hatCashYenQ4)   , null,
	};

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * {@link exalge2.ExAlgeSet#addition(exalge2.ExAlgeSet)} のためのテスト・メソッド。
	 * @since 0.94
	 */
	public void testAddition() {
		Object[][] seqData1 = new Object[][]{
				algeAppleData,
				algeMelonData,
		};
		Object[][] seqData2 = new Object[][]{
				algeBananaData,
				algeOrangeData,
		};
		Object[][] seqData3 = new Object[][]{
				algeAppleData,
				algeMelonData,
				algeBananaData,
				algeOrangeData,
		};
		Object[][] seqData4 = new Object[][]{
				algeBananaData,
				algeOrangeData,
				algeAppleData,
				algeMelonData,
		};
		
		ExAlgeSet set1 = new ExAlgeSet();
		set1.add(new Exalge(algeAppleData));
		set1.add(new Exalge(algeMelonData));
		checkSetSequence(set1, seqData1);
		
		ExAlgeSet set2 = new ExAlgeSet();
		set2.add(new Exalge(algeBananaData));
		set2.add(new Exalge(algeOrangeData));
		checkSetSequence(set2, seqData2);
		
		ExAlgeSet ret1 = set1.addition(set2);
		checkSetSequence(set1, seqData1);
		checkSetSequence(set2, seqData2);
		checkSetSequence(ret1, seqData3);
		
		ExAlgeSet ret2 = set2.addition(set1);
		checkSetSequence(set1, seqData1);
		checkSetSequence(set2, seqData2);
		checkSetSequence(ret2, seqData4);
	}

	/**
	 * {@link exalge2.ExAlgeSet#subtraction(exalge2.ExAlgeSet)} のためのテスト・メソッド。
	 * @since 0.94
	 */
	public void testSubtraction() {
		Object[][] seqData1 = new Object[][]{
				algeAppleData,
				algeOrangeData,
				algeBananaData,
				algeMelonData,
				algeBananaData,
				algeOrangeData,
		};
		Object[][] seqData2 = new Object[][]{
				algeMelonData,
				algeOrangeData,
		};
		Object[][] seqData3 = new Object[][]{
				algeAppleData,
				algeBananaData,
				algeBananaData,
		};
		
		ExAlgeSet set1 = new ExAlgeSet();
		set1.add(new Exalge(algeAppleData));
		set1.add(new Exalge(algeOrangeData));
		set1.add(new Exalge(algeBananaData));
		set1.add(new Exalge(algeMelonData));
		set1.add(new Exalge(algeBananaData));
		set1.add(new Exalge(algeOrangeData));
		checkSetSequence(set1, seqData1);
		
		ExAlgeSet set2 = new ExAlgeSet();
		set2.add(new Exalge(algeMelonData));
		set2.add(new Exalge(algeOrangeData));
		checkSetSequence(set2, seqData2);
		
		ExAlgeSet ret1 = set1.subtraction(set2);
		checkSetSequence(set1, seqData1);
		checkSetSequence(set2, seqData2);
		checkSetSequence(ret1, seqData3);
		
		ExAlgeSet ret2 = set2.subtraction(set1);
		checkSetSequence(set1, seqData1);
		checkSetSequence(set2, seqData2);
		assertTrue(ret2.isEmpty());
	}

	/**
	 * {@link exalge2.ExAlgeSet#getBases()} のためのテスト・メソッド。
	 * @since 0.94
	 */
	public void testGetBases() {
		Object[][] seqData1 = new Object[][]{
				algeAppleData,
				algeOrangeData,
				algeBananaData,
				algeMelonData,
				{}
		};
		ExAlgeSet set = new ExAlgeSet();
		set.add(new Exalge(algeAppleData));
		set.add(new Exalge(algeOrangeData));
		set.add(new Exalge(algeBananaData));
		set.add(new Exalge(algeMelonData));
		set.add(new Exalge());
		checkSetSequence(set, seqData1);
		
		ExBaseSet bases = set.getBases();
		
		ArrayList<ExBase> basedata = new ArrayList<ExBase>();
		for (int i = 0; i < seqData1.length; i++) {
			for (int j = 0; j < seqData1[i].length; j++) {
				ExBase b = (ExBase)seqData1[i][j++];
				basedata.add(b);
			}
		}
		
		assertTrue(bases.containsAll(basedata));
		bases.removeAll(basedata);
		assertTrue(bases.isEmpty());
	}

	/**
	 * {@link exalge2.ExAlgeSet#sum()} のためのテスト・メソッド。
	 */
	public void testSum() {
		Object[][] seqData1 = new Object[][]{
				algeAppleData,
				algeOrangeData,
				algeBananaData,
				algeMelonData,
				{}
		};
		ExAlgeSet set = new ExAlgeSet();
		set.add(new Exalge(algeAppleData));
		set.add(new Exalge(algeOrangeData));
		set.add(new Exalge(algeBananaData));
		set.add(new Exalge(algeMelonData));
		set.add(new Exalge());
		checkSetSequence(set, seqData1);
		
		Exalge ret = set.sum();
		assertFalse(ret.isEmpty());
		assertEquals(algeAllData.length/2, ret.getNumElements());
		ExAlgeSetIOTest.checkElemSequence(ret, algeAllData);
	}

	/**
	 * {@link exalge2.ExAlgeSet#projection(exalge2.ExBase)} のためのテスト・メソッド。
	 * @since 0.94
	 */
	public void testProjectionExBase() {
		Object[][] seqData1 = new Object[][]{
				algeAppleData,
				algeOrangeData,
				algeBananaData,
				algeMelonData,
		};
		ExAlgeSet set1 = new ExAlgeSet(Arrays.asList(new Exalge[]{
				new Exalge(algeAppleData),
				new Exalge(algeOrangeData),
				new Exalge(algeBananaData),
				new Exalge(algeMelonData),
		}));
		checkSetSequence(set1, seqData1);
		
		// projection-1
		Object[][] seqret1 = new Object[][]{
				{ new ExBase(nohatCashYenQ2), new BigDecimal(20)},
				{ new ExBase(nohatCashYenQ2), new BigDecimal(22)},
				{ new ExBase(nohatCashYenQ2), new BigDecimal(42)},
				{ new ExBase(nohatCashYenQ2), new BigDecimal(0) },
		};
		ExAlgeSet ret1 = set1.projection(new ExBase(nohatCashYenQ2));
		checkSetSequence(set1, seqData1);
		checkSetSequence(ret1, seqret1);
		
		// projection-2
		Object[][] seqret2 = new Object[][]{
				{ new ExBase(nohatCashYenQ4)  , new BigDecimal(84)},
				{ new ExBase(nohatCashYenQ4) , new BigDecimal(240)},
		};
		ExAlgeSet ret2 = set1.projection(new ExBase(nohatCashYenQ4));
		checkSetSequence(set1, seqData1);
		checkSetSequence(ret2, seqret2);
		
		// projection-3
		ExAlgeSet ret3 = set1.projection(new ExBase(nohatCashYen));
		checkSetSequence(set1, seqData1);
		assertTrue(ret3.isEmpty());
	}

	/**
	 * {@link exalge2.ExAlgeSet#projection(exalge2.ExBaseSet)} のためのテスト・メソッド。
	 * @since 0.94
	 */
	public void testProjectionExBaseSet() {
		Object[][] seqData1 = new Object[][]{
				algeAppleData,
				algeOrangeData,
				algeBananaData,
				algeMelonData,
		};
		ExAlgeSet set1 = new ExAlgeSet(Arrays.asList(new Exalge[]{
				new Exalge(algeAppleData),
				new Exalge(algeOrangeData),
				new Exalge(algeBananaData),
				new Exalge(algeMelonData),
		}));
		checkSetSequence(set1, seqData1);
		
		// projection-1
		ExBaseSet bases1 = new ExBaseSet(Arrays.asList(new ExBase[]{
				new ExBase(nohatCashYenQ1), new ExBase(hatCashYenQ1),
				new ExBase(nohatCashYenQ2), new ExBase(hatCashYenQ2),
				new ExBase(nohatCashYenQ3), new ExBase(hatCashYenQ3),
				new ExBase(nohatCashYenQ4), new ExBase(hatCashYenQ4),
		}));
		Object[][] seqret1 = new Object[][]{
				{
					new ExBase(nohatCashYenQ1) , new BigDecimal(10),
					new ExBase(nohatCashYenQ2) , new BigDecimal(20),
					new ExBase(hatCashYenQ3)   , new BigDecimal(30),
					new ExBase(hatCashYenQ4)   , new BigDecimal(40),
				},
				{
					new ExBase(nohatCashYenQ1)  , new BigDecimal(11),
					new ExBase(nohatCashYenQ2)  , new BigDecimal(22),
					new ExBase(hatCashYenQ3)    , new BigDecimal(33),
					new ExBase(hatCashYenQ4)    , new BigDecimal(44),
				},
				{
					new ExBase(hatCashYenQ1)    , new BigDecimal(21),
					new ExBase(nohatCashYenQ2)  , new BigDecimal(42),
					new ExBase(hatCashYenQ3)    , new BigDecimal(63),
					new ExBase(nohatCashYenQ4)  , new BigDecimal(84),
				},
				{
					new ExBase(hatCashYenQ1)   , new BigDecimal(120),
					new ExBase(nohatCashYenQ2) , new BigDecimal(0),
					new ExBase(hatCashYenQ3)   , new BigDecimal(0),
					new ExBase(nohatCashYenQ4) , new BigDecimal(240),
				},
		};
		ExAlgeSet ret1 = set1.projection(bases1);
		checkSetSequence(set1, seqData1);
		checkSetSequence(ret1, seqret1);
		
		// projection-2
		ExBaseSet bases2 = new ExBaseSet(Arrays.asList(new ExBase[]{
				new ExBase(nohatAppleYenQ1), new ExBase(hatAppleYenQ1),
				new ExBase(nohatOrangeYenQ2), new ExBase(hatOrangeYenQ2),
				new ExBase(nohatBananaYen), new ExBase(hatBananaYen),
				new ExBase(nohatMelonYenQ4), new ExBase(hatMelonYenQ4),
		}));
		Object[][] seqret2 = new Object[][]{
				{
					new ExBase(hatAppleYenQ1)  , new BigDecimal(10),
				},
				{
					new ExBase(hatOrangeYenQ2)  , new BigDecimal(22),
				},
				{
					new ExBase(hatMelonYenQ4)  , new BigDecimal(240),
				},
		};
		ExAlgeSet ret2 = set1.projection(bases2);
		checkSetSequence(set1, seqData1);
		checkSetSequence(ret2, seqret2);
		
		// projection-3
		ExBaseSet bases3 = new ExBaseSet(Arrays.asList(new ExBase[]{
				new ExBase(nohatAppleYen) , new ExBase(hatAppleYen),
				new ExBase(nohatOrangeYen), new ExBase(hatOrangeYen),
				new ExBase(nohatBananaYen), new ExBase(hatBananaYen),
				new ExBase(nohatMelonYen) , new ExBase(hatMelonYen),
				new ExBase(nohatCashYen)  , new ExBase(hatCashYen),
		}));
		ExAlgeSet ret3 = set1.projection(bases3);
		checkSetSequence(set1, seqData1);
		assertTrue(ret3.isEmpty());
		
		// projection-4
		ExBaseSet bases4 = new ExBaseSet();
		ExAlgeSet ret4 = set1.projection(bases4);
		checkSetSequence(set1, seqData1);
		assertTrue(ret4.isEmpty());
	}

	/**
	 * {@link exalge2.ExAlgeSet#patternProjection(exalge2.ExBase)} のためのテスト・メソッド。
	 * @since 0.94
	 */
	public void testPatternProjectionExBase() {
		Object[][] seqData1 = new Object[][]{
				algeAppleData,
				algeOrangeData,
				algeBananaData,
				algeMelonData,
		};
		ExAlgeSet set1 = new ExAlgeSet(Arrays.asList(new Exalge[]{
				new Exalge(algeAppleData),
				new Exalge(algeOrangeData),
				new Exalge(algeBananaData),
				new Exalge(algeMelonData),
		}));
		checkSetSequence(set1, seqData1);
		
		// projection-1
		ExBase pat1n = new ExBase("メロン-NO_HAT-*-Y*Q*-*");
		ExBase pat1h = new ExBase("メロン-HAT-*-Y*Q*-*");
		Object[][] seqret1 = new Object[][]{
				{
					new ExBase(nohatMelonYenQ1), new BigDecimal(120),
					new ExBase(hatMelonYenQ2)  , new BigDecimal(0),
					new ExBase(nohatMelonYenQ3), new BigDecimal(0),
					new ExBase(hatMelonYenQ4)  , new BigDecimal(240),
				},
		};
		ExAlgeSet ret1n = set1.patternProjection(pat1n);
		checkSetSequence(set1, seqData1);
		checkSetSequence(ret1n, seqret1);
		ExAlgeSet ret1h = set1.patternProjection(pat1h);
		checkSetSequence(set1, seqData1);
		checkSetSequence(ret1h, seqret1);
		
		// projection-2
		ExBase pat2n = new ExBase("*-NO_HAT-円-*Q3*-*");
		ExBase pat2h = new ExBase("*-HAT-円-*Q3*-*");
		Object[][] seqret2 = new Object[][]{
				{
					new ExBase(nohatAppleYenQ3), new BigDecimal(30),
					new ExBase(hatCashYenQ3)   , new BigDecimal(30),
				},
				{
					new ExBase(nohatOrangeYenQ3), new BigDecimal(33),
					new ExBase(hatCashYenQ3)    , new BigDecimal(33),
				},
				{
					new ExBase(nohatBananaYenQ3), new BigDecimal(63),
					new ExBase(hatCashYenQ3)    , new BigDecimal(63),
				},
				{
					new ExBase(nohatMelonYenQ3), new BigDecimal(0),
					new ExBase(hatCashYenQ3)   , new BigDecimal(0),
				},
		};
		ExAlgeSet ret2n = set1.patternProjection(pat2n);
		checkSetSequence(set1, seqData1);
		checkSetSequence(ret2n, seqret2);
		ExAlgeSet ret2h = set1.patternProjection(pat2h);
		checkSetSequence(set1, seqData1);
		checkSetSequence(ret2h, seqret2);
		
		// projection-3
		ExBase pat3n = new ExBase("*-NO_HAT-*-*2008*-*");
		ExBase pat3h = new ExBase("*-HAT-*-*2008*-*");
		ExAlgeSet ret3n = set1.patternProjection(pat3n);
		checkSetSequence(set1, seqData1);
		assertTrue(ret3n.isEmpty());
		ExAlgeSet ret3h = set1.patternProjection(pat3h);
		checkSetSequence(set1, seqData1);
		assertTrue(ret3h.isEmpty());
	}

	/**
	 * {@link exalge2.ExAlgeSet#patternProjection(exalge2.ExBasePattern)} のためのテスト・メソッド。
	 * @since 0.94
	 */
	public void testPatternProjectionExBasePattern() {
		Object[][] seqData1 = new Object[][]{
				algeAppleData,
				algeOrangeData,
				algeBananaData,
				algeMelonData,
		};
		ExAlgeSet set1 = new ExAlgeSet(Arrays.asList(new Exalge[]{
				new Exalge(algeAppleData),
				new Exalge(algeOrangeData),
				new Exalge(algeBananaData),
				new Exalge(algeMelonData),
		}));
		checkSetSequence(set1, seqData1);
		
		// projection-1
		ExBasePattern pat1n = new ExBasePattern("メロン-NO_HAT-*-Y*Q*-*");
		ExBasePattern pat1h = new ExBasePattern("メロン-HAT-*-Y*Q*-*");
		ExBasePattern pat1w = new ExBasePattern("メロン-*-*-Y*Q*-*");
		Object[][] seqret1n = new Object[][]{
				{
					new ExBase(nohatMelonYenQ1), new BigDecimal(120),
					new ExBase(nohatMelonYenQ3), new BigDecimal(0),
				},
		};
		Object[][] seqret1h = new Object[][]{
				{
					new ExBase(hatMelonYenQ2)  , new BigDecimal(0),
					new ExBase(hatMelonYenQ4)  , new BigDecimal(240),
				},
		};
		Object[][] seqret1w = new Object[][]{
				{
					new ExBase(nohatMelonYenQ1), new BigDecimal(120),
					new ExBase(hatMelonYenQ2)  , new BigDecimal(0),
					new ExBase(nohatMelonYenQ3), new BigDecimal(0),
					new ExBase(hatMelonYenQ4)  , new BigDecimal(240),
				},
		};
		ExAlgeSet ret1n = set1.patternProjection(pat1n);
		checkSetSequence(set1, seqData1);
		checkSetSequence(ret1n, seqret1n);
		ExAlgeSet ret1h = set1.patternProjection(pat1h);
		checkSetSequence(set1, seqData1);
		checkSetSequence(ret1h, seqret1h);
		ExAlgeSet ret1w = set1.patternProjection(pat1w);
		checkSetSequence(set1, seqData1);
		checkSetSequence(ret1w, seqret1w);
		
		// projection-2
		ExBasePattern pat2n = new ExBasePattern("*-NO_HAT-円-*Q3*-*");
		ExBasePattern pat2h = new ExBasePattern("*-HAT-円-*Q3*-*");
		ExBasePattern pat2w = new ExBasePattern("*-*-円-*Q3*-*");
		Object[][] seqret2n = new Object[][]{
				{
					new ExBase(nohatAppleYenQ3), new BigDecimal(30),
				},
				{
					new ExBase(nohatOrangeYenQ3), new BigDecimal(33),
				},
				{
					new ExBase(nohatBananaYenQ3), new BigDecimal(63),
				},
				{
					new ExBase(nohatMelonYenQ3), new BigDecimal(0),
				},
		};
		Object[][] seqret2h = new Object[][]{
				{
					new ExBase(hatCashYenQ3)   , new BigDecimal(30),
				},
				{
					new ExBase(hatCashYenQ3)    , new BigDecimal(33),
				},
				{
					new ExBase(hatCashYenQ3)    , new BigDecimal(63),
				},
				{
					new ExBase(hatCashYenQ3)   , new BigDecimal(0),
				},
		};
		Object[][] seqret2w = new Object[][]{
				{
					new ExBase(nohatAppleYenQ3), new BigDecimal(30),
					new ExBase(hatCashYenQ3)   , new BigDecimal(30),
				},
				{
					new ExBase(nohatOrangeYenQ3), new BigDecimal(33),
					new ExBase(hatCashYenQ3)    , new BigDecimal(33),
				},
				{
					new ExBase(nohatBananaYenQ3), new BigDecimal(63),
					new ExBase(hatCashYenQ3)    , new BigDecimal(63),
				},
				{
					new ExBase(nohatMelonYenQ3), new BigDecimal(0),
					new ExBase(hatCashYenQ3)   , new BigDecimal(0),
				},
		};
		ExAlgeSet ret2n = set1.patternProjection(pat2n);
		checkSetSequence(set1, seqData1);
		checkSetSequence(ret2n, seqret2n);
		ExAlgeSet ret2h = set1.patternProjection(pat2h);
		checkSetSequence(set1, seqData1);
		checkSetSequence(ret2h, seqret2h);
		ExAlgeSet ret2w = set1.patternProjection(pat2w);
		checkSetSequence(set1, seqData1);
		checkSetSequence(ret2w, seqret2w);
		
		// projection-3
		ExBasePattern pat3n = new ExBasePattern("*-NO_HAT-*-*2008*-*");
		ExBasePattern pat3h = new ExBasePattern("*-HAT-*-*2008*-*");
		ExBasePattern pat3w = new ExBasePattern("*-*-*-*2008*-*");
		ExAlgeSet ret3n = set1.patternProjection(pat3n);
		checkSetSequence(set1, seqData1);
		assertTrue(ret3n.isEmpty());
		ExAlgeSet ret3h = set1.patternProjection(pat3h);
		checkSetSequence(set1, seqData1);
		assertTrue(ret3h.isEmpty());
		ExAlgeSet ret3w = set1.patternProjection(pat3w);
		checkSetSequence(set1, seqData1);
		assertTrue(ret3w.isEmpty());
	}

	/**
	 * {@link exalge2.ExAlgeSet#patternProjection(exalge2.ExBaseSet)} のためのテスト・メソッド。
	 * @since 0.94
	 */
	public void testPatternProjectionExBaseSet() {
		Object[][] seqData1 = new Object[][]{
				algeAppleData,
				algeOrangeData,
				algeBananaData,
				algeMelonData,
		};
		ExAlgeSet set1 = new ExAlgeSet(Arrays.asList(new Exalge[]{
				new Exalge(algeAppleData),
				new Exalge(algeOrangeData),
				new Exalge(algeBananaData),
				new Exalge(algeMelonData),
		}));
		checkSetSequence(set1, seqData1);

		// projection-1
		ExBaseSet pat1n = new ExBaseSet(Arrays.asList(new ExBase[]{
				new ExBase("りんご-NO_HAT-*-Y*Q*-*"),
				new ExBase("メロン-NO_HAT-*-Y*Q*-*"),
		}));
		ExBaseSet pat1h = new ExBaseSet(Arrays.asList(new ExBase[]{
				new ExBase("りんご-HAT-*-Y*Q*-*"),
				new ExBase("メロン-HAT-*-Y*Q*-*"),
		}));
		Object[][] seqret1w = new Object[][]{
				{
					new ExBase(hatAppleYenQ1)  , new BigDecimal(10),
					new ExBase(hatAppleYenQ2)  , new BigDecimal(20),
					new ExBase(nohatAppleYenQ3), new BigDecimal(30),
					new ExBase(nohatAppleYenQ4), new BigDecimal(40),
				},
				{
					new ExBase(nohatMelonYenQ1), new BigDecimal(120),
					new ExBase(hatMelonYenQ2)  , new BigDecimal(0),
					new ExBase(nohatMelonYenQ3), new BigDecimal(0),
					new ExBase(hatMelonYenQ4)  , new BigDecimal(240),
				},
		};
		ExAlgeSet ret1n = set1.patternProjection(pat1n);
		checkSetSequence(set1, seqData1);
		checkSetSequence(ret1n, seqret1w);
		ExAlgeSet ret1h = set1.patternProjection(pat1h);
		checkSetSequence(set1, seqData1);
		checkSetSequence(ret1h, seqret1w);
		
		// projection-2
		ExBaseSet pat2n = new ExBaseSet(Arrays.asList(new ExBase[]{
				new ExBase("*-NO_HAT-円-Y*Q2*-*"),
				new ExBase("*-NO_HAT-円-Y*Q3*-*"),
		}));
		ExBaseSet pat2h = new ExBaseSet(Arrays.asList(new ExBase[]{
				new ExBase("*-HAT-円-Y*Q2*-*"),
				new ExBase("*-HAT-円-Y*Q3*-*"),
		}));
		Object[][] seqret2w = new Object[][]{
				{
					new ExBase(hatAppleYenQ2)  , new BigDecimal(20),
					new ExBase(nohatCashYenQ2) , new BigDecimal(20),
					new ExBase(nohatAppleYenQ3), new BigDecimal(30),
					new ExBase(hatCashYenQ3)   , new BigDecimal(30),
				},
				{
					new ExBase(hatOrangeYenQ2)  , new BigDecimal(22),
					new ExBase(nohatCashYenQ2)  , new BigDecimal(22),
					new ExBase(nohatOrangeYenQ3), new BigDecimal(33),
					new ExBase(hatCashYenQ3)    , new BigDecimal(33),
				},
				{
					new ExBase(hatBananaYenQ2)  , new BigDecimal(42),
					new ExBase(nohatCashYenQ2)  , new BigDecimal(42),
					new ExBase(nohatBananaYenQ3), new BigDecimal(63),
					new ExBase(hatCashYenQ3)    , new BigDecimal(63),
				},
				{
					new ExBase(hatMelonYenQ2)  , new BigDecimal(0),
					new ExBase(nohatCashYenQ2) , new BigDecimal(0),
					new ExBase(nohatMelonYenQ3), new BigDecimal(0),
					new ExBase(hatCashYenQ3)   , new BigDecimal(0),
				},
		};
		ExAlgeSet ret2n = set1.patternProjection(pat2n);
		checkSetSequence(set1, seqData1);
		checkSetSequence(ret2n, seqret2w);
		ExAlgeSet ret2h = set1.patternProjection(pat2h);
		checkSetSequence(set1, seqData1);
		checkSetSequence(ret2h, seqret2w);
		
		// projection-3
		ExBaseSet pat3n = new ExBaseSet(Arrays.asList(new ExBase[]{
				new ExBase("*-NO_HAT-円-*2008*-*"),
				new ExBase("*-NO_HAT-＄-*-*"),
		}));
		ExBaseSet pat3h = new ExBaseSet(Arrays.asList(new ExBase[]{
				new ExBase("*-HAT-円-*2008*-*"),
				new ExBase("*-HAT-＄-*-*"),
		}));
		ExAlgeSet ret3n = set1.patternProjection(pat3n);
		checkSetSequence(set1, seqData1);
		assertTrue(ret3n.isEmpty());
		ExAlgeSet ret3h = set1.patternProjection(pat3h);
		checkSetSequence(set1, seqData1);
		assertTrue(ret3h.isEmpty());
		
		// projection-4
		ExBaseSet pat4 = new ExBaseSet();
		ExAlgeSet ret4 = set1.patternProjection(pat4);
		checkSetSequence(set1, seqData1);
		assertTrue(ret4.isEmpty());
	}

	/**
	 * {@link exalge2.ExAlgeSet#patternProjection(exalge2.ExBasePatternSet)} のためのテスト・メソッド。
	 * @since 0.94
	 */
	public void testPatternProjectionExBasePatternSet() {
		Object[][] seqData1 = new Object[][]{
				algeAppleData,
				algeOrangeData,
				algeBananaData,
				algeMelonData,
		};
		ExAlgeSet set1 = new ExAlgeSet(Arrays.asList(new Exalge[]{
				new Exalge(algeAppleData),
				new Exalge(algeOrangeData),
				new Exalge(algeBananaData),
				new Exalge(algeMelonData),
		}));
		checkSetSequence(set1, seqData1);

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
		Object[][] seqret1n = new Object[][]{
				{
					new ExBase(nohatAppleYenQ3), new BigDecimal(30),
					new ExBase(nohatAppleYenQ4), new BigDecimal(40),
				},
				{
					new ExBase(nohatMelonYenQ1), new BigDecimal(120),
					new ExBase(nohatMelonYenQ3), new BigDecimal(0),
				},
		};
		Object[][] seqret1h = new Object[][]{
				{
					new ExBase(hatAppleYenQ1)  , new BigDecimal(10),
					new ExBase(hatAppleYenQ2)  , new BigDecimal(20),
				},
				{
					new ExBase(hatMelonYenQ2)  , new BigDecimal(0),
					new ExBase(hatMelonYenQ4)  , new BigDecimal(240),
				},
		};
		Object[][] seqret1w = new Object[][]{
				{
					new ExBase(hatAppleYenQ1)  , new BigDecimal(10),
					new ExBase(hatAppleYenQ2)  , new BigDecimal(20),
					new ExBase(nohatAppleYenQ3), new BigDecimal(30),
					new ExBase(nohatAppleYenQ4), new BigDecimal(40),
				},
				{
					new ExBase(nohatMelonYenQ1), new BigDecimal(120),
					new ExBase(hatMelonYenQ2)  , new BigDecimal(0),
					new ExBase(nohatMelonYenQ3), new BigDecimal(0),
					new ExBase(hatMelonYenQ4)  , new BigDecimal(240),
				},
		};
		ExAlgeSet ret1n = set1.patternProjection(pat1n);
		checkSetSequence(set1, seqData1);
		checkSetSequence(ret1n, seqret1n);
		ExAlgeSet ret1h = set1.patternProjection(pat1h);
		checkSetSequence(set1, seqData1);
		checkSetSequence(ret1h, seqret1h);
		ExAlgeSet ret1w = set1.patternProjection(pat1w);
		checkSetSequence(set1, seqData1);
		checkSetSequence(ret1w, seqret1w);
		
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
		Object[][] seqret2n = new Object[][]{
				{
					new ExBase(nohatCashYenQ2) , new BigDecimal(20),
					new ExBase(nohatAppleYenQ3), new BigDecimal(30),
				},
				{
					new ExBase(nohatCashYenQ2)  , new BigDecimal(22),
					new ExBase(nohatOrangeYenQ3), new BigDecimal(33),
				},
				{
					new ExBase(nohatCashYenQ2)  , new BigDecimal(42),
					new ExBase(nohatBananaYenQ3), new BigDecimal(63),
				},
				{
					new ExBase(nohatCashYenQ2) , new BigDecimal(0),
					new ExBase(nohatMelonYenQ3), new BigDecimal(0),
				},
		};
		Object[][] seqret2h = new Object[][]{
				{
					new ExBase(hatAppleYenQ2)  , new BigDecimal(20),
					new ExBase(hatCashYenQ3)   , new BigDecimal(30),
				},
				{
					new ExBase(hatOrangeYenQ2)  , new BigDecimal(22),
					new ExBase(hatCashYenQ3)    , new BigDecimal(33),
				},
				{
					new ExBase(hatBananaYenQ2)  , new BigDecimal(42),
					new ExBase(hatCashYenQ3)    , new BigDecimal(63),
				},
				{
					new ExBase(hatMelonYenQ2)  , new BigDecimal(0),
					new ExBase(hatCashYenQ3)   , new BigDecimal(0),
				},
		};
		Object[][] seqret2w = new Object[][]{
				{
					new ExBase(hatAppleYenQ2)  , new BigDecimal(20),
					new ExBase(nohatCashYenQ2) , new BigDecimal(20),
					new ExBase(nohatAppleYenQ3), new BigDecimal(30),
					new ExBase(hatCashYenQ3)   , new BigDecimal(30),
				},
				{
					new ExBase(hatOrangeYenQ2)  , new BigDecimal(22),
					new ExBase(nohatCashYenQ2)  , new BigDecimal(22),
					new ExBase(nohatOrangeYenQ3), new BigDecimal(33),
					new ExBase(hatCashYenQ3)    , new BigDecimal(33),
				},
				{
					new ExBase(hatBananaYenQ2)  , new BigDecimal(42),
					new ExBase(nohatCashYenQ2)  , new BigDecimal(42),
					new ExBase(nohatBananaYenQ3), new BigDecimal(63),
					new ExBase(hatCashYenQ3)    , new BigDecimal(63),
				},
				{
					new ExBase(hatMelonYenQ2)  , new BigDecimal(0),
					new ExBase(nohatCashYenQ2) , new BigDecimal(0),
					new ExBase(nohatMelonYenQ3), new BigDecimal(0),
					new ExBase(hatCashYenQ3)   , new BigDecimal(0),
				},
		};
		ExAlgeSet ret2n = set1.patternProjection(pat2n);
		checkSetSequence(set1, seqData1);
		checkSetSequence(ret2n, seqret2n);
		ExAlgeSet ret2h = set1.patternProjection(pat2h);
		checkSetSequence(set1, seqData1);
		checkSetSequence(ret2h, seqret2h);
		ExAlgeSet ret2w = set1.patternProjection(pat2w);
		checkSetSequence(set1, seqData1);
		checkSetSequence(ret2w, seqret2w);
		
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
		ExAlgeSet ret3n = set1.patternProjection(pat3n);
		checkSetSequence(set1, seqData1);
		assertTrue(ret3n.isEmpty());
		ExAlgeSet ret3h = set1.patternProjection(pat3h);
		checkSetSequence(set1, seqData1);
		assertTrue(ret3h.isEmpty());
		ExAlgeSet ret3w = set1.patternProjection(pat3w);
		checkSetSequence(set1, seqData1);
		assertTrue(ret3w.isEmpty());
		
		// projection-4
		ExBasePatternSet pat4 = new ExBasePatternSet();
		ExAlgeSet ret4 = set1.patternProjection(pat4);
		checkSetSequence(set1, seqData1);
		assertTrue(ret4.isEmpty());
	}

	/**
	 * {@link exalge2.ExAlgeSet#set(int, exalge2.Exalge)} のためのテスト・メソッド。
	 */
	public void testSetIntExalge() {
		Exalge[] alges = new Exalge[]{
				new Exalge(algeAppleData),
				new Exalge(algeBananaData),
				new Exalge(algeOrangeData),
				new Exalge(algeMelonData),
		};
		Object[][] seqData1 = new Object[][]{
				algeAppleData,
				algeBananaData,
				algeOrangeData,
				algeMelonData,
		};
		Object[][] seqData2 = new Object[][]{
				algeAppleData,
				algeOrangeData,
				algeBananaData,
				algeMelonData,
		};
		
		Exalge algeApple  = new Exalge(algeAppleData);
		Exalge algeOrange = new Exalge(algeOrangeData);
		Exalge algeBanana = new Exalge(algeBananaData);
		Exalge algeMelon  = new Exalge(algeMelonData);
		
		ExAlgeSet set1 = new ExAlgeSet(Arrays.asList(alges));
		assertFalse(set1.isEmpty());
		checkSetSequence(set1, seqData1);

		Exalge ret;
		// index:0
		ret = set1.set(0, algeApple);
		assertTrue(ret.equals(algeApple));
		assertTrue(ret.equals(set1.get(0)));
		// index:1
		ret = set1.set(1, algeOrange);
		assertTrue(ret.equals(algeBanana));
		assertFalse(ret.equals(set1.get(1)));
		// index:2
		ret = set1.set(2, algeBanana);
		assertTrue(ret.equals(algeOrange));
		assertFalse(ret.equals(set1.get(2)));
		// index:3
		ret = set1.set(3, algeMelon);
		assertTrue(ret.equals(algeMelon));
		assertTrue(ret.equals(set1.get(3)));
		// sequence
		checkSetSequence(set1, seqData2);
	}

	/**
	 * {@link exalge2.ExAlgeSet#add(exalge2.Exalge)} のためのテスト・メソッド。
	 */
	public void testAddExalge() {
		Object[][] seqData = new Object[][]{
			algeAppleData,
			algeOrangeData,
			algeBananaData,
			algeMelonData,
		};

		ExAlgeSet set = new ExAlgeSet();
		assertTrue(set.isEmpty());
		assertEquals(0, set.size());
		for (int i = 0; i < seqData.length; i++) {
			assertTrue(set.add(new Exalge(seqData[i])));
		}

		assertFalse(set.isEmpty());
		checkSetSequence(set, seqData);
	}

	/**
	 * {@link exalge2.ExAlgeSet#add(int, exalge2.Exalge)} のためのテスト・メソッド。
	 */
	public void testAddIntExalge() {
		Object[][] seqData = new Object[][]{
			algeAppleData,
			algeOrangeData,
			algeBananaData,
			algeMelonData,
		};

		ExAlgeSet set = new ExAlgeSet();
		assertTrue(set.isEmpty());
		assertEquals(0, set.size());
		set.add(0, new Exalge(algeMelonData));
		set.add(0, new Exalge(algeAppleData));
		set.add(1, new Exalge(algeOrangeData));
		set.add(2, new Exalge(algeBananaData));

		assertFalse(set.isEmpty());
		checkSetSequence(set, seqData);
	}

	/**
	 * {@link exalge2.ExAlgeSet#addAll(java.util.Collection)} のためのテスト・メソッド。
	 */
	public void testAddAllCollectionOfQextendsExalge() {
		Object[][] seqData = new Object[][]{
			algeAppleData,
			algeOrangeData,
			algeBananaData,
			algeMelonData,
		};
		
		Exalge[] alges = new Exalge[]{
			new Exalge(algeAppleData),
			new Exalge(algeOrangeData),
			new Exalge(algeBananaData),
			new Exalge(algeMelonData),
		};
		
		ExAlgeSet set = new ExAlgeSet();
		assertTrue(set.isEmpty());
		assertEquals(0, set.size());
		assertTrue(set.addAll(Arrays.asList(alges)));

		assertFalse(set.isEmpty());
		checkSetSequence(set, seqData);
	}

	/**
	 * {@link exalge2.ExAlgeSet#addAll(int, java.util.Collection)} のためのテスト・メソッド。
	 */
	public void testAddAllIntCollectionOfQextendsExalge() {
		Object[][] seqData = new Object[][]{
			algeAppleData,
			algeOrangeData,
			algeBananaData,
			algeMelonData,
		};
			
		Exalge[] alges1 = new Exalge[]{
			new Exalge(algeAppleData),
			new Exalge(algeMelonData),
		};
		Exalge[] alges2 = new Exalge[]{
			new Exalge(algeOrangeData),
			new Exalge(algeBananaData),
		};
		
		ExAlgeSet set = new ExAlgeSet();
		assertTrue(set.isEmpty());
		assertEquals(0, set.size());
		assertTrue(set.addAll(0, Arrays.asList(alges1)));
		assertTrue(set.addAll(1, Arrays.asList(alges2)));

		assertFalse(set.isEmpty());
		checkSetSequence(set, seqData);
	}

	/**
	 * {@link exalge2.ExAlgeSet#clone()} のためのテスト・メソッド。
	 */
	public void testClone() {
		Exalge[] alges1 = new Exalge[]{
				new Exalge(algeAppleData),
				new Exalge(algeOrangeData),
				new Exalge(algeBananaData),
		};
		ExAlgeSet set1 = new ExAlgeSet(Arrays.asList(alges1));
		ExAlgeSet set2 = (ExAlgeSet)set1.clone();
		assertEquals(set1, set2);
		
		set2.remove(new Exalge(algeOrangeData));
		Object[][] seqData2 = new Object[][]{
				algeAppleData,
				algeBananaData,
		};
		assertFalse(set1.equals(set2));
		checkSetSequence(set2, seqData2);
		
		Exalge[] alges2 = new Exalge[]{
				new Exalge(algeMelonData),
				new Exalge(algeBananaData),
				new Exalge(algeOrangeData),
				new Exalge(algeAppleData),
		};
		Object[][] seqData3 = new Object[][]{
				algeAppleData,
				algeOrangeData,
				algeBananaData,
				algeMelonData,
				algeBananaData,
				algeOrangeData,
				algeAppleData,
		};
		ExAlgeSet set3 = (ExAlgeSet)set1.clone();
		set3.addAll(Arrays.asList(alges2));
		assertFalse(set1.equals(set3));
		checkSetSequence(set3, seqData3);
	}

	/**
	 * {@link exalge2.ExAlgeSet#generalProjection(ExBase)} のためのテスト・メソッド。
	 * @since 0.960
	 */
	public void testGeneralProjectionExBase() {
		Object[][] seqData1 = new Object[][]{
				algeAppleData,
				algeOrangeData,
				algeBananaData,
				algeMelonData,
		};
		ExAlgeSet set1 = new ExAlgeSet(Arrays.asList(new Exalge[]{
				new Exalge(algeAppleData),
				new Exalge(algeOrangeData),
				new Exalge(algeBananaData),
				new Exalge(algeMelonData),
		}));
		checkSetSequence(set1, seqData1);
		
		// projection-1
		ExBase nAppleYenQ1 = new ExBase(nohatAppleYenQ1);
		ExBase hAppleYenQ1 = new ExBase(hatAppleYenQ1);
		ExBaseSet targetBases1 = new ExBaseSet(Arrays.asList(nAppleYenQ1, hAppleYenQ1));
		for (ExBase targetBase : targetBases1) {
			ExAlgeSet retSet = set1.generalProjection(targetBase);
			checkSetSequence(set1, seqData1);
			assertEquals(1, retSet.size());
			Exalge alge = retSet.get(0);
			assertEquals(1, alge.getNumElements());
			assertTrue(alge.containsBase(hAppleYenQ1));
			Exalge appleAlge = new Exalge(algeAppleData);
			assertTrue(appleAlge.get(hAppleYenQ1).compareTo(alge.get(hAppleYenQ1))==0);
		}
		
		// projection-2
		ExBase nCashYenQ3 = new ExBase(nohatCashYenQ3);
		ExBase hCashYenQ3 = new ExBase(hatCashYenQ3);
		ExBaseSet targetBases2 = new ExBaseSet(Arrays.asList(nCashYenQ3, hCashYenQ3));
		ExAlgeSet retAlgeSet = set1.projection(targetBases2);
		checkSetSequence(set1, seqData1);
		for (ExBase targetBase : targetBases2) {
			ExAlgeSet retSet = set1.generalProjection(targetBase);
			checkSetSequence(set1, seqData1);
			assertEquals(retAlgeSet, retSet);
		}
		
		// Check null exception
		boolean isCought = false;
		try {
			set1.generalProjection((ExBase)null);
			isCought = false;
		} catch (NullPointerException ex) {
			isCought = true;
		}
		assertTrue(isCought);
	}

	/**
	 * {@link exalge2.ExAlgeSet#generalProjection(ExBaseSet)} のためのテスト・メソッド。
	 * @since 0.960
	 */
	public void testGeneralProjectionExBaseSet() {
		ExBase[] testBaseData1 = new ExBase[]{
				new ExBase(nohatCashYenQ1),
				new ExBase(nohatCashYenQ2),
				new ExBase(nohatCashYenQ3),
				new ExBase(nohatCashYenQ4),
		};
		ExBase[] testBaseData2 = new ExBase[]{
				new ExBase(hatCashYenQ1),
				new ExBase(hatCashYenQ2),
				new ExBase(hatCashYenQ3),
				new ExBase(hatCashYenQ4),
		};
		ExBase[] testAllBaseData = new ExBase[]{
				new ExBase(nohatCashYenQ1),
				new ExBase(nohatCashYenQ2),
				new ExBase(nohatCashYenQ3),
				new ExBase(nohatCashYenQ4),
				new ExBase(hatCashYenQ1),
				new ExBase(hatCashYenQ2),
				new ExBase(hatCashYenQ3),
				new ExBase(hatCashYenQ4),
		};
		
		Object[][] seqData1 = new Object[][]{
				algeAppleData,
				algeOrangeData,
				algeBananaData,
				algeMelonData,
		};
		ExAlgeSet set1 = new ExAlgeSet(Arrays.asList(new Exalge[]{
				new Exalge(algeAppleData),
				new Exalge(algeOrangeData),
				new Exalge(algeBananaData),
				new Exalge(algeMelonData),
		}));
		checkSetSequence(set1, seqData1);

		ExBaseSet testAllBases = new ExBaseSet(Arrays.asList(testAllBaseData));
		ExAlgeSet retAlgeSet = set1.projection(testAllBases);
		for (Exalge alge : retAlgeSet) {
			assertTrue(testAllBases.containsAll(alge.getBases()));
		}
		
		// projection-1
		ExBaseSet testBases1 = new ExBaseSet(Arrays.asList(testBaseData1));
		ExAlgeSet ret1 = set1.generalProjection(testBases1);
		checkSetSequence(set1, seqData1);
		assertEquals(retAlgeSet, ret1);
		
		// projection-2
		ExBaseSet testBases2 = new ExBaseSet(Arrays.asList(testBaseData2));
		ExAlgeSet ret2 = set1.generalProjection(testBases2);
		checkSetSequence(set1, seqData1);
		assertEquals(retAlgeSet, ret2);
		
		// Check empty
		ExAlgeSet ret3 = set1.generalProjection(new ExBaseSet());
		checkSetSequence(set1, seqData1);
		assertTrue(ret3.isEmpty());
		
		// Check null exception
		boolean isCought = false;
		try {
			set1.generalProjection((ExBaseSet)null);
			isCought = false;
		} catch (NullPointerException ex) {
			isCought = true;
		}
		assertTrue(isCought);
	}

	/**
	 * {@link exalge2.ExAlgeSet#getNoHatBases()} のためのテスト・メソッド。
	 * @since 0.970
	 */
	public void testGetNoHatBases() {
		ExAlgeSet testAlge = new ExAlgeSet(Arrays.asList(
					ExalgeTest.makeAlge(new ExBase(hatAppleYen) , 20),
					ExalgeTest.makeAlge(new ExBase(hatOrangeYen), 40),
					ExalgeTest.makeAlge(new ExBase(hatBananaYen), 60),
					ExalgeTest.makeAlge(new ExBase(nohatCashYen), 120),
					ExalgeTest.makeAlge(new ExBase(nohatMelonYen), 0),
					ExalgeTest.makeAlge(new ExBase(hatMelonYen)  , 0)
				));
		ExBaseSet retBases;
		
		// Empty
		ExAlgeSet algeEmpty = new ExAlgeSet();
		retBases = algeEmpty.getNoHatBases();
		assertTrue(retBases.isEmpty());
		
		// alge
		ExBaseSet ansBases = ExalgeTest.makeBaseSet(new ExBase(nohatCashYen), new ExBase(nohatMelonYen));
		retBases = testAlge.getNoHatBases();
		assertEquals(retBases, ansBases);
	}

	/**
	 * {@link exalge2.ExAlgeSet#getHatBases()} のためのテスト・メソッド。
	 * @since 0.970
	 */
	public void testGetHatBases() {
		ExAlgeSet testAlge = new ExAlgeSet(Arrays.asList(
				ExalgeTest.makeAlge(new ExBase(hatAppleYen) , 20),
				ExalgeTest.makeAlge(new ExBase(hatOrangeYen), 40),
				ExalgeTest.makeAlge(new ExBase(hatBananaYen), 60),
				ExalgeTest.makeAlge(new ExBase(nohatCashYen), 120),
				ExalgeTest.makeAlge(new ExBase(nohatMelonYen), 0),
				ExalgeTest.makeAlge(new ExBase(hatMelonYen)  , 0)
			));
		ExBaseSet retBases;
		
		// Empty
		ExAlgeSet algeEmpty = new ExAlgeSet();
		retBases = algeEmpty.getHatBases();
		assertTrue(retBases.isEmpty());
		
		// alge
		ExBaseSet ansBases = ExalgeTest.makeBaseSet(new ExBase(hatAppleYen), new ExBase(hatOrangeYen), new ExBase(hatBananaYen), new ExBase(hatMelonYen));
		retBases = testAlge.getHatBases();
		assertEquals(retBases, ansBases);
	}

	/**
	 * {@link exalge2.ExAlgeSet#getBasesWithRemoveHat()} のためのテスト・メソッド。
	 * @since 0.970
	 */
	public void testGetBasesWithRemoveHat() {
		ExAlgeSet testAlge = new ExAlgeSet(Arrays.asList(
				ExalgeTest.makeAlge(new ExBase(hatAppleYen) , 20),
				ExalgeTest.makeAlge(new ExBase(hatOrangeYen), 40),
				ExalgeTest.makeAlge(new ExBase(hatBananaYen), 60),
				ExalgeTest.makeAlge(new ExBase(nohatCashYen), 120),
				ExalgeTest.makeAlge(new ExBase(nohatMelonYen), 0),
				ExalgeTest.makeAlge(new ExBase(hatMelonYen)  , 0)
			));
		ExBase[] bases = new ExBase[]{
			new ExBase(nohatAppleYen),
			new ExBase(nohatOrangeYen),
			new ExBase(nohatBananaYen),
			new ExBase(nohatCashYen),
			new ExBase(nohatMelonYen),
		};
		ExBaseSet retBases;
		
		// Empty
		ExAlgeSet algeEmpty = new ExAlgeSet();
		retBases = algeEmpty.getBasesWithRemoveHat();
		assertTrue(retBases.isEmpty());
		
		// alge
		ExBaseSet ansBases = ExalgeTest.makeBaseSet(bases);
		retBases = testAlge.getBasesWithRemoveHat();
		assertEquals(retBases, ansBases);
	}

	/**
	 * {@link exalge2.ExAlgeSet#getBasesWithSetHat()} のためのテスト・メソッド。
	 * @since 0.970
	 */
	public void testGetBasesWithSetHat() {
		ExAlgeSet testAlge = new ExAlgeSet(Arrays.asList(
				ExalgeTest.makeAlge(new ExBase(hatAppleYen) , 20),
				ExalgeTest.makeAlge(new ExBase(hatOrangeYen), 40),
				ExalgeTest.makeAlge(new ExBase(hatBananaYen), 60),
				ExalgeTest.makeAlge(new ExBase(nohatCashYen), 120),
				ExalgeTest.makeAlge(new ExBase(nohatMelonYen), 0),
				ExalgeTest.makeAlge(new ExBase(hatMelonYen)  , 0)
			));
		ExBase[] bases = new ExBase[]{
			new ExBase(hatAppleYen),
			new ExBase(hatOrangeYen),
			new ExBase(hatBananaYen),
			new ExBase(hatCashYen),
			new ExBase(hatMelonYen),
		};
		ExBaseSet retBases;
		
		// Empty
		ExAlgeSet algeEmpty = new ExAlgeSet();
		retBases = algeEmpty.getBasesWithSetHat();
		assertTrue(retBases.isEmpty());
		
		// alge
		ExBaseSet ansBases = ExalgeTest.makeBaseSet(bases);
		retBases = testAlge.getBasesWithSetHat();
		assertEquals(retBases, ansBases);
	}
	
	/**
	 * {@link exalge2.ExAlgeSet#oneValueProjection(BigDecimal)} のためのテスト・メソッド。
	 * @since 0.983
	 */
	public void testValueProjectionBigDecimal() {
		Object[][] seqData1 = new Object[][]{
				algeAppleData,
				algeOrangeData,
				algeBananaData,
				algeMelonData,
		};
		Object[][] seqData2 = new Object[][]{
				algeAppleData,
				algeOrangeDataWithNull,
				algeMelonDataWithNull,
				algeMangoDataWithNull,
		};
		ExAlgeSet set0 = new ExAlgeSet();
		ExAlgeSet set1 = new ExAlgeSet(Arrays.asList(new Exalge[]{
				new Exalge(algeAppleData),
				new Exalge(algeOrangeData),
				new Exalge(algeBananaData),
				new Exalge(algeMelonData)
		}));
		ExAlgeSet set2 = new ExAlgeSet(Arrays.asList(new Exalge[]{
				new Exalge(algeAppleData),
				new Exalge(algeOrangeDataWithNull),
				new Exalge(algeMelonDataWithNull),
				new Exalge(algeMangoDataWithNull)
		}));
		checkSetSequence(set1, seqData1);
		checkSetSequence(set2, seqData2);
		
		final BigDecimal valN = null;
		final BigDecimal val0 = BigDecimal.ZERO;
		final BigDecimal val1 = new BigDecimal(40);
		
		ExAlgeSet ansSet0_1 = new ExAlgeSet(Arrays.asList(new Exalge[]{
				new Exalge(new Object[]{
						new ExBase(hatMelonYenQ2)  , new BigDecimal(0),
						new ExBase(nohatCashYenQ2) , new BigDecimal(0),
						new ExBase(nohatMelonYenQ3), new BigDecimal(0),
						new ExBase(hatCashYenQ3)   , new BigDecimal(0),
				}),
		}));
		ExAlgeSet ansSet0_2 = new ExAlgeSet(Arrays.asList(new Exalge[]{
				new Exalge(new Object[]{
						new ExBase(nohatMelonYenQ3), new BigDecimal(0),
						new ExBase(hatCashYenQ3)   , new BigDecimal(0),
				}),
		}));
		ExAlgeSet ansSet1 = new ExAlgeSet(Arrays.asList(new Exalge[]{
				new Exalge(new Object[]{
						new ExBase(nohatAppleYenQ4), new BigDecimal(40),
						new ExBase(hatCashYenQ4)   , new BigDecimal(40),
				}),
			}));
		ExAlgeSet ansSetN = new ExAlgeSet(Arrays.asList(new Exalge[]{
				new Exalge(new Object[]{
						new ExBase(nohatOrangeYenQ3), null,
						new ExBase(hatCashYenQ3)    , null,
						new ExBase(nohatOrangeYenQ4), null,
						new ExBase(hatCashYenQ4)    , null,
				}),
				new Exalge(new Object[]{
						new ExBase(hatMelonYenQ2)  , null,
						new ExBase(nohatCashYenQ2) , null,
						new ExBase(hatMelonYenQ4)  , null,
						new ExBase(nohatCashYenQ4) , null,
				}),
				new Exalge(new Object[]{
						new ExBase(hatMangoYenQ1)  , null,
						new ExBase(nohatCashYenQ1) , null,
						new ExBase(hatMangoYenQ2)  , null,
						new ExBase(nohatCashYenQ2) , null,
						new ExBase(nohatMangoYenQ3), null,
						new ExBase(hatCashYenQ3)   , null,
						new ExBase(nohatMangoYenQ4), null,
						new ExBase(hatCashYenQ4)   , null,
				}),
		}));
		
		ExAlgeSet ret;
		
		ret = set0.oneValueProjection(valN);
		assertTrue(ret.isEmpty());
		ret = set1.oneValueProjection(valN);
		assertTrue(ret.isEmpty());
		ret = set2.oneValueProjection(valN);
		assertTrue(ret.equals(ansSetN));
		assertTrue(set0.isEmpty());
		checkSetSequence(set1, seqData1);
		checkSetSequence(set2, seqData2);
		
		ret = set0.oneValueProjection(val0);
		assertTrue(ret.isEmpty());
		ret = set1.oneValueProjection(val0);
		assertTrue(ret.equals(ansSet0_1));
		ret = set2.oneValueProjection(val0);
		assertTrue(ret.equals(ansSet0_2));
		assertTrue(set0.isEmpty());
		checkSetSequence(set1, seqData1);
		checkSetSequence(set2, seqData2);
		
		ret = set0.oneValueProjection(val1);
		assertTrue(ret.isEmpty());
		ret = set1.oneValueProjection(val1);
		assertTrue(ret.equals(ansSet1));
		ret = set2.oneValueProjection(val1);
		assertTrue(ret.equals(ansSet1));
		assertTrue(set0.isEmpty());
		checkSetSequence(set1, seqData1);
		checkSetSequence(set2, seqData2);
	}
	
	/**
	 * {@link exalge2.ExAlgeSet#valuesProjection(java.util.Collection)} のためのテスト・メソッド。
	 * @since 0.983
	 */
	public void testValueProjectionCollectionOfBigDecimal() {
		Object[][] seqData1 = new Object[][]{
				algeAppleData,
				algeOrangeData,
				algeBananaData,
				algeMelonData,
		};
		Object[][] seqData2 = new Object[][]{
				algeAppleData,
				algeOrangeDataWithNull,
				algeMelonDataWithNull,
				algeMangoDataWithNull,
		};
		ExAlgeSet set0 = new ExAlgeSet();
		ExAlgeSet set1 = new ExAlgeSet(Arrays.asList(new Exalge[]{
				new Exalge(algeAppleData),
				new Exalge(algeOrangeData),
				new Exalge(algeBananaData),
				new Exalge(algeMelonData)
		}));
		ExAlgeSet set2 = new ExAlgeSet(Arrays.asList(new Exalge[]{
				new Exalge(algeAppleData),
				new Exalge(algeOrangeDataWithNull),
				new Exalge(algeMelonDataWithNull),
				new Exalge(algeMangoDataWithNull)
		}));
		checkSetSequence(set1, seqData1);
		checkSetSequence(set2, seqData2);
		
		final BigDecimal valN = null;
		final BigDecimal val0 = BigDecimal.ZERO;
		final BigDecimal val1 = new BigDecimal(40);
		final Collection<? extends BigDecimal> c0 = Collections.<BigDecimal>emptyList();
		final Collection<? extends BigDecimal> c1 = Arrays.asList(val0, val1);
		final Collection<? extends BigDecimal> c2 = Arrays.asList(valN, val0);
		final Collection<? extends BigDecimal> c3 = Arrays.asList(new BigDecimal(1234), new BigDecimal("0.123"));
		
		ExAlgeSet ansSet1_1 = new ExAlgeSet(Arrays.asList(new Exalge[]{
				new Exalge(new Object[]{
						new ExBase(nohatAppleYenQ4), new BigDecimal(40),
						new ExBase(hatCashYenQ4)   , new BigDecimal(40),
				}),
				new Exalge(new Object[]{
						new ExBase(hatMelonYenQ2)  , new BigDecimal(0),
						new ExBase(nohatCashYenQ2) , new BigDecimal(0),
						new ExBase(nohatMelonYenQ3), new BigDecimal(0),
						new ExBase(hatCashYenQ3)   , new BigDecimal(0),
				}),
		}));
		ExAlgeSet ansSet1_2 = new ExAlgeSet(Arrays.asList(new Exalge[]{
				new Exalge(new Object[]{
						new ExBase(nohatAppleYenQ4), new BigDecimal(40),
						new ExBase(hatCashYenQ4)   , new BigDecimal(40),
				}),
				new Exalge(new Object[]{
						new ExBase(nohatMelonYenQ3), new BigDecimal(0),
						new ExBase(hatCashYenQ3)   , new BigDecimal(0),
				}),
		}));
		
		ExAlgeSet ansSet2_1 = new ExAlgeSet(Arrays.asList(new Exalge[]{
				new Exalge(new Object[]{
						new ExBase(hatMelonYenQ2)  , new BigDecimal(0),
						new ExBase(nohatCashYenQ2) , new BigDecimal(0),
						new ExBase(nohatMelonYenQ3), new BigDecimal(0),
						new ExBase(hatCashYenQ3)   , new BigDecimal(0),
				}),
		}));
		ExAlgeSet ansSet2_2 = new ExAlgeSet(Arrays.asList(new Exalge[]{
				new Exalge(new Object[]{
						new ExBase(nohatOrangeYenQ3), null,
						new ExBase(hatCashYenQ3)    , null,
						new ExBase(nohatOrangeYenQ4), null,
						new ExBase(hatCashYenQ4)    , null,
				}),
				new Exalge(new Object[]{
						new ExBase(hatMelonYenQ2)  , null,
						new ExBase(nohatCashYenQ2) , null,
						new ExBase(nohatMelonYenQ3), new BigDecimal(0),
						new ExBase(hatCashYenQ3)   , new BigDecimal(0),
						new ExBase(hatMelonYenQ4)  , null,
						new ExBase(nohatCashYenQ4) , null,
				}),
				new Exalge(new Object[]{
						new ExBase(hatMangoYenQ1)  , null,
						new ExBase(nohatCashYenQ1) , null,
						new ExBase(hatMangoYenQ2)  , null,
						new ExBase(nohatCashYenQ2) , null,
						new ExBase(nohatMangoYenQ3), null,
						new ExBase(hatCashYenQ3)   , null,
						new ExBase(nohatMangoYenQ4), null,
						new ExBase(hatCashYenQ4)   , null,
				}),
		}));
		
		ExAlgeSet ret;
		
		// null
		boolean coughtException;
		try {
			set2.valuesProjection((Collection<BigDecimal>)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		checkSetSequence(set2, seqData2);
		
		// collection empty
		ret = set0.valuesProjection(c0);
		assertTrue(ret.isEmpty());
		ret = set1.valuesProjection(c0);
		assertTrue(ret.isEmpty());
		ret = set2.valuesProjection(c0);
		assertTrue(ret.isEmpty());
		assertTrue(set0.isEmpty());
		checkSetSequence(set1, seqData1);
		checkSetSequence(set2, seqData2);
		
		// collection 1
		ret = set0.valuesProjection(c1);
		assertTrue(ret.isEmpty());
		ret = set1.valuesProjection(c1);
		assertTrue(ret.equals(ansSet1_1));
		ret = set2.valuesProjection(c1);
		assertTrue(ret.equals(ansSet1_2));
		assertTrue(set0.isEmpty());
		checkSetSequence(set1, seqData1);
		checkSetSequence(set2, seqData2);
		
		// collection 2
		ret = set0.valuesProjection(c2);
		assertTrue(ret.isEmpty());
		ret = set1.valuesProjection(c2);
		assertTrue(ret.equals(ansSet2_1));
		ret = set2.valuesProjection(c2);
		assertTrue(ret.equals(ansSet2_2));
		assertTrue(set0.isEmpty());
		checkSetSequence(set1, seqData1);
		checkSetSequence(set2, seqData2);
		
		// collection 3
		ret = set0.valuesProjection(c3);
		assertTrue(ret.isEmpty());
		ret = set1.valuesProjection(c3);
		assertTrue(ret.isEmpty());
		ret = set2.valuesProjection(c3);
		assertTrue(ret.isEmpty());
		assertTrue(set0.isEmpty());
		checkSetSequence(set1, seqData1);
		checkSetSequence(set2, seqData2);
	}
	
	/**
	 * {@link exalge2.ExAlgeSet#nullProjection()} のためのテスト・メソッド。
	 * @since 0.983
	 */
	public void testNullProjection() {
		Object[][] seqData1 = new Object[][]{
				algeAppleData,
				algeOrangeData,
				algeBananaData,
				algeMelonData,
		};
		Object[][] seqData2 = new Object[][]{
				algeAppleData,
				algeOrangeDataWithNull,
				algeMelonDataWithNull,
				algeMangoDataWithNull,
		};
		ExAlgeSet set0 = new ExAlgeSet();
		ExAlgeSet set1 = new ExAlgeSet(Arrays.asList(new Exalge[]{
				new Exalge(algeAppleData),
				new Exalge(algeOrangeData),
				new Exalge(algeBananaData),
				new Exalge(algeMelonData)
		}));
		ExAlgeSet set2 = new ExAlgeSet(Arrays.asList(new Exalge[]{
				new Exalge(algeAppleData),
				new Exalge(algeOrangeDataWithNull),
				new Exalge(algeMelonDataWithNull),
				new Exalge(algeMangoDataWithNull)
		}));
		checkSetSequence(set1, seqData1);
		checkSetSequence(set2, seqData2);
		
		ExAlgeSet ansSet = new ExAlgeSet(Arrays.asList(new Exalge[]{
				new Exalge(new Object[]{
						new ExBase(nohatOrangeYenQ3), null,
						new ExBase(hatCashYenQ3)    , null,
						new ExBase(nohatOrangeYenQ4), null,
						new ExBase(hatCashYenQ4)    , null,
				}),
				new Exalge(new Object[]{
						new ExBase(hatMelonYenQ2)  , null,
						new ExBase(nohatCashYenQ2) , null,
						new ExBase(hatMelonYenQ4)  , null,
						new ExBase(nohatCashYenQ4) , null,
				}),
				new Exalge(new Object[]{
						new ExBase(hatMangoYenQ1)  , null,
						new ExBase(nohatCashYenQ1) , null,
						new ExBase(hatMangoYenQ2)  , null,
						new ExBase(nohatCashYenQ2) , null,
						new ExBase(nohatMangoYenQ3), null,
						new ExBase(hatCashYenQ3)   , null,
						new ExBase(nohatMangoYenQ4), null,
						new ExBase(hatCashYenQ4)   , null,
				}),
		}));
		
		ExAlgeSet ret;
		
		ret = set0.nullProjection();
		assertTrue(ret.isEmpty());
		ret = set1.nullProjection();
		assertTrue(ret.isEmpty());
		ret = set2.nullProjection();
		assertTrue(ret.equals(ansSet));
		
		assertTrue(set0.isEmpty());
		checkSetSequence(set1, seqData1);
		checkSetSequence(set2, seqData2);
	}
	
	/**
	 * {@link exalge2.ExAlgeSet#nonullProjection()} のためのテスト・メソッド。
	 * @since 0.983
	 */
	public void testNoNullProjection() {
		Object[][] seqData1 = new Object[][]{
				algeAppleData,
				algeOrangeData,
				algeBananaData,
				algeMelonData,
		};
		Object[][] seqData2 = new Object[][]{
				algeAppleData,
				algeOrangeDataWithNull,
				algeMelonDataWithNull,
				algeMangoDataWithNull,
		};
		ExAlgeSet set0 = new ExAlgeSet();
		ExAlgeSet set1 = new ExAlgeSet(Arrays.asList(new Exalge[]{
				new Exalge(algeAppleData),
				new Exalge(algeOrangeData),
				new Exalge(algeBananaData),
				new Exalge(algeMelonData)
		}));
		ExAlgeSet set2 = new ExAlgeSet(Arrays.asList(new Exalge[]{
				new Exalge(algeAppleData),
				new Exalge(algeOrangeDataWithNull),
				new Exalge(algeMelonDataWithNull),
				new Exalge(algeMangoDataWithNull)
		}));
		checkSetSequence(set1, seqData1);
		checkSetSequence(set2, seqData2);
		
		ExAlgeSet ansSet = new ExAlgeSet(Arrays.asList(new Exalge[]{
				new Exalge(new Object[]{
						new ExBase(hatAppleYenQ1)  , new BigDecimal(10),
						new ExBase(nohatCashYenQ1) , new BigDecimal(10),
						new ExBase(hatAppleYenQ2)  , new BigDecimal(20),
						new ExBase(nohatCashYenQ2) , new BigDecimal(20),
						new ExBase(nohatAppleYenQ3), new BigDecimal(30),
						new ExBase(hatCashYenQ3)   , new BigDecimal(30),
						new ExBase(nohatAppleYenQ4), new BigDecimal(40),
						new ExBase(hatCashYenQ4)   , new BigDecimal(40),
				}),
				new Exalge(new Object[]{
						new ExBase(hatOrangeYenQ1)  , new BigDecimal(11),
						new ExBase(nohatCashYenQ1)  , new BigDecimal(11),
						new ExBase(hatOrangeYenQ2)  , new BigDecimal(22),
						new ExBase(nohatCashYenQ2)  , new BigDecimal(22),
				}),
				new Exalge(new Object[]{
						new ExBase(nohatMelonYenQ1), new BigDecimal(120),
						new ExBase(hatCashYenQ1)   , new BigDecimal(120),
						new ExBase(nohatMelonYenQ3), new BigDecimal(0),
						new ExBase(hatCashYenQ3)   , new BigDecimal(0),
				}),
		}));
		
		ExAlgeSet ret;
		
		ret = set0.nonullProjection();
		assertTrue(ret.isEmpty());
		ret = set1.nonullProjection();
		assertTrue(ret.equals(set1));
		ret = set2.nonullProjection();
		assertTrue(ret.equals(ansSet));
		
		assertTrue(set0.isEmpty());
		checkSetSequence(set1, seqData1);
		checkSetSequence(set2, seqData2);
	}
	
	/**
	 * {@link exalge2.ExAlgeSet#zeroProjection()} のためのテスト・メソッド。
	 * @since 0.984
	 */
	public void testZeroProjection() {
		ExAlgeSet set0 = new ExAlgeSet();
		ExAlgeSet set1 = new ExAlgeSet(Arrays.asList(new Exalge[]{
				ExalgeTest.makeAlge(new Object[]{
						new ExBase(hatAppleYenQ1)  , new BigDecimal(10),
						new ExBase(nohatCashYenQ1) , new BigDecimal(10),
						new ExBase(hatAppleYenQ2)  , new BigDecimal(20),
						new ExBase(nohatCashYenQ2) , new BigDecimal(20),
						new ExBase(nohatAppleYenQ3), new BigDecimal(30),
						new ExBase(hatCashYenQ3)   , new BigDecimal(30),
						new ExBase(nohatAppleYenQ4), new BigDecimal(40),
						new ExBase(hatCashYenQ4)   , new BigDecimal(40),
				}),
				ExalgeTest.makeAlge(new Object[]{
						new ExBase(nohatMelonYenQ1), new BigDecimal(120),
						new ExBase(hatCashYenQ1)   , new BigDecimal(120),
						new ExBase(hatMelonYenQ2)  , new BigDecimal(0),
						new ExBase(nohatCashYenQ2) , new BigDecimal(0),
						new ExBase(nohatMelonYenQ3), new BigDecimal(0),
						new ExBase(hatCashYenQ3)   , new BigDecimal(0),
						new ExBase(hatMelonYenQ4)  , new BigDecimal(240),
						new ExBase(nohatCashYenQ4) , new BigDecimal(240),
				}),
				ExalgeTest.makeAlge(new Object[]{
						new ExBase(hatMangoYenQ1)  , 0,
						new ExBase(nohatCashYenQ1) , 0,
						new ExBase(hatMangoYenQ2)  , 0,
						new ExBase(nohatCashYenQ2) , 0,
						new ExBase(nohatMangoYenQ3), 0,
						new ExBase(hatCashYenQ3)   , 0,
						new ExBase(nohatMangoYenQ4), 0,
						new ExBase(hatCashYenQ4)   , 0,
				}),
				ExalgeTest.makeAlge(new Object[]{
						new ExBase(hatMangoYenQ1)  , null,
						new ExBase(nohatCashYenQ1) , null,
						new ExBase(hatMangoYenQ2)  , null,
						new ExBase(nohatCashYenQ2) , null,
						new ExBase(nohatMangoYenQ3), null,
						new ExBase(hatCashYenQ3)   , null,
						new ExBase(nohatMangoYenQ4), null,
						new ExBase(hatCashYenQ4)   , null,
				}),
		}));
		
		ExAlgeSet ansSet = new ExAlgeSet(Arrays.asList(new Exalge[]{
				ExalgeTest.makeAlge(new Object[]{
						new ExBase(hatMelonYenQ2)  , new BigDecimal(0),
						new ExBase(nohatCashYenQ2) , new BigDecimal(0),
						new ExBase(nohatMelonYenQ3), new BigDecimal(0),
						new ExBase(hatCashYenQ3)   , new BigDecimal(0),
				}),
				ExalgeTest.makeAlge(new Object[]{
						new ExBase(hatMangoYenQ1)  , 0,
						new ExBase(nohatCashYenQ1) , 0,
						new ExBase(hatMangoYenQ2)  , 0,
						new ExBase(nohatCashYenQ2) , 0,
						new ExBase(nohatMangoYenQ3), 0,
						new ExBase(hatCashYenQ3)   , 0,
						new ExBase(nohatMangoYenQ4), 0,
						new ExBase(hatCashYenQ4)   , 0,
				}),
		}));
		
		ExAlgeSet ret;
		
		ret = set0.zeroProjection();
		assertTrue(ret.isEmpty());
		ret = set1.zeroProjection();
		assertEquals(ret, ansSet);
	}
	
	/**
	 * {@link exalge2.ExAlgeSet#notzeroProjection()} のためのテスト・メソッド。
	 * @since 0.984
	 */
	public void testNotZeroProjection() {
		ExAlgeSet set0 = new ExAlgeSet();
		ExAlgeSet set1 = new ExAlgeSet(Arrays.asList(new Exalge[]{
				ExalgeTest.makeAlge(new Object[]{
						new ExBase(hatAppleYenQ1)  , new BigDecimal(10),
						new ExBase(nohatCashYenQ1) , new BigDecimal(10),
						new ExBase(hatAppleYenQ2)  , new BigDecimal(20),
						new ExBase(nohatCashYenQ2) , new BigDecimal(20),
						new ExBase(nohatAppleYenQ3), new BigDecimal(30),
						new ExBase(hatCashYenQ3)   , new BigDecimal(30),
						new ExBase(nohatAppleYenQ4), new BigDecimal(40),
						new ExBase(hatCashYenQ4)   , new BigDecimal(40),
				}),
				ExalgeTest.makeAlge(new Object[]{
						new ExBase(nohatMelonYenQ1), new BigDecimal(120),
						new ExBase(hatCashYenQ1)   , new BigDecimal(120),
						new ExBase(hatMelonYenQ2)  , new BigDecimal(0),
						new ExBase(nohatCashYenQ2) , new BigDecimal(0),
						new ExBase(nohatMelonYenQ3), new BigDecimal(0),
						new ExBase(hatCashYenQ3)   , new BigDecimal(0),
						new ExBase(hatMelonYenQ4)  , new BigDecimal(240),
						new ExBase(nohatCashYenQ4) , new BigDecimal(240),
				}),
				ExalgeTest.makeAlge(new Object[]{
						new ExBase(hatMangoYenQ1)  , 0,
						new ExBase(nohatCashYenQ1) , 0,
						new ExBase(hatMangoYenQ2)  , 0,
						new ExBase(nohatCashYenQ2) , 0,
						new ExBase(nohatMangoYenQ3), 0,
						new ExBase(hatCashYenQ3)   , 0,
						new ExBase(nohatMangoYenQ4), 0,
						new ExBase(hatCashYenQ4)   , 0,
				}),
				ExalgeTest.makeAlge(new Object[]{
						new ExBase(hatMangoYenQ1)  , null,
						new ExBase(nohatCashYenQ1) , null,
						new ExBase(hatMangoYenQ2)  , null,
						new ExBase(nohatCashYenQ2) , null,
						new ExBase(nohatMangoYenQ3), null,
						new ExBase(hatCashYenQ3)   , null,
						new ExBase(nohatMangoYenQ4), null,
						new ExBase(hatCashYenQ4)   , null,
				}),
		}));
		
		ExAlgeSet ansSet = new ExAlgeSet(Arrays.asList(new Exalge[]{
				ExalgeTest.makeAlge(new Object[]{
						new ExBase(hatAppleYenQ1)  , new BigDecimal(10),
						new ExBase(nohatCashYenQ1) , new BigDecimal(10),
						new ExBase(hatAppleYenQ2)  , new BigDecimal(20),
						new ExBase(nohatCashYenQ2) , new BigDecimal(20),
						new ExBase(nohatAppleYenQ3), new BigDecimal(30),
						new ExBase(hatCashYenQ3)   , new BigDecimal(30),
						new ExBase(nohatAppleYenQ4), new BigDecimal(40),
						new ExBase(hatCashYenQ4)   , new BigDecimal(40),
				}),
				ExalgeTest.makeAlge(new Object[]{
						new ExBase(nohatMelonYenQ1), new BigDecimal(120),
						new ExBase(hatCashYenQ1)   , new BigDecimal(120),
						new ExBase(hatMelonYenQ4)  , new BigDecimal(240),
						new ExBase(nohatCashYenQ4) , new BigDecimal(240),
				}),
				ExalgeTest.makeAlge(new Object[]{
						new ExBase(hatMangoYenQ1)  , null,
						new ExBase(nohatCashYenQ1) , null,
						new ExBase(hatMangoYenQ2)  , null,
						new ExBase(nohatCashYenQ2) , null,
						new ExBase(nohatMangoYenQ3), null,
						new ExBase(hatCashYenQ3)   , null,
						new ExBase(nohatMangoYenQ4), null,
						new ExBase(hatCashYenQ4)   , null,
				}),
		}));
		
		ExAlgeSet ret;
		
		ret = set0.notzeroProjection();
		assertTrue(ret.isEmpty());
		ret = set1.notzeroProjection();
		assertEquals(ret, ansSet);
	}
	
	//----------------------------------------------------------
	//
	//----------------------------------------------------------
	
	static protected void checkSetSequence(ExAlgeSet targetSet, Object[][] dataSequence) {
		assertEquals(targetSet.size(), dataSequence.length);
		int idx = 0;
		Iterator<Exalge> it = targetSet.iterator();
		while (it.hasNext()) {
			assertTrue(idx < dataSequence.length);
			Exalge alge = it.next();
			Object[] seq = dataSequence[idx++];
			ExAlgeSetIOTest.checkElemSequence(alge, seq);
		}
	}

}
