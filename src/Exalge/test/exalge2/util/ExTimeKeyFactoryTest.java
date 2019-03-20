/**
 * 
 */
package exalge2.util;

import junit.framework.TestCase;

/**
 * @author ishizuka
 *
 */
public class ExTimeKeyFactoryTest extends TestCase {

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * {@link exalge2.util.ExTimeKeyFactory#createTimeKey(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testCreateTimeKey() {
		final String year1 = "Y0001";
		final String year2 = "Y2007";
		
		final String month1 = "Y0001M01";
		final String month2 = "Y2007M08";
		final String month3 = "Y9999M12";
		
		final String quat1 = "Y0001Q1";
		final String quat2 = "Y2007Q2";
		final String quat3 = "Y9999Q3";
		final String quat4 = "Y2000Q4";
		
		final String fyear1 = "FY0001";
		final String fyear2 = "FY2007";

		String[] errorString = new String[]{
				"Y0000", "Y10000", "Y2007M008", "Y2008Q03",
				"Y0000M00", "Y10000M12", "Y2007M8",
				"Y1234M", "Y1234Q",
				"FY0000", "FY1234M12", "FY1234Q3", "FY10000"
		};

		ExYearTimeKey yearKey = new ExYearTimeKey();
		ExMonthTimeKey monthKey = new ExMonthTimeKey();
		ExQuarterTimeKey quatKey = new ExQuarterTimeKey();
		ExFiscalYearTimeKey fyearKey = new ExFiscalYearTimeKey();
		ExTimeKey newKey = null;
		
		// 正常系
		//--- year
		assertTrue(yearKey.isSupported(year1));
		newKey = ExTimeKeyFactory.createTimeKey(year1);
		assertTrue(newKey instanceof ExYearTimeKey);
		assertEquals(1, newKey.getYear());
		assertEquals(1, newKey.getUnit());
		
		assertTrue(yearKey.isSupported(year2));
		newKey = ExTimeKeyFactory.createTimeKey(year2);
		assertTrue(newKey instanceof ExYearTimeKey);
		assertEquals(2007, newKey.getYear());
		assertEquals(2007, newKey.getUnit());
		
		//--- month
		assertTrue(monthKey.isSupported(month1));
		newKey = ExTimeKeyFactory.createTimeKey(month1);
		assertTrue(newKey instanceof ExMonthTimeKey);
		assertEquals(1, newKey.getYear());
		assertEquals(1, newKey.getUnit());

		assertTrue(monthKey.isSupported(month2));
		newKey = ExTimeKeyFactory.createTimeKey(month2);
		assertTrue(newKey instanceof ExMonthTimeKey);
		assertEquals(2007, newKey.getYear());
		assertEquals(8, newKey.getUnit());

		assertTrue(monthKey.isSupported(month3));
		newKey = ExTimeKeyFactory.createTimeKey(month3);
		assertTrue(newKey instanceof ExMonthTimeKey);
		assertEquals(9999, newKey.getYear());
		assertEquals(12, newKey.getUnit());
		
		//--- quarter
		assertTrue(quatKey.isSupported(quat1));
		newKey = ExTimeKeyFactory.createTimeKey(quat1);
		assertTrue(newKey instanceof ExQuarterTimeKey);
		assertEquals(1, newKey.getYear());
		assertEquals(1, newKey.getUnit());

		assertTrue(quatKey.isSupported(quat2));
		newKey = ExTimeKeyFactory.createTimeKey(quat2);
		assertTrue(newKey instanceof ExQuarterTimeKey);
		assertEquals(2007, newKey.getYear());
		assertEquals(2, newKey.getUnit());

		assertTrue(quatKey.isSupported(quat3));
		newKey = ExTimeKeyFactory.createTimeKey(quat3);
		assertTrue(newKey instanceof ExQuarterTimeKey);
		assertEquals(9999, newKey.getYear());
		assertEquals(3, newKey.getUnit());

		assertTrue(quatKey.isSupported(quat4));
		newKey = ExTimeKeyFactory.createTimeKey(quat4);
		assertTrue(newKey instanceof ExQuarterTimeKey);
		assertEquals(2000, newKey.getYear());
		assertEquals(4, newKey.getUnit());
		
		//--- fiscal year
		assertTrue(fyearKey.isSupported(fyear1));
		newKey = ExTimeKeyFactory.createTimeKey(fyear1);
		assertTrue(newKey instanceof ExFiscalYearTimeKey);
		assertEquals(1, newKey.getYear());
		assertEquals(1, newKey.getUnit());
		
		assertTrue(fyearKey.isSupported(fyear2));
		newKey = ExTimeKeyFactory.createTimeKey(fyear2);
		assertTrue(newKey instanceof ExFiscalYearTimeKey);
		assertEquals(2007, newKey.getYear());
		assertEquals(2007, newKey.getUnit());

		// 異常系
		for (int i = 0; i < errorString.length; i++) {
			try {
				newKey = ExTimeKeyFactory.createTimeKey(errorString[i]);
				super.fail("Not error occurred [" + errorString[i] + "]");
			}
			catch (Exception ex) {}
		}
	}

}
