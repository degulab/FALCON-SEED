/**
 * 
 */
package exalge2.util;

import java.util.Calendar;

import junit.framework.TestCase;

/**
 * @author ishizuka
 *
 */
public class ExQuarterTimeKeyTest extends TestCase {

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * {@link exalge2.util.ExQuarterTimeKey#ExQuarterTimeKey()} のためのテスト・メソッド。
	 */
	public void testExQuarterTimeKey() {
		Calendar now = Calendar.getInstance();
		ExQuarterTimeKey key = new ExQuarterTimeKey();
		
		assertEquals(now.get(Calendar.YEAR), key.year);
		int m = now.get(Calendar.MONTH)+1;
		if (1 <= m && m <= 3) {
			assertEquals(1, key.unit);
		} else if (4 <= m && m <= 6) {
			assertEquals(2, key.unit);
		} else if (7 <= m && m <= 9) {
			assertEquals(3, key.unit);
		} else if (10 <= m && m <= 12) {
			assertEquals(4, key.unit);
		}
	}

	/**
	 * {@link exalge2.util.ExQuarterTimeKey#ExQuarterTimeKey(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testExQuarterTimeKeyString() {
		final String n1 = "Y0001Q1";
		final String n2 = "Y2007Q2";
		final String n3 = "Y9999Q3";
		final String n4 = "Y2000Q4";
		final String e1 = "Y0000Q0";
		final String e2 = "Y10000Q3";
		final String e3 = "Y2007Q05";
		final String e4 = "Y2008M03";
		
		ExQuarterTimeKey key1 = new ExQuarterTimeKey(n1);
		assertEquals(1, key1.getYear());
		assertEquals(1, key1.getUnit());
		
		ExQuarterTimeKey key2 = new ExQuarterTimeKey(n2);
		assertEquals(2007, key2.getYear());
		assertEquals(2, key2.getUnit());
		
		ExQuarterTimeKey key3 = new ExQuarterTimeKey(n3);
		assertEquals(9999, key3.getYear());
		assertEquals(3, key3.getUnit());
		
		ExQuarterTimeKey key4 = new ExQuarterTimeKey(n4);
		assertEquals(2000, key4.getYear());
		assertEquals(4, key4.getUnit());

		ExQuarterTimeKey ekey = null;
		try {
			ekey = new ExQuarterTimeKey(e1);
			super.fail("Not error! [" + e1 + "]");
		} catch (IllegalArgumentException ex) {}
		
		try {
			ekey = new ExQuarterTimeKey(e2);
			super.fail("Not error! [" + e2 + "]");
		} catch (IllegalArgumentException ex) {}
		
		try {
			ekey = new ExQuarterTimeKey(e3);
			super.fail("Not error! [" + e3 + "]");
		} catch (IllegalArgumentException ex) {}
		
		try {
			ekey = new ExQuarterTimeKey(e4);
			super.fail("Not error! [" + e4 + "]");
		} catch (IllegalArgumentException ex) {}
		
		assertNull(ekey);
	}

	/**
	 * {@link exalge2.util.ExQuarterTimeKey#ExQuarterTimeKey(int, int)} のためのテスト・メソッド。
	 */
	public void testExQuarterTimeKeyIntInt() {
		ExQuarterTimeKey key1 = new ExQuarterTimeKey(1,1);
		assertEquals(1, key1.getYear());
		assertEquals(1, key1.getUnit());
		
		ExQuarterTimeKey key2 = new ExQuarterTimeKey(2007, 3);
		assertEquals(2007, key2.getYear());
		assertEquals(3, key2.getUnit());
		
		ExQuarterTimeKey key3 = new ExQuarterTimeKey(9999,4);
		assertEquals(9999, key3.getYear());
		assertEquals(4, key3.getUnit());
		
		ExQuarterTimeKey ekey = null;
		try {
			ekey = new ExQuarterTimeKey(0,1);
			super.fail("Not error! [0,1]");
		} catch (IllegalArgumentException ex) {}
		
		try {
			ekey = new ExQuarterTimeKey(10000,12);
			super.fail("Not error! [10000,12]");
		} catch (IllegalArgumentException ex) {}
		
		try {
			ekey = new ExQuarterTimeKey(2007,13);
			super.fail("Not error! [2007,13]");
		} catch (IllegalArgumentException ex) {}
		
		assertNull(ekey);
	}

	/**
	 * {@link exalge2.util.ExQuarterTimeKey#isSupported(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testIsSupported() {
		final String n1 = "Y0001Q1";
		final String n2 = "Y2007Q2";
		final String n3 = "Y9999Q3";
		final String n4 = "Y2000Q4";
		final String e1 = "Y0000Q0";
		final String e2 = "Y10000Q3";
		final String e3 = "Y2007Q05";
		final String e4 = "Y2008M03";

		ExQuarterTimeKey key = new ExQuarterTimeKey();
		assertTrue(key.isSupported(n1));
		assertTrue(key.isSupported(n2));
		assertTrue(key.isSupported(n3));
		assertTrue(key.isSupported(n4));
		assertFalse(key.isSupported(e1));
		assertFalse(key.isSupported(e2));
		assertFalse(key.isSupported(e3));
		assertFalse(key.isSupported(e4));
	}

	/**
	 * {@link exalge2.util.ExQuarterTimeKey#set(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testSetString() {
		final String n1 = "Y0001Q1";
		final String n2 = "Y2007Q2";
		final String n3 = "Y9999Q3";
		final String n4 = "Y2000Q4";
		final String e1 = "Y0000Q0";
		final String e2 = "Y10000Q3";
		final String e3 = "Y2007Q05";
		final String e4 = "Y2008M03";
		
		ExQuarterTimeKey key = new ExQuarterTimeKey();

		assertTrue(key.set(n1));
		assertEquals(1, key.getYear());
		assertEquals(1, key.getUnit());

		assertTrue(key.set(n2));
		assertEquals(2007, key.getYear());
		assertEquals(2, key.getUnit());

		assertTrue(key.set(n3));
		assertEquals(9999, key.getYear());
		assertEquals(3, key.getUnit());
		
		assertTrue(key.set(n4));
		assertEquals(2000, key.getYear());
		assertEquals(4, key.getUnit());

		try {
			key.set(e1);
			super.fail("Not error! [" + e1 + "]");
		} catch (IllegalArgumentException ex) {}
		
		try {
			key.set(e2);
			super.fail("Not error! [" + e2 + "]");
		} catch (IllegalArgumentException ex) {}
		
		try {
			key.set(e3);
			super.fail("Not error! [" + e3 + "]");
		} catch (IllegalArgumentException ex) {}
		
		try {
			key.set(e4);
			super.fail("Not error! [" + e4 + "]");
		} catch (IllegalArgumentException ex) {}
	}

	/**
	 * {@link exalge2.util.ExQuarterTimeKey#set(Calendar)} のためのテスト・メソッド。
	 */
	public void testSetCalendar() {
		ExQuarterTimeKey key0 = new ExQuarterTimeKey(2007, 3);
		int initYear = key0.getYear();
		int initUnit = key0.getUnit();
		
		ExQuarterTimeKey key1 = null;
		ExQuarterTimeKey key2 = null;
		
		Calendar cal = Calendar.getInstance();
		cal.set(1, 0, 1);

		while (cal.get(Calendar.YEAR) <= 10000) {
			key1 = new ExQuarterTimeKey(key0.toString());
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH) + 1;
			int quat;
			if (1 <= month && month <= 3)		{ quat = 1; }
			else if (4 <= month && month <= 6)	{ quat = 2; }
			else if (7 <= month && month <= 9)	{ quat = 3; }
			else								{ quat = 4; }
			if (year >= key1.getMinYear() && year <= key1.getMaxYear()) {
				try {
					key2 = new ExQuarterTimeKey(cal);
					boolean ret = key1.set(cal);
					if (year == initYear && quat == initUnit)
						assertFalse(ret);
					else
						assertTrue(ret);
					assertEquals(year, key1.getYear());
					assertEquals(quat, key1.getUnit());
					assertEquals(year, key2.getYear());
					assertEquals(quat, key2.getUnit());
					assertEquals(key1, key2);
				} catch (Throwable ex) {
					fail("cal[" + cal + "] key1[" + key1 + "] key2[" + key2 + "]");
				}
			}
			else {
				try {
					key1.set(cal);
					fail("cal[" + cal + "] key1[" + key1 + "] key2[" + key2 + "]");
				} catch (Throwable ex) {
					assertTrue(true);
				}
				try {
					key2 = new ExQuarterTimeKey(cal);
					fail("cal[" + cal + "] key1[" + key1 + "] key2[" + key2 + "]");
				} catch (Throwable ex) {
					assertTrue(true);
				}
			}
			cal.add(Calendar.MONTH, 1);
		}
	}

