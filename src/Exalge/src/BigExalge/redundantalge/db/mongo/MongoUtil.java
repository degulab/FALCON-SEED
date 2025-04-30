/*
 * @(#)MongoUtil.java	0.992	2020/03/12
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MongoUtil.java	0.991	2019/02/23
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MongoUtil.java	0.990	2018/11/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package redundantalge.db.mongo;

import java.util.ArrayList;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.ConnectionString;
import com.mongodb.MongoException;
import com.mongodb.MongoNamespace;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;

import redundantalge.util.UUIDGenerator;

/**
 * MongoDB に関するユーティリティ群。
 * 
 * @version 0.992
 * @since 0.990
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public class MongoUtil
{

	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	/** MongoDB に接続する際のスキーマを表す文字列 **/
	static public final String	MONGO_SCHEMA	= "mongodb";
	/** MongoDB のデフォルトポート番号 **/
	static public final int		MONGO_DEFAULT_PORT	= 27017;
	/** MongoDB のデフォルト認証データベース名 **/
	static public final String	MONGO_DEFAULT_AUTHDB	= "admin";

	/** MongoDB 接続先 URI のパラメータキー：認証データベース名 **/
	static public final String	MONGO_URIPARAM_AUTHDB = "authSource";
	
	/** MongoDB の例外に含まれる、詳細な応答内容の先頭キーワード **/
	static public final String	DETAILEDMSG_PREFIX_FULL_RESPONSE	= "The full response";
	/** MongoDB の例外に含まれる、詳細な応答内容の先頭キーワード **/
	static public final String	DETAILEDMSG_PREFIX_CLIENTVIEW		= "Client view of cluster state";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** MongoDB の ObjectID('_id') を除外するドキュメントオブジェクト **/
	static public final Document MONGO_FILTER_WITHOUT_OBJID = new Document("_id", 0);

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private MongoUtil() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * 指定されたパラメータで、MongoDB 接続先を示す文字列を生成する。
	 * このメソッドは、接続文字列の正当性については考慮しない。
	 * @param host			接続先ホスト名、<tt>null</tt> を指定した場合はローカルホストを示す IP アドレス
	 * @param port			接続先ポート番号、負の値を指定した場合はデフォルトのポート番号
	 * @param authDbName	認証データベース名、<tt>null</tt> を指定した場合は 'admin'
	 * @param authUser		認証ユーザー名、<tt>null</tt> を指定した場合は認証なし
	 * @param authPass		認証パスワード、<tt>null</tt> を指定した場合はパスワードなし
	 * @param useDbName		代数を格納するデータベース名、<tt>null</tt> を指定した場合はデータベースなし
	 * @param colName		代数を格納するコレクション名、<tt>null</tt> を指定した場合はコレクションなし
	 * @return	MongoDB 接続先を表す文字列
	 * @throws IllegalArgumentException	<em>authDbName</em>、<em>useDbName</em>、<em>colName</em> のいずれかが空文字の場合
	 */
	static public String makeString(String host, int port, String authDbName, String authUser, String authPass, String useDbName, String colName)
	{
		StringBuilder strbuf = new StringBuilder();
		//--- schema for MongoDB
		strbuf.append(MONGO_SCHEMA);
		strbuf.append("://");
		//--- user & password
		if (authUser != null) {
			strbuf.append(authUser);
			if (authPass != null) {
				strbuf.append(':');
				strbuf.append(authPass);
			}
			strbuf.append('@');
		}
		//--- Host
		if (host != null) {
			// specified host name
			strbuf.append(host);
		} else {
			// localhost
			strbuf.append("127.0.0.1");
		}
		//--- port
		if (port >= 0) {
			// specified port number
			strbuf.append(':');
			strbuf.append(port);
		} else {
			// default port number
			strbuf.append(':');
			strbuf.append(MONGO_DEFAULT_PORT);
		}
		strbuf.append('/');
		//--- useDbName
		if (useDbName != null) {
			MongoNamespace.checkDatabaseNameValidity(useDbName);
			strbuf.append(useDbName);
		}
		//--- colName
		if (colName != null) {
			if (useDbName == null) {
				throw new IllegalArgumentException("Although collection was specified, DB name is not specified.");
			}
			MongoNamespace.checkCollectionNameValidity(colName);
			strbuf.append('.');
			strbuf.append(colName);
		}
		
		// URI parameters
		//--- authDbName
		if (authUser != null) {
			strbuf.append('?');
			strbuf.append(MONGO_URIPARAM_AUTHDB);
			strbuf.append('=');
			if (authDbName != null) {
				MongoNamespace.checkDatabaseNameValidity(authDbName);
				strbuf.append(authDbName);
			} else {
				strbuf.append(MONGO_DEFAULT_AUTHDB);
			}
		}
		
		// make string
		return strbuf.toString();
	}
	
	/**
	 * 指定されたパラメータで、MongoDB 接続先を示す <code>ConnectionString</code> オブジェクトを生成する。
	 * @param host			接続先ホスト名、<tt>null</tt> を指定した場合はローカルホストを示す IP アドレス
	 * @param port			接続先ポート番号、負の値を指定した場合はデフォルトのポート番号
	 * @param authDbName	認証データベース名、<tt>null</tt> を指定した場合は 'admin'
	 * @param authUser		認証ユーザー名、<tt>null</tt> を指定した場合は認証なし
	 * @param authPass		認証パスワード、<tt>null</tt> を指定した場合はパスワードなし
	 * @param useDbName		代数を格納するデータベース名、<tt>null</tt> を指定した場合はデータベースなし
	 * @param colName		代数を格納するコレクション名、<tt>null</tt> を指定した場合はコレクションなし
	 * @return	MongoDB 接続先を示す <code>ConnectionString</code> オブジェクト
	 * @throws IllegalArgumentException	<em>authDbName</em>、<em>useDbName</em>、<em>colName</em> のいずれかが空文字の場合
	 * @see com.mongodb.ConnectionString
	 */
	static public ConnectionString makeConnectionString(String host, int port, String authDbName, String authUser, String authPass, String useDbName, String colName)
	{
		String str = makeString(host, port, authDbName, authUser, authPass, useDbName, colName);
		return new ConnectionString(str);
	}

	/**
	 * MongoDB をバックエンドとする交換代数において、コレクション名が英数字のみで構成されているかを検証する。
	 * @param name	検証する文字列
	 * @throws IllegalArgumentException	<em>name</em> が空文字の場合
	 */
	static public void validCollectionName(String name) {
		MongoNamespace.checkCollectionNameValidity(name);
	}
	
	/**
	 * 指定された接続文字列オブジェクトから、コレクション名を取り除く。
	 * なお、指定された接続文字列オブジェクトにコレクション名が含まれていない場合は、<em>mcs</em> をそのまま返す。
	 * @param mcs	対象の接続文字列オブジェクト
	 * @return	コレクション名を取り除いた接続文字列オブジェクト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	指定された接続文字列が適切ではない場合
	 */
	static public ConnectionString reduceCollectionNameFrom(ConnectionString mcs) {
		if (mcs.getCollection() == null)
			return mcs;
		String str = mcs.getConnectionString();
		int idxsrv  = str.indexOf(':')+3;
		int idxpath = str.indexOf('/', idxsrv);
		int idxscol = str.indexOf('.', idxpath);
		int idxopts = str.indexOf('?', idxsrv);
		StringBuilder sb = new StringBuilder();
		sb.append(str.substring(0, idxscol));
		if (idxopts >= 0) {
			sb.append(str.substring(idxopts));
		}
		return new ConnectionString(sb.toString());
	}

	/**
	 * MongoDB のユニークなコレクション名を返す。
	 * <p>このメソッドが返す文字列は、UUID の 16 進数表現の文字列。
	 * @return	ユニークなコレクション名を表す文字列。
	 */
	static public String makeUniqueCollectionName() {
		return UUIDGenerator.genHexUUID();
	}

	/**
	 * MongoDB の代数集合要素のための、ユニークな代数元 ID を返す。
	 * <p>このメソッドが返す文字列は、UUID の 16 進数表現の文字列。
	 * @return	ユニークな代数元 ID を表す文字列
	 */
	static public String makeUniqueAlgeSetElemID() {
		return UUIDGenerator.genHexUUID();
	}
	
	/**
	 * 例外を無視してカーソルをクローズする。
	 * <em>cursor</em> が <tt>null</tt> の場合、このメソッドは何もしない。
	 * @param cursor	クローズするカーソル
	 */
	static public void closeCursorSilent(MongoCursor<?> cursor) {
		if (cursor != null) {
			try {
				cursor.close();
			} catch (Throwable ignoreEx) {}
		}
	}

	/**
	 * 指定のコレクションから MongoDB のオブジェクト ID のみを除いた検索結果を返す。
	 * @param mcol	MongoDB コレクション
	 * @return	検索結果オブジェクト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースに関する処理に失敗した場合
	 */
	static public FindIterable<? extends Document> findAllWithoutObjectID(MongoCollection<? extends Document> mcol) {
		try {
			return mcol.find().projection(MONGO_FILTER_WITHOUT_OBJID);
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Faild to find and project in " + mcol.getNamespace().toString() + ":", ex);
		}
	}

	/**
	 * 指定されたコレクションの全要素を削除する。
	 * @param mcol	対象のコレクション
	 * @return	削除したドキュメント数
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースに関する処理に失敗した場合
	 */
	static public long clearAllDocuments(MongoCollection<? extends Document> mcol) {
		try {
			return mcol.deleteMany(new Document()).getDeletedCount();
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to clear all documents from " + mcol.getNamespace().toString() + ":", ex);
		}
	}

	/**
	 * <em>mcol_src</em> のコレクションの内容を、<em>mcol_dst</em> コレクションに複製する。
	 * <em>mcol_dst</em> コレクションが空ではない場合、<em>mcol_dst</em> の既存のドキュメントはすべて破棄される。
	 * @param mcol_dst	複製先のコレクション
	 * @param mcol_src	複製元のコレクション
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	同一のコレクションが指定されている場合
	 * @throws MongoAlgeError	データベースに関する処理に失敗した場合
	 */
	static public void duplicateCollection(MongoCollection<Document> mcol_dst, MongoCollection<? extends Document> mcol_src) {
		try {
			MongoNamespace dstNamespace = mcol_dst.getNamespace();
			MongoNamespace srcNamespace = mcol_src.getNamespace();
			if (srcNamespace.getDatabaseName().equals(dstNamespace.getDatabaseName())) {
				// same database
				if (srcNamespace.getCollectionName().equals(dstNamespace.getCollectionName())) {
					// same collection
					throw new IllegalArgumentException("Couldn't duplicate same collection" + srcNamespace.toString());
				}
				//--- clear as needed
				if (mcol_dst.countDocuments() > 0L) {
					mcol_dst.deleteMany(new Document());
				}
				//--- aggregate on same database
				if (mcol_src.countDocuments() > 0L) {
					ArrayList<Bson> list = new ArrayList<Bson>();
					list.add(Aggregates.project(new Document("_id", 0)));
					list.add(Aggregates.out(dstNamespace.getCollectionName()));
					mcol_src.aggregate(list).toCollection();
				}
			}
			else {
				// different databases
				//--- clear as needed
				if (mcol_dst.countDocuments() > 0L) {
					mcol_dst.deleteMany(new Document());
				}
				//--- duplicate between defferent databases
				if (mcol_src.countDocuments() > 0L) {
					MongoCursor<? extends Document> cursor = mcol_src.find().projection(MONGO_FILTER_WITHOUT_OBJID).iterator();
					try {
						while (cursor.hasNext()) {
							mcol_dst.insertOne(cursor.next());
						}
					}
					finally {
						try {
							cursor.close();
						} catch (Throwable ignoreEx) {}
					}
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to duplicate " + mcol_src.getNamespace().toString() + " to " + mcol_dst.getNamespace().toString() + ":", ex);
		}
	}

	/**
	 * 指定されたコレクションを指定のソート定義でソートし、ObjectID 以外の要素が等しいかどうかを判定する。
	 * @param sort	ソート定義オブジェクト、ソートしない場合は <tt>null</tt>
	 * @param mcol1	判定するコレクションの一方
	 * @param mcol2	判定するコレクションのもう一方
	 * @return	等しい場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws MongoAlgeError	データベースに関する処理に失敗した場合
	 */
	static public boolean equalsCollectionsWithoutID(Bson sort, MongoCollection<? extends Document> mcol1, MongoCollection<? extends Document> mcol2) {
		if (mcol1 == mcol2)
			return true;
		if (mcol1 == null || mcol2 == null)
			return false;
		try {
			long numCol1 = mcol1.countDocuments();
			long numCol2 = mcol2.countDocuments();
			if (numCol1 != numCol2)
				return false;
			else if (numCol1 == 0L)
				return true;
			//--- compare documents
			MongoCursor<? extends Document> cursor1 = null;
			MongoCursor<? extends Document> cursor2 = null;
			try {
				if (sort == null) {
					// without sort
					cursor1 = mcol1.find().projection(MONGO_FILTER_WITHOUT_OBJID).iterator();
					cursor2 = mcol2.find().projection(MONGO_FILTER_WITHOUT_OBJID).iterator();
				} else {
					// with sort
					cursor1 = mcol1.find().sort(sort).projection(MONGO_FILTER_WITHOUT_OBJID).iterator();
					cursor2 = mcol2.find().sort(sort).projection(MONGO_FILTER_WITHOUT_OBJID).iterator();
				}
				//--- compare
				while (cursor1.hasNext()) {
					Document doc1 = cursor1.next();
					Document doc2 = cursor2.next();
					if (!doc1.equals(doc2)) {
						// not equals
						return false;
					}
				}
				//--- equal all documents
				return true;
			}
			finally {
				if (cursor1 != null) {
					try {
						cursor1.close();
						cursor1 = null;
					} catch (Throwable ignoreEx) {}
				}
				if (cursor2 != null) {
					try {
						cursor2.close();
						cursor2 = null;
					} catch (Throwable ignoreEx) {}
				}
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to judge equals two collections:", ex);
		}
	}

	/**
	 * 指定したドキュメントを、指定した場所に上書きする。
	 * <p>指定した場所に存在しないドキュメントを追加する場合、<em>data</em> のデータ構造のみが新たなドキュメントとして追加されるため、既存のデータ構造と異なる場合は注意すること。
	 * @param mcol		対象のコレクション
	 * @param where		更新する場所を検索するためのキーを格納したドキュメント
	 * @param data		更新するデータセット
	 * @param multiple	<tt>true</tt> の場合は <em>where</em> と一致するすべてのドキュメントを更新する、<tt>false</tt> の場合はどれか一つを更新する
	 * @param upsert    <tt>true</tt> の場合は <em>where</em> と一致するドキュメントがない場合に追加する
	 * @return	更新結果のオブジェクト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースに関する処理に失敗した場合
	 */
	static public UpdateResult updateDocument(MongoCollection<Document> mcol, Document where, Document data, boolean multiple, boolean upsert) {
		where.isEmpty();
		data.isEmpty();
		try {
			if (multiple) {
				// 一致するすべてのドキュメントを更新
				UpdateOptions options = new UpdateOptions();
				options.upsert(upsert);
				return mcol.updateMany(where, new Document("$set", data), options);
			}
			else {
				// 一致するどれか一つのドキュメントを更新
				UpdateOptions options = new UpdateOptions();
				options.upsert(upsert);
				return mcol.updateOne(where, new Document("$set", data), options);
			}
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to update document into " + mcol.getNamespace().toString() + ":", ex);
		}
	}

	/**
	 * 指定されたドキュメントと同じ内容のものが存在しない場合のみ、そのドキュメントを追加する。
	 * @param mcol	対象のコレクション
	 * @param data	追加するデータ
	 * @return	追加された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースに関する処理に失敗した場合
	 */
	static public boolean insertDocumentIfNotExist(MongoCollection<Document> mcol, Document data) {
		data.isEmpty();
		try {
			if (mcol.countDocuments(data) == 0L) {
				// insert document
				mcol.insertOne(data);
				return true;
			}
			//--- no insertion
			return false;
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to insert document into " + mcol.getNamespace().toString() + " if same does not exist:", ex);
		}
	}

	/**
	 * 指定されたドキュメントと一致する内容を持つドキュメントを、指定されたコレクションから削除する。
	 * @param mcol		対象のコレクション
	 * @param where		削除する位置を検索するためのキーを格納したドキュメント
	 * @param multiple	<em>where</em> と一致するすべてのドキュメントを削除する場合は <tt>true</tt>
	 * @return	削除されたドキュメント数
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws MongoAlgeError	データベースに関する処理に失敗した場合
	 */
	static public long deleteMatchedDocument(MongoCollection<? extends Document> mcol, Document where, boolean multiple) {
		where.isEmpty();
		try {
			long removed;
			if (multiple) {
				removed = mcol.deleteMany(where).getDeletedCount();
			}
			else {
				removed = mcol.deleteOne(where).getDeletedCount();
			}
			return removed;
		}
		catch (MongoException ex) {
			throw new MongoAlgeError("Failed to delete matched document from " + mcol.getNamespace().toString() + ":", ex);
		}
	}
	
	/**
	 * 例外に含まれる詳細メッセージを取得する。
	 * 要因例外が含まれている場合、そのメッセージも改行文字に続けて連結する。
	 * MongoDB のスローする例外の場合、'The full response' 以降の文字列は除去する。
	 * @param thrown	対象の例外オブジェクト
	 * @return	整形された文字列、<em>thrown</em> が <tt>null</tt> の場合は <tt>null</tt>
	 * @since 0.992
	 */
	static public String formatExceptionMessage(Throwable thrown) {
		if (thrown == null)
			return null;
		
		String msgCause = null;
		Throwable cause = thrown.getCause();
		if (cause != null) {
			msgCause = cause.getLocalizedMessage();
			if (msgCause == null || msgCause.isEmpty()) {
				msgCause = msgCause.toString();
			}
			else {
				msgCause = reduceDetailedMessage(msgCause);
			}
		}
		
		String msg = thrown.getLocalizedMessage();
		if (msg == null || msg.isEmpty()) {
			msg = (msgCause==null ? thrown.toString() : msgCause);
		}
		else {
			msg = reduceDetailedMessage(msg);
			if (msgCause != null) {
				msg = msg.concat("\r\n");
				msg = msg.concat(msgCause);
			}
		}
		
		return msg;
	}
	
	/**
	 * MongoDB が生成するメッセージから、詳細なサーバーステータスを除去する。
	 * 例外に含まれるメッセージを取得する。
	 * @param msg	対象の文字列
	 * @return	除去済みの文字列、<em>msg</em> が <tt>null</tt> の場合は <tt>null</tt>
	 * @since 0.992
	 */
	static public String reduceDetailedMessage(String msg) {
		if (msg == null)
			return null;
		
		int index = msg.indexOf(DETAILEDMSG_PREFIX_FULL_RESPONSE);
		if (index > 0) {
			return msg.substring(0, index).trim();
		}
		
		index = msg.indexOf(DETAILEDMSG_PREFIX_CLIENTVIEW);
		if (index > 0) {
			return msg.substring(0, index).trim();
		}
		
		// no changes
		return msg;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
//	/**
//	 * MongoDB をバックエンドとする交換代数基底集合を保持するコレクション用の、ユニークな一時的コレクション名を返す。
//	 * このメソッドが返す文字列は、'tBS' に UUID の 16 進数表現の文字列を連結したものとなる。
//	 * @return	ユニークな一時的コレクション名を表す文字列
//	 */
//	static public String makeTemporaryExBaseCollectionName() {
//		return "tBS" + UUIDGenerator.genHexUUID();
//	}
//	
//	/**
//	 * MongoDB をバックエンドとする交換代数元を保持するコレクション用の、ユニークな一時的コレクション名を返す。
//	 * このメソッドが返す文字列は、'tAL' に UUID の 16 進数表現の文字列を連結したものとなる。
//	 * @return	ユニークな一時的コレクション名を表す文字列
//	 */
//	static public String makeTemporaryExalgeCollectionName() {
//		return "tAL" + UUIDGenerator.genHexUUID();
//	}
//	
//	/**
//	 * MongoDB をバックエンドとする交換代数集合の値を保持するコレクション用の、ユニークな一時的コレクション名を返す。
//	 * このメソッドが返す文字列は、'tASv' に UUID の 16 進数表現の文字列を連結したものとなる。
//	 * @return	ユニークな一時的コレクション名を表す文字列
//	 */
//	static public String makeTemporaryExAlgeSetValuesCollectionName() {
//		return "tASv" + UUIDGenerator.genHexUUID();
//	}
//	
//	/**
//	 * MongoDB をバックエンドとする交換代数集合のキーを保持するコレクション用の、ユニークな一時的コレクション名を返す。
//	 * このメソッドが返す文字列は、'tASk' に UUID の 16 進数表現の文字列を連結したものとなる。
//	 * @return	ユニークな一時的コレクション名を表す文字列
//	 */
//	static public String makeTemporaryExAlgeSetKeysCollectionName() {
//		return "tASk" + UUIDGenerator.genHexUUID();
//	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
