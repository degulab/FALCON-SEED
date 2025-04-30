/*
 * @(#)MongoExalgeSet.java	0.992	2020/03/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MongoExalgeSet.java	0.991	2019/02/23
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MongoExalgeSet.java	0.990	2018/11/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package exalge2.db.mongo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoException;
import com.mongodb.MongoNamespace;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;

import exalge2.ExAlgeSet;
import exalge2.ExBase;
import exalge2.ExBasePattern;
import exalge2.ExBasePatternSet;
import exalge2.ExBaseSet;
import exalge2.Exalge;
import exalge2.db.BigExAlgeSet;
import exalge2.db.BigExAlgeSetInnerElement;
import exalge2.db.BigExBaseSet;
import exalge2.db.BigExalge;
import exalge2.db.BigExalgeElement;
import redundantalge.db.BigIterator;
import redundantalge.db.mongo.MongoAlgeError;
import redundantalge.db.mongo.MongoSession;
import redundantalge.db.mongo.MongoUtil;

/**
 * 大容量の交換代数集合を保持するクラス。
 * <p>ストレージとして MongoDB を利用する。
 * 
 * <p>交換代数元のリストとして実装されている。
 * <p><b>この実装は同期化されない</b>。
 * <p>
 * <b>《入出力フォーマット》</b>
 * <br>
 * <code>MongoExAlgeSet</code> の MongoDB におけるデータ構造は、次の通りである。
 * <pre><code>
 * {
 *   &quot;elem_id&quot; : &lt;交換代数元 ID(UUID)&gt;
 *   &quot;base&quot; : {
 *     &quot;hat&quot; : &lt;boolean&gt;(NO_HAT=false | HAT=true)
 *     &quot;name&quot; : &lt;string&gt;
 *     &quot;unit&quot; : &lt;string&gt;
 *     &quot;time&quot; : &lt;string&gt;
 *     &quot;subject&quot; : &lt;string&gt;
 *   },
 *   &quot;value&quot; : &lt;null or BigDecimal&gt;
 * }
 * </code></pre>
 * 交換代数元 ID が同一のドキュメントは、同じ交換代数元に所属していることを示す。
 * 
 * @version 0.992
 * @since 0.990
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public class MongoExAlgeSet implements BigExAlgeSet<MongoExalge>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	///** 一時的なコレクション名(キーのコレクション)のプレフィックス **/
	//static public final String	TEMP_KEYS_COLNAME_PREFIX	= "Task";
	/** 一時的なコレクション名(値のコレクション)のプレフィックス **/
	static public final String	TEMP_VALS_COLNAME_PREFIX	= "Tas";
	
	static protected final String	MONGO_KEY_ELEM_COUNT	= "elemCount";
	
	static protected final List<Bson>	MONGO_AGGRE_ELEM_COUNT;
	
	static protected final List<Bson>	MONGO_AGGRE_ELEM_ITERABLE;
	
	static {
		//--- 交換代数集合の交換代数元数をカウントするパイプライン
		MONGO_AGGRE_ELEM_COUNT = new ArrayList<Bson>();
		MONGO_AGGRE_ELEM_COUNT.add(Aggregates.group("$" + MongoDelegateDocExAlgeSetElem.MONGO_KEY_SETID));
		MONGO_AGGRE_ELEM_COUNT.add(Aggregates.count(MONGO_KEY_ELEM_COUNT));
		//--- 交換代数集合の交換代数元 ID のみを保持する昇順ソートされたビューを生成するパイプライン
		MONGO_AGGRE_ELEM_ITERABLE = new ArrayList<Bson>();
		MONGO_AGGRE_ELEM_ITERABLE.add(Aggregates.group("$" + MongoDelegateDocExAlgeSetElem.MONGO_KEY_SETID));
		MONGO_AGGRE_ELEM_ITERABLE.add(Aggregates.project(new Document(MongoDelegateDocExAlgeSetElem.MONGO_KEY_SETID, "$_id").append("_id", 0)));
		MONGO_AGGRE_ELEM_ITERABLE.add(Aggregates.sort(Sorts.ascending(MongoDelegateDocExAlgeSetElem.MONGO_KEY_SETID)));
	}

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	protected MongoSession				_mongo_session;
	protected MongoCollection<Document>	_mongo_vals_col;
	protected final AtomicLong			_modCount = new AtomicLong(0L);

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定された MongoDB セッションにおいて、一時的なコレクションをストレージとする新しいインスタンスを生成する。
	 * <p>このメソッドで作成されたコレクションは、MongoDB セッションが切断されるときに破棄される。
	 * @param session	MongoDB セッションオブジェクト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public MongoExAlgeSet(MongoSession session) {
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
	public MongoExAlgeSet(MongoSession session, String collection) {
		if (session == null)
			throw new NullPointerException("MongoSession object is null.");
		_mongo_session = session;
		if (collection != null && !collection.isEmpty()) {
			// specified collection
			_mongo_vals_col = session.getPersistentCollection(collection);
		}
		else {
			// temporary collection
			_mongo_vals_col = session.getTemporaryCollection(TEMP_VALS_COLNAME_PREFIX);
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
		MongoNamespace oldName = _mongo_vals_col.getNamespace();
		MongoNamespace newName = new MongoNamespace(oldName.getDatabaseName(), newCollectionName);
		try {
			_mongo_vals_col.renameCollection(newName);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to persist of MongoExAlgeSet[" + _mongo_vals_col.getNamespace().toString() + "] :", ex);
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
			_mongo_vals_col.drop();
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to drop collection of MongoExAlgeSet[" + _mongo_vals_col.getNamespace().toString() + "] :", ex);
		}
		_mongo_session.unregisterTemporaryCollection(_mongo_vals_col.getNamespace());
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * このオブジェクトのハッシュ値を返す。
	 * @return	ハッシュ値
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public int hashCode() {
		int h = 0;
		Iterator<MongoExalge> it = iterator();
		while (it.hasNext()) {
			h = 31 * h + it.next().hashCode();
		}
		return h;
	}

	/**
	 * 指定されたオブジェクトとこのオブジェクトが等しいかを判定する。
	 * @param obj	判定するオブジェクト
	 * @return	等しい場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		
		if (!(obj instanceof MongoExAlgeSet)) {
			return false;
		}
		
		MongoExAlgeSet that = (MongoExAlgeSet)obj;
		return equalsAnotherExAlgeSetCollection(that._mongo_vals_col);
	}

	/**
	 * このオブジェクトの要素が空かどうかを判定する。
	 * @return	要素が空の場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean isEmpty() {
		return (size() == 0L);
	}
	
	/**
	 * このオブジェクトが保持する要素数を返す。
	 * @return	要素数
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public long size() {
		try {
			Document doc = _mongo_vals_col.aggregate(MONGO_AGGRE_ELEM_COUNT).first();
			if (doc != null && doc.containsKey(MONGO_KEY_ELEM_COUNT)) {
				return doc.get(MONGO_KEY_ELEM_COUNT, Number.class).longValue();
			} else {
				return 0L;
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to count Exalge elements in ExAlgeSet[" + _mongo_vals_col.getNamespace().toString() + "] :", ex);
		}
	}

	/**
	 * 指定された交換代数元が含まれているかを判定する。
	 * @param alge	判定する交換代数元
	 * @return	含まれている場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean contains(Exalge alge) {
		for (MongoExalge elem : this) {
			if (elem.equals(alge)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 指定された交換代数元が含まれているかを判定する。
	 * @param alge	判定する交換代数元
	 * @return	含まれている場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean contains(BigExalge alge) {
		for (MongoExalge elem : this) {
			if (elem.equals(alge)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 指定されたコレクションに含まれる要素のすべてが、自身に含まれているかどうかを判定する。
	 * @param c	判定するコレクション
	 * @return	すべての要素が含まれている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean containsAll(Collection<? extends Exalge> c) {
		for (Exalge alge : c) {
			if (!contains(alge)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 指定されたコレクションに含まれる要素のすべてが、自身に含まれているかどうかを判定する。
	 * @param set	判定するコレクション
	 * @return	すべての要素が含まれている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean containsAll(BigExAlgeSet<? extends BigExalge> set) {
		for (BigExalge alge : set) {
			if (!contains(alge)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * このオブジェクトの交換代数元に含まれる交換代数要素にアクセスするイテレーターを取得する。
	 * @return	このオブジェクトに含まれる交換代数要素のイテレーター
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public BigIterator<BigExAlgeSetInnerElement> innerElementIterator() {
		return newUnmodifiableDocumentIterator();
	}

	/**
	 * このオブジェクト交換代数基底にアクセスする変更不可能なイテレーターを取得する。
	 * @return	このオブジェクトに含まれる交換代数基底の変更不可能なイテレーター
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public BigIterator<ExBase> exbaseIterator() {
		return newUnmodifiableExBaseIterator();
	}
	
	/**
	 * 交換代数集合の交換代数元の単位でのイテレーターを返す。
	 * @return	交換代数元のイテレーターオブジェクト
	 * @see ConcurrentModificationException
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public BigIterator<MongoExalge> iterator() {
		return newMongoExalgeIterator();
	}

	/**
	 * この集合から、すべての要素を削除する。
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public void clear() {
		MongoUtil.clearAllDocuments(_mongo_vals_col);
		_modCount.incrementAndGet();
	}

	/**
	 * 指定された交換代数元を、この集合に追加する。
	 * なお、このメソッドでは要素が空の交換代数元は追加されない。
	 * @param alge	追加する交換代数元
	 * @return	追加された場合は <tt>true</tt>、すでに存在する場合は <tt>false</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean add(Exalge alge) {
		if (alge.isEmpty())
			return false;
		
		boolean modified = false;
		String newSetID = MongoUtil.makeUniqueAlgeSetElemID();
		try {
			for (Map.Entry<ExBase, BigDecimal> entry : alge.getUnmodifiableEntrySet()) {
				Document newDoc = MongoDelegateDocExAlgeSetElem.makeExAlgeSetElemDocument(newSetID, entry.getKey(), entry.getValue());
				_mongo_vals_col.insertOne(newDoc);
				_modCount.incrementAndGet();
				modified = true;
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to insert new Exalge element into MongoExAlgeSet[" + _mongo_vals_col.getNamespace().toString() + "] :", ex);
		}
		return modified;
	}
	
	/**
	 * 指定された交換代数元を、この集合に追加する。
	 * @param alge	追加する交換代数元
	 * @return	追加された場合は <tt>true</tt>、すでに存在する場合は <tt>false</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean add(BigExalge alge) {
		if (alge.isEmpty())
			return false;
		
		boolean modified = false;
		String newSetID = MongoUtil.makeUniqueAlgeSetElemID();
		BigIterator<BigExalgeElement> it = null;
		try {
			it = alge.elementIterator();
			while (it.hasNext()) {
				BigExalgeElement elem = it.next();
				Document newDoc = MongoDelegateDocExAlgeSetElem.makeExAlgeSetElemDocument(newSetID, elem.getBase(), elem.getValue());
				_mongo_vals_col.insertOne(newDoc);
				_modCount.incrementAndGet();
				modified = true;
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to insert new Exalge element into MongoExAlgeSet[" + _mongo_vals_col.getNamespace().toString() + "] :", ex);
		}
		finally {
			if (it != null) {
				it.closeCursor();
			}
		}
		return modified;
	}

	/**
	 * 指定された交換代数元を、この集合から削除する。
	 * @param alge	削除する交換代数元
	 * @return	削除された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean remove(Exalge alge) {
		if (alge == null || alge.isEmpty())
			return false;
		
		boolean removed = false;
		BigIterator<MongoExalge> it = null;
		try {
			it = iterator();
			while (it.hasNext()) {
				MongoExalge thisAlge = it.next();
				if (thisAlge.equals(alge)) {
					// remove
					it.remove();
					removed = true;
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to remove Exalge from MongoExAlgeSet[" + _mongo_vals_col.getNamespace().toString() + "] :", ex);
		}
		finally {
			if (it != null) {
				it.closeCursor();
			}
		}
		return removed;
	}
	
	/**
	 * 指定された交換代数元を、この集合から削除する。
	 * @param alge	削除する交換代数元
	 * @return	削除された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean remove(BigExalge alge) {
		if (alge == null || alge.isEmpty())
			return false;
		
		boolean removed = false;
		BigIterator<MongoExalge> it = null;
		try {
			it = iterator();
			while (it.hasNext()) {
				MongoExalge thisAlge = it.next();
				if (thisAlge.equals(alge)) {
					// remove
					it.remove();
					removed = true;
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to remove BigExalge from MongoExAlgeSet[" + _mongo_vals_col.getNamespace().toString() + "] :", ex);
		}
		finally {
			if (it != null) {
				it.closeCursor();
			}
		}
		return removed;
	}

	/**
	 * 指定されたコレクションに含まれるすべての交換代数元を、この集合に追加する。
	 * @param c	追加する交換代数元のコレクション
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean addAll(Collection<? extends Exalge> c) {
		boolean modified = false;
		for (Exalge alge : c) {
			if (add(alge)) {
				modified = true;
			}
		}
		return modified;
	}
	
	/**
	 * 指定されたコレクションに含まれるすべての交換代数元を、この集合に追加する。
	 * @param set	追加する交換代数元のコレクション
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean addAll(BigExAlgeSet<? extends BigExalge> set) {
		boolean modified = false;
		for (BigExalge alge : set) {
			if (add(alge)) {
				modified = true;
			}
		}
		return modified;
	}

	/**
	 * 指定されたコレクションに含まれるすべての交換代数元を、この集合から削除する。
	 * @param c	削除する交換代数元のコレクション
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean removeAll(Collection<? extends Exalge> c) {
		if (c == null || c.isEmpty())
			return false;
		
		boolean removed = false;
		BigIterator<MongoExalge> it = null;
		try {
			it = iterator();
			while (it.hasNext()) {
				MongoExalge thisAlge = it.next();
				if (c.contains(thisAlge)) {
					// remove
					it.remove();
					removed = true;
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to remove by collection of Exalge from MongoExAlgeSet[" + _mongo_vals_col.getNamespace().toString() + "] :", ex);
		}
		finally {
			if (it != null) {
				it.closeCursor();
			}
		}
		return removed;
	}
	
	/**
	 * 指定されたコレクションに含まれるすべての交換代数元を、この集合から削除する。
	 * @param set	削除する交換代数元のコレクション
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean removeAll(BigExAlgeSet<? extends BigExalge> set) {
		if (set == null || set.isEmpty())
			return false;
		
		boolean removed = false;
		BigIterator<MongoExalge> it = null;
		try {
			it = iterator();
			while (it.hasNext()) {
				MongoExalge thisAlge = it.next();
				if (set.contains(thisAlge)) {
					// remove
					it.remove();
					removed = true;
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to remove by collection of Exalge from MongoExAlgeSet[" + _mongo_vals_col.getNamespace().toString() + "] :", ex);
		}
		finally {
			if (it != null) {
				it.closeCursor();
			}
		}
		return removed;
	}

	/**
	 * 指定されたコレクションに含まれるすべての交換代数元のみを残し、その他の交換代数元をこの集合から削除する。
	 * @param c	残す交換代数元のコレクション
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean retainAll(Collection<? extends Exalge> c) {
		if (c == null)
			return false;
		
		boolean removed = false;
		BigIterator<MongoExalge> it = null;
		try {
			it = iterator();
			while (it.hasNext()) {
				MongoExalge thisAlge = it.next();
				if (!c.contains(thisAlge)) {
					// remove
					it.remove();
					removed = true;
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to retain by collection of Exalge from MongoExAlgeSet[" + _mongo_vals_col.getNamespace().toString() + "] :", ex);
		}
		finally {
			if (it != null) {
				it.closeCursor();
			}
		}
		return removed;
	}
	
	/**
	 * 指定されたコレクションに含まれるすべての交換代数元のみを残し、その他の交換代数元をこの集合から削除する。
	 * @param set	残す交換代数元のコレクション
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean retainAll(BigExAlgeSet<? extends BigExalge> set) {
		if (set == null)
			return false;
		
		boolean removed = false;
		BigIterator<MongoExalge> it = null;
		try {
			it = iterator();
			while (it.hasNext()) {
				MongoExalge thisAlge = it.next();
				if (!set.contains(thisAlge)) {
					// remove
					it.remove();
					removed = true;
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to retain by collection of Exalge from MongoExAlgeSet[" + _mongo_vals_col.getNamespace().toString() + "] :", ex);
		}
		finally {
			if (it != null) {
				it.closeCursor();
			}
		}
		return removed;
	}

	/**
	 * このオブジェクトの複製を生成する。
	 * 
	 * @return 複製された <code>BigExAlgeSet</code> オブジェクト
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExAlgeSet copy() {
		MongoExAlgeSet newSet = new MongoExAlgeSet(_mongo_session);
		MongoCursor<? extends Document> cursor = null;
		try {
			String newSetID = MongoUtil.makeUniqueAlgeSetElemID();
			String lastSetID = null;
			cursor = sortedDocuments(true, false).iterator();
			while (cursor.hasNext()) {
				Document docExAlgeSetElem = cursor.next();
				String curSetID = MongoDelegateDocExAlgeSetElem.getExAlgeSetIDfromDocument(docExAlgeSetElem);
				if (!Objects.equals(lastSetID, curSetID)) {
					newSetID = MongoUtil.makeUniqueAlgeSetElemID();
				}
				//--- change elem_id
				docExAlgeSetElem.append(MongoDelegateDocExAlgeSetElem.MONGO_KEY_SETID, newSetID);
				//--- inset into new object
				newSet._mongo_vals_col.insertOne(docExAlgeSetElem);
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to copy from ExAlgeSet[" + _mongo_vals_col.getNamespace().toString() + "] :", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		return newSet;
	}

	/**
	 * 交換代数集合を連結した、新しい交換代数集合のインスタンスを返す。
	 * <br>
	 * 新しい交換代数集合のインスタンスには、自身のインスタンスに含まれる
	 * 要素(元)の末尾に、引数で指定された集合の要素(元)を追加した
	 * 集合となる。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set	連結する交換代数集合
	 * @return		自身と引数に指定された集合を連結した新しい交換代数集合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExAlgeSet addition(ExAlgeSet set) {
		MongoExAlgeSet newSet = copy();
		newSet.addAll(set);
		return newSet;
	}
	
	/**
	 * 交換代数集合を連結した、新しい交換代数集合のインスタンスを返す。
	 * <br>
	 * 新しい交換代数集合のインスタンスには、自身のインスタンスに含まれる
	 * 要素(元)の末尾に、引数で指定された集合の要素(元)を追加した
	 * 集合となる。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set	連結する交換代数集合
	 * @return		自身と引数に指定された集合を連結した新しい交換代数集合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExAlgeSet addition(BigExAlgeSet<? extends BigExalge> set) {
		MongoExAlgeSet newSet = copy();
		newSet.addAll(set);
		return newSet;
	}

	/**
	 * 引数に指定された交換代数集合の要素を除いた、新しい交換代数集合の
	 * インスタンスを返す。
	 * <br>
	 * 新しい交換代数集合のインスタンスには、自身のインスタンスに含まれる
	 * 要素(元)のうち、引数で指定された集合の要素(元)を除いた集合となる。
	 * 同一の要素とみなすのは、要素の {@link exalge2.Exalge#equals(Object)}
	 * メソッドが <tt>true</tt> を返す場合とする。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set	自身の集合要素から取り除く要素を持つ交換代数集合
	 * @return		自身の集合要素から引数に指定された集合要素を取り除いた、
	 * 				新しい交換代数集合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExAlgeSet subtraction(ExAlgeSet set) {
		MongoExAlgeSet newSet = copy();
		newSet.removeAll(set);
		return newSet;
	}
	
	/**
	 * 引数に指定された交換代数集合の要素を除いた、新しい交換代数集合の
	 * インスタンスを返す。
	 * <br>
	 * 新しい交換代数集合のインスタンスには、自身のインスタンスに含まれる
	 * 要素(元)のうち、引数で指定された集合の要素(元)を除いた集合となる。
	 * 同一の要素とみなすのは、要素の {@link exalge2.Exalge#equals(Object)}
	 * メソッドが <tt>true</tt> を返す場合とする。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set	自身の集合要素から取り除く要素を持つ交換代数集合
	 * @return		自身の集合要素から引数に指定された集合要素を取り除いた、
	 * 				新しい交換代数集合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExAlgeSet subtraction(BigExAlgeSet<? extends BigExalge> set) {
		MongoExAlgeSet newSet = copy();
		newSet.removeAll(set);
		return newSet;
	}
	
	/**
	 * 交換代数に含まれる全ての基底を取り出す。
	 * <br>
	 * このメソッドが返す交換代数基底集合に、基底の重複はない。
	 * 
	 * @return 交換代数から取り出した基底集合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExBaseSet getBases() {
		MongoExBaseSet retBases = new MongoExBaseSet(_mongo_session);
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = distinctByExBase().iterator();
			while (cursor.hasNext()) {
				retBases.add(MongoDelegateDocExBase.toExBase(cursor.next()));
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get all bases from MongoExAlgeSet[" + _mongo_vals_col.getNamespace().toString() + "] :", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		return retBases;
	}

	/**
	 * この交換代数集合に含まれる基底のうち、ハットなし基底のみを取り出す。
	 * 
	 * @return	この交換代数集合に含まれるハットなし基底の集合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExBaseSet getNoHatBases() {
		MongoExBaseSet retBases = new MongoExBaseSet(_mongo_session);
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = distinctByExBase().iterator();
			while (cursor.hasNext()) {
				ExBase base = MongoDelegateDocExBase.toExBase(cursor.next());
				if (base.isNoHat()) {
					retBases.add(base);
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get NO_HAT bases from MongoExAlgeSet[" + _mongo_vals_col.getNamespace().toString() + "] :", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		return retBases;
	}

	/**
	 * この交換代数集合に含まれる基底のうち、ハット基底のみを取り出す。
	 * 
	 * @return	この交換代数集合に含まれるハット基底の集合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExBaseSet getHatBases() {
		MongoExBaseSet retBases = new MongoExBaseSet(_mongo_session);
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = distinctByExBase().iterator();
			while (cursor.hasNext()) {
				ExBase base = MongoDelegateDocExBase.toExBase(cursor.next());
				if (base.isHat()) {
					retBases.add(base);
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get HAT bases from MongoExAlgeSet[" + _mongo_vals_col.getNamespace().toString() + "] :", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		return retBases;
	}

	/**
	 * この交換代数集合から、全ての基底についてハットを除去した
	 * 基底集合を取得する。
	 * 
	 * @return	この交換代数集合に含まれる全基底のハット除去後の基底集合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExBaseSet getBasesWithRemoveHat() {
		MongoExBaseSet retBases = new MongoExBaseSet(_mongo_session);
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = distinctByExBase().iterator();
			while (cursor.hasNext()) {
				ExBase base = MongoDelegateDocExBase.toExBase(cursor.next());
				retBases.add(base.removeHat());
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get bases with remove HAT from MongoExAlgeSet[" + _mongo_vals_col.getNamespace().toString() + "] :", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		return retBases;
	}

	/**
	 * この交換代数集合から、全ての基底についてハットを付加した
	 * 基底集合を取得する。
	 * 
	 * @return	この交換代数集合に含まれる全基底のハット付加後の基底集合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExBaseSet getBasesWithSetHat() {
		MongoExBaseSet retBases = new MongoExBaseSet(_mongo_session);
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = distinctByExBase().iterator();
			while (cursor.hasNext()) {
				ExBase base = MongoDelegateDocExBase.toExBase(cursor.next());
				retBases.add(base.setHat());
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get bases with set HAT from MongoExAlgeSet[" + _mongo_vals_col.getNamespace().toString() + "] :", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		return retBases;
	}

	/**
	 * 自身に含まれる全ての交換代数の総和を計算した結果を取得する
	 * 
	 * @return 計算結果の交換代数
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge sum() {
		MongoExalge newAlge = new MongoExalge(_mongo_session);
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = sortedDocuments(true, true).iterator();
			while (cursor.hasNext()) {
				Document docExAlgeSetElem = cursor.next();
				ExBase base = MongoDelegateDocExalgeElem.toExBaseFromDocument(docExAlgeSetElem);
				BigDecimal val = MongoDelegateDocExalgeElem.getBigDecimalValueFromDocument(docExAlgeSetElem);
				newAlge.plusValue(base, val);
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to copy from ExAlgeSet[" + _mongo_vals_col.getNamespace().toString() + "] :", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		return newAlge;
	}
	
	/**
	 * 自身に含まれる全ての交換代数元に対し、
	 * {@link exalge2.db.mongo.MongoExalge#oneValueProjection(BigDecimal)} した結果を保持する集合を返す。
	 * <br>
	 * このメソッドでは、全要素について {@link exalge2.Exalge#oneValueProjection(BigDecimal)} した結果となる
	 * 交換代数元を持つ、新しい交換代数集合を生成する。
	 * このとき、要素が空となる交換代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * @param value	取り出す要素の値
	 * @return	指定された値と等しい要素のみを含む交換代数元の集合
	 * 
	 * @see exalge2.db.mongo.MongoExalge#oneValueProjection(BigDecimal)
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExAlgeSet oneValueProjection(BigDecimal value) {
		//--- 要素なし
		if (this.isEmpty()) {
			return new MongoExAlgeSet(_mongo_session);
		}

		//--- projection
		MongoExAlgeSet newSet = new MongoExAlgeSet(_mongo_session);
		for (MongoExalge alge : this) {
			MongoExalge tempAlge = alge.oneValueProjection(value);
			try {
				if (!tempAlge.isEmpty()) {
					newSet.add(tempAlge);
				}
			}
			finally {
				tempAlge.delete();
			}
		}
		return newSet;
	}
	
	/**
	 * 自身に含まれる全ての交換代数元に対し、
	 * {@link exalge2.db.mongo.MongoExalge#valuesProjection(Collection)} した結果を保持する集合を返す。
	 * <br>
	 * このメソッドでは、全要素について {@link exalge2.db.mongo.MongoExalge#valuesProjection(Collection)} した結果となる
	 * 交換代数元を持つ、新しい交換代数集合を生成する。
	 * このとき、要素が空となる交換代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * @param values	取り出す要素の値のコレクション
	 * @return	指定されたコレクションに含まれる値と等しい要素のみを含む
	 * 			交換代数元の集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * 
	 * @see exalge2.db.mongo.MongoExalge#valuesProjection(Collection)
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExAlgeSet valuesProjection(Collection<? extends BigDecimal> values) {
		//--- コレクションの要素なし
		if (values.isEmpty()) {
			return new MongoExAlgeSet(_mongo_session);
		}
		
		//--- 要素なし
		if (this.isEmpty()) {
			return new MongoExAlgeSet(_mongo_session);
		}

		//--- projection
		MongoExAlgeSet newSet = new MongoExAlgeSet(_mongo_session);
		for (MongoExalge alge : this) {
			MongoExalge tempAlge = alge.valuesProjection(values);
			try {
				if (!tempAlge.isEmpty()) {
					newSet.add(tempAlge);
				}
			}
			finally {
				tempAlge.delete();
			}
		}
		return newSet;
	}
	
	/**
	 * 自身に含まれる全ての交換代数元に対し、
	 * {@link exalge2.db.mongo.MongoExalge#nullProjection()} した結果を保持する集合を返す。
	 * <br>
	 * このメソッドでは、全要素について {@link exalge2.db.mongo.MongoExalge#nullProjection()} した結果となる
	 * 交換代数元を持つ、新しい交換代数集合を生成する。
	 * このとき、要素が空となる交換代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * @return	値が <tt>null</tt> の要素のみを含む交換代数元の集合
	 * 
	 * @see exalge2.db.mongo.MongoExalge#nullProjection()
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExAlgeSet nullProjection() {
		//--- 要素なし
		if (this.isEmpty()) {
			return new MongoExAlgeSet(_mongo_session);
		}

		//--- projection
		MongoExAlgeSet newSet = new MongoExAlgeSet(_mongo_session);
		for (MongoExalge alge : this) {
			MongoExalge tempAlge = alge.nullProjection();
			try {
				if (!tempAlge.isEmpty()) {
					newSet.add(tempAlge);
				}
			}
			finally {
				tempAlge.delete();
			}
		}
		return newSet;
	}
	
	/**
	 * 自身に含まれる全ての交換代数元に対し、
	 * {@link exalge2.db.mongo.MongoExalge#nonullProjection()} した結果を保持する集合を返す。
	 * <br>
	 * このメソッドでは、全要素について {@link exalge2.db.mongo.MongoExalge#nonullProjection()} した結果となる
	 * 交換代数元を持つ、新しい交換代数集合を生成する。
	 * このとき、要素が空となる交換代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * @return	値が <tt>null</tt> ではない要素のみを含む交換代数元の集合
	 * 
	 * @see exalge2.db.mongo.MongoExalge#nonullProjection()
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExAlgeSet nonullProjection() {
		//--- 要素なし
		if (this.isEmpty()) {
			return new MongoExAlgeSet(_mongo_session);
		}

		//--- projection
		MongoExAlgeSet newSet = new MongoExAlgeSet(_mongo_session);
		for (MongoExalge alge : this) {
			MongoExalge tempAlge = alge.nonullProjection();
			try {
				if (!tempAlge.isEmpty()) {
					newSet.add(tempAlge);
				}
			}
			finally {
				tempAlge.delete();
			}
		}
		return newSet;
	}
	
	/**
	 * 自身に含まれるすべての交換代数元に対し、
	 * {@link exalge2.db.mongo.MongoExalge#zeroProjection()} した結果を保持する集合を返す。
	 * <br>
	 * このメソッドでは、全要素について {@link exalge2.db.mongo.MongoExalge#zeroProjection()} した結果となる
	 * 交換代数元を持つ、新しい交換代数集合を生成する。
	 * このとき、要素が空となる交換代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * @return	値が 0 の要素のみを含む交換代数元の集合
	 * 
	 * @see exalge2.db.mongo.MongoExalge#zeroProjection()
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExAlgeSet zeroProjection() {
		//--- 要素なし
		if (this.isEmpty()) {
			return new MongoExAlgeSet(_mongo_session);
		}

		//--- projection
		MongoExAlgeSet newSet = new MongoExAlgeSet(_mongo_session);
		for (MongoExalge alge : this) {
			MongoExalge tempAlge = alge.zeroProjection();
			try {
				if (!tempAlge.isEmpty()) {
					newSet.add(tempAlge);
				}
			}
			finally {
				tempAlge.delete();
			}
		}
		return newSet;
	}
	
	/**
	 * 自身に含まれるすべての交換代数元に対し、
	 * {@link exalge2.db.mongo.MongoExalge#notzeroProjection()} した結果を保持する集合を返す。
	 * <br>
	 * このメソッドでは、全要素について {@link exalge2.db.mongo.MongoExalge#notzeroProjection()} した結果となる
	 * 交換代数元を持つ、新しい交換代数集合を生成する。
	 * このとき、要素が空となる交換代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * @return	値が 0 ではない要素のみを含む交換代数元の集合
	 * 
	 * @see exalge2.db.mongo.MongoExalge#notzeroProjection()
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExAlgeSet notzeroProjection() {
		//--- 要素なし
		if (this.isEmpty()) {
			return new MongoExAlgeSet(_mongo_session);
		}

		//--- projection
		MongoExAlgeSet newSet = new MongoExAlgeSet(_mongo_session);
		for (MongoExalge alge : this) {
			MongoExalge tempAlge = alge.notzeroProjection();
			try {
				if (!tempAlge.isEmpty()) {
					newSet.add(tempAlge);
				}
			}
			finally {
				tempAlge.delete();
			}
		}
		return newSet;
	}

	/**
	 * 自身に含まれる全ての交換代数元に対し、プロジェクションした
	 * 結果を保持する集合を返す。
	 * <br>
	 * このメソッドでは、全要素についてプロジェクションした結果となる
	 * 交換代数元を持つ、新しい交換代数集合を生成する。このとき、
	 * プロジェクションの結果、要素が空となる交換代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param base	取り出す基底
	 * @return		指定の基底でプロジェクションした結果を持つ交換代数集合
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 * 
	 * @see exalge2.db.mongo.MongoExalge#projection(ExBase)
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExAlgeSet projection(ExBase base) {
		//--- Check
		if (base == null) {
			throw new NullPointerException();
		}
		//--- 要素なし
		if (this.isEmpty()) {
			return new MongoExAlgeSet(_mongo_session);
		}

		//--- projection
		MongoExAlgeSet newSet = new MongoExAlgeSet(_mongo_session);
		for (MongoExalge alge : this) {
			MongoExalge tempAlge = alge.projection(base);
			try {
				if (!tempAlge.isEmpty()) {
					newSet.add(tempAlge);
				}
			}
			finally {
				tempAlge.delete();
			}
		}
		return newSet;
	}

	/**
	 * 自身に含まれる全ての交換代数元に対し、プロジェクションした
	 * 結果を保持する集合を返す。
	 * <br>
	 * このメソッドでは、全要素についてプロジェクションした結果となる
	 * 交換代数元を持つ、新しい交換代数集合を生成する。このとき、
	 * プロジェクションの結果、要素が空となる交換代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param baseset	取り出す基底の集合
	 * @return			指定の基底集合でプロジェクションした結果を持つ交換代数集合
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 * 
	 * @see exalge2.db.mongo.MongoExalge#projection(ExBaseSet)
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExAlgeSet projection(ExBaseSet baseset) {
		//--- Check
		if (baseset == null) {
			throw new NullPointerException();
		}
		//--- 要素なし
		if (this.isEmpty()) {
			return new MongoExAlgeSet(_mongo_session);
		}

		//--- projection
		MongoExAlgeSet newSet = new MongoExAlgeSet(_mongo_session);
		for (MongoExalge alge : this) {
			MongoExalge tempAlge = alge.projection(baseset);
			try {
				if (!tempAlge.isEmpty()) {
					newSet.add(tempAlge);
				}
			}
			finally {
				tempAlge.delete();
			}
		}
		return newSet;
	}
	
	/**
	 * 自身に含まれる全ての交換代数元に対し、プロジェクションした
	 * 結果を保持する集合を返す。
	 * <br>
	 * このメソッドでは、全要素についてプロジェクションした結果となる
	 * 交換代数元を持つ、新しい交換代数集合を生成する。このとき、
	 * プロジェクションの結果、要素が空となる交換代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param baseset	取り出す基底の集合
	 * @return			指定の基底集合でプロジェクションした結果を持つ交換代数集合
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 * 
	 * @see exalge2.db.mongo.MongoExalge#projection(BigExBaseSet)
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExAlgeSet projection(BigExBaseSet baseset) {
		//--- Check
		if (baseset == null) {
			throw new NullPointerException();
		}
		//--- 要素なし
		if (this.isEmpty()) {
			return new MongoExAlgeSet(_mongo_session);
		}

		//--- projection
		MongoExAlgeSet newSet = new MongoExAlgeSet(_mongo_session);
		for (MongoExalge alge : this) {
			MongoExalge tempAlge = alge.projection(baseset);
			try {
				if (!tempAlge.isEmpty()) {
					newSet.add(tempAlge);
				}
			}
			finally {
				tempAlge.delete();
			}
		}
		return newSet;
	}
	
	/**
	 * 自身に含まれる全ての交換代数元に対し、ハット基底キーを無視したプロジェクションの
	 * 結果を保持する集合を返す。
	 * <br>
	 * このメソッドでは、全要素についてハット基底キーを無視したプロジェクションの結果となる
	 * 交換代数元を持つ、新しい交換代数集合を生成する。このとき、
	 * プロジェクションの結果、要素が空となる交換代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param base	取り出す基底
	 * @return		指定の基底でプロジェクションした結果を持つ交換代数集合
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 * 
	 * @see exalge2.db.mongo.MongoExalge#generalProjection(ExBase)
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExAlgeSet generalProjection(ExBase base) {
		//--- Check
		if (base == null) {
			throw new NullPointerException();
		}
		//--- 要素なし
		if (this.isEmpty()) {
			return new MongoExAlgeSet(_mongo_session);
		}
		
		//--- 基底生成
		ExBaseSet targetBases = new ExBaseSet(2);
		targetBases.add(base.removeHat());
		targetBases.add(base.setHat());

		//--- projection
		MongoExAlgeSet newSet = new MongoExAlgeSet(_mongo_session);
		for (MongoExalge alge : this) {
			MongoExalge tempAlge = alge.generalProjection(targetBases);
			try {
				if (!tempAlge.isEmpty()) {
					newSet.add(tempAlge);
				}
			}
			finally {
				tempAlge.delete();
			}
		}
		return newSet;
	}
	
	/**
	 * 自身に含まれる全ての交換代数元に対し、ハット基底キーを無視したプロジェクションの
	 * 結果を保持する集合を返す。
	 * <br>
	 * このメソッドでは、全要素についてハット基底キーを無視したプロジェクションの結果となる
	 * 交換代数元を持つ、新しい交換代数集合を生成する。このとき、
	 * プロジェクションの結果、要素が空となる交換代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param baseset	取り出す基底の集合
	 * @return			指定の基底集合でプロジェクションした結果を持つ交換代数集合
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 * 
	 * @see exalge2.db.mongo.MongoExalge#generalProjection(ExBaseSet)
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExAlgeSet generalProjection(ExBaseSet baseset) {
		//--- Check
		if (baseset == null) {
			throw new NullPointerException();
		}
		//--- 要素なし
		if (this.isEmpty()) {
			return new MongoExAlgeSet(_mongo_session);
		}
		
		//--- 基底生成
		ExBaseSet targetBases = baseset.removeHat();
		targetBases.addAll(targetBases.setHat());

		//--- projection
		MongoExAlgeSet newSet = new MongoExAlgeSet(_mongo_session);
		for (MongoExalge alge : this) {
			MongoExalge tempAlge = alge.generalProjection(targetBases);
			try {
				if (!tempAlge.isEmpty()) {
					newSet.add(tempAlge);
				}
			}
			finally {
				tempAlge.delete();
			}
		}
		return newSet;
	}
	
	/**
	 * 自身に含まれる全ての交換代数元に対し、ハット基底キーを無視したプロジェクションの
	 * 結果を保持する集合を返す。
	 * <br>
	 * このメソッドでは、全要素についてハット基底キーを無視したプロジェクションの結果となる
	 * 交換代数元を持つ、新しい交換代数集合を生成する。このとき、
	 * プロジェクションの結果、要素が空となる交換代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param baseset	取り出す基底の集合
	 * @return			指定の基底集合でプロジェクションした結果を持つ交換代数集合
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 * 
	 * @see exalge2.db.mongo.MongoExalge#generalProjection(BigExBaseSet)
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExAlgeSet generalProjection(BigExBaseSet baseset) {
		//--- Check
		if (baseset == null) {
			throw new NullPointerException();
		}
		//--- 要素なし
		if (this.isEmpty()) {
			return new MongoExAlgeSet(_mongo_session);
		}
		
		//--- 基底生成
		BigExBaseSet targetBases = baseset.generalBases();
		try {
			//--- projection
			MongoExAlgeSet newSet = new MongoExAlgeSet(_mongo_session);
			for (MongoExalge alge : this) {
				MongoExalge tempAlge = alge.generalProjection(targetBases);
				try {
					if (!tempAlge.isEmpty()) {
						newSet.add(tempAlge);
					}
				}
				finally {
					tempAlge.delete();
				}
			}
			return newSet;
		}
		finally {
			targetBases.delete();
		}
	}

	/**
	 * 自身に含まれる全ての交換代数元に対し、基底パターンによって
	 * プロジェクションした結果を保持する集合を返す。
	 * <br>
	 * <br>
	 * 指定された基底のハットキー以外の基底キーに一致する基底のみを
	 * 取得し、その集合を返す。ハットキー以外の基底キーにアスタリスク
	 * 文字<code>('*')</code>が含まれている場合、0文字以上の任意の文字に
	 * マッチするワイルドカードとみなす。
	 * <br>
	 * このメソッドでは、全要素について、この基底パターンに一致する基底のみで
	 * プロジェクションした結果となる交換代数元を持つ、新しい交換代数集合を生成する。
	 * このとき、プロジェクションの結果、要素が空となる交換代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param base	基底パターンとみなす交換代数基底
	 * @return		指定の基底パターンでプロジェクションした結果を持つ交換代数集合
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 * 
	 * @see exalge2.db.mongo.MongoExalge#patternProjection(ExBase)
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExAlgeSet patternProjection(ExBase base) {
		ExBasePattern pattern = new ExBasePattern(base, true);
		return patternProjection(pattern);
	}

	/**
	 * 自身に含まれる全ての交換代数元に対し、基底パターンによって
	 * プロジェクションした結果を保持する集合を返す。
	 * <br>
	 * このメソッドでは、全要素について、指定された基底パターンに一致する基底のみで
	 * プロジェクションした結果となる
	 * 交換代数元を持つ、新しい交換代数集合を生成する。このとき、
	 * プロジェクションの結果、要素が空となる交換代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param pattern	基底パターン
	 * @return		指定の基底パターンでプロジェクションした結果を持つ交換代数集合
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 * 
	 * @see exalge2.Exalge#patternProjection(ExBasePattern)
	 */
	@Override
	public MongoExAlgeSet patternProjection(ExBasePattern pattern) {
		if (pattern == null)
			throw new NullPointerException();
		ExBasePatternSet patterns = new ExBasePatternSet(Arrays.asList(pattern));
		return patternProjection(patterns);
	}

	/**
	 * 自身に含まれる全ての交換代数元に対し、基底パターンによって
	 * プロジェクションした結果を保持する集合を返す。
	 * <br>
	 * 指定された集合に含まれる基底の、ハットキー以外の基底キーに
	 * 一致する基底のみを取得し、その集合を返す。ハットキー以外の
	 * 基底キーにアスタリスク文字<code>('*')</code>が含まれている場合、
	 * 0文字以上の任意の文字にマッチするワイルドカードとみなす。
	 * <br>
	 * このメソッドでは、全要素について、指定された基底パターンのどれかに一致する
	 * 基底のみでプロジェクションした結果となる
	 * 交換代数元を持つ、新しい交換代数集合を生成する。このとき、
	 * プロジェクションの結果、要素が空となる交換代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param bases	基底パターンとみなす基底の集合
	 * @return		指定の基底パターンでプロジェクションした結果を持つ交換代数集合
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 * 
	 * @see exalge2.db.mongo.MongoExalge#patternProjection(ExBaseSet)
	 */
	@Override
	public MongoExAlgeSet patternProjection(ExBaseSet bases) {
		ExBasePatternSet patterns = new ExBasePatternSet(bases, true);
		return patternProjection(patterns);
	}

	/**
	 * 自身に含まれる全ての交換代数元に対し、基底パターンによって
	 * プロジェクションした結果を保持する集合を返す。
	 * <br>
	 * 指定された集合に含まれる基底の、ハットキー以外の基底キーに
	 * 一致する基底のみを取得し、その集合を返す。ハットキー以外の
	 * 基底キーにアスタリスク文字<code>('*')</code>が含まれている場合、
	 * 0文字以上の任意の文字にマッチするワイルドカードとみなす。
	 * <br>
	 * このメソッドでは、全要素について、指定された基底パターンのどれかに一致する
	 * 基底のみでプロジェクションした結果となる
	 * 交換代数元を持つ、新しい交換代数集合を生成する。このとき、
	 * プロジェクションの結果、要素が空となる交換代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * <p><b>注意</b>
	 * <blockquote>
	 * このメソッドは、指定された交換代数基底集合から、交換代数基底パターン集合を「メモリ」上に生成する。
	 * そのため、基底集合の量によってはメモリ不足が発生する恐れがあるので、注意すること。
	 * </blockquote>
	 * 
	 * @param bases	基底パターンとみなす基底の集合
	 * @return		指定の基底パターンでプロジェクションした結果を持つ交換代数集合
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 * 
	 * @see exalge2.db.mongo.MongoExalge#patternProjection(BigExBaseSet)
	 */
	@Override
	public MongoExAlgeSet patternProjection(BigExBaseSet bases) {
		final ExBasePatternSet patterns = new ExBasePatternSet();
		for (ExBase base : bases) {
			patterns.add(new ExBasePattern(base, true));
		}
		return patternProjection(patterns);
	}

	/**
	 * 自身に含まれる全ての交換代数元に対し、基底パターンによって
	 * プロジェクションした結果を保持する集合を返す。
	 * <br>
	 * このメソッドでは、全要素について、指定された基底パターンのどれかに一致する
	 * 基底のみでプロジェクションした結果となる
	 * 交換代数元を持つ、新しい交換代数集合を生成する。このとき、
	 * プロジェクションの結果、要素が空となる交換代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param patterns	基底パターンの集合
	 * @return		指定の基底パターンでプロジェクションした結果を持つ交換代数集合
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 * 
	 * @see exalge2.db.mongo.MongoExalge#patternProjection(ExBasePatternSet)
	 */
	@Override
	public MongoExAlgeSet patternProjection(ExBasePatternSet patterns) {
		if (patterns == null)
			throw new NullPointerException();
		//--- 要素なし
		if (this.isEmpty()) {
			return new MongoExAlgeSet(_mongo_session);
		}
		
		// パターンマッチ
		MongoExAlgeSet newSet = new MongoExAlgeSet(_mongo_session);
		MongoCursor<? extends Document> cursor = null;
		try {
			String newSetID = MongoUtil.makeUniqueAlgeSetElemID();
			String lastSetID = null;
			cursor = sortedDocuments(true, false).iterator();
			while (cursor.hasNext()) {
				Document docExAlgeSetElem = cursor.next();
				String curSetID = MongoDelegateDocExAlgeSetElem.getExAlgeSetIDfromDocument(docExAlgeSetElem);
				if (!Objects.equals(lastSetID, curSetID)) {
					newSetID = MongoUtil.makeUniqueAlgeSetElemID();
				}
				//--- 基底のパターンマッチ
				ExBase base = MongoDelegateDocExalgeElem.toExBaseFromDocument(docExAlgeSetElem);
				if (patterns.matches(base)) {
					// 一致したので、新しい集合の元に追加
					//--- change elem_id
					docExAlgeSetElem.append(MongoDelegateDocExAlgeSetElem.MONGO_KEY_SETID, newSetID);
					//--- inset into new object
					newSet._mongo_vals_col.insertOne(docExAlgeSetElem);
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to match with patterns from ExAlgeSet[" + _mongo_vals_col.getNamespace().toString() + "] :", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		return newSet;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * このオブジェクトが保持するコレクションから、基底が重複しないドキュメントの検索結果を取得する。
	 * @return 検索結果のイテレート可能オブジェクト
	 * @throws MongoAlgeError	データベースに関する処理に失敗した場合
	 */
	protected DistinctIterable<? extends Document> distinctByExBase() {
		try {
			// 交換代数元 ID は無視
			return _mongo_vals_col.distinct(MongoDelegateDocExalgeElem.MONGO_KEY_BASE, Document.class);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to distinct by ExBase in MongoExAlgeSet[" + _mongo_vals_col.getNamespace().toString() + "] :", ex);
		}
	}

	/**
	 * このオブジェクトが保持するコレクションから、すべての値でソートされたイテレート可能オブジェクトを取得する。
	 * @param withoutObjectID	検索結果から MongoDB の ObjectID を除外する場合は <tt>true</tt>
	 * @param withoutSetID		検索結果から交換代数元 ID を除外する場合は <tt>true</tt>
	 * @return	イテレート可能オブジェクト
	 * @throws MongoAlgeError	データベースに関する処理に失敗した場合
	 */
	protected FindIterable<? extends Document> sortedDocuments(boolean withoutObjectID, boolean withoutSetID) {
		try {
			FindIterable<? extends Document> result = _mongo_vals_col.find().sort(MongoDelegateDocExAlgeSetElem.MONGO_SORT_EXALGESET_ELEM_ALL);
			if (withoutObjectID)
				result = result.projection(MongoUtil.MONGO_FILTER_WITHOUT_OBJID);
			if (withoutSetID)
				result = result.projection(new Document(MongoDelegateDocExAlgeSetElem.MONGO_KEY_SETID, 0));
			return result;
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Faild to sort by all Exalge fields in MongoExAlgeSet[" + _mongo_vals_col.getNamespace().toString() + "] :", ex);
		}
	}

	/**
	 * 指定された交換代数集合のコレクションが、自身の内容と一致するかどうかを判定する。
	 * <p>この判定では、両コレクションを交換代数元 ID、名前キー、ハットキー、単位キー、時間キー、主体キー、値の順に昇順でソートし、
	 * 交換代数元の単位でその内容を比較する。
	 * @param another	判定対象のコレクション
	 * @return	等しい場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えない場合
	 */
	protected boolean equalsAnotherExAlgeSetCollection(MongoCollection<? extends Document> another) {
		try {
			// 要素数で判定
			long numCol1 = _mongo_vals_col.countDocuments();
			long numCol2 = another.countDocuments();
			if (numCol1 != numCol2)
				return false;
			else if (numCol1 == 0L)
				return true;
			// 要素の値を比較
			MongoCursor<? extends Document> cursor1 = null;
			MongoCursor<? extends Document> cursor2 = null;
			try {
				cursor1 = _mongo_vals_col.find().sort(MongoDelegateDocExAlgeSetElem.MONGO_SORT_EXALGESET_ELEM_ALL).iterator();
				cursor2 = another.find().sort(MongoDelegateDocExAlgeSetElem.MONGO_SORT_EXALGESET_ELEM_ALL).iterator();
				long idxCol1 = -1L;
				long idxCol2 = -1L;
				String strLastSetID1 = null;
				String strLastSetID2 = null;
				while (cursor1.hasNext()) {
					Document doc1 = cursor1.next();
					Document doc2 = cursor2.next();
					//--- elem_id によるインデックス更新
					String strCurSetID1 = MongoDelegateDocExAlgeSetElem.getExAlgeSetIDfromDocument(doc1);
					String strCurSetID2 = MongoDelegateDocExAlgeSetElem.getExAlgeSetIDfromDocument(doc2);
					if (!Objects.equals(strCurSetID1, strLastSetID1)) {
						idxCol1++;
						strLastSetID1 = strCurSetID1;
					}
					if (!Objects.equals(strCurSetID2, strLastSetID2)) {
						idxCol2++;
						strLastSetID2 = strCurSetID2;
					}
					//--- judge with index
					if (idxCol1 != idxCol2) {
						return false;
					}
					//--- element
					if (!MongoDelegateDocExAlgeSetElem.equalsExalgeElemDocument(doc1, doc2)) {
						return false;
					}
				}
				// equal
				return true;
			}
			finally {
				if (cursor1 != null) {
					try {
						cursor1.close();
					} catch (Throwable ignoreEx) {}
				}
				if (cursor2 != null) {
					try {
						cursor2.close();
					} catch (Throwable ignoreEx) {}
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to judge equals between " + _mongo_vals_col.getNamespace().toString() + " and " + another.getNamespace().toString() + ":", ex);
		}
	}

	/**
	 * 交換代数元のイテレーターを生成する。
	 * 
	 * @return <code>MongoExalge</code> の <code>Iterator</code>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	protected BigIterator<MongoExalge> newMongoExalgeIterator() {
		// TODO: カーソルのクローズ処理を今後考慮
		try {
			//--- ソート済みイテレータにする
			MongoCursor<? extends Document> cursor = _mongo_vals_col.aggregate(MONGO_AGGRE_ELEM_ITERABLE).iterator();
			return new MongoExalgeIterator(cursor);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get MongExalge iteration cursor from MongoExAlgeSet[" + _mongo_vals_col.getNamespace().toString() + "] :", ex);
		}
	}
	
	protected BigIterator<BigExAlgeSetInnerElement> newUnmodifiableDocumentIterator() {
		// TODO: カーソルのクローズ処理を今後考慮
		try {
			//--- ソート済みイテレータにする
			// (注意) 削除を可能とするため、"_id" も検索結果に含める
			MongoCursor<? extends Document> cursor = sortedDocuments(false, false).iterator();
			return new MongoExAlgeSetDocumentIterator(cursor);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get sorted document cursor from MongoExAlgeSet[" + _mongo_vals_col.getNamespace().toString() + "] :", ex);
		}
	}
	
	protected BigIterator<ExBase> newUnmodifiableExBaseIterator() {
		// TODO: カーソルのクローズ処理を今後考慮
		try {
			//--- ソート済みイテレータにする
			MongoCursor<? extends Document> cursor = sortedDocuments(true, false).iterator();
			return new MongoExAlgeSetElementInnerBaseIterator(cursor);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get sorted ExBase cursor from MongoExAlgeSet[" + _mongo_vals_col.getNamespace().toString() + "] :", ex);
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	/**
	 * <code>MongoExAlgeSet</code> クラスの交換代数元の要素の値を保持するクラス。
	 * <p>このオブジェクトは不変である。
	 * 
	 * @version 0.990
	 * @since 0.990
	 * 
	 * @author SOARS Project.
	 * @author Y.Ishizuka(PieCake.inc,)
	 */
	static public class MongoExAlgeSetInnerElement implements BigExAlgeSetInnerElement
	{
		private final String		_setid;
		private final ExBase		_base;
		private final BigDecimal	_value;
		
		public MongoExAlgeSetInnerElement(String setid, ExBase base, BigDecimal value) {
			_setid = setid;
			_base  = base;
			_value = value;
		}

		@Override
		public String getAlgeSetID() {
			return _setid;
		}

		@Override
		public ExBase getBase() {
			return _base;
		}

		@Override
		public BigDecimal getValue() {
			return _value;
		}
	}
	
	/**
	 * <code>MongoExAlgeSet</code> クラスの要素にアクセス可能なイテレーターの共通実装。
	 * <p>
	 * このクラスは、交換代数集合<code>(MongoExAlgeSet)</code>から交換代数要素(単一の基底と値を持つ交換代数元)を
	 * 順次取得するためのイテレーターである。
	 * <p>
	 * このクラスのインタフェースでは、基本的に {@link #remove()} はサポートされていない。
	 * 
	 * @version 0.990
	 * @since 0.990
	 * 
	 * @author SOARS Project.
	 * @author Y.Ishizuka(PieCake.inc,)
	 */
	private abstract class AbstractMongoExAlgeSetInnerElementIterator<T> implements BigIterator<T>
	{
		MongoCursor<? extends Document>	_itCursor;
		long _expectedModCount;
		
		AbstractMongoExAlgeSetInnerElementIterator(MongoCursor<? extends Document> cursor) {
			_itCursor = cursor;
			_expectedModCount = _modCount.get();
		}

		public boolean hasNext() {
			try {
				return (_itCursor==null ? false : _itCursor.hasNext());
			} catch (MongoException ex) {
				throw new MongoAlgeError("MongoExAlgeSet's element iterator couldn't check next element in MongoExAlgeSet[" + _mongo_vals_col.getNamespace().toString() + "] :", ex);
			}
		}
		
		/**
		 * <code>AbstractMongoExalgeElementIterator</code> クラスでは、このメソッドはサポートされない。
		 * 
		 * @throws UnsupportedOperationException この例外を必ずスローする
		 */
		public void remove() {
			throw new UnsupportedOperationException("Unsupported \"remove\" operation!");
		}
		
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
	
	/**
	 * <code>MongoExAlgeSet</code> クラスのドキュメントイテレーター。
	 * <p>
	 * このクラスは、交換代数集合<code>(MongoExAlgeSet)</code>から交換代数要素(BigExAlgeSetInnerElement)を
	 * 順次取得するためのイテレーターである。
	 * <p>
	 * このクラスのインタフェースでは、{@link #remove()} は実行可能となっている。
	 * 
	 * @version 0.990
	 * @since 0.990
	 * 
	 * @author SOARS Project.
	 * @author Y.Ishizuka(PieCake.inc,)
	 */
	private class MongoExAlgeSetDocumentIterator extends AbstractMongoExAlgeSetInnerElementIterator<BigExAlgeSetInnerElement>
	{
		protected Object _lastObjectId;
		
		MongoExAlgeSetDocumentIterator(MongoCursor<? extends Document> cursor) {
			super(cursor);
		}
		
		@Override
		public BigExAlgeSetInnerElement next() {
			if (_expectedModCount != _modCount.get())
				throw new ConcurrentModificationException();
			if (_itCursor == null)
				throw new NoSuchElementException("MongoExAlgeSet's cursor was closed.");
			try {
				Document docExAlgeSetElem = _itCursor.next();
				_lastObjectId = docExAlgeSetElem.get("_id");
				String setid = MongoDelegateDocExAlgeSetElem.getExAlgeSetIDfromDocument(docExAlgeSetElem);
				ExBase base = MongoDelegateDocExalgeElem.toExBaseFromDocument(docExAlgeSetElem);
				BigDecimal value = MongoDelegateDocExalgeElem.getBigDecimalValueFromDocument(docExAlgeSetElem);
				if (!_itCursor.hasNext()) {
					closeCursor();
				}
				return new MongoExAlgeSetInnerElement(setid, base, value);
			} catch (MongoException ex) {
				throw new MongoAlgeError("MongoExAlgeSet's document iterator couldn't get next element in MongoExAlgeSet[" + _mongo_vals_col.getNamespace().toString() + "] :", ex);
			}
		}
		
		/**
		 * 現在の位置のドキュメントを削除する。
		 * 
		 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
		 */
		public void remove() {
			if (_expectedModCount != _modCount.get())
				throw new ConcurrentModificationException();
			try {
				if (_lastObjectId != null) {
					_mongo_vals_col.deleteOne(new Document("_id", _lastObjectId));
					_lastObjectId = null;
					_expectedModCount = _modCount.incrementAndGet();
				}
			}
			catch (MongoException ex) {
				throw new MongoAlgeError("MongoExAlgeSet's document iterator couldn't get next element in MongoExAlgeSet[" + _mongo_vals_col.getNamespace().toString() + "] :", ex);
			}
			//throw new UnsupportedOperationException("Unsupported \"remove\" operation!");
		}
	}
	
	/**
	 * <code>MongoExAlgeSet</code> クラスの交換代数基底としての要素イテレーター。
	 * <p>
	 * このクラスは、交換代数集合<code>(MongoExAlgeSet)</code>から交換代数元に含まれる交換代数要素の基底(ExBase)を
	 * 順次取得するためのイテレーターである。
	 * <p>
	 * このクラスのインタフェースでは、{@link #remove()} はサポートされていない。
	 * 
	 * @version 0.990
	 * @since 0.990
	 * 
	 * @author SOARS Project.
	 * @author Y.Ishizuka(PieCake.inc,)
	 */
	private class MongoExAlgeSetElementInnerBaseIterator extends AbstractMongoExAlgeSetInnerElementIterator<ExBase>
	{
		MongoExAlgeSetElementInnerBaseIterator(MongoCursor<? extends Document> cursor) {
			super(cursor);
		}
		
		@Override
		public ExBase next() {
			if (_expectedModCount != _modCount.get())
				throw new ConcurrentModificationException();
			if (_itCursor == null)
				throw new NoSuchElementException("MongoExAlgeSet's cursor was closed.");
			try {
				Document docExalgeElem = _itCursor.next();
				ExBase base = MongoDelegateDocExalgeElem.toExBaseFromDocument(docExalgeElem);
				if (!_itCursor.hasNext()) {
					closeCursor();
				}
				return base;
			} catch (MongoException ex) {
				throw new MongoAlgeError("MongoExAlgeSet's ExBase iterator couldn't get next element in MongoExAlgeSet[" + _mongo_vals_col.getNamespace().toString() + "] :", ex);
			}
		}
	}
	
	/**
	 * <code>MongoExAlgeSet</code> クラスの交換代数元のイテレーター。
	 * <p>
	 * このクラスは、交換代数集合<code>(MongoExAlgeSet)</code>から交換代数元(複数の基底と値を持つ交換代数元)を
	 * 順次取得するためのイテレーターである。
	 * <p>
	 * このクラスのインタフェースでは、{@link #remove()} は実行可能となっている。
	 * 
	 * @version 0.990
	 * @since 0.990
	 * 
	 * @author SOARS Project.
	 * @author Y.Ishizuka(PieCake.inc,)
	 */
	private class MongoExalgeIterator extends AbstractMongoExAlgeSetInnerElementIterator<MongoExalge>
	{
		protected String _lastSetId;
		
		MongoExalgeIterator(MongoCursor<? extends Document> cursor) {
			super(cursor);
		}
		
		@Override
		public MongoExalge next() {
			// このイテレータがアクセスするのは、重複のない交換代数元 ID のコレクションのカーソル
			if (_expectedModCount != _modCount.get())
				throw new ConcurrentModificationException();
			if (_itCursor == null)
				throw new NoSuchElementException("MongoExAlgeSet's cursor was closed.");
			try {
				Document docExalgeID = _itCursor.next();
				_lastSetId = MongoDelegateDocExAlgeSetElem.getExAlgeSetIDfromDocument(docExalgeID);
				if (!_itCursor.hasNext()) {
					closeCursor();
				}
				return new MongoExalge(MongoExAlgeSet.this, _lastSetId);
			} catch (MongoException ex) {
				throw new MongoAlgeError("MongoExAlgeSet's element iterator couldn't get next element in MongoExAlgeSet[" + _mongo_vals_col.getNamespace().toString() + "] :", ex);
			}
		}
		
		/**
		 * 現在の位置のドキュメントを削除する。
		 * 
		 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
		 */
		public void remove() {
			if (_expectedModCount != _modCount.get())
				throw new ConcurrentModificationException();
			try {
				if (_lastSetId != null) {
					_mongo_vals_col.deleteMany(Filters.eq(MongoDelegateDocExAlgeSetElem.MONGO_KEY_SETID, _lastSetId));
					_lastSetId = null;
					_expectedModCount = _modCount.incrementAndGet();
				}
			}
			catch (MongoException ex) {
				throw new MongoAlgeError("MongoExAlgeSet's element iterator couldn't remove current element in MongoExAlgeSet[" + _mongo_vals_col.getNamespace().toString() + "] :", ex);
			}
		}
	}
}