	/**
	 * {@link exalge2.util.ExQuarterTimeKey#getMinUnit()} のためのテスト・メソッド。
	 */
	public void testGetMinUnit() {
		ExQuarterTimeKey key = new ExQuarterTimeKey();
		
		assertEquals(1, key.getMinUnit());
	}

	/**
	 * {@link exalge2.util.ExQuarterTimeKey#getMaxUnit()} のためのテスト・メソッド。
	 */
	public void testGetMaxUnit() {
		ExQuarterTimeKey key = new ExQuarterTimeKey();
		
		assertEquals(4, key.getMaxUnit());
	}

	/**
	 * {@link exalge2.util.ExQuarterTimeKey#toString()} のためのテスト・メソッド。
	 */
	public void testToString() {
		final String n1 = "Y0001Q1";
		final String n2 = "Y2007Q2";
		final String n3 = "Y9999Q3";
		final String n4 = "Y2000Q4";
		
		ExQuarterTimeKey key = new ExQuarterTimeKey(2007,2);
		
		assertTrue(key.set(n1));
		assertEquals(n1, key.toString());
		
		assertTrue(key.set(n2));
		assertEquals(n2, key.toString());
		
		assertTrue(key.set(n3));
		assertEquals(n3, key.toString());
		
		assertTrue(key.set(n4));
		assertEquals(n4, key.toString());
	}

