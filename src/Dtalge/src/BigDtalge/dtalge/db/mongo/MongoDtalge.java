/*
 * @(#)MongoDtalge.java	0.5.0	2019/02/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.db.mongo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
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
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import dtalge.DtBase;
import dtalge.DtBasePattern;
import dtalge.DtBasePatternSet;
import dtalge.DtBaseSet;
import dtalge.DtStringThesaurus;
import dtalge.Dtalge;
import dtalge.db.BigDtBaseSet;
import dtalge.db.BigDtalge;
import dtalge.db.BigDtalgeElement;
import dtalge.exception.CsvFormatException;
import dtalge.exception.DtBaseNotFoundException;
import dtalge.exception.IllegalValueOfDataTypeException;
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
 * MongoDB 上で、データ代数の基底と値を保持するデータ代数クラス。
 * 基本的に {@link dtalge.Dtalge} と同様にインタフェースを提供する。
 * ただし、順序については不定である。
 * <p>このクラスは、基本的に不変オブジェクト(Immutable)として扱うことが可能なインタフェースと、
 * 破壊型(Mutable)としてのインタフェースを提供する。
 * ただし、外部からデータベースのコレクション変更は関知しない。
 * <br>
 * また、<b>この実装は同期化されない</b>。
 * <p>
 * <b>《入出力フォーマット》</b>
 * <br>
 * <code>MongoDtalge</code> の MongoDB におけるデータ構造は、次の通りである。
 * <pre><code>
 * {
 *   &quot;base&quot; {
 *     &quot;name&quot; : &lt;string&gt;
 *     &quot;type&quot; : &lt;string&gt;
 *     &quot;attr&quot; : &lt;string&gt;
 *     &quot;subject&quot; : &lt;string&gt;
 *   },
 *   &quot;value&quot; : &lt;null or BigDecimal or boolean or String&gt;
 * }
 * </code></pre>
 * 
 * @version 0.5.0
 * @since 0.5.0
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 *
 */
