/*
 * @(#)EmptyRangeIteratorTest.java	1.70	2011/05/17
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.util.range;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import junit.framework.TestCase;

/**
 * {@link ssac.aadl.runtime.util.range.EmptyRangeIterator} クラスのテスト。
 * 
 * @version 1.70	2011/05/17
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * 
 * @since 1.70
 */
public class EmptyRangeIteratorTest extends TestCase
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

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
	// Test Cases for 1.70
	//------------------------------------------------------------

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.EmptyRangeIterator#hasNext()}.
	 */
	public void testHasNext() {
		EmptyRangeIterator<?> it;
		
		it = new EmptyRangeIterator<Short>();
		assertFalse(it.hasNext());
		
		it = new EmptyRangeIterator<Integer>();
		assertFalse(it.hasNext());
		
		it = new EmptyRangeIterator<Long>();
		assertFalse(it.hasNext());
		
		it = new EmptyRangeIterator<BigDecimal>();
		assertFalse(it.hasNext());
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.EmptyRangeIterator#next()}.
	 */
	public void testNext() {
		EmptyRangeIterator<?> it;
		
		it = new EmptyRangeIterator<Short>();
		try {
			it.next();
			fail("Must be throw NoSuchElementException.");
		} catch (NoSuchElementException ex) {}
		
		it = new EmptyRangeIterator<Integer>();
		try {
			it.next();
			fail("Must be throw NoSuchElementException.");
		} catch (NoSuchElementException ex) {}
		
		it = new EmptyRangeIterator<Long>();
		try {
			it.next();
			fail("Must be throw NoSuchElementException.");
		} catch (NoSuchElementException ex) {}
		
		it = new EmptyRangeIterator<BigDecimal>();
		try {
			it.next();
			fail("Must be throw NoSuchElementException.");
		} catch (NoSuchElementException ex) {}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.EmptyRangeIterator#remove()}.
	 */
	public void testRemove() {
		EmptyRangeIterator<?> it;
		
		it = new EmptyRangeIterator<Short>();
		try {
			it.remove();
			fail("Must be throw UnsupportedOperationException.");
		} catch (UnsupportedOperationException ex) {}
		
		it = new EmptyRangeIterator<Integer>();
		try {
			it.remove();
			fail("Must be throw UnsupportedOperationException.");
		} catch (UnsupportedOperationException ex) {}
		
		it = new EmptyRangeIterator<Long>();
		try {
			it.remove();
			fail("Must be throw UnsupportedOperationException.");
		} catch (UnsupportedOperationException ex) {}
		
		it = new EmptyRangeIterator<BigDecimal>();
		try {
			it.remove();
			fail("Must be throw UnsupportedOperationException.");
		} catch (UnsupportedOperationException ex) {}
	}
}
