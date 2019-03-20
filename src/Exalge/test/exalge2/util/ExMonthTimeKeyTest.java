/**
 * 
 */
package exalge2.util;

import java.util.Calendar;

import exalge2.util.ExMonthTimeKey;

import junit.framework.TestCase;

/**
 * @author ishizuka
 *
 */
public class ExMonthTimeKeyTest extends TestCase {

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * {@link exalge2.util.ExMonthTimeKey#ExMonthTimeKey()} のためのテスト・メソッド。
	 */
	public void testExMonthTimeKey() {
		Calendar now = Calendar.getInstance();
		ExMonthTimeKey key = new ExMonthTimeKey();
		
		assertEquals(now.get(Calendar.YEAR), key.year);
		assertEquals(now.get(Calendar.MONTH)+1, key.unit);
	}

	/**
	 * {@link exalge2.util.ExMonthTimeKey#ExMonthTimeKey(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testExMonthTimeKeyString() {
		final String n1 = "Y0001M01";
		final String n2 = "Y2007M08";
		final String n3 = "Y9999M12";
		final String e1 = "Y0000M00";
		final String e2 = "Y10000M12";
		final String e3 = "Y2007M8";
		final String e4 = "Y2008Q3";
		
		ExMonthTimeKey key1 = new ExMonthTimeKey(n1);
		assertEquals(1, key1.getYear());
		assertEquals(1, key1.getUnit());
		
		ExMonthTimeKey key2 = new ExMonthTimeKey(n2);
		assertEquals(2007, key2.getYear());
		assertEquals(8, key2.getUnit());
		
		ExMonthTimeKey key3 = new ExMonthTimeKey(n3);
		assertEquals(9999, key3.getYear());
		assertEquals(12, key3.getUnit());

		ExMonthTimeKey ekey = null;
		try {
			ekey = new ExMonthTimeKey(e1);
			super.fail("Not error! [" + e1 + "]");
		} catch (IllegalArgumentException ex) {}
		
		try {
			ekey = new ExMonthTimeKey(e2);
			super.fail("Not error! [" + e2 + "]");
		} catch (IllegalArgumentException ex) {}
		
		try {
			ekey = new ExMonthTimeKey(e3);
			super.fail("Not error! [" + e3 + "]");
		} catch (IllegalArgumentException ex) {}
		
		try {
			ekey = new ExMonthTimeKey(e4);
			super.fail("Not error! [" + e4 + "]");
		} catch (IllegalArgumentException ex) {}
		
		assertNull(ekey);
	}

	/**
	 * {@link exalge2.util.ExMonthTimeKey#ExMonthTimeKey(int, int)} のためのテスト・メソッド。
	 */
	public void testExMonthTimeKeyIntInt() {
		ExMonthTimeKey key1 = new ExMonthTimeKey(1,1);
		assertEquals(1, key1.getYear());
		assertEquals(1, key1.getUnit());
		
		ExMonthTimeKey key2 = new ExMonthTimeKey(2007, 8);
		assertEquals(2007, key2.getYear());
		assertEquals(8, key2.getUnit());
		
		ExMonthTimeKey key3 = new ExMonthTimeKey(9999,12);
		assertEquals(9999, key3.getYear());
		assertEquals(12, key3.getUnit());
		
		ExMonthTimeKey ekey = null;
		try {
			ekey = new ExMonthTimeKey(0,1);
			super.fail("Not error! [0,1]");
		} catch (IllegalArgumentException ex) {}
		
		try {
			ekey = new ExMonthTimeKey(10000,12);
			super.fail("Not error! [10000,12]");
		} catch (IllegalArgumentException ex) {}
		
		try {
			ekey = new ExMonthTimeKey(2007,13);
			super.fail("Not error! [2007,13]");
		} catch (IllegalArgumentException ex) {}
		
		assertNull(ekey);
	}

	/**
	 * {@link exalge2.util.ExMonthTimeKey#isSupported(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testIsSupported() {
		final String n1 = "Y0001M01";
		final String n2 = "Y2007M08";
		final String n3 = "Y9999M12";
		final String e1 = "Y0000M00";
		final String e2 = "Y10000M12";
		final String e3 = "Y2007M8";
		final String e4 = "Y2008Q3";

		ExMonthTimeKey key = new ExMonthTimeKey();
		assertTrue(key.isSupported(n1));
		assertTrue(key.isSupported(n2));
		assertTrue(key.isSupported(n3));
		assertFalse(key.isSupported(e1));
		assertFalse(key.isSupported(e2));
		assertFalse(key.isSupported(e3));
		assertFalse(key.isSupported(e4));
	}

	/**
	 * {@link exalge2.util.ExMonthTimeKey#set(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testSetString() {
		final String n1 = "Y0001M01";
		final String n2 = "Y2007M08";
		final String n3 = "Y9999M12";
		final String e1 = "Y0000M00";
		final String e2 = "Y10000M12";
		final String e3 = "Y2007M8";
		final String e4 = "Y2008Q3";
		
		ExMonthTimeKey key = new ExMonthTimeKey();

		assertTrue(key.set(n1));
		assertEquals(1, key.getYear());
		assertEquals(1, key.getUnit());

		assertTrue(key.set(n2));
		assertEquals(2007, key.getYear());
		assertEquals(8, key.getUnit());

		assertTrue(key.set(n3));
		assertEquals(9999, key.getYear());
		assertEquals(12, key.getUnit());

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
	 * {@link exalge2.util.ExMonthTimeKey#set(Calendar)} のためのテスト・メソッド。
	 */
	public void testSetCalendar() {
		ExMonthTimeKey key0 = new ExMonthTimeKey(2007, 8);
		int initYear = key0.getYear();
		int initUnit = key0.getUnit();
		
		ExMonthTimeKey key1 = null;
		ExMonthTimeKey key2 = null;
		
		Calendar cal = Calendar.getInstance();
		cal.set(1, 0, 1);

		while (cal.get(Calendar.YEAR) <= 10000) {
			key1 = new ExMonthTimeKey(key0.toString());
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH) + 1;
			if (year >= key1.getMinYear() && year <= key1.getMaxYear()) {
				try {
					key2 = new ExMonthTimeKey(cal);
					boolean ret = key1.set(cal);
					if (year == initYear && month == initUnit)
						assertFalse(ret);
					else
						assertTrue(ret);
					assertEquals(year, key1.getYear());
					assertEquals(month, key1.getUnit());
					assertEquals(year, key2.getYear());
					assertEquals(month, key2.getUnit());
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
					key2 = new ExMonthTimeKey(cal);
					fail("cal[" + cal + "] key1[" + key1 + "] key2[" + key2 + "]");
				} catch (Throwable ex) {
					assertTrue(true);
				}
			}
			cal.add(Calendar.MONTH, 1);
		}
	}

