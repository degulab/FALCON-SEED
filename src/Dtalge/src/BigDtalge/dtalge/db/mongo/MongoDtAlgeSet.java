/*
 * @(#)MongoDtAlgeSet.java	0.5.0	2019/02/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.db.mongo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoException;
import com.mongodb.MongoNamespace;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.BsonField;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import dtalge.DtAlgeSet;
import dtalge.DtBase;
import dtalge.DtBasePattern;
import dtalge.DtBasePatternSet;
import dtalge.DtBaseSet;
import dtalge.Dtalge;
import dtalge.IDtStringThesaurus;
import dtalge.db.BigDtAlgeSet;
import dtalge.db.BigDtAlgeSetInnerElement;
import dtalge.db.BigDtBaseSet;
import dtalge.db.BigDtalge;
import dtalge.db.BigDtalgeElement;
import dtalge.exception.CsvFormatException;
import dtalge.io.internal.CsvReader;
import dtalge.io.internal.CsvWriter;
import dtalge.util.DtDataTypes;
import dtalge.util.Strings;
import dtalge.util.Validations;
import redundantalge.db.BigIterator;
import redundantalge.db.mongo.MongoAlgeError;
import redundantalge.db.mongo.MongoSession;
import redundantalge.db.mongo.MongoUtil;

/**
 * 大容量のデータ代数集合を保持するクラス。
 * 基本的に {@link dtalge.DtAlgeSet} と同様のインタフェースを提供する。
 * ただし、順序については不定である。
 * <p>ストレージとして MongoDB を利用する。
 * 
 * <p>データ代数元のリストとして実装されている。
 * <p><b>この実装は同期化されない</b>。
 * <p>
 * <b>《入出力フォーマット》</b>
 * <br>
 * <code>MongoDtAlgeSet</code> の MongoDB におけるデータ構造は、次の通りである。
 * <pre><code>
 * {
 *   &quot;elem_id&quot; : &lt;データ代数元 ID(UUID)&gt;
 *   &quot;base&quot; {
 *     &quot;name&quot; : &lt;string&gt;
 *     &quot;type&quot; : &lt;string&gt;
 *     &quot;attr&quot; : &lt;string&gt;
 *     &quot;subject&quot; : &lt;string&gt;
 *   },
 *   &quot;value&quot; : &lt;null or BigDecimal or boolean or String&gt;
 * }
 * </code></pre>
 * データ代数元 ID が同一のドキュメントは、同じデータ代数元に所属していることを示す。
 * 
 * @version 0.5.0
 * @since 0.5.0
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public class MongoDtAlgeSet implements BigDtAlgeSet<MongoDtalge>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	///** 一時的なコレクション名(キーのコレクション)のプレフィックス **/
	//static public final String	TEMP_KEYS_COLNAME_PREFIX	= "Task";
	/** 一時的なコレクション名(値のコレクション)のプレフィックス **/
	static public final String	TEMP_VALS_COLNAME_PREFIX	= "TDtas";
	
	static protected final String	MONGO_KEY_ELEM_COUNT	= "elemCount";
	
	static protected final List<Bson>	MONGO_AGGRE_ELEM_COUNT;
	
	static protected final List<Bson>	MONGO_AGGRE_ELEM_ITERABLE;
	
	static {
		//--- データ代数集合のデータ代数元数をカウントするパイプライン
		MONGO_AGGRE_ELEM_COUNT = new ArrayList<Bson>();
		MONGO_AGGRE_ELEM_COUNT.add(Aggregates.group("$" + MongoDelegateDocDtAlgeSetElem.MONGO_KEY_SETID));
		MONGO_AGGRE_ELEM_COUNT.add(Aggregates.count(MONGO_KEY_ELEM_COUNT));
		//--- データ代数集合のデータ代数元 ID のみを保持する昇順ソートされたビューを生成するパイプライン
		MONGO_AGGRE_ELEM_ITERABLE = new ArrayList<Bson>();
		MONGO_AGGRE_ELEM_ITERABLE.add(Aggregates.group("$" + MongoDelegateDocDtAlgeSetElem.MONGO_KEY_SETID));
		MONGO_AGGRE_ELEM_ITERABLE.add(Aggregates.project(new Document(MongoDelegateDocDtAlgeSetElem.MONGO_KEY_SETID, "$_id").append("_id", 0)));
		MONGO_AGGRE_ELEM_ITERABLE.add(Aggregates.sort(Sorts.ascending(MongoDelegateDocDtAlgeSetElem.MONGO_KEY_SETID)));
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
	public MongoDtAlgeSet(MongoSession session) {
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
	public MongoDtAlgeSet(MongoSession session, String collection) {
		if (session == null)
			throw new NullPointerException("MongoDtalgeSession object is null.");
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
	 * このオブジェクトの MongoDB セッションを取得する。
	 * @return	セッションオブジェクト
	 */
	public MongoSession getSession() {
		return _mongo_session;
	}
	
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
			throw new MongoAlgeError("Failed to persist of MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		_mongo_session.unregisterTemporaryCollection(oldName);
		_mongo_vals_col = _mongo_session.getPersistentCollection(newCollectionName);
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
			throw new MongoAlgeError("Failed to drop collection of MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		//_mongo_session.unregisterTemporaryCollection(_mongo_vals_col.getNamespace());
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
		Iterator<MongoDtalge> it = iterator();
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
		
		if (!(obj instanceof MongoDtAlgeSet)) {
			return false;
		}
		
		MongoDtAlgeSet that = (MongoDtAlgeSet)obj;
		return equalsAnotherDtAlgeSetCollection(that._mongo_vals_col);
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
			throw new MongoAlgeError("Failed to count Dtalge elements in DtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
	}

	/**
	 * 指定されたデータ代数元が含まれているかを判定する。
	 * @param alge	判定するデータ代数元
	 * @return	含まれている場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean contains(Dtalge alge) {
		for (MongoDtalge elem : this) {
			if (elem.isSameValues(alge)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 指定されたデータ代数元が含まれているかを判定する。
	 * @param alge	判定するデータ代数元
	 * @return	含まれている場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean contains(BigDtalge alge) {
		for (MongoDtalge elem : this) {
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
	public boolean containsAll(Collection<? extends Dtalge> c) {
		for (Dtalge alge : c) {
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
	public boolean containsAll(BigDtAlgeSet<? extends BigDtalge> set) {
		for (BigDtalge alge : set) {
			if (!contains(alge)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * このオブジェクトのデータ代数元に含まれるデータ代数要素にアクセスするイテレーターを取得する。
	 * 要素が返されるときの順序は、データベースの実装に依存する。
	 * @return	このオブジェクトに含まれるデータ代数要素のイテレーター
	 */
	@Override
	public BigIterator<BigDtAlgeSetInnerElement> innerElementIterator() {
		return newUnmodifiableNaturalOrderedDocumentIterator();
	}
	
	/**
	 * このオブジェクトのデータ代数元に含まれるデータ代数要素にアクセスするイテレーターを取得する。
	 * 要素が返されるときの順序は、データ代数元要素の格納順、基底、値の順に昇順ソートされた順序となる。
	 * @return	このオブジェクトに含まれるデータ代数要素のイテレーター
	 */
	@Override
	public BigIterator<BigDtAlgeSetInnerElement> sortedInnerElementIterator() {
		return newUnmodifiableSortedDocumentIterator();
	}

	/**
	 * このオブジェクトデータ代数基底にアクセスするイテレーターを取得する。
	 * このイテレーターが返す順序は、基底キーの順に昇順ソートされたものとなる。
	 * @return	このオブジェクトに含まれるデータ代数基底のイテレーター
	 */
	@Override
	public BigIterator<DtBase> dtbaseIterator() {
		return newUnmodifiableSortedDtBaseIterator();
	}
	
	/**
	 * データ代数集合のデータ代数元の単位でのイテレーターを返す。
	 * 要素が返されるときの順序は、データベースの実装に依存する。
	 * @return	データ代数元のイテレーターオブジェクト
	 * @see ConcurrentModificationException
	 */
	@Override
	public BigIterator<MongoDtalge> iterator() {
		return newMongoDtalgeIterator();
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
	 * 指定されたデータ代数元 ID が、このデータ代数集合に含まれているかを判定する。
	 * @param elemId	判定するデータ代数元 ID
	 * @return	含まれている場合は <tt>true</tt>
	 */
	public boolean containsAlgeElementId(String elemId) {
		try {
			return (_mongo_vals_col.countDocuments(makeQueryBySetId(elemId)) >= 0L);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to find element ID from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
	}

	/**
	 * 指定されたデータ代数元 ID と一致するデータ代数元のビューを取得する。
	 * @param elemId	取得するデータ代数元の ID
	 * @return	取得できた場合はビューオブジェクト、そうでない場合は <tt>null</tt>
	 */
	public MongoDtalge getByAlgeElementId(String elemId) {
		try {
			if (_mongo_vals_col.countDocuments(makeQueryBySetId(elemId)) >= 0L) {
				//--- exists
				return new MongoDtalge(this, elemId);
			}
			else {
				//--- not found
				return null;
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get element by ID from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
	}

	/**
	 * 指定されたデータ代数元 ID と一致するデータ代数元を、このデータ代数集合から削除する。
	 * 
	 * @param elemId	削除対象のデータ代数元の ID
	 * @return	削除できた場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean deleteByAlgeElementId(String elemId) {
		try {
			DeleteResult ret = _mongo_vals_col.deleteMany(makeQueryBySetId(elemId));
			return (ret.getDeletedCount() > 0L);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to delete element by ID from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
	}

	/**
	 * 指定されたデータ代数元を、この集合に追加する。
	 * なお、このメソッドでは要素が空のデータ代数元は追加されない。
	 * @param alge	追加するデータ代数元
	 * @return	追加された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean add(Dtalge alge) {
		if (alge.isEmpty())
			return false;
		
		boolean modified = false;
		String newSetID = MongoUtil.makeUniqueAlgeSetElemID();
		try {
			for (Map.Entry<DtBase, Object> entry : alge.getUnmodifiableEntrySet()) {
				Document newDoc = MongoDelegateDocDtAlgeSetElem.makeDtAlgeSetElemDocument(newSetID, entry.getKey(), entry.getValue());
				_mongo_vals_col.insertOne(newDoc);
				_modCount.incrementAndGet();
				modified = true;
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to insert new Dtalge element into MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		return modified;
	}
	
	/**
	 * 指定されたデータ代数元を、この集合に追加する。
	 * @param alge	追加するデータ代数元
	 * @return	追加された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean add(BigDtalge alge) {
		if (alge.isEmpty())
			return false;
		
		boolean modified = false;
		String newSetID = MongoUtil.makeUniqueAlgeSetElemID();
		BigIterator<BigDtalgeElement> it = null;
		try {
			it = alge.elementIterator();
			while (it.hasNext()) {
				BigDtalgeElement elem = it.next();
				Document newDoc = MongoDelegateDocDtAlgeSetElem.makeDtAlgeSetElemDocument(newSetID, elem.getBase(), elem.getValue());
				_mongo_vals_col.insertOne(newDoc);
				_modCount.incrementAndGet();
				modified = true;
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to insert new Dtalge element into MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			if (it != null) {
				it.closeCursor();
			}
		}
		return modified;
	}

	/**
	 * 指定されたデータ代数元を、この集合から削除する。
	 * @param alge	削除するデータ代数元
	 * @return	削除された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean remove(Dtalge alge) {
		if (alge == null || alge.isEmpty())
			return false;
		
		boolean removed = false;
		BigIterator<MongoDtalge> it = null;
		try {
			it = iterator();
			while (it.hasNext()) {
				MongoDtalge thisAlge = it.next();
				if (thisAlge.isSameValues(alge)) {
					// remove
					it.remove();
					removed = true;
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to remove Dtalge from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			if (it != null) {
				it.closeCursor();
			}
		}
		return removed;
	}
	
	/**
	 * 指定されたデータ代数元を、この集合から削除する。
	 * @param alge	削除するデータ代数元
	 * @return	削除された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean remove(BigDtalge alge) {
		if (alge == null || alge.isEmpty())
			return false;
		
		boolean removed = false;
		BigIterator<MongoDtalge> it = null;
		try {
			it = iterator();
			while (it.hasNext()) {
				MongoDtalge thisAlge = it.next();
				if (thisAlge.equals(alge)) {
					// remove
					it.remove();
					removed = true;
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to remove BigDtalge from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			if (it != null) {
				it.closeCursor();
			}
		}
		return removed;
	}

	/**
	 * 指定されたコレクションに含まれるすべてのデータ代数元を、この集合に追加する。
	 * @param c	追加するデータ代数元のコレクション
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean addAll(Collection<? extends Dtalge> c) {
		boolean modified = false;
		for (Dtalge alge : c) {
			if (add(alge)) {
				modified = true;
			}
		}
		return modified;
	}
	
	/**
	 * 指定されたコレクションに含まれるすべてのデータ代数元を、この集合に追加する。
	 * @param set	追加するデータ代数元のコレクション
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean addAll(BigDtAlgeSet<? extends BigDtalge> set) {
		boolean modified = false;
		for (BigDtalge alge : set) {
			if (add(alge)) {
				modified = true;
			}
		}
		return modified;
	}

	/**
	 * 指定されたコレクションに含まれるすべてのデータ代数元を、この集合から削除する。
	 * @param c	削除するデータ代数元のコレクション
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean removeAll(Collection<? extends Dtalge> c) {
		if (c == null || c.isEmpty())
			return false;
		
		boolean removed = false;
		BigIterator<MongoDtalge> it = null;
		try {
			it = iterator();
			while (it.hasNext()) {
				MongoDtalge thisAlge = it.next();
				boolean exists = false;
				for (Dtalge thatAlge : c) {
					if (thisAlge.isSameValues(thatAlge)) {
						exists = true;
						break;
					}
				}
				if (exists) {
					// remove
					it.remove();
					removed = true;
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to remove by collection of Dtalge from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			if (it != null) {
				it.closeCursor();
			}
		}
		return removed;
	}
	
	/**
	 * 指定されたコレクションに含まれるすべてのデータ代数元を、この集合から削除する。
	 * @param set	削除するデータ代数元のコレクション
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean removeAll(BigDtAlgeSet<? extends BigDtalge> set) {
		if (set == null || set.isEmpty())
			return false;
		
		boolean removed = false;
		BigIterator<MongoDtalge> it = null;
		try {
			it = iterator();
			while (it.hasNext()) {
				MongoDtalge thisAlge = it.next();
				if (set.contains(thisAlge)) {
					// remove
					it.remove();
					removed = true;
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to remove by collection of Dtalge from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			if (it != null) {
				it.closeCursor();
			}
		}
		return removed;
	}

	/**
	 * 指定されたコレクションに含まれるすべてのデータ代数元のみを残し、その他のデータ代数元をこの集合から削除する。
	 * @param c	残すデータ代数元のコレクション
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean retainAll(Collection<? extends Dtalge> c) {
		if (c == null)
			return false;
		
		boolean removed = false;
		BigIterator<MongoDtalge> it = null;
		try {
			it = iterator();
			while (it.hasNext()) {
				MongoDtalge thisAlge = it.next();
				boolean exists = false;
				for (Dtalge thatAlge : c) {
					if (thisAlge.isSameValues(thatAlge)) {
						exists = true;
						break;
					}
				}
				if (!exists) {
					// remove
					it.remove();
					removed = true;
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to retain by collection of Dtalge from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			if (it != null) {
				it.closeCursor();
			}
		}
		return removed;
	}
	
	/**
	 * 指定されたコレクションに含まれるすべてのデータ代数元のみを残し、その他のデータ代数元をこの集合から削除する。
	 * @param set	残す交換代数元のコレクション
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean retainAll(BigDtAlgeSet<? extends BigDtalge> set) {
		if (set == null)
			return false;
		
		boolean removed = false;
		BigIterator<MongoDtalge> it = null;
		try {
			it = iterator();
			while (it.hasNext()) {
				MongoDtalge thisAlge = it.next();
				if (!set.contains(thisAlge)) {
					// remove
					it.remove();
					removed = true;
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to retain by collection of Dtalge from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
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
	 * @return 複製された <code>BigDtAlgeSet</code> オブジェクト
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoDtAlgeSet copy() {
		MongoDtAlgeSet newSet = new MongoDtAlgeSet(_mongo_session);
		MongoCursor<? extends Document> cursor = null;
		try {
			String newSetID = MongoUtil.makeUniqueAlgeSetElemID();
			String lastSetID = null;
			cursor = sortedDocuments(true, false).iterator();
			while (cursor.hasNext()) {
				Document docDtAlgeSetElem = cursor.next();
				String curSetID = MongoDelegateDocDtAlgeSetElem.getDtAlgeSetIDfromDocument(docDtAlgeSetElem);
				if (!Objects.equals(lastSetID, curSetID)) {
					newSetID = MongoUtil.makeUniqueAlgeSetElemID();
				}
				//--- change elem_id
				docDtAlgeSetElem.append(MongoDelegateDocDtAlgeSetElem.MONGO_KEY_SETID, newSetID);
				//--- inset into new object
				newSet._mongo_vals_col.insertOne(docDtAlgeSetElem);
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to copy from DtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		return newSet;
	}

	/**
	 * データ代数集合を連結した、新しいデータ代数集合のインスタンスを返す。
	 * <br>
	 * 新しいデータ代数集合のインスタンスには、自身のインスタンスに含まれる
	 * 要素(元)の末尾に、引数で指定された集合の要素(元)を追加した
	 * 集合となる。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set	連結するデータ代数集合
	 * @return		自身と引数に指定された集合を連結した新しいデータ代数集合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoDtAlgeSet addition(DtAlgeSet set) {
		MongoDtAlgeSet newSet = copy();
		newSet.addAll(set);
		return newSet;
	}
	
	/**
	 * データ代数集合を連結した、新しいデータ代数集合のインスタンスを返す。
	 * <br>
	 * 新しいデータ代数集合のインスタンスには、自身のインスタンスに含まれる
	 * 要素(元)の末尾に、引数で指定された集合の要素(元)を追加した
	 * 集合となる。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set	連結するデータ代数集合
	 * @return		自身と引数に指定された集合を連結した新しいデータ代数集合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoDtAlgeSet addition(BigDtAlgeSet<? extends BigDtalge> set) {
		MongoDtAlgeSet newSet = copy();
		newSet.addAll(set);
		return newSet;
	}

	/**
	 * 引数に指定されたデータ代数集合の要素を除いた、新しいデータ代数集合の
	 * インスタンスを返す。
	 * <br>
	 * 新しいデータ代数集合のインスタンスには、自身のインスタンスに含まれる
	 * 要素(元)のうち、引数で指定された集合の要素(元)を除いた集合となる。
	 * 同一の要素とみなすのは、要素の {@link dtalge.db.mongo.MongoDtalge#equals(Object)}
	 * メソッドが <tt>true</tt> を返す場合とする。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set	自身の集合要素から取り除く要素を持つデータ代数集合
	 * @return		自身の集合要素から引数に指定された集合要素を取り除いた、
	 * 				新しいデータ代数集合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoDtAlgeSet subtraction(DtAlgeSet set) {
		MongoDtAlgeSet newSet = copy();
		newSet.removeAll(set);
		return newSet;
	}
	
	/**
	 * 引数に指定されたデータ代数集合の要素を除いた、新しいデータ代数集合の
	 * インスタンスを返す。
	 * <br>
	 * 新しいデータ代数集合のインスタンスには、自身のインスタンスに含まれる
	 * 要素(元)のうち、引数で指定された集合の要素(元)を除いた集合となる。
	 * 同一の要素とみなすのは、要素の {@link dtalge.db.mongo.MongoDtalge#equals(Object)}
	 * メソッドが <tt>true</tt> を返す場合とする。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set	自身の集合要素から取り除く要素を持つデータ代数集合
	 * @return		自身の集合要素から引数に指定された集合要素を取り除いた、
	 * 				新しいデータ代数集合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoDtAlgeSet subtraction(BigDtAlgeSet<? extends BigDtalge> set) {
		MongoDtAlgeSet newSet = copy();
		newSet.removeAll(set);
		return newSet;
	}
	
	/**
	 * データ代数に含まれる全ての基底を取り出す。
	 * <br>
	 * このメソッドが返すデータ代数基底集合に、基底の重複はない。
	 * 
	 * @return データ代数から取り出した基底集合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoDtBaseSet getBases() {
		MongoDtBaseSet retBases = new MongoDtBaseSet(_mongo_session);
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = distinctByDtBase().iterator();
			while (cursor.hasNext()) {
				retBases.add(MongoDelegateDocDtBase.toDtBase(cursor.next()));
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get all bases from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		return retBases;
	}
	
	/**
	 * このデータ代数集合に含まれる基底のうち、指定されたパターンに一致する
	 * 基底のみを格納する基底集合を返す。
	 * <br>
	 * 基底パターンに一致する基底が存在しない場合、このメソッドは空の基底集合を返す。
	 * 
	 * @param pattern	基底パターン
	 * @return	パターンに一致した基底のみの集合
	 * 
	 * @throws NullPointerException	指定された基底パターンが <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoDtBaseSet getMatchedBases(DtBasePattern pattern) {
		Validations.validNotNull(pattern);
		MongoDtBaseSet matchedBases = new MongoDtBaseSet(_mongo_session);
		if (!isEmpty()) {
			MongoCursor<? extends Document> cursor = null;
			try {
				cursor = distinctByDtBase().iterator();
				while (cursor.hasNext()) {
					Document docDtBaseElem = cursor.next();
					DtBase base = MongoDelegateDocDtBase.toDtBase(docDtBaseElem);
					// マッチする基底のみの集合を生成
					if (pattern.matches(base)) {
						matchedBases.add(base);
					}
				}
			}
			catch (MongoException ex) {
				throw new MongoAlgeError("Failed to get matched DtBases into new Collection from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
			}
			finally {
				MongoUtil.closeCursorSilent(cursor);
			}
		}
		return matchedBases;
	}
	
	/**
	 * このデータ代数集合に含まれる基底のうち、指定されたパターンに一致する
	 * 基底のみを格納する基底集合を返す。
	 * <br>
	 * このメソッドが返す基底集合には、指定された基底パターン集合に含まれる
	 * 基底パターンのどれかに一致した基底が含まれる。
	 * <br>
	 * 基底パターンに一致する基底が存在しない場合、もしくは、指定された基底
	 * パターン集合が空の場合、このメソッドは空の基底集合を返す。
	 * 
	 * @param patterns	基底パターンの集合
	 * @return	パターンに一致した基底のみの集合
	 * 
	 * @throws NullPointerException	指定された基底パターン集合が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoDtBaseSet getMatchedBases(DtBasePatternSet patterns) {
		Validations.validNotNull(patterns);
		MongoDtBaseSet matchedBases = new MongoDtBaseSet(_mongo_session);
		if (!isEmpty()) {
			MongoCursor<? extends Document> cursor = null;
			try {
				cursor = distinctByDtBase().iterator();
				while (cursor.hasNext()) {
					Document docDtBaseElem = cursor.next();
					DtBase base = MongoDelegateDocDtBase.toDtBase(docDtBaseElem);
					// マッチする基底のみの集合を生成
					if (patterns.matches(base)) {
						matchedBases.add(base);
					}
				}
			}
			catch (MongoException ex) {
				throw new MongoAlgeError("Failed to get matched DtBases into new Collection from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
			}
			finally {
				MongoUtil.closeCursorSilent(cursor);
			}
		}
		return matchedBases;
	}

	/**
	 * 自身に含まれるデータ代数元をすべて結合する。<br>
	 * このメソッドでは、全てのデータ代数元を、一つのデータ代数元に結合する。
	 * 同じ基底が異なるデータ代数元に存在する場合、内部的な結合の順序で上書きされる。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * この結合では、同じ基底の値は上書きされる。数値的な加算等の演算は行わない。
	 * </blockquote>
	 * 
	 * @return 結合結果の、新しいデータ代数元
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoDtalge sum() {
		MongoDtalge newAlge = new MongoDtalge(_mongo_session);
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = findAllDocuments(true, true).iterator();
			while (cursor.hasNext()) {
				Document docDtAlgeSetElem = cursor.next();
				DtBase base = MongoDelegateDocDtalgeElem.toDtBaseFromDocument(docDtAlgeSetElem);
				Object val = MongoDelegateDocDtalgeElem.getValueObjectFromDocument(docDtAlgeSetElem);
				newAlge.putValue(base, val);
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to sum DtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		return newAlge;
	}

	/**
	 * 指定された基底に割り当てられた全ての値が格納された新しい文字列リストを返す。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このメソッドでは、取得した要素をメモリ上のリストとして取得するため、大容量のデータを扱う場合にメモリ不足が発生する可能性がある。
	 * </blockquote>
	 * @param base	データ代数基底
	 * @return	基底に割り当てられた全ての値を格納する文字列リストを返す。
	 * 			基底に割り当てられた値が存在しない場合は、要素が空の文字列リストを返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	基底のデータ型が文字列型ではない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public List<String> toStringList(DtBase base) {
		if (!DtDataTypes.STRING.equals(base.getTypeKey()))
			throw new IllegalArgumentException("DtBase data type is not String.");
		
		List<String> list = new ArrayList<>();
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = findByBase(base, true, true).iterator();
			while (cursor.hasNext()) {
				Document docDtAlgeSetElem = cursor.next();
				list.add((String)MongoDelegateDocDtAlgeSetElem.getValueObjectFromDocument(docDtAlgeSetElem));
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get string values by DtBase in MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}

		return list;
	}
	
	/**
	 * 指定された基底に割り当てられた全ての重複しない(同値ではない)値が格納された新しい文字列リストを返す。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このメソッドでは、取得した要素をメモリ上のリストとして取得するため、大容量のデータを扱う場合にメモリ不足が発生する可能性がある。
	 * </blockquote>
	 * @param base	データ代数基底
	 * @return	基底に割り当てられた全ての重複しない(同値ではない)値を格納する文字列リストを返す。
	 * 			基底に割り当てられた値が存在しない場合は、要素が空の文字列リストを返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	基底のデータ型が文字列型ではない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public List<String> toDistinctStringList(DtBase base) {
		if (!DtDataTypes.STRING.equals(base.getTypeKey()))
			throw new IllegalArgumentException("DtBase data type is not String.");
		
		List<String> list = new ArrayList<>();
		MongoCursor<?> cursor = null;
		try {
			cursor = distinctValuesByDtBase(base).iterator();
			while (cursor.hasNext()) {
				Object value = cursor.next();
				list.add((String)value);
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get distinct string values by DtBase in MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}

		return list;
	}
	
	/**
	 * 指定された基底に割り当てられた全ての値が格納された新しい実数値リストを返す。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このメソッドでは、取得した要素をメモリ上のリストとして取得するため、大容量のデータを扱う場合にメモリ不足が発生する可能性がある。
	 * </blockquote>
	 * @param base	データ代数基底
	 * @return	基底に割り当てられた全ての値を格納する実数値リストを返す。
	 * 			基底に割り当てられた値が存在しない場合は、要素が空の実数値リストを返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	基底のデータ型が実数型ではない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public List<BigDecimal> toDecimalList(DtBase base) {
		if (!DtDataTypes.DECIMAL.equals(base.getTypeKey()))
			throw new IllegalArgumentException("DtBase data type is not Decimal.");
		
		List<BigDecimal> list = new ArrayList<>();
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = findByBase(base, true, true).iterator();
			while (cursor.hasNext()) {
				Document docDtAlgeSetElem = cursor.next();
				list.add((BigDecimal)MongoDelegateDocDtAlgeSetElem.getValueObjectFromDocument(docDtAlgeSetElem));
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get decimal values by DtBase in MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		
		return list;
	}
	
	/**
	 * 指定された基底に割り当てられた全ての重複しない(同値ではない)値が格納された新しい実数値リストを返す。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このメソッドでは、取得した要素をメモリ上のリストとして取得するため、大容量のデータを扱う場合にメモリ不足が発生する可能性がある。
	 * </blockquote>
	 * @param base	データ代数基底
	 * @return	基底に割り当てられた全ての重複しない(同値ではない)値を格納する実数値リストを返す。
	 * 			基底に割り当てられた値が存在しない場合は、要素が空の実数値リストを返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	基底のデータ型が実数型ではない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public List<BigDecimal> toDistinctDecimalList(DtBase base) {
		if (!DtDataTypes.DECIMAL.equals(base.getTypeKey()))
			throw new IllegalArgumentException("DtBase data type is not Decimal.");
		
		List<BigDecimal> list = new ArrayList<>();
		MongoCursor<?> cursor = null;
		try {
			cursor = distinctValuesByDtBase(base).iterator();
			while (cursor.hasNext()) {
				Object value = cursor.next();
				list.add((BigDecimal)value);
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get distinct decimal values by DtBase in MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		
		return list;
	}
	
	/**
	 * 指定された基底に割り当てられた全ての値が格納された新しい真偽値リストを返す。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このメソッドでは、取得した要素をメモリ上のリストとして取得するため、大容量のデータを扱う場合にメモリ不足が発生する可能性がある。
	 * </blockquote>
	 * @param base	データ代数基底
	 * @return	基底に割り当てられた全ての値を格納する真偽値リストを返す。
	 * 			基底に割り当てられた値が存在しない場合は、要素が空の真偽値リストを返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	基底のデータ型が真偽値型ではない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public List<Boolean> toBooleanList(DtBase base) {
		if (!DtDataTypes.BOOLEAN.equals(base.getTypeKey()))
			throw new IllegalArgumentException("DtBase data type is not Boolean.");
		
		List<Boolean> list = new ArrayList<>();
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = findByBase(base, true, true).iterator();
			while (cursor.hasNext()) {
				Document docDtAlgeSetElem = cursor.next();
				list.add((Boolean)MongoDelegateDocDtAlgeSetElem.getValueObjectFromDocument(docDtAlgeSetElem));
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get boolean values by DtBase in MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		
		return list;
	}
	
	/**
	 * 指定された基底に割り当てられた全ての重複しない(同値ではない)値が格納された新しい真偽値リストを返す。
	 * @param base	データ代数基底
	 * @return	基底に割り当てられた全ての重複しない(同値ではない)値を格納する真偽値リストを返す。
	 * 			基底に割り当てられた値が存在しない場合は、要素が空の真偽値リストを返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	基底のデータ型が真偽値型ではない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public List<Boolean> toDistinctBooleanList(DtBase base) {
		if (!DtDataTypes.BOOLEAN.equals(base.getTypeKey()))
			throw new IllegalArgumentException("DtBase data type is not Boolean.");
		
		List<Boolean> list = new ArrayList<>();
		MongoCursor<?> cursor = null;
		try {
			cursor = distinctValuesByDtBase(base).iterator();
			while (cursor.hasNext()) {
				Object value = cursor.next();
				list.add((Boolean)value);
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get distinct boolean values by DtBase in MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		
		return list;
	}

	/**
	 * 指定された基底に割り当てられている値のうち、<tt>null</tt> ではない最小値を取得する。
	 * 比較不可能な値の場合や、指定された基底をもつデータ代数元が一つも存在しない場合、
	 * このメソッドは <tt>null</tt> を返す。
	 * @param base	比較する値のデータ代数基底
	 * @return	指定された基底に割り当てられている最小値を返す。
	 * 			最小値が取得できない場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public Object minValue(DtBase base) {
		if (base == null) throw new NullPointerException();
		return getMinValue(base);
	}
	
	/**
	 * 指定された基底に割り当てられている値のうち、<tt>null</tt> ではない最大値を取得する。
	 * 比較不可能な値の場合や、指定された基底をもつデータ代数元が一つも存在しない場合、
	 * このメソッドは <tt>null</tt> を返す。
	 * @param base	比較する値のデータ代数基底
	 * @return	指定された基底に割り当てられている最大値を返す。
	 * 			最大値が取得できない場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public Object maxValue(DtBase base) {
		if (base == null) throw new NullPointerException();
		return getMaxValue(base);
	}
	
	/**
	 * 指定された基底に割り当てられている値のうち、<tt>null</tt> ではない最小の実数値を取得する。
	 * 比較不可能な値の場合や、指定された基底をもつデータ代数元が一つも存在しない場合、
	 * このメソッドは <tt>null</tt> を返す。
	 * 指定された基底が実数型のデータ型ではない場合、このメソッドは例外をスローする。
	 * @param base	比較する値のデータ代数基底
	 * @return	指定された基底に割り当てられている最小値を返す。
	 * 			最小値が取得できない場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	基底のデータ型が実数型ではない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public BigDecimal minDecimal(DtBase base) {
		if (!DtDataTypes.DECIMAL.equals(base.getTypeKey()))
			throw new IllegalArgumentException("DtBase data type is not Decimal.");
		return (BigDecimal)getMinValue(base);
	}
	
	/**
	 * 指定された基底に割り当てられている値のうち、<tt>null</tt> ではない最大の実数値を取得する。
	 * 比較不可能な値の場合や、指定された基底をもつデータ代数元が一つも存在しない場合、
	 * このメソッドは <tt>null</tt> を返す。
	 * 指定された基底が実数型のデータ型ではない場合、このメソッドは例外をスローする。
	 * @param base	比較する値のデータ代数基底
	 * @return	指定された基底に割り当てられている最大値を返す。
	 * 			最大値が取得できない場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	基底のデータ型が実数型ではない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public BigDecimal maxDecimal(DtBase base) {
		if (!DtDataTypes.DECIMAL.equals(base.getTypeKey()))
			throw new IllegalArgumentException("DtBase data type is not Decimal.");
		return (BigDecimal)getMaxValue(base);
	}

	/**
	 * 指定された基底に、指定された値が関連付けられているデータ代数元が存在するかを判定する。
	 * <em>base</em> に <tt>null</tt> が指定された場合、このメソッドは <tt>false</tt> を返す。
	 * @param base	データ代数基底
	 * @param value	判定する値
	 * @return	存在する場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean containsValue(DtBase base, Object value) {
		if (base == null)
			return false;
		
		try {
			Bson query = Filters.and(makeQueryByDtBase(base), makeQueryByValue(value));
			long num = _mongo_vals_col.countDocuments(query);
			return (num > 0L);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to find document by DtBase and value in MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
	}

	/**
	 * 指定された基底に、指定されたコレクションに含まれる値のどれか一つが関連付けられている
	 * データ代数元が存在するかを判定する。
	 * <em>base</em> に <tt>null</tt> が指定された場合、このメソッドは <tt>false</tt> を返す。
	 * @param base		データ代数基底
	 * @param values	判定する値を格納するコレクション
	 * @return			存在する場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean containsAnyValues(DtBase base, Collection<?> values) {
		if (base == null)
			return false;
		if (values==null || values.isEmpty())
			return false;
		
		try {
			Bson query = Filters.and(makeQueryByDtBase(base), makeQueryByMultiValues(values, false));
			long num = _mongo_vals_col.countDocuments(query);
			return (num > 0L);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to find document by DtBase and values in MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
	}

	/**
	 * 自身に含まれる全てのデータ代数元に対し、
	 * {@link dtalge.db.mongo.MongoDtalge#oneValueProjection(Object)} した結果を保持する集合を返す。
	 * <br>
	 * このメソッドでは、全要素について {@link dtalge.db.mongo.MongoDtalge#oneValueProjection(Object)} した結果となる
	 * データ代数元を持つ、新しいデータ代数集合を生成する。
	 * このとき、要素が空となるデータ代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * @param value	取り出すようその値
	 * @return	指定された値と等しい要素のみを含むデータ代数元の集合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 * 
	 * @see dtalge.db.mongo.MongoDtalge#oneValueProjection(Object)
	 */
	@Override
	public MongoDtAlgeSet oneValueProjection(Object value) {
		//--- 要素なし
		if (this.isEmpty()) {
			return new MongoDtAlgeSet(_mongo_session);
		}
		
		//--- projection by value
		MongoDtAlgeSet newSet = new MongoDtAlgeSet(_mongo_session);
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = findByValue(value, true, false).iterator();
			while (cursor.hasNext()) {
				newSet._mongo_vals_col.insertOne(cursor.next());
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to project by one value from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		return newSet;
	}

	/**
	 * 自身に含まれる全てのデータ代数元に対し、
	 * {@link dtalge.db.mongo.MongoDtalge#valuesProjection(Collection)} した結果を保持する集合を返す。
	 * <br>
	 * このメソッドでは、全要素について {@link dtalge.db.mongo.MongoDtalge#valuesProjection(Collection)} した結果となる
	 * データ代数元を持つ、新しいデータ代数集合を生成する。
	 * このとき、要素が空となるデータ代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * @param values	取り出す要素の値のコレクション
	 * @return	指定されたコレクションに含まれる値と等しい要素のみを含む
	 * 			データ代数元の集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 * 
	 * @see dtalge.db.mongo.MongoDtalge#valuesProjection(Collection)
	 */
	@Override
	public MongoDtAlgeSet valuesProjection(Collection<?> values) {
		MongoDtAlgeSet newSet = new MongoDtAlgeSet(_mongo_session);
		if (!values.isEmpty() && !this.isEmpty()) {
			MongoCursor<? extends Document> cursor = null;
			try {
				cursor = queryDocuments(makeQueryByMultiValues(values, false), true, false).iterator();
				while (cursor.hasNext()) {
					newSet._mongo_vals_col.insertOne(cursor.next());
				}
			}
			catch (MongoException ex) {
				throw new MongoAlgeError("Failed to query by values from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
			}
			finally {
				MongoUtil.closeCursorSilent(cursor);
			}
		}
		return newSet;
	}

	/**
	 * 自身に含まれる全てのデータ代数元に対し、
	 * {@link dtalge.db.mongo.MongoDtalge#nullProjection()} した結果を保持する集合を返す。
	 * <br>
	 * このメソッドでは、全要素について {@link dtalge.db.mongo.MongoDtalge#nullProjection()} した結果となる
	 * データ代数元を持つ、新しいデータ代数集合を生成する。
	 * このとき、要素が空となるデータ代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * @return	値が <tt>null</tt> の要素のみを含むデータ代数元の集合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 * 
	 * @see dtalge.db.mongo.MongoDtalge#nullProjection()
	 */
	@Override
	public MongoDtAlgeSet nullProjection() {
		return oneValueProjection(null);
	}
	
	/**
	 * 自身に含まれる全てのデータ代数元に対し、
	 * {@link dtalge.db.mongo.MongoDtalge#nonullProjection()} した結果を保持する集合を返す。
	 * <br>
	 * このメソッドでは、全要素について {@link dtalge.db.mongo.MongoDtalge#nonullProjection()} した結果となる
	 * データ代数元を持つ、新しいデータ代数集合を生成する。
	 * このとき、要素が空となるデータ代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * @return	値が <tt>null</tt> ではない要素のみを含むデータ代数元の集合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 * 
	 * @see dtalge.db.mongo.MongoDtalge#nonullProjection()
	 */
	@Override
	public MongoDtAlgeSet nonullProjection() {
		//--- 要素なし
		if (this.isEmpty()) {
			return new MongoDtAlgeSet(_mongo_session);
		}

		MongoDtAlgeSet newSet = new MongoDtAlgeSet(_mongo_session);
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = queryDocuments(makeQueryWithoutValue(null), true, false).iterator();
			while (cursor.hasNext()) {
				newSet._mongo_vals_col.insertOne(cursor.next());
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to project by non null value from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		return newSet;
	}

	/**
	 * この集合に含まれる全てのデータ代数元に対し、指定された基底で
	 * プロジェクションした結果を保持する集合を返す。
	 * <p>このメソッドは、この集合に含まれる全要素についてプロジェクション
	 * した結果となるデータ代数元を格納する、新しい集合を生成する。
	 * プロジェクションの結果、要素を持たないデータ代数元は除外される。
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @param base	取り出すデータ代数基底
	 * @return	取り出された要素を持つデータ代数元の集合
	 * 
	 * @throws NullPointerException 指定された基底が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 * 
	 * @see dtalge.db.mongo.MongoDtalge#projection(DtBase)
	 */
	@Override
	public MongoDtAlgeSet projection(DtBase base) {
		Validations.validNotNull(base);

		MongoDtAlgeSet newSet = new MongoDtAlgeSet(_mongo_session);
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = queryDocuments(makeQueryByDtBase(base), true, false).iterator();
			while (cursor.hasNext()) {
				newSet._mongo_vals_col.insertOne(cursor.next());
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to project by one DtBase from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		return newSet;
	}

	/**
	 * この集合に含まれる全てのデータ代数元に対し、指定された基底集合で
	 * プロジェクションした結果を保持する集合を返す。
	 * <p>このメソッドは、この集合に含まれる全要素についてプロジェクション
	 * した結果となるデータ代数元を格納する、新しい集合を生成する。
	 * プロジェクションの結果、要素を持たないデータ代数元は除外される。
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @param bases	取り出すデータ代数基底の集合
	 * @return	取り出された要素を持つデータ代数元の集合
	 * 
	 * @throws NullPointerException 指定された基底集合が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 * 
	 * @see dtalge.db.mongo.MongoDtalge#projection(DtBaseSet)
	 */
	@Override
	public MongoDtAlgeSet projection(DtBaseSet bases) {
		Validations.validNotNull(bases);
		
		// 要素がなければ、空集合を返す
		if (this.isEmpty() || bases.isEmpty()) {
			return new MongoDtAlgeSet(_mongo_session);
		}
		
		// 全要素をプロジェクション
		MongoDtAlgeSet newSet = new MongoDtAlgeSet(_mongo_session);
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = findAllDocuments(true, false).iterator();
			while (cursor.hasNext()) {
				Document docDtAlgeSetElem = cursor.next();
				DtBase docBase = MongoDelegateDocDtAlgeSetElem.toDtBaseFromDocument(docDtAlgeSetElem);
				if (bases.contains(docBase)) {
					newSet._mongo_vals_col.insertOne(docDtAlgeSetElem);
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to project by DtBaseSet from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		return newSet;
	}

	/**
	 * この集合に含まれる全てのデータ代数元に対し、指定された基底集合で
	 * プロジェクションした結果を保持する集合を返す。
	 * <p>このメソッドは、この集合に含まれる全要素についてプロジェクション
	 * した結果となるデータ代数元を格納する、新しい集合を生成する。
	 * プロジェクションの結果、要素を持たないデータ代数元は除外される。
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @param bases	取り出すデータ代数基底の集合
	 * @return	取り出された要素を持つデータ代数元の集合
	 * 
	 * @throws NullPointerException 指定された基底集合が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 * 
	 * @see dtalge.db.mongo.MongoDtalge#projection(dtalge.db.BigDtBaseSet)
	 */
	@Override
	public MongoDtAlgeSet projection(BigDtBaseSet bases) {
		Validations.validNotNull(bases);
		
		// 要素がなければ、空集合を返す
		if (this.isEmpty() || bases.isEmpty()) {
			return new MongoDtAlgeSet(_mongo_session);
		}
		
		// 全要素をプロジェクション
		MongoDtAlgeSet newSet = new MongoDtAlgeSet(_mongo_session);
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = findAllDocuments(true, false).iterator();
			while (cursor.hasNext()) {
				Document docDtAlgeSetElem = cursor.next();
				DtBase docBase = MongoDelegateDocDtAlgeSetElem.toDtBaseFromDocument(docDtAlgeSetElem);
				if (bases.contains(docBase)) {
					newSet._mongo_vals_col.insertOne(docDtAlgeSetElem);
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to project by DtBaseSet from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		return newSet;
	}

	/**
	 * この集合に含まれる全てのデータ代数元に対し、指定された基底パターンに
	 * よってプロジェクションした結果を保持する集合を返す。
	 * <p>
	 * このメソッドは、この集合に含まれる全要素について、指定された基底パターンに
	 * 一致する基底のみでプロジェクションした結果となるデータ代数元を格納する、
	 * 新しい集合を生成する。
	 * プロジェクションの結果、要素を持たないデータ代数元は除外される。
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @param pattern	基底パターン
	 * @return	基底パターンでプロジェクションした結果となるデータ代数元の集合
	 * 
	 * @throws	NullPointerException	指定された基底パターンが <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 * 
	 * @see dtalge.db.mongo.MongoDtalge#patternProjection(DtBasePattern)
	 */
	@Override
	public MongoDtAlgeSet patternProjection(DtBasePattern pattern) {
		Validations.validNotNull(pattern);
		
		// 要素がなければ、空集合を返す
		if (this.isEmpty()) {
			return new MongoDtAlgeSet(_mongo_session);
		}
		
		// 全要素をプロジェクション
		MongoDtAlgeSet newSet = new MongoDtAlgeSet(_mongo_session);
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = findAllDocuments(true, false).iterator();
			while (cursor.hasNext()) {
				Document docDtAlgeSetElem = cursor.next();
				DtBase docBase = MongoDelegateDocDtAlgeSetElem.toDtBaseFromDocument(docDtAlgeSetElem);
				if (pattern.matches(docBase)) {
					newSet._mongo_vals_col.insertOne(docDtAlgeSetElem);
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to project by DtBasePattern from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		return newSet;
	}

	/**
	 * この集合に含まれる全てのデータ代数元に対し、指定された基底パターンに
	 * よってプロジェクションした結果を保持する集合を返す。
	 * <p>
	 * このメソッドは、この集合に含まれる全要素について、指定された基底パターン
	 * のいずれかに一致する基底のみでプロジェクションした結果となるデータ代数元を
	 * 格納する、新しい集合を生成する。
	 * プロジェクションの結果、要素を持たないデータ代数元は除外される。
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @param patterns	基底パターンの集合
	 * @return	基底パターンでプロジェクションした結果となるデータ代数元の集合
	 * 
	 * @throws NullPointerException	指定された基底パターン集合が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 * 
	 * @see dtalge.db.mongo.MongoDtalge#patternProjection(DtBasePatternSet)
	 */
	@Override
	public MongoDtAlgeSet patternProjection(DtBasePatternSet patterns) {
		// 要素が存在しなければ、空集合を返す
		if (this.isEmpty() || patterns.isEmpty()) {
			return new MongoDtAlgeSet(_mongo_session);
		}
		
		// 全要素をプロジェクション
		MongoDtAlgeSet newSet = new MongoDtAlgeSet(_mongo_session);
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = findAllDocuments(true, false).iterator();
			while (cursor.hasNext()) {
				Document docDtAlgeSetElem = cursor.next();
				DtBase docBase = MongoDelegateDocDtAlgeSetElem.toDtBaseFromDocument(docDtAlgeSetElem);
				if (patterns.matches(docBase)) {
					newSet._mongo_vals_col.insertOne(docDtAlgeSetElem);
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to project by DtBasePatternSet from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		return newSet;
	}

	/**
	 * 指定された基底に関連付けられた値が、指定された値と等しい要素を含むデータ代数元のみを取り出す。
	 * @param base	データ代数基底
	 * @param value	判定する値
	 * @return	条件に一致したデータ代数元のみを格納する、新しいデータ代数集合を返す。
	 * 			条件に一致するデータ代数元が一つも存在しない場合は、要素が空のデータ代数集合を返す。
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoDtAlgeSet selectEqualValue(DtBase base, Object value) {
		if (base == null) throw new NullPointerException();
		if (this.isEmpty())
			return new MongoDtAlgeSet(_mongo_session);

		MongoDtAlgeSet newSet = new MongoDtAlgeSet(_mongo_session);
		MongoCursor<String> setid_cursor = null;
		try {
			setid_cursor = distinctSetIdByValue(base, value).iterator();
			while (setid_cursor.hasNext()) {
				String targetid = setid_cursor.next();
				MongoCursor<? extends Document> cursor = findBySetId(targetid, true, false).iterator();
				try {
					while (cursor.hasNext()) {
						newSet._mongo_vals_col.insertOne(cursor.next());
					}
				}
				finally {
					MongoUtil.closeCursorSilent(cursor);
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to select by value from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(setid_cursor);
		}
		
		return newSet;
	}
	
	/**
	 * 指定された基底に関連付けられた値が、指定された値と等しい要素を含むデータ代数元以外の
	 * データ代数元を全て取り出す。このメソッドが返すデータ代数集合には、指定された基底が存在しない
	 * データ代数元も含まれる。
	 * <p>この動作は、{@link #removeEqualValue(DtBase, Object)} と同じ。
	 * @param base	データ代数基底
	 * @param value	判定する値
	 * @return	条件に一致したデータ代数元のみを格納する、新しいデータ代数集合を返す。
	 * 			条件に一致するデータ代数元が一つも存在しない場合は、要素が空のデータ代数集合を返す。
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 * @see #removeEqualValue(DtBase, Object)
	 */
	@Override
	public MongoDtAlgeSet selectNotEqualValue(DtBase base, Object value) {
		return removeEqualValue(base, value);
	}
	
	/**
	 * 指定された基底に関連付けられた値が、指定された値よりも小さい要素を含むデータ代数元のみを取り出す。
	 * 指定された値と比較不可能な要素は、結果には含まれない。また、<em>value</em> に
	 * <tt>null</tt> を指定した場合、このメソッドは要素が空のデータ代数集合を返す。
	 * @param base	データ代数基底
	 * @param value	判定する値
	 * @return	条件に一致したデータ代数元のみを格納する、新しいデータ代数集合を返す。
	 * 			条件に一致するデータ代数元が一つも存在しない場合は、要素が空のデータ代数集合を返す。
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoDtAlgeSet selectLessThanValue(DtBase base, Object value) {
		if (base == null) throw new NullPointerException();
		if (this.isEmpty() || value == null || !(value instanceof Comparable))
			return new MongoDtAlgeSet(_mongo_session);
		try {
			DtDataTypes.validDataType(base.getTypeKey(), value);
		} catch (Throwable ex) {
			// cannot comparable
			return new MongoDtAlgeSet(_mongo_session);
		}

		MongoDtAlgeSet newSet = new MongoDtAlgeSet(_mongo_session);
		MongoCursor<String> setid_cursor = null;
		try {
			setid_cursor = distinctSetId(Filters.and(makeQueryByDtBase(base), Filters.lt(MongoDelegateDocDtAlgeSetElem.MONGO_KEY_VALUE, value))).iterator();
			while (setid_cursor.hasNext()) {
				String targetid = setid_cursor.next();
				MongoCursor<? extends Document> cursor = findBySetId(targetid, true, false).iterator();
				try {
					while (cursor.hasNext()) {
						newSet._mongo_vals_col.insertOne(cursor.next());
					}
				}
				finally {
					MongoUtil.closeCursorSilent(cursor);
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to select by (x < value) from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(setid_cursor);
		}
		
		return newSet;
	}
	
	/**
	 * 指定された基底に関連付けられた値が、指定された値よりも小さい、もしくは等しい要素を含む
	 * データ代数元のみを取り出す。
	 * 指定された値と比較不可能な要素は、結果には含まれない。また、<em>value</em> に
	 * <tt>null</tt> を指定した場合、このメソッドは要素が空のデータ代数集合を返す。
	 * @param base	データ代数基底
	 * @param value	判定する値
	 * @return	条件に一致したデータ代数元のみを格納する、新しいデータ代数集合を返す。
	 * 			条件に一致するデータ代数元が一つも存在しない場合は、要素が空のデータ代数集合を返す。
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoDtAlgeSet selectLessEqualValue(DtBase base, Object value) {
		if (base == null) throw new NullPointerException();
		if (this.isEmpty() || value == null || !(value instanceof Comparable))
			return new MongoDtAlgeSet(_mongo_session);
		try {
			DtDataTypes.validDataType(base.getTypeKey(), value);
		} catch (Throwable ex) {
			// cannot comparable
			return new MongoDtAlgeSet(_mongo_session);
		}

		MongoDtAlgeSet newSet = new MongoDtAlgeSet(_mongo_session);
		MongoCursor<String> setid_cursor = null;
		try {
			setid_cursor = distinctSetId(Filters.and(makeQueryByDtBase(base), Filters.lte(MongoDelegateDocDtAlgeSetElem.MONGO_KEY_VALUE, value))).iterator();
			while (setid_cursor.hasNext()) {
				String targetid = setid_cursor.next();
				MongoCursor<? extends Document> cursor = findBySetId(targetid, true, false).iterator();
				try {
					while (cursor.hasNext()) {
						newSet._mongo_vals_col.insertOne(cursor.next());
					}
				}
				finally {
					MongoUtil.closeCursorSilent(cursor);
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to select by (x <= value) from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(setid_cursor);
		}
		
		return newSet;
	}
	
	
	/**
	 * 指定された基底に関連付けられた値が、指定された値よりも大きい要素を含むデータ代数元のみを取り出す。
	 * 指定された値と比較不可能な要素は、結果には含まれない。また、<em>value</em> に
	 * <tt>null</tt> を指定した場合、このメソッドは要素が空のデータ代数集合を返す。
	 * @param base	データ代数基底
	 * @param value	判定する値
	 * @return	条件に一致したデータ代数元のみを格納する、新しいデータ代数集合を返す。
	 * 			条件に一致するデータ代数元が一つも存在しない場合は、要素が空のデータ代数集合を返す。
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoDtAlgeSet selectGreaterThanValue(DtBase base, Object value) {
		if (base == null) throw new NullPointerException();
		if (this.isEmpty() || value == null || !(value instanceof Comparable))
			return new MongoDtAlgeSet(_mongo_session);
		try {
			DtDataTypes.validDataType(base.getTypeKey(), value);
		} catch (Throwable ex) {
			// cannot comparable
			return new MongoDtAlgeSet(_mongo_session);
		}

		MongoDtAlgeSet newSet = new MongoDtAlgeSet(_mongo_session);
		MongoCursor<String> setid_cursor = null;
		try {
			setid_cursor = distinctSetId(Filters.and(makeQueryByDtBase(base), Filters.gt(MongoDelegateDocDtAlgeSetElem.MONGO_KEY_VALUE, value))).iterator();
			while (setid_cursor.hasNext()) {
				String targetid = setid_cursor.next();
				MongoCursor<? extends Document> cursor = findBySetId(targetid, true, false).iterator();
				try {
					while (cursor.hasNext()) {
						newSet._mongo_vals_col.insertOne(cursor.next());
					}
				}
				finally {
					MongoUtil.closeCursorSilent(cursor);
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to select by (x > value) from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(setid_cursor);
		}
		
		return newSet;
	}
	
	/**
	 * 指定された基底に関連付けられた値が、指定された値よりも大きい、もしくは等しい要素を含む
	 * データ代数元のみを取り出す。
	 * 指定された値と比較不可能な要素は、結果には含まれない。また、<em>value</em> に
	 * <tt>null</tt> を指定した場合、このメソッドは要素が空のデータ代数集合を返す。
	 * @param base	データ代数基底
	 * @param value	判定する値
	 * @return	条件に一致したデータ代数元のみを格納する、新しいデータ代数集合を返す。
	 * 			条件に一致するデータ代数元が一つも存在しない場合は、要素が空のデータ代数集合を返す。
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoDtAlgeSet selectGreaterEqualValue(DtBase base, Object value) {
		if (base == null) throw new NullPointerException();
		if (this.isEmpty() || value == null || !(value instanceof Comparable))
			return new MongoDtAlgeSet(_mongo_session);
		try {
			DtDataTypes.validDataType(base.getTypeKey(), value);
		} catch (Throwable ex) {
			// cannot comparable
			return new MongoDtAlgeSet(_mongo_session);
		}

		MongoDtAlgeSet newSet = new MongoDtAlgeSet(_mongo_session);
		MongoCursor<String> setid_cursor = null;
		try {
			setid_cursor = distinctSetId(Filters.and(makeQueryByDtBase(base), Filters.gte(MongoDelegateDocDtAlgeSetElem.MONGO_KEY_VALUE, value))).iterator();
			while (setid_cursor.hasNext()) {
				String targetid = setid_cursor.next();
				MongoCursor<? extends Document> cursor = findBySetId(targetid, true, false).iterator();
				try {
					while (cursor.hasNext()) {
						newSet._mongo_vals_col.insertOne(cursor.next());
					}
				}
				finally {
					MongoUtil.closeCursorSilent(cursor);
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to select by (x > value) from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(setid_cursor);
		}
		
		return newSet;
	}
	
	/**
	 * 指定された基底に関連付けられた値が、指定された値と等しい要素を含む全てのデータ代数元を、
	 * 指定されたデータ代数元で置き換えられた新しいデータ代数集合を返す。
	 * @param base		データ代数基底
	 * @param value		判定する値
	 * @param newAlge	新しいデータ代数元
	 * @return	条件に一致したデータ代数元が置き換えられた、新しいデータ代数集合を返す。
	 * @throws NullPointerException	<em>base</em>、もしくは <em>newAlge</em> が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoDtAlgeSet replaceEqualValue(DtBase base, Object value, Dtalge newAlge) {
		if (base == null || newAlge == null) throw new NullPointerException();
		if (this.isEmpty())
			return new MongoDtAlgeSet(_mongo_session);

		MongoDtAlgeSet newSet = new MongoDtAlgeSet(_mongo_session);
		MongoCursor<String> setid_cursor = null;
		try {
			Bson queryByBase  = makeQueryByDtBase(base);
			Bson queryByValue = makeQueryByValue(value);
			setid_cursor = distinctSetId(null).iterator();	// all documents
			while (setid_cursor.hasNext()) {
				String targetid = setid_cursor.next();
				long cnt = _mongo_vals_col.countDocuments(Filters.and(makeQueryBySetId(targetid), queryByBase, queryByValue));
				if (cnt > 0L) {
					// replace target
					for (Map.Entry<DtBase, Object> entry : newAlge.getUnmodifiableEntrySet()) {
						newSet._mongo_vals_col.insertOne(MongoDelegateDocDtAlgeSetElem.makeDtAlgeSetElemDocument(targetid, entry.getKey(), entry.getValue()));
					}
				}
				else {
					// insert original documents
					MongoCursor<? extends Document> cursor = findBySetId(targetid, true, false).iterator();
					try {
						while (cursor.hasNext()) {
							newSet._mongo_vals_col.insertOne(cursor.next());
						}
					}
					finally {
						MongoUtil.closeCursorSilent(cursor);
					}
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to replace to newAlge from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(setid_cursor);
		}
		
		return newSet;
	}
	
	/**
	 * 指定された基底に関連付けられた値が、指定された値と等しい要素を含む全てのデータ代数元を、
	 * 指定されたデータ代数元で置き換えられた新しいデータ代数集合を返す。
	 * @param base		データ代数基底
	 * @param value		判定する値
	 * @param newAlge	新しいデータ代数元
	 * @return	条件に一致したデータ代数元が置き換えられた、新しいデータ代数集合を返す。
	 * @throws NullPointerException	<em>base</em>、もしくは <em>newAlge</em> が <tt>null</tt> の場合
	 */
	@Override
	public MongoDtAlgeSet replaceEqualValue(DtBase base, Object value, BigDtalge newAlge) {
		if (base == null || newAlge == null) throw new NullPointerException();
		if (this.isEmpty())
			return new MongoDtAlgeSet(_mongo_session);

		MongoDtAlgeSet newSet = new MongoDtAlgeSet(_mongo_session);
		MongoCursor<String> setid_cursor = null;
		try {
			Bson queryByBase  = makeQueryByDtBase(base);
			Bson queryByValue = makeQueryByValue(value);
			setid_cursor = distinctSetId(null).iterator();	// all documents
			while (setid_cursor.hasNext()) {
				String targetid = setid_cursor.next();
				long cnt = _mongo_vals_col.countDocuments(Filters.and(makeQueryBySetId(targetid), queryByBase, queryByValue));
				if (cnt > 0L) {
					// replace target
					BigIterator<BigDtalgeElement> it = newAlge.elementIterator();
					while (it.hasNext()) {
						BigDtalgeElement elem = it.next();
						newSet._mongo_vals_col.insertOne(MongoDelegateDocDtAlgeSetElem.makeDtAlgeSetElemDocument(targetid, elem.getBase(), elem.getValue()));
					}
				}
				else {
					// insert original documents
					MongoCursor<? extends Document> cursor = findBySetId(targetid, true, false).iterator();
					try {
						while (cursor.hasNext()) {
							newSet._mongo_vals_col.insertOne(cursor.next());
						}
					}
					finally {
						MongoUtil.closeCursorSilent(cursor);
					}
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to replace to newAlge from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(setid_cursor);
		}
		
		return newSet;
	}
	
	/**
	 * 指定された基底に関連付けられた値が、指定された値と等しい要素を含む全てのデータ代数元を、
	 * 指定されたデータ代数元で置き換える。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このメソッドは、自身の内容を書き換える、破壊型メソッドである。
	 * </blockquote>
	 * @param base		データ代数基底
	 * @param value		判定する値
	 * @param newAlge	新しいデータ代数元
	 * @return	自身の内容が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	<em>base</em>、もしくは <em>newAlge</em> が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean updateEqualValue(DtBase base, Object value, Dtalge newAlge) {
		if (base == null || newAlge == null) throw new NullPointerException();
		if (this.isEmpty())
			return false;

		boolean modified = false;
		MongoCursor<String> setid_cursor = null;
		try {
			setid_cursor = distinctSetIdByValue(base, value).iterator();
			while (setid_cursor.hasNext()) {
				String targetid = setid_cursor.next();
				//--- delete
				DeleteResult delres = _mongo_vals_col.deleteMany(makeQueryBySetId(targetid));
				if (delres.getDeletedCount() > 0) {
					modified = true;
				}
				//--- insert newAlge
				for (Map.Entry<DtBase, Object> entry : newAlge.getUnmodifiableEntrySet()) {
					_mongo_vals_col.insertOne(MongoDelegateDocDtAlgeSetElem.makeDtAlgeSetElemDocument(targetid, entry.getKey(), entry.getValue()));
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to update with newAlge from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(setid_cursor);
		}
		
		return modified;
	}
	
	/**
	 * 指定された基底に関連付けられた値が、指定された値と等しい要素を含む全てのデータ代数元を、
	 * 指定されたデータ代数元で置き換える。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このメソッドは、自身の内容を書き換える、破壊型メソッドである。
	 * </blockquote>
	 * @param base		データ代数基底
	 * @param value		判定する値
	 * @param newAlge	新しいデータ代数元
	 * @return	自身の内容が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	<em>base</em>、もしくは <em>newAlge</em> が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean updateEqualValue(DtBase base, Object value, BigDtalge newAlge) {
		if (base == null || newAlge == null) throw new NullPointerException();
		if (this.isEmpty())
			return false;

		boolean modified = false;
		MongoCursor<String> setid_cursor = null;
		try {
			setid_cursor = distinctSetIdByValue(base, value).iterator();
			while (setid_cursor.hasNext()) {
				String targetid = setid_cursor.next();
				//--- delete
				DeleteResult delres = _mongo_vals_col.deleteMany(makeQueryBySetId(targetid));
				if (delres.getDeletedCount() > 0) {
					modified = true;
				}
				//--- insert newAlge
				BigIterator<BigDtalgeElement> it = newAlge.elementIterator();
				while (it.hasNext()) {
					BigDtalgeElement elem = it.next();
					_mongo_vals_col.insertOne(MongoDelegateDocDtAlgeSetElem.makeDtAlgeSetElemDocument(targetid, elem.getBase(), elem.getValue()));
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to update with newAlge from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(setid_cursor);
		}
		
		return modified;
	}

	/**
	 * 指定された基底に関連付けられた値が、指定された値と等しい要素を含む全てのデータ代数元を
	 * 取り除いた、新しいデータ代数集合を返す。
	 * @param base	データ代数基底
	 * @param value	判定する値
	 * @return	条件に一致したデータ代数元を含まない、新しいデータ代数集合を返す。
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoDtAlgeSet removeEqualValue(DtBase base, Object value) {
		if (base == null) throw new NullPointerException();
		if (this.isEmpty())
			return new MongoDtAlgeSet(_mongo_session);

		MongoDtAlgeSet newSet = new MongoDtAlgeSet(_mongo_session);
		MongoCursor<String> setid_cursor = null;
		try {
			Bson queryByBase  = makeQueryByDtBase(base);
			Bson queryByValue = makeQueryByValue(value);
			setid_cursor = distinctSetId(null).iterator();	// all documents
			while (setid_cursor.hasNext()) {
				String targetid = setid_cursor.next();
				long cnt = _mongo_vals_col.countDocuments(Filters.and(makeQueryBySetId(targetid), queryByBase, queryByValue));
				if (cnt == 0L) {
					// insert original documents
					MongoCursor<? extends Document> cursor = findBySetId(targetid, true, false).iterator();
					try {
						while (cursor.hasNext()) {
							newSet._mongo_vals_col.insertOne(cursor.next());
						}
					}
					finally {
						MongoUtil.closeCursorSilent(cursor);
					}
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to remove with value from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(setid_cursor);
		}
		
		return newSet;
	}

	/**
	 * 指定された基底に関連付けられた値が、指定された値と等しい要素を含む全てのデータ代数元を
	 * 削除する。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このメソッドは、自身の内容を書き換える、破壊型メソッドである。
	 * </blockquote>
	 * @param base	データ代数基底
	 * @param value	判定する値
	 * @return	自身の内容が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean deleteEqualValue(DtBase base, Object value) {
		if (base == null) throw new NullPointerException();
		if (this.isEmpty())
			return false;

		boolean modified = false;
		MongoCursor<String> setid_cursor = null;
		try {
			setid_cursor = distinctSetIdByValue(base, value).iterator();
			while (setid_cursor.hasNext()) {
				String targetid = setid_cursor.next();
				//--- delete
				DeleteResult delres = _mongo_vals_col.deleteMany(makeQueryBySetId(targetid));
				if (delres.getDeletedCount() > 0) {
					modified = true;
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to delete with value from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(setid_cursor);
		}
		
		return modified;
	}

	/**
	 * 指定された基底に関連付けられている値のうち、指定されたシソーラス定義において
	 * 比較不可能な極大値のみを含むデータ代数元を取り出す。
	 * <p>このメソッドは、指定された基底に関連付けられている値をすべて比較し、
	 * シソーラス定義において比較可能な値の中から最大値のみを含むデータ代数元を取り出す。
	 * 取り出されたデータ代数元に含まれる値は、それぞれ比較不可能もしくは同値となっている。
	 * @param base	判定する値が関連付けられているデータ代数基底
	 * @param thes	シソーラス定義
	 * @return	比較不可能な極大値のみを含むデータ代数元を格納する、新しいデータ代数集合を返す。
	 * 			比較不可能な極大値が存在しない場合は、要素が空のデータ代数集合を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>base</em> が文字列型のデータ代数基底ではない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoDtAlgeSet selectThesaurusMax(DtBase base, IDtStringThesaurus thes) {
		// check
		if (base == null)
			throw new NullPointerException("DtBase argument is null.");
		if (thes == null)
			throw new NullPointerException("DtThesaurus object is null.");
		if (!DtDataTypes.STRING.equals(base.getTypeKey()))
			throw new IllegalArgumentException("DtBase data type is not String.");
		
		if (thes.isEmpty() || this.isEmpty()) {
			//--- No thesaurus relations or No alge elements
			return new MongoDtAlgeSet(_mongo_session);
		}
		// 極大値の元のみを取り出す
		Set<String> maxids = new TreeSet<String>();
		Map<String, Set<String>> wordmap = new HashMap<>();
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = findByBase(base, true, false).iterator();
			while (cursor.hasNext()) {
				Document docElem = cursor.next();
				String value = (String)MongoDelegateDocDtAlgeSetElem.getValueObjectFromDocument(docElem);
				if (value == null || !thes.contains(value)) {
					// 語句がシソーラス定義に存在しない
					continue;
				}
				// シソーラスに定義されている語句を比較
				Iterator<Map.Entry<String,Set<String>>> it = wordmap.entrySet().iterator();
				while (it.hasNext()) {
					// 極大値候補との比較
					Map.Entry<String, Set<String>> entry = it.next();
					int cmp = thes.compare(value, entry.getKey());
					if (cmp < 0) {
						// value < entry.getKey()
						//--- 比較可能な値の中で最大値ではないので、候補に含めない
						value = null;
						break;
					}
					else if (cmp > 0) {
						// value > entry.getKey()
						//--- 比較可能な値の中で最大値のため、候補を除外
						maxids.removeAll(entry.getValue());
						it.remove();
					}
					// else : value == entry.getKey()
					//--- 比較不可能もしくは同値のため、次の候補と比較
				}
				// 語句を候補へ登録
				if (value != null) {
					String candsetid = MongoDelegateDocDtAlgeSetElem.getDtAlgeSetIDfromDocument(docElem);
					Set<String> candset = wordmap.get(value);
					if (candset == null) {
						// 新規候補を追加
						candset = new TreeSet<String>();
						candset.add(candsetid);
						wordmap.put(value, candset);
					} else {
						// 既存の候補に追加
						candset.add(candsetid);
					}
					maxids.add(candsetid);
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to select thesaurus max from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		
		//--- release map
		wordmap.clear();
		wordmap = null;
		
		// 新しいデータ代数集合に格納
		cursor = null;
		MongoDtAlgeSet retset = new MongoDtAlgeSet(_mongo_session);
		try {
			cursor = _mongo_vals_col.find(Filters.in(MongoDelegateDocDtAlgeSetElem.MONGO_KEY_SETID, maxids))
									.projection(MongoUtil.MONGO_FILTER_WITHOUT_OBJID)	// "_id" は除外
									.iterator();
			while (cursor.hasNext()) {
				// "_id" を除くドキュメントを、新しいデータ代数集合に追加
				retset._mongo_vals_col.insertOne(cursor.next());
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to select thesaurus max from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		return retset;
	}
	
	/**
	 * 指定された基底に関連付けられている値のうち、指定されたシソーラス定義において
	 * 比較不可能な極小値のみを含むデータ代数元を取り出す。
	 * <p>このメソッドは、指定された基底に関連付けられている値をすべて比較し、
	 * シソーラス定義において比較可能な値の中から最小値のみを含むデータ代数元を取り出す。
	 * 取り出されたデータ代数元に含まれる値は、それぞれ比較不可能もしくは同値となっている。
	 * @param base	判定する値が関連付けられているデータ代数基底
	 * @param thes	シソーラス定義
	 * @return	比較不可能な極小値のみを含むデータ代数元を格納する、新しいデータ代数集合を返す。
	 * 			比較不可能な極小値が存在しない場合は、要素が空のデータ代数集合を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>base</em> が文字列型のデータ代数基底ではない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoDtAlgeSet selectThesaurusMin(DtBase base, IDtStringThesaurus thes) {
		// check
		if (base == null)
			throw new NullPointerException("DtBase argument is null.");
		if (thes == null)
			throw new NullPointerException("DtThesaurus object is null.");
		if (!DtDataTypes.STRING.equals(base.getTypeKey()))
			throw new IllegalArgumentException("DtBase data type is not String.");
		
		if (thes.isEmpty() || this.isEmpty()) {
			//--- No thesaurus relations or No alge elements
			return new MongoDtAlgeSet(_mongo_session);
		}
		// 極小値の元のみを取り出す
		Set<String> maxids = new TreeSet<String>();
		Map<String, Set<String>> wordmap = new HashMap<>();
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = findByBase(base, true, false).iterator();
			while (cursor.hasNext()) {
				Document docElem = cursor.next();
				String value = (String)MongoDelegateDocDtAlgeSetElem.getValueObjectFromDocument(docElem);
				if (value == null || !thes.contains(value)) {
					// 語句がシソーラス定義に存在しない
					continue;
				}
				// シソーラスに定義されている語句を比較
				Iterator<Map.Entry<String,Set<String>>> it = wordmap.entrySet().iterator();
				while (it.hasNext()) {
					// 極大値候補との比較
					Map.Entry<String, Set<String>> entry = it.next();
					int cmp = thes.compare(value, entry.getKey());
					if (cmp > 0) {
						// value > entry.getKey()
						//--- 比較可能な値の中で最小値ではないので、候補に含めない
						value = null;
						break;
					}
					else if (cmp < 0) {
						// value < entry.getKey()
						//--- 比較可能な値の中で最小値のため、候補を除外
						maxids.removeAll(entry.getValue());
						it.remove();
					}
					// else : value == entry.getKey()
					//--- 比較不可能もしくは同値のため、次の候補と比較
				}
				// 語句を候補へ登録
				if (value != null) {
					String candsetid = MongoDelegateDocDtAlgeSetElem.getDtAlgeSetIDfromDocument(docElem);
					Set<String> candset = wordmap.get(value);
					if (candset == null) {
						// 新規候補を追加
						candset = new TreeSet<String>();
						candset.add(candsetid);
						wordmap.put(value, candset);
					} else {
						// 既存の候補に追加
						candset.add(candsetid);
					}
					maxids.add(candsetid);
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to select thesaurus max from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		
		//--- release map
		wordmap.clear();
		wordmap = null;
		
		// 新しいデータ代数集合に格納
		cursor = null;
		MongoDtAlgeSet retset = new MongoDtAlgeSet(_mongo_session);
		try {
			cursor = _mongo_vals_col.find(Filters.in(MongoDelegateDocDtAlgeSetElem.MONGO_KEY_SETID, maxids))
									.projection(MongoUtil.MONGO_FILTER_WITHOUT_OBJID)	// "_id" は除外
									.iterator();
			while (cursor.hasNext()) {
				// "_id" を除くドキュメントを、新しいデータ代数集合に追加
				retset._mongo_vals_col.insertOne(cursor.next());
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to select thesaurus max from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		return retset;
	}

	// データ代数の値のためのユーティリティクラス
	static protected class DtValue
	{
		private final Object _value;
		public DtValue(Object value) {
			if (value instanceof java.math.BigDecimal) {
				java.math.BigDecimal bdval = (java.math.BigDecimal)value;
				if (java.math.BigDecimal.ZERO.compareTo(bdval)==0) {
					_value = java.math.BigDecimal.ZERO;
				} else {
					_value = bdval.stripTrailingZeros();
				}
			} else {
				_value = value;
			}
		}
		@Override
		public int hashCode() {
			return (_value==null ? 0 : _value.hashCode());
		}
		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}

			if (obj instanceof DtValue) {
				Object aValue = ((DtValue)obj)._value;
				if (aValue == null) {
					return (this._value == null);
				} else {
					return (aValue.equals(this._value));
				}
			}

			return false;
		}
	}
	
	/**
	 * 名前付きシソーラス定義に基き、指定されたグループ内でのシソーラス極大値を持つデータ代数集合を抽出する。
	 * @param baseForGroup		グループとする値を示すデータ代数基底
	 * @param baseForTarget		極大値かどうかを判定する値を示すデータ台数基底
	 * @param baseForThesName	シソーラス名とする値を示すデータ台数基底
	 * @param thesmap			名前付きシソーラス定義
	 * @return	比較不可能な極大値のみを含むデータ代数元を格納する、新しいデータ代数集合を返す。
	 * 			比較不可能な極大値が存在しない場合は、要素が空のデータ代数集合を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>baseForTarget</em> または <em>baseForThesName</em> データ代数基底が文字列型ではない場合
	 */
	public MongoDtAlgeSet selectThesaurusMaxWithGroup(DtBase baseForGroup, DtBase baseForTarget, DtBase baseForThesName,
													MongoDtNamedStringThesaurus thesmap)
	{
		// check
		if (baseForGroup == null)
			throw new NullPointerException("'baseForGroup' argument is null.");
		if (baseForTarget == null)
			throw new NullPointerException("'baseForTarget' argument is null.");
		if (baseForThesName == null)
			throw new NullPointerException("'baseForThesName' argument is null.");
		if (thesmap == null)
			throw new NullPointerException("MongoDtNamedStringThesaurus object is null.");
		if (!dtalge.util.DtDataTypes.STRING.equals(baseForTarget.getTypeKey()))
			throw new IllegalArgumentException("'baseForTarget' DtBase data type is not String.");
		if (!dtalge.util.DtDataTypes.STRING.equals(baseForThesName.getTypeKey()))
			throw new IllegalArgumentException("'baseForThesName' DtBase data type is not String.");
		
		if (thesmap.isEmpty() || this.isEmpty()) {
			//--- No thesaurus entries or No alge alements
			return new MongoDtAlgeSet(_mongo_session);
		}
		
		// グループごとの極大値の元のみを取り出す
		Set<String> maxids = new TreeSet<String>();
		java.util.Map<DtValue, java.util.Map<String,Set<String>>> groupmap = new java.util.HashMap<DtValue, Map<String,Set<String>>>();
		//Map<String, Set<String>> wordmap = new HashMap<>();
		BigIterator<MongoDtalge> algeit = iterator();
		try {
			while (algeit.hasNext()) {
				MongoDtalge alge = algeit.next();
				//--- get and check thesaurus by name
				String thesname = null;
				if (alge.containsBase(baseForThesName)) {
					thesname = alge.getString(baseForThesName);
				}
				MongoDtStringThesaurus thes = thesmap.getThesaurus(thesname);
				if (thes == null) {
					// シソーラスが存在しない
					continue;
				}
				//--- check baseTarget contains
				if (!alge.containsBase(baseForTarget)) {
					// 基底が存在しない
					continue;
				}
				//--- check string value contains in thesaurus
				String value = alge.getString(baseForTarget);
				if (value == null || !thes.contains(value)) {
					// 語句がシソーラス定義に存在しない
					continue;
				}
				//--- get wordmap by group key
				DtValue groupkey;
				if (alge.containsBase(baseForGroup)) {
					groupkey = new DtValue(alge.get(baseForGroup));
				} else {
					groupkey = null;
				}
				java.util.Map<String,java.util.Set<String>> wordmap = groupmap.get(groupkey);
				if (wordmap == null) {
					wordmap = new java.util.HashMap<String,java.util.Set<String>>();
					groupmap.put(groupkey, wordmap);
				}
				// シソーラスに定義されている語句を比較
				java.util.Iterator<java.util.Map.Entry<String, java.util.Set<String>>> it = wordmap.entrySet().iterator();
				while (it.hasNext()) {
					// 極大値候補との比較
					java.util.Map.Entry<String, java.util.Set<String>> entry = it.next();
					int cmp = thes.compare(value, entry.getKey());
					if (cmp < 0) {
						// value < entry.getKey()
						//--- 比較可能な値の中で最大値ではないので、候補に含めない
						value = null;
						break;
					}
					else if (cmp > 0) {
						// value > entry.getKey()
						//--- 比較可能な値の中で最大値のため、候補を除外
						maxids.removeAll(entry.getValue());
						it.remove();
					}
					// else : value == entry.getKey()
					//--- 比較不可能もしくは同値のため、次の候補と比較
				}
				// 語句を候補へ登録
				if (value != null) {
					String candsetid = alge.getElementIdOfAlgeSet();
					Set<String> candset = wordmap.get(value);
					if (candset == null) {
						// 新規候補を追加
						candset = new TreeSet<String>();
						candset.add(candsetid);
						wordmap.put(value, candset);
					} else {
						// 既存の候補に追加
						candset.add(candsetid);
					}
					maxids.add(candsetid);
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to select thesaurus max with group & thesaurus name from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			algeit.closeCursor();
		}
		//--- release map
		groupmap.clear();
		groupmap = null;

		// 新しいデータ代数集合に格納
		MongoCursor<? extends Document> cursor = null;
		MongoDtAlgeSet retset = new MongoDtAlgeSet(_mongo_session);
		try {
			cursor = _mongo_vals_col.find(Filters.in(MongoDelegateDocDtAlgeSetElem.MONGO_KEY_SETID, maxids))
									.projection(MongoUtil.MONGO_FILTER_WITHOUT_OBJID)	// "_id" は除外
									.iterator();
			while (cursor.hasNext()) {
				// "_id" を除くドキュメントを、新しいデータ代数集合に追加
				retset._mongo_vals_col.insertOne(cursor.next());
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to select thesaurus max with group & thesaurus name from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		return retset;
	}

	/**
	 * 指定された基底の値で要素が並べ替えられた、新しいデータ代数集合を返す。
	 * 値の並べ替えにおいて、等しい値の順序は変更されない。
	 * <tt>null</tt> 値はどの値よりも小さい値として並べ替えられる。
	 * また、基底が存在しない要素は、<tt>null</tt> 値よりも小さい値として並べ替えられる。
	 * @param base			並べ替えのキーとする値が関連付けられているデータ代数基底
	 * @param ascending		昇順にソートする場合は <tt>true</tt>、降順にソートする場合は <tt>false</tt>
	 * @return	要素が並べ替えられた、新しいデータ代数集合
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 * @throws ClassCastException		基底に関連付けられた値が(<code>Comparable</code> インタフェースを実装していない)比較不可能の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoDtAlgeSet sortedAlgesByValue(DtBase base, boolean ascending) {
		Validations.validNotNull(base);
		MongoDtAlgeSet newset = copy();
		newset.sortAlgesByValue(base, ascending);
		return newset;
	}

	/**
	 * 指定された基底の値で要素が並べ替えられる。
	 * 値の並べ替えにおいて、等しい値の順序は変更されない。
	 * <tt>null</tt> 値はどの値よりも小さい値として並べ替えられる。
	 * また、基底が存在しない要素は、<tt>null</tt> 値よりも小さい値として並べ替えられる。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このメソッドは、自身の内容を書き換える、破壊型メソッドである。
	 * </blockquote>
	 * @param base			並べ替えのキーとする値が関連付けられているデータ代数基底
	 * @param ascending		昇順にソートする場合は <tt>true</tt>、降順にソートする場合は <tt>false</tt>
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 * @throws ClassCastException		基底に関連付けられた値が(<code>Comparable</code> インタフェースを実装していない)比較不可能の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public void sortAlgesByValue(DtBase base, boolean ascending) {
		if (base == null)
			throw new NullPointerException();
		
		// create aggregation
		BsonField accumValue;
		Bson sortOrder;
		if (ascending) {
			accumValue = Accumulators.min("elem_value", MongoDelegateDocDtAlgeSetElem.MONGO_KEY_$VALUE);
			sortOrder = Sorts.ascending("elem_value");
		} else {
			accumValue = Accumulators.max("elem_value", MongoDelegateDocDtAlgeSetElem.MONGO_KEY_$VALUE);
			sortOrder = Sorts.descending("elem_value");
		}
		ArrayList<Bson> aggre = new ArrayList<>();
		aggre.add(Aggregates.match(Filters.and(makeQueryByDtBase(base), makeQueryWithoutValue(null))));
		aggre.add(Aggregates.group(MongoDelegateDocDtAlgeSetElem.MONGO_KEY_$SETID, accumValue));
		aggre.add(Aggregates.project(new Document(MongoDelegateDocDtAlgeSetElem.MONGO_KEY_SETID, "$_id").append("_id", 0).append("elem_value", 1)));
		aggre.add(Aggregates.sort(sortOrder));

		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = _mongo_vals_col.aggregate(aggre).iterator();
			while (cursor.hasNext()) {
				String orgsetid = MongoDelegateDocDtAlgeSetElem.getDtAlgeSetIDfromDocument(cursor.next());
				String newsetid = MongoUtil.makeUniqueAlgeSetElemID();
				Bson query = makeQueryBySetId(orgsetid);
				MongoCursor<? extends Document> elemcursor = queryDocuments(query, true, false).iterator();
				try {
					while (elemcursor.hasNext()) {
						Document docElem = elemcursor.next();
						docElem.put(MongoDelegateDocDtAlgeSetElem.MONGO_KEY_SETID, newsetid);
						_mongo_vals_col.insertOne(docElem);
						_modCount.incrementAndGet();
					}
					_mongo_vals_col.deleteMany(query);
				}
				finally {
					MongoUtil.closeCursorSilent(elemcursor);
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to sort this by value of the DtBase in MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
	}
	
//	static protected class AlgesComparator implements Comparator<Dtalge>
//	{
//		protected final DtBase	_base;
//		protected final boolean	_ascending;
//		
//		public AlgesComparator(final DtBase base, final boolean ascending) {
//			this._base = base;
//			this._ascending = ascending;
//		}
//
//		@SuppressWarnings("unchecked")
//		public int compare(Dtalge alge1, Dtalge alge2) {
//			if (alge1 == alge2) {
//				return 0;	// same
//			}
//			if (alge1 == null) {
//				return (_ascending ? -1 : 1);	// alge1(null) < alge2(not null)
//			}
//			if (alge2 == null) {
//				return (_ascending ? 1 : -1);		// alge1(not null) > alge2(null)
//			}
//
//			boolean existBase1 = alge1.containsBase(_base);
//			boolean existBase2 = alge2.containsBase(_base);
//			if (!existBase1) {
//				if (!existBase2) {
//					return 0;	// alge1(no base) == alge2(no base)
//				} else {
//					return (_ascending ? -1 : 1);	// alge1(no base) < alge2(has base)
//				}
//			}
//			else if (!existBase2) {
//				return (_ascending ? 1 : -1);		// alge1(has base) > alge2(no base)
//			}
//			
//			Object value1 = alge1.get(_base);
//			Object value2 = alge2.get(_base);
//			if (value1 == value2) {
//				return 0;	// same values
//			}
//			if (value1 == null) {
//				return (_ascending ? -1 : 1);	// alge1(null value) < alge2(instanced value)
//			}
//			else if (value2 == null) {
//				return (_ascending ? 1 : -1);		// alge1(instanced value) > alge2(null value)
//			}
//			else {
//				if (_ascending) {
//					return ((Comparable)value1).compareTo(value2);
//				} else {
//					return ((Comparable)value2).compareTo(value1);
//				}
//			}
//		}
//	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 指定された基底に割り当てられている値のうち、<tt>null</tt> ではない最小値を取得する。
	 * 比較不可能な値の場合や、指定された基底をもつデータ代数元が一つも存在しない場合、
	 * このメソッドは <tt>null</tt> を返す。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このメソッドは、引数の正当性をチェックしない。
	 * </blockquote>
	 * @param base	比較する値のデータ代数基底
	 * @return	指定された基底に割り当てられている最小値を返す。
	 * 			最小値が取得できない場合は <tt>null</tt> を返す。
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	protected Object getMinValue(DtBase base) {
		if (this.isEmpty())
			return null;

		try {
			Document docElem = queryDocuments(Filters.and(makeQueryByDtBase(base), makeQueryWithoutValue(null)), true, false)
										.sort(Sorts.ascending(MongoDelegateDocDtAlgeSetElem.MONGO_KEY_VALUE))
										.first();
			return (docElem==null ? null : MongoDelegateDocDtAlgeSetElem.getValueObjectFromDocument(docElem));
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get minimum value by DtBase from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
	}
	
	/**
	 * 指定された基底に割り当てられている値のうち、<tt>null</tt> ではない最大値を取得する。
	 * 比較不可能な値の場合や、指定された基底をもつデータ代数元が一つも存在しない場合、
	 * このメソッドは <tt>null</tt> を返す。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このメソッドは、引数の正当性をチェックしない。
	 * </blockquote>
	 * @param base	比較する値のデータ代数基底
	 * @return	指定された基底に割り当てられている最大値を返す。
	 * 			最大値が取得できない場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	protected Object getMaxValue(DtBase base) {
		if (this.isEmpty())
			return null;

		try {
			Document docElem = queryDocuments(Filters.and(makeQueryByDtBase(base), makeQueryWithoutValue(null)), true, false)
										.sort(Sorts.descending(MongoDelegateDocDtAlgeSetElem.MONGO_KEY_VALUE))
										.first();
			return (docElem==null ? null : MongoDelegateDocDtAlgeSetElem.getValueObjectFromDocument(docElem));
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get minimum value by DtBase from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
	}

	//------------------------------------------------------------
	// for I/O
	//------------------------------------------------------------
	
	/**
	 * データ代数集合の内容を、指定のファイルにテーブル形式の CSV フォーマットで出力する。
	 * <p>
	 * 基底のない要素のフィールドは空欄となる。
	 * <tt>null</tt> の場合は <tt>null</tt> を示す特殊値が出力される。
	 * 特殊記号で始まる値は、特殊記号でエスケープされる。
	 * <br>出力時は、このインスタンスに格納されている基底の順序で出力される。
	 * 
	 * @param csvFile 出力先ファイル
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public void toTableCSV(File csvFile)
		throws IOException, FileNotFoundException
	{
//		CsvWriter writer = new CsvWriter(csvFile);
//		try {
//			writeToTableCsv(writer);
//		}
//		finally {
//			writer.close();
//		}
		//--- 順序はオリジナル、nullは出力する
		toTableCSV(null, false, csvFile);
	}
	
	/**
	 * データ代数集合の内容を、指定された文字セットで指定のファイルにテーブル形式の CSV フォーマットで出力する。
	 * <p>
	 * 基底のない要素のフィールドは空欄となる。
	 * <tt>null</tt> の場合は <tt>null</tt> を示す特殊値が出力される。
	 * 特殊記号で始まる値は、特殊記号でエスケープされる。
	 * <br>出力時は、このインスタンスに格納されている基底の順序で出力される。
	 * 
	 * @param csvFile 出力先ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public void toTableCSV(File csvFile, String charsetName)
		throws IOException, FileNotFoundException, UnsupportedEncodingException
	{
//		CsvWriter writer = new CsvWriter(csvFile, charsetName);
//		try {
//			writeToTableCsv(writer);
//		}
//		finally {
//			writer.close();
//		}
		//--- 順序はオリジナル、nullは出力する
		toTableCSV(null, false, csvFile, charsetName);
	}
	
	/**
	 * データ代数集合の内容を、指定のファイルにテーブル形式の CSV フォーマットで出力する。
	 * <p>
	 * 基底のない要素のフィールドは空欄となる。
	 * <tt>null</tt> の場合は <tt>null</tt> を示す特殊値が出力される。
	 * 特殊記号で始まる値は、特殊記号でエスケープされる。
	 * <br>順序指定用基底集合が指定された場合、その基底集合の順序の通りに基底が出力される。
	 * 順序指定用基底集合に含まれない基底は、行の終端にオリジナルの順序で出力される。
	 * 順序が指定されていない場合は、このインスタンスに格納されている基底の順序で出力される。
	 * 
	 * @param baseOrder	順序指定用基底集合。順序を指定しない場合は <tt>null</tt>
	 * @param csvFile 出力先ファイル
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public void toTableCsvWithBaseOrder(DtBaseSet baseOrder, File csvFile)
		throws IOException, FileNotFoundException
	{
		toTableCSV(baseOrder, false, csvFile);
	}
	
	/**
	 * データ代数集合の内容を、指定された文字セットで指定のファイルにテーブル形式の CSV フォーマットで出力する。
	 * <p>
	 * 基底のない要素のフィールドは空欄となる。
	 * <tt>null</tt> の場合は <tt>null</tt> を示す特殊値が出力される。
	 * 特殊記号で始まる値は、特殊記号でエスケープされる。
	 * <br>順序指定用基底集合が指定された場合、その基底集合の順序の通りに基底が出力される。
	 * 順序指定用基底集合に含まれない基底は、行の終端にオリジナルの順序で出力される。
	 * 順序が指定されていない場合は、このインスタンスに格納されている基底の順序で出力される。
	 * 
	 * @param baseOrder	順序指定用基底集合。順序を指定しない場合は <tt>null</tt>
	 * @param csvFile 出力先ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public void toTableCsvWithBaseOrder(DtBaseSet baseOrder, File csvFile, String charsetName)
		throws IOException, FileNotFoundException, UnsupportedEncodingException
	{
		toTableCSV(baseOrder, false, csvFile, charsetName);
	}
	
	/**
	 * データ代数集合の内容を、指定のファイルにテーブル形式の CSV フォーマットで出力する。
	 * <p>
	 * 基底のない要素のフィールドは空欄となる。また、基底に対応する値が <tt>null</tt> もしくは
	 * 空文字列の場合、その値と基底を削除してから出力する。
	 * 特殊記号で始まる値は、特殊記号でエスケープされる。
	 * <br>出力時は、このインスタンスに格納されている基底の順序で出力される。
	 * 
	 * @param csvFile 出力先ファイル
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public void toTableCsvWithoutNull(File csvFile)
		throws IOException, FileNotFoundException
	{
		toTableCSV(null, true, csvFile);
	}
	
	/**
	 * データ代数集合の内容を、指定された文字セットで指定のファイルにテーブル形式の CSV フォーマットで出力する。
	 * <p>
	 * 基底のない要素のフィールドは空欄となる。また、基底に対応する値が <tt>null</tt> もしくは
	 * 空文字列の場合、その値と基底を削除してから出力する。
	 * 特殊記号で始まる値は、特殊記号でエスケープされる。
	 * <br>出力時は、このインスタンスに格納されている基底の順序で出力される。
	 * 
	 * @param csvFile 出力先ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public void toTableCsvWithoutNull(File csvFile, String charsetName)
		throws IOException, FileNotFoundException, UnsupportedEncodingException
	{
		toTableCSV(null, true, csvFile, charsetName);
	}
	
	/**
	 * データ代数集合の内容を、指定のファイルにテーブル形式の CSV フォーマットで出力する。
	 * <p>
	 * 基底のない要素のフィールドは空欄となる。
	 * <tt>null</tt> の場合は <tt>null</tt> を示す特殊値が出力される。
	 * 特殊記号で始まる値は、特殊記号でエスケープされる。
	 * <br>順序指定用基底集合が指定された場合、その基底集合の順序の通りに基底が出力される。
	 * 順序指定用基底集合に含まれない基底は、行の終端にオリジナルの順序で出力される。
	 * 順序が指定されていない場合は、このインスタンスに格納されている基底の順序で出力される。
	 * <br><em>withoutNull</em> に <tt>true</tt> を指定した場合、<tt>null</tt> 値は出力せず、
	 * フィールドは空欄となる。
	 * 
	 * @param orderedBases	順序指定用基底集合。順序を指定しない場合は <tt>null</tt>
	 * @param withoutNull	<tt>null</tt> を出力しない場合は <tt>true</tt>
	 * @param csvFile 出力先ファイル
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public void toTableCSV(DtBaseSet orderedBases, boolean withoutNull, File csvFile)
		throws IOException, FileNotFoundException
	{
		CsvWriter writer = new CsvWriter(csvFile);
		try {
			writeToTableCsv_v2(writer, orderedBases, withoutNull);
		}
		finally {
			writer.close();
		}
	}
	
	/**
	 * データ代数集合の内容を、指定された文字セットで指定のファイルにテーブル形式の CSV フォーマットで出力する。
	 * <p>
	 * 基底のない要素のフィールドは空欄となる。
	 * <tt>null</tt> の場合は <tt>null</tt> を示す特殊値が出力される。
	 * 特殊記号で始まる値は、特殊記号でエスケープされる。
	 * <br>順序指定用基底集合が指定された場合、その基底集合の順序の通りに基底が出力される。
	 * 順序指定用基底集合に含まれない基底は、行の終端にオリジナルの順序で出力される。
	 * 順序が指定されていない場合は、このインスタンスに格納されている基底の順序で出力される。
	 * <br><em>withoutNull</em> に <tt>true</tt> を指定した場合、<tt>null</tt> 値は出力せず、
	 * フィールドは空欄となる。
	 * 
	 * @param orderedBases	順序指定用基底集合。順序を指定しない場合は <tt>null</tt>
	 * @param withoutNull	<tt>null</tt> を出力しない場合は <tt>true</tt>
	 * @param csvFile 出力先ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public void toTableCSV(DtBaseSet orderedBases, boolean withoutNull, File csvFile, String charsetName)
		throws IOException, FileNotFoundException, UnsupportedEncodingException
	{
		CsvWriter writer = new CsvWriter(csvFile, charsetName);
		try {
			writeToTableCsv_v2(writer, orderedBases, withoutNull);
		}
		finally {
			writer.close();
		}
	}
	
	/**
	 * データ代数集合の内容を、指定のファイルに CSV フォーマットで出力する。
	 * <p>
	 * 出力時は、このインスタンスに格納されている要素の順序で出力される。
	 * 
	 * @param csvFile 出力先ファイル
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public void toCSV(File csvFile)
		throws IOException, FileNotFoundException
	{
		CsvWriter writer = new CsvWriter(csvFile);
		try {
			writeToCsv_v2(writer);
		}
		finally {
			writer.close();
		}
	}
	
	/**
	 * データ代数集合の内容を、指定された文字セットで指定のファイルに CSV フォーマットで出力する。
	 * <p>
	 * 出力時は、このインスタンスに格納されている要素の順序で出力される。
	 * 
	 * @param csvFile 出力先ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public void toCSV(File csvFile, String charsetName)
		throws IOException, FileNotFoundException, UnsupportedEncodingException
	{
		CsvWriter writer = new CsvWriter(csvFile, charsetName);
		try {
			writeToCsv_v2(writer);
		}
		finally {
			writer.close();
		}
	}

	/**
	 * CSV フォーマットのファイルを読み込み、このオブジェクトに追加する。
	 * @param csvFile	読み込む CSV ファイル
	 * @return	このデータ代数集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws UnsupportedOperationException	このオブジェクトがビューの場合
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean addAllFromCSV(File csvFile)
		throws IOException, FileNotFoundException, CsvFormatException
	{
		CsvReader reader = new CsvReader(csvFile);
		try {
			return readFromCsv(reader);
		}
		finally {
			reader.close();
		}
	}
	
	/**
	 * 指定された文字セットで CSV フォーマットのファイルを読み込み、このオブジェクトに追加する。
	 * @param csvFile	読み込む CSV ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * @return	このデータ代数集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws UnsupportedOperationException	このオブジェクトがビューの場合
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean addAllFromCSV(File csvFile, String charsetName)
		throws IOException, FileNotFoundException, CsvFormatException, UnsupportedEncodingException
	{
		CsvReader reader = new CsvReader(csvFile, charsetName);
		try {
			return readFromCsv(reader);
		}
		finally {
			reader.close();
		}
	}
	
	/**
	 * CSV フォーマットのファイルを読み込み、新しいデータ代数集合を生成する。
	 * <p>
	 * 基本的に、ファイルに記述された要素の順序で、新しいインスタンスに格納される。
	 * 
	 * @param session	MongoDB セッションオブジェクト
	 * @param csvFile 読み込む CSV ファイル
	 * 
	 * @return ファイルの内容で生成された、新しい <code>MongoDtAlgeSet</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	static public MongoDtAlgeSet fromCSV(MongoSession session, File csvFile)
		throws IOException, FileNotFoundException, CsvFormatException
	{
		MongoDtAlgeSet newAlge = new MongoDtAlgeSet(session);
		newAlge.addAllFromCSV(csvFile);
		return newAlge;
	}
	
	/**
	 * 指定された文字セットで CSV フォーマットのファイルを読み込み、新しいデータ代数集合を生成する。
	 * <p>
	 * 基本的に、ファイルに記述された要素の順序で、新しいインスタンスに格納される。
	 * 
	 * @param session	MongoDB セッションオブジェクト
	 * @param csvFile 読み込む CSV ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @return ファイルの内容で生成された、新しい <code>MongoDtAlgeSet</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	static public MongoDtAlgeSet fromCSV(MongoSession session, File csvFile, String charsetName)
		throws IOException, FileNotFoundException, CsvFormatException, UnsupportedEncodingException
	{
		MongoDtAlgeSet newAlge = new MongoDtAlgeSet(session);
		newAlge.addAllFromCSV(csvFile, charsetName);
		return newAlge;
	}

	//------------------------------------------------------------
	// Internal methods for I/O
	//------------------------------------------------------------
	
	/**
	 * CSVフォーマットで <code>BigDtAlgeSetInnerElement</code> の内容を出力する。
	 * <tt>null</tt> の場合は 空文字を出力する。
	 * 特殊記号で始まる値は、特殊記号でエスケープする。
	 * 
	 * @param writer	CSVファイル出力オブジェクト
	 * @param base		出力する基底
	 * @param value		出力する値
	 * 
	 * @throws	IOException	入出力エラーが発生した場合
	 */
	protected void writeInnerElemToCsv_v2(CsvWriter writer, DtBase base, Object value)
		throws IOException
	{
		//--- value
		if (value == null) {
			// null 値は、空文字とする
			writer.writeField(Dtalge.CSV_VALUE_EMPTY);
		}
		else {
			String strValue;
			if (value instanceof BigDecimal)
				strValue = ((BigDecimal)value).stripTrailingZeros().toPlainString();
			else
				strValue = value.toString();
			if (strValue.startsWith(Dtalge.CSV_COMMAND_PREFIX)) {
				// 特殊記号で始まるものは、特殊記号でエスケープ
				writer.writeField(Dtalge.CSV_COMMAND_PREFIX.concat(strValue));
			}
			else {
				// 通常の文字列
				writer.writeField(strValue);
			}
		}
		//--- base
		base.writeFieldToCSV(writer);
		//--- new line
		writer.newLine();
	}

	/**
	 * CSVフォーマットで データ代数集合の内容を出力する。
	 * <tt>null</tt> の場合は 空文字を出力する。
	 * 特殊記号で始まる値は、特殊記号でエスケープする。
	 * 
	 * @param writer	CSVファイル出力オブジェクト
	 * 
	 * @throws	IOException	入出力エラーが発生した場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	protected void writeToCsv_v2(CsvWriter writer)
		throws IOException
	{
		// キーワード出力
		writer.writeLine(Dtalge.CSV_KEYWORD_V2);
		// データ出力
		String last_setid = null;
		MongoCursor<? extends Document> cursor = null;
		try {
			//--- データ代数元ごとに出力するので、データ代数元 ID も取得する
			cursor = sortedDocuments(true, false).iterator();
			//--- 先頭エントリ
			if (cursor.hasNext()) {
				Document docElem = cursor.next();
				last_setid   = MongoDelegateDocDtAlgeSetElem.getDtAlgeSetIDfromDocument(docElem);
				DtBase base  = MongoDelegateDocDtAlgeSetElem.toDtBaseFromDocument(docElem);
				Object value = MongoDelegateDocDtAlgeSetElem.getValueObjectFromDocument(docElem);
				writeInnerElemToCsv_v2(writer, base, value);
			}
			//--- 2番目以降のエントリ
			while (cursor.hasNext()) {
				Document docElem = cursor.next();
				String setid = MongoDelegateDocDtAlgeSetElem.getDtAlgeSetIDfromDocument(docElem);
				DtBase base  = MongoDelegateDocDtAlgeSetElem.toDtBaseFromDocument(docElem);
				Object value = MongoDelegateDocDtAlgeSetElem.getValueObjectFromDocument(docElem);
				if (!setid.equals(last_setid)) {
					// 元の区切り(空行)を挿入
					writer.writeBlankLine();
					last_setid = setid;
				}
				// 元の要素を出力
				writeInnerElemToCsv_v2(writer, base, value);
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to write to CSV file from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		writer.flush();
	}
	
	/**
	 * テーブル形式のCSVフォーマットでデータ代数集合の要素の値のみを出力する。
	 * @param writer	CSVファイル出力オブジェクト
	 * @param value		出力する値
	 * @param nullKeyword	<tt>null</tt> を表す特殊値
	 */
	protected void writeValueFieldToTableCsv_v2(CsvWriter writer, Object value, String nullKeyword)
		throws IOException
	{
		if (value == null) {
			// null 値
			writer.writeField(nullKeyword);
		}
		else {
			String strValue;
			if (value instanceof BigDecimal)
				strValue = ((BigDecimal)value).stripTrailingZeros().toPlainString();
			else
				strValue = value.toString();
			if (strValue.length() <= 0) {
				// 空文字列は null 値とする
				writer.writeField(nullKeyword);
			}
			else if (strValue.startsWith(Dtalge.CSV_COMMAND_PREFIX)) {
				// 特殊記号で始まるものは、特殊記号でエスケープ
				writer.writeField(Dtalge.CSV_COMMAND_PREFIX.concat(strValue));
			}
			else {
				// 通常の文字列
				writer.writeField(strValue);
			}
		}
	}

	/**
	 * テーブル形式のCSVフォーマットでデータ代数集合の内容を出力する。
	 * 基底のない要素は空文字をフィールドに出力し、
	 * <tt>null</tt> の場合は <tt>null</tt> を示す特殊値を出力する。
	 * 特殊記号で始まる値は、特殊記号でエスケープする。
	 * <br>順序指定用基底集合が指定された場合、その基底集合の順序の通りに基底を出力する。
	 * 順序指定用基底集合に含まれない基底は、行の終端にオリジナルの順序で出力する。
	 * <br><em>withoutNull</em> に <tt>true</tt> を指定した場合、<tt>null</tt> 値は出力せず、
	 * フィールドは空欄となる。
	 * 
	 * @param writer	CSVファイル出力オブジェクト
	 * @param orderedBases	順序指定用基底集合。順序を指定しない場合は <tt>null</tt>
	 * @param withoutNull	<tt>null</tt> を出力しない場合は <tt>true</tt>
	 * @throws IOException	入出力エラーが発生した場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	protected void writeToTableCsv_v2(CsvWriter writer, DtBaseSet orderedBases, boolean withoutNull)
		throws IOException
	{
		// キーワード出力
		writer.writeLine(Dtalge.CSV_TABLE_KEYWORD_V2);
		
		// 順序基底集合の生成
		boolean hasOrder = (orderedBases != null && !orderedBases.isEmpty());
		ArrayList<DtBase> allBases = new ArrayList<>();
		if (hasOrder) {
			for (DtBase base : orderedBases) {
				allBases.add(base);
			}
		}
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = distinctByDtBase().iterator();
			if (hasOrder) {
				while (cursor.hasNext()) {
					Document docDtBaseElem = cursor.next();
					DtBase base = MongoDelegateDocDtBase.toDtBase(docDtBaseElem);
					if (!orderedBases.contains(base)) {
						allBases.add(base);
					}
				}
			}
			else {
				while (cursor.hasNext()) {
					Document docDtBaseElem = cursor.next();
					DtBase base = MongoDelegateDocDtBase.toDtBase(docDtBaseElem);
					allBases.add(base);
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to write to Table-CSV file from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
			cursor = null;
		}
		if (allBases.isEmpty()) {
			// 出力なし
			writer.flush();
			return;
		}
		
		// 全基底の出力
		Dtalge.writeBasesToTableCsv(writer, allBases);
		
		// データ出力
		String nullKeyword = (withoutNull ? Dtalge.CSV_VALUE_EMPTY : Dtalge.CSV_COMMAND_NULL);
		try {
			//--- データ代数元 ID のみを重複なしに昇順ソートしたカーソル
			cursor = _mongo_vals_col.aggregate(MONGO_AGGRE_ELEM_ITERABLE).iterator();
			while (cursor.hasNext()) {
				String setid = MongoDelegateDocDtAlgeSetElem.getDtAlgeSetIDfromDocument(cursor.next());
				//--- このデータ代数元を 1 行に出力
				for (DtBase base : allBases) {
					Document docElem = findOneBySetIdAndBase(setid, base, true, true);
					if (docElem != null) {
						// 基底あり
						writeValueFieldToTableCsv_v2(writer, MongoDelegateDocDtAlgeSetElem.getValueObjectFromDocument(docElem), nullKeyword);
					}
					else {
						// 基底なし
						writer.writeField(Dtalge.CSV_VALUE_EMPTY);	// 基底なしは空文字を出力
					}
				}
				//--- 改行(データ代数元区切り)
				writer.newLine();
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to write to Table-CSV file from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		
		// flush
		writer.flush();
	}

	/**
	 * CSVフォーマットで、データ代数集合の内容を読み込む。
	 * 
	 * @param reader	CSVファイル入力オブジェクト
	 * @return	データ代数集合の内容が変更された場合は <tt>true</tt>
	 * @throws IOException	入出力エラーが発生した場合
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 */
	protected boolean readFromCsv(CsvReader reader)
		throws IOException, CsvFormatException
	{
		boolean modified = false;
		CsvReader.CsvRecord record;
		Dtalge.CsvFileType csvType = null;
		
		// 先頭１行目のキーワードをチェック
		record = reader.readRecord();
		if (record == null || !record.hasFields()) {
			// undefined Dtalge CSV ID
			throw new CsvFormatException("DtAlgeSet file ID not found.");
		} else {
			CsvReader.CsvField field = record.getField(0);
			if (field == null) {
				throw new CsvFormatException("Illegal DtAlgeSet file ID.", record.getLineNo(), 1);
			}
			String strFieldValue = field.getValue();
			csvType = Dtalge.CsvFileType.fromCsvKeyword(strFieldValue);
			if (csvType == null) {
				throw new CsvFormatException("Illegal DtAlgeSet field ID.", record.getLineNo(), 1);
			}
			// 以降のフィールドは無視
		}

		// データ読み込み
		String newSetId = MongoUtil.makeUniqueAlgeSetElemID();
		if (csvType.isTable()) {
			// テーブル形式のCSV読み込み
			List<DtBase> baselist = Dtalge.readBasesFromTableCsv(reader);
			if (baselist != null) {
				if (csvType.version() == Dtalge.CsvFileType.V2) {
					//--- テーブル形式 v2
					while ((record = reader.readRecord()) != null) {
						if (readValuesFromTableCsv_v2(record, baselist, newSetId)) {
							modified = true;
						}
						newSetId = MongoUtil.makeUniqueAlgeSetElemID();
					}
				} else {
					//--- 初期テーブル形式
					while ((record = reader.readRecord()) != null) {
						if (readValuesFromTableCsv(record, baselist, newSetId)) {
							modified = true;
						}
						newSetId = MongoUtil.makeUniqueAlgeSetElemID();
					}
				}
			}
		}
		else {
			// CSV標準形の読み込み
			if (csvType.version() == Dtalge.CsvFileType.V2) {
				//--- v2
				while ((record = reader.readRecord()) != null) {
					// 空行は元の区切りとする
					if (!record.hasFields() || !record.hasValues()) {
						//--- 次のデータ代数元 ID を生成
						newSetId = MongoUtil.makeUniqueAlgeSetElemID();
						continue;
					}
					
					// レコードを読み込む
					if (readRecordFromCsv_v2(record, newSetId)) {
						modified = true;
					}
				}
			} else {
				//--- 初期形式
				while ((record = reader.readRecord()) != null) {
					// 空行は元の区切りとする
					if (!record.hasFields() || !record.hasValues()) {
						//--- 次のデータ代数元 ID を生成
						newSetId = MongoUtil.makeUniqueAlgeSetElemID();
						continue;
					}
					
					// レコードを読み込む
					if (readRecordFromCsv(record, newSetId)) {
						modified = true;
					}
				}
			}
		}
		return modified;
	}
	
	/**
	 * このデータ代数集合に、新たなデータ代数元要素を追加する。
	 * データ代数元 ID と基底がすでに格納されている場合、値を上書きする。
	 * なお、データ代数の基底に対する値として適切かどうかは判定しない。
	 * @param set_id	データ代数元 ID
	 * @param base		データ代数基底
	 * @param value		値
	 * @return	データ代数集合の内容が変更された場合は <tt>true</tt>
	 */
	protected boolean putValue(String set_id, DtBase base, Object value) {
		Document data = MongoDelegateDocDtAlgeSetElem.makeDtAlgeSetElemDocument(set_id, base, value);
		Document where = new Document();
		where.append(MongoDelegateDocDtAlgeSetElem.MONGO_KEY_SETID, set_id);
		where.append(MongoDelegateDocDtAlgeSetElem.MONGO_KEY_BASE, data.get(MongoDelegateDocDtAlgeSetElem.MONGO_KEY_BASE));
		UpdateResult result = MongoUtil.updateDocument(_mongo_vals_col, where, data, false, true);
		if (result.getUpsertedId() != null) {
			//--- updated
			if (result.getMatchedCount() == 0) {
				//--- inserted
				_modCount.incrementAndGet();
			}
			return true;
		}
		else if (result.getModifiedCount() > 0L) {
			//--- updated
			return true;
		}
		else {
			//--- not modified
			return false;
		}
	}

	/**
	 * CSVフォーマットの1レコードの内容を読み込む。
	 * <br>値のフィールドが空欄の場合、<tt>null</tt> の値として読み込む。
	 * なお、このメソッドでは特殊記号に関する処理は行わない。
	 * 
	 * @param recReader		CSVフォーマットの1レコード入力オブジェクト
	 * @param setid			データ代数元 ID
	 * @return	データ代数集合の内容が変更された場合は <tt>true</tt>
	 * @throws IOException	入出力エラーが発生した場合
	 * @throws CsvFormatException	カラムのデータが正しくない場合
	 */
	protected boolean readRecordFromCsv(CsvReader.CsvRecord record, String setid)
		throws IOException, CsvFormatException
	{
		// setup field reader
		CsvReader.CsvFieldReader freader = new CsvReader.CsvFieldReader(record);
		
		// value
		String strValue = freader.readValue();
		if (Strings.isNullOrEmpty(strValue)) {
			// 長さ 0 の文字列も null とみなす
			strValue = null;
		}
		
		// base
		DtBase newBase = DtBase.readFieldFromCSV(freader);
		
		// データ型の正当性チェック
		Object value = null;
		try {
			value = DtDataTypes.valueOf(newBase.getTypeKey(), strValue);
		}
		catch (Throwable ex) {
			throw new CsvFormatException(ex.getMessage(), freader.getLineNo(), 1);
		}
		
		// データを追加
		return putValue(setid, newBase, value);
	}
	
	/**
	 * CSVフォーマットの1レコードの内容を読み込む。
	 * <br>値のフィールドが空欄の場合、<tt>null</tt> の値として読み込む。
	 * また、特殊記号で始まる値は、特殊値として読み込む。
	 * 
	 * @param recReader		CSVフォーマットの1レコード入力オブジェクト
	 * @param setid			データ代数元 ID
	 * @return	データ代数集合の内容が変更された場合は <tt>true</tt>
	 * @throws IOException	入出力エラーが発生した場合
	 * @throws CsvFormatException	カラムのデータが正しくない場合
	 */
	protected boolean readRecordFromCsv_v2(CsvReader.CsvRecord record, String setid)
		throws IOException, CsvFormatException
	{
		// setup field reader
		CsvReader.CsvFieldReader freader = new CsvReader.CsvFieldReader(record);
		
		// value
		String strValue = freader.readValue();
		int vtype = Dtalge.getCsvValueType(strValue);
		switch (vtype) {
			case Dtalge.CSVVALTYPE_NONE :
				strValue = null;
				break;
			case Dtalge.CSVVALTYPE_ESCAPED :
				strValue = strValue.substring(Dtalge.CSV_COMMAND_PREFIX.length());
				break;
			case Dtalge.CSVVALTYPE_CMD_NULL :
				strValue = null;
				break;
			case Dtalge.CSVVALTYPE_CMD_UNKNOWN :
				throw new CsvFormatException("Undefined keyword : " + strValue, freader.getLineNo(), 1);
			// CSVVALTYPE_STRING :
		}
		
		// base
		DtBase newBase = DtBase.readFieldFromCSV(freader);
		
		// データ型の正当性チェック
		Object value = null;
		try {
			value = DtDataTypes.valueOf(newBase.getTypeKey(), strValue);
		}
		catch (Throwable ex) {
			throw new CsvFormatException(ex.getMessage(), freader.getLineNo(), 1);
		}
		
		// データを追加
		return putValue(setid, newBase, value);
	}

	/**
	 * テーブル形式のCSVフォーマットから、値のレコードを1レコード読み込む。
	 * <br>値のフィールドが空欄の場合、<tt>null</tt> の値として読み込む。
	 * なお、このメソッドでは特殊記号に関する処理は行わない。
	 * @param record	CSVフォーマットの1レコード入力オブジェクト
	 * @param baselist	基底のリスト。フィールドの順序通りに読み込まれた基底が格納されていること。
	 * @param setid			データ代数元 ID
	 * @return	データ代数集合の内容が変更された場合は <tt>true</tt>
	 * @throws IOException	入出力エラーが発生した場合
	 * @throws CsvFormatException	カラムのデータが正しくない場合
	 */
	protected boolean readValuesFromTableCsv(CsvReader.CsvRecord record, List<DtBase> baselist, String setid)
		throws IOException, CsvFormatException
	{
		// setup field reader
		CsvReader.CsvFieldReader freader = new CsvReader.CsvFieldReader(record);
		
		// read values
		boolean modified = false;
		for (DtBase newBase : baselist) {
			// read value
			String strValue = freader.readValue();
			if (Strings.isNullOrEmpty(strValue)) {
				// 長さ 0 の文字列も null とみなす
				strValue = null;
			}
			
			// データ型の正当性チェック
			Object value = null;
			try {
				value = DtDataTypes.valueOf(newBase.getTypeKey(), strValue);
			}
			catch (Throwable ex) {
				throw new CsvFormatException(ex.getMessage(), freader.getLineNo(), freader.getNextPosition());
			}
			
			// データを追加
			if (putValue(setid, newBase, value)) {
				modified = true;
			}
		}
		
		// まだフィールドが存在する場合は、対応する基底が存在しない
		if (freader.hasNextField()) {
			throw new CsvFormatException("There is not DtBase corresponding to value.",
					freader.getLineNo(), freader.getNextPosition()+1);
		}
		
		return modified;
	}

	/**
	 * テーブル形式のCSVフォーマットから、値のレコードを1レコード読み込む。
	 * <br>値のフィールドが空欄の場合、値も基底も存在しない要素として扱う。
	 * また、特殊記号で始まる値は、特殊値として読み込む。
	 * @param record	CSVフォーマットの1レコード入力オブジェクト
	 * @param baselist	基底のリスト。フィールドの順序通りに読み込まれた基底が格納されていること。
	 * @param setid			データ代数元 ID
	 * @return	データ代数集合の内容が変更された場合は <tt>true</tt>
	 * @throws IOException	入出力エラーが発生した場合
	 * @throws CsvFormatException	カラムのデータが正しくない場合
	 */
	protected boolean readValuesFromTableCsv_v2(CsvReader.CsvRecord record, List<DtBase> baselist, String setid)
		throws IOException, CsvFormatException
	{
		// setup field reader
		CsvReader.CsvFieldReader freader = new CsvReader.CsvFieldReader(record);

		// read values
		boolean modified = false;
		for (DtBase newBase : baselist) {
			// read value
			String strValue = freader.readValue();
			int vtype = Dtalge.getCsvValueType(strValue);
			if (vtype != Dtalge.CSVVALTYPE_NONE) {
				// 文字列が存在する場合のみ、基底に対応する値をデータ代数元に追加する。
				// CSVフィールドが空欄の場合、テーブル形式においては、値と基底が存在しない
				// ものとして扱う。これはメモリ使用量を減らすための対策。
				switch (vtype) {
					case Dtalge.CSVVALTYPE_ESCAPED :
						strValue = strValue.substring(Dtalge.CSV_COMMAND_PREFIX.length());
						break;
					case Dtalge.CSVVALTYPE_CMD_NULL :
						strValue = null;
						break;
					case Dtalge.CSVVALTYPE_CMD_UNKNOWN :
						throw new CsvFormatException("Undefined keyword : " + strValue, freader.getLineNo(), freader.getNextPosition());
					// CSVVALTYPE_STRING :
				}
				
				// データ型の正当性チェック
				Object value = null;
				try {
					value = DtDataTypes.valueOf(newBase.getTypeKey(), strValue);
				}
				catch (Throwable ex) {
					throw new CsvFormatException(ex.getMessage(), freader.getLineNo(), freader.getNextPosition());
				}

				// データを追加
				if (putValue(setid, newBase, value)) {
					modified = true;
				}
			}
		}

		// まだフィールドが存在する場合は、対応する基底が存在しない
		if (freader.hasNextField()) {
			throw new CsvFormatException("There is not DtBase corresponding to value.",
					freader.getLineNo(), freader.getNextPosition()+1);
		}
		
		return modified;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 指定されたデータ代数元 ID でドキュメントを検索するクエリを生成する。
	 * @param setid	データ代数元 ID
	 * @return	フィルターオブジェクト
	 */
	protected Bson makeQueryBySetId(String setid) {
		return Filters.eq(MongoDelegateDocDtAlgeSetElem.MONGO_KEY_SETID, setid);
	}

	/**
	 * 指定された基底でドキュメントを検索するクエリを生成する。
	 * @param base	判定する基底
	 * @return	フィルターオブジェクト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	protected Bson makeQueryByDtBase(DtBase base) {
		return Filters.eq(MongoDelegateDocDtalgeElem.MONGO_KEY_BASE, MongoDelegateDocDtBase.makeDtBaseDocument(base));
	}
	
	/**
	 * 指定された基底を含まないドキュメントを検索するクエリを生成する。
	 * @param base	除外する基底
	 * @return	フィルターオブジェクト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	protected Bson makeQueryWithoutDtBase(DtBase base) {
		return Filters.ne(MongoDelegateDocDtalgeElem.MONGO_KEY_BASE, MongoDelegateDocDtBase.makeDtBaseDocument(base));
	}

	/**
	 * 指定された値でドキュメントを検索するクエリを生成する。
	 * 値は MongoDB において、比較(compare)により判定され、<tt>null</tt> かどうかも判定される。
	 * @param value	判定する値
	 * @return	フィルターオブジェクト
	 */
	protected Bson makeQueryByValue(Object value) {
		return Filters.eq(MongoDelegateDocDtalgeElem.MONGO_KEY_VALUE, value);
	}

	/**
	 * 指定された値を含まないドキュメントを検索するクエリを生成する。
	 * 値は MongoDB において、比較(compare)により判定され、<tt>null</tt> かどうかも判定される。
	 * @param value	除外する値
	 * @return	フィルターオブジェクト
	 */
	protected Bson makeQueryWithoutValue(Object value) {
		return Filters.ne(MongoDelegateDocDtalgeElem.MONGO_KEY_VALUE, value);
	}

	/**
	 * 指定された値のコレクションでドキュメントを検索するクエリを生成する。
	 * @param bases			判定する値のコレクション
	 * @param notMatches	指定されたコレクションに一致しないものを検索する場合は <tt>true</tt>
	 * @return	フィルターオブジェクト
	 * @throws NullPointerException	<em>bases</em> が <tt>null</tt> の場合
	 */
	protected Bson makeQueryByMultiValues(Collection<?> values, boolean notMatches) {
		if (values == null)
			throw new NullPointerException();
		Bson baseFilter;
		if (notMatches)
			baseFilter = Filters.nin(MongoDelegateDocDtalgeElem.MONGO_KEY_BASE, values);
		else
			baseFilter = Filters.in(MongoDelegateDocDtalgeElem.MONGO_KEY_BASE, values);
		return baseFilter;
	}

	/**
	 * このオブジェクトが保持するコレクションから、基底が重複しないドキュメントの検索結果を取得する。
	 * @return 検索結果のイテレート可能オブジェクト
	 * @throws MongoAlgeError	データベースに関する処理に失敗した場合
	 */
	protected DistinctIterable<? extends Document> distinctByDtBase() {
		try {
			// データ代数元 ID は無視
			return _mongo_vals_col.distinct(MongoDelegateDocDtalgeElem.MONGO_KEY_BASE, Document.class);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to distinct by DtBase in MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
	}
	
	/**
	 * このオブジェクトが保持するコレクションから、指定の基底の値が重複しないドキュメントの検索結果を取得する。
	 * @param base	検索対象のデータ代数基底
	 * @return	検索結果のイテレート可能オブジェクト
	 */
	protected DistinctIterable<?> distinctValuesByDtBase(DtBase base) {
		try {
			Class<?> datatype = DtDataTypes.classFromName(base.getTypeKey());
			Bson query = Filters.eq(MongoDelegateDocDtalgeElem.MONGO_KEY_BASE, MongoDelegateDocDtBase.makeDtBaseDocument(base));
			return _mongo_vals_col.distinct(MongoDelegateDocDtAlgeSetElem.MONGO_KEY_VALUE, query, datatype);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to distinct value By DtBase in MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
	}
	
	/**
	 * このオブジェクトが保持するコレクションから、指定されたクエリで検索された結果のうち、
	 * データ代数元 ID が重複しないドキュメントの検索結果を取得する。
	 * @param query	クエリ、<tt>null</tt> の場合はすべてのドキュメント
	 * @return	検索結果のイテレート可能オブジェクト
	 */
	protected DistinctIterable<String> distinctSetId(Bson query) {
		try {
			if (query != null) {
				return _mongo_vals_col.distinct(MongoDelegateDocDtAlgeSetElem.MONGO_KEY_SETID, query, String.class);
			} else {
				return _mongo_vals_col.distinct(MongoDelegateDocDtAlgeSetElem.MONGO_KEY_SETID, String.class);
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to distinct set_id by query in MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
	}

	/**
	 * このオブジェクトが保持するコレクションから、指定の基底の値が <tt>value</tt> と等しい要素の、
	 * データ代数元 ID が重複しないドキュメントの検索結果を取得する。
	 * @param base	検索対象のデータ代数基底
	 * @param value	検索対象の値
	 * @return	検索結果のイテレート可能オブジェクト
	 */
	protected DistinctIterable<String> distinctSetIdByValue(DtBase base, Object value) {
		return distinctSetId(Filters.and(makeQueryByDtBase(base), makeQueryByValue(value)));
	}
	
	/**
	 * このオブジェクトが保持するコレクションから、指定の基底の値が <tt>value</tt> と等しくない要素の、
	 * データ代数元 ID が重複しないドキュメントの検索結果を取得する。
	 * @param base	検索対象のデータ代数基底
	 * @param value	除外する値
	 * @return	検索結果のイテレート可能オブジェクト
	 */
	protected DistinctIterable<String> distinctSetIdWithoutValue(DtBase base, Object value) {
		return distinctSetId(Filters.and(makeQueryByDtBase(base), makeQueryWithoutValue(value)));
	}

	/**
	 * このオブジェクトが保持するコレクションから、格納順でイテレート可能なオブジェクトを取得する。
	 * @param withoutObjectID	検索結果から MongoDB の ObjectID を除外する場合は <tt>true</tt>
	 * @param withoutSetID		検索結果からデータ代数元 ID を除外する場合は <tt>true</tt>
	 * @return	イテレート可能オブジェクト
	 * @throws MongoAlgeError	データベースに関する処理に失敗した場合
	 */
	protected FindIterable<? extends Document> findAllDocuments(boolean withoutObjectID, boolean withoutSetID) {
		try {
			FindIterable<? extends Document> result = _mongo_vals_col.find();
			if (withoutObjectID)
				result = result.projection(MongoUtil.MONGO_FILTER_WITHOUT_OBJID);
			if (withoutSetID)
				result = result.projection(new Document(MongoDelegateDocDtAlgeSetElem.MONGO_KEY_SETID, 0));
			return result;
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Faild to find by all Dtalge fields in MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
	}

	/**
	 * このデータ代数集合に含まれるドキュメントから、指定されたクエリで検索した結果を返す。
	 * @param filter			検索条件、<tt>null</tt> の場合はすべてのドキュメント
	 * @param withoutObjectID	検索結果から MongoDB の ObjectID を除外する場合は <tt>true</tt>
	 * @param withoutSetID		検索結果からデータ代数元 ID を除外する場合は <tt>true</tt>
	 * @return	検索結果のイテレート可能オブジェクト
	 * @throws MongoAlgeError	データベースに関する処理に失敗した場合
	 */
	protected FindIterable<? extends Document> queryDocuments(Bson filter, boolean withoutObjectID, boolean withoutSetID) {
		try {
			FindIterable<? extends Document> result;
			if (filter == null)
				result = _mongo_vals_col.find();
			else
				result = _mongo_vals_col.find(filter);
			if (withoutObjectID)
				result = result.projection(MongoUtil.MONGO_FILTER_WITHOUT_OBJID);
			if (withoutSetID)
				result = result.projection(new Document(MongoDelegateDocDtAlgeSetElem.MONGO_KEY_SETID, 0));
			return result;
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Faild to query documents by specified filter from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
	}

	/**
	 * このオブジェクトが保持するコレクションから、指定されたデータ代数元 ID に一致するもののみを格納順でイテレート可能なオブジェクトを取得する。
	 * @param setid				取得対象とするデータ代数元 ID
	 * @param withoutObjectID	検索結果から MongoDB の ObjectID を除外する場合は <tt>true</tt>
	 * @param withoutSetID		検索結果からデータ代数元 ID を除外する場合は <tt>true</tt>
	 * @return	イテレート可能オブジェクト
	 * @throws MongoAlgeError	データベースに関する処理に失敗した場合
	 */
	protected FindIterable<? extends Document> findBySetId(String setid, boolean withoutObjectID, boolean withoutSetID) {
		return queryDocuments(makeQueryBySetId(setid), withoutObjectID, withoutSetID);
	}

	/**
	 * このオブジェクトが保持するコレクションから、指定された基底に一致するもののみを格納順でイテレート可能なオブジェクトを取得する。
	 * @param base				取得対象とするデータ代数基底
	 * @param withoutObjectID	検索結果から MongoDB の ObjectID を除外する場合は <tt>true</tt>
	 * @param withoutSetID		検索結果からデータ代数元 ID を除外する場合は <tt>true</tt>
	 * @return	イテレート可能オブジェクト
	 * @throws MongoAlgeError	データベースに関する処理に失敗した場合
	 */
	protected FindIterable<? extends Document> findByBase(DtBase base, boolean withoutObjectID, boolean withoutSetID) {
		return queryDocuments(makeQueryByDtBase(base), withoutObjectID, withoutSetID);
	}

	/**
	 * このデータ代数集合に含まれるドキュメントから、指定された値と一致するドキュメントを検索した結果を返す。
	 * MongoDB では、値は等しいかどうか(compare)で判定され、<tt>null</tt> も判定される。
	 * @param value	判定する値
	 * @param withoutObjectID	検索結果から MongoDB の ObjectID を除外する場合は <tt>true</tt>
	 * @param withoutSetID		検索結果からデータ代数元 ID を除外する場合は <tt>true</tt>
	 * @return	検索結果のイテレート可能オブジェクト
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースに関する処理に失敗した場合
	 */
	protected FindIterable<? extends Document> findByValue(Object value, boolean withoutObjectID, boolean withoutSetID) {
		return queryDocuments(makeQueryByValue(value), withoutObjectID, withoutSetID);
	}

	/**
	 * このデータ代数元に含まれるドキュメントから、指定された値と一致しないドキュメントを検索した結果を返す。
	 * MongoDB では、値は等しいかどうか(compare)で判定され、<tt>null</tt> も判定される。
	 * クエリ生成においては、データ代数集合の要素かどうかも考慮される。
	 * @param value	判定する値
	 * @param withoutObjectID	検索結果から MongoDB の ObjectID を除外する場合は <tt>true</tt>
	 * @param withoutSetID		検索結果からデータ代数元 ID を除外する場合は <tt>true</tt>
	 * @return	検索結果のイテレート可能オブジェクト
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースに関する処理に失敗した場合
	 */
	protected FindIterable<? extends Document> findWithoutValue(Object value, boolean withoutObjectID, boolean withoutSetID) {
		return queryDocuments(makeQueryWithoutValue(value), withoutObjectID, withoutSetID);
	}
	
	/**
	 * このデータ代数元に含まれるドキュメントから、指定されたデータ代数元 ID と基底に一致するドキュメントを一つ取得する。
	 * @param setid	検索するデータ代数元 ID
	 * @param base	検索するデータ代数基底
	 * @param withoutObjectID	検索結果から MongoDB の ObjectID を除外する場合は <tt>true</tt>
	 * @param withoutSetID		検索結果からデータ代数元 ID を除外する場合は <tt>true</tt>
	 * @return	検索結果のドキュメント、見つからない場合は <tt>null</tt>
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースに関する処理に失敗した場合
	 */
	protected Document findOneBySetIdAndBase(String setid, DtBase base, boolean withoutObjectID, boolean withoutSetID) {
		try {
			Bson query = Filters.and(makeQueryBySetId(setid), makeQueryByDtBase(base));
			return queryDocuments(query, withoutObjectID, withoutSetID).first();
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Faild to get by set_id and DtBase fields in MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
	}

	/**
	 * このオブジェクトが保持するコレクションから、すべての値でソートされたイテレート可能オブジェクトを取得する。
	 * @param withoutObjectID	検索結果から MongoDB の ObjectID を除外する場合は <tt>true</tt>
	 * @param withoutSetID		検索結果からデータ代数元 ID を除外する場合は <tt>true</tt>
	 * @return	イテレート可能オブジェクト
	 * @throws MongoAlgeError	データベースに関する処理に失敗した場合
	 */
	protected FindIterable<? extends Document> sortedDocuments(boolean withoutObjectID, boolean withoutSetID) {
		try {
			FindIterable<? extends Document> result = _mongo_vals_col.find().sort(MongoDelegateDocDtAlgeSetElem.MONGO_SORT_DTALGESET_ELEM_ALL);
			if (withoutObjectID)
				result = result.projection(MongoUtil.MONGO_FILTER_WITHOUT_OBJID);
			if (withoutSetID)
				result = result.projection(new Document(MongoDelegateDocDtAlgeSetElem.MONGO_KEY_SETID, 0));
			return result;
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Faild to sort by all Dtalge fields in MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
	}

	/**
	 * 指定されたデータ代数集合のコレクションが、自身の内容と一致するかどうかを判定する。
	 * <p>この判定では、両コレクションをデータ代数元 ID、名前キー、データ型キー、属性キー、主体キー、値の順に昇順でソートし、
	 * データ代数元の単位でその内容を比較する。
	 * @param another	判定対象のコレクション
	 * @return	等しい場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoExalgeError	データベースの処理が正常に行えない場合
	 */
	protected boolean equalsAnotherDtAlgeSetCollection(MongoCollection<? extends Document> another) {
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
				cursor1 = _mongo_vals_col.find().sort(MongoDelegateDocDtAlgeSetElem.MONGO_SORT_DTALGESET_ELEM_ALL).iterator();
				cursor2 = another.find().sort(MongoDelegateDocDtAlgeSetElem.MONGO_SORT_DTALGESET_ELEM_ALL).iterator();
				long idxCol1 = -1L;
				long idxCol2 = -1L;
				String strLastSetID1 = null;
				String strLastSetID2 = null;
				while (cursor1.hasNext()) {
					Document doc1 = cursor1.next();
					Document doc2 = cursor2.next();
					//--- elem_id によるインデックス更新
					String strCurSetID1 = MongoDelegateDocDtAlgeSetElem.getDtAlgeSetIDfromDocument(doc1);
					String strCurSetID2 = MongoDelegateDocDtAlgeSetElem.getDtAlgeSetIDfromDocument(doc2);
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
					if (!MongoDelegateDocDtAlgeSetElem.equalsDtalgeElemDocument(doc1, doc2)) {
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
	 * データ代数元のイテレーターを生成する。
	 * このイテレーターが返す順序は、データ代数元の格納順以外はデータベースの実装に依存する。
	 * 
	 * @return <code>MongoDtalge</code> の <code>Iterator</code>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	protected BigIterator<MongoDtalge> newMongoDtalgeIterator() {
		// TODO: カーソルのクローズ処理を今後考慮
		try {
			//--- ソート済みイテレータにする
			MongoCursor<? extends Document> cursor = _mongo_vals_col.aggregate(MONGO_AGGRE_ELEM_ITERABLE).iterator();
			return new MongoDtalgeIterator(cursor);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get MongDtalge iteration cursor from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
	}
	
	protected BigIterator<BigDtAlgeSetInnerElement> newUnmodifiableNaturalOrderedDocumentIterator() {
		// TODO: カーソルのクローズ処理を今後考慮
		try {
			// (注意) 削除を可能とするため、"_id" も検索結果に含める
			MongoCursor<? extends Document> cursor = findAllDocuments(false, false).iterator();
			return new MongoDtAlgeSetDocumentIterator(cursor);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get natural ordered document cursor from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
	}
	
	protected BigIterator<BigDtAlgeSetInnerElement> newUnmodifiableSortedDocumentIterator() {
		// TODO: カーソルのクローズ処理を今後考慮
		try {
			//--- ソート済みイテレータにする
			// (注意) 削除を可能とするため、"_id" も検索結果に含める
			MongoCursor<? extends Document> cursor = sortedDocuments(false, false).iterator();
			return new MongoDtAlgeSetDocumentIterator(cursor);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get sorted document cursor from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
	}
	
	protected BigIterator<DtBase> newUnmodifiableSortedDtBaseIterator() {
		// TODO: カーソルのクローズ処理を今後考慮
		try {
			//--- ソート済みイテレータにする
			MongoCursor<? extends Document> cursor = sortedDocuments(true, false).iterator();
			return new MongoDtAlgeSetElementInnerBaseIterator(cursor);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get sorted DtBase cursor from MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	/**
	 * <code>MongoDtAlgeSet</code> クラスのデータ代数元の要素の値を保持するクラス。
	 * <p>このオブジェクトは不変である。
	 * 
	 * @version 0.5.0
	 * @since 0.5.0
	 * 
	 * @author H.Deguchi(SOARS Project.)
	 * @author Y.Ishizuka(PieCake.inc,)
	 */
	static public class MongoDtAlgeSetInnerElement implements BigDtAlgeSetInnerElement
	{
		private final String	_setid;
		private final DtBase	_base;
		private final Object	_value;
		
		public MongoDtAlgeSetInnerElement(String setid, DtBase base, Object value) {
			_setid = setid;
			_base  = base;
			_value = value;
		}

		@Override
		public String getAlgeSetID() {
			return _setid;
		}

		@Override
		public DtBase getBase() {
			return _base;
		}

		@Override
		public Object getValue() {
			return _value;
		}
	}
	
	/**
	 * <code>MongoDtAlgeSet</code> クラスの要素にアクセス可能なイテレーターの共通実装。
	 * <p>
	 * このクラスは、データ代数集合<code>(MongoDtAlgeSet)</code>からデータ代数要素(単一の基底と値を持つデータ代数元)を
	 * 順次取得するためのイテレーターである。
	 * <p>
	 * このクラスのインタフェースでは、基本的に {@link #remove()} はサポートされていない。
	 * 
	 * @version 0.5.0
	 * @since 0.5.0
	 * 
	 * @author H.Deguchi(SOARS Project.)
	 * @author Y.Ishizuka(PieCake.inc,)
	 */
	private abstract class AbstractMongoDtAlgeSetInnerElementIterator<T> implements BigIterator<T>
	{
		MongoCursor<? extends Document>	_itCursor;
		long _expectedModCount;
		
		AbstractMongoDtAlgeSetInnerElementIterator(MongoCursor<? extends Document> cursor) {
			_itCursor = cursor;
			_expectedModCount = _modCount.get();
		}

		public boolean hasNext() {
			try {
				return (_itCursor==null ? false : _itCursor.hasNext());
			} catch (MongoException ex) {
				throw new MongoAlgeError("MongoDtAlgeSet's element iterator couldn't check next element in MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
			}
		}
		
		/**
		 * <code>AbstractMongoDtalgeElementIterator</code> クラスでは、このメソッドはサポートされない。
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
	 * <code>MongoDtAlgeSet</code> クラスのドキュメントイテレーター。
	 * <p>
	 * このクラスは、データ代数集合<code>(MongoDtAlgeSet)</code>からデータ代数要素(BigDtAlgeSetInnerElement)を
	 * 順次取得するためのイテレーターである。
	 * <p>
	 * このクラスのインタフェースでは、{@link #remove()} は実行可能となっている。
	 * 
	 * @version 0.5.0
	 * @since 0.5.0
	 * 
	 * @author H.Deguchi(SOARS Project.)
	 * @author Y.Ishizuka(PieCake.inc,)
	 */
	private class MongoDtAlgeSetDocumentIterator extends AbstractMongoDtAlgeSetInnerElementIterator<BigDtAlgeSetInnerElement>
	{
		protected Object _lastObjectId;
		
		MongoDtAlgeSetDocumentIterator(MongoCursor<? extends Document> cursor) {
			super(cursor);
		}
		
		@Override
		public BigDtAlgeSetInnerElement next() {
			if (_expectedModCount != _modCount.get())
				throw new ConcurrentModificationException();
			if (_itCursor == null)
				throw new NoSuchElementException("MongoDtAlgeSet's cursor was closed.");
			try {
				Document docDtAlgeSetElem = _itCursor.next();
				_lastObjectId = docDtAlgeSetElem.get("_id");
				String setid = MongoDelegateDocDtAlgeSetElem.getDtAlgeSetIDfromDocument(docDtAlgeSetElem);
				DtBase base = MongoDelegateDocDtalgeElem.toDtBaseFromDocument(docDtAlgeSetElem);
				Object value = MongoDelegateDocDtalgeElem.getValueObjectFromDocument(docDtAlgeSetElem);
				if (!_itCursor.hasNext()) {
					closeCursor();
				}
				return new MongoDtAlgeSetInnerElement(setid, base, value);
			} catch (MongoException ex) {
				throw new MongoAlgeError("MongoDtAlgeSet's document iterator couldn't get next element in MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
			}
		}
		
		/**
		 * 現在の位置のドキュメントを削除する。
		 * 
		 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
		 */
		public void remove() {
			try {
				if (_expectedModCount != _modCount.get())
					throw new ConcurrentModificationException();
				if (_lastObjectId != null) {
					_mongo_vals_col.deleteOne(new Document("_id", _lastObjectId));
					_lastObjectId = null;
					_expectedModCount = _modCount.incrementAndGet();
				}
			}
			catch (MongoException ex) {
				throw new MongoAlgeError("MongoDtAlgeSet's document iterator couldn't get next element in MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
			}
		}
	}
	
	/**
	 * <code>MongoDtAlgeSet</code> クラスのデータ代数基底としての要素イテレーター。
	 * <p>
	 * このクラスは、データ代数集合<code>(MongoDtAlgeSet)</code>からデータ代数元に含まれるデータ代数要素の基底(DtBase)を
	 * 順次取得するためのイテレーターである。
	 * <p>
	 * このクラスのインタフェースでは、{@link #remove()} はサポートされていない。
	 * 
	 * @version 0.5.0
	 * @since 0.5.0
	 * 
	 * @author H.Deguchi(SOARS Project.)
	 * @author Y.Ishizuka(PieCake.inc,)
	 */
	private class MongoDtAlgeSetElementInnerBaseIterator extends AbstractMongoDtAlgeSetInnerElementIterator<DtBase>
	{
		MongoDtAlgeSetElementInnerBaseIterator(MongoCursor<? extends Document> cursor) {
			super(cursor);
		}
		
		@Override
		public DtBase next() {
			if (_expectedModCount != _modCount.get())
				throw new ConcurrentModificationException();
			if (_itCursor == null)
				throw new NoSuchElementException("MongoDtAlgeSet's cursor was closed.");
			try {
				Document docDtalgeElem = _itCursor.next();
				DtBase base = MongoDelegateDocDtalgeElem.toDtBaseFromDocument(docDtalgeElem);
				if (!_itCursor.hasNext()) {
					closeCursor();
				}
				return base;
			} catch (MongoException ex) {
				throw new MongoAlgeError("MongoDtAlgeSet's DtBase iterator couldn't get next element in MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
			}
		}
	}
	
	/**
	 * <code>MongoDtAlgeSet</code> クラスのデータ代数元のイテレーター。
	 * <p>
	 * このクラスは、データ代数集合<code>(MongoDtAlgeSet)</code>からデータ代数元(複数の基底と値を持つデータ代数元)を
	 * 順次取得するためのイテレーターである。
	 * <p>
	 * このクラスのインタフェースでは、{@link #remove()} は実行可能となっている。
	 * 
	 * @version 0.5.0
	 * @since 0.5.0
	 * 
	 * @author H.Deguchi(SOARS Project.)
	 * @author Y.Ishizuka(PieCake.inc,)
	 */
	private class MongoDtalgeIterator extends AbstractMongoDtAlgeSetInnerElementIterator<MongoDtalge>
	{
		protected String _lastSetId;
		
		MongoDtalgeIterator(MongoCursor<? extends Document> cursor) {
			super(cursor);
		}
		
		@Override
		public MongoDtalge next() {
			// このイテレータがアクセスするのは、重複のないデータ代数元 ID のコレクションのカーソル
			if (_expectedModCount != _modCount.get())
				throw new ConcurrentModificationException();
			if (_itCursor == null)
				throw new NoSuchElementException("MongoDtAlgeSet's cursor was closed.");
			try {
				Document docDtalgeID = _itCursor.next();
				_lastSetId = MongoDelegateDocDtAlgeSetElem.getDtAlgeSetIDfromDocument(docDtalgeID);
				if (!_itCursor.hasNext()) {
					closeCursor();
				}
				return new MongoDtalge(MongoDtAlgeSet.this, _lastSetId);
			} catch (MongoException ex) {
				throw new MongoAlgeError("MongoDtAlgeSet's element iterator couldn't get next element in MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
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
					_mongo_vals_col.deleteMany(Filters.eq(MongoDelegateDocDtAlgeSetElem.MONGO_KEY_SETID, _lastSetId));
					_lastSetId = null;
					_expectedModCount = _modCount.incrementAndGet();
				}
			}
			catch (MongoException ex) {
				throw new MongoAlgeError("MongoDtAlgeSet's element iterator couldn't remove current element in MongoDtAlgeSet" + _mongo_vals_col.getNamespace().toString() + ":", ex);
			}
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
