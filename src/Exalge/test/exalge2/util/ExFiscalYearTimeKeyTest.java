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
public class ExFiscalYearTimeKeyTest extends TestCase {

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * {@link exalge2.util.ExFiscalYearTimeKey#ExFiscalYearTimeKey()} のためのテスト・メソッド。
	 */
	public void testExFiscalYearTimeKey() {
		Calendar now = Calendar.getInstance();
		ExFiscalYearTimeKey key = new ExFiscalYearTimeKey();

		int nowYear = now.get(Calendar.YEAR);
		int nowMonth = now.get(Calendar.MONTH)+1;
		if (1 <= nowMonth && nowMonth <= 3) {
			assertEquals((nowYear-1), key.year);
		}
		else {
			assertEquals(nowYear, key.year);
		}
		assertEquals(0, key.unit);
	}

	/**
	 * {@link exalge2.util.ExFiscalYearTimeKey#ExFiscalYearTimeKey(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testExFiscalYearTimeKeyString() {
		final String n1 = "FY0001";
		final String n2 = "FY2007";
		final String e1 = "FY0000";
		final String e2 = "FY10000";
		final String e3 = "Y2007M08";
		final String e4 = "FY2008Q3";
		
		ExFiscalYearTimeKey key1 = new ExFiscalYearTimeKey(n1);
		assertEquals(1, key1.getYear());
		
		ExFiscalYearTimeKey key2 = new ExFiscalYearTimeKey(n2);
		assertEquals(2007, key2.getYear());

		ExFiscalYearTimeKey ekey = null;
		try {
			ekey = new ExFiscalYearTimeKey(e1);
			super.fail("Not error! [" + e1 + "]");
		} catch (IllegalArgumentException ex) {}
		
		try {
			ekey = new ExFiscalYearTimeKey(e2);
			super.fail("Not error! [" + e2 + "]");
		} catch (IllegalArgumentException ex) {}
		
		try {
			ekey = new ExFiscalYearTimeKey(e3);
			super.fail("Not error! [" + e3 + "]");
		} catch (IllegalArgumentException ex) {}
		
		try {
			ekey = new ExFiscalYearTimeKey(e4);
			super.fail("Not error! [" + e4 + "]");
		} catch (IllegalArgumentException ex) {}
		
		assertNull(ekey);
	}

	/**
	 * {@link exalge2.util.ExFiscalYearTimeKey#ExFiscalYearTimeKey(int)} のためのテスト・メソッド。
	 */
	public void testExFiscalYearTimeKeyInt() {
		ExFiscalYearTimeKey key1 = new ExFiscalYearTimeKey(1);
		assertEquals(1, key1.getYear());
		
		ExFiscalYearTimeKey key2 = new ExFiscalYearTimeKey(2007);
		assertEquals(2007, key2.getYear());
		
		ExFiscalYearTimeKey ekey = null;
		try {
			ekey = new ExFiscalYearTimeKey(0);
			super.fail("Not error! [0]");
		} catch (IllegalArgumentException ex) {}
		
		try {
			ekey = new ExFiscalYearTimeKey(10000);
			super.fail("Not error! [10000]");
		} catch (IllegalArgumentException ex) {}
		
		assertNull(ekey);
	}

	/**
	 * 以下のテスト
	 * <ul>
	 * <li>{@link exalge2.util.ExFiscalYearTimeKey#getMinYear()}
	 * <li>{@link exalge2.util.ExFiscalYearTimeKey#getMaxYear()}
	 * <li>{@link exalge2.util.ExFiscalYearTimeKey#getMinUnit()}
	 * <li>{@link exalge2.util.ExFiscalYearTimeKey#getMaxUnit()}
	 * </ul>
	 *
	 */
	public void testGetMinMax() {
		ExFiscalYearTimeKey key = new ExFiscalYearTimeKey();
		
		assertEquals(1, key.getMinYear());
		assertEquals(9999, key.getMaxYear());
		assertEquals(1, key.getMinUnit());
		assertEquals(9999, key.getMaxUnit());
	}

	/**
	 * {@link exalge2.util.ExFiscalYearTimeKey#isValidUnit(int)} のためのテスト・メソッド。
	 */
	public void testIsValidUnit() {
		ExFiscalYearTimeKey key = new ExFiscalYearTimeKey();
		
		assertTrue(key.isValidUnit(1));
		assertTrue(key.isValidUnit(2007));
		assertTrue(key.isValidUnit(9999));
		assertFalse(key.isValidUnit(0));
		assertFalse(key.isValidUnit(-2007));
		assertFalse(key.isValidUnit(10000));
	}

	/**
	 * {@link exalge2.util.ExFiscalYearTimeKey#getUnit()} のためのテスト・メソッド。
	 */
	public void testGetUnit() {
		ExFiscalYearTimeKey key1 = new ExFiscalYearTimeKey();
		assertEquals(key1.getYear(), key1.getUnit());
		
		ExFiscalYearTimeKey key2 = new ExFiscalYearTimeKey(2010);
		assertEquals(key2.getYear(), key2.getUnit());
	}

	/**
	 * {@link exalge2.util.ExFiscalYearTimeKey#setUnit(int)} のためのテスト・メソッド。
	 */
	public void testSetUnit() {
		ExFiscalYearTimeKey key = new ExFiscalYearTimeKey();
		
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
	 * {@link exalge2.util.ExFiscalYearTimeKey#addUnit(int)} のためのテスト・メソッド。
	 */
	public void testAddUnit() {
		final int iyear = 2007;
		ExFiscalYearTimeKey key = new ExFiscalYearTimeKey(iyear);
		
		for (int i = 0; i < 10000; i++) {
			ExFiscalYearTimeKey ytk = new ExFiscalYearTimeKey(iyear);
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
			ExFiscalYearTimeKey ytk = new ExFiscalYearTimeKey(iyear);
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
	 * {@link exalge2.util.ExFiscalYearTimeKey#isSupported(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testIsSupported() {
		final String n1 = "FY0001";
		final String n2 = "FY2007";
		final String e1 = "FY0000";
		final String e2 = "FY10000";
		final String e3 = "Y2007M08";
		final String e4 = "FY2008Q3";

		ExFiscalYearTimeKey key = new ExFiscalYearTimeKey();
		assertTrue(key.isSupported(n1));
		assertTrue(key.isSupported(n2));
		assertFalse(key.isSupported(e1));
		assertFalse(key.isSupported(e2));
		assertFalse(key.isSupported(e3));
		assertFalse(key.isSupported(e4));
	}

	/**
	 * {@link exalge2.util.ExFiscalYearTimeKey#set(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testSetString() {
		final String n1 = "FY0001";
		final String n2 = "FY2007";
		final String e1 = "FY0000";
		final String e2 = "FY10000";
		final String e3 = "Y2007M08";
		final String e4 = "FY2008Q3";
		
		ExFiscalYearTimeKey key = new ExFiscalYearTimeKey();

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
	 * {@link exalge2.util.ExFiscalYearTimeKey#set(Calendar)} のためのテスト・メソッド。
	 */
	public void testSetCalendar() {
		ExFiscalYearTimeKey key0 = new ExFiscalYearTimeKey(2007);
		int initYear = key0.getYear();
		//int initUnit = key0.getUnit();
		
		ExFiscalYearTimeKey key1 = null;
		ExFiscalYearTimeKey key2 = null;
		
		Calendar cal = Calendar.getInstance();
		cal.set(1, 0, 1);

		while (cal.get(Calendar.YEAR) <= 10000) {
			key1 = new ExFiscalYearTimeKey(key0.toString());
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH) + 1;
			if (1 <= month && month <= 3)
				year -= 1;
			/*
			int quat;
			if (1 <= quat && quat <= 3)			{ quat = 1; }
			else if (4 <= quat && quat <= 6)	{ quat = 2; }
			else if (7 <= quat && quat <= 9)	{ quat = 3; }
			else								{ quat = 4; }
			*/
			if (year >= key1.getMinYear() && year <= key1.getMaxYear()) {
				try {
					key2 = new ExFiscalYearTimeKey(cal);
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
					key2 = new ExFiscalYearTimeKey(cal);
					fail("cal[" + cal + "] key1[" + key1 + "] key2[" + key2 + "]");
				} catch (Throwable ex) {
					assertTrue(true);
				}
			}
			cal.add(Calendar.MONTH, 1);
		}
	}

	/**
	 * {@link exalge2.util.ExFiscalYearTimeKey#toString()} のためのテスト・メソッド。
	 */
	public void testToString() {
		final String n1 = "FY0001";
		final String n2 = "FY2007";
		final String n3 = "FY9999";
		
		ExFiscalYearTimeKey key = new ExFiscalYearTimeKey(2007);
		
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
		ExFiscalYearTimeKey key = new ExFiscalYearTimeKey(2007);
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
		ExFiscalYearTimeKey key = new ExFiscalYearTimeKey();
		
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
		ExFiscalYearTimeKey key1 = new ExFiscalYearTimeKey(2007);
		assertEquals(2007, key1.getYear());
		
		key1.setYear(2010);
		assertEquals(2010, key1.getYear());
	}

	/**
	 * {@link exalge2.util.AbExTimeKey#setYear(int)} のためのテスト・メソッド。
	 */
	public void testSetYear() {
		ExFiscalYearTimeKey key = new ExFiscalYearTimeKey();
		
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
		ExFiscalYearTimeKey key = new ExFiscalYearTimeKey(iyear);
		
		for (int i = 0; i < 10000; i++) {
			ExFiscalYearTimeKey ytk = new ExFiscalYearTimeKey(iyear);
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
			ExFiscalYearTimeKey ytk = new ExFiscalYearTimeKey(iyear);
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
		ExFiscalYearTimeKey key1 = new ExFiscalYearTimeKey(2007);
		ExFiscalYearTimeKey key2 = new ExFiscalYearTimeKey(2007);
		ExFiscalYearTimeKey key3 = new ExFiscalYearTimeKey(2006);
		ExMonthTimeKey key4 = new ExMonthTimeKey(2007,8);
		ExYearTimeKey key5 = new ExYearTimeKey(2007);
		
		assertTrue(key1.equals(key2));
		assertFalse(key1.equals(key3));
		assertFalse(key1.equals(key4));
		assertFalse(key1.equals(key5));
		assertFalse(key1.equals(new Object()));
		assertFalse(key1.equals(null));
		
		assertTrue(key1.equals(key1));
	}
}
