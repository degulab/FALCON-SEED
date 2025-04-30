/*
 * @(#)MongoExalge.java	0.992	2020/03/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MongoExalge.java	0.991	2019/02/23
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MongoExalge.java	0.990	2018/11/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package exalge2.db.mongo;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
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
import com.mongodb.client.result.UpdateResult;

import exalge2.ExBase;
import exalge2.ExBasePattern;
import exalge2.ExBasePatternSet;
import exalge2.ExBaseSet;
import exalge2.ExTransfer;
import exalge2.Exalge;
import exalge2.ExtendedKeyID;
import exalge2.TransMatrix;
import exalge2.TransTable;
import exalge2.db.BigExBaseSet;
import exalge2.db.BigExalge;
import exalge2.db.BigExalgeElement;
import redundantalge.db.BigIterator;
import redundantalge.db.mongo.MongoAlgeError;
import redundantalge.db.mongo.MongoSession;
import redundantalge.db.mongo.MongoUtil;

/**
 * MongoDB 上で、交換代数の基底と値を保持する交換代数クラス。
 * <p>このクラスは、基本的に不変オブジェクト(Immutable)として扱えるようにインタフェースが実装されている。
 * ただし、外部からデータベースのコレクションが変更された場合はその限りではない。
 * <br>
 * また、<b>この実装は同期化されない</b>。
 * <p>
 * <b>《入出力フォーマット》</b>
 * <br>
 * <code>MongoExalge</code> の MongoDB におけるデータ構造は、次の通りである。
 * <pre><code>
 * {
 *   &quot;base&quot; {
 *     &quot;hat&quot; : &lt;boolean&gt;(NO_HAT=false | HAT=true)
 *     &quot;name&quot; : &lt;string&gt;
 *     &quot;unit&quot; : &lt;string&gt;
 *     &quot;time&quot; : &lt;string&gt;
 *     &quot;subject&quot; : &lt;string&gt;
 *   },
 *   &quot;value&quot; : &lt;null or BigDecimal&gt;
 * }
 * </code></pre>
 * 
 * @version 0.992
 * @since 0.990
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Li Hou(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 *
 */
