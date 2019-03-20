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
 * @(#)MqClient.java	0.3.1	2013/07/05
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MqClient.java	0.3.0	2013/06/30
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package org.fusesource.mqtt.client;

import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.hawtdispatch.Task;
import org.fusesource.mqtt.codec.MQTTFrame;

import ssac.aadl.runtime.mqtt.MqDeliveryToken;
import ssac.aadl.runtime.mqtt.MqPayload;
import ssac.aadl.runtime.mqtt.MqttArrivedMessage;
import ssac.aadl.runtime.mqtt.MqttException;
import ssac.aadl.runtime.mqtt.MqttTimedOutException;
import ssac.aadl.runtime.mqtt.MqttUtil;

/**
 * MQTT-client custom implementations.
 * <p>このクライアントオブジェクトは、ライブラリの実装上、1 回の接続のみ可能なオブジェクトであり、
 * 切断後は再利用できない。
 * 
 * @version 0.3.1	2013/07/05
 * @since 0.3.0
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
public abstract class MqClient
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/**
	 * MQTT クライアントのトレースメッセージ表示フラグのプロパティ・キー
	 */
	static public final String MQTT_CLIENT_TRACE_PROPERTY	= "MqttClient.trace";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	// connection
	final protected MqttCallbackConnection	_mqttClient;
	final protected int	_connTimeout;

	// state
	protected Object	_stateMutex	= new Object();
	
	protected volatile Throwable	_connLostCause = null;
	protected volatile boolean	_connLost = false;
	protected volatile boolean	_connected = false;
	protected volatile boolean	_connecting = false;
	protected volatile boolean	_disconnecting = false;
	
	protected volatile boolean	_onTrace	= false;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MqClient(MqttConnectOptions options)
	{
		_mqttClient = options.callbackConnection(new DefaultTracer());
		_mqttClient.listener(new MqClientListener());
		_connTimeout = options.getConnectionTimeout();
		updateTraceFlagByProperty();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このクライアントがサーバーと接続されている場合に <tt>true</tt> を返す。
	 * @return	接続中なら <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isConnected() {
		synchronized (_stateMutex) {
			return (_connected && !_disconnecting);
		}
	}

	/**
	 * このクライアントがサーバーとの接続プロセス中なら <tt>true</tt> を返す。
	 */
	public boolean isConnecting() {
		synchronized (_stateMutex) {
			return _connecting;
		}
	}

	/**
	 * このクライアントがサーバーとの切断プロセス中なら <tt>true</tt> を返す。
	 */
	public boolean isDisconnecting() {
		synchronized (_stateMutex) {
			return _disconnecting;
		}
	}

	/**
	 * このクライアントがすでに切断完了している場合に <tt>true</tt> を返す。
	 * 未接続や、接続処理中の場合などは <tt>false</tt> を返す。
	 */
	public boolean isDisconnected() {
		synchronized (_stateMutex) {
			return (!_connected && _mqttClient.getDisconnectedFlag());
		}
	}

	/**
	 * このクライアントが何らかの理由により切断された場合に <tt>true</tt> を返す。
	 * {@link #disconnect()} によって正常に切断されている場合、このメソッドは <tt>false</tt> を返す。
	 * @return	何らかの理由により切断された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isConnectionLost() {
		synchronized (_stateMutex) {
			return _connLost;
		}
	}

	/**
	 * このクライアントが何らかの理由に切断されたときの、要因を示す例外オブジェクトを返す。
	 * {@link #isConnected()} が <tt>true</tt> を返す場合、もしくは
	 * {@link #disconnect()} によって正常に切断されている場合、このメソッドは <tt>null</tt> を返す。
	 * @return	何らかの理由によりセッションが切断されたときの要因である例外オブジェクトを返す。
	 * 			接続中もしくは正常に切断されている場合は <tt>null</tt> を返す。
	 */
	public Throwable getConnectionLostCause() {
		synchronized (_stateMutex) {
			return _connLostCause;
		}
	}

	/**
	 * トレースメッセージの出力が有効な場合に <tt>true</tt> を返す。
	 */
	public boolean isTraceOn() {
		return _onTrace;
	}

	/**
	 * トレースメッセージ出力の on/off を設定する。
	 * @param beTraceOn	出力を有効とする場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public void setTranceOn(boolean beTraceOn) {
		_onTrace = beTraceOn;
	}
	
	public void updateTraceFlagByProperty() {
		String traceflag = System.getProperty(MQTT_CLIENT_TRACE_PROPERTY, null);
		if (traceflag != null && traceflag.length() > 0 && Boolean.valueOf(traceflag).booleanValue()) {
			_onTrace = true;
		} else {
			_onTrace = false;
		}
	}

	/**
	 * 現在の接続パラメータで、サーバーへ接続する。
	 * @throws MqttException	正常に接続できなかった場合、もしくはすでに接続されている場合
	 */
	public void connect() throws MqttException
	{
		MqConnectCallback connCB = null;
		
		// check state
		synchronized (_stateMutex) {
			// check connected
			if (_connected) {
				throw new MqttException("Client already connected.");
			}
			if (_connecting) {
				throw new MqttException("Client is currently connecting.");
			}
			if (_disconnecting) {
				throw new MqttException("Client is currently disconnecting.");
			}
			if (_mqttClient.getDisconnectedFlag()) {
				throw new MqttException("Client already disconnected.");
			}
			
			// change state
			_connecting = true;
			
			// create callback
			final MqConnectCallback cb = new MqConnectCallback();
			connCB = cb;
			
			// connect
			try {
				_mqttClient.getDispatchQueue().execute(new Task() {
					@Override
					public void run() {
						_mqttClient.connect(cb);
					}
				});
			}
			catch (Throwable ex) {
				synchronized (_stateMutex) {
					_connecting = false;
					throw new MqttException(ex);
				}
			}
		}
		
		// wait for connected
		if (_connTimeout > 0) {
			connCB.waitForCompletion(_connTimeout * 1000L);
		} else {
			connCB.waitForCompletion();
		}
	}

	/**
	 * このセッションを切断する。
	 * すでに切断されている場合や切断中の場合、このメソッドは何もしない。
	 * ただし、接続処理中の場合は例外をスローする。
	 * @throws MqttException	正常に切断できなかった場合、もしくは接続処理中の場合
	 */
	public void disconnect() throws MqttException
	{
		MqDisconnectCallback disconnCB = null;
		synchronized (_stateMutex) {
			if (_connecting) {
				// 接続中ならエラー
				throw new MqttException("Client is currently connecting.");
			}
			if (_disconnecting) {
				// 切断中ならスキップ
				return;
			}
			if (!_connected) {
				// 未接続ならスキップ
				return;
				
			}
			
			// 切断準備
			_disconnecting = true;
			
			// 切断
			final MqDisconnectCallback cb = new MqDisconnectCallback(null);
			disconnCB = cb;
			try {
				_mqttClient.getDispatchQueue().execute(new Task() {
					@Override
					public void run() {
						_mqttClient.disconnect(cb);
					}
				});
			}
			catch (Throwable ex) {
				synchronized (_stateMutex) {
					_disconnecting = false;
				}
				throw new MqttException(ex);
			}
		}
		
		// 切断完了を待機
		disconnCB.waitForCompletion(30000);
	}
	
	/**
	 * 指定されたトピックに対してメッセージを投稿する。
	 * このメソッドは、投稿完了まで待たない。
	 * <p>投稿完了までの待機には、このメソッドが返す <code>MqDeliveryToken</code> オブジェクトの
	 * <code>waitForCompletion()</code> もしくは <code>waitForCompletion(long timeout)</code> を実行する。
	 * 投稿が完了したかどうかの判定には <code>MqDeliveryToken</code> オブジェクトの <code>isComplete()</code> が
	 * <tt>true</tt> を返せば、投稿完了となる。
	 * @param topic		トピック
	 * @param payload	投稿するメッセージのバイト配列
	 * @param qos		メッセージ伝達品質（0, 1, 2）
	 * @param retained	サーバーにメッセージを残す場合は <tt>true</tt>
	 * @return	<code>MqDeliveryToken</code> オブジェクト
	 * @throws IllegalArgumentException	<em>qos</em> の値が 0、1、2 のどれかではない場合
	 * @throws MqttException		サーバーに接続されていない場合、もしくは、publish に失敗した場合
	 */
	public MqDeliveryToken publish(String topic, byte[] payload, int qos, boolean retained) throws MqttException
	{
		Buffer bufPayload = new Buffer(payload==null ? new byte[0] : payload);
		
		// publish
		return _publish(topic, bufPayload, qos, retained);
	}
	
	/**
	 * 指定されたトピックに対してメッセージを投稿する。
	 * このメソッドは、投稿完了まで待たない。
	 * <p>投稿完了までの待機には、このメソッドが返す <code>MqDeliveryToken</code> オブジェクトの
	 * <code>waitForCompletion()</code> もしくは <code>waitForCompletion(long timeout)</code> を実行する。
	 * 投稿が完了したかどうかの判定には <code>MqDeliveryToken</code> オブジェクトの <code>isComplete()</code> が
	 * <tt>true</tt> を返せば、投稿完了となる。
	 * @param topic		トピック
	 * @param payload	投稿するメッセージのバイト配列
	 * @param offset	投稿するバイト配列の開始位置
	 * @param length	投稿するバイト配列のオフセット位置からの有効データ長
	 * @param qos		メッセージ伝達品質（0, 1, 2）
	 * @param retained	サーバーにメッセージを残す場合は <tt>true</tt>
	 * @return	<code>MqDeliveryToken</code> オブジェクト
	 * @throws IndexOutOfBoundsException	<em>offset</em> および <em>length</em> の値が適切ではない場合
	 * @throws IllegalArgumentException	<em>qos</em> の値が 0、1、2 のどれかではない場合
	 * @throws IllegalStateException	サーバーに接続されていない場合
	 * @throws MqttException		サーバーに接続されていない場合、もしくは、publish に失敗した場合
	 * @since 0.3.0
	 */
	public MqDeliveryToken publish(String topic, byte[] payload, int offset, int length, int qos, boolean retained) throws MqttException
	{
		if (payload == null) {
			payload = new byte[0];
		}
		MqPayload.validPayload(payload, offset, length);
		Buffer bufPayload = new Buffer(payload, offset, length);
		
		// publish
		return _publish(topic, bufPayload, qos, retained);
	}
	
	/**
	 * 指定されたすべてのトピックに対して、メッセージのサブスクライブ（購読）を開始する。
	 * トピックにはワイルドカードが指定できる。
	 * @param topicFilters	トピック（ワイルドカードの指定も可）の配列
	 * @param qos	メッセージ伝達品質（0, 1, 2）の配列
	 * @throws NullPointerException		引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>topicFilters</code> の要素数より <em>qos</em> の要素数が少ない場合、
	 * 									もしくは <em>qos</em> の要素の値に 0、1、2 以外の値が含まれる場合
	 * @throws MqttException		サーバーに接続されていない場合、もしくは、サブスクライブに失敗した場合
	 */
	public void subscribe(String[] topicFilters, int[] qos) throws MqttException
	{
		if (topicFilters.length > qos.length) {
			throw new IllegalArgumentException("qos array length less than topicFilters array length!");
		}
		for (int elem : qos) {
			if (elem < 0 || elem > 2) {
				throw new IllegalArgumentException("qos element is not 0, 1 or 2 : " + qos);
			}
		}
		
		// create Topic objects
		final Topic[] topics = new Topic[topicFilters.length];
		for (int i = 0; i < topicFilters.length; i++) {
			topics[i] = new Topic(topicFilters[i], MqttCallbackConnection.qosValueToType(qos[i]));
		}
		
		// create response callback
		final MqResponseCallback<byte[]> cb = new MqResponseCallback<byte[]>();
		
		// unsubscribe
		synchronized (_stateMutex) {
			// check connection
			ensureConnected(null);
			
			// subscribe
			try {
				_mqttClient.getDispatchQueue().execute(new Task() {
					@Override
					public void run() {
						_mqttClient.subscribe(topics, cb);
					}
				});
			}
			catch (Throwable ex) {
				String errmsg = "Failed to send SUBSCRIBE token.";
				throw new MqttException(errmsg, ex);
			}
		}
		
		// wait to response
		cb.waitForCompletion(_connTimeout*1000);
	}
	
	/**
	 * 指定されたすべてのトピックに対するメッセージのサブスクライブ（購読）を終了する。
	 * トピックにはワイルドカードが指定できる。
	 * @param topicFilters	トピック（ワイルドカード含む）の配列
	 * @throws NullPointerException		引数が <tt>null</tt> の場合
	 * @throws MqttException	サーバーに接続されていない場合、もしくはサブスクライブ終了に失敗した場合
	 */
	public void unsubscribe(String[] topicFilters) throws MqttException
	{
		if (topicFilters == null)
			throw new NullPointerException();
		
		// create UTF8 topic filters
		final UTF8Buffer[] topics = new UTF8Buffer[topicFilters.length];
		for (int i = 0; i < topicFilters.length; i++) {
			topics[i] = UTF8Buffer.utf8(topicFilters[i]);
		}
		
		// create response callback
		final MqResponseCallback<Void> cb = new MqResponseCallback<Void>();

		// unsubscribe
		synchronized (_stateMutex) {
			// check connection
			ensureConnected(null);
			
			// unsubscribe
			try {
				_mqttClient.getDispatchQueue().execute(new Task() {
					@Override
					public void run() {
						_mqttClient.unsubscribe(topics, cb);
					}
				});
			}
			catch (Throwable ex) {
				String errmsg = "Failed to send UNSUBSCRIBE token.";
				throw new MqttException(errmsg, ex);
			}
		}
		
		// wait to response
		cb.waitForCompletion(_connTimeout*1000);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected MqttCallbackConnection getClientConnection() {
		return _mqttClient;
	}
	
	/**
	 * 指定されたトピックに対してメッセージを投稿する。
	 * このメソッドは、投稿完了まで待たない。
	 * <p>投稿完了までの待機には、このメソッドが返す <code>MqDeliveryToken</code> オブジェクトの
	 * <code>waitForCompletion()</code> もしくは <code>waitForCompletion(long timeout)</code> を実行する。
	 * 投稿が完了したかどうかの判定には <code>MqDeliveryToken</code> オブジェクトの <code>isComplete()</code> が
	 * <tt>true</tt> を返せば、投稿完了となる。
	 * @param topic		トピック
	 * @param payload	投稿するメッセージのバイトデータオブジェクト
	 * @param qos		メッセージ伝達品質（0, 1, 2）
	 * @param retained	サーバーにメッセージを残す場合は <tt>true</tt>
	 * @return	<code>MqResponseCallback</code> オブジェクト
	 * @throws IllegalArgumentException	<em>qos</em> の値が 0、1、2 のどれかではない場合
	 * @throws IllegalStateException	サーバーに接続されていない場合
	 * @throws MqttException		publish に失敗した場合
	 * @since 0.3.0
	 */
	protected MqResponseCallback<Void> _publish(final String topic, final Buffer payload, final int qos, final boolean retained) throws MqttException
	{
		// check QoS
		if (qos < 0 || qos > 2) {
			throw new IllegalArgumentException("qos is not 0, 1 or 2 : " + qos);
		}
		final QoS qosType = MqttCallbackConnection.qosValueToType(qos);
		
		// check topic
		if (topic.indexOf('#') >= 0 || topic.indexOf('+') >= 0) {
			throw new IllegalArgumentException("Could not include wildcard for Publish topic.");
		}
		
		// create sync object
		final UTF8Buffer utfTopic = UTF8Buffer.utf8(topic);
		final MqResponseCallback<Void> token = new MqResponseCallback<Void>();
		
		// publish
		synchronized (_stateMutex) {
			// check connection
			ensureConnected(null);
			
			// publish
			try {
				_mqttClient.getDispatchQueue().execute(new Task() {
					@Override
					public void run() {
						_mqttClient.publish(utfTopic, payload, qosType, retained, token);
					}
				});
				return token;
			}
			catch (Throwable ex) {
				throw new MqttException(ex);
			}
		}
	}

	/**
	 * サーバーとの接続が確立しているかを検証する。
	 * @param errmsg	エラー時に出力するメッセージ
	 * @throws MqttException	サーバーとの接続が確立していない場合
	 */
	protected void ensureConnected(String errmsg) throws MqttException
	{
		if (!isConnected()) {
			Throwable ex = getConnectionLostCause();
			if (errmsg != null && errmsg.length() > 0) {
				throw new MqttException(errmsg, ex);
			} else {
				throw new MqttException("MQTT client not connected!", ex);
			}
		}
	}

	/**
	 * サーバーとの接続が確立したとき、このメソッドが呼び出される。
	 */
	abstract protected void onClientConnected();

	/**
	 * サーバーとの接続が切断完了したときに、このメソッドが呼び出される。
	 * このメソッドは、MQTTクライアントのスレッド内から呼び出される。
	 */
	abstract protected void onClientDisconnected();
	
	/**
	 * サーバーへの接続が失われた場合、このメソッドが呼び出される。
	 * このメソッドは、MQTTクライアントのスレッド内から呼び出される。
	 * @param cause	接続が失われた要因
	 */
	abstract protected void onClientConnectionLost(Throwable cause);

	/**
	 * メッセージがサーバーから到着したときに、このメソッドが呼び出される。
	 * このメソッドは、MQTT クライアントによって同期的に呼び出される。
	 * このメソッドが正常に完了するまで、承認はサーバーに送信されない。
	 * このメソッドの実装が例外をスローした場合、このクライアントはシャットダウンされる。
	 * <p>
	 * このメソッドでクライアントとの接続を切断してはならない。その場合、デッドロックが発生する。
	 * @param message	受信したメッセージデータ
	 * @throws Exception	何らかのエラーが発生した場合
	 */
	abstract protected void onClientMessageArrived(MqttArrivedMessage message) throws Exception;
	
	protected void onClientTraceDebug(String message, Object...args) {
		if (isTraceOn()) {
			MqttUtil.out.append("[Debug] ").printf(message, args).println();
		}
	}
	
	protected void onClientTraceSend(MQTTFrame frame) {
		if (isTraceOn()) {
			MqttUtil.out.printf("[Send] %s", String.valueOf(frame)).println();
		}
	}
	
	protected void onClientTraceReceive(MQTTFrame frame) {
		if (isTraceOn()) {
			MqttUtil.out.printf("[Receive] %s", String.valueOf(frame)).println();
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	protected class MqConnectCallback extends MqResponseCallback<Void>
	{
		public void waitForCompletion(long timeout) throws MqttException {
			if (isTraceOn()) {
				onClientTraceDebug("<Called> MqClient.MqConnectCallback#waitForCompletion(%d)", timeout);
			}
			try {
				await(timeout);
			}
			catch (InterruptedException ex) {
				if (_mqttClient.transport() != null) {
					// kill connection
					_mqttClient.getDispatchQueue().execute(new Task() {
						@Override
						public void run() {
							_mqttClient.kill(null);
						}
					});
				}
				throw new MqttException(ex);
			}
			
			// check result
			if (isSucceeded()) {
				// connection succeeded
				synchronized (_stateMutex) {
					_connecting = false;
					_connected = true;
				}
				onClientConnected();
			}
			else {
				// connection failed
				Throwable error = getError();
				//--- kill connection
				_mqttClient.getDispatchQueue().execute(new Task() {
					@Override
					public void run() {
						_mqttClient.kill(null);
					}
				});
				synchronized (_stateMutex) {
					_connecting = false;
					if (error != null)
						throw new MqttException(error);
					else
						throw new MqttTimedOutException("Connection timed out!");
				}
			}
		}

		@Override
		public void onFailure(Throwable value) {
			if (isTraceOn()) {
				onClientTraceDebug("<Called> MqClient.MqConnectCallback#onFailure(%s)", String.valueOf(value));
			}
			super.onFailure(value);
		}

		@Override
		public void onSuccess(Void value) {
			if (isTraceOn()) {
				onClientTraceDebug("<Called> MqClient.MqConnectCallback#onSuccess()");
			}
			super.onSuccess(value);
		}
	}
	
	protected class MqDisconnectCallback extends MqResponseCallback<Void>
	{
		public MqDisconnectCallback(Throwable reason) {
			_failedReason = reason;
		}
		
		@Override
		public void waitForCompletion(long timeout) throws MqttException {
			if (isTraceOn()) {
				onClientTraceDebug("<Called> MqClient.MqDisconnectCallback#waitForCompletion(%d)", timeout);
			}
			
			try {
				super.waitForCompletion(timeout);
			}
			catch (MqttTimedOutException ex) {
				throw new MqttTimedOutException("Disconnection timed out!");
			}
			finally {
				synchronized (_stateMutex) {
					_disconnecting = false;
				}
			}
		}

		protected void onCompleted(Throwable reason) {
			boolean wasConnected;
			synchronized (this) {
				if (_failedReason == null) {
					_failedReason = reason;
				} else {
					reason = _failedReason;
				}
				synchronized (_stateMutex) {
					wasConnected = _connected;
					_disconnecting = false;
					_connected = false;
					if (wasConnected && reason != null) {
						_connLost = true;
						_connLostCause = reason;
					}
				}
			}
			
			if (wasConnected) {
				// call handler
				if (reason != null) {
					// connection lost
					onClientConnectionLost(reason);
				} else {
					// connection succeeded
					onClientDisconnected();
				}
			}
		}

		@Override
		public void onFailure(Throwable value) {
			if (isTraceOn()) {
				onClientTraceDebug("<Called> MqClient.MqDisconnectCallback#onFailure(%s)", String.valueOf(value));
			}
			super.onFailure(value);
			onCompleted(value);
		}

		@Override
		public void onSuccess(Void value) {
			if (isTraceOn()) {
				onClientTraceDebug("<Called> MqClient.MqDisconnectCallback#onSuccess()");
			}
			super.onSuccess(value);
			onCompleted(null);
		}
	}
	
	protected class MqClientListener implements MqttCallbackConnection.MqttMessageListener
	{
		private long	_recvCount = 0L;
		private QoS		_qos;
		private boolean	_retain;
		private boolean	_duplicate;
		
		public long getReceiveCount() {
			return _recvCount;
		}
		
		//
		// org.fusesource.mqtt.client.Listener interfaces
		//

		@Override
		public void onConnected() {
			if (isTraceOn()) {
				onClientTraceDebug("<Called> MqClient.MqClientListener#onConnected()");
			}
		}

		@Override
		public void onDisconnected() {
			if (isTraceOn()) {
				onClientTraceDebug("<Called> MqClient.MqClientListener#onDisconnected()");
			}
		}

		@Override
		public void onPublish(UTF8Buffer topic, Buffer body, Runnable ack)
		{
			_mqttClient.getDispatchQueue().assertExecuting();
			
			// 切断中なら、処理しない
			synchronized (_stateMutex) {
				if (_disconnecting) {
					return;
				}
			}

			// 受信メッセージオブジェクトを生成
			MqttArrivedMessage msg = new MqttArrivedMessage(topic.toString(),
												body.getData(), body.getOffset(), body.getLength(),
												getQos(), isRetained(), isDuplicated());

			// call onMessageArrived
			try {
				onClientMessageArrived(msg);
			}
			catch (RuntimeException ex) {
				throw ex;
			}
			catch (Exception ex) {
				throw new WrappedArrivedMessageException(ex);
			}
			
			// send response
			ack.run();
		}

		@Override
		public void onFailure(Throwable value) {
			_mqttClient.getDispatchQueue().assertExecuting();
			// onPublish で例外が発生した場合、待機中の全コールバックの onFailure が呼び出された後、
			// このメソッドが呼び出される。
			if (value instanceof WrappedArrivedMessageException) {
				value = ((WrappedArrivedMessageException)value).getCause();
			}
			if (isTraceOn()) {
				onClientTraceDebug("<Called> MqClient.ClientListener#onFailure(%s)", String.valueOf(value));
			}
			synchronized (_stateMutex) {
				if (_connected && !_disconnecting && !_mqttClient.getDisconnectedFlag()) {
					// 切断する
					_disconnecting = true;
					final MqDisconnectCallback disconnCB = new MqDisconnectCallback(value);
					_mqttClient.getDispatchQueue().execute(new Task() {
						@Override
						public void run() {
							_mqttClient.disconnect(disconnCB);
						}
					});
				}
			}
		}
		
		//
		// org.fusesource.mqtt.client.MessageListener interfaces
		//

		@Override
		public int getQos() {
			if (_qos == QoS.EXACTLY_ONCE)
				return 2;
			else if (_qos == QoS.AT_LEAST_ONCE)
				return 1;
			else
				return 0;
		}

		@Override
		public QoS getQosType() {
			return _qos;
		}

		@Override
		public void setQosType(QoS qos) {
			_qos = qos;
		}

		@Override
		public boolean isRetained() {
			return _retain;
		}

		@Override
		public void setRetained(boolean retained) {
			_retain = retained;
		}

		@Override
		public boolean isDuplicated() {
			return _duplicate;
		}

		@Override
		public void setDuplicated(boolean duplicated) {
			_duplicate = duplicated;
		}
	}
	
	protected class DefaultTracer extends Tracer
	{
		@Override
		public void debug(String message, Object... args) {
			onClientTraceDebug(message, args);
		}

		@Override
		public void onSend(MQTTFrame frame) {
			onClientTraceSend(frame);
		}

		@Override
		public void onReceive(MQTTFrame frame) {
			onClientTraceReceive(frame);
		}
	}
	
	static protected class WrappedArrivedMessageException extends RuntimeException
	{
		private static final long serialVersionUID = 4540365315672194214L;

		public WrappedArrivedMessageException(Throwable cause) {
			super(cause);
		}
	}
}
