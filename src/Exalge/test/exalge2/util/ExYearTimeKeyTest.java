/**
 * 
 */
package exalge2.util;

import java.util.Calendar;

import exalge2.util.ExMonthTimeKey;
import exalge2.util.ExYearTimeKey;

import junit.framework.TestCase;

/**
 * @author ishizuka
 *
 */
public class ExYearTimeKeyTest extends TestCase {

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * {@link exalge2.util.ExYearTimeKey#ExYearTimeKey()} のためのテスト・メソッド。
	 */
	public void testExYearTimeKey() {
		Calendar now = Calendar.getInstance();
		ExYearTimeKey key = new ExYearTimeKey();
		
		assertEquals(now.get(Calendar.YEAR), key.year);
		assertEquals(0, key.unit);
	}

	/**
	 * {@link exalge2.util.ExYearTimeKey#ExYearTimeKey(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testExYearTimeKeyString() {
		final String n1 = "Y0001";
		final String n2 = "Y2007";
		final String e1 = "Y0000";
		final String e2 = "Y10000";
		final String e3 = "Y2007M08";
		final String e4 = "Y2008Q3";
		
		ExYearTimeKey key1 = new ExYearTimeKey(n1);
		assertEquals(1, key1.getYear());
		
		ExYearTimeKey key2 = new ExYearTimeKey(n2);
		assertEquals(2007, key2.getYear());

		ExYearTimeKey ekey = null;
		try {
			ekey = new ExYearTimeKey(e1);
			super.fail("Not error! [" + e1 + "]");
		} catch (IllegalArgumentException ex) {}
		
		try {
			ekey = new ExYearTimeKey(e2);
			super.fail("Not error! [" + e2 + "]");
		} catch (IllegalArgumentException ex) {}
		
		try {
			ekey = new ExYearTimeKey(e3);
			super.fail("Not error! [" + e3 + "]");
		} catch (IllegalArgumentException ex) {}
		
		try {
			ekey = new ExYearTimeKey(e4);
			super.fail("Not error! [" + e4 + "]");
		} catch (IllegalArgumentException ex) {}
		
		assertNull(ekey);
	}

	/**
	 * {@link exalge2.util.ExYearTimeKey#ExYearTimeKey(int)} のためのテスト・メソッド。
	 */
	public void testExYearTimeKeyInt() {
		ExYearTimeKey key1 = new ExYearTimeKey(1);
		assertEquals(1, key1.getYear());
		
		ExYearTimeKey key2 = new ExYearTimeKey(2007);
		assertEquals(2007, key2.getYear());
		
		ExYearTimeKey ekey = null;
		try {
			ekey = new ExYearTimeKey(0);
			super.fail("Not error! [0]");
		} catch (IllegalArgumentException ex) {}
		
		try {
			ekey = new ExYearTimeKey(10000);
			super.fail("Not error! [10000]");
		} catch (IllegalArgumentException ex) {}
		
		assertNull(ekey);
	}

	/**
	 * 以下のテスト
	 * <ul>
	 * <li>{@link exalge2.util.ExYearTimeKey#getMinYear()}
	 * <li>{@link exalge2.util.ExYearTimeKey#getMaxYear()}
	 * <li>{@link exalge2.util.ExYearTimeKey#getMinUnit()}
	 * <li>{@link exalge2.util.ExYearTimeKey#getMaxUnit()}
	 * </ul>
	 *
	 */
	public void testGetMinMax() {
		ExYearTimeKey key = new ExYearTimeKey();
		
		assertEquals(1, key.getMinYear());
		assertEquals(9999, key.getMaxYear());
		assertEquals(1, key.getMinUnit());
		assertEquals(9999, key.getMaxUnit());
	}

	/**
	 * {@link exalge2.util.ExYearTimeKey#isValidUnit(int)} のためのテスト・メソッド。
	 */
	public void testIsValidUnit() {
		ExYearTimeKey key = new ExYearTimeKey();
		
		assertTrue(key.isValidUnit(1));
		assertTrue(key.isValidUnit(2007));
		assertTrue(key.isValidUnit(9999));
		assertFalse(key.isValidUnit(0));
		assertFalse(key.isValidUnit(-2007));
		assertFalse(key.isValidUnit(10000));
	}

	/**
	 * {@link exalge2.util.ExYearTimeKey#getUnit()} のためのテスト・メソッド。
	 */
	public void testGetUnit() {
		ExYearTimeKey key1 = new ExYearTimeKey();
		assertEquals(key1.getYear(), key1.getUnit());
		
		ExYearTimeKey key2 = new ExYearTimeKey(2010);
		assertEquals(key2.getYear(), key2.getUnit());
	}

	/**
	 * {@link exalge2.util.ExYearTimeKey#setUnit(int)} のためのテスト・メソッド。
	 */
	public void testSetUnit() {
		ExYearTimeKey key = new ExYearTimeKey();
		
		try {
			key.setUnit(0);
			super.fail("Not error! [0]");
		} catch (IllegalArgumentException ex) {}
		
		try {
			key.setUnit(10000);
			super.fail("Not error! [10000]");
		} catch (IllegalArgumentException ex) {}
		
		assertEquals(1, key.setUnit(1));
		assertEquals(2007, key.setUnit(2007));
		assertEquals(9999, key.setUnit(9999));
	}

	/**
	 * {@link exalge2.util.ExYearTimeKey#addUnit(int)} のためのテスト・メソッド。
	 */
	public void testAddUnit() {
		final int iyear = 2007;
		ExYearTimeKey key = new ExYearTimeKey(iyear);
		
		for (int i = 0; i < 10000; i++) {
			ExYearTimeKey ytk = new ExYearTimeKey(iyear);
			int year = iyear + i;
			if (year <= key.getMaxUnit()) {
				try {
					int y = ytk.addUnit(i);
					assertTrue(key.getMinUnit() <= ytk.getYear() && ytk.getYear() <= key.getMaxUnit());
					assertTrue(y == ytk.getYear() && y == year);
				}
				catch (Throwable ex) {
					fail("failed to add amount(" + i + ") to ExYearTimeKey[" + key.toString() + "]"); 
				}
			}
			else {
				try {
					ytk.addUnit(i);
					fail("Illegal operation to add amount(" + i + ") to ExYearTimeKey[" + key.toString() + "]"); 
				}
				catch (Throwable ex) {
					assertTrue(true);
				}
			}
		}
		
		for (int i = -1; i >= -10000; i--) {
			ExYearTimeKey ytk = new ExYearTimeKey(iyear);
			int year = iyear + i;
			if (year >= key.getMinUnit()) {
				try {
					int y = ytk.addUnit(i);
					assertTrue(key.getMinUnit() <= ytk.getYear() && ytk.getYear() <= key.getMaxUnit());
					assertTrue(y == ytk.getYear() && y == year);
				}
				catch (Throwable ex) {
					fail("failed to add amount(" + i + ") to ExYearTimeKey[" + key.toString() + "]"); 
				}
			}
			else {
				try {
					ytk.addUnit(i);
					fail("Illegal operation to add amount(" + i + ") to ExYearTimeKey[" + key.toString() + "]"); 
				}
				catch (Throwable ex) {
					assertTrue(true);
				}
			}
		}
	}

	/**
	 * {@link exalge2.util.ExYearTimeKey#isSupported(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testIsSupported() {
		final String n1 = "Y0001";
		final String n2 = "Y2007";
		final String e1 = "Y0000";
		final String e2 = "Y10000";
		final String e3 = "Y2007M08";
		final String e4 = "Y2008Q3";

		ExYearTimeKey key = new ExYearTimeKey();
		assertTrue(key.isSupported(n1));
		assertTrue(key.isSupported(n2));
		assertFalse(key.isSupported(e1));
		assertFalse(key.isSupported(e2));
		assertFalse(key.isSupported(e3));
		assertFalse(key.isSupported(e4));
	}

	/**
	 * {@link exalge2.util.ExYearTimeKey#set(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testSetString() {
		final String n1 = "Y0001";
		final String n2 = "Y2007";
		final String e1 = "Y0000";
		final String e2 = "Y10000";
		final String e3 = "Y2007M08";
		final String e4 = "Y2008Q3";
		
		ExYearTimeKey key = new ExYearTimeKey();

		assertTrue(key.set(n1));
		assertFalse(key.set(n1));
		
		assertTrue(key.set(n2));
		assertFalse(key.set(n2));

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
	 * {@link exalge2.util.ExYearTimeKey#set(Calendar)} のためのテスト・メソッド。
	 */
	public void testSetCalendar() {
		ExYearTimeKey key0 = new ExYearTimeKey(2007);
		int initYear = key0.getYear();
		//int initUnit = key0.getUnit();
		
		ExYearTimeKey key1 = null;
		ExYearTimeKey key2 = null;
		
		Calendar cal = Calendar.getInstance();
		cal.set(1, 0, 1);

		while (cal.get(Calendar.YEAR) <= 10000) {
			key1 = new ExYearTimeKey(key0.toString());
			int year = cal.get(Calendar.YEAR);
			if (year >= key1.getMinYear() && year <= key1.getMaxYear()) {
				try {
					key2 = new ExYearTimeKey(cal);
					boolean ret = key1.set(cal);
					if (year == initYear)
						assertFalse(ret);
					else
						assertTrue(ret);
					assertEquals(year, key1.getYear());
					assertEquals(year, key1.getUnit());
					assertEquals(year, key2.getYear());
					assertEquals(year, key2.getUnit());
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
					key2 = new ExYearTimeKey(cal);
					fail("cal[" + cal + "] key1[" + key1 + "] key2[" + key2 + "]");
				} catch (Throwable ex) {
					assertTrue(true);
				}
			}
			cal.add(Calendar.MONTH, 1);
		}
	}

	/**
	 * {@link exalge2.util.ExYearTimeKey#toString()} のためのテスト・メソッド。
	 */
	public void testToString() {
		final String n1 = "Y0001";
		final String n2 = "Y2007";
		final String n3 = "Y9999";
		
		ExYearTimeKey key = new ExYearTimeKey(2007);
		
		assertTrue(key.set(n1));
		assertEquals(n1, key.toString());
		
		assertTrue(key.set(n2));
		assertEquals(n2, key.toString());
		
		assertTrue(key.set(n3));
		assertEquals(n3, key.toString());
	}

	/**
	 * {@link exalge2.util.AbExTimeKey#hashCode()} のためのテスト・メソッド。
	 */
	public void testHashCode() {
		ExYearTimeKey key = new ExYearTimeKey(2007);
		assertEquals(2007, key.hashCode());
		
		key.setUnit(1);
		assertEquals(1, key.hashCode());
		
		key.setUnit(9999);
		assertEquals(9999, key.hashCode());
	}

	/**
	 * {@link exalge2.util.AbExTimeKey#isValidYear(int)} のためのテスト・メソッド。
	 */
	public void testIsValidYear() {
		ExYearTimeKey key = new ExYearTimeKey();
		
		assertTrue(key.isValidYear(1));
		assertTrue(key.isValidYear(2007));
		assertTrue(key.isValidYear(9999));
		assertFalse(key.isValidYear(0));
		assertFalse(key.isValidYear(-2007));
		assertFalse(key.isValidYear(10000));
	}

	/**
	 * {@link exalge2.util.AbExTimeKey#getYear()} のためのテスト・メソッド。
	 */
	public void testGetYear() {
		ExYearTimeKey key1 = new ExYearTimeKey(2007);
		assertEquals(2007, key1.getYear());
		
		key1.setYear(2010);
		assertEquals(2010, key1.getYear());
	}

	/**
	 * {@link exalge2.util.AbExTimeKey#setYear(int)} のためのテスト・メソッド。
	 */
	public void testSetYear() {
		ExYearTimeKey key = new ExYearTimeKey();
		
		try {
			key.setYear(0);
			super.fail("Not error! [0]");
		} catch (IllegalArgumentException ex) {}
		
		try {
			key.setYear(10000);
			super.fail("Not error! [10000]");
		} catch (IllegalArgumentException ex) {}
		
		assertEquals(1, key.setYear(1));
		assertEquals(2007, key.setYear(2007));
		assertEquals(9999, key.setYear(9999));
	}

	/**
	 * {@link exalge2.util.AbExTimeKey#addYear(int)} のためのテスト・メソッド。
	 */
	public void testAddYear() {
		final int iyear = 2007;
		ExYearTimeKey key = new ExYearTimeKey(iyear);
		
		for (int i = 0; i < 10000; i++) {
			ExYearTimeKey ytk = new ExYearTimeKey(iyear);
			int year = iyear + i;
			if (year <= key.getMaxYear()) {
				try {
					int y = ytk.addYear(i);
					assertTrue(key.getMinYear() <= ytk.getYear() && ytk.getYear() <= key.getMaxYear());
					assertTrue(y == ytk.getYear() && y == year);
				}
				catch (Throwable ex) {
					fail("failed to add amount(" + i + ") to ExYearTimeKey[" + key.toString() + "]"); 
				}
			}
			else {
				try {
					ytk.addYear(i);
					fail("Illegal operation to add amount(" + i + ") to ExYearTimeKey[" + key.toString() + "]"); 
				}
				catch (Throwable ex) {
					assertTrue(true);
				}
			}
		}
		
		for (int i = -1; i >= -10000; i--) {
			ExYearTimeKey ytk = new ExYearTimeKey(iyear);
			int year = iyear + i;
			if (year >= key.getMinYear()) {
				try {
					int y = ytk.addYear(i);
					assertTrue(key.getMinYear() <= ytk.getYear() && ytk.getYear() <= key.getMaxYear());
					assertTrue(y == ytk.getYear() && y == year);
				}
				catch (Throwable ex) {
					fail("failed to add amount(" + i + ") to ExYearTimeKey[" + key.toString() + "]"); 
				}
			}
			else {
				try {
					ytk.addYear(i);
					fail("Illegal operation to add amount(" + i + ") to ExYearTimeKey[" + key.toString() + "]"); 
				}
				catch (Throwable ex) {
					assertTrue(true);
				}
			}
		}
	}

	/**
	 * {@link exalge2.util.AbExTimeKey#equals(java.lang.Object)} のためのテスト・メソッド。
	 */
	public void testEqualsObject() {
		ExYearTimeKey key1 = new ExYearTimeKey(2007);
		ExYearTimeKey key2 = new ExYearTimeKey(2007);
		ExYearTimeKey key3 = new ExYearTimeKey(2006);
		ExMonthTimeKey key4 = new ExMonthTimeKey(2007,8);
		
		assertTrue(key1.equals(key2));
		assertFalse(key1.equals(key3));
		assertFalse(key1.equals(key4));
		assertFalse(key1.equals(new Object()));
		assertFalse(key1.equals(null));
		
		assertTrue(key1.equals(key1));
	}

}