	/**
	 * {@link exalge2.util.AbExTimeKey#hashCode()} のためのテスト・メソッド。
	 */
	public void testHashCode() {
		ExQuarterTimeKey key = new ExQuarterTimeKey(2007,2);
		assertEquals((2*10000+2007), key.hashCode());
		
		key.setUnit(4);
		assertEquals((4*10000+2007), key.hashCode());
		
		key.setYear(9999);
		assertEquals((4*10000+9999), key.hashCode());
	}

	/**
	 * {@link exalge2.util.AbExTimeKey#isValidUnit(int)} のためのテスト・メソッド。
	 */
	public void testIsValidUnit() {
		ExQuarterTimeKey key = new ExQuarterTimeKey();
		
		assertFalse(key.isValidUnit(0));
		assertTrue(key.isValidUnit(1));
		assertTrue(key.isValidUnit(2));
		assertTrue(key.isValidUnit(3));
		assertTrue(key.isValidUnit(4));
		assertFalse(key.isValidUnit(5));
		assertFalse(key.isValidUnit(1997));
		assertFalse(key.isValidUnit(-2));
	}

	/**
	 * {@link exalge2.util.AbExTimeKey#getUnit()} のためのテスト・メソッド。
	 */
	public void testGetUnit() {
		ExQuarterTimeKey key = new ExQuarterTimeKey(2007,3);
		
		assertEquals(3, key.getUnit());

		key.setUnit(1);
		assertEquals(1, key.getUnit());
		
		key.setUnit(4);
		assertEquals(4, key.getUnit());
	}

