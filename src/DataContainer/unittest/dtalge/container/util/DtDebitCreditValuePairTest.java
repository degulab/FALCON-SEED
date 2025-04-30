/*
 * @(#)DtDebitCreditValuePairTest.java	0.2.0	2025/02/16
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.util;

import static org.junit.Assert.assertThrows;

import java.math.BigDecimal;

import exalge2.ExBase;
import junit.framework.TestCase;

/**
 * {@link DtDebitCreditValuePair} クラスのユニットテスト。
 * 
 * @author Y.Ishizuka(PieCake,Inc.)
 * 
 * @since 0.2.0
 */
public class DtDebitCreditValuePairTest extends TestCase
{
	//------------------------------------------------------------
	// Test cases
	//------------------------------------------------------------

	/**
	 * Test method for {@link dtalge.container.util.DtDebitCreditValuePair#DtDebitCreditValuePair()}.
	 */
	public void testDtDebitCreditValuePair() {
		DtDebitCreditValuePair pair;
		
		pair = new DtDebitCreditValuePair();
		assertEquals(false, pair.hasDebitItem());
		assertEquals(false, pair.hasCreditItem());
		assertEquals(false, pair.hasAnyItem());
		assertEquals(false, pair.hasBothItems());
		assertEquals(false, pair.hasItemBySide(true));
		assertEquals(false, pair.hasItemBySide(false));
		assertNull(pair.getDebitBase());
		assertNull(pair.getDebitValue());
		assertNull(pair.getCreditBase());
		assertNull(pair.getCreditValue());
	}

	/**
	 * Test method for {@link dtalge.container.util.DtDebitCreditValuePair#setItemBySide(boolean, java.math.BigDecimal, exalge2.ExBase)}.
	 */
	public void testSetItemBySideAndClear() {
		DtDebitCreditValuePair pair = new DtDebitCreditValuePair();
		
		try {
			pair.setItemBySide(true, BigDecimal.ONE, null);
			fail();
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		try {
			pair.setItemBySide(false, BigDecimal.ONE, null);
			fail();
		} catch (NullPointerException ex) {
			assertTrue(true);
		}
		
		pair.setItemBySide(true, EXP_VALUE1, EXP_EXBASE1);
		assertEquals(true, pair.hasDebitItem());
		assertEquals(false, pair.hasCreditItem());
		assertEquals(true, pair.hasAnyItem());
		assertEquals(false, pair.hasBothItems());
		assertEquals(true, pair.hasItemBySide(true));
		assertEquals(false, pair.hasItemBySide(false));
		assertEquals(EXP_EXBASE1, pair.getDebitBase());
		assertEquals(EXP_VALUE1, pair.getDebitValue());
		assertEquals(null, pair.getCreditBase());
		assertEquals(null, pair.getCreditValue());
		
		pair.setItemBySide(false, EXP_VALUE2, EXP_EXBASE2);
		assertEquals(true, pair.hasDebitItem());
		assertEquals(true, pair.hasCreditItem());
		assertEquals(true, pair.hasAnyItem());
		assertEquals(true, pair.hasBothItems());
		assertEquals(true, pair.hasItemBySide(true));
		assertEquals(true, pair.hasItemBySide(false));
		assertEquals(EXP_EXBASE1, pair.getDebitBase());
		assertEquals(EXP_VALUE1, pair.getDebitValue());
		assertEquals(EXP_EXBASE2, pair.getCreditBase());
		assertEquals(EXP_VALUE2, pair.getCreditValue());
		
		pair.clearDebitItem();
		assertEquals(false, pair.hasDebitItem());
		assertEquals(true, pair.hasCreditItem());
		assertEquals(true, pair.hasAnyItem());
		assertEquals(false, pair.hasBothItems());
		assertEquals(false, pair.hasItemBySide(true));
		assertEquals(true, pair.hasItemBySide(false));
		assertEquals(null, pair.getDebitBase());
		assertEquals(null, pair.getDebitValue());
		assertEquals(EXP_EXBASE2, pair.getCreditBase());
		assertEquals(EXP_VALUE2, pair.getCreditValue());

		pair.setDebitItem(EXP_VALUE1, EXP_EXBASE1);
		assertEquals(true, pair.hasDebitItem());
		assertEquals(true, pair.hasCreditItem());
		assertEquals(true, pair.hasAnyItem());
		assertEquals(true, pair.hasBothItems());
		assertEquals(true, pair.hasItemBySide(true));
		assertEquals(true, pair.hasItemBySide(false));
		assertEquals(EXP_EXBASE1, pair.getDebitBase());
		assertEquals(EXP_VALUE1, pair.getDebitValue());
		assertEquals(EXP_EXBASE2, pair.getCreditBase());
		assertEquals(EXP_VALUE2, pair.getCreditValue());

		pair.clearCreditItem();
		assertEquals(true, pair.hasDebitItem());
		assertEquals(false, pair.hasCreditItem());
		assertEquals(true, pair.hasAnyItem());
		assertEquals(false, pair.hasBothItems());
		assertEquals(true, pair.hasItemBySide(true));
		assertEquals(false, pair.hasItemBySide(false));
		assertEquals(EXP_EXBASE1, pair.getDebitBase());
		assertEquals(EXP_VALUE1, pair.getDebitValue());
		assertEquals(null, pair.getCreditBase());
		assertEquals(null, pair.getCreditValue());
		
		pair.setCreditItem(EXP_VALUE2, EXP_EXBASE2);
		assertEquals(true, pair.hasDebitItem());
		assertEquals(true, pair.hasCreditItem());
		assertEquals(true, pair.hasAnyItem());
		assertEquals(true, pair.hasBothItems());
		assertEquals(true, pair.hasItemBySide(true));
		assertEquals(true, pair.hasItemBySide(false));
		assertEquals(EXP_EXBASE1, pair.getDebitBase());
		assertEquals(EXP_VALUE1, pair.getDebitValue());
		assertEquals(EXP_EXBASE2, pair.getCreditBase());
		assertEquals(EXP_VALUE2, pair.getCreditValue());
		
		pair.clearAllItems();
		assertEquals(false, pair.hasDebitItem());
		assertEquals(false, pair.hasCreditItem());
		assertEquals(false, pair.hasAnyItem());
		assertEquals(false, pair.hasBothItems());
		assertEquals(false, pair.hasItemBySide(true));
		assertEquals(false, pair.hasItemBySide(false));
		assertNull(pair.getDebitBase());
		assertNull(pair.getDebitValue());
		assertNull(pair.getCreditBase());
		assertNull(pair.getCreditValue());
	}

	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final BigDecimal	EXP_VALUE1 = new BigDecimal("0.1");
	static protected final BigDecimal	EXP_VALUE2 = new BigDecimal("-0.1");
	
	static protected final ExBase	EXP_EXBASE1 = new ExBase("nameA", ExBase.NO_HAT);
	static protected final ExBase	EXP_EXBASE2 = new ExBase("nameA", ExBase.HAT);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
