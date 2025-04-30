/*
 * @(#)MongoServerURI.java	0.991	2019/02/23
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package redundantalge.db.mongo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mongodb.ConnectionString;

import redundantalge.db.DbServerAddress;
import redundantalge.db.DbServerURI;

/**
 * MongoDB をバックエンドとする大容量代数オブジェクトの場所に関するインタフェース。
 * <p>このオブジェクトの実装は、不変オブジェクトとする。
 * 
 * @version 0.991
 * @since 0.991
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public class MongoServerURI implements DbServerURI
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** MongoDB 接続文字列オブジェクト(フル) **/
	protected ConnectionString		_fullConnectionString;
	/** MongoDB 接続文字列オブジェクト(クライアント) **/
	protected ConnectionString		_clientConnectionString;
	/** コレクション名 **/
	protected String				_targetCollection;
	/** サーバーアドレスの変更不可能なリスト **/
	protected List<DbServerAddress>	_serverAddrList;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/**
	 * 指定されたパラメータで、新しいインスタンスを生成する。
	 * @param uri	ストレージの場所を表す文字列
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	ストレージの場所を示す URI として不適切な場合
	 */
	public MongoServerURI(String uri) {
		_fullConnectionString = new ConnectionString(uri);
		_targetCollection = _fullConnectionString.getCollection();
		_clientConnectionString = MongoUtil.reduceCollectionNameFrom(_fullConnectionString);
	}
	
	/**
	 * 指定されたパラメータで、認証なしの MongoDB 接続先を示す文字列を生成する。
	 * このメソッドは、接続文字列の正当性については考慮しない。
	 * @param host			接続先ホスト名、<tt>null</tt> を指定した場合はローカルホストを示す IP アドレス
	 * @param port			接続先ポート番号、負の値を指定した場合はデフォルトのポート番号
	 * @param useDbName		交換代数を格納するデータベース名、<tt>null</tt> を指定した場合はデータベースなし
	 * @throws	IllegalArgumentException	接続先情報が正しくない場合
	 */
	public MongoServerURI(String host, int port, String useDbName) {
		this(host, port, null, null, null, useDbName);
	}
	
	/**
	 * 指定されたパラメータで、MongoDB 接続先を示す文字列を生成する。
	 * このメソッドは、接続文字列の正当性については考慮しない。
	 * @param host			接続先ホスト名、<tt>null</tt> を指定した場合はローカルホストを示す IP アドレス
	 * @param port			接続先ポート番号、負の値を指定した場合はデフォルトのポート番号
	 * @param authDbName	認証データベース名、<tt>null</tt> を指定した場合は 'admin'
	 * @param authUser		認証ユーザー名、<tt>null</tt> を指定した場合は認証なし
	 * @param authPass		認証パスワード、<tt>null</tt> を指定した場合はパスワードなし
	 * @param useDbName		交換代数を格納するデータベース名、<tt>null</tt> を指定した場合はデータベースなし
	 * @throws	IllegalArgumentException	接続先情報が正しくない場合
	 */
	public MongoServerURI(String host, int port, String authDbName, String authUser, String authPass, String useDbName) {
		this(host, port, authDbName, authUser, authPass, useDbName, null);
	}
	
	/**
	 * 指定されたパラメータで、MongoDB 接続先を示す文字列を生成する。
	 * このメソッドは、接続文字列の正当性については考慮しない。
	 * @param host			接続先ホスト名、<tt>null</tt> を指定した場合はローカルホストを示す IP アドレス
	 * @param port			接続先ポート番号、負の値を指定した場合はデフォルトのポート番号
	 * @param authDbName	認証データベース名、<tt>null</tt> を指定した場合は 'admin'
	 * @param authUser		認証ユーザー名、<tt>null</tt> を指定した場合は認証なし
	 * @param authPass		認証パスワード、<tt>null</tt> を指定した場合はパスワードなし
	 * @param useDbName		ストレージとするデータベース名、<tt>null</tt> を指定した場合はデータベースなし
	 * @param colName		ストレージとするコレクション名、<tt>null</tt> を指定した場合はコレクションなし
	 * @throws	IllegalArgumentException	接続先情報が正しくない場合
	 */
	public MongoServerURI(String host, int port, String authDbName, String authUser, String authPass, String useDbName, String colName) {
		_fullConnectionString = MongoUtil.makeConnectionString(host.toLowerCase(), port, authDbName, authUser, authPass, useDbName, colName);
		_targetCollection = _fullConnectionString.getCollection();
		_clientConnectionString = MongoUtil.reduceCollectionNameFrom(_fullConnectionString);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * このオブジェクトの設定されているスキーマを返す。
	 * @return	スキーマを表す文字列
	 */
	public String getScheme() {
		return MongoUtil.MONGO_SCHEMA;
	}
	
	/**
	 * このオブジェクトを生成したときの、完全な接続文字列オブジェクトを取得する。
	 * @return	完全な接続文字列オブジェクト
	 */
	public ConnectionString getFullConnectionString() {
		return _fullConnectionString;
	}
	
	/**
	 * MongoDB クライアント生成のための、データベース名とコレクション名を除いた接続文字列オブジェクトを取得する。
	 * @return	クライアント用接続文字列オブジェクト
	 */
	public ConnectionString getClientConnectionString() {
		return _clientConnectionString;
	}
	
	/**
	 * 接続先データベースのアドレスリストを返す。
	 * @return	接続先データベースアドレスのリスト
	 */
	public List<DbServerAddress> getHosts() {
		if (_serverAddrList == null) {
			List<String> strlist = _fullConnectionString.getHosts();
			ArrayList<DbServerAddress> addrList = new ArrayList<DbServerAddress>(strlist.size());
			for (String str : strlist) {
				addrList.add(DbServerAddress.parse(str, MongoUtil.MONGO_DEFAULT_PORT));
			}
			_serverAddrList = Collections.unmodifiableList(addrList);
		}
		return _serverAddrList;
	}

	/**
	 * 接続先データベースのデータベース名を返す。
	 * @return	データベース名、指定されていない場合は <tt>null</tt>
	 */
	@Override
	public String getDatabaseName() {
		return _fullConnectionString.getDatabase();
	}
	
	/**
	 * 接続先データベースのコレクション名を返す。
	 * @return	コレクション名、指定されていない場合は <tt>null</tt>
	 */
	public String getCollectionName() {
		return _targetCollection;
	}

	/**
	 * 接続時のユーザー名を返す。
	 * @return	接続ユーザー名、設定されていない場合は <tt>null</tt>
	 */
	@Override
	public String getUsername() {
		return _clientConnectionString.getUsername();
	}

	/**
	 * 接続時のパスワードを返す。
	 * @return	接続パスワード、設定されていない場合は <tt>null</tt>
	 */
	@Override
	public char[] getPassword() {
		return _clientConnectionString.getPassword();
	}

	//------------------------------------------------------------
	// Implements Object interfaces
	//------------------------------------------------------------

	/**
	 * このオブジェクトのハッシュ値を返す。
	 * @return	ハッシュ値
	 */
	@Override
	public int hashCode() {
		return _fullConnectionString.hashCode();
	}

	/**
	 * 指定されたオブジェクトと自身の内容が等しいかを判定する。
	 * @param obj	判定するオブジェクト
	 * @return	等しい場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		
		if (obj == null || !this.getClass().equals(obj.getClass())) {
			return false;
		}
		
		MongoServerURI that = (MongoServerURI)obj;
		return this._fullConnectionString.equals(that._fullConnectionString);
	}

	/**
	 * このオブジェクトが表す URI 文字列を返す。
	 * @return	このオブジェクトが表す URI 文字列
	 */
	@Override
	public String toString() {
		return _fullConnectionString.getConnectionString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
