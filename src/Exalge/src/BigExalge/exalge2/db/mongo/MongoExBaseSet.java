/*
 * @(#)MongoExBaseSet.java	0.992	2020/03/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MongoExBaseSet.java	0.991	2019/02/23
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MongoExBaseSet.java	0.990	2018/11/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package exalge2.db.mongo;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

import org.bson.Document;

import com.mongodb.MongoException;
import com.mongodb.MongoNamespace;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import exalge2.ExBase;
import exalge2.ExBasePattern;
import exalge2.ExBasePatternSet;
import exalge2.ExBaseSet;
import exalge2.db.BigExBaseSet;
import redundantalge.db.BigIterator;
import redundantalge.db.mongo.MongoAlgeError;
import redundantalge.db.mongo.MongoSession;
import redundantalge.db.mongo.MongoUtil;

/**
 * 大容量の交換代数基底集合を保持するクラス。
 * <p>ストレージとして MongoDB を利用する。
 * 
 * <p>交換代数基底 <code>ExBase</code> インスタンスの集合であり、
 * このオブジェクトが生成したコレクションである限り、同じ値が重複して含まれることはない。
 * <p>
 * このクラスでは、<tt>null</tt> を許容しない。
 * <br>
 * また、<b>この実装は同期化されない</b>。
 * <p>
 * <b>《入出力フォーマット》</b>
 * <br>
 * <code>MongoExBaseSet</code> の MongoDB におけるデータ構造は、次の通りである。
 * <pre><code>
 * {
 *   &quot;hat&quot; : &lt;boolean&gt;(NO_HAT=false | HAT=true)
 *   &quot;name&quot; : &lt;string&gt;
 *   &quot;unit&quot; : &lt;string&gt;
 *   &quot;time&quot; : &lt;string&gt;
 *   &quot;subject&quot; : &lt;string&gt;
 * }
 * </code></pre>
 * 
 * @version 0.992
 * @since 0.990
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public class MongoExBaseSet implements BigExBaseSet
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	/** 一時的なコレクション名のプレフィックス **/
	static public final String	TEMP_COLNAME_PREFIX	= "Tbs";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	protected MongoSession				_mongo_session;
	protected MongoCollection<Document>	_mongo_col;
	protected AtomicLong				_modCount = new AtomicLong(0L);

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定された MongoDB セッションにおいて、一時的なコレクションをストレージとする新しいインスタンスを生成する。
	 * <p>このメソッドで作成されたコレクションは、MongoDB セッションが切断されるときに破棄される。
	 * @param session	MongoDB セッションオブジェクト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public MongoExBaseSet(MongoSession session) {
		this(session, null);
	}

	/**
	 * 指定された MongoDB セッションにおいて、指定されたコレクションをストレージとする新しいインスタンスを生成する。
	 * <p><em>collection</em> に <tt>null</tt> もしくは空文字が指定された場合、一時的なコレクションをストレージとする。
	 * 一時的なコレクションは、MongoDB セッションが切断されるときに破棄される。
	 * @param session		MongoDB セッションオブジェクト
	 * @param collection	MongoDB コレクション名、<tt>null</tt> もしくは空文字の場合は一時的なコレクション
	 * @throws NullPointerException	<em>session</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	コレクション名が有効ではない場合
	 */
	public MongoExBaseSet(MongoSession session, String collection) {
		if (session == null)
			throw new NullPointerException("MongoSession object is null.");
		_mongo_session = session;
		if (collection != null && !collection.isEmpty()) {
			// specified collection
			_mongo_col = session.getPersistentCollection(collection);
		}
		else {
			// temporary collection
			_mongo_col = session.getTemporaryCollection(TEMP_COLNAME_PREFIX);
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * このオブジェクトの MongoDB コレクションを、指定されたコレクション名に変更する。
	 * このコレクションが一時的コレクションの場合は、指定されたコレクション名に変更した後、セッション切断時の削除対象から除外される。
	 * @param newCollectionName	新しいコレクション名
	 * @throws IllegalArgumentException	コレクション名が有効ではない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合、指定されたコレクション名がすでに存在している場合
	 */
	public void toPersistent(String newCollectionName) {
		MongoUtil.validCollectionName(newCollectionName);
		MongoNamespace oldName = _mongo_col.getNamespace();
		MongoNamespace newName = new MongoNamespace(oldName.getDatabaseName(), newCollectionName);
		try {
			_mongo_col.renameCollection(newName);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to persist of MongoExBaseSet[" + _mongo_col.getNamespace().toString() + "] :", ex);
		}
		_mongo_session.unregisterTemporaryCollection(oldName);
	}
	
	/**
	 * このオブジェクトが管理するストレージを削除する。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このメソッドは、このオブジェクトが管理するストレージが一時的なものかどうかに関係なく、
	 * 管理対象のストレージを削除するので、注意すること。
	 * なお、このメソッドを呼び出した後、このオブジェクトを利用するとデータが空の状態となる。
	 * </blockquote>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合、指定されたコレクション名がすでに存在している場合
	 */
	@Override
	public void delete() {
		try {
			_mongo_col.drop();
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to drop collection of MongoExBaseSet[" + _mongo_col.getNamespace().toString() + "] :", ex);
		}
		//_mongo_session.unregisterTemporaryCollection(_mongo_col.getNamespace());
	}
	
	/**
	 * このオブジェクトのハッシュ値を返す。
	 * @return	ハッシュ値
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public int hashCode() {
		MongoCursor<? extends Document> cursor = null;
		int h = 0;
		try {
			cursor = _mongo_col.find().projection(MongoUtil.MONGO_FILTER_WITHOUT_OBJID).iterator();
			while (cursor.hasNext()) {
				h += cursor.next().hashCode();
			}
			return h;
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to calculate hash code from " + _mongo_col.getNamespace().toString() + ":", ex);
		}
		finally {
			if (cursor != null) {
				try {
					cursor.close();
				} catch (Throwable ignoreEx) {}
				cursor = null;
			}
		}
	}

	/**
	 * 指定されたオブジェクトとこのオブジェクトが等しいかを判定する。
	 * @param obj	判定するオブジェクト
	 * @return	等しい場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof MongoExBaseSet) {
			MongoExBaseSet that = (MongoExBaseSet)obj;
			return MongoUtil.equalsCollectionsWithoutID(MongoDelegateDocExBase.MONGO_SORT_EXBASE_ALL, this._mongo_col, that._mongo_col);
		}
		else if (obj instanceof BigExBaseSet) {
			BigExBaseSet that = (BigExBaseSet)obj;
			long thisSize = this.size();
			if (thisSize == that.size()) {
				if (thisSize > 0L) {
					return containsAll(that);
				} else {
					return true;
				}
			}
		}
		else if (obj instanceof ExBaseSet) {
			ExBaseSet that = (ExBaseSet)obj;
			long thisSize = this.size();
			if (thisSize == that.size()) {
				if (thisSize > 0L) {
					return containsAll(that);
				} else {
					return true;
				}
			}
		}
		
		// not equals
		return false;
	}

	/**
	 * このオブジェクトの要素が空かどうかを判定する。
	 * @return	要素が空の場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean isEmpty() {
		return (size() != 0L);
	}
	
	/**
	 * このオブジェクトが保持する要素数を返す。
	 * @return	要素数
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public long size() {
		try {
			return _mongo_col.countDocuments();
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to count documents in MongoExBase[" + _mongo_col.getNamespace().toString() + "] :", ex);
		}
	}

	/**
	 * 指定された基底が含まれているかを判定する。
	 * @param base	判定する基底
	 * @return	含まれている場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean contains(ExBase base) {
		if (base == null)
			return false;
		try {
			return (_mongo_col.countDocuments(MongoDelegateDocExBase.makeExBaseDocument(base)) > 0L);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to contain ExBase in MongoExBase[" + _mongo_col.getNamespace().toString() + "] :", ex);
		}
	}

	/**
	 * 指定されたコレクションに含まれる要素のすべてが、自身に含まれているかどうかを判定する。
	 * @param c	判定するコレクション
	 * @return	すべての要素が含まれている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean containsAll(Collection<? extends ExBase> c) {
		for (ExBase base : c) {
			if (!contains(base))
				return false;
		}
		return true;
	}

	/**
	 * 指定されたコレクションに含まれる要素のすべてが、自身に含まれているかどうかを判定する。
	 * @param set	判定するコレクション
	 * @return	すべての要素が含まれている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean containsAll(BigExBaseSet set) {
		for (ExBase base : set) {
			if (!contains(base))
				return false;
		}
		return true;
	}

	/**
	 * 指定されたコレクションに含まれる要素のどれか一つが、自身に含まれているかどうかを判定する。
	 * @param c	判定するコレクション
	 * @return	どれか一つの要素が含まれている場合は <tt>true</tt>、一つも含まれていない場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean containsAny(Collection<? extends ExBase> c) {
		for (ExBase base : c) {
			if (contains(base))
				return true;
		}
		return false;
	}
	
	/**
	 * 指定されたコレクションに含まれる要素のどれか一つが、自身に含まれているかどうかを判定する。
	 * @param set	判定するコレクション
	 * @return	どれか一つの要素が含まれている場合は <tt>true</tt>、一つも含まれていない場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean containsAny(BigExBaseSet set) {
		for (ExBase base : set) {
			if (contains(base))
				return true;
		}
		return false;
	}
	
	/**
	 * このオブジェクトのイテレーターを返す。
	 * @return	イテレーターオブジェクト
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public BigIterator<ExBase> iterator() {
		// TODO: カーソルのクローズ処理を今後考慮
		try {
			//--- ソート済みイテレータにする
			MongoCursor<? extends Document> cursor = _mongo_col.find().sort(MongoDelegateDocExBase.MONGO_SORT_EXBASE_ALL).iterator();
			return new MongoExBaseSetIterator(cursor);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get sorted cursor from MongoExBaseSet[" + _mongo_col.getNamespace().toString() + "] :", ex);
		}
	}

	/**
	 * この集合から、すべての要素を削除する。
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public void clear() {
		MongoUtil.clearAllDocuments(_mongo_col);
		_modCount.incrementAndGet();
	}

	/**
	 * 指定された基底を、この集合に追加する。
	 * @param base	追加する基底
	 * @return	追加された場合は <tt>true</tt>、すでに存在する場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean add(ExBase base) {
		Document data = MongoDelegateDocExBase.makeExBaseDocument(base);
		if (MongoUtil.insertDocumentIfNotExist(_mongo_col, data)) {
			_modCount.incrementAndGet();
			return true;
		}
		return false;
	}

	/**
	 * 指定された基底を、この集合から削除する。
	 * @param base	削除する基底
	 * @return	削除された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean remove(ExBase base) {
		if (base == null)
			return false;
		Document where = MongoDelegateDocExBase.makeExBaseDocument(base);
		long removed = MongoUtil.deleteMatchedDocument(_mongo_col, where, true);
		if (removed > 0L) {
			_modCount.incrementAndGet();
			return true;
		}
		return false;
	}

	/**
	 * 指定されたコレクションに含まれるすべての基底を、この集合に追加する。
	 * @param c	追加する基底のコレクション
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean addAll(Collection<? extends ExBase> c) {
		long added = 0L;
		Iterator<? extends ExBase> it = c.iterator();
		while (it.hasNext()) {
			ExBase base = it.next();
			if (base != null) {
				Document data = MongoDelegateDocExBase.makeExBaseDocument(base);
				if (MongoUtil.insertDocumentIfNotExist(_mongo_col, data)) {
					_modCount.incrementAndGet();
					added++;
				}
			}
		}
		return (added > 0L);
	}
	
	/**
	 * 指定された基底集合に含まれるすべての基底を、この集合に追加する。
	 * @param set	追加する基底の集合
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean addAll(BigExBaseSet set) {
		long added = 0L;
		Iterator<? extends ExBase> it = set.iterator();
		while (it.hasNext()) {
			ExBase base = it.next();
			if (base != null) {
				Document data = MongoDelegateDocExBase.makeExBaseDocument(base);
				if (MongoUtil.insertDocumentIfNotExist(_mongo_col, data)) {
					_modCount.incrementAndGet();
					added++;
				}
			}
		}
		return (added > 0L);
	}

	/**
	 * 指定されたコレクションに含まれるすべての基底を、この集合から削除する。
	 * @param c	削除する基底のコレクション
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean removeAll(Collection<? extends ExBase> c) {
		long totalRemoved = 0L;
		Iterator<? extends ExBase> it = c.iterator();
		while (it.hasNext()) {
			ExBase base = it.next();
			if (base != null) {
				Document where = MongoDelegateDocExBase.makeExBaseDocument(base);
				long removed = MongoUtil.deleteMatchedDocument(_mongo_col, where, true);
				if (removed > 0L) {
					_modCount.incrementAndGet();
					totalRemoved += removed;
				}
			}
		}
		return (totalRemoved > 0L);
	}
	
	/**
	 * 指定された集合に含まれるすべての基底を、この集合から削除する。
	 * @param set	削除する基底の集合
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean removeAll(BigExBaseSet set) {
		long totalRemoved = 0L;
		Iterator<? extends ExBase> it = set.iterator();
		while (it.hasNext()) {
			ExBase base = it.next();
			if (base != null) {
				Document where = MongoDelegateDocExBase.makeExBaseDocument(base);
				long removed = MongoUtil.deleteMatchedDocument(_mongo_col, where, true);
				if (removed > 0L) {
					_modCount.incrementAndGet();
					totalRemoved += removed;
				}
			}
		}
		return (totalRemoved > 0L);
	}

	/**
	 * 指定されたコレクションに含まれるすべての基底のみを残し、その他の基底をこの集合から削除する。
	 * @param c	残す基底のコレクション
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean retainAll(Collection<? extends ExBase> c) {
		long totalRemoved = 0L;
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = _mongo_col.find().iterator();
			while (cursor.hasNext()) {
				ExBase base = MongoDelegateDocExBase.toExBase(cursor.next());
				if (!c.contains(base)) {
					cursor.remove();
					_modCount.incrementAndGet();
					totalRemoved++;
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to retain all elements of the specified collection at " + _mongo_col.getNamespace().toString() + ":", ex);
		}
		finally {
			if (cursor != null) {
				try {
					cursor.close();
				} catch (Throwable ignoreEx) {}
			}
		}
		return (totalRemoved > 0L);
	}

	/**
	 * 指定された基底集合に含まれるすべての基底のみを残し、その他の基底をこの集合から削除する。
	 * @param set	残す基底の集合
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean retainAll(BigExBaseSet set) {
		long totalRemoved = 0L;
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = _mongo_col.find().iterator();
			while (cursor.hasNext()) {
				ExBase base = MongoDelegateDocExBase.toExBase(cursor.next());
				if (!set.contains(base)) {
					cursor.remove();
					_modCount.incrementAndGet();
					totalRemoved++;
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to retain all elements of the specified collection at " + _mongo_col.getNamespace().toString() + ":", ex);
		}
		finally {
			if (cursor != null) {
				try {
					cursor.close();
				} catch (Throwable ignoreEx) {}
			}
		}
		return (totalRemoved > 0L);
	}

	/**
	 * このオブジェクトの複製を生成する。
	 * @return 複製された <code>MongoExBaseSet</code> オブジェクト
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExBaseSet copy() {
		MongoExBaseSet newSet = new MongoExBaseSet(_mongo_session);
		MongoUtil.duplicateCollection(newSet._mongo_col, this._mongo_col);
		return newSet;
	}

	/**
	 * 基底集合を連結した、新しい基底集合のインスタンスを返す。
	 * <br>
	 * 基底集合では、要素の基底が重複することはないため、{@link #union(ExBaseSet)} と
	 * 同じ結果を返す。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set	連結する基底集合
	 * @return		自身と引数に指定された集合を連結した新しい基底集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExBaseSet addition(ExBaseSet set) {
		MongoExBaseSet newSet = copy();
		newSet.addAll(set);
		return newSet;
	}
	
	/**
	 * 基底集合を連結した、新しい基底集合のインスタンスを返す。
	 * <br>
	 * 基底集合では、要素の基底が重複することはないため、{@link #union(BigExBaseSet)} と
	 * 同じ結果を返す。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set	連結する基底集合
	 * @return		自身と引数に指定された集合を連結した新しい基底集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExBaseSet addition(BigExBaseSet set) {
		MongoExBaseSet newSet = copy();
		newSet.addAll(set);
		return newSet;
	}

	/**
	 * 引数に指定された基底集合の要素を除いた、新しい基底集合の
	 * インスタンスを返す。
	 * <br>
	 * 基底集合では、要素の基底が重複することはないため、{@link #difference(ExBaseSet)} と
	 * 同じ結果を返す。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set	自身の集合要素から取り除く要素を持つ基底集合
	 * @return		自身の集合要素から引数に指定された集合要素を取り除いた、
	 * 				新しい基底集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExBaseSet subtraction(ExBaseSet set) {
		MongoExBaseSet newSet = copy();
		newSet.removeAll(set);
		return newSet;
	}

	/**
	 * 引数に指定された基底集合の要素を除いた、新しい基底集合の
	 * インスタンスを返す。
	 * <br>
	 * 基底集合では、要素の基底が重複することはないため、{@link #difference(BigExBaseSet)} と
	 * 同じ結果を返す。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set	自身の集合要素から取り除く要素を持つ基底集合
	 * @return		自身の集合要素から引数に指定された集合要素を取り除いた、
	 * 				新しい基底集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExBaseSet subtraction(BigExBaseSet set) {
		MongoExBaseSet newSet = copy();
		newSet.removeAll(set);
		return newSet;
	}

	/**
	 * 指定の基底集合との和を返す。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set 和を取る基底集合の一方
	 * @return 二つの基底集合の和となるインスタンス
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExBaseSet union(ExBaseSet set) {
		MongoExBaseSet newSet = copy();
		newSet.addAll(set);
		return newSet;
	}

	/**
	 * 指定の基底集合との和を返す。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set 和を取る基底集合の一方
	 * @return 二つの基底集合の和となるインスタンス
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExBaseSet union(BigExBaseSet set) {
		MongoExBaseSet newSet = copy();
		newSet.addAll(set);
		return newSet;
	}

	/**
	 * 指定の基底集合との積(共通部分)を返す。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set 積を取る基底集合の一方
	 * @return 二つの基底集合の積となるインスタンス
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExBaseSet intersection(ExBaseSet set) {
		MongoExBaseSet newSet = copy();
		newSet.retainAll(set);
		return newSet;
	}

	/**
	 * 指定の基底集合との積(共通部分)を返す。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set 積を取る基底集合の一方
	 * @return 二つの基底集合の積となるインスタンス
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExBaseSet intersection(BigExBaseSet set) {
		MongoExBaseSet newSet = copy();
		newSet.retainAll(set);
		return newSet;
	}

	/**
	 * 指定の基底集合との差を返す。
	 * <br>
	 * 集合の差は、このインスタンスの基底集合から、指定された基底集合を除いた集合となる。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set 差し引く基底の集合
	 * @return 指定された基底集合を差し引いた結果となるインスタンス
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExBaseSet difference(ExBaseSet set) {
		MongoExBaseSet newSet = copy();
		newSet.removeAll(set);
		return newSet;
	}

	/**
	 * 指定の基底集合との差を返す。
	 * <br>
	 * 集合の差は、このインスタンスの基底集合から、指定された基底集合を除いた集合となる。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set 差し引く基底の集合
	 * @return 指定された基底集合を差し引いた結果となるインスタンス
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExBaseSet difference(BigExBaseSet set) {
		MongoExBaseSet newSet = copy();
		newSet.removeAll(set);
		return newSet;
	}

	/**
	 * この基底集合から、ハットなし基底のみを取り出す。
	 * 
	 * @return	この集合に含まれるハットなし基底の集合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExBaseSet getNoHatBases() {
		MongoExBaseSet newSet = new MongoExBaseSet(this._mongo_session);
		for (ExBase base : this) {
			if (base.isNoHat()) {
				newSet.add(base);
			}
		}
		return newSet;
	}

	/**
	 * この基底集合から、ハット基底のみを取り出す。
	 * 
	 * @return	この集合に含まれるハット基底の集合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExBaseSet getHatBases() {
		MongoExBaseSet newSet = new MongoExBaseSet(this._mongo_session);
		for (ExBase base : this) {
			if (base.isHat()) {
				newSet.add(base);
			}
		}
		return newSet;
	}

	/**
	 * 指定の基底パターンに一致する基底の集合を取得する。
	 * <br>
	 * 指定された基底のハットキー以外の基底キーに一致する基底のみを
	 * 取得し、その集合を返す。ハットキー以外の基底キーにアスタリスク
	 * 文字<code>('*')</code>が含まれている場合、0文字以上の任意の文字に
	 * マッチするワイルドカードとみなす。
	 * <br>
	 * 基底パターンに一致する基底が存在しない場合、このメソッドは
	 * 空の基底集合を返す。
	 * 
	 * @param base	基底パターンとみなす交換代数基底
	 * @return		パターンに一致した基底の集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExBaseSet getMatchedBases(ExBase base) {
		ExBasePattern pattern = new ExBasePattern(base, true);
		return getMatchedBases(pattern);
	}

	/**
	 * 指定の基底パターンに一致する基底の集合を取得する。
	 * <br>
	 * 指定された基底パターンに一致する基底のみを取得し、その
	 * 集合を返す。
	 * <br>
	 * 基底パターンに一致する基底が存在しない場合、このメソッドは
	 * 空の基底集合を返す。
	 * 
	 * @param pattern	基底パターン
	 * @return			パターンに一致した基底の集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExBaseSet getMatchedBases(ExBasePattern pattern) {
		MongoExBaseSet newSet = new MongoExBaseSet(this._mongo_session);
		for (ExBase base : this) {
			if (pattern.matches(base)) {
				newSet.add(base);
			}
		}
		return newSet;
	}

	/**
	 * 指定の集合の基底パターンに一致する基底の集合を取得する。
	 * <br>
	 * 指定された集合に含まれる基底の、ハットキー以外の基底キーに
	 * 一致する基底のみを取得し、その集合を返す。ハットキー以外の
	 * 基底キーにアスタリスク文字<code>('*')</code>が含まれている場合、
	 * 0文字以上の任意の文字にマッチするワイルドカードとみなす。
	 * <br>
	 * 返される基底集合に含まれる基底は、指定の集合に含まれる基底パターンの
	 * どれかに一致した基底となる。基底パターンに一致する基底が存在しない場合、
	 * このメソッドは空の基底集合を返す。
	 * 
	 * @param bases	基底パターンとみなす基底の集合
	 * @return		パターンに一致した基底の集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExBaseSet getMatchedBases(ExBaseSet bases) {
		final ExBasePatternSet patterns = new ExBasePatternSet(bases, true);
		return getMatchedBases(patterns);
	}

	// TODO: 将来的に大容量化に対応が必要かは、要検討
	/**
	 * 指定の集合の基底パターンに一致する基底の集合を取得する。
	 * <br>
	 * 指定された集合に含まれる基底の、ハットキー以外の基底キーに
	 * 一致する基底のみを取得し、その集合を返す。ハットキー以外の
	 * 基底キーにアスタリスク文字<code>('*')</code>が含まれている場合、
	 * 0文字以上の任意の文字にマッチするワイルドカードとみなす。
	 * <br>
	 * 返される基底集合に含まれる基底は、指定の集合に含まれる基底パターンの
	 * どれかに一致した基底となる。基底パターンに一致する基底が存在しない場合、
	 * このメソッドは空の基底集合を返す。
	 * <p><b>注意</b>
	 * <blockquote>
	 * このメソッドは、指定された交換代数基底集合から、交換代数基底パターン集合を「メモリ」上に生成する。
	 * そのため、基底集合の量によってはメモリ不足が発生する恐れがあるので、注意すること。
	 * </blockquote>
	 * 
	 * @param bases	基底パターンとみなす基底の集合
	 * @return		パターンに一致した基底の集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExBaseSet getMatchedBases(BigExBaseSet bases) {
		final ExBasePatternSet patterns = new ExBasePatternSet();
		for (ExBase base : bases) {
			patterns.add(new ExBasePattern(base, true));
		}
		return getMatchedBases(patterns);
	}

	/**
	 * 指定の集合の基底パターンに一致する基底の集合を取得する。
	 * <br>
	 * 指定された集合に含まれる基底パターンに一致する基底のみを取得し、
	 * その集合を返す。
	 * <br>
	 * 返される基底集合に含まれる基底は、指定の集合に含まれる基底パターンの
	 * どれかに一致した基底となる。基底パターンに一致する基底が存在しない場合、
	 * このメソッドは空の基底集合を返す。
	 * 
	 * @param patterns	基底パターンの集合
	 * @return			パターンに一致した基底の集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExBaseSet getMatchedBases(ExBasePatternSet patterns) {
		MongoExBaseSet newSet = new MongoExBaseSet(this._mongo_session);
		for (ExBase base : this) {
			if (patterns.matches(base)) {
				newSet.add(base);
			}
		}
		return newSet;
	}
	
	/**
	 * 基底集合に含まれる全てのハットなし基底をハット基底に置き換えた、
	 * 新しい基底集合を返す。このメソッドが返す基底集合に含まれる基底は、
	 * 全てハット基底となる。
	 * <p>このメソッドは、基底のハットキーを {@link exalge2.ExBase#HAT} にした
	 * 基底に置き換える。ハットキーがすでに {@link exalge2.ExBase#HAT} のものは、
	 * そのまま格納される。
	 * 
	 * @return	すべての基底にハット(^)を付加した、新しい基底集合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExBaseSet setHat() {
		MongoExBaseSet newSet = new MongoExBaseSet(this._mongo_session);
		try {
			MongoCursor<? extends Document> cursor = _mongo_col.find().projection(MongoUtil.MONGO_FILTER_WITHOUT_OBJID).iterator();
			try {
				while (cursor.hasNext()) {
					Document newDoc = cursor.next();
					newDoc.append(MongoDelegateDocExBase.MONGO_KEY_HAT, true);
					MongoUtil.insertDocumentIfNotExist(newSet._mongo_col, newDoc);
				}
			}
			finally {
				try {
					cursor.close();
				} catch (Throwable ignoreEx) {}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to convert hat-key to NO_HAT in MongoExBaseSet[" + newSet._mongo_col.getNamespace().toString() + "] :", ex);
		}
		return newSet;
	}
	
	/**
	 * 基底集合に含まれる全てのハット基底をハットなし基底に置き換えた、
	 * 新しい基底集合を返す。このメソッドが返す基底集合に含まれる基底は、
	 * 全てハットなし基底となる。
	 * <p>このメソッドは、基底のハットキーを {@link exalge2.ExBase#NO_HAT} にした
	 * 基底に置き換える。ハットキーがすでに {@link exalge2.ExBase#NO_HAT} のものは、
	 * そのまま格納される。
	 * 
	 * @return	すべての基底のハット(^)を除去した、新しい基底集合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExBaseSet removeHat() {
		MongoExBaseSet newSet = new MongoExBaseSet(this._mongo_session);
		try {
			MongoCursor<? extends Document> cursor = _mongo_col.find().projection(MongoUtil.MONGO_FILTER_WITHOUT_OBJID).iterator();
			try {
				while (cursor.hasNext()) {
					Document newDoc = cursor.next();
					newDoc.append(MongoDelegateDocExBase.MONGO_KEY_HAT, false);
					MongoUtil.insertDocumentIfNotExist(newSet._mongo_col, newDoc);
				}
			}
			finally {
				try {
					cursor.close();
				} catch (Throwable ignoreEx) {}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to convert hat-key to NO_HAT in MongoExBaseSet[" + newSet._mongo_col.getNamespace().toString() + "] :", ex);
		}
		return newSet;
	}

	/**
	 * この基底集合にすべての基底について、ハットありとハットなしの両方を含む基底集合を生成する。
	 * @return	この集合に含まれるすべての基底についてハットありとハットなしの基底を含む新しい基底集合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExBaseSet generalBases() {
		MongoExBaseSet newSet = removeHat();
		try {
			MongoCursor<? extends Document> cursor = _mongo_col.find().projection(MongoUtil.MONGO_FILTER_WITHOUT_OBJID).iterator();
			try {
				while (cursor.hasNext()) {
					Document newDoc = cursor.next();
					newDoc.append(MongoDelegateDocExBase.MONGO_KEY_HAT, true);
					MongoUtil.insertDocumentIfNotExist(newSet._mongo_col, newDoc);
				}
			}
			finally {
				try {
					cursor.close();
				} catch (Throwable ignoreEx) {}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to convert hat-key to general in MongoExBaseSet[" + newSet._mongo_col.getNamespace().toString() + "] :", ex);
		}
		return newSet;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Override
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	protected class MongoExBaseSetIterator implements BigIterator<ExBase> {
		MongoCursor<? extends Document>	_itCursor;
		long _expectedModCount;
		protected Object _lastObjectId;
		
		MongoExBaseSetIterator(MongoCursor<? extends Document> cursor) {
			_itCursor = cursor;
			_expectedModCount = _modCount.get();
		}

		@Override
		public boolean hasNext() {
			try {
				return (_itCursor==null ? false : _itCursor.hasNext());
			} catch (MongoException ex) {
				throw new MongoAlgeError("MongoExBaseSet's iterator couldn't check next element in MongoExBaseSet[" + _mongo_col.getNamespace().toString() + "] :", ex);
			}
		}
		
		@Override
		public ExBase next() {
			if (_expectedModCount != _modCount.get())
				throw new ConcurrentModificationException();
			if (_itCursor == null)
				throw new NoSuchElementException("MongoExBaseSet's cursor was closed.");
			try {
				Document docExBase = _itCursor.next();
				_lastObjectId = docExBase.get("_id");
				ExBase base = MongoDelegateDocExBase.toExBase(docExBase);
				if (!_itCursor.hasNext()) {
					closeCursor();
				}
				return base;
			} catch (MongoException ex) {
				throw new MongoAlgeError("MongoExBaseSet's iterator couldn't get next element in MongoExBaseSet[" + _mongo_col.getNamespace().toString() + "] :", ex);
			}
		}

		@Override
		public void remove() {
			if (_expectedModCount != _modCount.get())
				throw new ConcurrentModificationException();
			try {
				if (_lastObjectId != null) {
					_mongo_col.deleteOne(new Document("_id", _lastObjectId));
					_lastObjectId = null;
					_expectedModCount = _modCount.incrementAndGet();
				}
			}
			catch (MongoException ex) {
				throw new MongoAlgeError("MongoExBaseSet's iterator couldn't remove element in MongoExBaseSet[" + _mongo_col.getNamespace().toString() + "] :", ex);
			}
		}
		
		@Override
		public void closeCursor() {
			if (_itCursor != null) {
				try {
					_itCursor.close();
					_itCursor = null;
				} catch (Throwable ignoreEx) {}
			}
		}

		@Override
		protected void finalize() throws Throwable {
			closeCursor();
			super.finalize();
		}
	}
}