public class MongoExalge implements BigExalge
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	/** 一時的なコレクション名のプレフィックス **/
	static public final String	TEMP_COLNAME_PREFIX	= "Tal";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	protected MongoSession				_mongo_session;
	protected MongoCollection<Document>	_mongo_col;
	protected AtomicLong				_modCount = new AtomicLong(0L);
	/** 交換代数集合の交換代数元 ID、未定義の場合は <tt>null</tt> **/
	protected final String				_elem_id;
	protected final AtomicLong			_modExAlgeSetCount;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定された MongoDB セッションにおいて、一時的なコレクションをストレージとする新しいインスタンスを生成する。
	 * <p>このメソッドで作成されたコレクションは、MongoDB セッションが切断されるときに破棄される。
	 * @param session	MongoDB セッションオブジェクト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public MongoExalge(MongoSession session) {
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
	public MongoExalge(MongoSession session, String collection) {
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
		_elem_id = null;
		_modExAlgeSetCount = null;
	}

	/**
	 * 指定された交換代数集合の要素を操作対象とする新しいインスタンスを生成する。
	 * <p>このメソッドでは、指定されたコレクションを一時コレクションとして登録しない。
	 * @param algeset	対象とする交換代数集合
	 * @param elem_id	対象とする交換代数元 ID
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>elem_id</em> が空文字列の場合
	 */
	MongoExalge(MongoExAlgeSet algeset, String elem_id) {
		_mongo_session = algeset._mongo_session;
		_mongo_col     = algeset._mongo_vals_col;
		_modExAlgeSetCount = algeset._modCount;
		if (elem_id.isEmpty())
			throw new IllegalArgumentException("'elem_id' is empty!");
		_elem_id = elem_id;
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
		if (_elem_id != null) {
			// 交換代数集合の要素であれば、新しいコレクションにコピー
			ArrayList<Bson> list = new ArrayList<Bson>();
			list.add(Aggregates.match(new Document("$" + MongoDelegateDocExAlgeSetElem.MONGO_KEY_SETID, _elem_id)));
			list.add(Aggregates.project(new Document("_id", 0).append(MongoDelegateDocExAlgeSetElem.MONGO_KEY_SETID, 0)));
			list.add(Aggregates.out(newCollectionName));
			_mongo_col.aggregate(list).toCollection();	// toCollection() を呼ばないとコレクションに出力されない
			_mongo_col = _mongo_session.getPersistentCollection(newCollectionName);
		}
		else {
			// 単独の交換代数元であれば、コレクション名を変更
			try {
				_mongo_col.renameCollection(newName);
			}
			catch (MongoException ex) {
				throw new MongoAlgeError("Failed to persist of MongoExalge[" + _mongo_col.getNamespace().toString() + "] :", ex);
			}
			//--- 一時的コレクションから除外
			_mongo_session.unregisterTemporaryCollection(oldName);
		}
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
		_mongo_session.unregisterTemporaryCollection(_mongo_col.getNamespace());
	}

	/**
	 * 交換代数の要素が空であることを示す。
	 * 
	 * @return 要素が一つも存在しない場合に true を返す。
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean isEmpty() {
		return (getNumElements() == 0L);
	}
	
	/**
	 * 交換代数の要素数を返す。
	 * 
	 * @return 交換代数の要素数
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public long getNumElements() {
		try {
			return _mongo_col.countDocuments(makeQeuryAll());
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to count documents in MongoExalge[" + _mongo_col.getNamespace().toString() + "] :", ex);
		}
	}

	/**
	 * 指定された基底が、このインスタンスの要素に含まれているかを示す。
	 * 
	 * @param base 交換代数基底
	 * @return 指定の基底が要素に含まれている場合に <tt>true</tt> を返す。
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean containsBase(ExBase base) {
		if (base == null)
			return false;
		try {
			return (_mongo_col.countDocuments(makeQueryByExBase(base)) > 0L);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to contain ExBase in MongoExalge[" + _mongo_col.getNamespace().toString() + "] :", ex);
		}
	}

	/**
	 * 指定された基底が、このインスタンスの要素に全て含まれているかを示す。
	 * 指定された基底集合の要素が空の場合、このメソッドは <tt>true</tt> を返す。
	 * 
	 * @param bases	交換代数基底集合
	 * @return	指定された基底集合の全ての基底が、この交換代数元に含まれている場合に <tt>true</tt> を返す。
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean containsAllBases(ExBaseSet bases) {
		for (ExBase base : bases) {
			if (!containsBase(base))
				return false;
		}
		return true;
	}

	/**
	 * 指定された基底が、このインスタンスの要素に全て含まれているかを示す。
	 * 指定された基底集合の要素が空の場合、このメソッドは <tt>true</tt> を返す。
	 * 
	 * @param bases	交換代数基底集合
	 * @return	指定された基底集合の全ての基底が、この交換代数元に含まれている場合に <tt>true</tt> を返す。
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean containsAllBases(BigExBaseSet bases) {
		for (ExBase base : bases) {
			if (!containsBase(base))
				return false;
		}
		return true;
	}

	/**
	 * 指定された基底のどれか 1 つが、このインスタンスの要素に含まれているかを示す。
	 * <p>このメソッドは、指定された基底集合に含まれるどれか 1 つが、この
	 * 交換代数元の要素に存在した場合に <tt>true</tt> を返す。
	 * 
	 * @param bases	検証する基底の集合
	 * @return	指定された基底集合のどれか 1 つが含まれている場合に <tt>true</tt> を返す。
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean containsAnyBases(ExBaseSet bases) {
		if (!bases.isEmpty()) {
			if (this.getNumElements() < bases.size()) {
				MongoCursor<? extends Document> cursor = null;
				try {
					cursor = findAllDocuments(true, true).iterator();
					while (cursor.hasNext()) {
						ExBase thisBase = MongoDelegateDocExalgeElem.toExBaseFromDocument(cursor.next());
						if (thisBase != null && bases.contains(thisBase)) {
							return true;
						}
					}
				}
				catch (MongoException ex) {
					throw new MongoAlgeError("Failed to get document from MongoExalge[" + _mongo_col.getNamespace().toString() + "] :", ex);
				}
				finally {
					MongoUtil.closeCursorSilent(cursor);
				}
			} else {
				for (ExBase base : bases) {
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
	 * 交換代数元の要素に存在した場合に <tt>true</tt> を返す。
	 * 
	 * @param bases	検証する基底の集合
	 * @return	指定された基底集合のどれか 1 つが含まれている場合に <tt>true</tt> を返す。
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean containsAnyBases(BigExBaseSet bases) {
		if (!bases.isEmpty()) {
			if (this.getNumElements() < bases.size()) {
				MongoCursor<? extends Document> cursor = null;
				try {
					cursor = findAllDocuments(true, true).iterator();
					while (cursor.hasNext()) {
						ExBase thisBase = MongoDelegateDocExalgeElem.toExBaseFromDocument(cursor.next());
						if (thisBase != null && bases.contains(thisBase)) {
							return true;
						}
					}
				}
				catch (MongoException ex) {
					throw new MongoAlgeError("Failed to get document from MongoExalge[" + _mongo_col.getNamespace().toString() + "] :", ex);
				}
				finally {
					MongoUtil.closeCursorSilent(cursor);
				}
			} else {
				for (ExBase base : bases) {
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
	 * @param value 交換代数の値
	 * @return 指定の値が要素に含まれている場合に <tt>true</tt> を返す。
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean containsValue(BigDecimal value) {
		try {
			long num = _mongo_col.countDocuments(makeQueryByValue(value));
			return (num > 0L);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to find document in MongoExalge[" + _mongo_col.getNamespace().toString() + "] :", ex);
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
	 * このオブジェクトの交換代数要素にアクセスする変更不可能なイテレーターを取得する。
	 * @return	このオブジェクトに含まれる交換代数要素の変更不可能なイテレーター
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public BigIterator<BigExalgeElement> elementIterator() {
		return newUnmodifiableElementIterator();
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
	 * <code>Exalge</code> の要素の変更不可能な反復子を返す。
	 * 要素が返されるときの順序は、このクラスのコレクションの実装に依存する。
	 * 
	 * @return 交換代数要素の <code>Iterator</code>
	 * 
	 * @see ConcurrentModificationException
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public BigIterator<Exalge> iterator() {
		return newMongoExalgeIterator();
	}

	/**
	 * 2つの交換代数が同値であるかを検証する。
	 * <br>
	 * このメソッドは、インスタンスの同一性ではなく、同値性を評価する。
	 * <br>{@link #isEqualValues(Exalge)} とは異なり、実数値 0 の基底も
	 * 評価対象となる。
	 * <br>
	 * 2つの交換代数が同値であるのは、次の場合となる。
	 * <ul>
	 * <li>2つの交換代数において、同一の基底が存在する。
	 * <li>2つの交換代数において、同一(等しい)基底に対する値が全て同値である。
	 * </ul>
	 * <p>
	 * 現在の実装において、このメソッドにより同等と判定された場合でも、
	 * 2つの <code>Exalge</code> インスタンスが返すハッシュコードが一致するとは
	 * 限らない。
	 * 
	 * @param alge 同値性を比較する対象の交換代数
	 * 
	 * @return 2つの交換代数が同値である場合は <tt>true</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean isSameValues(Exalge alge) {
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
				Document   thisDoc   = cursor.next();
				ExBase     thisBase  = MongoDelegateDocExalgeElem.toExBaseFromDocument(thisDoc);
				BigDecimal thisValue = MongoDelegateDocExalgeElem.getBigDecimalValueFromDocument(cursor.next());
				if (!alge.containsBase(thisBase))
					return false;
				BigDecimal thatValue = alge.getRealValue(thisBase);
				if (thisValue!=thatValue && (thisValue==null || thatValue==null || thisValue.compareTo(thatValue)!=0)) {
					// どちらか一方が null、もしくは実数値が同値ではない
					return false;
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to judge as same between Exalge and MongoExalge[" + _mongo_col.getNamespace().toString() + "] :", ex);
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
	 * 2つの交換代数が同値であるかを検証する。
	 * <br>
	 * このメソッドは、インスタンスの同一性ではなく、同値性を評価する。
	 * <br>{@link #isEqualValues(Exalge)} とは異なり、実数値 0 の基底も
	 * 評価対象となる。
	 * <br>
	 * 2つの交換代数が同値であるのは、次の場合となる。
	 * <ul>
	 * <li>2つの交換代数において、同一の基底が存在する。
	 * <li>2つの交換代数において、同一(等しい)基底に対する値が全て同値である。
	 * </ul>
	 * <p>
	 * 現在の実装において、このメソッドにより同等と判定された場合でも、
	 * 2つの <code>Exalge</code> インスタンスが返すハッシュコードが一致するとは
	 * 限らない。
	 * 
	 * @param alge 同値性を比較する対象の交換代数
	 * 
	 * @return 2つの交換代数が同値である場合は <tt>true</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean isSameValues(BigExalge alge) {
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
				Document   thisDoc   = cursor.next();
				ExBase     thisBase  = MongoDelegateDocExalgeElem.toExBaseFromDocument(thisDoc);
				BigDecimal thisValue = MongoDelegateDocExalgeElem.getBigDecimalValueFromDocument(cursor.next());
				if (!alge.containsBase(thisBase))
					return false;
				BigDecimal thatValue = alge.getRealValue(thisBase);
				if (thisValue!=thatValue && (thisValue==null || thatValue==null || thisValue.compareTo(thatValue)!=0)) {
					// どちらか一方が null、もしくは実数値が同値ではない
					return false;
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to judge as same between BigExalge and MongoExalge[" + _mongo_col.getNamespace().toString() + "] :", ex);
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
	 * 2つの交換代数が等しいかを検証する。
	 * <br>
	 * このメソッドは、インスタンスの同一性ではなく、値としての同等性を評価する。
	 * <br>{@link #isSameValues(Exalge)} とは異なり、実数値 0 の基底は
	 * 評価対象としない。
	 * <br>
	 * 2つの交換代数が同等であるのは、次の場合となる。
	 * <ul>
	 * <li>要素の値が 0 のものは無視する。
	 * <li>要素の値が 0 ではないものの基底が、2つの交換代数において全て一致する。
	 * <li>2つの交換代数において、同一(等しい)基底に対する値が全て同値である。
	 * </ul>
	 * <p>
	 * 現在の実装において、このメソッドにより同等と判定された場合でも、
	 * 2つの <code>Exalge</code> インスタンスが返すハッシュコードが一致するとは
	 * 限らない。
	 * <b>注：</b>
	 * <blockquote>
	 * 値が <tt>null</tt> のものは、無視しない。
	 * </blockquote>
	 * 
	 * @param alge 同等性を比較する対象の交換代数
	 * 
	 * @return 2つの交換代数が等しい場合は <tt>true</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean isEqualValues(Exalge alge) {
		if (alge == null) {
			return false;
		}
		
		// サイズを検証
		long sizeThis = getNumElements();
		long sizeAlge = alge.getNumElements();
		if (sizeThis <= 0 && sizeAlge <= 0) {
			// どちらも空の元なら、同等とみなす
			return true;
		}
		
		// すべての基底を比較
		MongoExBaseSet allBases = getBases();
		try {
			// 合成基底集合を生成
			allBases.addAll(alge.getUnmodifiableExBaseSet());
			// 全ての値を比較
			for (ExBase base : allBases) {
				boolean existThis = this.containsBase(base);
				boolean existAlge = alge.containsBase(base);
				BigDecimal thisValue = this.getRealValue(base);
				BigDecimal algeValue = alge.getRealValue(base);
				if (existThis) {
					//--- base exists in this
					if (existAlge) {
						//--- base exists in this & alge
						if (thisValue!=algeValue && (thisValue==null || algeValue==null || thisValue.compareTo(algeValue)!=0)) {
							// どちらか一方が null、もしくは実数値が同値ではない場合は、同等ではない
							return false;
						}
					} else {
						//--- base only exists in this
						if (thisValue==null || thisValue.compareTo(BigDecimal.ZERO)!=0) {
							// this にのみ存在する基底の値が 0 以外の場合は、同等ではない
							return false;
						}
					}
				}
				else {
					//--- base exists in alge
					if (algeValue==null || algeValue.compareTo(BigDecimal.ZERO)!=0) {
						// alge にのみ存在する基底の値が 0 以外の場合は、同等ではない
						return false;
					}
				}
			}
		}
		finally {
			allBases.delete();
		}
		
		// 全て同値
		return true;
	}

	/**
	 * 2つの交換代数が等しいかを検証する。
	 * <br>
	 * このメソッドは、インスタンスの同一性ではなく、値としての同等性を評価する。
	 * <br>{@link #isSameValues(Exalge)} とは異なり、実数値 0 の基底は
	 * 評価対象としない。
	 * <br>
	 * 2つの交換代数が同等であるのは、次の場合となる。
	 * <ul>
	 * <li>要素の値が 0 のものは無視する。
	 * <li>要素の値が 0 ではないものの基底が、2つの交換代数において全て一致する。
	 * <li>2つの交換代数において、同一(等しい)基底に対する値が全て同値である。
	 * </ul>
	 * <p>
	 * 現在の実装において、このメソッドにより同等と判定された場合でも、
	 * 2つの <code>Exalge</code> インスタンスが返すハッシュコードが一致するとは
	 * 限らない。
	 * <b>注：</b>
	 * <blockquote>
	 * 値が <tt>null</tt> のものは、無視しない。
	 * </blockquote>
	 * 
	 * @param alge 同等性を比較する対象の交換代数
	 * 
	 * @return 2つの交換代数が等しい場合は <tt>true</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean isEqualValues(BigExalge alge) {
		if (alge == null) {
			return false;
		}
		
		// サイズを検証
		long sizeThis = getNumElements();
		long sizeAlge = alge.getNumElements();
		if (sizeThis <= 0 && sizeAlge <= 0) {
			// どちらも空の元なら、同等とみなす
			return true;
		}
		
		// すべての基底を比較
		MongoExBaseSet allBases = getBases();
		BigIterator<ExBase> baseit = null;
		try {
			// 合成基底集合を生成
			baseit = alge.exbaseIterator();
			while (baseit.hasNext()) {
				allBases.add(baseit.next());
			}
			// 全ての値を比較
			for (ExBase base : allBases) {
				boolean existThis = this.containsBase(base);
				boolean existAlge = alge.containsBase(base);
				BigDecimal thisValue = this.getRealValue(base);
				BigDecimal algeValue = alge.getRealValue(base);
				if (existThis) {
					//--- base exists in this
					if (existAlge) {
						//--- base exists in this & alge
						if (thisValue!=algeValue && (thisValue==null || algeValue==null || thisValue.compareTo(algeValue)!=0)) {
							// どちらか一方が null、もしくは実数値が同値ではない場合は、同等ではない
							return false;
						}
					} else {
						//--- base only exists in this
						if (thisValue==null || thisValue.compareTo(BigDecimal.ZERO)!=0) {
							// this にのみ存在する基底の値が 0 以外の場合は、同等ではない
							return false;
						}
					}
				}
				else {
					//--- base exists in alge
					if (algeValue==null || algeValue.compareTo(BigDecimal.ZERO)!=0) {
						// alge にのみ存在する基底の値が 0 以外の場合は、同等ではない
						return false;
					}
				}
			}
		}
		finally {
			if (baseit != null) {
				baseit.closeCursor();
			}
			allBases.delete();
		}
		
		// 全て同値
		return true;
	}

	/**
	 * この交換代数元に含まれる基底を一つだけ取り出す。
	 * <p>
	 * このメソッドは、{@link #projection(ExBase)} によって要素が一つだけ
	 * 格納されている交換代数元から、その基底を取り出すために利用する。
	 * 取り出される基底は、このインスタンスの実装に依存する。そのため、
	 * 複数の要素が格納されている場合、どの基底が取り出されるかを保証する
	 * ものではない。
	 * <p>
	 * 要素が一つも存在しない場合、このメソッドは例外をスローする。
	 * 
	 * @return	この交換代数元に含まれる基底の一つを返す。
	 * 
	 * @throws java.util.NoSuchElementException	要素が存在しない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public ExBase getOneBase() {
		Document doc = findAllDocuments(true, true).first();
		if (doc == null)
			throw new NoSuchElementException();
		return MongoDelegateDocExalgeElem.toExBaseFromDocument(doc);
	}
	
	/**
	 * この交換代数元に含まれる全ての基底を取り出す。
	 * 
	 * @return この交換代数元に含まれる全ての基底の集合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExBaseSet getBases() {
		MongoExBaseSet retBases = new MongoExBaseSet(_mongo_session);
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = findAllDocuments(true, true).iterator();
			while (cursor.hasNext()) {
				retBases.add(MongoDelegateDocExalgeElem.toExBaseFromDocument(cursor.next()));
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get all ExBases into new Collection from MongoExalge[" + _mongo_col.getNamespace().toString() + "] :", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		return retBases;
	}
	
	/**
	 * 指定された文字列の配列に含まれるどれか一つの文字列と一致する
	 * 名前キーを持つ基底のみを取得する。
	 * 
	 * @param values	検索キーとなる文字列の配列
	 * @return	指定の文字列と一致する名前キーを持つ基底の集合を返す。
	 * 			一致する基底が見つからなかった場合は、要素が空の基底集合を返す。
	 * 			<code>values</code> の要素が空の場合も、要素が空の基底集合を返す。
	 * 
	 * @throws NullPointerException	<code>values</code> が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExBaseSet getBasesByNameKey(String[] values) {
		return getBasesByBaseKey(ExBase.KEY_NAME, makeBaseKeyTargetSet(values));
	}
	
	/**
	 * 指定された文字列の配列に含まれるどれか一つの文字列と一致する
	 * 単位キーを持つ基底のみを取得する。
	 * 
	 * @param values	検索キーとなる文字列の配列
	 * @return	指定の文字列と一致する単位キーを持つ基底の集合を返す。
	 * 			一致する基底が見つからなかった場合は、要素が空の基底集合を返す。
	 * 			<code>values</code> の要素が空の場合も、要素が空の基底集合を返す。
	 * 
	 * @throws NullPointerException	<code>values</code> が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExBaseSet getBasesByUnitKey(String[] values) {
		return getBasesByBaseKey(ExBase.KEY_EXT_UNIT, makeBaseKeyTargetSet(values));
	}
	
	/**
	 * 指定された文字列の配列に含まれるどれか一つの文字列と一致する
	 * 時間キーを持つ基底のみを取得する。
	 * 
	 * @param values	検索キーとなる文字列の配列
	 * @return	指定の文字列と一致する時間キーを持つ基底の集合を返す。
	 * 			一致する基底が見つからなかった場合は、要素が空の基底集合を返す。
	 * 			<code>values</code> の要素が空の場合も、要素が空の基底集合を返す。
	 * 
	 * @throws NullPointerException	<code>values</code> が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExBaseSet getBasesByTimeKey(String[] values) {
		return getBasesByBaseKey(ExBase.KEY_EXT_TIME, makeBaseKeyTargetSet(values));
	}
	
	/**
	 * 指定された文字列の配列に含まれるどれか一つの文字列と一致する
	 * 主体キーを持つ基底のみを取得する。
	 * 
	 * @param values	検索キーとなる文字列の配列
	 * @return	指定の文字列と一致する主体キーを持つ基底の集合を返す。
	 * 			一致する基底が見つからなかった場合は、要素が空の基底集合を返す。
	 * 			<code>values</code> の要素が空の場合も、要素が空の基底集合を返す。
	 * 
	 * @throws NullPointerException	<code>values</code> が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExBaseSet getBasesBySubjectKey(String[] values) {
		return getBasesByBaseKey(ExBase.KEY_EXT_SUBJECT, makeBaseKeyTargetSet(values));
	}

	/**
	 * この交換代数元に含まれる基底のうち、ハットなし基底のみを取り出す。
	 * 
	 * @return	この交換代数元に含まれるハットなし基底の集合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExBaseSet getNoHatBases() {
		MongoExBaseSet retBases = new MongoExBaseSet(_mongo_session);
		if (isEmpty())
			return retBases;
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = findAllDocuments(true, true).iterator();
			while (cursor.hasNext()) {
				ExBase base = MongoDelegateDocExalgeElem.toExBaseFromDocument(cursor.next());
				if (base.isNoHat()) {
					retBases.add(base);
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get all NO_HAT ExBases into new Collection from MongoExalge[" + _mongo_col.getNamespace().toString() + "] :", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		return retBases;
	}

	/**
	 * この交換代数元に含まれる基底のうち、ハット基底のみを取り出す。
	 * 
	 * @return	この交換代数元に含まれるハット基底の集合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExBaseSet getHatBases() {
		MongoExBaseSet retBases = new MongoExBaseSet(_mongo_session);
		if (isEmpty())
			return retBases;
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = findAllDocuments(true, true).iterator();
			while (cursor.hasNext()) {
				ExBase base = MongoDelegateDocExalgeElem.toExBaseFromDocument(cursor.next());
				if (base.isHat()) {
					retBases.add(base);
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get all HAT ExBases into new Collection from MongoExalge[" + _mongo_col.getNamespace().toString() + "] :", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		return retBases;
	}

	/**
	 * この交換代数元から、全ての基底についてハットを除去した
	 * 基底集合を取得する。
	 * 
	 * @return	この交換代数元に含まれる全基底のハット除去後の基底集合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExBaseSet getBasesWithRemoveHat() {
		MongoExBaseSet retBases = new MongoExBaseSet(_mongo_session);
		if (isEmpty())
			return retBases;
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = findAllDocuments(true, true).iterator();
			while (cursor.hasNext()) {
				ExBase base = MongoDelegateDocExalgeElem.toExBaseFromDocument(cursor.next());
				retBases.add(base.removeHat());
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get all ExBases with changing to NO_HAT into new Collection from MongoExalge[" + _mongo_col.getNamespace().toString() + "] :", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		return retBases;
	}

	/**
	 * この交換代数元から、全ての基底についてハットを付加した
	 * 基底集合を取得する。
	 * 
	 * @return	この交換代数元に含まれる全基底のハット付加後の基底集合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExBaseSet getBasesWithSetHat() {
		MongoExBaseSet retBases = new MongoExBaseSet(_mongo_session);
		if (isEmpty())
			return retBases;
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = findAllDocuments(true, true).iterator();
			while (cursor.hasNext()) {
				ExBase base = MongoDelegateDocExalgeElem.toExBaseFromDocument(cursor.next());
				retBases.add(base.setHat());
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get all ExBases with changing to HAT into new Collection from MongoExalge[" + _mongo_col.getNamespace().toString() + "] :", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		return retBases;
	}

	/**
	 * 指定された基底と対応する値を取り出す。
	 * <br>
	 * 要素に含まれていない基底を指定した場合は、0 を返す。
	 * <p>
	 * <b>(注)</b> 0 が返された場合でも、指定した基底が存在しないとは限らない。
	 * 実数値 0 の基底が要素に含まれている場合もある。
	 * 
	 * @param exbase 指定された基底
	 * @return 指定された基底と対応する値
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public BigDecimal get(ExBase exbase) {
		// Check
		if (exbase == null) {
			throw new NullPointerException();
		}
		
		Document doc = findByExBase(exbase, true, true).first();
		return (doc==null ? BigDecimal.ZERO : MongoDelegateDocExalgeElem.getBigDecimalValueFromDocument(doc));
	}

	/**
	 * 指定された基底と対応する値を取り出す。
	 * <br>
	 * 要素に含まれていない基底を指定した場合は、<tt>null</tt> を返す。
	 * <p>
	 * <b>(注)</b> <tt>null</tt> が返された場合でも、指定した基底が存在しないとは限らない。
	 * <tt>null</tt> 値の基底が要素に含まれている場合もある。
	 * @param exbase	指定された基底
	 * @return	指定された基底と対応する値、もしくは <tt>null</tt>
	 */
	public BigDecimal getRealValue(ExBase exbase) {
		if (exbase == null)
			return null;
		Document doc = findByExBase(exbase, true, true).first();
		return MongoDelegateDocExalgeElem.getBigDecimalValueFromDocument(doc);
	}

	/**
	 * 指定された基底と値が代入された、Exalgeの新しいインスタンスを返す。
	 * <p>
	 * 指定した基底が存在していない場合、交換代数の値と基底のマップの終端に
	 * 追加される。
	 * 
	 * <p>すでに同一基底が存在する場合、指定された値で上書きされる。この場合、
	 * 基底の順序は影響を受けない。
	 * <br>
	 * 値がマイナスの時、自動的に＾計算を行って値をプラスにするように設計されている。
	 * 
	 * @param exbase 交換代数の基底
	 * @param value  値(<tt>null</tt> の場合は、そのまま)
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合、
	 * 								もしくは、値が <tt>null</tt> であり、
	 * 								同一基底の値との加算演算が行えない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 * 
	 * @see exalge2.ExBase
	 * @see exalge2.ExBase#hat()
	 */
	@Override
	public MongoExalge put(ExBase exbase, BigDecimal value) {
		// Check
		if (exbase == null) {
			throw new NullPointerException("exbase is null");
		}
		
		MongoExalge newAlge = copy();
		newAlge.putValue(exbase, value);
		return newAlge;
	}

	/**
	 * 交換代数の要素の値が 0 のものを要素から削除した、Exalgeの新しいインスタンスを返す。
	 * 
	 * @return 要素の値が 0 のものを除いた Exalge インスタンス
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge normalization() {
		if (isEmpty())
			return new MongoExalge(_mongo_session);
		
		MongoExalge retAlge = new MongoExalge(_mongo_session);
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = _mongo_col.find(makeQueryWithoutValue(BigDecimal.ZERO)).iterator();
			while (cursor.hasNext()) {
				Document data = cursor.next();
				ExBase srcBase = MongoDelegateDocExalgeElem.toExBaseFromDocument(data);
				BigDecimal srcVal = MongoDelegateDocExalgeElem.getBigDecimalValueFromDocument(data);
				retAlge.plusValue(srcBase, srcVal);
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to normalize MongoEalge[" + _mongo_col.getNamespace().toString() + "] :", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		return retAlge;
	}

	/**
	 * Exalgeの複製を生成する。
	 * 
	 * @return 複製されたExalge
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge copy() {
		MongoExalge newAlge = new MongoExalge(_mongo_session);
		if (_elem_id != null) {
			// 交換代数集合の要素
			MongoCursor<? extends Document> cursor = null;
			try {
				cursor = findAllDocuments(true, true).iterator();
				while (cursor.hasNext()) {
					newAlge._mongo_col.insertOne(cursor.next());
				}
			}
			catch (MongoException ex) {
				throw new MongoAlgeError("Failed to copy MongoExalge[" + _mongo_col.getNamespace().toString() + "] :", ex);
			}
			finally {
				MongoUtil.closeCursorSilent(cursor);
			}
		}
		else {
			// 単独の交換代数元
			MongoUtil.duplicateCollection(newAlge._mongo_col, _mongo_col);
		}
		return newAlge;
	}
	
	/**
	 * 交換代数の全基底について、拡張基底キーの単位キーを置換する。
	 * <p>
	 * 変換の結果、同一のキーが複数存在する場合、全ての同一キーの値は加算される。
	 * このとき、既存の基底順序の最初に出現した基底の位置に集約される。
	 * 
	 * @param newKey 新しいキー。<tt>null</tt> もしくは、長さ 0 の文字列の場合、省略記号が代入される。
	 * 
	 * @return 単位キーが置き換えられたExalgeを返す
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge replaceUnitKey(String newKey) {
		return replaceExtendedKey(ExtendedKeyID.UNIT, newKey);
	}
	
	/**
	 * 交換代数の全基底について、拡張基底キーの時間キーを置換する。
	 * <p>
	 * 変換の結果、同一のキーが複数存在する場合、全ての同一キーの値は加算される。
	 * このとき、既存の基底順序の最初に出現した基底の位置に集約される。
	 * 
	 * @param newKey 新しいキー。<tt>null</tt> もしくは、長さ 0 の文字列の場合、省略記号が代入される。
	 * 
	 * @return 時間キーが置き換えられたExalgeを返す
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge replaceTimeKey(String newKey) {
		return replaceExtendedKey(ExtendedKeyID.TIME, newKey);
	}
	
	/**
	 * 交換代数の全基底について、指定された拡張基底キーを置換する。
	 * <p>
	 * 変換の結果、同一のキーが複数存在する場合、全ての同一キーの値は加算される。
	 * このとき、既存の基底順序の最初に出現した基底の位置に集約される。
	 * 
	 * @param keyid 置き換え対象の拡張基底キーのキーID。指定可能な値は、次のいずれかとなる。
	 * <ul>
	 * <li>{@link ExtendedKeyID#UNIT} - 単位キー
	 * <li>{@link ExtendedKeyID#TIME} - 時間キー
	 * <li>{@link ExtendedKeyID#SUBJECT} - サブジェクトキー
	 * </ul>
	 * @param newKey 新しいキー。<tt>null</tt> もしくは、長さ 0 の文字列の場合、省略記号が代入される。
	 * 
	 * @return 拡張基底キーが置き換えられたExalgeを返す
	 *
	 * @throws NullPointerException <tt>keyid</tt> が <tt>null</tt> の場合にスローされる
	 * 
	 * @see ExBase
	 * @see ExtendedKeyID
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge replaceExtendedKey(ExtendedKeyID keyid, String newKey) {
		// Check
		if (keyid == null)
			throw new NullPointerException("No keyid");

		// replace
		MongoExalge retAlge = new MongoExalge(_mongo_session);
		BigIterator<BigExalgeElement> it = elementIterator();
		try {
			while (it.hasNext()) {
				BigExalgeElement elem = it.next();
				ExBase dstBase = elem.getBase().replaceExtendedKey(keyid, newKey);
				retAlge.plusValue(dstBase, elem.getValue());
			}
		}
		finally {
			it.closeCursor();
		}
		return retAlge;
	}

	/**
	 * 2つの交換代数が同値であるかを検証する。
	 * <br>
	 * このメソッドは{@link #isSameValues(Exalge)}と同様に、
	 * インスタンスの同一性ではなく、同値性を評価する。
	 * <br>{@link #isEqualValues(Exalge)} とは異なり、実数値 0 の基底も
	 * 評価対象となる。
	 * <br>
	 * 2つの交換代数が同値であるのは、次の場合となる。
	 * <ul>
	 * <li>2つの交換代数において、同一の基底が存在する。
	 * <li>2つの交換代数において、同一(等しい)基底に対する値が全て同値である。
	 * </ul>
	 * <p>
	 * 
	 * @param obj 同値かを判定するオブジェクトの一方
	 * 
	 * @return 2つの交換代数が同値である場合は <tt>true</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			// same instance
			return true;
		}
		
		if (obj instanceof Exalge) {
			return isSameValues((Exalge)obj);
		}
		else if (obj instanceof BigExalge) {
			return isSameValues((BigExalge)obj);
		}
		
		return false;
	}

	/**
	 * このインスタンスのハッシュ値を返す。
	 * 
	 * @return ハッシュ値
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public int hashCode() {
		int h = 0;
		BigIterator<BigExalgeElement> it = elementIterator();
		try {
			while (it.hasNext()) {
				BigExalgeElement elem = it.next();
				//--- キー(ExBase)のハッシュ値は、ExBase#hashCode() の値をそのまま利用する。
				int hk = (elem.getBase() == null ? 0 : elem.getBase().hashCode());
				//--- 値(BigDecimal)のハッシュ値は、BigDecimal#stripTrailingZeros() によって下位の桁の
				//--- 0(余分な0)を消去した後の BigDecimal#hashCode() の値とする。
				//--- これは、通常BigDecimalでは、値[1] と 値[1.00]とでことなるハッシュ値となるため。
				//--- 但し、値が "0"、".0"、"0.0"、"0.00" の場合、BigDecimal#stripTrailingZeros() の
				//--- 結果は変わらず、値 0 ではあるが全て異なるハッシュ値を返すため、値 0 の場合は
				//--- 強制的に 0 とする。
				int hv = ((elem.getValue() == null || BigDecimal.ZERO.compareTo(elem.getValue())==0)
						? 0
						: elem.getValue().stripTrailingZeros().hashCode());
				h += (hk ^ hv);
			}
		}
		finally {
			it.closeCursor();
		}
		return h;
	}
	
	/**
	 * このインスタンスの全要素を文字列として、10,000 要素まで出力する。
	 * <br>
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
		StringBuffer sb = new StringBuffer();
		if (isEmpty()) {
			// 要素なし
			sb.append("()");
		}
		else {
			long num = getNumElements();
			BigIterator<BigExalgeElement> it = elementIterator();
			try {
				long cnt = 0L;
				if (it.hasNext()) {
					BigExalgeElement elem = it.next();
					if (elem.getValue()==null)
						sb.append("null");
					else
						sb.append(elem.getValue().stripTrailingZeros().toPlainString());
					sb.append(elem.getBase().toString());
					++cnt;
				}
				for (; it.hasNext() && cnt < 10000; cnt++) {
					BigExalgeElement elem = it.next();
					sb.append("+");
					if (elem.getValue()==null)
						sb.append("null");
					else
						sb.append(elem.getValue().stripTrailingZeros().toPlainString());
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
	 * 要素を持たない <code>Exalge</code> の新しいインスタンスを返す。
	 * @param value	取り出す要素の値
	 * @return	指定された値と等しい要素のみを含む <code>Exalge</code> の新しいインスタンス
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge oneValueProjection(BigDecimal value) {
		MongoExalge newAlge = new MongoExalge(_mongo_session);
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = findByValue(value, true, true).iterator();
			while (cursor.hasNext()) {
				newAlge._mongo_col.insertOne(cursor.next());
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to project by one value from MongoExalge[" + _mongo_col.getNamespace().toString() + "] :", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		return newAlge;
	}

	/**
	 * 指定されたコレクションに含まれる値と等しい要素のみを取り出す。<br>
	 * 指定の値と等しい要素が存在しない場合は、
	 * 要素を持たない <code>Exalge</code> の新しいインスタンスを返す。
	 * @param values	取り出す要素の値のコレクション
	 * @return	指定されたコレクションに含まれる値と等しい要素のみを含む
	 * 			<code>Exalge</code> の新しいインスタンス
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge valuesProjection(Collection<? extends BigDecimal> values) {
		MongoExalge newAlge = new MongoExalge(_mongo_session);
		if (!values.isEmpty() && !this.isEmpty()) {
			MongoCursor<? extends Document> cursor = null;
			try {
				cursor = queryDocuments(makeQueryByMultiValues(values, false), true, true).iterator();
				while (cursor.hasNext()) {
					newAlge._mongo_col.insertOne(cursor.next());
				}
			}
			catch (MongoException ex) {
				throw new MongoAlgeError("Failed to query by values from MongoExalge[" + _mongo_col.getNamespace().toString() + "] :", ex);
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
	 * 要素を持たない <code>Exalge</code> の新しいインスタンスを返す。
	 * @return	値が <tt>null</tt> の要素のみを含む <code>Exalge</code> の新しいインスタンス
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge nullProjection() {
		return oneValueProjection(null);
	}

	/**
	 * 値が <tt>null</tt> ではない要素のみを取り出す。<br>
	 * 値が <tt>null</tt> ではない要素が存在しない場合は、
	 * 要素を持たない <code>Exalge</code> の新しいインスタンスを返す。
	 * @return	値が <tt>null</tt> ではない要素のみを含む <code>Exalge</code> の新しいインスタンス
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge nonullProjection() {
		MongoExalge newAlge = new MongoExalge(_mongo_session);
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = queryDocuments(makeQueryWithoutValue(null), true, true).iterator();
			while (cursor.hasNext()) {
				newAlge._mongo_col.insertOne(cursor.next());
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to project by non null value from MongoExalge[" + _mongo_col.getNamespace().toString() + "] :", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		return newAlge;
	}

	/**
	 * 値が 0 の要素のみを取り出す。<br>
	 * 値が 0 の要素が存在しない場合は、要素を持たない <code>Exalge</code> の新しいインスタンスを返す。
	 * @return	値が 0 の要素のみを含む <code>Exalge</code> の新しいインスタンス
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge zeroProjection() {
		return oneValueProjection(BigDecimal.ZERO);
	}
	
	/**
	 * 値が 0 ではない要素のみを取り出す。<br>
	 * 値が 0 ではない要素が存在しない場合は、
	 * 要素を持たない <code>Exalge</code> の新しいインスタンスを返す。
	 * @return	値が 0 ではない要素のみを含む <code>Exalge</code> の新しいインスタンス
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge notzeroProjection() {
		MongoExalge newAlge = new MongoExalge(_mongo_session);
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = queryDocuments(makeQueryWithoutValue(BigDecimal.ZERO), true, true).iterator();
			while (cursor.hasNext()) {
				newAlge._mongo_col.insertOne(cursor.next());
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to project by not zero value from MongoExalge[" + _mongo_col.getNamespace().toString() + "] :", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		return newAlge;
	}
	
	/**
	 * 指定の基底のみを取り出す。
	 * <br>
	 * 指定の基底が存在しない場合、要素を持たない <code>Exalge</code> の新しいインスタンスを返す。
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = Proj[base](this)
	 * </blockquote>
	 * 
	 * @param base 基底
	 * 
	 * @return 取り出した基底の値のみを含む <code>Exalge</code>の新しいインスタンス
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge projection(ExBase base) {
		MongoExalge newAlge = new MongoExalge(_mongo_session);
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = queryDocuments(makeQueryByExBase(base), true, true).iterator();
			while (cursor.hasNext()) {
				newAlge._mongo_col.insertOne(cursor.next());
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to project by one ExBase from MongoExalge[" + _mongo_col.getNamespace().toString() + "] :", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		return newAlge;
	}
	
	/**
	 * 指定の基底集合に含まれる基底のみを取り出す。
	 * <br>
	 * 指定の基底が存在しない場合、要素を持たない <code>Exalge</code> の新しいインスタンスを返す。
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = Proj[bases](this)
	 * </blockquote>
	 * このメソッドが返すインスタンスでは、元(this)のインスタンスに格納されている基底の順序が
	 * 維持される。
	 * 
	 * @param bases 基底集合
	 * 
	 * @return 取り出した基底の値のみを含む <code>Exalge</code>の新しいインスタンス
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge projection(ExBaseSet bases) {
		MongoExalge newAlge = new MongoExalge(_mongo_session);
		BigIterator<BigExalgeElement> it = elementIterator();
		try {
			while (it.hasNext()) {
				BigExalgeElement elem = it.next();
				if (bases.contains(elem.getBase())) {
					newAlge.plusValue(elem.getBase(), elem.getValue());
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to project by multple ExBase from MongoExalge[" + _mongo_col.getNamespace().toString() + "] :", ex);
		}
		finally {
			it.closeCursor();
		}
		return newAlge;
	}
	
	/**
	 * 指定の基底集合に含まれる基底のみを取り出す。
	 * <br>
	 * 指定の基底が存在しない場合、要素を持たない <code>Exalge</code> の新しいインスタンスを返す。
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = Proj[bases](this)
	 * </blockquote>
	 * このメソッドが返すインスタンスでは、元(this)のインスタンスに格納されている基底の順序が
	 * 維持される。
	 * 
	 * @param bases 基底集合
	 * 
	 * @return 取り出した基底の値のみを含む <code>Exalge</code>の新しいインスタンス
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge projection(BigExBaseSet bases) {
		MongoExalge newAlge = new MongoExalge(_mongo_session);
		BigIterator<BigExalgeElement> it = elementIterator();
		try {
			while (it.hasNext()) {
				BigExalgeElement elem = it.next();
				if (bases.contains(elem.getBase())) {
					newAlge.plusValue(elem.getBase(), elem.getValue());
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to project by multple ExBase from MongoExalge[" + _mongo_col.getNamespace().toString() + "] :", ex);
		}
		finally {
			it.closeCursor();
		}
		return newAlge;
	}

	/**
	 * 指定された基底とハット基底キー以外が一致する要素のみを取り出す。
	 * <br>
	 * 指定の基底が存在しない場合、要素を持たない <code>Exalge</code> の新しいインスタンスを返す。
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = Proj[removeHat(base) ∪ setHat(base)](this)
	 * </blockquote>
	 * 
	 * @param base	基底(ハット基底キーは無視される)
	 * @return	取り出した基底の値のみを含む <code>Exalge</code> の新しいインスタンス
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge generalProjection(ExBase base) {
		MongoExalge newAlge = new MongoExalge(_mongo_session);
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = queryDocuments(makeQueryByMultiExBases(Arrays.asList(base.removeHat(), base.setHat()), false), true, true).iterator();
			while (cursor.hasNext()) {
				newAlge._mongo_col.insertOne(cursor.next());
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to general project by one ExBase from MongoExalge[" + _mongo_col.getNamespace().toString() + "] :", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		return newAlge;
	}
	
	/**
	 * 指定された基底集合に含まれる基底の、ハット基底キー以外が一致する要素のみを取り出す。
	 * <br>
	 * 指定の基底が存在しない場合、要素を持たない <code>Exalge</code> の新しいインスタンスを返す。
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = Proj[removeHat(bases) ∪ setHat(bases)](this)
	 * </blockquote>
	 * 
	 * @param bases	基底集合(ハット基底キーは無視される)
	 * @return	取り出した基底の値のみを含む <code>Exalge</code> の新しいインスタンス
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge generalProjection(ExBaseSet bases) {
		MongoExalge newAlge = new MongoExalge(_mongo_session);
		BigIterator<BigExalgeElement> it = elementIterator();
		try {
			while (it.hasNext()) {
				BigExalgeElement elem = it.next();
				if (bases.contains(elem.getBase().removeHat()) || bases.contains(elem.getBase().setHat())) {
					newAlge.plusValue(elem.getBase(), elem.getValue());
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to general project by multple ExBase from MongoExalge[" + _mongo_col.getNamespace().toString() + "] :", ex);
		}
		finally {
			it.closeCursor();
		}
		return newAlge;
	}

	/**
	 * 指定された基底集合に含まれる基底の、ハット基底キー以外が一致する要素のみを取り出す。
	 * <br>
	 * 指定の基底が存在しない場合、要素を持たない <code>Exalge</code> の新しいインスタンスを返す。
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = Proj[removeHat(bases) ∪ setHat(bases)](this)
	 * </blockquote>
	 * 
	 * @param bases	基底集合(ハット基底キーは無視される)
	 * @return	取り出した基底の値のみを含む <code>Exalge</code> の新しいインスタンス
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge generalProjection(BigExBaseSet bases) {
		MongoExalge newAlge = new MongoExalge(_mongo_session);
		BigIterator<BigExalgeElement> it = elementIterator();
		try {
			while (it.hasNext()) {
				BigExalgeElement elem = it.next();
				if (bases.contains(elem.getBase().removeHat()) || bases.contains(elem.getBase().setHat())) {
					newAlge.plusValue(elem.getBase(), elem.getValue());
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to general project by multple ExBase from MongoExalge[" + _mongo_col.getNamespace().toString() + "] :", ex);
		}
		finally {
			it.closeCursor();
		}
		return newAlge;
	}

	/**
	 * 指定の基底パターンに一致する基底を持つ交換代数のみを取り出す。
	 * <br>
	 * 指定された基底のハットキー以外の基底キーに一致する基底のみを
	 * 取得し、その集合を返す。ハットキー以外の基底キーにアスタリスク
	 * 文字<code>('*')</code>が含まれている場合、0文字以上の任意の文字に
	 * マッチするワイルドカードとみなす。
	 * <br>
	 * 基底パターンに一致する基底が存在しない場合、このメソッドは
	 * 空の交換代数元を返す。
	 * <br>
	 * このメソッドが返すインスタンスでは、元(this)のインスタンスに格納されている基底の順序が
	 * 極力維持される。
	 * 
	 * @param base	基底パターンとみなす交換代数基底
	 * @return		パターンに一致した基底のみを含む <code>Exalge</code> の新しいインスタンス
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge patternProjection(ExBase base) {
		ExBasePattern pattern = new ExBasePattern(base, true);
		return patternProjection(pattern);
	}

	/**
	 * 指定の基底パターンに一致する基底を持つ交換代数のみを取り出す。
	 * <br>
	 * 指定された基底パターンに一致する基底のみを取得し、その基底のみを
	 * 持つ交換代数元を返す。
	 * <br>
	 * 基底パターンに一致する基底が存在しない場合、このメソッドは
	 * 空の交換代数元を返す。
	 * <br>
	 * このメソッドが返すインスタンスでは、元(this)のインスタンスに格納されている基底の順序が
	 * 極力維持される。
	 * 
	 * @param pattern	基底パターン
	 * @return			パターンに一致した基底のみを含む <code>Exalge</code> の新しいインスタンス
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge patternProjection(ExBasePattern pattern) {
		MongoExalge newAlge = new MongoExalge(_mongo_session);
		BigIterator<BigExalgeElement> it = elementIterator();
		try {
			while (it.hasNext()) {
				BigExalgeElement elem = it.next();
				if (pattern.matches(elem.getBase())) {
					newAlge.plusValue(elem.getBase(), elem.getValue());
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to project by ExBasePattern from MongoExalge[" + _mongo_col.getNamespace().toString() + "] :", ex);
		}
		finally {
			it.closeCursor();
		}
		return newAlge;
	}

	/**
	 * 指定の集合の基底パターンに一致する基底を持つ交換代数のみを取り出す。
	 * <br>
	 * 指定された集合に含まれる基底の、ハットキー以外の基底キーに
	 * 一致する基底のみを取得し、その集合を返す。ハットキー以外の
	 * 基底キーにアスタリスク文字<code>('*')</code>が含まれている場合、
	 * 0文字以上の任意の文字にマッチするワイルドカードとみなす。
	 * <br>
	 * 返される交換代数元に含まれる基底は、指定の集合に含まれる基底パターンの
	 * どれかに一致した基底となる。基底パターンに一致する基底が存在しない場合、
	 * このメソッドは空の交換代数元を返す。
	 * <br>
	 * このメソッドが返すインスタンスでは、元(this)のインスタンスに格納されている基底の順序が
	 * 極力維持される。
	 * 
	 * @param bases	基底パターンとみなす基底の集合
	 * @return		パターンに一致した基底のみを含む <code>Exalge</code> の新しいインスタンス
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge patternProjection(ExBaseSet bases) {
		ExBasePatternSet patterns = new ExBasePatternSet(bases, true);
		return patternProjection(patterns);
	}

	/**
	 * 指定の集合の基底パターンに一致する基底を持つ交換代数のみを取り出す。
	 * <br>
	 * 指定された集合に含まれる基底の、ハットキー以外の基底キーに
	 * 一致する基底のみを取得し、その集合を返す。ハットキー以外の
	 * 基底キーにアスタリスク文字<code>('*')</code>が含まれている場合、
	 * 0文字以上の任意の文字にマッチするワイルドカードとみなす。
	 * <br>
	 * 返される交換代数元に含まれる基底は、指定の集合に含まれる基底パターンの
	 * どれかに一致した基底となる。基底パターンに一致する基底が存在しない場合、
	 * このメソッドは空の交換代数元を返す。
	 * <br>
	 * このメソッドが返すインスタンスでは、元(this)のインスタンスに格納されている基底の順序が
	 * 極力維持される。
	 * <p><b>注意</b>
	 * <blockquote>
	 * このメソッドは、指定された交換代数基底集合から、交換代数基底パターン集合を「メモリ」上に生成する。
	 * そのため、基底集合の量によってはメモリ不足が発生する恐れがあるので、注意すること。
	 * </blockquote>
	 * 
	 * @param bases	基底パターンとみなす基底の集合
	 * @return		パターンに一致した基底のみを含む <code>Exalge</code> の新しいインスタンス
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge patternProjection(BigExBaseSet bases) {
		final ExBasePatternSet patterns = new ExBasePatternSet();
		for (ExBase base : bases) {
			patterns.add(new ExBasePattern(base, true));
		}
		return patternProjection(patterns);
	}

	/**
	 * 指定の集合の基底パターンに一致する基底を持つ交換代数のみを取り出す。
	 * <br>
	 * 指定された基底パターンに一致する基底のみを取得し、その基底のみを
	 * 持つ交換代数元を返す。
	 * <br>
	 * 返される交換代数元に含まれる基底は、指定の集合に含まれる基底パターンの
	 * どれかに一致した基底となる。基底パターンに一致する基底が存在しない場合、
	 * このメソッドは空の交換代数元を返す。
	 * <br>
	 * このメソッドが返すインスタンスでは、元(this)のインスタンスに格納されている基底の順序が
	 * 極力維持される。
	 * 
	 * @param patterns	基底パターンの集合
	 * @return		パターンに一致した基底のみを含む <code>Exalge</code> の新しいインスタンス
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge patternProjection(ExBasePatternSet patterns) {
		MongoExalge newAlge = new MongoExalge(_mongo_session);
		BigIterator<BigExalgeElement> it = elementIterator();
		try {
			while (it.hasNext()) {
				BigExalgeElement elem = it.next();
				if (patterns.matches(elem.getBase())) {
					newAlge.plusValue(elem.getBase(), elem.getValue());
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to project by multiple ExBasePattern from MongoExalge[" + _mongo_col.getNamespace().toString() + "] :", ex);
		}
		finally {
			it.closeCursor();
		}
		return newAlge;
	}
	
	/**
	 * 指定された文字列配列のどれか一つに一致する名前キーを持つ要素のみを取り出す。
	 * 
	 * @param values	検索キーとなる文字列の配列
	 * @return	指定の文字列と一致する名前キーを持つ要素のみとなる交換代数元を返す。
	 * 			一致する基底が見つからなかった場合は、要素が空の交換代数元を返す。
	 * 			<code>values</code> の要素が空の場合も、要素が空の交換代数元を返す。
	 * 
	 * @throws NullPointerException	<code>values</code> が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge projectionByNameKey(String[] values) {
		return projectionByBaseKey(ExBase.KEY_NAME, makeBaseKeyTargetSet(values));
	}
	
	/**
	 * 指定された文字列配列のどれか一つに一致する単位キーを持つ要素のみを取り出す。
	 * 
	 * @param values	検索キーとなる文字列の配列
	 * @return	指定の文字列と一致する単位キーを持つ要素のみとなる交換代数元を返す。
	 * 			一致する基底が見つからなかった場合は、要素が空の交換代数元を返す。
	 * 			<code>values</code> の要素が空の場合も、要素が空の交換代数元を返す。
	 * 
	 * @throws NullPointerException	<code>values</code> が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge projectionByUnitKey(String[] values) {
		return projectionByBaseKey(ExBase.KEY_EXT_UNIT, makeBaseKeyTargetSet(values));
	}
	
	/**
	 * 指定された文字列配列のどれか一つに一致する時間キーを持つ要素のみを取り出す。
	 * 
	 * @param values	検索キーとなる文字列の配列
	 * @return	指定の文字列と一致する時間キーを持つ要素のみとなる交換代数元を返す。
	 * 			一致する基底が見つからなかった場合は、要素が空の交換代数元を返す。
	 * 			<code>values</code> の要素が空の場合も、要素が空の交換代数元を返す。
	 * 
	 * @throws NullPointerException	<code>values</code> が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge projectionByTimeKey(String[] values) {
		return projectionByBaseKey(ExBase.KEY_EXT_TIME, makeBaseKeyTargetSet(values));
	}
	
	/**
	 * 指定された文字列配列のどれか一つに一致する主体キーを持つ要素のみを取り出す。
	 * 
	 * @param values	検索キーとなる文字列の配列
	 * @return	指定の文字列と一致する主体キーを持つ要素のみとなる交換代数元を返す。
	 * 			一致する基底が見つからなかった場合は、要素が空の交換代数元を返す。
	 * 			<code>values</code> の要素が空の場合も、要素が空の交換代数元を返す。
	 * 
	 * @throws NullPointerException	<code>values</code> が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge projectionBySubjectKey(String[] values) {
		return projectionByBaseKey(ExBase.KEY_EXT_SUBJECT, makeBaseKeyTargetSet(values));
	}

	/**
	 * 指定の基底と値を加算した結果を持つ、<code>MongoExalge</code> の新しいインスタンスを返す。
	 * <p>
	 * 値がマイナスの時、自動的に＾計算を行って値をプラスにするように設計されている。
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = (this) + value&lt;exbase&gt;
	 * </blockquote>
	 * このメソッドでは、新しいインスタンスの基底の順序は、元(this)の基底の順序を維持する。
	 * 加算する値の基底が存在しない場合、交換代数の値と基底のマップの終端に
	 * 追加される。
	 * 
	 * @param exbase 指定された基底
	 * @param value 値
	 * @return 加算の結果
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合、
	 * 								もしくは、値が <tt>null</tt> であり、同一基底の
	 * 								値との加算演算が行えない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge plus(ExBase exbase, BigDecimal value) {
		MongoExalge newAlge = copy();
		newAlge.plusValue(exbase, value);
		return newAlge;
	}

	/**
	 * 指定の交換代数を加算した結果を持つ、<code>MongoExalge</code> の新しいインスタンスを返す。
	 * <p>
	 * 値がマイナスの時、自動的に＾計算を行って値をプラスにするように設計されている。
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = (this) + (plusData)
	 * </blockquote>
	 * このメソッドでは、新しいインスタンスの基底の順序は、元(this)の基底の順序を維持する。
	 * 加算する値の基底が存在しない場合、交換代数の値と基底のマップの終端に、
	 * 指定された交換代数(plusData)に格納されている順序で追加される。
	 * 
	 * @param plusData　加算される交換代数
	 * @return　加算された後の交換代数
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合、
	 * 								もしくは、値が <tt>null</tt> であり、同一基底の
	 * 								値との加算演算が行えない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge plus(Exalge plusData) {
		MongoExalge newAlge = copy();
		newAlge.plusValues(plusData);
		return newAlge;
	}

	/**
	 * 指定の交換代数を加算した結果を持つ、<code>Exalge</code> の新しいインスタンスを返す。
	 * <p>
	 * 値がマイナスの時、自動的に＾計算を行って値をプラスにするように設計されている。
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = (this) + (plusData)
	 * </blockquote>
	 * このメソッドでは、新しいインスタンスの基底の順序は、元(this)の基底の順序を維持する。
	 * 加算する値の基底が存在しない場合、交換代数の値と基底のマップの終端に、
	 * 指定された交換代数(plusData)に格納されている順序で追加される。
	 * 
	 * @param plusData　加算される交換代数
	 * @return　加算された後の交換代数
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合、
	 * 								もしくは、値が <tt>null</tt> であり、同一基底の
	 * 								値との加算演算が行えない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge plus(BigExalge plusData) {
		MongoExalge newAlge = copy();
		BigIterator<BigExalgeElement> it = plusData.elementIterator();
		while (it.hasNext()) {
			BigExalgeElement elem = it.next();
			newAlge.plusValue(elem.getBase(), elem.getValue());
		}
		return newAlge;
	}

	/**
	 * 指定された基底と値を、交換代数に加算する。
	 * <p>
	 * (注)このメソッドは、インスタンスの値を書き換える。
	 *<p>
	 * 値がマイナスの時、自動的に ^ 計算を行って値をプラスにするように設計されている。
	 * <p>
	 * (注) このメソッドは<b>破壊的メソッド</b>である。
	 * 
	 * @param base 交換代数の基底
	 * @param value 値
	 * @return このオブジェクト
	 * @throws NullPointerException 引数が <tt>null</tt> の場合、
	 * 								もしくは、値が <tt>null</tt> であり、同一基底の
	 * 								値との加算演算が行えない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge add(ExBase base, BigDecimal value) {
		// Check
		if (base == null)
			throw new NullPointerException("ExBase is null");
		// plus
		plusValue(base, value);
		return this;
	}

	/**
	 * 指定された交換代数の元を、この交換代数に加算する。
	 * <p>
	 * (注) このメソッドは<b>破壊的メソッド</b>である。
	 * 
	 * @param alge 加算する交換代数の元
	 * @return このオブジェクト
	 * @throws NullPointerException 引数が <tt>null</tt> の場合、
	 * 								もしくは、値が <tt>null</tt> であり、同一基底の
	 * 								値との加算演算が行えない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge add(Exalge alge) {
		plusValues(alge);
		return this;
	}

	/**
	 * 指定された交換代数の元を、この交換代数に加算する。
	 * <p>
	 * (注) このメソッドは<b>破壊的メソッド</b>である。
	 * 
	 * @param alge 加算する交換代数の元
	 * @return このオブジェクト
	 * @throws NullPointerException 引数が <tt>null</tt> の場合、
	 * 								もしくは、値が <tt>null</tt> であり、同一基底の
	 * 								値との加算演算が行えない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge add(BigExalge alge) {
		BigIterator<BigExalgeElement> it = alge.elementIterator();
		while (it.hasNext()) {
			BigExalgeElement elem = it.next();
			plusValue(elem.getBase(), elem.getValue());
		}
		return this;
	}

	/**
	 * 交換代数元の逆元を演算し、その結果を格納する <code>Exalge</code> の新しいインスタンスを返す。
	 * <p>
	 * 交換代数の逆元は、次のように定義されている。
	 * <blockquote>
	 * x = a&lt;e&gt; + b^&lt;e&gt;<br>
	 * invElement(x) = C&lt;e&gt; + D^&lt;e&gt;<br>
	 * このとき、C と D は、a と b の関係によって、次のように定義される。<br>
	 * a &gt; b のとき、C=1/(a-b)、D=0<br>
	 * a &lt; b のとき、C=0、D=1/(b-a)<br>
	 * a = b のとき、C=0、D=0
	 * </blockquote>
	 * この逆元は、交換代数元の逆数と次のような関係が成立する。
	 * <blockquote>
	 * invElement(x) = inverse(~x)
	 * </blockquote>
	 * 交換代数元の逆元の演算結果から、0値をもつ要素は除外される。
	 * 
	 * @return この交換代数元の逆元となる、新しい交換代数元を返す。
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge invElement() {
		MongoExalge newAlge = new MongoExalge(_mongo_session);
		if (!isEmpty()) {
			// 基底の収集
			BigExBaseSet nohatBases = getBasesWithRemoveHat();
			
			// 逆元の計算[ai>bi:di=0,ci=1/(ai-bi) ; ai<bi:ci=0,di=1/(bi-ai) ; ai=bi:ci=0,di=0]
			for (ExBase nBase : nohatBases) {
				ExBase hBase = nBase.setHat();
				BigDecimal a = get(nBase);
				BigDecimal b = get(hBase);
				int comp = a.compareTo(b);
				if (comp > 0) {
					//--- a > b : (1/(a-b))<e>
					newAlge.putValue(nBase, BigDecimal.ONE.divide(a.subtract(b), MathContext.DECIMAL128));
				}
				else if (comp < 0) {
					//--- a < b : (1/(b-a))^<e>
					newAlge.putValue(hBase, BigDecimal.ONE.divide(b.subtract(a), MathContext.DECIMAL128));
				}
				// else //--- a == b : 結果が 0 となるので、結果に含めない
			}
		}
		return newAlge;
	}

	/**
	 * 交換代数元の積(要素積)を演算し、その結果を格納する <code>Exalge</code> の新しいインスタンスを返す。
	 * <p>
	 * 交換代数元の積は、次のように定義されている。
	 * <blockquote>
	 * v = a&lt;e&gt; + b^&lt;e&gt;<br>
	 * w = c&lt;e&gt; + d^&lt;e&gt;<br>
	 * v*w = (ac + bd)&lt;e&gt; + (ad + bc)^&lt;e&gt;<br>
	 * </blockquote>
	 * このとき、バー(~)演算は以下の等式が成立する。
	 * <blockquote>
	 * (~v)*(~w) = ~(v*w)
	 * </blockquote>
	 * 従って、交換代数元の積は、次の演算を行うものとなる。
	 * <blockquote>
	 * bases = (this.getBases() ∪ x.getBases()).removeHat()<br>
	 * (return) = (this) * (x)<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;= ∑{ v*w | bs&lt;-bases, v=this.generalProjection(bs), w=x.generalProjection(bs)}
	 * </blockquote>
	 * また、積の定義において対となる基底が存在しない場合は、0値を持つ要素とみなし、
	 * 演算した結果から0値を持つ要素を除外する。例として、次のような元の積を示す。
	 * <blockquote>
	 * (a&lt;e&gt; + b^&lt;e&gt;) * (c&lt;e&gt;) → (a&lt;e&gt; + b^&lt;e&gt;) * (c&lt;e&gt; + 0^&lt;e&gt;) = ac&lt;e&gt; + bc^&lt;e&gt;
	 * <br>
	 * (a&lt;e&gt; + b^&lt;e&gt;) * (d^&lt;e&gt;) → (a&lt;e&gt; + b^&lt;e&gt;) * (0&lt;e&gt; + d^&lt;e&gt;) = bd&lt;e&gt; + ad^&lt;e&gt;
	 * <br>
	 * (a&lt;e&gt;) * (d^&lt;e&gt;) → (a&lt;e&gt; + 0^&lt;e&gt;) * (0&lt;e&gt; + d^&lt;e&gt;) = 0&lt;e&gt; + ad^&lt;e&gt; → ad^&lt;e&gt;
	 * <br>
	 * (b^&lt;e&gt;) * (d^&lt;e&gt;) → (0&lt;e&gt; + b^&lt;e&gt;) * (0&lt;e&gt; + d^&lt;e&gt;) = bd&lt;e&gt; + 0^&lt;e&gt; → bd&lt;e&gt;
	 * <br>
	 * (a&lt;e&gt;) * () → (a&lt;e&gt; + 0^&lt;e&gt;) * (0&lt;e&gt; + 0^&lt;e&gt;) = 0&lt;e&gt; + 0^&lt;e&gt; → ()
	 * </blockquote>
	 * 
	 * @param x	この交換代数元との積をとる、交換代数元の一方
	 * @return	演算後の交換代数
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge multiple(Exalge x) {
		if (x == null) {
			throw new NullPointerException();
		}
		
		if (this.isEmpty() || x.isEmpty()) {
			//--- どちらか一方が存在しない場合は、空の交換代数元を返す
			return new MongoExalge(_mongo_session);
		}
		
		// 積の演算(存在基底のみ)
		//    基本的に、両方に存在する基底のみが対象(一方のみに存在するものは0値要素)となるので、
		//    片側にしか存在しない基底セットは無視できる
		//    → 要素が少ないほうのハットを除去した基底のみで操作すれば良い。
		//--- 計算対象基底の収集
		MongoExalge retAlge;
		MongoExBaseSet nohatBases = new MongoExBaseSet(_mongo_session);
		try {
			long numThis = this.getNumElements();
			long numX    = x.getNumElements();
			if (numThis > numX) {
				for (ExBase base : x.getUnmodifiableExBaseSet()) {
					nohatBases.add(base.removeHat());
				}
			} else {
				BigIterator<ExBase> it = exbaseIterator();
				try {
					while (it.hasNext()) {
						nohatBases.add(it.next().removeHat());
					}
				}
				finally {
					it.closeCursor();
				}
			}
			//--- 結果を格納する元の生成
			retAlge = new MongoExalge(_mongo_session);
			//--- 積の演算[ (a<e> + b^<e>) * (c<e> + d^<e>) = (ac+bd)<e> + (ad+bc)^<e> ]
			for (ExBase nBase : nohatBases) {
				ExBase hBase = nBase.setHat();
				BigDecimal a = get(nBase);
				BigDecimal b = get(hBase);
				BigDecimal c = x.get(nBase);
				BigDecimal d = x.get(hBase);
				BigDecimal nValue = a.multiply(c).add(b.multiply(d));
				BigDecimal hValue = a.multiply(d).add(b.multiply(c));
				if (BigDecimal.ZERO.compareTo(nValue) != 0) {
					retAlge.putValue(nBase, nValue);
				}
				if (BigDecimal.ZERO.compareTo(hValue) != 0) {
					retAlge.putValue(hBase, hValue);
				}
			}
		}
		finally {
			nohatBases.delete();
		}
		
		// 完了
		return retAlge;
	}

	/**
	 * 交換代数元の積(要素積)を演算し、その結果を格納する <code>Exalge</code> の新しいインスタンスを返す。
	 * <p>
	 * 交換代数元の積は、次のように定義されている。
	 * <blockquote>
	 * v = a&lt;e&gt; + b^&lt;e&gt;<br>
	 * w = c&lt;e&gt; + d^&lt;e&gt;<br>
	 * v*w = (ac + bd)&lt;e&gt; + (ad + bc)^&lt;e&gt;<br>
	 * </blockquote>
	 * このとき、バー(~)演算は以下の等式が成立する。
	 * <blockquote>
	 * (~v)*(~w) = ~(v*w)
	 * </blockquote>
	 * 従って、交換代数元の積は、次の演算を行うものとなる。
	 * <blockquote>
	 * bases = (this.getBases() ∪ x.getBases()).removeHat()<br>
	 * (return) = (this) * (x)<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;= ∑{ v*w | bs&lt;-bases, v=this.generalProjection(bs), w=x.generalProjection(bs)}
	 * </blockquote>
	 * また、積の定義において対となる基底が存在しない場合は、0値を持つ要素とみなし、
	 * 演算した結果から0値を持つ要素を除外する。例として、次のような元の積を示す。
	 * <blockquote>
	 * (a&lt;e&gt; + b^&lt;e&gt;) * (c&lt;e&gt;) → (a&lt;e&gt; + b^&lt;e&gt;) * (c&lt;e&gt; + 0^&lt;e&gt;) = ac&lt;e&gt; + bc^&lt;e&gt;
	 * <br>
	 * (a&lt;e&gt; + b^&lt;e&gt;) * (d^&lt;e&gt;) → (a&lt;e&gt; + b^&lt;e&gt;) * (0&lt;e&gt; + d^&lt;e&gt;) = bd&lt;e&gt; + ad^&lt;e&gt;
	 * <br>
	 * (a&lt;e&gt;) * (d^&lt;e&gt;) → (a&lt;e&gt; + 0^&lt;e&gt;) * (0&lt;e&gt; + d^&lt;e&gt;) = 0&lt;e&gt; + ad^&lt;e&gt; → ad^&lt;e&gt;
	 * <br>
	 * (b^&lt;e&gt;) * (d^&lt;e&gt;) → (0&lt;e&gt; + b^&lt;e&gt;) * (0&lt;e&gt; + d^&lt;e&gt;) = bd&lt;e&gt; + 0^&lt;e&gt; → bd&lt;e&gt;
	 * <br>
	 * (a&lt;e&gt;) * () → (a&lt;e&gt; + 0^&lt;e&gt;) * (0&lt;e&gt; + 0^&lt;e&gt;) = 0&lt;e&gt; + 0^&lt;e&gt; → ()
	 * </blockquote>
	 * 
	 * @param x	この交換代数元との積をとる、交換代数元の一方
	 * @return	演算後の交換代数
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge multiple(BigExalge x) {
		if (x == null) {
			throw new NullPointerException();
		}
		
		if (this.isEmpty() || x.isEmpty()) {
			//--- どちらか一方が存在しない場合は、空の交換代数元を返す
			return new MongoExalge(_mongo_session);
		}
		
		// 積の演算(存在基底のみ)
		//    基本的に、両方に存在する基底のみが対象(一方のみに存在するものは0値要素)となるので、
		//    片側にしか存在しない基底セットは無視できる
		//    → 要素が少ないほうのハットを除去した基底のみで操作すれば良い。
		//--- 計算対象基底の収集
		MongoExalge retAlge;
		MongoExBaseSet nohatBases = new MongoExBaseSet(_mongo_session);
		try {
			long numThis = this.getNumElements();
			long numX    = x.getNumElements();
			BigIterator<ExBase> it = null;
			try {
				if (numThis > numX) {
					it = x.exbaseIterator();
				} else {
					it = exbaseIterator();
				}
				while (it.hasNext()) {
					nohatBases.add(it.next().removeHat());
				}
			}
			finally {
				if (it != null) {
					it.closeCursor();
				}
			}
			//--- 結果を格納する元の生成
			retAlge = new MongoExalge(_mongo_session);
			//--- 積の演算[ (a<e> + b^<e>) * (c<e> + d^<e>) = (ac+bd)<e> + (ad+bc)^<e> ]
			for (ExBase nBase : nohatBases) {
				ExBase hBase = nBase.setHat();
				BigDecimal a = get(nBase);
				BigDecimal b = get(hBase);
				BigDecimal c = x.get(nBase);
				BigDecimal d = x.get(hBase);
				BigDecimal nValue = a.multiply(c).add(b.multiply(d));
				BigDecimal hValue = a.multiply(d).add(b.multiply(c));
				if (BigDecimal.ZERO.compareTo(nValue) != 0) {
					retAlge.putValue(nBase, nValue);
				}
				if (BigDecimal.ZERO.compareTo(hValue) != 0) {
					retAlge.putValue(hBase, hValue);
				}
			}
		}
		finally {
			nohatBases.delete();
		}
		
		// 完了
		return retAlge;
	}

	/**
	 * 交換代数の全要素に対して指定の値を乗算した結果を持つ、
	 * <code>Exalge</code> の新しいインスタンスを返す。
	 * <p>
	 * 演算結果が 0 となる場合でも、その要素は保持される。
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = (this) × (x)
	 * </blockquote>
	 * このメソッドでは、新しいインスタンスの基底の順序は、元(this)の基底の順序を維持する。
	 * 
	 * @param x　乗算の倍率(正の値であること)
	 * 
	 * @return　乗算された後の交換代数
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 * @throws ArithmeticException 引数に負の値が指定された場合にスローされる
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge multiple(BigDecimal x) {
		// Check
		if (x.compareTo(BigDecimal.ZERO) < 0) {
			// minus value
			throw new ArithmeticException("Multiply by minus value");
		}
		
		// Multiple
		MongoExalge retAlge = new MongoExalge(_mongo_session);
		BigIterator<BigExalgeElement> it = elementIterator();
		try {
			while (it.hasNext()) {
				BigExalgeElement elem = it.next();
				retAlge.plusValue(elem.getBase(), elem.getValue().multiply(x));
			}
		}
		finally {
			it.closeCursor();
		}
		return retAlge;
	}
	
	/**
	 * 交換代数元の商(要素商)を演算し、その結果を格納する <code>Exalge</code> の新しいインスタンスを返す。
	 * <p>
	 * 交換代数元の商は、交換代数の逆元を用いて次のように定義されている。
	 * <blockquote>
	 * v = a&lt;e&gt; + b^&lt;e&gt;<br>
	 * w = c&lt;e&gt; + d^&lt;e&gt;<br>
	 * v/w = v * invElement(w) = (ac + bd)&lt;e&gt; + (ad + bc)^&lt;e&gt;<br>
	 * (c &gt; d) のとき、v/w = (a&lt;e&gt; + b^&lt;e&gt;) * ((1/(c-d))&lt;e&gt; + 0^&lt;e&gt;) = a(1/(c-d))&lt;e&gt; + b(1/(c-d))^&lt;e&gt;<br>
	 * (c &lt; d) のとき、v/w = (a&lt;e&gt; + b^&lt;e&gt;) * (0&lt;e&gt; + (1/(d-c))^&lt;e&gt;) = b(1/(d-c))&lt;e&gt; + a(1/(d-c))^&lt;e&gt;<br>
	 * (c = d) のとき、v/w = (a&lt;e&gt; + b^&lt;e&gt;) * (0&lt;e&gt; + 0^&lt;e&gt;) = 0&lt;e&gt; + 0^&lt;e&gt;
	 * </blockquote>
	 * 商の定義において対となる基底が存在しない場合は、0値を持つ要素とみなし、
	 * 演算した結果から0値を持つ要素を除外する。
	 * 
	 * @param x	この交換代数元との商をとる、交換代数元の一方
	 * @return	演算後の交換代数
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge divide(Exalge x) {
		if (x == null) {
			throw new NullPointerException();
		}
		
		if (this.isEmpty() || x.isEmpty()) {
			//--- どちらか一方が存在しない場合は、空の交換代数元を返す
			return new MongoExalge(_mongo_session);
		}
		
		// 商の演算(存在基底のみ)
		//    基本的に、両方に存在する基底のみが対象(一方のみに存在するものは0値要素)となるので、
		//    片側にしか存在しない基底セットは無視できる
		//    → 要素が少ないほうのハットを除去した基底のみで操作すれば良い。
		//--- 計算対象基底の収集
		MongoExalge retAlge;
		MongoExBaseSet nohatBases = new MongoExBaseSet(_mongo_session);
		try {
			long numThis = this.getNumElements();
			long numX    = x.getNumElements();
			if (numThis > numX) {
				for (ExBase base : x.getUnmodifiableExBaseSet()) {
					nohatBases.add(base.removeHat());
				}
			} else {
				BigIterator<ExBase> it = exbaseIterator();
				try {
					while (it.hasNext()) {
						nohatBases.add(it.next().removeHat());
					}
				}
				finally {
					it.closeCursor();
				}
			}
			//--- 結果を格納する元の生成
			retAlge = new MongoExalge(_mongo_session);
			//--- 商の演算[ (a<e> + b^<e>) / (c<e> + d^<e>) =
			//              if (c>d) := (a<e> + b^<e>) * (1/(c-d))<e>  = a(1/(c-d))<e> + b(1/(c-d))^<e>
			//              if (c<d) := (a<e> + b^<e>) * (1/(d-c))^<e> = b(1/(d-c))<e> + a(1/(d-c))^<e>
			//              if (c=d) := 0<e> + 0^<e>
			for (ExBase nBase : nohatBases) {
				ExBase hBase = nBase.setHat();
				BigDecimal a = get(nBase);
				BigDecimal b = get(hBase);
				BigDecimal c = x.get(nBase);
				BigDecimal d = x.get(hBase);
				int comp = c.compareTo(d);
				if (comp > 0) {
					//--- c > d : a(1/(c-d))<e> + b(1/(c-d))^<e>
					BigDecimal inv = BigDecimal.ONE.divide(c.subtract(d), MathContext.DECIMAL128);
					BigDecimal nValue = a.multiply(inv);
					BigDecimal hValue = b.multiply(inv);
					if (BigDecimal.ZERO.compareTo(nValue) != 0) {
						retAlge.putValue(nBase, nValue);
					}
					if (BigDecimal.ZERO.compareTo(hValue) != 0) {
						retAlge.putValue(hBase, hValue);
					}
				}
				else if (comp < 0) {
					//--- b(1/(d-c))<e> + a(1/(d-c))^<e>
					BigDecimal inv = BigDecimal.ONE.divide(d.subtract(c), MathContext.DECIMAL128);
					BigDecimal nValue = b.multiply(inv);
					BigDecimal hValue = a.multiply(inv);
					if (BigDecimal.ZERO.compareTo(nValue) != 0) {
						retAlge.putValue(nBase, nValue);
					}
					if (BigDecimal.ZERO.compareTo(hValue) != 0) {
						retAlge.putValue(hBase, hValue);
					}
				}
			}
		}
		finally {
			nohatBases.delete();
		}
		
		// 完了
		return retAlge;
	}
	
	/**
	 * 交換代数元の商(要素商)を演算し、その結果を格納する <code>Exalge</code> の新しいインスタンスを返す。
	 * <p>
	 * 交換代数元の商は、交換代数の逆元を用いて次のように定義されている。
	 * <blockquote>
	 * v = a&lt;e&gt; + b^&lt;e&gt;<br>
	 * w = c&lt;e&gt; + d^&lt;e&gt;<br>
	 * v/w = v * invElement(w) = (ac + bd)&lt;e&gt; + (ad + bc)^&lt;e&gt;<br>
	 * (c &gt; d) のとき、v/w = (a&lt;e&gt; + b^&lt;e&gt;) * ((1/(c-d))&lt;e&gt; + 0^&lt;e&gt;) = a(1/(c-d))&lt;e&gt; + b(1/(c-d))^&lt;e&gt;<br>
	 * (c &lt; d) のとき、v/w = (a&lt;e&gt; + b^&lt;e&gt;) * (0&lt;e&gt; + (1/(d-c))^&lt;e&gt;) = b(1/(d-c))&lt;e&gt; + a(1/(d-c))^&lt;e&gt;<br>
	 * (c = d) のとき、v/w = (a&lt;e&gt; + b^&lt;e&gt;) * (0&lt;e&gt; + 0^&lt;e&gt;) = 0&lt;e&gt; + 0^&lt;e&gt;
	 * </blockquote>
	 * 商の定義において対となる基底が存在しない場合は、0値を持つ要素とみなし、
	 * 演算した結果から0値を持つ要素を除外する。
	 * 
	 * @param x	この交換代数元との商をとる、交換代数元の一方
	 * @return	演算後の交換代数
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge divide(BigExalge x) {
		if (x == null) {
			throw new NullPointerException();
		}
		
		if (this.isEmpty() || x.isEmpty()) {
			//--- どちらか一方が存在しない場合は、空の交換代数元を返す
			return new MongoExalge(_mongo_session);
		}
		
		// 商の演算(存在基底のみ)
		//    基本的に、両方に存在する基底のみが対象(一方のみに存在するものは0値要素)となるので、
		//    片側にしか存在しない基底セットは無視できる
		//    → 要素が少ないほうのハットを除去した基底のみで操作すれば良い。
		//--- 計算対象基底の収集
		MongoExalge retAlge;
		MongoExBaseSet nohatBases = new MongoExBaseSet(_mongo_session);
		try {
			long numThis = this.getNumElements();
			long numX    = x.getNumElements();
			BigIterator<ExBase> it = null;
			try {
				if (numThis > numX) {
					it = x.exbaseIterator();
				} else {
					it = exbaseIterator();
				}
				while (it.hasNext()) {
					nohatBases.add(it.next().removeHat());
				}
			}
			finally {
				if (it != null) {
					it.closeCursor();
				}
			}
			//--- 結果を格納する元の生成
			retAlge = new MongoExalge(_mongo_session);
			//--- 商の演算[ (a<e> + b^<e>) / (c<e> + d^<e>) =
			//              if (c>d) := (a<e> + b^<e>) * (1/(c-d))<e>  = a(1/(c-d))<e> + b(1/(c-d))^<e>
			//              if (c<d) := (a<e> + b^<e>) * (1/(d-c))^<e> = b(1/(d-c))<e> + a(1/(d-c))^<e>
			//              if (c=d) := 0<e> + 0^<e>
			for (ExBase nBase : nohatBases) {
				ExBase hBase = nBase.setHat();
				BigDecimal a = get(nBase);
				BigDecimal b = get(hBase);
				BigDecimal c = x.get(nBase);
				BigDecimal d = x.get(hBase);
				int comp = c.compareTo(d);
				if (comp > 0) {
					//--- c > d : a(1/(c-d))<e> + b(1/(c-d))^<e>
					BigDecimal inv = BigDecimal.ONE.divide(c.subtract(d), MathContext.DECIMAL128);
					BigDecimal nValue = a.multiply(inv);
					BigDecimal hValue = b.multiply(inv);
					if (BigDecimal.ZERO.compareTo(nValue) != 0) {
						retAlge.putValue(nBase, nValue);
					}
					if (BigDecimal.ZERO.compareTo(hValue) != 0) {
						retAlge.putValue(hBase, hValue);
					}
				}
				else if (comp < 0) {
					//--- b(1/(d-c))<e> + a(1/(d-c))^<e>
					BigDecimal inv = BigDecimal.ONE.divide(d.subtract(c), MathContext.DECIMAL128);
					BigDecimal nValue = b.multiply(inv);
					BigDecimal hValue = a.multiply(inv);
					if (BigDecimal.ZERO.compareTo(nValue) != 0) {
						retAlge.putValue(nBase, nValue);
					}
					if (BigDecimal.ZERO.compareTo(hValue) != 0) {
						retAlge.putValue(hBase, hValue);
					}
				}
			}
		}
		finally {
			nohatBases.delete();
		}
		
		// 完了
		return retAlge;
	}

	/**
	 * 交換代数の全要素に対して指定の値で除算した結果を持つ、
	 * <code>Exalge</code> の新しいインスタンスを返す。
	 * <p>
	 * 除算においては、{@link MathContext#DECIMAL128} の精度で結果を保持する。
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = (this) ÷ (x)
	 * </blockquote>
	 * このメソッドでは、新しいインスタンスの基底の順序は、元(this)の基底の順序を維持する。
	 * 
	 * @param x 除算する値(正の値であること)
	 * 
	 * @return 除算された後の交換代数
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 * @throws ArithmeticException 引数の 0 もしくは負の値が指定された場合にスローされる
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge divide(BigDecimal x) {
		// Check
		int cmp = x.compareTo(BigDecimal.ZERO);
		if (cmp == 0) {
			// by Zero
			throw new ArithmeticException("Division by zero");
		} else if (cmp < 0) {
			// minus value
			throw new ArithmeticException("Division by minus value");
		}
		
		// Divide
		MongoExalge retAlge = new MongoExalge(_mongo_session);
		BigIterator<BigExalgeElement> it = elementIterator();
		try {
			while (it.hasNext()) {
				BigExalgeElement elem = it.next();
				retAlge.plusValue(elem.getBase(), elem.getValue().divide(x, MathContext.DECIMAL128));	// double と同程度の精度による桁制限＆丸め
			}
		}
		finally {
			it.closeCursor();
		}
		return retAlge;
	}

	/**
	 * 交換代数のノルム計算、交換代数内の値の総和
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = |(this)|
	 * </blockquote>
	 * 
	 * @return　値の総和
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public BigDecimal norm() {
		BigDecimal norm = BigDecimal.ZERO;
		BigIterator<BigExalgeElement> it = elementIterator();
		try {
			while (it.hasNext()) {
				BigExalgeElement elem = it.next();
				norm = norm.add(elem.getValue());
			}
		}
		finally {
			it.closeCursor();
		}
		return norm;
	}

	/**
	 * 交換代数の＾計算、すべての項目に対して、＾の部分の演算
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = ^ (this)
	 * </blockquote>
	 * このメソッドでは、新しいインスタンスの基底の順序は、元(this)の基底の順序を維持する。
	 * 
	 * @return　＾計算された後の交換代数
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge hat() {
		MongoExalge retAlge = new MongoExalge(_mongo_session);
		BigIterator<BigExalgeElement> it = elementIterator();
		try {
			while (it.hasNext()) {
				BigExalgeElement elem = it.next();
				retAlge.putValue(elem.getBase().hat(), elem.getValue());
			}
		}
		finally {
			it.closeCursor();
		}
		return retAlge;
	}

	/**
	 * 交換代数の整合を行った結果を保持する、新しい <code>Exalge</code> インスタンスを返す。
	 * <p>
	 * 交換代数の整合は、基底キーのハットキー以外が同一のもので、HATとNO_HATの
	 * 値を加算(正確には、NO_HATの値－HATの値)した結果となる。
	 * <br>
	 * 整合後の値が正の値であれば、基底のハットキーはNO_HAT、負の値であればHATと
	 * し、値が 0 になる基底は、要素から削除される。
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = ~ (this)
	 * </blockquote>
	 * このメソッドでは、新しいインスタンスの基底の順序は、元(this)の基底の順序が極力維持されるが、
	 * 元(this)の順序を保障するものではない。
	 * 
	 * @return 演算結果の交換代数
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge bar() {
		MongoExalge retAlge = new MongoExalge(_mongo_session);
		BigIterator<ExBase> it = exbaseIterator();
		try {
			while (it.hasNext()) {
				ExBase exbase = it.next();
				ExBase hatbase = exbase.hat();
				
				// NO_HATの基底を基準とし、対応するHAT基底との演算結果を
				// 新しいインスタンスに代入する。
				// 対応するペアが存在しないものは、そのまま新しいインスタンスに
				// 代入する
				
				if (exbase.isNoHat()) {
					// NO_HAT基底への処理
					//    exbase  -- NO_HAT基底
					//    hatbase -- HAT基底
					BigDecimal value = this.get(exbase);
					if (this.containsBase(hatbase)) {
						//-- 対応ペアあり
						value = value.subtract(this.get(hatbase));
					}
					// 結果の代入(対応ペアなしなら、そのまま代入
					if (value.compareTo(BigDecimal.ZERO) != 0) {
						// 0以外なら代入
						retAlge.putValue(exbase, value);
					}
				}
				else {
					// HAT基底への処理
					//    exbase  -- HAT基底
					//    hatbase -- NO_HAT基底
					if (!this.containsBase(hatbase)) {
						//-- 対応ペアなし
						BigDecimal value = this.get(exbase);
						if (value.compareTo(BigDecimal.ZERO) != 0) {
							// 0以外なら代入
							retAlge.putValue(exbase, value);	// result 書き換え
						}
					}
					// 対応ペアありの場合は、NO_HAT基底検知時に演算を行うため、
					// ここでの処理は不要
				}
			}
		}
		finally {
			it.closeCursor();
		}
		return retAlge;
	}

	/**
	 * 交換代数の整合を行った結果を保持する、新しい <code>Exalge</code> インスタンスを返す。
	 * <p>
	 * 交換代数の整合は、基底キーのハットキー以外が同一のもので、HATとNO_HATの
	 * 値を加算(正確には、NO_HATの値－HATの値)した結果となる。
	 * <br>
	 * 整合後の値が正の値であれば、基底のハットキーはNO_HAT、負の値であればHATと
	 * し、整合の結果の値が 0 になる基底は、要素から削除される。
	 * <p>このバー演算は、交換代数元の通常のバー演算よりも厳密であり、
	 * ハットなし基底とハット基底のペア(ハットキー以外の基底キーが同一のもの)でない場合、
	 * その要素は値に関わらず結果に出力される。
	 * <p>
	 * このメソッドでは、新しいインスタンスの基底の順序は、元(this)の基底の順序が極力維持されるが、
	 * 元(this)の順序を保障するものではない。
	 *
	 * @return 演算結果の交換代数
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge strictBar() {
		MongoExalge retAlge = new MongoExalge(_mongo_session);
		
		BigIterator<BigExalgeElement> it = elementIterator();
		try {
			while (it.hasNext()) {
				BigExalgeElement elem = it.next();
				ExBase exbase = elem.getBase();
				ExBase hatbase = exbase.hat();
				
				// NO_HATの基底を基準とし、対応するHAT基底との演算結果を新しいインスタンスに代入する。
				// 対応するペアが存在しないものは、そのまま新しいインスタンスに代入する
				
				if (!containsBase(hatbase)) {
					// 対応ペアなしなら、値やハットに関係なく、そのまま代入
					retAlge.putValue(exbase, elem.getValue());
				}
				else if (exbase.isNoHat()) {
					// 対応ペアあり、NO_HAT基底
					BigDecimal value = elem.getValue().subtract(getRealValue(hatbase));
					//--- 0 以外なら代入
					if (value.signum() != 0) {
						retAlge.putValue(exbase, value);
					}
				}
				// else : 対応ペアあり、HAT基底 : NO_HAT基底検知時に演算を行うため、ここでの処理は不要
			}
		}
		finally {
			it.closeCursor();
		}
		return retAlge;
	}
	
	/**
	 * 交換代数の整合を行った結果を保持する、新しい <code>Exalge</code> インスタンスを返す。
	 * <p>
	 * 交換代数の整合は、基底キーのハットキー以外が同一のもので、HATとNO_HATの
	 * 値を加算(正確には、NO_HATの値－HATの値)した結果となる。
	 * <br>
	 * 整合後の値が正の値であれば、基底のハットキーはNO_HAT、負の値であればHATとし、整合の結果の値が 0 になる基底は、
	 * <em>leaveAsNoHat</em> が <tt>true</tt> であればハットなし基底、
	 * <em>leaveAsNoHat</em> が <tt>false</tt> であればハット基底として結果に出力される。
	 * <p>このバー演算は、交換代数元の通常のバー演算よりも厳密であり、
	 * ハットなし基底とハット基底のペア(ハットキー以外の基底キーが同一のもの)でない場合、
	 * その要素は値に関わらず結果に出力される。
	 * <p>
	 * このメソッドでは、新しいインスタンスの基底の順序は、元(this)の基底の順序が極力維持されるが、
	 * 元(this)の順序を保障するものではない。
	 *
	 * @param leaveAsNoHat	バー演算の結果、値が 0 となる要素の基底をハットなし基底とする場合は <tt>true</tt>、ハット基底とする場合は <tt>false</tt>
	 * @return 演算結果の交換代数
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge strictBarLeaveZero(boolean leaveAsNoHat) {
		MongoExalge retAlge = new MongoExalge(_mongo_session);
		
		BigIterator<BigExalgeElement> it = elementIterator();
		try {
			while (it.hasNext()) {
				BigExalgeElement elem = it.next();
				ExBase exbase = elem.getBase();
				ExBase hatbase = exbase.hat();
				
				// NO_HATの基底を基準とし、対応するHAT基底との演算結果を新しいインスタンスに代入する。
				// 対応するペアが存在しないものは、そのまま新しいインスタンスに代入する
				
				if (!containsBase(hatbase)) {
					// 対応ペアなしなら、値やハットに関係なく、そのまま代入
					retAlge.putValue(exbase, elem.getValue());
				}
				else if (exbase.isNoHat()) {
					// 対応ペアあり、NO_HAT基底
					BigDecimal value = elem.getValue().subtract(getRealValue(hatbase));
					if (value.signum() != 0) {
						//--- 0 以外なら代入
						retAlge.putValue(exbase, value);
					}
					else {
						//--- 0 なら、
						//--- leaveAsNoHat=true -> NO_HAT基底を残す
						//--- leaveAsNoHat=false -> HAT基底を残す
						if (leaveAsNoHat)
							retAlge.putValue(exbase, BigDecimal.ZERO);
						else
							retAlge.putValue(hatbase, BigDecimal.ZERO);
					}
				}
				// else : 対応ペアあり、HAT基底 : NO_HAT基底検知時に演算を行うため、ここでの処理は不要
			}
		}
		finally {
			it.closeCursor();
		}
		return retAlge;
	}

	/**
	 * 交換代数の逆数を算出する。
	 * <br>
	 * 交換代数の逆数は、全要素の実数値の逆数を持つものを指す。
	 * <p>
	 * (例)<br>
	 * 交換代数 X が<br>
	 * &nbsp;&nbsp;X = 5&lt;e1&gt; + 2&circ;&lt;e2&gt;<br>
	 * のとき、Y = X<sup>-1</sup> (Y = X.inverse()) は、次のようになる。<br>
	 * &nbsp;&nbsp;Y = X<sup>-1</sup> = 0.2&lt;e1&gt; + 0.5&circ;&lt;e2&gt;<br>
	 * <p>
	 * 逆数の演算において、{@link MathContext#DECIMAL128} の精度で結果を保持する。
	 * 実数値によって計算誤差が含まれる場合があるため、逆数の逆数が元の値とは
	 * ならない (つまり、X &ne; (X<sup>-1</sup>)<sup>-1</sup>) ことに注意すること。
	 * <p>
	 * 交換代数の要素が空の場合、結果も要素が空の交換代数となる。
	 * <br>
	 * また、実数値 0 の要素は無視され、演算結果に含まれない。
	 * <p>
	 * このメソッドでは、新しいインスタンスの基底の順序は、元(this)の基底の順序が維持される。
	 * 
	 * @return 逆数演算後の交換代数
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge inverse() {
		MongoExalge newAlge = new MongoExalge(_mongo_session);
		BigIterator<ExBase> it = exbaseIterator();
		try {
			while (it.hasNext()) {
				ExBase exbase = it.next();
				BigDecimal exval = get(exbase);
				// inverse
				if (exval.compareTo(BigDecimal.ZERO) > 0) {
					BigDecimal exinv = BigDecimal.ONE.divide(exval, MathContext.DECIMAL128);	// double と同程度の精度による桁制限＆丸め
					newAlge.putValue(exbase, exinv);
				}
			}
		}
		finally {
			it.closeCursor();
		}
		return newAlge;
	}

	/**
	 * コレクションに含まれる交換代数の元の総和を返す。
	 * <br>
	 * 交換代数の元の総和は、交換代数基底が同一のもの同士の値の加算となる。
	 * <br>
	 * コレクションに含まれる <tt>null</tt> の要素は無視される。
	 * <p>
	 * このメソッドでは、新しいインスタンスの基底の順序は、元(this)の基底の順序を維持する。
	 * 加算する値の基底が存在しない場合、交換代数の値と基底のマップの終端に、
	 * 指定された交換代数コレクション(c)に格納されている順序で追加される。
	 * 
	 * @param session	総和の結果を格納するデータベースセッション
	 * @param c 総和を計算する交換代数の元のコレクション
	 * 
	 * @return 計算結果となる <code>Exalge</code> の新しいインスタンス
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	static public MongoExalge sum(MongoSession session, Collection<? extends Exalge> c) {
		MongoExalge newAlge = new MongoExalge(session);
		for (Exalge alge : c) {
			if (alge != null) {
				newAlge.plusValues(alge);
			}
		}
		return newAlge;
	}

	//------------------------------------------------------------
	// 振替系演算
	//------------------------------------------------------------
	
	/**
	 * 単一基底による基底の置換
	 * <p>
	 * この変換は、変換元基底に一致する基底を、変換先基底で示される基底に置換する。
	 * 基底は、ハット基底キーがワイルドカードとなっている基底パターンとみなされる。
	 * ハット基底キー以外の基底キーにワイルドカードを指定することも可能。変換先基底に
	 * ワイルドカードを含める場合、基底キー文字列の一部にワイルドカードを指定しても、
	 * 基底キー全体が変換対象基底キーに置き換わる。<br>
	 * なお、変換元基底に一致する基底が存在しない場合、この交換代数元と同じものを返す。
	 * <p>
	 * <b>(注)</b> この変換操作は基底の置換となり、変換元基底は結果に含まれない。
	 * 
	 * @param fromBase	変換元基底
	 * @param toBase	変換先基底
	 * @return	変換後の交換代数
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge transform(ExBase fromBase, ExBase toBase) {
		if (fromBase == null)
			throw new NullPointerException("fromBase is null.");
		if (toBase == null)
			throw new NullPointerException("toBase is null.");
		return transform(new ExBasePattern(fromBase, true), new ExBasePattern(toBase, true));
		
		/*
		// 基底パターンの生成
		ExBasePattern.PatternItem[] fromPattern = ExBasePattern.makePatternWithoutHatKey(fromBase);

		// 変換
		MongoExalge retAlge = null;
		MongoExalge tempAlge = null;
		MongoExalge transAlge = null;
		BigIterator<BigExalgeElement> it = null;
		try {
			tempAlge = new MongoExalge(_mongo_session);
			transAlge = new MongoExalge(_mongo_session);
			it = unmodifiableElementIterator();
			while (it.hasNext()) {
				BigExalgeElement elem = it.next();
				if (ExBasePattern.matchesByPattern(fromPattern, elem.getBase())) {
					//--- パターン一致
					ExBase modBase = ExBasePattern.translateBaseKeyWithoutHatKey(toBase, elem.getBase());
					//--- 変換後の値を合成
					transAlge.plusValue(modBase, elem.getValue());
					tempAlge.plusValue(modBase, elem.getValue());
				}
				else {
					//--- パターン不一致
					tempAlge.plusValue(elem.getBase(), elem.getValue());
				}
			}
			// 変換結果の保存
			retAlge = tempAlge;
			tempAlge = null;
		}
		finally {
			if (it != null) {
				it.closeCursor();
			}
			if (transAlge != null) {
				transAlge.delete();
			}
			if (tempAlge != null) {
				tempAlge.delete();
			}
		}
		return retAlge;
		*/
	}
	
	/**
	 * 単一基底パターンによる基底の置換
	 * <p>
	 * この変換は、変換元基底パターンに一致する基底を、変換先基底パターンで示される基底に置換する。
	 * 変換先基底パターンにワイルドカードを含める場合、基底キー文字列の一部にワイルドカードを指定しても、
	 * 基底キー全体が変換対象基底キーに置き換わる。<br>
	 * なお、変換元基底パターンに一致する基底が存在しない場合、この交換代数元と同じものを返す。
	 * <p>
	 * <b>(注)</b> この変換操作は基底の置換となり、変換元基底は結果に含まれない。
	 * 
	 * @param fromPattern	変換元基底パターン
	 * @param toPattern		変換先基底パターン
	 * @return	変換後の交換代数
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge transform(ExBasePattern fromPattern, ExBasePattern toPattern) {
		if (fromPattern == null)
			throw new NullPointerException("fromPattern is null.");
		if (toPattern == null)
			throw new NullPointerException("toPattern is null.");

		// 変換
		MongoExalge retAlge = null;
		MongoExalge tempAlge = null;
		MongoExalge transAlge = null;
		BigIterator<BigExalgeElement> it = null;
		try {
			tempAlge = new MongoExalge(_mongo_session);
			transAlge = new MongoExalge(_mongo_session);
			it = elementIterator();
			while (it.hasNext()) {
				BigExalgeElement elem = it.next();
				if (fromPattern.matches(elem.getBase())) {
					//--- パターン一致
					ExBase modBase = toPattern.translate(elem.getBase());
					//--- 変換後の値を合成
					transAlge.plusValue(modBase, elem.getValue());
					tempAlge.plusValue(modBase, elem.getValue());
				}
				else {
					//--- パターン不一致
					tempAlge.plusValue(elem.getBase(), elem.getValue());
				}
			}
			// 変換結果の保存
			retAlge = tempAlge;
			tempAlge = null;
		}
		finally {
			if (it != null) {
				it.closeCursor();
			}
			if (transAlge != null) {
				transAlge.delete();
			}
			if (tempAlge != null) {
				tempAlge.delete();
			}
		}
		return retAlge;
	}
	
	/**
	 * 複数基底による単一基底への基底の置換
	 * <p>
	 * この変換は、変換元基底のどれか一つに一致する基底を、変換先基底で示される基底に置換する。
	 * 基底は、ハット基底キーがワイルドカードとなっている基底パターンとみなされる。
	 * ハット基底キー以外の基底キーにワイルドカードを指定することも可能。変換先基底に
	 * ワイルドカードを含める場合、基底キー文字列の一部にワイルドカードを指定しても、
	 * 基底キー全体が変換対象基底キーに置き換わる。<br>
	 * なお、変換元基底に一致する基底が存在しない場合、この交換代数元と同じものを返す。
	 * <p>
	 * <b>(注)</b> この変換操作は基底の置換となり、変換元基底は結果に含まれない。
	 * 
	 * @param fromBases	変換元基底集合
	 * @param toBase	変換先基底
	 * @return	変換後の交換代数
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge transform(ExBaseSet fromBases, ExBase toBase) {
		if (fromBases == null)
			throw new NullPointerException("fromBases is null.");
		if (toBase == null)
			throw new NullPointerException("toBase is null.");
		
		if (fromBases.isEmpty()) {
			//--- 振替元がない場合は、この元をそのまま返す。
			return this;
		}
		
		// 基底パターンの生成
		ExBasePatternSet fromPatterns = new ExBasePatternSet(fromBases, true);
		// 変換
		return transform(fromPatterns, new ExBasePattern(toBase, true));
		
		/*
		// 基底パターンの生成
		ExBasePattern.PatternItem[][] fromPatterns = new ExBasePattern.PatternItem[fromBases.size()][];
		int index = 0;
		for (ExBase base : fromBases) {
			fromPatterns[index++] = ExBasePattern.makePatternWithoutHatKey(base._baseKeys);
		}
		
		// 変換
		Exalge retAlge = this.copy();
		Exalge transAlge = new Exalge();
		for (ExBase base : data.keySet()) {
			for (ExBasePattern.PatternItem[] pattern : fromPatterns) {
				if (ExBasePattern.matchesByPattern(pattern, base._baseKeys)) {
					//--- パターン一致
					transAlge.plusValue(ExBasePattern.translateBaseKeyWithoutHatKey(toBase._baseKeys, base), data.get(base));
					retAlge.data.remove(base);
					break;
				}
			}
		}
		
		// 変換結果の合成
		if (transAlge.isEmpty()) {
			// 変換後の要素が存在しないので、この元をそのまま返す。
			return this;
		}
		else {
			// 変換後の要素を合成
			retAlge.plusValues(transAlge);
			return retAlge;
		}
		*/
	}
	
	/**
	 * 複数基底パターンによる単一基底への基底の置換
	 * <p>
	 * この変換は、変換元基底パターンのどれか一つに一致する基底を、変換先基底パターンで示される基底に置換する。
	 * 変換先基底パターンにワイルドカードを含める場合、基底キー文字列の一部にワイルドカードを指定しても、
	 * 基底キー全体が変換対象基底キーに置き換わる。<br>
	 * なお、変換元基底パターンに一致する基底が存在しない場合、この交換代数元と同じものを返す。
	 * <p>
	 * <b>(注)</b> この変換操作は基底の置換となり、変換元基底は結果に含まれない。
	 * 
	 * @param fromPatterns	変換元基底パターン集合
	 * @param toPattern		変換先基底パターン
	 * @return	変換後の交換代数
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge transform(ExBasePatternSet fromPatterns, ExBasePattern toPattern) {
		if (fromPatterns == null)
			throw new NullPointerException("fromPatterns is null.");
		if (toPattern == null)
			throw new NullPointerException("toPattern is null.");
		
		if (fromPatterns.isEmpty()) {
			//--- 振替元がない場合は、この元をそのまま返す。
			return this;
		}

		// 変換
		MongoExalge retAlge = null;
		MongoExalge tempAlge = null;
		MongoExalge transAlge = null;
		BigIterator<BigExalgeElement> it = null;
		try {
			tempAlge = new MongoExalge(_mongo_session);
			transAlge = new MongoExalge(_mongo_session);
			it = elementIterator();
			while (it.hasNext()) {
				BigExalgeElement elem = it.next();
				if (fromPatterns.matches(elem.getBase())) {
					//--- パターン一致
					ExBase modBase = toPattern.translate(elem.getBase());
					//--- 変換後の値を合成
					transAlge.plusValue(modBase, elem.getValue());
					tempAlge.plusValue(modBase, elem.getValue());
				}
				else {
					//--- パターン不一致
					tempAlge.plusValue(elem.getBase(), elem.getValue());
				}
			}
			// 変換結果の保存
			retAlge = tempAlge;
			tempAlge = null;
		}
		finally {
			if (it != null) {
				it.closeCursor();
			}
			if (transAlge != null) {
				transAlge.delete();
			}
			if (tempAlge != null) {
				tempAlge.delete();
			}
		}
		return retAlge;
	}

	/**
	 * 指定された変換テーブルの変換定義に基づき、この交換代数元(<code>Exalge</code> オブジェクト)を変換する。
	 * 変換対象の基底に一致する、異なる変換元基底パターンが複数存在する場合は、
	 * 最初に一致した基底パターンを変換元基底パターンとする全ての変換定義によってのみ変換される。
	 * <p>
	 * 変換結果は、この交換代数元の全ての要素に、変換対象となった
	 * 基底のハットと元の値、変換後の全ての基底と値が加算された結果となる。
	 * <p>
	 * 変換方法については、{@link ExTransfer} クラスの説明を参照のこと。
	 * <p>
	 * <b>(注)</b> この変換では、Bar演算は行わない。変換結果を集約する場合、必要に応じて、
	 * Sum演算、Bar演算を行うこと。
	 * 
	 * @param transfer	変換に使用する {@link ExTransfer} オブジェクト
	 * @return	変換結果となる交換代数元の新しいインスタンスを返す。一つも変換が行われなかった
	 * 			場合は、このオブジェクトをそのまま返す。
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws ArithmeticException		'ratio'属性の変換において、変換比率の合計が 0 の場合にスローされる
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 * 
	 * @see ExTransfer
	 */
	@Override
	public MongoExalge transfer(ExTransfer transfer) {
		MongoExalge retAlge = null;
		MongoExalge tempAlge = null;
		try {
			//--- 変換結果格納オブジェクトの生成
			tempAlge = new MongoExalge(_mongo_session);
			//--- 変換実行
			transfer.bigTransfer(tempAlge, this);
			//--- 変換結果の保存
			retAlge = tempAlge;
			tempAlge = null;
		}
		finally {
			if (tempAlge != null) {
				tempAlge.delete();
			}
		}
		return retAlge;
	}

	/**
	 * 振替変換。Exalge#aggreTransfer(アグリゲーション)と同じ処理
	 * 
	 * @param table 振替変換テーブル
	 * @return 振替変換後の交換代数
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge transfer(TransTable table) {
		// aggreTransfer() と同じ機能
		return aggreTransfer(table);
	}

	/**
	 * 単一基底による振替変換(アグリゲーション)
	 * <p>
	 * この変換は、振替元基底に一致する基底を、振替先基底で示される基底に変換する。
	 * 基底は、ハット基底キーがワイルドカードとなっている基底パターンとみなされる。
	 * ハット基底キー以外の基底キーにワイルドカードを指定することも可能。振替先基底に
	 * ワイルドカードを含める場合、基底キー文字列の一部にワイルドカードを指定しても、
	 * 基底キー全体が変換対象基底キーに置き換わる。<br>
	 * 変換の際、変換対象の基底について、基底のハットと変換後の基底が変換対象基底の値で
	 * 加算される。これは、次のような振る舞いとなる。
	 * <blockquote>
	 * x = a^&lt;e1&gt; + b&lt;e2&gt;<br>
	 * x.aggreTransfer(&lt;e1&gt;, &lt;e3&gt;) = a^&lt;e1&gt; + b&lt;e2&gt; + a&lt;e1&gt; + a^&lt;e3&gt;
	 * </blockquote>
	 * なお、振替元基底に一致する基底が存在しない場合、この交換代数元と同じものを返す。
	 * <p>
	 * <b>(注)</b> この変換では、Bar演算は行わない。変換結果を集約する場合、必要に応じて、
	 * Sum演算、Bar演算を行うこと。
	 * 
	 * @param fromBase	振替元基底
	 * @param toBase	振替先基底
	 * @return	振替変換後の交換代数
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge aggreTransfer(ExBase fromBase, ExBase toBase) {
		if (fromBase == null)
			throw new NullPointerException("fromBase is null.");
		if (toBase == null)
			throw new NullPointerException("toBase is null.");
		
		// 変換
		return aggreTransfer(new ExBasePattern(fromBase, true), new ExBasePattern(toBase, true));

		/*
		// 基底パターンの生成
		ExBasePattern.PatternItem[] fromPattern = ExBasePattern.makePatternWithoutHatKey(fromBase._baseKeys);

		// 変換
		Exalge transAlge = new Exalge();
		for (ExBase base : data.keySet()) {
			if (ExBasePattern.matchesByPattern(fromPattern, base._baseKeys)) {
				//--- パターン一致
				ExBase tBase = ExBasePattern.translateBaseKeyWithoutHatKey(toBase._baseKeys, base);
				BigDecimal value = data.get(base);
				transAlge.plusValue(base.hat(), value);
				transAlge.plusValue(tBase, value);
			}
		}
		
		// 変換結果の合成
		if (transAlge.isEmpty()) {
			// 振替後の要素が存在しないので、この元をそのまま返す。
			return this;
		}
		else {
			// 振替後の要素を合成
			return this.plus(transAlge);
		}
		*/
	}
	
	/**
	 * 単一基底パターンによる振替変換(アグリゲーション)
	 * <p>
	 * この変換は、振替元基底パターンに一致する基底を、振替先基底パターンで示される基底に変換する。
	 * 振替先基底パターンにワイルドカードを含める場合、基底キー文字列の一部にワイルドカードを
	 * 指定しても、基底キー全体が変換対象基底キーに置き換わる。<br>
	 * 変換の際、変換対象の基底について、基底のハットと変換後の基底が変換対象基底の値で
	 * 加算される。これは、次のような振る舞いとなる。
	 * <blockquote>
	 * x = a^&lt;e1&gt; + b&lt;e2&gt;<br>
	 * x.aggreTransfer(&lt;e1&gt;, &lt;e3&gt;) = a^&lt;e1&gt; + b&lt;e2&gt; + a&lt;e1&gt; + a^&lt;e3&gt;
	 * </blockquote>
	 * なお、振替元基底パターンに一致する基底が存在しない場合、この交換代数元と同じものを返す。
	 * <p>
	 * <b>(注)</b> この変換では、Bar演算は行わない。変換結果を集約する場合、必要に応じて、
	 * Sum演算、Bar演算を行うこと。
	 * 
	 * @param fromPattern	振替元基底パターン
	 * @param toPattern		振替先基底パターン
	 * @return	振替変換後の交換代数
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge aggreTransfer(ExBasePattern fromPattern, ExBasePattern toPattern) {
		if (fromPattern == null)
			throw new NullPointerException("fromPattern is null.");
		if (toPattern == null)
			throw new NullPointerException("toPattern is null.");

		// 変換
		MongoExalge retAlge = null;
		MongoExalge transAlge = null;
		BigIterator<BigExalgeElement> it = null;
		try {
			transAlge = copy();
			it = elementIterator();
			while (it.hasNext()) {
				BigExalgeElement elem = it.next();
				if (fromPattern.matches(elem.getBase())) {
					//--- パターン一致
					ExBase tBase = toPattern.translate(elem.getBase());
					BigDecimal value = elem.getValue();
					transAlge.plusValue(elem.getBase().hat(), value);
					transAlge.plusValue(tBase, value);
				}
			}
			// 変換結果の保存
			retAlge = transAlge;
			transAlge = null;
		}
		finally {
			if (it != null) {
				it.closeCursor();
			}
			if (transAlge != null) {
				transAlge.delete();
			}
		}
		return retAlge;
	}
	
	/**
	 * 複数基底による単一基底への振替変換(アグリゲーション)
	 * <p>
	 * この変換は、振替元基底のどれか一つに一致する基底を、振替先基底で示される基底に変換する。
	 * 基底は、ハット基底キーがワイルドカードとなっている基底パターンとみなされる。
	 * ハット基底キー以外の基底キーにワイルドカードを指定することも可能。振替先基底に
	 * ワイルドカードを含める場合、基底キー文字列の一部にワイルドカードを指定しても、
	 * 基底キー全体が変換対象基底キーに置き換わる。<br>
	 * 変換の際、変換対象の基底について、基底のハットと変換後の基底が変換対象基底の値で
	 * 加算される。これは、次のような振る舞いとなる。
	 * <blockquote>
	 * x = a^&lt;e1&gt; + b&lt;e2&gt;<br>
	 * x.aggreTransfer(&lt;e1&gt;, &lt;e3&gt;) = a^&lt;e1&gt; + b&lt;e2&gt; + a&lt;e1&gt; + a^&lt;e3&gt;
	 * </blockquote>
	 * なお、振替元基底に一致する基底が存在しない場合、この交換代数元と同じものを返す。
	 * <p>
	 * <b>(注)</b> この変換では、Bar演算は行わない。変換結果を集約する場合、必要に応じて、
	 * Sum演算、Bar演算を行うこと。
	 * 
	 * @param fromBases	振替元基底集合
	 * @param toBase	振替先基底
	 * @return	振替変換後の交換代数
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge aggreTransfer(ExBaseSet fromBases, ExBase toBase) {
		if (fromBases == null)
			throw new NullPointerException("fromBases is null.");
		if (toBase == null)
			throw new NullPointerException("toBase is null.");
		
		if (fromBases.isEmpty()) {
			//--- 振替元がない場合は、この元をそのまま返す。
			return this;
		}
		
		// 基底パターンの生成
		ExBasePatternSet fromPatterns = new ExBasePatternSet(fromBases, true);
		// 変換
		return aggreTransfer(fromPatterns, new ExBasePattern(toBase, true));

		/*
		// 基底パターンの生成
		ExBasePattern.PatternItem[][] fromPatterns = new ExBasePattern.PatternItem[fromBases.size()][];
		int index = 0;
		for (ExBase base : fromBases) {
			fromPatterns[index++] = ExBasePattern.makePatternWithoutHatKey(base._baseKeys);
		}
		
		// 変換
		Exalge transAlge = new Exalge();
		for (ExBase base : data.keySet()) {
			for (ExBasePattern.PatternItem[] pattern : fromPatterns) {
				if (ExBasePattern.matchesByPattern(pattern, base._baseKeys)) {
					//--- パターン一致
					ExBase tBase = ExBasePattern.translateBaseKeyWithoutHatKey(toBase._baseKeys, base);
					BigDecimal value = data.get(base);
					transAlge.plusValue(base.hat(), value);
					transAlge.plusValue(tBase, value);
					break;
				}
			}
		}
		
		// 変換結果の合成
		if (transAlge.isEmpty()) {
			// 振替後の要素が存在しないので、この元をそのまま返す。
			return this;
		}
		else {
			// 振替後の要素を合成
			return this.plus(transAlge);
		}
		*/
	}
	
	/**
	 * 複数基底パターンによる単一基底への振替変換(アグリゲーション)
	 * <p>
	 * この変換は、振替元基底パターンのどれか一つに一致する基底を、振替先基底パターンで示される基底に変換する。
	 * 振替先基底パターンにワイルドカードを含める場合、基底キー文字列の一部にワイルドカードを指定しても、
	 * 基底キー全体が変換対象基底キーに置き換わる。<br>
	 * 変換の際、変換対象の基底について、基底のハットと変換後の基底が変換対象基底の値で
	 * 加算される。これは、次のような振る舞いとなる。
	 * <blockquote>
	 * x = a^&lt;e1&gt; + b&lt;e2&gt;<br>
	 * x.aggreTransfer(&lt;e1&gt;, &lt;e3&gt;) = a^&lt;e1&gt; + b&lt;e2&gt; + a&lt;e1&gt; + a^&lt;e3&gt;
	 * </blockquote>
	 * なお、振替元基底パターンに一致する基底が存在しない場合、この交換代数元と同じものを返す。
	 * <p>
	 * <b>(注)</b> この変換では、Bar演算は行わない。変換結果を集約する場合、必要に応じて、
	 * Sum演算、Bar演算を行うこと。
	 * 
	 * @param fromPatterns	振替元基底パターン集合
	 * @param toPattern		振替先基底パターン
	 * @return	振替変換後の交換代数
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge aggreTransfer(ExBasePatternSet fromPatterns, ExBasePattern toPattern) {
		if (fromPatterns == null)
			throw new NullPointerException("fromPatterns is null.");
		if (toPattern == null)
			throw new NullPointerException("toPattern is null.");
		
		if (fromPatterns.isEmpty()) {
			//--- 振替元がない場合は、この元をそのまま返す。
			return this;
		}

		// 変換
		MongoExalge retAlge = null;
		MongoExalge transAlge = null;
		BigIterator<BigExalgeElement> it = null;
		try {
			transAlge = copy();
			it = elementIterator();
			while (it.hasNext()) {
				BigExalgeElement elem = it.next();
				if (fromPatterns.matches(elem.getBase())) {
					//--- パターン一致
					ExBase tBase = toPattern.translate(elem.getBase());
					BigDecimal value = elem.getValue();
					transAlge.plusValue(elem.getBase().hat(), value);
					transAlge.plusValue(tBase, value);
				}
			}
			// 変換結果の保存
			retAlge = transAlge;
			transAlge = null;
		}
		finally {
			if (it != null) {
				it.closeCursor();
			}
			if (transAlge != null) {
				transAlge.delete();
			}
		}
		return retAlge;
	}

	/**
	 * 振替変換(アグリゲーション)
	 * <p>
	 * この変換は、指定された振替変換テーブルの変換定義に基づく振替変換を行う。
	 * この交換代数元の要素の基底に一致する、異なる振替元基底パターンが複数存在する
	 * 場合、最初に一致した振替元基底パターンの振替先基底パターンによって振替変換が
	 * 行われる。
	 * 変換の際、変換対象の要素について、基底のハットと変換後の基底が変換対象基底の値で
	 * 加算される。
	 * <p>
	 * <b>(注)</b> この変換では、Bar演算は行わない。変換結果を集約する場合、必要に応じて、
	 * Sum演算、Bar演算を行うこと。
	 * 
	 * @param table 振替変換テーブル
	 * @return 振替変換後の交換代数
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge aggreTransfer(TransTable table) {
		// 変換
		MongoExalge retAlge = null;
		MongoExalge transAlge = null;
		BigIterator<BigExalgeElement> it = null;
		try {
			transAlge = copy();
			it = elementIterator();
			while (it.hasNext()) {
				BigExalgeElement elem = it.next();
				ExBase aggreBase = table.transfer(elem.getBase());
				if (aggreBase != null) {
					BigDecimal value = elem.getValue();
					transAlge.plusValue(elem.getBase().hat(), value);
					transAlge.plusValue(aggreBase, value);
				}
			}
			// 変換結果の保存
			retAlge = transAlge;
			transAlge = null;
		}
		finally {
			if (it != null) {
				it.closeCursor();
			}
			if (transAlge != null) {
				transAlge.delete();
			}
		}
		return retAlge;
	}

	/**
	 * 按分変換
	 * <p>
	 * この変換は、変換元基底パターンに一致する基底を、指定された按分比率で変換先基底パターンで
	 * 示される基底に変換する。
	 * 変換の際、変換対象の要素について、基底のハットと変換後の交換代数が加算される。
	 * <p>
	 * このメソッドでは、按分変換マトリクスの按分比率計算方法の
	 * 設定({@link exalge2.TransMatrix#isTotalRatioUsed()} が返す値)により、
	 * 按分値の算出方法が異なる。計算方法の詳細については、{@link exalge2.TransMatrix#transfer(ExBase, BigDecimal)} を参照のこと。
	 * <p>
	 * 按分比率計算における除算においては、{@link MathContext#DECIMAL128} の精度で結果を保持する。
	 * そのため、1 つの値を 3 当分するような按分変換の場合、誤差が発生する。
	 * <br>
	 * 指定の基底に一致する、異なる按分元基底パターンが複数存在する場合、
	 * 最初に一致した按分元基底パターンに関連付けられた変換定義により
	 * 按分変換が行われる。
	 * <p>
	 * <b>(注)</b> この変換では、Bar演算は行わない。変換結果を集約する場合、必要に応じて、
	 * Sum演算、Bar演算を行うこと。
	 * 
	 * @param matrix 按分比率マトリクス
	 * @return 按分変換後の交換代数
	 * 
	 * @see TransMatrix#transfer(ExBase, BigDecimal)
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 * @throws IllegalStateException	一致する按分元基底に対応する按分先基底が存在しない場合にスローされる
	 * @throws ArithmeticException		按分計算において 0 除算が発生した場合にスローされる
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoExalge divideTransfer(TransMatrix matrix) {
		// 変換
		MongoExalge retAlge = null;
		MongoExalge transAlge = null;
		BigIterator<BigExalgeElement> it = null;
		try {
			transAlge = copy();
			it = elementIterator();
			while (it.hasNext()) {
				BigExalgeElement elem = it.next();
				//--- 按分変換(要素)
				Exalge ret = matrix.transfer(elem.getBase(), elem.getValue());
				if (ret != null) {
					transAlge.plusValue(elem.getBase().hat(), elem.getValue());
					transAlge.plusValues(ret);
				}
			}
			// 変換結果の保存
			retAlge = transAlge;
			transAlge = null;
		}
		finally {
			if (it != null) {
				it.closeCursor();
			}
			if (transAlge != null) {
				transAlge.delete();
			}
		}
		return retAlge;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	/**
	 * このオブジェクトの要素数変更カウンタをインクリメントする。
	 * @return	インクリメント後のカウンタ値
	 */
	protected long incrementModCount() {
		if (_modExAlgeSetCount != null) {
			_modExAlgeSetCount.incrementAndGet();
		}
		return _modCount.incrementAndGet();
	}
	
	/**
	 * 交換代数元のすべてのドキュメントに一致するクエリを生成する。
	 * クエリ生成においては、交換代数集合の要素かどうかも考慮される。
	 * @return	フィルターオブジェクト
	 */
	protected Bson makeQeuryAll() {
		if (_elem_id != null) {
			// 交換代数集合の要素
			return Filters.eq(MongoDelegateDocExAlgeSetElem.MONGO_KEY_SETID, _elem_id);
		} else {
			// 単独の交換代数元
			return new Document();
		}
	}

	/**
	 * 指定された基底でドキュメントを検索するクエリを生成する。
	 * クエリ生成においては、交換代数集合の要素かどうかも考慮される。
	 * @param base	判定する基底
	 * @return	フィルターオブジェクト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	protected Bson makeQueryByExBase(ExBase base) {
		if (_elem_id != null) {
			// 交換代数集合の要素
			return Filters.and(
					Filters.eq(MongoDelegateDocExAlgeSetElem.MONGO_KEY_SETID, _elem_id),
					Filters.eq(MongoDelegateDocExalgeElem.MONGO_KEY_BASE, MongoDelegateDocExBase.makeExBaseDocument(base)));
		}
		else {
			// 単独の交換代数元
			return Filters.eq(MongoDelegateDocExalgeElem.MONGO_KEY_BASE, MongoDelegateDocExBase.makeExBaseDocument(base));
		}
	}
	
	/**
	 * 指定された基底を含まないドキュメントを検索するクエリを生成する。
	 * クエリ生成においては、交換代数集合の要素かどうかも考慮される。
	 * @param base	除外する基底
	 * @return	フィルターオブジェクト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	protected Bson makeQueryWithoutExBase(ExBase base) {
		if (_elem_id != null) {
			// 交換代数集合の要素
			return Filters.and(
					Filters.eq(MongoDelegateDocExAlgeSetElem.MONGO_KEY_SETID, _elem_id),
					Filters.ne(MongoDelegateDocExalgeElem.MONGO_KEY_BASE, MongoDelegateDocExBase.makeExBaseDocument(base)));
		}
		else {
			// 単独の交換代数元
			return Filters.ne(MongoDelegateDocExalgeElem.MONGO_KEY_BASE, MongoDelegateDocExBase.makeExBaseDocument(base));
		}
	}

	/**
	 * 指定された基底のコレクションでドキュメントを検索するクエリを生成する。
	 * クエリ生成においては、交換代数集合の要素かどうかも考慮される。
	 * @param bases			判定する基底のコレクション
	 * @param notMatches	指定されたコレクションに一致しないものを検索する場合は <tt>true</tt>
	 * @return	フィルターオブジェクト
	 * @throws NullPointerException	<em>bases</em> が <tt>null</tt> の場合
	 */
	protected Bson makeQueryByMultiExBases(Collection<? extends ExBase> bases, boolean notMatches) {
		ArrayList<Document> aryBaseDocs = new ArrayList<Document>(bases.size());
		for (ExBase base : bases) {
			aryBaseDocs.add(MongoDelegateDocExBase.makeExBaseDocument(base));
		}
		Bson baseFilter;
		if (notMatches)
			baseFilter = Filters.nin(MongoDelegateDocExalgeElem.MONGO_KEY_BASE, aryBaseDocs);
		else
			baseFilter = Filters.in(MongoDelegateDocExalgeElem.MONGO_KEY_BASE, aryBaseDocs);
		
		if (_elem_id != null) {
			// 交換代数集合の要素
			return Filters.and(Filters.eq(MongoDelegateDocExAlgeSetElem.MONGO_KEY_SETID, _elem_id), baseFilter);
		}
		else {
			// 単独の交換代数元
			return baseFilter;
		}
	}

	/**
	 * 指定された値でドキュメントを検索するクエリを生成する。
	 * 値は MongoDB において、比較(compare)により判定され、<tt>null</tt> かどうかも判定される。
	 * クエリ生成においては、交換代数集合の要素かどうかも考慮される。
	 * @param value	判定する値
	 * @return	フィルターオブジェクト
	 */
	protected Bson makeQueryByValue(BigDecimal value) {
		if (_elem_id != null) {
			// 交換代数集合の要素
			return Filters.and(
					Filters.eq(MongoDelegateDocExAlgeSetElem.MONGO_KEY_SETID, _elem_id),
					Filters.eq(MongoDelegateDocExalgeElem.MONGO_KEY_VALUE, value));
		}
		else {
			// 単独の交換代数元
			return Filters.eq(MongoDelegateDocExalgeElem.MONGO_KEY_VALUE, value);
		}
	}

	/**
	 * 指定された値を含まないドキュメントを検索するクエリを生成する。
	 * 値は MongoDB において、比較(compare)により判定され、<tt>null</tt> かどうかも判定される。
	 * クエリ生成においては、交換代数集合の要素かどうかも考慮される。
	 * @param value	除外する値
	 * @return	フィルターオブジェクト
	 */
	protected Bson makeQueryWithoutValue(BigDecimal value) {
		if (_elem_id != null) {
			// 交換代数集合の要素
			return Filters.and(
					Filters.eq(MongoDelegateDocExAlgeSetElem.MONGO_KEY_SETID, _elem_id),
					Filters.ne(MongoDelegateDocExalgeElem.MONGO_KEY_VALUE, value));
		}
		else {
			// 単独の交換代数元
			return Filters.ne(MongoDelegateDocExalgeElem.MONGO_KEY_VALUE, value);
		}
	}

	/**
	 * 指定された値のコレクションでドキュメントを検索するクエリを生成する。
	 * クエリ生成においては、交換代数集合の要素かどうかも考慮される。
	 * @param bases			判定する値のコレクション
	 * @param notMatches	指定されたコレクションに一致しないものを検索する場合は <tt>true</tt>
	 * @return	フィルターオブジェクト
	 * @throws NullPointerException	<em>bases</em> が <tt>null</tt> の場合
	 */
	protected Bson makeQueryByMultiValues(Collection<? extends BigDecimal> values, boolean notMatches) {
		if (values == null)
			throw new NullPointerException();
		Bson baseFilter;
		if (notMatches)
			baseFilter = Filters.nin(MongoDelegateDocExalgeElem.MONGO_KEY_BASE, values);
		else
			baseFilter = Filters.in(MongoDelegateDocExalgeElem.MONGO_KEY_BASE, values);
		
		if (_elem_id != null) {
			// 交換代数集合の要素
			return Filters.and(Filters.eq(MongoDelegateDocExAlgeSetElem.MONGO_KEY_SETID, _elem_id), baseFilter);
		}
		else {
			// 単独の交換代数元
			return baseFilter;
		}
	}

	/**
	 * このオブジェクトが保持するコレクションから、指定された基底と一致するドキュメント数を取得する。
	 * クエリ生成においては、交換代数集合の要素かどうかも考慮される。
	 * @param base	比較する基底
	 * @return	指定された基底と一致するドキュメント数
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースに関する処理に失敗した場合
	 */
	protected long getNumDocumentsByExBase(ExBase base) {
		try {
			return _mongo_col.countDocuments(makeQueryByExBase(base));
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to count document by ExBase in MongoExalge[" + _mongo_col.getNamespace().toString() + "] :", ex);
		}
	}

	/**
	 * このオブジェクトが保持するコレクションから、指定された値と一致するドキュメント数を取得する。
	 * クエリ生成においては、交換代数集合の要素かどうかも考慮される。
	 * @param value	比較する値
	 * @return	指定された値と一致するドキュメント数
	 * @throws MongoAlgeError	データベースに関する処理に失敗した場合
	 */
	protected long getNumDocumentsByValue(BigDecimal value) {
		try {
			return _mongo_col.countDocuments(makeQueryByValue(value));
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to count document by Value in MongoExalge[" + _mongo_col.getNamespace().toString() + "] :", ex);
		}
	}
	
	/**
	 * このオブジェクトが交換代数集合の要素であれば、交換代数元 ID のみを含むドキュメントを生成する。
	 * @return	交換代数元 ID を含むドキュメント、交換代数集合の要素でない場合は空のドキュメント
	 * @throws MongoAlgeError	データベースに関する処理に失敗した場合
	 */
	protected Document makeSetIdDocument() {
		Document data = new Document();
		if (_elem_id != null) {
			data.append(MongoDelegateDocExAlgeSetElem.MONGO_KEY_SETID, _elem_id);
		}
		return data;
	}

	/**
	 * この交換代数元に含まれるドキュメントから、指定されたクエリで検索した結果を返す。
	 * @param filter			検索条件、<tt>null</tt> の場合はすべてのドキュメント
	 * @param withoutObjectID	検索結果から MongoDB の ObjectID を除外する場合は <tt>true</tt>
	 * @param withoutSetID		検索結果から交換代数元 ID を除外する場合は <tt>true</tt>
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
				result = result.projection(new Document(MongoDelegateDocExAlgeSetElem.MONGO_KEY_SETID, 0));
			return result;
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Faild to query documents by specified filter from MongoExalge[" + _mongo_col.getNamespace().toString() + "] :", ex);
		}
	}

	/**
	 * この交換代数元に含まれるすべての要素を検索した結果を返す。
	 * クエリ生成においては、交換代数集合の要素かどうかも考慮される。
	 * @param withoutObjectID	検索結果から MongoDB の ObjectID を除外する場合は <tt>true</tt>
	 * @param withoutSetID		検索結果から交換代数元 ID を除外する場合は <tt>true</tt>
	 * @return	検索結果のイテレート可能オブジェクト
	 * @throws MongoAlgeError	データベースに関する処理に失敗した場合
	 */
	protected FindIterable<? extends Document> findAllDocuments(boolean withoutObjectID, boolean withoutSetID) {
		return queryDocuments(makeSetIdDocument(), withoutObjectID, withoutSetID);
	}
	
	/**
	 * この交換代数元に含まれるドキュメントから、指定された基底と一致するドキュメントを検索した結果を返す。
	 * クエリ生成においては、交換代数集合の要素かどうかも考慮される。
	 * @param base	比較する基底
	 * @param withoutObjectID	検索結果から MongoDB の ObjectID を除外する場合は <tt>true</tt>
	 * @param withoutSetID		検索結果から交換代数元 ID を除外する場合は <tt>true</tt>
	 * @return	検索結果のイテレート可能オブジェクト
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースに関する処理に失敗した場合
	 */
	protected FindIterable<? extends Document> findByExBase(ExBase base, boolean withoutObjectID, boolean withoutSetID) {
		return queryDocuments(makeQueryByExBase(base), withoutObjectID, withoutSetID);
	}

	/**
	 * この交換代数元に含まれるドキュメントから、指定された値と一致するドキュメントを検索した結果を返す。
	 * MongoDB では、値は等しいかどうか(compare)で判定され、<tt>null</tt> も判定される。
	 * クエリ生成においては、交換代数集合の要素かどうかも考慮される。
	 * @param value	判定する値
	 * @param withoutObjectID	検索結果から MongoDB の ObjectID を除外する場合は <tt>true</tt>
	 * @param withoutSetID		検索結果から交換代数元 ID を除外する場合は <tt>true</tt>
	 * @return	検索結果のイテレート可能オブジェクト
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースに関する処理に失敗した場合
	 */
	protected FindIterable<? extends Document> findByValue(BigDecimal value, boolean withoutObjectID, boolean withoutSetID) {
		return queryDocuments(makeQueryByValue(value), withoutObjectID, withoutSetID);
	}

	/**
	 * このオブジェクトが保持するコレクションから、すべての値でソートされたイテレート可能オブジェクトを取得する。
	 * このオブジェクトが交換代数集合の要素の場合は、交換代数元 ID で抽出された結果のソート済みイテレート可能オブジェクトとなる。
	 * @param withoutObjectID	検索結果から MongoDB の ObjectID を除外する場合は <tt>true</tt>
	 * @param withoutSetID		検索結果から交換代数元 ID を除外する場合は <tt>true</tt>
	 * @return	イテレート可能オブジェクト
	 * @throws MongoAlgeError	データベースに関する処理に失敗した場合
	 */
	protected FindIterable<? extends Document> sortedDocuments(boolean withoutObjectID, boolean withoutSetID) {
		try {
			FindIterable<? extends Document> result = _mongo_col.find(makeSetIdDocument()).sort(MongoDelegateDocExalgeElem.MONGO_SORT_EXALGE_ELEM_ALL);
			if (withoutObjectID)
				result = result.projection(MongoUtil.MONGO_FILTER_WITHOUT_OBJID);
			if (_elem_id != null && withoutSetID)
				result = result.projection(new Document(MongoDelegateDocExAlgeSetElem.MONGO_KEY_SETID, 0));
			return result;
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Faild to sort by all Exalge fields in MongoExalge[" + _mongo_col.getNamespace().toString() + "] :", ex);
		}
	}

	/**
	 * このオブジェクトが保持するコレクションから、基底が重複しないドキュメントの検索結果を取得する。
	 * @return 検索結果のイテレート可能オブジェクト
	 * @throws MongoAlgeError	データベースに関する処理に失敗した場合
	 */
	protected DistinctIterable<? extends Document> distinctByExBase() {
		try {
			if (_elem_id != null) {
				// 交換代数集合の元
				return _mongo_col.distinct(MongoDelegateDocExalgeElem.MONGO_KEY_BASE,
						Filters.eq(MongoDelegateDocExAlgeSetElem.MONGO_KEY_SETID, _elem_id), Document.class);
			}
			else {
				// 単独の交換代数元
				return _mongo_col.distinct(MongoDelegateDocExalgeElem.MONGO_KEY_BASE, Document.class);
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to distinct by ExBase in MongoExalge[" + _mongo_col.getNamespace().toString() + "] :", ex);
		}
	}

	/**
	 * 交換代数元の要素として格納する MongoDB ドキュメントを、指定されたパラメータから生成する。
	 * このメソッドは、交換代数集合の要素の場合、交換代数元 ID も含める。
	 * @param base	基底
	 * @param value	値
	 * @return	MongoDB ドキュメント
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 */
	protected Document makeExalgeElemDocument(ExBase base, BigDecimal value) {
		Document data = MongoDelegateDocExalgeElem.makeExalgeElemDocument(base, value);
		if (_elem_id != null) {
			data.append(MongoDelegateDocExAlgeSetElem.MONGO_KEY_SETID, _elem_id);
		}
		return data;
	}

	/**
	 * 交換代数元の要素として基底のみを格納する MongoDB ドキュメントを、指定された基底から生成する。
	 * このメソッドは、交換代数集合の要素の場合、交換代数元 ID も含める。
	 * @param base	基底
	 * @return	生成されたドキュメント
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 */
	protected Document makeExBaseDocument(ExBase base) {
		Document data = new Document();
		if (_elem_id != null) {
			data.append(MongoDelegateDocExAlgeSetElem.MONGO_KEY_SETID, _elem_id);
		}
		MongoDelegateDocExalgeElem.appendExalgeElemBaseToDocument(data, base);
		return data;
	}

	/**
	 * 交換代数元の要素を抽出するフィルターを、指定された交換代数元要素のドキュメントから生成する。
	 * このメソッドでは、交換代数基底の要素を指定されたドキュメントから抽出し、新たに生成したドキュメントに格納する。
	 * @param data	交換代数元の要素のドキュメント
	 * @return	生成されたドキュメント
	 * @throws NullPointerException	<em>data</em> が <tt>null</tt> の場合
	 */
	protected Document makeExBaseFilterFromData(Document data) {
		Document filter = new Document();
		if (_elem_id != null) {
			filter.append(MongoDelegateDocExAlgeSetElem.MONGO_KEY_SETID, _elem_id);
		}
		return filter.append(MongoDelegateDocExalgeElem.MONGO_KEY_BASE, data.get(MongoDelegateDocExalgeElem.MONGO_KEY_BASE));
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
	 * 指定された文字列セットのどれか一つに一致する基底キーを持つ基底を取得する。
	 * <p>基底キーはインデックスで指定する。
	 * 
	 * @param keyIndex	判定対象の基底キーを示すインデックス
	 * @param targets	検索キーとなる文字列のセット
	 * @return	指定の文字列と一致する基底キーを持つ基底の集合を返す。
	 * 			一致する基底が見つからなかった場合は、要素が空の基底集合を返す。
	 * 			<code>targets</code> の要素が空の場合も、要素が空の基底集合を返す。
	 * 
	 * @throws NullPointerException	<code>targets</code> が <tt>null</tt> の場合
	 * @throws IndexOutOfBoundsException	<code>keyIndex</code> が範囲外の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	protected MongoExBaseSet getBasesByBaseKey(int keyIndex, Set<String> targets) {
		MongoExBaseSet retBases = new MongoExBaseSet(_mongo_session);
		if (!targets.isEmpty()) {
			MongoCursor<? extends Document> cursor = distinctByExBase().iterator(); 
			try {
				while (cursor.hasNext()) {
					Document docExalgeElem = cursor.next();
					String strKey = MongoDelegateDocExalgeElem.getExBaseKeyStringByIndex(docExalgeElem, keyIndex);
					if (targets.contains(strKey)) {
						ExBase base = MongoDelegateDocExalgeElem.toExBaseFromDocument(docExalgeElem);
						retBases.add(base);
					}
				}
			}
			finally {
				MongoUtil.closeCursorSilent(cursor);
			}
		}
		return retBases;
	}

	/**
	 * 指定された文字列セットのどれか一つに一致する基底キーを持つ要素のみを取り出す。
	 * <p>基底キーはインデックスで指定する。
	 * 
	 * @param keyIndex	判定対象の基底キーを示すインデックス
	 * @param targets	検索キーとなる文字列のセット
	 * @return	指定の文字列と一致する基底キーを持つ要素のみとなる交換代数元を返す。
	 * 			一致する基底が見つからなかった場合は、要素が空の交換代数元を返す。
	 * 			<code>targets</code> の要素が空の場合も、要素が空の交換代数元を返す。
	 * 
	 * @throws NullPointerException	<code>targets</code> が <tt>null</tt> の場合
	 * @throws IndexOutOfBoundsException	<code>keyIndex</code> が範囲外の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	protected MongoExalge projectionByBaseKey(int keyIndex, Set<String> targets) {
		MongoExalge retAlge = new MongoExalge(_mongo_session);
		if (!targets.isEmpty()) {
			MongoCursor<? extends Document> cursor = distinctByExBase().iterator();
			try {
				while (cursor.hasNext()) {
					Document docExalgeElem = cursor.next();
					String strKey = MongoDelegateDocExalgeElem.getExBaseKeyStringByIndex(docExalgeElem, keyIndex);
					if (targets.contains(strKey)) {
						ExBase base = MongoDelegateDocExalgeElem.toExBaseFromDocument(docExalgeElem);
						BigDecimal val = MongoDelegateDocExalgeElem.getBigDecimalValueFromDocument(docExalgeElem);
						retAlge.plusValue(base, val);
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
	 * 交換代数内部のすべての値をクリア(０に設定)する。
	 * <p>
	 * (注) このメソッドは<b>破壊的メソッド</b>である。
	 * @throws MongoRuntimeError	データベースの処理が正常に行えなかった場合
	 *
	protected void clearValue() {
		BigDecimal value = BigDecimal.ZERO;
		Iterator<ExBase> it = data.keySet().iterator();
		while (it.hasNext()) {
			ExBase base = it.next();
			put(base, value);
		}
	}
	/**/

	/**
	 * 指定された基底と値を代入する。
	 * <p>
	 * (注)このメソッドは、インスタンスの値を書き換える。
	 * <p>
	 * すでに同一基底が存在する場合、指定された値で上書きされる。
	 * <br>
	 * 値がマイナスの時、自動的に＾計算を行って値をプラスにするように設計されている。
	 * <p>
	 * (注) このメソッドは<b>破壊的メソッド</b>である。
	 * 
	 * @param base 交換代数の基底
	 * @param value 値
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	protected void putValue(ExBase base, BigDecimal value) {
		ExBase tgBase;
		BigDecimal tgValue;
		if (value != null && value.compareTo(BigDecimal.ZERO) < 0) {
			tgBase  = base.hat();
			tgValue = value.abs();
		} else {
			tgBase  = base;
			tgValue = value;
		}
		Document data = makeExalgeElemDocument(tgBase, tgValue);
		Document where = makeExBaseFilterFromData(data);
		UpdateResult result = MongoUtil.updateDocument(_mongo_col, where, data, false, true);
		if (result.getMatchedCount() == 0 && result.getUpsertedId() != null) {
			// inserted new document
			incrementModCount();
		}
	}

	/**
	 * 指定された基底と値を、交換代数に加算する。
	 * <p>
	 * (注)このメソッドは、インスタンスの値を書き換える。
	 *<p>
	 * 値がマイナスの時、自動的に ^ 計算を行って値をプラスにするように設計されている。
	 * <p>
	 * (注) このメソッドは<b>破壊的メソッド</b>である。
	 * 
	 * @param base 交換代数の基底
	 * @param value 値
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	protected void plusValue(ExBase base, BigDecimal value) {
		ExBase tgBase;
		BigDecimal tgValue;
		if (value != null && value.compareTo(BigDecimal.ZERO) < 0) {
			tgBase  = base.hat();
			tgValue = value.abs();
		} else {
			tgBase  = base;
			tgValue = value;
		}
		// 同一基底に加算
		try {
			Document where = makeExBaseDocument(tgBase);
			Document data  = _mongo_col.find(where).first();
			if (data == null) {
				// insert new document
				MongoDelegateDocExalgeElem.appendExalgeElemValueToDocument(where, tgValue);
				_mongo_col.insertOne(where);
				incrementModCount();
			}
			else {
				// update document
				BigDecimal orgValue = MongoDelegateDocExalgeElem.getBigDecimalValueFromDocument(data);
				tgValue = tgValue.add(orgValue);
				data = new Document();
				MongoDelegateDocExalgeElem.appendExalgeElemValueToDocument(data, tgValue);
				data = new Document("$set", data);
				_mongo_col.updateOne(where, data);
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to update added value in MongoExalge[" + _mongo_col.getNamespace().toString() + "] :", ex);
		}
	}

	/**
	 * 指定された交換代数の元を、この交換代数に加算する。
	 * <p>
	 * (注) このメソッドは<b>破壊的メソッド</b>である。
	 * 
	 * @param alge 加算する交換代数の元
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	protected void plusValues(Exalge alge) {
		Iterator<Map.Entry<ExBase, BigDecimal>> it = alge.getUnmodifiableEntrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<ExBase, BigDecimal> entry = it.next();
			plusValue(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * 交換代数元の要素イテレーターを生成する。
	 * 
	 * @return <code>MongoExalge</code> の <code>Iterator</code>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	protected BigIterator<Exalge> newMongoExalgeIterator() {
		// TODO: カーソルのクローズ処理を今後考慮
		try {
			//--- ソート済みイテレータにする
			MongoCursor<? extends Document> cursor = sortedDocuments(true, true).iterator();
			return new MongoExalgeIterator(cursor);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get sorted cursor from MongoExalge[" + _mongo_col.getNamespace().toString() + "] :", ex);
		}
	}
	
	protected BigIterator<BigExalgeElement> newUnmodifiableElementIterator() {
		// TODO: カーソルのクローズ処理を今後考慮
		try {
			//--- ソート済みイテレータにする
			MongoCursor<? extends Document> cursor = sortedDocuments(true, true).iterator();
			return new MongoExalgeElementIterator(cursor);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get sorted cursor from MongoExalge[" + _mongo_col.getNamespace().toString() + "] :", ex);
		}
	}
	
	protected BigIterator<ExBase> newUnmodifiableExBaseIterator() {
		// TODO: カーソルのクローズ処理を今後考慮
		try {
			//--- ソート済みイテレータにする
			MongoCursor<? extends Document> cursor = sortedDocuments(true, true).iterator();
			return new MongoExalgeElementBaseIterator(cursor);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get sorted cursor from MongoExalge[" + _mongo_col.getNamespace().toString() + "] :", ex);
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	/**
	 * <code>MongoExalge</code> クラスの要素の値を保持するクラス。
	 * <p>このオブジェクトは不変である。
	 * 
	 * @version 0.990
	 * @since 0.990
	 * 
	 * @author SOARS Project.
	 * @author Y.Ishizuka(PieCake.inc,)
	 */
	static public class MongoExalgeElement implements BigExalgeElement
	{
		private final ExBase		_base;
		private final BigDecimal	_value;
		
		public MongoExalgeElement(ExBase base, BigDecimal value) {
			_base = base;
			_value = value;
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
	 * <code>MongoExalge</code> クラスの要素にアクセス可能なイテレーターの共通実装。
	 * <p>
	 * このクラスは、交換代数元<code>(MongoExalge)</code>から交換代数要素(単一の基底と値を持つ交換代数元)を
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
	private abstract class AbstractMongoExalgeElementIterator<T> implements BigIterator<T>
	{
		MongoCursor<? extends Document>	_itCursor;
		long _expectedModCount;
		
		AbstractMongoExalgeElementIterator(MongoCursor<? extends Document> cursor) {
			_itCursor = cursor;
			_expectedModCount = _modCount.get();
		}

		public boolean hasNext() {
			try {
				return (_itCursor==null ? false : _itCursor.hasNext());
			} catch (MongoException ex) {
				throw new MongoAlgeError("MongoExalge's element iterator couldn't check next element in MongoExalge[" + _mongo_col.getNamespace().toString() + "] :", ex);
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
	 * <code>MongoExalge</code> クラスの要素イテレーター。
	 * <p>
	 * このクラスは、交換代数元<code>(MongoExalge)</code>から交換代数要素(BigExalgeElement)を
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
	private class MongoExalgeElementIterator extends AbstractMongoExalgeElementIterator<BigExalgeElement>
	{
		MongoExalgeElementIterator(MongoCursor<? extends Document> cursor) {
			super(cursor);
		}
		
		@Override
		public BigExalgeElement next() {
			if (_expectedModCount != _modCount.get())
				throw new ConcurrentModificationException();
			if (_itCursor == null)
				throw new NoSuchElementException("MongoExalge's cursor was closed.");
			try {
				Document docExalgeElem = _itCursor.next();
				ExBase base = MongoDelegateDocExalgeElem.toExBaseFromDocument(docExalgeElem);
				BigDecimal value = MongoDelegateDocExalgeElem.getBigDecimalValueFromDocument(docExalgeElem);
				if (!_itCursor.hasNext()) {
					closeCursor();
				}
				return new MongoExalgeElement(base, value);
			} catch (MongoException ex) {
				throw new MongoAlgeError("MongoExalge's iterator couldn't get next element in MongoExalge[" + _mongo_col.getNamespace().toString() + "] :", ex);
			}
		}
	}
	
	/**
	 * <code>MongoExalge</code> クラスの交換代数基底としての要素イテレーター。
	 * <p>
	 * このクラスは、交換代数元<code>(MongoExalge)</code>から交換代数要素の基底(ExBase)を
	 * 順次取得するためのイテレーターである。
	 * <p>
	 * このクラスのインタフェースでは、{@link #remove()} はサポートされていない。
	 * 
	 * @version 0.991
	 * @since 0.990
	 * 
	 * @author SOARS Project.
	 * @author Y.Ishizuka(PieCake.inc,)
	 */
	private class MongoExalgeElementBaseIterator extends AbstractMongoExalgeElementIterator<ExBase>
	{
		MongoExalgeElementBaseIterator(MongoCursor<? extends Document> cursor) {
			super(cursor);
		}
		
		@Override
		public ExBase next() {
			if (_expectedModCount != _modCount.get())
				throw new ConcurrentModificationException();
			if (_itCursor == null)
				throw new NoSuchElementException("MongoExalge's cursor was closed.");
			try {
				Document docExalgeElem = _itCursor.next();
				ExBase base = MongoDelegateDocExalgeElem.toExBaseFromDocument(docExalgeElem);
				if (!_itCursor.hasNext()) {
					closeCursor();
				}
				return base;
			} catch (MongoException ex) {
				throw new MongoAlgeError("MongoExalge's iterator couldn't get next element in MongoExalge[" + _mongo_col.getNamespace().toString() + "] :", ex);
			}
		}
	}
	
	/**
	 * <code>MongoExalge</code> クラスの交換代数元としての要素イテレーター。
	 * <p>
	 * このクラスは、交換代数元<code>(MongoExalge)</code>から交換代数要素(単一の基底と値を持つ交換代数元)を
	 * 順次取得するためのイテレーターである。
	 * <p>
	 * このクラスのインタフェースでは、{@link #remove()} はサポートされていない。
	 * 
	 * @version 0.991
	 * @since 0.990
	 * 
	 * @author SOARS Project.
	 * @author Y.Ishizuka(PieCake.inc,)
	 */
	private class MongoExalgeIterator extends AbstractMongoExalgeElementIterator<Exalge>
	{
		MongoExalgeIterator(MongoCursor<? extends Document> cursor) {
			super(cursor);
		}
		
		@Override
		public Exalge next() {
			if (_expectedModCount != _modCount.get())
				throw new ConcurrentModificationException();
			if (_itCursor == null)
				throw new NoSuchElementException("MongoExalge's cursor was closed.");
			try {
				Document docExalgeElem = _itCursor.next();
				ExBase base = MongoDelegateDocExalgeElem.toExBaseFromDocument(docExalgeElem);
				BigDecimal value = MongoDelegateDocExalgeElem.getBigDecimalValueFromDocument(docExalgeElem);
				if (!_itCursor.hasNext()) {
					closeCursor();
				}
				return new Exalge(base, value);
			} catch (MongoException ex) {
				throw new MongoAlgeError("MongoExalge's iterator couldn't get next element in MongoExalge[" + _mongo_col.getNamespace().toString() + "] :", ex);
			}
		}
	}
}
