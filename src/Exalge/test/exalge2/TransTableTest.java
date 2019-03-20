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
import java.util.LinkedHashMap;
import java.util.List;

import junit.framework.TestCase;

public class TransTableTest extends TestCase
{
	static private final String from1 = "りんご-*-*-*-*";
	static private final String from2 = "みかん-*-*-*-*";
	static private final String from3 = "ばなな-*-*-*-*";
	static private final String from4 = "メロン-*-*-*-*";
	static private final String from5 = "マンゴー-*-*-*-*";
	static private final String from6 = "まぐろ-*-*-*-*";
	static private final String from7 = "かつお-*-*-*-*";

	static private final String pattern1 = "name11-*-*-*-*";
	static private final String pattern2 = "name21-*-unit-*-*";
	static private final String pattern3 = "name31-*-unit-time-*";
	static private final String pattern4 = "name41-*-unit-time-subject";
	static private final String pattern5 = "name51-HAT-unit-time-subject";
	
	static private final ExBasePattern[] fromPatterns = new ExBasePattern[]{
		new ExBasePattern(from1),
		new ExBasePattern(from2),
		new ExBasePattern(from3),
		new ExBasePattern(from4),
		new ExBasePattern(from5),
		new ExBasePattern(from6),
		new ExBasePattern(from7),
	};
	
