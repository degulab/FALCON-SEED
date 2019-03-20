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
 *  Copyright 2007-2014  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)ModuleDataList.java	3.1.0	2014/05/12
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module;

import java.util.ArrayList;
import java.util.Collection;

/**
 * モジュール情報のリスト。
 * モジュール情報は、モジュール(フィルタ)定義、実行時の引数などが格納される。
 * <p>なお、このオブジェクトの要素には <tt>null</tt> を許容しない。<br>
 * また、このリストのクローンでは、リストの要素（モジュールデータ）もクローンを生成するディープコピーとなる。
 * <p><b>この実装は同期化されない。</b>
 * 
 * @version 3.1.0	2014/05/12
 * @since 3.1.0
 */
public class ModuleDataList<T extends ModuleRuntimeData> extends ArrayList<T>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 初期容量 10 で空のリストを作成する。
	 */
	public ModuleDataList() {
		super();
	}

	/**
	 * 指定された初期サイズで空のリストを作成する。
	 * @param initialCapacity	リストの初期容量
	 * @throws IllegalArgumentException	指定された初期容量が負の場合
	 */
	public ModuleDataList(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * 指定されたコレクションの要素が含まれているリストを、要素がコレクションの反復子によって返される順序で作成する。
	 * @param c	要素がリストに配置されるコレクション
	 * @throws NullPointerException	指定されたコレクションが <tt>null</tt> の場合
	 * @throws IllegalArgumentException	指定されたコレクションの要素に <tt>null</tt> が含まれている場合
	 */
	public ModuleDataList(Collection<? extends T> c) {
		super(c.size());
		for (T data : c) {
			if (data == null)
				throw new IllegalArgumentException("Collection contains Null!");
		}
		super.addAll(c);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * モジュールに設定された実行番号で検索する。
	 * このメソッドでは、リストの先頭から検索し、最初に見つかったモジュールを返す。
	 * @param runNo		検索する実行番号
	 * @return	最初に見つかったモジュールデータオブジェクトを返す。見つからなかった場合は <tt>null</tt>
	 */
	public T findByRunNo(long runNo) {
		for (T data : this) {
			if (data.getRunNo() == runNo) {
				return data;
			}
		}
		return null;
	}

	/**
	 * モジュールに設定された実行番号に一致する、最初のモジュールのインデックスを返す。
	 * このメソッドでは、リストの先頭から検索し、最初に見つかったモジュールのインデックスを返す。
	 * @param runNo		検索する実行番号
	 * @return	最初に見つかったモジュールのインデックス、見つからなかった場合は (-1)
	 */
	public int indexByRunNo(long runNo) {
		for (int index = 0; index < size(); index++) {
			if (get(index).getRunNo() == runNo) {
				return index;
			}
		}
		return (-1);
	}

	/**
	 * すべてのモジュールのうち、最小の引数の数を返す。
	 */
	public int getMinArgumentCount() {
		if (isEmpty())
			return 0;
		
		int mincnt = Integer.MAX_VALUE;
		for (T data : this) {
			int cnt = data.getArgumentCount();
			if (mincnt > cnt) {
				mincnt = cnt;
			}
		}
		
		return mincnt;
	}

	/**
	 * すべてのモジュールのうち、最大の引数の数を返す。
	 */
	public int getMaxArgumentCount() {
		if (isEmpty())
			return 0;
		
		int maxcnt = 0;
		for (T data : this) {
			int cnt = data.getArgumentCount();
			if (maxcnt < cnt) {
				maxcnt = cnt;
			}
		}
		
		return maxcnt;
	}

	/**
	 * リストの最後に、指定された要素を追加する。
	 * @param element	追加する要素
	 * @return	常に <tt>true</tt>
	 * @throws NullPointerException	<em>element</em> が <tt>null</tt> の場合
	 */
	@Override
	public boolean add(T element) {
		if (element == null)
			throw new IllegalArgumentException("'element' is null.");
		super.add(element);
		onDataInserted(size()-1, 1);
		return true;
	}

	/**
	 * リスト内の指定された位置に指定された要素を挿入する。
	 * @param index	挿入する位置のインデックス
	 * @param element	挿入する要素
	 * @throws NullPointerException	<em>element</em> が <tt>null</tt> の場合
	 */
	@Override
	public void add(int index, T element) {
		if (element == null)
			throw new IllegalArgumentException("'element' is null.");
		super.add(index, element);
		onDataInserted(index, 1);
	}

	/**
	 * 指定されたコレクション内のすべての要素を、指定されたコレクションの反復子によって返される順序でリストの最後に追加する。
	 * @param c	リストに追加する要素のコレクション
	 * @return	この呼び出しの結果、リストが変更された場合は <tt>true</tt>
	 * @throws NullPointerException	指定されたコレクションが <tt>null</tt> の場合
	 * @throws IllegalArgumentException	指定されたコレクションの要素に <tt>null</tt> が含まれている場合
	 */
	@Override
	public boolean addAll(Collection<? extends T> c) {
		if (c.isEmpty())
			return false;
		
		for (T element : c) {
			if (element == null)
				throw new IllegalArgumentException("ModuleRuntimeData collection included Null!");
		}
		int fromIndex = size();
		boolean result = super.addAll(c);
		if (result) {
			onDataInserted(fromIndex, c.size());
		}
		return result;
	}

	/**
	 * 指定されたコレクション内のすべての要素を、リストの指定された位置に挿入する。
	 * @param index	指定されたコレクションの最初の要素を挿入する位置のインデックス
	 * @param c	リストに挿入する要素のコレクション
	 * @return	この呼び出しの結果、このリストが変更された場合は <tt>true</tt>
	 * @throws NullPointerException	指定されたコレクションが <tt>null</tt> の場合
	 * @throws IllegalArgumentException	指定されたコレクションの要素に <tt>null</tt> が含まれている場合
	 */
	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		if (c.isEmpty())
			return false;
		
		for (T element : c) {
			if (element == null)
				throw new IllegalArgumentException("ModuleRuntimeData collection included Null!");
		}
		boolean result = super.addAll(index, c);
		if (result) {
			onDataInserted(index, c.size());
		}
		return result;
	}

	/**
	 * リストの指定された位置にある要素を、指定された要素で置き換える。
	 * @param index	置換される要素のインデックス
	 * @param element	指定した位置に格納する要素
	 * @return	指定された位置に以前あった要素
	 * @throws NullPointerException	指定された要素が <tt>null</tt> の場合
	 * @throws IndexOutOfBoundsException	インデックスが範囲外の場合(index &lt; 0 || index &gt;= size())
	 */
	@Override
	public T set(int index, T element) {
		if (element == null)
			throw new IllegalArgumentException("'element' is null.");
		T oldData = super.set(index, element);
		onDataChanged(index, oldData, element);
		return oldData;
	}

	/**
	 * このオブジェクトのディープコピーを返す。
	 * @return	このオブジェクトのインスタンスの複製
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ModuleDataList<T> clone() {
		// deep-copy
		ModuleDataList<T> newlist = (ModuleDataList<T>)super.clone();
		for (int index = 0; index < size(); index++) {
			newlist.fastSet(index, (T)get(index).clone());
		}
		return newlist;
	}

	/**
	 * 指定された要素がこのリストにあれば、その最初のものをリストから削除する。
	 * @param obj	リストから削除する要素
	 * @return	リストが指定された要素を保持している場合は <tt>true</tt>
	 */
	@Override
	public boolean remove(Object obj) {
		int removeIndex = indexOf(obj);
		if (removeIndex >= 0) {
			remove(removeIndex);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * リストの指定された位置にある要素を削除する。
	 * @param index	削除する要素のインデックス
	 * @throws IndexOutOfBoundsException	インデックスが範囲外の場合(index &lt; 0 || index &gt;= size())
	 */
	@Override
	public T remove(int index) {
		T removed = null;
		try {
			removed = super.remove(index);
			onDataRemoved(index, removed);
		}
		catch (Throwable ignoreEx) {
			ignoreEx = null;
		}
		return removed;
	}

	/**
	 * <em>start</em> から <em>end</em> までの 1 行または複数行を、<em>to</em> の位置に移動する。
	 * 移動後は、インデックス <em>start</em> にあった要素が、インデックス <em>to</em> に移動する。
	 * なお、インデックス <em>end</em> の要素も移動対象に含まれる。<p>
     *
     *  <pre>
     *  移動例:
     *  <p>
     *  1. moveRow(1,3,5);
     *          a|B|C|D|e|f|g|h|i|j|k   - before
     *          a|e|f|g|h|B|C|D|i|j|k   - after
     *  <p>
     *  2. moveRow(6,7,1);
     *          a|b|c|d|e|f|G|H|i|j|k   - before
     *          a|G|H|b|c|d|e|f|i|j|k   - after
     *  <p> 
     *  </pre>
	 * @param start		移動する要素の開始インデックス
	 * @param end		移動する要素の終了インデックス
	 * @param to		要素の移動先
	 * @throws IndexOutOfBoundsException	インデックスが適切ではない場合
	 */
	public void move(int start, int end, int to) {
		int shift = to - start; 
		int first, last; 
		if (shift < 0) { 
			first = to; 
			last = end; 
		}
		else { 
			first = start; 
			last = to + end - start;  
		}

		rotate(this, first, last + 1, shift);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static protected int gcd(int i, int j) {
		return (j == 0) ? i : gcd(j, i%j);
	}
	
	static protected <E extends ModuleRuntimeData> void rotate(ModuleDataList<E> list, int a, int b, int shift) {
		int size = b - a; 
		int r = size - shift;
		int g = gcd(size, r); 
		for(int i = 0; i < g; i++) {
			int to = i; 
			E tmp = list.get(a + to);
			for(int from = (to + r) % size; from != i; from = (to + r) % size) {
				list.fastSet(a + to, list.get(a + from));
				to = from; 
			}
			list.fastSet(a + to, tmp);
		}
	}

	/**
	 * 要素が追加もしくは挿入されたときに呼び出されるインタフェース
	 * @param fromIndex
	 * @param length
	 */
	protected void onDataInserted(int fromIndex, int length) {
		// no entry
	}

	/**
	 * 要素が変更されたときに呼び出されるインタフェース
	 * @param index
	 * @param oldData
	 * @param newData
	 */
	protected void onDataChanged(int index, final T oldData, final T newData) {
		// no entry
	}

	/**
	 * 要素が削除されたときに呼び出されるインタフェース
	 * @param removedIndex
	 * @param removedData
	 */
	protected void onDataRemoved(int removedIndex, final T removedData) {
		// no entry
	}
	
	protected void fastAdd(final T element) {
		super.add(element);
	}

	protected void fastAdd(int index, final T element) {
		super.add(index, element);
	}
	
	protected void fastAddAll(Collection<? extends T> c) {
		super.addAll(c);
	}
	
	protected void fastAddAll(int index, Collection<? extends T> c) {
		super.addAll(index, c);
	}
	
	protected T fastSet(int index, T element) {
		return super.set(index, element);
	}

	/**
	 * モジュールリストの終端にあるモジュール実行情報の実行番号を取得する。
	 * @return	モジュール実行番号、モジュールが一つも存在しない場合は 0
	 */
	protected long getLastModuleRunNo() {
		if (isEmpty()) {
			return 0L;
		} else {
			return get(size()-1).getRunNo();
		}
	}

	/**
	 * 指定されたインデックスに対応するモジュール実行情報から、実行番号を取得する。
	 * @param index	インデックス
	 * @return	インデックスが適切な場合はそのインデックスに対応するモジュールの実行番号、
	 * 			インデックスがモジュールリストのサイズ以上の場合はリスト終端のモジュールの実行番号、
	 * 			インデックスが負の場合は 0 を返す。また、要素が空の場合も 0 を返す。
	 */
	protected long getModuleRunNo(int index) {
		if (isEmpty())
			return 0L;
		
		if (index < 0) {
			return 0L;
		}
		else if (index >= size()) {
			return get(size()-1).getRunNo();
		}
		else {
			return get(index).getRunNo();
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
