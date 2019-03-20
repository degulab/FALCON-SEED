/**
 * 
 */
package exalge2;

import java.util.Arrays;
import java.util.Set;

import junit.framework.TestCase;
import exalge2.ExTransfer.ExBasePatternIndexSet;

/**
 * @author ishizuka
 *
 */
public class ExBasePatternIndexSetTest extends TestCase
{
	static private final ExBasePattern patANBCD = new ExBasePattern("Apache-NO_HAT-Bash-Clone-Delete");
	static private final ExBasePattern patBHCDA = new ExBasePattern("Bash-HAT-Clone-Delete-Apache");
	static private final ExBasePattern patCWDAB = new ExBasePattern("Clone-*-Delete-Apache-Bash");
	static private final ExBasePattern patWNWWW = new ExBasePattern("Ap*he-NO_HAT-B*h-C*e-De*te");
	static private final ExBasePattern patWHWWW = new ExBasePattern("Ap*he-HAT-B*h-C*e-De*te");
	static private final ExBasePattern patWWWWW = new ExBasePattern("Ap*he-*-B*h-C*e-De*te");
	static private final ExBasePattern patAWWWW = new ExBasePattern("Apache-*-*-*-*");
	static private final ExBasePattern patWWAWW = new ExBasePattern("*-*-Apache-*-*");
	static private final ExBasePattern patWWWAW = new ExBasePattern("*-*-*-Apache-*");
	static private final ExBasePattern patWWWWA = new ExBasePattern("*-*-*-*-Apache");

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	static boolean contains(ExBasePatternIndexSet indexset, ExBasePattern pattern) {
		boolean allWildcard = true;
		ExBasePattern.PatternItem[] items = pattern.getPatternItems();
		if (items != null && items.length > 0) {
			for (ExBasePattern.PatternItem item : items) {
				if (ExBasePattern.PatternItem.PATTERN_FIXED==item.getPatternID() && ExBase.KEY_HAT!=item.getKeyIndex()) {
					allWildcard = false;
					String key = (String)item.getPattern();
					Set<ExBasePattern> pset = indexset.getKeyIndex(item.getKeyIndex()).get(key);
					if (pset == null)
						return false;
					if (!pset.contains(pattern))
						return false;
				}
			}
		}
		if (allWildcard) {
			if (!indexset.getNoIndexPatterns().contains(pattern))
				return false;
		}
		
		// exist
		return true;
	}

	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/**
	 * {@link exalge2.ExTransfer.ExBasePatternIndexSet#ExBasePatternIndexSet()} のためのテスト・メソッド。
	 */
	public void testExBasePatternIndexSet() {
		ExBasePatternIndexSet set = new ExBasePatternIndexSet();
		assertTrue(set.isEmpty());
	}

	/**
	 * {@link exalge2.ExTransfer.ExBasePatternIndexSet#isEmpty()} のためのテスト・メソッド。
	 */
	public void testIsEmpty() {
		ExBasePatternIndexSet set = new ExBasePatternIndexSet();
		assertTrue(set.isEmpty());
		
		set.add(patAWWWW);
		assertFalse(set.isEmptyFixedPattern());
		assertFalse(set.isEmpty());
		set.add(patWWAWW);
		assertFalse(set.isEmptyFixedPattern());
		assertFalse(set.isEmpty());
		set.add(patWWWAW);
		assertFalse(set.isEmptyFixedPattern());
		assertFalse(set.isEmpty());
		set.add(patWWWWA);
		assertFalse(set.isEmpty());
		
		set.remove(patWWWWA);
		assertFalse(set.isEmptyFixedPattern());
		assertFalse(set.isEmpty());
		set.remove(patWWWAW);
		assertFalse(set.isEmptyFixedPattern());
		assertFalse(set.isEmpty());
		set.remove(patWWAWW);
		assertFalse(set.isEmptyFixedPattern());
		assertFalse(set.isEmpty());
		set.remove(patAWWWW);
		assertTrue(set.isEmpty());
		
		set.add(patWWWWW);
		assertTrue(set.isEmptyFixedPattern());
		assertFalse(set.isEmpty());
		set.add(patWNWWW);
		assertTrue(set.isEmptyFixedPattern());
		assertFalse(set.isEmpty());
		set.add(patWHWWW);
		assertTrue(set.isEmptyFixedPattern());
		assertFalse(set.isEmpty());
		
		set.remove(patWHWWW);
		assertTrue(set.isEmptyFixedPattern());
		assertFalse(set.isEmpty());
		set.remove(patWNWWW);
		assertTrue(set.isEmptyFixedPattern());
		assertFalse(set.isEmpty());
		set.remove(patWWWWW);
		assertTrue(set.isEmpty());
	}

