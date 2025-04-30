/*
 * @(#)MongoDtNamedStringThesaurusMap.java	0.5.0	2019/02/25
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.db.mongo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.LinkedHashSet;
import java.util.List;
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
import com.mongodb.client.model.GraphLookupOptions;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.DeleteResult;

import dtalge.DtStringThesaurus;
import dtalge.db.BigDtNamedStringThesaurus;
import dtalge.db.BigDtNamedStringThesaurusInnerElement;
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
 * 大容量の名前付きシソーラス定義を保持するクラス。
 * <p>ストレージとして MongoDB を利用する。
 * <p>名前に対応する要素は、データ代数のシソーラス定義(<code>MongoDtStringThesaurus</code>)として利用できる。
 * このオブジェクトは、シソーラス名をキー、シソーラス定義オブジェクト(<code>MongoDtStringThesaurus</code>)を値と
 * するマップであり、シソーラス名に関連付けられるシソーラス定義オブジェクトは一つである。
 * このオブジェクトでは、名前のないシソーラス定義も保持することができ、無名シソーラス定義として
 * 一つだけ保持することができる。
 * 
 * <p><b>この実装は同期化されない</b>。
 * <p>
 * <b>《入出力フォーマット》</b>
 * <br>
 * <code>MongoDtNamedStringThesaurusMap</code> の MongoDB におけるデータ構造は、次の通りである。
 * <pre><code>
 * {
 *   &quot;name&quot; : &lt;シソーラス名(シソーラス定義ID)&gt;
 *   &quot;node&quot; : &lt;シソーラス語句&gt;
 *   &quot;children&quot; : &lt;子のシソーラス語句の配列;&gt;
 * }
 * </code></pre>
 * シソーラス名(シソーラス定義ID) が同一のドキュメントは、同じシソーラス定義に所属していることを示す。
 * 
 * @version 0.5.0
 * @since 0.5.0
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public class MongoDtNamedStringThesaurus implements BigDtNamedStringThesaurus<MongoDtStringThesaurus>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	/** 無名のシソーラス定義を表す、シソーラス名(シソーラス定義 ID) **/
	static public final String NoNameThesKey = MongoDelegateDocDtStringThesaurusElem.NoNameThesKey;
	
	/** 一時的なコレクション名(値のコレクション)のプレフィックス **/
	static public final String	TEMP_VALS_COLNAME_PREFIX	= "TDtNST";
	
	static protected final String	MONGO_KEY_ELEM_COUNT	= "elemCount";
	
	static protected final List<Bson>	MONGO_AGGRE_ELEM_COUNT;
	
	static protected final List<Bson>	MONGO_AGGRE_ELEM_NA_ITERABLE;
	
	static protected final List<Bson>	MONGO_AGGRE_ELEM_SORTED_ITERABLE;
	
	static {
		//--- シソーラス定義名の総数をカウントするパイプライン
		MONGO_AGGRE_ELEM_COUNT = new ArrayList<Bson>();
		MONGO_AGGRE_ELEM_COUNT.add(Aggregates.group("$" + MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_NAME));
		MONGO_AGGRE_ELEM_COUNT.add(Aggregates.count(MONGO_KEY_ELEM_COUNT));
		//--- シソーラス定義名のみを保持するデータベース依存順序のビューを生成するパイプライン
		MONGO_AGGRE_ELEM_NA_ITERABLE = new ArrayList<Bson>();
		MONGO_AGGRE_ELEM_NA_ITERABLE.add(Aggregates.group("$" + MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_NAME));
		MONGO_AGGRE_ELEM_NA_ITERABLE.add(Aggregates.project(new Document(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_NAME, "$_id").append("_id", 0)));
		//--- シソーラス定義名のみを保持する昇順ソートされたビューを生成するパイプライン
		MONGO_AGGRE_ELEM_SORTED_ITERABLE = new ArrayList<Bson>();
		MONGO_AGGRE_ELEM_SORTED_ITERABLE.add(Aggregates.group("$" + MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_NAME));
		MONGO_AGGRE_ELEM_SORTED_ITERABLE.add(Aggregates.project(new Document(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_NAME, "$_id").append("_id", 0)));
		MONGO_AGGRE_ELEM_SORTED_ITERABLE.add(Aggregates.sort(Sorts.ascending(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_NAME)));
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
	public MongoDtNamedStringThesaurus(MongoSession session) {
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
	public MongoDtNamedStringThesaurus(MongoSession session, String collection) {
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
			throw new MongoAlgeError("Failed to persist of MongoDtNamedStringThesaurus" + _mongo_vals_col.getNamespace().toString() + ":", ex);
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
	public void delete() {
		try {
			_mongo_vals_col.drop();
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to drop collection of MongoDtNamedStringThesaurus" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		//_mongo_session.unregisterTemporaryCollection(_mongo_vals_col.getNamespace());
	}

	/**
	 * このマップから、すべての要素を削除する。
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public void clear() {
		MongoUtil.clearAllDocuments(_mongo_vals_col);
		_modCount.incrementAndGet();
	}

	/**
	 * シソーラス定義が一つも存在しないなら <tt>true</tt> を返す。
	 * 
	 * @return	シソーラス定義が一つも存在しない場合は <tt>true</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public boolean isEmpty() {
		return (size() == 0L);
	}
	
	/**
	 * このオブジェクトに格納されているシソーラス定義数を返す。
	 * このオブジェクトが返す値は、シソーラス定義名の総数となる。
	 * 
	 * @return シソーラス定義数
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
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
			throw new MongoAlgeError("Failed to count MongoDtStringThesaurus elements in MongoDtNamedStringThesaurus" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
	}
	
	/**
	 * このオブジェクトに格納されているシソーラス定義の関係数を返す。
	 * このオブジェクトが返す値は、シソーラス定義に含まれる親子関係数の合計値となる。
	 * @return	全シソーラス定義の親子関係数の合計
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public long getNumRelations() {
		try {
			return _mongo_vals_col.countDocuments();
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to count total relations in MongoDtNamedStringThesaurus" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
	}

	/**
	 * 指定されたシソーラス名を保持している場合に <tt>true</tt> を返す。
	 * シソーラス名に <tt>null</tt> もしくは空文字列を指定した場合、
	 * 無名シソーラスを保持しているかどうかを判定する。
	 * @param name	シソーラス名
	 * @return	指定されたシソーラス名を保持している場合は <tt>true</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public boolean containsName(String name) {
		try {
			return (_mongo_vals_col.countDocuments(makeQueryByName(MongoDelegateDocDtStringThesaurusElem.normalizeThesName(name))) > 0L);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to contain name in MongoDtNamedStringThesaurus" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
	}

	/*
	 * 指定されたシソーラスと等しいシソーラスを保持している場合に <tt>true</tt> を返す。
	 * @param thes	判定するシソーラス
	 * @return	指定されたシソーラスを保持している場合は <tt>true</tt>
	 */
	//public boolean containsThesaurus(dtalge.DtStringThesaurus thes) {
	//	return _thesmap.containsValue(thes);
	//}

	/**
	 * 指定されたシソーラス名に対応するシソーラスを取得する。
	 * シソーラス名に <tt>null</tt> もしくは空文字列を指定した場合、
	 * 無名シソーラスを返す。
	 * @param name	シソーラス名
	 * @return	シソーラス名に対応するシソーラスを返す。
	 * 			シソーラス名に対応するシソーラスが存在しない場合は <tt>null</tt> を返す。
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public MongoDtStringThesaurus getThesaurus(String name) {
		String normalizedName = MongoDelegateDocDtStringThesaurusElem.normalizeThesName(name);
		try {
			if (_mongo_vals_col.countDocuments(makeQueryByName(normalizedName)) > 0L) {
				// exists
				return new MongoDtStringThesaurus(this, normalizedName);
			}
			else {
				// not found
				return null;
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get MongoDtStringThesaurus element from MongoDtNamedStringThesaurus" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
	}

	/**
	 * 指定されたシソーラスのシソーラス名を返す。
	 * 指定されたシソーラスと等しいシソーラスを複数保持している場合は、
	 * 最初に見つかったシソーラスのシソーラス名を返す。
	 * @param thes	検索対象のシソーラス
	 * @return	指定のシソーラスと等しいシソーラスのうち、最初に見つかった
	 * 			シソーラスのシソーラス名を返す。
	 * 			無名シソーラスの場合は空文字列を返す。
	 * 			見つからなかった場合は <tt>null</tt> を返す。
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public String nameOf(DtStringThesaurus thes) {
		if (!isEmpty() && thes != null && !thes.isEmpty()) {
			MongoCursor<String> cursor = null;
			try {
				cursor = distinctByName().iterator();
				while (cursor.hasNext()) {
					String name = cursor.next();
					MongoDtStringThesaurus thesview = new MongoDtStringThesaurus(this, name);
					if (thesview.isSameRelations(thes)) {
						return name;
					}
				}
			}
			catch (MongoException ex) {
				throw new MongoAlgeError("Failed to query by DtStringThesaurus from MongoDtNamedStringThesaurus" + _mongo_vals_col.getNamespace().toString() + ":", ex);
			}
			finally {
				MongoUtil.closeCursorSilent(cursor);
			}
		}
		// not found
		return null;
	}

	/**
	 * 指定されたシソーラスに対応するすべてのシソーラス名を取得する。
	 * 無名シソーラスの場合、シソーラス名には空文字列が格納される。
	 * @param thes	検索対象のシソーラス
	 * @return	指定されたシソーラスに対応するシソーラス名をすべて格納するリストを返す。
	 * 			指定されたシソーラスを保持していない場合は、要素が空のリストを返す。
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public List<String> findAllNamesOf(DtStringThesaurus thes) {
		List<String> result = Collections.emptyList();
		if (!isEmpty() && thes != null && !thes.isEmpty()) {
			ArrayList<String> namelist = new ArrayList<>();
			MongoCursor<String> cursor = null;
			try {
				cursor = distinctByName().iterator();
				while (cursor.hasNext()) {
					String name = cursor.next();
					MongoDtStringThesaurus thesview = new MongoDtStringThesaurus(this, name);
					if (thesview.isSameRelations(thes)) {
						namelist.add(name);
					}
				}
			}
			catch (MongoException ex) {
				throw new MongoAlgeError("Failed to query by DtStringThesaurus from MongoDtNamedStringThesaurus" + _mongo_vals_col.getNamespace().toString() + ":", ex);
			}
			finally {
				MongoUtil.closeCursorSilent(cursor);
			}
			if (!namelist.isEmpty()) {
				result = namelist;
			}
		}
		return result;
	}

	/**
	 * このオブジェクトが保持しているすべてのシソーラス名を取得する。
	 * @return	すべてのシソーラス名を格納するリストを返す。
	 * 			シソーラスが一つも存在しない場合は、要素が空のリストを返す。
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public List<String> getAllThesaurusNames() {
		if (isEmpty()) {
			return Collections.emptyList();
		}
		
		ArrayList<String> namelist = new ArrayList<>();
		MongoCursor<String> cursor = null;
		try {
			cursor = distinctByName().iterator();
			while (cursor.hasNext()) {
				namelist.add(cursor.next());
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get thesaurus names from MongoDtNamedStringThesaurus" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		return namelist;
	}

	/**
	 * このオブジェクトの複製を生成する。
	 * 
	 * @return 実体が複製されたオブジェクトの新しいインスタンス
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	@Override
	public MongoDtNamedStringThesaurus copy() {
		MongoDtNamedStringThesaurus newThes = new MongoDtNamedStringThesaurus(_mongo_session);
		MongoUtil.duplicateCollection(newThes._mongo_vals_col, _mongo_vals_col);
		return newThes;
	}
	
	/**
	 * 指定されたシソーラス名で、語句の関係を登録する。
	 * <p>
	 * このメソッドは、2 つの語句の親子関係(大小関係)をシソーラス名に対応するシソーラスへ登録する。
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
	 * @param name		シソーラス名
	 * @param parent	親として登録する語句
	 * @param child		子として登録する語句
	 * @return			新しい関係が登録された場合 <tt>true</tt>
	 * 
	 * @throws IllegalArgumentException	指定された語句もしくは語句の関係が適切ではない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public boolean put(String name, String parent, String child) {
		// check
		Validations.validArgument(!Strings.isNullOrEmpty(parent), "Illegal parent : %s", String.valueOf(parent));
		Validations.validArgument(!Strings.isNullOrEmpty(child), "Illegal child : %s", String.valueOf(child));
		Validations.validArgument(!parent.equals(child), "Parent word is same as child word : %s", String.valueOf(parent));
		String normalizedName = MongoDelegateDocDtStringThesaurusElem.normalizeThesName(name);
		
		// 登録済みの関係かチェックする
		if (existsPair(normalizedName, parent, child)) {
			// already exist relation
			return false;
		}
		
		// 循環参照のチェック
		if (hasRelation(normalizedName, parent, child)) {
			// parent の親が child として登録済みの場合、この関係は循環参照となる。
			throw new IllegalArgumentException(
					String.format("Illegal [\"%s\" < \"%s\"] relation for \"" + normalizedName + "\", because already exist [\"%s\" > \"%s\"] relation.",
							child, parent, child, parent));
		}
		
		// 登録
		try {
			Document docElem = MongoDelegateDocDtStringThesaurusElem.makeNamedThesaurusElemDocument(normalizedName, parent, child);
			_mongo_vals_col.insertOne(docElem);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to insert relation for \"" + normalizedName + "\" into MongoDtNamedStringThesaurus" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		
		// 登録完了
		return true;
	}

	/**
	 * 指定されたシソーラス名で、指定されたシソーラスを登録する。
	 * シソーラス名が <tt>null</tt> もしくは空文字列の場合は、無名シソーラスとして登録する。
	 * 指定したシソーラス名がすでに登録されている場合、指定されたシソーラスで置き換える。
	 * @param name	シソーラス名
	 * @param thes	シソーラス
	 * @return	このオブジェクトのシソーラス定義が変更された場合に <tt>true</tt> を返す
	 * @throws	NullPointerException	<em>thes</em> が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public boolean put(String name, DtStringThesaurus thes) {
		if (thes == null)
			throw new NullPointerException("Thesaurus is null.");
		if (thes.isEmpty())
			return false;

		String normalizedName = MongoDelegateDocDtStringThesaurusElem.normalizeThesName(name);
		boolean modified = false;
		for (String child : thes.getAllThesaurusChildren()) {
			List<String> parents = thes.getThesaurusParents(child);
			for (String parent : parents) {
				if (put(normalizedName, parent, child)) {
					modified = true;
				}
			}
		}
		return modified;
	}

	/**
	 * 指定されたシソーラス名で、指定されたシソーラスを登録する。
	 * シソーラス名が <tt>null</tt> もしくは空文字列の場合は、無名シソーラスとして登録する。
	 * 指定したシソーラス名がすでに登録されている場合、指定されたシソーラスで置き換える。
	 * @param name	シソーラス名
	 * @param thes	シソーラス
	 * @return	このオブジェクトのシソーラス定義が変更された場合に <tt>true</tt> を返す
	 * @throws	NullPointerException	<em>thes</em> が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public boolean put(String name, BigDtStringThesaurus<? extends BigDtStringThesaurusElement> thes) {
		if (thes == null)
			throw new NullPointerException("Thesaurus is null.");
		if (thes.isEmpty())
			return false;

		String normalizedName = MongoDelegateDocDtStringThesaurusElem.normalizeThesName(name);
		boolean modified = false;
		BigIterator<? extends BigDtStringThesaurusElement> it = thes.iterator();
		try {
			while (it.hasNext()) {
				BigDtStringThesaurusElement elem = it.next();
				if (put(normalizedName, elem.getParentWord(), elem.getChildWord())) {
					modified = true;
				}
			}
		}
		finally {
			it.closeCursor();
		}
		return modified;
	}

	/**
	 * 指定されたシソーラス定義から、シソーラス名とシソーラスとの対応をすべてコピーする。
	 * シソーラス名とシソーラスとの対応は、指定された対応で置き換えられる。
	 * @param namedthes	コピーするシソーラス名とシソーラスとの対応
	 * @return	このオブジェクトのシソーラス定義が変更された場合に <tt>true</tt> を返す
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public boolean putAll(BigDtNamedStringThesaurus<? extends BigDtStringThesaurus<? extends BigDtStringThesaurusElement>> namedthes) {
		if (namedthes == null)
			throw new NullPointerException("Named thesaurus is null.");
		if (namedthes.isEmpty())
			return false;

		boolean modified = false;
		BigIterator<? extends BigDtNamedStringThesaurusInnerElement> it = namedthes.innerElementIterator();
		try {
			while (it.hasNext()) {
				BigDtNamedStringThesaurusInnerElement elem = it.next();
				if (put(elem.getName(), elem.getParentWord(), elem.getChildWord())) {
					modified = true;
				}
			}
		}
		finally {
			it.closeCursor();
		}
		return modified;
	}

	/**
	 * 指定されたシソーラス定義から、指定された親子関係をシソーラスから除去する。
	 * 
	 * @param name		シソーラス定義名
	 * @param parent	除去する関係の親となる語句
	 * @param child		除去する関係の子となる語句
	 * @return	関係が除去された場合は <tt>true</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public boolean remove(String name, String parent, String child) {
		try {
			DeleteResult ret = _mongo_vals_col.deleteMany(
									makeQueryRelationPair(MongoDelegateDocDtStringThesaurusElem.normalizeThesName(name),
									parent, child));
			return (ret.getDeletedCount() > 0L);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to delete relation from MongoDtNamedStringThesaurus" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
	}
	
	/**
	 * 指定されたシソーラス定義を除去する。
	 * @param name	除去するシソーラス名
	 * @return	シソーラス定義が除去された場合は <tt>true</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public boolean removeThesaurus(String name) {
		try {
			DeleteResult ret = _mongo_vals_col.deleteMany(makeQueryByName(MongoDelegateDocDtStringThesaurusElem.normalizeThesName(name)));
			return (ret.getDeletedCount() > 0L);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to delete thesaurus from MongoDtNamedStringThesaurus" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
	}

//	/**
//	 * 指定されたシソーラス名に関連付けられているシソーラスの全ての語句を取得する。
//	 * このメソッドが返すセット要素の順序は、指定された条件でソートされたものとなる。
//	 * @param name					シソーラス名
//	 * @param relationAscending		シソーラス定義における小さい順にソートする場合は <tt>true</tt>、
//	 * 								大きい順にソートする場合は <tt>false</tt>
//	 * @param wordAscending			語句が比較不能もしくは等しいとき、語句の辞書的に小さい順にソートする場合は <tt>true</tt>、
//	 * 								辞書的に大きい順にソートする場合は <tt>false</tt>
//	 * @return	シソーラス名に対応するシソーラスが存在する場合は、そのシソーラスに含まれる全ての語句のセットを返す。
//	 * 			存在しない場合は <tt>null</tt> を返す。
//	 */
//	public Set<String> getSortedAllWordsByName(String name, boolean relationAscending, boolean wordAscending) {
//		dtalge.DtStringThesaurus thes = _thesmap.get(normalizedKey(name));
//		if (thes != null) {
//			DelegateDtStringThesaurus ddt = new DelegateDtStringThesaurus(thes);
//			return ddt.getSortedWords(relationAscending, wordAscending);
//		} else {
//			return null;
//		}
//	}
	
	/**
	 * 指定されたシソーラス名に関連付けられているシソーラスの全ての語句を取得する。
	 * @param name	シソーラス名
	 * @return	シソーラス名に対応するシソーラスが存在する場合は、そのシソーラスに含まれる全ての語句のリストを返す。
	 * 			存在しない場合は <tt>null</tt> を返す。
	 */
	public List<String> getAllThesaurusWordsByName(String name) {
		MongoDtStringThesaurus thes = getThesaurus(name);
		if (thes != null) {
			LinkedHashSet<String> wordset = new LinkedHashSet<String>();
			BigIterator<MongoDtStringThesaurusElement> it = thes.iterator();
			try {
				while (it.hasNext()) {
					MongoDtStringThesaurusElement elem = it.next();
					wordset.add(elem.getParentWord());
					wordset.add(elem.getChildWord());
				}
			}
			finally {
				it.closeCursor();
			}
			return new ArrayList<String>(wordset);
		} else {
			return null;
		}
	}
	
//	/**
//	 * 指定されたシソーラス名に関連付けられているシソーラスの全ての語句を取得する。
//	 * このメソッドが返すリスト要素の順序は、指定された条件でソートされたものとなる。
//	 * @param name					シソーラス名
//	 * @param relationAscending		シソーラス定義における小さい順にソートする場合は <tt>true</tt>、
//	 * 								大きい順にソートする場合は <tt>false</tt>
//	 * @param wordAscending			語句が比較不能もしくは等しいとき、語句の辞書的に小さい順にソートする場合は <tt>true</tt>、
//	 * 								辞書的に大きい順にソートする場合は <tt>false</tt>
//	 * @return	シソーラス名に対応するシソーラスが存在する場合は、そのシソーラスに含まれる全ての語句のリストを返す。
//	 * 			存在しない場合は <tt>null</tt> を返す。
//	 */
//	public List<String> getSortedAllThesaurusWordsByName(String name, boolean relationAscending, boolean wordAscending) {
//		dtalge.DtStringThesaurus thes = _thesmap.get(normalizedKey(name));
//		if (thes != null) {
//			DelegateDtStringThesaurus ddt = new DelegateDtStringThesaurus(thes);
//			return new ArrayList<String>(ddt.getSortedWords(relationAscending, wordAscending));
//		} else {
//			return null;
//		}
//	}

	/**
	 * このオブジェクトのハッシュコード値を返す。
	 * @return	ハッシュコード値
	 */
	@Override
	public int hashCode() {
		int h = 0;
		
		MongoCursor<? extends Document> cursor = sortedDocuments(true, false).iterator();
		try {
			while (cursor.hasNext()) {
				Document thisDoc   = cursor.next();
				String thisName   = MongoDelegateDocDtStringThesaurusElem.getThesaurusNameFromDocument(thisDoc);
				String thisParent = MongoDelegateDocDtStringThesaurusElem.getThesaurusParentWordFromDocument(thisDoc);
				String thisChild  = MongoDelegateDocDtStringThesaurusElem.getThesaurusChildWordFromDocument(thisDoc);
				h += ((thisName==null ? 0 : thisName.hashCode()) ^ (thisParent==null ? 0 : thisParent.hashCode()) ^ (thisChild==null ? 0 : thisChild.hashCode()));
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to calc hash code of MongoDtNamedStringThesaurus" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		
		return h;
	}

	/**
	 * 指定されたオブジェクトとこのオブジェクトが等しいかどうかを比較する。
	 * 指定されたオブジェクトが名前付きシソーラス定義であり、2 つのオブジェクトが同じ対応関係を表す場合に <tt>true</tt> を返す。
	 * @param obj	このオブジェクトと等しいかどうかを比較するオブジェクト
	 * @return	指定されたオブジェクトがこのオブジェクトと等しい場合は <tt>true</tt>
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		
		if (!(obj instanceof MongoDtNamedStringThesaurus)) {
			return false;
		}
		
		MongoDtNamedStringThesaurus that = (MongoDtNamedStringThesaurus)obj;
		return equalsAnotherNamedStringThesaurusCollection(that._mongo_vals_col);
	}

	/**
	 * このシソーラスの文字列表現を返します。
	 * 大容量の場合、10,000 要素まで出力します。
	 * <p>
	 * 文字列表現は、エントリ(親と子の関係定義)の文字列表現を中括弧 (<tt>"{}"</tt>) で囲んで示すリストとなる。
	 * エントリの文字列表現は、1 つの親と 1 つ以上の子で構成され、次のように表される。
	 * <blockquote>
	 * <b>[</b><i>シソーラス定義名</i><b>]=</b><i>親</i><b>-&gt;</b><i>子</i>
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
			cursor = sortedDocuments(true, false).iterator();
			if (cursor.hasNext()) {
				//--- first
				docElem = cursor.next();
				sb.append('[');
				sb.append(MongoDelegateDocDtStringThesaurusElem.getThesaurusNameFromDocument(docElem));
				sb.append("]=");
				sb.append(MongoDelegateDocDtStringThesaurusElem.getThesaurusParentWordFromDocument(docElem));
				sb.append("->");
				sb.append(MongoDelegateDocDtStringThesaurusElem.getThesaurusChildWordFromDocument(docElem));
				++cnt;
			}
			for (; cursor.hasNext() && cnt < 10000L; cnt++) {
				docElem = cursor.next();
				sb.append(", ");
				sb.append('[');
				sb.append(MongoDelegateDocDtStringThesaurusElem.getThesaurusNameFromDocument(docElem));
				sb.append("]=");
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
			throw new MongoAlgeError("Failed to convert to string from MongoDtNamedStringThesaurus" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
		finally {
			MongoUtil.closeCursorSilent(cursor);
		}
		sb.append("}");
		return sb.toString();
	}
	
	/**
	 * このオブジェクトに含まれる最小要素にアクセスするイテレーターを取得する。
	 * 要素が返されるときの順序は、このクラスのコレクションの実装に依存する。
	 * @return	このオブジェクトに含まれる一つの親子関係を表す最小要素のイテレーター
	 */
	public BigIterator<BigDtNamedStringThesaurusInnerElement> innerElementIterator() {
		return newUnmodifiableNaturalOrderedDocumentIterator();
	}
	
	/**
	 * このオブジェクトに含まれる最小要素にアクセスするイテレーターを取得する。
	 * 要素が返されるときの順序は、シソーラス定義名、親子関係の親語句、親子関係の子語句の順に辞書順ソートされた順序となる。
	 * @return	このオブジェクトに含まれる一つの親子関係を表す最小要素のイテレーター
	 */
	public BigIterator<BigDtNamedStringThesaurusInnerElement> sortedInnerElementIterator() {
		return newUnmodifiableSortedDocumentIterator();
	}
	
	/**
	 * 名前付きシソーラス定義のシソーラス定義ごとのイテレーターを返す。
	 * 要素が返されるときの順序は、このクラスのコレクションの実装に依存する。
	 * @return	シソーラス定義のイテレーターオブジェクト
	 * @see ConcurrentModificationException
	 */
	public BigIterator<MongoDtStringThesaurus> iterator() {
		return newMongoDtStringThesaurusIterator();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * シソーラス定義のイテレーターを生成する。
	 * このイテレーターが返す順序は、データベースの実装に依存する。
	 * 
	 * @return <code>MongoDtStringThesaurus</code> の <code>Iterator</code>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	protected BigIterator<MongoDtStringThesaurus> newMongoDtStringThesaurusIterator() {
		// TODO: カーソルのクローズ処理を今後考慮
		try {
			//--- ソート順とする
			//MongoCursor<? extends Document> cursor = _mongo_vals_col.aggregate(MONGO_AGGRE_ELEM_SORTED_ITERABLE).iterator();
			//--- データベースの順序とする
			MongoCursor<? extends Document> cursor = _mongo_vals_col.aggregate(MONGO_AGGRE_ELEM_NA_ITERABLE).iterator();
			return new MongoDtStringThesaurusIterator(cursor);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get MongDtStringThesaurus iteration cursor from MongoDtNamedStringThesaurus" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
	}
	
	protected BigIterator<BigDtNamedStringThesaurusInnerElement> newUnmodifiableNaturalOrderedDocumentIterator() {
		// TODO: カーソルのクローズ処理を今後考慮
		try {
			// (注意) 削除を可能とするため、"_id" も検索結果に含める
			MongoCursor<? extends Document> cursor = _mongo_vals_col.find().iterator();
			return new MongoDtNamedStringThesaurusDocumentIterator(cursor);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get natural ordered document cursor from MongoDtNamedStringThesaurus" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
	}
	
	protected BigIterator<BigDtNamedStringThesaurusInnerElement> newUnmodifiableSortedDocumentIterator() {
		// TODO: カーソルのクローズ処理を今後考慮
		try {
			//--- ソート済みイテレータにする
			// (注意) 削除を可能とするため、"_id" も検索結果に含める
			MongoCursor<? extends Document> cursor = sortedDocuments(false, false).iterator();
			return new MongoDtNamedStringThesaurusDocumentIterator(cursor);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to get sorted document cursor from MongoDtNamedStringThesaurus" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
	}

	/**
	 * 指定された名前付きシソーラスのコレクションが、自身の内容と一致するかどうかを判定する。
	 * <p>この判定では、両コレクションをシソーラス名、親語句、子語句の順に昇順でソートし、
	 * シソーラス定義の単位でその内容を比較する。
	 * @param another	判定対象のコレクション
	 * @return	等しい場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoExalgeError	データベースの処理が正常に行えない場合
	 */
	protected boolean equalsAnotherNamedStringThesaurusCollection(MongoCollection<? extends Document> another) {
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
				cursor1 = _mongo_vals_col.find().sort(MongoDelegateDocDtStringThesaurusElem.MONGO_SORT_NAMEDSTRINGTHES_ELEM_ALL).iterator();
				cursor2 = another.find().sort(MongoDelegateDocDtStringThesaurusElem.MONGO_SORT_NAMEDSTRINGTHES_ELEM_ALL).iterator();
				String thisValue = null;
				String thatValue = null;
				while (cursor1.hasNext()) {
					Document doc1 = cursor1.next();
					Document doc2 = cursor2.next();
					//--- name
					thisValue = MongoDelegateDocDtStringThesaurusElem.getThesaurusNameFromDocument(doc1);
					thatValue = MongoDelegateDocDtStringThesaurusElem.getThesaurusNameFromDocument(doc2);
					if (!Objects.equals(thisValue, thatValue)) {
						return false;
					}
					//--- parent
					thisValue = MongoDelegateDocDtStringThesaurusElem.getThesaurusParentWordFromDocument(doc1);
					thatValue = MongoDelegateDocDtStringThesaurusElem.getThesaurusParentWordFromDocument(doc2);
					if (!Objects.equals(thisValue, thatValue)) {
						return false;
					}
					//--- child
					thisValue = MongoDelegateDocDtStringThesaurusElem.getThesaurusChildWordFromDocument(doc1);
					thatValue = MongoDelegateDocDtStringThesaurusElem.getThesaurusChildWordFromDocument(doc2);
					if (!Objects.equals(thisValue, thatValue)) {
						return false;
					}
				}
				// equal
				return true;
			}
			finally {
				MongoUtil.closeCursorSilent(cursor1);
				MongoUtil.closeCursorSilent(cursor2);
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to judge equals between " + _mongo_vals_col.getNamespace().toString() + " and " + another.getNamespace().toString() + ":", ex);
		}
	}
	
	/**
	 * 指定されたシソーラス定義において、指定された子の語句が親を持つ関係として含まれているかを判定する。
	 * @param normalizedName	正規化済みのシソーラス定義名
	 * @param child	判定する関係の子の語句
	 * @return	関係が含まれている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	protected boolean existsChildOfRelation(String normalizedName, String child) {
		try {
			return (_mongo_vals_col.countDocuments(makeQueryRelationByChild(normalizedName, child)) > 0L);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to query relations by word as child in MongoDtNamedStringThesaurus" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
	}
	
	/**
	 * 指定されたシソーラス定義において、指定された親の語句が子を持つ関係として含まれているかを判定する。
	 * @param normalizedName	正規化済みのシソーラス定義名
	 * @param parent	判定する関係の親の語句
	 * @return	関係が含まれている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	protected boolean existsParentOfRelation(String normalizedName, String parent) {
		try {
			return (_mongo_vals_col.countDocuments(makeQueryRelationByParent(normalizedName, parent)) > 0L);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to query relations by word as parent in MongoDtNamedStringThesaurus" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
	}
	
	/**
	 * 指定されたシソーラス定義において、指定された親子関係が含まれているかを判定する。
	 * @param normalizedName	正規化済みのシソーラス定義名
	 * @param parent	判定する関係の親の語句
	 * @param child	判定する関係の子の語句
	 * @return	関係が含まれている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	protected boolean existsPair(String normalizedName, String parent, String child) {
		try {
			return (_mongo_vals_col.countDocuments(makeQueryRelationPair(normalizedName, parent, child)) > 0L);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to query relations by pair in MongoDtNamedStringThesaurus" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
	}

	/**
	 * 指定されたシソーラス定義において、2 つの語句が関係を持つかを検証する。
	 * シソーラス定義において、<code>descendant</code> の先祖が
	 * <code>ancestor</code> なら、<tt>true</tt> を返す。
	 * なお、<code>descendant.equals(ancestor)</code> が <tt>true</tt> の
	 * 場合、このメソッドは <tt>false</tt> を返す。
	 * <p><b>注:</b>このメソッドでは、2 つの引数は <tt>null</tt> ではなく、
	 * 同値でもないことを前提としているため、<tt>null</tt> もしくは同値で
	 * あるかの検証は行わない。
	 * 
	 * @param normalizedName	正規化済みのシソーラス定義名
	 * @param descendant	子孫とみなす語句
	 * @param ancestor		先祖とみなす語句
	 * 
	 * @return	2 つの語句の関係が定義されている場合は <tt>true</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	protected boolean hasRelation(String normalizedName, String descendant, String ancestor) {
		// check exists child
		if (!existsChildOfRelation(normalizedName, descendant)) {
			return false;
		}
		// check exists parent
		if (!existsParentOfRelation(normalizedName, ancestor)) {
			return false;
		}
		// check exist pair
		if (existsPair(normalizedName, ancestor, descendant)) {
			//--- exist pair
			return true;
		}
		// check exists relation
		return hasRelationRecursive(normalizedName, descendant, ancestor);
	}
	
	/**
	 * 指定されたシソーラス定義において、2 つの語句が関係を持つかを検証する。
	 * このメソッドは、<em>descendant</em> の親を辿り、<em>ancestor</em> が
	 * 見つかるまで再帰的に探索する。
	 * 
	 * @param normalizedName	正規化済みのシソーラス定義名
	 * @param descendant	子孫とみなす語句
	 * @param ancestor		先祖とみなす語句
	 * 
	 * @return	2 つの語句の関係が定義されている場合は <tt>true</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	protected boolean hasRelationRecursive(String normalizedName, String descendant, String ancestor) {
		GraphLookupOptions glopts = new GraphLookupOptions();
		glopts.depthField(MongoDelegateDocDtStringThesaurusElem.MONGO_AGGRE_KEY_DEPTH);
		glopts.restrictSearchWithMatch(Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_NAME, normalizedName));
		
		ArrayList<Bson> aggre = new ArrayList<>();
		//--- matches by name & decendant
		aggre.add(Aggregates.match(Filters.and(
				Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_NAME, normalizedName),
				Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_CHILD, descendant)
			)));
		//--- graph lookup
		aggre.add(Aggregates.graphLookup(
				_mongo_vals_col.getNamespace().getCollectionName(),
				MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_$PARENT,
				MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_PARENT,
				MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_CHILD,
				MongoDelegateDocDtStringThesaurusElem.MONGO_AGGRE_KEY_GRAPH,
				glopts
		));
		//--- projection
		Document docProj = new Document();
		docProj.append(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_NAME, 1);
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
			return (_mongo_vals_col.aggregate(aggre).first() != null);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to query relations of the words in MongoDtNamedStringThesaurus" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
	}
	
	/**
	 * 指定されたシソーラス定義から、指定された関係を除去する。
	 * 
	 * @param normalizedName	正規化済みのシソーラス定義名
	 * @param parent			除去する関係の親となる語句
	 * @param child				除去する関係の子となる語句
	 * @return	関係が除去された場合は <tt>true</tt>
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	protected boolean removeRelation(String normalizedName, String parent, String child) {
		try {
			DeleteResult ret = _mongo_vals_col.deleteOne(makeQueryRelationPair(normalizedName, parent, child));
			return (ret.getDeletedCount() > 0L);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to delete relation from MongoDtNamedStringThesaurus" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
	}
	
	/**
	 * 指定されたシソーラス名でドキュメントを検索するクエリを生成する。
	 * @param normalizedName	正規化済みシソーラス名
	 * @return	フィルターオブジェクト
	 */
	protected Bson makeQueryByName(String normalizedName) {
		return Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_NAME, normalizedName);
	}
	
	/**
	 * 指定されたシソーラス名で、指定された語句を親とするドキュメントを取得するクエリを生成する。
	 * このメソッドが生成するクエリは、子の有無については関知しない。
	 * @param normalizedName	正規化済みシソーラス名
	 * @param word	親語句として検索する語句
	 * @return	フィルターオブジェクト
	 */
	protected Bson makeQueryByParent(String normalizedName, String word) {
		return Filters.and(Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_NAME, normalizedName),
				Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_PARENT, word));
	}
	
	/**
	 * 指定されたシソーラス名で、指定された語句を子とするドキュメントを取得するクエリを生成する。
	 * このメソッドが生成するクエリは、親の有無については関知しない。
	 * @param normalizedName	正規化済みシソーラス名
	 * @param word	子語句として検索する語句
	 * @return	フィルターオブジェクト
	 */
	protected Bson makeQueryByChild(String normalizedName, String word) {
		return Filters.and(Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_NAME, normalizedName),
				Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_CHILD, word));
	}
	
	/**
	 * 指定されたシソーラス名で、指定された語句を親とし、子を持つドキュメントを取得するクエリを生成する。
	 * このメソッドが生成するクエリは、子を持たない親が含まれるドキュメントは除外される。
	 * @param normalizedName	正規化済みシソーラス名
	 * @param word	親語句として検索する語句
	 * @return	フィルターオブジェクト
	 */
	protected Bson makeQueryRelationByParent(String normalizedName, String word) {
		return Filters.and(Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_NAME, normalizedName),
				Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_PARENT, word),
				Filters.ne(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_CHILD, null));
	}
	
	/**
	 * 指定されたシソーラス名で、指定された語句を親とし、子を持つドキュメントを取得するクエリを生成する。
	 * このメソッドが生成するクエリは、親を持たない親が含まれるドキュメントは除外される。
	 * @param normalizedName	正規化済みシソーラス名
	 * @param word	親語句として検索する語句
	 * @return	フィルターオブジェクト
	 */
	protected Bson makeQueryRelationByChild(String normalizedName, String word) {
		return Filters.and(Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_NAME, normalizedName),
				Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_CHILD, word),
				Filters.ne(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_PARENT, null));
	}
	
	/**
	 * 指定されたシソーラス名で、指定された語句が親もしくは子のどちらかに含まれるドキュメントを取得するクエリを生成する。
	 * @param normalizedName	正規化済みシソーラス名
	 * @param word	親または子として検索する語句
	 * @return	フィルターオブジェクト
	 */
	protected Bson makeQueryRelationByWord(String normalizedName, String word) {
		return Filters.and(Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_NAME, normalizedName),
				Filters.or(Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_PARENT, word),
						Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_CHILD, word)));
	}
	
	protected Bson makeQueryRelationPair(String normalizedName, String parent, String child) {
		return Filters.and(Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_NAME, normalizedName),
				Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_PARENT, parent),
				Filters.eq(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_CHILD, child));
	}
	
	/**
	 * このオブジェクトが保持するコレクションから、シソーラス名が重複しないドキュメントの検索結果を取得する。
	 * @return	検索結果のイテレート可能オブジェクト
	 */
	protected DistinctIterable<String> distinctByName() {
		// シソーラス名のみ
		return _mongo_vals_col.distinct(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_NAME, String.class);
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
			FindIterable<? extends Document> result = _mongo_vals_col.find().sort(MongoDelegateDocDtStringThesaurusElem.MONGO_SORT_NAMEDSTRINGTHES_ELEM_ALL);
			if (withoutObjectID)
				result = result.projection(MongoUtil.MONGO_FILTER_WITHOUT_OBJID);
			if (withoutThesName)
				result = result.projection(new Document(MongoDelegateDocDtStringThesaurusElem.MONGO_KEY_NAME, 0));
			return result;
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Faild to sort by all fields in MongoDtNamedStringThesaurus" + _mongo_vals_col.getNamespace().toString() + ":", ex);
		}
	}

	//------------------------------------------------------------
	// I/O
	//------------------------------------------------------------

	/**
	 * 名前付きシソーラス定義の内容を、指定のファイルに CSV フォーマットで出力する。
	 * 
	 * @param csvFile 出力先ファイル
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws MongoAlgeError	データベースに関する処理に失敗した場合
	 * 
	 */
	public void toCSV(File csvFile)
		throws IOException, FileNotFoundException
	{
		dtalge.io.internal.CsvWriter writer = new dtalge.io.internal.CsvWriter(csvFile);
		try {
			writeToCSV(writer);
		}
		finally {
			writer.close();
		}
	}
	
	/**
	 * 名前付きシソーラス定義の内容を、指定された文字セットで指定のファイルに CSV フォーマットで出力する。
	 * 
	 * @param csvFile 出力先ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * @throws MongoAlgeError	データベースに関する処理に失敗した場合
	 * 
	 */
	public void toCSV(File csvFile, String charsetName)
		throws IOException, FileNotFoundException, UnsupportedEncodingException
	{
		dtalge.io.internal.CsvWriter writer = new dtalge.io.internal.CsvWriter(csvFile, charsetName);
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
	 * @return	このシソーラス定義が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
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
	 * CSV フォーマットのファイルを読み込み、新しい名前付きシソーラス定義を生成する。
	 * 
	 * @param session	MongoDB セッションオブジェクト
	 * @param csvFile 読み込む CSV ファイル
	 * 
	 * @return ファイルの内容で生成された、新しい <code>DtNamedStringThesaurusMap</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws dtalge.exception.CsvFormatException シソーラス定義のデータが正しくない場合にスローされる
	 * @throws MongoAlgeError	データベースに関する処理に失敗した場合
	 */
	static public MongoDtNamedStringThesaurus fromCSV(MongoSession session, File csvFile)
		throws IOException, FileNotFoundException, dtalge.exception.CsvFormatException
	{
		MongoDtNamedStringThesaurus newThes = new MongoDtNamedStringThesaurus(session);
		newThes.addAllFromCSV(csvFile);
		return newThes;
	}
	
	/**
	 * 指定された文字セットで CSV フォーマットのファイルを読み込み、新しい名前付きシソーラス定義を生成する。
	 * 
	 * @param session	MongoDB セッションオブジェクト
	 * @param csvFile 読み込む CSV ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @return ファイルの内容で生成された、新しい <code>DtNamedStringThesaurusMap</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws dtalge.exception.CsvFormatException シソーラス定義のデータが正しくない場合にスローされる
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * @throws MongoAlgeError	データベースに関する処理に失敗した場合
	 */
	static public MongoDtNamedStringThesaurus fromCSV(MongoSession session, File csvFile, String charsetName)
		throws IOException, FileNotFoundException, dtalge.exception.CsvFormatException, UnsupportedEncodingException
	{
		MongoDtNamedStringThesaurus newThes = new MongoDtNamedStringThesaurus(session);
		newThes.addAllFromCSV(csvFile, charsetName);
		return newThes;
	}

	//------------------------------------------------------------
	// Internal methods for I/O
	//------------------------------------------------------------

	/**
	 * CSVファイルフォーマットの第1行目のキーワード
	 */
	static public final String CSV_KEYWORD = "#NamedThesaurusMap";

	// シソーラス定義をCSVフォーマットで出力する
	protected void writeToCSV(CsvWriter writer)
		throws IOException
	{
		// キーワード出力
		writer.writeLine(CSV_KEYWORD);

		// データ出力
		MongoCursor<? extends Document> cursor = null;
		try {
			cursor = sortedDocuments(true, false).iterator();
			while (cursor.hasNext()) {
				Document docElem = cursor.next();
				writer.writeField(MongoDelegateDocDtStringThesaurusElem.getThesaurusNameFromDocument(docElem));
				writer.writeField(MongoDelegateDocDtStringThesaurusElem.getThesaurusParentWordFromDocument(docElem));
				writer.writeField(MongoDelegateDocDtStringThesaurusElem.getThesaurusChildWordFromDocument(docElem));
				writer.newLine();
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Faild to write to CSV file from MongoDtNamedStringThesaurus" + _mongo_vals_col.getNamespace().toString() + ":", ex);
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
			throw new CsvFormatException("DtNamedStringThesaurusMap file ID not found.");
		} else {
			dtalge.io.internal.CsvReader.CsvField field = record.getField(0);
			if (field == null || !CSV_KEYWORD.equals(field.getValue())) {
				throw new CsvFormatException("Illegal DtNamedStringThesaurusMap file ID.", record.getLineNo(), 1);
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
			
			// read name column
			String name = freader.readValue();
			if (dtalge.util.Strings.isNullOrEmpty(name)) {
				name = NoNameThesKey;
			}
			
			// read parent column
			String parent = freader.readValue();
			if (dtalge.util.Strings.isNullOrEmpty(parent)) {
				throw new dtalge.exception.CsvFormatException("Parent word cannot be omitted.",
												freader.getLineNo(), freader.getNextPosition());
			}
			
			// read child column
			String child = freader.readValue();
			if (dtalge.util.Strings.isNullOrEmpty(child)) {
				throw new dtalge.exception.CsvFormatException("Child word cannot be omitted.",
												freader.getLineNo(), freader.getNextPosition());
			}
			
			// put in thesaurus
			if (put(name, parent, child)) {
				modified = true;
			}
		}
		return modified;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	/**
	 * <code>MongoDtNamedStringThesaurus</code> クラスの要素にアクセス可能なイテレーターの共通実装。
	 * <p>
	 * このクラスは、名前付きシソーラス定義から単位のペアを順次取得するためのイテレーターである。
	 * <p>
	 * このクラスのインタフェースでは、基本的に {@link #remove()} はサポートされていない。
	 * 
	 * @version 0.5.0
	 * @since 0.5.0
	 * 
	 * @author H.Deguchi(SOARS Project.)
	 * @author Y.Ishizuka(PieCake.inc,)
	 */
	private abstract class AbstractMongoDtNamedStringThesaurusInnerElementIterator<T> implements BigIterator<T>
	{
		MongoCursor<? extends Document>	_itCursor;
		long _expectedModCount;
		
		AbstractMongoDtNamedStringThesaurusInnerElementIterator(MongoCursor<? extends Document> cursor) {
			_itCursor = cursor;
			_expectedModCount = _modCount.get();
		}

		public boolean hasNext() {
			try {
				return (_itCursor==null ? false : _itCursor.hasNext());
			} catch (MongoException ex) {
				throw new MongoAlgeError("MongoDtNamedStringThesaurus's element iterator couldn't check next element in MongoDtNamedStringThesaurus" + _mongo_vals_col.getNamespace().toString() + ":", ex);
			}
		}
		
		/**
		 * <code>AbstractMongoDtNamedStringThesaurusInnerElementIterator</code> クラスでは、このメソッドはサポートされない。
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
	 * <code>MongoDtNamedStringThesaurus</code> クラスのドキュメントイテレーター。
	 * <p>
	 * このクラスは、名前付きシソーラス定義から名前ごとのシソーラス定義を順次取得するためのイテレーターである。
	 * <p>
	 * このクラスのインタフェースでは、{@link #remove()} は実行可能となっている。
	 * 
	 * @version 0.5.0
	 * @since 0.5.0
	 * 
	 * @author H.Deguchi(SOARS Project.)
	 * @author Y.Ishizuka(PieCake.inc,)
	 */
	private class MongoDtNamedStringThesaurusDocumentIterator extends AbstractMongoDtNamedStringThesaurusInnerElementIterator<BigDtNamedStringThesaurusInnerElement>
	{
		protected Object _lastObjectId;
		
		MongoDtNamedStringThesaurusDocumentIterator(MongoCursor<? extends Document> cursor) {
			super(cursor);
		}
		
		@Override
		public MongoDtNamedStringThesaurusInnerElement next() {
			if (_expectedModCount != _modCount.get())
				throw new ConcurrentModificationException();
			if (_itCursor == null)
				throw new NoSuchElementException("MongoDtNamedStringThesaurus's cursor was closed.");
			try {
				Document docElem = _itCursor.next();
				_lastObjectId = docElem.get("_id");
				MongoDtNamedStringThesaurusInnerElement elem = new MongoDtNamedStringThesaurusInnerElement(
						MongoDelegateDocDtStringThesaurusElem.getThesaurusNameFromDocument(docElem),
						MongoDelegateDocDtStringThesaurusElem.getThesaurusParentWordFromDocument(docElem),
						MongoDelegateDocDtStringThesaurusElem.getThesaurusChildWordFromDocument(docElem)
				);
				if (!_itCursor.hasNext()) {
					closeCursor();
				}
				return elem;
			} catch (MongoException ex) {
				throw new MongoAlgeError("MongoDtNamedStringThesaurus's document iterator couldn't get next element in MongoDtNamedStringThesaurus" + _mongo_vals_col.getNamespace().toString() + ":", ex);
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
				throw new MongoAlgeError("MongoDtNamedStringThesaurus's document iterator couldn't remove current element in MongoDtNamedStringThesaurus" + _mongo_vals_col.getNamespace().toString() + ":", ex);
			}
		}
	}
	
	/**
	 * <code>MongoDtNamedStringThesaurus</code> クラスのシソーラス定義イテレーター。
	 * <p>
	 * このクラスは、名前付きシソーラス定義から名前ごとのシソーラス定義を順次取得するためのイテレーターである。
	 * 
	 * @version 0.5.0
	 * @since 0.5.0
	 * 
	 * @author H.Deguchi(SOARS Project.)
	 * @author Y.Ishizuka(PieCake.inc,)
	 */
	private class MongoDtStringThesaurusIterator extends AbstractMongoDtNamedStringThesaurusInnerElementIterator<MongoDtStringThesaurus>
	{
		protected String _lastName;
		
		public MongoDtStringThesaurusIterator(MongoCursor<? extends Document> cursor) {
			super(cursor);
		}
		
		@Override
		public MongoDtStringThesaurus next() {
			// このイテレータがアクセスするのは、重複のないデータ代数元 ID のコレクションのカーソル
			if (_expectedModCount != _modCount.get())
				throw new ConcurrentModificationException();
			if (_itCursor == null)
				throw new NoSuchElementException("MongoDtNamedStringThesaurus's cursor was closed.");
			try {
				Document doc = _itCursor.next();
				_lastName = MongoDelegateDocDtStringThesaurusElem.getThesaurusNameFromDocument(doc);
				if (!_itCursor.hasNext()) {
					closeCursor();
				}
				return new MongoDtStringThesaurus(MongoDtNamedStringThesaurus.this, _lastName);
			} catch (MongoException ex) {
				throw new MongoAlgeError("MongoDtNamedStringThesaurus's element iterator couldn't get next element in MongoDtNamedStringThesaurus" + _mongo_vals_col.getNamespace().toString() + ":", ex);
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
				if (_lastName != null) {
					_mongo_vals_col.deleteMany(makeQueryByName(_lastName));
					_lastName = null;
					_expectedModCount = _modCount.incrementAndGet();
				}
			}
			catch (MongoException ex) {
				throw new MongoAlgeError("MongoDtNamedStringThesaurus's element iterator couldn't remove current element in MongoDtNamedStringThesaurus" + _mongo_vals_col.getNamespace().toString() + ":", ex);
			}
		}
	}
}
