/*
 * @(#)MongoDtBaseSet.java	0.5.0	2019/02/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.db.mongo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

import org.bson.Document;

import com.mongodb.MongoException;
import com.mongodb.MongoNamespace;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import dtalge.DtBase;
import dtalge.DtBasePattern;
import dtalge.DtBasePatternSet;
import dtalge.DtBaseSet;
import dtalge.db.BigDtBaseSet;
import dtalge.exception.CsvFormatException;
import dtalge.io.internal.CsvReader;
import dtalge.io.internal.CsvWriter;
import redundantalge.db.BigIterator;
import redundantalge.db.mongo.MongoAlgeError;
import redundantalge.db.mongo.MongoSession;
import redundantalge.db.mongo.MongoUtil;

/**
 * 大容量のデータ代数基底集合を保持するクラス。
 * <p>ストレージとして MongoDB を利用する。
 * 
 * <p>データ代数基底 <code>DtBase</code> インスタンスの集合であり、
 * このオブジェクトが生成したコレクションである限り、同じ値が重複して含まれることはない。
 * <p>
 * このクラスでは、<tt>null</tt> を許容しない。
 * <br>
 * また、<b>この実装は同期化されない</b>。
 * <p>
 * <b>《入出力フォーマット》</b>
 * <br>
 * <code>MongoDtBaseSet</code> の MongoDB におけるデータ構造は、次の通りである。
 * <pre><code>
 * {
 *   &quot;name&quot; : &lt;string&gt;
 *   &quot;type&quot; : &lt;string&gt;
 *   &quot;attr&quot; : &lt;string&gt;
 *   &quot;subject&quot; : &lt;string&gt;
 * }
 * </code></pre>
 * 
 * @version 0.5.0
 * @since 0.5.0
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public class MongoDtBaseSet implements BigDtBaseSet
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	/** 一時的なコレクション名のプレフィックス **/
	static public final String	TEMP_COLNAME_PREFIX	= "TDtbs";

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
	public MongoDtBaseSet(MongoSession session) {
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
	public MongoDtBaseSet(MongoSession session, String collection) {
		if (session == null)
			throw new NullPointerException("MongoDtalgeSession object is null.");
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
		MongoNamespace oldName = _mongo_col.getNamespace();
		MongoNamespace newName = new MongoNamespace(oldName.getDatabaseName(), newCollectionName);
		try {
			_mongo_col.renameCollection(newName);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to persist of MongoDtBaseSet" + _mongo_col.getNamespace().toString() + ":", ex);
		}
		_mongo_session.unregisterTemporaryCollection(oldName);
		_mongo_col = _mongo_session.getPersistentCollection(newCollectionName);
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
			throw new MongoAlgeError("Failed to drop collection of MongoDtBaseSet" + _mongo_col.getNamespace().toString() + ":", ex);
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
		if (obj instanceof MongoDtBaseSet) {
			MongoDtBaseSet that = (MongoDtBaseSet)obj;
			return MongoUtil.equalsCollectionsWithoutID(MongoDelegateDocDtBase.MONGO_SORT_DTBASE_ALL, this._mongo_col, that._mongo_col);
		}
		else if (obj instanceof BigDtBaseSet) {
			BigDtBaseSet that = (BigDtBaseSet)obj;
			long thisSize = this.size();
			if (thisSize == that.size()) {
				if (thisSize > 0L) {
					return containsAll(that);
				} else {
					return true;
				}
			}
		}
		else if (obj instanceof DtBaseSet) {
			DtBaseSet that = (DtBaseSet)obj;
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
			throw new MongoAlgeError("Failed to count documents in MongoDtBase" + _mongo_col.getNamespace().toString() + ":", ex);
		}
	}

	/**
	 * 指定された基底が含まれているかを判定する。
	 * @param base	判定する基底
	 * @return	含まれている場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean contains(DtBase base) {
		if (base == null)
			return false;
		try {
			return (_mongo_col.countDocuments(MongoDelegateDocDtBase.makeDtBaseDocument(base)) > 0L);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to contain DtBase in MongoDtBase" + _mongo_col.getNamespace().toString() + ":", ex);
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
	public boolean containsAll(Collection<? extends DtBase> c) {
		for (DtBase base : c) {
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
	public boolean containsAll(BigDtBaseSet set) {
		for (DtBase base : set) {
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
	public boolean containsAny(Collection<? extends DtBase> c) {
		for (DtBase base : c) {
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
	public boolean containsAny(BigDtBaseSet set) {
		for (DtBase base : set) {
			if (contains(base))
				return true;
		}
		return false;
	}

	/**
	 * この基底集合に含まれる基底の名前キーのみの集合を取得する。
	 * このメソッドが返す集合は、次のような規則で構成される。
	 * <ul>
	 * <li>キーの文字列は重複しない
	 * <li>キーの文字列は、文字列の自然順序付けにより昇順にソートされる
	 * </ul>
	 * 
	 * @param withoutOmitted	省略記号('#') を除外する場合は <tt>true</tt> を指定する
	 * @return	基底の名前キーの集合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public Set<String> getBaseNameKeySet(boolean withoutOmitted) {
		return getBaseKeySet(MongoDelegateDocDtBase.MONGO_KEYIDX_NAME, withoutOmitted);
	}
	
	/**
	 * この基底集合に含まれる基底のデータ型キーのみの集合を取得する。
	 * このメソッドが返す集合は、次のような規則で構成される。
	 * <ul>
	 * <li>キーの文字列は重複しない
	 * <li>キーの文字列は、文字列の自然順序付けにより昇順にソートされる
	 * </ul>
	 * 
	 * @param withoutOmitted	省略記号('#') を除外する場合は <tt>true</tt> を指定する
	 * @return	基底のデータ型キーの集合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public Set<String> getBaseTypeKeySet(boolean withoutOmitted) {
		return getBaseKeySet(MongoDelegateDocDtBase.MONGO_KEYIDX_TYPE, withoutOmitted);
	}
	
	/**
	 * この基底集合に含まれる基底の属性キーのみの集合を取得する。
	 * このメソッドが返す集合は、次のような規則で構成される。
	 * <ul>
	 * <li>キーの文字列は重複しない
	 * <li>キーの文字列は、文字列の自然順序付けにより昇順にソートされる
	 * </ul>
	 * 
	 * @param withoutOmitted	省略記号('#') を除外する場合は <tt>true</tt> を指定する
	 * @return	基底の属性キーの集合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public Set<String> getBaseAttributeKeySet(boolean withoutOmitted) {
		return getBaseKeySet(MongoDelegateDocDtBase.MONGO_KEYIDX_ATTR, withoutOmitted);
	}
	
	/**
	 * この基底集合に含まれる基底の主体キーのみの集合を取得する。
	 * このメソッドが返す集合は、次のような規則で構成される。
	 * <ul>
	 * <li>キーの文字列は重複しない
	 * <li>キーの文字列は、文字列の自然順序付けにより昇順にソートされる
	 * </ul>
	 * 
	 * @param withoutOmitted	省略記号('#') を除外する場合は <tt>true</tt> を指定する
	 * @return	基底の主体キーの集合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public Set<String> getBaseSubjectKeySet(boolean withoutOmitted) {
		return getBaseKeySet(MongoDelegateDocDtBase.MONGO_KEYIDX_SUBJECT, withoutOmitted);
	}

	/**
	 * 指定されたインデックスの位置にある基底キーのみの集合を取得する。
	 * このメソッドが返す集合は、次のような規則で構成される。
	 * <ul>
	 * <li>キーの文字列は重複しない
	 * <li>キーの文字列は、文字列の自然順序付けにより昇順にソートされる
	 * </ul>
	 * 
	 * @param index		取得する基底キーのインデックス
	 * @param withoutOmitted	省略記号('#') を除外する場合は <tt>true</tt> を指定する
	 * @return	指定された基底キーの集合
	 * 
	 * @throws IndexOutOfBoundsException	インデックスが範囲外の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	protected Set<String> getBaseKeySet(int index, boolean withoutOmitted) {
		if (withoutOmitted)
			return getBaseKeySet(index, Collections.singleton(DtBase.OMITTED));
		else
			return getBaseKeySet(index, null);
	}

	/**
	 * 指定されたインデックスの位置にある基底キーのみの集合を取得する。
	 * このメソッドが返す集合は、次のような規則で構成される。
	 * <ul>
	 * <li>キーの文字列は重複しない
	 * <li>キーの文字列は、文字列の自然順序付けにより昇順にソートされる
	 * </ul>
	 * <p>
	 * <code>withoutStrings</code> が <tt>null</tt> ではない場合、この引数のコレクションに
	 * 含まれる文字列は、このメソッドが返す集合から除外される。
	 * 
	 * @param index		取得する基底キーのインデックス
	 * @param withoutStrings	除外する文字列の集合
	 * @return	指定された基底キーの集合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	protected Set<String> getBaseKeySet(int index, Collection<? extends String> withoutStrings) {
		Set<String> resultSet = new TreeSet<String>();
		//--- 指定されたインデックスに相当するキーのみを取得
		try {
			MongoCursor<? extends Document> cursor = _mongo_col.distinct(MongoDelegateDocDtBase.MOGNO_KEYS_ARRAY[index], Document.class).iterator();
			try {
				while (cursor.hasNext()) {
					Document docElem = cursor.next();
					String strKey = MongoDelegateDocDtBase.getDtBaseKeyStringByIndex(docElem, index);
					if (strKey != null && !strKey.isEmpty()) {
						resultSet.add(strKey);
					}
				}
			}
			finally {
				MongoUtil.closeCursorSilent(cursor);
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to distinct by DtBase's key(" + index + ") in MongoDtBaseSet" + _mongo_col.getNamespace().toString() + ":", ex);
		}
		if (withoutStrings != null && !withoutStrings.isEmpty()) {
			resultSet.removeAll(withoutStrings);
		}
		return resultSet;
	}
	
	/**
	 * このオブジェクトのイテレーターを返す。
	 * @return	イテレーターオブジェクト
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public BigIterator<DtBase> iterator() {
		// TODO: カーソルのクローズ処理を今後考慮
		try {
			//--- ソート済みイテレータにする
			MongoCursor<? extends Document> cursor = _mongo_col.find().sort(MongoDelegateDocDtBase.MONGO_SORT_DTBASE_ALL).iterator();
			return new MongoDtBaseSetIterator(cursor);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get sorted cursor from MongoDtBaseSet" + _mongo_col.getNamespace().toString() + ":", ex);
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
	public boolean add(DtBase base) {
		Document data = MongoDelegateDocDtBase.makeDtBaseDocument(base);
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
	public boolean remove(DtBase base) {
		if (base == null)
			return false;
		Document where = MongoDelegateDocDtBase.makeDtBaseDocument(base);
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
	public boolean addAll(Collection<? extends DtBase> c) {
		long added = 0L;
		Iterator<? extends DtBase> it = c.iterator();
		while (it.hasNext()) {
			DtBase base = it.next();
			if (base != null) {
				Document data = MongoDelegateDocDtBase.makeDtBaseDocument(base);
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
	public boolean addAll(BigDtBaseSet set) {
		long added = 0L;
		Iterator<? extends DtBase> it = set.iterator();
		while (it.hasNext()) {
			DtBase base = it.next();
			if (base != null) {
				Document data = MongoDelegateDocDtBase.makeDtBaseDocument(base);
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
	public boolean removeAll(Collection<? extends DtBase> c) {
		long totalRemoved = 0L;
		Iterator<? extends DtBase> it = c.iterator();
		while (it.hasNext()) {
			DtBase base = it.next();
			if (base != null) {
				Document where = MongoDelegateDocDtBase.makeDtBaseDocument(base);
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
	public boolean removeAll(BigDtBaseSet set) {
		long totalRemoved = 0L;
		Iterator<? extends DtBase> it = set.iterator();
		while (it.hasNext()) {
			DtBase base = it.next();
			if (base != null) {
				Document where = MongoDelegateDocDtBase.makeDtBaseDocument(base);
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
	public boolean retainAll(Collection<? extends DtBase> c) {
		long totalRemoved = 0L;
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = _mongo_col.find().iterator();
			while (cursor.hasNext()) {
				DtBase base = MongoDelegateDocDtBase.toDtBase(cursor.next());
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
	public boolean retainAll(BigDtBaseSet set) {
		long totalRemoved = 0L;
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = _mongo_col.find().iterator();
			while (cursor.hasNext()) {
				DtBase base = MongoDelegateDocDtBase.toDtBase(cursor.next());
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
	 * @return 複製された <code>MongoDtBaseSet</code> オブジェクト
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoDtBaseSet copy() {
		MongoDtBaseSet newSet = new MongoDtBaseSet(_mongo_session);
		MongoUtil.duplicateCollection(newSet._mongo_col, this._mongo_col);
		return newSet;
	}

	/**
	 * 基底集合を連結した、新しい基底集合のインスタンスを返す。
	 * <br>
	 * 基底集合では、要素の基底が重複することはないため、{@link #union(DtBaseSet)} と
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
	public MongoDtBaseSet addition(DtBaseSet set) {
		MongoDtBaseSet newSet = copy();
		newSet.addAll(set);
		return newSet;
	}
	
	/**
	 * 基底集合を連結した、新しい基底集合のインスタンスを返す。
	 * <br>
	 * 基底集合では、要素の基底が重複することはないため、{@link #union(BigDtBaseSet)} と
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
	public MongoDtBaseSet addition(BigDtBaseSet set) {
		MongoDtBaseSet newSet = copy();
		newSet.addAll(set);
		return newSet;
	}

	/**
	 * 引数に指定された基底集合の要素を除いた、新しい基底集合の
	 * インスタンスを返す。
	 * <br>
	 * 基底集合では、要素の基底が重複することはないため、{@link #difference(DtBaseSet)} と
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
	public MongoDtBaseSet subtraction(DtBaseSet set) {
		MongoDtBaseSet newSet = copy();
		newSet.removeAll(set);
		return newSet;
	}

	/**
	 * 引数に指定された基底集合の要素を除いた、新しい基底集合の
	 * インスタンスを返す。
	 * <br>
	 * 基底集合では、要素の基底が重複することはないため、{@link #difference(BigDtBaseSet)} と
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
	public MongoDtBaseSet subtraction(BigDtBaseSet set) {
		MongoDtBaseSet newSet = copy();
		newSet.removeAll(set);
		return newSet;
	}

	/**
	 * この集合から指定された基底集合の要素以外を除いた、新しい基底集合の
	 * インスタンスを返す。
	 * <br>
	 * 基底集合では、要素の基底が重複することはないため、{@link #intersection(DtBaseSet)} と
	 * 同じ結果を返す。
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @param set	この集合において維持する要素を持つ基底集合
	 * @return	この集合から指定された集合の要素以外を除いた、新しい基底集合
	 * 
	 * @throws NullPointerException	指定された集合が <tt>null</tt> の場合
	 */
	@Override
	public MongoDtBaseSet retention(DtBaseSet set) {
		return intersection(set);
	}

	/**
	 * この集合から指定された基底集合の要素以外を除いた、新しい基底集合の
	 * インスタンスを返す。
	 * <br>
	 * 基底集合では、要素の基底が重複することはないため、{@link #intersection(DtBaseSet)} と
	 * 同じ結果を返す。
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @param set	この集合において維持する要素を持つ基底集合
	 * @return	この集合から指定された集合の要素以外を除いた、新しい基底集合
	 * 
	 * @throws NullPointerException	指定された集合が <tt>null</tt> の場合
	 */
	@Override
	public MongoDtBaseSet retention(BigDtBaseSet set) {
		return intersection(set);
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
	public MongoDtBaseSet union(DtBaseSet set) {
		MongoDtBaseSet newSet = copy();
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
	public MongoDtBaseSet union(BigDtBaseSet set) {
		MongoDtBaseSet newSet = copy();
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
	public MongoDtBaseSet intersection(DtBaseSet set) {
		MongoDtBaseSet newSet = copy();
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
	public MongoDtBaseSet intersection(BigDtBaseSet set) {
		MongoDtBaseSet newSet = copy();
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
	public MongoDtBaseSet difference(DtBaseSet set) {
		MongoDtBaseSet newSet = copy();
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
	public MongoDtBaseSet difference(BigDtBaseSet set) {
		MongoDtBaseSet newSet = copy();
		newSet.removeAll(set);
		return newSet;
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
	public MongoDtBaseSet getMatchedBases(DtBasePattern pattern) {
		MongoDtBaseSet newSet = new MongoDtBaseSet(this._mongo_session);
		for (DtBase base : this) {
			if (pattern.matches(base)) {
				newSet.add(base);
			}
		}
		return newSet;
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
	public MongoDtBaseSet getMatchedBases(DtBasePatternSet patterns) {
		MongoDtBaseSet newSet = new MongoDtBaseSet(this._mongo_session);
		for (DtBase base : this) {
			if (patterns.matches(base)) {
				newSet.add(base);
			}
		}
		return newSet;
	}

	//------------------------------------------------------------
	// I/O
	//------------------------------------------------------------
	
	/**
	 * 基底集合の内容を、指定のファイルに CSV フォーマットで出力する。
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
			writeToCSV(writer);
		}
		finally {
			writer.close();
		}
	}
	
	/**
	 * 基底集合の内容を、指定された文字セットで指定のファイルに CSV フォーマットで出力する。
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
			writeToCSV(writer);
		}
		finally {
			writer.close();
		}
	}

	/**
	 * CSV フォーマットのファイルを読み込み、このオブジェクトに追加する。
	 * @param csvFile	読み込む CSV ファイル
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
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
			return readFromCSV(reader);
		}
		finally {
			reader.close();
		}
	}
	
	/**
	 * 指定された文字セットで CSV フォーマットのファイルを読み込み、このオブジェクトに追加する。
	 * @param csvFile	読み込む CSV ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
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
			return readFromCSV(reader);
		}
		finally {
			reader.close();
		}
	}
	
	/**
	 * CSV フォーマットのファイルを読み込み、指定された MongoDB セッションにおいて一時的なコレクションをストレージとする新しい基底集合を生成する。
	 * <p>
	 * 基本的に、ファイルに記述された要素の順序で、新しいインスタンスに格納される。
	 * 
	 * @param session	MongoDB セッションオブジェクト
	 * @param csvFile 読み込む CSV ファイル
	 * 
	 * @return ファイルの内容で生成された、新しい <code>MongoDtBaseSet</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	static public MongoDtBaseSet fromCSV(MongoSession session, File csvFile)
		throws IOException, FileNotFoundException, CsvFormatException
	{
		MongoDtBaseSet newset = new MongoDtBaseSet(session);
		newset.addAllFromCSV(csvFile);
		return newset;
	}
	
	/**
	 * 指定された文字セットで CSV フォーマットのファイルを読み込み、指定された MongoDB セッションにおいて一時的なコレクションをストレージとする新しい基底集合を生成する。
	 * <p>
	 * 基本的に、ファイルに記述された要素の順序で、新しいインスタンスに格納される。
	 * 
	 * @param session	MongoDB セッションオブジェクト
	 * @param csvFile 読み込む CSV ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @return ファイルの内容で生成された、新しい <code>DtBaseSet</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	static public MongoDtBaseSet fromCSV(MongoSession session, File csvFile, String charsetName)
		throws IOException, FileNotFoundException, CsvFormatException, UnsupportedEncodingException
	{
		MongoDtBaseSet newset = new MongoDtBaseSet(session);
		newset.addAllFromCSV(csvFile, charsetName);
		return newset;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * CSVファイルフォーマットの第1行目のキーワード
	 */
	static protected final String CSV_KEYWORD = "#DtbaseSet";

	/**
	 * CSVフォーマットで DtBaseSet の内容を出力
	 * 
	 * @param writer CSVファイル出力オブジェクト
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	protected void writeToCSV(CsvWriter writer)
		throws IOException
	{
		// キーワード出力
		writer.writeLine(CSV_KEYWORD);

		// データ出力
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = _mongo_col.find().iterator();
			while (cursor.hasNext()) {
				DtBase base = MongoDelegateDocDtBase.toDtBase(cursor.next());
				base.writeFieldToCSV(writer);
				writer.newLine();
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to retain all elements of the specified collection at " + _mongo_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		writer.flush();
	}

	/**
	 * CSVフォーマットで DtBaseSet の内容を読み込む
	 * @param reader CSVファイル入力オブジェクト
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 */
	protected boolean readFromCSV(CsvReader reader)
		throws IOException, CsvFormatException
	{
		CsvReader.CsvRecord record;
		
		// 先頭１行目のキーワードをチェック
		record = reader.readRecord();
		if (record == null || !record.hasFields()) {
			// undefined DtBaseSet CSV ID
			throw new CsvFormatException("DtBaseSet file ID not found.");
		} else {
			CsvReader.CsvField field = record.getField(0);
			if (field == null || !CSV_KEYWORD.equals(field.getValue())) {
				throw new CsvFormatException("Illegal DtBaseSet file ID.", record.getLineNo(), 1);
			}
			// 以降のフィールドは無視
		}
		
		// データ読み込み
		boolean modified = false;
		while ((record = reader.readRecord()) != null) {
			// 空行、もしくは値のないレコードはスキップ
			if (!record.hasFields() || !record.hasValues()) {
				continue;
			}
			
			// フィールドの値読み出し
			CsvReader.CsvFieldReader freader = new CsvReader.CsvFieldReader(record);
			DtBase newBase = DtBase.readFieldFromCSV(freader);
			if (add(newBase)) {
				// modified
				modified = true;
			}
		}
		return modified;
	}

	//------------------------------------------------------------
	// Override
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	protected class MongoDtBaseSetIterator implements BigIterator<DtBase> {
		MongoCursor<? extends Document>	_itCursor;
		long _expectedModCount;
		protected Object _lastObjectId;
		
		MongoDtBaseSetIterator(MongoCursor<? extends Document> cursor) {
			_itCursor = cursor;
			_expectedModCount = _modCount.get();
		}

		@Override
		public boolean hasNext() {
			try {
				return (_itCursor==null ? false : _itCursor.hasNext());
			} catch (MongoException ex) {
				throw new MongoAlgeError("MongoDtBaseSet's iterator couldn't check next element in MongoDtBaseSet" + _mongo_col.getNamespace().toString() + ":", ex);
			}
		}
		
		@Override
		public DtBase next() {
			if (_expectedModCount != _modCount.get())
				throw new ConcurrentModificationException();
			if (_itCursor == null)
				throw new NoSuchElementException("MongoDtBaseSet's cursor was closed.");
			try {
				Document docDtBase = _itCursor.next();
				_lastObjectId = docDtBase.get("_id");
				DtBase base = MongoDelegateDocDtBase.toDtBase(docDtBase);
				if (!_itCursor.hasNext()) {
					closeCursor();
				}
				return base;
			} catch (MongoException ex) {
				throw new MongoAlgeError("MongoDtBaseSet's iterator couldn't get next element in MongoDtBaseSet" + _mongo_col.getNamespace().toString() + ":", ex);
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
			} catch (MongoException ex) {
				throw new MongoAlgeError("MongoDtBaseSet's iterator couldn't remove element in MongoDtBaseSet" + _mongo_col.getNamespace().toString() + ":", ex);
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