	/**
	 * {@link exalge2.ExTransfer.ExBasePatternIndexSet#clear()} のためのテスト・メソッド。
	 */
	public void testClear() {
		ExBasePatternIndexSet set = new ExBasePatternIndexSet();
		assertTrue(set.isEmpty());
		
		set.add(patAWWWW);
		set.add(patWWAWW);
		set.add(patWWWAW);
		set.add(patWWWWA);
		set.add(patWHWWW);
		set.add(patWNWWW);
		set.add(patWWWWW);
		assertFalse(set.isEmpty());
		
		set.clear();
		assertTrue(set.isEmpty());
	}

	/**
	 * {@link exalge2.ExTransfer.ExBasePatternIndexSet#add(exalge2.ExBasePattern)} のためのテスト・メソッド。
	 */
	public void testAdd() {
		ExBasePatternIndexSet set = new ExBasePatternIndexSet();
		assertTrue(set.isEmpty());
		
		set.add(patWNWWW);
		assertTrue(set.isEmptyFixedPattern());
		assertFalse(set.isEmpty());
		set.add(patWHWWW);
		assertTrue(set.isEmptyFixedPattern());
		assertFalse(set.isEmpty());
		set.add(patWWWWW);
		assertTrue(set.isEmptyFixedPattern());
		assertFalse(set.isEmpty());
		
		set.add(patAWWWW);
		set.add(patWWAWW);
		set.add(patWWWAW);
		set.add(patWWWWA);
		assertFalse(set.isEmptyFixedPattern());
		assertTrue(set.getNameKeyIndex().size() == 1);
		assertTrue(set.getUnitKeyIndex().size() == 1);
		assertTrue(set.getTimeKeyIndex().size() == 1);
		assertTrue(set.getSubjectKeyIndex().size() == 1);
		set.add(patANBCD);
		set.add(patBHCDA);
		set.add(patCWDAB);
		assertTrue(set.getNameKeyIndex().size() == 3);
		assertTrue(set.getUnitKeyIndex().size() == 4);
		assertTrue(set.getTimeKeyIndex().size() == 3);
		assertTrue(set.getSubjectKeyIndex().size() == 3);
		set.add(patAWWWW);
		set.add(patWWAWW);
		set.add(patWWWAW);
		set.add(patWWWWA);
		assertTrue(set.getNameKeyIndex().size() == 3);
		assertTrue(set.getUnitKeyIndex().size() == 4);
		assertTrue(set.getTimeKeyIndex().size() == 3);
		assertTrue(set.getSubjectKeyIndex().size() == 3);
		
		ExBasePatternSet wpset = new ExBasePatternSet(Arrays.asList(patWNWWW, patWHWWW, patWWWWW));
		assertTrue(set.getNoIndexPatterns().equals(wpset));
	}

