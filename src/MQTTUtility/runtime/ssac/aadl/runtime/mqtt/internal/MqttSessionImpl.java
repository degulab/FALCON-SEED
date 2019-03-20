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
 * @(#)MqttSession.java	0.3.0	2013/06/30
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MqttSession.java	0.2.0	2013/05/10
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.mqtt.internal;

import java.util.Arrays;

import org.fusesource.mqtt.client.MqClient;
import org.fusesource.mqtt.client.MqttConnectOptions;

import ssac.aadl.runtime.mqtt.MqDeliveryToken;
import ssac.aadl.runtime.mqtt.MqttArrivedMessage;
import ssac.aadl.runtime.mqtt.MqttConnectionParams;
import ssac.aadl.runtime.mqtt.MqttException;
import ssac.aadl.runtime.mqtt.MqttRuntimeException;
import ssac.aadl.runtime.mqtt.MqttSession;
import ssac.aadl.runtime.mqtt.MqttUtil;
import ssac.aadl.runtime.mqtt.event.MqttEventHandler;

/**
 * MQTT のセッションを保持するクラスの実装。
 * 
 * @version 0.3.0	2013/06/30
 * @since 0.2.0
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
public class MqttSessionImpl implements MqttSession
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	protected final MqttConnectionParams	_mqttParams;
	
	// connection
	private volatile MqttClientEntity	_mqttClient;

	// state
	protected boolean		_onTrace	= false;
	
	private volatile MqttEventHandler	_handler = null;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定された接続パラメータで、新しいインスタンスを生成する。
	 * @param params	接続パラメータ
	 * @throws NullPointerException	<em>params</em> が <tt>null</tt> の場合
	 * @throws MqttRuntimeException	セッションオブジェクトが生成できない場合
	 */
	public MqttSessionImpl(MqttConnectionParams params) {
		if (params == null)
			throw new NullPointerException("MqttConnectionParams is null.");
		
		_mqttParams = params;
		try {
			_mqttClient = new MqttClientEntity(params.getOptions());
		}
		catch (Throwable ex) {
			throw new MqttRuntimeException("Failed to create MQTT client object.", ex, params);
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * トレースメッセージの出力が有効な場合に <tt>true</tt> を返す。
	 */
	public boolean isTraceOn() {
		return _mqttClient.isTraceOn();
	}

	/**
	 * トレースメッセージ出力の on/off を設定する。
	 * @param beTraceOn	出力を有効とする場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public void setTranceOn(boolean beTraceOn) {
		_mqttClient.setTranceOn(beTraceOn);
	}
	
	public void updateTraceFlagByProperty() {
		_mqttClient.updateTraceFlagByProperty();
	}

	//------------------------------------------------------------
	// ssac.aadl.runtime.mqtt.MqttSession interfaces
	//------------------------------------------------------------
	
	/**
	 * 接続先サーバーの URI を取得する。
	 * @return	サーバー URI
	 */
	public String getServerURI() {
		return _mqttParams.getServerURI();
	}

	/**
	 * 現在設定されているクライアントIDを返す。
	 * @return	クライアントID
	 */
	public String getClientID() {
		return _mqttParams.getClientID();
	}

	/**
	 * 再接続時にセッションの状態をクリアするかどうかの設定を取得する。
	 * @return	クリアする場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isCleanSession() {
		return _mqttParams.isCleanSession();
	}

	/**
	 * 接続時のタイムアウト時間を返す。デフォルトは 30 秒。
	 * このタイムアウト時間は、サーバーへの接続、サブスクライブ、アンサブスクライブの
	 * リクエスト完了までのタイムアウトを指定する。
	 * @return	タイムアウト時間[秒]
	 */
	public int getConnectionTimeout() {
		return _mqttParams.getConnectionTimeout();
	}

	/**
	 * キープアライブ間隔を返す。デフォルトは 60 秒。
	 * この時間間隔は、送信もしくは受信したメッセージ間の最大時間間隔を定義する。
	 * @return	キープアライブ間隔［秒］
	 */
	public int getKeepAliveInterval() {
		return _mqttParams.getKeepAliveInterval();
	}

	/**
	 * このセッションがサーバーと接続されている場合に <tt>true</tt> を返す。
	 * @return	接続中なら <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isConnected() {
		return _mqttClient.isConnected();
	}

	/**
	 * このセッションが何らかの理由により切断された場合に <tt>true</tt> を返す。
	 * {@link #disconnect()} によって正常に切断されている場合、このメソッドは <tt>false</tt> を返す。
	 * @return	何らかの理由により切断された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isConnectionLost() {
		return _mqttClient.isConnectionLost();
	}

	/**
	 * このセッションが何らかの理由に切断されたときの、要因を示す例外オブジェクトを返す。
	 * {@link #isConnected()} が <tt>true</tt> を返す場合、もしくは
	 * {@link #disconnect()} によって正常に切断されている場合、このメソッドは <tt>null</tt> を返す。
	 * @return	何らかの理由によりセッションが切断されたときの要因である例外オブジェクトを返す。
	 * 			接続中もしくは正常に切断されている場合は <tt>null</tt> を返す。
	 */
	public Throwable getConnectionLostCause() {
		return _mqttClient.getConnectionLostCause();
	}

	/**
	 * 現在の接続パラメータで、サーバーへ接続する。
	 * @throws MqttRuntimeException	正常に接続できなかった場合、もしくはすでに接続されている場合
	 */
	public void connect() {
		// check disconnected
		if (_mqttClient.isDisconnected()) {
			try {
				MqttClientEntity newClient = new MqttClientEntity(_mqttParams.getOptions());
				_mqttClient = newClient;
			}
			catch (Throwable ex) {
				throw new MqttRuntimeException("Failed to create MQTT client object.", ex, _mqttParams);
			}
		}
		
		// connect
		try {
			_mqttClient.connect();
		}
		catch (MqttException ex) {
			throw new MqttRuntimeException(ex, _mqttParams);
		}
		catch (Throwable ex) {
			throw new MqttRuntimeException("Failed to connect", ex, _mqttParams);
		}
	}

	/**
	 * このセッションを切断する。
	 * すでに切断されているセッションの場合、このメソッドは何もしない。
	 * @throws MqttRuntimeException	正常に切断できなかった場合
	 */
	public void disconnect() {
		if (_mqttClient.isConnected()) {
			try {
				_mqttClient.disconnect();
			}
			catch (MqttException ex) {
				throw new MqttRuntimeException(ex, _mqttParams);
			}
			catch (Throwable ex) {
				throw new MqttRuntimeException("Failed to disconnect.", ex, _mqttParams);
			}
		}
	}
	
	/**
	 * 指定されたトピックに対してメッセージを投稿する。
	 * このメソッドは、投稿完了まで待たない。
	 * <p>投稿完了までの待機には、このメソッドが返す <code>MqDeliveryToken</code> オブジェクトの
	 * <code>waitForCompletion()</code> もしくは <code>waitForCompletion(long timeout)</code> を実行する。
	 * 投稿が完了したかどうかの判定には <code>MqDeliveryToken</code> オブジェクトの <code>isComplete()</code> が
	 * <tt>true</tt> を返せば、投稿完了となる。
	 * @param topic		トピック
	 * @param message	投稿するメッセージ
	 * @param qos		メッセージ伝達品質（0, 1, 2）
	 * @param retained	サーバーにメッセージを残す場合は <tt>true</tt>
	 * @return	<code>MqDeliveryToken</code> オブジェクト
	 * @throws IllegalArgumentException	<em>qos</em> の値が 0、1、2 のどれかではない場合
	 * @throws MqttRuntimeException		サーバーに接続されていない場合、もしくは、publish に失敗した場合
	 */
	public MqDeliveryToken asyncPublish(String topic, byte[] payload, int qos, boolean retained) {
		try {
			return _mqttClient.publish(topic, payload, qos, retained);
		}
		catch (RuntimeException ex) {
			throw ex;
		}
		catch (Throwable ex) {
			String errmsg = String.format("Failed to publish to [%s], cause %s", String.valueOf(topic), ex.toString());
			throw new MqttRuntimeException(errmsg, ex, _mqttParams);
		}
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
	 * @throws MqttRuntimeException		publish に失敗した場合
	 * @since 0.3.0
	 */
	public MqDeliveryToken asyncPublish(String topic, byte[] payload, int offset, int length, int qos, boolean retained) {
		try {
			return _mqttClient.publish(topic, payload, offset, length, qos, retained);
		}
		catch (RuntimeException ex) {
			throw ex;
		}
		catch (Throwable ex) {
			String errmsg = String.format("Failed to publish to [%s], cause %s", String.valueOf(topic), ex.toString());
			throw new MqttRuntimeException(errmsg, ex, _mqttParams);
		}
	}

	/**
	 * 指定されたトピックに対してメッセージを投稿する。
	 * このメソッドは、投稿が完了するまで処理をブロックする。
	 * @param topic		トピック
	 * @param payload	投稿するメッセージのバイト配列
	 * @param qos		メッセージ伝達品質（0, 1, 2）
	 * @param retained	サーバーにメッセージを残す場合は <tt>true</tt>
	 * @throws IllegalArgumentException	<em>qos</em> の値が 0、1、2 のどれかではない場合
	 * @throws IllegalStateException	サーバーに接続されていない場合
	 * @throws MqttRuntimeException		publish に失敗した場合
	 */
	public void publishAndWait(String topic, byte[] message, int qos, boolean retained) {
		// publish
		MqDeliveryToken mqtoken = asyncPublish(topic, message, qos, retained);
		
		// wait for completion
		try {
			mqtoken.waitForCompletion();
		}
		catch (MqttException ex) {
			String errmsg = String.format("Failed to publish to [%s], cause %s", String.valueOf(topic), ex.toString());
			throw new MqttRuntimeException(errmsg, ex, _mqttParams);
		}
	}
	
	/**
	 * 指定されたトピックに対してメッセージを投稿する。
	 * このメソッドは、投稿が完了するまで処理をブロックする。
	 * @param topic		トピック
	 * @param payload	投稿するメッセージのバイト配列
	 * @param offset	投稿するバイト配列の開始位置
	 * @param length	投稿するバイト配列のオフセット位置からの有効データ長
	 * @param qos		メッセージ伝達品質（0, 1, 2）
	 * @param retained	サーバーにメッセージを残す場合は <tt>true</tt>
	 * @throws IndexOutOfBoundsException	<em>offset</em> および <em>length</em> の値が適切ではない場合
	 * @throws IllegalArgumentException	<em>qos</em> の値が 0、1、2 のどれかではない場合
	 * @throws IllegalStateException	サーバーに接続されていない場合
	 * @throws MqttRuntimeException		publish に失敗した場合
	 * @since 0.3.0
	 */
	public void publishAndWait(String topic, byte[] payload, int offset, int length, int qos, boolean retained) {
		// publish
		MqDeliveryToken mqtoken = asyncPublish(topic, payload, offset, length, qos, retained);
		
		// wait for completion
		try {
			mqtoken.waitForCompletion();
		}
		catch (MqttException ex) {
			String errmsg = String.format("Failed to publish to [%s], cause %s", String.valueOf(topic), ex.toString());
			throw new MqttRuntimeException(errmsg, ex, _mqttParams);
		}
	}
	
	/**
	 * 指定されたトピックに対して、<code>qos=1</code> でメッセージのサブスクライブ（購読）を開始する。
	 * トピックにはワイルドカードが指定できる。
	 * @param topic	トピック（ワイルドカードの指定も可）
	 * @throws MqttRuntimeException		サーバーに接続されていない場合、もしくは、サブスクライブに失敗した場合
	 */
	public void subscribe(String topic) {
		subscribe(topic, 1);
	}

	/**
	 * 指定されたトピックに対して、メッセージのサブスクライブ（購読）を開始する。
	 * トピックにはワイルドカードが指定できる。
	 * @param topic	トピック（ワイルドカードの指定も可）
	 * @param qos	メッセージ伝達品質（0, 1, 2）
	 * @throws IllegalArgumentException	<em>qos</em> の値が 0、1、2 のどれかではない場合
	 * @throws MqttRuntimeException		サーバーに接続されていない場合、もしくは、サブスクライブに失敗した場合
	 */
	public void subscribe(String topic, int qos) {
		try {
			String[] topicFilters = new String[]{topic};
			_mqttClient.subscribe(topicFilters, new int[]{qos});
			//--- verbose
			MqttUtil.out.printf("MQTT client subscribed [%s] : clientID=%s", String.valueOf(topic), _mqttParams.getClientID()).println();
			onSubscribeCompleted(topicFilters);
		}
		catch (RuntimeException ex) {
			throw ex;
		}
		catch (Throwable ex) {
			String errmsg = String.format("Failed to subscribe for [%s], cause %s", String.valueOf(topic), ex.toString());
			throw new MqttRuntimeException(errmsg, ex, _mqttParams);
		}
	}
	
	/**
	 * 指定されたすべてのトピックに対して、<code>qos=1</code> でメッセージのサブスクライブ（購読）を開始する。
	 * トピックにはワイルドカードが指定できる。
	 * @param topicFilters	トピック（ワイルドカードの指定も可）の配列
	 * @param qos	メッセージ伝達品質（0, 1, 2）の配列
	 * @throws NullPointerException		引数が <tt>null</tt> の場合
	 * @throws MqttRuntimeException		サーバーに接続されていない場合、もしくは、サブスクライブに失敗した場合
	 */
	public void subscribe(String[] topicFilters) {
		int[] qos = new int[topicFilters.length];
		Arrays.fill(qos, 1);
		subscribe(topicFilters, qos);
	}
	
	/**
	 * 指定されたすべてのトピックに対して、メッセージのサブスクライブ（購読）を開始する。
	 * トピックにはワイルドカードが指定できる。
	 * @param topicFilters	トピック（ワイルドカードの指定も可）の配列
	 * @param qos	メッセージ伝達品質（0, 1, 2）の配列
	 * @throws NullPointerException		引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>topicFilters</code> の要素数より <em>qos</em> の要素数が少ない場合、
	 * 									もしくは <em>qos</em> の要素の値に 0、1、2 以外の値が含まれる場合
	 * @throws MqttRuntimeException		サーバーに接続されていない場合、もしくは、サブスクライブに失敗した場合
	 */
	public void subscribe(String[] topicFilters, int[] qos) {
		try {
			_mqttClient.subscribe(topicFilters, qos);
			//--- verbose
			if (MqttUtil.out != null && MqttUtil.out != MqttUtil.NullPrintStream) {
				MqttUtil.out.printf("MQTT client subscribed %d topic filters : clientID=%s", topicFilters.length, _mqttParams.getClientID()).println();
			}
			onSubscribeCompleted(topicFilters);
		}
		catch (RuntimeException ex) {
			throw ex;
		}
		catch (Throwable ex) {
			String errmsg = String.format("Failed to subscribe for %d topic filters, cause %s",
					topicFilters.length, ex.toString());
			throw new MqttRuntimeException(errmsg, ex, _mqttParams);
		}
	}

	/**
	 * 指定されたトピックに対するメッセージのサブスクライブ（購読）を終了する。
	 * トピックにはワイルドカードが指定できる。
	 * @param topic	トピック（ワイルドカード含む）
	 * @throws MqttRuntimeException	サーバーに接続されていない場合、もしくはサブスクライブ終了に失敗した場合
	 */
	public void unsubscribe(String topic) {
		// unsubscribe
		try {
			String[] topicFilters = new String[]{topic};
			_mqttClient.unsubscribe(topicFilters);
			// verbose
			MqttUtil.out.printf("MQTT client unsubscribed [%s] : clientID=%s", String.valueOf(topic), _mqttParams.getClientID()).println();
			onUnsubscribeCompleted(topicFilters);
		}
		catch (RuntimeException ex) {
			throw ex;
		}
		catch (Throwable ex) {
			String errmsg = String.format("Failed to unsubscribe for [%s], cause %s", String.valueOf(topic), ex.toString());
			throw new MqttRuntimeException(errmsg, ex, _mqttParams);
		}
	}
	
	/**
	 * 指定されたすべてのトピックに対するメッセージのサブスクライブ（購読）を終了する。
	 * トピックにはワイルドカードが指定できる。
	 * @param topic	トピック（ワイルドカード含む）の配列
	 * @throws NullPointerException		引数が <tt>null</tt> の場合
	 * @throws MqttRuntimeException	サーバーに接続されていない場合、もしくはサブスクライブ終了に失敗した場合
	 */
	public void unsubscribe(String[] topicFilters) {
		// unsubscribe
		try {
			_mqttClient.unsubscribe(topicFilters);
			// verbose
			if (MqttUtil.out != null && MqttUtil.out != MqttUtil.NullPrintStream) {
				MqttUtil.out.printf("MQTT client unsubscribed %d topic filters : clientID=%s", topicFilters.length, _mqttParams.getClientID()).println();
			}
			onUnsubscribeCompleted(topicFilters);
		}
		catch (RuntimeException ex) {
			throw ex;
		}
		catch (Throwable ex) {
			String errmsg = String.format("Failed to unsubscribe for %d topic filters, cause %s",
					topicFilters.length, ex.toString());
			throw new MqttRuntimeException(errmsg, ex, _mqttParams);
		}
	}

	/**
	 * 現在設定されている MQTT イベントハンドラを取得する。
	 * 設定されていない場合は <tt>null</tt> を返す。
	 * @return	設定されている <code>MqttEventHandler</code> オブジェクト、設定されていない場合は <tt>null</tt>
	 * @see ssac.aadl.runtime.mqtt.event.MqttEventHandler
	 */
	public MqttEventHandler getEventHandler() {
		return _handler;
	}

	/**
	 * 新しい MQTT イベントハンドラを設定する。
	 * イベントハンドラを無効にする場合は <tt>null</tt> を指定する。
	 * @param newHandler	<code>MqttEventHandler</code> オブジェクト、設定しない場合は <tt>null</tt>
	 * @see ssac.aadl.runtime.mqtt.event.MqttEventHandler
	 */
	public void setEventHandler(MqttEventHandler newHandler) {
		_handler = newHandler;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void ensureConnected(String errmsg) {
		if (!isConnected()) {
			Throwable ex = getConnectionLostCause();
			if (ex == null) {
				ex = new MqttException("MQTT client not connected!");
			}
			if (errmsg != null && errmsg.length() > 0) {
				throw new MqttRuntimeException(errmsg, ex, _mqttParams);
			} else {
				throw new MqttRuntimeException("MQTT client not connected!", ex, _mqttParams);
			}
		}
	}
	
	/**
	 * サーバーへの接続が失われた場合、このメソッドが呼び出される。
	 * このメソッドは、MQTTクライアントのスレッド内から呼び出される。
	 * @param cause	接続が失われた要因
	 */
	protected void onConnectionLost(Throwable cause) {
		// verbose
		MqttUtil.err.printf("MQTT client connection lost : serverURI=%s, clientID=%s", _mqttParams.getServerURI(), _mqttParams.getClientID()).println();
		MqttUtil.err.printf("(cause) %s", String.valueOf(cause)).println();
		//--- call event handler
		MqttEventHandler handler = _handler;
		if (handler != null) {
			handler.connectionLost(this, cause);
		}
	}

	/**
	 * サーバーとの接続が確立したとき、このメソッドが呼び出される。
	 */
	protected void onConnected() {
		// verbose
		MqttUtil.out.printf("MQTT client connected : serverURI=%s, clientID=%s", _mqttParams.getServerURI(), _mqttParams.getClientID()).println();
	}

	/**
	 * サーバーとの接続が切断されたときに、このメソッドが呼び出される。
	 */
	protected void onDisconnected() {
		// verbose
		MqttUtil.out.printf("MQTT client disconnected : serverURI=%s, clientID=%s", _mqttParams.getServerURI(), _mqttParams.getClientID()).println();
		//--- call event handler
		MqttEventHandler handler = _handler;
		if (handler != null) {
			handler.disconnected(this);
		}
	}

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
	protected void onMessageArrived(MqttArrivedMessage message) throws Exception
	{
		MqttEventHandler handler = _handler;
		if (handler != null) {
			handler.messageArrived(this, message);
		}
	}
	
	protected void onSubscribeCompleted(String[] topicFilters) {
		// place holder
	}
	
	protected void onUnsubscribeCompleted(String[] topicFilters) {
		// place holder
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	protected class MqttClientEntity extends MqClient
	{
		public MqttClientEntity(MqttConnectOptions options) {
			super(options);
		}

		@Override
		protected void onClientConnectionLost(Throwable cause) {
			if (isTraceOn()) {
				onClientTraceDebug("<Called> MqttSessionImpl.MqttClientEntity#onClientConnectionLost(%s)", String.valueOf(cause));
			}
			onConnectionLost(cause);
		}

		@Override
		protected void onClientConnected() {
			if (isTraceOn()) {
				onClientTraceDebug("<Called> MqttSessionImpl.MqttClientEntity#onClientConnected()");
			}
			onConnected();
		}

		@Override
		protected void onClientDisconnected() {
			if (isTraceOn()) {
				onClientTraceDebug("<Called> MqttSessionImpl.MqttClientEntity#onClientDisconnected()");
			}
			onDisconnected();
		}

		@Override
		protected void onClientMessageArrived(MqttArrivedMessage message) throws Exception {
			onMessageArrived(message);
		}
	}
}