	/**
	 * {@link exalge2.util.AbExTimeKey#setUnit(int)} のためのテスト・メソッド。
	 */
	public void testSetUnit() {
		ExQuarterTimeKey key = new ExQuarterTimeKey();
		
		try {
			key.setUnit(0);
			super.fail("Not error! [0]");
		} catch (IllegalArgumentException ex) {}
		
		try {
			key.setUnit(5);
			super.fail("Not error! [5]");
		} catch (IllegalArgumentException ex) {}
		
		try {
			key.setUnit(-2);
			super.fail("Not error! [-2]");
		} catch (IllegalArgumentException ex) {}
		
		assertEquals(1, key.setUnit(1));
		assertEquals(2, key.setUnit(2));
		assertEquals(3, key.setUnit(3));
		assertEquals(4, key.setUnit(4));
	}

	/**
	 * {@link exalge2.util.AbExTimeKey#addUnit(int)} のためのテスト・メソッド。
	 */
	public void testAddUnit() {
		ExQuarterTimeKey key = new ExQuarterTimeKey(2007,3);

		int i = 0;
		int year = key.getYear();
		int unit = key.getUnit();
		while (i < 100000) {
			for ( ; unit <= key.getMaxUnit(); unit++, i++) {
				ExQuarterTimeKey qtk = new ExQuarterTimeKey(key.toString());
				if (year <= key.getMaxYear()) {
					try {
						int q = qtk.addUnit(i);
						assertTrue(qtk.getMinUnit() <= q && q <= qtk.getMaxUnit());
						assertTrue(year == qtk.getYear());
						assertTrue(unit == q && unit == qtk.getUnit());
					}
					catch (Throwable ex) {
						fail("failed to add amount(" + i + ") to ExQuarterTimeKey[" + key.toString() + "]"); 
					}
				}
				else {
					try {
						qtk.addUnit(i);
						fail("Illegal operation to add amount(" + i + ") to ExQuarterTimeKey[" + key.toString() + "]"); 
					}
					catch (Throwable ex) {
						assertTrue(true);
					}
				}
			}
			year += 1;
			unit = key.getMinUnit();
		}

		i = 0;
		year = key.getYear();
		unit = key.getUnit();
		while (i >= -100000) {
			for ( ; unit >= key.getMinUnit(); unit--, i--) {
				ExQuarterTimeKey qtk = new ExQuarterTimeKey(key.toString());
				if (year >= key.getMinYear()) {
					try {
						int q = qtk.addUnit(i);
						assertTrue(qtk.getMinUnit() <= q && q <= qtk.getMaxUnit());
						assertTrue(year == qtk.getYear());
						assertTrue(unit == q && unit == qtk.getUnit());
					}
					catch (Throwable ex) {
						fail("failed to add amount(" + i + ") to ExQuarterTimeKey[" + key.toString() + "]"); 
					}
				}
				else {
					try {
						qtk.addUnit(i);
						fail("Illegal operation to add amount(" + i + ") to ExQuarterTimeKey[" + key.toString() + "]"); 
					}
					catch (Throwable ex) {
						assertTrue(true);
					}
				}
			}
			year -= 1;
			unit = key.getMaxUnit();
		}
	}

	/**
	 * {@link exalge2.util.AbExTimeKey#equals(java.lang.Object)} のためのテスト・メソッド。
	 */
	public void testEqualsObject() {
		ExQuarterTimeKey key1 = new ExQuarterTimeKey(2007,1);
		ExQuarterTimeKey key2 = new ExQuarterTimeKey(2007,1);
		ExQuarterTimeKey key3 = new ExQuarterTimeKey(2007,2);
		ExQuarterTimeKey key4 = new ExQuarterTimeKey(2006,2);
		ExQuarterTimeKey key5 = new ExQuarterTimeKey(2006,3);
		ExYearTimeKey key6 = new ExYearTimeKey(2007);
		
		assertTrue(key1.equals(key2));
		assertFalse(key1.equals(key3));
		assertFalse(key1.equals(key4));
		assertFalse(key1.equals(key5));
		assertFalse(key1.equals(key6));
		assertFalse(key1.equals(new Object()));
		assertFalse(key1.equals(null));
		
		assertTrue(key1.equals(key1));
	}

}