	/**
	 * {@link exalge2.util.ExMonthTimeKey#getMinUnit()} のためのテスト・メソッド。
	 */
	public void testGetMinUnit() {
		ExMonthTimeKey key = new ExMonthTimeKey();
		
		assertEquals(1, key.getMinUnit());
	}

	/**
	 * {@link exalge2.util.ExMonthTimeKey#getMaxUnit()} のためのテスト・メソッド。
	 */
	public void testGetMaxUnit() {
		ExMonthTimeKey key = new ExMonthTimeKey();
		
		assertEquals(12, key.getMaxUnit());
	}

	/**
	 * {@link exalge2.util.ExMonthTimeKey#toString()} のためのテスト・メソッド。
	 */
	public void testToString() {
		final String n1 = "Y0001M01";
		final String n2 = "Y2007M08";
		final String n3 = "Y9999M12";
		
		ExMonthTimeKey key = new ExMonthTimeKey(2007,8);
		
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
		ExMonthTimeKey key = new ExMonthTimeKey(2007,8);
		assertEquals((8*10000+2007), key.hashCode());
		
		key.setUnit(12);
		assertEquals((12*10000+2007), key.hashCode());
		
		key.setYear(9999);
		assertEquals((12*10000+9999), key.hashCode());
	}

	/**
	 * {@link exalge2.util.AbExTimeKey#isValidUnit(int)} のためのテスト・メソッド。
	 */
	public void testIsValidUnit() {
		ExMonthTimeKey key = new ExMonthTimeKey();
		
		assertFalse(key.isValidUnit(0));
		assertTrue(key.isValidUnit(1));
		assertTrue(key.isValidUnit(8));
		assertTrue(key.isValidUnit(12));
		assertFalse(key.isValidUnit(13));
		assertFalse(key.isValidUnit(1997));
		assertFalse(key.isValidUnit(-10));
	}

