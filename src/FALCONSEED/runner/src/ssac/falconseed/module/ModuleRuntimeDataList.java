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
 *  Copyright 2007-2012  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)ModuleRuntimeDataList.java	2.0.0	2012/10/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module;

import java.util.ArrayList;
import java.util.Collection;

/**
 * モジュール実行時情報のリスト。
 * モジュール実行時情報は、モジュール(フィルタ)定義、実行時の引数などが格納される。
 * <p>なお、このオブジェクトの要素には <tt>null</tt> を許容しない。
 * 
 * @version 2.0.0	2012/10/19
 * @since 2.0.0
 */
public class ModuleRuntimeDataList extends ArrayList<ModuleRuntimeData>
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
	
	public ModuleRuntimeDataList() {
		super();
	}
	
	public ModuleRuntimeDataList(int initialCapacity) {
		super(initialCapacity);
	}
	
	public ModuleRuntimeDataList(Collection<? extends ModuleRuntimeData> c) {
		super(c.size());
		for (ModuleRuntimeData data : c) {
			if (data == null)
				throw new IllegalArgumentException("ModuleRuntimeData collection included Null!");
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
	 * @return	最初に見つかった <code>ModuleRuntimeData</code> オブジェクトを返す。
	 * 			見つからなかった場合は <tt>null</tt>
	 */
	public ModuleRuntimeData findByRunNo(long runNo) {
		for (ModuleRuntimeData data : this) {
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
		for (ModuleRuntimeData data : this) {
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
		for (ModuleRuntimeData data : this) {
			int cnt = data.getArgumentCount();
			if (maxcnt < cnt) {
				maxcnt = cnt;
			}
		}
		
		return maxcnt;
	}

	@Override
	public boolean add(ModuleRuntimeData element) {
		if (element == null)
			throw new IllegalArgumentException("'element' is null.");
		super.add(element);
		onDataInserted(size()-1, 1);
		return true;
	}
	
	@Override
	public void add(int index, ModuleRuntimeData element) {
		if (element == null)
			throw new IllegalArgumentException("'element' is null.");
		super.add(index, element);
		onDataInserted(index, 1);
	}

	@Override
	public boolean addAll(Collection<? extends ModuleRuntimeData> c) {
		if (c.isEmpty())
			return false;
		
		for (ModuleRuntimeData element : c) {
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

	@Override
	public boolean addAll(int index, Collection<? extends ModuleRuntimeData> c) {
		if (c.isEmpty())
			return false;
		
		for (ModuleRuntimeData element : c) {
			if (element == null)
				throw new IllegalArgumentException("ModuleRuntimeData collection included Null!");
		}
		boolean result = super.addAll(index, c);
		if (result) {
			onDataInserted(index, c.size());
		}
		return result;
	}

	@Override
	public ModuleRuntimeData set(int index, ModuleRuntimeData element) {
		if (element == null)
			throw new IllegalArgumentException("'element' is null.");
		ModuleRuntimeData oldData = super.set(index, element);
		onDataChanged(index, oldData, element);
		return oldData;
	}

	@Override
	public ModuleRuntimeDataList clone() {
		// deep-copy
		ModuleRuntimeDataList newlist = (ModuleRuntimeDataList)super.clone();
		for (int index = 0; index < size(); index++) {
			newlist.fastSet(index, get(index).clone());
		}
		return newlist;
	}

	@Override
	public boolean remove(Object o) {
		int removeIndex = indexOf(o);
		if (removeIndex >= 0) {
			remove(removeIndex);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public ModuleRuntimeData remove(int index) {
		ModuleRuntimeData removed = null;
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
	
	static protected void rotate(ModuleRuntimeDataList list, int a, int b, int shift) {
		int size = b - a; 
		int r = size - shift;
		int g = gcd(size, r); 
		for(int i = 0; i < g; i++) {
			int to = i; 
			ModuleRuntimeData tmp = list.get(a + to); 
			for(int from = (to + r) % size; from != i; from = (to + r) % size) {
				list.fastSet(a + to, list.get(a + from));
				to = from; 
			}
			list.fastSet(a + to, tmp);
		}
	}
	
	protected void onDataInserted(int fromIndex, int length) {
		// no entry
	}
	
	protected void onDataChanged(int index, final ModuleRuntimeData oldData, final ModuleRuntimeData newData) {
		// no entry
	}
	
	protected void onDataRemoved(int removedIndex, final ModuleRuntimeData removedData) {
		// no entry
	}
	
	protected void fastAdd(final ModuleRuntimeData element) {
		super.add(element);
	}

	protected void fastAdd(int index, final ModuleRuntimeData element) {
		super.add(index, element);
	}
	
	protected void fastAddAll(Collection<? extends ModuleRuntimeData> c) {
		super.addAll(c);
	}
	
	protected void fastAddAll(int index, Collection<? extends ModuleRuntimeData> c) {
		super.addAll(index, c);
	}
	
	protected ModuleRuntimeData fastSet(int index, ModuleRuntimeData element) {
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