public class MongoDtalge implements BigDtalge
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	/** 一時的なコレクション名のプレフィックス **/
	static public final String	TEMP_COLNAME_PREFIX	= "TDtal";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	protected MongoSession				_mongo_session;
	protected MongoCollection<Document>	_mongo_col;
	protected AtomicLong				_modCount = new AtomicLong(0L);
	/** データ代数集合のデータ代数元 ID、未定義の場合は <tt>null</tt> **/
	protected final String				_elem_id;
	protected final AtomicLong			_modDtAlgeSetCount;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定された MongoDB セッションにおいて、一時的なコレクションをストレージとする新しいインスタンスを生成する。
	 * <p>このメソッドで作成されたコレクションは、MongoDB セッションが切断されるときに破棄される。
	 * @param session	MongoDB セッションオブジェクト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public MongoDtalge(MongoSession session) {
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
	public MongoDtalge(MongoSession session, String collection) {
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
		_elem_id = null;
		_modDtAlgeSetCount = null;
	}

	/**
	 * 指定されたデータ代数集合の要素を操作対象とする新しいインスタンスを生成する。
	 * <p>このメソッドでは、指定されたコレクションを一時コレクションとして登録しない。
	 * @param algeset	対象とするデータ代数集合
	 * @param elem_id	対象とするデータ代数元 ID
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>elem_id</em> が空文字列の場合
	 */
	MongoDtalge(MongoDtAlgeSet algeset, String elem_id) {
		_mongo_session = algeset._mongo_session;
		_mongo_col     = algeset._mongo_vals_col;
		_modDtAlgeSetCount = algeset._modCount;
		if (elem_id.isEmpty())
			throw new IllegalArgumentException("'elem_id' is empty!");
		_elem_id = elem_id;
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
		if (_elem_id != null) {
			// データ代数集合の要素であれば、新しいコレクションにコピー
			ArrayList<Bson> list = new ArrayList<Bson>();
			list.add(Aggregates.match(new Document("$" + MongoDelegateDocDtAlgeSetElem.MONGO_KEY_SETID, _elem_id)));
			list.add(Aggregates.project(new Document("_id", 0).append(MongoDelegateDocDtAlgeSetElem.MONGO_KEY_SETID, 0)));
			list.add(Aggregates.out(newCollectionName));
			_mongo_col.aggregate(list).toCollection();	// toCollection() を呼ばないとコレクションに出力されない
		}
		else {
			// 単独のデータ代数元であれば、コレクション名を変更
			try {
				_mongo_col.renameCollection(newName);
			}
			catch (MongoException ex) {
				throw new MongoAlgeError("Failed to persist of MongoDtalge" + _mongo_col.getNamespace().toString() + ":", ex);
			}
			//--- 一時的コレクションから除外
			_mongo_session.unregisterTemporaryCollection(oldName);
		}
		_mongo_col = _mongo_session.getPersistentCollection(newCollectionName);
	}
	
	/**
	 * このオブジェクトがデータ代数集合のビューの場合、データ代数集合上のデータ代数元 ID を返す。
	 * @return	データ代数元 ID、ビューでない場合は <tt>null</tt>
	 */
	public String getElementIdOfAlgeSet() {
		return _elem_id;
	}
	
	/**
	 * このオブジェクトが管理するストレージが、データ代数集合のものかどうかを判定する。
	 * データ代数集合のものである場合、このオブジェクトに対する破壊型メソッドの呼び出しでは {@link java.lang.UnsupportedOperationException} がスローされる。
	 * @return	データ代数集合の要素であれば <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean isView() {
		return (_elem_id != null);
	}
	
	/**
	 * このオブジェクトが管理するストレージを削除する。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このメソッドは、このオブジェクトが管理するストレージが一時的なものかどうかに関係なく、
	 * 管理対象のストレージを削除するので、注意すること。
	 * なお、このメソッドを呼び出した後、このオブジェクトを利用するとデータが空の状態となる。
	 * </blockquote>
	 * @throws UnsupportedOperationException	このオブジェクトがビューの場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合、指定されたコレクション名がすでに存在している場合
	 */
	@Override
	public void delete() {
		ensureNotView();
		// 単独のストレージ
		try {
			_mongo_col.drop();
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to drop collection of MongoDtalge" + _mongo_col.getNamespace().toString() + ":", ex);
		}
		//_mongo_session.unregisterTemporaryCollection(_mongo_col.getNamespace());
	}

	/**
	 * データ代数元の要素が空であることを示す。
	 * 
	 * @return 要素が一つも存在しない場合に true を返す。
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean isEmpty() {
		return (getNumElements() == 0L);
	}
	
	/**
	 * データ代数元の要素数を返す。
	 * 
	 * @return	データ代数元の要素数
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public long getNumElements() {
		try {
			return _mongo_col.countDocuments(makeQeuryAll());
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to count documents in MongoDtalge" + _mongo_col.getNamespace().toString() + ":", ex);
		}
	}

	/**
	 * 指定された基底が、このインスタンスの要素に含まれているかを示す。
	 * 
	 * @param base データ代数基底
	 * @return 指定の基底が要素に含まれている場合に <tt>true</tt> を返す。
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean containsBase(DtBase base) {
		if (base == null)
			return false;
		try {
			return (_mongo_col.countDocuments(makeQueryByDtBase(base)) > 0L);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to contain DtBase in MongoDtalge" + _mongo_col.getNamespace().toString() + ":", ex);
		}
	}

	/**
	 * 指定された基底が、このインスタンスの要素に全て含まれているかを示す。
	 * <p>このメソッドは、指定された基底集合に含まれるすべての基底が、この
	 * データ代数元の要素に存在した場合のみ <tt>true</tt> を返す。
	 * 指定された基底集合の要素が空の場合は、<tt>false</tt> を返す。
	 * 
	 * @param bases	検証するデータ代数基底の集合
	 * @return	すべての基底が含まれている場合に <tt>true</tt> を返す。
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean containsAllBases(DtBaseSet bases) {
		if (bases.isEmpty() || this.isEmpty())
			return false;
		for (DtBase base : bases) {
			if (!containsBase(base))
				return false;
		}
		return true;
	}

	/**
	 * 指定された基底が、このインスタンスの要素に全て含まれているかを示す。
	 * <p>このメソッドは、指定された基底集合に含まれるすべての基底が、この
	 * データ代数元の要素に存在した場合のみ <tt>true</tt> を返す。
	 * 指定された基底集合の要素が空の場合は、<tt>false</tt> を返す。
	 * 
	 * @param bases	検証するデータ代数基底の集合
	 * @return	すべての基底が含まれている場合に <tt>true</tt> を返す。
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean containsAllBases(BigDtBaseSet bases) {
		if (bases.isEmpty() || this.isEmpty())
			return false;
		for (DtBase base : bases) {
			if (!containsBase(base))
				return false;
		}
		return true;
	}

	/**
	 * 指定された基底のどれか 1 つが、このインスタンスの要素に含まれているかを示す。
	 * <p>このメソッドは、指定された基底集合に含まれるどれか 1 つが、この
	 * データ代数元の要素に存在した場合に <tt>true</tt> を返す。
	 * 指定された基底集合の要素が空の場合は、<tt>false</tt> を返す。
	 * 
	 * @param bases	検証するデータ代数基底の集合
	 * @return	指定した基底集合のどれか 1 つが含まれている場合に <tt>true</tt> を返す。
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean containsAnyBases(DtBaseSet bases) {
		if (!bases.isEmpty() && !this.isEmpty()) {
			if (this.getNumElements() < bases.size()) {
				MongoCursor<? extends Document> cursor = null;
				try {
					cursor = findAllDocuments(true, true).iterator();
					while (cursor.hasNext()) {
						DtBase thisBase = MongoDelegateDocDtalgeElem.toDtBaseFromDocument(cursor.next());
						if (thisBase != null && bases.contains(thisBase)) {
							return true;
						}
					}
				}
				catch (MongoException ex) {
					throw new MongoAlgeError("Failed to get document from MongoDtalge" + _mongo_col.getNamespace().toString() + ":", ex);
				}
				finally {
					MongoUtil.closeCursorSilent(cursor);
				}
			} else {
				for (DtBase base : bases) {
					if (containsBase(base)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 指定された基底のどれか 1 つが、このインスタンスの要素に含まれているかを示す。
	 * <p>このメソッドは、指定された基底集合に含まれるどれか 1 つが、この
	 * データ代数元の要素に存在した場合に <tt>true</tt> を返す。
	 * 指定された基底集合の要素が空の場合は、<tt>false</tt> を返す。
	 * 
	 * @param bases	検証するデータ代数基底の集合
	 * @return	指定した基底集合のどれか 1 つが含まれている場合に <tt>true</tt> を返す。
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean containsAnyBases(BigDtBaseSet bases) {
		if (!bases.isEmpty() && !this.isEmpty()) {
			if (this.getNumElements() < bases.size()) {
				MongoCursor<? extends Document> cursor = null;
				try {
					cursor = findAllDocuments(true, true).iterator();
					while (cursor.hasNext()) {
						DtBase thisBase = MongoDelegateDocDtalgeElem.toDtBaseFromDocument(cursor.next());
						if (thisBase != null && bases.contains(thisBase)) {
							return true;
						}
					}
				}
				catch (MongoException ex) {
					throw new MongoAlgeError("Failed to get document from MongoDtalge" + _mongo_col.getNamespace().toString() + ":", ex);
				}
				finally {
					MongoUtil.closeCursorSilent(cursor);
				}
			} else {
				for (DtBase base : bases) {
					if (containsBase(base)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 指定された値が、このインスタンスの要素に含まれているかを示す。
	 * 
	 * @param value	データ代数の値
	 * @return	指定の値が含まれている場合に <tt>true</tt> を返す。
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean containsValue(Object value) {
		try {
			long num = _mongo_col.countDocuments(makeQueryByValue(value));
			return (num > 0L);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to find document in MongoDtalge" + _mongo_col.getNamespace().toString() + ":", ex);
		}
	}

	/**
	 * このインスタンスの要素に <tt>null</tt> 値が含まれている場合に <tt>true</tt> を返す。
	 * @return	このインスタンスの要素に <tt>null</tt> 値が含まれている場合は <tt>true</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean containsNull() {
		return containsValue(null);
	}

	/**
	 * 指定されたコレクションに含まれる全ての値が、このインスタンスの要素に
	 * 含まれている場合に <tt>true</tt> を返す。
	 * <em>values</em> が <tt>null</tt> もしくは空の場合は、<tt>false</tt> を返す。
	 * @param values	検証する値のコレクション
	 * @return	指定されたコレクションに含まれる全ての値が、このインスタンスの要素に含まれている場合は <tt>true</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean containsAllValues(Collection<?> values) {
		if (values == null || values.isEmpty() || this.isEmpty()) {
			return false;
		}
		
		for (Object value : values) {
			if (!containsValue(value)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 指定されたコレクションに含まれる値のどれか 1 つが、
	 * このインスタンスの要素に含まれている場合に <tt>true</tt> を返す。
	 * <em>values</em> が <tt>null</tt> もしくは空の場合は、<tt>false</tt> を返す。
	 * @param values	検証する値のコレクション
	 * @return	指定されたコレクションに含まれる値のどれか 1 つが、このインスタンスの要素に含まれている場合は <tt>true</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean containsAnyValues(Collection<?> values) {
		if (values != null && !values.isEmpty() && !this.isEmpty()) {
			for (Object value : values) {
				if (containsValue(value)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * このオブジェクトのデータ代数要素にアクセスする変更不可能なイテレーターを取得する。
	 * このイテレーターが返す順序は、データベースの実装に依存する。
	 * @return	このオブジェクトに含まれるデータ代数要素の変更不可能なイテレーター
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public BigIterator<BigDtalgeElement> elementIterator() {
		return newUnmodifiableNaturalOrderedElementIterator();
	}
	
	/**
	 * このオブジェクトのデータ代数要素にアクセスする変更不可能なイテレーターを取得する。
	 * このイテレーターが返す順序は、基底キーと値の順に昇順ソートされたものとなる。
	 * @return	このオブジェクトに含まれるデータ代数要素の変更不可能なイテレーター
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public BigIterator<BigDtalgeElement> sortedElementIterator() {
		return newUnmodifiableSortedElementIterator();
	}

	/**
	 * このオブジェクトデータ代数基底にアクセスする変更不可能なイテレーターを取得する。
	 * このイテレーターが返す順序は、基底キーの順に昇順ソートされたものとなる。
	 * @return	このオブジェクトに含まれるデータ代数基底の変更不可能なイテレーター
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public BigIterator<DtBase> dtbaseIterator() {
		return newUnmodifiableSortedDtBaseIterator();
	}

	/**
	 * <code>Dtalge</code> の要素の変更不可能な反復子を返す。
	 * このイテレーターが返す順序は、データベースの実装に依存する。
	 * 
	 * @return データ代数要素の <code>Iterator</code>
	 * 
	 * @see ConcurrentModificationException
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public BigIterator<Dtalge> iterator() {
		return newUnmodifiableNaturalOrderedDtalgeIterator();
	}

	/**
	 * このデータ代数元の先頭に位置する要素の基底を取得する。
	 * 要素のコレクションの先頭は、このクラスのコレクションの実装に依存するため、
	 * 要素が 1 つしかないデータ代数元から要素を取得する場合に有効である。
	 * 
	 * @return	データ代数元の先頭に位置する要素の基底を返す。
	 * 
	 * @throws java.util.NoSuchElementException	要素が存在しない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public DtBase getOneBase() {
		Document doc = findAllDocuments(true, true).first();
		if (doc == null)
			throw new NoSuchElementException();
		return MongoDelegateDocDtalgeElem.toDtBaseFromDocument(doc);
	}
	
	/**
	 * このデータ代数元の先頭に位置する要素の値を取得する。
	 * 要素のコレクションの先頭は、このクラスのコレクションの実装に依存するため、
	 * 要素が 1 つしかないデータ代数元から要素を取得する場合に有効である。
	 * 
	 * @return	データ代数元の先頭に位置する要素の値を返す。
	 * 
	 * @throws NoSuchElementException	このデータ代数元に要素が存在しない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public Object getOneValue() {
		Document doc = findAllDocuments(true, true).first();
		if (doc == null)
			throw new NoSuchElementException();
		return MongoDelegateDocDtalgeElem.getValueObjectFromDocument(doc);
	}
	
	/**
	 * このデータ代数元に含まれる全ての基底を取り出す。
	 * 
	 * @return このデータ代数元に含まれる全ての基底の集合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoDtBaseSet getBases() {
		MongoDtBaseSet retBases = new MongoDtBaseSet(_mongo_session);
		if (!isEmpty()) {
			MongoCursor<? extends Document> cursor = null;
			try {
				cursor = distinctByDtBase().iterator();
				while (cursor.hasNext()) {
					Document docDtBaseElem = cursor.next();
					DtBase base = MongoDelegateDocDtBase.toDtBase(docDtBaseElem);
					retBases.add(base);
				}
			}
			catch (MongoException ex) {
				throw new MongoAlgeError("Failed to get all DtBases into new Collection from MongoDtalge" + _mongo_col.getNamespace().toString() + ":", ex);
			}
			finally {
				MongoUtil.closeCursorSilent(cursor);
			}
		}
		return retBases;
	}

	/**
	 * このデータ代数元に含まれる基底のうち、指定されたパターンに一致する
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
		MongoDtBaseSet retBases = new MongoDtBaseSet(_mongo_session);
		if (!isEmpty()) {
			MongoCursor<? extends Document> cursor = null;
			try {
				cursor = distinctByDtBase().iterator();
				while (cursor.hasNext()) {
					Document docDtBaseElem = cursor.next();
					DtBase base = MongoDelegateDocDtBase.toDtBase(docDtBaseElem);
					// マッチする基底のみの集合を生成
					if (pattern.matches(base)) {
						retBases.add(base);
					}
				}
			}
			catch (MongoException ex) {
				throw new MongoAlgeError("Failed to get matched DtBases into new Collection from MongoDtalge" + _mongo_col.getNamespace().toString() + ":", ex);
			}
			finally {
				MongoUtil.closeCursorSilent(cursor);
			}
		}
		return retBases;
	}

	/**
	 * このデータ代数元に含まれる基底のうち、指定されたパターンに一致する
	 * 基底のみを格納する基底集合を返す。
	 * <br>
	 * このメソッドが返す基底集合には、指定された基底パターン集合に含まれる
	 * 基底パターンのどれかに一致した基底が含まれる。
	 * <br>
	 * 基底パターンに一致する基底が存在しない場合、もしくは、データ代数元が
	 * 要素を持たない場合、このメソッドは空の基底集合を返す。
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
		MongoDtBaseSet retBases = new MongoDtBaseSet(_mongo_session);
		if (!patterns.isEmpty() && !isEmpty()) {
			MongoCursor<? extends Document> cursor = null;
			try {
				cursor = distinctByDtBase().iterator();
				while (cursor.hasNext()) {
					Document docDtBaseElem = cursor.next();
					DtBase base = MongoDelegateDocDtBase.toDtBase(docDtBaseElem);
					// マッチする基底のみの集合を生成
					if (patterns.matches(base)) {
						retBases.add(base);
					}
				}
			}
			catch (MongoException ex) {
				throw new MongoAlgeError("Failed to get matched DtBases into new Collection from MongoDtalge" + _mongo_col.getNamespace().toString() + ":", ex);
			}
			finally {
				MongoUtil.closeCursorSilent(cursor);
			}
		}
		return retBases;
	}

	/**
	 * 指定された基底に対応する値を取り出す。
	 * 要素に含まれていない基底を指定した場合、このメソッドは例外をスローする。
	 * 
	 * @param base	取り出す値の基底
	 * @return	基底に対応する値
	 * 
	 * @throws NullPointerException 指定された基底が <tt>null</tt> の場合
	 * @throws DtBaseNotFoundException 指定された基底が要素に含まれていない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public Object get(DtBase base) {
		Validations.validNotNull(base, "'base' argument cannot be null.");
		Document doc = findByDtBase(base, true, true).first();
		if (doc == null) {
			throw new DtBaseNotFoundException(base.toString() + " not exist.");
		} else {
			return MongoDelegateDocDtalgeElem.getValueObjectFromDocument(doc);
		}
	}
	
	/**
	 * 指定された基底に対応する値を、指定のデータ型として取り出す。
	 * 指定された基底のデータ型キーと指定のデータ型が異なる場合や、
	 * 要素に含まれていない基底を指定した場合は、このメソッドは例外をスローする。
	 * 
	 * @param base	取り出す値の基底
	 * @return	基底に対応する値
	 * 
	 * @throws NullPointerException	指定された基底が <tt>null</tt> の場合
	 * @throws DtBaseNotFoundException	指定された基底が要素に含まれていない場合
	 * @throws IllegalValueOfDataTypeException	指定された基底のデータ型キーと指定のデータ型が異なる場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	protected Object getByType(DtBase base, String typeName, Class<?> reqType) {
		Object val = get(base);
		if (!typeName.equals(base.getTypeKey())) {
			// illegal data type
			String msg = String.format("DtBase(%s) Type key is not %s.",
										base.toString(), typeName);
			throw new IllegalValueOfDataTypeException(typeName, reqType, val, msg);
		}
		return val;
	}

	/**
	 * 指定された基底に対応する値を、真偽値として取り出す。
	 * 指定された基底のデータ型キーが真偽値型ではない場合や、
	 * 要素に含まれていない基底を指定した場合は、このメソッドは例外をスローする。
	 * 
	 * @param base	取り出す値の基底
	 * @return	基底に対応する値
	 * 
	 * @throws NullPointerException	指定された基底が <tt>null</tt> の場合
	 * @throws DtBaseNotFoundException	指定された基底が要素に含まれていない場合
	 * @throws IllegalValueOfDataTypeException	指定された基底のデータ型キーが真偽値型ではない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public Boolean getBoolean(DtBase base) {
		return ((Boolean)getByType(base, DtDataTypes.BOOLEAN, Boolean.class));
	}

	/**
	 * 指定された基底に対応する値を、文字列型として取り出す。
	 * 指定された基底のデータ型キーが文字列型ではない場合や、
	 * 要素に含まれていない基底を指定した場合は、このメソッドは例外をスローする。
	 * 
	 * @param base	取り出す値の基底
	 * @return	基底に対応する値
	 * 
	 * @throws NullPointerException	指定された基底が <tt>null</tt> の場合
	 * @throws DtBaseNotFoundException	指定された基底が要素に含まれていない場合
	 * @throws IllegalValueOfDataTypeException	指定された基底のデータ型キーが文字列型ではない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public String getString(DtBase base) {
		return ((String)getByType(base, DtDataTypes.STRING, String.class));
	}
	
	/**
	 * 指定された基底に対応する値を、実数値として取り出す。
	 * 指定された基底のデータ型キーが実数値型ではない場合や、
	 * 要素に含まれていない基底を指定した場合は、このメソッドは例外をスローする。
	 * 
	 * @param base	取り出す値の基底
	 * @return	基底に対応する値
	 * 
	 * @throws NullPointerException	指定された基底が <tt>null</tt> の場合
	 * @throws DtBaseNotFoundException	指定された基底が要素に含まれていない場合
	 * @throws IllegalValueOfDataTypeException	指定された基底のデータ型キーが実数値型ではない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public BigDecimal getDecimal(DtBase base) {
		return ((BigDecimal)getByType(base, DtDataTypes.DECIMAL, BigDecimal.class));
	}

	/**
	 * 指定された基底と値が代入された、<code>MongoDtalge</code> の新しいインスタンスを返す。
	 * <p>
	 * 指定した基底が存在していない場合、データ代数元の基底と値のマップ終端に追加される。
	 * すでに同一基底が存在する場合、指定された値で上書きされる。
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @param base	データ代数の基底
	 * @param value	データ代数の値
	 * 
	 * @return	代入後のデータ代数元の新しいインスタンス
	 * 
	 * @throws NullPointerException	指定された基底が <tt>null</tt> の場合
	 * @throws IllegalValueOfDataTypeException	指定された値が基底のデータ型と異なる場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoDtalge put(DtBase base, Object value) {
		Validations.validNotNull(base, "'base' argument cannot be null.");
		MongoDtalge newAlge = copy();
		newAlge.putValue(base, value);
		return newAlge;
	}

	/**
	 * 指定されたデータ代数元のすべての要素が代入された、<code>MongoDtalge</code> の新しい
	 * インスタンスを返す。
	 * <p>
	 * 指定した基底が存在していない場合、データ代数元の基底と値のマップ終端に追加される。
	 * すでに同一基底が存在する場合、指定されたデータ代数元の値で上書きされる。
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @param alge	代入するデータ代数元
	 * @return	代入後のデータ代数元の新しいインスタンス
	 * 
	 * @throws NullPointerException	指定された基底が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoDtalge put(Dtalge alge) {
		Validations.validNotNull(alge, "'alge' argument cannot be null.");
		MongoDtalge newAlge = copy();
		newAlge.putValues(alge);
		return newAlge;
	}

	/**
	 * 指定されたデータ代数元のすべての要素が代入された、<code>MongoDtalge</code> の新しい
	 * インスタンスを返す。
	 * <p>
	 * 指定した基底が存在していない場合、データ代数元の基底と値のマップ終端に追加される。
	 * すでに同一基底が存在する場合、指定されたデータ代数元の値で上書きされる。
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @param alge	代入するデータ代数元
	 * @return	代入後のデータ代数元の新しいインスタンス
	 * 
	 * @throws NullPointerException	指定された基底が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoDtalge put(BigDtalge alge) {
		Validations.validNotNull(alge, "'alge' argument cannot be null.");
		MongoDtalge newAlge = copy();
		BigIterator<BigDtalgeElement> it = alge.elementIterator();
		try {
			while (it.hasNext()) {
				BigDtalgeElement elem = it.next();
				newAlge.putValue(elem.getBase(), elem.getValue());
			}
		}
		finally {
			it.closeCursor();
		}
		return newAlge;
	}

	/**
	 * 指定された基底と値を、このデータ代数に加算する。
	 * <p>
	 * 指定した基底が存在していない場合、データ代数元の基底と値のマップ終端に追加される。
	 * すでに同一基底が存在する場合、指定された値で上書きされる。この場合、
	 * 基底の順序は影響を受けない。
	 * <p>
	 * (注)このメソッドは、インスタンスの値を書き換える。
	 * 
	 * @param base データ代数の基底
	 * @param value 値
	 * @return このオブジェクト
	 * @throws NullPointerException	指定された基底が <tt>null</tt> の場合
	 * @throws IllegalValueOfDataTypeException	指定された値が基底のデータ型と異なる場合
	 * @throws UnsupportedOperationException	このオブジェクトがビューの場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoDtalge add(DtBase base, Object value) {
		ensureNotView();
		putValue(base, value);
		return this;
	}
	
	/**
	 * 指定されたデータ代数元のすべての要素を、このデータ代数に加算する。
	 * <p>
	 * 指定した基底が存在していない場合、データ代数元の基底と値のマップ終端に追加される。
	 * すでに同一基底が存在する場合、指定された値で上書きされる。この場合、
	 * 基底の順序は影響を受けない。
	 * <p>
	 * (注)このメソッドは、インスタンスの値を書き換える。
	 * 
	 * @param alge	代入するデータ代数元
	 * @return このオブジェクト
	 * @throws NullPointerException	指定された基底が <tt>null</tt> の場合
	 * @throws UnsupportedOperationException	このオブジェクトがビューの場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoDtalge add(Dtalge alge) {
		Validations.validNotNull(alge, "'alge' argument cannot be null.");
		ensureNotView();
		putValues(alge);
		return this;
	}
	
	/**
	 * 指定されたデータ代数元のすべての要素を、このデータ代数に加算する。
	 * <p>
	 * 指定した基底が存在していない場合、データ代数元の基底と値のマップ終端に追加される。
	 * すでに同一基底が存在する場合、指定された値で上書きされる。この場合、
	 * 基底の順序は影響を受けない。
	 * <p>
	 * (注)このメソッドは、インスタンスの値を書き換える。
	 * 
	 * @param alge	代入するデータ代数元
	 * @return このオブジェクト
	 * @throws NullPointerException	指定された基底が <tt>null</tt> の場合
	 * @throws UnsupportedOperationException	このオブジェクトがビューの場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoDtalge add(BigDtalge alge) {
		ensureNotView();
		BigIterator<BigDtalgeElement> it = alge.elementIterator();
		try {
			while (it.hasNext()) {
				BigDtalgeElement elem = it.next();
				putValue(elem.getBase(), elem.getValue());
			}
		}
		finally {
			it.closeCursor();
		}
		return this;
	}

	/**
	 * データ代数元の要素の値が <tt>null</tt> のものを除外した、<code>MongoDtalge</code> の
	 * 新しいインスタンスを返す。
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @return	値が <tt>null</tt> の要素を除外した <code>Dtalge</code> インスタンス
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public MongoDtalge normalization() {
		MongoDtalge newAlge;
		if (!containsNull()) {
			// null value not exist
			newAlge = copy();
		}
		else {
			// includes null values
			newAlge = new MongoDtalge(_mongo_session);
			MongoCursor<? extends Document> cursor = null;
			try {
				cursor = findWithoutValue(null, true, true).iterator();
				while (cursor.hasNext()) {
					newAlge._mongo_col.insertOne(cursor.next());
				}
			}
			catch (MongoException ex) {
				throw new MongoAlgeError("Failed to create normalized MongoDtalge" + _mongo_col.getNamespace().toString() + ":", ex);
			}
			finally {
				MongoUtil.closeCursorSilent(cursor);
			}
		}
		return newAlge;
	}

	/**
	 * このデータ代数元から、要素の値が <tt>null</tt> のものを削除する。
	 * @return	このデータ代数元の内容が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws UnsupportedOperationException	このオブジェクトがビューの場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public boolean normalize() {
		ensureNotView();
		if (!containsNull())
			return false;	// no Null value
		try {
			DeleteResult ret = _mongo_col.deleteMany(makeQueryByValue(null));
			return (ret.getDeletedCount() > 0L);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to normalize MongoDtalge" + _mongo_col.getNamespace().toString() + ":", ex);
		}
	}

	/**
	 * Dtalgeの複製を生成する。
	 * 
	 * @return 複製されたDtalge
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoDtalge copy() {
		MongoDtalge newAlge = new MongoDtalge(_mongo_session);
		if (_elem_id != null) {
			// データ代数集合の要素
			MongoCursor<? extends Document> cursor = null;
			try {
				cursor = findAllDocuments(true, true).iterator();
				while (cursor.hasNext()) {
					newAlge._mongo_col.insertOne(cursor.next());
				}
			}
			catch (MongoException ex) {
				throw new MongoAlgeError("Failed to copy MongoDtalge" + _mongo_col.getNamespace().toString() + ":", ex);
			}
			finally {
				MongoUtil.closeCursorSilent(cursor);
			}
		}
		else {
			// 単独のデータ代数元
			MongoUtil.duplicateCollection(newAlge._mongo_col, _mongo_col);
		}
		return newAlge;
	}

	/**
	 * 2 つのデータ代数元が同値であるかを検証する。
	 * <p>このメソッドはインスタンスの同一性ではなく、同値性を評価する。
	 * <br>
	 * 2 つのデータ代数元の関係が次のような場合に同値とみなす。
	 * <ul>
	 * <li>同一の基底が存在する。
	 * <li>同一(等しい)基底に対応する値が全て同値である。
	 * </ul>
	 * なお、基底に対応する値の同値性は、{@link Object#equals(Object)} メソッドの
	 * 実行結果によるものとする。
	 * ただし、値が実数値の場合は {@link java.math.BigDecimal#compareTo(BigDecimal)} の
	 * 結果が 0 となる場合を同値とする。
	 * <p>
	 * 現在の実装において、このメソッドにより同等と判定された場合でも、
	 * このオブジェクトが返すハッシュコード値と <code>alge.hashCode()</code> の値が一致するとは限らない。
	 * 
	 * @param alge 同値性を比較する対象のデータ代数
	 * 
	 * @return 2つのデータ代数が同値である場合は <tt>true</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean isSameValues(Dtalge alge) {
		if (alge == null) {
			return false;
		}
		
		// 要素数の比較
		long numCol1 = this.getNumElements();
		long numCol2 = alge.getNumElements();
		if (numCol1 != numCol2)
			return false;
		else if (numCol1 == 0L)
			return true;	// 要素がどちらも空の場合は、同値とみなす
		
		// 要素の比較
		MongoCursor<? extends Document> cursor = findAllDocuments(true, true).iterator();
		try {
			while (cursor.hasNext()) {
				Document thisDoc   = cursor.next();
				DtBase   thisBase  = MongoDelegateDocDtalgeElem.toDtBaseFromDocument(thisDoc);
				Object   thisValue = MongoDelegateDocDtalgeElem.getValueObjectFromDocument(cursor.next());
				if (!alge.containsBase(thisBase))
					return false;
				Object thatValue = alge.get(thisBase);
				if (thisValue != thatValue) {
					// not same instances
					if (thisValue == null || thatValue == null) {
						// null value of which one
						return false;
					}
					else if (thisValue instanceof BigDecimal) {
						if (!(thatValue instanceof BigDecimal)) {
							// thatValue is not BigDecimal instance
							return false;
						}
						else if (0 != ((BigDecimal)thisValue).compareTo((BigDecimal)thatValue)) {
							// thisValue != thatValue (BigDecimal#compareTo)
							return false;
						}
					}
					else if (!thisValue.equals(thatValue)) {
						// not equals objects
						return false;
					}
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to judge as same between Dtalge and MongoDtalge" + _mongo_col.getNamespace().toString() + ":", ex);
		}
		finally {
			try {
				cursor.close();
			} catch (Throwable ignoreEx) {}
		}
		
		// 全て同値
		return true;
	}

	/**
	 * 2 つのデータ代数元が同値であるかを検証する。
	 * <p>このメソッドはインスタンスの同一性ではなく、同値性を評価する。
	 * <br>
	 * 2 つのデータ代数元の関係が次のような場合に同値とみなす。
	 * <ul>
	 * <li>同一の基底が存在する。
	 * <li>同一(等しい)基底に対応する値が全て同値である。
	 * </ul>
	 * なお、基底に対応する値の同値性は、{@link Object#equals(Object)} メソッドの
	 * 実行結果によるものとする。
	 * ただし、値が実数値の場合は {@link java.math.BigDecimal#compareTo(BigDecimal)} の
	 * 結果が 0 となる場合を同値とする。
	 * <p>
	 * 現在の実装において、このメソッドにより同等と判定された場合でも、
	 * このオブジェクトが返すハッシュコード値と <code>alge.hashCode()</code> の値が一致するとは限らない。
	 * 
	 * @param alge 同値性を比較する対象のデータ代数
	 * 
	 * @return 2つのデータ代数が同値である場合は <tt>true</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean isSameValues(BigDtalge alge) {
		if (alge == null) {
			return false;
		}
		
		// 要素数の比較
		long numCol1 = this.getNumElements();
		long numCol2 = alge.getNumElements();
		if (numCol1 != numCol2)
			return false;
		else if (numCol1 == 0L)
			return true;	// 要素がどちらも空の場合は、同値とみなす
		
		// 要素の比較
		MongoCursor<? extends Document> cursor = findAllDocuments(true, true).iterator();
		try {
			while (cursor.hasNext()) {
				Document thisDoc   = cursor.next();
				DtBase   thisBase  = MongoDelegateDocDtalgeElem.toDtBaseFromDocument(thisDoc);
				Object   thisValue = MongoDelegateDocDtalgeElem.getValueObjectFromDocument(cursor.next());
				if (!alge.containsBase(thisBase))
					return false;
				Object thatValue = alge.get(thisBase);
				if (thisValue != thatValue) {
					// not same instances
					if (thisValue == null || thatValue == null) {
						// null value of which one
						return false;
					}
					else if (thisValue instanceof BigDecimal) {
						if (!(thatValue instanceof BigDecimal)) {
							// thatValue is not BigDecimal instance
							return false;
						}
						else if (0 != ((BigDecimal)thisValue).compareTo((BigDecimal)thatValue)) {
							// thisValue != thatValue (BigDecimal#compareTo)
							return false;
						}
					}
					else if (!thisValue.equals(thatValue)) {
						// not equals objects
						return false;
					}
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to judge as same between BigDtalge and MongoDtalge" + _mongo_col.getNamespace().toString() + ":", ex);
		}
		finally {
			try {
				cursor.close();
			} catch (Throwable ignoreEx) {}
		}
		
		// 全て同値
		return true;
	}

	/**
	 * このインスタンスのハッシュ値を返す。
	 * 
	 * @return	ハッシュ値
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public int hashCode() {
		int h = 0;
		BigIterator<BigDtalgeElement> it = sortedElementIterator();
		try {
			while (it.hasNext()) {
				BigDtalgeElement elem = it.next();
				//--- キー(DtBase)のハッシュ値は、DtBase#hashCode() の値をそのまま利用する。
				int hk = (elem.getBase() == null ? 0 : elem.getBase().hashCode());
				//--- 値が BigDecimal の場合、BigDecimal#stripTrailingZeros() によって下位の桁の
				//--- 0(余分な0)を消去した後の BigDecimal#hashCode() の値とする。値が 0 の場合は
				//--- 強制的に 0 とする。
				//--- 値が BigDecimal 以外の場合は、Object#hashCode() の値をそのまま利用する。
				int hv = 0;
				Object val = elem.getValue();
				if (val instanceof BigDecimal) {
					BigDecimal dv = (BigDecimal)val;
					if (0!=BigDecimal.ZERO.compareTo(dv)) {
						hv = dv.stripTrailingZeros().hashCode();
					}
				} else if (val != null) {
					hv = val.hashCode();
				}
				h += (hk ^ hv);
			}
		}
		finally {
			it.closeCursor();
		}
		return h;
	}

	/**
	 * 2 つのデータ代数元が同値であるかを検証する。
	 * <p>このメソッドはインスタンスの同一性ではなく、同値性を評価する。
	 * <br>
	 * 2 つのデータ代数元の関係が次のような場合に同値とみなす。
	 * <ul>
	 * <li>同一の基底が存在する。
	 * <li>同一(等しい)基底に対応する値が全て同値である。
	 * </ul>
	 * なお、基底に対応する値の同値性は、{@link Object#equals(Object)} メソッドの
	 * 実行結果によるものとする。
	 * ただし、値が実数値の場合は {@link java.math.BigDecimal#compareTo(BigDecimal)} の
	 * 結果が 0 となる場合を同値とする。
	 * 
	 * @param obj	同値性を判定するオブジェクトの一方
	 * 
	 * @return 同値である場合に <tt>true</tt> を返す。
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			// same instance
			return true;
		}
		
		if (obj instanceof Dtalge) {
			return isSameValues((Dtalge)obj);
		}
		else if (obj instanceof BigDtalge) {
			return isSameValues((BigDtalge)obj);
		}
		
		return false;
	}
	
	/**
	 * このインスタンスの全要素を文字列として、10,000 要素まで出力する。
	 * <p>
	 * 出力形式は、次の通り。
	 * <blockquote>
	 * <i>値</i> <i>基底</i> '+' <i>値</i> <i>基底</i> '+' ...
	 * </blockquote>
	 * なお、要素が一つも存在しない場合、次のように出力される。
	 * <blockquote>
	 * ()
	 * </blockquote>
	 * 
	 * @see java.lang.Object#toString()
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		if (isEmpty()) {
			// empty
			sb.append("()");
		} else {
			long num = getNumElements();
			BigIterator<BigDtalgeElement> it = elementIterator();
			try {
				long cnt = 0L;
				Object value;
				if (it.hasNext()) {
					BigDtalgeElement elem = it.next();
					value = elem.getValue();
					if (value instanceof BigDecimal) {
						sb.append(((BigDecimal)value).stripTrailingZeros().toPlainString());
					} else {
						sb.append(value);
					}
					sb.append(elem.getBase().toString());
					++cnt;
				}
				for (; it.hasNext() && cnt < 10000; cnt++) {
					BigDtalgeElement elem = it.next();
					sb.append("+");
					value = elem.getValue();
					if (value instanceof BigDecimal) {
						sb.append(((BigDecimal)value).stripTrailingZeros().toPlainString());
					} else {
						sb.append(value);
					}
					sb.append(elem.getBase().toString());
				}
				if (it.hasNext()) {
					sb.append("+...too many elements(");
					sb.append(num);
					sb.append(" elements)");
				}
			}
			finally {
				it.closeCursor();
			}
		}
		
		return sb.toString();
	}

	//------------------------------------------------------------
	// Operations
	//------------------------------------------------------------

	/**
	 * 指定された値と等しい要素のみを取り出す。<br>
	 * 指定の値と等しい要素が存在しない場合は、
	 * 要素を持たない <code>MongoDtalge</code> の新しいインスタンスを返す。
	 * @param value	取り出す要素の値
	 * @return	指定された値と等しい要素のみを含む <code>MongoDtalge</code> の新しいインスタンス
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoDtalge oneValueProjection(Object value) {
		MongoDtalge newAlge = new MongoDtalge(_mongo_session);
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = findByValue(value, true, true).iterator();
			while (cursor.hasNext()) {
				newAlge._mongo_col.insertOne(cursor.next());
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to project by one value from MongoDtalge" + _mongo_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		return newAlge;
	}

	/**
	 * 指定されたコレクションに含まれる値と等しい要素のみを取り出す。<br>
	 * 指定の値と等しい要素が存在しない場合は、
	 * 要素を持たない <code>MongoDtalge</code> の新しいインスタンスを返す。
	 * @param values	取り出す要素の値のコレクション
	 * @return	指定されたコレクションに含まれる値と等しい要素のみを含む
	 * 			<code>MongoDtalge</code> の新しいインスタンス
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoDtalge valuesProjection(Collection<?> values) {
		MongoDtalge newAlge = new MongoDtalge(_mongo_session);
		if (!values.isEmpty() && !this.isEmpty()) {
			MongoCursor<? extends Document> cursor = null;
			try {
				cursor = queryDocuments(makeQueryByMultiValues(values, false), true, true).iterator();
				while (cursor.hasNext()) {
					newAlge._mongo_col.insertOne(cursor.next());
				}
			}
			catch (MongoException ex) {
				throw new MongoAlgeError("Failed to query by values from MongoDtalge" + _mongo_col.getNamespace().toString() + ":", ex);
			}
			finally {
				MongoUtil.closeCursorSilent(cursor);
			}
		}
		return newAlge;
	}

	/**
	 * 値が <tt>null</tt> の要素のみを取り出す。<br>
	 * 値が <tt>null</tt> の要素が存在しない場合は、
	 * 要素を持たない <code>MongoDtalge</code> の新しいインスタンスを返す。
	 * @return	値が <tt>null</tt> の要素のみを含む <code>MongoDtalge</code> の新しいインスタンス
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoDtalge nullProjection() {
		return oneValueProjection(null);
	}

	/**
	 * 値が <tt>null</tt> ではない要素のみを取り出す。<br>
	 * 値が <tt>null</tt> ではない要素が存在しない場合は、
	 * 要素を持たない <code>MongoDtalge</code> の新しいインスタンスを返す。
	 * @return	値が <tt>null</tt> ではない要素のみを含む <code>MongoDtalge</code> の新しいインスタンス
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoDtalge nonullProjection() {
		MongoDtalge newAlge = new MongoDtalge(_mongo_session);
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = queryDocuments(makeQueryWithoutValue(null), true, true).iterator();
			while (cursor.hasNext()) {
				newAlge._mongo_col.insertOne(cursor.next());
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to project by non null value from MongoDtalge" + _mongo_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		return newAlge;
	}
	
	/**
	 * このデータ代数元から、指定された基底と一致する要素のみを取り出し、
	 * その要素のみを持つ <code>MongoDtalge</code> の新しいインスタンスを返す。
	 * <br>
	 * 指定の基底が存在しない場合、要素を持たない <code>MongoDtalge</code> の新しいインスタンスを返す。
	 * <p>
	 * このメソッドは、次のデータ代数演算を行うものである。
	 * <blockquote>
	 * (return) = Proj[base](this)
	 * </blockquote>
	 * 
	 * @param base 基底
	 * 
	 * @return 取り出した基底の値のみを含む <code>MongoDtalge</code>の新しいインスタンス
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoDtalge projection(DtBase base) {
		MongoDtalge newAlge = new MongoDtalge(_mongo_session);
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = queryDocuments(makeQueryByDtBase(base), true, true).iterator();
			while (cursor.hasNext()) {
				newAlge._mongo_col.insertOne(cursor.next());
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to project by one DtBase from MongoDtalge" + _mongo_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		return newAlge;
	}
	
	/**
	 * このデータ代数元から、指定された基底と一致する要素のみを取り出し、
	 * その要素のみを持つ <code>Dtalge</code> の新しいインスタンスを返す。
	 * <br>
	 * 指定の基底が存在しない場合、要素を持たない <code>Dtalge</code> の新しいインスタンスを返す。
	 * <p>
	 * このメソッドは、次のデータ代数演算を行い、結果をメモリ上に保持するものである。
	 * <blockquote>
	 * (return) = Proj[base](this)
	 * </blockquote>
	 * 
	 * @param base 基底
	 * 
	 * @return 取り出した基底の値のみを含む <code>Dtalge</code>の新しいインスタンス
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public Dtalge memoryProjection(DtBase base) {
		Dtalge newAlge = new Dtalge();
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = queryDocuments(makeQueryByDtBase(base), true, true).iterator();
			while (cursor.hasNext()) {
				Document docElem = cursor.next();
				newAlge.add(
						MongoDelegateDocDtalgeElem.toDtBaseFromDocument(docElem),
						MongoDelegateDocDtalgeElem.getValueObjectFromDocument(docElem)
				);
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to project by one DtBase from MongoDtalge" + _mongo_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		return newAlge;
	}
	
	/**
	 * このデータ代数元から、指定された基底集合に含まれる基底と一致する要素のみを
	 * 取り出し、その要素のみを持つ <code>MongoDtalge</code> の新しいインスタンスを返す。
	 * <br>
	 * 指定された基底が存在しない場合、要素を持たない <code>MongoDtalge</code> の
	 * 新しいインスタンスを返す。
	 * <p>
	 * このメソッドは、次のデータ代数演算を行うものである。
	 * <blockquote>
	 * (return) = Proj[bases](this)
	 * </blockquote>
	 * 
	 * @param bases	取り出す基底が含まれるデータ代数基底集合
	 * 
	 * @return 取り出された要素のみを含む <code>MongoDtalge</code> インスタンス
	 * 
	 * @throws NullPointerException 指定された基底集合が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoDtalge projection(DtBaseSet bases) {
		Validations.validNotNull(bases);
		MongoDtalge newAlge = new MongoDtalge(_mongo_session);
		BigIterator<BigDtalgeElement> it = elementIterator();
		try {
			while (it.hasNext()) {
				BigDtalgeElement elem = it.next();
				if (bases.contains(elem.getBase())) {
					newAlge.putValue(elem.getBase(), elem.getValue());
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to project by multple DtBase from MongoDtalge" + _mongo_col.getNamespace().toString() + ":", ex);
		}
		finally {
			it.closeCursor();
		}
		return newAlge;
	}
	
	/**
	 * このデータ代数元から、指定された基底集合に含まれる基底と一致する要素のみを
	 * 取り出し、その要素のみを持つ <code>Dtalge</code> の新しいインスタンスを返す。
	 * <br>
	 * 指定された基底が存在しない場合、要素を持たない <code>Dtalge</code> の
	 * 新しいインスタンスを返す。
	 * <p>
	 * このメソッドは、次のデータ代数演算を行い、結果をメモリ上に保持するものである。
	 * <blockquote>
	 * (return) = Proj[bases](this)
	 * </blockquote>
	 * 
	 * @param bases	取り出す基底が含まれるデータ代数基底集合
	 * 
	 * @return 取り出された要素のみを含む <code>Dtalge</code> インスタンス
	 * 
	 * @throws NullPointerException 指定された基底集合が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public Dtalge memoryProjection(DtBaseSet bases) {
		Validations.validNotNull(bases);
		Dtalge newAlge = new Dtalge();
		BigIterator<BigDtalgeElement> it = elementIterator();
		try {
			while (it.hasNext()) {
				BigDtalgeElement elem = it.next();
				if (bases.contains(elem.getBase())) {
					newAlge.add(elem.getBase(), elem.getValue());
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to project by multple DtBase from MongoDtalge" + _mongo_col.getNamespace().toString() + ":", ex);
		}
		finally {
			it.closeCursor();
		}
		return newAlge;
	}
	
	/**
	 * このデータ代数元から、指定された基底集合に含まれる基底と一致する要素のみを
	 * 取り出し、その要素のみを持つ <code>MongoDtalge</code> の新しいインスタンスを返す。
	 * <br>
	 * 指定された基底が存在しない場合、要素を持たない <code>MongoDtalge</code> の
	 * 新しいインスタンスを返す。
	 * <p>
	 * このメソッドは、次のデータ代数演算を行うものである。
	 * <blockquote>
	 * (return) = Proj[bases](this)
	 * </blockquote>
	 * 
	 * @param bases	取り出す基底が含まれるデータ代数基底集合
	 * 
	 * @return 取り出された要素のみを含む <code>MongoDtalge</code> インスタンス
	 * 
	 * @throws NullPointerException 指定された基底集合が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoDtalge projection(BigDtBaseSet bases) {
		MongoDtalge newAlge = new MongoDtalge(_mongo_session);
		BigIterator<BigDtalgeElement> it = elementIterator();
		try {
			while (it.hasNext()) {
				BigDtalgeElement elem = it.next();
				if (bases.contains(elem.getBase())) {
					newAlge.putValue(elem.getBase(), elem.getValue());
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to project by multple DtBase from MongoDtalge" + _mongo_col.getNamespace().toString() + ":", ex);
		}
		finally {
			it.closeCursor();
		}
		return newAlge;
	}

	/**
	 * 指定の基底パターンに一致する基底を持つデータ代数のみを取り出す。
	 * <br>
	 * 指定された基底パターンに一致する基底のみを取得し、その基底のみを
	 * 持つデータ代数元を返す。
	 * <br>
	 * 基底パターンに一致する基底が存在しない場合、このメソッドは
	 * 空のデータ代数元を返す。
	 * <br>
	 * このメソッドが返すインスタンスでは、元(this)のインスタンスに格納されている基底の順序が
	 * 極力維持される。
	 * 
	 * @param pattern	基底パターン
	 * @return			パターンに一致した基底のみを含む <code>MongoDtalge</code> の新しいインスタンス
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoDtalge patternProjection(DtBasePattern pattern) {
		Validations.validNotNull(pattern);
		MongoDtalge newAlge = new MongoDtalge(_mongo_session);
		BigIterator<BigDtalgeElement> it = elementIterator();
		try {
			while (it.hasNext()) {
				BigDtalgeElement elem = it.next();
				if (pattern.matches(elem.getBase())) {
					newAlge.putValue(elem.getBase(), elem.getValue());
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to project by DtBasePattern from MongoDtalge" + _mongo_col.getNamespace().toString() + ":", ex);
		}
		finally {
			it.closeCursor();
		}
		return newAlge;
	}

	/**
	 * 指定の集合の基底パターンに一致する基底を持つデータ代数のみを取り出す。
	 * <br>
	 * 指定された基底パターンに一致する基底のみを取得し、その基底のみを
	 * 持つデータ代数元を返す。
	 * <br>
	 * 返されるデータ代数元に含まれる基底は、指定の集合に含まれる基底パターンの
	 * どれかに一致した基底となる。基底パターンに一致する基底が存在しない場合、
	 * このメソッドは空のデータ代数元を返す。
	 * <br>
	 * このメソッドが返すインスタンスでは、元(this)のインスタンスに格納されている基底の順序が
	 * 極力維持される。
	 * 
	 * @param patterns	基底パターンの集合
	 * @return		パターンに一致した基底のみを含む <code>MongoDtalge</code> の新しいインスタンス
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoDtalge patternProjection(DtBasePatternSet patterns) {
		Validations.validNotNull(patterns);
		MongoDtalge newAlge = new MongoDtalge(_mongo_session);
		BigIterator<BigDtalgeElement> it = elementIterator();
		try {
			while (it.hasNext()) {
				BigDtalgeElement elem = it.next();
				if (patterns.matches(elem.getBase())) {
					newAlge.putValue(elem.getBase(), elem.getValue());
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to project by multiple DtBasePattern from MongoDtalge" + _mongo_col.getNamespace().toString() + ":", ex);
		}
		finally {
			it.closeCursor();
		}
		return newAlge;
	}

	/**
	 * コレクションに含まれるデータ代数元を結合する。
	 * <br>
	 * このメソッドは、コレクションに含まれる全てのデータ代数元を一つのデータ代数元に
	 * 結合するメソッドであり、同じ基底の値は、コレクションに格納されている順序で
	 * 上書きされる。
	 * <p>
	 * このメソッドでは、新しいインスタンスの基底の順序は、元(this)の基底の順序を維持する。
	 * 結合する値の基底が存在しない場合、データ代数の値と基底のマップの終端に、
	 * 指定されたデータ代数コレクション(c)に格納されている順序で追加される。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * この結合は、同じ基底の値は上書きされる。数値的な加算等の演算は行わない。
	 * </blockquote>
	 * 
	 * @param session	結合結果を格納するデータベースセッション
	 * @param c 結合するデータ代数の元のコレクション
	 * 
	 * @return 計算結果となる <code>MongoDtalge</code> の新しいインスタンス
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	static public MongoDtalge sum(MongoSession session, Collection<? extends Dtalge> c) {
		MongoDtalge newAlge = new MongoDtalge(session);
		for (Dtalge alge : c) {
			if (alge != null) {
				newAlge.putValues(alge);
			}
		}
		return newAlge;
	}

	//------------------------------------------------------------
	// 振替系演算
	//------------------------------------------------------------

	/**
	 * 指定された基底の値が <code>srcObj</code> と等しい場合にのみ、
	 * その値を <code>dstObj</code> に置き換えた、<code>MongoDtalge</code> の
	 * 新しいインスタンスを返す。
	 * <p>
	 * 次の場合、このデータ代数元自身のインスタンスを返す。
	 * <ul>
	 * <li>指定された基底が、このデータ代数元に存在しない場合
	 * <li>指定された基底に対応する値が、<code>srcObj</code> と等しくない場合
	 * </ul>
	 * 
	 * @param base	振替対象とするデータ代数基底
	 * @param srcObj	振替元の値
	 * @param dstObj	振替先の値
	 * @return	振替が行われた場合は振替結果を保持する新しい <code>MongoDtalge</code> インスタンス、
	 * 			振替が行われなかった場合は <code>this</code>
	 * 
	 * @throws NullPointerException 指定された基底が <tt>null</tt> の場合
	 * @throws IllegalValueOfDataTypeException	指定された振替先の値が基底のデータ型と異なる場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public MongoDtalge thesconv(DtBase base, Object srcObj, Object dstObj) {
		//--- check
		Validations.validNotNull(base, "'base' argument cannot be null.");
		DtDataTypes.validDataType(base.getTypeKey(), dstObj);

		//--- convert
		MongoDtalge newAlge = null;
		if (this.containsBase(base)) {
			Object targetValue = this.get(base);
			if (srcObj == targetValue || (srcObj != null && srcObj.equals(targetValue))) {
				newAlge = copy();
				newAlge.putValue(base, dstObj);
			}
		}
		
		return (newAlge != null ? newAlge : this.copy());
	}

	/**
	 * 指定されたシソーラス定義を基に、指定された基底の値を、指定された分類集合の値に振り替える。
	 * このメソッドは、振り替え後の新しいデータ代数元を返す。
	 * <p>
	 * シソーラス定義に基づく分類集合への値振替では、指定された基底の値が
	 * シソーラス定義に属し、分類集合に含まれる値よりも小さい場合、
	 * 分類集合内の関係する値へ振り替える。
	 * <br>
	 * このメソッドでは、分類集合の値とは関係を持たない値の場合、その元は振り替え不可能と
	 * みなし <tt>null</tt> を返す。
	 * <p>
	 * 指定された基底が、このデータ代数元に存在しない場合、このメソッドは例外をスローする。
	 * 
	 * @param base	振替対象とするデータ代数基底
	 * @param thes	シソーラス定義
	 * @param words	振替先の値の分類集合
	 * @return	振替に成功した場合は、振替後の要素を格納する新しい <code>MongoDtalge</code> の
	 * インスタンスを返す。振替不可能の場合は <tt>null</tt> を返す。
	 * 
	 * @throws NullPointerException 引数に指定されたオブジェクトが <tt>null</tt> の場合
	 * @throws IllegalArgumentException 基底のデータ型が文字列型ではない場合、
	 * 									もしくは、指定された語句の集合が分類集合ではない場合
	 * @throws DtBaseNotFoundException 指定された基底がこのデータ代数基底に含まれていない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public MongoDtalge thesconv(DtBase base, DtStringThesaurus thes, String...words) {
		Validations.validNotNull(base, "'base' argument cannot be null.");
		Validations.validNotNull(thes, "'thes' argument cannot be null.");
		Validations.validNotNull(words, "String collection argument cannot be null.");
		//--- 文字列型?
		Validations.validArgument(DtDataTypes.STRING.equals(base.getTypeKey()), "Illegal DtBase data type : %s", base.getTypeKey());
		//--- 分類集合?
		Validations.validArgument(thes.isClassificationSet(words), "Words must be Classification set in thesaurus.");

		// 基底の値を取得(存在しなければ、例外スロー)
		Object value = get(base);
		String targetWord = (String)value;
		
		// リレーションある？
		if (targetWord != null) {	// シソーラスに null は含まれない
			for (String word : words) {
				if (word.equals(targetWord)) {
					// 同じ値なので変換せず、この元をそのまま返す。
					return this.copy();
				}
				if (thes.lessThan(targetWord, word)) {
					// シソーラス定義に関係がある
					return this.put(base, word);
				}
			}
		}
		
		// 分類集合と比較不能な値の場合、null を返す。
		return null;
	}
	
	/**
	 * 指定されたシソーラス定義を基に、指定された基底の値を、指定された分類集合の値に振り替える。
	 * このメソッドは、振り替え後の新しいデータ代数元を返す。
	 * <p>
	 * シソーラス定義に基づく分類集合への値振替では、指定された基底の値が
	 * シソーラス定義に属し、分類集合に含まれる値よりも小さい場合、
	 * 分類集合内の関係する値へ振り替える。
	 * <br>
	 * このメソッドでは、分類集合の値とは関係を持たない値の場合、その元は振り替え不可能と
	 * みなし <tt>null</tt> を返す。
	 * <p>
	 * 指定された基底が、このデータ代数元に存在しない場合、このメソッドは例外をスローする。
	 * 
	 * @param base	振替対象とするデータ代数基底
	 * @param thes	シソーラス定義
	 * @param words	振替先の値の分類集合
	 * @return	振替に成功した場合は、振替後の要素を格納する新しい <code>MongoDtalge</code> の
	 * インスタンスを返す。振替不可能の場合は <tt>null</tt> を返す。
	 * 
	 * @throws NullPointerException 引数に指定されたオブジェクトが <tt>null</tt> の場合
	 * @throws IllegalArgumentException 基底のデータ型が文字列型ではない場合、
	 * 									もしくは、指定された語句の集合が分類集合ではない場合
	 * @throws DtBaseNotFoundException 指定された基底がこのデータ代数基底に含まれていない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public MongoDtalge thesconv(DtBase base, DtStringThesaurus thes, Collection<? extends String> words) {
		Validations.validNotNull(base, "'base' argument cannot be null.");
		Validations.validNotNull(thes, "'thes' argument cannot be null.");
		Validations.validNotNull(words, "String collection argument cannot be null.");
		//--- 文字列型?
		Validations.validArgument(DtDataTypes.STRING.equals(base.getTypeKey()), "Illegal DtBase data type : %s", base.getTypeKey());
		//--- 分類集合?
		Validations.validArgument(thes.isClassificationSet(words), "Words must be Classification set in thesaurus.");

		// 基底の値を取得(存在しなければ、例外スロー)
		Object value = get(base);
		String targetWord = (String)value;
		
		// リレーションある？
		if (targetWord != null) {	// シソーラスに null は含まれない
			for (String word : words) {
				if (word.equals(targetWord)) {
					// 同じ値なので変換せず、この元をそのまま返す。
					return this.copy();
				}
				if (thes.lessThan(targetWord, word)) {
					// シソーラス定義に関係がある
					return this.put(base, word);
				}
			}
		}
		
		// 分類集合と比較不能な値の場合、null を返す。
		return null;
	}

	//------------------------------------------------------------
	// for I/O
	//------------------------------------------------------------
	
	/**
	 * データ代数元の内容を、指定のファイルにテーブル形式の CSV フォーマットで出力する。
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
		//--- 順序はオリジナル、nullは出力する
		toTableCSV(null, false, csvFile);
	}
	
	/**
	 * データ代数元の内容を、指定された文字セットで指定のファイルにテーブル形式の CSV フォーマットで出力する。
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
		//--- 順序はオリジナル、nullは出力する
		toTableCSV(null, false, csvFile, charsetName);
	}
	
	/**
	 * データ代数元の内容を、指定のファイルにテーブル形式の CSV フォーマットで出力する。
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
	 * データ代数元の内容を、指定された文字セットで指定のファイルにテーブル形式の CSV フォーマットで出力する。
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
	 * データ代数元の内容を、指定のファイルにテーブル形式の CSV フォーマットで出力する。
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
	 * データ代数元の内容を、指定された文字セットで指定のファイルにテーブル形式の CSV フォーマットで出力する。
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
	 * データ代数元の内容を、指定のファイルにテーブル形式の CSV フォーマットで出力する。
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
	 * @param baseOrder	順序指定用基底集合。順序を指定しない場合は <tt>null</tt>
	 * @param withoutNull	<tt>null</tt> を出力しない場合は <tt>true</tt>
	 * @param csvFile 出力先ファイル
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public void toTableCSV(DtBaseSet baseOrder, boolean withoutNull, File csvFile)
		throws IOException, FileNotFoundException
	{
		CsvWriter writer = new CsvWriter(csvFile);
		try {
			// キーワード出力
			writer.writeLine(Dtalge.CSV_TABLE_KEYWORD_V2);
			// エントリ出力
			writeToTableCsv_v2(writer, baseOrder, withoutNull);
		}
		finally {
			writer.close();
		}
	}
	
	/**
	 * データ代数元の内容を、指定された文字セットで指定のファイルにテーブル形式の CSV フォーマットで出力する。
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
	 * @param baseOrder	順序指定用基底集合。順序を指定しない場合は <tt>null</tt>
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
	public void toTableCSV(DtBaseSet baseOrder, boolean withoutNull, File csvFile, String charsetName)
		throws IOException, FileNotFoundException, UnsupportedEncodingException
	{
		CsvWriter writer = new CsvWriter(csvFile, charsetName);
		try {
			// キーワード出力
			writer.writeLine(Dtalge.CSV_TABLE_KEYWORD_V2);
			// エントリ出力
			writeToTableCsv_v2(writer, baseOrder, withoutNull);
		}
		finally {
			writer.close();
		}
	}
	
	/**
	 * データ代数元の内容を、指定のファイルに CSV フォーマットで出力する。
	 * <p>
	 * 出力時は、このインスタンスに格納されている基底の順序で出力される。
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
			// キーワード出力
			writer.writeLine(Dtalge.CSV_KEYWORD_V2);
			// エントリ出力
			writeToCsv_v2(writer);
		}
		finally {
			writer.close();
		}
	}
	
	/**
	 * データ代数元の内容を、指定された文字セットで指定のファイルに CSV フォーマットで出力する。
	 * <p>
	 * 出力時は、このインスタンスに格納されている基底の順序で出力される。
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
			// キーワード出力
			writer.writeLine(Dtalge.CSV_KEYWORD_V2);
			// エントリ出力
			writeToCsv_v2(writer);
		}
		finally {
			writer.close();
		}
	}

	/**
	 * CSV フォーマットのファイルを読み込み、このオブジェクトに追加する。
	 * @param csvFile	読み込む CSV ファイル
	 * @return	このデータ代数元が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
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
		ensureNotView();
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
	 * @return	このデータ代数元が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
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
		ensureNotView();
		CsvReader reader = new CsvReader(csvFile, charsetName);
		try {
			return readFromCsv(reader);
		}
		finally {
			reader.close();
		}
	}
	
	/**
	 * CSV フォーマットのファイルを読み込み、新しいデータ代数元を生成する。
	 * <p>
	 * 基本的に、ファイルに記述された要素の順序で、新しいインスタンスに格納される。
	 * 
	 * @param session	MongoDB セッションオブジェクト
	 * @param csvFile 読み込む CSV ファイル
	 * 
	 * @return ファイルの内容で生成された、新しい <code>MongoDtalge</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	static public MongoDtalge fromCSV(MongoSession session, File csvFile)
		throws IOException, FileNotFoundException, CsvFormatException
	{
		MongoDtalge newAlge = new MongoDtalge(session);
		newAlge.addAllFromCSV(csvFile);
		return newAlge;
	}
	
	/**
	 * 指定された文字セットで CSV フォーマットのファイルを読み込み、新しいデータ代数元を生成する。
	 * <p>
	 * 基本的に、ファイルに記述された要素の順序で、新しいインスタンスに格納される。
	 * 
	 * @param session	MongoDB セッションオブジェクト
	 * @param csvFile 読み込む CSV ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @return ファイルの内容で生成された、新しい <code>MongoDtalge</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	static public MongoDtalge fromCSV(MongoSession session, File csvFile, String charsetName)
		throws IOException, FileNotFoundException, CsvFormatException, UnsupportedEncodingException
	{
		MongoDtalge newAlge = new MongoDtalge(session);
		newAlge.addAllFromCSV(csvFile, charsetName);
		return newAlge;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * このオブジェクトがデータ代数集合のビューである場合に、{@link java.lang.UnsupportedOperationException} をスローする。
	 */
	protected void ensureNotView() {
		if (_elem_id != null)
			throw new UnsupportedOperationException("This object is view of MongoDtAlgeSet" + _mongo_col.getNamespace().toString());
	}
	
	/**
	 * このオブジェクトの要素数変更カウンタをインクリメントする。
	 * @return	インクリメント後のカウンタ値
	 */
	protected long incrementModCount() {
		if (_modDtAlgeSetCount != null) {
			_modDtAlgeSetCount.incrementAndGet();
		}
		return _modCount.incrementAndGet();
	}
	
	/**
	 * データ代数元のすべてのドキュメントに一致するクエリを生成する。
	 * クエリ生成においては、データ代数集合の要素かどうかも考慮される。
	 * @return	フィルターオブジェクト
	 */
	protected Bson makeQeuryAll() {
		if (_elem_id != null) {
			// データ代数集合の要素
			return Filters.eq(MongoDelegateDocDtAlgeSetElem.MONGO_KEY_SETID, _elem_id);
		} else {
			// 単独のデータ代数元
			return new Document();
		}
	}

	/**
	 * 指定された基底でドキュメントを検索するクエリを生成する。
	 * クエリ生成においては、データ代数集合の要素かどうかも考慮される。
	 * @param base	判定する基底
	 * @return	フィルターオブジェクト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	protected Bson makeQueryByDtBase(DtBase base) {
		if (_elem_id != null) {
			// データ代数集合の要素
			return Filters.and(
					Filters.eq(MongoDelegateDocDtAlgeSetElem.MONGO_KEY_SETID, _elem_id),
					Filters.eq(MongoDelegateDocDtalgeElem.MONGO_KEY_BASE, MongoDelegateDocDtBase.makeDtBaseDocument(base)));
		}
		else {
			// 単独のデータ代数元
			return Filters.eq(MongoDelegateDocDtalgeElem.MONGO_KEY_BASE, MongoDelegateDocDtBase.makeDtBaseDocument(base));
		}
	}
	
	/**
	 * 指定された基底を含まないドキュメントを検索するクエリを生成する。
	 * クエリ生成においては、データ代数集合の要素かどうかも考慮される。
	 * @param base	除外する基底
	 * @return	フィルターオブジェクト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	protected Bson makeQueryWithoutDtBase(DtBase base) {
		if (_elem_id != null) {
			// データ代数集合の要素
			return Filters.and(
					Filters.eq(MongoDelegateDocDtAlgeSetElem.MONGO_KEY_SETID, _elem_id),
					Filters.ne(MongoDelegateDocDtalgeElem.MONGO_KEY_BASE, MongoDelegateDocDtBase.makeDtBaseDocument(base)));
		}
		else {
			// 単独のデータ代数元
			return Filters.ne(MongoDelegateDocDtalgeElem.MONGO_KEY_BASE, MongoDelegateDocDtBase.makeDtBaseDocument(base));
		}
	}

	/**
	 * 指定された基底のコレクションでドキュメントを検索するクエリを生成する。
	 * クエリ生成においては、データ代数集合の要素かどうかも考慮される。
	 * @param bases			判定する基底のコレクション
	 * @param notMatches	指定されたコレクションに一致しないものを検索する場合は <tt>true</tt>
	 * @return	フィルターオブジェクト
	 * @throws NullPointerException	<em>bases</em> が <tt>null</tt> の場合
	 */
	protected Bson makeQueryByMultiDtBases(Collection<? extends DtBase> bases, boolean notMatches) {
		ArrayList<Document> aryBaseDocs = new ArrayList<Document>(bases.size());
		for (DtBase base : bases) {
			aryBaseDocs.add(MongoDelegateDocDtBase.makeDtBaseDocument(base));
		}
		Bson baseFilter;
		if (notMatches)
			baseFilter = Filters.nin(MongoDelegateDocDtalgeElem.MONGO_KEY_BASE, aryBaseDocs);
		else
			baseFilter = Filters.in(MongoDelegateDocDtalgeElem.MONGO_KEY_BASE, aryBaseDocs);
		
		if (_elem_id != null) {
			// データ代数集合の要素
			return Filters.and(Filters.eq(MongoDelegateDocDtAlgeSetElem.MONGO_KEY_SETID, _elem_id), baseFilter);
		}
		else {
			// 単独のデータ代数元
			return baseFilter;
		}
	}

	/**
	 * 指定された値でドキュメントを検索するクエリを生成する。
	 * 値は MongoDB において、比較(compare)により判定され、<tt>null</tt> かどうかも判定される。
	 * クエリ生成においては、データ代数集合の要素かどうかも考慮される。
	 * @param value	判定する値
	 * @param notEquals	指定された値と等しくないものを検索する場合は <tt>true</tt>
	 * @return	フィルターオブジェクト
	 */
	protected Bson makeQueryByValue(Object value) {
		if (_elem_id != null) {
			// データ代数集合の要素
			return Filters.and(
					Filters.eq(MongoDelegateDocDtAlgeSetElem.MONGO_KEY_SETID, _elem_id),
					Filters.eq(MongoDelegateDocDtalgeElem.MONGO_KEY_VALUE, value));
		}
		else {
			// 単独のデータ代数元
			return Filters.eq(MongoDelegateDocDtalgeElem.MONGO_KEY_VALUE, value);
		}
	}

	/**
	 * 指定された値を含まないドキュメントを検索するクエリを生成する。
	 * 値は MongoDB において、比較(compare)により判定され、<tt>null</tt> かどうかも判定される。
	 * クエリ生成においては、データ代数集合の要素かどうかも考慮される。
	 * @param value	除外する値
	 * @return	フィルターオブジェクト
	 */
	protected Bson makeQueryWithoutValue(Object value) {
		if (_elem_id != null) {
			// データ代数集合の要素
			return Filters.and(
					Filters.eq(MongoDelegateDocDtAlgeSetElem.MONGO_KEY_SETID, _elem_id),
					Filters.ne(MongoDelegateDocDtalgeElem.MONGO_KEY_VALUE, value));
		}
		else {
			// 単独のデータ代数元
			return Filters.ne(MongoDelegateDocDtalgeElem.MONGO_KEY_VALUE, value);
		}
	}

	/**
	 * 指定された値のコレクションでドキュメントを検索するクエリを生成する。
	 * クエリ生成においては、データ代数集合の要素かどうかも考慮される。
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
			baseFilter = Filters.nin(MongoDelegateDocDtalgeElem.MONGO_KEY_VALUE, values);
		else
			baseFilter = Filters.in(MongoDelegateDocDtalgeElem.MONGO_KEY_VALUE, values);
		
		if (_elem_id != null) {
			// データ代数集合の要素
			return Filters.and(Filters.eq(MongoDelegateDocDtAlgeSetElem.MONGO_KEY_SETID, _elem_id), baseFilter);
		}
		else {
			// 単独のデータ代数元
			return baseFilter;
		}
	}

	/**
	 * このオブジェクトが保持するコレクションから、指定された基底と一致するドキュメント数を取得する。
	 * クエリ生成においては、データ代数集合の要素かどうかも考慮される。
	 * @param base	比較する基底
	 * @return	指定された基底と一致するドキュメント数
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースに関する処理に失敗した場合
	 */
	protected long getNumDocumentsByDtBase(DtBase base) {
		try {
			return _mongo_col.countDocuments(makeQueryByDtBase(base));
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to count document by DtBase in MongoDtalge" + _mongo_col.getNamespace().toString() + ":", ex);
		}
	}

	/**
	 * このオブジェクトが保持するコレクションから、指定された値と一致するドキュメント数を取得する。
	 * クエリ生成においては、データ代数集合の要素かどうかも考慮される。
	 * @param value	比較する値
	 * @return	指定された値と一致するドキュメント数
	 * @throws MongoAlgeError	データベースに関する処理に失敗した場合
	 */
	protected long getNumDocumentsByValue(Object value) {
		try {
			return _mongo_col.countDocuments(makeQueryByValue(value));
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to count document by Value in MongoDtalge" + _mongo_col.getNamespace().toString() + ":", ex);
		}
	}
	
	/**
	 * このオブジェクトがデータ代数集合の要素であれば、データ代数元 ID のみを含むドキュメントを生成する。
	 * @return	データ代数元 ID を含むドキュメント、データ代数集合の要素でない場合は空のドキュメント
	 * @throws MongoAlgeError	データベースに関する処理に失敗した場合
	 */
	protected Document makeSetIdDocument() {
		Document data = new Document();
		if (_elem_id != null) {
			data.append(MongoDelegateDocDtAlgeSetElem.MONGO_KEY_SETID, _elem_id);
		}
		return data;
	}

	/**
	 * このデータ代数元に含まれるドキュメントから、指定されたクエリで検索した結果を返す。
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
				result = _mongo_col.find(makeSetIdDocument());
			else
				result = _mongo_col.find(filter);
			if (withoutObjectID)
				result = result.projection(MongoUtil.MONGO_FILTER_WITHOUT_OBJID);
			if (_elem_id != null && withoutSetID)
				result = result.projection(new Document(MongoDelegateDocDtAlgeSetElem.MONGO_KEY_SETID, 0));
			return result;
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Faild to query documents by specified filter from MongoDtalge" + _mongo_col.getNamespace().toString() + ":", ex);
		}
	}

	/**
	 * このデータ代数元に含まれるすべての要素を検索した結果を返す。
	 * クエリ生成においては、データ代数集合の要素かどうかも考慮される。
	 * @param withoutObjectID	検索結果から MongoDB の ObjectID を除外する場合は <tt>true</tt>
	 * @param withoutSetID		検索結果からデータ代数元 ID を除外する場合は <tt>true</tt>
	 * @return	検索結果のイテレート可能オブジェクト
	 * @throws MongoAlgeError	データベースに関する処理に失敗した場合
	 */
	protected FindIterable<? extends Document> findAllDocuments(boolean withoutObjectID, boolean withoutSetID) {
		return queryDocuments(makeSetIdDocument(), withoutObjectID, withoutSetID);
	}
	
	/**
	 * このデータ代数元に含まれるドキュメントから、指定された基底と一致するドキュメントを検索した結果を返す。
	 * クエリ生成においては、データ代数集合の要素かどうかも考慮される。
	 * @param base	比較する基底
	 * @param withoutObjectID	検索結果から MongoDB の ObjectID を除外する場合は <tt>true</tt>
	 * @param withoutSetID		検索結果からデータ代数元 ID を除外する場合は <tt>true</tt>
	 * @return	検索結果のイテレート可能オブジェクト
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースに関する処理に失敗した場合
	 */
	protected FindIterable<? extends Document> findByDtBase(DtBase base, boolean withoutObjectID, boolean withoutSetID) {
		return queryDocuments(makeQueryByDtBase(base), withoutObjectID, withoutSetID);
	}

	/**
	 * このデータ代数元に含まれるドキュメントから、指定された値と一致するドキュメントを検索した結果を返す。
	 * MongoDB では、値は等しいかどうか(compare)で判定され、<tt>null</tt> も判定される。
	 * クエリ生成においては、データ代数集合の要素かどうかも考慮される。
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
	 * このオブジェクトが保持するコレクションから、すべての値でソートされたイテレート可能オブジェクトを取得する。
	 * このオブジェクトがデータ代数集合の要素の場合は、データ代数元 ID で抽出された結果のソート済みイテレート可能オブジェクトとなる。
	 * @param withoutObjectID	検索結果から MongoDB の ObjectID を除外する場合は <tt>true</tt>
	 * @param withoutSetID		検索結果からデータ代数元 ID を除外する場合は <tt>true</tt>
	 * @return	イテレート可能オブジェクト
	 * @throws MongoAlgeError	データベースに関する処理に失敗した場合
	 */
	protected FindIterable<? extends Document> sortedDocuments(boolean withoutObjectID, boolean withoutSetID) {
		try {
			FindIterable<? extends Document> result = _mongo_col.find(makeSetIdDocument()).sort(MongoDelegateDocDtalgeElem.MONGO_SORT_DTALGE_ELEM_ALL);
			if (withoutObjectID)
				result = result.projection(MongoUtil.MONGO_FILTER_WITHOUT_OBJID);
			if (_elem_id != null && withoutSetID)
				result = result.projection(new Document(MongoDelegateDocDtAlgeSetElem.MONGO_KEY_SETID, 0));
			return result;
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Faild to sort by all Dtalge fields in MongoDtalge" + _mongo_col.getNamespace().toString() + ":", ex);
		}
	}

	/**
	 * このオブジェクトが保持するコレクションから、基底が重複しないドキュメントの検索結果を取得する。
	 * @return 検索結果のイテレート可能オブジェクト
	 * @throws MongoAlgeError	データベースに関する処理に失敗した場合
	 */
	protected DistinctIterable<? extends Document> distinctByDtBase() {
		try {
			if (_elem_id != null) {
				// データ代数集合の元
				return _mongo_col.distinct(MongoDelegateDocDtalgeElem.MONGO_KEY_BASE,
						Filters.eq(MongoDelegateDocDtAlgeSetElem.MONGO_KEY_SETID, _elem_id), Document.class);
			}
			else {
				// 単独のデータ代数元
				return _mongo_col.distinct(MongoDelegateDocDtalgeElem.MONGO_KEY_BASE, Document.class);
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to distinct by DtBase in MongoDtalge" + _mongo_col.getNamespace().toString() + ":", ex);
		}
	}

	/**
	 * データ代数元の要素として格納する MongoDB ドキュメントを、指定されたパラメータから生成する。
	 * このメソッドは、データ代数集合の要素の場合、データ代数元 ID も含める。
	 * @param base	基底
	 * @param value	値
	 * @return	MongoDB ドキュメント
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 */
	protected Document makeDtalgeElemDocument(DtBase base, Object value) {
		Document data = MongoDelegateDocDtalgeElem.makeDtalgeElemDocument(base, value);
		if (_elem_id != null) {
			data.append(MongoDelegateDocDtAlgeSetElem.MONGO_KEY_SETID, _elem_id);
		}
		return data;
	}

	/**
	 * データ代数元の要素として基底のみを格納する MongoDB ドキュメントを、指定された基底から生成する。
	 * このメソッドは、データ代数集合の要素の場合、データ代数元 ID も含める。
	 * @param base	基底
	 * @return	生成されたドキュメント
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 */
	protected Document makeDtBaseDocument(DtBase base) {
		Document data = new Document();
		if (_elem_id != null) {
			data.append(MongoDelegateDocDtAlgeSetElem.MONGO_KEY_SETID, _elem_id);
		}
		MongoDelegateDocDtalgeElem.appendDtalgeElemBaseToDocument(data, base);
		return data;
	}

	/**
	 * データ代数元の要素を抽出するフィルターを、指定されたデータ代数元要素のドキュメントから生成する。
	 * このメソッドでは、データ代数基底の要素を指定されたドキュメントから抽出し、新たに生成したドキュメントに格納する。
	 * @param data	データ代数元の要素のドキュメント
	 * @return	生成されたドキュメント
	 * @throws NullPointerException	<em>data</em> が <tt>null</tt> の場合
	 */
	protected Document makeDtBaseFilterFromData(Document data) {
		Document filter = new Document();
		if (_elem_id != null) {
			filter.append(MongoDelegateDocDtAlgeSetElem.MONGO_KEY_SETID, _elem_id);
		}
		return filter.append(MongoDelegateDocDtalgeElem.MONGO_KEY_BASE, data.get(MongoDelegateDocDtalgeElem.MONGO_KEY_BASE));
	}

	/**
	 * 文字列配列から、{@link java.util.HashSet} インスタンスを生成する。
	 * 
	 * @return 指定された文字列を全て格納する、{@link java.util.HashSet} インスタンスを返す。
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 */
	protected Set<String> makeBaseKeyTargetSet(String...targets) {
		if (targets.length == 1) {
			return Collections.singleton(targets[0]);
		} else if (targets.length > 1) {
			return new HashSet<String>(Arrays.asList(targets));
		} else {
			return Collections.<String>emptySet();
		}
	}

	/**
	 * 指定された文字列セットのどれか一つに一致する基底キーを持つ要素のみを取り出す。
	 * <p>基底キーはインデックスで指定する。
	 * 
	 * @param keyIndex	判定対象の基底キーを示すインデックス
	 * @param targets	検索キーとなる文字列のセット
	 * @return	指定の文字列と一致する基底キーを持つ要素のみとなるデータ代数元を返す。
	 * 			一致する基底が見つからなかった場合は、要素が空のデータ代数元を返す。
	 * 			<code>targets</code> の要素が空の場合も、要素が空のデータ代数元を返す。
	 * 
	 * @throws NullPointerException	<code>targets</code> が <tt>null</tt> の場合
	 * @throws IndexOutOfBoundsException	<code>keyIndex</code> が範囲外の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	protected MongoDtalge projectionByBaseKey(int keyIndex, Set<String> targets) {
		MongoDtalge retAlge = new MongoDtalge(_mongo_session);
		if (!targets.isEmpty()) {
			MongoCursor<? extends Document> cursor = distinctByDtBase().iterator();
			try {
				while (cursor.hasNext()) {
					Document docDtalgeElem = cursor.next();
					String strKey = MongoDelegateDocDtalgeElem.getDtBaseKeyStringByIndex(docDtalgeElem, keyIndex);
					if (targets.contains(strKey)) {
						DtBase base = MongoDelegateDocDtalgeElem.toDtBaseFromDocument(docDtalgeElem);
						Object val = MongoDelegateDocDtalgeElem.getValueObjectFromDocument(docDtalgeElem);
						retAlge.putValue(base, val);
					}
				}
			}
			finally {
				MongoUtil.closeCursorSilent(cursor);
			}
		}
		return retAlge;
	}

	/*
	 * @deprecated	使用していない
	 * データ代数内部のすべての値をクリア(０に設定)する。
	 * <p>
	 * (注) このメソッドは<b>破壊的メソッド</b>である。
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 *
	protected void clearValue() {
		BigDecimal value = BigDecimal.ZERO;
		Iterator<DtBase> it = data.keySet().iterator();
		while (it.hasNext()) {
			DtBase base = it.next();
			put(base, value);
		}
	}
	/**/

	/**
	 * 指定された基底と値を代入する。
	 * <p>すでに同一基底が存在する場合、指定された値で上書きする。
	 * <p><b>注:</b>このメソッドは、インスタンスの値を書き換える。
	 * 
	 * @param base	データ代数の基底
	 * @param value	データ代数の値
	 * @return	このデータ代数が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * 
	 * @throws NullPointerException	指定された基底が <tt>null</tt> の場合
	 * @throws IllegalValueOfDataTypeException	指定された値が基底のデータ型と異なる場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	protected boolean putValue(DtBase base, Object value) {
		DtDataTypes.validDataType(base.getTypeKey(), value);
		Document data = makeDtalgeElemDocument(base, value);
		Document where = makeDtBaseFilterFromData(data);
		UpdateResult result = MongoUtil.updateDocument(_mongo_col, where, data, false, true);
		if (result.getUpsertedId() != null) {
			//--- updated
			if (result.getMatchedCount() == 0) {
				//--- inserted
				incrementModCount();
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
	 * 指定されたデータ代数元に含まれるすべての要素を代入する。
	 * <p>すでに同一基底が存在する場合、指定された値で上書きする。
	 * <p><b>注:</b>このメソッドは、インスタンスの値を書き換える。
	 * 
	 * @param alge	このデータ代数元に配置される要素を持つデータ代数元
	 * @return	このデータ代数が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * 
	 * @throws NullPointerException 指定されたデータ代数元が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	protected boolean putValues(Dtalge alge) {
		boolean modified = false;
		Iterator<Map.Entry<DtBase, Object>> it = alge.getUnmodifiableEntrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<DtBase, Object> entry = it.next();
			if (putValue(entry.getKey(), entry.getValue())) {
				//--- modified
				modified = true;
			}
		}
		return modified;
	}

	/**
	 * データ代数元の要素イテレーターを生成する。
	 * 
	 * @return <code>MongoDtalge</code> の <code>Iterator</code>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	protected BigIterator<Dtalge> newUnmodifiableNaturalOrderedDtalgeIterator() {
		// TODO: カーソルのクローズ処理を今後考慮
		try {
			MongoCursor<? extends Document> cursor = findAllDocuments(true, true).iterator();
			return new MongoDtalgeIterator(cursor);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get sorted cursor from MongoDtalge" + _mongo_col.getNamespace().toString() + ":", ex);
		}
	}
	
	protected BigIterator<BigDtalgeElement> newUnmodifiableNaturalOrderedElementIterator() {
		// TODO: カーソルのクローズ処理を今後考慮
		try {
			MongoCursor<? extends Document> cursor = findAllDocuments(false, false).iterator();
			return new MongoDtalgeElementIterator(cursor);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get natural ordered cursor from MongoDtalge" + _mongo_col.getNamespace().toString() + ":", ex);
		}
	}
	
	protected BigIterator<BigDtalgeElement> newUnmodifiableSortedElementIterator() {
		// TODO: カーソルのクローズ処理を今後考慮
		try {
			//--- ソート済みイテレータにする
			MongoCursor<? extends Document> cursor = sortedDocuments(false, false).iterator();
			//MongoCursor<? extends Document> cursor = findAllDocuments(true, true).iterator();
			return new MongoDtalgeElementIterator(cursor);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get sorted cursor from MongoDtalge" + _mongo_col.getNamespace().toString() + ":", ex);
		}
	}
	
	protected BigIterator<DtBase> newUnmodifiableSortedDtBaseIterator() {
		// TODO: カーソルのクローズ処理を今後考慮
		try {
			//--- ソート済みイテレータにする
			MongoCursor<? extends Document> cursor = sortedDocuments(true, true).iterator();
			return new MongoDtalgeElementBaseIterator(cursor);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get sorted cursor from MongoDtalge" + _mongo_col.getNamespace().toString() + ":", ex);
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	/**
	 * <code>MongoDtalge</code> クラスの要素の値を保持するクラス。
	 * <p>このオブジェクトは不変である。
	 * 
	 * @version 0.5.0
	 * @since 0.5.0
	 * 
	 * @author H.Deguchi(SOARS Project.)
	 * @author Y.Ishizuka(PieCake.inc,)
	 */
	static public class MongoDtalgeElement implements BigDtalgeElement
	{
		private final DtBase	_base;
		private final Object	_value;
		
		public MongoDtalgeElement(DtBase base, Object value) {
			_base = base;
			_value = value;
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
	 * <code>MongoDtalge</code> クラスの要素にアクセス可能なイテレーターの共通実装。
	 * <p>
	 * このクラスは、データ代数元<code>(MongoDtalge)</code>からデータ代数要素(単一の基底と値を持つデータ代数元)を
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
	private abstract class AbstractMongoDtalgeElementIterator<T> implements BigIterator<T>
	{
		MongoCursor<? extends Document>	_itCursor;
		long _expectedModCount;
		
		AbstractMongoDtalgeElementIterator(MongoCursor<? extends Document> cursor) {
			_itCursor = cursor;
			_expectedModCount = _modCount.get();
		}

		public boolean hasNext() {
			try {
				return (_itCursor==null ? false : _itCursor.hasNext());
			} catch (MongoException ex) {
				throw new MongoAlgeError("MongoDtalge's element iterator couldn't check next element in MongoDtalge" + _mongo_col.getNamespace().toString() + ":", ex);
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
	 * <code>MongoDtalge</code> クラスの要素イテレーター。
	 * <p>
	 * このクラスは、データ代数元<code>(MongoDtalge)</code>からデータ代数要素(BigDtalgeElement)を
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
	private class MongoDtalgeElementIterator extends AbstractMongoDtalgeElementIterator<BigDtalgeElement>
	{
		MongoDtalgeElementIterator(MongoCursor<? extends Document> cursor) {
			super(cursor);
		}
		
		@Override
		public BigDtalgeElement next() {
			if (_expectedModCount != _modCount.get())
				throw new ConcurrentModificationException();
			if (_itCursor == null)
				throw new NoSuchElementException("MongoDtalge's cursor was closed.");
			try {
				Document docDtalgeElem = _itCursor.next();
				DtBase base = MongoDelegateDocDtalgeElem.toDtBaseFromDocument(docDtalgeElem);
				Object value = MongoDelegateDocDtalgeElem.getValueObjectFromDocument(docDtalgeElem);
				if (!_itCursor.hasNext()) {
					closeCursor();
				}
				return new MongoDtalgeElement(base, value);
			} catch (MongoException ex) {
				throw new MongoAlgeError("MongoDtalge's iterator couldn't get next element in MongoDtalge" + _mongo_col.getNamespace().toString() + ":", ex);
			}
		}
	}
	
	/**
	 * <code>MongoDtalge</code> クラスのデータ代数基底としての要素イテレーター。
	 * <p>
	 * このクラスは、データ代数元<code>(MongoDtalge)</code>からデータ代数要素の基底(DtBase)を
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
	private class MongoDtalgeElementBaseIterator extends AbstractMongoDtalgeElementIterator<DtBase>
	{
		MongoDtalgeElementBaseIterator(MongoCursor<? extends Document> cursor) {
			super(cursor);
		}
		
		@Override
		public DtBase next() {
			if (_expectedModCount != _modCount.get())
				throw new ConcurrentModificationException();
			if (_itCursor == null)
				throw new NoSuchElementException("MongoDtalge's cursor was closed.");
			try {
				Document docDtalgeElem = _itCursor.next();
				DtBase base = MongoDelegateDocDtalgeElem.toDtBaseFromDocument(docDtalgeElem);
				if (!_itCursor.hasNext()) {
					closeCursor();
				}
				return base;
			} catch (MongoException ex) {
				throw new MongoAlgeError("MongoDtalge's iterator couldn't get next element in MongoDtalge" + _mongo_col.getNamespace().toString() + ":", ex);
			}
		}
	}
	
	/**
	 * <code>MongoDtalge</code> クラスのデータ代数元としての要素イテレーター。
	 * <p>
	 * このクラスは、データ代数元<code>(MongoDtalge)</code>からデータ代数要素(単一の基底と値を持つデータ代数元)を
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
	private class MongoDtalgeIterator extends AbstractMongoDtalgeElementIterator<Dtalge>
	{
		MongoDtalgeIterator(MongoCursor<? extends Document> cursor) {
			super(cursor);
		}
		
		@Override
		public Dtalge next() {
			if (_expectedModCount != _modCount.get())
				throw new ConcurrentModificationException();
			if (_itCursor == null)
				throw new NoSuchElementException("MongoDtalge's cursor was closed.");
			try {
				Document docDtalgeElem = _itCursor.next();
				DtBase base = MongoDelegateDocDtalgeElem.toDtBaseFromDocument(docDtalgeElem);
				Object value = MongoDelegateDocDtalgeElem.getValueObjectFromDocument(docDtalgeElem);
				if (_itCursor.hasNext()) {
					closeCursor();
				}
				return new Dtalge(base, value);
			} catch (MongoException ex) {
				throw new MongoAlgeError("MongoDtalge's iterator couldn't get next element in MongoDtalge" + _mongo_col.getNamespace().toString() + ":", ex);
			}
		}
	}

	/**
	 * このデータ代数元に含まれる基底が指定されたパターンと一致するかを評価し、
	 * 指定された基底集合の内容を更新する。
	 * 基底パターンと一致した基底は <code>matchedBases</code> に、
	 * 一致しない基底は <code>unmatchedBases</code> に追加される。
	 * <p><b>注:</b>引数に指定されたパラメータが全て <tt>null</tt> では
	 * ないことを前提としている。
	 * このメソッドは、パラメータを評価しないため、メソッドの呼び出しには注意すること。
	 * 
	 * @param matchedBases		パターンと一致する基底を格納する基底集合への参照
	 * @param unmatchedBases	パターンと一致しない基底を格納する基底集合への参照
	 * @param pattern	評価するパターン
	 */
	protected void updateMatchedBases(BigDtBaseSet matchedBases, BigDtBaseSet unmatchedBases,
										DtBasePattern pattern)
	{
		BigIterator<DtBase> it = dtbaseIterator();
		try {
			while (it.hasNext()) {
				DtBase abase = it.next();
				if (!matchedBases.contains(abase) && !unmatchedBases.contains(abase)) {
					if (pattern.matches(abase))
						matchedBases.add(abase);
					else
						unmatchedBases.add(abase);
				}
			}
		}
		finally {
			it.closeCursor();
		}
	}
	
	/**
	 * このデータ代数元に含まれる基底が指定されたパターンと一致するかを評価し、
	 * 指定された基底集合の内容を更新する。
	 * 基底パターンと一致した基底は <code>matchedBases</code> に、
	 * 一致しない基底は <code>unmatchedBases</code> に追加される。
	 * <p><b>注:</b>引数に指定されたパラメータが全て <tt>null</tt> ではない
	 * ことを前提としている。
	 * このメソッドは、パラメータを評価しないため、メソッドの呼び出しには注意すること。
	 * 
	 * @param matchedBases		パターンと一致する基底を格納する基底集合への参照
	 * @param unmatchedBases	パターンと一致しない基底を格納する基底集合への参照
	 * @param patterns	評価するパターンの集合
	 */
	protected void updateMatchedBases(BigDtBaseSet matchedBases, BigDtBaseSet unmatchedBases,
										DtBasePatternSet patterns)
	{
		BigIterator<DtBase> it = dtbaseIterator();
		try {
			while (it.hasNext()) {
				DtBase abase = it.next();
				if (!matchedBases.contains(abase) && !unmatchedBases.contains(abase)) {
					if (patterns.matches(abase))
						matchedBases.add(abase);
					else
						unmatchedBases.add(abase);
				}
			}
		}
		finally {
			it.closeCursor();
		}
	}

	//------------------------------------------------------------
	// Internal methods for I/O
	//------------------------------------------------------------
	
	/**
	 * CSVフォーマットで Dtalge の内容を出力する。
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
		// データ出力
		BigIterator<BigDtalgeElement> it = elementIterator();
		try {
			while (it.hasNext()) {
				BigDtalgeElement elem = it.next();
				//--- value
				Object value = elem.getValue();
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
				elem.getBase().writeFieldToCSV(writer);
				//--- new line
				writer.newLine();
			}
		}
		finally {
			it.closeCursor();
		}
		writer.flush();
	}

	/**
	 * 順序指定用基底集合に従い、このオブジェクトのすべての基底を並べ替えた新しい基底集合を返す。
	 * 順序指定用基底集合に含まれない基底は、オリジナルの基底集合の順序に従い、新しい基底集合の終端に追加される。
	 * なお、並べ替えが必要ない場合は、オリジナルの基底集合オブジェクトをそのまま返す。
	 * @param baseOrder	順序指定用基底集合
	 * @return	並べ替え済みの新しい基底集合を返す。並べ替えが行われなかった場合は、並べ替え対象の基底集合を返す。
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	protected Set<DtBase> getOrderedBaseSet(final Collection<? extends DtBase> baseOrder) {
		if (isEmpty()) {
			//--- このデータ代数元が空の場合は、空の基底集合を返す。
			return new LinkedHashSet<DtBase>();
		}
		
		// 基底集合の生成
		LinkedHashSet<DtBase> bases;
		if (baseOrder == null || baseOrder.isEmpty()) {
			bases = new LinkedHashSet<DtBase>();
		} else {
			bases = new LinkedHashSet<DtBase>(baseOrder);
		}
		
		// 順序指定基底集合から、このデータ代数に含まれない基底を除外
		Iterator<DtBase> it = bases.iterator();
		while (it.hasNext()) {
			if (!containsBase(it.next())) {
				//--- 除外
				it.remove();
			}
		}
		
		// このデータ代数の基底のうち、順序指定基底集合に含まれないものを終端に追加
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = distinctByDtBase().iterator();
			while (cursor.hasNext()) {
				Document docDtBaseElem = cursor.next();
				DtBase base = MongoDelegateDocDtBase.toDtBase(docDtBaseElem);
				if (!bases.contains(base)) {
					//--- 追加
					bases.add(base);
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get all DtBases into new Collection from MongoDtalge" + _mongo_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		
		// 完了
		return bases;
	}

	/**
	 * テーブル形式のCSVフォーマットで Dtalge の内容を出力する。
	 * 基底のない要素は空文字をフィールドに出力し、
	 * <tt>null</tt> の場合は <tt>null</tt> を示す特殊値を出力する。
	 * 特殊記号で始まる値は、特殊記号でエスケープする。
	 * <br>順序指定用基底集合が指定された場合、その基底集合の順序の通りに基底を出力する。
	 * 順序指定用基底集合に含まれない基底は、行の終端にオリジナルの順序で出力する。
	 * <br><em>withoutNull</em> に <tt>true</tt> を指定した場合、<tt>null</tt> 値は出力せず、
	 * フィールドは空欄となる。
	 * @param writer	CSVファイル出力オブジェクト
	 * @param baseOrder	順序指定用基底コレクション。順序を指定しない場合は <tt>null</tt>
	 * @param withoutNull	<tt>null</tt> を出力しない場合は <tt>true</tt>
	 * @throws IOException	入出力エラーが発生した場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	protected void writeToTableCsv_v2(CsvWriter writer, Collection<DtBase> baseOrder, boolean withoutNull)
		throws IOException
	{
		if (!isEmpty()) {
			// 順序指定に順じた基底集合を取得
			Set<DtBase> bases = getOrderedBaseSet(baseOrder);
			
			// 基底情報を出力
			writeBasesToTableCsv(writer, bases);
			
			// 要素値を出力
			if (withoutNull) {
				//--- null や空文字は出力しない
				writeValuesToTableCsv_v2(writer, bases, Dtalge.CSV_VALUE_EMPTY);
			} else {
				//--- 空文字を null として出力
				writeValuesToTableCsv_v2(writer, bases, Dtalge.CSV_COMMAND_NULL);
			}
			
			writer.flush();
		}
	}

	/**
	 * テーブル形式のCSVフォーマットで、全ての DtBase の内容を出力
	 * <p><b>(注)</b>
	 * <blockquote>
	 * <em>bases</em> の要素が空ではないこと。このメソッドではチェックしない。
	 * </blockquote>
	 * 
	 * @param writer	CSVファイル出力オブジェクト
	 * @param bases		出力対象の基底の集合
	 * @throws IOException	入出力エラーが発生した場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	static protected void writeBasesToTableCsv(CsvWriter writer, Set<DtBase> bases)
		throws IOException
	{
		assert !bases.isEmpty();
		
		// バッファ作成
		StringBuilder sbName = new StringBuilder();
		StringBuilder sbType = new StringBuilder();
		StringBuilder sbAttr = new StringBuilder();
		StringBuilder sbSubj = new StringBuilder();
		
		// 先頭の要素を出力
		Iterator<DtBase> it = bases.iterator();
		DtBase base = it.next();
		sbName.append(writer.enquote(base.getNameKey()));
		sbType.append(writer.enquote(base.getTypeKey()));
		sbAttr.append(writer.enquote(base.getAttributeKey()));
		sbSubj.append(writer.enquote(base.getSubjectKey()));
		
		// ２番目以降の要素を出力
		char delim = writer.getDelimiterChar();
		while (it.hasNext()) {
			base = it.next();
			//--- name
			sbName.append(delim);
			sbName.append(writer.enquote(base.getNameKey()));
			//--- type
			sbType.append(delim);
			sbType.append(writer.enquote(base.getTypeKey()));
			//--- attr
			sbAttr.append(delim);
			sbAttr.append(writer.enquote(base.getAttributeKey()));
			//--- subject
			sbSubj.append(delim);
			sbSubj.append(writer.enquote(base.getSubjectKey()));
		}
		
		// 基底要素をCSVレコードとして出力
		//--- name
		writer.writeLine(sbName.toString());
		//--- type
		writer.writeLine(sbType.toString());
		//--- attr
		writer.writeLine(sbAttr.toString());
		//--- subject
		writer.writeLine(sbSubj.toString());
	}

	/**
	 * 指定された基底集合の順序に従い、この Dtalge の要素の値を
	 * テーブル形式のCSVフォーマットで出力する。
	 * 基底のない要素は空文字をフィールドに出力し、
	 * <tt>null</tt> の場合は <tt>null</tt> を示す特殊値を出力する。
	 * 特殊記号で始まる値は、特殊記号でエスケープする。
	 * <br>
	 * このメソッドでは、この Dtalge の要素の値を、指定された基底集合の基底順序で
	 * 1レコードに出力する。
	 * 
	 * <p><b>(注)</b>
	 * <blockquote>
	 * <em>bases</em> の要素が空ではないこと。このメソッドではチェックしない。
	 * </blockquote>
	 * 
	 * @param writer		CSVファイル出力オブジェクト
	 * @param bases			出力対象の基底順序集合
	 * @param nullKeyword	<tt>null</tt> を表す特殊値
	 * @throws IOException	入出力エラーが発生した場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	protected void writeValuesToTableCsv_v2(CsvWriter writer, Set<DtBase> bases, String nullKeyword)
		throws IOException
	{
		assert !bases.isEmpty();

		for (DtBase base : bases) {
			if (!containsBase(base)) {
				// 基底なし
				writer.writeField(Dtalge.CSV_VALUE_EMPTY);	// 基底なしは空文字を出力
			}
			else {
				Object value = get(base);
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
		}
		
		writer.newLine();
	}

	/**
	 * CSVフォーマットで Dtalge の内容を読み込む
	 * @param reader CSVファイル入力オブジェクト
	 * @return	このデータ代数が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	protected boolean readFromCsv(CsvReader reader)
		throws IOException, CsvFormatException
	{
		CsvReader.CsvRecord record;
		Dtalge.CsvFileType	csvType = null;
		
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
				throw new CsvFormatException("Illegal DtAlgeSet file ID.", record.getLineNo(), 1);
			}
			// 以降のフィールドは無視
		}
		
		// データ読み込み
		boolean modified = false;
		if (csvType.isTable()) {
			// テーブル形式のCSVファイル読み込み
			List<DtBase> baselist = readBasesFromTableCsv(reader);
			if (baselist != null) {
				if (csvType.version() == Dtalge.CsvFileType.V2) {
					//--- テーブル形式 v2
					while ((record = reader.readRecord()) != null) {
						if (readValuesFromTableCsv_v2(record, baselist)) {
							//--- modified
							modified = true;
						}
					}
				} else {
					//--- 初期テーブル形式
					while ((record = reader.readRecord()) != null) {
						if (readValuesFromTableCsv(record, baselist)) {
							//--- modified
							modified = true;
						}
					}
				}
			}
		}
		else {
			// CSV標準形の読み込み
			if (csvType.version() == Dtalge.CsvFileType.V2) {
				//--- v2
				while ((record = reader.readRecord()) != null) {
					// 空行、もしくは値のないレコードはスキップ
					if (!record.hasFields() || !record.hasValues()) {
						continue;
					}
					// フィールドの値読み出し
					if (readRecordFromCsv_v2(record)) {
						//--- modified
						modified = true;
					}
				}
			} else {
				//--- 初期形式
				while ((record = reader.readRecord()) != null) {
					// 空行、もしくは値のないレコードはスキップ
					if (!record.hasFields() || !record.hasValues()) {
						continue;
					}
					// フィールドの値読み出し
					if (readRecordFromCsv(record)) {
						//--- modified
						modified = true;
					}
				}
			}
		}
		return modified;
	}

	/**
	 * CSVフォーマットの1レコードの内容を読み込む。
	 * <br>値のフィールドが空欄の場合、<tt>null</tt> の値として読み込む。
	 * なお、このメソッドでは特殊記号に関する処理は行わない。
	 * 
	 * @param recReader		CSVフォーマットの1レコード入力オブジェクト
	 * @return	このデータ代数が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws IOException	入出力エラーが発生した場合
	 * @throws CsvFormatException	カラムのデータが正しくない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	protected boolean readRecordFromCsv(CsvReader.CsvRecord record)
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
		return this.putValue(newBase, value);
	}
	
	/**
	 * CSVフォーマットの1レコードの内容を読み込む。
	 * <br>値のフィールドが空欄の場合、<tt>null</tt> の値として読み込む。
	 * また、特殊記号で始まる値は、特殊値として読み込む。
	 * 
	 * @param recReader		CSVフォーマットの1レコード入力オブジェクト
	 * @return	このデータ代数が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws IOException	入出力エラーが発生した場合
	 * @throws CsvFormatException	カラムのデータが正しくない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	protected boolean readRecordFromCsv_v2(CsvReader.CsvRecord record)
		throws IOException, CsvFormatException
	{
		// setup field reader
		CsvReader.CsvFieldReader freader = new CsvReader.CsvFieldReader(record);
		
		// value
		String strValue = freader.readValue();
//		if (Strings.isNullOrEmpty(strValue)) {
//			// 長さ 0 の文字列も null とみなす
//			strValue = null;
//		}
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
		return this.putValue(newBase, value);
	}

	/**
	 * テーブル形式のCSVフォーマットから基底のレコードを読み込む。
	 * @param reader	CSVファイル入力オブジェクト
	 * @return	読み込んだ基底のリスト。フィールドの順序の通りに基底が格納される。
	 * @throws IOException	入出力エラーが発生した場合
	 * @throws CsvFormatException	カラムのデータが正しくない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	protected List<DtBase> readBasesFromTableCsv(CsvReader reader)
		throws IOException, CsvFormatException
	{
		CsvReader.CsvRecord record;
		
		// name キーのレコードチェック
		record = reader.readRecord();
		if (record == null) {
			// レコードは存在しない
			return null;
		}
		else if (!record.hasFields()) {
			// 以降、フィールドの有無をチェック
			int errLineNo = record.getLineNo();
			int errFieldNo = 1;
			while ((record = reader.readRecord()) != null) {
				if (record.hasFields() || record.hasValues()) {
					// フィールドが存在する場合、名前キーが省略されたとみなしエラーとする。
					throw new CsvFormatException("name key cannot be omitted.", errLineNo, errFieldNo);
				}
			}
			// フィールドが存在しないため、空の要素とみなす
			return null;
		}
		
		// バッファの生成
		ArrayList<DtBase.BaseKeyContainer> keylist = new ArrayList<DtBase.BaseKeyContainer>();
		
		// name キーの読み込み
		DtBase.readNameKeyFieldsFromTableCsv(record, keylist);
		
		// type キーの読み込み
		record = reader.readRecord();
		if (record == null) {
			// type キーの省略は許可しない
			throw new CsvFormatException("type key cannot be omitted.", reader.getLineNo(), 1);
		}
		DtBase.readTypeKeyFieldsFromTableCsv(record, keylist);
		
		// attr キーの読み込み
		record = reader.readRecord();
		if (record != null) {
			DtBase.readAttributeKeyFieldsFromTableCsv(record, keylist);
		}
		
		// subject キーの読み込み
		record = reader.readRecord();
		if (record != null) {
			DtBase.readSubjectKeyFieldsFromTableCsv(record, keylist);
		}
		
		// 基底キーの集合を生成
		return DtBase.makeDtBaseListByKeyContainers(keylist);
	}

	/**
	 * テーブル形式のCSVフォーマットから、値のレコードを1レコード読み込む。
	 * <br>値のフィールドが空欄の場合、<tt>null</tt> の値として読み込む。
	 * なお、このメソッドでは特殊記号に関する処理は行わない。
	 * @param record	CSVフォーマットの1レコード入力オブジェクト
	 * @param baselist	基底のリスト。フィールドの順序通りに読み込まれた基底が格納されていること。
	 * @return	このデータ代数が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws IOException	入出力エラーが発生した場合
	 * @throws CsvFormatException	カラムのデータが正しくない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	protected boolean readValuesFromTableCsv(CsvReader.CsvRecord record, List<DtBase> baselist)
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
			if (this.putValue(newBase, value)) {
				//--- modified
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
	 * @return	このデータ代数が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws IOException	入出力エラーが発生した場合
	 * @throws CsvFormatException	カラムのデータが正しくない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	protected boolean readValuesFromTableCsv_v2(CsvReader.CsvRecord record, List<DtBase> baselist)
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
				if (this.putValue(newBase, value)) {
					//--- modified
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
}
