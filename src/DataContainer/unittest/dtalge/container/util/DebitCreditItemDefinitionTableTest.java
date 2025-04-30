/*
 * @(#)DebitCreditItemDefinitionTableTest.java	0.2.0	2025/02/16
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.util;

import java.io.File;

import dtalge.exception.CsvFormatException;
import junit.framework.TestCase;

/**
 * {@link DebitCreditItemDefinitionTable} クラスのユニットテスト。
 * 
 * @author Y.Ishizuka(PieCake,Inc.)
 * 
 * @since 0.2.0
 */
public class DebitCreditItemDefinitionTableTest extends TestCase
{
	//------------------------------------------------------------
	// Test cases
	//------------------------------------------------------------

	/**
	 * Test method for {@link dtalge.container.util.DebitCreditItemDefinitionTable#DebitCreditItemDefinitionTable()}.
	 */
	public void testDebitCreditItemDefinitionTable() {
		DebitCreditItemDefinitionTable table = new DebitCreditItemDefinitionTable();
		assertEquals(true, table.isEmpty());
		assertEquals(0, table.size());
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME11));
		assertEquals(null, table.get(EXP_STR_NAME11));
	}

	/**
	 * Test method for {@link dtalge.container.util.DebitCreditItemDefinitionTable#getDebitCreditSideFromString(java.lang.String)}.
	 */
	public void testGetDebitCreditSideFromString() {
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, DebitCreditItemDefinitionTable.getDebitCreditSideFromString(null));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, DebitCreditItemDefinitionTable.getDebitCreditSideFromString(""));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, DebitCreditItemDefinitionTable.getDebitCreditSideFromString(EXP_STR_SIDE_UNKNOWN1));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, DebitCreditItemDefinitionTable.getDebitCreditSideFromString(EXP_STR_SIDE_UNKNOWN2));
		
		// debit
		assertEquals(DebitCreditItem.SIDE_DEBIT, DebitCreditItemDefinitionTable.getDebitCreditSideFromString(EXP_STR_SIDE_DEBIT1));
		assertEquals(DebitCreditItem.SIDE_DEBIT, DebitCreditItemDefinitionTable.getDebitCreditSideFromString(EXP_STR_SIDE_DEBIT2));
		assertEquals(DebitCreditItem.SIDE_DEBIT, DebitCreditItemDefinitionTable.getDebitCreditSideFromString(EXP_STR_SIDE_DEBIT3));
		assertEquals(DebitCreditItem.SIDE_DEBIT, DebitCreditItemDefinitionTable.getDebitCreditSideFromString(EXP_STR_SIDE_DEBIT4));
		assertEquals(DebitCreditItem.SIDE_DEBIT, DebitCreditItemDefinitionTable.getDebitCreditSideFromString(EXP_STR_SIDE_DEBIT5));
		
		// credit
		assertEquals(DebitCreditItem.SIDE_CREDIT, DebitCreditItemDefinitionTable.getDebitCreditSideFromString(EXP_STR_SIDE_CREDIT1));
		assertEquals(DebitCreditItem.SIDE_CREDIT, DebitCreditItemDefinitionTable.getDebitCreditSideFromString(EXP_STR_SIDE_CREDIT2));
		assertEquals(DebitCreditItem.SIDE_CREDIT, DebitCreditItemDefinitionTable.getDebitCreditSideFromString(EXP_STR_SIDE_CREDIT3));
		assertEquals(DebitCreditItem.SIDE_CREDIT, DebitCreditItemDefinitionTable.getDebitCreditSideFromString(EXP_STR_SIDE_CREDIT4));
		assertEquals(DebitCreditItem.SIDE_CREDIT, DebitCreditItemDefinitionTable.getDebitCreditSideFromString(EXP_STR_SIDE_CREDIT5));
	}

	/**
	 * Test method for {@link dtalge.container.util.DebitCreditItemDefinitionTable#put(int, java.lang.String)}.
	 * Test method for {@link dtalge.container.util.DebitCreditItemDefinitionTable#isEmpty()}.
	 * Test method for {@link dtalge.container.util.DebitCreditItemDefinitionTable#size()}.
	 * Test method for {@link dtalge.container.util.DebitCreditItemDefinitionTable#containsName(java.lang.Object)}.
	 * Test method for {@link dtalge.container.util.DebitCreditItemDefinitionTable#isDebitSide(java.lang.Object)}.
	 * Test method for {@link dtalge.container.util.DebitCreditItemDefinitionTable#isCreditSide(java.lang.Object)}.
	 * Test method for {@link dtalge.container.util.DebitCreditItemDefinitionTable#getType(java.lang.Object)}.
	 */
	public void testPutAndGet() {
		DebitCreditItemDefinitionTable table = new DebitCreditItemDefinitionTable();
		assertEquals(true, table.isEmpty());
		assertEquals(0, table.size());
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME11));
		assertEquals(null, table.get(EXP_STR_NAME11));
		
		// exception for put
		//--- debit
		try {
			table.put(DebitCreditItem.SIDE_DEBIT, null);
			fail();
		} catch (IllegalArgumentException ex) {
			assertTrue(true);
		}
		try {
			table.put(DebitCreditItem.SIDE_DEBIT, "");
			fail();
		} catch (IllegalArgumentException ex) {
			assertTrue(true);
		}
		//--- credit
		try {
			table.put(DebitCreditItem.SIDE_CREDIT, null);
			fail();
		} catch (IllegalArgumentException ex) {
			assertTrue(true);
		}
		try {
			table.put(DebitCreditItem.SIDE_CREDIT, "");
			fail();
		} catch (IllegalArgumentException ex) {
			assertTrue(true);
		}
		//--- unknown side
		try {
			table.put(DebitCreditItem.SIDE_UNKNOWN, null);
			fail();
		} catch (IllegalArgumentException ex) {
			assertTrue(true);
		}
		try {
			table.put(DebitCreditItem.SIDE_UNKNOWN, "");
			fail();
		} catch (IllegalArgumentException ex) {
			assertTrue(true);
		}
		try {
			table.put(DebitCreditItem.SIDE_UNKNOWN, EXP_STR_NAME11);
			fail();
		} catch (IllegalArgumentException ex) {
			assertTrue(true);
		}
		
		// put
		table = new DebitCreditItemDefinitionTable();
		table.isEmpty();
		assertEquals(0, table.size());
		assertEquals(false, table.containsName(EXP_STR_NAME0));
		assertEquals(false, table.containsName(EXP_STR_NAME11));
		assertEquals(false, table.containsName(EXP_STR_NAME12));
		assertEquals(false, table.containsName(EXP_STR_NAME21));
		assertEquals(false, table.containsName(EXP_STR_NAME22));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME0));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME11));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME12));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME21));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME22));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME0));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME11));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME12));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME21));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME22));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME0));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME11));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME12));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME21));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME22));
		//--- debit 1
		table.put(DebitCreditItem.SIDE_DEBIT, EXP_STR_NAME11);
		assertEquals(false, table.isEmpty());
		assertEquals(1, table.size());
		assertEquals(false, table.containsName(EXP_STR_NAME0));
		assertEquals(true, table.containsName(EXP_STR_NAME11));
		assertEquals(false, table.containsName(EXP_STR_NAME12));
		assertEquals(false, table.containsName(EXP_STR_NAME21));
		assertEquals(false, table.containsName(EXP_STR_NAME22));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME0));
		assertEquals(true, table.isDebitSide(EXP_STR_NAME11));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME12));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME21));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME22));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME0));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME11));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME12));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME21));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME22));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME0));
		assertEquals(DebitCreditItem.SIDE_DEBIT, table.getType(EXP_STR_NAME11));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME12));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME21));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME22));
		//--- debit 2
		table.put(DebitCreditItem.SIDE_DEBIT, EXP_STR_NAME12);
		assertEquals(false, table.isEmpty());
		assertEquals(2, table.size());
		assertEquals(false, table.containsName(EXP_STR_NAME0));
		assertEquals(true, table.containsName(EXP_STR_NAME11));
		assertEquals(true, table.containsName(EXP_STR_NAME12));
		assertEquals(false, table.containsName(EXP_STR_NAME21));
		assertEquals(false, table.containsName(EXP_STR_NAME22));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME0));
		assertEquals(true, table.isDebitSide(EXP_STR_NAME11));
		assertEquals(true, table.isDebitSide(EXP_STR_NAME12));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME21));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME22));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME0));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME11));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME12));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME21));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME22));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME0));
		assertEquals(DebitCreditItem.SIDE_DEBIT, table.getType(EXP_STR_NAME11));
		assertEquals(DebitCreditItem.SIDE_DEBIT, table.getType(EXP_STR_NAME12));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME21));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME22));
		//--- credit 1
		table.put(DebitCreditItem.SIDE_CREDIT, EXP_STR_NAME21);
		assertEquals(false, table.isEmpty());
		assertEquals(3, table.size());
		assertEquals(false, table.containsName(EXP_STR_NAME0));
		assertEquals(true, table.containsName(EXP_STR_NAME11));
		assertEquals(true, table.containsName(EXP_STR_NAME12));
		assertEquals(true, table.containsName(EXP_STR_NAME21));
		assertEquals(false, table.containsName(EXP_STR_NAME22));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME0));
		assertEquals(true, table.isDebitSide(EXP_STR_NAME11));
		assertEquals(true, table.isDebitSide(EXP_STR_NAME12));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME21));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME22));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME0));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME11));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME12));
		assertEquals(true, table.isCreditSide(EXP_STR_NAME21));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME22));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME0));
		assertEquals(DebitCreditItem.SIDE_DEBIT, table.getType(EXP_STR_NAME11));
		assertEquals(DebitCreditItem.SIDE_DEBIT, table.getType(EXP_STR_NAME12));
		assertEquals(DebitCreditItem.SIDE_CREDIT, table.getType(EXP_STR_NAME21));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME22));
		//--- credit 2
		table.put(DebitCreditItem.SIDE_CREDIT, EXP_STR_NAME22);
		assertEquals(false, table.isEmpty());
		assertEquals(4, table.size());
		assertEquals(false, table.containsName(EXP_STR_NAME0));
		assertEquals(true, table.containsName(EXP_STR_NAME11));
		assertEquals(true, table.containsName(EXP_STR_NAME12));
		assertEquals(true, table.containsName(EXP_STR_NAME21));
		assertEquals(true, table.containsName(EXP_STR_NAME22));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME0));
		assertEquals(true, table.isDebitSide(EXP_STR_NAME11));
		assertEquals(true, table.isDebitSide(EXP_STR_NAME12));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME21));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME22));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME0));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME11));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME12));
		assertEquals(true, table.isCreditSide(EXP_STR_NAME21));
		assertEquals(true, table.isCreditSide(EXP_STR_NAME22));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME0));
		assertEquals(DebitCreditItem.SIDE_DEBIT, table.getType(EXP_STR_NAME11));
		assertEquals(DebitCreditItem.SIDE_DEBIT, table.getType(EXP_STR_NAME12));
		assertEquals(DebitCreditItem.SIDE_CREDIT, table.getType(EXP_STR_NAME21));
		assertEquals(DebitCreditItem.SIDE_CREDIT, table.getType(EXP_STR_NAME22));
	}

	/**
	 * Test method for {@link dtalge.container.util.DebitCreditItemDefinitionTable#remove(java.lang.Object)}.
	 */
	public void testRemove() {
		// new
		DebitCreditItemDefinitionTable table = new DebitCreditItemDefinitionTable();
		table.isEmpty();
		assertEquals(0, table.size());
		assertEquals(false, table.containsName(EXP_STR_NAME0));
		assertEquals(false, table.containsName(EXP_STR_NAME11));
		assertEquals(false, table.containsName(EXP_STR_NAME12));
		assertEquals(false, table.containsName(EXP_STR_NAME21));
		assertEquals(false, table.containsName(EXP_STR_NAME22));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME0));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME11));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME12));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME21));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME22));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME0));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME11));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME12));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME21));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME22));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME0));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME11));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME12));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME21));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME22));
		
		// put
		table.put(DebitCreditItem.SIDE_CREDIT, EXP_STR_NAME22);
		table.put(DebitCreditItem.SIDE_CREDIT, EXP_STR_NAME21);
		table.put(DebitCreditItem.SIDE_DEBIT, EXP_STR_NAME12);
		table.put(DebitCreditItem.SIDE_DEBIT, EXP_STR_NAME11);
		assertEquals(false, table.isEmpty());
		assertEquals(4, table.size());
		assertEquals(false, table.containsName(EXP_STR_NAME0));
		assertEquals(true, table.containsName(EXP_STR_NAME11));
		assertEquals(true, table.containsName(EXP_STR_NAME12));
		assertEquals(true, table.containsName(EXP_STR_NAME21));
		assertEquals(true, table.containsName(EXP_STR_NAME22));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME0));
		assertEquals(true, table.isDebitSide(EXP_STR_NAME11));
		assertEquals(true, table.isDebitSide(EXP_STR_NAME12));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME21));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME22));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME0));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME11));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME12));
		assertEquals(true, table.isCreditSide(EXP_STR_NAME21));
		assertEquals(true, table.isCreditSide(EXP_STR_NAME22));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME0));
		assertEquals(DebitCreditItem.SIDE_DEBIT, table.getType(EXP_STR_NAME11));
		assertEquals(DebitCreditItem.SIDE_DEBIT, table.getType(EXP_STR_NAME12));
		assertEquals(DebitCreditItem.SIDE_CREDIT, table.getType(EXP_STR_NAME21));
		assertEquals(DebitCreditItem.SIDE_CREDIT, table.getType(EXP_STR_NAME22));
		
		// prepare exp
		DebitCreditItem expItem11 = new DebitCreditItem(DebitCreditItem.SIDE_DEBIT, EXP_STR_NAME11);
		DebitCreditItem expItem12 = new DebitCreditItem(DebitCreditItem.SIDE_DEBIT, EXP_STR_NAME12);
		DebitCreditItem expItem21 = new DebitCreditItem(DebitCreditItem.SIDE_CREDIT, EXP_STR_NAME21);
		DebitCreditItem expItem22 = new DebitCreditItem(DebitCreditItem.SIDE_CREDIT, EXP_STR_NAME22);
		
		// remove
		DebitCreditItem ret;
		//--- remove unknown
		ret = table.remove("hoge");
		assertNull(ret);
		//--- debit 1
		ret = table.remove(EXP_STR_NAME11);
		assertEquals(false, table.isEmpty());
		assertEquals(3, table.size());
		assertEquals(false, table.containsName(EXP_STR_NAME0));
		assertEquals(false, table.containsName(EXP_STR_NAME11));
		assertEquals(true, table.containsName(EXP_STR_NAME12));
		assertEquals(true, table.containsName(EXP_STR_NAME21));
		assertEquals(true, table.containsName(EXP_STR_NAME22));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME0));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME11));
		assertEquals(true, table.isDebitSide(EXP_STR_NAME12));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME21));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME22));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME0));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME11));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME12));
		assertEquals(true, table.isCreditSide(EXP_STR_NAME21));
		assertEquals(true, table.isCreditSide(EXP_STR_NAME22));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME0));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME11));
		assertEquals(DebitCreditItem.SIDE_DEBIT, table.getType(EXP_STR_NAME12));
		assertEquals(DebitCreditItem.SIDE_CREDIT, table.getType(EXP_STR_NAME21));
		assertEquals(DebitCreditItem.SIDE_CREDIT, table.getType(EXP_STR_NAME22));
		assertNotNull(ret);
		assertEquals(true, ret.equals(expItem11));
		assertEquals(false, ret.equals(expItem12));
		assertEquals(false, ret.equals(expItem21));
		assertEquals(false, ret.equals(expItem22));
		//--- debit 2
		ret = table.remove(EXP_STR_NAME12);
		assertEquals(false, table.isEmpty());
		assertEquals(2, table.size());
		assertEquals(false, table.containsName(EXP_STR_NAME0));
		assertEquals(false, table.containsName(EXP_STR_NAME11));
		assertEquals(false, table.containsName(EXP_STR_NAME12));
		assertEquals(true, table.containsName(EXP_STR_NAME21));
		assertEquals(true, table.containsName(EXP_STR_NAME22));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME0));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME11));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME12));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME21));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME22));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME0));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME11));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME12));
		assertEquals(true, table.isCreditSide(EXP_STR_NAME21));
		assertEquals(true, table.isCreditSide(EXP_STR_NAME22));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME0));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME11));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME12));
		assertEquals(DebitCreditItem.SIDE_CREDIT, table.getType(EXP_STR_NAME21));
		assertEquals(DebitCreditItem.SIDE_CREDIT, table.getType(EXP_STR_NAME22));
		assertNotNull(ret);
		assertEquals(false, ret.equals(expItem11));
		assertEquals(true, ret.equals(expItem12));
		assertEquals(false, ret.equals(expItem21));
		assertEquals(false, ret.equals(expItem22));
		//--- credit 1
		ret = table.remove(EXP_STR_NAME21);
		assertEquals(false, table.isEmpty());
		assertEquals(1, table.size());
		assertEquals(false, table.containsName(EXP_STR_NAME0));
		assertEquals(false, table.containsName(EXP_STR_NAME11));
		assertEquals(false, table.containsName(EXP_STR_NAME12));
		assertEquals(false, table.containsName(EXP_STR_NAME21));
		assertEquals(true, table.containsName(EXP_STR_NAME22));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME0));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME11));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME12));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME21));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME22));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME0));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME11));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME12));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME21));
		assertEquals(true, table.isCreditSide(EXP_STR_NAME22));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME0));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME11));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME12));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME21));
		assertEquals(DebitCreditItem.SIDE_CREDIT, table.getType(EXP_STR_NAME22));
		assertNotNull(ret);
		assertEquals(false, ret.equals(expItem11));
		assertEquals(false, ret.equals(expItem12));
		assertEquals(true, ret.equals(expItem21));
		assertEquals(false, ret.equals(expItem22));
		//--- credit 2
		ret = table.remove(EXP_STR_NAME22);
		assertEquals(true, table.isEmpty());
		assertEquals(0, table.size());
		assertEquals(false, table.containsName(EXP_STR_NAME0));
		assertEquals(false, table.containsName(EXP_STR_NAME11));
		assertEquals(false, table.containsName(EXP_STR_NAME12));
		assertEquals(false, table.containsName(EXP_STR_NAME21));
		assertEquals(false, table.containsName(EXP_STR_NAME22));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME0));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME11));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME12));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME21));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME22));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME0));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME11));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME12));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME21));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME22));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME0));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME11));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME12));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME21));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME22));
		assertNotNull(ret);
		assertEquals(false, ret.equals(expItem11));
		assertEquals(false, ret.equals(expItem12));
		assertEquals(false, ret.equals(expItem21));
		assertEquals(true, ret.equals(expItem22));
	}

	/**
	 * Test method for {@link dtalge.container.util.DebitCreditItemDefinitionTable#clear()}.
	 */
	public void testClear() {
		// new
		DebitCreditItemDefinitionTable table = new DebitCreditItemDefinitionTable();
		table.isEmpty();
		assertEquals(0, table.size());
		assertEquals(false, table.containsName(EXP_STR_NAME0));
		assertEquals(false, table.containsName(EXP_STR_NAME11));
		assertEquals(false, table.containsName(EXP_STR_NAME12));
		assertEquals(false, table.containsName(EXP_STR_NAME21));
		assertEquals(false, table.containsName(EXP_STR_NAME22));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME0));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME11));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME12));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME21));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME22));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME0));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME11));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME12));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME21));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME22));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME0));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME11));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME12));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME21));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME22));
		
		// clear
		table.clear();
		table.isEmpty();
		assertEquals(0, table.size());
		assertEquals(false, table.containsName(EXP_STR_NAME0));
		assertEquals(false, table.containsName(EXP_STR_NAME11));
		assertEquals(false, table.containsName(EXP_STR_NAME12));
		assertEquals(false, table.containsName(EXP_STR_NAME21));
		assertEquals(false, table.containsName(EXP_STR_NAME22));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME0));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME11));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME12));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME21));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME22));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME0));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME11));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME12));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME21));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME22));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME0));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME11));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME12));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME21));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME22));
		
		// put
		table.put(DebitCreditItem.SIDE_CREDIT, EXP_STR_NAME22);
		table.put(DebitCreditItem.SIDE_CREDIT, EXP_STR_NAME21);
		table.put(DebitCreditItem.SIDE_DEBIT, EXP_STR_NAME12);
		table.put(DebitCreditItem.SIDE_DEBIT, EXP_STR_NAME11);
		assertEquals(false, table.isEmpty());
		assertEquals(4, table.size());
		assertEquals(false, table.containsName(EXP_STR_NAME0));
		assertEquals(true, table.containsName(EXP_STR_NAME11));
		assertEquals(true, table.containsName(EXP_STR_NAME12));
		assertEquals(true, table.containsName(EXP_STR_NAME21));
		assertEquals(true, table.containsName(EXP_STR_NAME22));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME0));
		assertEquals(true, table.isDebitSide(EXP_STR_NAME11));
		assertEquals(true, table.isDebitSide(EXP_STR_NAME12));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME21));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME22));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME0));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME11));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME12));
		assertEquals(true, table.isCreditSide(EXP_STR_NAME21));
		assertEquals(true, table.isCreditSide(EXP_STR_NAME22));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME0));
		assertEquals(DebitCreditItem.SIDE_DEBIT, table.getType(EXP_STR_NAME11));
		assertEquals(DebitCreditItem.SIDE_DEBIT, table.getType(EXP_STR_NAME12));
		assertEquals(DebitCreditItem.SIDE_CREDIT, table.getType(EXP_STR_NAME21));
		assertEquals(DebitCreditItem.SIDE_CREDIT, table.getType(EXP_STR_NAME22));
		
		// clear
		table.clear();
		table.isEmpty();
		assertEquals(0, table.size());
		assertEquals(false, table.containsName(EXP_STR_NAME0));
		assertEquals(false, table.containsName(EXP_STR_NAME11));
		assertEquals(false, table.containsName(EXP_STR_NAME12));
		assertEquals(false, table.containsName(EXP_STR_NAME21));
		assertEquals(false, table.containsName(EXP_STR_NAME22));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME0));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME11));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME12));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME21));
		assertEquals(false, table.isDebitSide(EXP_STR_NAME22));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME0));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME11));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME12));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME21));
		assertEquals(false, table.isCreditSide(EXP_STR_NAME22));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME0));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME11));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME12));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME21));
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, table.getType(EXP_STR_NAME22));
	}

	/**
	 * Test method for {@link dtalge.container.util.DebitCreditItemDefinitionTable#get(java.lang.Object)}.
	 */
	public void testGet() {
		// new
		DebitCreditItemDefinitionTable table = new DebitCreditItemDefinitionTable();
		table.isEmpty();
		assertEquals(null, table.get(EXP_STR_NAME0));
		assertEquals(null, table.get(EXP_STR_NAME11));
		assertEquals(null, table.get(EXP_STR_NAME12));
		assertEquals(null, table.get(EXP_STR_NAME21));
		assertEquals(null, table.get(EXP_STR_NAME22));
		
		// prepare exp
		DebitCreditItem expItem11 = new DebitCreditItem(DebitCreditItem.SIDE_DEBIT, EXP_STR_NAME11);
		DebitCreditItem expItem12 = new DebitCreditItem(DebitCreditItem.SIDE_DEBIT, EXP_STR_NAME12);
		DebitCreditItem expItem21 = new DebitCreditItem(DebitCreditItem.SIDE_CREDIT, EXP_STR_NAME21);
		DebitCreditItem expItem22 = new DebitCreditItem(DebitCreditItem.SIDE_CREDIT, EXP_STR_NAME22);
		
		// put
		table.put(DebitCreditItem.SIDE_CREDIT, EXP_STR_NAME22);
		table.put(DebitCreditItem.SIDE_CREDIT, EXP_STR_NAME21);
		table.put(DebitCreditItem.SIDE_DEBIT, EXP_STR_NAME12);
		table.put(DebitCreditItem.SIDE_DEBIT, EXP_STR_NAME11);
		assertEquals(false, table.isEmpty());
		assertEquals(4, table.size());
		
		// get
		DebitCreditItem ret;
		//--- unknown
		ret = table.get("hoge");
		assertNull(ret);
		//--- debit 1
		ret = table.get(EXP_STR_NAME11);
		assertEquals(expItem11, ret);
		//--- debit 2
		ret = table.get(EXP_STR_NAME12);
		assertEquals(expItem12, ret);
		//--- debit 3
		ret = table.get(EXP_STR_NAME21);
		assertEquals(expItem21, ret);
		//--- debit 4
		ret = table.get(EXP_STR_NAME22);
		assertEquals(expItem22, ret);
	}
	
	/**
	 * Test method for {@link dtalge.container.util.DebitCreditItemDefinitionTable#equals(Object)}.
	 */
	public void testEquals() {
		// new
		DebitCreditItemDefinitionTable tableEmpty1 = new DebitCreditItemDefinitionTable();
		DebitCreditItemDefinitionTable tableEmpty2 = new DebitCreditItemDefinitionTable();
		DebitCreditItemDefinitionTable tableHasEntries1 = new DebitCreditItemDefinitionTable();
		DebitCreditItemDefinitionTable tableHasEntries2 = new DebitCreditItemDefinitionTable();

		// prepare exp
		DebitCreditItem expItem11 = new DebitCreditItem(DebitCreditItem.SIDE_DEBIT, EXP_STR_NAME11);
		DebitCreditItem expItem12 = new DebitCreditItem(DebitCreditItem.SIDE_DEBIT, EXP_STR_NAME12);
		DebitCreditItem expItem21 = new DebitCreditItem(DebitCreditItem.SIDE_CREDIT, EXP_STR_NAME21);
		DebitCreditItem expItem22 = new DebitCreditItem(DebitCreditItem.SIDE_CREDIT, EXP_STR_NAME22);
		tableHasEntries1.put(DebitCreditItem.SIDE_CREDIT, EXP_STR_NAME22);
		tableHasEntries1.put(DebitCreditItem.SIDE_CREDIT, EXP_STR_NAME21);
		tableHasEntries1.put(DebitCreditItem.SIDE_DEBIT, EXP_STR_NAME12);
		tableHasEntries1.put(DebitCreditItem.SIDE_DEBIT, EXP_STR_NAME11);
		tableHasEntries2.put(DebitCreditItem.SIDE_CREDIT, EXP_STR_NAME22);
		tableHasEntries2.put(DebitCreditItem.SIDE_CREDIT, EXP_STR_NAME21);
		tableHasEntries2.put(DebitCreditItem.SIDE_DEBIT, EXP_STR_NAME12);
		tableHasEntries2.put(DebitCreditItem.SIDE_DEBIT, EXP_STR_NAME11);
		
		// empty
		assertEquals(true, tableEmpty1.equals(tableEmpty1));
		assertEquals(true, tableEmpty1.equals(tableEmpty2));
		assertEquals(false, tableEmpty1.equals(tableHasEntries1));
		assertEquals(false, tableEmpty1.equals(tableHasEntries2));

		assertEquals(true, tableEmpty2.equals(tableEmpty1));
		assertEquals(true, tableEmpty2.equals(tableEmpty2));
		assertEquals(false, tableEmpty2.equals(tableHasEntries1));
		assertEquals(false, tableEmpty2.equals(tableHasEntries2));

		assertEquals(false, tableHasEntries1.equals(tableEmpty1));
		assertEquals(false, tableHasEntries1.equals(tableEmpty2));
		assertEquals(true, tableHasEntries1.equals(tableHasEntries1));
		assertEquals(true, tableHasEntries1.equals(tableHasEntries2));

		assertEquals(false, tableHasEntries2.equals(tableEmpty1));
		assertEquals(false, tableHasEntries2.equals(tableEmpty2));
		assertEquals(true, tableHasEntries2.equals(tableHasEntries1));
		assertEquals(true, tableHasEntries2.equals(tableHasEntries2));
	}
	
	/**
	 * Test method for {@link dtalge.container.util.DebitCreditItemDefinitionTable#toCSV(java.io.File)}.
	 * Test method for {@link dtalge.container.util.DebitCreditItemDefinitionTable#fromCSV(java.io.File)}.
	 */
	public void testToCsvAndFromCsvDependsPlatform() throws Throwable
	{
		DebitCreditItemDefinitionTable srcTable = new DebitCreditItemDefinitionTable();
		DebitCreditItemDefinitionTable dstTable = null;
		
		// empty
		assertEquals(true, srcTable.isEmpty());
		File emptyCsvFile = new File(csvpath, PATH_PF_OUT_EMPTY_TABLE_CSV);
		//--- write to CSV
		srcTable.toCSV(emptyCsvFile);
		//--- read from CSV
		dstTable = DebitCreditItemDefinitionTable.fromCSV(emptyCsvFile);
		//--- compare
		assertEquals(true, dstTable.isEmpty());
		assertEquals(srcTable, dstTable);
		
		// has entries
		srcTable.put(DebitCreditItem.SIDE_CREDIT, EXP_STR_NAME22);
		srcTable.put(DebitCreditItem.SIDE_CREDIT, EXP_STR_NAME21);
		srcTable.put(DebitCreditItem.SIDE_DEBIT, EXP_STR_NAME12);
		srcTable.put(DebitCreditItem.SIDE_DEBIT, EXP_STR_NAME11);
		assertEquals(false, srcTable.isEmpty());
		File hasEntriesCsvFile = new File(csvpath, PATH_PF_OUT_VALID_TABLE_CSV);
		//--- write to CSV
		srcTable.toCSV(hasEntriesCsvFile);
		//--- read from CSV
		dstTable = DebitCreditItemDefinitionTable.fromCSV(hasEntriesCsvFile);
		//--- compare
		assertEquals(false, dstTable.isEmpty());
		assertEquals(srcTable, dstTable);
	}

	/**
	 * Test method for {@link dtalge.container.util.DebitCreditItemDefinitionTable#toCSV(java.io.File, java.lang.String)}.
	 * Test method for {@link dtalge.container.util.DebitCreditItemDefinitionTable#fromCSV(java.io.File, java.lang.String)}.
	 */
	public void testToCsvAndFromCsvWithCharsetName() throws Throwable
	{
		DebitCreditItemDefinitionTable srcTable = new DebitCreditItemDefinitionTable();
		DebitCreditItemDefinitionTable dstTable = null;
		
		// SJIS: empty
		assertEquals(true, srcTable.isEmpty());
		File emptyCsvFile = new File(csvpath, PATH_SJIS_OUT_EMPTY_TABLE_CSV);
		//--- write to CSV
		srcTable.toCSV(emptyCsvFile);
		//--- read from CSV
		dstTable = DebitCreditItemDefinitionTable.fromCSV(emptyCsvFile, SJIS);
		//--- compare
		assertEquals(true, dstTable.isEmpty());
		assertEquals(srcTable, dstTable);
		
		// SJIS: has entries
		srcTable.put(DebitCreditItem.SIDE_CREDIT, EXP_STR_NAME22);
		srcTable.put(DebitCreditItem.SIDE_CREDIT, EXP_STR_NAME21);
		srcTable.put(DebitCreditItem.SIDE_DEBIT, EXP_STR_NAME12);
		srcTable.put(DebitCreditItem.SIDE_DEBIT, EXP_STR_NAME11);
		assertEquals(false, srcTable.isEmpty());
		File hasEntriesCsvFile = new File(csvpath, PATH_SJIS_OUT_VALID_TABLE_CSV);
		//--- write to CSV
		srcTable.toCSV(hasEntriesCsvFile);
		//--- read from CSV
		dstTable = DebitCreditItemDefinitionTable.fromCSV(hasEntriesCsvFile, SJIS);
		//--- compare
		assertEquals(false, dstTable.isEmpty());
		assertEquals(srcTable, dstTable);

		
		// UTF-8: empty
		srcTable.clear();
		assertEquals(true, srcTable.isEmpty());
		emptyCsvFile = new File(csvpath, PATH_UTF8_OUT_EMPTY_TABLE_CSV);
		//--- write to CSV
		srcTable.toCSV(emptyCsvFile);
		//--- read from CSV
		dstTable = DebitCreditItemDefinitionTable.fromCSV(emptyCsvFile, UTF8);
		//--- compare
		assertEquals(true, dstTable.isEmpty());
		assertEquals(srcTable, dstTable);
		
		// UTF-8: has entries
		srcTable.put(DebitCreditItem.SIDE_CREDIT, EXP_STR_NAME22);
		srcTable.put(DebitCreditItem.SIDE_CREDIT, EXP_STR_NAME21);
		srcTable.put(DebitCreditItem.SIDE_DEBIT, EXP_STR_NAME12);
		srcTable.put(DebitCreditItem.SIDE_DEBIT, EXP_STR_NAME11);
		assertEquals(false, srcTable.isEmpty());
		hasEntriesCsvFile = new File(csvpath, PATH_UTF8_OUT_VALID_TABLE_CSV);
		//--- write to CSV
		srcTable.toCSV(hasEntriesCsvFile);
		//--- read from CSV
		dstTable = DebitCreditItemDefinitionTable.fromCSV(hasEntriesCsvFile, UTF8);
		//--- compare
		assertEquals(false, dstTable.isEmpty());
		assertEquals(srcTable, dstTable);
	}
	
	public void testFromCsvUsingValidCsvFile() throws Throwable
	{
		// exp
		DebitCreditItemDefinitionTable expTable = new DebitCreditItemDefinitionTable();
		//--- #借方:資産
		expTable.put(DebitCreditItem.SIDE_DEBIT, "商品");
		expTable.put(DebitCreditItem.SIDE_DEBIT, "現金");
		//expTable.put(DebitCreditItem.SIDE_DEBIT, "受取手形");
		expTable.put(DebitCreditItem.SIDE_DEBIT, "売掛金");
		//--- #借方:費用
		expTable.put(DebitCreditItem.SIDE_DEBIT, "売上原価");
		expTable.put(DebitCreditItem.SIDE_DEBIT, "減価償却費");
		expTable.put(DebitCreditItem.SIDE_DEBIT, "リース代");
		expTable.put(DebitCreditItem.SIDE_DEBIT, "特別損失");
		//expTable.put(DebitCreditItem.SIDE_DEBIT, "手形売却損");
		//--- #貸方:負債
		expTable.put(DebitCreditItem.SIDE_CREDIT, "買掛金");
		//--- #貸方:収益
		expTable.put(DebitCreditItem.SIDE_CREDIT, "売上");
		expTable.put(DebitCreditItem.SIDE_CREDIT, "売上総利益");
		//expTable.put(DebitCreditItem.SIDE_CREDIT, "支払手形");
		expTable.put(DebitCreditItem.SIDE_CREDIT, "利息");
		expTable.put(DebitCreditItem.SIDE_CREDIT, "受取利息");
		expTable.put(DebitCreditItem.SIDE_CREDIT, "営業利益");
		expTable.put(DebitCreditItem.SIDE_CREDIT, "経常利益");
		expTable.put(DebitCreditItem.SIDE_CREDIT, "当期純利益");
		
		// file
		File csvfile = new File(csvpath, "input_valid_table_SJIS.csv");
		//--- read from CSV
		DebitCreditItemDefinitionTable dstTable = DebitCreditItemDefinitionTable.fromCSV(csvfile, SJIS);
		//--- test
		assertEquals(expTable, dstTable);
	}
	
	public void testFromCsvUsingInvalidCsvFile() throws Throwable
	{
		// files
		File csvFileInvalidTypeName = new File(csvpath, "input_invalid_table_unknowntype_SJIS.csv");
		File csvFileInvalidNoName = new File(csvpath, "input_invalid_table_noname_SJIS.csv");
		File csvFileInvalidNoType = new File(csvpath, "input_invalid_table_notype_SJIS.csv");
		
		// invalid type name
		try {
			DebitCreditItemDefinitionTable.fromCSV(csvFileInvalidTypeName, SJIS);
			fail();
		}
		catch (CsvFormatException ex) {
			ex.printStackTrace();
			assertTrue(true);
		}
		
		// invalid no name
		try {
			DebitCreditItemDefinitionTable.fromCSV(csvFileInvalidNoName, SJIS);
			fail();
		}
		catch (CsvFormatException ex) {
			ex.printStackTrace();
			assertTrue(true);
		}
		
		// invalid no type
		try {
			DebitCreditItemDefinitionTable.fromCSV(csvFileInvalidNoType, SJIS);
			fail();
		}
		catch (CsvFormatException ex) {
			ex.printStackTrace();
			assertTrue(true);
		}
	}

	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final File csvpath = new File("testdata/DebitCreditItemDefinitionTable");
	static protected final String SJIS = "MS932";
	static protected final String UTF8 = "UTF-8";
	static protected final String PATH_PF_OUT_EMPTY_TABLE_CSV = "out_empty_table.csv";
	static protected final String PATH_SJIS_OUT_EMPTY_TABLE_CSV = "out_empty_table_" + SJIS + ".csv";
	static protected final String PATH_UTF8_OUT_EMPTY_TABLE_CSV = "out_empty_table_" + UTF8 + ".csv";
	static protected final String PATH_PF_OUT_VALID_TABLE_CSV = "out_valid_table.csv";
	static protected final String PATH_SJIS_OUT_VALID_TABLE_CSV = "out_valid_table_" + SJIS + ".csv";
	static protected final String PATH_UTF8_OUT_VALID_TABLE_CSV = "out_valid_table_" + UTF8 + ".csv";
	
	static protected final String EXP_STR_SIDE_UNKNOWN1 = "Deb";
	static protected final String EXP_STR_SIDE_UNKNOWN2 = "Cre";
	static protected final String EXP_STR_SIDE_DEBIT1 = "Debit";
	static protected final String EXP_STR_SIDE_DEBIT2 = "Dr";
	static protected final String EXP_STR_SIDE_DEBIT3 = "Dr.";
	static protected final String EXP_STR_SIDE_DEBIT4 = "借方";
	static protected final String EXP_STR_SIDE_DEBIT5 = "借";
	static protected final String EXP_STR_SIDE_CREDIT1 = "Credit";
	static protected final String EXP_STR_SIDE_CREDIT2 = "Cr";
	static protected final String EXP_STR_SIDE_CREDIT3 = "Cr.";
	static protected final String EXP_STR_SIDE_CREDIT4 = "貸方";
	static protected final String EXP_STR_SIDE_CREDIT5 = "貸";
	
	static protected final String EXP_STR_NAME0 = "";
	static protected final String EXP_STR_NAME11 = "DebitNameKey";
	static protected final String EXP_STR_NAME12 = "Debit!Name$Key";
	static protected final String EXP_STR_NAME21 = "CreditNameKey";
	static protected final String EXP_STR_NAME22 = "Credit!Name$Key";

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
