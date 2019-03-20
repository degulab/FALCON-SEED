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
 * @(#)MqttManager.java	0.3.0	2013/06/30
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MqttManager.java	0.2.0	2013/05/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MqttManager.java	0.1.0	2013/03/07
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.mqtt.internal;

import java.util.HashSet;
import java.util.Iterator;

import ssac.aadl.runtime.mqtt.MqttSession;

/**
 * <code>MQTTSession</code> インスタンス、
 * <code>MQTTPublisher</code> インスタンス、
 * <code>MQTTSubscriber</code> インスタンスを保持するクラス。
 * <p>このクラスは、AADL実行モジュールのインスタンスでオープンされた
 * セッションのインスタンスを保持し、AADL実行モジュール終了時に
 * 切断されていないセッションを切断するために使用する。
 * 
 * @version 0.3.0	2013/06/30
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
public class MqttManager extends Thread
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static private MqttManager	_instManager = null;
	
	protected final Object _mutex = new Object();
	
	protected final HashSet<MqttSession>	_sessionSet;
	
	volatile protected boolean	_nowCleanUp = false;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	static public final MqttManager getInstance() {
		if (_instManager == null) {
			_instManager = new MqttManager();
			Runtime.getRuntime().addShutdownHook(_instManager);
		}
		return _instManager;
	}
	
	protected MqttManager() {
		_sessionSet = new HashSet<MqttSession>();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

//	/**
//	 * 指定された接続パラメータでサーバーに接続し、新しいセッションを開始する。
//	 * <em>serverURI</em> にポート番号が含まれていない場合、1883 番ポートが指定される。
//	 * <em>serverURI</em> にプロトコルが指定されていない場合、&quot;tcp://&quot; が指定される。
//	 * <em>serverURI</em> が <tt>null</tt> もしくは空文字の場合、ローカルIPアドレスが指定される。
//	 * <p>
//	 * このメソッドでは、接続に失敗した場合でも、MqttSession オブジェクトを返す。接続に失敗した場合、
//	 * {@link ssac.aadl.runtime.mqtt.MqttSession#isConnected()} は <tt>false</tt> を返し、
//	 * {@link ssac.aadl.runtime.mqtt.MqttSession#isError()} は <tt>true</tt> を返す。
//	 * @param serverURI	接続先を示す URI
//	 * @return	<code>MqttManagedSession</code> オブジェクト
//	 * @throws IllegalArgumentException	<em>serverURI</em> に含まれるスキーマがサポートされていない場合
//	 * @throws MqttRuntimeException	接続できなかった場合
//	 */
//	static public MqttManagedSession connect(String serverURI) {
//		return connect(new MqttConnectionParams(serverURI));
//	}
//
//	/**
//	 * 指定された接続パラメータでサーバーに接続し、新しいセッションを開始する。
//	 * <em>serverURI</em> にポート番号が含まれている場合は <em>portNo</em> は無視される。
//	 * ポート番号が含まれていない場合は 1883 番ポートが <em>serverURI</em> の終端に付加される。
//	 * <em>serverURI</em> にプロトコルが指定されていない場合、&quot;tcp://&quot; が指定される。
//	 * <em>serverURI</em> が <tt>null</tt> もしくは空文字の場合、ローカルIPアドレスが指定される。
//	 * <p>
//	 * このメソッドでは、接続に失敗した場合でも、MqttSession オブジェクトを返す。接続に失敗した場合、
//	 * {@link ssac.aadl.runtime.mqtt.MqttSession#isConnected()} は <tt>false</tt> を返し、
//	 * {@link ssac.aadl.runtime.mqtt.MqttSession#isError()} は <tt>true</tt> を返す。
//	 * @param serverURI	接続先を示す URI
//	 * @param portNo	接続先ポート番号(0～65535)
//	 * @return	<code>MqttManagedSession</code> オブジェクト
//	 * @throws IllegalArgumentException	<em>serverURI</em> に含まれるスキーマがサポートされていない場合、または <em>portNo</em> が範囲外の場合
//	 * @throws MqttRuntimeException	接続できなかった場合
//	 */
//	static public MqttManagedSession connect(String serverURI, int portNo) {
//		return connect(new MqttConnectionParams(serverURI, portNo));
//	}
//
//	/**
//	 * 指定された接続パラメータでサーバーに接続し、新しいセッションを開始する。
//	 * <em>serverURI</em> にポート番号が含まれていない場合、1883 番ポートが指定される。
//	 * <em>serverURI</em> にプロトコルが指定されていない場合、&quot;tcp://&quot; が指定される。
//	 * <em>serverURI</em> が <tt>null</tt> もしくは空文字の場合、ローカルIPアドレスが指定される。
//	 * <em>clientID</em> が <tt>null</tt> もしくは空文字の場合、ランダムなクライアントIDが設定される。
//	 * <p>
//	 * このメソッドでは、接続に失敗した場合でも、MqttSession オブジェクトを返す。接続に失敗した場合、
//	 * {@link ssac.aadl.runtime.mqtt.MqttSession#isConnected()} は <tt>false</tt> を返し、
//	 * {@link ssac.aadl.runtime.mqtt.MqttSession#isError()} は <tt>true</tt> を返す。
//	 * @param serverURI	接続先を示す URI
//	 * @param clientID	23文字以下のクライアントID
//	 * @return	<code>MqttManagedSession</code> オブジェクト
//	 * @throws IllegalArgumentException	<em>serverURI</em> に含まれるスキーマがサポートされていない場合、または <em>clientID</em> が 23 文字よりも大きい場合
//	 * @throws MqttRuntimeException	接続できなかった場合
//	 */
//	static public MqttManagedSession connect(String serverURI, String clientID) {
//		return connect(new MqttConnectionParams(serverURI, clientID));
//	}
//
//	/**
//	 * 指定された接続パラメータでサーバーに接続し、新しいセッションを開始する。
//	 * <em>serverURI</em> にポート番号が含まれている場合は <em>portNo</em> は無視される。
//	 * ポート番号が含まれていない場合は 1883 番ポートが <em>serverURI</em> の終端に付加される。
//	 * <em>serverURI</em> にプロトコルが指定されていない場合、&quot;tcp://&quot; が指定される。
//	 * <em>serverURI</em> が <tt>null</tt> もしくは空文字の場合、ローカルIPアドレスが指定される。
//	 * <em>clientID</em> が <tt>null</tt> もしくは空文字の場合、ランダムなクライアントIDが設定される。
//	 * <p>
//	 * このメソッドでは、接続に失敗した場合でも、MqttSession オブジェクトを返す。接続に失敗した場合、
//	 * {@link ssac.aadl.runtime.mqtt.MqttSession#isConnected()} は <tt>false</tt> を返し、
//	 * {@link ssac.aadl.runtime.mqtt.MqttSession#isError()} は <tt>true</tt> を返す。
//	 * @param serverURI	接続先を示す URI
//	 * @param portNo	接続先ポート番号(0～65535)
//	 * @param clientID	23文字以下のクライアントID
//	 * @return	<code>MqttManagedSession</code> オブジェクト
//	 * @throws IllegalArgumentException	<em>serverURI</em> に含まれるスキーマがサポートされていない場合、または <em>portNo</em> が範囲外の場合、
//	 * 									または <em>clientID</em> が 23 文字よりも大きい場合
//	 * @throws MqttRuntimeException	接続できなかった場合
//	 */
//	static public MqttManagedSession connect(String serverURI, int portNo, String clientID) {
//		return connect(new MqttConnectionParams(serverURI, portNo, clientID));
//	}

//	/**
//	 * 指定された接続パラメータでサーバーに接続し、新しいセッションを開始する。
//	 * <p>
//	 * このメソッドでは、接続に失敗した場合でも、MqttSession オブジェクトを返す。接続に失敗した場合、
//	 * {@link ssac.aadl.runtime.mqtt.MqttSession#isConnected()} は <tt>false</tt> を返し、
//	 * {@link ssac.aadl.runtime.mqtt.MqttSession#isError()} は <tt>true</tt> を返す。
//	 * @param params	接続パラメータ
//	 * @return	<code>MqttManagedSession</code> オブジェクト
//	 * @throws NullPointerException	<em>params</em> が <tt>null</tt> の場合
//	 * @throws MqttRuntimeException	接続できなかった場合
//	 */
//	static public MqttSession connect(MqttConnectionParams params) {
//		MqttSessionImpl session = new MqttSessionImpl(params);
//		session.setController(_instController);
//		session.connect();
//		return session;
//		
////		MqttManager manager = getInstance();
////		MqttManagedSession session = new MqttManagedSession(manager, params);
////		session.connect();
////		return session;
//	}

	/**
	 * このオブジェクトが保持するすべてのセッションの接続を切断し、セッションオブジェクトを破棄する。
	 */
	public void cleanup() {
		HashSet<MqttSession> set = null;
		synchronized (_mutex) {
			_nowCleanUp = true;
			set = new HashSet<MqttSession>(_sessionSet);
			_sessionSet.clear();
		}
		
		if (set != null && !set.isEmpty()) {
			Iterator<MqttSession> rit = set.iterator();
			while (rit.hasNext()) {
				MqttSession session = rit.next();
				try {
					session.disconnect();
				} catch (Throwable ignoreEx) { ignoreEx=null; }
				rit.remove();
			}
		}
		
		synchronized (_mutex) {
			_nowCleanUp = false;
		}
	}

	/**
	 * このオブジェクトに新しいセッションオブジェクトを追加する。
	 * @param session	追加するセッションオブジェクト
	 * @return	追加された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean add(MqttSession session) {
		if (session != null) {
			synchronized (_mutex) {
				if (!_nowCleanUp) {
					return _sessionSet.add(session);
				}
			}
		}
		return false;
	}

	/**
	 * このオブジェクトから、指定されたセッションオブジェクトを除外する。
	 * @param session	除外するセッションオブジェクト
	 * @return	除外された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean remove(MqttSession session) {
		if (session != null) {
			synchronized (_mutex) {
				if (!_nowCleanUp) {
					return _sessionSet.remove(session);
				}
			}
		}
		return false;
	}

	//------------------------------------------------------------
	// Public helper
	//------------------------------------------------------------

	//------------------------------------------------------------
	// java.lang.Runnable interfaces
	//------------------------------------------------------------

	/**
	 * shutdown hook
	 */
	@Override
	public void run() {
		cleanup();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
