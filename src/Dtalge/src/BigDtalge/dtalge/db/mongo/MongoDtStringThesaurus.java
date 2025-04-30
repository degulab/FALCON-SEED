/*
 * @(#)MongoDtStringThesaurus.java	0.5.0	2019/02/25
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.db.mongo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoException;
import com.mongodb.MongoNamespace;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.GraphLookupOptions;
import com.mongodb.client.result.DeleteResult;

import dtalge.DtStringThesaurus;
import dtalge.db.BigDtStringThesaurus;
import dtalge.db.BigDtStringThesaurusElement;
import dtalge.exception.CsvFormatException;
import dtalge.io.internal.CsvReader;
import dtalge.io.internal.CsvWriter;
import dtalge.util.Strings;
import dtalge.util.Validations;
import redundantalge.db.BigIterator;
import redundantalge.db.mongo.MongoAlgeError;
import redundantalge.db.mongo.MongoSession;
import redundantalge.db.mongo.MongoUtil;

/**
 * 大容量のシソーラス定義を保持するクラス。
 * 基本的に、{@link dtalge.DtStringThesaurus} と同様のインタフェースを提供する。
 * <p>ストレージとして MongoDB を利用する。
 * なお、外部からデータベースのコレクション変更は関知しない。
 * <br>
 * また、<b>この実装は同期化されない</b>。
 * <p>
 * <b>《入出力フォーマット》</b>
 * <br>
 * <code>MongoDtStringThesaurus</code> の MongoDB におけるデータ構造は、次の通りである。
 * <pre><code>
 * {
 *   &quot;node&quot; : &lt;シソーラス語句&gt;
 *   &quot;children&quot; : &lt;子のシソーラス語句の配列;&gt;
 * }
 * </code></pre>
 * 
 * @version 0.5.0
 * @since 0.5.0
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public class MongoDtStringThesaurus implements BigDtStringThesaurus<MongoDtStringThesaurusElement>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	/** 一時的なコレクション名のプレフィックス **/
	static public final String	TEMP_COLNAME_PREFIX	= "TDtst";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	protected MongoSession				_mongo_session;
	protected MongoCollection<Document>	_mongo_col;
	protected AtomicLong				_modCount = new AtomicLong(0L);
	/** このシソーラス定義が所属するシソーラス定義名、所属しない場合は <tt>null</tt> **/
	protected final String				_thesname;
	protected final AtomicLong			_modNamedThesCount;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定された MongoDB セッションにおいて、一時的なコレクションをストレージとする新しいインスタンスを生成する。
	 * <p>このメソッドで作成されたコレクションは、MongoDB セッションが切断されるときに破棄される。
	 * @param session	MongoDB セッションオブジェクト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public MongoDtStringThesaurus(MongoSession session) {
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
	public MongoDtStringThesaurus(MongoSession session, String collection) {
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
		_thesname = null;
		_modNamedThesCount = null;
	}

	/**
	 * 指定された名前付きシソーラス定義の要素を操作対象とする新しいインスタンスを生成する。
	 * <p>このメソッドでは、指定されたコレクションを一時コレクションとして登録しない。
	 * @param namedthes	対象とする名前付きシソーラス定義
	 * @param thesname	対象とするシソーラス定義名
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	MongoDtStringThesaurus(MongoDtNamedStringThesaurus namedthes, String thesname) {
		_mongo_session = namedthes._mongo_session;
		_mongo_col     = namedthes._mongo_vals_col;
		_modNamedThesCount = namedthes._modCount;
		if (thesname == null)
			throw new NullPointerException("Name of thesaurus is null.");
		_thesname = thesname;
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
		if (_thesname != null) {
			// 名前付きシソーラス定義の要素であれば、新しいコレクションにコピー
			ArrayList<Bson> list = new ArrayList<Bson>();
			list.add(Aggregates.match(new Document("$" + MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_NAME, _thesname)));
			list.add(Aggregates.project(new Document("_id", 0).append(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_NAME, 0)));
			list.add(Aggregates.out(newCollectionName));
			_mongo_col.aggregate(list).toCollection();	// toCollection() を呼ばないとコレクションに出力されない
		}
		else {
			// 単独のデータ代数元であれば、コレクション名を変更
			try {
				_mongo_col.renameCollection(newName);
			}
			catch (MongoException ex) {
				throw new MongoAlgeError("Failed to persist of MongoDtStringThesaurus" + _mongo_col.getNamespace().toString() + ":", ex);
			}
			//--- 一時的コレクションから除外
			_mongo_session.unregisterTemporaryCollection(oldName);
		}
		_mongo_col = _mongo_session.getPersistentCollection(newCollectionName);
	}
	
	/**
	 * このオブジェクトが管理するストレージが、名前付きシソーラス定義のものかどうかを判定する。
	 * 名前付きシソーラス定義のものである場合、このオブジェクトに対する破壊型メソッドの呼び出しでは {@link java.lang.UnsupportedOperationException} がスローされる。
	 * @return	名前付きシソーラス定義の要素であれば <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean isView() {
		return (_thesname != null);
	}

	/**
	 * このオブジェクトが名前付きシソーラス定義のビューである場合に、{@link java.lang.UnsupportedOperationException} をスローする。
	 */
	protected void ensureNotView() {
		if (_thesname != null)
			throw new UnsupportedOperationException("This object is view of MongoDtNamedStringThesaurus" + _mongo_col.getNamespace().toString());
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
		try {
			_mongo_col.drop();
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to drop collection of MongoDtStringThesaurus" + _mongo_col.getNamespace().toString() + ":", ex);
		}
		//_mongo_session.unregisterTemporaryCollection(_mongo_col.getNamespace());
	}

	/**
	 * シソーラス定義をすべてクリアする
	 * @throws UnsupportedOperationException	このオブジェクトがビューの場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public void clear() {
		ensureNotView();
		MongoUtil.clearAllDocuments(_mongo_col);
		_modCount.incrementAndGet();
	}

	/**
	 * シソーラス定義が存在しないなら <tt>true</tt> を返す。
	 * 
	 * @return	シソーラス定義が存在しない場合は <tt>true</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public boolean isEmpty() {
		return (size() == 0L);
	}

	/**
	 * シソーラス定義の関係数を返す。
	 * 関係数とは、2 つの語句の直接的な結びつき(親子関係)を 1 とした場合の総数となる。
	 * 従って、親を複数持つ子がある場合、その関係もカウントされる。
	 * 
	 * @return 語句の直接的な関係数
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public long size() {
		try {
			return _mongo_col.countDocuments(makeQueryAll());
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to count documents in MongoDtStringThesaurus" + _mongo_col.getNamespace().toString() + ":", ex);
		}
	}

	/**
	 * 指定された語句が直接的な関係となる子を有する場合は <tt>true</tt> を返す。
	 * 
	 * @param word	判定する語句
	 * @return	直接の子が定義されている場合は <tt>true</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public boolean hasChild(String word) {
		return (word==null ? false : existsParentOfRelation(word));
	}

	/**
	 * 指定された語句が直接的な関係となる親を有する場合は <tt>true</tt> を返す。
	 * 
	 * @param word	判定する語句
	 * @return	直接の親が定義されている場合は <tt>true</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public boolean hasParent(String word) {
		return (word==null ? false : existsChildOfRelation(word));
	}

	/**
	 * 指定された語句がシソーラス定義内に存在する場合は <tt>true</tt> を返す。
	 * 
	 * @param word	判定する語句
	 * @return	関係が定義されている語句であれば <tt>true</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public boolean contains(String word) {
		if (word != null) {
			try {
				if (_mongo_col.countDocuments(makeQueryRelationByWord(word)) > 0L) {
					return true;
				}
			}
			catch (MongoException ex) {
				throw new MongoAlgeError("Failed to query relations by word as parent or child in MongoDtStringThesaurus" + _mongo_col.getNamespace().toString() + ":", ex);
			}
		}
		return false;
	}
	
	/**
	 * 指定された語句の集合のどれか一つがシソーラス定義内に存在する場合は <tt>true</tt> を返す。
	 * 
	 * @param words		判定する語句の配列
	 * @return	シソーラス定義内に存在すれば <tt>true</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public boolean containsAny(String...words) {
		if (words != null) {
			try {
				Bson query = Filters.or(Filters.in(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_PARENT, words),
						Filters.in(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_CHILD, words));
				if (_thesname != null) {
					query = Filters.and(Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_NAME, _thesname), query);
				}
				if (_mongo_col.countDocuments(query) > 0L) {
					return true;
				}
			}
			catch (MongoException ex) {
				throw new MongoAlgeError("Failed to query relations by any word in MongoDtStringThesaurus" + _mongo_col.getNamespace().toString() + ":", ex);
			}
		}
		return false;
	}

	/**
	 * 指定された語句の集合のどれか一つがシソーラス定義内に存在する場合は <tt>true</tt> を返す。
	 * 
	 * @param c		判定する語句の集合
	 * @return	シソーラス定義内に存在すれば <tt>true</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public boolean containsAny(Collection<? extends String> c) {
		if (c != null) {
			try {
				Bson query = Filters.or(Filters.in(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_PARENT, c),
						Filters.in(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_CHILD, c));
				if (_thesname != null) {
					query = Filters.and(Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_NAME, _thesname), query);
				}
				if (_mongo_col.countDocuments(query) > 0L) {
					return true;
				}
			}
			catch (MongoException ex) {
				throw new MongoAlgeError("Failed to query relations by any word in MongoDtStringThesaurus" + _mongo_col.getNamespace().toString() + ":", ex);
			}
		}
		return false;
	}
	
	/**
	 * 指定された語句全てがシソーラス定義内に存在する場合は <tt>true</tt> を返す。
	 * 
	 * @param words		判定する語句の配列
	 * @return	シソーラス定義内に全て存在すれば <tt>true</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public boolean containsAll(String...words) {
		if (words == null || words.length < 1)
			return false;
		for (String word : words) {
			if (!contains(word)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 指定された語句全てがシソーラス定義内に存在する場合は <tt>true</tt> を返す。
	 * 
	 * @param c		判定する語句の集合
	 * @return	シソーラス定義内に全て存在すれば <tt>true</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public boolean containsAll(Collection<? extends String> c) {
		if (c == null || c.isEmpty())
			return false;
		for (String word : c) {
			if (!contains(word)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 指定された親子関係がシソーラス定義内に存在する場合は <tt>true</tt> を返す。
	 * 
	 * @param parent	親となる語句
	 * @param child		子となる語句
	 * @return	2 つの語句が親子として定義されていれば <tt>true</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public boolean containsRelation(String parent, String child) {
		try {
			return (_mongo_col.countDocuments(makeQueryRelationPair(parent, child)) > 0L);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to query relations by parent and child in MongoDtStringThesaurus" + _mongo_col.getNamespace().toString() + ":", ex);
		}
	}

	/**
	 * 指定された語句の直接の親となる全ての語句を取得する。
	 * @param word	判定する語句
	 * @return	親となる全ての語句を格納する配列を返す。
	 * 			指定された語句が登録されていない場合や、親となる語句が未定義の場合は、
	 * 			要素が空の配列を返す。
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public String[] getParents(String word) {
		String[] ret = DtStringThesaurus.EmptyStringArray;
		List<String> list = getThesaurusParents(word);
		if (!list.isEmpty()) {
			ret = list.toArray(new String[list.size()]);
		}
		return ret;
	}

	/**
	 * 指定された語句の直接の子となる全ての語句を取得する。
	 * @param word	判定する語句
	 * @return	子となる全ての語句を格納する配列を返す。
	 * 			指定された語句が登録されていない場合や、子となる語句が未定義の場合は、
	 * 			要素が空の配列を返す。
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public String[] getChildren(String word) {
		String[] ret = DtStringThesaurus.EmptyStringArray;
		List<String> list = getThesaurusChildren(word);
		if (!list.isEmpty()) {
			ret = list.toArray(new String[list.size()]);
		}
		return ret;
	}
	
	/**
	 * 指定された語句の直接の親となる全ての語句を取得する。
	 * @param word	判定する語句
	 * @return	親となる全ての語句を格納する文字列リストを返す。
	 * 			指定された語句が登録されていない場合や、親となる語句が未定義の場合は、
	 * 			要素が空の文字列リストを返す。
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public List<String> getThesaurusParents(String word) {
		List<String> ret = Collections.emptyList();
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = _mongo_col.find(makeQueryRelationByChild(word)).iterator();
			if (cursor.hasNext()) {
				ArrayList<String> list = new ArrayList<>();
				while (cursor.hasNext()) {
					Document docElem = cursor.next();
					String strParent = MongoDelegateDocDtStringThesaurusElem.getThesaurusParentWordFromDocument(docElem);
					if (strParent != null) {
						list.add(strParent);
					}
				}
				if (!list.isEmpty()) {
					ret = list;
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get parents of the word in MongoDtStringThesaurus" + _mongo_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		return ret;
	}
	
	/**
	 * 指定された語句の直接の子となる全ての語句を取得する。
	 * @param word	判定する語句
	 * @return	子となる全ての語句を格納する文字列リストを返す。
	 * 			指定された語句が登録されていない場合や、子となる語句が未定義の場合は、
	 * 			要素が空の文字列リストを返す。
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public List<String> getThesaurusChildren(String word) {
		List<String> ret = Collections.emptyList();
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = _mongo_col.find(makeQueryRelationByParent(word)).iterator();
			if (cursor.hasNext()) {
				ArrayList<String> list = new ArrayList<>();
				while (cursor.hasNext()) {
					Document docElem = cursor.next();
					String strChild = MongoDelegateDocDtStringThesaurusElem.getThesaurusChildWordFromDocument(docElem);
					if (strChild != null) {
						list.add(strChild);
					}
				}
				if (!list.isEmpty()) {
					ret = list;
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get children of the word in MongoDtStringThesaurus" + _mongo_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		return ret;
	}

	/**
	 * 指定された 2 つの語句が比較可能(関係を持つ)であれば <tt>true</tt> を返す。
	 * なお、2 つの引数が同値の場合、<tt>false</tt> を返す。
	 * 
	 * @param word1		検証する語句
	 * @param word2		検証する語句のもう一方
	 * @return	比較可能であれば <tt>true</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public boolean isComparable(String word1, String word2) {
		// 同値？
		if (word1 == null || word2 == null) {
			return false;
		}
		if (word1.equals(word2)) {
			return false;	// 同値の場合は、比較不能？
		}
		
		// word1 が word2 の子孫か検証する
		if (hasRelation(word1, word2)) {
			return true;
		}
		
		// word2 が word1 の子孫か検証する
		if (hasRelation(word2, word1)) {
			return true;
		}
		
		// 比較不能(関係は存在しない)
		return false;
	}

	/**
	 * 指定された 2 つの語句をシソーラス定義に基づき比較する。
	 * <code>word1</code> が <code>word2</code> の子孫にあたる(<code>word1</code> &lt; <code>word2</code>)場合は負の値を返す。
	 * <code>word1</code> が <code>word2</code> の祖先にあたる(<code>word1</code> &gt; <code>word2</code>)場合は正の値を返す。
	 * 上記以外の場合は 0 を返す。<br>
	 * なお、2 つの語句のどちらかが <tt>null</tt> もしくは同値の場合も 0 を返す。
	 * 
	 * @param word1		比較する語句
	 * @param word2		比較する語句のもう一方
	 * @return	比較結果を返す。
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public int compare(String word1, String word2) {
		// 同値？
		if (word1 == null || word2 == null) {
			return 0;
		}
		if (word1.equals(word2)) {
			return 0;
		}
		
		// word1 < word2
		if (hasRelation(word1, word2)) {
			return (-1);
		}
		
		// word1 > word2
		if (hasRelation(word2, word1)) {
			return (1);
		}
		
		// no relation
		return (0);
	}

	/**
	 * 指定された語句の集合が分類集合かを判定する。
	 * <p>
	 * 分類集合は、それぞれの語句の全ての組み合わせで比較不可能であることが
	 * 条件となる。
	 * 
	 * @param words		検証する語句の集合
	 * @return	分類集合であれば <tt>true</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public boolean isClassificationSet(String...words) {
		// 集合の要素が存在しない場合は、分類集合とみなさない
		if (words == null || words.length < 1) {
			return false;
		}
		
		// 集合の要素がシソーラス定義にすべて存在しなければ、分類集合とみなさない
		if (!containsAll(words)) {
			return false;
		}
		
		// 要素が 1 つなら、分類集合とみなす
		if (words.length == 1) {
			return true;
		}
		
		// すべての要素の関係をチェックする
		int outerLimit = words.length - 1;
		int innerLimit = words.length;
		for (int i = 0; i < outerLimit; i++) {
			String word1 = words[i];
			for (int j = i+1; j < innerLimit; j++) {
				String word2 = words[j];
				if (isComparable(word1, word2)) {
					// 比較可能な語句を含むため、分類集合とみなさない
					return false;
				}
			}
		}
		
		// すべての語句の組み合わせが比較不可能なので、分類集合とみなす
		return true;
	}
	
	/**
	 * 指定された語句の集合が分類集合かを判定する。
	 * <p>
	 * 分類集合は、それぞれの語句の全ての組み合わせで比較不可能であることが
	 * 条件となる。
	 * 
	 * @param c		検証する語句の集合
	 * @return	分類集合であれば <tt>true</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public boolean isClassificationSet(Collection<? extends String> c) {
		// 集合の要素が存在しない場合は、分類集合とみなさない
		if (c == null || c.isEmpty()) {
			return false;
		}
		
		// 分類集合かを検証
		return isClassificationSet(c.toArray(new String[c.size()]));
	}
	
	/**
	 * 2 つの語句の関係が比較可能であり、(<tt>descendant</tt> &lt; <tt>ancestor</tt>) で
	 * あるかどうかを判定する。
	 * 現在のシソーラス定義において「子は親よりも小さい」ため、<code>descendant</code> の先祖が
	 * <code>ancestor</code> の場合のみ、<tt>true</tt> を返す。
	 * <p>なお、<code>descendant</code> もしくは <code>ancestor</code> のどちらかが <tt>null</tt> の
	 * 場合、このメソッドは <tt>false</tt> を返す。
	 * @param ancestor		先祖とみなす語句
	 * @param descendant	子孫とみなす語句
	 * @return (<tt>descendant</tt> &lt; <tt>ancestor</tt>) の場合に <tt>true</tt> を返す
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public boolean lessThan(String descendant, String ancestor) {
		if (descendant == ancestor || descendant == null || descendant.equals(ancestor)) {
			// 比較不能
			return false;
		}
		
		return hasRelation(descendant, ancestor);
	}

	/**
	 * このオブジェクトの複製を生成する。
	 * 
	 * @return 実体が複製されたオブジェクトの新しいインスタンス
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoDtStringThesaurus copy() {
		MongoDtStringThesaurus newThes = new MongoDtStringThesaurus(_mongo_session);
		if (_thesname != null) {
			// 名前付きシソーラス定義の要素
			MongoCursor<? extends Document> cursor = null;
			try {
				cursor = findAllDocuments(true, true).iterator();
				while (cursor.hasNext()) {
					newThes._mongo_col.insertOne(cursor.next());
				}
			}
			catch (MongoException ex) {
				throw new MongoAlgeError("Failed to copy MongoDtStringThesaurus" + _mongo_col.getNamespace().toString() + ":", ex);
			}
			finally {
				MongoUtil.closeCursorSilent(cursor);
			}
		}
		else {
			// 単独のシソーラス定義
			MongoUtil.duplicateCollection(newThes._mongo_col, _mongo_col);
		}
		return newThes;
	}

	/**
	 * 語句の関係をシソーラスへ登録する。
	 * <p>
	 * このメソッドは、2 つの語句の親子関係(大小関係)をシソーラスへ登録する。
	 * 指定された関係が登録済みの場合、このメソッドは <tt>false</tt> を返す。
	 * なお、子として指定された語句がすでに別の親の子として関係が定義されている
	 * 場合、すでに存在する親を新しく指定された親との関係として上書きする。
	 * <p>
	 * シソーラス定義の制約に基づき、次の場合は例外をスローする。
	 * <ul>
	 * <li>語句が <tt>null</tt> もしくは、長さ 0 の文字列の場合
	 * <li>指定されたた 2 つの語句が等しい場合
	 * <li><code>parent</code> の語句が子、<code>child</code> の語句が親として定義済みの場合(循環関係となる為)
	 * </ul>
	 * 
	 * @param parent	親として登録する語句
	 * @param child		子として登録する語句
	 * @return			新しい関係が登録された場合 <tt>true</tt>
	 * 
	 * @throws IllegalArgumentException	指定された語句もしくは語句の関係が適切ではない場合
	 * @throws UnsupportedOperationException	このオブジェクトがビューの場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public boolean put(String parent, String child) {
		ensureNotView();
		// check
		Validations.validArgument(!Strings.isNullOrEmpty(parent), "Illegal parent : %s", String.valueOf(parent));
		Validations.validArgument(!Strings.isNullOrEmpty(child), "Illegal child : %s", String.valueOf(child));
		Validations.validArgument(!parent.equals(child), "Parent word is same as child word : %s", String.valueOf(parent));
		
		// 登録済みの関係かチェックする
		if (containsRelation(parent, child)) {
			// already exist relation
			return false;
		}
		
		// 循環参照のチェック
		if (hasRelation(parent, child)) {
			// parent の親が child として登録済みの場合、この関係は循環参照となる。
			throw new IllegalArgumentException(
					String.format("Illegal [\"%s\" < \"%s\"] relation, because already exist [\"%s\" > \"%s\"] relation.",
							child, parent, child, parent));
		}
		
		// 登録
		try {
			Document docElem;
			if (_thesname != null) {
				docElem = MongoDelegateDocDtStringThesaurusElem.makeNamedThesaurusElemDocument(_thesname, parent, child);
			} else {
				docElem = MongoDelegateDocDtStringThesaurusElem.makeThesaurusElemDocument(parent, child);
			}
			_mongo_col.insertOne(docElem);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to insert relation into MongoDtStringThesaurus" + _mongo_col.getNamespace().toString() + ":", ex);
		}
		
		// 登録完了
		return true;
	}

	/**
	 * 指定された語句をシソーラスから除去する。
	 * <p>
	 * このメソッドは、指定された語句そのものをシソーラスから除去するため、
	 * この語句への全ての関係を除去する。
	 * 
	 * @param word	除去する語句
	 * @return	語句が除去された場合は <tt>true</tt>
	 * @throws UnsupportedOperationException	このオブジェクトがビューの場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public boolean remove(String word) {
		ensureNotView();
		try {
			DeleteResult ret = _mongo_col.deleteMany(makeQueryRelationByWord(word));
			return (ret.getDeletedCount() > 0L);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to delete word from MongoDtStringThesaurus" + _mongo_col.getNamespace().toString() + ":", ex);
		}
	}

	/**
	 * 指定された親子関係をシソーラスから除去する。
	 * 
	 * @param parent	除去する関係の親となる語句
	 * @param child		除去する関係の子となる語句
	 * @return	関係が除去された場合は <tt>true</tt>
	 * @throws UnsupportedOperationException	このオブジェクトがビューの場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public boolean remove(String parent, String child) {
		ensureNotView();
		try {
			DeleteResult ret = _mongo_col.deleteMany(makeQueryRelationPair(parent, child));
			return (ret.getDeletedCount() > 0L);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to delete relation from MongoDtStringThesaurus" + _mongo_col.getNamespace().toString() + ":", ex);
		}
	}

	/**
	 * このシソーラスのハッシュコード値を返す。
	 * シソーラスのハッシュコードは、シソーラス定義の各エントリのハッシュコード値の
	 * 合計である。これにより、任意の 2 つのシソーラス <tt>t1</tt> と <tt>t2</tt> に
	 * ついて、<tt>t1.equals(t2)</tt> の場合 <tt>t1.hashCode()==t2.hashCode()</tt> に
	 * なる。
	 * 
	 * @return	このシソーラスのハッシュコード値
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public int hashCode() {
		int h = 0;
		
//        int keyHash = (key==null ? 0 : key.hashCode());
//        int valueHash = (value==null ? 0 : value.hashCode());
//        return keyHash ^ valueHash;
//        int h = 0;
//        Iterator<Entry<K,V>> i = entrySet().iterator();
//        while (i.hasNext())
//            h += i.next().hashCode();
//        return h;
		// 要素の比較
		MongoCursor<? extends Document> cursor = sortedDocuments(true, true).iterator();
		try {
			while (cursor.hasNext()) {
				Document thisDoc   = cursor.next();
				String thisParent = MongoDelegateDocDtStringThesaurusElem.getThesaurusParentWordFromDocument(thisDoc);
				String thisChild  = MongoDelegateDocDtStringThesaurusElem.getThesaurusChildWordFromDocument(thisDoc);
				h += ((thisParent==null ? 0 : thisParent.hashCode()) ^ (thisChild==null ? 0 : thisChild.hashCode()));
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to calc hash code of MongoDtStringThesaurus" + _mongo_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		
		return h;
	}

	/**
	 * 指定されたオブジェクトとこのシソーラスが等しいかどうかを比較する。
	 * 指定されたオブジェクトがシソーラスであり、2 つのシソーラスが同じ
	 * 定義を表す場合に <tt>true</tt> を返す。
	 * 
	 * @param obj	このシソーラスと等しいかどうかを比較するオブジェクト
	 * @return	指定されたオブジェクトがこのシソーラスと等しい場合は <tt>true</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;	// same instance
		}
		
		if (obj instanceof DtStringThesaurus) {
			return isSameRelations((DtStringThesaurus)obj);
		}
		else if (obj instanceof BigDtStringThesaurus) {
			return isSameRelations((BigDtStringThesaurus<?>)obj);
		}
		
		// not matched
		return false;
	}

	/**
	 * 2 つのシソーラス定義が同値であるかを検証する。
	 * 指定されたオブジェクトがシソーラスであり、2 つのシソーラスが同じ
	 * 定義を表す場合に <tt>true</tt> を返す。
	 * <p>
	 * 現在の実装において、このメソッドにより同等と判定された場合でも、
	 * このオブジェクトが返すハッシュコード値と <code>thes.hashCode()</code> の値が一致するとは限らない。
	 * 
	 * @param thes 同値性を比較する対象のシソーラス定義
	 * 
	 * @return 指定されたオブジェクトがこのシソーラスと等しい場合は <tt>true</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean isSameRelations(DtStringThesaurus thes) {
		if (thes == null)	return false;
		
		// 要素数の比較
		long numThis = this.size();
		long numThat = thes.size();
		if (numThis != numThat)
			return false;
		else if (numThis == 0L)
			return true;	// 要素がどちらも空の場合は、同値とみなす
		
		// 要素の比較
		MongoCursor<? extends Document> cursor = findAllDocuments(true, true).iterator();
		try {
			while (cursor.hasNext()) {
				Document thisDoc   = cursor.next();
				String thisParent = MongoDelegateDocDtStringThesaurusElem.getThesaurusParentWordFromDocument(thisDoc);
				String thisChild  = MongoDelegateDocDtStringThesaurusElem.getThesaurusChildWordFromDocument(thisDoc);
				if (!thes.containsRelation(thisParent, thisChild)) {
					//--- 親子関係なし
					return false;
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to judge as same between DtStringThesaurus and MongoDtStringThesaurus" + _mongo_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		
		// 全て同値
		return true;
	}

	/**
	 * 2 つのシソーラス定義が同値であるかを検証する。
	 * 指定されたオブジェクトがシソーラスであり、2 つのシソーラスが同じ
	 * 定義を表す場合に <tt>true</tt> を返す。
	 * <p>
	 * 現在の実装において、このメソッドにより同等と判定された場合でも、
	 * このオブジェクトが返すハッシュコード値と <code>thes.hashCode()</code> の値が一致するとは限らない。
	 * 
	 * @param thes 同値性を比較する対象のシソーラス定義
	 * 
	 * @return 指定されたオブジェクトがこのシソーラスと等しい場合は <tt>true</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public boolean isSameRelations(BigDtStringThesaurus<? extends BigDtStringThesaurusElement> thes) {
		if (thes == null)	return false;
		
		// 要素数の比較
		long numThis = this.size();
		long numThat = thes.size();
		if (numThis != numThat)
			return false;
		else if (numThis == 0L)
			return true;	// 要素がどちらも空の場合は、同値とみなす
		
		// 要素の比較
		MongoCursor<? extends Document> cursor = findAllDocuments(true, true).iterator();
		try {
			while (cursor.hasNext()) {
				Document thisDoc   = cursor.next();
				String thisParent = MongoDelegateDocDtStringThesaurusElem.getThesaurusParentWordFromDocument(thisDoc);
				String thisChild  = MongoDelegateDocDtStringThesaurusElem.getThesaurusChildWordFromDocument(thisDoc);
				if (!thes.containsRelation(thisParent, thisChild)) {
					//--- 親子関係なし
					return false;
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to judge as same between BigDtStringThesaurus and MongoDtStringThesaurus" + _mongo_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		
		// 全て同値
		return true;
	}

	/**
	 * このシソーラスの文字列表現を返します。
	 * 大容量の場合、10,000 要素まで出力します。
	 * <p>
	 * 文字列表現は、エントリ(親と子の関係定義)の文字列表現を中括弧 (<tt>"{}"</tt>) で囲んで示すリストとなる。
	 * エントリの文字列表現は、1 つの親と 1 つ以上の子で構成され、次のように表される。
	 * <blockquote>
	 * <i>親</i><b>-&gt;</b><i>子</i>
	 * </blockquote>
	 * 隣接するエントリの文字列表現は、文字 <tt>", "</tt> (コンマと空白文字) によって
	 * 区切られる。
	 * <p>
	 * この実装は空の文字列バッファを作成し、左中括弧を付加してから、定義のエントリを
	 * 反復して調べ、各エントリの文字列表現を順に付加していく。
	 * 最後のエントリの後には右中括弧が付加され、文字列バッファから文字列を取得して返す。
	 * 
	 * @return	このシソーラスの文字列表現
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		MongoCursor<? extends Document> cursor = null;
		try {
			long num = size();
			long cnt = 0L;
			Document docElem;
			cursor = sortedDocuments(true, true).iterator();
			if (cursor.hasNext()) {
				//--- first
				docElem = cursor.next();
				sb.append(MongoDelegateDocDtStringThesaurusElem.getThesaurusParentWordFromDocument(docElem));
				sb.append("->");
				sb.append(MongoDelegateDocDtStringThesaurusElem.getThesaurusChildWordFromDocument(docElem));
				++cnt;
			}
			for (; cursor.hasNext() && cnt < 10000L; cnt++) {
				docElem = cursor.next();
				sb.append(", ");
				sb.append(MongoDelegateDocDtStringThesaurusElem.getThesaurusParentWordFromDocument(docElem));
				sb.append("->");
				sb.append(MongoDelegateDocDtStringThesaurusElem.getThesaurusChildWordFromDocument(docElem));
			}
			if (cursor.hasNext()) {
				sb.append("+...too many elements(");
				sb.append(num);
				sb.append(" elements)");
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to convert to string from MongoDtStringThesaurus" + _mongo_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		sb.append("}");
		return sb.toString();
	}
	
	/**
	 * このオブジェクトのイテレーターを返す。
	 * @return	イテレーターオブジェクト
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public BigIterator<MongoDtStringThesaurusElement> iterator() {
		// TODO: カーソルのクローズ処理を今後考慮
		try {
			//--- ソート済みイテレータにする
			MongoCursor<? extends Document> cursor = sortedDocuments(false, false).iterator();
			return new MongoDtStringThesaurusElemIterator(cursor);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get sorted cursor from MongoDtBaseSet" + _mongo_col.getNamespace().toString() + ":", ex);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	/**
	 * このシソーラス定義において、指定された子の語句が親を持つ関係として含まれているかを判定する。
	 * @param child	判定する関係の子の語句
	 * @return	関係が含まれている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	protected boolean existsChildOfRelation(String child) {
		try {
			return (_mongo_col.countDocuments(makeQueryRelationByChild(child)) > 0L);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to query relations by word as child in MongoDtStringThesaurus" + _mongo_col.getNamespace().toString() + ":", ex);
		}
	}
	
	/**
	 * このシソーラス定義において、指定された親の語句が子を持つ関係として含まれているかを判定する。
	 * @param parent	判定する関係の親の語句
	 * @return	関係が含まれている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	protected boolean existsParentOfRelation(String parent) {
		try {
			return (_mongo_col.countDocuments(makeQueryRelationByParent(parent)) > 0L);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to query relations by word as parent in MongoDtStringThesaurus" + _mongo_col.getNamespace().toString() + ":", ex);
		}
	}

	/**
	 * 2 つの語句が関係を持つかを検証する。
	 * 現在のシソーラス定義において、<code>descendant</code> の先祖が
	 * <code>ancestor</code> なら、<tt>true</tt> を返す。
	 * なお、<code>descendant.equals(ancestor)</code> が <tt>true</tt> の
	 * 場合、このメソッドは <tt>false</tt> を返す。
	 * <p><b>注:</b>このメソッドでは、2 つの引数は <tt>null</tt> ではなく、
	 * 同値でもないことを前提としているため、<tt>null</tt> もしくは同値で
	 * あるかの検証は行わない。
	 * 
	 * @param descendant	子孫とみなす語句
	 * @param ancestor		先祖とみなす語句
	 * 
	 * @return	2 つの語句の関係が定義されている場合は <tt>true</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	protected boolean hasRelation(String descendant, String ancestor) {
		// check exists child
		if (!existsChildOfRelation(descendant)) {
			return false;
		}
		// check exists parent
		if (!existsParentOfRelation(ancestor)) {
			return false;
		}
		// check exist pair
		if (containsRelation(ancestor, descendant)) {
			//--- exist pair
			return true;
		}
		// check exists relation
		return hasRelationRecursive(descendant, ancestor);
	}
	
	/**
	 * 2 つの語句が関係を持つかを検証する。
	 * このメソッドは、<em>descendant</em> の親を辿り、<em>ancestor</em> が
	 * 見つかるまで再帰的に探索する。
	 * 
	 * @param descendant	子孫とみなす語句
	 * @param ancestor		先祖とみなす語句
	 * 
	 * @return	2 つの語句の関係が定義されている場合は <tt>true</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	protected boolean hasRelationRecursive(String descendant, String ancestor) {
		GraphLookupOptions glopts = new GraphLookupOptions();
		glopts.depthField(MongoDelegateDocDtStringThesaurusElem.MONGO_AGGRE_KEY_DEPTH);
		if (_thesname != null) {
			glopts.restrictSearchWithMatch(Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_NAME, _thesname));
		}
		
		ArrayList<Bson> aggre = new ArrayList<>();
		//--- matches by name & decendant
		if (_thesname != null) {
			//--- 名前付き
			aggre.add(Aggregates.match(Filters.and(
					Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_NAME, _thesname),
					Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_CHILD, descendant)
				)));
		}
		else {
			//--- 単独
			aggre.add(Aggregates.match(Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_CHILD, descendant)));
		}
		//--- graph lookup
		aggre.add(Aggregates.graphLookup(
				_mongo_col.getNamespace().getCollectionName(),
				MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_$PARENT,
				MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_PARENT,
				MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_CHILD,
				MongoDelegateDocDtStringThesaurusElem.MONGO_AGGRE_KEY_GRAPH,
				glopts
		));
		//--- projection
		Document docProj = new Document();
		if (_thesname != null) {
			docProj.append(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_NAME, 1);
		}
		docProj.append(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_PARENT, 1)
			.append(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_CHILD, 1)
			.append(MongoDelegateDocDtStringThesaurusElem.MONGO_AGGRE_KEY_GRAPH, new Document("$filter",new Document()
				.append("input", MongoDelegateDocDtStringThesaurusElem.MONGO_AGGRE_KEY_$GRAPH)
				.append("as", "nlist")
				.append("cond", new Document("$eq", Arrays.asList(
						"$$nlist."+MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_PARENT, ancestor)))
			));
		aggre.add(Aggregates.project(docProj));
		//--- remove empty nodelist from results
		aggre.add(Aggregates.match(Filters.expr(new Document("$gt", Arrays.asList(
				new Document("$size", MongoDelegateDocDtStringThesaurusElem.MONGO_AGGRE_KEY_$GRAPH), 0)
		))));
		
		// aggregation
		try {
			return (_mongo_col.aggregate(aggre).first() != null);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to query relations of the words in MongoDtStringThesaurus" + _mongo_col.getNamespace().toString() + ":", ex);
		}
		
		
		
		/*
		Set<String> parents = relationMap.get(descendant);
		while (parents != null) {
			// has relation?
			if (parents.contains(ancestor)) {
				return true;
			}
			
			// next parents
			if (parents.size() == 1) {
				parents = relationMap.get(parents.iterator().next());
			} else {
				break;
			}
		}
		if (parents != null) {
			// next parents
			for (String parent : parents) {
				if (hasRelationRecursive(parent, ancestor)) {
					return true;
				}
			}
		}
		/**/
		
		// not have relation
		//return false;
	}
	
	/**
	 * このシソーラス定義から、指定された関係を除去する。
	 * 
	 * @param parent	除去する関係の親となる語句
	 * @param child		除去する関係の子となる語句
	 * @return	関係が除去された場合は <tt>true</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	protected boolean removeRelation(String parent, String child) {
		try {
			DeleteResult ret = _mongo_col.deleteOne(makeQueryRelationPair(parent, child));
			return (ret.getDeletedCount() > 0L);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to delete relation from MongoDtStringThesaurus" + _mongo_col.getNamespace().toString() + ":", ex);
		}
	}
	
//	/**
//	 * 子親関係マップから、指定された関係を除去する。
//	 * <p>
//	 * このメソッドは子親関係マップからのみ関係を除去するものであり、
//	 * 親子関係マップは変更されない。
//	 * 
//	 * @param parent	除去する関係の親となる語句
//	 * @param child		除去する関係の子となる語句
//	 * @return	関係が除去された場合は <tt>true</tt>
//	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
//	 */
//	protected boolean removeFromRelationMap(String parent, String child) {
//		boolean removed = false;
//
//		Set<String> parents = relationMap.get(child);
//		if (parents != null) {
//			removed = parents.remove(parent);
//			if (parents.isEmpty()) {
//				relationMap.remove(child);
//			}
//		}
//		
//		return removed;
//	}
//
//	/**
//	 * 親子関係マップから、指定された関係を除去する。
//	 * <p>
//	 * このメソッドは親子関係マップからのみ関係を除去するものであり、
//	 * 子親関係マップは変更されない。
//	 * 
//	 * @param parent	除去する関係の親となる語句
//	 * @param child		除去する関係の子となる語句
//	 * @return	関係が除去された場合は <tt>true</tt>
//	 */
//	protected boolean removeFromChildrenMap(String parent, String child) {
//		boolean removed = false;
//		
//		Set<String> children = childrenMap.get(parent);
//		if (children != null) {
//			removed = children.remove(child);
//			if (children.isEmpty()) {
//				childrenMap.remove(parent);
//			}
//		}
//		
//		return removed;
//	}
	
