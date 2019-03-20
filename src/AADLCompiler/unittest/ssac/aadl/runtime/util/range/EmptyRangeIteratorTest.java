/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  Copyright 2007-2011  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
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
