/*
 * @(#)DebitCreditItemTest.java	0.2.0	2025/02/16
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.util;

import junit.framework.TestCase;

/**
 * {@link DebitCreditItem} クラスのユニットテスト。
 * 
 * @author Y.Ishizuka(PieCake,Inc.)
 * 
 * @since 0.2.0
 */
public class DebitCreditItemTest extends TestCase
{
	//------------------------------------------------------------
	// Test cases
	//------------------------------------------------------------

	/**
	 * Test method for {@link dtalge.container.util.DebitCreditItem#DebitCreditItem(int)}.
	 */
	public void testDebitCreditItemInt() {
		DebitCreditItem item;
		
		item = new DebitCreditItem(DebitCreditItem.SIDE_UNKNOWN);
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, item.getType());
		assertNull(item.getNameKey());
		
		item = new DebitCreditItem(DebitCreditItem.SIDE_DEBIT);
		assertEquals(DebitCreditItem.SIDE_DEBIT, item.getType());
		assertNull(item.getNameKey());
		
		item = new DebitCreditItem(DebitCreditItem.SIDE_CREDIT);
		assertEquals(DebitCreditItem.SIDE_CREDIT, item.getType());
		assertNull(item.getNameKey());
	}

	/**
	 * Test method for {@link dtalge.container.util.DebitCreditItem#DebitCreditItem(int, java.lang.String)}.
	 */
	public void testDebitCreditItemIntString() {
		
		DebitCreditItem item;
		
		item = new DebitCreditItem(DebitCreditItem.SIDE_UNKNOWN, EXP_NAMEKEY_STR);
		assertEquals(DebitCreditItem.SIDE_UNKNOWN, item.getType());
		assertEquals(EXP_NAMEKEY_STR, item.getNameKey());
		assertEquals(false, item.isDebitSide());
		assertEquals(false, item.isCreditSide());
		assertEquals(false, item.isEmptyNameKey());
		
		item = new DebitCreditItem(DebitCreditItem.SIDE_DEBIT, EXP_NAMEKEY_STR);
		assertEquals(DebitCreditItem.SIDE_DEBIT, item.getType());
		assertEquals(EXP_NAMEKEY_STR, item.getNameKey());
		assertEquals(true, item.isDebitSide());
		assertEquals(false, item.isCreditSide());
		assertEquals(false, item.isEmptyNameKey());
		
		item = new DebitCreditItem(DebitCreditItem.SIDE_CREDIT, EXP_NAMEKEY_STR);
		assertEquals(DebitCreditItem.SIDE_CREDIT, item.getType());
		assertEquals(EXP_NAMEKEY_STR, item.getNameKey());
		assertEquals(false, item.isDebitSide());
		assertEquals(true, item.isCreditSide());
		assertEquals(false, item.isEmptyNameKey());
		
		item = new DebitCreditItem(DebitCreditItem.SIDE_CREDIT, null);
		assertEquals(DebitCreditItem.SIDE_CREDIT, item.getType());
		assertNull(item.getNameKey());
		assertEquals(false, item.isDebitSide());
		assertEquals(true, item.isCreditSide());
		assertEquals(true, item.isEmptyNameKey());
		
		item = new DebitCreditItem(DebitCreditItem.SIDE_CREDIT, EXP_NAMEKEY_EMPTY);
		assertEquals(DebitCreditItem.SIDE_CREDIT, item.getType());
		assertEquals(EXP_NAMEKEY_EMPTY, item.getNameKey());
		assertEquals(false, item.isDebitSide());
		assertEquals(true, item.isCreditSide());
		assertEquals(true, item.isEmptyNameKey());
		
		item = new DebitCreditItem(DebitCreditItem.SIDE_CREDIT, EXP_NAMEKEY_INCLUDE_INVALIDBASEKEY);
		assertEquals(DebitCreditItem.SIDE_CREDIT, item.getType());
		assertEquals(EXP_NAMEKEY_INCLUDE_INVALIDBASEKEY, item.getNameKey());
		assertEquals(false, item.isDebitSide());
		assertEquals(true, item.isCreditSide());
		assertEquals(false, item.isEmptyNameKey());
	}

	/**
	 * Test method for {@link dtalge.container.util.DebitCreditItem#hashCode()}.
	 */
	public void testHashCodeEquals() {
		
		DebitCreditItem item00 = new DebitCreditItem(DebitCreditItem.SIDE_UNKNOWN, null);
		int expHash00 = calcHashCode(DebitCreditItem.SIDE_UNKNOWN, null);
		DebitCreditItem item01 = new DebitCreditItem(DebitCreditItem.SIDE_UNKNOWN, EXP_NAMEKEY_EMPTY);
		int expHash01 = calcHashCode(DebitCreditItem.SIDE_UNKNOWN, EXP_NAMEKEY_EMPTY);
		DebitCreditItem item02 = new DebitCreditItem(DebitCreditItem.SIDE_UNKNOWN, EXP_NAMEKEY_INCLUDE_INVALIDBASEKEY);
		int expHash02 = calcHashCode(DebitCreditItem.SIDE_UNKNOWN, EXP_NAMEKEY_INCLUDE_INVALIDBASEKEY);
		DebitCreditItem item03 = new DebitCreditItem(DebitCreditItem.SIDE_UNKNOWN, null);
		int expHash03 = calcHashCode(DebitCreditItem.SIDE_UNKNOWN, null);
		DebitCreditItem item04 = new DebitCreditItem(DebitCreditItem.SIDE_UNKNOWN, EXP_NAMEKEY_EMPTY);
		int expHash04 = calcHashCode(DebitCreditItem.SIDE_UNKNOWN, EXP_NAMEKEY_EMPTY);
		DebitCreditItem item05 = new DebitCreditItem(DebitCreditItem.SIDE_UNKNOWN, EXP_NAMEKEY_INCLUDE_INVALIDBASEKEY);
		int expHash05 = calcHashCode(DebitCreditItem.SIDE_UNKNOWN, EXP_NAMEKEY_INCLUDE_INVALIDBASEKEY);

		DebitCreditItem item10 = new DebitCreditItem(DebitCreditItem.SIDE_DEBIT, null);
		int expHash10 = calcHashCode(DebitCreditItem.SIDE_DEBIT, null);
		DebitCreditItem item11 = new DebitCreditItem(DebitCreditItem.SIDE_DEBIT, EXP_NAMEKEY_EMPTY);
		int expHash11 = calcHashCode(DebitCreditItem.SIDE_DEBIT, EXP_NAMEKEY_EMPTY);
		DebitCreditItem item12 = new DebitCreditItem(DebitCreditItem.SIDE_DEBIT, EXP_NAMEKEY_INCLUDE_INVALIDBASEKEY);
		int expHash12 = calcHashCode(DebitCreditItem.SIDE_DEBIT, EXP_NAMEKEY_INCLUDE_INVALIDBASEKEY);
		DebitCreditItem item13 = new DebitCreditItem(DebitCreditItem.SIDE_DEBIT, null);
		int expHash13 = calcHashCode(DebitCreditItem.SIDE_DEBIT, null);
		DebitCreditItem item14 = new DebitCreditItem(DebitCreditItem.SIDE_DEBIT, EXP_NAMEKEY_EMPTY);
		int expHash14 = calcHashCode(DebitCreditItem.SIDE_DEBIT, EXP_NAMEKEY_EMPTY);
		DebitCreditItem item15 = new DebitCreditItem(DebitCreditItem.SIDE_DEBIT, EXP_NAMEKEY_INCLUDE_INVALIDBASEKEY);
		int expHash15 = calcHashCode(DebitCreditItem.SIDE_DEBIT, EXP_NAMEKEY_INCLUDE_INVALIDBASEKEY);

		DebitCreditItem item20 = new DebitCreditItem(DebitCreditItem.SIDE_CREDIT, null);
		int expHash20 = calcHashCode(DebitCreditItem.SIDE_CREDIT, null);
		DebitCreditItem item21 = new DebitCreditItem(DebitCreditItem.SIDE_CREDIT, EXP_NAMEKEY_EMPTY);
		int expHash21 = calcHashCode(DebitCreditItem.SIDE_CREDIT, EXP_NAMEKEY_EMPTY);
		DebitCreditItem item22 = new DebitCreditItem(DebitCreditItem.SIDE_CREDIT, EXP_NAMEKEY_INCLUDE_INVALIDBASEKEY);
		int expHash22 = calcHashCode(DebitCreditItem.SIDE_CREDIT, EXP_NAMEKEY_INCLUDE_INVALIDBASEKEY);
		DebitCreditItem item23 = new DebitCreditItem(DebitCreditItem.SIDE_CREDIT, null);
		int expHash23 = calcHashCode(DebitCreditItem.SIDE_CREDIT, null);
		DebitCreditItem item24 = new DebitCreditItem(DebitCreditItem.SIDE_CREDIT, EXP_NAMEKEY_EMPTY);
		int expHash24 = calcHashCode(DebitCreditItem.SIDE_CREDIT, EXP_NAMEKEY_EMPTY);
		DebitCreditItem item25 = new DebitCreditItem(DebitCreditItem.SIDE_CREDIT, EXP_NAMEKEY_INCLUDE_INVALIDBASEKEY);
		int expHash25 = calcHashCode(DebitCreditItem.SIDE_CREDIT, EXP_NAMEKEY_INCLUDE_INVALIDBASEKEY);
		
		// hashCode
		assertEquals(expHash00, item00.hashCode());
		assertEquals(expHash01, item01.hashCode());
		assertEquals(expHash02, item02.hashCode());
		assertEquals(expHash03, item03.hashCode());
		assertEquals(expHash04, item04.hashCode());
		assertEquals(expHash05, item05.hashCode());

		assertEquals(expHash10, item10.hashCode());
		assertEquals(expHash11, item11.hashCode());
		assertEquals(expHash12, item12.hashCode());
		assertEquals(expHash13, item13.hashCode());
		assertEquals(expHash14, item14.hashCode());
		assertEquals(expHash15, item15.hashCode());

		assertEquals(expHash20, item20.hashCode());
		assertEquals(expHash21, item21.hashCode());
		assertEquals(expHash22, item22.hashCode());
		assertEquals(expHash23, item23.hashCode());
		assertEquals(expHash24, item24.hashCode());
		assertEquals(expHash25, item25.hashCode());

		// equals
		assertEquals(true, item00.equals(item00));
		assertEquals(false, item00.equals(item01));
		assertEquals(false, item00.equals(item02));
		assertEquals(true, item00.equals(item03));
		assertEquals(false, item00.equals(item04));
		assertEquals(false, item00.equals(item05));
		assertEquals(false, item00.equals(item10));
		assertEquals(false, item00.equals(item11));
		assertEquals(false, item00.equals(item12));
		assertEquals(false, item00.equals(item13));
		assertEquals(false, item00.equals(item14));
		assertEquals(false, item00.equals(item15));
		assertEquals(false, item00.equals(item20));
		assertEquals(false, item00.equals(item21));
		assertEquals(false, item00.equals(item22));
		assertEquals(false, item00.equals(item23));
		assertEquals(false, item00.equals(item24));
		assertEquals(false, item00.equals(item25));

		assertEquals(false, item01.equals(item00));
		assertEquals(true, item01.equals(item01));
		assertEquals(false, item01.equals(item02));
		assertEquals(false, item01.equals(item03));
		assertEquals(true, item01.equals(item04));
		assertEquals(false, item01.equals(item05));
		assertEquals(false, item01.equals(item10));
		assertEquals(false, item01.equals(item11));
		assertEquals(false, item01.equals(item12));
		assertEquals(false, item01.equals(item13));
		assertEquals(false, item01.equals(item14));
		assertEquals(false, item01.equals(item15));
		assertEquals(false, item01.equals(item20));
		assertEquals(false, item01.equals(item21));
		assertEquals(false, item01.equals(item22));
		assertEquals(false, item01.equals(item23));
		assertEquals(false, item01.equals(item24));
		assertEquals(false, item01.equals(item25));

		assertEquals(false, item02.equals(item00));
		assertEquals(false, item02.equals(item01));
		assertEquals(true, item02.equals(item02));
		assertEquals(false, item02.equals(item03));
		assertEquals(false, item02.equals(item04));
		assertEquals(true, item02.equals(item05));
		assertEquals(false, item02.equals(item10));
		assertEquals(false, item02.equals(item11));
		assertEquals(false, item02.equals(item12));
		assertEquals(false, item02.equals(item13));
		assertEquals(false, item02.equals(item14));
		assertEquals(false, item02.equals(item15));
		assertEquals(false, item02.equals(item20));
		assertEquals(false, item02.equals(item21));
		assertEquals(false, item02.equals(item22));
		assertEquals(false, item02.equals(item23));
		assertEquals(false, item02.equals(item24));
		assertEquals(false, item02.equals(item25));
		
		
		assertEquals(false, item10.equals(item00));
		assertEquals(false, item10.equals(item01));
		assertEquals(false, item10.equals(item02));
		assertEquals(false, item10.equals(item03));
		assertEquals(false, item10.equals(item04));
		assertEquals(false, item10.equals(item05));
		assertEquals(true, item10.equals(item10));
		assertEquals(false, item10.equals(item11));
		assertEquals(false, item10.equals(item12));
		assertEquals(true, item10.equals(item13));
		assertEquals(false, item10.equals(item14));
		assertEquals(false, item10.equals(item15));
		assertEquals(false, item10.equals(item20));
		assertEquals(false, item10.equals(item21));
		assertEquals(false, item10.equals(item22));
		assertEquals(false, item10.equals(item23));
		assertEquals(false, item10.equals(item24));
		assertEquals(false, item10.equals(item25));

		assertEquals(false, item11.equals(item00));
		assertEquals(false, item11.equals(item01));
		assertEquals(false, item11.equals(item02));
		assertEquals(false, item11.equals(item03));
		assertEquals(false, item11.equals(item04));
		assertEquals(false, item11.equals(item05));
		assertEquals(false, item11.equals(item10));
		assertEquals(true, item11.equals(item11));
		assertEquals(false, item11.equals(item12));
		assertEquals(false, item11.equals(item13));
		assertEquals(true, item11.equals(item14));
		assertEquals(false, item11.equals(item15));
		assertEquals(false, item11.equals(item20));
		assertEquals(false, item11.equals(item21));
		assertEquals(false, item11.equals(item22));
		assertEquals(false, item11.equals(item23));
		assertEquals(false, item11.equals(item24));
		assertEquals(false, item11.equals(item25));

		assertEquals(false, item12.equals(item00));
		assertEquals(false, item12.equals(item01));
		assertEquals(false, item12.equals(item02));
		assertEquals(false, item12.equals(item03));
		assertEquals(false, item12.equals(item04));
		assertEquals(false, item12.equals(item05));
		assertEquals(false, item12.equals(item10));
		assertEquals(false, item12.equals(item11));
		assertEquals(true, item12.equals(item12));
		assertEquals(false, item12.equals(item13));
		assertEquals(false, item12.equals(item14));
		assertEquals(true, item12.equals(item15));
		assertEquals(false, item12.equals(item20));
		assertEquals(false, item12.equals(item21));
		assertEquals(false, item12.equals(item22));
		assertEquals(false, item12.equals(item23));
		assertEquals(false, item12.equals(item24));
		assertEquals(false, item12.equals(item25));

		
		
		assertEquals(false, item20.equals(item00));
		assertEquals(false, item20.equals(item01));
		assertEquals(false, item20.equals(item02));
		assertEquals(false, item20.equals(item03));
		assertEquals(false, item20.equals(item04));
		assertEquals(false, item20.equals(item05));
		assertEquals(false, item20.equals(item10));
		assertEquals(false, item20.equals(item11));
		assertEquals(false, item20.equals(item12));
		assertEquals(false, item20.equals(item13));
		assertEquals(false, item20.equals(item14));
		assertEquals(false, item20.equals(item15));
		assertEquals(true, item20.equals(item20));
		assertEquals(false, item20.equals(item21));
		assertEquals(false, item20.equals(item22));
		assertEquals(true, item20.equals(item23));
		assertEquals(false, item20.equals(item24));
		assertEquals(false, item20.equals(item25));

		assertEquals(false, item21.equals(item00));
		assertEquals(false, item21.equals(item01));
		assertEquals(false, item21.equals(item02));
		assertEquals(false, item21.equals(item03));
		assertEquals(false, item21.equals(item04));
		assertEquals(false, item21.equals(item05));
		assertEquals(false, item21.equals(item10));
		assertEquals(false, item21.equals(item11));
		assertEquals(false, item21.equals(item12));
		assertEquals(false, item21.equals(item13));
		assertEquals(false, item21.equals(item14));
		assertEquals(false, item21.equals(item15));
		assertEquals(false, item21.equals(item20));
		assertEquals(true, item21.equals(item21));
		assertEquals(false, item21.equals(item22));
		assertEquals(false, item21.equals(item23));
		assertEquals(true, item21.equals(item24));
		assertEquals(false, item21.equals(item25));

		assertEquals(false, item22.equals(item00));
		assertEquals(false, item22.equals(item01));
		assertEquals(false, item22.equals(item02));
		assertEquals(false, item22.equals(item03));
		assertEquals(false, item22.equals(item04));
		assertEquals(false, item22.equals(item05));
		assertEquals(false, item22.equals(item10));
		assertEquals(false, item22.equals(item11));
		assertEquals(false, item22.equals(item12));
		assertEquals(false, item22.equals(item13));
		assertEquals(false, item22.equals(item14));
		assertEquals(false, item22.equals(item15));
		assertEquals(false, item22.equals(item20));
		assertEquals(false, item22.equals(item21));
		assertEquals(true, item22.equals(item22));
		assertEquals(false, item22.equals(item23));
		assertEquals(false, item22.equals(item24));
		assertEquals(true, item22.equals(item25));
	}

	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final String	EXP_NAMEKEY_NULL  = null;
	static protected final String	EXP_NAMEKEY_EMPTY = "";
	static protected final String	EXP_NAMEKEY_STR   = "NameKey";
	static protected final String	EXP_NAMEKEY_INCLUDE_INVALIDBASEKEY = "Name$Key";

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
	
	protected int calcHashCode(int type, String name)
	{
		int h = type;
		h = 31 * h + (name==null ? 0 : name.hashCode());
		return h;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