	/**
	 * {@link exalge2.util.AbExTimeKey#getUnit()} のためのテスト・メソッド。
	 */
	public void testGetUnit() {
		ExMonthTimeKey key = new ExMonthTimeKey(2007,8);
		
		assertEquals(8, key.getUnit());

		key.setUnit(1);
		assertEquals(1, key.getUnit());
		
		key.setUnit(12);
		assertEquals(12, key.getUnit());
	}

	/**
	 * {@link exalge2.util.AbExTimeKey#setUnit(int)} のためのテスト・メソッド。
	 */
	public void testSetUnit() {
		ExMonthTimeKey key = new ExMonthTimeKey();
		
		try {
			key.setUnit(0);
			super.fail("Not error! [0]");
		} catch (IllegalArgumentException ex) {}
		
		try {
			key.setUnit(13);
			super.fail("Not error! [10000]");
		} catch (IllegalArgumentException ex) {}
		
		try {
			key.setUnit(-10);
			super.fail("Not error! [-10]");
		} catch (IllegalArgumentException ex) {}
		
		assertEquals(1, key.setUnit(1));
		assertEquals(10, key.setUnit(10));
		assertEquals(12, key.setUnit(12));
	}

	/**
	 * {@link exalge2.util.AbExTimeKey#addUnit(int)} のためのテスト・メソッド。
	 */
	public void testAddUnit() {
		ExMonthTimeKey key = new ExMonthTimeKey(2007,8);
		
		for (int i = 0; i < 100000; i++) {
			ExMonthTimeKey mtk = new ExMonthTimeKey(key.toString());
			Calendar cn = Calendar.getInstance();
			cn.set(mtk.getYear(), mtk.getUnit()-1, 1);
			cn.add(Calendar.MONTH, i);
			int year = cn.get(Calendar.YEAR);
			int month = cn.get(Calendar.MONTH) + 1;
			if (cn.get(Calendar.ERA) == java.util.GregorianCalendar.AD && year <= key.getMaxYear()) {
				try {
					int m = mtk.addUnit(i);
					//System.out.println("[" + key + "].addUnit(" + i + ") = [" + mtk + "](" + year + "/" + month + "/1)");
					assertTrue(mtk.getMinUnit() <= m && m <= mtk.getMaxUnit());
					assertTrue(year == mtk.getYear());
					assertTrue(month == m);
				}
				catch (Throwable ex) {
					fail("failed to add amount(" + i + ") to ExMonthTimeKey[" + key.toString() + "]"); 
				}
			}
			else {
				try {
					int m = mtk.addUnit(i);
					assertTrue(mtk.getMinUnit() <= m && m <= mtk.getMaxUnit());
					fail("Illegal operation to add amount(" + i + ") to ExMonthTimeKey[" + key.toString() + "]"); 
				}
				catch (Throwable ex) {
					assertTrue(true);
				}
			}
		}
		
		for (int i = -1; i > -100000; i--) {
			ExMonthTimeKey mtk = new ExMonthTimeKey(key.toString());
			Calendar cn = Calendar.getInstance();
			cn.set(mtk.getYear(), mtk.getUnit()-1, 1);
			cn.add(Calendar.MONTH, i);
			int year = cn.get(Calendar.YEAR);
			int month = cn.get(Calendar.MONTH) + 1;
			if (cn.get(Calendar.ERA) == java.util.GregorianCalendar.AD && year >= key.getMinYear()) {
				try {
					int m = mtk.addUnit(i);
					//System.out.println("[" + key + "].addUnit(" + i + ") = [" + mtk + "](" + year + "/" + month + "/1)");
					assertTrue(mtk.getMinUnit() <= m && m <= mtk.getMaxUnit());
					assertTrue(year == mtk.getYear());
					assertTrue(month == m);
				}
				catch (Throwable ex) {
					fail("failed to add amount(" + i + ") to ExMonthTimeKey[" + key.toString() + "]"); 
				}
			}
			else {
				try {
					int m = mtk.addUnit(i);
					assertTrue(mtk.getMinUnit() <= m && m <= mtk.getMaxUnit());
					fail("Illegal operation to add amount(" + i + ") to ExMonthTimeKey[" + key.toString() + "]"); 
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
		ExMonthTimeKey key1 = new ExMonthTimeKey(2007,8);
		ExMonthTimeKey key2 = new ExMonthTimeKey(2007,8);
		ExMonthTimeKey key3 = new ExMonthTimeKey(2007,9);
		ExMonthTimeKey key4 = new ExMonthTimeKey(2006,8);
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
