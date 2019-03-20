/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  Copyright 2007-2013  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)MqttConnectionParams.java	0.4.0	2014/05/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MqttConnectionParams.java	0.3.0	2013/06/28
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MqttConnectionParams.java	0.2.0	2013/05/15
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MqttConnectionParams.java	0.1.0	2013/03/05
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.mqtt;

import java.net.URI;

import org.fusesource.mqtt.client.MqttConnectOptions;

/**
 * MQTT クライアントのサーバー接続時のパラメータ設定。
 * 
 * @version 0.4.0	2014/05/29
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
public class MqttConnectionParams
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	static protected final String SCHEME_SEP	= "://";
	static protected final String SCHEME_TCP	= "tcp://";
	//static protected final String SCHEME_SSL	= "ssl://";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** MQTT コネクションオプション **/
	private final MqttConnectOptions	_mqttOptions;
//	/** 接続先のURI **/
//	private String	_serverUri;
//	/** 接続時のクライアントID **/
//	private String	_clientId;
//	/** メッセージ永続化時の格納ディレクトリ **/
//	private String	_persistDir;
//	/** メッセージ永続化を行う場合は <tt>true</tt> **/
//	private boolean	_usePersistence = false;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * デフォルトの接続先とクライアントIDで、新しいインスタンスを生成する。
	 */
	public MqttConnectionParams() {
		this(null, MqttUtil.DEFAULT_PORT_NUMBER, null);
	}

	/**
	 * 指定されたサーバーURIとデフォルトのクライアントIDで、新しいインスタンスを生成する。
	 * <em>serverURI</em> にポート番号が含まれていない場合、1883 番ポートが指定される。
	 * <em>serverURI</em> にプロトコルが指定されていない場合、&quot;tcp://&quot; が指定される。
	 * <em>serverURI</em> が <tt>null</tt> もしくは空文字の場合、ローカルIPアドレスが指定される。
	 * @param serverURI	接続先を示す URI
	 * @throws IllegalArgumentException	<em>serverURI</em> に含まれるスキーマがサポートされていない場合
	 */
	public MqttConnectionParams(String serverURI) {
		this(serverURI, MqttUtil.DEFAULT_PORT_NUMBER, null);
	}

	/**
	 * 指定されたサーバーURIとデフォルトのクライアントIDで、新しいインスタンスを生成する。
	 * <em>serverURI</em> にポート番号が含まれている場合は <em>portNo</em> は無視される。
	 * ポート番号が含まれていない場合は 1883 番ポートが <em>serverURI</em> の終端に付加される。
	 * <em>serverURI</em> にプロトコルが指定されていない場合、&quot;tcp://&quot; が指定される。
	 * <em>serverURI</em> が <tt>null</tt> もしくは空文字の場合、ローカルIPアドレスが指定される。
	 * @param serverURI	接続先を示す URI
	 * @param portNo	接続先ポート番号(0～65535)
	 * @throws IllegalArgumentException	<em>serverURI</em> に含まれるスキーマがサポートされていない場合、または <em>portNo</em> が範囲外の場合
	 */
	public MqttConnectionParams(String serverURI, int portNo) {
		this(serverURI, portNo, null);
	}

	/**
	 * 指定されたサーバーURIとクライアントIDで、新しいインスタンスを生成する。
	 * <em>serverURI</em> にポート番号が含まれていない場合、1883 番ポートが指定される。
	 * <em>serverURI</em> にプロトコルが指定されていない場合、&quot;tcp://&quot; が指定される。
	 * <em>serverURI</em> が <tt>null</tt> もしくは空文字の場合、ローカルIPアドレスが指定される。
	 * <em>clientID</em> が <tt>null</tt> もしくは空文字の場合、ランダムなクライアントIDが設定される。
	 * @param serverURI	接続先を示す URI
	 * @param clientID	23文字以下のクライアントID
	 * @throws IllegalArgumentException	<em>serverURI</em> に含まれるスキーマがサポートされていない場合、または <em>clientID</em> が 23 文字よりも大きい場合
	 */
	public MqttConnectionParams(String serverURI, String clientID) {
		this(serverURI, MqttUtil.DEFAULT_PORT_NUMBER, clientID);
	}

	/**
	 * 指定されたサーバーURIとクライアントIDで、新しいインスタンスを生成する。
	 * <em>serverURI</em> にポート番号が含まれている場合は <em>portNo</em> は無視される。
	 * ポート番号が含まれていない場合は 1883 番ポートが <em>serverURI</em> の終端に付加される。
	 * <em>serverURI</em> にプロトコルが指定されていない場合、&quot;tcp://&quot; が指定される。
	 * <em>serverURI</em> が <tt>null</tt> もしくは空文字の場合、ローカルIPアドレスが指定される。
	 * <em>clientID</em> が <tt>null</tt> もしくは空文字の場合、ランダムなクライアントIDが設定される。
	 * @param serverURI	接続先を示す URI
	 * @param portNo	接続先ポート番号(0～65535)
	 * @param clientID	23文字以下のクライアントID
	 * @throws IllegalArgumentException	<em>serverURI</em> に含まれるスキーマがサポートされていない場合、または <em>portNo</em> が範囲外の場合、
	 * 									または <em>clientID</em> が 23 文字よりも大きい場合
	 */
	public MqttConnectionParams(String serverURI, int portNo, String clientID) {
		this._mqttOptions = new MqttConnectOptions();
		setServerURI(serverURI, portNo);
		setClientID(clientID);
//		this._persistDir= getDefaultPersistenceDirectory();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * <code>MQTT</code> 接続先サーバーを示す文字列から、<code>URI</code> に変換する。
	 * このメソッドでは、<code>MQTT</code> がサポートするスキーマかどうかも判定する。
	 * @param uri	接続先サーバーを示す文字列
	 * @return	<code>URI</code> オブジェクト
	 * @throws NullPointerException	<em>uri</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>uri</em> に含まれるスキーマがサポートされていない場合、または <em>uri</em> に含まれるポート番号が範囲外の場合
	 * @since 0.4.0
	 */
	static public URI parseServerURI(String uri) {
		if (uri.length() <= 0)
			throw new IllegalArgumentException("Server URI is empty!");
		
		URI serverURI;
		try {
			if (uri.indexOf(SCHEME_SEP) < 0) {
				// no scheme, uri is hostname or IP address
				uri = SCHEME_TCP + uri;
			}
			serverURI = new URI(uri);
		}
		catch (Throwable ex) {
			throw new IllegalArgumentException("Server URI is illegal : \"" + uri + "\"", ex);
		}
		
		// check Scheme
		String uriScheme = serverURI.getScheme();
		if (uriScheme == null) {
			// no scheme
			throw new IllegalArgumentException("Server URI has no scheme : \"" + uri + "\"");
		}
		else if (!"tcp".equals(uriScheme) && org.fusesource.hawtdispatch.transport.SslTransport.protocol(uriScheme)==null) {
			// unsupported shceme
			throw new IllegalArgumentException("Unsupported Server URI scheme : \"" + uri + "\"");
		}
		
		// check port number
		int uriPortNo = serverURI.getPort();
		if (uriPortNo >= 0) {
			// check specified port number
			validPortNo(uriPortNo);
		}
		else {
			// no port number, set port number
			//--- insert port no
			String orgURI = serverURI.toString();
			String strAuth = uriScheme + SCHEME_SEP + serverURI.getRawAuthority();
			if (strAuth.endsWith(":")) {
				uri = strAuth + String.valueOf(MqttUtil.DEFAULT_PORT_NUMBER) + orgURI.substring(strAuth.length());
			} else {
				uri = strAuth + ":" + String.valueOf(MqttUtil.DEFAULT_PORT_NUMBER) + orgURI.substring(strAuth.length());
			}
			try {
				serverURI = new URI(uri);
			}
			catch (Throwable ex) {
				throw new IllegalArgumentException("Server URI is illegal : \"" + uri + "\"", ex);
			}
		}
		
		return serverURI;
	}
	
	/**
	 * 接続先サーバーの URI を取得する。
	 * @return	サーバー URI
	 */
	public String getServerURI() {
		URI uri = _mqttOptions.getHost();
		return (uri==null ? "" : uri.toString());
	}

	/**
	 * 接続先サーバーの URI を設定する。
	 * <em>uri</em> にポート番号が含まれていない場合、1883 番ポートが指定される。
	 * <em>uri</em> にプロトコルが指定されていない場合、&quot;tcp://&quot; が指定される。
	 * <em>uri</em> が <tt>null</tt> もしくは空文字の場合、ローカルIPアドレスが指定される。
	 * @param uri	接続先サーバーを示す URI
	 * @throws IllegalArgumentException	<em>uri</em> に含まれるスキーマがサポートされていない場合
	 */
	public void setServerURI(String uri) {
		setServerURI(uri, MqttUtil.DEFAULT_PORT_NUMBER);
	}

	/**
	 * 
	 * 接続先サーバーの URI を設定する。
	 * <em>uri</em> にポート番号が含まれている場合は <em>portNo</em> は無視される。
	 * ポート番号が含まれていない場合は 1883 番ポートが <em>uri</em> の終端に付加される。
	 * <em>uri</em> にプロトコルが指定されていない場合、&quot;tcp://&quot; が指定される。
	 * <em>uri</em> が <tt>null</tt> もしくは空文字の場合、ローカルIPアドレスが指定される。
	 * @param uri	接続先サーバーを示す URI
	 * @param portNo	接続先ポート番号(0～65535)
	 * @throws IllegalArgumentException	<em>uri</em> に含まれるスキーマがサポートされていない場合、または <em>portNo</em> が範囲外の場合
	 */
	public void setServerURI(String uri, int portNo) {
		URI serverURI;
		if (uri != null && uri.length() > 0) {
			try {
				if (uri.indexOf(SCHEME_SEP) < 0) {
					// no scheme, uri is hostname or IP address
					uri = SCHEME_TCP + uri;
				}
				serverURI = new URI(uri);
			}
			catch (Throwable ex) {
				throw new IllegalArgumentException("Server URI is illegal : \"" + uri + "\"", ex);
			}
			
			// check Scheme
			String uriScheme = serverURI.getScheme();
			if (uriScheme == null) {
				// no scheme
				throw new IllegalArgumentException("Server URI has no scheme : \"" + uri + "\"");
			}
			else if (!"tcp".equals(uriScheme) && org.fusesource.hawtdispatch.transport.SslTransport.protocol(uriScheme)==null) {
				// unsupported shceme
				throw new IllegalArgumentException("Unsupported Server URI scheme : \"" + uri + "\"");
			}
			
			// check port number
			int uriPortNo = serverURI.getPort();
			if (uriPortNo < 0) {
				// no port number, set port number
				validPortNo(portNo);
				//--- insert port no
				String orgURI = serverURI.toString();
				String strAuth = uriScheme + SCHEME_SEP + serverURI.getRawAuthority();
				uri = strAuth + ":" + String.valueOf(portNo) + orgURI.substring(strAuth.length());
				try {
					serverURI = new URI(uri);
				}
				catch (Throwable ex) {
					throw new IllegalArgumentException("Server URI is illegal : \"" + uri + "\"", ex);
				}
			} else {
				// check specified port number
				validPortNo(uriPortNo);
			}
		}
		else {
			// create default URI
			validPortNo(portNo);
			uri = SCHEME_TCP + MqttUtil.DEFAULT_IP_ADDRESS + ":" + portNo;
			try {
				serverURI = new URI(uri);
			}
			catch (Throwable ex) {
				throw new IllegalArgumentException("Server URI is illegal : \"" + uri + "\"", ex);
			}
		}
		
		// set new URI
		_mqttOptions.setHost(serverURI);
	}

	/**
	 * 指定された文字列がクライアントIDとして適切でない場合は、例外をスローする。
	 * @param clientID	チェックするクライアントID
	 * @throws IllegalArgumentException	クライアントIDとして適切ではない場合
	 */
	static public void validClientID(String clientID) {
		if (clientID == null || clientID.length() <= 0) {
			throw new IllegalArgumentException("No client ID!");
		}
		else if (clientID.length() > MqttUtil.MAX_CLIENTID_LENGTH) {
			throw new IllegalArgumentException("Client ID is greater than " + MqttUtil.MAX_CLIENTID_LENGTH + " characters : " + clientID);
		}
	}

	/**
	 * 現在設定されているクライアントIDを返す。
	 * @return	クライアントID
	 */
	public String getClientID() {
		return _mqttOptions.getClientId().toString();
	}

	/**
	 * クライアントIDを設定する。
	 * なお、<em>clientID</em> が <tt>null</tt> もしくは空文字の場合、
	 * {@link ssac.aadl.runtime.mqtt.MqttUtil#generateClientID()} が返す文字列がクライアントIDとして設定される。
	 * @param clientID	23文字以下のクライアントID
	 * @throws IllegalArgumentException	<em>clientID</em> が 23 文字よりも大きい場合
	 */
	public void setClientID(String clientID) {
		if (clientID != null && clientID.length() > 0) {
			validClientID(clientID);
		} else {
			clientID = MqttUtil.generateClientID();
		}
		_mqttOptions.setClientId(clientID);
	}

	/**
	 * <code>MqttConnectOptions</code> インスタンスを取得する。
	 * @return	<code>MqttConnectOptions</code> オブジェクト
	 */
	public MqttConnectOptions getOptions() {
		return _mqttOptions;
	}

//	/**
//	 * 現在のメッセージ永続化の有効／無効を取得する。
//	 * @return	永続化有効なら <tt>true</tt>、それ以外なら <tt>false</tt>
//	 */
//	public boolean getUsePersistence() {
//		return _usePersistence;
//	}

//	/**
//	 * メッセージ永続化の有効／無効を設定する。
//	 * @param toUse	永続化する場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
//	 */
//	public void setUsePersistence(boolean toUse) {
//		_usePersistence = toUse;
//	}

//	/**
//	 * メッセージ永続化時にデータを格納するデフォルト・ディレクトリを示すパスを返す。
//	 * @return	ディレクトリを示す文字列
//	 */
//	static public String getDefaultPersistenceDirectory() {
//		return System.getProperty("java.io.tmpdir");
//	}

//	/**
//	 * メッセージ永続化時にデータを格納するディレクトリを取得する。
//	 * @return	ディレクトリを示すパス文字列
//	 */
//	public String getPersistenceDirectory() {
//		return _persistDir;
//	}

//	/**
//	 * メッセージ永続化時にデータを格納するディレクトリを設定する。
//	 * <em>dir</em> が <tt>null</tt> もしくは空文字の場合は、{@link #getDefaultPersistenceDirectory()} が返す
//	 * パスが設定される。
//	 * @param dir	ディレクトリを示すパス文字列
//	 */
//	public void setPersistenceDirectory(String dir) {
//		if (dir != null && dir.length() > 0) {
//			_persistDir = dir;
//		} else {
//			_persistDir = getDefaultPersistenceDirectory();
//		}
//	}

	/**
	 * 再接続時にセッションの状態をクリアするかどうかの設定を取得する。
	 * @return	クリアする場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isCleanSession() {
		return _mqttOptions.isCleanSession();
	}

	/**
	 * 再接続時にセッションの状態を維持するかどうかを設定する。
	 * @param cleanSession	クリアする場合は <tt>true</tt>、維持する場合は <tt>false</tt>
	 */
	public void setCleanSession(boolean cleanSession) {
		_mqttOptions.setCleanSession(cleanSession);
	}

	/**
	 * 接続時のタイムアウト時間を返す。デフォルトは 30 秒。
	 * このタイムアウト時間は、サーバーへの接続、サブスクライブ、アンサブスクライブの
	 * リクエスト完了までのタイムアウトを指定する。
	 * @return	タイムアウト時間[秒]
	 */
	public int getConnectionTimeout() {
		return _mqttOptions.getConnectionTimeout();
	}

	/**
	 * 接続時のタイムアウト時間を設定する。デフォルトは 30 秒。
	 * このタイムアウト時間は、サーバーへの接続、サブスクライブ、アンサブスクライブの
	 * リクエスト完了までのタイムアウトを指定する。
	 * @param timeout	設定するタイムアウト時間[秒]
	 */
	public void setConnectionTimeout(int timeout) {
		_mqttOptions.setConnectionTimeout(timeout);
	}

	/**
	 * キープアライブ間隔を返す。デフォルトは 60 秒。
	 * この時間間隔は、送信もしくは受信したメッセージ間の最大時間間隔を定義する。
	 * @return	キープアライブ間隔［秒］
	 */
	public int getKeepAliveInterval() {
		return _mqttOptions.getKeepAliveInterval();
	}

	/**
	 * キープアライブ間隔を設定する。デフォルトは 60 秒。
	 * この時間間隔は、送信もしくは受信したメッセージ間の最大時間間隔を定義する。
	 * @param interval	設定するキープアライブ間隔［秒］
	 */
	public void setKeepAliveInterval(int interval) {
		_mqttOptions.setKeepAliveInterval(interval);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static protected void validPortNo(int portNo) {
		if (portNo < 0 || portNo > 65535) {
			throw new IllegalArgumentException("Port number is Out of range : " + portNo);
		}
	}
}
