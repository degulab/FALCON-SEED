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
