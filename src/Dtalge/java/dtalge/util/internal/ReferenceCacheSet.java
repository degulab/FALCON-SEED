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
 *  Copyright 2007-2010  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
/*
 * @(#)ReferenceCacheSet.java	0.10	2008/08/25
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.util.internal;

import static dtalge.util.Validations.validArgument;
import static dtalge.util.Validations.validNotNull;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import dtalge.util.internal.ReferenceType;

/**
 * インスタンスのキャッシュを管理するクラス。
 * このクラスのキャッシュは、コンストラクタで指定された参照種別に
 * よってキャッシュが破棄されるタイミングが異なる。
 * <p>
 * <code>{@link basealge.util.ReferenceType#STRONG}</code> が指定された場合、
 * このキャッシュには強参照のインスタンスが格納される。つまり、このキャッシュ
 * オブジェクトが破棄されない限り、キャッシュとして格納されているインスタンスは
 * 破棄されない。
 * <p>
 * <code>{@link basealge.util.ReferenceType#SOFT}</code> が指定された場合、
 * このキャッシュにはソフト参照のインスタンスが格納される。キャッシュに
 * 格納されたオブジェクトは、ソフト到達可能なオブジェクトと判断された場合、
 * 自動的に破棄される。
 * <p>
 * <code>{@link basealge.util.ReferenceType#WEAK}</code> が指定された場合、
 * このキャッシュには弱参照のインスタンスが格納される。キャッシュに
 * 格納されたオブジェクトは、弱到達可能なオブジェクトと判断された場合、
 * 自動的に破棄される。
 * <p>
 * このクラスは、ハッシュテーブルを基にし、Set インタフェースを実装する。
 * このクラスでは、セットの繰り返し順序について保証しない。特に、その順序を
 * 一定に保つことを保証しない。このクラスは、<tt>null</tt> 要素を許容します。
 * このクラスは、ハッシュ関数が複数のバケットで適切に要素を分散することを
 * 前提として、基本のオペレーション (<code>add</code>、<code>remove</code>、
 * <code>contains</code>、および <code>size</code>) で一定時間の性能を提供する。
 * セットの繰り返し処理では、キャッシュのサイズ (要素数) と基となる ハッシュテーブル
 * の「容量」(バケット数) の合計に比例した時間が必要となる。したがって、
 * 繰り返し処理の性能が重要な場合は、初期容量をあまり高く (負荷係数をあまり低く) 設定
 * しないことが重要となる。
 * <br><b>この実装は同期化されない。</b>
 * <p>
 * このクラスの <code>iterator</code> メソッドによって返される反復子は
 * 「フェイルファスト」である。反復子が作成されたあとに、
 * 反復子独自の <code>remove</code> メソッド以外の方法でセットが変更された場合、
 * 反復子は <code>ConcurrentModificationException</code> をスローする。
 * したがって、同時変更が行われると、反復子は、将来の予測できない時点に
 * おいて予測できない動作が発生する危険を回避するために、ただちにかつ
 * 手際よく例外をスローする。
 * <br>
 * 通常、非同期の同時変更がある場合、確かな保証を行うことは不可能なので、
 * 反復子のフェイルファストの動作を保証することはできない。
 * フェイルファスト反復子は最善努力原則に基づき、
 * <code>ConcurrentModificationException</code> をスローする。
 * したがって、正確を期すためにこの例外に依存するプログラムを書くことは誤りである。
 * 「反復子のフェイルファストの動作はバグを検出するためにのみ使用すべきである」
 * 
 * @version 0.10	2008/08/25
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 * 
 * @since 0.10
 */