	static private final ExBasePattern[] toPatterns = new ExBasePattern[]{
		new ExBasePattern(pattern1),
		new ExBasePattern(pattern2),
		new ExBasePattern(pattern3),
		new ExBasePattern(pattern4),
		new ExBasePattern(pattern5),
	};
	
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
	 * {@link exalge2.TransTable#TransTable()} のためのテスト・メソッド。
	 */
	public void testTransTable() {
		TransTable table = new TransTable();
		assertTrue(table.getTransMap().isEmpty());
	}

	/**
	 * {@link exalge2.TransTable#isEmpty()} のためのテスト・メソッド。
	 */
	public void testIsEmpty() {
		TransTable table = new TransTable();
		assertTrue(table.getTransMap().isEmpty());
		assertTrue(table.isEmpty());
		
		table.getTransMap().put(new ExBasePattern("*"), new ExBasePattern("*"));
		assertFalse(table.getTransMap().isEmpty());
		assertFalse(table.isEmpty());
		
		table.getTransMap().clear();
		assertTrue(table.getTransMap().isEmpty());
		assertTrue(table.isEmpty());
	}

	/**
	 * {@link exalge2.TransTable#size()} のためのテスト・メソッド。
	 */
	public void testSize() {
		TransTable table = new TransTable();
		assertEquals(0, table.getTransMap().size());
		assertEquals(0, table.size());
		
		table.getTransMap().put(new ExBasePattern("*"), new ExBasePattern("*"));
		assertEquals(1, table.getTransMap().size());
		assertEquals(1, table.size());
		
		table.getTransMap().put(new ExBasePattern("2"), new ExBasePattern("*"));
		assertEquals(2, table.getTransMap().size());
		assertEquals(2, table.size());
		
		table.getTransMap().put(new ExBasePattern("*"), new ExBasePattern("*"));
		assertEquals(2, table.getTransMap().size());
		assertEquals(2, table.size());
	}

	/**
	 * {@link exalge2.TransTable#clear()} のためのテスト・メソッド。
	 */
	public void testClear() {
		TransTable table = new TransTable();
		assertTrue(table.isEmpty());
		
		table.getTransMap().put(new ExBasePattern("name1"), new ExBasePattern("*"));
		table.getTransMap().put(new ExBasePattern("name2"), new ExBasePattern("*"));
		table.getTransMap().put(new ExBasePattern("name3"), new ExBasePattern("*"));
		assertFalse(table.isEmpty());
		
		table.clear();
		assertTrue(table.isEmpty());
	}

	/**
	 * {@link exalge2.TransTable#containsTransFrom(exalge2.ExBasePattern)} のためのテスト・メソッド。
	 */
	public void testContainsTransFrom() {
		TransTable table = new TransTable();
		assertTrue(table.isEmpty());
		
		assertFalse(table.containsTransFrom(null));
		for (ExBasePattern fp : fromPatterns) {
			assertFalse(table.containsTransFrom(fp));
		}
		
		int maxLen = toPatterns.length - 1;
		for (int i = 0; i < maxLen; i++) {
			table.put(fromPatterns[i], toPatterns[i]);
		}
		assertFalse(table.isEmpty());
		assertEquals(maxLen, table.getTransMap().size());
		
		for (int i = 0; i < toPatterns.length; i++) {
			if (i < maxLen) {
				assertTrue(table.containsTransFrom(fromPatterns[i]));
			} else {
				assertFalse(table.containsTransFrom(fromPatterns[i]));
			}
		}
	}

	/**
	 * {@link exalge2.TransTable#containsTransTo(exalge2.ExBasePattern)} のためのテスト・メソッド。
	 */
	public void testContainsTransTo() {
		TransTable table = new TransTable();
		assertTrue(table.isEmpty());
		
		assertFalse(table.containsTransFrom(null));
		for (ExBasePattern fp : toPatterns) {
			assertFalse(table.containsTransTo(fp));
		}
		
		int maxLen = toPatterns.length - 1;
		for (int i = 0; i < maxLen; i++) {
			table.put(fromPatterns[i], toPatterns[i]);
		}
		assertFalse(table.isEmpty());
		assertEquals(maxLen, table.getTransMap().size());
		
		for (int i = 0; i < toPatterns.length; i++) {
			if (i < maxLen) {
				assertTrue(table.containsTransTo(toPatterns[i]));
			} else {
				assertFalse(table.containsTransTo(toPatterns[i]));
			}
		}
	}

	/**
	 * {@link exalge2.TransTable#transFromPatterns()} のためのテスト・メソッド。
	 */
	public void testTransFromPatterns() {
		TransTable table = new TransTable();
		assertTrue(table.isEmpty());
		assertTrue(table.transFromPatterns().isEmpty());
		
		ExBasePatternSet set = new ExBasePatternSet();
		int maxLen = toPatterns.length - 1;
		for (int i = 0; i < maxLen; i++) {
			table.put(fromPatterns[i], toPatterns[i]);
			set.add(fromPatterns[i]);
		}
		assertFalse(table.isEmpty());
		assertEquals(maxLen, table.getTransMap().size());
		assertEquals(maxLen, set.size());
		
		assertEquals(set, table.transFromPatterns());
	}

	/**
	 * {@link exalge2.TransTable#transToPatterns()} のためのテスト・メソッド。
	 */
	public void testTransToPatterns() {
		TransTable table = new TransTable();
		assertTrue(table.isEmpty());
		assertTrue(table.transToPatterns().isEmpty());
		
		List<ExBasePattern> list = new ArrayList<ExBasePattern>();
		int maxLen = toPatterns.length - 1;
		for (int i = 0; i < maxLen; i++) {
			table.put(fromPatterns[i], toPatterns[i]);
			list.add(toPatterns[i]);
		}
		assertFalse(table.isEmpty());
		assertEquals(maxLen, table.getTransMap().size());
		assertEquals(maxLen, list.size());

		assertEquals(list.size(), table.transToPatterns().size());
		assertTrue(table.transToPatterns().containsAll(list));
	}

	/**
	 * {@link exalge2.TransTable#getTransTo(exalge2.ExBasePattern)} のためのテスト・メソッド。
	 */
	public void testGetTransTo() {
		TransTable table = new TransTable();
		assertTrue(table.isEmpty());
		assertTrue(table.getTransTo(null) == null);
		for (ExBasePattern tp : toPatterns) {
			assertTrue(table.getTransTo(tp) == null);
		}
		
		int maxLen = toPatterns.length - 1;
		for (int i = 0; i < maxLen; i++) {
			table.put(fromPatterns[i], toPatterns[i]);
		}
		assertFalse(table.isEmpty());
		assertEquals(maxLen, table.getTransMap().size());
		
		assertTrue(table.getTransTo(null) == null);
		for (int i = 0; i < toPatterns.length; i++) {
			if (i < maxLen) {
				assertEquals(toPatterns[i], table.getTransTo(fromPatterns[i]));
			} else {
				assertTrue(table.getTransTo(fromPatterns[i]) == null);
			}
		}
	}

	/**
	 * {@link exalge2.TransTable#put(exalge2.ExBasePattern, exalge2.ExBasePattern)} のためのテスト・メソッド。
	 */
	public void testPut() {
		TransTable table = new TransTable();
		assertTrue(table.isEmpty());
		
		boolean coughtException;
		try {
			table.put(null, toPatterns[0]);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			table.put(fromPatterns[0], null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			table.put(null, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		
		int maxLen = toPatterns.length - 1;
		for (int i = 0; i < maxLen; i++) {
			table.put(fromPatterns[i], toPatterns[i]);
		}
		assertFalse(table.isEmpty());
		assertEquals(maxLen, table.getTransMap().size());
		try {
			table.put(null, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		try {
			table.put(null, toPatterns[0]);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			table.put(fromPatterns[0], null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		assertTrue(coughtException);
		
		for (int i = 0; i < toPatterns.length; i++) {
			if (i < maxLen) {
				assertEquals(toPatterns[i], table.getTransMap().get(fromPatterns[i]));
			} else {
				assertTrue(table.getTransMap().get(fromPatterns[i]) == null);
			}
		}
	}

	/**
	 * {@link exalge2.TransTable#remove(exalge2.ExBasePattern)} のためのテスト・メソッド。
	 */
	public void testRemove() {
		TransTable table = new TransTable();
		assertTrue(table.isEmpty());
		
		table.put(fromPatterns[0], toPatterns[0]);
		table.put(fromPatterns[1], toPatterns[1]);
		table.put(fromPatterns[2], toPatterns[2]);
		table.put(fromPatterns[3], toPatterns[3]);
		assertFalse(table.isEmpty());
		assertEquals(4, table.getTransMap().size());
		
		table.remove(null);
		assertEquals(4, table.size());
		table.remove(fromPatterns[4]);
		assertEquals(4, table.size());
		table.remove(fromPatterns[3]);
		assertEquals(3, table.size());
		table.remove(fromPatterns[2]);
		assertEquals(2, table.size());
		table.remove(fromPatterns[1]);
		assertEquals(1, table.size());
		assertEquals(toPatterns[0], table.getTransTo(fromPatterns[0]));
		table.remove(fromPatterns[0]);
		assertTrue(table.isEmpty());
	}

	/**
	 * {@link exalge2.TransTable#machesTransFrom(exalge2.ExBase)} のためのテスト・メソッド。
	 */
	public void testMachesTransFrom() {
		ExBase base1 = new ExBase(ExalgeTest.hatAppleYen);
		ExBase base2 = new ExBase(ExalgeTest.nohatOrangeNum);
		ExBase base3 = new ExBase(ExalgeTest.hatMangoNum);
		ExBase base4 = new ExBase(ExalgeTest.nohatCashYen);
		
		TransTable table = new TransTable();
		assertTrue(table.isEmpty());
		assertTrue(table.machesTransFrom(null) == null);
		assertTrue(table.machesTransFrom(base1) == null);
		assertTrue(table.machesTransFrom(base2) == null);
		assertTrue(table.machesTransFrom(base3) == null);
		assertTrue(table.machesTransFrom(base4) == null);
		
		for (int i = 0; i < toPatterns.length; i++) {
			table.put(fromPatterns[i], toPatterns[i]);
		}
		
		assertFalse(table.isEmpty());
		assertTrue(table.machesTransFrom(null) == null);
		assertEquals(table.machesTransFrom(base1), fromPatterns[0]);
		assertEquals(table.machesTransFrom(base2), fromPatterns[1]);
		assertEquals(table.machesTransFrom(base3), fromPatterns[4]);
		assertTrue(table.machesTransFrom(base4) == null);
	}

	/**
	 * {@link exalge2.TransTable#transform(exalge2.ExBase)} のためのテスト・メソッド。
	 */
	public void testTransformExBase() {
		ExBase base1 = new ExBase(ExalgeTest.hatAppleYen);
		ExBase base2 = new ExBase(ExalgeTest.nohatOrangeNum);
		ExBase base3 = new ExBase(ExalgeTest.hatMangoNum);
		ExBase base4 = new ExBase(ExalgeTest.nohatCashYen);
		
		ExBase ans1 = new ExBase("name11-HAT-円-#-#");
		ExBase ans2 = new ExBase("name21-NO_HAT-unit-#-#");
		ExBase ans3 = new ExBase("name51-HAT-unit-time-subject");
		ExBase ans4 = new ExBase(ExalgeTest.nohatCashYen);
		
		TransTable table = new TransTable();
		assertTrue(table.isEmpty());
		boolean coughtException;
		try {
			table.transform((ExBase)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		assertEquals(base1, table.transform(base1));
		assertEquals(base2, table.transform(base2));
		assertEquals(base3, table.transform(base3));
		assertEquals(base4, table.transform(base4));
		
		for (int i = 0; i < toPatterns.length; i++) {
			table.put(fromPatterns[i], toPatterns[i]);
		}
		assertFalse(table.isEmpty());
		
		try {
			table.transform((ExBase)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		assertEquals(ans1, table.transform(base1));
		assertEquals(ans2, table.transform(base2));
		assertEquals(ans3, table.transform(base3));
		assertEquals(ans4, table.transform(base4));
	}

	/**
	 * {@link exalge2.TransTable#transform(exalge2.ExBaseSet)} のためのテスト・メソッド。
	 */
	public void testTransformExBaseSet() {
		ExBase base1 = new ExBase(ExalgeTest.hatAppleYen);
		ExBase base2 = new ExBase(ExalgeTest.nohatOrangeNum);
		ExBase base3 = new ExBase(ExalgeTest.hatMangoNum);
		ExBase base4 = new ExBase(ExalgeTest.nohatCashYen);
		
		ExBase ans1 = new ExBase("name11-HAT-円-#-#");
		ExBase ans2 = new ExBase("name21-NO_HAT-unit-#-#");
		ExBase ans3 = new ExBase("name51-HAT-unit-time-subject");
		ExBase ans4 = new ExBase(ExalgeTest.nohatCashYen);
		
		ExBaseSet testSet = new ExBaseSet(Arrays.asList(base1, base2, base3, base4));
		ExBaseSet ansSet  = new ExBaseSet(Arrays.asList(ans1, ans2, ans3, ans4));
		
		TransTable table = new TransTable();
		assertTrue(table.isEmpty());
		boolean coughtException;
		try {
			table.transform((ExBaseSet)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		assertEquals(new ExBaseSet(), table.transform(new ExBaseSet()));
		assertEquals(testSet, table.transform(testSet));
		
		for (int i = 0; i < toPatterns.length; i++) {
			table.put(fromPatterns[i], toPatterns[i]);
		}
		
		assertFalse(table.isEmpty());
		try {
			table.transform((ExBaseSet)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		assertEquals(new ExBaseSet(), table.transform(new ExBaseSet()));
		assertEquals(ansSet, table.transform(testSet));
	}

	/**
	 * {@link exalge2.TransTable#transfer(exalge2.ExBase)} のためのテスト・メソッド。
	 */
	public void testTransfer() {
		ExBase base1 = new ExBase(ExalgeTest.hatAppleYen);
		ExBase base2 = new ExBase(ExalgeTest.nohatOrangeNum);
		ExBase base3 = new ExBase(ExalgeTest.hatMangoNum);
		ExBase base4 = new ExBase(ExalgeTest.nohatCashYen);
		
		ExBase ans1 = new ExBase("name11-HAT-円-#-#");
		ExBase ans2 = new ExBase("name21-NO_HAT-unit-#-#");
		ExBase ans3 = new ExBase("name51-HAT-unit-time-subject");
		
		TransTable table = new TransTable();
		assertTrue(table.isEmpty());
		assertTrue(table.transfer(null) == null);
		assertTrue(table.transfer(base1) == null);
		assertTrue(table.transfer(base2) == null);
		assertTrue(table.transfer(base3) == null);
		assertTrue(table.transfer(base4) == null);
		
		for (int i = 0; i < toPatterns.length; i++) {
			table.put(fromPatterns[i], toPatterns[i]);
		}
		
		assertFalse(table.isEmpty());
		assertTrue(table.transfer(null) == null);
		assertEquals(table.transfer(base1), ans1);
		assertEquals(table.transfer(base2), ans2);
		assertEquals(table.transfer(base3), ans3);
		assertTrue(table.transfer(base4) == null);
	}

	/**
	 * {@link exalge2.TransTable#clone()} のためのテスト・メソッド。
	 */
	public void testClone() {
		TransTable t1 = new TransTable();
		assertTrue(t1.isEmpty());
		TransTable t2 = (TransTable)t1.clone();
		assertTrue(t2.isEmpty());
		assertTrue(t1 != t2);
		t1.put(fromPatterns[0], toPatterns[0]);
		assertFalse(t1.isEmpty());
		assertTrue(t2.isEmpty());
		
		t1 = new TransTable();
		for (int i = 0; i < toPatterns.length; i++) {
			t1.put(fromPatterns[i], toPatterns[i]);
		}
		assertEquals(toPatterns.length, t1.size());
		t2 = (TransTable)t1.clone();
		assertEquals(toPatterns.length, t2.size());
		assertTrue(t1 != t2);
		t1.clear();
		assertTrue(t1.isEmpty());
		assertFalse(t2.isEmpty());
	}

	/**
	 * {@link exalge2.TransTable#hashCode()} のためのテスト・メソッド。
	 */
	public void testHashCode() {
		TransTable table = new TransTable();
		assertTrue(table.isEmpty());
		assertEquals(0, table.hashCode());
		
		LinkedHashMap<ExBasePattern, ExBasePattern> map = new LinkedHashMap<ExBasePattern,ExBasePattern>();
		for (int i = 0; i < toPatterns.length; i++) {
			table.put(fromPatterns[i], toPatterns[i]);
			map.put(fromPatterns[i], toPatterns[i]);
		}
		assertEquals(toPatterns.length, table.size());
		assertEquals(toPatterns.length, map.size());
		assertEquals(map.hashCode(), table.hashCode());
	}

	/**
	 * {@link exalge2.TransTable#equals(java.lang.Object)} のためのテスト・メソッド。
	 */
	public void testEqualsObject() {
		TransTable t1 = new TransTable();
		TransTable t2 = new TransTable();
		assertTrue(t1.isEmpty());
		assertTrue(t2.isEmpty());
		assertEquals(t1, t2);
		assertEquals(t2, t1);
		assertEquals(t1.hashCode(), t2.hashCode());
		
		for (int i = 0; i < toPatterns.length; i++) {
			t1.put(fromPatterns[i], toPatterns[i]);
			t2.put(fromPatterns[i], toPatterns[(toPatterns.length-1-i)]);
		}
		assertEquals(toPatterns.length, t1.size());
		assertEquals(toPatterns.length, t2.size());
		assertFalse(t1.equals(t2));
		assertFalse(t2.equals(t1));
		
		for (int i = 0; i < toPatterns.length; i++) {
			t2.put(fromPatterns[i], toPatterns[i]);
		}
		assertEquals(toPatterns.length, t1.size());
		assertEquals(toPatterns.length, t2.size());
		assertEquals(t1, t2);
		assertEquals(t2, t1);
		assertEquals(t1.hashCode(), t2.hashCode());
	}

	/**
	 * {@link exalge2.TransTable#toString()} のためのテスト・メソッド。
	 */
	public void testToString() {
		TransTable table = new TransTable();
		assertTrue(table.isEmpty());
		assertEquals("[]", table.toString());
		
		StringBuilder sb = new StringBuilder();
		sb.append("[\n");
		for (int i = 0; i < toPatterns.length; i++) {
			table.put(fromPatterns[i], toPatterns[i]);
			sb.append(fromPatterns[i]);
			sb.append(" -> ");
			sb.append(toPatterns[i]);
			sb.append("\n");
		}
		sb.append("]");
		assertEquals(toPatterns.length, table.size());
		assertEquals(sb.toString(), table.toString());
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
