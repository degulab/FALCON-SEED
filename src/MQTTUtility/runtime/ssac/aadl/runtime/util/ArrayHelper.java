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
 *  Copyright 2007-2010  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)ArrayHelper.java	1.16	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.util;

import java.util.Arrays;



/**
 * 配列操作ユーティリティ
 * 
 * @version 1.16	2010/09/27
 * @since 1.16
 */
public class ArrayHelper
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	static public final boolean[]	EMPTY_BOOLEAN_ARRAY	= new boolean[0];
	static public final byte[]		EMPTY_BYTE_ARRAY	= new byte[0];
	static public final char[]		EMPTY_CHAR_ARRAY	= new char[0];
	static public final short[]		EMPTY_SHORT_ARRAY	= new short[0];
	static public final int[]			EMPTY_INT_ARRAY		= new int[0];
	static public final long[]		EMPTY_LONG_ARRAY	= new long[0];
	static public final float[]		EMPTY_FLOAT_ARRAY	= new float[0];
	static public final double[]		EMPTY_DOUBLE_ARRAY	= new double[0];
	
	static public final Object[]		EMPTY_OBJECT_ARRAY	= new Object[0];
	static public final Class<?>[]	EMPTY_CLASS_ARRAY	= new Class[0];
	static public final String[]		EMPTY_STRING_ARRAY	= new String[0];

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定された配列の部分配列である新しい配列を生成する。
	 * 部分配列は <code>beginIndex</code> から始まり <code>endIndex - 1</code> までの要素となる。
	 * @param array		対象の配列
	 * @param beginIndex	開始インデックス(この値を含む)
	 * @param endIndex		終了インデックス(この値を含まない)
	 * @return	生成された部分配列
	 */
	static public byte[] subarray(byte array[], int beginIndex, int endIndex)
	{
		if (array == null)
			return null;
		
		if (beginIndex < 0)
			beginIndex = 0;
		if (endIndex > array.length)
			endIndex = array.length;
		
		int newSize = endIndex - beginIndex;
		if (newSize <= 0) {
			return EMPTY_BYTE_ARRAY;
		}
		else {
			byte[] subarray = new byte[newSize];
			System.arraycopy(array, beginIndex, subarray, 0, newSize);
			return subarray;
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static public class SimpleByteArrayList implements Cloneable
	{
		private byte[]	_array;
		private int	_size;
		
		public SimpleByteArrayList() {
			this(64);
		}
		
		public SimpleByteArrayList(int initialCapacity) {
			if (initialCapacity < 0)
				throw new IllegalArgumentException("The specified initial capacity is negative : " + initialCapacity);
			this._array = new byte[initialCapacity];
			this._size = 0;
		}
		
		public SimpleByteArrayList(byte[] that) {
			this._array = (byte[])that.clone();
			this._size  = that.length;
		}
		
		public void ensureCapacity(int minCapacity) {
			if (_array.length < minCapacity) {
				int newCapacity = (_array.length * 3) / 2 + 1;
				byte[] oldArray = this._array;
				this._array = new byte[newCapacity < minCapacity ? minCapacity : newCapacity];
				System.arraycopy(oldArray, 0, _array, 0, _size);
			}
		}
		
		public void trimToSize() {
			if (_size < _array.length) {
				byte[] oldArray = this._array;
				this._array = new byte[_size];
				System.arraycopy(oldArray, 0, _array, 0, _size);
			}
		}
		
		public void clear() {
			this._size = 0;
		}
		
		public int size() {
			return _size;
		}
		
		public byte get(int index) {
			return _array[index];
		}
		
		public byte set(int index, byte element) {
			byte oldElement = _array[index];
			_array[index] = element;
			return oldElement;
		}
		
		public byte removeAt(int index) {
			byte oldElement = _array[index];
			int numMoves = _size - index - 1;
			if (numMoves > 0) {
				System.arraycopy(_array, index+1, _array, index, numMoves);
			}
			--_size;
			return oldElement;
		}
		
		public void removeRange(int offset, int length) {
			if (offset < 0)
				throw new IndexOutOfBoundsException("Offset(" + offset + ") < 0");
			if (length < 0)
				throw new IndexOutOfBoundsException("Length(" + length + ") < 0");
			if (offset < _size) {
				int srcPos = offset + length;
				if (srcPos < 0 || srcPos >= _size)
					_size = srcPos;
				else {
					System.arraycopy(_array, srcPos, _array, offset, (_size - srcPos));
					_size -= length;
				}
			}
		}
		
		public void add(byte element) {
			ensureCapacity(_size+1);
			_array[_size] = element;
			++_size;
		}
		
		public void add(int index, byte element) {
			checkRangeIncludingEndpoint(index);
			ensureCapacity(_size+1);
			int numMoves = _size - index;
			if (numMoves > 0) {
				System.arraycopy(_array, index, _array, index+1, numMoves);
			}
			_array[index] = element;
			++_size;
		}
		
		public void append(SimpleByteArrayList bytes) {
			appendData(bytes._array, 0, bytes._size);
		}
		
		public void append(SimpleByteArrayList bytes, int offset, int length) {
			bytes.checkOffsetAndLength(offset, length);
			appendData(bytes._array, offset, length);
		}
		
		public void append(byte[] bytes) {
			appendData(bytes, 0, bytes.length);
		}
		
		public void append(byte[] bytes, int offset, int length) {
			checkOffsetAndLength(bytes, offset, length);
			appendData(bytes, offset, length);
		}
		
		protected void appendData(byte[] bytes, int offset, int length) {
			ensureCapacity(_size + length);
			System.arraycopy(bytes, offset, _array, _size, length);
			_size += length;
		}
		
		public int indexOf(byte element) {
			for (int i = 0, n = _size; i < n; ++i) {
				if (_array[i] == element) {
					return i;
				}
			}
			return (-1);
		}
		
		public int lastIndexOf(byte element) {
			for (int i = _size; 0 < i--; ) {
				if (_array[i] == element) {
					return i;
				}
			}
			return (-1);
		}
		
		public byte[] toArray() {
			return Arrays.copyOf(_array, _size);
		}
		
		public byte[] toArray(int offset, int length) {
			checkOffsetAndLength(offset, length);
			byte[] newArray = new byte[length];
			System.arraycopy(_array, offset, newArray, 0, length);
			return newArray;
		}
		
		static protected final void checkOffset(byte[] bytes, int offset) {
			if (offset < 0)
				throw new IndexOutOfBoundsException("Offset(" + offset + ") < 0");
			else if (offset > bytes.length)
				throw new IndexOutOfBoundsException("Offset(" + offset + ") >= Size(" + bytes.length + ")");
			else if (offset==bytes.length && bytes.length > 0)
				throw new IndexOutOfBoundsException("Offset(" + offset + ") >= Size(" + bytes.length + ")");
		}
		
		static protected final void checkOffsetAndLength(byte[] bytes, int offset, int length) {
			checkOffset(bytes, offset);
			if (length < 0)
				throw new IndexOutOfBoundsException("Length(" + length + ") < 0");
			else if ((bytes.length - length) < offset)
				throw new IndexOutOfBoundsException("Offset(" + offset + ") + Length(" + length + ") > Size(" + bytes.length + ")");
		}
		
		protected final void checkOffset(int offset) {
			if (offset < 0)
				throw new IndexOutOfBoundsException("Offset(" + offset + ") < 0");
			else if (offset > _size)
				throw new IndexOutOfBoundsException("Offset(" + offset + ") >= Size(" + _size + ")");
			else if (offset==_size && _size > 0)
				throw new IndexOutOfBoundsException("Offset(" + offset + ") >= Size(" + _size + ")");
		}
		
		protected final void checkOffsetAndLength(int offset, int length) {
			checkOffset(offset);
			if (length < 0)
				throw new IndexOutOfBoundsException("Length(" + length + ") < 0");
			else if ((_size - length) < offset)
				throw new IndexOutOfBoundsException("Offset(" + offset + ") + Length(" + length + ") > Size(" + _size + ")");
		}
		
		protected final void checkRange(int index) {
			if (index < 0)
				throw new IndexOutOfBoundsException("Index(" + index + ") < 0");
			else if (index >= _size)
				throw new IndexOutOfBoundsException("Index(" + index + ") >= Size(" + _size + ")");
		}
		
		protected final void checkRangeIncludingEndpoint(int index) {
			if (index < 0)
				throw new IndexOutOfBoundsException("Index(" + index + ") < 0");
			else if (index > _size)
				throw new IndexOutOfBoundsException("Index(" + index + ") > Size(" + _size + ")");
		}

		@Override
		public SimpleByteArrayList clone()
		{
			try {
				SimpleByteArrayList v = (SimpleByteArrayList)super.clone();
				v._array = (byte[])this._array.clone();
				return v;
			}
			catch (CloneNotSupportedException ex) {
				throw new InternalError();
			}
		}

		@Override
		public int hashCode() {
			int result = 1;
			for (int i = 0, n = _size; i < n; ++i) {
				result = 31 * result + _array[i];
			}
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			
			if (obj instanceof SimpleByteArrayList) {
				SimpleByteArrayList list = (SimpleByteArrayList)obj;
				int len = this._size;
				if (list._size == len) {
					for (int i = 0; i < len; ++i) {
						if (list._array[i] != this._array[i]) {
							return false;
						}
					}
					return true;
				}
			}
			
			return false;
		}

		@Override
		public String toString() {
			if (_size == 0)
				return "[]";
			
			StringBuilder sb = new StringBuilder();
			sb.append('[');
			sb.append(_array[0]);
			for (int i = 1, len = _size; i < len; ++i) {
				sb.append(", ");
				sb.append(_array[i]);
			}
			sb.append(']');
			return sb.toString();
		}
	}
	
	static public class SimpleIntegerArrayList implements Cloneable
	{
		private int[]	_array;
		private int	_size;
		
		public SimpleIntegerArrayList() {
			this(16);
		}
		
		public SimpleIntegerArrayList(int initialCapacity) {
			if (initialCapacity < 0)
				throw new IllegalArgumentException("The specified initial capacity is negative : " + initialCapacity);
			this._array = new int[initialCapacity];
			this._size = 0;
		}
		
		public SimpleIntegerArrayList(int[] that) {
			this._array = (int[])that.clone();
			this._size  = that.length;
		}
		
		public void ensureCapacity(int minCapacity) {
			if (_array.length < minCapacity) {
				int newCapacity = (_array.length * 3) / 2 + 1;
				int[] oldArray = this._array;
				this._array = new int[newCapacity < minCapacity ? minCapacity : newCapacity];
				System.arraycopy(oldArray, 0, _array, 0, _size);
			}
		}
		
		public void trimToSize() {
			if (_size < _array.length) {
				int[] oldArray = this._array;
				this._array = new int[_size];
				System.arraycopy(oldArray, 0, _array, 0, _size);
			}
		}
		
		public void clear() {
			this._size = 0;
		}
		
		public int size() {
			return _size;
		}
		
		public int get(int index) {
			return _array[index];
		}
		
		public int set(int index, int element) {
			int oldElement = _array[index];
			_array[index] = element;
			return oldElement;
		}
		
		public int removeAt(int index) {
			int oldElement = _array[index];
			int numMoves = _size - index - 1;
			if (numMoves > 0) {
				System.arraycopy(_array, index+1, _array, index, numMoves);
			}
			--_size;
			return oldElement;
		}
		
		public void add(int element) {
			ensureCapacity(_size+1);
			_array[_size] = element;
			++_size;
		}
		
		public void add(int index, int element) {
			checkRangeIncludingEndpoint(index);
			ensureCapacity(_size+1);
			int numMoves = _size - index;
			if (numMoves > 0) {
				System.arraycopy(_array, index, _array, index+1, numMoves);
			}
			_array[index] = element;
			++_size;
		}
		
		public int indexOf(int element) {
			for (int i = 0, n = _size; i < n; ++i) {
				if (_array[i] == element) {
					return i;
				}
			}
			return (-1);
		}
		
		public int lastIndexOf(int element) {
			for (int i = _size; 0 < i--; ) {
				if (_array[i] == element) {
					return i;
				}
			}
			return (-1);
		}
		
		protected final void checkRange(int index) {
			if (index < 0)
				throw new IndexOutOfBoundsException("Index(" + index + ") < 0");
			else if (index >= _size)
				throw new IndexOutOfBoundsException("Index(" + index + ") >= Size(" + _size + ")");
		}
		
		protected final void checkRangeIncludingEndpoint(int index) {
			if (index < 0)
				throw new IndexOutOfBoundsException("Index(" + index + ") < 0");
			else if (index > _size)
				throw new IndexOutOfBoundsException("Index(" + index + ") > Size(" + _size + ")");
		}

		@Override
		public SimpleIntegerArrayList clone()
		{
			try {
				SimpleIntegerArrayList v = (SimpleIntegerArrayList)super.clone();
				v._array = (int[])this._array.clone();
				return v;
			}
			catch (CloneNotSupportedException ex) {
				throw new InternalError();
			}
		}

		@Override
		public int hashCode() {
			int result = 1;
			for (int i = 0, n = _size; i < n; ++i) {
				result = 31 * result + _array[i];
			}
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			
			if (obj instanceof SimpleIntegerArrayList) {
				SimpleIntegerArrayList list = (SimpleIntegerArrayList)obj;
				int len = this._size;
				if (list._size == len) {
					for (int i = 0; i < len; ++i) {
						if (list._array[i] != this._array[i]) {
							return false;
						}
					}
					return true;
				}
			}
			
			return false;
		}

		@Override
		public String toString() {
			if (_size == 0)
				return "[]";
			
			StringBuilder sb = new StringBuilder();
			sb.append('[');
			sb.append(_array[0]);
			for (int i = 1, len = _size; i < len; ++i) {
				sb.append(", ");
				sb.append(_array[i]);
			}
			sb.append(']');
			return sb.toString();
		}
	}
}