	/**
	 * {@link exalge2.ExTransfer.ExBasePatternIndexSet#addAll(java.util.Collection)} のためのテスト・メソッド。
	 */
	public void testAddAll() {
		ExBasePatternSet patset = new ExBasePatternSet(Arrays.asList(
				patWNWWW, patWHWWW, patWWWWW,
				patAWWWW, patWWAWW, patWWWAW, patWWWWA,
				patANBCD, patBHCDA, patCWDAB,
				patAWWWW, patWWAWW, patWWWAW, patWWWWA
		));

		ExBasePatternIndexSet set = new ExBasePatternIndexSet();
		assertTrue(set.isEmpty());
		
		set.addAll(patset);

		assertFalse(set.isEmpty());
		assertFalse(set.isEmptyFixedPattern());
		assertTrue(set.getNameKeyIndex().size() == 3);
		assertTrue(set.getUnitKeyIndex().size() == 4);
		assertTrue(set.getTimeKeyIndex().size() == 3);
		assertTrue(set.getSubjectKeyIndex().size() == 3);
		ExBasePatternSet wpset = new ExBasePatternSet(Arrays.asList(patWNWWW, patWHWWW, patWWWWW));
		assertTrue(set.getNoIndexPatterns().equals(wpset));
	}

	/**
	 * {@link exalge2.ExTransfer.ExBasePatternIndexSet#remove(exalge2.ExBasePattern)} のためのテスト・メソッド。
	 */
	public void testRemove() {
		ExBasePatternSet patset = new ExBasePatternSet(Arrays.asList(
				patWNWWW, patWHWWW, patWWWWW,
				patAWWWW, patWWAWW, patWWWAW, patWWWWA,
				patANBCD, patBHCDA, patCWDAB,
				patAWWWW, patWWAWW, patWWWAW, patWWWWA
		));

		ExBasePatternIndexSet set = new ExBasePatternIndexSet();
		assertTrue(set.isEmpty());
		
		set.addAll(patset);
		assertFalse(set.isEmpty());
		
		for (ExBasePattern pat : patset) {
			set.remove(pat);
		}
		assertTrue(set.isEmpty());
	}

	/**
	 * {@link exalge2.ExTransfer.ExBasePatternIndexSet#removeAll(java.util.Collection)} のためのテスト・メソッド。
	 */
	public void testRemoveAll() {
		ExBasePatternSet patset = new ExBasePatternSet(Arrays.asList(
				patWNWWW, patWHWWW, patWWWWW,
				patAWWWW, patWWAWW, patWWWAW, patWWWWA,
				patANBCD, patBHCDA, patCWDAB,
				patAWWWW, patWWAWW, patWWWAW, patWWWWA
		));

		ExBasePatternIndexSet set = new ExBasePatternIndexSet();
		assertTrue(set.isEmpty());
		
		set.addAll(patset);
		assertFalse(set.isEmpty());

		set.removeAll(patset);
		assertTrue(set.isEmpty());
	}

