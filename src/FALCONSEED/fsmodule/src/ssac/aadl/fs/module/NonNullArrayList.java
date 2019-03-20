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
 *  Copyright 2007-2015  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)NonNullArrayList.java	3.2.0	2015/06/16
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module;

import java.util.ArrayList;
import java.util.Collection;

/**
 * <tt>null</tt> を許可しない <code>ArrayList</code> クラス。
 * 
 * @version 3.2.0
 * @since 3.2.0
 */
public class NonNullArrayList<E> extends ArrayList<E>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 5945741270329992074L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public NonNullArrayList() {
		super();
	}

	public NonNullArrayList(int initialCapacity) {
		super(initialCapacity);
	}

	public NonNullArrayList(Collection<? extends E> c) {
		super(c);
		_validNoNullElements(c);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Implement java.util.ArrayList interfaces
	//------------------------------------------------------------

	@Override
	public boolean add(E element) {
		if (element == null)
			throw new NullPointerException();
		return super.add(element);
	}

	@Override
	public void add(int index, E element) {
		if (element == null)
			throw new NullPointerException();
		super.add(index, element);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		_validNoNullElements(c);
		return super.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		_validNoNullElements(c);
		return super.addAll(index, c);
	}

	@Override
	public E set(int index, E element) {
		if (element == null)
			throw new NullPointerException();
		return super.set(index, element);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void _replace(int index, E element) {
		super.add(index, element);
	}
	
	/**
	 * 指定されたコレクションに含まれる要素に <tt>null</tt> が含まれていないことをチェックする。
	 * @param c	コレクション
	 * @throws NullPointerException	引数が <tt>null</tt> の場合、もしくは要素が <tt>null</tt> の場合
	 */
	protected void _validNoNullElements(Collection<? extends E> c) {
		for (E element : c) {
			if (element == null) {
				throw new NullPointerException("The specified collection has null element.");
			}
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