public class ReferenceCacheSet<E> extends AbstractSet<E> implements CacheSet<E>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static final int DEFAULT_INITIAL_CAPACITY = 16;
	static final int MAXIMUM_CAPACITY = 1 << 30;
	static final float DEFAULT_LOAD_FACTOR = 0.75f;
	
	static final Object NULL_KEY = new Object();

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	transient Entry[] table;
	
	transient int size;
	
	int threshold;
	
	final float loadFactor;
	
	final ReferenceType refType;

	transient volatile int modCount;
	
	private final ReferenceQueue<E> queue = new ReferenceQueue<E>();

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定された初期容量および負荷係数で、新しい空のキャッシュを生成する。
	 * キャッシュされるインスタンスは、指定された参照種別に準じて格納される。
	 * 
	 * @param refType	参照種別({@link basealge.util.ReferenceType})
     * @param initialCapacity キャッシュの初期容量
     * @param loadFactor キャッシュの負荷係数
     * 
     * @throws IllegalArgumentException	初期容量が 0 より小さいか、負荷係数が正の値ではない場合にスローされる
	 */
	public ReferenceCacheSet(ReferenceType refType, int initialCapacity, float loadFactor) {
		this.refType = validNotNull(refType, "'refType' argument cannot be null.");
		validArgument(initialCapacity >= 0, "Illegal 'initialCapacity' : %d", initialCapacity);
		validArgument(loadFactor > 0 && !Float.isNaN(loadFactor), "Illegal 'initialCapacity' : " + loadFactor);
		
		if (initialCapacity > MAXIMUM_CAPACITY)
			initialCapacity = MAXIMUM_CAPACITY;
		
		int capacity = 1;
		while (capacity < initialCapacity) {
			capacity <<= 1;
		}
		
		table = new Entry[capacity];
		this.loadFactor = loadFactor;
		threshold = (int)(capacity * loadFactor);
	}

	/**
	 * 指定された初期容量およびデフォルトの負荷係数で、新しい空のキャッシュを生成する。
	 * キャッシュされるインスタンスは、指定された参照種別に準じて格納される。
	 * 
	 * @param refType	参照種別({@link basealge.util.ReferenceType})
     * @param initialCapacity キャッシュの初期容量
     * 
     * @throws IllegalArgumentException	初期容量が 0 より小さいか、負荷係数が正の値ではない場合にスローされる
	 */
	public ReferenceCacheSet(ReferenceType refType, int initialCapacity) {
		this(refType, initialCapacity, DEFAULT_LOAD_FACTOR);
	}

	/**
	 * デフォルトの初期容量と負荷係数で、新しい空のキャッシュを生成する。
	 * キャッシュされるインスタンスは、指定された参照種別に準じて格納される。
	 * 
	 * @param refType	参照種別({@link basealge.util.ReferenceType})
	 */
	public ReferenceCacheSet(ReferenceType refType) {
		this.refType = validNotNull(refType, "'refType' argument cannot be null.");
		this.loadFactor = DEFAULT_LOAD_FACTOR;
		this.threshold = (int)(DEFAULT_INITIAL_CAPACITY);
		table = new Entry[DEFAULT_INITIAL_CAPACITY];
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * キャッシュに格納されているインスタンス数を取得する。
	 * 
	 * @return	キャッシュに格納されているインスタンス数
	 */
	public int size() {
		if (size == 0)
			return 0;
		expungeStaleEntries();
		return size;
	}

	/**
	 * キャッシュにインスタンスが存在しない場合に <tt>true</tt> を返す。
	 * 
	 * @return	インスタンスが存在しない場合は <tt>true</tt>
	 */
	public boolean isEmpty() {
		return size() == 0;
	}
	
	/**
	 * 指定されたオブジェクトの参照をキャッシュする。
	 * <p>
	 * 指定されたオブジェクトのインスタンスが、
	 * このオブジェクトの <code>equals(Object)</code> メソッドに
	 * よってキャッシュされたインスタンスと等しいと判断された場合は
	 * キャッシュ済みのインスタンスを返す。
	 * キャッシュに存在しないインスタンスの場合は、指定された
	 * インスタンスをキャッシュに追加し、そのインスタンスを返す。
	 * 
	 * @param o	キャッシュするオブジェクトへの参照
	 * @return	キャッシュ済みのインスタンス
	 */
	@SuppressWarnings("unchecked")
	public E cache(E o) {
		if (o == null) {
			return o;
		}
		
		E elem = maskNull(o);
		int h = hash(elem);
		Entry[] tab = getTable();
		int i = indexFor(h, tab.length);
		
		for (Entry<E> e = tab[i]; e != null; e = e.next) {
			if (h == e.ref.identityHash() && eq(elem, e.ref.get())) {
				return e.element();
			}
		}
		
		modCount++;
		Entry<E> e = tab[i];
		tab[i] = new Entry<E>(refType, elem, queue, h, e);
		if (++size >= threshold)
			resize(tab.length * 2);
		return o;
		
	}

	/**
	 * 指定されたオブジェクトがキャッシュ済みであれば <tt>true</tt> を返す。
	 * 
	 * @return	指定されたオブジェクトがキャッシュ済みであれば <tt>true</tt>
	 */
	public boolean contains(Object o) {
		return getEntry(o) != null;
	}

	/**
	 * 指定されたオブジェクトと等しい要素を取得する。
	 * 等しい要素が存在しない場合は <tt>null</tt> を返す。
	 * 
	 * @param key	要素を取得するキーとなるオブジェクト
	 * @return	キーと等しいオブジェクト。存在しない場合は <tt>null</tt>
	 */
	@SuppressWarnings("unchecked")
	Entry<E> getEntry(Object key) {
		Object k = maskNull(key);
		int h = hash(k);
		Entry[] tab = getTable();
		int index = indexFor(h, tab.length);
		Entry<E> e = tab[index];
		while (e != null && !(e.ref.identityHash() == h && eq(k, e.ref.get()))) {
			e = e.next;
		}
		return e;
	}

	/**
	 * セットの要素の反復子を返す。要素が返されるときに特定の順序は無い。
	 * 
	 * @return セットの要素の <code>Iterator</code>
	 */
	public Iterator<E> iterator() {
		return new ElementIterator();
	}

	/**
	 * 指定されたオブジェクトがキャッシュの要素として存在しない場合に、
	 * そのオブジェクトをキャッシュに追加する。
	 * 
	 * @param o	キャッシュに追加されるオブジェクト
	 * @return	キャッシュが指定されたオブジェクトを保持していなかった場合は <tt>true</tt>
	 */
	@SuppressWarnings("unchecked")
	public boolean add(E o) {
		E elem = maskNull(o);
		int h = hash(elem);
		Entry[] tab = getTable();
		int i = indexFor(h, tab.length);
		
		for (Entry<E> e = tab[i]; e != null; e = e.next) {
			if (h == e.ref.identityHash() && eq(elem, e.ref.get())) {
				return false;
			}
		}
		
		modCount++;
		Entry<E> e = tab[i];
		tab[i] = new Entry<E>(refType, elem, queue, h, e);
		if (++size >= threshold)
			resize(tab.length * 2);
		return true;
	}

	/**
	 * 指定されたサイズにハッシュテーブルの容量を拡張する。
	 * 
	 * @param newCapacity	ハッシュテーブルの容量
	 */
	void resize(int newCapacity) {
		Entry[] oldTable = getTable();
		int oldCapacity = oldTable.length;
		if (oldCapacity == MAXIMUM_CAPACITY) {
			threshold = Integer.MAX_VALUE;
			return;
		}
		
		Entry[] newTable = new Entry[newCapacity];
		transfer(oldTable, newTable);
		table = newTable;
		
		if (size >= threshold / 2) {
			threshold = (int)(newCapacity * loadFactor);
		} else {
			expungeStaleEntries();
			transfer(newTable, oldTable);
			table = oldTable;
		}
	}

	/**
	 * ハッシュテーブルの内容をコピーする。
	 * 
	 * @param src	コピー元のハッシュテーブル
	 * @param dest	コピー先のハッシュテーブル
	 */
	@SuppressWarnings("unchecked")
	private void transfer(Entry[] src, Entry[] dest) {
		for (int j = 0; j < src.length; ++j) {
			Entry<E> e = src[j];
			src[j] = null;
			while (e != null) {
				Entry<E> next = e.next;
				Object key = e.ref.get();
				if (key == null) {
					e.next = null;
					size--;
				} else {
					int i = indexFor(e.ref.identityHash(), dest.length);
					e.next = dest[i];
					dest[i] = e;
				}
				e = next;
			}
		}
	}

	/**
	 * 指定されたオブジェクトがキャッシュにあれば、
	 * キャッシュから削除する。
	 * 
	 * @param o	キャッシュにあれば削除されるオブジェクト
	 * @return	指定されたオブジェクトがキャッシュにあった場合は <tt>true</tt>
	 */
	@SuppressWarnings("unchecked")
	public boolean remove(Object o) {
		Object k = maskNull(o);
		int h = hash(k);
		Entry[] tab = getTable();
		int i = indexFor(h, tab.length);
		Entry<E> prev = tab[i];
		Entry<E> cur = prev;
		
		while (cur != null) {
			Entry<E> next = cur.next;
			if (h == cur.ref.identityHash() && eq(k, cur.ref.get())) {
				modCount++;
				size--;
				if (prev == cur)
					tab[i] = next;
				else
					prev.next = next;
				cur.next = null;
				return true;
			}
			prev = cur;
			cur = next;
		}
		
		return false;
	}

	/**
	 * 指定されたコレクションのすべての要素をキャッシュに追加する。
	 * オペレーションの進行中に、指定されたコレクションが変更された
	 * 場合の、このオペレーションの動作は定義されていない。
	 * したがって、指定されたコレクションがこのコレクション自身であり、
	 * このコレクションが空ではない場合、この呼び出しの動作は定義
	 * されていない。
	 * 
	 * @param c	要素がキャッシュに追加されるコレクション
	 * @return	この呼び出しの結果、このキャッシュが変更された場合は <tt>true</tt>
	 * 
	 * @throws NullPointerException	指定されたコレクションが <tt>null</tt> の場合
	 */
	public boolean addAll(Collection<? extends E> c) {
		int numKeysToBeAdded = c.size();
		if (numKeysToBeAdded == 0) {
			return false;
		}
		
		if (numKeysToBeAdded > threshold) {
			int targetCapacity = (int)(numKeysToBeAdded / loadFactor + 1);
			if (targetCapacity > MAXIMUM_CAPACITY)
				targetCapacity = MAXIMUM_CAPACITY;
			int newCapacity = table.length;
			while (newCapacity < targetCapacity) {
				newCapacity <<= 1;
			}
			if (newCapacity > table.length) {
				resize(newCapacity);
			}
		}

		boolean modified = false;
		for (Iterator<? extends E> i = c.iterator(); i.hasNext(); ) {
			if (add(i.next())) {
				modified = true;
			}
		}
		return modified;
	}

	/**
	 * このキャッシュから、指定されたコレクションに含まれる要素を
	 * すべて削除する。
	 * 
	 * @param c	このキャッシュから削除される要素
	 * @return	この呼び出しの結果、このキャッシュが変更された場合は <tt>true</tt>
	 * 
	 * @throws NullPointerException	指定されたコレクションが <tt>null</tt> の場合
	 */
	@SuppressWarnings("unchecked")
	public boolean removeAll(Collection<?> c) {
		if (c.size() == 0) {
			return false;
		}
		
        boolean modified = false;
        Entry[] tab = getTable();
        
        for (Iterator<?> i = c.iterator(); i.hasNext(); ) {
        	Object k = maskNull(i.next());
        	int h = hash(k);
        	int idx = indexFor(h, tab.length);
        	Entry<E> prev = tab[idx];
        	Entry<E> cur = prev;
        	
    		while (cur != null) {
    			Entry<E> next = cur.next;
    			if (h == cur.ref.identityHash() && eq(k, cur.ref.get())) {
    				modCount++;
    				size--;
    				if (prev == cur)
    					tab[idx] = next;
    				else
    					prev.next = next;
    				cur.next = null;
    				modified = true;
    				break;
    			}
    			prev = cur;
    			cur = next;
    		}
        }
        
        expungeStaleEntries();
        return modified;
	}

	/**
	 * このキャッシュからすべての要素を削除する。
	 */
	public void clear() {
		while (queue.poll() != null);
		
		modCount++;
		Entry[] tab = table;
		for (int i = 0; i < tab.length; ++i) {
			tab[i] = null;
		}
		size = 0;
		
		while (queue.poll() != null);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * ハッシュ値を計算する。
	 * 
	 * @param h	ハッシュ値を計算する値
	 * @return	計算したハッシュ値
	 */
	static int hash(int h) {
		h += ~(h << 9);
		h ^=  (h >>> 14);
		h +=  (h << 4);
		h ^=  (h >>> 10);
		return h;
	}

	/**
	 * オブジェクトのハッシュ値を計算する。
	 * 
	 * @param key	ハッシュ値を計算するオブジェクト
	 * @return	計算したハッシュ値
	 */
	static int hash(Object key) {
		return hash(key.hashCode());
	}

	/**
	 * <tt>null</tt> 値をマスクしたキー値を取得する。
	 * 
	 * @param key	マスクするオブジェクト
	 * @return	マスクした値
	 */
	@SuppressWarnings("unchecked")
	static <T> T maskNull(T key) {
		return (key == null ? (T)NULL_KEY : key);
	}

	/**
	 * 格納されたキーから、マスクを解除する。
	 * 
	 * @param key	ハッシュテーブルに格納されているキー
	 * @return	マスクを解除した値
	 */
	static <T> T unmaskNull(T key) {
		return (key == NULL_KEY ? null : key);
	}

	/**
	 * オブジェクトの同値性を評価する。
	 * 
	 * @param x	評価するオブジェクトの一方
	 * @param y	評価するオブジェクトのもう一方
	 * @return	指定された引数が等しい場合は <tt>true</tt>
	 */
	static boolean eq(Object x, Object y) {
		return (x == y || x.equals(y));
	}

	/**
	 * 指定されたハッシュ値とハッシュテーブルの容量から、
	 * ハッシュテーブルのインデックスを取得する。
	 * 
	 * @param h		ハッシュ値
	 * @param length	ハッシュテーブルの容量
	 * @return			ハッシュテーブルのインデックス
	 */
	static int indexFor(int h, int length) {
		return (h & (length-1));
	}

	/**
	 * 破棄された参照をキャッシュから除去する。
	 */
	@SuppressWarnings("unchecked")
	private void expungeStaleEntries() {
		ReferenceElement<E> ref;
		while ((ref = (ReferenceElement<E>)queue.poll()) != null) {
			int h = ref.identityHash();
			int i = indexFor(h, table.length);
			
			Entry<E> prev = table[i];
			Entry<E> cur = prev;
			while (cur != null) {
				Entry<E> next = cur.next;
				if (cur.ref == ref) {
					if (prev.ref == ref)
						table[i] = next;
					else
						prev.next = next;
					cur.next = null;
					size--;
					break;
				}
				prev = cur;
				cur = next;
			}
		}
	}

	/**
	 * ハッシュテーブルを取得する。
	 * このメソッドの呼び出し時点で、破棄された参照は
	 * 破棄される。
	 * 
	 * @return	ハッシュテーブル
	 */
	private Entry[] getTable() {
		expungeStaleEntries();
		return table;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * キャッシュの要素を表すインタフェース
	 */
	static interface ReferenceElement<T> {
		public T get();
		public int identityHash();
	}

	/**
	 * 強参照の要素を格納するクラス。
	 */
	static class DirectElement<T> implements ReferenceElement<T> {
		private final T referent;
		private final int hash;
		public DirectElement(int hash, T referent, ReferenceQueue<? super T> queue) {
			this.hash = hash;
			this.referent = referent;
		}
		public T get() { return referent; }
		public int identityHash() { return hash; }
	}

	/**
	 * ソフト参照の要素を格納するクラス。
	 */
	static class SoftElement<T> extends SoftReference<T> implements ReferenceElement<T> {
		private final int hash;
		public SoftElement(int hash, T referent, ReferenceQueue<? super T> queue) {
			super(referent, queue);
			this.hash = hash;
		}
		public int identityHash() { return hash; }
	}

	/**
	 * 弱参照の要素を格納するクラス。
	 */
	static class WeakElement<T> extends WeakReference<T> implements ReferenceElement<T> {
		private final int hash;
		public WeakElement(int hash, T referent, ReferenceQueue<? super T> queue) {
			super(referent, queue);
			this.hash = hash;
		}
		public int identityHash() { return hash; }
	}

	/**
	 * 指定された参照種別に準じたキャッシュ要素を生成する。
	 * 
	 * @param refType	参照種別
	 * @param hash		ハッシュ値
	 * @param referent	格納する参照
	 * @param queue	参照キュー
	 * @return			生成された要素
	 */
	static <T> ReferenceElement<T> createReferenceElement(ReferenceType refType, int hash, T referent, ReferenceQueue<? super T> queue) {
		switch (refType) {
			case STRONG :
				return new DirectElement<T>(hash, referent, queue);
			case SOFT :
				return new SoftElement<T>(hash, referent, queue);
			case WEAK :
				return new WeakElement<T>(hash, referent, queue);
			default :
				throw new AssertionError();
		}
	}

	/**
	 * ハッシュテーブルの要素となるクラス。
	 */
	static class Entry<E> {
		private final ReferenceElement<E> ref;
		private Entry<E> next;
		
		Entry(ReferenceType refType, E referent, ReferenceQueue<? super E> queue, int hash, Entry<E> next) {
			this.ref = ReferenceCacheSet.<E>createReferenceElement(refType, hash, referent, queue);
			this.next = next;
		}
		
		public E element() {
			return ReferenceCacheSet.<E>unmaskNull(ref.get());
		}
		
		public boolean equals(Object o) {
			if (o instanceof Entry) {
				Entry e = (Entry)o;
				Object elem1 = element();
				Object elem2 = e.element();
				if (elem1 == elem2 || (elem1 != null && elem1.equals(elem2))) {
					return true;
				}
			}
			return false;
		}
		
		public int hashCode() {
			Object elem = element();
			return (elem == null ? 0 : elem.hashCode());
		}
		
		public String toString() {
			return String.valueOf(element());
		}
	}

	/**
	 * キャッシュのイテレータを実装するクラス。
	 */
	private abstract class HashIterator<T> implements Iterator<T> {
		int index;
		Entry<E> entry = null;
		Entry<E> lastReturned = null;
		int expectedModCount = modCount;
		
		Object nextKey = null;
		Object currentKey = null;
		
		HashIterator() {
			index = (size() != 0 ? table.length : 0);
		}
		
		@SuppressWarnings("unchecked")
		public boolean hasNext() {
			Entry[] t = table;
			
			while (nextKey == null) {
				Entry<E> e = entry;
				int i = index;
				while (e == null && i > 0) {
					e = t[--i];
				}
				entry = e;
				index = i;
				if (e == null) {
					currentKey = null;
					return false;
				}
				nextKey = e.ref.get();
				
				if (nextKey == null) {
					entry = entry.next;
				}
			}
			
			return true;
		}
		
		protected Entry<E> nextEntry() {
			if (modCount != expectedModCount)
				throw new ConcurrentModificationException();
			if (nextKey == null && !hasNext())
				throw new NoSuchElementException();
			
			lastReturned = entry;
			entry = entry.next;
			currentKey = nextKey;
			nextKey = null;
			return lastReturned;
		}
		
		public void remove() {
			if (lastReturned == null)
				throw new IllegalStateException();
			if (modCount != expectedModCount)
				throw new ConcurrentModificationException();
			
			ReferenceCacheSet.this.remove(currentKey);
			expectedModCount = modCount;
			lastReturned = null;
			currentKey = null;
		}
	}
	
	/**
	 * キャッシュのイテレータを実装するクラス。
	 */
	private class ElementIterator extends HashIterator<E> {
		public E next() {
			return nextEntry().element();
		}
	}
}
