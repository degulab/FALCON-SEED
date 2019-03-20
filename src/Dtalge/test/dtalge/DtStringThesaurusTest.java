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
 *  Copyright 2007-2011  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
/*
 * @(#)DtStringThesaurusTest.java	0.30	2011/03/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtStringThesaurusTest.java	0.20	2010/02/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtStringThesaurusTest.java	0.10	2008/09/02
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dtalge.util.Validations;

import junit.framework.TestCase;

/**
 * <code>DtStringThesaurus</code> のユニットテスト。
 * このテストケースでは、ファイル入出力のテストは含まない。
 * 
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class DtStringThesaurusTest extends TestCase
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final String[] thesPairs = {
		"A1", "A1B1",
		"A1", "A1B2",
		"A2", "A2B1",
		"A2", "A2B2",
		"A2", "A2B3",
		"A3", "A3B1",
		"A1B1", "A1B1C1",
		"A1B1", "A1B1C2",
		"A1B2", "A1B2C1",
		"A2B1", "A2B1C1",
		"A2B1", "A2B1C2",
		"A2B3", "A2B3C1",
		"A2B3", "A2B3C2",
		"A2B3", "A2B3C3",
		"A2B3C3", "A2B3C3D1",
		"A2B3C3", "A2B3C3D2",
		"A2B3C3", "A2B3C3D3",
		"A2B3C3D3", "A2B3C3D3E1",
		"A2B3C3D3E1", "A2B3C3D3E1F1",
	};
	
	static private final String[] thesPairs2 = {
		"A1", "A1B1",
		"A1", "A1B2",
		"A2", "A2B1",
		"A2", "A2B2",
		"A2", "A2B3",
		"A3", "A3B1",
		"A1B1", "A1B1C1",
		"A1B1", "A1B1C2",
		"A1B2", "A1B2C1",
		"A2B1", "A2B1C1",
		"A2B1", "A2B1C2",
		"A2B3", "A2B3C1",
		"A2B3", "A2B3C2",
		"A2B3", "A2B3C3",
		"A1B1C1", "A1B1C1D1",
		"A1B1C1", "A1B1C1D2",
		"A1B1C1D1", "A1B1C1D1E1",
		"A2B3C3", "A2B3C3D1",
		"A2B3C3", "A2B3C3D2",
		"A2B3C3", "A2B3C3D3",
		"A2B3C3D3", "A2B3C3D3E1",
		"A2B3C3D3E1", "A2B3C3D3E1F1",
		"A2B3C3D3E1F1", "A2B3C3D3E1F1G1",
	};
	
	static private final String[] thesMPPairs = {
		"N1", "N2",
		"N1", "N3",
		"N2", "N4",
		"N2", "N5",
		"N4", "N6",
		"N5", "N6",
		"N3", "N7",
		"N6", "N7",
	};

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static protected void putAllToThesaurus(DtStringThesaurus thes, String...pairs) {
		Validations.validNotNull(thes);
		Validations.validNotNull(pairs);
		Validations.validArgument((pairs.length % 2) == 0);
		
		for (int i = 0; i < pairs.length; i+=2) {
			thes.put(pairs[i], pairs[i+1]);
		}
	}
	
	static protected DtStringThesaurus newThesaurus(String...pairs) {
		DtStringThesaurus thes = new DtStringThesaurus();
		putAllToThesaurus(thes, pairs);
		return thes;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	//------------------------------------------------------------
	// Test cases
	//------------------------------------------------------------

	/**
	 * {@link dtalge.DtStringThesaurus#DtStringThesaurus()} のためのテスト・メソッド。
	 */
	public void testDtStringThesaurus() {
		DtStringThesaurus thes = new DtStringThesaurus();
		assertNotNull(thes.relationMap);
		assertNotNull(thes.childrenMap);
		assertTrue(thes.relationMap.isEmpty());
		assertTrue(thes.childrenMap.isEmpty());
	}

	/**
	 * {@link dtalge.DtStringThesaurus#put(java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testPut() {
		//--- null
		DtStringThesaurus thes0 = new DtStringThesaurus();
		try {
			thes0.put(null, null);
			fail("must be throw IllegalArgumentException.");
		} catch (IllegalArgumentException ex) { assertTrue(true); }
		try {
			thes0.put("parent", null);
			fail("must be throw IllegalArgumentException.");
		} catch (IllegalArgumentException ex) { assertTrue(true); }
		try {
			thes0.put(null, "child");
			fail("must be throw IllegalArgumentException.");
		} catch (IllegalArgumentException ex) { assertTrue(true); }
		//--- empty
		try {
			thes0.put("", "");
			fail("must be throw IllegalArgumentException.");
		} catch (IllegalArgumentException ex) { assertTrue(true); }
		try {
			thes0.put("parent", "");
			fail("must be throw IllegalArgumentException.");
		} catch (IllegalArgumentException ex) { assertTrue(true); }
		try {
			thes0.put("", "child");
			fail("must be throw IllegalArgumentException.");
		} catch (IllegalArgumentException ex) { assertTrue(true); }
		//--- same word
		try {
			thes0.put("parent", "parent");
			fail("must be throw IllegalArgumentException.");
		} catch (IllegalArgumentException ex) { assertTrue(true); }
		try {
			thes0.put("child", "child");
			fail("must be throw IllegalArgumentException.");
		} catch (IllegalArgumentException ex) { assertTrue(true); }
		//--- Circular reference
		thes0.put("parent", "child");
		try {
			thes0.put("child", "parent");
			fail("must be throw IllegalArgumentException.");
		} catch (IllegalArgumentException ex) { assertTrue(true); }
		
		//--- create thesaurus
		DtStringThesaurus thes1 = new DtStringThesaurus();
		for (int i = 0; i < thesPairs.length; i+=2) {
			String parent = thesPairs[i];
			String child  = thesPairs[i+1];
			assertTrue(String.valueOf(i), thes1.put(parent, child));
			assertTrue(String.valueOf(i), thes1.relationMap.containsKey(child));
			//assertEquals(String.valueOf(i), parent, thes1.relationMap.get(child));
			Set<String> ps = thes1.relationMap.get(child);
			assertNotNull(String.valueOf(i), ps);
			assertTrue(String.valueOf(i), ps.contains(parent));
			assertTrue(String.valueOf(i), thes1.childrenMap.containsKey(parent));
			Set<String> cs = thes1.childrenMap.get(parent);
			assertNotNull(String.valueOf(i), cs);
			assertTrue(String.valueOf(i), cs.contains(child));
		}
		
		//--- put already exist
		String parent = "A2B3C3";
		String child  = "A2B3C3D3";
		assertFalse(thes1.put(parent, child));
		assertTrue(thes1.relationMap.containsKey(child));
		//assertEquals(parent, thes1.relationMap.get(child));
		Set<String> ps = thes1.relationMap.get(child);
		assertNotNull(ps);
		assertTrue(ps.contains(parent));
		assertTrue(thes1.childrenMap.containsKey(parent));
		Set<String> cs = thes1.childrenMap.get(parent);
		assertNotNull(cs);
		assertTrue(cs.contains(child));
		
		//--- append new parent for has children
		String newParent = "Z1";
		assertTrue(thes1.put(newParent, child));
		assertTrue(thes1.relationMap.containsKey(child));
		//assertFalse(parent.equals(thes1.relationMap.get(child)));
		//assertTrue (newParent.equals(thes1.relationMap.get(child)));
		ps = thes1.relationMap.get(child);
		assertNotNull(ps);
		assertTrue(ps.contains(parent));
		assertTrue(ps.contains(newParent));
		assertTrue(thes1.childrenMap.containsKey(parent));
		cs = thes1.childrenMap.get(parent);
		assertNotNull(cs);
		//assertFalse(cs.contains(child));
		assertTrue(cs.contains(child));
		assertTrue(thes1.childrenMap.containsKey(newParent));
		cs = thes1.childrenMap.get(newParent);
		assertNotNull(cs);
		assertTrue(cs.contains(child));
		
		//--- append new parent for has only one child
		parent = "A1B2";
		child = "A1B2C1";
		assertTrue(thes1.relationMap.containsKey(child));
		//assertEquals(parent, thes1.relationMap.get(child));
		ps = thes1.relationMap.get(child);
		assertNotNull(ps);
		assertTrue(ps.contains(parent));
		assertTrue(thes1.childrenMap.containsKey(parent));
		cs = thes1.childrenMap.get(parent);
		assertNotNull(cs);
		assertTrue(cs.contains(child));
		newParent = "Z2";
		assertTrue(thes1.put(newParent, child));
		assertTrue(thes1.relationMap.containsKey(child));
		//assertFalse(parent.equals(thes1.relationMap.get(child)));
		ps = thes1.relationMap.get(child);
		assertNotNull(ps);
		assertTrue(ps.contains(parent));
		cs = thes1.childrenMap.get(parent);
		assertNotNull(cs);
		assertTrue(cs.contains(child));
		//assertTrue (newParent.equals(thes1.relationMap.get(child)));
		assertTrue(ps.contains(newParent));
		//assertFalse(thes1.childrenMap.containsKey(parent));
		assertTrue(thes1.childrenMap.containsKey(parent));
		assertTrue(thes1.childrenMap.containsKey(newParent));
		cs = thes1.childrenMap.get(newParent);
		assertNotNull(cs);
		assertTrue(cs.contains(child));
		
		//--- create thesaurus
		parent = "A1B1C1";
		child  = "A1";
		//--- Circular reference
		try {
			thes1.put(parent, child);
			fail("must be throw IllegalArgumentException.");
		} catch (IllegalArgumentException ex) { assertTrue(true); }
	}

	/**
	 * {@link dtalge.DtStringThesaurus#clear()} のためのテスト・メソッド。
	 */
	public void testClear() {
		DtStringThesaurus thes = new DtStringThesaurus();
		thes.put("parent", "child");
		assertFalse(thes.relationMap.isEmpty());
		assertFalse(thes.childrenMap.isEmpty());
		
		thes.clear();
		
		assertTrue(thes.relationMap.isEmpty());
		assertTrue(thes.childrenMap.isEmpty());
	}

	/**
	 * {@link dtalge.DtStringThesaurus#isEmpty()} のためのテスト・メソッド。
	 */
	public void testIsEmpty() {
		DtStringThesaurus thes = new DtStringThesaurus();
		assertTrue(thes.relationMap.isEmpty());
		assertTrue(thes.childrenMap.isEmpty());
		assertTrue(thes.isEmpty());
		
		thes.put("parent", "child");
		assertFalse(thes.relationMap.isEmpty());
		assertFalse(thes.childrenMap.isEmpty());
		assertFalse(thes.isEmpty());
		
		thes.clear();
		assertTrue(thes.relationMap.isEmpty());
		assertTrue(thes.childrenMap.isEmpty());
		assertTrue(thes.isEmpty());
	}

	/**
	 * {@link dtalge.DtStringThesaurus#size()} のためのテスト・メソッド。
	 */
	public void testSize() {
		DtStringThesaurus thes = new DtStringThesaurus();
		assertTrue(thes.relationMap.isEmpty());
		assertTrue(thes.childrenMap.isEmpty());
		assertEquals(0, thes.size());
		
		thes.put("parent", "child");
		assertFalse(thes.relationMap.isEmpty());
		assertFalse(thes.childrenMap.isEmpty());
		assertEquals(1, thes.size());
		
		thes.put("parent2", "child2");
		assertFalse(thes.relationMap.isEmpty());
		assertFalse(thes.childrenMap.isEmpty());
		assertEquals(2, thes.size());
		
		thes.put("parent", "child3");
		assertFalse(thes.relationMap.isEmpty());
		assertFalse(thes.childrenMap.isEmpty());
		assertEquals(3, thes.size());
		
		thes.clear();
		assertTrue(thes.relationMap.isEmpty());
		assertTrue(thes.childrenMap.isEmpty());
		assertEquals(0, thes.size());
	}

	/**
	 * {@link dtalge.DtStringThesaurus#hasChild(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testHasChild() {
		DtStringThesaurus thes = new DtStringThesaurus();
		assertFalse(thes.hasChild(null));
		assertFalse(thes.hasChild("parent"));
		
		thes.put("parent", "child");
		thes.put("parent2", "child2");
		thes.put("parent", "child3");
		
		assertFalse(thes.hasChild(null));
		assertFalse(thes.hasChild("child"));
		assertFalse(thes.hasChild("child2"));
		assertFalse(thes.hasChild("child3"));
		assertTrue(thes.hasChild("parent"));
		assertTrue(thes.hasChild("parent2"));
		
		thes.clear();
		assertFalse(thes.hasChild(null));
		assertFalse(thes.hasChild("child"));
		assertFalse(thes.hasChild("child2"));
		assertFalse(thes.hasChild("child3"));
		assertFalse(thes.hasChild("parent"));
		assertFalse(thes.hasChild("parent2"));
	}

	/**
	 * {@link dtalge.DtStringThesaurus#hasParent(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testHasParent() {
		DtStringThesaurus thes = new DtStringThesaurus();
		assertFalse(thes.hasParent(null));
		assertFalse(thes.hasParent("child"));
		
		thes.put("parent", "child");
		thes.put("parent2", "child2");
		thes.put("parent", "child3");
		
		assertFalse(thes.hasParent(null));
		assertTrue(thes.hasParent("child"));
		assertTrue(thes.hasParent("child2"));
		assertTrue(thes.hasParent("child3"));
		assertFalse(thes.hasParent("parent"));
		assertFalse(thes.hasParent("parent2"));
		
		thes.clear();
		assertFalse(thes.hasParent(null));
		assertFalse(thes.hasParent("child"));
		assertFalse(thes.hasParent("child2"));
		assertFalse(thes.hasParent("child3"));
		assertFalse(thes.hasParent("parent"));
		assertFalse(thes.hasParent("parent2"));
	}

	/**
	 * {@link dtalge.DtStringThesaurus#contains(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testContains() {
		DtStringThesaurus thes = new DtStringThesaurus();
		assertFalse(thes.contains(null));
		assertFalse(thes.contains("parent"));
		assertFalse(thes.contains("child"));
		
		thes.put("parent", "child");
		thes.put("parent2", "child2");
		thes.put("parent", "child3");
		
		assertFalse(thes.hasParent(null));
		assertTrue(thes.contains("child"));
		assertTrue(thes.contains("child2"));
		assertTrue(thes.contains("child3"));
		assertTrue(thes.contains("parent"));
		assertTrue(thes.contains("parent2"));
		assertFalse(thes.contains("parent3"));
		assertFalse(thes.contains("child4"));
		
		thes.clear();
		assertFalse(thes.contains(null));
		assertFalse(thes.contains("child"));
		assertFalse(thes.contains("child2"));
		assertFalse(thes.contains("child3"));
		assertFalse(thes.contains("parent"));
		assertFalse(thes.contains("parent2"));
		assertFalse(thes.contains("parent3"));
		assertFalse(thes.contains("child4"));
	}

	/**
	 * {@link dtalge.DtStringThesaurus#containsAny(java.lang.String[])} のためのテスト・メソッド。
	 */
	public void testContainsAnyStringArray() {
		DtStringThesaurus thes1 = newThesaurus(thesPairs);
		DtStringThesaurus thes2 = newThesaurus(thesPairs2);
		assertEquals((thesPairs.length/2), thes1.size());
		assertEquals((thesPairs2.length/2), thes2.size());
		
		assertFalse(thes1.containsAny((String[])null));
		assertFalse(thes2.containsAny((String[])null));
		assertFalse(thes1.containsAny("hoge", null));
		assertFalse(thes2.containsAny("hoge", null));
		
		assertTrue(thes1.containsAny(thesPairs));
		assertTrue(thes1.containsAny(thesPairs2));
		assertTrue(thes2.containsAny(thesPairs));
		assertTrue(thes2.containsAny(thesPairs2));
	}

	/**
	 * {@link dtalge.DtStringThesaurus#containsAny(java.util.Collection)} のためのテスト・メソッド。
	 */
	public void testContainsAnyCollectionOfQextendsString() {
		DtStringThesaurus thes1 = newThesaurus(thesPairs);
		DtStringThesaurus thes2 = newThesaurus(thesPairs2);
		assertEquals((thesPairs.length/2), thes1.size());
		assertEquals((thesPairs2.length/2), thes2.size());
		
		assertFalse(thes1.containsAny((Collection<String>)null));
		assertFalse(thes2.containsAny((Collection<String>)null));
		assertFalse(thes1.containsAny(Arrays.asList("hoge", null)));
		assertFalse(thes2.containsAny(Arrays.asList("hoge", null)));
		
		assertTrue(thes1.containsAny(Arrays.asList(thesPairs)));
		assertTrue(thes1.containsAny(Arrays.asList(thesPairs2)));
		assertTrue(thes2.containsAny(Arrays.asList(thesPairs)));
		assertTrue(thes2.containsAny(Arrays.asList(thesPairs2)));
	}

	/**
	 * {@link dtalge.DtStringThesaurus#containsAll(java.lang.String[])} のためのテスト・メソッド。
	 */
	public void testContainsAllStringArray() {
		DtStringThesaurus thes1 = newThesaurus(thesPairs);
		DtStringThesaurus thes2 = newThesaurus(thesPairs2);
		assertEquals((thesPairs.length/2), thes1.size());
		assertEquals((thesPairs2.length/2), thes2.size());
		
		assertFalse(thes1.containsAll((String[])null));
		assertFalse(thes2.containsAll((String[])null));
		assertFalse(thes1.containsAll("hoge", null));
		assertFalse(thes2.containsAll("hoge", null));
		
		assertTrue(thes1.containsAll(thesPairs));
		assertFalse(thes1.containsAll(thesPairs2));
		assertTrue(thes2.containsAll(thesPairs));
		assertTrue(thes2.containsAll(thesPairs2));
	}

	/**
	 * {@link dtalge.DtStringThesaurus#containsAll(java.util.Collection)} のためのテスト・メソッド。
	 */
	public void testContainsAllCollectionOfQextendsString() {
		DtStringThesaurus thes1 = newThesaurus(thesPairs);
		DtStringThesaurus thes2 = newThesaurus(thesPairs2);
		assertEquals((thesPairs.length/2), thes1.size());
		assertEquals((thesPairs2.length/2), thes2.size());
		
		assertFalse(thes1.containsAll((Collection<String>)null));
		assertFalse(thes2.containsAll((Collection<String>)null));
		assertFalse(thes1.containsAll(Arrays.asList("hoge", null)));
		assertFalse(thes2.containsAll(Arrays.asList("hoge", null)));
		
		assertTrue(thes1.containsAll(Arrays.asList(thesPairs)));
		assertFalse(thes1.containsAll(Arrays.asList(thesPairs2)));
		assertTrue(thes2.containsAll(Arrays.asList(thesPairs)));
		assertTrue(thes2.containsAll(Arrays.asList(thesPairs2)));
	}

	/**
	 * {@link dtalge.DtStringThesaurus#containsRelation(java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testContainsRelation() {
		final String[] thesLineage = {
			"A1", "A1B2C1",
			"A2", "A2B3C3D3E1F1",
		};
		final String[] thesNoPairs = {
			null, null,
			null, "A1B1",
			"A1", null,
			"hoge1", "A1B1",
			"A1", "hoge2",
			"hoge1", "hoge2",
			"A1B1C1", "A1B1C1D1",
			"A1B1C1", "A1B1C1D2",
			"A1B1C1D1", "A1B1C1D1E1",
		};
		
		DtStringThesaurus thes1 = newThesaurus(thesPairs);
		assertEquals((thesPairs.length/2), thes1.size());
		
		//--- exist relation
		for (int i = 0; i < thesPairs.length; i+=2) {
			String parent = thesPairs[i];
			String child  = thesPairs[i+1];
			assertTrue(thes1.containsRelation(parent, child));
			//--- not exist patterns
			assertFalse(thes1.containsRelation(child, parent));
			assertFalse(thes1.containsRelation(parent, parent));
			assertFalse(thes1.containsRelation(child, child));
		}
		
		//--- lineage pattern
		for (int i = 0; i < thesLineage.length; i+=2) {
			String anc = thesLineage[i];
			String des = thesLineage[i+1];
			assertFalse(thes1.containsRelation(anc, des));
			assertFalse(thes1.containsRelation(des, anc));
		}
		
		//--- no relations
		for (int i = 0; i < thesNoPairs.length; i+=2) {
			String parent = thesNoPairs[i];
			String child  = thesNoPairs[i+1];
			assertFalse(thes1.containsRelation(parent, child));
			assertFalse(thes1.containsRelation(child, parent));
		}
	}

	/**
	 * {@link dtalge.DtStringThesaurus#isComparable(java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testIsComparable() {
		final String[] comparables = {
			"A1", "A1B2C1",
			"A2", "A2B3C3D3E1F1",
		};
		final String[] noComparables = {
			null, null,
			null, "A1B1",
			"A1", null,
			"hoge1", "A1B1",
			"A1", "hoge2",
			"hoge1", "hoge2",
			"A1", "A2",
			"A1B2C1", "A2B3C3D3E1F1",
		};
		
		DtStringThesaurus thes1 = newThesaurus(thesPairs);
		assertEquals((thesPairs.length/2), thes1.size());
		
		//--- exist relation
		for (int i = 0; i < thesPairs.length; i+=2) {
			String parent = thesPairs[i];
			String child  = thesPairs[i+1];
			assertTrue(thes1.isComparable(parent, child));
			assertTrue(thes1.isComparable(child, parent));
			assertFalse(thes1.isComparable(parent, parent));
			assertFalse(thes1.isComparable(child, child));
		}
		
		//--- comparables
		for (int i = 0; i < comparables.length; i+=2) {
			String w1 = comparables[i];
			String w2 = comparables[i+1];
			assertTrue(thes1.isComparable(w1, w2));
			assertTrue(thes1.isComparable(w2, w1));
		}
		
		//--- no comparables
		for (int i = 0; i < noComparables.length; i+=2) {
			String w1 = noComparables[i];
			String w2 = noComparables[i+1];
			assertFalse(thes1.isComparable(w1, w2));
			assertFalse(thes1.isComparable(w2, w1));
			assertFalse(thes1.isComparable(w1, w1));
			assertFalse(thes1.isComparable(w2, w2));
		}
		
		// for Multi-Parents
		final String[] mpcomparables = {
			"N1", "N7",
			"N2", "N6",
			"N4", "N7",
			"N4", "N1",
			"N5", "N7",
			"N5", "N1",
		};
		final String[] nompcomparables = {
			"N2", "N3",
			"N4", "N5",
			"N4", "N3",
			"N5", "N3",
			"N6", "N3",
		};
		DtStringThesaurus thes4 = newThesaurus(thesMPPairs);
		assertEquals((thesMPPairs.length/2), thes4.size());
		//--- exist relation
		for (int i = 0; i < thesMPPairs.length; i+=2) {
			String parent = thesMPPairs[i];
			String child  = thesMPPairs[i+1];
			assertTrue(thes4.isComparable(parent, child));
			assertTrue(thes4.isComparable(child, parent));
			assertFalse(thes4.isComparable(parent, parent));
			assertFalse(thes4.isComparable(child, child));
		}
		//--- comparables
		for (int i = 0; i < mpcomparables.length; i+=2) {
			String w1 = mpcomparables[i];
			String w2 = mpcomparables[i+1];
			assertTrue(thes4.isComparable(w1, w2));
			assertTrue(thes4.isComparable(w2, w1));
		}
		//--- no comparables
		for (int i = 0; i < nompcomparables.length; i+=2) {
			String w1 = nompcomparables[i];
			String w2 = nompcomparables[i+1];
			assertFalse(thes4.isComparable(w1, w2));
			assertFalse(thes4.isComparable(w2, w1));
			assertFalse(thes4.isComparable(w1, w1));
			assertFalse(thes4.isComparable(w2, w2));
		}
	}

	/**
	 * {@link dtalge.DtStringThesaurus#compareTo(java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testCompareTo() {
		final String[] comparables = {
				"A1", "A1B2C1",
				"A2", "A2B3C3D3E1F1",
		};
		final String[] noComparables = {
				null, null,
				null, "A1B1",
				"A1", null,
				"hoge1", "A1B1",
				"A1", "hoge2",
				"hoge1", "hoge2",
				"A1", "A2",
				"A1B2C1", "A2B3C3D3E1F1",
		};

		DtStringThesaurus thes1 = newThesaurus(thesPairs);
		assertEquals((thesPairs.length/2), thes1.size());

		//--- exist relation
		for (int i = 0; i < thesPairs.length; i+=2) {
			String parent = thesPairs[i];
			String child  = thesPairs[i+1];
			assertTrue(thes1.compareTo(parent, child) > 0);
			assertTrue(thes1.compareTo(child, parent) < 0);
			assertTrue(thes1.compareTo(parent, parent) == 0);
			assertTrue(thes1.compareTo(child, child) == 0);
		}

		//--- comparables
		for (int i = 0; i < comparables.length; i+=2) {
			String w1 = comparables[i];
			String w2 = comparables[i+1];
			assertTrue(thes1.compareTo(w1, w2) > 0);
			assertTrue(thes1.compareTo(w2, w1) < 0);
		}

		//--- no comparables
		for (int i = 0; i < noComparables.length; i+=2) {
			String w1 = noComparables[i];
			String w2 = noComparables[i+1];
			assertTrue(thes1.compareTo(w1, w2) == 0);
			assertTrue(thes1.compareTo(w2, w1) == 0);
			assertTrue(thes1.compareTo(w1, w1) == 0);
			assertTrue(thes1.compareTo(w2, w2) == 0);
		}
		
		// for Multi-Parents
		final String[] mpcomparables = {
				"N1", "N4",
				"N1", "N5",
				"N1", "N7",
				"N2", "N6",
				"N4", "N7",
				"N5", "N7",
		};
		final String[] nompcomparables = {
				"N2", "N3",
				"N4", "N5",
				"N4", "N3",
				"N5", "N3",
				"N6", "N3",
		};
		DtStringThesaurus thes4 = newThesaurus(thesMPPairs);
		assertEquals((thesMPPairs.length/2), thes4.size());
		//--- exist relation
		for (int i = 0; i < thesMPPairs.length; i+=2) {
			String parent = thesMPPairs[i];
			String child  = thesMPPairs[i+1];
			assertTrue(thes4.compareTo(parent, child) > 0);
			assertTrue(thes4.compareTo(child, parent) < 0);
			assertTrue(thes4.compareTo(parent, parent) == 0);
			assertTrue(thes4.compareTo(child, child) == 0);
		}
		//--- comparables
		for (int i = 0; i < mpcomparables.length; i+=2) {
			String w1 = mpcomparables[i];
			String w2 = mpcomparables[i+1];
			assertTrue(thes4.compareTo(w1, w2) > 0);
			assertTrue(thes4.compareTo(w2, w1) < 0);
		}
		//--- no comparables
		for (int i = 0; i < nompcomparables.length; i+=2) {
			String w1 = nompcomparables[i];
			String w2 = nompcomparables[i+1];
			assertTrue(thes4.compareTo(w1, w2) == 0);
			assertTrue(thes4.compareTo(w2, w1) == 0);
			assertTrue(thes4.compareTo(w1, w1) == 0);
			assertTrue(thes4.compareTo(w2, w2) == 0);
		}
	}

	/**
	 * {@link dtalge.DtStringThesaurus#isClassificationSet(java.lang.String[])} のためのテスト・メソッド。
	 */
	public void testIsClassificationSetStringArray() {
		final String[] classifications = {
				"A1B1",
				"A2B1",
				"A2B3C3D3",
		};
		final String[] noClassifications = {
				null, "",
				"A1",
				"B1",
				"C1",
		};

		DtStringThesaurus thes1 = newThesaurus(thesPairs);
		assertEquals((thesPairs.length/2), thes1.size());
		
		//--- classifications
		assertTrue(thes1.contains("A1B1C1"));
		assertTrue(thes1.isClassificationSet("A1B1C1"));
		assertTrue(thes1.isClassificationSet(classifications));
		
		//--- no classifications
		assertFalse(thes1.contains("hoge"));
		assertFalse(thes1.isClassificationSet("hoge"));
		assertFalse(thes1.isClassificationSet((String[])null));
		assertFalse(thes1.isClassificationSet(noClassifications));
		assertTrue(thes1.isComparable("A1", "A1B1C1"));
		assertFalse(thes1.isClassificationSet("A1", "A1B1C1"));
		
		// for Multi-Parents
		final String[] mpclassifications = {
				"N3",
				"N4",
				"N5",
		};
		final String[] nompclassifications = {
				"N2",
				"N3",
				"N6",
		};
		DtStringThesaurus thes4 = newThesaurus(thesMPPairs);
		assertEquals((thesMPPairs.length/2), thes4.size());
		//--- classifications
		assertTrue(thes4.contains("N6"));
		assertTrue(thes4.isClassificationSet("N6"));
		assertTrue(thes4.isClassificationSet(mpclassifications));
		//--- no classifications
		assertFalse(thes4.contains("hoge"));
		assertFalse(thes4.isClassificationSet("hoge"));
		assertFalse(thes4.isClassificationSet((String[])null));
		assertFalse(thes4.isClassificationSet(nompclassifications));
		assertTrue(thes4.isComparable("N1", "N7"));
		assertFalse(thes4.isClassificationSet("N1", "N7"));
	}

	/**
	 * {@link dtalge.DtStringThesaurus#isClassificationSet(java.util.Collection)} のためのテスト・メソッド。
	 */
	public void testIsClassificationSetCollectionOfQextendsString() {
		final String[] classifications = {
				"A1B1",
				"A2B1",
				"A2B3C3D3",
		};
		final String[] noClassifications = {
				null, "",
				"A1",
				"B1",
				"C1",
		};

		DtStringThesaurus thes1 = newThesaurus(thesPairs);
		assertEquals((thesPairs.length/2), thes1.size());
		
		//--- classifications
		assertTrue(thes1.contains("A1B1C1"));
		assertTrue(thes1.isClassificationSet(Arrays.asList("A1B1C1")));
		assertTrue(thes1.isClassificationSet(Arrays.asList(classifications)));
		
		//--- no classifications
		assertFalse(thes1.contains("hoge"));
		assertFalse(thes1.isClassificationSet(Arrays.asList("hoge")));
		assertFalse(thes1.isClassificationSet((Collection<String>)null));
		assertFalse(thes1.isClassificationSet(Arrays.asList(noClassifications)));
		assertTrue(thes1.isComparable("A1", "A1B1C1"));
		assertFalse(thes1.isClassificationSet(Arrays.asList("A1", "A1B1C1")));
		
		// for Multi-Parents
		final String[] mpclassifications = {
				"N3",
				"N4",
				"N5",
		};
		final String[] nompclassifications = {
				"N2",
				"N3",
				"N6",
		};
		DtStringThesaurus thes4 = newThesaurus(thesMPPairs);
		assertEquals((thesMPPairs.length/2), thes4.size());
		//--- classifications
		assertTrue(thes4.contains("N6"));
		assertTrue(thes4.isClassificationSet(Arrays.asList("N6")));
		assertTrue(thes4.isClassificationSet(Arrays.asList(mpclassifications)));
		//--- no classifications
		assertFalse(thes4.contains("hoge"));
		assertFalse(thes4.isClassificationSet(Arrays.asList("hoge")));
		assertFalse(thes4.isClassificationSet((Collection<String>)null));
		assertFalse(thes4.isClassificationSet(Arrays.asList(nompclassifications)));
		assertTrue(thes4.isComparable("N1", "N7"));
		assertFalse(thes4.isClassificationSet(Arrays.asList("N1", "N7")));
	}

	/**
	 * {@link dtalge.DtStringThesaurus#remove(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testRemoveString() {
		DtStringThesaurus thes0 = new DtStringThesaurus();
		assertFalse(thes0.remove(null));
		assertFalse(thes0.remove(""));
		assertFalse(thes0.remove("A1B1"));
		
		DtStringThesaurus thes1 = newThesaurus(thesPairs);
		assertEquals((thesPairs.length/2), thes1.size());
		assertFalse(thes1.remove(null));
		assertFalse(thes1.remove(""));
		String word = "A1B1C1";
		assertTrue(thes1.contains(word));
		assertTrue(thes1.remove(word));
		assertFalse(thes1.contains(word));
		for (int i = 0; i < thesPairs.length; i+=2) {
			String parent = thesPairs[i];
			String child  = thesPairs[i+1];
			if (word.equals(parent) || word.equals(child))
				assertFalse(thes1.containsRelation(parent, child));
			else
				assertTrue(thes1.containsRelation(parent, child));
		}
		
		DtStringThesaurus thes2 = new DtStringThesaurus();
		thes2.put("hoge1", "hoge2");
		assertTrue(thes2.remove("hoge1"));
		assertTrue(thes2.relationMap.isEmpty());
		assertTrue(thes2.childrenMap.isEmpty());
		thes2.put("hoge1", "hoge2");
		assertTrue(thes2.remove("hoge2"));
		assertTrue(thes2.relationMap.isEmpty());
		assertTrue(thes2.childrenMap.isEmpty());

		// for Multi-parents
		DtStringThesaurus thes4 = newThesaurus(thesMPPairs);
		assertEquals((thesMPPairs.length/2), thes4.size());
		for (int i = 0; i < thesMPPairs.length; i+=2) {
			String parent = thesMPPairs[i];
			String child  = thesMPPairs[i+1];
			assertTrue(thes4.containsRelation(parent, child));
		}
		assertTrue(thes4.remove("N3"));
		assertTrue(thes4.remove("N6"));
		assertTrue(thes4.remove("N2"));
		assertTrue(thes4.relationMap.isEmpty());
		assertTrue(thes4.childrenMap.isEmpty());
	}

	/**
	 * {@link dtalge.DtStringThesaurus#remove(java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testRemoveStringString() {
		final String[] noPairs = {
				null, null,
				null, "A2B3",
				"A3", null,
				"", "",
				"", "A1B1C2",
				"A1B2", "",
				"hoge1", "hoge2",
				"hoge1", "A3B1",
				"A1B1", "hoge2",
		};
		
		String targetParent = "A2B3C3";
		String targetChild  = "A2B3C3D1";
		
		DtStringThesaurus thes0 = new DtStringThesaurus();
		assertTrue(thes0.isEmpty());
		assertFalse(thes0.remove(targetParent, targetChild));
		for (int i = 0; i < noPairs.length; i+=2) {
			String parent = noPairs[i];
			String child  = noPairs[i+1];
			assertFalse(String.valueOf(parent) + " > " + String.valueOf(child), thes0.remove(parent, child));
		}
		
		DtStringThesaurus thes1 = newThesaurus(thesPairs);
		assertEquals((thesPairs.length/2), thes1.size());
		for (int i = 0; i < noPairs.length; i+=2) {
			String parent = noPairs[i];
			String child  = noPairs[i+1];
			assertFalse(String.valueOf(parent) + " > " + String.valueOf(child), thes1.remove(parent, child));
		}
		assertEquals((thesPairs.length/2), thes1.size());
		assertFalse(thes1.remove(targetChild, targetParent));
		assertEquals((thesPairs.length/2), thes1.size());
		assertTrue(thes1.remove(targetParent, targetChild));
		assertEquals(((thesPairs.length/2)-1), thes1.size());
		for (int i = 0; i < thesPairs.length; i+=2) {
			String parent = thesPairs[i];
			String child  = thesPairs[i+1];
			if (targetParent.equals(parent) && targetChild.equals(child))
				assertFalse(thes1.containsRelation(parent, child));
			else
				assertTrue(thes1.containsRelation(parent, child));
		}
		
		DtStringThesaurus thes2 = new DtStringThesaurus();
		targetParent = "hoge1";
		targetChild  = "hoge2";
		thes2.put(targetParent, targetChild);
		assertEquals(1, thes2.size());
		assertTrue(thes2.remove(targetParent, targetChild));
		assertTrue(thes2.relationMap.isEmpty());
		assertTrue(thes2.childrenMap.isEmpty());
		
		DtStringThesaurus thes3 = newThesaurus(thesPairs2);
		assertEquals((thesPairs2.length/2), thes3.size());
		for (int i = 0; i < thesPairs2.length; i+=2) {
			String parent = thesPairs2[i];
			String child  = thesPairs2[i+1];
			assertTrue(thes3.remove(parent, child));
		}
		assertTrue(thes3.relationMap.isEmpty());
		assertTrue(thes3.childrenMap.isEmpty());

		// for Multi-parents
		DtStringThesaurus thes4 = newThesaurus(thesMPPairs);
		assertEquals((thesMPPairs.length/2), thes4.size());
		for (int i = 0; i < thesMPPairs.length; i+=2) {
			String parent = thesMPPairs[i];
			String child  = thesMPPairs[i+1];
			assertTrue(thes4.containsRelation(parent, child));
			assertTrue(thes4.remove(parent, child));
		}
		assertTrue(thes4.relationMap.isEmpty());
		assertTrue(thes4.childrenMap.isEmpty());
	}

	/**
	 * {@link dtalge.DtStringThesaurus#hashCode()} のためのテスト・メソッド。
	 */
	public void testHashCode() {
		DtStringThesaurus thes0 = new DtStringThesaurus();
		assertTrue(thes0.isEmpty());
		assertEquals(0, thes0.hashCode());
		
		int h = 0;
		for (int i = 0; i < thesPairs.length; i+=2) {
			String parent = thesPairs[i];
			String child  = thesPairs[i+1];
			h += (child.hashCode() ^ parent.hashCode());
		}
		DtStringThesaurus thes1 = newThesaurus(thesPairs);
		assertEquals((thesPairs.length/2), thes1.size());
		assertEquals(h, thes1.hashCode());
		
		// for multi-parents
		DtStringThesaurus thes3a = newThesaurus(thesMPPairs);
		DtStringThesaurus thes3b = newThesaurus(thesMPPairs);
		assertNotSame(thes3a, thes3b);
		assertEquals((thesMPPairs.length/2), thes3a.size());
		assertEquals((thesMPPairs.length/2), thes3b.size());
		assertEquals(thes3a.hashCode(), thes3b.hashCode());
	}

	/**
	 * {@link dtalge.DtStringThesaurus#equals(java.lang.Object)} のためのテスト・メソッド。
	 */
	public void testEqualsObject() {
		DtStringThesaurus thes0a = new DtStringThesaurus();
		DtStringThesaurus thes0b = new DtStringThesaurus();
		DtStringThesaurus thes1a = newThesaurus(thesPairs);
		DtStringThesaurus thes1b = newThesaurus(thesPairs);
		DtStringThesaurus thes2a = newThesaurus(thesPairs2);
		DtStringThesaurus thes2b = newThesaurus(thesPairs2);
		DtStringThesaurus thes3a = newThesaurus(thesMPPairs);
		DtStringThesaurus thes3b = newThesaurus(thesMPPairs);
		
		assertNotSame(thes0a, thes0b);
		assertNotSame(thes1a, thes1b);
		assertNotSame(thes2a, thes2b);
		assertNotSame(thes3a, thes3b);
		assertTrue(thes0a.isEmpty());
		assertTrue(thes0b.isEmpty());
		assertEquals((thesPairs.length/2), thes1a.size());
		assertEquals((thesPairs.length/2), thes1b.size());
		assertEquals((thesPairs2.length/2), thes2a.size());
		assertEquals((thesPairs2.length/2), thes2b.size());
		assertEquals((thesMPPairs.length/2), thes3a.size());
		assertEquals((thesMPPairs.length/2), thes3b.size());

		assertTrue (thes0a.equals(thes0b));
		assertFalse(thes0a.equals(thes1b));
		assertFalse(thes0a.equals(thes2b));
		assertFalse(thes0a.equals(thes3b));
		assertFalse(thes1a.equals(thes0b));
		assertTrue (thes1a.equals(thes1b));
		assertFalse(thes1a.equals(thes2b));
		assertFalse(thes1a.equals(thes3b));
		assertFalse(thes2a.equals(thes0b));
		assertFalse(thes2a.equals(thes1b));
		assertTrue (thes2a.equals(thes2b));
		assertFalse(thes2a.equals(thes3b));
		assertFalse(thes3a.equals(thes0b));
		assertFalse(thes3a.equals(thes1b));
		assertFalse(thes3a.equals(thes2b));
		assertTrue (thes3a.equals(thes3b));

		assertTrue (thes0b.equals(thes0a));
		assertFalse(thes0b.equals(thes1a));
		assertFalse(thes0b.equals(thes2a));
		assertFalse(thes0b.equals(thes3a));
		assertFalse(thes1b.equals(thes0a));
		assertTrue (thes1b.equals(thes1a));
		assertFalse(thes1b.equals(thes2a));
		assertFalse(thes1b.equals(thes3a));
		assertFalse(thes2b.equals(thes0a));
		assertFalse(thes2b.equals(thes1a));
		assertTrue (thes2b.equals(thes2a));
		assertFalse(thes2b.equals(thes3a));
		assertFalse(thes3b.equals(thes0a));
		assertFalse(thes3b.equals(thes1a));
		assertFalse(thes3b.equals(thes2a));
		assertTrue (thes3b.equals(thes3a));
	}

	/**
	 * {@link dtalge.DtStringThesaurus#toString()} のためのテスト・メソッド。
	 */
	public void testToString() {
		DtStringThesaurus thes0 = new DtStringThesaurus();
		assertEquals("{}", thes0.toString());
		
		DtStringThesaurus thes1 = newThesaurus("hoge1", "hoge2");
		assertEquals(thes1.toString(), "{hoge1>[hoge2]}", thes1.toString());
		
		thes1.put("hoge1", "hoge3");
		assertEquals(thes1.toString(), "{hoge1>[hoge2, hoge3]}", thes1.toString());
	}

	/**
	 * {@link dtalge.DtStringThesaurus#getParents(String)} のためのテスト・メソッド。
	 * @since 0.30
	 */
	public void testGetParentsString() {
		String[][] parents = {
				{ null, },
				{ "N1", },
				{ "N2", "N1", },
				{ "N3", "N1", },
				{ "N4", "N2", },
				{ "N5", "N2", },
				{ "N6", "N4", "N5", },
				{ "N7", "N3", "N6", },
		};
		
		DtStringThesaurus thes = newThesaurus(thesMPPairs);
		assertEquals((thesMPPairs.length/2), thes.size());
		
		for (int i = 0; i < parents.length; i++) {
			String word = parents[i][0];
			Set<String> set = new HashSet<String>();
			for (int j = 1; j < parents[i].length; j++) {
				set.add(parents[i][j]);
			}
			String[] pa = thes.getParents(word);
			assertEquals(set.size(), pa.length);
			if (!set.isEmpty()) {
				assertEquals(set, new HashSet<String>(Arrays.asList(pa)));
			}
		}
	}

	/**
	 * {@link dtalge.DtStringThesaurus#getChildren(String)} のためのテスト・メソッド。
	 * @since 0.30
	 */
	public void testGetChildrenString() {
		String[][] children = {
				{ null, },
				{ "N7", },
				{ "N6", "N7" },
				{ "N5", "N6" },
				{ "N4", "N6" },
				{ "N3", "N7" },
				{ "N2", "N4", "N5" },
				{ "N1", "N2", "N3" },
		};
		
		DtStringThesaurus thes = newThesaurus(thesMPPairs);
		assertEquals((thesMPPairs.length/2), thes.size());
		
		for (int i = 0; i < children.length; i++) {
			String word = children[i][0];
			Set<String> set = new HashSet<String>();
			for (int j = 1; j < children[i].length; j++) {
				set.add(children[i][j]);
			}
			String[] pa = thes.getChildren(word);
			assertEquals(set.size(), pa.length);
			if (!set.isEmpty()) {
				assertEquals(set, new HashSet<String>(Arrays.asList(pa)));
			}
		}
	}

	/**
	 * {@link dtalge.DtStringThesaurus#getThesaurusParents(String)} のためのテスト・メソッド。
	 * @since 0.30
	 */
	public void testGetThesaurusParentsString() {
		String[][] parents = {
				{ null, },
				{ "N1", },
				{ "N2", "N1", },
				{ "N3", "N1", },
				{ "N4", "N2", },
				{ "N5", "N2", },
				{ "N6", "N4", "N5", },
				{ "N7", "N3", "N6", },
		};
		
		DtStringThesaurus thes = newThesaurus(thesMPPairs);
		assertEquals((thesMPPairs.length/2), thes.size());
		
		for (int i = 0; i < parents.length; i++) {
			String word = parents[i][0];
			List<String> anslist = new ArrayList<String>();
			for (int j = 1; j < parents[i].length; j++) {
				anslist.add(parents[i][j]);
			}
			List<String> retlist = thes.getThesaurusParents(word);
			assertEquals(retlist, anslist);
		}
	}

	/**
	 * {@link dtalge.DtStringThesaurus#getThesaurusChildren(String)} のためのテスト・メソッド。
	 * @since 0.30
	 */
	public void testGetThesaurusChildrenString() {
		String[][] children = {
				{ null, },
				{ "N7", },
				{ "N6", "N7" },
				{ "N5", "N6" },
				{ "N4", "N6" },
				{ "N3", "N7" },
				{ "N2", "N4", "N5" },
				{ "N1", "N2", "N3" },
		};
		
		DtStringThesaurus thes = newThesaurus(thesMPPairs);
		assertEquals((thesMPPairs.length/2), thes.size());
		
		for (int i = 0; i < children.length; i++) {
			String word = children[i][0];
			List<String> anslist = new ArrayList<String>();
			for (int j = 1; j < children[i].length; j++) {
				anslist.add(children[i][j]);
			}
			List<String> retlist = thes.getThesaurusChildren(word);
			assertEquals(retlist, anslist);
		}
	}
}