//	/**
//	 * 子親マップの複数の親を格納するコレクションを生成する。
//	 * @return 複数の親を格納する <code>Set</code> コレクション
//	 * @since 0.30
//	 */
//	protected Set<String> createParentsCollection() {
//		return new TreeSet<String>();
//	}
//
//	/**
//	 * 親子マップの複数の子を格納するコレクションを生成する。
//	 * 
//	 * @return	複数の子を格納する <code>Set</code> コレクション
//	 */
//	protected Set<String> createChildrenCollection() {
//		return new TreeSet<String>();
//	}
//
//	/**
//	 * 子親関係マップを生成する。
//	 * 
//	 * @return 子から親への関係を格納するマップ
//	 * @since 0.30
//	 */
//	protected Map<String,Set<String>> createMPRelationMap() {
//		return new TreeMap<String,Set<String>>();
//	}
//
//	/**
//	 * 親子関係マップを生成する。
//	 * 
//	 * @return 親から子への関係を格納するマップ
//	 */
//	protected Map<String,Set<String>> createChildrenMap() {
//		return new TreeMap<String,Set<String>>();
//	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	/**
	 * このオブジェクトの要素数変更カウンタをインクリメントする。
	 * @return	インクリメント後のカウンタ値
	 */
	protected long incrementModCount() {
		if (_modNamedThesCount != null) {
			_modNamedThesCount.incrementAndGet();
		}
		return _modCount.incrementAndGet();
	}
	
	/**
	 * このオブジェクトのすべてのドキュメントを取得するクエリを生成する。
	 * クエリ生成においては、名前付きシソーラス定義の要素かどうかも考慮される。
	 * @return	フィルターオブジェクト
	 */
	protected Bson makeQueryAll() {
		if (_thesname != null) {
			// 名前付きシソーラス定義の要素
			return Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_NAME, _thesname);
		} else {
			// 単独のオブジェクト
			return new Document();
		}
	}
	
	/**
	 * 指定された語句を親とするドキュメントを取得するクエリを生成する。
	 * このメソッドが生成するクエリは、子の有無については関知しない。
	 * クエリ生成においては、名前付きシソーラス定義の要素かどうかも考慮される。
	 * @param word	親語句として検索する語句
	 * @return	フィルターオブジェクト
	 */
	protected Bson makeQueryByParent(String word) {
		if (_thesname != null) {
			// 名前付きシソーラス定義の要素
			return Filters.and(Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_NAME, _thesname),
								Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_PARENT, word));
		} else {
			// 単独のオブジェクト
			return Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_PARENT, word);
		}
	}
	
	/**
	 * 指定された語句を子とするドキュメントを取得するクエリを生成する。
	 * このメソッドが生成するクエリは、親の有無については関知しない。
	 * クエリ生成においては、名前付きシソーラス定義の要素かどうかも考慮される。
	 * @param word	子語句として検索する語句
	 * @return	フィルターオブジェクト
	 */
	protected Bson makeQueryByChild(String word) {
		if (_thesname != null) {
			// 名前付きシソーラス定義の要素
			return Filters.and(Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_NAME, _thesname),
								Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_CHILD, word));
		} else {
			// 単独のオブジェクト
			return Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_CHILD, word);
		}
	}
	
	/**
	 * 指定された語句を親とし、子を持つドキュメントを取得するクエリを生成する。
	 * このメソッドが生成するクエリは、子を持たない親が含まれるドキュメントは除外される。
	 * クエリ生成においては、名前付きシソーラス定義の要素かどうかも考慮される。
	 * @param word	親語句として検索する語句
	 * @return	フィルターオブジェクト
	 */
	protected Bson makeQueryRelationByParent(String word) {
		if (_thesname != null) {
			// 名前付きシソーラス定義の要素
			return Filters.and(Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_NAME, _thesname),
								Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_PARENT, word),
								Filters.ne(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_CHILD, null));
		} else {
			// 単独のオブジェクト
			return Filters.and(Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_PARENT, word),
								Filters.ne(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_CHILD, null));
		}
	}
	
	/**
	 * 指定された語句を親とし、子を持つドキュメントを取得するクエリを生成する。
	 * このメソッドが生成するクエリは、親を持たない親が含まれるドキュメントは除外される。
	 * クエリ生成においては、名前付きシソーラス定義の要素かどうかも考慮される。
	 * @param word	親語句として検索する語句
	 * @return	フィルターオブジェクト
	 */
	protected Bson makeQueryRelationByChild(String word) {
		if (_thesname != null) {
			// 名前付きシソーラス定義の要素
			return Filters.and(Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_NAME, _thesname),
								Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_CHILD, word),
								Filters.ne(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_PARENT, null));
		} else {
			// 単独のオブジェクト
			return Filters.and(Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_CHILD, word),
								Filters.ne(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_PARENT, null));
		}
	}
	
	/**
	 * 指定された語句が親もしくは子のどちらかに含まれるドキュメントを取得するクエリを生成する。
	 * クエリ生成においては、名前付きシソーラス定義の要素かどうかも考慮される。
	 * @param word	親または子として検索する語句
	 * @return	フィルターオブジェクト
	 */
	protected Bson makeQueryRelationByWord(String word) {
		if (_thesname != null) {
			// 名前付きシソーラス定義の要素
			return Filters.and(Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_NAME, _thesname),
					Filters.or(Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_PARENT, word),
							Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_CHILD, word)));
		} else {
			// 単独のオブジェクト
			return Filters.or(Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_PARENT, word),
								Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_CHILD, word));
		}
	}
	
	protected Bson makeQueryRelationPair(String parent, String child) {
		if (_thesname != null) {
			// 名前付きシソーラス定義の要素
			return Filters.and(Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_NAME, _thesname),
							Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_PARENT, parent),
							Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_CHILD, child));
		} else {
			// 単独のオブジェクト
			return Filters.and(Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_PARENT, parent),
								Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_CHILD, child));
		}
	}
	
	/**
	 * このオブジェクトが名前付きシソーラス定義の要素であれば、シソーラス定義名のみを含むドキュメントを生成する。
	 * @return	シソーラス定義名を含むドキュメント、名前付きシソーラス定義の要素でない場合は空のドキュメント
	 */
	protected Document makeThesNameDocument() {
		Document data = new Document();
		if (_thesname != null) {
			data.append(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_NAME, _thesname);
		}
		return data;
	}

	/**
	 * このオブジェクトに含まれるドキュメントから、指定されたクエリで検索した結果を返す。
	 * @param filter			検索条件、<tt>null</tt> の場合はすべてのドキュメント
	 * @param withoutObjectID	検索結果から MongoDB の ObjectID を除外する場合は <tt>true</tt>
	 * @param withoutThesName	検索結果からシソーラス定義名を除外する場合は <tt>true</tt>
	 * @return	検索結果のイテレート可能オブジェクト
	 * @throws MongoAlgeError	データベースに関する処理に失敗した場合
	 */
	protected FindIterable<? extends Document> queryDocuments(Bson filter, boolean withoutObjectID, boolean withoutThesName) {
		try {
			FindIterable<? extends Document> result;
			if (filter == null)
				result = _mongo_col.find(makeQueryAll());
			else
				result = _mongo_col.find(filter);
			if (withoutObjectID)
				result = result.projection(MongoUtil.MONGO_FILTER_WITHOUT_OBJID);
			if (_thesname != null && withoutThesName)
				result = result.projection(new Document(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_NAME, 0));
			return result;
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Faild to query documents by specified filter from MongoDtStringThesaurus" + _mongo_col.getNamespace().toString() + ":", ex);
		}
	}

	/**
	 * このオブジェクトに含まれるすべての要素を検索した結果を返す。
	 * クエリ生成においては、名前付きシソーラス定義の要素かどうかも考慮される。
	 * @param withoutObjectID	検索結果から MongoDB の ObjectID を除外する場合は <tt>true</tt>
	 * @param withoutThesName	検索結果からシソーラス定義名を除外する場合は <tt>true</tt>
	 * @return	検索結果のイテレート可能オブジェクト
	 * @throws MongoAlgeError	データベースに関する処理に失敗した場合
	 */
	protected FindIterable<? extends Document> findAllDocuments(boolean withoutObjectID, boolean withoutThesName) {
		return queryDocuments(null, withoutObjectID, withoutThesName);
	}

	/**
	 * このオブジェクトが保持するコレクションから、すべての値でソートされたイテレート可能オブジェクトを取得する。
	 * このオブジェクトがデータ代数集合の要素の場合は、データ代数元 ID で抽出された結果のソート済みイテレート可能オブジェクトとなる。
	 * @param withoutObjectID	検索結果から MongoDB の ObjectID を除外する場合は <tt>true</tt>
	 * @param withoutThesName	検索結果からシソーラス定義名を除外する場合は <tt>true</tt>
	 * @return	イテレート可能オブジェクト
	 * @throws MongoAlgeError	データベースに関する処理に失敗した場合
	 */
	protected FindIterable<? extends Document> sortedDocuments(boolean withoutObjectID, boolean withoutThesName) {
		try {
			FindIterable<? extends Document> result = _mongo_col.find(makeThesNameDocument()).sort(MongoDelegateDocDtStringThesaurusElem.MONGO_SORT_STRINGTHES_ELEM_ALL);
			if (withoutObjectID)
				result = result.projection(MongoUtil.MONGO_FILTER_WITHOUT_OBJID);
			if (_thesname != null && withoutThesName)
				result = result.projection(new Document(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_NAME, 0));
			return result;
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Faild to sort by all Dtalge fields in MongoDtStringThesaurus" + _mongo_col.getNamespace().toString() + ":", ex);
		}
	}

	//------------------------------------------------------------
	// I/O
	//------------------------------------------------------------

	/**
	 * シソーラスの内容を、指定のファイルに CSV フォーマットで出力する。
	 * 
	 * @param csvFile 出力先ファイル
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 * 
	 */
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
	 * シソーラスの内容を、指定された文字セットで指定のファイルに CSV フォーマットで出力する。
	 * 
	 * @param csvFile 出力先ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 * 
	 */
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
	 * @return	このシソーラス定義が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
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
	 * @return	このシソーラス定義が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
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
			return readFromCSV(reader);
		}
		finally {
			reader.close();
		}
	}
	
	/**
	 * CSV フォーマットのファイルを読み込み、新しいシソーラスを生成する。
	 * 
	 * @param session	MongoDB セッションオブジェクト
	 * @param csvFile 読み込む CSV ファイル
	 * 
	 * @return ファイルの内容で生成された、新しい <code>MongoDtStringThesaurus</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException シソーラス定義のデータが正しくない場合にスローされる
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	static public MongoDtStringThesaurus fromCSV(MongoSession session, File csvFile)
		throws IOException, FileNotFoundException, CsvFormatException
	{
		MongoDtStringThesaurus newThes = new MongoDtStringThesaurus(session);
		newThes.addAllFromCSV(csvFile);
		return newThes;
	}
	
	/**
	 * 指定された文字セットで CSV フォーマットのファイルを読み込み、新しいシソーラスを生成する。
	 * 
	 * @param session	MongoDB セッションオブジェクト
	 * @param csvFile 読み込む CSV ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @return ファイルの内容で生成された、新しい <code>MongoDtStringThesaurus</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException シソーラス定義のデータが正しくない場合にスローされる
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	static public MongoDtStringThesaurus fromCSV(MongoSession session, File csvFile, String charsetName)
		throws IOException, FileNotFoundException, CsvFormatException, UnsupportedEncodingException
	{
		MongoDtStringThesaurus newThes = new MongoDtStringThesaurus(session);
		newThes.addAllFromCSV(csvFile, charsetName);
		return newThes;
	}

	//------------------------------------------------------------
	// Internal methods for I/O
	//------------------------------------------------------------

	// シソーラス定義をCSVフォーマットで出力する
	protected void writeToCSV(CsvWriter writer)
		throws IOException
	{
		// キーワード出力
		writer.writeLine(DtStringThesaurus.CSV_KEYWORD);

		// データ出力
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = sortedDocuments(true, true).iterator();
			while (cursor.hasNext()) {
				Document docElem = cursor.next();
				writer.writeField(MongoDelegateDocDtStringThesaurusElem.getThesaurusParentWordFromDocument(docElem));
				writer.writeField(MongoDelegateDocDtStringThesaurusElem.getThesaurusChildWordFromDocument(docElem));
				writer.newLine();
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Faild to write to CSV file from MongoDtStringThesaurus" + _mongo_col.getNamespace().toString() + ":", ex);
		}
		writer.flush();
	}

	// CSVからシソーラス定義を読み込む
	protected boolean readFromCSV(CsvReader reader)
		throws IOException, CsvFormatException
	{
		CsvReader.CsvRecord record;
		
		// 先頭１行目のキーワードをチェック
		record = reader.readRecord();
		if (record == null || !record.hasFields()) {
			// undefined DtStringThesaurus CSV ID
			throw new CsvFormatException("DtStringThesaurus file ID not found.");
		} else {
			CsvReader.CsvField field = record.getField(0);
			if (field == null || !DtStringThesaurus.CSV_KEYWORD.equals(field.getValue())) {
				throw new CsvFormatException("Illegal DtStringThesaurus file ID.", record.getLineNo(), 1);
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
			
			// read parent column
			String parent = freader.readValue();
			if (Strings.isNullOrEmpty(parent)) {
				throw new CsvFormatException("Parent word cannot be omitted.",
												freader.getLineNo(), freader.getNextPosition());
			}
			
			// read child column
			String child = freader.readValue();
			if (Strings.isNullOrEmpty(child)) {
				throw new CsvFormatException("Child word cannot be omitted.",
												freader.getLineNo(), freader.getNextPosition());
			}
			
			// put in thesaurus
			try {
				if (put(parent, child)) {
					modified = true;
				}
			}
			catch (IllegalArgumentException ex) {
				throw new CsvFormatException(ex.getMessage(), freader.getLineNo(), -1);
			}
		}
		return modified;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	protected class MongoDtStringThesaurusElemIterator implements BigIterator<MongoDtStringThesaurusElement> {
		MongoCursor<? extends Document>	_itCursor;
		long _expectedModCount;
		protected Object _lastObjectId;
		
		MongoDtStringThesaurusElemIterator(MongoCursor<? extends Document> cursor) {
			_itCursor = cursor;
			_expectedModCount = _modCount.get();
		}

		@Override
		public boolean hasNext() {
			try {
				return (_itCursor==null ? false : _itCursor.hasNext());
			} catch (MongoException ex) {
				throw new MongoAlgeError("MongoDtStringThesaurus's iterator couldn't check next element in MongoDtStringThesaurus" + _mongo_col.getNamespace().toString() + ":", ex);
			}
		}
		
		@Override
		public MongoDtStringThesaurusElement next() {
			if (_expectedModCount != _modCount.get())
				throw new ConcurrentModificationException();
			if (_itCursor == null)
				throw new NoSuchElementException("MongoDtStringThesaurus's cursor was closed.");
			try {
				Document docElem = _itCursor.next();
				_lastObjectId = docElem.get("_id");
				MongoDtStringThesaurusElement elem = new MongoDtStringThesaurusElement(
						MongoDelegateDocDtStringThesaurusElem.getThesaurusParentWordFromDocument(docElem),
						MongoDelegateDocDtStringThesaurusElem.getThesaurusChildWordFromDocument(docElem)
				);
				if (!_itCursor.hasNext()) {
					closeCursor();
				}
				return elem;
			} catch (MongoException ex) {
				throw new MongoAlgeError("MongoDtStringThesaurus's iterator couldn't get next element in MongoDtStringThesaurus" + _mongo_col.getNamespace().toString() + ":", ex);
			}
		}

		@Override
		public void remove() {
			if (_thesname != null) {
				// view object
				throw new UnsupportedOperationException("Unsupported \"remove\" operation, because this object is view of MongoDtNamedStringThesaurus!");
			}
			if (_expectedModCount != _modCount.get())
				throw new ConcurrentModificationException();
			try {
				if (_lastObjectId != null) {
					_mongo_col.deleteOne(new Document("_id", _lastObjectId));
					_lastObjectId = null;
					_expectedModCount = _modCount.incrementAndGet();
				}
			} catch (MongoException ex) {
				throw new MongoAlgeError("MongoDtStringThesaurus's iterator couldn't remove element in MongoDtStringThesaurus" + _mongo_col.getNamespace().toString() + ":", ex);
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