	/**
	 * {@link exalge2.ExTransfer.ExBasePatternIndexSet#matches(exalge2.ExBase)} のためのテスト・メソッド。
	 */
	public void testMatches() {
		String[] stra = new String[]{
			"A11111", "A22222", "A33333", "A44444", "A55555",	
		};
		String[] strb = new String[]{
			"B11111", "B22222", "B33333", "B44444", "B55555",	
		};
		String[] strc = new String[]{
			"C11111", "C22222", "C33333", "C44444", "C55555",	
		};
		String[] strd = new String[]{
			"D11111", "D22222", "D33333", "D44444", "D55555",	
		};
		String[] stre = new String[]{
			"E11111", "E22222", "E33333", "E44444", "E55555",	
		};
		ExBasePatternSet patSet = new ExBasePatternSet(Arrays.asList(
				new ExBasePattern(stra[0], "*", "*", strc[0], "*"),
				new ExBasePattern(stra[0], "*", "*", strc[1], "*"),
				new ExBasePattern(stra[0], "*", "*", strc[2], "*"),
				new ExBasePattern(stra[0], "*", "*", strc[3], "*"),
				new ExBasePattern(stra[0], "*", "*", strc[4], "*"),
				new ExBasePattern(stra[1], "*", strb[0], strc[0], "*"),
				new ExBasePattern(stra[1], "*", strb[1], strc[1], "*"),
				new ExBasePattern(stra[1], "*", strb[2], strc[2], "*"),
				new ExBasePattern(stra[1], "*", strb[3], strc[3], "*"),
				new ExBasePattern(stra[1], "*", strb[4], strc[4], "*"),
				new ExBasePattern("*", "*", strb[0], strd[4], stre[4]),
				new ExBasePattern("*", "*", strb[1], strd[4], stre[4]),
				new ExBasePattern("*", "*", strb[2], strd[4], stre[4]),
				new ExBasePattern("*", "*", strb[3], strd[4], stre[4]),
				new ExBasePattern("*", "*", strb[4], strd[4], stre[4]),
				new ExBasePattern("*", "*", strb[0], strd[4], stre[3]),
				new ExBasePattern("*", "*", strb[1], strd[4], stre[3]),
				new ExBasePattern("*", "*", strb[2], strd[4], stre[3]),
				new ExBasePattern("*", "*", strb[3], strd[4], stre[3]),
				new ExBasePattern("*", "*", strb[4], strd[4], stre[3]),
				new ExBasePattern("E*", "*", "D*", "C*", "B*"),
				new ExBasePattern("D*", "*", "C*", "B*", "A*"),
				new ExBasePattern("C*", "*", "*", "*", "*")
		));
		
		ExBasePatternIndexSet set = new ExBasePatternIndexSet();
		assertTrue(set.isEmpty());
		
		set.addAll(patSet);
		assertFalse(set.isEmpty());
		
		ExBaseSet fixedMatchedBases = new ExBaseSet(Arrays.asList(
				new ExBase(stra[0], ExBase.HAT, strd[1], strc[2], strb[3]),
				new ExBase(stra[1], ExBase.NO_HAT, strb[1], strc[1], stre[3]),
				new ExBase(stra[4], ExBase.HAT, strb[1], strd[4], stre[3]),
				new ExBase(stra[3], ExBase.NO_HAT, strb[2], strd[4], stre[4])
		));
		ExBaseSet wcardMatchedBases = new ExBaseSet(Arrays.asList(
				new ExBase(stre[0], ExBase.NO_HAT, strd[1], strc[2], strb[3]),
				new ExBase(strd[1], ExBase.HAT, strc[2], strb[3], stra[4]),
				new ExBase(strc[2], ExBase.NO_HAT, strb[3], strd[4], stre[2]),
				new ExBase(strc[3], ExBase.HAT, strb[2], strd[4], stra[0])
		));
		ExBaseSet nomatchedBases = new ExBaseSet(Arrays.asList(
				new ExBase(stra[2], ExBase.NO_HAT, strb[3], strd[4], stre[0]),
				new ExBase(stre[4], ExBase.HAT, strd[3], strc[2], stra[1]),
				new ExBase(strd[3], ExBase.NO_HAT, strc[2], strb[1], stre[0])
		));
		
		ExBasePattern ret;
		
		System.out.println("<<<<< Test ExBasePatternIndexSetTest:testMatches >>>>>");
		
		for (ExBase base : fixedMatchedBases) {
			ret = set.matches(base);
			System.out.println("    " + base + " matches " + String.valueOf(ret));
			assertTrue(ret != null);
			assertTrue(ret.matches(base));
			assertEquals(ret, set.matchesFixedPatternIndex(base));
			assertTrue(null == set.matchesAllWildcardPattern(base));
		}
		
		for (ExBase base : wcardMatchedBases) {
			ret = set.matches(base);
			System.out.println("    " + base + " matches " + String.valueOf(ret));
			assertTrue(ret != null);
			assertTrue(ret.matches(base));
			assertEquals(ret, set.matchesAllWildcardPattern(base));
			assertTrue(null == set.matchesFixedPatternIndex(base));
		}
		
		for (ExBase base : nomatchedBases) {
			ret = set.matches(base);
			System.out.println("    " + base + " matches " + String.valueOf(ret));
			assertTrue(ret == null);
		}
		
		System.out.println("------------------------------------------------------");
	}
}
