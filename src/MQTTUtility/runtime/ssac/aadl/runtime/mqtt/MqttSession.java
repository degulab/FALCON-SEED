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
 * @(#)MqttSession.java	0.3.0	2013/06/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MqttSession.java	0.2.0	2013/05/15
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MqttSession.java	0.1.0	2013/03/07
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.mqtt;


/**
 * MQTT のセッションを管理するインタフェース。
 * 
 * @version 0.3.0	2013/06/27
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
public interface MqttSession
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * 接続先サーバーの URI を取得する。
	 * @return	サーバー URI
	 */
	public String getServerURI();

	/**
	 * 現在設定されているクライアントIDを返す。
	 * @return	クライアントID
	 */
	public String getClientID();

	/**
	 * 再接続時にセッションの状態をクリアするかどうかの設定を取得する。
	 * @return	クリアする場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isCleanSession();

	/**
	 * 接続時のタイムアウト時間を返す。デフォルトは 30 秒。
	 * このタイムアウト時間は、サーバーへの接続、サブスクライブ、アンサブスクライブの
	 * リクエスト完了までのタイムアウトを指定する。
	 * @return	タイムアウト時間[秒]
	 */
	public int getConnectionTimeout();

	/**
	 * キープアライブ間隔を返す。デフォルトは 60 秒。
	 * この時間間隔は、送信もしくは受信したメッセージ間の最大時間間隔を定義する。
	 * @return	キープアライブ間隔［秒］
	 */
	public int getKeepAliveInterval();

	/**
	 * このセッションがサーバーと接続されている場合に <tt>true</tt> を返す。
	 * @return	接続中なら <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isConnected();

	/**
	 * このセッションが何らかの理由により切断された場合に <tt>true</tt> を返す。
	 * {@link #isConnected()} が <tt>true</tt> を返す場合、もしくは
	 * {@link #disconnect()} によって正常に切断されている場合、このメソッドは <tt>false</tt> を返す。
	 * @return	何らかの理由により切断された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isConnectionLost();

	/**
	 * このセッションが何らかの理由に切断されたときの、要因を示す例外オブジェクトを返す。
	 * {@link #isConnected()} が <tt>true</tt> を返す場合、もしくは
	 * {@link #disconnect()} によって正常に切断されている場合、このメソッドは <tt>null</tt> を返す。
	 * @return	何らかの理由によりセッションが切断されたときの要因である例外オブジェクトを返す。
	 * 			接続中もしくは正常に切断されている場合は <tt>null</tt> を返す。
	 */
	public Throwable getConnectionLostCause();

//	/**
//	 * 現在の接続パラメータで、サーバーへ接続する。
//	 * @throws MqttRuntimeException	正常に接続できなかった場合、もしくはすでに接続されている場合
//	 */
//	public void connect();

	/**
	 * このセッションを切断する。
	 * すでに切断されているセッションの場合、このメソッドは何もしない。
	 * @throws MqttRuntimeException	正常に切断できなかった場合
	 */
	public void disconnect();
	
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
	 * @throws IllegalStateException	サーバーに接続されていない場合
	 * @throws MqttRuntimeException		publish に失敗した場合
	 */
	public MqDeliveryToken asyncPublish(String topic, byte[] payload, int qos, boolean retained);
	
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
	public MqDeliveryToken asyncPublish(String topic, byte[] payload, int offset, int length, int qos, boolean retained);
	
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
	public void publishAndWait(String topic, byte[] payload, int qos, boolean retained);
	
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
	public void publishAndWait(String topic, byte[] payload, int offset, int length, int qos, boolean retained);
	
	/**
	 * 指定されたトピックに対して、<code>qos=1</code> でメッセージの購読を要求する。
	 * トピックにはワイルドカードが指定できる。
	 * @param topic	トピック（ワイルドカード含む）
	 * @throws IllegalStateException	サーバーに接続されていない場合
	 * @throws MqttRuntimeException		subscribe に失敗した場合
	 */
	public void subscribe(String topic);

	/**
	 * 指定されたトピックに対して、メッセージの購読を要求する。
	 * トピックにはワイルドカードが指定できる。
	 * @param topic	トピック（ワイルドカード含む）
	 * @param qos	購読時のメッセージ伝達品質（0, 1, 2）
	 * @throws IllegalArgumentException	<em>qos</em> の値が 0、1、2 のどれかではない場合
	 * @throws IllegalStateException	サーバーに接続されていない場合
	 * @throws MqttRuntimeException		subscribe に失敗した場合
	 */
	public void subscribe(String topic, int qos);
	
	/**
	 * 指定されたトピックに対して、<code>qos=1</code> でメッセージの購読を要求する。
	 * トピックにはワイルドカードが指定できる。
	 * @param topicFilters	トピック（ワイルドカード含む）の配列
	 * @throws NullPointerException		引数が <tt>null</tt> の場合
	 * @throws IllegalStateException	サーバーに接続されていない場合
	 * @throws MqttRuntimeException		subscribe に失敗した場合
	 */
	public void subscribe(String[] topicFilters);
	
	/**
	 * 指定されたトピックに対して、メッセージの購読を要求する。
	 * トピックにはワイルドカードが指定できる。
	 * @param topicFilters	トピック（ワイルドカード含む）の配列
	 * @param qos	購読時のメッセージ伝達品質（0, 1, 2）の配列
	 * @throws NullPointerException		引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>topicFilters</code> の要素数より <em>qos</em> の要素数が少ない場合、
	 * 									もしくは <em>qos</em> の要素の値に 0、1、2 以外の値が含まれる場合
	 * @throws IllegalStateException	サーバーに接続されていない場合
	 * @throws MqttRuntimeException		subscribe に失敗した場合
	 */
	public void subscribe(String[] topicFilters, int[] qos);

	/**
	 * 指定されたトピックに対するメッセージの購読を解除する。
	 * トピックにはワイルドカードが指定できる。
	 * @param topic	トピック（ワイルドカード含む）
	 * @throws IllegalStateException	サーバーに接続されていない場合
	 * @throws MqttRuntimeException	unsubscribe に失敗した場合
	 */
	public void unsubscribe(String topic);
	
	/**
	 * 指定されたトピックに対するメッセージの購読を解除する。
	 * トピックにはワイルドカードが指定できる。
	 * @param topicFilters	トピック（ワイルドカード含む）の配列
	 * @throws NullPointerException		引数が <tt>null</tt> の場合
	 * @throws IllegalStateException	サーバーに接続されていない場合
	 * @throws MqttRuntimeException	unsubscribe に失敗した場合
	 */
	public void unsubscribe(String[] topicFilters);

//	/**
//	 * 現在設定されている MQTT イベントハンドラを取得する。
//	 * 設定されていない場合は <tt>null</tt> を返す。
//	 * @return	設定されている <code>MqttEventHandler</code> オブジェクト、設定されていない場合は <tt>null</tt>
//	 * @see ssac.aadl.runtime.mqtt.event.MqttEventHandler
//	 */
//	public MqttEventHandler getEventHandler();
//
//	/**
//	 * 新しい MQTT イベントハンドラを設定する。
//	 * イベントハンドラを無効にする場合は <tt>null</tt> を指定する。
//	 * @param newHandler	<code>MqttEventHandler</code> オブジェクト、設定しない場合は <tt>null</tt>
//	 * @see ssac.aadl.runtime.mqtt.event.MqttEventHandler
//	 */
//	public void setEventHandler(MqttEventHandler newHandler);
}
