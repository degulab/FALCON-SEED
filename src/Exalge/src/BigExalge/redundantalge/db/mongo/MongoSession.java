/*
 * @(#)MongoSession.java	0.992	2020/03/12
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MongoSession.java	0.991	2019/02/23
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package redundantalge.db.mongo;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.bson.Document;
import org.bson.codecs.BigDecimalCodec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import com.mongodb.Block;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.MongoNamespace;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.connection.ServerConnectionState;
import com.mongodb.connection.ServerDescription;
import com.mongodb.connection.ServerSettings;
import com.mongodb.connection.ServerSettings.Builder;
import com.mongodb.event.ServerClosedEvent;
import com.mongodb.event.ServerDescriptionChangedEvent;
import com.mongodb.event.ServerListener;
import com.mongodb.event.ServerOpeningEvent;

/**
 * MongoDB とのセッションを保持するオブジェクト。
 * <p><b>注意：</b>
 * <blockquote>
 * このオブジェクトは、スレッドセーフである。
 * </blockquote>
 * 
 * @version 0.992
 * @since 0.991
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public class MongoSession
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	///** PING コマンド **/
	//static protected final BasicDBObject	_cmdPing = new BasicDBObject("ping", "1");
	
	/** サーバーが応答するまでの待機時間(ミリ秒) **/
	static protected long	MS_WAIT_CONNECTION	= 60000L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 管理対象のセッションの、アプリケーション唯一のマップ **/
	static protected final LinkedHashMap<ConnectionString, MongoSession>	_sessions;
	
	static {
		//--- create session map
		_sessions = new LinkedHashMap<ConnectionString, MongoSession>();
		//--- register shutdown hook
		Runtime.getRuntime().addShutdownHook(new MongoSessionShutdownHook());
	}

	/** サーバー応答に応じて呼び出されるイベントハンドラ **/
	protected final AtomicReference<MongoSessionHandler>	_atomicHandler	= new AtomicReference<MongoSessionHandler>();
	
	/** このセッションの接続先を示すオブジェクト **/
	protected final ConnectionString	_clientConnectionString;
	/** MongoDB サーバーの状態監視結果を受け取るリスナーインスタンス **/
	protected MongoServerListener	_serverListener;

	/** MongoDB Client オブジェクト **/
	protected MongoClient	_mclient;
	/** MongoDB Database オブジェクト **/
	protected MongoDatabase	_mdb;
	/** データベースへの書き込み権限を所持していることを示すフラグ、不明な場合は <tt>null</tt> **/
	protected Boolean		_flgDbWritable;
	/** テンポラリコレクションのコレクション名をキーとする生成順マップ **/
	protected LinkedHashMap<MongoNamespace, MongoCollection<Document>>	_tempCollectionMap;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/**
	 * 指定されたパラメータを持つ、新しいインスタンスを生成する。
	 * このメソッドでは、データベースへの接続は行われない。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * セッションは {@link org.redundantalge.common.db.mongo.MongoSessionManager} から
	 * 生成するものであり、このコンストラクタを直接呼び出してはならない。
	 * </blockquote>
	 * @param muri	データベースの接続先を示すオブジェクト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	データベース名が指定されていない場合
	 */
	protected MongoSession(final MongoServerURI muri) {
		String dbName = muri.getDatabaseName();
		if (dbName == null || dbName.isEmpty())
			throw new IllegalArgumentException("MongoServerURI has no databse name : [" + muri.toString() + "]");
		_clientConnectionString = muri.getClientConnectionString();
		_serverListener = new MongoServerListener();
		_tempCollectionMap = new LinkedHashMap<MongoNamespace, MongoCollection<Document>>();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * サーバー応答に応じて呼び出されるイベントハンドラを取得する。
	 * @return	設定されているハンドラオブジェクト、設定されていない場合は <tt>null</tt>
	 */
	public MongoSessionHandler getHandler() {
		return _atomicHandler.get();
	}
	
	/**
	 * サーバー応答に応じて呼び出されるイベントハンドラを設定する。
	 * @param newHandler	設定するハンドラオブジェクト、設定しない場合は <tt>null</tt>
	 */
	public void setHandler(MongoSessionHandler newHandler) {
		_atomicHandler.set(newHandler);
	}
	
	/**
	 * 指定されたコレクションが存在するかどうかを判定する。
	 * @param colname	判定するコレクション名
	 * @return	コレクションが存在していれば <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws MongoAlgeError	サーバーとの接続が確立できない場合
	 * @since 0.992
	 */
	public boolean collectionExists(String colname) {
		ensureConnection();
		MongoIterable<String> names = _mdb.listCollectionNames();
		if (names != null) {
			for (String cname : names) {
				if (cname != null && cname.equals(colname)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 指定されたコレクションを削除する。
	 * @param colname	コレクション名
	 * @return	コレクションが削除できた場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws MongoAlgeError	サーバーとの接続が確立できない場合、もしくは削除に失敗した場合
	 * @since 0.992
	 */
	public boolean removeCollection(String colname) {
		boolean removed = false;
		if (collectionExists(colname)) {
			MongoCollection<Document> mcol = _mdb.getCollection(colname);
			try {
				mcol.drop();
				removed = true;
			}
			catch (MongoException ex) {
				throw new MongoAlgeError("Failed to drop collection of [" + mcol.getNamespace().toString() + "] :", ex);
			}
		}
		return removed;
	}

	/**
	 * 指定された接続先で接続される MongoDB のセッションを取得する。
	 * @param muri	データベースの接続先を示すオブジェクト
	 * @return	指定の接続先に接続される MongoDB クライアントセッション
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	データベース名が指定されていない場合
	 */
	static public final MongoSession getOrNewSession(MongoServerURI muri) {
		ConnectionString ccs = muri.getClientConnectionString();
		synchronized (_sessions) {
			MongoSession session = _sessions.get(ccs);
			if (session == null) {
				session = new MongoSession(muri);
				//--- register session
				_sessions.put(ccs, session);
			}
			return session;
		}
	}
	
	/**
	 * このプロセスで利用されているすべての <code>MongoClientSession</code> を切断し破棄する。
	 */
	static public final void cleanupAllSessions() {
		MongoSession[] arySessions;
		synchronized (_sessions) {
			arySessions = _sessions.values().toArray(new MongoSession[_sessions.size()]);
			_sessions.clear();
		}
		//--- 例外を無視してクローズ
		for (MongoSession client : arySessions) {
			try {
				client.closeSession();
			} catch (Throwable ignoreEx) {}
		}
	}
	
	/**
	 * 指定されたセッションを、管理対象に追加する。
	 * @param session	管理対象とするセッション
	 * @return	追加された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	static protected boolean registerSession(MongoSession session) {
		synchronized (_sessions) {
			if (!_sessions.containsKey(session.getConnectionString())) {
				_sessions.put(session.getConnectionString(), session);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 指定されたセッションを、管理対象から除外する。
	 * @param session	除外するセッション
	 * @return	除外された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	static protected boolean unregisterSession(MongoSession session) {
		synchronized (_sessions) {
			if (_sessions.containsKey(session.getConnectionString())) {
				_sessions.remove(session.getConnectionString());
				return true;
			}
		}
		return false;
	}
	
	/**
	 * このセッションの接続先を示す文字列を返す。
	 * @return	接続先 URI を表す文字列
	 */
	public String getStorageUriString() {
		return _clientConnectionString.getConnectionString();
	}
	
	/**
	 * このセッションの接続先を示すオブジェクトを返す。
	 * @return	接続先文字列オブジェクト
	 */
	public ConnectionString getConnectionString() {
		return _clientConnectionString;
	}

	/**
	 * このセッションのデータベースを返す。
	 * @return	MongoDB のデータベース
	 */
	public MongoDatabase getDatabase() {
		return _mdb;
	}

	/**
	 * 指定されたコレクション名でコレクションを取得する。
	 * <p>このメソッドで取得したコレクションは、セッションを閉じても破棄されない。
	 * <p>このメソッド呼び出し時にデータベースに接続されていない場合、データベースの接続を行う。
	 * @param name	コレクション名
	 * @return	コレクションオブジェクト
	 * @throws IllegalArgumentException	コレクション名が有効ではない場合
	 * @throws MongoAlgeError	サーバーとの接続が確立できない場合
	 */
	public MongoCollection<Document> getPersistentCollection(String name) {
		MongoUtil.validCollectionName(name);
		ensureConnection();
		synchronized (_clientConnectionString) {
			if (_mdb == null) {
				throw new IllegalStateException("MongoDB session was closed by user.");
			}
			try {
				return _mdb.getCollection(name);
			}
			catch (Throwable ex) {
				throw new MongoAlgeError("Failed to get collection[" + name + "]");
			}
		}
	}

	/**
	 * 指定されたプレフィックスで、一時的なコレクションを取得する。
	 * <p>このメソッドで取得したコレクションは、セッションを閉じるときに自動的に破棄される。
	 * <p>このメソッド呼び出し時にデータベースに接続されていない場合、データベースの接続を行う。
	 * @param prefix	一時的なコレクションの名前に付加するプレフィックス
	 * @return	コレクションオブジェクト
	 * @throws MongoAlgeError	サーバーとの接続が確立できない場合
	 */
	public MongoCollection<Document> getTemporaryCollection(String prefix) {
		String colName;
		if (prefix != null && !prefix.isEmpty()) {
			colName = prefix + MongoUtil.makeUniqueCollectionName();
		} else {
			colName = MongoUtil.makeUniqueCollectionName();
		}
		ensureConnection();
		synchronized (_clientConnectionString) {
			if (_mdb == null) {
				throw new IllegalStateException("MongoDB session was closed by user.");
			}
			try {
				MongoCollection<Document> mcol = _mdb.getCollection(colName);
				registerTemporaryCollection(mcol);
				return mcol;
			}
			catch (Throwable ex) {
				throw new MongoAlgeError("Failed to get collection[" + colName + "]");
			}
		}
	}
	
	/**
	 * このセッションが接続されているかどうかを判定する。
	 * このメソッドは、例外をスローしない。
	 * @return	接続要求中もしくは接続されている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean isConnected() {
		synchronized (_clientConnectionString) {
			if (_mdb == null) {
				// no session
				return false;
			}
			
			// check server status
			return (_serverListener.isOpened() || _serverListener.isConnected());
		}
	}

	/**
	 * このセッションを閉じる。
	 * 例外はスローしない。
	 */
	public void closeSession() {
		synchronized (_clientConnectionString) {
			if (_mclient != null) {
				// マネージャーから除外
				unregisterSession(this);
				// drop all temporary collections
				dropAllTemporaryCollections();
				// close session
				try {
					_mclient.close();
				} catch (Throwable ignoreEx) {}
				_mdb = null;
				_mclient = null;
			}
			_flgDbWritable = null;
		}
	}

	/**
	 * 現在の接続先情報に基づき、MongoDB セッションが接続された状態にする。
	 * @throws MongoConnectionError	サーバーとの接続が確立できない場合
	 */
	public void ensureConnection() {
		synchronized (_clientConnectionString) {
			// 新規作成
			if (_mdb == null) {
				_serverListener.clearErrors();
				MongoClient newClient = null;
				try {
					//--- 代数要素の値のために、BigDecimal に対応するコーデックを準備
					CodecRegistry codecreg = CodecRegistries.fromRegistries(
							CodecRegistries.fromCodecs(new BigDecimalCodec()),
							MongoClientSettings.getDefaultCodecRegistry());
					//--- クライアント設定
					MongoClientSettings.Builder mongoBuilder = MongoClientSettings.builder();
					mongoBuilder.applyConnectionString(_clientConnectionString);
					mongoBuilder.applyToServerSettings(new Block<ServerSettings.Builder>() {
						@Override
						public void apply(Builder t) {
							t.addServerListener(_serverListener);
						}
					});
					//--- クライアントの生成
					newClient = MongoClients.create(mongoBuilder.build());
					//--- サーバー応答を待機
					_serverListener.awaitFirstServerResponse(MS_WAIT_CONNECTION, TimeUnit.MILLISECONDS);
					if (_serverListener.hasErrors()) {
						//--- Server からのエラー応答
						throw new MongoConnectionError("Failed to connect to MongoDB server.", _serverListener.getLastError());
					}
					//--- データベースの生成
					MongoDatabase mdb = newClient.getDatabase(_clientConnectionString.getDatabase());
					if (_serverListener.hasErrors()) {
						//--- Server からのエラー応答
						throw new MongoConnectionError("Failed to get database[" + String.valueOf(_clientConnectionString.getDatabase()) + "] from MongoDB server.", _serverListener.getLastError());
					}
					//--- データベースにコーデックを適用
					mdb.withCodecRegistry(codecreg);
					if (_serverListener.hasErrors()) {
						//--- Server からのエラー応答
						throw new MongoConnectionError("Failed to set custom codec to database[" + String.valueOf(_clientConnectionString.getDatabase()) + "].", _serverListener.getLastError());
					}
					//--- succeeded
					_mclient = newClient;
					_mdb = mdb;
					newClient = null;
					mdb = null;
				}
				catch (InterruptedException ex) {
					// 待機中に割込みが発生
					throw new MongoConnectionError("Waiting connection is interrupted.", ex);
				}
				catch (MongoConnectionError ex) {
					throw ex;
				}
				catch (Throwable ex) {
					// 通常、接続できない場合はここで例外が発生するが、
					// Mongo-Java-Driver の場合は遅延接続となっているため、
					// サーバー側への何らかのコマンドが送信されるまで接続が行われず、
					// ここで例外は発生しない。
					// が、念のため。
					throw new MongoConnectionError("Failed to connect to MongoDB server.", ex);
				}
				finally {
					if (newClient != null) {
						// failed to create a new client
						try {
							newClient.close();
						} catch (Throwable ignoreEx) {}
					}
				}
			}
			
			// 接続確認
			//--- MongoDatabase.listConnectionNames() のみでは接続が確立しない。
			//--- listConnectionNames() が返す MongoIterable にアクセスして初めて、サーバーに問い合わせが発行される。
			//--- また、サーバーへの readWrite 権限の有無を、ダミーコレクションを作成して判定している。
			//--- メンドクセーナーモー
			if (_flgDbWritable == null || !_serverListener.isConnected()) {
				// 未接続の場合、もしくは書き込み権限が不明な場合
				TreeSet<String> colNameSet = new TreeSet<String>();
				String createdTempColName = null;
				try {
					// コレクションリストの取得
					MongoIterable<String> names = _mdb.listCollectionNames();
					if (names != null) {
						for (String cname : names) {
							if (cname != null && !cname.isEmpty()) {
								colNameSet.add(cname);
							}
						}
					}
					if (_flgDbWritable == null) {
						// コレクションリストが取得できた時点で、読込権限ありとみなす。
						_flgDbWritable = Boolean.FALSE;
					}
					
					// テンポラリコレクションの作成
					String colName;
					do {
						colName = "tmpdmy" + MongoUtil.makeUniqueCollectionName();
					} while (colNameSet.contains(colName));
					createdTempColName = colName;
					_mdb.createCollection(createdTempColName);
					if (_serverListener.hasErrors()) {
						//--- Server からのエラー応答
						throw new MongoConnectionError("Failed to create testing collection in database[" + String.valueOf(_clientConnectionString.getDatabase()) + "].", _serverListener.getLastError());
					}
					else {
						// エラーが発生しなければ、テンポラリコレクションは削除
						MongoCollection<Document> mcol = _mdb.getCollection(createdTempColName);
						mcol.drop();
						if (_serverListener.hasErrors()) {
							//--- Server からのエラー応答
							throw new MongoConnectionError("Failed to drop testing collection from database[" + String.valueOf(_clientConnectionString.getDatabase()) + "].", _serverListener.getLastError());
						}
					}
					
					// この時点で書き込み権限あり
					_flgDbWritable = Boolean.TRUE;
				}
				catch (MongoConnectionError ex) {
					throw ex;
				}
				catch (Throwable ex) {
					// MongoDB is down...
					throw new MongoConnectionError("Failed to access MongoDB server.", ex);
				}
			}
			else if (!_flgDbWritable.booleanValue()) {
				// 書き込み権限がなければ、継続不可
				throw new MongoAlgeError("Could not write to the database[" + String.valueOf(_clientConnectionString.getDatabase()) + "].");
			}
		}
	}
	
	/**
	 * 指定されたコレクションを一時的なコレクションとして登録する。
	 * 登録されたコレクションは、セッション切断直前に自動的に破棄される。
	 * @param mcol	登録するコレクション
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public void registerTemporaryCollection(MongoCollection<Document> mcol) {
		MongoNamespace colName = mcol.getNamespace();
		synchronized (_clientConnectionString) {
			_tempCollectionMap.put(colName, mcol);
		}
	}
	
	/**
	 * 指定されたコレクションを一時的コレクションの登録から除外する。
	 * @param mcol	除外するコレクション
	 * @return	除外された場合は <tt>true</tt>、未登録の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public boolean unregisterTemporaryCollection(MongoCollection<Document> mcol) {
		return unregisterTemporaryCollection(mcol.getNamespace());
	}
	
	/**
	 * 指定された名前のコレクションを、一時的コレクションの登録から除外する。
	 * @param colName	除外するコレクションの名前
	 * @return	除外された場合は <tt>true</tt>、未登録の場合は <tt>false</tt>
	 * @since 0.992
	 */
	public boolean unregisterTemporaryCollection(MongoNamespace colName) {
		synchronized (_clientConnectionString) {
			return (_tempCollectionMap.remove(colName) != null);
		}
	}
	
	/**
	 * MongoDB サーバーからのエラー応答を保持していないかどうかを判定する。
	 * @return	エラー応答を一つも保持していない場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @since 0.992
	 */
	public boolean isServerErrorEmpty() {
		return _serverListener.isErrorEmpty();
	}
	
	/**
	 * MongoDB サーバーからのエラー応答を保持しているかどうかを判定する。
	 * @return	エラー応答を一つ以上保持している場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @since 0.992
	 */
	public boolean hasServerErrors() {
		return _serverListener.hasErrors();
	}
	
	/**
	 * このオブジェクトが保持している、MongoDB サーバーからのエラー応答をすべて破棄する。
	 * @since 0.992
	 */
	public void resetServerErrors() {
		_serverListener.clearErrors();
	}
	
	/**
	 * このオブジェクトが保持している、MongoDB サーバーからのエラー応答をすべて取得する。
	 * このメソッドでは、エラーは応答は除去されない。
	 * @return	すべてのエラー応答を格納する新しい配列、エラーが応答が一つもない場合は空の配列
	 * @since 0.992
	 */
	public Throwable[] getServerErrors() {
		return _serverListener.getErrors();
	}
	
	/**
	 * このオブジェクトが保持している、MongoDB サーバーからのエラー応答をすべて取得し、このオブジェクトから除去する。
	 * @return	すべてのエラー応答を格納する新しい配列、エラーが応答が一つもない場合は空の配列
	 * @since 0.992
	 */
	public Throwable[] getAndResetServerErrors() {
		return _serverListener.getAndClearErrors();
	}
	
	/**
	 * このオブジェクトが保持している、MongoDB サーバーからの最初のエラー応答を取得する。
	 * @return	最初のエラー応答を表す例外オブジェクト、存在しない場合は <tt>null</tt>
	 * @since 0.992
	 */
	public Throwable getFirstServerError() {
		return _serverListener.getFirstError();
	}
	
	/**
	 * このオブジェクトが保持している、MongoDB サーバーからの最後のエラー応答を取得する。
	 * @return	最後のエラー応答を表す例外オブジェクト、存在しない場合は <tt>null</tt>
	 * @since 0.992
	 */
	public Throwable getLastServerError() {
		return _serverListener.getLastError();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void dropAllTemporaryCollections() {
		for (Map.Entry<MongoNamespace, MongoCollection<Document>> entry : _tempCollectionMap.entrySet()) {
			try {
				entry.getValue().drop();
			} catch (Throwable ignoreEx) {}
		}
		_tempCollectionMap.clear();
	}
	
//	protected boolean ping(MongoDatabase mongodb) {
//		if (mongodb == null) {
//			throw new IllegalStateException("MongoDB session was closed by user.");
//		}
//		Document doc = null;
//		try {
//			doc = mongodb.runCommand(_cmdPing);
//			return true;
//		} catch (Throwable ignoreEx) {
//			// MongoDB is down...
//			return false;
//		} finally {
//			if (doc != null) {
//				doc.clear();
//			}
//		}
//	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	/**
	 * MongoDB サーバーの状態監視結果を受け取るリスナー。
	 * 
	 * @version 0.991
	 * @since 0.991
	 * 
	 * @author H.Deguchi(SOARS Project.)
	 * @author Y.Ishizuka(PieCake.inc,)
	 */
	protected class MongoServerListener implements ServerListener
	{
		protected final ReentrantLock	_lock = new ReentrantLock();
		/** 初回の MongoDB サーバーからのステータス変更通知を受領した場合にシグナル発行 **/
		protected final Condition		_condRecvFirstResponse = _lock.newCondition();

		protected long			_cntReceivedResponses = 0L;
		protected boolean		_opened    = false;
		protected boolean		_connected = false;
		protected boolean		_lastOk    = false;
		protected LinkedList<Throwable>	_errQueue = new LinkedList<Throwable>();
		
		/**
		 * サーバーからの最初の応答を受信するまで、指定された時間待機する。
		 * @param time	待機する時間
		 * @param unit	待機する時間の単位
		 * @return	待機時間内に応答があった場合は <tt>true</tt>、待機時間が経過した場合は <tt>false</tt>
		 * @throws InterruptedException	待機中に割込みが発生した場合
		 */
		public boolean awaitFirstServerResponse(long time, TimeUnit unit)
		throws InterruptedException
		{
			_lock.lock();
			try {
				if (_cntReceivedResponses == 0L) {
					// サーバーの応答を受け取るまで、指定時間待機
					return _condRecvFirstResponse.await(time, unit);
				}
				else {
					// 受信済みなので待機無し
					return true;
				}
			}
			finally {
				_lock.unlock();
			}
		}

		/**
		 * MongoDB サーバーとのセッションがオープンされたかどうかを判定する。
		 * セッションがオープンされていても、MongoDB サーバーに対するセッションが確立しているかどうかは不明なので、注意。
		 * @return	サーバーとのセッションがオープンされている場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
		 */
		public boolean isOpened() {
			_lock.lock();
			try {
				return _opened;
			}
			finally {
				_lock.unlock();
			}
		}
		
		/**
		 * MongoDB サーバーとのセッションが確立しているかどうかを判定する。
		 * @return	サーバーとのセッションが確立している場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
		 */
		public boolean isConnected() {
			_lock.lock();
			try {
				return _connected;
			}
			finally {
				_lock.unlock();
			}
		}
		
		/**
		 * 直前に取得したサーバーステータスが OK かどうかを判定する。
		 * @return	直前のサーバーステータスが OK の場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
		 */
		public boolean isLastOk() {
			_lock.lock();
			try {
				return _lastOk;
			}
			finally {
				_lock.unlock();
			}
		}
		
		/**
		 * MongoDB サーバーからのエラー応答を保持していないかどうかを判定する。
		 * @return	エラー応答を一つも保持していない場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
		 */
		public boolean isErrorEmpty() {
			_lock.lock();
			try {
				return _errQueue.isEmpty();
			}
			finally {
				_lock.unlock();
			}
		}
		
		/**
		 * MongoDB サーバーからのエラー応答を保持しているかどうかを判定する。
		 * @return	エラー応答を一つ以上保持している場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
		 */
		public boolean hasErrors() {
			_lock.lock();
			try {
				return !_errQueue.isEmpty();
			}
			finally {
				_lock.unlock();
			}
		}
		
		/**
		 * このオブジェクトが保持している、MongoDB サーバーからのエラー応答をすべて破棄する。
		 */
		public void clearErrors() {
			_lock.lock();
			try {
				_lastOk = false;
				_errQueue.clear();
			}
			finally {
				_lock.unlock();
			}
		}
		
		/**
		 * このオブジェクトが保持している、MongoDB サーバーからのエラー応答をすべて取得する。
		 * このメソッドでは、エラー応答は除去されない。
		 * @return	すべてのエラー応答を格納する新しい配列、エラーが応答が一つもない場合は空の配列
		 */
		public Throwable[] getErrors() {
			_lock.lock();
			try {
				if (_errQueue.isEmpty()) {
					return new Throwable[0];
				}
				else {
					return _errQueue.toArray(new Throwable[_errQueue.size()]);
				}
			}
			finally {
				_lock.unlock();
			}
		}
		
		/**
		 * このオブジェクトが保持している、MongoDB サーバーからのエラー応答をすべて取得し、このオブジェクトからすべて除去する。
		 * @return	すべてのエラー応答を格納する新しい配列、エラーが応答が一つもない場合は空の配列
		 */
		public Throwable[] getAndClearErrors() {
			Throwable[] result;
			_lock.lock();
			try {
				if (_errQueue.isEmpty()) {
					result = new Throwable[0];
				}
				else {
					result = _errQueue.toArray(new Throwable[_errQueue.size()]);
					_errQueue.clear();
				}
			}
			finally {
				_lock.unlock();
			}
			return result;
		}
		
		/**
		 * このオブジェクトが保持している、MongoDB サーバーからの最初のエラー応答を取得する。
		 * @return	最初のエラー応答を表す例外オブジェクト、存在しない場合は <tt>null</tt>
		 */
		public Throwable getFirstError() {
			_lock.lock();
			try {
				return _errQueue.peekFirst();
			}
			finally {
				_lock.unlock();
			}
		}
		
		/**
		 * このオブジェクトが保持している、MongoDB サーバーからの最後のエラー応答を取得する。
		 * @return	最後のエラー応答を表す例外オブジェクト、存在しない場合は <tt>null</tt>
		 */
		public Throwable getLastError() {
			_lock.lock();
			try {
				return _errQueue.peekLast();
			}
			finally {
				_lock.unlock();
			}
		}
		
		@Override
		public void serverOpening(ServerOpeningEvent event) {
			_lock.lock();
			try {
				_opened = true;
			}
			finally {
				_lock.unlock();
			}
			//--- call handler
			MongoSessionHandler handler = _atomicHandler.get();
			if (handler != null) {
				handler.serverOpened(MongoSession.this);
			}
		}
		@Override
		public void serverClosed(ServerClosedEvent event) {
			_lock.lock();
			try {
				_cntReceivedResponses = 0L;
				_opened = false;
				_connected = false;
			}
			finally {
				_lock.unlock();
			}
			//--- call handler
			MongoSessionHandler handler = _atomicHandler.get();
			if (handler != null) {
				handler.serverClosed(MongoSession.this);
			}
		}
		@Override
		public void serverDescriptionChanged(ServerDescriptionChangedEvent event) {
			ServerDescription sd = event.getNewDescription();
			boolean nowConnected = ServerConnectionState.CONNECTED.equals(sd.getState());
			boolean oldConnected;
			Throwable thrown = sd.getException();
			_lock.lock();
			try {
				oldConnected = _connected;
				_connected = nowConnected;
				if (thrown != null) {
					_errQueue.add(thrown);
				}
				if (_cntReceivedResponses == 0L) {
					_cntReceivedResponses++;
					_condRecvFirstResponse.signal();
				}
				else {
					_cntReceivedResponses++;
				}
			}
			finally {
				_lock.unlock();
			}
			if (oldConnected != nowConnected) {
				// status changed
				MongoSessionHandler handler = _atomicHandler.get();
				if (handler != null) {
					if (nowConnected) {
						// 接続確立
						handler.serverConnected(MongoSession.this);
					}
					else {
						// 予期せぬ切断
						handler.serverLostConnection(MongoSession.this, thrown);
					}
				}
			}
		}
	}
	
	/**
	 * シャットダウンフック
	 * 
	 * @version 0.991
	 * @since 0.991
	 * 
	 * @author H.Deguchi(SOARS Project.)
 	 * @author Y.Ishizuka(PieCake.inc,)
	 */
	static protected class MongoSessionShutdownHook extends Thread
	{
		public MongoSessionShutdownHook() {
			super("MongoSessionShutdownHook");
		}
		
		@Override
		public void run() {
			cleanupAllSessions();
		}
	}
}
